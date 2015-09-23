package ly.kite.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ly.kite.catalogue.PrintOrder;
import ly.kite.catalogue.PrintOrderSubmissionListener;
import ly.kite.R;

public class OrderReceiptActivity extends Activity {

    public static final String EXTRA_PRINT_ORDER = "ly.kite.EXTRA_PRINT_ORDER";

    private PrintOrder printOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView( R.layout.screen_order_receipt );

        this.printOrder = (PrintOrder) getIntent().getParcelableExtra(EXTRA_PRINT_ORDER);

        if (printOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the OrderReceiptActivity");
        }


        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled( true );
        }

        // Show an error dialog if we're arriving with a recent Payment success but we failed to successfully print the order.
        if (!printOrder.isPrinted() && getParent() instanceof PaymentActivity && printOrder.getLastPrintSubmissionError() != null) {
            showErrorDialog(printOrder.getLastPrintSubmissionError().getMessage());
        }


    ListView l = (ListView)findViewById( R.id.order_summary_list_view );
    l.setAdapter( new OrderPricingAdaptor( this, printOrder.getOrderPricing() ) );

    TextView orderView = (TextView)findViewById( R.id.text_view_order_id );
    orderView.setText( printOrder.getReceipt() );

    Button retryPrintButton = (Button)findViewById( R.id.button_retry_print );
    ImageView headerView = (ImageView)findViewById( R.id.image_view_order_receipt_header );
    if ( this.printOrder.isPrinted() )
      {
      headerView.setImageResource( R.drawable.receipt_success );
      retryPrintButton.setVisibility( View.GONE );
      }
    else
      {
      headerView.setImageResource( R.drawable.receipt_failure );
      retryPrintButton.setVisibility( View.VISIBLE );

      StringBuilder receipt = new StringBuilder();
      if ( printOrder.getProofOfPayment() != null )
        {
        receipt.append( printOrder.getProofOfPayment() );
        }

      if ( printOrder.getPromoCode() != null )
        {
        if ( receipt.length() > 0 )
          {
          receipt.append( " " );
          }

        receipt.append( "PROMO-" ).append( printOrder.getPromoCode() );
        }

      orderView.setText( receipt );
      }

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    public void onButtonRetryPrintClicked(View view) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Processing");
        dialog.setMessage("One moment...");
        dialog.setMax(100);
        dialog.show();

        printOrder.submitForPrinting(this, new PrintOrderSubmissionListener() {
            @Override
            public void onProgress( PrintOrder printOrder, int primaryProgressPercent, int secondaryProgressPercent ) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                dialog.setProgress( primaryProgressPercent );
                dialog.setSecondaryProgress( secondaryProgressPercent );
                dialog.setMessage("Uploading images");
            }

            @Override
            public void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                //printOrder.saveToHistory(OrderReceiptActivity.this);
                dialog.dismiss();

                Button retryPrintButton = (Button) findViewById(R.id.button_retry_print);
                ImageView headerView = (ImageView) findViewById(R.id.image_view_order_receipt_header);
                TextView orderView = (TextView) findViewById(R.id.text_view_order_id);
                orderView.setText(printOrder.getReceipt());
                headerView.setImageResource(R.drawable.receipt_success);
                retryPrintButton.setVisibility(View.GONE);
            }

            @Override
            public void onError(PrintOrder printOrder, Exception error) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                dialog.dismiss();
                showErrorDialog(error.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!").setMessage(message).setPositiveButton("OK", null);
        Dialog d = builder.create();
        d.show();
    }

}
