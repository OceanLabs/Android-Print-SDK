/*****************************************************
 *
 * ImageSelectionAdaptor.java
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

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.journey.UserJourneyType;
import ly.kite.ordering.ImageSpec;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.widget.CheckableImageContainerFrame;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This is the adaptor for the image selection recycler
 * view.
 *
 *****************************************************/
public class ImageSelectionAdaptor extends RecyclerView.Adapter<ImageSelectionAdaptor.ViewHolder>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String   LOG_TAG               = "ImageSelectionAdaptor";

  static private final int      VIEW_TYPE_IMAGE       = 0x00;
  static private final int      VIEW_TYPE_TITLE       = 0x01;
  static private final int      VIEW_TYPE_SPACER      = 0x02;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                      mContext;
  private Product                      mProduct;
  private List<Boolean>                mSharedAssetIsCheckedList;
  private int                          mNumberOfColumns;
  private UserJourneyType              mUserJourneyType;
  private IOnImageCheckChangeListener  mListener;

  private int                          mScaledImageWidthInPixels;
  private LayoutInflater               mLayoutInflator;
  private int                          mPlaceholderBackgroundColour1;
  private int                          mPlaceholderBackgroundColour2;

  private int                          mImagesPerPack;
  private int                          mItemsPerPack;
  private int                          mCurrentGridStartIndex;
  private int                          mAssetCount;
  private int                          mPackCount;
  private List<Item>                   mItemList;
  private int                          mFilledItemCount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  static private String viewTypeStringFromInt( int viewTypeInt )
    {
    switch ( viewTypeInt )
      {
      case VIEW_TYPE_IMAGE:  return ( "VIEW_TYPE_IMAGE" );
      case VIEW_TYPE_TITLE:  return ( "VIEW_TYPE_TITLE" );
      case VIEW_TYPE_SPACER: return ( "VIEW_TYPE_SPACER" );
      }

    return ( String.valueOf( viewTypeInt ) );
    }


  ////////// Constructor(s) //////////

  public ImageSelectionAdaptor( Context context, Product product, List<ImageSpec> imageSpecList, List<Boolean> sharedAssetIsCheckedList, int numberOfColumns, IOnImageCheckChangeListener listener )
    {
    mContext                  = context;
    mProduct                  = product;
    mSharedAssetIsCheckedList = sharedAssetIsCheckedList;
    mNumberOfColumns          = numberOfColumns;
    mUserJourneyType          = product.getUserJourneyType();
    mListener                 = listener;

    mLayoutInflator           = LayoutInflater.from( context );

    Resources resources = context.getResources();

    mScaledImageWidthInPixels     = resources.getDimensionPixelSize( R.dimen.image_selection_thumbnail_size );

    mPlaceholderBackgroundColour1 = resources.getColor( R.color.image_selection_placeholder_background_1 );
    mPlaceholderBackgroundColour2 = resources.getColor( R.color.image_selection_placeholder_background_2 );


    // All the titles, images, and placeholders need to go into a flat list.

    mImagesPerPack         = product.getQuantityPerSheet();
    mItemsPerPack          = mImagesPerPack + 1;  // title + images
    mCurrentGridStartIndex = 1;
    mAssetCount            = 0;
    mItemList              = new ArrayList<>();
    mFilledItemCount       = 0;

    // We always have at least one pack (even if it's empty)
    addPack();

    // Add any assets
    for ( ImageSpec assetsAndQuantity : imageSpecList )
      {
      addAsset( assetsAndQuantity );
      }
    }


  ////////// RecyclerView.Adapter Method(s) //////////

  /*****************************************************
   *
   * Returns a count of the number of items.
   *
   *****************************************************/
  @Override
  public int getItemCount()
    {
    return ( mItemList.size() );
    }


  /*****************************************************
   *
   * Returns the view type.
   *
   *****************************************************/
  @Override
  public int getItemViewType( int position )
    {
    Item item = mItemList.get( position );

    switch ( item.itemType )
      {
      case TITLE:
        return ( VIEW_TYPE_TITLE );

      case IMAGE:
        // Fall through
      case PLACEHOLDER:
        return ( VIEW_TYPE_IMAGE );

      case SPACER:
      default:
        return ( VIEW_TYPE_SPACER );
      }
    }


  /*****************************************************
   *
   * Creates a view holder for the specified view type.
   *
   *****************************************************/
  @Override
  public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
    if ( KiteSDK.DEBUG_IMAGE_SELECTION_SCREEN )
      {
      Log.d( LOG_TAG, "onCreateViewHolder( parent = " + parent + ", viewType = " + viewTypeStringFromInt( viewType ) );
      }


    // Inflate the correct layout

    View view;

    if ( viewType == VIEW_TYPE_TITLE )
      {
      view = mLayoutInflator.inflate( R.layout.recycler_item_title, parent, false );
      }
    else if ( viewType == VIEW_TYPE_IMAGE )
      {
      view = mLayoutInflator.inflate( R.layout.recycler_item_image, parent, false );
      }
    else
      {
      view = mLayoutInflator.inflate( R.layout.recycler_item_proceed_frame_spacer, parent, false );
      }

    ViewHolder viewHolder = new ViewHolder( view );


    return ( viewHolder );
    }


  /*****************************************************
   *
   * Binds the supplied holder to the supplied position,
   * i.e. uses the view references to set the view according
   * to the data at that position.
   *
   *****************************************************/
  @Override
  public void onBindViewHolder( ViewHolder viewHolder, int position )
    {
    if ( KiteSDK.DEBUG_IMAGE_SELECTION_SCREEN )
      {
      Log.d( LOG_TAG, "onBindViewHolder( viewHolder = " + viewHolder + ", position = " + position );
      }


    // Get the item at this position
    Item item = mItemList.get( position );

    // Attach the view holder to the item
    item.viewHolder = viewHolder;


    // Determine what type of item it is

    if ( KiteSDK.DEBUG_IMAGE_SELECTION_SCREEN ) Log.d( LOG_TAG, "  item.itemType = " + item.itemType );

    switch ( item.itemType )
      {
      case TITLE:

        // Set the title
        viewHolder.titleTextView.setText( item.title );

        break;

      case IMAGE:

        AssetFragment assetFragment = item.imageSpec.getAssetFragment();

        if ( KiteSDK.DEBUG_IMAGE_SELECTION_SCREEN ) Log.d( LOG_TAG, "  assetFragment = " + assetFragment );


        // If have got an edited asset - request the image once the view
        // has been sized.

        viewHolder.checkableImageView.setBackgroundColor( mPlaceholderBackgroundColour1 );

        if ( assetFragment != null )
          {
          viewHolder.checkableImageView.requestScaledImageOnceSized( assetFragment );
          }
        else
          {
          viewHolder.checkableImageView.clear();
          }


        // See if the image is checked
        viewHolder.checkableImageView.setChecked( mSharedAssetIsCheckedList.get( item.assetIndex ) );

        // Set the item as the click listener
        viewHolder.checkableImageView.setOnClickListener( item );

        break;

      case PLACEHOLDER:

        // Clear any image that is currently set, then set the background colour according
        // to the position: we alternate the colour to produce a checkerboard effect.

        viewHolder.checkableImageView.clear();

        viewHolder.checkableImageView.setBackgroundColor( ( item.checkerBoardValue & 0x01 ) == 0 ? mPlaceholderBackgroundColour1 : mPlaceholderBackgroundColour2 );
        viewHolder.checkableImageView.setChecked( false );

        // Clear any click listener
        viewHolder.checkableImageView.setOnClickListener( null );

        break;


      case SPACER:

        // We don't need to do anything for the spacer
      }

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Puts an item into the item list at the specified
   * position. If the position is already occupied, the
   * existing item will be replaced. Otherwise the new item
   * is added to the end.
   *
   *****************************************************/
  private void putItem( int index, Item item )
    {
    if ( index < mItemList.size() )
      {
      mItemList.set( index, item );

      notifyItemChanged( index );
      }
    else
      {
      mItemList.add( item );

      notifyItemInserted( mItemList.size() - 1 );
      }
    }


  /*****************************************************
   *
   * Returns the position of the ImageSpec object
   * for use with various adapter change notification methods
   *
   *****************************************************/
  public int positionOf( ImageSpec assetsAndQuantity )
    {
    // Check that we actually have an item list
    if ( mItemList == null ) return ( -1 );

    for ( int candidateItemIndex = 0; candidateItemIndex < mItemList.size(); candidateItemIndex ++ )
      {
      Item candidateItem = mItemList.get( candidateItemIndex );

      if ( candidateItem.imageSpec == assetsAndQuantity )
        {
        return ( candidateItemIndex );
        }
      }

    return ( -1 );
    }


  /*****************************************************
   *
   * Adds an asset. This is used both by our constructor,
   * and by the fragment. We need to be able to add assets
   * without necessarily re-building the entire item list
   * from scratch.
   *
   *****************************************************/
  public void addAsset( ImageSpec assetsAndQuantity )
    {
    // See if we need to add a new pack
    if ( ( mFilledItemCount % mItemsPerPack ) == 0 )
      {
      addPack();
      }

    // Add the asset; replace any previous placeholder.
    putItem( mFilledItemCount, new Item( mAssetCount, assetsAndQuantity ) );

    mAssetCount ++;
    mFilledItemCount ++;
    }


  /*****************************************************
   *
   * Returns the number of assets.
   *
   *****************************************************/
  public int getAssetCount()
    {
    return ( mAssetCount );
    }


  /*****************************************************
   *
   * Adds a pack.
   *
   *****************************************************/
  private void addPack()
    {
    mPackCount ++;


    // Add the title

    String title = mContext.getString( R.string.kitesdk_image_selection_pack_title_format_string, mPackCount, mImagesPerPack, mProduct.getName() );

    putItem( mFilledItemCount, new Item( title ) );


    mFilledItemCount ++;

    mCurrentGridStartIndex = mFilledItemCount;


    // Add as many placeholders as we need to complete the pack.

    for ( int itemIndex = mFilledItemCount; ( itemIndex % mItemsPerPack ) != 0; itemIndex ++ )
      {
      int gridY = ( itemIndex - mCurrentGridStartIndex ) / mNumberOfColumns;
      int gridX = ( itemIndex - mCurrentGridStartIndex ) % mNumberOfColumns;

      int checkerBoardValue = ( gridY + gridX );

      mItemList.add( new Item( checkerBoardValue ) );
      }


    // Add the footer to take the content above the proceed overlay frame. This will get replaced
    // when a new pack is added, but added at the end again.
    mItemList.add( new Item() );
    }


  /*****************************************************
   *
   * Called when the checked status of images is changes
   * by the fragment - goes through all the checkable
   * images and re-set their state.
   *
   *****************************************************/
  public void onUpdateCheckedImages()
    {
    for ( Item item : mItemList )
      {
      if ( item.itemType == ItemType.IMAGE )
        {
        boolean isChecked = mSharedAssetIsCheckedList.get( item.assetIndex );

        if ( item.viewHolder                    != null &&
             item.viewHolder.checkableImageView != null )
          {
          // If the state changes, we animate the transition.
          item.viewHolder.checkableImageView.transitionChecked( isChecked );
          }
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An adaptor item type.
   *
   *****************************************************/
  private enum ItemType
    {
    TITLE,
    IMAGE,
    PLACEHOLDER,
    SPACER;
    }


  /*****************************************************
   *
   * An adaptor item. An item may be one of the following
   * types:
   *
   *   - A title
   *   - An image (asset)
   *   - A placeholder: an unused image
   *
   *****************************************************/
  private class Item implements View.OnClickListener
    {
    ItemType          itemType;
    String            title;
    int               assetIndex;
    ImageSpec         imageSpec;
    int               checkerBoardValue;

    ViewHolder        viewHolder;


    Item( String title )
      {
      this.itemType             = ItemType.TITLE;
      this.title                = title;
      }

    Item( int assetIndex, ImageSpec imageSpec )
      {
      this.itemType   = ItemType.IMAGE;
      this.assetIndex = assetIndex;
      this.imageSpec  = imageSpec;
      }

    Item( int checkerBoardValue )
      {
      this.itemType          = ItemType.PLACEHOLDER;
      this.checkerBoardValue = checkerBoardValue;
      }

    Item()
      {
      this.itemType = ItemType.SPACER;
      }


    /*****************************************************
     *
     * Called when the checkable image is clicked.
     *
     *****************************************************/
    @Override
    public void onClick( View v )
      {
      if ( itemType == ItemType.IMAGE && viewHolder != null )
        {
        boolean isChecked = mSharedAssetIsCheckedList.get( this.assetIndex );

        isChecked = ! isChecked;

        viewHolder.checkableImageView.transitionChecked( isChecked );

        // Update the shared list of checked items
        mSharedAssetIsCheckedList.set( this.assetIndex, isChecked );

        // Notify any listener
        if ( mListener != null ) mListener.onImageCheckChange( this.assetIndex, isChecked );
        }
      }
    }


  /*****************************************************
   *
   * A listener for checked image changes.
   *
   *****************************************************/
  public interface IOnImageCheckChangeListener
    {
    public void onImageCheckChange( int assetIndex, boolean isChecked );
    }


  /*****************************************************
   *
   * References to the views for each item.
   *
   *****************************************************/
  class ViewHolder extends RecyclerView.ViewHolder
    {
    TextView                      titleTextView;
    CheckableImageContainerFrame  checkableImageView;


    public ViewHolder( View itemView )
      {
      super( itemView );

      this.titleTextView      = (TextView)itemView.findViewById( R.id.title_text_view );
      this.checkableImageView = (CheckableImageContainerFrame)itemView.findViewById( R.id.checkable_image_view );
      }
    }


  /*****************************************************
   *
   * Used in conjunction with the GridLayoutManager to
   * return the number of spans for each view type.
   *
   *****************************************************/
  public class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup
    {
    private int  mNumberOfColumns;


    public SpanSizeLookup( int numberOfColumns )
      {
      mNumberOfColumns = numberOfColumns;
      }


    @Override
    public int getSpanSize( int position )
      {
      // Get the item at the position
      Item item = mItemList.get( position );

      // A title and spacer occupies all the columns; everything else just one column.
      if ( item.itemType == ItemType.TITLE ||
           item.itemType == ItemType.SPACER )
        {
        return ( mNumberOfColumns );
        }

      return ( 1 );
      }
    }

  }

