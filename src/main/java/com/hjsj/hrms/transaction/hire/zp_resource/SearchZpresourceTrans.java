/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchZpresourceTrans</p>
 * <p>Description:查询招聘资源,zp_resource</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpresourceTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String resource_id = (String)hm.get("a_id");
        RecordVo vo=new RecordVo("zp_resource");
        String flag =(String)this.getFormHM().get("flag");
        
        //ExecuteSQL executeSQL = new ExecuteSQL();
        /**
         * 按新增按钮时，则不进行查询，直接退出
         */

        
        if("1".equals(flag)){
           	this.getFormHM().put("zpresourcevo",vo);
        }else{
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            vo.setString("resource_id",resource_id);
            String sql = "select * from zp_resource where resource_id = "+resource_id;
            List rs = ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
        	for(int i=0;rs!=null&&i<rs.size();i++){
        		LazyDynaBean rec=(LazyDynaBean)rs.get(i);
            	vo.setString("name", rec.get("name").toString());
            	vo.setString("area", rec.get("area").toString());
            	vo.setString("scope", rec.get("scope").toString());
            	vo.setString("charge", rec.get("charge").toString());
            	vo.setString("phone", rec.get("phone").toString());
            	vo.setString("linkman", rec.get("linkman").toString());
            	vo.setString("address", rec.get("address").toString());
            	vo.setString("postalcode", rec.get("postalcode").toString());
            	vo.setString("http", rec.get("http").toString());
            	vo.setString("description", PubFunc.nullToStr( rec.get("description").toString()));
            	vo.setString("status", rec.get("status").toString());
            	vo.setString("type_id", rec.get("type_id").toString());
            }
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zpresourcevo",vo);
        }

	}

	}

}
