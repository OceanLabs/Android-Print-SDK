/*****************************************************
 *
 * PhotobookAdaptor.java
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

package ly.kite.journey.creation.photobook;


///// Import(s) /////

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ly.kite.R;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Product;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.widget.CheckableImageContainerFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the adaptor for the photobook list view.
 *
 *****************************************************/
public class PhotobookAdaptor extends BaseAdapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "PhotobookAdaptor";

  static private final int     FRONT_COVER_ASSET_INDEX = 0;

  static public  final int     FRONT_COVER_POSITION    = 0;
  static public  final int     INSTRUCTIONS_POSITION   = 1;
  static public  final int     CONTENT_START_POSITION  = 2;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity                 mActivity;
  private Product                  mProduct;
  private List<AssetsAndQuantity>  mAssetsAndQuantityList;
  private IListener                mListener;

  private LayoutInflater           mLayoutInflator;

  private boolean                  mInSelectionMode;
  private HashSet<Asset>           mSelectedEditedAssetSet;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  PhotobookAdaptor( Activity activity, Product product, List<AssetsAndQuantity> assetsAndQuantityList, IListener listener )
    {
    mActivity              = activity;
    mProduct               = product;
    mAssetsAndQuantityList = assetsAndQuantityList;
    mListener              = listener;

    mLayoutInflator        = activity.getLayoutInflater();

    mSelectedEditedAssetSet = new HashSet<>();
    }


  ////////// BaseAdapter Method(s) //////////

  @Override
  public int getCount()
    {
    // The number of rows is the sum of the following:
    //   - Front cover
    //   - Instructions
    //   - Images per page / 2

    return ( 2 + ( mProduct.getQuantityPerSheet() / 2 ) );
    }

  @Override
  public Object getItem( int position )
    {
    return ( null );
    }

  @Override
  public long getItemId( int position )
    {
    return ( 0 );
    }

  @Override
  public View getView( int position, View convertView, ViewGroup parent )
    {
    View view;

    if ( position == FRONT_COVER_POSITION )
      {
      view = mLayoutInflator.inflate( R.layout.list_item_photobook_front_cover, parent, false );

      FrontCoverViewHolder frontCoverViewHolder = new FrontCoverViewHolder( view );

      view.setTag( frontCoverViewHolder );


      // We only display the add image icon if there is no assets and quantity for that position,
      // not just if there is no edited asset yet.

      if ( mAssetsAndQuantityList.size() > FRONT_COVER_POSITION )
        {
        frontCoverViewHolder.assetIndex = FRONT_COVER_ASSET_INDEX;
        frontCoverViewHolder.addImageView.setVisibility( View.INVISIBLE );

        Asset editedAsset = mAssetsAndQuantityList.get( 0 ).getEditedAsset();

        if ( mInSelectionMode )
          {
          if ( mSelectedEditedAssetSet.contains( editedAsset ) )
            {
            frontCoverViewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.CHECKED );
            }
          else
            {
            frontCoverViewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_VISIBLE );
            }
          }
        else
          {
          frontCoverViewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
          }

        if ( editedAsset != null )
          {
          frontCoverViewHolder.checkableImageContainerFrame.clearForNewImage( editedAsset );

          AssetHelper.requestImage( mActivity, editedAsset, frontCoverViewHolder.checkableImageContainerFrame );
          }
        }
      else
        {
        frontCoverViewHolder.assetIndex = -1;
        frontCoverViewHolder.addImageView.setVisibility( View.VISIBLE );
        }
      }
    else if ( position == INSTRUCTIONS_POSITION )
      {
      view = mLayoutInflator.inflate( R.layout.list_item_photobook_instructions, parent, false );
      }
    else
      {
      Object tag;
      ContentViewHolder contentViewHolder;

      if ( convertView != null &&
              ( tag = convertView.getTag() ) != null &&
              ( tag instanceof ContentViewHolder ) )
        {
        view              = convertView;
        contentViewHolder = (ContentViewHolder)tag;
        }
      else
        {
        view              = mLayoutInflator.inflate( R.layout.list_item_photobook_content, parent, false );
        contentViewHolder = new ContentViewHolder( view );

        view.setTag( contentViewHolder );
        }


      // Calculate the assets and quantity indexes for the list view position

      int leftIndex  = ( ( position - CONTENT_START_POSITION ) * 2 ) + 1;
      int rightIndex = leftIndex + 1;

      AssetsAndQuantity leftAssetsAndQuantity  = getAssetsAndQuantityAt( leftIndex );
      AssetsAndQuantity rightAssetsAndQuantity = getAssetsAndQuantityAt( rightIndex );

      contentViewHolder.leftTextView.setText( String.format( "%02d", leftIndex ) );
      contentViewHolder.rightTextView.setText( String.format( "%02d", rightIndex ) );

//      viewHolder.leftAssetsAndQuantity  = leftAssetsAndQuantity;
//      viewHolder.rightAssetsAndQuantity = rightAssetsAndQuantity;


      if ( leftAssetsAndQuantity != null )
        {
        contentViewHolder.leftAssetIndex = leftIndex;
        contentViewHolder.leftAddImageView.setVisibility( View.INVISIBLE );

        Asset leftEditedAsset = leftAssetsAndQuantity.getEditedAsset();

        if ( mInSelectionMode )
          {
          if ( mSelectedEditedAssetSet.contains( leftEditedAsset ) )
            {
            contentViewHolder.leftCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.CHECKED );
            }
          else
            {
            contentViewHolder.leftCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_VISIBLE );
            }
          }
        else
          {
          contentViewHolder.leftCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
          }

        if ( leftEditedAsset != null )
          {
          contentViewHolder.leftCheckableImageContainerFrame.clearForNewImage( leftEditedAsset );

          AssetHelper.requestImage( mActivity, leftEditedAsset, contentViewHolder.leftCheckableImageContainerFrame );
          }
        }
      else
        {
        contentViewHolder.leftAssetIndex = -1;
        contentViewHolder.leftAddImageView.setVisibility( View.VISIBLE );
        contentViewHolder.leftCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
        contentViewHolder.leftCheckableImageContainerFrame.clear();
        }


      if ( rightAssetsAndQuantity != null )
        {
        contentViewHolder.rightAssetIndex = rightIndex;
        contentViewHolder.rightAddImageView.setVisibility( View.INVISIBLE );

        Asset rightEditedAsset = rightAssetsAndQuantity.getEditedAsset();

        if ( mInSelectionMode )
          {
          if ( mSelectedEditedAssetSet.contains( rightEditedAsset ) )
            {
            contentViewHolder.rightCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.CHECKED );
            }
          else
            {
            contentViewHolder.rightCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_VISIBLE );
            }
          }
        else
          {
          contentViewHolder.rightCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
          }

        if ( rightEditedAsset != null )
          {
          contentViewHolder.rightCheckableImageContainerFrame.clearForNewImage( rightEditedAsset );

          AssetHelper.requestImage( mActivity, rightEditedAsset, contentViewHolder.rightCheckableImageContainerFrame );
          }
        }
      else
        {
        contentViewHolder.rightAssetIndex = -1;
        contentViewHolder.rightAddImageView.setVisibility( View.VISIBLE );
        contentViewHolder.rightCheckableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
        contentViewHolder.rightCheckableImageContainerFrame.clear();
        }
      }


    return ( view );
    }



  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the asset for the asset index, or null
   * if it doesn't exist.
   *
   *****************************************************/
  private AssetsAndQuantity getAssetsAndQuantityAt( int index )
    {
    if ( index < 0 || index >= mAssetsAndQuantityList.size() ) return ( null );

    return ( mAssetsAndQuantityList.get( index ) );
    }


  /*****************************************************
   *
   * Notifies us that the data has changed for the supplied
   * assets.
   *
   *****************************************************/
  void notifyDataSetChanged( AssetsAndQuantity assetsAndQuantity )
    {
    // We need to determine which view is displaying the images for the
    // supplied assets, and invalidate it so that it can be re-displayed.


    // TODO: Change to recycler view to do this
//    int childCount = mParentViewGroup.getChildCount();
//
//    for ( int childIndex = 0; childIndex < childCount; childIndex ++ )
//      {
//      View childView = mParentViewGroup.getChildAt( childIndex );
//
//      Object tag;
//
//      if ( ( tag = childView.getTag() ) != null &&
//           ( tag instanceof ViewHolder ) )
//        {
//        ViewHolder viewHolder = (ViewHolder)tag;
//
//        if ( viewHolder.leftAssetsAndQuantity == assetsAndQuantity ||
//             viewHolder.rightAssetsAndQuantity == assetsAndQuantity )
//          {
//          childView.invalidate();
//
//          return;
//          }
//        }
//      }


    // If we failed to find a corresponding child view, then update everything just to be safe
    notifyDataSetChanged();
    }


  /*****************************************************
   *
   * Sets the selection mode.
   *
   *****************************************************/
  public void setSelectionMode( boolean inSelectionMode )
    {
    if ( inSelectionMode != mInSelectionMode )
      {
      mInSelectionMode = inSelectionMode;

      if ( inSelectionMode )
        {
        mSelectedEditedAssetSet.clear();
        }

      notifyDataSetChanged();
      }
    }


  /*****************************************************
   *
   * Returns the selected edited assets.
   *
   *****************************************************/
  public Set<Asset> getSelectedEditedAssetSet()
    {
    return ( mSelectedEditedAssetSet );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An event listener.
   *
   *****************************************************/
  interface IListener
    {
    void onAddImage();
    void onClickImage( int assetIndex );
    void onLongClickImage( int assetIndex, View view );
    }


  /*****************************************************
   *
   * Front cover view holder.
   *
   *****************************************************/
  private class FrontCoverViewHolder implements View.OnClickListener,
                                                View.OnLongClickListener
    {
    View                          view;
    CheckableImageContainerFrame  checkableImageContainerFrame;
    ImageView                     addImageView;

    int                           assetIndex;


    FrontCoverViewHolder( View view )
      {
      this.view                         = view;
      this.checkableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.checkable_image_container_frame );
      this.addImageView                 = (ImageView)view.findViewById( R.id.add_image_view );

      this.checkableImageContainerFrame.setOnClickListener( this );
      this.checkableImageContainerFrame.setOnLongClickListener( this );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      if ( view == this.checkableImageContainerFrame && this.assetIndex >= 0 )
        {
        if ( mInSelectionMode )
          {
          Asset editedAsset = mAssetsAndQuantityList.get( this.assetIndex ).getEditedAsset();

          if ( ! mSelectedEditedAssetSet.contains( editedAsset ) )
            {
            mSelectedEditedAssetSet.add( editedAsset );

            this.checkableImageContainerFrame.setChecked( true );
            }
          else
            {
            mSelectedEditedAssetSet.remove( editedAsset );

            this.checkableImageContainerFrame.setChecked( false );
            }
          }
        else
          {
          mListener.onClickImage( this.assetIndex );
          }

        return;
        }

      if ( ! mInSelectionMode ) mListener.onAddImage();
      }


    ////////// View.OnLongClickListener Method(s) //////////

    @Override
    public boolean onLongClick( View view )
      {
      if ( ! mInSelectionMode )
        {
        if ( view == this.checkableImageContainerFrame && this.assetIndex >= 0 )
          {
          mListener.onLongClickImage( this.assetIndex, this.checkableImageContainerFrame );

          return ( true );
          }
        }

      return ( false );
      }

    }


  /*****************************************************
   *
   * Content view holder.
   *
   *****************************************************/
  private class ContentViewHolder implements View.OnClickListener,
                                             View.OnLongClickListener
    {
    View                view;

    CheckableImageContainerFrame  leftCheckableImageContainerFrame;
    CheckableImageContainerFrame  rightCheckableImageContainerFrame;

    ImageView                     leftAddImageView;
    ImageView                     rightAddImageView;

    TextView                      leftTextView;
    TextView                      rightTextView;

    int                           leftAssetIndex;
    int                           rightAssetIndex;


    ContentViewHolder( View view )
      {
      this.view = view;

      this.leftCheckableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.left_checkable_image_container_frame );
      this.rightCheckableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.right_checkable_image_container_frame );

      this.leftAddImageView        = (ImageView)view.findViewById( R.id.left_add_image_view );
      this.rightAddImageView       = (ImageView)view.findViewById( R.id.right_add_image_view );

      this.leftTextView            = (TextView)view.findViewById( R.id.left_text_view );
      this.rightTextView           = (TextView)view.findViewById( R.id.right_text_view );


      leftCheckableImageContainerFrame.setOnClickListener( this );
      leftCheckableImageContainerFrame.setOnLongClickListener( this );

      rightCheckableImageContainerFrame.setOnClickListener( this );
      rightCheckableImageContainerFrame.setOnLongClickListener( this );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      if ( view == this.leftCheckableImageContainerFrame && this.leftAssetIndex >= 0 )
        {
        if ( mInSelectionMode )
          {
          Asset editedAsset = mAssetsAndQuantityList.get( this.leftAssetIndex ).getEditedAsset();

          if ( ! mSelectedEditedAssetSet.contains( editedAsset ) )
            {
            mSelectedEditedAssetSet.add( editedAsset );

            this.leftCheckableImageContainerFrame.setChecked( true );
            }
          else
            {
            mSelectedEditedAssetSet.remove( editedAsset );

            this.leftCheckableImageContainerFrame.setChecked( false );
            }
          }
        else
          {
          mListener.onClickImage( this.leftAssetIndex );
          }

        return;
        }
      else if ( view == this.rightCheckableImageContainerFrame && this.rightAssetIndex >= 0 )
        {
        if ( mInSelectionMode )
          {
          Asset editedAsset = mAssetsAndQuantityList.get( this.rightAssetIndex ).getEditedAsset();

          if ( ! mSelectedEditedAssetSet.contains( editedAsset ) )
            {
            mSelectedEditedAssetSet.add( editedAsset );

            this.rightCheckableImageContainerFrame.setChecked( true );
            }
          else
            {
            mSelectedEditedAssetSet.remove( editedAsset );

            this.rightCheckableImageContainerFrame.setChecked( false );
            }
          }
        else
          {
          mListener.onClickImage( this.rightAssetIndex );
          }

        return;
        }

      if ( ! mInSelectionMode ) mListener.onAddImage();
      }


    ////////// View.OnLongClickListener Method(s) //////////

    @Override
    public boolean onLongClick( View view )
      {
      if ( ! mInSelectionMode )
        {
        if ( view == this.leftCheckableImageContainerFrame )
          {
          if ( this.leftAssetIndex >= 0 )
            {
            mListener.onLongClickImage( this.leftAssetIndex, this.leftCheckableImageContainerFrame );

            return ( true );
            }
          }
        else if ( view == this.rightCheckableImageContainerFrame )
          {
          if ( this.rightAssetIndex >= 0 )
            {
            mListener.onLongClickImage( this.rightAssetIndex, this.rightCheckableImageContainerFrame );

            return ( true );
            }
          }
        }

      return ( false );
      }

    }

  }

