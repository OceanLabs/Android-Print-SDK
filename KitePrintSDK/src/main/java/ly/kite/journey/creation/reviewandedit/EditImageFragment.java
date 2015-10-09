/*****************************************************
 *
 * EditImageFragment.java
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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.R;
import ly.kite.journey.creation.AEditImageFragment;
import ly.kite.journey.AKiteActivity;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Product;
import ly.kite.util.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment allows the user to edit an image.
 *
 *****************************************************/
public class EditImageFragment extends AEditImageFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                     = "EditImageFragment";

  public  static final String      BUNDLE_KEY_UNEDITED_ASSET   = "uneditedAsset";
  public  static final String      BUNDLE_KEY_MASK_RESOURCE_ID = "maskResourceId";
  public  static final String      BUNDLE_KEY_PRODUCT          = "product";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Asset  mAsset;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static EditImageFragment newInstance( Asset uneditedAsset, Product product )
    {
    EditImageFragment fragment = new EditImageFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_UNEDITED_ASSET, uneditedAsset );
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AEditImageFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // We have already obtained the product in the parent fragment


    // Get the asset and mask

    Bundle arguments = getArguments();

    if ( arguments != null )
      {
      mAsset = arguments.getParcelable( BUNDLE_KEY_UNEDITED_ASSET );

      if ( mAsset == null )
        {
        Log.e( LOG_TAG, "No asset found" );

        mKiteActivity.displayModalDialog(
                R.string.alert_dialog_title_no_asset,
                R.string.alert_dialog_message_no_asset,
                AKiteActivity.NO_BUTTON,
                null,
                R.string.Cancel,
                mKiteActivity.new FinishRunnable()
        );

        return;
        }
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
    View view = super.onCreateView( layoutInflator, container, savedInstanceState );


    mCancelButton.setVisibility( View.VISIBLE );
    mCancelButton.setText( R.string.Cancel );

    mConfirmButton.setVisibility( View.VISIBLE );
    mConfirmButton.setText( R.string.OK );


    return ( view );
    }


  /*****************************************************
   *
   * Called when the fragment is on top.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    mKiteActivity.setTitle( R.string.edit_image_title );
    }


  /*****************************************************
   *
   * Called after the activity is created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // Request the image and set the mask

    ImageAgent imageManager = ImageAgent.getInstance( mKiteActivity );

    mEditableImageContainerFrame.setImageKey( mAsset );
    mEditableImageContainerFrame.setMask( mProduct.getUserJourneyType().maskResourceId(), mProduct.getImageAspectRatio() );

    // When we have the image - kick off the prompt text display cycle if this is the first time
    // we're loading an image.
    AssetHelper.requestImage( mKiteActivity, mAsset, new PromptTextTrigger( mEditableImageContainerFrame ) );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the cancel button is clicked.
   *
   *****************************************************/
  protected void onCancel()
    {
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).eiOnCancel();
      }
    }


  /*****************************************************
   *
   * Called when the confirm button is clicked.
   *
   *****************************************************/
  protected void onConfirm()
    {
    Asset croppedImageAsset = getEditedImageAsset();

    if ( croppedImageAsset == null ) return;


    if ( mKiteActivity instanceof ICallback )
      {
      ((ICallback) mKiteActivity).eiOnConfirm( croppedImageAsset );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void eiOnCancel();
    public void eiOnConfirm( Asset editedAsset );
    }

  }

