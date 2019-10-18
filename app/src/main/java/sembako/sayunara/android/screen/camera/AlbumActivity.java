package sembako.sayunara.android.screen.camera;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sembako.sayunara.android.R;
import sembako.sayunara.android.screen.camera.model.Album;
import sembako.sayunara.android.screen.camera.util.CameraBaseActivity;
import sembako.sayunara.android.screen.camera.util.FileUtils;
import sembako.sayunara.android.screen.camera.util.ImageUtils;
import sembako.sayunara.android.screen.camera.util.PagerSlidingTabStrip;
import sembako.sayunara.android.screen.camera.util.StringUtils;

/**
 * 相册界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class AlbumActivity extends CameraBaseActivity {

    private Map<String, Album> albums;
    private List<String> paths = new ArrayList<String>();

    private PagerSlidingTabStrip tab;
    private ViewPager pager;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        tab = findViewById(R.id.indicator);
        pager = findViewById(R.id.pager);
        toolbar = findViewById(R.id.title_layout);


        albums = ImageUtils.findGalleries(this, paths, 0);
        toolbar.setTitle("Gallery");
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setViewPager(pager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == RESULT_OK) {
            // super.onActivityResult(requestCode,resultCode,result);
            Intent intent = new Intent();
            intent.putExtra("path", result.getStringExtra("path"));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }


    public  class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AlbumFragment.newInstance(albums.get(paths.get(position)).getPhotos());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Album album = albums.get(paths.get(position % paths.size()));
            if (StringUtils.equalsIgnoreCase(FileUtils.getInst().getSystemPhotoPath(),
                    album.getAlbumUri())) {
                return "CAMERA";
            } else if (album.getTitle().length() > 13) {
                return album.getTitle().substring(0, 11) + "...";
            }
            return album.getTitle();
        }

        @Override
        public int getCount() {
            return paths.size();
        }
    }

}
