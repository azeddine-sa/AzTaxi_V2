package be.azsa.aztaxi_v3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import be.azsa.aztaxi_v3.R;
import be.azsa.aztaxi_v3.model.User;

public class ProfilFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get arguments
        String idUser = getArguments().getString("idUser");
        String firstname = getArguments().getString("firstname");
        String lastname = getArguments().getString("lastname");
        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        String phone = getArguments().getString("phone");

        //User user = new User(Long.parseLong(idUser),firstname,lastname,email,password,phone);


        return inflater.inflate(R.layout.fragment_profil, container, false);
    }
}
