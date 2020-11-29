/*
 * Created on 2005-11-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.structuresql;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonSqlExec {
	public String getUserId(String tableName,Connection conn) throws GeneralException{
		String strsql = "select max(A0100) as a0100 from " + tableName + " order by A0100";
		List rs=ExecuteSQL.executeMyQuery(strsql,conn);
		int userPlace;
		String userNumber;
		if (!rs.isEmpty()) {
			//System.out.println("dfas");
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("a0100")!=null)
			   userPlace =Integer.parseInt(((rec.get("a0100").toString().trim().length()>0)?rec.get("a0100").toString().trim():"0")) + 1;
			else
			   userPlace=1;
		} else
			userPlace = 1;
	//	System.out.println("userNumber" + userPlace);
		userNumber = Integer.toString(userPlace);
		for (int i = 0; i < 8 - (Integer.toString(userPlace)).length(); i++)
			userNumber = "0" + userNumber;
		//cat.debug("userNumber ->" + userNumber);
		
		return userNumber;
	}
}
