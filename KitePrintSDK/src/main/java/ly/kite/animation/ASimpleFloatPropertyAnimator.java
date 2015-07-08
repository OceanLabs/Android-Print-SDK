/*****************************************************
 *
 * ASimpleFloatPropertyAnimator.java
 *
 *
 * Copyright (c) 2012, JL
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Neither the name(s) of the copyright holder(s) nor the name(s) of its
 *       contributor(s) may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER(S) AND CONTRIBUTOR(S) "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER(S) OR CONTRIBUTOR(S) BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.animation;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


///// Import(s) /////


///// Class Declaration /////

/*****************************************************
 *
 * This class animates a single float property on the Android
 * UI thread.
 *
 *****************************************************/
public abstract class ASimpleFloatPropertyAnimator implements Runnable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG       = "ASimpleFloatPropertyAnimator";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private long                          mDurationMillis;
  private float                         mInitialValue;
  private float                         mFinalValue;
  private Interpolator                  mInterpolator;

  private float                         mValueDifference;
  private float                         mElapsedToProportionMultiplier;

  private long                          mStartTimeMillis;
  private long                          mEndTimeMillis;

  private boolean                       mAnimationHasBeenCancelled;
  private Handler                       mHandler;

  
  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////
  
  public ASimpleFloatPropertyAnimator( long durationMillis, float initialValue, float finalValue, Interpolator interpolator )
    {
    mDurationMillis                = durationMillis;
    mInitialValue                  = initialValue;
    mFinalValue                    = finalValue;
    mInterpolator                  = interpolator;

    mValueDifference               = mFinalValue - mInitialValue;
    mElapsedToProportionMultiplier = 1f / (float)durationMillis;

    mHandler = new Handler();
    }


  public ASimpleFloatPropertyAnimator( long durationMillis, float initialValue, float finalValue )
    {
    this( durationMillis, initialValue, finalValue, new LinearInterpolator() );
    }


    ////////// Runnable Method(s) //////////

  /*****************************************************
   *
   * Called for every 'frame' of the animation from the
   * UI thread.
   *
   *****************************************************/
  public void run()
    {
    // If we have been cancelled - return immediately and do not continue
    if ( mAnimationHasBeenCancelled ) return;


    // Get the current time
    long currentTimeMillis = SystemClock.elapsedRealtime();


    // If this is the first frame - initialise the animation
    if ( mStartTimeMillis <= 0 )
      {
      // Save the start time, and calculate the end time.
      mStartTimeMillis = currentTimeMillis;
      mEndTimeMillis   = currentTimeMillis + mDurationMillis;

      // Set the initial property value
      onSetValue( mInitialValue );
      }

    // If this is the last frame - finish
    else if ( currentTimeMillis >= mEndTimeMillis )
      {
      // Set the final property value
      onSetValue( mFinalValue );

      onAnimationComplete();

      // Don't re-post on the UI thread
      return;
      }

    // Otherwise this is an intermediate frame
    else
      {
      // Calculate the time fraction and the interpolated value
      float proportion = (float)( currentTimeMillis - mStartTimeMillis ) * mElapsedToProportionMultiplier;
      float value      = mInitialValue + ( mInterpolator.getInterpolation( proportion ) * mValueDifference );

      // Set the intermediate property value
      onSetValue( value );
      }

    // Re-post on the UI thread for the next frame
    mHandler.post( this );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Starts the animation.
   *
   *****************************************************/
  public void start()
    {
    mAnimationHasBeenCancelled = false;

    // Post ourselves onto the UI thread
    mHandler.post( this );
    }


  /*****************************************************
   *
   * Called to set the value during animation.
   *
   *****************************************************/
   public abstract void onSetValue( float value );


  /*****************************************************
   *
   * Called when the animation is complete.
   *
   *****************************************************/
  public void onAnimationComplete() {}


  /*****************************************************
   * 
   * Cancels the animation.
   * 
   *****************************************************/
  public void cancel()
    {
    mAnimationHasBeenCancelled = true;
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

