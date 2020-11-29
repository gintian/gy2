package com.hjsj.hrms.businessobject.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Serialnumber {
	// 业务字典计算指标代码号，方法添加userType参数，jingq upd 2015.01.22
	public static String getSerialnum(Connection conn, String fieldsetid,
			String userType) throws SQLException {
		String re = "";
		String ret = "";
		ArrayList list = new ArrayList();
		String itemid = "";
		ContentDAO dao = null;
		String lens = "";
		try {
			if(Sql_switcher.searchDbServer()==Constant.MSSQL){
				lens = "LEN(ItemId)";
			} else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				lens = "length(ItemId)";
			}
			String sqls = "select (select MAX(displayid) from t_hr_busifield where FieldSetId = '"
					+ fieldsetid
					+ "') as AAA,(select MAX(ItemId) from t_hr_busifield where FieldSetId = '"
					+ fieldsetid
					+ "' and "+lens+" = 5) as BBB from t_hr_busifield where FieldSetId = '"
					+ fieldsetid + "'";
			RowSet rs1 = null;
			dao = new ContentDAO(conn);
			rs1 = dao.search(sqls);
			while(rs1.next()){
				re = rs1.getString(1);
				ret = rs1.getString(2);
			}
			if(ret==null||"".equals(ret)){
				re = "1";
			} else {
				re = Integer.parseInt(re)+1+"";
			}
			String dev_flag = SystemConfig.getPropertyValue("dev_flag");
			IndexBo bo = new IndexBo(conn);
			itemid  = bo.getBusiIndex(fieldsetid,dev_flag);
			//itemid = bo.toAdd(ret, fieldsetid, userType, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemid + "/" + re;

	}

}
