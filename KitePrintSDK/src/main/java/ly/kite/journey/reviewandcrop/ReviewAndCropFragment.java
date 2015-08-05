/*****************************************************
 *
 * ReviewAndCropFragment.java
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

package ly.kite.journey.reviewandcrop;


///// Import(s) /////


///// Class Declaration /////

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import ly.kite.R;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AKiteFragment;
import ly.kite.journey.AProductCreationFragment;
import ly.kite.product.AssetAndQuantity;
import ly.kite.product.Product;

/*****************************************************
 *
 * This fragment displays the review and crop screen
 * for images.
 *
 *****************************************************/
public class ReviewAndCropFragment extends AProductCreationFragment implements AssetAndQuantityAdaptor.IListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String  TAG                                = "ReviewAndCropFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private GridView                     mGridView;
  private AssetAndQuantityAdaptor      mAssetAndQuantityAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ReviewAndCropFragment newInstance( ArrayList<AssetAndQuantity> uncroppedAssetAndQuantityArrayList,
                                                   ArrayList<AssetAndQuantity> croppedAssetAndQuantityArrayList,
                                                   Product                     product )
    {
    ReviewAndCropFragment fragment = new ReviewAndCropFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelableArrayList( BUNDLE_KEY_UNCROPPED_ASSET_AND_QUANTITY_LIST, uncroppedAssetAndQuantityArrayList );
    arguments.putParcelableArrayList( BUNDLE_KEY_CROPPED_ASSET_AND_QUANTITY_LIST,   croppedAssetAndQuantityArrayList );
    arguments.putParcelable         ( BUNDLE_KEY_PRODUCT,                           product );

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


    // The super class will have retrieved any asset lists and product from the arguments, so
    // we just need to make sure we got them.

    if ( ! assetListsValid() ) return;

    if ( ! productIsValid() ) return;


    setTitle();
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_review_and_crop, container, false );

    mGridView = (GridView)view.findViewById( R.id.grid_view );


    // Create and set the adaptor
    mAssetAndQuantityAdaptor = new AssetAndQuantityAdaptor( mKiteActivity, mCroppedAssetAndQuantityArrayList, this );
    mGridView.setAdapter( mAssetAndQuantityAdaptor );


    return ( view );
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
      R.string.alert_dialog_title_delete_photo,
      R.string.alert_dialog_message_delete_photo,
      R.string.alert_dialog_delete_photo_confirm_text,
      new DeleteAssetRunnable( assetIndex ),
      R.string.alert_dialog_delete_photo_cancel_text,
      null
      );
    }


  /*****************************************************
   *
   * Called when the decrease button has been pressed to
   * take the quantity to zero.
   *
   *****************************************************/
  @Override
  public void onQuantityChanged( int assetIndex )
    {

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
    // TODO
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

    for ( AssetAndQuantity assetAndQuantity : mUncroppedAssetAndQuantityArrayList )
      {
      numberOfImages += assetAndQuantity.getQuantity();
      }


    int quantityPerPack = mProduct.getQuantityPerSheet();
    int numberOfPacks   = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;

    mKiteActivity.setTitle( getString( R.string.review_and_crop_title_format_string, numberOfImages, ( numberOfPacks * quantityPerPack ) ) );
    }


  ////////// Inner Class(es) //////////

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
      mUncroppedAssetAndQuantityArrayList.remove( mAssetIndex );
      mCroppedAssetAndQuantityArrayList.remove( mAssetIndex );

      // Update the screen
      mAssetAndQuantityAdaptor.notifyDataSetInvalidated();

      setTitle();
      }
    }

  }

