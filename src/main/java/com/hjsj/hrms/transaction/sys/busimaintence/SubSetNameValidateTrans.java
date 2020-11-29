package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 * 
 * <p>Title:构库子集名称验证</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2009:5:53:03 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SubSetNameValidateTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg = "1";
		String tableid = (String)this.getFormHM().get("tableid");
		String tablename = (String)this.getFormHM().get("tablename");
		tablename = com.hrms.frame.codec.SafeCode.decode(tablename);
		String mainid = (String)this.getFormHM().get("mainid");
		
		boolean flag=true;
		if(this.checkupfieldname(tableid,tablename,mainid)){
			flag = false;
			msg=ResourceFactory.getProperty("kjg.error.clew");
		}
		this.getFormHM().put("msg", msg);
	}

	//检查表名是否存在
	public boolean checkupfieldname(String id,String name,String mainid){
		boolean flag=false;
		try{
			String sql ="select customdesc from t_hr_busitable where customdesc='"+name+"' and fieldsetid <> '"+id+"' and id <> '"+mainid+"'";
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql);
			while(rs.next()){
				flag = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
}
