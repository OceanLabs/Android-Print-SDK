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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.KiteSDK;
import ly.kite.analytics.Analytics;
import ly.kite.product.PrintJob;
import ly.kite.product.PrintOrder;
import ly.kite.R;
import ly.kite.address.Address;
import ly.kite.address.AddressBookActivity;
import ly.kite.product.Product;
import ly.kite.product.ProductGroup;
import ly.kite.product.ProductLoader;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


public class CheckoutActivity extends Activity {

    private static final long  MAXIMUM_PRODUCT_AGE_MILLIS = 1 * 60 * 60 * 1000;

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

    private PrintOrder mPrintOrder;
    private String apiKey;
    private KiteSDK.Environment environment;


    public static void start( Activity activity, PrintOrder printOrder, int requestCode )
      {
      Intent intent = new Intent( activity, CheckoutActivity.class );

      intent.putExtra( EXTRA_PRINT_ORDER, (Parcelable)printOrder );

      activity.startActivityForResult( intent, requestCode );
      }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_checkout);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        String apiKey = getIntent().getStringExtra(EXTRA_PRINT_API_KEY);
        String envString = getIntent().getStringExtra(EXTRA_PRINT_ENVIRONMENT);

        // TODO: Determine a better way of doing this.
        mPrintOrder = (PrintOrder) getIntent().getParcelableExtra(EXTRA_PRINT_ORDER);

        if (apiKey == null) {
            apiKey = KiteSDK.getInstance( this ).getAPIKey();
            if (apiKey == null) {
                throw new IllegalArgumentException("You must specify an API key string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize");
            }
        }

        if ( mPrintOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the CheckoutActivity");
        }

        if ( mPrintOrder.getJobs().size() < 1) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra that actually has some jobs for printing i.e. PrintOrder.getJobs().size() > 0");
        }

        KiteSDK.Environment env = null;
        if (envString == null) {
            env = KiteSDK.getInstance( this ).getEnvironment();
            if (env == null) {
                throw new IllegalArgumentException("You must specify an environment string extra in the intent used to start the CheckoutActivity or with KitePrintSDK.initialize");
            }
        } else {
            if (envString.equals(ENVIRONMENT_STAGING)) {
                env = KiteSDK.Environment.STAGING;
            } else if (envString.equals(ENVIRONMENT_TEST)) {
                env = KiteSDK.Environment.TEST;
            } else if (envString.equals(ENVIRONMENT_LIVE)) {
                env = KiteSDK.Environment.LIVE;
            } else {
                throw new IllegalArgumentException("Bad print environment extra: " + envString);
            }
        }

        this.apiKey = apiKey;
        this.environment = env;
        KiteSDK.getInstance( this ).setEnvironment( apiKey, env );
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // hide keyboard initially
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackShippingScreenViewed( mPrintOrder, Analytics.VARIANT_JSON_PROPERTY_VALUE_CLASSIC_PLUS_ADDRESS_SEARCH, true );
      }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_PRINT_ORDER, mPrintOrder );
        outState.putString(EXTRA_PRINT_API_KEY, apiKey);
        outState.putSerializable( EXTRA_PRINT_ENVIRONMENT, environment );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mPrintOrder = savedInstanceState.getParcelable(EXTRA_PRINT_ORDER);
        this.apiKey = savedInstanceState.getString(EXTRA_PRINT_API_KEY);
        this.environment = (KiteSDK.Environment) savedInstanceState.getSerializable(EXTRA_PRINT_ENVIRONMENT);
        KiteSDK.getInstance( this ).setEnvironment( apiKey, environment );
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

        if ( mPrintOrder.getShippingAddress() == null) {
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

        JSONObject userData = mPrintOrder.getUserData();
        if (userData == null) {
            userData = new JSONObject();
        }

        try {
            userData.put("email", email);
            userData.put("phone", phone);
        } catch (JSONException ex) {/* ignore */}
        mPrintOrder.setUserData( userData );
        mPrintOrder.setNotificationEmail( email );
        mPrintOrder.setNotificationPhoneNumber( phone );

        SharedPreferences settings = getSharedPreferences(SHIPPING_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SHIPPING_PREFERENCE_EMAIL, email);
        editor.putString(SHIPPING_PREFERENCE_PHONE, phone);
        editor.commit();


        // Make sure we have up-to-date products before we proceed

        final ProgressDialog progress = ProgressDialog.show(this, null, "Loading");

        ProductLoader.getInstance( this ).getAllProducts(
                MAXIMUM_PRODUCT_AGE_MILLIS,
                new ProductLoader.ProductConsumer()
                    {
                    @Override
                    public void onGotProducts( ArrayList<ProductGroup> productGroupList, HashMap<String, Product> productTable )
                        {
                        progress.dismiss();

                        startPaymentActivity();
                        }

                    @Override
                    public void onProductRetrievalError( Exception exception )
                        {
                        progress.dismiss();

                        showRetryTemplateSyncDialog( exception );
                        }
                    }
                );
    }

    private void showRetryTemplateSyncDialog(Exception error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Oops");
        builder.setMessage( error.getLocalizedMessage() );
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

        try
          {
          // This will return null if there are no products, or they are out of date, but that's
          // OK because we catch any exceptions.
          Pair<ArrayList<ProductGroup>,HashMap<String,Product>> productPair = ProductLoader.getInstance( this ).getCachedProducts( MAXIMUM_PRODUCT_AGE_MILLIS );

          // Go through every print job and check that we can get a product from the product id
          for ( PrintJob job : mPrintOrder.getJobs() )
            {
            String productId = productPair.second.get( job.getProduct().getId() ).getId();
            }
          }
        catch ( Exception exception )
          {
          showRetryTemplateSyncDialog( exception );
          return;
          }

        PaymentActivity.start( this, mPrintOrder, apiKey, getPaymentActivityEnvironment(),  REQUEST_CODE_PAYMENT );
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
                mPrintOrder.setShippingAddress( address );
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
