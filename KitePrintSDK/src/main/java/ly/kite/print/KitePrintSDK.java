package ly.kite.print;

import com.paypal.android.sdk.payments.PaymentActivity;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class KitePrintSDK {

    private static final String PAYPAL_CLIENT_ID_SANDBOX = "Aa5nsBDntBpozWQykoxQXoHFOqs551hTNt0B8LQXTudoh8bD0nT1F735c_Fh";
    private static final String PAYPAL_RECIPIENT_SANDBOX = "hello-facilitator@psilov.eu";
    private static final String PAYPAL_CLIENT_ID_LIVE = "AT2JfBAmXD-CHGJnUb05ik4J-GrCi4XxjY9_grfCFjreYaLrNswj8uzhuWyj";
    private static final String PAYPAL_RECIPIENT_LIVE = "deon@oceanlabs.co";

    public static enum Environment {
        LIVE("https://api.psilov.eu", PaymentActivity.ENVIRONMENT_LIVE, PAYPAL_CLIENT_ID_LIVE, PAYPAL_RECIPIENT_LIVE),
        TEST("https://api.psilov.eu", PaymentActivity.ENVIRONMENT_SANDBOX, PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX),
        STAGING("http://staging.api.psilov.eu", PaymentActivity.ENVIRONMENT_SANDBOX, PAYPAL_CLIENT_ID_SANDBOX, PAYPAL_RECIPIENT_SANDBOX); /* private environment intended only for Ocean Labs use, hands off :) */

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

    public static void initialize(String apiKey) {
        KitePrintSDK.apiKey = apiKey;
        KitePrintSDK.environment = Environment.LIVE;
    }

    public static void initialize(String apiKey, Environment env) {
        KitePrintSDK.apiKey = apiKey;
        KitePrintSDK.environment = env;
    }

    public static String getAPIKey() {
        return apiKey;
    }

    public static Environment getEnvironment() {
        return environment;
    }
}
