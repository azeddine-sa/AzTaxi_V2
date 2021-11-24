package be.azsa.aztaxi_v3.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Date;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;

public class BookFragment extends Fragment {

    private Long idUser;
    private EditText book_depart;
    private EditText book_destination;
    private LocalDate book_when;
    private EditText book_infos;
    private Button book_commander;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        book_depart = (EditText) view.findViewById(R.id.et_book_depart);
        book_destination = (EditText) view.findViewById(R.id.et_book_destination);
        book_when = LocalDate.now();
        book_infos = (EditText) view.findViewById(R.id.et_book_infos);

        book_commander = (Button) view.findViewById(R.id.btn_book_commander);
        book_commander.setOnClickListener(book_commander_listener);

        return view;
    }

    private View.OnClickListener book_commander_listener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //code
            //Volley
            String postUrl = "http://192.168.0.241:8080/api/order/add";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

            //User
            String departure = book_depart.getText().toString().toLowerCase();
            String arrival = book_destination.getText().toString().toLowerCase();
            Date datetime = null;
            String infos = book_infos.getText().toString();

            if (departure.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire une adresse de départ", Toast.LENGTH_SHORT).show();
            }else if(arrival.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire une adresse de destination", Toast.LENGTH_SHORT).show();
            }else{
                //Gson
                JSONObject postData = new JSONObject();
                try {
                    postData.put("departure", departure);
                    postData.put("arrival", arrival);
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
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur", Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);

                Toast.makeText(getActivity().getApplicationContext(), "Commande executée", Toast.LENGTH_SHORT).show();

            }
        }
    };
}
