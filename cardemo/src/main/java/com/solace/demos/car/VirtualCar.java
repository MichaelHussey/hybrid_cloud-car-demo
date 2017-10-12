/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.solace.demos.car;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

@Configuration
@ComponentScan
@RestController
@EnableAutoConfiguration
public class VirtualCar implements ApplicationListener<ApplicationReadyEvent>{
	
	static private JCSMPSession session;

    //@RequestMapping("/")
    //String home() {
    //    return "Hello World!";
    //}

    @RequestMapping(value = "/car/new/{carId}/{driverName}", method = RequestMethod.POST)
    public ResponseEntity<?> newCar(
                                @PathVariable("carId") String carId,
                                @PathVariable("driverName") String driverName) throws JCSMPException {
		System.out.println("Entering car/new: " + carId + "/" + driverName);

        Topic topic = JCSMPFactory.onlyInstance().createTopic("car/new/" + carId + "/" + driverName);
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText("");
        prod.send(msg,topic);
        System.out.println("Successfully published message on topic: "+topic.toString());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @RequestMapping(value = "/car/fault/{carId}/{fault}", method = RequestMethod.POST)
    public ResponseEntity<?> faultCar(
                                @PathVariable("carId") String carId,
                                @PathVariable("fault") String fault) throws JCSMPException {
		System.out.println("Entering car/fault: " + carId + "/" + fault);

        Topic topic = JCSMPFactory.onlyInstance().createTopic("car/fault/" + carId + "/" + fault);
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText("");
        prod.send(msg,topic);
        System.out.println("Successfully published message on topic: "+topic.toString());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @RequestMapping(value = "/car/clear/{carId}", method = RequestMethod.POST)
    public ResponseEntity<?> clearFault(
                                @PathVariable("carId") String carId) throws JCSMPException {
		System.out.println("Entering car/clear: " + carId);

        Topic topic = JCSMPFactory.onlyInstance().createTopic("car/clear/" + carId);
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText("");
        prod.send(msg,topic);
        System.out.println("Successfully published message on topic: "+topic.toString());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @RequestMapping(value = "/car/remove/{carId}", method = RequestMethod.POST)
    public ResponseEntity<?> removeCar(
                                @PathVariable("carId") String carId) throws JCSMPException {
		System.out.println("Entering car/remove: " + carId);

        Topic topic = JCSMPFactory.onlyInstance().createTopic("car/remove/" + carId);
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText("");
        prod.send(msg,topic);
        System.out.println("Successfully published message on topic: "+topic.toString());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
    
    @Value("${solace.smfHost}")
    private String smfHost;
    @Value("${solace.vpnName}")
    private String msgVpnName;
    @Value("${solace.clientUsername}")
    private String clientUsername;
    @Value("${solace.clientPassword}")
    private String clientPassword;

  /**
   * This event is executed as late as conceivably possible to indicate that 
   * the application is ready to service requests.
   */
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {

		final JCSMPProperties properties = new JCSMPProperties();
		properties.setProperty(JCSMPProperties.HOST, smfHost);
		properties.setProperty(JCSMPProperties.VPN_NAME, msgVpnName);
		properties.setProperty(JCSMPProperties.USERNAME, clientUsername);
		properties.setProperty(JCSMPProperties.PASSWORD, clientPassword);

	try  {
        session = JCSMPFactory.onlyInstance().createSession(properties);
        session.connect();
        
        System.out.println("************* Solace initialized correctly!! ************");
        System.out.println("Vpn: " + msgVpnName);
        System.out.println("User: " + clientUsername);
        System.out.println("Host: " + smfHost);
    }
    catch (Exception e) {
    	e.printStackTrace();
    }
    return;
  }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(VirtualCar.class, args);
    }
}
