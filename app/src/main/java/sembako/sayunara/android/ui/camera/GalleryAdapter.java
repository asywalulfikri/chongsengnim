package sembako.sayunara.android.ui.camera;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.List;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.camera.model.PhotoItem;
import sembako.sayunara.android.ui.camera.util.AppConstants;
import sembako.sayunara.android.ui.camera.util.ImageLoaderUtils;

/**
 * @author tongqian.ni
 *
 */
public class GalleryAdapter extends BaseAdapter {

    private Context mContext;
    private List<PhotoItem> values;
    public static GalleryHolder holder;
    private DisplayMetrics displayMetrics = null;


    public GalleryAdapter(Context context, List<PhotoItem> values) {
        this.mContext = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GalleryHolder holder;
        int width =  getScreenWidth()- dp2px(10) / 4 - dp2px(4);
        int weight = width/4;
        Log.d("ukuranya",String.valueOf(width));
      //  (App.getApp().getScreenWidth() - App.getApp().dp2px(10)) / 4 - App.getApp().dp2px(4);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_gallery, null);
            holder = new GalleryHolder();
            holder.sample = convertView.findViewById(R.id.gallery_sample_image);
            holder.sample.setLayoutParams(new AbsListView.LayoutParams(weight,weight));
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }
        final PhotoItem gallery = (PhotoItem) getItem(position);
        initImageLoader(mContext);
        ImageLoaderUtils.displayLocalImage(gallery.getImageUri(), holder.sample,null);
        
        return convertView;
    }
///%category%/%postname%/
    class GalleryHolder {
        ImageView sample;
    }

    public static void initImageLoader(Context context) {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(context, AppConstants.APP_IMAGE)))
                .diskCacheSize(100 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
                .threadPoolSize(3)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(mContext.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }
    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(mContext.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }


}
