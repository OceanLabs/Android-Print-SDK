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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


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

  private Context                     mContext;
  private LinkedHashSet<String>       mSelectedURLStrings;
  private ICallback                   mCallback;

  private LayoutInflater              mLayoutInflator;
  private View                        mLoadingView;

  private int                         mMaxImageCount;
  private List<IImagePickerItem>      mItemList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  ImagePickerGridViewAdaptor( Context context, LinkedHashSet<String> selectedURLStrings, ICallback callback )
    {
    mContext              = context;
    mSelectedURLStrings   = selectedURLStrings;
    mCallback             = callback;

    mLayoutInflator       = LayoutInflater.from( context );

    mLoadingView          = mLayoutInflator.inflate( R.layout.ip_loading_view, null );

    mItemList             = new ArrayList<>();
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
    holder.item = mItemList.get( position );


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


    // Determine the visibility / state of the check

    if ( holder.item.getParentKey() != null )
      {
      // The check is shown for a parent if anything below it
      // is selected.

      if ( holder.item.getSelectedCount( mSelectedURLStrings ) > 0 )
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

      if ( mSelectedURLStrings.contains( holder.item.getImageURLString() ) )
        {
        holder.checkImageView.setImageResource( R.drawable.ip_icon_check_on );
        }
      else
        {
        holder.checkImageView.setImageResource( R.drawable.ip_icon_check_off );
        }
      }


    // Load and resize the image using Picasso

    Picasso.with( mContext )
            .load( holder.item.getThumbnailImageURLString() )
            .resizeDimen( R.dimen.ip_image_default_resize_width, R.dimen.ip_image_default_resize_height )
            .centerCrop()
            .onlyScaleDown()
            .into( holder.imageView );


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
   * Sets the checked item set.
   *
   *****************************************************/
  void setSelectedURLStrings( LinkedHashSet<String> selectedURLStrings )
    {
    mSelectedURLStrings = selectedURLStrings;
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


  /*****************************************************
   *
   * Sets whether the loading view is visible.
   *
   *****************************************************/
  void setLoadingViewVisible( boolean isVisible )
    {
    // TODO

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
    public void onDescendLevel( String parentKey );
    public void onSelectedCountChanged( int oldCount, int newCount );
    }


  /*****************************************************
   *
   * A view holder.
   *
   *****************************************************/
  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
    IImagePickerItem item;

    ImageView       imageView;
    ImageView       checkImageView;
    TextView        labelTextView;


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

      String parentKey = this.item.getParentKey();

      if ( parentKey != null )
        {
        mCallback.onDescendLevel( parentKey );
        }


      // Check or un-check the item
      else
        {
        // Use the full image URL as the hash key. If we use the item, it will fail to match
        // after the set has been restored.
        String urlString = this.item.getImageURLString();

        int previousSelectedCount = mSelectedURLStrings.size();

        if ( mSelectedURLStrings.contains( urlString ) )
          {
          mSelectedURLStrings.remove( urlString );
          }
        else
          {
          mSelectedURLStrings.add( urlString );
          }

        int newSelectedCount = mSelectedURLStrings.size();


        // If there is a maximum allowed number of selected images, and we are over
        // that number, remove selected images in the order they were added.

        if ( mMaxImageCount > 0 )
          {
          while ( newSelectedCount > mMaxImageCount )
            {
            // Get a new iterator each iteration just to be safe. There should only be one iteration anyway.
            // TODO: Create a proper HashQueue at some point to make this cleaner

            Iterator<String> urlStringIterator = mSelectedURLStrings.iterator();

            urlStringIterator.next();
            urlStringIterator.remove();

            newSelectedCount = mSelectedURLStrings.size();
            }
          }


        notifyDataSetChanged();

        mCallback.onSelectedCountChanged( previousSelectedCount, newSelectedCount );
        }
      }
    }

  }

