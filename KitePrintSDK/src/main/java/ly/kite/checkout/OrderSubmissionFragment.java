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
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import ly.kite.R;
import ly.kite.api.OrderState;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a headless fragment that submits an
 * order.
 *
 *****************************************************/
public class OrderSubmissionFragment extends DialogFragment implements IOrderSubmissionProgressListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "OrderSubmissionFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private OrderSubmitter  mOrderSubmitter;

  private Runnable        mLastEventRunnable;

  private ProgressDialog  mProgressDialog;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Attaches this fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static public OrderSubmissionFragment start( Activity activity, Order order )
    {
    OrderSubmissionFragment orderSubmissionFragment = new OrderSubmissionFragment();

    orderSubmissionFragment.show( activity.getFragmentManager(), OrderSubmissionFragment.TAG );

    orderSubmissionFragment.submit( activity, order );

    return ( orderSubmissionFragment );
    }


  ////////// Constructor(s) //////////


  ////////// DialogFragment Method(s) //////////

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
   * Called to create a dialog.
   *
   *****************************************************/
  @Override
  public AlertDialog onCreateDialog( Bundle savedInstanceState )
    {
    // If there isn't already a progress dialog - create one now

    if ( mProgressDialog == null )
      {
      mProgressDialog = new ProgressDialog( getActivity() );

      mProgressDialog.setCancelable( false );
      mProgressDialog.setIndeterminate( false );
      mProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
      mProgressDialog.setProgressNumberFormat( null );   // Don't display the "N/100" text
      mProgressDialog.setTitle( R.string.alert_dialog_title_processing );
      mProgressDialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
      mProgressDialog.setMax( 100 );
      }

    //if ( ! mProgressDialog.isShowing() ) mProgressDialog.show();

    return ( mProgressDialog );
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

    checkLastEvent();
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
    
    checkLastEvent();
    }


  ////////// IOrderSubmissionResultListener Method(s) //////////

  /*****************************************************
   *
   * Called with order submission progress.
   *
   *****************************************************/
  @Override
  public void onOrderUpdate( Order order, OrderState state, int primaryProgressPercent, int secondaryProgressPercent )
    {
    // Determine what the order state is, and set the progress dialog accordingly

    ProgressDialog progressDialog = (ProgressDialog)getDialog();
    
    if ( progressDialog == null ) return;

    switch ( state )
      {
      case UPLOADING:
        progressDialog.setIndeterminate( false );
        //mProgressDialog.setProgressPercentFormat( NumberFormat.getPercentInstance() );
        progressDialog.setProgress( primaryProgressPercent );
        progressDialog.setSecondaryProgress( secondaryProgressPercent );
        progressDialog.setMessage( getString( R.string.order_submission_message_uploading ) );
        break;

      // The progress bar becomes indeterminate once the images have been uploaded

      case POSTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_posted ) );
        break;

      case RECEIVED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_received ) );
        break;

      case ACCEPTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_accepted ) );
        break;

      // We shouldn't get any other states, but if we do - display its name
      default:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( state.name() );
        break;
      }
    }


  /*****************************************************
   *
   * Called when an order submission has completed.
   *
   *****************************************************/
  @Override
  public void onOrderComplete( final Order order, final OrderState state )
    {
    // The call-back might get called when we are not attached to an activity,
    // and there is no target fragment. The runnable will therefore get re-run
    // when we are attached or a target fragment is set.

    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        IOrderSubmissionResultListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderComplete( order, state );

        IOrderSubmissionResultListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderComplete( order, state );
        }
      };

    mLastEventRunnable.run();
    }


  /*****************************************************
   *
   * Called when an order submission has timed out.
   *
   *****************************************************/
  @Override
  public void onOrderTimeout( final Order order )
    {
    // The call-back might get called when we are not attached to an activity,
    // and there is no target fragment. The runnable will therefore get re-run
    // when we are attached or a target fragment is set.

    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        IOrderSubmissionResultListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderTimeout( order );

        IOrderSubmissionResultListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderTimeout( order );
        }
      };

    mLastEventRunnable.run();
    }


  /*****************************************************
   *
   * Called when an order submission returns an error.
   *
   *****************************************************/
  @Override
  public void onOrderError( final Order order, final Exception exception )
    {
    // The call-back might get called when we are not attached to an activity,
    // and there is no target fragment. The runnable will therefore get re-run
    // when we are attached or a target fragment is set.

    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        IOrderSubmissionResultListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderError( order, exception );

        IOrderSubmissionResultListener fragmentListener = getFragmentListener();
        if ( fragmentListener != null ) fragmentListener.onOrderError( order, exception );
        }
      };

    mLastEventRunnable.run();
    }


  /*****************************************************
   *
   * Called when an order submission is a suplicate.
   *
   *****************************************************/
  @Override
  public void onOrderDuplicate( final Order order, final String originalOrderId )
    {
    // The call-back might get called when we are not attached to an activity,
    // and there is no target fragment. The runnable will therefore get re-run
    // when we are attached or a target fragment is set.

    mLastEventRunnable = new Runnable()
      {
      @Override
      public void run()
        {
        IOrderSubmissionResultListener activityListener = getActivityListener();
        if ( activityListener != null ) activityListener.onOrderDuplicate( order, originalOrderId );

        IOrderSubmissionResultListener fragmentListener = getFragmentListener();
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
  public void submit( Context context, Order order )
    {
    mOrderSubmitter = new OrderSubmitter( context, order, this );

    mOrderSubmitter.submit();
    }


  /*****************************************************
   *
   * Checks for a last event.
   *
   *****************************************************/
  private void checkLastEvent()
    {
    if ( mLastEventRunnable != null ) mLastEventRunnable.run();
    }


  /*****************************************************
   *
   * Returns the activity cast to a listener.
   *
   *****************************************************/
  private IOrderSubmissionResultListener getActivityListener()
    {
    Activity activity;

    if ( ( activity = getActivity() ) != null &&
           activity instanceof IOrderSubmissionResultListener )
      {
      return ( (IOrderSubmissionResultListener)activity );
      }

    return ( null );
    }


  /*****************************************************
   *
   * Returns the target fragment cast to a listener.
   *
   *****************************************************/
  private IOrderSubmissionResultListener getFragmentListener()
    {
    Fragment fragment;

    if ( ( fragment = getTargetFragment() ) != null &&
            fragment instanceof IOrderSubmissionResultListener )
      {
      return ( (IOrderSubmissionResultListener)fragment );
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

