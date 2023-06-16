package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                if (Email.equals("") || Password.equals("") || Name.equals("") || Phone.equals("") || Address.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "請輸入完整", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveDataToSqlServer(Name, Email, Password, Address, Phone);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 存取註冊資料
     */
    private void saveDataToSqlServer(String Name, String Email, String Password, String Address, String Phone){

        try {
            Connection conn = DatabaseConfig.getConnection();
            String nextId = getNextMemberId(conn);
            String Role = "Member";
            String query = "INSERT INTO Member (mId, Name, Email, Password, Address, Phone, Role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nextId);
            statement.setString(2, Name);
            statement.setString(3, Email);
            statement.setString(4, Password);
            statement.setString(5, Address);
            statement.setString(6, Phone);
            statement.setString(7, Role);
            statement.executeUpdate();

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *ID遞增生成
     */
    private String getNextMemberId(Connection connection) throws SQLException {
        String query = "SELECT MAX(mId) AS maxId FROM Member";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        // 获取当前最大的 mId 值
        String maxId = resultSet.getString("maxId");
        int nextId;
        if (maxId == null) {
            nextId = 1;
        } else {
            nextId = Integer.parseInt(maxId) + 1;
        }
        return String.valueOf(nextId);
    }
}