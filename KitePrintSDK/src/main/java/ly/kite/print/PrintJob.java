package ly.kite.print;

import android.content.Context;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import ly.kite.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
public abstract class PrintJob implements Parcelable, Serializable {

    private static final long serialVersionUID = 0L;

    public abstract BigDecimal getCost();
    public abstract ProductType getProductType();
    public abstract int getQuantity();
    public abstract String getTemplateName();
    abstract List<Asset> getAssetsForUploading();
    abstract JSONObject getJSONRepresentation();

    public static PrintJob createPrintJob(List<Asset> assets, String templateId) {
        return new PrintsPrintJob(templateId, assets);
    }

    public static PrintJob createPostcardPrintJob(List<Asset> assets, String templateId, String message, String location1, String location2, Address address){
        return new PostcardPrintJob(templateId,assets,message,address,location1,location2);
    }


    public static PrintJob createPostcardPrintJobWithCustomBodyStyle(List<Asset> assets, String templateId, String message, String location1, String location2, Address address, String style){
        return new PostcardPrintJob(templateId,assets,message,address,location1,location2, style);
    }

}
