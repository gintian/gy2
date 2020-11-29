package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String unit_type = (String)this.getFormHM().get("unit_type");
		unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlstr = "select formula from HRPFormula where FId='"+id+"' and Unit_type='"+unit_type+"'";
		String formula = "";
		try {
			this.frowset=dao.search(sqlstr);
			while(this.frowset.next()){
				formula = this.frowset.getString("formula");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		formula=formula!=null?formula:"";
		this.getFormHM().put("formula",SafeCode.encode(formula));
		
	}

}
