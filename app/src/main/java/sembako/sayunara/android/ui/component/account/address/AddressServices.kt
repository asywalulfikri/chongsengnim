package sembako.sayunara.android.ui.component.account.address

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.component.account.login.data.model.User

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



    internal fun getList(view : AddressView.ViewList, fireBaseFireStore: FirebaseFirestore, user : User?,) {
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_ADDRESS).document(user?.profile?.userId.toString()).collection(Constant.Collection.COLLECTION_ADDRESS_LIST)
        val query: Query = collectionReference
            .orderBy("createdAt.timestamp", Query.Direction.DESCENDING)
            .limit(10)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.loadingIndicator(false)
                view.onRequestSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestFailed(task.exception?.message.toString())
            }
        }

    }

    internal fun getListDetail(view : AddressView.DetailList, fireBaseFireStore: FirebaseFirestore, user : User?,) {
        view.loadingIndicator(true)
        val collectionReference = fireBaseFireStore.collection(Constant.Collection.COLLECTION_ADDRESS).document(user?.profile?.userId.toString())
        val query: DocumentReference = collectionReference
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.onRequestDetailSuccess(task.result)

            } else {
                view.loadingIndicator(false)
                view.onRequestDetailFailed(task.exception?.message.toString())
            }
        }

    }

}