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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ly.kite.KiteSDKException;
import ly.kite.KiteSDK;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.journey.UserJourneyType;
import ly.kite.util.AssetHelper;
import ly.kite.util.HTTPJSONRequest;
import ly.kite.api.KiteAPIRequest;


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
public class CatalogueLoader implements HTTPJSONRequest.IJSONResponseListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String  LOG_TAG                               = "CatalogueLoader";

  static private final boolean DISPLAY_DEBUGGING                     = false;
  static private final boolean DISPLAY_PRE_CACHING_INFO              = false;
  static private final boolean TEST_EMPTY_CATALOGUE                  = false;

  static public  final long    ANY_AGE_OK                            = -1;

  static private final String  TEMPLATE_REQUEST_FORMAT_STRING        = "%s/template/?limit=200";

  static private final String  JSON_NAME_ACTIVE                      = "active";
  static private final String  JSON_NAME_AMOUNT                      = "amount";
  static private final String  JSON_NAME_BACKGROUND_IMAGE_URL        = "product_background_image_url";
  static private final String  JSON_NAME_BOTTOM                      = "bottom";
  static private final String  JSON_NAME_CALENDAR_ASSETS             = "ios_calendar_assets";
  static private final String  JSON_NAME_CURRENCY                    = "currency";
  static private final String  JSON_NAME_CENTIMETERS                 = "cm";
  static private final String  JSON_NAME_COST                        = "cost";
  static private final String  JSON_NAME_COVER_PHOTO_VARIANTS        = "cover_photo_variants";
  static private final String  JSON_NAME_DESCRIPTION                 = "description";
  static private final String  JSON_NAME_FORMATTED_AMOUNT            = "formatted";
  static private final String  JSON_NAME_GRID_COUNT_X                = "grid_count_x";
  static private final String  JSON_NAME_GRID_COUNT_Y                = "grid_count_y";
  static private final String  JSON_NAME_GROUP_IMAGE                 = "ios_sdk_class_photo";
  static private final String  JSON_NAME_GROUP_LABEL                 = "ios_sdk_product_class";
  static private final String  JSON_NAME_HEIGHT                      = "height";
  static private final String  JSON_NAME_IMAGE_ASPECT_RATIO          = "image_aspect_ratio";
  static private final String  JSON_NAME_IMAGE_BORDER                = "image_border";
  static private final String  JSON_NAME_IMAGES_PER_PAGE             = "images_per_page";
  static private final String  JSON_NAME_INCH                        = "inch";
  static private final String  JSON_NAME_LABEL_COLOUR                = "ios_sdk_label_color";
  static private final String  JSON_NAME_LEFT                        = "left";
  static private final String  JSON_NAME_MASK_BLEED                  = "mask_bleed";
  static private final String  JSON_NAME_ORIGINAL_AMOUNT             = "original_amount";
  //static private final String  JSON_NAME_BORDER                      = "ios_image_border";
  static private final String  JSON_NAME_MASK_URL                    = "mask_url";
  static private final String  JSON_NAME_OPTIONS                     = "options";
  static private final String  JSON_NAME_OPTION_NAME                 = "name";
  static private final String  JSON_NAME_OPTION_CODE                 = "code";
  static private final String  JSON_NAME_HIGHLIGHTS_URL              = "product_highlights_url";
  static private final String  JSON_NAME_PAYPAL_SUPPORTED_CURRENCIES = "paypal_supported_currencies";
  static private final String  JSON_NAME_PIXELS                      = "px";
  static private final String  JSON_NAME_PRINT_IN_STORE              = "print_in_store";
  static private final String  JSON_NAME_PRODUCT_ACTIVE              = "product_active";
  static private final String  JSON_NAME_PRODUCT_ARRAY               = "objects";
  static private final String  JSON_NAME_PRODUCT_CODE                = "product_code";
  static private final String  JSON_NAME_PRODUCT_DETAIL              = "product";
  //static private final String  JSON_NAME_PRODUCT_HERO_IMAGE          = "ios_sdk_cover_photo";
  static private final String  JSON_NAME_PRODUCT_ID                  = "template_id";
  static private final String  JSON_NAME_PRODUCT_NAME                = "name";
  static private final String  JSON_NAME_PRODUCT_SHOTS               = "ios_sdk_product_shots";
  static private final String  JSON_NAME_PRODUCT_SIZE                = "size";
  static private final String  JSON_NAME_PRODUCT_SUBCLASS            = "ios_sdk_product_subclass";
  static private final String  JSON_NAME_PRODUCT_TYPE                = "ios_sdk_product_type";
  static private final String  JSON_NAME_PRODUCT_UI_CLASS            = "ios_sdk_ui_class";
  static private final String  JSON_NAME_RIGHT                       = "right";
  static private final String  JSON_NAME_SHIPPING_COSTS              = "shipping_costs";
  static private final String  JSON_NAME_SUPPORTED_OPTIONS           = "supported_options";
  static private final String  JSON_NAME_SUPPORTS_TEXT_ON_BORDER     = "supports_text_on_border";
  static private final String  JSON_NAME_TOP                         = "top";
  static private final String  JSON_NAME_URL                         = "url";
  static private final String  JSON_NAME_USER_CONFIG                 = "user_config";
  static private final String  JSON_NAME_VALUE_NAME                  = "name";
  static private final String  JSON_NAME_VALUE_CODE                  = "code";
  static private final String  JSON_NAME_VARIANT_ID                  = "variant_id";
  static private final String  JSON_NAME_WIDTH                       = "width";
  static private final String  JSON_NAME_CATEGORY                    = "product_category";
  static private final String  JSON_NAME_BLEND_MODE                  = "mask_blend_mode";

  static private final int     DEFAULT_IMAGES_PER_PAGE               = 1;
  static private final int     DEFAULT_GRID_SIZE                     = 1;

  static private final String  COVER_PHOTO_VARIANT_ID_DEFAULT        = "default";


  ////////// Static Variable(s) //////////

  static private CatalogueLoader sCatalogueLoaderInstance;


  ////////// Member Variable(s) //////////

  private Context                        mContext;

  private Handler                        mHandler;

  private KiteAPIRequest                 mHTTPJSONRequest;
  private LinkedList<ICatalogueConsumer> mConsumerList;
  private String                         mRequestAPIKey;

  private Catalogue                      mLastRetrievedCatalogue;
  private String                         mLastRetrievedEnvironmentAPIKey;
  private long                           mLastRetrievedElapsedRealtimeMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /****************************************************
   *
   * Returns the URL for a specific cover photo variant.
   *
   ****************************************************/
  static private URL getCoverPhotoURL( JSONArray variantsJSONArray, String variantId ) throws Exception
    {
    if ( variantsJSONArray == null ) throw ( new IllegalArgumentException( "No cover photos variant array supplied" ) );

    String defaultVariantURLString = null;

    for ( int index = 0; index < variantsJSONArray.length(); index ++ )
      {
      JSONObject variantJSONObject = variantsJSONArray.getJSONObject( index );

      String variant = variantJSONObject.getString( JSON_NAME_VARIANT_ID );
      String urlString = variantJSONObject.getString( JSON_NAME_URL );

      if ( variant.equals( variantId ) )
        {
        return ( new URL( urlString ) );
        }

      if ( variant.equals( COVER_PHOTO_VARIANT_ID_DEFAULT ) )  defaultVariantURLString = urlString;
      }

    // If we couldn't find the request variant - return the default
    return ( new URL( defaultVariantURLString ) );
    }


  /****************************************************
   *
   * Returns an instance of the manager.
   *
   ****************************************************/
  public static CatalogueLoader getInstance( Context context )
    {
    if ( sCatalogueLoaderInstance == null )
      {
      sCatalogueLoaderInstance = new CatalogueLoader( context );
      }

    return ( sCatalogueLoaderInstance );
    }


  /****************************************************
   *
   * Parses a JSON shipping cost.
   *
   ****************************************************/
  private static MultipleCurrencyAmounts parseShippingCost( JSONObject shippingCostJSONObject ) throws JSONException
    {
    return ( new MultipleCurrencyAmounts( shippingCostJSONObject ) );
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
  private static SingleCurrencyAmounts parseCost( JSONObject costJSONObject ) throws JSONException
    {
    Currency   currency        = Currency.getInstance( costJSONObject.getString( JSON_NAME_CURRENCY ) );
    BigDecimal amount          = new BigDecimal( costJSONObject.getString( JSON_NAME_AMOUNT ) );
    String     formattedAmount = costJSONObject.getString( JSON_NAME_FORMATTED_AMOUNT );


    // Check for an original amount

    String originalAmountString = costJSONObject.optString( JSON_NAME_ORIGINAL_AMOUNT, null );

    BigDecimal originalAmount = ( originalAmountString != null ? new BigDecimal( originalAmountString ) : null );


    return ( new SingleCurrencyAmounts( currency, amount, formattedAmount, originalAmount ) );
    }


  /****************************************************
   *
   * Parses a user journey type.
   *
   ****************************************************/
  private static UserJourneyType parseUserJourneyType( String userJourneyTypeJSONString )
    {
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
  private static MultipleCurrencyAmounts parseCost( JSONArray costJSONArray ) throws JSONException
    {
    MultipleCurrencyAmounts cost = new MultipleCurrencyAmounts();

    for ( int costIndex = 0; costIndex < costJSONArray.length(); costIndex ++ )
      {
      JSONObject costJSONObject = costJSONArray.getJSONObject( costIndex );

      cost.add( parseCost( costJSONObject ) );
      }

    return ( cost );
    }


  /****************************************************
   *
   * Parses JSON product options.
   *
   ****************************************************/
  private static List<ProductOption> parseProductOptions( JSONArray optionJSONArray )
    {
    List<ProductOption> optionList = new ArrayList<>();

    if ( optionJSONArray != null )
      {
      int optionCount = optionJSONArray.length();

      for ( int optionIndex = 0; optionIndex < optionCount; optionIndex ++ )
        {
        try
          {
          JSONObject optionJSONObject = optionJSONArray.getJSONObject( optionIndex );

          String     optionCode       = optionJSONObject.getString( JSON_NAME_OPTION_CODE );
          String     optionName       = optionJSONObject.getString( JSON_NAME_OPTION_NAME );

          JSONArray  valueJSONArray   = optionJSONObject.getJSONArray( JSON_NAME_OPTIONS );

          ProductOption productOption = new ProductOption( optionCode, optionName );

          int valueCount = valueJSONArray.length();

          for ( int valueIndex = 0; valueIndex < valueCount; valueIndex ++ )
            {
            JSONObject valueJSONObject = valueJSONArray.getJSONObject( valueIndex );

            String valueCode = valueJSONObject.getString( JSON_NAME_VALUE_CODE );
            String valueName = valueJSONObject.getString( JSON_NAME_VALUE_NAME );

            productOption.addValue( valueCode, valueName );
            }

          optionList.add( productOption );
          }
        catch ( JSONException je )
          {
          Log.e( LOG_TAG, "Unable to parse product options: " + optionJSONArray.toString(), je );
          }
        }
      }

    return ( optionList );
    }


  /****************************************************
   *
   * Parses JSON calendar assets.
   *
   ****************************************************/
  private static ArrayList<String> parseCalendarAssets( JSONArray assetsJSONArray )
    {
    ArrayList<String> imageURLStringList = new ArrayList<>();

    if ( assetsJSONArray != null )
      {
      int assetCount = assetsJSONArray.length();

      for ( int assetIndex = 0; assetIndex < assetCount; assetIndex ++ )
        {
        try
          {
          String imageURLString = assetsJSONArray.getString( assetIndex );

          imageURLStringList.add( imageURLString );
          }
        catch ( JSONException je )
          {
          Log.e( LOG_TAG, "Unable to parse calendar assets: " + assetsJSONArray.toString(), je );
          }
        }
      }

    return ( imageURLStringList );
    }


  /****************************************************
   *
   * Parses a JSON products array.
   *
   ****************************************************/
  static void parseProducts( JSONArray productJSONArray, Catalogue catalogue )
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

        if ( KiteSDK.DISPLAY_PRODUCT_JSON )
          {
          Log.d( LOG_TAG, "Product JSON:\n" + productJSONObject.toString() );
          }

        boolean                          active             = productJSONObject.optBoolean( JSON_NAME_ACTIVE,         false );
        boolean                          productActive      = productJSONObject.optBoolean( JSON_NAME_PRODUCT_ACTIVE, false );

        String                           productId          = productJSONObject .getString( JSON_NAME_PRODUCT_ID );
        String                           productName        = productJSONObject.getString( JSON_NAME_PRODUCT_NAME );
        String                           productDescription = productJSONObject.getString( JSON_NAME_DESCRIPTION );
        String                           productCategory    = productJSONObject.getString(JSON_NAME_CATEGORY);
        int                              imagesPerPage      = productJSONObject.optInt( JSON_NAME_IMAGES_PER_PAGE, DEFAULT_IMAGES_PER_PAGE );
        int                              gridCountX         = productJSONObject.optInt( JSON_NAME_GRID_COUNT_X, DEFAULT_GRID_SIZE );
        int                              gridCountY         = productJSONObject.optInt( JSON_NAME_GRID_COUNT_Y, DEFAULT_GRID_SIZE );
        MultipleCurrencyAmounts          cost               = parseCost( productJSONObject.getJSONArray( JSON_NAME_COST ) );
        boolean                          printInStore       = productJSONObject.optBoolean( JSON_NAME_PRINT_IN_STORE, false );
        MultipleDestinationShippingCosts shippingCosts      = parseShippingCosts( productJSONObject.getJSONObject( JSON_NAME_SHIPPING_COSTS ) );


        // Get the product detail

        JSONObject productDetailJSONObject = productJSONObject.getJSONObject( JSON_NAME_PRODUCT_DETAIL );

        URL                    groupImageURL        = new URL( productDetailJSONObject.getString( JSON_NAME_GROUP_IMAGE ) );
        //URL                    heroImageURL        = new URL( productDetailJSONObject.getString( JSON_NAME_PRODUCT_HERO_IMAGE ) );
        int                    labelColour          = parseColour( productDetailJSONObject.getJSONArray( JSON_NAME_LABEL_COLOUR ) );
        String                 groupLabel           = productDetailJSONObject.getString( JSON_NAME_GROUP_LABEL );
        ArrayList<URL>         imageURLList         = parseProductShots( productDetailJSONObject.getJSONArray( JSON_NAME_PRODUCT_SHOTS ) );
        //String                 productSubclass     = productDetailJSONObject.getString( JSON_NAME_PRODUCT_SUBCLASS );
        String                 productType          = productDetailJSONObject.getString( JSON_NAME_PRODUCT_TYPE );
        UserJourneyType        userJourneyType      = parseUserJourneyType( productDetailJSONObject.getString( JSON_NAME_PRODUCT_UI_CLASS ) );
        String                 productCode          = productDetailJSONObject.getString( JSON_NAME_PRODUCT_CODE );
        MultipleUnitSize       size                 = parseProductSize( productDetailJSONObject.getJSONObject( JSON_NAME_PRODUCT_SIZE ) );
        //float                  imageAspectRatio     = (float)productDetailJSONObject.optDouble( JSON_NAME_IMAGE_ASPECT_RATIO, Product.DEFAULT_IMAGE_ASPECT_RATIO );
        float imageAspectRatio = (float) productDetailJSONObject.optDouble(JSON_NAME_IMAGE_ASPECT_RATIO, Product.DEFAULT_IMAGE_ASPECT_RATIO);

        //Check for posters or photo-books (use journey as localisation changes other values except product type)
        if (userJourneyType.equals(UserJourneyType.POSTER)) {
          //For picture collages on posters the aspect ration of each image should be one (square images)
          //For ordinary posters the aspect ratio should be 0.707 (portrait ration)
          imageAspectRatio = ( gridCountX > 1 || gridCountY > 1 ) ? 1f : 0.707f;
        } else if (userJourneyType.equals(UserJourneyType.PHOTOBOOK)) {
          //Add image count to title (there are multiple products with the same name but different image count)
          productName += " (" + imagesPerPage + ")";
          //Determine photo-books layout by size
          SingleUnitSize sizeInCM = size.get(UnitOfLength.CENTIMETERS);
          imageAspectRatio = sizeInCM.getWidth() / sizeInCM.getHeight();
        }

        boolean                supportsTextOnBorder = productDetailJSONObject.optBoolean( JSON_NAME_SUPPORTS_TEXT_ON_BORDER, false );

        // Get the cover photo

        JSONArray coverPhotoVariantsJSONArray = productDetailJSONObject.getJSONArray( JSON_NAME_COVER_PHOTO_VARIANTS );

        URL coverPhotoURL = getCoverPhotoURL( coverPhotoVariantsJSONArray, COVER_PHOTO_VARIANT_ID_DEFAULT );


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


        // Get any background / highlight images

        String backgroundImageURLString = productDetailJSONObject.optString( JSON_NAME_BACKGROUND_IMAGE_URL, null );
        String highlightImageURLString  = productDetailJSONObject.optString( JSON_NAME_HIGHLIGHTS_URL,       null );

        // Get mask blend mode
        String maskBlendMode = productDetailJSONObject.optString( JSON_NAME_BLEND_MODE, null );

        // Get any product options

        JSONArray productOptionJSONArray = productDetailJSONObject.optJSONArray( JSON_NAME_SUPPORTED_OPTIONS );

        List<ProductOption> productOptionList = parseProductOptions( productOptionJSONArray );


        // Get any calendar assets

        JSONArray calendarAssetsJSONArray = productJSONObject.optJSONArray( JSON_NAME_CALENDAR_ASSETS );

        ArrayList<String> calendarImageURLStringList = parseCalendarAssets( calendarAssetsJSONArray );


        // Create the product and display it

        Product product = new Product( productId, productCode, productName, productType, productCategory, labelColour, userJourneyType, imagesPerPage )
                .setGridSize( gridCountX, gridCountY )
                .setCost( cost )
                .setDescription( productDescription )
                .setShippingCosts( shippingCosts )
                .setImageURLs( coverPhotoURL, imageURLList )
                .setLabelColour( labelColour )
                .setMask( maskURL, maskBleed )
                .setMaskBlendMode( maskBlendMode )
                .setSize( size )
                .setCreationImage( imageAspectRatio, imageBorder )
                .setProductOptions( productOptionList )
                .setFlag( Product.Flag.PRINT_IN_STORE, printInStore )
                .setFlag( Product.Flag.SUPPORTS_TEXT_ON_BORDER, supportsTextOnBorder )
                .setCalendarImages( calendarImageURLStringList );


        if ( backgroundImageURLString != null )
          {
          product.addUnderImage( new URL( backgroundImageURLString ) );
          }

        if ( highlightImageURLString != null )
          {
          product.addOverImage( new URL( highlightImageURLString ) );
          }


        if ( KiteSDK.DISPLAY_PRODUCTS )
          {
          Log.i( LOG_TAG, "-- Found product --" );
          Log.i( LOG_TAG, product.toLogString( groupLabel ) );
          }


        // Add the product to the catalogue. If it doesn't have a supported
        // user journey, then we add it has a discarded product.

        if ( ! active )
          {
          Log.i( LOG_TAG, "-- Product ( " + groupLabel + " / " + productName + " ) discarded: inactive --" );

          catalogue.addDiscardedProduct( product );
          }
        else if ( ! productActive )
          {
          Log.i( LOG_TAG, "-- Product ( " + groupLabel + " / " + productName + " ) discarded: product inactive --" );

          catalogue.addDiscardedProduct( product );
          }
        else if ( ! ProductCreationActivity.isSupported( userJourneyType ) )
          {
          Log.i( LOG_TAG, "-- Product ( " + groupLabel + " / " + productName + " ) discarded: user journey type not supported: " + userJourneyType + " --" );

          catalogue.addDiscardedProduct( product );
          }
        else if ( TEST_EMPTY_CATALOGUE )
          {
          Log.i( LOG_TAG, "-- Product ( " + groupLabel + " / " + productName + " ) discarded: testing empty catalogue --" );

          catalogue.addDiscardedProduct( product );
          }
        else
          {
          catalogue.addProduct( groupLabel, groupImageURL, product );
          }
        }
      catch ( Exception exception )
        {
        // The product JSON is getting massive now, so display the exception separately
        Log.e( LOG_TAG, "Unable to parse JSON product: " + productJSONObject );
        Log.e( LOG_TAG, "Exception:", exception );

        // Ignore individual errors - try and get as many products as possible
        }
      }


    if ( DISPLAY_PRE_CACHING_INFO ) catalogue.displayPreCachingInfo();
    }


  ////////// Constructor(s) //////////

  // Constructor is private to ensure it is a singleton
  private CatalogueLoader( Context context )
    {
    mContext      = context.getApplicationContext();
    mHandler      = new Handler();
    mConsumerList = new LinkedList<>();
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
        JSONObject errorJSONObject = jsonData.getJSONObject( KiteAPIRequest.ERROR_RESPONSE_JSON_OBJECT_NAME );
        String     errorMessage    = errorJSONObject.getString( KiteAPIRequest.ERROR_RESPONSE_MESSAGE_JSON_NAME );
        String     errorCode       = errorJSONObject.getString( KiteAPIRequest.ERROR_RESPONSE_CODE_JSON_NAME );

        Exception exception = new KiteSDKException( errorMessage );

        postErrorToConsumers( exception );
        }
      catch ( JSONException je )
        {
        postErrorToConsumers( je );
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
    postErrorToConsumers( exception );
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
   * @return true, if the catalogue is immediately available.
   * @return false, if a catalogue retrieval must be completed
   *         before it can be returned.
   *
   ****************************************************/
  public boolean requestCatalogue( long maximumAgeMillis, ICatalogueConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "requestCatalogue( maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + " )" );


    // If there is currently a retrieval in progress, always wait for the result

    if ( mHTTPJSONRequest != null )
      {
      mConsumerList.addLast( consumer );

      return ( false );
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
        postCatalogueToConsumer( mLastRetrievedCatalogue, consumer );

        return ( true );
        }
      }


    // We need to perform a new retrieval. Create a new request, and consumer list containing the consumer.

    String url = String.format( TEMPLATE_REQUEST_FORMAT_STRING, KiteSDK.getInstance( mContext ).getAPIEndpoint() );

    mHTTPJSONRequest = new KiteAPIRequest( mContext, KiteAPIRequest.HttpMethod.GET, url, null, null );
    mConsumerList.addLast( consumer );
    mRequestAPIKey = currentAPIKey;


    // Kick off the retrieval
    mHTTPJSONRequest.start( this );

    return ( false );
    }


  /****************************************************
   *
   * Retrieves a catalogue, filtered by the supplied product
   * ids. If there are no products ids, or the product ids don't
   * match any actual products - the full catalogue is returned.
   *
   * @param filterProductIds       A string array of product ids for filtering.
   *
   * @param maximumAgeMillis The maximum permitted time in milliseconds
   *                         since the last retrieval. If the value supplied
   *                         is < 0, there is no maximum age.
   * @param consumer         The sync listener for the result.
   *
   ****************************************************/
  public void requestCatalogue( long maximumAgeMillis, String[] filterProductIds, ICatalogueConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "getCatalogue( filterProductIds = " + filterProductIds + ", maximumAgeMillis = " + maximumAgeMillis + ", consumer = " + consumer + " )" );

    requestCatalogue( maximumAgeMillis, new CatalogueFilterConsumer( filterProductIds, consumer ) );
    }


  /****************************************************
   *
   * Retrieves a catalogue.
   *
   * @param consumer        The sync listener for the result.
   *
   * @return true, if the catalogue is immediately available.
   * @return false, if a catalogue retrieval must be completed
   *         before it can be returned.
   *
   ****************************************************/
  public boolean requestCatalogue( ICatalogueConsumer consumer )
    {
    if ( DISPLAY_DEBUGGING ) Log.d( LOG_TAG, "requestCatalogue( consumer = " + consumer + " )" );

    return ( requestCatalogue( ANY_AGE_OK, consumer ) );
    }


  /****************************************************
   *
   * Called when we have catalogue data to parse.
   *
   ****************************************************/
  public void onCatalogue( JSONObject jsonData )
    {
    try
      {
      // Create a new catalogue

      Catalogue catalogue = new Catalogue();


      // Iterate through the top-level keys

      Iterator<String> topLevelKeysIterator = jsonData.keys();

      while ( topLevelKeysIterator.hasNext() )
        {
        String topLevelKey = topLevelKeysIterator.next();

        if ( topLevelKey == null || topLevelKey.trim().equals( "" ) ) continue;


        // Determine what type of data this is

        if ( topLevelKey.equals( JSON_NAME_USER_CONFIG ) )
          {
          ///// User config data /////

          JSONObject userConfigData = jsonData.getJSONObject( topLevelKey );

          Log.i( LOG_TAG, "Storing user config data: " + userConfigData.toString() );

          catalogue.setUserConfigData( userConfigData );
          }

        else if ( topLevelKey.equals( JSON_NAME_PRODUCT_ARRAY ) )
          {
          ///// Product data /////

          // Try to get a set of products, then parse them.

          JSONArray productsJSONArray = jsonData.getJSONArray( JSON_NAME_PRODUCT_ARRAY );

          parseProducts( productsJSONArray, catalogue );
          }

        else if ( topLevelKey.equals( JSON_NAME_PAYPAL_SUPPORTED_CURRENCIES ) )
          {
          ///// PayPal supported currencies /////

          JSONArray payPalSupportedCurrenciesJSONArray = jsonData.getJSONArray( JSON_NAME_PAYPAL_SUPPORTED_CURRENCIES );

          for ( int index = 0; index < payPalSupportedCurrenciesJSONArray.length(); index ++ )
            {
            String   currencyCode = payPalSupportedCurrenciesJSONArray.getString( index );
            Currency currency     = Currency.getInstance( currencyCode );

            if ( currency != null ) catalogue.addPayPalSupportedCurrency( currencyCode );
            else                    Log.e( LOG_TAG, "Unknown currency code: " + currencyCode );
            }
          }

        else
          {
          ///// Custom data /////

          // Custom data could either be an object or an array

          Object customData = jsonData.get( topLevelKey );

          if ( customData instanceof JSONObject )
            {
            Log.i( LOG_TAG, "Storing custom object: " + topLevelKey + " = " + customData.toString() );

            catalogue.setCustomObject( topLevelKey, jsonData.getJSONObject( topLevelKey ) );
            }
          else if ( customData instanceof JSONArray )
            {
            Log.i( LOG_TAG, "Discarding custom array: " + topLevelKey + " = " + customData.toString() );
            }
          else
            {
            Log.i( LOG_TAG, "Discarding custom data: " + topLevelKey + " = " + customData.toString() );
            }
          }

        }


      // Save the query result
      mLastRetrievedCatalogue             = catalogue;
      mLastRetrievedEnvironmentAPIKey     = mRequestAPIKey;
      mLastRetrievedElapsedRealtimeMillis = SystemClock.elapsedRealtime();

      // Choose and lock the currency
      KiteSDK.getInstance( mContext ).chooseAndLockCurrency( catalogue );

      // Expire old cached product images
      AssetHelper.clearProductAssets( mContext, catalogue.getAllProductImageURLs() );

      postCatalogueToConsumers( catalogue );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error creating catalogue", je );

      postErrorToConsumers( je );
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
   * Posts the catalogue to a consumer.
   *
   ****************************************************/
  private void postCatalogueToConsumer( Catalogue catalogue, ICatalogueConsumer consumer )
    {
    CatalogueCallbackRunnable callbackRunnable = new CatalogueCallbackRunnable( catalogue, consumer );

    mHandler.post( callbackRunnable );
    }


  /****************************************************
   *
   * Posts the catalogue to the consumers.
   *
   ****************************************************/
  private void postCatalogueToConsumers( Catalogue catalogue )
    {
    ICatalogueConsumer consumer;

    while ( ( consumer = mConsumerList.pollFirst() ) != null )
      {
      postCatalogueToConsumer( catalogue, consumer );
      }


    // Clear the request
    mHTTPJSONRequest = null;
    }


  /****************************************************
   *
   * Posts an error to a consumer.
   *
   ****************************************************/
  private void postErrorToConsumer( Exception exception, ICatalogueConsumer consumer )
    {
    ErrorCallbackRunnable callbackRunnable = new ErrorCallbackRunnable( exception, consumer );

    mHandler.post( callbackRunnable );
    }


  /****************************************************
   *
   * Posts an error to the consumers.
   *
   ****************************************************/
  private void postErrorToConsumers( Exception exception )
    {
    // Go through each of the consumers and notify them of the error

    ICatalogueConsumer consumer;

    while ( ( consumer = mConsumerList.pollFirst() ) != null )
      {
      postErrorToConsumer( exception, consumer );
      }


    // Clear the request
    mHTTPJSONRequest = null;
    }


  /****************************************************
   *
   * Cancels any outstanding request.
   *
   ****************************************************/
  public void cancelRequests()
    {
    if ( mHTTPJSONRequest != null )
      {
      mHTTPJSONRequest.cancel();

      mHTTPJSONRequest = null;
      }

    if ( mConsumerList != null ) mConsumerList.clear();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * This class consumes a catalogue, but then filters it
   * down by product ids, before returning it to the ultimate
   * consumer.
   *
   *****************************************************/
  public class CatalogueFilterConsumer implements ICatalogueConsumer
    {
    private String[]           mProductIds;
    private ICatalogueConsumer mConsumer;


    CatalogueFilterConsumer( String[] productsIds, ICatalogueConsumer consumer )
      {
      mProductIds = productsIds;
      mConsumer   = consumer;
      }


    /*****************************************************
     *
     * Called when a request completes successfully.
     *
     *****************************************************/
    @Override
    public void onCatalogueSuccess( Catalogue catalogue )
      {
      // See if we need to do any filtering

      if ( mProductIds != null && mProductIds.length > 0 )
        {
        Catalogue filteredCatalogue = catalogue.createFiltered( mProductIds );

        // Only use the filtered catalogue if it contains at least one product.
        // Otherwise stick with the original.
        if ( filteredCatalogue.getProductCount() > 0 )
          {
          catalogue = filteredCatalogue;
          }
        }

      mConsumer.onCatalogueSuccess( catalogue );
      }


    /*****************************************************
     *
     * Called when a request is cancelled.
     *
     *****************************************************/
    @Override
    public void onCatalogueCancelled()
      {
      // Simply pass the error through to the consumer
      mConsumer.onCatalogueCancelled();
      }


    /*****************************************************
     *
     * Called when a request results in an error.
     *
     *****************************************************/
    @Override
    public void onCatalogueError( Exception exception )
      {
      // Simply pass the error through to the consumer
      mConsumer.onCatalogueError( exception );
      }

    }


  /*****************************************************
   *
   * Passes the catalogue to a consumer.
   *
   *****************************************************/
  private class CatalogueCallbackRunnable implements Runnable
    {
    Catalogue        mCatalogue;
    ICatalogueConsumer mConsumer;


    CatalogueCallbackRunnable( Catalogue catalogue, ICatalogueConsumer consumer )
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
    Exception           mException;
    ICatalogueConsumer  mConsumer;


    ErrorCallbackRunnable( Exception exception, ICatalogueConsumer consumer )
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
