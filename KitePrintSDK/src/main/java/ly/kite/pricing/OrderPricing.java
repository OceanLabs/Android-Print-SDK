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

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ly.kite.product.MultipleCurrencyAmount;
import ly.kite.product.SingleCurrencyAmount;

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

  private String                  mPromoCodeInvalidMessage;
  private MultipleCurrencyAmount  mPromoCodeDiscount;

  private ArrayList<LineItem>     mLineItemArrayList;
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
        mPromoCodeDiscount = new MultipleCurrencyAmount( promoCodeJSONObject.getJSONObject( JSON_NAME_DISCOUNT ) );
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


    mTotalProductCost  = new MultipleCurrencyAmount( totalProductCostJSONObject );
    mTotalCost         = new MultipleCurrencyAmount( totalJSONObject );
    mTotalShippingCost = new MultipleCurrencyAmount( totalShippingCostJSONObject );
    }


  /*****************************************************
   *
   * Creates order pricing from a parcel.
   *
   *****************************************************/
  private OrderPricing( Parcel parcel )
    {
    mPromoCodeInvalidMessage = parcel.readString();
    mPromoCodeDiscount       = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );

    mLineItemArrayList = new ArrayList<LineItem>();
    parcel.readList( mLineItemArrayList, LineItem.class.getClassLoader() );

    mTotalProductCost = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );
    mTotalCost = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );
    mTotalShippingCost = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );
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
    parcel.writeString( mPromoCodeInvalidMessage );
    parcel.writeParcelable( mPromoCodeDiscount, flags );

    parcel.writeList( mLineItemArrayList );
    parcel.readList( mLineItemArrayList, LineItem.class.getClassLoader() );

    parcel.writeParcelable( mTotalProductCost, flags );
    parcel.writeParcelable( mTotalCost, flags );
    parcel.writeParcelable( mTotalShippingCost, flags );
    }


  ////////// Method(s) //////////

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
  public MultipleCurrencyAmount getPromoCodeDiscount()
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
  public MultipleCurrencyAmount getTotalCost()
    {
    return ( mTotalCost );
    }


  /*****************************************************
   *
   * Returns the total shipping cost.
   *
   *****************************************************/
  public MultipleCurrencyAmount getTotalShippingCost()
    {
    return ( mTotalShippingCost );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A line item in the pricing detail.
   *
   *****************************************************/
  static public class LineItem implements Parcelable
    {

    public static final Parcelable.Creator<LineItem> CREATOR =
        new Parcelable.Creator<LineItem>()
        {
        public LineItem createFromParcel( Parcel sourceParcel )
          {
          return ( new LineItem( sourceParcel ) );
          }

        public LineItem[] newArray( int size )
          {
          return ( new LineItem[ size ] );
          }
        };


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


    LineItem( Parcel parcel )
      {
      mProductId    = parcel.readString();
      mDescription  = parcel.readString();
      mShippingCost = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );
      mQuantity     = parcel.readInt();
      mProductCost  = (MultipleCurrencyAmount)parcel.readParcelable( MultipleCurrencyAmount.class.getClassLoader() );
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
      parcel.writeString( mProductId );
      parcel.writeString( mDescription );
      parcel.writeParcelable( mShippingCost, flags );
      parcel.writeInt( mQuantity );
      parcel.writeParcelable( mProductCost, flags );
      }


    ////////// Method(s) //////////

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
     * Returns the product cost.
     *
     *****************************************************/
    public MultipleCurrencyAmount getProductCost()
      {
      return ( mProductCost );
      }

    }

  }

