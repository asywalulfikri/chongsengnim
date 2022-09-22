package sembako.sayunara.android.service

import retrofit2.Call
import retrofit2.http.GET
import sembako.sayunara.android.ui.component.product.editProduct.model.City

interface CityService {

    @GET("/api-indonesia/api/regencies/13.json")
    fun listCountries(): Call<List<City>>
}