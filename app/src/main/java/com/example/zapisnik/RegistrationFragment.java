package com.example.zapisnik;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(username, email, password);
            }
        });

        // Set up clickable "Log in" text.
        SpannableString spannableString = new SpannableString(tvLoginLink.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Navigate back to LoginFragment when "Log in" is clicked.
                LoginFragment loginFragment = new LoginFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, loginFragment)
                        .commit();
            }
        };
        String text = tvLoginLink.getText().toString();
        int start = text.indexOf("Log in");
        int end = start + "Log in".length();
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLoginLink.setText(spannableString);
        tvLoginLink.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private void registerUser(String username, String email, String password) {
        UserApi api = RetrofitClient.getUserApi();
        Call<RegisterResponse> call = api.registerUser(username, email, password);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                        // Optionally, navigate to LoginFragment after registration
                        LoginFragment loginFragment = new LoginFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, loginFragment)
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Registration failed: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Registration failed: Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Registration failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
