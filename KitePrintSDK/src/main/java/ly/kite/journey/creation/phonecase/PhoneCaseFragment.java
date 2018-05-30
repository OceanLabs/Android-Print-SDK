/*****************************************************
 *
 * PhoneCaseFragment.java
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

package ly.kite.journey.creation.phonecase;


///// Import(s) /////

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.R;
import ly.kite.journey.creation.AEditImageFragment;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.util.ViewToBitmap;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class PhoneCaseFragment extends AEditImageFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "PhoneCaseFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static PhoneCaseFragment newInstance( Product product )
    {
    return ( new PhoneCaseFragment( product ) );
    }


  ////////// Constructor(s) //////////

  public PhoneCaseFragment()
    {
    }


  @SuppressLint("ValidFragment")
  private PhoneCaseFragment( Product product )
    {
    super( product );
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
   * Called the first time the options menu is created.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    onCreateOptionsMenu( menu, menuInflator, R.menu.phone_case );
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

    setBackwardsTextViewVisibility( View.GONE );

    setForwardsTextViewVisibility( View.VISIBLE );
    setForwardsTextViewText( R.string.phone_case_proceed_button_text );

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
      Resources resources = getResources();

      TypedValue anchorPointValue = new TypedValue();

      resources.getValue( R.dimen.edit_phone_case_anchor_point, anchorPointValue, true );

      mEditableImageContainerFrame
              .setImage( mUnmodifiedImageAssetFragment )
              .setMask( mProduct.getMaskURL(), mProduct.getMaskBleed() )
              .setMaskBlendMode( mProduct.getMaskBlendMode() )
              .setUnderImages( mProduct.getUnderImageURLList() )
              .setOverImages( mProduct.getOverImageURLList() )
              .setAnchorPoint( anchorPointValue.getFloat() );
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


  /*****************************************************
   *
   * Called when an edited asset is returned.
   *
   *****************************************************/
  @Override
  protected void onEditingComplete( AssetFragment assetFragment )
    {
    // Fetch the preview image and resize it
    Bitmap originalBitmap = mEditableImageContainerFrame.getPreviewBitmap();
    Bitmap cleanBitmap = ViewToBitmap.removeBitmapBlankSpace(originalBitmap);
    Bitmap bitmap = ViewToBitmap.resizeAsPreviewImage( mKiteActivity, cleanBitmap );
    mImageSpecArrayList.get(0).setPreviewImage( bitmap );

    assetFragment.setAssetPreviewBitmap(ViewToBitmap.resizeAsPreviewImage(mKiteActivity, bitmap));

    if ( assetFragment != null && mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pcOnCreated( assetFragment );
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
    public void pcOnCreated( AssetFragment assetFragment );
    }

  }

