package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class BudgetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String cformula_id="0";
			StringBuffer sql = new StringBuffer();
			StringBuffer tabsql = new StringBuffer();
			StringBuffer sql1 = new StringBuffer();
			ArrayList list = new ArrayList();
			ArrayList tablist = new ArrayList();
			int number = 1;
			String formula_id1 ="";//确定光标定位到哪行的formula_id
			String formula_id2 = "";//删除时选中项最前面那项的formula_id；
			HashMap hmMap = (HashMap) this.getFormHM().get("requestPamaHM");
			String tab_id = (String) hmMap.get("tab_id");
			String flag = (String) hmMap.get("flag");
			String btnreturnvisible=(String)this.getFormHM().get("btnreturnvisible"); //返回按钮是否显示			
			btnreturnvisible = btnreturnvisible!=null&&btnreturnvisible.length()>0?btnreturnvisible:"false";			
			hmMap.remove("flag");
			hmMap.remove("tab_id");
			if(tab_id==null){
				tab_id="0";
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			if("0".equals(tab_id)){
				sql.append("select * from gz_budget_formula order by seq");
			}else{
				sql.append("select * from gz_budget_formula where tab_id = "+tab_id+" order by seq");
			}
			
			this.frowset = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while (this.frowset.next()) {
				bean = new LazyDynaBean();
				String tab_id2 = this.frowset.getString("tab_id");
				String seq = this.frowset.getString("seq");
				String destflag = this.frowset.getString("destflag");
				String extattr = this.frowset.getString("extattr");
				String formulaname = this.frowset.getString("formulaname");
				String formuladcrp = this.frowset.getString("formuladcrp");
				String formulatype = this.frowset.getString("formulatype");
				String formula_id = this.frowset.getString("formula_id");
				String isLine = "1";// 是否显示行范围，1显示0显示
				String isColumn = "1";// 是否显示列范围，1显示0不显示
				String isType = "1";// 公式类别的显示情况，名册的为1，总额的为2，其他的为3
				if ("1".equals(tab_id2)) {
					isLine = "0";
					isColumn = "0";
					isType = "2";
				}
				if ("2".equals(tab_id2)) {
					isLine = "1";
					isColumn = "1";
					isType = "1";
				}
				if ("3".equals(tab_id2)) {
					isLine = "1";
					isColumn = "1";
					// isType = "3";
				}
				if (!"1".equals(tab_id2) && !"2".equals(tab_id2)) {
					isType = "3";
				}
				StringBuffer btsql = new StringBuffer();
				btsql
						.append("select tab_name from gz_budget_tab where tab_id = "
								+ tab_id2 + "");
				RowSet rs = dao.search(btsql.toString());
				while (rs.next()) {
					String tab_name = rs.getString("tab_name");

					bean.set("tab_name", tab_name);
				}
				bean.set("tab_id", tab_id2);
				bean.set("seq", seq);
				bean.set("number", String.valueOf(number));
				bean.set("destflag", destflag);
				bean.set("extattr", extattr);
				bean.set("formulaname", formulaname);
				bean.set("formuladcrp", formuladcrp);
				bean.set("formulatype", formulatype);
				bean.set("formula_id", formula_id);
				bean.set("isLine", isLine);
				bean.set("isColumn", isColumn);
				bean.set("isType", isType);
				list.add(bean);
				number++;
			}
			
			if(flag==null){
				this.getFormHM().put("btnreturnvisible",btnreturnvisible );	
				if (this.frowset.first()){
					cformula_id = this.frowset.getString("formula_id");					
				}
			}else if("init".equals(flag)){
				this.getFormHM().put("btnreturnvisible","false" );	
				if (this.frowset.first()){
					cformula_id = this.frowset.getString("formula_id");					
				}
			}else if("seq".equals(flag)){
				formula_id1 = (String) hmMap.get("formula_id");//调顺序时定位光标
				cformula_id = formula_id1;
			}else if("del".equals(flag)){
				formula_id2 = (String) hmMap.get("formula_id1");//删除时定位光标
				cformula_id = formula_id2;
			}else if("fromdef".equals(flag)){
				this.getFormHM().put("btnreturnvisible","true" );	
				if (this.frowset.first()){
					cformula_id = this.frowset.getString("formula_id");
				}
			}else if("add".equals(flag)){
				sql1.append("select max(formula_id) as aaa from gz_budget_formula");
				this.frowset = dao.search(sql1.toString());
				while (this.frowset.next()) {
					cformula_id = this.frowset.getString("aaa");
				}
			}

			tabsql.append("select tab_id,tab_name from gz_budget_tab order by seq");
			this.frowset = dao.search(tabsql.toString());
			while (this.frowset.next()) {
				CommonData datavo = new CommonData(this.frowset
						.getString("tab_id"), this.frowset
						.getString("tab_name"));
				tablist.add(datavo);
			}

			this.getFormHM().put("list", list);
			this.getFormHM().put("tablist", tablist);
			this.getFormHM().put("count", String.valueOf(number - 1));
			this.getFormHM().put("formuladcrp", "");
			this.getFormHM().put("tab_id", tab_id);
			this.getFormHM().put("formula_id", cformula_id);

		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}

}
