package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    TextView tvOrderNumber;
    TextView tvOrderTime;
    TextView tvDeliveryAddress;
    TextView tvTotalAmount;
    List<OrderItem> orderItems = new ArrayList<>();
    DetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderTime = findViewById(R.id.tvOrderTime);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        RecyclerView recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getLatestOrderData();
            }
        });
        thread.start();
        adapter = new DetailsAdapter(orderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrderItems.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewOrderItems.setAdapter(adapter);
    }

    private void getLatestOrderData() {
        try {
            Connection connection = DatabaseConfig.getConnection();
            String queryOrders = "SELECT TOP 1 o.oId, o.OrderDate, d.Quantity, d.Price, d.TotalPrice, d.Product, d.Size, d.Sweetness " +
                    "FROM Orders o " +
                    "JOIN Details d ON o.oId = d.oId " +
                    "WHERE o.OrderDate = (SELECT MAX(OrderDate) FROM Orders)";
            PreparedStatement statement = connection.prepareStatement(queryOrders);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String orderId = resultSet.getString("oId");
                //Log.e( "getLatestOrderData: ", orderId);
                String orderDate = resultSet.getString("OrderDate");
                String quantity = resultSet.getString("Quantity");
                int totalPrice = resultSet.getInt("TotalPrice");
                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvOrderNumber.setText("訂單號：" + orderId);
                        tvOrderTime.setText("下單時間：" + orderDate);
                        tvDeliveryAddress.setText("配送地址：" + quantity);
                        tvTotalAmount.setText("最终支付金額：$" + totalPrice);

                    }
                });
            }

            String queryDetails = "SELECT o.oId, o.OrderDate, d.Quantity, d.Price, d.TotalPrice, d.Product, d.Size, d.Sweetness " +
                    "FROM Orders o " +
                    "JOIN Details d ON o.oId = d.oId " +
                    "WHERE o.oId = ? " +
                    "ORDER BY o.OrderDate DESC";
            PreparedStatement statement2 = connection.prepareStatement(queryDetails);
            statement2.setString(1, resultSet.getString("oId"));
            ResultSet resultSet2 = statement2.executeQuery();
            while (resultSet2.next()) {
                String product = resultSet2.getString("Product");
                String size = resultSet2.getString("Size");
                String sweetness = resultSet2.getString("Sweetness");
                String price = resultSet2.getString("Price");

                OrderItem orderItem = new OrderItem(product, sweetness, size, price);
                orderItems.add(orderItem);
                adapter.notifyDataSetChanged();
            }

            resultSet.close();
            statement.close();
            resultSet2.close();
            statement2.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}