package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:WritePlanContentTrans.java</p>
 * <p>Description:展现工作计划和工作总结交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WritePlanContentTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			/*
			// 加密解密
			Des des=new Des();
			a0100 = des.EncryPwdStr(a0100);  // 加密
			a0100 = des.DecryPwdStr(a0100); // 解密
									
			String ss = SafeCode.encode(PubFunc.convertTo64Base("")); // 转码
			System.out.println(ss+"---------------");
			ss = PubFunc.convert64BaseToString(SafeCode.decode(ss)); // 解码
			System.out.println(ss+"==================");
			*/
			
			String nbase = (String)this.getFormHM().get("mdnbase");
			String a0100 = (String)this.getFormHM().get("mda0100");
			String opt = (String)this.getFormHM().get("mdopt");
			String p0100 = (String)this.getFormHM().get("mdp0100");			
			this.getFormHM().put("mdnbase", nbase);
			this.getFormHM().put("mda0100", a0100);
			this.getFormHM().put("mdopt", opt);
			this.getFormHM().put("mdp0100", p0100);
			
			nbase = PubFunc.convert64BaseToString(SafeCode.decode(nbase));			
			a0100 = PubFunc.convert64BaseToString(SafeCode.decode(a0100));
			opt = PubFunc.convert64BaseToString(SafeCode.decode(opt));
			p0100 = PubFunc.convert64BaseToString(SafeCode.decode(p0100));
			String log_type = (String)this.getFormHM().get("log_type");
			String workType = (String)this.getFormHM().get("workType");
			String state = (String)this.getFormHM().get("state");
			String p0115 = (String)this.getFormHM().get("p0115");
			String month_num = (String)this.getFormHM().get("month_num");
			String year_num = (String)this.getFormHM().get("year_num");
			String month = (String)this.getFormHM().get("month");
			String day_num = (String)this.getFormHM().get("day_num");
			String week_num = (String)this.getFormHM().get("week_num");
			String quarter_num = (String)this.getFormHM().get("quarter_num");
			String addORupdate = "update"; // 新增或编辑日志的标志参数
			HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
			if(reqhm.get("pdCode")!=null && !"".equals(reqhm.get("pdCode")))
				this.getFormHM().put("pendingCode", reqhm.get("pdCode"));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			WorkdiarySelStr wss=new WorkdiarySelStr();
			//返回是否为抄送人员标记
			String flag=wss.reChaoSongFlag(p0100, this.getUserView().getUserName(), this.getUserView().getA0100(), nbase, this.getFrameconn());
			//this.getFormHM().put("csflag", flag);
			//取消待办中的任务
			if("1".equals(flag)){
				if("5".equals(reqhm.get("home")))
					dao.update("update per_diary_actor set display=1 where p0100="
							+p0100+"  and lower(nbase)='"+nbase.toLowerCase()+"' and a0100='"+this.getUserView().getA0100()+"'");
			}
			
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getFrameconn(),this.getUserView(),state,nbase,a0100);
			bo.analyseParameter(); // 刚进来时，加载一次参数设置
			ArrayList baseInfoList = bo.getBaseInfo(p0100, 1,year_num, quarter_num, month_num, week_num, day_num);
			if("".equals(p0100))
			{
				p0100 = bo.getP0100()+"";
				addORupdate = "add";
				this.getFormHM().put("mdp0100", SafeCode.encode(PubFunc.convertTo64Base(p0100)));
			}
			HashMap map = bo.getCopyToStr(p0100);
			String copyToStr = "";
			String copyToName = "";
			if(map.get("id")!=null)
				copyToStr = (String)map.get("id");
			if(map.get("name")!=null)
				copyToName = (String)map.get("name");
			ArrayList editContentList = bo.getEditFieldList(p0100,log_type);
						
			String planHtml = "";	
			String summaryStr = "";
			String planP0100 = "";
			if("2".equalsIgnoreCase(log_type)) // 工作总结
			{
				planP0100 = bo.getSummaryPlanStr(year_num,quarter_num,month_num,week_num,day_num);
				planHtml = bo.getPlanHtmlStr(planP0100,"0");
				summaryStr = bo.getSummaryStr(p0100);
				if(summaryStr==null || summaryStr.trim().length()<=0) // 如果未填写总结责引入同周期的计划内容
					summaryStr = bo.getPlanSumStr(planP0100);					
			}
			else 
				planHtml = bo.getPlanHtmlStr(p0100,opt);
			
			LazyDynaBean leaderCommandsBean = bo.getLeaderCommands(p0100, opt);
			String helpScript = bo.getHelpScript();
			
			String dbType = "1";
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.MSSQL:
			    {
			    	dbType = "1";
					break;
			    }
				case Constant.ORACEL:
				{ 
					dbType = "2";
					break;
				}
				case Constant.DB2:
				{
					dbType = "3";
					break;
				}
		    }			
			
			String checkCycleStr = ""; // 表头显示的周期
			String planCycleStr = ""; // 总结显示同期计划
			String refer_id = ""; // 参考信息登记表
			String print_id = ""; // 打印信息登记表
			if("4".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 年计划
    		{
				checkCycleStr = year_num+"年工作计划";
				refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id14");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id14");
    		}
    		else if("4".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 年总结
    		{
    			checkCycleStr = year_num+"年工作总结";
    			planCycleStr = year_num+"年工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id24");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id24");
    		}
    		else if("3".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 季计划
    		{
    			checkCycleStr = year_num+"年第"+quarter_num+"季度工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id13");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id13");
    		}
    		else if("3".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 季总结
    		{
    			checkCycleStr = year_num+"年第"+quarter_num+"季度工作总结";
    			planCycleStr = year_num+"年第"+quarter_num+"季度工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id23");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id23");
    		}
    		else if("2".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 月计划
    		{
    			checkCycleStr = year_num+"年"+month_num+"月工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id12");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id12");
    		}
    		else if("2".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 月总结
    		{
    			checkCycleStr = year_num+"年"+month_num+"月工作总结";
    			planCycleStr = year_num+"年"+month_num+"月工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id22");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id22");		    		    	    			
    		}
    		else if("1".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type)) // 周计划
    		{
    			checkCycleStr = year_num+"年"+month_num+"月第"+week_num+"周工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id11");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id11");
    		}
    		else if("1".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type)) // 周总结
    		{
    			checkCycleStr = year_num+"年"+month_num+"月第"+week_num+"周工作总结";
    			planCycleStr = year_num+"年"+month_num+"月第"+week_num+"周工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id21");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id21");
    		}
    		else if("0".equalsIgnoreCase(state) && "1".equalsIgnoreCase(log_type))
    		{
    			checkCycleStr = year_num+"年"+month_num+"月"+day_num+"号工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id0");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id0");
    		}
    		else if("0".equalsIgnoreCase(state) && "2".equalsIgnoreCase(log_type))
    		{
    			checkCycleStr = year_num+"年"+month_num+"月"+day_num+"号工作总结";
    			planCycleStr = year_num+"年"+month_num+"月"+day_num+"号工作计划";
    			refer_id = (String)WorkPlanViewBo.workParametersMap.get("refer_id0");
    			print_id = (String)WorkPlanViewBo.workParametersMap.get("print_id0");
    		}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			String startime = format.format(bo.getStartDateAndEndDate(year_num, quarter_num, month_num, week_num, day_num, 1));
			String sp_relation = (String)WorkPlanViewBo.workParametersMap.get("sp_relation");
			String record_grade = (String)WorkPlanViewBo.workParametersMap.get("record_grade");
			String sp_level = (String)WorkPlanViewBo.workParametersMap.get("sp_level");
			ArrayList recordGradeList = new ArrayList();
			recordGradeList = bo.getGradedescList();
			String recordGradeName = "null";//默认请评级
			
			recordGradeName = bo.getRecordGrade(p0100, opt,sp_level);
		
			String uvA0100 = this.userView.getA0100();
			String uvNbase = this.userView.getDbname();
			//判断当前用户级别
			
			String userStatus = "";
			//System.out.println(bo.isCurrUser(a0100, nbase, uvA0100, uvNbase));
			//System.out.println(bo.isFinalLeader(a0100, nbase, sp_relation, uvA0100, uvNbase));
			//System.out.println(bo.isInSpRelation(a0100,nbase,sp_relation,uvA0100,uvNbase));
			if((nbase+a0100).equals(uvNbase+uvA0100)){
				//当前是用户
				userStatus="0";
			}else if(bo.isCurrUser(a0100, nbase, uvA0100, uvNbase) && (bo.isFinalLeader(a0100, nbase, sp_relation, uvA0100, uvNbase) ||  !bo.isInSpRelation(a0100,nbase,sp_relation,uvA0100,uvNbase))){
				//当前是终极领导
				userStatus = "2";
			}else{
				//当前是中间领导
				userStatus = "1";
			}
			this.getFormHM().put("recordGradeName", recordGradeName);
			this.getFormHM().put("record_grade", record_grade);
			this.getFormHM().put("recordGradeList", recordGradeList);
			this.getFormHM().put("userStatus", userStatus);
			this.getFormHM().put("planP0100", planP0100);
			this.getFormHM().put("planCycleStr", planCycleStr);
			this.getFormHM().put("startime",startime);
			this.getFormHM().put("leaderCommandsBean",leaderCommandsBean);
			this.getFormHM().put("planHtml", planHtml);
			this.getFormHM().put("p0100", p0100);
			this.getFormHM().put("log_type",log_type);
			this.getFormHM().put("workType",workType);
			this.getFormHM().put("state",state);
			this.getFormHM().put("p0115",p0115);
			this.getFormHM().put("copyToStr",copyToStr);
			this.getFormHM().put("codyToName", copyToName);
			this.getFormHM().put("editContentList", editContentList);
			this.getFormHM().put("baseInfoList", baseInfoList);			
			this.getFormHM().put("helpScript", helpScript);
			this.getFormHM().put("optPlan", opt);
			this.getFormHM().put("month_num",month_num);
			this.getFormHM().put("year_num",year_num);
			this.getFormHM().put("month",month);
			this.getFormHM().put("day_num",day_num);
			this.getFormHM().put("week_num", week_num);
			this.getFormHM().put("quarter_num", quarter_num);
			this.getFormHM().put("addORupdate", addORupdate);
			this.getFormHM().put("summaryStr", summaryStr);
			this.getFormHM().put("dbType", dbType);
			this.getFormHM().put("refer_id", refer_id);
			if(refer_id!=null && refer_id.trim().length()>0)
				this.getFormHM().put("refer_name", bo.getPerRnameVo(refer_id).getString("name"));
			else
				this.getFormHM().put("refer_name", "");
			this.getFormHM().put("print_id", print_id);
			this.getFormHM().put("checkCycleStr", checkCycleStr);
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("nbase", nbase);
			this.getFormHM().put("a0101", bo.getPersonVo(nbase,a0100).getString("a0101"));
			this.getFormHM().put("workNbase",(String)WorkPlanViewBo.workParametersMap.get("nbase"));
			this.getFormHM().put("sp_level",(String)WorkPlanViewBo.workParametersMap.get("sp_level"));	
			this.getFormHM().put("sp_relation",sp_relation);			
			this.getFormHM().put("dailyPlan_attachment",(String)WorkPlanViewBo.workParametersMap.get("dailyPlan_attachment"));
			this.getFormHM().put("dailySumm_attachment",(String)WorkPlanViewBo.workParametersMap.get("dailySumm_attachment"));
			this.getFormHM().put("workLength", bo.getWorkLength());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
}
