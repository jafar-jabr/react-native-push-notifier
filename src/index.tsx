import { NativeModules } from 'react-native';

type PushNotifierType = {
  showActionPush(
    notificationData: Record<string, any>,
    soundFile: string | null
  ): void;
  showInfoPush(
    notificationData: Record<string, any>,
    notificationId: number,
    soundFile: string | null
  ): void;
  removeNotification(notificationId: number): void;
  clearNotifications(): void;
  runAlert(sound: string): void;
  stopAlert(s: string): void;
  isAppInForeground(): Promise<boolean>;
  userTrackAlert(): Promise<string>;
};

const { PushNotifier } = NativeModules;

export default PushNotifier as PushNotifierType;
