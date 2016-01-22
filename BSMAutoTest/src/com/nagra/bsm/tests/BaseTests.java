package com.nagra.bsm.tests;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestName;

public class BaseTests {

	@Rule
	public TestName testName = new TestName();
	protected static final Logger logger = Logger.getLogger(BaseTests.class);	

}
