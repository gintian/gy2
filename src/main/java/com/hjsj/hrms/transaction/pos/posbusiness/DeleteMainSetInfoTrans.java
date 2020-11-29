package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteMainSetInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(frameconn);
		try{
			
			String codesetid = ConstantParamter.getConstantVo("PS_C_CODE").getString("str_value");
			ArrayList selectedList = (ArrayList)this.formHM.get("selectedlist");
			ArrayList itemidArr = new ArrayList();
			if(codesetid!=null&&this.getFormHM().get("codesetid")!=null&&codesetid.equals(this.getFormHM().get("codesetid").toString())){
				String sql = "delete H01 where h0100 like ?";
						FieldSet fs = DataDictionary.getFieldSetVo("H01"); 
						if(fs!=null && "1".equals(fs.getUseflag())){
						   for(int i=0;i<selectedList.size();i++){
							   RecordVo vo = (RecordVo)selectedList.get(i);
							   sql = "delete H01 where h0100 like '"+vo.getString("codeitemid")+"%' ";
							   dao.update(sql);
						   }
						}
						
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
