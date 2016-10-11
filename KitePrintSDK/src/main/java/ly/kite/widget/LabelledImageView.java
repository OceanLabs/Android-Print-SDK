/*****************************************************
 *
 * LabelledImageView.java
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
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import ly.kite.image.IImageConsumer;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a widget that displays a image potentially
 * with a label. It allows images to be supplied at a later
 * stage, fading them in where appropriate.
 *
 *****************************************************/
public class LabelledImageView extends AAREImageContainerFrame implements IImageConsumer, Animation.AnimationListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                      = "LabelledImageView";

  private static final int     IMAGE_ANCHOR_GRAVITY_NONE    = 0;
  private static final int     IMAGE_ANCHOR_GRAVITY_LEFT    = 1;
  private static final int     IMAGE_ANCHOR_GRAVITY_TOP     = 2;
  private static final int     IMAGE_ANCHOR_GRAVITY_RIGHT   = 3;
  private static final int     IMAGE_ANCHOR_GRAVITY_BOTTOM  = 4;

  private static final int     NO_FORCED_LABEL_COLOUR       = 0x00000000;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ImageView            mEmptyFrameImageView;
  private ImageView            mImageView;
  private OverlayLabel         mOverlayLabel;

  private int                  mForcedLabelColour;

  private ImageView.ScaleType  mImageViewScaleType;
  private int                  mImageViewAnchorGravity;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public LabelledImageView( Context context )
    {
    super( context );
    }

  public LabelledImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public LabelledImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LabelledImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
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
  protected View onCreateView( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Inflate the layout and attach it to this view

    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.labelled_image_view, this, true );


    // Save references to the child views
    mEmptyFrameImageView = (ImageView)view.findViewById( R.id.empty_frame_image_view );
    mImageView           = (ImageView)view.findViewById( R.id.image_view );
    mOverlayLabel        = (OverlayLabel)view.findViewById( R.id.overlay_label );

    // Apply any scale type
    if ( mImageViewScaleType != null && mImageView != null )
      {
      mImageView.setScaleType( mImageViewScaleType );
      }


    // Set up the overlay label

    Resources resources = context.getResources();

    mOverlayLabel.setCornerRadius( resources.getDimension( R.dimen.labelled_image_label_corner_radius ) );
    mOverlayLabel.setBackgroundShadow(
            resources.getColor( R.color.labelled_image_label_shadow ),
            resources.getDimension( R.dimen.labelled_image_label_shadow_blur_radius ),
            resources.getDimension( R.dimen.labelled_image_label_shadow_y_offset ) );


    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.LabelledImageView, defaultStyle, defaultStyle );


      if ( mImageView != null && mImageView instanceof AnchorableImageView )
        {
        AnchorableImageView anchorableImageView = (AnchorableImageView)mImageView;


        // See if there is an image anchor gravity. The SDK customiser overrides any XML defined value.

        if ( mImageViewAnchorGravity != Gravity.NO_GRAVITY )
          {
          anchorableImageView.setAnchorGravity( mImageViewAnchorGravity );
          }
        else
          {
          int imageAnchorGravity = typedArray.getInt( R.styleable.LabelledImageView_imageAnchorGravity, IMAGE_ANCHOR_GRAVITY_NONE );

          switch ( imageAnchorGravity )
            {
            case IMAGE_ANCHOR_GRAVITY_LEFT:
              anchorableImageView.setAnchorGravity( Gravity.LEFT );
              break;
            case IMAGE_ANCHOR_GRAVITY_TOP:
              anchorableImageView.setAnchorGravity( Gravity.TOP );
              break;
            case IMAGE_ANCHOR_GRAVITY_RIGHT:
              anchorableImageView.setAnchorGravity( Gravity.RIGHT );
              break;
            case IMAGE_ANCHOR_GRAVITY_BOTTOM:
              anchorableImageView.setAnchorGravity( Gravity.BOTTOM );
              break;
            }
          }


        // Check the image anchor point

        float imageAnchorPoint = typedArray.getFloat( R.styleable.LabelledImageView_imageAnchorPoint, 0f );

        anchorableImageView.setAnchorPoint( imageAnchorPoint );
        }


      // See if there is a forced label colour
      mForcedLabelColour = typedArray.getColor( R.styleable.LabelledImageView_forcedLabelColour, NO_FORCED_LABEL_COLOUR );

      // Check for label opacity
      mOverlayLabel.setLabelOpacity( typedArray.getFloat( R.styleable.LabelledImageView_labelOpacity, 1f ) );

      // See if the label shadow should be hidden
      mOverlayLabel.setHideLabelShadow( typedArray.getBoolean( R.styleable.LabelledImageView_hideLabelShadow, false ) );

      typedArray.recycle();
      }


    return ( view );
    }


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Indicates that the image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    super.onImageDownloading( key );

    mEmptyFrameImageView.setVisibility( View.VISIBLE );
    mImageView.setVisibility( View.INVISIBLE );

    showProgressSpinner();
    }


  ////////// Animation.AnimationListener Method(s) //////////

  /*****************************************************
   *
   * Called when an animation completes.
   *
   *****************************************************/
  @Override
  public void onAnimationEnd( Animation animation )
    {
    super.onAnimationEnd( animation );

    // We want to make the empty frame invisible after the
    // photo image fade in has finished.
    if ( animation == mFadeInAnimation )
      {
      mEmptyFrameImageView.setVisibility( View.GONE );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the scale type of the image view.
   *
   *****************************************************/
  public void setImageScaleType( ImageView.ScaleType scaleType )
    {
    mImageViewScaleType = scaleType;

    if ( scaleType != null && mImageView != null ) mImageView.setScaleType( scaleType );
    }


  /*****************************************************
   *
   * Sets the anchor point of the image view.
   *
   *****************************************************/
  public void setImageAnchorGravity( int gravity )
    {
    mImageViewAnchorGravity = gravity;

    if ( gravity != Gravity.NO_GRAVITY && mImageView != null && mImageView instanceof AnchorableImageView )
      {
      ( (AnchorableImageView)mImageView ).setAnchorGravity( gravity );
      }
    }


  /*****************************************************
   *
   * Forces the label background colour.
   *
   *****************************************************/
  public void setForcedLabelColour( int colour )
    {
    mForcedLabelColour = colour;

    mOverlayLabel.setBackgroundColor( colour );
    }


  /*****************************************************
   *
   * Sets the label text.
   *
   *****************************************************/
  public void setLabel( String label )
    {
    mOverlayLabel.setText( label );

    mOverlayLabel.setVisibility( label != null ? View.VISIBLE : View.INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the label text and colour.
   *
   *****************************************************/
  public void setLabel( String label, int colour )
    {
    setLabel( label );

    // Set the background colour. If we have a forced colour then use that instead of the
    // supplied one.
    mOverlayLabel.setBackgroundColor( mForcedLabelColour != NO_FORCED_LABEL_COLOUR ? mForcedLabelColour : colour );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

