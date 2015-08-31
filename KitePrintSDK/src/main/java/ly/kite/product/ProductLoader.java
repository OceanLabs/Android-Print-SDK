/*****************************************************
 *
 * ProductLoader.java
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

package ly.kite.product;


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
import ly.kite.journey.ProductCreationActivity;
import ly.kite.journey.UserJourneyType;
import ly.kite.util.HTTPJSONRequest;


///// Class Declaration /////

/****************************************************
 *
 * This singleton class retrieves and stores all the
 * product groups and products from the server.
 *
 ****************************************************/
public class ProductLoader implements HTTPJSONRequest.HTTPJSONRequestListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String  LOG_TAG                               = "ProductLoader";

  private static final boolean DISPLAY_DEBUGGING                     = false;

  public  static final long    ANY_AGE_OK                            = -1;

  private static final String  REQUEST_FORMAT_STRING                 = "%s/template/?limit=100";

  private static final String  JSON_NAME_AMOUNT                      = "amount";
  private static final String  JSON_NAME_CURRENCY                    = "currency";
  private static final String  JSON_NAME_CENTIMETERS                 = "cm";
  private static final String  JSON_NAME_COST                        = "cost";
  private static final String  JSON_NAME_FORMATTED_AMOUNT            = "formatted";
  private static final String  JSON_NAME_GROUP_IMAGE                 = "ios_sdk_class_photo";
  private static final String  JSON_NAME_GROUP_LABEL                 = "ios_sdk_product_class";
  private static final String  JSON_NAME_HEIGHT                      = "height";
  private static final String  JSON_NAME_IMAGES_PER_PAGE             = "images_per_page";
  private static final String  JSON_NAME_INCH                        = "inch";
  private static final String  JSON_NAME_LABEL_COLOUR                = "ios_sdk_label_color";
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
  private static final String  JSON_NAME_SHIPPING_COSTS              = "shipping_costs";
  private static final String  JSON_NAME_WIDTH                       = "width";

  private static final int     DEFAULT_IMAGES_PER_PAGE               = 1;


  ////////// Static Variable(s) //////////

  private static ProductLoader sProductCache;


  ////////// Member Variable(s) //////////

  private Context                                   mContext;

  private Handler                                   mSharedHandler;

  private HTTPJSONRequest                           mHTTPJSONRequest;
  private ArrayList<Pair<ProductConsumer,Handler>>  mConsumerHandlerList;

  private ArrayList<ProductGroup>                   mLastRetrievedProductGroupList;
  private HashMap<String,Product>                   mLastRetrievedProductTable;
  private long                                      mLastRetrievedElapsedRealtimeMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /****************************************************
   *
   * Returns an instance of the manager.
   *
   ****************************************************/
  public static ProductLoader getInstance( Context context )
    {
    if ( sProductCache == null )
      {
      sProductCache = new ProductLoader( context );
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
  private static Border parseBorder( JSONArray borderJSONArray ) throws JSONException
  {
      int top    = borderJSONArray.getInt( 0 ) * 4; // * 4 to adjust for iOS screen density baked into ios_image_border: TODO: get Tom to add android border field
      int right  = borderJSONArray.getInt( 1 ) * 4;
      int bottom = borderJSONArray.getInt( 2 ) * 4;
      int left   = borderJSONArray.getInt( 3 ) * 4;

      return ( new Border( top, right, bottom, left ) );
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
  private static Pair<ArrayList<ProductGroup>,HashMap<String,Product>> parseProducts( JSONArray productJSONArray )
    {
    HashMap<String,ProductGroup> productGroupTable = new HashMap<>();
    ArrayList<ProductGroup>      productGroupList  = new ArrayList<>();
    HashMap<String,Product>      productTable      = new HashMap<>();


    // Go through each JSON product

    next_product: for ( int productIndex = 0; productIndex < productJSONArray.length(); productIndex ++ )
      {
      // Parse the product data to create a Product object, and then add it to our list.

      JSONObject productJSONObject = null;

      try
        {
        // Get the product information

        productJSONObject = productJSONArray.getJSONObject( productIndex );

        String                           productId     = productJSONObject.getString( JSON_NAME_PRODUCT_ID );
        String                           productName   = productJSONObject.getString( JSON_NAME_PRODUCT_NAME );
        int                              imagesPerPage = productJSONObject.optInt( JSON_NAME_IMAGES_PER_PAGE, DEFAULT_IMAGES_PER_PAGE );
        MultipleCurrencyAmount           cost          = parseCost( productJSONObject.getJSONArray( JSON_NAME_COST ) );
        MultipleDestinationShippingCosts shippingCosts = parseShippingCosts( productJSONObject.getJSONObject( JSON_NAME_SHIPPING_COSTS ) );


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



        URL   maskURL   = null;
        Bleed maskBleed = null;
        Border border = null;

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
          border = parseBorder( productDetailJSONObject.getJSONArray( JSON_NAME_BORDER ) );
          }
        catch ( JSONException je)
          {
            // Ignore
          }


        // Create the product and display it

        Product product = new Product( productId, productCode, productName, productType, labelColour, userJourneyType, imagesPerPage )
                .setCost( cost )
                .setShippingCosts( shippingCosts )
                .setImageURLs( heroImageURL, imageURLList )
                .setLabelColour( labelColour )
                .setMask( maskURL, maskBleed )
                .setSize( size )
                .setBorder( border );

        Log.i( LOG_TAG, "-- Found product --" );
        Log.i( LOG_TAG, product.toLogString( groupLabel ) );



        // Only display products for which we have a defined user journey
        if ( ! ProductCreationActivity.isSupported( userJourneyType ) )
          {
          Log.i( LOG_TAG, "-- Product discarded: no user journey --" );

          continue next_product;
          }


        // See if we already have the product group. If not - create it now.

        ProductGroup productGroup = productGroupTable.get( groupLabel );

        if ( productGroup == null )
          {
          productGroup = new ProductGroup( groupLabel, labelColour, groupImageURL );

          Log.i( LOG_TAG, "-- New Product Group --" );
          Log.i( LOG_TAG, productGroup.toLogString() );

          productGroupList.add( productGroup );
          productGroupTable.put( groupLabel, productGroup );
          }


        // Add the product to its group
        productGroup.add( product );

        // Add the product to the product id / product table
        productTable.put( productId, product );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to parse JSON product: " + productJSONObject, exception );

        // Ignore individual errors - try and getCost as many products as possible
        }
      }


    return ( new Pair<>( productGroupList, productTable ) );
    }


  ////////// Constructor(s) //////////

  // Constructor is private to ensure it is a singleton
  private ProductLoader( Context context )
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
      onProductData( jsonData );
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
   * Retrieves a list of all the products. This must be called on
   * the UI thread, and always returns products asynchronously.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void getAllProducts( long maximumAgeMillis, ProductConsumer consumer, Handler callbackHandler )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getAllProducts( maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + ", callbackHandler = " + callbackHandler + " )" );


    // If we weren't supplied a callback handler - use a shared one

    if ( callbackHandler == null )
      {
      if ( mSharedHandler == null ) mSharedHandler = new Handler();

      callbackHandler = mSharedHandler;
      }


    // If there is currently a retrieval in progress, always wait for the result

    if ( mHTTPJSONRequest != null )
      {
      mConsumerHandlerList.add( new Pair<ProductConsumer,Handler>( consumer, callbackHandler ) );

      return;
      }


    // There is no retrieval currently in progress. If there is a previously retrieved
    // list, and that its age is OK for what we want, pass it to the consumer
    // immediately.

    if ( mLastRetrievedElapsedRealtimeMillis > 0 )
      {
      long minAcceptableElapsedRealtimeMillis = (
              maximumAgeMillis >= 0
                      ? SystemClock.elapsedRealtime() - maximumAgeMillis
                      : 0 );

      if ( mLastRetrievedElapsedRealtimeMillis >= minAcceptableElapsedRealtimeMillis )
        {
        returnProductsToConsumer( mLastRetrievedProductGroupList, mLastRetrievedProductTable, consumer, callbackHandler );

        return;
        }
      }


    // We need to perform a new retrieval. Create a new request, and consumer list containing the consumer.

    String url = String.format( REQUEST_FORMAT_STRING, KiteSDK.getInstance( mContext ).getAPIEndpoint() );

    mHTTPJSONRequest = new HTTPJSONRequest( mContext, HTTPJSONRequest.HttpMethod.GET, url, null, null );
    mConsumerHandlerList = new ArrayList<Pair<ProductConsumer,Handler>>();
    mConsumerHandlerList.add( new Pair<ProductConsumer,Handler>( consumer, callbackHandler ) );


    // Kick off the retrieval
    mHTTPJSONRequest.start( this );
    }


  /****************************************************
   *
   * Retrieves a list of all the products.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void getAllProducts( long maximumAgeMillis, ProductConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getAllProducts( maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + " )" );

    getAllProducts( maximumAgeMillis, consumer, null );
    }


  /****************************************************
   *
   * Retrieves a list of all the products.
   *
   * @param consumer        The sync listener for the result.
   * @param callbackHandler The handler to use when posting
   *                        the callback.
   *
   ****************************************************/
  public void getAllProducts( ProductConsumer consumer, Handler callbackHandler )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getAllProducts( consumer = " + consumer + ", callbackHandler = " + callbackHandler + " )" );

    getAllProducts( ANY_AGE_OK, consumer, callbackHandler );
    }


  /****************************************************
   *
   * Retrieves a list of all the products.
   *
   * @param consumer The sync listener for the result.
   *
   ****************************************************/
  public void getAllProducts( ProductConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getAllProducts( consumer = " + consumer + " )" );

    getAllProducts( ANY_AGE_OK, consumer, null );
    }


  /****************************************************
   *
   * Called when we have product data to parse.
   *
   ****************************************************/
  public void onProductData( JSONObject jsonData )
    {

    try
      {
      // Try to getCost an array of products, then parse it.

      JSONArray productsJSONArray = jsonData.getJSONArray( JSON_NAME_PRODUCT_ARRAY );

      Pair<ArrayList<ProductGroup>,HashMap<String,Product>> productPair = parseProducts( productsJSONArray );

      ArrayList<ProductGroup> productGroupList = productPair.first;
      HashMap<String,Product> productTable     = productPair.second;

      // Save the query result
      mLastRetrievedProductGroupList      = productGroupList;
      mLastRetrievedProductTable          = productTable;
      mLastRetrievedElapsedRealtimeMillis = SystemClock.elapsedRealtime();

      // Pass the update products list to each of the consumers that want it
      for ( Pair<ProductConsumer,Handler> consumerHandlerPair : mConsumerHandlerList )
        {
        returnProductsToConsumer( productGroupList, productTable, consumerHandlerPair );
        }

      mHTTPJSONRequest = null;
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
  public Pair<ArrayList<ProductGroup>,HashMap<String,Product>> getCachedProducts( long maximumAgeMillis )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCachedProducts( maximumAgeMillis = " + maximumAgeMillis + " )" );

    long minAcceptableElapsedRealtimeMillis = (
            maximumAgeMillis >= 0
                    ? SystemClock.elapsedRealtime() - maximumAgeMillis
                    : 0 );

    if ( mLastRetrievedElapsedRealtimeMillis >= minAcceptableElapsedRealtimeMillis )
      {
      return ( new Pair<ArrayList<ProductGroup>,HashMap<String,Product>>( mLastRetrievedProductGroupList, mLastRetrievedProductTable ) );
      }

    return ( null );
    }


  /****************************************************
   *
   * Passes the products to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnProductsToConsumer( ArrayList<ProductGroup> productGroupList, HashMap<String, Product> productTable, ProductConsumer consumer, Handler callbackHandler )
    {
    // If no handler was supplied - return the products immediately. Otherwise post the call.

    if ( callbackHandler == null )
      {
      consumer.onGotProducts( productGroupList, productTable );
      }
    else
      {
      ProductsCallbackRunnable callbackRunnable = new ProductsCallbackRunnable( productGroupList, productTable, consumer );

      callbackHandler.post( callbackRunnable );
      }
    }


  /****************************************************
   *
   * Passes the products to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnProductsToConsumer( ArrayList<ProductGroup> productGroupList, HashMap<String, Product> productTable, Pair<ProductConsumer, Handler> consumerHandlerPair )
    {
    returnProductsToConsumer( productGroupList, productTable, consumerHandlerPair.first, consumerHandlerPair.second );
    }


  /****************************************************
   *
   * Returns an error to a consumer, either directly
   * or using a handler.
   *
   ****************************************************/
  private void returnErrorToConsumer( Exception exception, ProductConsumer consumer, Handler callbackHandler )
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

    for ( Pair<ProductConsumer,Handler> consumerHandlerPair : mConsumerHandlerList )
      {
      returnErrorToConsumer( exception, consumerHandlerPair.first, consumerHandlerPair.second );
      }


    // Clear the request
    mHTTPJSONRequest = null;
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
   * a products request.
   *
   *****************************************************/
  public interface ProductConsumer
    {
    ////////// Static Constant(s) //////////


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Called when a request completes successfully.
     *
     * @param productGroupList A list of product groups returned from
     *                         the server.
     *
     *****************************************************/
    void onGotProducts( ArrayList<ProductGroup> productGroupList, HashMap<String,Product> productTable );


    /*****************************************************
     *
     * Called when a request results in an error.
     *
     * @param exception The exception that was thrown.
     *
     *****************************************************/
    void onProductRetrievalError( Exception exception );
    }


  /*****************************************************
   *
   * Passes the product details to a consumer.
   *
   *****************************************************/
  private class ProductsCallbackRunnable implements Runnable
    {
    ArrayList<ProductGroup>   mProductGroupList;
    HashMap<String, Product>  mProductTable;
    ProductConsumer           mConsumer;


    ProductsCallbackRunnable( ArrayList<ProductGroup> productGroupList, HashMap<String, Product> productTable, ProductConsumer consumer )
      {
      mProductGroupList = productGroupList;
      mProductTable     = productTable;
      mConsumer         = consumer;
      }


    @Override
    public void run()
      {
      mConsumer.onGotProducts( mProductGroupList, mProductTable );
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
    ProductConsumer  mConsumer;


    ErrorCallbackRunnable( Exception exception, ProductConsumer consumer )
      {
      mException = exception;
      mConsumer  = consumer;
      }


    @Override
    public void run()
      {
      mConsumer.onProductRetrievalError( mException );
      }
    }

  }
