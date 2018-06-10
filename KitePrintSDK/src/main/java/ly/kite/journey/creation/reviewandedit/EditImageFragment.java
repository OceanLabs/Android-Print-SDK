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

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.R;
import ly.kite.journey.UserJourneyType;
import ly.kite.journey.creation.AEditImageFragment;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.widget.EditableMaskedImageView;


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
  static public final String TAG = "EditImageFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static EditImageFragment newInstance( Product product, AssetFragment imageAssetFragment )
    {
    return ( new EditImageFragment( product, imageAssetFragment ) );
    }


  ////////// Constructor(s) //////////

  public EditImageFragment()
    {
    }


  @SuppressLint("ValidFragment")
  private EditImageFragment( Product product, AssetFragment imageAssetFragment )
    {
    super( product, imageAssetFragment );
    }


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

    setHasOptionsMenu( true );
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


    // Set the text of the backwards (cancel) button. If it is blank - hide the button.

    String backwardsText = getString( R.string.kitesdk_edit_image_backwards_button_text);

    if ( backwardsText != null && backwardsText.trim().length() > 0 )
      {
      setBackwardsTextViewVisibility( View.VISIBLE );
      setBackwardsTextViewText( backwardsText );
      setBackwardsTextViewEnabled(true);
      setBackwardsTextViewOnClickListener(this);
      }
    else
      {
      setBackwardsTextViewVisibility( View.GONE );
      }


    setForwardsTextViewVisibility( View.VISIBLE );
    setForwardsTextViewText( R.string.kitesdk_edit_image_forwards_button_text);
    setForwardsTextViewBold( true );

    return ( view );
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


    if ( mEditableImageContainerFrame != null )
      {
      // Set up the editable image

      Resources resources = getResources();

      UserJourneyType                         userJourneyType = mProduct.getUserJourneyType();
      EditableMaskedImageView.BorderHighlight borderHighlight = userJourneyType.editBorderHighlight();

      mEditableImageContainerFrame
              .setImage( mUnmodifiedImageAssetFragment )
              .setMask( mProduct.getUserJourneyType().editMaskResourceId(), mProduct.getImageAspectRatio() )
              .setTranslucentBorderPixels( resources.getDimensionPixelSize( R.dimen.edit_image_translucent_border_size ) )
              .setBorderHighlight( borderHighlight, resources.getDimensionPixelSize( R.dimen.edit_image_border_highlight_size ) );

      if ( borderHighlight == EditableMaskedImageView.BorderHighlight.RECTANGLE )
        {
        mEditableImageContainerFrame.setCornerOverlays( R.drawable.corner_top_left, R.drawable.corner_top_right, R.drawable.corner_bottom_left, R.drawable.corner_bottom_right );
        }
      }
    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    onCreateOptionsMenu( menu, menuInflator, R.menu.edit_image );
    }


  /*****************************************************
   *
   * Called when the fragment is on top.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    mKiteActivity.setTitle( R.string.kitesdk_edit_image_title);
    }


  /*****************************************************
   *
   * Called when an edited asset is returned.
   *
   *****************************************************/
  protected void onEditingComplete( AssetFragment assetFragment )
    {
    if ( assetFragment != null && mKiteActivity instanceof ICallback )
      {
      ((ICallback)mKiteActivity).eiOnConfirm( assetFragment );
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
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).eiOnCancel();
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
    public void eiOnConfirm( AssetFragment assetFragment );
    }

  }

