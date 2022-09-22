package sembako.sayunara.android.ui.component.product.editProduct.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class City : Serializable {
    @SerializedName("id")
    @Expose
    private var id: String? = null

    @SerializedName("province_id")
    @Expose
    private var provinceId: String? = null

    @SerializedName("name")
    @Expose
    private var name: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getProvinceId(): String? {
        return provinceId
    }

    fun setProvinceId(provinceId: String?) {
        this.provinceId = provinceId
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }
}