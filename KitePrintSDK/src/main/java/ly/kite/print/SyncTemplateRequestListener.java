package ly.kite.print;

import java.util.ArrayList;

/**
 * Created by alibros on 06/01/15.
 */


public interface SyncTemplateRequestListener {
    void onSyncComplete(ArrayList<Template> templates);
    void onError(Exception error);
}