package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextAccount;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI元素
        editTextAccount = findViewById(R.id.editLoginAccount);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);
        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();

        // 註冊按鈕點擊事件
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 創建一個新的Intent來跳轉到RegistrationDetailsActivity
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        // 登入按鈕點擊事件
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Account = editTextAccount.getText().toString();
                String Password = editTextPassword.getText().toString();

                // 在新线程中执行数据库查询
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 建立数据库连接
                            Connection conn = DatabaseConfig.getConnection();
                            // 创建 SQL 查询语句
                            String query = "SELECT * FROM Member WHERE Account = ? AND Password = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, Account);
                            stmt.setString(2, Password);

                            // 执行查询
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {

                                editor.putString("mid",rs.getString("mId"));
                                editor.commit();
                                // 用户存在，获取用户角色
                                String role = rs.getString("Role");
                                // 根据角色导航到不同的界面
                                if (role.equals("Administrator")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(LoginActivity.this, Administrator.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else if (role.equals("Member")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(LoginActivity.this, Order.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                // 用户不存在或密码错误
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "電子信箱或密碼錯誤", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // 关闭数据库连接
                            rs.close();
                            stmt.close();
                            conn.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 启动线程
                thread.start();
            }
        });
    }
}