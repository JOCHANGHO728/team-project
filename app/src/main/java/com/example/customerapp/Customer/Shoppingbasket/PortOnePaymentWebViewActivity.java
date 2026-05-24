package com.example.customerapp.Customer.Shoppingbasket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customerapp.BuildConfig;

public class PortOnePaymentWebViewActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri callbackUri = getIntent() != null ? getIntent().getData() : null;
        if (callbackUri != null
                && "customerapp".equalsIgnoreCase(callbackUri.getScheme())
                && "payment-callback".equalsIgnoreCase(callbackUri.getHost())) {
            openResultPage(callbackUri, 0);
            return;
        }

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        if (totalPrice <= 0) {
            finish();
            return;
        }

        String impCode = BuildConfig.PORTONE_IMP_CODE;
        String pgCode = getIntent().getStringExtra("pg_code");
        String payName = getIntent().getStringExtra("pay_name");
        if (pgCode == null || pgCode.isBlank()) {
            pgCode = BuildConfig.PORTONE_PG_TOSS;
        }
        if (payName == null || payName.isBlank()) {
            payName = "간편결제";
        }
        if (impCode == null || impCode.isBlank() || "imp_your_code".equals(impCode)) {
            Toast.makeText(this, "PortOne 가맹점 식별코드(imp_xxx)를 설정하세요.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (pgCode == null || pgCode.isBlank()) {
            Toast.makeText(this, "PortOne PG 코드가 설정되지 않았습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        WebView webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(Uri.parse(url));
            }

            private boolean handleUrl(Uri uri) {
                String url = uri.toString();
                if (url.startsWith("customerapp://payment-callback")) {
                    openResultPage(uri, totalPrice);
                    return true;
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent parsedIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (parsedIntent != null) {
                            startActivity(parsedIntent);
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                    return true;
                }

                String scheme = uri.getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    return false;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (Exception e) {
                    Toast.makeText(PortOnePaymentWebViewActivity.this, "결제 앱 실행 실패", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        String merchantUid = "mid_" + System.currentTimeMillis();
        String html = "<!doctype html><html><head><meta charset='utf-8' />"
                + "<meta name='viewport' content='width=device-width, initial-scale=1' />"
                + "<title>PortOne Payment</title></head><body>"
                + "<script src='https://code.jquery.com/jquery-1.12.4.min.js'></script>"
                + "<script src='https://cdn.iamport.kr/js/iamport.payment-1.2.0.js'></script>"
                + "<div style='font-family:sans-serif;padding:24px;'>"
                + "<h3>" + payName + "로 이동 중입니다</h3>"
                + "<p id='status' style='margin-top:12px;color:#666;'>결제창을 준비하고 있습니다.</p>"
                + "</div>"
                + "<script>"
                + "const IMP=window.IMP;"
                + "const statusEl=document.getElementById('status');"
                + "if(!IMP){statusEl.innerText='PortOne 스크립트 로드 실패';}"
                + "else{IMP.init('" + impCode + "');setTimeout(startPay,300);}"
                + "function startPay(){"
                + "statusEl.innerText='결제창을 여는 중...';"
                + "if(!IMP){return;}"
                + "IMP.request_pay({"
                + "pg:'" + pgCode + "',"
                + "pay_method:'card',"
                + "merchant_uid:'" + merchantUid + "',"
                + "name:'장바구니 결제',"
                + "amount:" + totalPrice + ","
                + "buyer_name:'고객',"
                + "buyer_tel:'01012345678',"
                + "app_scheme:'customerapp',"
                + "m_redirect_url:'customerapp://payment-callback?amount=" + totalPrice + "'"
                + "},function(rsp){"
                + "if(rsp.success){"
                + "window.location.href='customerapp://payment-callback?imp_success=true"
                + "&amount=" + totalPrice + "&imp_uid='+(rsp.imp_uid||'')+'&merchant_uid='+(rsp.merchant_uid||'');"
                + "}else{"
                + "window.location.href='customerapp://payment-callback?imp_success=false"
                + "&error_msg=' + encodeURIComponent(rsp.error_msg||'알 수 없는 오류');"
                + "}"
                + "});"
                + "}"
                + "</script></body></html>";

        webView.loadDataWithBaseURL("https://pay.customerapp.local", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openResultPage(Uri uri, int fallbackAmount) {
        int paymentAmount = fallbackAmount;
        String amountValue = uri.getQueryParameter("amount");
        if (paymentAmount <= 0 && amountValue != null) {
            try {
                paymentAmount = Integer.parseInt(amountValue);
            } catch (NumberFormatException ignored) {
            }
        }

        String successValue = uri.getQueryParameter("imp_success");
        if (successValue == null) {
            successValue = uri.getQueryParameter("success");
        }

        boolean isSuccess = "true".equalsIgnoreCase(successValue);
        if (!isSuccess) {
            String errorMsg = uri.getQueryParameter("error_msg");
            if (errorMsg == null || errorMsg.isBlank()) {
                errorMsg = "결제에 실패했습니다.";
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("total_price", paymentAmount);
        intent.putExtra("merchant_uid", uri.getQueryParameter("merchant_uid"));
        intent.putExtra("imp_uid", uri.getQueryParameter("imp_uid"));
        startActivity(intent);
        finish();
    }
}
