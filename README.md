<!-- [![Build Status](https://travis-ci.org/SolaceLabs/pcf-car-demo.svg?branch=master)](https://travis-ci.org/SolaceLabs/pcf-car-demo) -->


# Tracking the Movement and Maintenance of Connected Cars
 **An IoT Microservice demo using Solace Messaging and Spring Boot**

This demo simulates a connected vehicle tracking scenario a lot like something we’ve helped one of our customers deploy. 
In this case, we’ll be tracking car sensor data to show different kinds of maintenance events and faults, 
and can give individual users the ability to be “tracked” and manually log maintenance faults to see them reflected on the monitoring side of things.

## **Monitoring Map**

The enterprise end of this equation is a map of London that displays the location and condition of any number of 
computer- and user-generated cars. The demo simulates the tracking of their location, because the demo is generally 
delivered to groups of people sitting in a room, which would make for an uninteresting, static and cluttered map.

![](http://dev.solace.com/wp-content/uploads/2017/04/pivotal-car-markers-mocked-up-768x434.jpg)

## **Car Condition App**

The demo also includes a mobile app that people can use to place themselves, in the form of a car marked with their name, 
on the map among the computer-generated cars. The app gives users the ability to cause and repair four different faults: front flat tire, rear flat tire, engine trouble and busted taillight.  

The point is to track the health of each car by sending failures, and fixes, to our backend system, 
which captures the events and displays them on the Google map. When a user causes a fault their map marker 
turns red and indicates which of the four faults they generated, along with a “Fix it” button with which they can easily clear 
the fault. Each of these interactions represent events that flow between the car and and/or roadside emergencies.

![](http://dev.solace.com/wp-content/uploads/2017/04/smartphone-car-flat-rear-768x382.png)

## **Behind the Scenes**

This is the high-level architecture for the Hybrid-Cloud connected car scenario. Solace VMRs running
in two different cloud providers are configured as part of a single Multi-Node Routing group.

![](http://dev.solace.com/wp-content/uploads/2017/04/pcf-car-demo-architecture-768x407.png)


## **Getting Started**

```
git clone https://github.com/MichaelHussey/hybrid_cloud-car-demo
cd hybrid_cloud-car-demo
./gradlew installDist
```
This compiles the code, generates distributable jar files and associated start scripts. To execute a component on a different machine
simply copy the contents of ```component/install``` to that machine.

Start each of the 3 components individually. When starting on the same machine the two Spring containers should 
have a different HTTP port (otherwise they will collide on the default port). 

### VirtualCar

The VirtualCar component hosts the "Car Condition App" which you demo participants can use to 
register themselves and then "cause" and "fix" various faults in the car. 

This is a Spring Boot Application and the configuration parameters to the Solace VMR are read 
from each the ```cardemo/src/main/resources/application.properties``` file.
For a static setup edit this file before building, otherwise the values can be passed on the command line as shown below.

```
cd cardemo
./build/install/cardemo/bin/cardemo --server.port=8080 --solace.smfHost=192.168.56.111
```
Once this component is up and running the demo users can access the app at ```http://<address of server>:8080```

### GpsGenerator_POJO

This is a POJO Application which generates the location for the registered cars using pre-defined trajectories.
It also creates and moves some "computer-generated" vehicles. It takes the following parameters (default values in brackets)

```
cargen
	-smfhost: the message VPN to use (192.168.56.111)
	-vpn: the message VPN to use (default)
	-username: the client username (default)
	-password: the client password (default)
```

Start the car movement generator:

```
cd cargen
./build/install/cargen/bin/cargen -smfHost=192.168.56.110
```
This listens to new demo user registrations published by the VirtualCar and publishes a stream of location 
messages for each car.

### CarMapServer

The CarMapServer hosts a web page which shows a map with the location and status of all cars. It also shows some
simple aggregate status for all the virtual cars registered.

This is another Spring Boot Application and again the configuration parameters to the Solace VMR are read 
from each the ```carmap/src/main/resources/application.properties``` file.
For a static setup edit this file before building, otherwise the values can be passed on the command line as shown below.

```
cd carmap
./build/install/cardemo/bin/carmap --server.port=8081 --solace.smfHost=192.168.56.111
```
Once this component is up and running the "analytics front end" for the demo is available at ```http://<address of server>:8081```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

See the list of [contributors](https://github.com/MichaelHussey/hybrid_cloud-car-demo/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache License, Version 2.0. - See the [LICENSE](LICENSE) file for details.

## References

Here are some interesting links if you're new to these concepts:

* [The Solace Developer Portal](http://dev.solace.com/)
* [Solace Labs](http://dev.solace.com/labs/)
* [Martin Fowler on Microservices](http://martinfowler.com/articles/microservices.html)
* [A intro to Microservices Design Patterns](http://blog.arungupta.me/microservice-design-patterns/)
* [REST vs Messaging for Microservices](http://www.slideshare.net/ewolff/rest-vs-messaging-for-microservices)
