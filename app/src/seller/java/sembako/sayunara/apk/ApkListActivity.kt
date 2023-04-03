package sembako.sayunara.apk

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import sembako.sayunara.android.R
import sembako.sayunara.android.databinding.ActivityApkListBinding
import sembako.sayunara.android.databinding.LayoutEmptyBinding
import sembako.sayunara.android.databinding.LayoutProgressBarWithTextBinding
import sembako.sayunara.android.databinding.ToolbarBinding
import sembako.sayunara.android.helper.blur.BlurBehind
import sembako.sayunara.android.helper.blur.OnBlurCompleteListener
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.account.login.ui.login.LoginFirstActivity
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup
import sembako.sayunara.apk.adapter.ApkAdapter
import java.util.*


class ApkListActivity : BaseActivity(),ApkView.ViewList,ApkAdapter.OnClickListener{

    private var adapter = ApkAdapter()
    val services = ApkServices()
    var handler : Handler? = Handler()

    private lateinit var  binding : ActivityApkListBinding
    private lateinit var layoutEmptyBinding: LayoutEmptyBinding
    private lateinit var layoutProgressBinding: LayoutProgressBarWithTextBinding
    private lateinit var toolbarBinding : ToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApkListBinding.inflate(layoutInflater)
        layoutEmptyBinding = LayoutEmptyBinding.inflate(layoutInflater)
        layoutProgressBinding = LayoutProgressBarWithTextBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(toolbarBinding.toolbar)
        setupRecyclerView(binding.recyclerView)


        if(isLogin()){
            loadTask()
            binding.swipeRefresh.setOnRefreshListener {
                loadTask()
            }
        }else{
            layoutEmptyBinding.layoutEmpty.visibility =View.VISIBLE
            layoutEmptyBinding.textViewEmptyList.text = "Masuk Terlebih dahulu"
        }
    }

    private fun loadTask(){
        layoutProgressBinding.layoutProgress.visibility = View.VISIBLE
        services.getList(this, FirebaseFirestore.getInstance())
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun loadingIndicator(isLoading: Boolean) {

    }

    override fun onRequestSuccess(arrayList: ArrayList<ConfigSetup>) {

        if(arrayList.size>0){
            layoutEmptyBinding.layoutEmpty.visibility = View.GONE
            layoutProgressBinding.layoutProgress.visibility = View.GONE
        }else{
            layoutEmptyBinding.layoutEmpty.visibility = View.VISIBLE
            layoutEmptyBinding.textViewEmptyList.text = getString(R.string.empty_basket)
        }


        binding.swipeRefresh.isRefreshing = false
        layoutProgressBinding.layoutProgress.visibility = View.GONE
        adapter.setItems(arrayList, this)
        binding.recyclerView.adapter = this.adapter
    }



    @SuppressLint("InflateParams")
    override fun showDialogLogin(message: String?) {
        BlurBehind.instance!!.execute(activity as AppCompatActivity?, object : OnBlurCompleteListener {
            override fun onBlurComplete() {
                val intent = Intent(activity, LoginFirstActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivityForResult(intent, 1313)
            }
        })
    }

    override fun onRequestFailed(code: Int?) {
        setToast(code!!)
    }

    override fun onClickDetail(position: Int, configSetup: ConfigSetup) {
        val intent = Intent(this,ApkDetailActivity::class.java)
        intent.putExtra("config",configSetup)
        startActivity(intent)
    }


}
