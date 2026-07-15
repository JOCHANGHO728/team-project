package com.example.customerapp.Customer.Shoppingbasket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartList = new ArrayList<>();

    // 🔥 장바구니 데이터 갱신
    public void setData(List<CartItem> items) {
        if (items == null) return;
        List<CartItem> newItems = new ArrayList<>(items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(cartList, newItems));
        this.cartList = newItems;
        diffResult.dispatchUpdatesTo(this);
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
        holder.tvPrice.setText(String.format(java.util.Locale.KOREA, "₩%,d", product.getPPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvScannedRibbon.setVisibility(item.isScannedInStore() ? View.VISIBLE : View.GONE);

        // 서버 이미지가 없거나 로딩에 실패하면 기본 이미지를 표시한다.
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.ivProduct);

        // 🔥 + 버튼 클릭
        holder.btnPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            CartManager.getInstance().updateQuantity(product, newQuantity);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) listener.onCartUpdated();
        });

        // 🔥 - 버튼 클릭
        holder.btnMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;

            if (newQty <= 0) {
                // 0개면 삭제
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CartManager.getInstance().removeItem(product);
                    cartList.remove(pos);
                    notifyItemRemoved(pos);
                }
            } else {
                item.setQuantity(newQty);
                CartManager.getInstance().updateQuantity(product, newQty);
                notifyItemChanged(holder.getAdapterPosition());
            }
            if (listener != null) listener.onCartUpdated();

        });

        // 삭제 버튼
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CartManager.getInstance().removeItem(product);
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
        TextView tvName, tvPrice, tvQuantity, tvScannedRibbon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvScannedRibbon = itemView.findViewById(R.id.tvScannedRibbon);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private static class DiffCallback extends DiffUtil.Callback {
        private final List<CartItem> oldItems;
        private final List<CartItem> newItems;

        DiffCallback(List<CartItem> oldItems, List<CartItem> newItems) {
            this.oldItems = new ArrayList<>(oldItems);
            this.newItems = new ArrayList<>(newItems);
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Product oldProduct = oldItems.get(oldItemPosition).getProduct();
            Product newProduct = newItems.get(newItemPosition).getProduct();
            return oldProduct.getPId() != null && oldProduct.getPId().equals(newProduct.getPId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            CartItem oldItem = oldItems.get(oldItemPosition);
            CartItem newItem = newItems.get(newItemPosition);
            Product oldProduct = oldItem.getProduct();
            Product newProduct = newItem.getProduct();
            return oldItem.getQuantity() == newItem.getQuantity()
                    && oldItem.getScannedQuantity() == newItem.getScannedQuantity()
                    && oldProduct.getPPrice() == newProduct.getPPrice()
                    && stringEquals(oldProduct.getPName(), newProduct.getPName())
                    && stringEquals(oldProduct.getImageUrl(), newProduct.getImageUrl());
        }

        private boolean stringEquals(String left, String right) {
            return left == null ? right == null : left.equals(right);
        }
    }
}
