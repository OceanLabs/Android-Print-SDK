/*****************************************************
 *
 * ProductActivity.java
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.print.Asset;
import ly.kite.KiteSDK;
import ly.kite.print.Product;
import ly.kite.print.ProductGroup;


///// Class Declaration /////

/*****************************************************
 *
 * This class displays the product photos and allows
 * the user to drill down into the products.
 *
 *****************************************************/
public class ProductActivity extends AGroupOrProductActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                               = "ProductActivity";

  public  static final String  INTENT_EXTRA_NAME_ASSET_LIST          = KiteSDK.INTENT_PREFIX + ".AssetList";
  public  static final String  INTENT_EXTRA_NAME_PRODUCT_GROUP_LABEL = KiteSDK.INTENT_PREFIX + ".ProductGroupLabel";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String              mProductGroupLabel;

  private ArrayList<Product>  mProductList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity with a
   * set of assets.
   *
   *****************************************************/
  static void start( Context context, ArrayList<Asset> assetArrayList, String productGroupLabel )
    {
    Intent intent = new Intent( context, ProductActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );
    intent.putExtra( ProductActivity.INTENT_EXTRA_NAME_PRODUCT_GROUP_LABEL, productGroupLabel );

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


    // Get the product group label

    if ( ( mProductGroupLabel = mIntent.getStringExtra( INTENT_EXTRA_NAME_PRODUCT_GROUP_LABEL ) ) == null )
      {
      Log.e( LOG_TAG, "No product group label found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_product_group_label,
              R.string.alert_dialog_message_no_product_group_label,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
        );

      finish();

      return;
      }


    setTitle( mProductGroupLabel );

    getProducts();

    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackProductSelectionScreenViewed();
      }
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


    // Try and find a product list

    mProductList = ProductGroup.findProductsByGroupLabel( productGroupList, mProductGroupLabel );

    if ( mProductList != null )
      {
      // Display the products
      mGridAdaptor = new GroupOrProductAdaptor( this, mProductList, mGridView );
      mGridView.setAdapter( mGridAdaptor );

      // Register for item selection
      mGridView.setOnItemClickListener( this );
      }
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
    // Get the product. Remember to ignore any clicks on placeholder images.

    int adaptorIndex = mGridView.adaptorIndexFromPosition( position );

    if ( adaptorIndex >= mProductList.size() ) return;

    Product clickedProduct = mProductList.get( adaptorIndex );


    // Launch the product overview activity
    ProductOverviewActivity.start( this, mAssetArrayList, clickedProduct );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

