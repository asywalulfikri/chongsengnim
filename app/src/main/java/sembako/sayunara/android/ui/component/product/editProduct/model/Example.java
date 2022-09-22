package sembako.sayunara.android.ui.component.product.editProduct.model;



import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("province_id")
    @Expose
    private String provinceId;
    @SerializedName("name")
    @Expose
    private String name;
    private final static long serialVersionUID = 3002539136418476954L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
