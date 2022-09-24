package sembako.sayunara.android.service

import retrofit2.Call
import retrofit2.http.*
import sembako.sayunara.android.ui.component.product.editProduct.model.City

interface CityService {

    @GET("/api-indonesia/api/regencies/13.json")
    fun listCountries(): Call<List<City>>

    @GET("api-indonesia/api/districts/1307.json")
    fun listDistrict(): Call<List<City>>

    @GET("api-indonesia/api/districts/1307.json")
    fun listSubDistrict(): Call<List<City>>


    @GET("api-indonesia/api/{type}/{id}.json")
    fun getListLocation(
        @Path("type") type: String,
        @Path("id") id: String
    ): Call<List<City>>?
}