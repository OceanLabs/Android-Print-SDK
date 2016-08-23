/*****************************************************
 *
 * PricingAgent.java
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

package ly.kite.pricing;


///// Import(s) /////

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.util.ACache;
import ly.kite.util.HTTPJSONRequest;
import ly.kite.api.KiteAPIRequest;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an agent for the price API endpoint.
 *
 *****************************************************/
public class PricingAgent extends ACache<String,OrderPricing,PricingAgent.ConsumerHolder>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                        = "PricingAgent";

  static private final String  PRICING_ENDPOINT_FORMAT_STRING = "%s/price/";

  static private final String  JSON_NAME_BASKET                = "basket";
  static private final String  JSON_NAME_SHIPPING_COUNTRY_CODE = "shipping_country_code";
  static private final String  JSON_NAME_PROMO_CODE            = "promo_code";


  ////////// Static Variable(s) //////////

  static private PricingAgent  sPricingAgent;


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public PricingAgent getInstance()
    {
    if ( sPricingAgent == null )
      {
      sPricingAgent = new PricingAgent();
      }

    return ( sPricingAgent );
    }


  ////////// Constructor(s) //////////

  private PricingAgent()
    {
    super();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Retrieves the price information for an order. The promo
   * code is passed in separately because we don't want to
   * store it in the order until we know it's valid.
   *
   *****************************************************/
  public OrderPricing requestPricing( Context context, Order order, String promoCode, List<String> payPalSupportedCurrencyCodeList, IPricingConsumer consumer, int requestId )
    {
    // Get the request body first, because we also use it as the caching key

    String requestBodyString = getRequestBody( context, order, promoCode );

    if ( KiteSDK.DEBUG_PRICING ) Log.d( LOG_TAG, "Request body:\n" + requestBodyString );


    // Construct the request URL first, because we also use it as the caching key.
    //String requestURLString = getRequestURLString( context, order, promoCode );


    // If we already have the price information cached from a previous retrieval - return it now

    OrderPricing cachedPricing = getCachedValue( requestBodyString );

    if ( cachedPricing != null ) return ( cachedPricing );


    // We don't already have the price information. If there is already a request running -
    // add this consumer to the list of consumers waiting for the result. Otherwise start
    // a new request.

    if ( ! registerForValue( requestBodyString, new ConsumerHolder( consumer, requestId ) ) )
      {
      KiteSDK kiteSDK = KiteSDK.getInstance( context );

      String requestURLString = String.format( PRICING_ENDPOINT_FORMAT_STRING, kiteSDK.getAPIEndpoint() );

      KiteAPIRequest request = new KiteAPIRequest( context, KiteAPIRequest.HttpMethod.POST, requestURLString, null, requestBodyString );

      request.start( new PriceRequestListener( context, requestBodyString, payPalSupportedCurrencyCodeList ) );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Returns the pricing request JSON body.
   *
   * {
   * "basket":
   *   [
   *     {
   *     "country_code": "GBR",
   *     "job_id": "48CD1DFA-254B-4FF9-A81C-1FB7A854C509",
   *     "quantity": 1,
   *     "template_id":"i6splus_case"
   *     }
   *   ],
   * "pay_in_store": 0,
   * "payment_gateway": "APPLE_PAY",
   * "promo_code": "",
   * "ship_to_store": 0,
   * "shipping_country_code": "GBR"
   * }
   *****************************************************/
  private String getRequestBody( Context context, Order order, String promoCode )
    {
    JSONObject bodyJSONObject = new JSONObject();


    Address  shippingAddress;
    Country  country;
    String   shippingCountryCode;

    if ( ( shippingAddress = order.getShippingAddress()   ) != null &&
         ( country         = shippingAddress.getCountry() ) != null )
      {
      shippingCountryCode = country.iso3Code();
      }
    else
      {
      shippingCountryCode = Country.getInstance().iso3Code();
      }

    if ( promoCode == null ) promoCode = "";


    JSONArray basketJSONArray = order.asBasketJSONArray( shippingCountryCode );


    try
      {
      bodyJSONObject.put( JSON_NAME_BASKET,                basketJSONArray );
      bodyJSONObject.put( JSON_NAME_SHIPPING_COUNTRY_CODE, shippingCountryCode );
      bodyJSONObject.put( JSON_NAME_PROMO_CODE,            promoCode );


      // Add in any additional parameters

      HashMap<String,String> additionalParametersMap = order.getAdditionalParameters();

      if ( additionalParametersMap != null )
        {
        for ( String parameterName : additionalParametersMap.keySet() )
          {
          bodyJSONObject.put( parameterName, additionalParametersMap.get( parameterName ) );
          }
        }


      return ( bodyJSONObject.toString() );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Unable to create body JSON", je );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Retrieves the price information for an order. The promo
   * code is passed in separately because we don't want to
   * store it in the order until we know it's valid.
   *
   *****************************************************/
  public OrderPricing requestPricing( Context context, Order order, String promoCode, List<String> payPalSupportedCurrencyCodeList, IPricingConsumer consumer )
    {
    return ( requestPricing( context, order, promoCode, payPalSupportedCurrencyCodeList, consumer, 0 ) );
    }


  /*****************************************************
   *
   * Distributes a value to a callback.
   *
   *****************************************************/
  protected void onValueAvailable( OrderPricing pricing, ConsumerHolder consumerHolder )
    {
    consumerHolder.consumer.paOnSuccess( consumerHolder.requestId, pricing );
    }


  /*****************************************************
   *
   * Distributes an error to a consumer. The callback will
   * never be null.
   *
   *****************************************************/
  protected void onError( Exception exception, ConsumerHolder consumerHolder )
    {
    consumerHolder.consumer.paOnError( consumerHolder.requestId, exception );
    }


  ////////// Inner Class(es) //////////

  public interface IPricingConsumer
    {
    public void paOnSuccess( int requestId, OrderPricing pricing );
    public void paOnError( int requestId, Exception exception );
    }


  /*****************************************************
   *
   * A holder for the consumer and request id.
   *
   *****************************************************/
  class ConsumerHolder
    {
    IPricingConsumer  consumer;
    int               requestId;

    ConsumerHolder( IPricingConsumer consumer, int requestId )
      {
      this.consumer  = consumer;
      this.requestId = requestId;
      }
    }


  /*****************************************************
   *
   * The callback for a price request.
   *
   *****************************************************/
  private class PriceRequestListener implements HTTPJSONRequest.IJSONResponseListener
    {
    private Context       mContext;
    private String        mRequestBodyString;
    private List<String>  mPayPalSupportedCurrencyCodeList;


    PriceRequestListener( Context context, String requestBodyString, List<String> payPalSupportedCurrencyCodeList )
      {
      mContext                         = context;
      mRequestBodyString               = requestBodyString;
      mPayPalSupportedCurrencyCodeList = payPalSupportedCurrencyCodeList;
      }


    /*****************************************************
     *
     * Called when the price request returns successfully.
     *
     *****************************************************/
    @Override
    public void onSuccess( int httpStatusCode, JSONObject jsonObject )
      {
      if ( KiteSDK.DEBUG_PRICING ) Log.d( LOG_TAG, "Request body: " + mRequestBodyString + "\nReturned JSON: " + jsonObject.toString() );

      try
        {
        OrderPricing orderPricing = new OrderPricing( jsonObject );

        PricingAgent.this.saveAndDistributeValue( mRequestBodyString, orderPricing );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to get pricing:\nRequest body: " + mRequestBodyString + "\nReturned JSON: " + jsonObject.toString(), exception );

        PricingAgent.this.onError( mRequestBodyString, exception );
        }
      }


    /*****************************************************
     *
     * Called when the price request returns an error.
     *
     *****************************************************/
    @Override
    public void onError( Exception exception )
      {
      PricingAgent.this.onError( mRequestBodyString, exception );
      }

    }

  }
