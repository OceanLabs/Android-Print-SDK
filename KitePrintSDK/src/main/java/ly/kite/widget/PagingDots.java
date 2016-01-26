/*****************************************************
 *
 * PagingDots.java
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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a paging dots widget, used to provide
 * a user with an indication of progress for galleries
 * or multiple screens / pages.
 *
 *****************************************************/
public class PagingDots extends LinearLayout implements AdapterView.OnItemSelectedListener, ViewPager.OnPageChangeListener
  {
  ////////// Static Constant(s) //////////

  private static final int PADDING_IN_DIP = 5;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private int        mOffImageResourceId;
  private int        mOnImageResourceId;
  private int        mCurrentPageIndex;

  private Animation  mOutAnimation;
  private Animation  mInAnimation;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public PagingDots( Context context )
    {
    super( context );

    setProperties();
    }


  public PagingDots( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    setProperties();
    }


  ////////// AdapterView.OnItemSelectedListener Method(s) //////////

  /*****************************************************
   *
   * Called when an item is selected.
   *
   *****************************************************/
  public void onItemSelected( AdapterView<?> adaptorView, View view, int position, long id )
    {
    setPageIndex( position );
    }


  /*****************************************************
   *
   * Called when nothing is selected.
   *
   *****************************************************/
  public void onNothingSelected( AdapterView<?> adaptorView )
    {
    setPageIndex( -1 );
    }


  ////////// ViewPager.OnPageChangeListener Method(s) //////////

  @Override
  public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels )
    {
    }

  @Override
  public void onPageSelected( int position )
    {
    setPageIndex( position );
    }

  @Override
  public void onPageScrollStateChanged( int state )
    {

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the orientation and gravity.
   *
   *****************************************************/
  private void setProperties()
    {
    // Set the orientation
    setOrientation( HORIZONTAL );

    // Set the gravity
    setGravity( Gravity.CENTER );
    }


  /*****************************************************
   *
   * Sets the number of pages and image resources to use
   * when drawing the dots.
   *
   *****************************************************/
  public void setProperties( int pageCount, int offImageResourceId, int onImageResourceId )
    {
    // Save the values
    mOffImageResourceId = offImageResourceId;
    mOnImageResourceId  = onImageResourceId;

    // Adjust the page index if necessary
    if ( mCurrentPageIndex >= pageCount ) mCurrentPageIndex = pageCount - 1;

    // Clear all existing children of this layout
    removeAllViews();

    // Calculate what the padding will be in pixels (from DIPs)
    int paddingPixels = (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DIP, getResources().getDisplayMetrics() );

    // Create the correct number of child image views
    for ( int childIndex = 0; childIndex < pageCount; childIndex ++ )
      {
      // Create an image view
      ImageView imageView = new ImageView( getContext() );
      imageView.setPadding( paddingPixels, paddingPixels, paddingPixels, paddingPixels );

      // Set the correct off/on image depending on whether this is the current page
      imageView.setImageResource( childIndex == mCurrentPageIndex ? onImageResourceId : offImageResourceId );

      // Create the layout params for this image view
      LayoutParams layoutParams = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
      layoutParams.gravity = Gravity.CENTER;

      // Add the image view to the layout
      addView( imageView, layoutParams );
      }

    invalidate();
    }


  /*****************************************************
   *
   * Sets the out animation for dots that are becoming unselected.
   *
   *****************************************************/
  public void setOutAnimation( Animation outAnimation )
    {
    mOutAnimation = outAnimation;
    }


  /*****************************************************
   *
   * Sets the in animation for dots that are becoming unselected.
   *
   *****************************************************/
  public void setInAnimation( Animation inAnimation )
    {
    mInAnimation = inAnimation;
    }


  /*****************************************************
   *
   * Sets the current page index.
   *
   *****************************************************/
  public void setPageIndex( int pageIndex)
    {
    int previousPageIndex = mCurrentPageIndex;
    mCurrentPageIndex     = pageIndex;

    // Go through every child image view
    for ( int childIndex = 0; childIndex < getChildCount(); childIndex ++ )
      {
      // If the child is an image view, set its image depending on whether this is
      // the current page.

      View childView = getChildAt( childIndex );

      if ( childView instanceof ImageView )
        {
        ImageView imageView = (ImageView)childView;

        if ( childIndex == previousPageIndex )
          {
          // This is the previously selected page


          // If there is an out animation - start it. Otherwise switch to the off image.

          if ( mOutAnimation != null )
            {
            //mOutAnimation.setAnimationListener( new DotAnimationListener( imageView, 0 /* mOffImageResourceId */ ) );

            imageView.startAnimation( mOutAnimation );
            }
          else
            {
            // There is no out animation, so just display the unselected image.
            imageView.setImageResource( mOffImageResourceId );
            }
          }
        else if ( childIndex == mCurrentPageIndex )
          {
          // This is the newly selected page


          // Set the selected image immediately, and if there is an in animation - start it.

          imageView.setImageResource(  mOnImageResourceId );

          if ( mInAnimation != null )
            {
            //mInAnimation.setAnimationListener( new DotAnimationListener( imageView ) );

            imageView.startAnimation( mInAnimation );
            }
          }
        else
          {
          // Make sure there is no animation lingering
          imageView.setAnimation( null );

          imageView.setImageResource( mOffImageResourceId );
          }
        }
      }

    invalidate();
    }


  /*****************************************************
   *
   * Performs two actions after a dot animation has finished:
   *   - Sets a new image for the image view, if supplied
   *   - Removes the animation from the image view
   *
   *****************************************************/
  private class DotAnimationListener implements Animation.AnimationListener
    {
    private static final int NO_IMAGE_RESOURCE = 0;

    private ImageView  mImageView;
    private int        mImageResourceId;


    DotAnimationListener( ImageView imageView, int imageResourceId )
      {
      mImageView       = imageView;
      mImageResourceId = imageResourceId;
      }

    DotAnimationListener( ImageView imageView )
      {
      this( imageView, NO_IMAGE_RESOURCE );
      }


    @Override
    public void onAnimationStart( Animation animation )
      {
      // Ignore
      }

    @Override
    public void onAnimationEnd( Animation animation )
      {
      // Set a new image
      if ( mImageResourceId != NO_IMAGE_RESOURCE ) mImageView.setImageResource( mImageResourceId );

      // Clear the animation
      mImageView.setAnimation( null );
      }

    @Override
    public void onAnimationRepeat( Animation animation )
      {
      // Shouldn't happen
      }
    }

  }

