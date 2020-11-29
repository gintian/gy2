package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class PishiTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		String checkflag = "";
		if(reqhm!=null){
			checkflag=(String)reqhm.get("checkflag");
			checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
			reqhm.remove("checkflag");
		}else{
			checkflag=(String)this.getFormHM().get("checkflag");
			checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
		}
		
		String sp_idea = "";
		String chg_id = "";

		if("search".equalsIgnoreCase(checkflag)){
			chg_id=(String)reqhm.get("chg_id");
			if(chg_id != null && chg_id.length() > 0)
			    chg_id = PubFunc.decrypt(chg_id);
			
			chg_id=chg_id!=null&&chg_id.trim().length()>0?chg_id:"";
			reqhm.remove("chg_id");
			sp_idea=searchIdea(chg_id);
			this.getFormHM().put("sp_idea",sp_idea);
		}else if("update".equalsIgnoreCase(checkflag)){
			chg_id=(String)this.getFormHM().get("chg_id");
			chg_id=chg_id!=null&&chg_id.trim().length()>0?chg_id:"";
			if(chg_id != null && chg_id.length() > 0)
                chg_id = PubFunc.decrypt(chg_id);
			
			sp_idea = (String)this.getFormHM().get("sp_idea");
			sp_idea=sp_idea!=null&&sp_idea.trim().length()>0?sp_idea:"";
			sp_idea=SafeCode.decode(sp_idea);
			MyselfDataApprove self = new MyselfDataApprove(this.getFrameconn(), this.userView);
			String org = this.setOrgInfo(userView.getDbname(), userView.getA0100(), this.getFrameconn());
			//zxj 20160613 【19018】
            if("///".equals(org)){
                org = userView.getUserFullName();
            }
			self.updateSp_idea(chg_id, sp_idea, org);
//			updateIdea(chg_id,sp_idea);
		}else if("check".equalsIgnoreCase(checkflag)){
			chg_id=(String)reqhm.get("chg_id");
			chg_id=chg_id!=null&&chg_id.trim().length()>0?chg_id:"";
			if(chg_id != null && chg_id.length() > 0)
                chg_id = PubFunc.decrypt(chg_id);
			
			reqhm.remove("chg_id");
			sp_idea=searchIdea(chg_id);
			this.getFormHM().put("sp_idea",sp_idea);
		}
		this.getFormHM().put("chg_id",PubFunc.encrypt(chg_id));
		this.getFormHM().put("checkflag",checkflag);
	}
	private String searchIdea(String chg_id){
		String sp_idea = "";
		String sqlstr = "select sp_idea from t_hr_mydata_chg where chg_id='"+chg_id+"'";
		ContentDAO dao  = new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sqlstr);
			while(this.frowset.next()){
				sp_idea=this.frowset.getString("sp_idea");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(sp_idea == null || sp_idea.length() <= 0) {
			sp_idea = "";
		}
		if (sp_idea.contains("<?xml ")) {
			MyselfDataApprove self = new MyselfDataApprove(this.frameconn,this.userView);
			sp_idea = self.queryBackidea(sp_idea);
		}
		return sp_idea;
	}
	private void updateIdea(String chg_id,String sp_idea){
		String sqlstr = "update t_hr_mydata_chg set sp_idea='"+sp_idea+"' where chg_id="+chg_id;
		ContentDAO dao  = new ContentDAO(this.frameconn);
		try {
			dao.update(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得“单位/部门/职位/姓名”形式字符窜
	 * 
	 * @param userbase
	 * @param A0100
	 * @param dao
	 */
	private String setOrgInfo(String userbase, String A0100,
			Connection connection) {
		ContentDAO dao = new ContentDAO(connection);
		StringBuffer strsql = new StringBuffer();
		StringBuffer name = new StringBuffer();
		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		String a0101 = "";
		try {
			if (userbase != null && userbase.length() > 0 && A0100 != null
					&& A0100.length() > 0) {
				strsql.append("select b0110,e0122,e01a1,a0101 from ");
				strsql.append(userbase);
				strsql.append("A01 where a0100='");
				strsql.append(A0100);
				strsql.append("'");
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) {
					b0110 = this.getFrowset().getString("B0110");
					e0122 = this.getFrowset().getString("E0122");
					e01a1 = this.getFrowset().getString("E01A1");
					a0101 = this.getFrowset().getString("a0101");
				}
			}
		} catch (Exception e) {

		} finally {
			if (b0110 != null && b0110.trim().length() > 0)
				b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode
						.getCode("UN", b0110).getCodename() : " ";
			if (e0122 != null && e0122.trim().length() > 0)
				e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode
						.getCode("UM", e0122).getCodename() : " ";
			if (e01a1 != null && e01a1.trim().length() > 0)
				e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode
						.getCode("@K", e01a1).getCodename() : " ";
			if (a0101 != null && a0101.trim().length() > 0)
				a0101 = a0101 != null ? a0101 : " ";
		}
		
		if (b0110 == null) {
			name.append("");
		} else {
			name.append(b0110);
		}		
		name.append("/");
		if (e0122 == null) {
			name.append("");
		} else {
			name.append(e0122);
		}
		name.append("/");
		if (e01a1 == null) {
			name.append("");
		} else {
			name.append(e01a1);
		}
		name.append("/");
		if (a0101 == null) {
			name.append("");
		} else {
			name.append(a0101);
		}

		return name.toString();
	}

}
