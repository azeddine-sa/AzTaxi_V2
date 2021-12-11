package be.azsa.aztaxi_v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStateManagerControl;
import androidx.fragment.app.FragmentTransaction;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfilFragment.SendDataInterface {

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

        //Récupération données de connexions
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.hasExtra("idUser")){
                idUser = intent.getStringExtra("idUser");
                firstname = intent.getStringExtra("firstname");
                lastname = intent.getStringExtra("lastname");
                email = intent.getStringExtra("email");
                password = intent.getStringExtra("password");
                phone = intent.getStringExtra("phone");

                user = new User(Long.parseLong(idUser), firstname, lastname, email,password,phone);
            }
        }else{
            Toast.makeText(MainActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
        }

        Log.i("USER", firstname.toString()+" "+lastname.toString());

        if(savedInstanceState==null){
            HomeFragment homeFrag = new HomeFragment();
            FragmentTransaction hfTransaction = getSupportFragmentManager().beginTransaction();

            //Donné d'activité à envoyer
            Bundle bundlehome = new Bundle();
            bundlehome.putString("idUser", idUser);
            bundlehome.putString("firstname", firstname);
            bundlehome.putString("lastname", lastname);
            bundlehome.putString("email", email);
            bundlehome.putString("password", password);
            bundlehome.putString("phone", phone);

            homeFrag.setArguments(bundlehome);

            hfTransaction.replace(R.id.fragment_container,homeFrag).commit();
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
                BookFragment bookFrag = new BookFragment();
                FragmentTransaction bfTransaction = getSupportFragmentManager().beginTransaction();

                //Donné d'activité à envoyer
                Bundle bundlebook = new Bundle();
                bundlebook.putString("idUser", idUser);
                bundlebook.putString("firstname", firstname);
                bundlebook.putString("lastname", lastname);
                bundlebook.putString("email", email);
                bundlebook.putString("password", password);
                bundlebook.putString("phone", phone);

                bookFrag.setArguments(bundlebook);

                bfTransaction.replace(R.id.fragment_container, bookFrag).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HistoryFragment()).commit();
                break;
            case R.id.nav_profil:
                ProfilFragment profilFrag = new ProfilFragment();
                FragmentTransaction pfTransaction = getSupportFragmentManager().beginTransaction();

                //Donné d'activité à envoyer
                Bundle bundleprofil = new Bundle();
                bundleprofil.putString("idUser", idUser);
                bundleprofil.putString("firstname", firstname);
                bundleprofil.putString("lastname", lastname);
                bundleprofil.putString("email", email);
                bundleprofil.putString("password", password);
                bundleprofil.putString("phone", phone);

                profilFrag.setArguments(bundleprofil);

                pfTransaction.replace(R.id.fragment_container,profilFrag).commit();
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

    //Update Data User from ProfilFragment
    @Override
    public void sendData(String firstname, String lastname, String email, String password, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}