package be.azsa.aztaxi_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    //propriétés
    private EditText register_firstname, register_lastname,register_mail, register_password, register_password2, register_phone;
    private Button register_signUp, register_back;
    private CheckBox register_cgu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        register_firstname = (EditText) findViewById(R.id.et_register_firstname);
        register_lastname = (EditText) findViewById(R.id.et_register_lastname);
        register_mail = (EditText) findViewById(R.id.et_register_mail);
        register_password = (EditText) findViewById(R.id.et_register_password);
        register_password2 = (EditText) findViewById(R.id.et_register_password2);
        register_phone = (EditText) findViewById(R.id.et_register_phone);
        register_signUp = (Button) findViewById(R.id.btn_register_signup);
        register_back = (Button) findViewById(R.id.btn_register_back);
        register_cgu = (CheckBox) findViewById(R.id.cb_register_cg);

        initListener();
    }

    private void initListener(){
        //S'inscrire
        register_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Volley
                String postUrl = "http://192.168.141.234:8080/api/user/add";
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);

                //User
                String firstname = register_firstname.getText().toString().toLowerCase();
                String lastname = register_lastname.getText().toString().toLowerCase();
                String email = register_mail.getText().toString().toLowerCase();
                String password = register_password.getText().toString();
                String password2 = register_password2.getText().toString();
                String phone = register_phone.getText().toString();

                if (firstname.isEmpty()){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez introduire un nom", Toast.LENGTH_SHORT).show();
                }else if(lastname.isEmpty()){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez introduire un prénom", Toast.LENGTH_SHORT).show();
                }else if(!email.matches(".+@.+\\.[a-z]+")){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez introduire une adresse email valide !", Toast.LENGTH_SHORT).show();
                }else if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}+$")){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez introduire un mot de passe de minimum 8 caractères et contenant au minimum 1 minuscule, 1 majuscule et 1 chiffre",
                            Toast.LENGTH_SHORT).show();
                }else if(!password2.equals(password)){
                    Toast.makeText(RegisterActivity.this,
                            "Le mot de passe ne correspond pas, veuillez introduire le même mot de passe",
                            Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez introduire un numéro de gsm BELGE valide commençant par +32 ou 0032 !",
                            Toast.LENGTH_SHORT).show();
                }else if(!register_cgu.isChecked()){
                    Toast.makeText(RegisterActivity.this,
                            "Veuillez accepter les termes et conditions!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //Gson
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("firstname", firstname);
                        postData.put("lastname", lastname);
                        postData.put("email", email);
                        postData.put("password", password);
                        postData.put("phone", phone);
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

                    Toast.makeText(RegisterActivity.this, "Ajout Réussi", Toast.LENGTH_SHORT).show();

                    //redirection vers la page de connexion
                    Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(Main);
                }
            }
        });

        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirection vers la page de connexion
                Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(Main);
            }
        });
    }
}