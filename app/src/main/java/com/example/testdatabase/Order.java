package com.example.testdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {
    private List<String> orderList;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerViewOrderList;
    private Spinner spinnerMenu;
    private Spinner spinnerSize;
    private Spinner spinnerToppings;
    private Button buttonPlaceOrder;
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
        spinnerToppings = findViewById(R.id.spinnerSweetness);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order = spinnerMenu.getSelectedItem().toString() + " - " +
                        spinnerSize.getSelectedItem().toString() + " - " +
                        spinnerToppings.getSelectedItem().toString();
                orderList.add(order);
                orderAdapter.notifyDataSetChanged();
            }
        });
    }
}