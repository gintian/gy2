package com.hjsj.hrms.businessobject.performance.singleGrade;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.LazyDynaMap;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SingleGradeBo 
{  
	private Connection conn=null;
	private UserView userView = null;
	private BatchGradeBo batchGradeBo=null;
	private int td_width=100;
	private int td_height=60;
	private String showOneMark="False";            //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
	private int operateModel=0;                    //1:单人打分 0：自我评价
	private HashMap userNumberPointResultMap=new HashMap();
	private HashMap objectSelfCore=new HashMap();    //考核对象自我打分分值
	private boolean  isLookObjectScore=false;                 //是否可以查看对象自评分
	private String   DescriptiveWholeEval="True";	 //显示描述性总体评价，默认为 True
	private String   WholeEval="";
	private String   WholeEvalMode="0";          //总体评价采集方式: 0-录入等级，1-录入分值
	public String getWholeEvalMode() {
		return WholeEvalMode;
	}

	public void setWholeEvalMode(String wholeEvalMode) {
		WholeEvalMode = wholeEvalMode;
	}

	private String wholeEvalScore ="";

	
	
	public String getWholeEvalScore() {
		return wholeEvalScore;
	}

	public void setWholeEvalScore(String wholeEvalScore) {
		this.wholeEvalScore = wholeEvalScore;
	}
	
	private String   selfScoreInDirectLeader="";     //上级领导给下级打分时是否 显示考核对象的自我打分分数
	private String   allowSeeLowerGrade="";          //允许查看下级评分
	private String       NodeKnowDegree="";          //了解程度
	private  String 	  GradeClass="";             //等级分类id
	private  String totalAppFormula="";              // 总体评价的计算公式，默认为空
	private String       performanceType="0";		 //考核形式  0：绩效考核  1：民主评测
	private String   isShowSubmittedScores="";		 ////提交后的分数是否显示
	private String   showDeductionCause="False";	 //评分说明？
	private String relatingTargetCard = "1"; // 关联目标卡 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
	private String showYPTargetCard = "0";//1:只显示已自评目标卡 0：显示所有
	private String   showIndicatorRole="False";
	private String   DegreeShowType="1";             //1-标准标度 2-指标标度
	private RecordVo planVo=null;
	private String fillctrl="0";
	private String[] a_temp=null;
	private String noGradeItem="";  //没有设置上下限值的指标名称
	private String BlankScoreOption="0";             //指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
	private String BlankScoreUseDegree="";			//指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…
	private String PointEvalType="0";  //360指标评分型式  0：下拉（默认）   1：单选
	private String RadioDirection = "";
	private String _scoreflag="";//=2混合，=1标度
	private String    fromModel="menu";                // frontPanel 来自首页快捷评分面板进入  ,menu
	
	
	private boolean isShowObjectSelfScore=false;
	private boolean showObjectLowerScore=false;
	private HashMap pointMaxValueMap=new HashMap();
	private LazyDynaBean mainbodyBean=null;
	private String opt="0";  //0:显示自我评分分值表  1：显示他人打分分值表
	private HashMap allEvalResultMap=new HashMap();    //各考核主体给对象的评分结果
	private ArrayList lowerGradeMainbodyList=new ArrayList();  //下级主体列表
	private  ArrayList gradelist=new ArrayList();  //标准标度
	private String isHasSaveButton = "0"; //国税用
	String fillTableExplain = "";//国税用
	private boolean isByModel = false;//是否按岗位素质模型测评
	private String planDesc = "";
	private String currentAndvance="0";//当前人员当前测评阶段当前招聘渠道是否设置了高级测评方式 0:未设置 1：设置成了高级测评方式
	private String hireState="";//如果设置了高级测评方式的话，表示测评的阶段 31：初试 32：复试
	private String returnflag;
	
	private String batchGradeMainBodybody_id="";//多人评分页面查看下属打分明细时的主体分类级别。 如果不是空的则说明需要展示出下级详细人员姓名。 zhanghua 2017-9-13

	public String getBatchGradeMainBodybody_id() {
		return batchGradeMainBodybody_id;
	}

	public void setBatchGradeMainBodybody_id(String batchGradeMainBodybody_id) {
		this.batchGradeMainBodybody_id = batchGradeMainBodybody_id;
	}

	public String getHireState() {
		return hireState;
	}

	public void setHireState(String hireState) {
		this.hireState = hireState;
	}

	public String getCurrentAndvance() {
		return currentAndvance;
	}

	public void setCurrentAndvance(String currentAndvance) {
		this.currentAndvance = currentAndvance;
	}

	/**
	 * @return the planDesc
	 */
	public String getPlanDesc() {
		return planDesc;
	}

	/**
	 * @param planDesc the planDesc to set
	 */
	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}

	public SingleGradeBo(Connection conn)
	{
		this.conn=conn;
		batchGradeBo=new BatchGradeBo(this.conn);
	}
	public SingleGradeBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView = userView;
		batchGradeBo=new BatchGradeBo(this.conn);
	}
	
	public SingleGradeBo(Connection conn,String planid)
	{
		this.conn=conn;
		batchGradeBo=new BatchGradeBo(this.conn,planid);
		this.planVo=getPlanVo(planid);
	}
	
	public SingleGradeBo(Connection conn,UserView userView,String planid)
	{
		this.conn=conn;
		this.userView = userView;
		batchGradeBo=new BatchGradeBo(this.conn,planid);
		this.planVo=getPlanVo(planid);
	}
	
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(planid));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.planVo=vo;
		return vo;
	}
	
	
	/**
	 * 取得模板下的统一打分指标
	 * @param templateID
	 * @return
	 */
	public String getOneMarkPointStr(String templateID)
	{
		StringBuffer _str=new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			String sql = "select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po "
			    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID + "' "; // pi.seq,
				sql +=" and  po.pointkind='1' and po.status=1   ";
		    rowSet = dao.search(sql);
			while(rowSet.next())
				_str.append(",'"+rowSet.getString("point_id")+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
		}
		return _str.toString();
	}
	

	String noPrivPointStr="";  //无权限的指标
	/**
	 * 得到考核页面
	 * @param template_id
	 * @param plan_id
	 * @param status  //权重分值表识 0：分值 1：权重
	 * @param userID  考核主体
	 * @param a_status 考核对象状态
	 * @param flag  1:单人打分 0：自我评价
	 * @param a_status 该计划下该考核对象的打分状态(per_mainbody.status)
	 * @return
	 */
	public ArrayList getSingleGradeHtml(String template_id,String plan_id,String status,String userID,String object_id,String a_status,String titleName,int flag,UserView userView) throws GeneralException
	{
		ArrayList list_temp=new ArrayList();
		try
		{
			 this.operateModel=flag;
			 batchGradeBo.setObject_id(object_id);//初始化batchGradeBo
			 
			 boolean isByModelOrNot = getByModel(plan_id,this.conn);
			 boolean isHaveMatch = isHaveMatchByModel(object_id, this.conn);
			 if(isByModelOrNot && isHaveMatch){
				 isByModel = true;
			 }
			 
			 HashMap pointMap = new HashMap();//考核对象为键，该考核对象的所有指标为键值
	
			 if(isByModel){
				 //能力素质支持一个评估计划适应多个岗位进行评估
				 pointMap = batchGradeBo.getCompetencyPointprivMap(String.valueOf(plan_id),userID);
			 }else{
				 pointMap=batchGradeBo.getPointprivMap(String.valueOf(plan_id),userID);   //得到指标权限信息   zhaoxg标记
			 }
			 HashMap   a_pointmap=(HashMap)pointMap.get(object_id);		   					  //得到具有某考核对象的指标权限map	
			 Set keySet=a_pointmap.keySet();
			 //如果是按岗位素质模型测评，则不需要找出没有权限的指标
			 this.noPrivPointStr="";
			 String oneMardStr="";
			 if(!isByModel){//如果不是按岗位素质模型测评
				 for(Iterator t=keySet.iterator();t.hasNext();)
				 {
					 String key=(String)t.next();
					 if(a_pointmap.get(key)!=null&& "0".equals((String)a_pointmap.get(key)))
						 noPrivPointStr+=",'"+key+"'";	 
				 }
				 if(this.noPrivPointStr.length()>0)
					 this.noPrivPointStr=this.noPrivPointStr.substring(1);
				 
				 if("false".equalsIgnoreCase(this.showOneMark))  //不显示统一评分指标
				 {
					 	oneMardStr=getOneMarkPointStr(template_id);
					 	if(oneMardStr.length()>0)
					 	{
					 		if(this.noPrivPointStr.length()>0)
					 			this.noPrivPointStr+=oneMardStr;
					 		else
					 			 this.noPrivPointStr=oneMardStr.substring(1);
					 	}
				 
					 
				 }
			 }

			ArrayList list = new ArrayList();//得到所有数据的list
			if(isByModel){////能力素质支持一个评估计划适应多个岗位进行评估
				String e01a1 = this.getE01a1(object_id);
			    list = getCompetencyAllDataList(e01a1);
			}
			else
			    list=getPerformanceStencilList(template_id);
			
			ArrayList items=(ArrayList)list.get(0);				//模版项目列表(按顺序)
			int        lay=((Integer)list.get(1)).intValue();	//表头的层数
			HashMap    map=(HashMap)list.get(2);				//各项目包含的指标个数
			HashMap    subItemMap=(HashMap)list.get(3);			//各项目的子项目(hashmap)		
			
			batchGradeBo.setShowOneMark(this.showOneMark);
			ArrayList pointList=batchGradeBo.getPerPointList(template_id,plan_id);//是否按岗位素质模型测评
			ArrayList sList=(ArrayList) pointList.get(0);//标准标度内容  zzk
			ArrayList pList=(ArrayList) pointList.get(1);//指标内容
			HashMap pMap=new HashMap();
			if(sList.size()==0){
			
				throw GeneralExceptionHandler.Handle(new Exception("没有设置标准标度!"));
			}
			for(int i=0;i<sList.size();i++){
				String[] temp = new String[14];
				temp=(String[]) sList.get(i);
				pMap.put(temp[1], "");
			}
			for(int i=0;i<pList.size();i++){
				String[] temp = new String[14];
				temp=(String[]) pList.get(i);
				String pointId=temp[0];
				String pointDesc=temp[1];
				if(pMap.get(pointId)==null){
					throw GeneralExceptionHandler.Handle(new Exception("指标:"+pointDesc+"没有设置标度!"));
				}
			}
			ArrayList _list=(ArrayList)pointList.get(1);
			ArrayList _list2=new ArrayList();
			StringBuffer pointContrl_str=new StringBuffer("");
			for(int i=0;i<_list.size();i++)
			{
				String[] _temp=(String[])_list.get(i);
				if(a_pointmap.get(_temp[0])!=null&& "1".equals((String)a_pointmap.get(_temp[0])))
				{
					 if("false".equalsIgnoreCase(this.showOneMark))  //不显示统一评分指标
					 {
						 if(oneMardStr.toLowerCase().indexOf("'"+_temp[0].toLowerCase()+"'")==-1)
						 {
							 _list2.add(_temp);
							 pointContrl_str.append("/1");
						 }
					 }
					 else
					 {
						 _list2.add(_temp);
						 pointContrl_str.append("/1");
					 }
				}
			}
			ArrayList _newPointList=new ArrayList();
			_newPointList.add((ArrayList)pointList.get(0));
			_newPointList.add(_list2);
			_newPointList.add((String)pointList.get(2));
			_newPointList.add((String)pointList.get(3));
			pointList=_newPointList;
			HashMap   pointItemMap=getPointItemList((ArrayList)pointList.get(1),items);		
			this.mainbodyBean=getMainbodyBean(plan_id,object_id,userID);
			this.wholeEvalScore = (String)this.mainbodyBean.get("wholeEvalScore");
			list_temp=getGradeHtml(userID,status,template_id,plan_id,lay,map,subItemMap,items,pointList,pointItemMap,object_id,a_status,titleName,userView);
			
			if(pointContrl_str!=null && pointContrl_str.length()>0)
				list_temp.add(pointContrl_str.substring(1));
			else
				list_temp.add(pointContrl_str.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return list_temp;
	}
	
	
	/**判断是否有保存、提交按钮  郭峰*/
	public String getIsHasSaveButton(){
		return isHasSaveButton;
	}
	
	
	/**
	 * 取得下级考核主体的信息列表
	 * @param property // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
	 * @return
	 */
	public ArrayList getLowerGradeList(String mainbody_id,String object_id,int plan_id)
	{
		ArrayList list=new ArrayList();
		try
		{
			int property=10;
			ContentDAO dao = new ContentDAO(this.conn);
			String _str="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="level_o";
			String _sql="select per_mainbodyset."+_str+" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id"
					+" and plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'";
			RowSet rowSet=dao.search(_sql);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null)
					property=rowSet.getInt(1);
			}
			
			if(property==5)
				return list;
			
			String level_str="";
			switch (property)
			{
				case 1:
					level_str="5,2";
					break;
				case 0:
					level_str="5,1,2";
					break;
				case -1:
					level_str="5,1,0,2";
					break;
				case -2:
					level_str="5,1,0,-1,2";
					break;
			}
			
			if(level_str.length()==0)
				return list;
			
			StringBuffer  sql=new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
			sql.append(" and pm.plan_id="+plan_id+" and pm.object_id='"+object_id+"' and  ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql.append("  pms.level_o");
			else
				sql.append("  pms.level ");
			sql.append(" in ("+level_str+")");
			String cloumn="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				cloumn="level_o";
			sql.append(" order by "+cloumn+" desc ");
			rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("a0100", rowSet.getString("mainbody_id"));
				abean.set("a0101", rowSet.getString("a0101"));
				abean.set("bodyname", rowSet.getString("name"));
				abean.set("status", rowSet.getString("status"));
				
				String _status=rowSet.getString("status");
	 			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
					continue;
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得下级考核主体的信息列表 zhanghua 2017-10-31
	 * @param property // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
	 * @return
	 */
	public ArrayList getLowerGradeList(String mainbody_id,String object_id,int plan_id,String body_id)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=null;

			
			StringBuffer  sql=new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
			sql.append(" and pm.plan_id="+plan_id+" and pm.object_id='"+object_id+"' and  pms.body_id ");

			sql.append(" ="+body_id+" ");

			sql.append(" order by pm.body_id desc ");
			rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("a0100", rowSet.getString("mainbody_id"));
				abean.set("a0101", rowSet.getString("a0101"));
				abean.set("bodyname", rowSet.getString("name"));
				abean.set("status", rowSet.getString("status"));
				
				String _status=rowSet.getString("status");
	 			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
					continue;
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public LazyDynaBean getMainbodyBean(String plan_id,String object_id,String mainbody_id)
	{
		LazyDynaBean abean=null;
		try
		{
			String _str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="level_o";
			else
				_str="level";
			
			ContentDAO dao = new ContentDAO(this.conn);
			
			RowSet rowSet=dao.search("select per_mainbody.*,per_mainbodyset."+_str+" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id and plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("body_id",rowSet.getString("body_id"));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"");
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
				abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
				abean.set("level", rowSet.getString(_str)!=null?rowSet.getString(_str):"");
				abean.set("wholeEvalScore",rowSet.getString("whole_score") == null ? "0" : rowSet.getString("whole_score"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	
	
	/**
	 * 取得 个人绩效 功能入口连接地址
	 * @param plan_id    绩效计划
	 * @param object_id  考核对象
	 * @param mainbody_id 考核主体
	 * @param loadxml    
	 * @return
	 */
	public String getIndividualPerformance(String plan_id,String object_id,String mainbody_id,LoadXml loadxml,int object_type,int flag)
	{
		String individualURL="";
		Hashtable htxml=new Hashtable();		
		htxml=loadxml.getDegreeWhole();
		String perSet=(String)htxml.get("PerSet");
		if(perSet!=null&&perSet.trim().length()>0)
		{
			object_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(object_id));
			mainbody_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainbody_id));
			
			String name=ResourceFactory.getProperty("performance.batchgrade.performanceData"); 
			if(flag==0){
				String clientname = SystemConfig.getPropertyValue("clientName");
				if("gs".equalsIgnoreCase(clientname)){//如果是广东国税，就让绩效数据以弹出框的形式出现，而不是跳转页面
					individualURL="<a href='/selfservice/performance/singleGrade.do?b_individual=search&operate=2&plan_id="+plan_id+"&object_id="+object_id+"&mainbody_id="+mainbody_id+"' target='_blank'>"+name+"</a>";
				}else{
					individualURL="<a href=\"javascript:indiviPerformance('"+plan_id+"','"+object_id+"','"+mainbody_id+"')\">"+name+"</a>";
				}
			}
			else if(flag==1){
				String buttonClass="mybutton";
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
				{
				    buttonClass="mybuttonBig";
				}
				individualURL="<Input type='button' value='"+ResourceFactory.getProperty("performance.batchgrade.performanceData")+"' class='"+buttonClass+"'  name='b_indiviPerformance'  onclick=\"indiviPerformance('"+plan_id+"','"+object_id+"','"+mainbody_id+"')\" />";
			}
				
		}
		return individualURL;
	}
	
	
	/**
	 * 取得 指标说明 功能模块入口连接地址
	 * @param planid  考核计划id
	 * @return
	 */
	public String getTargetDeclare(String planid,LoadXml loadxml)
	{
		String targetDeclare="";
		Hashtable htxml=new Hashtable();		
		htxml=loadxml.getDegreeWhole();
		String showIndicatorDesc=(String)htxml.get("ShowIndicatorDesc");
		if(showIndicatorDesc!=null&& "true".equalsIgnoreCase(showIndicatorDesc))
		{
			ContentDAO dao=new ContentDAO(this.conn);
			try
			{
				RowSet rowSet=dao.search("select file_ext from per_plan where plan_id="+planid);
				if(rowSet.next())
				{
					String ext=rowSet.getString("file_ext");
					if(ext!=null&&ext.trim().length()>0)
						targetDeclare="<a href='javascript:showInfo("+planid+")' style='font-size:12px;'>"+ResourceFactory.getProperty("lable.performance.pointDeclare")+"</a>";	
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return targetDeclare;
	}
	
	/**
	 * 取得不打分 功能模块入口地址
	 * @param objectid  考核对象id
	 * @param planid    考核计划id
	 * @param mainbodyID 考核主体id
	 * @return
	 */
	public String getNotMark(String objectid,String planid,String mainbodyID,LoadXml loadxml)
	{
		String notMark="";
		Hashtable htxml=new Hashtable();		
		htxml=loadxml.getDegreeWhole();
		String showNoMarking=(String)htxml.get("ShowNoMarking");
	
		String[] a_temp=objectid.split("/");
		objectid=a_temp[0];
	
		if(showNoMarking!=null&& "true".equalsIgnoreCase(showNoMarking))
		{
			notMark="<a href='/performance/markStatus/markStatusList.do?b_edit=edit&operater=2&status="+a_temp[1]+"&planID="+planid+"&objectID="+objectid+"&mainbodyID="+mainbodyID+"'>"+ResourceFactory.getProperty("performance.batchgrade.unGradeReason")+"</a>";
			
		}
		return notMark;
	}
	
	
	//得到考评对象的自我评价分数
	public HashMap getSelfGradeValue(String objectID,String planID)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			
			String  sql="select * from per_table_"+planID+"  where mainbody_id='"+objectID+"' and object_id='"+objectID+"' ";
			
			if(this.planVo!=null&&(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4))
				sql="select * from per_table_"+planID+"  where mainbody_id=(select mainbody_id from per_mainbody where  plan_id="+planID+" and object_id='"+objectID+"'  and body_id=-1 ) and object_id='"+objectID+"' ";
			rowSet=dao.search(sql);
			ResultSetMetaData mt=rowSet.getMetaData();
			while(rowSet.next())
			{
				String[] temp=new String[mt.getColumnCount()];
				for(int i=0;i<mt.getColumnCount();i++)
				{
					temp[i]=rowSet.getString(i+1)!=null?rowSet.getString(i+1):"";
				}
				map.put(temp[5],temp);
			}
			sql="select know_id,whole_grade_id,status,whole_score from per_mainbody where plan_id="+planID+" and mainbody_id='"+objectID+"' and object_id='"+objectID+"' ";
			if(this.planVo!=null&&(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4))
				sql="select know_id,whole_grade_id,status,whole_score from per_mainbody where plan_id="+planID+" and mainbody_id=(select mainbody_id from per_mainbody where  plan_id="+planID+" and object_id='"+objectID+"'  and body_id=-1 )  and object_id='"+objectID+"' ";
			rowSet=dao.search(sql);
			String _status="";
			if(rowSet.next())
			{
				map.put("know_id",rowSet.getString(1));
				map.put("whole_grade_id",rowSet.getString(2));
				map.put("status",rowSet.getString(3));
				map.put("whole_score",rowSet.getString(4));
				_status=rowSet.getString(3);
			}
		 
			if(_status!=null&&("2".equals(_status)|| "3".equals(_status)|| "8".equals(_status)))
			{
				
			}
			else
				map=new HashMap(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 取得考核主体是考核对象的主体类别
	 * @param object_id
	 * @param mainbody_id
	 * @param planid
	 * @return
	 */
	public String getMainBodyLevel(String object_id,String mainbody_id,String planid)
	{
		String level="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			String sql="select ";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o";
			else
				sql+=" level ";
			sql+=" from per_mainbodyset where body_id=(select body_id from per_mainbody where plan_id";
			sql+="="+planid+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"')";
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null)
					level=rowSet.getString(1);
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return level;
	}
	
	/**
	 * 判断 当前评分人是不是团队的负责人
	 * @param object_id
	 * @param userid
	 * @param plan_id
	 * @return
	 */
	public boolean isSelfGrade(String object_id,String userid,String plan_id)
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			 
			String _str="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="level_o";
			 
			rowSet=dao.search("select pm.* from per_mainbody pm,per_mainbodyset pms  where pm.body_id=pms.body_id and pm.plan_id="+plan_id+" and pm.object_id='"+object_id+"'  and pms."+_str+"=5 and pm.mainbody_id='"+userid+"' ");
			if(rowSet.next())
				flag=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public ArrayList getGradeHtml(String userID,String status,String template_id,String plan_id,int lay,HashMap map,HashMap subItemMap,ArrayList items,ArrayList pointList,HashMap pointItemMap,String object_id,String a_status,String titleName,UserView userView) throws GeneralException
	{
		
		ArrayList list=new ArrayList();
	     StringBuffer html=new StringBuffer("");
	     StringBuffer personalComment=new StringBuffer(" ");
	     StringBuffer goalComment=new StringBuffer(" ");
	     StringBuffer pointIDs=new StringBuffer("");
	     String       isNull="0";
	     String		  scoreflag="";
	     String       limitation="";
	     String       showOneMark="";
	     String       ScoreBySumup="";
	     String       isShowTotalScore="";
	     int total_width=0;
	     StringBuffer dataArea=new StringBuffer("");
	     String KeepDecimal="0";
	     String showNoMarking="";
	     String clientname = SystemConfig.getPropertyValue("clientName");
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 Hashtable htxml=new Hashtable();
			 LoadXml loadxml=null;
			 if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			 {
				 loadxml = new LoadXml(this.conn,plan_id);
				 BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			 }
			 else
			 {
				 loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			 }
		//	 LoadXml loadxml=new LoadXml(conn,plan_id);
			 
			 
			 htxml=loadxml.getDegreeWhole();
			 this.PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
			 this.RadioDirection=(String)htxml.get("RadioDirection");   //排列方式 1. 横排 0.竖排
		
			 this.DescriptiveWholeEval=(String)htxml.get("DescriptiveWholeEval");
			 this.BlankScoreOption=(String)htxml.get("BlankScoreOption");
			 this.BlankScoreUseDegree=(String)htxml.get("BlankScoreUseDegree");  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…
			 String ScaleToDegreeRule=(String)htxml.get("limitrule");       //分值转标度规则（1-就高 2-就低 3-就近就高（默认值）） 
			 this.allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");//允许查看下级对考核对象评分 zhaoxg标记
			 if(this.isShowObjectSelfScore)
				 this.allowSeeLowerGrade="false";
		//	 this.allowSeeLowerGrade="True";
			 this.NodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度
			 this.WholeEval=(String)htxml.get("WholeEval");			    //总体评价
			 this.WholeEvalMode = (String)htxml.get("WholeEvalMode");
			 if("False".equalsIgnoreCase(this.WholeEval))
					this.WholeEvalMode = "0";
			 String EvalClass = (String)htxml.get("EvalClass");            //在计划参数中的等级分类ID
			 this.totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			 this.GradeClass = "";
			 if(EvalClass==null||EvalClass.trim().length()<=0|| "0".equals(EvalClass.trim()))
			  this.GradeClass=(String)htxml.get("GradeClass");					//等级分类ID
			 else
			   this.GradeClass=(String)htxml.get("EvalClass");
			 limitation=(String)htxml.get("limitation");                  //=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)
			 this.performanceType=(String)htxml.get("performanceType");		//考核形式  0：绩效考核  1：民主评测
			 String SummaryFlag=(String)htxml.get("SummaryFlag");				//个人总结评价作为评分标准
			 String  noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	  //显示个人目标
			 this.relatingTargetCard=(String)htxml.get("relatingTargetCard");          //关联目标卡 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
			 this.showYPTargetCard = (String)htxml.get("showYPTargetCard");//True:显示已自评目标卡 False：不显示
				if(showYPTargetCard==null || "False".equalsIgnoreCase(showYPTargetCard)){
					this.showYPTargetCard = "0";
				}else if(showYPTargetCard!=null && "True".equalsIgnoreCase(showYPTargetCard)){
					this.showYPTargetCard = "1";
				}
			 scoreflag=(String)htxml.get("scoreflag");					//=2混合，=1标度
			 this._scoreflag=scoreflag;
			 showOneMark=(String)htxml.get("ShowOneMark");  //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
			 ScoreBySumup=(String)htxml.get("ScoreBySumup"); //BS个人总结没填写，主体为其打分时不能提交
			 selfScoreInDirectLeader=(String)htxml.get("SelfScoreInDirectLeader");				//上级领导给下级打分时是否 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
			 this.DegreeShowType=(String)htxml.get("DegreeShowType");
			 KeepDecimal=(String)htxml.get("KeepDecimal");  //小数位	
			 showNoMarking=(String)htxml.get("ShowNoMarking");  //是否显示不打分。
			 isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
			 this.isShowSubmittedScores=(String)htxml.get("isShowSubmittedScores"); //提交后的分数是否显示
			 
			 int extendNum=0;
			 String showIndicatorDegree=(String)htxml.get("showIndicatorDegree");  //显示考核指标标度说明
			 showIndicatorRole=(String)htxml.get("showIndicatorRole");  //显示考核指标评分原则
			 String showIndicatorContent=(String)htxml.get("showIndicatorContent");  //显示考核指标内容
			 this.showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			 
			 if("True".equalsIgnoreCase(showIndicatorDegree))
				 extendNum++;
			 if("True".equalsIgnoreCase(showIndicatorRole))
			 {
				 extendNum++; extendNum++;
			 }
			 if("True".equalsIgnoreCase(showIndicatorContent))
				 extendNum++;
			
			 if("1".equals(this.performanceType))
			 {
					SummaryFlag="True";
			//		showNoMarking="True";
			 }
			 if("1".equals(this.BlankScoreOption)){
				 if(isByModel){
					 this.pointMaxValueMap=this.batchGradeBo.getMaxPointValueByModel();      //指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
				 }else{
					 this.pointMaxValueMap=this.batchGradeBo.getMaxPointValue(template_id);      //指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
				 }
				
			 }
				 
			 
			 this.setShowOneMark(showOneMark);
			 this.batchGradeBo.setShowOneMark(showOneMark);
			 this.setUserNumberPointResultMap(this.batchGradeBo.getUserNumberPointResultMap((ArrayList)pointList.get(1),plan_id));
			
			 DecimalFormat myformat1 = new DecimalFormat("########.####");
			 HashMap templatePointInfo=new HashMap();  //模板下的指标信息
			 if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("showTemplatePointValue")))
				 templatePointInfo=this.batchGradeBo.getTemplatePointInfo(template_id);
			 
			 
			 ArrayList pointInfoList=(ArrayList)pointList.get(0);           //指标详细集
			 ArrayList a_pointList=(ArrayList)pointList.get(1);             //指标集 
			 isNull=(String)pointList.get(2);				                //指标范围是否为空 0：不为空  1：为空
			 this.noGradeItem=(String)pointList.get(3);
			 HashMap itemsSignMap=getItemsSignMap(items);
			 
			 String[] temp={object_id," ",a_status};
			 ArrayList objectList=new ArrayList();
			 objectList.add(temp);
			 this.a_temp=temp;
			//上级领导给下级打分时是否 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.  zhaoxg标记
			 String mitiScoreMergeSelfEval=(String)htxml.get("mitiScoreMergeSelfEval");  //显示自我评价  显示自我评价和允许哪类主体可以查看自我评价必须一块使用  zzk  2014/1/28
			 if(!"0".equalsIgnoreCase(selfScoreInDirectLeader)&&!"False".equalsIgnoreCase(selfScoreInDirectLeader)&&operateModel==1)
			 {
				 //DirectUpperPosBo directUpperPosBo=new DirectUpperPosBo(this.conn);
				String level=getMainBodyLevel(object_id,userID,plan_id);
				 
				 if(("True".equalsIgnoreCase(mitiScoreMergeSelfEval)&& "True".equalsIgnoreCase(selfScoreInDirectLeader)|| "1".equals(selfScoreInDirectLeader))&& "1".equals(level))
					 this.isLookObjectScore=true;
				 else if("True".equalsIgnoreCase(mitiScoreMergeSelfEval)&& "2".equals(selfScoreInDirectLeader)&&("1".equals(level)|| "0".equals(level)|| "-2".equals(level)|| "-1".equals(level)))
					 this.isLookObjectScore=true; //directUpperPosBo.isUpperLead(object_id,userID,1);
				 else if("True".equalsIgnoreCase(mitiScoreMergeSelfEval)&& "3".equals(selfScoreInDirectLeader)&&!userID.equalsIgnoreCase(object_id))
					 this.isLookObjectScore=true;
				 
				 if(this.planVo!=null&&(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4))
				 {
					 if(isSelfGrade(object_id,userID,plan_id))
						 this.isLookObjectScore=false;
				 }
				 
				 if(this.isLookObjectScore)
					 objectSelfCore=getSelfGradeValue(object_id,plan_id);  //考核对象自我打分分值
				 if(!"2".equals(objectSelfCore.get("status"))){
					 this.isLookObjectScore=false;  //考核对象没有提交  也是看不到自我评分
				 }
				// isLeader=true;
			 }
			 
			 
			 /* 得到某计划考核主体给对象的评分结果hashMap */
			 HashMap perTableMap=new HashMap();
			 if(this.opt!=null&& "0".equals(this.opt)&&isShowObjectSelfScore)
			 {
				 if(this.planVo!=null&&(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4))
				 {	

					 RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
					 if(rowSet.next())
						 perTableMap=this.batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id),rowSet.getString("mainbody_id"),objectList);
				 	 else
				 		perTableMap=batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id),object_id,objectList);
				
				 }
				 else
					 perTableMap=batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id),object_id,objectList);
			 }
			 else
				 perTableMap=batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id),userID,objectList);
			 
			 HashMap objectResultMap=(HashMap)perTableMap.get(object_id);
			 HashMap pointMap = new HashMap();
			 HashMap perPointScoreMap = new HashMap();
			 if(isByModel){
				 ////能力素质支持一个评估计划适应多个岗位进行评估
				 pointMap=batchGradeBo.getCompetencyPointprivMap(String.valueOf(plan_id),userID);//得到有权限的指标
				 String e01a1 = getE01a1(object_id);
				 perPointScoreMap=batchGradeBo.getCompetencyPerPointScore(e01a1);                  //得到模版下各指标的分值及最大上限值和最小下限值
			 }else{
				 pointMap=batchGradeBo.getPointprivMap(String.valueOf(plan_id),userID);   //得到指标权限信息
				 
				 perPointScoreMap=batchGradeBo.getPerPointScore(template_id);			//得到模版下各指标的分值及最大上限值和最小下限值
			 }
			 HashMap a_pointmap=(HashMap)pointMap.get(object_id);		   					  //得到具有某考核对象的指标权限map
			 if(String.valueOf(this.planVo.getInt("busitype"))!=null&& this.planVo.getInt("busitype")==1)
				 gradelist=batchGradeBo.getCompeGradeDesc();  //能力素质标准标度;
			 else
				 gradelist=batchGradeBo.getGradeDesc();  //绩效标准标度;
			 
			 /* 了解程度 && 总体评价 */
			 ArrayList  nodeKnowDegreeList=new ArrayList();
			 ArrayList  wholeEvalList=new ArrayList();
			 if("true".equals(NodeKnowDegree))
			 {
					nodeKnowDegreeList=batchGradeBo.getExtendInfoValue("1","");			
			 }
			 if("true".equals(this.WholeEval))
			 {
					wholeEvalList=batchGradeBo.getExtendInfoValue("2",GradeClass);				
			 }
			 
			 if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1){
				 if(org.apache.commons.lang.StringUtils.isBlank(batchGradeMainBodybody_id))
				    lowerGradeMainbodyList=getLowerGradeList(userID,object_id,Integer.parseInt(plan_id));//zhaoxg标记
				 else
					lowerGradeMainbodyList=getLowerGradeList(userID,object_id,Integer.parseInt(plan_id),batchGradeMainBodybody_id);//zhanghua
			 }
			 
			 
			 //加标题
			 int num=1;
			 if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
			 {
				 int _extendNum=lowerGradeMainbodyList.size();
				 if("True".equalsIgnoreCase(showDeductionCause))
					 _extendNum+=_extendNum;
				 num+=_extendNum;
				 
			 }
			 else if(this.isLookObjectScore&&operateModel==1&&!this.isShowObjectSelfScore)
		    	num=2;
			
			 if(!this.isShowObjectSelfScore&& "True".equalsIgnoreCase(showDeductionCause))
				 num+=1;
			 
			 if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(scoreflag)) //单选按钮的型式评分
				 num=num-1+gradelist.size();
			 
			 html.append("<tr><td  class='RecordRow'  valign='middle' align='center' height='50'   colspan='"+(lay+num+extendNum+1)+"' ><br><font face=宋体 style='font-weight:bold;font-size:15pt'> "+titleName+" </font> </td></tr>");
			 //输出表头
			
			 
			 if(this.batchGradeBo.getBasicFieldList().size()>0) //主集信息
			 {
				
				 HashMap  otherInfoMap=this.batchGradeBo.getOtherInfoMap(objectList);
				 LazyDynaBean valueBean=null;
				 if(otherInfoMap.get(object_id)!=null)
						valueBean=(LazyDynaBean)otherInfoMap.get(object_id);
				 ArrayList basicFieldList=this.batchGradeBo.getBasicFieldList();
				 LazyDynaBean abean=null;
				 
				 html.append("<tr><td  width='100%'  style='border:0px'  colspan='"+(lay+num+extendNum+1)+"' ><table  width='100%'  style='border-collapse:collapse;'  >");
				 
				 html.append("<tr>");
				 String object_type = this.planVo.getString("object_type");
				 String _itemid = "";
				 if("2".equals(object_type)){//人员
					 _itemid = "a0101";
				 }else{//团队、单位、部门
					 _itemid = "b0110";
				 }
				 for(int i=0;i<basicFieldList.size();i++)
			     {
					        if(i!=0&&i%2==0)
					        {
					        	html.append("</tr><tr>");
					        }
			    			abean=(LazyDynaBean)basicFieldList.get(i);
			    			String itemid=(String)abean.get("item_id");    
						    String context="&nbsp;";
			    			if(valueBean!=null&&valueBean.get(itemid)!=null)
			    				context+=(String)valueBean.get(itemid);
			    			if(itemid.equalsIgnoreCase(_itemid)&& "&nbsp;".equals(context)){//如果b01子集里面基本信息已经删除  那么此处取出考核对象的名字显示 zhaoxg add 2014-10-23
			    				String sql = "select a0101 from per_object where object_id='"+object_id+"'";
			    				RowSet rs = dao.search(sql);
			    				if(rs.next()){
			    					context+=rs.getString("a0101");
			    				}
			    			}
			    			String _str="TableRow_lrt";
			    			String _str2="RecordRow_lrt";
			    			
			    			if(i%2==0)
			    				_str="TableRow_rt";
			    			if(i%2==1)
			    				_str2="RecordRow_lt";
			    			if(i==0||i==1)
			    			{
			    				_str="TableRow_lr";
			    				if(i==0)
			    					_str="TableRow_r";
			    				_str2="RecordRow_lr";
			    				if(i==1)
			    					_str2="RecordRow_l";
			    			}
			    			html.append("<td class='"+_str+"' width='20%'  valign='middle' align='center' >"+(String)abean.get("itemdesc")+"</td>"); 
			    			html.append("<td class='"+_str2+"' width='30%'  valign='middle' align='left' >"+context+"</td>"); 
			    		//	str.append("<td  id='a' class='RecordRow' align='left'  width='"+this.columnWidth+"'  nowrap >"+context+"</td>");
			    			
			     }
				 if(basicFieldList.size()%2==1)
				 {	
					 	if(basicFieldList.size()<3)
					 	{
					 		html.append("<td class='TableRow_lr' width='20%'  valign='middle' align='center' >&nbsp;</td>");
					 		html.append("<td class='RecordRow_l' width='30%'  valign='middle' align='left' >&nbsp;</td>"); 
					 	}
					 	else
					 	{	html.append("<td class='TableRow_lrt' width='20%'  valign='middle' align='center' >&nbsp;</td>"); 
		    			    html.append("<td class='RecordRow_lt' width='30%'  valign='middle' align='left' >&nbsp;</td>"); 
					 	}
				 }
				 html.append("</tr></table>"); 
				 
				 html.append("</td></tr>");
			 }
			 
			 
			 
			 
			 html.append("<tr class='trDeep'  height='25'  >");
			 for(int i=1;i<=lay;i++)
			 {
				 
				 if(i!=lay)
				 {
					 if(lay==2)
						 html.append("<td class='TableRow_2rows'  valign='middle' align='center'  nowrap  width='"+td_width+"'   nowrap >"+ResourceFactory.getProperty("gz.formula.project")+"</td>"); 
					 else
						 html.append("<td class='TableRow_2rows'  valign='middle' align='center'  nowrap    width='"+td_width+"'  nowrap  >"+ResourceFactory.getProperty("gz.formula.project")+i+"</td>"); 
					 
				 }
				 else
					 html.append("<td class='TableRow_2rows'  valign='middle' align='center'  nowrap  width='"+(td_width+150)+"'  nowrap  >&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.wizard.target")+"&nbsp;&nbsp;&nbsp;</td>"); 
			 }
			 total_width=(lay-1)*td_width+(td_width+150);
			 
			 if("True".equalsIgnoreCase(showIndicatorContent))//考核内容
			 {	 
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width+200)+"'  nowrap >"+ResourceFactory.getProperty("jx.khplan.khcontent")+"</td>");
			 	 total_width+=(td_width+150);
			 }
			 if("True".equalsIgnoreCase(showIndicatorDegree))
			 {
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='60' nowrap  >"+ResourceFactory.getProperty("jx.khplan.scaleDesc")+"</td>");
				 total_width+=60;
			 }
			 if("True".equalsIgnoreCase(showIndicatorRole))
			 {
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='50'  nowrap >"+ResourceFactory.getProperty("jx.khplan.stanScore")+"</td>");
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width+200)+"' nowrap  >"+ResourceFactory.getProperty("performance.batchgrade.scoreCause")+"</td>");//评分原则
				 total_width+=(td_width+150);
			 }
			 
			 if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
			 {
				 for(int j=0;j<lowerGradeMainbodyList.size();j++)
					{
						LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
						String body_name=(String)a_bean.get("bodyname");
						String _status=(String)a_bean.get("status");
						
			 			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
							continue;
						String name="";
			 			if(this.planVo.getInt("plan_type")==0)
							name=body_name+ResourceFactory.getProperty("jx.evaluation.evaluation");
						else
							name=(String)a_bean.get("a0101")+ResourceFactory.getProperty("jx.evaluation.evaluation");
			 			 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+td_width+"'  nowrap  >"+name+"</td>"); 
						 total_width+=this.td_width;
						      
						 
					 
						 if("True".equalsIgnoreCase(showDeductionCause))///显示扣分原因
						{
							 name=(String)a_bean.get("a0101")+"_"+ResourceFactory.getProperty("lable.performance.DeductMark");
							if(this.planVo.getInt("plan_type")==0)
								name=body_name+"_"+ResourceFactory.getProperty("lable.performance.DeductMark");
							 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width+50)+"' nowrap  >"+name+"</td>");		
							 total_width+=td_width+50; 
						}
						 
					}
					this.allEvalResultMap=objectAllEvaluateMap(object_id,plan_id,template_id);
			 }
			 else if((this.isLookObjectScore&&operateModel==1))
			 {
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+td_width+"'  nowrap  >"+ResourceFactory.getProperty("performance.batchgrade.selfEvaluate")+"</td>"); /////自我评分
				 total_width+=this.td_width;
				 
				 if("True".equalsIgnoreCase(showDeductionCause)&&!this.isShowObjectSelfScore)
				 {
					 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width+50)+"' nowrap  >"+ResourceFactory.getProperty("jx.evaluation.perEvaluationDesc")+"</td>");////自我评分说明		
					 total_width+=td_width+50;
				 }
				 
			 }
			 
			 
			 //评分 pjf
			 if((!this.isShowObjectSelfScore|| "1".equals(this.opt)) && !this.showObjectLowerScore)
			 {
				 
				 if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(scoreflag)) //单选按钮的型式评分
				 {
					 
					 LazyDynaBean abean=null;
					 for(int i=0;i<gradelist.size();i++)
					 {
						 abean=(LazyDynaBean)gradelist.get(i);
						 String desc=(String)abean.get("gradedesc");
						 int value=100;  
						 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+value+"' nowrap  >"+desc+"</td>");	
						 total_width+=value;
					 }
				 }
				 else
				 {
					 int value=150;
					 if("True".equalsIgnoreCase(showIndicatorRole))
						 value=150; 
					 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+value+"'  nowrap >&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("jx.evaluation.evaluation")+"</td>");	
					 total_width+=value;
				 }
			 
			 }
			//smk 2015.12.04 能力素质考评,混合模式打分页面增加等级列,
			 if("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1){
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width-20)+"' nowrap  >"+ResourceFactory.getProperty("jx.param.degreepro")+"<input type='hidden' id='ScaleToDegreeRule' value='"+ScaleToDegreeRule+"' /></td>");
			 }
			 //评分说明 pjf
			 if("True".equalsIgnoreCase(showDeductionCause) && !this.showObjectLowerScore)
			 {
				 html.append("<td class='TableRow_2rows'  valign='middle' align='center'    width='"+(td_width+50)+"' nowrap  >"+ResourceFactory.getProperty("lable.performance.DeductMark")+"</td>");		
				 total_width+=td_width+50;
			 }
			 html.append("</tr>");
			 
			 int a_no=1;
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] point=(String[])t.next();
				 if("gs".equalsIgnoreCase(clientname)){
					 pointIDs.append("/"+plan_id+object_id+point[0]);
				 }else{
					 pointIDs.append("/"+point[0]);
				 }
				 String[] pointScore=(String[])perPointScoreMap.get(point[0]);
				 
				 ///////////////////各指标的数值范围///////////////////////
				 
			
				 if(point[6]!=null&&point[6].trim().length()>0)
				 {
					 	
						String a_value="";
						try
						{
							String fieldSetID="";
							RowSet rowSet=dao.search("select * from fielditem where itemid='"+point[6]+"'");
							if(rowSet.next())
							{
								fieldSetID=rowSet.getString("fieldsetid");
							}
							String a_sql="";
							if("A01".equalsIgnoreCase(fieldSetID))
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+"  where a0100='"+object_id+"'";
							}
							else
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+" a where a.i9999 =(select max(I9999) from usr"+fieldSetID+" b where a0100='"+object_id+"' and a.a0100=b.a0100)";
							}
							rowSet=dao.search(a_sql);
							if(rowSet.next())
							{
								a_value=rowSet.getString(1);
							}
							if(a_value!=null&&!"".equals(a_value))
							{
							
							java.util.regex.Pattern p=java.util.regex.Pattern.compile("^\\d+$|^\\d+\\.\\d+$");
						    java.util.regex.Matcher m=p.matcher(a_value);   	
							if(m.matches())
							{
								PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
								String per_comTable = "per_grade_template"; // 绩效标准标度
								if(ppo.getComOrPer(template_id,"temp"))
									per_comTable = "per_grade_competence"; // 能力素质标准标度
				    			rowSet=dao.search("select pp.item_id,po.point_id,po.pointname,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue ,po.fielditem"
				    							+" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
				    							+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+template_id+"' "
				    							+" and pp.point_id='"+pointScore[0]+"'  order by pp.seq"	);	
				    			
				    			while(rowSet.next())
				    			{
				    				if("1".equals(pointScore[6])) //定量
				    				{
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
				    					}
				    				}
				    				else						  //定性
				    				{
				    				
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value")*rowSet.getFloat("score"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
				    					}
				    					
				    				}
				    			}
							}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				 }
				 
				 
				 
				 
						if("1".equals(point[2]))   //定量指标
						{
											
							dataArea.append("/");
							if(pointScore[3]!=null)
								dataArea.append(PubFunc.round(pointScore[3],1));
							dataArea.append("*");
							if(pointScore[2]!=null)
								dataArea.append(PubFunc.round(pointScore[2],1));
							dataArea.append("#");
							dataArea.append(pointScore[4]);
							dataArea.append("*");
							dataArea.append(pointScore[5]);	
						}
						else					  //定性指标
						{
							
							dataArea.append("/");
							if(pointScore!=null && pointScore[3]!=null && pointScore[2]!=null)
							{
								dataArea.append(PubFunc.multiple(pointScore[1],pointScore[3],3));
								dataArea.append("*");
								dataArea.append(PubFunc.multiple(pointScore[1],pointScore[2],3));
							}
							else
							{
								dataArea.append("0");
								dataArea.append("*");
								if(pointScore!=null)
									dataArea.append(pointScore[1]);
							}
							dataArea.append("#");
							if(pointScore!=null)
								dataArea.append(pointScore[4]);
							dataArea.append("*");
							if(pointScore!=null)
								dataArea.append(pointScore[5]);
						}
						
						dataArea.append("#");
						if(pointScore!=null && pointScore[1]!=null&&pointScore[7]!=null)
							dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[7])));
						dataArea.append("*");
						if(pointScore!=null && pointScore[1]!=null&&pointScore[8]!=null)
							dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[8])));
						

				 //////////////////////////////////////////////////
				 
				 ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
				 int a_lay=lay;
				 html.append("<tr>");
				 
				 int pointItemLength=pointItemList.size(); 
				 String[] currentItem=null;
				 for(int i=0;i<a_lay;i++)
				 {
					 if(i==0)
					 {
						 String[] item=(String[])pointItemList.get(--pointItemLength);
						 String sign=(String)itemsSignMap.get(item[0]);
						 if(!"1".equals(sign))
						 {				
						
							 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
							 html.append(item[3]);
							 html.append("</td>");
							 currentItem=item;
							 itemsSignMap.put(item[0],"1");
						 }
					 }
					 else if(i!=0&&i!=a_lay-1)
					 {
						 if(--pointItemLength>=0)
						 {
						 
							 String[] item=(String[])pointItemList.get(pointItemLength);
							 String sign=(String)itemsSignMap.get(item[0]);
							 if(!"1".equals(sign))
							 {							
								 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
								 html.append(item[3]);
								 html.append("</td>");								
								 currentItem=item;
								 itemsSignMap.put(item[0],"1");						
							 }
						 }
						 else
						 {
							 html.append("<td class='RecordRow'  valign='middle' align='center'  height='"+td_height+"'    width='"+td_width+"'  >&nbsp;</td>");
						 }					 
					 }
					 else
					 {
						 html.append("<td class='RecordRow'  valign='middle' align='left'  height='"+td_height+"'  ");
					    
					     
					     //没有权限的指标不让其显示
			//		     if(point[2].equals("1")&&point[7]!=null&&point[7].equals("1")&&this.showOneMark.equalsIgnoreCase("false"))
			//		    	 html.append(" style='display:none' ");
						 html.append(" >"); 
						 if(a_no<10)
								 html.append("&nbsp;"+a_no+".&nbsp;&nbsp;&nbsp;");
						 else
								 html.append("&nbsp;"+a_no+".&nbsp;&nbsp;");
						 if("1".equals(point[2])&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
						 {
							 
						 }
						 else
						 {
							 html.append(point[1]);
						     if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
								html.append("<font color='red'>*</font>");
						     
						     
						     if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("showTemplatePointValue")))
							 {
									if(templatePointInfo.get(point[0].toUpperCase())!=null)
									{
										LazyDynaBean _pointBean=(LazyDynaBean)templatePointInfo.get(point[0].toUpperCase());
										if ("0".equals(status)) //分值
										{
											html.append("&nbsp;&nbsp;<font face="+ResourceFactory.getProperty("performance.batchgrade.songFont")+" style='font-weight:normal;font-size:8pt'>" +(String)_pointBean.get("score") + ""+ResourceFactory.getProperty("lable.performance.score")+"</font>");
										}
										else
										{
											String rank=PubFunc.multiple((String)_pointBean.get("rank"), "100", 5);
											html.append("&nbsp;&nbsp;<font face="+ResourceFactory.getProperty("performance.batchgrade.songFont")+" style='font-weight:normal;font-size:8pt'>" +myformat1.format(Double.parseDouble(rank))+"%</font>");
										}
									}
							 }
						     
						 }
						 html.append("&nbsp;&nbsp;&nbsp;"); 
						 html.append("</td>");
					 }
				 }
				 ArrayList a_list=getParticularPointList(pointInfoList,point);
				 
				 if("True".equalsIgnoreCase(showIndicatorContent))
				 {
					 // "考核内容"列设置了靠上显示后，如果只有一行显示会靠上一点，用line-height控制行高保证单行时显示效果 chent 20180324 update
					 html.append("<td class='RecordRow'  valign='top' align='left'    width='"+(td_width+200)+"' style='line-height:30px;'  >");//考核内容
					 
					 html.append(point[9].replaceAll("\r\n","<br>"));
					 
					 html.append("</td>");
				 }
				 
				 
				 if("True".equalsIgnoreCase(showIndicatorDegree))//显示考核指标标度说明
				 {
					 html.append("<td class='RecordRow' id='a"+point[0]+"'  valign='middle' align='center'  height='"+td_height+"'    width='60' ");
					 if("1".equals(point[2])&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
						 html.append("</td>");
					 else
					 {
//						 if(!this.isShowObjectSelfScore)//加这个标度说明出不来  zhaoxg 2014-12-16
					     {
						     if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
						     {
						    	//smk 2015.12.01 能力素质考评,显示指标标度、解释合并
						    	 if(this.planVo.getInt("busitype")==1){
						    		 html.append(" onclick='showDateSelectBox4(this);'  onmouseout='javascript:Element.hide(\"date_panel\");' onmouseover=\"this.style.cursor='pointer'\" ");
						    	 }else{
						    		 if(point[5]==null|| "2".equals(point[5]))
						    			 html.append(" onclick='showDateSelectBox2(this);'  onmouseout='javascript:Element.hide(\"date_panel\");' onmouseover=\"this.style.cursor='pointer'\" ");
						    		 else
						    			 html.append(" onclick='showDateSelectBox(this);'  onmouseout='javascript:Element.hide(\"date_panel\");' onmouseover=\"this.style.cursor='pointer'\" ");
						    	 }
						     }
					     }
				//		 if(point[2].equals("1")&&point[7]!=null&&point[7].equals("1")&&this.showOneMark.equalsIgnoreCase("false"))
				//	    	 html.append(" style='display:none' ");
						 html.append(" >");
						 if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
							 html.append(" <font color='#2E67B9' >"+ResourceFactory.getProperty("jx.khplan.scaleDesc")+"</font></td>");
						 else
							 html.append("</td>");
					 }
				 }
				 if("True".equalsIgnoreCase(showIndicatorRole))
				 { 
					 String _score=point[8];
					 if(_score!=null&&_score.trim().length()>0)
						 _score=myformat1.format(Double.parseDouble(_score));
					 html.append("<td class='RecordRow'  valign='middle' align='center'    width='50'   >"+_score+"</td>");
					 html.append("<td class='RecordRow'  valign='middle' align='left'    width='"+(td_width+200)+"'   >"+point[10].replaceAll("\r\n","<br>")+"</td>");//评分原则
				 }
				 // 评分 pjf
				 HashMap tempmap=batchGradeBo.getPointprivMap(String.valueOf(plan_id),userID);  
				 HashMap _map=(HashMap) tempmap.get("huicong"+object_id);//慧聪网需求   此处获取的是当前主体真正有的指标权限   前面那个是根据下级主体或者对象的权限得来的  zhaoxg 2014-6-20
				 String str=(String) _map.get(point[0]);
				 if(this.planVo.getInt("busitype")==1) //如果是能力素质计划，默认指标权限全为1  20141203 dengcan
					 str="1";
				 String[] tempory={object_id,plan_id,a_status,str};
				 html.append(getPointTD((ArrayList)a_list.get(0),(String)a_list.get(1),tempory,objectResultMap,status,a_pointmap,point[0],scoreflag,pointScore,point));
				 //smk 能力素质考评,混合模式增加等级列
				 if("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1){
					 String bzbd = getbzbd((ArrayList)a_list.get(0),tempory,objectResultMap);
					 bzbd = bzbd==null?"":bzbd;
					 html.append("<td class='RecordRow'  valign='middle' align='center' ><span id="+point[0]+"_dj >&nbsp;"+bzbd+"&nbsp;</span><input type='hidden' id='"+point[0]+"_df' value='"+point[8]+"' /></td>");
				 }
				 //扣分原因
				 if("True".equalsIgnoreCase(showDeductionCause) && !this.showObjectLowerScore)
				 {
					 
					 if("0".equals((String)a_pointmap.get(point[0])))
			    	 {
						     html.append("<td class='RecordRow'  valign='middle' align='center'    width='"+(td_width+50)+"'   >&nbsp;</td>");			 
			    	 }
					 else if("1".equals((String)a_list.get(1))&&point[7]!=null&& "1".equals(point[7]))
					 {
						 	 html.append("<td class='RecordRow'  valign='middle' align='center'    width='"+(td_width+50)+"'   >&nbsp;</td>");			 
					 }
					 else
					 {
						 
						 
						 String[] values=null;
						 String reasons="";
						 String allreasons="";
						 if(objectResultMap!=null&&objectResultMap.get(point[0])!=null)	
						 {
							 values=(String[])objectResultMap.get(point[0]);
							 if(values!=null&&values.length>=8)
							 {
								 reasons=values[7];
								 allreasons=values[7];
							 }
							 if(reasons!=null&&reasons.length()>40)
								reasons=reasons.substring(0,40)+"......";
							 reasons=reasons.replaceAll("<br>","\r\n");
							 reasons=reasons.replaceAll(" ","&nbsp;");
							 allreasons=allreasons.replaceAll("<br>","\r\n");
							 allreasons=allreasons.replaceAll(" ","&nbsp;");
						 }
						 String  valign="top";
						 if(reasons.trim().length()==0)
							 	valign="middle";
						 //zhaoxg标记
						 if("0".equals(str)){
							 if(("1".equals(opt)||isShowObjectSelfScore)&&!this.showObjectLowerScore)
								 html.append("<td class='RecordRow'  valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td id='r_"+point[0]+"' title=\""+allreasons+"\">"+reasons+"</td></tr></table></td>");			 
							 else
								 html.append("<td class='RecordRow'   valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td id='r_"+point[0]+"' title=\""+allreasons+"\">"+reasons+"</td></tr></table></td>");
						 }else{
							 if(("1".equals(opt)||isShowObjectSelfScore)&&!this.showObjectLowerScore)
								 html.append("<td class='RecordRow'  valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td id='r_"+point[0]+"' title=\""+allreasons+"\">"+reasons+"</td></tr><tr><td align='center'  ><img title='"+ResourceFactory.getProperty("jx.evaluation.fillEvaluationDesc")+"' onclick='scoreReason(\""+plan_id+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(object_id))+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(object_id))+"\",\""+point[0]+"\",\"1\")'  src='/images/readwrite_obj.gif' border=0></td></tr></table></td>");			 
							 else
								 html.append("<td class='RecordRow'   valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td id='r_"+point[0]+"' title=\""+allreasons+"\">"+reasons+"</td></tr><tr><td align='center'  ><img  title='"+ResourceFactory.getProperty("jx.evaluation.fillEvaluationDesc")+"' onclick='scoreReason(\""+plan_id+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(object_id))+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(userID))+"\",\""+point[0]+"\",\""+opt+"\")'  src='/images/readwrite_obj.gif' border=0></td></tr></table></td>");
						 }

					 } 
				 }
				 html.append("</tr>  \n ");
				 
				 a_no++;
			 }
			 
				/* 是否有了解程度 */
				String select_id=" ";
				if("true".equals(NodeKnowDegree))
				{
					if(objectResultMap!=null&&objectResultMap.get("know_id")!=null)
						select_id=(String)objectResultMap.get("know_id");
					html.append(getExtendTd(nodeKnowDegreeList,temp[0],select_id,lay+extendNum,ResourceFactory.getProperty("lable.statistic.knowdegree"),"konwDegree"));
				}
				/* 是否有总体评价 */
				select_id=" ";
				if("true".equals(this.WholeEval)|| "True".equalsIgnoreCase(this.DescriptiveWholeEval))
				{
					if("0".equals(this.WholeEvalMode)){//0录入等级
						if(objectResultMap!=null&&objectResultMap.get("whole_grade_id")!=null)
							select_id=(String)objectResultMap.get("whole_grade_id");
						html.append(getExtendTd2(wholeEvalList,temp[0],select_id,lay+extendNum,ResourceFactory.getProperty("lable.statistic.wholeeven"),"wholeEval",plan_id,userID));//总体评价
					}
					else if("1".equals(this.WholeEvalMode)){//录入分值
						html.append(" <tr> <td class='RecordRow' align='center'  colspan='"+(lay+extendNum)+"'    nowrap > ");
						html.append(ResourceFactory.getProperty("lable.statistic.wholeeven"));//总体评价
						html.append("</td>");
						if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
				        {
							ResultSet res=null;
				            for(int j=0;j<lowerGradeMainbodyList.size();j++)
				            {
				                LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
				                String a0100=(String)a_bean.get("a0100");
				                String _status=(String)a_bean.get("status");
				                if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
				                    continue;
				                HashMap pointMap1=(HashMap)this.allEvalResultMap.get(a0100);
				                if(pointMap1==null)
				                    pointMap1=new HashMap();
				                String whole_str="";
				                String whole_score="";//总分
				                //String sql="select * from per_mainbody where plan_id='"+plan_id+"' and object_id='"+object_id+"' and mainbody_id='"+a0100+"'";
				                //res=dao.search(sql);
				                //while(res.next()){
				                	//whole_score=res.getString("whole_score");
				                //}
				                if(pointMap1.get("whole_str")!=null)
				                    whole_str=(String)pointMap1.get("whole_str");
				                html.append("<td  class='RecordRow' align='center' >");
				                //html.append(PubFunc.round(whole_score, Integer.parseInt(KeepDecimal)));
				                html.append(whole_str);
				                html.append("&nbsp;</td>");

				                if("True".equalsIgnoreCase(showDeductionCause))
				                {
				                    html.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
				                }
				                
				            }
			                if("0".equals(this.opt)){
			                	
		                		String tempScore = PubFunc.round(wholeEvalScore, Integer.parseInt(KeepDecimal));
					        	//String tempScore = wholeEvalScore;
								String tempstatus = (String)this.mainbodyBean.get("status");
			                	if("0".equals(tempstatus))
			                		tempScore = "";
								html.append("<td vAlign='bottom' class='RecordRow' align='left'>&nbsp;&nbsp;&nbsp;<input type='text' id='wholeEvalScoreId' value='"+tempScore+"'  style='width: 65.0px' name='wholeEvalScore' ");
								if("2".equals(a_temp[2])|| "4".equals(a_temp[2])|| "7".equals(a_temp[2])){
									html.append(" disabled ");

								}
								html.append(" />");
								String tempUid = "~"+SafeCode.encode(PubFunc.convertTo64Base(userID));
								String tempObj = "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0]));
//								if(this.isLookObjectScore&&operateModel==1){
//									if((!this.performanceType.equals("1")&&this.DescriptiveWholeEval.equalsIgnoreCase("True"))||opt.equals("1"))
//						    		{
//						    			if(opt.equals("1"))
//						    				html.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+plan_id+"','"+tempObj+"','"+tempUid+"')\" > ");
//						    			else
//						    				html.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+plan_id+"','"+tempObj+"','"+tempObj+"')\" > ");
//						    		}
//								}
								if(!this.isShowObjectSelfScore && !this.showObjectLowerScore){
									if(!"1".equals(this.performanceType)&& "True".equalsIgnoreCase(this.DescriptiveWholeEval)){
										html.append("&nbsp;&nbsp;<img id='showDescriptive' style=\"cursor:hand;"); 
										/*if(a_temp[2].equals("2")||a_temp[2].equals("4")||a_temp[2].equals("7")){
											html.append("display:none; ");
										}*/
										html.append("\" src=\"/images/table.gif\"   onclick=\"javascript:showWindow('"+plan_id+"','"+tempObj+"','"+tempUid+"')\" > ");
									}
								}
								html.append("</td>");
			                }else if("1".equals(this.opt)){
			                	
			                }
				        }else{
							//String tempScore = PubFunc.round(wholeEvalScore, Integer.parseInt(KeepDecimal));
							String tempUid = "~"+SafeCode.encode(PubFunc.convertTo64Base(userID));
							String tempObj = "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0]));
				        	if(this.isLookObjectScore&&operateModel==1){
				        		String whole_score=(String) objectSelfCore.get("whole_score");
				        		whole_score=PubFunc.round(whole_score, Integer.parseInt(KeepDecimal)); ///总体评价录入分值 按计算规则控制小数位控制  zzk 2014/2/11
				        		html.append("<td vAlign='bottom' class='RecordRow' align='left'>&nbsp;&nbsp"+whole_score);
				        		html.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+plan_id+"','"+tempObj+"','"+tempObj+"')\" > ");
				                if("True".equalsIgnoreCase(showDeductionCause))
				                {
				                    html.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
				                }
				        	}
				        	String tempScore = wholeEvalScore;
				        	tempScore = PubFunc.round(wholeEvalScore, Integer.parseInt(KeepDecimal));
							String tempstatus = (String)this.mainbodyBean.get("status");
		                	if("0".equals(tempstatus))
		                		tempScore = "";
							html.append("<td vAlign='bottom' class='RecordRow' align='left'>&nbsp;&nbsp;&nbsp;<input type='text' id='wholeEvalScoreId' value='"+tempScore+"'  style='width: 65.0px' name='wholeEvalScore' ");
							if("2".equals(a_temp[2])|| "4".equals(a_temp[2])|| "7".equals(a_temp[2])){
								html.append(" disabled ");

							}
							html.append(" />");
//							if(this.isLookObjectScore&&operateModel==1){
//								if((!this.performanceType.equals("1")&&this.DescriptiveWholeEval.equalsIgnoreCase("True"))||opt.equals("1"))
//					    		{
//					    			if(opt.equals("1"))
//					    				html.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+plan_id+"','"+tempObj+"','"+tempUid+"')\" > ");
//					    			else
//					    				html.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+plan_id+"','"+tempObj+"','"+tempObj+"')\" > ");
//					    		}
//							}
							if(!this.isShowObjectSelfScore && !this.showObjectLowerScore){
								if(!"1".equals(this.performanceType)&& "True".equalsIgnoreCase(this.DescriptiveWholeEval)){
									html.append("&nbsp;&nbsp;<img id='showDescriptive' style=\"cursor:hand;"); 
									/*if(a_temp[2].equals("2")||a_temp[2].equals("4")||a_temp[2].equals("7")){
										html.append("display:none; ");
									}*/
									html.append("\" src=\"/images/table.gif\"   onclick=\"javascript:showWindow('"+plan_id+"','"+tempObj+"','"+tempUid+"')\" > ");
								}
							}
							html.append("</td>");
				        }

						html.append(" </tr>");
					}
					
				}
				//不打分原因
				if(showNoMarking!=null&& "true".equalsIgnoreCase(showNoMarking))
				{
					html.append(getNotMarkTd(object_id,lay+extendNum,plan_id,userID,a_status,performanceType));
					
				}
				//建议和意见
				if("1".equals(this.performanceType))
				{
					html.append(getNotMarkTd2(object_id,lay+extendNum,plan_id,userID,a_status,performanceType));
					
				}
				
				
				
				HashMap objRelatePlanValue=new HashMap();
				String ScoreShowRelatePlan=(String)htxml.get("ScoreShowRelatePlan"); //多人评分显示引入计划得分
				if("True".equalsIgnoreCase(ScoreShowRelatePlan))
				{
					objRelatePlanValue=this.batchGradeBo.getObjRelatePlanValue(htxml,Integer.parseInt(plan_id)); 
					LazyDynaBean abean=(LazyDynaBean)objRelatePlanValue.get(object_id);
			    	ArrayList planlist = loadxml.getRelatePlanValue("Plan");
			    	LazyDynaBean abean0=null;
			    	
			    	
			    	int _num=0;
		    		if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
					{
						for(int j=0;j<lowerGradeMainbodyList.size();j++)
						{
							LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
							String a0100=(String)a_bean.get("a0100");
							String _status=(String)a_bean.get("status");
							if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
								continue; 
							_num++;
							if("True".equalsIgnoreCase(showDeductionCause))
							{
								_num++;
							}
						}
						
					}
					else if(this.isLookObjectScore&&operateModel==1)
			    	{
						if("1".equalsIgnoreCase(this.opt))
						{
							_num++;
						}
						else
							_num++;
						if("True".equalsIgnoreCase(showDeductionCause))
						{
							_num++;
						}
			    	}

					if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
					{
						 
						if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(scoreflag))
						{
							int n=0;
							if("True".equalsIgnoreCase(showDeductionCause))
								 n=1;  
							_num+=this.gradelist.size()+n;
						}
					    else
					    {
					    	_num++;
					    }
					}
			    	 
			    	for(int i=0;i<planlist.size();i++)
			    	{
			    		abean0=(LazyDynaBean)planlist.get(i);
			    		String id=(String)abean0.get("id");
			    		String Name=(String)abean0.get("Name"); 			
			    		 
			    		String score=(String)abean.get("G_"+id);
			    		html.append("<tr> <td class='RecordRow' align='center'  colspan='"+(lay+extendNum)+"'    nowrap >"+Name+"</td>");
			    		
			    		if(_num==0)
			    			_num=1;
			    		html.append("<td class='RecordRow' align='left'  colspan='"+_num+"'    nowrap >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+score+"</td></tr>");
						 
			    	}
					
				
				}
				
				
				
				
				if("true".equalsIgnoreCase(isShowTotalScore)&&(("2".equals(a_status)&& "true".equalsIgnoreCase(isShowSubmittedScores))||!"2".equals(a_status)))
				{
					this.batchGradeBo.getDynaRankInfoMap(plan_id);
					this.batchGradeBo.getObjectInfoMap(plan_id);
					//添加总分功能
					float selfScore=0f;
					if((this.isLookObjectScore&&operateModel==1)|| "1".equals(opt))
					{
						 if(this.planVo!=null&&(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4))
						 {
							
							 RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
							 if(rowSet.next())
								 selfScore=this.batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),rowSet.getString("mainbody_id"),template_id,object_id,userView);
						 }
						 else
							 selfScore=this.batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),object_id,template_id,object_id,userView);
					}
					
					
					html.append("<tr> <td class='RecordRow' align='center'  colspan='"+(lay+extendNum)+"'    nowrap >");
					html.append(ResourceFactory.getProperty("label.zp_exam.sum_score"));
					html.append("</td>");
					
					
					if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
					{
						for(int j=0;j<lowerGradeMainbodyList.size();j++)
						{
							LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
							String a0100=(String)a_bean.get("a0100");
							String _status=(String)a_bean.get("status");
							if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
								continue;
							
							float score=this.batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),a0100,template_id,object_id,userView);
							html.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal))+"</td>");
							
							if("True".equalsIgnoreCase(showDeductionCause))
							{
								html.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
							}
						}
						
					}
					else if(this.isLookObjectScore&&operateModel==1)
			    	{
						if(this.objectSelfCore.size()==0)
						{
							html.append("<td class='RecordRow' align='center' >&nbsp;</td>");
						}
						else
						{
							if("1".equalsIgnoreCase(this.opt))
							{
								float score=this.batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),userID,template_id,object_id,userView);
								html.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal))+"</td>");
								
							}
							else
								html.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(selfScore),Integer.parseInt(KeepDecimal))+"</td>");
						}
						if("True".equalsIgnoreCase(showDeductionCause))
						{
							html.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
						}
			    	}
					
					
					
					if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
					{
						float score=this.batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),userID,template_id,object_id,userView);
						if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(scoreflag))
						{
							int n=0;
							if("True".equalsIgnoreCase(showDeductionCause))
								 n=1; 
							html.append("<td class='RecordRow' colspan='"+(this.gradelist.size()+n)+"'   id='ascore' align='left'  ><font color='blue'>");
						
						}
					    else{
							int n=1;
							if("True".equalsIgnoreCase(showDeductionCause))
								 n=2; 
					    	html.append("<td class='RecordRow' colspan='"+n+"'  id='ascore' align='left'  ><font color='blue'>");
					    }
						html.append(PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal)));
						html.append("</font></td>");
					}
					html.append("</tr>");
				}
				
				
				
				
				String level=(String)this.mainbodyBean.get("level");
				//个人目标作为评分标准
				if("true".equals(noteIdioGoal))
				{
					if("False".equalsIgnoreCase(relatingTargetCard) || "1".equalsIgnoreCase(relatingTargetCard))
					{
						
						boolean summaryIsWrite=summaryIsWrite(object_id,plan_id,1,this.planVo.getInt("object_type"));
						
						if((this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4)&& "-1".equals(this.mainbodyBean.get("body_id")))
						{
							goalComment.append("<a href=\"/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal2\">");
							goalComment.append(ResourceFactory.getProperty("lable.performance.perGoal"));
							goalComment.append("</a>");
						}
						else if(userID.equalsIgnoreCase(object_id)&&(!"1".equals(level)||("1".equals(level)&&summaryIsWrite)))
						{
							goalComment.append("<a href=\"/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal2\">");
							goalComment.append(ResourceFactory.getProperty("lable.performance.perGoal"));
							goalComment.append("</a>");
						}
						else
						{
							goalComment.append("<a href='/selfservice/performance/view_summary.do?b_query=link&planNum=");
							goalComment.append(plan_id);
							goalComment.append("&objectId=");
							goalComment.append("~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])));
							if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4)
								goalComment.append("&optUrl=goal2' ");
							else
								goalComment.append("&optUrl=goal' ");
							goalComment.append(" target='_blank'>");
							goalComment.append(ResourceFactory.getProperty("lable.performance.perGoal"));
							goalComment.append("</a>");		
						}
					//	
					//		goalComment.setLength(0);
					}
					else
					{
						goalComment.append(getGoalCardUrl(plan_id,object_id));
					}
				}
				
				
				
				/* 个人总结评价作为评分标准*/
				if("True".equals(SummaryFlag))
				{
				
					boolean summaryIsWrite=summaryIsWrite(object_id,plan_id,2,this.planVo.getInt("object_type"));
					
					
					if((this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4)&& "-1".equals(this.mainbodyBean.get("body_id")))
					{
						personalComment.append("<a href=\"/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=summary2\">");
						if("0".equals(performanceType))
						{
							String info=SystemConfig.getPropertyValue("per_examineInfo");
							if(info==null||info.length()==0)
								personalComment.append(ResourceFactory.getProperty("lable.performance.perSummary"));
							else
								personalComment.append(info);
							
						}
						else
							personalComment.append(ResourceFactory.getProperty("label.reportwork.report"));
						personalComment.append("</a>");
					}
					else if(userID.equalsIgnoreCase(object_id)&&(!"1".equals(level)||("1".equals(level)&&summaryIsWrite))  )
					{
						personalComment.append("<a href=\"/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=summary2\">");
						if("0".equals(performanceType))
						{
							String info=SystemConfig.getPropertyValue("per_examineInfo");
							if(info==null||info.length()==0)
								personalComment.append(ResourceFactory.getProperty("lable.performance.perSummary"));
							else
								personalComment.append(info);
							
						}
						else
							personalComment.append(ResourceFactory.getProperty("label.reportwork.report"));
						personalComment.append("</a>");
					}
					else
					{
						personalComment.append("<a href='/selfservice/performance/view_summary.do?b_query=link&planNum=");
						personalComment.append(plan_id);
						
						if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4)
							personalComment.append("&optUrl=summary2");
						else
							personalComment.append("&optUrl=summary");
						personalComment.append("&objectId=");
						personalComment.append("~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])));
						personalComment.append("' target='_blank'>");
						if("0".equals(performanceType))
						{
							String info=SystemConfig.getPropertyValue("per_examineInfo");
							if(info==null||info.length()==0)
								personalComment.append(ResourceFactory.getProperty("lable.performance.perSummary"));
							else
								personalComment.append(info);
							
						}
						else
							personalComment.append(ResourceFactory.getProperty("label.reportwork.report"));
						
						personalComment.append("</a>");		
					}
				//	if(this.planVo.getInt("object_type")==1)
				//		personalComment.setLength(0);
					
				}
				
				// 2013.11.09 pjf 
			html.append("<div>");
			if(SystemConfig.getPropertyValue("clientName")!=null&& "hkyh".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) {
				String  plan_descript_content = this.planDesc;
				if(plan_descript_content!=null && !"".equals(plan_descript_content)) {
					html.append("<table width='70%' align='left' border='0'>");
					html.append("<tr><td>");
					html.append("计划说明&nbsp;<a href='javascript:displayIMG(\"planDesc\",\"but2\");'><img id='but2' src='/images/expand_pm.gif' border='0' style='cursor:hand'/></a>");
					html.append("</td></tr><tr><td>");
					html.append("<table id='planDesc' style='display=block' width='100%' border='0' cellspacing='0'  align='left' cellpadding='0' class='ListTable'>");
					html.append(" <tr><td align='left'>");
					html.append("<textarea name='planDesc' rows='10' cols='95'  readonly='readonly' >");
					html.append(plan_descript_content);
					html.append(" </textarea></td></tr></table></td></tr><tr>");
					html.append("<td></td></tr></table>");
				}
			}
			 html.append("</div>");
			 html.append("</table>");
			 
			 //处理下面的三个按钮“填表说明，保存，提交”。国税要把填表说明改成超级链接的形式
			 if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
			 {
				 
				 if("gs".equalsIgnoreCase(clientname)){///如果是广东国税    只出现一个保存和提交按钮
					 if(!"2".equals(a_status) && !"7".equals(a_status) &&!"4".equals(a_status) ){
						 this.setIsHasSaveButton("1");
					 }
					 //处理填表说明这个超链接
					 RecordVo planVo=getPlanVo(plan_id);
					 String plan_id_code=PubFunc.encryption(plan_id);
					 if(planVo.getString("descript")!=null&&planVo.getString("descript").length()>0)
						 this.setFillTableExplain("<a href='javascript:planDescript(\""+plan_id_code+"\")'>"+"填表说明"+"</a>");
				 }else {
					 html.append("<table border='0' cellspacing='0' align='left' cellpadding='0'>");
					 html.append("<tr>");
					 html.append("<td id=\"buttonsid\" align='left' style='height:35px'>");				 
					 
				//	 html.append("<br>");
					 if(!"2".equals(a_status)&&!"7".equals(a_status))
					 {
						 html.append("<span id=\"buttons\"  ");
			//			 if(a_status.equals("4"))                  //20141204 dengcan
			//				 html.append("style='display:none'");
						 if("hkyh".equalsIgnoreCase(clientname)) { // 2013.11.09 pjf
							 if(planVo.getString("descript")!=null&&planVo.getString("descript").length()>0){
								 html.append(" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.temporary.save")+"\" onclick=\"check(1)\" class=\"mybutton\">");
							 }else 
								 html.append(" ><input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.temporary.save")+"\" onclick=\"check(1)\" class=\"mybutton\">");
						 } else
							 html.append(" ><input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.temporary.save")+"\" onclick=\"check(1)\" class=\"mybutton\">");
						 html.append("&nbsp;<input type=\"button\" name=\"b_refer\" value=\""+ResourceFactory.getProperty("lable.welcomeinv.sumbit")+"\" onclick=\"check(2)\" class=\"mybutton\"></span> ");
					 }
		//			 html.append("</td><td align='left' style='height:35px'>");
					 
					 RecordVo planVo=getPlanVo(plan_id);
					 String plan_id_code=PubFunc.encryption(plan_id);
					 if(!"hkyh".equalsIgnoreCase(clientname)) { // 2013.11.09 pjf
						 if(planVo.getString("descript")!=null&&planVo.getString("descript").length()>0)
							 html.append("<input type=\"button\" name=\"b_descript\" value=\""+ResourceFactory.getProperty("lable.performance.fillDeclare")+"\" onclick=\"planDescript('"+plan_id_code+"')\" class=\"mybutton\">");
					 }
					 String bosflg = this.userView.getBosflag();
					 if("bi".equalsIgnoreCase(bosflg)) {
						 html.append("&nbsp;<input type=\"button\" name=\"b_close\" value=\""+ResourceFactory.getProperty("button.return")+"\" onclick=\"window.location.href='/templates/index/bi_portal.do?b_query=link'\" class=\"mybutton\">"); 
					 }else if("8".equals(returnflag)){
						 html.append("&nbsp;<input type=\"button\" name=\"b_close\" value=\""+ResourceFactory.getProperty("button.return")+"\" onclick=\"window.location.href='/templates/index/hcm_portal.do?b_query=link'\" class=\"mybutton\">"); 
					 }else if("frontPanel".equalsIgnoreCase(fromModel))
						 html.append("&nbsp;<input type=\"button\" name=\"b_close\" value=\""+ResourceFactory.getProperty("button.return")+"\" onclick=\"goback()\" class=\"mybutton\">");					 
					 html.append("</td></tr></table>");
				 }
			 }
			
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		 }
		 if("gs".equalsIgnoreCase(clientname)){//如果是国税
			 list.add("<table id='"+plan_id+object_id+"table1' class='ListTable_self3' width='"+total_width+"' >"+html.toString());
		 }else{
			 list.add("<table class='ListTable_self3' width='"+total_width+"' >"+html.toString());
		 }
		 
		 list.add(personalComment.toString());
		 list.add(isNull);
		 list.add(scoreflag);
		 list.add(dataArea.toString());
		 list.add(NodeKnowDegree);
		 list.add(WholeEval);
		 list.add(limitation);
		 list.add(GradeClass);
		 list.add(String.valueOf(lay));
		 list.add(ScoreBySumup);
		 list.add(goalComment.toString());
		 list.add(pointIDs.toString());
		 return list;
	}
	
	//smk 2015.12.04 查询标准标度等级
	private String getbzbd(ArrayList tempList,String[] temp,HashMap objectResultMap) {
		for(Iterator t=tempList.iterator();t.hasNext();)
		{
			String[] a_temp=(String[])t.next();
			
			// 提交后的分数是否显示
	
    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
    			{
    				String[] values=(String[])objectResultMap.get(a_temp[1]);
    				if(values!=null)
    				{
	    				if(values[6]!=null&&values[6].equals(a_temp[5]))
	    				{
	    					
							return a_temp[13];
	    				}
    				}
    			}  	
    			else
    			{
    				if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
    				{
	    				 if("1".equals(this.BlankScoreOption))
						 {
	    					 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
	    					 String defaultValue=(String)abean.get("gradecode");
		    				 if(defaultValue!=null)
			    			 {
				    				if(defaultValue.equals(a_temp[5]))
				    				{
				    					return a_temp[13];
				    				}
			    			 }
		    				 
						 }
		    			 else  if("2".equals(this.BlankScoreOption))
		    			 {
							 if(this.BlankScoreUseDegree!=null)
			    			 {
				    				if(this.BlankScoreUseDegree.equals(a_temp[5]))
				    				{
				    					return a_temp[13];
				    				}
			    			 }
		    			 }
    				}
    			}
    			
    					
		}
		return "";
	}
	

	/**
	 * 移除小数点后面的零
	 * @param number
	 */
	public String moveZero(String number)
	{
		DecimalFormat df = new DecimalFormat("###############.#####"); 
		if(number==null||number.length()==0)
			return "";
		return df.format(Double.parseDouble(number));
	}
	
	/**
	 * 取得 各考核主体打分结果
	 * @param object_id
	 * @return
	 */
	public HashMap objectAllEvaluateMap(String object_id,String plan_id,String template_id)
	{
		HashMap map=new HashMap();
		HashMap allMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer mainbodyids=new StringBuffer("");
			Hashtable htxml=new Hashtable();
		    LoadXml loadxml=null;
		    if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
		    {
			   loadxml = new LoadXml(this.conn,plan_id);
			   BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
		    }
		    else
		    {
			   loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
		    }
	//	 LoadXml loadxml=new LoadXml(conn,plan_id);
		 
		 
		    htxml=loadxml.getDegreeWhole();
		    String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位	
			RowSet rowSet=null;
			
			{
				PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
				String per_comTable = "per_grade_template"; // 绩效标准标度
				if(ppo.getComOrPer(template_id,"temp"))
					per_comTable = "per_grade_competence"; // 能力素质标准标度
				String sql="select per_table_"+plan_id+".*,per_grade.gradedesc,"+per_comTable+".gradedesc desc2 from per_table_"+plan_id+" left join per_grade "
						+" on per_table_"+plan_id+".point_id=per_grade.point_id and per_table_"+plan_id+".degree_id=per_grade.gradecode "
						+" left join "+per_comTable+""
						+" on per_grade.gradecode="+per_comTable+".grade_template_id "
						+" where object_id='"+object_id+"' order by mainbody_id";
				rowSet=dao.search(sql);
				LazyDynaBean abean=new LazyDynaBean();
				ArrayList mainbodyList=new ArrayList();
				String mainbody_id="";
				ResultSetMetaData dt=rowSet.getMetaData();
				boolean isreasons=false;
				for(int i=0;i<dt.getColumnCount();i++)
				{
					if("reasons".equalsIgnoreCase(dt.getColumnName(i+1)))
						isreasons=true;
				}
				
				while(rowSet.next())
				{
					if(mainbody_id.length()==0)
						mainbody_id=rowSet.getString("mainbody_id");
					if(!mainbody_id.equals(rowSet.getString("mainbody_id")))
					{
						mainbodyList.add(mainbody_id);
						mainbodyids.append(",'"+mainbody_id+"'");
						allMap.put(mainbody_id,map);
						mainbody_id=rowSet.getString("mainbody_id");
						map=new HashMap();
					}
					abean=new LazyDynaBean();
					String id=rowSet.getString("id");
					String score=rowSet.getString("score")!=null?moveZero(rowSet.getString("score")):"";
					String amount=rowSet.getString("amount")!=null?moveZero(rowSet.getString("amount")):"";
					String point_id=rowSet.getString("point_id");
					String degree_id=rowSet.getString("degree_id")!=null?rowSet.getString("degree_id"):"";
					String gradedesc=Sql_switcher.readMemo(rowSet,"gradedesc");
					String desc2=Sql_switcher.readMemo(rowSet,"desc2");
					String reasons="";
					if(isreasons)
						reasons=Sql_switcher.readMemo(rowSet,"reasons");
					abean.set("id", id);
					abean.set("point_id", point_id);
					abean.set("score", score);
					abean.set("amount", amount);
					abean.set("degree_id", degree_id);
					abean.set("reasons",reasons);
					abean.set("gradedesc",gradedesc);
					abean.set("desc2",desc2);
					map.put(point_id,abean);
				}
				if(mainbody_id.trim().length()>0)
				{
					mainbodyids.append(",'"+mainbody_id+"'");
					mainbodyList.add(mainbody_id);
					allMap.put(mainbody_id,map);
				}
			}
			
			if(mainbodyids.length()==0)
			{
				for(int j=0;j<lowerGradeMainbodyList.size();j++)
				{
					LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
					String a0100=(String)a_bean.get("a0100");
					mainbodyids.append(",'"+a0100+"'");
				}
			}
			
			
			if(mainbodyids.length()>0)
			{
				ArrayList  nodeKnowDegreeList=getExtendInfoValue("1","");
				String     GradeClass=this.GradeClass;					//等级分类ID
				ArrayList  wholeArrayList=getExtendInfoValue("2",GradeClass);
				rowSet=dao.search("select  *  from per_mainbody where   mainbody_id in ("+mainbodyids.substring(1)+") and plan_id="+plan_id+" and object_id='"+object_id+"'");
				while(rowSet.next())
				{
					String a_mainbody_id=rowSet.getString("mainbody_id");
					
					if(allMap.get(a_mainbody_id)==null)
					{
						allMap.put(a_mainbody_id,new HashMap());
					}
					
					if(allMap.get(a_mainbody_id)!=null)
					{
						HashMap amap=(HashMap)allMap.get(a_mainbody_id);
						
						
						if("true".equalsIgnoreCase(this.NodeKnowDegree))  //了解程度
						{
							
							String know_id="";
							if(rowSet.getString("know_id")!=null)
								know_id=rowSet.getString("know_id");
							String know_str="";
							for(int i=0;i<nodeKnowDegreeList.size();i++)
							{
								String[] temp=(String[])nodeKnowDegreeList.get(i);
								
								if(temp[0].equals(know_id))
									know_str=temp[1];
							}
							amap.put("know_str",know_str);
						}
						if("true".equalsIgnoreCase(this.WholeEval)|| "true".equalsIgnoreCase(this.DescriptiveWholeEval))  //总体评价
						{
							
								String whole_id="";
								String whole_str="";
								if(rowSet.getString("whole_grade_id")!=null)
									whole_id=rowSet.getString("whole_grade_id");
								String a_bodyid=rowSet.getString("body_id");
								
								if("true".equalsIgnoreCase(this.WholeEval))
								{
									if("0".equals(WholeEvalMode)) {  //2013.12.17 pjf
										for(int i=0;i<wholeArrayList.size();i++)
										{
											String[] temp=(String[])wholeArrayList.get(i);
											
											if(temp[0].equals(whole_id))
												whole_str=temp[1];
											
										}
									} else{
										String tempScore = PubFunc.round( rowSet.getString("whole_score"), Integer.parseInt(KeepDecimal));
										whole_str = tempScore;
									}
								}
								if("true".equalsIgnoreCase(this.DescriptiveWholeEval))
								{
									whole_str+="&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"  ";
									whole_str+=" onclick=\"javascript:showWindow2('"+plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(a_mainbody_id))+"',"+a_bodyid+")\" >  ";
								}
								amap.put("whole_str",whole_str);
							}
					      
						}						
						
					}
				}
				
		
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return allMap;
	}
	
	
	
	

	
	/**
	 * 得到 了解程度 或 总体评价的选项信息
	 * @param flag				 1:了解程度   2：总体评价
	 * @param gradeClass		 等级分类ID
	 * @return
	 */
	public ArrayList getExtendInfoValue(String flag,String gradeClass)  throws GeneralException
	{
		ArrayList  list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			if("1".equals(flag))
				rowSet=dao.search("select know_id,name from per_know where status=1 order by seq ");
			else if("2".equals(flag))
				rowSet=dao.search("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass);			
			while(rowSet.next())
			{
				String[] temp=new String[2];
				temp[0]=rowSet.getString(1);
				temp[1]=rowSet.getString(2);
				list.add(temp);
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	
	
	/**
	 * 
	 * @param object_id
	 * @param plan_id
	 * @param flag  1:目标  2报告
	 * @return
	 */
	private boolean summaryIsWrite(String object_id,String plan_id,int flag,int object_type)
	{
		boolean summaryIsWrite=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(object_type!=2)
			    object_id=getUnManager(plan_id,object_id);
			String sql="select state from  per_article   where plan_id="+plan_id+" and fileflag=1 and Article_type="+flag+" and  a0100='"+object_id+"'" ;
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String state=rowSet.getString("state")!=null?rowSet.getString("state"):"";
				if(!"2".equals(state)&&!"1".equals(state))
					summaryIsWrite=true;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return summaryIsWrite;
	}
	
	

	public String getUnManager(String plan_id,String object_id)
	{
		String a0100="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1   ");
			if(rowSet.next())
				a0100=rowSet.getString(1);
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a0100;
	}
	
	
	
	
	
	
	/**
	 * 取得对象关联的目标卡
	 * @param planid
	 * @param object_id
	 * @return
	 */
	public String getGoalCardUrl(String planid,String object_id)
	{
		
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_plan where plan_id="+planid);
			int cycle = 0;
    		String theyear = "";
    		String themonth = "";
    		String thequarter = "";
			if(rowSet.next())
			{
				cycle=rowSet.getInt("cycle");
				theyear=rowSet.getString("theyear");
				themonth=rowSet.getString("themonth");
				thequarter=rowSet.getString("thequarter");
			}
			StringBuffer sql = new StringBuffer("");
			if(cycle!=7)
			{
			//	sql.append("select distinct per_plan.plan_id from per_plan,per_object,per_mainbody ");
		    	sql.append("select per_plan.plan_id,per_object.sp_flag posp_flag,per_mainbody.sp_flag pmsp_flag from per_plan,per_object,per_mainbody ");
		    	sql.append(" where per_plan.plan_id=per_object.plan_id and per_object.object_id=per_mainbody.object_id ");
		    	sql.append(" and per_plan.plan_id=per_mainbody.plan_id and method=2 ");
		    //	sql.append(" and ( per_object.sp_flag='03' or per_object.sp_flag='06' ) and (per_mainbody.sp_flag='03') ");
		    	sql.append(" and per_object.object_id='"+object_id+"' ");		    	
		    			    			    	
		    	if(relatingTargetCard!=null && relatingTargetCard.trim().length()>0 && "3".equalsIgnoreCase(relatingTargetCard))
				{
		    		sql.append(" and per_mainbody.mainbody_id = '"+ this.userView.getA0100()+"' ");
			    	sql.append(" and (per_mainbody.status is not null and per_mainbody.status<>'' and per_mainbody.status<>'0' )");
				}
		    	else if(relatingTargetCard!=null && relatingTargetCard.trim().length()>0 && "2".equals(relatingTargetCard) && "1".equals(showYPTargetCard))//如果是 查看对象目标卡，则要保证考核对象已经自评
				{
					if(this.planVo.getInt("object_type")==2){//如果是人员
					    if(Sql_switcher.searchDbServer()!=2) {//如果不是oracle库
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level='5') and per_mainbody.object_id=per_mainbody.mainbody_id)");//自评
					    } else {
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level_o='5') and per_mainbody.object_id=per_mainbody.mainbody_id)");//自评
					    }
					}else{//如果是团队
					    if(Sql_switcher.searchDbServer()!=2) {
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level='5') and per_mainbody.object_id<>per_mainbody.mainbody_id )");//自评
					    } else {
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level_o='5') and per_mainbody.object_id<>per_mainbody.mainbody_id )");//自评
					    }
					}
			    	
					sql.append(" and (per_mainbody.status is not null and per_mainbody.status<>'' and per_mainbody.status='2' )");//提交了
				}
				if(cycle==0)  //年度
				{
					sql.append(" and theyear='"+theyear+"' ");				
				}
				if(cycle==1)  //半年
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and ( ( cycle=1 and Thequarter='"+thequarter+"' ) ");
					if("01".equals(thequarter)|| "1".equals(thequarter))
					{
						sql.append(" or (cycle=2 and ( Thequarter='01' or Thequarter='02') ) ");
						sql.append(" or (cycle=3 and Themonth in ('01','02','03','04','05','06') ) ");
					}
					else
					{
						sql.append(" or (cycle=2 and ( Thequarter='03' or Thequarter='04') ) ");
						sql.append(" or (cycle=3 and Themonth in ('07','08','09','10','11','12') ) ");
					}					
					sql.append(" )");
				}
				if(cycle==2)  //季度
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and ( ( cycle=2 and Thequarter='"+thequarter+"' ) ");
					if("01".equals(thequarter)|| "1".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('01','02','03') ) ");
					}
					else if("02".equals(thequarter)|| "2".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('04','05','06') ) ");
					}
					else if("03".equals(thequarter)|| "3".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('07','08','09') ) ");
					}
					else if("04".equals(thequarter)|| "4".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('10','11','12') ) ");
					}					
					sql.append(" )");
				}
				if(cycle==3)  //月
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and cycle=3 and Themonth='"+themonth+"' ");
				}
				rowSet = dao.search(sql.toString());
				HashMap planidMap = new HashMap();
				String plan_id = "";
				while(rowSet.next())
				{
					String pid = rowSet.getString("plan_id");
					String posp_flag = rowSet.getString("posp_flag");
					String pmsp_flag = rowSet.getString("pmsp_flag");
					LoadXml loadxml = new LoadXml(this.conn, pid);																					
					Hashtable htxml = loadxml.getDegreeWhole();					 
					String noApproveTargetCanScore = (String)htxml.get("NoApproveTargetCanScore"); // 目标卡未审批也允许打分 True, False, 默认为 False
					
					if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
					{
						if((posp_flag!=null && posp_flag.trim().length()>0 && ("03".equalsIgnoreCase(posp_flag) || "06".equalsIgnoreCase(posp_flag))))
						{}
						else
							continue;
					}
					
					if(planidMap.get(pid)==null)
						plan_id += "," + pid;
				    planidMap.put(pid,"1");
				}
				if(plan_id.length()>0)
				{
					str.append("<a href='#' onclick='openWin(\"/performance/objectiveManage/objectiveCard.do?b_query2=query&opt=0&operator=1&planids="+plan_id+"&relatingTargetCard=" + relatingTargetCard + "&object_id="+"~"+SafeCode.encode(PubFunc.convertTo64Base(object_id))+"\")' ");
					str.append(" >");
					str.append(ResourceFactory.getProperty("lable.performance.perGoal"));
					str.append("</a>");		
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str.toString();
	}
	
	
	
	
	
	
	
	public ArrayList getParticularPointList(ArrayList pointList,String[] point)
	{
		ArrayList list=new ArrayList();
		ArrayList tempPointList=new ArrayList();
		String    point_kind=""; 
		for(Iterator t=pointList.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			if(temp[1].equals(point[0]))
			{
				tempPointList.add(temp);	
				point_kind=temp[3];
			}
		}
		list.add(tempPointList);
		list.add(point_kind);
		return list;
		
	}
	
	
	public String getNotMarkTd(String object_id,int lay,String planID,String mainbodyID,String status,String performanceType)
	{
	    String tempPlanid = "~"+SafeCode.encode(PubFunc.convertTo64Base(planID));
        String tempObj = "~"+SafeCode.encode(PubFunc.convertTo64Base(object_id));
        String tempMainbodyid = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainbodyID));
		StringBuffer   td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");  //2013.11.30 pjf 
		if("0".equals(performanceType))
			td.append(ResourceFactory.getProperty("lable.performnace.noMarkCause"));
		else if("1".equals(performanceType))
			td.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
		td.append("</td>");
		int x=1;
		if("True".equalsIgnoreCase(showDeductionCause))
			 x++;
		//能力素质考评,混合模式打分页面增加等级列时，跨列数应自增1
		if("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1)
			x++;
		if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
		{
			for(int j=0;j<lowerGradeMainbodyList.size();j++)
			{
				LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
				String a0100=(String)a_bean.get("a0100");
				String _status=(String)a_bean.get("status");
				if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
					continue;
				
				 
				
				td.append("<td  class='RecordRow' align='left' >"); 
	    		td.append("<table ><tr><td width='5' nowrap  valign='bottom' >");
	    		String check="";
	    		String display="block";
	    		if(_status!=null&&("4".equals(_status)|| "7".equals(_status)))
	    		{
	    			check="checked";
	    			display="block";
	    		} 
	    		td.append("<input type='checkbox' tile='sdfasdf'  ");  		
	    		td.append(" disabled ");
	    		td.append(" name='b"+object_id+"_"+planID+"_"+a0100+"'  "+check+"  ></td>");
	    		if("block".equals(display))
	    		{
	    			td.append("<td width='70'  valign='bottom' nowrap > ");
	    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+tempPlanid+"&objectID="+tempObj+"&mainbodyID="+"~"+SafeCode.encode(PubFunc.convertTo64Base(a0100))+"&type=0')\" >");
	    			if("0".equals(performanceType))
	    				td.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
	    			else  if("1".equals(performanceType))
	    				td.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
	    			td.append("</a></td>");
	    		}
	    		td.append("</tr></table>"); 
	    		td.append("&nbsp;</td>");
	    		if("True".equalsIgnoreCase(showDeductionCause))
				{
	    			 td.append("<td  class='RecordRow' align='left' >&nbsp;</td>");
				} 
				
			}
		}
		else if(this.isLookObjectScore&&operateModel==1)
    	{
    		td.append("<td  class='RecordRow' align='left'  >");
    		String object_status=(String)this.objectSelfCore.get("status");
    		
    		td.append("<table ><tr><td width='5' nowrap  valign='bottom' >");
    		String check="";
    		String display="block";
    		if(object_status!=null&&("4".equals(object_status)|| "7".equals(object_status)))
    		{
    			check="checked";
    			display="block";
    		}
    		
    		
    		td.append("<input type='checkbox' tile='sdfasdf'  ");  		
    		td.append(" disabled ");
    		td.append(" name='b"+object_id+"_"+planID+"_"+mainbodyID+"'  "+check+"  ></td>");
    		if("block".equals(display))
    		{
    			td.append("<td width='70'  valign='bottom' nowrap > ");
    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+tempPlanid+"&objectID="+tempObj+"&mainbodyID="+tempObj+"&type=0')\" >");
    			if("0".equals(performanceType))
    				td.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
    			else  if("1".equals(performanceType))
    				td.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
    			td.append("</a></td>");
    		}
    		td.append("</tr></table>");
    		
    		td.append("&nbsp;</td>");
    		if("True".equalsIgnoreCase(showDeductionCause))
			{
    			 td.append("<td  class='RecordRow' align='left' >&nbsp;</td>");
			}

    	}
		if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
		{
			if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(_scoreflag))
			{
				int n=0;
				if("True".equalsIgnoreCase(showDeductionCause))
					 n=1; 
				 
				td.append("<td  class='RecordRow'   colspan='"+(this.gradelist.size()+n)+"' align='left' > ");
			}
			else
				td.append("<td  class='RecordRow'   align='left'  colspan='"+x+"'>");
			td.append("<table   ><tr><td   nowrap  valign='bottom' >");
			String check="";
			String display="block";
			if("4".equals(status)|| "7".equals(status))
			{
				check="checked";
				display="block";
			}
			
			String a_status="4";
			if("7".equals(status))
				a_status="7";
			
			if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(_scoreflag))
				td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			td.append("<input type='checkbox' tile='sdfasdf' onclick='setStatus(this)' ");
			if("7".equals(status)|| "2".equals(status))
				td.append(" disabled ");
			
			if("1".equals(this.fillctrl))  //必打分项
			{
				td.append(" style='display:none' ");
			}
			
			td.append(" name='b"+object_id+"_"+planID+"_"+mainbodyID+"'  "+check+"  ></td>");
			
			td.append("<td width='70'  valign='bottom' nowrap > ");
			td.append(" <div style='display:"+display+"' id='b"+object_id+"' >");
			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status="+a_status+"&planID="+tempPlanid+"&objectID="+tempObj+"&mainbodyID="+tempMainbodyid+"&type=0')\" >");
			if("0".equals(performanceType))
				td.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
			else  if("1".equals(performanceType))
				td.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
			td.append("</a></div></td>");
			td.append("</tr></table>");
			td.append("</td>");
		}
		return td.toString();
	}
	
	
	//建议和意见
	public String getNotMarkTd2(String object_id,int lay,String planID,String mainbodyID,String status,String performanceType)
	{
		StringBuffer   td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");
	    td.append(ResourceFactory.getProperty("performance.batchgrade.otherInfo"));
		td.append("</td>");
		
		if(this.isLookObjectScore&&operateModel==1)
    	{
    		td.append("<td  class='RecordRow' align='center' >");
    		
    		{	
    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+planID+"&objectID="+object_id+"&mainbodyID="+object_id+"&type=1&edit=false')\" >");
    		    td.append(ResourceFactory.getProperty("kh.field.helpcontent"));
    		}
    		td.append("&nbsp;</td>");
    		if("True".equalsIgnoreCase(showDeductionCause))
			{
    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
			}
    	}
		
		if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
		{
			td.append("<td  class='RecordRow' align='center' >");
			String a_status="4";
		    td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status="+a_status+"&planID="+planID+"&objectID="+object_id+"&mainbodyID="+mainbodyID+"&type=1')\" >");
		    td.append(ResourceFactory.getProperty("kh.field.helpcontent"));		
			td.append("</td>");
		}
		return td.toString();
	}
	
	
	
	/**
	 * 生成 总体评价选项
	 * @param list   选项信息
	 * @return
	 */
	public String getExtendTd2(ArrayList list,String userID,String selectid,int lay,String name,String controlName,String planID,String mainbodyID)
	{
		String clientname = SystemConfig.getPropertyValue("clientName");
		StringBuffer   td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");
		td.append(name);
		td.append("</td>");
		String tempObject_id = userID;//加密之前的  郭峰
		userID = "~"+SafeCode.encode(PubFunc.convertTo64Base(userID));
		mainbodyID = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainbodyID));
		//判断是否有描述性评议项     陈旭光  2014-12-20
		PerEvaluationBo bo =new PerEvaluationBo(this.conn, planID, "");   
        boolean flag = bo.isProAppraise();
		
		if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
		{
			for(int j=0;j<lowerGradeMainbodyList.size();j++)
			{
				LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
				String a0100=(String)a_bean.get("a0100");
				String _status=(String)a_bean.get("status");
				if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
					continue;
				HashMap pointMap=(HashMap)this.allEvalResultMap.get(a0100);
				if(pointMap==null)
					pointMap=new HashMap();
				String whole_str="";
				if(pointMap.get("whole_str")!=null)
					whole_str=(String)pointMap.get("whole_str");
				td.append("<td  class='RecordRow' align='center' >");
				td.append(whole_str);
				td.append("&nbsp;</td>");
	    		if("True".equalsIgnoreCase(showDeductionCause))
				{
	    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
				}
				
			}
		}
		else if(this.isLookObjectScore&&operateModel==1)
    	{
    		td.append("<td  class='RecordRow' align='center' >");
    		if("True".equalsIgnoreCase(this.WholeEval))
    		{
	    		if(this.objectSelfCore.get("whole_grade_id")!=null)
	    		{
		    		for(int i=0;i<list.size();i++)
		    		{
		    			String[] temp=(String[])list.get(i);
		    			if(temp[0].equals((String)this.objectSelfCore.get("whole_grade_id")))
		    			{
		    				td.append(temp[1]);
		    				break;
		    			}
		    		}	
	    		}
    		}
    		if(((!"1".equals(this.performanceType) || ("1".equals(this.performanceType) && !flag))&& "True".equalsIgnoreCase(this.DescriptiveWholeEval))|| "1".equals(opt))
    		{
    			if("1".equals(opt))
    				td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+planID+"','"+userID+"','"+mainbodyID+"')\" > ");
    			else
    				td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow2('"+planID+"','"+userID+"','"+userID+"')\" > ");
    		}
    		
    		td.append("&nbsp;</td>");
    		if("True".equalsIgnoreCase(showDeductionCause))
			{
    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
			}

    	}
		if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
		{
			if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(_scoreflag))
			{
				int n=0;
				if("True".equalsIgnoreCase(showDeductionCause))
					 n=1; 
				td.append("<td   colspan='"+(this.gradelist.size()+n)+"'   vAlign='center' class='RecordRow' align='left' >");
			
			}
			else {
				// 有评分说明的时候，合并2个单元格.参照不打分原因 lium 
				int colspan = 1;
				if ("True".equalsIgnoreCase(showDeductionCause)){
					colspan++;
				}
				//smk 能力素质考评,混合评分模式，合并单元格+1
				if ("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1){
					colspan++;
				}
				td.append("<td colspan='").append(colspan).append("' vAlign='center' class='RecordRow' align='left' >");
			}
			if("True".equalsIgnoreCase(this.WholeEval))//总体评价
			{
				if(this.totalAppFormula!=null && this.totalAppFormula.trim().length()>0 && "wholeEval".equalsIgnoreCase(controlName))
				{
					td.append(" <input type='hidden' name='"+controlName+"' value='");
					String wholeVale = "";
					for(int i=0;i<list.size();i++)
		    		{
		    			String[] temp=(String[])list.get(i);
		    			if(temp[0].equals(selectid))
		    			{
		    				td.append(temp[0]);
		    				wholeVale = temp[1];
		    				break;
		    			}
		    		}
					td.append("'> ");
					td.append(" <span id='totalAppValue'> ");
					td.append(wholeVale);
					td.append("</span> ");
				}
				else
				{
					if("gs".equalsIgnoreCase(clientname)){//如果是国税   总体评价的处理
						td.append("<select name='"+planID+tempObject_id+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
					}else{
						td.append("<select name='"+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
					}
					
					if("2".equals(a_temp[2])|| "4".equals(a_temp[2])|| "7".equals(a_temp[2]))
						td.append(" disabled ");
				//	if(this.totalAppFormula!=null && this.totalAppFormula.trim().length()>0 && controlName.equalsIgnoreCase("wholeEval"))
				//		td.append(" disabled ");
					td.append(" >");
					td.append("<option value=''></option>");
					for(int i=0;i<list.size();i++)
					{
						String[] temp=(String[])list.get(i);
						td.append("<option value='");
						
						td.append(temp[0]);
						td.append("' ");
						if(temp[0].equals(selectid))
							td.append("selected");
						td.append(" >");
						td.append(temp[1]);
						td.append("</option>");
					}	
					td.append("</select>");
				}
			}
			
		    //td.append("&nbsp;<a  href=\"javascript:showWindow('"+planID+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(userID))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(mainbodyID))+"')\" >");
			//td.append("描述</a>");
			if((!"1".equals(this.performanceType) || ("1".equals(this.performanceType) && !flag)) && "True".equalsIgnoreCase(this.DescriptiveWholeEval))
				td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"   onclick=\"javascript:showWindow('"+planID+"','"+userID+"','"+mainbodyID+"')\" >&nbsp;&nbsp;&nbsp;&nbsp; ");
			td.append("</td>");
		}
		td.append("</tr>");
		return td.toString();
	}
	
	
	/**
	 * 生成 扩展的评分选项
	 * @param list   选项信息
	 * @return
	 */
	public String getExtendTd(ArrayList list,String userID,String selectid,int lay,String name,String controlName)
	{
		String clientname = SystemConfig.getPropertyValue("clientName");
		StringBuffer   td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");
		td.append(name);
		td.append("</td>");
		
		
		if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
		{
			for(int j=0;j<lowerGradeMainbodyList.size();j++)
			{
				LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
				String a0100=(String)a_bean.get("a0100");
				String _status=(String)a_bean.get("status");
				if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
					continue;
				HashMap pointMap=(HashMap)this.allEvalResultMap.get(a0100);
				if(pointMap==null)
					pointMap=new HashMap();
				String know_str="";
				if(pointMap.get("know_str")!=null)
					know_str=(String)pointMap.get("know_str");
				td.append("<td  class='RecordRow' align='center' >");
				td.append(know_str);
				td.append("&nbsp;</td>");
	    		if("True".equalsIgnoreCase(showDeductionCause))
				{
	    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
				}
				
			}
		}
		else if(this.isLookObjectScore&&operateModel==1)
    	{
    		td.append("<td  class='RecordRow' align='center' >");
    		if(this.objectSelfCore.get("know_id")!=null)
    		{
	    		for(int i=0;i<list.size();i++)
	    		{
	    			String[] temp=(String[])list.get(i);
	    			if(temp[0].equals((String)this.objectSelfCore.get("know_id")))
	    			{
	    				td.append(temp[1]);
	    				break;
	    			}
	    		}	
    		}
    		td.append("&nbsp;</td>");
    		
    		if("True".equalsIgnoreCase(showDeductionCause))
			{
    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
			}

    	}
		if(!this.isShowObjectSelfScore && !this.showObjectLowerScore)
		{
			
			if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(_scoreflag))
			{
				int n=0;
				if("True".equalsIgnoreCase(showDeductionCause))
					 n=1;
				td.append("<td class='RecordRow' colspan='"+(this.gradelist.size()+n)+"'  align='left' >");
			}
			else {
				// 有评分说明的时候，合并2个单元格.参照不打分原因 lium 
				int colspan = 1;
				if ("True".equalsIgnoreCase(showDeductionCause)){
					colspan++;
				}
				//smk 能力素质考评,混合评分模式，合并单元格+1
				if ("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1){
					colspan++;
				}
				td.append("<td colspan='").append(colspan).append("' class='RecordRow' align='left' >");
			}
			if("gs".equalsIgnoreCase(clientname)){
				RecordVo tempvo = this.getPlanVo();
				String tempplan_id = tempvo.getString("plan_id");
				td.append("<select name='"+tempplan_id+userID+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
			}else{
				td.append("<select name='"+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
			}
			
			if("2".equals(a_temp[2])|| "4".equals(a_temp[2])|| "7".equals(a_temp[2]))
				td.append(" disabled ");
			td.append(" >");
			td.append("<option value=''></option>");
			for(int i=0;i<list.size();i++)
			{
				String[] temp=(String[])list.get(i);
				td.append("<option value='");
				
				td.append(temp[0]);
				td.append("' ");
				if(temp[0].equals(selectid))
					td.append("selected");
				td.append(" >");
				td.append(temp[1]);
				
				td.append("</option>");
			}	
			td.append("</select>");
			td.append("</td>");
		}
		td.append("</tr>");
		return td.toString();
	}
	
	/**
	 * 
	 * @param objectID
	 * @param point_kind
	 * @param pointID
	 * @param scoreflag
	 * @param status            权重分值标识 0：分值  1：权重
	 * @param point
	 * @return
	 */
	public String getLowerMainbodyPonitTd(String objectID,String point_kind,String pointID,String scoreflag,String[] point,String status)
	{
		StringBuffer   td=new StringBuffer("");
	   DecimalFormat myformat1 = new DecimalFormat("##########.#####");
	   
	   for(int j=0;j<lowerGradeMainbodyList.size();j++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
			String a0100=(String)a_bean.get("a0100");
			String _status=(String)a_bean.get("status");
			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
				continue;
			HashMap pointMap=(HashMap)this.allEvalResultMap.get(a0100);
			if(pointMap==null)
				pointMap=new HashMap();
			LazyDynaBean valueBean=(LazyDynaBean)pointMap.get(pointID);
	    	td.append("<td class='RecordRow' align='center'  ");
	    	td.append(" nowrap >");
	    	td.append("<table border='0' ");
		    td.append(" ><tr><td  align='center' nowrap >");   
		    	
		    HashMap userNumberResultMap=(HashMap)this.getUserNumberPointResultMap().get(objectID);
		    if("1".equals(scoreflag)&& "0".equals(point_kind))
		    {
		    		if(valueBean==null|| "0".equals((String)valueBean.get("score")))
		    		{
		    			td.append("");
		    		}
		    		else
		    		{
		    				String gradedesc=(String)valueBean.get("desc2");
		    				if("2".equals(DegreeShowType))
		    					gradedesc=(String)valueBean.get("gradedesc");
		    				td.append(gradedesc);
					    	
		    		}
		    }
		    else
		    {
		 /*
		    		if(valueBean==null||((String)valueBean.get("score")).equals("0"))
		    		{
		    			td.append("");
		    		}
		    		else */
		    		{
		    			if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
		    		    	 td.append("  ");
		    			else
		    			{
		    				
			    		
				    		if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "true".equalsIgnoreCase(this.showOneMark))
				    		{
				    			if(userNumberResultMap!=null&&userNumberResultMap.get(pointID)!=null)
				    				td.append((String)userNumberResultMap.get(pointID));
				    		}
				    		else
				    		{
				    			if(valueBean==null|| "0".equals((String)valueBean.get("score")))
					    		{
					    			td.append("");
					    		}
				    			else
				    			{
						    				if("0".equals(point_kind))
						    					td.append((String)valueBean.get("score"));
						    				else
						    					td.append((String)valueBean.get("amount"));			    				
				    			}
					    			
				    		}
		    			}
		    		}
		    
		    }
		    	td.append("</td></tr></table>");
	    	
	    	
	    	td.append("</td>");
	    	
	    	if("true".equalsIgnoreCase(this.showDeductionCause))
	    	{
	    		String reasons="";
	    		
	    		if(valueBean!=null)
	    			reasons=(String)valueBean.get("reasons");
				 reasons=reasons.replaceAll("<br>","\r\n");
				 reasons=reasons.replaceAll(" ","&nbsp;");
				 String  valign="top";
				 if(reasons.trim().length()==0)
					 	valign="middle";
				 td.append("<td class='RecordRow'  valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td >"+reasons+"</td></tr></table></td>");			 
			}
	    	
	    	
    	
    	
    	
		}
    	
    	
    	
    	
    	return td.toString();
	}
	
	
	
	public String getSelfScorePonitTd(String objectID,ArrayList tempList,String point_kind,String status,HashMap pointMap,String pointID,String scoreflag,String[] point)
	{
		StringBuffer   td=new StringBuffer("");
	DecimalFormat myformat1 = new DecimalFormat("##########.#####");
    	td.append("<td class='RecordRow' align='center'  ");
    	boolean isValue=true;
    	if("0".equals((String)pointMap.get(pointID)))
    		isValue=false;
    	//	td.append(" style='display:none' ");
    	if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
	    	isValue=false;
    	//	td.append(" style='display:none' ");
   	
    	td.append(" nowrap >");
    	
    	
	    	td.append("<table border='0' ");
	 //   	if(!isValue)
	 //   		td.append(" style='display:none' ");
	    	td.append(" ><tr><td  align='center' nowrap >");   
	    	
	    	String[] pointGrade=(String[])tempList.get(0);
	    	String a_fieldItem=pointGrade[10];
	    	String a_fieldItem_1=pointGrade[11];
	    	
	
	    	HashMap userNumberResultMap=(HashMap)this.getUserNumberPointResultMap().get(objectID);
	    	
	    	if("1".equals(scoreflag)&& "0".equals(point_kind))
	    	{
	    		if("0".equals((String)pointMap.get(pointID)))
	    		{
	    			td.append("");
	    		}
	    		else
	    		{
		    		for(Iterator t=tempList.iterator();t.hasNext();)
		    		{
		    			String[] a_temp=(String[])t.next();
		    				if(this.objectSelfCore.get(a_temp[1])!=null)
		    				{
			    				String[] values=(String[])this.objectSelfCore.get(a_temp[1]);
			    				if(values!=null)
			    				{
				    				if(values[6]!=null&&values[6].equals(a_temp[5]))
				    				{
				    					if("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))
						    				td.append(a_temp[13]);
				    					else if("2".equals(this.DegreeShowType))
						    				td.append(a_temp[4]);
				    				//	td.append(a_temp[13]);
				    				}
			    				}
		    				}
		    		}
	    		}
	    	}
	    	else
	    	{
	 
	    		String[]  a_temp=(String[])tempList.get(0);
	    	
	    		if("0".equals((String)pointMap.get(pointID)))
	    		{
	    			td.append("");   		
	    		}
	    		else
	    		{
	    			if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
	    		    	 td.append("  ");
	    			else
	    			{
	    				
		    		
			    		if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "true".equalsIgnoreCase(this.showOneMark))
			    		{
			    			if(userNumberResultMap!=null&&userNumberResultMap.get(pointID)!=null)
			    				td.append((String)userNumberResultMap.get(pointID));
			    		}
			    		else
			    		{
			    			if(this.objectSelfCore.get(a_temp[1])!=null)
			    			{
				    			String[] values=(String[])this.objectSelfCore.get(a_temp[1]);   			
				    			if(values!=null&&(values[4]!=null||values[3]!=null))
				    			{			
					    			
					    			if("1".equals(status))
					    			{
					    				if("0".equals(point_kind))
					    					td.append(values[3]!=null?myformat1.format(new Double(values[3])):"");
					    				else
					    					td.append(values[4]!=null?myformat1.format(new Double(values[4])):"");			    				
					    			}
					    			else
					    			{
					    				if(values[4]!=null&& "1".equals(point_kind))
					    					td.append(myformat1.format(new Double(values[4])));
					    				else if(values[3]!=null&& "0".equals(point_kind))
					    				{
					    					 
					    					if(values[3].trim().length()==0)
					    						td.append("");
					    					else
					    						td.append(myformat1.format(new Double(values[3])));
					    				
					    				}
					    			}
				    			}
			    			}
						
			    		}
	    			}
		    		
	    		}
	    
	    	}
	    	td.append("</td></tr></table>");
    	
    	
    	td.append("</td>");
    	
    	if("true".equalsIgnoreCase(this.showDeductionCause)&&!this.isShowObjectSelfScore&&!this.showObjectLowerScore)
    	{
    		String reasons="";
    		String[] values=(String[])this.objectSelfCore.get(pointID);
    		 if(values!=null&&values.length>=8)
				 reasons=values[7];
			 reasons=reasons.replaceAll("<br>","\r\n");
			 reasons=reasons.replaceAll(" ","&nbsp;");
			 
			 String  valign="top";
			 if(reasons.trim().length()==0)
				 	valign="middle";
			 td.append("<td class='RecordRow'  valign='"+valign+"' align='left'    width='"+(td_width+50)+"'   ><table width='100%'  ><tr><td >"+reasons+"</td></tr></table></td>");			 
		}
    	return td.toString();
	}
	
	
	/**
	 * 按条件生成 <td>中的内容 
	 * @param tempList			指标标度值
	 * @param point_kind		要素类型 0:定性要点；1:定量要点
	 * @param temp				考核对象信息  id\姓名\状态
	 * @param objectResultMap	对象的考核结果
	 * @param status            权重分值标识 0：分值  1：权重
	 * @param pointMap          具有某人的指标权限map
	 * @param scoreflag			1:标度 2:混合
	 * @return
	 */
    public	String getPointTD(ArrayList tempList,String point_kind,String[] temp,HashMap objectResultMap,String status,HashMap pointMap,String pointID,String scoreflag,String[] pointScore,String[] point) throws GeneralException
    {
    	String clientname = SystemConfig.getPropertyValue("clientName");//判断是否国税
    	StringBuffer   td=new StringBuffer("");
    	
    	if("True".equalsIgnoreCase(allowSeeLowerGrade)&&operateModel==1&&this.lowerGradeMainbodyList.size()>0)
    	{
    		td.append(getLowerMainbodyPonitTd(temp[0],point_kind,pointID,scoreflag,point,status));
    	}
    	else if((this.isLookObjectScore&&operateModel==1)|| "1".equalsIgnoreCase(opt))
    	{
    		if("1".equalsIgnoreCase(opt))
    		{
    			if(objectResultMap==null)
    				this.objectSelfCore=new HashMap();
    			else
    				this.objectSelfCore=objectResultMap;
    		}
    		td.append(getSelfScorePonitTd(temp[0],tempList,point_kind,status,pointMap,pointID,scoreflag,point));
    		
    	}
    	
    	
    	 
    	
    	
    	
    	

    	if(!this.isShowObjectSelfScore&&!this.showObjectLowerScore)
    	{
    		String a_fieldItem="";
	    	String a_fieldItem_1="";
    		if(tempList.size()>0)
	    	{
		    	String[] pointGrade=(String[])tempList.get(0);
		    	a_fieldItem=pointGrade[10];
		    	a_fieldItem_1=pointGrade[11];
		    	//if(a_fieldItem!=null&&a_fieldItem.trim().length()>0&&point_kind.equals("0"))
		    	if((a_fieldItem!=null&&a_fieldItem.trim().length()>0&& "0".equals(point_kind))||(a_fieldItem_1!=null&&a_fieldItem_1.trim().length()>0&& "0".equals(point_kind)))
		    	{
		    		tempList=batchGradeBo.getFiltrateDate(tempList,a_fieldItem,temp[0],point_kind,a_fieldItem_1);
		    	}
	    	}
    		
    		HashMap userNumberResultMap=(HashMap)this.getUserNumberPointResultMap().get(temp[0]);
    		if(("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))&& "1".equals(this.PointEvalType)&& "1".equals(scoreflag))
    		{
    			
    			if("1".equals(scoreflag)&& "0".equals(point_kind))
    			{
    				if("0".equals((String)pointMap.get(pointID)))
		    		{
    					td.append("<td colspan='"+gradelist.size()+"' class='RecordRow' align='left' >");
		    			td.append("<input type='hidden' name='a");
		    			td.append(temp[0]);
		    			td.append("' value='null'  />"); 
		    			td.append("</td>");
		    		}
		    		else
		    		{
    				
	    				LazyDynaBean _bean=null;
	    				for(int n=0;n<this.gradelist.size();n++)
	    				{
	    					_bean=(LazyDynaBean)this.gradelist.get(n);
	    					td.append("<td class='RecordRow' align='center' >");
	    					if(n<tempList.size())
	    					{
	    						if("gs".equalsIgnoreCase(clientname)){
	    							td.append("<input type='radio' name='"+temp[1]+temp[0]+pointID+"' value='"+(String)_bean.get("grade_template_id")+"'  ");
	    						}else{
	    							td.append("<input type='radio' name='"+pointID+"' value='"+(String)_bean.get("grade_template_id")+"'  ");
	    						}
	    						
	    						if("2".equals(temp[2])|| "4".equals(temp[2])|| "7".equals(temp[2]))
	    			    			td.append(" disabled='true' ");
	    						String[] a_temp=(String[])tempList.get(n);
	    						// 提交后的分数是否显示
				    			if(!("false".equals(this.isShowSubmittedScores)&& "2".equals(temp[2])))
				    			{
					    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
					    			{
					    				String[] values=(String[])objectResultMap.get(a_temp[1]);
					    				if(values!=null)
					    				{
						    				if(values[6]!=null&&values[6].equals(_bean.get("grade_template_id"))) 
						    					td.append("checked");   
					    				}
					    			}  	
					    			else
					    			{
					    				if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
					    				{
						    				 if("1".equals(this.BlankScoreOption))
											 {
							    				 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
							    				 String defaultValue=(String)abean.get("gradecode");
							    				 if(defaultValue!=null)
								    			 {
									    				if(defaultValue.equals(a_temp[5])) 
									    					td.append("checked"); 	  
								    			 } 
											 }
							    			 else  if("2".equals(this.BlankScoreOption))
							    			 {
												 if(this.BlankScoreUseDegree!=null)
								    			 {
									    				if(this.BlankScoreUseDegree.equals(a_temp[5])) 
									    					td.append("checked"); 		  
								    			 }
							    			 }
					    				}
					    			} 
				    			}
	    						td.append(" />");
	    					}
	    					else
	    						td.append("&nbsp;");
	    					td.append("</td>");
	    				}
		    		}
    			}
    			else if("1".equals(point_kind))
    			{
    				  
    				if(tempList.size()>0)
		    		{
			    		String[]  a_temp=(String[])tempList.get(0);
			    		td.append("<td colspan='"+gradelist.size()+"' class='RecordRow' align='left' >");
			    		td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <input  style='width: 65.0px'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'     "); 
			    		if("0".equals((String)pointMap.get(pointID)))
			    		{
			    			td.append(" type='hidden'");
			    			td.append(" value='null' ");
			    		}
			    		else
			    		{
			    			if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
			    		    	 td.append(" type='hidden' ");
			    			else
			    				td.append(" type='text' ");
				    		if("2".equals(temp[2])|| "4".equals(temp[2])|| "7".equals(temp[2])||("1".equals(point_kind)&& "true".equalsIgnoreCase(this.showOneMark)&&point[7]!=null&& "1".equals(point[7])|| "0".equals(temp[3])))//zhaoxg add ||temp[3].equals("0")为没权限的指标（可看不可打分） 慧聪需求
				    			td.append(" disabled='false'");
				    		if(!("false".equals(this.isShowSubmittedScores)&& "2".equals(temp[2])))
			    			{
					    		if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "true".equalsIgnoreCase(this.showOneMark))
					    		{
					    			if(userNumberResultMap!=null)
					    				td.append(" value='"+(String)userNumberResultMap.get(pointID)+"' ");
					    		}
					    		else
					    		{
						    		if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
									{
						    			String[] values=(String[])objectResultMap.get(a_temp[1]);   			
						    			if(values!=null&&(values[4]!=null||values[3]!=null))
						    			{			
							    			td.append(" value='");
							    			if("1".equals(status))
							    			{
							    				if("0".equals(point_kind))
							    					td.append(values[3]!=null?values[3]:"");
							    				else
							    					td.append(values[4]!=null?values[4]:"");			    				
							    			}
							    			else
							    			{
							    				if(values[4]!=null&& "1".equals(point_kind))
							    					td.append(values[4]);
							    				else if(values[3]!=null&& "0".equals(point_kind))
							    					td.append(values[3]);
							    			}
							    			
							    			td.append("'");
						    			}
									} 
						    		else
						    		{
						    			if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
						    			{
						    			 if("1".equals(this.BlankScoreOption))
										 {
						    				
						    				 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
						    				 if("0".equals(point_kind))
						    				 {
						    					 td.append(" value='"+PubFunc.multiple((String)abean.get("score"),(String)abean.get("gradevalue"),1)+"' ");
						    				 }
						    				 else
						    				 {
						    					 td.append(" value='"+(String)abean.get("top_value")+"' ");
						    				 }
										 }
						    			 else  if("2".equals(this.BlankScoreOption))
						    			 {
						    				 boolean isValue=false;
						    				 String[] t=null;
											 for (Iterator tt = tempList.iterator(); tt.hasNext();) {
													String[] temp1 = (String[]) tt.next();  
											
													if(temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree))
													{
														isValue=true;
														t=temp1;
													}
											 }
											 if(isValue)
											 {
												 if("0".equals(point_kind))
							    				 {
													String d_value= PubFunc.round(String.valueOf(Float
																.parseFloat(t[9])
																* Float.parseFloat(t[8])), 1);
													td.append(" value='"+d_value+"' ");
							    				 }
							    				 else
							    				 {
							    					 td.append(" value='"+t[6]+"' ");
							    				 }
											 }
											 
						    			 }
						    			}
						    		}
					    		}
			    			}
			    		}
			    		td.append(" name='a"+temp[0]+"'    id='"+point[0]+"'      />");		 
			    		td.append(" </td> ");
		    		}
    				
    				
    			}
    			
    		}
    		else
    		{ 
		    	td.append("<td class='RecordRow' align='left'  ");
//		    	if(showIndicatorRole.equalsIgnoreCase("False"))
//		    		td.append(" width='250' ");
//		    	else
//		    		td.append(" width='200' ");
		    	td.append(" nowrap >");
		    	if("False".equalsIgnoreCase(showIndicatorRole)){
		    		td.append("<table width='160' border='0'><tr><td  ");
		    		if(("1".equals(PointEvalType)) && ("1".equals(scoreflag)) && "1".equals(RadioDirection)){
		    			
		    		}else{
		    			td.append(" width='80'");
		    		}
		    		td.append(" align='center' nowrap >");
		    	}
		    		   
		    	
		    	
		    	
		    	
		    	
		    	if("1".equals(scoreflag)&& "0".equals(point_kind)&& "0".equals(PointEvalType))//下拉
		    	{
		    		if("0".equals((String)pointMap.get(pointID)))
		    		{
		    			td.append("<input type='hidden' name='a");
		    			td.append(temp[0]);
		    			td.append("' value='null'  />");
		    			
		    		}
		    		else
		    		{//zhaoxg标记
			    		td.append("<select name='a");
			    		if("gs".equalsIgnoreCase(clientname)){//如果是国税      控制下拉框打分  郭峰
			    			td.append(temp[1]+temp[0]);
			    		}else{
			    			td.append(temp[0]);
			    		}
			    		td.append("'");   		
			    		if("2".equals(temp[2])|| "4".equals(temp[2])|| "7".equals(temp[2])|| "0".equals(temp[3]))
			    			td.append(" disabled='false'");
			    		td.append("   onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'        ><option value=''></option>");
			    		boolean isValue=false;
			    		for(Iterator t=tempList.iterator();t.hasNext();)
			    		{
			    			String[] a_temp=(String[])t.next();
			    			td.append("<option value='");
			    			td.append(a_temp[5]);
			    			td.append("' "); 
			    			
			    			// 提交后的分数是否显示
			    			if(!("false".equals(this.isShowSubmittedScores)&& "2".equals(temp[2])))
			    			{
				    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
				    			{
				    				String[] values=(String[])objectResultMap.get(a_temp[1]);
				    				if(values!=null)
				    				{
					    				if(values[6]!=null&&values[6].equals(a_temp[5]))
					    				{
					    					td.append("selected");
					    					isValue=true;
					    				}
				    				}
				    			}  	
				    			else
				    			{
				    				if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
				    				{
					    				 if("1".equals(this.BlankScoreOption))
										 {
						    				 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
						    				 String defaultValue=(String)abean.get("gradecode");
						    				 if(defaultValue!=null)
							    			 {
								    				if(defaultValue.equals(a_temp[5]))
								    				{
								    					td.append("selected");
								    					isValue=true;
								    				}
							    			 }
						    				 
										 }
						    			 else  if("2".equals(this.BlankScoreOption))
						    			 {
											 if(this.BlankScoreUseDegree!=null)
							    			 {
								    				if(this.BlankScoreUseDegree.equals(a_temp[5]))
								    				{
								    					td.append("selected");
								    					isValue=true;
								    				}
							    			 }
						    			 }
				    				}
				    			}
				    			
				    			if(!isValue)
				    			{
				    				if(a_fieldItem!=null&&a_fieldItem.trim().length()>0&&a_fieldItem_1!=null&&a_fieldItem_1.trim().length()>0&& "0".equals(point_kind)&&a_fieldItem.equals(a_fieldItem_1))
				    				{
				    					if(tempList.size()==1)
				    						td.append("selected");
				    				}
				    			}
			    			}
			    			td.append(" >&nbsp;&nbsp;");
			    			if("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))
			    				td.append(a_temp[13]);
			    			else if("2".equals(this.DegreeShowType))
			    				td.append(a_temp[4]);
			    			td.append("&nbsp;&nbsp;</option>");   			
			    		}
			    		td.append("</select>");  	
		    		}
		    	}
		    	else if(("1".equals(PointEvalType)) && ("1".equals(scoreflag))  && "1".equals(RadioDirection)){
		    		if("0".equals((String)pointMap.get(pointID)))
		    		{
		    			td.append("<input type='hidden' name='a");
		    			td.append(temp[0]);
		    			td.append("' value='null'  />");
		    			
		    		}
		    		else
		    		{
			    		boolean isValue=false;
			    		for(Iterator t=tempList.iterator();t.hasNext();)
			    		{
			    			String[] a_temp=(String[])t.next();
			    			if("gs".equalsIgnoreCase(clientname)){//如果是国税
			    				td.append("<input type='radio' name='"+temp[1]+temp[0]+a_temp[1]+"' value='");
			    			}else{
			    				td.append("<input type='radio' name='"+a_temp[1]+"' value='");
			    			}
			    			td.append(a_temp[5]);
			    			td.append("' "); 
			    			if("2".equals(temp[2])|| "4".equals(temp[2])|| "7".equals(temp[2]))
    			    			td.append(" disabled='true' ");
			    			// 提交后的分数是否显示
			    			if(!("false".equals(this.isShowSubmittedScores)&& "2".equals(temp[2])))
			    			{
				    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
				    			{
				    				String[] values=(String[])objectResultMap.get(a_temp[1]);
				    				if(values!=null)
				    				{
					    				if(values[6]!=null&&values[6].equals(a_temp[5]))
					    				{
					    					td.append("checked");
					    					isValue=true;
					    				}
				    				}
				    			}  	
				    			else
				    			{
				    				if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
				    				{
					    				 if("1".equals(this.BlankScoreOption))
										 {
						    				 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
						    				 String defaultValue=(String)abean.get("gradecode");
						    				 if(defaultValue!=null)
							    			 {
								    				if(defaultValue.equals(a_temp[5]))
								    				{
								    					td.append("checked");
								    					isValue=true;
								    				}
							    			 }
						    				 
										 }
						    			 else  if("2".equals(this.BlankScoreOption))
						    			 {
											 if(this.BlankScoreUseDegree!=null)
							    			 {
								    				if(this.BlankScoreUseDegree.equals(a_temp[5]))
								    				{
								    					td.append("checked");
								    					isValue=true;
								    				}
							    			 }
						    			 }
				    				}
				    			}
				    			
				    			if(!isValue)
				    			{
				    				if(a_fieldItem!=null&&a_fieldItem.trim().length()>0&&a_fieldItem_1!=null&&a_fieldItem_1.trim().length()>0&& "0".equals(point_kind)&&a_fieldItem.equals(a_fieldItem_1))
				    				{
				    					if(tempList.size()==1)
				    						td.append("checked");
				    				}
				    			}
			    			}
			    			td.append(" />&nbsp;&nbsp;");
			    			if("1".equals(this.DegreeShowType)|| "3".equals(this.DegreeShowType))
			    				td.append(a_temp[13]);
			    			else if("2".equals(this.DegreeShowType))
			    				td.append(a_temp[4]);
			    		}
		    		}
		    	}
		    	else
		    	{
		    		if(tempList.size()>0)
		    		{
			    		String[]  a_temp=(String[])tempList.get(0);
			    		td.append(" <input  style='width: 65.0px'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'     "); //zhaoxg标记
			    		//smk 能力素质考评,混合模式打分页面增加等级列,输入分数自动计算等级,2015.11.25
						 if("2".equalsIgnoreCase(_scoreflag) && this.planVo.getInt("busitype")==1){
					    		td.append(" onkeyup='autograde(this);' onchange='autograde(this);'  ");
						 }
			    	    if(((String)pointMap.get(pointID))!=null && "0".equals((String)pointMap.get(pointID)))
			    		{
			    			td.append(" type='hidden'");
			    			td.append(" value='null' ");
			    		}
			    		else
			    		{
			    			if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "false".equalsIgnoreCase(this.showOneMark))
			    		    	 td.append(" type='hidden' ");
			    			else
			    				td.append(" type='text' ");
				    		if("2".equals(temp[2])|| "4".equals(temp[2])|| "7".equals(temp[2])||("1".equals(point_kind)&& "true".equalsIgnoreCase(this.showOneMark)&&point[7]!=null&& "1".equals(point[7])||"0".equals(temp[3])))//zhaoxg add ||temp[3].equals("0") 慧聪需求
				    			td.append(" disabled='false'");
				    		if(!("false".equals(this.isShowSubmittedScores)&& "2".equals(temp[2])))
			    			{
					    		if("1".equals(point_kind)&&point[7]!=null&& "1".equals(point[7])&& "true".equalsIgnoreCase(this.showOneMark))
					    		{
					    			if(userNumberResultMap!=null)
					    				td.append(" value='"+(String)userNumberResultMap.get(pointID)+"' ");
					    		}
					    		else
					    		{
						    		if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
									{
						    			String[] values=(String[])objectResultMap.get(a_temp[1]);   			
						    			if(values!=null&&(values[4]!=null||values[3]!=null))
						    			{			
							    			td.append(" value='");
							    			if("1".equals(status))
							    			{
							    				if("0".equals(point_kind))
							    					td.append(values[3]!=null?values[3]:"");
							    				else
							    					td.append(values[4]!=null?values[4]:"");			    				
							    			}
							    			else
							    			{
							    				if(values[4]!=null&& "1".equals(point_kind))
							    					td.append(values[4]);
							    				else if(values[3]!=null&& "0".equals(point_kind))
							    					td.append(values[3]);
							    			}
							    			
							    			td.append("'");
						    			}
									} 
						    		else
						    		{
						    			if(!"4".equals(temp[2])&&!"7".equals(temp[2]))
						    			{
						    			 if("1".equals(this.BlankScoreOption))
										 {
						    				
						    				 LazyDynaBean abean=(LazyDynaBean)this.pointMaxValueMap.get(a_temp[1]);
						    				 if("0".equals(point_kind))
						    				 {
						    					 td.append(" value='"+PubFunc.multiple((String)abean.get("score"),(String)abean.get("gradevalue"),1)+"' ");
						    				 }
						    				 else
						    				 {
						    					 td.append(" value='"+(String)abean.get("top_value")+"' ");
						    				 }
										 }
						    			 else  if("2".equals(this.BlankScoreOption))//指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
						    			 {	 
						    				 DecimalFormat myformat1 = new DecimalFormat("##########.#####");//
						    				 boolean isValue=false;
						    				 String[] t=null;
											 for (Iterator tt = tempList.iterator(); tt.hasNext();) {
													String[] temp1 = (String[]) tt.next();  
											
													if(temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree))
													{
														isValue=true;
														t=temp1;
													}
											 }
											 if(isValue)
											 {
												 if("0".equals(point_kind))
							    				 {
													 // 2013.12.03 pjf
													if(t[8]==null || "".equals(t[8])){
														t[8] = "0";
													}
													if(t[9]==null || "".equals(t[9])){
														t[9] = "0";
													}
													String d_value= PubFunc.round(String.valueOf(Float
																.parseFloat(t[9])
																* Float.parseFloat(t[8])), 1);
													d_value=myformat1.format(Float.parseFloat(d_value));//zzk 2014/2/11 未打分时 指标分值处理小数位和正常打分处理格式输出一致  
													td.append(" value='"+d_value+"' ");
							    				 }
							    				 else
							    				 {
							    					 td.append(" value='"+t[6]+"' ");
							    				 }
											 }
											 
						    			 }
						    			}
						    		}
					    		}
			    			}
			    		}
			    	    if("gs".equalsIgnoreCase(clientname)){//如果是国税  键盘录分的控制
			    	    	td.append(" name='a"+temp[1]+temp[0]+"'    id='"+point[0]+"'      />");	
			    	    }else{
			    	    	td.append(" name='a"+temp[0]+"'    id='"+point[0]+"'      />");	
			    	    }
			    			
		    		
		    		}
		    
		    	}
		    	
		    	if("False".equalsIgnoreCase(showIndicatorRole))
		    	{
		    		DecimalFormat myformat1 = new DecimalFormat("########.####");//
			    	td.append("</td>");
			    	td.append("<td width='80' >");
			    	if("1".equals(point[2]))   //定量指标
					{
							if(point[7]==null||!"1".equals(point[7]))
							{
								if(pointScore[3]!=null&&pointScore[2]!=null)
									td.append(" ( "+PubFunc.round(pointScore[3],1)+"~"+PubFunc.round(pointScore[2],1)+" ) ");
							}
					}
					else					  //定性指标
					{
							if("2".equals(scoreflag))
								td.append(" ( "+ResourceFactory.getProperty("lable.performance.singleGrade.value")+":"+(pointScore[1]!=null&&pointScore[1].length()>0?myformat1.format(Double.parseDouble(pointScore[1])):"")+" ) ");			
					}
					td.append("</td>"); 
					td.append("</tr></table>"); 
		    	}
		    	td.append("</td>");
	    	
    		} 
			
    	}
    	return td.toString();
    }
	
	
	
	
	
	
	
	
	
	
	//初始化 表项显示纪录 map
	public HashMap getItemsSignMap(ArrayList items)
	{
		HashMap itemSignMap=new HashMap();
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			itemSignMap.put(temp[0],"0");
		}
		return itemSignMap;
	}
	
	
	

	/**
	 * 得到指标对应的表项列表((按层次))
	 * @param pointList
	 * @param items
	 * @return
	 */
	public HashMap getPointItemList(ArrayList pointList,ArrayList items)
	{
		HashMap pointItemMap=new HashMap();	
		for(Iterator t=pointList.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();			
			
			String   item_str=temp[3];
			ArrayList pointItemList=new ArrayList();
			getPointItemList(item_str,pointItemList,items);
			pointItemMap.put(temp[0],pointItemList);
		}
		return pointItemMap;
	}
	
	
	
	public void getPointItemList(String item_str,ArrayList pointItemList,ArrayList items)
	{
		
		for(Iterator t1=items.iterator();t1.hasNext();)
		{
			String[] item=(String[])t1.next();
			if(item[0].equals(item_str))
			{
				pointItemList.add(item);
				if(item[1]!=null)
					getPointItemList(item[1],pointItemList,items);
			}
		}

	}
	
	/** 能力素质  按岗位素质模型测评  向普通打分模板整合   完全仿写   郭峰
	 * 指标分类列表(按顺序) && 各项目的子项目(hashmap) && 表头的层数 && HashMap各项目包含的指标个数
	 * @param templateID
	 * @return
	 */
	public ArrayList getCompetencyAllDataList(String e01a1) throws GeneralException{
		ArrayList  list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			//要返回的四个变量
			int        lays=2;                  //表头的层数  固定的，就是2层
			HashMap    map=new HashMap();       //各项目包含的指标个数
			HashMap    subItemMap=new HashMap();//各项目的子项目   肯定为空
			ArrayList items = new ArrayList();  //指标分类列表
			
			/* 按顺序得到模版项目列表 */
			items=batchGradeBo.getCompentencyPointSet(e01a1);
			
			/* 得到各最底层项目的指标个数集合 */
			StringBuffer sb= new StringBuffer("");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Calendar calendar = Calendar.getInstance();				
			String historyDate = sdf.format(calendar.getTime());
			sb.append("select (case when(point_type is null or point_type='') then '-9999' else point_type end) as point_type,count(point_id) count from per_competency_modal where object_type='3' and object_id ='"+e01a1+"'");
			sb.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date group by point_type");
			rowSet=dao.search(sb.toString());
			while(rowSet.next()){
				String temppoint_type = rowSet.getString("point_type");
				if(map.get(temppoint_type)!=null){
					int count = Integer.parseInt((String)map.get(temppoint_type));
					count = count+Integer.parseInt(rowSet.getString("count"));
					map.put(temppoint_type,String.valueOf(count));
				}else{
					map.put(temppoint_type,rowSet.getString("count"));
				}
			}
			if(rowSet!=null)
				rowSet.close();
			list.add(items);
			list.add(new Integer(lays));
			list.add(map);
			list.add(subItemMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 模版项目列表(按顺序) && 各项目的子项目(hashmap) && 表头的层数 && HashMap各项目包含的指标个数
	 * @param templateID
	 * @return
	 */
	public ArrayList getPerformanceStencilList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		int        lays=0;                  //表头的层数
		HashMap    map=new HashMap();       //各项目包含的指标个数
		HashMap    subItemMap=new HashMap();//各项目的子项目
		try
		{

			String sql="";
			/* 按顺序得到模版项目列表 */
			ArrayList items=batchGradeBo.getItems(templateID);			
			/* 取得表头的层数 */
			lays=getLays(items);
			lays++;	            //包含指标层
			
			/* 得到各最底层项目的指标个数集合 */
			sql="select pp.item_id,count(pp.item_id) count  from  per_template_item pi,per_template_point pp where pi.item_id=pp.item_id and pi.template_id='"+templateID+"' ";
			if(noPrivPointStr.length()>0) //	去掉无权限指标 2010-08-25
			{
				sql+=" and lower(pp.point_id) not in ("+this.noPrivPointStr.toLowerCase()+")";
			}
			if("2".equals(this.fromType))
			{
				if(this.privPointStr.length()>0)
				{
					sql+=" and UPPER(pp.point_id) in ('"+this.privPointStr.replaceAll(",","','").toUpperCase()+"')";
				}
			}
			sql+=" group by pp.item_id ";
			rowSet=dao.search(sql);
			HashMap itemsCountMap=new HashMap();
			while(rowSet.next())
			{
				itemsCountMap.put(rowSet.getString("item_id"),rowSet.getString("count"));	
			}
			
			/* 求得map值 */
			for(Iterator t=items.iterator();t.hasNext();)
			{
				int count=0;
				String[] temp=(String[])t.next();
				this.leafNodes="";
				getleafCounts(temp,items,itemsCountMap);					
				this.leafNodes+="/";
			
				String[] a=this.leafNodes.substring(1).split("/");

				
			
				for(int i=0;i<a.length;i++)
				{
					if(itemsCountMap.get(a[i])!=null)
						count+=Integer.parseInt((String)itemsCountMap.get(a[i]));	
			//		else
			//			count++;
				}	
		//		if(!a[0].equals(temp[0])&&itemsCountMap.get(temp[0])!=null)
				{
		//			count+=Integer.parseInt((String)itemsCountMap.get(temp[0]));	
				}	
				map.put(temp[0],String.valueOf(count));
				
			}
			
			//各项目的子项目(hashmap)
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				StringBuffer subItem_str=new StringBuffer("");
				for(Iterator t1=items.iterator();t1.hasNext();)
				{
					
					String[] te=(String[])t1.next();
					if(te[1]!=null&&te[1].equals(temp[0]))
					{
						subItem_str.append(te[0]);
						subItem_str.append("/");
					}
				}
				if(subItem_str.length()>1)
				{
	
					subItemMap.put(temp[0],subItem_str.toString());
				}
				else
					subItemMap.put(temp[0],"");
			}

			list.add(items);
			list.add(new Integer(lays));
			list.add(map);
			list.add(subItemMap);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	/*	finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}*/
		return list;
	}
	
	
	
	
	/**
	 * 取得表头的层数
	 */
	
	public int  getLays(ArrayList items)
	{
		int a_lays=0;
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] item=(String[])t.next();
			if(item[1]==null)
			{	
				int lay=CountLevel(item,items) ;
				if( a_lays<lay)
				{
					a_lays=lay;
				}
			}			
		}
		return a_lays+1;
	}
	
	int CountLevel(String[] MyNode,ArrayList list) 
	 { 
	  if (MyNode == null) return -1; 
	  int iLevel = 1; int iMaxLevel = 0; 
	  ArrayList subNodeList=new ArrayList();
	  for (int i=0; i<list.size(); i++) 
	  { 
	   String[] temp=(String[])list.get(i);
	   if(temp[1]!=null&&temp[1].equals(MyNode[0]))
	    subNodeList.add(temp);   
	  }
	  
	  for (int i=0; i<subNodeList.size(); i++) 
	  { 
	   iLevel = CountLevel((String[])subNodeList.get(i),list)+1; 
	   if (iMaxLevel < iLevel) 
	   iMaxLevel = iLevel; 
	  } 
	  return iMaxLevel; 
	 } 

	
	/**
	 * 求得某节点的所有叶子节点的id串
	 * @param node
	 * @param items
	 */
	String leafNodes="";	
	public void getleafCounts(String[] node,ArrayList items,HashMap itemsCountMap)
	{
		int i=0;
		for(Iterator t=items.iterator();t.hasNext();)
		{
			String[] temp=(String[])t.next();
			if(node[0].equals(temp[1]))
			{
				i++;
			}
		}		
		if(i==0)
			leafNodes+="/"+node[0];
		else
		{
			if(itemsCountMap.get(node[0])!=null&&leafNodes.indexOf("/"+node[0])==-1)
				leafNodes+="/"+node[0];
			
			for(Iterator t=items.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();
				if(node[0].equals(temp[1]))
				{
					if (itemsCountMap.get(node[0]) != null && leafNodes.indexOf("/" + node[0]) == -1)
				    	leafNodes += "/" + node[0];
					else if(itemsCountMap.get(temp[0])!=null&&leafNodes.indexOf("/"+node[0])==-1)
						leafNodes+="/"+node[0];
					getleafCounts(temp,items,itemsCountMap);	//递归
					
				}
			}
		}		
	}
	
	
	
	//判断是否有最高标度限制
	public String isOverLimitation(String gradeClass,String limitation,String userValue,String plan_id,String mainBodyId,String object_id,String templateId,String scoreflag,String wholeEval,String wholeEval_value) throws GeneralException
	{
		String info="";
		try
		{
			String[] user_value=userValue.split("/");
			/* 得到某计划某人的考评对象集合 */
			ArrayList objectList=batchGradeBo.getPerplanObjects(Integer.parseInt(plan_id),mainBodyId);			
			 /* 得到某计划考核主体给对象的评分结果hashMap */
			 HashMap perTableMap=batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id),mainBodyId,objectList);					 
			 ArrayList pointList=batchGradeBo.getPerPointList(templateId,plan_id);			
			ArrayList a_pointList=(ArrayList)pointList.get(1);			
			HashMap perPointScoreMap=batchGradeBo.getPerPointScore(templateId);			 //得到模版下各指标的分值及最大上限值和最小下限值
			
			
