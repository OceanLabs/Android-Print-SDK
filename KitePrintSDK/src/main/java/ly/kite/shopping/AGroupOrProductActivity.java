/*****************************************************
 *
 * AProductOrGroupActivity.java
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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import ly.kite.R;
import ly.kite.print.Asset;
import ly.kite.KiteSDK;
import ly.kite.print.ProductManager;
import ly.kite.widget.HeaderFooterGridView;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the parent of both the product
 * group and product activities, and provides some common
 * methods.
 *
 *****************************************************/
public abstract class AGroupOrProductActivity extends AKiteActivity implements ProductManager.ProductConsumer, AdapterView.OnItemClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private   static final String  LOG_TAG                         = "AProductOrGroupActivity";

  private   static final String  INTENT_EXTRA_NAME_ASSET_LIST    = KiteSDK.INTENT_PREFIX + ".AssetList";
  public    static final long    MAX_ACCEPTED_PRODUCT_AGE_MILLIS = 1000 * 60 * 60;  // 1 hour


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Intent                   mIntent;
  protected ArrayList<Asset>         mAssetArrayList;

  protected HeaderFooterGridView     mGridView;
  protected ProgressBar              mProgressBar;

  protected ProductManager           mProductManager;
  protected BaseAdapter              mGridAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity with a
   * set of assets.
   *
   *****************************************************/
  static void start( Context context, ArrayList<Asset> assetArrayList )
    {
    Intent intent = new Intent( context, AGroupOrProductActivity.class );

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


    // Get the assets

    mIntent = getIntent();

    if ( mIntent == null )
      {
      Log.e( LOG_TAG, "No intent found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_intent,
              R.string.alert_dialog_message_no_intent,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
        );

      return;
      }

    if ( ( mAssetArrayList = mIntent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
        );

      return;
      }


    // Set up the screen content

    setContentView( R.layout.screen_group_or_product );

    mGridView    = (HeaderFooterGridView)findViewById( R.id.grid_view );
    mProgressBar = (ProgressBar)findViewById( R.id.progress_bar );


    // Add a header view to the grid, that matches the height of the status bar + action bar. When
    // the content is at the top, it will align with the action bar.
    addHeaderFooterSpacers();
    }


  /*****************************************************
   *
   * Called when the activity becomes visible.
   *
   *****************************************************/
  @Override
  public void onStart()
    {
    super.onStart();
    }


  ////////// ProductSyncer.SyncListener Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onProductRetrievalError( Exception exception )
    {
    onProductFetchFinished();

    displayModalDialog(
            R.string.alert_dialog_title_error_retrieving_products,
            R.string.alert_dialog_message_error_retrieving_products,
            R.string.Retry,
            new SyncProductsRunnable(),
            R.string.Cancel,
            new FinishRunnable()
      );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Gets the products.
   *
   *****************************************************/
  protected void getProducts()
    {
    mProgressBar.setVisibility( View.VISIBLE );


    // Request a set of products. We supply a handler because we don't want to be called
    // back immediately - often the GridView won't have been configured correctly yet (because
    // when we specify the number of columns it doesn't take effect immediately).

    mProductManager = ProductManager.getInstance();

    mProductManager.getAllProducts( MAX_ACCEPTED_PRODUCT_AGE_MILLIS, this );
    }


  /*****************************************************
   *
   * Updates the UI (i.e. removes the progress spinner)
   * when syncing has finished, regardless of whether there
   * was an error or not.
   *
   *****************************************************/
  public void onProductFetchFinished()
    {
    mProgressBar.setVisibility( View.GONE );
    }


  /*****************************************************
   *
   * Adds a header to the supplied grid view.
   *
   *****************************************************/
  private void addHeaderFooterSpacers()
    {
    // Calculate the height. Start with the action bar size, and remember to deduct the vertical spacing
    // height, otherwise the first spacer appears below the action bar.

    Resources resources = getResources();

    // Start by deducting the grid's vertical spacing
    int headerSpacerHeight = - (int)getResources().getDimension( R.dimen.group_or_product_grid_vertical_spacing );
    int footerSpacerHeight = - (int)getResources().getDimension( R.dimen.group_or_product_grid_vertical_spacing );


    // Add the action bar height

    TypedValue typedValue = new TypedValue();

    if ( getTheme().resolveAttribute( android.R.attr.actionBarSize, typedValue, true ) )
      {
      headerSpacerHeight += TypedValue.complexToDimensionPixelSize( typedValue.data, resources.getDisplayMetrics() );
      }


    // If we are running on Lollipop onwards, the status and navigation bars are also transparent, so getCost their heights
    // and adjust the spacers' heights accordingly. Note that the navigation bar is only counted if the device is in
    // portrait orientation, since the navigation bar gets moved over to the side in landscape orientation.

    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
      {
      // Status bar

      int statusBarHeightResourceId = getResources().getIdentifier( "status_bar_height", "dimen", "android" );

      if ( statusBarHeightResourceId > 0 )
        {
        headerSpacerHeight += getResources().getDimensionPixelSize( statusBarHeightResourceId );
        }


      // Navigation bar

//      Rect visibleFrame = new Rect();
//      getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
//      DisplayMetrics dm = getResources().getDisplayMetrics();
//
//      View rootView = getWindow().getDecorView().getRootView();
//
//      Point point = new Point();
//      Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
//      defaultDisplay.getSize( point );


      int orientation = getResources().getConfiguration().orientation;

      int navigationBarHeightResourceId = getResources().getIdentifier(
              ( orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape" ), "dimen", "android" );

      if ( navigationBarHeightResourceId > 0 )
        {
        footerSpacerHeight += getResources().getDimensionPixelSize( navigationBarHeightResourceId );
        }
      }


    // Create the spacers and add it to the top of the grid

    mGridView.addHeaderView( newSpacerView( headerSpacerHeight ) );

    if ( footerSpacerHeight > 0 )
      {
      mGridView.addFooterView( newSpacerView( footerSpacerHeight ) );
      }
    }


  /*****************************************************
   *
   * Creates a view and sets its height.
   *
   *****************************************************/
  private View newSpacerView( int height )
    {
    View view = new View( this );

    GridView.LayoutParams layoutParams = new GridView.LayoutParams( GridView.LayoutParams.MATCH_PARENT, height );

    view.setLayoutParams( layoutParams );

    return ( view );
    }


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

