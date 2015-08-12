/*****************************************************
 *
 * OrderPricing.java
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


///// Class Declaration /////

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ly.kite.product.MultipleCurrencyAmount;

/*****************************************************
 *
 * This class holds pricing information for an order.
 *
 *****************************************************/
public class OrderPricing
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "OrderPricing";

  private static final String  JSON_NAME_DESCRIPTION         = "description";
  private static final String  JSON_NAME_DISCOUNT            = "discount";
  private static final String  JSON_NAME_INVALID_MESSAGE     = "invalid_message";
  private static final String  JSON_NAME_LINE_ITEMS          = "line_items";
  private static final String  JSON_NAME_PRODUCT_COST        = "product_cost";
  private static final String  JSON_NAME_PROMO_CODE          = "promo_code";
  private static final String  JSON_NAME_QUANTITY            = "quantity";
  private static final String  JSON_NAME_SHIPPING_COST       = "shipping_cost";
  private static final String  JSON_NAME_TEMPLATE_ID         = "template_id";
  private static final String  JSON_NAME_TOTAL               = "total";
  private static final String  JSON_NAME_TOTAL_PRODUCT_COST  = "total_product_cost";
  private static final String  JSON_NAME_TOTAL_SHIPPING_COST = "total_shipping_cost";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                  mPromoCodeInvalidMessage;
  private MultipleCurrencyAmount mPromoCodeDiscount;

  private ArrayList<LineItem> mLineItemArrayList;
  private MultipleCurrencyAmount  mTotalProductCost;
  private MultipleCurrencyAmount  mTotalCost;
  private MultipleCurrencyAmount  mTotalShippingCost;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  /*****************************************************
   *
   * Creates a new pricing object from JSON, in the following form:
   *
   * {
   * "promo_code":
   *   {
   *   "invalid_message":"No Promo Code matches code: null",
   *   "discount":
   *     {
   *     "EUR":0,
   *     "GBP":0,
   *     "USD":0
   *     }
   *   },
   * "total_product_cost":
   *   {
   *   "EUR":8,
   *   "GBP":6.25,
   *   "USD":11
   *   },
   * "line_items":
   *   [
   *     {
   *     "template_id":"stickers_circle",
   *     "description":"Pack of 5 Sticker Circles",
   *     "shipping_cost":
   *       {
   *       "EUR":0,
   *       "GBP":0,
   *       "USD":0
   *       },
   *     "quantity":"5",
   *     "product_cost":
   *       {
   *       "EUR":8,
   *       "GBP":6.25,
   *       "USD":11
   *       }
   *     }
   *   ],
   * "total":
   *   {
   *   "EUR":8,
   *   "GBP":6.25,
   *   "USD":11
   *   },
   * "total_shipping_cost":
   *   {
   *   "EUR":0,
   *   "GBP":0,
   *   "USD":0
   *   }
   * }
   *
   *****************************************************/
  OrderPricing( JSONObject jsonObject ) throws JSONException
    {
    // Get the top level items
    JSONObject promoCodeJSONObject         = jsonObject.optJSONObject( JSON_NAME_PROMO_CODE );
    JSONObject totalProductCostJSONObject  = jsonObject.getJSONObject( JSON_NAME_TOTAL_PRODUCT_COST );
    JSONArray  lineItemsJSONArray          = jsonObject.getJSONArray( JSON_NAME_LINE_ITEMS );
    JSONObject totalJSONObject             = jsonObject.getJSONObject( JSON_NAME_TOTAL );
    JSONObject totalShippingCostJSONObject = jsonObject.getJSONObject( JSON_NAME_TOTAL_SHIPPING_COST );


    // Promo code

    if ( promoCodeJSONObject != null )
      {
      mPromoCodeInvalidMessage = promoCodeJSONObject.getString( JSON_NAME_INVALID_MESSAGE );

      try
        {
        mPromoCodeDiscount = new MultipleCurrencyAmount( promoCodeJSONObject.getJSONObject( JSON_NAME_DISCOUNT ) );
        }
      catch ( Exception exception )
        {
        // Ignore
        }
      }


    // Line items

    for ( int lineItemIndex = 0; lineItemIndex < lineItemsJSONArray.length(); lineItemIndex ++ )
      {
      LineItem lineItem = new LineItem( lineItemsJSONArray.getJSONObject( lineItemIndex ) );
      }



    mTotalProductCost  = new MultipleCurrencyAmount( totalProductCostJSONObject );
    mTotalCost         = new MultipleCurrencyAmount( totalJSONObject );
    mTotalShippingCost = new MultipleCurrencyAmount( totalShippingCostJSONObject );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A line item in the pricing detail.
   *
   *****************************************************/
  public class LineItem
    {
    private String                  mProductId;
    private String                  mDescription;
    private MultipleCurrencyAmount  mShippingCost;
    private int                     mQuantity;
    private MultipleCurrencyAmount  mProductCost;


    /*****************************************************
     *
     *     {
     *     "template_id":"stickers_circle",
     *     "description":"Pack of 5 Sticker Circles",
     *     "shipping_cost":
     *       {
     *       "EUR":0,
     *       "GBP":0,
     *       "USD":0
     *       },
     *     "quantity":"5",
     *     "product_cost":
     *       {
     *       "EUR":8,
     *       "GBP":6.25,
     *       "USD":11
     *       }
     *     }
     *
     *****************************************************/
    LineItem( JSONObject jsonObject ) throws JSONException
      {
      mProductId    = jsonObject.getString( JSON_NAME_TEMPLATE_ID );
      mDescription  = jsonObject.getString( JSON_NAME_DESCRIPTION );
      mShippingCost = new MultipleCurrencyAmount( jsonObject.getJSONObject( JSON_NAME_SHIPPING_COST ) );
      mQuantity     = jsonObject.getInt( JSON_NAME_QUANTITY );
      mProductCost  = new MultipleCurrencyAmount( jsonObject.getJSONObject( JSON_NAME_PRODUCT_COST ) );
      }
    }

  }

