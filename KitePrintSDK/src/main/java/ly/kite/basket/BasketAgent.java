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
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.ordering.ImageSpec;
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
  public BasketAgent clear()
    {
    mDatabaseAgent.clear();

    AssetHelper.clearBasketAssets( mApplicationContext );

    return ( this );
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
    addItem( -1, product, optionsMap, imageSpecList, orderQuantity, addListener );
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
   * Replaces an item.
   *
   *****************************************************/
  public void replaceItem( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity, IAddListener addListener )
    {
    // Delete the item and add a new one

    mDatabaseAgent.deleteItem( itemId );

    addItem( itemId, product, optionsMap, imageSpecList, orderQuantity, addListener );
    }


  /*****************************************************
   *
   * Saves an item to the basket synchronously.
   *
   *****************************************************/
  private void addItem( long itemId, Product product, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, int orderQuantity )
    {
    // Move any referenced assets to the basket
    List<ImageSpec> basketImageSpecList = AssetHelper.createAsBasketAssets( mApplicationContext, imageSpecList );

    mDatabaseAgent.saveItem( itemId, product, optionsMap, basketImageSpecList, orderQuantity );
    }


  /*****************************************************
   *
   * Returns a list of basket items.
   *
   *****************************************************/
  public List<BasketItem> getAllItems( Catalogue catalogue )
    {
    return ( mDatabaseAgent.loadBasket( catalogue ) );
    }


  /*****************************************************
   *
   * Returns the item count.
   *
   *****************************************************/
  public int getItemCount()
    {
    return ( mDatabaseAgent.selectItemCount() );
    }


  /*****************************************************
   *
   * Increments the order quantity.
   *
   *****************************************************/
  public int incrementOrderQuantity( long jobId )
    {
    return ( mDatabaseAgent.updateOrderQuantity( jobId, +1 ) );
    }


  /*****************************************************
   *
   * Decrements the order quantity.
   *
   *****************************************************/
  public int decrementOrderQuantity( long jobId )
    {
    int orderQuantity = mDatabaseAgent.updateOrderQuantity( jobId, -1 );

    // If the order quantity has gone to zero - delete the job
    if ( orderQuantity < 1 )
      {
      mDatabaseAgent.deleteItem( jobId );
      }

    return ( orderQuantity );
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
   * Task for adding orders to the basket.
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
      addItem( mItemId, mProduct, mOptionsMap, mImageSpecList, mOrderQuantity );

      return ( null );
      }


    @Override
    protected void onPostExecute( Void result )
      {
      if ( mAddListener != null ) mAddListener.onItemAdded();
      }


    }

  }

