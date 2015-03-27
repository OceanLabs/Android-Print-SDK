package ly.kite.print;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alibros on 06/01/15.
 */
public class Template implements Parcelable, Serializable {

    public enum TemplateUI implements Serializable{
        NA, Circle, Rectangle, Phone_Case, Frame, Poster;

        public String toString(){
            switch(this){
                case NA:
                    return "NA";
                case Circle:
                    return "Circle";
                case Rectangle:
                    return "Rectangle";
                case Phone_Case:
                    return "Phone_Case";
                case Frame:
                    return "Frame";
                case Poster:
                    return "Poster";
            }
            return null;
        }

        public static TemplateUI getEnum(String value){
            if(value.equalsIgnoreCase(Circle.toString()))
                return TemplateUI.Circle;
            else if(value.equalsIgnoreCase(Rectangle.toString()))
                return TemplateUI.Rectangle;
            else if(value.equalsIgnoreCase(Phone_Case.toString()))
                return TemplateUI.Phone_Case;
            else if(value.equalsIgnoreCase(Frame.toString()))
                return TemplateUI.Frame;
            else if(value.equalsIgnoreCase(Poster.toString()))
                return TemplateUI.Poster;
            else
                return TemplateUI.NA;
        }
    }

    private static final String PERSISTED_TEMPLATES_FILENAME = "templates";
    private static final String PREF_LAST_SYNC_DATE = "last_sync_date";

    private static final ArrayList<TemplateSyncListener> SYNC_LISTENERS = new ArrayList<TemplateSyncListener>();
    private static List<Template> syncedTemplates;
    private static Date lastSyncDate;

    private String id;
    private int quantityPerSheet;
    private String name;
    private Map<String, BigDecimal> costsByCurrencyCode;
    private boolean enabled;
    private String coverPhotoURL;
    private List<String> productPhotographyURLs;
    private int labelColor;
    private TemplateUI templateUI;
    private String templateClass;
    private String templateType;
    private String classPhotoURL;

    private PointF sizeCm;
    private PointF sizeInches;
    private PointF sizePx;
    private String productCode;
    private static SyncTemplateRequest inProgressSyncReq;

    Template(String id, Map<String, BigDecimal> costsByCurrencyCode, String name, int quantityPerSheet,
             String coverPhotoURL, int labelColor, TemplateUI templateUI, List<String> productPhotographyURLs,
             PointF sizeCm, PointF sizeInch, PointF sizePx, String classPhotoURL, String templateType, String templateClass) {
        this.costsByCurrencyCode = costsByCurrencyCode;
        this.name = name;
        this.quantityPerSheet = quantityPerSheet;
        this.id = id;
        this.coverPhotoURL = coverPhotoURL;
        this.labelColor = labelColor;
        this.templateUI = templateUI;
        this.productPhotographyURLs = productPhotographyURLs;
        this.sizeCm = sizeCm;
        this.sizeInches = sizeInch;
        this.sizePx = sizePx;
        this.templateClass = templateClass;
        this.templateType = templateType;
        this.classPhotoURL = classPhotoURL;
    }

    public String getId() {
        return id;
    }

    public int getQuantityPerSheet() {
        return quantityPerSheet;
    }

    public String getName() {
        return name;
    }

    public int getLabelColor(){
        return labelColor;
    }

    public BigDecimal getCost(String currencyCode) {
        return costsByCurrencyCode.get(currencyCode);
    }

