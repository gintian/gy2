/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 9, 2006:1:28:51 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportStateDBInitTrans extends IBusiness {

public void execute() throws GeneralException {
		
		ArrayList reportStateList=(ArrayList)this.getFormHM().get("selectedList");
		if(reportStateList==null||reportStateList.size()==0){
			//return;
		    Exception e = new Exception(ResourceFactory.getProperty("report_collect.info7")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		String isSub=(String)hm.get("isSub");
		if(isSub==null)
			isSub="false";		
		for(Iterator t=reportStateList.iterator();t.hasNext();)
    	{
    		LazyDynaBean a=(LazyDynaBean)t.next();
    		String temp = (String) a.get("id");
    		String [] tt = temp.split("/");
    		String unitCode = tt[0];
    		String tabid = tt[1];
    		String usql ="";
    		if("false".equals(isSub))
    			usql="update  treport_ctrl  set status = -1 , description =null ,username =null,currappuser =null,appuser =null where unitcode = '"+unitCode+"' and tabid = " + tabid;
    		else
    			usql="update  treport_ctrl  set status = -1 , description =null ,username =null,currappuser =null,appuser =null where unitcode like '"+unitCode+"%' and tabid = " + tabid;
    		this.deleteDB(unitCode,tabid,flag,isSub);
    		this.updateSQL(usql);
    	}
	}
	
	public void updateSQL(String sql) throws GeneralException{
		//System.out.println(sql);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql);
		} catch (Exception  e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}





	/**
	 * 删除特定填报单位，特定报表对应相关数据
	 * @param unitCode 填报单位编码
	 * @param tabid    报表ID
	 */
	public void deleteDB(String unitCode,String tabid,String flag,String isSub){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String userNames = this.getUserNames(unitCode,isSub);
		if(!"".equals(userNames)){
			if("1".equals(flag)){
				//编辑表
				String sql = "delete from tb"+tabid+" where username in(" +userNames+")";
				//参数信息表
				String sql3 ="";
				if("false".equals(isSub))
					sql3="delete from tp_t"+tabid+ " where unitcode = '" +unitCode+"'";
				else
					sql3="delete from tp_t"+tabid+ " where unitcode like '" +unitCode+"%'";
				//归档表(编辑与汇总共有)(C/S中未做处理)
				//String sql2 = "delete from ta_"+tabid+" where unitcode='"+unitCode+"'";
				
				try {
					//删除填报单位的对应所有用户针对特定报表的所有数据
					DbWizard dbWizard=new DbWizard(this.frameconn);
					if(dbWizard.isExistTable("tb"+tabid,false))
						dao.delete(sql,new ArrayList());
					
					//删除填报单位对应的归档数据
					//dao.delete(sql2,new ArrayList());
					
					//删除填报单位对应的表参数信息
					if(dbWizard.isExistTable("tp_t"+tabid,false))
						dao.delete(sql3,new ArrayList());
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				//归档表(编辑与汇总共有)(C/S中未做处理)
				//String sql2 = "delete from ta_"+tabid+" where unitcode='"+unitCode+"'";		
				
				//汇总表
				String sql1 ="";
				if("false".equals(isSub))
					sql1="delete from tt_" + tabid + " where unitcode='"+unitCode+"'";				
				else
					sql1="delete from tt_" + tabid + " where unitcode like '"+unitCode+"%'";	
				//
				String sql5 ="";
				if("false".equals(isSub))
					sql5="delete from tt_t"+tabid+" where unitcode='"+ unitCode +"'";	
				else
					sql5="delete from tt_t"+tabid+" where unitcode like '"+ unitCode +"%'";
				try {
					//删除填报单位对应的汇总数据
					DbWizard dbWizard=new DbWizard(this.frameconn);
					if(dbWizard.isExistTable("tt_"+tabid,false))
						dao.delete(sql1,new ArrayList());
					
					//删除填报单位对应的归档数据
					//dao.delete(sql2,new ArrayList());	
					
					if(dbWizard.isExistTable("tt_t"+tabid,false))
						dao.delete(sql5,new ArrayList());
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取填报单位对应的用户列表
	 * @param unitCode
	 * @return
	 */
	public String getUserNames(String unitCode,String isSub){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql ="";
		if("false".equals(isSub))
			sql="select username  from operuser where unitcode = '"+unitCode+"'";
		else
			sql="select username  from operuser where unitcode like '"+unitCode+"%'";
		
		String uns = "";
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				uns+="'";
				uns+=rs.getString("username");
				uns+="'";
				uns+=",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(!"".equals(uns)){
			uns = uns.substring(0,uns.length()-1);
		}
		return uns;
	}
}
