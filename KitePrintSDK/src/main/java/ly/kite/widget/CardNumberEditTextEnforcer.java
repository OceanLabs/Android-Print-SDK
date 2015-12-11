/*****************************************************
 *
 * CardNumberEditTextEnforcer.java
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
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;


///// Class Declaration /////

/*****************************************************
 *
 * This class limits what can be typed into an edit text
 * to a card number.
 *
 *****************************************************/
public class CardNumberEditTextEnforcer extends AEditTextEnforcer implements TextWatcher
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG              = "CardNumberEditTextEnforcer";

  static public  final int     REQUIRED_DIGIT_COUNT = 16;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public CardNumberEditTextEnforcer( EditText editText, ICallback callback )
    {
    super( editText, callback );

    // Set up the text change listener
    editText.addTextChangedListener( this );
    }


  public CardNumberEditTextEnforcer( EditText editText )
    {
    this( editText, null );
    }


  ////////// TextWatcher Method(s) //////////

  /*****************************************************
   *
   * Called before the text is changed.
   *
   *****************************************************/
  @Override
  public void beforeTextChanged( CharSequence s, int start, int count, int after )
    {
    // Do nothing
    }


  /*****************************************************
   *
   * Called after the text is changed.
   *
   *****************************************************/
  @Override
  public void onTextChanged( CharSequence charSequence, int start, int before, int count )
    {
    // Ignore empty strings
    if ( charSequence == null || charSequence.length() < 1 ) return;

    // Get just the digits from the string
    String digitsString = getDigits( charSequence );

    // Make sure we haven't exceeded the limit
    if ( digitsString.length() > REQUIRED_DIGIT_COUNT ) digitsString = digitsString.substring( 0, REQUIRED_DIGIT_COUNT );


    // Format the card number into sequences of 4 digits with a space between each one

    String formattedString;

    if ( digitsString.length() > 12 )
      {
      formattedString = digitsString.substring( 0, 4 ) + " " + digitsString.substring( 4, 8 ) + " " + digitsString.substring( 8, 12 ) + " " + digitsString.substring( 12 );
      }
    else if ( digitsString.length() > 8 )
      {
      formattedString = digitsString.substring( 0, 4 ) + " " + digitsString.substring( 4, 8 ) + " " + digitsString.substring( 8 );
      }
    else if ( digitsString.length() > 4 )
      {
      formattedString = digitsString.substring( 0, 4 ) + " " + digitsString.substring( 4 );
      }
    else
      {
      formattedString = digitsString;
      }


    // Only change the original string if it doesn't already match this formatted string - to avoid triggering
    // another text changed event (and an infinite loop).

    if ( ! formattedString.equals( charSequence.toString() ) )
      {
      mEditText.setText( formattedString );

      // Put the cursor at the end
      mEditText.setSelection( formattedString.length() );
      }


    // If we have the correct number of digits - call the callback
    if ( digitsString.length() == REQUIRED_DIGIT_COUNT && mCallback != null )
      {
      mCallback.eteOnTextComplete( mEditText );
      }
    }


  /*****************************************************
   *
   * Called after the text is changed.
   *
   *****************************************************/
  @Override
  public void afterTextChanged( Editable s )
    {
    // Do nothing
    }


  ////////// Inner Class(es) //////////

  }