/*
 * Created on 2006-3-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchIniCatalog_idTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		try{
			String  catalog_id=(String)this.getFormHM().get("catalog_id");
			if(catalog_id==null||catalog_id.length()<=0)
			{
				StringBuffer sql=new StringBuffer();
				sql.append("select min(catalog_id) as catalog_id from  hr_org_catalog");
				List rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(!rs.isEmpty())
				{
				    LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				    this.getFormHM().put("catalog_id",rec.get("catalog_id"));
				    if(rec.get("catalog_id")==null || rec.get("catalog_id").toString().length()==0)
				    	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.inform.org.nohistoryorg"),"",""));
					
				}else
				{
					  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("general.inform.org.nohistoryorg"),"",""));
				}
			}else
			  this.getFormHM().put("catalog_id",catalog_id);
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);   //add by wangcq on 2014-11-13 抛出异常，给前台提示用户
		}
	}

}
