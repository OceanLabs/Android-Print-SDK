/*****************************************************
 *
 * OrderingDataAgentTests.java
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

package ly.kite.ordering;


///// Import(s) /////

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteTestCase;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.journey.UserJourneyType;
import ly.kite.util.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the ordering data agent class.
 *
 *****************************************************/
public class OrderingDataAgentTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "OrderingDataAgentTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clear basket tests.
   *
   *****************************************************/

  public void testClear1()
    {
    OrderingDataAgent dataAgent = OrderingDataAgent.getInstance( getContext() );


    dataAgent.clearDefaultBasket();

    List<BasketItem> basketItemList = dataAgent.getAllItems( null );

    Assert.assertEquals( 0, basketItemList.size() );
    }


  /*****************************************************
   *
   * Add to basket tests.
   *
   *****************************************************/

  public void testAdd1()
    {
    OrderingDataAgent dataAgent = OrderingDataAgent.getInstance( getContext() );


    dataAgent.clearDefaultBasket();

    List<BasketItem> basketItemList = dataAgent.getAllItems( null );

    Assert.assertEquals( 0, basketItemList.size() );


    Product product = new Product( "product_id", "product_code", "Rectangular Product", "Product type","Product category", 0xff000000, UserJourneyType.RECTANGLE, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "group_label", null, product );

    List<ImageSpec> imageSpecList = new ArrayList<>();
    imageSpecList.add( new ImageSpec( new Asset( "/tmp/image1.jpg" ) ) );


    dataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, null, imageSpecList, 1 );


    basketItemList = dataAgent.getAllItems( catalogue );

    Assert.assertEquals( 1, basketItemList.size() );


    dataAgent.clearDefaultBasket();

    basketItemList = dataAgent.getAllItems( null );

    Assert.assertEquals( 0, basketItemList.size() );
    }

  public void testAdd2()
    {
    OrderingDataAgent dataAgent = OrderingDataAgent.getInstance( getContext() );


    dataAgent.clearDefaultBasket();


    Product product = new Product( "product_id", "product_code", "Rectangular Product","Product category", "Product type", 0xff000000, UserJourneyType.RECTANGLE, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "group_label", null, product );

    List<ImageSpec> imageSpecList = new ArrayList<>();
    Asset asset = createSessionAssetFile();
    imageSpecList.add( new ImageSpec( asset ) );


    dataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, null, imageSpecList, 1 );


    List<BasketItem>basketItemList = dataAgent.getAllItems( catalogue );

    Assert.assertEquals( 1, basketItemList.size() );


    BasketItem basketItem = basketItemList.get( 0 );

    Assert.assertEquals( "product_id", basketItem.getProduct().getId() );
    Assert.assertEquals( null, basketItem.getOptionsMap() );
    Assert.assertEquals( 1, basketItem.getOrderQuantity() );

    imageSpecList = basketItem.getImageSpecList();

    Assert.assertEquals( 1, imageSpecList.size() );

    ImageSpec imageSpec = imageSpecList.get( 0 );
    assertProperties( imageSpec, null, null, 1 );
    }


  ////////// Inner Class(es) //////////

  }