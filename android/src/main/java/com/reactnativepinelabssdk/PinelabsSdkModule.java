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
        onResponse.invoke();
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
    String customerId = options.hasKey("customerId") ? options.getString("customerId") : "";
    String customerAddress = options.hasKey("customerAddress") ? options.getString("customerAddress") : "";
    String customerAddressPin = options.hasKey("customerAddressPin") ? options.getString("customerAddressPin") : "";
    String productCode = options.hasKey("productCode") ? options.getString("productCode") : "";
    Boolean isProductionRequest = options.hasKey("isProductionRequest") && options.getBoolean("isProductionRequest");
    String udfField1 = options.hasKey("udfField1") ? options.getString("udfField1") : "";
    String udfField2 = options.hasKey("udfField2") ? options.getString("udfField2") : "";
    String udfField3 = options.hasKey("udfField3") ? options.getString("udfField3") : "";
    String udfField4 = options.hasKey("udfField4") ? options.getString("udfField4") : "";
    String multiCartProductDetails = options.hasKey("multiCartProductDetails") ? options.getString("multiCartProductDetails") : "";
    int requestAgent = options.hasKey("requestAgent") ? options.getInt("requestAgent") : -1;
    int sequenceId = options.hasKey("sequenceId") ? options.getInt("sequenceId") : -1;


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
      .setProductCode(productCode)
      .setUdfField1(udfField1)
      .setUdfField2(udfField2)
      .setUdfField3(udfField3)
      .setUdfField4(udfField4)
      .setRequestAgent(requestAgent)
      .setSequenceId(sequenceId)
      .setMultiCartProductDetails(multiCartProductDetails);
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
    if(objPaymentParam.getCustomerId().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.CUSTOMER_ID, objPaymentParam.getCustomerId());
    }
    if(objPaymentParam.getCustomerAddress().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.CUSTOMER_ADDRESS, (objPaymentParam.getCustomerAddress()) );
    }
    if(objPaymentParam.getCustomerAddressPin().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.CUSTOMER_ADDRESS_PIN, objPaymentParam.getCustomerAddressPin());
    }
    if(objPaymentParam.getUdfField1().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.UDF_FIELD1, objPaymentParam.getUdfField1());
    }
    if(objPaymentParam.getUdfField2().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.UDF_FIELD2, objPaymentParam.getUdfField2());
    }
    if(objPaymentParam.getUdfField3().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.UDF_FIELD3, objPaymentParam.getUdfField3());
    }
    if(objPaymentParam.getUdfField4().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.UDF_FIELD4, objPaymentParam.getUdfField4());
    }
    if(objPaymentParam.getMultiCartProductDetails().length() > 0){
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.MULTICART_PRODUCT_DETAILS, objPaymentParam.getMultiCartProductDetails());
    }
    if(objPaymentParam.getRequestAgent() >= 0) {
    hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.REQUEST_AGENT, String.valueOf(objPaymentParam.getRequestAgent()));
    }
    if(objPaymentParam.getSequenceId() >= 0) {
      hmPaymentParam.put(PinePGConfig.PaymentParamsConstants.SEQUENCE_ID, String.valueOf(objPaymentParam.getSequenceId()));
    }
    return hmPaymentParam;
  }
}

