package com.hjsj.hrms.transaction.general.chkformula;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveAddFormula extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String chkflag = (String)this.getFormHM().get("chkflag");
		chkflag=chkflag!=null&&chkflag.trim().length()>0?chkflag:"";
		
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&tabid.trim().length()>0?flag:"";
		
		String name = (String)this.getFormHM().get("name");
		name=name!=null&&name.trim().length()>0?name:"";
		name=SafeCode.decode(name);
		
		String information = (String)this.getFormHM().get("information");
		information=information!=null&&information.trim().length()>0?information:"";
		information=SafeCode.decode(information);
		information = PubFunc.keyWord_reback(information);
		name = PubFunc.keyWord_reback(name);
		String infor = "ok";
		String chkid = "";
		if("add".equalsIgnoreCase(chkflag)|| "addmore".equalsIgnoreCase(chkflag)){
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			chkid=idg.getId("hrpchkformula.chkid");
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("insert into hrpChkformula(chkId,Name,Information,seq,tabid,B0110,flag)");
			sqlstr.append(" values(?,?,?,?,?,?,?)");
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			ArrayList valuelist = new ArrayList();
			valuelist.add(chkid);
			valuelist.add(name);
			valuelist.add(information);
			valuelist.add(seqId(dao,tabid)+"");
			valuelist.add(tabid);
			valuelist.add("UN");
			valuelist.add(flag);
			try {
				dao.update(sqlstr.toString(),valuelist);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("alert".equalsIgnoreCase(chkflag)){
			chkid = (String)this.getFormHM().get("chkid");
			chkid=chkid!=null&&chkid.trim().length()>0?chkid:"";
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update hrpChkformula set Name=?,Information=? where chkid=?");
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			ArrayList valuelist = new ArrayList();

			valuelist.add(name);
			valuelist.add(information);
			valuelist.add(chkid);

			try {
				dao.update(sqlstr.toString(),valuelist);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("chkflag",chkflag);
		this.getFormHM().put("chkid",Integer.parseInt(chkid)+"");
	}
	private int seqId(ContentDAO dao,String tabid){
		int seq=1;
		try {
			this.frowset = dao.search("select max(seq) as seq from hrpChkformula where tabid='"+tabid+"'");
			if(this.frowset.next()){
				seq=this.frowset.getInt("seq");
			}
			seq=seq>0?seq:1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return seq+1;
	}
}
