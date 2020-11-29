/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpresourceSetTrans</p>
 * <p>Description:保存招聘资源分类,zp_resource_set</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpresourceSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpresourceSetvo");
        if(vo==null)
            return;
        String flag_set=(String)this.getFormHM().get("flag_set");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag_set))
        {
            /**
             * 新增招聘资源分类
             */
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String type_id=idg.getId("zp_resource_set.type_id");
               vo.setString("type_id",type_id); 
               String sql = "insert into zp_resource_set (type_id,name,status)" +
            		"values('"+type_id+"','"+vo.getString("name")+"','0')";
               ArrayList list = new ArrayList();
           
               dao.update(sql,list);
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
        else if("0".equals(flag_set))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_resource_set set name='"+vo.getString("name")+
							"' where type_id ='"+vo.getString("type_id")+"'";
	        	dao.update(sql,list);
	        	
	            //dao.updateValueObject(vo);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
