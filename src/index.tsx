import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-pinelabs-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const PinelabsSdk = NativeModules.PinelabsSdk
  ? NativeModules.PinelabsSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const { start, generateHash: nativeGenerateHash } = PinelabsSdk;

type optionsType = {
  merchantTxnId: string;
  merchantId: number;
  amount: number;
  merchantAccessCode: string;
  navigationMode: number;
  transactionType: number;
  payModeOnLandingPage: string;
  customerEmail: string;
  customerMobileNo: string;
  customerId: string;
  customerAddress: string;
  customerAddressPin: string;
  diaSecret: string;
  diaSecretType: string;
  productCode?: string;
  themeId?: number;
  isProductionRequest?: boolean;
  isHeaderToBeShow?: boolean;
};

type paramsType = {
  options: optionsType;
  onResponse: CallableFunction;
};

export const startPayment = (params: paramsType): void => {
  start(params.options, params.onResponse);
};

export const generateHash = (
  options: optionsType,
  secretKey: string
): string => {
  return nativeGenerateHash(options, secretKey);
};

export default { startPayment, generateHash };
