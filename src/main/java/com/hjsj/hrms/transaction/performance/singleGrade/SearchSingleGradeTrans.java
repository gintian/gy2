package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleCheckBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * <p>Title:SearchSingleGradeTrans.java</p>
 * <p>Description>:单人考评/自我评价</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-05-31 下午03:56:27</p>
 * <p>@version: 1.0</p>
 * <p>@author: Administrator
 */

public class SearchSingleGradeTrans extends IBusiness {	

	public void execute() throws GeneralException {
		if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
		//非在职人员不允许使用改功能
		if(!"USR".equalsIgnoreCase(userView.getDbname())) {
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
		}
		ArrayList dblist=new ArrayList();
		ArrayList objectList=new ArrayList();
		String titlename="";
		String planid="";
		String object_id="";
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测   2:团队考核 3：人员考核  4：单位
		String optObject=(String)hm.get("optObject");   // 1：领导班子  2：班子成员
		String fromModel=(String)hm.get("fromModel");   // frontPanel 来自首页快捷评分面板进入  ,menu
		if(fromModel==null)
			fromModel="menu";
		String _planid=(String)hm.get("to_plan_id");
		hm.remove("to_plan_id");
		
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
		this.getFormHM().put("fromModel",fromModel);
		this.getFormHM().put("model",model);
		this.getFormHM().put("optObject",optObject);
		
		try
		{
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String organization=userView.getUserOrgId();

		//	ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		//	String controlByKHMoudle = bo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)			
			// 得到绩效考核计划列表
			String perPlanSql = "select plan_id,name,status,parameter_content,object_type,template_id from per_plan where ( status=4 or status=6 ) ";
			if (!userView.isSuper_admin())
			{	
				perPlanSql += "and plan_id in (select plan_id from per_mainbody where ";
	/*			if(model!=null&&(model.equals("2")||model.equals("3")||model.equals("4")))
				{
					
				}
				else
					perPlanSql += " mainbody_id<>object_id and ";
	*/		
				perPlanSql+=" mainbody_id='"+ userView.getA0100() + "' )";
			
				if(!"USR".equalsIgnoreCase(userView.getDbname()))
					perPlanSql+=" and 1=2 ";
			}
			
			perPlanSql += " and ( Method=1 or method is null ) order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			this.frowset = dao.search(perPlanSql);

			CommonData vo1 = new CommonData("0", " ");
			dblist.add(vo1);
			int i = 0;
			LoadXml loadXml=null; //new LoadXml();
			
			while (this.frowset.next()) 
			{
			/*	
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
				{
					String template_id = this.getFrowset().getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id))					
							continue;					
					}
				}
			*/	
				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
			//	String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
				String object_type=this.frowset.getString("object_type");
				
				if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
				{
					
					loadXml = new LoadXml(this.getFrameconn(),plan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
				}
				else
				{
					loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
				}
				Hashtable htxml = loadXml.getDegreeWhole();
				String performanceType=(String)htxml.get("performanceType");
			//	String performanceType=loadXml.getPerformanceType(xmlContent);
	            if("1".equals(model))
	            {
	            	if(optObject!=null && "1".equals(optObject) && !("1".equals(object_type) || "3".equals(object_type) || "4".equals(object_type))){
	            		continue;
	            	}
	            	if(optObject!=null && "2".equals(optObject) && !"2".equals(object_type)){
	            		continue;
	            	}
	            }
	            
	            if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
	            {
	            	if("2".equals(model))
	            	{
	            		if(!"4".equals(object_type)&&!"1".equals(object_type))
	            			continue;
	            		else
	            		{
	            			CommonData vo = new CommonData(plan_id, name);
		            		dblist.add(vo);
		            		i++;
	            		}
	            	}
	            	else
	            	{
		            	int objectType=Integer.parseInt(model)-1;
		            	if(objectType!=Integer.parseInt(object_type))
		            		continue;
		            	else
		            	{
		            		CommonData vo = new CommonData(plan_id, name);
		            		dblist.add(vo);
		            		i++;
		            	}
	            	}
	            }
	            else if(model.equals(performanceType))
	            {
	            	
	            	//if(loadXml.getHandEval(xmlContent).equalsIgnoreCase("FALSE"))
	            	if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
	            	{
	            		CommonData vo = new CommonData(plan_id, name);
	            		dblist.add(vo);
	            		i++;
                	}
	            }
			} //遍历完所有的计划
			
			ArrayList aList=new ArrayList();
			if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
				aList=singleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),1,0);
			else
				aList=singleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),2,1);
			if(aList.size()>1)
			{
				
				if(_planid!=null&&_planid.length()>0)
				{
					CommonData dd=null;
					for(int j=0;j<aList.size();j++)
					{
						CommonData d=(CommonData)aList.get(j);
						String _id=d.getDataValue();
						if(_id.equals(_planid))
						{
							dd=d;
							planid=_planid;
							titlename=d.getDataName();
						}
					}
					
					if(dd!=null)
					{
						ArrayList list0=new ArrayList();
						list0.add(dd);
						aList=list0;
					}
				}
				
				if(planid.length()==0)//如果计划号为空（比如说第一次进入页面），取第一个计划
				{
					CommonData d=(CommonData)aList.get(1);
					titlename=d.getDataName();
					planid=d.getDataValue();
					LoadXml loadxml = new LoadXml(this.getFrameconn(), planid);
					Hashtable params = loadxml.getDegreeWhole();
					//显示历次得分表showHistoryScore
					String showHistoryScore = (String)params.get("ShowHistoryScore");
					this.getFormHM().put("showHistoryScore", showHistoryScore);
					String mustFillWholeEval=(String)params.get("MustFillWholeEval");  //总体评价必填      2014.01.07   pjf
					this.getFormHM().put("mustFillWholeEval", mustFillWholeEval);
				}
			}
			float topscore = 0;
			if(planid.length()>0){
    			String topStr = "";
    		    topStr = " select topscore from per_template where template_id=( select template_id from per_plan where plan_id="+planid+") ";
    		    this.frowset=dao.search(topStr);
    			if(this.frowset.next())
    			{
    				topscore = (float)frowset.getFloat("topscore");
    			}
			}
			this.getFormHM().put("topscore",String.valueOf(topscore));
			
			dblist=aList;

			CommonData vo2 = new CommonData("0", " ");
			objectList.add(vo2);
			this.getFormHM().put("dblist",dblist);
			this.getFormHM().put("dbpre","0");
			this.getFormHM().put("objectList",objectList);
			this.getFormHM().put("gradeHtml"," ");
			this.getFormHM().put("individualPerformance","");
			this.getFormHM().put("personalComment"," ");
			this.getFormHM().put("scoreflag"," ");
			this.getFormHM().put("targetDeclare"," ");
			if(!"".equals(titlename)&&!"".equals(planid))
			{
				singleGradeBo.getPlanVo(planid);
				 
				String objectid=getObjectID(planid,model);//form中的objectList有数据了
				if(!"".equals(objectid))
				{
					
					BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),planid);
					singleGradeBo.setBatchGradeBo(batchGradeBo);
					int object_type=2; // 1:部门 2：人员
					this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type,pp.descript from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+planid);
					this.frowset.next();
					String template_id=this.frowset.getString(1);
					String status=this.frowset.getString(2);		//权重分值表识 0：分值 1：权重
					if(status==null|| "".equals(status))
						status="0";
					object_type=this.frowset.getInt(3);
					String[] tt=objectid.replaceAll("／", "/").split("/");
					String a_status=tt[1];
					String fillctrl="0";
					this.frowset=dao.search("select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id="+planid+" and po.plan_id="+planid+" and pm.mainbody_id='"+this.getUserView().getA0100()+"' and po.object_id='"+tt[0]+"'");
					if(this.frowset.next())
					{
						a_status=this.frowset.getString("status");					
						if(this.frowset.getString("fillctrl")!=null)
							fillctrl=this.frowset.getString("fillctrl");
					}
					Hashtable htxml=new Hashtable();
				//	LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
					LoadXml loadxml=null;
					if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
					{
							
						loadxml = new LoadXml(this.getFrameconn(),planid);
						BatchGradeBo.getPlanLoadXmlMap().put(planid,loadxml);
					}
					else
					{
						loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
					}
											 					 
					htxml=loadxml.getDegreeWhole();
					String ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志
					String showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth");
					String performanceType=(String) htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
					String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
				    if(totalAppFormula==null || totalAppFormula.trim().length()<=0)
				    	totalAppFormula = "";
				    else
				    	totalAppFormula = "1";
					String  noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	  //显示个人目标
					String notMark="";
					String showOneMark=(String)htxml.get("ShowOneMark");
					if(!"0".equals(objectid))
						notMark=singleGradeBo.getNotMark(objectid,planid,this.getUserView().getA0100(),loadxml);
					String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
					singleGradeBo.setFillctrl(fillctrl);
					singleGradeBo.setShowOneMark(showOneMark);
					singleGradeBo.setFromModel(fromModel);
					//计划说明 2013.11.09 pjf
	    			String clientname = SystemConfig.getPropertyValue("clientName");
	    		    if("hkyh".equalsIgnoreCase(clientname)) {
	    		    	this.getFormHM().put("plan_descript_content", getDescript(planid));
	    		    	singleGradeBo.setPlanDesc(getDescript(planid));
	    		    	//this.getFormHM().put("plan_descript_content", getDescript(planid).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
	    		    	//singleGradeBo.setPlanDesc(getDescript(planid).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
	    		    }
					ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,planid,status,this.getUserView().getA0100(),tt[0],a_status,titlename,1,this.userView);
				   
					if("True".equalsIgnoreCase(ShowEmployeeRecord))//显示员工日志
				    {
						singleGradeBo.getBatchGradeBo().setPlanid(planid);
				    	LazyDynaBean timeBean=singleGradeBo.getBatchGradeBo().getPlanKhTime();
				    	String a0100=singleGradeBo.getBatchGradeBo().getA0100(tt[0],planid);
				    	String desc=ResourceFactory.getProperty("performance.singlegrade.seeDiary"); 
				    	// '/performance/workdiary/workdiaryshow.do?b_query=link&a0100=Usr"+tt[0]+"&start_date="+(String)timeBean.get("start_date")+"&end_date="+(String)timeBean.get("end_date")+"'
				    	if(a0100.length()>0&&showDayWeekMonth !=null&&showDayWeekMonth.trim().length()>0)
				    	{
				    		a0100=SafeCode.encode(PubFunc.convertTo64Base(a0100));
				    		this.getFormHM().put("employRecordUrl","<a href='javascript:showWordDiary(\""+planid+"\",\""+a0100+"\",\""+(String)timeBean.get("start_date")+"\",\""+(String)timeBean.get("end_date")+"\")'   >"+desc+"</a>");
				    	}
				    	else
				    		this.getFormHM().put("employRecordUrl", "");
				    
				    }
				    else
				    	this.getFormHM().put("employRecordUrl", "");
				    String targetDeclare=singleGradeBo.getTargetDeclare(planid,loadxml);
				    String individualPerformance=singleGradeBo.getIndividualPerformance(planid,tt[0],this.getUserView().getA0100(),loadxml,object_type,0);
				    this.getFormHM().put("paramTable",htxml);
				    this.getFormHM().put("PointEvalType", (String)htxml.get("PointEvalType"));
				    this.getFormHM().put("DegreeShowType",(String)htxml.get("DegreeShowType"));
				    this.getFormHM().put("noteIdioGoal",noteIdioGoal);
				    this.getFormHM().put("isEntireysub", ((String)htxml.get("isEntireysub")).toLowerCase());
				    this.getFormHM().put("evalOutLimitStdScore",((String)htxml.get("EvalOutLimitStdScore")).toLowerCase());
				    this.getFormHM().put("individualPerformance",individualPerformance);
				    this.getFormHM().put("notMark",notMark);
				    this.getFormHM().put("targetDeclare",targetDeclare);
