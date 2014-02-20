package co.oceanlabs.pssdk.checkout;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PrintOrder;
import co.oceanlabs.pssdk.R;

public class CheckoutActivity extends Activity {

    public static final String EXTRA_PRINT_ORDER = "co.oceanlabs.pssdk.EXTRA_PRINT_ORDER";
    public static final String EXTRA_PRINT_ENVIRONMENT = "co.oceanlabs.pssdk.EXTRA_PRINT_ENVIRONMENT";
    public static final String EXTRA_PRINT_API_KEY = "co.oceanlabs.pssdk.EXTRA_PRINT_API_KEY";

    public static final String ENVIRONMENT_STAGING = "co.oceanlabs.pssdk.ENVIRONMENT_STAGING";
    public static final String ENVIRONMENT_LIVE = "co.oceanlabs.pssdk.ENVIRONMENT_LIVE";
    public static final String ENVIRONMENT_TEST = "co.oceanlabs.pssdk.ENVIRONMENT_TEST";

    private PrintOrder printOrder;
    private String apiKey;
    private PSPrintSDK.Environment environment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            throw new IllegalArgumentException("You must specify an API key string extra in the intent used to start the CheckoutActivity");
        }

        if (printOrder == null) {
            throw new IllegalArgumentException("You must specify a PrintOrder object extra in the intent used to start the CheckoutActivity");
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

    private String getPaymentActivityEnvironment() {
        switch (environment) {
            case LIVE: return PaymentActivity.ENVIRONMENT_LIVE;
            case STAGING: return PaymentActivity.ENVIRONMENT_STAGING;
            case TEST: return PaymentActivity.ENVIRONMENT_TEST;
            default:
                throw new IllegalStateException("oops");
        }
    }

    public void onButtonNextClicked(View view) {
        Intent i = new Intent(this, PaymentActivity.class);
        i.putExtra(PaymentActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder);
        i.putExtra(PaymentActivity.EXTRA_PRINT_API_KEY, apiKey);
        i.putExtra(PaymentActivity.EXTRA_PRINT_ENVIRONMENT, getPaymentActivityEnvironment());
        startActivity(i);
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
            return rootView;
        }
    }


}
