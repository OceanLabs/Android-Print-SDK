/*****************************************************
 *
 * CheckableImageView.java
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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import ly.kite.R;
import ly.kite.util.IImageConsumer;


///// Class Declaration /////

/*****************************************************
 *
 * This class combines a standard Android image view with
 * an image consumer. As soon as the image becomes
 * available it is set.
 *
 *****************************************************/
public class CheckableImageView extends AFixableImageFrame implements IImageConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "CheckableImageView";

  private static final long    CHECK_ANIMATION_DURATION_MILLIS = 200L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean    mIsChecked;
  private ImageView  mCheckImageView;

  private Object     mExpectedKey;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public CheckableImageView( Context context )
    {
    super( context );
    }

  public CheckableImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public CheckableImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public CheckableImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );
    }


  ////////// AFixableImageFrame Method(s) //////////

  /*****************************************************
   *
   * Returns the content view.
   *
   *****************************************************/
  @Override
  protected View onCreateView( Context context )
    {
    LayoutInflater layoutInflator = LayoutInflater.from( context );

    View view = layoutInflator.inflate( R.layout.checkable_image_view, this, true );

    mCheckImageView = (ImageView)view.findViewById( R.id.check_image_view );

    return ( view );
    }


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when an image is downloading.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    // Ignore
    }


  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    if ( key.equals( mExpectedKey ) ) mImageView.setImageBitmap( bitmap );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the checked state.
   *
   *****************************************************/
  public void setChecked( boolean isChecked )
    {
    mIsChecked = isChecked;

    int visibility = ( isChecked ? View.VISIBLE : View.GONE );

    mCheckImageView.setAnimation( null );
    mCheckImageView.setVisibility( visibility );
    }


  /*****************************************************
   *
   * Sets the checked state, but animates any transition.
   *
   *****************************************************/
  public void transitionChecked( boolean isChecked )
    {
    boolean wasChecked = mIsChecked;

    setChecked( isChecked );

    Animation animation       = null;
    int       finalVisibility = 0;

    if ( ! wasChecked && isChecked )
      {
      ///// Animate in /////

      animation = new AlphaAnimation( 0f, 1f );

      finalVisibility = View.VISIBLE;
      }
    else if ( wasChecked && ! isChecked )
      {
      ///// Animate out /////

      animation = new AlphaAnimation( 1f, 0f );
      //animation.setFillAfter( true );

      finalVisibility = View.GONE;
      }


    if ( animation != null )
      {
      animation.setDuration( CHECK_ANIMATION_DURATION_MILLIS );
      animation.setAnimationListener( new VisibilitySettingAnimationListener( mCheckImageView, finalVisibility ) );

      mCheckImageView.startAnimation( animation );
      }

    }



  /*****************************************************
   *
   * Clears the image and sets the key for the next
   * expected image.
   *
   *****************************************************/
  public void clear( Object expectedKey )
    {
    setExpectedKey( expectedKey );

    mImageView.setImageBitmap( null );
    }


  /*****************************************************
   *
   * Clears the image and key.
   *
   *****************************************************/
  public void clear()
    {
    clear( null );
    }


  /*****************************************************
   *
   * Sets the key to expect.
   *
   *****************************************************/
  public void setExpectedKey( Object key )
    {
    mExpectedKey = key;
    }


  ////////// Inner Class(es) //////////

  }

