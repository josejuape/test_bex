package com.bex.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({@ContextConfiguration(classes = TestJobLogger.class)})
@Configuration
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, TestJobLogger.class})
public class TestJobLogger extends AbstractTestExecutionListener  {
	private JobLogger jobLogger;
	private static final String PATH_FILE_LOG = "C:\\Apps\\BEX";
	
	@Test
	public void saveLogFile() throws Exception{
		
	}
	
	@Test
	public void printMessageToConsole() throws Exception{
		
		this.jobLogger = new JobLogger(false, true, false,
                true, false, false, null);
		
		String message = "Message of test for logger";
		this.jobLogger.logMessage(message, true, false, false);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void printWarningToConsole() throws Exception{
		
		this.jobLogger = new JobLogger(false, true, false,
                false, true, false, null);
		
		String message = "Warning of test for logger";
		this.jobLogger.logMessage(message, false, true, false);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void printErrorToConsole() throws Exception{
		
		this.jobLogger = new JobLogger(false, true, false,
                false, false, true, null);
		
		String message = "Error of test for logger";
		this.jobLogger.logMessage(message, false, false, true);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void printMessageToFile() throws Exception{
		Map<String,String> dbParams = new HashMap<String, String>();
		dbParams.put("logFileFolder", PATH_FILE_LOG);
		this.jobLogger = new JobLogger(true, false, false,
                true, false, false, dbParams);
		
		String message = "Message of test for logger - file";
		this.jobLogger.logMessage(message, true, false, false);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void printWarningToFile() throws Exception{
		Map<String,String> dbParams = new HashMap<String, String>();
		dbParams.put("logFileFolder", PATH_FILE_LOG);
		this.jobLogger = new JobLogger(true, false, false,
                false, true, false, dbParams);
		
		String message = "Warning of test for logger - file";
		this.jobLogger.logMessage(message, false, true, false);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void printErrorToFile() throws Exception{
		Map<String,String> dbParams = new HashMap<String, String>();
		dbParams.put("logFileFolder", PATH_FILE_LOG);
		this.jobLogger = new JobLogger(true, false, false,
                false, false, true, dbParams);
		
		String message = "Warning of test for logger - file";
		this.jobLogger.logMessage(message, false, false, true);
		Assert.assertNotNull(message);
	}
	
	@Test
	public void saveMessageMongoDb() throws Exception{
		Map<String,String> dbParams = new HashMap<String, String>();
		dbParams.put("userName", "");
		dbParams.put("password", "");
		dbParams.put("dbms", "dblogger");
		dbParams.put("serverName", "localhost");
		dbParams.put("portNumber", "27017");
		this.jobLogger = new JobLogger(false, false, true,
                true, false, false, dbParams);
		
		String message = "Message of test for logger";
		this.jobLogger.logMessage(message, true, false, false);
		Assert.assertNotNull(message);
	}
}
