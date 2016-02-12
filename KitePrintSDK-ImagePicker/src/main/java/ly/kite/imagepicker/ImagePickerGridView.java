/*****************************************************
 *
 * ImagePickerGridView.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.imagepicker;


///// Import(s) /////

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a grid view for displaying a hierarchy
 * of selectable images.
 *
 *****************************************************/
public class ImagePickerGridView extends RecyclerView implements ImagePickerGridViewAdaptor.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                            = "ImagePickerGridView";

  static private final String  BUNDLE_KEY_PARENT_STATE            = "parentState";
  static private final String  BUNDLE_KEY_SELECTED_URL_STRINGS    = "selectedURLStrings";
  static private final String  BUNDLE_KEY_CURRENT_LEVEL           = "currentLevel";
  static private final String  BUNDLE_KEY_PARENT_KEY_STACK        = "parentKeyStack";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private LinkedHashSet<String>            mSelectedURLStrings;
  private int                              mCurrentLevel;
  private ArrayList<String>                mParentKeyStack;

  private boolean                          mIsLoading;
  private boolean                          mHasMoreItems;
  private ICallback                        mPhotoSource;

  private ImagePickerGridViewAdaptor       mPhotoGridAdaptor;
  private RecyclerView.LayoutManager       mGridLayoutManager;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ImagePickerGridView( Context context )
    {
    super( context );

    initialise( context );
    }

  public ImagePickerGridView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public ImagePickerGridView( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );

    initialise( context );
    }


  ////////// RecyclerView Method(s) //////////

  /*****************************************************
   *
   * Saves the state.
   *
   *****************************************************/
  @Override
  protected Parcelable onSaveInstanceState()
    {
    Parcelable parentStateParcelable = super.onSaveInstanceState();

    Bundle outState = new Bundle();

    outState.putParcelable( BUNDLE_KEY_PARENT_STATE, parentStateParcelable );

    outState.putSerializable( BUNDLE_KEY_SELECTED_URL_STRINGS, mSelectedURLStrings );
    outState.putInt( BUNDLE_KEY_CURRENT_LEVEL, mCurrentLevel );
    outState.putStringArrayList( BUNDLE_KEY_PARENT_KEY_STACK, mParentKeyStack );

    return ( outState );
    }


  /*****************************************************
   *
   * Restores the state.
   *
   *****************************************************/
  @Override
  protected void onRestoreInstanceState( Parcelable stateParcelable )
    {
    Bundle state = (Bundle)stateParcelable;


    // Restore any selected items

    LinkedHashSet<String> selectedURLStrings = (LinkedHashSet<String>)state.getSerializable( BUNDLE_KEY_SELECTED_URL_STRINGS );

    if ( selectedURLStrings != null )
      {
      mSelectedURLStrings = selectedURLStrings;

      mPhotoGridAdaptor.setSelectedURLStrings( selectedURLStrings );
      }


    // Restore any level

    int currentLevel = state.getInt( BUNDLE_KEY_CURRENT_LEVEL, -1 );

    if ( currentLevel >= 0 ) mCurrentLevel = currentLevel;


    // Restore any parent item stack

    ArrayList<String> parentKeyStack = state.getStringArrayList( BUNDLE_KEY_PARENT_KEY_STACK );

    if ( parentKeyStack != null ) mParentKeyStack = parentKeyStack;


    // Restore the parent state
    super.onRestoreInstanceState( state.getParcelable( BUNDLE_KEY_PARENT_STATE ) );
    }


  ////////// PhotoGridViewAdaptor.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a parent item is clicked.
   *
   *****************************************************/
  @Override
  public void onDescendLevel( String parentKey )
    {
    mCurrentLevel ++;
    mParentKeyStack.add( parentKey );

    // Clear the displayed items
    mPhotoGridAdaptor.clearAllItems();

    scrollToPosition( 0 );

    // Call back to the photo source
    if ( mPhotoSource != null )
      {
      mPhotoSource.onSetLevel( mCurrentLevel, parentKey );
      }
    }


  /*****************************************************
   *
   * Called when the number of selected items changes.
   *
   *****************************************************/
  @Override
  public void onSelectedCountChanged( int oldCount, int newCount )
    {
    if ( mPhotoSource != null ) mPhotoSource.onSelectedCountChanged( oldCount, newCount );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    mSelectedURLStrings = new LinkedHashSet<>();
    mParentKeyStack     = new ArrayList<>();


    // Set up the adaptor

    mPhotoGridAdaptor  = new ImagePickerGridViewAdaptor( context, mSelectedURLStrings, this );

    setAdapter( mPhotoGridAdaptor );


    // Set up the layout manager

    mGridLayoutManager = new GridLayoutManager( context, context.getResources().getInteger( R.integer.ip_grid_columns ) );

    setLayoutManager( mGridLayoutManager );


    mIsLoading = false;

    setHasMoreItems( true );

    addOnScrollListener( new ScrollListener() );
    }


  /*****************************************************
   *
   * Ascends a level.
   *
   * @return true, if the level was ascended (because we
   *         were already at a lower level.
   * @return false, if we are already at the highest level
   *         and thus cannot ascend any further.
   *
   *****************************************************/
  public boolean onAscendLevel()
    {
    if ( mCurrentLevel > 0 )
      {
      mCurrentLevel --;

      String parentKey;

      if ( mParentKeyStack.size() > 0 )
        {
        parentKey = mParentKeyStack.remove( mCurrentLevel );
        }
      else
        {
        parentKey = null;
        }

      // Clear the displayed items
      mPhotoGridAdaptor.clearAllItems();

      scrollToPosition( 0 );

      // Call back to the photo source
      if ( mPhotoSource != null )
        {
        mPhotoSource.onSetLevel( mCurrentLevel, parentKey );
        }

      return ( true );
      }


    return ( false );
    }


  /*****************************************************
   *
   * Returns the current level.
   *
   *****************************************************/
  public int getCurrentLevel()
    {
    return ( mCurrentLevel );
    }


  /*****************************************************
   *
   * Returns the current parent key.
   *
   *****************************************************/
  public String getCurrentParentKey()
    {
    if ( mCurrentLevel > 0 ) return ( mParentKeyStack.get( mCurrentLevel - 1 ) );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the number of selected items.
   *
   *****************************************************/
  public int getSelectedCount()
    {
    return ( mSelectedURLStrings.size() );
    }


  /*****************************************************
   *
   * Returns true if more items are being loaded, false
   * otherwise.
   *
   *****************************************************/
  public boolean isLoading()
    {
    return ( mIsLoading );
    }


  /*****************************************************
   *
   * Sets the is loading state.
   *
   *****************************************************/
  public void setIsLoading( boolean isLoading )
    {
    mIsLoading = isLoading;
    }


  /*****************************************************
   *
   * Sets the maximum number of selectable images.
   *
   *****************************************************/
  public void setMaxImageCount( int maxImageCount )
    {
    mPhotoGridAdaptor.setMaxImageCount( maxImageCount );
    }


  /*****************************************************
   *
   * Sets the photo source.
   *
   *****************************************************/
  public void setCallback( ICallback photoSource )
    {
    mPhotoSource = photoSource;
    }


  /*****************************************************
   *
   * Sets whether there are more items at the current level.
   *
   *****************************************************/
  public void setHasMoreItems( boolean hasMoreItems )
    {
    mHasMoreItems = hasMoreItems;

    mPhotoGridAdaptor.setLoadingViewVisible( hasMoreItems );
    }


  /*****************************************************
   *
   * Returns whether there are more items at the current
   * level.
   *
   *****************************************************/
  public boolean hasMoreItems()
    {
    return ( mHasMoreItems );
    }


  /*****************************************************
   *
   * Called when more grid items have been loaded.
   *
   *****************************************************/
  public void onFinishedLoading( Collection<? extends IImagePickerItem> newItems, boolean hasMoreItems )
    {
    setHasMoreItems( hasMoreItems );

    setIsLoading( false );

    if ( newItems != null && newItems.size() > 0 )
      {
      mPhotoGridAdaptor.addMoreItems( newItems );
      }
    }


  /*****************************************************
   *
   * Returns the set of selected image URLs.
   *
   *****************************************************/
  public HashSet<String> getSelectedURLStringSet()
    {
    return ( mSelectedURLStrings );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An agent responsible for sourcing photos.
   *
   *****************************************************/
  public interface ICallback
    {
    public void onSetLevel( int level, String parentKey );
    public void onLoadMoreImages();
    public void onSelectedCountChanged( int oldCount, int newCount );
    }


  /*****************************************************
   *
   * ...
   *
   *****************************************************/
  private class ScrollListener extends RecyclerView.OnScrollListener
    {

    @Override
    public void onScrollStateChanged( RecyclerView recyclerView, int scrollState )
      {
      // Ignore
      }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy )
      {
      // TODO
//      if ( totalItemCount > 0 )
//        {
//        int lastVisibleItem = firstVisibleItem + visibleItemCount;
//
//        if ( ! mIsLoading && mHasMoreItems && ( lastVisibleItem == totalItemCount ) )
//          {
//          if ( mPhotoSource != null )
//            {
//            mIsLoading = true;
//
//            mPhotoSource.onLoadMoreImages();
//            }
//
//          }
//        }
      }

    }

  }