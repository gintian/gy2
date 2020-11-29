package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleCheckBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
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
 * <p>Title:ExcecuteSingleGradeTrans.java</p>
 * <p>Description>:单人考评展现考核页面</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-05-31 下午03:56:27</p>
 * <p>@version: 1.0</p>
 * <p>@author: Administrator
 */

public class ExcecuteSingleGradeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String plan_id=(String)this.getFormHM().get("dbpre");
			if("0".equals(plan_id)){//如果是空  即plan_id为0  则返回空考核对象   页面上显示俩空的下拉框
				ArrayList objectList=new ArrayList();
				CommonData vo1=new CommonData("0"," ");
				objectList.add(vo1); 
				this.getFormHM().put("objectList",objectList);	
				return;
			}			
			String object_id=(String)this.getFormHM().get("object_id");
			object_id = object_id.replaceAll("／", "/");
			String searchname=(String)this.getFormHM().get("searchname");
			searchname = searchname.replaceAll("／", "/");
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			//zzk 2014/2/15 不做评价考核对象要被选中
			if("4".equals(hm.get("isNoMark"))&&object_id!=null&&object_id.length()>0){
				object_id=object_id.substring(0,object_id.indexOf("/"))+"/4";
			}
			if("0".equals(hm.get("isNoMark"))&&object_id!=null&&object_id.length()>0){
				object_id=object_id.substring(0,object_id.indexOf("/"))+"/0";
			}
			hm.remove("isNoMark");
			String typeflag=(String)hm.get("typeflag");//1是拼音简码查询
			hm.remove("typeflag");
			if(typeflag!=null && "1".equals(typeflag)&&searchname!=null && searchname.trim().length()>0)
				object_id = searchname;
//			this.getFormHM().put("object_id",object_id);
			String titleName=(String)this.getFormHM().get("titleName");
			String model=(String)this.getFormHM().get("model");
			String fromModel=(String)this.getFormHM().get("fromModel");   // frontPanel 来自首页快捷评分面板进入  ,menu
			if(fromModel==null)
				fromModel="menu";
			if("0".equals(object_id))
			{
				this.getFormHM().put("gradeHtml","  ");
				this.getFormHM().put("individualPerformance","");
				this.getFormHM().put("targetDeclare","");
				this.getFormHM().put("notMark","");
				this.getFormHM().put("personalComment", "");
				this.getFormHM().put("goalComment","");
				this.getFormHM().put("employRecordUrl","");
				return;
				
			}
			getObjectList(plan_id,model,object_id);
			
			int object_type=2; // 1,3,4:部门 2：人员 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type,pp.descript from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+plan_id);
//			this.frowset.next();
			String template_id="";
			String status="";		//权重分值表识 0：分值 1：权重
