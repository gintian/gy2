package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

public class InitSearchStatChartTrans extends IBusiness {

	public void execute() throws GeneralException {
		String statid=(String)this.getFormHM().get("statid");
		String userbase=(String)this.getFormHM().get("nbase");
		if(userbase==null||userbase.length()<=0)
		{
			ArrayList Dblist=userView.getPrivDbList();				
			if(Dblist!=null && Dblist.size()>0){
				userbase=Dblist.get(0).toString();
			}
		}
		StringBuffer sql =new StringBuffer();
		sql.append("select * from SName where id=");
		sql.append(statid);
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		String name="";
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			name= rec.get("name")!=null?rec.get("name").toString():"";
			
		}
		String charttype=(String)this.getFormHM().get("charttype");
		charttype=charttype!=null&&charttype.length()>0?charttype:"1";//柱状
		this.getFormHM().put("charttype", charttype);
		this.getFormHM().put("snamedisplay",name);
		this.getFormHM().put("nbase", userbase);
		this.getFormHM().put("stid", statid.toString());
		StringBuffer chartjs=new StringBuffer();
		if("2".equals(charttype))
		{
			
			
		}
	}
}
