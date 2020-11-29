package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.List;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class QueryInfoSetupTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			
			String inforkind=(String)this.getFormHM().get("inforkind");
			this.getFormHM().put("strsql", "select id,name,CASE archive_type WHEN 1 THEN '"+ResourceFactory.getProperty("stat.info.setup.archive_type.month")+"' WHEN 2 THEN '"+ResourceFactory.getProperty("stat.info.setup.archive_type.season")+"' WHEN 3 THEN '"+ResourceFactory.getProperty("stat.info.setup.archive_type.half")+"' WHEN 4 THEN '"+ResourceFactory.getProperty("stat.info.setup.archive_type.year")+"' ELSE '' END archive_type_name,(select fieldsetdesc from fieldset where Archive_set=fieldsetid) Archive_set_name");
			this.getFormHM().put("columns", "id,name,archive_set_name,archive_type_name");
			this.getFormHM().put("cond_str", " from sname where infokind="+inforkind+" and type='1'");
			this.getFormHM().put("order_by", " order by snorder");
			StringBuffer sql = new StringBuffer();
			sql.append((String)this.getFormHM().get("strsql"));
			sql.append((String)this.getFormHM().get("cond_str"));
			sql.append((String)this.getFormHM().get("order_by"));
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			ArrayList list = new ArrayList();
			for(int i=0;i<rs.size();i++)
	   		{
			     DynaBean rec=(DynaBean)rs.get(i);
			     String id=rec.get("id")!=null?rec.get("id").toString():"";
			     if((this.userView.isHaveResource(IResourceConstant.STATICS,id))){
			    	 list.add(rec);
			     }
	   		}
			this.getFormHM().put("list", list);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
