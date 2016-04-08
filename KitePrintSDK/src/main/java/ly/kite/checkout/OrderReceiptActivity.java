package ly.kite.checkout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import ly.kite.ordering.Order;
import ly.kite.R;

public class OrderReceiptActivity extends Activity
  {
  public static final String EXTRA_PRINT_ORDER = "ly.kite.EXTRA_PRINT_ORDER";

  private Order mPrintOrder;


  static public void startForResult( Activity activity, Order printOrder, int requestCode )
    {
    Intent intent = new Intent( activity, OrderReceiptActivity.class );

    intent.putExtra( OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable)printOrder );

    activity.startActivityForResult( intent, requestCode );
    }


  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    requestWindowFeature( Window.FEATURE_ACTION_BAR );

    Order printOrder = (Order) getIntent().getParcelableExtra( EXTRA_PRINT_ORDER );

    processPrintOrder( printOrder );
    }

  @Override
  public boolean onMenuItemSelected( int featureId, MenuItem item )
    {
    if ( item.getItemId() == android.R.id.home )
      {
      leaveScreen();

      return ( true );
      }

    return ( super.onMenuItemSelected( featureId, item ) );
    }

  @Override
  public void onBackPressed()
    {
    // Keep the back key regardless of whether the order succeeded or failed, just in case
    // there is no "continue shopping" button.

    setResult( Activity.RESULT_OK );

    super.onBackPressed();
    }


  private void processPrintOrder( Order printOrder )
    {
    // Make sure we got a print order
    if ( printOrder == null )
      {
      throw new IllegalArgumentException( "The print order is null" );
      }


    // Save the print order
    mPrintOrder = printOrder;

    // Get the action bar
    ActionBar actionBar = getActionBar();

    if ( mPrintOrder.isPrinted() )
      {
      ///// Success /////

      setContentView( R.layout.screen_order_receipt );

      ListView listView        = (ListView)findViewById( R.id.order_summary_list_view );
      TextView orderIdTextView = (TextView)findViewById( R.id.order_id_text_view );


      listView.setAdapter( new OrderPricingAdaptor( this, mPrintOrder.getOrderPricing() ) );


      if ( orderIdTextView != null ) orderIdTextView.setText( mPrintOrder.getReceipt() );

      // If the order was successful - disable the back action
      if ( actionBar != null ) getActionBar().setDisplayHomeAsUpEnabled( false );
      }
    else
      {
      ///// Failure /////

      setContentView( R.layout.screen_order_failure );

      TextView paymentIdTextView = (TextView)findViewById( R.id.payment_id_text_view );
      Button   retryPrintButton  = (Button)findViewById( R.id.retry_print_button );


      if ( paymentIdTextView != null )
        {
        StringBuilder receipt = new StringBuilder();

        if ( mPrintOrder.getProofOfPayment() != null )
          {
          paymentIdTextView.setText( mPrintOrder.getProofOfPayment() );
          }
        }


      // Show an error dialog if we're arriving with a recent Payment success but we failed to successfully print the order.
      if ( getParent() instanceof PaymentActivity && mPrintOrder.getLastPrintSubmissionError() != null )
        {
        showErrorDialog( mPrintOrder.getLastPrintSubmissionError().getMessage() );
        }

      // If the order failed - enable the back action
      if ( actionBar != null ) getActionBar().setDisplayHomeAsUpEnabled( true );
      }

    }


  private void leaveScreen()
    {
    setResult( Activity.RESULT_OK );

    finish();
    }

  public void onContinueShoppingClicked( View view )
    {
    leaveScreen();
    }

  public void onRetryPrintClicked( View view )
    {
    final ProgressDialog dialog = new ProgressDialog( this );
    dialog.setCancelable( false );
    dialog.setIndeterminate( false );
    dialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
    dialog.setTitle( R.string.alert_dialog_title_processing );
    dialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
    dialog.setMax( 100 );
    dialog.show();

    mPrintOrder.submitForPrinting( this, new Order.ISubmissionProgressListener()
    {
    @Override
    public void onProgress( Order printOrder, int primaryProgressPercent, int secondaryProgressPercent )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      dialog.setProgress( primaryProgressPercent );
      dialog.setSecondaryProgress( secondaryProgressPercent );
      dialog.setMessage( getString( R.string.alert_dialog_message_uploading_images ) );
      }

    @Override
    public void onSubmissionComplete( Order printOrder, String orderIdReceipt )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      //mPrintOrder.saveToHistory(OrderReceiptActivity.this);
      dialog.dismiss();

      processPrintOrder( printOrder );
      }

    @Override
    public void onError( Order printOrder, Exception error )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      dialog.dismiss();
      showErrorDialog( error.getMessage() );
      }
    } );
    }

  private void showErrorDialog( String message )
    {
    AlertDialog.Builder builder = new AlertDialog.Builder( this );
    builder.setTitle( R.string.alert_dialog_title_oops ).setMessage( message ).setPositiveButton( R.string.OK, null );
    Dialog d = builder.create();
    d.show();
    }

  }
