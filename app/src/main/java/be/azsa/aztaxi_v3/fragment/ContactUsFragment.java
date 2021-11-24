package be.azsa.aztaxi_v3.fragment;

import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import be.azsa.aztaxi_v3.MainActivity;
import be.azsa.aztaxi_v3.R;

public class ContactUsFragment extends Fragment {

    TextView call;
    Button send, back;
    EditText object, message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactus, container, false);

        call = (TextView) view.findViewById(R.id.tv_contactus_call);
        call.setOnClickListener(call_listener);

        object = (EditText) view.findViewById(R.id.et_contactus_object);
        message = (EditText) view.findViewById(R.id.et_contactus_message);

        send = (Button) view.findViewById(R.id.btn_contactus_send);
        send.setOnClickListener(send_listener);
        return view;
    }

    //OnClick phone number
    private View.OnClickListener call_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tél:022222222"));

            startActivity(intent);
        }
    };

    //OnClick send message
    private View.OnClickListener send_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);

            Uri uri = Uri.parse("mailto:az.sa.taxi@gmail.com");
            intent.setData(uri);
            intent.putExtra("subject", object.getText().toString());
            intent.putExtra("body", message.getText().toString());

            if (object.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Veuillez introduire l'objet de votre demande",Toast.LENGTH_SHORT).show();
            } else if(message.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Veuillez introduire l'objet de votre demande",Toast.LENGTH_SHORT).show();
            } else{
                startActivity(intent);
            }
        }
    };
}
