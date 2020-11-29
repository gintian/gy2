package com.hjsj.hrms.transaction.gz.tempvar;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class AddTeimVarTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String cstate = (String)reqhm.get("cstate");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
		reqhm.remove("cstate");
		
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		reqhm.remove("flag");
		
		String nflag = (String)reqhm.get("nflag");
		nflag=nflag!=null&&nflag.trim().length()>0?nflag:"";
		reqhm.remove("nflag");
		
		String showflag=(String) reqhm.get("showflag");
		showflag=showflag!=null&&showflag.trim().length()>0?showflag:"0";
		reqhm.remove("showflag");
		
		hm.clear();
		
		hm.put("type",type);
		hm.put("cstate",cstate);
		hm.put("ntype","1");
		hm.put("codelist",codeList());
		hm.put("nflag",nflag);
		hm.put("fiddec","2");
		this.getFormHM().put("showflag", showflag);
		if("conadd".equalsIgnoreCase(flag))
			hm.put("checkclose","close");
	}
	private ArrayList codeList(){
		ArrayList codelist = new ArrayList();
		String sqlstr = "select codesetid,codesetdesc,maxlength from codeset order by codesetid";
		ContentDAO dao  = new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sqlstr);
			while(this.frowset.next()){
				CommonData dataobj = new CommonData(this.frowset.getString("codesetid")+":"+this.frowset.getString("maxlength"),
						this.frowset.getString("codesetid")+":"+this.frowset.getString("codesetdesc"));
				codelist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codelist;
	}
	

}
