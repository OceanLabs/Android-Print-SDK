/*****************************************************
 *
 * CatalogueLoader.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;

import ly.kite.KiteSDKException;
import ly.kite.KiteSDK;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.journey.UserJourneyType;
import ly.kite.util.HTTPJSONRequest;


///// Class Declaration /////

/****************************************************
 *
 * This class retrieves and stores all the catalogue
 * details the server:
 *   - Product groups
 *   - Products
 *   - Additional details such as banners etc.
 *
 ****************************************************/
public class CatalogueLoader implements HTTPJSONRequest.HTTPJSONRequestListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String  LOG_TAG                               = "CatalogueLoader";

  private static final boolean DISPLAY_DEBUGGING                     = false;

  public  static final long    ANY_AGE_OK                            = -1;

  private static final String TEMPLATE_REQUEST_FORMAT_STRING         = "%s/template/?limit=100";

  private static final String  JSON_NAME_AMOUNT                      = "amount";
  private static final String  JSON_NAME_BOTTOM                      = "bottom";
  private static final String  JSON_NAME_CURRENCY                    = "currency";
  private static final String  JSON_NAME_CENTIMETERS                 = "cm";
  private static final String  JSON_NAME_COST                        = "cost";
  private static final String  JSON_NAME_DESCRIPTION                 = "description";
  private static final String  JSON_NAME_FORMATTED_AMOUNT            = "formatted";
  private static final String  JSON_NAME_GROUP_IMAGE                 = "ios_sdk_class_photo";
  private static final String  JSON_NAME_GROUP_LABEL                 = "ios_sdk_product_class";
  private static final String  JSON_NAME_HEIGHT                      = "height";
  private static final String  JSON_NAME_IMAGE_ASPECT_RATIO          = "image_aspect_ratio";
  private static final String  JSON_NAME_IMAGE_BORDER                = "image_border";
  private static final String  JSON_NAME_IMAGES_PER_PAGE             = "images_per_page";
  private static final String  JSON_NAME_INCH                        = "inch";
  private static final String  JSON_NAME_LABEL_COLOUR                = "ios_sdk_label_color";
  private static final String  JSON_NAME_LEFT                        = "left";
  private static final String  JSON_NAME_MASK_BLEED                  = "mask_bleed";
  private static final String  JSON_NAME_BORDER                      = "ios_image_border";
  private static final String  JSON_NAME_MASK_URL                    = "mask_url";
  private static final String  JSON_NAME_PIXELS                      = "px";
  private static final String  JSON_NAME_PRODUCT_ARRAY               = "objects";
  private static final String  JSON_NAME_PRODUCT_CODE                = "product_code";
  private static final String  JSON_NAME_PRODUCT_DETAIL              = "product";
  private static final String  JSON_NAME_PRODUCT_HERO_IMAGE          = "ios_sdk_cover_photo";
  private static final String  JSON_NAME_PRODUCT_ID                  = "template_id";
  private static final String  JSON_NAME_PRODUCT_NAME                = "name";
  private static final String  JSON_NAME_PRODUCT_SHOTS               = "ios_sdk_product_shots";
  private static final String  JSON_NAME_PRODUCT_SIZE                = "size";
  private static final String  JSON_NAME_PRODUCT_SUBCLASS            = "ios_sdk_product_subclass";
  private static final String  JSON_NAME_PRODUCT_TYPE                = "ios_sdk_product_type";
  private static final String  JSON_NAME_PRODUCT_UI_CLASS            = "ios_sdk_ui_class";
  private static final String  JSON_NAME_RIGHT                       = "right";
  private static final String  JSON_NAME_SHIPPING_COSTS              = "shipping_costs";
  private static final String  JSON_NAME_TOP                         = "top";
  private static final String  JSON_NAME_USER_CONFIG                 = "user_config";
  private static final String  JSON_NAME_WIDTH                       = "width";

  private static final int     DEFAULT_IMAGES_PER_PAGE               = 1;


  ////////// Static Variable(s) //////////

  private static CatalogueLoader sProductCache;


  ////////// Member Variable(s) //////////

  private Context                                   mContext;

  private Handler                                   mSharedHandler;

  private HTTPJSONRequest                           mHTTPJSONRequest;
  private ArrayList<Pair<CatalogueConsumer,Handler>>  mConsumerHandlerList;
  private String                                    mRequestAPIKey;

  private Catalogue                                 mLastRetrievedCatalogue;
  private String                                    mLastRetrievedEnvironmentAPIKey;
  private long                                      mLastRetrievedElapsedRealtimeMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /****************************************************
   *
   * Returns an instance of the manager.
   *
   ****************************************************/
  public static CatalogueLoader getInstance( Context context )
    {
    if ( sProductCache == null )
      {
      sProductCache = new CatalogueLoader( context );
      }

    return ( sProductCache );
    }


  /****************************************************
   *
   * Parses a JSON shipping cost.
   *
   ****************************************************/
  private static MultipleCurrencyAmount parseShippingCost( JSONObject shippingCostJSONObject ) throws JSONException
    {
    return ( new MultipleCurrencyAmount( shippingCostJSONObject ) );
    }


  /****************************************************
   *
   * Parses JSON shipping costs.
   *
   ****************************************************/
  private static MultipleDestinationShippingCosts parseShippingCosts( JSONObject shippingCostsJSONObject ) throws JSONException
    {
    MultipleDestinationShippingCosts shippingCosts = new MultipleDestinationShippingCosts();


    // The JSON shipping costs are not an array, so we need to iterate through the keys (which are the destination codes

    Iterator<String> destinationCodeIterator = shippingCostsJSONObject.keys();

    while ( destinationCodeIterator.hasNext() )
      {
      String destinationCode = destinationCodeIterator.next();

      shippingCosts.add( destinationCode, parseShippingCost( shippingCostsJSONObject.getJSONObject( destinationCode ) ) );
      }


    return ( shippingCosts );
    }


  /****************************************************
   *
   * Parses a JSON size.
   *
   ****************************************************/
  private static SingleUnitSize parseProductSize( JSONObject productSizeJSONObject, UnitOfLength unit ) throws JSONException
    {
    float width  = (float)productSizeJSONObject.getDouble( JSON_NAME_WIDTH );
    float height = (float)productSizeJSONObject.getDouble( JSON_NAME_HEIGHT );

    return ( new SingleUnitSize( unit, width, height  ) );
    }


  /****************************************************
   *
   * Parses a JSON product size.
   *
   ****************************************************/
  private static MultipleUnitSize parseProductSize( JSONObject productSizeJSONObject ) throws JSONException
    {
    MultipleUnitSize size = new MultipleUnitSize();

    size.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_CENTIMETERS ), UnitOfLength.CENTIMETERS ) );
    size.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_INCH ),        UnitOfLength.INCHES      ) );

    try
      {
      size.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_PIXELS ), UnitOfLength.PIXELS ) );
      }
    catch ( JSONException je )
      {
      // Ignore
      }

    return ( size );
    }


  /****************************************************
   *
   * Parses a JSON bleed.
   *
   ****************************************************/
  private static Bleed parseBleed( JSONArray bleedJSONArray ) throws JSONException
    {
    int top    = bleedJSONArray.getInt( 0 );
    int right  = bleedJSONArray.getInt( 1 );
    int bottom = bleedJSONArray.getInt( 2 );
    int left   = bleedJSONArray.getInt( 3 );

    return ( new Bleed( top, right, bottom, left ) );
    }


  /****************************************************
   *
   * Parses a JSON border.
   *
   ****************************************************/
  private static BorderF parseImageBorder( JSONObject borderJSONObject ) throws JSONException
    {
    float top    = (float)borderJSONObject.optDouble( JSON_NAME_TOP );
    float right  = (float)borderJSONObject.optDouble( JSON_NAME_RIGHT );
    float bottom = (float)borderJSONObject.optDouble( JSON_NAME_BOTTOM );
    float left   = (float)borderJSONObject.optDouble( JSON_NAME_LEFT );

    return ( new BorderF( top, right, bottom, left ) );
    }


  /****************************************************
   *
   * Parses an array of product shot URLs.
   *
   ****************************************************/
  private static ArrayList<URL> parseProductShots( JSONArray productShotsJSONArray ) throws JSONException
    {
    ArrayList<URL> productShotList = new ArrayList<URL>();

    for ( int shotIndex = 0; shotIndex < productShotsJSONArray.length(); shotIndex ++ )
      {
      String urlString = productShotsJSONArray.getString( shotIndex );

      try
        {
        productShotList.add( new URL( urlString ) );
        }
      catch ( MalformedURLException mue )
        {
        Log.e( LOG_TAG, "Invalid URL: " + urlString, mue );
        }
      }

    return ( productShotList );
    }


  /****************************************************
   *
   * Parses a JSON colour.
   *
   ****************************************************/
  private static int parseColour( JSONArray colourJSONArray ) throws JSONException
    {
    int red   = colourJSONArray.getInt( 0 );
    int green = colourJSONArray.getInt( 1 );
    int blue  = colourJSONArray.getInt( 2 );

    return ( 0xff000000 | ( ( red << 16 ) & 0x00ff0000 ) | ( ( green << 8 ) & 0x0000ff00 ) | ( blue & 0x000000ff ) );
    }


  /****************************************************
   *
   * Parses a JSON cost.
   *
   ****************************************************/
  private static SingleCurrencyAmount parseCost( JSONObject costJSONObject ) throws JSONException
    {
    Currency   currency        = Currency.getInstance( costJSONObject.getString( JSON_NAME_CURRENCY ) );
    BigDecimal amount          = new BigDecimal( costJSONObject.getString( JSON_NAME_AMOUNT ) );
    String     formattedAmount = costJSONObject.getString( JSON_NAME_FORMATTED_AMOUNT );

    return ( new SingleCurrencyAmount( currency, amount, formattedAmount ) );
    }


  /****************************************************
   *
   * Parses a user journey type.
   *
   ****************************************************/
  private static UserJourneyType parseUserJourneyType( String userJourneyTypeJSONString )
    {
    // At the moment, these match the names of the enum constants

    try
      {
      return ( UserJourneyType.valueOf( userJourneyTypeJSONString ) );
      }
    catch ( Exception e )
      {
      // Fall through
      }

    return ( null );
    }


  /****************************************************
   *
   * Parses a JSON cost array.
   *
   ****************************************************/
  private static MultipleCurrencyAmount parseCost( JSONArray costJSONArray ) throws JSONException
    {
    MultipleCurrencyAmount cost = new MultipleCurrencyAmount();

    for ( int costIndex = 0; costIndex < costJSONArray.length(); costIndex ++ )
      {
      JSONObject costJSONObject = costJSONArray.getJSONObject( costIndex );

      cost.add( parseCost( costJSONObject ) );
      }

    return ( cost );
    }


  /****************************************************
   *
   * Parses a JSON products array.
   *
   ****************************************************/
  private static void parseProducts( JSONArray productJSONArray, Catalogue catalogue )
    {
    // Go through each JSON product

    next_product: for ( int productIndex = 0; productIndex < productJSONArray.length(); productIndex ++ )
      {
      // Parse the product data to create a Product object, and then add it to our list.

      JSONObject productJSONObject = null;

      try
        {
        // Get the product information

        productJSONObject = productJSONArray.getJSONObject( productIndex );

        // Uncomment to dump out JSON
        Log.d( LOG_TAG, "Product JSON:\n" + productJSONObject.toString() );

        String                           productId          = productJSONObject.getString( JSON_NAME_PRODUCT_ID );
        String                           productName        = productJSONObject.getString( JSON_NAME_PRODUCT_NAME );
        String                           productDescription = productJSONObject.getString( JSON_NAME_DESCRIPTION );
        int                              imagesPerPage      = productJSONObject.optInt( JSON_NAME_IMAGES_PER_PAGE, DEFAULT_IMAGES_PER_PAGE );
        MultipleCurrencyAmount           cost               = parseCost( productJSONObject.getJSONArray( JSON_NAME_COST ) );
        MultipleDestinationShippingCosts shippingCosts      = parseShippingCosts( productJSONObject.getJSONObject( JSON_NAME_SHIPPING_COSTS ) );


        // Get the product detail

        JSONObject productDetailJSONObject = productJSONObject.getJSONObject( JSON_NAME_PRODUCT_DETAIL );

        URL                    groupImageURL       = new URL( productDetailJSONObject.getString( JSON_NAME_GROUP_IMAGE ) );
        URL                    heroImageURL        = new URL( productDetailJSONObject.getString( JSON_NAME_PRODUCT_HERO_IMAGE ) );
        int                    labelColour         = parseColour( productDetailJSONObject.getJSONArray( JSON_NAME_LABEL_COLOUR ) );
        String                 groupLabel          = productDetailJSONObject.getString( JSON_NAME_GROUP_LABEL );
        ArrayList<URL>         imageURLList        = parseProductShots( productDetailJSONObject.getJSONArray( JSON_NAME_PRODUCT_SHOTS ) );
        //String                 productSubclass     = productDetailJSONObject.getString( JSON_NAME_PRODUCT_SUBCLASS );
        String                 productType         = productDetailJSONObject.getString( JSON_NAME_PRODUCT_TYPE );
        UserJourneyType        userJourneyType     = parseUserJourneyType( productDetailJSONObject.getString( JSON_NAME_PRODUCT_UI_CLASS ) );
        String                 productCode         = productDetailJSONObject.getString( JSON_NAME_PRODUCT_CODE );
        MultipleUnitSize       size                = parseProductSize( productDetailJSONObject.getJSONObject( JSON_NAME_PRODUCT_SIZE ) );
        float                  imageAspectRatio    = (float)productDetailJSONObject.optDouble( JSON_NAME_IMAGE_ASPECT_RATIO, Product.DEFAULT_IMAGE_ASPECT_RATIO );




        URL     maskURL     = null;
        Bleed   maskBleed   = null;
        BorderF imageBorder = null;

        try
          {
          maskURL   = new URL( productDetailJSONObject.getString( JSON_NAME_MASK_URL ) );
          maskBleed = parseBleed( productDetailJSONObject.getJSONArray( JSON_NAME_MASK_BLEED ) );
          }
        catch ( JSONException je )
          {
          // Ignore
          }

        try
          {
          imageBorder = parseImageBorder( productDetailJSONObject.getJSONObject( JSON_NAME_IMAGE_BORDER ) );
          }
        catch ( JSONException je)
          {
            // Ignore
          }


        // Create the product and display it

        Product product = new Product( productId, productCode, productName, productType, labelColour, userJourneyType, imagesPerPage )
                .setCost( cost )
                .setDescription( productDescription )
                .setShippingCosts( shippingCosts )
                .setImageURLs( heroImageURL, imageURLList )
                .setLabelColour( labelColour )
                .setMask( maskURL, maskBleed )
                .setSize( size )
                .setCreationImage( imageAspectRatio, imageBorder );

        Log.i( LOG_TAG, "-- Found product --" );
        Log.i( LOG_TAG, product.toLogString( groupLabel ) );


        // Only use products for which we have a defined user journey
        if ( ! ProductCreationActivity.isSupported( userJourneyType ) )
          {
          Log.i( LOG_TAG, "-- Product discarded: no user journey --" );

          continue next_product;
          }


        // Add the product to the catalogue

        catalogue.addProduct( groupLabel, groupImageURL, product );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to parse JSON product: " + productJSONObject, exception );

        // Ignore individual errors - try and get as many products as possible
        }
      }
    }


  ////////// Constructor(s) //////////

  // Constructor is private to ensure it is a singleton
  private CatalogueLoader( Context context )
    {
    mContext = context;
    }


  ////////// BaseRequest.BaseRequestListener Method(s) //////////

  /****************************************************
   *
   * Called when the retrieval request completes successfully.
   *
   ****************************************************/
  @Override
  public void onSuccess( int httpStatusCode, JSONObject jsonData )
    {
    // Check if we got a valid HTTP response code

    if ( httpStatusCode >= 200 && httpStatusCode <= 299 )
      {
      onCatalogue( jsonData );
      }
    else
      {
      // Invalid HTTP response code - see if we can getCost an error message

      try
        {
        JSONObject errorJSONObject = jsonData.getJSONObject( HTTPJSONRequest.ERROR_RESPONSE_JSON_OBJECT_NAME );
        String errorMessage = errorJSONObject.getString( HTTPJSONRequest.ERROR_RESPONSE_MESSAGE_JSON_NAME );
        String errorCode = errorJSONObject.getString( HTTPJSONRequest.ERROR_RESPONSE_CODE_JSON_NAME );

        Exception exception = new KiteSDKException( errorMessage );

        returnErrorToConsumers( exception );
        }
      catch ( JSONException je )
        {
        returnErrorToConsumers( je);
        }
      }
    }


  /****************************************************
   *
   * Called when a request fails with an error.
   *
   ****************************************************/
  @Override
  public void onError( Exception exception )
    {
    returnErrorToConsumers( exception );
    }


  ////////// Method(s) //////////

  /****************************************************
   *
   * Retrieves a catalogue. This must be called on
   * the UI thread, and always returns products asynchronously.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void getCatalogue( long maximumAgeMillis, CatalogueConsumer consumer, Handler callbackHandler )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCatalogue( maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + ", callbackHandler = " + callbackHandler + " )" );


    // If we weren't supplied a callback handler - use a shared one

    if ( callbackHandler == null )
      {
      if ( mSharedHandler == null ) mSharedHandler = new Handler();

      callbackHandler = mSharedHandler;
      }


    // If there is currently a retrieval in progress, always wait for the result

    if ( mHTTPJSONRequest != null )
      {
      mConsumerHandlerList.add( new Pair<CatalogueConsumer,Handler>( consumer, callbackHandler ) );

      return;
      }


    // There is no retrieval currently in progress. Check if there is a suitable
    // cached list of products to return immediately. The conditions for such a
    // list are:
    //   - The were retrieved suitably recently
    //   - The API key for the environment matches the current one

    String currentAPIKey = KiteSDK.getInstance( mContext ).getAPIKey();

    if ( mLastRetrievedEnvironmentAPIKey != null &&
         mLastRetrievedEnvironmentAPIKey.equals( currentAPIKey ) &&
         mLastRetrievedElapsedRealtimeMillis > 0 )
      {
      long minAcceptableElapsedRealtimeMillis = (
              maximumAgeMillis >= 0
                      ? SystemClock.elapsedRealtime() - maximumAgeMillis
                      : 0 );

      if ( mLastRetrievedElapsedRealtimeMillis >= minAcceptableElapsedRealtimeMillis )
        {
        returnCatalogueToConsumer( mLastRetrievedCatalogue, consumer, callbackHandler );

        return;
        }
      }


    // We need to perform a new retrieval. Create a new request, and consumer list containing the consumer.

    String url = String.format( TEMPLATE_REQUEST_FORMAT_STRING, KiteSDK.getInstance( mContext ).getAPIEndpoint() );

    mHTTPJSONRequest = new HTTPJSONRequest( mContext, HTTPJSONRequest.HttpMethod.GET, url, null, null );
    mConsumerHandlerList = new ArrayList<Pair<CatalogueConsumer,Handler>>();
    mConsumerHandlerList.add( new Pair<CatalogueConsumer,Handler>( consumer, callbackHandler ) );
    mRequestAPIKey = currentAPIKey;


    // Kick off the retrieval
    mHTTPJSONRequest.start( this );
    }


  /****************************************************
   *
   * Retrieves a catalogue.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void getCatalogue( long maximumAgeMillis, CatalogueConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCatalogue( maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + " )" );

    getCatalogue( maximumAgeMillis, consumer, null );
    }


  /****************************************************
   *
   * Retrieves a catalogue.
   *
   * @param consumer        The sync listener for the result.
   * @param callbackHandler The handler to use when posting
   *                        the callback.
   *
   ****************************************************/
  public void getCatalogue( CatalogueConsumer consumer, Handler callbackHandler )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCatalogue( consumer = " + consumer + ", callbackHandler = " + callbackHandler + " )" );

    getCatalogue( ANY_AGE_OK, consumer, callbackHandler );
    }


  /****************************************************
   *
   * Retrieves a catalogue.
   *
   * @param consumer The sync listener for the result.
   *
   ****************************************************/
  public void getCatalogue( CatalogueConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCatalogue( consumer = " + consumer + " )" );

    getCatalogue( ANY_AGE_OK, consumer, null );
    }


  /****************************************************
   *
   * Called when we have catalogue data to parse.
   *
   ****************************************************/
  public void onCatalogue( JSONObject jsonData )
    {
    // Create a new catalogue, with any custom data.

    JSONObject userConfigJSONObject = jsonData.optJSONObject( JSON_NAME_USER_CONFIG );

    Catalogue  catalogue            = new Catalogue( userConfigJSONObject );


    try
      {
      // Try to get a set of products, then parse it.

      JSONArray productsJSONArray = jsonData.getJSONArray( JSON_NAME_PRODUCT_ARRAY );

      parseProducts( productsJSONArray, catalogue );


      // Save the query result
      mLastRetrievedCatalogue             = catalogue;
      mLastRetrievedEnvironmentAPIKey     = mRequestAPIKey;
      mLastRetrievedElapsedRealtimeMillis = SystemClock.elapsedRealtime();

      // Pass the update products list to each of the consumers that want it
      for ( Pair<CatalogueConsumer,Handler> consumerHandlerPair : mConsumerHandlerList )
        {
        returnCatalogueToConsumer( catalogue, consumerHandlerPair );
        }

      mHTTPJSONRequest     = null;
      mConsumerHandlerList = null;
      }
    catch ( JSONException je )
      {
      returnErrorToConsumers( je );
      }

    }


  /****************************************************
   *
   * Returns any cached products if they are not too old.
   *
   * @return The product group list and product table, or
   *         null if there are no up-to-date products.
   *
   ****************************************************/
  public Catalogue getCachedCatalogue( long maximumAgeMillis )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCachedCatalogue( maximumAgeMillis = " + maximumAgeMillis + " )" );

    long minAcceptableElapsedRealtimeMillis = (
            maximumAgeMillis >= 0
                    ? SystemClock.elapsedRealtime() - maximumAgeMillis
                    : 0 );

    if ( mLastRetrievedElapsedRealtimeMillis >= minAcceptableElapsedRealtimeMillis )
      {
      return ( mLastRetrievedCatalogue );
      }

    return ( null );
    }


  /****************************************************
   *
   * Passes the products to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnCatalogueToConsumer( Catalogue catalogue, CatalogueConsumer consumer, Handler callbackHandler )
    {
    // If no handler was supplied - return the products immediately. Otherwise post the call.

    if ( callbackHandler == null )
      {
      consumer.onCatalogueSuccess( catalogue );
      }
    else
      {
      CatalogueCallbackRunnable callbackRunnable = new CatalogueCallbackRunnable( catalogue, consumer );

      callbackHandler.post( callbackRunnable );
      }
    }


  /****************************************************
   *
   * Passes the products to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnCatalogueToConsumer( Catalogue catalogue, Pair<CatalogueConsumer, Handler> consumerHandlerPair )
    {
    returnCatalogueToConsumer( catalogue, consumerHandlerPair.first, consumerHandlerPair.second );
    }


  /****************************************************
   *
   * Returns an error to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnErrorToConsumer( Exception exception, CatalogueConsumer consumer, Handler callbackHandler )
    {
    ErrorCallbackRunnable callbackRunnable = new ErrorCallbackRunnable( exception, consumer );

    callbackHandler.post( callbackRunnable );
    }


  /****************************************************
   *
   * Returns an error to the consumers.
   *
   ****************************************************/
  private void returnErrorToConsumers( Exception exception )
    {
    // Go through each of the consumers and notify them of the error

    for ( Pair<CatalogueConsumer,Handler> consumerHandlerPair : mConsumerHandlerList )
      {
      returnErrorToConsumer( exception, consumerHandlerPair.first, consumerHandlerPair.second );
      }


    // Clear the request
    mHTTPJSONRequest     = null;
    mConsumerHandlerList = null;
    }


  /****************************************************
   *
   * Cancels the request.
   *
   ****************************************************/
  public void cancelSubmissionForPrinting()
    {
    if ( mHTTPJSONRequest != null )
      {
      mHTTPJSONRequest.cancel();

      mHTTPJSONRequest = null;
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * This interface defines a listener to the result of
   * a catalogue request.
   *
   *****************************************************/
  public interface CatalogueConsumer
    {
    ////////// Static Constant(s) //////////


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Called when a request completes successfully.
     *
     * @param catalogue The retrieved catalogue.
     *
     *****************************************************/
    void onCatalogueSuccess( Catalogue catalogue );


    /*****************************************************
     *
     * Called when a request results in an error.
     *
     * @param exception The exception that was thrown.
     *
     *****************************************************/
    void onCatalogueError( Exception exception );
    }


  /*****************************************************
   *
   * Passes the catalogue to a consumer.
   *
   *****************************************************/
  private class CatalogueCallbackRunnable implements Runnable
    {
    Catalogue        mCatalogue;
    CatalogueConsumer mConsumer;


    CatalogueCallbackRunnable( Catalogue catalogue, CatalogueConsumer consumer )
      {
      mCatalogue = catalogue;
      mConsumer  = consumer;
      }


    @Override
    public void run()
      {
      mConsumer.onCatalogueSuccess( mCatalogue );
      }
    }


  /*****************************************************
   *
   * Returns an error to a consumer.
   *
   *****************************************************/
  private class ErrorCallbackRunnable implements Runnable
    {
    Exception        mException;
    CatalogueConsumer mConsumer;


    ErrorCallbackRunnable( Exception exception, CatalogueConsumer consumer )
      {
      mException = exception;
      mConsumer  = consumer;
      }


    @Override
    public void run()
      {
      mConsumer.onCatalogueError( mException );
      }
    }

  }
