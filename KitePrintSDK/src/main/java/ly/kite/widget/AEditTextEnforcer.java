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

import android.text.Editable;
import android.text.TextWatcher;
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
  static public String getDigits( CharSequence originalCharSequence )
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