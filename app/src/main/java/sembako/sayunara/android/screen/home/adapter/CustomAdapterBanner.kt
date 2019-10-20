package sembako.sayunara.android.screen.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

import com.ms.banner.holder.BannerViewHolder
import com.squareup.picasso.Picasso
import sembako.sayunara.android.R
import sembako.sayunara.android.screen.home.model.Banner

class CustomAdapterBanner : BannerViewHolder<Banner> {

    @SuppressLint("InflateParams")
    override fun createView(context: Context, position: Int, banner: Banner): View {
        val view = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
        val image = view.findViewById<ImageView>(R.id.iv_banner)

        Picasso.get()
                .load(banner.url)
                .into(image)

        return view
    }

}