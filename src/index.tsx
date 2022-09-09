import { NativeModules } from 'react-native';

type PushNotifierType = {
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
  audioPermission(): Promise<string>;
  showIncomingCall(notificationData: Record<string, any>): Promise<string>;
  callPermissions(): Promise<any>;
};

const { PushNotifier } = NativeModules;

export default PushNotifier as PushNotifierType;
