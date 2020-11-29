package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchHunterUserTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		try{
		String huntergroupid = (String)this.getFormHM().get("huntergroupid");
		huntergroupid = PubFunc.decrypt(huntergroupid);
		ArrayList columns = new ArrayList(); 
		ColumnsInfo usernamec = new ColumnsInfo();
		usernamec.setColumnId("username");
		usernamec.setColumnDesc("帐号");
		usernamec.setColumnWidth(150);
		usernamec.setLocked(true);
		usernamec.setKey(true);
		columns.add(usernamec);
		ColumnsInfo passwordc = new ColumnsInfo();
		passwordc.setColumnId("password");
		passwordc.setColumnDesc("密码");
		passwordc.setColumnWidth(150);
		passwordc.setLocked(true);
		columns.add(passwordc);
		ColumnsInfo namec = new ColumnsInfo();
		namec.setColumnId("name");
		namec.setColumnDesc("姓名");
		namec.setColumnWidth(100);
		namec.setRendererFunc("namerender");
		columns.add(namec);
		ColumnsInfo emailc = new ColumnsInfo();
		emailc.setColumnId("email");
		emailc.setColumnDesc("邮箱");
		emailc.setColumnWidth(180);
		columns.add(emailc);
		ColumnsInfo telc = new ColumnsInfo();
		telc.setColumnId("tel");
		telc.setColumnDesc("办公电话");
		telc.setColumnWidth(120);
		columns.add(telc);
		ColumnsInfo phonec = new ColumnsInfo();
		phonec.setColumnId("phone");
		phonec.setColumnDesc("移动电话");
		phonec.setColumnWidth(120);
		columns.add(phonec);
		ColumnsInfo usedc = new ColumnsInfo();
		usedc.setColumnId("isused");
		usedc.setColumnDesc("启用状态");
		usedc.setColumnWidth(100);
		usedc.setCodesetId("45");
		columns.add(usedc);
		ColumnsInfo leaderc = new ColumnsInfo();
		leaderc.setColumnId("isleader");
		leaderc.setColumnDesc("是否主账号");
		leaderc.setColumnWidth(100);
		leaderc.setCodesetId("45");
		columns.add(leaderc);
		
		
		String constantxml = "recruitment/headhunter";
		
		String sqlstr="select username,password,name,email,tel,phone,isused,isleader from zp_headhunter_login where z6000="+huntergroupid;
	    //ArrayList datalist = (ArrayList) ExecuteSQL.executeMyQuery(sqlstr, frameconn);
	    this.getFormHM().put("sqlstr", sqlstr);
	    this.getFormHM().put("usercolumns", columns);
	    this.getFormHM().put("constantxml", constantxml);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
