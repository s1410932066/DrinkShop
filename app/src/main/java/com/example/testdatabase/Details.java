package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    TextView tvOrderNumber;
    TextView tvOrderTime;
    TextView tvTotalAmount;
    List<OrderItem> orderItems = new ArrayList<>();
    DetailsAdapter adapter;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderTime = findViewById(R.id.tvOrderTime);
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

        Button btnReorder = findViewById(R.id.btnReorder);
        btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details.this, Order.class);
                startActivity(intent);
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DeleteOrder();
                    }
                });
                thread.start();
                Toast.makeText(Details.this, "訂單已取消請重新選購", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Details.this, Order.class);
                startActivity(intent);
            }
        });
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
                orderId = resultSet.getString("oId");
                String orderDate = resultSet.getString("OrderDate");
                int totalPrice = resultSet.getInt("TotalPrice");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvOrderNumber.setText("訂單號：" + orderId);
                        tvOrderTime.setText("下單時間：" + orderDate);
                        tvTotalAmount.setText("最終支付金額：$" + totalPrice);
                    }
                });

                String queryDetails = "SELECT o.oId, o.OrderDate, d.Quantity, d.Price, d.TotalPrice, d.Product, d.Size, d.Sweetness " +
                        "FROM Orders o " +
                        "JOIN Details d ON o.oId = d.oId " +
                        "WHERE o.oId = ? " +
                        "ORDER BY o.OrderDate DESC";
                PreparedStatement statement2 = connection.prepareStatement(queryDetails);
                statement2.setString(1, orderId);
                ResultSet resultSet2 = statement2.executeQuery();

                while (resultSet2.next()) {
                    String product = resultSet2.getString("Product");
                    String size = resultSet2.getString("Size");
                    String sweetness = resultSet2.getString("Sweetness");
                    int price = resultSet2.getInt("Price");

                    OrderItem orderItem = new OrderItem(product, sweetness, size, price);
                    orderItems.add(orderItem);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                resultSet2.close();
                statement2.close();
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DeleteOrder() {
        try {
            Connection connection = DatabaseConfig.getConnection();
            String deleteOrderQuery = "DELETE FROM Orders WHERE oId = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteOrderQuery);
            deleteStatement.setString(1, orderId);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                Log.d("DetailsActivity", "Order deleted successfully");
            } else {
                Log.d("DetailsActivity", "Failed to delete order");
            }

            deleteStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}