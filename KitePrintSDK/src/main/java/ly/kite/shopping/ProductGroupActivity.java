/*****************************************************
 *
 * ProductGroupActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.shopping;


///// Import(s) /////

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import ly.kite.R;
import ly.kite.print.Asset;
import ly.kite.KiteSDK;
import ly.kite.print.Product;
import ly.kite.print.ProductGroup;


///// Class Declaration /////

/*****************************************************
 *
 * This class displays the product group photos and allows
 * the user to drill down to products within that group.
 *
 *****************************************************/
public class ProductGroupActivity extends AGroupOrProductActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                      = "ProductGroupActivity";

  private static final String  INTENT_EXTRA_NAME_ASSET_LIST = KiteSDK.INTENT_PREFIX + ".AssetList";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<ProductGroup>  mProductGroupList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity with a
   * set of assets.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList )
    {
    Intent intent = new Intent( context, ProductGroupActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

    context.startActivity( intent );
    }



  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    getProducts();
    }


  ////////// ProductManager.SyncListener Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onGotProducts( ArrayList<ProductGroup> productGroupList, HashMap<String,Product> productTable )
    {
    onProductFetchFinished();

    mProductGroupList = productGroupList;

    // Display the product groups
    mGridAdaptor = new GroupOrProductAdaptor( this, productGroupList, mGridView );
    mGridView.setAdapter( mGridAdaptor );

    // Register for item selection
    mGridView.setOnItemClickListener( this );
    }


  ////////// AdapterView.OnItemClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is clicked.
   *
   *****************************************************/
  @Override
  public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    {
    // Get the product group. Remember to ignore any clicks on placeholder images.

    int adaptorIndex = mGridView.adaptorIndexFromPosition( position );

    if ( adaptorIndex >= mProductGroupList.size() ) return;

    ProductGroup clickedProductGroup = mProductGroupList.get( adaptorIndex );


    // Start the product activity, passing it the assets and the product group label (to
    // identify it).
    ProductActivity.start( this, mAssetArrayList, clickedProductGroup.getDisplayLabel() );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Starts a product sync.
   *
   *****************************************************/
  private class SyncProductsRunnable implements Runnable
    {
    @Override
    public void run()
      {
      getProducts();
      }
    }

  }

