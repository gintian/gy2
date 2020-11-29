package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 8, 2007:12:38:59 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetFieldsTrans   extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList list =new ArrayList();
		try {//zgd 2014-7-15 系统参数、其他参数内的人员照片浏览指标集
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String photo_other_itemid=sysbo.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);//显示照片显示其他子集
			photo_other_itemid=photo_other_itemid!=null&&photo_other_itemid.length()>0?photo_other_itemid:"";
			if(photo_other_itemid!=null&&photo_other_itemid.length()>0)
			{
				String[] pitems=photo_other_itemid.split(",");
				if(pitems==null||pitems.length==0)
				{
					return;
				}
				FieldItem item=null;
				CommonData dataobj =null;
				for(int i=0;i<pitems.length;i++)
				{
					String itemid=pitems[i];
					if(itemid==null|| "".equals(itemid))
						continue;
					item=DataDictionary.getFieldItem(itemid.toUpperCase());
					if(item!=null)
					{
						dataobj=new CommonData();
						dataobj.setDataName(item.getItemdesc());
						dataobj.setDataValue(item.getItemid());
						list.add(dataobj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("photoitemlist",list);
		/*String field_falg=(String)this.getFormHM().get("field_falg");
		if(field_falg==null||field_falg.length()<=0)
			field_falg="output";
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());	
		if(field_falg.equals("output"))
		{
			getOutput(leadarParamXML);
		}else if(field_falg.equals("display"))
		{
			getDisplay(leadarParamXML);
		}else if("condi_display".equals(field_falg)){
			getCondi_Display(leadarParamXML);
		}*/
	}
	public void getOutput(LeadarParamXML leadarParamXML)
	{
			
		String output_field=leadarParamXML.getTextValue(LeadarParamXML.OUTPUT);		
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList list=leaderParam.getFields(output_field);
		this.getFormHM().put("itemlist",list);
	}
	public void getDisplay(LeadarParamXML leadarParamXML)
	{
		String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);			
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList list=leaderParam.getFields(display_field);
		this.getFormHM().put("itemlist",list);
	}
	public void getCondi_Display(LeadarParamXML leadarParamXML)
	{
		String display_field=leadarParamXML.getTextValue(LeadarParamXML.CONDI_DISPLAY);			
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList list=leaderParam.getFields(display_field);
		this.getFormHM().put("itemlist",list);
	}
}
