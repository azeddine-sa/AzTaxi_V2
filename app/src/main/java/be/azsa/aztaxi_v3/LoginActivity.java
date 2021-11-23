package be.azsa.aztaxi_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import be.azsa.aztaxi_v3.model.User;

public class LoginActivity extends AppCompatActivity {

    //propriétés
    private EditText login;
    private EditText password;
    private Button signin;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    //initialistion avec les objets graphiques
    private void init(){
        login=(EditText) findViewById(R.id.et_home_login);
        password=(EditText) findViewById(R.id.et_home_password);
        signin=(Button) findViewById(R.id.btn_home_signin);
        signup=(TextView) findViewById(R.id.tv_home_signup);

        initListener();
    }

    //écoute évènements
    private void initListener(){
        //Clique sur "Se connecter"
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Donnée de connexion introduite par l'utilisateur
                String mail=login.getText().toString().toLowerCase();
                String pass=password.getText().toString();
                User user = new User();

                //Volley connexion API
                String getUrl = "http://192.168.0.241:8080/api/user/bymail/"+mail;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("user",response);
                                try{
                                    JSONObject jsonObject = new JSONObject(response);
                                    String idUser = jsonObject.getString("idUser");
                                    String firstname = jsonObject.getString("firstname");
                                    String lastname= jsonObject.getString("lastname");
                                    String email = jsonObject.getString("email");
                                    String password = jsonObject.getString("password");
                                    String phone = jsonObject.getString("phone");

                                    user.setIdUser(Long.parseLong(idUser));
                                    user.setPhone(phone);
                                    user.setPassword(password);
                                    user.setEmail(email);
                                    user.setLastname(lastname);
                                    user.setFirstname(firstname);

                                    if(pass.equals(user.getPassword())){
                                        Toast.makeText(LoginActivity.this,
                                                "Bienvenue "+user.getLastname().toUpperCase(),
                                                Toast.LENGTH_SHORT).show();
                                        //redirection vers la page de connexion
                                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                        main.putExtra("idUser", idUser);
                                        main.putExtra("firstname", firstname);
                                        main.putExtra("lastname", lastname);
                                        main.putExtra("email", email);
                                        main.putExtra("phone", phone);
                                        main.putExtra("password", password);
                                        startActivity(main);
                                    } else{
                                        Toast.makeText(LoginActivity.this,
                                                "Mot de Passe Incorrect",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Adresse Email non reconnue",Toast.LENGTH_LONG).show();
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                requestQueue.add(stringRequest);

            }
        });

        //Clique sur "pas encore inscrit"
        signup.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });
    }
}