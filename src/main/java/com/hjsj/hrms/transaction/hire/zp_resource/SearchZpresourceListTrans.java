/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SearchZpresourceSetListTrans</p>
 * <p>Description:查询招聘资源分类列表,zp_resource_set</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpresourceListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo rv=(RecordVo)this.getFormHM().get("zpresourcevo");
		String type_id = rv.getString("type_id");
		StringBuffer strsql=new StringBuffer();
		if(type_id == null || "".equals(type_id)){
			strsql.append("select * from zp_resource");
		}else{
	       if("#".equals(type_id))
	        strsql.append("select * from zp_resource");
	       else
	       	strsql.append("select * from zp_resource where type_id = '"+type_id+"'");
		} //ExecuteSQL executeSQL = new ExecuteSQL();
	       ArrayList list=new ArrayList();
	       try
	       {
	        List rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
        	for(int i=0;rs!=null&&i<rs.size();i++){
        		LazyDynaBean rec=(LazyDynaBean)rs.get(i);
	             RecordVo vo=new RecordVo("zp_resource");
	             vo.setString("resource_id", rec.get("resource_id").toString());
	             vo.setString("name", rec.get("name").toString());
	             vo.setString("area", rec.get("area").toString());
	             vo.setString("scope", rec.get("scope").toString());
	             vo.setString("charge", rec.get("charge").toString());
	             vo.setString("phone", rec.get("phone").toString());
	             vo.setString("linkman", rec.get("linkman").toString());
	             vo.setString("address", rec.get("address").toString());
	             vo.setString("postalcode", rec.get("postalcode").toString());
	             vo.setString("http", rec.get("http").toString());
	             String description = PubFunc.nullToStr(rec.get("description").toString());
	             vo.setString("description", description.substring(0,description.length()>20?20:description.length())+"...");
	             vo.setString("status",rec.get("status").toString());
	             vo.setString("type_id", rec.get("type_id").toString());
	             list.add(vo);
	         }
	       }catch(Exception sqle){
	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);
	       }
	       finally
	       {
	           this.getFormHM().put("zpresourcelist",list); 
	       }
	}

}
