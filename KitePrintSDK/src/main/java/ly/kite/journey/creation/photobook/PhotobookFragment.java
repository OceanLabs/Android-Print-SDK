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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ly.kite.journey.AImageSource;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.journey.creation.IUpdatedAssetListener;
import ly.kite.catalogue.Asset;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.catalogue.Product;
import ly.kite.R;


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
                                                                           View.OnDragListener,
                                                                           ActionMode.Callback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public  final String  TAG                                             = "PhotobookFragment";

  // Size of auto-scroll zone for drag-and-drop, as a proportion of the list view size.
  static private final float   AUTO_SCROLL_ZONE_SIZE_AS_PROPORTION             = 0.3f;
  static private final float   AUTO_SCROLL_MAX_SPEED_IN_PROPORTION_PER_SECOND  = 0.5f;
  static private final long    AUTO_SCROLL_INTERVAL_MILLIS                     = 10;
  static private final int     AUTO_SCROLL_INTERVAL_MILLIS_AS_INT              = (int)AUTO_SCROLL_INTERVAL_MILLIS;

  static private final int     DISABLED_ALPHA                                  = 100;
  static private final int     ENABLED_ALPHA                                   = 255;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private PhotobookView                mPhotobookView;
  private PhotobookAdaptor mPhotobookAdaptor;

  private Parcelable                   mPhotobookViewState;

  private Menu                         mMenu;

  private int                          mDraggedAssetIndex;

  private Handler                      mHandler;
  private ScrollRunnable               mScrollRunnable;

  private ActionMode                   mActionMode;
  private MenuItem                     mActionModeEditMenuItem;
  private MenuItem                     mActionModeDiscardMenuItem;


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

    mPhotobookView = (PhotobookView)view.findViewById( R.id.recycler_view );
    mPhotobookView.setLayoutManager( new LinearLayoutManager( mKiteActivity ) );

    // Set up the forwards button
    setForwardsButtonText( R.string.Next );
    setForwardsButtonOnClickListener( this );

    // We listen for drag events so that we can auto scroll up or down when dragging
    //mPhotoBookListView.setOnDragListener( this );
    mPhotobookView.setOnDragListener( this );

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
   * Called to find out the maximum number of images we
   * want to select.
   *
   *****************************************************/
  @Override
  protected int getMaxAddImageCount()
    {
    // Limit the number of images selectable, if the image source supports it.

    int maxImages = 1 + mProduct.getQuantityPerSheet() - mAssetsAndQuantityArrayList.size();

    if ( maxImages < 0 ) maxImages = 0;


    return ( maxImages );
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

    if ( mPhotobookView != null )
      {
      // Save the list view state so we can come back to the same position
      mPhotobookViewState = mPhotobookView.onSaveInstanceState();

      mPhotobookView.setAdapter( null );
      }

    mPhotobookView = null;
    }


  /*****************************************************
   *
   * Called when an image is cropped.
   *
   *****************************************************/
  protected void onImageCropped( AssetsAndQuantity assetsAndQuantity )
    {
    // Notify the adaptor that an asset has changed
    if ( mPhotobookAdaptor != null ) mPhotobookAdaptor.notifyDataSetChanged( assetsAndQuantity );
    }


  /*****************************************************
   *
   * Called when all images have been cropped.
   *
   *****************************************************/
  protected void onAllImagesCropped()
    {
    if ( mPhotobookAdaptor != null ) mPhotobookAdaptor.notifyDataSetChanged();
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
    Button proceedButton = getForwardsButton();

    if ( view == proceedButton )
      {
      ///// Checkout /////

      if ( mAssetsAndQuantityArrayList.isEmpty() )
        {
        mKiteActivity.displayModalDialog(R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_images_selected, R.string.OK, null, 0, null);
        }
      else if ( mKiteActivity instanceof ICallback )
        {
        int expectedImageCount = 1 + mProduct.getQuantityPerSheet();
        int actualImageCount   = mAssetsAndQuantityArrayList.size();

        if ( actualImageCount < expectedImageCount )
          {
          displayNotFullDialog( expectedImageCount, actualImageCount, new CallbackNextRunnable() );
          }
        else
          {
          ( (ICallback)mKiteActivity ).pbOnNext();
          }
        }
      }

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
    // Start the action mode

    mActionMode = mKiteActivity.startActionMode( this );

    mPhotobookAdaptor.setSelectionMode( true );

    // Select the image that was clicked
    mPhotobookAdaptor.selectAsset( assetIndex );
    }


  /*****************************************************
   *
   * Called when an image is long clicked.
   *
   *****************************************************/
  @Override
  public void onLongClickImage( int draggedAssetIndex, View view )
    {
    if ( draggedAssetIndex < 0 ) return;

    mDraggedAssetIndex = draggedAssetIndex;

    mPhotobookView.startDrag( null, new View.DragShadowBuilder( view ), null, 0 );
    }


  /*****************************************************
   *
   * Called when the set of selected assets changes.
   *
   *****************************************************/
  @Override
  public void onSelectedAssetsChange( int selectedAssetCount )
    {
    // The edit action is only available when the selected asset count
    // exactly equals 1

    if ( mActionModeEditMenuItem != null )
      {
      if ( selectedAssetCount == 1 )
        {
        mActionModeEditMenuItem.setEnabled( true );
        mActionModeEditMenuItem.getIcon().setAlpha( ENABLED_ALPHA );
        }
      else
        {
        mActionModeEditMenuItem.setEnabled( false );
        mActionModeEditMenuItem.getIcon().setAlpha( DISABLED_ALPHA );
        }
      }


    // The delete action is only available when the selected asset count
    // is greater than 0

    if ( mActionModeDiscardMenuItem != null )
      {
      if ( selectedAssetCount > 0 )
        {
        mActionModeDiscardMenuItem.setEnabled( true );
        mActionModeDiscardMenuItem.getIcon().setAlpha( ENABLED_ALPHA );
        }
      else
        {
        mActionModeDiscardMenuItem.setEnabled( false );
        mActionModeDiscardMenuItem.getIcon().setAlpha( DISABLED_ALPHA );
        }
      }
    }


  ////////// IUpdatedAssetListener Method(s) //////////

  /*****************************************************
   *
   * Updates the assets and quantity.
   *
   *****************************************************/
  public void onAssetUpdated( int assetIndex, AssetsAndQuantity assetsAndQuantity )
    {
    if ( mPhotobookAdaptor != null )
      {
      mPhotobookAdaptor.notifyDataSetChanged();
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


      // Determine which zone the location is in

      int photobookViewHeight = mPhotobookView.getHeight();

      float yTopStart    = photobookViewHeight * AUTO_SCROLL_ZONE_SIZE_AS_PROPORTION;
      float yBottomStart = photobookViewHeight - yTopStart;

      if ( y < yTopStart )
        {
        ///// Top zone /////

        // Calculate the speed and run auto-scroll

        float speedAsProportionPerSecond = ( ( y - yTopStart ) / yTopStart ) * AUTO_SCROLL_MAX_SPEED_IN_PROPORTION_PER_SECOND;

        setAutoScroll( speedAsProportionPerSecond );
        }
      else if ( y <= yBottomStart )
        {
        ///// Middle zone

        stopAutoScroll();
        }
      else
        {
        ///// Bottom zone /////

        // Calculate the speed and run auto-scroll

        float speedAsProportionPerSecond = ( ( y - yBottomStart ) / ( photobookViewHeight - yBottomStart ) ) * AUTO_SCROLL_MAX_SPEED_IN_PROPORTION_PER_SECOND;

        setAutoScroll( speedAsProportionPerSecond );
        }

      return ( true );
      }
    else if ( action == DragEvent.ACTION_DROP )
      {
      // Get the location
      float x = event.getX();
      float y = event.getY();

      onEndDrag( (int)x, (int)y );

      return ( true );
      }


    return ( false );
    }


  ////////// ActionMode.Callback Method(s) //////////

  /*****************************************************
   *
   * Called when action mode is first entered.
   *
   *****************************************************/
  @Override
  public boolean onCreateActionMode( ActionMode mode, Menu menu )
    {
    // Inflate the action mode menu, and get a reference to the edit
    // action, in case more images are selected and we want to disable
    // it.

    MenuInflater inflator = mode.getMenuInflater();

    inflator.inflate( R.menu.photobook_action_mode, menu );

    mActionModeEditMenuItem    = menu.findItem( R.id.edit_menu_item );
    mActionModeDiscardMenuItem = menu.findItem( R.id.discard_menu_item );

    // Can't click next whilst in action mode
    setForwardsButtonEnabled( false );


    return ( true );
    }


  /*****************************************************
   *
   * Called each time the action mode is shown.
   *
   *****************************************************/
  @Override
  public boolean onPrepareActionMode( ActionMode mode, Menu menu )
    {
    return ( false ); // Return false if nothing is done
    }


  /*****************************************************
   *
   * Called when a contextual item is chosen.
   *
   *****************************************************/
  @Override
  public boolean onActionItemClicked( ActionMode mode, MenuItem item )
    {
    int itemId = item.getItemId();


    // Get the selected assets
    HashSet<Asset> selectedEditedAssetHashSet = mPhotobookAdaptor.getSelectedEditedAssets();


    // Determine which action was clicked

    if ( itemId == R.id.edit_menu_item )
      {
      ///// Edit /////

      // Launch the edit screen for the chosen asset. There should be only one selected asset
      // so just grab the first.

      Iterator assetIterator = selectedEditedAssetHashSet.iterator();

      if ( assetIterator.hasNext() )
        {
        Asset selectedEditedAsset = (Asset)assetIterator.next();

        int selectedAssetIndex = findEditedAsset( selectedEditedAsset );

        if ( selectedAssetIndex >= 0 )
          {
          if ( mKiteActivity instanceof ICallback )
            {
            ( (ICallback) mKiteActivity ).pbOnEdit( selectedAssetIndex );
            }
          }
        }

      // Exit action mode
      mode.finish();

      return ( true );
      }

    else if ( itemId == R.id.discard_menu_item )
      {
      ///// Delete /////

      // Delete the selected assets
      for ( Asset selectedEditedAsset : selectedEditedAssetHashSet )
        {
        removeImageForEditedAsset( selectedEditedAsset );
        }

      // Don't update the data. It gets updated when the action mode is destroyed, and we
      // don't want it updating twice in a row.

      // Exit action mode
      mode.finish();

      return ( true );
      }


    return ( false );
    }


  /*****************************************************
   *
   * Called when the user exits action mode.
   *
   *****************************************************/
  @Override
  public void onDestroyActionMode( ActionMode mode )
    {
    mActionMode             = null;
    mActionModeEditMenuItem = null;

    // End the selection mode (and thus update the data)
    mPhotobookAdaptor.setSelectionMode( false );

    setForwardsButtonEnabled( true );
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


    mPhotobookAdaptor = new PhotobookAdaptor( mKiteActivity, mProduct, mAssetsAndQuantityArrayList, this );

    mPhotobookView.setAdapter( mPhotobookAdaptor );


    // Restore any list view state

    if ( mPhotobookViewState != null )
      {
      mPhotobookView.onRestoreInstanceState( mPhotobookViewState );

      mPhotobookViewState = null;
      }
    }


  /*****************************************************
   *
   * Ensures that auto-scroll is running at the supplied
   * speed.
   *
   *****************************************************/
  private void setAutoScroll( float speedAsProportionPerSecond )
    {
    // Make sure we have a handler
    if ( mHandler == null ) mHandler = new Handler();


    // Make sure we have a scroll runnable. The presence of a scroll handler
    // also indicates whether auto-scrolling is actually running. So if we
    // need to create a scroll runnable, we also need to post it.

    if ( mScrollRunnable == null )
      {
      mScrollRunnable = new ScrollRunnable();

      postScrollRunnable();
      }


    // Set the speed
    mScrollRunnable.setSpeed( speedAsProportionPerSecond );
    }


  /*****************************************************
   *
   * Ensures that auto-scroll is not running.
   *
   *****************************************************/
  private void stopAutoScroll()
    {
    if ( mScrollRunnable != null )
      {
      if ( mHandler != null ) mHandler.removeCallbacks( mScrollRunnable );

      mScrollRunnable = null;
      }
    }


  /*****************************************************
   *
   * Posts the scroll runnable.
   *
   *****************************************************/
  private void postScrollRunnable()
    {
    mHandler.postDelayed( mScrollRunnable, 10 );
    }


  /*****************************************************
   *
   * Called when dragging stops.
   *
   *****************************************************/
  private void onEndDrag( int dragLastX, int dragLastY )
    {
    stopAutoScroll();


    // Determine which asset the drag ended on

    int position = mPhotobookView.positionFromPoint( dragLastX, dragLastY );

    int dropAssetIndex = -1;

    if ( position == PhotobookAdaptor.FRONT_COVER_POSITION )
      {
      dropAssetIndex = 0;
      }
    else if ( position >= PhotobookAdaptor.CONTENT_START_POSITION )
      {
      dropAssetIndex = 1 +
                       ( ( position - PhotobookAdaptor.CONTENT_START_POSITION ) * 2 ) +
                       ( dragLastX > ( mPhotobookView.getWidth() / 2 ) ? 1 : 0 );
      }


    if ( dropAssetIndex >= 0 )
      {
      // If the drop ends on an empty image, assume it was dragged to the end

      if ( dropAssetIndex >= mAssetsAndQuantityArrayList.size() )
        {
        dropAssetIndex = mAssetsAndQuantityArrayList.size() - 1;
        }


      // Insert the dragged asset into the drop position, and shift the others
      // out of the way.

      if ( dropAssetIndex != mDraggedAssetIndex )
        {
        AssetsAndQuantity draggedAssetsAndQuantity = mAssetsAndQuantityArrayList.remove( mDraggedAssetIndex );

        mAssetsAndQuantityArrayList.add( dropAssetIndex, draggedAssetsAndQuantity );
        }

      mPhotobookAdaptor.notifyDataSetChanged();
      }

    }


  /*****************************************************
   *
   * Removes an image based on its edited asset.
   *
   *****************************************************/
  private void removeImageForEditedAsset( Asset editedAsset )
    {
    for ( int candidateIndex = 0; candidateIndex < mAssetsAndQuantityArrayList.size(); candidateIndex ++ )
      {
      Asset candidateEditedAsset = mAssetsAndQuantityArrayList.get( candidateIndex ).getEditedAsset();

      if ( candidateEditedAsset == editedAsset )
        {
        mAssetsAndQuantityArrayList.remove( candidateIndex );

        return;
        }
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


  /*****************************************************
   *
   * A runnable for scrolling the list view.
   *
   *****************************************************/
  private class ScrollRunnable implements Runnable
    {
    private float  mSpeedAsProportionPerSecond;
    private long   mLastScrollRealtimeMillis;


    void setSpeed( float speedAsProportionPerSecond )
      {
      mSpeedAsProportionPerSecond = speedAsProportionPerSecond;
      }


    @Override
    public void run()
      {
      // Get the current elapsed time
      long currentRealtimeMillis = SystemClock.elapsedRealtime();


      // Check that the time looks OK

      if ( mLastScrollRealtimeMillis > 0 && currentRealtimeMillis > mLastScrollRealtimeMillis )
        {
        // Calculate the scroll amount

        long elapsedTimeMillis = currentRealtimeMillis - mLastScrollRealtimeMillis;

        int scrollPixels = (int)( ( mPhotobookView.getHeight() * ( (float)elapsedTimeMillis / 1000f ) ) * mSpeedAsProportionPerSecond );

        mPhotobookView.smoothScrollBy( scrollPixels, AUTO_SCROLL_INTERVAL_MILLIS_AS_INT );
        }


      // Save the current time and re-post

      mLastScrollRealtimeMillis = currentRealtimeMillis;

      postScrollRunnable();
      }

    }


  /*****************************************************
   *
   * Calls back to the activity.
   *
   *****************************************************/
  private class CallbackNextRunnable implements Runnable
    {
    @Override
    public void run()
      {
      ( (ICallback)mKiteActivity ).pbOnNext();
      }
    }


  }