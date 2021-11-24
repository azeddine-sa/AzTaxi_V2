package be.azsa.aztaxi_v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import be.azsa.aztaxi_v3.fragment.BookFragment;
import be.azsa.aztaxi_v3.fragment.ContactUsFragment;
import be.azsa.aztaxi_v3.fragment.HistoryFragment;
import be.azsa.aztaxi_v3.fragment.HomeFragment;
import be.azsa.aztaxi_v3.fragment.ProfilFragment;
import be.azsa.aztaxi_v3.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Donnée utilisateur
    private String idUser, firstname, lastname, email, phone, password;
    private User user;

    //Layout
    private DrawerLayout drawer;

    //Google Maps
    private static final int PERMS_CALL_ID = 1234;
    private LocationManager lm;
    private Location location;
    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Menu
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        //Envoie de données d'une activité vers un fragment
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.hasExtra("idUser")){
                idUser = intent.getStringExtra("idUser");
                firstname = intent.getStringExtra("firstname");
                lastname = intent.getStringExtra("lastname");
                email = intent.getStringExtra("email");
                password = intent.getStringExtra("password");
                phone = intent.getStringExtra("phone");
                Log.i("TEST", idUser+" "+firstname+" "+lastname+" "+email);

                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                bundle.putString("firstname", firstname);
                bundle.putString("lastname", lastname);
                bundle.putString("email", email);
                bundle.putString("password", password);
                bundle.putString("phone", phone);

                ProfilFragment profilFrag = new ProfilFragment();
                profilFrag.setArguments(bundle);
            }
        }else{
            Toast.makeText(MainActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_book:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookFragment()).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HistoryFragment()).commit();
                break;
            case R.id.nav_profil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfilFragment()).commit();
                break;
            case R.id.nav_contact_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactUsFragment()).commit();
                break;
            case R.id.nav_logout:
                Intent LogOut = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(LogOut);
                Toast.makeText(MainActivity.this, "Déconnexion", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}