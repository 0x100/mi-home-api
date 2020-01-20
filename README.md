<img src="https://user-images.githubusercontent.com/1588120/72687893-7285a500-3b13-11ea-832c-ef6bb43f3279.png" width="64px" height="64px"/>

###

REST and Java API for interacting with Xiaomi Smart Home devices.

## Capabilities
Devices supported by the API which connect to a gateway through Zigbee:
* Xiaomi Door and Window Sensor
* Xiaomi Button
* Xiaomi Plug (Socket)
* Xiaomi Magic Cube
* Xiaomi Motion Sensor

Built-in into the gateway:
* Xiaomi Gateway Light
* Xiaomi Gateway Illumination Sensor

## Build and run
```
cd api
mvnw spring-boot:run
```
#### Requirements
- JDK 11. Check the compiler version with `javac -version` or `mvnw -version`.
- Maven 3.2+ (or use the Maven wrapper `mvnw` supplied with the project).

## Docker
Docker file location: 
```
/api/Dockerfile
```

#### Build image 
```
cd api
mvnw dockerfile:build
```

#### Run 
```
docker run com/mihome-api:latest
```

## Examples
See examples of using the Java API in the [samples](https://github.com/0x100/mi-home-api/tree/master/samples/src/main/java/com/mihome/api/samples) module.

## Rest API
After run the application you can find the Rest API at `http://localhost:8080/v1/`.
Now it contains methods:

- GET `/devices` - list known devices.
- GET `/devices/type/{type}` - list known devices of the specified type. The `type` variable can take values:
```
XIAOMI_CUBE
XIAOMI_DOOR_WINDOW_SENSOR
XIAOMI_SOCKET
XIAOMI_MOTION_SENSOR
XIAOMI_SWITCH_BUTTON
```
- POST `/devices/subscribe` - subscribe on events from a specified device.
Request params example:
```json
{
  "deviceSid" : "a22b4b5b6c7cc0d",
  "webHookUrl" : "http://localhost:8081/callback"
}
```
`deviceSid` param takes a unique SID of the device to subscribe (you can get it from the methods above).

`webHookUrl` param takes the URI which will called on firing any event at the subscribed device.
That URI must take following parameters:
```json
{
  "deviceSid" : "Device SID",
  "action": "Name of the fired event (action)"
}
```

##
Thanks to [FreePik](https://www.freepik.com/) for the logo.
