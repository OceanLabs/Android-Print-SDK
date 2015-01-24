package ly.kite.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.os.Bundle;
//import android.telecom.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.print.KitePrintSDK;
import ly.kite.print.KitePrintSDKException;
import ly.kite.print.PrintJob;
import ly.kite.print.PrintOrder;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;
import ly.kite.print.Template;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;


public class CheckoutActivity extends Activity {

    private static final String SHIPPING_PREFERENCES = "shipping_preferences";
    private static final String SHIPPING_PREFERENCE_EMAIL = "shipping_preferences.email";
    private static final String SHIPPING_PREFERENCE_PHONE = "shipping_preferences.phone";

    public static final String EXTRA_PRINT_ORDER = "ly.kite.EXTRA_PRINT_ORDER";
    public static final String EXTRA_PRINT_ENVIRONMENT = "ly.kite.EXTRA_PRINT_ENVIRONMENT";
    public static final String EXTRA_PRINT_API_KEY = "ly.kite.EXTRA_PRINT_API_KEY";

    public static final String ENVIRONMENT_STAGING = "ly.kite.ENVIRONMENT_STAGING";
    public static final String ENVIRONMENT_LIVE = "ly.kite.ENVIRONMENT_LIVE";
    public static final String ENVIRONMENT_TEST = "ly.kite.ENVIRONMENT_TEST";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_ADDRESS_BOOK = 2;

