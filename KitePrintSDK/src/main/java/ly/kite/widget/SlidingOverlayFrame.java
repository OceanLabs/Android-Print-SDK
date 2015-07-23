/*****************************************************
 *
 * SlidingOverlayFrame.java
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

package ly.kite.widget;


///// Import(s) /////

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a frame layout that contains a slider. The slider consists of
 * two parts: a part that remains visible when the rest of the slider is hidden,
 * and a normally hidden part that is revealed when the slider is animated out.
 *
 *****************************************************/
public class SlidingOverlayFrame extends FrameLayout implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String           LOG_TAG                                 = "SlidingOverlayFrame";
  @SuppressWarnings( "unused" )
  private static final int              LOGGING_LEVEL                           = 0;

  private static final ExpandDirection  DEFAULT_EXPAND_DIRECTION                = ExpandDirection.UP;
  private static final long             DEFAULT_SLIDE_ANIMATION_DURATION_MILLIS = 500L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ExpandDirection  mExpandDirection;
  private long             mSlideAnimationDurationMillis;

  private LinearLayout     mSliderLayout;
  private View             mAlwaysVisibleView;
  private View             mRevealedView;

  private Interpolator     mAnimationInterpolator;

  private boolean          mSliderIsExpanded;
  private boolean          mSliderIsAnimating;

  private IListener        mListener;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public SlidingOverlayFrame( Context context )
    {
    super( context );

    initialise( context, null, 0 );
    }

  public SlidingOverlayFrame( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context, attrs, 0 );
    }

  public SlidingOverlayFrame( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );

    initialise( context, attrs, defStyle );
    }


  ////////// View Method(s) //////////


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked. Used to listen for
   * clicks in the always visible and revealed views.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    // If we have no listener, then we don't actually care
    // if anything has been clicked.
    if ( mListener == null ) return;

    // Check which view was clicked
    if ( view == mAlwaysVisibleView )
      {
      mListener.onAlwaysVisibleViewClicked( view );
      }
    else if ( view == mRevealedView )
      {
      mListener.onRevealedViewClicked( view );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the slider frame.
   *
   *****************************************************/
  private void initialise( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Create the slider as a linear layout with the appropriate
    // orientation.
    mSliderLayout = new LinearLayout( context );

    // Set default values
    mExpandDirection              = DEFAULT_EXPAND_DIRECTION;
    mSlideAnimationDurationMillis = DEFAULT_SLIDE_ANIMATION_DURATION_MILLIS;
    mAlwaysVisibleView            = new View( context );
    mRevealedView                 = new View( context );


    // If we have attributes, try and getCost the ones we are interested in.

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.SlidingOverlayFrame, defaultStyle, defaultStyle );

      mExpandDirection              = ExpandDirection.values()[ typedArray.getInteger( R.styleable.SlidingOverlayFrame_expandDirection, DEFAULT_EXPAND_DIRECTION.ordinal() ) ];
      mSlideAnimationDurationMillis = typedArray.getInteger( R.styleable.SlidingOverlayFrame_slideAnimationDurationMillis, (int)DEFAULT_SLIDE_ANIMATION_DURATION_MILLIS );

      setAlwaysVisibleView( typedArray.getResourceId( R.styleable.SlidingOverlayFrame_alwaysVisibleView, 0 ) );
      setRevealedView     ( typedArray.getResourceId( R.styleable.SlidingOverlayFrame_revealedView, 0 ) );

      typedArray.recycle();
      }


    setUpViews();
    }


  /*****************************************************
   *
   * Sets up the views.
   *
   *****************************************************/
  private void setUpViews()
    {
    // Set the slider orientation according to the expand direction
    mSliderLayout.setOrientation( mExpandDirection.getLinearLayoutOrientation() );

    // Add or replace the whole slider layout in this frame layout
    addView( mSliderLayout, 0, mExpandDirection.getSliderLayoutParams() );


    ///// Always visible view /////

    // If we haven't been provided a view - create a dummy one
    View alwaysVisibleView = ( mAlwaysVisibleView != null ? mAlwaysVisibleView : new View( getContext() ) );

    setAlwaysVisibleView( alwaysVisibleView );


    ///// Revealed view /////

    View revealedView = ( mRevealedView != null ? mRevealedView : new View( getContext() ));

    setRevealedView( revealedView );
    }


  /*****************************************************
   *
   * Sets the direction.
   *
   *****************************************************/
  public void setExpandDirection( ExpandDirection expandDirection )
    {
    mExpandDirection = expandDirection;

    setUpViews();
    }


  /*****************************************************
   *
   * Returns the orientation.
   *
   *****************************************************/
  public ExpandDirection getOrientation()
    {
    return ( mExpandDirection );
    }


  /*****************************************************
   *
   * Returns the view for a layout resource id.
   *
   *****************************************************/
  private View getViewFromLayoutResource( int layoutResourceId )
    {
    Context context = getContext();


    // If the supplied layout resource id is zero - create a
    // dummy view.

    View view;

    if ( layoutResourceId != 0 )
      {
      view = inflate( getContext(), layoutResourceId, new FrameLayout( context ) );

      if ( view == null )
        {
        throw ( new RuntimeException( "Failed to inflate always visible view from layout resource" ) );
        }
      }
    else
      {
      view = new View( context );
      }


    return ( view );
    }


  /*****************************************************
   *
   * Inserts or replaces a child view within a view group.
   *
   *****************************************************/
  private void insertOrReplaceChildView( ViewGroup viewGroup, int childIndex, View newView, ViewGroup.LayoutParams layoutParams )
    {
    // If there is already a child view at the supplied index - remove
    // it first.
    if ( viewGroup.getChildAt( childIndex ) != null )
      {
      viewGroup.removeViewAt( childIndex );
      }

    viewGroup.addView( newView, childIndex, layoutParams );
    }


  /*****************************************************
   *
   * Sets the always visible view.
   *
   *****************************************************/
  public void setAlwaysVisibleView( View alwaysVisibleView )
    {
    mAlwaysVisibleView = alwaysVisibleView;

    // Set ourself as the listener
    alwaysVisibleView.setOnClickListener( this );

    // Set the view visibility
    alwaysVisibleView.setVisibility( View.VISIBLE );

    // Remove any existing views from the slider layout
    mSliderLayout.removeAllViews();

    // Add the child views
    mExpandDirection.addChildViews( mSliderLayout, alwaysVisibleView, mRevealedView );
    }


  /*****************************************************
   *
   * Sets the always visible view from a layout resource id.
   *
   *****************************************************/
  public void setAlwaysVisibleView( int layoutResourceId )
    {
    setAlwaysVisibleView( getViewFromLayoutResource( layoutResourceId ) );
    }


  /*****************************************************
   *
   * Returns the always visible view.
   *
   *****************************************************/
  public View getAlwaysVisibleView()
    {
    return ( mAlwaysVisibleView );
    }


  /*****************************************************
   *
   * Sets the lower view.
   *
   *****************************************************/
  public void setRevealedView( View revealedView )
    {
    mRevealedView = revealedView;

    // Set ourself as the listener
    revealedView.setOnClickListener( this );

    // Set the view visibility
    revealedView.setVisibility( mSliderIsExpanded ? View.VISIBLE : View.GONE );

    // Remove any existing views from the slider layout
    mSliderLayout.removeAllViews();

    // Add the child views
    mExpandDirection.addChildViews( mSliderLayout, mAlwaysVisibleView, revealedView );
    }


  /*****************************************************
   *
   * Sets the revealed view from a layout resource id.
   *
   *****************************************************/
  public void setRevealedView( int layoutResourceId )
    {
    setRevealedView( getViewFromLayoutResource( layoutResourceId ) );
    }


  /*****************************************************
   *
   * Returns the revealed view.
   *
   *****************************************************/
  public View getRevealedView()
    {
    return ( mRevealedView );
    }


  /*****************************************************
   *
   * Sets the interpolator to use whilst animating.
   *
   *****************************************************/
  public void setAnimationInterpolator( Interpolator interpolator )
    {
    mAnimationInterpolator = interpolator;
    }


  /*****************************************************
   *
   * Sets the expanded state without animating.
   *
   *****************************************************/
  public void snapToExpandedState( boolean sliderShouldBeExpanded )
    {
    // If the slider is already in the correct state or is animating - do nothing
    if ( mSliderIsExpanded == sliderShouldBeExpanded || mSliderIsAnimating ) return;

    // Set the appropriate visibility of the revealed view
    mRevealedView.setVisibility( sliderShouldBeExpanded ? View.VISIBLE : View.GONE );

    mSliderIsExpanded = sliderShouldBeExpanded;
    }


  /*****************************************************
   *
   * Animates the slider to the expanded state.
   *
   *****************************************************/
  public void animateToExpandedState( boolean sliderWillBeExpanded )
    {
    // If the slider is already in the correct state or is animating - do nothing
    if ( mSliderIsExpanded == sliderWillBeExpanded || mSliderIsAnimating ) return;

    // Get the frame size
    int frameWidth  = getWidth();
    int frameHeight = getHeight();


    Animation animation;

    // If the slider is to be expanded:
    //   1. Make the revealed view visible
    //   2. Perform a translate animation of the slider into view
    if ( sliderWillBeExpanded )
      {
      // Ensure the revealed view is visible, and measure its size

      mRevealedView.setVisibility( View.VISIBLE );

      mRevealedView.measure(
              mExpandDirection.getWidthMeasureSpecForRevealedView( frameWidth ),
              mExpandDirection.getHeightMeasureSpecForRevealedView( frameHeight ) );


      // Animate the slider in by the height of the revealed view
      animation = mExpandDirection.getExpandAnimation( mRevealedView.getMeasuredWidth(), mRevealedView.getMeasuredHeight() );

      // Set a listener for the end of animation
      animation.setAnimationListener( new ExpandAnimationListener() );
      }

    // Otherwise, if the slider is to be shrunk:
    //   1. Perform a translate animation of the slider out of view
    //   2. When the animation has finished - make the revealed view invisible
    else
      {
      // Animate the slider out by the actual height of the revealed view
      animation = mExpandDirection.getShrinkAnimation( mRevealedView.getWidth(), mRevealedView.getHeight() );

      // Set a listener for the end of animation
      animation.setAnimationListener( new ShrinkAnimationListener() );

      // Set the fill to maintain the offset after the animation completes. This allows us to change the
      // view visibility and clear the animation without a noticable flicker.
      animation.setFillAfter( true );
      }


    // Set the animation duration
    animation.setDuration( mSlideAnimationDurationMillis );

    // Set any interpolator
    if ( mAnimationInterpolator != null ) animation.setInterpolator( mAnimationInterpolator );

    // Start the animation
    startAnimation( animation );

    mSliderIsAnimating = true;
    mSliderIsExpanded  = sliderWillBeExpanded;
    }


  /*****************************************************
   *
   * Returns true or false, depending on whether the slider
   * is expanded or not.
   *
   *****************************************************/
  public boolean sliderIsExpanded()
    {
    return ( mSliderIsExpanded );
    }


  /*****************************************************
   *
   * Sets the slide animation duration in milliseconds.
   *
   *****************************************************/
  public void setSlideAnimationDuration( long durationMillis )
    {
    mSlideAnimationDurationMillis = durationMillis;
    }


  /*****************************************************
   *
   * Returns the slide animation duration in milliseconds.
   *
   *****************************************************/
  public long getSlideAnimationDurationMillis()
    {
    return ( mSlideAnimationDurationMillis );
    }


  /*****************************************************
   *
   * Sets the listener.
   *
   *****************************************************/
  public void setListener( IListener listener )
    {
    mListener = listener;
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The direction of the slider.
   *
   *****************************************************/
  public enum ExpandDirection
    {
    ///// Up /////

      UP
              {
              protected int getLinearLayoutOrientation()
                {
                return ( LinearLayout.VERTICAL );
                }

              protected FrameLayout.LayoutParams getSliderLayoutParams()
                {
                return ( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM ) );
                }

              protected void addChildViews( ViewGroup viewGroup, View alwaysVisibleView, View revealedView )
                {
                ViewGroup.LayoutParams childLayoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );

                viewGroup.addView( alwaysVisibleView, childLayoutParams );
                viewGroup.addView( revealedView,      childLayoutParams );
                }

              protected int getWidthMeasureSpecForRevealedView( int frameWidth )
                {
                return ( MeasureSpec.makeMeasureSpec( frameWidth, MeasureSpec.EXACTLY ) );
                }

              protected int getHeightMeasureSpecForRevealedView( int frameHeight )
                {
                return ( MeasureSpec.makeMeasureSpec( frameHeight, MeasureSpec.AT_MOST ) );
                }

              protected Animation getExpandAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, 0.0f, revealedHeight, 0.0f ) );
                }

              protected Animation getShrinkAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, 0.0f, 0.0f, revealedHeight ) );
                }

              },


      ///// Down /////

      DOWN
              {
              protected int getLinearLayoutOrientation()
                {
                return ( LinearLayout.VERTICAL );
                }

              protected FrameLayout.LayoutParams getSliderLayoutParams()
                {
                return ( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP ) );
                }

              protected void addChildViews( ViewGroup viewGroup, View alwaysVisibleView, View revealedView )
                {
                ViewGroup.LayoutParams childLayoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );

                viewGroup.addView( revealedView,      childLayoutParams );
                viewGroup.addView( alwaysVisibleView, childLayoutParams );
                }

              protected int getWidthMeasureSpecForRevealedView( int frameWidth )
                {
                return ( MeasureSpec.makeMeasureSpec( frameWidth, MeasureSpec.EXACTLY ) );
                }

              protected int getHeightMeasureSpecForRevealedView( int frameHeight )
                {
                return ( MeasureSpec.makeMeasureSpec( frameHeight, MeasureSpec.AT_MOST ) );
                }

              protected Animation getExpandAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, 0.0f, - revealedHeight, 0.0f ) );
                }

              protected Animation getShrinkAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, 0.0f, 0.0f, - revealedHeight ) );
                }

              },


      ///// Left /////

      LEFT
              {
              protected int getLinearLayoutOrientation()
                {
                return ( LinearLayout.HORIZONTAL );
                }

              protected FrameLayout.LayoutParams getSliderLayoutParams()
                {
                return ( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.RIGHT ) );
                }

              protected void addChildViews( ViewGroup viewGroup, View alwaysVisibleView, View revealedView )
                {
                ViewGroup.LayoutParams childLayoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT );

                viewGroup.addView( alwaysVisibleView, childLayoutParams );
                viewGroup.addView( revealedView,      childLayoutParams );
                }

              protected int getWidthMeasureSpecForRevealedView( int frameWidth )
                {
                return ( MeasureSpec.makeMeasureSpec( frameWidth, MeasureSpec.AT_MOST ) );
                }

              protected int getHeightMeasureSpecForRevealedView( int frameHeight )
                {
                return ( MeasureSpec.makeMeasureSpec( frameHeight, MeasureSpec.EXACTLY ) );
                }

              protected Animation getExpandAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( revealedWidth, 0.0f, 0.0f, 0.0f ) );
                }

              protected Animation getShrinkAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, revealedWidth, 0.0f, 0.0f ) );
                }

              },


      ///// Right /////

      RIGHT
              {
              protected int getLinearLayoutOrientation()
                {
                return ( LinearLayout.HORIZONTAL );
                }

              protected FrameLayout.LayoutParams getSliderLayoutParams()
                {
                return ( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.LEFT ) );
                }

              protected void addChildViews( ViewGroup viewGroup, View alwaysVisibleView, View revealedView )
                {
                ViewGroup.LayoutParams childLayoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT );

                viewGroup.addView( revealedView,      childLayoutParams );
                viewGroup.addView( alwaysVisibleView, childLayoutParams );
                }

              protected int getWidthMeasureSpecForRevealedView( int frameWidth )
                {
                return ( MeasureSpec.makeMeasureSpec( frameWidth, MeasureSpec.AT_MOST ) );
                }

              protected int getHeightMeasureSpecForRevealedView( int frameHeight )
                {
                return ( MeasureSpec.makeMeasureSpec( frameHeight, MeasureSpec.EXACTLY ) );
                }

              protected Animation getExpandAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( - revealedWidth, 0.0f, 0.0f, 0.0f ) );
                }

              protected Animation getShrinkAnimation( int revealedWidth, int revealedHeight )
                {
                return ( new TranslateAnimation( 0.0f, - revealedWidth, 0.0f, 0.0f ) );
                }

              };


    ///// Methods /////

    protected abstract int                       getLinearLayoutOrientation();
    protected abstract FrameLayout.LayoutParams  getSliderLayoutParams();
    protected abstract void                      addChildViews( ViewGroup viewGroup, View alwaysVisibleView, View revealedView );
    protected abstract int                       getWidthMeasureSpecForRevealedView( int frameWidth );
    protected abstract int                       getHeightMeasureSpecForRevealedView( int frameHeight );
    protected abstract Animation                 getExpandAnimation( int revealedWidth, int revealedHeight );
    protected abstract Animation                 getShrinkAnimation( int revealedWidth, int revealedHeight );
    }


  ///// Inner Class(es) /////

  /*****************************************************
   *
   * The listener interface.
   *
   *****************************************************/
  public interface IListener
    {
    /*****************************************************
     *
     * Called when the always visible view is clicked.
     *
     *****************************************************/
    public void onAlwaysVisibleViewClicked( View view );

    /*****************************************************
     *
     * Called when the revealed view is clicked.
     *
     *****************************************************/
    public void onRevealedViewClicked( View view );
    }


  /*****************************************************
   *
   * The expand animation listener.
   *
   *****************************************************/
  private class ExpandAnimationListener implements AnimationListener
    {
    @Override
    public void onAnimationEnd( Animation animation )
      {
      mSliderIsAnimating = false;
      }

    @Override
    public void onAnimationRepeat( Animation animation )
      {
      }

    @Override
    public void onAnimationStart( Animation animation )
      {
      }
    }


  /*****************************************************
   *
   * The shrink animation listener.
   *
   *****************************************************/
  private class ShrinkAnimationListener implements AnimationListener
    {
    @Override
    public void onAnimationEnd( Animation animation )
      {
      mSliderIsAnimating = false;

      // Once the shrink animation is finished - clear it, and make the revealed view disappear.
      clearAnimation();

      mRevealedView.setVisibility( View.GONE );
      }

    @Override
    public void onAnimationRepeat( Animation animation )
      {
      }

    @Override
    public void onAnimationStart( Animation animation )
      {
      }
    }

  }

