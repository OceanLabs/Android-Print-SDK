/*****************************************************
 *
 * BasketDatabaseAgentTests.java
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

package ly.kite.basket;


///// Import(s) /////

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.AndroidTestCase;
import android.util.SparseArray;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.image.ImageAgent;
import ly.kite.journey.UserJourneyType;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.util.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the image agent class.
 *
 *****************************************************/
public class BasketDatabaseAgentTests extends AndroidTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "BasketDatabaseAgentTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Asset list job tests
   *
   *****************************************************/

  public void testAssetList1()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();

    Asset asset = new Asset( "/data/data/ly.kite.sample/files/dummy_file_path.jpg" );
    Job   job   = Job.createPrintJob( Product.DUMMY_PRODUCT, asset );

    databaseAgent.saveJob( job );

    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testAssetList2()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    final List<Asset> assetList = new ArrayList<>();

    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
    assetList.add( null );
    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
    assetList.add( null );
    assetList.add( null );

    Job job = Job.createPrintJob( Product.DUMMY_PRODUCT, assetList );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  /*****************************************************
   *
   * Greeting card job tests
   *
   *****************************************************/

  public void testGreetingCard1()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );

    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1 );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testGreetingCard2()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );

    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, null, null );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testGreetingCard3()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );

    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, null, asset3, null );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testGreetingCard4()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );

    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, null, asset4 );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testGreetingCard5()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );

    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, asset3, asset4 );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  /*****************************************************
   *
   * Photobook job tests
   *
   *****************************************************/

  public void testPhotobook1()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset frontCoverAsset = new Asset( "/data/data/ly.kite.sample/files/file_path.jpg" );


    final List<Asset> contentAssetList = new ArrayList<>();

    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
    contentAssetList.add( null );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
    contentAssetList.add( null );
    contentAssetList.add( null );


    Job job = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  public void testPhotobook2()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );

    databaseAgent.clearBasket();


    Asset frontCoverAsset = null;


    final List<Asset> contentAssetList = new ArrayList<>();

    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
    contentAssetList.add( null );
    contentAssetList.add( null );


    Job job = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );


    databaseAgent.saveJob( job );


    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job, loadedJob );
    }


  /*****************************************************
   *
   * Multiple jobs tests
   *
   *****************************************************/

  public void testBasket1()
    {
    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );


    Asset frontCoverAsset = new Asset( "/data/data/ly.kite.sample/files/file_path.jpg" );

    final List<Asset> contentAssetList = new ArrayList<>();

    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
    contentAssetList.add( null );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
    contentAssetList.add( null );
    contentAssetList.add( null );

    Job job1 = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );


    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );

    Job job2 = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1 );


    // Clear basket

    databaseAgent.clearBasket();

    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    List<Job> loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 0, loadedJobs.size() );


    // Add 1st job

    databaseAgent.saveJob( job1 );

    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 1, loadedJobs.size() );

    Job loadedJob1 = loadedBasket.getJobs().get( 0 );

    Assert.assertEquals( job1, loadedJob1 );


    // Add 2nd job

    databaseAgent.saveJob( job2 );

    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
    loadedJobs   = loadedBasket.getJobs();

    Assert.assertEquals( 2, loadedJobs.size() );

    loadedJob1 = loadedBasket.getJobs().get( 0 );
    Job loadedJob2 = loadedBasket.getJobs().get( 1 );

    Assert.assertEquals( job1, loadedJob1 );
    Assert.assertEquals( job2, loadedJob2 );
    }


  ////////// Inner Class(es) //////////

  }