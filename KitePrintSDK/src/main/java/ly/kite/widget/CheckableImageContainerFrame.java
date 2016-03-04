/*****************************************************
 *
 * CheckableImageContainerFrame.java
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
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class overlays a check mark on an image view.
 *
 *****************************************************/
public class CheckableImageContainerFrame extends AAREImageContainerFrame
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "CheckableImageContainerFrame";

  private static final long    CHECK_ANIMATION_DURATION_MILLIS = 200L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private State      mState;
  private boolean    mUncheckedStateIsVisible;

  private ImageView  mCheckImageView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public CheckableImageContainerFrame( Context context )
    {
    super( context );
    }

  public CheckableImageContainerFrame( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public CheckableImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public CheckableImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
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
    LayoutInflater layoutInflator = LayoutInflater.from( context );

    View view = layoutInflator.inflate( R.layout.checkable_image_container_frame, this, true );

    mCheckImageView = (ImageView)view.findViewById( R.id.check_image_view );

    initialise( context );

    return ( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    setState( State.UNCHECKED_INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the state.
   *
   *****************************************************/
  public State setState( State state )
    {
    mState = state;

    switch ( state )
      {
      case UNCHECKED_INVISIBLE:
        mUncheckedStateIsVisible = false;
        mCheckImageView.setVisibility( View.INVISIBLE );
        break;

      case UNCHECKED_VISIBLE:
        mUncheckedStateIsVisible = true;
        mCheckImageView.setImageResource( R.drawable.image_check_off );
        mCheckImageView.setVisibility( View.VISIBLE );
        break;

      case CHECKED:
        mCheckImageView.setImageResource( R.drawable.image_check_on );
        mCheckImageView.setVisibility( View.VISIBLE );
        break;
      }

    invalidate();

    return ( state );
    }


  /*****************************************************
   *
   * Sets the checked state.
   *
   * @return The new state.
   *
   *****************************************************/
  public State setChecked( boolean isChecked )
    {
    mCheckImageView.setAnimation( null );

    return ( setState( testChecked( isChecked ) ) );
    }


  /*****************************************************
   *
   * Tests the effect of setting the checked state. Does not
   * actually change the state.
   *
   * @return The new state
   *
   *****************************************************/
  public State testChecked( boolean isChecked )
    {
    if ( isChecked ) return ( State.CHECKED );

    if ( mUncheckedStateIsVisible ) return ( State.UNCHECKED_VISIBLE );

    return ( State.UNCHECKED_INVISIBLE );
    }


  /*****************************************************
   *
   * Returns true if the image is checked, false otherwise.
   *
   *****************************************************/
  public boolean isChecked()
    {
    return ( mState == State.CHECKED );
    }


  /*****************************************************
   *
   * Sets the checked state, but animates any transition.
   *
   *****************************************************/
  public void transitionChecked( boolean isChecked )
    {
    State previousState = mState;
    State newState      = setChecked( isChecked );


    // We only animate for the following transitions:
    //   - UNCHECKED_INVISIBLE -> CHECKED
    //   - CHECKED -> UNCHECKED_INVISIBLE

    Animation animation       = null;
    int       finalVisibility = 0;

    if ( previousState == State.UNCHECKED_INVISIBLE && newState == State.CHECKED )
      {
      ///// Animate invisible -> checked /////

      animation = new AlphaAnimation( 0f, 1f );

      finalVisibility = View.VISIBLE;
      }
    else if ( previousState == State.CHECKED && newState == State.UNCHECKED_INVISIBLE )
      {
      ///// Animate checked -> invisible /////

      animation = new AlphaAnimation( 1f, 0f );

      mCheckImageView.setVisibility( View.VISIBLE );

      finalVisibility = View.GONE;
      }


    if ( animation != null )
      {
      animation.setDuration( CHECK_ANIMATION_DURATION_MILLIS );
      animation.setAnimationListener( new VisibilitySettingAnimationListener( mCheckImageView, finalVisibility ) );

      mCheckImageView.startAnimation( animation );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Describes the state of the check.
   *
   *****************************************************/
  public enum State
    {
    UNCHECKED_INVISIBLE,
    UNCHECKED_VISIBLE,
    CHECKED
    }

  }

