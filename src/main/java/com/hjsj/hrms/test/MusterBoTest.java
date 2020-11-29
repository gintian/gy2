package com.hjsj.hrms.test;

import com.hjsj.hrms.businessobject.general.muster.FontBeanVo;
import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import junit.framework.TestCase;

import java.sql.Connection;

public class MusterBoTest extends TestCase {

	private Connection conn=null;
	protected void setUp() throws Exception {
		super.setUp();
		DbManager db=new DbManager();
		conn=db.getConnection(1);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if(conn!=null)
			conn.close();
	}

	/*
	 * Test method for 'com.hjsj.hrms.businessobject.general.muster.MusterBo.getFontProperty(int, int)'
	 */

	public void testXXX() {
		MusterBo bo=new MusterBo(this.conn,null);
		FontBeanVo fontvo=new FontBeanVo();
//		bo.parseTitleFontDefintion(fontvo,"/fn\"宋体\"/fz\"11\"/c/fb1/fi0/fu0/fk0");
//		bo.parseHeadFontDefintion(fontvo,"/fn\"宋体\"/fz\"11\"/fb1/fi0/fu0/fk0");
//		bo.parseBodyFontDefintion(fontvo,"宋体,11,0,0,0,0,");

		assertEquals("宋体",fontvo.getFontname());
	}	
}
