package be.azsa.aztaxi_v3.fragment;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
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
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.model.User;

public class HomeFragment extends Fragment implements LocationListener {
    //Google Maps
    private static final int PERMS_CALL_ID = 1234;
    private LocationManager lm;
    private Location location;
    private MapView mapView;
    private GoogleMap googleMap;
    private Geocoder geocoder;

    //propriétés
    private User user;
    private String idUser, firstname, lastname, email, password, phone;
    private TextView menu_commande, menu_trajets, menu_profil, menu_contactUs, menu_logout;
    private EditText home_depart, home_destination;
    private Button home_submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //init view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // get arguments Bundle
        idUser = getArguments().getString("idUser");
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        phone = getArguments().getString("phone");

        user = new User(Long.parseLong(idUser),firstname,lastname,email,password,phone);

        //assign variable
        home_depart = (EditText) view.findViewById(R.id.et_home_depart);
        home_destination = (EditText) view.findViewById(R.id.et_home_destination);
        home_submit = (Button) view.findViewById(R.id.btn_home_submit);
        home_submit.setOnClickListener(home_submit_listener);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        //init place
        Places.initialize(getContext(), getString(R.string.API_KEY));
        home_depart.setFocusable(false);
        home_depart.setOnClickListener(home_depart_listener);
        home_destination.setFocusable(false);
        home_destination.setOnClickListener(home_destination_listener);

        //return
        return view;
    }

    //Listener submit
    public View.OnClickListener home_submit_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //code
            //Volley
            String postUrl = getString(R.string.localhost)+"/order/add";
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
                //User Json
                JSONObject userData = new JSONObject();
                try {
                    userData.put("idUser", user.getIdUser());
                    userData.put("firstname", user.getFirstname());
                    userData.put("lastname", user.getLastname());
                    userData.put("email", user.getEmail());
                    userData.put("password", user.getPassword());
                    userData.put("phone", user.getPhone());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //RequestPost Json
                JSONObject postData = new JSONObject();
                try {
                    postData.put("departure", departure);
                    postData.put("arrival", arrival);
                    postData.put("infos", "");
                    postData.put("user", userData);
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
    //Autocomplete destination
    public View.OnClickListener home_depart_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //init place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                    Place.Field.LAT_LNG, Place.Field.NAME);
            //intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                    fieldList).build(getContext());
            //start
            startActivityForResult(intent, 100);
        }
    };
    //Autocomplete depart
    public View.OnClickListener home_destination_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //init place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                    Place.Field.LAT_LNG, Place.Field.NAME);
            //intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                    fieldList).build(getContext());
            //start
            startActivityForResult(intent, 200);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Set Address on editText
            home_depart.setText(place.getAddress());
            googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        } else if(requestCode==200 && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Set Address on editText
            home_destination.setText(place.getAddress());
            googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }

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

        loadMap();
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

        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final Fragment fragment = fragmentManager.findFragmentById(R.id.map);
        if(fragment!=null){
            fragmentManager.beginTransaction().remove(fragment).commit();
            googleMap=null;
        }

        if (lm != null){
            lm.removeUpdates(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
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

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            try {
                List<Address> listAddress = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                if (listAddress.size()>0){
                    if (home_depart.getText().toString().isEmpty()){
                        home_depart.setText(listAddress.get(0).getAddressLine(0));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
