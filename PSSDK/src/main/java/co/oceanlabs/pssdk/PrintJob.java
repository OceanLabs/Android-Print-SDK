package co.oceanlabs.pssdk;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by deonbotha on 09/02/2014.
 */
public abstract class PrintJob {
    public abstract BigDecimal getCost();
    public abstract ProductType getProductType();
    public abstract int getQuantity();
    abstract List<Asset> getAssetsForUploading();
    abstract JSONObject getJSONRepresentation();

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
