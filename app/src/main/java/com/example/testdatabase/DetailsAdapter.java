package com.example.testdatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {
    private List<OrderItem> orderItems;

    public DetailsAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new DetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProduct;
        private TextView tvSweetness;
        private TextView tvSize;
        private TextView tvAmount;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvSweetness = itemView.findViewById(R.id.tvSweetness);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        public void bind(OrderItem orderItem) {
            tvProduct.setText("商品: " + orderItem.getProduct());
            tvSweetness.setText("甜度: " + orderItem.getSweetness());
            tvSize.setText("容量: " + orderItem.getSize());
            tvAmount.setText("價格: " + orderItem.getAmount() + " 元");
        }
    }
}
