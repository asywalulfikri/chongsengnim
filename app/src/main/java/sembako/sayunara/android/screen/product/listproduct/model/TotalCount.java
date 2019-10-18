
package sembako.sayunara.android.screen.product.listproduct.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TotalCount implements Serializable {

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(long totalResponses) {
        this.totalResponses = totalResponses;
    }

    public long getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(long totalSell) {
        this.totalSell = totalSell;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    @SerializedName("totalLikes")
    @Expose
    private long totalLikes;
    @SerializedName("totalResponses")
    @Expose
    private long totalResponses;
    @SerializedName("totalSell")
    @Expose
    private long totalSell;
    @SerializedName("totalViews")
    @Expose
    private long totalViews;

}
