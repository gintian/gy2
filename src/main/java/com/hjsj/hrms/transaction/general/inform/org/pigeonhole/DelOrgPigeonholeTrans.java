/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform.org.pigeonhole;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * @author Administrator
 *
 */
public class DelOrgPigeonholeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String catalogid = (String)this.getFormHM().get("catalogid");
		catalogid = catalogid!=null?catalogid:"";
		
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = null;
			sql = "delete from hr_org_catalog where catalog_id='"+catalogid+"'";
			if(dao.update(sql)>0){
				sql=sql.replace("hr_org_catalog", "hr_org_history");
				dao.update(sql);
			}
			StringBuffer sqlstr=new StringBuffer();
			sqlstr.append("select min(catalog_id) as catalog_id from  hr_org_catalog");
			List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
			if(!rs.isEmpty())
			{
			    LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			    this.getFormHM().put("catalog_id",rec.get("catalog_id"));
			}else
				this.getFormHM().put("catalog_id","");
		    this.getFormHM().put("msg", "1");
		}catch(Exception e){
			this.getFormHM().put("msg", "0");
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
	}

}
