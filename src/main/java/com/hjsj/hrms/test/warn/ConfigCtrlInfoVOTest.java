package com.hjsj.hrms.test.warn;


import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import junit.framework.TestCase;

public class ConfigCtrlInfoVOTest extends TestCase {

	ConfigCtrlInfoVO test = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		String strSourceXML =  "<?xml version='1.0' encoding=\"GB2312\"?>"+
		 "<hrpwarn>"+
		 "     <warnctrl domain=\"UNxxx|UMxxxx|QKxxx|RLxxxxx\"  email=\"true|false\" mobile=\"true|false\" days=\"10\" rule=\"0|1\"/>"+
		 "     <frequency   type=\"0|1|2\" value=\"10\"/>"+
		 "</hrpwarn>";
		
		test = new ConfigCtrlInfoVO( strSourceXML );

	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrDays()'
	 */
	public void testGetStrDays() {
		assertEquals( test.getStrDays(), "10");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrDomain()'
	 */
	public void testGetStrDomain() {
		assertEquals( test.getStrDomain(), "UNxxx|UMxxxx|QKxxx|RLxxxxx");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrEmail()'
	 */
	public void testGetStrEmail() {
		assertEquals( test.getStrEmail(), "true|false");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrFreqType()'
	 */
	public void testGetStrFreqType() {
		assertEquals( test.getStrFreqType(), "0|1|2");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrFreqValue()'
	 */
	public void testGetStrFreqValue() {
		assertEquals( test.getStrFreqValue(), "10");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrMobile()'
	 */
	public void testGetStrMobile() {
		assertEquals( test.getStrMobile(), "true|false");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrRule()'
	 */
	public void testGetStrRule() {
		assertEquals( test.getStrRule(), "0|1");
	}

	/*
	 * Test method for 'com.hjsj.hrms.actionform.sys.warn.ConfigCtrlInfoVO.getStrXML()'
	 */
	public void testGetResultXML() {
		String strResult = test.generateStringXML();
		test = new ConfigCtrlInfoVO( strResult );
		assertNotNull( strResult );
		assertEquals( test.getStrFreqValue(), "10");
	}

}
