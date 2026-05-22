package com.example.customerapp.Customer.Shoppingbasket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartList = new ArrayList<>();

    // 🔥 장바구니 데이터 갱신
    public void setData(List<CartItem> items) {
        if (items == null) return;
        this.cartList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        Product product = item.getProduct();

        // 🔥 상품명 / 가격 / 수량 표시
        holder.tvName.setText(product.getPName());
        holder.tvPrice.setText(product.getPPrice() + "원");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // 🔥 이미지 로딩 (서버 이미지 없으면 placeholder)
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())  // Product 클래스에서 기본 URL 제공
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.ivProduct);

        // 🔥 + 버튼 클릭
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) listener.onCartUpdated();
        });

        // 🔥 - 버튼 클릭
        holder.btnMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;

            if (newQty <= 0) {
                // 0개면 삭제
                cartList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            } else {
                item.setQuantity(newQty);
                notifyItemChanged(holder.getAdapterPosition());
            }
            if (listener != null) listener.onCartUpdated();

        });

        // 삭제 버튼
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                cartList.remove(pos);
                notifyItemRemoved(pos);
            }
            if (listener != null) listener.onCartUpdated();
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public interface OnCartChangeListener {
        void onCartUpdated();
    }

    private OnCartChangeListener listener;

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct, btnPlus, btnMinus, btnDelete;
        TextView tvName, tvPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}