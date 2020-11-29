package com.hjsj.hrms.transaction.train.evaluatingStencil;

import com.hjsj.hrms.businessobject.performance.singleGrade.SingGradeTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 展现调查模版
 * @author Owner
 *
 */
public class ShowResearchStencilTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String template_id=(String)hm.get("id");
		String r3101=(String)hm.get("r3101");      //活动编号；
		r3101 = r3101 == null || "".equals(r3101) ? "" : r3101;
		if (!Pattern.matches("\\d+", r3101)) { // 纯数字的话无需解密 lium
			r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
		}
		String enteryType=(String)hm.get("enteryType");
		String isClose=(String)hm.get("isClose");
		String home=(String)hm.get("home");
		try
		{
			String type=(String)hm.get("type");
			String titleName="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select r3130 from r31 where r3101='"+r3101+"'");
			if(this.frowset.next())
			{
				if(this.frowset.getString("r3130")!=null)
					titleName=this.frowset.getString("r3130");
			
			}
			if("job".equalsIgnoreCase(type))
				titleName+="培训班活动评估";
			else
				titleName+="教师评估";
			
			String status="0";
			this.frowset=dao.search("select * from per_template where template_id='"+template_id+"'");
			if(this.frowset.next())
			{
				status=this.frowset.getString("status");				
			}
			
			SingGradeTemplateBo singGradeTemplateBo=new SingGradeTemplateBo(this.getFrameconn(),20);
			singGradeTemplateBo.setR3101(r3101);
			singGradeTemplateBo.setUserView(userView);
			ArrayList list=singGradeTemplateBo.getSingleGradeHtml(template_id,status,this.getUserView().getA0100(),this.getUserView().getA0100(),titleName);		   
			this.getFormHM().put("titleName",titleName);
			this.getFormHM().put("status",status);   // 0:分值  1:权重
		    this.getFormHM().put("gradeHtml",(String)list.get(0));		  
		    this.getFormHM().put("isNull",(String)list.get(2));
		    this.getFormHM().put("scoreflag",(String)list.get(3));
		    String dataArea = (String)list.get(4);
		    if(dataArea != null && dataArea.length() > 0)
		        dataArea = dataArea.substring(1);
		    
		    this.getFormHM().put("dataArea",dataArea);	   
		    this.getFormHM().put("lay",(String)list.get(9));
		    this.getFormHM().put("objectID",this.getUserView().getA0100());
		    this.getFormHM().put("enteryType", enteryType);
		    this.getFormHM().put("isClose", isClose);
		    this.getFormHM().put("home", home);
		    
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
