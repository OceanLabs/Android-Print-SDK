/*****************************************************
 *
 * AEditImageFragment.java
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

package ly.kite.journey;


///// Import(s) /////

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ly.kite.R;
import ly.kite.product.Asset;
import ly.kite.product.AssetHelper;
import ly.kite.product.Product;
import ly.kite.widget.EditableImageContainerFrame;
import ly.kite.widget.PromptTextFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent of fragments that allow users to
 * edit an image to fit within a mask.
 *
 *****************************************************/
abstract public class AEditImageFragment extends AProductCreationFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                        = "AEditImageFragment";

  public  static final String      BUNDLE_KEY_PRODUCT             = "product";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Product                      mProduct;

  protected EditableImageContainerFrame  mEditableImageContainerFrame;
  protected Button                       mCancelButton;
  protected Button                       mConfirmButton;
  private   PromptTextFrame              mPromptTextFrame;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AKiteFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the assets and product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_no_arguments,
              R.string.alert_dialog_message_no_arguments,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
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
    View view = layoutInflator.inflate( R.layout.screen_edit_image, container, false );

    mEditableImageContainerFrame = (EditableImageContainerFrame)view.findViewById( R.id.editable_consumer_image_view );
    mCancelButton                = (Button)view.findViewById( R.id.cancel_button );
    mConfirmButton               = (Button)view.findViewById( R.id.confirm_button );
    mPromptTextFrame             = (PromptTextFrame)view.findViewById( R.id.prompt_text_frame );


    mCancelButton.setOnClickListener( this );
    mConfirmButton.setOnClickListener( this );


    return ( view );
    }


  /*****************************************************
   *
   * Called when the activitiy has been created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // Only show the prompt animation the first time this
    // screen is displayed.

    if ( savedInstanceState == null &&
         mPromptTextFrame   != null )
      {
      mPromptTextFrame.startDisplayCycle();
      }
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when something is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mCancelButton )
      {
      ///// Cancel /////

      onCancel();
      }
    else if ( view == mConfirmButton )
      {
      ///// Confirm /////

      onConfirm();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the cancel button is clicked.
   *
   *****************************************************/
  protected void onCancel()
    {
    }


  /*****************************************************
   *
   * Called when the confirm button is clicked.
   *
   *****************************************************/
  abstract protected void onConfirm();


  /*****************************************************
   *
   * Gets the cropped image and creates a file-backed
   * asset from it.
   *
   *****************************************************/
  protected Asset getEditedImageAsset()
    {
    if ( mEditableImageContainerFrame == null ) return ( null );


    Bitmap editedImageBitmap = mEditableImageContainerFrame.getEditableImageView().getImageCroppedToMask();


    // Sometimes users can hit the next button before we've actually got all the images, so check
    // for this.

    if ( editedImageBitmap == null )
      {
      Log.w( LOG_TAG, "Cropped image not yet available" );

      return ( null );
      }


    // Create the cropped image asset as a file. We always create file-backed assets within
    // the SDK so we don't hit problems with transaction sizes when passing assets through
    // intents.

    Asset editedImageAsset = AssetHelper.createAsCachedFile( mKiteActivity, editedImageBitmap );

    if ( editedImageAsset == null )
      {
      Log.e( LOG_TAG, "Could not create edited image asset" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_create_order,
              R.string.alert_dialog_message_no_cropped_image_asset,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              null );

      return ( null );
      }


    return ( editedImageAsset );
    }


  ////////// Inner Class(es) //////////

  }

