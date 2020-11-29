package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
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
public class EditTempVarTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String cstate = (String)reqhm.get("cstate");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
		reqhm.remove("cstate");
		
		String id = (String)reqhm.get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		reqhm.remove("id");
		
		String showflag=(String) reqhm.get("showflag");
		showflag=showflag!=null&&showflag.trim().length()>0?showflag:"0";
		reqhm.remove("showflag");
		
		String sqlstr = "select * from midvariable where nid="+id+"";
		String codesetid="";
		
		try {
			RowSet rs = dao.search(sqlstr);
			if(rs.next()){
				hm.put("tempvarname",rs.getString("chz"));
				hm.put("ntype",rs.getInt("ntype")+"");
				hm.put("fidlen",rs.getInt("fldlen")+"");
				hm.put("fiddec",rs.getInt("flddec")+"");
				codesetid = rs.getString("codesetid");
				hm.put("codesetid",codesetid+":"+rs.getInt("fldlen"));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TempvarBo tempvarbo = new TempvarBo();
		hm.put("codeset",tempvarbo.codeTozh(this.frameconn,codesetid));
		
		hm.put("nid",id);
		hm.put("type",type);
		hm.put("cstate",cstate);
		hm.put("codelist",codeList());
		this.getFormHM().put("showflag", showflag);
	}
	private ArrayList codeList(){
		ArrayList codelist = new ArrayList();
		String sqlstr = "select codesetid,codesetdesc,maxlength from codeset";
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
