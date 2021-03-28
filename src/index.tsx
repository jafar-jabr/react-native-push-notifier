import { NativeModules } from 'react-native';

type PushNotifierType = {
  showActionPush(
    notificationData: Record<string, any>,
    soundFile: string | null
  ): null;
  showInfoPush(
    notificationData: Record<string, any>,
    notificationId: number,
    soundFile: string | null
  ): null;
  removeNotification(notificationId: number): null;
  clearNotifications(): null;
  runAlert(sound: string): null;
  isAppInForeground(): Promise<boolean>;
};

const { PushNotifier } = NativeModules;

export default PushNotifier as PushNotifierType;
