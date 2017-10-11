/**
 * 
 */
package com.solace.demos.cargen;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author michussey
 *
 */
public class SolaceMessagingInfoTest {

	@Test
	public void testDefaultParameters() {
		String[] args = {};
		
		SolaceMessagingInfo smi = SolaceMessagingInfo.getInstance();
		try {
			smi.parseArgs(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// all defaults
		assertEquals(smi.getSmfHost(),"192.168.56.101");
		assertEquals(smi.getClientUsername(),"client01");
		assertEquals(smi.getClientPassword(),"client01");
		assertEquals(smi.getMsgVpnName(),"demo_vpn");
		
	}

	@Test
	public void testSetParameters() {
		String[] args = {"-username=test1", "-password=pass1", "-smfhost=127.0.0.1", "-vpn=avpn"};
		
		SolaceMessagingInfo smi = SolaceMessagingInfo.getInstance();
		try {
			smi.parseArgs(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// all defaults
		assertEquals(smi.getSmfHost(),"127.0.0.1");
		assertEquals(smi.getClientUsername(),"test1");
		assertEquals(smi.getClientPassword(),"pass1");
		assertEquals(smi.getMsgVpnName(),"avpn");
		
	}

	@Test
	public void testBadParameters() {
		String[] args = {"-unknown=xxx"};
		
		SolaceMessagingInfo smi = SolaceMessagingInfo.getInstance();
		try {
			smi.parseArgs(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assertNotNull(e);
		}		
	}

}