//			object_type=this.frowset.getInt(3);
			if(this.frowset.next()){
				template_id=this.frowset.getString(1);
				status=this.frowset.getString(2);		
				object_type=this.frowset.getInt(3);
			}
			if(status==null|| "".equals(status))
				status="0";
			String[] tt=object_id.split("/");
			String a_status=tt[1];
			String fillctrl="0";
			this.frowset=dao.search("select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and pm.mainbody_id='"+this.getUserView().getA0100()+"' and po.object_id='"+tt[0]+"'");
			if(this.frowset.next())
			{
				a_status=this.frowset.getString("status");
				if(this.frowset.getString("fillctrl")!=null)
					fillctrl=this.frowset.getString("fillctrl");
			}
			
			Hashtable htxml=new Hashtable();
			LoadXml loadxml=null; //new LoadXml(this.getFrameconn(),plan_id);
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{					
				loadxml = new LoadXml(this.getFrameconn(),plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else			
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);			
			 
			ArrayList dblist=new ArrayList();
			dblist = updatePlanStatus(plan_id, model, object_id);
			for(int i=0;i<dblist.size();i++)
			{
				CommonData cd=(CommonData)dblist.get(i);
				if(cd.getDataValue().equalsIgnoreCase(plan_id))
				{
					titleName=cd.getDataName();
					break;
				}
			}			 
			 
			htxml=loadxml.getDegreeWhole();
			String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
			String EvalOutLimitStdScore=(String)htxml.get("EvalOutLimitStdScore");
			String showOneMark=(String)htxml.get("ShowOneMark");
			String  noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	  //显示个人目标
			String ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志
			String showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth"); 
			//显示历次得分表showHistoryScore
			String showHistoryScore = (String)htxml.get("ShowHistoryScore");
			this.getFormHM().put("showHistoryScore", showHistoryScore);
			
			String mustFillWholeEval=(String)htxml.get("MustFillWholeEval");  //总体评价必填      2014.01.07   pjf
			this.getFormHM().put("mustFillWholeEval", mustFillWholeEval);
			String wholeEvalMode=(String)htxml.get("WholeEvalMode");  //总体评价必填      2014.01.07   pjf
			this.getFormHM().put("wholeEvalMode", wholeEvalMode);
			 
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView,plan_id);
			//计划说明 2013.11.09 pjf
			String clientname = SystemConfig.getPropertyValue("clientName");
			if("hkyh".equalsIgnoreCase(clientname)) {
		    	this.getFormHM().put("plan_descript_content", getDescript(plan_id));
		    	singleGradeBo.setPlanDesc(getDescript(plan_id));
		    	//this.getFormHM().put("plan_descript_content", getDescript(plan_id).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
		    	//singleGradeBo.setPlanDesc(getDescript(plan_id).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
		    }
			String notMark="";
			if(!"0".equals(object_id))
				notMark=singleGradeBo.getNotMark(object_id,plan_id,this.getUserView().getA0100(),loadxml);
			
			singleGradeBo.setShowOneMark(showOneMark);    
			singleGradeBo.setFillctrl(fillctrl);
			singleGradeBo.setFromModel(fromModel);
			ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,plan_id,status,this.getUserView().getA0100(),tt[0],a_status,titleName,1,this.userView);
		   
		    String targetDeclare=singleGradeBo.getTargetDeclare(plan_id,loadxml);
		    String individualPerformance=singleGradeBo.getIndividualPerformance(plan_id,tt[0],this.getUserView().getA0100(),loadxml,object_type,0);
		   
		    /*
		    BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
		    ArrayList objectList=batchGradeBo.getPerplanObjects(Integer.parseInt(plan_id),this.userView.getUserId());
		    ArrayList a_objectList=new ArrayList();
		    CommonData vo1=new CommonData("0"," ");
			a_objectList.add(vo1); 
		    for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();				
				CommonData vo=new CommonData(temp[0]+"/"+temp[2],temp[1]);
				a_objectList.add(vo);
			}
		    this.getFormHM().put("objectList",a_objectList);	
		    */
		    this.getFormHM().put("paramTable",htxml);
		    this.getFormHM().put("evalOutLimitStdScore",EvalOutLimitStdScore.toLowerCase());
		    this.getFormHM().put("noteIdioGoal",noteIdioGoal); 
		    this.getFormHM().put("DegreeShowType",(String)htxml.get("DegreeShowType"));
		    this.getFormHM().put("isEntireysub", ((String)htxml.get("isEntireysub")).toLowerCase());
		    this.getFormHM().put("PointEvalType", (String)htxml.get("PointEvalType"));
		    this.getFormHM().put("fillctrl",fillctrl);
		    this.getFormHM().put("individualPerformance",individualPerformance);
		    this.getFormHM().put("targetDeclare",targetDeclare);
		    this.getFormHM().put("notMark",notMark);
		    this.getFormHM().put("dblist",dblist);
		    
