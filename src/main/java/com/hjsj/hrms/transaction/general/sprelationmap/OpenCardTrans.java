package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * @author lizw
 *
 */
public class OpenCardTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String param=SafeCode.decode(PubFunc.keyWord_reback((String)map.get("param")));
            String infokind="1";/*1人员登记表2单位登记表4职位登记表5计划登记表*/
            String tabid="";
            String nbase="";
            String objid="";
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
            if("UN".equalsIgnoreCase(param.substring(0, 2))|| "UM".equalsIgnoreCase(param.substring(0, 2))){
            	infokind="2";
            	tabid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
            	objid=param.substring(2);
            }else if("@K".equalsIgnoreCase(param.substring(0, 2))){
            	infokind="4";
            	tabid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
            	objid=param.substring(2);
            }else{
            	tabid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
            	nbase=param.substring(0, 3);
            	objid=param.substring(3);
            }
			this.getFormHM().put("nbase",nbase);
			this.getFormHM().put("objid",objid);
			this.getFormHM().put("tabid",tabid);
			this.getFormHM().put("infokind", infokind);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
