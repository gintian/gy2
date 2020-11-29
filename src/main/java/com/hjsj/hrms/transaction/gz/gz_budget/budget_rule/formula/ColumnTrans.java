package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaXmlBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

public class ColumnTrans extends IBusiness {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String formula_id = (String) this.getFormHM().get("formula_id");
			BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn,formula_id);
			String colrange=XmlBo.getColrange();
			String[] Colrange=colrange.split(",");//获取xml文件中的列范围内容
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			String s="";
			s="nbase,beginmonth,A0100,A0101,E0122,sc010,b0110,budget_id,itemid,itemdesc,tab_id".toLowerCase();
			sql.append("select tab_id from gz_budget_formula where formula_id = "
							+ formula_id + "");
			this.frowset = dao.search(sql.toString());

			while (this.frowset.next()) {

				String tab_id = this.frowset.getString("tab_id");
				if ("3".equals(tab_id)|| "2".equals(tab_id)) {
					StringBuffer sqll = new StringBuffer();
					if ("3".equals(tab_id))
					   sqll.append("select itemid,itemdesc from t_hr_busifield where fieldsetid = 'SC02' and useflag =1 order by displayid");
					else
						sqll.append("select itemid,itemdesc from t_hr_busifield where fieldsetid = 'SC01' and useflag =1  order by displayid");	
					RowSet rs = dao.search(sqll.toString());

					while (rs.next()) {
						LazyDynaBean bean = new LazyDynaBean();
						String itemid = rs.getString("itemid");
						if(s.indexOf(itemid.toLowerCase())>=0){
							continue;
						}
						String itemdesc = rs.getString("itemdesc");
						int flag = 1;
						for(int i = 0;i<Colrange.length;i++){//用数据库中的列范围内容与可选列范围比对，用来返回前台，得知是否已选
							if(itemid.equals(Colrange[i])){
								flag = 0;
							}
						}
						bean.set("itemdesc", itemdesc);
						bean.set("itemid", itemid);
						bean.set("formula_id", formula_id);
						bean.set("flag", Integer.valueOf(flag));
						list.add(bean);
					}
				} else if (!"1".equals(tab_id)) {
					StringBuffer sqll = new StringBuffer();
					sqll
							.append("select itemid,itemdesc from t_hr_busifield where fieldsetid = 'SC03' and useflag =1  order by displayid");
					RowSet rs = dao.search(sqll.toString());
					while (rs.next()) {
						LazyDynaBean bean = new LazyDynaBean();
						String itemdesc = rs.getString("itemdesc");
						String itemid = rs.getString("itemid");
						if(s.indexOf(itemid.toLowerCase())>=0){
							continue;
						}
						int flag = 1;
						for(int i = 0;i<Colrange.length;i++){//用数据库中的列范围内容与可选列范围比对，用来返回前台，得知是否已选
							if(itemid.equals(Colrange[i])){
								flag = 0;
							}
						}
						bean.set("itemdesc", itemdesc);
						bean.set("itemid", itemid);
						bean.set("formula_id", formula_id);
						bean.set("flag", Integer.valueOf(flag));
						list.add(bean);
					}
				}
			}
			this.getFormHM().put("list", list);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
}
