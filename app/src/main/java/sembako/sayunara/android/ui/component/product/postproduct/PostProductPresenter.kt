package sembako.sayunara.android.ui.component.product.postproduct

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import sembako.sayunara.android.BuildConfig
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.helper.OnTextWatcher
import sembako.sayunara.android.ui.base.BasePresenter
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PostProductPresenter : BasePresenter<PostProductContract.PostProductView>(),PostProductContract.PostActionListener ,OnTextWatcher{

    private var messageError = 0

    override fun checkData(isEdit : Boolean) {
        if(validation()){
            postProduct(isEdit)
        }else{
            view?.showErrorValidation(messageError)
        }

    }

    fun validation(): Boolean {

        val i: Boolean

        if (view?.mProductName!!.isNotEmpty() && view?.mProductType!!.isNotEmpty() && view?.mProductWeight!!.isNotEmpty()&&view?.mUrlImage1!!.isNotEmpty()&&view?.mProductUnit!!.isNotEmpty() &&view?.mProductDescription!!.isNotEmpty()) {
            i = true
            view?.setColorButton(R.color.colorOrange)

        } else {
            view?.setColorButton(R.color.colorOrange)
            i = false
            when {
                view?.mProductName!!.isEmpty() -> messageError = R.string.text_product_name_empty
                view?.mProductType!!.isEmpty() -> messageError = R.string.text_product_type_empty
                view?.mProductWeight!!.isEmpty() -> messageError = R.string.text_product_weight_empty
                view?.mProductUnit!!.isEmpty() -> messageError = R.string.text_product_unit_empty
                view?.mUrlImage1!!.isEmpty() -> messageError = R.string.text_product_image_empty
                view?.mProductPrice!!.isEmpty() -> messageError = R.string.text_product_price_empty
                view?.mProductStock!!.isEmpty() -> messageError = R.string.text_product_stock_empty
                view?.mProductDescription!!.isEmpty() -> messageError = R.string.text_product_description_empty
                view?.mUrlImage1!!.isEmpty()-> messageError = R.string.text_error_image_empty

            }
        }

        return i

    }


    @SuppressLint("SimpleDateFormat")
    override fun postProduct(isEdit: Boolean) {

        val uuid = UUID.randomUUID().toString()
        val arrayList: MutableList<String> = ArrayList()
        arrayList.add(view?.mUrlImage1.toString())
        if (view?.mUrlImage2.toString().isNotEmpty()) {
            arrayList.add(view?.mUrlImage2.toString())
        }
        if (view?.mUrlImage3.toString().isNotEmpty()) {
            arrayList.add(view?.mUrlImage3.toString())
        }

        val sdf = SimpleDateFormat("dd-M-yyyy-hh-mm-ss")
        val currentDate = sdf.format(Date())

        val generateUUID = "$currentDate-$uuid"
        view?.loadingIndicator(true)
        val mFireBaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val obj: MutableMap<String?, Any?> = java.util.HashMap()
        val tsLong = System.currentTimeMillis() / 1000

        val tz = TimeZone.getTimeZone("GMT+7")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        val timestamp = Timestamp(System.currentTimeMillis())
        df.timeZone = tz
        val nowAsISO = df.format(Date())
        var arraySearch: List<String>
        var dataSearch1 = view?.mProductName.toString().toLowerCase().replace(".","").replace(",","")
        var dataSearch2 =  view?.mProductDescription.toString().toLowerCase().replace(".","").replace(",","")

        var text = dataSearch1+" "+dataSearch2
        arraySearch = text.split(" ")

        //Post Data
        obj[Constant.ProductKey.productName] = view?.mProductName
        obj[Constant.UserKey.userId] = view?.getUser!!.profile.userId


        var arrayType: List<String>
        var type = view?.mProductType.toString().toLowerCase()
        arrayType = type.split(",")

        obj["type"] = arrayType
        obj["search"] = arraySearch
        obj["images"] = arrayList


        val coordinate: MutableMap<String, String?> = HashMap()
        coordinate["latitude"] = view?.mLatitude
        coordinate["longitude"] = view?.mLongitude
        obj["coordinate"] = coordinate
        obj["description"] = view?.mProductDescription
        obj["unit"] = view?.mProductUnit

        if(view!!.mProductDiscount.toString().isEmpty()){
            obj["discount"] = 0
        }else{
            obj["discount"] = view!!.mProductDiscount.toString().toInt()
        }
        obj["price"] = view?.mProductPrice!!.toInt()
        obj["stock"] = view?.mProductStock!!.toInt()
        obj["weight"] = view?.mProductWeight!!.toDouble()

        obj["isActive"] = true
        obj["isHighLight"] = view!!.isHighLight
        val information = (Build.MANUFACTURER + " " + Build.MODEL + " , Version OS :" + Build.VERSION.RELEASE)



        val phone: MutableMap<String, Any?> = HashMap()
        phone[Constant.UserKey.versionName] = BuildConfig.VERSION_NAME
        phone[Constant.UserKey.versionAndroid] = Build.VERSION.SDK_INT.toString()
        phone[Constant.UserKey.versionCode] = BuildConfig.VERSION_CODE
        phone[Constant.UserKey.detailDevices] = information
        obj[Constant.UserKey.devices] = phone



        val datePublishedData: MutableMap<String, Any> = java.util.HashMap()
        datePublishedData[Constant.UserKey.iso] = nowAsISO
        datePublishedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.createdAt] = datePublishedData


        val dateSubmittedData: MutableMap<String, Any> = java.util.HashMap()
        dateSubmittedData[Constant.UserKey.iso] = nowAsISO
        dateSubmittedData[Constant.UserKey.timestamp] = tsLong
        obj[Constant.UserKey.updatedAt] = dateSubmittedData

        val idProduct = if (isEdit) {
            view!!.mProductId.toString()
        } else {
            generateUUID
        }

        if (!isEdit) {
            obj["id"] = generateUUID
        }else{
            obj["id"] = idProduct
        }


        mFireBaseFireStore.collection(Constant.Collection.COLLECTION_PRODUCT).document(idProduct)
                .set(obj)
                .addOnSuccessListener {
                    view?.loadingIndicator(false)
                    onRequestSuccess()
                }
                .addOnFailureListener { e ->
                    view?.loadingIndicator(false)
                    onRequestFailed(e.hashCode())
                }
    }


    override fun onRequestSuccess() {
        view?.onBack()
    }

    override fun onRequestFailed(error: Int) {

    }

    override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(editText: EditText?, editable: Editable?) {

    }

}