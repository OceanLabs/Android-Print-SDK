/*****************************************************
 *
 * BasketOrderTests.java
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

package ly.kite;


///// Import(s) /////

import android.content.Context;
import android.database.Cursor;
import android.graphics.RectF;
import android.util.Log;

import junit.framework.Assert;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.catalogue.BorderF;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.journey.UserJourneyType;
import ly.kite.ordering.BasketItem;
import ly.kite.ordering.GreetingCardJob;
import ly.kite.ordering.ImageSpec;
import ly.kite.ordering.ImagesJob;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.ordering.OrderingDataAgent;
import ly.kite.ordering.OrderingDatabaseAgent;
import ly.kite.KiteSDK;
import ly.kite.ordering.PhotobookJob;
import ly.kite.pricing.OrderPricing;
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the full cycle from creation -> basket
 * -> order.
 *
 *****************************************************/
public class BasketOrderTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "BasketOrderTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * CALENDAR tests.
   *
   *****************************************************/

  public void testCalendar1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );

    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.CALENDAR, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();

    Assert.assertEquals( 3, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );
    Job job3 = jobList.get( 2 );

    Assert.assertTrue( job1 instanceof ImagesJob );
    Assert.assertTrue( job2 instanceof ImagesJob );
    Assert.assertTrue( job3 instanceof ImagesJob );

    ImagesJob imagesJob1 = (ImagesJob)job1;
    ImagesJob imagesJob2 = (ImagesJob)job2;
    ImagesJob imagesJob3 = (ImagesJob)job3;


    AssetFragment assetFragment1;
    AssetFragment assetFragment2;


    // Job 1

    Assert.assertEquals( "product_id", imagesJob1.getProductId() );
    Assert.assertEquals( 3, imagesJob1.getOrderQuantity() );

    List<UploadableImage> uploadableImageList1 = imagesJob1.getUploadableImageList();
    List<String>          borderTextList1      = imagesJob1.getBorderTextList();

    Assert.assertEquals( 2, uploadableImageList1.size() );
    Assert.assertNull( borderTextList1 );

    Assert.assertNull( uploadableImageList1.get( 0 ) );

    Assert.assertNotNull( assetFragment2 = uploadableImageList1.get( 1 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", imagesJob2.getProductId() );
    Assert.assertEquals( 3, imagesJob2.getOrderQuantity() );

    List<UploadableImage> uploadableImageList2 = imagesJob2.getUploadableImageList();
    List<String>          borderTextList2      = imagesJob2.getBorderTextList();

    Assert.assertEquals( 2, uploadableImageList2.size() );
    Assert.assertNull( borderTextList2 );

    Assert.assertNull( uploadableImageList2.get( 0 ) );
    Assert.assertNull( uploadableImageList2.get( 1 ) );

    Assert.assertEquals( "Alpha", imagesJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob2.getProductOption( "Parameter2" ) );


    // Job 3

    Assert.assertEquals( "product_id", imagesJob3.getProductId() );
    Assert.assertEquals( 3, imagesJob3.getOrderQuantity() );

    List<UploadableImage> uploadableImageList3 = imagesJob3.getUploadableImageList();
    List<String>          borderTextList3      = imagesJob3.getBorderTextList();

    Assert.assertEquals( 2, uploadableImageList3.size() );
    Assert.assertNull( borderTextList3 );

    Assert.assertNotNull( assetFragment1 = uploadableImageList3.get( 0 ).getAssetFragment() );
    Assert.assertEquals( originalProportionalRectangle2, assetFragment1.getProportionalRectangle() );

    Assert.assertNotNull( assetFragment2 = uploadableImageList3.get( 1 ).getAssetFragment() );
    Assert.assertEquals( originalProportionalRectangle2, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob3.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob3.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * CIRCLE tests.
   *
   *****************************************************/

  public void testCircle1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.CIRCLE, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 2, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );

    Assert.assertTrue( job1 instanceof ImagesJob );
    Assert.assertTrue( job2 instanceof ImagesJob );

    ImagesJob imagesJob1 = (ImagesJob)job1;
    ImagesJob imagesJob2 = (ImagesJob)job2;


    AssetFragment assetFragment1;
    AssetFragment assetFragment2;


    // Job 1

    Assert.assertEquals( "product_id", imagesJob1.getProductId() );
    Assert.assertEquals( 3, imagesJob1.getOrderQuantity() );

    List<UploadableImage> uploadableImageList1 = imagesJob1.getUploadableImageList();
    List<String>          borderTextList1      = imagesJob1.getBorderTextList();

    Assert.assertEquals( 2, uploadableImageList1.size() );
    Assert.assertNull( borderTextList1 );

    Assert.assertNotNull( assetFragment1 = uploadableImageList1.get( 0 ).getAssetFragment() );
    Assert.assertNotNull( assetFragment2 = uploadableImageList1.get( 1 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment1.getProportionalRectangle() );
    Assert.assertEquals( originalProportionalRectangle2, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", imagesJob2.getProductId() );
    Assert.assertEquals( 3, imagesJob2.getOrderQuantity() );

    List<UploadableImage> uploadableImageList2 = imagesJob2.getUploadableImageList();
    List<String>          borderTextList2      = imagesJob2.getBorderTextList();

    Assert.assertEquals( 1, uploadableImageList2.size() );
    Assert.assertNull( borderTextList2 );

    Assert.assertNotNull( assetFragment1 = uploadableImageList2.get( 0 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment1.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob2.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * GREETING CARD tests.
   *
   *****************************************************/

  public void testGreetingCard1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type", "Product category", 0xff000000, UserJourneyType.GREETINGCARD, 4 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 3, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );
    Job job3 = jobList.get( 2 );

    Assert.assertTrue( job1 instanceof GreetingCardJob );
    Assert.assertTrue( job2 instanceof GreetingCardJob );
    Assert.assertTrue( job3 instanceof GreetingCardJob );

    GreetingCardJob greetingCardJob1 = (GreetingCardJob)job1;
    GreetingCardJob greetingCardJob2 = (GreetingCardJob)job2;
    GreetingCardJob greetingCardJob3 = (GreetingCardJob)job3;


    AssetFragment assetFragment;


    // Job 1

    Assert.assertEquals( "product_id", greetingCardJob1.getProductId() );
    Assert.assertEquals( 3, greetingCardJob1.getOrderQuantity() );

    Assert.assertNotNull( assetFragment = greetingCardJob1.getFrontUploadableImage().getAssetFragment() );
    Assert.assertNull( greetingCardJob1.getBackUploadableImage() );
    Assert.assertNull( greetingCardJob1.getInsideLeftUploadableImage() );
    Assert.assertNull( greetingCardJob1.getInsideRightUploadableImage() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", greetingCardJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", greetingCardJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", greetingCardJob2.getProductId() );
    Assert.assertEquals( 3, greetingCardJob2.getOrderQuantity() );

    Assert.assertNotNull( assetFragment = greetingCardJob2.getFrontUploadableImage().getAssetFragment() );
    Assert.assertNull( greetingCardJob2.getBackUploadableImage() );
    Assert.assertNull( greetingCardJob2.getInsideLeftUploadableImage() );
    Assert.assertNull( greetingCardJob2.getInsideRightUploadableImage() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", greetingCardJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", greetingCardJob2.getProductOption( "Parameter2" ) );


    // Job 3

    Assert.assertEquals( "product_id", greetingCardJob3.getProductId() );
    Assert.assertEquals( 3, greetingCardJob3.getOrderQuantity() );

    Assert.assertNotNull( assetFragment = greetingCardJob3.getFrontUploadableImage().getAssetFragment() );
    Assert.assertNull( greetingCardJob3.getBackUploadableImage() );
    Assert.assertNull( greetingCardJob3.getInsideLeftUploadableImage() );
    Assert.assertNull( greetingCardJob3.getInsideRightUploadableImage() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", greetingCardJob3.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", greetingCardJob3.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * PHONE CASE tests.
   *
   *****************************************************/

  public void testPhoneCase1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "A Phone Case", "Phone Case","Product category", 0xff000000, UserJourneyType.PHONE_CASE, 1 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Phone Cases", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 3, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );
    Job job3 = jobList.get( 2 );

    Assert.assertTrue( job1 instanceof ImagesJob );
    Assert.assertTrue( job2 instanceof ImagesJob );
    Assert.assertTrue( job3 instanceof ImagesJob );

    ImagesJob imagesJob1 = (ImagesJob)job1;
    ImagesJob imagesJob2 = (ImagesJob)job2;
    ImagesJob imagesJob3 = (ImagesJob)job3;

    List<UploadableImage> uploadableImageList1 = imagesJob1.getUploadableImageList();
    List<UploadableImage> uploadableImageList2 = imagesJob2.getUploadableImageList();
    List<UploadableImage> uploadableImageList3 = imagesJob3.getUploadableImageList();

    AssetFragment assetFragment1;
    AssetFragment assetFragment2;
    AssetFragment assetFragment3;


    // Job 1

    Assert.assertEquals( "product_id", imagesJob1.getProductId() );
    Assert.assertEquals( 3, imagesJob1.getOrderQuantity() );

    Assert.assertEquals( 1, uploadableImageList1.size() );
    Assert.assertNotNull( assetFragment1 = uploadableImageList1.get( 0 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment1.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", imagesJob2.getProductId() );
    Assert.assertEquals( 3, imagesJob2.getOrderQuantity() );

    Assert.assertEquals( 1, uploadableImageList2.size() );
    Assert.assertNotNull( assetFragment2 = uploadableImageList2.get( 0 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob2.getProductOption( "Parameter2" ) );


    // Job 3

    Assert.assertEquals( "product_id", imagesJob3.getProductId() );
    Assert.assertEquals( 3, imagesJob3.getOrderQuantity() );

    Assert.assertEquals( 1, uploadableImageList3.size() );
    Assert.assertNotNull( assetFragment3 = uploadableImageList3.get( 0 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment3.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob3.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob3.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * PHOTOBOOK tests.
   *
   *****************************************************/

  // No summary front page
  public void testPhotobook1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.PHOTOBOOK, 2 );

    KiteSDK.IEnvironment environment;
    environment = KiteSDK.DefaultEnvironment.TEST;

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );
    originalImageSpecList.add( null );


    // Initialise the SDK so that we can supply a customiser
    KiteSDK.getInstance( getContext(), "NoKey", KiteSDK.DefaultEnvironment.TEST );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 2, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );

    Assert.assertTrue( job1 instanceof PhotobookJob );
    Assert.assertTrue( job2 instanceof PhotobookJob );

    PhotobookJob photobookJob1 = (PhotobookJob)job1;
    PhotobookJob photobookJob2 = (PhotobookJob)job2;


    AssetFragment frontCoverAssetFragment;
    AssetFragment assetFragment1;
    AssetFragment assetFragment2;


    // Job 1

    Assert.assertEquals( "product_id", photobookJob1.getProductId() );
    Assert.assertEquals( 3, photobookJob1.getOrderQuantity() );

    Assert.assertNull( photobookJob1.getFrontCoverUploadableImage() );

    List<UploadableImage> uploadableImageList1 = photobookJob1.getUploadableImageList();
    Assert.assertEquals( 2, uploadableImageList1.size() );

    Assert.assertNotNull( assetFragment1 = uploadableImageList1.get( 0 ).getAssetFragment() );
    Assert.assertNull( uploadableImageList1.get( 1 ) );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment1.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", photobookJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", photobookJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", photobookJob2.getProductId() );
    Assert.assertEquals( 3, photobookJob2.getOrderQuantity() );

    Assert.assertNotNull( frontCoverAssetFragment = photobookJob2.getFrontCoverUploadableImage().getAssetFragment() );
    Assert.assertEquals( originalProportionalRectangle2, frontCoverAssetFragment.getProportionalRectangle() );

    List<UploadableImage> uploadableImageList2 = photobookJob2.getUploadableImageList();
    Assert.assertEquals( 2, uploadableImageList2.size() );

    Assert.assertNotNull( assetFragment1 = uploadableImageList2.get( 0 ).getAssetFragment() );
    Assert.assertNull( uploadableImageList2.get( 1 ) );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment1.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", photobookJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", photobookJob2.getProductOption( "Parameter2" ) );
    }


  // Summary front page
  public void testPhotobook2()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.PHOTOBOOK, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    // Initialise the SDK so that we can supply a customiser
    KiteSDK.getInstance( getContext(), "NoKey", KiteSDK.DefaultEnvironment.TEST )
            .setCustomiser( PhotobookSummaryCustomiser.class );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 3, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );
    Job job3 = jobList.get( 2 );

    Assert.assertTrue( job1 instanceof PhotobookJob );
    Assert.assertTrue( job2 instanceof PhotobookJob );
    Assert.assertTrue( job3 instanceof PhotobookJob );

    PhotobookJob photobookJob1 = (PhotobookJob)job1;
    PhotobookJob photobookJob2 = (PhotobookJob)job2;
    PhotobookJob photobookJob3 = (PhotobookJob)job3;


    AssetFragment assetFragment1;
    AssetFragment assetFragment2;


    // Job 1

    Assert.assertEquals( "product_id", photobookJob1.getProductId() );
    Assert.assertEquals( 3, photobookJob1.getOrderQuantity() );

    List<UploadableImage> uploadableImageList1 = photobookJob1.getUploadableImageList();
    Assert.assertEquals( 2, uploadableImageList1.size() );

    Assert.assertNull( uploadableImageList1.get( 0 ) );
    Assert.assertNotNull( assetFragment2 = uploadableImageList1.get( 1 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", photobookJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", photobookJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", photobookJob2.getProductId() );
    Assert.assertEquals( 3, photobookJob2.getOrderQuantity() );

    List<UploadableImage> uploadableImageList2 = photobookJob2.getUploadableImageList();
    Assert.assertEquals( 2, uploadableImageList2.size() );

    Assert.assertNull( uploadableImageList2.get( 0 ) );
    Assert.assertNull( uploadableImageList2.get( 1 ) );

    Assert.assertEquals( "Alpha", photobookJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", photobookJob2.getProductOption( "Parameter2" ) );


    // Job 3

    Assert.assertEquals( "product_id", photobookJob3.getProductId() );
    Assert.assertEquals( 3, photobookJob3.getOrderQuantity() );

    List<UploadableImage> uploadableImageList3 = photobookJob3.getUploadableImageList();
    Assert.assertEquals( 2, uploadableImageList3.size() );

    Assert.assertNotNull( assetFragment1 = uploadableImageList3.get( 0 ).getAssetFragment() );
    Assert.assertNotNull( assetFragment2 = uploadableImageList3.get( 1 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment1.getProportionalRectangle() );
    Assert.assertEquals( originalProportionalRectangle2, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", photobookJob3.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", photobookJob3.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * RECTANGLE tests.
   *
   *****************************************************/

  public void testRectangle1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.RECTANGLE, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 2, jobList.size() );

    Job job1 = jobList.get( 0 );
    Job job2 = jobList.get( 1 );

    Assert.assertTrue( job1 instanceof ImagesJob );
    Assert.assertTrue( job2 instanceof ImagesJob );

    ImagesJob imagesJob1 = (ImagesJob)job1;
    ImagesJob imagesJob2 = (ImagesJob)job2;


    AssetFragment assetFragment1;
    AssetFragment assetFragment2;


    // Job 1

    Assert.assertEquals( "product_id", imagesJob1.getProductId() );
    Assert.assertEquals( 3, imagesJob1.getOrderQuantity() );

    List<UploadableImage> uploadableImageList1 = imagesJob1.getUploadableImageList();
    List<String>          borderTextList1      = imagesJob1.getBorderTextList();

    Assert.assertEquals( 2, uploadableImageList1.size() );
    Assert.assertNull( borderTextList1 );

    Assert.assertNotNull( assetFragment1 = uploadableImageList1.get( 0 ).getAssetFragment() );
    Assert.assertNotNull( assetFragment2 = uploadableImageList1.get( 1 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle1, assetFragment1.getProportionalRectangle() );
    Assert.assertEquals( originalProportionalRectangle2, assetFragment2.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob1.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob1.getProductOption( "Parameter2" ) );


    // Job 2

    Assert.assertEquals( "product_id", imagesJob2.getProductId() );
    Assert.assertEquals( 3, imagesJob2.getOrderQuantity() );

    List<UploadableImage> uploadableImageList2 = imagesJob2.getUploadableImageList();
    List<String>          borderTextList2      = imagesJob2.getBorderTextList();

    Assert.assertEquals( 1, uploadableImageList2.size() );
    Assert.assertNull( borderTextList2 );

    Assert.assertNotNull( assetFragment1 = uploadableImageList2.get( 0 ).getAssetFragment() );

    Assert.assertEquals( originalProportionalRectangle2, assetFragment1.getProportionalRectangle() );

    Assert.assertEquals( "Alpha", imagesJob2.getProductOption( "Parameter1" ) );
    Assert.assertEquals( "Bravo", imagesJob2.getProductOption( "Parameter2" ) );
    }


  /*****************************************************
   *
   * Successful order tests.
   *
   *****************************************************/

  public void testSuccess1()
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( getContext() );
    Product product = new Product( "product_id", "product_code", "Product Name", "Product Type","Product category", 0xff000000, UserJourneyType.RECTANGLE, 2 );

    Catalogue catalogue = new Catalogue();
    catalogue.addProduct( "Group Label", null, product );

    HashMap<String,String> optionsMap = new HashMap<>();
    optionsMap.put( "Parameter1", "Alpha" );
    optionsMap.put( "Parameter2", "Bravo" );

    RectF originalProportionalRectangle1 = new RectF( 0.0f, 0.0f, 1.0f, 1.0f );
    RectF originalProportionalRectangle2 = new RectF( 0.3f, 0.25f, 0.8f, 0.75f );

    ImageSpec originalImageSpec1 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle1, null, 1 );
    ImageSpec originalImageSpec2 = new ImageSpec( createSessionAssetFile(), originalProportionalRectangle2, "Second border text", 2 );

    List<ImageSpec> originalImageSpecList = new ArrayList<>();
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec1 );
    originalImageSpecList.add( null );
    originalImageSpecList.add( null );
    originalImageSpecList.add( originalImageSpec2 );


    orderingDataAgent.clearDefaultBasket();

    orderingDataAgent.addItemSynchronously( OrderingDataAgent.CREATE_NEW_ITEM_ID, product, optionsMap, originalImageSpecList, 3 );

    List<BasketItem> basketItemList = orderingDataAgent.getAllItems( catalogue );

    Address shippingAddress = Address.getKiteTeamAddress();

    Order order = new Order( getContext(), basketItemList, shippingAddress, "info@kite.ly", "0123 456789", null );

    List<Job> jobList = order.getJobs();


    Assert.assertEquals( 2, jobList.size() );


    try
      {
      order.setReceipt( "receipt" );
      order.setOrderPricing( new OrderPricing( "{ total: { GBP : 23.50 }, total_shipping_cost: { GBP : 2.99 }, total_product_cost : { GBP : 23.50 }, line_items: [ ] }" ) );

      orderingDataAgent.onOrderSuccess( OrderingDataAgent.NO_ORDER_ID, order );
      }
    catch ( JSONException je )
      {
      Log.e( LOG_TAG, "Unable to set order success", je );

      Assert.fail( je.getMessage() );
      }


    basketItemList = orderingDataAgent.getAllItems( catalogue );

    Assert.assertEquals( 0, basketItemList.size() );
    }


  ////////// Inner Class(es) //////////

  }