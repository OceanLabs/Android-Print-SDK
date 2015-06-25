/*****************************************************
 *
 * ProductManager.java
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

package ly.kite.print;


///// Import(s) /////

import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;

import ly.kite.shopping.MultipleCurrencyCost;
import ly.kite.shopping.ShippingCosts;
import ly.kite.shopping.SingleCurrencyCost;


///// Class Declaration /////

/****************************************************
 *
 * This singleton class retrieves and stores all the
 * product groups and products from the server.
 *
 ****************************************************/
public class ProductManager implements BaseRequest.BaseRequestListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String LOG_TAG                               = "ProductManager";

  public  static final long   ANY_AGE_OK                            = -1;

  private static final String REQUEST_FORMAT_STRING                 = "%s/template/?limit=100";

  private static final String JSON_NAME_AMOUNT                      = "amount";
  private static final String JSON_NAME_CURRENCY                    = "currency";
  private static final String JSON_NAME_CENTIMETERS                 = "cm";
  private static final String JSON_NAME_COST                        = "cost";
  private static final String JSON_NAME_FORMATTED_AMOUNT            = "formatted";
  private static final String JSON_NAME_GROUP_IMAGE                 = "ios_sdk_class_photo";
  private static final String JSON_NAME_GROUP_LABEL                 = "ios_sdk_product_class";
  private static final String JSON_NAME_HEIGHT                      = "height";
  private static final String JSON_NAME_IMAGES_PER_PAGE             = "images_per_page";
  private static final String JSON_NAME_INCH                        = "inch";
  private static final String JSON_NAME_LABEL_COLOUR                = "ios_sdk_label_color";
  private static final String JSON_NAME_MASK_BLEED                  = "mask_bleed";
  private static final String JSON_NAME_MASK_URL                    = "mask_url";
  private static final String JSON_NAME_PIXELS                      = "px";
  private static final String JSON_NAME_PRODUCT_ARRAY               = "objects";
  private static final String JSON_NAME_PRODUCT_CODE                = "product_code";
  private static final String JSON_NAME_PRODUCT_DETAIL              = "product";
  private static final String JSON_NAME_PRODUCT_HERO_IMAGE          = "ios_sdk_cover_photo";
  private static final String JSON_NAME_PRODUCT_ID                  = "template_id";
  private static final String JSON_NAME_PRODUCT_NAME                = "name";
  private static final String JSON_NAME_PRODUCT_SHOTS               = "ios_sdk_product_shots";
  private static final String JSON_NAME_PRODUCT_SIZE                = "size";
  private static final String JSON_NAME_PRODUCT_SUBCLASS            = "ios_sdk_product_subclass";
  private static final String JSON_NAME_PRODUCT_TYPE                = "ios_sdk_product_type";
  private static final String JSON_NAME_PRODUCT_UI_CLASS            = "ios_sdk_ui_class";
  private static final String JSON_NAME_SHIPPING_COSTS              = "shipping_costs";
  private static final String JSON_NAME_SHIPPING_DEST_UK            = "GBR";
  private static final String JSON_NAME_SHIPPING_DEST_USA           = "USA";
  private static final String JSON_NAME_SHIPPING_DEST_EUROPE        = "europe";
  private static final String JSON_NAME_SHIPPING_DEST_REST_OF_WORLD = "rest_of_world";
  private static final String JSON_NAME_WIDTH                       = "width";


  ////////// Static Variable(s) //////////

  private static ProductManager sProductSyncer;


  ////////// Member Variable(s) //////////

  private BaseRequest                 mBaseRequest;
  private ArrayList<ProductConsumer>  mProductConsumerList;

  private ArrayList<ProductGroup>     mLastRetrievedProductGroupList;
  private HashMap<String,Product>     mLastRetrievedProductTable;
  private long                        mLastRetrievedElapsedRealtimeMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /****************************************************
   *
   * Returns an instance of the syncer.
   *
   ****************************************************/
  public static ProductManager getInstance()
    {
    if ( sProductSyncer == null )
      {
      sProductSyncer = new ProductManager();
      }

    return ( sProductSyncer );
    }


  /****************************************************
   *
   * Parses a JSON shipping cost.
   *
   ****************************************************/
  private static MultipleCurrencyCost parseShippingCost( JSONObject shippingCostJSONObject ) throws JSONException
    {
    // The costs aren't in an array, so we need to iterate through the keys, i.e. the currency codes.

    MultipleCurrencyCost shippingCost = new MultipleCurrencyCost();

    Iterator<String> currencyIterator = shippingCostJSONObject.keys();

    while ( currencyIterator.hasNext() )
      {
      String     currencyCode = currencyIterator.next();
      BigDecimal amount       = new BigDecimal( shippingCostJSONObject.getString( currencyCode ) );

      shippingCost.add( new SingleCurrencyCost( Currency.getInstance( currencyCode ), amount ) );
      }

    return ( shippingCost );
    }


  /****************************************************
   *
   * Parses JSON shipping costs.
   *
   ****************************************************/
  private static ShippingCosts parseShippingCosts( JSONObject shippingCostsJSONObject ) throws JSONException
    {
    ShippingCosts shippingCosts = new ShippingCosts();

    shippingCosts.add( ShippingCosts.Destination.UK,            parseShippingCost( shippingCostsJSONObject.getJSONObject( JSON_NAME_SHIPPING_DEST_UK            ) ) );
    shippingCosts.add( ShippingCosts.Destination.USA,           parseShippingCost( shippingCostsJSONObject.getJSONObject( JSON_NAME_SHIPPING_DEST_USA           ) ) );
    shippingCosts.add( ShippingCosts.Destination.EUROPE,        parseShippingCost( shippingCostsJSONObject.getJSONObject( JSON_NAME_SHIPPING_DEST_EUROPE ) ) );
    shippingCosts.add( ShippingCosts.Destination.REST_OF_WORLD, parseShippingCost( shippingCostsJSONObject.getJSONObject( JSON_NAME_SHIPPING_DEST_REST_OF_WORLD ) ) );

    return ( shippingCosts );
    }


  /****************************************************
   *
   * Parses a JSON size.
   *
   ****************************************************/
  private static ProductSize parseProductSize( JSONObject productSizeJSONObject, ProductSize.Units units ) throws JSONException
    {
    float width  = (float)productSizeJSONObject.getLong( JSON_NAME_WIDTH );
    float height = (float)productSizeJSONObject.getLong( JSON_NAME_HEIGHT );

    return ( new ProductSize( units, width, height  ) );
    }


  /****************************************************
   *
   * Parses a JSON product size.
   *
   ****************************************************/
  private static ArrayList<ProductSize> parseProductSize( JSONObject productSizeJSONObject ) throws JSONException
    {
    ArrayList<ProductSize> productSizeList = new ArrayList<ProductSize>();

    productSizeList.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_CENTIMETERS ), ProductSize.Units.CENTIMETERS ) );
    productSizeList.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_INCH ),        ProductSize.Units.INCHES      ) );

    try
      {
      productSizeList.add( parseProductSize( productSizeJSONObject.getJSONObject( JSON_NAME_PIXELS ), ProductSize.Units.PIXELS ) );
      }
    catch ( JSONException je )
      {
      // Ignore
      }

    return ( productSizeList );
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
    int left   = bleedJSONArray.getInt( 2 );

    return ( new Bleed( top, right, bottom, left ) );
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
  private static SingleCurrencyCost parseCost( JSONObject costJSONObject ) throws JSONException
    {
    Currency   currency        = Currency.getInstance( costJSONObject.getString( JSON_NAME_CURRENCY ) );
    BigDecimal amount          = new BigDecimal( costJSONObject.getString( JSON_NAME_AMOUNT ) );
    String     formattedAmount = costJSONObject.getString( JSON_NAME_FORMATTED_AMOUNT );

    return ( new SingleCurrencyCost( currency, amount, formattedAmount ) );
    }


  /****************************************************
   *
   * Parses a JSON cost array.
   *
   ****************************************************/
  private static MultipleCurrencyCost parseCost( JSONArray costJSONArray ) throws JSONException
    {
    MultipleCurrencyCost cost = new MultipleCurrencyCost();

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

    for ( int productIndex = 0; productIndex < productJSONArray.length(); productIndex ++ )
      {
      // Parse the product data to create a Product object, and then add it to our list.

      JSONObject productJSONObject = null;

      try
        {
        // Get the product information

        productJSONObject = productJSONArray.getJSONObject( productIndex );

        String                 productId           = productJSONObject.getString( JSON_NAME_PRODUCT_ID );
        String                 productName         = productJSONObject.getString( JSON_NAME_PRODUCT_NAME );
        int                    imagesPerPage       = productJSONObject.getInt( JSON_NAME_IMAGES_PER_PAGE );
        MultipleCurrencyCost   cost                = parseCost( productJSONObject.getJSONArray( JSON_NAME_COST ) );
        ShippingCosts          shippingCosts       = parseShippingCosts( productJSONObject.getJSONObject( JSON_NAME_SHIPPING_COSTS ) );


        // Get the product detail

        JSONObject productDetailJSONObject = productJSONObject.getJSONObject( JSON_NAME_PRODUCT_DETAIL );

        URL                    groupImageURL       = new URL( productDetailJSONObject.getString( JSON_NAME_GROUP_IMAGE ) );
        URL                    heroImageURL        = new URL( productDetailJSONObject.getString( JSON_NAME_PRODUCT_HERO_IMAGE ) );
        int                    labelColour         = parseColour( productDetailJSONObject.getJSONArray( JSON_NAME_LABEL_COLOUR ) );
        String                 groupLabel          = productDetailJSONObject.getString( JSON_NAME_GROUP_LABEL );
        ArrayList<URL>         imageURLList        = parseProductShots( productDetailJSONObject.getJSONArray( JSON_NAME_PRODUCT_SHOTS ) );
        //String                 productSubclass     = productDetailJSONObject.getString( JSON_NAME_PRODUCT_SUBCLASS );
        String                 productType         = productDetailJSONObject.getString( JSON_NAME_PRODUCT_TYPE );
        String                 productUIClass      = productDetailJSONObject.getString( JSON_NAME_PRODUCT_UI_CLASS );
        String                 productCode         = productDetailJSONObject.getString( JSON_NAME_PRODUCT_CODE );
        ArrayList<ProductSize> productSizeList     = parseProductSize( productDetailJSONObject.getJSONObject( JSON_NAME_PRODUCT_SIZE ) );


        URL   maskURL   = null;
        Bleed maskBleed = null;

        try
          {
          maskURL   = new URL( productDetailJSONObject.getString( JSON_NAME_MASK_URL ) );
          maskBleed = parseBleed( productDetailJSONObject.getJSONArray( JSON_NAME_MASK_BLEED ) );
          }
        catch ( JSONException je )
          {
          // Ignore
          }


        // See if we already have the product group. If not - create it now.

        ProductGroup productGroup = productGroupTable.get( groupLabel );

        if ( productGroup == null )
          {
          productGroup = new ProductGroup( groupLabel, labelColour, groupImageURL );

          productGroupList.add( productGroup );
          productGroupTable.put( groupLabel, productGroup );
          }


        // Create the product and add it to the product group

        Product product = new Product( productId, productCode, productName, productType, labelColour, productUIClass, imagesPerPage )
                .setCost( cost )
                .setShippingCosts( shippingCosts )
                .setImageURLs( heroImageURL, imageURLList )
                .setLabelColour( labelColour )
                .setMask( maskURL, maskBleed )
                .setSize( productSizeList );

        productGroup.add( product );


        // Add the product to the product id / product table
        productTable.put( productId, product );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to parse JSON product: " + productJSONObject, exception );

        // Ignore individual errors - try and get as many products as possible
        }
      }


    return ( new Pair<>( productGroupList, productTable ) );
    }


  ////////// Constructor(s) //////////

  // Constructor is private to ensure it is a singleton
  private ProductManager()
    {
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

    try
      {
      // Check if we got a valid HTTP response code

      if ( httpStatusCode >= 200 && httpStatusCode <= 299 )
        {
        // Try to get an array of products, then parse it.

        JSONArray productsJSONArray = jsonData.getJSONArray( JSON_NAME_PRODUCT_ARRAY );

        Pair<ArrayList<ProductGroup>,HashMap<String,Product>> productPair = parseProducts( productsJSONArray );

        ArrayList<ProductGroup> productGroupList = productPair.first;
        HashMap<String,Product> productTable     = productPair.second;

        synchronized ( this )
          {
          // Save the query result
          mLastRetrievedProductGroupList      = productGroupList;
          mLastRetrievedProductTable          = productTable;
          mLastRetrievedElapsedRealtimeMillis = SystemClock.elapsedRealtime();

          // Pass the update products list to each of the consumers that want it
          for ( ProductConsumer consumer : mProductConsumerList )
            {
            consumer.onGotProducts( productGroupList, productTable );
            }

          mBaseRequest         = null;
          mProductConsumerList = null;
          }
        }
      else
        {
        // Invalid HTTP response code - see if we can get an error message

        JSONObject errorJSONObject = jsonData.getJSONObject( BaseRequest.ERROR_RESPONSE_JSON_OBJECT_NAME );
        String     errorMessage    = errorJSONObject.getString( BaseRequest.ERROR_RESPONSE_MESSAGE_JSON_NAME );
        String     errorCode       = errorJSONObject.getString( BaseRequest.ERROR_RESPONSE_CODE_JSON_NAME );

        Exception exception = new KitePrintSDKException( errorMessage );

        returnErrorToConsumers( exception );
        }

      }
    catch ( JSONException je )
      {
      returnErrorToConsumers( je );
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
   * Retrieves a list of all the products. Must be called on
   * the UI thread.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void getAllProducts( final long maximumAgeMillis, final ProductConsumer consumer )
    {

    synchronized ( this )
      {
      // If there is currently a retrieval in progress, always wait for the result

      if ( mBaseRequest != null )
        {
        mProductConsumerList.add( consumer );

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
          consumer.onGotProducts( mLastRetrievedProductGroupList, mLastRetrievedProductTable );

          return;
          }
        }


      // We need to perform a new retrieval

      String url = String.format( REQUEST_FORMAT_STRING, KitePrintSDK.getEnvironment().getPrintAPIEndpoint() );

      mBaseRequest = new BaseRequest( BaseRequest.HttpMethod.GET, url, null, null );
      mProductConsumerList = new ArrayList<ProductConsumer>();
      mProductConsumerList.add( consumer );
      }


    // Kick off the retrieval
    mBaseRequest.start( this );
    }


  /****************************************************
   *
   * Retrieves a list of all the products.
   *
   * @param consumer The sync listener for the result.
   *
   ****************************************************/
  public void getAllProducts( final ProductConsumer consumer )
    {
    getAllProducts( ANY_AGE_OK, consumer );
    }


  /****************************************************
   *
   * Synchronously obtains the products. We need to be able
   * to do this because the product retrieval is asynchronous.
   *
   * @return The product group list, or null if they
   *         could not be retrieved.
   *
   ****************************************************/
  public Pair<ArrayList<ProductGroup>,HashMap<String,Product>> getAllProducts()
    {
    SynchronousProductConsumer consumer = new SynchronousProductConsumer();

    Pair<ArrayList<ProductGroup>,HashMap<String,Product>> productPair = consumer.getProductPair();

    if ( productPair == null ) throw ( new RuntimeException( "Unable to retrieve products" ) );

    return ( productPair );
    }


  /****************************************************
   *
   * Returns a product by its id.
   *
   * @param id The product id.
   *
   * @return The product with a matching id.
   *
   * @throws RuntimeException If the products could not be
   *         retrieved.
   *
   * @throws IllegalArgumentException If no product was found
   *         with a matching id.
   *
   ****************************************************/
  public Product getProductById( String id )
    {
    // Get the products synchronously

    Pair<ArrayList<ProductGroup>,HashMap<String,Product>> productPair = getAllProducts();

    if ( productPair == null ) return ( null );


    // Lookup the product in the product table

    Product product = productPair.second.get( id );

    if ( product == null ) throw ( new IllegalArgumentException( "No product was found with the id " + id ) );

    return ( product );
    }


  /****************************************************
   *
   * Returns an error to the consumers.
   *
   ****************************************************/
  private synchronized void returnErrorToConsumers( Exception exception )
    {
    // Go through each of the consumers and notify them of the error

    for ( ProductConsumer consumer : mProductConsumerList )
      {
      consumer.onProductRetrievalError( exception );
      }


    // Clear the request
    mBaseRequest         = null;
    mProductConsumerList = null;
    }


  /****************************************************
   *
   * Cancels the request.
   *
   ****************************************************/
  public void cancelSubmissionForPrinting()
    {
    if ( mBaseRequest != null )
      {
      mBaseRequest.cancel();

      mBaseRequest = null;
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
   * This class is used to turn the asynchronous product
   * retrieval into a synchronous process.
   *
   *****************************************************/
  private class SynchronousProductConsumer implements ProductConsumer
    {
    private Pair<ArrayList<ProductGroup>,HashMap<String,Product>>  mProductPair;


    @Override
    public synchronized void onGotProducts( ArrayList<ProductGroup> productGroupList, HashMap<String,Product> productTable )
      {
      mProductPair = new Pair<ArrayList<ProductGroup>,HashMap<String,Product>>( productGroupList, productTable );

      notifyAll();
      }


    @Override
    public synchronized void onProductRetrievalError( Exception exception )
      {
      Log.e( LOG_TAG, "Synchronous product retrieval returned error", exception );

      // The product pair stays null

      notifyAll();
      }


    synchronized Pair<ArrayList<ProductGroup>,HashMap<String,Product>> getProductPair()
      {
      // Kick off a retrieval now we have the monitor
      getAllProducts( this );

      // Wait for a notification
      try
        {
        wait();
        }
      catch ( InterruptedException ie )
        {
        // Ignore
        }

      return ( mProductPair );
      }
    }

  }
