import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import {
  startPayment,
  generateHash,
  paymentOptions,
  hashOptions,
} from 'react-native-pinelabs-sdk';

export default function App() {
  const handlePress = async () => {
    const hashOptions: hashOptions = {
      merchantId: XXXXX, //merchant ID
      merchantTxnId: `reactTestApp${Math.round(Math.random() * 10000000)}`,
      merchantAccessCode: 'XXXXXX-XXX', //Access code
      amount: 10000,
      navigationMode: 2,
      transactionType: 1,
      payModeOnLandingPage: '1',
      customerEmail: abc@gmail.com, // email
      customerMobileNo: '12345XXXX', // mobile no.
      customerId: '786',
      customerAddress: 'hno 15',
      customerAddressPin: '201301',
      isProductionRequest: false,
      isHeaderToBeShow: true,
      themeId: 2,
      productCode: '40',
    };

    const hash = await generateHash(
      hashOptions,
      'XXXXXXXXXX'
    ); // secret key and hashOptions

    const paymentOptions: paymentOptions = {
      ...hashOptions,
      diaSecret: hash,
      diaSecretType: 'SHA256',
    };

    startPayment({
      options: paymentOptions,
      onResponse: () => {
        console.log('response received');
      },
    });
  };

  return (
    <View style={styles.container}>
      <Button title="Buy now !!" onPress={handlePress} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
