package be.azsa.aztaxi_v3.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.RegisterActivity;
import be.azsa.aztaxi_v3.model.User;

public class ProfilFragment extends Fragment {
    private EditText profil_firstname, profil_lastname,profil_mail,profil_oldpassword, profil_password, profil_password2, profil_phone;
    private Button profil_validation;
    private User user;
    String idUser, firstname, lastname, email, password, phone;
    SendDataInterface sendDataInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // get arguments Bundle
        idUser = getArguments().getString("idUser");
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        phone = getArguments().getString("phone");

        User user = new User(Long.parseLong(idUser),firstname,lastname,email,password,phone);
        Log.i("TEST", firstname.toString()+" "+lastname.toString()+" "+email.toString()+" "+phone.toString());

        // initialisation
        profil_firstname = (EditText) view.findViewById(R.id.et_profil_firstname);
        profil_firstname.setText(firstname, TextView.BufferType.EDITABLE);

        profil_lastname = (EditText) view.findViewById((R.id.et_profil_lastname));
        profil_lastname.setText(lastname, TextView.BufferType.EDITABLE);

        profil_mail = (EditText) view.findViewById(R.id.et_profil_mail);
        profil_mail.setText(email, TextView.BufferType.EDITABLE);

        profil_phone = (EditText) view.findViewById(R.id.et_profil_phone);
        profil_phone.setText(phone, TextView.BufferType.EDITABLE);

        profil_oldpassword = (EditText) view.findViewById(R.id.et_profil_oldpassword);
        profil_password = (EditText) view.findViewById(R.id.et_profil_password);
        profil_password2 = (EditText) view.findViewById(R.id.et_profil_password2);

        profil_validation = (Button) view.findViewById(R.id.btn_profil_validation);
        //listener confirmation
        profil_validation.setOnClickListener(profil_validation_listener);

        return view;
    }

    public View.OnClickListener profil_validation_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Volley
            String putUrl = "http://192.168.145.234:8080/api/user/"+idUser+"/edit";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

            //User
            String nFirstname = profil_firstname.getText().toString().toLowerCase();
            String nLastname = profil_lastname.getText().toString().toLowerCase();
            String nEmail = profil_mail.getText().toString().toLowerCase();
            String oPassword = profil_oldpassword.getText().toString();
            String nPassword = profil_password.getText().toString();
            String nPassword2 = profil_password2.getText().toString();
            String nPhone = profil_phone.getText().toString();

            if (nFirstname.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire un nom", Toast.LENGTH_SHORT).show();
            }else if(nLastname.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire un prénom", Toast.LENGTH_SHORT).show();
            }else if(!nEmail.matches(".+@.+\\.[a-z]+")){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire une adresse email valide !", Toast.LENGTH_SHORT).show();
            }else if(!oPassword.equals(password)){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Ancien Mot passe incorrect ! Veuillez recommencez...",
                        Toast.LENGTH_SHORT).show();
            }else if(!nPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}+$")){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire un mot de passe de minimum 8 caractères et contenant au minimum 1 minuscule, 1 majuscule et 1 chiffre",
                        Toast.LENGTH_SHORT).show();
            }else if(!nPassword2.equals(nPassword)){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Le nouveau mot de passe ne correspond pas, veuillez introduire le même mot de passe",
                        Toast.LENGTH_SHORT).show();
            }else if(phone.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire un numéro de gsm BELGE valide commençant par +32 ou 0032 !",
                        Toast.LENGTH_SHORT).show();
            }else{
                //Gson
                JSONObject putData = new JSONObject();
                try {
                    putData.put("idUser", idUser);
                    putData.put("firstname", nFirstname);
                    putData.put("lastname", nLastname);
                    putData.put("email", nEmail);
                    putData.put("password", nPassword);
                    putData.put("phone", nPhone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //POST REST API
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, putUrl, putData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        //Send New Data User to Activity
                        sendDataInterface.sendData(nFirstname,nLastname,nEmail,nPassword,nPhone);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);

                Toast.makeText(getActivity().getApplicationContext(), "Modification Réussi", Toast.LENGTH_SHORT).show();

            }
        }
    };

    //Interface Update Data User
    public interface SendDataInterface{
        public void sendData(String firstname, String lastname, String email, String password, String phone);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;

        try {
            sendDataInterface = (SendDataInterface) activity;
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }
    }
}
