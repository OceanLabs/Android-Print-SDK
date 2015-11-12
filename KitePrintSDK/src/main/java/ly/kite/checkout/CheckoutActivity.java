/*****************************************************
 *
 * CheckoutActivity.java
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import ly.kite.KiteSDK;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.journey.AKiteActivity;
import ly.kite.pricing.IPricingConsumer;
import ly.kite.pricing.PricingAgent;
import ly.kite.catalogue.PrintJob;
import ly.kite.catalogue.PrintOrder;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;
import ly.kite.catalogue.CatalogueLoader;


///// Class Declaration /////

/*****************************************************
 *
 * This class displays the first screen of the check-out
 * process - the shipping screen.
 *
 *****************************************************/
public class CheckoutActivity extends AKiteActivity implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String LOG_TAG                    = "CheckoutActivity";

  public  static final String EXTRA_PRINT_ORDER          = "ly.kite.EXTRA_PRINT_ORDER";
  public  static final String EXTRA_PRINT_ENVIRONMENT    = "ly.kite.EXTRA_PRINT_ENVIRONMENT";
  public  static final String EXTRA_PRINT_API_KEY        = "ly.kite.EXTRA_PRINT_API_KEY";

  public  static final String ENVIRONMENT_STAGING        = "ly.kite.ENVIRONMENT_STAGING";
  public  static final String ENVIRONMENT_LIVE           = "ly.kite.ENVIRONMENT_LIVE";
  public  static final String ENVIRONMENT_TEST           = "ly.kite.ENVIRONMENT_TEST";

  private static final long   MAXIMUM_PRODUCT_AGE_MILLIS = 1 * 60 * 60 * 1000;

  private static final String SHIPPING_PREFERENCES       = "shipping_preferences";
  private static final String SHIPPING_PREFERENCE_EMAIL  = "shipping_preferences.email";
  private static final String SHIPPING_PREFERENCE_PHONE  = "shipping_preferences.phone";

  private static final int    REQUEST_CODE_PAYMENT       = 1;
  private static final int    REQUEST_CODE_ADDRESS_BOOK  = 2;

  private static final String           NO_PROMO_CODE_YET            = null;
  private static final IPricingConsumer DONT_BOTHER_RETURNING_PRICES = null;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private PrintOrder           mPrintOrder;
