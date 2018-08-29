/*****************************************************
 *
 * ImagePickerGridViewAdaptor.java
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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the adaptor for the image picker grid
 * view.
 *
 *****************************************************/
public class ImagePickerGridViewAdaptor extends RecyclerView.Adapter<ImagePickerGridViewAdaptor.ViewHolder>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ImagePickerGridViewAdaptor";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                                mContext;
  private LinkedHashMap<String,ISelectableItem>  mSelectedItemTable;
  private ICallback                              mCallback;

  private LayoutInflater                         mLayoutInflator;

  private int                                    mMaxImageCount;
  private List<IImagePickerItem>                 mItemList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  ImagePickerGridViewAdaptor( Context context, LinkedHashMap<String,ISelectableItem> selectedItemTable, ICallback callback )
    {
    mContext           = context;
    mSelectedItemTable = selectedItemTable;
    mCallback          = callback;

    mLayoutInflator    = LayoutInflater.from( context );

    mItemList          = new ArrayList<>();
    }


  ////////// RecyclerView.Adapter Method(s) //////////

  /*****************************************************
   *
   * Returns the number of items.
   *
   *****************************************************/
  @Override
  public int getItemCount()
    {
    return ( mItemList.size() );
    }


  /*****************************************************
   *
   * Returns a view holder for the specified view type.
   *
   *****************************************************/
  @Override
  public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
    View gridItemView = mLayoutInflator.inflate( R.layout.ip_grid_item, null );

    return ( new ViewHolder( gridItemView ) );
    }


  /*****************************************************
   *
   * Binds a view holder to a position, i.e. sets up the
   * view for the position.
   *
   *****************************************************/
  @Override
  public void onBindViewHolder( ViewHolder holder, int position )
    {
    holder.position = position;
    holder.item     = mItemList.get( position );


    // Set any label

    String label = holder.item.getLabel();

    if ( label != null && ( ! label.trim().equals( "" ) ) )
      {
      holder.labelTextView.setVisibility( View.VISIBLE );
      holder.labelTextView.setText( holder.item.getLabel() );
      }
    else
      {
      holder.labelTextView.setVisibility( View.INVISIBLE );
      }


    // Determine the visibility / state of the check mark

    if ( holder.item.getKeyIfParent() != null )
      {
      // The check is shown for a parent if anything below it
      // is selected.

      if ( holder.item.getSelectedCount( mSelectedItemTable ) > 0 )
        {
        holder.checkImageView.setVisibility( View.VISIBLE );
        holder.checkImageView.setImageResource( R.drawable.ip_icon_check_on );
        }
      else
        {
        holder.checkImageView.setVisibility( View.INVISIBLE );
        }
      }
    else
      {
      holder.checkImageView.setVisibility( View.VISIBLE );

      if ( holder.item.getSelectedCount( mSelectedItemTable ) > 0 )
        {
        holder.checkImageView.setImageResource( R.drawable.ip_icon_check_on );
        }
      else
        {
        holder.checkImageView.setImageResource( R.drawable.ip_icon_check_off );
        }
      }


    // Load and resize the image using Picasso
    holder.item.loadThumbnailImageInto( holder.imageView );

    // Set the view holder as the the click listener for the view
    holder.itemView.setOnClickListener( holder );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the maximum image count.
   *
   *****************************************************/
  void setMaxImageCount( int maxImageCount )
    {
    mMaxImageCount = maxImageCount;
    }


  /*****************************************************
   *
   * Sets the checked item table.
   *
   *****************************************************/
  void setSelectedItemTable( LinkedHashMap<String,ISelectableItem> selectedItemTable )
    {
    mSelectedItemTable = selectedItemTable;
    }


  /*****************************************************
   *
   * Clears all the items.
   *
   *****************************************************/
  void clearAllItems()
    {
    mItemList.clear();

    notifyDataSetChanged();
    }


  /*****************************************************
   *
   * Adds more items.
   *
   *****************************************************/
  void addMoreItems( Collection<? extends IImagePickerItem> newItems )
    {
    mItemList.addAll( newItems );

    notifyDataSetChanged();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback.
   *
   *****************************************************/
  interface ICallback
    {
    public void onDescend( String parentKey );
    public void onSelectedCountChanged( int oldCount, int newCount );
    }


  /*****************************************************
   *
   * A view holder.
   *
   *****************************************************/
  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
    int               position;

    IImagePickerItem  item;

    ImageView         imageView;
    ImageView         checkImageView;
    TextView          labelTextView;


    public ViewHolder( View itemView )
      {
      super( itemView );

      this.imageView      = (ImageView)itemView.findViewById( R.id.photo_grid_image_view );
      this.checkImageView = (ImageView)itemView.findViewById( R.id.photo_grid_check_image_view );
      this.labelTextView  = (TextView)itemView.findViewById( R.id.photo_grid_label_text_view );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      // If the item is a parent - descend into it

      String parentKey = this.item.getKeyIfParent();

      if ( parentKey != null )
        {
        mCallback.onDescend( parentKey );

        return;
        }


      // If the item is selectable - check or un-check it

      ISelectableItem selectableItem = this.item.getSelectableItem();

      if ( selectableItem != null )
        {
        String selectableItemKey = selectableItem.getKey();

        int previousSelectedCount = mSelectedItemTable.size();

        if ( mSelectedItemTable.containsKey( selectableItemKey ) )
          {
          mSelectedItemTable.remove( selectableItemKey );
          }
        else
          {
          mSelectedItemTable.put( selectableItemKey, selectableItem );
          }

        int newSelectedCount = mSelectedItemTable.size();


        // If there is a maximum allowed number of selected images, and we are over
        // that number, remove selected images in the order they were added.

        if ( mMaxImageCount > 0 )
          {
          while ( newSelectedCount > mMaxImageCount )
            {
            // Get a new iterator each iteration just to be safe. There should only be one iteration anyway.
            // TODO: Create a proper HashQueue at some point to make this cleaner

            Iterator<String> keyIterator = mSelectedItemTable.keySet().iterator();

            String key = keyIterator.next();

            mSelectedItemTable.remove( key );

            newSelectedCount = mSelectedItemTable.size();
            }
          }


        notifyDataSetChanged();

        mCallback.onSelectedCountChanged( previousSelectedCount, newSelectedCount );
        }
      }
    }

  }

