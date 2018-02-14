## receipts-native

This is a branch of my receipts-native learning project, simplified to highlight a problem.

It seems that Figwheel crashes when a macro expansion calls `js/require` on a module that has not yet been loaded.
(If the same module had been previously loaded, from a .cljs file, then the call works happily).

The steps to duplicate the problem are:

- Start a Genymotion Android emulator
- Run `lein figwheel` in one terminal
- Run `exp start --android` in a second terminal

The crash log is:

```
[exp] Your URL is: exp://192.168.86.61:19000
[exp] Opening on Android device
[exp] Logs for your project will appear below. Press Ctrl+C to exit.
[exp] Dependency graph loaded.
[exp] Finished building JavaScript bundle in 2567ms.
[exp] Running application "main" with appParams: {"initialProps":{"exp":{"initialUri":"exp://192.168.86.61:19000","manifest":{"iconUrl":"http://192.168.86.61:19001/assets/./assets/icons/app.png","debuggerHost":"192.168.86.61:19001","env":{},"icon":"./assets/icons/app.png","logUrl":"http://192.168.86.61:19000/logs","privacy":"public","primaryColor":"#cccccc","orientation":"portrait","description":"No description","bundleUrl":"http://192.168.86.61:19001/main.bundle?platform=android&dev=true&minify=false&hot=false&assetPlugin=/home/deg/Documents/git/projects/receipts-native/node_modules/expo/tools/hashAssetFiles","ios":{"supportsTablet":true},"notification":{"iconUrl":"http://192.168.86.61:19001/assets/./assets/icons/loading.png","color":"#000000","icon":"./assets/icons/loading.png"},"packagerOpts":{"dev":true,"lanType":"ip","hostType":"lan","minify":false,"urlRandomness":"uq-udx"},"id":"@degeldeg/receipts-native","xde":true,"developer":{"tool":"exp","projectRoot":"/home/deg/Documents/git/projects/receipts-native"},"name":"receipts-native","slug":"receipts-native","mainModuleName":"main","sdkVersion":"22.0.0","loading":{"iconUrl":"https://s3.amazonaws.com/exp-brand-assets/ExponentEmptyManifest_192.png","hideExponentText":false,"icon":"https://s3.amazonaws.com/exp-brand-assets/ExponentEmptyManifest_192.png"},"version":"1.0.0","isVerified":true},"shell":false}},"rootTag":1}. __DEV__ === true, development-level warning are ON, performance optimizations are OFF
[exp] Loading Closure base.
[exp] Shimming require
[exp] Shimming goog functions.
[exp] Requiring: react
[exp] Requiring: create-react-class
[exp] Requiring: react-native
[exp] Requiring: native-base
[exp] Finished building JavaScript bundle in 342ms.
[exp] Figwheel: trying to open cljs reload socket
[exp] Figwheel: socket connection established

[exp] undefined is not an object (evaluating 'receipts_native.core.init.call')
* js/figwheel-bridge.js:117:14 in <unknown>
- node_modules/promise/setimmediate/core.js:37:14 in tryCallOne
- node_modules/promise/setimmediate/core.js:123:25 in <unknown>
- ... 10 more stack frames from framework internals

[exp] Unknown named module: 'native-base'
- node_modules/metro-bundler/src/Resolver/polyfills/require.js:96:12 in _require
* js/figwheel-bridge.js:162:26 in require
* js/figwheel-bridge.js:117:14 in <unknown>
- node_modules/promise/setimmediate/core.js:37:14 in tryCallOne
- node_modules/promise/setimmediate/core.js:123:25 in <unknown>
- ... 10 more stack frames from framework internals

[exp] Figwheel: notified of file changes
[exp] Requiring: native-base
[exp] Loading! http://192.168.56.1:19001/target/expo/goog/../receipts_native/core.js
[exp] Figwheel: NOT loading these files 
[exp] figwheel-no-load meta-data: ("../env/expo/main.js")
[exp] not required: ("target/expo/receipts_native/core.js")
[exp] Finished building JavaScript bundle in 260ms.

[exp] Figwheel: Error loading file http://192.168.56.1:19001/target/expo/goog/../receipts_native/core.js
* js/figwheel-bridge.js:234:28 in <unknown>
* js/figwheel-bridge.js:124:12 in <unknown>
- node_modules/promise/setimmediate/core.js:37:14 in tryCallOne
- node_modules/promise/setimmediate/core.js:123:25 in <unknown>
- ... 10 more stack frames from framework internals

[exp] Unknown named module: 'native-base'
- node_modules/metro-bundler/src/Resolver/polyfills/require.js:96:12 in _require
* js/figwheel-bridge.js:162:26 in require
* js/figwheel-bridge.js:117:14 in <unknown>
- node_modules/promise/setimmediate/core.js:37:14 in tryCallOne
- node_modules/promise/setimmediate/core.js:123:25 in <unknown>
- ... 10 more stack frames from framework internals
```

This is a re-working of my Receipts app in React Native.
So far, just the GUI, and not much of that. But, I'll slowly add to it as I have time.

See [React Native in
ClojureScript](https://github.com/deg/clojure-then-you-think/wiki/React-Native-in-ClojureScript)
for notes I'm writing as I learn this platform.


## Template notes

### Usage

#### Install Expo [XDE and mobile client](https://docs.expo.io/versions/v15.0.0/introduction/installation.html)
    If you don't want to use XDE (not IDE, it stands for Expo Development Tools), you can use [exp CLI](https://docs.expo.io/versions/v15.0.0/guides/exp-cli.html).

``` shell
    yarn global add exp
```

#### Install [Lein](http://leiningen.org/#install) or [Boot](https://github.com/boot-clj/boot)

#### Install npm modules

``` shell
    yarn install
```

#### Signup using exp CLI

``` shell
    exp signup
```

#### Start the figwheel server and cljs repl

##### leiningen users
``` shell
    lein figwheel
```

##### boot users
``` shell
    boot dev

    ;; then input (cljs-repl) in the connected clojure repl to connect to boot cljs repl
```

#### Start Exponent server (Using `exp`)

##### Also connect to Android device

``` shell
    exp start -a --lan
```

##### Also connect to iOS Simulator

``` shell
    exp start -i --lan
```

### Add new assets or external modules
1. `require` module:

``` clj
    (def cljs-logo (js/require "./assets/images/cljs.png"))
    (def FontAwesome (js/require "@expo/vector-icons/FontAwesome"))
```
2. Reload simulator or device

### Make sure you disable live reload from the Developer Menu, also turn off Hot Module Reload.
Since Figwheel already does those.

### Production build (generates js/externs.js and main.js)

#### leiningen users
``` shell
lein prod-build
```

#### boot users
``` shell
boot prod
```
