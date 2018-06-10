/*****************************************************
 *
 * ShippingActivity.java
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

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ly.kite.KiteSDK;
import ly.kite.address.AAddressActivity;
import ly.kite.address.AddressEditActivity;
import ly.kite.analytics.Analytics;
import ly.kite.pricing.PricingAgent;
import ly.kite.ordering.Order;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This class displays the shipping screen of the check-out
 * process. If the activity is supplied an order then
 * managed check-out is assumed, that is: this becomes
 * the first screen in the check-out process. If no order is
 * supplied, then we assume that the caller is managing the
 * check-out process, and we simply collect the shipping
 * address, email and phone number.
 *
 *****************************************************/
public class ShippingActivity extends AShippingActivity implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String                         LOG_TAG                      = "ShippingActivity";

  static private final String                         PARAMETER_NAME_SHIPPING_EMAIL = "shipping_email";
  static private final String                         PARAMETER_NAME_SHIPPING_PHONE = "shipping_phone";

  static private final int                            REQUEST_CODE_PAYMENT         = 1;
  static private final int                            REQUEST_CODE_ADDRESS_BOOK    = 2;

  static private final String                         NO_PROMO_CODE_YET            = null;
  static private final PricingAgent.IPricingConsumer  DONT_BOTHER_RETURNING_PRICES = null;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean   mRequestPhoneNumber;

  private Address   mShippingAddress;
  private String    mInitialEmail;
  private String    mInitialPhone;

  private Button    mAddressPickerButton;
  private EditText  mEmailEditText;
  private View      mPhoneView;
  private EditText  mPhoneEditText;
  private TextView  mForwardsTextView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts the activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order order, int requestCode )
    {
    Intent intent = new Intent( activity, ShippingActivity.class );

    addExtras( order, intent );

    activity.startActivityForResult( intent, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    mRequestPhoneNumber = mSDKCustomiser.requestPhoneNumber();


    // See if we have saved a shipping address, email, and phone number

    if ( savedInstanceState != null )
      {
      mShippingAddress = savedInstanceState.getParcelable( KEY_SHIPPING_ADDRESS );
      mInitialEmail    = savedInstanceState.getString( KEY_EMAIL );
      mInitialPhone    = savedInstanceState.getString( KEY_PHONE );
      }


    // If some values weren't saved - try and get them from the intent

    Intent intent = getIntent();

    Order  order  = null;

    if ( intent != null )
      {
      if ( mShippingAddress == null ) mShippingAddress = intent.getParcelableExtra( KEY_SHIPPING_ADDRESS );
      if ( mInitialEmail    == null ) mInitialEmail    = intent.getStringExtra( KEY_EMAIL );
      if ( mInitialPhone    == null ) mInitialPhone    = intent.getStringExtra( KEY_PHONE );

      // Get the order just for analytics
      order = intent.getParcelableExtra( KEY_ORDER );
      }


    // If we still don't have an email or phone - try and get persisted values

    if ( mInitialEmail == null ) mInitialEmail = KiteSDK.getStringAppParameter( this, KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_EMAIL, null );
    if ( mInitialPhone == null ) mInitialPhone = KiteSDK.getStringAppParameter( this, KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_PHONE, null );


    // Set up the screen

    setContentView( R.layout.screen_shipping );

    mAddressPickerButton          = (Button)findViewById( R.id.address_picker_button );
    mEmailEditText                = (EditText)findViewById( R.id.email_edit_text );
    mPhoneView                    = findViewById( R.id.phone_view );
    mPhoneEditText                = (EditText)findViewById( R.id.phone_edit_text );
    TextView requirePhoneTextView = (TextView)findViewById( R.id.phone_require_reason );


    // Set up the forwards button

    mForwardsTextView = (TextView)findViewById( R.id.cta_bar_right_text_view );

    if ( mForwardsTextView == null )
      {
      mForwardsTextView = (TextView)findViewById( R.id.proceed_overlay_text_view );
      }

    mForwardsTextView.setText( R.string.kitesdk_shipping_proceed_button_text);


    // Set up Privacy Policy and Terms of Use links

    TextView mPrivacyTermsTextView = findViewById(R.id.privacy_terms_text);

    String html = getString(R.string.kitesdk_privacy_terms_text_html, getResources().getString(R.string.kitesdk_url_terms_of_use), getResources().getString(R.string.kitesdk_url_privacy_policy));
    Spannable result = (Spannable) Html.fromHtml(html);

    for (URLSpan u: result.getSpans(0, result.length(), URLSpan.class)) {
      result.setSpan(new UnderlineSpan() {
        public void updateDrawState(TextPaint tp) {
          tp.setUnderlineText(false);
        }
      }, result.getSpanStart(u), result.getSpanEnd(u), 0);
    }

    mPrivacyTermsTextView.setText(result);
    mPrivacyTermsTextView.setMovementMethod(LinkMovementMethod.getInstance());

    // Display any values

    if ( mShippingAddress != null ) onShippingAddress( mShippingAddress );
    if ( mInitialEmail    != null ) mEmailEditText.setText( mInitialEmail );

    if ( mRequestPhoneNumber )
      {
      setViewVisibilitySafely( mPhoneView, View.VISIBLE );
      setViewVisibilitySafely( mPhoneEditText, View.VISIBLE );
      setViewVisibilitySafely( requirePhoneTextView, View.VISIBLE );

      if ( mInitialPhone != null ) mPhoneEditText.setText( mInitialPhone );
      }
    else
      {
      setViewVisibilitySafely( mPhoneView, View.GONE );
      setViewVisibilitySafely( mPhoneEditText, View.GONE );
      setViewVisibilitySafely( requirePhoneTextView, View.GONE );
      }


    // hide keyboard initially
    getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );


    // Analytics
    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackShippingScreenViewed( order, Analytics.VARIANT_JSON_PROPERTY_VALUE_CLASSIC_PLUS_ADDRESS_SEARCH, true );
      }


    // Set up a listener on the forwards button
    mForwardsTextView.setOnClickListener( this );
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    if ( mShippingAddress != null ) outState.putParcelable( KEY_SHIPPING_ADDRESS, mShippingAddress );

    String email = getPopulatedStringOrNull( mEmailEditText );
    if ( email != null ) outState.putString( KEY_EMAIL, email );

    String phone = getPopulatedStringOrNull( mPhoneEditText );
    if ( phone != null ) outState.putString( KEY_PHONE, phone );
    }


  /*****************************************************
   *
   * Called when a menu (or action bar) item is selected.
   *
   *****************************************************/
  @Override
  public boolean onMenuItemSelected( int featureId, MenuItem item )
    {
    if ( item.getItemId() == android.R.id.home )
      {
      finish();

      return ( true );
      }

    return ( super.onMenuItemSelected( featureId, item ) );
    }


  /*****************************************************
   *
   * Called when the address book returns a new address.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_ADDRESS_BOOK && resultCode == RESULT_OK )
      {
      mShippingAddress = AAddressActivity.getAddress( data );

      onShippingAddress( mShippingAddress );
      }
    }


  ////////// View.OnClickListener Method(s) //////////

  @Override
  public void onClick( View view )
    {
    if ( view == mForwardsTextView )
      {
      onForwardsClicked();

      return;
      }

    super.onClick( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the shipping address changes.
   *
   *****************************************************/
  private void onShippingAddress( Address shippingAddress )
    {
    if(shippingAddress != null && shippingAddress.isFilledIn()) {
      mAddressPickerButton.setText(shippingAddress.toMultiLineText());
    } else {
      mAddressPickerButton.setText( getString(R.string.kitesdk_shipping_delivery_address_button_text));
    }
    }


  public void onChooseDeliveryAddressButtonClicked( View view )
    {
    // If the address book is enabled - go to the address book activity. Otherwise
    // go direct to the new address activity.

    if ( mSDKCustomiser.addressBookEnabled() )
      {
      AddressBookActivity.startForResult( this, REQUEST_CODE_ADDRESS_BOOK );
      }
    else
      {
      AddressEditActivity.startForResult( this, mShippingAddress, REQUEST_CODE_ADDRESS_BOOK );
      }
    }


  public void onForwardsClicked()
    {
    // Prepare an intent to return
    Intent resultIntent = new Intent();


    // Check that we have a shipping address

    if ( mShippingAddress == null )
      {
      showErrorDialog( R.string.kitesdk_alert_dialog_title_invalid_delivery_address, R.string.kitesdk_alert_dialog_message_invalid_delivery_address);

      return;
      }

    resultIntent.putExtra( KEY_SHIPPING_ADDRESS, (Parcelable)mShippingAddress );


    // Validate the email

    String email = getPopulatedStringOrNull( mEmailEditText );

    if ( ! isEmailValid( email ) )
      {
      showErrorDialog( R.string.kitesdk_alert_dialog_title_invalid_email_address, R.string.kitesdk_alert_dialog_message_invalid_email_address);

      return;
      }


    // Save the email

    resultIntent.putExtra( KEY_EMAIL, email );

    KiteSDK.setAppParameter( this, KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_EMAIL, email );


    // Check that we need and have a valid phone number

    if ( mRequestPhoneNumber )
      {
      String phone = getPopulatedStringOrNull( mPhoneEditText );

      if ( phone == null || phone.trim().length() < 5 )
        {
        showErrorDialog( R.string.kitesdk_alert_dialog_title_invalid_phone_number, R.string.kitesdk_alert_dialog_message_invalid_phone_number);

        return;
        }


      // Save the phone number

      resultIntent.putExtra( KEY_PHONE, phone );

      KiteSDK.setAppParameter( this, KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_SHIPPING_PHONE, phone );
      }


    // Return the values
    setResult( RESULT_OK, resultIntent );

    finish();
    }


  boolean isEmailValid( CharSequence email )
    {
    if ( email == null ) return ( false );

    return ( android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/
  }

