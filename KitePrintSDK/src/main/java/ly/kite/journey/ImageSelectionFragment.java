/*****************************************************
 *
 * ImageSelectionFragment.java
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import ly.kite.analytics.Analytics;
import ly.kite.journey.imageselection.ImagePackAdaptor;
import ly.kite.journey.imageselection.ImageSource;
import ly.kite.journey.imageselection.ImageSourceAdaptor;
import ly.kite.product.Asset;
import ly.kite.product.Product;

import ly.kite.R;
import ly.kite.util.BooleanHelper;
import ly.kite.widget.VisibilitySettingAnimationListener;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class ImageSelectionFragment extends AJourneyFragment implements AdapterView.OnItemClickListener,
                                                                        View.OnClickListener,
                                                                        ImagePackAdaptor.IOnImageCheckChangeListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                          = "ImageSelectionFragment";

  public  static final String      BUNDLE_KEY_ASSET_LIST             = "assetList";
  public  static final String      BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY = "assetIsCheckedArray";
  public  static final String      BUNDLE_KEY_PRODUCT                = "product";

  private static final int         REQUEST_CODE_SELECT_IMAGE        = 10;

  private static final long        CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS   = 300L;
  private static final long        PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS = CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>            mAssetArrayList;
  private Product                     mProduct;

  private ArrayList<Boolean>          mSharedAssetIsCheckedArrayList;
  private int                         mUncheckedImagesCount;

  private int                         mNumberOfColumns;

  private BaseAdapter                 mImageSourceAdaptor;
  private GridView                    mImageSourceGridView;
  private Button                      mClearPhotosButton;
  private Button                      mProceedOverlayButton;

  private RecyclerView                mImageRecyclerView;
  private GridLayoutManager           mImageLayoutManager;
  private ImagePackAdaptor            mImagePackAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ImageSelectionFragment newInstance( ArrayList<Asset> assetArrayList, Product product )
    {
    ImageSelectionFragment fragment = new ImageSelectionFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelableArrayList( BUNDLE_KEY_ASSET_LIST, assetArrayList );
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
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


    // We want to check if we are being re-created first, because the assets may have changed
    // since we were first launched - in which case the asset lists from the saved instance state
    // will be different from those that were provided in the argument bundle.

    if ( savedInstanceState != null )
      {
      mAssetArrayList = savedInstanceState.getParcelableArrayList( BUNDLE_KEY_ASSET_LIST );


      boolean[] assetIsCheckedArray = savedInstanceState.getBooleanArray( BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY );

      mSharedAssetIsCheckedArrayList = BooleanHelper.arrayListFrom( assetIsCheckedArray );
      }


    // Get the assets and product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_no_arguments,
              R.string.alert_dialog_message_no_arguments,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // Only get the asset list if we couldn't find a saved list
    if ( mAssetArrayList == null )
      {
      if ( ( mAssetArrayList = arguments.getParcelableArrayList( BUNDLE_KEY_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
        {
        Log.e( LOG_TAG, "No asset list found" );

        mKiteActivity.displayModalDialog(
                R.string.alert_dialog_title_no_asset_list,
                R.string.alert_dialog_message_no_asset_list,
                AKiteActivity.DONT_DISPLAY_BUTTON,
                null,
                R.string.Cancel,
                mKiteActivity.new FinishRunnable()
        );

        return;
        }
      }


    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // If we don't have a valid "is checked" list - create a new one with all the images checked.

    mUncheckedImagesCount = 0;

    if ( mSharedAssetIsCheckedArrayList == null || mSharedAssetIsCheckedArrayList.size() != mAssetArrayList.size() )
      {
      mSharedAssetIsCheckedArrayList = new ArrayList<>( mAssetArrayList.size() );

      for ( Asset asset : mAssetArrayList ) mSharedAssetIsCheckedArrayList.add( true );
      }
    else
      {
      // We already have a valid list, so scan it and calculate the number of unchecked images.

      for ( boolean isChecked : mSharedAssetIsCheckedArrayList )
        {
        if ( ! isChecked ) mUncheckedImagesCount ++;
        }
      }


    mNumberOfColumns = getResources().getInteger( R.integer.image_selection_grid_num_columns );
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_image_selection, container, false );

    mImageSourceGridView  = (GridView)view.findViewById( R.id.image_source_grid_view );
    mImageRecyclerView    = (RecyclerView)view.findViewById( R.id.image_recycler_view );
    mClearPhotosButton    = (Button)view.findViewById( R.id.clear_photos_button );
    mProceedOverlayButton = (Button)view.findViewById( R.id.proceed_overlay_button );


    // Display the image sources
    ArrayList<ImageSource> imageSourceList = new ArrayList<>();
    imageSourceList.add( ImageSource.DEVICE );
    mImageSourceAdaptor = new ImageSourceAdaptor( mKiteActivity, imageSourceList );
    mImageSourceGridView.setNumColumns( mImageSourceAdaptor.getCount() );
    mImageSourceGridView.setAdapter( mImageSourceAdaptor );


    // Set up the image recycler view
    setUpRecyclerView();


    // If there are unchecked images, then we need to show (but not animate in) the clear photos
    // button, and set the correct text.

    if ( mUncheckedImagesCount > 0 )
      {
      mClearPhotosButton.setVisibility( View.VISIBLE );

      setClearPhotosButtonText();

      mProceedOverlayButton.setVisibility( View.GONE );
      }
    else
      {
      mClearPhotosButton.setVisibility( View.GONE );

      mProceedOverlayButton.setVisibility( View.VISIBLE );
      }


    mProceedOverlayButton.setText( R.string.image_selection_review_button_text );


    // Set up the listener(s)
    mImageSourceGridView.setOnItemClickListener( this );
    mClearPhotosButton.setOnClickListener( this );
    mProceedOverlayButton.setOnClickListener( this );


    // TODO: Create a common superclass of all product creation fragments
    if ( savedInstanceState == null )
      {
      Analytics.getInstance( mKiteActivity ).trackCreateProductScreenViewed( mProduct );
      }


    return ( view );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  protected void onTop()
    {
    if ( mProduct != null ) updateTitle();
    }


  /*****************************************************
   *
   * Saves the current state to the supplied bundle.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    outState.putParcelableArrayList( BUNDLE_KEY_ASSET_LIST, mAssetArrayList );


    // We need to convert the Boolean list into a boolean array before we can
    // add it to the bundle.

    boolean[] isCheckedArray = BooleanHelper.arrayFrom( mSharedAssetIsCheckedArrayList );

    outState.putBooleanArray( BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY, isCheckedArray );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );

    if ( requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK )
      {
      // We don't allow duplicate images, so first check that the asset isn't already in
      // our list. Note that we don't check that the image is the same but come from
      // different sources.

      Uri newImageUri = data.getData();

      Asset newAsset = new Asset( newImageUri );

      if ( ! Asset.isInList( mAssetArrayList, newAsset ) )
        {
        // Add the selected image to our asset list, mark it as checked, and update the recycler view.

        mAssetArrayList.add( new Asset( newImageUri ) );
        mSharedAssetIsCheckedArrayList.add( true );

        setUpRecyclerView();
        }
      }
    }


  /*****************************************************
   *
   * Called when the back key is pressed.
   *
   *****************************************************/
  @Override
  public boolean onBackPressIntercepted()
    {
    // If any images are unchecked (i.e. marked for deletion), then
    // the back key will cancel the deletion - i.e. all the images
    // will be checked again.

    if ( mUncheckedImagesCount < 1 ) return ( false );


    // Re-check any unchecked images

    int assetIndex = 0;

    for ( Boolean booleanObject : mSharedAssetIsCheckedArrayList )
      {
      if ( ! booleanObject ) mSharedAssetIsCheckedArrayList.set( assetIndex, true );

      assetIndex ++;
      }

    mUncheckedImagesCount = 0;

    animateClearPhotosButtonOut();
    animateProceedOverlayButtonIn();

    mImagePackAdaptor.onUpdateCheckedImages();


    return ( true );
    }


  ////////// AdapterView.OnItemClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when an item is clicked.
   *
   *****************************************************/
  @Override
  public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    {
    if ( parent == mImageSourceGridView )
      {
      ///// Image Source /////

      ImageSource imageSource = (ImageSource)mImageSourceGridView.getItemAtPosition( position );

      imageSource.onClick( this, REQUEST_CODE_SELECT_IMAGE );
      }
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mClearPhotosButton )
      {
      ///// Clear photos /////

      // We need to go through all the assets and remove any that are unchecked - both the
      // actual asset and the is checked value.

      for ( int assetIndex = 0; assetIndex < mAssetArrayList.size(); assetIndex ++ )
        {
        if ( ! mSharedAssetIsCheckedArrayList.get( assetIndex ) )
          {
          mAssetArrayList.remove( assetIndex );
          mSharedAssetIsCheckedArrayList.remove( assetIndex );

          // If we delete an asset, then the next asset now falls into its place
          assetIndex --;
          }
        }

      mUncheckedImagesCount = 0;


      // Update the screen

      animateClearPhotosButtonOut();
      animateProceedOverlayButtonIn();

      setUpRecyclerView();
      }
    else if ( view == mProceedOverlayButton )
      {
      ///// Review and Crop /////
      }

    }


  ////////// ImagePackAdaptor.IOnImageCheckChangeListener Method(s) //////////

  /*****************************************************
   *
   * Called when the checked state of an asset image
   * changes.
   *
   *****************************************************/
  public void onImageCheckChange( int assetIndex, boolean isChecked )
    {
    int previousUncheckedImagesCount = mUncheckedImagesCount;

    // Update the unchecked images count
    if ( isChecked ) mUncheckedImagesCount --;
    else             mUncheckedImagesCount ++;


    // Check if we need to show or hide the clear photos button

    if ( previousUncheckedImagesCount > 0 && mUncheckedImagesCount == 0 )
      {
      ///// Hide button /////

      animateClearPhotosButtonOut();
      animateProceedOverlayButtonIn();
      }
    else if ( previousUncheckedImagesCount == 0 && mUncheckedImagesCount > 0 )
      {
      /////  Show button /////

      animateProceedOverlayButtonOut();
      animateClearPhotosButtonIn();
      }

    // Set the text
    if ( mUncheckedImagesCount > 0 )
      {
      setClearPhotosButtonText();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Updates the title.
   *
   *****************************************************/
  private void updateTitle()
    {
    mKiteActivity.setTitle( mProduct.getName() );
    }


  /*****************************************************
   *
   * Creates the recycler view adaptor and sets it.
   *
   *****************************************************/
  private void setUpRecyclerView()
    {
    mImagePackAdaptor = new ImagePackAdaptor( mKiteActivity, mProduct, mAssetArrayList, mSharedAssetIsCheckedArrayList, mNumberOfColumns, this );

    mImageLayoutManager = new GridLayoutManager( mKiteActivity, mNumberOfColumns );
    mImageLayoutManager.setSpanSizeLookup( mImagePackAdaptor.new SpanSizeLookup( mNumberOfColumns ) );

    mImageRecyclerView.setLayoutManager( mImageLayoutManager );

    mImageRecyclerView.setAdapter( mImagePackAdaptor );
    }


  /*****************************************************
   *
   * Sets the clear photos button text.
   *
   *****************************************************/
  private void setClearPhotosButtonText()
    {
    String buttonText = getString( R.string.image_selection_clear_photos_format_string, mUncheckedImagesCount, getResources().getQuantityString( R.plurals.Photos_plurals, mUncheckedImagesCount ) );

    mClearPhotosButton.setText( buttonText );
    }


  /*****************************************************
   *
   * Animates the clear photos button in.
   *
   *****************************************************/
  private void animateClearPhotosButtonIn()
    {
    mClearPhotosButton.setVisibility( View.VISIBLE );

    Animation animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            1f,
            Animation.RELATIVE_TO_SELF,
            0f );

    animation.setDuration( CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS );

    mClearPhotosButton.startAnimation( animation );
    }


  /*****************************************************
   *
   * Animates the clear photos button out.
   *
   *****************************************************/
  private void animateClearPhotosButtonOut()
    {
    mClearPhotosButton.setVisibility( View.VISIBLE );

    Animation animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            1f );

    animation.setDuration( CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS );
    animation.setFillAfter( true );
    animation.setAnimationListener( new VisibilitySettingAnimationListener( mClearPhotosButton, View.GONE ) );

    mClearPhotosButton.startAnimation( animation );
    }


  /*****************************************************
   *
   * Animates the proceed overlay button in.
   *
   *****************************************************/
  private void animateProceedOverlayButtonIn()
    {
    mProceedOverlayButton.setVisibility( View.VISIBLE );

    Animation animation = new AlphaAnimation( 0f, 1f );

    animation.setDuration( PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS );

    mProceedOverlayButton.startAnimation( animation );
    }


  /*****************************************************
   *
   * Animates the proceed overlay button out.
   *
   *****************************************************/
  private void animateProceedOverlayButtonOut()
    {
    mProceedOverlayButton.setVisibility( View.VISIBLE );

    Animation animation = new AlphaAnimation( 1f, 0f );

    animation.setDuration( PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS );
    animation.setFillAfter( true );
    animation.setAnimationListener( new VisibilitySettingAnimationListener( mProceedOverlayButton, View.GONE ) );

    mProceedOverlayButton.startAnimation( animation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void isOnNext( List<Asset> assetList );
    }

  }

