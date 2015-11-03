/*****************************************************
 *
 * ProductSelectionActivity.java
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

package ly.kite.journey.selection;


///// Import(s) /////

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.CatalogueLoader;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.creation.ProductCreationActivity;
import ly.kite.journey.creation.reviewandedit.ReviewAndEditFragment;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.ProductGroup;
import ly.kite.widget.HeaderFooterGridView;


///// Class Declaration /////

/*****************************************************
 *
 * This activity coordinates the various fragments involved
 * in selecting a product. Once the product has been selected,
 * it hands over to the product creation activity, which
 * starts the appropriate fragments for the UI class / user
 * journey type.
 *
 *****************************************************/
public class ProductSelectionActivity extends AKiteActivity implements ICatalogueHolder,
                                                                       ICatalogueConsumer,
                                                                       ChooseProductGroupFragment.ICallback,
                                                                       ChooseProductFragment.ICallback,
                                                                       ProductOverviewFragment.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "ProductSelectionAct.";  // Can't be more than 23 characters ... who knew?!

  private static final String  INTENT_EXTRA_NAME_ASSET_LIST    = KiteSDK.INTENT_PREFIX + ".assetList";
  private static final String  INTENT_EXTRA_NAME_PRODUCT_IDS   = KiteSDK.INTENT_PREFIX + ".productIds";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;
  private String[]                      mProductIds;

  private ProgressBar                   mProgressSpinner;
  private ChooseProductGroupFragment    mProductGroupFragment;
  private ChooseProductFragment         mProductFragment;
  private ProductOverviewFragment       mProductOverviewFragment;
  private ReviewAndEditFragment         mReviewAndCropFragment;

  private CatalogueLoader               mCatalogueLoader;
  private Catalogue                     mCatalogue;
  private ICatalogueConsumer            mCatalogueConsumer;
  private boolean                       mAddFragmentOnCatalogue;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList, String... productIds )
    {
    Intent intent = new Intent( context, ProductSelectionActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

    if ( productIds != null && productIds.length > 0 )
      {
      intent.putExtra( INTENT_EXTRA_NAME_PRODUCT_IDS, productIds );
      }

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Converts an asset array list into an asset + quantity
   * array list, with the quantities set to 1.
   *
   *****************************************************/
  private static ArrayList<AssetsAndQuantity> assetsAndQuantityArrayListFrom( ArrayList<Asset> assetArrayList )
    {
    ArrayList<AssetsAndQuantity> assetsAndQuantityArrayList = new ArrayList<>( assetArrayList.size() );

    for ( Asset asset : assetArrayList )
      {
      assetsAndQuantityArrayList.add( new AssetsAndQuantity( asset ) );
      }

    return ( assetsAndQuantityArrayList );
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


    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
      {
      getWindow().setStatusBarColor( getResources().getColor( R.color.translucent_status_bar ) );
      }



    // Get the assets. Note that the asset list may be null, since some apps allow assets to be
    // chosen at a later stage, in which case we create an empty one here.

    ArrayList<Asset> assetArrayList = null;
    Intent           intent         = getIntent();

    if ( intent != null )
      {
      assetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST );
      mProductIds    = intent.getStringArrayExtra( INTENT_EXTRA_NAME_PRODUCT_IDS );
      }

    if ( assetArrayList == null ) assetArrayList = new ArrayList<Asset>();


    // We convert the asset list into an assets and quantity list (long before we get
    // to cropping and editing) because if the user comes out of product creation, and
    // goes back into another product - we want to remember quantities.

    mAssetsAndQuantityArrayList = assetsAndQuantityArrayListFrom( assetArrayList );


    // Set up the screen content

    setContentView( R.layout.screen_product_selection );

    mProgressSpinner = (ProgressBar)findViewById( R.id.progress_spinner );


    // We need to get a filtered product catalogue before we create any fragments,
    // since depending on how many products are returned - we can either start
    // the choose group, choose product, or product overview fragment.

    if ( savedInstanceState == null )
      {
      mAddFragmentOnCatalogue = true;
      }


    mCatalogueLoader = CatalogueLoader.getInstance( this );

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

    mCatalogueLoader.requestCatalogue( KiteSDK.MAX_ACCEPTED_PRODUCT_AGE_MILLIS, mProductIds, this );
    }


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );


    // The parent method will check for the checkout result.


    // See if we got an updated assets + quantity list

    if ( data != null )
      {
      ArrayList<AssetsAndQuantity> assetsAndQuantityArrayList = data.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY__LIST );

      if ( assetsAndQuantityArrayList != null ) mAssetsAndQuantityArrayList = assetsAndQuantityArrayList;
      }
    }


  /*****************************************************
   *
   * Called some time after the activity is no longer visible.
   *
   *****************************************************/
  @Override
  protected void onStop()
    {
    super.onStop();

    if ( mCatalogueLoader != null ) mCatalogueLoader.cancelRequests();
    }


  ////////// ICatalogueHolder Method(s) //////////

  /*****************************************************
   *
   * Returns a catalogue.
   *
   *****************************************************/
  @Override
  public void getCatalogue( ICatalogueConsumer consumer )
    {
    if ( mCatalogue != null )
      {
      consumer.onCatalogueSuccess( mCatalogue );
      }
    else
      {
      mCatalogueConsumer = consumer;
      }
    }


  ////////// CatalogueLoader.ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the catalogue is loaded successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    // Some apps may wish to amend the catalogue
    mCatalogue = getAdjustedCatalogue( catalogue );

    // Hide the progress spinner
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );


    // If this is the first time we have been created - start the first fragment
    if ( mAddFragmentOnCatalogue )
      {
      // Determine which fragment we need to start with

      ArrayList<ProductGroup> productGroupList = mCatalogue.getProductGroupList();

      if ( productGroupList != null && productGroupList.size() > 0 )
        {
        // If there is more than one group - start the choose group fragment
        if ( productGroupList.size() > 1 )
          {
          addFragment( mProductGroupFragment = ChooseProductGroupFragment.newInstance(), ChooseProductGroupFragment.TAG );
          }
        else
          {
          ProductGroup       productGroup = productGroupList.get( 0 );
          ArrayList<Product> productList  = productGroup.getProductList();

          if ( productList != null && productList.size() > 0 )
            {
            // If there is more than one product - start the choose product fragment
            if ( productList.size() > 1 )
              {
              addFragment( mProductFragment = ChooseProductFragment.newInstance( productGroup ), ChooseProductFragment.TAG );
              }
            else
              {
              // There is just one product - go straight to the product overview screen
              onDisplayProductOverview( productList.get( 0 ) );
              }
            }
          }
        }

      }


    // Pass the result on to any consumer
    if ( mCatalogueConsumer != null )
      {
      mCatalogueConsumer.onCatalogueSuccess( mCatalogue );

      mCatalogueConsumer = null;
      }
    }


  /*****************************************************
   *
   * Called when the catalogue load fails.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( Exception exception )
    {
    mCatalogue = null;

    if ( isVisible() )
      {
      // Hide the progress spinner
      if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );

      // Display an error dialog
      displayModalDialog
              (
                      R.string.alert_dialog_title_error_retrieving_products,
                      R.string.alert_dialog_message_error_retrieving_products,
                      R.string.Retry,
                      new RequestCatalogueRunnable(),
                      R.string.Cancel,
                      new FinishRunnable()
              );
      }

    }


  ////////// ChooseProductGroupFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called before the product group grid view is populated.
   *
   *****************************************************/
  @Override
  public void pgOnPrePopulateProductGroupGrid( Catalogue catalogue, HeaderFooterGridView headerFooterGridView )
    {
    // Do nothing
    }


  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pgOnProductGroupChosen( ProductGroup productGroup )
    {
    // If the product group contains more than one product - display
    // the choose product screen. Otherwise go straight to the product
    // overview.

    List<Product> productList = productGroup.getProductList();

    if ( productList == null || productList.size() > 1 )
      {
      mProductFragment = ChooseProductFragment.newInstance( productGroup );

      addFragment( mProductFragment, ChooseProductFragment.TAG );

      return;
      }


    onDisplayProductOverview( productList.get( 0 ) );
    }


  ////////// ChooseProductFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pOnProductChosen( Product product )
    {
    onDisplayProductOverview( product );
    }


  ////////// ProductOverviewFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the user wishes to create a product.
   *
   *****************************************************/
  @Override
  public void poOnCreateProduct( Product product, HashMap<String,String> optionMap )
    {
    // Once the product has been chosen and the user clicks "Start Creating",
    // we then hand over to the product creation activity to choose the journey
    // depending on the product.

    ProductCreationActivity.startForResult( this, mAssetsAndQuantityArrayList, product, optionMap, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Should be overridden if an app wants to adjust the
   * catalogue in some way, such as to remove shipping
   * to certain locations.
   *
   *****************************************************/
  protected Catalogue getAdjustedCatalogue( Catalogue catalogue )
    {
    // The default implementation is to do nothing
    return ( catalogue );
    }


  /*****************************************************
   *
   * Displays the product overview fragment for the supplied
   * product.
   *
   *****************************************************/
  private void onDisplayProductOverview( Product product )
    {
    mProductOverviewFragment = ProductOverviewFragment.newInstance( product );

    addFragment( mProductOverviewFragment, ProductOverviewFragment.TAG );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Requests the catalogue.
   *
   *****************************************************/
  private class RequestCatalogueRunnable implements Runnable
    {
    @Override
    public void run()
      {
      if ( mCatalogueLoader != null )
        {
        if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );

        mCatalogueLoader.requestCatalogue( KiteSDK.MAX_ACCEPTED_PRODUCT_AGE_MILLIS, mProductIds, ProductSelectionActivity.this );
        }
      }
    }



  }

