package co.oceanlabs.pssdk.payment;

import android.content.Context;
import android.net.Credentials;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import co.oceanlabs.pssdk.BaseRequest;
import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PSPrintSDKException;
import co.oceanlabs.pssdk.address.Country;
import io.card.payment.CardType;

/**
 * Created by deonbotha on 16/02/2014.
 */
public class PayPalCard implements Serializable {

    private static final String PERSISTED_LUC_FILENAME = "luc";

    public static enum CardType {
        VISA("visa"),
        MASTERCARD("mastercard"),
        DISCOVER("discover"),
        AMEX("amex"),
        UNSUPPORTED("unsupported");

        private final String paypalIdentifier;

        CardType(String paypalIdentifier) {
            this.paypalIdentifier = paypalIdentifier;
        }

        public static CardType getCardType(io.card.payment.CardType type) {
            switch (type) {
                case AMEX:
                    return AMEX;
                case MASTERCARD:
                    return MASTERCARD;
                case DISCOVER:
                    return DISCOVER;
                case VISA:
                    return VISA;
                default:
                    return UNSUPPORTED;
            }
        }
    }

    public static enum Currency {
        GBP("GBP"),
        USD("USD");

        private final String  code;

        Currency(String code) {
            this.code = code;
        }
    }

    public static enum Environment {
        SANDBOX("api.sandbox.paypal.com", "QWE1bnNCRG50QnBveldReWtveFFYb0hGT3FzNTUxaFROdDBCOExRWFR1ZG9oOGJEMG5UMUY3MzVjX0ZoOg=="),
        LIVE("api.paypal.com", "QVQySmZCQW1YRC1DSEdKblViMDVpazRKLUdyQ2k0WHhqWTlfZ3JmQ0ZqcmVZYUxyTnN3ajh1emh1V3lqOg==");

        private final String apiEndpoint;
        private final String authToken;

        Environment(String apiEndpoint, String authToken) {
            this.apiEndpoint = apiEndpoint;
            this.authToken = authToken;
        }
    }

    private static final long serialVersionUID = 0L;
    private String number;
    private String numberMasked;
    private CardType cardType;
    private int expireMonth;
    private int expireYear;
    private String cvv2;
    private String firstName;
    private String lastName;
    private String vaultId;
    private Date vaultExpireDate;

    public PayPalCard() {

    }

    public PayPalCard(CardType type, String number, int expireMonth, int expireYear, String cvv2) {
        this.cardType = type;
        this.number = number;
        this.expireMonth = expireMonth;
        setExpireYear(expireYear);
        this.cvv2 = cvv2;
    }

    public String getNumber() {
        return number;
    }

    public String getNumberMasked() {
        return numberMasked;
    }

    public String getLastFour() {
        if (number != null && number.length() == 16) {
            return number.substring(number.length() - 4);
        } else if (numberMasked != null) {
            return numberMasked.substring(numberMasked.length() - Math.min(4, numberMasked.length()));
        }

        return null;
    }

    public CardType getCardType() {
        return cardType;
    }

    public int getExpireMonth() {
        return expireMonth;
    }

    public int getExpireYear() {
        return expireYear;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public void setExpireMonth(int expireMonth) {
        if (expireMonth < 1 || expireMonth > 12) {
            throw new IllegalArgumentException("Expire month must be in range of 1-12 incusive");
        }
        this.expireMonth = expireMonth;
    }

    public void setExpireYear(int expireYear) {
        if (expireYear <= 99) {
            expireYear += 2000;
        }

        this.expireYear = expireYear;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    private void getAccessToken(final Environment env, final AccessTokenListener listener) {
        AsyncTask<Void, Void, Object> requestTask = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost req = new HttpPost(String.format("https://%s/v1/oauth2/token", env.apiEndpoint));
                req.setHeader("Content-Type", "application/x-www-form-urlencoded");
                try {
                    req.setEntity(new StringEntity("grant_type=client_credentials"));
                } catch (UnsupportedEncodingException e) {
                    return e;
                }

                req.setHeader("Authorization", "Basic " + env.authToken);

                try {
                    HttpResponse response = httpclient.execute(req);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null;) {
                        builder.append(line).append("\n");
                    }

                    JSONTokener t = new JSONTokener(builder.toString());
                    JSONObject json = new JSONObject(t);
                    String accessToken = json.getString("access_token");
                    return accessToken;
                } catch (Exception e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object response) {
                if (response instanceof String) {
                    listener.onAccessToken((String) response);
                } else {
                    listener.onError((Exception) response);
                }
            }
        };

        requestTask.execute();
    }

