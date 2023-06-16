package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Order extends AppCompatActivity {
    private List<String> orderList;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerViewOrderList;
    private Spinner spinnerMenu;
    private Spinner spinnerSize;
    private Spinner spinnerSweetness;
    private Button buttonAddOrder;
    private Button buttonSend;

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
        buttonAddOrder = findViewById(R.id.buttonPlaceOrder);
        buttonSend = findViewById(R.id.buttonSend);

        //初始化
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToDatabase();
                loadProducts();
            }
        });
        thread.start();
        //新增餐點
        buttonAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMenu = spinnerMenu.getSelectedItem().toString();
                String selectedSize = spinnerSize.getSelectedItem().toString();
                String selectedSweetness = spinnerSweetness.getSelectedItem().toString();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int price = getPriceFromDatabase(selectedMenu);

                        if (selectedSize.equals("中杯(+$5)")) {
                            price += 5;
                        } else if (selectedSize.equals("大杯(+$10)")) {
                            price += 10;
                        }

                        String order = selectedMenu + " - " + selectedSize + " - " + selectedSweetness + " - " + price + "元";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                orderList.add(order);
                                orderAdapter.notifyDataSetChanged();
                                Thread priceThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int totalPrice = calculateTotalPrice();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateTotalPrice(totalPrice);
                                            }
                                        });
                                    }
                                });
                                priceThread.start();
                            }
                        });
                    }
                });

                thread.start();
            }
        });
        //確認送出
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread sendThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        insertOrderData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Order.this, Details.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                sendThread.start();
            }
        });
    }

    /**
     * 資料庫連接
     */
    private void connectToDatabase() {
        try {
            // 建立資料庫連線
            connection = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存資料庫裡抓商品放置下拉是選單
     */
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

    /**
     * 抓取特定商品的價格
     */
    private int getPriceFromDatabase(String menu) {
        int price = 0;
        try {
            String query = "SELECT Price FROM Product WHERE ProductName = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, menu);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                price = resultSet.getInt("Price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resultSet and statement
            closeResultSetAndStatement();
        }
        return price;
    }

    /**
     * 計算總額
     */
    private int calculateTotalPrice() {
        int totalPrice = 0;

        for (String order : orderList) {
            String[] orderDetails = order.split(" - ");
            String selectedMenu = orderDetails[0];
            String selectedSize = orderDetails[1];

            int price = getPriceFromDatabase(selectedMenu);

            if (selectedSize.equals("中杯(+$5)")) {
                price += 5;
            } else if (selectedSize.equals("大杯(+$10)")) {
                price += 10;
            }

            totalPrice += price;
        }

        return totalPrice;
    }

    /**
     * 刷新總金額
     */
    private void updateTotalPrice(int totalPrice) {
        TextView textViewTotalPrice = findViewById(R.id.textViewTotalAmount);
        textViewTotalPrice.setText("總金額: $" + totalPrice);
    }

    /**
     * 關閉連結
     */
    private void closeResultSetAndStatement() {
        try {
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

    /**
     * 新增訂單
     */
    private void insertOrderData() {
        try {
            Connection conn = DatabaseConfig.getConnection();
//            String orderId = generateUniqueOrderId(conn);
            UUID uuid = UUID.randomUUID();
            String orderId = uuid.toString().substring(0, 8);
            SharedPreferences user= getSharedPreferences("user", MODE_PRIVATE);
            String memberId = user.getString("mid"," ");
            // 獲取當前日期和時間
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String orderDate = dateFormat.format(new Date());

            // 將資料插入Order資料表
            String orderQuery = "INSERT INTO Orders (oId, mId, OrderDate, Status) VALUES (?, ?, ?, ?)";
            PreparedStatement orderStatement = connection.prepareStatement(orderQuery);
            orderStatement.setString(1, orderId);
            orderStatement.setString(2, memberId);
            orderStatement.setString(3, orderDate);
            orderStatement.setString(4, "準備中");
            orderStatement.executeUpdate();
            orderStatement.close();

            // 获取最新插入的订单的 oId
            String getLastOrderIdQuery = "SELECT MAX(oId) AS lastOrderId FROM Orders";
            Statement getLastOrderIdStatement = conn.createStatement();
            ResultSet resultSet = getLastOrderIdStatement.executeQuery(getLastOrderIdQuery);

            String oId = null;
            if (resultSet.next()) {
                oId = resultSet.getString("lastOrderId");
            }

            getLastOrderIdStatement.close();

            // 將資料插入Details資料表
            String detailsQuery = "INSERT INTO Details (dId, oId, Quantity, Product, Price, Size, Sweetness, TotalPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement detailsStatement = connection.prepareStatement(detailsQuery);
            for (String order : orderList) {
                String[] orderDetails = order.split(" - ");
                String selectedMenu = orderDetails[0];
                String selectedSize = orderDetails[1];
                String selectedSweetness = orderDetails[2];
                int price = Integer.parseInt(orderDetails[3].replace("元", ""));

                // 生成唯一的明細ID（dId）
                String detailId = generateUniqueDetailId(conn);
                detailsStatement.setString(1, detailId);
                detailsStatement.setString(2, oId);
                detailsStatement.setInt(3, 1); // 假設數量總是1
                detailsStatement.setString(4, selectedMenu);
                detailsStatement.setInt(5, price);
                detailsStatement.setString(6, selectedSize);
                detailsStatement.setString(7, selectedSweetness);
                detailsStatement.setInt(8, price);
                detailsStatement.executeUpdate();

            }
            detailsStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成oId
     */
//    private String generateUniqueOrderId(Connection connection) throws SQLException {
//        String query = "SELECT MAX(oId) AS maxId FROM Orders";
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery(query);
//        resultSet.next();
//        String maxId = resultSet.getString("maxId");
//        int nextId;
//        if (maxId == null) {
//            nextId = 1;
//        } else {
//            nextId = Integer.parseInt(maxId) + 1;
//        }
//        return String.valueOf(nextId);
//    }
    /**
     * 生成dId
     */
    private String generateUniqueDetailId(Connection connection) throws SQLException {
            String query = "SELECT MAX(dId) AS maxId FROM Details";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
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