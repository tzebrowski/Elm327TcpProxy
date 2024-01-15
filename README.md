# ELM327 TCP Proxy

![CI](https://github.com/tzebrowski/Elm327TcpProxy/workflows/Build/badge.svg?branch=main)

## About

`ELM327 TCP Proxy` is a simple TCP server which might work in two distinct modes: `EchoServer` and `ProxyServer`. 
Server is intended to increase quality of the connection between Wifi ELM327 Obd2 adapters and applications dedicated for vehicle diagnosis which use Wifi connections.
Application allows to bypass some of the commands which are not supported by the adapter and ensure connection is not dropped. 

### Features

* Overriding CAN/AT commands
* Dumping communication between Elm327 adapter and Application to the log file.
* Acting as mock-server


### Building and running

Build command

```
	mvn clean install
```


Run command

```
	java -jar target/elm327-tcp-proxy-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```


### Configuration

Server during startup is looking for configuration within execution dir. File should be named `config.yaml` and contains configuration like bellow. If file does not exists in the specified location, default one is loaded.

#### Echo Server

Example config for Echo Server

```
server:
   port: 5555

serverType: ECHO
   
adapter:
   port: 35000
   ip: "192.168.0.10"
    
overrides:
   
   - 
      key: ATZ 
      value: ELM327 v1.4b
   
   -
      key: AT@1
      value: OBD SOLUTIONS LLC
      
   - 
      key: 3E00 
      value: 7E00
   - 
      key: 3 
      value: 7E00
   - 
      key: E00 
      value: 7E00
```