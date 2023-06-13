package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {
    private List<String> orderList;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerViewOrderList;
    private Spinner spinnerMenu;
    private Spinner spinnerSize;
    private Spinner spinnerSweetness;
    private Button buttonPlaceOrder;

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);

        recyclerViewOrderList = findViewById(R.id.recyclerViewOrderList);
        recyclerViewOrderList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrderList.setAdapter(orderAdapter);

        spinnerMenu = findViewById(R.id.spinnerMenu);
        spinnerSize = findViewById(R.id.spinnerSize);
        spinnerSweetness = findViewById(R.id.spinnerSweetness);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order = spinnerMenu.getSelectedItem().toString() + " - " +
                        spinnerSize.getSelectedItem().toString() + " - " +
                        spinnerSweetness.getSelectedItem().toString();
                orderList.add(order);
                orderAdapter.notifyDataSetChanged();
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToDatabase();
                loadProducts();
            }
        });
        thread.start();
    }

    private void connectToDatabase() {
        try {
            // 載入驅動程式
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            // 設定資料庫連接資訊
            String url = "jdbc:jtds:sqlserver://192.168.10.8:1433/DrinkShop";
            String username = "sa";
            String password = "2ixxddux";
            // 建立資料庫連線
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        try {
            // 建立 SQL 查詢
            String query = "SELECT ProductName FROM Product";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            // 清空下拉式選單
            List<String> productNames = new ArrayList<>();
            while (resultSet.next()) {
                String productName = resultSet.getString("ProductName");
//                Integer price = resultSet.getInt("Price");
                productNames.add(productName);
            }

            // 將產品名稱加入下拉式選單
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMenu.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // 關閉連線和資源
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}