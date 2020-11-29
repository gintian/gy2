package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 * 
 * <p>Title:业务字典构库写入子集名称</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2009:5:21:51 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SubSetNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tableid = (String)hm.get("tableid");
		String id = (String)hm.get("id");
		String tablename = this.tablename(tableid,id);
		this.getFormHM().put("tableid", tableid);  
		this.getFormHM().put("tablename", tablename);
		this.getFormHM().put("mainid", id);
		
	}
	public String tablename(String tableid,String id){
		String name="";
			try{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select fieldsetdesc,customdesc from t_hr_busitable where id='"+id+"' and fieldsetid='"+tableid+"'";
				RowSet rowSet = dao.search(sql.toString());
				while(rowSet.next()){
					name = rowSet.getString("customdesc");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		return name;
	}
}
