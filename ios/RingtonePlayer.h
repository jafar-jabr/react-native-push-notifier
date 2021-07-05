//
//  RingtonePlayer.h
//  AloDoc
//
//  Created by Jafar Jabr on 05.07.2021.
//

#ifndef RingtonePlayer_h
#define RingtonePlayer_h


#endif /* RingtonePlayer_h */

@interface RingtonePlayer : NSObject
+ (RingtonePlayer*)shared;
- (void)playAlert:(NSString*)name;
- (void)stopAlert;
@end
