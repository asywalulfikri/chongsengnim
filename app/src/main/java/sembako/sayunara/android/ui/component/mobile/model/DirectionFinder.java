package sembako.sayunara.android.ui.component.mobile.model;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBPQbuaV_iUmZh1b0ok-TDVI1FzzDiAuC0";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;
    private String waypoint;
    private String urlnyaaa = "https://maps.googleapis.com/maps/api/directions/json?origin=-0.9295442,100.3607169&destination=-0.47067899999999996,100.4059456&waypoints=-0.397011,99.9104272|-0.2339303,100.6341319&key=AIzaSyAFA7GSeWE5x57TvVAMqBCnZtLQBJDzDMA";

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination, String waypoint) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.waypoint = waypoint;

    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    public void executeway() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrlway());
    }

    public void executeway3() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrlway1());
    }


    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        Log.d("pantez", DIRECTION_URL_API+ "origin=" + origin + "&destination=" + destination + "&key=" + GOOGLE_API_KEY);
        return DIRECTION_URL_API + "origin=" + origin + "&destination=" + destination + "&key=" + GOOGLE_API_KEY;
    }

    private String createUrlway() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        Log.d("pantezzz", DIRECTION_URL_API+ "origin=" + origin + "&destination=" + destination + "&waypoints="+waypoint+"&key=" + GOOGLE_API_KEY);
        return DIRECTION_URL_API + "origin=" + origin + "&destination=" + destination + "&waypoints="+waypoint+ "&key=" + GOOGLE_API_KEY;
    }

    private String createUrlway1() throws UnsupportedEncodingException {
        Log.d("pantezzz", "https://maps.googleapis.com/maps/api/directions/json?origin=-0.9470831999999999,100.417181&destination=-0.47067899999999996,100.4059456&waypoints=-0.30391779999999996,100.383479|-0.236526,100.4139789&key=AIzaSyAFA7GSeWE5x57TvVAMqBCnZtLQBJDzDMA");
        return urlnyaaa;
    }



    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            for(int x = 0 ; x<jsonLegs.length();x++){
                JSONObject xxx = jsonLegs.getJSONObject(x);
                JSONObject jsonDistance1 = xxx.getJSONObject("distance");

                Distance distance = new Distance(jsonDistance1.getString("text"), jsonDistance1.getInt("value"));
                route.distance.add(distance);
            }
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
           // JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");


            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
