package sembako.sayunara.android.ui.component.product.postproduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_product.*
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.ui.base.BasePresenter
import sembako.sayunara.android.ui.base.ConnectionActivity
import sembako.sayunara.android.ui.component.account.login.data.model.User
import sembako.sayunara.android.ui.component.product.listproduct.model.Product
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PostProductActivity : ConnectionActivity(),PostProductContract.PostProductView{
    override val mProductId: String?
        get() = product.id
    override val mProductName: String?
        get() = etProductName.text.toString().trim()
    override val mProductType: String?
        get() = etProductType.text.toString().trim()
    override val mProductWeight: String?
        get() = etProductWeight.text.toString().trim()
    override val mProductUnit: String?
        get() = etProductUnit.text.toString().trim()
    override val mProductPrice: String?
        get() = etProductPrice.text.toString().trim().replace("Rp","").trim().replace(",","")
    override val mProductStock: String?
        get() = etProductStock.text.toString().trim()
    override val mProductDescription: String?
        get() = etProductDescription.text.toString().trim()
    override val mProductDiscount: String?
        get() = etProductDiscount.text.toString()

    override val mLatitude: String
        get() = latitude
    override val mLongitude: String
        get() = longitude
    override val mVersionName: String?
        get() = getVersionName(this)
    override var mUrlImage1: String? = null
        get() = url1
    override var mUrlImage2: String? = null
        get() = url2
    override var mUrlImage3: String? = null
        get() = url3
    override val isHighLight: Boolean?
        get() =  highLight
    override val mArrayType: ArrayList<String>
        get() = arrayType

    @Inject
    lateinit var mPostProductPresenter: PostProductPresenter
    private lateinit var mPostProductDialog: PostProductDialog
    var arrayType = ArrayList<String>()
    var isEdit = false
    var highLight = false
    lateinit var product : Product
    private var isLoad = false
    var url1 : String = ""
    var url2 : String? = ""
    var url3 : String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        mPostProductPresenter = PostProductPresenter()
        mPostProductDialog = PostProductDialog(this)
        mPostProductPresenter.attachView(this)

        if(intent.hasExtra(Constant.IntentExtra.product)){
            product = intent.getSerializableExtra(Constant.IntentExtra.product) as Product
            setEdit(product)
            isEdit = true
        }
        setupViews()

    }

    override fun showErrorValidation(message: Int) {
        setToast(message)
    }

    override fun loadingIndicator(isLoading: Boolean) {
        setDialog(isLoading)

    }

    override fun setColorButton(color: Int) {
        setColorTintButton(btnSubmit, getColors(this,color))
    }

    override val getUser: User?
        get() = user

    override fun onBack() {
        isLoad = true
        onBackPressed()
    }

    override fun setPresenter(presenter: BasePresenter<*>) {

    }


    private fun setupViews(){
        setupToolbar(toolbar, getString(R.string.text_add_product))
        mPostProductPresenter.validation()
        etProductUnit.setOnClickListener {
            mPostProductDialog.showUnitDialog()
        }

        ivImage.setOnClickListener {
            mPostProductDialog.takeImage()
        }
        etProductType.setOnClickListener {
            mPostProductDialog.showDialogType()
        }

        etHighLight.setOnClickListener {
            mPostProductDialog.dialogHighLight()
        }

        btnSubmit.setOnClickListener{
            mPostProductPresenter.checkData(isEdit)
        }

        rlFirst.setOnClickListener {
            mPostProductDialog.dialogUrl(1)
        }
        rlSecond.setOnClickListener {
            mPostProductDialog.dialogUrl(2)
        }
        rlThird.setOnClickListener {
            mPostProductDialog.dialogUrl(3)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPostProductDialog.onActivityResult(requestCode,resultCode,data)

    }
    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constant.IntentExtra.isLoad, isLoad)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        mPostProductPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPostProductPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPostProductPresenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setEdit(product : Product){
        etProductName.setText(product.name)
        mPostProductDialog.setImage(1,product.images[0],ivFirst,progressBar1,ivCloseFirst)
        url1 = product.images[0]
        if(product.images.size>1){
            mPostProductDialog.setImage(2,product.images[1],ivSecond,progressBar2,ivCloseSecond)
            url2 = product.images[1]
        }
        if(product.images.size>2){
            mPostProductDialog.setImage(3,product.images[2],ivThird,progressBar3,ivCloseThird)
            url3 = product.images[2]
        }

        arrayType = product.type
        val sb = StringBuilder()
        for (i in product.type!!.indices) {
            val result = product.type!![i]
            sb.append(result)
            if (i < product.type!!.size - 1) {
                sb.append(", ") // Add a comma for separation
            }
        }
        etProductType.setText(sb)
        etProductPrice.setText(product.price.toString())
        etProductStock.setText(product.stock.toString())
        etProductWeight.setText(product.weight.toString())
        etProductDescription.setText(product.description)
        etProductDiscount.setText(product.discount.toString())
        etProductUnit.setText(product.unit)
        if(product.isHighLight!!){
            etHighLight.setText(getString(R.string.text_yes))
        }else{
            etHighLight.setText(getString(R.string.text_no))
        }
    }

}