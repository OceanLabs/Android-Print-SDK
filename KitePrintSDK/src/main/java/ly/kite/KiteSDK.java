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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import ly.kite.product.Asset;
import ly.kite.journey.ProductSelectionActivity;
import ly.kite.product.AssetHelper;


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

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "KiteSDK";

  public  static final String SDK_VERSION                               = "2.0";

  private static final String SHARED_PREFERENCES_NAME                   = "kite_shared_prefs";
  private static final String SHARED_PREFERENCES_KEY_API_KEY            = "api_key";
  private static final String SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME   = "environment_name";
  private static final String SHARED_PREFERENCES_INSTAGRAM_CLIENT_ID    = "instagram_client_id";
  private static final String SHARED_PREFERENCES_INSTAGRAM_REDIRECT_URI = "instagram_redirect_uri";


  // Old stuff

  //private static final String PAYPAL_CLIENT_ID_SANDBOX = "Aa5nsBDntBpozWQykoxQXoHFOqs551hTNt0B8LQXTudoh8bD0nT1F735c_Fh";
  //private static final String PAYPAL_RECIPIENT_SANDBOX = "hello-facilitator@psilov.eu";

  //private static final String PAYPAL_CLIENT_ID_LIVE = "AT2JfBAmXD-CHGJnUb05ik4J-GrCi4XxjY9_grfCFjreYaLrNswj8uzhuWyj";
  //private static final String PAYPAL_RECIPIENT_LIVE = "deon@oceanlabs.co";


  public static final String PAYPAL_CLIENT_ID_SANDBOX = "AcEcBRDxqcCKiikjm05FyD4Sfi4pkNP98AYN67sr3_yZdBe23xEk0qhdhZLM";
  public static final String PAYPAL_RECIPIENT_SANDBOX = "sandbox-merchant@kite.ly";

  public static final String PAYPAL_CLIENT_ID_LIVE    = "ASYVBBCHF_KwVUstugKy4qvpQaPlUeE_5beKRJHpIP2d3SA_jZrsaUDTmLQY";
  public static final String PAYPAL_RECIPIENT_LIVE    = "hello@kite.ly";


  public static final String INTENT_PREFIX            = "ly.kite";


  ////////// Static Variable(s) //////////

  private static KiteSDK  sKiteSDK;


  ////////// Member Variable(s) //////////

  private Context      mApplicationContext;
  private String       mAPIKey;
  private Environment  mEnvironment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the SDK, assuming it has
   * already been initialised.
   *
   *****************************************************/
  public static KiteSDK getInstance( Context context )
    {
    if ( sKiteSDK == null )
      {
      // We need to create an instance, but we have only been
      // given a context, so we need to try and load a previously
      // saved environment.

      SharedPreferences sharedPreferences = context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );


      String apiKey = sharedPreferences.getString( SHARED_PREFERENCES_KEY_API_KEY, null );

      if ( apiKey == null ) throw ( new IllegalStateException( "Unable to find persisted API key" ) );


      String environmentName = sharedPreferences.getString( SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME, null );

      if ( apiKey == null ) throw ( new IllegalStateException( "Unable to find persisted environment name" ) );

      try
        {
        Environment environment = Environment.valueOf( environmentName );

        sKiteSDK = new KiteSDK( context, apiKey, environment );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to load previous environment", exception );
        }

      }

    return ( sKiteSDK );
    }


  /*****************************************************
   *
   * Returns a singleton instance of the SDK. Note that
   * if there is already an instance of the SDK, it will
   * have its environment set to the supplied values.
   *
   *****************************************************/
  public static KiteSDK getInstance( Context context, String apiKey, Environment environment )
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
   * Convenience method for initialising and Launching the
   * shopping experience.
   *
   *****************************************************/
  public static void startShopping( Context context, String apiKey, KiteSDK.Environment environment, ArrayList<Asset> assetArrayList )
    {
    KiteSDK kiteSDK = getInstance( context, apiKey, environment );

    kiteSDK.startShopping( context, assetArrayList );
    }


  /*****************************************************
   *
   * Convenience method for initialising and Launching the
   * shopping experience, without any assets.
   *
   *****************************************************/
  public static void startShopping( Context context, String apiKey, KiteSDK.Environment environment )
    {
    KiteSDK kiteSDK = getInstance( context, apiKey, environment );

    // Create an empty asset array list
    ArrayList<Asset> assetArrayList = new ArrayList<>( 0 );

    kiteSDK.startShopping( context, assetArrayList );
    }


  ////////// Constructor(s) //////////

  private KiteSDK( Context context, String apiKey, Environment environment )
    {
    mApplicationContext = context.getApplicationContext();
    
    setEnvironment( apiKey, environment );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets a new API key and environment. These details
   * are persisted so that they can be recalled later if
   * this class is garbage collected.
   *
   *****************************************************/
  public void setEnvironment( String apiKey, Environment environment )
    {
    mAPIKey      = apiKey;
    mEnvironment = environment;

    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor
      .putString( SHARED_PREFERENCES_KEY_API_KEY,          apiKey )
      .putString( SHARED_PREFERENCES_KEY_ENVIRONMENT_NAME, environment.name() );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to save current environment to shared preferences" );
      }
    }

  /*****************************************************
   *
   * Sets the Instagram developer credentials. Doing
   * this enables Instagram as an image source
   *
   *****************************************************/
  public void setInstagramCredentials( String clientId, String redirectUri )
    {
    SharedPreferences.Editor editor = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ).edit();

    editor
      .putString( SHARED_PREFERENCES_INSTAGRAM_CLIENT_ID,       clientId )
      .putString( SHARED_PREFERENCES_INSTAGRAM_REDIRECT_URI, redirectUri );

    if ( ! editor.commit() )
      {
      Log.e( LOG_TAG, "Unable to save instagram credentials to shared preferences" );
      }
    }


  /*****************************************************
   *
   * Returns the API key.
   *
   *****************************************************/
  public String getAPIKey()
    {
    return (mAPIKey);
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
   * Returns the instagram client id or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramClientId()
    {
    SharedPreferences prefs = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    return prefs.getString( SHARED_PREFERENCES_INSTAGRAM_CLIENT_ID, null );
    }


  /*****************************************************
   *
   * Returns the instagram redirect uri or null if one has
   * not been set.
   *
   *****************************************************/
  public String getInstagramRedirectURI()
    {
    SharedPreferences prefs = mApplicationContext.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    return prefs.getString( SHARED_PREFERENCES_INSTAGRAM_REDIRECT_URI, null );
    }


  /*****************************************************
   *
   * Launches the shopping experience.
   *
   *****************************************************/
  public void startShopping( Context context, ArrayList<Asset> assetArrayList )
    {
    // Clear any temporary assets before starting a new session
    AssetHelper.clearCachedImages( context );

    // Make sure all the assets are parcelable (this may create some new cached
    // assets). Note that from here on in the SDK is responsible for ensuring
    // that any newly created assets are parcelable.
    assetArrayList = AssetHelper.toParcelableList( context, assetArrayList );

    // We use the activity context here, not the application context
    ProductSelectionActivity.start( context, assetArrayList );
    }


  /*****************************************************
   *
   * Returns the print API endpoint.
   *
   *****************************************************/
  public String getAPIEndpoint()
    {
    return ( mEnvironment.getPrintAPIEndpoint() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Details about the current environment.
   *
   *****************************************************/
  public static enum Environment
    {
    LIVE    ( "https://api.kite.ly/v1.4",   PayPalConfiguration.ENVIRONMENT_PRODUCTION, PAYPAL_CLIENT_ID_LIVE,    PAYPAL_RECIPIENT_LIVE ),
    TEST    ( "https://api.kite.ly/v1.4",   PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX ),
    STAGING ( "http://staging.api.kite.ly", PayPalConfiguration.ENVIRONMENT_SANDBOX,    PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX ); /* private environment intended only for Ocean Labs use, hands off :) */

    private final String  mAPIEndpoint;
    private final String  mPayPalEnvironment;
    private final String  mPayPalClientId;
    private final String  mPayPalRecipient;

    private Environment( String apiEndpoint, String payPalEnvironment, String payPalClientId, String payPalRecipient )
      {
      mAPIEndpoint       = apiEndpoint;
      mPayPalEnvironment = payPalEnvironment;
      mPayPalClientId    = payPalClientId;
      mPayPalRecipient   = payPalRecipient;
      }

    public String getPrintAPIEndpoint()
      {
      return mAPIEndpoint;
      }

    public String getPayPalClientId()
      {
      return mPayPalClientId;
      }

    public String getPayPalEnvironment()
      {
      return mPayPalEnvironment;
      }

    public String getPayPalReceiverEmail()
      {
      return mPayPalRecipient;
      }
    }

  }
