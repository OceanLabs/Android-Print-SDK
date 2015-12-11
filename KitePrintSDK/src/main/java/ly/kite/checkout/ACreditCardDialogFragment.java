/*****************************************************
 *
 * ACreditCardFragment.java
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

package ly.kite.checkout;


///// Import(s) /////

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import ly.kite.R;
import ly.kite.widget.AEditTextEnforcer;
import ly.kite.widget.CVVEditTextEnforcer;
import ly.kite.widget.CardNumberEditTextEnforcer;
import ly.kite.widget.MonthEditTextEnforcer;
import ly.kite.widget.YearEditTextEnforcer;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the parent of fragments that
 * collect credit card details.
 *
 *****************************************************/
abstract public class ACreditCardDialogFragment extends DialogFragment implements AEditTextEnforcer.ICallback,
                                                                                  View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                    = "ACreditCardFragment";

  static private final int     MAX_CARD_VALIDITY_IN_YEARS = 20;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private EditText  mCardNumberEditText;
  private EditText  mExpiryMonthEditText;
  private EditText  mExpiryYearEditText;
  private EditText  mCVVEditText;
  private TextView  mErrorTextView;
  private Button    mCancelButton;
  private Button    mProceedButton;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// DialogFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState)
    {
    super.onCreate( savedInstanceState );

    setStyle( STYLE_NO_TITLE, 0 );
    }


  /*****************************************************
   *
   * Creates and returns the view for this fragment.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.dialog_credit_card, null );

    mCardNumberEditText  = (EditText)view.findViewById( R.id.card_number_edit_text );
    mExpiryMonthEditText = (EditText)view.findViewById( R.id.expiry_month_edit_text );
    mExpiryYearEditText  = (EditText)view.findViewById( R.id.expiry_year_edit_text );
    mCVVEditText         = (EditText)view.findViewById( R.id.cvv_edit_text );
    mErrorTextView       = (TextView)view.findViewById( R.id.error_text_view );
    mCancelButton        = (Button)view.findViewById( R.id.cancel_button );
    mProceedButton       = (Button)view.findViewById( R.id.proceed_button );


    // Set up enforcers for the text fields

    if ( mCardNumberEditText != null )
      {
      new CardNumberEditTextEnforcer( mCardNumberEditText, this );
      }

    if ( mExpiryMonthEditText != null )
      {
      new MonthEditTextEnforcer( mExpiryMonthEditText, this );
      }

    if ( mExpiryYearEditText != null )
      {
      // The valid years are from now until the maximum allowed expiry

      int firstYear = Calendar.getInstance().get( Calendar.YEAR );

      new YearEditTextEnforcer( mExpiryYearEditText, firstYear, firstYear + MAX_CARD_VALIDITY_IN_YEARS, this );
      }

    if ( mCVVEditText != null )
      {
      new CVVEditTextEnforcer( mCVVEditText, this );
      }

    if ( mCancelButton  != null ) mCancelButton.setOnClickListener( this );
    if ( mProceedButton != null ) mProceedButton.setOnClickListener( this );

    return ( view );
    }


  ////////// AEditTextEnforcer.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the card number is complete.
   *
   *****************************************************/
  @Override
  public void eteOnTextComplete( EditText editText )
    {
    onClearError();


    // Check what edit text has been completed, and shift the focus to the next
    // one.

    if ( editText == mCardNumberEditText )
      {
      // Card number -> expiry month

      if ( mExpiryMonthEditText != null )
        {
        mExpiryMonthEditText.requestFocus();
        }
      }

    else if ( editText == mExpiryMonthEditText )
      {
      // Expiry month -> expiry year

      if ( mExpiryYearEditText != null )
        {
        mExpiryYearEditText.requestFocus();
        }
      }

    else if ( editText == mExpiryYearEditText )
      {
      // Expiry year -> CVV

      if ( mCVVEditText != null )
        {
        mCVVEditText.requestFocus();
        }
      }

    else if ( editText == mCVVEditText )
      {
      // CVV -> Done button

      if ( mProceedButton != null )
        {
        mProceedButton.requestFocus();
        }
      }

    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mCancelButton )
      {
      dismiss();
      }

    else if ( view == mProceedButton )
      {
      onProceed();
      }

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the proceed button has been clicked.
   *
   *****************************************************/
  private void onProceed()
    {
    String cardNumberString  = mCardNumberEditText.getText().toString();
    String expiryMonthString = mExpiryMonthEditText.getText().toString();
    String expiryYearString  = mExpiryYearEditText.getText().toString();
    String cvvString         = mCVVEditText.getText().toString();

    onProceed( cardNumberString,
               expiryMonthString,
               expiryYearString,
               cvvString );
    }


  /*****************************************************
   *
   * Called to clear any error text.
   *
   *****************************************************/
  protected void onClearError()
    {
    mErrorTextView.setText( null );
    }


  /*****************************************************
   *
   * Called when there is an error with the card details.
   *
   *****************************************************/
  protected void onDisplayError( String message )
    {
    mErrorTextView.setText( message );
    }


  /*****************************************************
   *
   * Called when there is an error with the card details.
   *
   *****************************************************/
  protected void onDisplayError( int messageResourceId )
    {
    mErrorTextView.setText( getActivity().getString( messageResourceId ) );
    }


  /*****************************************************
   *
   * Called when the proceed button has been clicked, and
   * the obvious details validated.
   *
   *****************************************************/
  abstract protected void onProceed( String cardNumberString, String expiryMonthString, String expiryYearString, String cvvString );


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