//  private String               mAPIKey;
//  private KiteSDK.Environment  mEnvironment;

  private EditText             mEmailEditText;
  private EditText             mPhoneEditText;
  private Button               mProceedButton;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  static public void start( Activity activity, PrintOrder printOrder, int requestCode )
    {
    Intent intent = new Intent( activity, CheckoutActivity.class );

    intent.putExtra( EXTRA_PRINT_ORDER, (Parcelable) printOrder );

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
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    requestWindowFeature( Window.FEATURE_ACTION_BAR );


    setContentView( R.layout.screen_checkout );

    mEmailEditText = (EditText)findViewById( R.id.email_edit_text );
    mPhoneEditText = (EditText)findViewById( R.id.phone_edit_text );
    mProceedButton = (Button)findViewById( R.id.proceed_overlay_button );


    // Restore email address and phone number from history
    // Restore preferences

    SharedPreferences settings = getSharedPreferences( SHIPPING_PREFERENCES, 0 );

    String email = settings.getString( SHIPPING_PREFERENCE_EMAIL, null );
    String phone = settings.getString( SHIPPING_PREFERENCE_PHONE, null );

    if ( email != null ) mEmailEditText.setText( email );
    if ( phone != null ) mPhoneEditText.setText( phone );

    if ( !KiteSDK.getInstance( this ).getRequestPhoneNumber() )
      {
      mPhoneEditText.setVisibility( View.GONE );
      findViewById(R.id.phone_require_reason).setVisibility( View.GONE );
      }


    String apiKey = getIntent().getStringExtra( EXTRA_PRINT_API_KEY );
    String envString = getIntent().getStringExtra( EXTRA_PRINT_ENVIRONMENT );

    mPrintOrder = (PrintOrder) getIntent().getParcelableExtra( EXTRA_PRINT_ORDER );

    if ( apiKey == null )
      {
      apiKey = KiteSDK.getInstance( this ).getAPIKey();
      if ( apiKey == null )
        {
        throw new IllegalArgumentException( "You must specify an API key string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize" );
        }
      }

    if ( mPrintOrder == null )
      {
      throw new IllegalArgumentException( "You must specify a PrintOrder object extra in the intent used to start the CheckoutActivity" );
      }

    if ( mPrintOrder.getJobs().size() < 1 )
      {
      throw new IllegalArgumentException( "You must specify a PrintOrder object extra that actually has some jobs for printing i.e. PrintOrder.getJobs().size() > 0" );
      }

//    KiteSDK.Environment environment = null;
//
//    if ( envString == null )
//      {
//      environment = KiteSDK.getInstance( this ).getEnvironment();
//
//      if ( environment == null )
//        {
//        throw new IllegalArgumentException( "You must specify an environment string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize" );
//        }
//      }
//    else
//      {
//      if ( envString.equals( ENVIRONMENT_STAGING ) )
//        {
//        environment = KiteSDK.DefaultEnvironment.STAGING;
//        }
//      else if ( envString.equals( ENVIRONMENT_TEST ) )
//        {
//        environment = KiteSDK.DefaultEnvironment.TEST;
//        }
//      else if ( envString.equals( ENVIRONMENT_LIVE ) )
//        {
//        environment = KiteSDK.DefaultEnvironment.LIVE;
//        }
//      else
//        {
//        throw new IllegalArgumentException( "Bad print environment extra: " + envString );
//        }
//      }
//
//    mAPIKey      = apiKey;
//    mEnvironment = environment;
//
//    KiteSDK.getInstance( this ).setEnvironment( apiKey, environment );


    mProceedButton.setText( R.string.shipping_proceed_button_text );


    // hide keyboard initially
    getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );


    // Request the pricing now - even though we don't use it on this screen, and it may change once
    // a shipping address has been chosen (if the shipping address country is different to the default
    // locale). This is to minimise any delay to the user.
    PricingAgent.getInstance().requestPricing( this, mPrintOrder, NO_PROMO_CODE_YET, DONT_BOTHER_RETURNING_PRICES );


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackShippingScreenViewed( mPrintOrder, Analytics.VARIANT_JSON_PROPERTY_VALUE_CLASSIC_PLUS_ADDRESS_SEARCH, true );
      }


    mProceedButton.setOnClickListener( this );
    }


  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    outState.putParcelable( EXTRA_PRINT_ORDER, mPrintOrder );
//    outState.putString( EXTRA_PRINT_API_KEY, mAPIKey );
//    outState.putParcelable( EXTRA_PRINT_ENVIRONMENT, mEnvironment );
    }

  @Override
  protected void onRestoreInstanceState( Bundle savedInstanceState )
    {
    super.onRestoreInstanceState( savedInstanceState );

    mPrintOrder  = savedInstanceState.getParcelable( EXTRA_PRINT_ORDER );
//    mAPIKey      = savedInstanceState.getString( EXTRA_PRINT_API_KEY );
//    mEnvironment = (KiteSDK.DefaultEnvironment) savedInstanceState.getSerializable( EXTRA_PRINT_ENVIRONMENT );
//
//    KiteSDK.getInstance( this ).setEnvironment( mAPIKey, mEnvironment );
    }

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


  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_PAYMENT )
      {
      if ( resultCode == Activity.RESULT_OK )
        {
        setResult( Activity.RESULT_OK );

        finish();
        }
      }
    else if ( requestCode == REQUEST_CODE_ADDRESS_BOOK )
      {
      if ( resultCode == RESULT_OK )
        {
        Address address = data.getParcelableExtra( AddressBookActivity.EXTRA_ADDRESS );
        mPrintOrder.setShippingAddress( address );
        Button chooseAddressButton = (Button) findViewById( R.id.address_picker_button );
        chooseAddressButton.setText( address.toString() );

        // Re-request the pricing if the shipping address changes, just in case the shipping
        // price changes.
        PricingAgent.getInstance().requestPricing( this, mPrintOrder, NO_PROMO_CODE_YET, DONT_BOTHER_RETURNING_PRICES );
        }
      }
    }


  ////////// View.OnClickListener Method(s) //////////

  @Override
  public void onClick( View view )
    {
    if ( view == mProceedButton )
      {
      onProceedButtonClicked();
      }
    }


  ////////// Method(s) //////////

  public void onChooseDeliveryAddressButtonClicked( View view )
    {
    Intent i = new Intent( this, AddressBookActivity.class );
    startActivityForResult( i, REQUEST_CODE_ADDRESS_BOOK );
    }

