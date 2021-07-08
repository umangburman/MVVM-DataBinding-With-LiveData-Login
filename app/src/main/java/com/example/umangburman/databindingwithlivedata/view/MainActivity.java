package com.example.umangburman.databindingwithlivedata.view;

import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;

import com.example.umangburman.databindingwithlivedata.R;
import com.example.umangburman.databindingwithlivedata.viewmodel.LoginViewModel;
import com.example.umangburman.databindingwithlivedata.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

        loginViewModel.getUser().observe(this, loginUser -> {

            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrEmailAddress())) {
                binding.txtEmailAddress.setError("Enter an E-Mail Address");
                binding.txtEmailAddress.requestFocus();
            } else if (!loginUser.isEmailValid()) {
                binding.txtEmailAddress.setError("Enter a Valid E-mail Address");
                binding.txtEmailAddress.requestFocus();
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrPassword())) {
                binding.txtPassword.setError("Enter a Password");
                binding.txtPassword.requestFocus();
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                binding.txtPassword.setError("Enter at least 6 Digit password");
                binding.txtPassword.requestFocus();
            } else {
                binding.lblEmailAnswer.setText(loginUser.getStrEmailAddress());
                binding.lblPasswordAnswer.setText(loginUser.getStrPassword());
            }

        });

    }
}
