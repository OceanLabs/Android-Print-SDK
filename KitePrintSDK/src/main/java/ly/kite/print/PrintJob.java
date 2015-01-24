package ly.kite.print;

import android.content.Context;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
public abstract class PrintJob implements Parcelable, Serializable {

    private static final long serialVersionUID = 0L;

    public abstract BigDecimal getCost(String currencyCode);
    public abstract Set<String> getCurrenciesSupported();

    public abstract ProductType getProductType();
    public abstract int getQuantity();
    public abstract String getTemplateId();
    abstract List<Asset> getAssetsForUploading();
    abstract JSONObject getJSONRepresentation();

    public static PrintJob createPrintJob(List<Asset> assets, ProductType productType) {
        return new PrintsPrintJob(productType.getDefaultTemplate(), assets);
    }

    public static PrintJob createPrintJob(List<Asset> assets, String templateId) {
        return new PrintsPrintJob(templateId, assets);
    }

    public static PrintJob createPostcardPrintJob(String templateId, Asset frontImageAsset, String message, Address address) {
        return new PostcardPrintJob(templateId, frontImageAsset, message, address);
    }

}
