/*****************************************************
 *
 * EditBorderTextImageFragment.java
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

package ly.kite.journey.creation.reviewandedit;


///// Import(s) /////

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ly.kite.R;
import ly.kite.journey.creation.AEditImageFragment;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to edit an image that
 * also allows text on the border.
 *
 *****************************************************/
public class EditBorderTextImageFragment extends AEditImageFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public  final String  TAG                    = "EditBorderTextImageFragment";

  static private final String  BUNDLE_KEY_BORDER_TEXT = "borderText";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String    mInitialBorderText;

  private EditText  mBorderEditText;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static EditBorderTextImageFragment newInstance( Product product, AssetFragment imageAssetFragment, String borderText )
    {
    EditBorderTextImageFragment fragment = new EditBorderTextImageFragment( product, imageAssetFragment );

    if ( borderText != null ) fragment.getArguments().putString( BUNDLE_KEY_BORDER_TEXT, borderText );

    return ( fragment );
    }


  ////////// Constructor(s) //////////

  public EditBorderTextImageFragment()
    {
    }


  @SuppressLint("ValidFragment")
  private EditBorderTextImageFragment( Product product, AssetFragment imageAssetFragment )
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


    // The border text may have been changed since the screen was entered, so check for saved
    // text first, then fallback to any supplied as an argument.

    if ( savedInstanceState == null )
      {
      Bundle arguments = getArguments();

      mInitialBorderText = arguments.getString( BUNDLE_KEY_BORDER_TEXT );
      }


    setHasOptionsMenu( true );
    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    onCreateOptionsMenu( menu, menuInflator, R.menu.edit_border_text_image );
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = super.onCreateView( layoutInflator, R.layout.screen_edit_border_text_image, container, savedInstanceState );

    mBorderEditText = (EditText)view.findViewById( R.id.border_edit_text );


    if ( mInitialBorderText != null ) mBorderEditText.setText( mInitialBorderText );


    setBackwardsTextViewVisibility( View.GONE );

    setForwardsTextViewVisibility( View.VISIBLE );
    setForwardsTextViewText( R.string.kitesdk_edit_border_text_image_forwards_button_text);


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


    // If we haven't already got an image asset - look in the asset list. Always use the
    // last one in the list - the most recently selected.

    if ( mUnmodifiedImageAssetFragment == null )
      {
      int imageSpecCount = ( mImageSpecArrayList != null ? mImageSpecArrayList.size() : 0 );

      if ( imageSpecCount > 0 )
        {
        mUnmodifiedImageAssetFragment = mImageSpecArrayList.get( imageSpecCount - 1 ).getAssetFragment();
        }
      }


    if ( mEditableImageContainerFrame != null )
      {
      mEditableImageContainerFrame
              .setImage( mUnmodifiedImageAssetFragment )
              .setMask( R.drawable.filled_white_rectangle, mProduct.getImageAspectRatio() );
      }
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

    if ( mProduct != null ) mKiteActivity.setTitle( mProduct.getName() );
    }


  // We don't need to save the instance state - the edit text containing the border text will
  // do that automatically.


  /*****************************************************
   *
   * Uses the supplied asset for the photo.
   *
   * For new images, we remove the previous border text.
   *
   *****************************************************/
  @Override
  protected void useAssetForImage( AssetFragment assetFragment, boolean imageIsNew )
    {
    super.useAssetForImage( assetFragment, imageIsNew );

    if ( imageIsNew )
      {
      mBorderEditText.setText( null );
      }
    }


  /*****************************************************
   *
   * Called when an edited asset is returned.
   *
   *****************************************************/
  @Override
  protected void onEditingComplete( AssetFragment assetFragment )
    {
    if ( assetFragment != null && mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).btiOnForwards( assetFragment, mBorderEditText.getText().toString() );
      }
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void btiOnForwards( AssetFragment assetFragment, String borderText );
    }

  }

