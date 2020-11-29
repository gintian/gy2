package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckIntoClass extends IBusiness {

	public void execute() throws GeneralException {

		String a0100 = (String)this.getFormHM().get("a0100");
		ArrayList a0100list = new ArrayList();
		if(a0100!=null&&a0100.length()>0){
		    String[] a0100s=a0100.split(",");
		    for(int m = 0; m<a0100s.length;m++){
		        if(a0100s[m]!=null&&a0100s[m].length() > 0)
		            a0100list.add(PubFunc.decrypt(SafeCode.decode(a0100s[m])));
		    }
		}
		String f="true";
		String R4002 ="";
		String r3130="";
		ContentDAO dao=new ContentDAO(this.frameconn);
		try{
		for (int i = 0; i < a0100list.size(); i++) {
		    String personid = (String) a0100list.get(i);
			if (personid != null && personid.length() > 0) {
				this.frowset = dao.search("select R4002,R3130 from r40 join r31 on r40.R4005=r31.r3101 where r3127='04' and R4001='"+personid+"'");
				if(this.frowset.next()){
					R4002=this.frowset.getString("R4002");
					r3130=this.frowset.getString("R3130");
					f="false";
				}
			}
			if(!"true".equals(f))
				break;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("f", f);
		this.getFormHM().put("R4002", R4002);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("r3130", r3130);
		}

}
