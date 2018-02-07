 ###### Base: [ ![Download](https://api.bintray.com/packages/dreiklang/Breadcast/breadcast-base/images/download.svg) ](https://bintray.com/dreiklang/Breadcast/breadcast-base/_latestVersion) Processor: [ ![Download](https://api.bintray.com/packages/dreiklang/Breadcast/breadcast-processor/images/download.svg) ](https://bintray.com/dreiklang/Breadcast/breadcast-processor/_latestVersion) Annotation: [ ![Download](https://api.bintray.com/packages/dreiklang/Breadcast/breadcast-annotation/images/download.svg) ](https://bintray.com/dreiklang/Breadcast/breadcast-annotation/_latestVersion)

# ![bread](https://png.icons8.com/metro/50/000000/bread.png) Breadcast
Small Broadcast Receiver Library for Android

> ... simplifies listening to broadcasts by hiding what would be boilerplate code.

- __One for All__: Single BroadcastReceiver instance for every action
- __Clean look__: Generates the code behind the curtains
- __Thread-Modes__: Multithreaded callback via annotation option
- __Simple__: Broadcast listening possible in one statement

#### Before:
```java
class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                ...
            case Intent.ACTION_SCREEN_ON:
                ...
        }
    }
}
```
```java
IntentFilter filter = new IntentFilter();
filter.addAction(Intent.ACTION_SCREEN_OFF);
filter.addAction(Intent.ACTION_SCREEN_ON);
context.registerBroadcast(new MyReceiver(), filter);
```

### With Breadcast you can choose between 2 ways:

### 1. Breadcast Standalone (Base package)
```java
new Breadcaster(context)
    .action(Intent.ACTION_SCREEN_OFF, (context, intent) -> ... )
    .action(Intent.ACTION_SCREEN_ON, (context, intent) -> ... )
    .register();
```

### 2. Breadcast Annotation
```java
Breadcast.init(context);
```
```java
class MyClass { // extends ...
    MyClass() {
        Breadcast.instance().register(this); // required for non-static methods
    }
    
    @Receive(action = Intent.ACTION_SCREEN_OFF)
    void onScreenOff() { ... }
    
    @Receive(action = Intent.ACTION_SCREEN_ON)
    void onScreenOn() { ... }
}
```

#### More examples
```java
@Receive(action = {Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF})
onScreenChange(Context context, Intent intent) { ... } // multiple
	
@Receive(action = "custom1", threadMode = ThreadModus.ASYNC)
onCustomAsync() { ... } // asynchronous

@Receive(action = Intent.ACTION_SHUTDOWN)
static withoutRegister() { ... } // static - called regardless of registration	
```

####  To register Breadcast via manifest (implicit/static), use _ManifestBreadcast_
```xml
<receiver android:name="io.dreiklang.breadcast.base.statics.ManifestBreadcast">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```
```
@Receive(action = Intent.ACTION_BOOT_COMPLETED)
static onBoot() { ... } // must be static
```
## Notes
- Always init() Breadcast before usage (e.g. in Application class)
- Standalone: Remember to call release() if not needed anymore (memory leaks)
- Watch out for Android O's [implicit broadcast exceptions](https://developer.android.com/guide/components/broadcast-exceptions.html)

## Download
#### Gradle
```java
dependencies {
    // required
    implementation 'io.dreiklang:breadcast-base:1.1.0'
    
    // if you use the annotation
    implementation 'io.dreiklang:breadcast-annotation:1.1.0'
    annotationProcessor 'io.dreiklang:breadcast-processor:1.1.0'
}
```

Please open an issue case if you find one. It's merely 1-2 minutes against literally tons of neurons roasted for debunking them. :)

__Thank you and have fun!__

License
-------

    Copyright 2018 Nhu Huy Le

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
    
Logo icon by [Icons8](https://icons8.com)
