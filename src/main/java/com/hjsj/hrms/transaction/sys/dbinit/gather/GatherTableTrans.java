package com.hjsj.hrms.transaction.sys.dbinit.gather;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:采集表生成</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 26, 2008:3:56:19 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class GatherTableTrans extends IBusiness{

	public void execute() throws GeneralException {
//		String fieldA="A";
//		String fieldB="B";
//		String fieldK="K";
//		GatherTableBo gather = new GatherTableBo(this.getFrameconn());
//		ArrayList userlist = gather.userlist(fieldA);
//		this.getFormHM().put("userlist", userlist);
//		
//		ArrayList unitslist = gather.unitslist(fieldB, fieldK);
//		this.getFormHM().put("unitslist", unitslist);
//		
//		ArrayList indexlist = gather.indexlist();
//		this.getFormHM().put("indexlist", indexlist);
		boolean lock = (Boolean.valueOf((String)this.getFormHM().get("lock"))).booleanValue();
		boolean isS = (Boolean.valueOf((String)this.getFormHM().get("isS"))).booleanValue();
		ArrayList alllist = new ArrayList();
		CommonData da = new CommonData();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String retname="";
		String retspre="";
		String sql = "select classname,classpre from informationclass ";
		RowSet rowSet;
		try {
			rowSet = dao.search(sql.toString());
			while(rowSet.next()){
				retspre = rowSet.getString("classpre");
				if("Y".equalsIgnoreCase(retspre)||"V".equalsIgnoreCase(retspre)||"W".equalsIgnoreCase(retspre))
					if(!lock)
						continue;
				if("H".equalsIgnoreCase(retspre))
					if(!isS)
						continue;
				retname=rowSet.getString("classname");
				da = new CommonData();
				da.setDataName(retname+"采集表");
				da.setDataValue(retspre);
				alllist.add(da);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("alllist", alllist);
	}
	
}
