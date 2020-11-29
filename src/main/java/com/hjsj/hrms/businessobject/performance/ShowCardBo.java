package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShowCardBo {
	private Connection conn = null;
	private UserView userView = null;
	private String plan_id = "";
	private String template_id = "";
	private String object_id = "";
	private String method = "";//1 360管理 2 目标管理
	private String object_type = "";//考核对象类型  1:部门 2:人员 3:单位 4.部门
	private String accuracy = "";//小数的位数
	
	private HashMap itemPointNum = new HashMap();//每个项目拥有的叶子节点数(最底层节点数)
	private ArrayList parentList = new ArrayList();//所有的父亲节点（也就是所有的顶层项目）
	private ArrayList leafItemList = new ArrayList();//最底层的项目
	private ArrayList templateItemList = new ArrayList();//该模板所有的项目信息。list存放bean
	private HashMap itemToPointMap = new HashMap();// 共性项目对应指标
	private HashMap selfItemToPointMap = new HashMap();// 个性项目对应指标
	private HashMap itemHaveFieldList = new HashMap();//项目id对应的指标的详细信息列表(包括没有指标的项目)
	private HashMap leafItemLinkMap = new HashMap();
	private HashMap childItemLinkMap = new HashMap();
	private HashMap ifHasChildMap = new HashMap();
	private HashMap layMap = new HashMap();
	private HashMap pointScoreMap = new HashMap();//指标号为键值，分数为内容(作者：郭峰)
	private HashMap pointStandardScoreMap = new HashMap();//指标号为键值，分数为标准分值
	private HashMap pointStandardRankMap = new HashMap();//指标号为键值，分数为权重
	private ArrayList pointSetList = new ArrayList();//存储所有打过分的指标编号(共性项目)
	DecimalFormat myformat1 = new DecimalFormat("########.########");
	private int td_width = 130;
	private int td_height = 30;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	Calendar calendar = Calendar.getInstance();				
	String historyDate = sdf.format(calendar.getTime());
	//显示评分明细的功能
	String scoreExplainFlag = "";
	int mainbodyNum = 0;
	//得到 mainbody和 mainbodyname 的map
	HashMap mainbodyMap = new HashMap();
	//得到评分说明总的map(大map套着小map,小map中point_id为键值，reasons为内容)
	HashMap mainbodyTotal = new HashMap();
	//得到考核对象列表
	private boolean  byModel = false;
	private boolean isHasPoint = false;
	private boolean isByModelFlag = false;
	
	public ShowCardBo(Connection conn,UserView userView,String plan_id,String template_id,String object_id,String method,String object_type,String scoreExplainFlag) throws GeneralException{
		this.conn=conn;
		this.userView = userView;
		this.plan_id = plan_id;
		this.template_id = template_id;
		this.object_id = object_id;
		this.method = method;
		this.object_type = object_type;
		this.accuracy = getAccuracy();
		this.scoreExplainFlag = scoreExplainFlag;
		BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,plan_id);
		initData(plan_id,object_id);
		String e01a1 = batchGradeBo.getE01a1(object_id);
		if(isByModelFlag){//是否按岗位素质模型，并定义了指标
			this.templateItemList = this.getItemListByModel(e01a1);
		}else{
			this.templateItemList = this.getTemplateItemList(template_id);
		}
		if(isByModelFlag){
			this.itemToPointMap = this.getItemToPointMapByModel(e01a1);
		}else{
			this.itemToPointMap = this.getItemToPointMap(template_id);
		}
		
		this.selfItemToPointMap = this.getSelfItemToPointMap();
		
		if(isByModelFlag){
			leafItemList = templateItemList;
			this.itemHaveFieldList = this.getItemHasFieldListByModel(e01a1);
		}else{
			get_LeafItemList();
			this.itemHaveFieldList = this.getItemHasFieldList2();
			
		}
		
		this.leafItemLinkMap = getLeafItemLinkMap();
		this.childItemLinkMap = this.getChildItemLinkMap();
		this.doMethod2();
		this.itemPointNum = getItemPointNum();
		if(isByModelFlag){
			this.pointScoreMap = getPointScoreMapByModel();
			this.pointStandardScoreMap = getPointStandardScoreMap(e01a1);//不知道干什么用的
			this.pointStandardRankMap = getPointStandardRankMap(e01a1);
		}else if(byModel){//当勾选了按岗位素质模型测评，但是岗位却没有指标的时候，那么取分数也从历史表中取
			this.pointScoreMap = getPointScoreMapByModel();
		}else{
			this.pointScoreMap = getPointScoreMap();
		}
		if("1".equals(scoreExplainFlag)){
			this.mainbodyNum = getMainbodyNum();
			this.mainbodyMap = getMainbodyMap();
			this.mainbodyTotal = getMainbodyTotal();
		}
	} //构造函数结束
	
	public void initData(String plan_id,String object_id){
		byModel = SingleGradeBo.getByModel(plan_id,this.conn);
		if(!byModel){
			return;
		}
		isHasPoint = SingleGradeBo.isHaveMatchByModel(object_id, this.conn);
		if(byModel && isHasPoint){
			isByModelFlag = true;
		}
	}
	public String getTableHtml(){
		StringBuffer html = new StringBuffer();
		try{
			
			StringBuffer htmlContext = new StringBuffer("");
			StringBuffer r_item = new StringBuffer("");
			StringBuffer score = new StringBuffer("");
			HashMap existWriteItem = new HashMap();
			LazyDynaBean abean = null;
			LazyDynaBean a_bean = null;
			StringBuffer extendtHead = new StringBuffer();
			String busitype = getBusiType();
			String status = getStatus();
			HashMap pointrankMap = null;
			if("1".equals(busitype)&&"1".equals(status)) {
                pointrankMap = getPointRankMap(this.template_id);
            }
				
			
			//int index=0;//序号
			// 输出表头
			int colCount = this.a_lays + 2;//列的层数（只包括项目和指标）
			extendtHead.append("<table   class='ListTable_self2' border='0' cellspacing='0'  align='center' cellpadding='0' >");
			extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
			//extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='1' nowrap >序号</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center'  colspan='" + this.a_lays + "' nowrap >"+ResourceFactory.getProperty("kh.field.projectname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' width=\"200\" nowrap >"+ResourceFactory.getProperty("kjg.title.indexname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' width=\"100\" nowrap >"+ResourceFactory.getProperty("kh.field.scorevalue")+"</td>\r\n");
			//liuy 分值*权重 begin
			if("1".equals(busitype)&&(isByModelFlag||"1".equals(status)))//是能力素质  并且（使用模型或者权重模板）
            {
                extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' width=\"100\" nowrap >"+ResourceFactory.getProperty("kh.field.scorerank")+"</td>\r\n");
            }
			//liuy end
			extendtHead.append("</tr>\r\n");
			
			//输出表体
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				htmlContext.append("<tr>\r\n");
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);//项目之间的继承关系
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					if (existWriteItem.get(itemid) != null){
						if(e==0){
							htmlContext.delete(htmlContext.length()-8, htmlContext.length());
						}
						continue;
					}
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");
					/** 该项目所占的行数 */
					int colspan = ((itemPointNum.get(itemid) == null ? 0 : ((Integer) itemPointNum.get(itemid)).intValue()) + (childItemLinkMap.get(itemid) == null ? 0 : ((Integer) childItemLinkMap.get(itemid)).intValue()));
					/** 画出该项目 */
					//index++;//画项目时也要使序号加1
					//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+(index)+"</td>");
					htmlContext.append(writeTd(itemdesc, colspan, "left", this.td_width));//画出项目
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0) 
						{
							for (int h = 0; h < fieldlistp.size(); h++)
							{
								LazyDynaBean xbean = (LazyDynaBean) fieldlistp.get(h);
								String pointid = (String) xbean.get("point_id");
								if (h != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
									
								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow' >" + (String) xbean.get("name") + "</td>");

								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								
								
								htmlContext.append("</tr>\r\n");
							}
						}
						/** 没有指标 */

						else
						{

							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.a_lays - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
								htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
								htmlContext.append("&nbsp;");
								htmlContext.append("</td>");
								
								htmlContext.append("</tr>\r\n");
							}
						}
					}

					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(itemid);

						if (fieldlist != null && fieldlist.size() != 0)
						{
							for (int x = 0; x < fieldlist.size(); x++)
							{
								if (x != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
									
								LazyDynaBean xbean = (LazyDynaBean) fieldlist.get(x);
								String pointid = (String) xbean.get("point_id");
								String rank = (String) xbean.get("rank");
								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow' width=\"200\" ");
								
								htmlContext.append(">" + (String) xbean.get("name") + "</td>");

								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								
								//liuy 2015-11-25 begin
								if("1".equals(busitype)&&(isByModelFlag||"1".equals(status))){
									double scorevalue = 0.0;
									double rankvalue = 0.0;
									if(this.pointScoreMap.get(pointid)!=null) {
                                        scorevalue = Double.parseDouble((String)this.pointScoreMap.get(pointid));
                                    }
									if(isByModelFlag){										
										if(this.pointStandardRankMap.get(pointid)!=null) {
                                            rankvalue = Double.parseDouble((String)this.pointStandardRankMap.get(pointid));
                                        }
									}else if("1".equals(status)) {
										rankvalue = Double.parseDouble((String)pointrankMap.get(pointid));
									}
									htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
									htmlContext.append((String)PubFunc.round(String.valueOf(scorevalue*rankvalue),2));
									htmlContext.append("</td>");
								}
								//liuy 2015-11-25 end
								
								htmlContext.append("</tr>\r\n");
							}
						} else
						{
							for (int f = 0; f < this.a_lays - layer + 1; f++)
							{
								htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
							htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							htmlContext.append("&nbsp;");
							htmlContext.append("</td>");
							
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
		htmlContext.append("</table>");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		return html.toString();	
		} catch (Exception e){
			e.printStackTrace();
		}
		return html.toString();
	}
	
	/**
	 * 根据template_id得到模板权重
	 * @param template_id
	 * @return
	 */
	private HashMap getPointRankMap(String template_id){
		HashMap pointrank = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select ptp.point_id,ptp.rank from per_template_point ptp,per_point pp where  ptp.item_id in ";
			sql += "(select item_id from per_template_item where UPPER(template_id)='"+ template_id +"') and ptp.point_id=pp.point_id order by ptp.seq";
			rs = dao.search(sql);
			while(rs.next()) {
				pointrank.put(rs.getString("point_id"), rs.getString("rank"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs !=null) {
                PubFunc.closeResource(rs);
            }
		}
		
		return pointrank;
	}
	
	/**
	 * 是否是能力素质，1能力素质，0绩效管理
	 * @return
	 */
	private String getBusiType(){
		String busitype = "";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select busitype from per_plan where plan_id="+this.plan_id);
			if (rs.next()) {
				busitype = rs.getString("busitype");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs !=null) {
                PubFunc.closeResource(rs);
            }
		}
		
		return busitype;
	}
	
	/**
	 * 根据template_id判断是模板类型，1权重，2分值
	 * @return
	 */
	private String getStatus(){
		String status = "";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select status from per_template where template_id = '"+this.template_id+"'");
			if (rs.next()) {
				status = rs.getString("status");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs !=null) {
                PubFunc.closeResource(rs);
            }
		}
		
		return status;
	}
	
	//显示详细
	public String getTableHtmlDetail(){
		StringBuffer html = new StringBuffer();
		try{
			
			StringBuffer htmlContext = new StringBuffer("");
			StringBuffer r_item = new StringBuffer("");
			StringBuffer score = new StringBuffer("");
			HashMap existWriteItem = new HashMap();
			LazyDynaBean abean = null;
			LazyDynaBean a_bean = null;
			StringBuffer extendtHead = new StringBuffer();
			
			//int index=0;//序号
			// 输出表头
			int colCount = this.a_lays + 2;//列的层数（只包括项目和指标）
			extendtHead.append("<table   class='ListTable_self2' >");
			extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
			//extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='1' nowrap >序号</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='" + this.a_lays + "' nowrap >"+ResourceFactory.getProperty("kh.field.projectname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"200\" nowrap >"+ResourceFactory.getProperty("kjg.title.indexname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap >标准分值</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap >权重</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"100\" nowrap >"+ResourceFactory.getProperty("kh.field.scorevalue")+"</td>\r\n");
			extendtHead.append("</tr>\r\n");
			
			//输出表体
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				htmlContext.append("<tr>\r\n");
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);//项目之间的继承关系
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					if (existWriteItem.get(itemid) != null){
						if(e==0){
							htmlContext.delete(htmlContext.length()-8, htmlContext.length());
						}
						continue;
					}
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");
					/** 该项目所占的行数 */
					int colspan = ((itemPointNum.get(itemid) == null ? 0 : ((Integer) itemPointNum.get(itemid)).intValue()) + (childItemLinkMap.get(itemid) == null ? 0 : ((Integer) childItemLinkMap.get(itemid)).intValue()));
					/** 画出该项目 */
					//index++;//画项目时也要使序号加1
					//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+(index)+"</td>");
					htmlContext.append(writeTd(itemdesc, colspan, "left", this.td_width));//画出项目
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0) 
						{
							for (int h = 0; h < fieldlistp.size(); h++)
							{
								LazyDynaBean xbean = (LazyDynaBean) fieldlistp.get(h);
								String pointid = (String) xbean.get("point_id");
								if (h != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
								
								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow' >" + (String) xbean.get("name") + "</td>");
								
								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								
								
								htmlContext.append("</tr>\r\n");
							}
						}
						/** 没有指标 */
						
						else
						{
							
							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.a_lays - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
								htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
								htmlContext.append("&nbsp;");
								htmlContext.append("</td>");
								
								htmlContext.append("</tr>\r\n");
							}
						}
					}
					
					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(itemid);
						
						if (fieldlist != null && fieldlist.size() != 0)
						{
							for (int x = 0; x < fieldlist.size(); x++)
							{
								if (x != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
								
								LazyDynaBean xbean = (LazyDynaBean) fieldlist.get(x);
								String pointid = (String) xbean.get("point_id");
								
								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow' width=\"200\" ");
								
								htmlContext.append(">" + (String) xbean.get("name") + "</td>");
								
								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointStandardScoreMap.get(pointid));//标准分值
								htmlContext.append("</td>");
								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointStandardRankMap.get(pointid));//权重
								htmlContext.append("</td>");
								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								
								htmlContext.append("</tr>\r\n");
							}
						} else
						{
							for (int f = 0; f < this.a_lays - layer + 1; f++)
							{
								htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
							htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							htmlContext.append("&nbsp;");
							htmlContext.append("</td>");
							
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
			htmlContext.append("</table>");
			html.append(extendtHead.toString());
			html.append(htmlContext.toString());
			return html.toString();	
		} catch (Exception e){
			e.printStackTrace();
		}
		return html.toString();
	}
	//画出带有"评分说明"的卡片
	public String getTableHtml(String scoreExplainFlag){

		StringBuffer html = new StringBuffer();
		try{
			
			StringBuffer htmlContext = new StringBuffer("");
			StringBuffer r_item = new StringBuffer("");
			StringBuffer score = new StringBuffer("");
			HashMap existWriteItem = new HashMap();
			LazyDynaBean abean = null;
			LazyDynaBean a_bean = null;
			StringBuffer extendtHead = new StringBuffer();
			
			//int index=0;//序号
			// 输出表头
			int colCount = this.a_lays + 2;//列的层数（只包括项目和指标）
			extendtHead.append("<table   class='ListTable_self2' >");
			//画第一层表头
			extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
			//extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='1' nowrap >序号</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center'  rowspan='2' colspan='" + this.a_lays + "' nowrap >"+ResourceFactory.getProperty("kh.field.projectname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' rowspan='2' width=\"200\" nowrap >"+ResourceFactory.getProperty("kjg.title.indexname")+"</td>\r\n");
			extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' rowspan='2' width=\"100\" nowrap >"+ResourceFactory.getProperty("kh.field.scorevalue")+"</td>\r\n");
			if(this.mainbodyNum!=0) {
                extendtHead.append("<td class='TableRow_2rows' style='border-top:0;'  valign='middle' align='center' colspan='"+this.mainbodyNum+"' nowrap >"+ResourceFactory.getProperty("lable.performance.DeductMark")+"</td>\r\n");
            }
			extendtHead.append("</tr>\r\n");
			//画第二层表头
			extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
			Set key = mainbodyMap.keySet();
		    for (Iterator it = key.iterator(); it.hasNext();) {
		    	String s = (String) it.next();
				extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' width=\"200\" nowrap >"+mainbodyMap.get(s)+"</td>\r\n");
		    }
			extendtHead.append("</tr>\r\n");
			
			//输出表体
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				htmlContext.append("<tr>\r\n");
				ArrayList linkParentList = (ArrayList) this.leafItemLinkMap.get(item_id);//项目之间的继承关系
				/** 叶子项目的继承关系列表 */
				for (int e = linkParentList.size() - 1; e >= 0; e--)
				{
					a_bean = (LazyDynaBean) linkParentList.get(e);
					String itemid = (String) a_bean.get("item_id");
					if (existWriteItem.get(itemid) != null) {
                        continue;
                    }
					existWriteItem.put(itemid, "1");
					String itemdesc = (String) a_bean.get("itemdesc");
					/** 该项目所占的行数 */
					int colspan = ((itemPointNum.get(itemid) == null ? 0 : ((Integer) itemPointNum.get(itemid)).intValue()) + (childItemLinkMap.get(itemid) == null ? 0 : ((Integer) childItemLinkMap.get(itemid)).intValue()));
					/** 画出该项目 */
					//index++;//画项目时也要使序号加1
					//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+(index)+"</td>");
					htmlContext.append(writeTd(itemdesc, colspan, "left", this.td_width));//画出项目
					if (e != 0)
					{
						/** 该项目的层数 */
						int layer = Integer.parseInt((String) layMap.get(itemid));
						/** 对应指标列表 */
						ArrayList fieldlistp = (ArrayList) this.itemHaveFieldList.get(itemid);
						/** 该项目有指标 */
						if (fieldlistp != null && fieldlistp.size() > 0) 
						{
							for (int h = 0; h < fieldlistp.size(); h++)
							{
								LazyDynaBean xbean = (LazyDynaBean) fieldlistp.get(h);
								String pointid = (String) xbean.get("point_id");
								if (h != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
									
								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow' >" + (String) xbean.get("name") + "</td>");

								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								for (Iterator it = key.iterator(); it.hasNext();) {
							    	String s = (String) it.next();
							    	HashMap pointReasonMap = (HashMap)mainbodyTotal.get(s);
							    	String reasonsPart = "";
							    	String reasonsWhole = "";
							    	if(pointReasonMap.get(pointid)!=null){
							    		reasonsPart = ((String)pointReasonMap.get(pointid)).split("`")[0];
							    		reasonsWhole = ((String)pointReasonMap.get(pointid)).split("`")[1];
							    	}
							    	htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow'>");
							    	htmlContext.append("<a href='javascript:showExplain(\""+reasonsWhole+"\")' >");
							    	htmlContext.append(reasonsPart+"</a></td>");
							    }
								htmlContext.append("</tr>\r\n");
							}
						}
						/** 没有指标 */

						else
						{

							if (ifHasChildMap.get(itemid) == null)
							{
								for (int f = 0; f < this.a_lays - layer + 1; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
								htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
								htmlContext.append("&nbsp;");
								htmlContext.append("</td>");
								for (int ii=0;ii<mainbodyNum;ii++) {
							    	htmlContext.append("<td align=\"right\" class='RecordRow' width=\"200\">");
									htmlContext.append("&nbsp;");
									htmlContext.append("</td>");
							    }
								htmlContext.append("</tr>\r\n");
							}
						}
					}

					if (e == 0)
					{
						int layer = Integer.parseInt((String) layMap.get(itemid));
						ArrayList fieldlist = (ArrayList) this.itemHaveFieldList.get(itemid);

						if (fieldlist != null && fieldlist.size() != 0)
						{
							for (int x = 0; x < fieldlist.size(); x++)
							{
								if (x != 0){
									//index++;
									htmlContext.append("<tr>\r\n");
									//htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>"+index+"</td>");
								}
									
								LazyDynaBean xbean = (LazyDynaBean) fieldlist.get(x);
								String pointid = (String) xbean.get("point_id");

								for (int f = 0; f < this.a_lays - layer; f++)
								{
									htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
								htmlContext.append("<td align=\"left\" class='RecordRow' width=\"200\" ");
								
								htmlContext.append(">" + (String) xbean.get("name") + "</td>");

								htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
								htmlContext.append((String)this.pointScoreMap.get(pointid));
								htmlContext.append("</td>");
								for (Iterator it = key.iterator(); it.hasNext();) {
							    	String s = (String) it.next();
							    	HashMap pointReasonMap = (HashMap)mainbodyTotal.get(s);
							    	String reasonsPart = "";
							    	String reasonsWhole = "";
							    	if(pointReasonMap.get(pointid)!=null){
							    		reasonsPart = ((String)pointReasonMap.get(pointid)).split("`")[0];
							    		reasonsWhole = ((String)pointReasonMap.get(pointid)).split("`")[1];
							    	}
							    	htmlContext.append("<td align=\"left\" width=\"200\" class='RecordRow'>");
							    	htmlContext.append("<a href='javascript:showExplain(\""+reasonsWhole+"\")' >");
							    	htmlContext.append(reasonsPart+"</a></td>");
							    }
								htmlContext.append("</tr>\r\n");
							}
						} else
						{
							for (int f = 0; f < this.a_lays - layer + 1; f++)
							{
								htmlContext.append("<td align=\"left\" width=\"100\" class='RecordRow'>&nbsp;&nbsp;</td>");
							}
							/** 兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分 */
							htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							htmlContext.append("&nbsp;");
							htmlContext.append("</td>");
							for (int ii=0;ii<mainbodyNum;ii++) {
						    	htmlContext.append("<td align=\"right\" class='RecordRow' width=\"200\">");
								htmlContext.append("&nbsp;");
								htmlContext.append("</td>");
						    }
							htmlContext.append("</tr>\r\n");
						}
					}
				}
			}
		htmlContext.append("</table>");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		return html.toString();	
		} catch (Exception e){
			e.printStackTrace();
		}
		return html.toString();
	
	}
	
	///////////////////////////////////////////////辅助函数///////////////////////////////////////////////////////////////////////
	int a_lays = 0;//求层数

    public void getLays(ArrayList items){
		for (Iterator t = items.iterator(); t.hasNext();){
		    String[] item = (String[]) t.next();
		    if (item[1] == null){
				int lay = CountLevel(item, items);
				if (a_lays < lay){
				    a_lays = lay;
				}
		    }
		}
    }
    int CountLevel(String[] MyNode, ArrayList list){
		if (MyNode == null) {
            return -1;
        }
		int iLevel = 1;
		int iMaxLevel = 0;
		ArrayList subNodeList = new ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
		    String[] temp = (String[]) list.get(i);
		    if (temp[1] != null && temp[1].equals(MyNode[0])) {
                subNodeList.add(temp);
            }
		}
	
		for (int i = 0; i < subNodeList.size(); i++)
		{
		    iLevel = CountLevel((String[]) subNodeList.get(i), list) + 1;
		    if (iMaxLevel < iLevel) {
                iMaxLevel = iLevel;
            }
		}
		return iMaxLevel;
    }

	/**
	 * 叶子项目列表
	 */
	public void get_LeafItemList()
	{
		LazyDynaBean abean = null;
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			abean = (LazyDynaBean) this.templateItemList.get(i);
			String parent_id = (String) abean.get("parent_id");
			if (parent_id.length() == 0)
			{
				setLeafItemFunc(abean);
			}
		}
	}

	// 递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id = (String) abean.get("item_id");
		String child_id = (String) abean.get("child_id");
		if (child_id.length() == 0 || isLeaf(item_id, child_id))
		{
			this.leafItemList.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parent_id = (String) a_bean.get("parent_id");
			if (parent_id.equals(item_id)) {
                setLeafItemFunc(a_bean);
            }
		}
	}

	public boolean isLeaf(String item_id, String child_id)
	{
		boolean flag = true;
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select * from per_template_item where parent_id=" + item_id + " or item_id=" + child_id);
			while (rs.next())
			{
				flag = false;
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	//取得所有的项目
	public ArrayList getTemplateItemList(String templateID)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select * from  per_template_item where template_id='" + templateID + "'  order by seq");
			LazyDynaBean abean = null;
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				abean.set("parent_id", rowSet.getString("parent_id") != null ? rowSet.getString("parent_id") : "");
				abean.set("child_id", rowSet.getString("child_id") != null ? rowSet.getString("child_id") : "");
				abean.set("template_id", rowSet.getString("template_id"));
				abean.set("itemdesc", PubFunc.toHtml(rowSet.getString("itemdesc")));
				abean.set("seq", rowSet.getString("seq"));
				abean.set("kind", rowSet.getString("kind") != null ? rowSet.getString("kind") : "1");
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				abean.set("rank_type", rowSet.getString("rank_type") != null ? rowSet.getString("rank_type") : "");
				list.add(abean);
				if (rowSet.getString("parent_id") == null || "".equals(rowSet.getString("parent_id")))
				{
					this.parentList.add(abean);//初始化父亲节点
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getPointStandardScoreMap(String e01a1){
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select * from  per_competency_modal pcm where pcm.object_id='"+e01a1+"' and pcm.object_type='3'");
			LazyDynaBean abean = null;
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("point_id", rowSet.getString("point_id"));
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				map.put(rowSet.getString("point_id"), rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getPointStandardRankMap(String e01a1){
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao.search("select * from  per_competency_modal pcm where pcm.object_id='"+e01a1+"' and pcm.object_type='3'");
			LazyDynaBean abean = null;
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("point_id", rowSet.getString("point_id"));
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				map.put(rowSet.getString("point_id"), rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**得到所有指标分类  郭峰*/
	public ArrayList getItemListByModel(String e01a1)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sbsql = new StringBuffer("");
			sbsql.append("select pcm.point_type,pcm.score,pcm.rank,ci.codeitemdesc from per_competency_modal pcm left join codeitem ci on pcm.point_type=ci.codeitemid and ci.codesetid='70' where pcm.object_type='3' and pcm.object_id = '"+e01a1+"'");
	    	sbsql.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
			rowSet = dao.search(sbsql.toString());
			LazyDynaBean abean = null;
			int i = 0;
			ArrayList lastlist = new ArrayList();
			while (rowSet.next())
			{
				String temppoint_type = rowSet.getString("point_type");
				if(temppoint_type==null || "".equals(temppoint_type)){
					temppoint_type = "-9999";
				}
				if(lastlist.contains(temppoint_type)){
					continue;
				}
				i++;
				abean = new LazyDynaBean();
				String itemdesc = rowSet.getString("codeitemdesc");
				if(itemdesc==null || "".equals(itemdesc)){
					//itemdesc = "无指标分类";    //岗位素质模型指标没有指标分类  2013.12.3 pjf
    	    		itemdesc = "岗位素质模型指标";
				}
				abean.set("item_id", temppoint_type);
				abean.set("parent_id", "");
				abean.set("child_id", "");
				abean.set("template_id", "");
				abean.set("itemdesc",itemdesc);
				abean.set("seq", String.valueOf(i));
				abean.set("kind", "1");
				abean.set("score", rowSet.getString("score") == null ? "0" : this.myformat1.format(rowSet.getDouble("score")));
				abean.set("rank", rowSet.getString("rank") == null ? "0" : this.myformat1.format(rowSet.getDouble("rank")));
				abean.set("rank_type","0");
				list.add(abean);
				lastlist.add(temppoint_type);
				this.parentList.add(abean);//初始化父亲节点
				
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得项目拥有的叶子节点数
	 */
	public HashMap getItemPointNum()
	{
		HashMap map = new HashMap();
		LazyDynaBean a_bean = null;
		LazyDynaBean aa_bean = null;
		for (int i = 0; i < templateItemList.size(); i++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(i);
			ArrayList list = new ArrayList();
			getLeafItemList(a_bean, list);
			int n = 0;
			for (int j = 0; j < list.size(); j++)
			{
				aa_bean = (LazyDynaBean) list.get(j);
				String item_id = (String) aa_bean.get("item_id");
				if (itemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) itemToPointMap.get(item_id)).size();
                } else if (this.selfItemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) selfItemToPointMap.get(item_id)).size();
                } else {
                    n += 1;
                }
			}
			map.put((String) a_bean.get("item_id"), new Integer(n));
		}
		return map;
	}
	public void getLeafItemList(LazyDynaBean abean, ArrayList list)
	{
		String item_id = (String) abean.get("item_id");
		String child_id = (String) abean.get("child_id");

		if (child_id.length() == 0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parent_id = (String) a_bean.get("parent_id");
			if (parent_id.equals(item_id)) {
                getLeafItemList(a_bean, list);
            }
		}

	}
	/**
	 * 画单元格
	 */
	private String writeTd(String context, int rowspan, String align, int width)
	{
		StringBuffer td = new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle'  nowrap align='" + align + "'");
		if (rowspan != 0) {
            td.append(" rowspan='" + (rowspan) + "' ");
        } else {
            td.append(" height='" + td_height + "' ");
        }
		td.append("  width='" + width + "'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}
	
	//得到项目和指标的对应关系
	public HashMap getItemToPointMap(String templateID)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select ptp.*,pp.pointname from per_template_point ptp,per_point pp where  ptp.item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='");
			sql.append(templateID + "') and ptp.point_id=pp.point_id order by ptp.seq");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while (rs.next())
			{
				bean = new LazyDynaBean();

				bean.set("point_id", rs.getString("point_id"));
				bean.set("score", rs.getString("score") == null ? "0" : this.myformat1.format(rs.getDouble("score")));
				bean.set("rank", rs.getString("rank") == null ? "0" : this.myformat1.format(rs.getDouble("rank")));
				bean.set("item_id", rs.getString("item_id"));
				bean.set("pointname", rs.getString("pointname"));
				String item_id = rs.getString("item_id");
				this.pointSetList.add(rs.getString("point_id"));
				if (map.get(item_id) != null)
				{
					ArrayList list = (ArrayList) map.get(item_id);
					list.add(bean);
					map.put(item_id, list);
				} else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id, list);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**得到项目和指标的对应关系  郭峰*/
	public HashMap getItemToPointMapByModel(String e01a1)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sbsql = new StringBuffer("");
			sbsql.append("select pcm.point_type,pcm.point_id,pcm.score,pcm.rank,pp.pointname from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id where pcm.object_type='3' and pcm.object_id = '"+e01a1+"'");
	    	sbsql.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sbsql.toString());
			LazyDynaBean bean = null;
			while (rs.next())
			{
				bean = new LazyDynaBean();
				String temppoint_type = rs.getString("point_type");
				if(temppoint_type==null || "".equals(temppoint_type)){
					temppoint_type = "-9999";
				}
				bean.set("item_id", temppoint_type);
				bean.set("point_id", rs.getString("point_id"));
				bean.set("score", rs.getString("score") == null ? "0" : this.myformat1.format(rs.getDouble("score")));
				bean.set("rank", rs.getString("rank") == null ? "0" : this.myformat1.format(rs.getDouble("rank")));
				bean.set("pointname", rs.getString("pointname"));
				this.pointSetList.add(rs.getString("point_id"));
				if (map.get(temppoint_type) != null)
				{
					ArrayList list = (ArrayList) map.get(temppoint_type);
					list.add(bean);
					map.put(temppoint_type, list);
				} else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(temppoint_type, list);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	// 个性项目对应的指标或任务
	public HashMap getSelfItemToPointMap()
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from p04 where plan_id=" + this.plan_id);
			if("2".equals(this.object_type)) {
                buf.append(" and a0100='" + this.object_id+ "'");
            } else {
                buf.append(" and b0110='" + this.object_id+ "'");
            }
			buf.append(" and item_id in (select item_id from per_template_item where template_id='" + template_id + "' and kind=2) order by item_id, seq");

			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			LazyDynaBean bean = null;
			while (rs.next())
			{
				String chg_type = rs.getString("chg_type");
				if(chg_type!=null && "3".equals(chg_type)) {
                    continue;
                }
				
				bean = new LazyDynaBean();
				bean.set("point_id", rs.getString("p0401"));
				bean.set("score", rs.getString("p0413") == null ? "0" : this.myformat1.format(rs.getDouble("p0413")));
				bean.set("rank", rs.getString("p0415") == null ? "0" : this.myformat1.format(rs.getDouble("p0415")));
				bean.set("item_id", rs.getString("item_id"));
				bean.set("pointname", rs.getString("p0407"));
				String item_id = rs.getString("item_id");
				if (map.get(item_id) != null)
				{
					ArrayList list = (ArrayList) map.get(item_id);
					list.add(bean);
					map.put(item_id, list);
				} else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id, list);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getItemHasFieldList2()
	{

		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			for (int i = 0; i < this.templateItemList.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
				String itemid = (String) bean.get("item_id");
				String kind = (String) bean.get("kind");
				StringBuffer sql = new StringBuffer();

					StringBuffer buf = new StringBuffer();
					ArrayList list = new ArrayList();
					if("1".equals(method)){//如果是360管理
						buf.append("select pp.point_id,pp.pointname ");
						buf.append("from per_template_item pti,per_template_point ptp,per_point pp ");
						buf.append("where ptp.item_id=pti.item_id and ptp.point_id=pp.point_id and template_id='"+template_id+"' and pti.item_id='"+itemid+"' ");
						buf.append("order by ptp.seq");
						rs = dao.search(buf.toString());
						while (rs.next())
						{
							LazyDynaBean a_bean = new LazyDynaBean();
							a_bean.set("point_id", rs.getString("point_id"));
							a_bean.set("itemid", itemid);
							a_bean.set("name", rs.getString("pointname"));
							list.add(a_bean);
						}
					}else if("2".equals(method)){
						buf.append("select * from p04 where plan_id=" + this.plan_id);
						if("2".equals(this.object_type)) {
                            buf.append(" and a0100='" + this.object_id+ "'");
                        } else {
                            buf.append(" and b0110='" + this.object_id+ "'");
                        }
						buf.append(" and item_id ='" + itemid + "' order by  seq");
						rs = dao.search(buf.toString());
						while (rs.next())
						{
							String chg_type = rs.getString("chg_type");
							if(chg_type!=null && "3".equals(chg_type)) {
                                continue;
                            }
							LazyDynaBean a_bean = new LazyDynaBean();
							a_bean.set("point_id", rs.getString("p0401"));
							a_bean.set("score", rs.getString("p0413") == null ? "0" : this.myformat1.format(rs.getDouble("p0413")));
							a_bean.set("rank", rs.getString("p0415") == null ? "0" : this.myformat1.format(rs.getDouble("p0415")));
							a_bean.set("itemid", itemid);
							a_bean.set("name", rs.getString("p0407") == null ? "" :(rs.getString("p0407")));
							a_bean.set("seq", rs.getString("seq"));
							a_bean.set("fromflag", rs.getString("fromflag"));
							list.add(a_bean);
						}
					}
					
					map.put(itemid, list);
			
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getItemHasFieldListByModel(String e01a1)
	{
		
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			for (int i = 0; i < this.templateItemList.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
				String itemid = (String) bean.get("item_id");
				String addSql = " and pcm.point_type='"+itemid+"'";
				if("-9999".equals(itemid)){
					addSql=" and (pcm.point_type='' or pcm.point_type is null)";
				}
				StringBuffer sbsql = new StringBuffer();
				ArrayList list = new ArrayList();
				sbsql.append("select pcm.point_id,pp.pointname from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id where pcm.object_type='3' and pcm.object_id = '"+e01a1+"'"+addSql);
		    	sbsql.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
				rs = dao.search(sbsql.toString());
				while (rs.next())
				{
					LazyDynaBean a_bean = new LazyDynaBean();
					a_bean.set("point_id", rs.getString("point_id"));
					a_bean.set("itemid", itemid);
					a_bean.set("name", rs.getString("pointname"));
					list.add(a_bean);
				}	
				map.put(itemid, list);
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 叶子项目对应的继承关系
	 * 
	 * @return
	 */
	public HashMap getLeafItemLinkMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean = null;
			for (int i = 0; i < this.leafItemList.size(); i++)
			{
				abean = (LazyDynaBean) this.leafItemList.get(i);
				String item_id = (String) abean.get("item_id");
				String parent_id = (String) abean.get("parent_id");
				ArrayList linkList = new ArrayList();
				getParentItem(linkList, abean);
				if (linkList.size() > a_lays) {
                    a_lays = linkList.size();
                }
				map.put(item_id, linkList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	// 寻找继承关系
	public void getParentItem(ArrayList list, LazyDynaBean abean)
	{
		String item_id = (String) abean.get("item_id");
		String parent_id = (String) abean.get("parent_id");
		/** 顶级节点 */
		if (parent_id.length() == 0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean = null;
		for (int i = 0; i < templateItemList.size(); i++)
		{
			a_bean = (LazyDynaBean) this.templateItemList.get(i);
			String itemid = (String) a_bean.get("item_id");
			String parentid = (String) a_bean.get("parent_id");
			if (itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list, a_bean);
			}
		}
	}
	public HashMap getChildItemLinkMap()
	{
		HashMap map = new HashMap();
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) this.templateItemList.get(i);
			ArrayList list = new ArrayList();
			doMethod(bean, list);
			LazyDynaBean aa_bean = null;
			int n = 0;
			for (int j = 0; j < list.size(); j++)
			{
				aa_bean = (LazyDynaBean) list.get(j);
				String item_id = (String) aa_bean.get("item_id");
				if (itemToPointMap.get(item_id) != null) {
                    n += ((ArrayList) itemToPointMap.get(item_id)).size();
                }

			}
			map.put((String) bean.get("item_id"), new Integer(n));
		}
		return map;
	}
	public void doMethod(LazyDynaBean bean, ArrayList list)
	{
		String itemid = (String) bean.get("item_id");
		String childid = (String) bean.get("child_id");
		if (childid.length() == 0)
		{
			// list.add(bean);
			return;
		} else
		{
			list.add(bean);
		}
		for (int j = 0; j < this.templateItemList.size(); j++)
		{
			LazyDynaBean a_bean = (LazyDynaBean) this.templateItemList.get(j);
			String parentid = (String) a_bean.get("parent_id");
			if (parentid.equals(itemid))
			{
				doMethod(a_bean, list);
			}
		}
	}
	public void doMethod2()
	{
		for (int i = 0; i < parentList.size(); i++)
		{
			LazyDynaBean bean = (LazyDynaBean) parentList.get(i);
			String itemid = (String) bean.get("item_id");
			layMap.put(itemid, "1");
			doM(bean, 1);
		}
	}

	public void doM(LazyDynaBean bean, int lay)
	{
		lay++;
		for (int i = 0; i < this.templateItemList.size(); i++)
		{
			LazyDynaBean a_bean = (LazyDynaBean) this.templateItemList.get(i);
			String itemid = (String) bean.get("item_id");
			String a_itemid = (String) a_bean.get("item_id");
			String parentid = (String) a_bean.get("parent_id");
			if (parentid.equals(itemid))
			{
				ifHasChildMap.put(itemid, "1");
				layMap.put(a_itemid, lay + "");
				doM(a_bean, lay);
			}
		}
	}

	public HashMap getPointScoreMap(){
		String decimal = this.getDecimal();//格式化数据的规则
		java.text.DecimalFormat df = new java.text.DecimalFormat(decimal);
		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			int n = this.pointSetList.size();
			if(n!=0){//如果有共性指标
				sb.append("select ");
				for(int i=0;i<n;i++){
					sb.append("c_"+(String)pointSetList.get(i));
					if(i!=n-1) {
                        sb.append(", ");
                    }
				}
				sb.append(" from per_result_"+plan_id+" where object_id='"+object_id+"'");
				rowSet = dao.search(sb.toString());
				if(rowSet.next()){
					for(int j=0;j<n;j++){
						map.put((String)pointSetList.get(j), df.format(rowSet.getDouble(j+1)));
					}
				}
			}
			if("2".equals(method)){//如果是目标管理，还要加上个性项目的分数
				StringBuffer sb2 = new StringBuffer();
				if("2".equals(this.object_type)) {
                    sb2.append("select p0401,targetscore from p04 where plan_id='"+plan_id+"' and (chg_type<>3 or chg_type is null) and a0100='"+object_id+"' and item_id in (select item_id from per_template_item where template_id='" + template_id + "' and kind=2)");
                } else {
                    sb2.append("select p0401,targetscore from p04 where plan_id='"+plan_id+"' and (chg_type<>3 or chg_type is null) and b0110='"+object_id+"' and item_id in (select item_id from per_template_item where template_id='" + template_id + "' and kind=2)");
                }
				rowSet = dao.search(sb2.toString());
				while(rowSet.next()){
					map.put(rowSet.getString("p0401"), df.format(rowSet.getDouble("targetscore")));
				}
			}
			
			//定量统一打分指标 分取自业绩数据录入  zzk 2014/2/21
			String sql = " SELECT P0401, P0415 FROM P04, per_point "
				+ " WHERE plan_id="
				+ plan_id

				+ " AND P0401=point_id AND per_point.status=1 and per_point.pointkind=1 ";//per_point.status 0不统一打分 1统一打分 per_point.pointkind 0定性  1定量
			if(!"2".equals(this.object_type)) {
                sql += " AND b0110='" + object_id + "'";
            } else {
                sql += " AND A0100='" + object_id + "'";
            }
			
			rowSet=dao.search(sql);
			DbWizard dbWizard = new DbWizard(this.conn);
			RowSet rowSet2=null;
			while(rowSet.next()){

				String tableName = "per_gather_" + plan_id;
				if (dbWizard.isExistTable(tableName, false))
				{
					String p0401 = rowSet.getString("p0401");
					String sql1 = "SELECT * FROM  per_gather_score_"
							+ plan_id
							+ " where gather_id in (SELECT gather_id FROM ";
					sql1 += "per_gather_" + plan_id
							+ " WHERE object_id='" + object_id
							+ "' )";
					rowSet2 = dao.search(sql1);
					if (rowSet2.next()) {
						String str = "T_" + p0401 + "_S";
						map.put(p0401, df.format(rowSet2.getDouble(str)));
					}
				}

			
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getPointScoreMapByModel(){
		String decimal = this.getDecimal();//格式化数据的规则
		java.text.DecimalFormat df = new java.text.DecimalFormat(decimal);
		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			int n = this.pointSetList.size();
			if(n!=0){//如果有共性指标
				
				sb.append("select point_id,score from per_history_result where object_id='"+object_id+"' and  plan_id="+plan_id+""); 
				rowSet = dao.search(sb.toString());
				while(rowSet.next()){
					map.put((String)rowSet.getString("point_id"), df.format(rowSet.getDouble("score")));
				}
				if(map.size()==0){//如果没有给某个考核对象打分，那么历史表中根本没有该考核对象的数据
					Set key = itemToPointMap.keySet();
					for (Iterator it = key.iterator(); it.hasNext();) {
						String s = (String) it.next();
						ArrayList innerlist = (ArrayList)this.itemToPointMap.get(s);
						for(int j=0;j<innerlist.size();j++){
							LazyDynaBean tempbean = (LazyDynaBean)innerlist.get(j);
							String point_id = (String)tempbean.get("point_id");
							map.put(point_id, df.format(0));
						}
					}
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	//得到小数位数
	public String getAccuracy(){
		String accuracy = "";
		Hashtable htxml = new Hashtable();
		LoadXml _loadxml=null;
		if(BatchGradeBo.planLoadXmlMap.get(plan_id)==null)
		{
			_loadxml = new LoadXml(this.conn,plan_id);
			BatchGradeBo.planLoadXmlMap.put(plan_id,_loadxml);
		}
		else {
            _loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(plan_id);
        }
		htxml =_loadxml.getDegreeWhole();
		accuracy = (String)htxml.get("KeepDecimal"); // 小数位
		
		return accuracy;
	}
	//格式化数据的规则
	public String getDecimal(){
		String str = "0.";
		int decimalInt = Integer.parseInt(this.accuracy);
		for(int i=0;i<decimalInt;i++){
			str+="0";
		}
		return str;
	}
	//得到计划参数中的"显示评分说明"
	public String getScoreExplain(Hashtable plan_parameters){
		String str = "0";
		String strTemp = (String)plan_parameters.get("showDeductionCause");
		if("true".equalsIgnoreCase(strTemp)) {
            str = "1";
        }
		return str;
	}
	//得到有几个主体给该对象打分
	public int getMainbodyNum(){
		int num = 0;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			if("1".equals(this.method))//如果是360
            {
                sb.append("select count(distinct mainbody_id) mainbodyNum from per_table_"+plan_id+" where object_id='"+object_id+"'");
            } else {
                sb.append("select count(distinct mainbody_id) mainbodyNum from per_target_evaluation where plan_id='"+this.plan_id+"' and object_id='"+object_id+"'");
            }
			rowSet = dao.search(sb.toString());
			if(rowSet.next()){
				num = rowSet.getInt("mainbodyNum");
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return num;
	}
	public HashMap getMainbodyMap(){

		HashMap map = new HashMap();
		try{
			String plan_type = getPlanType();//得到计划类型 0:不记名 1:记名
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			if("1".equals(method)) {
                sb.append("select distinct mainbody_id from per_table_"+plan_id+" where object_id='"+object_id+"'");
            } else {
                sb.append("select distinct mainbody_id from per_target_evaluation where plan_id='"+this.plan_id+"' and object_id='"+object_id+"'");
            }
			rowSet = dao.search(sb.toString());
			ArrayList list = new ArrayList();
			while(rowSet.next()){
				list.add(rowSet.getString("mainbody_id"));
			}
			for(int i=0;i<list.size();i++){
				sb.delete(0, sb.length());
				sb.append("select a0101 from per_mainbody where plan_id='"+plan_id+"' and mainbody_id='"+list.get(i)+"'");
				rowSet = dao.search(sb.toString());
				if(rowSet.next()){
					if("1".equals(plan_type)) {
                        map.put(list.get(i), rowSet.getString("a0101"));
                    } else {
                        map.put(list.get(i), "主体"+(i+1));
                    }
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	
	}
	//得到评分说明总的map(大map套着小map,小map中point_id为键值，reasons为内容)
	public HashMap getMainbodyTotal(){

		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			Set key = mainbodyMap.keySet();
		    for (Iterator it = key.iterator(); it.hasNext();) {
		    	HashMap pointreason = new HashMap();
		    	String s = (String) it.next();
		    	sb.delete(0, sb.length());
		    	if("1".equals(method)) {
                    sb.append("select point_id,reasons from per_table_"+plan_id+" where object_id='"+object_id+"' and mainbody_id='"+s+"'");
                } else {
                    sb.append("select p.p0401 point_id,reasons from per_target_evaluation pte,p04 p where pte.p0400=p.p0400 and pte.object_id='"+object_id+"' and pte.mainbody_id='"+s+"' and pte.plan_id='"+plan_id+"'");
                }
		    	rowSet = dao.search(sb.toString());
		    	while(rowSet.next()){
		    		String strTemp = rowSet.getString("reasons");
		    		if(strTemp==null || "".equals(strTemp)) {
                        strTemp = " ";
                    }
		    		String strTemp2 = strTemp;//如果字数超过13个，超出部分加上省略号
		    		if(strTemp.length()>12) {
                        strTemp2 = strTemp.substring(0, 12)+"......";
                    }
		    		pointreason.put(rowSet.getString("point_id"),strTemp2+"`"+strTemp);
		    	}
		    	map.put(s, pointreason);
		    }
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	
	}
	//得到考核对象列表
	public ArrayList getObjectList(){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			rowSet = dao.search("select object_id,a0101 from per_object where plan_id='"+plan_id+"' order by a0000");
			while(rowSet.next()){
				String object_id = rowSet.getString("object_id");
				String a0101 = rowSet.getString("a0101");
				CommonData obj=new CommonData(PubFunc.encrypt(object_id),a0101);
				list.add(obj);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到该计划是否记名
	public String getPlanType(){
		PerformanceImplementBo pb = new PerformanceImplementBo(conn);
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String plan_type=vo.getString("plan_type");
		return plan_type;
	}
}