    public void storeCard(final Environment env, final PayPalCardVaultStorageListener listener) {
        getAccessToken(env, new AccessTokenListener() {
            @Override
            public void onAccessToken(final String accessToken) {
                final JSONObject storeJSON = new JSONObject();

                try {
                    storeJSON.put("number", number);
                    storeJSON.put("type", cardType.paypalIdentifier);
                    storeJSON.put("expire_month", "" + expireMonth);
                    storeJSON.put("expire_year", "" + expireYear);
                    storeJSON.put("cvv2", cvv2);
                } catch (JSONException ex) {
                    listener.onError(PayPalCard.this, ex);
                    return;
                }

                AsyncTask<Void, Void, Object> requestTask = new AsyncTask<Void, Void, Object>() {
                    @Override
                    protected Object doInBackground(Void... voids) {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost req = new HttpPost(String.format("https://%s/v1/vault/credit-card", env.apiEndpoint));
                        req.setHeader("Content-Type", "application/json");
                        req.setHeader("Accept-Language", "en");
                        try {
                            req.setEntity(new StringEntity(storeJSON.toString()));
                        } catch (UnsupportedEncodingException e) {
                            return e;
                        }

                        req.setHeader("Authorization", "Bearer " + accessToken);

                        try {
                            HttpResponse response = httpclient.execute(req);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                            StringBuilder builder = new StringBuilder();
                            for (String line = null; (line = reader.readLine()) != null;) {
                                builder.append(line).append("\n");
                            }

                            JSONTokener t = new JSONTokener(builder.toString());
                            JSONObject json = new JSONObject(t);
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode >= 200 && statusCode <= 299) {
                                VaultStoreResponse storageResponse = new VaultStoreResponse();
                                storageResponse.number = json.getString("number");
                                storageResponse.vaultId = json.getString("id");
                                String vaultExpireDateStr = json.getString("valid_until");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

                                storageResponse.vaultExpireDate = dateFormat.parse(vaultExpireDateStr.replaceAll("Z$", "+0000"));
                                return storageResponse;
                            } else {
                                String errorMessage = json.optString("message");
                                if (errorMessage == null) {
                                    errorMessage = "Failed to make the payment. Please check your internet connectivity and try again.";
                                }

                                return new PSPrintSDKException(errorMessage);
                            }
                        } catch (Exception e) {
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Object response) {
                        if (response instanceof VaultStoreResponse) {
                            VaultStoreResponse storageResponse = (VaultStoreResponse) response;
                            PayPalCard.this.vaultId = storageResponse.vaultId;
                            PayPalCard.this.vaultExpireDate = storageResponse.vaultExpireDate;
                            PayPalCard.this.numberMasked = storageResponse.number;
                            listener.onStoreSuccess(PayPalCard.this);
                        } else {
                            listener.onError(PayPalCard.this, (Exception) response);
                        }
                    }
                };

                requestTask.execute();
            }

            @Override
            public void onError(Exception error) {
                listener.onError(PayPalCard.this, error);
            }
        });
    }

    private JSONObject createPaymentJSON(BigDecimal amount, Currency currency, String description) throws JSONException {
        JSONObject fundingInstrument = new JSONObject();
        if (number != null) {
            // take payment directly using full card number
            JSONObject cc = new JSONObject();
            fundingInstrument.put("credit_card", cc);
            cc.put("number", number);
            cc.put("type", cardType.paypalIdentifier);
            cc.put("expire_month", "" + expireMonth);
            cc.put("expire_year", "" + expireYear);
            cc.put("cvv2", cvv2);
        } else {
            JSONObject token = new JSONObject();
            fundingInstrument.put("credit_card_token", token);
            token.put("credit_card_id", vaultId);
        }

        JSONObject payment = new JSONObject();
        payment.put("intent", "sale");

        JSONObject payer = new JSONObject();
        payment.put("payer", payer);
        payer.put("payment_method", "credit_card");
        JSONArray fundingInstruments = new JSONArray();
        payer.put("funding_instruments", fundingInstruments);
        fundingInstruments.put(fundingInstrument);

        JSONArray transactions = new JSONArray();
        payment.put("transactions", transactions);
        JSONObject transaction = new JSONObject();
        transactions.put(transaction);
        transaction.put("description", description);
        JSONObject _amount = new JSONObject();
        transaction.put("amount", _amount);
        _amount.put("total", String.format("%.2f", amount.floatValue()));
        _amount.put("currency", currency.code);

        return payment;
    }

