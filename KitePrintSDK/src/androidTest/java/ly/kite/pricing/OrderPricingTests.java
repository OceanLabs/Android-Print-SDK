/*****************************************************
 *
 * OrderPricingTests.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2017 Kite Tech Ltd. https://www.kite.ly
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

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteTestCase;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the order pricing class.
 *
 *****************************************************/
public class OrderPricingTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "OrderPricingTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Parsing tests
   *
   *****************************************************/

  public void testParsing1()
    {
    try
      {
      JSONObject pricingJSONObject = new JSONObject( new JSONTokener(
              "{" +
                      "\"promo_code\":" +
                      "{" +
                      "\"invalid_message\":\"No Promo Code matches code: null\"," +
                      "\"discount\":" +
                      "{" +
                      "\"EUR\":0," +
                      "\"GBP\":0," +
                      "\"USD\":0" +
                      "}" +
                      "}," +
                      "\"total_product_cost\":" +
                      "{" +
                      "\"EUR\":8," +
                      "\"GBP\":6.25," +
                      "\"USD\":11" +
                      "}," +
                      "\"line_items\":" +
                      "[" +
                      "{" +
                      "\"template_id\":\"stickers_circle\"," +
                      "\"description\":\"Pack of 5 Sticker Circles\"," +
                      "\"shipping_cost\":" +
                      "{" +
                      "\"EUR\":0," +
                      "\"GBP\":0," +
                      "\"USD\":0" +
                      "}," +
                      "\"quantity\":\"5\"," +
                      "\"product_cost\":" +
                      "{" +
                      "\"EUR\":8," +
                      "\"GBP\":6.25," +
                      "\"USD\":11" +
                      "}" +
                      "}" +
                      "]," +
                      "\"total\":" +
                      "{" +
                      "\"EUR\":8," +
                      "\"GBP\":6.25," +
                      "\"USD\":11" +
                      "}," +
                      "\"total_shipping_cost\":" +
                      "{" +
                      "\"EUR\":2," +
                      "\"GBP\":1.5," +
                      "\"USD\":2.5" +
                      "}" +
                      "}" ) );

      OrderPricing orderPricing = new OrderPricing( pricingJSONObject );


      Assert.assertEquals( "No Promo Code matches code: null", orderPricing.getPromoCodeInvalidMessage() );


      List<OrderPricing.LineItem> lineItemList = orderPricing.getLineItems();

      Assert.assertEquals( 1, lineItemList.size() );

      OrderPricing.LineItem lineItem1 = lineItemList.get( 0 );

      Assert.assertEquals( "stickers_circle", lineItem1.getProductId() );
      Assert.assertEquals( "Pack of 5 Sticker Circles", lineItem1.getDescription() );
      Assert.assertEquals( 8.00, lineItem1.getProductCost().get( "EUR" ).getAmountAsDouble() );
      Assert.assertEquals( 6.25, lineItem1.getProductCost().get( "GBP" ).getAmountAsDouble() );
      Assert.assertEquals( 11.00, lineItem1.getProductCost().get( "USD" ).getAmountAsDouble() );

      Assert.assertEquals(  8.00, orderPricing.getTotalCost().get( "EUR" ).getAmountAsDouble() );
      Assert.assertEquals(  6.25, orderPricing.getTotalCost().get( "GBP" ).getAmountAsDouble() );
      Assert.assertEquals( 11.00, orderPricing.getTotalCost().get( "USD" ).getAmountAsDouble() );

      Assert.assertEquals( 2.00, orderPricing.getTotalShippingCost().get( "EUR" ).getAmountAsDouble() );
      Assert.assertEquals( 1.50, orderPricing.getTotalShippingCost().get( "GBP" ).getAmountAsDouble() );
      Assert.assertEquals( 2.50, orderPricing.getTotalShippingCost().get( "USD" ).getAmountAsDouble() );

      Assert.assertTrue( OrderPricing.currencyCanBeUsed( lineItemList, "EUR" ) );
      Assert.assertTrue( OrderPricing.currencyCanBeUsed( lineItemList, "GBP" ) );
      Assert.assertTrue( OrderPricing.currencyCanBeUsed( lineItemList, "USD" ) );
      Assert.assertFalse( OrderPricing.currencyCanBeUsed( lineItemList, "SEK" ) );
      }
    catch ( JSONException je )
      {
      Assert.fail();
      }
    }


  ////////// Inner Class(es) //////////

  }

