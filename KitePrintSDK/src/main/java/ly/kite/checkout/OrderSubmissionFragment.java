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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import ly.kite.R;
import ly.kite.api.OrderState;
import ly.kite.app.ARetainedDialogFragment;
import ly.kite.app.RetainedFragmentHelper;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a headless fragment that submits an
 * order.
 *
 *****************************************************/
public class OrderSubmissionFragment extends ARetainedDialogFragment implements IOrderSubmissionProgressListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "OrderSubmissionFragment";


  ////////// Static Variable(s) //////////

  ////////// Member Variable(s) //////////

  private OrderSubmitter  mOrderSubmitter;

  private ProgressDialog  mProgressDialog;

  ////////// Interface(s) //////////
  public interface OrderSubmissionListener<T> {
    void onTrigger(T arg);
  }


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Attaches this fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static public void start( final AOrderSubmissionActivity activity, final Order order , final OrderSubmissionListener<OrderSubmissionFragment> onGotFragment)
    {

      new Handler().post(new Runnable()
        {
        public void run()
          {
          OrderSubmissionFragment orderSubmissionFragment = new OrderSubmissionFragment();

          // When the app runs in background "show" causes the app to crash , so check before
          if( !activity.isRunningInBackground() && !orderSubmissionFragment.isAdded())
            {
            orderSubmissionFragment.show( activity.getFragmentManager() , TAG );
            }

          orderSubmissionFragment.submit( activity, order );

          onGotFragment.onTrigger ( orderSubmissionFragment );
        }
      });
    }


  /*****************************************************
   *
   * Tries to find this fragment, and returns it.
   *
   *****************************************************/
  static public OrderSubmissionFragment findFragment( Activity activity )
    {
    return ( (OrderSubmissionFragment) find( activity, TAG, OrderSubmissionFragment.class ) );
    }

  ////////// Constructor(s) //////////

  public OrderSubmissionFragment()
    {
    super( IOrderSubmissionResultListener.class );
    }

    ////////// DialogFragment Method(s) //////////

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

      mProgressDialog.setIndeterminate( false );
      mProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
      mProgressDialog.setProgressNumberFormat( null );   // Don't display the "N/100" text
      mProgressDialog.setTitle( R.string.kitesdk_alert_dialog_title_processing);
      mProgressDialog.setMessage( getString( R.string.kitesdk_alert_dialog_message_processing) );
      mProgressDialog.setMax( 100 );
      }

    setCancelable( false );

    return ( mProgressDialog );
    }


  ////////// IOrderSubmissionProgressListener Method(s) //////////

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
        progressDialog.setProgress( primaryProgressPercent );
        progressDialog.setSecondaryProgress( secondaryProgressPercent );
        progressDialog.setMessage( getString( R.string.kitesdk_order_submission_message_uploading) );
        break;

      // The progress bar becomes indeterminate once the images have been uploaded

      case POSTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.kitesdk_order_submission_message_posted) );
        break;

      case RECEIVED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.kitesdk_order_submission_message_received) );
        break;

      case ACCEPTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.kitesdk_order_submission_message_accepted) );
        break;

      // We shouldn't get any other states, but if we do - display its name
      default:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( state.name() );
        break;
      }
    }


  // These methods are implemented by IOrderSubmissionResultListener, which
  // is extended by IOrderSubmissionProgressListener.

  /*****************************************************
   *
   * Called when an order submission has completed.
   *
   *****************************************************/
  @Override
  public void onOrderComplete( final Order order, final OrderState state )
    {
    // The call-back might get called when we are not attached to an activity,
    // and there is no target fragment. The callback will therefore get re-notified
    // when we are attached or a target fragment is set.

    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object callback )
        {
        ( (IOrderSubmissionResultListener)callback ).onOrderComplete( order, state );
        }
      } );
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
    // and there is no target fragment. The callback will therefore get re-notified
    // when we are attached or a target fragment is set.

    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object callbackObject )
        {
        ( (IOrderSubmissionResultListener)callbackObject ).onOrderTimeout( order );
        }
      } );
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
    // and there is no target fragment. The callback will therefore get re-notified
    // when we are attached or a target fragment is set.

    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object callbackObject )
        {
        ( (IOrderSubmissionResultListener)callbackObject ).onOrderError( order, exception );
        }
      } );
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
    // and there is no target fragment. The callback will therefore get re-notified
    // when we are attached or a target fragment is set.

    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object callbackObject )
        {
        ( (IOrderSubmissionResultListener)callbackObject ).onOrderDuplicate( order, originalOrderId );
        }
      } );
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


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

