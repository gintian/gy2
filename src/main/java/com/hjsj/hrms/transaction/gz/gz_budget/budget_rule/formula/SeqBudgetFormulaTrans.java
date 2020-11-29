package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SeqBudgetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			//先获得参数
			String formula_id = SafeCode.decode((String)this.getFormHM().get("formula_id"));
			formula_id = PubFunc.keyWord_reback(formula_id);
			String seq = SafeCode.decode((String)this.getFormHM().get("seq"));
			seq = PubFunc.keyWord_reback(seq);
			String move = SafeCode.decode((String)this.getFormHM().get("move"));
			move = PubFunc.keyWord_reback(move);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String tab_id = SafeCode.decode((String) this.getFormHM().get("tab_id"));
			tab_id = PubFunc.keyWord_reback(tab_id);
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			ArrayList list1 = new ArrayList();
			if("0".equals(tab_id)){
				sql.append("select formula_id,seq from gz_budget_formula order by seq");
			}else{
				sql.append("select formula_id,seq from gz_budget_formula where tab_id = "+tab_id+" order by seq ");
			}
			
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				String a = this.frowset.getString("formula_id");
				String c = this.frowset.getString("seq");
				list.add(a);
				list1.add(c);//把formulaid和seq封装到不同的list容器中，在两个list中是一一对应的
			}
			String b = "";
			int newSeq = 0;
			if("up".equals(move)){
				for(int i=0;i<list.size();i++){
					if(formula_id.equals(list.get(i))){//判断选中行所在的list中的哪个位置
						b = (String) list1.get(i-1);//对应的另一个list中的位置的上一个位置即是选中行的上一行
						newSeq = Integer.parseInt(b);
					}
				}
			}else{
				for(int i=0;i<list.size();i++){
					if(formula_id.equals(list.get(i))){
						b = (String) list1.get(i+1);
						newSeq = Integer.parseInt(b);
					}
				}
			}
			StringBuffer sb2 = new StringBuffer();
			sb2.append("update gz_budget_formula set seq="+seq+" where seq="+newSeq);
			dao.update(sb2.toString());
			StringBuffer sb1 = new StringBuffer();
			sb1.append("update gz_budget_formula set seq="+newSeq+" where formula_id="+formula_id);
			dao.update(sb1.toString());
			this.getFormHM().put("formula_id", formula_id);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
