/*****************************************************
 *
 * GroupOrProductView.java
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ly.kite.R;
import ly.kite.util.RemoteImageConsumer;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a widget that displays a product item -
 * either a group or product. It allows images to be
 * supplied at a later stage, fading them in where
 * appropriate.
 *
 *****************************************************/
public class GroupOrProductView extends FrameLayout implements RemoteImageConsumer, Animation.AnimationListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                           = "GroupOrProductView";

  private static final float   DEFAULT_ASPECT_RATIO              = 1.0f;

  private static final long    FADE_IN_ANIMATION_DURATION_MILLIS = 300L;
  private static final float   ALPHA_TRANSPARENT                 = 0.0f;
  private static final float   ALPHA_OPAQUE                      = 1.0f;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ImageView     mEmptyFrameImageView;
  private ImageView     mImageView;
  private OverlayLabel  mOverlayLabel;
  private ProgressBar   mProgressBar;

  private float         mWidthToHeightMultiplier;

  private String        mExpectedImageURLString;

  private Animation     mFadeInAnimation;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public GroupOrProductView( Context context )
    {
    super( context );

    initialise( context );
    }

  public GroupOrProductView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public GroupOrProductView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public GroupOrProductView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Called to measure the view.
   *
   *****************************************************/
  @Override
  protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
    // When we are called - set the image view dimensions

    int widthMode = MeasureSpec.getMode( widthMeasureSpec );
    int widthSize = MeasureSpec.getSize( widthMeasureSpec );

    if ( widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY )
      {
      ViewGroup.LayoutParams imageLayoutParams = mImageView.getLayoutParams();

      imageLayoutParams.width  = widthSize;
      imageLayoutParams.height = (int)( widthSize * mWidthToHeightMultiplier );

      mImageView.setLayoutParams( imageLayoutParams );
      }


    super.onMeasure( widthMeasureSpec, heightMeasureSpec );
    }


  ////////// RemoteImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Sets the image, when it has come from memory.
   *
   *****************************************************/
  @Override
  public void onImageImmediate( Bitmap bitmap )
    {
    setImage( bitmap );

    mEmptyFrameImageView.setVisibility( View.GONE );
    }


  /*****************************************************
   *
   * Indicates that the image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading()
    {
    mEmptyFrameImageView.setVisibility( View.VISIBLE );
    mImageView.setVisibility( View.INVISIBLE );
    mOverlayLabel.setVisibility( View.INVISIBLE );
    mProgressBar.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Sets the image after it has been loaded.
   *
   *****************************************************/
  @Override
  public void onImageLoaded( String imageURLString, Bitmap bitmap )
    {
    // Make sure the image is the one we were expecting.
    synchronized ( this )
      {
      if ( mExpectedImageURLString == null || mExpectedImageURLString.equals( imageURLString ) )
        {
        setImage( bitmap );

        fadeImageIn();
        }
      }
    }


  ////////// Animation.AnimationListener Method(s) //////////

  /*****************************************************
   *
   * Called when an animation starts.
   *
   *****************************************************/
  @Override
  public void onAnimationStart( Animation animation )
    {
    // Ignore
    }


  /*****************************************************
   *
   * Called when an animation completes.
   *
   *****************************************************/
  @Override
  public void onAnimationEnd( Animation animation )
    {
    // For the image fade in animation, make the empty frame invisible it has finished.
    if ( animation == mFadeInAnimation )
      {
      mEmptyFrameImageView.setVisibility( View.GONE );
      }
    }


  /*****************************************************
   *
   * Called when an animation repeats.
   *
   *****************************************************/
  @Override
  public void onAnimationRepeat( Animation animation )
    {
    // Ignore
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises this product item image.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    setAspectRatio( DEFAULT_ASPECT_RATIO );


    // Inflate the layout and attach it to this view

    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.group_or_product_view, this, true );


    // Save references to the child views
    mEmptyFrameImageView = (ImageView)view.findViewById( R.id.empty_frame_image_view );
    mImageView           = (ImageView)view.findViewById( R.id.image_view );
    mOverlayLabel        = (OverlayLabel)view.findViewById( R.id.overlay_label );
    mProgressBar         = (ProgressBar)view.findViewById( R.id.progress_bar );


    // Set up the overlay label

    Resources resources = context.getResources();

    mOverlayLabel.setCornerRadius( resources.getDimension( R.dimen.group_or_product_label_corner_radius ) );
    mOverlayLabel.setBackgroundShadow(
            resources.getColor( R.color.group_or_product_label_shadow_colour ),
            resources.getDimension( R.dimen.group_or_product_label_shadow_blur_radius ),
            resources.getDimension( R.dimen.group_or_product_label_shadow_y_offset ) );
    }


  /*****************************************************
   *
   * Sets the aspect ratio for images.
   *
   *****************************************************/
  public void setAspectRatio( float aspectRatio )
    {
    mWidthToHeightMultiplier = 1.0f / aspectRatio;
    }


  /*****************************************************
   *
   * Sets the image.
   *
   *****************************************************/
  public void setImage( Bitmap bitmap )
    {
    mImageView.setImageBitmap( bitmap );

    mImageView.setVisibility( View.VISIBLE );
    mProgressBar.setVisibility( View.INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the label text.
   *
   *****************************************************/
  public void setLabel( String label )
    {
    mOverlayLabel.setText( label );
    mOverlayLabel.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Sets the label text and colour.
   *
   *****************************************************/
  public void setLabel( String label, int colour )
    {
    setLabel( label );

    mOverlayLabel.setBackgroundColor( colour );
    }


  /*****************************************************
   *
   * Sets an expected image URL.
   *
   *****************************************************/
  public void setExpectedImageURL( String imageURLString )
    {
    synchronized ( this )
      {
      mExpectedImageURLString = imageURLString;
      }
    }


  /*****************************************************
   *
   * Starts a fade-in animation on the image.
   *
   *****************************************************/
  private void fadeImageIn()
    {
    mFadeInAnimation = new AlphaAnimation( ALPHA_TRANSPARENT, ALPHA_OPAQUE );
    mFadeInAnimation.setDuration( FADE_IN_ANIMATION_DURATION_MILLIS );

    // We make the empty frame invisible only after the fade in animation has finished.
    mFadeInAnimation.setAnimationListener( this );

    mImageView.startAnimation( mFadeInAnimation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

