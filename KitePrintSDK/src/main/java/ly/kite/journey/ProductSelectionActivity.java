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

package ly.kite.journey;


///// Import(s) /////


///// Class Declaration /////

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.journey.reviewandedit.ReviewAndEditFragment;
import ly.kite.product.Asset;
import ly.kite.product.Product;
import ly.kite.product.ProductGroup;

/*****************************************************
 *
 * This activity coordinates the various fragments involved
 * in selecting a product. Once the product has been selected,
 * it hands over to the product creation activity, which
 * starts the appropriate fragments for the UI class / user
 * journey type.
 *
 *****************************************************/
public class ProductSelectionActivity extends AKiteActivity implements ChooseProductGroupFragment.ICallback,
                                                                       ChooseProductFragment.ICallback,
                                                                       ProductOverviewFragment.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "ProductSelectionAct.";  // Can't be more than 23 characters ... who knew?!

  private static final String  INTENT_EXTRA_NAME_ASSET_LIST    = KiteSDK.INTENT_PREFIX + ".assetList";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;

  private ChooseProductGroupFragment    mProductGroupFragment;
  private ChooseProductFragment         mProductFragment;
  private ProductOverviewFragment       mProductOverviewFragment;
  private ReviewAndEditFragment mReviewAndCropFragment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList )
    {
    Intent intent = new Intent( context, ProductSelectionActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

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
      assetsAndQuantityArrayList.add( new AssetsAndQuantity( asset, 1 ) );
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



    // Get the assets

    Intent intent = getIntent();

    if ( intent == null )
      {
      Log.e( LOG_TAG, "No intent found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_intent,
              R.string.alert_dialog_message_no_intent,
              NO_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }

    ArrayList<Asset> assetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST );

    if ( assetArrayList == null || assetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              NO_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }


    // We convert the asset list into an assets and quantity list (long before we get
    // to cropping and editing) because if the user comes out of product creation, and
    // goes back into another product - we want to remember quantities.

    mAssetsAndQuantityArrayList = assetsAndQuantityArrayListFrom( assetArrayList );


    // Set up the screen content
    setContentView( R.layout.screen_generic_fragment_container );


    // Start the first fragment
    if ( savedInstanceState == null )
      {
      addFragment( mProductGroupFragment = ChooseProductGroupFragment.newInstance(), ChooseProductGroupFragment.TAG );
      }
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


  ////////// ChooseProductGroupFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pgOnProductGroupChosen( ProductGroup productGroup )
    {
    // Display the product selection fragment

    mProductFragment = ChooseProductFragment.newInstance( productGroup );

    addFragment( mProductFragment, ChooseProductFragment.TAG );
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
    // Display the product overview fragment

    mProductOverviewFragment = ProductOverviewFragment.newInstance( product );

    addFragment( mProductOverviewFragment, ProductOverviewFragment.TAG );
    }


  ////////// ProductOverviewFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the user wishes to create a product.
   *
   *****************************************************/
  @Override
  public void poOnCreateProduct( Product product )
    {
    // Once the product has been chosen and the user clicks "Start Creating",
    // we then hand over to the product creation activity to choose the journey
    // depending on the product.

    ProductCreationActivity.startForResult( this, mAssetsAndQuantityArrayList, product, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

