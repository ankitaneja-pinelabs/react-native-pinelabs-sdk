import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import { startPayment, generateHash } from 'react-native-pinelabs-sdk';

export default function App() {
  const handlePress = async () => {
    const options = {
      merchantId: 106600,
      merchantTxnId: `reactTestApp${Math.round(Math.random() * 10000000)}`,
      merchantAccessCode: 'bcf441be-411b-46a1-aa88-c6e852a7d68c',
      amount: 10000,
      navigationMode: 2,
      transactionType: 1,
      payModeOnLandingPage: '1',
      customerEmail: 'harsh.kumar01@pinelabs.com',
      customerMobileNo: '9582492891',
      customerId: '786',
      customerAddress: 'hno 15',
      customerAddressPin: '201301',
      diaSecret:
        'FD39611A4498F67C3E49CE5FEC6083638C39AD6BC8C287F829E803AF89C6F761',
      diaSecretType: 'SHA256',
      isProductionRequest: false,
      isHeaderToBeShow: true,
      themeId: 2,
      productCode: '40',
    };
    const hash = await generateHash(
      options,
      '9A7282D0556544C59AFE8EC92F5C85F6'
    );
    startPayment({
      options: { ...options, diaSecret: hash },
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
