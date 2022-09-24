package sembako.sayunara.android.ui.component.account.address

import android.util.Log
import org.json.JSONObject
import sembako.sayunara.android.ui.component.account.register.LocationGet

class AddressServices {


   /* fun addAddress(addAddressView: AddAddressView, locationGet: LocationGet) {

    }


    fun editAddress(addAddressView: AddAddressView, locationGet: LocationGet, id:String) {

    }



    private fun getBodyAddAddress(addAddressView: AddAddressView, accountId : String?, locationGet: LocationGet): JSONObject {
        val root = JSONObject()
        try {
           *//* root.put("placeId",addAddressView.placeID)
            root.put("placeName",addAddressView.placeName.text.toString().trim())
            root.put("accountId", accountId)
            root.put("formattedAddress",addAddressView.location.text.toString().trim())
            root.put("addressDetail",addAddressView.location.text.toString().trim())*//*
            root.put("country","Indonesia")
            root.put("region",locationGet.province)
            root.put("city",locationGet.city)
            root.put("district",locationGet.district)
            root.put("subdistrict",locationGet.subDistrict)
            root.put("latitude",locationGet.latitude)
            root.put("longitude",locationGet.longitude)
            root.put("zipCode",locationGet.zipCode)


            Log.d("chongsne",root.toString())

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return root
    }*/
}