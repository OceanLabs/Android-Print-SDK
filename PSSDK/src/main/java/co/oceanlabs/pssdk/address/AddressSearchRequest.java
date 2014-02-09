package co.oceanlabs.pssdk.address;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import co.oceanlabs.pssdk.BaseRequest;
import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PSPrintSDKException;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class AddressSearchRequest {

    private BaseRequest searchRequest;

    public void cancelSearch() {
        if (searchRequest != null) {
            searchRequest.cancel();
            searchRequest = null;
        }
    }

    public void search(String query, Country country, AddressSearchRequestListener listener) {
        String queryParams = null;
        try {
            queryParams = String.format("search_term=%s&country_code=%s", URLEncoder.encode(query, "utf-8"), country.getCodeAlpha3());
        } catch (UnsupportedEncodingException e) {
            listener.onError(e);
            return;
        }

        String url = String.format("https://%s/v1/address/search?%s", PSPrintSDK.APIHostname, queryParams);
        startSearch(url, listener);
    }

    public void searchForAddress(Address address, AddressSearchRequestListener listener) {
        String queryParams = null;
        try {
            if (address.getId() == null) {
                if (address.getCountry().getCodeAlpha3().equals("GBR") && address.getZipOrPostalCode() != null && address.getZipOrPostalCode().length() > 0) {
                    String line1 = address.getLine1() == null ? "" : address.getLine1();
                    queryParams = String.format("postcode=%s&address_line_1=%s&country_code=GBR", URLEncoder.encode(address.getZipOrPostalCode(), "utf-8"), URLEncoder.encode(line1, "utf-8"));
                } else {
                    queryParams = String.format("search_term=%s&country_code=%s", URLEncoder.encode(address.getDisplayAddressWithoutRecipient(), "utf-8"), address.getCountry().getCodeAlpha3());
                }
            } else {
                queryParams = String.format("@address_id=%s&country_code=%s", URLEncoder.encode(address.getId(), "utf-8"), address.getCountry().getCodeAlpha3());
            }
        } catch (UnsupportedEncodingException ex) {
            listener.onError(ex);
            return;
        }

        String url = String.format("https://%s/v1/address/search?%s", PSPrintSDK.APIHostname, queryParams);
        startSearch(url, listener);
    }

    private void startSearch(String url, final AddressSearchRequestListener listener) {
        searchRequest = new BaseRequest(BaseRequest.HttpMethod.GET, url, null, null);
        searchRequest.start(new BaseRequest.BaseRequestListener() {
            @Override
            public void onSuccess(int httpStatusCode, JSONObject json) {
                searchRequest = null;
                JSONObject error = json.optJSONObject("error");
                JSONArray choices = json.optJSONArray("choices");
                JSONObject unique = json.optJSONObject("unique");
                if (error != null) {
                    String message = error.optString("message");
                    listener.onError(new PSPrintSDKException(message));
                } else if (choices != null) {
                    ArrayList<Address> addrs = new ArrayList<Address>();
                    for (int i = 0; i < choices.length(); ++i) {
                        JSONObject choice = choices.optJSONObject(i);
                        addrs.add(Address.createPartialAddress(choice.optString("address_id"), choice.optString("display_address")));
                    }
                    listener.onMultipleChoices(addrs);
                } else {
                    assert unique != null : "oops this should be the only option left";
                    Address addr = new Address();
                    addr.setLine1(unique.optString("address_line_1"));
                    addr.setLine2(unique.optString("address_line_2"));
                    addr.setCity(unique.optString("city"));
                    addr.setStateOrCounty(unique.optString("county_state"));
                    addr.setZipOrPostalCode(unique.optString("postcode"));
                    addr.setCountry(Country.getInstance(unique.optString("country_code")));
                    listener.onUniqueAddress(addr);
                }
            }

            @Override
            public void onError(Exception ex) {
                searchRequest = null;
                listener.onError(ex);
            }
        });
    }


}
