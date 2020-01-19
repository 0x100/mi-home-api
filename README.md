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
See the [samples](https://github.com/0x100/mi-home-api/tree/master/samples/src/main/java/com/mihome/api/samples) module.

##
Thanks to [FreePik](https://www.freepik.com/) for the logo.
