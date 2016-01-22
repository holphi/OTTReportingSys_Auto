package com.nagra.bsm.tests;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.nagra.bsm.tests.categorymarker.SanityCheck;

@RunWith(Categories.class)
@IncludeCategory(SanityCheck.class)
@SuiteClasses({RptScheduleMgrTests.class,
	           ReportsMgrTests.class,
			   UserMgrTests.class,
			   FrontPageConfTests.class,
			   RoleMgrTests.class, })

public class SanityTestSuite {

}
