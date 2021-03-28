import { NativeModules } from 'react-native';

type PushNotifierType = {
  multiply(a: number, b: number): Promise<number>;
};

const { PushNotifier } = NativeModules;

export default PushNotifier as PushNotifierType;
