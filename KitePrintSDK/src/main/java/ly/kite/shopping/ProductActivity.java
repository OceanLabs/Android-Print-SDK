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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import ly.kite.R;
import ly.kite.print.Asset;
import ly.kite.print.KitePrintSDK;
import ly.kite.print.Product;
import ly.kite.print.ProductGroup;
import ly.kite.print.ProductSyncer;


///// Class Declaration /////

/*****************************************************
 *
 * This class displays the product photos and allows
 * the user to drill down into the products.
 *
 *****************************************************/
public class ProductActivity extends Activity implements ProductSyncer.SyncListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                               = "ProductActivity";

  public  static final String  INTENT_EXTRA_NAME_ASSET_LIST          = KitePrintSDK.INTENT_PREFIX + ".AssetList";
  public  static final String  INTENT_EXTRA_NAME_PRODUCT_GROUP_LABEL = KitePrintSDK.INTENT_PREFIX + ".ProductGroupLabel";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>  mAssetArrayList;
  private String            mProductGroupLabel;

  private GridView          mGridView;
  private ProgressBar       mProgressBar;

  private ProductSyncer     mProductSyncer;
  private BaseAdapter       mGridAdaptor;


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


    // Get the assets and the product group label

    Intent intent = getIntent();

    if ( intent == null )
      {
      Log.e( LOG_TAG, "No intent found" );

      // TODO: Display error dialog

      finish();

      return;
      }

    if ( ( mAssetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      // TODO: Display error dialog

      finish();

      return;
      }

    if ( ( mProductGroupLabel = intent.getStringExtra( INTENT_EXTRA_NAME_PRODUCT_GROUP_LABEL ) ) == null )
      {
      Log.e( LOG_TAG, "No product group label found" );

      // TODO: Display error dialog

      finish();

      return;
      }

    // Set up the screen content

    setContentView( R.layout.activity_product_groups );

    setTitle( R.string.title_product_groups_activity );

    mGridView    = (GridView)findViewById( R.id.grid_view );
    mProgressBar = (ProgressBar)findViewById( R.id.progress_bar );


    // Get the last retrieved product group list

    mProductSyncer = ProductSyncer.getInstance();

    mProductSyncer.getLastRetrievedProductGroupList( this );
    }


  /*****************************************************
   *
   * Called when the home action is clicked.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    switch ( item.getItemId() )
      {
      case android.R.id.home:
        finish();
        return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  ////////// ProductSyncer.SyncListener Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onSyncComplete( ArrayList<ProductGroup> productGroupList )
    {
    mProgressBar.setVisibility( View.GONE );

    // Try and find a product list

    ArrayList<Product> productList = ProductGroup.findProductsByGroupLabel( productGroupList, mProductGroupLabel );

    if ( productList != null )
      {
      // Display the products
      mGridAdaptor = new DisplayItemAdaptor( this, productList );
      mGridView.setAdapter( mGridAdaptor );
      }
    }


  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onError( Exception error )
    {
    mProgressBar.setVisibility( View.GONE );

    // TODO: Display an error
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

