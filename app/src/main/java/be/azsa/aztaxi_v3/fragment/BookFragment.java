package be.azsa.aztaxi_v3.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;

public class BookFragment extends Fragment {

    private Long idUser;
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

        //initialistion
        book_depart = (EditText) view.findViewById(R.id.et_book_depart);
        book_destination = (EditText) view.findViewById(R.id.et_book_destination);
        book_infos = (EditText) view.findViewById(R.id.et_book_infos);
        book_date = (TextView) view.findViewById(R.id.tv_book_date);
        book_time = (TextView) view.findViewById(R.id.tv_book_time);
        book_commander = (Button) view.findViewById(R.id.btn_book_commander);

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
