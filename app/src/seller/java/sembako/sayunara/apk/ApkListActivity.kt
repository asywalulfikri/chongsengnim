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
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.seller.activity_apk_list.*
import sembako.sayunara.android.R
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk_list)

        setupToolbar(toolbar)
        setupRecyclerView(recyclerView)


        if(isLogin()){
            loadTask()
            swipeRefresh.setOnRefreshListener {
                loadTask()
            }
        }else{
            layout_empty.visibility =View.VISIBLE
            textViewEmptyList.text = "Masuk Terlebih dahulu"
        }
    }

    private fun loadTask(){
        layout_progress.visibility = View.VISIBLE
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
            layout_empty.visibility = View.GONE
            layout_progress.visibility = View.GONE
        }else{
            layout_empty.visibility = View.VISIBLE
            textViewEmptyList.text = getString(R.string.empty_basket)
        }


        swipeRefresh.isRefreshing = false
        layout_progress.visibility = View.GONE
        adapter.setItems(arrayList, this)
        recyclerView.adapter = this.adapter
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
