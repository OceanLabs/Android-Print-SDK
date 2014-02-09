package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class PSPrintSDK {

    public static final String APIHostname = "api.psilov.eu";

    private static String apiKey;

    public static void initialize(String apiKey) {
        PSPrintSDK.apiKey = apiKey;
    }

    public static String getAPIKey() {
        return apiKey;
    }

}
