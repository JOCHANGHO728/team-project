package com.example.customerapp.Customer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.Customer.Shopping.ProductAdapter;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;

import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent; // Intent 사용을 위해 필요
import com.google.android.material.bottomnavigation.BottomNavigationView; // BottomNavigationView 사용을 위해 필요

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Customer extends AppCompatActivity {

    private RecyclerView rvCategory, rvProductList;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private String currentCategory = "식품"; // 현재 선택된 카테고리 추적

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        // 1. 뷰 초기화
        rvCategory = findViewById(R.id.rv_category);
        rvProductList = findViewById(R.id.rv_product_list);
        SearchView svProductSearch = findViewById(R.id.sv_product_search);

        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        // 2. 카테고리 목록 데이터 (이미지 기준 항목들)
        List<String> categories = Arrays.asList("식품", "가정용품", "전자제품", "과자", "음료", "의류");

        // 3. 카테고리 어댑터 설정
        categoryAdapter = new CategoryAdapter(categories, categoryName -> {
            currentCategory = categoryName; // 카테고리 변경 시 저장
            updateProductList(categoryName);
            svProductSearch.setQuery("", false); // 카테고리 바꿀 때 검색창 초기화
        });
        rvCategory.setAdapter(categoryAdapter);

        // 4. 검색 기능 구현 (실시간 필터링)
        svProductSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText); // 글자 입력할 때마다 실시간 필터링
                return true;
            }
        });

        // 5. 초기 화면 설정
        updateProductList(currentCategory);

        // 6. 하단 네비게이션 설정 (추가되는 부분)
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // 현재 화면인 '상품목록' 아이콘을 활성화 상태로 표시
        bottomNavigation.setSelectedItemId(R.id.nav_shopping);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_shopping) {
                return true; // 현재 화면이므로 아무것도 하지 않음
            }
            else if (id == R.id.nav_cart) {
                intent = new Intent(this, com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket.class);
            }
            else if (id == R.id.nav_ledger) {
                intent = new Intent(this, com.example.customerapp.Customer.Household_Ledger.household_Ledger.class);
            }
            else if (id == R.id.nav_my_info) {
                intent = new Intent(this, com.example.customerapp.Customer.MyInfo.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    // 상품 리스트 업데이트 (전체 출력)
    private void updateProductList(String categoryName) {
        List<Product> productList = getProductsByCategory(categoryName);
        productAdapter = new ProductAdapter(productList);
        rvProductList.setAdapter(productAdapter);
    }

    // 검색어에 따른 상품 필터링 로직
    private void filterProducts(String query) {
        List<Product> allProducts = getProductsByCategory(currentCategory);
        List<Product> filteredList = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter = new ProductAdapter(filteredList);
        rvProductList.setAdapter(productAdapter);
    }



    // --- 카테고리 어댑터 (이미지 제외 및 선택 강조) ---
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<String> categories;
        private OnCategoryClickListener listener;
        private int selectedPosition = 0;

        public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
            this.categories = categories;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_left, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = categories.get(position);
            holder.tvName.setText(name);

            // 선택 상태에 따른 디자인 (배경 화이트/연회색)
            if (selectedPosition == position) {
                holder.itemView.setBackgroundColor(Color.WHITE);
                holder.tvName.setTextColor(Color.BLACK);
                holder.tvName.setTypeface(null, Typeface.BOLD);
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.tvName.setTextColor(Color.GRAY);
                holder.tvName.setTypeface(null, Typeface.NORMAL);
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onCategoryClick(name);
            });
        }

        @Override
        public int getItemCount() { return categories.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_category_name);
            }
        }
    }
    // <--- 여기에 메서드를 복사해서 넣으세요!
    private List<Product> getProductsByCategory(String categoryName) {
        return new ArrayList<>();
    }
}
