package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class singleGradeBo_new 
{
	
	private Connection conn=null;
	private RecordVo plan_vo=null;
	private RecordVo template_vo=null;
	private String templateid="";
	private UserView userView=null;
	
	private Hashtable planParam=null;
	private LazyDynaBean objectBean=null;
	private HashMap objectEvaluateValueMap=new HashMap(); //考核对象评估结果
	private HashMap pointDescMap=new HashMap();   //考核指标标度列表
	private String  perPointNoGrade="0";          //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
	private ArrayList templateItemList=new ArrayList();  //模板项目记录表
	private ArrayList pointList=new ArrayList();
	private ArrayList leafItemList=new ArrayList();      //叶子项目列表
	private HashMap   itemToPointMap=new HashMap();      //项目对应任务map
	private HashMap    leafItemLinkMap=new HashMap();     //叶子项目对应的继承关系
	private HashMap itemPointNum=new HashMap();
	private int lay=0;
	private StringBuffer extendtHead=new StringBuffer("");
	private int td_width=160;
	private int td_height=30;
	private HashMap pointPrivMap=new HashMap();   //主体对应的考核指标权限
	
	public singleGradeBo_new(Connection con,String planid, UserView userView)
	{
		
		this.conn=con;
		this.plan_vo=getPlanVo(planid);
		this.template_vo=get_TemplateVo(this.plan_vo.getString("template_id"));
		this.templateid=this.plan_vo.getString("template_id");
		this.userView=userView;
		LoadXml loadxml=new LoadXml(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
		this.planParam=loadxml.getDegreeWhole();
		//System.out.println("TTTTTT="+this.planParam);
	}
	
	
	/**
	 * 取得计划下的考核对象
	 * @param plan_id
	 * @return
	 */
	public  ArrayList getObjects(String plan_id)
	{
		ArrayList list=new ArrayList();
		
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_object where  plan_id="+plan_id+" order by a0000,b0110,e0122");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("e0122",AdminCode.getCodeName("UM",rowSet.getString("e0122")));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("a0101",rowSet.getString("a0101"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
//	取得考核主体信息
	public LazyDynaBean getPerMainbodyBean(String objectId,String mainbody_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+this.plan_vo.getInt("plan_id")+" and  object_id='"+objectId+"' and mainbody_id='"+mainbody_id+"' ");
			if(rowSet.next())
			{
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("e0122",rowSet.getString("e0122"));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("fillctrl",rowSet.getString("fillctrl")!=null?rowSet.getString("fillctrl"):"0");
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	//取得考核对象信息
	public LazyDynaBean getObjectBean(String objectId)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_object where object_id='"+objectId+"'  and plan_id="+this.plan_vo.getInt("plan_id"));
			if(rowSet.next())
			{
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("e0122",rowSet.getString("e0122"));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("a0101",rowSet.getString("a0101"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	//取得计划信息
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
		return vo;
	}
	
    //	取得模版信息
	public RecordVo get_TemplateVo(String templateID)
	{
		RecordVo vo=new RecordVo("per_template");
		try
		{
			vo.setString("template_id",templateID);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	
	
	
	
	public String getGradeCardHtml(String object_id,String mainbody_id)
	{
		String html="";
		try
		{
			BatchGradeBo bo=new BatchGradeBo(this.conn);
			 HashMap _pointPrivMap= new HashMap();
			 if(mainbody_id.trim().length()>0) {
                 _pointPrivMap = bo.getPointprivMap(String.valueOf(this.plan_vo.getInt("plan_id")), mainbody_id); // 得到指标权限信息
             } else//考核主体为空 设置当前考核主体权限为无
             {
                 _pointPrivMap = bo.getPointprivMap2(String.valueOf(this.plan_vo.getInt("plan_id")), object_id); // 得到指标权限信息
             }
			 pointPrivMap=(HashMap)_pointPrivMap.get(object_id);		   					  //得到具有某考核对象的指标权限map	
			
			this.objectBean=getObjectBean(object_id);			
			this.objectEvaluateValueMap=getObjectEvaluateValueMap(object_id,mainbody_id);   //取得考核对象评估结果
			this.pointDescMap=getTemplatePointDetail(true);			   //取得模板引入的绩效指标标度
			this.templateItemList=getTemplateItemList();
			get_LeafItemList();
			this.pointList=getPointList();
			this.itemToPointMap=getItemToPointMap();
			this.leafItemLinkMap=getLeafItemLinkMap();
			this.itemPointNum=getItemPointNum();
			html=writeHtml(object_id,mainbody_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html;
	}
	
	
	
	
	/**
	 * 写html
	 * @return
	 */
	public String writeHtml(String object_id,String mainbody_id)
	{
		
		StringBuffer html=new StringBuffer("");
		StringBuffer htmlContext=new StringBuffer("");
		try
		{
		
			HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			ArrayList _pointList=batchGradeBo.getPerPointList(this.plan_vo.getString("template_id"),String.valueOf(this.plan_vo.getInt("plan_id")));	
			
			
			
			 //输出表头
			extendtHead.append("<tr class='trDeep_self'  height='30' >\r\n");
			extendtHead.append("<td class='TableRow_2rows' nowrap valign='middle' align='center'  colspan='"+this.lay+"'>&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("train.job.itemName")+"&nbsp;&nbsp;&nbsp;&nbsp;</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' nowrap valign='middle' align='center' >&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.wizard.target")+"&nbsp;&nbsp;&nbsp;&nbsp;</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' nowrap  valign='middle' align='center' >&nbsp;&nbsp;"+ResourceFactory.getProperty("kh.field.scorevalue")+"&nbsp;&nbsp;</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'   valign='middle' align='center' >"+(this.objectBean.get("a0101")==null?"考核对象":(String)this.objectBean.get("a0101"))+"</td>\r\n");
			
			int rowNum=0;
			
			
			/*
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
				String item_kind=(String)abean.get("kind");
				for(int j=0;j<num;j++)
				{
					htmlContext.append("<tr>\r\n");
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					for(int e=linkParentList.size()-1;e>=0;e--)
					{
						a_bean=(LazyDynaBean)linkParentList.get(e);
						String itemid=(String)a_bean.get("item_id");
						if(existWriteItem.get(itemid)!=null)
							continue;
						existWriteItem.put(itemid,"1");
						String itemdesc=(String)a_bean.get("itemdesc");
						htmlContext.append(writeTd(itemdesc,((Integer)itemPointNum.get(itemid)).intValue(),"left",this.td_width));
						
						
						
						
						
					}
					
					htmlContext.append(writePointGrid(current,pointList,j));
					if(pointList==null)
						htmlContext.append(getExtendTd(null));
					else
						htmlContext.append(getExtendTd((LazyDynaBean)pointList.get(j)));
					htmlContext.append("</tr>\r\n");
				}
			}
			*/
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			ArrayList list=singleGradeBo.getPerformanceStencilList(this.plan_vo.getString("template_id"));
			ArrayList items=(ArrayList)list.get(0);				//模版项目列表(按顺序)
			HashMap    map=(HashMap)list.get(2);				//各项目包含的指标个数
			HashMap    subItemMap=(HashMap)list.get(3);			//各项目的子项目(hashmap)		
			HashMap itemsSignMap=singleGradeBo.getItemsSignMap(items);
			
			 ArrayList a_pointList=(ArrayList)_pointList.get(1);             //指标集 
			 HashMap   pointItemMap=singleGradeBo.getPointItemList((ArrayList)_pointList.get(1),items);	
			 int a_no=1;
			 String  showOneMark=(String)planParam.get("ShowOneMark");  //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] point=(String[])t.next();
				 ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
				 int a_lay=lay;
				 htmlContext.append("<tr>");
				 
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
							 htmlContext.append(writeTd(item[3],Integer.parseInt((String)map.get(item[0])),"left",this.td_width));
							 currentItem=item;
							 itemsSignMap.put(item[0],"1");
						 }
					 }
					 else 
					 {
						 if(--pointItemLength>=0)
						 {
						 
							 String[] item=(String[])pointItemList.get(pointItemLength);
							 String sign=(String)itemsSignMap.get(item[0]);
							 if(!"1".equals(sign))
							 {					
								 htmlContext.append(writeTd(item[3],Integer.parseInt((String)map.get(item[0])),"left",this.td_width));								
								 currentItem=item;
								 itemsSignMap.put(item[0],"1");						
							 }
						 }
						 else
						 {
							 htmlContext.append(writeTd("&nbsp;",1,"left",this.td_width));
						 }					 
					 }
				 
				 }
				 
				 LazyDynaBean _abean=null;
				 for(int i=0;i<this.pointList.size();i++)
				 {
						_abean=(LazyDynaBean)this.pointList.get(i);
						String point_id=(String)_abean.get("point_id");
						if(point_id.equalsIgnoreCase(point[0]))
						{
							break;
						}
						else {
                            _abean=null;
                        }
				 }
				 if(_abean==null) {
                     htmlContext.append(writeTd("&nbsp;",0,"left",this.td_width));
                 } else {
                     htmlContext.append(writePointTd((String)_abean.get("pointname"),(String)_abean.get("point_id"),"left",this.td_width));
                 }
				 htmlContext.append(writeTd((String)_abean.get("score"),0,"right",80));
				 if(_abean==null) {
                     htmlContext.append(getExtendTd(null));
                 } else {
                     htmlContext.append(getExtendTd(_abean));
                 }
					htmlContext.append("</tr>\r\n");
				 a_no++;
			 }
			
			int width=(this.lay+2)*this.td_width;
			int height=rowNum*this.td_height;
			StringBuffer titleHtml=new StringBuffer("<table style='background-color:#FFF;' cellpadding='5'  class='ListTable_self' width='"+width+"' height='"+height+"' >");
			html.append(titleHtml.toString());
			extendtHead.append("</tr>\r\n");
			html.append(extendtHead.toString());
			html.append(htmlContext.toString());
			
			 /* 了解程度 && 总体评价 */
			 String NodeKnowDegree=(String)planParam.get("NodeKnowDegree");	    //了解程度
			 String WholeEval=(String)planParam.get("WholeEval");			    //总体评价
			 String GradeClass=(String)planParam.get("GradeClass");					//等级分类ID
		//	 BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			 
			 ArrayList  nodeKnowDegreeList=new ArrayList();
			 ArrayList  wholeEvalList=new ArrayList();
			 if("true".equals(NodeKnowDegree))
			 {
					nodeKnowDegreeList=batchGradeBo.getExtendInfoValue("1","");			
			 }
			 if("true".equals(WholeEval))
			 {
					wholeEvalList=batchGradeBo.getExtendInfoValue("2",GradeClass);				
			 }
			 /* 是否有了解程度 */
			 String select_id=" ";
			 if("true".equals(NodeKnowDegree))
			 {
					if(this.objectEvaluateValueMap.get("know_id")!=null) {
                        select_id=(String)this.objectEvaluateValueMap.get("know_id");
                    }
					html.append(getExtendTd(nodeKnowDegreeList,select_id,lay+2,ResourceFactory.getProperty("lable.statistic.knowdegree"),"konwDegree"));
			 }
			 /* 是否有总体评价 */
			 select_id=" ";
			 if("true".equals(WholeEval))
			 {
					if(this.objectEvaluateValueMap.get("whole_grade_id")!=null) {
                        select_id=(String)this.objectEvaluateValueMap.get("whole_grade_id");
                    }
					html.append(getExtendTd(wholeEvalList,select_id,lay+2,ResourceFactory.getProperty("lable.statistic.wholeeven"),"wholeEval"));
				
			 }
			 
			html.append("<tr class='RecordRow_self complex_border_color'  height='25' >\r\n");
			html.append("<td class='RecordRow_self complex_border_color'  valign='middle' align='center'  colspan='"+(this.lay+1)+"'>"+ResourceFactory.getProperty("label.kh.template.total")+"</td>\r\n");
			html.append("<td class='RecordRow_self complex_border_color'   valign='middle' align='center' >"+this.template_vo.getDouble("topscore")+"</td>\r\n");
			
			DecimalFormat myformat1 = new DecimalFormat("########.###");//
			float score= batchGradeBo.getObjectTotalScore(this.plan_vo.getInt("plan_id"),mainbody_id,this.templateid,object_id,this.userView);
			html.append("<td class='RecordRow_self complex_border_color'   valign='middle' align='center' id='totalScore'  >"+myformat1.format(score)+"</td>\r\n");
			html.append("</tr>");
			
			html.append("</table>");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}
	
	
	
	/**
	 * 生成 扩展的评分选项
	 * @param list   选项信息
	 * @return
	 */
	public String getExtendTd(ArrayList list,String selectid,int lay,String name,String controlName)
	{
		StringBuffer   td=new StringBuffer("<tr  class='RecordRow_self'  height='25' > <td class='RecordRow_self complex_border_color' align='center'  colspan='"+lay+"'    nowrap >");
		td.append(name);
		td.append("</td>");
		
		
		td.append("<td class='RecordRow_self complex_border_color'  align='left' >");
		td.append("<select name='"+controlName+"'  >");
		td.append("<option value=''></option>");
		for(int i=0;i<list.size();i++)
		{
			String[] temp=(String[])list.get(i);
			td.append("<option value='");
			td.append(temp[0]);
			td.append("' ");
			if(temp[0].equals(selectid)) {
                td.append("selected");
            }
			td.append(" >");
			td.append(temp[1]);
			td.append("</option>");
		}	
		td.append("</select>");
		td.append("</td></tr>");
		return td.toString();
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 取得评估单元格
	 * @return
	 */
	public String getExtendTd(LazyDynaBean point_bean)
	{
		StringBuffer ext_html=new StringBuffer("");
		if(point_bean!=null)
		{
			StringBuffer temp_str=new StringBuffer("");
			LazyDynaBean valueBean=null;    //标度值
			String point_id=(String)point_bean.get("point_id");
			if(this.objectEvaluateValueMap.get(point_id)!=null) {
                valueBean=(LazyDynaBean)this.objectEvaluateValueMap.get(point_id);
            }
			ArrayList pointGradeList=(ArrayList)this.pointDescMap.get(point_id);
			
			String pointkind="0"; 
			String status="";
			if(pointGradeList!=null&&pointGradeList.size()>0)
			{
				LazyDynaBean abean=(LazyDynaBean)pointGradeList.get(0);
				pointkind=(String)abean.get("pointkind");           // 0:定性  1:定量
				status=(String)abean.get("status");
			}
			else {
                perPointNoGrade="1";
            }
			String scoreflag=(String)this.planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合) =4打分按加扣分处理
			String gather_type =this.plan_vo.getString("gather_type");//0 网上 1 机读 2:网上+机读

			if("0".equals(gather_type))
			{
				if (("4".equals(scoreflag) || "2".equals(scoreflag)) || "1".equals(pointkind)) {

					temp_str.append("\r\n<input type='text' name='p_" + point_id+ "'  size=5  onblur='validateValue(this,\"null\")' ");
					
					
					String  showOneMark=(String)planParam.get("ShowOneMark");  //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False &&showOneMark.equalsIgnoreCase("true")
					if("1".equals(pointkind)&& "1".equals(status)) {
                        temp_str.append(" disabled='false'");
                    } else if("0".equals((String)this.pointPrivMap.get(point_id))) {
                        temp_str.append(" disabled='false'");
                    } else if("4".equals(scoreflag)) {
                        temp_str.append(" disabled='false'");
                    }
					
					if (valueBean != null) {
						if ("1".equals(pointkind)) {
						   String amount="";
						   if(valueBean.get("amount")!=null && ((String)valueBean.get("amount")).trim().length()>0 && Float.parseFloat((String)valueBean.get("amount"))!=0) {
                               amount=(String) valueBean.get("amount");
                           }
							temp_str.append(" value='"+amount+ "' ");
						} else if ("0".equals(pointkind)) {
							String score="";
							if(valueBean.get("score")!=null&&((String)valueBean.get("score")).trim().length()>0&&Float.parseFloat((String)valueBean.get("score"))!=0) {
                                score=(String) valueBean.get("score");
                            }
							temp_str.append(" value='"+score+ "' ");
						}
					}
					temp_str.append(" />");

				}
				else if("1".equals(scoreflag)&& "0".equals(pointkind))
				{
					temp_str.append("\r\n<select name='p_" + point_id + "' ");
					
					if((this.pointPrivMap.get(point_id)!=null) && "0".equals((String)this.pointPrivMap.get(point_id))) {
                        temp_str.append(" disabled='false'");
                    }
					temp_str.append(" >");
					String a_value = "";
					if (valueBean != null) {
                        a_value = (String) valueBean.get("degree_id");
                    }
					temp_str.append("\r\n<option value=''></option>");
					if(pointGradeList!=null)
					{
						for (int i = 0; i < pointGradeList.size(); i++) {
							LazyDynaBean a_bean = (LazyDynaBean) pointGradeList.get(i);
							String gradecode = (String) a_bean.get("gradecode");
							String gradedesc = (String) a_bean.get("desc2");
							if("2".equals(this.planParam.get("DegreeShowType"))) {
                                gradedesc=(String)a_bean.get("gradedesc");
                            }
							temp_str.append("\r\n<option value='" + gradecode + "' ");
							if (a_value.equals(gradecode)) {
                                temp_str.append("selected");
                            }
							temp_str.append(" >" + gradedesc + "</option>");
						}
					}
					temp_str.append("</select>");

				}
			}else if(!"0".equals(gather_type))			//如果是机读计划默认按标度显示 但是支持录入分值和标度两种方式 所以做成文本框的方式显示标度
			{
				temp_str.append("\r\n<input type='text' name='p_" + point_id+ "'  size=5 onfocus='saveBeforeVali(this.value)'  onblur='saveMathineScores(this)' ");
				
				
				String  showOneMark=(String)planParam.get("ShowOneMark");  //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False &&showOneMark.equalsIgnoreCase("true")
				if("1".equals(pointkind) && "1".equals(status)) {
                    temp_str.append(" disabled='false'");
                } else if("0".equals((String)this.pointPrivMap.get(point_id))) {
                    temp_str.append(" disabled='false'");
                }
				String a_value = "";
				if (valueBean != null) {
                    a_value = (String) valueBean.get("degree_id");
                }
				temp_str.append(" value='"+a_value+ "' ");
				temp_str.append(" />");
			}

			ext_html.append(writeTd(temp_str.toString(),0,"center",80));
			
			
		}
		else {
            ext_html.append(writeTd("&nbsp;",0,"center",80));
        }
		return ext_html.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 画空格　和　指标格
	 * @param current
	 * @param pointList
	 * @param j
	 * @return
	 */
	public String writePointGrid(int current,ArrayList pointList,int j)
	{
		StringBuffer tempHtml=new StringBuffer("");
		LazyDynaBean point_bean=null;
		for(int e=current;e<this.lay;e++) {
            tempHtml.append(writeTd("&nbsp;",1,"left",this.td_width));
        }
		
		if(pointList==null) {
            tempHtml.append(writeTd("&nbsp;",0,"left",this.td_width));
        } else
		{ 
			point_bean=(LazyDynaBean)pointList.get(j);
			tempHtml.append(writePointTd((String)point_bean.get("pointname"),(String)point_bean.get("point_id"),"left",this.td_width));
		}
		  
		point_bean=(LazyDynaBean)pointList.get(j);
		tempHtml.append(writeTd((String)point_bean.get("score"),0,"right",80));
		return tempHtml.toString();
	}
	
	
	private String writePointTd(String context,String pointid,String align,int width)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordRow_self complex_border_color'  valign='middle'  id='"+pointid+"'  align='"+align+"' ");
		td.append(" height='"+td_height+"' ");
		td.append("  width='"+width+"'");
		td.append("  onclick='showDateSelectBox(this);'  onmouseout='hidden1()' >  ");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}
	
	
	
	
	private String writeTd(String context,int rowspan,String align,int width)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordRow_self complex_border_color'  valign='middle' align='"+align+"' ");
		if(rowspan!=0) {
            td.append(" rowspan='"+rowspan+"' ");
        } else {
            td.append(" height='"+td_height+"' ");
        }
		td.append("  width='"+width+"'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
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
			boolean isSelf=false;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(item_id.equalsIgnoreCase((String)a_bean.get("item_id"))) {
                    isSelf=true;
                }
				if(itemToPointMap.get(item_id)!=null) {
                    n+=((ArrayList)itemToPointMap.get(item_id)).size();
                } else {
                    n+=1;
                }
			}
			if(!isSelf)
			{
				if(itemToPointMap.get((String)a_bean.get("item_id"))!=null) {
                    n+=((ArrayList)itemToPointMap.get((String)a_bean.get("item_id"))).size();
                }
			}
			
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
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
				a_bean=(LazyDynaBean)this.templateItemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    getLeafItemList(a_bean,list);
                }
		}
		
	}
	
	
	
	
	
	
	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public  HashMap getLeafItemLinkMap()
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				String parent_id=(String)abean.get("parent_id");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean);
				if(linkList.size()>lay) {
                    lay=linkList.size();
                }
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
		String item_id=(String)abean.get("item_id");
		String parent_id=(String)abean.get("parent_id");
		if(parent_id.length()==0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,a_bean);
			}
			
		}
		
		
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得项目对应指标map
	 * @return
	 */
	public HashMap getItemToPointMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean abean=null;
			for(int i=0;i<this.pointList.size();i++)
			{
				abean=(LazyDynaBean)this.pointList.get(i);
				
				String point_id=(String)abean.get("point_id");
				String item_id=(String)abean.get("item_id");
				if(map.get(item_id)!=null)
				{
					ArrayList tempList=(ArrayList)map.get(item_id);
					tempList.add(abean);
					map.put(item_id,tempList);
				}
				else
				{
					ArrayList tempList=new ArrayList();
					tempList.add(abean);
					map.put(item_id,tempList);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**
	 * 取得模板指标列表
	 * @return
	 */
	public ArrayList getPointList()
	{
		DecimalFormat myformat1 = new DecimalFormat("##########.#####");
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,pp.score,pp.rank  from per_template_item pi,per_template_point pp,per_point po "
					+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+this.plan_vo.getString("template_id")+"'  order by pp.seq");	  //pi.seq,
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("point_id",rowSet.getString("point_id"));
				abean.set("pointname",rowSet.getString("pointname"));
				abean.set("pointkind",rowSet.getString("pointkind"));
				abean.set("item_id",rowSet.getString("item_id"));
				
				double score = rowSet.getDouble("score");								
				abean.set("score",String.valueOf(score));
				
				String rank = (String)rowSet.getString("rank")!=null? (String)rowSet.getString("rank"):"";
		    	if(rank!=null && rank.trim().length()>0) {
                    rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0
                }
		    	abean.set("rank",rank);	
				
//				abean.set("rank",rowSet.getString("rank"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 移除小数点后面的零
	 * @param number
	 */
	public String moveZero(String number)
	{
		DecimalFormat df = new DecimalFormat("###############.#####"); 
		if(number==null||number.trim().length()==0) {
            return "";
        }
		return df.format(Double.parseDouble(number));
	}
	

	/**
	 * 叶子项目列表
	 *
	 */
	public void get_LeafItemList()
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
	
   //	递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		if(child_id.length()==0)
		{
			this.leafItemList.add(abean);
				return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
				a_bean=(LazyDynaBean)this.templateItemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    setLeafItemFunc(a_bean);
                }
		}
	}
	
	
	
	
	
	
	
	/**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList()
	{
		DecimalFormat myformat1 = new DecimalFormat("##########.#####");
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);			
			RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+this.plan_vo.getString("template_id")+"'  order by seq");
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
		    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"");
		    	abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"");
		    	
		    	String rank = (String)rowSet.getString("rank")!=null? (String)rowSet.getString("rank"):"";
		    	if(rank!=null && rank.trim().length()>0) {
                    rank=myformat1.format(Double.parseDouble(rank));//去掉小数点后面的0
                }
		    	abean.set("rank",rank);	
		    	
		    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
		    	list.add(abean);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 * 取得考核对象评估结果
	 * @return
	 */
	public HashMap getObjectEvaluateValueMap(String object_id,String mainbody_id)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_table_"+this.plan_vo.getInt("plan_id")+" where object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
			LazyDynaBean abean=new LazyDynaBean();
			DecimalFormat myformat1 = new DecimalFormat("########.###");//
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				String id=rowSet.getString("id");
				String score=rowSet.getString("score")!=null?rowSet.getString("score"):"";
				String amount=rowSet.getString("amount")!=null?rowSet.getString("amount"):"";
				String degree_id=rowSet.getString("degree_id")!=null?rowSet.getString("degree_id"):"";
				String point_id=rowSet.getString("point_id");
				
				abean.set("id", id);
				abean.set("point_id", point_id);
				if(score.length()==0) {
                    abean.set("score", score);
                } else {
                    abean.set("score",myformat1.format(Double.parseDouble(score)));
                }
				if(amount.length()==0) {
                    abean.set("amount", amount);
                } else {
                    abean.set("amount", myformat1.format(Double.parseDouble(amount)));
                }
				abean.set("degree_id", degree_id);
				map.put(point_id,abean);
			}
			
			String sql="select know_id,whole_grade_id from per_mainbody where plan_id="+this.plan_vo.getInt("plan_id")+" and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"' ";
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				map.put("know_id",rowSet.getString(1)!=null?rowSet.getString(1):"");
				map.put("whole_grade_id",rowSet.getString(2)!=null?rowSet.getString(2):"");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
		
	/**
	 * 取得模板引入的绩效指标标度
	 * @return
	 */
	public HashMap getTemplatePointDetail(boolean flag)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			StringBuffer sql=new StringBuffer("select pg.gradedesc "); 
			sql.append(" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg ,"+per_comTable+" pgt");
			sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id ");
			sql.append(" and  po.point_id=pg.point_id and pgt.grade_template_id=pg.gradecode and template_id='"+this.plan_vo.getString("template_id")+"'  ");
			RowSet rowSet=dao.search(sql.toString());
			int max_size=0;
			while(rowSet.next())
			{
				 
				String gradedesc=Sql_switcher.readMemo(rowSet,"gradedesc");
				if(gradedesc.length()>max_size) {
                    max_size=gradedesc.length();
                }
			}
			
			sql.setLength(0);
			sql.append("select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc desc2 "); 
			sql.append(" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg ,"+per_comTable+" pgt");
			sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id ");
			sql.append(" and  po.point_id=pg.point_id and pgt.grade_template_id=pg.gradecode and template_id='"+this.plan_vo.getString("template_id")+"'  order by pp.seq,pg.point_id,pg.gradecode");
          
			rowSet=dao.search(sql.toString());
            LazyDynaBean abean=null;
            String pointId="";
            ArrayList tempList=new ArrayList();
            while(rowSet.next())
            {
            	abean=new LazyDynaBean();
            	String point_id=rowSet.getString("point_id")!=null?rowSet.getString("point_id"):""; 
            	String gradevalue=rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"";  
            	String gradedesc=Sql_switcher.readMemo(rowSet,"gradedesc");
            	if(flag)
            	{
	            	if(gradedesc.length()<max_size)
	            	{
	            		String s="";
	            		for(int i=0;i<max_size-gradedesc.length();i++)
	            		{
	            			s+="&nbsp;&nbsp;";
	            		}
	            		gradedesc=gradedesc+s;
	            	}
            	}
            	
            	String gradecode=rowSet.getString("gradecode")!=null?rowSet.getString("gradecode"):""; 
            	String top_value=rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"";  
            	String bottom_value=rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"";
            	String pointkind=rowSet.getString("pointkind")!=null?rowSet.getString("pointkind"):"";
            	String status=rowSet.getString("status")!=null?rowSet.getString("status"):"";
            	if("".equals(pointId)) {
                    pointId=point_id;
                }
            	
            	if(top_value.length()==0||bottom_value.length()==0)
            	{	
            		perPointNoGrade="1";
            	}
            	if(!point_id.equals(pointId))
            	{
            		if(tempList.size()==0) {
                        perPointNoGrade="1";
                    }
            		map.put(pointId,tempList);
            		pointId=point_id;
            		tempList=new ArrayList();
            	}
            	abean.set("point_id",point_id);
            	abean.set("gradevalue",gradevalue);
            	abean.set("gradedesc",gradedesc);
            	abean.set("status",status);
            	abean.set("desc2",rowSet.getString("desc2")!=null?rowSet.getString("desc2"):"");
            	abean.set("gradecode",gradecode);
            	abean.set("top_value",top_value);
            	abean.set("bottom_value",bottom_value);
            	abean.set("pointname",rowSet.getString("pointname"));
            	abean.set("score", rowSet.getString("score"));
            	abean.set("pointkind",pointkind);
            	tempList.add(abean);
            }
            if(tempList.size()==0) {
                perPointNoGrade="1";
            }
            map.put(pointId,tempList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**
	 * 校验评分值是否正确
	 * @flag 1:保存 或 2:提交
	 */
	public String saveTaskScore(ArrayList scoreList,String flag,String body_id,String object_id,String konwDegree,String wholeEval,String mainbody_status) throws GeneralException
	{
		StringBuffer info=new StringBuffer("");
		try
		{
			this.pointDescMap=getTemplatePointDetail(false);			   //取得模板引入的绩效指标标度
			ArrayList resultList=new ArrayList();
			LazyDynaBean grade_bean=null;
			String scoreflag=(String)this.planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
			for(int i=0;i<scoreList.size();i++)
			{
				ArrayList tempList=new ArrayList();
				String[] temp=((String)scoreList.get(i)).split(":");
				String pint_id=temp[0].substring(2);
				
				IDGenerator idg = new IDGenerator(2, this.conn);
				String id = idg.getId("per_table_xxx.id");
				tempList.add(new Integer(id));
				tempList.add(object_id);
				tempList.add(body_id);
				if(!"null".equals(temp[1]))
				{
						ArrayList gradeList=(ArrayList)pointDescMap.get(pint_id);
						grade_bean=(LazyDynaBean)gradeList.get(0);
						String pointkind=(String)grade_bean.get("pointkind");
						String gradedesc=(String)grade_bean.get("gradedesc");
						String pointname=(String)grade_bean.get("pointname");
						String a_info=setPointValue(tempList,temp[1].trim(),gradeList,scoreflag,pointkind);
						if(a_info.length()>0)
						{
								info.append("\r\n"+pointname+":"+ResourceFactory.getProperty("org.performance.errorInfo1"));
								break;
						}
				
						resultList.add(tempList);
				}
			/*	else
				{
					tempList.add(new Float(0));
					tempList.add(new Float(0));
					tempList.add(pint_id);
					tempList.add("");
				}
				
				*/
			}
			if(info.length()==0&& "2".equals(flag))
			{
				 String sameResultsOption =(String)this.planParam.get("SameResultsOption"); // 核对象指标结果是否全部相同 1: 可以保存 2: 不能保存
				 if("2".equals(sameResultsOption)&&resultList.size()==scoreList.size())
				 {
					 String gradecode="";
					 boolean isSame=true;
					 for(int i=0;i<resultList.size();i++)
					 {
						 ArrayList tempList=(ArrayList)resultList.get(i);
						 if("".equals(gradecode)) {
                             gradecode=(String)tempList.get(6);
                         }
						 if(!gradecode.equals((String)tempList.get(6))) {
                             isSame=false;
                         }
					 }
					 if(isSame)
					 {
						 info.append(ResourceFactory.getProperty("performance.implement.info6")+"!");
					 }
				 }
			}
			if(info.length()==0)
			{
				ContentDAO dao = new ContentDAO(this.conn);
				dao.delete("delete from per_table_"+this.plan_vo.getInt("plan_id")+" where  object_id='"+object_id+"' and mainbody_id='"+body_id+"'",new ArrayList());
				StringBuffer sql=new StringBuffer("insert into per_table_"+this.plan_vo.getInt("plan_id")+" (id,object_id,mainbody_id,score,amount,point_id,degree_id) values (?,?,?,?,?,?,?)");
				dao.batchInsert(sql.toString(),resultList);
				String up_sql="update per_mainbody set status=";
				
				String gather_type = this.plan_vo.getString("gather_type");//0 网上 1 机读 2:网上+机读
				if("0".equals(gather_type)) {
                    up_sql+=flag;
                } else if(!"0".equals(gather_type)) {
                    up_sql+=mainbody_status;
                }
				
				String NodeKnowDegree=(String)planParam.get("NodeKnowDegree");	    //了解程度
				if("true".equalsIgnoreCase(NodeKnowDegree))
				{
					 if(konwDegree.trim().length()==0) {
                         konwDegree="-1";
                     }
					 up_sql+=",know_id="+konwDegree;
				}
				String WholeEval=(String)planParam.get("WholeEval");			    //总体评价
				if("true".equalsIgnoreCase(WholeEval))
				{
					 if(wholeEval.trim().length()==0) {
                         wholeEval="-1";
                     }
					 up_sql+=",whole_grade_id="+wholeEval;
				}
				up_sql+=" where  plan_id="+this.plan_vo.getInt("plan_id")+" and object_id='"+object_id+"' and mainbody_id='"+body_id+"'";
				dao.update(up_sql);
			}
		
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		return info.toString();
	}
	
	
	
	public String setPointValue(ArrayList tempList,String value,ArrayList pointGradeList,String scoreflag,String pointkind) throws GeneralException
	{
		 String info="1";
		 try
		 {
		 String ScaleToDegreeRule=(String)this.planParam.get("limitrule");      //分值转标度规则（1-就高 2-就低 3-就近就高（默认值）） 
		 if((("0".equals(pointkind)&& "2".equals(scoreflag))|| "1".equals(pointkind))&&value.matches("-?[\\d]*[.]?[\\d]+")&& "2".equals(ScaleToDegreeRule)) //2混合 // 分值 // 就低
		 {
			 for(int i=pointGradeList.size()-1;i>=0;i--)
			 {
					LazyDynaBean abean=(LazyDynaBean)pointGradeList.get(i);
					String gradecode=(String)abean.get("gradecode");
					float top_value=Float.parseFloat((String)abean.get("top_value"));
					float bottom_value=Float.parseFloat((String)abean.get("bottom_value"));
					float gradevalue=Float.parseFloat((String)abean.get("gradevalue"));
					float pointscore=Float.parseFloat((String)abean.get("score"));
					String point_id=(String)abean.get("point_id");
					float a_value=Float.parseFloat(value);
					if("0".equals(pointkind))  //定性
					{
						String avalue=PubFunc.round(String.valueOf(a_value),3);
						String topvalue=PubFunc.multiple(String.valueOf(top_value),String.valueOf(pointscore), 3);
						String bottomvalue=PubFunc.multiple(String.valueOf(bottom_value),String.valueOf(pointscore), 3);
						
						if(Float.parseFloat(avalue)<=Float.parseFloat(topvalue)&&a_value>=Float.parseFloat(bottomvalue))
						{
							tempList.add(new Float(a_value));
							tempList.add(new Float(0));
							tempList.add(point_id);
							tempList.add(gradecode);
							info="";
							break;
						}
					}
					else					 //定量
					{
						if(a_value<=top_value&&a_value>=bottom_value)
						{
							tempList.add(new Float(gradevalue*pointscore));
							tempList.add(new Float(a_value));
							tempList.add(point_id);
							tempList.add(gradecode);
							info="";
							break;
						}
					}
			}	
		}
		else
		{
			for(int i=0;i<pointGradeList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)pointGradeList.get(i);
				String gradecode=(String)abean.get("gradecode");
				float top_value=Float.parseFloat((String)abean.get("top_value"));
				float bottom_value=Float.parseFloat((String)abean.get("bottom_value"));
				float gradevalue=Float.parseFloat((String)abean.get("gradevalue"));
				float pointscore=Float.parseFloat((String)abean.get("score"));
				String point_id=(String)abean.get("point_id");
				if("0".equals(pointkind)&& "2".equals(scoreflag)&&value.matches("-?[\\d]*[.]?[\\d]+"))
				{
					float a_value=Float.parseFloat(value);
					
					String avalue=PubFunc.round(String.valueOf(a_value),3);
					String topvalue=PubFunc.multiple(String.valueOf(top_value),String.valueOf(pointscore), 3);
					String bottomvalue=PubFunc.multiple(String.valueOf(bottom_value),String.valueOf(pointscore), 3);
					
					
					if(Float.parseFloat(avalue)<=Float.parseFloat(topvalue)&&Float.parseFloat(avalue)>=Float.parseFloat(bottomvalue))
					{
						tempList.add(new Float(a_value));
						tempList.add(new Float(0));
						tempList.add(point_id);
						tempList.add(gradecode);
						info="";
						break;
					}
				}
				else
				{
					if("0".equals(pointkind))
					{
						if(value.equalsIgnoreCase(gradecode))
						{
								tempList.add(new Float(pointscore*gradevalue));
								tempList.add(new Float(0));
								tempList.add(point_id);
								tempList.add(value.toUpperCase());
								info="";
								break;
						}
					}
					else  //定量
					{
						
						if(value.matches("-?[\\d]*[.]?[\\d]+"))
						{
							float a_value=Float.parseFloat(value);
							if(a_value<=top_value&&a_value>=bottom_value)
							{
								tempList.add(new Float(gradevalue*pointscore));
								tempList.add(new Float(a_value));
								tempList.add(point_id);
								tempList.add(gradecode);
								info="";
								break;
							}
						}
						
					}
				}
			}
		}
		 
		 }
		 catch(Exception e)
		 {
			 throw GeneralExceptionHandler.Handle(e);
		 }
		return info;
	}
	
	
	
	
	
	

    /** ****** 生成excel ****** */

    private HSSFWorkbook workbook = new HSSFWorkbook();

    private HSSFSheet sheet = null;

    private HSSFCellStyle centerstyle = null;

    private int rowNum = 0; // 行坐标

    private short colIndex = 0; // 纵坐标

    private HSSFRow row = null;

    private HSSFCell csCell = null;

    private int totalColNum = 0; // 评估表的总共列数

    private int tableHeadTotalLayNum = 0; // 表头总层数

    private int resultSize = 0;

    
    /**
     * 输出(绩效分析/选票统计)excel
     * @param planid
     * @return
     */
    public String getPerVoteStatExcel(String planid,String codeitemid)
    {
    	String outputFile = "perAnalyseChart_"+PubFunc.getStrg()+".xls";
    	try
		{
    		PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.conn,this.userView);
    		ArrayList perDegreeList=bo.getPerDegreeList(planid);
    		ArrayList wholeEvalDataList=bo.getWholeEvalDataList(planid,codeitemid,perDegreeList);
    		workbook = new HSSFWorkbook();
		    sheet = workbook.createSheet("Sheet0");
		    centerstyle = style(workbook, 1);
		    //输出表头
		    if(perDegreeList.size()>0) {
		    	executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.colIndex+perDegreeList.size()*2+1)),this.plan_vo.getString("name"),style(workbook,0));
				this.rowNum++;
				executeCell(this.rowNum,this.colIndex,this.rowNum+2,this.colIndex,ResourceFactory.getProperty("kjg.gather.xuhao"),centerstyle);
			    this.colIndex++;
			    executeCell(this.rowNum,this.colIndex,this.rowNum+2,this.colIndex,ResourceFactory.getProperty("jx.datacol.khobj"),centerstyle);
			    this.colIndex++;
			    executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.colIndex+perDegreeList.size()*2-1)),ResourceFactory.getProperty("org.performance.zt"),centerstyle);
			    this.rowNum++;
		    } else {
		    	executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(2)),this.plan_vo.getString("name"),style(workbook,0));
				this.rowNum++;
		    	executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("kjg.gather.xuhao"),centerstyle);
			    this.colIndex++;
			    executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("jx.datacol.khobj"),centerstyle);
			    this.colIndex++;
			    executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("org.performance.zt"),centerstyle);
		    }
    		this.colIndex=0;
    		this.colIndex++;this.colIndex++;
    		
    		if(perDegreeList.size()>0) {
	    		for(int i=0;i<perDegreeList.size();i++)
	    		{
	    			LazyDynaBean abean=(LazyDynaBean)perDegreeList.get(i);
	    			executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.colIndex+1)),(String)abean.get("itemname"),centerstyle);
	    			this.colIndex++;this.colIndex++;
	    		}
	    		this.rowNum++;
	    		this.colIndex=0;this.colIndex++;this.colIndex++;
	    		for(int i=0;i<perDegreeList.size();i++)
	    		{
	    			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("lable.welcome.invtextresult.ballot"),centerstyle);
	    			this.colIndex++;
	    			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("train.evaluationStencil.percent"),centerstyle);   			
	    			this.colIndex++;
	    		}
    		}	
    		for(int i=0;i<wholeEvalDataList.size();i++){
    				this.rowNum++;
    				this.colIndex=0;
			  		LazyDynaBean abean=(LazyDynaBean)wholeEvalDataList.get(i);
			  		executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,String.valueOf(i+1),centerstyle);
			  		this.colIndex++;
			  		executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)abean.get("a0101"),centerstyle);
			  		
			  		if(perDegreeList.size()>0) {
				  		for(int j=0;j<perDegreeList.size();j++)
				  		{
				  			LazyDynaBean a_bean =(LazyDynaBean)perDegreeList.get(j);
				  			String id=(String)a_bean.get("id");
				  			this.colIndex++;
				  			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)abean.get(id),centerstyle);
				  			this.colIndex++;
				  			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)abean.get(id+"%"),centerstyle);
					  		
				  		}
			  		} else {
			  			this.colIndex++;
			  			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
			  		}
			  	
			   }
    		
    		
    		
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
		    workbook.write(fileOut);
		    fileOut.close();
		    sheet = null;
		    workbook = null;
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return outputFile;
    }
    //判断某计划的模板是否为权重模板
    public boolean testIsRankTempl(String planid)
    {
	boolean flag=false;
	ContentDAO dao = new ContentDAO(this.conn);
	String sql = "select * from per_template where status=1 and template_id in (select template_id from per_plan where plan_id="+planid+")";
	try
	{
	    RowSet rowSet=dao.search(sql);	
	    if(rowSet.next()) {
            flag=true;
        }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return flag;
    }
    
    /**
     * 输出绩效分析/统计分析 excel
     * @param planid
     * @return
     */
    public String getPerAnalyseStatExcel(String planid)
    {
    	String outputFile = "perAnalyseChart_"+PubFunc.getStrg()+".xls";
    	boolean isRankTempl = this.testIsRankTempl(planid);    	
    	try
		{
     		PerformanceAnalyseBo bo=new PerformanceAnalyseBo(this.conn,this.userView);
    		ArrayList standGradeList=bo.getStandGradeList(this.plan_vo.getString("template_id"));
			ArrayList planPointList=bo.getPlanPointList(planid);
			HashMap pointGradeNumMap=bo.getPointGradeNumMap(planid,standGradeList,planPointList);  		
			int men_num=bo.getPlanObjectNum(planid);  //考核对象人数
			
			this.pointDescMap=getTemplatePointDetail(false);			   //取得模板引入的绩效指标标度
			this.templateItemList=getTemplateItemList();
			get_LeafItemList();
			this.pointList=getPointList();
			this.itemToPointMap=getItemToPointMap();
			this.leafItemLinkMap=getLeafItemLinkMap();
			this.itemPointNum=getItemPointNum();
    	
			workbook = new HSSFWorkbook();
		    sheet = workbook.createSheet("Sheet0");
		    centerstyle = style(workbook, 1);
		    
		    
		    HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			int columnSize=0;
			
			
			executeCell(this.rowNum,Short.parseShort("0"),this.rowNum,Short.parseShort("2"),this.plan_vo.getString("name"),null);
			executeCell(this.rowNum,Short.parseShort("4"),this.rowNum,Short.parseShort("5"),ResourceFactory.getProperty("performance.batchgrade.objectNumber")+": "+men_num,null);
			
			this.rowNum++;
			 //输出表头
			executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay-1)),ResourceFactory.getProperty("train.job.itemName"),centerstyle);
			this.colIndex+=Short.parseShort(String.valueOf(this.lay));
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("kh.field.field_n"),centerstyle);
			this.colIndex++;
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("kh.field.scorevalue"),centerstyle);
			if(isRankTempl)
			{
			    this.colIndex++;
			    executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("label.kh.template.qz"),centerstyle); 
			}			
			for(int i=0;i<standGradeList.size();i++)
			{
				LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(i);
				this.colIndex++;
				executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)aa_bean.get("gradedesc"),centerstyle);
			}
			
			
			columnSize=this.colIndex;
			
			int rowNum=0;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
				String item_kind=(String)abean.get("kind");
				for(int j=0;j<num;j++)
				{
					this.rowNum++;
					this.colIndex=0;
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					for(int e=linkParentList.size()-1;e>=0;e--)
					{
						a_bean=(LazyDynaBean)linkParentList.get(e);
						String itemid=(String)a_bean.get("item_id");
						if(existWriteItem.get(itemid)!=null)
						{
							this.colIndex++;
							continue;
						}
						existWriteItem.put(itemid,"1");
						String itemdesc=(String)a_bean.get("itemdesc");
						executeCell(this.rowNum,this.colIndex,this.rowNum+((Integer)itemPointNum.get(itemid)).intValue()-1,this.colIndex,itemdesc,centerstyle);
						this.colIndex++;
						//////////////////////////////////////////////////////////////////////////
						if(item_id.equals(itemid)) {
                            continue;
                        }
						String parent_id = (String)a_bean.get("parent_id");
						if(this.lay>1 && parent_id.length()==0 && this.itemToPointMap.get(itemid)!=null)//顶层项目有指标
						{
							short temp = this.colIndex;
							ArrayList pointList2=(ArrayList)this.itemToPointMap.get(itemid);			
							for(int k=0;k<pointList2.size();k++)
							{								
								writePointGridExcel(1,pointList2,k,a_bean);
								if(pointList2==null)
								{									
								    if(isRankTempl)
									{
								    	executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,moveZero((String)a_bean.get("rank")),centerstyle);
								    	this.colIndex++;
								    }
									for(int x=0;x<standGradeList.size();x++)
									{
										LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(x);
										String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
										String context="";
										if(pointGradeNumMap.get(itemid+"_"+grade_template_id)!=null) {
                                            context=(String)pointGradeNumMap.get(itemid+"_"+grade_template_id);
                                        }
									
										executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
										this.colIndex++;
										
									}
								}else
								{
									LazyDynaBean point_bean=(LazyDynaBean)pointList2.get(k);
									String point_id=(String)point_bean.get("point_id");
									if(isRankTempl)
									{
									executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("rank"),centerstyle);
									this.colIndex++;}
									for(int x=0;x<standGradeList.size();x++)
									{
										LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(x);
										String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
										String context="";
										if(pointGradeNumMap.get(point_id+"_"+grade_template_id)!=null) {
                                            context=(String)pointGradeNumMap.get(point_id+"_"+grade_template_id);
                                        }
									
										executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
										this.colIndex++;
										
									}
								}
						
								this.rowNum++;
								this.colIndex=temp;
							}
						
						}
						///////////////////////////////////////////////////////////////////////
					}
					writePointGridExcel(current,pointList,j,abean);
				    
					if(pointList==null)
					{
					    if(isRankTempl)
						{
						executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,moveZero((String)abean.get("rank")),centerstyle);
						this.colIndex++;}
						for(int e=0;e<standGradeList.size();e++)
						{
							LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e);
							String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
							String context="";
							if(pointGradeNumMap.get(item_id+"_"+grade_template_id)!=null) {
                                context=(String)pointGradeNumMap.get(item_id+"_"+grade_template_id);
                            }
						
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
							this.colIndex++;
							
						}
						
						
						//executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
					}
					else
					{
						LazyDynaBean point_bean=(LazyDynaBean)pointList.get(j);
						String point_id=(String)point_bean.get("point_id");
						if(isRankTempl)
						{
						executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("rank"),centerstyle);
						this.colIndex++;}
						for(int e=0;e<standGradeList.size();e++)
						{
							LazyDynaBean aa_bean=(LazyDynaBean)standGradeList.get(e);
							String grade_template_id=((String)aa_bean.get("grade_template_id")).toLowerCase();
							String context="";
							if(pointGradeNumMap.get(point_id+"_"+grade_template_id)!=null) {
                                context=(String)pointGradeNumMap.get(point_id+"_"+grade_template_id);
                            }
						
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
							this.colIndex++;
							
						}
					}
				
				}
			}
			this.rowNum++;
			this.colIndex=0;
		
			//格式化
			for (int i = 0; i <=this.lay; i++)
			{
				this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
			}
			for (int i = 0; i <=this.pointList.size()+2; i++)
			{
			    row = sheet.getRow(i);
			    if(row==null) {
                    row = sheet.createRow(i);
                }
			    row.setHeight((short) 400);
			}
			
		    FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
		    workbook.write(fileOut);
		    fileOut.close();
		    sheet = null;
		    workbook = null;
			
			
    	
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return outputFile;
    }
    
    
    
    
    
    /**
         * 生成 考核评估 excel
         * 
         * @param computeFashion
         *                计算方式 1：得分统计 2:主体票数统计 3: 指标票数分统计
         * @param whl
         *                人员显示条件
         * @param pointResultValue
         *                指标结果值 1:分数 2:平均分比值 3:总分比值 4:单项比值
         * @param order
         *                排序
         * @param bodyid
         *                主体类别
         * @return
         */
    public String getDataGatherExcel(String object_id,String mainbody_id)
    {

		String outputFile = "perDataGather_"+PubFunc.getStrg()+".xls";
		try
		{
			
			this.objectBean=getObjectBean(object_id);
			this.objectEvaluateValueMap=getObjectEvaluateValueMap(object_id,mainbody_id);   //取得考核对象评估结果
			this.pointDescMap=getTemplatePointDetail(false);			   //取得模板引入的绩效指标标度
			this.templateItemList=getTemplateItemList();
			get_LeafItemList();
			this.pointList=getPointList();
			this.itemToPointMap=getItemToPointMap();
			this.leafItemLinkMap=getLeafItemLinkMap();
			this.itemPointNum=getItemPointNum();
		
			
			
			
		    workbook = new HSSFWorkbook();
		    sheet = workbook.createSheet("Sheet0");
		    centerstyle = style(workbook, 1);
		    
		    
		    HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			int columnSize=0;
			
			 //输出表头
			executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay-1)),ResourceFactory.getProperty("train.job.itemName"),centerstyle);
			this.colIndex+=Short.parseShort(String.valueOf(this.lay));
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("kjg.title.indexname"),centerstyle);
			this.colIndex++;
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,ResourceFactory.getProperty("jx.param.mark"),centerstyle);
			this.colIndex++;
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)this.objectBean.get("a0101"),centerstyle);
			columnSize=this.colIndex;
			DecimalFormat myformat1 = new DecimalFormat("########.###");//
			int rowNum=0;
			
			
			/*
			
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
				String item_kind=(String)abean.get("kind");
				for(int j=0;j<num;j++)
				{
					this.rowNum++;
					this.colIndex=0;
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					for(int e=linkParentList.size()-1;e>=0;e--)
					{
						a_bean=(LazyDynaBean)linkParentList.get(e);
						String itemid=(String)a_bean.get("item_id");
						if(existWriteItem.get(itemid)!=null)
						{
							this.colIndex++;
							continue;
						}
						existWriteItem.put(itemid,"1");
						String itemdesc=(String)a_bean.get("itemdesc");
						executeCell(this.rowNum,this.colIndex,this.rowNum+((Integer)itemPointNum.get(itemid)).intValue()-1,this.colIndex,itemdesc,centerstyle);
						this.colIndex++;
					}
					writePointGridExcel(current,pointList,j,abean);
				    
					if(pointList==null)
					{
						executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
					}
					else
					{
						LazyDynaBean point_bean=(LazyDynaBean)pointList.get(j);
						if(point_bean!=null)
						{
						
							LazyDynaBean valueBean=null;    //标度值
							String point_id=(String)point_bean.get("point_id");
							if(this.objectEvaluateValueMap.get(point_id)!=null)
								valueBean=(LazyDynaBean)this.objectEvaluateValueMap.get(point_id);
							ArrayList pointGradeList=(ArrayList)this.pointDescMap.get(point_id);
							LazyDynaBean aabean=(LazyDynaBean)pointGradeList.get(0);
							String pointkind=(String)aabean.get("pointkind");           // 0:定性  1:定量
							String scoreflag=(String)this.planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
							if (scoreflag.equals("2") || pointkind.equals("1")) {
								String context="";
								if (valueBean != null) {
									if (pointkind.equals("1")) {
										context=(String) valueBean.get("amount");
									} else if (pointkind.equals("0")) {
										context=(String) valueBean.get("score");
									}
								}
								executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
							}
							else if(scoreflag.equals("1")&&pointkind.equals("0"))
							{								
								String a_value = "";
								if (valueBean != null)
									a_value = (String) valueBean.get("degree_id");
								
								for (int e = 0; e < pointGradeList.size(); e++) {
									LazyDynaBean aa_bean = (LazyDynaBean) pointGradeList.get(e);
									String gradecode = (String) aa_bean.get("gradecode");
									String gradedesc = (String) aa_bean.get("gradedesc");
									String desc2=(String)aa_bean.get("desc2");
									if (a_value.equalsIgnoreCase(gradecode))
										a_value= desc2;
								}
								executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,a_value,centerstyle);
							}
							
							
						}
						else
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
					
					
					}
				
				}
			}
			*/
			
			
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			ArrayList list=singleGradeBo.getPerformanceStencilList(this.plan_vo.getString("template_id"));
			ArrayList items=(ArrayList)list.get(0);				//模版项目列表(按顺序)
			HashMap    map=(HashMap)list.get(2);				//各项目包含的指标个数
			HashMap    subItemMap=(HashMap)list.get(3);			//各项目的子项目(hashmap)		
			HashMap itemsSignMap=singleGradeBo.getItemsSignMap(items);
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			ArrayList _pointList=batchGradeBo.getPerPointList(this.plan_vo.getString("template_id"),String.valueOf(this.plan_vo.getInt("plan_id")));	
			ArrayList a_pointList=(ArrayList)_pointList.get(1);             //指标集 
			HashMap   pointItemMap=singleGradeBo.getPointItemList((ArrayList)_pointList.get(1),items);	
			int a_no=1;
			String  showOneMark=(String)planParam.get("ShowOneMark");  //BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
			
			for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] point=(String[])t.next();
				 ArrayList pointItemList=(ArrayList)pointItemMap.get(point[0]);
				 int a_lay=lay;
			//	 htmlContext.append("<tr>");
				 this.rowNum++;
				 this.colIndex=0;
				 
				 
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
						//	 htmlContext.append(writeTd(item[3],Integer.parseInt((String)map.get(item[0])),"left",this.td_width));
							
							 executeCell(this.rowNum,this.colIndex,this.rowNum+Integer.parseInt((String)map.get(item[0]))-1,this.colIndex,item[3],centerstyle);
							 this.colIndex++;
							 
							 currentItem=item;
							 itemsSignMap.put(item[0],"1");
						 }
						 else {
                             this.colIndex++;
                         }
					 }
					 else 
					 {
						 if(--pointItemLength>=0)
						 {
						 
							 String[] item=(String[])pointItemList.get(pointItemLength);
							 String sign=(String)itemsSignMap.get(item[0]);
							 if(!"1".equals(sign))
							 {					
								// htmlContext.append(writeTd(item[3],Integer.parseInt((String)map.get(item[0])),"left",this.td_width));			
								 
								 executeCell(this.rowNum,this.colIndex,this.rowNum+Integer.parseInt((String)map.get(item[0]))-1,this.colIndex,item[3],centerstyle);
								 this.colIndex++;
								 
								 currentItem=item;
								 itemsSignMap.put(item[0],"1");						
							 }else
							 {
								// executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
								 this.colIndex++;
							 }
						 }
						 else
						 {
							// htmlContext.append(writeTd("&nbsp;",1,"left",this.td_width));
							 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
							 this.colIndex++;
						 }					 
					 }
				 
				 }
				 
				 LazyDynaBean _abean=null;
				 for(int i=0;i<this.pointList.size();i++)
				 {
						_abean=(LazyDynaBean)this.pointList.get(i);
						String point_id=(String)_abean.get("point_id");
						if(point_id.equalsIgnoreCase(point[0]))
						{
							break;
						}
						else {
                            _abean=null;
                        }
				 }
				 if(_abean==null)
				 {
				//	 htmlContext.append(writeTd("&nbsp;",0,"left",this.td_width));
					 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
				 }
				 else
				 {
					// htmlContext.append(writePointTd((String)_abean.get("pointname"),(String)_abean.get("point_id"),"left",this.td_width));
					 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)_abean.get("pointname"),centerstyle);
				 }
				 this.colIndex++;
				 
				// htmlContext.append(writeTd((String)_abean.get("score"),0,"right",80));
				 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)_abean.get("score"),centerstyle);
				 this.colIndex++;
				 
				 if(_abean==null)
				 {
				//	 htmlContext.append(getExtendTd(null));
					 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
				 }
				 else
				 {
				//	 htmlContext.append(getExtendTd(_abean));
					 LazyDynaBean valueBean=null;    //标度值
						String point_id=(String)_abean.get("point_id");
						if(this.objectEvaluateValueMap.get(point_id)!=null) {
                            valueBean=(LazyDynaBean)this.objectEvaluateValueMap.get(point_id);
                        }
						ArrayList pointGradeList=(ArrayList)this.pointDescMap.get(point_id);
						LazyDynaBean aabean=(LazyDynaBean)pointGradeList.get(0);
						String pointkind=(String)aabean.get("pointkind");           // 0:定性  1:定量
						String scoreflag=(String)this.planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
						if ("4".equals(scoreflag) || "2".equals(scoreflag) || "1".equals(pointkind)) {
							String context="";
							if (valueBean != null) {
								if ("1".equals(pointkind)) {
									context=(String) valueBean.get("amount");
								} else if ("0".equals(pointkind)) {
									context=(String) valueBean.get("score");
								}
							}
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,context,centerstyle);
						}
						else if("1".equals(scoreflag)&& "0".equals(pointkind))
						{								
							String a_value = "";
							if (valueBean != null) {
                                a_value = (String) valueBean.get("degree_id");
                            }
							
							for (int e = 0; e < pointGradeList.size(); e++) {
								LazyDynaBean aa_bean = (LazyDynaBean) pointGradeList.get(e);
								String gradecode = (String) aa_bean.get("gradecode");
								String gradedesc = (String) aa_bean.get("gradedesc");
								String desc2=(String)aa_bean.get("desc2");
								if (a_value.equalsIgnoreCase(gradecode))
								{
									a_value= desc2;
									if("2".equals(this.planParam.get("DegreeShowType"))) {
                                        a_value=gradedesc;
                                    }
								}
							}
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,a_value,centerstyle);
						}
				 }
				//	htmlContext.append("</tr>\r\n");
				 a_no++;
			 }
			
			
			
			
			
			
			
			
			
			
			
			 /* 了解程度 && 总体评价 */
			 String NodeKnowDegree=(String)planParam.get("NodeKnowDegree");	    //了解程度
			 String WholeEval=(String)planParam.get("WholeEval");			    //总体评价
			 String GradeClass=(String)planParam.get("GradeClass");					//等级分类ID
		//	 BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
			 
			 ArrayList  nodeKnowDegreeList=new ArrayList();
			 ArrayList  wholeEvalList=new ArrayList();
			 if("true".equals(NodeKnowDegree))
			 {
					nodeKnowDegreeList=batchGradeBo.getExtendInfoValue("1","");			
			 }
			 if("true".equals(WholeEval))
			 {
					wholeEvalList=batchGradeBo.getExtendInfoValue("2",GradeClass);				
			 }
			 /* 是否有了解程度 */
			 String select_id=" ";
			 if("true".equals(NodeKnowDegree))
			 {
					if(this.objectEvaluateValueMap.get("know_id")!=null) {
                        select_id=(String)this.objectEvaluateValueMap.get("know_id");
                    }
					
					this.rowNum++;
					this.colIndex=0;
					executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay+1)),ResourceFactory.getProperty("lable.statistic.knowdegree"),centerstyle);
					this.colIndex+=Short.parseShort(String.valueOf(this.lay+1));
					this.colIndex++;
					String value="";
					for(int i=0;i<nodeKnowDegreeList.size();i++)
					{
						String[] temp=(String[])nodeKnowDegreeList.get(i);
						if(temp[0].equals(select_id)) {
                            value=temp[1];
                        }
					}
					executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,value,centerstyle);
			 }
			 /* 是否有总体评价 */
			 select_id=" ";
			 if("true".equals(WholeEval))
			 {
					if(this.objectEvaluateValueMap.get("whole_grade_id")!=null) {
                        select_id=(String)this.objectEvaluateValueMap.get("whole_grade_id");
                    }
					
					
					this.rowNum++;
					this.colIndex=0;
					executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay+1)),ResourceFactory.getProperty("lable.statistic.wholeeven"),centerstyle);
					this.colIndex+=Short.parseShort(String.valueOf(this.lay+1));
					this.colIndex++;
					String value="";
					for(int i=0;i<wholeEvalList.size();i++)
					{
						String[] temp=(String[])wholeEvalList.get(i);
						if(temp[0].equals(select_id)) {
                            value=temp[1];
                        }
					}
					executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,value,centerstyle);
				
			 }
			
			
			
			
			
			
			
			this.rowNum++;
			this.colIndex=0;
			executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay)),ResourceFactory.getProperty("label.kh.template.total"),centerstyle);
			this.colIndex+=Short.parseShort(String.valueOf(this.lay+1));
			double topscore = (double)this.template_vo.getDouble("topscore");
			String hzscore = Double.toString(topscore) ;
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,hzscore,centerstyle);
			this.colIndex++;
			float score= batchGradeBo.getObjectTotalScore(this.plan_vo.getInt("plan_id"),mainbody_id,this.templateid,object_id,this.userView);
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,myformat1.format(score),centerstyle);

			
			//格式化
			for (int i = 0; i <=columnSize; i++)
			{
				this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
			}
			for (int i = 0; i <=this.pointList.size()+2; i++)
			{
			    row = sheet.getRow(i);
			    if(row==null) {
                    row = sheet.createRow(i);
                }
			    row.setHeight((short) 400);
			}
			
		    FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
		    workbook.write(fileOut);
		    fileOut.close();
		    sheet = null;
		    workbook = null;
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return outputFile;
    }

    
    
    /**
	 * 画空格　和　指标格
	 * @param current
	 * @param pointList
	 * @param j
	 * @return
	 */
	public void writePointGridExcel(int current,ArrayList pointList,int j,LazyDynaBean itemBean)
	{
		
		LazyDynaBean point_bean=null;
		for(int e=current;e<this.lay;e++)
		{
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
			this.colIndex++;
			//tempHtml.append(writeTd("&nbsp;",1,"left",this.td_width));	
		}
		
		if(pointList==null)
		{
			//tempHtml.append(writeTd("&nbsp;",0,"left",this.td_width));
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex," ",centerstyle);
			this.colIndex++;
			
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,moveZero((String)itemBean.get("score")),centerstyle);
			this.colIndex++;
		}
		else
		{  
			point_bean=(LazyDynaBean)pointList.get(j);
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("pointname"),centerstyle);
			this.colIndex++;
		//	tempHtml.append(writePointTd((String)point_bean.get("pointname"),(String)point_bean.get("point_id"),"left",this.td_width));
		
		  
			point_bean=(LazyDynaBean)pointList.get(j);
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("score"),centerstyle);
			this.colIndex++;
		}
	//	tempHtml.append(writeTd((String)point_bean.get("score"),0,"right",80));
	//	return tempHtml.toString();
	}
    
    
    
    /**
     * 
     * @param a
     *                起始 x坐标
     * @param b
     *                起始 y坐标
     * @param c
     *                终止 x坐标
     * @param d
     *                终止 y坐标
     * @param content
     *                内容
     * @param style
     *                表格样式
     * @param fontEffect
     *                字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
     */
	public void executeCell(int a, short b, int c, short d, String content,
			HSSFCellStyle aStyle) {
		try {
			HSSFRow row = sheet.getRow(a);
			if(row==null) {
                row = sheet.createRow(a);
            }
			
			HSSFCell cell = row.getCell(b);
			if(cell==null) {
                cell = row.createCell(b);
            }
			
			cell.setCellValue(new HSSFRichTextString(content));
			if(aStyle!=null) {
                cell.setCellStyle(aStyle);
            }
			short b1 = b;
			while (++b1 <= d) {
				cell = row.getCell(b1);
				if(cell==null) {
                    cell = row.createCell(b1);
                }
				
				if(aStyle!=null) {
                    cell.setCellStyle(aStyle);
                }
			}
			for (int a1 = a + 1; a1 <= c; a1++) {
				row = sheet.getRow(a1);
				if(row==null) {
                    row = sheet.createRow(a1);
                }
				b1 = b;
				while (b1 <= d) {
					cell = row.getCell(b1);
					if(cell==null) {
                        cell = row.createCell(b1);
                    }
					if(aStyle!=null) {
                        cell.setCellStyle(aStyle);
                    }
					b1++;
				}
			}
			
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    
    
    
	
    /**
     * 设置excel表格效果
     * 
     * @param styles
     *                设置不同的效果
     * @param workbook
     *                新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook, int styles)
	{
		
		HSSFCellStyle style = workbook.createCellStyle();
		
		switch (styles)
		{
		
		case 0:
		    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 15);
		    fonttitle.setBold(true);// 加粗
		    style.setFont(fonttitle);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    break;
		case 1:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    break;
		case 2:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.TOP);
		    break;
		case 3:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		    break;
		case 4:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		    break;
		default:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setAlignment(HorizontalAlignment.LEFT);
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    break;
		}
		style.setWrapText(true);
		return style;
	}
	
	/**
	 * 设置excel字体效果
	 * 
	 * @param fonts
	 *                设置不同的字体
	 * @param size
	 *                设置字体的大小
	 * @param workbook
	 *                新建的表格
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size)
	{
	
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		return font;
	}

/** ************ end ********** */

	/*
	 * 统计票数
	 */
	public String getVote(String planId)
	{
		String ret= "";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String VotesNum=(String)this.planParam.get("VotesNum");
			
			if(VotesNum!=null&&!"".equals(VotesNum)){
				ret = VotesNum;
				
			}else{
				RowSet rowSet=dao.search("select count( distinct mainbody_id) as mainbody_id from per_mainbody where  plan_id="+planId);
			
				if(rowSet.next()) {
                    ret=rowSet.getString(1);
                }
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	/*
	 * 统计票数
	 */
	public String getVote2(String planId)
	{
		String ret= "0";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String VotesNum=(String)this.planParam.get("VotesNum");
			
			if(VotesNum!=null&&!"".equals(VotesNum)) {
                ret = VotesNum;
            }
		
			String VotesNum_sj="0";
			RowSet rowSet=dao.search("select count( distinct mainbody_id) as mainbody_id from per_mainbody where  plan_id="+planId);
			if(rowSet.next()) {
                VotesNum_sj=rowSet.getString(1);
            }
				
			if(Double.parseDouble(ret)<Double.parseDouble(VotesNum_sj)) {
                ret = VotesNum_sj;
            }
				
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	/*
	 * 计算票数与百分比；
	 */
	public ArrayList getFullvote(String vote,String planId)
	{
		ArrayList list = new ArrayList();
		String full="";
		String vote1=vote;
		String fullthan;
		LazyDynaBean bean =null;
		ContentDAO dao = new ContentDAO(this.conn);
		int spent;
		String name="";
		
	
		try{
			//有效票数
			RowSet ineffect = dao.search("select count(*) from (select mainBody_id from per_mainbody where plan_id ='"+planId+"' group by mainBody_id) a");
			while(ineffect.next()){
				full = ineffect.getString(1);
				double   dvote1   =   Double.parseDouble(vote1);	//强制转换成 double;
				double   dfull   =   Double.parseDouble(full);
				double  p3  =  dfull  /  dvote1;
				   NumberFormat nf  =  NumberFormat.getPercentInstance();
				   nf.setMinimumFractionDigits( 2 );
				   fullthan  =  nf.format(p3);
				   bean = new LazyDynaBean();
				   name=ResourceFactory.getProperty("lable.performance.ballot.ineffect");
				   bean.set("name",name);
				   bean.set("vote",full);
				   bean.set("bl",fullthan);
				   list.add(bean);
			}
			//全部提交
			RowSet rowSet = dao.search("select count(*)  from (select mainbody_id from per_mainbody where plan_id ='"+planId+"' and status =2 group by mainbody_id) a");
			while(rowSet.next()){
				full = rowSet.getString(1);
				double   dvote1   =   Double.parseDouble(vote1);	//强制转换成 double;
				double   dfull   =   Double.parseDouble(full);
				double  p3  =  dfull  /  dvote1;
				   NumberFormat nf  =  NumberFormat.getPercentInstance();
				   nf.setMinimumFractionDigits( 2 );
				   fullthan  =  nf.format(p3);
				   bean = new LazyDynaBean();
				   name=ResourceFactory.getProperty("lable.performance.ballot.full");
				   bean.set("name",name);
				   bean.set("vote",full);
				   bean.set("bl",fullthan);
				   list.add(bean);
			}
			//部分提交
			RowSet rowSet3 = dao.search("select count(*)  from (select mainbody_id from per_mainbody where plan_id ='"+planId+"' and status =3 group by mainbody_id) a");
			
			while(rowSet3.next()){
				full = rowSet3.getString(1);
				double   dvote1   =   Double.parseDouble(vote1);	//强制转换成 double;
				double   dfull   =   Double.parseDouble(full);
				double  p3  =  dfull  /  dvote1;
				   NumberFormat nf  =  NumberFormat.getPercentInstance();
				   nf.setMinimumFractionDigits( 2 );
				   fullthan  =  nf.format(p3);
				   bean = new LazyDynaBean();
				   name = ResourceFactory.getProperty("lable.performance.ballot.part");
				   bean.set("name",name);
				   bean.set("vote",full);
				   bean.set("bl",fullthan);
				   list.add(bean);
			}
			//未提交
			RowSet rowSet0 = dao.search("select count(*)  from (select mainbody_id from per_mainbody where plan_id ='"+planId+"' and status =0 group by mainbody_id) a");
			while(rowSet0.next()){
				full = rowSet0.getString(1);
				double   dvote1   =   Double.parseDouble(vote1);	//强制转换成 double;
				double   dfull   =   Double.parseDouble(full);
				double  p3  =  dfull  /  dvote1;
				   NumberFormat nf  =  NumberFormat.getPercentInstance();
				   nf.setMinimumFractionDigits( 2 );
				   fullthan  =  nf.format(p3);
				   bean = new LazyDynaBean();
				   name=ResourceFactory.getProperty("lable.performance.ballot.null");
				   bean.set("name",name);
				   bean.set("vote",full);
				   bean.set("bl",fullthan);
				   list.add(bean);
			}
		
			//废票数
			RowSet depose = dao.search("select count(*) from (select mainBody_id from per_mainbody where plan_id ='"+planId+"' group by mainBody_id) a");
			while(depose.next()){
				full = depose.getString(1);
				double   dvote1   =   Double.parseDouble(vote1);	//强制转换成 double;
				double   dfull   =   Double.parseDouble(full);
				
				spent  = (int)dvote1-(int)dfull;
				double  p3  =  spent / dvote1;
				   NumberFormat nf  =  NumberFormat.getPercentInstance();
				   nf.setMinimumFractionDigits( 2 );
				   fullthan  =  nf.format(p3);
				   bean = new LazyDynaBean();
				   name=ResourceFactory.getProperty("lable.performance.ballot.trash");
				   bean.set("name",name);
				   bean.set("vote",new Integer(spent));
				   bean.set("bl",fullthan);
				   list.add(bean);
				   
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	

	public String getPerPointNoGrade() {
		return perPointNoGrade;
	}


	public void setPerPointNoGrade(String perPointNoGrade) {
		this.perPointNoGrade = perPointNoGrade;
	}


	public Hashtable getPlanParam() {
		return planParam;
	}


	public void setPlanParam(Hashtable planParam) {
		this.planParam = planParam;
	}


	public String getTemplateid() {
		return templateid;
	}


	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}


	public RecordVo getPlan_vo() {
		return plan_vo;
	}


	public void setPlan_vo(RecordVo plan_vo) {
		this.plan_vo = plan_vo;
	}
	
	
}