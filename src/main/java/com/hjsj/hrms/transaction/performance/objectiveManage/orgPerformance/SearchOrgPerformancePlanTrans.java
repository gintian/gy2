package com.hjsj.hrms.transaction.performance.objectiveManage.orgPerformance;

import com.hjsj.hrms.businessobject.performance.objectiveManage.OrgPerformanceBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchOrgPerformancePlanTrans.java</p>
 * <p> Description:团队绩效</p>
 * <p>Company:hjsj</p>
 * <p> create time:2008-08-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchOrgPerformancePlanTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			if(this.userView.getA0100()==null || "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String year=/*Calendar.getInstance().get(Calendar.YEAR)+*/"-1";
			String quarter="-1";
			String month="-1";
			String status="-2";
			String spStatus="-2";
	        String a0100=this.userView.getA0100();
	        String planid="-1";
	        OrgPerformanceBo bo = new OrgPerformanceBo(this.getFrameconn());
	        
	        String returnflag=(String)hm.get("returnflag");
	        this.getFormHM().put("returnflag",returnflag);
	        
	        String isTargetCardTemp = "0";// 目标卡制定是否需要发送邮件通知
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
            if ( this.frowset.next())
            {
                String str_value = this.frowset.getString("str_value");
                if (str_value == null || (str_value != null && "".equals(str_value)))
                {
        
                } else
                {
                    Document doc = PubFunc.generateDom(str_value);
                    String xpath = "//Per_Parameters";
                    XPath xpath_ = XPath.newInstance(xpath);
                    Element ele = (Element) xpath_.selectSingleNode(doc);
                    Element child;
                    if (ele != null)
                    {
                        child = ele.getChild("TargetCard");
                        if (child != null)
                        {
                            isTargetCardTemp = child.getAttributeValue("email");
                        }
                        
                    }
                }
            }
            this.getFormHM().put("isTargetCardTemp", isTargetCardTemp);
	        
			if("1".equals(opt))
			{		
				//从首页我的任务进入，需自动匹配计划的考核期间
				if(returnflag!=null && ("8".equals(returnflag) || "10".equals(returnflag)))
				{
					String plan_id=(String)hm.get("plan_id");
					planid=plan_id;
					if(plan_id!=null)
					{
						RecordVo plan_vo=new RecordVo("per_plan");
						plan_vo.setInt("plan_id",Integer.parseInt(plan_id));
						plan_vo=dao.findByPrimaryKey(plan_vo);
						int cycle=plan_vo.getInt("cycle"); //(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
						if(cycle!=7)
						{
							String theyear=plan_vo.getString("theyear");
							if(cycle==0 || cycle==1)
							{
								
							}
							else if(cycle==2) //季度
							{
								String thequarter=plan_vo.getString("thequarter");
								quarter=thequarter;
							}
							else if(cycle==3)
							{ 
								String themonth=plan_vo.getString("themonth"); 
								month=themonth;
							} 
							year=theyear;
							opt="2";
						}
					}
				}				
			}
			else if("2".equals(opt))
			{
				year = (String)this.getFormHM().get("year");
				quarter = (String)this.getFormHM().get("quarter");
				status = (String)this.getFormHM().get("status");
				month=(String)this.getFormHM().get("month");
				spStatus=(String)this.getFormHM().get("spStatus");
			}else if("3".equals(opt))
			{
				year = (String)hm.get("year");
				quarter = (String)hm.get("quarter");
				status = (String)hm.get("status");
				month=(String)hm.get("month");
				spStatus=(String)hm.get("spStatus");
			}
			else if("4".equals(opt))
				planid=(String)hm.get("planid");
			
			HashMap reMap = bo.getOrgPlanList(a0100, year, quarter, month, status,this.userView,null,planid,spStatus);
			ArrayList planList = (ArrayList)reMap.get("list");
			ArrayList quarterList = bo.getQuarterList(a0100, year);
			ArrayList monthList = bo.getMonthList(year, quarter, a0100);
			ArrayList statusList = bo.getStatusList();
			SetUnderlingObjectiveBo suob = new SetUnderlingObjectiveBo(this.getFrameconn());
			ArrayList spStatusList = suob.getStatusList();
			this.getFormHM().put("spStatus", spStatus);
			this.getFormHM().put("spStatusList",spStatusList);
			this.getFormHM().put("orgPlanList",planList);
			this.getFormHM().put("year", year);
			// "评估期"下拉框只有首次进入页面时才进行初始化，否则点击年份后下拉备选项就只剩当前选择的这个年份了 chent 20180324 update
			if("-1".equals(year)) {
				ArrayList yearList = (ArrayList)reMap.get("yearlist");
				this.getFormHM().put("yearList", yearList);
			}
			this.getFormHM().put("month", month);
			this.getFormHM().put("monthList", monthList);
			this.getFormHM().put("quarter", quarter);
			this.getFormHM().put("quarterList", quarterList);
			this.getFormHM().put("status",status);
			this.getFormHM().put("statusList", statusList);
			this.getFormHM().put("plan_id",planid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