//		    this.getFormHM().put("gradeHtml",(String)list.get(0));
		    
		    
		    //  2011.06.25  JinChunhai
		    String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
		    String scoreflag=(String)htxml.get("scoreflag");		// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
		    String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
		    String RadioDirection=(String)htxml.get("RadioDirection"); 
		    String performanceType=(String)htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
		    if(("1".equals(PointEvalType)) && ("1".equals(scoreflag))  && "0".equals(RadioDirection))
		    {
			    SingleCheckBo singleCheckBo=new SingleCheckBo(this.frameconn,this.userView,plan_id,tt[0],this.getUserView().getA0100());
			    ArrayList htmlList=singleCheckBo.getSingleGradeHtml(a_status,titleName,status,a_status);				    				    				  				    
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
		    	this.getFormHM().put("personalComment","<a href='/selfservice/performance/selfGrade.do?b_querySummary=link'>绩效报告</a>");
		    }
		    else
		    	this.getFormHM().put("personalComment",(String)list.get(1));
		    
		    if("True".equalsIgnoreCase(ShowEmployeeRecord))
		    {
		    	LazyDynaBean timeBean=singleGradeBo.getBatchGradeBo().getPlanKhTime();
		    	String a0100=singleGradeBo.getBatchGradeBo().getA0100(tt[0],plan_id);
		    	String desc="查看日志";
		   // 	this.getFormHM().put("employRecordUrl","<a href='/performance/workdiary/workdiaryshow.do?b_query=link&a0100=Usr"+tt[0]+"&start_date="+(String)timeBean.get("start_date")+"&end_date="+(String)timeBean.get("end_date")+"'  target='_blank'  >员工日志</a>");
		    	if(a0100.length()>0&&showDayWeekMonth!=null&&showDayWeekMonth.trim().length()>0)
		    	{
		    		a0100=SafeCode.encode(PubFunc.convertTo64Base(a0100));
		    		this.getFormHM().put("employRecordUrl","<a href='javascript:showWordDiary(\""+plan_id+"\",\""+a0100+"\",\""+(String)timeBean.get("start_date")+"\",\""+(String)timeBean.get("end_date")+"\")'   >"+desc+"</a>");
		    	}
		    	else
		    		this.getFormHM().put("employRecordUrl", "");
		    }
		    else
		    	this.getFormHM().put("employRecordUrl", "");
		    
		    this.getFormHM().put("isNull",(String)list.get(2));
		    this.getFormHM().put("scoreflag",(String)list.get(3));
		    if(((String)list.get(4)).length()>0)
		    	this.getFormHM().put("dataArea",((String)list.get(4)).substring(1));
		    else
		    	this.getFormHM().put("dataArea","");
		    this.getFormHM().put("mainBodyId",this.getUserView().getA0100());
		    this.getFormHM().put("templateId",template_id);
		    this.getFormHM().put("nodeKnowDegree",(String)list.get(5));
		    this.getFormHM().put("wholeEval",(String)list.get(6));
		    this.getFormHM().put("limitation",(String)list.get(7));
		    this.getFormHM().put("gradeClass",(String)list.get(8));
		    this.getFormHM().put("lay",(String)list.get(9));
		    this.getFormHM().put("scoreBySumup",(String)list.get(10));
		    this.getFormHM().put("status",status);
		    if(((String)list.get(11)).length()>0&&(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))&&this.userView.getA0100().equals(tt[0]))
		    {
		    	this.getFormHM().put("goalComment","<a href='/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal'>绩效目标</a>");
		    }
		    else
		    	this.getFormHM().put("goalComment",(String)list.get(11));
		    this.getFormHM().put("pointContrl",(String)list.get(13));
		    this.getFormHM().put("pointIDs", (String)list.get(12));
		    this.getFormHM().put("performanceType",singleGradeBo.getPerformanceType());
		    this.getFormHM().put("isShowTotalScore",isShowTotalScore.toLowerCase());
		    this.getFormHM().put("noGradeItem", singleGradeBo.getNoGradeItem());
		    String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
		    if(totalAppFormula==null || totalAppFormula.trim().length()<=0)
		    	totalAppFormula = "";
		    else
		    	totalAppFormula = "1";
		    this.getFormHM().put("totalAppFormula",totalAppFormula);
		    this.getFormHM().put("objectStatus", a_status);
		    String topStr = "";
		    topStr = " select topscore from per_template where template_id=( select template_id from per_plan where plan_id="+plan_id+") ";
		    this.frowset=dao.search(topStr);
		    float topscore = 0;
			if(this.frowset.next())
			{
				topscore = (float)frowset.getFloat("topscore");
			}
			this.getFormHM().put("topscore",String.valueOf(topscore));
			
			
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
    
	public void getObjectList(String planid,String model,String object_id)
	{
		String[] _object_id = object_id.split("/");//object_id里面包含状态，保存提交等操作会改变  如果不实时更新form里面的值 那么前台下拉框就不能准确定位  此处加一个刷新页面则更新object_id值 zhaoxg add
		ArrayList a_objectList = new ArrayList();
		try
		{			
			 
			BatchGradeBo batchGradeBo = new BatchGradeBo(this.getFrameconn(),planid);
			ArrayList objectList = batchGradeBo.getPerplanObjects(Integer.parseInt(planid),this.userView.getA0100(),"2"/*model*/);	
			CommonData vo1 = new CommonData("0"," ");
			a_objectList.add(vo1); 
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();	
				if(_object_id[0].equals(temp[0])){
					object_id = temp[0]+"/"+temp[2];
				}
				String stateText = "(未评)";
				if("2".equals(temp[2])){
					stateText = "(已评)";
				}
				CommonData vo = new CommonData(temp[0]+"/"+temp[2],temp[1]+stateText);
				a_objectList.add(vo);
			}
			
			if(objectList.size()>0)
			{
				this.getFormHM().put("objectList",a_objectList);
			}						
			this.getFormHM().put("object_id",object_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}				
//		return a_objectList;		
	}
	/**
	 * 得到状态变更后的计划列表
	 * @param planid
	 * @param model
	 * @param object_id
	 * @throws GeneralException 
	 */
	public ArrayList updatePlanStatus(String planid,String model,String object_id) throws GeneralException {
		ArrayList aList=new ArrayList();
		try {
			ArrayList dblist = (ArrayList)this.getFormHM().get("dblist");
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());

			for(int i=0;dblist!=null && i<dblist.size();i++) {
				CommonData cd=(CommonData)dblist.get(i);
				String pName = cd.getDataName();
				//去掉计划名后的状态
				if(pName!=null && (pName.endsWith("(未评价)") || pName.endsWith("(正评价)") || pName.endsWith("(已评价)"))){
					cd.setDataName(pName.substring(0,pName.length()-5));
				}
			}
			//0：绩效考核  1：民主评测   2:团队考核 3：人员考核  4：单位 
			if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
				aList=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),1,0);
			else
				aList=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),2,1);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return aList;
	}
}
