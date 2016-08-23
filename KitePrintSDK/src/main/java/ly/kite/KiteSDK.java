/*****************************************************
 *
 * KiteSDK.java
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

package ly.kite;


///// Import(s) /////

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.catalogue.Product;
import ly.kite.ordering.OrderingDataAgent;
import ly.kite.catalogue.CatalogueLoader;
import ly.kite.checkout.AShippingActivity;
import ly.kite.checkout.DefaultPaymentFragment;
import ly.kite.checkout.APaymentFragment;
import ly.kite.checkout.PaymentActivity;
import ly.kite.journey.basket.BasketActivity;
import ly.kite.ordering.Order;
import ly.kite.payment.PayPalCard;
import ly.kite.util.Asset;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.instagramphotopicker.InstagramImageSource;
import ly.kite.journey.selection.ProductSelectionActivity;
import ly.kite.util.AssetHelper;
import ly.kite.util.DelimitedStringBuilder;
import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This singleton class is the Kite SDK, which holds
 * details about the API key and current environment.
 *
 *****************************************************/
public class KiteSDK
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG                                              = "KiteSDK";

  static public  final boolean DEBUG_PAYMENT_KEYS                                  = false;
  static public  final boolean DEBUG_PRICING                                       = false;
  static public  final boolean DEBUG_RETAINED_FRAGMENT                             = true;
  static public  final boolean DISPLAY_PRODUCT_JSON                                = false;

  static public  final String SDK_VERSION                                          = "5.3.1";

  static public  final String IMAGE_CATEGORY_APP                                   = "app";
  static public  final String IMAGE_CATEGORY_PRODUCT_ITEM                          = "product_item";
  static public  final String IMAGE_CATEGORY_SESSION_ASSET                         = "session_asset";

  static private final String SHARED_PREFERENCES_NAME_PERMANENT                    = "kite_permanent_shared_prefs";
  static private final String SHARED_PREFERENCES_NAME_APP_SESSION                  = "kite_app_session_shared_prefs";
  static private final String SHARED_PREFERENCES_NAME_CUSTOMER_SESSION             = "kite_customer_session_shared_prefs";


  // Shared preference keys are formed by:
  //   shared preferences key prefix + parameter name + [ shared preferences key suffix ]

  static private final String SHARED_PREFERENCES_KEY_PREFIX_SDK                    = "sdk_";
  static private final String SHARED_PREFERENCES_KEY_PREFIX_APP                    = "app_";

  static private final String PARAMETER_NAME_API_KEY                               = "api_key";
  static private final String PARAMETER_NAME_UNIQUE_USER_ID                        = "unique_user_id";
  static private final String PARAMETER_NAME_ENVIRONMENT_NAME                      = "environment_name";
  static private final String PARAMETER_NAME_API_ENDPOINT                          = "api_endpoint";
  static private final String PARAMETER_NAME_IMAGE_SOURCES                         = "image_sources";

  static private final String PARAMETER_NAME_PAYMENT_ACTIVITY_ENVIRONMENT          = "payment_activity_environment";
  static private final String PARAMETER_NAME_PAYPAL_ENVIRONMENT                    = "paypal_environment";
  static private final String PARAMETER_NAME_PAYPAL_API_ENDPOINT                   = "paypal_api_endpoint";
  static private final String PARAMETER_NAME_PAYPAL_CLIENT_ID                      = "paypay_client_id";
  static private final String PARAMETER_NAME_PAYPAL_ACCOUNT_ID                     = "paypay_account_id";

  static private final String PARAMETER_NAME_STRIPE_PUBLIC_KEY                     = "stripe_public_key";
  static private final String PARAMETER_NAME_STRIPE_ACCOUNT_ID                     = "stripe_account_id";

  static private final String PARAMETER_NAME_LOCKED_CURRENCY_CODE                  = "locked_currency_code";
  static private final String PARAMETER_NAME_PAYPAL_PAYMENTS_AVAILABLE             = "paypal_payments_available";

  static private final String PARAMETER_NAME_INSTAGRAM_CLIENT_ID                   = "instagram_client_id";
  static private final String PARAMETER_NAME_INSTAGRAM_REDIRECT_URI                = "instagram_redirect_uri";

  static private final String PARAMETER_NAME_SDK_CUSTOMISER_CLASS_NAME             = "sdk_customiser_class_name";

  static private final String PARAMETER_NAME_REQUEST_PHONE_NUMBER                  = "request_phone_number";

  static private final String PARAMETER_NAME_END_CUSTOMER_SESSION_ICON_URL         = "end_customer_session_icon_url";

  static private final String PARAMETER_NAME_SHIPPING_ACTIVITY_CLASS_NAME          = "shipping_activity_class_name";
  static private final String PARAMETER_NAME_ADDRESS_BOOK_ENABLED                  = "address_book_enabled";

  static private final String PARAMETER_NAME_INACTIVITY_TIMER_ENABLED              = "inactivity_timer_enabled";

  static private final String PARAMETER_NAME_PAYMENT_FRAGMENT_CLASS_NAME           = "payment_fragment_class_name";

  static private final String SHARED_PREFERENCES_KEY_SUFFIX_RECIPIENT              = "_recipient";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_LINE1                  = "_line1";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_LINE2                  = "_line2";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_CITY                   = "_city";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_STATE_OR_COUNTY        = "_state_or_county";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_ZIP_OR_POSTAL_CODE     = "_zip_or_postal_code";
  static private final String SHARED_PREFERENCES_KEY_SUFFIX_COUNTRY_CODE           = "_country_code";

  static private final String ENVIRONMENT_TEST                                     = "ly.kite.ENVIRONMENT_TEST";
  static private final String ENVIRONMENT_STAGING                                  = "ly.kite.ENVIRONMENT_STAGING";
  static private final String ENVIRONMENT_LIVE                                     = "ly.kite.ENVIRONMENT_LIVE";

  static public  final String PAYPAL_LIVE_API_ENDPOINT                             = "api.paypal.com";
  static public  final String PAYPAL_LIVE_CLIENT_ID                                = "ASYVBBCHF_KwVUstugKy4qvpQaPlUeE_5beKRJHpIP2d3SA_jZrsaUDTmLQY";

  static public  final String PAYPAL_SANDBOX_API_ENDPOINT                          = "api.sandbox.paypal.com";
  static public  final String PAYPAL_SANDBOX_CLIENT_ID                             = "AcEcBRDxqcCKiikjm05FyD4Sfi4pkNP98AYN67sr3_yZdBe23xEk0qhdhZLM";

  static public  final String PAYPAL_PASSWORD                                      = "";

  static public  final String CLASS_NAMES_SEPARATOR                                = ",";

  static public final String INTENT_PREFIX                                         = "ly.kite";

  static public final long   MAX_ACCEPTED_PRODUCT_AGE_MILLIS                       = 1000 * 60 * 60;  // 1 hour

  static public final float  FLOAT_ZERO_THRESHOLD                                  = 0.0001f;

  static public final int    ACTIVITY_REQUEST_CODE_FIRST                           = 10;


  ////////// Static Variable(s) //////////

  private static KiteSDK  sKiteSDK;


  ////////// Member Variable(s) //////////

  private Context         mApplicationContext;
  private String          mAPIKey;
  private Environment     mEnvironment;
  private String          mUniqueUserId;

  private AImageSource[]  mImageSources;

  private SDKCustomiser   mCustomiser;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Clears parameters for a particular scope.
   *
   *****************************************************/
  static public void clearAllParameters( Context context, Scope scope )
    {
    scope.sharedPreferences( context )
      .edit()
        .clear()
      .apply();
    }


  /*****************************************************
   *
   * Validates a parameter name, then generates a key from it.
   *
   *****************************************************/
  static private String getParameterKey( String prefix, String name )
    {
    // Check that the name is populated
    if ( name == null || name.trim().equals( "" ) )
      {
      throw ( new IllegalArgumentException( "Parameter name must be supplied: " + name ) );
      }

    return ( ( prefix != null ? prefix.trim() : "" ) + name );
    }


  /*****************************************************
   *
   * Sets a string parameter.
   *
   *****************************************************/
  static private void setParameter( Context context, Scope scope, String prefix, String name, String value )
    {
    String key = getParameterKey( prefix, name );

    scope.sharedPreferences( context )
            .edit()
            .putString( key, value )
            .apply();
    }


  /*****************************************************
   *
   * Returns a string parameter.
   *
   *****************************************************/
  static private String getStringParameter( Context context, Scope scope, String prefix, String name, String defaultValue )
    {
    String key = getParameterKey( prefix, name );

    return ( scope.sharedPreferences( context ).getString( key, defaultValue ) );
    }


  /*****************************************************
   *
   * Sets a boolean parameter.
   *
   *****************************************************/
  static private void setParameter( Context context, Scope scope, String prefix, String name, boolean value )
    {
    String key = getParameterKey( prefix, name );

    scope.sharedPreferences( context )
      .edit()
        .putBoolean( key, value )
      .apply();
    }


  /*****************************************************
   *
   * Returns a boolean parameter.
   *
   *****************************************************/
  static private boolean getBooleanParameter( Context context, Scope scope, String prefix, String name, boolean defaultValue )
    {
    String key = getParameterKey( prefix, name );

    return ( scope.sharedPreferences( context ).getBoolean( key, defaultValue ) );
    }


  /*****************************************************
   *
   * Sets an address parameter.
   *
   *****************************************************/
  static private void setParameter( Context context, Scope scope, String prefix, String name, Address address )
    {
    String key = getParameterKey( prefix, name );

    scope.sharedPreferences( context )
      .edit()
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_RECIPIENT,          address.getRecipientName() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_LINE1,              address.getLine1() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_LINE2,              address.getLine2() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_CITY,               address.getCity() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_STATE_OR_COUNTY,    address.getStateOrCounty() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_ZIP_OR_POSTAL_CODE, address.getZipOrPostalCode() )
        .putString( key + SHARED_PREFERENCES_KEY_SUFFIX_COUNTRY_CODE,       address.getCountry().iso3Code() )
      .apply();
    }


  /*****************************************************
   *
   * Returns an address parameter.
   *
   *****************************************************/
  static private Address getAddressParameter( Context context, Scope scope, String prefix, String name )
    {
    String key = getParameterKey( prefix, name );

    SharedPreferences sharedPreferences = scope.sharedPreferences( context );

    String  recipient       = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_RECIPIENT, null );
    String  line1           = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_LINE1, null );
    String  line2           = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_LINE2, null );
    String  city            = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_CITY, null );
    String  stateOrCounty   = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_STATE_OR_COUNTY, null );
    String  zipOrPostalCode = sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_ZIP_OR_POSTAL_CODE, null );
    Country country         = Country.getInstance( sharedPreferences.getString( key + SHARED_PREFERENCES_KEY_SUFFIX_COUNTRY_CODE, null ) );

    if ( recipient == null && line1 == null && line2 == null && city == null && stateOrCounty == null && zipOrPostalCode == null && country == null ) return ( null );

    return ( new Address( recipient, line1, line2, city, stateOrCounty, zipOrPostalCode, country ) );
    }


  /*****************************************************
   *
   * Sets a boolean app parameter.
   *
   * Allows an app to use parameters before the SDK
   * is initialised.
   *
   *****************************************************/
  static public void setAppParameter( Context context, Scope scope, String name, boolean booleanValue )
    {
    setParameter( context, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, booleanValue );
    }


  /*****************************************************
   *
   * Sets a string app parameter.
   *
   * Allows an app to use parameters before the SDK
   * is initialised.
   *
   *****************************************************/
  static public void setAppParameter( Context context, Scope scope, String name, String stringValue )
    {
    setParameter( context, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, stringValue );
    }


  /*****************************************************
   *
   * Returns a boolean app parameter.
   *
   * Allows an app to use parameters before the SDK
   * is initialised.
   *
   *****************************************************/
  static public boolean getBooleanAppParameter( Context context, Scope scope, String name, boolean defaultValue )
    {
    return ( getBooleanParameter( context, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns a string app parameter.
   *
   * Allows an app to use parameters before the SDK
   * is initialised.
   *
   *****************************************************/
  static public String getStringAppParameter( Context context, Scope scope, String name, String defaultValue )
    {
    return ( getStringParameter( context, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns an instance of an already-initialised SDK.
   *
   *****************************************************/
  static public KiteSDK getInstance( Context context )
    {
    if ( sKiteSDK == null )
      {
      sKiteSDK = new KiteSDK( context );
      }

    return ( sKiteSDK );
    }


  /*****************************************************
   *
   * Returns a singleton instance of the SDK. Note that
   * if there is already an instance of the SDK, it will
   * be re-initialised with the supplied values.
   *
   *****************************************************/
  static public KiteSDK getInstance( Context context, String apiKey, IEnvironment environment )
    {
    if ( sKiteSDK != null )
      {
      sKiteSDK.setEnvironment( apiKey, environment );
      }
    else
      {
      sKiteSDK = new KiteSDK( context, apiKey, environment );
      }

    return ( sKiteSDK );
    }


  /*****************************************************
   *
   * Initialises the Kite SDK without returning an instance.
   *
   *****************************************************/
  static public void initialise( Context context, String apiKey, IEnvironment environment )
    {
    getInstance( context, apiKey, environment );
    }


  /*****************************************************
   *
   * Convenience method for initialising and Launching the
   * shopping experience for a selected set of products, based
   * on their ids.
   *
   *****************************************************/
  static public KiteSDK startShoppingByProductId( Context context, String apiKey, IEnvironment environment, ArrayList<Asset> assetArrayList, String... productIds )
    {
    KiteSDK kiteSDK = getInstance( context, apiKey, environment );

    kiteSDK.startShoppingByProductId( context, assetArrayList, productIds );

    return ( kiteSDK );
    }


  /*****************************************************
   *
   * Convenience method for initialising and Launching the
   * shopping experience, without any assets.
   *
   *****************************************************/
  static public void startShopping( Context context, String apiKey, IEnvironment environment )
    {
    KiteSDK kiteSDK = getInstance( context, apiKey, environment );

    // Create an empty asset array list
    ArrayList<Asset> assetArrayList = new ArrayList<>( 0 );

    kiteSDK.startShopping( context, assetArrayList );
    }


  ////////// Constructor(s) //////////

  /*****************************************************
   *
   * Creates a new instance of an already initialised SDK.
   *
   *****************************************************/
  private KiteSDK( Context context )
    {
    mApplicationContext = context.getApplicationContext();


    // A permanent API key overrides an app session key

    String apiKey = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_API_KEY, null );

    if ( apiKey == null )
      {
      apiKey = getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_API_KEY, null );
      }

    if ( apiKey == null ) throw ( new IllegalStateException( "Unable to load API key ... have you initialised the SDK?" ) );


    Environment environment = new Environment( this );

    if ( environment == null ) throw ( new IllegalStateException( "Unable to load environment ... have you initialised the SDK?" ) );


    // Set the environment but don't bother saving it out again
    setEnvironment( apiKey, environment, false );


    String imageSourceClassNames = getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_IMAGE_SOURCES, null );

    restoreImageSourcesByClassNames( imageSourceClassNames );
    }


  /*****************************************************
   *
   * Creates and initialises a new instance, using the
   * supplied Kite API key and environment.
   *
   *****************************************************/
  private KiteSDK( Context context, String apiKey, IEnvironment environment )
    {
    mApplicationContext = context.getApplicationContext();

    // Clear all session parameters
    clearAllParameters( Scope.APP_SESSION );
    //clearAllParameters( Scope.CUSTOMER_SESSION );

    setEnvironment( apiKey, environment );

    // Set default image sources
    setImageSources( new DeviceImageSource(), new InstagramImageSource() );

    // Clear any temporary assets
    AssetHelper.clearSessionAssets( context );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets a new API key and environment, optionally
   * persisting them.
   *
   *****************************************************/
  private KiteSDK setEnvironment( String apiKey, IEnvironment environment, boolean save )
    {
    mAPIKey      = apiKey;
    mEnvironment = Environment.getFrom( environment );

    if ( save )
      {
      setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_API_KEY, apiKey );

      mEnvironment.saveTo( this );
      }

    return ( this );
    }


  /*****************************************************
   *
   * Sets a new API key and environment. These details
   * are persisted so that they can be recalled later if
   * this class is garbage collected.
   *
   *****************************************************/
  public KiteSDK setEnvironment( String apiKey, IEnvironment environment )
    {
    return ( setEnvironment( apiKey, environment, true ) );
    }


  /*****************************************************
   *
   * Returns the environment name.
   *
   *****************************************************/
  public String getEnvironmentName()
    {
    return ( mEnvironment.getName() );
    }


  /*****************************************************
   *
   * Ends the customer session.
   *
   *****************************************************/
  public void endCustomerSession()
    {
    clearAllParameters( Scope.CUSTOMER_SESSION );


    // Empty any basket

    OrderingDataAgent basketAgent = OrderingDataAgent.getInstance( mApplicationContext );

    basketAgent.clearDefaultBasket();


    // Go through all the images sources and end any social network log-ins

    for ( AImageSource imageSource : getAvailableImageSources() )
      {
      try
        {
        imageSource.endCustomerSession( mApplicationContext );
        }
      catch ( Exception e )
        {
        Log.e( LOG_TAG, "Unable to end customer session for image source: " + imageSource, e );
        }
      }


    // Empty address book
    Address.deleteAddressBook( mApplicationContext );


    // Clear credit card
    PayPalCard.clearLastUsedCard( mApplicationContext );
    }


  /*****************************************************
   *
   * Clears parameters for a particular scope.
   *
   *****************************************************/
  public KiteSDK clearAllParameters( Scope scope )
    {
    clearAllParameters( mApplicationContext, scope );

    return ( this );
    }


  /*****************************************************
   *
   * Sets an SDK customiser.
   *
   *****************************************************/
  public KiteSDK setCustomiser( Class<? extends SDKCustomiser> customiserClass )
    {
    // Clear any old cached customiser. A new one will get instantiated the first
    // time it is requested.
    mCustomiser = null;

    return ( setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_SDK_CUSTOMISER_CLASS_NAME, customiserClass.getName() ) );
    }


  /*****************************************************
   *
   * Returns the SDK customiser.
   *
   *****************************************************/
  public SDKCustomiser getCustomiser()
    {
    if ( mCustomiser == null )
      {
      String customiserClassName = getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_SDK_CUSTOMISER_CLASS_NAME, null );

      if ( customiserClassName != null )
        {
        try
          {
          Class<? extends SDKCustomiser> customiserClass = (Class<? extends SDKCustomiser>)Class.forName( customiserClassName );

          mCustomiser = customiserClass.newInstance();
          }
        catch ( Exception e )
          {
          Log.e( LOG_TAG, "Unable to instantiate customiser " + customiserClassName, e );
          }
        }

      // If there was no customiser, or we couldn't instantiate one, fall back to the default.
      if ( mCustomiser == null ) mCustomiser = new SDKCustomiser();
      }

    return ( mCustomiser );
    }


  /*****************************************************
   *
   * Sets the Instagram developer credentials. Doing
   * this enables Instagram as an image source
   *
   *****************************************************/
  public KiteSDK setInstagramCredentials( String clientId, String redirectUri )
    {
    setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_INSTAGRAM_CLIENT_ID,    clientId );
    setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_INSTAGRAM_REDIRECT_URI, redirectUri );

    return ( this );
    }


  /*****************************************************
   *
   * Returns true if we have Instagram credentials.
   *
   *****************************************************/
  public boolean haveInstagramCredentials()
    {
    return ( getInstagramClientId() != null && getInstagramRedirectURI() != null );
    }


  /*****************************************************
   *
   * Sets the display of phone number entry field in checkout
   * journey. If false then phone number will not be requested
   *
   *****************************************************/
  public KiteSDK setRequestPhoneNumber( boolean requestPhoneNumber )
    {
    return ( setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_REQUEST_PHONE_NUMBER, requestPhoneNumber ) );
    }


  /*****************************************************
   *
   * Returns the API key.
   *
   *****************************************************/
  public String getAPIKey()
    {
    return ( mAPIKey );
    }


  /*****************************************************
   *
   * Returns a unique id representing the user. This is
   * generated and then persisted.
   *
   *****************************************************/
  public String getUniqueUserId()
    {
    // If we don't have a cached id - see if we previously
    // saved one and load it in. Otherwise generate a new
    // one now and save it.

    if ( mUniqueUserId == null )
      {
      // The scope is permanent, because we don't want it changing per app session
      mUniqueUserId = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_UNIQUE_USER_ID, null );

      if ( mUniqueUserId == null )
        {
        mUniqueUserId = UUID.randomUUID().toString();

        setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_UNIQUE_USER_ID, mUniqueUserId );
        }
      }


    return ( mUniqueUserId );
    }


  /*****************************************************
   *
   * Sets the enabled state of the inactivity timer.
   *
   *****************************************************/
  public KiteSDK setInactivityTimerEnabled( boolean enabled )
    {
    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_INACTIVITY_TIMER_ENABLED, enabled );

    return ( this );
    }



  /*****************************************************
   *
   * Returns the enabled state of the inactivity timer.
   * If the parameter has not been explicitly set, it defaults
   * to false.
   *
   *****************************************************/
  public boolean inactivityTimerIsEnabled()
    {
    return ( getBooleanSDKParameter( Scope.PERMANENT, PARAMETER_NAME_INACTIVITY_TIMER_ENABLED, false ) );
    }


  /*****************************************************
   *
   * Sets the shipping activity.
   *
   *****************************************************/
  public KiteSDK setShippingActivity( Class<? extends AShippingActivity> shippingActivity )
    {
    return ( setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_SHIPPING_ACTIVITY_CLASS_NAME, shippingActivity.getName() ) );
    }


  /*****************************************************
   *
   * Returns the shipping activity.
   *
   *****************************************************/
  public Class<? extends AShippingActivity> getShippingActivityClass()
    {
    String shippingActivityClassName = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_SHIPPING_ACTIVITY_CLASS_NAME, null );

    if ( shippingActivityClassName != null )
      {
      try
        {
        return ( (Class<? extends AShippingActivity>)Class.forName( shippingActivityClassName ) );
        }
      catch ( Exception e )
        {
        Log.e( LOG_TAG, "Unable to get shipping activity " + shippingActivityClassName, e );
        }
      }

    return ( null );
    }


  /*****************************************************
   *
   * Sets the enabled state of the address book.
   *
   *****************************************************/
  public KiteSDK setAddressBookEnabled( boolean enabled )
    {
    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_ADDRESS_BOOK_ENABLED, enabled );

    return ( this );
    }


  /*****************************************************
   *
   * Returns the enabled state of the address book. If the
   * parameter has not been explicitly set, it defaults
   * to true.
   *
   *****************************************************/
  public boolean addressBookIsEnabled()
    {
    return ( getBooleanSDKParameter( Scope.PERMANENT, PARAMETER_NAME_ADDRESS_BOOK_ENABLED, true ) );
    }


  /*****************************************************
   *
   * Sets the payment fragment. The payment fragment displays
   * the payment option buttons and processes them.
   *
   *****************************************************/
  public KiteSDK setPaymentFragment( Class<? extends APaymentFragment> paymentFragment )
    {
    return ( setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYMENT_FRAGMENT_CLASS_NAME, paymentFragment.getName() ) );
    }


  /*****************************************************
   *
   * Returns the payment fragment.
   *
   *****************************************************/
  public APaymentFragment getPaymentFragment()
    {
    String paymentFragmentClassName = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYMENT_FRAGMENT_CLASS_NAME, null );

    if ( paymentFragmentClassName != null )
      {
      try
        {
        Class<?> paymentFragmentClass = Class.forName( paymentFragmentClassName );

        return ( (APaymentFragment)paymentFragmentClass.newInstance() );
        }
      catch ( Exception e )
        {
        Log.e( LOG_TAG, "Unable to get payment fragment " + paymentFragmentClassName, e );
        }
      }

    // If anything goes wrong - return the default payment agent
    return ( new DefaultPaymentFragment() );
    }


  /*****************************************************
   *
   * Returns the instagram client id or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramClientId()
    {
    return ( getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_INSTAGRAM_CLIENT_ID, null ) );
    }


  /*****************************************************
   *
   * Returns the instagram redirect uri or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramRedirectURI()
    {
    return ( getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_INSTAGRAM_REDIRECT_URI, null ) );
    }


  /*****************************************************
   *
   * Returns whether the users phone number should be
   * requested in the checkout journey
   *
   *****************************************************/
  public boolean getRequestPhoneNumber()
    {
    return ( getBooleanSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_REQUEST_PHONE_NUMBER, true ) );
    }


  /*****************************************************
   *
   * Sets image sources.
   *
   *****************************************************/
  private void restoreImageSourcesByClassNames( String classNamesString )
    {
    List<AImageSource> imageSourceList = new ArrayList<>();


    if ( classNamesString != null )
      {
      String[] classNameArray = classNamesString.split( CLASS_NAMES_SEPARATOR );


      // Try to dynamically load each of the image sources

      for ( String className : classNameArray )
        {
        try
          {
          Class<?>       imageSourceClass            = Class.forName( className );
          AImageSource   imageSource                 = (AImageSource)imageSourceClass.newInstance();

          imageSourceList.add( imageSource );
          }
        catch ( Exception e )
          {
          Log.e( LOG_TAG, "Unable to load image source " + className, e );
          }
        }
      }


    // Convert the image source list to an array

    AImageSource[] imageSourceArray = new AImageSource[ imageSourceList.size() ];

    imageSourceList.toArray( imageSourceArray );


    setImageSources( false, imageSourceArray );
    }


  /*****************************************************
   *
   * Sets image sources.
   *
   *****************************************************/
  public KiteSDK setImageSources( AImageSource... imageSources )
    {
    return ( setImageSources( true, imageSources ) );
    }


  /*****************************************************
   *
   * Sets image sources.
   *
   *****************************************************/
  private KiteSDK setImageSources( boolean saveSources, AImageSource... imageSources)
    {
    mImageSources = imageSources;

    DelimitedStringBuilder classNamesStringBuilder = new DelimitedStringBuilder( CLASS_NAMES_SEPARATOR );

    if ( imageSources != null )
      {
      // Iterate through every image source. For each one, set its activity
      // request code, and get its class name.

      String[] classNamesArray = new String[ imageSources.length ];

      int requestCode = ACTIVITY_REQUEST_CODE_FIRST;

      for ( AImageSource imageSource : imageSources )
        {
        imageSource.setActivityRequestCode( requestCode ++ );

        classNamesStringBuilder.append( imageSource.getClass().getName() );
        }
      }


    // Save the class names. This may be an empty list.

    if ( saveSources )
      {
      setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_IMAGE_SOURCES, classNamesStringBuilder.toString() );
      }


    return ( this );
    }


  /*****************************************************
   *
   * Sets the permanent Stripe public key.
   *
   *****************************************************/
  public KiteSDK setPermanentStripeKey( String publicKey, String accountId )
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "setPermanentStripeKey( publicKey = " + publicKey + ", accountId = " + accountId + " )" );

    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_STRIPE_PUBLIC_KEY, publicKey );
    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_STRIPE_ACCOUNT_ID, accountId );

    return ( this );
    }


  /*****************************************************
   *
   * Removes the permanent Stripe client id / account.
   *
   *****************************************************/
  public void removePermanentStripeKey()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "removePermanentStripeKey()" );

    setPermanentStripeKey( null, null );
    }


  /*****************************************************
   *
   * Sets the session Stripe public key.
   *
   *****************************************************/
  public KiteSDK setSessionStripePublicKey( String stripePublicKey )
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "setSessionStripePublicKey( stripePublicKey = " + stripePublicKey + " )" );

    setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_STRIPE_PUBLIC_KEY, stripePublicKey );

    return ( this );
    }


  /*****************************************************
   *
   * Sets the Stripe public key.
   *
   *****************************************************/
  @Deprecated
  public KiteSDK setStripePublicKey( String stripePublicKey )
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "getStripePublicKey( stripePublicKey = " + stripePublicKey + " )" );

    return ( setSessionStripePublicKey( stripePublicKey ) );
    }


  /*****************************************************
   *
   * Returns the Stripe public key.
   *
   * We first check for a permanent key, before falling back
   * to a session one.
   *
   *****************************************************/
  public String getStripePublicKey()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "getStripePublicKey()" );

    String permanentKey = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_STRIPE_PUBLIC_KEY, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  permanent key = " + permanentKey );

    if ( permanentKey != null && ! permanentKey.trim().equals( "" ) )
      {
      return ( permanentKey );
      }

    String sessionKey = getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_STRIPE_PUBLIC_KEY, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  session key = " + sessionKey );

    return ( sessionKey );
    }


  /*****************************************************
   *
   * Returns any Stripe account id.
   *
   *****************************************************/
  public String getStripeAccountId()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "getStripeAccountId()" );

    String accountId = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_STRIPE_ACCOUNT_ID, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  account id = " + accountId );

    return ( accountId );
    }


  /*****************************************************
   *
   * Sets the permanent PayPal client id / account.
   *
   *****************************************************/
  public KiteSDK setPermanentPayPalKey( String clientId, String accountId )
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "setPermanentPayPalKey( clientId = " + clientId + ", accountId = " + accountId + " )" );

    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYPAL_CLIENT_ID,  clientId );
    setSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYPAL_ACCOUNT_ID, accountId );

    return ( this );
    }


  /*****************************************************
   *
   * Removes the permanent PayPal client id / account.
   *
   *****************************************************/
  public void removePermanentPayPalKey()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "removePermanentPayPalKey()" );

    setPermanentPayPalKey( null, null );
    }


  /*****************************************************
   *
   * Returns the PayPal environment.
   *
   *****************************************************/
  public String getPayPalEnvironment()
    {
    return ( mEnvironment.getPayPalEnvironment() );
    }


  /*****************************************************
   *
   * Returns the PayPal API endpoint.
   *
   *****************************************************/
  public String getPayPalAPIEndpoint()
    {
    return ( mEnvironment.getAPIEndpoint() );
    }


  /*****************************************************
   *
   * Returns the PayPal client id.
   *
   * We first check for a permanent key, before falling back
   * to a session one.
   *
   *****************************************************/
  public String getPayPalClientId()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "getPayPalClientId()" );

    String permanentId = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYPAL_CLIENT_ID, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  permanent id = " + permanentId );

    if ( permanentId != null && ! permanentId.trim().equals( "" ) )
      {
      return ( permanentId );
      }

    String sessionId = getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_CLIENT_ID, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  session id = " + sessionId );

    return ( sessionId );
    }


  /*****************************************************
   *
   * Returns any PayPal account id.
   *
   *****************************************************/
  public String getPayPalAccountId()
    {
    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "getPayPalAccountId()" );

    String accountId = getStringSDKParameter( Scope.PERMANENT, PARAMETER_NAME_PAYPAL_ACCOUNT_ID, null );

    if ( DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "  account id = " + accountId );

    return ( accountId );
    }


  /*****************************************************
   *
   * Returns the PayPal auth token.
   *
   *****************************************************/
  public String getPayPalAuthToken()
    {
    return ( Base64.encodeToString( ( getPayPalClientId() + ":" + PAYPAL_PASSWORD ).getBytes(), Base64.NO_WRAP ) );
    }


  /*****************************************************
   *
   * Sets the end customer session icon URL.
   *
   *****************************************************/
  public KiteSDK setEndCustomerSessionIconURL( String endCustomerSessionIconURL )
    {
    setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_END_CUSTOMER_SESSION_ICON_URL, endCustomerSessionIconURL );

    return ( this );
    }


  /*****************************************************
   *
   * Chooses and locks a currency.
   *
   *****************************************************/
  public void chooseAndLockCurrency( Catalogue catalogue )
    {
    // Determine the local currency
    Locale   defaultLocale = Locale.getDefault();
    Currency localCurrency = ( defaultLocale != null ? Currency.getInstance( defaultLocale ) : null );

    // Get the PayPal supported currencies
    List<String> payPalSupportedCurrencyCodeList = catalogue.getPayPalSupportedCurrencyCodes();


    // Get the available prices, by looking at the prices given for one of the products

    Product someProduct = catalogue.getProduct( 0 );

    if ( someProduct != null )
      {
      MultipleCurrencyAmount cost = someProduct.getCost();

      Set<String> availableCurrencyCodeSet = cost.getAllCurrencyCodes();


      // 1. Template response, look at paypal_supported_currencies = …
      // 2. Do I have Paypal or Stripe as CC processor. If Stripe no currency lockdown, if paypal I exclude displaying in a currency that is not supported
      // 3. If Stripe : Display in local currency  , If Paypal : Display in local currency unless not paypal supported then USD / Fallback
      // 4. In payment screen, show Pay by Paypal if currency being displayed is in paypal_supported_currencies


      // Work out what currency we are going to lock to

      String lockedCurrencyCode;

      if ( getCustomiser().getCreditCardAgent().usesPayPal() )
        {
        ///// PayPal /////

        // Pick the best currency
        lockedCurrencyCode = chooseBestCurrency( localCurrency.getCurrencyCode(), MultipleCurrencyAmount.FALLBACK_CURRENCY_CODES, payPalSupportedCurrencyCodeList );

        setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_PAYMENTS_AVAILABLE, true );
        }
      else
        {
        ///// Stripe /////

        lockedCurrencyCode = localCurrency.getCurrencyCode();

        // PayPal payments are only available if the locked currency code is a PayPal supported currency.
        setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_PAYMENTS_AVAILABLE, payPalSupportedCurrencyCodeList.contains( lockedCurrencyCode ) );
        }


      setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_LOCKED_CURRENCY_CODE, lockedCurrencyCode );
      }
    }


  /*****************************************************
   *
   * Picks the best currency that is supported.
   *
   *****************************************************/
  private String chooseBestCurrency( String preferredCurrencyCode, String[] fallbackCurrencyCodes, List<String> supportedCurrencyCodeList )
    {
    if ( preferredCurrencyCode != null )
      {
      if ( supportedCurrencyCodeList.contains( preferredCurrencyCode ) ) return ( preferredCurrencyCode );

      for ( String fallbackCurrencyCode : fallbackCurrencyCodes )
        {
        if ( supportedCurrencyCodeList.contains( fallbackCurrencyCode ) ) return ( fallbackCurrencyCode );
        }
      }

    return ( supportedCurrencyCodeList.get( 0 ) );
    }


  /*****************************************************
   *
   * Returns the locked currency code.
   *
   *****************************************************/
  public String getLockedCurrencyCode()
    {
    return ( getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_LOCKED_CURRENCY_CODE, null ) );
    }


  /*****************************************************
   *
   * Returns true if PayPal payments are available.
   *
   *****************************************************/
  public boolean getPayPalPaymentsAvailable()
    {
    return ( getBooleanSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_PAYMENTS_AVAILABLE, true ) );
    }


  /*****************************************************
   *
   * Returns the end customer session icon URL.
   *
   *****************************************************/
  public String getEndCustomerSessionIconURL()
    {
    return ( getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_END_CUSTOMER_SESSION_ICON_URL, null ) );
    }


  /*****************************************************
   *
   * Sets a string SDK parameter.
   *
   *****************************************************/
  public KiteSDK setSDKParameter( Scope scope, String name, String string )
    {
    setParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_SDK, name, string );

    return ( this );
    }


  /*****************************************************
   *
   * Sets a string app parameter.
   *
   *****************************************************/
  public KiteSDK setAppParameter( Scope scope, String name, String string )
    {
    setParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, string );

    return ( this );
    }


  /*****************************************************
   *
   * Sets a boolean SDK parameter.
   *
   *****************************************************/
  public KiteSDK setSDKParameter( Scope scope, String name, boolean booleanValue )
    {
    setParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_SDK, name, booleanValue );

    return ( this );
    }


  /*****************************************************
   *
   * Sets a boolean app parameter.
   *
   *****************************************************/
  public KiteSDK setAppParameter( Scope scope, String name, boolean booleanValue )
    {
    setParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, booleanValue );

    return ( this );
    }


  /*****************************************************
   *
   * Sets an address app parameter.
   *
   *****************************************************/
  public KiteSDK setAppParameter( Scope scope, String name, Address address )
    {
    setParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, address );

    return ( this );
    }


  /*****************************************************
   *
   * Returns the value of an SDK string parameter.
   *
   *****************************************************/
  public String getStringSDKParameter( Scope scope, String name, String defaultValue )
    {
    return ( getStringParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_SDK, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns the value of an app string parameter.
   *
   *****************************************************/
  public String getStringAppParameter( Scope scope, String name, String defaultValue )
    {
    return ( getStringParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns the value of an SDK boolean parameter.
   *
   *****************************************************/
  public boolean getBooleanSDKParameter( Scope scope, String name, boolean defaultValue )
    {
    return ( getBooleanParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_SDK, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns the value of an app boolean parameter.
   *
   *****************************************************/
  public boolean getBooleanAppParameter( Scope scope, String name, boolean defaultValue )
    {
    return ( getBooleanParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns the value of a custom address parameter.
   *
   *****************************************************/
  public Address getAddressAppParameter( Scope scope, String name )
    {
    return ( getAddressParameter( mApplicationContext, scope, SHARED_PREFERENCES_KEY_PREFIX_APP, name ) );
    }


  /*****************************************************
   *
   * Returns an instance of the image agent.
   *
   *****************************************************/
  public ImageAgent getImageAgent()
    {
    return ( ImageAgent.getInstance( mApplicationContext ) );
    }


  /*****************************************************
   *
   * Returns an instance of the catalogue loader.
   *
   *****************************************************/
  public CatalogueLoader getCatalogueLoader()
    {
    return ( CatalogueLoader.getInstance( mApplicationContext ) );
    }


  /*****************************************************
   *
   * Launches the shopping experience for all products.
   *
   *****************************************************/
  public void startShopping( Context context, ArrayList<Asset> assetArrayList )
    {
    startShoppingByProductId( context, assetArrayList );
    }


  /*****************************************************
   *
   * Launches the shopping experience for selected products.
   * We have used a different method name because we may
   * wish to filter by something else in the future.
   *
   *****************************************************/
  public void startShoppingByProductId( Context context, ArrayList<Asset> assetArrayList, String... productIds )
    {
    // Clear any temporary assets
    AssetHelper.clearSessionAssets( context );

    // Make sure all the assets are parcelable (this may create some new cached
    // assets). Note that from here on in the SDK is responsible for ensuring
    // that all new assets are parcelable.
    assetArrayList = AssetHelper.toParcelableList( context, assetArrayList );

    // We use the activity context here, not the application context
    ProductSelectionActivity.start( context, assetArrayList, productIds );
    }


  /*****************************************************
   *
   * Launches managed checkout.
   *
   *****************************************************/
  public void startCheckout( Context context, Order order )
    {
    BasketActivity.start( context, order );
    }


  /*****************************************************
   *
   * Launches managed checkout, and returns the result.
   *
   *****************************************************/
  public void startCheckoutForResult( Activity activity, Order order, int requestCode )
    {
    BasketActivity.startForResult( activity, order, requestCode );
    }


  /*****************************************************
   *
   * Launches managed checkout, and returns the result.
   *
   *****************************************************/
  @Deprecated
  public void startCheckout( Activity activity, Order order, int requestCode )
    {
    startCheckoutForResult( activity, order, requestCode );
    }


  /*****************************************************
   *
   * Launches the payment screen, and returns the result.
   *
   *****************************************************/
  public void startPaymentForResult( Activity activity, Order order, ArrayList<String> payPalSupportedCurrencyCodes, int requestCode )
    {
    PaymentActivity.startForResult( activity, order, payPalSupportedCurrencyCodes, requestCode );
    }


  /*****************************************************
   *
   * Launches the payment screen, and returns the result.
   *
   *****************************************************/
  public void startPaymentForResult( Activity activity, Order order, int requestCode )
    {
    PaymentActivity.startForResult( activity, order, requestCode );
    }


  /*****************************************************
   *
   * Returns the print API endpoint.
   *
   *****************************************************/
  public String getAPIEndpoint()
    {
    return ( mEnvironment.getAPIEndpoint() );
    }


  /*****************************************************
   *
   * Returns a list of available image sources.
   *
   *****************************************************/
  public ArrayList<AImageSource> getAvailableImageSources()
    {
    ArrayList<AImageSource> imageSourceList = new ArrayList<>();

    if ( mImageSources != null )
      {
      for ( AImageSource imageSource : mImageSources )
        {
        if ( imageSource.isAvailable( mApplicationContext ) ) imageSourceList.add( imageSource );
        }
      }

    return ( imageSourceList );
    }


  /*****************************************************
   *
   * Returns the image source that has the supplied menu
   * item id.
   *
   *****************************************************/
  public AImageSource getImageSourceByMenuItemId( int itemId )
    {
    if ( mImageSources != null )
      {
      for ( AImageSource candidateImageSource : mImageSources )
        {
        if ( candidateImageSource.getMenuItemId() == itemId ) return ( candidateImageSource );
        }
      }

    return ( null );
    }


  /*****************************************************
   *
   * Interprets an activity result, and returns any assets.
   *
   *****************************************************/
  public void getAssetsFromPickerResult( Activity activity, int requestCode, int resultCode, Intent data, AImageSource.IAssetConsumer assetConsumer )
    {
    if ( resultCode == Activity.RESULT_OK )
      {
      // Go through the image sources, and find the one with the matching request code

      if ( mImageSources != null )
        {
        for ( AImageSource imageSource : mImageSources )
          {
          if ( imageSource.getActivityRequestCode() == requestCode )
            {
            imageSource.getAssetsFromPickerResult( activity, data, assetConsumer );

            return;
            }
          }
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The common interface for all environments.
   *
   *****************************************************/
  public interface IEnvironment
    {
    public String getName();
    public String getAPIEndpoint();
    public String getPaymentActivityEnvironment();
    public String getPayPalEnvironment();
    public String getPayPalAPIEndpoint();
    public String getPayPalClientId();
    }


  /*****************************************************
   *
   * A general environment.
   *
   *****************************************************/
  public static class Environment implements IEnvironment, Parcelable
    {
    private final String  mName;
    private final String  mAPIEndpoint;
    private final String  mPaymentActivityEnvironment;
    private final String  mPayPalEnvironment;
    private final String  mPayPalAPIEndpoint;
    private final String  mPayPalClientId;


    public static final Parcelable.Creator<Environment> CREATOR =
      new Parcelable.Creator<Environment>()
        {
        public Environment createFromParcel( Parcel sourceParcel )
          {
          return ( new Environment( sourceParcel ) );
          }

        public Environment[] newArray( int size )
          {
          return (new Environment[ size ]);
          }
        };


    static Environment getFrom( IEnvironment sourceEnvironment )
      {
      // If the source environment is already an instance of this class - return it unmodified
      if ( sourceEnvironment instanceof Environment ) return ( (Environment)sourceEnvironment );

      return ( new Environment( sourceEnvironment ) );
      }


    Environment( String name, String apiEndpoint, String paymentActivityEnvironment, String payPalEnvironment, String payPalAPIEndpoint, String payPalClientId )
      {
      mName                       = name;
      mAPIEndpoint                = apiEndpoint;
      mPaymentActivityEnvironment = paymentActivityEnvironment;
      mPayPalEnvironment          = payPalEnvironment;
      mPayPalAPIEndpoint          = payPalAPIEndpoint;
      mPayPalClientId             = payPalClientId;
      }


    public Environment( IEnvironment templateEnvironment, String payPalClientId )
      {
      mName                       = templateEnvironment.getName();
      mAPIEndpoint                = templateEnvironment.getAPIEndpoint();
      mPaymentActivityEnvironment = templateEnvironment.getPaymentActivityEnvironment();
      mPayPalEnvironment          = templateEnvironment.getPayPalEnvironment();
      mPayPalAPIEndpoint          = templateEnvironment.getPayPalAPIEndpoint();
      mPayPalClientId             = payPalClientId;
      }


    public Environment( IEnvironment templateEnvironment )
      {
      mName                       = templateEnvironment.getName();
      mAPIEndpoint                = templateEnvironment.getAPIEndpoint();
      mPaymentActivityEnvironment = templateEnvironment.getPaymentActivityEnvironment();
      mPayPalEnvironment          = templateEnvironment.getPayPalEnvironment();
      mPayPalAPIEndpoint          = templateEnvironment.getPayPalAPIEndpoint();
      mPayPalClientId             = templateEnvironment.getPayPalClientId();
      }


    Environment( KiteSDK kiteSDK )
      {
      mName                       = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_ENVIRONMENT_NAME,             null );
      mAPIEndpoint                = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_API_ENDPOINT,                 null );
      mPaymentActivityEnvironment = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYMENT_ACTIVITY_ENVIRONMENT, null );
      mPayPalEnvironment          = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_ENVIRONMENT,           null );
      mPayPalAPIEndpoint          = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_API_ENDPOINT,          null );
      mPayPalClientId             = kiteSDK.getStringSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_CLIENT_ID,             null );
      }


    Environment( Parcel parcel )
      {
      mName                       = parcel.readString();
      mAPIEndpoint                = parcel.readString();
      mPaymentActivityEnvironment = parcel.readString();
      mPayPalEnvironment          = parcel.readString();
      mPayPalAPIEndpoint          = parcel.readString();
      mPayPalClientId             = parcel.readString();
      }


    public int describeContents()
      {
      return ( 0 );
      }

    public void writeToParcel( Parcel parcel, int flags )
      {
      parcel.writeString( mName );
      parcel.writeString( mAPIEndpoint );
      parcel.writeString( mPaymentActivityEnvironment );
      parcel.writeString( mPayPalEnvironment );
      parcel.writeString( mPayPalAPIEndpoint );
      parcel.writeString( mPayPalClientId );
      }


    public String getName()
      {
      return ( mName );
      }

    public String getAPIEndpoint()
      {
      return ( mAPIEndpoint );
      }

    public String getPaymentActivityEnvironment()
      {
      return ( mPaymentActivityEnvironment );
      }

    public String getPayPalEnvironment()
      {
      return ( mPayPalEnvironment );
      }

    public String getPayPalAPIEndpoint()
      {
      return ( mPayPalAPIEndpoint );
      }

    public String getPayPalClientId()
      {
      return ( mPayPalClientId );
      }

    void saveTo( KiteSDK kiteSDK )
      {
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_ENVIRONMENT_NAME,             mName );
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_API_ENDPOINT,                 mAPIEndpoint );
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYMENT_ACTIVITY_ENVIRONMENT, mPaymentActivityEnvironment );
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_ENVIRONMENT,           mPayPalEnvironment );
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_API_ENDPOINT,          mPayPalAPIEndpoint );
      kiteSDK.setSDKParameter( Scope.APP_SESSION, PARAMETER_NAME_PAYPAL_CLIENT_ID,             mPayPalClientId );
      }

    }


  /*****************************************************
   *
   * There are a number of different scopes for things like
   * parameters:
   *   - Permanent
   *   - App session: until the SDK is re-initialised
   *   - Customer session: for apps that can be used by
   *     multiple customers, until the next customer.
   *
   *****************************************************/
  public enum Scope
    {
    PERMANENT         ( SHARED_PREFERENCES_NAME_PERMANENT        ),
    APP_SESSION       ( SHARED_PREFERENCES_NAME_APP_SESSION      ),
    CUSTOMER_SESSION  ( SHARED_PREFERENCES_NAME_CUSTOMER_SESSION );


    private String  mSharedPreferencesName;


    private Scope( String sharedPreferencesName )
      {
      mSharedPreferencesName = sharedPreferencesName;
      }


    String sharedPreferencesName()
      {
      return ( mSharedPreferencesName );
      }


    SharedPreferences sharedPreferences( Context context )
      {
      return( context.getSharedPreferences( mSharedPreferencesName, Context.MODE_PRIVATE ) );
      }
    }


  /*****************************************************
   *
   * A set of pre-defined environments.
   *
   *****************************************************/
  public static enum DefaultEnvironment implements IEnvironment
    {
    LIVE    ( "Live",    "https://api.kite.ly/v3.0",     ENVIRONMENT_LIVE,    PayPalConfiguration.ENVIRONMENT_PRODUCTION, PAYPAL_LIVE_API_ENDPOINT,    PAYPAL_LIVE_CLIENT_ID    ),
    TEST    ( "Test",    "https://api.kite.ly/v3.0",     ENVIRONMENT_TEST,    PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_SANDBOX_API_ENDPOINT, PAYPAL_SANDBOX_CLIENT_ID ),
    STAGING ( "Staging", "https://staging.kite.ly/v3.0", ENVIRONMENT_STAGING, PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_SANDBOX_API_ENDPOINT, PAYPAL_SANDBOX_CLIENT_ID ); /* private environment intended only for Ocean Labs use, hands off :) */


    private Environment  mEnvironment;


    private DefaultEnvironment( String name, String apiEndpoint, String paymentActivityEnvironment, String payPalEnvironment, String payPalAPIEndpoint, String payPalClientId )
      {
      mEnvironment = new Environment( name, apiEndpoint, paymentActivityEnvironment, payPalEnvironment, payPalAPIEndpoint, payPalClientId );
      }


    public String getName()
      {
      return ( mEnvironment.getName() );
      }

    public String getAPIEndpoint()
      {
      return ( mEnvironment.getAPIEndpoint() );
      }

    public String getPaymentActivityEnvironment()
      {
      return ( mEnvironment.getPaymentActivityEnvironment() );
      }

    public String getPayPalEnvironment()
      {
      return ( mEnvironment.getPayPalEnvironment() );
      }

    public String getPayPalAPIEndpoint()
      {
      return ( mEnvironment.getPayPalAPIEndpoint() );
      }

    public String getPayPalClientId()
      {
      return ( mEnvironment.getPayPalClientId() );
      }

    }

  }
