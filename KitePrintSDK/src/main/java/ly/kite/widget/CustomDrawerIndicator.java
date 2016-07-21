/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.kite.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a drawable that can be used as a menu indicator. It is supplied to resources -
 * a 'burger'-style menu icon and an arrow, and will animate between them.
 */
public class CustomDrawerIndicator extends Drawable
  {
  static private final String  LOG_TAG           = "DrawerIndicator";

  static private final boolean DEBUGGING_ENABLED = false;

  /**
   * Direction to make the arrow point towards the left.
   */
  public static final int ARROW_DIRECTION_LEFT = 0;

  /**
   * Direction to make the arrow point towards the right.
   */
  public static final int ARROW_DIRECTION_RIGHT = 1;

  /**
   * Direction to make the arrow point towards the start.
   */
  public static final int ARROW_DIRECTION_START = 2;

  /**
   * Direction to make the arrow point to the end.
   */
  public static final int ARROW_DIRECTION_END = 3;


  /** @hide */
  @IntDef({ ARROW_DIRECTION_LEFT, ARROW_DIRECTION_RIGHT,
          ARROW_DIRECTION_START, ARROW_DIRECTION_END })
  @Retention(RetentionPolicy.SOURCE)
  public @interface ArrowDirection
    {
    }

  private final Drawable mShowDrawable;
  private final Drawable mHideDrawable;

  // Whether we should mirror animation when animation is reversed.
  private boolean mVerticalMirror = false;

  // The interpolated version of the original progress
  private float mProgress;

  // The arrow direction
  private int mDirection = ARROW_DIRECTION_START;


  /**
   * @param context used to get the configuration for the drawable from
   */
  public CustomDrawerIndicator( Context context, int showDrawableResourceId, int hideDrawableResourceId )
    {
    Resources resources = context.getResources();

    mShowDrawable = resources.getDrawable( showDrawableResourceId );
    mHideDrawable = resources.getDrawable( hideDrawableResourceId );
    }


  public void setPosition( float position )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setPosition( position = " + position + " )" );

    if ( position == 1f )
      {
      setVerticalMirror( true );
      }
    else if ( position == 0f )
      {
      setVerticalMirror( false );
      }

    setProgress( position );
    }

  public float getPosition()
    {
    return getProgress();
    }


  /**
   * Set the arrow direction.
   */
  public void setDirection( @ArrowDirection int direction )
    {
    if ( direction != mDirection )
      {
      mDirection = direction;

      invalidateSelf();
      }
    }

  /**
   * Returns the arrow direction.
   */
  @ArrowDirection
  public int getDirection()
    {
    return mDirection;
    }

  /**
   * If set, canvas is flipped when progress reached to end and going back to start.
   */
  public void setVerticalMirror( boolean verticalMirror )
    {
    if ( mVerticalMirror != verticalMirror )
      {
      mVerticalMirror = verticalMirror;

      invalidateSelf();
      }
    }

  @Override
  public void draw( Canvas canvas )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "draw( canvas = " + canvas + " )" );

    Rect bounds = getBounds();

    final boolean flipToPointRight;
    switch ( mDirection )
      {
      case ARROW_DIRECTION_LEFT:
        flipToPointRight = false;
        break;
      case ARROW_DIRECTION_RIGHT:
        flipToPointRight = true;
        break;
      case ARROW_DIRECTION_END:
        flipToPointRight = DrawableCompat.getLayoutDirection( this )
                == ViewCompat.LAYOUT_DIRECTION_LTR;
        break;
      case ARROW_DIRECTION_START:
      default:
        flipToPointRight = DrawableCompat.getLayoutDirection( this )
                == ViewCompat.LAYOUT_DIRECTION_RTL;
        break;
      }

// The whole canvas rotates as the transition happens
    final float canvasRotate = lerp(
            flipToPointRight ? 0 : -180,
            flipToPointRight ? 180 : 0,
            mProgress );

    int centerX = bounds.centerX();
    int centerY = bounds.centerY();

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "  mVerticalMirror = " + mVerticalMirror + ", flipToPointRight = " + flipToPointRight + ", canvasRotate = " + canvasRotate );

    float canvasRotationForHideDrawable = 0f;

      canvasRotationForHideDrawable = canvasRotate * ( ( mVerticalMirror ^ flipToPointRight ) ? -1 : 1 );

    float canvasRotationForShowDrawable = canvasRotationForHideDrawable + 180;


    canvas.save();

    canvas.rotate( canvasRotationForShowDrawable, centerX, centerY );

    mShowDrawable.setBounds( bounds );
    mShowDrawable.setAlpha( (int)( ( 1f - mProgress ) * 255 ) );
    mShowDrawable.draw( canvas );

    canvas.restore();


    canvas.save();

    canvas.rotate( canvasRotationForHideDrawable, centerX, centerY );

    mHideDrawable.setBounds( bounds );
    mHideDrawable.setAlpha( (int)( mProgress * 255 ) );
    mHideDrawable.draw( canvas );

    canvas.restore();
    }

  @Override
  public void setAlpha( int alpha )
    {
    }

  @Override
  public void setColorFilter( ColorFilter colorFilter )
    {
    }

  @Override
  public int getIntrinsicHeight()
    {
    return ( Math.max( mShowDrawable.getIntrinsicHeight(), mHideDrawable.getIntrinsicHeight() ) );
    }

  @Override
  public int getIntrinsicWidth()
    {
    return ( Math.max( mShowDrawable.getIntrinsicWidth(), mHideDrawable.getIntrinsicWidth() ) );
    }

  @Override
  public int getOpacity()
    {
    return PixelFormat.TRANSLUCENT;
    }

  /**
   * Returns the current progress of the arrow.
   */
  @FloatRange(from = 0.0, to = 1.0)
  public float getProgress()
    {
    return mProgress;
    }

  /**
   * Set the progress of the arrow.
   *
   * <p>A value of {@code 0.0} indicates that the arrow should be drawn in it's starting
   * position. A value of {@code 1.0} indicates that the arrow should be drawn in it's ending
   * position.</p>
   */
  public void setProgress( @FloatRange(from = 0.0, to = 1.0) float progress )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setProgress( progress = " + progress + " )" );

    if ( mProgress != progress )
      {
      mProgress = progress;

      invalidateSelf();
      }
    }

  /**
   * Linear interpolate between a and b with parameter t.
   */
  private static float lerp( float a, float b, float t )
    {
    return a + ( b - a ) * t;
    }
  }