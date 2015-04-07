package ly.kite.printshop;

import java.io.File;
import java.io.Serializable;

/**
 * @author Andreas C. Osowski
 */
public abstract class Photo implements Serializable {
    private PhotoSource source;
    private boolean selected = false;
    private long id;
//    private PhotoTransform transform;
    private File rendered = null;

    public Photo(PhotoSource source, long id) {
        this.source = source;
        this.id = id;
    }

//    public PhotoSource getSource() {
//        return source;
//    }

    public long getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

//    public PhotoTransform getTransform() {
//        return transform;
//    }

//    public void setTransform(PhotoTransform transform) {
//        this.transform = transform;
//    }

    public File getRendered() {
        return rendered;
    }

    public void setRendered(File rendered) {
        this.rendered = rendered;
    }
}
