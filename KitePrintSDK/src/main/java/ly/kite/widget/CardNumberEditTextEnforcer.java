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
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import ly.kite.R;


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
  static private final String  LOG_TAG                          = "CardNumberEditTextEnforcer";

  static public  final int     DEFAULT_REQUIRED_DIGIT_COUNT     = 16;

  static private final int     NO_LOGO_RESOURCE_ID              = 0;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ImageView            mLogoImageView;
  private CVVEditTextEnforcer  mCVVEditTextEnforcer;

  private int                  mMinRequiredDigitCount;
  private int                  mMaxRequiredDigitCount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public CardNumberEditTextEnforcer( EditText editText, ImageView logoImageView, CVVEditTextEnforcer cvvEditTextEnforcer, ICallback callback )
    {
    super( editText, callback );

    mLogoImageView       = logoImageView;
    mCVVEditTextEnforcer = cvvEditTextEnforcer;

    setRequiredDigitCount( DEFAULT_REQUIRED_DIGIT_COUNT );

    // Set up the text change listener
    editText.addTextChangedListener( this );
    }


  public CardNumberEditTextEnforcer( EditText editText, ICallback callback )
    {
    this( editText, null, null, callback );
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
    // Get just the digits from the string
    String digitsString = getDigits( charSequence );

    // Make sure we haven't exceeded the limit
    if ( digitsString.length() > mMaxRequiredDigitCount ) digitsString = digitsString.substring( 0, mMaxRequiredDigitCount );

    // Format the card number according to the type of credit card
    String formattedString = processCardNumber( digitsString );


    // Only change the original string if it doesn't already match this formatted string - to avoid triggering
    // another text changed event (and an infinite loop).

    if ( ! formattedString.equals( charSequence.toString() ) )
      {
      mEditText.setText( formattedString );

      // Put the cursor at the end
      mEditText.setSelection( formattedString.length() );
      }


    // If we have the correct number of digits - call the callback
    if ( digitsString.length() == mMaxRequiredDigitCount && mCallback != null )
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


  ////////// TextWatcher Method(s) //////////

  /*****************************************************
   *
   * Formats the credit card number.
   *
   *****************************************************/
  private String processCardNumber( String digitsString )
    {
    if ( digitsStartWith( digitsString, "34" ) ||
         digitsStartWith( digitsString, "37" ) )
      {
      ///// American Express /////

      setRequiredDigitCount( 15 );

      setCVVRequiredDigitCount( 4 );

      setLogo( R.drawable.credit_card_logo_amex );

      return ( formatNumber( digitsString, 4, 6, 5 ) );
      }
      else if ( digitsStartBetween( digitsString, 300,305 ) )
      {
      ///// Diner's Club Carte Blanche /////

      setRequiredDigitCount( 14 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_diners );

      return ( formatNumber( digitsString, 4, 6, 4 ) );
      }
    else if ( digitsStartWith( digitsString, "309" ) ||
              digitsStartWith ( digitsString, "36" ) ||
              digitsStartBetween( digitsString, 38, 39 ) )
      {
      ///// Diner's Club International /////

      setRequiredDigitCount( 14 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_diners );

      return ( formatNumber( digitsString, 4, 6, 4 ) );
      }
    else if ( digitsStartBetween( digitsString, 54, 55 ) )
      {
      ///// Diner's Club US & Canada /////

      // MasterCard co-branded, but split here in case we want to recognise it

      setRequiredDigitCount( 16 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_diners );

      return ( formatNumber( digitsString, 4, 4, 4, 4 ) );
      }
    else if ( digitsStartWith( digitsString, "6011" ) ||
              digitsStartBetween( digitsString, 622126, 622925 ) ||
              digitsStartBetween( digitsString, 644, 649 ) ||
              digitsStartWith( digitsString, "65" ) )
      {
      ///// Discover /////

      setRequiredDigitCount( 16, 19 );  // Technically should be 16 or 19 not 16-19

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_discover );

      return ( formatNumber( digitsString, 4, 10, 5 ) );
      }
    else if ( digitsStartBetween( digitsString, 3528, 3589 ) )
      {
      ///// JCB /////

      setRequiredDigitCount( 16 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_jcb );

      return ( formatNumber( digitsString, 4, 4, 4, 4 ) );
      }
    else if ( digitsStartBetween( digitsString, 2221, 2720 ) ||
              digitsStartBetween( digitsString, 51, 55 ) )
      {
      ///// MasterCard /////

      setRequiredDigitCount( 16 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_mastercard );

      return ( formatNumber( digitsString, 4, 4, 4, 4 ) );
      }
    else if ( digitsStartWith( digitsString, "50" ) ||
              digitsStartBetween( digitsString, 56, 69 ) )
      {
      ///// Maestro /////

      setRequiredDigitCount( 12, 19 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_maestro );

      return ( formatNumber( digitsString, 4, 4, 4, 10 ) );
      }
    else if ( digitsStartWith( digitsString, "4" ) )
      {
      ///// Visa /////

      setRequiredDigitCount( 16 );

      setCVVRequiredDigitCount( 3 );

      setLogo( R.drawable.credit_card_logo_visa );

      return ( formatNumber( digitsString, 4, 4, 4, 4 ) );
      }
    else
      {
      ///// Default /////

      setRequiredDigitCount( 16, 19 );

      setCVVRequiredDigitCount( 3 );

      setLogo( NO_LOGO_RESOURCE_ID );

      return ( formatNumber( digitsString, 4, 10, 5 ) );
      }

    }


  /*****************************************************
   *
   * Returns true if the digits string starts with the supplied
   * digits.
   *
   *****************************************************/
  private boolean digitsStartWith( String digitsString, String searchPrefix )
    {
    if ( digitsString == null || digitsString.length() < 1 || searchPrefix == null || searchPrefix.length() < 1 ) return ( false );

    return ( digitsString.startsWith( searchPrefix ) );
    }


  /*****************************************************
   *
   * Sets the number of required digits.
   *
   *****************************************************/
  private void setRequiredDigitCount( int minRequiredDigitCount, int maxRequiredDigitCount )
    {
    mMinRequiredDigitCount = minRequiredDigitCount;
    mMaxRequiredDigitCount = maxRequiredDigitCount;
    }


  /*****************************************************
   *
   * Sets the number of required digits.
   *
   *****************************************************/
  private void setRequiredDigitCount( int requiredDigitCount )
    {
    setRequiredDigitCount( requiredDigitCount, requiredDigitCount );
    }


  /*****************************************************
   *
   * Sets the logo.
   *
   *****************************************************/
  private void setLogo( int logoResourceId )
    {
    if ( mLogoImageView != null )
      {
      if ( logoResourceId != NO_LOGO_RESOURCE_ID ) mLogoImageView.setImageResource( logoResourceId );
      else                                         mLogoImageView.setImageDrawable( null );
      }
    }


  /*****************************************************
   *
   * Sets the number of CVV digits.
   *
   *****************************************************/
  private void setCVVRequiredDigitCount( int digitCount )
    {
    if ( mCVVEditTextEnforcer != null ) mCVVEditTextEnforcer.setRequiredDigitCount( digitCount );
    }


  ////////// Inner Class(es) //////////

  }