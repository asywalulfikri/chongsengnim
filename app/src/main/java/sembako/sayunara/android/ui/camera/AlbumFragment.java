package sembako.sayunara.android.ui.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import sembako.sayunara.android.R;
import sembako.sayunara.android.ui.base.BaseFragment;
import sembako.sayunara.android.ui.camera.model.PhotoItem;
import sembako.sayunara.android.ui.camera.util.AppConstants;
import sembako.sayunara.android.ui.camera.util.ImageUtils;

import static android.app.Activity.RESULT_OK;

/**
 * @author tongqian.ni
 */
public class AlbumFragment extends BaseFragment {
    private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();

    public AlbumFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static Fragment newInstance(ArrayList<PhotoItem> photos) {
        Fragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable("photos", photos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_album, null);
        photos = (ArrayList<PhotoItem>) getArguments().getSerializable("photos");
        albums = root.findViewById(R.id.albums);
        albums.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                PhotoItem photo = photos.get(arg2);
                processPhotoItem(photo);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent result) {
        //  if (requestCode == AppConstants.REQUEST_CROP && resultCode == RESULT_OK) {
        String uri = result.getStringExtra("path");
        Log.d("pathalbum",uri);
        if(!uri.equals(null)|| !uri.equals("")){
            Intent newIntent = new Intent();
            newIntent.putExtra("path",uri);
            getActivity().setResult(RESULT_OK,newIntent);
            getActivity().finish();
        }
    }

    public void processPhotoItem(PhotoItem photo) {
        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo.getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        if (ImageUtils.isSquare(photo.getImageUri())) {
            Intent newIntent = new Intent();
            newIntent.putExtra("path",uri.getPath());
            getActivity().setResult(RESULT_OK,newIntent);
            getActivity().finish();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent newIntent = new Intent();
                newIntent.putExtra("path",uri.getPath());
                getActivity().setResult(RESULT_OK,newIntent);
                getActivity().finish();
            }else{
                Intent newIntent = new Intent(getActivity(), CropPhotoActivity.class);
                newIntent.setData(uri);
                startActivityForResult(newIntent, AppConstants.REQUEST_CROP);
            }
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        albums.setAdapter(new GalleryAdapter(getActivity(), photos));
    }

    private GridView albums;
}