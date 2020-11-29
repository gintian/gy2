package com.hjsj.hrms.businessobject.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:SingleCheckBo.java</p>
 * <p>Description>:单人考评/自我评价 新展现页面</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-05-31 下午03:56:27</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SingleCheckBo 
{
	
	private BatchGradeBo batchGradeBo = null;
	private Connection con = null;                 // 数据库连接
	private UserView userView = null;              // 登录用户
	private String plan_id = "";                   // 考核计划 ID
	private String userID = "";                    // 登录用户的 A0100  即考核主体 ID
	private String object_id = "";                 // 考核对象 ID	
	private RecordVo plan_vo = null;               // 考核计划信息vo
	private RecordVo template_vo = null;           // 考核模板信息vo
	private LoadXml loadxml = null;
	DecimalFormat myformat = new DecimalFormat("########.########"); //格式化小数
	private int td_width=100;                      // 行高
	private int td_height=60;                      // 行宽
	
	/**  表头信息参数  */
	private ArrayList headList = new ArrayList();  // 表头信息
	private boolean isShowObjectSelfScore = false; // 是否显示考核对象的自我评分 
	private String opt="0";  // 0:显示自我评分分值表 1：显示他人打分分值表
	private int total_width=0;  // 页面 table 的宽度
	
	/**  计划参数  */			
//	private int operateModel = 0;                    //1:单人打分 0：自我评价		
	private String showOneMark = "False";         // BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
		
	private String noPrivPointStr = "";            // 无权限的指标	
	private ArrayList templateItemList = new ArrayList();  // 模板项目记录
	private ArrayList leafItemList=new ArrayList();      //叶子项目列表
	private HashMap leafItemLinkMap = new HashMap();  //叶子项目对应的继承关系
	private int lay = 0;                           // 模板项目的层数
		
	private ArrayList pointList = new ArrayList();  // 叶子项目对应的指标集	
	private HashMap itemPointMap = new HashMap();  // 项目对应的指标map   
	private HashMap pointMap = new HashMap();      // 指标信息map 
	private HashMap itemToNumberMap = new HashMap();// 各项目拥有的节点数  
	private HashMap itemScaleMap = new HashMap();  // 项目对应的指标和指标标度map	
	private HashMap pointScaleMap = new HashMap();  // 指标对应的指标标度集	
	private HashMap pointmapPrv = new HashMap();   // 得到某考核对象的指标权限 map	
	private HashMap objectResultMap = new HashMap();   // 得到某计划考核主体给对象的评分结果hashMap	
	private HashMap userNumberResultMap = new HashMap();   // 得到考核对象拥有的考核指标	
	private ArrayList PointScaleList = new ArrayList();  // 指标标度集		
	
	/**  程序运行中参数  */	
	private HashMap allEvalResultMap = new HashMap();  //各考核主体给对象的评分结果
	private HashMap objectSelfCore = new HashMap();    //考核对象自我打分分值
	private boolean isLookObjectScore=false; //是否可以查看对象自评分
	private String fillctrl = "0";  // 必打分项
	private String    fromModel="menu";                // frontPanel 来自首页快捷评分面板进入  ,menu
	private HashMap objDynaRankMap = new HashMap(); // 动态指标权重
	String fillTableExplain = "";//clientname=gs   填表说明要用超链接的形式  国税用  郭峰
	private String isHasSaveButton = "0"; //国税用
	private String wholeEvalScore ="";
	private LazyDynaBean mainbodyBean=null;
	private String appitem_id = ""; 
	private ArrayList appContantList = new ArrayList();
	private boolean isProAppraise =false;
	
	public String getWholeEvalScore() {
		return wholeEvalScore;
	}

	public void setWholeEvalScore(String wholeEvalScore) {
		this.wholeEvalScore = wholeEvalScore;
	}
	public SingleCheckBo(Connection con,UserView userView,String plan_id,String object_id,String userID)
	{				
		this.con = con;
		this.userView = userView;
		this.plan_id=plan_id;
		this.object_id=object_id;			
		this.userID=userID;		
		this.plan_vo=getPlanVo(plan_id);
		this.template_vo=getTemplateVo(this.plan_vo.getString("template_id"));				
		init();
		batchGradeBo=new BatchGradeBo(this.con,plan_id);
		//判断考核计划是否定义了描述性评议项     陈旭光  2014-12-20
		PerEvaluationBo bo =new PerEvaluationBo(this.con, plan_id, "");  
		this.isProAppraise = bo.isProAppraise();
	}
	/**
	 *  初始化方法
	 */
	private void init()
	{		
		try
		{
			Hashtable htxml=new Hashtable();				
			if(BatchGradeBo.getPlanLoadXmlMap().get(this.plan_id)==null)
			{						
				this.loadxml = new LoadXml(this.con,this.plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(this.plan_id,this.loadxml);
			}
			else
			{
				this.loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(this.plan_id);
			}
			
			htxml=this.loadxml.getDegreeWhole();					    
			this.showOneMark = (String)htxml.get("ShowOneMark"); // BS打分时显示统一打分的指标，以便参考  默认为False
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	}
	
	/**
	 * 得到考核页面
	 * @return
	 */
	public ArrayList getSingleGradeHtml(String mainbodyType,String titleName,String weightScore,String object_status)
	{
//		this.operateModel=flag;
		ArrayList list_html=new ArrayList();
		try
		{	
			String bymodel=this.plan_vo.getString("bymodel");
			boolean flag=false;
			String e01a1="";
			if("1".equals(bymodel)){
				e01a1=getE01a1(object_id);
				flag=byModelEvaluation(e01a1);
			}
			if(flag){
				/**获得无权限的指标*/	
				this.noPrivPointStr = "";   
				this.lay=1;
				/**能力素质指标分类列表*/						
				this.templateItemList = getFieldSetList(e01a1);
				/**指标分类对应的指标map*/
				this.itemPointMap = getFieldItemPointList(e01a1);
				
				get_LeafItemListByModel();
				/**动态岗位素质模型模板对应的指标集合*/
				this.pointList = getFieldSetPointList(e01a1);
				/**指标对应的指标标度集*/
				this.pointScaleMap=getPointScaleMapByModel(e01a1);
				this.itemToNumberMap=getItemPointNumByModel();		
				this.headList = getHeadList(object_status);     // 获得表头信息
				this.mainbodyBean=getMainbodyBean(plan_id,object_id,userID);
				this.wholeEvalScore = (String)this.mainbodyBean.get("wholeEvalScore");
				list_html=getGradeHtml(titleName,weightScore,object_status);	// 得到考核页面 html
			}else{
				/**获得无权限的指标*/	
				this.noPrivPointStr = getNoPrivPointStr();   				 				
				/**模版项目列表*/						
				this.templateItemList = getTemplateItemList();
				/**项目对应的指标map*/
				this.itemPointMap = getItemPointList();
				/**得到项目中所有的叶子项目*/
				get_LeafItemList();
				/**模板对应的指标集*/
				this.pointList = getChildItemPointList();			
				/**项目的itemid对应的该项目的所有父亲，爷爷，太爷的列表*/
				this.leafItemLinkMap = getLeafItemLinkMap();
				/**指标对应的指标标度集*/
				this.pointScaleMap=getPointScaleMap();
				/**各项目拥有的节点数*/			
				this.itemToNumberMap=getItemPointNum();		
				
				
				this.headList = getHeadList(object_status);     // 获得表头信息
				this.mainbodyBean=getMainbodyBean(plan_id,object_id,userID);
				this.wholeEvalScore = (String)this.mainbodyBean.get("wholeEvalScore");
				list_html=getGradeHtml(titleName,weightScore,object_status);	// 得到考核页面 html
			}

								
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		
		return list_html;
	}	
	
	/**
	 * 获取考核对象所在岗位
	 * @param object_id
	 * @return
	 * @throws GeneralException
	 */
	public String getE01a1(String object_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(con);
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
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	return e01a1;
    	
    }
	/**
	 * 判断岗位是否存在有效地岗位素质模型指标,是否按岗位素质模型测评
	 * @param object_id
	 * @return
	 * @throws GeneralException
	 */
	public boolean byModelEvaluation(String object_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(con);
    	RowSet rowSet = null;
		boolean bool=false;
    	try
    	{
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		 Calendar calendar = Calendar.getInstance();				
		 String historyDate = sdf.format(calendar.getTime());
    	 String sql="select point_id from per_competency_modal where object_type='3' and object_id ='"+object_id+"' " +
    				" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date";
    	 rowSet=dao.search(sql);
    	 if(rowSet.next()){
     		bool=true;
    	 }

    	} catch (Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}

		return bool;
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
			
			ContentDAO dao = new ContentDAO(this.con);
			
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
	 * 得到考核页面 html
	 * @return
	 */	
	public ArrayList getGradeHtml(String titleName,String weightScore,String object_status) throws GeneralException
	{		
		String clientname = SystemConfig.getPropertyValue("clientName");
		ArrayList list=new ArrayList();
	    StringBuffer html=new StringBuffer("");	 
	    StringBuffer titleHtml=new StringBuffer(""); // 表头html  加以区分方便维护
	    StringBuffer bodyHtml=new StringBuffer("");  // 表体html  加以区分方便维护
	    StringBuffer tailHtml=new StringBuffer("");  // 表尾html  加以区分方便维护
		try
		{	
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();
			String NodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度
			String WholeEval=(String)htxml.get("WholeEval");			    //总体评价
			String WholeEvalMode=(String)htxml.get("WholeEvalMode");          //总体评价采集方式: 0-录入等级，1-录入分值
			if("False".equalsIgnoreCase(WholeEval))
				WholeEvalMode = "0";
			String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位	
			String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort"); // 显示总分
			String showNoMarking=(String)htxml.get("ShowNoMarking");  //是否显示不打分。
			String showIndicatorContent=(String)htxml.get("showIndicatorContent");  // 显示考核指标内容
			String showIndicatorDegree=(String)htxml.get("showIndicatorDegree");  // 显示考核指标标度说明
			String showIndicatorRole=(String)htxml.get("showIndicatorRole");  // 显示考核指标评分原则
			String isShowSubmittedScores=(String)htxml.get("isShowSubmittedScores"); //提交后的分数是否显示	
			String performanceType=(String) htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
			String DescriptiveWholeEval=(String)htxml.get("DescriptiveWholeEval"); //显示描述性总体评价，默认为 True	
			String DegreeShowType=(String)htxml.get("DegreeShowType"); //显示描述性总体评价，默认为 True	
			String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			
			int extendNum = 0;  //  了解程度 && 总体评价	 的rowspan	 				 
			if("True".equalsIgnoreCase(showIndicatorDegree))
				extendNum++;
			if("True".equalsIgnoreCase(showIndicatorRole))
			{
				extendNum++; extendNum++;
			}
			if("True".equalsIgnoreCase(showIndicatorContent))
				extendNum++;			
			if("True".equalsIgnoreCase(showDeductionCause))
				extendNum++;
			
			ArrayList contentList = new ArrayList();
			ArrayList descList = new ArrayList();
			if("1".equals(performanceType))
			{
				// 获取描述性评议项答案
				contentList = getProAppraiseContentList();
				// 获取描述性评议项
				descList = getProAppraiseDesc();
			}
			
			// 加标题 
			titleHtml.append("<tr><td class='RecordRow' valign='middle' align='center' height='50' colspan='"+this.headList.size()+"' ><br><font face=宋体 style='font-weight:bold;font-size:15pt'> "+titleName+" </font> </td></tr>");
						
			// 显示主集信息
			String[] temp={this.object_id," ",object_status};
			ArrayList objectList=new ArrayList();
			objectList.add(temp);
			if(this.batchGradeBo.getBasicFieldList().size()>0) //主集信息
			{				
				HashMap otherInfoMap=this.batchGradeBo.getOtherInfoMap(objectList);
				LazyDynaBean valueBean=null;
				if(otherInfoMap.get(this.object_id)!=null)
					valueBean=(LazyDynaBean)otherInfoMap.get(this.object_id);
				ArrayList basicFieldList=this.batchGradeBo.getBasicFieldList();
				LazyDynaBean cbean=null;
				 
				titleHtml.append("<tr><td width='100%' style='border:0px' colspan='"+(this.headList.size())+"' ><table width='100%' style='border-collapse:collapse;' >");
				 
				titleHtml.append("<tr>");
				for(int i=0;i<basicFieldList.size();i++)
			    {
					if(i!=0&&i%2==0)
					{
						titleHtml.append("</tr><tr>");
					}
			    	cbean=(LazyDynaBean)basicFieldList.get(i);
			    	String itemid=(String)cbean.get("item_id");    
					String context="&nbsp;";
			    	if(valueBean!=null&&valueBean.get(itemid)!=null)
			    		context+=(String)valueBean.get(itemid);
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
			    	titleHtml.append("<td class='"+_str+"' width='20%' valign='middle' align='center' >"+(String)cbean.get("itemdesc")+"</td>"); 
			    	titleHtml.append("<td class='"+_str2+"' width='30%' valign='middle' align='left' >"+context+"</td>"); 			    			
			    }
				if(basicFieldList.size()%2==1)
				{	
					if(basicFieldList.size()<3)
					{
						titleHtml.append("<td class='TableRow_lr' width='20%' valign='middle' align='center' >&nbsp;</td>");
						titleHtml.append("<td class='RecordRow_l' width='30%' valign='middle' align='left' >&nbsp;</td>"); 
					}
					else
					{	
						titleHtml.append("<td class='TableRow_lrt' width='20%' valign='middle' align='center' >&nbsp;</td>"); 
						titleHtml.append("<td class='RecordRow_lt' width='30%' valign='middle' align='left' >&nbsp;</td>"); 
					}
				}
				titleHtml.append("</tr></table>"); 				 
				titleHtml.append("</td></tr>");
			}
			
			
			// 输出表头			
			titleHtml.append("<tr class='trDeep' height='25' >");			
			LazyDynaBean abean=null;
			for(int k=0;k<this.headList.size();k++)
			{
				abean=(LazyDynaBean)this.headList.get(k);
			//	String itemid=(String)abean.get("itemid");     // 表头字段ID
				String itemdesc=(String)abean.get("itemdesc"); // 表头描述信息
				int itemWidth=((Integer)abean.get("itemWidth")).intValue(); // 表头各列宽度
								
				titleHtml.append("<td class='TableRow_2rows' valign='middle' align='center' nowrap width='"+itemWidth+"' >"+itemdesc+"</td>"); 												
			}			
			titleHtml.append("</tr>");
			
			
			// 输出表体	
			LazyDynaBean bean=null;
			HashMap existWriteItem=new HashMap();  // 放已画过的上级项目、上上级项目
		    //  循环所有指标
			for(int k=0;k<this.pointList.size();k++)
			{
				LazyDynaBean lbean = (LazyDynaBean)this.pointList.get(k);
				String item_id = (String)lbean.get("item_id");  // 项目ID
				String point_id = (String)lbean.get("point_id");  // 指标ID
				String pointname = (String)lbean.get("pointname");// 指标名称	
			//	String score = (String)lbean.get("score");     // 指标得分	
				String pointkind = (String)lbean.get("pointkind");// 指标类型 0：定性 1：定量	
				String visible = (String)lbean.get("visible");// 是否显示解释/标度 	=1显示解释 =2标度 其它不显示
				String status = (String)lbean.get("status");// 打分方式  =0（考核主体用对这个指标打分） =1（考核主体不用对这个指标打分） 默认值为1	
			//	String kh_content = (String)lbean.get("kh_content");// 考核内容	
			//	String gd_principle = (String)lbean.get("gd_principle");// 评分原则		
									
				ArrayList linkScaleList=(ArrayList)this.pointScaleMap.get(point_id); //  指标对应的指标标度
				if(("1".equals(DegreeShowType)|| "3".equals(DegreeShowType)) && (!"1".equalsIgnoreCase(pointkind)))// 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
				{
					//linkScaleList = getGradeDesc();
					linkScaleList = getGradeDescByPoint(point_id);//得到与指标标度关联的标准标度  2013.13.04 pjf
				}
				HashMap encloseWriteItem=new HashMap();  // 放已画过的和指标对应的除指标标度外的其它字段
				//  循环指标标度
				if(linkScaleList!=null && linkScaleList.size()>0)
				{
					for(int t=0;t<linkScaleList.size();t++)
				    {																								
						bodyHtml.append("<tr>");
						
						// 画表体列
						LazyDynaBean dbean=null;
						for(int x=0;x<this.headList.size();x++)
						{
							dbean=(LazyDynaBean)this.headList.get(x);
							String itemid=(String)dbean.get("itemid");     // 表头字段ID
							String itemdesc=(String)dbean.get("itemdesc"); // 表头描述信息
							int itemWidth=((Integer)dbean.get("itemWidth")).intValue(); // 表头各列宽度
							
							// 画指标的上级、上上级项目
							if("item_id".equalsIgnoreCase(itemid))
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");	
																								
								ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id); 
								int e=linkParentList.size()-1;
								int y=linkParentList.size();
								for(int n=0;n<this.lay;n++)
						    	{					
									if(e>=0)
							    	{
										bean=(LazyDynaBean)linkParentList.get(e);
								    	String itemId=(String)bean.get("item_id");
							    		if(existWriteItem.get(itemId)!=null)
								    	{
							    			e--;
							    			y--;
							    			continue;
							    		}
							    		existWriteItem.put(itemId,"1");
							    		String itemDesc=(String)bean.get("itemdesc");
							    		//画出一个父项目	
							    		int rowspan=((this.itemToNumberMap.get(itemId)==null?0:((Integer)this.itemToNumberMap.get(itemId)).intValue()));
							    		if("1".equals(DegreeShowType) || "3".equals(DegreeShowType))
							    		{
							    		//	rowspan = ((ArrayList)this.itemPointMap.get(item_id)).size()*linkScaleList.size();
							    		}
							    		bodyHtml.append(writeTd(itemId,itemDesc,"center",rowspan,td_width));			    					    					    		
							    		e--;			    					    			
									}
									y--;
									if(y<0)
									{
										//  画空项目																
										bodyHtml.append(writeTd("","","center",linkScaleList.size(),td_width));											
									}
						    	}
							// 画指标  " kh_content;scaleExplain;gd_principle;mainbody_scoreExplain;self_scoreExplain;scoreReason "
							}else if("point_id".equalsIgnoreCase(itemid))
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");
								
								if(point_id.indexOf("xxx")!=-1)
								{
									//  画空指标	
									bodyHtml.append(writeTd("",pointname,"left",0,td_width+150));
									bodyHtml.append("</tr>");
									continue;
								}else
								{					
									//  画指标	
									bodyHtml.append(writeTd("",pointname,"left",linkScaleList.size(),td_width+150));
								}
							// 显示标度说明
							}else if("scaleExplain".equalsIgnoreCase(itemid))
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");								
								bodyHtml.append(writeScaleExplain(point_id,itemdesc,pointkind,status,visible,linkScaleList.size(),itemWidth));	
									
							// 显示下级主体评分 	
							}else if("mainbody_score".equalsIgnoreCase(itemid))
							{
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");								
								bodyHtml.append(getLowerMainbodyPonitTd(pointkind,point_id,status,linkScaleList.size()));
								
							// 显示下级主体评分说明 	
							}else if("mainbody_scoreExplain".equalsIgnoreCase(itemid))
							{
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");								
											
							// 显示自我评分	
							}else if("self_score".equalsIgnoreCase(itemid))
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");																								
								bodyHtml.append(getSelfScorePonitTd(pointkind,weightScore,point_id,status,linkScaleList.size()));
								
							// 显示自我评分说明
							}else if("self_scoreExplain".equalsIgnoreCase(itemid))
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");																
//								bodyHtml.append(getSelfScoreExplainTd(point_id,linkScaleList.size()));	
								
							// 评分
							}else if("pointGrade".equalsIgnoreCase(itemid))
							{																																				
								LazyDynaBean zbean = (LazyDynaBean)linkScaleList.get(t);
								String gradedesc = (String)zbean.get("gradedesc");// 指标标度描述	
								String gradecode = (String)zbean.get("gradecode");// 指标标度代码	
								String gradevalue = (String)zbean.get("gradevalue");// 
								String top_value = (String)zbean.get("top_value");// 
							//	String bottom_value = (String)zbean.get("bottom_value");// 
								String point_score = (String)zbean.get("score");// 
								if("1".equals(DegreeShowType) || "3".equals(DegreeShowType))
								{
									 gradecode = (String)zbean.get("grade_template_id");// 指标标度代码	
								//	String bottom_value = (String)zbean.get("bottom_value");// 
								}
									
								bodyHtml.append(writePointGrade(point_id,pointkind,object_status,status,gradecode,gradedesc,gradevalue,point_score,top_value));
								
							// 评分说明
							}else if("scoreReason".equalsIgnoreCase(itemid))
							{
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");								
								bodyHtml.append(writeScoreReason(point_id,linkScaleList.size(),itemWidth));
									
							// 其它列	
							}else
							{	
								if(encloseWriteItem.get(itemid)!=null)
						    	{			    			
					    			continue;
					    		}
								encloseWriteItem.put(itemid,"1");								
								String context=(String)lbean.get(itemid);
								if(context!=null)
								bodyHtml.append(writeTd("",context.replaceAll("\r\n","<br>"),"left",linkScaleList.size(),itemWidth));		
								
							/*	
								bodyHtml.append("<td class='RecordRow' align='left' width='"+itemWidth+"' ");	
								if((linkScaleList.size())!=0)
									bodyHtml.append(" rowspan='"+(linkScaleList.size())+"' ");		
								bodyHtml.append(" >");	
								bodyHtml.append(context.replaceAll("\r\n","<br>"));									
							//	bodyHtml.append(" <table width='100%' ><tr><td>"+context.replaceAll("\r\n","<br>")+"</td></tr></table>");																
								bodyHtml.append("</td>");	
							*/						
							}												
						}						
						bodyHtml.append("</tr>");									
				    }
				}
			/*	else
				{
					bodyHtml.append("<tr>");
					for(int x=0;x<this.headList.size();x++)
					{
						bodyHtml.append("<td class='RecordRow' align='left' ");							
						bodyHtml.append(" >");	
						bodyHtml.append("");							 
						bodyHtml.append("</td>");
					}					
					bodyHtml.append("</tr>");					
				}	
			*/			
			}
			
			
			// 描述性评议项
			if("1".equals(performanceType))
			{
				tailHtml.append(getProAppraise(this.lay+extendNum+1,object_status,descList));				
			}						
			/* 是否有了解程度 */
			String select_id=" ";
			if("true".equals(NodeKnowDegree))
			{
				if(this.objectResultMap!=null && this.objectResultMap.get("know_id")!=null)
					select_id=(String)this.objectResultMap.get("know_id");
				tailHtml.append(getExtendTd(select_id,this.lay+extendNum+1,ResourceFactory.getProperty("lable.statistic.knowdegree"),"konwDegree",object_status));
			}
			/* 是否有总体评价 */
			select_id=" ";
			if("true".equals(WholeEval) || "True".equalsIgnoreCase(DescriptiveWholeEval))
			{
				if("0".equals(WholeEvalMode)){
					if(this.objectResultMap!=null && this.objectResultMap.get("whole_grade_id")!=null)
						select_id=(String)this.objectResultMap.get("whole_grade_id");
						tailHtml.append(getExtendTd2(select_id,this.lay+extendNum+1,ResourceFactory.getProperty("lable.statistic.wholeeven"),"wholeEval",object_status));
				}
				else if("1".equals(WholeEvalMode)){
					tailHtml.append(" <tr> <td class='RecordRow' align='center'  colspan='"+(lay+extendNum+1)+"'    nowrap > ");
					tailHtml.append(ResourceFactory.getProperty("lable.statistic.wholeeven"));
					tailHtml.append("</td>");
		            htxml=this.loadxml.getDegreeWhole();
		            String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
		        //  String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		            ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
		            if("True".equalsIgnoreCase(allowSeeLowerGrade))
		                lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
		            if(this.isShowObjectSelfScore)
		                allowSeeLowerGrade="false";
					if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
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
		                    tailHtml.append("<td  class='RecordRow' align='center' >");
		                    tailHtml.append(whole_str);
		                    tailHtml.append("&nbsp;</td>");
		                    if("True".equalsIgnoreCase(showDeductionCause))
		                    {
		                        tailHtml.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
		                    }               
		                }
		            }
					String tempScore = PubFunc.round(wholeEvalScore, Integer.parseInt(KeepDecimal));
					//String tempScore = wholeEvalScore;
					String tempstatus = (String)this.mainbodyBean.get("status");
                	if("0".equals(tempstatus))
                		tempScore = "";
					tailHtml.append("<td vAlign='bottom' class='RecordRow' align='left'>&nbsp;&nbsp;&nbsp;<input type='text' id='wholeEvalScoreId' value='"+tempScore+"'  size=10 name='wholeEvalScore' ");
					if("2".equals(object_status) || "4".equals(object_status) || "7".equals(object_status)){
						tailHtml.append(" disabled ");
					}
					tailHtml.append(" />");
					if(this.isLookObjectScore){
						if(((!"1".equals(performanceType) || ("1".equals(performanceType) && !this.isProAppraise)) && "True".equalsIgnoreCase(DescriptiveWholeEval)) || "1".equals(opt))
			    		{
			    			if("1".equals(opt))
			    				tailHtml.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\" onclick=\"javascript:showWindow2('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID))+"')\" > ");
			    			else
			    				tailHtml.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\" onclick=\"javascript:showWindow2('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"')\" > ");
			    		}
					}
					if(!this.isShowObjectSelfScore){
						tailHtml.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\" onclick=\"javascript:showWindow('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID))+"')\" > ");
					}
					tailHtml.append("</td>");
					tailHtml.append(" </tr>");
				}
			}
			//不打分原因
			if(showNoMarking!=null && "true".equalsIgnoreCase(showNoMarking))
			{
				tailHtml.append(getNotMarkTd(this.lay+extendNum+1,object_status));				
			}
			//建议和意见
			if("1".equals(performanceType) && (descList==null || descList.size()<=0))
			{
				tailHtml.append(getNotMarkTd2(this.lay+extendNum+1,object_status));				
			}
			//总分功能
			if("true".equalsIgnoreCase(isShowTotalScore) && (("2".equals(object_status) && "true".equalsIgnoreCase(isShowSubmittedScores)) || !"2".equals(object_status)))
			{				
				tailHtml.append(writeTotalScore(this.lay+extendNum+1));
			}			
			
			html.append(titleHtml.toString());
			html.append(bodyHtml.toString());
			html.append(tailHtml.toString());
			html.append("</table>");
			
			//  按钮 
			
			if(!this.isShowObjectSelfScore)
			{
				if("gs".equalsIgnoreCase(clientname)){///如果是广东国税    只出现一个保存和提交按钮
					 if(!"2".equals(object_status) && !"7".equals(object_status) &&!"4".equals(object_status) ){
						 this.setIsHasSaveButton("1");
					 }
					 //处理填表说明这个超链接
					 RecordVo planVo=getPlanVo(plan_id);
					 if(planVo.getString("descript")!=null&&planVo.getString("descript").length()>0)
						 this.setFillTableExplain("<a href='javascript:planDescript(\""+PubFunc.encryption(plan_id)+"\")'>"+"填表说明"+"</a>");
				 }else{//如果不是国税
					html.append("<table border='0' cellspacing='0' align='left' cellpadding='0'>");
					html.append("<tr>");
					html.append("<td id=\"buttons\" align='left' style='height:35px'>");				 
					 
				//	html.append("<br>");
					if(!"2".equals(object_status)&&!"7".equals(object_status))
					{
						html.append("<span id=\"buttons\"  ");
						if("4".equals(object_status))
							html.append("style='display:none'");
						html.append(" ><input type=\"button\" name=\"b_save\" value=\""+ResourceFactory.getProperty("button.save")+"\" onclick=\"check(1)\" class=\"mybutton\">");
						html.append("&nbsp;<input type=\"button\" name=\"b_refer\" value=\""+ResourceFactory.getProperty("lable.welcomeinv.sumbit")+"\" onclick=\"check(2)\" class=\"mybutton\"></span> ");
					}
					html.append("</td><td align='left' style='height:35px'>");
					
					RecordVo planVo=getPlanVo(plan_id);
					if(!"hkyh".equalsIgnoreCase(clientname)) { // 2013.11.09 pjf
						if(planVo.getString("descript")!=null&&planVo.getString("descript").length()>0)
							html.append("&nbsp;<input type=\"button\" name=\"b_descript\" value=\""+ResourceFactory.getProperty("lable.performance.fillDeclare")+"\" onclick=\"planDescript('"+PubFunc.encryption(plan_id)+"')\" class=\"mybutton\">");
					}
					if("frontPanel".equalsIgnoreCase(fromModel))
						html.append("&nbsp;<input type=\"button\" name=\"b_close\" value=\""+ResourceFactory.getProperty("button.return")+"\" onclick=\"goback()\" class=\"mybutton\">");
					
					html.append("</td></tr></table>");
				}
				
			}			
			if("gs".equalsIgnoreCase(clientname)){//如果是国税
				list.add("<table id='"+plan_id+object_id+"table1' class='ListTable_self3' width='"+total_width+"' >"+html.toString());
			}else{
				list.add("<table class='ListTable_self3' width='"+total_width+"' >"+html.toString());
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
	* 画单元格	
	* @return
	*/
	private String writeTd(String itemid,String context,String align,int rowspan,int width)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle' align='"+align+"'");
		if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");
		else
			td.append(" height='"+td_height+"' ");
		td.append(" width='"+width+"'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}
	/**
	* 画标度说明 	
	* @return
	*/
	private String writeScaleExplain(String point_id,String context,String pointkind,String status,String visible,int rowspan,int width)
	{
		StringBuffer td=new StringBuffer("");
		td.append("<td class='RecordRow' id='a"+point_id+"' valign='middle' align='center' width='"+width+"' ");
		if(rowspan!=0)															
			td.append(" rowspan='"+rowspan+"' ");
		else
			td.append(" height='"+td_height+"' ");
		if("1".equals(pointkind) && status!=null && "1".equals(status) && "false".equalsIgnoreCase(this.showOneMark))
			td.append("</td>");
		else
		{
			if(!this.isShowObjectSelfScore)
		    {
			    if(visible==null || "1".equals(visible) || "2".equals(visible))
			    {
			    	if(visible==null || "2".equals(visible))
			    		td.append(" onclick='showDateSelectBox2(this);' onmouseout='Element.hide(\"date_panel\");' ");
			    	else
			    		td.append(" onclick='showDateSelectBox(this);' onmouseout='Element.hide(\"date_panel\");' ");
			    }
		    }						
			td.append(" >");
			if(visible==null || "1".equals(visible) || "2".equals(visible))
				td.append(" <font color='#2E67B9' >"+context+"</font></td>");
			else
				td.append("</td>");
		}
		return td.toString();
	}
	/**
	* 画评分	即指标标度
	* @return
	*/
	private String writePointGrade(String point_id,String pointkind,String object_status,String status,String gradecode,String gradedesc,String gradevalue,String point_score,String top_value)
	{
		StringBuffer td=new StringBuffer("");		
		td.append("<td class='RecordRow' valign='middle' align='left' nowrap height='"+td_height+"' >");
		
		Hashtable htxml=new Hashtable();							
		htxml=this.loadxml.getDegreeWhole();	
		String isShowSubmittedScores=(String)htxml.get("isShowSubmittedScores"); //提交后的分数是否显示		
		String BlankScoreOption=(String)htxml.get("BlankScoreOption");		//指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数		
		String BlankScoreUseDegree=(String)htxml.get("BlankScoreUseDegree");  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…			
		HashMap pointMaxValueMap = new HashMap();
		if("1".equals(BlankScoreOption))
			 pointMaxValueMap=getMaxPointValue();      //指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理	
		
		if("1".equalsIgnoreCase(pointkind))
		{									
			td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input style='width: 65.0px' onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' "); 
    		if("0".equals((String)this.pointmapPrv.get(point_id)))
    		{
    			td.append(" type='hidden'");
    			td.append(" value='null' ");
    		}
    		else
    		{
    			if("1".equals(pointkind) && status!=null && "1".equals(status) && "false".equalsIgnoreCase(this.showOneMark))
    				td.append(" type='hidden' ");
    			else
    				td.append(" type='text' ");
	    		if("2".equals(object_status) || "4".equals(object_status) || "7".equals(object_status) || ("1".equals(pointkind) && "true".equalsIgnoreCase(this.showOneMark) && status!=null && "1".equals(status)))
	    			td.append(" disabled='false'");
	    		if(!("false".equals(isShowSubmittedScores) && "2".equals(object_status)))
    			{
		    		if("1".equals(pointkind) && status!=null && "1".equals(status) && "true".equalsIgnoreCase(this.showOneMark))
		    		{
		    			if(this.userNumberResultMap!=null)
		    				td.append(" value='"+(String)this.userNumberResultMap.get(point_id)+"' ");
		    		}
		    		else
		    		{
			    		if("1".equals(object_status) || "2".equals(object_status) || "3".equals(object_status))
						{
			    			String[] values=(String[])this.objectResultMap.get(point_id);   			
			    			if(values!=null&&(values[4]!=null||values[3]!=null))
			    			{			
			    				td.append(" value='");
				    			if("1".equals(status))
				    			{
				    				if("0".equals(pointkind))
				    					td.append(values[3]!=null?values[3]:"");
				    				else
				    					td.append(values[4]!=null?values[4]:"");			    				
				    			}
				    			else
				    			{
				    				if(values[4]!=null && "1".equals(pointkind))
				    					td.append(values[4]);
				    				else if(values[3]!=null && "0".equals(pointkind))
				    					td.append(values[3]);
				    			}
				    			
				    			td.append("'");
			    			}
						} 
			    		else
			    		{
			    			if(!"4".equals(object_status) && !"7".equals(object_status))
			    			{
			    				if("1".equals(BlankScoreOption))
			    				{									    				
			    					LazyDynaBean ybean=(LazyDynaBean)pointMaxValueMap.get(point_id);
			    					if("0".equals(pointkind))
			    					{
			    						td.append(" value='"+PubFunc.multiple((String)ybean.get("score"),(String)ybean.get("gradevalue"),1)+"' ");
			    					}
			    					else
			    					{
			    						td.append(" value='"+(String)ybean.get("top_value")+"' ");
			    					}
			    				}
			    				else if("2".equals(BlankScoreOption))
			    				{									    					
			    					if(gradecode.equalsIgnoreCase(BlankScoreUseDegree))
			    					{
			    						if("0".equals(pointkind))
			    						{
			    							String d_value= PubFunc.round(String.valueOf(Float
													.parseFloat(gradevalue)
													* Float.parseFloat(point_score)), 1);
			    							td.append(" value='"+d_value+"' ");
			    						}
			    						else
			    						{
			    							td.append(" value='"+top_value+"' ");
			    						}
			    					}														 
			    			 	}
			    			}
			    		}
		    		}
    			}
    		}
    		td.append(" name='"+point_id+"' />");	
			
		}else
		{									
//			td.append("<input type='radio' name='"+point_id+"' value='"+gradecode+"'");
//			td.append(" />&nbsp;");
//			td.append(gradedesc);	
			String clientname = SystemConfig.getPropertyValue("clientName");
			if("gs".equalsIgnoreCase(clientname)){//如果是国税
				td.append("<input type='radio' name='"+this.plan_id+this.object_id+point_id+"' value='"+gradecode+"'  ");
			}else{
				td.append("<input type='radio' name='"+point_id+"' value='"+gradecode+"'  ");
			}
			if("2".equals(object_status) || "4".equals(object_status) || "7".equals(object_status))
		    	td.append(" disabled='true' ");

			// 提交后的分数是否显示
	    	if(!("false".equals(isShowSubmittedScores) && "2".equals(object_status)))
	    	{
		    	if("1".equals(object_status) || "2".equals(object_status) || "3".equals(object_status))
		    	{
		    		String[] values=(String[])objectResultMap.get(point_id);
		    		if(values!=null)
		    		{
			    		if(values[6]!=null && values[6].equals(gradecode)) 
			    			td.append("checked");   
		    		}
		    	}else
		    	{
		    		if(!"4".equals(object_status) && !"7".equals(object_status))
		    		{
			    		if("1".equals(BlankScoreOption))
						{
				    		LazyDynaBean abean=(LazyDynaBean)pointMaxValueMap.get(point_id);
				    		String defaultValue=(String)abean.get("gradecode");
				    		if(defaultValue!=null)
					    	{
				    			if(defaultValue.equals(gradecode)) 
				    				td.append("checked"); 	  
					    	} 
						}
				    	else if("2".equals(BlankScoreOption))
				    	{
							if(BlankScoreUseDegree!=null)
					    	{
								if(BlankScoreUseDegree.equals(gradecode)) 
									td.append("checked"); 		  
					    	}
				    	}
		    		}
		    	} 
	    	}
			td.append(" />&nbsp;");
			td.append(gradedesc);			
		}	
		td.append("</td>");
		
		return td.toString();
	}
	/**
	*  画评分说明	
	* @return
	*/
	private String writeScoreReason(String point_id,int rowspan,int width)
	{
		StringBuffer td=new StringBuffer("");							
		
		if(("0".equals((String)this.pointmapPrv.get(point_id))))
    	{
			td.append("<td class='RecordRow' valign='middle' align='center' ");	
			if(rowspan!=0)
				td.append(" rowspan='"+(rowspan)+"' ");			
			td.append(" width='"+width+"'");
			td.append(" >");
			td.append("&nbsp;");
			td.append("</td>");
    	}
		else
		{
			String[] values=null;
			String reasons="";
			String allreasons="";
			if(this.objectResultMap!=null && this.objectResultMap.get(point_id)!=null)	
			{
				values=(String[])this.objectResultMap.get(point_id);
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
			String valign="top";
			if(reasons.trim().length()==0)
				valign="middle";
			 
			if("1".equals(opt) || isShowObjectSelfScore)
			{
				td.append("<td class='RecordRow' valign='"+valign+"' align='left' ");			 
				if(rowspan!=0)
					td.append(" rowspan='"+(rowspan)+"' ");				
				td.append(" width='"+width+"'");
				td.append(" >");
				td.append(" <table width='100%' ><tr><td id='r_"+point_id+"' title=\""+allreasons+"\">"+reasons+"</td></tr><tr><td align='center'  ><img title='填写评分说明' onclick='scoreReason(\""+this.plan_id+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"\",\""+point_id+"\",\"1\")' src='/images/readwrite_obj.gif' border=0></td></tr></table></td>");
				
			}else
			{
				td.append("<td class='RecordRow' valign='"+valign+"' align='left' ");
				if(rowspan!=0)
					td.append(" rowspan='"+(rowspan)+"' ");				
				td.append(" width='"+width+"'");
				td.append(" >");
				td.append(" <table width='100%' ><tr><td id='r_"+point_id+"' title=\""+allreasons+"\">"+reasons+"</td></tr><tr><td align='center'  ><img title='填写评分说明' onclick='scoreReason(\""+this.plan_id+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"\",\""+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID))+"\",\""+point_id+"\",\""+this.opt+"\")' src='/images/readwrite_obj.gif' border=0></td></tr></table></td>");
			}
		}
				
		return td.toString();
	}
	/** 显示下级主体评分及评分说明
	 * @param point_kind
	 * @param pointID
	 * @param status
	 * @return
	 */
	public String getLowerMainbodyPonitTd(String point_kind,String pointID,String status,int rowspan)
	{
		StringBuffer td=new StringBuffer("");	   
		Hashtable htxml=new Hashtable();							
		htxml=this.loadxml.getDegreeWhole();	
		String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		String scoreflag=(String)htxml.get("scoreflag");	   //=2混合，=1标度
		String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
		String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
		ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
		if("True".equalsIgnoreCase(allowSeeLowerGrade))
		    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
		
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
	    	td.append("<td class='RecordRow' align='center' ");   //下属评分列居中   2013.12.10 pjf
	    	if(rowspan!=0)
				td.append(" rowspan='"+(rowspan)+"' ");	
	    	td.append(" nowrap >");
	    	td.append("<table border='0' ");
		    td.append(" ><tr><td align='center' nowrap >");   
		    		    
		    if("1".equals(scoreflag) && "0".equals(point_kind))
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
		    	if(valueBean==null|| "0".equals((String)valueBean.get("score")))
		    	{
		    		td.append("");
		    	}
		    	else
		    	{
		    		if("1".equals(point_kind) && status!=null && "1".equals(status) && "false".equalsIgnoreCase(this.showOneMark))
		    		    td.append("  ");
		    		else
		    		{
				    	if("1".equals(point_kind) && status!=null && "1".equals(status) && "true".equalsIgnoreCase(this.showOneMark))
				    	{
				    		if(userNumberResultMap!=null && userNumberResultMap.get(pointID)!=null)
				    			td.append((String)userNumberResultMap.get(pointID));
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
		    td.append("</td></tr></table>");	    		    	
	    	td.append("</td>");	 
	    	
	    	if("true".equalsIgnoreCase(showDeductionCause))
	    	{
	    		String reasons="";	    		
	    		if(valueBean!=null)
	    			reasons=(String)valueBean.get("reasons");
				reasons=reasons.replaceAll("<br>","\r\n");
				reasons=reasons.replaceAll(" ","&nbsp;");
				String  valign="top";
				if(reasons.trim().length()==0)
				valign="middle";
				td.append("<td class='RecordRow' valign='"+valign+"' align='left' width='"+(td_width+50)+"' ");
				if(rowspan!=0)
					td.append(" rowspan='"+(rowspan)+"' ");					  
				td.append(" >&nbsp;&nbsp;");	    
				td.append("<table width='100%' ><tr><td >"+reasons+"</td></tr></table></td>");
			}
		}
    	return td.toString();
	}
	/** 显示自我评分
	 * @param point_kind
	 * @param pointID
	 * @param status
	 * @return
	 */
	public String getSelfScorePonitTd(String point_kind,String weightScore,String pointID,String status,int rowspan)
	{
		StringBuffer td=new StringBuffer("");
		Hashtable htxml=new Hashtable();							
		htxml=this.loadxml.getDegreeWhole();	
		String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		String scoreflag=(String)htxml.get("scoreflag");	   //=2混合，=1标度
		String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
		
	    td.append("<td class='RecordRow' align='left'  ");
	    if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");	
	    boolean isValue = true;
	    //能力素质没有指标权限  2013.12.4 pjf
	    if(pointmapPrv==null || "".equals(pointmapPrv) || pointmapPrv.size()==0 || "0".equals((String)pointmapPrv.get(pointID)))
	    	isValue=false;
	    if("1".equals(point_kind) && status!=null && "1".equals(status) && "false".equalsIgnoreCase(this.showOneMark))
		    isValue=false;	   	
	    td.append(" nowrap >&nbsp;&nbsp;");	    		    	
		td.append("<table border='0' ");		
		td.append(" ><tr><td  align='center' nowrap >");   
		
		HashMap userNumberResultMap=(HashMap)this.getUserNumberPointResultMap().get(this.object_id);		    	
		if("1".equals(scoreflag) && "0".equals(point_kind))
		{
			//能力素质没有指标权限  2013.12.4 pjf
			if(pointmapPrv==null || "".equals(pointmapPrv) || pointmapPrv.size()==0 || "0".equals((String)pointmapPrv.get(pointID)))
		    {
		    	td.append("");
		    }
		    else
		    {		    	
				for(int t=0;t<this.PointScaleList.size();t++)
				{
					LazyDynaBean zbean = (LazyDynaBean)this.PointScaleList.get(t);
					String point_id = (String)zbean.get("point_id");// 指标id	
					String gradedesc = (String)zbean.get("gradedesc");// 指标标度描述	
					String gradecode = (String)zbean.get("gradecode");// 指标标度代码						
						
					if(point_id.equalsIgnoreCase(pointID))
					{
						if(this.objectSelfCore.get(point_id)!=null)
					    {
						    String[] values=(String[])this.objectSelfCore.get(point_id);
						    if(values!=null)
						    {
							    if(values[6]!=null && values[6].equals(gradecode))
							    {
							    	if("1".equals(DegreeShowType) || "3".equals(DegreeShowType))
									    td.append(gradedesc);
							    	else if("2".equals(DegreeShowType))
									    td.append(gradedesc);					    				
							    }
						    }
					    }
					}
				}							   
		    }
		}else
		{		 		    	
		    if("0".equals((String)pointmapPrv.get(pointID)))
		    {
		    	td.append("");   		
		    }
		    else
		    {
		    	if("1".equals(point_kind) && status!=null && "1".equals(status) && "false".equalsIgnoreCase(this.showOneMark))
		    		td.append("  ");
		    	else
		    	{
				    if("1".equals(point_kind) && status!=null && "1".equals(status) && "true".equalsIgnoreCase(this.showOneMark))
				    {
				    	if(userNumberResultMap!=null&&userNumberResultMap.get(pointID)!=null)
				    		td.append((String)userNumberResultMap.get(pointID));
				    }
				    else
				    {
				    	if(this.objectSelfCore.get(pointID)!=null)
				    	{
					    	String[] values=(String[])this.objectSelfCore.get(pointID);   			
					    	if(values!=null && (values[4]!=null || values[3]!=null))
					    	{									    			
						    	if("1".equals(status))
						    	{
						    		if("0".equals(point_kind))
						    			td.append(values[3]!=null?myformat.format(new Double(values[3])):"");
						    		else
						    			td.append(values[4]!=null?myformat.format(new Double(values[4])):"");			    				
						    	}
						    	else
						    	{
						    		if(values[4]!=null && "1".equals(point_kind))
						    			td.append(myformat.format(new Double(values[4])));
						    		else if(values[3]!=null && "0".equals(point_kind))
						    			td.append(myformat.format(new Double(values[3])));
						    	}
					    	}
				    	}							
				    }
		    	}			    		
		    }		    
		}
		td.append("</td></tr></table>");	    		    	
	    td.append("</td>");
	    
	    if("true".equalsIgnoreCase(showDeductionCause) && !this.isShowObjectSelfScore)
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
			td.append("<td class='RecordRow' valign='"+valign+"' align='left' width='"+(td_width+50)+"' ");
			if(rowspan!=0)
				td.append(" rowspan='"+(rowspan)+"' ");	
			td.append(" >&nbsp;&nbsp;");	
			td.append(" <table width='100%' ><tr><td >"+reasons+"</td></tr></table></td> ");
    	}
	    
	    return td.toString();
	}
	/** 显示自我评分说明
	 * @return
	 */
/*	public String getSelfScoreExplainTd(String pointID,int rowspan)
	{
		StringBuffer td=new StringBuffer("");
		String reasons="";
    	String[] values=(String[])this.objectSelfCore.get(pointID);
    	if(values!=null&&values.length>=8)
    		reasons=values[7];
		reasons=reasons.replaceAll("<br>","\r\n");
		reasons=reasons.replaceAll(" ","&nbsp;");
			 
		String valign="top";
		if(reasons.trim().length()==0)
			valign="middle";
		td.append("<td class='RecordRow' valign='"+valign+"' align='left' width='"+(td_width+50)+"' ");
		if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");	
		td.append(" >&nbsp;&nbsp;");	
		td.append(" <table width='100%' ><tr><td >"+reasons+"</td></tr></table></td> ");
		return td.toString();
	}
*/	
	
	// 描述性评议项
	public String getProAppraise(int lay,String status,ArrayList descList)
	{		
		StringBuffer tr = new StringBuffer("");
		
		for(int i=0;i<descList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)descList.get(i);
			String id = (String)bean.get("id");
			String value = (String)bean.get("value");
			
			tr.append("<tr>");
			tr.append("<td class='RecordRow' align='left' colspan='"+(lay+1)+"' nowrap >");
			tr.append(value);
			tr.append("</td>");
			tr.append("</tr>");
			
			tr.append("<tr>");
			tr.append("<td class='RecordRow' colspan='"+(lay+1)+"' nowrap >");
		//	tr.append("<table border='0' cellspacing='0' width='100%' cellpadding='0' align='center'>");
		//	tr.append("<tr class='trDeep1'>");
		//	tr.append("<td>");
		//	tr.append("<html:textarea name='examPlanForm' styleId='addDescription' property='addDescription' cols='90' rows='6'> </html:textarea>");
			tr.append("<textarea id=\"appItemid"+id+"\" name=\"appItemdesc"+id+"\" cols='90' rows='7'></textarea>");
		//	tr.append("</td>");
		//	tr.append("</tr>");
		//	tr.append("</table>");
			tr.append("</td>");
			tr.append("</tr>");
			
		}				
		
		return tr.toString();
	}	
	
	/**
	 * 生成 了解程度
	 * @return
	 */
	public String getExtendTd(String selectid,int lay,String name,String controlName,String object_status)
	{
		String clientname = SystemConfig.getPropertyValue("clientName");
		StringBuffer td=new StringBuffer("<tr> <td class='RecordRow' align='center' colspan='"+lay+"' nowrap >");
		td.append(name);
		td.append("</td>");	
		try 
		{
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();	
			String NodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度
			String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
			String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
			String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
			
			ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
			if("True".equalsIgnoreCase(allowSeeLowerGrade))
			    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
			ArrayList nodeKnowDegreeList = new ArrayList();  // 了解程度的选项信息
			if("true".equals(NodeKnowDegree))
				nodeKnowDegreeList=getExtendInfoValue("1","");					
			if(this.isShowObjectSelfScore)
				allowSeeLowerGrade="false";
			ArrayList gradelist=getGradeDesc();  //标准标度;					
			
			if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
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
			else if(this.isLookObjectScore)
	    	{
	    		td.append("<td  class='RecordRow' align='center' >");
	    		if(this.objectSelfCore.get("know_id")!=null)
	    		{
		    		for(int i=0;i<nodeKnowDegreeList.size();i++)
		    		{
		    			String[] temp=(String[])nodeKnowDegreeList.get(i);
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
			if(!this.isShowObjectSelfScore)
			{			
				if(("1".equals(DegreeShowType) || "3".equals(DegreeShowType)) && "1".equals(PointEvalType))
				{
					int n=0;
					if("True".equalsIgnoreCase(showDeductionCause))
						 n=1;
					td.append("<td class='RecordRow' colspan='"+(gradelist.size()+n)+"' align='left' >");
				}
				else
					td.append("<td class='RecordRow'   align='left' >");
				if("gs".equalsIgnoreCase(clientname)){
					td.append("<select name='"+this.plan_id+this.object_id+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
				}else{
					td.append("<select name='"+controlName+"'  onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
				}
				
				if("2".equals(object_status) || "4".equals(object_status) || "7".equals(object_status))
					td.append(" disabled ");
				td.append(" >");
				td.append("<option value=''></option>");
				for(int i=0;i<nodeKnowDegreeList.size();i++)
				{
					String[] temp=(String[])nodeKnowDegreeList.get(i);
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
		
		} catch (GeneralException e) 
		{				
			e.printStackTrace();
		}
		return td.toString();
	}
	/**
	 * 生成 总体评价
	 * @param list   选项信息
	 * @return
	 */
	public String getExtendTd2(String selectid,int lay,String name,String controlName,String object_status)
	{
		String clientname = SystemConfig.getPropertyValue("clientName");
		StringBuffer td=new StringBuffer("<tr> <td class='RecordRow' align='center' colspan='"+lay+"' nowrap >");
		td.append(name);
		td.append("</td>");
		try 
		{
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();
			String WholeEval=(String)htxml.get("WholeEval");			    //总体评价
			String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			String performanceType=(String) htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
			String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
			String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
			String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
			String DescriptiveWholeEval=(String)htxml.get("DescriptiveWholeEval"); //显示描述性总体评价，默认为 True	
			String GradeClass=(String)htxml.get("GradeClass");	//等级分类ID	
			String EvalClass = (String)htxml.get("EvalClass");            //在计划参数中的等级分类ID
			if(EvalClass==null||EvalClass.trim().length()<=0|| "0".equals(EvalClass.trim()))
				GradeClass = (String)htxml.get("GradeClass");					//等级分类ID
			else
				GradeClass = (String)htxml.get("EvalClass");
			
			ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
			if("True".equalsIgnoreCase(allowSeeLowerGrade))
			    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
			ArrayList wholeEvalList = new ArrayList();    // 总体评价的选项信息
			if("true".equals(WholeEval))
				wholeEvalList=getExtendInfoValue("2",GradeClass);																
			if(this.isShowObjectSelfScore)
				allowSeeLowerGrade="false";
			ArrayList gradelist=getGradeDesc();  //标准标度;
						
			if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
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
			else if(this.isLookObjectScore)
	    	{
	    		td.append("<td  class='RecordRow' align='center' >");
	    		if("True".equalsIgnoreCase(WholeEval))
	    		{
		    		if(this.objectSelfCore.get("whole_grade_id")!=null)
		    		{
			    		for(int i=0;i<wholeEvalList.size();i++)
			    		{
			    			String[] temp=(String[])wholeEvalList.get(i);
			    			if(temp[0].equals((String)this.objectSelfCore.get("whole_grade_id")))
			    			{
			    				td.append(temp[1]);
			    				break;
			    			}
			    		}	
		    		}
	    		}
	    		if((!"1".equals(performanceType)|| ("1".equals(performanceType) && !this.isProAppraise) && "True".equalsIgnoreCase(DescriptiveWholeEval)) || "1".equals(opt))
	    		{
	    			if("1".equals(opt))
	    				td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\" onclick=\"javascript:showWindow2('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID))+"')\" > ");
	    			else
	    				td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\" onclick=\"javascript:showWindow2('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"')\" > ");
	    		}
	    		
	    		td.append("&nbsp;</td>");
	    		if("True".equalsIgnoreCase(showDeductionCause))
				{
	    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
				}
	    	}
			if(!this.isShowObjectSelfScore)
			{
				if(("1".equals(DegreeShowType) || "3".equals(DegreeShowType)) && "1".equals(PointEvalType))
				{
					int n=0;
					if("True".equalsIgnoreCase(showDeductionCause))
						 n=1; 
					td.append("<td colspan='"+(gradelist.size()+n)+"' vAlign='bottom' class='RecordRow' align='left' >");			
				}
				else
					td.append("<td vAlign='bottom' class='RecordRow' align='left' >");
				if("True".equalsIgnoreCase(WholeEval))
				{
					if(totalAppFormula!=null && totalAppFormula.trim().length()>0 && "wholeEval".equalsIgnoreCase(controlName))
					{
						td.append(" <input type='hidden' name='"+controlName+"' value='");
						String wholeVale = "";
						for(int i=0;i<wholeEvalList.size();i++)
			    		{
			    			String[] temp=(String[])wholeEvalList.get(i);
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
						if("gs".equalsIgnoreCase(clientname)){
							td.append("<select name='"+this.plan_id+this.object_id+controlName+"' onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
						}else{
							td.append("<select name='"+controlName+"' onkeydown='if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  ");
						}
						
						if("2".equals(object_status) || "4".equals(object_status) || "7".equals(object_status))
							td.append(" disabled ");
					//	if(totalAppFormula!=null && totalAppFormula.trim().length()>0 && controlName.equalsIgnoreCase("wholeEval"))
					//		td.append(" disabled ");
						td.append(" >");
						td.append("<option value=''></option>");
						for(int i=0;i<wholeEvalList.size();i++)
						{
							String[] temp=(String[])wholeEvalList.get(i);
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
				if((!"1".equals(performanceType) || ("1".equals(performanceType) && !this.isProAppraise)) && "True".equalsIgnoreCase(DescriptiveWholeEval))
					td.append("&nbsp;&nbsp;<img  style=\"cursor:hand\" src=\"/images/table.gif\" onclick=\"javascript:showWindow('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID))+"')\" >&nbsp;&nbsp;&nbsp;&nbsp; ");
				td.append("</td>");
			}
			td.append("</tr>");
			
		} catch (GeneralException e) 
		{				
			e.printStackTrace();
		}
		return td.toString();
	}
	// 生成 不打分原因
	public String getNotMarkTd(int lay,String status)
	{
	    String tempid = "~"+SafeCode.encode(PubFunc.convertTo64Base(this.plan_id));
        String tempObj = "~"+SafeCode.encode(PubFunc.convertTo64Base(object_id));
		Hashtable htxml=new Hashtable();							
		htxml=this.loadxml.getDegreeWhole();
		String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
		String performanceType=(String) htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
		String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
		String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
		ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
		if("True".equalsIgnoreCase(allowSeeLowerGrade))
		    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
		if(this.isShowObjectSelfScore)
			allowSeeLowerGrade="false";
		ArrayList gradelist=getGradeDesc();  //标准标度;
		
		StringBuffer td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");
		if("0".equals(performanceType))
			td.append(ResourceFactory.getProperty("lable.performnace.noMarkCause"));
		else if("1".equals(performanceType))
			td.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
		td.append("</td>");
		
		if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
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
	    		td.append(" name='b"+this.object_id+"_"+this.plan_id+"_"+a0100+"'  "+check+"  ></td>");
	    		if("block".equals(display))
	    		{
	    			td.append("<td width='70'  valign='bottom' nowrap > ");
	    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+tempid+"&objectID="+tempObj+"&mainbodyID="+"~"+SafeCode.encode(PubFunc.convertTo64Base(a0100))+"&type=0')\" >");
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
		else if(this.isLookObjectScore)
    	{
    		td.append("<td  class='RecordRow' align='left' >");
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
    		td.append(" name='b"+this.object_id+"_"+this.plan_id+"_"+this.userID+"'  "+check+"  ></td>");
    		if("block".equals(display))
    		{
    			td.append("<td width='70'  valign='bottom' nowrap > ");
    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+tempid+"&objectID="+tempObj+"&mainbodyID="+tempObj+"&type=0')\" >");
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
		if(!this.isShowObjectSelfScore)
		{
			if(("1".equals(DegreeShowType) || "3".equals(DegreeShowType)) && "1".equals(PointEvalType))
			{
				int n=0;
				if("True".equalsIgnoreCase(showDeductionCause))
					 n=1; 
				 
				td.append("<td  class='RecordRow'   colspan='"+(gradelist.size()+n)+"' align='left' > ");
			}
			else
				td.append("<td  class='RecordRow'   align='left' >");
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
			
			if(("1".equals(DegreeShowType) || "3".equals(DegreeShowType)) && "1".equals(PointEvalType))
				td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			td.append("<input type='checkbox' tile='sdfasdf' onclick='setStatus(this)' ");
			if("7".equals(status)|| "2".equals(status))
				td.append(" disabled ");
			
			if("1".equals(this.fillctrl))  //必打分项
			{
				td.append(" style='display:none' ");
			}
			
			td.append(" name='b"+this.object_id+"_"+this.plan_id+"_"+this.userID+"'  "+check+"  ></td>");
			
			td.append("<td width='70'  valign='bottom' nowrap > ");
			td.append(" <div style='display:"+display+"' id='b"+this.object_id+"' >");
			String tempuid = "~"+SafeCode.encode(PubFunc.convertTo64Base(this.userID));
			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status="+a_status+"&planID="+tempid+"&objectID="+tempObj+"&mainbodyID="+tempuid+"&type=0')\" >");
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
	public String getNotMarkTd2(int lay,String status)
	{
		StringBuffer td=new StringBuffer("<tr> <td class='RecordRow' align='center'  colspan='"+lay+"'    nowrap >");
	    td.append(ResourceFactory.getProperty("performance.batchgrade.otherInfo"));
		td.append("</td>");
		
		Hashtable htxml=new Hashtable();							
		htxml=this.loadxml.getDegreeWhole();
		String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
		
		if(this.isLookObjectScore)
    	{
    		td.append("<td  class='RecordRow' align='center' >");
    		
    		{	
    			td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status=7&planID="+this.plan_id+"&objectID="+this.object_id+"&mainbodyID="+this.object_id+"&type=1&edit=false')\" >");
    		    td.append(ResourceFactory.getProperty("kh.field.helpcontent"));
    		}
    		td.append("&nbsp;</td>");
    		if("True".equalsIgnoreCase(showDeductionCause))
			{
    			 td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
			}
    	}
		
		if(!this.isShowObjectSelfScore)
		{
			td.append("<td  class='RecordRow' align='center' >");
			String a_status="4";
		    td.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&status="+a_status+"&planID="+this.plan_id+"&objectID="+this.object_id+"&mainbodyID="+this.userID+"&type=1')\" >");
		    td.append(ResourceFactory.getProperty("kh.field.helpcontent"));		
			td.append("</td>");
		}
		return td.toString();
	}
	/**
	* 画总分	
	* @return
	*/
	private String writeTotalScore(int rowspan) throws GeneralException
	{		
		StringBuffer td=new StringBuffer("");
		try
		{		
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();
			String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
			String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
			String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位				
			String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选
			String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
			
			ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
			if("True".equalsIgnoreCase(allowSeeLowerGrade))
			    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
			if(this.isShowObjectSelfScore)
				allowSeeLowerGrade="false";
			ArrayList gradelist=getGradeDesc();  //标准标度;
			
			ContentDAO dao=new ContentDAO(this.con);
			float selfScore=0f;
			if((this.isLookObjectScore) || "1".equals(opt))
			{
				 if(this.plan_vo!=null && (this.plan_vo.getInt("object_type")==1 || this.plan_vo.getInt("object_type")==3 || this.plan_vo.getInt("object_type")==4))
				 {						
					 RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
					 if(rowSet.next())
						 selfScore=getObjectTotalScore(rowSet.getString("mainbody_id"));
				 }
				 else
					 selfScore=getObjectTotalScore(this.object_id);
			}								
			td.append("<tr> <td class='RecordRow' align='center' colspan='"+(rowspan)+"' nowrap >");
			td.append("总分");
			td.append("</td>");
							
			if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
			{
				for(int j=0;j<lowerGradeMainbodyList.size();j++)
				{
					LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
					String a0100=(String)a_bean.get("a0100");
					String _status=(String)a_bean.get("status");
					if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
						continue;
					
					float score=getObjectTotalScore(a0100);
					td.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal))+"</td>");
					
					if("True".equalsIgnoreCase(showDeductionCause))
					{
						td.append("<td  class='RecordRow' align='center' >&nbsp;</td>");
					}
				}					
			}
			else if(this.isLookObjectScore)
	    	{
				if("1".equalsIgnoreCase(this.opt))
				{
					float score=getObjectTotalScore(userID);
					td.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal))+"</td>");						
				}
				else
					td.append("<td class='RecordRow' align='center' >"+PubFunc.round(String.valueOf(selfScore),Integer.parseInt(KeepDecimal))+"</td>");
				if("True".equalsIgnoreCase(showDeductionCause))
				{
					td.append("<td class='RecordRow' align='center' >&nbsp;</td>");
				}
	    	}
			
			if(!this.isShowObjectSelfScore)
			{
				float score=getObjectTotalScore(userID);
				if(("1".equals(DegreeShowType) || "3".equals(DegreeShowType)) && "1".equals(PointEvalType))
				{
					int n=0;
					if("True".equalsIgnoreCase(showDeductionCause))
						 n=1; 
					td.append("<td class='RecordRow' colspan='"+(gradelist.size()+n)+"' id='ascore' align='left' ><font color='blue'>");					
				}
			    else
			    	td.append("<td class='RecordRow' id='ascore' align='left' ><font color='blue'>");
				td.append(PubFunc.round(String.valueOf(score),Integer.parseInt(KeepDecimal)));
				td.append("</font></td>");
			}
			td.append("</tr>");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return td.toString();
	}
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String plan_id)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.con);
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("method")==0)
				vo.setInt("method",1);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * template_id 模板编号
	 * 获得某编号的考核模板的所有信息
	 */
	public RecordVo getTemplateVo(String template_id)
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",template_id);
			ContentDAO dao = new ContentDAO(this.con);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * 获得无权限的指标
	 * @return
	 */
	public String getNoPrivPointStr()
	{
		String str = "";		
		try
		{
			HashMap pointMap=getPointprivMap();                          // 得到指标权限信息
			this.pointmapPrv=(HashMap)pointMap.get(this.object_id);	 // 得到某考核对象的指标权限 map	
			 
			Set keySet=this.pointmapPrv.keySet();		
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if((this.pointmapPrv.get(key)!=null) && "0".equals((String)this.pointmapPrv.get(key)))
					str+=",'"+key+"'";	 
			}
			if(str.trim().length()>0)
				str=str.substring(1);
			 					
			if("false".equalsIgnoreCase(this.showOneMark))             // false 不显示统一评分指标
			{
				String oneMardStr=getOneMarkPointStr();       // 取得模板下的定量统一打分指标                    
				if(oneMardStr.trim().length()>0)
				{
					if(str.trim().length()>0)
						str+=oneMardStr;
				 	else
				 		str=oneMardStr.substring(1);
				}			 				 
			}
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return str;
	}		

	/**
     * 得到指标权限信息    
     * @return
     */
	public HashMap getPointprivMap() throws GeneralException
	{
	
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try
		{
		    ArrayList pointList = new ArrayList();
		    String sql = "select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
			           + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + this.plan_id;
		    rowSet = dao.search(sql);
		    while (rowSet.next())
		    {
		    	pointList.add(rowSet.getString(1));
		    }
		
		    sql = "select * from per_pointpriv_" + this.plan_id + " where mainbody_id='" + this.userID + "'";
		    rowSet = dao.search(sql);
		    int num = 0;
		    while (rowSet.next())
		    {
				num++;
				String object_id = rowSet.getString("object_id");
				HashMap pointMap = new HashMap();
				for (Iterator t = pointList.iterator(); t.hasNext();)
				{
				    String temp = (String) t.next();
				    String _value=rowSet.getString("C_" + temp);
				    if("0.0".equals(_value))
				    	_value="0";
				    else if("1.0".equals(_value))
				    	_value="1";
				    pointMap.put(temp,_value);
			
				}
				map.put(object_id, pointMap);
		    }
		    if (num == 0)
		    {
				rowSet = dao.search("select distinct object_id from per_mainbody where plan_id=" + this.plan_id + " and mainbody_id='" + this.userID + "' ");
				while (rowSet.next())
				{
				    String object_id = rowSet.getString("object_id");
				    HashMap pointMap = new HashMap();
				    for (Iterator t = pointList.iterator(); t.hasNext();)
				    {
						String temp = (String) t.next();
						pointMap.put(temp, "1");
				    }
				    map.put(object_id, pointMap);
				}
		    }
		    
		    if(rowSet!=null)
				rowSet.close();
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		}	
		return map;
	}

	/**
	 * 取得模板下的定量统一打分指标
	 * @return
	 */
	public String getOneMarkPointStr()
	{
		StringBuffer str=new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet=null;
		try
		{
			String sql = "select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po "
			    	   + " where pi.item_id=pp.item_id and pp.point_id=po.point_id and template_id='" + this.plan_vo.getString("template_id") + "' "; // pi.seq,
				   sql +=" and  po.pointkind='1' and po.status=1 ";
		    rowSet = dao.search(sql);
			while(rowSet.next())
			{
				str.append(",'"+rowSet.getString("point_id")+"'");
			}
			
			if(rowSet!=null)
				rowSet.close();
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return str.toString();
	}	
	
	/**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList()
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			rowSet=dao.search("select * from per_template_item where template_id='"+this.plan_vo.getString("template_id")+"' order by seq");
		    LazyDynaBean abean=null;
			while(rowSet.next())
		    {
				abean=new LazyDynaBean();
		    	abean.set("item_id",rowSet.getString("item_id"));
		    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
		    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
		    	abean.set("template_id",rowSet.getString("template_id"));
		    	abean.set("itemdesc",rowSet.getString("itemdesc"));
		    	abean.set("seq",rowSet.getString("seq"));
		    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"1");	
		    	abean.set("score",rowSet.getString("score")==null?"0":this.myformat.format(rowSet.getDouble("score")));
		    	abean.set("rank",rowSet.getString("rank")==null?"0":this.myformat.format(rowSet.getDouble("rank")));
		    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
		    	list.add(abean);
//		    	if(rowSet.getString("parent_id")==null||rowSet.getString("parent_id").equals(""))
//		    	{
//		    		this.parentList.add(abean);
//		    	}
		    }
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得岗位的能力素质指标分类
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldSetList(String object_id) throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
		    LazyDynaBean abean=null;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar calendar = Calendar.getInstance();				
			String historyDate = sdf.format(calendar.getTime());
		    StringBuffer sbsql=new StringBuffer();
    	    sbsql.append("select pcm.point_type,ci.codeitemdesc from per_competency_modal pcm left join codeitem ci on pcm.point_type=ci.codeitemid and ci.codesetid='70' where pcm.object_type='3' and pcm.object_id = '"+object_id+"'");
    	    sbsql.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
    	    rowSet=dao.search(sbsql.toString());
    	    HashMap map=new HashMap();
			while(rowSet.next())
		    {
				abean=new LazyDynaBean();
				String point_type=rowSet.getString("point_type")==null|| "".equals(rowSet.getString("point_type"))?"-9999":rowSet.getString("point_type");
				// 岗位素质模型指标没有指标分类  2013.12.3 pjf
				//String codeitemdesc=rowSet.getString("codeitemdesc")==null||rowSet.getString("codeitemdesc").equals("")?"无指标分类":rowSet.getString("codeitemdesc");
				String codeitemdesc=rowSet.getString("codeitemdesc")==null|| "".equals(rowSet.getString("codeitemdesc"))?"岗位素质模型指标":rowSet.getString("codeitemdesc");
				if(map.get(point_type)==null){
					map.put(point_type, "1");
			    	abean.set("item_id",point_type);//指标分类 对应70代码
			    	abean.set("itemdesc",codeitemdesc);//指标分类名称
			    	list.add(abean);
				}

		    }
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}	
	/**
	 * 获得叶子项目列表
	 */
	public void get_LeafItemList()
	{
		try
		{			
			LazyDynaBean abean=null;
			for(int i=0;i<this.templateItemList.size();i++)
			{
				abean=(LazyDynaBean)this.templateItemList.get(i);
				String parent_id=(String)abean.get("parent_id");
				if(parent_id.length()==0)
				{				
					setLeafItemFunc(abean);
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 能力素质分层级分类 暂时只取一层
	 */
	public void get_LeafItemListByModel(){
		LazyDynaBean abean=null;
		ArrayList list=new ArrayList();
		for(int i=0;i<this.templateItemList.size();i++)
		{	list=new ArrayList();
			abean=(LazyDynaBean) templateItemList.get(i);
			list.add(abean);
			this.leafItemList.add(abean);
			this.leafItemLinkMap.put(abean.get("item_id"),list);
		}
	}
    //	递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		//判断项目下是否有指标
		if(child_id.length()==0) 
		{
			this.leafItemList.add(abean);
			return;
		}
		else if(this.itemPointMap.get(item_id)!=null) // 包含顶层项目有指标的情况
		{
			this.leafItemList.add(abean);
		}
			
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parent_id=(String)a_bean.get("parent_id");
			if(parent_id.equals(item_id))
				setLeafItemFunc(a_bean);
		}
	}
	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public HashMap getLeafItemLinkMap()
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
			//	String parent_id=(String)abean.get("parent_id");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean);
				if(linkList.size()>this.lay)
					this.lay=linkList.size();
				map.put(item_id,linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	//寻找继承关系
	public void getParentItem(ArrayList list,LazyDynaBean abean)
	{
	//	String item_id=(String)abean.get("item_id");
		String parent_id=(String)abean.get("parent_id");
		if(parent_id.length()==0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean=null;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)a_bean.get("item_id");
	//		String parentid=(String)a_bean.get("parent_id");
			if(itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,a_bean);
			}			
		}				
	}		
	/**
	 * 叶子项目对应的指标集	
	 * @return ArrayList
	 */
	public ArrayList getChildItemPointList()
	{	
		ArrayList list  =  new ArrayList();
		HashMap pointmap = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		try
		{
			for(int i=0;i<this.leafItemList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)this.leafItemList.get(i);
				String itemid=(String)bean.get("item_id");				
				StringBuffer sql = new StringBuffer();
				sql.append("select ptp.point_id,ptp.score,ptp.seq,ptp.rank,po.pointname,po.kh_content,po.gd_principle,po.pointkind,po.visible,po.status from per_template_point ptp,per_point po ");
				sql.append(" where ptp.point_id=po.point_id ");	
				if(this.noPrivPointStr!=null && this.noPrivPointStr.trim().length()>0)
					sql.append(" and lower(ptp.point_id) not in ("+this.noPrivPointStr.toLowerCase()+")");	
				sql.append(" and item_id="+itemid+" ");	
				sql.append(" order by ptp.seq");												
				rs = dao.search(sql.toString());
				String sign = "false";
				while(rs.next())
				{
					LazyDynaBean a_bean = new LazyDynaBean();
					a_bean.set("item_id",itemid);
					a_bean.set("point_id",rs.getString("point_id")!=null?rs.getString("point_id"):"");
					a_bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
					a_bean.set("seq",rs.getString("seq")!=null?rs.getString("seq"):"");
					a_bean.set("pointkind",isNull(rs.getString("pointkind")));
					a_bean.set("visible",isNull(rs.getString("visible")));
					a_bean.set("status",rs.getString("status")!=null?rs.getString("status"):"0");
					a_bean.set("pointname",rs.getString("pointname")!=null?rs.getString("pointname"):"");
					a_bean.set("kh_content",rs.getString("kh_content")!=null?rs.getString("kh_content"):"");
					a_bean.set("gd_principle",rs.getString("gd_principle")!=null?rs.getString("gd_principle"):"");
					a_bean.set("rank",rs.getString("rank")==null?"0":this.myformat.format(rs.getDouble("rank")));
					list.add(a_bean);
					pointmap.put(rs.getString("point_id").toLowerCase(), a_bean);
					sign = "true";
				}
				if("false".equalsIgnoreCase(sign))  // 叶子项目下无指标时放入一个虚拟指标
				{
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("item_id",itemid);
					abean.set("point_id",itemid+":xxx");
					abean.set("pointname","");
					list.add(abean);					
				}
				// 指标信息map
//				if(this.pointMap.size()==0)
					this.pointMap=pointmap;				
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 *能力素质指标分类对应的指标集	
	 * @return ArrayList
	 */
	public ArrayList getFieldSetPointList(String object_id)
	{	
		ArrayList list  =  new ArrayList();
		HashMap pointmap = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		try
		{
			for(int i=0;i<this.templateItemList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)this.templateItemList.get(i);
				String item_id=(String)bean.get("item_id");//能力素质分类				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
				Calendar calendar = Calendar.getInstance();				
				String historyDate = sdf.format(calendar.getTime());
			    StringBuffer sbsql=new StringBuffer();
	    	    sbsql.append("select pcm.point_id,pcm.score,po.seq,pcm.rank,po.pointname,po.kh_content,po.gd_principle,po.pointkind,po.visible,po.status from per_competency_modal pcm ,per_point po where pcm.point_id=po.point_id ");
	    	    if("-9999".equals(item_id)){
	    	    	sbsql.append("  and pcm.object_type='3' and pcm.object_id = '"+object_id+"' and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date" +
	    	    			" and (pcm.point_type='' or pcm.point_type is null)");					
	    	    }else{
	    	    	sbsql.append("  and pcm.object_type='3' and pcm.object_id = '"+object_id+"' and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date" +
	    	    			" and pcm.point_type='"+item_id+"'");					
	    	    }
	    	    
	    	    rs=dao.search(sbsql.toString());
				String sign = "false";
				while(rs.next())
				{
					LazyDynaBean a_bean = new LazyDynaBean();
					a_bean.set("item_id",item_id);
					a_bean.set("point_id",rs.getString("point_id")!=null?rs.getString("point_id"):"");
					a_bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
					a_bean.set("seq",rs.getString("seq")!=null?rs.getString("seq"):"");
					a_bean.set("pointkind",isNull(rs.getString("pointkind")));
					a_bean.set("visible",isNull(rs.getString("visible")));
					a_bean.set("status",rs.getString("status")!=null?rs.getString("status"):"0");
					a_bean.set("pointname",rs.getString("pointname")!=null?rs.getString("pointname"):"");
					a_bean.set("kh_content",rs.getString("kh_content")!=null?rs.getString("kh_content"):"");
					a_bean.set("gd_principle",rs.getString("gd_principle")!=null?rs.getString("gd_principle"):"");
					a_bean.set("rank",rs.getString("rank")==null?"0":this.myformat.format(rs.getDouble("rank")));
					list.add(a_bean);
					pointmap.put(rs.getString("point_id").toLowerCase(), a_bean);
					sign = "true";
				}

				// 指标信息map
//				if(this.pointMap.size()==0)
					this.pointMap=pointmap;				
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public String isNull(String str)
    {

    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    return "";
		else
		    return str;

    }
	/**
	 * 项目对应的指标集	
	 * @return HashMap
	 */
	public HashMap getItemPointList()
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();			
			sql.append("select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.status ");
			sql.append(" from per_template_item pi,per_template_point pp,per_point po ");
			sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id ");
			if(this.noPrivPointStr!=null && this.noPrivPointStr.trim().length()>0)
				sql.append(" and lower(po.point_id) not in ("+this.noPrivPointStr.toLowerCase()+")");	
			sql.append(" and template_id='"+this.plan_vo.getString("template_id")+"' ");	
			sql.append(" order by pp.seq");							
			
/*			StringBuffer sql = new StringBuffer();
			sql.append("select ptp.*,pp.pointname from per_template_point ptp,per_point pp where  ptp.item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='");
			sql.append(templateID.toUpperCase()+"') and ptp.point_id=pp.point_id order by ptp.seq");
*/						
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next())
			{
				bean = new LazyDynaBean();
			
				bean.set("point_id",rs.getString("point_id"));
				bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
				bean.set("item_id",rs.getString("item_id"));
				bean.set("pointname", rs.getString("pointname"));
				String item_id = rs.getString("item_id");
				if(map.get(item_id)!=null)
				{
					ArrayList list = (ArrayList)map.get(item_id);
					list.add(bean);
					map.put(item_id, list);	
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id,list);
				}
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 指标分类对应的指标集	  
	 * @return HashMap key 能力素质分类对应的代码类70的代码  value list(bean:point_id,score,item_id,pointname)
	 */
	public HashMap getFieldItemPointList(String object_id)
	{
		HashMap map = new HashMap();
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar calendar = Calendar.getInstance();				
			String historyDate = sdf.format(calendar.getTime());
		    StringBuffer sbsql=new StringBuffer();
    	    sbsql.append("select pcm.point_id,pcm.score,pcm.point_type item_id,per_point.pointname from per_competency_modal pcm , per_point where pcm.point_id=per_point.point_id ");
    	    sbsql.append("  and pcm.object_type='3' and pcm.object_id = '"+object_id+"' and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");					
			
				
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs = dao.search(sbsql.toString());
			LazyDynaBean bean = null;
			while(rs.next())
			{
				bean = new LazyDynaBean();
				String item_id = rs.getString("item_id")==null|| "".equals(rs.getString("item_id"))?"-9999":rs.getString("item_id");
				bean.set("point_id",rs.getString("point_id"));
				bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
				bean.set("item_id",item_id);
				bean.set("pointname", rs.getString("pointname"));

				if(map.get(item_id)!=null)
				{
					ArrayList list = (ArrayList)map.get(item_id);
					list.add(bean);
					map.put(item_id, list);	
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id,list);
				}
			}
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	/**
	 * 取得项目拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNum()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			getLeafItemList(a_bean,list);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(this.itemScaleMap.get(item_id)!=null)				
					n+=((ArrayList)this.itemScaleMap.get(item_id)).size();																		
				else
					n+=1;
			}
			map.put((String)a_bean.get("item_id"),new Integer(n));
		}
		return map;
	}	
	/**
	 * 按岗位素质模型取得指标分类拥有的节点数
	 * @return
	 */
	public HashMap getItemPointNumByModel()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{	
			int n=0;
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String item_id=(String)a_bean.get("item_id");
			if(this.itemScaleMap.get(item_id)!=null)				
				n+=((ArrayList)this.itemScaleMap.get(item_id)).size();																		
			else
				n+=1;
			map.put((String)a_bean.get("item_id"),new Integer(n));
		}
		return map;
	}	
	public void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		if(child_id.length()==0)
		{
			list.add(abean);
			return;
		}
		else if(this.itemPointMap.get(item_id)!=null) // 包含顶层项目有指标的情况
		{
			list.add(abean);
		}	
		
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parent_id=(String)a_bean.get("parent_id");
			if(parent_id.equals(item_id))
				getLeafItemList(a_bean,list);
		}		
	}	
	/**
	 * 指标对应的指标标度集	
	 * @return HashMap
	 */
	public HashMap getPointScaleMap()
	{
		ArrayList PointScaleList = new ArrayList();
		HashMap map = new HashMap();
		HashMap IteMap = new HashMap();
		HashMap jchMap = new HashMap();
		RowSet rs = null;
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			StringBuffer sql = new StringBuffer();			
			sql.append("select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.status ");
			sql.append(" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt ");
			sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id ");
			if(this.noPrivPointStr!=null && this.noPrivPointStr.trim().length()>0)
				sql.append(" and lower(po.point_id) not in ("+this.noPrivPointStr.toLowerCase()+")");	
			sql.append(" and template_id='"+this.plan_vo.getString("template_id")+"' ");	
			sql.append(" order by pp.seq,pg.grade_id");							
									
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next())
			{
				bean = new LazyDynaBean();	
				bean.set("item_id",rs.getString("item_id"));
				bean.set("point_id",rs.getString("point_id"));
				bean.set("pointname", rs.getString("pointname"));
				bean.set("pointkind", rs.getString("pointkind"));
				bean.set("gradedesc", rs.getString("gradedesc"));
				bean.set("gradecode", rs.getString("gradecode"));				
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat.format(rs.getDouble("bottom_value")));
				bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat.format(rs.getDouble("gradevalue")));
				bean.set("status", rs.getString("status"));	
				PointScaleList.add(bean);
				
				// 指标对应的指标标度map
				String point_id = rs.getString("point_id");
				String pointkind = rs.getString("pointkind");
				if(map.get(point_id)!=null)
				{
					if(!"1".equalsIgnoreCase(pointkind))
					{
						ArrayList list = (ArrayList)map.get(point_id);
						list.add(bean);
						map.put(point_id, list);
					}
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(point_id,list);
				}
				// 项目对应的指标和指标标度map
				String item_id = rs.getString("item_id");								
				if(IteMap.get(item_id)!=null)
				{
					if(!"1".equalsIgnoreCase(pointkind))
					{						
						ArrayList list = (ArrayList)IteMap.get(item_id);
						list.add(bean);
						IteMap.put(item_id, list);	
						
					}else
					{
						if(jchMap.get(point_id)==null)
						{													
							ArrayList list = (ArrayList)IteMap.get(item_id);
							list.add(bean);
							IteMap.put(item_id, list);
							jchMap.put(point_id,"1");
						}					
					}
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					IteMap.put(item_id,list);
					jchMap.put(point_id,"1");
				}
				
			}
			// 项目对应的指标和指标标度map
//			if(this.itemScaleMap.size()==0)
				this.itemScaleMap=IteMap;
				
			// 指标标度List
//			if(this.PointScaleList.size()==0)
				this.PointScaleList=PointScaleList;
				
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}		
	
	/**
	 * 指标对应的指标标度集	
	 * @return HashMap
	 */
	public HashMap getPointScaleMapByModel(String object_id)
	{
		ArrayList PointScaleList = new ArrayList();
		HashMap map = new HashMap();
		HashMap IteMap = new HashMap();
		HashMap jchMap = new HashMap();
		RowSet rs = null;
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar calendar = Calendar.getInstance();				
			String historyDate = sdf.format(calendar.getTime());
		    StringBuffer sbsql=new StringBuffer();
    	    sbsql.append("select pcm.point_type item_id,pcm.score,pcm.point_id,pp.pointname,pp.pointkind,pp.status,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pg.gradevalue" +
    	    		" from per_competency_modal pcm ,per_grade pg ,per_point pp  where pcm.point_id=pp.point_id and pcm.point_id=pg.point_id");
    	    sbsql.append("  and pcm.object_type='3' and pcm.object_id = '"+object_id+"' and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");					
			
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(sbsql.toString());
			LazyDynaBean bean = null;
			String item_id="";
			while(rs.next())
			{
				bean = new LazyDynaBean();	
				item_id=rs.getString("item_id")==null|| "".equals(rs.getString("item_id"))?"-9999":rs.getString("item_id");
				bean.set("item_id",rs.getString("item_id"));
				bean.set("point_id",rs.getString("point_id"));
				bean.set("pointname", rs.getString("pointname"));
				bean.set("pointkind", rs.getString("pointkind"));
				bean.set("gradedesc", rs.getString("gradedesc"));
				bean.set("gradecode", rs.getString("gradecode"));				
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat.format(rs.getDouble("bottom_value")));
				bean.set("score",rs.getString("score")==null?"0":this.myformat.format(rs.getDouble("score")));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat.format(rs.getDouble("gradevalue")));
				bean.set("status", rs.getString("status"));	
				PointScaleList.add(bean);
				
				// 指标对应的指标标度map
				String point_id = rs.getString("point_id");
				String pointkind = rs.getString("pointkind");
				if(map.get(point_id)!=null)
				{
					if(!"1".equalsIgnoreCase(pointkind))
					{
						ArrayList list = (ArrayList)map.get(point_id);
						list.add(bean);
						map.put(point_id, list);
					}
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(point_id,list);
				}
				// 项目对应的指标和指标标度map
				//String item_id = rs.getString("item_id");								
				if(IteMap.get(item_id)!=null)
				{
					if(!"1".equalsIgnoreCase(pointkind))
					{						
						ArrayList list = (ArrayList)IteMap.get(item_id);
						list.add(bean);
						IteMap.put(item_id, list);	
						
					}else
					{
						if(jchMap.get(point_id)==null)
						{													
							ArrayList list = (ArrayList)IteMap.get(item_id);
							list.add(bean);
							IteMap.put(item_id, list);
							jchMap.put(point_id,"1");
						}					
					}
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					IteMap.put(item_id,list);
					jchMap.put(point_id,"1");
				}
				
			}
			// 项目对应的指标和指标标度map
//			if(this.itemScaleMap.size()==0)
				this.itemScaleMap=IteMap;
				
			// 指标标度List
//			if(this.PointScaleList.size()==0)
				this.PointScaleList=PointScaleList;
				
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	/**
	 * 获得表头列表
	 * @return list
	 */
	public ArrayList getHeadList(String object_status)
	{
		ArrayList list=new ArrayList();
		ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
		try
		{		
			ContentDAO dao=new ContentDAO(this.con);
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();					    			
			String showIndicatorContent=(String)htxml.get("showIndicatorContent");  // 显示考核指标内容
			String showIndicatorDegree=(String)htxml.get("showIndicatorDegree");  // 显示考核指标标度说明
			String showIndicatorRole=(String)htxml.get("showIndicatorRole");  // 显示考核指标评分原则
			String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
			if(this.isShowObjectSelfScore)
				allowSeeLowerGrade="false";
			String selfScoreInDirectLeader=(String)htxml.get("SelfScoreInDirectLeader");//上级领导给下级打分时是否 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
			String DegreeShowType=(String)htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
			String PointEvalType=(String)htxml.get("PointEvalType");   //360指标评分型式  0：下拉（默认）   1：单选						 
		//	String NodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度			
			String showDeductionCause=(String)htxml.get("showDeductionCause");  //显示扣分原因(Ture, False(默认))
					
			if("True".equalsIgnoreCase(allowSeeLowerGrade))
			    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
			
			if(!"0".equalsIgnoreCase(selfScoreInDirectLeader) && !"False".equalsIgnoreCase(selfScoreInDirectLeader))
			{
				String level=getMainBodyLevel();  //取得考核主体是考核对象的主体类别				 
				if(("True".equalsIgnoreCase(selfScoreInDirectLeader) || "1".equals(selfScoreInDirectLeader)) && "1".equals(level))
					isLookObjectScore=true;
				else if("2".equals(selfScoreInDirectLeader) && ("1".equals(level) || "0".equals(level) || "-2".equals(level) || "-1".equals(level)))
					isLookObjectScore=true; 
				else if("3".equals(selfScoreInDirectLeader) && !this.userID.equalsIgnoreCase(this.object_id))
					isLookObjectScore=true;
				 
				if(this.plan_vo!=null && (this.plan_vo.getInt("object_type")==1 || this.plan_vo.getInt("object_type")==3 || this.plan_vo.getInt("object_type")==4))
				{
					if(isSelfGrade())  // 判断 当前评分人是不是团队的负责人
						isLookObjectScore=false;
				}				 
				if(isLookObjectScore)
					this.objectSelfCore=getSelfGradeValue();  //考核对象自我打分分值
			}
			/* 得到某计划考核主体给对象的评分结果hashMap */
			String[] temp={this.object_id," ",object_status};
			ArrayList objectList=new ArrayList();
			objectList.add(temp);
			HashMap perTableMap=new HashMap();
			if(this.opt!=null && "0".equals(this.opt)&& this.isShowObjectSelfScore)
			{
				if(this.plan_vo!=null && (this.plan_vo.getInt("object_type")==1 || this.plan_vo.getInt("object_type")==3 || this.plan_vo.getInt("object_type")==4))
				{	
					RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+this.plan_id+" and object_id='"+this.object_id+"' and body_id=-1");
					if(rowSet.next())
						perTableMap=getPerTableXXX(Integer.parseInt(this.plan_id),rowSet.getString("mainbody_id"),objectList);
				 	else
				 		perTableMap=getPerTableXXX(Integer.parseInt(this.plan_id),this.object_id,objectList);				
				}
				else
					perTableMap=getPerTableXXX(Integer.parseInt(this.plan_id),this.object_id,objectList);
			}
			else
				perTableMap=getPerTableXXX(Integer.parseInt(this.plan_id),this.userID,objectList);
			this.objectResultMap=(HashMap)perTableMap.get(this.object_id);
			
			this.userNumberResultMap=(HashMap)getUserNumberPointResultMap().get(this.object_id); // 得到考核对象拥有的考核指标
															
			
			// 放表头信息
			LazyDynaBean abean=null;
			for(int i=1;i<=(this.lay+1);i++)  // (this.lay+1)包含指标层
			{				 
				if(i!=(this.lay+1))
				{
					if((this.lay+1)==2)
					{
						// 就一层项目			
						abean=new LazyDynaBean();
						abean.set("itemid", "item_id");
						abean.set("itemdesc",ResourceFactory.getProperty("gz.formula.project"));
						abean.set("itemWidth",new Integer(td_width));						
						list.add(abean);
					}
					else
					{
						// 多层项目			
						abean=new LazyDynaBean();
						abean.set("itemid", "item_id");
						abean.set("itemdesc",ResourceFactory.getProperty("gz.formula.project")+i);
						abean.set("itemWidth",new Integer(td_width));
						list.add(abean);
					}
				}else
				{
					// 指标名称
					abean=new LazyDynaBean();
					abean.set("itemid", "point_id");			
					abean.set("itemdesc",ResourceFactory.getProperty("kq.wizard.target"));			
					abean.set("itemWidth",new Integer(td_width+150));
					list.add(abean);
				}
			}									
			total_width=this.lay*td_width+(td_width+150);
			
			if("True".equalsIgnoreCase(showIndicatorContent))
			{	
				// 考核内容
				abean=new LazyDynaBean();	
				abean.set("itemid", "kh_content");
				abean.set("itemdesc",ResourceFactory.getProperty("jx.khplan.khcontent"));					
				abean.set("itemWidth",new Integer(td_width));
				list.add(abean);		
				total_width+=(td_width);
			}
			if("True".equalsIgnoreCase(showIndicatorDegree))
			{
				// 标度说明
				abean=new LazyDynaBean();	
				abean.set("itemid", "scaleExplain");
				abean.set("itemdesc","标度说明");			
				abean.set("itemWidth",new Integer(td_width-40));
				list.add(abean);		
				total_width+=(td_width-40);								
			}
			if("True".equalsIgnoreCase(showIndicatorRole))
			{
				// 标准分
				abean=new LazyDynaBean();	
				abean.set("itemid", "score");
				abean.set("itemdesc","标准分");			
				abean.set("itemWidth",new Integer(td_width-50));
				list.add(abean);		
				total_width+=(td_width-50);
				
				// 评分原则
				abean=new LazyDynaBean();	
				abean.set("itemid", "gd_principle");
				abean.set("itemdesc",ResourceFactory.getProperty("performance.batchgrade.scoreCause"));			
				abean.set("itemWidth",new Integer(td_width+100));
				list.add(abean);		
				total_width+=(td_width+100);								
			}			 
			if("True".equalsIgnoreCase(allowSeeLowerGrade) && lowerGradeMainbodyList.size()>0)
			{
				for(int j=0;j<lowerGradeMainbodyList.size();j++)
				{
					LazyDynaBean a_bean=(LazyDynaBean)lowerGradeMainbodyList.get(j);
				//	String a0100=(String)a_bean.get("a0100");
					String body_name=(String)a_bean.get("bodyname");
					String _status=(String)a_bean.get("status");
						
			 		if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
						continue;
					String name="";
			 		if(this.plan_vo.getInt("plan_type")==0)
						name=body_name+"评分";
					else
						name=(String)a_bean.get("a0101")+"评分";
			 		
			 		// ***评分
					abean=new LazyDynaBean();	
					abean.set("itemid", "mainbody_score");
					abean.set("itemdesc",name);			
					abean.set("itemWidth",new Integer(td_width));
					list.add(abean);		
					total_width+=td_width;
			 		
					if("True".equalsIgnoreCase(showDeductionCause))///显示扣分原因
					{
						name=(String)a_bean.get("a0101")+"_评分说明";
						if(this.plan_vo.getInt("plan_type")==0)
							name=body_name+"_评分说明";
						
						// ***_评分说明
						abean=new LazyDynaBean();	
						abean.set("itemid", "mainbody_scoreExplain");
						abean.set("itemdesc",name);			
						abean.set("itemWidth",new Integer(td_width+50));
						list.add(abean);		
						total_width+=(td_width+50);
					}						 
				}
				this.allEvalResultMap=objectAllEvaluateMap();  // 取得 各考核主体打分结果
				
			}else if(isLookObjectScore)
			{
				//自我评分
				abean=new LazyDynaBean();
				abean.set("itemid", "self_score");
				abean.set("itemdesc",ResourceFactory.getProperty("performance.batchgrade.selfEvaluate"));			
				abean.set("itemWidth",new Integer(td_width));
				list.add(abean);		
				total_width+=td_width;									
				 
				if("True".equalsIgnoreCase(showDeductionCause) && !this.isShowObjectSelfScore)
				{
					//自我评分说明
					abean=new LazyDynaBean();	
					abean.set("itemid", "self_scoreExplain");
					abean.set("itemdesc","自我评分说明");			
					abean.set("itemWidth",new Integer(td_width+50));
					list.add(abean);		
					total_width+=(td_width+50);					
				}				 
			}
			
			ArrayList gradelist=getGradeDesc();  //标准标度;
			if(!this.isShowObjectSelfScore || "1".equals(this.opt))
			{				 
				/*if((DegreeShowType.equals("1") || DegreeShowType.equals("3")) && PointEvalType.equals("1")) //单选按钮的型式评分
				{					 
					LazyDynaBean bean=null;
					for(int i=0;i<gradelist.size();i++)
					{
						bean=(LazyDynaBean)gradelist.get(i);
						String grade_template_id=(String)bean.get("grade_template_id");
						String desc=(String)bean.get("gradedesc");
						 
						//标准标度
						abean=new LazyDynaBean();
						abean.set("itemid",grade_template_id);		
						abean.set("itemdesc",desc);			
						abean.set("itemWidth",new Integer(td_width));
						list.add(abean);		
						total_width+=td_width;	
					}
				}else
				{*/
					int value=(td_width+90);
					if("True".equalsIgnoreCase(showIndicatorRole))
						value=td_width; 
					// 评分
					abean=new LazyDynaBean();
					abean.set("itemid","pointGrade");	
					abean.set("itemdesc",ResourceFactory.getProperty("jx.evaluation.evaluation"));			
					abean.set("itemWidth",new Integer(value));
					list.add(abean);		
					total_width+=value;					 
				//}			 
			}
			if("True".equalsIgnoreCase(showDeductionCause))
			{
				// 评分说明
				abean=new LazyDynaBean();
				abean.set("itemid","scoreReason");	
				abean.set("itemdesc","评分说明");			
				abean.set("itemWidth",new Integer(td_width+50));
				list.add(abean);		
				total_width+=(td_width+50);				 
			}			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得下级考核主体的信息列表
	 * @param property // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
	 * @return
	 */
	public ArrayList getLowerGradeList()
	{
		ArrayList list=new ArrayList();
		try
		{
			int property=10;
			ContentDAO dao = new ContentDAO(this.con);
			String _str="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="level_o";
			String _sql="select per_mainbodyset."+_str+" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id"
					+" and plan_id="+Integer.parseInt(this.plan_id)+" and object_id='"+this.object_id+"' and mainbody_id='"+this.userID+"'";
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
			
			StringBuffer sql=new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
			sql.append(" and pm.plan_id="+Integer.parseInt(this.plan_id)+" and pm.object_id='"+this.object_id+"' and  ");
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
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}	
	/**
	 * 取得考核主体是考核对象的主体类别
	 * @return
	 */
	public String getMainBodyLevel()
	{
		String level="";
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet=null;
		try
		{
			String sql="select ";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o";
			else
				sql+=" level ";
			sql+=" from per_mainbodyset where body_id=(select body_id from per_mainbody where plan_id";
			sql+="="+this.plan_id+" and object_id='"+this.object_id+"' and mainbody_id='"+this.userID+"')";
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
	 * @return
	 */
	public boolean isSelfGrade()
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet=null;
		try
		{			 
			String _str="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="level_o";
			 
			rowSet=dao.search("select pm.* from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id and pm.plan_id="+this.plan_id+" " +
					         "and pm.object_id='"+this.object_id+"' and pms."+_str+"=5 and pm.mainbody_id='"+this.userID+"' ");
			if(rowSet.next())
				flag=true;
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 
	 * @Title: getGradeDescByPoint   
	 * @Description:    获取与指标标度匹配的标准标度
	 * @param @param point_id
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	public ArrayList getGradeDescByPoint(String point_id)
    {
    	ArrayList list=new ArrayList();
    	RowSet rowSet = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.con);
    		String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
    		rowSet = dao.search("select pc.* from "+per_comTable+" pc,per_grade pg where pg.point_id='"+point_id+"' and pg.gradecode=pc.grade_template_id order by pc.gradevalue desc");
    		LazyDynaBean abean=null;
    		while(rowSet.next())
    		{
    			abean=new LazyDynaBean();
    			abean.set("grade_template_id",rowSet.getString("grade_template_id"));
    			abean.set("gradevalue",rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"");
    			abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");
    			abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"");
    			abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"");
    			
    			list.add(abean);
    		}
    		if(rowSet!=null)
				rowSet.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
	
	/**
     * 获得标准标度
     * @return
     */
    public ArrayList getGradeDesc()
    {
    	ArrayList list=new ArrayList();
    	RowSet rowSet = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.con);
    		String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
    		rowSet = dao.search("select * from "+per_comTable+" order by gradevalue desc");
    		LazyDynaBean abean=null;
    		while(rowSet.next())
    		{
    			abean=new LazyDynaBean();
    			abean.set("grade_template_id",rowSet.getString("grade_template_id"));
    			abean.set("gradevalue",rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"");
    			abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");
    			abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"");
    			abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"");
    			
    			list.add(abean);
    		}
    		if(rowSet!=null)
				rowSet.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
            
    /**
     * 获取描述性评议项答案
     */
	public ArrayList getProAppraiseContentList()
	{
		ArrayList appList = new ArrayList();
		
		String extpro = this.getExtDesc();
		if (extpro == null || extpro.trim().length()<=0 || extpro.indexOf("<?xml") == -1)
		    return appList;

		try
		{
		    Document doc = PubFunc.generateDom(extpro);
		    String xpath = "//descriptive_evaluate";
		    XPath xpath_ = XPath.newInstance(xpath);
   	    
		    Element ele = (Element) xpath_.selectSingleNode(doc);		    
		    
		    if (ele != null)
		    {
				List list = (List) ele.getChildren("option");
				for (int i = 0; i < list.size(); i++)
				{
				    Element temp = (Element) list.get(i);	
				    if(temp.getText() != null && !"".equals(temp.getText()))
				    	appList.add(temp.getAttributeValue("id")+"`"+temp.getText());					
				}
		    }
		    
		    if(appList!=null)
		    	this.appContantList = appList;		    
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return appList;
	}
   
	/**
     * 取得描述性评议项答案
     */
	public String getExtDesc()
	{
		RowSet rs = null;
		String extpro = "";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("select description from per_mainbody where plan_id = '"+this.plan_id+"' ");
		strsql.append(" and object_id = '"+this.object_id+"'  ");
		strsql.append(" and mainbody_id = '"+this.userID+"' ");
						
		ContentDAO dao = new ContentDAO(this.con);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next())
		    {
				String temp = rs.getString(1);
				if (extpro != null)
				    extpro = temp;
		    }
		    if(rs!=null)
		    	rs.close();
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return extpro;
	}
    
    /**
     * 获取描述性评议项数据
     */
	public ArrayList getProAppraiseDesc()
	{
		ArrayList fieldlist = new ArrayList();
		String itemid = "";
		String extpro = this.getExtpro();
		if (extpro == null || extpro.trim().length()<=0)
		    return fieldlist;
		try
		{
		    Document doc = PubFunc.generateDom(extpro);
		    String xpath = "//descriptive_evaluate";
		    XPath xpath_ = XPath.newInstance(xpath);
   	    
		    Element ele = (Element) xpath_.selectSingleNode(doc);		    
		    
		    if (ele != null)
		    {
				List list1 = (List) ele.getChildren("option");
				LazyDynaBean abean = null;
				for (int i = 0; i < list1.size(); i++)
				{
				    Element temp = (Element) list1.get(i);
				   
				    abean = new LazyDynaBean();
	    			abean.set("id",temp.getAttributeValue("id"));
	    			abean.set("seq",temp.getAttributeValue("seq"));
	    			abean.set("value",temp.getText());
				    fieldlist.add(abean);
				    
				    itemid += "/"+temp.getAttributeValue("id");
				    
				/*
	    			FieldItem items = new FieldItem();
					items.setItemid(temp.getAttributeValue("id"));
					items.setItemdesc(temp.getText());
					items.setItemtype("A");
					items.setCodesetid("0");
					String contentStr = "";
					if(contentMap!=null && contentMap.get(temp.getAttributeValue("id"))!=null)
					{
						contentStr = (String)contentMap.get(temp.getAttributeValue("id"));
					}
					items.setValue(contentStr);
					items.setPriv_status(1);
	    			
					fieldlist.add(items);
				*/
				}
		    }
		    
		    if(itemid!=null && itemid.trim().length()>0)
		    	this.appitem_id = itemid.substring(1);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return fieldlist;
	}
   
	/**
     * 取得设置的描述性评议项xml内容
     */
	public String getExtpro()
	{
		RowSet rs = null;
		String extpro = "";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("select parameter_content from per_plan where plan_id=");
		strsql.append(this.plan_id);
						
		ContentDAO dao = new ContentDAO(this.con);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next())
		    {
				String temp = rs.getString(1);
				if (extpro != null)
				    extpro = temp;
		    }
		    if(rs!=null)
		    	rs.close();
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return extpro;
	}
    
    /**
	 * 取得 各考核主体打分结果
	 * @return HashMap
	 */
	public HashMap objectAllEvaluateMap()
	{
		HashMap map=new HashMap();
		HashMap allMap=new HashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);			
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();	
			String GradeClass=(String)htxml.get("GradeClass");					//等级分类ID	
			String NodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度
			String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位	
			String WholeEval=(String)htxml.get("WholeEval");			    //总体评价
			String WholeEvalMode = (String)htxml.get("WholeEvalMode");
			if("False".equalsIgnoreCase(WholeEval))
				WholeEvalMode = "0";
			String DescriptiveWholeEval=(String)htxml.get("DescriptiveWholeEval"); //显示描述性总体评价，默认为 True	
			String allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");// 允许查看下级对考核对象评分
			ArrayList lowerGradeMainbodyList = new ArrayList();  //下级主体列表
			if("True".equalsIgnoreCase(allowSeeLowerGrade))
			    lowerGradeMainbodyList=getLowerGradeList();  // 取得下级考核主体的信息列表
			
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			StringBuffer mainbodyids=new StringBuffer("");													
			String sql="select per_table_"+this.plan_id+".*,per_grade.gradedesc,"+per_comTable+".gradedesc desc2 from per_table_"+this.plan_id+" left join per_grade "
						+" on per_table_"+this.plan_id+".point_id=per_grade.point_id and per_table_"+this.plan_id+".degree_id=per_grade.gradecode "
						+" left join "+per_comTable+""
						+" on per_grade.gradecode="+per_comTable+".grade_template_id "
						+" where object_id='"+this.object_id+"' order by mainbody_id";
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
				ArrayList nodeKnowDegreeList=getExtendInfoValue("1","");				
				ArrayList wholeArrayList=getExtendInfoValue("2",GradeClass);
				rowSet=dao.search("select * from per_mainbody where mainbody_id in ("+mainbodyids.substring(1)+") and plan_id="+this.plan_id+" and object_id='"+this.object_id+"'");
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
												
						if("true".equalsIgnoreCase(NodeKnowDegree))  //了解程度
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
						if("true".equalsIgnoreCase(WholeEval) || "true".equalsIgnoreCase(DescriptiveWholeEval))   //总体评价
						{							
							String whole_id="";
							String whole_str="";
							if(rowSet.getString("whole_grade_id")!=null)
								whole_id=rowSet.getString("whole_grade_id");
							String a_bodyid=rowSet.getString("body_id");
								
							if("true".equalsIgnoreCase(WholeEval))
							{
								if("0".equals(WholeEvalMode)) {   // 2013.12.17 pjf
									for(int i=0;i<wholeArrayList.size();i++)
									{
										String[] temp=(String[])wholeArrayList.get(i);										
										if(temp[0].equals(whole_id))
											whole_str=temp[1];										
									}
								} else {
									String tempScore = PubFunc.round( rowSet.getString("whole_score"), Integer.parseInt(KeepDecimal));
									whole_str = tempScore;
								}
							} 
							if("true".equalsIgnoreCase(DescriptiveWholeEval))
							{
								whole_str+="&nbsp;&nbsp;<img  style=\"cursor:hand\"  src=\"/images/table.gif\"  ";
								whole_str+=" onclick=\"javascript:showWindow2('"+this.plan_id+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(this.object_id))+"','"+"~"+SafeCode.encode(PubFunc.convertTo64Base(a_mainbody_id))+"',"+a_bodyid+")\" >  ";
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
	 * @param flag	1:了解程度   2：总体评价
	 * @param gradeClass 等级分类ID
	 * @return
	 */
	public ArrayList getExtendInfoValue(String flag,String gradeClass)  throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet=null;
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
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 得到考核对象拥有的考核指标
	 * @param pointList 所有指标信息
	 * @return HashMap
	 */
	public HashMap getUserNumberPointResultMap()
    {
		HashMap map = new HashMap();
		StringBuffer sqll = new StringBuffer("");
		StringBuffer columns = new StringBuffer("");
		for (int i = 0; i < this.pointList.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean)this.pointList.get(i);
			String point_id = (String)bean.get("point_id");  // 指标ID
			
			if(point_id.indexOf("xxx")==-1)
			{
				String pointkind = (String)bean.get("pointkind");// 指标类型 0：定性 1：定量				
				String status = (String)bean.get("status");// 打分方式  =0（考核主体用对这个指标打分） =1（考核主体不用对这个指标打分） 默认值为1	
				
			    if ("1".equals(pointkind) && status != null && "1".equals(status) && "true".equalsIgnoreCase(this.showOneMark))
			    {
					sqll.append(",C_" + point_id);
					columns.append(",C_" + point_id);
			    }
			}
		}
		sqll.append(" from per_result_" + this.plan_id);
		ContentDAO dao = new ContentDAO(this.con);
		RowSet frowset = null;
		try
		{
		    if (columns.length() > 0)
		    {
				String[] columnArr = columns.substring(1).split(",");
				frowset = dao.search(" select object_id" + sqll.toString());
				while (frowset.next())
				{
				    HashMap userMap = new HashMap();
				    for (int i = 0; i < columnArr.length; i++)
				    {
						String aa = columnArr[i].substring(2);
						String str=frowset.getString(columnArr[i]) != null ? frowset.getString(columnArr[i]) : "";
						if(str.length()>0)
						{
							str=this.myformat.format(Double.parseDouble(str));
						}
						userMap.put(aa,str);
				    }
				    map.put(frowset.getString("object_id"), userMap);
				}
		    }
		    
		    if(frowset!=null)
		    	frowset.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}	
		return map;
    }
	/**
	 * 得到考评对象的自我评价分数
	 * @return HashMap
	 */	
	public HashMap getSelfGradeValue()
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet=null;
		try
		{			
			String sql="select * from per_table_"+this.plan_id+" where mainbody_id='"+this.object_id+"' and object_id='"+this.object_id+"' ";
			
			if(this.plan_vo!=null && (this.plan_vo.getInt("object_type")==1 || this.plan_vo.getInt("object_type")==3 || this.plan_vo.getInt("object_type")==4))
				sql="select * from per_table_"+this.plan_id+" where mainbody_id=(select mainbody_id from per_mainbody where plan_id="+this.plan_id+" and object_id='"+this.object_id+"' and body_id=-1 ) and object_id='"+this.object_id+"' ";
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
			sql="select know_id,whole_grade_id,status from per_mainbody where plan_id="+this.plan_id+" and mainbody_id='"+this.object_id+"' and object_id='"+this.object_id+"' ";
			if(this.plan_vo!=null && (this.plan_vo.getInt("object_type")==1 || this.plan_vo.getInt("object_type")==3 || this.plan_vo.getInt("object_type")==4))
				sql="select know_id,whole_grade_id,status from per_mainbody where plan_id="+this.plan_id+" and mainbody_id=(select mainbody_id from per_mainbody where plan_id="+this.plan_id+" and object_id='"+this.object_id+"' and body_id=-1 ) and object_id='"+this.object_id+"' ";
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				map.put("know_id",rowSet.getString(1));
				map.put("whole_grade_id",rowSet.getString(2));
				map.put("status",rowSet.getString(3));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	 /**
     * 得到某计划考核主体给对象的评分结果hashMap
     * @param mainbodyID 考核主体id
     * @param object_id 考核对象列表
     * @return HashMap
     */
	public HashMap getPerTableXXX(int plan_id, String mainbodyID, ArrayList object_id) throws GeneralException
	{	
		HashMap hashMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try
		{
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();	
			String performanceType=(String) htxml.get("performanceType"); // 考核形式 0：绩效考核 1：民主评测
			
		    DbWizard dbWizard = new DbWizard(this.con);
		    for (Iterator t = object_id.iterator(); t.hasNext();)
		    {
				String[] temp0 = (String[]) t.next();
				if ("1".equals(temp0[2]) || "2".equals(temp0[2]) || "3".equals(temp0[2])|| "8".equals(temp0[2]) || ("1".equals(performanceType) && ("4".equals(temp0[2]) || "7".equals(temp0[2]))))
				{
				    String objectid = temp0[0];
				    HashMap map = new HashMap();
				    String sql = "";
				    if (dbWizard.isExistTable("per_table_" + plan_id))
				    {
						sql = "select * from per_table_" + plan_id + "  where mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
						rowSet = dao.search(sql);
						int cols = rowSet.getMetaData().getColumnCount();
						while (rowSet.next())
						{
						    String[] temp = new String[cols];
						    // String[] temp=new String[8];
						    for (int i = 0; i < cols; i++)
						    {
								if (i == 3 || i==4)
								{
								    if (rowSet.getString(i + 1) != null)
								    {
										if(i == 3)
										    temp[i] = this.myformat.format(rowSet.getDouble(i + 1));
										if(i == 4)
										    temp[i] = PubFunc.round(Double.toString(rowSet.getDouble(i + 1)),1);
								    } else
								    	temp[i] = rowSet.getString(i + 1);
								}else
								    temp[i] = rowSet.getString(i + 1) != null ? rowSet.getString(i + 1) : "";
						    }
						    map.put(temp[5], temp);// temp[5]取得是point_id字段
						}			
				    }
				    sql = "select know_id,whole_grade_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
				    rowSet = dao.search(sql);
				    if (rowSet.next())
				    {
						map.put("know_id", rowSet.getString(1));
						map.put("whole_grade_id", rowSet.getString(2));
				    }
				    hashMap.put(objectid, map);
				}
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		
		return hashMap;
	}
	 /**
     * 取得模版下指标的最高标度
     */
	public HashMap getMaxPointValue()
	{	
		HashMap map = new HashMap();
		try
		{
		    ContentDAO dao = new ContentDAO(this.con);
		    String sql = "select ptp.point_id,ptp.score,ptp.rank,pp.pointkind,b.gradecode,b.gradevalue,b.top_value,b.bottom_value from per_template_item pti,per_template_point ptp,per_point pp,"
			    	   + " (select a.* from per_grade a where a.gradevalue=(select max(b.gradevalue) from per_grade b where a.point_id=b.point_id  ) )b"
			    	   + " where ptp.item_id=pti.item_id and pp.point_id=ptp.point_id and ptp.point_id=b.point_id  and pti.template_id='" + this.plan_vo.getString("template_id") + "' ";
		    RowSet rowSet = dao.search(sql);
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();
				String point_id = rowSet.getString("point_id");
				String score = rowSet.getString("score");
				String rank = rowSet.getString("rank");
				String pointkind = rowSet.getString("pointkind");
				String gradecode = rowSet.getString("gradecode");
				String gradevalue = rowSet.getString("gradevalue");
				String top_value = rowSet.getString("top_value");
				abean.set("score", score);
				abean.set("rank", rank);
				abean.set("pointkind", pointkind);
				abean.set("gradecode", gradecode);
				abean.set("gradevalue", gradevalue);
				abean.set("top_value", top_value);
				map.put(point_id, abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
	}
	 /**
     * 取得某考核计划下 主体 给考核对象 打分的分值
     * @return
     */
	public float getObjectTotalScore(String mainbody_id)
	{	
		double score = 0d;
		String avalue="";
		ContentDAO dao = new ContentDAO(this.con);
		try
		{	
			Hashtable htxml=new Hashtable();							
			htxml=this.loadxml.getDegreeWhole();
			String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位	
			
		    String sql="select * from per_table_"+this.plan_id+" where  mainbody_id='"+mainbody_id+"' and object_id='"+this.object_id+"'";
		    RowSet rowSet2=dao.search(sql);
		    String a_score="0";
		    String pointid="";
		    while(rowSet2.next())
		    {
		    	pointid=rowSet2.getString("point_id").toLowerCase();
		    	if(pointMap.get(pointid)!=null)
		    	{
		    		LazyDynaBean abean = (LazyDynaBean) pointMap.get(pointid);
		    		String rank = (String) abean.get("rank");
		    		String point_id = (String) abean.get("point_id");
		    		String arank = getRankByObjectID(rank, this.object_id, point_id);
		    		String pointScore="0";
		    		if(rowSet2.getString("score")!=null)
		    			pointScore=rowSet2.getString("score");
		    		pointScore=PubFunc.multiple(pointScore,arank, Integer.parseInt(KeepDecimal));
		    		a_score=PubFunc.add(a_score,pointScore, Integer.parseInt(KeepDecimal));
		    	}
		    }
		    score=Double.parseDouble(a_score);
		  
		    //将统一打分定量指标的值加进来
		    ArrayList apointList = new ArrayList();
		    ArrayList aapointList=new ArrayList();
		    Set keySet=pointMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
		    {
				String key=(String)t.next();
			    LazyDynaBean abean = (LazyDynaBean) pointMap.get(key);
			    String[] aa = new String[8];
			    aa[0] = (String) abean.get("point_id");
			    aa[2] = (String) abean.get("pointkind");
			    aa[7] = (String) abean.get("status");
			    apointList.add(aa);
			    if ("1".equals(aa[2]) && aa[7] != null && "1".equals(aa[7]))
					aapointList.add(aa[0]);
			}						
			for (int i = 0; i < aapointList.size() && this.userNumberResultMap!=null; i++)
			{
			    String point_id = (String) aapointList.get(i);
			    if (this.userNumberResultMap.get(point_id) != null && !"".equals((String) this.userNumberResultMap.get(point_id)))
			    {
			    	score += Double.parseDouble((String) this.userNumberResultMap.get(point_id));
			    }
			}
			avalue = PubFunc.round(String.valueOf(score), Integer.parseInt(KeepDecimal));	    
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return Float.parseFloat(avalue);
	}
    /**
     * 得到对象对于某指标的动态权重
     * @return
     */
	public String getRankByObjectID(String defaultRank, String objectID, String pointid)
	{		
		String a_rank = "";
		HashMap objectInfoMap = getObjectInfoMap();
		HashMap dynaRankInfoMap = getDynaRankInfoMap();
		LazyDynaBean objectInfo = (LazyDynaBean)objectInfoMap.get(objectID);
		ArrayList perDynaRankList = (ArrayList)dynaRankInfoMap.get(pointid);
		if (objectInfo == null || perDynaRankList == null)
		    return defaultRank;
		
		String b0110 = (String) objectInfo.get("b0110");
		String e0122 = (String) objectInfo.get("e0122"); // 部门
		String e01a1 = (String) objectInfo.get("e01a1"); // 岗位
		String body_id = (String) objectInfo.get("body_id"); // 考核对象类别
		
		//  人>考核对象类别>组织机构节点
		if(this.objDynaRankMap.get(pointid+"_"+objectID)!=null)
			a_rank = (String)this.objDynaRankMap.get(pointid+"_"+objectID); // 人
		else if(this.objDynaRankMap.get(pointid+"_"+body_id)!=null)
			a_rank = (String)this.objDynaRankMap.get(pointid+"_"+body_id); // 考核对象类别
		else if(this.objDynaRankMap.get(pointid+"_"+e01a1)!=null)
			a_rank = (String)this.objDynaRankMap.get(pointid+"_"+e01a1); // 岗位
		else if(this.objDynaRankMap.get(pointid+"_"+e0122)!=null)
			a_rank = (String)this.objDynaRankMap.get(pointid+"_"+e0122); // 部门
		else if(this.objDynaRankMap.get(pointid+"_"+b0110)!=null)
			a_rank = (String)this.objDynaRankMap.get(pointid+"_"+b0110); // 单位
		
		/*
		for (int i = 0; i < perDynaRankList.size(); i++)
		{
		    LazyDynaBean abean = (LazyDynaBean) perDynaRankList.get(i);
		    String dyna_obj_type = (String) abean.get("dyna_obj_type");
		    String dyna_obj = (String) abean.get("dyna_obj");
		    String rank = (String) abean.get("rank");
		    if (dyna_obj_type.equals("4")) // 对象id
		    {
				if (objectID.equals(dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
		    }else if (dyna_obj_type.equals("5")) // 考核对象类别
		    {
				if (body_id.equals(dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
		    }else if (dyna_obj_type.equals("3")) // 职位
		    {
				if (isFitObj(e01a1, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}		
		    }else if (dyna_obj_type.equals("2")) // 部门
		    {
				if (isFitObj(e0122, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}			
		    }else if (dyna_obj_type.equals("1")) // 单位
		    {
				if (isFitObj(b0110, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}		
		    }		
		}
		*/
		
		if ("".equals(a_rank))
		    a_rank = defaultRank;
		return a_rank;
	}	
	public boolean isFitObj(String obj, String obj2)
	{		
		boolean flag = false;
		if (obj == null || "".equals(obj))
		    return flag;
		else if (obj.trim().length() == obj2.trim().length())
		{
		    if (obj.equals(obj2))
		    	flag = true;
		} else if (obj.trim().length() > obj2.trim().length())
		{
		    if (obj.substring(0, obj2.length()).equals(obj2))
		    	flag = true;
		}
		return flag;
	}
	public HashMap getObjectInfoMap()
    {
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try
		{
		    rowSet = dao.search("select per_object.*,per_plan.object_type from per_object,per_plan  where per_object.plan_id=per_plan.plan_id and per_object.plan_id=" + this.plan_id);
		    while (rowSet.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("object_id", rowSet.getString("object_id"));
				String b0110 = rowSet.getString("b0110") != null ? rowSet.getString("b0110") : "";
				String e0122 = rowSet.getString("e0122") != null ? rowSet.getString("e0122") : "";
				String e01a1 = rowSet.getString("e01a1") != null ? rowSet.getString("e01a1") : "";
				String body_id = rowSet.getString("body_id") != null ? rowSet.getString("body_id") : "";
				abean.set("b0110", b0110);
				abean.set("e0122", e0122); // 部门
				abean.set("e01a1", e01a1); // 岗位
				abean.set("body_id", body_id); // 考核对象类别
				abean.set("object_type", rowSet.getString("object_type")); // 2:人员   // 1：部门
		
				map.put(rowSet.getString("object_id"), abean);
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
    }
	/**
     * 得到动态权重表里的信息
     * @return map (要素号：List)
     */
	public HashMap getDynaRankInfoMap()
	{	
		HashMap map = new HashMap();
		HashMap rangkMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		try
		{
		    String point_id = "";
		    ArrayList tempList = new ArrayList();
		    rowSet = dao.search("select * from per_dyna_rank where plan_id=" + this.plan_id + " order by point_id,dyna_obj_type desc,dyna_obj desc");
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();
				String a_point_id = rowSet.getString("point_id");
				if(!a_point_id.equals(point_id) && !"".equals(point_id))
				{
				    map.put(point_id, tempList);
				    tempList = new ArrayList();
				}
				abean.set("point_id", rowSet.getString("point_id"));
				abean.set("plan_id", rowSet.getString("plan_id"));
				abean.set("dyna_obj_type", rowSet.getString("dyna_obj_type"));
				abean.set("dyna_obj", rowSet.getString("dyna_obj"));
				abean.set("rank", rowSet.getString("rank"));
				tempList.add(abean);
				rangkMap.put(rowSet.getString("point_id")+"_"+rowSet.getString("dyna_obj"),Double.toString(rowSet.getDouble("rank")));
				
				point_id = a_point_id;
		    }
		    map.put(point_id, tempList);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}	
		this.setObjDynaRankMap(rangkMap);
		return map;
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
	
	
	public boolean isShowObjectSelfScore() 
	{
		return isShowObjectSelfScore;
	}
	public void setShowObjectSelfScore(boolean isShowObjectSelfScore) 
	{
		this.isShowObjectSelfScore = isShowObjectSelfScore;
	}
	public String getOpt() 
	{
		return opt;
	}
	public void setOpt(String opt) 
	{
		this.opt = opt;
	}
	public String getFillctrl() {
		return fillctrl;
	}
	public void setFillctrl(String fillctrl) {
		this.fillctrl = fillctrl;
	}
	public String getFromModel() {
		return fromModel;
	}
	public void setFromModel(String fromModel) {
		this.fromModel = fromModel;
	}
	public HashMap getObjDynaRankMap() {
		return objDynaRankMap;
	}
	public void setObjDynaRankMap(HashMap objDynaRankMap) {
		this.objDynaRankMap = objDynaRankMap;
	}
	public String getFillTableExplain() {
		return fillTableExplain;
	}
	public void setFillTableExplain(String fillTableExplain) {
		this.fillTableExplain = fillTableExplain;
	}
	public String getIsHasSaveButton() {
		return isHasSaveButton;
	}
	public void setIsHasSaveButton(String isHasSaveButton) {
		this.isHasSaveButton = isHasSaveButton;
	}

	public String getAppitem_id() {
		return appitem_id;
	}

	public void setAppitem_id(String appitem_id) {
		this.appitem_id = appitem_id;
	}

	public ArrayList getAppContantList() {
		return appContantList;
	}

	public void setAppContantList(ArrayList appContantList) {
		this.appContantList = appContantList;
	}
	
}
