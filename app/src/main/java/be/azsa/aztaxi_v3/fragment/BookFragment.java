package be.azsa.aztaxi_v3.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.model.User;

public class BookFragment extends Fragment {

    private String idUser, firstname, lastname, email, password, phone;
    private User user;
    private EditText book_depart, book_destination, book_infos ;
    private TextView book_date, book_time;
    private Button book_commander;

    //Date&Time Picker
    Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);
    int tHour;
    int tMinute;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        // get arguments Bundle
        idUser = getArguments().getString("idUser");
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        phone = getArguments().getString("phone");

        user = new User(Long.parseLong(idUser),firstname,lastname,email,password,phone);

        //initialistion
        book_depart = (EditText) view.findViewById(R.id.et_book_depart);
        book_destination = (EditText) view.findViewById(R.id.et_book_destination);
        book_infos = (EditText) view.findViewById(R.id.et_book_infos);
        book_date = (TextView) view.findViewById(R.id.tv_book_date);
        book_time = (TextView) view.findViewById(R.id.tv_book_time);
        book_commander = (Button) view.findViewById(R.id.btn_book_commander);

        //init place
        Places.initialize(getContext(), getString(R.string.API_KEY));
        book_depart.setFocusable(false);
        book_depart.setOnClickListener(book_depart_listener);
        book_destination.setFocusable(false);
        book_destination.setOnClickListener(book_destination_listener);

        //onClickListener
        book_date.setOnClickListener(book_date_listener);
        book_time.setOnClickListener(book_time_listener);
        book_commander.setOnClickListener(book_commander_listener);

        return view;
    }

    //DatePicker
    private View.OnClickListener book_date_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    month = month+1;
                    String date = year+"-"+month+"-"+day;

                    //mise en forme de la date (pattern)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = sdf.parse(date);
                        book_date.setText(sdf.format(date1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                };
            },year,month,day);
            datePickerDialog.show();
        }
    };
    //Timepicker
    private View.OnClickListener book_time_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    tHour = hourOfDay;
                    tMinute = minute;
                    String time = tHour+":"+tMinute;

                    //mise en forme de l'heure (pattern)
                    SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");
                    try {
                        Date date2 = f24hours.parse(time);
                        book_time.setText(f24hours.format(date2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, 24, 00,true);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(tHour,tMinute);
            timePickerDialog.show();
        }
    };
    //Autocomplete Depart listener
    private View.OnClickListener book_depart_listener = new View.OnClickListener() {
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
    //Autocomplete Destination listener
    private View.OnClickListener book_destination_listener = new View.OnClickListener() {
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
    //Submit Listener
    private View.OnClickListener book_commander_listener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //code
            //Volley
            String postUrl = getString(R.string.localhost)+"/order/add";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

            //User
            String departure = book_depart.getText().toString().toLowerCase();
            String arrival = book_destination.getText().toString().toLowerCase();
            String info = book_infos.getText().toString().toLowerCase();

            //date&time
            String date = book_date.getText().toString();
            String time = book_time.getText().toString();
            String datetime = date +"T"+ time+":00.000";
            Log.i("DEBBUG", datetime);

            if (departure.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire une adresse de départ", Toast.LENGTH_SHORT).show();
            }else if(arrival.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Veuillez introduire une adresse de destination", Toast.LENGTH_SHORT).show();
            }else{
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
                    postData.put("datetime", datetime);
                    postData.put("infos", info);
                    postData.put("status","SENT");
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
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur", Toast.LENGTH_SHORT).show();

                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);

                Toast.makeText(getActivity().getApplicationContext(), "Commande executée", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Set Address on editText
            book_depart.setText(place.getAddress());
        } else if(requestCode==200 && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            //Set Address on editText
            book_destination.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
