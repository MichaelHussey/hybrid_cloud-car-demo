package com.solace.demos.carmap;

import java.util.regex.Pattern;

public class SolaceMessagingInfo {

	public static SolaceMessagingInfo getInstance() {
		return new SolaceMessagingInfo();
	}

	private String msgVpnName = "demo_vpn";
	private String clientUsername = "client02";
	private String clientPassword = "client02";
	private String smfHost = "192.168.56.99";

	public String getMsgVpnName() {
		return msgVpnName;
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public String getSmfHost() {
		return smfHost;
	}

	public void parseArgs(String[] args) throws Exception {
		
		if (args.length == 0)
			return;
		
		for (String arg : args) {
			// Only split if param matches the "-param=value" regexp.
			if (Pattern.matches("-[\\w]+=.*", arg)) {
				String[] tokens = arg.split("=", 2);
				if (tokens[0].equals("-vpn"))
				{
					msgVpnName  = tokens[1];
				}
				else if (tokens[0].equals("-username"))
				{
					clientUsername  = tokens[1];
				}
				else if (tokens[0].equals("-password"))
				{
					clientPassword  = tokens[1];
				}
				else if (tokens[0].equals("-smfhost"))
				{
					smfHost  = tokens[1];
				}
				else
				{
					throw new Exception("Unrecognised parameter "+arg);
				}
			}
		}

	}

}
