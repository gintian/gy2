package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SaveSummaryAffixBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleCheckBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
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

/**
 * <p>Title:ExcecuteSelfGradeTrans.java</p>
 * <p>Description:展现自我评价</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExcecuteSelfGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{			
			String plan_id=(String)this.getFormHM().get("dbpre");
			String titleName=(String)this.getFormHM().get("titleName");
			/*LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = loadxml.getDegreeWhole();
			//显示历次得分表showHistoryScore
			String showHistoryScore = (String)params.get("showHistoryScore");
			this.getFormHM().put("showHistoryScore", showHistoryScore);
			*/
			//查询模板是否存在
			SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.getFrameconn());
			String temp = isnullaffix.isnullArticle_name(plan_id);
			this.getFormHM().put("isnullAffix", temp);
			
			if("0".equals(plan_id))
			{
				 this.getFormHM().put("gradeHtml"," ");
				 this.getFormHM().put("scoreflag","0");
				 this.getFormHM().put("individualPerformance","");
				 this.getFormHM().put("targetDeclare","");
				 this.getFormHM().put("notMark","");
				 
				 this.getFormHM().put("goalContext","");
				 this.getFormHM().put("isGoalFile","0");
				 this.getFormHM().put("isSummary","false");
				 this.getFormHM().put("noteIdioGoal","false");
				 this.getFormHM().put("employRecordUrl", "");
				 
				 this.getFormHM().put("s_rejectCause","");
				 this.getFormHM().put("summary","");
				 this.getFormHM().put("summaryFileIdsList",new ArrayList());
				 this.getFormHM().put("summaryState", "");
				 
				 this.getFormHM().put("g_rejectCause","");
				 this.getFormHM().put("goalContext","");
				 this.getFormHM().put("goalState","");
				 this.getFormHM().put("goalFileIdsList",new ArrayList());
				 this.getFormHM().put("isSummary","");
				 this.getFormHM().put("noteIdioGoal","");
				 this.getFormHM().put("plan_descript_content", "");
			}
			else
			{	
				LoadXml loadxml = null;			
				if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
				{	
					loadxml = new LoadXml(this.getFrameconn(),plan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
				}
				else
					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
								 
				ArrayList dblist=(ArrayList)this.getFormHM().get("dblist");
				for(int i=0;i<dblist.size();i++)
				{
					CommonData cd=(CommonData)dblist.get(i);
					if(cd.getDataValue().equalsIgnoreCase(plan_id))
					{
						titleName=cd.getDataName();
						break;
					}
				}
				 
				Hashtable htxml=new Hashtable();		
				htxml=loadxml.getDegreeWhole();
				String performanceType=(String)htxml.get("performanceType");		      //考核形式  0：绩效考核  1：民主评测
				String SummaryFlag=((String)htxml.get("SummaryFlag")).toLowerCase();     //个人总结报告
				String noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	  //显示个人目标
				String relatingTargetCard=(String)htxml.get("relatingTargetCard");        //关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
				String SelfEvalNotScore=((String)htxml.get("SelfEvalNotScore")).toLowerCase(); //自我评价不显示打分
				String ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志
				String showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth");
			    String allowUploadFile = (String)htxml.get("AllowUploadFile"); //是否支持附件上传
			   //显示历次得分表showHistoryScore
				String showHistoryScore = (String)htxml.get("ShowHistoryScore");
				
				
				String mustFillWholeEval=(String)htxml.get("MustFillWholeEval");  //总体评价必填      2014.01.07   pjf
				this.getFormHM().put("mustFillWholeEval", mustFillWholeEval);
				
				this.getFormHM().put("showHistoryScore", showHistoryScore);
				this.getFormHM().put("allowUploadFile", allowUploadFile);
				this.getFormHM().put("evalOutLimitStdScore",((String)htxml.get("EvalOutLimitStdScore")).toLowerCase());				
				this.getFormHM().put("performanceType",performanceType);
				
				String _str="";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str=" pms.level_o";
				else
					_str=" pms.level ";
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				int n=0;
				String _sql="select * from per_mainbody where object_id='"+userView.getA0100()+"' and mainbody_id='"+userView.getA0100()+"' and plan_id="+plan_id;
				this.frowset=dao.search(_sql);
				if(this.frowset.next())
					n++;
				if(n==0)
				{
					_sql="select pm.* from per_mainbody pm,per_mainbodyset pms where pm.plan_id="+plan_id+"   ";
					_sql+=" and pm.body_id=pms.body_id and "+_str+"=5 and pm.mainbody_id='"+userView.getA0100()+"' and pm.object_id<>pm.mainbody_id ";
					this.frowset=dao.search(_sql);
					if(this.frowset.next())
						n++;
				}
				if(n>0&& "false".equalsIgnoreCase(SelfEvalNotScore)) //如果可以自我打分
				{
					
					int object_type=2; // 1:部门 2：人员
					String object_id=(String)this.getFormHM().get("object_id");
					this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type,pp.descript from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+plan_id);
					this.frowset.next();
					String template_id=this.frowset.getString(1);
					String status=this.frowset.getString(2);		//权重分值表识 0：分值 1：权重
					if(status==null|| "".equals(status))
						status="0";
					object_type=this.frowset.getInt(3);
					String sql="";
					if(object_type==2)
						sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and pm.object_id='"+this.getUserView().getA0100()+"' and pm.mainbody_id='"+this.getUserView().getA0100()+"'";
					else
					{
						sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po,per_mainbodyset pms where pm.object_id=po.object_id and pm.plan_id="+plan_id+" and po.plan_id="+plan_id;
						sql+=" and pm.body_id=pms.body_id and "+_str+"=5 and pm.mainbody_id='"+userView.getA0100()+"' and pm.object_id<>pm.mainbody_id ";
					}					
					
					this.frowset=dao.search(sql);
					String fillctrl="0";
					if(this.frowset.next())
					{
						object_id=this.frowset.getString(1)+"/"+this.frowset.getString(3);
						if(this.frowset.getString("fillctrl")!=null)
							fillctrl=this.frowset.getString("fillctrl");
					}
					String[] tt=object_id.replaceAll("／", "/").split("/");
					//LoadXml loadxml=new LoadXml(this.getFrameconn(),plan_id);
					//Hashtable htxml=new Hashtable();
					htxml=loadxml.getDegreeWhole();
					String showOneMark=(String)htxml.get("ShowOneMark");
				    String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
					SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView,plan_id);
					//计划说明 2013.11.09 pjf
					String clientname = SystemConfig.getPropertyValue("clientName");
				    if("hkyh".equalsIgnoreCase(clientname)) {
				    	this.getFormHM().put("plan_descript_content", getDescript(plan_id));
				    	singleGradeBo.setPlanDesc(getDescript(plan_id));
				    	//this.getFormHM().put("plan_descript_content", getDescript(plan_id).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
				    	//singleGradeBo.setPlanDesc(getDescript(plan_id).replaceAll("\r\n","<br>").replaceAll(" ", "&nbsp;&nbsp;"));
				    }
				    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
				    singleGradeBo.setReturnflag((String)hm.get("returnflag"));
				    hm.remove("returnflag");//把返回标记清空 防止混乱  zhaoxg add 2014-9-3
					singleGradeBo.setShowOneMark(showOneMark);
					singleGradeBo.setFillctrl(fillctrl);
				    ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,plan_id,status,this.getUserView().getA0100(),tt[0],tt[1],titleName,0,this.userView);
				    String notMark=singleGradeBo.getNotMark(object_id,plan_id,this.getUserView().getA0100(),loadxml);
				    String targetDeclare=singleGradeBo.getTargetDeclare(plan_id,loadxml);
				    String individualPerformance=singleGradeBo.getIndividualPerformance(plan_id,object_id,this.getUserView().getA0100(),loadxml,object_type,0);
				    
				    
				    if("True".equalsIgnoreCase(ShowEmployeeRecord))
				    {
				    	LazyDynaBean timeBean=singleGradeBo.getBatchGradeBo().getPlanKhTime();
				    	String a0100=singleGradeBo.getBatchGradeBo().getA0100(tt[0],plan_id);
				    	String desc="查看日志"; 
				       if(a0100.length()>0&&showDayWeekMonth !=null&&showDayWeekMonth.trim().length()>0){
				            a0100=SafeCode.encode(PubFunc.convertTo64Base(a0100));
				    		this.getFormHM().put("employRecordUrl","<a href='javascript:showWordDiary(\""+plan_id+"\",\""+a0100+"\",\""+(String)timeBean.get("start_date")+"\",\""+(String)timeBean.get("end_date")+"\")'   >"+desc+"</a>");
				       }else
				    		this.getFormHM().put("employRecordUrl", "");
				    }
				    else
				    	this.getFormHM().put("employRecordUrl", "");
				     
				    this.getFormHM().put("objectStatus", tt[1]);
				    this.getFormHM().put("DegreeShowType",(String)htxml.get("DegreeShowType"));
				    this.getFormHM().put("PointEvalType", (String)htxml.get("PointEvalType"));
				    this.getFormHM().put("isEntireysub", ((String)htxml.get("isEntireysub")).toLowerCase());
				    this.getFormHM().put("fillctrl",fillctrl);
				    this.getFormHM().put("individualPerformance",individualPerformance);
				    this.getFormHM().put("targetDeclare",targetDeclare);
				    this.getFormHM().put("notMark",notMark);
