/*****************************************************
 *
 * OrderingDatabaseAgentTests.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2016 Kite Tech Ltd. https://www.kite.ly
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

import android.graphics.RectF;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.KiteTestCase;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.journey.UserJourneyType;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the ordering database agent.
 *
 *****************************************************/
public class OrderingDatabaseAgentTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "OrderingDatabaseAgentTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////

  // TODO: Re-write these tests now that we have a different database structure

  /*****************************************************
   *
   * Asset list job tests
   *
   *****************************************************/

  public void testSaveClearSaveBasket()
    {
    OrderingDatabaseAgent databaseAgent = new OrderingDatabaseAgent( getContext(), null );

    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.RECTANGLE, 1 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );


    databaseAgent.clearBasket( OrderingDataAgent.BASKET_ID_DEFAULT );


    long itemId = OrderingDataAgent.CREATE_NEW_ITEM_ID;


    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, "First border text", 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( originalImageSpec2 );


    databaseAgent.saveDefaultBasketItem( itemId, product, optionsMap, originalImageSpecList, 1 );


    List<BasketItem> basketItemList = databaseAgent.loadDefaultBasket( getContext(), catalogue );


    Assert.assertEquals( 1, basketItemList.size() );

    BasketItem basketItem = basketItemList.get( 0 );

    List<ImageSpec> imageSpecList = basketItem.getImageSpecList();

    Assert.assertEquals( 2, imageSpecList.size() );

    ImageSpec imageSpec1 = imageSpecList.get( 0 );
    Assert.assertEquals( "First border text", imageSpec1.getBorderText() );
    Assert.assertEquals( 1, imageSpec1.getQuantity() );

    ImageSpec imageSpec2 = imageSpecList.get( 1 );
    Assert.assertEquals( "Second border text", imageSpec2.getBorderText() );
    Assert.assertEquals( 2, imageSpec2.getQuantity() );


    databaseAgent.clearBasket( OrderingDataAgent.BASKET_ID_DEFAULT );

    basketItemList = databaseAgent.loadDefaultBasket( getContext(), catalogue );

    Assert.assertEquals( 0, basketItemList.size() );


    originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Third border text", 1 );

    originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( originalImageSpec2 );


    databaseAgent.saveDefaultBasketItem( itemId, product, optionsMap, originalImageSpecList, 1 );


    basketItemList = databaseAgent.loadDefaultBasket( getContext(), catalogue );


    Assert.assertEquals( 1, basketItemList.size() );

    basketItem = basketItemList.get( 0 );

    imageSpecList = basketItem.getImageSpecList();

    Assert.assertEquals( 2, imageSpecList.size() );

    imageSpec1 = imageSpecList.get( 0 );
    Assert.assertEquals( null, imageSpec1.getBorderText() );
    Assert.assertEquals( 1, imageSpec1.getQuantity() );

    imageSpec2 = imageSpecList.get( 1 );
    Assert.assertEquals( "Third border text", imageSpec2.getBorderText() );
    Assert.assertEquals( 1, imageSpec2.getQuantity() );
    }




  ////////// Inner Class(es) //////////

  }