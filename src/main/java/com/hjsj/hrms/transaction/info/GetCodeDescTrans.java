package com.hjsj.hrms.transaction.info;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetCodeDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a_code=(String)this.getFormHM().get("a_code");
		String codedesc = "";
		try{
			if(a_code.length()>2){
				String codesetid = a_code.substring(0,2);
				if("@K".equals(codesetid)){
					String codeitemid = a_code.substring(2);
					String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')";
					ContentDAO dao = new ContentDAO(this.frameconn);
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						codedesc = this.frowset.getString("codeitemdesc");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("parentdesc", codedesc);
		}
		
	}
	
	
}
