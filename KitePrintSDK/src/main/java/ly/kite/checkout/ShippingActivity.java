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

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ly.kite.KiteSDK;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.journey.AKiteActivity;
import ly.kite.pricing.PricingAgent;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;
import ly.kite.catalogue.CatalogueLoader;


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
public class ShippingActivity extends AKiteActivity implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String                         LOG_TAG                      = "ShippingActivity";

  static public  final String                         KEY_ORDER                    = "ly.kite.order";
  static public  final String                         KEY_SHIPPING_ADDRESS         = "ly.kite.shippingaddress";
  static public  final String                         KEY_EMAIL                    = "ly.kite.email";
  static public  final String                         KEY_PHONE                    = "ly.kite.phone";

  //private static final long                           MAXIMUM_PRODUCT_AGE_MILLIS   = 1 * 60 * 60 * 1000;

  static private final String                         SHIPPING_PREFERENCES         = "shipping_preferences";
  static private final String                         SHIPPING_PREFERENCE_EMAIL    = "shipping_preferences.email";
  static private final String                         SHIPPING_PREFERENCE_PHONE    = "shipping_preferences.phone";

  static private final int                            REQUEST_CODE_PAYMENT         = 1;
  static private final int                            REQUEST_CODE_ADDRESS_BOOK    = 2;

  static private final String                         NO_PROMO_CODE_YET            = null;
  static private final PricingAgent.IPricingConsumer  DONT_BOTHER_RETURNING_PRICES = null;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Address   mShippingAddress;
  private String    mInitialEmail;
  private String    mInitialPhone;

  private Button    mAddressPickerButton;
  private EditText  mEmailEditText;
  private EditText  mPhoneEditText;
  private Button    mForwardsButton;


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


    // We only need to pass the order to the activity for analytics
    intent.putExtra( KEY_ORDER, order );


    // Put any shipping address, email, and phone number from the order into the intent.

    intent.putExtra( KEY_SHIPPING_ADDRESS, (Parcelable)order.getShippingAddress() );

    JSONObject userData = order.getUserData();

    if ( userData != null )
      {
      intent.putExtra( KEY_EMAIL, userData.optString( "email" ) );
      intent.putExtra( KEY_PHONE, userData.optString( "phone" ) );
      }


    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Returns the shipping address from an intent.
   *
   *****************************************************/
  static public Address getShippingAddress( Intent data )
    {
    return ( data.getParcelableExtra( KEY_SHIPPING_ADDRESS ) );
    }


  /*****************************************************
   *
   * Returns the email from an intent.
   *
   *****************************************************/
  static public String getEmail( Intent data )
    {
    return ( data.getStringExtra( KEY_EMAIL ) );
    }


  /*****************************************************
   *
   * Returns the phone number from an intent.
   *
   *****************************************************/
  static public String getPhone( Intent data )
    {
    return ( data.getStringExtra( KEY_PHONE ) );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // See if we have saved a shipping address, email, and phone number

    if ( savedInstanceState != null )
      {
      mShippingAddress = savedInstanceState.getParcelable( KEY_SHIPPING_ADDRESS );
      mInitialEmail    = savedInstanceState.getString( KEY_EMAIL );
      mInitialPhone    = savedInstanceState.getString( KEY_PHONE );
      }
    else
      {
      }


    // If some values weren't saved - try and get them from the intent

    Intent intent = getIntent();

    Order order   = null;

    if ( intent != null )
      {
      if ( mShippingAddress == null ) mShippingAddress = intent.getParcelableExtra( KEY_SHIPPING_ADDRESS );
      if ( mInitialEmail    == null ) mInitialEmail    = intent.getStringExtra( KEY_EMAIL );
      if ( mInitialPhone    == null ) mInitialPhone    = intent.getStringExtra( KEY_PHONE );

      // Get the order just for analytics
      order = intent.getParcelableExtra( KEY_ORDER );
      }


    // If we still don't have an email or phone - try and get persisted values

    SharedPreferences settings = getSharedPreferences( SHIPPING_PREFERENCES, 0 );

    if ( mInitialEmail == null ) mInitialEmail = settings.getString( SHIPPING_PREFERENCE_EMAIL, null );
    if ( mInitialPhone == null ) mInitialPhone = settings.getString( SHIPPING_PREFERENCE_PHONE, null );


    // Set up the screen

    setContentView( R.layout.screen_shipping );

    mAddressPickerButton          = (Button)findViewById( R.id.address_picker_button );
    mEmailEditText                = (EditText)findViewById( R.id.email_edit_text );
    mPhoneEditText                = (EditText)findViewById( R.id.phone_edit_text );
    TextView requirePhoneTextView = (TextView)findViewById( R.id.phone_require_reason );


    // Set up the forwards button

    mForwardsButton = (Button)findViewById( R.id.cta_bar_right_button );

    if ( mForwardsButton == null )
      {
      mForwardsButton = (Button)findViewById( R.id.proceed_overlay_button );
      }

    mForwardsButton.setText( R.string.shipping_proceed_button_text );


    // Display any values

    if ( mShippingAddress != null ) onShippingAddress( mShippingAddress );
    if ( mInitialEmail    != null ) mEmailEditText.setText( mInitialEmail );

    if ( KiteSDK.getInstance( this ).getRequestPhoneNumber() )
      {
      mPhoneEditText.setVisibility( View.VISIBLE );
      requirePhoneTextView.setVisibility( View.VISIBLE );

      if ( mInitialPhone != null ) mPhoneEditText.setText( mInitialPhone );
      }
    else
      {
      mPhoneEditText.setVisibility( View.GONE );
      requirePhoneTextView.setVisibility( View.GONE );
      }


    // hide keyboard initially
    getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );


    // Analytics
    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackShippingScreenViewed( order, Analytics.VARIANT_JSON_PROPERTY_VALUE_CLASSIC_PLUS_ADDRESS_SEARCH, true );
      }


    // Set up a listener on the forwards button
    mForwardsButton.setOnClickListener( this );
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
      mShippingAddress = AddressBookActivity.getAddress( data );

      onShippingAddress( mShippingAddress );
      }
    }


  ////////// View.OnClickListener Method(s) //////////

  @Override
  public void onClick( View view )
    {
    if ( view == mForwardsButton )
      {
      onForwardsButtonClicked();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the shipping address changes.
   *
   *****************************************************/
  private void onShippingAddress( Address shippingAddress )
    {
    mAddressPickerButton.setText( shippingAddress.toMultiLineText() );
    }


  public void onChooseDeliveryAddressButtonClicked( View view )
    {
    Intent i = new Intent( this, AddressBookActivity.class );
    startActivityForResult( i, REQUEST_CODE_ADDRESS_BOOK );
    }

  private void showErrorDialog( String title, String message )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( title ).setMessage( message ).setPositiveButton( R.string.OK, null );
    Dialog d = builder.create();
    d.show();
    }

  private void showErrorDialog( int titleResourceId, int messageResourceId )
    {
    showErrorDialog( getString( titleResourceId ), getString( messageResourceId ) );
    }


  public void onForwardsButtonClicked()
    {
    // Prepare an intent to return
    Intent resultIntent = new Intent();


    // Check that we have a shipping address

    if ( mShippingAddress == null )
      {
      showErrorDialog( R.string.alert_dialog_title_invalid_delivery_address, R.string.alert_dialog_message_invalid_delivery_address );

      return;
      }

    resultIntent.putExtra( KEY_SHIPPING_ADDRESS, (Parcelable)mShippingAddress );


    // Save any valid email / phone
    SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences( SHIPPING_PREFERENCES, 0 ).edit();


    // Check that we have a valid email

    String email = getPopulatedStringOrNull( mEmailEditText );

    if ( ! isEmailValid( email ) )
      {
      showErrorDialog( R.string.alert_dialog_title_invalid_email_address, R.string.alert_dialog_message_invalid_email_address );

      return;
      }

    resultIntent.putExtra( KEY_EMAIL, email );

    sharedPreferencesEditor.putString( SHIPPING_PREFERENCE_EMAIL, email );


    // Check that we need and have a valid phone number

    if ( KiteSDK.getInstance( this ).getRequestPhoneNumber() )
      {
      String phone = getPopulatedStringOrNull( mPhoneEditText );

      if ( phone.length() < 5 )
        {
        showErrorDialog( R.string.alert_dialog_title_invalid_phone_number, R.string.alert_dialog_message_invalid_phone_number );

        return;
        }

      resultIntent.putExtra( KEY_PHONE, phone );

      sharedPreferencesEditor.putString( SHIPPING_PREFERENCE_PHONE, phone );
      }


    // Commit the shared preferences
    sharedPreferencesEditor.apply();

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

