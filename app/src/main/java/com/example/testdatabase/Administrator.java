package com.example.testdatabase;

import static com.example.testdatabase.DatabaseConfig.getConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Administrator extends AppCompatActivity {
    private static final String TAG = "Administrator";
    EditText editTextProductName;
    EditText editTextProductPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = editTextProductName.getText().toString();
                String productPrice = editTextProductPrice.getText().toString();
                // 新增新品飲料到Product資料表
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addNewProduct(productName, Integer.parseInt(productPrice));
                    }
                });
                thread.start();

            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 查詢購買次數最多的會員
                queryMostFrequentMember();
                // 計算店裡銷售金額
                calculateTotalSales();
            }
        });
        thread.start();
    }


    private void addNewProduct(String productName, int price) {
        try {
            Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();

            // 获取下一个可用的 pId 值，假设自增主键的起始值为 1
            String getNextIdQuery = "SELECT MAX(pId) AS nextId FROM Product";
            ResultSet resultSet = stmt.executeQuery(getNextIdQuery);
            int nextId = 1; // 默认起始值为 1
            if (resultSet.next()) {
                nextId = resultSet.getInt("nextId") + 1;
            }

            // 执行插入操作，显式指定 pId 列的值
            String sql = "INSERT INTO Product (pId, ProductName, Price) VALUES (" + nextId + ", '" + productName + "', " + price + ");";
            stmt.executeUpdate(sql);
            editTextProductName.setText("");
            editTextProductPrice.setText("");
            Toast.makeText(Administrator.this, "新增新品飲料成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "新增新品飲料成功");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            Log.e(TAG, "新增新品飲料失敗：" + e.getMessage());
        }
    }

    private void queryMostFrequentMember() {
        try {
            Connection conn = DatabaseConfig.getConnection(); // 獲取資料庫連接
            Statement stmt = conn.createStatement();

            String sql = "SELECT Member.Name, Member.Email, Member.Phone, COUNT(Orders.oId) AS OrderCount " +
                    "FROM Member " +
                    "INNER JOIN Orders ON Member.mId = Orders.mId " +
                    "WHERE Orders.OrderDate IS NOT NULL " +
                    "GROUP BY Member.Name, Member.Email, Member.Phone " +
                    "ORDER BY OrderCount DESC;";
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<String> memberList = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                int orderCount = rs.getInt("OrderCount");

                String memberInfo = "會員名稱：" + name + "\n會員電子信箱：" + email + "\n會員電話：" + phone + "\n訂單次數：" + orderCount;
                memberList.add(memberInfo);
            }

            rs.close();
            stmt.close();
            conn.close();

            // 创建适配器并设置给 ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberList);
            ListView listViewPopularProducts = findViewById(R.id.listViewPopularProducts);
            listViewPopularProducts.setAdapter(adapter);
        } catch (SQLException e) {
            Log.e(TAG, "查詢購買次數最多的會員失敗：" + e.getMessage());
        }
    }

    private void calculateTotalSales() {
        try {
            Connection conn = DatabaseConfig.getConnection(); // 獲取資料庫連接
            Statement stmt = conn.createStatement();

            String sql = "SELECT SUM(Details.Price) AS TotalSales FROM Details;";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                double totalSales = rs.getDouble("TotalSales");

                TextView textViewTotalSales = findViewById(R.id.textViewTotalSales);
                textViewTotalSales.setText("總銷售額：" + totalSales);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            Log.e(TAG, "計算店裡銷售金額失敗：" + e.getMessage());
        }
    }
}