    public void chargeCard(final Environment env, final BigDecimal amount, final Currency currency, final String description, final PayPalCardChargeListener listener) {
        getAccessToken(env, new AccessTokenListener() {
            @Override
            public void onAccessToken(final String accessToken) {
                JSONObject paymentJSON = null;
                try {
                    paymentJSON = createPaymentJSON(amount, currency, description);
                } catch (JSONException ex) {
                    listener.onError(PayPalCard.this, ex);
                    return;
                }

                AsyncTask<JSONObject, Void, Object> requestTask = new AsyncTask<JSONObject, Void, Object>() {
                    @Override
                    protected Object doInBackground(JSONObject... jsons) {
                        JSONObject paymentJSON = jsons[0];

                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost req = new HttpPost(String.format("https://%s/v1/payments/payment", env.apiEndpoint));
                        req.setHeader("Content-Type", "application/json");
                        req.setHeader("Accept-Language", "en");
                        try {
                            req.setEntity(new StringEntity(paymentJSON.toString()));
                        } catch (UnsupportedEncodingException e) {
                            return e;
                        }

                        req.setHeader("Authorization", "Bearer " + accessToken);

                        try {
                            HttpResponse response = httpclient.execute(req);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                            StringBuilder builder = new StringBuilder();
                            for (String line = null; (line = reader.readLine()) != null;) {
                                builder.append(line).append("\n");
                            }

                            JSONTokener t = new JSONTokener(builder.toString());
                            JSONObject json = new JSONObject(t);
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode >= 200 && statusCode <= 299) {
                                String paymentId = json.getString("id");
                                String paymentState = json.getString("state");
                                if (!paymentState.equalsIgnoreCase("approved")) {
                                    return new PSPrintSDKException("Your payment was not approved. Please try again.");
                                }

                                return paymentId;
                            } else {
                                String errorMessage = json.optString("message");
                                if (errorMessage == null) {
                                    errorMessage = "Failed to make the payment. Please check your internet connectivity and try again.";
                                }

                                return new PSPrintSDKException(errorMessage);
                            }
                        } catch (Exception e) {
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Object response) {
                        if (response instanceof String) {
                            listener.onChargeSuccess(PayPalCard.this, (String) response);
                        } else {
                            listener.onError(PayPalCard.this, (Exception) response);
                        }
                    }
                };

                requestTask.execute(paymentJSON);
            }

            @Override
            public void onError(Exception error) {
                listener.onError(PayPalCard.this, error);
            }
        });
    }

    public boolean isStoredInVault() {
        return vaultId != null;
    }

    public boolean hasVaultStorageExpired() {
        if (vaultExpireDate == null) {
            return true;
        }

        return vaultExpireDate.before(new Date());
    }

    private static interface AccessTokenListener {
        void onAccessToken(String accessToken);
        void onError(Exception error);
    }

    private static class VaultStoreResponse {
        String number;
        String vaultId;
        Date vaultExpireDate;
    }

    /*
     * Last used card persistence
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(numberMasked);
        out.writeInt(cardType.ordinal());
        out.writeInt(expireMonth);
        out.writeInt(expireYear);
        out.writeObject(firstName);
        out.writeObject(lastName);
        out.writeObject(vaultId);
        out.writeObject(vaultExpireDate);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        numberMasked = (String) in.readObject();
        cardType = CardType.values()[in.readInt()];
        expireMonth = in.readInt();
        expireYear = in.readInt();
        firstName = (String) in.readObject();
        lastName = (String) in.readObject();
        vaultId = (String) in.readObject();
        vaultExpireDate = (Date) in.readObject();
    }

    public static PayPalCard getLastUsedCard(Context c) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new BufferedInputStream(c.openFileInput(PERSISTED_LUC_FILENAME)));
            PayPalCard luc = (PayPalCard) is.readObject();
            return luc;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                is.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }

    public static void clearLastUsedCard(Context c) {
        persistLastUsedCardToDisk(c, null);
    }

    private static void persistLastUsedCardToDisk(Context c, PayPalCard card) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new BufferedOutputStream(c.openFileOutput(PERSISTED_LUC_FILENAME, Context.MODE_PRIVATE)));
            os.writeObject(card);
        } catch (Exception ex) {
            // ignore, we'll just lose this last used card
        } finally {
            try {
                os.close();
            } catch (Exception ex) {/* ignore */}
        }
    }

    public void saveAsLastUsedCard(Context c) {
        persistLastUsedCardToDisk(c, this);
    }
}
