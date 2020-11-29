package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetA0100Trans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		String msg="ok";
		String newa0100="";
		String nbase="";
		try{
			String uniqueitem=(String)this.getFormHM().get("uniqueitem");
			if(uniqueitem==null||uniqueitem.length()<1){
				msg="error";
				return;
			}
			String a0100=(String)this.getFormHM().get("a0100");
			if(a0100==null||a0100.length()<1){
				msg="error";
				return;
			}
			ArrayList dblist = userView.getPrivDbList();
			String codesetid = userView.getManagePrivCode();
			String codeitemid = userView.getManagePrivCodeValue();
			int size = dblist.size();
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			for(int i=0;i<size;i++){
				nbase = (String)dblist.get(i);
				sql.setLength(0);
				sql.append("select a0100 from "+nbase+"A01 where "+uniqueitem+"='"+a0100+"'");
				if("@K".equalsIgnoreCase(codesetid)){
					sql.append(" and (e01a1 like '"+codeitemid+"%' or e01a1 is null)");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sql.append(" and (e0122 like '"+codeitemid+"%' or e0122 is null)");
				}else{
					sql.append(" and (b0110 like '"+codeitemid+"%' or b0110 is null)");
				}
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next()){
					newa0100 = this.frowset.getString("a0100");
					break;
				}
			}
			if(newa0100.length()==0){
				msg="在您的权限范围内不能查看此人当前信息!";
			}
		}catch(Exception e){
			msg="error";
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("a0100", newa0100);
			this.getFormHM().put("nbase", nbase);
		}
	}

}
