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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import ly.kite.checkout.PaymentActivity;
import ly.kite.catalogue.Asset;
import ly.kite.journey.AImageSource;
import ly.kite.journey.DeviceImageSource;
import ly.kite.journey.InstagramImageSource;
import ly.kite.journey.selection.ProductSelectionActivity;
import ly.kite.catalogue.AssetHelper;
import ly.kite.util.ImageAgent;


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

  static private final String LOG_TAG                                             = "KiteSDK";

  static public  final String SDK_VERSION                                         = "3.0.0";

  static private final String SHARED_PREFERENCES_NAME                             = "kite_shared_prefs";

  static private final String SHARED_PREFERENCES_KEY_API_KEY                      = "api_key";
  static private final String SHARED_PREFERENCES_KEY_UNIQUE_USER_ID               = "unique_user_id";
  static private final String SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME             = "environment_name";
  static private final String SHARED_PREFERENCES_KEY_API_ENDPOINT                 = "api_endpoint";
  static private final String SHARED_PREFERENCES_KEY_IMAGE_SOURCES                = "image_sources";

  static private final String SHARED_PREFERENCES_KEY_PAYMENT_ACTIVITY_ENVIRONMENT = "payment_activity_environment";
  static private final String SHARED_PREFERENCES_KEY_PAYPAL_ENVIRONMENT           = "paypal_environment";
  static private final String SHARED_PREFERENCES_KEY_PAYPAL_API_ENDPOINT          = "paypal_api_endpoint";
  static private final String SHARED_PREFERENCES_KEY_PAYPAL_CLIENT_ID             = "paypay_client_id";
  static private final String SHARED_PREFERENCES_KEY_PAYPAL_PASSWORD              = "paypal_password";

  static private final String SHARED_PREFERENCES_KEY_INSTAGRAM_CLIENT_ID          = "instagram_client_id";
  static private final String SHARED_PREFERENCES_KEY_INSTAGRAM_REDIRECT_URI       = "instagram_redirect_uri";

  static private final String SHARED_PREFERENCES_KEY_REQUEST_PHONE_NUMBER         = "request_phone_number";

  static private final String SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX      = "custom_parameter_";

  static public  final String PAYPAL_LIVE_API_ENDPOINT                            = "api.paypal.com";
  static public  final String PAYPAL_LIVE_CLIENT_ID                               = "ASYVBBCHF_KwVUstugKy4qvpQaPlUeE_5beKRJHpIP2d3SA_jZrsaUDTmLQY";
  static public  final String PAYPAL_LIVE_PASSWORD                                = "";

  static public  final String PAYPAL_SANDBOX_API_ENDPOINT                         = "api.sandbox.paypal.com";
  static public  final String PAYPAL_SANDBOX_CLIENT_ID                            = "AcEcBRDxqcCKiikjm05FyD4Sfi4pkNP98AYN67sr3_yZdBe23xEk0qhdhZLM";
  static public  final String PAYPAL_SANDBOX_PASSWORD                             = "";


  static public final String INTENT_PREFIX                                        = "ly.kite";

  static public final long   MAX_ACCEPTED_PRODUCT_AGE_MILLIS                      = 1000 * 60 * 60;  // 1 hour

  static public final float  FLOAT_ZERO_THRESHOLD                                 = 0.0001f;

  static public final int    ACTIVITY_REQUEST_CODE_FIRST                          = 10;



  ////////// Static Variable(s) //////////

  private static KiteSDK  sKiteSDK;


  ////////// Member Variable(s) //////////

  private Context         mApplicationContext;
  private String          mAPIKey;
  private Environment     mEnvironment;
  private String          mUniqueUserId;

  private AImageSource[]  mImageSources;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the SDK, assuming it has
   * already been initialised.
   *
   *****************************************************/
  static public KiteSDK getInstance( Context context )
    {
    if ( sKiteSDK == null )
      {
      sKiteSDK = new KiteSDK( context, context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ) );
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
   * Creates a new instance by reading in previously persisted
   * values.
   *
   *****************************************************/
  private KiteSDK( Context context )
    {
    mApplicationContext = context.getApplicationContext();
    }


  /*****************************************************
   *
   * Creates a new instance from shared preferences.
   *
   *****************************************************/
  private KiteSDK( Context context, SharedPreferences sharedPreferences )
    {
    this( context );


    String apiKey = sharedPreferences.getString( SHARED_PREFERENCES_KEY_API_KEY, null );

    if ( apiKey == null ) throw ( new IllegalStateException( "Unable to load API key ... have you initialised the SDK?" ) );


    Environment environment = new Environment( sharedPreferences );

    if ( environment == null ) throw ( new IllegalStateException( "Unable to load environment ... have you initialised the SDK?" ) );


    setEnvironment( apiKey, environment );

    setImageSourcesByClassName( sharedPreferences.getStringSet( SHARED_PREFERENCES_KEY_IMAGE_SOURCES, null ) );
    }


  /*****************************************************
   *
   * Creates a new instance with the supplied Kite API key
   * and environment.
   *
   *****************************************************/
  private KiteSDK( Context context, String apiKey, IEnvironment environment )
    {
    this( context );

    setEnvironment( apiKey, environment );

    clearInstagramCredentials();

    // Set default image sources
    setImageSources( new DeviceImageSource(), new InstagramImageSource() );

    // Clear any temporary assets
    AssetHelper.clearCachedImages( context );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets a new API key and environment. These details
   * are persisted so that they can be recalled later if
   * this class is garbage collected.
   *
   *****************************************************/
  public KiteSDK setEnvironment( String apiKey, IEnvironment environment )
    {
    mAPIKey      = apiKey;
    mEnvironment = Environment.getFrom( environment );

    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor.putString( SHARED_PREFERENCES_KEY_API_KEY, apiKey );

    environment.writeTo( editor );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to save current environment to shared preferences" );
      }

    return ( sKiteSDK );
    }


  /*****************************************************
   *
   * Sets the Instagram developer credentials. Doing
   * this enables Instagram as an image source
   *
   *****************************************************/
  public KiteSDK setInstagramCredentials( String clientId, String redirectUri )
    {
    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor
      .putString( SHARED_PREFERENCES_KEY_INSTAGRAM_CLIENT_ID, clientId )
      .putString( SHARED_PREFERENCES_KEY_INSTAGRAM_REDIRECT_URI, redirectUri );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to save instagram credentials to shared preferences" );
      }

    return ( this );
    }


  /*****************************************************
   *
   * Clears the Instagram developer credentials.
   *
   *****************************************************/
  public KiteSDK clearInstagramCredentials()
    {
    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor
      .remove( SHARED_PREFERENCES_KEY_INSTAGRAM_CLIENT_ID )
      .remove( SHARED_PREFERENCES_KEY_INSTAGRAM_REDIRECT_URI );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to clear Instagram credentials from shared preferences" );
      }

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
    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor.putBoolean( SHARED_PREFERENCES_KEY_REQUEST_PHONE_NUMBER, requestPhoneNumber );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to save request phone number preference to shared preferences" );
      }

    return ( this );
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
   * Returns the environment.
   *
   *****************************************************/
  public Environment getEnvironment()
    {
    return ( mEnvironment );
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
      SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

      String uniqueUserId = sharedPreferences.getString( SHARED_PREFERENCES_KEY_UNIQUE_USER_ID, null );

      if ( uniqueUserId == null )
        {
        uniqueUserId = UUID.randomUUID().toString();

        sharedPreferences
          .edit()
            .putString( SHARED_PREFERENCES_KEY_UNIQUE_USER_ID, uniqueUserId )
          .commit();
        }

      mUniqueUserId = uniqueUserId;
      }

    return ( mUniqueUserId );
    }


  /*****************************************************
   *
   * Returns the instagram client id or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramClientId()
    {
    SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

    return ( sharedPreferences.getString( SHARED_PREFERENCES_KEY_INSTAGRAM_CLIENT_ID, null ) );
    }


  /*****************************************************
   *
   * Returns the instagram redirect uri or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramRedirectURI()
    {
    SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

    return ( sharedPreferences.getString( SHARED_PREFERENCES_KEY_INSTAGRAM_REDIRECT_URI, null ) );
    }


  /*****************************************************
   *
   * Returns whether the users phone number should be
   * requested in the checkout journey
   *
   *****************************************************/
  public boolean getRequestPhoneNumber()
    {
    SharedPreferences prefs = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

    return ( prefs.getBoolean( SHARED_PREFERENCES_KEY_REQUEST_PHONE_NUMBER, true ) );
    }


  /*****************************************************
   *
   * Sets image sources.
   *
   *****************************************************/
  public KiteSDK setImageSourcesByClassName( Set<String> classNameSet )
    {
    List<AImageSource> imageSourceList = new ArrayList<>();

    if ( classNameSet != null )
      {
      // Try to dynamically load each of the image sources

      for ( String className : classNameSet )
        {
        try
          {
          Class<?>       imageSourceClass            = Class.forName( className );
          Constructor<?> imageSourceClassConstructor = imageSourceClass.getConstructor();
          AImageSource   imageSource                 = (AImageSource)imageSourceClassConstructor.newInstance();

          imageSourceList.add( imageSource );
          }
        catch ( Exception e )
          {
          Log.e( LOG_TAG, "Unable to load image source " + className, e );
          }
        }
      }


    // Convert the list to an array

    AImageSource[] imageSources = new AImageSource[ imageSourceList.size() ];

    imageSourceList.toArray( imageSources );


    return ( setImageSources( imageSources ) );
    }


  /*****************************************************
   *
   * Sets image sources.
   *
   *****************************************************/
  public KiteSDK setImageSources( AImageSource... imageSources )
    {
    mImageSources = imageSources;


    HashSet<String> classNameSet = new HashSet<>();

    if ( imageSources != null )
      {
      // Iterate through every image source. For each one, set its activity
      // request code, and add its class name to a set.

      int requestCode = ACTIVITY_REQUEST_CODE_FIRST;

      for ( AImageSource imageSource : imageSources )
        {
        imageSource.setActivityRequestCode( requestCode ++ );

        classNameSet.add( imageSource.getClass().getName() );
        }
      }


    // Save the class names. This may be an empty set.

    mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE )
            .edit()
            .putStringSet( SHARED_PREFERENCES_KEY_IMAGE_SOURCES, classNameSet )
      .apply();


    return ( this );
    }


  /*****************************************************
   *
   * Sets a custom parameter.
   *
   *****************************************************/
  public KiteSDK setCustomString( String name, String value )
    {
    // Check that the parameter name doesn't start with the custom prefix

    if ( name.startsWith( SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX ) )
      {
      throw ( new IllegalArgumentException( "Custom parameter names must not start with " + SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX ) );
      }


    mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE )
      .edit()
        .putString( SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX + name, value )
      .apply();

    return ( this );
    }


  /*****************************************************
   *
   * Returns the value of a custom parameter.
   *
   *****************************************************/
  public String getCustomString( String name, String defaultValue )
    {
    // Check that the parameter name doesn't start with the custom prefix

    if ( name.startsWith( SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX ) )
      {
      throw ( new IllegalArgumentException( "Custom parameter names must not start with " + SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX ) );
      }


    return ( mApplicationContext
               .getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE )
               .getString( SHARED_PREFERENCES_CUSTOM_PARAMETER_KEY_PREFIX + name, defaultValue ) );
    }


  /*****************************************************
   *
   * Returns an instance of the image loader.
   *
   *****************************************************/
  public ImageAgent getImageAgent( Context context )
    {
    return ( ImageAgent.getInstance( context ) );
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
    AssetHelper.clearCachedImages( context );

    // Make sure all the assets are parcelable (this may create some new cached
    // assets). Note that from here on in the SDK is responsible for ensuring
    // that all new assets are parcelable.
    assetArrayList = AssetHelper.toParcelableList( context, assetArrayList );

    // We use the activity context here, not the application context
    ProductSelectionActivity.start( context, assetArrayList, productIds );
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
    public String getPayPalPassword();

    public void writeTo( SharedPreferences.Editor editor );
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
    private final String  mPayPalPassword;


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


    Environment( String name, String apiEndpoint, String paymentActivityEnvironment, String payPalEnvironment, String payPalAPIEndpoint, String payPalClientId, String payPalPassword )
      {
      mName                       = name;
      mAPIEndpoint                = apiEndpoint;
      mPaymentActivityEnvironment = paymentActivityEnvironment;
      mPayPalEnvironment          = payPalEnvironment;
      mPayPalAPIEndpoint          = payPalAPIEndpoint;
      mPayPalClientId             = payPalClientId;
      mPayPalPassword             = payPalPassword;
      }


    public Environment( IEnvironment templateEnvironment, String payPalClientId )
      {
      mName                       = templateEnvironment.getName();
      mAPIEndpoint                = templateEnvironment.getAPIEndpoint();
      mPaymentActivityEnvironment = templateEnvironment.getPaymentActivityEnvironment();
      mPayPalEnvironment          = templateEnvironment.getPayPalEnvironment();
      mPayPalAPIEndpoint          = templateEnvironment.getPayPalAPIEndpoint();
      mPayPalClientId             = payPalClientId;
      mPayPalPassword             = templateEnvironment.getPayPalPassword();
      }


    public Environment( IEnvironment templateEnvironment )
      {
      mName                       = templateEnvironment.getName();
      mAPIEndpoint                = templateEnvironment.getAPIEndpoint();
      mPaymentActivityEnvironment = templateEnvironment.getPaymentActivityEnvironment();
      mPayPalEnvironment          = templateEnvironment.getPayPalEnvironment();
      mPayPalAPIEndpoint          = templateEnvironment.getPayPalAPIEndpoint();
      mPayPalClientId             = templateEnvironment.getPayPalClientId();
      mPayPalPassword             = templateEnvironment.getPayPalPassword();
      }


    Environment( SharedPreferences sharedPreferences )
      {
      mName                       = sharedPreferences.getString( SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME, null );
      mAPIEndpoint                = sharedPreferences.getString( SHARED_PREFERENCES_KEY_API_ENDPOINT, null );
      mPaymentActivityEnvironment = sharedPreferences.getString( SHARED_PREFERENCES_KEY_PAYMENT_ACTIVITY_ENVIRONMENT, null );
      mPayPalEnvironment          = sharedPreferences.getString( SHARED_PREFERENCES_KEY_PAYPAL_ENVIRONMENT, null );
      mPayPalAPIEndpoint          = sharedPreferences.getString( SHARED_PREFERENCES_KEY_PAYPAL_API_ENDPOINT, null );
      mPayPalClientId             = sharedPreferences.getString( SHARED_PREFERENCES_KEY_PAYPAL_CLIENT_ID, null );
      mPayPalPassword             = sharedPreferences.getString( SHARED_PREFERENCES_KEY_PAYPAL_PASSWORD, null );
      }


    Environment( Parcel parcel )
      {
      mName                       = parcel.readString();
      mAPIEndpoint                = parcel.readString();
      mPaymentActivityEnvironment = parcel.readString();
      mPayPalEnvironment          = parcel.readString();
      mPayPalAPIEndpoint          = parcel.readString();
      mPayPalClientId             = parcel.readString();
      mPayPalPassword            = parcel.readString();
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
      parcel.writeString( mPayPalPassword );
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

    public String getPayPalPassword()
      {
      return ( mPayPalPassword );
      }

    public void writeTo( SharedPreferences.Editor editor )
      {
      editor.putString( SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME, mName );
      editor.putString( SHARED_PREFERENCES_KEY_API_ENDPOINT, mAPIEndpoint );
      editor.putString( SHARED_PREFERENCES_KEY_PAYMENT_ACTIVITY_ENVIRONMENT, mPaymentActivityEnvironment );
      editor.putString( SHARED_PREFERENCES_KEY_PAYPAL_ENVIRONMENT, mPayPalEnvironment );
      editor.putString( SHARED_PREFERENCES_KEY_PAYPAL_API_ENDPOINT, mPayPalAPIEndpoint );
      editor.putString( SHARED_PREFERENCES_KEY_PAYPAL_CLIENT_ID, mPayPalClientId );
      editor.putString( SHARED_PREFERENCES_KEY_PAYPAL_PASSWORD, mPayPalPassword );
      }


    public String getPayPalAuthToken()
      {
      return ( Base64.encodeToString( ( mPayPalClientId + ":" + mPayPalPassword ).getBytes(), Base64.NO_WRAP ) );
      }
    }


  /*****************************************************
   *
   * A set of pre-defined environments.
   *
   *****************************************************/
  public static enum DefaultEnvironment implements IEnvironment
    {
    LIVE    ( "Live",    "https://api.kite.ly/v1.4",   PaymentActivity.ENVIRONMENT_LIVE,    PayPalConfiguration.ENVIRONMENT_PRODUCTION, PAYPAL_LIVE_API_ENDPOINT,    PAYPAL_LIVE_CLIENT_ID,    PAYPAL_LIVE_PASSWORD    ),
    TEST    ( "Test",    "https://api.kite.ly/v1.4",   PaymentActivity.ENVIRONMENT_TEST,    PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_SANDBOX_API_ENDPOINT, PAYPAL_SANDBOX_CLIENT_ID, PAYPAL_SANDBOX_PASSWORD ),
    STAGING ( "Staging", "http://staging.api.kite.ly", PaymentActivity.ENVIRONMENT_STAGING, PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_SANDBOX_API_ENDPOINT, PAYPAL_SANDBOX_CLIENT_ID, PAYPAL_SANDBOX_PASSWORD ); /* private environment intended only for Ocean Labs use, hands off :) */


    private Environment  mEnvironment;


    private DefaultEnvironment( String name, String apiEndpoint, String paymentActivityEnvironment, String payPalEnvironment, String payPalAPIEndpoint, String payPalClientId, String payPalPassword )
      {
      mEnvironment = new Environment( name, apiEndpoint, paymentActivityEnvironment, payPalEnvironment, payPalAPIEndpoint, payPalClientId, payPalPassword );
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

    public String getPayPalPassword()
      {
      return ( mEnvironment.getPayPalPassword() );
      }


    public void writeTo( SharedPreferences.Editor editor )
      {
      mEnvironment.writeTo( editor );
      }
    }

  }
