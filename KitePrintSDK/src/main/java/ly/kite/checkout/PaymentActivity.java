package ly.kite.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import ly.kite.analytics.Analytics;
import ly.kite.print.ApplyPromoCodeListener;
import ly.kite.KiteSDK;
import ly.kite.print.PrintOrder;
import ly.kite.print.PrintOrderSubmissionListener;
import ly.kite.R;
import ly.kite.payment.PayPalCard;
import ly.kite.payment.PayPalCardChargeListener;
import ly.kite.payment.PayPalCardVaultStorageListener;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class PaymentActivity extends Activity {

    public static final String EXTRA_PRINT_ORDER = "ly.kite.EXTRA_PRINT_ORDER";
    public static final String EXTRA_PRINT_ENVIRONMENT = "ly.kite.EXTRA_PRINT_ENVIRONMENT";
    public static final String EXTRA_PRINT_API_KEY = "ly.kite.EXTRA_PRINT_API_KEY";

    public static final String ENVIRONMENT_STAGING = "ly.kite.ENVIRONMENT_STAGING";
    public static final String ENVIRONMENT_LIVE = "ly.kite.ENVIRONMENT_LIVE";
    public static final String ENVIRONMENT_TEST = "ly.kite.ENVIRONMENT_TEST";

    private static final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

    private static final int REQUEST_CODE_PAYPAL = 0;
    private static final int REQUEST_CODE_CREDITCARD = 1;
    private static final int REQUEST_CODE_RECEIPT = 2;

  // The print order, if it contains image assets, is too larger to be passed
  // in an intent - we get a TransactionTooLargeException. So we need to pass
  // it as a static variable.
  // TODO: Determine a better way of doing this.
  private static PrintOrder sPrintOrder;

    private PrintOrder mPrintOrder;
    private String apiKey;
    private KiteSDK.Environment printEnvironment;
    private PayPalCard.Environment paypalEnvironment;

  public static void start( Activity activity, PrintOrder printOrder, String apiKey, String environmentName, int requestCode )
    {
    Intent intent = new Intent( activity, PaymentActivity.class );

    // TODO: Determine a better way of doing this.
    sPrintOrder = printOrder;

    //intent.putExtra(PaymentActivity.EXTRA_PRINT_ORDER, (Parcelable) mPrintOrder );
    intent.putExtra( PaymentActivity.EXTRA_PRINT_API_KEY, apiKey );
    intent.putExtra( PaymentActivity.EXTRA_PRINT_ENVIRONMENT, environmentName );

    activity.startActivityForResult( intent, requestCode );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_payment);

        String apiKey = getIntent().getStringExtra(EXTRA_PRINT_API_KEY);
        String envString = getIntent().getStringExtra(EXTRA_PRINT_ENVIRONMENT);

    // TODO: Determine a better way of doing this.
    //this.mPrintOrder = (PrintOrder) getIntent().getParcelableExtra(EXTRA_PRINT_ORDER);
    mPrintOrder = sPrintOrder;

        if (apiKey == null) {
            throw new IllegalArgumentException("You must specify an API key string extra in the intent used to start the PaymentActivity");
        }

        if ( mPrintOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the PaymentActivity");
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance( mPrintOrder ))
                    .commit();
        }

