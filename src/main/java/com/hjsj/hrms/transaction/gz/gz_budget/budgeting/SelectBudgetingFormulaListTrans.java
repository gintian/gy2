package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaListBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectBudgetingFormulaListTrans extends IBusiness {


	public void execute() throws GeneralException {
		try {
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag=(String)requestPamaHM.get("flag");
			ArrayList list=new ArrayList();
			ArrayList tablist=new ArrayList();
			String selecttab_id= (String)this.getFormHM().get("selectformula_tabid");
			if ((selecttab_id==null)||("".equals(selecttab_id))) selecttab_id="0";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String strSql ="select * from gz_budget_tab order by seq";		
			this.frowset = dao.search(strSql);
			while (this.frowset.next()) {
				CommonData datavo = new CommonData(this.frowset
						.getString("tab_id"), this.frowset
						.getString("tab_name"));
				
				tablist.add(datavo);
			}
			
			BudgetFormulaListBo formulabo = new BudgetFormulaListBo(this.getFrameconn(),this.userView);			
			list=formulabo.getList();			
			BudgetFormulaBo formula;
			ArrayList formulaList=new ArrayList();			
			for(int i=0;i<list.size();i++)
			{
				formula=(BudgetFormulaBo)list.get(i);
				int tabid=formula.getTabID();
				if (("0".equals(selecttab_id)) || (selecttab_id.equals(Integer.toString(tabid)))){
					LazyDynaBean bean= new LazyDynaBean();
					
					bean.set("formulaid", new Integer(formula.getFormulaID()));
					bean.set("formulaname", formula.getFormulaName());					
					bean.set("tabname", getTabname(Integer.toString(tabid)));
					formulaList.add(bean);					
					
				}
			}
			this.getFormHM().put("selectformulalist", formulaList);
			this.getFormHM().put("selectformula_tablist", tablist);
			this.getFormHM().put("selectformula_tabid", selecttab_id);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	public String getTabname(String tabid) throws GeneralException {
		String tab_name="";
		try{
			this.frowset.beforeFirst();

			while (this.frowset.next()) {
				if (this.frowset.getString("tab_id").equals(tabid))
				{
					tab_name=this.frowset.getString("tab_name");
					break;
				}
			}
		
			return tab_name;
		}	
	catch(Exception ex){
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
		
	}	
		
	}

}
