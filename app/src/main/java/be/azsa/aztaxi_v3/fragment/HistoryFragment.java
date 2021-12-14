package be.azsa.aztaxi_v3.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.model.User;

public class HistoryFragment extends Fragment {
    private TextView history_list;
    private String idUser, firstname, lastname, email, password, phone;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // get arguments Bundle
        idUser = getArguments().getString("idUser");
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        phone = getArguments().getString("phone");

        user = new User(Long.parseLong(idUser),firstname,lastname,email,password,phone);

        history_list = (TextView) view.findViewById(R.id.tv_history_list);

        // Request a string response from the provided URL.
        JSONObject getData = new JSONObject();
        try {
            getData.put("idUser", user.getIdUser());
            getData.put("firstname", user.getFirstname());
            getData.put("lastname", user.getLastname());
            getData.put("email", user.getEmail());
            getData.put("password", user.getPassword());
            getData.put("phone", user.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //recupération de la l'historique de l'utilisateur sur la BD
        String getUrl = getString(R.string.localhost)+"/orders";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, getUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String departure = jsonObject.getString("departure");
                        String arrival = jsonObject.getString("arrival");
                        String datetime = jsonObject.getString("datetime");
                        String infos = jsonObject.getString("infos");
                        String user_id = jsonObject.getString("user");

                        //Split user infos
                        String[] user_parts = user_id.split(",");

                        //si user_id correspond a l'id de l'utilisateur connecté
                        if (user_parts[0].substring(10).replaceAll("\"","").equals(idUser)){
                            history_list.append("DEPART : "+departure+
                                    "\nDESTINATION : " +arrival+
                                    "\nDATE & HEURE DE COMMANDE : " +datetime.substring(0,10)+" - "+datetime.substring(11,16) +
                                    "\nINFORMATIONS COMPLEMENTAIRE : "+infos+
                                    "\n----------------------------------------------------------------------------------------\n");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                history_list.setText("Il n'y a pas d'historique");
                Log.i("DEBBUG", error.getMessage());
            }
        });

        requestQueue.add(jsonArrayRequest);

        return view;
    }
}