//        KitePrintSDK.Environment env = KitePrintSDK.Environment.LIVE;
        KiteSDK.Environment env = KiteSDK.Environment.TEST;
        this.paypalEnvironment = PayPalCard.Environment.LIVE;
        if (envString != null) {
            if (envString.equals(ENVIRONMENT_STAGING)) {
                env = KiteSDK.Environment.STAGING;
                paypalEnvironment = PayPalCard.Environment.SANDBOX;
            } else if (envString.equals(ENVIRONMENT_TEST)) {
                env = KiteSDK.Environment.TEST;
                paypalEnvironment = PayPalCard.Environment.SANDBOX;
            }
        }

        this.apiKey = apiKey;
        this.printEnvironment = env;

        KiteSDK.getInstance( this ).setEnvironment( apiKey, env );

        /*
         * Start PayPal Service
         */
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, printEnvironment.getPayPalEnvironment());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_CLIENT_ID, printEnvironment.getPayPalClientId());
        startService(intent);

        if (getActionBar() != null) {
            if (this.paypalEnvironment == PayPalCard.Environment.SANDBOX) {
                getActionBar().setTitle("Payment (Sandbox)");
            } else {
                getActionBar().setTitle("Payment");
            }
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


    if ( savedInstanceState == null )
        {
        Analytics.getInstance( this ).trackPaymentScreenViewed( mPrintOrder );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_PRINT_ORDER, mPrintOrder );
        outState.putString(EXTRA_PRINT_API_KEY, apiKey);
        outState.putSerializable(EXTRA_PRINT_ENVIRONMENT, printEnvironment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mPrintOrder = savedInstanceState.getParcelable( EXTRA_PRINT_ORDER );
        this.apiKey = savedInstanceState.getString(EXTRA_PRINT_API_KEY);
        this.printEnvironment = (KiteSDK.Environment) savedInstanceState.getSerializable(EXTRA_PRINT_ENVIRONMENT);
        KiteSDK.getInstance( this ).setEnvironment( apiKey, printEnvironment );

        paypalEnvironment = PayPalCard.Environment.LIVE;
        if (printEnvironment == KiteSDK.Environment.STAGING || printEnvironment == KiteSDK.Environment.TEST) {
            paypalEnvironment = PayPalCard.Environment.SANDBOX;
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void onButtonPayWithPayPalClicked(View view) {
        PayPalPayment payment = new PayPalPayment( mPrintOrder.getCost( mPrintOrder.getCurrencyCode()), mPrintOrder.getCurrencyCode(), "Product");
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, printEnvironment.getPayPalEnvironment());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_CLIENT_ID, printEnvironment.getPayPalClientId());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYER_ID, "<someuser@somedomain.com>");
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RECEIVER_EMAIL, printEnvironment.getPayPalReceiverEmail());
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_SKIP_CREDIT_CARD, true);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL);
    }

    public void onButtonPayWithCreditCardClicked(View view) {
        final PayPalCard lastUsedCard = PayPalCard.getLastUsedCard(this);
        if (lastUsedCard != null && !lastUsedCard.hasVaultStorageExpired()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (this.paypalEnvironment == PayPalCard.Environment.SANDBOX) {
                builder.setTitle("Payment Source (Sandbox)");
            } else {
                builder.setTitle("Payment Source");
            }

            builder.setItems(new String[] {"Pay with new card", "Pay with card ending " + lastUsedCard.getLastFour()}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int itemIndex) {
                    if (itemIndex == 0) {
                        payWithNewCard();
                    } else {
                        payWithExistingCard(lastUsedCard);
                    }
                }
            });
            builder.show();
        } else {
            payWithNewCard();
        }
    }

    private void payWithNewCard() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, CARD_IO_TOKEN);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        startActivityForResult(scanIntent, REQUEST_CODE_CREDITCARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYPAL) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {

                    Analytics.getInstance( this ).trackPaymentCompleted( mPrintOrder, Analytics.PAYMENT_METHOD_PAYPAL );

                    try {
                        String proofOfPayment = confirm.toJSONObject().getJSONObject("proof_of_payment").getJSONObject("adaptive_payment").getString("pay_key");
                        submitOrderForPrinting(proofOfPayment);
                    } catch (JSONException e) {
                        showErrorDialog(e.getMessage());
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_CREDITCARD) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                if (!scanResult.isExpiryValid()) {
                    showErrorDialog("Sorry it looks like that card has expired. Please try again.");
                    return;
                }

                PayPalCard card = new PayPalCard();
                card.setNumber(scanResult.cardNumber);
                card.setExpireMonth(scanResult.expiryMonth);
                card.setExpireYear(scanResult.expiryYear);
                card.setCvv2(scanResult.cvv);
                card.setCardType(PayPalCard.CardType.getCardType(scanResult.getCardType()));

                if (card.getCardType() == PayPalCard.CardType.UNSUPPORTED) {
                    showErrorDialog("Sorry we couldn't recognize your card. Please try again manually entering your card details if necessary.");
                    return;
                }

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setTitle("Processing");
                dialog.setMessage("One moment");
                dialog.show();
                card.storeCard(paypalEnvironment, new PayPalCardVaultStorageListener() {
                    @Override
                    public void onStoreSuccess(PayPalCard card) {
                        dialog.dismiss();
                        payWithExistingCard(card);
                    }

                    @Override
                    public void onError(PayPalCard card, Exception ex) {
                        dialog.dismiss();
                        showErrorDialog(ex.getMessage());
                    }
                });

            } else {
                // card scan cancelled
            }
        } else if (requestCode == REQUEST_CODE_RECEIPT) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public static PayPalCard.Currency getPayPalCurrency(String currency) {
        if (currency.equals("GBP")) {
            return PayPalCard.Currency.GBP;
        } else if(currency.equals("EUR")){
            return PayPalCard.Currency.EUR;
        } else if (currency.equals("USD")){
            return PayPalCard.Currency.USD;
        } else if (currency.equals("SGD")){
            return PayPalCard.Currency.SGD;
        } else if (currency.equals("AUD")){
            return PayPalCard.Currency.AUD;
        } else if (currency.equals("NZD")){
            return PayPalCard.Currency.NZD;
        } else if (currency.equals("CAD")){
            return PayPalCard.Currency.CAD;
        } else {
            return PayPalCard.Currency.GBP;
        }
    }

    private void payWithExistingCard(PayPalCard card) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Processing");
        dialog.setMessage("One moment");
        dialog.show();

        card.chargeCard( paypalEnvironment, mPrintOrder.getCost( mPrintOrder.getCurrencyCode() ), getPayPalCurrency( mPrintOrder.getCurrencyCode() ), "", new PayPalCardChargeListener()
        {
        @Override
        public void onChargeSuccess( PayPalCard card, String proofOfPayment )
            {
            Analytics.getInstance( PaymentActivity.this ).trackPaymentCompleted( mPrintOrder, Analytics.PAYMENT_METHOD_CREDIT_CARD );

            dialog.dismiss();
            submitOrderForPrinting( proofOfPayment );
            card.saveAsLastUsedCard( PaymentActivity.this );
            }

        @Override
        public void onError( PayPalCard card, Exception ex )
            {
            dialog.dismiss();
            showErrorDialog( ex.getMessage() );
            }
        } );
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!").setMessage(message).setPositiveButton("OK", null);
        Dialog d = builder.create();
        d.show();
    }

    private void submitOrderForPrinting(String proofOfPayment) {
        if (proofOfPayment!=null) {
            mPrintOrder.setProofOfPayment( proofOfPayment );
        }
        //mPrintOrder.saveToHistory(this);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Processing");
        dialog.setMessage("One moment...");
        dialog.setMax(100);
        dialog.show();

        mPrintOrder.submitForPrinting( this, new PrintOrderSubmissionListener()
        {
        @Override
        public void onProgress( PrintOrder printOrder, int totalAssetsUploaded, int totalAssetsToUpload, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite, long totalBytesWritten, long totalBytesExpectedToWrite )
            {
            if ( Looper.myLooper() != Looper.getMainLooper() )
                throw new AssertionError( "Should be calling back on the main thread" );
            final float step = (1.0f / totalAssetsToUpload);
            float progress = totalAssetsUploaded * step + (totalAssetBytesWritten / (float) totalAssetBytesExpectedToWrite) * step;
            dialog.setProgress( (int) (totalAssetsUploaded * step * 100) );
            dialog.setSecondaryProgress( (int) (progress * 100) );
            dialog.setMessage( "Uploading images" );
            }

        @Override
        public void onSubmissionComplete( PrintOrder printOrder, String orderIdReceipt )
            {
            if ( Looper.myLooper() != Looper.getMainLooper() )
                throw new AssertionError( "Should be calling back on the main thread" );
            //mPrintOrder.saveToHistory(PaymentActivity.this);
            dialog.dismiss();
            Intent i = new Intent( PaymentActivity.this, OrderReceiptActivity.class );
            i.putExtra( OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder );
            startActivityForResult( i, REQUEST_CODE_RECEIPT );

            Analytics.getInstance( PaymentActivity.this ).trackOrderSubmission( printOrder );
            }

        @Override
        public void onError( PrintOrder printOrder, Exception error )
            {
            if ( Looper.myLooper() != Looper.getMainLooper() )
                throw new AssertionError( "Should be calling back on the main thread" );
            //mPrintOrder.saveToHistory(PaymentActivity.this);
            dialog.dismiss();
            //showErrorDialog(error.getMessage());

            Intent i = new Intent( PaymentActivity.this, OrderReceiptActivity.class );
            i.putExtra( OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder );
            startActivityForResult( i, REQUEST_CODE_RECEIPT );
            }
        } );
    }

    private void updateViewsBasedOnPromoCodeChange() {
        Button applyButton = (Button) findViewById(R.id.button_apply_promo);
        EditText promoText = (EditText) findViewById(R.id.edit_text_promo_code);
        if ( mPrintOrder.getPromoCode() != null) {
            promoText.setText( mPrintOrder.getPromoCode());
            promoText.setEnabled(false);
            applyButton.setText("Clear");
        } else {
            promoText.setText("");
            promoText.setEnabled(true);
            applyButton.setText("Apply");
        }

        Button payWithCreditCardButton = (Button) findViewById(R.id.button_pay_with_credit_card);
        if ( mPrintOrder.getCost( mPrintOrder.getCurrencyCode()).compareTo(BigDecimal.ZERO) <= 0) {
            findViewById(R.id.button_pay_with_paypal).setVisibility(View.GONE);
            payWithCreditCardButton.setText("Checkout for Free!");
            payWithCreditCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitOrderForPrinting(null);
                }
            });


        } else {
            findViewById(R.id.button_pay_with_paypal).setVisibility(View.VISIBLE);
            payWithCreditCardButton.setText("Pay with Credit Card");
        }
    }

    public void onButtonApplyClicked(View view) {
        if ( mPrintOrder.getPromoCode() != null) {
            // Clear promo code
            mPrintOrder.clearPromoCode();
            updateViewsBasedOnPromoCodeChange();
        } else {
            // Apply promo code
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setTitle("Processing");
            dialog.setMessage("Checking Code...");
            dialog.show();

            String promoCode = ((EditText) findViewById(R.id.edit_text_promo_code)).getText().toString();
            mPrintOrder.applyPromoCode( this, promoCode, new ApplyPromoCodeListener() {
                @Override
                public void onPromoCodeApplied(PrintOrder order, BigDecimal discount) {
                    dialog.dismiss();
                    Toast.makeText(PaymentActivity.this, "Discount applied!", Toast.LENGTH_LONG).show();
                    updateViewsBasedOnPromoCodeChange();
                }

                @Override
                public void onError(PrintOrder order, Exception ex) {
                    dialog.dismiss();
                    showErrorDialog(ex.getMessage());
                }
            });
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    // TODO: Remove inner fragment
    public static class PlaceholderFragment extends Fragment {

        final static String BUNDLE_KEY_PRINT_ORDER = "mPrintOrder";

        private PrintOrder printOrder;


        static PlaceholderFragment newInstance( PrintOrder printOrder )
          {
          Bundle argumentBundle = new Bundle();

          argumentBundle.putParcelable( BUNDLE_KEY_PRINT_ORDER, printOrder );

          PlaceholderFragment placeholderFragment = new PlaceholderFragment();
          placeholderFragment.setArguments( argumentBundle );

          return ( placeholderFragment );
          }


        @Override
        public void onCreate( Bundle savedInstanceState )
          {
          super.onCreate( savedInstanceState );

          Bundle argumentBundle = getArguments();

          this.printOrder = (PrintOrder)argumentBundle.getParcelable( BUNDLE_KEY_PRINT_ORDER );
          }


        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_payment, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ListView l = (ListView) view.findViewById(R.id.list_view_order_summary);
            l.setAdapter(new PrintOrderSummaryListAdapter(printOrder));
            ((PaymentActivity) getActivity()).updateViewsBasedOnPromoCodeChange();

            final Button applyButton = (Button) view.findViewById(R.id.button_apply_promo);
            final EditText promoText = (EditText) view.findViewById(R.id.edit_text_promo_code);
            promoText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    applyButton.setEnabled(promoText.getText().length() > 0);
                }
            });
        }
    }



}
