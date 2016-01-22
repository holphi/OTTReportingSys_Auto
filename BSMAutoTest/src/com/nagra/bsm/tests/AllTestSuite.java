package com.nagra.bsm.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FrontPageConfTests.class,
					 LogViewerTests.class,
					 ReportsMgrTests.class,
					 RoleMgrTests.class,
					 RptTransportTests.class,
					 UserMgrTests.class
					 })

public class AllTestSuite {

}
