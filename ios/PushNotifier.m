#import "PushNotifier.h"
#import <React/RCTLog.h>
#import <UserNotifications/UserNotifications.h>
#import <React/RCTBridge.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTUtils.h>

NSString *const RCTRemoteNotificationReceived = @"RemoteNotificationReceived";

static NSString *const kLocalNotificationReceived = @"LocalNotificationReceived";
static NSString *const kRemoteNotificationsRegistered = @"RemoteNotificationsRegistered";
static NSString *const kRemoteNotificationRegistrationFailed = @"RemoteNotificationRegistrationFailed";

static NSString *const kErrorUnableToRequestPermissions = @"E_UNABLE_TO_REQUEST_PERMISSIONS";

@implementation PushNotifier

RCT_EXPORT_MODULE()

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
// and https://reactnative.dev/docs/native-modules-ios#synchronous-methods

RCT_REMAP_METHOD(showInfoPush, withBody:(nonnull NSDictionary *)notificationData notificationId:(nonnull NSNumber *)notificationId soundFile:(nonnull NSString *)soundFile)
{
    RCTLogInfo(@"called showInfoPush within within %@ at %@", notificationId, soundFile);

    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];

    UNNotificationAction *acceptAction = [UNNotificationAction actionWithIdentifier:@"accept"
                                                                              title:@"Accept" options:UNNotificationActionOptionForeground];
    UNNotificationAction *rejectAction = [UNNotificationAction actionWithIdentifier:@"reject"
                                                                              title:@"Reject" options:UNNotificationActionOptionDestructive];


    UNNotificationCategory *category = [UNNotificationCategory categoryWithIdentifier:@"showInfoPushCategory"
                                                                              actions:@[acceptAction,rejectAction] intentIdentifiers:@[]
                                                                              options:UNNotificationCategoryOptionNone];
    NSSet *categories = [NSSet setWithObject:category];

    [center setNotificationCategories:categories];

    UNMutableNotificationContent *content = [UNMutableNotificationContent new];
    content.title = [notificationData valueForKey:@"title"];
    content.body = [notificationData valueForKey:@"body"];
    content.categoryIdentifier = @"showInfoPushCategory";
    content.sound = [UNNotificationSound defaultSound];

    UNTimeIntervalNotificationTrigger *trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:7 repeats:NO];

    NSString *identifier = @"LocalNotification";
    UNNotificationRequest *request = [UNNotificationRequest requestWithIdentifier:identifier content:content trigger:trigger];

    [center addNotificationRequest:request withCompletionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            NSLog(@"Something went wrong: %@",error);
        }
    }];
}

RCT_REMAP_METHOD(showActionPush,
                 multiplyWithA:(nonnull NSNumber*)a withB:(nonnull NSNumber*)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  NSNumber *result = @([a floatValue] * [b floatValue]);

  resolve(result);
}

RCT_REMAP_METHOD(runAlert, audioName:(nonnull NSString*)name)
{
    NSLog(@"called with name: %@",name);
}

RCT_REMAP_METHOD(stopAlert, withNullString:(nullable NSString*)blank)
{
    NSLog(@"stope called with name:");
}

@end