//				    this.getFormHM().put("gradeHtml",(String)list.get(0));
				    this.getFormHM().put("totalAppFormula",totalAppFormula);
				     //总体评价采集方式: 0-录入等级，1-录入分值   zzk 2014/1/20
				    String wholeEvalMode=singleGradeBo.getWholeEvalMode();
				    this.getFormHM().put("wholeEvalMode",wholeEvalMode);
				    //  2011.06.25  JinChunhai
				    String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
				    String scoreflag=(String)htxml.get("scoreflag");		// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
				    String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
				    String RadioDirection=(String)htxml.get("RadioDirection"); //排列方式 1. 横排 0.竖排
				    if(("1".equals(PointEvalType)) && ("1".equals(scoreflag))   && "0".equals(RadioDirection))//如果评分方式是单选
				    {
					    SingleCheckBo singleCheckBo=new SingleCheckBo(this.frameconn,this.userView,planid,tt[0],this.getUserView().getA0100());
					    singleCheckBo.setFromModel(fromModel);
					    ArrayList htmlList=singleCheckBo.getSingleGradeHtml(a_status,titlename,status,a_status);				    				    				  				    
					    this.getFormHM().put("gradeHtml",(String)htmlList.get(0));
					    
					    String appitem_id = "";
					    ArrayList appContantList = new ArrayList();
						if("1".equals(performanceType))
						{
							appitem_id = singleCheckBo.getAppitem_id();
							appContantList = singleCheckBo.getAppContantList();
						}
						this.getFormHM().put("appitem_id",appitem_id);
						this.getFormHM().put("appContantList",appContantList);
					    
				    }else
				    {
				    	this.getFormHM().put("gradeHtml",(String)list.get(0));
				    }				    				    
				    
				    
				    if(((String)list.get(11)).length()>0&&(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))&&this.userView.getA0100().equals(tt[0]))
				    {
				    	this.getFormHM().put("personalComment","<a href='/selfservice/performance/selfGrade.do?b_querySummary=link'>"+ResourceFactory.getProperty("lable.performance.perSummary")+"</a>");
				    }
				    else
				    	this.getFormHM().put("personalComment",(String)list.get(1));
				    
				    this.getFormHM().put("isNull",(String)list.get(2));
				    this.getFormHM().put("scoreflag",(String)list.get(3));
				    if(((String)list.get(4)).length()>0)
				    	this.getFormHM().put("dataArea",((String)list.get(4)).substring(1));
				    else
				    	this.getFormHM().put("dataArea","");
				    
				    this.getFormHM().put("performanceType",performanceType);
				    this.getFormHM().put("mainBodyId",this.getUserView().getA0100());
				    this.getFormHM().put("templateId",template_id);
				    this.getFormHM().put("nodeKnowDegree",(String)list.get(5));
				    this.getFormHM().put("wholeEval",(String)list.get(6));
				    this.getFormHM().put("limitation",(String)list.get(7));
				    this.getFormHM().put("gradeClass",(String)list.get(8));
				    this.getFormHM().put("lay",(String)list.get(9));
				    this.getFormHM().put("scoreBySumup",(String)list.get(10));
				    if(((String)list.get(11)).length()>0&&(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))&&this.userView.getA0100().equals(tt[0]))
				    {
				    	this.getFormHM().put("goalComment","<a href='/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal'>"+ResourceFactory.getProperty("lable.performance.perGoal")+"</a>");
				    }
				    else
				    	this.getFormHM().put("goalComment",(String)list.get(11));
				    this.getFormHM().put("pointContrl",(String)list.get(13));
				    this.getFormHM().put("pointIDs", (String)list.get(12));
				    
				    this.getFormHM().put("status",status);
				    this.getFormHM().put("isShowTotalScore",isShowTotalScore);
				    this.getFormHM().put("noGradeItem", singleGradeBo.getNoGradeItem());
				}	
			}
			else
			{
				this.getFormHM().put("goalComment","");
				this.getFormHM().put("employRecordUrl", "");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}
	
	 /**
     * 取得考核计划说明内容
     * @param plan_id
     * @return
     */
    public String getDescript(String plan_id)
	{
		String descript="";
		try
		{
			String sql = " select descript from per_plan where plan_id="+plan_id;
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				descript=Sql_switcher.readMemo(rs, "descript");
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return descript;
	}
    
	public String getObjectID(String planid,String model)
	{
		String objectid="";
		ArrayList a_objectList=new ArrayList();
		try
		{			
			 
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),planid);
			ArrayList objectList=batchGradeBo.getPerplanObjects(Integer.parseInt(planid),this.userView.getA0100(),"2"/*model*/);
			StringBuffer userIDs=new StringBuffer("");	
			CommonData vo1=new CommonData("0","　");//改为全角空格，解决bug 39520 
			a_objectList.add(vo1); 
			int a=0;
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();	
				if(a==0)
				{
					objectid=temp[0]+"/"+temp[2];
				}
				String stateText = "(未评)";
				if("2".equals(temp[2])){
					stateText = "(已评)";
				}
				CommonData vo=new CommonData(temp[0]+"/"+temp[2],temp[1]+stateText);
				a_objectList.add(vo);
				a++;
			}
			
			if(objectList.size()>0)
			{
				this.getFormHM().put("objectList",a_objectList);
				this.getFormHM().put("dbpre",planid);
				this.getFormHM().put("object_id",objectid);
			}						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
		return objectid;		
	}		
}
