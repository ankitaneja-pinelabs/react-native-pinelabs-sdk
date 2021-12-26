package com.reactnativepinelabssdk;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import java.util.HashMap;

import com.facebook.react.bridge.ReadableMap;
import com.pinelabs.sdk.HashingAlgorithm;
import com.pinelabs.sdk.IPinePGResponseCallback;
import com.pinelabs.sdk.Order;
import com.pinelabs.sdk.PinePGConfig;
import com.pinelabs.sdk.PinePGPaymentManager;

import org.json.JSONObject;

public class PinelabsSdkModule extends ReactContextBaseJavaModule {
  private PinePGPaymentManager paymentManager;

  PinelabsSdkModule(ReactApplicationContext context){
    super(context);
    paymentManager = new PinePGPaymentManager();
  }

  @Override
  public String getName() {
    return "PinelabsSdk";
  }

  @ReactMethod
  public void start(ReadableMap options, Callback onResponse){
    Boolean isProductionRequest = options.hasKey("isProductionRequest") && options.getBoolean("isProductionRequest");
    Boolean isHeaderToBeShow = !options.hasKey("isHeaderToBeShow") || options.getBoolean("isHeaderToBeShow");
    String diaSecret = options.getString("diaSecret");
    String diaSecretType = options.getString("diaSecretType");
    int themeId = options.hasKey("themeId") ? options.getInt("themeId") : 1;


    Order order = createOrder(options);
    Map<String, String> pinePGPaymentParam = createPinePgPaymentParam(order);

    pinePGPaymentParam.put(PinePGConfig.PaymentParamsConstants.DIA_SECRET, diaSecret);
    pinePGPaymentParam.put(PinePGConfig.PaymentParamsConstants.DIA_SECRET_TYPE, diaSecretType);

    Context context = getReactApplicationContext();
    Activity activity = getCurrentActivity();
    paymentManager.startPayment(pinePGPaymentParam, activity, themeId, isHeaderToBeShow, isProductionRequest, new IPinePGResponseCallback() {
      @Override
      public void internetNotAvailable(int code, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onErrorOccured(int code, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onTransactionResponse() {
        onResponse.invoke();
      }

      @Override
      public void onCancelTxn(int code, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onPressedBackButton(int code, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }
    });
  }

  @ReactMethod
  public void generateHash(ReadableMap options, String secretKey, Promise promise) {
    Order order = createOrder(options);
    Map<String, String> pinePGPaymentParam = createPinePgPaymentParam(order);
    String secureType = "SHA256";
    JSONObject pinePGJson = new JSONObject(pinePGPaymentParam);
    String hash = HashingAlgorithm.GenerateHash(pinePGPaymentParam, secretKey, secureType, String.valueOf(pinePGJson));
    promise.resolve(hash);
  }

  private Order createOrder(ReadableMap options){
    String merchantTxnId = options.getString("merchantTxnId");
    int merchantId = options.getInt("merchantId");
    int amount = options.getInt("amount");
    String merchantAccessCode = options.getString("merchantAccessCode");
    int navigationMode = options.getInt("navigationMode");
    int transactionType = options.getInt("transactionType");
    String payModeOnLandingPage = options.getString("payModeOnLandingPage");
    String customerEmail = options.getString("customerEmail");
    String customerMobileNo = options.getString("customerMobileNo");
    String customerId = options.getString("customerId");
    String customerAddress = options.getString("customerAddress");
    String customerAddressPin = options.getString("customerAddressPin");
    String productCode = options.hasKey("productCode") ? options.getString("productCode") : "";
    Boolean isProductionRequest = options.hasKey("isProductionRequest") && options.getBoolean("isProductionRequest");


    Order order = new Order();
    order
      .setMerchantId(merchantId)
      .setMerchantUrl(isProductionRequest ? PinePGConfig.PINE_PG_RETURN_URL_PRODUCTION : PinePGConfig.PINE_PG_PRETURN_URL_SANDBOX)
      .setMerchantAccessCode(merchantAccessCode)
      .setAmountInPaise(amount)
      .setUniqueMerchantTxnId(merchantTxnId)
      .setNavigationMode(navigationMode)
      .setTransactionType(transactionType)
      .setPayModeOnLandingPage(payModeOnLandingPage)
      .setCustomerEmail(customerEmail)
      .setCustomerMobileNo(customerMobileNo)
      .setCustomerId(customerId)
      .setCustomerAddress(customerAddress)
      .setCustomerAddressPin(customerAddressPin)
      .setProductCode(productCode);
    return order;
  }

  private Map<String, String> createPinePgPaymentParam(Order objPaymentParam) {
    Map<String, String> hmPaymentParam = new HashMap<>();
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.UNIQUE_MERCHANT_TXN_ID, objPaymentParam.getUniqueMerchantTxnId());
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.MERCHANT_ID, String.valueOf(objPaymentParam.getMerchantId()));
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.AMOUNT, String.valueOf(objPaymentParam.getAmountInPaise()));
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.MERCHANT_ACCESS_CODE, String.valueOf(objPaymentParam.getMerchantAccessCode()));
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.NAVIGATION_MODE, String.valueOf(objPaymentParam.getNavigationMode()));
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.TRANSACTION_TYPE, String.valueOf(objPaymentParam.getTransactionType()));
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.PAYMENT_MODE_LKANDING_PAGE, objPaymentParam.getPayModeOnLandingPage());
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.CUSTOMER_EMAIL, objPaymentParam.getCustomerEmail());
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.CUSTOMER_MOBILE_NO, objPaymentParam.getCustomerMobileNo());
    return hmPaymentParam;
  }
}
