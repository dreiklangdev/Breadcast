# ![bread](https://png.icons8.com/metro/50/000000/bread.png) Breadcast
Broadcast Receiver Library for Android

> ... simplifies listening to broadcasts by hiding what would be boilerplate code.

- __One for All__: Single BroadcastReceiver instance for a performance/memory boost
- __Clean look__: Breadcast generates the code behind the curtains
- __Simple__: With possible broadcast listening in one statement
- __Thead-Modes__: Multithreading via annotation option

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
#### With Breadcast Standalone:
```java
new Breadcaster(context)
    .action(Intent.ACTION_SCREEN_OFF, (context, intent) -> ... )
    .action(Intent.ACTION_SCREEN_ON, (context, intent) -> ... )
    .register();
```

#### With Breadcast Annotation:
```java
Breadcast.init(context);
```
```java
class MyClass { // extends ...
    MyClass() {
        Breadcast.register(this);
    }
    
    @Receive(action = Intent.ACTION_SCREEN_OFF)
    onActionScreenOff() { ... }
    
    @Receive(action = Intent.ACTION_SCREEN_ON, threadMode = ThreadModus.MAIN)
    onActionScreenOn(Context context, Intent intent) { ... } // onUiThread
}
```

## Download
#### Gradle
```java
dependencies {
    // required
    implementation 'io.dreiklang:breadcast-base:1.0.1'
    
    // if you use the annotation
    implementation 'io.dreiklang:breadcast-annotation:1.0.1'
    annotationProcessor 'io.dreiklang:breadcast-processor:1.0.1'
}
```

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
