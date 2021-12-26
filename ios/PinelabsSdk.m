#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTUtils.h>

@interface RCT_EXTERN_MODULE(PinelabsSdk, NSObject)

RCT_EXTERN_METHOD(start:(NSDictionary)options
                  onResponse:(RCTResponseSenderBlock)onResponse)

RCT_EXTERN_METHOD(generateHash:(NSDictionary)options
                  secretKey:(NSString)secretKey
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

@end
