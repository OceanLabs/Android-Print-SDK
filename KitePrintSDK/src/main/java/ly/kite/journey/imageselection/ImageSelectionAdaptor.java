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

package ly.kite.journey.imageselection;


///// Import(s) /////

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.UserJourneyType;
import ly.kite.product.Asset;
import ly.kite.product.AssetHelper;
import ly.kite.product.Product;
import ly.kite.widget.CheckableImageView;

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
  private static final String  LOG_TAG = "ImageSelectionAdaptor";

  private static final int VIEW_TYPE_IMAGE               = 0x00;
  private static final int VIEW_TYPE_TITLE               = 0x01;
  private static final int VIEW_TYPE_SPACER              = 0x02;

  private static final int IMAGE_CACHE_CAPACITY_IN_BYTES = 10 * 1024 * 1024;  // 10 MB


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                      mContext;
  private List<Boolean>                mSharedAssetIsCheckedList;
  private int                          mNumberOfColumns;
  private UserJourneyType              mUserJourneyType;
  private IOnImageCheckChangeListener  mListener;

  private int                          mScaledImageWidthInPixels;
  private LayoutInflater               mLayoutInflator;
  private int                          mPlaceholderBackgroundColour1;
  private int                          mPlaceholderBackgroundColour2;

  private List<Item>                   mItemList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ImageSelectionAdaptor( Context context, Product product, List<AssetsAndQuantity> assetAndQuantityList, List<Boolean> sharedAssetIsCheckedList, int numberOfColumns, IOnImageCheckChangeListener listener )
    {
    mContext                  = context;
    mSharedAssetIsCheckedList = sharedAssetIsCheckedList;
    mNumberOfColumns          = numberOfColumns;
    mUserJourneyType          = product.getUserJourneyType();
    mListener                 = listener;

    mLayoutInflator           = LayoutInflater.from( context );

    Resources resources = context.getResources();

    mScaledImageWidthInPixels     = resources.getDimensionPixelSize( R.dimen.image_thumbnail_size );

    mPlaceholderBackgroundColour1 = resources.getColor( R.color.image_selection_placeholder_background_1 );
    mPlaceholderBackgroundColour2 = resources.getColor( R.color.image_selection_placeholder_background_2 );


    // All the titles, images, and placeholders need to go into a flat list.

    int imagesPerPack = product.getQuantityPerSheet();

    mItemList = new ArrayList<>();

    int packNumber           = 0;
    int assetIndex;
    int itemIndex            = 0;
    int gridStartItemIndex   = 0;

    for ( assetIndex = 0; assetIndex < assetAndQuantityList.size(); assetIndex ++ )
      {
      // Check if we need to add a title item

      if ( ( assetIndex % imagesPerPack ) == 0 )
        {
        packNumber ++;

        String title = context.getString( R.string.image_selection_pack_title_format_string, packNumber, imagesPerPack, product.getName() );

        mItemList.add( new Item( title ) );

        itemIndex ++;

        gridStartItemIndex = itemIndex;
        }


      // Add the image
      mItemList.add( new Item( assetIndex, assetAndQuantityList.get( assetIndex ) ) );

      itemIndex ++;
      }


    // Add as many placeholders as we need to complete the pack

    while ( ( assetIndex % imagesPerPack ) != 0 )
      {
      int gridY = ( itemIndex - gridStartItemIndex ) / numberOfColumns;
      int gridX = ( itemIndex - gridStartItemIndex ) % numberOfColumns;

      int checkerBoardValue = ( gridY + gridX );

      mItemList.add( new Item( checkerBoardValue ) );

      assetIndex ++;
      itemIndex ++;
      }


    // Add the footer to take the content above the proceed overlay frame
    mItemList.add( new Item() );
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
    // Get the item at this position
    Item item = mItemList.get( position );

    item.viewHolder = viewHolder;


    // Determine what type of item it is

    switch ( item.itemType )
      {
      case TITLE:

        // Set the title
        viewHolder.titleTextView.setText( item.title );

        break;

      case IMAGE:

        // Clear any image that is currently set, then request a new one.

        Asset editedAsset = item.assetsAndQuantity.getEditedAsset();

        viewHolder.checkableImageView.clearForNewImage( editedAsset );

        AssetHelper.requestImage( mContext, editedAsset, null, mScaledImageWidthInPixels, viewHolder.checkableImageView );


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

        // If the state changes, we animate the transition.
        item.viewHolder.checkableImageView.transitionChecked( isChecked );
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
    AssetsAndQuantity assetsAndQuantity;
    int               checkerBoardValue;

    ViewHolder        viewHolder;


    Item( String title )
      {
      this.itemType             = ItemType.TITLE;
      this.title                = title;
      }

    Item( int assetIndex, AssetsAndQuantity assetsAndQuantity )
      {
      this.itemType          = ItemType.IMAGE;
      this.assetIndex        = assetIndex;
      this.assetsAndQuantity = assetsAndQuantity;
      }

    Item( int checkerBoardValue )
      {
      this.itemType             = ItemType.PLACEHOLDER;
      this.checkerBoardValue    = checkerBoardValue;
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
    TextView           titleTextView;
    CheckableImageView checkableImageView;


    public ViewHolder( View itemView )
      {
      super( itemView );

      titleTextView      = (TextView)itemView.findViewById( R.id.title_text_view );
      checkableImageView = (CheckableImageView)itemView.findViewById( R.id.checkable_image_view );
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

