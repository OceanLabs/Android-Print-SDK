package co.oceanlabs.pssdk.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PrintJob;
import co.oceanlabs.pssdk.PrintOrder;
import co.oceanlabs.pssdk.PrintOrderSubmissionListener;
import co.oceanlabs.pssdk.R;

public class PaymentActivity extends Activity {

    public static final String EXTRA_PRINT_ORDER = "co.oceanlabs.pssdk.EXTRA_PRINT_ORDER";
    public static final String EXTRA_PRINT_ENVIRONMENT = "co.oceanlabs.pssdk.EXTRA_PRINT_ENVIRONMENT";
    public static final String EXTRA_PRINT_API_KEY = "co.oceanlabs.pssdk.EXTRA_PRINT_API_KEY";

    public static final String ENVIRONMENT_STAGING = "co.oceanlabs.pssdk.ENVIRONMENT_STAGING";
    public static final String ENVIRONMENT_LIVE = "co.oceanlabs.pssdk.ENVIRONMENT_LIVE";
    public static final String ENVIRONMENT_TEST = "co.oceanlabs.pssdk.ENVIRONMENT_TEST";

    private static final int REQUEST_CODE_PAYPAL = 0;

    private PrintOrder printOrder;
    private String apiKey;
    private PSPrintSDK.Environment environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String apiKey = getIntent().getStringExtra(EXTRA_PRINT_API_KEY);
        String envString = getIntent().getStringExtra(EXTRA_PRINT_ENVIRONMENT);
        this.printOrder = (PrintOrder) getIntent().getParcelableExtra(EXTRA_PRINT_ORDER);

        if (apiKey == null) {
            throw new IllegalArgumentException("You must specify an API key string extra in the intent used to start the PaymentActivity");
        }

        if (printOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the PaymentActivity");
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(printOrder))
                    .commit();
        }

        PSPrintSDK.Environment env = PSPrintSDK.Environment.LIVE;
        if (envString != null) {
            if (envString.equals(ENVIRONMENT_STAGING)) {
                env = PSPrintSDK.Environment.STAGING;
            } else if (envString.equals(ENVIRONMENT_TEST)) {
                env = PSPrintSDK.Environment.TEST;
            }
        }

        this.apiKey = apiKey;
        this.environment = env;

        PSPrintSDK.initialize(apiKey, env);


        /*
         * Start PayPal Service
         */
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, environment.getPayPalEnvironment());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_CLIENT_ID, environment.getPayPalClientId());
        startService(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_PRINT_ORDER, printOrder);
        outState.putString(EXTRA_PRINT_API_KEY, apiKey);
        outState.putSerializable(EXTRA_PRINT_ENVIRONMENT, environment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.printOrder = savedInstanceState.getParcelable(EXTRA_PRINT_ORDER);
        this.apiKey = savedInstanceState.getString(EXTRA_PRINT_API_KEY);
        this.environment = (PSPrintSDK.Environment) savedInstanceState.getSerializable(EXTRA_PRINT_ENVIRONMENT);
        PSPrintSDK.initialize(apiKey, environment);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onButtonPayWithPayPalClicked(View view) {
        PayPalPayment payment = new PayPalPayment(printOrder.getCost(), "GBP", "Product");
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, environment.getPayPalEnvironment());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_CLIENT_ID, environment.getPayPalClientId());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYER_ID, "<someuser@somedomain.com>");
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RECEIVER_EMAIL, environment.getPayPalReceiverEmail());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        //intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_SKIP_CREDIT_CARD, true);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYPAL) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String proofOfPayment = confirm.toJSONObject().getJSONObject("proof_of_payment").getJSONObject("adaptive_payment").getString("pay_key");
                        submitOrderForPrinting(proofOfPayment);
                    } catch (JSONException e) {
                        showErrorDialog(e.getMessage());
                    }
                }
            }
        }
    }

    public void onButtonPayWithCreditCardClicked(View view) {

    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!")
                .setMessage(message)
                .setPositiveButton("OK", null);
        // Create the AlertDialog object and return it
        Dialog d = builder.create();
        d.show();
    }

    private void submitOrderForPrinting(String proofOfPayment) {
        printOrder.setProofOfPayment(proofOfPayment);
        printOrder.saveToHistory(this);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Processing");
        dialog.setMessage("One moment...");
        dialog.setMax(100);

        dialog.show();
        printOrder.submitForPrinting(this, new PrintOrderSubmissionListener() {
            @Override
            public void onProgress(PrintOrder printOrder, int totalAssetsUploaded, int totalAssetsToUpload, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite, long totalBytesWritten, long totalBytesExpectedToWrite) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                final float step = (1.0f / totalAssetsToUpload);
                float progress = totalAssetsUploaded * step + (totalAssetBytesWritten / (float) totalAssetBytesExpectedToWrite) * step;
                dialog.setProgress((int) (totalAssetsUploaded * step * 100));
                dialog.setSecondaryProgress((int) (progress * 100));
                dialog.setMessage(String.format("Uploading images %d/%d", totalAssetsUploaded + 1, totalAssetsToUpload));
            }

            @Override
            public void onSubmissionComplete(PrintOrder printOrder, String orderIdReceipt) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                printOrder.saveToHistory(PaymentActivity.this);
                dialog.dismiss();
                Intent i = new Intent(PaymentActivity.this, OrderReceiptActivity.class);
                i.putExtra(OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder);
                startActivity(i);
            }

            @Override
            public void onError(PrintOrder printOrder, Exception error) {
                if (Looper.myLooper() != Looper.getMainLooper()) throw new AssertionError("Should be calling back on the main thread");
                dialog.dismiss();
                showErrorDialog(error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private final PrintOrder printOrder;

        public PlaceholderFragment(PrintOrder printOrder) {
            this.printOrder = printOrder;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_payment, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ListView l = (ListView) view.findViewById(R.id.list_view_order_summary);
            l.setAdapter(new PrintOrderSummaryListAdapter(printOrder));
        }
    }

    private static class PrintOrderSummaryListAdapter extends BaseAdapter {

        private final PrintOrder order;

        private PrintOrderSummaryListAdapter(PrintOrder order) {
            this.order = order;
        }

        @Override
        public int getCount() {
            return order.getJobs().size();
        }

        @Override
        public Object getItem(int i) {
            return order.getJobs().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.order_summary_list_item, parent, false);
            TextView itemDescription = (TextView) row.findViewById(R.id.text_view_order_item_description);
            TextView itemCost = (TextView) row.findViewById(R.id.text_view_order_item_cost);

            PrintJob job = order.getJobs().get(i);
            itemDescription.setText(String.format("Pack of %d %s", job.getQuantity(), job.getProductType().getProductName()));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
            itemCost.setText(formatter.format(job.getCost().doubleValue()));

            return (row);
        }
    }

}
