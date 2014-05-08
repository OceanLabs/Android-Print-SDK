package ly.kite.print;

import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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

    public static PrintJob createPrintJob(List<Asset> assets, ProductType productType) {
        if (productType == ProductType.POSTCARD) {
            throw new IllegalArgumentException("Postcards are not yet supported. Coming very soon!");
        }

        return new PrintsPrintJob(productType, assets);
    }

    public static PrintJob createSquaresPrintJob(List<Asset> assets) {
        return new PrintsPrintJob(ProductType.SQUARES, assets);
    }

    public static PrintJob createMiniSquaresPrintJob(List<Asset> assets) {
        return new PrintsPrintJob(ProductType.MINI_SQUARES, assets);
    }

    public static PrintJob createPolaroidsPrintJob(List<Asset> assets) {
        return new PrintsPrintJob(ProductType.POLAROIDS, assets);
    }

    public static PrintJob createMiniPolaroidsPrintJob(List<Asset> assets) {
        return new PrintsPrintJob(ProductType.MINI_POLAROIDS, assets);
    }

    public static PrintJob createMagnetsPrintJob(List<Asset> assets) {
        return new PrintsPrintJob(ProductType.MAGNETS, assets);
    }
}
