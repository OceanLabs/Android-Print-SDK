package ly.kite.print;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class KitePrintSDK {

    private static final String PAYPAL_CLIENT_ID_SANDBOX = "Aa5nsBDntBpozWQykoxQXoHFOqs551hTNt0B8LQXTudoh8bD0nT1F735c_Fh";
    private static final String PAYPAL_RECIPIENT_SANDBOX = "hello-facilitator@psilov.eu";
    private static final String PAYPAL_CLIENT_ID_LIVE = "AT2JfBAmXD-CHGJnUb05ik4J-GrCi4XxjY9_grfCFjreYaLrNswj8uzhuWyj";
    private static final String PAYPAL_RECIPIENT_LIVE = "deon@oceanlabs.co";

    public static enum Environment {
        LIVE("https://api.kite.ly", PaymentActivity.ENVIRONMENT_LIVE, PAYPAL_CLIENT_ID_LIVE, PAYPAL_RECIPIENT_LIVE),
        TEST("https://api.kite.ly", PaymentActivity.ENVIRONMENT_SANDBOX, PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX),
        STAGING("http://staging.api.kite.ly", PaymentActivity.ENVIRONMENT_SANDBOX, PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX); /* private environment intended only for Ocean Labs use, hands off :) */

        private final String apiEndpoint;
        private final String payPalEnvironment;
        private final String payPalClientId;
        private final String payPalRecipient;

        private Environment(String apiEndpoint, String payPalEnvironment, String payPalClientId, String payPalRecipient) {
            this.apiEndpoint = apiEndpoint;
            this.payPalEnvironment = payPalEnvironment;
            this.payPalClientId = payPalClientId;
            this.payPalRecipient = payPalRecipient;
        }

        public String getPrintAPIEndpoint() {
            return apiEndpoint;
        }
        public String getPayPalClientId() {
            return payPalClientId;
        }
        public String getPayPalEnvironment() {
            return payPalEnvironment;
        }
        public String getPayPalReceiverEmail() {
            return payPalRecipient;
        }
    }

    private static String apiKey;
    private static Environment environment;
    private static Context appContext;

    public static void initialize(String apiKey, Context context) {
        KitePrintSDK.apiKey = apiKey;
        KitePrintSDK.environment = Environment.LIVE;
        KitePrintSDK.appContext = context;

        Locale defaultLocale = Locale.getDefault();
        Currency a = Currency.getInstance(defaultLocale);
        KitePrintSDK.userCurrencyCode = a.getCurrencyCode();

    }

    public static String getUserCurrencyCode() {
        return userCurrencyCode;
    }

    public static String userCurrencyCode;

    public static void initialize(String apiKey, Environment env, Context context) {
        KitePrintSDK.apiKey = apiKey;
        KitePrintSDK.environment = env;
        KitePrintSDK.appContext = context;

        Locale defaultLocale = Locale.getDefault();
        Currency a = Currency.getInstance(defaultLocale);
        KitePrintSDK.userCurrencyCode = a.getCurrencyCode();
    }


    public static void syncTemplates(Context context){
        Template.syncTemplates(context);
    }


    public static String getAPIKey() {
        return apiKey;
    }
    public static Context getAppContext() {
        return appContext;
    }

    public static Environment getEnvironment() {
        return environment;
    }


    public static void updateOrderSummaryString(String summary){
        SharedPreferences settings = appContext.getSharedPreferences("ly.kite.sharedpreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        editor.putString("order_summary",summary);
        editor.commit();
    }

    public static String getOrderSummaryString(){

        SharedPreferences settings = KitePrintSDK.getAppContext().getSharedPreferences("ly.kite.sharedpreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = settings.getString("order_summary", "");
        return json;

    }




}
