package sembako.sayunara.home

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.seller.fragment_home.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseFragment
import sembako.sayunara.user.ListUserActivity

class HomeFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
    }

    @SuppressLint("SetTextI18n")
    fun setupMenu(){

        tv_name.text = "Hallo "+ getUsers?.profile?.username.toString()
        tv_status.text = "Status kamu adalah "+getUsers?.profile?.type.toString()


        cv_product.setOnClickListener {

        }

        cv_user.setOnClickListener {
            val intent = Intent(activity,ListUserActivity::class.java)
            startActivity(intent)
        }



    }
}
