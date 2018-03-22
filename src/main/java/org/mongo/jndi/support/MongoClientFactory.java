package org.mongo.jndi.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * This is a JNDI factory class to create MongoClient<br/>
 * It can build MongoClient base on three method.<br/>
 * 1. Build without auth<br/>
 * sample: mongoHost="localhost" mongoPort="27017"<br/>
 * <br/>
 * 2. Build with auth<br/>
 * sample: mongoHost="localhost" mongoPort="27017" dbName="test" username="user"
 * password="pwd"<br/>
 * <br/>
 * 3. Build with mongoUri<br/>
 * mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]<br/>
 * sample:
 * mongodb://user:pwd@db1.example.net:27017,db2.example.net:2500/test?replicaSet=test
 * 
 * @author edwin.tang
 * 
 */
public final class MongoClientFactory implements Serializable, ObjectFactory {
	private static final long serialVersionUID = 1156559128319686849L;

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
		Properties config = new Properties();

		Reference ref = (Reference) obj;
		Enumeration<RefAddr> addrs = ref.getAll();
		while (addrs.hasMoreElements()) {
			RefAddr addr = addrs.nextElement();
			String key = addr.getType();
			String value = (String) addr.getContent();
			config.setProperty(key, value);
		}

		if (config.containsKey("mongoUri")) {
			String mongoUri = config.getProperty("mongoUri");
			return new MongoClient(new MongoClientURI(mongoUri));
		}

		String mongoHost = config.getProperty("mongoHost", "localhost");
		Integer mongoPort = Integer.parseInt(config.getProperty("mongoPort", "27017"));
		String dbName = config.getProperty("dbName");
		String username = config.getProperty("username");
		String password = config.getProperty("password");
		if (isBlank(username) || isBlank(password)) {
			return new MongoClient(mongoHost, mongoPort);
		}

		return new MongoClient(Collections.singletonList(new ServerAddress(mongoHost, mongoPort)),
				Collections.singletonList(MongoCredential.createCredential(username, dbName, password.toCharArray())));
	}

	private boolean isBlank(String str) {
		if (null == str) {
			return true;
		}

		return "".equals(str.trim());
	}

}
