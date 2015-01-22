package ly.kite.print;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ly.kite.payment.PayPalCard;

/**
 * Created by alibros on 06/01/15.
 */
public class Template implements Parcelable, Serializable {

    private String template_id;
    private int images_per_page;
    private String name;
    private ArrayList<TemplateCost> costs;
    private int custom_pack_quantity = 0;

    public Template(ArrayList<TemplateCost> costs, String name, int images_per_page, String template_id, int custom_pack_quantity) {
        this.costs = costs;
        this.name = name;
        this.images_per_page = images_per_page;
        this.template_id = template_id;
        this.custom_pack_quantity = custom_pack_quantity;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public int getImages_per_page() {
        return images_per_page;
    }

    public String getName() {
        return name;
    }

    public ArrayList<TemplateCost> getCosts() {
        return costs;
    }


    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public void setImages_per_page(int images_per_page) {
        this.images_per_page = images_per_page;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(ArrayList<TemplateCost> costs) {
        this.costs = costs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    static Template parseTemplate(JSONObject json) throws JSONException {
        String jname = json.getString("name");
        int jimages_per_page = json.optInt("images_per_page");
        int jcustom_pack_quantity = json.optInt("custom_pack_quantity");
        String jtemplate_id = json.getString("template_id");
        JSONArray jcost = json.optJSONArray("cost");
        ArrayList<TemplateCost> tempCosts = new ArrayList<TemplateCost>();
        for (int i = 0; i < jcost.length(); i++) {

            JSONObject jsonObject = jcost.getJSONObject(i);
            String amount = jsonObject.getString("amount");
            String currency = jsonObject.getString("currency");

            TemplateCost costTemp = new TemplateCost(currency,amount);
            tempCosts.add(costTemp);


        }

        return new Template(tempCosts, jname, jimages_per_page, jtemplate_id, jcustom_pack_quantity);
    }



    public static void syncTemplates(final Context context){

        SyncTemplateRequest request = new SyncTemplateRequest();
        request.sync(new SyncTemplateRequestListener() {
            @Override
            public void onSyncComplete(SyncTemplateRequest request, ArrayList<Template> templates) {

                SharedPreferences settings = context.getSharedPreferences("ly.kite.sharedpreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                Gson gson = new Gson();

                String templatesJson = gson.toJson(templates);
                editor.putString("templates",templatesJson);
                editor.commit();

                for (Template template : templates) {
                    String json = gson.toJson(template);
                    editor.putString(template.getTemplate_id(), json);
                    editor.commit();

                }

            }

            @Override
            public void onError(SyncTemplateRequest req, Exception error) {

            }
        });
    }

    public static int getSyncedTemplateNumberOfImages(String template_id) {

        SharedPreferences settings = KitePrintSDK.getAppContext().getSharedPreferences("ly.kite.sharedpreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = settings.getString(template_id, "");
        Template template = gson.fromJson(json, Template.class);
        return template.getImages_per_page();
    }

    public static String getSupportedCurrency(){

            String userCurrencyCode = KitePrintSDK.getUserCurrencyCode();

            if(userCurrencyCode.equals("GBP")) {
                return userCurrencyCode;

            }else if(userCurrencyCode.equals("EUR")){
                return userCurrencyCode;

            }else if(userCurrencyCode.equals("USD")){
                return userCurrencyCode;
            }else if(userCurrencyCode.equals("SGD")){
                return userCurrencyCode;
            }else if(userCurrencyCode.equals("AUD")){
                return userCurrencyCode;
            }else if(userCurrencyCode.equals("NZD")){
                return userCurrencyCode;
            }else if(userCurrencyCode.equals("CAD")){
                return userCurrencyCode;
            } else {
                return "GBP";
            }


    }

    public static PayPalCard.Currency getPayPalCurrency(){

        String userCurrencyCode = getSupportedCurrency();


        if(userCurrencyCode.equals("GBP")) {
            return PayPalCard.Currency.GBP;

        }else if(userCurrencyCode.equals("EUR")){
            return PayPalCard.Currency.EUR;

        }else if(userCurrencyCode.equals("USD")){
            return PayPalCard.Currency.USD;
        }else if(userCurrencyCode.equals("SGD")){
            return PayPalCard.Currency.SGD;
        }else if(userCurrencyCode.equals("AUD")){
            return PayPalCard.Currency.AUD;
        }else if(userCurrencyCode.equals("NZD")){
            return PayPalCard.Currency.NZD;
        }else if(userCurrencyCode.equals("CAD")){
            return PayPalCard.Currency.CAD;
        } else {
            return PayPalCard.Currency.GBP;
        }

    }


    public static float getCostForTemplate(String template_id){

        SharedPreferences settings = KitePrintSDK.getAppContext().getSharedPreferences("ly.kite.sharedpreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = settings.getString(template_id, "");
        Template template = gson.fromJson(json, Template.class);

        String productCost = "0.0";
        if (template != null) {
            ArrayList<TemplateCost> cost = template.getCosts();
            if (cost != null) {
                for (TemplateCost o : cost) {
                    String currency = o.getCurrency();
                    if (currency.equals(getSupportedCurrency())) {
                        productCost = o.getAmount();
                    }
                }
            }
        }
        return Float.parseFloat(productCost);
    }


    public static ArrayList<Template> getSyncedTemplates(){

        SharedPreferences settings = KitePrintSDK.getAppContext().getSharedPreferences("ly.kite.sharedpreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = settings.getString("templates", "");
        java.lang.reflect.Type listOfTemplates = new TypeToken<ArrayList<Template>>(){}.getType();
        return gson.fromJson(json, listOfTemplates);

    }


    public void setCustom_pack_quantity(int custom_pack_quantity) {
        this.custom_pack_quantity = custom_pack_quantity;
    }

}
