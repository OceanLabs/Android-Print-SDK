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

package ly.kite.journey.reviewandedit;


///// Import(s) /////


///// Class Declaration /////

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.journey.AProductCreationFragment;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.product.Asset;
import ly.kite.product.Product;

/*****************************************************
 *
 * This fragment displays the review and edit screen
 * for images.
 *
 *****************************************************/
public class ReviewAndEditFragment extends AProductCreationFragment implements AssetAndQuantityAdaptor.IListener,
                                                                               View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String  TAG                                = "ReviewAndEditFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private GridView                     mGridView;
  private Button                       mProceedOverlayButton;

  private AssetAndQuantityAdaptor      mAssetAndQuantityAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ReviewAndEditFragment newInstance( ArrayList<AssetsAndQuantity> assetsAndQuantityArrayList,
                                                   Product                      product )
    {
    ReviewAndEditFragment fragment = new ReviewAndEditFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST, assetsAndQuantityArrayList );
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


    // The super class will have retrieved any asset lists and product from the arguments, so
    // we just need to make sure we got them.

    if ( ! assetListValid() ) return;

    if ( ! productIsValid() ) return;


    setTitle();

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

    mGridView             = (GridView)view.findViewById( R.id.grid_view );
    mProceedOverlayButton = (Button)view.findViewById( R.id.proceed_overlay_button );


    mProceedOverlayButton.setText( R.string.review_and_edit_proceed_button_text );

    mProceedOverlayButton.setOnClickListener( this );


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
    mAssetAndQuantityAdaptor = new AssetAndQuantityAdaptor( mKiteActivity, mAssetsAndQuantityArrayList, mProduct, this );
    mGridView.setAdapter( mAssetAndQuantityAdaptor );
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
    // when not on this screen.

    if ( mGridView != null ) mGridView.setAdapter( null );

    mAssetAndQuantityAdaptor = null;
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
    if ( view == mProceedOverlayButton )
      {
      ///// Confirm /////

      if ( mKiteActivity instanceof ICallback )
        {
        ( (ICallback)mKiteActivity ).reOnConfirm();
        }
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

    for ( AssetsAndQuantity assetsAndQuantity : mAssetsAndQuantityArrayList )
      {
      numberOfImages += assetsAndQuantity.getQuantity();
      }


    int quantityPerPack = mProduct.getQuantityPerSheet();
    int numberOfPacks   = ( numberOfImages + ( quantityPerPack - 1 ) ) / quantityPerPack;

    mKiteActivity.setTitle( getString( R.string.review_and_edit_title_format_string, numberOfImages, ( numberOfPacks * quantityPerPack ) ) );
    }


  /*****************************************************
   *
   * Updates the assets and quantity.
   *
   *****************************************************/
  public void onAssetUpdated( int assetIndex, AssetsAndQuantity assetsAndQuantity )
    {
    mAssetsAndQuantityArrayList.set( assetIndex, assetsAndQuantity );

    if ( mAssetAndQuantityAdaptor != null ) mAssetAndQuantityAdaptor.notifyDataSetInvalidated();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void reOnEdit( int assetIndex );
    public void reOnConfirm();
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
      mAssetsAndQuantityArrayList.remove( mAssetIndex );

      // Update the screen
      mAssetAndQuantityAdaptor.notifyDataSetInvalidated();

      setTitle();
      }
    }

  }

