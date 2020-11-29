package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateMainSetInfoTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		
		String code = (String)this.getFormHM().get("code");
		String codeitemid = (String) this.getFormHM().get("codeitemid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		ContentDAO dao = new ContentDAO(frameconn);
		try{
			
			String codesetid = ConstantParamter.getRealConstantVo("PS_C_CODE").getString("str_value");
			
			if(this.getFormHM().get("codesetid").toString().equals(codesetid)){
				
				FieldSet fs = DataDictionary.getFieldSetVo("H01"); 
				if(fs!=null && "1".equals(fs.getUseflag())){
					    if(code.equals(codesetid))
					    	code="";
						StringBuffer sql = new StringBuffer("insert into H01(h0100,createtime,modtime,createusername,modusername) values( ");
						sql.append(" '"+code+codeitemid+"', ");
						sql.append(Sql_switcher.dateValue(sdf.format(new Date()))+", ");
						sql.append(Sql_switcher.dateValue(sdf.format(new Date()))+", ");
						sql.append(" '"+this.userView.getUserName()+"', ");
						sql.append(" '"+this.userView.getUserName()+"') ");
						dao.update(sql.toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