    private PrintOrder printOrder;
    private String apiKey;
    private KitePrintSDK.Environment environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_checkout);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        String apiKey = getIntent().getStringExtra(EXTRA_PRINT_API_KEY);
        String envString = getIntent().getStringExtra(EXTRA_PRINT_ENVIRONMENT);
        this.printOrder = (PrintOrder) getIntent().getParcelableExtra(EXTRA_PRINT_ORDER);

        if (apiKey == null) {
            apiKey = KitePrintSDK.getAPIKey();
            if (apiKey == null) {
                throw new IllegalArgumentException("You must specify an API key string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize");
            }
        }

        if (printOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the CheckoutActivity");
        }

        if (printOrder.getJobs().size() < 1) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra that actually has some jobs for printing i.e. PrintOrder.getJobs().size() > 0");
        }

        KitePrintSDK.Environment env = null;
        if (envString == null) {
            env = KitePrintSDK.getEnvironment();
            if (env == null) {
                throw new IllegalArgumentException("You must specify an environment string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize");
            }
        } else {
            if (envString.equals(ENVIRONMENT_STAGING)) {
                env = KitePrintSDK.Environment.STAGING;
            } else if (envString.equals(ENVIRONMENT_TEST)) {
                env = KitePrintSDK.Environment.TEST;
            } else if (envString.equals(ENVIRONMENT_LIVE)) {
                env = KitePrintSDK.Environment.LIVE;
            } else {
                throw new IllegalArgumentException("Bad print environment extra: " + envString);
            }
        }

        this.apiKey = apiKey;
        this.environment = env;
        KitePrintSDK.initialize(apiKey, env, getApplicationContext());
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // hide keyboard initially
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        this.environment = (KitePrintSDK.Environment) savedInstanceState.getSerializable(EXTRA_PRINT_ENVIRONMENT);
        KitePrintSDK.initialize(apiKey, environment, getApplicationContext());
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void onButtonDeliverAddressClicked(View view) {
        Intent i = new Intent(this, AddressBookActivity.class);
        startActivityForResult(i, REQUEST_CODE_ADDRESS_BOOK);
    }

    private String getPaymentActivityEnvironment() {
        switch (environment) {
            case LIVE: return PaymentActivity.ENVIRONMENT_LIVE;
            case STAGING: return PaymentActivity.ENVIRONMENT_STAGING;
            case TEST: return PaymentActivity.ENVIRONMENT_TEST;
            default:
                throw new IllegalStateException("oops");
        }
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setPositiveButton("OK", null);
        Dialog d = builder.create();
        d.show();
    }

    public void onButtonNextClicked(View view) {
        String email = ((TextView) findViewById(R.id.email_address_text_view)).getText().toString();
        String phone = ((TextView) findViewById(R.id.phone_number_text_view)).getText().toString();

        if (printOrder.getShippingAddress() == null) {
            showErrorDialog("Invalid Delivery Address", "Please choose a delivery address");
            return;
        }

        if (!isEmailValid(email)) {
            showErrorDialog("Invalid Email Address", "Please enter a valid email address");
            return;
        }

        if (phone.length() < 5) {
            showErrorDialog("Invalid Phone Number", "Please enter a valid phone number");
            return;
        }

        JSONObject userData = printOrder.getUserData();
        if (userData == null) {
            userData = new JSONObject();
        }

        try {
            userData.put("email", email);
            userData.put("phone", phone);
        } catch (JSONException ex) {/* ignore */}
        printOrder.setUserData(userData);
        printOrder.setNotificationEmail(email);
        printOrder.setNotificationPhoneNumber(phone);

        SharedPreferences settings = getSharedPreferences(SHIPPING_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SHIPPING_PREFERENCE_EMAIL, email);
        editor.putString(SHIPPING_PREFERENCE_PHONE, phone);
        editor.commit();

        Date lastSyncedDate = Template.getLastSyncDate();
        Date dateHourAgo = new Date(System.currentTimeMillis() - (1 * 60 * 60 * 1000));
        if (Template.isSyncInProgress() || lastSyncedDate == null || lastSyncedDate.compareTo(dateHourAgo) < 0) {
            final ProgressDialog progress = ProgressDialog.show(this, null, "Loading");
            Template.sync(getApplicationContext(), new Template.TemplateSyncListener() {
                @Override
                public void onSuccess() {
                    progress.dismiss();
                    startPaymentActivity();
                }

                @Override
                public void onError(Exception error) {
                    progress.dismiss();
                    showRetryTemplateSyncDialog(error);
                }
            });
        } else {
            // templates synced recently enough to jump straight to payment
            startPaymentActivity();
        }
    }

    private void showRetryTemplateSyncDialog(Exception error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Oops");
        builder.setMessage(error.getLocalizedMessage());
        if (error instanceof UnknownHostException || error instanceof SocketTimeoutException) {
            builder.setMessage("Please check your internet connectivity and then try again");
        }

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onButtonNextClicked(null);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void startPaymentActivity() {
        // Check we have valid templates for every printjob
        for (PrintJob job : printOrder.getJobs()) {
            try {
                Template.getTemplate(job.getTemplateId());
            } catch (Exception ex) {
                showRetryTemplateSyncDialog(ex);
                return;
            }
        }

        Intent i = new Intent(this, PaymentActivity.class);
        i.putExtra(PaymentActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder);
        i.putExtra(PaymentActivity.EXTRA_PRINT_API_KEY, apiKey);
        i.putExtra(PaymentActivity.EXTRA_PRINT_ENVIRONMENT, getPaymentActivityEnvironment());
        startActivityForResult(i, REQUEST_CODE_PAYMENT);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        } else if (requestCode == REQUEST_CODE_ADDRESS_BOOK) {
            if (resultCode == RESULT_OK) {
                Address address = data.getParcelableExtra(AddressBookActivity.EXTRA_ADDRESS);
                printOrder.setShippingAddress(address);
                Button chooseAddressButton = (Button) findViewById(R.id.address_picker_button);
                chooseAddressButton.setText(address.toString());
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_checkout, container, false);

            // Restore email address and phone number from history
            // Restore preferences
            SharedPreferences settings = getActivity().getSharedPreferences(SHIPPING_PREFERENCES, 0);
            String email = settings.getString(SHIPPING_PREFERENCE_EMAIL, null);
            String phone = settings.getString(SHIPPING_PREFERENCE_PHONE, null);
            if (email != null) {
                EditText emailEditText = (EditText) rootView.findViewById(R.id.email_address_text_view);
                emailEditText.setText(email);
            }

            if (phone != null) {
                EditText phoneEditText = (EditText) rootView.findViewById(R.id.phone_number_text_view);
                phoneEditText.setText(phone);
            }

            return rootView;
        }
    }


}
