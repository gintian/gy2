package com.hjsj.hrms.transaction.kq.register.empchange;


import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;


public class EmpChangeSelectDateTrans extends IBusiness{
	public void execute()throws GeneralException	   
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	    String userbase = (String) hm.get("userbase");
   	    String a0100=(String)hm.get("a0100");	   
   	    String changestatus= (String)hm.get("changestatus");   	    
		String b0110=(String)hm.get("b0110");			
   	    ArrayList datelist= RegisterDate.registerdate(b0110,this.getFrameconn(),this.userView);   
   	    String workcalendar=getDateSelectHtml(datelist,"");
   	    this.getFormHM().put("userbase",userbase);
   	    this.getFormHM().put("a0100",a0100);
   	    this.getFormHM().put("datelist",datelist);
   	    this.getFormHM().put("workcalendar",workcalendar);
   	    this.getFormHM().put("changestatus",changestatus);
	}
	 /**
     * 考勤日期的下拉菜单
     * @param datelist 当前考勤期间的所有日期 
     *        CommonData 
     *        getDataValue  值
     *        getDataName  显示
     * @param  registerdate 当前日期    
     * @return 
     *        select的html代码   
     */
    public static String getDateSelectHtml(ArrayList datelist,String registerdate)
    {
    	StringBuffer selecthtml= new StringBuffer();
    	selecthtml.append("<select name='curdate' size='1'>");
    	if(registerdate==null||registerdate.length()<=0)
		{
    		CommonData vo=(CommonData)datelist.get(0);
			registerdate=vo.getDataValue();
		}
    	String rest_state=ResourceFactory.getProperty("kq.date.work");
    	for(int i=0;i<datelist.size();i++)
    	{
    		CommonData vo=(CommonData)datelist.get(i);
    		String style="";    		
    		if(vo.getDataName().indexOf(rest_state)!=-1)
    		{
    			style="style='COLOR: #000000'";
    		}else
    		{
    			style="style='COLOR: #FF0000'";
    		}
    		if(registerdate.equals(vo.getDataValue().trim()))
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+" selected='selected'>");
    		}else
    		{
    			selecthtml.append("<option value="+vo.getDataValue()+" "+style+">");
    		}
    		selecthtml.append(vo.getDataName());
    		selecthtml.append("</option>");
    	}
    	selecthtml.append("</select> ");
    	return selecthtml.toString();
    }
}
