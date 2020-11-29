package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.OperationSQLStr;
import com.hjsj.hrms.businessobject.general.operation.TwfdefineBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class UpdateFixTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo t_wf_defineVo=new RecordVo("t_wf_define");
		String selstr="";
		TwfdefineBo twf=new TwfdefineBo();
		if(reqhm.containsKey("tabid")){		
			String tabid=(String) reqhm.get("tabid");
			t_wf_defineVo.setString("tabid",tabid);
			try {
				t_wf_defineVo=dao.findByPrimaryKey(t_wf_defineVo);
				selstr=OperationSQLStr.getOperationname(dao,t_wf_defineVo.getString("operationcode"),"1");
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				twf.paraxml(t_wf_defineVo.getString("ctrl_para"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String flag=t_wf_defineVo.getString("flag");
//			String sbch=OperationSQLStr.getvalideflag(flag);
			hm.put("validateflag",flag);
			hm.put("inputurl",twf.getEdit_form());
			hm.put("appurl",twf.getAppeal_form());
			hm.put("selstr",selstr);
			hm.put("t_wf_defineVo",t_wf_defineVo);
			hm.put("uflag","1");
			hm.put("edit_param", twf.getEdit_param());
			hm.put("appeal_param", twf.getAppeal_param());
			reqhm.remove("tabid");
		}else{
			t_wf_defineVo=(RecordVo) hm.get("t_wf_defineVo");
			String inputurl=(String) hm.get("inputurl");
			String appurl=(String) hm.get("appurl");
			String validateflag=(String) hm.get("validateflag");
			String[] inputname=(String[])hm.get("inputname");
			String[] inputparam=(String[])hm.get("inputparam");
			String[] appname=(String[])hm.get("appname");
			String[] appparam=(String[])hm.get("appparam");
			if("on".equals(validateflag)){
				validateflag="1";
			}else{
				validateflag="0";
			}
			t_wf_defineVo.setString("flag",validateflag);
			String ctrl_para="";
			try {
				ctrl_para=twf.updatectrl_para(t_wf_defineVo.getString("ctrl_para"),inputurl,appurl);
				ctrl_para=twf.updatectrl_Formpara(ctrl_para,inputname,inputparam,appname,appparam);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			t_wf_defineVo.setString("ctrl_para",ctrl_para);
			try {
				dao.updateValueObject(t_wf_defineVo);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
