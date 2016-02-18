/*****************************************************
 *
 * PhotobookFragment.java
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

package ly.kite.journey.creation.photobook;


///// Import(s) /////

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.journey.AImageSource;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.catalogue.Asset;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.catalogue.Product;

import ly.kite.R;
import ly.kite.journey.creation.IUpdatedAssetListener;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a screen that allows users to
 * create a photobook.
 *
 *****************************************************/
public class PhotobookFragment extends AProductCreationFragment implements PhotobookAdaptor.IListener,
                                                                           View.OnClickListener,
                                                                           AImageSource.IAssetConsumer,
                                                                           IUpdatedAssetListener,
                                                                           View.OnDragListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String      TAG                                             = "PhotobookFragment";

  private static final long        PROCEED_BUTTON_BUTTON_ANIMATION_DURATION_MILLIS = 300L;

  private static final int         PROGRESS_COMPLETE                               = 100;  // 100%


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private int                          mInitialUneditedAssetsCount;
  private int                          mUneditedAssetsRemaining;

  private PhotobookAdaptor             mPhotoBookAdaptor;
  private ListView                     mPhotoBookListView;

  private Menu                         mMenu;

  private Parcelable                   mListViewState;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static PhotobookFragment newInstance( Product product )
    {
    PhotobookFragment fragment = new PhotobookFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AProductCreationFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    setHasOptionsMenu( true );
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_photobook, container, false );

    super.onViewCreated( view );

    mPhotoBookListView = (ListView)view.findViewById( R.id.list_view );


    // Set up the listener(s)
    if ( mProceedOverlayButton != null ) mProceedOverlayButton.setOnClickListener( this );

    // We listen for drag events so that we can auto scroll up or down when dragging
    mPhotoBookListView.setOnDragListener( this );


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

    // Make sure we have cropped versions of all assets
    requestCroppedAssets();
    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    super.onCreateOptionsMenu( menu, menuInflator, R.menu.photobook );

    mMenu = menu;
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // Limit the number of images selectable, if the image source supports it.

    int maxImages = 1 + mProduct.getQuantityPerSheet() - mAssetsAndQuantityArrayList.size();

    if ( maxImages < 0 ) maxImages = 0;


    return ( super.onOptionsItemSelected( item, maxImages ) );
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


    mKiteActivity.setTitle( R.string.title_photobook );

    setUpListView();
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

    if ( mPhotoBookListView != null )
      {
      // Save the list view state so we can come back to the same position
      mListViewState = mPhotoBookListView.onSaveInstanceState();

      mPhotoBookListView.setAdapter( null );
      }

    mPhotoBookAdaptor = null;
    }


  /*****************************************************
   *
   * Called when an image is cropped.
   *
   *****************************************************/
  protected void onImageCropped( AssetsAndQuantity assetsAndQuantity )
    {
    // Notify the adaptor that an asset has changed
    //if ( mPhotoBookAdaptor != null ) mPhotoBookAdaptor.notifyDataSetChanged( assetsAndQuantity );
    }


  /*****************************************************
   *
   * Called when all images have been cropped.
   *
   *****************************************************/
  protected void onAllImagesCropped()
    {
    if ( mPhotoBookAdaptor != null ) mPhotoBookAdaptor.notifyDataSetChanged();
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
//    if ( view == mClearPhotosButton )
//      {
//      ///// Clear photos /////
//
//      // We need to go through all the assets and remove any that are unchecked - from
//      // both lists, and the "is checked" value.
//
//      for ( int assetIndex = 0; assetIndex < mAssetsAndQuantityArrayList.size(); assetIndex ++ )
//        {
//        if ( ! mAssetIsCheckedArrayList.get( assetIndex ) )
//          {
//          mAssetsAndQuantityArrayList.remove( assetIndex );
//          mAssetIsCheckedArrayList.remove( assetIndex );
//
//          // If we delete an asset, then the next asset now falls into its place
//          assetIndex --;
//          }
//        }
//
//      mUncheckedImagesCount = 0;
//
//
//      // Update the screen
//
//      setTitle();
//
//      animateClearPhotosButtonOut();
//      animateProceedOverlayButtonIn();
//
//      setUpListView();
//      }
//    else if ( view == mProceedOverlayButton )
//      {
//      ///// Review and Crop /////
//
//      if ( mAssetsAndQuantityArrayList.isEmpty() )
//        {
//        mKiteActivity.displayModalDialog(R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_images_selected, R.string.OK, null, 0, null);
//        }
//      else if ( mKiteActivity instanceof ICallback )
//        {
//        ( (ICallback)mKiteActivity ).isOnNext();
//        }
//      }
//
    }


  ////////// PhotobookAdaptor.IListener Method(s) //////////

  /*****************************************************
   *
   * Called to add an image.
   *
   *****************************************************/
  @Override
  public void onAddImage()
    {
    if ( mMenu != null ) mMenu.performIdentifierAction( R.id.add_photo_menu_item, 0 );
    }


  /*****************************************************
   *
   * Called when an image is clicked.
   *
   *****************************************************/
  @Override
  public void onClickImage( int assetIndex )
    {
    // Launch the edit screen for the chosen asset

    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pbOnEdit( assetIndex );
      }
    }


  /*****************************************************
   *
   * Called when an image is long clicked.
   *
   *****************************************************/
  @Override
  public void onLongClickImage( int assetIndex )
    {
    }


  ////////// IUpdatedAssetListener Method(s) //////////

  /*****************************************************
   *
   * Updates the assets and quantity.
   *
   *****************************************************/
  public void onAssetUpdated( int assetIndex, AssetsAndQuantity assetsAndQuantity )
    {
    if ( mPhotoBookAdaptor != null )
      {
      mPhotoBookAdaptor.notifyDataSetChanged();
      }
    }


  ////////// View.OnDragListener Method(s) //////////

  /*****************************************************
   *
   * Called when there is a drag event.
   *
   *****************************************************/
  @Override
  public boolean onDrag( View view, DragEvent event )
    {
    // We only respond to start and location events

    int action = event.getAction();

    if ( action == DragEvent.ACTION_DRAG_STARTED )
      {
      return ( true );
      }
    else if ( action == DragEvent.ACTION_DRAG_LOCATION )
      {
      // Get the location
      float x = event.getX();
      float y = event.getY();

      Log.d( TAG, "x = " + x + ", y = " + y );

      return ( true );
      }

    return ( false );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets up the list view.
   *
   *****************************************************/
  private void setUpListView()
    {
    // Limit the number of assets to the total amount required for a
    // photobook, i.e. front cover + content pages.

    int maxAssetCount = 1 + mProduct.getQuantityPerSheet();

    while ( mAssetsAndQuantityArrayList.size() > maxAssetCount )
      {
      mAssetsAndQuantityArrayList.remove( mAssetsAndQuantityArrayList.size() - 1 );
      }


    mPhotoBookAdaptor = new PhotobookAdaptor( mKiteActivity, mProduct, mAssetsAndQuantityArrayList, this );

    mPhotoBookListView.setAdapter( mPhotoBookAdaptor );


    // Restore any list view state

    if ( mListViewState != null )
      {
      mPhotoBookListView.onRestoreInstanceState( mListViewState );

      mListViewState = null;
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
    public void pbOnEdit( int assetIndex );
    public void pbOnNext();
    }

  }

