#import <AVFoundation/AVFoundation.h>
#import "RingtonePlayer.h"

@implementation RingtonePlayer

static RingtonePlayer *_shared = nil;
AVAudioPlayer *audioPlayer;
+ (RingtonePlayer*)shared {
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    _shared = [[self alloc] init];
  });
  return _shared;
}
-(void)playAlert:(NSString*)name {
  NSString *audioPath = [[NSBundle mainBundle] pathForResource: name ofType:@"mp3"];
  NSLog(@"%@", audioPath);
  NSURL *audioURL = [NSURL fileURLWithPath: audioPath];
  NSError *audioError = nil;
  audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:audioURL error:&audioError];
  if (!audioError) {
    NSLog(@"playing!");
    [audioPlayer play];
  } else {
    NSLog(@"Error!");
  }
}
-(void)stopAlert {
  [audioPlayer stop];
}
@end
