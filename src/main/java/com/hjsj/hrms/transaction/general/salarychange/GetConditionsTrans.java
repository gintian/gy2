package com.hjsj.hrms.transaction.general.salarychange;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class GetConditionsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		String cfactor = "";
		String formula = "";
		if(tableid.length()>0&&id.length()>0){
			String sqlstr = "select formula,cfactor from gzAdj_formula where tabid="+tableid+" and id="+id;
			ContentDAO dao=new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sqlstr);
				if(this.frowset.next()){
					formula = this.frowset.getString("formula");
					cfactor = this.frowset.getString("cfactor");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("formula",SafeCode.encode(formula));
		this.getFormHM().put("cfactor",SafeCode.encode(cfactor));
	}
}
