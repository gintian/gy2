package com.hjsj.hrms.transaction.param;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 电话邮箱配置
 * @author Owner
 *
 */
public class PhoneParamTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		String fieldcond ="select itemid,itemdesc from fielditem where fieldsetid='A01' and useflag='1' and itemtype='A' and codesetid='0'";
		String email="";
		String phone="";
		String telephone="";
	
		String sql="select str_value from constant where Constant='SS_EMAIL'";
		try{
			//System.out.println("param");
		 /*List smaillist=ExecuteSQL.executeMyQuery(sql,this.getFrameconn());		 
		 if(!smaillist.isEmpty())
		 {
		 	LazyDynaBean rec=(LazyDynaBean)smaillist.get(0);
		 	email=rec.get("str_value")!=null?rec.get("str_value").toString():"";
		 }
		 
		 sql="select str_value from constant where Constant='SS_MOBILE_PHONE'";
		 List phonelist=ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
		 if(!phonelist.isEmpty())
		 {
		 	LazyDynaBean rec=(LazyDynaBean)phonelist.get(0);
		 	phone=rec.get("str_value")!=null?rec.get("str_value").toString():"";
		 }	*/
		 RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL", this.frameconn);
		 if(vo!=null){
			 email=vo.getString("str_value");
			 email = email!=null?email:"";
		 }
		 
		 vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", this.frameconn);
		 if(vo!=null){
			 phone=vo.getString("str_value");
			 phone = phone!=null?phone:"";
		 }
		 
		 vo = ConstantParamter.getConstantVo("SS_TELEPHONE", this.frameconn);
		 if(vo!=null){
			 telephone=vo.getString("str_value");
			 telephone = telephone!=null?telephone:"";
		 }
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		   this.getFormHM().put("email",email);
		   this.getFormHM().put("phone",phone);
		   this.getFormHM().put("fieldcond",fieldcond);
		   this.getFormHM().put("telephone", telephone);
	}
}
