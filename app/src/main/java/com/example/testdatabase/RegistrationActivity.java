package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextConfirmPassword;
    private EditText editTextFullName;
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
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonRegister = findViewById(R.id.buttonRegister);

        // 註冊按鈕點擊事件
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = editTextEmail.getText().toString();
                String Password = editTextConfirmPassword.getText().toString();
                String Name = editTextFullName.getText().toString();
                String Phone = editTextPhone.getText().toString();
                String Address = editTextAddress.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveDataToSqlServer(Name, Email, Password, Address, Phone);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }).start();
            }
        });
    }
    private void saveDataToSqlServer(String Name, String Email, String Password, String Address, String Phone){
        String connectionString = "jdbc:jtds:sqlserver://192.168.10.8:1433/DrinkShop";
        String username = "sa";
        String password = "2ixxddux";
        Connection connection = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString, username, password);
            UUID uuid = UUID.randomUUID();
            String mId = uuid.toString().substring(0, 8);
            String Role = "Member";
            String query = "INSERT INTO Member (mId, Name, Email, Password, Address, Phone, Role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, mId);
            statement.setString(2, Name);
            statement.setString(3, Email);
            statement.setString(4, Password);
            statement.setString(5, Address);
            statement.setString(6, Phone);
            statement.setString(7, Role);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}