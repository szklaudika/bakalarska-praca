package com.example.zapisnik;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;

public class ResetPasswordFragment extends Fragment {

    private EditText etEmail;
    private Button btnReset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        etEmail = view.findViewById(R.id.etEmailReset);
        btnReset = view.findViewById(R.id.btnResetPassword);

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                UserApi api = RetrofitClient.getUserApi();
                Call<GenericResponse> call = api.sendResetLink(email);

                call.enqueue(new retrofit2.Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull retrofit2.Response<GenericResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            GenericResponse res = response.body();
                            Toast.makeText(getActivity(), res.getMessage(), Toast.LENGTH_LONG).show();
                            if ("success".equalsIgnoreCase(res.getStatus())) {
                                getParentFragmentManager().popBackStack();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error sending reset link", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;
    }
}
