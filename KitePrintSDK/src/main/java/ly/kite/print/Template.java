package ly.kite.print;

import android.content.Context;
import android.content.SharedPreferences;
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

import ly.kite.address.Address;

/**
 * Created by alibros on 06/01/15.
 */
public class Template implements Parcelable, Serializable {

    private static final String PERSISTED_TEMPLATES_FILENAME = "templates";
    private static final String PREF_LAST_SYNC_DATE = "last_sync_date";

    private static final ArrayList<TemplateSyncListener> SYNC_LISTENERS = new ArrayList<TemplateSyncListener>();
    private static List<Template> syncedTemplates;
    private static Date lastSyncDate;

    private String id;
    private int quantityPerSheet;
    private String name;
    private Map<String, BigDecimal> costsByCurrencyCode;
    private static SyncTemplateRequest inProgressSyncReq;


    Template(String id, Map<String, BigDecimal> costsByCurrencyCode, String name, int quantityPerSheet) {
        this.costsByCurrencyCode = costsByCurrencyCode;
        this.name = name;
        this.quantityPerSheet = quantityPerSheet;
        this.id = id;
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

    public BigDecimal getCost(String currencyCode) {
        return costsByCurrencyCode.get(currencyCode);
    }

    public Set<String> getCurrenciesSupported() {
        return costsByCurrencyCode.keySet();
    }

    static Template parseTemplate(JSONObject json) throws JSONException {
        String name = json.getString("name");
        int quantityPerSheet = json.optInt("images_per_page");
        String templateId = json.getString("template_id");

        Map<String, BigDecimal> costsByCurrencyCode = new HashMap<String, BigDecimal>();
        JSONArray costsJSON = json.optJSONArray("cost");
        for (int i = 0; i < costsJSON.length(); i++) {
            JSONObject jsonObject = costsJSON.getJSONObject(i);
            String amountString = jsonObject.getString("amount");
            String currency = jsonObject.getString("currency");
            BigDecimal amount = new BigDecimal(amountString);
            costsByCurrencyCode.put(currency, amount);
        }

        return new Template(templateId, costsByCurrencyCode, name, quantityPerSheet);
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
        } catch (Exception ex) {
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
            Map<String, BigDecimal> costsByCurrencyCode = new HashMap<String, BigDecimal>();
            for (int i = 0; i < numCurrencies; ++i) {
                String currencyCode = in.readString();
                BigDecimal cost = new BigDecimal(in.readString());
            }

            return new Template(id, costsByCurrencyCode, name, quantityPerSheet);
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
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();
        quantityPerSheet = in.readInt();
        name = (String) in.readObject();
        costsByCurrencyCode = (Map<String, BigDecimal>) in.readObject();
    }

}
