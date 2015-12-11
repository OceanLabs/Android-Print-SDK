/*****************************************************
 *
 * MonthEditTextEnforcer.java
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

import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class limits what can be typed into an edit text
 * to a valid month of the year.
 *
 *****************************************************/
public class MonthEditTextEnforcer extends ALimitedRangeEditTextEnforcer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "MonthEditTextEnforcer";

  static public final String[] VALID_STRINGS =
    {
    "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"
    };


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MonthEditTextEnforcer( EditText editText, ICallback callback )
    {
    super( editText, callback );
    }


  ////////// AFixedRangeEditTextEnforcer Method(s) //////////

  /*****************************************************
   *
   * Returns a valid string for a single entered character,
   * or null if no shortcut should be available.
   *
   *****************************************************/
  @Override
  protected String getShortcutString( char character )
    {
    if ( character >= '2' && character <= '9' ) return ( "0" + character );

    return ( null );
    }


  /*****************************************************
   *
   * Returns a list of all the valid strings that contain the
   * supplied (sub)string.
   *
   *****************************************************/
  @Override
  protected List<String> getValidStringsContaining( String searchString )
    {
    return ( getValidStrings( VALID_STRINGS, searchString ) );
    }


  ////////// Inner Class(es) //////////

  }