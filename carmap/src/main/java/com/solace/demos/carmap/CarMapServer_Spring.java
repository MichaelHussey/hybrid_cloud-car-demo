package com.solace.demos.carmap;
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

import org.springframework.beans.factory.annotation.Value;

//package com.solace.demos.cloudfoundry.scaling.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solacesystems.jcsmp.Browser;
import com.solacesystems.jcsmp.BrowserProperties;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.Topic;

@Configuration
//@ComponentScan
@RestController
@EnableAutoConfiguration
public class CarMapServer_Spring implements ApplicationListener<ApplicationReadyEvent>{

	static private JCSMPSession session;
    static private Queue listenQueue;
    static private Browser myBrowser;

    @RequestMapping(value = "/cars", method = RequestMethod.GET)
    String getCars() throws JCSMPException {
		System.out.println("Entering getCars");

        BytesXMLMessage rx_msg = null;
        String response = "[\n";
        int i=0;

        while (i < 50 && (rx_msg = myBrowser.getNext()) != null) {
            System.out.println("Browser got message... dumping:");
            System.out.println(rx_msg.dump());

            String body = "";
            if (rx_msg.hasAttachment()) {
                byte[] attachment = new byte[rx_msg.getAttachmentContentLength()];
                rx_msg.readAttachmentBytes(attachment);
                body = new String(attachment);
            }
            System.out.println("Body: " + body);
            if (i > 0)
                response += ",\n";
            response += body;

            System.out.print("Removing message from browser...");
            myBrowser.remove(rx_msg);
            System.out.println("Message removed");

            i++;
        }
        response += "\n]";
        System.out.println("Finished browsing.");

        return response;
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
          
          // Now create a queue and subscribe it to car updates
          EndpointProperties provision = new EndpointProperties();
          provision.setPermission(EndpointProperties.PERMISSION_DELETE);
          provision.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
          provision.setQuota(100);

          listenQueue = JCSMPFactory.onlyInstance().createQueue("cars");
          session.provision(listenQueue, provision, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

          Topic topic = JCSMPFactory.onlyInstance().createTopic("geo/coord/>");
          try { 
              // subscribe to "geo/coord/%s" 
              session.addSubscription(listenQueue, topic, JCSMPSession.WAIT_FOR_CONFIRM);
          } catch (Exception e) {}

          BrowserProperties br_prop = new BrowserProperties();
          br_prop.setEndpoint(listenQueue);
          br_prop.setTransportWindowSize(50);
          br_prop.setWaitTimeout(150);
          myBrowser = session.createBrowser(br_prop);

          System.out.println("************* Solace initialized correctly!! ************");
      }
      catch (Exception e) {
      	e.printStackTrace();
      }
      return;
    }
    
    public static void main(String[] args) throws Exception {


        SpringApplication.run(CarMapServer_Spring.class, args);
    }
}
