/*****************************************************
 *
 * BasketAgent.java
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

package ly.kite.basket;


///// Import(s) /////


///// Class Declaration /////

import android.content.Context;

import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.ordering.AssetListJob;
import ly.kite.ordering.GreetingCardJob;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.ordering.PhotobookJob;
import ly.kite.ordering.PostcardJob;
import ly.kite.util.Asset;
import ly.kite.util.AssetHelper;

/*****************************************************
 *
 * This class manages the basket.
 *
 *****************************************************/
public class BasketAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "BasketAgent";


  ////////// Static Variable(s) //////////

  static private BasketAgent  sBasketAgent;


  ////////// Member Variable(s) //////////

  private Context              mApplicationContext;
  private BasketDatabaseAgent  mDatabaseAgent;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public BasketAgent getInstance( Context context )
    {
    if ( sBasketAgent == null )
      {
      sBasketAgent = new BasketAgent( context );
      }

    return ( sBasketAgent );
    }


  ////////// Constructor(s) //////////

  private BasketAgent( Context context )
    {
    mApplicationContext = context.getApplicationContext();
    mDatabaseAgent      = new BasketDatabaseAgent( mApplicationContext, null );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the basket.
   *
   *****************************************************/
  public BasketAgent clearBasket()
    {
    mDatabaseAgent.clearBasket();

    return ( this );
    }


  /*****************************************************
   *
   * Saves an order to the basket.
   *
   *****************************************************/
  public BasketAgent addToBasket( Order order )
    {
    // Go through all the jobs in the order

    for ( Job job : order.getJobs() )
      {
      addToBasket( job );
      }


    return ( this );
    }


  /*****************************************************
   *
   * Saves a job to the basket.
   *
   *****************************************************/
  public BasketAgent addToBasket( Job originalJob )
    {
    Product                product    = originalJob.getProduct();
    HashMap<String,String> optionsMap = originalJob.getProductOptions();


    // Move any referenced assets to the basket

    Job newJob;

    if ( originalJob instanceof GreetingCardJob )
      {
      GreetingCardJob greetingCardJob = (GreetingCardJob)originalJob;

      Asset frontImageAsset       = AssetHelper.createAsBasketAsset( mApplicationContext, greetingCardJob.getFrontImageAsset() );
      Asset backImageAsset        = AssetHelper.createAsBasketAsset( mApplicationContext, greetingCardJob.getBackImageAsset() );
      Asset insideLeftImageAsset  = AssetHelper.createAsBasketAsset( mApplicationContext, greetingCardJob.getInsideLeftImageAsset() );
      Asset insideRightImageAsset = AssetHelper.createAsBasketAsset( mApplicationContext, greetingCardJob.getInsideLeftImageAsset() );

      newJob = Job.createGreetingCardJob( product, optionsMap, frontImageAsset, backImageAsset, insideLeftImageAsset, insideRightImageAsset );
      }
    else if ( originalJob instanceof PhotobookJob )
      {
      PhotobookJob photobookJob = (PhotobookJob)originalJob;

      Asset frontCoverAsset = AssetHelper.createAsBasketAsset( mApplicationContext, photobookJob.getFrontCoverAsset() );

      List<Asset> contentAssetList = AssetHelper.createAsBasketAssets( mApplicationContext, photobookJob.getAssets() );

      newJob = Job.createPhotobookJob( product, optionsMap, frontCoverAsset, contentAssetList );
      }
    else if ( originalJob instanceof PostcardJob )
      {
      PostcardJob postcardJob = (PostcardJob)originalJob;

      Asset frontImageAsset       = AssetHelper.createAsBasketAsset( mApplicationContext, postcardJob.getFrontImageAsset() );
      Asset backImageAsset        = AssetHelper.createAsBasketAsset( mApplicationContext, postcardJob.getBackImageAsset() );

      newJob = Job.createPostcardJob( product, optionsMap, frontImageAsset, backImageAsset, postcardJob.getMessage(), postcardJob.getAddress() );
      }
    else if ( originalJob instanceof AssetListJob )
      {
      AssetListJob assetListJob = (AssetListJob)originalJob;

      List<Asset> assetList = AssetHelper.createAsBasketAssets( mApplicationContext, assetListJob.getAssets() );

      newJob = Job.createPrintJob( product, optionsMap, assetList );
      }
    else
      {
      throw ( new IllegalArgumentException( "Cannot save unsupported job type to basket: " + originalJob ) );
      }


    mDatabaseAgent.saveToBasket( newJob );


    return ( this );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

