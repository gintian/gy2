package com.hjsj.hrms.test;

import com.hjsj.hrms.taglib.general.menubar.Menu;
import junit.framework.TestCase;


public class TestMenuXml extends TestCase {

	/*
	 * Test method for 'com.hjsj.hrms.taglib.general.menubar.Menu.outMenuxml()'
	 */
	public void testOutMenuxml() {
		Menu menu=new Menu("muster_data","menu1",null);
		System.out.println(menu.outMenuxml());
	}

}
