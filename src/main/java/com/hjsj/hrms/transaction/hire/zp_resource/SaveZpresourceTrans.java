/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpresourceTrans</p>
 * <p>Description:保存招聘资源,zp_resource</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpresourceTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo=(RecordVo)this.getFormHM().get("zpresourcevo");
        if(vo==null)
            return;
        String flag =(String)this.getFormHM().get("flag");
        ExecuteSQL executeSQL = new ExecuteSQL();
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        ArrayList list = new ArrayList();
        if("1".equals(flag))
        {
            /**
             * 新增招聘资源
             */
        	PreparedStatement pstmt=null;
        	try{
        	   IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String resource_id = idg.getId("zp_resource.resource_id");
               //System.out.println("description "+vo.getString("description"));
               String sql = "insert into zp_resource (resource_id,name,area,scope,charge,phone,linkman,address,postalcode,http,description,status,type_id) values ("+resource_id+",'"+vo.getString("name")+"','"+vo.getString("area")+"','"+vo.getString("scope")+"',"+vo.getString("charge")+",'"+vo.getString("phone")+"','"+vo.getString("linkman")+"','"+vo.getString("address")+"','"+vo.getString("postalcode")+"','"+vo.getString("http")+"','"+vo.getString("description")+"','0','"+vo.getString("type_id")+"')";
               dao.insert(sql,list);
            }catch(Exception e){
            	e.printStackTrace();
            }      
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	String sql="update zp_resource set name = '"+vo.getString("name")+"',area='"+vo.getString("area")+"',scope='"+vo.getString("scope")+"',charge = "+vo.getString("charge")+",phone='"+vo.getString("phone")+"',linkman = '"+vo.getString("linkman")+"',address = '"+vo.getString("address")+"',postalcode = '"+vo.getString("postalcode")+"',http = '"+vo.getString("http")+"',description = '"+vo.getString("description")+"',type_id = '"+vo.getString("type_id")+"' where  resource_id = "+vo.getString("resource_id");
	        	dao.update(sql,list);
	        }
	        catch(Exception sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
