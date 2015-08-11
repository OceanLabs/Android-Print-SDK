/*****************************************************
 *
 * ProductCreationActivity.java
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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.journey.imageselection.ImageSelectionFragment;
import ly.kite.journey.phonecase.PhoneCaseFragment;
import ly.kite.journey.reviewandedit.EditImageFragment;
import ly.kite.journey.reviewandedit.ReviewAndEditFragment;
import ly.kite.product.Asset;
import ly.kite.product.PrintJob;
import ly.kite.product.PrintOrder;
import ly.kite.product.Product;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is responsible for coordinating the user
 * journey fragments specific to the UI class.
 *
 *****************************************************/
public class ProductCreationActivity extends AKiteActivity implements PhoneCaseFragment.ICallback,
                                                                      ImageSelectionFragment.ICallback,
                                                                      ReviewAndEditFragment.ICallback,
                                                                      EditImageFragment.ICallback

  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                                    = "ProductCreationActivity";

  private static final String  INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY_LIST = KiteSDK.INTENT_PREFIX + ".assetsAndQuantityList";
  private static final String  INTENT_EXTRA_NAME_PRODUCT                  = KiteSDK.INTENT_PREFIX + ".product";


  ////////// Static Variable(s) //////////

  static private ProductCreationActivity sUserJourneyCoordinator;


  ////////// Member Variable(s) //////////

  private ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;
  private Product                       mProduct;


