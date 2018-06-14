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

import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.journey.AImageSource;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.journey.ImageSourceAdaptor;
import ly.kite.catalogue.Product;

import ly.kite.R;
import ly.kite.journey.creation.IUpdatedImageListener;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.BooleanUtils;
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
                                                                                ImageSelectionAdaptor.IOnImageCheckChangeListener,
                                                                                IUpdatedImageListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String      TAG                                             = "ImageSelectionFragment";

  public  static final String      BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY               = "assetIsCheckedArray";

  private static final long        CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS   = 300L;
  private static final long        PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS = CLEAR_PHOTOS_BUTTON_ANIMATION_DURATION_MILLIS;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Boolean>           mAssetIsCheckedArrayList;
  private int                          mUncheckedImagesCount;

  private int                          mNumberOfColumns;

  private BaseAdapter                  mImageSourceAdaptor;
  private GridView                     mImageSourceGridView;
  private View                         mFadeableView;
  private Button                       mClearPhotosButton;
  private View                         mEmptyView;

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

      mAssetIsCheckedArrayList = BooleanUtils.arrayListFrom( assetIsCheckedArray );
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

    super.onViewCreated( view );

    mImageSourceGridView  = (GridView)view.findViewById( R.id.image_source_grid_view );
    mImageRecyclerView    = (RecyclerView)view.findViewById( R.id.image_recycler_view );
    mFadeableView          = view.findViewById( R.id.fadeable_view );
    mClearPhotosButton    = (Button)view.findViewById( R.id.clear_photos_button );
    mEmptyView            = view.findViewById( R.id.empty_view );


    // Set up the image sources

    ArrayList<AImageSource> imageSourceList = KiteSDK.getInstance( mKiteActivity ).getAvailableImageSources();

    if ( mImageSourceGridView != null )
      {
      mImageSourceAdaptor = new ImageSourceAdaptor( mKiteActivity, AImageSource.LayoutType.HORIZONTAL, imageSourceList );
      mImageSourceGridView.setNumColumns( mImageSourceAdaptor.getCount() );
      mImageSourceGridView.setAdapter( mImageSourceAdaptor );

      mImageSourceGridView.setOnItemClickListener( this );
      }


    checkEmptyView();


    mClearPhotosButton.setOnClickListener( this );

    setForwardsTextViewOnClickListener( this );


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

    if ( mAssetIsCheckedArrayList == null || mAssetIsCheckedArrayList.size() != mImageSpecArrayList.size() )
      {
      mAssetIsCheckedArrayList = new ArrayList<>( mImageSpecArrayList.size() );

      for ( ImageSpec imageSpec : mImageSpecArrayList ) mAssetIsCheckedArrayList.add( true );
      }
    else
      {
      // We already have a valid list, so scan it and calculate the number of unchecked images.

      for ( boolean isChecked : mAssetIsCheckedArrayList )
        {
        if ( ! isChecked ) mUncheckedImagesCount ++;
        }
      }


    // Get cropped versions of assets.
    requestCroppedAssets();


    // If there are unchecked images, then we need to show (but not animate in) the clear photos
    // button, and set the correct text.

    if ( mUncheckedImagesCount > 0 )
      {
      mClearPhotosButton.setVisibility( View.VISIBLE );

      setClearPhotosButtonText();

      setForwardsTextViewVisibility( View.GONE );
      }
    else
      {
      mClearPhotosButton.setVisibility( View.GONE );

      setForwardsTextViewVisibility( View.VISIBLE );
      }


    setForwardsTextViewText( R.string.kitesdk_image_selection_proceed_button_text);
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

    boolean[] isCheckedArray = BooleanUtils.arrayFrom( mAssetIsCheckedArrayList );

    outState.putBooleanArray( BUNDLE_KEY_ASSET_IS_CHECKED_ARRAY, isCheckedArray );
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
    animateFadeableViewIn();

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

    checkEmptyView();
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


  /*****************************************************
   *
   * Called when an image is cropped.
   *
   *****************************************************/
  protected void onImageCropped( ImageSpec imageSpec )
    {
    // Update the adaptor with the edited (cropped) image

    if ( mImagePackAdaptor != null )
      {
      int position = mImagePackAdaptor.positionOf( imageSpec );

      if ( position >= 0 ) mImagePackAdaptor.notifyItemChanged( position );
      }
    }


  /*****************************************************
   *
   * Called when a new asset is added.
   *
   *****************************************************/
  @Override
  protected void onAssetAdded( ImageSpec imageSpec )
    {
    mAssetIsCheckedArrayList.add( true );

    mImagePackAdaptor.addAsset( imageSpec );

    checkEmptyView();
    }


  /*****************************************************
   *
   * Called when at least one new asset has been added.
   *
   *****************************************************/
  @Override
  protected void onNewAssetsBeingCropped()
    {
    setTitle();
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
    if ( mImageSourceGridView != null && parent == mImageSourceGridView )
      {
      ///// Image Source /////

      AImageSource imageSource = (AImageSource)mImageSourceGridView.getItemAtPosition( position );

      imageSource.onPick( this, getTotalImagesUsedCount(), mProduct.hasMultiplePackSupport(), mProduct.getQuantityPerSheet(), position );
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

      for ( int assetIndex = 0; assetIndex < mImageSpecArrayList.size(); assetIndex ++ )
        {
        if ( ! mAssetIsCheckedArrayList.get( assetIndex ) )
          {
          mImageSpecArrayList.remove( assetIndex );
          mAssetIsCheckedArrayList.remove( assetIndex );

          // If we delete an asset, then the next asset now falls into its place
          assetIndex --;
          }
        }

      mUncheckedImagesCount = 0;


      // Update the screen

      setTitle();

      animateClearPhotosButtonOut();
      animateFadeableViewIn();

      setUpRecyclerView();

      checkEmptyView();

      return;
      }
    else if ( view == getForwardsTextView() )
      {
      ///// Review and Crop /////

      if ( mImageSpecArrayList.isEmpty() )
        {
        mKiteActivity.displayModalDialog(R.string.kitesdk_alert_dialog_title_oops, R.string.kitesdk_alert_dialog_message_no_images_selected, R.string.kitesdk_OK, null, 0, null);
        }
      else if ( mKiteActivity instanceof ICallback )
        {
        ( (ICallback)mKiteActivity ).isOnNext();
        }

      return;
      }

    super.onClick( view );
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
      animateFadeableViewIn();
      }
    else if ( previousUncheckedImagesCount == 0 && mUncheckedImagesCount > 0 )
      {
      /////  Show button /////

      animateFadeableViewOut();
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
   * Updates the image spec.
   *
   *****************************************************/
  @Override
  public void onImageUpdated( int imageIndex, ImageSpec imageSpec )
    {
    // We don't need to request any cropped image because it is the edited asset
    // that has been updated. So just updated the recycler view.
    if ( mImagePackAdaptor != null ) mImagePackAdaptor.notifyDataSetChanged();
    }


  ////////// Method(s) //////////

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

    mImagePackAdaptor = new ImageSelectionAdaptor( mKiteActivity, mProduct, mImageSpecArrayList, mAssetIsCheckedArrayList, mNumberOfColumns, this );

    mImageLayoutManager = new GridLayoutManager( mKiteActivity, mNumberOfColumns );
    mImageLayoutManager.setSpanSizeLookup( mImagePackAdaptor.new SpanSizeLookup( mNumberOfColumns ) );

    mImageRecyclerView.setLayoutManager( mImageLayoutManager );

    mImageRecyclerView.setAdapter( mImagePackAdaptor );
    }


  /*****************************************************
   *
   * Displays / hides any empty view.
   *
   *****************************************************/
  private void checkEmptyView()
    {
    if ( mEmptyView != null && mImageSpecArrayList != null )
      {
      if ( mImageSpecArrayList.size() > 0 ) mEmptyView.setVisibility( View.GONE );
      else                                  mEmptyView.setVisibility( View.VISIBLE );
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

    for ( ImageSpec imageSpec : mImageSpecArrayList )
      {
      if ( mAssetIsCheckedArrayList.get( assetIndex ) ) numberOfImages += imageSpec.getQuantity();

      assetIndex ++;
      }


    int quantityPerPack = mProduct.getQuantityPerSheet();
    int numberOfPacks   = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;

    mKiteActivity.setTitle( getString( R.string.kitesdk_image_selection_title_format_string, mProduct.getName(), numberOfImages, ( numberOfPacks * quantityPerPack ) ) );
    }


  /*****************************************************
   *
   * Sets the clear photos button text.
   *
   *****************************************************/
  private void setClearPhotosButtonText()
    {
    String buttonText = getString( R.string.kitesdk_image_selection_clear_photos_format_string, getResources().getQuantityString( R.plurals.kitesdk_Photo_plurals, mUncheckedImagesCount, mUncheckedImagesCount ) );

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
   * Animates the fadeable view in.
   *
   *****************************************************/
  private void animateFadeableViewIn()
    {
    if ( mFadeableView != null )
      {
      mFadeableView.setVisibility( View.VISIBLE );

      Animation animation = new AlphaAnimation( 0f, 1f );

      animation.setDuration( PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS );

      mFadeableView.startAnimation( animation );
      }
    }


  /*****************************************************
   *
   * Animates the fadeable view out.
   *
   *****************************************************/
  private void animateFadeableViewOut()
    {
    if ( mFadeableView != null )
      {
      mFadeableView.setVisibility( View.VISIBLE );

      Animation animation = new AlphaAnimation( 1f, 0f );

      animation.setDuration( PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS );
      animation.setFillAfter( true );
      animation.setAnimationListener( new VisibilitySettingAnimationListener( mFadeableView, View.GONE ) );

      mFadeableView.startAnimation( animation );
      }
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

  }

