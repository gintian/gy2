package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitCollectRegisterTrans extends IBusiness{
    public void execute()throws GeneralException{
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");	
		String kind=(String)hm.get("kind");
		String code = (String)hm.get("code");   
		if(code==null||code.length()<=0){
			code="";    			
		}
		String b0110="";
		String code_kind="";
		if(kind==null||kind.length()<=0)
		{
			kind=RegisterInitInfoData.getKindValue(kind,this.userView);
			code="";
		}
		if(code.length()<=0){
			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			b0110=managePrivCode.getUNB0110();  
			
		}else{
			if("2".equals(kind))
    		{
				b0110="UN"+code;
    		}else
    		{
    			code_kind=RegisterInitInfoData.getDbB0100(code,kind,this.getFormHM(),this.userView,this.getFrameconn());
    			b0110="UN"+code_kind;
    		}
			
				
		}  		
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);		
		ArrayList datelist=(ArrayList) this.getFormHM().get("datelist");
		if(datelist==null||datelist.size()<=0)
		{
			datelist =RegisterDate.registerdate(b0110,this.getFrameconn(),this.userView); 
		}
		this.getFormHM().put("showtypelist",RegisterInitInfoData.getShowType());
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
		this.getFormHM().put("kq_dbase_list",kq_dbase_list); 
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);

	}

}
