/*****************************************************
 *
 * PosterAdaptor.java
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

package ly.kite.journey.creation.poster;


///// Import(s) /////

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ly.kite.R;
import ly.kite.ordering.ImageSpec;
import ly.kite.widget.AREFrameLayout;
import ly.kite.widget.CheckableImageContainerFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the adaptor for the photobook list view.
 *
 *****************************************************/
public class PosterAdaptor extends RecyclerView.Adapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                 = "PosterAdaptor";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity                                   mActivity;
  private PosterFragment                             mPosterFragment;
  private ArrayList<ImageSpec>                       mImageSpecArrayList;
  private IListener                                  mListener;

  private LayoutInflater                             mLayoutInflator;

  private HashSet<CheckableImageContainerFrame>      mVisibleCheckableImageSet;
  private SparseArray<CheckableImageContainerFrame>  mVisibleCheckableImageArray;

  private boolean                                    mInSelectionMode;
  private HashSet<Integer>                           mSelectedAssetIndexHashSet;

  private int                                        mCurrentlyHighlightedAssetIndex;

  private float mImageAspectRatio;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  PosterAdaptor( Activity activity, PosterFragment posterFragment ,ArrayList<ImageSpec> imageSpecArrayList, float aspectRatio, IListener listener )
    {
    mActivity                   = activity;
    mPosterFragment             = posterFragment;
    mImageSpecArrayList         = imageSpecArrayList;
    mListener                   = listener;

    mImageAspectRatio = aspectRatio;

    mLayoutInflator             = activity.getLayoutInflater();

    mVisibleCheckableImageSet   = new HashSet<>();
    mVisibleCheckableImageArray = new SparseArray<>();

    mSelectedAssetIndexHashSet  = new HashSet<>();
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
    return ( mImageSpecArrayList.size() );
    }


  /*****************************************************
   *
   * Creates a view holder for the supplied view type.
   *
   *****************************************************/
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
    return ( new ImageViewHolder( mLayoutInflator.inflate( R.layout.item_poster_image, parent, false ) ) );
    }


  /*****************************************************
   *
   * Populates a view.
   *
   *****************************************************/
  @Override
  public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int position )
    {
    ImageViewHolder imageViewHolder = (ImageViewHolder)viewHolder;

    // We don't need to remove any previously visible images, because everything is always visible


    // Set up the new image

    imageViewHolder.imageIndex = position;

    CheckableImageContainerFrame checkableImageContainerFrame = imageViewHolder.checkableImageContainerFrame;
    ImageView                    addImageView                 = imageViewHolder.addImageView;

    mVisibleCheckableImageSet.add( checkableImageContainerFrame );
    mVisibleCheckableImageArray.put( position, checkableImageContainerFrame );

    // Get the matching image spec
    ImageSpec imageSpec = getImageSpecAt( position );


    if ( imageSpec != null )
      {
      addImageView.setVisibility( View.INVISIBLE );

      if ( mInSelectionMode )
        {
        if ( mSelectedAssetIndexHashSet.contains( position ) )
          {
          checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.CHECKED );
          }
        else
          {
          checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_VISIBLE );
          }
        }
      else
        {
        checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
        }


      imageSpec.loadThumbnail( mActivity, checkableImageContainerFrame, mPosterFragment.isPosterCollage());
      }
    else
      {
      addImageView.setVisibility( View.VISIBLE );
      checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
      checkableImageContainerFrame.clear();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the asset for the asset index, or null
   * if it doesn't exist.
   *
   *****************************************************/
  private ImageSpec getImageSpecAt( int imageIndex )
    {
    if ( imageIndex >= 0 && imageIndex < mImageSpecArrayList.size() )
      {
      return ( mImageSpecArrayList.get( imageIndex ) );
      }

    return ( null );
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


      CheckableImageContainerFrame.State newState;

      if ( inSelectionMode )
        {
        mSelectedAssetIndexHashSet.clear();

        newState = CheckableImageContainerFrame.State.UNCHECKED_VISIBLE;
        }
      else
        {
        newState = CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE;
        }


      // Check all the visible check image containers to show their check circle

      Iterator<CheckableImageContainerFrame> visibleCheckableImageIterator = mVisibleCheckableImageSet.iterator();

      while ( visibleCheckableImageIterator.hasNext() )
        {
        CheckableImageContainerFrame checkableImage = visibleCheckableImageIterator.next();

        checkableImage.setState( newState );
        }
      }
    }


  /*****************************************************
   *
   * Selects an image.
   *
   *****************************************************/
  public void selectImage( int imageIndex )
    {
    ImageSpec imageSpec = mImageSpecArrayList.get( imageIndex );

    if ( imageSpec != null )
      {
      mSelectedAssetIndexHashSet.add( imageIndex );


      // If the image for this asset is visible, set its state

      CheckableImageContainerFrame visibleCheckableImage = mVisibleCheckableImageArray.get( imageIndex );

      if ( visibleCheckableImage != null )
        {
        visibleCheckableImage.setState( CheckableImageContainerFrame.State.CHECKED );
        }


      onSelectedImagesChanged();
      }
    }


  /*****************************************************
   *
   * Called when the set of selected assets has changed.
   *
   *****************************************************/
  private void onSelectedImagesChanged()
    {
    mListener.onSelectedImagesChanged( mSelectedAssetIndexHashSet.size() );
    }


  /*****************************************************
   *
   * Returns the selected edited assets.
   *
   *****************************************************/
  public HashSet<Integer> getSelectedAssets()
    {
    return ( mSelectedAssetIndexHashSet );
    }


  /*****************************************************
   *
   * Sets the currently highlighted asset image.
   *
   *****************************************************/
  public void setHighlightedAsset( int assetIndex )
    {
    if ( assetIndex != mCurrentlyHighlightedAssetIndex )
      {
      clearHighlightedAsset();

      CheckableImageContainerFrame newHighlightedCheckableImage = mVisibleCheckableImageArray.get( assetIndex );

      if ( newHighlightedCheckableImage != null )
        {
        Resources resources = mActivity.getResources();

        newHighlightedCheckableImage.setHighlightBorderSizePixels( resources.getDimensionPixelSize( R.dimen.checkable_image_highlight_border_size ) );
        newHighlightedCheckableImage.setHighlightBorderColour( resources.getColor( R.color.photobook_target_image_highlight ) );
        newHighlightedCheckableImage.setHighlightBorderShowing( true );

        mCurrentlyHighlightedAssetIndex = assetIndex;
        }
      }
    }


  /*****************************************************
   *
   * Clears the currently highlighted asset image.
   *
   *****************************************************/
  public void clearHighlightedAsset()
    {
    if ( mCurrentlyHighlightedAssetIndex >= 0 )
      {
      CheckableImageContainerFrame currentlyHighlightedCheckableImage = mVisibleCheckableImageArray.get( mCurrentlyHighlightedAssetIndex, null );

      if ( currentlyHighlightedCheckableImage != null )
        {
        currentlyHighlightedCheckableImage.setHighlightBorderShowing( false );
        }

      mCurrentlyHighlightedAssetIndex = -1;
      }
    }


  /*****************************************************
   *
   * Called when add image is clicked whilst in selection
   * mode. The action is rejected by animating the icon.
   *
   *****************************************************/
  void rejectAddImage( ImageView imageView )
    {
    // Get the animation set and start it
    Animation animation = AnimationUtils.loadAnimation( mActivity, R.anim.reject_add_image );

    imageView.startAnimation( animation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An event listener.
   *
   *****************************************************/
  interface IListener
    {
    void onClickImage( int assetIndex, View view );
    void onLongClickImage( int assetIndex, View view );
    void onSelectedImagesChanged( int selectedImageCount );
    }


  /*****************************************************
   *
   * Image view holder.
   *
   *****************************************************/
  private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                           View.OnLongClickListener
    {
    int                           imageIndex;
    CheckableImageContainerFrame  checkableImageContainerFrame;
    ImageView                     addImageView;
    AREFrameLayout                mFrameLayout;

    ImageViewHolder( View view )
      {
      super( view );

      this.checkableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.checkable_image_container_frame );
      this.addImageView                 = (ImageView)view.findViewById( R.id.add_image_view );
      this.mFrameLayout = (AREFrameLayout) view.findViewById(R.id.poster_item_frame_layout);
      mFrameLayout.setAspectRatio(mImageAspectRatio);

      this.checkableImageContainerFrame.setOnClickListener( this );
      this.checkableImageContainerFrame.setOnLongClickListener( this );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      if ( view == this.checkableImageContainerFrame )
        {
        if ( mInSelectionMode )
          {
          ImageSpec imageSpec = getImageSpecAt( this.imageIndex );

          if ( imageSpec != null )
            {
            if ( ! mSelectedAssetIndexHashSet.contains( this.imageIndex ) )
              {
              mSelectedAssetIndexHashSet.add( this.imageIndex );

              this.checkableImageContainerFrame.setChecked( true );
              }
            else
              {
              mSelectedAssetIndexHashSet.remove( this.imageIndex );

              this.checkableImageContainerFrame.setChecked( false );
              }

            onSelectedImagesChanged();
            }
          else
            {
            rejectAddImage( this.addImageView );
            }
          }
        else
          {
          mListener.onClickImage( this.imageIndex, view );
          }
        }
      }


    ////////// View.OnLongClickListener Method(s) //////////

    @Override
    public boolean onLongClick( View view )
      {
      if ( ! mInSelectionMode )
        {
        if ( view == this.checkableImageContainerFrame )
          {
          if ( getImageSpecAt( this.imageIndex ) != null )
            {
            mListener.onLongClickImage( this.imageIndex, this.checkableImageContainerFrame );

            return ( true );
            }
          }
        }
      return ( false );
      }
    }

  }