/*****************************************************
 *
 * OrderHistoryItem.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.ordering;


///// Import(s) /////


///// Class Declaration /////

import java.util.HashMap;
import java.util.List;

import ly.kite.address.Address;

/*****************************************************
 *
 * This class represents an order history item, and is
 * used to pass order history back from the ordering
 * data manager.
 *
 *****************************************************/
public class OrderHistoryItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "OrderHistoryItem";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private long                    mOrderId;
  private String                  mDateString;
  private String                  mDescription;
  private Long                    mBasketIdLong;
  private List<BasketItem>        mBasketItemList;
  private Address                 mShippingAddress;
  private String                  mNotificationEmail;
  private String                  mNotificationPhone;
  private String                  mUserDataJSON;
  private HashMap<String,String>  mAdditionalParametersMap;
  private String                  mPromoCode;
  private String                  mPricingJSON;
  private String                  mProofOfPayment;
  private String                  mReceipt;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  OrderHistoryItem( long                   orderId,
                    String                 dateString,
                    String                 description,
                    Long                   basketIdLong,
                    Address                shippingAddress,
                    String                 notificationEmail,
                    String                 notificationPhone,
                    String                 userDataJSON,
                    HashMap<String,String> additionalParametersMap,
                    String                 promoCode,
                    String                 pricingJSON,
                    String                 proofOfPayment,
                    String                 receipt )
    {
    mOrderId                 = orderId;
    mDateString              = dateString;
    mDescription             = description;
    mBasketIdLong            = basketIdLong;
    mShippingAddress         = shippingAddress;
    mNotificationEmail       = notificationEmail;
    mNotificationPhone       = notificationPhone;
    mUserDataJSON            = userDataJSON;
    mAdditionalParametersMap = additionalParametersMap;
    mPromoCode               = promoCode;
    mPricingJSON             = pricingJSON;
    mProofOfPayment          = proofOfPayment;
    mReceipt                 = receipt;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the order id.
   *
   *****************************************************/
  public long getOrderId()
    {
    return ( mOrderId );
    }


  /*****************************************************
   *
   * Returns the date.
   *
   *****************************************************/
  public String getDateString()
    {
    return ( mDateString );
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
   * Returns the basket id.
   *
   *****************************************************/
  public Long getBasketIdLong()
    {
    return ( mBasketIdLong );
    }


  /*****************************************************
   *
   * Sets the basket items.
   *
   *****************************************************/
  public void setBasket( List<BasketItem> basketItemList )
    {
    mBasketItemList = basketItemList;
    }


  /*****************************************************
   *
   * Returns the basket items.
   *
   *****************************************************/
  public List<BasketItem> getBasket()
    {
    return ( mBasketItemList );
    }


  /*****************************************************
   *
   * Returns the shipping address.
   *
   *****************************************************/
  public Address getShippingAddress()
    {
    return ( mShippingAddress );
    }


  /*****************************************************
   *
   * Returns the notification email.
   *
   *****************************************************/
  public String getNotificationEmail()
    {
    return ( mNotificationEmail );
    }


  /*****************************************************
   *
   * Returns the notification phone.
   *
   *****************************************************/
  public String getNotificationPhone()
    {
    return ( mNotificationPhone );
    }


  /*****************************************************
   *
   * Returns the user data.
   *
   *****************************************************/
  public String getUserDataJSON()
    {
    return ( mUserDataJSON );
    }


  /*****************************************************
   *
   * Returns the additional parameters.
   *
   *****************************************************/
  public HashMap<String,String> getAdditionalParametersMap()
    {
    return ( mAdditionalParametersMap );
    }


  /*****************************************************
   *
   * Returns the promo code.
   *
   *****************************************************/
  public String getPromoCode()
    {
    return ( mPromoCode );
    }


  /*****************************************************
   *
   * Returns the pricing JSON.
   *
   *****************************************************/
  public String getPricingJSON()
    {
    return ( mPricingJSON );
    }


  /*****************************************************
   *
   * Returns the proof of payment.
   *
   *****************************************************/
  public String getProofOfPayment()
    {
    return ( mProofOfPayment );
    }


  /*****************************************************
   *
   * Returns the receipt.
   *
   *****************************************************/
  public String getReceipt()
    {
    return ( mReceipt );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

