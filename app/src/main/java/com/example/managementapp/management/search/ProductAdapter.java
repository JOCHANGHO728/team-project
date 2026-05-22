package com.example.managementapp.management.search;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managementapp.R;
import com.example.managementapp.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductResponse> productList = new ArrayList<>();

    // 🔥 클릭 리스너 인터페이스
    public interface OnProductClickListener {
        void onProductClick(ProductResponse product);
    }

    private OnProductClickListener listener;

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    // 선택된 아이템 위치 저장
    private int selectedPosition = RecyclerView.NO_POSITION;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);

        // 데이터 바인딩
        holder.tvName.setText(product.getP_name());
        holder.tvPrice.setText("가격: " + product.getP_price());
        holder.tvQuantity.setText("수량: " + product.getP_quantity());
        holder.tvCategory.setText("카테고리: " + product.getCategory());

        // 🔥 선택된 아이템 배경색 처리
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#D0E8FF"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // 🔥 클릭 시 선택 처리 + 리스너 호출
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<ProductResponse> list) {
        this.productList = list;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}