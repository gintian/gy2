/**
 * 
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * @author Owner
 *
 */
public class LoadCalculateInfoFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		   OtherParam param=new OtherParam(this.getFrameconn());
		   Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
		   if(setmap==null)
			   return;
		   if("true".equalsIgnoreCase(setmap.get("valid").toString()))
		   {
		     this.getFormHM().put("idcardfield", setmap.get("src").toString().toLowerCase());
		     this.getFormHM().put("birthdayfield", setmap.get("birthday").toString().toLowerCase());
		     this.getFormHM().put("agefield", setmap.get("age").toString().toLowerCase());
		     this.getFormHM().put("axfield", setmap.get("ax").toString().toLowerCase());
		   }else
		   {
		       Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(this.getFrameconn());
		       //身份证指标
		       String chk = othparam.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); 
		       //身份证验证是否启用 
		       String chkvalid = othparam.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
		       if("1".equals(chkvalid))
		           this.getFormHM().put("idcardfield", chk);
		       else
		           this.getFormHM().put("idcardfield", "");
		       
			   this.getFormHM().put("birthdayfield", "");
			   this.getFormHM().put("agefield", ""); 
		   }
		   setmap=param.serachAtrr("/param/formual[@name=\"bywork\"]");
		   if("true".equalsIgnoreCase(setmap.get("valid").toString()))
		   {
			   //System.out.println(setmap.get("src").toString().toLowerCase());
		       this.getFormHM().put("workdatefield", setmap.get("src").toString().toLowerCase());
		       this.getFormHM().put("workagefield", setmap.get("dest").toString().toLowerCase());
		   }
		   else
		   {
			   this.getFormHM().put("workdatefield", "");
		       this.getFormHM().put("workagefield", "");
		   }
		   setmap=param.serachAtrr("/param/formual[@name=\"byorg\"]");
		   if("true".equalsIgnoreCase(setmap.get("valid").toString()))
		   {
		 	   this.getFormHM().put("startpostfield", setmap.get("src").toString().toLowerCase());
		       this.getFormHM().put("postagefield", setmap.get("dest").toString().toLowerCase());
		   }else
		   {
			   this.getFormHM().put("startpostfield", "");
		       this.getFormHM().put("postagefield", "");
		   }
		}catch(Exception e)
		{
			e.printStackTrace();			
		}

	}

}

