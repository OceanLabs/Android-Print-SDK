/*****************************************************
 *
 * OrderSubmissionFragment.java
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

package ly.kite.checkout;


///// Import(s) /////

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import ly.kite.api.OrderState;
import ly.kite.ordering.PrintOrder;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a headless fragment that submits an
 * order.
 *
 *****************************************************/
public class OrderSubmissionFragment extends Fragment implements OrderSubmitter.IProgressListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "OrderSubmissionFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private OrderSubmitter  mOrderSubmitter;

  private Runnable        mLastEventRunnable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Fragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    // Make sure we are retained even if the activity is destroyed, e.g. during
    // orientation changes.
    setRetainInstance( true );
    }


  /*****************************************************
   *
   * Called when the fragment is attached to an activity.
   *
   *****************************************************/
  @Override
  public void onAttach( Activity activity )
    {
    super.onAttach( activity );

    if ( mLastEventRunnable != null ) mLastEventRunnable.run();
    }


  /*****************************************************
   *
   * Called when a target fragment is set.
   *
   *****************************************************/
  @Override
  public void setTargetFragment( Fragment fragment, int requestCode )
    {
    super.setTargetFragment( fragment, requestCode );

    if ( mLastEventRunnable != null ) mLastEventRunnable.run();
    }


  ////////// OrderSubmitter.IProgressListener Method(s) //////////

  @Override
  public void onOrderUpdate( final PrintOrder order, final OrderState state, final int primaryProgressPercent, final int secondaryProgressPercent )
    {
    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        OrderSubmitter.IProgressListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderUpdate( order, state, primaryProgressPercent, secondaryProgressPercent );

        OrderSubmitter.IProgressListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderUpdate( order, state, primaryProgressPercent, secondaryProgressPercent );
        }
      };

    mLastEventRunnable.run();
    }

  @Override
  public void onOrderComplete( final PrintOrder order, final OrderState state )
    {
    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        OrderSubmitter.IProgressListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderComplete( order, state );

        OrderSubmitter.IProgressListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderComplete( order, state );
        }
      };

    mLastEventRunnable.run();
    }

  @Override
  public void onOrderTimeout( final PrintOrder order )
    {
    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        OrderSubmitter.IProgressListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderTimeout( order );

        OrderSubmitter.IProgressListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderTimeout( order );
        }
      };

    mLastEventRunnable.run();
    }

  @Override
  public void onOrderError( final PrintOrder order, final Exception exception )
    {
    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        OrderSubmitter.IProgressListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderError( order, exception );

        OrderSubmitter.IProgressListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderError( order, exception );
        }
      };

    mLastEventRunnable.run();
    }

  @Override
  public void onOrderDuplicate( final PrintOrder order, final String originalOrderId )
    {
    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        OrderSubmitter.IProgressListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderDuplicate( order, originalOrderId );

        OrderSubmitter.IProgressListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderDuplicate( order, originalOrderId );
        }
      };

    mLastEventRunnable.run();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Submits an order for processing.
   *
   *****************************************************/
  public void submit( Context context, PrintOrder order )
    {
    mOrderSubmitter = new OrderSubmitter( context, order, this );

    mOrderSubmitter.submit();
    }


  /*****************************************************
   *
   * Returns the activity cast to a listener.
   *
   *****************************************************/
  private OrderSubmitter.IProgressListener getActivityListener()
    {
    Activity activity;

    if ( ( activity = getActivity() ) != null &&
           activity instanceof OrderSubmitter.IProgressListener )
      {
      return ( (OrderSubmitter.IProgressListener)activity );
      }

    return ( null );
    }


  /*****************************************************
   *
   * Returns the target fragment cast to a listener.
   *
   *****************************************************/
  private OrderSubmitter.IProgressListener getFragmentListener()
    {
    Fragment fragment;

    if ( ( fragment = getTargetFragment() ) != null &&
            fragment instanceof OrderSubmitter.IProgressListener )
      {
      return ( (OrderSubmitter.IProgressListener)fragment );
      }

    return ( null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

