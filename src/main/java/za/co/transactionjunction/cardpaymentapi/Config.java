package za.co.transactionjunction.cardpaymentapi;

import za.co.transactionjunction.imbeko.jdbc.data.handler.JunctionInstanceConfig;

public class Config
{
	private String host;
	private int port;

	public Config(JunctionInstanceConfig junctionInstanceConfig)
	{
		this.host = junctionInstanceConfig.getProperty("host", "localhost");
		this.port = junctionInstanceConfig.getPropertyAsInteger("port", 8080);
	}

	public int getPort()
	{
		return port;
	}

	public String getHost()
	{
		return host;
	}
}
