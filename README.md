# XAAPI
Java API for interacting with Aqara-compatible devices.

The ones which connect to a gateway through Zigbee:
* Xiaomi Door and Window Sensor
* Xiaomi Button
* Xiaomi Plug (Socket)
* Xiaomi Magic Cube
* Xiaomi Motion Sensor

Built-in into the gateway:
* Xiaomi Gateway Light
* Xiaomi Gateway Illumination Sensor

## Examples

#### Discover
```java
XiaomiGateway gateway = XiaomiGateway.discover(); // works only for single gateway at the moment
```

#### Bind manually
```java
XiaomiGateway gateway = new XiaomiGateway("192.168.1.105"); // works for multiple gateways
```

#### Subscribe and receive updates
```java
IInteractiveDevice.SubscriptionToken illuminationChangeSubscriptionToken =
    gateway.getBuiltinIlluminationSensor().subscribeForIlluminationChange((Integer value) -> System.out.println("Illumination changed to: " + value));

String buttonSid = "158d0001232e95";
IInteractiveDevice.SubscriptionToken buttonActionsSubscriptionToken =
    gateway.getDevice(buttonSid).asXiaomiSwitchButton().subscribeForActions((String action) -> System.out.println("Button: " + action));

ExecutorService threadPool = Executors.newFixedThreadPool(1);
gateway.startReceivingUpdates(threadPool);
threadPool.awaitTermination(60, TimeUnit.SECONDS); // run for 60 seconds
```

#### Unsubscribe
```java
// cancelling subscription is optional
gateway.getDevice(buttonSid).asXiaomiSwitchButton().unsubscribeForActions(buttonActionsSubscriptionToken);
```

## Docker
Docker file - projectDir/Dockerfile
Build image 
```mvn dockerfile:build```

Run 
```docker run com/mihome-api:latest```

