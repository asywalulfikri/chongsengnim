package sembako.sayunara.android.ui.component.product.postproduct.model;

import android.net.Uri;

import java.io.Serializable;

public class PodImage implements Serializable {

    Uri source;
    boolean isSignature;

    public Uri getSource() {
        return source;
    }

    public void setSource(Uri source) {
        this.source = source;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public void setSignature(boolean signature) {
        isSignature = signature;
    }
}
