/*
 * Created on 2005-9-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_employ;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchEmailMessageTrans</p>
 * <p>Description:查询邮件信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchEmailMessageTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo constant_vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
		if(constant_vo==null)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetemailfield"),"",""));
		String emailField = constant_vo.getString("str_value");
		RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		if(rv==null)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.zp_exam.notsetdbname"),"",""));
	    String dbpre = rv.getString("str_value");
		ArrayList list = new ArrayList();
		ArrayList toAddr = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList infodata=(ArrayList)this.getFormHM().get("selectedlist");
		int count = 0;
		String strCount = "";
		if(infodata.isEmpty()){
			strCount = "0";
			this.getFormHM().put("count",strCount); 
		}
		else{
			for(int i=0;i<infodata.size();i++)
			{				
				LazyDynaBean rec=(LazyDynaBean)infodata.get(i);
				String A0100=rec.get("a0100").toString();
				SetInformStatus(dao,A0100);
				try{
				   	  String strsql = "update zp_pos_tache set status = '1' where A0100 = '"+A0100+"'";
				   	  dao.update(strsql,list);
				   	  toAddr.add(A0100);
				   	  count++;				   	 
				}catch(SQLException sqle){
				    sqle.printStackTrace();
				    throw GeneralExceptionHandler.Handle(sqle);
			    }finally{
			    	this.getFormHM().put("toAddrlist",toAddr);
			    	if(count == 0){
			    		strCount = "0";
			    	}else{
			    		strCount = String.valueOf(count);
			    	}
			    	this.getFormHM().put("count",strCount); 
			    }
			}
		}
	}
	/*wlh修改改变通知的状态*/
   private void SetInformStatus(ContentDAO dao,String a0100)
   {
   	 try{
   	    String sql="update zp_pos_tache set status='1' where a0100='" + a0100 + "'";
     	dao.update(sql);
     }catch(Exception e)
	 {
    	e.printStackTrace();
     }
   }
}
