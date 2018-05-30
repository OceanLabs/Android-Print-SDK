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
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.ordering.ImageSpec;
import ly.kite.catalogue.Product;
import ly.kite.image.ImageAgent;
import ly.kite.util.AssetFragment;
import ly.kite.widget.CheckableImageContainerFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the adaptor for the photobook list view.
 *
 *****************************************************/
public class PhotobookAdaptor extends RecyclerView.Adapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                   = "PhotobookAdaptor";

  static private final int     FRONT_COVER_ASSET_INDEX   = 0;

  static public  final int     FRONT_COVER_POSITION      = 0;
  static public  final int     INSTRUCTIONS_POSITION     = 1;
  static public  final int     CONTENT_START_POSITION    = 2;

  static public  final int     FRONT_COVER_VIEW_TYPE     = 0;
  static public  final int     INSTRUCTIONS_VIEW_TYPE    = 1;
  static public  final int     CONTENT_VIEW_TYPE         = 2;

  static         final int     COVER_SUMMARY_IMAGE_COUNT = 9;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity                                   mActivity;
  private Product                                    mProduct;
  private ArrayList<ImageSpec>                       mImageSpecArrayList;
  private IListener                                  mListener;

  private LayoutInflater                             mLayoutInflator;

  private HashSet<CheckableImageContainerFrame>      mVisibleCheckableImageSet;
  private SparseArray<CheckableImageContainerFrame>  mVisibleCheckableImageArray;

  private boolean                                    mInSelectionMode;
  private HashSet<Integer>                           mSelectedAssetIndexHashSet;

  private boolean                                    mFrontCoverIsSummary;
  private int                                        mFrontCoverPlaceableImageCount;

  private int                                        mCurrentlyHighlightedAssetIndex;

  private float                                      mAspectRatio;

  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  PhotobookAdaptor( Activity activity, Product product, ArrayList<ImageSpec> imageSpecArrayList, IListener listener )
    {
    mActivity                   = activity;
    mProduct                    = product;
    mImageSpecArrayList         = imageSpecArrayList;
    mListener                   = listener;

    mLayoutInflator             = activity.getLayoutInflater();

    mVisibleCheckableImageSet   = new HashSet<>();
    mVisibleCheckableImageArray = new SparseArray<>();

    mSelectedAssetIndexHashSet  = new HashSet<>();

    mAspectRatio = product.getImageAspectRatio();

    if ( mFrontCoverIsSummary = KiteSDK.getInstance( activity ).getCustomiser().photobookFrontCoverIsSummary() )
      {
      mFrontCoverPlaceableImageCount = 0;
      }
    else
      {
      mFrontCoverPlaceableImageCount = 1;
      }
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
    // The number of rows is the sum of the following:
    //   - Front cover
    //   - Instructions
    //   - (Images per page / 2, rounded up ) + 1 (for blank pages at start/end of book)

    return ( 2 + ( ( mProduct.getQuantityPerSheet() + 1 ) / 2 ) ) + 1;
    }


  /*****************************************************
   *
   * Returns the view type for the position.
   *
   *****************************************************/
  @Override
  public int getItemViewType( int position )
    {
    if      ( position == FRONT_COVER_POSITION  ) return ( FRONT_COVER_VIEW_TYPE  );
    else if ( position == INSTRUCTIONS_POSITION ) return ( INSTRUCTIONS_VIEW_TYPE );

    return ( CONTENT_VIEW_TYPE );
    }


  /*****************************************************
   *
   * Creates a view holder for the supplied view type.
   *
   *****************************************************/
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
    if ( viewType == FRONT_COVER_VIEW_TYPE )
      {
      return ( new FrontCoverViewHolder( mLayoutInflator.inflate( R.layout.list_item_photobook_front_cover, parent, false ) ) );
      }
    else if ( viewType == INSTRUCTIONS_VIEW_TYPE )
      {
      return ( new InstructionsViewHolder( mLayoutInflator.inflate( R.layout.list_item_photobook_instructions, parent, false ) ) );
      }

    return ( new ContentViewHolder( mLayoutInflator.inflate( R.layout.list_item_photobook_content, parent, false ) ) );
    }


  /*****************************************************
   *
   * Populates a view.
   *
   *****************************************************/
  @Override
  public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int position )
    {
    if ( viewHolder instanceof FrontCoverViewHolder )
      {
      bindFrontCover( (FrontCoverViewHolder) viewHolder );
      }
    else if ( viewHolder instanceof InstructionsViewHolder )
      {
      // Do nothing - the inflated view already has the correct text
      }
    else
      {
      bindContent( (ContentViewHolder)viewHolder, position );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Binds the front cover view holder.
   *
   *****************************************************/
  private void bindFrontCover( FrontCoverViewHolder viewHolder )
    {
    if ( mFrontCoverIsSummary )
      {
      ///// Summary front cover /////

      viewHolder.imageIndex = -1;

      viewHolder.checkableImageContainerFrame.setVisibility( View.INVISIBLE );
      viewHolder.addImageView.setVisibility( View.INVISIBLE );
      viewHolder.imageGridView.setVisibility( View.VISIBLE );

      // Populate the summary image grid
      for ( int imageIndex = 0; imageIndex < COVER_SUMMARY_IMAGE_COUNT; imageIndex ++ )
        {
        ImageView imageView = viewHolder.imageGridViewHolder.gridImageViewArray[ imageIndex ];

        imageView.setImageDrawable( null );

        ImageSpec imageSpec = mImageSpecArrayList.get( imageIndex );

        if ( imageSpec != null )
          {
          AssetFragment imageAssetFragment = imageSpec.getAssetFragment();

          if ( imageAssetFragment != null )
            {
            ImageAgent.with( mActivity )
                    .load( imageAssetFragment )
                    .setHighPriority( true )
                    .resizeForDimen( imageView, R.dimen.image_default_resize_size, R.dimen.image_default_resize_size )
                    .onlyScaleDown()
                    .reduceColourSpace()
                    .into( imageView, imageAssetFragment );
            }
          }

        }
      }
    else
      {
      ///// Standard front cover /////

      viewHolder.checkableImageContainerFrame.setVisibility( View.VISIBLE );
      // The add image view visibility is set below
      viewHolder.imageGridView.setVisibility( View.INVISIBLE );

      // If the holder is already bound - remove its reference
      if ( viewHolder.imageIndex >= 0 )
        {
        mVisibleCheckableImageSet.remove( viewHolder.checkableImageContainerFrame );
        mVisibleCheckableImageArray.remove( viewHolder.imageIndex );
        }


      viewHolder.imageIndex = FRONT_COVER_ASSET_INDEX;

      mVisibleCheckableImageSet.add( viewHolder.checkableImageContainerFrame );
      mVisibleCheckableImageArray.put( viewHolder.imageIndex, viewHolder.checkableImageContainerFrame );


      // We only display the add image icon if there is no assets and quantity for that position,
      // not just if there is no edited asset yet.

      ImageSpec imageSpec = mImageSpecArrayList.get( FRONT_COVER_ASSET_INDEX );

      if ( imageSpec != null )
        {
        viewHolder.addImageView.setVisibility( View.INVISIBLE );

        AssetFragment assetFragment = imageSpec.getAssetFragment();

        if ( mInSelectionMode )
          {
          if ( mSelectedAssetIndexHashSet.contains( FRONT_COVER_ASSET_INDEX ) )
            {
            viewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.CHECKED );
            }
          else
            {
            viewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_VISIBLE );
            }
          }
        else
          {
          viewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
          }

        if ( assetFragment != null )
          {
          viewHolder.checkableImageContainerFrame.clearForNewImage( assetFragment );

          ImageAgent.with( mActivity )
                  .load( assetFragment )
                  .setHighPriority( true )
                  .resizeForDimen( viewHolder.checkableImageContainerFrame, R.dimen.image_default_resize_size, R.dimen.image_default_resize_size )
                  .onlyScaleDown()
                  .reduceColourSpace()
                  .into( viewHolder.checkableImageContainerFrame, assetFragment );
          }

        }
      else
        {
        viewHolder.addImageView.setVisibility( View.VISIBLE );
        viewHolder.checkableImageContainerFrame.setState( CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE );
        viewHolder.checkableImageContainerFrame.clear();
        }
      }
    }


  /*****************************************************
   *
   * Binds the content view holder.
   *
   *****************************************************/
  private void bindContent( ContentViewHolder viewHolder, int position )
    {
    if ( viewHolder.leftAssetIndex >= 0)
      {
      mVisibleCheckableImageSet.remove( viewHolder.leftCheckableImageContainerFrame );
      mVisibleCheckableImageArray.remove( viewHolder.leftAssetIndex );
      }

    if ( viewHolder.rightAssetIndex >= 0 )
      {
      mVisibleCheckableImageSet.remove( viewHolder.rightCheckableImageContainerFrame );
      mVisibleCheckableImageArray.remove( viewHolder.rightAssetIndex );
      }

    // Calculate the indexes for the list view position (the first image from content (left image) is the cover one , but we don't display it)
    int leftIndex  = mFrontCoverPlaceableImageCount + ( ( position - CONTENT_START_POSITION ) * 2 ) - 1;
    int rightIndex = leftIndex + 1;

    viewHolder.leftAssetIndex = leftIndex;

    mVisibleCheckableImageSet.add( viewHolder.leftCheckableImageContainerFrame );
    mVisibleCheckableImageArray.put( viewHolder.leftAssetIndex, viewHolder.leftCheckableImageContainerFrame );

    //first page is blank
    if (leftIndex == 0) {
      //remove page count , and remove/hide checkableFrame (no onClick action)
      viewHolder.leftTextView.setText(" ");
      viewHolder.leftAddImageView.setVisibility(View.GONE);
      viewHolder.leftCheckableImageContainerFrame.clear();
      viewHolder.leftCheckableImageContainerFrame.setVisibility(View.GONE);

    } else {
      viewHolder.leftTextView.setText(String.format("%02d", leftIndex));
      viewHolder.leftCheckableImageContainerFrame.setVisibility(View.VISIBLE);

      ImageSpec leftImageSpec = getImageSpecAt(leftIndex);

      if (leftImageSpec != null) {
        viewHolder.leftAddImageView.setVisibility(View.INVISIBLE);

        AssetFragment leftAssetFragment = leftImageSpec.getAssetFragment();

        if (mInSelectionMode) {
          if (mSelectedAssetIndexHashSet.contains(viewHolder.leftAssetIndex)) {
            viewHolder.leftCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.CHECKED);
          } else {
            viewHolder.leftCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_VISIBLE);
          }
        } else {
          viewHolder.leftCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE);
        }

        if (leftAssetFragment != null) {
          viewHolder.leftCheckableImageContainerFrame.clearForNewImage(leftAssetFragment);

          //AssetHelper.requestImage( mActivity, leftEditedAsset, viewHolder.leftCheckableImageContainerFrame );
          ImageAgent.with(mActivity)
              .load(leftAssetFragment)
              .setHighPriority( true )
              .resizeForDimen(viewHolder.leftCheckableImageContainerFrame, R.dimen.image_default_resize_size, R.dimen.image_default_resize_size)
              .onlyScaleDown()
              .reduceColourSpace()
              .into(viewHolder.leftCheckableImageContainerFrame, leftAssetFragment);
        }
      } else {
        viewHolder.leftAddImageView.setVisibility(View.VISIBLE);
        viewHolder.leftCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE);
        viewHolder.leftCheckableImageContainerFrame.clear();
      }
    }


    viewHolder.rightAssetIndex = rightIndex;

    mVisibleCheckableImageSet.add( viewHolder.rightCheckableImageContainerFrame );
    mVisibleCheckableImageArray.put( viewHolder.rightAssetIndex, viewHolder.rightCheckableImageContainerFrame );

    //last page is also blank (index is out of bounds by 1 with the imageSpecArrayList)
    if (rightIndex == mImageSpecArrayList.size()) {
        //remove page count , and remove/hide checkableFrame (no onClick action)
        viewHolder.rightTextView.setText(" ");
        viewHolder.rightAddImageView.setVisibility(View.GONE);
        viewHolder.rightCheckableImageContainerFrame.clear();
        viewHolder.rightCheckableImageContainerFrame.setVisibility(View.GONE);

      } else {

      viewHolder.rightTextView.setText(String.format("%02d", rightIndex));
      viewHolder.rightCheckableImageContainerFrame.setVisibility(View.VISIBLE);

      ImageSpec rightImageSpec = getImageSpecAt(rightIndex);

      if (rightImageSpec != null) {
        viewHolder.rightAddImageView.setVisibility(View.INVISIBLE);

        AssetFragment rightAssetFragment = rightImageSpec.getAssetFragment();

        if (mInSelectionMode) {
          if (mSelectedAssetIndexHashSet.contains(viewHolder.rightAssetIndex)) {
            viewHolder.rightCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.CHECKED);
          } else {
            viewHolder.rightCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_VISIBLE);
          }
        } else {
          viewHolder.rightCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE);
        }

        if (rightAssetFragment != null) {
          viewHolder.rightCheckableImageContainerFrame.clearForNewImage(rightAssetFragment);

          ImageAgent.with(mActivity)
              .load(rightAssetFragment)
              .setHighPriority( true )
              .resizeForDimen(viewHolder.rightCheckableImageContainerFrame, R.dimen.image_default_resize_size, R.dimen.image_default_resize_size)
              .onlyScaleDown()
              .reduceColourSpace()
              .into(viewHolder.rightCheckableImageContainerFrame, rightAssetFragment);
        }
      } else {
        viewHolder.rightAddImageView.setVisibility(View.VISIBLE);
        viewHolder.rightCheckableImageContainerFrame.setState(CheckableImageContainerFrame.State.UNCHECKED_INVISIBLE);
        viewHolder.rightCheckableImageContainerFrame.clear();
      }
    }
    }


  /*****************************************************
   *
   * Returns the asset for the asset index, or null
   * if it doesn't exist.
   *
   *****************************************************/
  private ImageSpec getImageSpecAt( int index )
    {
    if ( index < 0 || index >= mImageSpecArrayList.size() ) return ( null );

    return ( mImageSpecArrayList.get( index ) );
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

        //don't take into account the last empty page
        if(assetIndex < mImageSpecArrayList.size())
          {
          //check if the page already contains an asset
          //border is covered by the image so it should be used only for empty pages ; alpha is useless for empty pages
          if (mImageSpecArrayList.get(assetIndex) != null)
            {
            newHighlightedCheckableImage.setAlpha(0.3f);
            }
          else
            {
            newHighlightedCheckableImage.setHighlightBorderSizePixels(resources.getDimensionPixelSize(R.dimen.checkable_image_highlight_border_size));
            newHighlightedCheckableImage.setHighlightBorderColour(resources.getColor(R.color.photobook_target_image_highlight));
            newHighlightedCheckableImage.setHighlightBorderShowing(true);
            }
          mCurrentlyHighlightedAssetIndex = assetIndex;
          }
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
        currentlyHighlightedCheckableImage.setAlpha(1f);
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
   * Front cover view holder.
   *
   *****************************************************/
  private class FrontCoverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                                View.OnLongClickListener
    {
    int                           imageIndex;

    FrameLayout                   mainLayout;
    FrameLayout                   imageFrameLayout;
    CheckableImageContainerFrame  checkableImageContainerFrame;
    ImageView                     addImageView;
    View                          imageGridView;
    ImageGridViewHolder           imageGridViewHolder;


    FrontCoverViewHolder( View view )
      {
      super( view );

      this.imageIndex                   = -1;

      this.mainLayout                   = (FrameLayout)view.findViewById( R.id.front_cover_main_layout );
      this.imageFrameLayout             = (FrameLayout)view.findViewById( R.id.cover_frame_layout );
      this.checkableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.checkable_image_container_frame );
      this.addImageView                 = (ImageView)view.findViewById( R.id.add_image_view );
      this.imageGridView                = view.findViewById( R.id.image_grid );

      this.imageGridViewHolder          = new ImageGridViewHolder( this.imageGridView );

      //set layout size according to the aspect ratio
      float height = view.getResources().getDimension(R.dimen.photobook_front_cover_height);
      float width  = height * mAspectRatio;
      ViewGroup.LayoutParams params = mainLayout.getLayoutParams();
      params.height = (int) height;
      params.width = (int) width;
      this.mainLayout.setLayoutParams(params);

      //compute padding parameters
      int topPadding = (int) ((height * 10) / 100);
      int bottomPadding = (int) ((height * 10) / 100);
      int leftPadding = (int) ((width * 14) / 100);
      int rightPadding = (int) ((width * 6) / 100);
      imageFrameLayout.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

      this.checkableImageContainerFrame.setOnClickListener(this);
      this.checkableImageContainerFrame.setOnLongClickListener( this );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      if ( ! mFrontCoverIsSummary )
        {
        if ( view == this.checkableImageContainerFrame )
          {
          if ( mInSelectionMode )
            {
            ImageSpec imageSpec = getImageSpecAt( this.imageIndex );

            if ( imageSpec != null )
              {
              if ( !mSelectedAssetIndexHashSet.contains( this.imageIndex ) )
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
      }


    ////////// View.OnLongClickListener Method(s) //////////

    @Override
    public boolean onLongClick( View view )
      {
      if ( ! mFrontCoverIsSummary )
        {
        if ( !mInSelectionMode )
          {
          if ( view == this.checkableImageContainerFrame && getImageSpecAt( FRONT_COVER_ASSET_INDEX ) != null )
            {
            mListener.onLongClickImage( this.imageIndex, this.checkableImageContainerFrame );

            return ( true );
            }
          }
        }

      return ( false );
      }

    }


  /*****************************************************
   *
   * Summary image grid view holder.
   *
   *****************************************************/
  private class ImageGridViewHolder
    {
    ImageView[]  gridImageViewArray;


    ImageGridViewHolder( View view )
      {
      this.gridImageViewArray = new ImageView[ COVER_SUMMARY_IMAGE_COUNT ];

      if ( view != null )
        {
        this.gridImageViewArray[ 0 ] = (ImageView) view.findViewById( R.id.grid_image_1 );
        this.gridImageViewArray[ 1 ] = (ImageView) view.findViewById( R.id.grid_image_2 );
        this.gridImageViewArray[ 2 ] = (ImageView) view.findViewById( R.id.grid_image_3 );
        this.gridImageViewArray[ 3 ] = (ImageView) view.findViewById( R.id.grid_image_4 );
        this.gridImageViewArray[ 4 ] = (ImageView) view.findViewById( R.id.grid_image_5 );
        this.gridImageViewArray[ 5 ] = (ImageView) view.findViewById( R.id.grid_image_6 );
        this.gridImageViewArray[ 6 ] = (ImageView) view.findViewById( R.id.grid_image_7 );
        this.gridImageViewArray[ 7 ] = (ImageView) view.findViewById( R.id.grid_image_8 );
        this.gridImageViewArray[ 8 ] = (ImageView) view.findViewById( R.id.grid_image_9 );
        }
      }
    }


  /*****************************************************
   *
   * Instructions view holder.
   *
   *****************************************************/
  private class InstructionsViewHolder extends RecyclerView.ViewHolder
    {
    InstructionsViewHolder( View view )
      {
          super(view);
          TextView instructions= (TextView) view.findViewById(R.id.photobook_instructions_text_view);

          String htmlInstructions = view.getResources().getString(R.string.kitesdk_multi_page_instructions_html);
          Spanned htmlAsSpanned = Html.fromHtml(htmlInstructions);

          instructions.setText(htmlAsSpanned);
      }
    }


  /*****************************************************
   *
   * Content view holder.
   *
   *****************************************************/
  private class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                             View.OnLongClickListener
    {
    int                           leftAssetIndex;
    int                           rightAssetIndex;

    FrameLayout                   mainLayout;
    FrameLayout                   leftFrameLayout;
    FrameLayout                   rightFrameLayout;

    CheckableImageContainerFrame  leftCheckableImageContainerFrame;
    CheckableImageContainerFrame  rightCheckableImageContainerFrame;

    ImageView                     leftAddImageView;
    ImageView                     rightAddImageView;

    TextView                      leftTextView;
    TextView                      rightTextView;


    ContentViewHolder( View view )
      {
      super( view );

      this.leftAssetIndex                    = -1;
      this.rightAssetIndex                   = -1;

      this.mainLayout                        = (FrameLayout) view.findViewById(R.id.main_layout_content);
      this.leftFrameLayout                   = (FrameLayout) view.findViewById(R.id.left_frame_layout);
      this.rightFrameLayout                  = (FrameLayout) view.findViewById(R.id.right_frame_layout);

      this.leftCheckableImageContainerFrame  = (CheckableImageContainerFrame)view.findViewById( R.id.left_checkable_image_container_frame );
      this.rightCheckableImageContainerFrame = (CheckableImageContainerFrame)view.findViewById( R.id.right_checkable_image_container_frame );

      this.leftAddImageView                  = (ImageView)view.findViewById( R.id.left_add_image_view );
      this.rightAddImageView                 = (ImageView)view.findViewById( R.id.right_add_image_view );

      this.leftTextView                      = (TextView)view.findViewById( R.id.left_text_view );
      this.rightTextView                     = (TextView)view.findViewById( R.id.right_text_view );

      //set main layout size according to the aspect ratio
      float height = view.getResources().getDimension(R.dimen.photobook_content_height);
      float width  = view.getResources().getDimension(R.dimen.photobook_content_width);
      height = height/mAspectRatio;
      ViewGroup.LayoutParams params = mainLayout.getLayoutParams();
      params.height = (int) height;
      params.width = (int) width;
      this.mainLayout.setLayoutParams(params);

      //compute padding parameters
      int topPadding = (int) ((height * 2.6)/100);
      int bottomPadding = (int) ((height * 2.6)/100);
      int outsidePadding = (int) ((width * 4)/100);
      leftFrameLayout.setPadding(outsidePadding, topPadding, 0, bottomPadding);
      rightFrameLayout.setPadding(0,topPadding, outsidePadding, bottomPadding);

      leftCheckableImageContainerFrame.setOnClickListener( this );
      leftCheckableImageContainerFrame.setOnLongClickListener( this );

      rightCheckableImageContainerFrame.setOnClickListener( this );
      rightCheckableImageContainerFrame.setOnLongClickListener( this );
      }


    ////////// View.OnClickListener Method(s) //////////

    @Override
    public void onClick( View view )
      {
      if ( view == this.leftCheckableImageContainerFrame )
        {
        if ( mInSelectionMode )
          {
          ImageSpec leftImageSpec = getImageSpecAt( this.leftAssetIndex );

          if ( leftImageSpec != null )
            {
            if ( ! mSelectedAssetIndexHashSet.contains( this.leftAssetIndex ) )
              {
              mSelectedAssetIndexHashSet.add( this.leftAssetIndex );

              this.leftCheckableImageContainerFrame.setChecked( true );
              }
            else
              {
              mSelectedAssetIndexHashSet.remove( this.leftAssetIndex );

              this.leftCheckableImageContainerFrame.setChecked( false );
              }

            onSelectedImagesChanged();
            }
          else
            {
            rejectAddImage( this.leftAddImageView );
            }
          }
        else
          {
          mListener.onClickImage( this.leftAssetIndex, view );
          }

        return;
        }


      if ( view == this.rightCheckableImageContainerFrame )
        {
        if ( mInSelectionMode )
          {
          ImageSpec rightImageSpec = getImageSpecAt( this.rightAssetIndex );

          if ( rightImageSpec != null )
            {
            if ( ! mSelectedAssetIndexHashSet.contains( this.rightAssetIndex ) )
              {
              mSelectedAssetIndexHashSet.add( this.rightAssetIndex );

              this.rightCheckableImageContainerFrame.setChecked( true );
              }
            else
              {
              mSelectedAssetIndexHashSet.remove( this.rightAssetIndex );

              this.rightCheckableImageContainerFrame.setChecked( false );
              }

            onSelectedImagesChanged();
            }
          else
            {
            rejectAddImage( this.rightAddImageView );
            }
          }
        else
          {
          mListener.onClickImage( this.rightAssetIndex, view );
          }

        return;
        }

      }


    ////////// View.OnLongClickListener Method(s) //////////

    @Override
    public boolean onLongClick( View view )
      {
      if ( ! mInSelectionMode )
        {
        if ( view == this.leftCheckableImageContainerFrame )
          {
          if ( getImageSpecAt( this.leftAssetIndex ) != null )
            {
            mListener.onLongClickImage( this.leftAssetIndex, this.leftCheckableImageContainerFrame );

            return ( true );
            }
          }
        else if ( view == this.rightCheckableImageContainerFrame )
          {
          if ( getImageSpecAt( this.rightAssetIndex ) != null )
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

