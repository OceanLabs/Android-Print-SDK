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

import org.json.JSONObject;

import java.net.URLEncoder;

import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.util.ACache;
import ly.kite.util.HTTPJSONRequest;
import ly.kite.product.PrintOrder;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an agent for the price API endpoint.
 *
 *****************************************************/
public class PricingAgent extends ACache<String,OrderPricing,IPricingConsumer>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG               = "PricingAgent";

  private static final String  REQUEST_FORMAT_STRING = "%s/price/?basket=%s&shipping_country_code=%s&promo_code=%s";


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
   * Retrieves the price information for an order.
   *
   *****************************************************/
  public OrderPricing requestPricing( Context context, PrintOrder order, IPricingConsumer consumer )
    {
    // Construct the request URL first, because we also use it as the caching key.

    KiteSDK kiteSDK = KiteSDK.getInstance( context );

    String basketString = order.toBasketString();

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

    String promoCode = order.getPromoCode();

    if ( promoCode == null ) promoCode = "";


    String requestURLString = String.format( REQUEST_FORMAT_STRING, kiteSDK.getAPIEndpoint(), basketString, shippingCountryCode, URLEncoder.encode( promoCode ) );


    // If we already have the price information cached from a previous retrieval - return it now

    OrderPricing cachedPricing = getCachedValue( requestURLString );

    if ( cachedPricing != null ) return ( cachedPricing );


    // We don't already have the price information. If there is already a request running -
    // add this consumer to the list of consumers waiting for the result. Otherwise start
    // a new request.

    if ( ! requestAlreadyStarted( requestURLString, consumer ) )
      {
      HTTPJSONRequest request = new HTTPJSONRequest( context, HTTPJSONRequest.HttpMethod.GET, requestURLString, null, null );

      request.start( new PriceRequestListener( requestURLString ) );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Distributes a value to a callback.
   *
   *****************************************************/
  protected void onValueAvailable( OrderPricing pricing, IPricingConsumer consumer )
    {
    consumer.paOnSuccess( pricing );
    }


  /*****************************************************
   *
   * Distributes an error to a consumer. The callback will
   * never be null.
   *
   *****************************************************/
  protected void onError( Exception exception, IPricingConsumer consumer )
    {
    consumer.paOnError( exception );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback for a price request.
   *
   *****************************************************/
  private class PriceRequestListener implements HTTPJSONRequest.HTTPJSONRequestListener
    {
    private String  mRequestURLString;


    PriceRequestListener( String requestURLString )
      {
      mRequestURLString = requestURLString;
      }


    /*****************************************************
     *
     * Called when the price request returns successfully.
     *
     *****************************************************/
    @Override
    public void onSuccess( int httpStatusCode, JSONObject jsonObject )
      {
      try
        {
        OrderPricing pricing = new OrderPricing( jsonObject );

        PricingAgent.this.onValueAvailable( mRequestURLString, pricing );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to creating pricing from JSON: " + jsonObject.toString(), exception );

        PricingAgent.this.onError( mRequestURLString, exception );
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
      PricingAgent.this.onError( mRequestURLString, exception );
      }
    }


  }
