package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextConfirmPassword;
    private EditText editTextFullName;
    private EditText editTextGender;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private Button buttonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // 初始化UI元素
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextGender = findViewById(R.id.editTextGender);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonRegister = findViewById(R.id.buttonRegister);

        // 註冊按鈕點擊事件
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String fullName = editTextFullName.getText().toString();
                String gender = editTextGender.getText().toString();
                String phone = editTextPhone.getText().toString();
                String address = editTextAddress.getText().toString();
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                // 在此處理註冊邏輯，將所有註冊資訊傳遞給後端資料庫
                // 這裡只是一個示範，你需要根據你的需求進行實現
            }
        });
    }
}