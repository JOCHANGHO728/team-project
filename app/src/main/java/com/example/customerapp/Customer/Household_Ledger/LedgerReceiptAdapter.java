package com.example.customerapp.Customer.Household_Ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LedgerReceiptAdapter extends RecyclerView.Adapter<LedgerReceiptAdapter.ViewHolder> {

    private final List<LedgerReceipt> receipts = new ArrayList<>();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);

    public void setData(List<LedgerReceipt> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ReceiptDiffCallback(receipts, items));
        receipts.clear();
        receipts.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LedgerReceipt receipt = receipts.get(position);
        holder.tvOrderId.setText("주문번호 #" + receipt.orderId);
        holder.tvOrderDate.setText(receipt.orderDate);
        holder.tvLines.setText(receipt.linesText);
        holder.tvTotal.setText("합계 " + numberFormat.format(receipt.totalAmount) + "원");
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvOrderDate;
        private final TextView tvLines;
        private final TextView tvTotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_receipt_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_receipt_order_date);
            tvLines = itemView.findViewById(R.id.tv_receipt_lines);
            tvTotal = itemView.findViewById(R.id.tv_receipt_total);
        }
    }

    public static class LedgerReceipt {
        public final long orderId;
        public final String orderDate;
        public final String linesText;
        public final int totalAmount;

        public LedgerReceipt(long orderId, String orderDate, String linesText, int totalAmount) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.linesText = linesText;
            this.totalAmount = totalAmount;
        }
    }

    private static class ReceiptDiffCallback extends DiffUtil.Callback {
        private final List<LedgerReceipt> oldItems;
        private final List<LedgerReceipt> newItems;

        ReceiptDiffCallback(List<LedgerReceipt> oldItems, List<LedgerReceipt> newItems) {
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
            return oldItems.get(oldItemPosition).orderId == newItems.get(newItemPosition).orderId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            LedgerReceipt oldItem = oldItems.get(oldItemPosition);
            LedgerReceipt newItem = newItems.get(newItemPosition);
            return oldItem.totalAmount == newItem.totalAmount
                    && stringEquals(oldItem.orderDate, newItem.orderDate)
                    && stringEquals(oldItem.linesText, newItem.linesText);
        }

        private boolean stringEquals(String left, String right) {
            return left == null ? right == null : left.equals(right);
        }
    }
}
