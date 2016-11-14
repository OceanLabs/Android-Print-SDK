/*****************************************************
 *
 * AEditTextEnforcer.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.widget;


///// Import(s) /////

import android.widget.EditText;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent of text enforcers.
 *
 *****************************************************/
public class AEditTextEnforcer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG      = "AEditTextEnforcer";

  static public final int TEXT_COLOUR_ERROR = 0xffff0000;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected EditText   mEditText;
  protected ICallback  mCallback;

  protected int        mOKTextColour;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns just the digits from a character sequence.
   *
   *****************************************************/
  static protected String getDigits( CharSequence originalCharSequence )
    {
    if ( originalCharSequence == null ) return ( "" );


    StringBuilder stringBuilder = new StringBuilder( originalCharSequence.length() );

    for ( int charIndex = 0; charIndex < originalCharSequence.length(); charIndex ++ )
      {
      char c = originalCharSequence.charAt( charIndex );

      if ( c >= '0' && c <= '9' ) stringBuilder.append( c );
      }

    return ( stringBuilder.toString() );
    }


  /*****************************************************
   *
   * Returns true if the digits string starts with the supplied
   * range.
   *
   *****************************************************/
  static protected boolean digitsStartBetween( String digitsString, int rangeFirst, int rangeLast )
    {
    if ( digitsString == null || digitsString.length() < 1 || rangeFirst < 1 || rangeLast < 1 || rangeFirst > rangeLast ) return ( false );

    int prefixSize = 0;

    if      ( rangeLast <         10 ) prefixSize = 1;
    else if ( rangeLast <        100 ) prefixSize = 2;
    else if ( rangeLast <       1000 ) prefixSize = 3;
    else if ( rangeLast <      10000 ) prefixSize = 4;
    else if ( rangeLast <     100000 ) prefixSize = 5;
    else if ( rangeLast <    1000000 ) prefixSize = 6;
    else if ( rangeLast <   10000000 ) prefixSize = 7;
    else if ( rangeLast <  100000000 ) prefixSize = 8;

    if ( digitsString.length() < prefixSize ) return ( false );

    int prefix = Integer.parseInt( digitsString.substring( 0, prefixSize ) );

    if ( prefix >= rangeFirst && prefix <= rangeLast ) return ( true );

    return ( false );
    }


  /*****************************************************
   *
   * Formats a card number according to the supplied groups.
   *
   *****************************************************/
  static protected String formatNumber( String digitString, int... numberGroupings )
    {
    int digitStringLength;

    if ( digitString == null || ( digitStringLength = digitString.length() ) < 1 ) return ( "" );

    if ( numberGroupings == null || numberGroupings.length < 1 ) return ( digitString );


    StringBuilder stringBuilder = new StringBuilder();

    int digitIndex    = 0;
    int groupingIndex = 0;

    while ( digitIndex < digitStringLength && groupingIndex < numberGroupings.length )
      {
      int end = digitIndex + numberGroupings[ groupingIndex ];

      stringBuilder.append( safeSubstring( digitString, digitIndex, end ) );

      if ( end < digitStringLength ) stringBuilder.append( " " );

      digitIndex    += numberGroupings[ groupingIndex ];
      groupingIndex ++;
      }

    if ( digitIndex < digitString.length() ) stringBuilder.append( safeSubstring( digitString, digitIndex ) );

    return ( stringBuilder.toString() );
    }


  /*****************************************************
   *
   * Returns a substring, correcting any bounds.
   *
   *****************************************************/
  static protected String safeSubstring( String sourceString, int start, int end )
    {
    int sourceStringLength;

    if ( sourceString == null || ( sourceStringLength = sourceString.length() ) < 1 ) return ( "" );

    if ( start >= sourceStringLength ) return ( "" );

    if ( start < 0 ) start = 0;

    if ( end < start ) return ( "" );

    if ( end >= sourceStringLength ) end = sourceStringLength;

    return ( sourceString.substring( start, end ) );
    }


  /*****************************************************
   *
   * Returns a substring, correcting any bounds.
   *
   *****************************************************/
  static protected String safeSubstring( String sourceString, int start )
    {
    int sourceStringLength;

    if ( sourceString == null || ( sourceStringLength = sourceString.length() ) < 1 ) return ( "" );

    return ( safeSubstring( sourceString, start, sourceStringLength - 1 ) );
    }


  ////////// Constructor(s) //////////

  public AEditTextEnforcer( EditText editText, ICallback callback )
    {
    mEditText = editText;
    mCallback = callback;

    // Save the current text colour
    mOKTextColour = editText.getCurrentTextColor();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback
   *
   *****************************************************/
  public interface ICallback
    {
    public void eteOnTextComplete( EditText editText );
    }

  }