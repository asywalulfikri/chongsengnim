
package sembako.sayunara.android.screen.product.listproduct.model;

import com.google.firebase.Timestamp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class createdAt implements Serializable {

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @SerializedName("iso")
    @Expose
    private String iso;
    @SerializedName("timestamp")
    @Expose
    private Timestamp timestamp;

}
