/*****************************************************
 *
 * PromptTextFrame.java
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
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This widget is a frame layout that displays prompt text.
 * The text is animated in, and remains visible for a short
 * time, before being animated away again.
 *
 *****************************************************/
public class PromptTextFrame extends FrameLayout
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private   static final String  LOG_TAG                           = "PromptTextFrame";

  private   static final long    IN_ANIMATION_DURATION_MILLIS      = 500L;
  private   static final long    OUT_ANIMATION_DELAY_MILLIS        = 2000L;
  private   static final long    OUT_ANIMATION_DURATION_MILLIS     = 500L;

  private   static final float   ALPHA_TRANSPARENT                 = 0f;
  private   static final float   ALPHA_OPAQUE                      = 1f;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private TextView   mPromptTextView;

  private Animation  mInAnimation;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public PromptTextFrame( Context context )
    {
    super( context );

    initialise( context, null, 0 );
    }

  public PromptTextFrame( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context, attrs, 0 );
    }

  public PromptTextFrame( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PromptTextFrame( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context, attrs, defStyleAttr );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises this widget.
   *
   *****************************************************/
  private void initialise( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Inflate the view

    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.prompt_text_frame, this, true );

    mPromptTextView = (TextView)view.findViewById( R.id.prompt_text_view );


    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.PromptTextFrame, defaultStyle, defaultStyle );


      // If a prompt was defined in the XML then set it now, respecting any tags.

      TypedValue value = new TypedValue();

      String prompt = typedArray.getString( R.styleable.PromptTextFrame_promptText );

      if ( prompt != null )
        {
        mPromptTextView.setText( Html.fromHtml( prompt ) );
        }


      typedArray.recycle();
      }


    // Make the text invisible to start
    mPromptTextView.setVisibility( View.GONE );
    }


  /*****************************************************
   *
   * Starts the display cycle.
   *
   *****************************************************/
  public void startDisplayCycle()
    {
    mPromptTextView.setVisibility( View.VISIBLE );

    mInAnimation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0,
            Animation.RELATIVE_TO_SELF, 0,
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0 );

    mInAnimation.setDuration( IN_ANIMATION_DURATION_MILLIS );
    mInAnimation.setFillBefore( true );
    mInAnimation.setFillAfter( true );
    mInAnimation.setAnimationListener( new OutAnimationTrigger() );

    mPromptTextView.startAnimation( mInAnimation );
    }

  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Waits for a period of time, then displays the out
   * animation.
   *
   *****************************************************/
  private class OutAnimationTrigger implements Animation.AnimationListener, Runnable
    {
    @Override
    public void onAnimationStart( Animation animation )
      {
      // Ignore
      }


    /*****************************************************
     *
     * Called when the in animation has finished.
     *
     *****************************************************/
    @Override
    public void onAnimationEnd( Animation animation )
      {
      if ( animation == mInAnimation )
        {
        mInAnimation = null;

        // Delay before starting the out animation
        new Handler().postDelayed( this, OUT_ANIMATION_DELAY_MILLIS );
        }
      }

    @Override
    public void onAnimationRepeat( Animation animation )
      {
      // Ignore
      }


    /*****************************************************
     *
     * Called after a delay whilst the prompt text is displayed
     * on screen.
     *
     *****************************************************/
    @Override
    public void run()
      {
      // Create and start the out animation. Once the out animation
      // has finished, we clear the animation and hide the view.

      Animation outAnimation = new TranslateAnimation(
              Animation.RELATIVE_TO_SELF, 0,
              Animation.RELATIVE_TO_SELF, 0,
              Animation.RELATIVE_TO_SELF, 0,
              Animation.RELATIVE_TO_SELF, -1f );

      outAnimation.setDuration( OUT_ANIMATION_DURATION_MILLIS );
      outAnimation.setFillBefore( true );
      outAnimation.setFillAfter( true );

      outAnimation.setAnimationListener( new VisibilitySettingAnimationListener( mPromptTextView, View.GONE ) );

      mPromptTextView.startAnimation( outAnimation );
      }
    }

  }
