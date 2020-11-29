package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class AppealSelfInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String flag = (String)this.getFormHM().get("flag");
		String userbase=(String)this.getFormHM().get("userbase");
		String A0100=(String)this.getFormHM().get("a0100");
		if(!"notself".equals(flag)){
			userbase = this.userView.getDbname();
			A0100 = this.userView.getA0100();
		}else{
			CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
			userbase = cps.checkDb(userbase);
			A0100 = cps.checkA0100("",userbase , A0100, "");
		}
		//xuj修正小君越权不兼容员工审批
	    /*//防止越权 zxj  
		
        String userbase = this.userView.getDbname();
        String A0100 = this.userView.getA0100();*/
        
		String setname=(String)this.getFormHM().get("setname");
		String I9999=(String)this.getFormHM().get("i9999");
		String actiontype=(String)this.getFormHM().get("actiontype");
		

		 ContentDAO dao=new ContentDAO(this.getFrameconn());
	     try
	     {
	    	 FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
	    	 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	 		String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
	 		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
	 		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
	 		approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
	 		if("1".equals(inputchinfor)&& "1".equals(approveflag)){
	 			MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView,userbase,A0100);
	 			String chg_id=mysel.getChg_id();
	 			if(StringUtils.isEmpty(chg_id))
	 			    throw new GeneralException("", "子集数据没有改变不需要报批！", "", "");
	 			

	 			if("A01".equalsIgnoreCase(setname)){
	 				I9999=A0100;
	 			}
	 			String sequence ="1";
	 			if("new".equalsIgnoreCase(actiontype)){
	 				if(!"I9999".equalsIgnoreCase(I9999)&&!I9999.equalsIgnoreCase(A0100))
	 					actiontype="insert";
	 				if("I9999".equalsIgnoreCase(I9999))
	 					I9999="-1";
	 			}
	 			mysel.getOtherParamList1(fieldset.getFieldsetid(),I9999);
	 			ArrayList sequenceList = mysel.getSequenceList();
	 			if(sequenceList.size()>0){
	 				sequence =Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+"";
	 			}
	 			
				mysel.updateApployMyselfDataApp(chg_id,fieldset,"02",I9999,actiontype,sequence);
	 		}else{
	 			StringBuffer statesql=new StringBuffer();
	 			statesql.append("update ");
	 			statesql.append(userbase);
	 			statesql.append(setname);
	 			statesql.append(" set state='1' where a0100='");
	 			statesql.append(A0100);
	 			statesql.append("'");
	 			if(!"A01".equalsIgnoreCase(setname))
	 			{
	 				statesql.append(" and i9999=");
	 				statesql.append(I9999);
	 			}
	 			//System.out.println(statesql.toString());
	 			dao.update(statesql.toString());
	 		}
		 }
		 catch(Exception sqle)
		 {
		    sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
	}

}
