package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SeqBudgetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			//先获得参数
			String tab_id = SafeCode.decode((String)this.getFormHM().get("tab_id"));
			tab_id = PubFunc.keyWord_reback(tab_id);
			String seq = SafeCode.decode((String)this.getFormHM().get("seq"));
			seq = PubFunc.keyWord_reback(seq);
			String move = SafeCode.decode((String)this.getFormHM().get("move"));
			move = PubFunc.keyWord_reback(move);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			int newSeq = 0;
			StringBuffer strSql= new StringBuffer();
			if("up".equals(move)){
				strSql.append("select MAX(seq) as newseq from gz_budget_tab where seq <"+seq);
			}else{
				strSql.append("select Min(seq) as newseq from gz_budget_tab where seq >"+seq);
			}
			this.frowset =dao.search(strSql.toString());
			while (this.frowset.next()){
				newSeq =this.frowset.getInt("newseq");
				StringBuffer sb2 = new StringBuffer();
				sb2.append("update gz_budget_tab set seq="+seq+" where seq="+newSeq);
				dao.update(sb2.toString());
				StringBuffer sb1 = new StringBuffer();
				sb1.append("update gz_budget_tab set seq="+newSeq+" where tab_id="+tab_id);
				dao.update(sb1.toString());				
				
			}
			

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