//  private String getPaymentActivityEnvironment()
//    {
//    switch ( mEnvironment )
//      {
//      case LIVE:
//        return PaymentActivity.ENVIRONMENT_LIVE;
//      case STAGING:
//        return PaymentActivity.ENVIRONMENT_STAGING;
//      case TEST:
//        return PaymentActivity.ENVIRONMENT_TEST;
//      default:
//        throw new IllegalStateException( "oops" );
//      }
//    }

  private void showErrorDialog( String title, String message )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( title ).setMessage( message ).setPositiveButton( "OK", null );
    Dialog d = builder.create();
    d.show();
    }

  public void onProceedButtonClicked()
    {
    String email = mEmailEditText.getText().toString();
    String phone = mPhoneEditText.getText().toString();

    if ( mPrintOrder.getShippingAddress() == null )
      {
      showErrorDialog( "Invalid Delivery Address", "Please choose a delivery address" );
      return;
      }

    if ( !isEmailValid( email ) )
      {
      showErrorDialog( "Invalid Email Address", "Please enter a valid email address" );
      return;
      }

    if ( KiteSDK.getInstance( this ).getRequestPhoneNumber() && phone.length() < 5 )
      {
      showErrorDialog( "Invalid Phone Number", "Please enter a valid phone number" );
      return;
      }

    JSONObject userData = mPrintOrder.getUserData();
    if ( userData == null )
      {
      userData = new JSONObject();
      }

    try
      {
      userData.put( "email", email );
      userData.put( "phone", phone );
      }
    catch ( JSONException ex )
      {/* ignore */}
    mPrintOrder.setUserData( userData );
    mPrintOrder.setNotificationEmail( email );
    mPrintOrder.setNotificationPhoneNumber( phone );

    SharedPreferences settings = getSharedPreferences( SHIPPING_PREFERENCES, 0 );
    SharedPreferences.Editor editor = settings.edit();
    editor.putString( SHIPPING_PREFERENCE_EMAIL, email );
    editor.putString( SHIPPING_PREFERENCE_PHONE, phone );
    editor.commit();


    // Make sure we have up-to-date products before we proceed

    final ProgressDialog progress = ProgressDialog.show( this, null, "Loading" );

    CatalogueLoader.getInstance( this ).requestCatalogue(
            MAXIMUM_PRODUCT_AGE_MILLIS,
            new ICatalogueConsumer()
            {
            @Override
            public void onCatalogueSuccess( Catalogue catalogue )
              {
              progress.dismiss();

              startPaymentActivity();
              }

            @Override
            public void onCatalogueError( Exception exception )
              {
              progress.dismiss();

              showRetryTemplateSyncDialog( exception );
              }
            }
    );
    }

  private void showRetryTemplateSyncDialog( Exception error )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( CheckoutActivity.this );
    builder.setTitle( "Oops" );
    builder.setMessage( error.getLocalizedMessage() );
    if ( error instanceof UnknownHostException || error instanceof SocketTimeoutException )
      {
      builder.setMessage( "Please check your internet connectivity and then try again" );
      }

    builder.setPositiveButton( "Retry", new DialogInterface.OnClickListener()
    {
    @Override
    public void onClick( DialogInterface dialogInterface, int i )
      {
      onProceedButtonClicked();
      }
    } );
    builder.setNegativeButton( "Cancel", null );
    builder.show();
    }

  private void startPaymentActivity()
    {

    // Check we have valid templates for every printjob

    try
      {
      // This will return null if there are no products, or they are out of date, but that's
      // OK because we catch any exceptions.
      Catalogue catalogue = CatalogueLoader.getInstance( this ).getCachedCatalogue( MAXIMUM_PRODUCT_AGE_MILLIS );

      // Go through every print job and check that we can get a product from the product id
      for ( PrintJob job : mPrintOrder.getJobs() )
        {
        catalogue.confirmProductIdExistsOrThrow( job.getProduct().getId() );
        }
      }
    catch ( Exception exception )
      {
      showRetryTemplateSyncDialog( exception );

      return;
      }

    KiteSDK kiteSDK = KiteSDK.getInstance( this );

    PaymentActivity.start( this, mPrintOrder, kiteSDK.getAPIKey(), kiteSDK.getEnvironment().getPaymentActivityEnvironment(), REQUEST_CODE_PAYMENT );
    }

  boolean isEmailValid( CharSequence email )
    {
    return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/
  }

