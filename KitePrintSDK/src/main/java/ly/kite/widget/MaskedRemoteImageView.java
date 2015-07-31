/*****************************************************
 *
 * MaskedRemoteImageView.java
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

package ly.kite.widget;


///// Import(s) /////

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import ly.kite.R;
import ly.kite.product.Bleed;
import ly.kite.util.IImageConsumer;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a view that displays a mask over another
 * image (such as a photo). The underlying photo/image
 * may be moved and zoomed.
 *
 *****************************************************/
public class MaskedRemoteImageView extends FrameLayout implements IImageConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "MaskedRemoteImageView";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private MaskedImageView  mMaskedImageView;
  private ProgressBar      mProgressBar;

  private Object           mImageKey;
  private Object           mMaskKey;
  private Bleed            mMaskBleed;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MaskedRemoteImageView( Context context )
    {
    super( context );

    initialise( context );
    }

  public MaskedRemoteImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public MaskedRemoteImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public MaskedRemoteImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Called when the view size changes.
   *
   *****************************************************/
  @Override
  public void onSizeChanged( int width, int height, int oldWidth, int oldHeight )
    {
    super.onSizeChanged( width, height, oldWidth, oldHeight );
    }


  /*****************************************************
   *
   * Draws the view.
   *
   *****************************************************/


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the remote image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    mProgressBar.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Called when the remote image has been loaded.
   *
   *****************************************************/
  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    if ( key == mImageKey ) mMaskedImageView.setImageBitmap( bitmap );
    if ( key == mMaskKey  ) mMaskedImageView.setMask( bitmap, mMaskBleed );


    // If both images have been downloaded - remove the progress spinner

    if ( mMaskedImageView.bothBitmapsAvailable() )
      {
      mProgressBar.setVisibility( View.GONE );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.masked_remote_image_view, this, true );

    mMaskedImageView = (MaskedImageView)view.findViewById( R.id.masked_image_view );
    mProgressBar     = (ProgressBar)view.findViewById( R.id.progress_bar );
    }


  /*****************************************************
   *
   * Sets the key for the image request.
   *
   *****************************************************/
  public void setImageKey( Object key )
    {
    mImageKey = key;
    }


  /*****************************************************
   *
   * Sets the request key and bleed for the mask.
   *
   *****************************************************/
  public void setMaskDetails( Object key, Bleed maskBleed )
    {
    mMaskKey   = key;
    mMaskBleed = maskBleed;
    }


  /*****************************************************
   *
   * Returns the masked image view.
   *
   *****************************************************/
  public MaskedImageView getMaskedImageView()
    {
    return ( mMaskedImageView );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

