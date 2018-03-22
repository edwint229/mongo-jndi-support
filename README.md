mongo-jndi-support
===================
If you want to lookup MongoClient as JNDI like connect other database(Oracle,MySQL), Then this project will help you easier.

1. Dowload this project, in dist folder you will found two jar files, put it into tomcat lib folder.

2. Add Resource define into tomcat server.xml

Example 1. Build without auth

`<Resource name="testJndi" auth="Container" factory="org.mongo.jndi.support.MongoClientFactory" type="com.mongodb.MongoClient" mongoHost="localhost" mongoPort="27017"/>`


Example 2. Build with auth

`<Resource name="testJndi" auth="Container" factory="org.mongo.jndi.support.MongoClientFactory" type="com.mongodb.MongoClient" mongoHost="localhost" mongoPort="27017" dbName="test" username="user" password="test1234"/>`


Example 3. Build with mongoUri

`<Resource name="testJndi" auth="Container" factory="org.mongo.jndi.support.MongoClientFactory" type="com.mongodb.MongoClient" mongoUri="mongodb://user:pwd@db1.example.net:27017,db2.example.net:2500/test?replicaSet=test"/>`

3. Add JNDI name to global context, define into tomcat context.xml

`<ResourceLink global="testJndi" name="testJndi" type="com.mongodb.MongoClient"/>`

4. Then you can lookup this jdni in you application, below is a Spring Configure Sample.

		<?xml version="1.0" encoding="UTF-8"?>
		<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
			xmlns:tx="http://www.springframework.org/schema/tx" xmlns:context="http://www.springframework.org/schema/context"
			xsi:schemaLocation="http://www.springframework.org/schema/beans 
								http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
								http://www.springframework.org/schema/tx
								http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
								http://www.springframework.org/schema/aop 
								http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
								http://www.springframework.org/schema/context   
								http://www.springframework.org/schema/context/spring-context-3.0.xsd">

			<bean id="mongoClient" class="org.springframework.jndi.JndiObjectFactoryBean">
				<property name="jndiName">
					<value>java:comp/env/testJndi</value>
				</property>
			</bean>
		</beans>

Inject in java directly

		package com.gpayroll.audit.config;

		import javax.annotation.Resource;

		import org.springframework.beans.factory.annotation.Value;
		import org.springframework.context.annotation.Configuration;
		import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
		import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

		import com.mongodb.Mongo;
		import com.mongodb.MongoClient;

		@Configuration
		@EnableMongoRepositories(basePackages = RepositoryConfig.MONGO_BASE_PACKAGE)
		public class RepositoryConfig extends AbstractMongoConfiguration {
			public static final String MONGO_BASE_PACKAGE = "com.gpayroll.mongo.dao";

			@Value("${mongo.dbname}")
			private String dbName;

			@Resource
			private MongoClient mongoClient;

			@Override
			protected String getDatabaseName() {
				return dbName;
			}

			@Override
			public Mongo mongo() throws Exception {
				return mongoClient;
			}

			@Override
			protected String getMappingBasePackage() {
				return MONGO_BASE_PACKAGE;
			}
		}
5. BTW, Please setup your mongo-java-driver as provider to avoid jar file conflict.

		<dependency>
			<groupId>org.mongodb</groupId>
			<artifactId>mongo-java-driver</artifactId>
			<version>2.14.0</version>
			<scope>provided</scope>
		</dependency>