//				    this.getFormHM().put("gradeHtml",(String)list.get(0));
				    this.getFormHM().put("paramTable",htxml);
				    
				    this.getFormHM().put("wholeEvalScore",singleGradeBo.getWholeEvalScore());   //总体评价得分
				    String topStr = "";
				    topStr = " select topscore from per_template where template_id=( select template_id from per_plan where plan_id="+plan_id+") ";
				    this.frowset=dao.search(topStr);
				    float topscore = 0;
					if(this.frowset.next())
					{
						topscore = (float)frowset.getFloat("topscore");
					}
					this.getFormHM().put("topscore",String.valueOf(topscore));
					this.getFormHM().put("wholeEvalMode",singleGradeBo.getWholeEvalMode());   //总体评价得分
				    
				    //  2011.06.25  JinChunhai
				    String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
				    String scoreflag=(String)htxml.get("scoreflag");		// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
				    String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
				    String RadioDirection=(String)htxml.get("RadioDirection"); 
				    if(("1".equals(PointEvalType)) && ("1".equals(scoreflag)) && "0".equals(RadioDirection))
				    {
					    SingleCheckBo singleCheckBo=new SingleCheckBo(this.frameconn,this.userView,plan_id,tt[0],this.getUserView().getA0100());
					    ArrayList htmlList=singleCheckBo.getSingleGradeHtml(tt[1],titleName,status,tt[1]);				    				    				  				    
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
				    
				    
				    this.getFormHM().put("personalComment",(String)list.get(1));
				    
				    this.getFormHM().put("goalComment", (String)list.get(11));
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
				    this.getFormHM().put("object_id",object_id);
				    this.getFormHM().put("isSelfMark","1");
				    this.getFormHM().put("isSummary",SummaryFlag);
				    this.getFormHM().put("noteIdioGoal",noteIdioGoal);
				    this.getFormHM().put("relatingTargetCard",relatingTargetCard.toLowerCase());
				    this.getFormHM().put("pointIDs", (String)list.get(12));
				    this.getFormHM().put("pointContrl",(String)list.get(13));
				    this.getFormHM().put("isShowTotalScore",isShowTotalScore.toLowerCase());
				    this.getFormHM().put("noGradeItem", singleGradeBo.getNoGradeItem());
				    
				    this.getFormHM().put("goalContext","");
					this.getFormHM().put("isGoalFile","0");
					String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
				    if(totalAppFormula==null || totalAppFormula.trim().length()<=0)
				    	totalAppFormula = "";
				    else
				    	totalAppFormula = "1";
				    this.getFormHM().put("totalAppFormula",totalAppFormula);
					if(this.frowset.next())
					{
						topscore = (float)frowset.getFloat("topscore");
					}
					this.getFormHM().put("topscore",String.valueOf(topscore));
				}
				else  //如果不具备自我打分权限，则自动展现 自我总结 页面
				{
					
					this.getFormHM().put("employRecordUrl", "");
					String summaryState="0";
					String s_rejectCause="";
					ArrayList summaryFileIdsList=new ArrayList();
					String summary=" ";
					StringBuffer strsql=new StringBuffer();
					strsql.append("select * from per_article where plan_id="+plan_id+" and a0100='"+this.userView.getA0100()+"' " );
					strsql.append(" and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"' and article_type=2 order by fileflag");
					this.frowset=dao.search(strsql.toString());
					while(this.frowset.next())
					{
						if(this.frowset.getInt("fileflag")==1)  //文本
						{
							summary=Sql_switcher.readMemo(this.frowset,"Content");
							summaryState=this.frowset.getString("state");
							s_rejectCause=Sql_switcher.readMemo(this.frowset,"description");
						}
						else if(this.frowset.getInt("fileflag")==2)  //附件
						{
							LazyDynaBean abean=new LazyDynaBean();
							abean.set("id", this.frowset.getString("Article_id"));
							abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
							summaryFileIdsList.add(abean);
						}
					}
					this.getFormHM().put("s_rejectCause",s_rejectCause);
					this.getFormHM().put("summary",summary);
					this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
					this.getFormHM().put("summaryState", summaryState);
					
					
					String goalContext=" ";   //目标内容
					String goalState="0";
					String g_rejectCause="";
					ArrayList goalFileIdsList=new ArrayList();
					if("True".equalsIgnoreCase(relatingTargetCard) || "2".equalsIgnoreCase(relatingTargetCard))  //当关联目标卡时，不出现绩效目标填报界面
						noteIdioGoal="false";
					if("true".equals(noteIdioGoal))
					{
						strsql.setLength(0);
						strsql.append("select * from per_article where plan_id="+plan_id+" and a0100='"+this.userView.getA0100()+"' " );
						strsql.append(" and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"' and article_type=1 order by fileflag");
						this.frowset=dao.search(strsql.toString());
						while(this.frowset.next())
						{
							if(this.frowset.getInt("fileflag")==1)  //文本
							{
								goalContext=Sql_switcher.readMemo(this.frowset,"Content");
								goalState=this.frowset.getString("state");
								g_rejectCause=Sql_switcher.readMemo(this.frowset,"description");
							}
							else if(this.frowset.getInt("fileflag")==2)  //附件
							{
								LazyDynaBean abean=new LazyDynaBean();
								abean.set("id", this.frowset.getString("Article_id"));
								abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
								goalFileIdsList.add(abean);
							}
						}						
					}
					this.getFormHM().put("g_rejectCause",g_rejectCause);
					this.getFormHM().put("goalContext",goalContext);
					this.getFormHM().put("goalState",goalState);
					this.getFormHM().put("goalFileIdsList",goalFileIdsList);
					this.getFormHM().put("isSummary",SummaryFlag);
					this.getFormHM().put("noteIdioGoal",noteIdioGoal);
					this.getFormHM().put("gradeHtml"," ");
					this.getFormHM().put("scoreflag","0");
					this.getFormHM().put("individualPerformance","");
					this.getFormHM().put("targetDeclare","");
					this.getFormHM().put("notMark","");
					this.getFormHM().put("isSelfMark","0");
					
				}
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
    
	/**
	 * 动态产生表中没有的字段
	 * @param tableName
	 */
	public void updateTable(String tableName)
	{
		RecordVo vo=new RecordVo(tableName);
		try
		{
			
			Table table=new Table(tableName);
			int num=0;
			if(!vo.hasAttribute("summarize"))	//个人总结
			{
				Field obj=new Field("summarize","summarize");
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");				
				table.addField(obj);
				num++;
			}
			
			if(!vo.hasAttribute("affix"))      //个人总结附件
			{
				Field obj=new Field("affix","affix");
				obj.setDatatype(DataType.BLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");				
				table.addField(obj);
				num++;
			}
			if(!vo.hasAttribute("ext"))		  //附件扩展名
			{
				Field obj=new Field("ext","ext");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");	
				obj.setLength(10);
				table.addField(obj);
				num++;
			}
			if(num>0)
			{
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				dbWizard.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel(tableName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
