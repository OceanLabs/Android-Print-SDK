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

package ly.kite.journey.creation;


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
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.IAssetsAndQuantityHolder;
import ly.kite.journey.ImageSourceFragment;
import ly.kite.journey.UserJourneyType;
import ly.kite.journey.creation.imageselection.ImageSelectionFragment;
import ly.kite.journey.creation.phonecase.PhoneCaseFragment;
import ly.kite.journey.creation.reviewandedit.EditImageFragment;
import ly.kite.journey.creation.reviewandedit.ReviewAndEditFragment;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.PrintJob;
import ly.kite.catalogue.PrintOrder;
import ly.kite.catalogue.Product;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is responsible for coordinating the user
 * journey fragments specific to the UI class.
 *
 *****************************************************/
public class ProductCreationActivity extends AKiteActivity implements IAssetsAndQuantityHolder,
                                                                      ImageSourceFragment.ICallback,
                                                                      PhoneCaseFragment.ICallback,
                                                                      ImageSelectionFragment.ICallback,
                                                                      ReviewAndEditFragment.ICallback,
                                                                      EditImageFragment.ICallback

  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                                    = "ProductCreationActivity";

  public  static final String  INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY_LIST = KiteSDK.INTENT_PREFIX + ".assetsAndQuantityList";
  public  static final String  INTENT_EXTRA_NAME_PRODUCT                  = KiteSDK.INTENT_PREFIX + ".product";

  private static final String  BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST        = "assetsAndQuantityList";
  private static final String  BUNDLE_KEY_LAST_EDITED_ASSET_INDEX         = "lastEditedAssetIndex";


  ////////// Static Variable(s) //////////

  static private ProductCreationActivity sUserJourneyCoordinator;


  ////////// Member Variable(s) //////////

  private ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;
  private Product                       mProduct;

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


    // If we have a saved instance state - try to get the assets and quantity list from it, in preference
    // to the intent. We will probably have added to it since. We need to do this before calling

    if ( savedInstanceState != null )
      {
      mAssetsAndQuantityArrayList = savedInstanceState.getParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST );
      mLastEditedAssetIndex       = savedInstanceState.getInt( BUNDLE_KEY_LAST_EDITED_ASSET_INDEX );
      }


    // Get the intent extras

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


    // If we didn't get an assets and quantity list from a saved state - get the original from the intent. If
    // all else fails - create a new empty one.

    if ( mAssetsAndQuantityArrayList == null )
      {
      mAssetsAndQuantityArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY_LIST );
      }

    if ( mAssetsAndQuantityArrayList == null ) mAssetsAndQuantityArrayList = new ArrayList<>();


    mProduct = intent.getParcelableExtra( INTENT_EXTRA_NAME_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_product,
              R.string.alert_dialog_message_no_product,
              NO_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      return;
      }


    // Set up the screen content
    setContentView( R.layout.screen_product_selection );


    // Start the first fragment

    if ( savedInstanceState == null )
      {
      addFirstFragment();

      Analytics.getInstance( this ).trackCreateProductScreenViewed( mProduct );
      }
    }


  /*****************************************************
   *
   * Called to save the state.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );


    // Save the assets and quantity list
    if ( mAssetsAndQuantityArrayList != null )
      {
      outState.putParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST, mAssetsAndQuantityArrayList );
      }

    // Save the last edited index. Otherwise if we change the orientation when editing an image, the
    // updated image gets associated with the wrong asset when coming back.
    outState.putInt( BUNDLE_KEY_LAST_EDITED_ASSET_INDEX, mLastEditedAssetIndex );
    }



  // TODO: We need to pass an updated assets + quantity list back to the
  // TODO: calling activity.


  ////////// IAssetsAndQuantityHolder Method(s) //////////

  /*****************************************************
   *
   * Returns the assets and quantity list.
   *
   *****************************************************/
  public ArrayList<AssetsAndQuantity> getAssetsAndQuantityArrayList()
    {
    return ( mAssetsAndQuantityArrayList );
    }


  ////////// ImageSourceFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a phone case has been created.
   *
   *****************************************************/
  @Override
  public void isOnAssetsAdded()
    {
    // We want to remove the image source fragment without triggering the back stack listener - otherwise
    // it will detect that there are no fragments and exit.

    popFragmentSecretly();;


    addFirstFragment();
    }


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
  public void isOnNext()
    {
    // Move forward to the review and edit screen

    ReviewAndEditFragment reviewAndEditFragment = ReviewAndEditFragment.newInstance( mProduct );

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

    assetsAndQuantity.setEditedAsset( editedAsset, mProduct.getId() );


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
    // For all user journeys, if there are no assets - we first display the image
    // source fragment.

    if ( mAssetsAndQuantityArrayList.size() < 1 )
      {
      addFragment( ImageSourceFragment.newInstance( mProduct ), ImageSourceFragment.TAG );

      return;
      }


    switch ( mProduct.getUserJourneyType() )
      {
      case CIRCLE:
        addFragment( ImageSelectionFragment.newInstance( mProduct ), ImageSelectionFragment.TAG );
        break;

      case PHONE_CASE:
        addFragment( PhoneCaseFragment.newInstance( mProduct ), PhoneCaseFragment.TAG );
        break;

      case RECTANGLE:
        addFragment( ImageSelectionFragment.newInstance( mProduct ), ImageSelectionFragment.TAG );
        break;
      }

    }


  ////////// Inner Class(es) //////////

  }

