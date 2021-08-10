package sembako.sayunara.android.ui.component.mobile.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Route {
    public List<Distance> distance = new ArrayList<>();
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
