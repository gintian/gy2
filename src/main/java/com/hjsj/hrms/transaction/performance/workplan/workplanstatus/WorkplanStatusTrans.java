package com.hjsj.hrms.transaction.performance.workplan.workplanstatus;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.businessobject.performance.workplan.WorkPlanStatusBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * <p>Title:WorkplanStatusTrans.java</p>
 * <p>Description:填报状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-07-09 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkplanStatusTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			WorkPlanStatusBo sbo = new WorkPlanStatusBo(this.getFrameconn(),this.getUserView());
			String codeid = (String)hm.get("codeid");
			String opt = (String)hm.get("opt"); // opt=1:从menu.xml进入  opt=2:是点击链接查找下级 opt=3:是点击返回按钮
			hm.remove("codeid");
			hm.remove("opt");
			if(codeid==null || codeid.trim().length()<=0)
				codeid = (String)this.getFormHM().get("codeid");
							
			String cycle = (String)this.getFormHM().get("cycle"); // 日志类型 cycle 0:日报 1:周报 2:月报 3:季报 4:年报
			if(cycle==null || cycle.trim().length()<=0)
				cycle = "1";			
			String year = (String)this.getFormHM().get("year");
			if(year==null || year.trim().length()<=0)
				year = Calendar.getInstance().get(Calendar.YEAR)+"";
			String month = (String)this.getFormHM().get("month");
			if(month==null || month.trim().length()<=0)
				month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";			
			String type = (String)this.getFormHM().get("type");
			if(type==null || type.trim().length()<=0){
				type = "1";
				ArrayList<CommonData> typeList = sbo.getTypeList();
				if(typeList!=null){
					typeList.get(0);
					type = typeList.get(0).getDataValue();
				}
			}
			
			// 取得登录用户范围内的填报单位信息
			String quarter = (String)this.getFormHM().get("quarter");
			if(quarter==null || quarter.trim().length()<=0)
				quarter = sbo.getSeason(month); // 当前是哪个季度
			
			// 初始化
//			if(opt!=null && opt.trim().length()>0 && opt.equals("1"))
//			{
//				cycle = "2";	
//				year = Calendar.getInstance().get(Calendar.YEAR)+"";								
//				month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";	
//				quarter = sbo.getSeason(month); // 当前是哪个季度
//				type = "1";
//			}						
			
			String unitName = (String)this.getFormHM().get("unitName");
			LinkedHashMap codeitemidMap = new LinkedHashMap();
			if("init".equals(codeid))
			{				
				unitName = sbo.getUnitOrE0122Name("");				
				codeitemidMap = sbo.getUnitStatusList("",cycle,year,quarter,month,type); // 获得填报状态数据
			}
			else
			{
				if(opt!=null && opt.trim().length()>0 && "1".equals(opt))
					unitName = sbo.getUnitOrE0122Name("");
				else if(opt!=null && opt.trim().length()>0 && ("2".equals(opt) || "3".equals(opt)))
					unitName = sbo.getUnitOrE0122Name(codeid);
				
				codeitemidMap = sbo.getUnitStatusList(codeid,cycle,year,quarter,month,type); // 获得填报状态数据
			}
		//	if(codeitemidMap==null || codeitemidMap.size()<=0)
		//		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));			
			
			ArrayList cycleDaysList = sbo.getCycleDaysList(cycle,year,quarter,month,"1");
			String theadHtml = sbo.getTheadHtml(cycleDaysList);
			ArrayList cycleDayList = sbo.getCycleDaysList(cycle,year,quarter,month,"2");
			String tabBodyHtml = sbo.getTableBodyHtml(codeitemidMap,cycleDayList);
			String tableHtml = theadHtml+tabBodyHtml;        //得到表头和标题
			
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn());
			bo.analyseParameter(); // 刚进来时，加载一次参数设置
			
			this.getFormHM().put("tableHtml",tableHtml);
			this.getFormHM().put("cycle",cycle);
			this.getFormHM().put("cycleTypeList",sbo.getCycleTypeList());
			this.getFormHM().put("year",year);
			this.getFormHM().put("yearTypeList",sbo.getYearTypeList());
			this.getFormHM().put("quarter",quarter);
			this.getFormHM().put("quarterTypeList",sbo.getQuarterTypeList());
			this.getFormHM().put("month",month);
			this.getFormHM().put("monthTypeList",sbo.getMonthTypeList());
			this.getFormHM().put("type",type);
			this.getFormHM().put("typeList",sbo.getTypeList());
			
			this.getFormHM().put("unitName",unitName);
			this.getFormHM().put("codeid",codeid);
			this.getFormHM().put("haveCycleMap",sbo.getHaveCycleMap());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}