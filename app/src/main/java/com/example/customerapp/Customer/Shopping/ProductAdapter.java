package com.example.customerapp.Customer.Shopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> productList;
    private OnAddToCartClickListener addToCartClickListener;
    private OnProductClickListener productClickListener;

    public ProductAdapter(List<Product> productList) {
        this.productList = new ArrayList<>(productList);
        setHasStableIds(true);
    }

    public ProductAdapter(List<Product> productList, OnAddToCartClickListener listener) {
        this(productList);
        this.addToCartClickListener = listener;
    }

    public ProductAdapter(List<Product> productList, OnAddToCartClickListener addListener, OnProductClickListener clickListener) {
        this(productList);
        this.addToCartClickListener = addListener;
        this.productClickListener = clickListener;
    }

    public void setData(List<Product> products) {
        List<Product> newProducts = new ArrayList<>(products);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProductDiffCallback(productList, newProducts));
        productList.clear();
        productList.addAll(newProducts);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getPName());
        holder.tvPrice.setText(String.format(Locale.KOREA, "%,d원", product.getPPrice()));
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.ivProduct);
        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
        holder.btnPlus.setOnClickListener(v -> {
            if (addToCartClickListener != null) {
                addToCartClickListener.onAddToCart(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public long getItemId(int position) {
        Product product = productList.get(position);
        return product.getPId() == null ? RecyclerView.NO_ID : product.getPId();
    }

    public interface OnAddToCartClickListener {
        void onAddToCart(Product product);
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivProduct, btnPlus;
        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }

    private static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Product> oldItems;
        private final List<Product> newItems;

        ProductDiffCallback(List<Product> oldItems, List<Product> newItems) {
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
            Product oldProduct = oldItems.get(oldItemPosition);
            Product newProduct = newItems.get(newItemPosition);
            return oldProduct.getPId() != null && oldProduct.getPId().equals(newProduct.getPId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Product oldProduct = oldItems.get(oldItemPosition);
            Product newProduct = newItems.get(newItemPosition);
            return oldProduct.getPPrice() == newProduct.getPPrice()
                    && stringEquals(oldProduct.getPName(), newProduct.getPName())
                    && stringEquals(oldProduct.getImageUrl(), newProduct.getImageUrl());
        }

        private boolean stringEquals(String left, String right) {
            return left == null ? right == null : left.equals(right);
        }
    }
}