    public Set<String> getCurrenciesSupported() {
        return costsByCurrencyCode.keySet();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCoverPhotoURL() {
        return coverPhotoURL;
    }

    public List<String> getProductPhotographyURLs() {
        return productPhotographyURLs;
    }

    public TemplateUI getTemplateUI(){
        return templateUI;
    }

    public String getProductCode() {
        return productCode;
    }

    public PointF getSizeCm() {
        return sizeCm;
    }

    public PointF getSizeInches() {
        return sizeInches;
    }

    public PointF getSizePx() {
        return sizePx;
    }

    public String getTemplateClass() {
        return templateClass;
    }

    public String getTemplateType() {
        return templateType;
    }

    public String getClassPhotoURL() {
        return classPhotoURL;
    }

    static Template parseTemplate(JSONObject json) throws JSONException {
        String name = json.getString("name");
        int quantityPerSheet = json.optInt("images_per_page");
        String templateId = json.getString("template_id");
        JSONObject productJSON = json.getJSONObject("product");
        String templateClass = productJSON.optString("ios_sdk_product_class");
        String templateType = productJSON.optString("ios_sdk_product_type");
        String uiClass = productJSON.optString("ios_sdk_ui_class");
        String coverPhoto = productJSON.optString("ios_sdk_cover_photo");
        String classPhoto = productJSON.optString("ios_sdk_class_photo");
        JSONArray imagesJSON = productJSON.optJSONArray("ios_sdk_product_shots");
        JSONArray colorJSON = productJSON.optJSONArray("ios_sdk_label_color");
        int color = Color.BLACK;
        if (colorJSON != null) {
            color = Color.argb(255, colorJSON.getInt(0), colorJSON.getInt(1), colorJSON.getInt(2));
        }

        JSONObject sizesJSON = productJSON.optJSONObject("size");
        JSONObject cmJSON = sizesJSON.optJSONObject("cm");
        JSONObject inchJSON = sizesJSON.optJSONObject("inch");
        JSONObject pxJSON = sizesJSON.optJSONObject("px");

        PointF sizeCm = new PointF((float)cmJSON.getDouble("width"), (float)cmJSON.getDouble("height"));
        PointF sizeInch = new PointF((float)inchJSON.getDouble("width"), (float)inchJSON.getDouble("height"));
        PointF sizePx = new PointF(0,0);

        if (pxJSON != null) {
            sizePx = new PointF((float) pxJSON.getDouble("width"), (float) pxJSON.getDouble("height"));
        }

        ArrayList<String> images = new ArrayList<String>();
        for (int imageIndex = 0; imageIndex < imagesJSON.length(); imageIndex++){
            images.add(imagesJSON.getString(imageIndex));
        }

        Map<String, BigDecimal> costsByCurrencyCode = new HashMap<String, BigDecimal>();
        JSONArray costsJSON = json.optJSONArray("cost");
        for (int i = 0; i < costsJSON.length(); i++) {
            JSONObject jsonObject = costsJSON.getJSONObject(i);
            String amountString = jsonObject.getString("amount");
            String currency = jsonObject.getString("currency");
            BigDecimal amount = new BigDecimal(amountString);
            costsByCurrencyCode.put(currency, amount);
        }

        return new Template(templateId, costsByCurrencyCode, name, quantityPerSheet,
                coverPhoto, color, TemplateUI.getEnum(uiClass), images,
                sizeCm, sizeInch, sizePx, classPhoto, templateType, templateClass
        );
    }

    public static Date getLastSyncDate() {
        getTemplates(); // this forces lastSyncDate & templates to be read from disk if currently null
        return lastSyncDate;
    }

    public static interface TemplateSyncListener {
        void onSuccess();
        void onError(Exception error);
    }

    public static void sync(Context context, TemplateSyncListener listener) {
        synchronized (SYNC_LISTENERS) {
            SYNC_LISTENERS.add(listener);
        }
        sync(context);
    }

    public static void sync(Context context) {
        if (isSyncInProgress()) {
            return;
        }

        final Context appContext = context.getApplicationContext();
        inProgressSyncReq = new SyncTemplateRequest();
        inProgressSyncReq.sync(new SyncTemplateRequestListener() {
            @Override
            public void onSyncComplete(ArrayList<Template> templates) {
                inProgressSyncReq = null;
                persistTemplatesToDiskAsLatest(appContext, templates);
                synchronized (SYNC_LISTENERS) {
                    for (TemplateSyncListener listener : SYNC_LISTENERS) {
                        listener.onSuccess();
                    }
                    SYNC_LISTENERS.clear();
                }
            }

            @Override
            public void onError(Exception error) {
                inProgressSyncReq = null;
                synchronized (SYNC_LISTENERS) {
                    for (TemplateSyncListener listener : SYNC_LISTENERS) {
                        listener.onError(error);
                    }
                    SYNC_LISTENERS.clear();
                }
            }
        });
    }

    public static boolean isSyncInProgress() {
        return inProgressSyncReq != null;
    }

    private static void persistTemplatesToDiskAsLatest(Context context, List<Template> templates) {
        syncedTemplates = templates;
        lastSyncDate = new Date();

        // Write sync date
        SharedPreferences settings = context.getSharedPreferences(KitePrintSDK.KITE_SHARED_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREF_LAST_SYNC_DATE, lastSyncDate.getTime());
        editor.commit();

        // Write templates
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new BufferedOutputStream(context.openFileOutput(PERSISTED_TEMPLATES_FILENAME, Context.MODE_PRIVATE)));
            os.writeObject(templates);
        } catch (Exception ex) {
            // ignore, we'll just lose this persist for now
        } finally {
            try {
                os.close();
            } catch (Exception ex) {/* ignore */}
        }
    }

    public static Template getTemplate(String templateId) {
        List<Template> templates = getTemplates();
        for (Template template : templates) {
            if (template.getId().equals(templateId)) {
                return template;
            }
        }

        throw new KitePrintSDKException("Couldn't find Kite product template with id: '" + templateId + "'", KitePrintSDKException.ErrorCode.TEMPLATE_NOT_FOUND);
    }

    public static List<Template> getTemplates() {
        if (syncedTemplates != null) {
            Log.v("here", "synced: " + syncedTemplates.size());
            return syncedTemplates;
        }

        // Try read last sync date
        SharedPreferences settings = KitePrintSDK.getApplicationContext().getSharedPreferences(KitePrintSDK.KITE_SHARED_PREFERENCES, 0);
        if (settings.contains(PREF_LAST_SYNC_DATE)) {
            lastSyncDate = new Date(settings.getLong(PREF_LAST_SYNC_DATE, 0));
        }

        // Try read previously persisted templates from disk
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new BufferedInputStream(KitePrintSDK.getApplicationContext().openFileInput(PERSISTED_TEMPLATES_FILENAME)));
            syncedTemplates = (List<Template>) is.readObject();
            return syncedTemplates;
        } catch (FileNotFoundException ex) {
            return new ArrayList<Template>();
        }
        catch (InvalidClassException e){
            Log.v("", "Saved templates format outdated.");
            return new ArrayList<Template>();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                is.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeInt(quantityPerSheet);
        parcel.writeString(name);
        parcel.writeInt(costsByCurrencyCode.size());
        parcel.writeString(coverPhotoURL);
        parcel.writeInt(labelColor);
        parcel.writeSerializable(templateUI);
        parcel.writeStringList(productPhotographyURLs);
        parcel.writeFloat(sizeCm.x);
        parcel.writeFloat(sizeCm.y);
        parcel.writeFloat(sizeInches.x);
        parcel.writeFloat(sizeInches.y);
        parcel.writeFloat(sizePx.x);
        parcel.writeFloat(sizePx.y);
        parcel.writeString(templateClass);
        parcel.writeString(templateType);
        parcel.writeString(classPhotoURL);
        for (String currencyCode : costsByCurrencyCode.keySet()) {
            BigDecimal cost = costsByCurrencyCode.get(currencyCode);
            parcel.writeString(currencyCode);
            parcel.writeString(cost.toString());
        }
    }

    public static final Parcelable.Creator<Template> CREATOR = new Parcelable.Creator<Template>() {
        public Template createFromParcel(Parcel in) {
            String id = in.readString();
            int quantityPerSheet = in.readInt();
            String name = in.readString();
            int numCurrencies = in.readInt();
            String coverPhoto = in.readString();
            int labelColor = in.readInt();
            TemplateUI templateUI = (TemplateUI) in.readSerializable();
            List<String> productPhotographyURLs = new ArrayList<String>();
            in.readStringList(productPhotographyURLs);
            PointF sizeCm = new PointF(in.readFloat(), in.readFloat());
            PointF sizeInch = new PointF(in.readFloat(), in.readFloat());
            PointF sizePx = new PointF(in.readFloat(), in.readFloat());
            String templateClass = in.readString();
            String templateType = in.readString();
            String classPhotoURL = in.readString();

            Map<String, BigDecimal> costsByCurrencyCode = new HashMap<String, BigDecimal>();
            for (int i = 0; i < numCurrencies; ++i) {
                String currencyCode = in.readString();
                BigDecimal cost = new BigDecimal(in.readString());
                costsByCurrencyCode.put(currencyCode, cost);
            }

            return new Template(id, costsByCurrencyCode, name, quantityPerSheet, coverPhoto, labelColor,
                    templateUI, productPhotographyURLs, sizeCm, sizeInch, sizePx, classPhotoURL, templateType, templateClass);
        }

        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(id);
        out.writeInt(quantityPerSheet);
        out.writeObject(name);
        out.writeObject(costsByCurrencyCode);
        out.writeObject(coverPhotoURL);
        out.writeObject(labelColor);
        out.writeObject(templateUI);
        out.writeObject(productPhotographyURLs);
//        out.writeObject(sizeCm);
//        out.writeObject(sizeInches);
//        out.writeObject(sizePx);
        out.writeObject(templateClass);
        out.writeObject(templateType);
        out.writeObject(classPhotoURL);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();
        quantityPerSheet = in.readInt();
        name = (String) in.readObject();
        costsByCurrencyCode = (Map<String, BigDecimal>) in.readObject();
        coverPhotoURL = (String) in.readObject();
        labelColor = (int) in.readObject();
        templateUI = (TemplateUI) in.readObject();
        productPhotographyURLs = (List<String>) in.readObject();
//        sizeCm = (PointF)in.readObject();
//        sizeInches = (PointF)in.readObject();
//        sizePx = (PointF)in.readObject();
        templateClass = (String)in.readObject();
        templateType = (String)in.readObject();
        classPhotoURL = (String)in.readObject();
    }

}
