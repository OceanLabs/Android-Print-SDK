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

package ly.kite.journey.creation.imageselection;


///// Import(s) /////

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.journey.ImageSource;
import ly.kite.journey.ImageSourceAdaptor;
import ly.kite.catalogue.Asset;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Product;

import ly.kite.R;
import ly.kite.util.BooleanHelper;
import ly.kite.util.IImageConsumer;
import ly.kite.util.IImageTransformer;
import ly.kite.util.ImageAgent;
import ly.kite.widget.VisibilitySettingAnimationListener;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class ImageSelectionFragment extends AProductCreationFragment implements AdapterView.OnItemClickListener,
                                                                                View.OnClickListener,
                                                                                ImageSelectionAdaptor.IOnImageCheckChangeListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String      TAG                                             = "ImageSelectionFragment";

  public  static final String      BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY               = "assetIsCheckedArray";

  private static final long        CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS   = 300L;
  private static final long        PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS = CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS;

  private static final int         PROGRESS_COMPLETE                               = 100;  // 100%


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Boolean>           mAssetIsCheckedArrayList;
  private int                          mUncheckedImagesCount;

  private int                          mNumberOfColumns;

  private int                          mInitialUneditedAssetsCount;
  private int                          mUneditedAssetsRemaining;

  private BaseAdapter                  mImageSourceAdaptor;
  private GridView                     mImageSourceGridView;
  private ProgressBar                  mProgressBar;
  private Button                       mClearPhotosButton;
  private Button                       mProceedOverlayButton;

  private RecyclerView                 mImageRecyclerView;
  private GridLayoutManager            mImageLayoutManager;
  private ImageSelectionAdaptor        mImagePackAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ImageSelectionFragment newInstance( Product product )
    {
    ImageSelectionFragment fragment = new ImageSelectionFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AJourneyFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // See if we saved an "is checked" array

    if ( savedInstanceState != null )
      {
      boolean[] assetIsCheckedArray = savedInstanceState.getBooleanArray( BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY );

      mAssetIsCheckedArrayList = BooleanHelper.arrayListFrom( assetIsCheckedArray );
      }
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
    mProgressBar          = (ProgressBar)view.findViewById( R.id.progress_bar );
    mImageRecyclerView    = (RecyclerView)view.findViewById( R.id.image_recycler_view );
    mClearPhotosButton    = (Button)view.findViewById( R.id.clear_photos_button );
    mProceedOverlayButton = (Button)view.findViewById( R.id.proceed_overlay_button );


    // Set up the image sources

    ArrayList<ImageSource> imageSourceList = KiteSDK.getInstance( mKiteActivity ).getAvailableImageSources();

    mImageSourceAdaptor = new ImageSourceAdaptor( mKiteActivity, R.layout.grid_item_image_source_horizontal, imageSourceList );
    mImageSourceGridView.setNumColumns( mImageSourceAdaptor.getCount() );
    mImageSourceGridView.setAdapter( mImageSourceAdaptor );


    // Set up the listener(s)
    mImageSourceGridView.setOnItemClickListener( this );
    mClearPhotosButton.setOnClickListener( this );
    mProceedOverlayButton.setOnClickListener( this );


    return ( view );
    }


  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // If we don't have a valid "is checked" list - create a new one with all the images checked.

    mUncheckedImagesCount = 0;

    if ( mAssetIsCheckedArrayList == null || mAssetIsCheckedArrayList.size() != mAssetsAndQuantityArrayList.size() )
      {
      mAssetIsCheckedArrayList = new ArrayList<>( mAssetsAndQuantityArrayList.size() );

      for ( AssetsAndQuantity assetAndQuantity : mAssetsAndQuantityArrayList ) mAssetIsCheckedArrayList.add( true );
      }
    else
      {
      // We already have a valid list, so scan it and calculate the number of unchecked images.

      for ( boolean isChecked : mAssetIsCheckedArrayList )
        {
        if ( ! isChecked ) mUncheckedImagesCount ++;
        }
      }


    // We need to create a set of initial edited images - which are basically cropped
    // to a square. We need to do these on a background thread, but We also need to make
    // sure that we can't go further until all of them have been completed.

    mUneditedAssetsRemaining = 0;

    for ( AssetsAndQuantity assetsAndQuantity : mAssetsAndQuantityArrayList )
      {
      String productId = mProduct.getId();


      // If we don't already have an edited asset - create one now

      if ( ( productId == null ) ||
              ( ! productId.equals( assetsAndQuantity.getEditedForProductId() ) ) )
        {
        mUneditedAssetsRemaining ++;

        AssetImageCropper cropper = new AssetImageCropper( assetsAndQuantity, mProduct.getImageAspectRatio() );

        AssetHelper.requestImage( mKiteActivity, assetsAndQuantity.getUneditedAsset(), cropper, 0, cropper );
        }
      }

    mInitialUneditedAssetsCount = mUneditedAssetsRemaining;


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


    mProceedOverlayButton.setText( R.string.image_selection_proceed_button_text );


    showProgress( mUneditedAssetsRemaining, mInitialUneditedAssetsCount );
    }


  /*****************************************************
   *
   * Saves the current state to the supplied bundle.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );


    // We need to convert the Boolean list into a boolean array before we can
    // add it to the bundle.

    boolean[] isCheckedArray = BooleanHelper.arrayFrom( mAssetIsCheckedArrayList );

    outState.putBooleanArray( BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY, isCheckedArray );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent returnedIntent )
    {
    super.onActivityResult( requestCode, resultCode, returnedIntent );


    // Get assets for any images returned and add them

    List<Asset> assetList = ImageSource.getAssetsFromResult( requestCode, resultCode, returnedIntent );

    if ( assetList != null )
      {
      addAssets( assetList );
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

    for ( Boolean booleanObject : mAssetIsCheckedArrayList )
      {
      if ( ! booleanObject ) mAssetIsCheckedArrayList.set( assetIndex, true );

      assetIndex ++;
      }

    mUncheckedImagesCount = 0;


    // Update the screen

    setTitle();

    animateClearPhotosButtonOut();
    animateProceedOverlayButtonIn();

    mImagePackAdaptor.onUpdateCheckedImages();


    return ( true );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();


    if ( mProduct != null ) setTitle();


    setUpRecyclerView();


    // We don't enable the proceed button until all the assets have been cropped

    if ( mUneditedAssetsRemaining < 1 )
      {
      mProceedOverlayButton.setEnabled( true );
      }
    else
      {
      mProceedOverlayButton.setEnabled( false );
      }
    }


  /*****************************************************
   *
   * Called when the fragment is not on top.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    super.onNotTop();


    // Clear out the stored images to reduce memory usage
    // when not on this screen.

    if ( mImageRecyclerView != null ) mImageRecyclerView.setAdapter( null );

    mImagePackAdaptor = null;
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

      imageSource.onPick( this, false );
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

      // We need to go through all the assets and remove any that are unchecked - from
      // both lists, and the "is checked" value.

      for ( int assetIndex = 0; assetIndex < mAssetsAndQuantityArrayList.size(); assetIndex ++ )
        {
        if ( ! mAssetIsCheckedArrayList.get( assetIndex ) )
          {
          mAssetsAndQuantityArrayList.remove( assetIndex );
          mAssetIsCheckedArrayList.remove( assetIndex );

          // If we delete an asset, then the next asset now falls into its place
          assetIndex --;
          }
        }

      mUncheckedImagesCount = 0;


      // Update the screen

      setTitle();

      animateClearPhotosButtonOut();
      animateProceedOverlayButtonIn();

      setUpRecyclerView();
      }
    else if ( view == mProceedOverlayButton )
      {
      ///// Review and Crop /////

      if ( mAssetsAndQuantityArrayList.isEmpty() )
        {
        mKiteActivity.displayModalDialog(R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_images_selected, R.string.OK, null, 0, null);
        }
      else if ( mKiteActivity instanceof ICallback )
        {
        ( (ICallback)mKiteActivity ).isOnNext();
        }
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

    setTitle();


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
   * Shows the cropping progress.
   *
   *****************************************************/
  private void showProgress( int remainingCount, int totalCount )
    {
    if ( mProgressBar == null ) return;


    // If there are no images, or none left to crop, don't show the
    // progress bar.

    if ( totalCount < 1 || remainingCount < 1 )
      {
      mProgressBar.setVisibility( View.INVISIBLE );
      }
    else
      {
      mProgressBar.setVisibility( View.VISIBLE );

      mProgressBar.setProgress( PROGRESS_COMPLETE * ( totalCount - remainingCount ) / totalCount );
      }
    }


  /*****************************************************
   *
   * Sets up the recycler view.
   *
   *****************************************************/
  private void setUpRecyclerView()
    {
    if ( mNumberOfColumns == 0 )
      {
      mNumberOfColumns = getResources().getInteger( R.integer.image_selection_grid_num_columns );
      }

    mImagePackAdaptor = new ImageSelectionAdaptor( mKiteActivity, mProduct, mAssetsAndQuantityArrayList, mAssetIsCheckedArrayList, mNumberOfColumns, this );

    mImageLayoutManager = new GridLayoutManager( mKiteActivity, mNumberOfColumns );
    mImageLayoutManager.setSpanSizeLookup( mImagePackAdaptor.new SpanSizeLookup( mNumberOfColumns ) );

    mImageRecyclerView.setLayoutManager( mImageLayoutManager );

    mImageRecyclerView.setAdapter( mImagePackAdaptor );
    }


  /*****************************************************
   *
   * Adds new unedited assets to the users collection.
   * Duplicates will be discarded.
   *
   *****************************************************/
  private void addAssets( List<Asset> assets )
    {
    boolean addedNewAsset = false;

    for ( Asset asset : assets )
      {
      // We don't allow duplicate images, so first check that the asset isn't already in
      // our list. Note that we don't check the scenario where the image is the same but
      // from a different source - a byte by byte comparison would take too long, and a
      // duplicate is unlikely anyway.

      if ( ! AssetsAndQuantity.uneditedAssetIsInList( mAssetsAndQuantityArrayList, asset ) )
        {
        // Start with the unedited asset, and a quantity of 1.

        AssetsAndQuantity assetsAndQuantity = new AssetsAndQuantity( asset );

        mUneditedAssetsRemaining++;


        // Create an edited version of the asset. We are basically doing the same thing we did
        // when we were created, but just for the new asset. We are doing this in the background
        // again, so we need to disable the proceed button again.

        if ( ! addedNewAsset )
          {
          addedNewAsset = true;

          mProceedOverlayButton.setEnabled( false );
          }


        // Add the selected image to our asset lists, mark it as checked
        mAssetsAndQuantityArrayList.add( assetsAndQuantity );
        mAssetIsCheckedArrayList.add( true );

        // Let the adaptor know we've added another asset.
        mImagePackAdaptor.addAsset( assetsAndQuantity );


        // Request the image and crop it

        AssetImageCropper cropper = new AssetImageCropper( assetsAndQuantity, mProduct.getImageAspectRatio() );

        AssetHelper.requestImage( mKiteActivity, assetsAndQuantity.getUneditedAsset(), cropper, 0, cropper );
        }
      }


    if ( addedNewAsset )
      {
      setTitle();

      mInitialUneditedAssetsCount = mUneditedAssetsRemaining;

      showProgress( mUneditedAssetsRemaining, mInitialUneditedAssetsCount );
      }
    }


  /*****************************************************
   *
   * Updates the title.
   *
   *****************************************************/
  private void setTitle()
    {
    // Calculate the total number of images

    int numberOfImages = 0;

    int assetIndex = 0;

    for ( AssetsAndQuantity assetAndQuantity : mAssetsAndQuantityArrayList )
      {
      if ( mAssetIsCheckedArrayList.get( assetIndex ) ) numberOfImages += assetAndQuantity.getQuantity();

      assetIndex ++;
      }


    int quantityPerPack = mProduct.getQuantityPerSheet();
    int numberOfPacks   = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;

    mKiteActivity.setTitle( getString( R.string.image_selection_title_format_string, mProduct.getName(), numberOfImages, ( numberOfPacks * quantityPerPack ) ) );
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


  /*****************************************************
   *
   * Updates the assets and quantity.
   *
   *****************************************************/
  public void onAssetUpdated( int assetIndex, AssetsAndQuantity assetsAndQuantity )
    {
    // We don't need to request any cropped image because it is the edited asset
    // that has been updated. So just updated the recycler view.
    if ( mImagePackAdaptor != null ) mImagePackAdaptor.notifyDataSetChanged();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void isOnNext();
    }


  /*****************************************************
   *
   * An image transformer that crops the supplied image
   * to a square, creates an asset from it, and then stores
   * it as an edited asset.
   *
   * We also use it as the image consumer, because the available
   * method gets called on the UI thread.
   *
   *****************************************************/
  private class AssetImageCropper implements IImageTransformer, IImageConsumer
    {
    private AssetsAndQuantity  mAssetsAndQuantity;
    private float              mCroppedAspectRatio;


    AssetImageCropper( AssetsAndQuantity assetsAndQuantity, float croppedAspectRatio )
      {
      mAssetsAndQuantity  = assetsAndQuantity;
      mCroppedAspectRatio = croppedAspectRatio;
      }


    ////////// AssetHelper.IImageTransformer Method(s) //////////

    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      // Crop the bitmap to the required shape
      Bitmap croppedBitmap = ImageAgent.crop( bitmap, mCroppedAspectRatio );


      // Create a new file-backed asset from the cropped bitmap, and save it as the edited asset.

      Asset editedAsset = AssetHelper.createAsCachedFile( mKiteActivity, croppedBitmap );

      mAssetsAndQuantity.setEditedAsset( editedAsset, mProduct.getId() );


      return ( croppedBitmap );
      }


    ////////// IImageConsumer Method(s) //////////

    @Override
    public void onImageDownloading( Object key )
      {
      // Ignore
      }


    /*****************************************************
     *
     * Called on the UI thread, with the cropped image.
     *
     *****************************************************/
    @Override
    public void onImageAvailable( Object key, Bitmap bitmap )
      {
      // Update the adaptor with the edited (cropped) image

      if ( mImagePackAdaptor != null )
        {
        int position = mImagePackAdaptor.positionOf( mAssetsAndQuantity );

        if ( position >= 0 ) mImagePackAdaptor.notifyItemChanged( position );
        }


      // If we now have all the cropped images - enable the proceed button.

      mUneditedAssetsRemaining --;

      showProgress( mUneditedAssetsRemaining, mInitialUneditedAssetsCount );

      if ( mUneditedAssetsRemaining < 1 )
        {
        if ( mProceedOverlayButton != null ) mProceedOverlayButton.setEnabled( true );
        }
      }
    }


  }

