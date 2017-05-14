package com.liequ.rabbitmq.factory;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liequ.rabbitmq.exception.ConfigException;
import com.liequ.rabbitmq.util.Envm;
import com.rabbitmq.client.Address;

public class BrokerConfigManager {

	private static final BrokerConfigManager instance = new BrokerConfigManager();

	private final static String CONFIG_FILE_NAME = "rabbitmq.json";

	private BrokerConfigManager() {
	}

	HashMap<String, BrokerConfig> BROKER_CONFIG = new HashMap<String, BrokerConfig>(
			4);

	public static BrokerConfigManager getInstance() {
		return instance;
	}

	public BrokerConfig getBrokerConfig(String brokerName) throws Exception {
		if (!BROKER_CONFIG.containsKey(brokerName)) {
			synchronized (this) {
				if (!BROKER_CONFIG.containsKey(brokerName)) {
					init();
				}
			}
		}
		return BROKER_CONFIG.get(brokerName);
	}

	public void init() throws Exception {
		String path = Envm.ROOT.concat(CONFIG_FILE_NAME);
		File configFile = new File(path);
		JsonParser parse = new JsonParser();
		JsonObject json = (JsonObject) parse.parse(new FileReader(configFile));
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();

		for (Entry<String, JsonElement> entry : entrySet) {
			String brokerName = entry.getKey().toString().trim();

			BrokerConfig config = new BrokerConfig();
			JsonObject result = json.get(brokerName).getAsJsonObject();

			if (!result.has("userName"))
				throw new ConfigException("userName is not null");
			String userName = result.get("userName").getAsString();
			config.setUserName(userName);

			if (!result.has("passsWord"))
				throw new ConfigException("passsWord is not null");
			String passsWord = result.get("passsWord").getAsString();
			config.setPasssWord(passsWord);

			if (!result.has("virtualHost"))
				throw new ConfigException("virtualHost is not null");
			String virtualHost = result.get("virtualHost").getAsString();
			config.setVirtualHost(virtualHost);

			if (!result.has("server"))
				throw new ConfigException("server is not null");
			String value = result.get("server").getAsString();
			String[] servers = value.split(",");
			if (servers.length == 0) {
				throw new ConfigException("server must be seted");
			}
			ArrayList<Address> addressList = new ArrayList<Address>(3);
			for (String server : servers) {
				String[] addStr = server.split(":");
				if (addStr.length != 2) {
					throw new ConfigException("Unrecognise server " + server);
				}
				String host = addStr[0];
				int port = Integer.parseInt(addStr[1]);
				Address address = new Address(host, port);
				addressList.add(address);
			}
			config.setServerPortAddress(addressList);

			if (result.has("maxWaitMillis")) {
				config.setMaxWaitMillis(result.get("maxWaitMillis").getAsLong());
			}

			if (result.has("maxConnTotal")) {
				config.setMaxConnTotal(result.get("maxConnTotal").getAsInt());
			}

			if (result.has("maxChannelCountPerConn")) {
				config.setMaxChannelCountPerConn(result.get(
						"maxChannelCountPerConn").getAsInt());
			}

			if (result.has("initialSize")) {
				config.setInitialSize(result.get("initialSize").getAsInt());
			}
			if (result.has("maxWaitMillis")) {
				config.setMaxWaitMillis(result.get("initialSize").getAsLong());
			}

			if (result.has("blockWhenExhausted")) {
				config.setBlockWhenExhausted(result.get("blockWhenExhausted")
						.getAsBoolean());
			}
			BROKER_CONFIG.put(brokerName, config);
		}
	}
}
