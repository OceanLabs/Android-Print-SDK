/*****************************************************
 *
 * ExtendedRecyclerView.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any co<merge>

    <include layout="@layout/include_cta_bar"/>

</merge>mpetitor platforms. This means the software MAY NOT be modified 
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

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an extended recycler view.
 *
 *****************************************************/
public class ExtendedRecyclerView extends RecyclerView
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ExtendedRecyclerView";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ExtendedRecyclerView( Context context )
    {
    super( context );
    }

  public ExtendedRecyclerView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public ExtendedRecyclerView( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );
    }


  ////////// RecyclerView Method(s) //////////

  /*****************************************************
   *
   * Saves the instance state.
   *
   *****************************************************/
  @Override
  public Parcelable onSaveInstanceState()
    {
    return ( super.onSaveInstanceState() );
    }


  /*****************************************************
   *
   * Restores the instance state.
   *
   *****************************************************/
  @Override
  public void onRestoreInstanceState( Parcelable state )
    {
    super.onRestoreInstanceState( state );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns a position corresponding to the supplied point.
   *
   *****************************************************/
  public int positionFromPoint( int x, int y )
    {
    View childView = findChildViewUnder( x, y );

    if ( childView != null )
      {
      return ( getChildAdapterPosition( childView ) );
      }

    return ( -1 );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

