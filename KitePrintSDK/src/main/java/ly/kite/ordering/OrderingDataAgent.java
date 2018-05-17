/*****************************************************
 *
 * OrderingDataAgent.java
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


///// Class Declaration /////

import android.content.Context;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetHelper;

/*****************************************************
 *
 * This class manages the basket.
 *
 *****************************************************/
public class OrderingDataAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG            = "OrderingDataAgent";

  static public  final int     CREATE_NEW_ITEM_ID = -1;
  static public  final long    BASKET_ID_DEFAULT  = 0;
  static public  final long    NO_ORDER_ID        = -1;


  ////////// Static Variable(s) //////////

  static private OrderingDataAgent sDataAgent;


  ////////// Member Variable(s) //////////

  private Context                mApplicationContext;
  private OrderingDatabaseAgent  mOrderingDatabaseAgent;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public OrderingDataAgent getInstance( Context context )
    {
    if ( sDataAgent == null )
      {
      sDataAgent = new OrderingDataAgent( context );
      }

    return ( sDataAgent );
    }


  ////////// Constructor(s) //////////

  private OrderingDataAgent( Context context )
    {
    mApplicationContext    = context.getApplicationContext();
    mOrderingDatabaseAgent = new OrderingDatabaseAgent( mApplicationContext, null );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears a basket.
   *
   *****************************************************/
  public OrderingDataAgent clearBasket( long basketId )
    {
    mOrderingDatabaseAgent.clearBasket( basketId );

    AssetHelper.clearBasketAssets( mApplicationContext, basketId );

    return ( this );
    }


  /*****************************************************
   *
   * Clears the default basket.
   *
   *****************************************************/
  public OrderingDataAgent clearDefaultBasket()
    {
    return ( clearBasket( BASKET_ID_DEFAULT ) );
    }


  /*****************************************************
   *
   * Saves an item to the basket. This is performed
   * asynchronously because assets may need to be copied
   * into a dedicated directory, so a listener should be
   * provided. The listener is called once the order has
   * been added to the basket.
   *
   *****************************************************/
  public void addItem( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity, IAddListener addListener )
    {
    // Create an add item task and start it
    new AddItemTask( itemId, product, optionsMap, imageSpecList, orderQuantity, addListener ).execute();
    }


  /*****************************************************
   *
   * Saves an item to the basket. This is performed
   * asynchronously because assets may need to be copied
   * into a dedicated directory, so a listener should be
   * provided. The listener is called once the order has
   * been added to the basket.
   *
   *****************************************************/
  public void addItem( Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity, IAddListener addListener )
    {
    addItem( CREATE_NEW_ITEM_ID, product, optionsMap, imageSpecList, orderQuantity, addListener );
    }


  /*****************************************************
   *
   * Saves an item to the basket. This is performed
   * asynchronously because assets may need to be copied
   * into a dedicated directory, so a listener should be
   * provided. The listener is called once the order has
   * been added to the basket.
   *
   *****************************************************/
  public void addItem( Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, IAddListener addListener )
    {
    addItem( product, optionsMap, imageSpecList, 1, addListener );
    }


  /*****************************************************
   *
   * Replaces an item in the default basket.
   *
   *****************************************************/
  public void replaceItem( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity, IAddListener addListener )
    {
    // Delete the item and add a new one

    mOrderingDatabaseAgent.deleteItem( itemId );

    addItem( itemId, product, optionsMap, imageSpecList, orderQuantity, addListener );
    }


  /*****************************************************
   *
   * Saves an item to the default basket synchronously.
   * This should not be called from anywhere other than
   * the AddItemTask.
   *
   *****************************************************/
  public void addItemSynchronously( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity )
    {
    // We need to create a basket item per <mProduct.getQuantityPerSheet()> images, i.e.
    // split the images into multiple jobs.

    List<List<ImageSpec>> splitImageSpecLists = product.getUserJourneyType().dbItemsFromCreationItems( mApplicationContext, imageSpecList, product );

    if ( splitImageSpecLists != null )
      {
      // Each list of image specs now corresponds to a basket item

      for ( List<ImageSpec> itemImageSpecList : splitImageSpecLists )
        {
        if ( itemImageSpecList != null )
          {
          // Move any referenced assets to the basket (if they are not already in)
          itemImageSpecList = AssetHelper.createAsBasketAssets( mApplicationContext, BASKET_ID_DEFAULT, itemImageSpecList );

          // Create or replace the basket item
          mOrderingDatabaseAgent.saveDefaultBasketItem( itemId, product, optionsMap, itemImageSpecList, orderQuantity );

          // If we were supplied an item id then this is an update. However, if more images were
          // subsequently added whilst editing the item - additional jobs are inserted as new ones.
          itemId = CREATE_NEW_ITEM_ID;
          }
        }
      }
    }


  /*****************************************************
   *
   * Returns a list of default basket items.
   *
   *****************************************************/
  public List<BasketItem> getAllItems( Catalogue catalogue )
    {
    return ( mOrderingDatabaseAgent.loadDefaultBasket( mApplicationContext, catalogue ) );
    }


  /*****************************************************
   *
   * Returns the item count for the default basket.
   *
   *****************************************************/
  public int getItemCount()
    {
    return ( mOrderingDatabaseAgent.selectItemCount() );
    }


  /*****************************************************
   *
   * Increments the order quantity for a basket item.
   *
   *****************************************************/
  public int incrementOrderQuantity( long itemId )
    {
    return ( mOrderingDatabaseAgent.updateOrderQuantity( itemId, +1 ) );
    }


  /*****************************************************
   *
   * Decrements the order quantity for a basket item.
   *
   *****************************************************/
  public int decrementOrderQuantity( long itemId )
    {
    int orderQuantity = mOrderingDatabaseAgent.updateOrderQuantity( itemId, -1 );

    // If the order quantity has gone to zero - delete the item
    if ( orderQuantity < 1 )
      {
      mOrderingDatabaseAgent.deleteItem( itemId );
      }

    return ( orderQuantity );
    }


  /*****************************************************
   *
   * Returns a list of order history items.
   *
   *****************************************************/
  public List<OrderHistoryItem> getOrderHistoryList( Catalogue catalogue )
    {
    return ( mOrderingDatabaseAgent.loadOrderHistory( mApplicationContext, catalogue ) );
    }


  /*****************************************************
   *
   * Called when an order was successfully completed from
   * the default basket.
   *
   *****************************************************/
  public void onOrderSuccess( long previousOrderId, Order order )
    {
    if ( previousOrderId >= 0 )
      {
      // If a previously failed order has now been successful, it will already have its own
      // order id, so we just need to:
      //   - Determine its basket id
      //   - Remove its basket (assets & database entries)
      //   - Clear all order details except those required for a successful order history entry

      long basketId = mOrderingDatabaseAgent.selectBasketIdForOrder( previousOrderId );

      if ( basketId >= 0 )
        {
        clearBasket( basketId );

        mOrderingDatabaseAgent.updateToSuccessfulOrder( previousOrderId, order.getReceipt() );
        }
      }
    else
      {
      // This is a new order, so simply clear its basket and create a new database order

      clearDefaultBasket();

      mOrderingDatabaseAgent.insertSuccessfulOrder( order.getItemsDescription(), order.getReceipt(), order.getOrderPricing().getPricingJSONString() );
      }
    }


  /*****************************************************
   *
   * Called when an order failed.
   *
   *****************************************************/
  public long onOrderFailure( long previousOrderId, Order order )
    {
      //Do not store failed order in order history , instead do NOT clear the basket s.t. the user
      //can go back and change details (ex: payment details)
      return ( mOrderingDatabaseAgent.selectBasketIdForOrder( previousOrderId ) );
//    if ( previousOrderId >= 0 )
//      {
      // If the order has already failed at least once, and fails
      // again - we don't need to do anything to it.
      // TODO: Do we need to update the date?
      // So simply retrieve the basket id and return it.

//      return ( mOrderingDatabaseAgent.selectBasketIdForOrder( previousOrderId ) );
//      }
//    else
//      {
      // Get a new basket id
//      long newBasketId = mOrderingDatabaseAgent.insertBasket( -1 );
//
//
//      // Move items and assets from the default basket to the new basket
//
//      AssetHelper.moveBasket( mApplicationContext, BASKET_ID_DEFAULT, newBasketId );
//
//      //mOrderingDatabaseAgent.updateBasket( BASKET_ID_DEFAULT, newBasketId );
//
//
//      // Create the new order on the database, and return its id
//      return ( mOrderingDatabaseAgent.newOrder( newBasketId, order ) );
//      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Interface for add-to-basket listeners.
   *
   *****************************************************/
  public interface IAddListener
    {
    public void onItemAdded();
    }


  /*****************************************************
   *
   * Task for adding orders to the default basket.
   *
   *****************************************************/
  private class AddItemTask extends AsyncTask<Void,Void,Void>
    {
    private long                    mItemId;
    private Product                 mProduct;
    private HashMap<String,String>  mOptionsMap;
    private List<ImageSpec>         mImageSpecList;
    private int                     mOrderQuantity;
    private IAddListener            mAddListener;


    AddItemTask( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity, IAddListener addListener )
      {
      mItemId        = itemId;
      mProduct       = product;
      mOptionsMap    = optionsMap;
      mImageSpecList = imageSpecList;
      mOrderQuantity = orderQuantity;
      mAddListener   = addListener;
      }


    @Override
    protected Void doInBackground( Void... params )
      {
      addItemSynchronously( mItemId, mProduct, mOptionsMap, mImageSpecList, mOrderQuantity );

      return ( null );
      }


    @Override
    protected void onPostExecute( Void result )
      {
      if ( mAddListener != null ) mAddListener.onItemAdded();
      }


    }

  }

