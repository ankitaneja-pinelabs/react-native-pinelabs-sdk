import UIKit
import PinePGSDK

@objc(PinelabsSdk)
class PinelabsSdk: UIViewController {
    static var onResponseCallback:RCTResponseSenderBlock?=nil;
    
    @objc static func requiresMainQueueSetup() -> Bool {return true}
    
    @objc(start:onResponse:)
    func start(options:NSDictionary ,onResponse:@escaping RCTResponseSenderBlock) -> Void
    {
        let vc = RCTPresentedViewController();
        let rootViewController = UIApplication.shared.delegate?.window??.rootViewController;
        let isProductionRequest = options["isProductionRequest"] != nil ? (options["isProductionRequest"] as! Int) : 0;
        let isHeaderToBeShow = options["isHeaderToBeShow"] == nil || options["isHeaderToBeShow"] as! Bool;
        let diaSecret = options["diaSecret"] as! String;
        let diaSecretType = options["diaSecretType"] as! String;
        let themeId = options["themeId"] != nil ? options["themeId"] as! Int : 1;
        PinelabsSdk.onResponseCallback = onResponse;
        
        let orderParam = createOrder(options: options);
        var pinePGPaymentParam=Dictionary<String,String>();
        pinePGPaymentParam = createPinePgPaymentParam(objPaymentParam: orderParam);
        pinePGPaymentParam[PaymentParamsConstants.DIA_SECRET] = diaSecret;
        pinePGPaymentParam[PaymentParamsConstants.DIA_SECRET_TYPE] = diaSecretType;
        DispatchQueue.main.async {
            PinePGPaymentManager.startPayment( pinePGPaymentParam: pinePGPaymentParam, Context: vc ?? rootViewController!, themeId: themeId, isHeaderShown: isHeaderToBeShow, Enviroment: isProductionRequest,pinePGResponseCallback: MerchantCallbackResponse())
        }
    }
    
    @objc(generateHash:secretKey:resolve:reject:)
    func generateHash(options:NSDictionary,
                      secretKey: NSString,
                      resolve:RCTPromiseResolveBlock,
                      reject:RCTPromiseRejectBlock)
    -> Void {
        let orderParam = createOrder(options: options);
        var pinePGPaymentParam=Dictionary<String,String>();
        pinePGPaymentParam = createPinePgPaymentParam(objPaymentParam: orderParam);
        let secureType:String="SHA256";
        let hash = Hashing_Algorithm.GenerateHash(requestMap: pinePGPaymentParam,strSecretKey: secretKey as! String,strHashType: secureType);
        resolve(hash);
    }

    func createOrder(options: NSDictionary) -> Order {
        let merchantTxnId = options["merchantTxnId"] as! String;
        let merchantId = options["merchantId"] as! Int;
        let productCode = options["productCode"] != nil ? options["productCode"] as! String : "";
        let amount = options["amount"] as! Int;
        let merchantAccessCode = options["merchantAccessCode"] as! String;
        let navigationMode = options["navigationMode"] as! Int;
        let transactionType = options["transactionType"] as! Int;
        let payModeOnLandingPage = options["payModeOnLandingPage"] as! String;
        let customerEmail = options["customerEmail"] as! String;
        let customerMobileNo = options["customerMobileNo"] as! String;
        let customerId = options["customerId"] as! String;
        let customerAddress = options["customerAddress"] as! String;
        let customerAddressPin = options["customerAddressPin"] as! String;
        let udfField1 = options["udfField1"] != nil ? options["udfField1"] as! String : "";
        let udfField2 = options["udfField2"] != nil ? options["udfField2"] as! String : "";
        let udfField3 = options["udfField3"] != nil ? options["udfField3"] as! String : "";
        let udfField4 = options["udfField4"] != nil ? options["udfField4"] as! String : "";
        let requestAgent = options["requestAgent"] != nil ? options["requestAgent"] as! Int : -1;
        let sequenceId = options["sequenceId"] != nil ? options["sequenceId"] as! Int : -1;

        
        let paymentParamBuilder = Order()
        paymentParamBuilder.setMerchantId(merchantId: merchantId)
            .setUniqueMerchantTxnId(uniqueMerchantTxnId: merchantTxnId)
            .setMerchantAccessCode(merchantAccessCode: merchantAccessCode)
            .setNavigationMode(navigationMode: navigationMode)
            .setAmountInPaise(amountInPaise: Int64(amount))
            .setTransactionType(transactionType: transactionType)
            .setPayModeOnLandingPage(payModeOnLandingPage: payModeOnLandingPage)
            .setSequenceId(sequenceId: sequenceId)
            .setProductCode(productCode: productCode)
            .setCustomerEmail(customerEmail: customerEmail)
            .setCustomerMobileNo(customerMobileNo: customerMobileNo)
            .setCustomerId(customerId: customerId)
            .setCustomerAddress(customerAddress: customerAddress)
            .setCustomerAddressPin(customerAddressPin: customerAddressPin)
            .setUdfField1(udfField1: udfField1)
            .setUdfField2(udfField2: udfField2)
            .setUdfField3(udfField3: udfField3)
            .setUdfField4(udfField4: udfField4)
            .setRequestAgent(requestAgent: requestAgent);
        return paymentParamBuilder
    }

