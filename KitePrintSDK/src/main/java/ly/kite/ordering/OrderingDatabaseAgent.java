/*****************************************************
 *
 * OrderingDatabaseAgent.java
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

package ly.kite.ordering;


///// Import(s) /////

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ly.kite.KiteSDK;
import ly.kite.SecurePreferences;
import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.pricing.OrderPricing;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;
import ly.kite.util.AssetHelper;
import ly.kite.util.BitmapDBConverter;


///// Class Declaration /////

/*****************************************************
 *
 * This class manages the basket database.
 *
 *****************************************************/
public class OrderingDatabaseAgent extends SQLiteOpenHelper
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG                                        = "OrderingDatabaseAgent";

  static private final String DATABASE_NAME                                  = "ordering.db";
  static private final int    DATABASE_VERSION                               = 4;

  static private final String TABLE_ADDRESS                                  = "Address";
  static private final String TABLE_BASKET                                   = "Basket";
  static private final String TABLE_IMAGE_SPEC                               = "ImageSpec";
  static private final String TABLE_IMAGE_PREVIEW                            = "ImagePreview";
  static private final String TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER          = "ImageSpecAdditionalParameter";
  static private final String TABLE_ITEM                                     = "Item";
  static private final String TABLE_ITEM_IMAGE_SPEC                          = "ItemImageSpec";
  static private final String TABLE_OPTION                                   = "Option";
  static private final String TABLE_ORDER                                    = "_Order";
  static private final String TABLE_ORDER_ADDITIONAL_PARAMETER               = "OrderAdditionalParameter";

  static private final String COLUMN_IMAGE_SPEC_ID                           = "image_spec_id";

  static private final String SQL_DROP_ADDRESS_TABLE                         = "DROP TABLE " + TABLE_ADDRESS;
  static private final String SQL_DROP_BASKET_TABLE                          = "DROP TABLE " + TABLE_BASKET;
  static private final String SQL_DROP_IMAGE_SPEC_TABLE                      = "DROP TABLE " + TABLE_IMAGE_SPEC;
  static private final String SQL_DROP_IMAGE_SPEC_ADDITIONAL_PARAMETER_TABLE = "DROP TABLE " + TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER;
  static private final String SQL_DROP_ITEM_TABLE                            = "DROP TABLE " + TABLE_ITEM;
  static private final String SQL_DROP_ITEM_IMAGE_SPEC_TABLE                 = "DROP TABLE " + TABLE_ITEM_IMAGE_SPEC;
  static private final String SQL_DROP_OPTION_TABLE                          = "DROP TABLE " + TABLE_OPTION;
  static private final String SQL_DROP_ORDER_TABLE                           = "DROP TABLE " + TABLE_ORDER;
  static private final String SQL_DROP_ORDER_ADDITIONAL_PARAMETER_TABLE      = "DROP TABLE " + TABLE_ORDER_ADDITIONAL_PARAMETER;

  static private final String ORDER_HISTORY_OLD_DATE_FORMAT                      = "dd MMMM yyyy";
  static private final String ORDER_HISTORY_NEW_DATE_FORMAT                      = "dd/MM/yyyy";


  static private final String IMAGE_SPEC_ADDITIONAL_PARAMETER_NAME_BORDER_TEXT = "borderText";

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


  // The Basket table is used simply to obtain and reserve basket ids. Basket id 0
  // is reserved for the current basket, i.e. not associated with an order.
  static private final String SQL_CREATE_BASKET_TABLE =
          "CREATE TABLE " + TABLE_BASKET +
                  " ( " +
                  "id            INTEGER  PRIMARY KEY," +
                  "dummy_column  TEXT     NULL" +
                  " )";


  static private final String SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_TABLE =
          "CREATE TABLE " + TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER +
                  " ( " +
                  "image_spec_id   INTEGER  NOT NULL," +
                  "name            TEXT     NOT NULL," +
                  "value           TEXT     NOT NULL" +
                  " )";

  static private final String SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_INDEX_1 =
          "CREATE UNIQUE INDEX ImageSpecAdditonalParameterIndex1 ON " + TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER + " ( image_spec_id, name )";


  static private final String SQL_CREATE_IMAGE_SPEC_TABLE =
          "CREATE TABLE " + TABLE_IMAGE_SPEC +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "image_file_name TEXT     NOT NULL," +
                  "left            REAL     NOT NULL," +
                  "top             REAL     NOT NULL," +
                  "right           REAL     NOT NULL," +
                  "bottom          REAL     NOT NULL," +
                  "quantity        INTEGER  NOT NULL" +
                  " )";

  static private final String SQL_CREATE_IMAGE_PREVIEW_TABLE =
          "CREATE TABLE " + TABLE_IMAGE_PREVIEW +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "imageBytes      BLOB     NULL" +
                  " )";


  static private final String SQL_CREATE_ITEM_TABLE =
          "CREATE TABLE " + TABLE_ITEM +
                  " ( " +
                  "id              INTEGER  PRIMARY KEY," +
                  "basket_id       INTEGER  NOT NULL," +
                  "product_id      TEXT     NOT NULL," +
                  "order_quantity  INT      NOT NULL" +
                  " )";

  static private final String SQL_CREATE_ITEM_INDEX_1 =
          "CREATE INDEX ItemIndex1 ON " + TABLE_ITEM + " ( basket_id )";


  static private final String SQL_CREATE_ITEM_IMAGE_SPEC_TABLE =
          "CREATE TABLE " + TABLE_ITEM_IMAGE_SPEC +
                  " ( " +
                  "item_id           INTEGER  NOT NULL," +
                  "image_spec_index  INTEGER  NOT NULL," +
                  "image_spec_id     INTEGER      NULL" +
                  " )";

  static private final String SQL_CREATE_ITEM_IMAGE_SPEC_INDEX_1 =
          "CREATE UNIQUE INDEX ItemImageSpecIndex1 ON " + TABLE_ITEM_IMAGE_SPEC + " ( item_id, image_spec_index )";


  static private final String SQL_CREATE_OPTION_TABLE =
          "CREATE TABLE " + TABLE_OPTION +
                  " ( " +
                  "item_id         INTEGER  NOT NULL," +
                  "name            TEXT     NOT NULL," +
                  "value           TEXT     NOT NULL" +
                  " )";

  static private final String SQL_CREATE_OPTION_INDEX_1 =
          "CREATE UNIQUE INDEX OptionIndex1 ON " + TABLE_OPTION + " ( item_id, name )";


  static private final String SQL_CREATE_ORDER_TABLE =
          "CREATE TABLE " + TABLE_ORDER +
                  " ( " +
                  "id                   INTEGER PRIMARY KEY," +
                  "date                 TEXT    NOT NULL," +
                  "description          TEXT    NOT NULL," +
                  "basket_id            INTEGER     NULL," +
                  "shipping_address_id  INTEGER     NULL," +
                  "notification_email   TEXT        NULL," +
                  "notification_phone   TEXT        NULL," +
                  "user_data_json       TEXT        NULL," +
                  "promo_code           TEXT        NULL," +
                  "pricing_json         TEXT        NULL," +
                  "proof_of_payment     TEXT        NULL," +
                  "receipt              TEXT        NULL" +
                  " )";


  static private final String SQL_CREATE_ORDER_ADDITIONAL_PARAMETER_TABLE =
          "CREATE TABLE " + TABLE_ORDER_ADDITIONAL_PARAMETER +
                  " ( " +
                  "order_id             INTEGER NOT NULL," +
                  "name                 TEXT    NOT NULL," +
                  "value                TEXT    NOT NULL" +
                  " )";

  static private final String SQL_CREATE_ORDER_ADDITIONAL_PARAMETER_INDEX_1 =
          "CREATE UNIQUE INDEX OrderAdditionalParameterIndex1 ON " + TABLE_ORDER_ADDITIONAL_PARAMETER + " ( order_id, name )";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns a String from the cursor, or null if the
   * value is not found or an SQL null.
   *
   *****************************************************/
  static private String getStringOrNull( Cursor cursor, String columnName )
    {
    int columnIndex = cursor.getColumnIndex( columnName );

    if ( columnIndex < 0 || cursor.isNull( columnIndex ) ) return ( null );

    return ( cursor.getString( columnIndex ) );
    }


  /*****************************************************
   *
   * Returns a Long from the cursor, or null if the
   * value is not found or an SQL null.
   *
   *****************************************************/
  static private Long getLongOrNull( Cursor cursor, String columnName )
    {
    int columnIndex = cursor.getColumnIndex( columnName );

    if ( columnIndex < 0 || cursor.isNull( columnIndex ) ) return ( null );

    return ( cursor.getLong( columnIndex ) );
    }


  ////////// Constructor(s) //////////

  public OrderingDatabaseAgent( Context context, CursorFactory factory )
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
    database.execSQL( SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_TABLE );
    database.execSQL( SQL_CREATE_BASKET_TABLE );
    database.execSQL( SQL_CREATE_ORDER_TABLE );
    database.execSQL( SQL_CREATE_ITEM_TABLE );
    database.execSQL( SQL_CREATE_ITEM_IMAGE_SPEC_TABLE );
    database.execSQL( SQL_CREATE_OPTION_TABLE );
    database.execSQL( SQL_CREATE_ORDER_ADDITIONAL_PARAMETER_TABLE );

    database.execSQL( SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_INDEX_1 );
    database.execSQL( SQL_CREATE_ITEM_INDEX_1 );
    database.execSQL( SQL_CREATE_ITEM_IMAGE_SPEC_INDEX_1 );
    database.execSQL( SQL_CREATE_OPTION_INDEX_1 );
    database.execSQL( SQL_CREATE_ORDER_ADDITIONAL_PARAMETER_INDEX_1 );
    database.execSQL( SQL_CREATE_IMAGE_PREVIEW_TABLE );


    // Reserve the default basket id
    insertBasket( OrderingDataAgent.BASKET_ID_DEFAULT, database );
    }

  
  /*****************************************************
   *
   * Called when the database is upgraded.
   *
   *****************************************************/
  @Override
  public void onUpgrade( SQLiteDatabase database, int oldVersionNumber, int newVersionNumber )
    {
    if ( oldVersionNumber == 2 && newVersionNumber == 3 )
      {
      database.execSQL( SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_TABLE );
      database.execSQL( SQL_CREATE_IMAGE_SPEC_ADDITIONAL_PARAMETER_INDEX_1 );
      database.execSQL( SQL_CREATE_IMAGE_PREVIEW_TABLE );
      }
    else if ( oldVersionNumber == 3 && newVersionNumber == 4 )
      {
      database.execSQL( SQL_CREATE_IMAGE_PREVIEW_TABLE );
      }
    else
      {
      database.execSQL( SQL_DROP_ADDRESS_TABLE );
      database.execSQL( SQL_DROP_BASKET_TABLE );
      database.execSQL( SQL_DROP_ITEM_IMAGE_SPEC_TABLE );
      database.execSQL( SQL_DROP_IMAGE_SPEC_ADDITIONAL_PARAMETER_TABLE );
      database.execSQL( SQL_DROP_IMAGE_SPEC_TABLE );
      database.execSQL( SQL_DROP_OPTION_TABLE );
      database.execSQL( SQL_DROP_ORDER_ADDITIONAL_PARAMETER_TABLE );
      database.execSQL( SQL_DROP_ITEM_TABLE );
      database.execSQL( SQL_DROP_ORDER_TABLE );
      database.execSQL( SQL_DROP_BASKET_TABLE );
      database.execSQL( SQL_CREATE_IMAGE_PREVIEW_TABLE );

      onCreate( database );
      }
    }

  
  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns a date string for order history.
   *
   *****************************************************/
  private String getDateString()
    {
    SimpleDateFormat dateFormat = new SimpleDateFormat( ORDER_HISTORY_NEW_DATE_FORMAT );

    return ( dateFormat.format( new Date() ) );
    }


  /*****************************************************
   *
   * Returns content values common to both successful and
   * failed orders.
   *
   *****************************************************/
  private ContentValues getOrderContentValues( String description )
    {
    ContentValues contentValues = new ContentValues();

     //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    contentValues.put( "date",         pref.encrypt(getDateString() ));  // e.g. 02 June 2016
    contentValues.put( "description",  pref.encrypt(description ));

    pref.reset();
    return ( contentValues );
    }


  /*****************************************************
   *
   * Creates a new successful order.
   *
   *****************************************************/
  public long insertSuccessfulOrder( String description, String receipt, String pricingJSON )
    {
    // Create the values to be inserted.

    ContentValues contentValues = getOrderContentValues( description );

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    contentValues.put( "pricing_json", pref.encrypt(pricingJSON) );
    contentValues.put( "receipt",      pref.encrypt(receipt) );

    pref.reset();
    return ( insertOrder( contentValues ) );
    }

  /*****************************************************
   *
   * Updates information for a order item (due to lack of encryption)
   *
   *****************************************************/
  private void updateOrderHistoryItem (OrderHistoryItem orderHistoryItem, SQLiteDatabase database) {
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    StringBuilder sqlStringBuilder = new StringBuilder()
        .append( "UPDATE " ).append( TABLE_ORDER )
        .append(   " SET date                = '" ).append( pref.encrypt(orderHistoryItem.getDateString())).append("',")
        .append(        "description         = '" ).append( pref.encrypt(orderHistoryItem.getDescription())).append("',")
        .append(        "notification_email  = '" ).append( pref.encrypt(orderHistoryItem.getNotificationEmail())).append("',")
        .append(        "notification_phone  = '" ).append( pref.encrypt(orderHistoryItem.getNotificationPhone())).append("',")
        .append(        "user_data_json      = '" ).append( pref.encrypt(orderHistoryItem.getUserDataJSON())).append("',")
        .append(        "promo_code          = '" ).append( pref.encrypt(orderHistoryItem.getPromoCode())).append("',")
        .append(        "proof_of_payment    = '" ).append( pref.encrypt(orderHistoryItem.getProofOfPayment())).append("',")
        .append(        "pricing_json        = '" ).append( pref.encrypt(orderHistoryItem.getPricingJSON())).append("',")
        .append(        "receipt             = '" ).append( pref.encrypt(orderHistoryItem.getReceipt())).append( "'" )
        .append( " WHERE id = " ).append( orderHistoryItem.getOrderId() );

    pref.reset();
    if ( database == null )
    {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
    }
    database.execSQL( sqlStringBuilder.toString() );
  }

  /*****************************************************
   *
   * Updates information for a order item (due to lack of encryption)
   *
   *****************************************************/
  private void updateOrderAdditionalParameters(HashMap<String,String> additionalParametersMap, Long orderId, SQLiteDatabase database) {
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    String sqlDelete = "DELETE FROM " + TABLE_ORDER_ADDITIONAL_PARAMETER + " WHERE order_id = " + orderId;

    if (database == null) {
      Log.e(LOG_TAG, "Unable to get writable database");
      return;
    }
    database.execSQL(sqlDelete);
    for ( String name : additionalParametersMap.keySet() ) {
      ContentValues contentValues = new ContentValues();

      contentValues.put("order_id", orderId);
      contentValues.put("name", pref.encrypt(name));
      contentValues.put("value", pref.encrypt(additionalParametersMap.get(name)));
      database.insert( TABLE_ORDER_ADDITIONAL_PARAMETER, null, contentValues );
    }
    pref.reset();
  }

  /*****************************************************
   *
   * Updates information for a order item (due to lack of encryption)
   *
   *****************************************************/
  private void updateAddress(Long addressId, Address address, SQLiteDatabase database) {
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    StringBuilder sqlStringBuilder = new StringBuilder()
        .append( "UPDATE " ).append( TABLE_ADDRESS )
        .append(   " SET recipient_name      = '" ).append( pref.encrypt(address.getRecipientName())).append("',")
        .append(        "line1               = '" ).append( pref.encrypt(address.getLine1())).append("',")
        .append(        "line2               = '" ).append( pref.encrypt(address.getLine2())).append("',")
        .append(        "city                = '" ).append( pref.encrypt(address.getCity())).append("',")
        .append(        "state_or_county     = '" ).append( pref.encrypt(address.getStateOrCounty())).append("',")
        .append(        "zip_or_postal_code  = '" ).append( pref.encrypt(address.getZipOrPostalCode())).append("',")
        .append(        "country_iso2_code   = '" ).append( pref.encrypt(address.getCountry().iso2Code())).append("'")
        .append( " WHERE id = " ).append( addressId );

    pref.reset();
    if ( database == null )
    {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
    }
    database.execSQL( sqlStringBuilder.toString() );
  }

  /*****************************************************
   *
   * Updates basket item
   *
   *****************************************************/
  public void updateItem( long itemId, String productId)
  {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
    {
      Log.e( LOG_TAG, "Unable to get writable database" );
       return;
    }

    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    database.execSQL( "UPDATE " + TABLE_ITEM + " SET product_id = '" +  pref.encrypt(productId) + "' WHERE id = " + itemId );
    database.close();

    pref.reset();
    }

   /*****************************************************
    *
    * Updates basket option
    *
    *****************************************************/
    public void updateBasketOption( long itemId, String name, String value, SQLiteDatabase database)
    {
      SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

      StringBuilder sqlStringBuilder = new StringBuilder()
          .append( "UPDATE " ).append( TABLE_OPTION )
          .append(   " SET name                = '" ).append( pref.encrypt(name)).append("',")
          .append(        "value               = '" ).append( pref.encrypt(value)).append("'")
          .append( " WHERE item_id = " ).append( itemId );

      pref.reset();
      if ( database == null )
      {
        Log.e( LOG_TAG, "Unable to get writable database" );
        return;
      }

      database.execSQL( sqlStringBuilder .toString());
    }
   /*****************************************************
    *
    * Updates image spec
    *
    *****************************************************/
    public void updateImageSpec( long id, String image_file_name, SQLiteDatabase database)
    {
      if ( database == null )
      {
        Log.e( LOG_TAG, "Unable to get writable database" );
        return;
      }

      SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
      database.execSQL( "UPDATE " + TABLE_IMAGE_SPEC + " SET image_file_name = '" +  pref.encrypt(image_file_name) + "' WHERE id = " + id );
      pref.reset();
    }

  /*****************************************************
   *
   * Updates image spec additional parameters
   *
   *****************************************************/
  public void updateImageSpecAdditionalParams(HashMap<String,String> additionalParametersMap, Long id, SQLiteDatabase database)
  {

    String sqlDelete = "DELETE FROM " + TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER + " WHERE image_spec_id  = " + id;

    if (database == null) {
      Log.e(LOG_TAG, "Unable to get writable database");
      return;
    }

    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
    database.execSQL(sqlDelete);
    for ( String name : additionalParametersMap.keySet() ) {
      ContentValues contentValues = new ContentValues();
      contentValues.put("image_spec_id", id);
      contentValues.put("name", pref.encrypt(name));
      contentValues.put("value", pref.encrypt(additionalParametersMap.get(name)));
      database.insert( TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER, null, contentValues );
    }
    pref.reset();
  }

  /*****************************************************
   *
   * Persists an order that has not successfully been submitted,
   * so doesn't have a receipt, but may have any of the other
   * fields.
   *
   *****************************************************/
  public long newOrder( long basketId, Order order )
    {
    ContentValues contentValues = getOrderContentValues( order.getItemsDescription() );

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);


    // Create a shipping address

    Address shippingAddress = order.getShippingAddress();

    if ( shippingAddress != null )
      {
      long addressId = insertAddress( shippingAddress );

      if ( addressId >= 0 )
        {
        contentValues.put( "shipping_address_id", addressId );
        }
      }


    JSONObject   userData     = order.getUserData();
    OrderPricing orderPricing = order.getOrderPricing();


    contentValues.put(              "basket_id",          basketId );
    putStringOrNull( contentValues, "notification_email", pref.encrypt( order.getNotificationEmail() ));
    putStringOrNull( contentValues, "notification_phone", pref.encrypt( order.getNotificationPhoneNumber()) );
    putStringOrNull( contentValues, "user_data_json",     pref.encrypt( userData != null ? userData.toString() : null ) );
    putStringOrNull( contentValues, "promo_code",         pref.encrypt( order.getPromoCode()) );
    putStringOrNull( contentValues, "pricing_json",       pref.encrypt( orderPricing != null ? orderPricing.getPricingJSONString() : null ) );
    putStringOrNull( contentValues, "proof_of_payment",   pref.encrypt( order.getProofOfPayment()) );

    long orderId = insertOrder( contentValues );


    if ( orderId >= 0 )
      {
      // Create any additional options

      HashMap<String,String> additionalParametersMap = order.getAdditionalParameters();

      if ( additionalParametersMap != null )
        {
        insertAdditionalParameters( orderId, additionalParametersMap );
        }
      }

    pref.reset();
    return ( orderId );
    }


  /*****************************************************
   *
   * Creates a new order.
   *
   *****************************************************/
  public long insertOrder( ContentValues contentValues )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( -1 );
      }


    // Try to insert the new order

    try
      {
      long orderId = database.insert( TABLE_ORDER, null, contentValues );

      if ( orderId < 0 )
        {
        Log.e( LOG_TAG, "Unable to insert new order" );
        }

      return ( orderId );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new order", exception );

      return ( -1 );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts a map of additional parameters.
   *
   *****************************************************/
  private void insertAdditionalParameters( long orderId, HashMap<String,String> additionalParametersMap )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
    try
      {
      // Go through each of the options

      for ( String name : additionalParametersMap.keySet() )
        {
        // Create and try to insert the new parameters


        ContentValues contentValues = new ContentValues();

        contentValues.put( "order_id", orderId );
        contentValues.put( "name",     pref.encrypt(name) );
        contentValues.put( "value",    pref.encrypt(additionalParametersMap.get( name )) );

        database.insert( TABLE_ORDER_ADDITIONAL_PARAMETER, null, contentValues );
        }
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert additional parameter", exception );
      }
    finally
      {
      pref.reset();
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Clears the specified basket.
   *
   *****************************************************/
  public void clearBasket( long basketId )
    {
    SQLiteDatabase database = null;

    try
      {
      database = getWritableDatabase();

      if ( database == null )
        {
        Log.e( LOG_TAG, "Unable to get writable database" );

        return;
        }


      // We don't want to duplicate (and risk messing up) the SQL, so select
      // all the items from the basket, and use the delete item method to remove
      // them one by one.

      List<ContentValues> itemContentValuesList = selectBasketItems( database, basketId );

      if ( itemContentValuesList != null )
        {
        for ( ContentValues contentValues : itemContentValuesList )
          {
          deleteItem( database, contentValues.getAsLong( "item_id" ) );
          }
        }


      // Delete the actual basket entry, but never delete the default basket.

      if ( basketId != OrderingDataAgent.BASKET_ID_DEFAULT )
        {
        database.execSQL( "DELETE FROM " + TABLE_BASKET + " WHERE id = " + basketId );
        }
      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Unable to clear basket", e );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Saves an item to a basket.
   *
   *****************************************************/
  public void saveDefaultBasketItem( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity )
    {
    // Create the item and product options

    itemId = newBasketItem( itemId, OrderingDataAgent.BASKET_ID_DEFAULT, product, optionsMap, orderQuantity );

    if ( itemId < 0 ) return;

    // Save preview image if available

    for( int index = 0; index < imageSpecList.size(); index++ )
      {
      if ( imageSpecList.get( index ) != null )
        {
        Asset asset = imageSpecList.get( index ).getAsset();
        if ( asset != null && asset.hasPreviewImage() )
          {
          insertPreviewImage( itemId, asset.getPreviewBitmap() );
          break;
          }
        }
      }

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
  private long newBasketItem( long itemId, long basketId, Product product, HashMap<String,String> optionsMap, int orderQuantity )
    {
    // Insert the item

    itemId = insertBasketItem( itemId, basketId, product, orderQuantity );

    if ( itemId < 0 ) return ( itemId );


    // Insert any options
    if ( optionsMap != null ) insertOptions( itemId, optionsMap );

    return ( itemId );
    }


  /*****************************************************
   *
   * Inserts a basket.
   *
   * @return The (primary key /) id of the new basket, or -1,
   *         if the basket could not be created.
   *
   *****************************************************/
  private long insertBasket( long basketId, SQLiteDatabase database )
    {
    // Create the values to be inserted. If the basket id < 0, we
    // allow the database to create a unique id for us.

    ContentValues contentValues = new ContentValues();

    if ( basketId >= 0 ) contentValues.put( "id", basketId );


    // Try to insert the new item
    basketId = database.insert( TABLE_BASKET, "dummy_column", contentValues );

    if ( basketId < 0 )
      {
      Log.e( LOG_TAG, "Unable to insert new basket" );
      }

    return ( basketId );
    }


  /*****************************************************
   *
   * Inserts a basket.
   *
   * @return The (primary key /) id of the new basket, or -1,
   *         if the basket could not be created.
   *
   *****************************************************/
  public long insertBasket( long basketId )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return ( -1 );
      }


    try
      {
      return ( insertBasket( basketId, database ) );
      }
    catch ( Exception e )
      {
      return ( -1 );
      }
    finally
      {
      if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Inserts an item.
   *
   * @return The (primary key /) id of the new item, or -1,
   *         if the item could not be created.
   *
   *****************************************************/
  private long insertBasketItem( long itemId, long basketId, Product product, int orderQuantity )
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

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    if ( itemId >= 0 ) contentValues.put( "id", itemId );
    contentValues.put( "basket_id",      basketId );
    contentValues.put( "product_id",     pref.encrypt(product.getId()) );
    contentValues.put( "order_quantity", orderQuantity );

    // Try to insert the new item

    try
      {
      itemId = database.insert( TABLE_ITEM, null, contentValues );

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
        pref.reset();
        if ( database != null ) database.close();
      }
    }


  /*****************************************************
   *
   * Updates a previously failed order that is now successful.
   *
   *****************************************************/
  public void updateToSuccessfulOrder( long orderId, String receipt )
    {
    // Create the update query
    // TODO: Do we need to update the date?
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "UPDATE " ).append( TABLE_ORDER )
            .append(   " SET basket_id           = NULL," )
            .append(        "shipping_address_id = NULL," )
            .append(        "notification_email  = NULL," )
            .append(        "notification_phone  = NULL," )
            .append(        "user_data_json      = NULL," )
            .append(        "promo_code          = NULL," )
            .append(        "proof_of_payment    = NULL," )
            .append(        "receipt             = '" ).append( receipt ).append( "'" )
            .append( " WHERE id = " ).append( orderId );


    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    database.execSQL( sqlStringBuilder.toString() );

    database.close();
    }


  /*****************************************************
   *
   * Moves items to another basket.
   *
   *****************************************************/
  public void updateBasket( long oldBasketId, long newBasketId )
    {
    SQLiteDatabase database = getWritableDatabase();

    if ( database == null )
      {
      Log.e( LOG_TAG, "Unable to get writable database" );

      return;
      }


    // Delete rows in an order that is safe
    database.execSQL( "UPDATE " + TABLE_ITEM + " SET basket_id = " + newBasketId + " WHERE basket_id = " + oldBasketId );

    database.close();
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

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    try
      {
      // Go through each of the options

      for ( String name : optionsMap.keySet() )
        {
        // Create and try to insert the new option

        ContentValues contentValues = new ContentValues();

        contentValues.put( "item_id", itemId );
        contentValues.put( "name",    pref.encrypt(name) );
        contentValues.put( "value",   pref.encrypt(optionsMap.get( name )) );

        database.insert( TABLE_OPTION, null, contentValues );
        }
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert option", exception );
      }
    finally
      {
      pref.reset();
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

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

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
          contentValues.put( "image_file_name", pref.encrypt(asset.getImageFileName()));
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


          // Insert any image spec additional parameters

          String borderText = imageSpec.getBorderText();

          if ( borderText != null )
            {
            insertImageSpecAdditionalParameter( database, imageSpecId, IMAGE_SPEC_ADDITIONAL_PARAMETER_NAME_BORDER_TEXT, borderText );
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
      pref.reset();
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
   * Inserts an image spec additional parameter.
   *
   * @return The (primary key /) id of the new item, or -1,
   *         if the item could not be created.
   *
   *****************************************************/
  private long insertImageSpecAdditionalParameter( SQLiteDatabase database, long imageSpecId, String name, String value )
    {
    ContentValues contentValues = new ContentValues();

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    contentValues.put( "image_spec_id", imageSpecId );
    contentValues.put( "name",          pref.encrypt(name) );
    contentValues.put( "value",         pref.encrypt(value) );

    pref.reset();

    // Try to insert the new parameter

    try
      {
      long id = database.insert( TABLE_IMAGE_SPEC_ADDITIONAL_PARAMETER, null, contentValues );

      if ( id < 0 )
        {
        Log.e( LOG_TAG, "Unable to insert new image spec parameter" );
        }

      return ( id );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to insert new image spec parameter", exception );

      return ( -1 );
      }
    }

  /*****************************************************
   *
   * Inserts a preview image
   *
   *****************************************************/
  private void insertPreviewImage(long itemId, Bitmap bitmap)
    {

    final SQLiteDatabase database = getWritableDatabase();

    if (database == null)
      {
      Log.e(LOG_TAG, "Unable to get writable database");

      return;
      }

    try
      {
      //Convert bitmap to byte array
      byte[] imageBytes = BitmapDBConverter.getBytesFromBitmap(bitmap);

      // Create and try to insert the new item image spec
      final ContentValues contentValues = new ContentValues();

      contentValues.put("id", itemId);
      contentValues.put("imageBytes", imageBytes);

      database.insert(TABLE_IMAGE_PREVIEW, null, contentValues);
      }
    catch (Exception exception)
      {
      Log.e(LOG_TAG, "Unable to insert new preview image", exception);
      }
    finally
      {
      if (database != null) database.close();
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

    //////// Encryption initialiser //////////
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    putStringOrNull( contentValues, "recipient_name",     pref.encrypt (address.getRecipientName() ));
    putStringOrNull( contentValues, "line1",              pref.encrypt (address.getLine1() ));
    putStringOrNull( contentValues, "line2",              pref.encrypt (address.getLine2() ));
    putStringOrNull( contentValues, "city",               pref.encrypt (address.getCity()) );
    putStringOrNull( contentValues, "state_or_county",    pref.encrypt (address.getStateOrCounty() ));
    putStringOrNull( contentValues, "zip_or_postal_code", pref.encrypt (address.getZipOrPostalCode() ));
    putStringOrNull( contentValues, "country_iso2_code",  pref.encrypt (address.getCountry().iso2Code() ));

    pref.reset();
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
  private void putStringOrNull( ContentValues contentValues, String key, String value )
    {
    if ( value != null ) contentValues.put( key, value );
    else                 contentValues.putNull( key );
    }


  /*****************************************************
   *
   * Returns a list of order history items.
   *
   *****************************************************/
  List<OrderHistoryItem> loadOrderHistory( Context context, Catalogue catalogue )
    {
    // Get all the shipping addresses
    SparseArray<Address> shippingAddressSparseArray = selectAllShippingAddresses();

    // Get all the additional parameters
    SparseArray<HashMap<String,String>> additionalParametersSparseArray = selectAllAdditionalParameters();

    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id," )
            .append(        "date," )
            .append(        "description," )
            .append(        "basket_id," )
            .append(        "shipping_address_id," )
            .append(        "notification_email," )
            .append(        "notification_phone," )
            .append(        "user_data_json," )
            .append(        "promo_code," )
            .append(        "pricing_json," )
            .append(        "proof_of_payment," )
            .append(        "receipt" )
            .append( "  FROM _Order" )
            .append( " ORDER BY id" );

    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      List<OrderHistoryItem> orderHistoryItemList = new ArrayList<>();

      //////// Decryption initialiser //////////


      // Process every row

      while ( cursor.moveToNext() )
        {
          SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
          boolean hasToBeEncrypted = false;
          long orderId;
          String dateString;
          String description;
          Long basketIdLong = null;
          Long shippingAddressIdLong = null;
          String notificationEmail = null;
          String notificationPhone = null;
          String userDataJSON = null;
          String promoCode = null;
          String pricingJSON;
          String proofOfPayment = null;
          String receipt = null;

          try {
            orderId = cursor.getLong(cursor.getColumnIndex("id"));
            dateString = pref.decrypt(cursor.getString(cursor.getColumnIndex("date")));
            description = pref.decrypt(cursor.getString(cursor.getColumnIndex("description")));
            pricingJSON = pref.decrypt(getStringOrNull(cursor, "pricing_json"));
            basketIdLong = getLongOrNull(cursor, "basket_id");
            shippingAddressIdLong = getLongOrNull(cursor, "shipping_address_id");
            //the following ones can cause a false result so they will be treated separately
            try {
              receipt = pref.decrypt(getStringOrNull(cursor, "receipt"));
            } catch (Exception e) {}
            try {
              proofOfPayment = pref.decrypt(getStringOrNull(cursor, "proof_of_payment"));
            } catch (Exception e) {}
            try {
              notificationEmail = pref.decrypt(getStringOrNull(cursor, "notification_email"));
            } catch (Exception e) {}
            try {
              notificationPhone = pref.decrypt(getStringOrNull(cursor, "notification_phone"));
            } catch (Exception e) {}
            try {
              userDataJSON = pref.decrypt(getStringOrNull(cursor, "user_data_json"));
            } catch (Exception e) {}
            try {
              promoCode = pref.decrypt(getStringOrNull(cursor, "promo_code"));
            } catch (Exception e) {}

          } catch (Exception ex){
            hasToBeEncrypted = true;
            orderId = cursor.getLong(cursor.getColumnIndex("id"));
            dateString = cursor.getString(cursor.getColumnIndex("date"));
            description = cursor.getString(cursor.getColumnIndex("description"));
            basketIdLong = getLongOrNull(cursor, "basket_id");
            shippingAddressIdLong = getLongOrNull(cursor, "shipping_address_id");
            notificationEmail = getStringOrNull(cursor, "notification_email");
            notificationPhone = getStringOrNull(cursor, "notification_phone");
            userDataJSON = getStringOrNull(cursor, "user_data_json");
            promoCode = getStringOrNull(cursor, "promo_code");
            pricingJSON = getStringOrNull(cursor, "pricing_json");
            proofOfPayment = getStringOrNull(cursor, "proof_of_payment");
            receipt = getStringOrNull(cursor, "receipt");
          }
            HashMap<String, String> additionalParametersMap = additionalParametersSparseArray.get((int) orderId);
            Address shippingAddress = (shippingAddressIdLong != null ? shippingAddressSparseArray.get(shippingAddressIdLong.intValue()) : null);

            try
              {
              //Convert new format to Date type
              SimpleDateFormat newDateFormat = new SimpleDateFormat( ORDER_HISTORY_NEW_DATE_FORMAT );
              Date date = newDateFormat.parse( dateString );

              //Convert format to old format for display purposes
              SimpleDateFormat dateFormatForDisplay = new SimpleDateFormat( ORDER_HISTORY_OLD_DATE_FORMAT, Locale.getDefault() );
              dateString = dateFormatForDisplay.format( date ).toString();
              }
            catch (Exception e)
              {
                //fall through
              }

            //Try to get localised product name
            boolean hasInformationChanged = false;
            String newOrderDescription = "";
            try
              {
              JSONObject pricingJSONObject = new JSONObject( pricingJSON );
              JSONArray lineItems = pricingJSONObject.getJSONArray( "line_items" );
              //Go through all items from this specific order
              for ( int index = 0; index < lineItems.length(); index++ )
                {
                JSONObject item = lineItems.getJSONObject( index );
                String templateId = item.getString( "template_id" );
                int quantity = item.getInt( "quantity" );
                //Try searching for the product in the localised catalogue
                Product product = catalogue.findProductById( templateId );
                if ( product != null )
                  {
                  //If product is found replace the item's description
                  //Check if quantity is already in the tile , so it resets to one
                  String newDescription;
                  if( quantity == product.getQuantityPerSheet() )
                    {
                    newDescription = 1 + " x " + product.getDisplayName();
                    }
                  else
                    {
                    newDescription = quantity + " x " + product.getDisplayName();
                    }
                  item.put( "description", newDescription );
                  //Replace item in line items array
                  lineItems.put( index, item );
                  hasInformationChanged = true;
                  //limit preview description to 5 items
                  if( index <= 5 )
                    {
                    if( index == 5 )
                      {
                      //end list with ... if there are over 5 items in the order
                      newOrderDescription = newOrderDescription.concat( "..." );
                      }
                    else
                      {
                      newOrderDescription = newOrderDescription.concat ( newDescription + "\n" );
                      }
                    }
                  }
                else
                  {
                  //fallback , use old description
                  //limit preview description to 5 items
                  if( index <= 5 )
                    {
                    if( index == 5 )
                      {
                      //end list with ... if there are over 5 items in the order
                      newOrderDescription = newOrderDescription.concat( "..." );
                      }
                    else
                      {
                      newOrderDescription = newOrderDescription.concat ( item.getString( "description" ) + "/n" );
                      }
                    }
                  }
                //Rebuild pricingJSON string only if details have changed
                if (hasInformationChanged)
                  {
                  pricingJSONObject.put( "line_items", lineItems );
                  pricingJSON = pricingJSONObject.toString();
                  }
                //Replace order description
                if( newOrderDescription != null )
                  {
                  description = newOrderDescription;
                  }
                }
              }
            catch ( Exception e )
              {
              Log.e( LOG_TAG, "Could not replace order history item title" );
              }


            OrderHistoryItem orderHistoryItem = new OrderHistoryItem(
                    orderId,
                    dateString,
                    description,
                    basketIdLong,
                    shippingAddress,
                    notificationEmail,
                    notificationPhone,
                    userDataJSON,
                    additionalParametersMap,
                    promoCode,
                    pricingJSON,
                    proofOfPayment,
                    receipt);

          pref.reset();
          if(hasToBeEncrypted && SecurePreferences.encryptData) {
            updateOrderHistoryItem(orderHistoryItem, database);
          }

          orderHistoryItemList.add(orderHistoryItem);
        }

      // Now we need to go back through the orders, and load any baskets. In the future, for performance
      // reasons, we may have to do something different here. This will become pretty slow as the number
      // of orders increases.

      for ( OrderHistoryItem orderHistoryItem : orderHistoryItemList )
        {
        Long basketIdLong = orderHistoryItem.getBasketIdLong();

        if ( basketIdLong != null )
          {
          List<BasketItem> basketItemList = loadBasket( context, basketIdLong, catalogue );

          orderHistoryItem.setBasket( basketItemList );
          }
        }

      return ( orderHistoryItemList );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all orders", exception );

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
   * Returns the basket id for an order.
   *
   *****************************************************/
  long selectBasketIdForOrder( long orderId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT basket_id" )
            .append( "  FROM " ).append( TABLE_ORDER )
            .append( " WHERE id = " ).append( orderId );


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );

      if ( cursor.moveToFirst() )
        {
        long basketId = cursor.getLong( cursor.getColumnIndex( "basket_id" ) );

        return ( basketId );
        }

      Log.e( LOG_TAG, "Unable to get basket id" );

      return ( -1 );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select order id", exception );

      return ( -1 );
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
   * Returns a sparse array of maps of order additional
   * parameters, indexed by order id.
   *
   *****************************************************/
  SparseArray<HashMap<String,String>> selectAllAdditionalParameters()
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT order_id," )
            .append(        "name," )
            .append(        "value" )
            .append( "  FROM " ).append( TABLE_ORDER_ADDITIONAL_PARAMETER )
            .append( " ORDER BY order_id, name");


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;
    SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<HashMap<String,String>> additionalParametersSparseArray = new SparseArray<>();

      long currentOrderId = -1;

      HashMap<String,String> additionalParametersMap = null;


      // Process every row; each row corresponds to an additional parameter
      ArrayList<Long> toBeEncrypted = new ArrayList<>();
      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   orderId = cursor.getLong  ( cursor.getColumnIndex( "order_id" ) );

        String name;
        String value;

        try {
          name = pref.decrypt(cursor.getString(cursor.getColumnIndex("name")));
          value = pref.decrypt(cursor.getString(cursor.getColumnIndex("value")));
        } catch (Exception e) {
          if(!toBeEncrypted.contains(orderId)) {
            toBeEncrypted.add(orderId);
          }
          name = cursor.getString(cursor.getColumnIndex("name"));
          value = cursor.getString(cursor.getColumnIndex("value"));
        }

        // If this is a new order, create a new map and insert it into the array

        if ( orderId != currentOrderId )
          {
          currentOrderId = orderId;

          additionalParametersMap = new HashMap<>();

          additionalParametersSparseArray.put( (int)orderId, additionalParametersMap );
          }

        additionalParametersMap.put( name, value );
        }
      pref.reset();
      if(SecurePreferences.encryptData) {
        for (int i = 0; i < toBeEncrypted.size(); i++) {
          Long key = toBeEncrypted.get(i);
          additionalParametersMap = additionalParametersSparseArray.get(key.intValue());
          updateOrderAdditionalParameters(additionalParametersMap, key, database);
        }
      }
      return ( additionalParametersSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select additional parameters", exception );

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
   * Returns a sparse array of shipping addresses, indexed
   * by id.
   *
   *****************************************************/
  SparseArray<Address> selectAllShippingAddresses()
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
            .append( "  FROM " ).append( TABLE_ADDRESS );

    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<Address> shippingAddressSparseArray = new SparseArray<>();


      // Process every row; each row corresponds to an additional parameter

      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        boolean hasToBeEncrypted = false;

        long   addressId;
        String recipientName;
        String line1;
        String line2;
        String city;
        String stateOrCounty;
        String zipOrPostalCode;
        String countryISO2Code;

        try {
          SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
          addressId = cursor.getLong(cursor.getColumnIndex("id"));
          recipientName = pref.decrypt(getStringOrNull(cursor, "recipient_name"));
          line1 = pref.decrypt(getStringOrNull(cursor, "line1"));
          line2 = pref.decrypt(getStringOrNull(cursor, "line2"));
          city = pref.decrypt(getStringOrNull(cursor, "city"));
          stateOrCounty = pref.decrypt(getStringOrNull(cursor, "state_or_county"));
          zipOrPostalCode = pref.decrypt(getStringOrNull(cursor, "zip_or_postal_code"));
          countryISO2Code = pref.decrypt(getStringOrNull(cursor, "country_iso2_code"));
          pref.reset();
        } catch (Exception e) {
          hasToBeEncrypted = true;
          addressId = cursor.getLong(cursor.getColumnIndex("id"));
          recipientName = getStringOrNull(cursor, "recipient_name");
          line1 = getStringOrNull(cursor, "line1");
          line2 = getStringOrNull(cursor, "line2");
          city = getStringOrNull(cursor, "city");
          stateOrCounty = getStringOrNull(cursor, "state_or_county");
          zipOrPostalCode = getStringOrNull(cursor, "zip_or_postal_code");
          countryISO2Code = getStringOrNull(cursor, "country_iso2_code");
        }

        Address shippingAddress = new Address( recipientName, line1, line2, city, stateOrCounty, zipOrPostalCode, Country.getInstance( countryISO2Code ) );

        if(hasToBeEncrypted && SecurePreferences.encryptData) {
          updateAddress(addressId, shippingAddress, database);
        }
        shippingAddressSparseArray.put( (int)addressId, shippingAddress );
        }


      return ( shippingAddressSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select shipping addresses", exception );

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
   * Loads the basket.
   *
   * @return An list of basket items.
   *
   *****************************************************/
  public List<BasketItem> loadBasket( Context context, long basketId, Catalogue catalogue )
    {
    // Get all the items

    List<ContentValues> itemContentValuesList = selectBasketItems( basketId );

    if ( itemContentValuesList == null )
      {
      return ( null );
      }


    // Get all the product options

    SparseArray<HashMap<String,String>> optionsSparseArray = selectOptionsForBasket( basketId );

    if ( optionsSparseArray == null )
      {
      return ( null );
      }


    // Get all the image specs

    SparseArray<ImageSpec> imageSpecSparseArray = selectAllImageSpecs( context, basketId );

    if ( imageSpecSparseArray == null )
      {
      return ( null );
      }


    // Get all the item image specs

    SparseArray<List<Long>> itemImageSpecsSparseArray = selectAllItemImageSpecs( basketId );

    if ( itemImageSpecsSparseArray == null )
      {
      return ( null );
      }



    // Get all the addresses

//    SparseArray<Address> addressesSparseArray = selectAllAddresses( basketId );
//
//    if ( addressesSparseArray == null )
//      {
//      return ( null );
//      }


    // We now need to combine all the data structures into one list of basket items

    List<BasketItem> basketItemList = new ArrayList<>( itemContentValuesList.size() );


    // Go through the item list

    for ( ContentValues itemContentValues : itemContentValuesList )
      {
      // Get the base item details
        long itemId = itemContentValues.getAsLong("item_id");
        String productId;
        try {
          SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
          productId = pref.decrypt(itemContentValues.getAsString("product_id"));
          pref.reset();
        } catch (Exception e) {
          productId = itemContentValues.getAsString("product_id");
          if(SecurePreferences.encryptData) {
            updateItem(itemId, productId);
          }
        }
        int orderQuantity = itemContentValues.getAsInteger("order_quantity");


        // Look up the product from its id

        Product product = catalogue.findProductById(productId);

        if (product == null) {
          Log.e(LOG_TAG, "Product not found for id " + productId);

          continue;
        }


        // Get any options for this job (which may be null)
        HashMap<String, String> optionsMap = optionsSparseArray.get((int) itemId);


        // Get any image specs, and set all their cropped for ids to the product. This is
        // so that if we edit the item later, the creation fragments don't think that the
        // assets were cropped for a different product, and try and crop them again.

        ArrayList<ImageSpec> imageSpecList = getImageSpecList(itemImageSpecsSparseArray.get((int) itemId), imageSpecSparseArray);

        for (ImageSpec imageSpec : imageSpecList) {
          // Don't forget that some image specs may be null, for example: if representing blank pages in
          // a photobook.
          if (imageSpec != null) imageSpec.setCroppedForProductId(productId);
        }

        // Get preview image
        Bitmap previewImage = selectPreviewImage(itemId);
        // Add it to image spec list
        for( int index = 0; index < imageSpecList.size(); index++ )
          {
          if ( imageSpecList.get( index ) != null )
            {
            imageSpecList.get( index ).setPreviewImage( previewImage );
            break;
            }
          }
        // Create a basket item and add it to our list

        BasketItem basketItem = new BasketItem(itemId, product, orderQuantity, optionsMap, imageSpecList);

        basketItemList.add(basketItem);
      }


    return ( basketItemList );
    }


  /*****************************************************
   *
   * Loads the basket.
   *
   * @return An list of basket items.
   *
   *****************************************************/
  public List<BasketItem> loadDefaultBasket( Context context, Catalogue catalogue )
    {
    return ( loadBasket( context, OrderingDataAgent.BASKET_ID_DEFAULT, catalogue ) );
    }


  /*****************************************************
   * 
   * Returns a sparse array of content values resulting
   * from selecting all items, indexed by item_id.
   * 
   *****************************************************/
  List<ContentValues> selectBasketItems( SQLiteDatabase database, long basketId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT id                                      AS item_id," )
            .append(        "product_id                              AS product_id," )
            .append(        "order_quantity                          AS order_quantity" )
            .append( "  FROM Item" )
            .append( " WHERE basket_id = " ).append( basketId )
            .append( " ORDER BY id");


    // Initialise the cursor
    Cursor         cursor   = null;

    try
      {
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
        contentValues.put( "product_id",     cursor.getString( cursor.getColumnIndex( "product_id" )  ) );
        contentValues.put( "order_quantity", cursor.getInt( cursor.getColumnIndex( "order_quantity" ) ) );

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
      }

    }

  /*****************************************************
   *
   * Returns the preview image if available
   *
   *****************************************************/
  Bitmap selectPreviewImage(long itemId)
    {
    // Construct the SQL statement:
    final StringBuilder sqlStringBuilder = new StringBuilder()
                .append("SELECT imageBytes")
                .append("  FROM ").append(TABLE_IMAGE_PREVIEW)
                .append(" WHERE id = ").append(itemId);

    SQLiteDatabase database = null;
    Cursor cursor = null;

    try
      {
      // Open the database
      database = getWritableDatabase();
      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery(sqlStringBuilder.toString(), null);

      if( cursor != null && cursor.moveToFirst() )
        {
        int index = cursor.getColumnIndex("imageBytes");
        byte[] bytes = cursor.getBlob(index);
        if (bytes != null) return BitmapDBConverter.getBitmapFromBytes(bytes);
        }
        return null;
      }
    catch (Exception exception)
      {
      Log.e(LOG_TAG, "Unable to get preview image", exception);
      return null;
      }
    finally
      {
      // Make sure the cursor is closed
      if (cursor != null) cursor.close();

      // Make sure the database is closed
      if (database != null) database.close();
      }
    }


  /*****************************************************
   *
   * Returns a sparse array of content values resulting
   * from selecting all items, indexed by item_id.
   *
   *****************************************************/
  List<ContentValues> selectBasketItems( long basketId )
    {
    SQLiteDatabase database = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      return ( selectBasketItems( database, basketId ) );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select basket items", exception );
      }
    finally
      {
      // Make sure the database is closed
      if ( database != null ) database.close();
      }

    return ( null );
    }


  /*****************************************************
   *
   * Returns a sparse array of product options, indexed by
   * job_id.
   *
   *****************************************************/
  SparseArray<HashMap<String,String>> selectOptionsForBasket( long basketId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT o.item_id," )
            .append(        "o.name," )
            .append(        "o.value" )
            .append( "  FROM Item i," )
            .append( "       Option o" )
            .append( " WHERE i.basket_id = " ).append( basketId )
            .append( "   AND o.item_id = i.id" )
            .append( " ORDER BY o.item_id, o.name");


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
        String name;
        String value;
        try {
          SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
          name = pref.decrypt(cursor.getString(cursor.getColumnIndex("name")));
          value = pref.decrypt(cursor.getString(cursor.getColumnIndex("value")));
          pref.reset();
        } catch (Exception e) {
          name = cursor.getString(cursor.getColumnIndex("name"));
          value = cursor.getString(cursor.getColumnIndex("value"));
          if(SecurePreferences.encryptData) {
            updateBasketOption(itemId, name, value, database);
          }
        }


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
  SparseArray<ImageSpec> selectAllImageSpecs( Context context, long basketId )
    {
    // Get all the image spec additional parameters

    SparseArray<HashMap<String,String>> imageSpecAdditionalParametersSparseArray = selectAllImageSpecAdditionalParameters( context, basketId );

    if ( imageSpecAdditionalParametersSparseArray == null ) imageSpecAdditionalParametersSparseArray = new SparseArray<>();


    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT ispec.id," )
            .append(        "ispec.image_file_name," )
            .append(        "ispec.left," )
            .append(        "ispec.top," )
            .append(        "ispec.right," )
            .append(        "ispec.bottom," )
            .append(        "ispec.quantity" )
            .append( "  FROM Item i," )
            .append( "       ItemImageSpec iis,")
            .append( "       ImageSpec ispec" )
            .append( " WHERE i.basket_id = " ).append( basketId )
            .append( "   AND iis.item_id = i.id" )
            .append( "   AND ispec.id    = iis.image_spec_id" )
            .append( " ORDER BY ispec.id");


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
          long imageSpecId = cursor.getLong(cursor.getColumnIndex("id"));
          String imageFileName;
          try {
            //////// Decryption initialiser //////////
            SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
            imageFileName = pref.decrypt(cursor.getString(cursor.getColumnIndex("image_file_name")));
            pref.reset();
          } catch (Exception e) {
            imageFileName = cursor.getString(cursor.getColumnIndex("image_file_name"));
            if(SecurePreferences.encryptData) {
              updateImageSpec(imageSpecId, imageFileName, database);
            }
          }
          float left = cursor.getFloat(cursor.getColumnIndex("left"));
          float top = cursor.getFloat(cursor.getColumnIndex("top"));
          float right = cursor.getFloat(cursor.getColumnIndex("right"));
          float bottom = cursor.getFloat(cursor.getColumnIndex("bottom"));
          int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

          ImageSpec imageSpec = new ImageSpec(AssetHelper.createExistingBasketAsset(context, basketId, imageFileName), new RectF(left, top, right, bottom), quantity);


          // Restore any additional parameters

          HashMap<String, String> additionalParametersHashMap = imageSpecAdditionalParametersSparseArray.get((int) imageSpecId);

          if (additionalParametersHashMap != null) {
            String borderText = additionalParametersHashMap.get(IMAGE_SPEC_ADDITIONAL_PARAMETER_NAME_BORDER_TEXT);

            if (borderText != null) imageSpec.setBorderText(borderText);
          }


          imageSpecSparseArray.put((int) imageSpecId, imageSpec);
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
   * Returns a sparse array of hash maps, indexed by
   * image spec id.
   *
   *****************************************************/
  SparseArray<HashMap<String,String>> selectAllImageSpecAdditionalParameters( Context context, long basketId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT isap.image_spec_id," )
            .append(        "isap.name," )
            .append(        "isap.value" )
            .append( "  FROM Item i," )
            .append( "       ItemImageSpec iis,")
            .append( "       ImageSpec ispec," )
            .append( "       ImageSpecAdditionalParameter isap" )
            .append( " WHERE i.basket_id        = " ).append( basketId )
            .append( "   AND iis.item_id        = i.id" )
            .append( "   AND ispec.id           = iis.image_spec_id" )
            .append( "   AND isap.image_spec_id = ispec.id" )
            .append( " ORDER BY ispec.id" );


    // Initialise the database and cursor
    SQLiteDatabase database = null;
    Cursor         cursor   = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      // Execute the query, and get a cursor for the result set
      cursor = database.rawQuery( sqlStringBuilder.toString(), null );


      SparseArray<HashMap<String,String>> imageSpecAdditionalParametersSparseArray = new SparseArray<>();


      // Process every row; each row corresponds to a single parameter

      ArrayList<Long> toBeEncrypted = new ArrayList<>();
      while ( cursor.moveToNext() )
        {
        // Get the values from the cursor

        long   imageSpecId = cursor.getLong  ( cursor.getColumnIndex( "image_spec_id" ) );
        String name;
        String value;
        try {
          SecurePreferences pref = new SecurePreferences(KiteSDK.ENCRYPTION_KEY);
          name = pref.decrypt(cursor.getString(cursor.getColumnIndex("name")));
          value = pref.decrypt(cursor.getString(cursor.getColumnIndex("value")));
          pref.reset();
        } catch (Exception e) {
          if(!toBeEncrypted.contains(imageSpecId)) {
            toBeEncrypted.add(imageSpecId);
          }
          name = cursor.getString(cursor.getColumnIndex("name"));
          value = cursor.getString(cursor.getColumnIndex("value"));
        }

        HashMap<String,String> parametersHashMap = imageSpecAdditionalParametersSparseArray.get( (int)imageSpecId );

        if ( parametersHashMap == null )
          {
          parametersHashMap = new HashMap<>();

          imageSpecAdditionalParametersSparseArray.put( (int)imageSpecId, parametersHashMap );
          }

        parametersHashMap.put( name, value );
        }

       if(SecurePreferences.encryptData) {
         for (int i = 0; i < toBeEncrypted.size(); i++) {
           Long key = toBeEncrypted.get(i);
           HashMap<String,String> parametersHashMap = imageSpecAdditionalParametersSparseArray.get(key.intValue());
           updateImageSpecAdditionalParams(parametersHashMap, key, database);
         }
       }

      return ( imageSpecAdditionalParametersSparseArray );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to select all image spec additional parameters", exception );

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
  SparseArray<List<Long>> selectAllItemImageSpecs( long basketId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT iis.item_id," )
            .append(        "iis.image_spec_id" )
            .append( "  FROM Item i," )
            .append( "       ItemImageSpec iis" )
            .append( " WHERE i.basket_id = " ).append( basketId )
            .append( "   AND iis.item_id = i.id" )
            .append( " ORDER BY iis.item_id," )
            .append(           "iis.image_spec_index" );


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


//  /*****************************************************
//   *
//   * Returns a sparse array of addresses, indexed by address_id.
//   *
//   *****************************************************/
//  SparseArray<Address> selectAllAddresses()
//    {
//    // Construct the SQL statement:
//    StringBuilder sqlStringBuilder = new StringBuilder()
//            .append( "SELECT id," )
//            .append(        "recipient_name," )
//            .append(        "line1," )
//            .append(        "line2," )
//            .append(        "city," )
//            .append(        "state_or_county," )
//            .append(        "zip_or_postal_code," )
//            .append(        "country_iso2_code" )
//            .append( "  FROM Address" )
//            .append( " ORDER BY id");
//
//
//    // Initialise the database and cursor
//    SQLiteDatabase database = null;
//    Cursor         cursor   = null;
//
//    try
//      {
//      // Open the database
//      database = getWritableDatabase();
//
//      // Execute the query, and get a cursor for the result set
//      cursor = database.rawQuery( sqlStringBuilder.toString(), null );
//
//
//      SparseArray<Address> addressesSparseArray = new SparseArray<>();
//
//
//      // Process every row; each row corresponds to a single address
//
//      while ( cursor.moveToNext() )
//        {
//        // Get the values from the cursor
//
//        long   addressId       = cursor.getLong  ( cursor.getColumnIndex( "id" ) );
//
//        String recipientName   = cursor.getString( cursor.getColumnIndex( "recipient_name" ) );
//        String line1           = cursor.getString( cursor.getColumnIndex( "line1" ) );
//        String line2           = cursor.getString( cursor.getColumnIndex( "line2" ) );
//        String city            = cursor.getString( cursor.getColumnIndex( "city" ) );
//        String stateOrCounty   = cursor.getString( cursor.getColumnIndex( "state_or_county" ) );
//        String zipOrPostalCode = cursor.getString( cursor.getColumnIndex( "zip_or_postal_code" ) );
//        Country country        = Country.getInstance( cursor.getString( cursor.getColumnIndex( "country_iso2_code" ) ) );
//
//        Address address        = new Address( recipientName, line1, line2, city, stateOrCounty, zipOrPostalCode, country );
//
//        addressesSparseArray.put( (int)addressId, address );
//        }
//
//
//      return ( addressesSparseArray );
//      }
//    catch ( Exception exception )
//      {
//      Log.e( LOG_TAG, "Unable to select all addresses", exception );
//
//      return ( null );
//      }
//    finally
//      {
//      // Make sure the cursor is closed
//      if ( cursor != null ) cursor.close();
//
//      // Make sure the database is closed
//      if ( database != null ) database.close();
//      }
//
//    }


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
   * Counts the number of items in a basket.
   *
   *****************************************************/
  public int selectItemCount( long basketId )
    {
    // Construct the SQL statement:
    StringBuilder sqlStringBuilder = new StringBuilder()
            .append( "SELECT SUM( order_quantity ) AS item_count" )
            .append( "  FROM Item" )
            .append( " WHERE basket_id = ").append( basketId );


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
   * Counts the number of items in the default basket.
   *
   *****************************************************/
  public int selectItemCount()
    {
    return ( selectItemCount( OrderingDataAgent.BASKET_ID_DEFAULT ) );
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
   * Deletes an  item. Since item ids are unique across baskets,
   * we don't need to specify the basket id as well.
   *
   *****************************************************/
  public void deleteItem( SQLiteDatabase database, long itemId )
    {
    // Construct the delete SQL statements.

    String deleteImageSpecAdditionalParameterSQLString = "DELETE" +
            " FROM ImageSpecAdditionalParameter" +
            " WHERE image_spec_id IN ( SELECT image_spec_id" +
            " FROM ItemImageSpec" +
            " WHERE item_id = " + itemId +
            " )";

    String deleteImageSpecSQLString = "DELETE" +
            " FROM ImageSpec" +
            " WHERE id IN ( SELECT image_spec_id" +
            " FROM ItemImageSpec" +
            " WHERE item_id = " + itemId +
            " )";

    String deleteItemImageSpecSQLString = "DELETE FROM ItemImageSpec WHERE item_id = " + itemId;

    String deleteOptionSQLString        = "DELETE FROM Option WHERE item_id = " + itemId;

    String deleteItemSQLString          = "DELETE FROM Item WHERE id = " + itemId;


    try
      {
      // Execute the delete statements
      //database.execSQL( deleteAddressSQLString );
      database.execSQL( deleteImageSpecAdditionalParameterSQLString );
      database.execSQL( deleteImageSpecSQLString );
      database.execSQL( deleteItemImageSpecSQLString );
      database.execSQL( deleteOptionSQLString );
      database.execSQL( deleteItemSQLString );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to delete item", exception );
      }

    //Remove the preview image
    final String deleteImagePreviewSQLString = "DELETE FROM ImagePreview WHERE id = " + itemId;

    try
      {
      database.execSQL(deleteImagePreviewSQLString);
      }
    catch (Exception e)
      {
      Log.i(LOG_TAG, "Unable to delete preview image", e);
      }
    }


  /*****************************************************
   *
   * Deletes an  item. Since item ids are unique across baskets,
   * we don't need to specify the basket id as well.
   *
   *****************************************************/
  public void deleteItem( long itemId )
    {
    SQLiteDatabase database = null;

    try
      {
      // Open the database
      database = getWritableDatabase();

      deleteItem( database, itemId );
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
