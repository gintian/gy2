package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:CheckFieldTrans.java</p>
 * <p>Description:校验考核指标</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2011-08-06</p>
 * @author JinChunhai
 * @version 5.0
 */

public class CheckFieldTrans extends IBusiness
{

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException 
	{		
		try
		{			
			KhFieldBo bo = new KhFieldBo(this.getFrameconn(),this.userView);
			String msg = "1";
			String point_id = (String)this.getFormHM().get("fieldnumber");
			CheckPrivSafeBo cpbo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean temp = bo.isHaveThisField(point_id);
			if(temp){
				boolean _flag = cpbo.isHaveItemPriv(point_id);
				if(!_flag){
					return;
				}
			}
			String type=(String)this.getFormHM().get("type");
			String hiddenpointid = (String)this.getFormHM().get("hiddennumber");
			String saveandcontinue = (String)this.getFormHM().get("saveandcontinue");
			String gradeids=(String)this.getFormHM().get("ids");
			
			boolean flag=true;
			if(!("2".equals(type)&&point_id.equals(hiddenpointid)))
			{
				if(temp)
				{
					flag=false;
					msg=ResourceFactory.getProperty("kh.field.field_renum"); 
				}
			}
			if("1".equals(msg))
			{
				if(gradeids==null||gradeids.trim().length()<=0)
				{
					msg="指标必须包含标度！";
				}
			}
			if("1".equals(msg))
			{
		    	if(flag)
	    		{
		    		String subsys_id = (String)this.getFormHM().get("subsys_id");
	    			msg=bo.hasThisGrade(gradeids,subsys_id);
	    		}
			}
			
			// 校验定义的指标公式
			if("1".equals(msg))
			{				
				String formula = (String) this.getFormHM().get("formula");
				formula = formula != null && formula.trim().length() > 0 ? formula : "";
				formula = SafeCode.decode(formula);	
				formula = PubFunc.keyWord_reback(formula);
				msg = bo.testformula(formula);
				if("ok".equalsIgnoreCase(msg) || "noHave".equalsIgnoreCase(msg))
					msg = "1";
			}
			
			msg=SafeCode.encode(msg);
			this.getFormHM().put("msg",msg);
			this.getFormHM().put("type",type);
			this.getFormHM().put("saveandcontinue",saveandcontinue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
