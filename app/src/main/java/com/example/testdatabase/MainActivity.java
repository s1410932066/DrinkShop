package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI元素
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);

        // 註冊按鈕點擊事件
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 創建一個新的Intent來跳轉到RegistrationDetailsActivity
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        // 登入按鈕點擊事件
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String DB_URL = "jdbc:jtds:sqlserver://192.168.10.8:1433/DrinkShop";
                String DB_USERNAME = "sa";
                String DB_PASSWORD = "2ixxddux";

                // 在新线程中执行数据库查询
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 连接到数据库并查询用户信息
                        try {
                            // 建立数据库连接
                            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                            // 创建 SQL 查询语句
                            String query = "SELECT * FROM Member WHERE Email = ? AND Password = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, username);
                            stmt.setString(2, password);

                            // 执行查询
                            ResultSet rs = stmt.executeQuery();
                            Log.e("run: ",rs.toString() );
                            if (rs.next()) {
                                // 用户存在，获取用户角色
                                String role = rs.getString("Role");
//                                Log.e("run: ",role );
                                // 根据角色导航到不同的界面
                                if (role.equals("Administrator")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(MainActivity.this, Administrator.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else if (role.equals("Member")) {
//                                    Log.e("run: ","我有到" );
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(MainActivity.this, Order.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                // 用户不存在或密码错误
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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