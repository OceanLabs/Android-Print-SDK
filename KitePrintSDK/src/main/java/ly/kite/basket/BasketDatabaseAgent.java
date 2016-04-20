/*****************************************************
 *
 * BasketDatabaseAgent.java
 *
 * Derived from TemplateDatabaseAgent.java, copyright (c) 2012, JL
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.ordering.AssetListJob;
import ly.kite.ordering.GreetingCardJob;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.ordering.PhotobookJob;
import ly.kite.ordering.PostcardJob;
import ly.kite.util.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This class manages the basket database.
 *
 *****************************************************/
public class BasketDatabaseAgent extends SQLiteOpenHelper
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG                          = "BasketDatabaseAgent";

  static private final String DATABASE_NAME                    = "basket.db";
  static private final int    DATABASE_VERSION                 = 2;

  static private final String TABLE_ADDRESS                    = "Address";
  static private final String TABLE_ASSET                      = "Asset";
  static private final String TABLE_GREETING_CARD_JOB          = "GreetingCardJob";
  static private final String TABLE_JOB                        = "Job";
  static private final String TABLE_JOB_ASSET                  = "JobAsset";
  static private final String TABLE_OPTION                     = "Option";
  static private final String TABLE_PHOTOBOOK_JOB              = "PhotobookJob";
  static private final String TABLE_POSTCARD_JOB               = "PostcardJob";

  static private final String COLUMN_ASSET_ID                  = "asset_id";

  static private final String JOB_TYPE_ASSET_LIST              = "AL";
  static private final String JOB_TYPE_PHOTOBOOK               = "PB";
  static private final String JOB_TYPE_POSTCARD                = "PC";
  static private final String JOB_TYPE_GREETING_CARD           = "GC";

  static private final String SQL_DROP_ADDRESS_TABLE           = "DROP TABLE " + TABLE_ADDRESS;
  static private final String SQL_DROP_ASSET_TABLE             = "DROP TABLE " + TABLE_ASSET;
  static private final String SQL_DROP_GREETING_CARD_JOB_TABLE = "DROP TABLE " + TABLE_GREETING_CARD_JOB;
  static private final String SQL_DROP_JOB_TABLE               = "DROP TABLE " + TABLE_JOB;
  static private final String SQL_DROP_JOB_ASSET_TABLE         = "DROP TABLE " + TABLE_JOB_ASSET;
  static private final String SQL_DROP_OPTION_TABLE            = "DROP TABLE " + TABLE_OPTION;
  static private final String SQL_DROP_PHOTOBOOK_JOB_TABLE     = "DROP TABLE " + TABLE_PHOTOBOOK_JOB;
  static private final String SQL_DROP_POSTCARD_JOB_TABLE      = "DROP TABLE " + TABLE_POSTCARD_JOB;


  static private final String SQL_CREATE_ADDRESS_TABLE =
          "CREATE TABLE " + TABLE_ADDRESS +
                  " ( " +
                  "id                  INTEGER  PRIMARY KEY," +
                  "recipient_name      TEXT     NOT NULL," +
                  "line1               TEXT     NOT NULL," +
                  "line2               TEXT         NULL," +
                  "city                TEXT         NULL," +
                  "state_or_county     TEXT         NULL," +
                  "zip_or_postal_code  TEXT         NULL," +
                  "country_iso2_code   TEXT     NOT NULL" +
                  " )";


  static private final String SQL_CREATE_ASSET_TABLE =
          "CREATE TABLE " + TABLE_ASSET +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "image_file_path TEXT     NOT NULL" +
                  " )";


  static private final String SQL_CREATE_JOB_TABLE =
          "CREATE TABLE " + TABLE_JOB +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "type            TEXT     NOT NULL," +
                  "product_id      TEXT     NOT NULL" +
                  " )";


  static private final String SQL_CREATE_OPTION_TABLE =
          "CREATE TABLE " + TABLE_OPTION +
                  " ( " +
                  "job_id          INTEGER  NOT NULL," +
                  "name            TEXT     NOT NULL," +
                  "value           TEXT     NOT NULL" +
                  " )";

  static private final String SQL_CREATE_OPTION_INDEX_1 =
          "CREATE UNIQUE INDEX OptionIndex1 ON " + TABLE_OPTION + " ( job_id, name )";


  static private final String SQL_CREATE_JOB_ASSET_TABLE =
          "CREATE TABLE " + TABLE_JOB_ASSET +
                  " ( " +
                  "job_id          INTEGER  NOT NULL," +
                  "asset_index     INTEGER  NOT NULL," +
                  "asset_id        INTEGER      NULL" +
                  " )";

  static private final String SQL_CREATE_JOB_ASSET_INDEX_1 =
          "CREATE UNIQUE INDEX JobAssetIndex1 ON " + TABLE_JOB_ASSET + " ( job_id, asset_index )";


  static private final String SQL_CREATE_PHOTOBOOK_JOB_TABLE =
          "CREATE TABLE " + TABLE_PHOTOBOOK_JOB +
                  " ( " +
                  "job_id                INTEGER  NOT NULL," +
                  "front_cover_asset_id  INTEGER  NOT NULL" +
                  " )";

  static private final String SQL_CREATE_PHOTOBOOK_JOB_INDEX_1 =
          "CREATE UNIQUE INDEX PhotobookJobIndex1 ON " + TABLE_PHOTOBOOK_JOB + " ( job_id )";


  static private final String SQL_CREATE_POSTCARD_JOB_TABLE =
          "CREATE TABLE " + TABLE_POSTCARD_JOB +
                  " ( " +
                  "job_id                INTEGER  NOT NULL," +
                  "front_image_asset_id  INTEGER  NOT NULL," +
                  "back_image_asset_id   INTEGER  NOT NULL," +
                  "message               TEXT         NULL," +
                  "address_id            INTEGER  NOT NULL"  +
                  " )";

  static private final String SQL_CREATE_POSTCARD_JOB_INDEX_1 =
          "CREATE UNIQUE INDEX PostcardJobIndex1 ON " + TABLE_POSTCARD_JOB + " ( job_id )";


  static private final String SQL_CREATE_GREETING_CARD_JOB_TABLE =
          "CREATE TABLE " + TABLE_GREETING_CARD_JOB +
                  " ( " +
                  "job_id                       INTEGER  NOT NULL," +
                  "front_image_asset_id         INTEGER  NOT NULL," +
                  "back_image_asset_id          INTEGER      NULL," +
                  "inside_left_image_asset_id   INTEGER      NULL," +
                  "inside_right_image_asset_id  INTEGER      NULL" +
                  " )";

  static private final String SQL_CREATE_GREETING_CARD_JOB_INDEX_1 =
          "CREATE UNIQUE INDEX GreetingCardJobIndex1 ON " + TABLE_GREETING_CARD_JOB + " ( job_id )";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public BasketDatabaseAgent( Context context, CursorFactory factory )
    {
    super( context, DATABASE_NAME, factory, DATABASE_VERSION );
    }

  
  
  ////////// SQLiteOpenHelper Methods //////////

  /*****************************************************
   *
   * Called when the database is created.
   *
   *****************************************************/
  @Override
  public void onCreate( SQLiteDatabase database )
    {
    // Create the tables and any indexes

    database.execSQL( SQL_CREATE_ADDRESS_TABLE );
    database.execSQL( SQL_CREATE_ASSET_TABLE );
    database.execSQL( SQL_CREATE_GREETING_CARD_JOB_TABLE );
    database.execSQL( SQL_CREATE_JOB_TABLE );
    database.execSQL( SQL_CREATE_JOB_ASSET_TABLE );
    database.execSQL( SQL_CREATE_OPTION_TABLE );
    database.execSQL( SQL_CREATE_PHOTOBOOK_JOB_TABLE );
    database.execSQL( SQL_CREATE_POSTCARD_JOB_TABLE );

    database.execSQL( SQL_CREATE_GREETING_CARD_JOB_INDEX_1 );
    database.execSQL( SQL_CREATE_JOB_ASSET_INDEX_1 );
    database.execSQL( SQL_CREATE_OPTION_INDEX_1 );
    database.execSQL( SQL_CREATE_PHOTOBOOK_JOB_INDEX_1 );
    database.execSQL( SQL_CREATE_POSTCARD_JOB_INDEX_1 );
    }

  
  /*****************************************************
   *
   * Called when the database is upgraded.
   *
   *****************************************************/
  @Override
  public void onUpgrade( SQLiteDatabase database, int oldVersionNumber, int newVersionNumber )
    {
    database.execSQL( SQL_DROP_ADDRESS_TABLE );
    database.execSQL( SQL_DROP_ASSET_TABLE );
    database.execSQL( SQL_DROP_GREETING_CARD_JOB_TABLE );
    database.execSQL( SQL_DROP_JOB_TABLE );
    database.execSQL( SQL_DROP_JOB_ASSET_TABLE );
    database.execSQL( SQL_DROP_OPTION_TABLE );
    database.execSQL( SQL_DROP_PHOTOBOOK_JOB_TABLE );
    database.execSQL( SQL_DROP_POSTCARD_JOB_TABLE );

    onCreate( database );
    }

  
  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the basket.
   *
   *****************************************************/
  public void clearBasket()
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Delete rows in an order that is safe should we ever use foreign keys

    database.execSQL( "DELETE FROM " + TABLE_JOB_ASSET );
    database.execSQL( "DELETE FROM " + TABLE_PHOTOBOOK_JOB );
    database.execSQL( "DELETE FROM " + TABLE_POSTCARD_JOB );
    database.execSQL( "DELETE FROM " + TABLE_GREETING_CARD_JOB );
    database.execSQL( "DELETE FROM " + TABLE_ADDRESS );
    database.execSQL( "DELETE FROM " + TABLE_ASSET );
    database.execSQL( "DELETE FROM " + TABLE_OPTION );
    database.execSQL( "DELETE FROM " + TABLE_JOB );
    }


  /*****************************************************
   *
   * Saves an order to the basket. Note that any jobs
   * within the basket will be added to any existing
   * jobs within the basket.
   *
   *****************************************************/
  public void saveToBasket( Order order )
    {
    if ( order != null )
      {
      // Go through each of the jobs, and save them

      for ( Job job : order.getJobs() )
        {
        saveJob( job );
        }
      }
    }


  /*****************************************************
   *
   * Saves a job to the basket.
   *
   *****************************************************/
  public void saveJob( Job job )
    {
    // Work out what type of job this is

    if ( job instanceof PhotobookJob )
      {
      PhotobookJob photobookJob = (PhotobookJob)job;


      // Create the job and product options

      long jobId = newJob( photobookJob, JOB_TYPE_PHOTOBOOK );

      if ( jobId < 0 ) return;


      // Create the content assets

      long[] contentAssetIds = insertAssets( photobookJob.getAssets() );

      if ( contentAssetIds == null ) return;


      // Create the front cover asset
      long[] coverAssetIds = insertAssets( photobookJob.getFrontCoverAsset() );


      // Create the photobook job
      insertPhotobookJob( jobId, coverAssetIds );

      // Create job assets
      insertJobAssets( jobId, contentAssetIds );
      }
    else if ( job instanceof PostcardJob )
      {
      PostcardJob postcardJob = (PostcardJob)job;


      // Create the job and product options

      long jobId = newJob( postcardJob, JOB_TYPE_POSTCARD );

      if ( jobId < 0 ) return;


      // Create the asset(s)
      long[] assetIds = insertAssets(
              postcardJob.getFrontImageAsset(),
              postcardJob.getBackImageAsset() );


      // Create the address
      long addressId = insertAddress( postcardJob.getAddress() );


      // Create the postcard job
      insertPostcardJob( jobId, assetIds, postcardJob.getMessage(), addressId );
      }
    else if ( job instanceof GreetingCardJob )
      {
      GreetingCardJob greetingCardJob = (GreetingCardJob)job;


      // Create the job and product options

      long jobId = newJob( greetingCardJob, JOB_TYPE_GREETING_CARD );

      if ( jobId < 0 ) return;


      // Create the asset(s)
      long[] assetIds = insertAssets(
              greetingCardJob.getFrontImageAsset(),
              greetingCardJob.getBackImageAsset(),
              greetingCardJob.getInsideLeftImageAsset(),
              greetingCardJob.getInsideRightImageAsset() );


      // Create the greeting card job
      insertGreetingCardJob( jobId, assetIds );
      }
    else if ( job instanceof AssetListJob )
      {
      AssetListJob assetListJob = (AssetListJob)job;


      // Create the job and product options

      long jobId = newJob( assetListJob, JOB_TYPE_ASSET_LIST );

      if ( jobId < 0 ) return;


      // Create the assets

      long[] assetIds = insertAssets( assetListJob.getAssets() );

      if ( assetIds == null ) return;


      // Create job assets
      insertJobAssets( jobId, assetIds );
      }
    }


  /*****************************************************
   *
   * Inserts a job and its new options.
   *
   * @return The (primary key /) id of the new job, or -1,
   *         if the job could not be created.
   *
   *****************************************************/
  private long newJob( Job job, String jobType )
    {
    // Insert the job

    long jobId = insertJob( job, jobType );

    if ( jobId < 0 ) return ( jobId );


    // Insert the options
    insertOptions( jobId, job.getProductOptions() );

    return ( jobId );
    }


  /*****************************************************
   *
   * Inserts a job.
   *
   * @return The (primary key /) id of the new job, or -1,
   *         if the job could not be created.
   *
   *****************************************************/
  private long insertJob( Job job, String jobType )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( -1 );
      }


    // Create the values to be inserted. We don't specify the job id because
    // we want the database to auto-generate it for us.

    ContentValues contentValues = new ContentValues();

    contentValues.put( "type",       jobType );
    contentValues.put( "product_id", job.getProduct().getId() );


    // Try to insert the new job

    try
      {
      long jobId = database.insert( TABLE_JOB, null, contentValues );

      if ( jobId < 0 )
        {
        Log.e( LOG_TAG, "Unable to insert new job" );
        }

      return ( jobId );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new job", exception );

      return ( -1 );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a map of options.
   *
   * @return The (primary keys /) ids of the inserted assets,
   *         or null, if any of the assets could not be created.
   *
   *****************************************************/
  private void insertOptions( long jobId, HashMap<String,String> optionMap )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    try
      {
      // Go through each of the options

      for ( String name : optionMap.keySet() )
        {
        // Create and try to insert the new option

        ContentValues contentValues = new ContentValues();

        contentValues.put( "job_id", jobId );
        contentValues.put( "name",   name );
        contentValues.put( "value",  optionMap.get( name ) );

        database.insert( TABLE_OPTION, null, contentValues );
        }
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new asset", exception );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a list of assets.
   *
   * @return The (primary keys /) ids of the inserted assets,
   *         or null, if any of the assets could not be created.
   *
   *****************************************************/
  private long[] insertAssets( List<Asset> assetList )
    {
    Asset[] assetArray = new Asset[ assetList.size() ];

    assetList.toArray( assetArray );

    return ( insertAssets( assetArray ) );
    }


  /*****************************************************
   *
   * Inserts an array of assets.
   *
   * @return The (primary keys /) ids of the inserted assets,
   *         or null, if any of the assets could not be created.
   *
   *****************************************************/
  private long[] insertAssets( Asset... assets )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( null );
      }


    // Create a return array
    long[] assetIds = new long[ assets.length ];


    try
      {
      // Go through each of the assets in order

      int assetIndex = 0;

      for ( Asset asset : assets )
        {
        // If the asset is null, we use a placeholder value of -1 for the asset id.

        long assetId;

        if ( asset != null )
          {
          // Create and try to insert the new asset

          ContentValues contentValues = new ContentValues();

          // If the asset isn't an image file, this should throw an exception, which is what we want.
          contentValues.put( "image_file_path", asset.getImageFilePath() );

          assetId = database.insert( TABLE_ASSET, null, contentValues );

          if ( assetId < 0 )
            {
            Log.e( LOG_TAG, "Unable to insert new asset" );

            return ( null );
            }
          }
        else
          {
          assetId = -1;
          }


        // Save the asset id
        assetIds[ assetIndex ++ ] = assetId;
        }


      return ( assetIds );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new asset", exception );

      return ( null );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a set of job-asset mappings.
   *
   *****************************************************/
  private void insertJobAssets( long jobId, long[] assetIds )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    try
      {
      // Go through each of the asset ids. We create a row for blank (< 0) asset
      // ids.

      int assetIndex = 0;

      for ( long assetId : assetIds )
        {
        // Create and try to insert the new job asset

        ContentValues jobAssetContentValues = new ContentValues();

        jobAssetContentValues.put( "job_id",      jobId );
        jobAssetContentValues.put( "asset_index", assetIndex ++ );
        putAssetId( jobAssetContentValues, assetId );

        database.insert( TABLE_JOB_ASSET, null, jobAssetContentValues );
        }
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new job asset", exception );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts an address.
   *
   *****************************************************/
  private long insertAddress( Address address )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( -1 );
      }


    // Create the values to be inserted.

    ContentValues contentValues = new ContentValues();

    putString( contentValues, "recipient_name",     address.getRecipientName() );
    putString( contentValues, "line1",              address.getLine1() );
    putString( contentValues, "line2",              address.getLine2() );
    putString( contentValues, "city",               address.getCity() );
    putString( contentValues, "state_or_county",    address.getStateOrCounty() );
    putString( contentValues, "zip_or_postal_code", address.getZipOrPostalCode() );
    putString( contentValues, "country_iso2_code",  address.getCountry().iso2Code() );


    // Try to insert the new address

    try
      {
      long addressId = database.insert( TABLE_ADDRESS, null, contentValues );

      if ( addressId < 0 )
        {
        Log.e( LOG_TAG, "Unable to insert address" );
        }

      return ( addressId );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert address", exception );

      return ( -1 );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a photobook job.
   *
   *****************************************************/
  private void insertPhotobookJob( long jobId, long[] assetIds )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Create the values to be inserted.

    ContentValues contentValues = new ContentValues();

    contentValues.put( "job_id", jobId );

    putAssetId( contentValues, "front_cover_asset_id", assetIds[ 0 ] );


    // Try to insert the new job

    try
      {
      database.insert( TABLE_PHOTOBOOK_JOB, null, contentValues );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert photobook job", exception );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a postcard job.
   *
   *****************************************************/
  private void insertPostcardJob( long jobId, long[] assetIds, String message, long addressId )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Create the values to be inserted.

    ContentValues contentValues = new ContentValues();

    contentValues.put( "job_id", jobId );

    putAssetId( contentValues, "front_image_asset_id", assetIds[ 0 ] );
    putAssetId( contentValues, "back_image_asset_id",  assetIds[ 1 ] );

    putString( contentValues, "message", message );

    contentValues.put( "address_id", addressId );


    // Try to insert the new job

    try
      {
      database.insert( TABLE_POSTCARD_JOB, null, contentValues );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert postcard job", exception );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a greeting card job.
   *
   *****************************************************/
  private void insertGreetingCardJob( long jobId, long[] assetIds )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Create the values to be inserted.

    ContentValues contentValues = new ContentValues();

    contentValues.put( "job_id", jobId );

    putAssetId( contentValues, "front_image_asset_id",        assetIds[ 0 ] );
    putAssetId( contentValues, "back_image_asset_id",         assetIds[ 1 ] );
    putAssetId( contentValues, "inside_left_image_asset_id",  assetIds[ 2 ] );
    putAssetId( contentValues, "inside_right_image_asset_id", assetIds[ 3 ] );


    // Try to insert the new job

    try
      {
      database.insert( TABLE_GREETING_CARD_JOB, null, contentValues );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert greeting card job", exception );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Puts an asset id into a content value, for the supplied
   * key. If the asset id < 0 then a null is put instead.
   *
   *****************************************************/
  private void putAssetId( ContentValues contentValues, String key, long assetId )
    {
    if ( assetId >= 0 ) contentValues.put( key, assetId );
    else                contentValues.putNull( key );
    }


  /*****************************************************
   *
   * Puts an asset id into a content value. If the asset
   * id < 0 then a null is put instead.
   *
   *****************************************************/
  private void putAssetId( ContentValues contentValues, long assetId )
    {
    putAssetId( contentValues, COLUMN_ASSET_ID, assetId );
    }

  /*****************************************************
   *
   * Puts a string into a content value, for the supplied
   * key. If the string is null then a null is put instead.
   *
   *****************************************************/
  private void putString( ContentValues contentValues, String key, String value )
    {
    if ( value != null ) contentValues.put( key, value );
    else                 contentValues.putNull( key );
    }


  /*****************************************************
   *
   * Loads the basket.
   *
   * @return An Order containing all the jobs in the basket.
   *
   *****************************************************/
  public Order loadBasket( Catalogue catalogue )
    {
    // Get all the jobs

    List<ContentValues> jobContentValuesList = selectAllJobs();

    if ( jobContentValuesList == null )
      {
      return ( null );
      }


    // Get all the product options

    SparseArray<HashMap<String,String>> optionsSparseArray = selectAllOptions();

    if ( optionsSparseArray == null )
      {
      return ( null );
      }


    // Get all the assets

    SparseArray<String> assetsSparseArray = selectAllAssets();

    if ( assetsSparseArray == null )
      {
      return ( null );
      }


    // Get all the job assets

    SparseArray<List<Long>> jobAssetsSparseArray = selectAllJobAssets();

    if ( jobAssetsSparseArray == null )
      {
      return ( null );
      }


    // Get all the addresses

    SparseArray<Address> addressesSparseArray = selectAllAddresses();

    if ( addressesSparseArray == null )
      {
      return ( null );
      }


    // We now need to combine all the data structures into one order

    Order basket = new Order();


    // Go through the job list

    for ( ContentValues jobContentValues : jobContentValuesList )
      {
      // Get the base job details

      long   jobId     = jobContentValues.getAsLong( "job_id" );
      String jobType   = jobContentValues.getAsString( "job_type" );
      String productId = jobContentValues.getAsString( "job_product_id" );

      if ( jobType == null )
        {
        Log.e( LOG_TAG, "Type not found for job " + jobId );

        continue;
        }

      Product product = catalogue.getProductById( productId );

      if ( product == null )
        {
        Log.e( LOG_TAG, "Product not found for id " + productId );

        continue;
        }


      // Get any options for this job (which may be null)
      HashMap<String,String> optionsHashMap = optionsSparseArray.get( (int)jobId );


      // Determine the job type

      Job job;

      if ( jobType.equals( JOB_TYPE_GREETING_CARD ) )
        {
        ///// Greeting card /////

        Asset frontImageAsset       = getAsset( assetsSparseArray, jobContentValues.getAsLong( "gc_front_asset_id" ) );
        Asset backImageAsset        = getAsset( assetsSparseArray, jobContentValues.getAsLong( "gc_back_asset_id" ) );
        Asset insideLeftImageAsset  = getAsset( assetsSparseArray, jobContentValues.getAsLong( "gc_inside_left_asset_id" ) );
        Asset insideRightImageAsset = getAsset( assetsSparseArray, jobContentValues.getAsLong( "gc_inside_right_asset_id" ) );

        job = Job.createGreetingCardJob( product, frontImageAsset, backImageAsset, insideLeftImageAsset, insideRightImageAsset );
        }
      else if ( jobType.equals( JOB_TYPE_PHOTOBOOK ) )
        {
        ///// Photobook /////

        Asset frontCoverAsset = getAsset( assetsSparseArray, jobContentValues.getAsLong( "pb_front_asset_id" ) );

        List<Asset> assetList = getAssetList( jobAssetsSparseArray.get( (int)jobId ), assetsSparseArray );

        job = Job.createPhotobookJob( product, frontCoverAsset, assetList );
        }
      else if ( jobType.equals( JOB_TYPE_POSTCARD ) )
        {
        ///// Postcard /////

        Asset   frontImageAsset = getAsset( assetsSparseArray, jobContentValues.getAsLong( "pc_front_asset_id" ) );
        Asset   backImageAsset  = getAsset( assetsSparseArray, jobContentValues.getAsLong( "pc_back_asset_id" ) );
        String  message         = jobContentValues.getAsString( "pc_message" );
        Address address         = getAddress( addressesSparseArray, jobContentValues.getAsLong( "pc_address_id" ) );

        job = Job.createPostcardJob( product, frontImageAsset, backImageAsset, message, address );
        }
      else if ( jobType.equals( JOB_TYPE_ASSET_LIST ) )
        {
        ///// Asset List /////

        List<Asset> assetList = getAssetList( jobAssetsSparseArray.get( (int)jobId ), assetsSparseArray );

        job = Job.createPrintJob( product, optionsHashMap, assetList );
        }
      else
        {
        Log.e( LOG_TAG, "Invalid job type: " + jobType );

        continue;
        }


      basket.addJob( job );
      }


    return ( basket );
    }


  /*****************************************************
   * 
   * Returns a sparse array of content values resulting
   * from selecting all jobs, indexes by job_id.
   * 
   *****************************************************/
  List<ContentValues> selectAllJobs()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT Job.id                                       AS job_id," )
            .append(        "Job.type                                     AS job_type," )
            .append(        "Job.product_id                               AS job_product_id," )
            .append(        "PhotobookJob.front_cover_asset_id            AS pb_front_asset_id," )
            .append(        "PostcardJob.front_image_asset_id             AS pc_front_asset_id," )
            .append(        "PostcardJob.back_image_asset_id              AS pc_back_asset_id," )
            .append(        "PostcardJob.message                          AS pc_message," )
            .append(        "PostcardJob.address_id                       AS pc_address_id," )
            .append(        "GreetingCardJob.front_image_asset_id         AS gc_front_asset_id," )
            .append(        "GreetingCardJob.back_image_asset_id          AS gc_back_asset_id," )
            .append(        "GreetingCardJob.inside_left_image_asset_id   AS gc_inside_left_asset_id," )
            .append(        "GreetingCardJob.inside_right_image_asset_id  AS gc_inside_right_asset_id" )
            .append( "  FROM Job" )
            .append( "  LEFT OUTER JOIN PhotobookJob    ON PhotobookJob.job_id    = Job.id" )
            .append( "  LEFT OUTER JOIN PostcardJob     ON PostcardJob.job_id     = Job.id" )
            .append( "  LEFT OUTER JOIN GreetingCardJob ON GreetingCardJob.job_id = Job.id" )
            .append( " ORDER BY Job.id");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      List<ContentValues> contentValuesList = new ArrayList<>();


      // Process every row; each row corresponds to a single job

      while ( cursor.moveToNext() )
        {
        // Create content values from the cursor

        ContentValues contentValues = new ContentValues();

        long jobId = cursor.getLong(   cursor.getColumnIndex( "job_id" ) );

        contentValues.put( "job_id",         jobId );
        contentValues.put( "job_type",       cursor.getString( cursor.getColumnIndex( "job_type" ) ) );
        contentValues.put( "job_product_id", cursor.getString( cursor.getColumnIndex( "job_product_id" ) ) );

        putLong(   cursor, "pb_front_asset_id",        contentValues );

        putLong(   cursor, "pc_front_asset_id",        contentValues );
        putLong(   cursor, "pc_back_asset_id",         contentValues );
        putString( cursor, "pc_message",               contentValues );
        putLong(   cursor, "pc_address_id",            contentValues );

        putLong(   cursor, "gc_front_asset_id",        contentValues );
        putLong(   cursor, "gc_back_asset_id",         contentValues );
        putLong(   cursor, "gc_inside_left_asset_id",  contentValues );
        putLong(   cursor, "gc_inside_right_asset_id", contentValues );

        contentValuesList.add( contentValues );
        }

      return ( contentValuesList );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all jobs", exception );

      return ( null );
      }
    finally
      {
      // Make sure the cursor is closed
      if ( cursor != null ) cursor.close();
    
      // Make sure the database is closed
      if ( database != null ) database.close();
      }
    
    }


  /*****************************************************
   *
   * Returns a sparse array of product options, indexed by
   * job_id.
   *
   *****************************************************/
  SparseArray<HashMap<String,String>> selectAllOptions()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT job_id," )
            .append(        "name," )
            .append(        "value" )
            .append( "  FROM Option" )
            .append( " ORDER BY job_id, name");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<HashMap<String,String>> optionsSparseArray = new SparseArray<>();

      long currentJobId = -1;

      HashMap<String,String> optionsHashMap = null;


      // Process every row; each row corresponds to an option

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   jobId = cursor.getLong  ( cursor.getColumnIndex( "job_id" ) );
        String name  = cursor.getString( cursor.getColumnIndex( "name" ) );
        String value = cursor.getString( cursor.getColumnIndex( "value" ) );


        // If this is a new job, create a new options hash map and insert it into the array

        if ( jobId != currentJobId )
          {
          currentJobId   = jobId;

          optionsHashMap = new HashMap<>();

          optionsSparseArray.put( (int)jobId, optionsHashMap );
          }


        optionsHashMap.put( name, value );
        }


      return ( optionsSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all options", exception );

      return ( null );
      }
    finally
      {
      // Make sure the cursor is closed
      if ( cursor != null ) cursor.close();

      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    }


  /*****************************************************
   *
   * Returns a sparse array of assets, indexed by asset_id.
   *
   *****************************************************/
  SparseArray<String> selectAllAssets()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id," )
            .append(        "image_file_path" )
            .append( "  FROM Asset" )
            .append( " ORDER BY id");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<String> assetsSparseArray = new SparseArray<>();


      // Process every row; each row corresponds to a single asset

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   assetId       = cursor.getLong  ( cursor.getColumnIndex( "id" ) );
        String imageFilePath = cursor.getString( cursor.getColumnIndex( "image_file_path" ) );

        assetsSparseArray.put( (int)assetId, imageFilePath );
        }


      return ( assetsSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all assets", exception );

      return ( null );
      }
    finally
      {
      // Make sure the cursor is closed
      if ( cursor != null ) cursor.close();

      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    }


  /*****************************************************
   *
   * Returns a sparse array of assets, indexed by
   * job_id.
   *
   *****************************************************/
  SparseArray<List<Long>> selectAllJobAssets()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT job_id," )
            .append(        "asset_id" )
            .append( "  FROM JobAsset" )
            .append( " ORDER BY job_id, asset_index");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<List<Long>> jobAssetsSparseArray = new SparseArray<>();

      long currentJobId = -1;

      List<Long> assetIdList = null;


      // Process every row; each row corresponds to an asset

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long jobId = cursor.getLong  ( cursor.getColumnIndex( "job_id" ) );


        // The asset id may be null (or not exist), so we need to create a blank entry for this

        int assetIdColumnIndex = cursor.getColumnIndex( "asset_id" );

        Long assetIdLong = ( assetIdColumnIndex >= 0 && ( ! cursor.isNull( assetIdColumnIndex ) )
                ? cursor.getLong( assetIdColumnIndex )
                : null );


        // If this is a new job, create a new asset list and insert it into the array

        if ( jobId != currentJobId )
          {
          currentJobId = jobId;

          assetIdList = new ArrayList<>();

          jobAssetsSparseArray.put( (int)jobId, assetIdList );
          }


        assetIdList.add( assetIdLong );
        }


      return ( jobAssetsSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all job assets", exception );

      return ( null );
      }
    finally
      {
      // Make sure the cursor is closed
      if ( cursor != null ) cursor.close();

      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    }


  /*****************************************************
   *
   * Returns a sparse array of addresses, indexed by address_id.
   *
   *****************************************************/
  SparseArray<Address> selectAllAddresses()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id," )
            .append(        "recipient_name," )
            .append(        "line1," )
            .append(        "line2," )
            .append(        "city," )
            .append(        "state_or_county," )
            .append(        "zip_or_postal_code," )
            .append(        "country_iso2_code" )
            .append( "  FROM Address" )
            .append( " ORDER BY id");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<Address> addressesSparseArray = new SparseArray<>();


      // Process every row; each row corresponds to a single address

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   addressId       = cursor.getLong  ( cursor.getColumnIndex( "id" ) );

        String recipientName   = cursor.getString( cursor.getColumnIndex( "recipient_name" ) );
        String line1           = cursor.getString( cursor.getColumnIndex( "line1" ) );
        String line2           = cursor.getString( cursor.getColumnIndex( "line2" ) );
        String city            = cursor.getString( cursor.getColumnIndex( "city" ) );
        String stateOrCounty   = cursor.getString( cursor.getColumnIndex( "state_or_county" ) );
        String zipOrPostalCode = cursor.getString( cursor.getColumnIndex( "zip_or_postal_code" ) );
        Country country        = Country.getInstance( cursor.getString( cursor.getColumnIndex( "country_iso2_code" ) ) );

        Address address        = new Address( recipientName, line1, line2, city, stateOrCounty, zipOrPostalCode, country );

        addressesSparseArray.put( (int)addressId, address );
        }


      return ( addressesSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all addresses", exception );

      return ( null );
      }
    finally
      {
      // Make sure the cursor is closed
      if ( cursor != null ) cursor.close();

      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    }


  /*****************************************************
   *
   * Transfers a long value from a cursor to content values,
   * where it is not null.
   *
   *****************************************************/
  private void putLong( Cursor cursor, String key, ContentValues contentValues )
    {
    int columnIndex = cursor.getColumnIndex( key );

    if ( columnIndex < 0 ) return;

    if ( cursor.isNull( columnIndex ) ) return;

    contentValues.put( key, cursor.getLong( columnIndex ) );
    }


  /*****************************************************
   *
   * Transfers a string value from a cursor to content values,
   * where it is not null.
   *
   *****************************************************/
  private void putString( Cursor cursor, String key, ContentValues contentValues )
    {
    int columnIndex = cursor.getColumnIndex( key );

    if ( columnIndex < 0 ) return;

    if ( cursor.isNull( columnIndex ) ) return;

    contentValues.put( key, cursor.getString( columnIndex ) );
    }


  /*****************************************************
   *
   * Converts a list of asset ids into a list of assets.
   *
   *****************************************************/
  private List<Asset> getAssetList( List<Long> assetIdList, SparseArray<String> imageFilePathSparseArray )
    {
    if ( assetIdList == null ) assetIdList = new ArrayList<>( 0 );

    List<Asset> assetList = new ArrayList<>( assetIdList.size() );

    for ( Long assetIdLong : assetIdList )
      {
      assetList.add( getAsset( imageFilePathSparseArray, assetIdLong ) );
      }

    return ( assetList );
    }


  /*****************************************************
   *
   * Returns an asset for the asset id.
   *
   *****************************************************/
  private Asset getAsset( SparseArray<String> imageFilePathSparseArray, Long assetIdLong )
    {
    return ( assetIdLong != null ? getAsset( imageFilePathSparseArray.get( assetIdLong.intValue() ) ) : null );
    }


  /*****************************************************
   *
   * Returns an asset for the asset id.
   *
   *****************************************************/
  private Asset getAsset( String imageFilePath )
    {
    return ( imageFilePath != null ? new Asset( imageFilePath ) : null );
    }


  /*****************************************************
   *
   * Returns an address for the address id.
   *
   *****************************************************/
  private Address getAddress( SparseArray<Address> addressesSparseArray, Long addressIdLong )
    {
    return ( addressesSparseArray.get( addressIdLong.intValue() ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