//  private PhoneCaseFragment             mPhoneCaseFragment;
//
//  private ImageSelectionFragment        mImageSelectionFragment;
//  private ReviewAndEditFragment         mReviewAndEditFragment;
//  private EditImageFragment             mEditImageFragment;

  private int                           mLastEditedAssetIndex;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the user journey type is supported.
   *
   *****************************************************/
  static public boolean isSupported( UserJourneyType type )
    {
    switch ( type )
      {
      case CIRCLE:        return ( true );
      case FRAME:         break;
      case GREETING_CARD: break;
      case PHONE_CASE:    return ( true );
      case PHOTOBOOK:     break;
      case POSTCARD:      break;
      case POSTER:        break;
      case RECTANGLE:     return ( true );
      }

    return ( false );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult( Activity                      activity,
                                     ArrayList<AssetsAndQuantity>  assetsAndQuantityArrayList,
                                     Product                       product,
                                     int                           requestCode )
    {
    Intent intent = new Intent( activity, ProductCreationActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY_LIST, assetsAndQuantityArrayList );
    intent.putExtra( INTENT_EXTRA_NAME_PRODUCT, product );

    activity.startActivityForResult( intent, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// AKiteActivity Method(s) //////////

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



    // Get the intent extras

    Intent intent = getIntent();

    if ( intent == null )
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

    mAssetsAndQuantityArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY_LIST );

    if ( mAssetsAndQuantityArrayList == null || mAssetsAndQuantityArrayList.size() < 1 )
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


    mProduct = intent.getParcelableExtra( INTENT_EXTRA_NAME_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_product,
              R.string.alert_dialog_message_no_product,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }


    // Set up the screen content
    setContentView( R.layout.screen_generic_fragment_container );


    // Start the first fragment

    if ( savedInstanceState == null )
      {
      addFirstFragment();

      Analytics.getInstance( this ).trackCreateProductScreenViewed( mProduct );
      }
    }


  // TODO: We need to pass an updated assets + quantity list back to the
  // TODO: calling activity.


  ////////// PhoneCaseFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a phone case has been created.
   *
   *****************************************************/
  @Override
  public void pcOnCreated( Asset imageAsset )
    {
    // Create the print order

    PrintOrder printOrder = new PrintOrder();

    printOrder.addPrintJob( PrintJob.createPrintJob( mProduct, imageAsset ) );


    // Start the check-out activity
    CheckoutActivity.start( this, printOrder, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// ImageSelectionFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the images have been selected, and the user
   * has pressed the review and crop button.
   *
   *****************************************************/
  @Override
  public void isOnNext( ArrayList<AssetsAndQuantity> assetsAndQuantityList )
    {
    // Update the assets and quantity list. If this is the first time - it will
    // contain cropped assets that we need.
    mAssetsAndQuantityArrayList = assetsAndQuantityList;

    ReviewAndEditFragment reviewAndEditFragment = ReviewAndEditFragment.newInstance( mAssetsAndQuantityArrayList, mProduct );

    addFragment( reviewAndEditFragment, ReviewAndEditFragment.TAG );
    }


  ////////// ReviewAndEditFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when an asset image is to be edited.
   *
   *****************************************************/
  @Override
  public void reOnEdit( int assetIndex )
    {
    // Start the edit fragment

    EditImageFragment editImageFragment = EditImageFragment.newInstance( mAssetsAndQuantityArrayList.get( assetIndex ).getUneditedAsset(), mProduct );

    addFragment( editImageFragment, EditImageFragment.TAG );

    mLastEditedAssetIndex = assetIndex;
    }


  /*****************************************************
   *
   * Called when the confirm button is clicked.
   *
   *****************************************************/
  public void reOnConfirm()
    {
    // In order to create a print job, we need to create a list of assets. Assets are listed
    // as many times as their quantity - so if an asset has a quantity of 3, we include it 3
    // times.

    ArrayList<Asset> assetArrayList = new ArrayList<>();

    for ( AssetsAndQuantity assetsAndQuantity : mAssetsAndQuantityArrayList )
      {
      for ( int index = 0; index < assetsAndQuantity.getQuantity(); index ++ )
        {
        assetArrayList.add( assetsAndQuantity.getEditedAsset() );
        }
      }


    // Create the print order

    PrintOrder printOrder = new PrintOrder();

    printOrder.addPrintJob( PrintJob.createPrintJob( mProduct, assetArrayList ) );


    // Start the check-out activity
    CheckoutActivity.start( this, printOrder, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// EditImageFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the cancel button is clicked.
   *
   *****************************************************/
  @Override
  public void eiOnCancel()
    {
    // Remove the top (edit image) fragment
    popFragment();
    }


  /*****************************************************
   *
   * Called when the OK button is clicked.
   *
   *****************************************************/
  @Override
  public void eiOnConfirm( Asset editedAsset )
    {
    // Replace the edited asset with the new one
    
    AssetsAndQuantity assetsAndQuantity = mAssetsAndQuantityArrayList.get( mLastEditedAssetIndex );

    assetsAndQuantity.setEditedAsset( editedAsset, mProduct.getUserJourneyType() );


    // Remove the edit image fragment from the back stack
    popFragment();


    // Notify the review and edit fragment

    ReviewAndEditFragment reviewAndEditFragment = (ReviewAndEditFragment)mFragmentManager.findFragmentByTag( ReviewAndEditFragment.TAG );

    if ( reviewAndEditFragment != null ) reviewAndEditFragment.onAssetUpdated( mLastEditedAssetIndex, assetsAndQuantity );


    // Notify the image selection fragment

    ImageSelectionFragment imageSelectionFragment = (ImageSelectionFragment)mFragmentManager.findFragmentByTag( ImageSelectionFragment.TAG );

    imageSelectionFragment.onAssetUpdated( mLastEditedAssetIndex, assetsAndQuantity );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Starts the next stage in the appropriate user journey
   * for the supplied product.
   *
   *****************************************************/
  private void addFirstFragment()
    {
    switch ( mProduct.getUserJourneyType() )
      {
      case CIRCLE:
        addFragment( ImageSelectionFragment.newInstance( mAssetsAndQuantityArrayList, mProduct ), ImageSelectionFragment.TAG );
        break;

      case PHONE_CASE:
        addFragment( PhoneCaseFragment.newInstance( mAssetsAndQuantityArrayList, mProduct ), PhoneCaseFragment.TAG );
        break;

      case RECTANGLE:
        addFragment( ImageSelectionFragment.newInstance( mAssetsAndQuantityArrayList, mProduct ), ImageSelectionFragment.TAG );
        break;
      }

    }


  ////////// Inner Class(es) //////////

  }

