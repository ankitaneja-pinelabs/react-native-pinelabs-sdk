##### React native wrapper for the PinePGSDK offered by Pinelabs.

### Installation

##### 1) Install react-native-pinelabs-sdk from github:

---

```sh
npm install git+https://github.com/ankitaneja-pinelabs/react-native-pinelabs-sdk.git --save
```

### For Android

##### 2) Open AndroidManifest.xml from the android folder and add the following lines:

---

- **_xmlns:tools="http://schemas.android.com/tools"_** in the manifest tag
- **_tools:replace="label,theme"_** in the application tag

```sh
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools" // add this line here
  package="com.example.reactnativepinelabssdk">
    ...
    <application
      android:name=".MainApplication"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:roundIcon="@mipmap/ic_launcher_round"
      android:allowBackup="false"
      tools:replace="label,theme" // add this line here
      android:theme="@style/AppTheme">
      ...
    </application>
    ...
</manifest>
```

### For iOS

##### 3) Open podfile from IOS folder and add the following line in your app target:

---

- **_pod "PinePGSDK", git: "https://github.com/ankitaneja-pinelabs/PinePGSDK.git"_**

```sh
target 'PinelabsSdkExample' do
  ...
  pod "PinePGSDK", git: "https://github.com/ankitaneja-pinelabs/PinePGSDK.git"
  ...
end
```

##### 4) Navigate to IOS folder of your project and install pods

---

```sh
cd ios && pod install
```

##### Additional step for React Native version < 0.60

---

```sh
npx react-native link react-native-pinelabs-sdk
```

#### Methods

##### 1) startPayment

---

###### Requires an object as parameter with the following keys:

Return type: Void

#

| Key              | Type     | Required | Description                                     |
| ---------------- | -------- | -------- | ----------------------------------------------- |
| **_options_**    | object   | yes      | Contains order details data.                    |
| **_onResponse_** | function | yes      | Callback when any type of response is received. |

###### Params for options object in the startPayment and generateHash method


| Key                        | Type    | Required | Description                                                                                                                                                           |
| -------------------------- | ------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **_merchantTxnId_**        | string  | yes      | It is the transaction Id generated at merchant side, for merchant transaction tracking. It is required only for PreAuth and Purchase transactions.                    |
| **_merchantId_**           | string  | yes      | You can find it in your (merchant) registration data. It is the merchant Id issued by Pine Labs                                                                       |
| **_amount_**               | number  | yes      | It is the amount for which payment transaction is required. Greater than zero, in the least currency denominator (e.g. for INR amount is in Paise )                   |
| **_merchantAccessCode_**   | string  | yes      | You can find it in your (merchant) Registration data                                                                                                                  |
| **_navigationMode_**       | number  | yes      | Integration mode - 2 (Redirect) or 7 (Seamless)                                                                                                                       |
| **_transactionType_**      | number  | yes      | 1 (Purchase), 8 (PreAuth), 3 (Inquiry), 9 (Capture), 10 (Refund)                                                                                                      |
| **_payModeOnLandingPage_** | string  | yes      | It will contain csv of valid payment mode Ids.                                                                                                                        |
| **_customerEmail_**        | string  | yes      | Email address of customer.                                                                                                                                            |
| **_customerMobileNo_**     | string  | yes      | Mobile number of customer.                                                                                                                                            |
| **_customerId_**           | string  | yes      | In case of Saved Card/Express Checkout, this is used for getting saved cards.                                                                                         |
| **_customerAddress_**      | string  | yes      | Address of customer                                                                                                                                                   |
| **_customerAddressPin_**   | string  | yes      | Postal code of customer                                                                                                                                               |
| **_diaSecret_**            | string  | yes      | Hash of request parameters. Please refer to HashGeneration section of this [LINK](https://developer.pinelabs.com/payment-gateway/docs) for hash generation algorithm. |
| **_diaSecretType_**        | string  | yes      | Use SHA256 or MD5 as its Value                                                                                                                                        |
| **_productCode_**          | string  | no       | It is merchant product code. It is required for brand EMI transaction.                                                                                                |
| **_themeId_**              | number  | no       | A Integer variable(possible value 0,1,2) to apply theme on Pine labs SDK.                                                                                             |
| **_isProductionRequest_**  | boolean | no       | A boolean variable (true/false) to determine whether request is for production environment.                                                                           |
| **_isHeaderToBeShow_**     | boolean | no       | A Boolean variable (true/false) to hide or show header bar. **_Note:_** On Android, works only if action bar is present.                                              |

###### Usage:


```sh
    const options = {
      merchantId: 123456,
      merchantTxnId: 'uniqueTxnId',
      merchantAccessCode: 'merchant-access-code',
      amount: 10000,
      navigationMode: 2,
      transactionType: 1,
      payModeOnLandingPage: '1',
      customerEmail: 'customer@email.com',
      customerMobileNo: '9876543210',
      customerId: '123',
      customerAddress: 'Address',
      customerAddressPin: '1234',
      diaSecret: 'generatedDiaSecret',
      diaSecretType: 'SHA256',
      isProductionRequest: false,
      isHeaderToBeShow: true,
      themeId: 1,
      productCode: '40',
    };

    const onResponse = () => {
        // handle response here
    }

    pinelabsSdk.startPayment( { options, onResponse } )
```

##### 2) generateHash (Should be used for testing purpose only)

---

###### Generates the SHA256 diaSecret with the following parameters:

Return type: Promise containing the generated diaSecret for the payload.


| Parameter | Type   | Required | Description                                                                                              |
| --------- | ------ | -------- | -------------------------------------------------------------------------------------------------------- |
| options   | object | yes      | Contains order details data.                                                                             |
| secretKey | string | yes      | You can find it in your (merchant) registration data. It is the merchant secret key issued by Pine Labs. |

###### Usage:


```sh
    const diaSecret = await generateHash( options, 'secretKey' );
```

##### For more details about the Payment Gateway API please follow this [LINK](https://developer.pinelabs.com/payment-gateway/docs)
