/*
package sembako.sayunara.android.ui.component.product.listproduct

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_list_product2.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.product.listproduct.adapter.MuseumAdapter
import sembako.sayunara.android.ui.component.product.listproduct.model.*


*/
/* Created by Asywalul Fikri *//*


class ListProductActivity : BaseActivity(){


    private lateinit var viewModel: MuseumViewModel
    private lateinit var adapter: MuseumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product2)

        //setup toolbar
        setupToolbar(toolbar)

        //setup ViewModel
        setupViewModel()

        //setup recyclerView
        setupRecyclerView()


    }

    private fun setupRecyclerView(){
        adapter= MuseumAdapter(viewModel.museums.value?: emptyList())
        recyclerView.layoutManager = setupLinearLayoutManager()
        recyclerView.adapter= adapter
    }


    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this, ViewModelFactory(Injection.providerRepository())).get(MuseumViewModel::class.java)
        */
/*viewModel.museums.observe(this,renderMuseums)

        viewModel.isViewLoading.observe(this,isViewLoadingObserver)
        viewModel.onMessageError.observe(this,onMessageErrorObserver)
        viewModel.isEmptyList.observe(this,emptyListObserver)*//*

    }

    //observers
    private val renderMuseums= Observer<List<Product>> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility=View.GONE
        layoutEmpty.visibility=View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver= Observer<Boolean> {
        Log.v(TAG, "isViewLoading $it")
        val visibility=if(it)View.VISIBLE else View.GONE
        progressBar.visibility= visibility
    }

    @SuppressLint("SetTextI18n")
    private val onMessageErrorObserver= Observer<Any> {
        Log.v(TAG, "onMessageError $it")
        layoutError.visibility= View.VISIBLE
        layoutEmpty.visibility=View.GONE
        textViewError.text= "Error $it"
    }

    private val emptyListObserver= Observer<Boolean> {
        Log.v(TAG, "emptyListObserver $it")
        layoutEmpty.visibility=View.VISIBLE
        layoutError.visibility=View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMuseums()
    }
}*/
