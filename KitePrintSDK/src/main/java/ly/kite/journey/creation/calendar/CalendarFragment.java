/*****************************************************
 *
 * CalendarFragment.java
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

package ly.kite.journey.creation.calendar;


///// Import(s) /////

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ly.kite.journey.AImageSource;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.journey.creation.IUpdatedImageListener;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.catalogue.Product;
import ly.kite.R;
import ly.kite.util.ViewToBitmap;
import ly.kite.widget.ExtendedRecyclerView;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a screen that allows users to
 * create a calendar.
 *
 *****************************************************/
public class CalendarFragment extends AProductCreationFragment implements CalendarAdaptor.IListener,
                                                                          View.OnClickListener,
                                                                          AImageSource.IAssetConsumer,
                                                                          IUpdatedImageListener,
                                                                          View.OnDragListener,
                                                                          ActionMode.Callback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public  final String  TAG             = "CalendarFragment";

  static         final int     MONTHS_PER_YEAR = 12;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ExtendedRecyclerView  mCalendarView;
  private CalendarAdaptor       mCalendarAdaptor;

  private int                   mAddImageIndex;

  private int                   mDraggedImageIndex;

  private MenuItem              mActionModeEditMenuItem;
  private MenuItem              mActionModeDiscardMenuItem;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static CalendarFragment newInstance( Product product )
    {
    CalendarFragment fragment = new CalendarFragment();

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
    View view = layoutInflator.inflate( R.layout.screen_calendar, container, false );

    super.onViewCreated( view );

    mCalendarView = (ExtendedRecyclerView) view.findViewById( R.id.calendar_view );
    mCalendarView.setLayoutManager( new LinearLayoutManager( mKiteActivity ) );

    // Set up the forwards button
    setForwardsTextViewText( R.string.Next );
    setForwardsTextViewOnClickListener( this );

    mCalendarView.setOnDragListener( this );

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
   * Called to find out the number of images we
   * already selected
   *
   *****************************************************/
  @Override
  protected int getNumberOfImagesUsed()
    {
    return ( getTotalImagesUsedCount() );
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
    return ( getRemainingImageCapacity( mAddImageIndex ) );
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


    mKiteActivity.setTitle( R.string.title_calendar );

    setUpCalendarView();
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

    suspendView( mCalendarView );
    }


  /*****************************************************
   *
   * Called to crop any assets that haven't already been.
   *
   *****************************************************/
  @Override
  protected boolean requestCroppedAssets()
    {
    // Before we crop assets, make sure that the asset list
    // is the correct size.
    enforceAssetListSize( MONTHS_PER_YEAR * mProduct.getGridCountX() * mProduct.getGridCountY() );

    return ( super.requestCroppedAssets() );
    }


  /*****************************************************
   *
   * Called when all images have been cropped.
   *
   *****************************************************/
  protected void onAllImagesCropped()
    {
    if ( mCalendarAdaptor != null ) mCalendarAdaptor.notifyDataSetChanged();

    onScreenReady();
    }


  /*****************************************************
   *
   * Adds new unedited assets to the end of the current list.
   * Duplicates will be discarded.
   *
   *****************************************************/
  @Override
  protected void onAddAssets( List<Asset> newAssetList )
    {
    // We intercept this call so that we can call the version
    // that inserts new assets into a sparse list instead.
    super.onAddAssets( newAssetList, mAddImageIndex, false );
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
    TextView proceedTextView = getForwardsTextView();

    if ( view == proceedTextView )
      {
      ///// Checkout /////

      if ( mImageSpecArrayList.isEmpty() )
        {
        mKiteActivity.displayModalDialog(R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_images_selected, R.string.OK, null, 0, null);
        }
      else if ( mKiteActivity instanceof ICallback )
        {
        int expectedImageCount = mProduct.getQuantityPerSheet();


        // Pages can be blank, so to calculate the actual number of images we need to go through
        // them all.

        int actualImageCount = 0;

        for ( ImageSpec imageSpec : mImageSpecArrayList )
          {
          if ( imageSpec != null ) actualImageCount ++;
          }


        if ( actualImageCount < expectedImageCount )
          {
          displayNotFullDialog( expectedImageCount, actualImageCount, new CallbackNextRunnable() );
          }
        else
          {
          // Get the first view (January page) as preview image
          Bitmap originalBitmap = ViewToBitmap.getItemBitmap( mCalendarView, 0 );
          Bitmap cleanBitmap = ViewToBitmap.removeBitmapBlankSpace(originalBitmap);
          Bitmap bitmap = ViewToBitmap.resizeAsPreviewImage( mKiteActivity, cleanBitmap );
          //Store preview in the first non-null imageSpec element
          for( int index = 0; index < mImageSpecArrayList.size(); index++ )
            {
            if (mImageSpecArrayList.get( index ) != null )
              {
              mImageSpecArrayList.get( index ).setPreviewImage( bitmap );
              break;
              }
            }

          ( (ICallback)mKiteActivity ).calOnNext();
          }
        }
      }

    }


  ////////// CalendarAdaptor.IListener Method(s) //////////

  /*****************************************************
   *
   * Called when an image is clicked.
   *
   *****************************************************/
  @Override
  public void onClickImage( int imageIndex, View view )
    {
    // Determine whether the click is on a filled, or blank, space.
    if ( mImageSpecArrayList.get( imageIndex) != null )
      {
      ///// Filled space /////

      // Start the action mode

      mKiteActivity.startActionMode( this );

      mCalendarAdaptor.setSelectionMode( true );

      // Select the image that was clicked
      mCalendarAdaptor.selectImage( imageIndex );
      }
    else
      {
      ///// Blank space /////

      mAddImageIndex = imageIndex;

      onAddImage( view );
      }
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

    mDraggedImageIndex = draggedAssetIndex;

    mCalendarView.startDrag( null, new View.DragShadowBuilder( view ), null, 0 );
    }


  /*****************************************************
   *
   * Called when the set of selected assets changes.
   *
   *****************************************************/
  @Override
  public void onSelectedImagesChanged( int selectedAssetCount )
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
  public void onImageUpdated( int imageIndex, ImageSpec imageSpec )
    {
    if ( mCalendarAdaptor != null )
      {
      mCalendarAdaptor.notifyDataSetChanged();
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


      // Get the asset that we are currently dragged over
      int currentAssetIndex = imageIndexFromPoint( (int)x, (int)y );

      // We only highlight the target image if it is different from the dragged one
      if ( currentAssetIndex >= 0 && currentAssetIndex != mDraggedImageIndex )
        {
        mCalendarAdaptor.setHighlightedAsset( currentAssetIndex );
        }
      else
        {
        mCalendarAdaptor.clearHighlightedAsset();
        }

      // Check if we need to auto-scroll
      checkAutoScroll( mCalendarView, x, y );

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

    inflator.inflate( R.menu.calendar_action_mode, menu );

    mActionModeEditMenuItem    = menu.findItem( R.id.edit_menu_item );
    mActionModeDiscardMenuItem = menu.findItem( R.id.discard_menu_item );

    // Can't click next whilst in action mode
    setForwardsTextViewEnabled( false );


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
    HashSet<Integer> selectedAssetIndexHashSet = mCalendarAdaptor.getSelectedAssets();


    // Determine which action was clicked

    if ( itemId == R.id.edit_menu_item )
      {
      ///// Edit /////

      // Launch the edit screen for the chosen asset. There should be only one selected asset
      // so just grab the first.

      Iterator<Integer> assetIndexIterator = selectedAssetIndexHashSet.iterator();

      if ( assetIndexIterator.hasNext() )
        {
        int selectedAssetIndex = assetIndexIterator.next();

        if ( selectedAssetIndex >= 0 )
          {
          if ( mKiteActivity instanceof ICallback )
            {
            ( (ICallback) mKiteActivity ).calOnEdit( selectedAssetIndex );
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

      // Delete the selected assets, leaving blank pages in their stead
      for ( int selectedAssetIndex : selectedAssetIndexHashSet )
        {
        mImageSpecArrayList.set( selectedAssetIndex, null );
        }

      mCalendarAdaptor.notifyDataSetChanged();

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
    mActionModeEditMenuItem = null;

    // End the selection mode
    mCalendarAdaptor.setSelectionMode( false );

    setForwardsTextViewEnabled( true );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets up the calendar view.
   *
   *****************************************************/
  private void setUpCalendarView()
    {
    mCalendarAdaptor = new CalendarAdaptor( mKiteActivity, mProduct, mImageSpecArrayList, this );

    mCalendarView.setAdapter( mCalendarAdaptor );


    restoreView( mCalendarView );
    }


  /*****************************************************
   *
   * Called when dragging stops.
   *
   *****************************************************/
  private void onEndDrag( int dropX, int dropY )
    {
    stopAutoScroll();

    mCalendarAdaptor.clearHighlightedAsset();

    // Determine which asset the drag ended on
    int dropImageIndex = imageIndexFromPoint( dropX, dropY );

    if ( dropImageIndex >= 0 )
      {
      // Make sure we haven't dropped the image back on itself

      if ( dropImageIndex != mDraggedImageIndex )
        {
        // Simply swap the two positions

        ImageSpec draggedImageSpec = mImageSpecArrayList.get( mDraggedImageIndex );
        ImageSpec dropImageSpec    = mImageSpecArrayList.get( dropImageIndex );

        mImageSpecArrayList.set( dropImageIndex,     draggedImageSpec );
        mImageSpecArrayList.set( mDraggedImageIndex, dropImageSpec );


        mCalendarAdaptor.notifyDataSetChanged();
        }
      }

    }


  /*****************************************************
   *
   * Called when dragging stops.
   *
   *****************************************************/
  private int imageIndexFromPoint( int x, int y )
    {
    int  monthIndex = mCalendarView.positionFromPoint( x, y );
    View pageView   = mCalendarView.findChildViewUnder( x, y );


    // Convert the point into an image / asset index. Remember that the grid
    // is a square at the top of the page view.

    if ( monthIndex >= 0 && monthIndex < MONTHS_PER_YEAR && pageView != null )
      {
      int gridCountX    = mProduct.getGridCountX();
      int gridCountY    = mProduct.getGridCountY();
      int gridSize      = pageView.getWidth() / gridCountX;

      int gridX = ( x - pageView.getLeft() ) / gridSize;
      int gridY = ( y - pageView.getTop()  ) / gridSize;

      if ( gridX >= 0 && gridX < gridCountX && gridY >= 0 && gridY < gridCountY )
        {
        return ( ( monthIndex * gridCountX * gridCountY ) +
                 ( gridY * gridCountX ) +
                   gridX );
        }
      }


    return ( -1 );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void calOnEdit( int assetIndex );
    public void calOnNext();
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
      // Get the first view (January page) as preview image
      Bitmap originalBitmap = ViewToBitmap.getItemBitmap( mCalendarView, 0 );
      Bitmap cleanBitmap = ViewToBitmap.removeBitmapBlankSpace(originalBitmap);
      Bitmap bitmap = ViewToBitmap.resizeAsPreviewImage( mKiteActivity, cleanBitmap );
      //Store preview in the first non-null imageSpec element
      for( int index = 0; index < mImageSpecArrayList.size(); index++ )
        {
        if (mImageSpecArrayList.get( index ) != null )
          {
          mImageSpecArrayList.get( index ).setPreviewImage( bitmap );
          break;
          }
        }
      ( (ICallback)mKiteActivity ).calOnNext();
      }
    }

  }