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

import android.test.AndroidTestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the ordering database agent.
 *
 *****************************************************/
public class OrderingDatabaseAgentTests extends AndroidTestCase
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

//  /*****************************************************
//   *
//   * Asset list job tests
//   *
//   *****************************************************/
//
//  public void testAssetList1()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//    Asset asset = new Asset( "/data/data/ly.kite.sample/files/dummy_file_path.jpg" );
//    Job   job   = Job.createPrintJob( Product.DUMMY_PRODUCT, asset );
//
//    databaseAgent.saveItem( job );
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testAssetList2()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    final List<Asset> assetList = new ArrayList<>();
//
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    assetList.add( null );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    assetList.add( null );
//    assetList.add( null );
//
//    Job job = Job.createPrintJob( Product.DUMMY_PRODUCT, assetList );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testAssetList3()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    HashMap<String,String> optionsMap = new HashMap<>();
//
//    optionsMap.put( "name1", "value1" );
//    optionsMap.put( "name2", "value2" );
//
//
//    List<Asset> assetList = new ArrayList<>();
//
//    assetList.add( null );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    assetList.add( null );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    assetList.add( null );
//
//
//    Job job = Job.createPrintJob( Product.DUMMY_PRODUCT, optionsMap, assetList );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  /*****************************************************
//   *
//   * Greeting card job tests
//   *
//   *****************************************************/
//
//  public void testGreetingCard1()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//
//    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1 );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testGreetingCard2()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
//    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
//    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );
//
//    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, null, null );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testGreetingCard3()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
//    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
//    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );
//
//    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, null, asset3, null );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testGreetingCard4()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
//    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
//    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );
//
//    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, null, asset4 );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testGreetingCard5()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" );
//    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" );
//    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );
//
//    Job job = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, asset3, asset4 );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  /*****************************************************
//   *
//   * Photobook job tests
//   *
//   *****************************************************/
//
//  public void testPhotobook1()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset frontCoverAsset = new Asset( "/data/data/ly.kite.sample/files/file_path.jpg" );
//
//
//    final List<Asset> contentAssetList = new ArrayList<>();
//
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( null );
//
//
//    Job job = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  public void testPhotobook2()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//    databaseAgent.clear();
//
//
//    Asset frontCoverAsset = null;
//
//
//    final List<Asset> contentAssetList = new ArrayList<>();
//
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( null );
//
//
//    Job job = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );
//
//
//    databaseAgent.saveItem( job );
//
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job, loadedJob );
//    }
//
//
//  /*****************************************************
//   *
//   * Multiple jobs tests
//   *
//   *****************************************************/
//
//  public void testBasket1()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//
//    // Job 1 = photobook job
//
//    Asset frontCoverAsset = new Asset( "/data/data/ly.kite.sample/files/file_path.jpg" );
//
//    List<Asset> contentAssetList = new ArrayList<>();
//
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( null );
//
//    Job job1 = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );
//
//
//    // Job 2 = greeting card job
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" );
//
//    Job job2 = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1 );
//
//
//    // Clear basket
//
//    databaseAgent.clear();
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 0, loadedJobs.size() );
//
//
//    // Add 1st job
//
//    databaseAgent.saveItem( job1 );
//
//    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 1, loadedJobs.size() );
//
//    Job loadedJob1 = loadedBasket.getJobs().get( 0 );
//
//    Assert.assertEquals( job1, loadedJob1 );
//
//
//    // Add 2nd job
//
//    databaseAgent.saveItem( job2 );
//
//    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 2, loadedJobs.size() );
//
//    loadedJob1 = loadedBasket.getJobs().get( 0 );
//    Job loadedJob2 = loadedBasket.getJobs().get( 1 );
//
//    Assert.assertEquals( job1, loadedJob1 );
//    Assert.assertEquals( job2, loadedJob2 );
//    }
//
//
//  public void testBasket2()
//    {
//    BasketDatabaseAgent databaseAgent = new BasketDatabaseAgent( getContext(), null );
//
//
//    // Job 1 = asset list job
//
//    List<Asset> assetList = new ArrayList<>();
//
//    assetList.add( null );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_1.jpg" ) );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_2.jpg" ) );
//    assetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_3.jpg" ) );
//    assetList.add( null );
//    assetList.add( null );
//
//    HashMap<String,String> optionsMap = new HashMap<>();
//
//    optionsMap.put( "colour", "red" );
//    optionsMap.put( "size", "medium" );
//
//    Job job1 = Job.createPrintJob( Product.DUMMY_PRODUCT, optionsMap, assetList );
//
//
//    // Job 2 = greeting card job
//
//    Asset asset1 = new Asset( "/data/data/ly.kite.sample/files/file_path_4.jpg" );
//    Asset asset2 = new Asset( "/data/data/ly.kite.sample/files/file_path_5.jpg" );
//    Asset asset3 = new Asset( "/data/data/ly.kite.sample/files/file_path_6.jpg" );
//    Asset asset4 = new Asset( "/data/data/ly.kite.sample/files/file_path_7.jpg" );
//
//    Job job2 = Job.createGreetingCardJob( Product.DUMMY_PRODUCT, asset1, asset2, asset3, asset4 );
//
//
//    // Job 3 = photobook job
//
//    Asset frontCoverAsset = new Asset( "/data/data/ly.kite.sample/files/file_path_8.jpg" );
//
//    List<Asset> contentAssetList = new ArrayList<>();
//
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_9.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_10.jpg" ) );
//    contentAssetList.add( new Asset( "/data/data/ly.kite.sample/files/file_path_11.jpg" ) );
//    contentAssetList.add( null );
//    contentAssetList.add( null );
//
//    Job job3 = Job.createPhotobookJob( Product.DUMMY_PRODUCT, frontCoverAsset, contentAssetList );
//
//
//    // Job 4 = postcard job
//
//    Asset frontImageAsset = new Asset( "/data/data/ly.kite.sample/files/file_path_12.jpg" );
//    Asset backImageAsset  = new Asset( "/data/data/ly.kite.sample/files/file_path_14.jpg" );
//
//    Job job4 = Job.createPostcardJob( Product.DUMMY_PRODUCT, frontImageAsset, backImageAsset, "Hello", Address.getKiteTeamAddress() );
//
//
//    // Clear basket
//
//    databaseAgent.clear();
//
//    Order     loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    List<Job> loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 0, loadedJobs.size() );
//
//
//    // Jobs 1, 2
//
//    databaseAgent.saveItem( job1 );
//    databaseAgent.saveItem( job2 );
//
//    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 2, loadedJobs.size() );
//
//    Job loadedJob1 = loadedBasket.getJobs().get( 0 );
//    Job loadedJob2 = loadedBasket.getJobs().get( 1 );
//
//    Assert.assertEquals( job1, loadedJob1 );
//    Assert.assertEquals( job2, loadedJob2 );
//
//
//    // Job 3
//
//    databaseAgent.saveItem( job3 );
//
//    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 3, loadedJobs.size() );
//
//    loadedJob1 = loadedBasket.getJobs().get( 0 );
//    loadedJob2 = loadedBasket.getJobs().get( 1 );
//    Job loadedJob3 = loadedBasket.getJobs().get( 2 );
//
//    Assert.assertEquals( job1, loadedJob1 );
//    Assert.assertEquals( job2, loadedJob2 );
//    Assert.assertEquals( job3, loadedJob3 );
//
//
//    // Job 4
//
//    databaseAgent.saveItem( job4 );
//
//    loadedBasket = databaseAgent.loadBasket( Catalogue.DUMMY_CATALOGUE );
//    loadedJobs   = loadedBasket.getJobs();
//
//    Assert.assertEquals( 4, loadedJobs.size() );
//
//    loadedJob1 = loadedBasket.getJobs().get( 0 );
//    loadedJob2 = loadedBasket.getJobs().get( 1 );
//    loadedJob3 = loadedBasket.getJobs().get( 2 );
//    Job loadedJob4 = loadedBasket.getJobs().get( 3 );
//
//    Assert.assertEquals( job1, loadedJob1 );
//    Assert.assertEquals( job2, loadedJob2 );
//    Assert.assertEquals( job3, loadedJob3 );
//    Assert.assertEquals( job4, loadedJob4 );
//    }


  ////////// Inner Class(es) //////////

  }