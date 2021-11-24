package be.azsa.aztaxi_v3.fragment;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.model.User;

public class HomeFragment extends Fragment implements LocationListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    //Google Maps
    private static final int PERMS_CALL_ID = 1234;
    private LocationManager lm;
    private Location location;
    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private Geocoder geocoder;

    //propriétés
    private User user;
    private Long idUser;
    private TextView menu_commande, menu_trajets, menu_profil, menu_contactUs, menu_logout;
    private EditText home_depart, home_destination;
    private Button home_submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //init view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Intent home = getActivity().getIntent();
        if(home!=null){
            if(home.hasExtra("idUser")){
                idUser = Long.parseLong(home.getStringExtra("idUser"));
            }
        }

        home_depart = (EditText) view.findViewById(R.id.et_home_depart);
        home_destination = (EditText) view.findViewById(R.id.et_home_destination);
        home_submit = (Button) view.findViewById(R.id.btn_home_submit);
        home_submit.setOnClickListener(home_submit_listener);

        //return
        return view;
    }


    public View.OnClickListener home_submit_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //code
            //Volley
            String postUrl = "http://192.168.0.241:8080/api/order/add";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            //User
            String departure = home_depart.getText().toString().toLowerCase();
            String arrival = home_destination.getText().toString().toLowerCase();

            if (departure.isEmpty()) {
                Toast.makeText(getActivity(),
                        "Veuillez introduire une adresse de départ", Toast.LENGTH_SHORT).show();
            } else if (arrival.isEmpty()) {
                Toast.makeText(getActivity(),
                        "Veuillez introduire une adresse de destination", Toast.LENGTH_SHORT).show();
            } else {
                //Gson
                JSONObject postData = new JSONObject();
                try {
                    postData.put("departure", departure);
                    postData.put("arrival", arrival);
                    postData.put("infos", "");
                    postData.put("idUser", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //POST REST API
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);

                Toast.makeText(getActivity(), "Commande executée", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        //Vérification de permission (acces_fine_location && acces_coarse_location)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID );
            return;
        }

        //Service de la plateforme Android
        lm = (LocationManager) getSystemService(getActivity().getSystemService(Context.LOCATION_SERVICE));
        //Fournisseur GPS (activé)
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMS_CALL_ID){
            checkPermissions();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (lm != null){
            lm.removeUpdates(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadMap(){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                HomeFragment.this.googleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(13));
                googleMap.setMyLocationEnabled(true);
            }
        });
    }
    private Object getSystemService(Object systemService) {
        return systemService;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(googleMap != null){
            LatLng googleLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}
