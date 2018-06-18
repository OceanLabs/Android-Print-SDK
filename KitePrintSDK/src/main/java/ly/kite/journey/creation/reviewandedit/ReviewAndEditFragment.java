/*****************************************************
 *
 * ReviewAndEditFragment.java
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

package ly.kite.journey.creation.reviewandedit;


///// Import(s) /////


///// Class Declaration /////

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.journey.creation.AProductCreationFragment;
import ly.kite.catalogue.Product;
import ly.kite.journey.creation.IUpdatedImageListener;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.ViewToBitmap;

/*****************************************************
 *
 * This fragment displays the review and edit screen
 * for images.
 *
 *****************************************************/
public class ReviewAndEditFragment extends AProductCreationFragment implements ImageSpecAdaptor.IListener,
                                                                               View.OnClickListener,
                                                                               IUpdatedImageListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String  TAG = "ReviewAndEditFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private GridView          mGridView;
  private Parcelable        mGridViewState;
  //private TextView          mProceedOverlayTextView;
  private TextView          mForwardsTextView;

  private ImageSpecAdaptor  mImageSpecAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ReviewAndEditFragment newInstance( Product product )
    {
    ReviewAndEditFragment fragment = new ReviewAndEditFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AJourneyFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( getActivity() ).trackProductOrderReviewScreenViewed( mProduct );
      }
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_review_and_edit, container, false );

    super.onViewCreated( view );

    mGridView         = (GridView)view.findViewById( R.id.grid_view );
    mForwardsTextView = setForwardsTextViewText( R.string.kitesdk_review_and_edit_proceed_button_text);


    setForwardsTextViewOnClickListener( this );


    return ( view );
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

    // Create and set the adaptor
    mImageSpecAdaptor = new ImageSpecAdaptor( mKiteActivity, mImageSpecArrayList, mProduct, this );
    mGridView.setAdapter( mImageSpecAdaptor );

    // restore gridview state
    if ( mGridViewState != null )
      {
      mGridView.onRestoreInstanceState( mGridViewState );
      mGridViewState = null;
      }

    setTitle(); // restore title
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
    // when not on this screen. Save state so we can return
    // to correct scroll offset in onTop

    if ( mGridView != null )
      {
      mGridViewState = mGridView.onSaveInstanceState();

      mGridView.setAdapter( null );
      }

    mImageSpecAdaptor = null;
    }




  ////////// AssetAndQuantityAdaptor.IListener Method(s) //////////

  /*****************************************************
   *
   * Called when the decrease button has been pressed to
   * take the quantity to zero.
   *
   *****************************************************/
  @Override
  public void onWantsToBeZero( int assetIndex )
    {
    // Display a dialog confirming whether the user wants to
    // delete the photo. If the user cancels - the quantity has
    // been left at one.

    mKiteActivity.displayModalDialog(
      R.string.kitesdk_alert_dialog_title_delete_photo,
      R.string.kitesdk_alert_dialog_message_delete_photo,
      R.string.kitesdk_alert_dialog_delete_photo_confirm_text,
      new DeleteAssetRunnable( assetIndex ),
      R.string.kitesdk_alert_dialog_delete_photo_cancel_text,
      null
      );
    }


  /*****************************************************
   *
   * Called when the quantity of an asset changes.
   *
   *****************************************************/
  @Override
  public void onQuantityChanged( int assetIndex )
    {
    setTitle();
    }


  /*****************************************************
   *
   * Called when the edit button for an asset has been
   * clicked.
   *
   *****************************************************/
  @Override
  public void onEdit( int assetIndex )
    {
    // Launch the edit screen for the chosen asset

    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).reOnEdit( assetIndex );
      }
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
    if ( view == mForwardsTextView )
      {
      ///// Confirm /////

      if ( mKiteActivity instanceof ICallback )
        {
        // firstly check if number of images user has selected meets expectations, if not
        // prompt the user for confirmation to continue
        int numberOfImages = 0;

        for ( ImageSpec imageSpec : mImageSpecArrayList )
          {
          numberOfImages += imageSpec.getQuantity();
          }

        int quantityPerPack = mProduct.getQuantityPerSheet();
        int numberOfPacks = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;
        int expectedNumberOfImages = numberOfPacks * quantityPerPack;
        if ( numberOfImages < expectedNumberOfImages )
          {
          displayNotFullDialog( expectedNumberOfImages, numberOfImages, new CallbackConfirmRunnable() );
          }
        else
          {
           //Get preview image
           Bitmap originalBitmap = ViewToBitmap.getItemBitmap(mGridView, 0);
           Bitmap resizedBitmap = ViewToBitmap.resizeAsPreviewImage( mKiteActivity, originalBitmap );
           Bitmap cleanBitmap = ViewToBitmap.removeBitmapBlankSpace( resizedBitmap );
           //Store preview in the first non-null imageSpec element
           for( int index = 0; index < mImageSpecArrayList.size(); index++ )
            {
            if (mImageSpecArrayList.get( index ) != null )
              {
              mImageSpecArrayList.get( index ).setPreviewImage( cleanBitmap );
              break;
              }
            }
          ( (ICallback) mKiteActivity ).reOnConfirm();
          }
        }
      }
    }


  ////////// IUpdatedAssetListener Method(s) //////////

  /*****************************************************
   *
   * Updates the assets and quantity.
   *
   *****************************************************/
  @Override
  public void onImageUpdated( int imageIndex, ImageSpec imageSpec )
    {
    if ( mImageSpecAdaptor != null )
      {
      mImageSpecAdaptor.notifyDataSetInvalidated();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Updates the title.
   *
   *****************************************************/
  private void setTitle()
    {
    // Calculate the total number of images

    int numberOfImages = 0;

    for ( ImageSpec imageSpec : mImageSpecArrayList )
      {
      numberOfImages += imageSpec.getQuantity();
      }


    int quantityPerPack = mProduct.getQuantityPerSheet();
    int numberOfPacks   = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;

    mKiteActivity.setTitle( getString( R.string.kitesdk_review_and_edit_title_format_string, numberOfImages, ( numberOfPacks * quantityPerPack ) ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void reOnEdit( int imageIndex );
    public void reOnConfirm();
    }


  /*****************************************************
   *
   * Calls back to the activity.
   *
   *****************************************************/
  private class CallbackConfirmRunnable implements Runnable
    {
    @Override
    public void run()
      {
      //Get preview image
      Bitmap originalBitmap = ViewToBitmap.getItemBitmap(mGridView, 0);
      Bitmap resizedBitmap = ViewToBitmap.resizeAsPreviewImage( mKiteActivity, originalBitmap );
      Bitmap cleanBitmap = ViewToBitmap.removeBitmapBlankSpace( resizedBitmap );
      //Store preview in the first non-null imageSpec element
      for( int index = 0; index < mImageSpecArrayList.size(); index++ )
        {
        if (mImageSpecArrayList.get( index ) != null )
          {
          mImageSpecArrayList.get( index ).setPreviewImage( cleanBitmap );
          break;
          }
        }
      ( (ICallback)mKiteActivity ).reOnConfirm();
      }
    }


  /*****************************************************
   *
   * Deletes an asset.
   *
   *****************************************************/
  private class DeleteAssetRunnable implements Runnable
    {
    private int  mAssetIndex;


    DeleteAssetRunnable( int assetIndex )
      {
      mAssetIndex = assetIndex;
      }


    @Override
    public void run()
      {
      // Remove the asset
      mImageSpecArrayList.remove( mAssetIndex );

      // Update the screen
      mImageSpecAdaptor.notifyDataSetInvalidated();

      setTitle();
      }
    }

  }

