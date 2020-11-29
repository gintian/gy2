package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:SaveBonusParamTrans.java</p>
 * <p>Description:保存奖金参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveBonusParamTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try
		{
    	
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String menuid = (String) hm.get("menuid");
			String paramStr = (String)this.getFormHM().get("paramStr");
		
			ConstantXml xml = new ConstantXml(this.frameconn,"GZ_PARAM","Params");
			if(menuid!=null && "1".equals(menuid))
			{
			    xml.setTextValue("/Params/Bonus/base", paramStr);
			    String jobnum = (String)this.getFormHM().get("jobnum");
			    xml.setTextValue("/Params/Bonus/num", jobnum);
			    String bonusSet = (String)this.getFormHM().get("bonusSet");
			    if(bonusSet==null||bonusSet.trim().length()==0)
			    	throw GeneralExceptionHandler.Handle(new Exception("请选择奖金子集"));
			    xml.setTextValue("/Params/Bonus/setid", bonusSet);
			    xml.saveStrValue();
			}
	
	    
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

    }
}
