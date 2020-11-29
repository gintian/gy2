package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;

public class SaveFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sqlstr = (String)this.getFormHM().get("sqlstr");
		sqlstr=sqlstr!=null?sqlstr:"";
		
		String valuestr = (String)this.getFormHM().get("valuestr");
		valuestr=valuestr!=null?valuestr:"";
		
		try {
			this.fstmt = this.frameconn.prepareStatement(sqlstr.toString());
			String varArr[] = valuestr.split("`");
			for(int i=0;i<varArr.length;i++){
				String fieldvalue = varArr[i].substring(1);
				if (varArr[i].startsWith("A")) {
					this.fstmt.setString(i+1, fieldvalue);
				} else if (varArr[i].startsWith("D")) {
					WeekUtils weekUtils = new WeekUtils();
					if (fieldvalue.trim().length() > 0)
						this.fstmt.setDate(i+1, new java.sql.Date(weekUtils
								.strTodate(fieldvalue).getTime()));
					else
						this.fstmt.setDate(i+1, null);
				} else if (varArr[i].startsWith("N")) {
					this.fstmt.setFloat(i+1, Float.parseFloat(fieldvalue));
				} else if (varArr[i].startsWith("M")) {
					if (Sql_switcher.searchDbServer() == Constant.ORACEL
							|| Sql_switcher.searchDbServer() == Constant.DB2) {
						Reader clobReader = new StringReader(fieldvalue);
						this.fstmt.setCharacterStream(i+1, clobReader, fieldvalue
								.length());
					} else
						this.fstmt.setString(i+1, fieldvalue);
				}
			}
			DbSecurityImpl dbS = new DbSecurityImpl();
			try {						
				dbS.open(this.frameconn, sqlstr.toString()); 
				this.fstmt.executeUpdate();
			} finally {
				try {
					dbS.close(this.frameconn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
