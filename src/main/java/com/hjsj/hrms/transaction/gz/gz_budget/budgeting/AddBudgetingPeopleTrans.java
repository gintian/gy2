package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddBudgetingPeopleTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
		String b0110=bo.getUnitcode();
		BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
		Integer budget_id = allocBo.getCurrentBudgetId(); 
		BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
		String rylb_codeset=((String)sysbo.getSysValueMap().get("rylb_codeset")).toLowerCase();
		String sql="select * from codeitem where upper(codesetid)='"+rylb_codeset.toUpperCase()+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ResultSet rs=null;
		ResultSet ros=null;
		String codeitemdesc="";
		String codeitemid="";
		String num;
		ArrayList list=new ArrayList();
		try {
			rs=dao.search(sql);
			while(rs.next()){
				codeitemdesc=rs.getString("codeitemdesc");
				codeitemid=rs.getString("codeitemid");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codeitemdesc", codeitemdesc);
				bean.set("codeitemid", codeitemid);
				bean.set("codeitemtype", "N");
				String sbl="select count(*) as A from sc01 where sc000='"+codeitemid+"' and b0110='"+b0110+"' and budget_id="+budget_id.toString()
				          + " and A0101='" + ResourceFactory.getProperty("gz.budget.newstaff") + "'";
				ros=dao.search(sbl);
				if(ros.next()){
					num=ros.getString("A");
					bean.set("num", num);
				}
				list.add(bean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("fieldList", list);
	}

}
