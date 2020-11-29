package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;


public class DeleteFormula extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
        ContentDAO dao=new ContentDAO(this.getFrameconn());  
        try
        {	
        	HashMap hm = this.getFormHM();
        	String formula_id = "";
    		String ids = SafeCode.decode((String)this.getFormHM().get("ids"));
    		ids = PubFunc.keyWord_reback(ids);
    		String tab_id = SafeCode.decode((String)this.getFormHM().get("tab_id"));
    		tab_id = PubFunc.keyWord_reback(tab_id);
    		String[] aa = ids.split(",");
    		formula_id = aa[aa.length-1];//选中多条待删除公式中的最后一条的id
    		String b = "";//光标该定位的那一行的formula_id
			StringBuffer sb_delete = new StringBuffer();
			StringBuffer delsql = new StringBuffer();
			StringBuffer delsql1 = new StringBuffer();
			if("0".equals(tab_id)){
			delsql.append("select formula_id from gz_budget_formula where seq = (select min(seq) from gz_budget_formula where seq > (select seq from gz_budget_formula where formula_id = "+formula_id+") and formula_id not in ("+ids+"))");
			RowSet rs = dao.search(delsql.toString());//为了删除完后光标定位到删除项的下一行，所以在这查出下一行的id
			if(rs.next()){	
				b = rs.getString("formula_id");			
			}else {
				delsql1.append("select formula_id from gz_budget_formula where seq = (select max(seq) from gz_budget_formula where seq < (select seq from gz_budget_formula where formula_id = "+formula_id+") and formula_id not in ("+ids+"))");
				this.frowset = dao.search(delsql1.toString());//如果删除的是最后一行，那么没下一行，则定位到倒数第二行
				while (this.frowset.next()){
					b = this.frowset.getString("formula_id");
				}
			}
			}else{
				delsql.append("select formula_id from gz_budget_formula where seq = (select min(seq) from gz_budget_formula where seq > (select seq from gz_budget_formula where formula_id = "+formula_id+") and formula_id not in ("+ids+") and tab_id = "+tab_id+")");
				RowSet rs = dao.search(delsql.toString());//为了删除完后光标定位到删除项的下一行，所以在这查出下一行的id
				if(rs.next()){	
					b = rs.getString("formula_id");			
				}else {
					delsql1.append("select formula_id from gz_budget_formula where seq = (select max(seq) from gz_budget_formula where seq < (select seq from gz_budget_formula where formula_id = "+formula_id+") and formula_id not in ("+ids+") and tab_id = "+tab_id+")");
					this.frowset = dao.search(delsql1.toString());//如果删除的是最后一行，那么没下一行，则定位到倒数第二行
					while (this.frowset.next()){
						b = this.frowset.getString("formula_id");
					}
				}
			}
			sb_delete.append("delete from gz_budget_formula where formula_id in ("+ids+")");
			dao.delete(sb_delete.toString(), new ArrayList());
			hm.put("formula_id1", b);
			hm.put("tab_id", tab_id);
        }
	    catch(Exception sqle)
	    {
	         sqle.printStackTrace();
	         throw GeneralExceptionHandler.Handle(sqle); 
	    }
	}

}
