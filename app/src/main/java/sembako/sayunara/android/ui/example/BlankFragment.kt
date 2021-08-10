package sembako.sayunara.android.ui.example

/**
 * Description: Home Fragment
 * Date: 2019/08/2
 *
 * @author asywalulfikri@gmail.com
 */


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseFragment

class BlankFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home,
                container, false)
    }

}
