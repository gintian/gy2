package com.hjsj.hrms.transaction.general.sys.options;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchPortalItemAdjustTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	try{
		ArrayList nodeslist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
		/*
		 * 1公告栏 bulletin
		 * 2预警提示 warn
		 * 3常用花名册 muster
		 * 4常用查询  query
		 * 5常用统计分析 stat
		 * 6常用登记表 card
		 * 7常用报表 report
		 * 8代办事宜  matter
		 * */
		ArrayList inforlist=new ArrayList();
		for(int i=0;nodeslist!=null && i<nodeslist.size();i++)
		{
			ArrayList attributelist=(ArrayList)nodeslist.get(i);
			for(int j=0;j<attributelist.size();j++)
			{
				LabelValueView item=(LabelValueView)attributelist.get(j);

				if("id".equals(item.getLabel()) && "1".equals(item.getValue()))
				{
					CommonData vo=new CommonData("1",ResourceFactory.getProperty("system.options.itembulletin"));
					inforlist.add(vo);				
				}
				if("id".equals(item.getLabel()) && "2".equals(item.getValue()))
				{
					CommonData vo=new CommonData("2",ResourceFactory.getProperty("system.options.itemwarn"));
					inforlist.add(vo);		
				}	    			
				if("id".equals(item.getLabel()) && "3".equals(item.getValue()))
				{
					CommonData vo=new CommonData("3",ResourceFactory.getProperty("system.options.itemmuster"));
					inforlist.add(vo);		
				}
				if("id".equals(item.getLabel()) && "4".equals(item.getValue()))
				{
					CommonData vo=new CommonData("4",ResourceFactory.getProperty("system.options.itemquery"));
					inforlist.add(vo);		
				}
				if("id".equals(item.getLabel()) && "5".equals(item.getValue()))
				{
					CommonData vo=new CommonData("5",ResourceFactory.getProperty("system.options.itemstat"));
					inforlist.add(vo);		
				}
				if("id".equals(item.getLabel()) && "6".equals(item.getValue()))
				{
					CommonData vo=new CommonData("6",ResourceFactory.getProperty("system.options.itemcard"));
					inforlist.add(vo);		
				}
				if("id".equals(item.getLabel()) && "7".equals(item.getValue()))
				{
					CommonData vo=new CommonData("7",ResourceFactory.getProperty("system.options.itemreport"));
					inforlist.add(vo);		
				}		
				if("id".equals(item.getLabel()) && "8".equals(item.getValue()))
				{
					CommonData vo=new CommonData("8",ResourceFactory.getProperty("system.options.itemmatter"));
					inforlist.add(vo);		
				}
			}
		}
		            /*ArrayList inforlist=new ArrayList();
					CommonData vo=new CommonData("1",ResourceFactory.getProperty("system.options.itembulletin"));
					inforlist.add(vo);				

					CommonData vo1=new CommonData("2",ResourceFactory.getProperty("system.options.itemwarn"));
					inforlist.add(vo1);		
    	
					CommonData vo2=new CommonData("3",ResourceFactory.getProperty("system.options.itemmuster"));
					inforlist.add(vo2);		

					CommonData vo3=new CommonData("4",ResourceFactory.getProperty("system.options.itemquery"));
					inforlist.add(vo3);		
		
		
					CommonData vo4=new CommonData("5",ResourceFactory.getProperty("system.options.itemstat"));
					inforlist.add(vo4);		
			
	
					CommonData vo5=new CommonData("6",ResourceFactory.getProperty("system.options.itemcard"));
					inforlist.add(vo5);		
		
					CommonData vo6=new CommonData("7",ResourceFactory.getProperty("system.options.itemreport"));
					inforlist.add(vo6);	*/	
		this.getFormHM().put("inforlist",inforlist);
	 }catch(Exception ex){
		  ex.printStackTrace();
	      throw GeneralExceptionHandler.Handle(ex);
     }
   }
}
