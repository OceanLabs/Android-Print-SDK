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
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;


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
  static private final int    DATABASE_VERSION                 = 4;

  static private final String TABLE_ADDRESS                    = "Address";
  static private final String TABLE_IMAGE_SPEC                 = "ImageSpec";
  static private final String TABLE_ITEM                       = "Item";
  static private final String TABLE_ITEM_IMAGE_SPEC            = "ItemImageSpec";
  static private final String TABLE_OPTION                     = "Option";

  static private final String COLUMN_IMAGE_SPEC_ID             = "image_spec_id";

  static private final String SQL_DROP_ADDRESS_TABLE           = "DROP TABLE " + TABLE_ADDRESS;
  static private final String SQL_DROP_IMAGE_SPEC_TABLE        = "DROP TABLE " + TABLE_IMAGE_SPEC;
  static private final String SQL_DROP_ITEM_TABLE              = "DROP TABLE " + TABLE_ITEM;
  static private final String SQL_DROP_ITEM_IMAGE_SPEC_TABLE   = "DROP TABLE " + TABLE_ITEM_IMAGE_SPEC;
  static private final String SQL_DROP_OPTION_TABLE            = "DROP TABLE " + TABLE_OPTION;


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


  static private final String SQL_CREATE_IMAGE_SPEC_TABLE =
          "CREATE TABLE " + TABLE_IMAGE_SPEC +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "image_file_path TEXT     NOT NULL," +
                  "left            REAL     NOT NULL," +
                  "top             REAL     NOT NULL," +
                  "right           REAL     NOT NULL," +
                  "bottom          REAL     NOT NULL," +
                  "quantity        INTEGER  NOT NULL" +
                  " )";


  static private final String SQL_CREATE_ITEM_TABLE =
          "CREATE TABLE " + TABLE_ITEM +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "product_id      TEXT     NOT NULL," +
                  "order_quantity  INT      NOT NULL" +
                  " )";


  static private final String SQL_CREATE_OPTION_TABLE =
          "CREATE TABLE " + TABLE_OPTION +
                  " ( " +
                  "item_id         INTEGER  NOT NULL," +
                  "name            TEXT     NOT NULL," +
                  "value           TEXT     NOT NULL" +
                  " )";

  static private final String SQL_CREATE_OPTION_INDEX_1 =
          "CREATE UNIQUE INDEX OptionIndex1 ON " + TABLE_OPTION + " ( item_id, name )";


  static private final String SQL_CREATE_ITEM_IMAGE_SPEC_TABLE =
          "CREATE TABLE " + TABLE_ITEM_IMAGE_SPEC +
                  " ( " +
                  "item_id               INTEGER  NOT NULL," +
                  "image_spec_index  INTEGER  NOT NULL," +
                  "image_spec_id     INTEGER      NULL" +
                  " )";

  static private final String SQL_CREATE_ITEM_IMAGE_SPEC_INDEX_1 =
          "CREATE UNIQUE INDEX ItemImageSpecIndex1 ON " + TABLE_ITEM_IMAGE_SPEC + " ( item_id, image_spec_index )";


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
    database.execSQL( SQL_CREATE_IMAGE_SPEC_TABLE );
    database.execSQL( SQL_CREATE_ITEM_TABLE );
    database.execSQL( SQL_CREATE_ITEM_IMAGE_SPEC_TABLE );
    database.execSQL( SQL_CREATE_OPTION_TABLE );

    database.execSQL( SQL_CREATE_ITEM_IMAGE_SPEC_INDEX_1 );
    database.execSQL( SQL_CREATE_OPTION_INDEX_1 );
    }

  
  /*****************************************************
   *
   * Called when the database is upgraded.
   *
   *****************************************************/
  @Override
  public void onUpgrade( SQLiteDatabase database, int oldVersionNumber, int newVersionNumber )
    {
    if ( oldVersionNumber < 4 )
      {
      database.execSQL( "DROP TABLE Address" );
      database.execSQL( "DROP TABLE JobAsset" );
      database.execSQL( "DROP TABLE Asset" );
      database.execSQL( "DROP TABLE GreetingCardJob" );
      database.execSQL( "DROP TABLE PhotobookJob" );
      database.execSQL( "DROP TABLE PostcardJob" );
      database.execSQL( "DROP TABLE Option" );
      database.execSQL( "DROP TABLE Job" );
      }
    else
      {
      database.execSQL( SQL_DROP_ADDRESS_TABLE );
      database.execSQL( SQL_DROP_ITEM_IMAGE_SPEC_TABLE );
      database.execSQL( SQL_DROP_IMAGE_SPEC_TABLE );
      database.execSQL( SQL_DROP_OPTION_TABLE );
      database.execSQL( SQL_DROP_ITEM_TABLE );
      }

    onCreate( database );
    }

  
  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the basket.
   *
   *****************************************************/
  public void clear()
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Delete rows in an order that is safe should we ever use foreign keys

    database.execSQL( "DELETE FROM " + TABLE_ITEM_IMAGE_SPEC );
    database.execSQL( "DELETE FROM " + TABLE_ADDRESS );
    database.execSQL( "DELETE FROM " + TABLE_IMAGE_SPEC );
    database.execSQL( "DELETE FROM " + TABLE_OPTION );
    database.execSQL( "DELETE FROM " + TABLE_ITEM );
    }


  /*****************************************************
   *
   * Saves an item to the basket.
   *
   *****************************************************/
  public void saveItem( Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity )
    {
    // Create the item and product options

    long itemId = newItem( product, optionsMap, orderQuantity );

    if ( itemId < 0 ) return;


    // Create the image specs

    long[] imageSpecIds = insertImageSpecs( imageSpecList );

    if ( imageSpecIds == null ) return;


    // Create item image specs
    insertItemImageSpecs( itemId, imageSpecIds );
    }


  /*****************************************************
   *
   * Inserts an item and its options.
   *
   * @return The (primary key /) id of the new job, or -1,
   *         if the job could not be created.
   *
   *****************************************************/
  private long newItem( Product product, HashMap<String,String> optionsMap, int orderQuantity )
    {
    // Insert the item

    long itemId = insertItem( product, orderQuantity );

    if ( itemId < 0 ) return ( itemId );


    // Insert the options
    insertOptions( itemId, optionsMap );

    return ( itemId );
    }


  /*****************************************************
   *
   * Inserts an item.
   *
   * @return The (primary key /) id of the new item, or -1,
   *         if the item could not be created.
   *
   *****************************************************/
  private long insertItem( Product product, int orderQuantity )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( -1 );
      }


    // Create the values to be inserted. We don't specify the item id because
    // we want the database to auto-generate it for us.

    ContentValues contentValues = new ContentValues();

    contentValues.put( "product_id",     product.getId() );
    contentValues.put( "order_quantity", orderQuantity );


    // Try to insert the new item

    try
      {
      long itemId = database.insert( TABLE_ITEM, null, contentValues );

      if ( itemId < 0 )
        {
        Log.e( LOG_TAG, "Unable to insert new item" );
        }

      return ( itemId );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new item", exception );

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
  private void insertOptions( long itemId, HashMap<String,String> optionsMap )
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

      for ( String name : optionsMap.keySet() )
        {
        // Create and try to insert the new option

        ContentValues contentValues = new ContentValues();

        contentValues.put( "item_id", itemId );
        contentValues.put( "name",    name );
        contentValues.put( "value",   optionsMap.get( name ) );

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
   * Inserts a list of image specs.
   *
   * @return The (primary keys /) ids of the inserted assets,
   *         or null, if any of the assets could not be created.
   *
   *****************************************************/
  private long[] insertImageSpecs( List<ImageSpec> imageSpecList )
    {
    ImageSpec[] imageSpecArray = new ImageSpec[ imageSpecList.size() ];

    imageSpecList.toArray( imageSpecArray );

    return ( insertImageSpecs( imageSpecArray ) );
    }


  /*****************************************************
   *
   * Inserts an array of image specs.
   *
   * @return The (primary keys /) ids of the inserted assets,
   *         or null, if any of the assets could not be created.
   *
   *****************************************************/
  private long[] insertImageSpecs( ImageSpec... imageSpecs )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( null );
      }


    // Create a return array
    long[] imageSpecIds = new long[ imageSpecs.length ];


    try
      {
      // Go through each of the image specs in order

      int imageSpecIndex = 0;

      for ( ImageSpec imageSpec : imageSpecs )
        {
        // If the image spec is null, we use a placeholder value of -1 for the id.

        long imageSpecId;

        if ( imageSpec != null )
          {
          // Create and try to insert the new image spec

          AssetFragment assetFragment         = imageSpec.getAssetFragment();
          Asset         asset                 = assetFragment.getAsset();
          RectF         proportionalRectangle = assetFragment.getProportionalRectangle();

          ContentValues contentValues = new ContentValues();

          // If the asset isn't an image file, this should throw an exception, which is what we want.
          contentValues.put( "image_file_path", asset.getImageFilePath() );
          contentValues.put( "left",            proportionalRectangle.left );
          contentValues.put( "top",             proportionalRectangle.top );
          contentValues.put( "right",           proportionalRectangle.right );
          contentValues.put( "bottom",          proportionalRectangle.bottom );
          contentValues.put( "quantity",        imageSpec.getQuantity() );

          imageSpecId = database.insert( TABLE_IMAGE_SPEC, null, contentValues );

          if ( imageSpecId < 0 )
            {
            Log.e( LOG_TAG, "Unable to insert new image spec" );

            return ( null );
            }
          }
        else
          {
          imageSpecId = -1;
          }


        // Save the asset id
        imageSpecIds[ imageSpecIndex ++ ] = imageSpecId;
        }


      return ( imageSpecIds );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new image spec", exception );

      return ( null );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a set of item / image spec mappings.
   *
   *****************************************************/
  private void insertItemImageSpecs( long itemId, long[] imageSpecIds )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    try
      {
      // Go through each of the image spec ids. We create a row for blank (< 0) asset
      // ids.

      int imageSpecIndex = 0;

      for ( long imageSpecId : imageSpecIds )
        {
        // Create and try to insert the new item image spec

        ContentValues contentValues = new ContentValues();

        contentValues.put( "item_id",          itemId );
        contentValues.put( "image_spec_index", imageSpecIndex ++ );

        putImageSpecId( contentValues, imageSpecId );

        database.insert( TABLE_ITEM_IMAGE_SPEC, null, contentValues );
        }
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new item image spec", exception );
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
   * Puts an image spec id into a content value, for
   * the supplied key. If the asset id < 0 then a null is
   * put instead.
   *
   *****************************************************/
  private void putImageSpecId( ContentValues contentValues, String key, long imageSpecId )
    {
    if ( imageSpecId >= 0 ) contentValues.put( key, imageSpecId );
    else                    contentValues.putNull( key );
    }


  /*****************************************************
   *
   * Puts an image spec id into a content value. If the
   * asset id < 0 then a null is put instead.
   *
   *****************************************************/
  private void putImageSpecId( ContentValues contentValues, long imageSpecId )
    {
    putImageSpecId( contentValues, COLUMN_IMAGE_SPEC_ID, imageSpecId );
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
   * @return An list of basket items.
   *
   *****************************************************/
  public List<BasketItem> loadBasket( Catalogue catalogue )
    {
    // Get all the items

    List<ContentValues> itemContentValuesList = selectAllItems();

    if ( itemContentValuesList == null )
      {
      return ( null );
      }


    // Get all the product options

    SparseArray<HashMap<String,String>> optionsSparseArray = selectAllOptions();

    if ( optionsSparseArray == null )
      {
      return ( null );
      }


    // Get all the image specs

    SparseArray<ImageSpec> imageSpecSparseArray = selectAllImageSpecs();

    if ( imageSpecSparseArray == null )
      {
      return ( null );
      }


    // Get all the item image specs

    SparseArray<List<Long>> itemImageSpecsSparseArray = selectAllItemImageSpecs();

    if ( itemImageSpecsSparseArray == null )
      {
      return ( null );
      }


    // Get all the addresses

    SparseArray<Address> addressesSparseArray = selectAllAddresses();

    if ( addressesSparseArray == null )
      {
      return ( null );
      }


    // We now need to combine all the data structures into one list of basket items

    List<BasketItem> basketItemList = new ArrayList<>( itemContentValuesList.size() );


    // Go through the item list

    for ( ContentValues itemContentValues : itemContentValuesList )
      {
      // Get the base item details

      long   itemId        = itemContentValues.getAsLong( "item_id" );
      String productId     = itemContentValues.getAsString( "product_id" );
      int    orderQuantity = itemContentValues.getAsInteger( "order_quantity" );


      // Look up the product from its id

      Product product = catalogue.getProductById( productId );

      if ( product == null )
        {
        Log.e( LOG_TAG, "Product not found for id " + productId );

        continue;
        }


      // Get any options for this job (which may be null)
      HashMap<String,String> optionsMap = optionsSparseArray.get( (int)itemId );


      // Get any image specs, and set all their cropped for ids to the product. This is
      // so that if we edit the item later, the creation fragments don't think that the
      // assets were cropped for a different product, and try and crop them again.

      ArrayList<ImageSpec> imageSpecList = getImageSpecList( itemImageSpecsSparseArray.get( (int)itemId ), imageSpecSparseArray );

      for ( ImageSpec imageSpec : imageSpecList )
        {
        imageSpec.setCroppedForProductId( productId );
        }


      // Create a basket item and add it to our list

      BasketItem basketItem = new BasketItem( itemId, product, orderQuantity, optionsMap, imageSpecList );

      basketItemList.add( basketItem );
      }


    return ( basketItemList );
    }


  /*****************************************************
   * 
   * Returns a sparse array of content values resulting
   * from selecting all items, indexed by item_id.
   * 
   *****************************************************/
  List<ContentValues> selectAllItems()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id                                      AS item_id," )
            .append(        "product_id                              AS product_id," )
            .append(        "order_quantity                          AS order_quantity" )
            .append( "  FROM Item" )
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


      List<ContentValues> contentValuesList = new ArrayList<>();


      // Process every row; each row corresponds to a single job

      while ( cursor.moveToNext() )
        {
        // Create content values from the cursor

        ContentValues contentValues = new ContentValues();

        long itemId = cursor.getLong(   cursor.getColumnIndex( "item_id" ) );

        contentValues.put( "item_id",        itemId );
        contentValues.put( "product_id",     cursor.getString( cursor.getColumnIndex( "product_id" ) ) );
        contentValues.put( "order_quantity", cursor.getInt   ( cursor.getColumnIndex( "order_quantity" ) ) );

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
            .append( "SELECT item_id," )
            .append(        "name," )
            .append(        "value" )
            .append( "  FROM Option" )
            .append( " ORDER BY item_id, name");


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

        long   itemId = cursor.getLong  ( cursor.getColumnIndex( "item_id" ) );
        String name   = cursor.getString( cursor.getColumnIndex( "name" ) );
        String value  = cursor.getString( cursor.getColumnIndex( "value" ) );


        // If this is a new job, create a new options hash map and insert it into the array

        if ( itemId != currentJobId )
          {
          currentJobId   = itemId;

          optionsHashMap = new HashMap<>();

          optionsSparseArray.put( (int)itemId, optionsHashMap );
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
   * Returns a sparse array of image specs, indexed by
   * image spec id.
   *
   *****************************************************/
  SparseArray<ImageSpec> selectAllImageSpecs()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id," )
            .append(        "image_file_path," )
            .append(        "left," )
            .append(        "top," )
            .append(        "right," )
            .append(        "bottom," )
            .append(        "quantity" )
            .append( "  FROM ImageSpec" )
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


      SparseArray<ImageSpec> imageSpecSparseArray = new SparseArray<>();


      // Process every row; each row corresponds to a single asset

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   imageSpecId   = cursor.getLong  ( cursor.getColumnIndex( "id" ) );
        String imageFilePath = cursor.getString( cursor.getColumnIndex( "image_file_path" ) );
        float  left          = cursor.getFloat( cursor.getColumnIndex( "left" ) );
        float  top           = cursor.getFloat( cursor.getColumnIndex( "top" ) );
        float  right         = cursor.getFloat( cursor.getColumnIndex( "right" ) );
        float  bottom        = cursor.getFloat( cursor.getColumnIndex( "bottom" ) );
        int    quantity      = cursor.getInt( cursor.getColumnIndex( "quantity" ) );

        ImageSpec imageSpec = new ImageSpec( new Asset( imageFilePath ), new RectF( left, top, right, bottom ), quantity );

        imageSpecSparseArray.put( (int)imageSpecId, imageSpec );
        }


      return ( imageSpecSparseArray );
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
   * Returns a sparse array of assets fragment ids, indexed by
   * item_id.
   *
   *****************************************************/
  SparseArray<List<Long>> selectAllItemImageSpecs()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT item_id," )
            .append(        "image_spec_id" )
            .append( "  FROM ItemImageSpec" )
            .append( " ORDER BY item_id, image_spec_index");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<List<Long>> itemImageSpecsSparseArray = new SparseArray<>();

      long currentItemId = -1;

      List<Long> imageSpecIdList = null;


      // Process every row; each row corresponds to an asset

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long itemId = cursor.getLong  ( cursor.getColumnIndex( "item_id" ) );


        // The asset id may be null (or not exist), so we need to create a blank entry for this

        int imageSpecIdColumnIndex = cursor.getColumnIndex( "image_spec_id" );

        Long imageSpecIdLong = ( imageSpecIdColumnIndex >= 0 && ( ! cursor.isNull( imageSpecIdColumnIndex ) )
                ? cursor.getLong( imageSpecIdColumnIndex )
                : null );


        // If this is a new item, create a new image spec list and insert it into the array

        if ( itemId != currentItemId )
          {
          currentItemId = itemId;

          imageSpecIdList = new ArrayList<>();

          itemImageSpecsSparseArray.put( (int)itemId, imageSpecIdList );
          }


        imageSpecIdList.add( imageSpecIdLong );
        }


      return ( itemImageSpecsSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all item image specs", exception );

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
   * Converts a list of image spec ids into a list of
   * image specs.
   *
   *****************************************************/
  private ArrayList<ImageSpec> getImageSpecList( List<Long> imageSpecIdList, SparseArray<ImageSpec> imageSpecSparseArray )
    {
    if ( imageSpecIdList == null ) imageSpecIdList = new ArrayList<>( 0 );

    ArrayList<ImageSpec> imageSpecList = new ArrayList<>( imageSpecIdList.size() );

    for ( Long imageSpecIdLong : imageSpecIdList )
      {
      imageSpecList.add( getImageSpec( imageSpecSparseArray, imageSpecIdLong ) );
      }

    return ( imageSpecList );
    }


  /*****************************************************
   *
   * Returns an asset for the asset id.
   *
   *****************************************************/
  private ImageSpec getImageSpec( SparseArray<ImageSpec> imageSpecSparseArray, Long imageSpecIdLong )
    {
    return ( imageSpecIdLong != null ? imageSpecSparseArray.get( imageSpecIdLong.intValue() ) : null );
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


  /*****************************************************
   *
   * Counts the number of items in the basket.
   *
   *****************************************************/
  public int selectItemCount()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT SUM( order_quantity ) AS item_count" )
            .append( "  FROM Item" );


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      // Process every row; each row corresponds to a single address

      if ( cursor.moveToFirst() )
        {
        return ( cursor.getInt( cursor.getColumnIndex( "item_count" ) ) );
        }

      return ( 0 );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select item count", exception );

      return ( 0 );
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
   * Updates the order quantity for an item, then returns
   * the new quantity.
   *
   *****************************************************/
  public int updateOrderQuantity( long itemId, int quantityDelta )
    {
    // Construct the update SQL statement:
    StringBuilder updateSQLStringBuilder = new StringBuilder()
            .append( "UPDATE Item" )
            .append(   " SET order_quantity = order_quantity + " ).append( quantityDelta )
            .append( " WHERE id = " ).append( itemId );

    // Construct the select SQL statement:
    StringBuilder selectSQLStringBuilder = new StringBuilder()
            .append( "SELECT order_quantity" )
            .append( "  FROM Item" )
            .append( " WHERE id = " ).append( itemId );


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the update query
      database.execSQL( updateSQLStringBuilder.toString() );

      // Execute the select query, and get a cursor for the result set
      cursor = database.rawQuery( selectSQLStringBuilder.toString(), null );


      // Get the new order quantity

      if ( cursor.moveToFirst() )
        {
        return ( cursor.getInt( cursor.getColumnIndex( "order_quantity" ) ) );
        }

      return ( 0 );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to update order quantity", exception );

      return ( 0 );
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
   * Deletes an  item.
   *
   *****************************************************/
  public void deleteItem( long itemId )
    {
    // Construct the delete SQL statements.

    String deleteImageSpecSQLString = "DELETE" +
                                     " FROM ImageSpec" +
                                    " WHERE id IN ( SELECT image_spec_id" +
                                                    " FROM ItemImageSpec" +
                                                   " WHERE item_id = " + itemId +
                                                " )";

    String deleteItemImageSpecSQLString = "DELETE FROM ItemImageSpec WHERE item_id = " + itemId;

    String deleteOptionSQLString        = "DELETE FROM Option WHERE item_id = " + itemId;

    String deleteItemSQLString          = "DELETE FROM Item WHERE id = " + itemId;


    SQLiteDatabase database = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the delete statements
      //database.execSQL( deleteAddressSQLString );
      database.execSQL( deleteImageSpecSQLString );
      database.execSQL( deleteItemImageSpecSQLString );
      database.execSQL( deleteOptionSQLString );
      database.execSQL( deleteItemSQLString );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to delete item", exception );
      }
    finally
      {
      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
