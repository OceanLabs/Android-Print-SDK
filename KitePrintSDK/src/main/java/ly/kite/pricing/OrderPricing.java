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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.kite.catalogue.MultipleCurrencyAmounts;


///// Class Declaration /////

/*****************************************************
 *
 * This class holds pricing information for an order.
 *
 *****************************************************/
public class OrderPricing implements Parcelable
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
  private static final String  JSON_NAME_CURRENCY_USED       = "currency_used";

  private static final String  JSON_VALUE_NULL               = "null";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<OrderPricing> CREATOR =
    new Parcelable.Creator<OrderPricing>()
      {
      public OrderPricing createFromParcel( Parcel sourceParcel )
        {
        return ( new OrderPricing( sourceParcel ) );
        }

      public OrderPricing[] newArray( int size )
        {
        return ( new OrderPricing[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private JSONObject              mOrderPricingJSONObject;

  private String                  mPromoCodeInvalidMessage;
  private MultipleCurrencyAmounts mPromoCodeDiscount;

  private ArrayList<LineItem>     mLineItemArrayList;
  private MultipleCurrencyAmounts mTotalProductCost;
  private MultipleCurrencyAmounts mTotalCost;
  private MultipleCurrencyAmounts mTotalShippingCost;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the supplied currency can be used
   * in all the line items.
   *
   *****************************************************/
  static boolean currencyCanBeUsed( List<LineItem> lineItemList, String currencyCode )
    {
    if ( lineItemList != null )
      {
      for ( LineItem lineItem : lineItemList )
        {
        if ( lineItem != null )
          {
          if ( ( ! currencyCanBeUsed( lineItem.getShippingCost(), currencyCode ) ) ||
               ( ! currencyCanBeUsed( lineItem.getProductCost(), currencyCode  ) ) ) return ( false );
          }
        }
      }

    return ( true );
    }


  /*****************************************************
   *
   * Returns true if the supplied currency is available
   * in the multiple currency amount, of the multiple
   * currency amount is null.
   *
   *****************************************************/
  static boolean currencyCanBeUsed( MultipleCurrencyAmounts multipleCurrencyAmount, String currencyCode )
    {
    return ( multipleCurrencyAmount == null || multipleCurrencyAmount.contains( currencyCode ) );
    }


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
  OrderPricing( JSONObject orderPricingJSONObject ) throws JSONException
    {
    mOrderPricingJSONObject = orderPricingJSONObject;

    // Get the top level items
    JSONObject promoCodeJSONObject         = orderPricingJSONObject.optJSONObject( JSON_NAME_PROMO_CODE );
    JSONObject totalProductCostJSONObject  = orderPricingJSONObject.getJSONObject( JSON_NAME_TOTAL_PRODUCT_COST );
    JSONArray  lineItemsJSONArray          = orderPricingJSONObject.getJSONArray( JSON_NAME_LINE_ITEMS );
    JSONObject totalJSONObject             = orderPricingJSONObject.getJSONObject( JSON_NAME_TOTAL );
    JSONObject totalShippingCostJSONObject = orderPricingJSONObject.getJSONObject( JSON_NAME_TOTAL_SHIPPING_COST );


    // Promo code

    if ( promoCodeJSONObject != null )
      {
      String promoCodeInvalidMessage = promoCodeJSONObject.getString( JSON_NAME_INVALID_MESSAGE );

      if ( promoCodeInvalidMessage == null || promoCodeInvalidMessage.equals( JSON_VALUE_NULL ) )
        {
        mPromoCodeInvalidMessage = null;
        }
      else
        {
        mPromoCodeInvalidMessage = promoCodeInvalidMessage;
        }

      try
        {
        mPromoCodeDiscount = new MultipleCurrencyAmounts( promoCodeJSONObject.getJSONObject( JSON_NAME_DISCOUNT ) );
        }
      catch ( Exception exception )
        {
        // Ignore
        }
      }


    // Line items

    mLineItemArrayList = new ArrayList<>( lineItemsJSONArray.length() );

    for ( int lineItemIndex = 0; lineItemIndex < lineItemsJSONArray.length(); lineItemIndex ++ )
      {
      LineItem lineItem = new LineItem( lineItemsJSONArray.getJSONObject( lineItemIndex ) );

      mLineItemArrayList.add( lineItem );
      }


    mTotalProductCost  = new MultipleCurrencyAmounts( totalProductCostJSONObject );
    mTotalCost         = new MultipleCurrencyAmounts( totalJSONObject );
    mTotalShippingCost = new MultipleCurrencyAmounts( totalShippingCostJSONObject );
    }


  /*****************************************************
   *
   * Creates a new pricing object from JSON, in the following form:
   *
   *****************************************************/
  public OrderPricing( String orderPricingJSON ) throws JSONException
    {
    this( new JSONObject( orderPricingJSON ) );
    }


  /*****************************************************
   *
   * Creates order pricing from a parcel.
   *
   *****************************************************/
  private OrderPricing( Parcel parcel )
    {
    try
      {
      mOrderPricingJSONObject = new JSONObject( parcel.readString() );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Unable to parse pricing JSON", je );
      }

    mPromoCodeInvalidMessage = parcel.readString();
    mPromoCodeDiscount       = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );


    // Read the line items

    int lineItemCount = parcel.readInt();

    mLineItemArrayList = new ArrayList<LineItem>();

    for ( int lineItemIndex = 0; lineItemIndex < lineItemCount; lineItemIndex ++ )
      {
      mLineItemArrayList.add( new LineItem( parcel ) );
      }


    mTotalProductCost      = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
    mTotalCost             = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
    mTotalShippingCost     = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
    }


  ////////// Parcelable Method(s) //////////

  /*****************************************************
   *
   * Describes the contents.
   *
   *****************************************************/
  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Write the contents to a parcel.
   *
   *****************************************************/
  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    parcel.writeString( mOrderPricingJSONObject.toString() );

    parcel.writeString( mPromoCodeInvalidMessage );
    parcel.writeParcelable( mPromoCodeDiscount, flags );


    // Write the line items

    parcel.writeInt( mLineItemArrayList.size() );

    for ( LineItem lineItem : mLineItemArrayList )
      {
      lineItem.writeToParcel( parcel, flags );
      }


    parcel.writeParcelable( mTotalProductCost, flags );
    parcel.writeParcelable( mTotalCost, flags );
    parcel.writeParcelable( mTotalShippingCost, flags );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the pricing JSON string.
   *
   *****************************************************/
  public String getPricingJSONString()
    {
      try
        {
        String currencyUsed = mLineItemArrayList.get(0).mProductCost.getDefaultAmountWithFallback().getCurrencyCode();
        mOrderPricingJSONObject.put( JSON_NAME_CURRENCY_USED, currencyUsed );
        }
      catch ( Exception e )
        {
        Log.e( LOG_TAG, "Could not add current currency to json" );
        }
    return ( mOrderPricingJSONObject.toString() );
    }


  /*****************************************************
   *
   * Returns any promo code invalid message, or null, if
   * there was no promo code error.
   *
   *****************************************************/
  public String getPromoCodeInvalidMessage()
    {
    return ( mPromoCodeInvalidMessage );
    }


  /*****************************************************
   *
   * Returns any promo code discount.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getPromoCodeDiscount()
    {
    return ( mPromoCodeDiscount );
    }


  /*****************************************************
   *
   * Returns the line item list.
   *
   *****************************************************/
  public List<LineItem> getLineItems()
    {
    return ( mLineItemArrayList );
    }


  /*****************************************************
   *
   * Returns the total cost.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getTotalCost()
    {
    return ( mTotalCost );
    }


  /*****************************************************
   *
   * Returns the total shipping cost.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getTotalShippingCost()
    {
    return ( mTotalShippingCost );
    }


  /*****************************************************
   *
   * Returns a fall-back currency that all the prices are
   * available in.
   *
   *****************************************************/
  public String getFallbackCurrencyCode( List<String> currencyCodes )
    {
    // We start with the fall-back currencies from the Multiple Currency Amount
    // class.

    for ( String candidateCurrencyCode : MultipleCurrencyAmounts.FALLBACK_CURRENCY_CODES )
      {
      if ( currencyIsUbiquitous( candidateCurrencyCode ) ) return ( candidateCurrencyCode );
      }


    // If that doesn't work, try the currencies that we were supplied. There
    // might be some overlap with the currencies.

    if ( currencyCodes != null )
      {
      for ( String candidateCurrencyCode : currencyCodes )
        {
        if ( currencyIsUbiquitous( candidateCurrencyCode ) ) return ( candidateCurrencyCode );
        }
      }


    // We couldn't find any ubiquitous currency

    return ( null );
    }


  /*****************************************************
   *
   * Returns true if the supplied currency is available
   * across all the prices .
   *
   *****************************************************/
  private boolean currencyIsUbiquitous( String currencyCode )
    {
    return ( currencyCanBeUsed( mPromoCodeDiscount, currencyCode ) &&
             currencyCanBeUsed( mLineItemArrayList, currencyCode ) &&
             currencyCanBeUsed( mTotalProductCost,  currencyCode ) &&
             currencyCanBeUsed( mTotalCost,         currencyCode ) &&
             currencyCanBeUsed( mTotalShippingCost, currencyCode ) );
    }


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
    private MultipleCurrencyAmounts mShippingCost;
    private int                     mQuantity;
    private MultipleCurrencyAmounts mProductCost;


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
      mShippingCost = new MultipleCurrencyAmounts( jsonObject.getJSONObject( JSON_NAME_SHIPPING_COST ) );
      mQuantity     = jsonObject.getInt( JSON_NAME_QUANTITY );
      mProductCost  = new MultipleCurrencyAmounts( jsonObject.getJSONObject( JSON_NAME_PRODUCT_COST ) );
      }


    LineItem( Parcel parcel )
      {
      mProductId    = parcel.readString();
      mDescription  = parcel.readString();
      mShippingCost = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
      mQuantity     = parcel.readInt();
      mProductCost  = (MultipleCurrencyAmounts)parcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
      }


    /*****************************************************
     *
     * Write the contents to a parcel.
     *
     *****************************************************/
    public void writeToParcel( Parcel parcel, int flags )
      {
      parcel.writeString( mProductId );
      parcel.writeString( mDescription );
      parcel.writeParcelable( mShippingCost, flags );
      parcel.writeInt( mQuantity );
      parcel.writeParcelable( mProductCost, flags );
      }


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Returns the product id.
     *
     *****************************************************/
    public String getProductId()
      {
      return ( mProductId );
      }


    /*****************************************************
     *
     * Returns the description.
     *
     *****************************************************/
    public String getDescription()
      {
      return ( mDescription );
      }


    /*****************************************************
     *
     * Returns the shipping cost.
     *
     *****************************************************/
    public MultipleCurrencyAmounts getShippingCost()
      {
      return ( mShippingCost );
      }


    /*****************************************************
     *
     * Returns the quantity.
     *
     *****************************************************/
    public int getQuantity()
      {
      return ( mQuantity );
      }


    /*****************************************************
     *
     * Returns the product cost.
     *
     *****************************************************/
    public MultipleCurrencyAmounts getProductCost()
      {
      return ( mProductCost );
      }

    }

    /*****************************************************
     *
     * Returns the currency code that will be used
     *
     *****************************************************/
    public String getCurrencyUsed()
    {
      try
        {
        return mOrderPricingJSONObject.getString(JSON_NAME_CURRENCY_USED);
        }
      catch ( Exception e )
        {
        Log.i( LOG_TAG, "Used currency not found for order!");
        }
        return null;
    }

    /*****************************************************
     *
     * Check for free checkout
     *
     *****************************************************/
    public boolean isCheckoutFree() {
      return mTotalCost.getDefaultAmountWithFallback().isZero();
    }

  }

