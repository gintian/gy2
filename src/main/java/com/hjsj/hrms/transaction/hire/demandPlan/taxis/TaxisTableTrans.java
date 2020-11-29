package com.hjsj.hrms.transaction.hire.demandPlan.taxis;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:TaxisTableTrans.java</p>
 * <p>Description:对（单表）数据按指标进行排序</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 20, 2006 11:23:19 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class TaxisTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String fasion=(String)this.getFormHM().get("fasion");  //1：升序  0：降序
		ArrayList fieldList=(ArrayList)this.getFormHM().get("selectedFields");
		
		String a_fasion="ASC";
		if("0".equals(fasion))
			a_fasion="desc";
		StringBuffer ext_sql=new StringBuffer("");
		for(int i=0;i<fieldList.size();i++)
		{
			String temp=(String)fieldList.get(i);
			if(temp!=null&&temp.length()>0)
			{
				temp=temp.replaceAll("unit","codeitemid");
				temp=temp.replaceAll("departid","codeitemid");
				ext_sql.append(","+temp+" "+a_fasion);
			}
		}
		
		this.getFormHM().put("orderSql", PubFunc.encrypt(" order by "+ext_sql.substring(1)));//dml
		
	}

}
