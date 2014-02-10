package co.oceanlabs.pssdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by deonbotha on 09/02/2014.
 */
public class PrintsPrintJob extends PrintJob {

    private final ProductType productType;
    private final List<Asset> assets;

    public PrintsPrintJob(ProductType productType, List<Asset> assets) {
        this.productType = productType;
        this.assets = assets;
    }

    @Override
    public BigDecimal getCost() {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }

    @Override
    public int getQuantity() {
        return assets.size();
    }

    @Override
    List<Asset> getAssetsForUploading() {
        return assets;
    }

    @Override
    JSONObject getJSONRepresentation() {
        JSONArray assets = new JSONArray();
        for (Asset a : this.assets) {
            assets.put("" + a.getId());
        }

        JSONObject json = new JSONObject();
        try {
            json.put("template_id", productType.defaultTemplate);
            json.put("assets", assets);
            json.put("frame_contents", new JSONObject());
        } catch (JSONException ex) {
            throw new RuntimeException(ex); // this should NEVER happen :)
        }
        return json;
    }
}
