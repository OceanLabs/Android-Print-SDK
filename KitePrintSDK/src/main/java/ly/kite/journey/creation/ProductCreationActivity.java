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
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.journey.creation.calendar.CalendarFragment;
import ly.kite.journey.creation.poster.PosterFragment;
import ly.kite.journey.creation.reviewandedit.EditBorderTextImageFragment;
import ly.kite.ordering.OrderingDataAgent;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.IImageSpecStore;
import ly.kite.ordering.ImageSpec;
import ly.kite.journey.basket.BasketActivity;
import ly.kite.journey.creation.imagesource.ImageSourceFragment;
import ly.kite.journey.UserJourneyType;
import ly.kite.journey.creation.imageselection.ImageSelectionFragment;
import ly.kite.journey.creation.phonecase.PhoneCaseFragment;
import ly.kite.journey.creation.photobook.PhotobookFragment;
import ly.kite.journey.creation.reviewandedit.EditImageFragment;
import ly.kite.journey.creation.reviewandedit.ReviewAndEditFragment;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is responsible for coordinating the user
 * journey fragments specific to the UI class.
 *
 *****************************************************/
public class ProductCreationActivity extends AKiteActivity implements IImageSpecStore,
                                                                      ImageSourceFragment.ICallback,
                                                                      PhoneCaseFragment.ICallback,
                                                                      ImageSelectionFragment.ICallback,
                                                                      PhotobookFragment.ICallback,
                                                                      CalendarFragment.ICallback,
                                                                      PosterFragment.ICallback,
                                                                      ReviewAndEditFragment.ICallback,
                                                                      EditImageFragment.ICallback,
                                                                      EditBorderTextImageFragment.ICallback,
                                                                      OrderingDataAgent.IAddListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                                    = "ProductCreationActivity";

  static public  final String  INTENT_EXTRA_NAME_BASKET_ITEM_ID           = KiteSDK.INTENT_PREFIX + ".basketItemId";
  static public  final String  INTENT_EXTRA_NAME_ORDER_QUANTITY           = KiteSDK.INTENT_PREFIX + ".orderQuantity";
  static public  final String  INTENT_EXTRA_NAME_PRODUCT                  = KiteSDK.INTENT_PREFIX + ".product";
  static public  final String  INTENT_EXTRA_NAME_OPTIONS_MAP              = KiteSDK.INTENT_PREFIX + ".optionsMap";

  static private final String  BUNDLE_KEY_IMAGE_SPEC_LIST                 = "imageSpecList";
  static private final String  BUNDLE_KEY_LAST_EDITED_IMAGE_INDEX         = "lastEditedImageIndex";


  ////////// Static Variable(s) //////////

  //static private ProductCreationActivity sUserJourneyCoordinator;


  ////////// Member Variable(s) //////////

  private boolean                       mInEditMode;

  private long                          mBasketItemId;
  private int                           mOrderQuantity;

  private Product                       mProduct;
  private HashMap<String,String>        mOptionMap;
  private ArrayList<ImageSpec>          mImageSpecArrayList;

  private ICustomImageEditorAgent       mCustomImageEditorAgent;
  private int                           mLastEditedImageIndex;

  private ProgressDialog                mProgressDialog;


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
      case CALENDAR:
      case CIRCLE:
      case GREETINGCARD:
      case PHONE_CASE:
      case PHOTOBOOK:
      case POSTER:
      case RECTANGLE:
        return ( true );

      case FRAME:
      case POSTCARD:
      }

    return ( false );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult( Activity                      activity,
                                     long                          basketItemId,
                                     Product                       product,
                                     HashMap<String,String>        optionsMap,
                                     ArrayList<ImageSpec>          imageSpecArrayList,
                                     int                           orderQuantity,
                                     int                           requestCode )
    {
    Intent intent = new Intent( activity, ProductCreationActivity.class );

    intent.putExtra( INTENT_EXTRA_NAME_BASKET_ITEM_ID, basketItemId );
    intent.putExtra( INTENT_EXTRA_NAME_PRODUCT, product );
    intent.putExtra( INTENT_EXTRA_NAME_OPTIONS_MAP, optionsMap );
    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_IMAGE_SPEC_LIST, imageSpecArrayList );
    intent.putExtra( INTENT_EXTRA_NAME_ORDER_QUANTITY, orderQuantity );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult( Activity                      activity,
                                     Product                       product,
                                     HashMap<String,String>        optionsMap,
                                     ArrayList<ImageSpec>          imageSpecArrayList,
                                     int                           requestCode )
    {
    startForResult( activity, -1L, product, optionsMap, imageSpecArrayList, 1, requestCode );
    }


  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult( Activity                      activity,
                                     Product                       product,
                                     ArrayList<ImageSpec>          imageSpecArrayList,
                                     int                           requestCode )
    {
    startForResult( activity, product, new HashMap<String, String>( 0 ), imageSpecArrayList, requestCode );
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
      mImageSpecArrayList   = savedInstanceState.getParcelableArrayList( BUNDLE_KEY_IMAGE_SPEC_LIST );
      mLastEditedImageIndex = savedInstanceState.getInt( BUNDLE_KEY_LAST_EDITED_IMAGE_INDEX );
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


    // If we didn't get an image spec list from a saved state - get the original from the intent. If
    // all else fails - create a new empty one.

    if ( mImageSpecArrayList == null )
      {
      mImageSpecArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_IMAGE_SPEC_LIST );
      }

    if ( mImageSpecArrayList == null ) mImageSpecArrayList = new ArrayList<>();


    // See if we got a basket item id, and are thus editing a basket item

    mBasketItemId = intent.getLongExtra( INTENT_EXTRA_NAME_BASKET_ITEM_ID, -1L );

    if ( mBasketItemId >= 0 ) mInEditMode = true;


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


    mOptionMap = (HashMap<String,String>)intent.getSerializableExtra( INTENT_EXTRA_NAME_OPTIONS_MAP );

    if ( mOptionMap == null ) mOptionMap = new HashMap<>( 0 );


    mOrderQuantity = intent.getIntExtra( INTENT_EXTRA_NAME_ORDER_QUANTITY, 1 );


    // Set up the screen content
    setContentView( R.layout.screen_product_creation );


    // Start the first fragment

    if ( savedInstanceState == null )
      {
      addNextFragment();

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
    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( LOG_TAG, "--> onSaveInstanceState( outState = " + outState + " )" );

    super.onSaveInstanceState( outState );


    // Save the assets and quantity list
    if ( mImageSpecArrayList != null )
      {
      outState.putParcelableArrayList( BUNDLE_KEY_IMAGE_SPEC_LIST, mImageSpecArrayList );
      }

    // Save the last edited index. Otherwise if we change the orientation when editing an image, the
    // updated image gets associated with the wrong asset when coming back.
    outState.putInt( BUNDLE_KEY_LAST_EDITED_IMAGE_INDEX, mLastEditedImageIndex );

    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( LOG_TAG, "<-- onSaveInstanceState( outState = " + outState + " )" );
    }



  // TODO: We need to pass an updated image spec list back to the
  // TODO: calling activity.


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent resultIntent )
    {
    super.onActivityResult( requestCode, resultCode, resultIntent );


    // Check for a custom image editor result

    if ( requestCode == ACTIVITY_REQUEST_CODE_EDIT_IMAGE && resultCode  == RESULT_OK )
      {
      AssetFragment assetFragment = mCustomImageEditorAgent.getAssetFragment( resultIntent );

      onImageEdited( assetFragment );

      return;
      }


    // Check for continue shopping result

    if ( resultCode  == ACTIVITY_RESULT_CODE_CONTINUE_SHOPPING )
      {
      setResult( resultCode );

      finish();

      return;
      }

    }


  ////////// IImageSpecStore Method(s) //////////

  /*****************************************************
   *
   * Returns the assets and quantity list.
   *
   *****************************************************/
  @Override
  public ArrayList<ImageSpec> getImageSpecArrayList()
    {
    return ( mImageSpecArrayList );
    }


  ////////// ImageSourceFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when image assets have been selected.
   *
   *****************************************************/
  @Override
  public void isOnAssetsAdded()
    {
    // Go to the first creation fragment appropriate for the journey type
    addNextFragment();
    }


  ////////// PhoneCaseFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a phone case has been created.
   *
   *****************************************************/
  @Override
  public void pcOnCreated( AssetFragment imageAssetFragment )
    {
    onNewBasketItem( imageAssetFragment );
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


  ////////// PhotobookFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called to edit an asset.
   *
   *****************************************************/
  @Override
  public void pbOnEdit( int assetIndex )
    {
    editImage( assetIndex );
    }


  /*****************************************************
   *
   * Called to move on to payment.
   *
   *****************************************************/
  @Override
  public void pbOnNext()
    {
    onNewBasketItem( mImageSpecArrayList );
    }


  ////////// CalendarFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called to edit an asset.
   *
   *****************************************************/
  @Override
  public void calOnEdit( int assetIndex )
    {
    editImage( assetIndex );
    }


  /*****************************************************
   *
   * Called to move on to payment.
   *
   *****************************************************/
  @Override
  public void calOnNext()
    {
    onNewBasketItem( mImageSpecArrayList );
    }


  ////////// PosterFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called to edit an asset.
   *
   *****************************************************/
  @Override
  public void posterOnEdit( int assetIndex )
    {
    editImage( assetIndex );
    }


  /*****************************************************
   *
   * Called to move on to payment.
   *
   *****************************************************/
  @Override
  public void posterOnNext()
    {
    onNewBasketItem( mImageSpecArrayList );
    }


  ////////// ReviewAndEditFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when an asset image is to be edited.
   *
   *****************************************************/
  @Override
  public void reOnEdit( int imageIndex )
    {
    editImage( imageIndex );
    }


  /*****************************************************
   *
   * Called when the confirm button is clicked.
   *
   *****************************************************/
  public void reOnConfirm()
    {
    onNewBasketItem( mImageSpecArrayList );
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
  public void eiOnConfirm( AssetFragment assetFragment )
    {
    // Remove the edit image fragment from the back stack
    popFragment();

    onImageEdited( assetFragment );
    }


  ////////// EditBorderTextImageFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the OK button is clicked.
   *
   *****************************************************/
  @Override
  public void btiOnForwards( AssetFragment assetFragment, String borderText )
    {
    // Remove the edit image fragment from the back stack
    popFragment();

    onImageEdited( assetFragment, borderText );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Asynchronous call-back when an order has been added
   * to the basket.
   *
   *****************************************************/
  @Override
  public void onItemAdded()
    {
    // Hide the progress dialog
    if ( mProgressDialog != null )
      {
      mProgressDialog.dismiss();

      mProgressDialog = null;
      }


    // Go to the basket screen. If we are in edit mode, then we were called from the basket
    // activity, so only need to finish to return.

    if ( mInEditMode )
      {
      finish();
      }
    else
      {
      BasketActivity.startForResult( this, ACTIVITY_REQUEST_CODE_ADD_TO_BASKET );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Starts the next stage in the appropriate user journey
   * for the supplied product.
   *
   *****************************************************/
  private void addNextFragment()
    {
    // Some screen we can go to if there are no image assets. Others, like the phone case, require
    // the user to select an image first.

    switch ( mProduct.getUserJourneyType() )
      {
      case CALENDAR:
        addFragment( CalendarFragment.newInstance( mProduct ), CalendarFragment.TAG );
        break;

      case CIRCLE:
      case GREETINGCARD:
      case RECTANGLE:
        addFragment( ImageSelectionFragment.newInstance( mProduct ), ImageSelectionFragment.TAG );
        break;

      case PHONE_CASE:
        if ( ! imageSourceFragmentStarted() )
          {
          addFragment( PhoneCaseFragment.newInstance( mProduct ), PhoneCaseFragment.TAG );
          }
        break;

      case PHOTOBOOK:
        addFragment( PhotobookFragment.newInstance( mProduct ), PhotobookFragment.TAG );
        break;

      case POSTER:
        addFragment( PosterFragment.newInstance( mProduct ), PosterFragment.TAG );
        break;
      }
    }


  /*****************************************************
   *
   * Starts the image source fragment if there are currently
   * no image assets.
   *
   * @return true, if the image source fragment was started
   *         (because there were no image assets).
   * @return false othwerise
   *
   *****************************************************/
  private boolean imageSourceFragmentStarted()
    {
    if ( mImageSpecArrayList.size() < 1 )
      {
      addFragment( ImageSourceFragment.newInstance( mProduct ), ImageSourceFragment.TAG );

      return ( true );
      }

    return ( false );
    }


  /*****************************************************
   *
   * Edits an image.
   *
   *****************************************************/
  private void editImage( int imageIndex )
    {
    mLastEditedImageIndex = imageIndex;


    // Get the asset we want to edit
    ImageSpec imageSpec = mImageSpecArrayList.get( imageIndex );


    // Start the edit fragment. By default we use the edit image fragment, but this can be
    // overridden within an app to use a custom editor.

    ICustomImageEditorAgent customImageEditorAgent = mSDKCustomiser.getCustomImageEditorAgent();

    if ( customImageEditorAgent != null )
      {
      mCustomImageEditorAgent = customImageEditorAgent;

      mCustomImageEditorAgent.onStartEditor( this, imageSpec.getAsset(), AKiteActivity.ACTIVITY_REQUEST_CODE_EDIT_IMAGE );
      }

    // If the product supports border text - launch the border text image editor
    else if ( mProduct.flagIsSet( Product.Flag.SUPPORTS_TEXT_ON_BORDER ) )
      {
      EditBorderTextImageFragment borderTextImageFragment = EditBorderTextImageFragment.newInstance( mProduct, imageSpec.getAssetFragment(), imageSpec.getBorderText() );

      addFragment( borderTextImageFragment, EditBorderTextImageFragment.TAG );
      }

    // Fall back to the standard image editor
    else
      {
      EditImageFragment editImageFragment = EditImageFragment.newInstance( mProduct, imageSpec.getAssetFragment() );

      addFragment( editImageFragment, EditImageFragment.TAG );
      }
    }


  /*****************************************************
   *
   * Called when an image is edited.
   *
   *****************************************************/
  public void onImageEdited( AssetFragment assetFragment, String borderText )
    {
    // Replace the edited asset with the new one

    ImageSpec imageSpec = mImageSpecArrayList.get( mLastEditedImageIndex );

    imageSpec.setImage( assetFragment, mProduct.getId() );
    imageSpec.setBorderText( borderText );


    // Once an image has been edited, we need to notify any fragments that use it. A fragment
    // identifies itself as wanting to be notified by implementing the IUpdatedAssetListener
    // interface.

    int entryCount = mFragmentManager.getBackStackEntryCount();

    for ( int entryIndex = 0; entryIndex < entryCount; entryIndex ++ )
      {
      FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt( entryIndex );

      if ( entry != null )
        {
        String fragmentName = entry.getName();

        Fragment fragment= mFragmentManager.findFragmentByTag( fragmentName );

        if ( fragment != null && fragment instanceof IUpdatedImageListener )
          {
          ( (IUpdatedImageListener) fragment ).onImageUpdated( mLastEditedImageIndex, imageSpec );
          }
        }
      }

    }


  /*****************************************************
   *
   * Called when an image is edited.
   *
   *****************************************************/
  public void onImageEdited( AssetFragment assetFragment )
    {
    onImageEdited( assetFragment, null );
    }


  /*****************************************************
   *
   * Ensures that the progress dialog is showing.
   *
   *****************************************************/
  private void showProgressDialog( int titleResourceId )
    {
    mProgressDialog = new ProgressDialog( this );
    mProgressDialog.setTitle( titleResourceId );
    mProgressDialog.setIndeterminate( true );
    mProgressDialog.show();
    }


  /*****************************************************
   *
   * Adds or replaces an item in the basket, then proceeds
   * to the next stage.
   *
   *****************************************************/
  private void onNewBasketItem( List<ImageSpec> imageSpecList )
    {
    OrderingDataAgent orderingDataAgent = OrderingDataAgent.getInstance( this );

    if ( mInEditMode )
      {
      showProgressDialog( R.string.progress_dialog_title_updating_basket );

      orderingDataAgent.replaceItem( mBasketItemId, mProduct, mOptionMap, imageSpecList, mOrderQuantity, this , 123);//123 for N/A , will  be set right in ShippingMethod
      }
    else
      {
      showProgressDialog( R.string.progress_dialog_title_add_to_basket );

      orderingDataAgent.addItem( mProduct, mOptionMap, imageSpecList, this ,123);//123 for N/A , will  be set right in ShippingMethod
      }

    }


  /*****************************************************
   *
   * Adds or replaces an item in the basket, then proceeds
   * to the next stage.
   *
   *****************************************************/
  private void onNewBasketItem( AssetFragment assetFragment )
    {
    List<ImageSpec> imageSpecList = new ArrayList<>( 1 );

    imageSpecList.add( new ImageSpec( assetFragment, 1 ) );

    onNewBasketItem( imageSpecList );
    }


  ////////// Inner Class(es) //////////


  }
