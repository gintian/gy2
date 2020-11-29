package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowRelatingcodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		RecordVo t_hr_relatingcode=new RecordVo("t_hr_relatingcode");
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String add_flag = (String)reqhm.get("add_flag");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		BusiSelStr bssr=new BusiSelStr();
		if(reqhm.containsKey("b_action")){
			if(reqhm.containsKey("add")){
//				进入增加关联代码页面
				t_hr_relatingcode.clearValues();
//				查询业务字典各个系统,组成一个list给页面显示
				hm.put("flag","0");
				reqhm.remove("add");	
			}
			if(reqhm.containsKey("update")){
//				进入修改关联代码页面
				reqhm.remove("update");
				String codesetid=(String) reqhm.get("codesetid");
				t_hr_relatingcode.setString("codesetid",codesetid);
				try {
					t_hr_relatingcode=dao.findByPrimaryKey(t_hr_relatingcode);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	
				hm.put("flag","1");
			}
			hm.put("add_flag",add_flag);
			hm.put("systemlist",bssr.getSubsys(dao,null));
			hm.put("t_hr_relatingcode",t_hr_relatingcode);
		}else{
			BusiSQLStr bss=new BusiSQLStr();
			String[] sql=bss.getRelatingcodeSQL();
			hm.put("sql",sql[0]);
			hm.put("where",sql[1]);
			hm.put("column",sql[2]);
			hm.put("orderby",sql[3]);
		}	
		
	}
	

}
