/*****************************************************
 *
 * Analytics.java
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

package ly.kite.analytics;


///// Import(s) /////

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ly.kite.BuildConfig;
import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.pricing.OrderPricing;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.catalogue.PrintJob;
import ly.kite.catalogue.PrintOrder;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.SingleCurrencyAmount;


///// Class Declaration /////

/*****************************************************
 *
 * This class performs analytics processing and uploads
 * data using the appropriate analytics agent.
 *
 *****************************************************/
public class Analytics
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                                       = "Analytics";

  private static final String SHARED_PREFERENCES_NAME                        = "kite_sdk_analytics_shared_prefs";

  private static final String  EVENT_NAME_SDK_LOADED                         = "Kite Loaded";
  private static final String  EVENT_NAME_PRODUCT_SELECTION_SCREEN_VIEWED    = "Product Selection Screen Viewed";
  private static final String  EVENT_NAME_PRODUCT_OVERVIEW_SCREEN_VIEWED     = "Product Description Screen Viewed";
  private static final String  EVENT_NAME_CREATE_PRODUCT_SCREEN_VIEWED       = "Review Screen Viewed";
  private static final String  EVENT_NAME_PRODUCT_ORDER_REVIEW_SCREEN_VIEWED = "Product Order Review Screen";
  private static final String  EVENT_NAME_SHIPPING_SCREEN_VIEWED             = "Shipping Screen Viewed";
  private static final String  EVENT_NAME_CREATE_PAYMENT_SCREEN_VIEWED       = "Payment Screen Viewed";
  private static final String  EVENT_NAME_PAYMENT_COMPLETED                  = "Payment Completed";
  private static final String  EVENT_NAME_ORDER_SUBMISSION                   = "Print Order Submission";


  private static final String  JSON_PROPERTY_NAME_EVENT                      = "event";
  private static final String  JSON_PROPERTY_NAME_PROPERTIES                 = "properties";

  private static final String  JSON_PROPERTY_NAME_API_TOKEN                = "token";
  private static final String  JSON_PROPERTY_NAME_UNIQUE_USER_ID           = "distinct_id";
  private static final String  JSON_PROPERTY_NAME_APP_PACKAGE              = "App Package";
  private static final String  JSON_PROPERTY_NAME_APP_NAME                 = "App Name";
  private static final String  JSON_PROPERTY_NAME_APP_VERSION              = "App Version";
  private static final String  JSON_PROPERTY_NAME_PLATFORM                 = "platform";
  private static final String  JSON_PROPERTY_NAME_PLATFORM_VERSION         = "platform version";
  private static final String  JSON_PROPERTY_NAME_MODEL                    = "model";
  private static final String  JSON_PROPERTY_NAME_SCREEN_HEIGHT            = "Screen Height";
  private static final String  JSON_PROPERTY_NAME_SCREEN_WIDTH             = "Screen Width";
  private static final String  JSON_PROPERTY_NAME_ENVIRONMENT              = "Environment";
  private static final String  JSON_PROPERTY_NAME_API_KEY                  = "API Key";
  private static final String  JSON_PROPERTY_NAME_KITE_SDK_VERSION         = "Kite SDK Version";
  private static final String  JSON_PROPERTY_NAME_LOCALE_COUNTRY           = "Locale Country";

  private static final String  JSON_PROPERTY_NAME_ENTRY_POINT              = "Entry Point";
  private static final String  JSON_PROPERTY_NAME_PRODUCT_NAME             = "Product Name";

  private static final String  JSON_PROPERTY_NAME_EMAIL                    = "email";
  private static final String  JSON_PROPERTY_NAME_PHONE                    = "phone";

  private static final String  JSON_PROPERTY_NAME_PAYMENT_METHOD           = "Payment Method";
  private static final String  JSON_PROPERTY_NAME_PRINT_ORDER_ID           = "Print Order Id";
  private static final String  JSON_PROPERTY_NAME_PRINT_SUBMISSION_SUCCESS = "Print Submission Success";
  private static final String  JSON_PROPERTY_NAME_PRINT_SUBMISSION_ERROR   = "Print Submission Error";
  private static final String  JSON_PROPERTY_NAME_PRODUCT                  = "Product";
  private static final String  JSON_PROPERTY_NAME_PROOF_OF_PAYMENT         = "Proof of Payment";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_EMAIL           = "Shipping Email";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_PHONE           = "Shipping Phone";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_SCREEN_VARIANT  = "Shipping Screen Variant";
  private static final String  JSON_PROPERTY_NAME_SHOW_PHONE_ENTRY_FIELD   = "Showing Phone Entry Field";
  private static final String  JSON_PROPERTY_NAME_VOUCHER_CODE             = "Voucher Code";

  private static final String  JSON_PROPERTY_NAME_COST                     = "Cost";
  private static final String  JSON_PROPERTY_NAME_JOB_COUNT                = "Job Count";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_RECIPIENT       = "Shipping Recipient";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_LINE_1          = "Shipping Line 1";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_LINE_2          = "Shipping Line 2";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_CITY            = "Shipping City";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_COUNTY          = "Shipping County";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_POSTCODE        = "Shipping Postcode";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_COUNTRY         = "Shipping Country";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE2   = "Shipping Country Code2";
  private static final String  JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE3   = "Shipping Country Code3";

  public  static final String  ENTRY_POINT_JSON_PROPERTY_VALUE_HOME_SCREEN = "Home Screen";

  private static final String  PLATFORM_JSON_PROPERTY_VALUE_ANDROID        = "Android";

  public  static final String  VARIANT_JSON_PROPERTY_VALUE_CLASSIC_PLUS_ADDRESS_SEARCH = "Classic + Address Search";

  public  static final String  PAYMENT_METHOD_PAYPAL                       = "PayPal";
  public  static final String  PAYMENT_METHOD_CREDIT_CARD                  = "Credit Card";

  private static final String  JSON_PROPERTY_VALUE_YES                     = "Yes";
  private static final String  JSON_PROPERTY_VALUE_NO                      = "No";

  private static final String  JSON_PROPERTY_VALUE_TRUE                    = "True";
  private static final String  JSON_PROPERTY_VALUE_FALSE                   = "False";


  ////////// Static Variable(s) //////////

  private static Analytics  sAnalytics;


  ////////// Member Variable(s) //////////

  private Context     mContext;

  private HashMap<String,Object> mCachedPropertiesMap;
  private IAnalyticsEventCallback mCachedEventCallback;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns a singleton instance of this class.
   *
   *****************************************************/
  public static Analytics getInstance( Context context )
    {
    if ( sAnalytics == null )
      {
      sAnalytics = new Analytics( context );
      }

    return ( sAnalytics );
    }


  /*****************************************************
   *
   * Returns a non-null string.
   *
   *****************************************************/
  private static String nonNullString( String originalString )
    {
    if ( originalString == null ) return ( String.valueOf( "" ) );

    return ( originalString );
    }


  /*****************************************************
   *
   * Adds the properties of a print order to a JSON object.
   *
   *****************************************************/
  private static void addToJSON( PrintOrder printOrder, JSONObject jsonObject )
    {
    try
      {
      ///// Product names /////

      List<PrintJob> printJobList = printOrder.getJobs();

      JSONArray productNameJSONArray = new JSONArray();

      int jobCount = 0;

      if ( printJobList != null )
        {
        for ( PrintJob printJob : printJobList )
          {
          jobCount ++;

          Product product = printJob.getProduct();

          if ( product != null ) productNameJSONArray.put( product.getName() );
          }
        }

      jsonObject.put( JSON_PROPERTY_NAME_PRODUCT, productNameJSONArray );


      // Proof of payment

      jsonObject.put( JSON_PROPERTY_NAME_PROOF_OF_PAYMENT, printOrder.getProofOfPayment() );


      ///// Print submission /////

      Exception lastPrintSubmissionError = printOrder.getLastPrintSubmissionError();
      String receipt = printOrder.getReceipt();

      if ( lastPrintSubmissionError != null )
        {
        jsonObject.put( JSON_PROPERTY_NAME_PRINT_SUBMISSION_SUCCESS, JSON_PROPERTY_VALUE_FALSE );
        jsonObject.put( JSON_PROPERTY_NAME_PRINT_SUBMISSION_ERROR, lastPrintSubmissionError.toString() );
        }
      else if ( receipt != null )
        {
        jsonObject.put( JSON_PROPERTY_NAME_PRINT_ORDER_ID, receipt );
        jsonObject.put( JSON_PROPERTY_NAME_PRINT_SUBMISSION_SUCCESS, JSON_PROPERTY_VALUE_TRUE );
        jsonObject.put( JSON_PROPERTY_NAME_PRINT_SUBMISSION_ERROR, JSON_PROPERTY_VALUE_FALSE );
        }


      // Promo code

      String promoCode = printOrder.getPromoCode();

      if ( promoCode != null ) jsonObject.put( JSON_PROPERTY_NAME_VOUCHER_CODE, promoCode );


      ///// User data /////

      JSONObject userDataJSONObject = printOrder.getUserData();

      if ( userDataJSONObject != null )
        {
        String email = userDataJSONObject.getString( JSON_PROPERTY_NAME_EMAIL );
        String phone = userDataJSONObject.getString( JSON_PROPERTY_NAME_PHONE );

        if ( email != null ) jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_EMAIL, email );
        if ( phone != null ) jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_PHONE, phone );
        }


      ///// Shipping address /////

      Address shippingAddress = printOrder.getShippingAddress();

      if ( shippingAddress != null )
        {
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_RECIPIENT, nonNullString( shippingAddress.getRecipientName() ) );
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_LINE_1,    nonNullString( shippingAddress.getLine1() ) );
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_LINE_2,    nonNullString( shippingAddress.getLine2() ) );
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_CITY,      nonNullString( shippingAddress.getCity() ) );
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTY,    nonNullString( shippingAddress.getStateOrCounty() ) );
        jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_POSTCODE,  nonNullString( shippingAddress.getZipOrPostalCode() ) );

        Country country = shippingAddress.getCountry();

        if ( country != null )
          {
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY,       nonNullString( country.displayName() ) );
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE2, nonNullString( country.iso2Code() ) );
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE3, nonNullString( country.iso3Code() ) );
          }
        else
          {
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY,       "" );
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE2, "" );
          jsonObject.put( JSON_PROPERTY_NAME_SHIPPING_COUNTRY_CODE3, "" );
          }
        }


      ///// Cost /////

      OrderPricing           orderPricing = printOrder.getOrderPricing();
      MultipleCurrencyAmount totalCost;
      SingleCurrencyAmount   totalCostInGBP;

      if (   orderPricing                                   != null &&
           ( totalCost      = orderPricing.getTotalCost() ) != null &&
           ( totalCostInGBP = totalCost.get( "GBP" )      ) != null )
        {
        jsonObject.put( JSON_PROPERTY_NAME_COST, totalCostInGBP.getAmount() );
        }


      // Job count

      jsonObject.put( JSON_PROPERTY_NAME_JOB_COUNT, jobCount );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }
    }


  ////////// Constructor(s) //////////

  private Analytics( Context context )
    {
    mContext = context;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Gets the integrating applications analytics event
   * callback if any, else returns null. The event
   * callback class is registered by the integrating
   * application as a string resource
   * (R.string.analytics_event_callback_class_name) which we
   * instantiate via reflection & cache for future use
   *
   *****************************************************/
  private IAnalyticsEventCallback getEventCallback()
    {
    if ( mCachedEventCallback == null )
      {
      // Load registered analytics event callback (if any) via reflection
      String callbackClassName = mContext.getString(R.string.analytics_event_callback_class_name);
      try
        {
        Class<?> clazz = Class.forName( callbackClassName );
        Constructor<?> ctor = clazz.getConstructor( Context.class );
        mCachedEventCallback = (IAnalyticsEventCallback) ctor.newInstance( new Object[] { mContext } );
      } catch ( Exception ex )
        {
        mCachedEventCallback = new NullAnalyticsEventCallback(mContext);
        }
      }

    return mCachedEventCallback;
    }

  /*****************************************************
   *
   * Returns a JSON object containing the standard properties
   * that we upload with every event.
   *
   * The same properties are uploaded with every analytics
   * event, so it makes sense to cache them. However, we don't
   * want to return the actual cached properties object, as
   * every time we add more properties, they leak into all the
   * events.
   *
   * So we create the properties as a map, and then copy them
   * into a new JSON object.
   *
   *****************************************************/
  private JSONObject getPropertiesJSONObject()
    {
    // If we haven't already create the properties object - do
    // so now.

    if ( mCachedPropertiesMap == null )
      {
      mCachedPropertiesMap = new HashMap<>();

      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_API_TOKEN,        MixpanelAgent.API_TOKEN );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_UNIQUE_USER_ID,   KiteSDK.getInstance( mContext ).getUniqueUserId() );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_APP_PACKAGE,      mContext.getPackageName() );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_APP_NAME,         mContext.getString( mContext.getApplicationInfo().labelRes ) );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_APP_VERSION,      BuildConfig.VERSION_NAME );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_PLATFORM,         PLATFORM_JSON_PROPERTY_VALUE_ANDROID );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_PLATFORM_VERSION, Build.VERSION.RELEASE );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_MODEL,            Build.MODEL );


      DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_SCREEN_WIDTH,     displayMetrics.widthPixels );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_SCREEN_HEIGHT,    displayMetrics.heightPixels );


      KiteSDK              kiteSDK     = KiteSDK.getInstance( mContext );
      KiteSDK.Environment  environment = kiteSDK.getEnvironment();

      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_ENVIRONMENT,      environment.getName() );


      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_API_KEY,          kiteSDK.getAPIKey() );


      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_KITE_SDK_VERSION, KiteSDK.SDK_VERSION );
      mCachedPropertiesMap.put( JSON_PROPERTY_NAME_LOCALE_COUNTRY,   Country.getInstance( Locale.getDefault() ).displayName() );
      }


    return ( new JSONObject( mCachedPropertiesMap ) );
    }


  /*****************************************************
   *
   * Tracks an event.
   *
   *****************************************************/
  private void trackEvent( String eventName, JSONObject propertiesJSONObject )
    {
    // Create a new JSON object:
    //   - The event is a top level property
    //   - The properties go under the "property" name
    //

    JSONObject eventJSONObject = new JSONObject();

    try
      {
      eventJSONObject.put( JSON_PROPERTY_NAME_EVENT, eventName );
      eventJSONObject.put( JSON_PROPERTY_NAME_PROPERTIES, propertiesJSONObject );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }


    // Make a request through the agent
    MixpanelAgent.getInstance( mContext ).trackEvent( eventJSONObject );
    }


  /*****************************************************
   *
   * Tracks an event.
   *
   *****************************************************/
  private void trackEvent( String eventName )
    {
    trackEvent( eventName, getPropertiesJSONObject() );
    }


  /*****************************************************
   *
   * Called when the user enters the SDK for shopping.
   *
   *****************************************************/
  public void trackSDKLoaded( String entryPoint )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_ENTRY_POINT, entryPoint );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_SDK_LOADED, propertiesJSONObject );
    getEventCallback().onSDKLoaded( entryPoint );
    }


  /*****************************************************
   *
   * Called when the user enters the product list screen.
   *
   *****************************************************/
  public void trackProductSelectionScreenViewed()
    {
    trackEvent( EVENT_NAME_PRODUCT_SELECTION_SCREEN_VIEWED );
    getEventCallback().onProductSelectionScreenViewed();
    }


  /*****************************************************
   *
   * Called when the user enters the product overview screen.
   *
   *****************************************************/
  public void trackProductOverviewScreenViewed( Product product )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_PRODUCT_NAME, product.getName() );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_PRODUCT_OVERVIEW_SCREEN_VIEWED, propertiesJSONObject );
    getEventCallback().onProductOverviewScreenViewed(product);
    }


  /*****************************************************
   *
   * Called when the user enters the product creation/add photo screen.
   *
   *****************************************************/
  public void trackCreateProductScreenViewed( Product product )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_PRODUCT_NAME, product.getName() );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_CREATE_PRODUCT_SCREEN_VIEWED, propertiesJSONObject );
    getEventCallback().onCreateProductScreenViewed(product);
    }


  /*****************************************************
   *
   * Called when the user enters the product order review screen
   *
   *****************************************************/
  public void trackProductOrderReviewScreenViewed( Product product )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_PRODUCT_NAME, product.getName() );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_PRODUCT_ORDER_REVIEW_SCREEN_VIEWED, propertiesJSONObject );
    getEventCallback().onProductOrderReviewScreenViewed( product );
    }

  /*****************************************************
   *
   * Called when the user enters the shipping screen.
   *
   *****************************************************/
  public void trackShippingScreenViewed( PrintOrder printOrder, String variant, boolean showPhoneEntryField )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    addToJSON( printOrder, propertiesJSONObject );

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_SHIPPING_SCREEN_VARIANT, variant );
      propertiesJSONObject.put( JSON_PROPERTY_NAME_SHOW_PHONE_ENTRY_FIELD,  ( showPhoneEntryField ? JSON_PROPERTY_VALUE_YES : JSON_PROPERTY_VALUE_NO ) );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_SHIPPING_SCREEN_VIEWED, propertiesJSONObject );
    getEventCallback().onShippingScreenViewed(printOrder, variant, showPhoneEntryField);
    }


  /*****************************************************
   *
   * Called when the user enters the payment screen.
   *
   *****************************************************/
  public void trackPaymentScreenViewed( PrintOrder printOrder )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    addToJSON( printOrder, propertiesJSONObject );

    trackEvent( EVENT_NAME_CREATE_PAYMENT_SCREEN_VIEWED, propertiesJSONObject );
    getEventCallback().onPaymentScreenViewed(printOrder);
    }


  /*****************************************************
   *
   * Called when the payment is completed.
   *
   *****************************************************/
  public void trackPaymentCompleted( PrintOrder printOrder, String paymentMethod )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    addToJSON( printOrder, propertiesJSONObject );

    try
      {
      propertiesJSONObject.put( JSON_PROPERTY_NAME_PAYMENT_METHOD, paymentMethod );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Error setting JSON properties", je );
      }

    trackEvent( EVENT_NAME_PAYMENT_COMPLETED, propertiesJSONObject );
    getEventCallback().onPaymentCompleted(printOrder, paymentMethod);
    }


  /*****************************************************
   *
   * Called when the order is submitted.
   *
   *****************************************************/
  public void trackOrderSubmission( PrintOrder printOrder )
    {
    JSONObject propertiesJSONObject = getPropertiesJSONObject();

    addToJSON( printOrder, propertiesJSONObject );

    trackEvent( EVENT_NAME_ORDER_SUBMISSION, propertiesJSONObject );
    getEventCallback().onOrderSubmission(printOrder);
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
