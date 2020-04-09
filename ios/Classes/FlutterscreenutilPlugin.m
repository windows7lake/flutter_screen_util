#import "FlutterScreenUtilPlugin.h"
#if __has_include(<flutterscreenutil/flutterscreenutil-Swift.h>)
#import <flutterscreenutil/flutterscreenutil-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutterscreenutil-Swift.h"
#endif

@implementation FlutterScreenUtilPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterScreenUtilPlugin registerWithRegistrar:registrar];
}
@end