    func createPinePgPaymentParam(objPaymentParam:Order) -> (Dictionary<String ,String>)
    {
        var hmPaymentParam=Dictionary<String,String>()
        hmPaymentParam[PaymentParamsConstants.UNIQUE_MERCHANT_TXN_ID]=objPaymentParam.getUniqueMerchantTxnId()
        hmPaymentParam[PaymentParamsConstants.MERCHANT_ID]=String(objPaymentParam.getMerchantId())
        hmPaymentParam[PaymentParamsConstants.AMOUNT]=String(objPaymentParam.getAmountInPaise())
        hmPaymentParam[PaymentParamsConstants.MERCHANT_ACCESS_CODE]=String(objPaymentParam.getMerchantAccessCode())
        hmPaymentParam[PaymentParamsConstants.NAVIGATION_MODE]=String(objPaymentParam.getNavigationMode())
        hmPaymentParam[PaymentParamsConstants.TRANSACTION_TYPE]=String(objPaymentParam.getTransactionType())
        if(objPaymentParam.getSequenceId() >= 0){
            hmPaymentParam[PaymentParamsConstants.SEQUENCE_ID]=String(objPaymentParam.getSequenceId())
        }

        if(objPaymentParam.getProductCode() != nil && objPaymentParam.getProductCode().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.PRODUCT_CODE]=objPaymentParam.getProductCode();
        }
        if(objPaymentParam.getCustomerEmail() != nil && objPaymentParam.getCustomerEmail().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.CUSTOMER_EMAIL]=objPaymentParam.getCustomerEmail()
        }
        if(objPaymentParam.getCustomerMobileNo() != nil && objPaymentParam.getCustomerMobileNo().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.CUSTOMER_MOBILE_NO]=objPaymentParam.getCustomerMobileNo()
        }
        if(objPaymentParam.getCustomerId() != nil && objPaymentParam.getCustomerId().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.CUSTOMER_ID]=objPaymentParam.getCustomerId()
        }
        if(objPaymentParam.getCustomerAddress() != nil && objPaymentParam.getCustomerAddress().count>0) {
            hmPaymentParam[PaymentParamsConstants.CUSTOMER_ADDRESS]=objPaymentParam.getCustomerAddress()
        }
        if(objPaymentParam.getCustomerAddressPin() != nil && objPaymentParam.getCustomerAddressPin().count>0) {
            hmPaymentParam[PaymentParamsConstants.CUSTOMER_ADDRESS_PIN]=objPaymentParam.getCustomerAddressPin()
        }
        if(objPaymentParam.getUdfField1() != nil && objPaymentParam.getUdfField1().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.UDF_FIELD1]=objPaymentParam.getUdfField1()
        }
        if(objPaymentParam.getUdfField2() != nil && objPaymentParam.getUdfField2().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.UDF_FIELD2]=objPaymentParam.getUdfField2()
        }
        if(objPaymentParam.getUdfField3() != nil && objPaymentParam.getUdfField3().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.UDF_FIELD3]=objPaymentParam.getUdfField3()
        }
        if(objPaymentParam.getUdfField4() != nil && objPaymentParam.getUdfField4().count>0)
        {
            hmPaymentParam[PaymentParamsConstants.UDF_FIELD1]=objPaymentParam.getUdfField4()
        }
        if(objPaymentParam.getRequestAgent() >= 0)
        {
            hmPaymentParam[PaymentParamsConstants.REQUEST_AGENT]=String(objPaymentParam.getRequestAgent())
        }
        hmPaymentParam[PaymentParamsConstants.PAYMENT_MODE_LOANDING_PAGE]=objPaymentParam.getPayModeOnLandingPage()
        
        return hmPaymentParam;
    }
}

public class MerchantCallbackResponse:UIViewController, IPinePGResponseCallback
{
    public func internetNotAvailable(code: Int, message: String)
    {
        Toas.default.show(text:message, duration: 4.0)
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            Toas.default.dismiss(animated: true);
        }
    }
    public func onErrorOccured(code: Int, message: String)
    {
        Toas.default.show(text: message, duration: 4.0)
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            Toas.default.dismiss(animated: true);
        }
    }
    public func onTransactionResponse()
    {
        PinelabsSdk.onResponseCallback!([])
    }
    public func onCancelTxn(code: Int, message: String)
    {
        Toas.default.show(text: message, duration: 4.0)
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            Toas.default.dismiss(animated: true);
        }
    }
    public func onPressedBackButton(code: Int, message: String)
    {
        Toas.default.show(text:message, duration: 4.0)
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            Toas.default.dismiss(animated: true);
        }
    }
}