//			int max=0;    //最高标度数
//			if(Float.parseFloat(limitation)>=1)
//				max=Integer.parseInt(limitation);
//			else
//				max=Integer.parseInt(PubFunc.round(String.valueOf(Float.parseFloat(limitation)*objectList.size()),0));
			
			int max=0;    //最高标度数
		 	double temp0=0;
			if(Double.parseDouble(limitation)>=1)
				temp0=Double.parseDouble(limitation);
			else
				temp0=Double.parseDouble(limitation)*objectList.size();
			max=(int)Math.floor(temp0); // 向下取整 3.0 3.1 3.8 返回 3.0 3.0 3.0
			
			for(int i=0;i<a_pointList.size();i++)
			{
				String[] a_point=(String[])a_pointList.get(i);
				if(!"null".equals(user_value[i]))
				{
					String[] pointScore=(String[])perPointScoreMap.get(a_point[0]);
					String degreeMax=pointScore[4];
					int num=0;
					
					for(int j=0;j<objectList.size();j++)
					{
						String[] temp=(String[])objectList.get(j);
						if(temp[0].equals(object_id))
							continue;
						HashMap objectResultMap=(HashMap)perTableMap.get(temp[0]);		
						if(objectResultMap!=null)
						{	
								
							String[] temp2=(String[])objectResultMap.get(a_point[0]);
							if(temp2!=null&&temp2[6]!=null&&!object_id.equals(temp[0])&&temp2[6].equals(degreeMax))
								num++;
						}
					}
					if(num>=max)
					{
						
						if(user_value[i].trim().equals(pointScore[4]))
						{
							info=a_point[1]+ResourceFactory.getProperty("lable.performance.singleGrade.info2");
							break;
						}
						if(isDigital(user_value[i].trim()))
						{
							if(Float.parseFloat(user_value[i])<=Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[7])&&Float.parseFloat(user_value[i])>=Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[8]))
							{
								info=a_point[1]+ResourceFactory.getProperty("lable.performance.singleGrade.info2");
								break;
							}
						}
					}
					
				}
			}
			
			if("true".equals(wholeEval)&&"0".equals(this.WholeEvalMode))
			{
				ArrayList wholeEvalList=batchGradeBo.getExtendInfoValue("2",gradeClass);	
				if(wholeEvalList.size()>0)
				{
					String[] t=(String[])wholeEvalList.get(0);
					int num=0;
					for(int j=0;j<objectList.size();j++)
					{
						String[] temp=(String[])objectList.get(j);
						HashMap objectResultMap=(HashMap)perTableMap.get(temp[0]);	
						if(objectResultMap!=null)
						{
							String temp2=(String)objectResultMap.get("whole_grade_id");
							if(temp2!=null&&!object_id.equals(temp[0])&&temp2.equals(t[0]))
								num++;
						}
					}
					if(num>=max)
					{
						if(wholeEval_value!=null&&wholeEval_value.equals(t[0]))
						{
							info=ResourceFactory.getProperty("lable.performance.singleGrade.info1");						
						}
					}
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return info;
	}
	
	
	
	
	public boolean isDigital(String str)
	{
		String number = "1234567890.";
        for (int i = 0; i <str.length(); i++) {
            if (number.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
	}
	
	
/*****************************         个人绩效信息                ***********************************************88/	
	
	/**
	 * @param statMethod //1:按年统计 2：按月统计 3：按季度统计  4：按半年统计 9-时间段
	 * @param object_id
	 * @param mainbody_id
	 * @param plan_id
	 * @param fieldSet
	 * @param perCompare  个人绩效比对 0：不比对  1：比对
	 * @param perSetShowMode  //绩效子集显示方式  1-明细项，2-合计项 或 3-两者者显
	 * @param startDate   起始时间
	 * @param endDate     结束时间
	 * @return [0:String] changFlag, [1:String[] ]reportTitles[],[2: ArrayList] performanceList  ,[3:String[] ]itemidList
	 */
	public ArrayList getIndividualPerformance(UserView userView,String perSetShowMode,String statMethod,String year,String month,String count,String quarter,String halfYear,String perCompare,String object_id,String mainbody_id,String plan_id,String fieldSet,String startDate,String endDate)
	{
		ArrayList list=new ArrayList();		
		ArrayList filedItemList=getFiledItemListByFieldSetID(fieldSet.toUpperCase(),userView);
		String   objectType=getObjectType(plan_id);        // 考核对象类型     1：部门      2:人员
		String[] reportTitles=getReportTitles(filedItemList,objectType);
		ArrayList itemidList=getItemIDList(filedItemList,objectType);
		String   changFlag=getChangFlag(filedItemList);    // 0：不按年月变化  1：按月变化  2：按年变化
		ArrayList a_performanceList=new ArrayList();
		ArrayList performanceList=new ArrayList();
		
		if(!"1".equals(perSetShowMode))
			perCompare="0";
		String   sql=getList_SQL(statMethod,changFlag,filedItemList,year,month,count,quarter,halfYear,perCompare,object_id,mainbody_id,plan_id,fieldSet,objectType,startDate,endDate);
		 
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			RowSet rowSet=dao.search(sql);		
			while(rowSet.next())
			{
				LazyDynaMap dynaBean1 = new LazyDynaMap();
				dynaBean1.set("aa",rowSet.getString(1));
				for(int i=0;i<itemidList.size();i++)
				{
					
					LazyDynaBean lazyDynaMap=(LazyDynaBean)itemidList.get(i);
					String itemType=(String)lazyDynaMap.get("itemtype");
					 
					String context="";
					if("M".equalsIgnoreCase(itemType))
						context=Sql_switcher.readMemo(rowSet,(String)lazyDynaMap.get("itemid"));
					else
						context=rowSet.getString((String)lazyDynaMap.get("itemid")); 
					if(context!=null)
					{
						dynaBean1.set("a"+String.valueOf(i),context);
						if("2".equals(objectType))
						{
							context=context.trim();
							if(i==0)
								dynaBean1.set("a"+String.valueOf(i),AdminCode.getCodeName("UN",context));
							if(i==1)
							{
								if(Integer.parseInt(display_e0122)==0)
								{
									dynaBean1.set("a"+String.valueOf(i),AdminCode.getCodeName("UM",context));
								}
								else
								{
									CodeItem item=AdminCode.getCode("UM",context,Integer.parseInt(display_e0122));
					    	    	if(item!=null)
					    	    	{
					    	    		dynaBean1.set("a"+String.valueOf(i),item.getCodename());
					        		}
					    	    	else
					    	    	{
					    	    		dynaBean1.set("a"+String.valueOf(i),AdminCode.getCodeName("UM",context));
					    	    	}
					    	    	
								}
							}
						}
						
					}
					else
					{
						
						dynaBean1.set("a"+String.valueOf(i)," ");
					}
				}
				performanceList.add(dynaBean1);
			}
			
			
			a_performanceList=getShowPerformanceList(performanceList,perSetShowMode,itemidList,filedItemList,objectType);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		list.add(changFlag);
		list.add(reportTitles);
		//list.add(performanceList);
		list.add(a_performanceList);
		list.add(itemidList);
		return list;
		
	}
	
	
	/**
	 * 根据显示方式重新过滤数据集
	 * @param performanceList
	 * @param perSetShowMode //绩效子集显示方式  1-明细项，2-合计项 或 3-两者者显
	 * @param itemidList
	 * @return
	 */
	public ArrayList getShowPerformanceList(ArrayList performanceList,String perSetShowMode,ArrayList itemidList,ArrayList filedItemList,String objectType)
	{
		ArrayList showPerformanceList = new ArrayList();
		if ("1".equals(perSetShowMode))
			showPerformanceList = performanceList;
		else if ("2".equals(perSetShowMode)) {					//显示合计项
			LazyDynaMap dynaBean1 = new LazyDynaMap();
			HashMap objSumMap=new HashMap();
			
			boolean isNew = false;
			for (int i = 0; i < performanceList.size(); i++) {
				LazyDynaMap aLazyDynaMap = (LazyDynaMap) performanceList.get(i);
				String id=(String)aLazyDynaMap.get("aa");
				
				if(objSumMap.get(id)==null)
				{
					dynaBean1 = new LazyDynaMap();
					objSumMap.put(id,dynaBean1);
				}
				dynaBean1=(LazyDynaMap)objSumMap.get(id);
				 
				
				for (int a = 0; a < itemidList.size(); a++) {
				/*
					String itemType = "A";
					String itemid="";
					if (a > 0) {
						LazyDynaMap a_dynaBean = (LazyDynaMap) filedItemList
								.get(a - 1);
						itemType = (String) a_dynaBean.get("itemtype");
						itemid= (String)a_dynaBean.get("itemid");
					}
					*/
					LazyDynaBean lazyDynaMap=(LazyDynaBean)itemidList.get(a);
					String itemType=(String)lazyDynaMap.get("itemtype");
					String itemid=(String)lazyDynaMap.get("itemid");
					
					if ("N".equalsIgnoreCase(itemType)) {
							if("Z1".equalsIgnoreCase(itemid.substring(itemid.length()-2)))  //故意不让次数出现  2013.11.28 pjf
							{
								dynaBean1.set("a" + a,"");
							}
							else
							{
								if(dynaBean1.get("a" + a)==null||((String)dynaBean1.get("a" + a)).trim().length()==0)
									dynaBean1.set("a"+a,"0");
								if(aLazyDynaMap.get("a" + a)==null||((String)aLazyDynaMap.get("a" + a)).trim().length()==0)
									aLazyDynaMap.set("a"+a,"0");
								float temp = Float
										.parseFloat((String) dynaBean1
												.get("a" + a))
										+ Float
												.parseFloat((String) aLazyDynaMap
														.get("a" + a));
								dynaBean1
										.set("a" + a, String.valueOf(temp));
							}
						
						
					} else {
						dynaBean1.set("a" + a, (String) aLazyDynaMap
								.get("a" + a));
					}
					
					/*
					if (i == 0) {
						dynaBean1.set("a" + a, (String) aLazyDynaMap.get("a"
								+ a));

					} else {

						if (((String) dynaBean1.get("a0"))
								.equals((String) aLazyDynaMap.get("a0"))) {
							if (itemType.equalsIgnoreCase("N")) {

								if (dynaBean1.get("a" + a) != null
										&& !((String) dynaBean1.get("a" + a))
												.equals(" ")
										&& aLazyDynaMap.get("a" + a) != null
										&& !((String) aLazyDynaMap.get("a" + a))
												.equals(" ")) {	
									if(itemid.substring(itemid.length()-2).equalsIgnoreCase("Z1"))
									{
										int temp = Integer.parseInt((String) dynaBean1
														.get("a" + a)) + 1;
										dynaBean1.set("a" + a, String
												.valueOf(temp));
									}
									else
									{
										float temp = Float
												.parseFloat((String) dynaBean1
														.get("a" + a))
												+ Float
														.parseFloat((String) aLazyDynaMap
																.get("a" + a));
										dynaBean1
												.set("a" + a, String.valueOf(temp));
									}
								
								
								}
							} else {
								dynaBean1.set("a" + a, (String) aLazyDynaMap
										.get("a" + a));
							}
						}
					}
					*/
				}
				
				 
				/*
				if ((i != 0 && !((String) dynaBean1.get("a0"))
						.equals((String) aLazyDynaMap.get("a0")))
						|| i == performanceList.size() - 1) {
					showPerformanceList.add(dynaBean1);
					dynaBean1 = aLazyDynaMap;
				}
				 */
			}
			
			Set set=objSumMap.keySet();
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				LazyDynaMap map=(LazyDynaMap)objSumMap.get(key);
				showPerformanceList.add(map); 
			}
			
			

		} else if ("3".equals(perSetShowMode)) {
			LazyDynaMap dynaBean1 = new LazyDynaMap();
			boolean isNew = false;
			LazyDynaMap aLazyDynaMap=new LazyDynaMap();
			HashMap objSumMap=new HashMap();
			String o_id="";
			for (int i = 0; i < performanceList.size(); i++) {

				aLazyDynaMap = (LazyDynaMap) performanceList.get(i);
				String id=(String)aLazyDynaMap.get("aa"); 
				if(objSumMap.get(id)==null)
				{
					dynaBean1 = new LazyDynaMap();
					objSumMap.put(id,dynaBean1);
				}
				dynaBean1=(LazyDynaMap)objSumMap.get(id);
				if(o_id.length()==0)
					o_id=id;
				

				for (int a = 0; a < itemidList.size(); a++) {
				/*
					String itemType = "A";
					String itemid="";
					if (a > 0) {
						LazyDynaMap a_dynaBean = (LazyDynaMap) filedItemList
								.get(a - 1);
						itemType = (String) a_dynaBean.get("itemtype");
						itemid= (String)a_dynaBean.get("itemid");
					}
               */
					LazyDynaBean lazyDynaMap=(LazyDynaBean)itemidList.get(a);
					String itemType=(String)lazyDynaMap.get("itemtype");
					String itemid=(String)lazyDynaMap.get("itemid");
					
					if ("N".equalsIgnoreCase(itemType)) {
						if("Z1".equalsIgnoreCase(itemid.substring(itemid.length()-2)))
						{
							dynaBean1.set("a" + a,"");
						}
						else
						{
							if(dynaBean1.get("a" + a)==null||((String)dynaBean1.get("a" + a)).trim().length()==0)
								dynaBean1.set("a"+a,"0");
							if(aLazyDynaMap.get("a" + a)==null||((String)aLazyDynaMap.get("a" + a)).trim().length()==0)
								aLazyDynaMap.set("a"+a,"0");
							float temp = Float
									.parseFloat((String) dynaBean1
											.get("a" + a))
									+ Float
											.parseFloat((String) aLazyDynaMap
													.get("a" + a));
							dynaBean1
									.set("a" + a, String.valueOf(temp));
						}
					
					
					} else {
						
						
						if(!"2".equalsIgnoreCase(objectType)&&a==0)
							dynaBean1.set("a" + a, (String) aLazyDynaMap
								.get("a" + a));
						else if("2".equalsIgnoreCase(objectType)&&a==2)
							dynaBean1.set("a" + a, (String) aLazyDynaMap
									.get("a" + a));
						else
							dynaBean1.set("a" + a," ");
					}
					
					
					
					/*
					if (i == 0) {
						dynaBean1.set("a" + a, (String) aLazyDynaMap.get("a"
								+ a));

					} else {

						if (((String) dynaBean1.get("a0"))
								.equals((String) aLazyDynaMap.get("a0"))) {
							if (itemType.equalsIgnoreCase("N")) {

								if (dynaBean1.get("a" + a) != null
										&& !((String) dynaBean1.get("a" + a))
												.equals(" ")
										&& aLazyDynaMap.get("a" + a) != null
										&& !((String) aLazyDynaMap.get("a" + a))
												.equals(" ")) {
									
									if(itemid.substring(itemid.length()-2).equalsIgnoreCase("Z1"))
									{
										int temp =Integer.parseInt((String) dynaBean1
														.get("a" + a)) + 1;
										dynaBean1.set("a" + a, String
												.valueOf(temp));
									}
									else
									{
										float temp = Float
												.parseFloat((String) dynaBean1
														.get("a" + a))
												+ Float
														.parseFloat((String) aLazyDynaMap
																.get("a" + a));
										dynaBean1
												.set("a" + a, String.valueOf(temp));
									}
								}
							} else {
								if(a==0)
									dynaBean1.set("a" + a, (String) aLazyDynaMap
										.get("a" + a));
								else
									dynaBean1.set("a" + a," ");
									
							}
						}
					}
					
					*/
				}

				
				if(!o_id.equalsIgnoreCase(id))
				{
					LazyDynaMap dynaBean_o=(LazyDynaMap)objSumMap.get(o_id);
					if("2".equalsIgnoreCase(objectType))
						dynaBean_o.set("a2",((String)dynaBean_o.get("a2"))+"("+ResourceFactory.getProperty("planar.stat.total")+")");
					else
						dynaBean_o.set("a0",((String)dynaBean_o.get("a0"))+"("+ResourceFactory.getProperty("planar.stat.total")+")");
					showPerformanceList.add(dynaBean_o);
					o_id=id;
				}
				showPerformanceList.add(aLazyDynaMap);
				
				/*
				if ((i != 0 && !((String) dynaBean1.get("a0"))
						.equals((String) aLazyDynaMap.get("a0")))
						|| i == performanceList.size() - 1) {
					if( i == performanceList.size() - 1)
						showPerformanceList.add(aLazyDynaMap);
					dynaBean1.set("a0",((String)dynaBean1.get("a0"))+"("+ResourceFactory.getProperty("planar.stat.total")+")");
					showPerformanceList.add(dynaBean1);
		
					if(i!= (performanceList.size() - 1))
					{
						showPerformanceList.add(getNewDynaMap(aLazyDynaMap,itemidList));
					
					}
					dynaBean1 = aLazyDynaMap;
					
				}
				else
					showPerformanceList.add(aLazyDynaMap);
					*/

			} 
			if(o_id.length()>0)
			{
				LazyDynaMap dynaBean_o=(LazyDynaMap)objSumMap.get(o_id);
				if("2".equalsIgnoreCase(objectType))
					dynaBean_o.set("a2",((String)dynaBean_o.get("a2"))+"("+ResourceFactory.getProperty("planar.stat.total")+")");
				else
					dynaBean_o.set("a0",((String)dynaBean_o.get("a0"))+"("+ResourceFactory.getProperty("planar.stat.total")+")");
				showPerformanceList.add(dynaBean_o);
			}
			
		}
		return showPerformanceList;
	}
	
	
	public LazyDynaMap getNewDynaMap(LazyDynaMap oldMap,ArrayList itemidList)
	{
		LazyDynaMap aMap=new LazyDynaMap();
		for (int a = 0; a < itemidList.size(); a++) {
			aMap.set("a" + a, (String) oldMap.get("a"+ a));
		}
		return aMap;
	}
	
	
	/**
	 * 根据条件取得 sql
	 * @param statMethod //1:按年统计 2：按月统计 3：按季度统计  4：按半年统计
	 * @param year   	  年
	 * @param month  	  月
	 * @param count  	  次
	 * @param perCompare  个人绩效比对 0：不比对  1：比对
	 * @param object_id   考核对象id
	 * @param mainbody_id 考核主体id
	 * @param plan_id     考核计划id
	 * @param fieldSet    指标集id
	 * @param filedItemList  某指标集下的指标信息列表
	 * @param objectType  考核对象类型
	 * @return String
	 */
	public String getList_SQL(String statMethod,String changFlag,ArrayList filedItemList,String year,String month,String count,String quarter,String halfYear,String perCompare,String object_id,String mainbody_id,String plan_id,String fieldSet,String  objectType,String startDate,String endDate)
	{
		String sql="";
		if("2".equals(objectType))
		{
			sql=getList_SQL_A(statMethod,changFlag,filedItemList,year,month,count,quarter,halfYear,perCompare,object_id,mainbody_id,plan_id,fieldSet,objectType,startDate,endDate);
		}
		else
		{
			sql=getList_SQL_B(statMethod,changFlag,filedItemList,year,month,count,quarter,halfYear,perCompare,object_id,mainbody_id,plan_id,fieldSet,objectType,startDate,endDate);
		}
		return sql;
	}
	
	
	

	
	
	public String getList_SQL_A(String statMethod,String changFlag,ArrayList filedItemList,String year,String month,String count,String quarter,String halfYear,String perCompare,String object_id,String mainbody_id,String plan_id,String fieldSet,String  objectType,String startDate,String endDate)
	{
		String sql = "";

		StringBuffer sql_select = new StringBuffer("select usra01.a0100,usra01.b0110,usra01.e0122,usra01.a0101");
		StringBuffer sql_from0 = new StringBuffer("");
		StringBuffer sql_from = new StringBuffer(
				" left outer join usra01 on usr" + fieldSet
						+ ".a0100=usra01.a0100 left outer join per_object on  usra01.a0100=per_object.object_id ");
		StringBuffer sql_where = new StringBuffer(" where 1=1 ");
		for (int i = 0; i < filedItemList.size(); i++) {
			LazyDynaMap dynaBean1 = (LazyDynaMap) filedItemList.get(i);
			String a_itemid = (String) dynaBean1.get("itemid");
			String a_itemtype = (String) dynaBean1.get("itemtype");
			String a_codesetid = (String) dynaBean1.get("codesetid");
			if ("D".equalsIgnoreCase(a_itemtype)) {
				if (a_itemid.equalsIgnoreCase(fieldSet + "Z0")) {
					if ("1".equals(changFlag)) // 按月变化
					{
						sql_select.append(","
								+ Sql_switcher.numberToChar(Sql_switcher
										.year(fieldSet + "Z0"))
								+ Sql_switcher.concat()
								+ "'"+ResourceFactory.getProperty("datestyle.year")+"'"
								+ Sql_switcher.concat()
								+ Sql_switcher.numberToChar(Sql_switcher
										.month(fieldSet + "Z0" ))
								+ Sql_switcher.concat() + "'"+ResourceFactory.getProperty("datestyle.month")+"' " 
								+ fieldSet
								+ "Z0 ,"
								+Sql_switcher.numberToChar(Sql_switcher
										.month(fieldSet + "Z0"))
								+ Sql_switcher.concat()
								+"'"+ResourceFactory.getProperty("datestyle.year")+"'");

					}
					if ("2".equals(changFlag)) // 按年变化
					{
						sql_select.append(","
								+ Sql_switcher.numberToChar(Sql_switcher
										.year(fieldSet + "Z0"))
								+ Sql_switcher.concat() + "'"+ResourceFactory.getProperty("datestyle.year")+"' " 							
								+ fieldSet
								+ "Z0");
					}
				} else {
					sql_select.append(","
							+ Sql_switcher.numberToChar(Sql_switcher
									.year(a_itemid))
							+ Sql_switcher.concat()
							+ "'-'"
							+ Sql_switcher.concat()
							+ Sql_switcher.numberToChar(Sql_switcher
									.month(a_itemid))
							+ Sql_switcher.concat()
							+ "'-'"
							+ Sql_switcher.concat()
							+ Sql_switcher.numberToChar(Sql_switcher
									.day(a_itemid)) + " " + a_itemid);

				}
			} else {
				if (a_codesetid != null && a_codesetid.trim().length() > 1) {
					sql_select.append(",tt" + i + ".codeitemdesc " + a_itemid);
					sql_from
							.append(" left outer join (select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"
									+ a_codesetid
									+ "' ) tt"
									+ i
									+ " on usr"
									+ fieldSet
									+ "."
									+ a_itemid
									+ "=tt"
									+ i
									+ ".codeitemid");
				} else {
					sql_select.append(",usr" + fieldSet + "."
							+ (String) dynaBean1.get("itemid"));
				}
			}
		}

		// 年月次
		if ("0".equals(changFlag)) {
			if (!"A01".equalsIgnoreCase(fieldSet))
				sql_from0.append(" from ( select * from usr" + fieldSet
						+ " a where a.i9999=(select max(i9999) from usr"
						+ fieldSet + " b where a.a0100=b.a0100 ) ) usr"
						+ fieldSet);
			else
				sql_from0.append(" from usr" + fieldSet);
		} 
		else
		{
			ArrayList subSqllist=get_FROM_WHL_SQL(plan_id,1,statMethod,fieldSet,year,month,count,quarter,halfYear,startDate,endDate);
			sql_from0.append((String)subSqllist.get(0));
			sql_where.append((String)subSqllist.get(1));
			
		}

		//比对
		if ("1".equals(perCompare)||object_id.trim().length()==0) {
			sql_where.append(" and usr" + fieldSet + ".a0100 in ("
					+ getPlanObjectids(plan_id, mainbody_id) + ")");
		} else {
			sql_where.append(" and usr" + fieldSet + ".a0100='" + object_id
					+ "'");

		}

		sql = sql_select.toString() + sql_from0.toString()
				+ sql_from.toString() + sql_where.toString()+" and per_object.plan_id="+plan_id+" order by per_object.a0000,usr" + fieldSet + ".a0100";
		if("1".equals(changFlag)|| "2".equals(changFlag))
			sql+=",usr" + fieldSet + "."+fieldSet+"Z0 desc,usr" + fieldSet + "."+fieldSet+"Z1";  //2013.11.28 pjf
		return sql;
	}
	
	
	
	public ArrayList get_FROM_WHL_SQL(String planid,int flag,String statMethod,String fieldSet,String year,String month,String count,String quarter,String halfYear,String startDate,String endDate)
	{
		ArrayList list=new ArrayList();
		StringBuffer sql_from0=new StringBuffer("");
		StringBuffer sql_where=new StringBuffer("");
//		statMethod 1:按年统计 2：按月统计 3：按季度统计  4：按半年统计
		String pre="";
		if(flag==1)
			pre="usr";
		
		if ("2".equals(statMethod)) {
			sql_from0.append(" from "+pre+ fieldSet);
			sql_where.append(" and  "
					+ Sql_switcher.year(pre+ fieldSet + "." + fieldSet
							+ "Z0")
					+ "="
					+ year
					+ " and  "
					+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet
							+ "Z0") + "=" + month);
			if(!"0".equals(count))
				sql_where.append(" and "+pre+ fieldSet + "." + fieldSet + "Z1="+ count);
		} else if ("1".equals(statMethod)) {
			sql_from0.append(" from "+pre+ fieldSet);
			sql_where.append(" and  "
					+ Sql_switcher.year(pre+ fieldSet + "." + fieldSet
							+ "Z0") + "=" + year);
			if(!"0".equals(count))
				sql_where.append(" and "+pre+ fieldSet + "." + fieldSet + "Z1="+ count);
			
		}else if ("3".equals(statMethod)) {
			StringBuffer month_str=new StringBuffer(" ");
			if("1".equals(quarter))
			{
				month_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=1 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=2 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=3 ");
			}
			else if("2".equals(quarter))
			{
				month_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=4 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=5 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=6 ");

			}
			else if("3".equals(quarter))
			{
				month_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=7 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=8 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=9 ");

			}
			else if("4".equals(quarter))
			{
				month_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=10 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=11 or "+Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=12 ");

			}
			
			sql_from0.append(" from "+pre+ fieldSet);
			sql_where.append(" and  "
					+ Sql_switcher.year(pre+ fieldSet + "." + fieldSet
							+ "Z0")
					+ "="
					+ year
					+ " and ( "
					+ month_str.toString()
					+" )");
			
			
		}else if ("4".equals(statMethod)) {
			StringBuffer halfYear_str=new StringBuffer(" ");
			if("1".equals(halfYear))
			{
				halfYear_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "<7" );
			}
			else if("2".equals(halfYear))
			{
				halfYear_str.append(Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + ">6");

			}
			
			
			sql_from0.append(" from "+pre+ fieldSet);
			sql_where.append(" and  "
					+ Sql_switcher.year(pre+ fieldSet + "." + fieldSet
							+ "Z0")
					+ "="
					+ year
					+ " and  "
					+ halfYear_str.toString());
			
		}
		else if("9".equals(statMethod))
		{
			String[] startDates=startDate.split("-");
			String[] endDates=endDate.split("-");
			sql_from0.append(" from "+pre+ fieldSet);
			if(startDate!=null&&startDate.trim().length()>0)
			{
				sql_where.append(" and ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ ">"+ startDates[0]);
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ startDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + ">" + startDates[1]+" ) ");
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ startDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=" + startDates[1]+" and "+Sql_switcher.day(pre+fieldSet+"."+fieldSet+"Z0")+">="+startDates[2]);
				sql_where.append(" ) )");
				
			}
			if(endDate!=null&&endDate.trim().length()>0)
			{ 
			
			sql_where.append(" and ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "<"+ endDates[0]);
			sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ endDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "<" + endDates[1]+" ) ");
			sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ endDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=" + endDates[1]+" and "+Sql_switcher.day(pre+fieldSet+"."+fieldSet+"Z0")+"<="+endDates[2]);
			sql_where.append(" ) )");
			}
			
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=loadxml.getDegreeWhole();
			String aStartDate=(String)htxml.get("StatStartDate");
			String aEndDate=(String)htxml.get("StatEndDate");
			
			if(aStartDate!=null&&aStartDate.trim().length()>0)
			{
				startDates=aStartDate.split("\\.");
				sql_where.append(" and ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ ">"+ startDates[0]);
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ startDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + ">" + startDates[1]+" ) ");
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ startDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=" + startDates[1]+" and "+Sql_switcher.day(pre+fieldSet+"."+fieldSet+"Z0")+">="+startDates[2]);
				sql_where.append(" ) )");
				
			}
			if(aEndDate!=null&&aEndDate.trim().length()>0)
			{ 
				endDates=aEndDate.split("\\.");
				sql_where.append(" and ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "<"+ endDates[0]);
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ endDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "<" + endDates[1]+" ) ");
				sql_where.append(" or ( "+Sql_switcher.year(pre+ fieldSet + "." + fieldSet+ "Z0")+ "="+ endDates[0]+ " and  "+ Sql_switcher.month(pre+ fieldSet + "." + fieldSet+ "Z0") + "=" + endDates[1]+" and "+Sql_switcher.day(pre+fieldSet+"."+fieldSet+"Z0")+"<="+endDates[2]);
				sql_where.append(" ) )");
			}
			
		}
		
		list.add(sql_from0.toString());
		list.add(sql_where.toString());
		
		return list;		
	}
	
	
	
	
	public String getList_SQL_B(String statMethod,String changFlag,ArrayList filedItemList,String year,String month,String count,String quarter,String halfYear,String perCompare,String object_id,String mainbody_id,String plan_id,String fieldSet,String  objectType,String startDate,String endDate)
	{
		String sql = "";

		StringBuffer sql_select = new StringBuffer("select organization.codeitemid,organization.codeitemdesc");
		StringBuffer sql_from0 = new StringBuffer("");
		StringBuffer sql_from = new StringBuffer(
				"  left outer join per_object on  "+fieldSet+".b0110=per_object.object_id  left outer join organization on " + fieldSet
						+ ".b0110=organization.codeitemid");
		StringBuffer sql_where = new StringBuffer(" where 1=1 ");
		
		for (int i = 0; i < filedItemList.size(); i++) {
			LazyDynaMap dynaBean1 = (LazyDynaMap) filedItemList.get(i);
			String a_itemid = (String) dynaBean1.get("itemid");
			String a_itemtype = (String) dynaBean1.get("itemtype");
			String a_codesetid = (String) dynaBean1.get("codesetid");
			if ("D".equalsIgnoreCase(a_itemtype)) {
				if (a_itemid.equalsIgnoreCase(fieldSet + "Z0")) {
					if ("1".equals(changFlag)) // 按月变化
					{
						sql_select.append(","
								+ Sql_switcher.numberToChar(Sql_switcher
										.year(fieldSet + "Z0"))
								+ Sql_switcher.concat()
								+ "'"+ResourceFactory.getProperty("datestyle.year")+"'"
								+ Sql_switcher.concat()
								+ Sql_switcher.numberToChar(Sql_switcher
										.month(fieldSet + "Z0"))
								+ Sql_switcher.concat() + "'"+ResourceFactory.getProperty("datestyle.month")+"' " 
								+ fieldSet
								+ "Z0");

					}
					if ("2".equals(changFlag)) // 按年变化
					{
						sql_select.append(","
								+ Sql_switcher.numberToChar(Sql_switcher
										.year(fieldSet + "Z0"))
								+ Sql_switcher.concat() + "'"+ResourceFactory.getProperty("datestyle.year")+"' " 								
								+ fieldSet
								+ "Z0");
					}
				} else {
					sql_select.append(","
							+ Sql_switcher.numberToChar(Sql_switcher
									.year(a_itemid))
							+ Sql_switcher.concat()
							+ "'-'"
							+ Sql_switcher.concat()
							+ Sql_switcher.numberToChar(Sql_switcher
									.month(a_itemid))
							+ Sql_switcher.concat()
							+ "'-'"
							+ Sql_switcher.concat()
							+ Sql_switcher.numberToChar(Sql_switcher
									.day(a_itemid)) + " " + a_itemid);

				}
			} else {
				if (a_codesetid != null && a_codesetid.trim().length() > 1) {
					sql_select.append(",tt" + i + ".codeitemdesc " + a_itemid);
					sql_from.append(" left outer join ");
					if(!"UN".equalsIgnoreCase(a_codesetid)&&!"UM".equalsIgnoreCase(a_codesetid)&&!"@K".equalsIgnoreCase(a_codesetid))
						sql_from.append("  (select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"+ a_codesetid+ "' )");
					else
						sql_from.append("  (select codesetid,codeitemid,codeitemdesc from organization where codesetid='"+ a_codesetid+ "' )");
					sql_from.append("tt"+ i+ " on "+ fieldSet+ "."+ a_itemid+ "=tt"+ i+ ".codeitemid");
				} else {
					sql_select.append("," + fieldSet + "."+ (String) dynaBean1.get("itemid"));
				}
			}
		}

		// 年月次
		if ("0".equals(changFlag)) {
			if (!"B01".equalsIgnoreCase(fieldSet))
				sql_from0.append(" from ( select * from " + fieldSet
						+ " a where a.i9999=(select max(i9999) from "
						+ fieldSet + " b where a.b0110=b.b0110 ) ) "
						+ fieldSet);
			else
				sql_from0.append(" from " + fieldSet);
		} 
		else
		{
			ArrayList subSqllist=get_FROM_WHL_SQL(plan_id,2,statMethod,fieldSet,year,month,count,quarter,halfYear,startDate,endDate);
			sql_from0.append((String)subSqllist.get(0));
			sql_where.append((String)subSqllist.get(1));
		}

		//比对
		if ("1".equals(perCompare)||object_id.trim().length()==0) {
			sql_where.append(" and " + fieldSet + ".b0110 in ("
					+ getPlanObjectids(plan_id, mainbody_id) + ")");
		} else {
			sql_where.append(" and " + fieldSet + ".b0110='" + object_id
					+ "'");

		}

		sql = sql_select.toString() + sql_from0.toString()
				+ sql_from.toString() + sql_where.toString()+" and per_object.plan_id="+plan_id+" order by per_object.a0000 ";
		if("1".equals(changFlag)|| "2".equals(changFlag))
			sql+="," + fieldSet + "."+fieldSet+"Z0," + fieldSet + "."+fieldSet+"Z1";
		return sql;
	}
	
	
	
	
	/**
	 * 取得考核计划下某考核主体对应的所有考核对象的id
	 * @param plan_id
	 * @param mainbody_id
	 * @return "'00001','0002','0003'"
	 */
	public String getPlanObjectids(String plan_id,String mainbody_id)
	{
		StringBuffer sql=new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select object_id from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"'");
			int i=0;
			while(rowSet.next())
			{
				if(i==0)
					sql.append("'"+rowSet.getString("object_id")+"'");
				else
					sql.append(",'"+rowSet.getString("object_id")+"'");
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	
	
	
	
	
	/**
	 * 	取得指标集的按月变化标志
	 * @param filedItemList  某指标集下的指标信息列表
	 * @return
	 */
	public String getChangFlag(ArrayList filedItemList)
	{
		String changFlag="0";
		if(filedItemList.size()>0)
		{
			LazyDynaMap dynaBean1 =(LazyDynaMap)filedItemList.get(0);
			changFlag=(String)dynaBean1.get("changeflag");
		}
		return changFlag;
	}
	
	
	
	/**
	 * 取得表头信息
	 * @param filedItemList  某指标集下的指标信息列表
	 * @return
	 */
	public ArrayList getItemIDList(ArrayList filedItemList,String objectType)
	{
		ArrayList itemIDList=new ArrayList();
		LazyDynaBean dynaBean1 = new LazyDynaBean();	
		if("2".equals(objectType))
		{ 
			LazyDynaBean aa = new LazyDynaBean();
			aa.set("itemid","b0110");
			aa.set("itemtype","A");
			itemIDList.add(aa);
			LazyDynaBean ab = new LazyDynaBean();
			ab.set("itemid","e0122");
			ab.set("itemtype","A");
			itemIDList.add(ab);
			
			dynaBean1.set("itemid","a0101");
			dynaBean1.set("itemtype","A");
		}
		else
		{ 
			dynaBean1.set("itemid","codeitemdesc");
			dynaBean1.set("itemtype","A");
		}
		itemIDList.add(dynaBean1);
		for(int i=0;i<filedItemList.size();i++)
		{
			LazyDynaMap a_dynaBean1 =(LazyDynaMap)filedItemList.get(i);
			LazyDynaBean dynaBean = new LazyDynaBean();			
			dynaBean.set("itemid",((String)a_dynaBean1.get("itemid")).toLowerCase());
			dynaBean.set("itemtype",((String)a_dynaBean1.get("itemtype")).toLowerCase());
			itemIDList.add(dynaBean);
		}
		return itemIDList;
	}
	
	
	/**
	 * 取得表头信息
	 * @param filedItemList  某指标集下的指标信息列表
	 * @return
	 */
	public String[] getReportTitles(ArrayList filedItemList,String objectType)
	{
		String[] reportTitles=new String[filedItemList.size()+1];
		if("2".equals(objectType))
		{
			reportTitles=new String[filedItemList.size()+3];
			reportTitles[0]=ResourceFactory.getProperty("b0110.label");
			reportTitles[1]=ResourceFactory.getProperty("e0122.label");
			reportTitles[2]=ResourceFactory.getProperty("label.title.name");
		
		}
		else
			reportTitles[0]=ResourceFactory.getProperty("general.inform.org.organizationName");
		for(int i=0;i<filedItemList.size();i++)
		{
			LazyDynaMap dynaBean1 =(LazyDynaMap)filedItemList.get(i);
			if("2".equals(objectType))
				reportTitles[i+3]=(String)dynaBean1.get("itemdesc");
			else
				reportTitles[i+1]=(String)dynaBean1.get("itemdesc");
		}
		return reportTitles;
	}
	
	
	
	/**
	 * 取得考核计划下的 考核对象类型
	 * @param  planid
	 * @return 1:部门 2：人员 
	 */
	public String getObjectType(String planid)
	{
		String objectType="";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select object_type from per_plan where plan_id="+planid);
			if(rowSet.next())
				objectType=rowSet.getString("object_type");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectType;
	}
	
	
	
	/**
	 * 取得指标集下的指标 信息列表
	 * @param fieldSetID
	 * @return
	 */
	public ArrayList getFiledItemListByFieldSetID(String fieldSetID,UserView userView)
	{
		ArrayList fieldItemList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select fielditem.*,fieldset.changeflag from fielditem,fieldset where fieldset.fieldsetid=fielditem.fieldsetid and fielditem.useflag=1  and  fielditem.fieldsetid='"+fieldSetID+"' order by fielditem.displayid");
			while(rowSet.next())
			{
				if("0".equalsIgnoreCase(userView.analyseFieldPriv(rowSet.getString("itemid"))))
					continue;
				LazyDynaMap dynaBean1 = new LazyDynaMap();
				dynaBean1.set("itemid",rowSet.getString("itemid"));			
				dynaBean1.set("itemtype",rowSet.getString("itemtype"));
				dynaBean1.set("itemdesc",rowSet.getString("itemdesc"));
				dynaBean1.set("codesetid",rowSet.getString("codesetid"));
				dynaBean1.set("changeflag",rowSet.getString("changeflag"));
				fieldItemList.add(dynaBean1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldItemList;
	}
	
	
	private String fromType="1";//默认为1如果等于2的话，从招聘中调用的
	
	public void setFromType(String fromType)
	{
		this.fromType=fromType;
	}
	private String privPointStr;//通过资源分配授权的考核指标权限
	public void setPrivPointStr(String privPointStr)
	{
		this.privPointStr=privPointStr;
		
	}
	/** ******************************招聘管理（考核页面）*************************************  */
	
	/**
	 * 得到考核页面(招聘管理考核页面)
	 * @param template_id
	 * @param status  //权重分值表识 0：分值 1：权重
	 * @param userID
	 * @param a_status 考核对象状态
	 * @return
	 */
	public ArrayList getSingleGradeHtml(String template_id,String status,String userID,String object_id,String a_status,String titleName,String scoreFlag) throws GeneralException
	{	
		ArrayList list_temp=new ArrayList();
		try
		{
			
			ArrayList list=getPerformanceStencilList(template_id);
			ArrayList items=(ArrayList)list.get(0);				//模版项目列表(按顺序)
			int        lay=((Integer)list.get(1)).intValue();	//表头的层数
			HashMap    map=(HashMap)list.get(2);				//各项目包含的指标个数
			HashMap    subItemMap=(HashMap)list.get(3);			//各项目的子项目(hashmap)		
			ArrayList pointList=getZpPointList(template_id);			
			HashMap   pointItemMap=getPointItemList((ArrayList)pointList.get(1),items);			
			list_temp=getGradeHtml(userID,status,template_id,lay,map,subItemMap,items,pointList,pointItemMap,object_id,a_status,titleName,scoreFlag);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return list_temp;
	}
	
	

	/**
	 * 返回 招聘 某绩效模版的所有指标集
	 * @param templateID
	 * @return
	 */
	public ArrayList getZpPointList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();		
		ArrayList  pointGrageList=new ArrayList();
		ArrayList  a_pointGrageList=new ArrayList();
		ArrayList  pointList=new ArrayList();	
		ContentDAO dao=new ContentDAO(this.conn);
		String    isNull="0";                    //判断模版中指标标度上下限值是否设置
		RowSet     rowSet=null;
		
		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(ppo.getComOrPer(templateID,"temp"))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		HashMap   map2=new HashMap();
		String     sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+templateID+"'  ";  //pi.seq,
		if("2".equals(this.fromType))
		{
			if(this.privPointStr.length()>0)
			{
				sql+=" and UPPER(pp.point_id) in ('"+this.privPointStr.replaceAll(",","','").toUpperCase()+"')";
			}else{
				sql+=" and 1=2";
			}
		}
		sql+=" order by pp.seq";
		try
		{
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[13];
				for(int i=0;i<13;i++)
				{
					if(i==2)
						temp[i]=Sql_switcher.readMemo(rowSet,"pointname");
					else if(i==4)
						temp[i]=Sql_switcher.readMemo(rowSet,"gradedesc");
					else
						temp[i]=rowSet.getString(i+1);
					if(i==6||i==7)
					{
						if(temp[i]==null)
						{
							isNull="1";
						}
					}
					
				}
				a_pointGrageList.add(temp);
			}			
			sql="select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"' ";	  //pi.seq,
			if("2".equals(this.fromType))
			{
				if(this.privPointStr.length()>0)
				{
					sql+=" and UPPER(po.point_id) in ('"+this.privPointStr.replaceAll(",", "','").toUpperCase()+"')";
				}else{
					sql+=" and 1=2";
				}
			}
			sql+="  order by pp.seq";
			rowSet =dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[8];
				temp[0]=rowSet.getString(1);
				temp[1]=Sql_switcher.readMemo(rowSet,"pointname");
				temp[2]=rowSet.getString(3);
				temp[3]=rowSet.getString(4);
				temp[4]="";
				temp[5]=rowSet.getString("visible");
				temp[6]=rowSet.getString("fielditem");
				temp[7]=rowSet.getString("status");
			//	pointList.add(temp);
				map2.put(temp[0].toLowerCase(),temp);
			}
			
			
			//解决排列顺序问题
			ArrayList seqList=new ArrayList();
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.conn);
			ArrayList apointList=new ArrayList();
			ArrayList layItemList=new ArrayList();
			HashMap itemPoint=new HashMap();
			//分析绩效考核模版
			parameterSetBo.anaylseTemplateTable(apointList,layItemList,itemPoint,templateID,this.privPointStr);
			for(int i=0;i<apointList.size();i++)
			{
				seqList.add(((String)apointList.get(i)).toLowerCase());
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				if(map2.get(temp)!=null)
				pointList.add((String[])map2.get(temp));
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				for(Iterator t1=a_pointGrageList.iterator();t1.hasNext();)
				{
					String[] tt=(String[])t1.next();
					if(tt[1].toLowerCase().equals(temp))
						pointGrageList.add(tt);
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		list.add(pointGrageList);
		list.add(pointList);
		list.add(isNull);
		
		return list;
	}
	
	
	

	public ArrayList getGradeHtml(String userID,String status,String template_id,int lay,HashMap map,HashMap subItemMap,ArrayList items,ArrayList pointList,HashMap pointItemMap,String object_id,String a_status,String titleName,String a_scoreFlag) throws GeneralException
	{
		
		ArrayList list=new ArrayList();
	     StringBuffer html=new StringBuffer("<table   class='ListTable_self3' >");
	     StringBuffer personalComment=new StringBuffer(" ");
	     String       isNull="0";
	     String		  scoreflag="";
	     String       NodeKnowDegree="";
	     String 	  WholeEval="";
	     String       limitation="";
	     String 	  GradeClass="";
	     StringBuffer dataArea=new StringBuffer("");
		 try
		 {
			 Hashtable htxml=new Hashtable(); 
			 String ScaleToDegreeRule="3";   //分值转标度规则（1-就高 2-就低 3-就近就高（默认值）） 
			 NodeKnowDegree="false";	    //了解程度
			 WholeEval="false";			    //总体评价	
			 limitation="-1";                  //=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)		
			 scoreflag=a_scoreFlag;					//=2混合，=1标度
			 ArrayList pointInfoList=(ArrayList)pointList.get(0);           //指标详细集
			 ArrayList a_pointList=(ArrayList)pointList.get(1);             //指标集 
			 isNull=(String)pointList.get(2);				                //指标范围是否为空 0：不为空  1：为空
			 HashMap itemsSignMap=getItemsSignMap(items);		
			
			 /* 得到某计划考核主体给对象的评分结果hashMap */
			 HashMap perTableMap=getPerTableXXX(userID,object_id);
			 HashMap objectResultMap=(HashMap)perTableMap.get(object_id);			 

			 HashMap   a_pointmap=new HashMap();		   					  //得到具有某考核对象的指标权限map				
			 for(int i=0;i<a_pointList.size();i++)
			 {
				 String[] temp=(String[])a_pointList.get(i);
				 a_pointmap.put(temp[0],"1");
			 }
			 HashMap perPointScoreMap=batchGradeBo.getPerPointScore(template_id);			 //得到模版下各指标的分值及最大上限值和最小下限值
			 String[] temp={object_id," ",a_status};
			 
			 //加标题
			 html.append("<tr><td  class='common_old_color common_background_color' style='valign='middle' align='center' height='50'   colspan='"+(lay+1)+"' ><font face=宋体 style='font-weight:bold;font-size:15pt'> "+titleName+" </font> </td></tr>");
			 
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] point=(String[])t.next();
				 String[] pointScore=(String[])perPointScoreMap.get(point[0]);
				 
				 ///////////////////各指标的数值范围///////////////////////
				 
			
				 if(point[6]!=null&&point[6].trim().length()>0)
				 {
					 	ContentDAO dao=new ContentDAO(this.conn);
						String a_value="";
						try
						{
							String fieldSetID="";
							RowSet rowSet=dao.search("select * from fielditem where itemid='"+point[6]+"'");
							if(rowSet.next())
							{
								fieldSetID=rowSet.getString("fieldsetid");
							}
							String a_sql="";
							if("A01".equalsIgnoreCase(fieldSetID))
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+"  where a0100='"+object_id+"'";
							}
							else
							{
								a_sql="select "+point[6]+" from usr"+fieldSetID+" a where a.i9999 =(select max(I9999) from usr"+fieldSetID+" b where a0100='"+object_id+"' and a.a0100=b.a0100)";
							}
							rowSet=dao.search(a_sql);
							if(rowSet.next())
							{
								a_value=rowSet.getString(1);
							}
							
							if(a_value!=null&&!"".equals(a_value))
							{
							
							java.util.regex.Pattern p=java.util.regex.Pattern.compile("^\\d+$|^\\d+\\.\\d+$");
						    java.util.regex.Matcher m=p.matcher(a_value);   	
							if(m.matches())
							{
								PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
								String per_comTable = "per_grade_template"; // 绩效标准标度
								if(ppo.getComOrPer(template_id,"temp"))
									per_comTable = "per_grade_competence"; // 能力素质标准标度
				    			rowSet=dao.search("select pp.item_id,po.point_id,po.pointname,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue ,po.fielditem"
				    							+" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
				    							+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+template_id+"' "
				    							+" and pp.point_id='"+pointScore[0]+"'  order by pp.seq"	);	
				    			
				    			while(rowSet.next())
				    			{
				    				if("1".equals(pointScore[6])) //定量
				    				{
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
				    					}
				    				}
				    				else						  //定性
				    				{
				    				
				    					if(Float.parseFloat(a_value)>=rowSet.getFloat("bottom_value")*rowSet.getFloat("score"))
				    					{
				    						pointScore[2]=rowSet.getString("top_value");
				    						pointScore[4]=rowSet.getString("gradeCode");
				    						break;
				    					}
				    					
				    				}
				    			}
							}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				 }
				 
				 
				 
				 
						if("1".equals(point[2]))   //定量指标
						{
											
							dataArea.append("/");
							dataArea.append(PubFunc.round(pointScore[3],0));
							dataArea.append("*");
							dataArea.append(PubFunc.round(pointScore[2],0));
							dataArea.append("#");
							dataArea.append(pointScore[4]);
							dataArea.append("*");
							dataArea.append(pointScore[5]);	
						}
						else					  //定性指标
						{
							
							dataArea.append("/");
							if(pointScore[3]!=null&&pointScore[2]!=null)
							{
								dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[3])));
								dataArea.append("*");
								dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[2])));
							}
							else
							{
								dataArea.append("0");
								dataArea.append("*");
								dataArea.append(pointScore[1]);
							}
							dataArea.append("#");
							dataArea.append(pointScore[4]);
							dataArea.append("*");
							dataArea.append(pointScore[5]);
						}
						
						dataArea.append("#");
						dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[7])));
						dataArea.append("*");
						dataArea.append(String.valueOf(Float.parseFloat(pointScore[1])*Float.parseFloat(pointScore[8])));
						

				 //////////////////////////////////////////////////
				 
				 ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
				 int a_lay=lay;
				 html.append("<tr>");
				 
				 int pointItemLength=pointItemList.size(); 
				 String[] currentItem=null;
				 for(int i=0;i<a_lay;i++)
				 {
					 if(i==0)
					 {
						 String[] item=(String[])pointItemList.get(--pointItemLength);
						 String sign=(String)itemsSignMap.get(item[0]);
						 if(!"1".equals(sign))
						 {				
						
							 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
							 html.append(item[3]);
							 html.append("</td>");
							 currentItem=item;
							 itemsSignMap.put(item[0],"1");
						 }
					 }
					 else if(i!=0&&i!=a_lay-1)
					 {
						 if(--pointItemLength>=0)
						 {
						 
							 String[] item=(String[])pointItemList.get(pointItemLength);
							 String sign=(String)itemsSignMap.get(item[0]);
							 if(!"1".equals(sign))
							 {							
								 html.append("<td class='RecordRow'  valign='middle' align='center'  rowspan='"+(String)map.get(item[0])+"'  width='"+td_width+"' height='"+td_height+"'  >");
								 html.append(item[3]);
								 html.append("</td>");								
								 currentItem=item;
								 itemsSignMap.put(item[0],"1");						
							 }
						 }
						 else
						 {
							 html.append("<td class='RecordRow'  valign='middle' align='center'  height='"+td_height+"'    width='"+td_width+"'  >&nbsp;</td>");
						 }					 
					 }
					 else
					 {
						 html.append("<td class='RecordRow' id='"+point[0]+"'  valign='middle' align='left'  height='"+td_height+"'  ");
					     if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
					    	 html.append(" onclick='showDateSelectBox(this);'  onmouseout='javascript:Element.hide(\"date_panel\");'  ");
						 html.append(" >");	
						 html.append(point[1]);
						 if(point[5]==null|| "1".equals(point[5])|| "2".equals(point[5]))
								html.append("<font color='red'>*</font>");
						 html.append("&nbsp;&nbsp;&nbsp;"); 
						 html.append("</td>");
					 }
				 }
				 ArrayList a_list=getParticularPointList(pointInfoList,point);
				 html.append(getPointTD2((ArrayList)a_list.get(0),(String)a_list.get(1),temp,objectResultMap,status,a_pointmap,point[0],scoreflag,pointScore,point));
				 html.append("</tr>  \n ");
			 }
				/* 是否有了解程度 */
				String select_id=" ";			
			 html.append("</table>");

			 html.append("<span id=\"buttons\">");
			 if(dataArea.length()>0)   //避免空模版
				 html.append("<input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.save")+"\" onclick=\"check(1)\" class=\"mybutton\" style='margin-top:5px;'>&nbsp;");

			html.append("<input type=\"button\" name=\"b_refer\" value=\""+ResourceFactory.getProperty("reportcheck.return")+"\" onclick=\"goBack()\" class=\"mybutton\" style='margin-top:5px;'>");
			html.append("</span> ");
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		 }
		
		 list.add(html.toString());
		 list.add(personalComment.toString());
		 list.add(isNull);
		 list.add(scoreflag);
		 list.add(dataArea.toString());
		 list.add(NodeKnowDegree);
		 list.add(WholeEval);
		 list.add(limitation);
		 list.add(GradeClass);
		 list.add(String.valueOf(lay));
		 return list;
	}
	
	
	
	
	

	/**
	 * 按条件生成 <td>中的内容 
	 * @param tempList			指标标度值
	 * @param point_kind		要素类型 0:定性要点；1:定量要点
	 * @param temp				考核对象信息  id\姓名\状态
	 * @param objectResultMap	对象的考核结果
	 * @param status            权重分值标识 0：分值  1：权重
	 * @param pointMap          具有某人的指标权限map
	 * @param scoreflag			1:标度 2:混合
	 * @return
	 */
    public	String getPointTD2(ArrayList tempList,String point_kind,String[] temp,HashMap objectResultMap,String status,HashMap pointMap,String pointID,String scoreflag,String[] pointScore,String[] point) throws GeneralException
    {
    	StringBuffer   td=new StringBuffer("");

    	td.append("<td class='RecordRow' align='left'  ");
    	/*if(((String)pointMap.get(pointID)).equals("0"))
    		td.append(" style='display:none' ");
    	if(point_kind.equals("1")&&point[7]!=null&&point[7].equals("1"))
	    	 td.append(" style='display:none' ");
    	*/
    	td.append(" nowrap >");
    	td.append("<table border='0'><tr><td width='150' align='center' nowrap >");   
    	
    	String[] pointGrade=(String[])tempList.get(0);
    	String a_fieldItem=pointGrade[10];
    	String a_fieldItem_1=pointGrade[11];
    	if(a_fieldItem!=null&&a_fieldItem.trim().length()>0&& "0".equals(point_kind))
    	{
    		tempList=batchGradeBo.getFiltrateDate(tempList,a_fieldItem,temp[0],point_kind,a_fieldItem_1);
    	}
    	
    	if("1".equals(scoreflag)&& "0".equals(point_kind))
    	{
    		/*if(((String)pointMap.get(pointID)).equals("0"))
    		{
    			td.append("<input type='hidden' name='a");
    			td.append(temp[0]);
    			td.append("' value='null'  />");
    			
    		}
    		else*/
    		{
	    		td.append("<select name='a");
	    		td.append(temp[0]);
	    		td.append("'");   		
	    		/*if(temp[2].equals("2"))
	    			td.append(" disabled='false'");*/
	    		td.append("  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'    ><option value=''></option>");
	    		for(Iterator t=tempList.iterator();t.hasNext();)
	    		{
	    			String[] a_temp=(String[])t.next();
	    			td.append("<option value='");
	    			td.append(a_temp[5]);
	    			td.append("' ");   			
	    			if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
	    			{
	    				String[] values=(String[])objectResultMap.get(a_temp[1]);
	    				if(values!=null)
	    				{
		    				if(values[6]!=null&&values[6].equals(a_temp[5]))
		    					td.append("selected");
	    				}
	    			}  			
	    			td.append(" >&nbsp;&nbsp;");
	    			td.append(a_temp[4]);
	    			td.append("&nbsp;&nbsp;</option>");   			
	    		}
	    		td.append("</select>");  	
    		}
    	}
    	else
    	{
 
    		String[]  a_temp=(String[])tempList.get(0);
    		td.append(" <input  style='width: 65.0px' "); 
    		/*if(((String)pointMap.get(pointID)).equals("0"))
    		{
    			td.append(" type='hidden'");
    			td.append(" value='null' ");
    		}
    		else*/
    		{
    			
    				td.append(" type='text' ");
		    		if("1".equals(temp[2])|| "2".equals(temp[2])|| "3".equals(temp[2]))
					{
		    			String[] values=(String[])objectResultMap.get(a_temp[1]);   			
		    			if(values!=null&&(values[4]!=null||values[3]!=null))
		    			{			
			    			td.append(" value='");
			    			if("1".equals(status))
			    				td.append(values[3]);
			    			else
			    			{
			    				if(values[4]!=null)
			    					td.append(values[4]);
			    				else{
			    					if(values[3].indexOf(".")>0){
			    						values[3] = values[3].replaceAll("0+?$", "");
			    						values[3] = values[3].replaceAll("[.]$", "");
			    					}
			    						td.append(values[3]);
			    					
			    				}
			    					
			    			}
			    			
			    			td.append("'");
		    			}
					} 
    		}
    		td.append(" name='a"+temp[0]+"'    id='"+point[0]+"'      />");		
    		
    
    	}
    	td.append("</td><td width='40%' >");
    	
    	//if(status.equals("0")||scoreflag.equals("2"))
		{
			if("1".equals(point[2]))   //定量指标
			{
				
				td.append(" ( "+PubFunc.round(pointScore[3],0)+"~"+PubFunc.round(pointScore[2],0)+" ) ");
			}
			else					  //定性指标
			{
				if("2".equals(scoreflag))
					td.append(" ( "+ResourceFactory.getProperty("lable.performance.singleGrade.value")+":"+PubFunc.round(pointScore[1],0)+" ) ");			
			}
		}
		/*else
		{		
			if(scoreflag.equals("2"))
				td.append(" ( "+ResourceFactory.getProperty("lable.performance.singleGrade.value")+":"+PubFunc.round(pointScore[1],0)+" ) ");	
		}*/

    	td.append("</td></tr></table>    </td>");
    	return td.toString();
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * 得到某计划考核主体给对象的评分结果hashMap
	 * @param plan_id       考核计划id
	 * @param mainbodyID	考核主体id
	 * @param object_id		考核对象列表
	 * @return	HashMap
	 */
	public HashMap getPerTableXXX(String mainbodyID,String object_id)   throws GeneralException
	{
		HashMap hashMap=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			
					String objectid=object_id;
					HashMap map=new HashMap();
					String  sql="select * from zp_test_template  where a0100_1='"+mainbodyID+"' and a0100='"+objectid+"' ";
					if("1".equals(this.currentAndvance)){
						sql=sql+" and interview="+this.hireState;
					}
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String[] temp=new String[7];
						for(int i=0;i<7;i++)
						{
							temp[i]=rowSet.getString(i+1);
						}
						map.put(temp[5],temp);
					}
				
					hashMap.put(objectid,map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		return hashMap;
	}
	
	
	

	/**
	 * 返回  某绩效模版的所有指标集
	 * @param templateID
	 * @return
	 */
	public ArrayList getPerPointList(String templateID)  throws GeneralException
	{
		ArrayList  list=new ArrayList();		
		ArrayList  pointGrageList=new ArrayList();
		ArrayList  a_pointGrageList=new ArrayList();
		ArrayList  pointList=new ArrayList();	
		ContentDAO dao=new ContentDAO(this.conn);
		String    isNull="0";                    //判断模版中指标标度上下限值是否设置
		RowSet     rowSet=null;
		
		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(ppo.getComOrPer(templateID,"temp"))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		HashMap   map2=new HashMap();
		String     sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+templateID+"'  ";  //pi.seq,
		if("2".equals(this.fromType))
		{
			if(this.privPointStr.length()>0)
			{
				sql+=" and UPPER(pp.point_id) in ('"+this.privPointStr.replaceAll(",","','").toUpperCase()+"')";
			}
		}
		sql+=" order by pp.seq";
		try
		{
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[13];
				for(int i=0;i<13;i++)
				{
					if(i==2)
						temp[i]=Sql_switcher.readMemo(rowSet,"pointname");
					else if(i==4)
						temp[i]=Sql_switcher.readMemo(rowSet,"gradedesc");
					else
						temp[i]=rowSet.getString(i+1);
					if(i==6||i==7)
					{
						if(temp[i]==null)
						{
							isNull="1";
						}
					}
					
				}
				a_pointGrageList.add(temp);
			}			
			sql="select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"' ";	  //pi.seq,
			if("2".equals(this.fromType))
			{
				if(this.privPointStr.length()>0)
				{
					sql+=" and UPPER(po.point_id) in ('"+this.privPointStr.replaceAll(",", "','").toUpperCase()+"')";
				}
			}
			sql+="  order by pp.seq";
			rowSet =dao.search(sql);
			while(rowSet.next())
			{
				String[] temp=new String[8];
				temp[0]=rowSet.getString(1);
				temp[1]=Sql_switcher.readMemo(rowSet,"pointname");
				temp[2]=rowSet.getString(3);
				temp[3]=rowSet.getString(4);
				temp[4]="";
				temp[5]=rowSet.getString("visible");
				temp[6]=rowSet.getString("fielditem");
				temp[7]=rowSet.getString("status");
			//	pointList.add(temp);
				map2.put(temp[0].toLowerCase(),temp);
			}
			
			
			//解决排列顺序问题
			ArrayList seqList=new ArrayList();
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.conn);
			ArrayList apointList=new ArrayList();
			ArrayList layItemList=new ArrayList();
			HashMap itemPoint=new HashMap();
			//分析绩效考核模版
			parameterSetBo.anaylseTemplateTable(apointList,layItemList,itemPoint,templateID,this.privPointStr);
			for(int i=0;i<apointList.size();i++)
			{
				seqList.add(((String)apointList.get(i)).toLowerCase());
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				pointList.add((String[])map2.get(temp));
			}
			
			for(Iterator t=seqList.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				for(Iterator t1=a_pointGrageList.iterator();t1.hasNext();)
				{
					String[] tt=(String[])t1.next();
					if(tt[1].toLowerCase().equals(temp))
						pointGrageList.add(tt);
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		list.add(pointGrageList);
		list.add(pointList);
		list.add(isNull);
		
		return list;
	}
	public String anaysePrivPoint(String roleid,String flag)
	{
		SysPrivBo privbo=new SysPrivBo(roleid,flag,this.conn,"warnpriv");
		String res_str=privbo.getWarn_str();
		int res_type=Integer.parseInt("23");
		ResourceParser parser=new ResourceParser(res_str,res_type);
		/**1,2,3*/
		String str_content=parser.getContent();
		str_content=str_content+",";
		str_content=str_content.replace("R,", ",");
		return str_content;
	}
	//以下三个函数：isHaveMatchByModel，getByModel，getE01a1是为“是否按岗位素质模型测评”而写。  郭峰
	/**
	 * Description: 判断该岗位是否定义了指标
	 * @Version1.0 
	 * @param object_id
	 * @return
	 */
	public static boolean isHaveMatchByModel(String object_id,Connection con){
		boolean isHasPoint = false;
		try{
			RowSet rs = null;
		    ContentDAO dao = new ContentDAO(con);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar calendar = Calendar.getInstance();				
			String historyDate = sdf.format(calendar.getTime());
			StringBuffer sql = new StringBuffer("");
			sql.setLength(0);
			sql.append("select * from per_competency_modal where object_type='3' and object_id = (select "+Sql_switcher.isnull("e01a1", "null")+" from usra01 where a0100='"+object_id+"')");
			sql.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date");
			rs = dao.search(sql.toString());
			if(rs.next()){
				isHasPoint = true;
			}
			if(rs!=null)
	    		rs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return isHasPoint;
    }
	
	/**
	 * Description: 是否勾选了“按岗位素质模型测评”参数
	 * @Version1.0 
	 * @param planid
	 * @param con
	 * @return
	 */
	public static boolean getByModel(String planid,Connection con){
		boolean flag = false;
		String bymodel = null;
		RowSet rs = null;
		try
		{	
			String sql = "";
		    ContentDAO dao = new ContentDAO(con);
		    sql = "select bymodel from per_plan where plan_id = "+planid;
		   
		    rs = dao.search(sql);	
	    	if(rs.next())
	    	{	    		
	    		bymodel = String.valueOf(rs.getInt("bymodel"));//0 或 空 :不按岗位模型测评 1: 按岗位素质模型测评
	    	}		    			    										
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		if(bymodel!=null && "1".equals(bymodel) ){
			flag = true;
		 }
		return flag;
	}
	/**得到职位*/
	public String getE01a1(String object_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	String e01a1 = "";
    	try
    	{
    	RowSet rs0 = dao.search("select e01a1 from usra01 where a0100='"+object_id+"'");
    	if(rs0.next())
			e01a1 = rs0.getString(1);
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return e01a1;
    	
    }

	public BatchGradeBo getBatchGradeBo() {
		return batchGradeBo;
	}


	public void setBatchGradeBo(BatchGradeBo batchGradeBo) {
		this.batchGradeBo = batchGradeBo;
	}


	public String getShowOneMark() {
		return showOneMark;
	}


	public void setShowOneMark(String showOneMark) {
		this.showOneMark = showOneMark;
	}


	public HashMap getUserNumberPointResultMap() {
		return userNumberPointResultMap;
	}


	public void setUserNumberPointResultMap(HashMap userNumberPointResultMap) {
		this.userNumberPointResultMap = userNumberPointResultMap;
	}


	public String getPerformanceType() {
		return performanceType;
	}


	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}


	public String getFillctrl() {
		return fillctrl;
	}


	public void setFillctrl(String fillctrl) {
		this.fillctrl = fillctrl;
	}


	public String getSelfScoreInDirectLeader() {
		return selfScoreInDirectLeader;
	}


	public void setSelfScoreInDirectLeader(String selfScoreInDirectLeader) {
		this.selfScoreInDirectLeader = selfScoreInDirectLeader;
	}


	public String getNoGradeItem() {
		return noGradeItem;
	}


	public void setNoGradeItem(String noGradeItem) {
		this.noGradeItem = noGradeItem;
	}


	public boolean isShowObjectSelfScore() {
		return isShowObjectSelfScore;
	}


	public void setShowObjectSelfScore(boolean isShowObjectSelfScore) {
		this.isShowObjectSelfScore = isShowObjectSelfScore;
	}


	public String getOpt() {
		return opt;
	}


	public void setOpt(String opt) {
		this.opt = opt;
	}


	public RecordVo getPlanVo() {
		return planVo;
	}


	public void setPlanVo(RecordVo planVo) {
		this.planVo = planVo;
	}


	public String getFromModel() {
		return fromModel;
	}


	public void setFromModel(String fromModel) {
		this.fromModel = fromModel;
	}
	public boolean isShowObjectLowerScore() {
		return showObjectLowerScore;
	}
	public void setShowObjectLowerScore(boolean showObjectLowerScore) {
		this.showObjectLowerScore = showObjectLowerScore;
	}
	public void setIsHasSaveButton(String isHasSaveButton) {
		this.isHasSaveButton = isHasSaveButton;
	}
	public String getFillTableExplain() {
		return fillTableExplain;
	}
	public void setFillTableExplain(String fillTableExplain) {
		this.fillTableExplain = fillTableExplain;
	}

	public String getReturnflag() {
		return returnflag;
	}

	public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}
	
	
	
	
	
	
}
