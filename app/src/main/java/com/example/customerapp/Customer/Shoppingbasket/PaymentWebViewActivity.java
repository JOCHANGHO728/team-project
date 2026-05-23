package com.example.customerapp.Customer.Shoppingbasket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentWebViewActivity extends AppCompatActivity {

    // 토스페이먼츠 테스트 클라이언트 키로 교체해서 사용하세요.
    private static final String TOSS_TEST_CLIENT_KEY = "test_ck_your_client_key";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        if (totalPrice <= 0) {
            finish();
            return;
        }

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        String orderId = "ORDER_" + System.currentTimeMillis();
        String html = "<!doctype html><html><head><meta charset='utf-8' />"
                + "<meta name='viewport' content='width=device-width, initial-scale=1' />"
                + "<title>Toss Payment</title></head><body>"
                + "<script src='https://js.tosspayments.com/v2/standard'></script>"
                + "<script>"
                + "const clientKey='" + TOSS_TEST_CLIENT_KEY + "';"
                + "const tossPayments=TossPayments(clientKey);"
                + "const payment=tossPayments.payment({customerKey:'ANONYMOUS'});"
                + "payment.requestPayment({"
                + "method:'CARD',"
                + "amount:{value:" + totalPrice + ",currency:'KRW'},"
                + "orderId:'" + orderId + "',"
                + "orderName:'장바구니 결제',"
                + "successUrl:'https://tosspayments.com/success',"
                + "failUrl:'https://tosspayments.com/fail'"
                + "}).catch(function(){"
                + "document.body.innerHTML='<h3>결제창 호출 실패</h3>';"
                + "});"
                + "</script></body></html>";

        webView.loadDataWithBaseURL("https://tosspayments.com", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
