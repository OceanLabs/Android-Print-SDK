package ly.kite.print;

import java.util.ArrayList;

/**
 * Created by alibros on 06/01/15.
 */


public interface SyncTemplateRequestListener {
    void onSyncComplete(SyncTemplateRequest request, ArrayList<Template> templates);
    void onError(SyncTemplateRequest req, Exception error);
}