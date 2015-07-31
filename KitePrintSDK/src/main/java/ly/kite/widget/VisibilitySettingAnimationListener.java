/*****************************************************
 *
 * VisibilitySettingAnimationListener.java
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


///// Class Declaration /////

import android.view.View;
import android.view.animation.Animation;

/*****************************************************
 *
 * Clears the animation and sets the visibility of a view
 * after animation has finished.
 *
 *****************************************************/
public class VisibilitySettingAnimationListener implements Animation.AnimationListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "VisibilitySettingAnimationListener";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private View mView;
  private int   mVisibility;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public VisibilitySettingAnimationListener( View view, int visibility )
    {
    mView       = view;
    mVisibility = visibility;
    }


  ////////// Animation.AnimationListener Method(s) //////////

  /*****************************************************
   *
   * Called when animation starts.
   *
   *****************************************************/
  @Override
  public void onAnimationStart( Animation animation )
    {
    // Ignore
    }


  /*****************************************************
   *
   * Called when animation completes.
   *
   *****************************************************/
  @Override
  public void onAnimationEnd( Animation animation )
    {
    // Clear the animation and set the visibility
    mView.setAnimation( null );
    mView.setVisibility( mVisibility );
    }


  /*****************************************************
   *
   * Called when animation repeats.
   *
   *****************************************************/
  @Override
  public void onAnimationRepeat( Animation animation )
    {
    // Ignore
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

