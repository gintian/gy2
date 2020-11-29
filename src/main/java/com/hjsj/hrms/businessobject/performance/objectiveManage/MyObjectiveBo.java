package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:MyObjectiveBo.java</p>
 * <p>Description>:MyObjectiveBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-31 下午03:57:31</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class MyObjectiveBo 
{
	private Connection conn;
	private HashMap map;
	private UserView userView;
	public MyObjectiveBo(Connection conn)
	{
		this.conn=conn;
		map = new HashMap();
	}
	public MyObjectiveBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		map = new HashMap();
		this.userView=userView;
	}
	private int lay=0;
    private int rowNum = 0; // 行坐标
    private short colIndex = 0; // 纵坐标
	private HSSFWorkbook hwb;
	private HSSFSheet sheet;
	private String[] records_array; 
	private HashMap itemToPointMap=new HashMap();
	private ArrayList templateItemList = new ArrayList();
	private HashMap leafItemLinkMap = new HashMap();
	private HashMap itemPointNum= new HashMap();
	private HashMap itemHaveFieldList=new HashMap();
	private HashMap childItemLinkMap= new HashMap();
	private HashMap nameMap=new HashMap();
	private ArrayList parentList= new ArrayList();
	private ArrayList leafItemList = new ArrayList();
	private HashMap layMap=new HashMap();
	private HashMap ifHasChildMap=new HashMap();
	private String status ="0";
	private HSSFCellStyle centerstyle ;
	private ArrayList p04List = new ArrayList();
	private HSSFRow row = null;
	private String model="1";
	boolean hasAddMinusItem=false; //模板中是否设置了加扣分项目 （动态项目设置）
	String addMinusPointStr="";    //加扣分指标
	/**
	 * 准备导出excel的数据
	 */
	public void initData(String planid,String a0100,ContentDAO dao,boolean flag,UserView userView)
	{
		try
		{
    		RecordVo vo = new RecordVo("per_plan");
    		vo.setInt("plan_id", Integer.parseInt(planid));
     		vo=dao.findByPrimaryKey(vo);
     		String templateID=vo.getString("template_id");
     		RecordVo vo2=new RecordVo("per_template");
     		vo2.setString("template_id", templateID);
     		vo2=dao.findByPrimaryKey(vo2);
     		this.status=vo2.getString("status");
     		/**取得模板的所有项目并且初始化parentList列表*/
     		this.templateItemList=this.getTemplateItemList(templateID,vo.getInt("status"));
     		/**得到项目中所有的叶子项目*/
    		get_LeafItemList();
    		/**项目对应指标(取值p04)*/
    		this.itemToPointMap=this.getItemToPointMap(planid,a0100,dao,flag,userView,vo.getString("object_type"));
    		/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
    		this.leafItemLinkMap=getLeafItemLinkMap();
    		/**每个项目对应的叶子节点个数*/
    		this.itemPointNum=getItemPointNum();
    		/**除叶子节点外的节点的指标数量*/
    		this.childItemLinkMap=this.getChildItemLinkMap();
    		this.doMethod2();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	public HashMap getTraceItem(Hashtable ht_table,Hashtable planParam,String plan_id,String object_id)
	{
		HashMap map = new HashMap();
		try
		{
			String TargetTraceItem="";
			String AllowLeaderTrace="false";//AllowLeaderTrace//TargetTraceItem
			String hiden_str="";
			if(ht_table!=null)
			{
				
				if(ht_table.get("TargetTraceItem")!=null)
					TargetTraceItem=(String)ht_table.get("TargetTraceItem");
				if(TargetTraceItem!=null&&TargetTraceItem.trim().length()>0)
					AllowLeaderTrace=(String)ht_table.get("AllowLeaderTrace");
				else
					AllowLeaderTrace="false";
			}
			if(TargetTraceItem==null)
				TargetTraceItem="";
			
			if(planParam.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)planParam.get("TargetTraceEnabled")))
			{
				if(planParam.get("TargetTraceItem")!=null&&((String)planParam.get("TargetTraceItem")).trim().length()>0)
				{
					TargetTraceItem=(String)planParam.get("TargetTraceItem");
					AllowLeaderTrace=(String)planParam.get("AllowLeaderTrace");
				}
				else
				{
					TargetTraceItem="";
					AllowLeaderTrace="false";
				}
			}
			if(TargetTraceItem.length()>0)
			{
					String[] temps=TargetTraceItem.split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].length()>0)
							hiden_str+=temps[i].toUpperCase()+"/";
					}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public boolean  isPrivByOrg(String planid,String object_id,UserView view,String TargetDefineItem)
	{
		boolean flag = false;
		RowSet rs = null;
		try
		{
			FieldItem item  = DataDictionary.getFieldItem("score_org");
			if("1".equals(item.getState())&&(TargetDefineItem!=null&&!"".equals(TargetDefineItem.trim())&&TargetDefineItem.indexOf("SCORE_ORG")!=-1))
			{
				flag=true;
			}
			if(flag)
			{
				String un_functionary="";
				RecordVo plan_vo = new RecordVo("per_plan");
				ContentDAO dao = new ContentDAO(this.conn);
				plan_vo.setInt("plan_id", Integer.parseInt(planid));
				plan_vo=dao.findByPrimaryKey(plan_vo);
				if(plan_vo.getInt("object_type")!=2)
				{
					String sql = "select mainbody_id from per_mainbody where plan_id="+planid+" and object_id='"+object_id+"' and body_id=-1";
					rs = dao.search(sql);
					while(rs.next())
					{
						un_functionary=rs.getString(1);
					}
				}
				//本人或团队负责人,启动以前的计划，不控制，启动以后的计划，控制
				if(view.getA0100().equals(object_id)||view.getA0100().equals(un_functionary)||plan_vo.getInt("status")<4||plan_vo.getInt("status")==8)
				{
					/**本人或团队负责人，展现所有*/
					flag=false;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	/**model=1我的目标，=2 目标评估records 格式a0100-planid/a0100-planid    logo 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况 */
	public String ExportEXCEL(String records,String model,UserView userView,String body_id,String opt,String logo)
	{
		String fileName=userView.getUserName()+"_ObjectiveCard.xls";
		FileOutputStream fileOut = null;
		try
		{
			/**取得导出人员的名字（a0100-->a0101）*/
			this.getNameMap(records);
			String[] arr=records.split("/");
			hwb=new HSSFWorkbook();
			centerstyle = style(hwb, 1);
			HSSFCellStyle style=style(hwb,100);
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.conn);
			Hashtable ht_table=bo.analyseParameterXml();
			ContentDAO dao = new ContentDAO(this.conn);

		    FieldItem fielditem2=DataDictionary.getFieldItem("p0407");
		    String taskName="";
			if(fielditem2==null|| "任务内容".equalsIgnoreCase(fielditem2.getItemdesc().trim()))
			{
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
					taskName="工作目标";
				else
					taskName=ResourceFactory.getProperty("jx.khplan.point");
			}
			else
				taskName=fielditem2.getItemdesc();

			FieldItem fielditem=DataDictionary.getFieldItem("item_id");
			String projectName="";
			if(fielditem==null|| "项目号".equalsIgnoreCase(fielditem.getItemdesc().trim()))
				projectName="项目";
			else
				projectName=fielditem.getItemdesc();
			for(int u=0;u<arr.length;u++)
			{
				/**此处为了清空数据，要不然当选择多人导出的时候，会出现数据错乱的情况*/
				itemToPointMap=new HashMap();
				templateItemList = new ArrayList();
				leafItemLinkMap = new HashMap();
				itemPointNum= new HashMap();
				itemHaveFieldList=new HashMap();
				childItemLinkMap= new HashMap();
				parentList= new ArrayList();
				leafItemList = new ArrayList();
				layMap=new HashMap();
			    ifHasChildMap=new HashMap();
			    lay=0;
			    this.hasAddMinusItem=false;
			    this.addMinusPointStr="";
				String status ="0";
				p04List = new ArrayList();	
				String[] str=arr[u].split("-");
				sheet=null;
				/**一个人一页*/
				//String key=str[0]+str[1];
				//String sheetName=(String)this.nameMap.get(str[0]+str[1]);
				//System.out.println(key+"----->"+sheetName);
				
//				ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt);
				ObjectCardBo cardbo=new ObjectCardBo(this.conn,str[1],str[0],userView,model,body_id,opt);
				ArrayList alist = cardbo.getHeadList();
				cardbo.setTemplateItemList(cardbo.getTemplateItemList());
				this.hasAddMinusItem=cardbo.getHasAddMinusItem();
				this.addMinusPointStr=cardbo.getAddMinusPointStr();
				this.sheet=hwb.createSheet((String)this.nameMap.get(str[0]+str[1]));
				this.rowNum=0;
				this.colIndex=0;
				LoadXml loadxml=null;
				if(BatchGradeBo.getPlanLoadXmlMap().get(str[1])==null)
				{
					loadxml=new LoadXml(this.conn,str[1]);
					BatchGradeBo.getPlanLoadXmlMap().put(str[1],loadxml);
				}
				else
					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(str[1]);		
				Hashtable planParam=loadxml.getDegreeWhole();
				String cardField="";
				if(ht_table!=null)
				{
					if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
					{
						String temp=(String)ht_table.get("TargetDefineItem");
						temp=temp.replaceAll(",","");
						if(temp.trim().length()>0)
							cardField=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
					}
				}
				if(planParam.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)planParam.get("TargetTraceEnabled")))
				{
					if(planParam.get("TargetDefineItem")!=null&&((String)planParam.get("TargetDefineItem")).trim().length()>0)
					{
						String temp=(String)planParam.get("TargetDefineItem");
						temp=temp.replaceAll(",","");
						if(temp.trim().length()>0)
							cardField=(","+((String)planParam.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
					}
				}
				boolean flag=this.isPrivByOrg(str[1], str[0], userView, cardField);
				RecordVo plan_vo = new RecordVo("per_plan");
				plan_vo.setInt("plan_id", Integer.parseInt(str[1]));
				plan_vo=dao.findByPrimaryKey(plan_vo);
				String templateid = plan_vo.getString("template_id");
				RecordVo template_vo = new RecordVo("per_template");
				template_vo.setString("template_id", templateid);
				template_vo = dao.findByPrimaryKey(template_vo);
				initData(str[1],str[0],dao,flag,userView);
				//--------------------------------------------------------------------
				if(this.lay==0)
					this.lay=1;
				HashMap existWriteItem=new HashMap();
				LazyDynaBean abean=null;
				LazyDynaBean a_bean=null;
				int columnSize=0;
				 //输出表头
				executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay-1)),projectName,centerstyle);
				this.colIndex+=Short.parseShort(String.valueOf(this.lay));
				if(planParam.get("TaskNameDesc")!=null&&!"".equals((String)planParam.get("TaskNameDesc")))
				{
					executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)planParam.get("TaskNameDesc"),centerstyle);
				}else{
	    			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,taskName,centerstyle);
				}
				this.colIndex++;
				for(int x=0;x<alist.size();x++)
    			{
	    			LazyDynaBean labean=(LazyDynaBean)alist.get(x);
	    			String itemid=(String)labean.get("itemid");
	    			String itemdesc=(String)labean.get("itemdesc");
					
					String cloun="/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/PLAN_ID/STATE/";
					            ///NBASE/A0100/A0101/B0110/E0122/E01A1/P0401/P0425/P0427/P0407/P0413/P0415/P0421/P0423/PLAN_ID/STATE/
					if(cloun.indexOf("/"+itemid.toUpperCase()+"/")!=-1)
						continue;
//					if(item.getUseflag().equals("0"))
//						continue;
					if(cardField.length()>0&&cardField.indexOf(","+itemid.toUpperCase()+",")==-1)
						continue;
					if(!"1".equals(template_vo.getString("status"))&&("P0415".equalsIgnoreCase(itemid)|| "p0423".equalsIgnoreCase(itemid)))
					{
						continue;
					}
					executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,itemdesc,centerstyle);
					this.colIndex++;
				}
	     		columnSize=this.colIndex+lay;
				
				int rowNum=0;
				for(int i=0;i<this.leafItemList.size();i++)
				{
					abean=(LazyDynaBean)this.leafItemList.get(i);
					String item_id=(String)abean.get("item_id");
				    	if(i==0)
					    	this.rowNum++;
						this.colIndex=0;
						rowNum++;
						ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
						int current=linkParentList.size();
						if(current==1)
						{
							if(existWriteItem.get(item_id)!=null)
				    		{
				    			this.colIndex++;
			    				continue;
			    			}
			    			existWriteItem.put(item_id,"1");
			    			String itemdesc=(String)abean.get("itemdesc");
			    			/**画出一个父项目*/
			    			int colspan=((itemPointNum.get(item_id)==null?0:((Integer)itemPointNum.get(item_id)).intValue())+(childItemLinkMap.get(item_id)==null?0:((Integer)childItemLinkMap.get(item_id)).intValue()));
			    			executeCell(this.rowNum,this.colIndex,colspan==0?this.rowNum:(this.rowNum+colspan-1),this.colIndex,itemdesc,centerstyle);
			    			this.colIndex++;
			    			/**父项目包含指标，画出指标*/
			    			/**该项目的层数*/
			    			int layer=Integer.parseInt((String)layMap.get(item_id));
			    			/**对应指标列表*/
				    		ArrayList fieldlistp = (ArrayList)this.itemToPointMap.get(item_id);
				    		/**该项目有指标*/
				    		if(fieldlistp!=null&&fieldlistp.size()>0)
				    		{
				    			for(int h=0;h<fieldlistp.size();h++)
				    			{
					    			LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
					    			int k=0;
				    				for(int f=0;f<this.lay-layer;f++)
				    				{
				    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
				    					k++;
				    				}
				    				/**指标名称*/
				    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("p0407"),centerstyle);
				    				k++;
				    				for(int x=0;x<alist.size();x++)
				        			{
				    	    			LazyDynaBean labean=(LazyDynaBean)alist.get(x);
				    	    			String itemid=(String)labean.get("itemid");			    	    			
				    					String cloun="/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/PLAN_ID/STATE/";
				    					if(cloun.indexOf("/"+itemid.toUpperCase()+"/")!=-1)
				    						continue;
				    					if(cardField.length()>0&&cardField.indexOf(","+itemid.toUpperCase()+",")==-1)
				    						continue;
				    					if(!"1".equals(template_vo.getString("status"))&&("P0415".equalsIgnoreCase(itemid)|| "p0423".equalsIgnoreCase(itemid)))
				    					{
				    						continue;
				    					}
				    					if("p0419".equalsIgnoreCase(itemid))
				    					{
				    						String value=(String)pointbean.get(itemid);
				    						if(value!=null&&!"".equalsIgnoreCase(value))
				    							value=value+"%";
				    			    		executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);
				    					}else if("p0415".equalsIgnoreCase(itemid)){
				    						String p0400=(String)pointbean.get("p0400");
				    						if(this.hasAddMinusItem&&addMinusPointStr.length()>0)
				    						{
				    							if(this.addMinusPointStr.indexOf("'"+p0400+"'")!=-1)
				    								executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
				    							else
				    							{
				    								executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemid),centerstyle);				    				
				    							}
				    						}
				    						else
				    						{
				    							executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemid),centerstyle);				    				
				    						}
				    						
				    					}else
				    						executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemid),centerstyle);				    				
				    					k++;
				    				}
			    	
			        			    	this.rowNum++;
		    					}
			    			}
				    		/**没有指标**/
			    			else
			    			{
			    				/**画出空格*/
			    				int k=0;
			    				for(int f=0;f<this.lay-layer;f++)
			    				{
			    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
			    				    k++;
			    				}
			    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k)," ",centerstyle);
			    				for(int x=0;x<alist.size();x++)
			        			{
			    	    			LazyDynaBean labean=(LazyDynaBean)alist.get(x);
			    	    			String itemid=(String)labean.get("itemid");
			    					String cloun="/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/PLAN_ID/STATE/";
			    					if(cloun.indexOf("/"+itemid.toUpperCase()+"/")!=-1)
			    						continue;
			    					if(cardField.length()>0&&cardField.indexOf(","+itemid.toUpperCase()+",")==-1)
			    						continue;
			    					if(!"1".equals(template_vo.getString("status")) && ("P0415".equalsIgnoreCase(itemid) || "p0423".equalsIgnoreCase(itemid)))
			    					{
			    						continue;
			    					}
			    					String value="";
			    					if("p0413".equalsIgnoreCase(itemid))
			    						value=(String)abean.get("score");
			    					if("p0415".equalsIgnoreCase(itemid))
			    						value=(String)abean.get("rank");
			    					k++;
			    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);				    				

			    				}
		    					this.rowNum++;
	    					}	
						}
						else
						{
				    		/**叶子项目的所有父项目列表（爷爷，太爷）*/
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
				    			/**画出一个父项目*/
				    			int colspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));	
				        		//this.rowNum++;
				    			executeCell(this.rowNum,this.colIndex,colspan==0?this.rowNum:(this.rowNum+colspan-1),this.colIndex,itemdesc,centerstyle);
				    			this.colIndex++;
				    			/**父项目包含指标，画出指标*/
				    			/**该项目的层数*/
				    			int layer=Integer.parseInt((String)layMap.get(itemid));
				    			/**对应指标列表*/
					    		ArrayList fieldlistp = (ArrayList)this.itemToPointMap.get(itemid);
					    		/**该项目有指标*/
					    		if(fieldlistp!=null&&fieldlistp.size()>0)
					    		{  
					    			for(int h=0;h<fieldlistp.size();h++)
					    			{
						    			LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
						    			int k=0;
						    			
					    				for(int f=0;f<this.lay-layer;f++)
					    				{
					    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
					    					k++;
					    				}
					    				/**指标名称*/
					    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("p0407"),centerstyle);
					    				
					    				for(int x=0;x<alist.size();x++)
					        			{
					    	    			LazyDynaBean labean=(LazyDynaBean)alist.get(x);
					    	    			String itemId=(String)labean.get("itemid");
					    					String cloun="/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/PLAN_ID/STATE/";
					    					if(cloun.indexOf("/"+itemId.toUpperCase()+"/")!=-1)
					    						continue;
					    					if(cardField.length()>0&&cardField.indexOf(","+itemId.toUpperCase()+",")==-1)
					    						continue;
					    					if(!"1".equals(template_vo.getString("status"))&&("P0415".equalsIgnoreCase(itemId)|| "p0423".equalsIgnoreCase(itemId)))
					    					{
					    						continue;
					    					}
					    					k++;
					    					if("p0419".equalsIgnoreCase(itemId))
					    					{
					    						String value=(String)pointbean.get(itemId);
					    						if(value!=null&&!"".equalsIgnoreCase(value))
					    							value=value+"%";
					    			    		executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);
					    					}else
					    						executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get(itemId),centerstyle);				    				

					    				}
				    					
				        			    	this.rowNum++;
			    					}
				    			}
					    		/**没有指标**/
				    			else
				    			{
				    				if(e==0)
				    				{
			    	    				int k=0;
				        				for(int f=0;f<this.lay-layer;f++)
				        				{
				    	     				executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
				    	    			    k++;
				    		    		}
				        				/**指标名称列画空*/
			    			    		executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
			    			    		for(int x=0;x<alist.size();x++)
					        			{
					    	    			LazyDynaBean labean=(LazyDynaBean)alist.get(x);
					    	    			String itemId=(String)labean.get("itemid");
    				    					String cloun="/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/PLAN_ID/STATE/";
					    					if(cloun.indexOf("/"+itemId.toUpperCase()+"/")!=-1)
					    						continue;
					    					if(cardField.length()>0&&cardField.indexOf(","+itemId.toUpperCase()+",")==-1)
					    						continue;
					    					if(!"1".equals(template_vo.getString("status"))&&("P0415".equalsIgnoreCase(itemId)|| "p0423".equalsIgnoreCase(itemId)))
					    					{
					    						continue;
					    					}
					    					String value="";
					    					if("p0413".equalsIgnoreCase(itemId))
					    						value=(String)a_bean.get("score");
					    					if("p0415".equalsIgnoreCase(itemId))
					    						value=(String)a_bean.get("rank");
					    					k++;
					    					if("p0419".equalsIgnoreCase(itemId))
					    					{
					    						if(value!=null&&!"".equalsIgnoreCase(value))
					    							value=value+"%";	
					    					}
					    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),value,centerstyle);

					    				}
			    			    		this.rowNum++;
				    				}
		    					}
		    				}
						}
					/*}*/
				}
			
				for (int i = 0; i <=columnSize; i++)
				{
					this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
				}
				for (int i = 0; i <=this.rowNum; i++)
				{
				    row = sheet.getRow(i);
				    if(row==null)
				    	row = sheet.createRow(i);
				    row.setHeight((short) 400);
				}		
				//----------------------------------------------------------------------------
			}
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + fileName);
			hwb.write(fileOut);
			fileName=fileName.replaceAll(".xls","#");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(hwb);
		}
		return fileName;
	}
	public HashMap getChildItemLinkMap()
	{
		HashMap map = new HashMap();
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			doMethod(bean,list);
			LazyDynaBean aa_bean=null;
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				
			}
			map.put((String)bean.get("item_id"),new Integer(n));
		}
		return map;
	}
	public void doMethod(LazyDynaBean bean,ArrayList list)
	{
		String itemid=(String)bean.get("item_id");
		String childid=(String)bean.get("child_id");
		if(childid.length()==0)
		{
			//list.add(bean);
			return;
		}else
		{
			list.add(bean);
		}
		for(int j=0;j<this.templateItemList.size();j++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parentid=(String)a_bean.get("parent_id");
			if(parentid.equals(itemid))
			{
				doMethod(a_bean,list);
			}
		}
	}
	public void doMethod2()
	{
		for(int i=0;i<parentList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)parentList.get(i);
			String itemid=(String)bean.get("item_id");
			layMap.put(itemid, "1");
			doM(bean,1);		
		}
	}
	public void doM(LazyDynaBean bean,int lay)
	{
		lay++;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)bean.get("item_id");
			String a_itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(parentid.equals(itemid))
			{
				ifHasChildMap.put(itemid, "1");
				layMap.put(a_itemid,lay+"");
				doM(a_bean,lay);
			}
		}
	}
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
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				else
					n+=1;
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
				if(parent_id.equals(item_id))
					getLeafItemList(a_bean,list);
		}
		
	}
	public HashMap getItemToPointMap(String plan_id,String a0100,ContentDAO dao,boolean flag,UserView userView,String object_type)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from p04 where ");
			sql.append(" plan_id="+plan_id +" and ");
//			if(this.getModel()!=null&&this.getModel().equals("3"))
			if(object_type!=null && "2".equals(object_type))
				sql.append(" a0100='"+a0100+"'");
			else
		    	sql.append(" b0110='"+a0100+"' ");
			if(flag)
			{
				if(userView.getUnitIdByBusi("5")!=null&&userView.getUnitIdByBusi("5").length()>0&&!"UN".equalsIgnoreCase(userView.getUnitIdByBusi("5")))//&&(this.plan_vo.getInt("status")!=4&&this.plan_vo.getInt("status")!=6&&this.perObject_vo.getString("sp_flag")!=null&&!this.perObject_vo.getString("sp_flag").equals("")&&!this.perObject_vo.getString("sp_flag").equals("01"))
				{
					String temp=userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
					String[] arr=temp.split("`");
					StringBuffer t_buf = new StringBuffer();
					for(int i=0;i<arr.length;i++)
					{
						if(arr[i]==null|| "".equals(arr[i]))
							continue;
						t_buf.append(" or score_org like '"+arr[i].substring(2)+"%'");
					}
					t_buf.append(" or score_org is null or score_org =''");
					sql.append(" and ("+t_buf.toString().substring(3)+")");
				}else
				{
			    	sql.append(" and (UPPER(score_org)='"+userView.getUserOrgId()+"' or UPPER(score_org)='"+userView.getUserDeptId()+"'");
			    	sql.append(" or score_org is null or score_org ='')");
				}
			}
			sql.append(" and (chg_type!=3 or chg_type is null)  order by item_id,seq");
			RowSet rowSet= dao.search(sql.toString());
			LazyDynaBean abean=null;
			ArrayList fieldList=DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("p0400",rowSet.getString("p0400"));
				abean.set("b0110",rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"");
				abean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
				abean.set("e01a1",rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"");
				abean.set("nbase",rowSet.getString("nbase")!=null?rowSet.getString("nbase"):"");
				abean.set("a0100",rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"");
				abean.set("a0101",rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
				abean.set("p0401",rowSet.getString("p0401"));
				abean.set("p0407",Sql_switcher.readMemo(rowSet,"p0407"));
				/**分值*/
				abean.set("p0413",moveZero(rowSet.getString("p0413")));
				/**权重*/
				abean.set("p0415",rowSet.getString("p0415")!=null?getXS((Double.parseDouble(moveZero(rowSet.getString("p0415")))*100)+"",2)+"%":"");
		        /**调整后标准分值*/
				abean.set("p0421",rowSet.getString("p0421")!=null?moveZero(rowSet.getString("p0421")):"");
				 /**调整后权重*/
				abean.set("p0423",rowSet.getString("p0423")!=null?getXS((Double.parseDouble(moveZero(rowSet.getString("p0423")))*100)+"",2)+"%":"");
				  /**任务变更说明*/
				abean.set("p0425",Sql_switcher.readMemo(rowSet,"p0425"));
				abean.set("chg_type",rowSet.getString("chg_type")!=null?rowSet.getString("chg_type"):"0");
				
				abean.set("plan_id",rowSet.getString("plan_id"));
				abean.set("item_id",rowSet.getString("item_id"));
				abean.set("fromflag",rowSet.getString("fromflag"));
				abean.set("state",rowSet.getString("state")!=null?rowSet.getString("state"):"0");
				abean.set("seq",rowSet.getString("seq")!=null?rowSet.getString("seq"):"0");
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					String itemid=item.getItemid();
					String str="/P0421/P0423/P_P0400/F_P0400/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/P0413/P0415/PLAN_ID/STATE/";
					if(str.indexOf("/"+itemid.toUpperCase()+"/")!=-1)
						continue;
					if("0".equals(item.getState()))
						continue;
					if("D".equalsIgnoreCase(item.getItemtype()))
					{
							if(rowSet.getDate(itemid)!=null)
								abean.set(itemid,fm.format(rowSet.getDate(itemid)));
							else
								abean.set(itemid,"");
					}
					else if("M".equalsIgnoreCase(item.getItemtype()))
							abean.set(itemid,Sql_switcher.readMemo(rowSet,itemid));
					else
					{
						if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())||item.getCodesetid().trim().length()==0)
						{
							if("p0424".equalsIgnoreCase(itemid))
							{
								String astr=rowSet.getString(itemid);
								if(astr==null|| "".equals(astr.trim()))
									abean.set(itemid,"");
								else
								{
									abean.set(itemid,astr.split("/")[1]);
								}
							}
							else
				    			abean.set(itemid,rowSet.getString(itemid)!=null?rowSet.getString(itemid):"");
						}
						else
						{
							if(rowSet.getString(itemid)==null)
								abean.set(itemid,"");
							else
							{
								if("score_org".equalsIgnoreCase(itemid))
								{
									String value=AdminCode.getCodeName("UM", rowSet.getString(itemid));
									if(value==null|| "".equals(value))
										value=AdminCode.getCodeName("UN", rowSet.getString(itemid));
									abean.set(itemid,value);
								}else{
					    			abean.set(itemid,AdminCode.getCodeName(item.getCodesetid(), rowSet.getString(itemid)));
								}
								
							}
						}
					}
				}
				if(map.get(rowSet.getString("item_id"))==null)
				{
					ArrayList list = new ArrayList();
					list.add(abean);
					map.put(rowSet.getString("item_id"), list);
				}
				else
				{
					ArrayList list=(ArrayList)map.get(rowSet.getString("item_id"));
					list.add(abean);
					map.put(rowSet.getString("item_id"), list);
				}
				p04List.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getXS(String str,int scale){
    	if(str==null|| "null".equalsIgnoreCase(str)|| "".equals(str))
    		str="0.00";
    	BigDecimal m=new BigDecimal(str);
    	BigDecimal one = new BigDecimal("1");
    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }
 public String moveZero(String number)
 {
	DecimalFormat df = new DecimalFormat("###############.#####"); 
	if(number==null||number.length()==0)
		return "0.00";
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
				if(parent_id.equals(item_id))
					setLeafItemFunc(a_bean);
		}
	}
	/**
	 * 模板的所有项目
	 * @param templateID
	 * @return
	 */
	public ArrayList getTemplateItemList(String templateID,int status)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select * from  per_template_item where template_id='"+templateID+"' ";

				//目标制定不显示共性指标 liantong
			if(SystemConfig.getPropertyValue("noShowCommonPonit")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noShowCommonPonit")))
			{
				if(status==8)
					sql+=" and kind<>1 ";
				
			}
			sql+=" order by seq ";
			RowSet rowSet=dao.search(sql);
		    LazyDynaBean abean=null;
			DecimalFormat myformat1 = new DecimalFormat("########.##");//
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
		    	abean.set("score",myformat1.format(Double.parseDouble(rowSet.getString("score")==null|| "".equals(rowSet.getString("score"))?"0":rowSet.getString("score"))));
		    	abean.set("rank",Double.parseDouble(myformat1.format(Double.parseDouble(rowSet.getString("rank")==null|| "".equals(rowSet.getString("rank"))?"0":rowSet.getString("rank"))))*100+"%");
		    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
		    	list.add(abean);
		    	if(rowSet.getString("parent_id")==null|| "".equals(rowSet.getString("parent_id")))
		    	{
		    		this.parentList.add(abean);
		    	}
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
				if(linkList.size()>lay)
					lay=linkList.size();
				map.put(item_id,linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
//	寻找继承关系
	public void getParentItem(ArrayList list,LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String parent_id=(String)abean.get("parent_id");
		/**顶级节点*/
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
	public void executeCell(int a, short b, int c, short d, String content,
			HSSFCellStyle aStyle) {
		try {
			HSSFRow row = sheet.getRow(a);
			if(row==null)
				row = sheet.createRow(a);
			
			HSSFCell cell = row.getCell(b);
			if(cell==null)
				cell = row.createCell(b);
			
			String macth="[0-9]+(.[0-9]+)?";
			if(content!=null&&!"".equals(content)&&content.matches(macth))
			{
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(Double.parseDouble(content));
			}
			else
				cell.setCellValue(new HSSFRichTextString(content));
			cell.setCellStyle(aStyle);
			short b1 = b;
			while (++b1 <= d) {			
				cell = row.getCell(b1);
				if(cell==null)
					cell = row.createCell(b1);
				
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++) {			
				row = sheet.getRow(a1);
				if(row==null)
					row = sheet.createRow(a1);
				
				b1 = b;
				while (b1 <= d) {				
					cell = row.getCell(b1);
					if(cell==null)
						cell = row.createCell(b1);
					
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	public void executeCellByType(int a, short b, int c, short d, String content,HSSFCellStyle aStyle,String itemType) {
		try {
			HSSFRow row = sheet.getRow(a);
			if(row==null)
				row = sheet.createRow(a);
			
			HSSFCell cell = row.getCell(b);
			if(cell==null)
				cell = row.createCell(b);
			
			content=content.replaceAll("<br>","\r\n");
			String macth="[0-9]+(.[0-9]+)?";
			if(content!=null&&!"".equals(content)&& "N".equalsIgnoreCase(itemType)&&content.matches(macth))
			{
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(Double.parseDouble(content));
			}
			else
				cell.setCellValue(new HSSFRichTextString(content));
			cell.setCellStyle(aStyle);
			short b1 = b;
			while (++b1 <= d) {			
				cell = row.getCell(b1);
				if(cell==null)
					cell = row.createCell(b1);
				
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++) {			
				row = sheet.getRow(a1);
				if(row==null)
					row = sheet.createRow(a1);
				
				b1 = b;
				while (b1 <= d) {				
					cell = row.getCell(b1);
					if(cell==null)
						cell = row.createCell(b1);
					
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置excel的样式
	 * @param workbook
	 * @param styles
	 * @return
	 */
		public HSSFCellStyle style(HSSFWorkbook workbook, int styles)
		{
			
			HSSFCellStyle style = workbook.createCellStyle();
			
			switch (styles)
			{
			
			case 0:
			    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 12);
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
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			    break;
			case 4:
			    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			    style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			    break;
			case 6:
			    HSSFFont fonttitleb = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 12);
			    fonttitleb.setBold(true);// 加粗
			    style.setFont(fonttitleb);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.CENTER);
			    break;
			case 98:
				HSSFFont afont=fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10);
				afont.setColor(HSSFFont.COLOR_RED);
				 style.setFont(afont);
				 style.setAlignment(HorizontalAlignment.LEFT);
				 style.setBorderBottom(BorderStyle.THIN);
				 style.setBorderLeft(BorderStyle.THIN);
				 style.setBorderRight(BorderStyle.THIN);
				 style.setBorderTop(BorderStyle.THIN);
				 style.setVerticalAlignment(VerticalAlignment.CENTER);
				 break;
			case 99:
				 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
				 style.setAlignment(HorizontalAlignment.LEFT);
				 style.setBorderBottom(BorderStyle.THIN);
				 style.setBorderLeft(BorderStyle.THIN);
				 style.setBorderRight(BorderStyle.THIN);
				 style.setBorderTop(BorderStyle.THIN);
				 style.setVerticalAlignment(VerticalAlignment.CENTER);
				 break;
			case 88:
				 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
				 style.setAlignment(HorizontalAlignment.RIGHT);
				 style.setBorderBottom(BorderStyle.THIN);
				 style.setBorderLeft(BorderStyle.THIN);
				 style.setBorderRight(BorderStyle.THIN);
				 style.setBorderTop(BorderStyle.THIN);
				 style.setVerticalAlignment(VerticalAlignment.CENTER);
				 break;
			case 89:
				 HSSFFont bfont=fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10);
				 bfont.setColor(HSSFFont.COLOR_RED);
				 style.setFont(bfont);
				 style.setAlignment(HorizontalAlignment.RIGHT);
				 style.setBorderBottom(BorderStyle.THIN);
				 style.setBorderLeft(BorderStyle.THIN);
				 style.setBorderRight(BorderStyle.THIN);
				 style.setBorderTop(BorderStyle.THIN);
				 style.setVerticalAlignment(VerticalAlignment.CENTER);
				 break;
			default:
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    break;
			}
			style.setWrapText(true);
			return style;
		}
		/**
		 * 设置excel的字体
		 * @param workbook
		 * @param fonts
		 * @param size
		 * @return
		 */
		public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size)
		{
		
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) size);
			font.setFontName(fonts);
			return font;
		}
	public String getperBodyId(String plan_id,ContentDAO dao,String a0100)
	{
		String str = "0";
		RowSet rs=null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select mainbody_id from per_mainbody where ");
			sql.append("object_id='"+a0100+"' and plan_id="+plan_id+" and mainbody_id='"+a0100+"'");			
			rs = dao.search(sql.toString());
			boolean ismainbody = false;
			while(rs.next())
			{
				ismainbody = true;
				break;
			}
			
			StringBuffer strSql = new StringBuffer();
			strSql.append("select body_id from per_plan_body where plan_id="+plan_id+" and (body_id = '5' or body_id = '-1') and isgrade=1 ");
			rs=dao.search(strSql.toString());
			boolean isScoreOrNo = true;
			while(rs.next())
			{
				isScoreOrNo = false;				
			}
			if(ismainbody && isScoreOrNo)
				str = "1";
			
			if(rs!=null)			
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public int getp04(String objectid,String planid,ContentDAO dao)
	{
		int i=0;
		try
		{
			String sql="select count(*) total from p04 where plan_id="+planid+"  and a0100='"+objectid+"'  and state=-1";
		    RowSet rs =null;
		    rs=dao.search(sql);
		    while(rs.next())
		    {
		    	i=rs.getInt("total");
		    }
		   rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	public String getPermainbody(String objectid,String planid,ContentDAO dao)
	{
		String str=null;
		try
		{
			String sql = "select status from per_mainbody where object_id='"+objectid+"' and plan_id="+planid;
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				str=rs.getString("status");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**本人是否打分*/
	public String getMainBodyStatus(String a0100,int plan_id)
	{
		String str="";
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select status from per_mainbody p where ");
			buf.append(" p.plan_id="+plan_id);
			//buf.append(" and p.mainbody_id='"+a0100+"'");
	        buf.append(" and p.object_id='"+a0100+"' and p.mainbody_id='"+a0100+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
			{
				str=rs.getString("status")==null?"":rs.getString("status");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**是否有人打分*/
	public boolean isMarket(String a0100,int plan_id)
	{
		boolean flag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select status from per_mainbody p where ");
			buf.append(" p.plan_id="+plan_id);
	        buf.append(" and p.object_id='"+a0100+"' and (p.status='1' or p.status='2')");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
			{
				flag=true;
				break;
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public HashMap hasPersonMarkMap(String a0100)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select plan_id,object_id from per_mainbody p where ");
			buf.append("  p.object_id='"+a0100+"' and (status=2 or status=1 )");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
			{
				map.put(rs.getString("plan_id")+rs.getString("object_Id"), "1");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**取得要导出人员的名字*/
	public void getNameMap(String records)
	{
		try
		{
			StringBuffer a0100=new StringBuffer("");
			StringBuffer planid=new StringBuffer("");
			String[] arr=records.split("/");
			for(int u=0;u<arr.length;u++)
			{
				String[] str=arr[u].split("-");
				a0100.append(",'"+str[0]+"'");
				planid.append(","+str[1]);
			}
			if(a0100.toString().length()>0)
			{
				StringBuffer sql = new StringBuffer("");
				/*sql.append("select a.a0101,a.a0100,a.plan_id,b.name,b.cycle,b.theyear,b.themonth,b.thequarter from (select distinct a0101,a0100,plan_id from p04 where p04.a0100 in ("+a0100.substring(1)+")) a , per_plan b where a.plan_id=b.plan_id ");
				sql.append("  and a.plan_id in ("+planid.toString().substring(1)+") order by a0100");*/
				sql.append(" select a.a0101,a.object_id,b.plan_id,b.name,b.cycle,b.theyear,b.themonth,b.thequarter from per_object a,per_plan b where a.plan_id=b.plan_id");
				String strsql= " and (";
				String[] objectids = a0100.toString().split(",");
				String[] planids = planid.toString().split(",");
				for(int k=1;k<objectids.length;k++){
					if(k==objectids.length-1)
						strsql += "(a.object_id="+objectids[k]+" and a.plan_id="+planids[k]+" )";
					else
						strsql += " (a.object_id="+objectids[k]+" and a.plan_id="+planids[k]+" ) or ";
				}
				strsql += ") order by object_id ";
				sql.append(strsql);
				//sql.append(" and a.object_id in ("+a0100.substring(1)+") and a.plan_id in ("+planid.toString().substring(1)+")");
				//sql.append(" and b.plan_id in ("+planid.toString().substring(1)+") order by object_id");
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs= dao.search(sql.toString());
				int i=0;
				String aa="";
				HashMap exsitMap=new HashMap();
				while(rs.next())
				{
					i++;
					//考核周期:(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
					String cycle=rs.getString("cycle");
					String name="";
				    if("3".equals(cycle))//按月度
				    {
				    	name=rs.getString("theyear")+rs.getString("themonth")+"-";
				    }
				    else if("0".equals(cycle))
				    {
				    	name=rs.getString("theyear")+"-";
				    }
				    else if("1".equals(cycle))
				    {
				    	if("01".equals(rs.getString("thequarter"))|| "1".equals(rs.getString("thequarter")))
				        	name=rs.getString("theyear")+"上-";
				    	else
				    		name=rs.getString("theyear")+"下-";
				    }
				    else if("2".equals(cycle))
				    {
				    	if("01".equals(rs.getString("thequarter"))|| "1".equals(rs.getString("thequarter")))
				        	name=rs.getString("theyear")+"一-";
				    	else if("02".equals(rs.getString("thequarter"))|| "2".equals(rs.getString("thequarter")))
				        	name=rs.getString("theyear")+"二-";
				    	else if("03".equals(rs.getString("thequarter"))|| "3".equals(rs.getString("thequarter")))
				        	name=rs.getString("theyear")+"三-";
				    	else if("04".equals(rs.getString("thequarter"))|| "4".equals(rs.getString("thequarter")))
				        	name=rs.getString("theyear")+"四-";
				    }
				    else{
				    	name="不定期-";
				    }
				    
					String sheetName=rs.getString("a0101");
					String temp = name+sheetName;
					if(exsitMap.get(temp)==null)
					{
			    		nameMap.put(rs.getString("object_id")+rs.getString("plan_id"),name+sheetName);
			    		exsitMap.put(name+sheetName, new Integer(1)); 
					}else
					{
						int count=((Integer)exsitMap.get(name+sheetName)).intValue();
						nameMap.put(rs.getString("object_id")+rs.getString("plan_id"),name+sheetName+count);
						count=count+1;
						exsitMap.put(name+sheetName, new Integer(count)); 
					}
				}
				rs.close();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private ArrayList yearlist = new ArrayList();
	
	public ArrayList getYearList()
	{
		return this.yearlist;
	}
	public ArrayList getOrgPlanList(String a0100,String year,String quarter,String month,String status,String sp_flagSQL)
	{
	    ArrayList list = new ArrayList();
	    	try
	    	{
	    		yearlist.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	    		StringBuffer buf = new StringBuffer();
	    		buf.append(" select T.*,pm.seq,pm.reasons,pm.status as scorestatus,pm.body_id as body_id from (");//取出主体id，用于判断对象是否是本人这个主体（空就是没有）   zhaoxg add 2014-12-1
	    		buf.append("select pp.plan_id,pp.theyear,pp.name,pp.thequarter,pp.themonth,pp.status,pp.b0110,pp.parameter_content,pp.cycle,"+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,");
	    		buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
	    		buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,"+Sql_switcher.day("pp.end_date")+" as ed");
	    		buf.append(" ,po.sp_flag,po.trace_sp_flag,po.kh_relations,po.object_id,ppb.opt AS planOpt from per_plan pp ");
	    		// 添加本人(主体)的打分确认标识(planOpt:1=确认, 0|null=打分) modify by lium
	    		buf.append(" LEFT JOIN per_plan_body ppb ON pp.plan_id=ppb.plan_id AND ppb.body_id=5,per_object po ");
	    		buf.append("  where  pp.plan_id=po.plan_id  ");
	    		buf.append(" and  pp.object_type='2'  and po.object_id='"+a0100+"'");
	    		if(sp_flagSQL!=null&&!"".equals(sp_flagSQL))
	    		{
	    			buf.append(" and ("+sp_flagSQL+")");
	    		}
	    		buf.append(" and (pp.status='4' or pp.status='6' or pp.status='7' or pp.status='5' or pp.status='8')");
	    		/**pp.cycle<>'7'兼容不定期考核的计划*/
	    		buf.append(" and pp.method='2') T left join per_mainbody pm on ");
	    		buf.append(" T.plan_id=pm.plan_id and T.object_id=pm.object_id and pm.mainbody_id='"+a0100+"'");
	    		buf.append(" order by T.norder asc,T.plan_id desc");
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs = null;
	    		rs = dao.search(buf.toString());
	    		HashMap map = this.hasPersonMarkMap(a0100);
	    		AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.conn);
	    		Hashtable ht_table=bo.analyseParameterXml();
	    		String AllowLeaderTrace="false";
	    		//trace_sp_flag
	    		//if(ht_table!=null)
	    			//AllowLeaderTrace=(String)ht_table.get("AllowLeaderTrace");
	    		boolean priv=false;
				FieldItem item  = DataDictionary.getFieldItem("score_org");
				if(item!=null&& "1".equals(item.getState()))
				{
					priv=true;
				}
				RenderRelationBo rrbo = new RenderRelationBo(conn,userView);
				HashMap fieldMap = new HashMap();
				HashMap yearmap = new HashMap();
				HashMap eqMap=new HashMap();
				Des des=new Des();
               // des.EncryPwdStr(value);  加密
               // des.DecryPwdStr(value)  解密
	    		while(rs.next())
	    		{
	    			int plan_id = rs.getInt("plan_id");
	    			
	    		    String cycle=rs.getString("cycle"); 
	    		    String sp_flag=rs.getString("sp_flag");
	    		    String kh_relations=rs.getString("kh_relations");
	    		    if(kh_relations==null)
	    		    	kh_relations="0";
	    		    if(sp_flag==null|| "".equals(sp_flag))
	    		    {
	    		    	sp_flag="01";
	    		    }
	    		    String reasons=Sql_switcher.readMemo(rs, "reasons");
	    		    String scoreStatus=rs.getString("scorestatus");
	    		    String isScoreUntread="0";//评分被驳回
					if("1".equals(scoreStatus)&&reasons.length()>0)
					{
						isScoreUntread="1";
					}
	    			String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",sp_flag);
	    			if("07".equals(sp_flag))
	    				spFlagDesc+="/意见";
	    		    if("3".equals(cycle))//按月度
	    		    {
	    		    	if((!"-1".equals(year)&&rs.getString("theyear").equals(year))|| "-1".equals(year))
	    		    	{
	    		    		int qu=0;
	    		    		if(!"-1".equals(quarter))
	    		    		{
	    		    			qu=this.getFirstMonthInQuarter(quarter);
	    		    		}
	    		    		String a_month=rs.getString("themonth");
	    		    		int int_month=Integer.parseInt(a_month);
	    		    		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
	    		    		{
	    		    			if("-1".equals(month)||Integer.parseInt(month)==Integer.parseInt(rs.getString("themonth")))
	    		    			{
	    		    				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    		    	    			{
	    		    					
    		    	    			}else{
    		    	    				continue;
    		    	    			}
	    		    			}else{
		    	    				continue;
		    	    			}
	    		    		}else{
	    	    				continue;
	    	    			}
	    		    	}else{
    	    				continue;
    	    			}
	        		}else if("0".equals(cycle))//年度
	        		{
	        			if("-1".equals(year)||rs.getString("theyear").equals(year))
	        			{
	        				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    			{

	    	    			}else{
	    	    				continue;
	    	    			}
	        			}else{
    	    				continue;
    	    			}
	        		}
	        		else if("1".equals(cycle))//半年度
	        		{
	        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
	        			{
	        				String a_quarter = rs.getString("thequarter");
	        				if("1".equals(a_quarter))//上半年，算1，2季度
	        				{
	        					if("-1".equals(quarter)||Integer.parseInt(quarter)==1||Integer.parseInt(quarter)==2)
	        					{
	        						if("-1".equals(month)||(1<=Integer.parseInt(month)&&Integer.parseInt(month)<=6))
	        						{
	        							if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    		    	    			{
	      
	    		    	    			}else{
	    		    	    				continue;
	    		    	    			}
	        						}else{
    		    	    				continue;
    		    	    			}
	        					}else{
		    	    				continue;
		    	    			}
	        				}
	        				else//下半年算3，4季度
	        				{
	        					if("-1".equals(quarter)||Integer.parseInt(quarter)==3||Integer.parseInt(quarter)==4)
	        					{
	        						if("-1".equals(month)||(7<=Integer.parseInt(month)&&Integer.parseInt(month)<=12))
	        						{
	        							if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    		    	    			{
	      
	    		    	    			}else{
	    		    	    				continue;
	    		    	    			}
	        						}else{
    		    	    				continue;
    		    	    			}
	        					}else{
		    	    				continue;
		    	    			}
	        				}
	        			}
	        		}
	        		else if("7".equals(cycle))//不定期
	        		{
	        			String sy=rs.getString("sy");
	        			String sm=rs.getString("sm");
	        			String ey=rs.getString("ey");
	        			String em=rs.getString("em");
	        			int int_year = 0;
	        			int int_sy=0;
	        			int int_sm=0;
	        			int int_ey=0;
	        			int int_em=0;
	        		    int_year=Integer.parseInt(year);
	        		    int_sy=Integer.parseInt(sy);
	        		    int_sm=Integer.parseInt(sm);
	        		    int_ey=Integer.parseInt(ey);
	        		    int_em=Integer.parseInt(em);
	        			if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
	        			{
	        				if("-1".equals(quarter)||(Integer.parseInt(quarter)==1&&((1<=int_sm&&int_sm<=3)||(1<=int_em&&int_em<=3)))
	        						||(Integer.parseInt(quarter)==2&&((4<=int_sm&&int_sm<=6)||(4<=int_em&&int_em<=6)))
	        						||(Integer.parseInt(quarter)==3&&((7<=int_sm&&int_sm<=9)||(7<=int_em&&int_em<=9)))
	        						||(Integer.parseInt(quarter)==4&&((10<=int_sm&&int_sm<=12)||(10<=int_em&&int_em<=12))))
        				{
	        			    	int int_month=0;
	        		    		if(!"-1".equals(month))
	        	    			{
	        		    			int_month=Integer.parseInt(month);
	        		    		}
	        		    		if("-1".equals(month)||(!"-1".equals(month)&&(int_sm<=int_month&&int_em>=int_month)))
	        		    		{
	        		    			if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    		    	    			{
	        	
    		    	    			}else{
    		    	    				continue;
    		    	    			}
	        	    			}else{
		    	    				continue;
		    	    			}
	        				}else{
	    	    				continue;
	    	    			}
	        			
	        			}else{
    	    				continue;
    	    			}
	        		}
	        		else if("2".equals(cycle))//季度
	        		{
	        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
	        			{
	        				if("-1".equals(quarter)||quarter.equals(rs.getString("thequarter")))
	        				{
	        					if("-1".equals(month)||(("1".equals(quarter)|| "01".equals(quarter))&&("1".equals(month)|| "2".equals(month)|| "3".equals(month)))
	        							||(("2".equals(quarter)|| "02".equals(quarter))&&("4".equals(month)|| "5".equals(month)|| "6".equals(month)))
	        							||(("3".equals(quarter)|| "03".equals(quarter))&&("7".equals(month)|| "8".equals(month)|| "9".equals(month)))
	        							||(("4".equals(quarter)|| "04".equals(quarter))&&("10".equals(month)|| "11".equals(month)|| "12".equals(month))))
	        					{
	        						if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    		    	    			{
	    
    		    	    			}else{
    		    	    				continue;
    		    	    			}
	        					}else{
		    	    				continue;
		    	    			}
	        				}else{
	    	    				continue;
	    	    			}
	        			}else{
    	    				continue;
    	    			}
	        		}
	    		    /**本人打分状态是否提交=2提交*/
	    			String pmstatus=this.getMainBodyStatus(a0100, plan_id);
	    			/**是否有人给该对象打过分*/
	    			boolean isMarket=this.isMarket(a0100, plan_id);
	    		    String gradedesc=this.getDegreeDesc(plan_id, a0100);
	    		    /**计划的考核主体是否包含本人=1包含*/
	    		    String str=this.getperBodyId(plan_id+"", dao,a0100);
	    		    int tzCount= this.getp04(a0100, rs.getString("plan_id"), dao);
 	         		LazyDynaBean bean = new LazyDynaBean();
                    bean.set("gradedesc",gradedesc);
                    bean.set("planid",rs.getString("plan_id"));
                    bean.set("name", rs.getString("name"));
                    bean.set("a0100",a0100);
                    bean.set("mda0100",PubFunc.encryption(a0100));
                    bean.set("mdplanid", PubFunc.encryption(rs.getString("plan_id")));
                    //ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt);
                    bean.set("record",PubFunc.encrypt(a0100)+"-"+PubFunc.encrypt(rs.getString("plan_id")));
			     	String a_status = rs.getString("status");
			     	String body_id = rs.getString("body_id")==null?"":rs.getString("body_id");
				/* 考核对象状态：01 起草 02 已报批 03 已批04 已发布05 执行中06 结束 07 驳回 08 报审09 暂停 */
				/* 考核主体对考核对象的打分状态：0:未打分 1:正在编辑 2:已提交 3:不打分 */
				// 计划状态(0, 1, 2 ,3, 4, 5, 6, 7)=
				// (起草[可以编辑和删除], 报批, 已批,已发布,
				// 启动[不能编辑和删除],//暂停[不能编辑和删除]，评估[不能编辑,可以删除 ]，结束)
				/*
				 * 判断规则：要根据考核计划，对象，主体来判断， 考核对象状态为非已批（就起草）为制定，
				 * 打分：当考核对象状态为已批，考核计划为非结束，考核主体状态对对象的打分状态为非提交，可以打分， 否则为查看
				 */
			    	String bs = "0";
				/**
				 * 页面显示status含义 =0：查看 =1：自评 =2：制定目标 ！=4并且bs=1：可以调整
				 */
				// String bstatus=this.getPermainbody(a0100, plan_id+"", dao);
				/** **************************************************************** */
			    	LoadXml parameter_content = null;
			    	if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id + "") == null) {

			    		parameter_content = new LoadXml(this.conn, plan_id + "");
			    		BatchGradeBo.getPlanLoadXmlMap().put(plan_id + "",parameter_content);
		    		} else {
		    			parameter_content = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id + "");
		    		}
			    	Hashtable params = parameter_content.getDegreeWhole();
			    	String GradeByBodySeq="false";//按考核主体顺序号控制评分流程(True, False默认为False)
	  				if(params.get("GradeByBodySeq")!=null)
	  					GradeByBodySeq=(String)params.get("GradeByBodySeq");
	  				boolean isCanScore=true;
	  				if("true".equalsIgnoreCase(GradeByBodySeq))//按顺序来评分
	  				{
	  					String seq=rs.getString("seq")==null?"0":rs.getString("seq");
	  					HashMap amap=null;
	  					if(eqMap.get(plan_id+"")!=null)
	  					{
	  						amap=(HashMap)eqMap.get(plan_id+"");
	  					}else{
	  						ObjectiveEvaluateBo oeb=new ObjectiveEvaluateBo(this.conn);
	  						amap=oeb.getObjectEvalInfo(plan_id+"",a0100);
	  						eqMap.put(plan_id+"", amap);
	  					}
	  					if("0".equals(seq))
	  						isCanScore=true;
	  					else if(amap!=null&&amap.get(a0100)!=null)
	  					{
	  						String currseq=(String)amap.get(a0100);
	  						if(seq.equals(currseq))
	  							isCanScore=true;
	  						else
	  							isCanScore=false;
	  					}
	  				}
			     	String NoApproveTargetCanScore = "";
			    	if (params.get("NoApproveTargetCanScore") != null)
			    		NoApproveTargetCanScore = (String) params.get("NoApproveTargetCanScore");
		     		if (params.get("AllowLeaderTrace") != null)
			    		AllowLeaderTrace = (String) params.get("AllowLeaderTrace");
			    	if (ht_table != null&& ht_table.get("AllowLeaderTrace") != null&& "false".equalsIgnoreCase(AllowLeaderTrace))
				    	AllowLeaderTrace = (String) ht_table.get("AllowLeaderTrace");
			     	String taskAdjustNeedNew = (String) params.get("taskAdjustNeedNew");
			    	/** 已批后是否可调整 */
			    	String TargetAllowAdjustAfterApprove = (String) params.get("TargetAllowAdjustAfterApprove");
			    	if (TargetAllowAdjustAfterApprove == null|| "".equals(TargetAllowAdjustAfterApprove))
			    		TargetAllowAdjustAfterApprove = "true";
		     		if (taskAdjustNeedNew == null)
			    		taskAdjustNeedNew = "false";
		     		 String TargetDefineItem="";
		    		if(ht_table!=null)
		    		{
		    		     if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
		    			     TargetDefineItem=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
		    		}
		    		if(params.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)params.get("TargetTraceEnabled")))
		    	    {
		    			if(params.get("TargetDefineItem")!=null&&((String)params.get("TargetDefineItem")).trim().length()>0)
		    					TargetDefineItem=(","+((String)params.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
		    		}
		    		if(priv&&TargetDefineItem!=null&&TargetDefineItem.trim().length()>0&&TargetDefineItem.toUpperCase().indexOf("SCORE_ORG")!=-1)
					{
						if(rrbo.isByOrg2(a0100, a0100, "", Integer.parseInt(a_status)))
						{
							HashMap amap = null;
		         			if(fieldMap.get(plan_id+"")!=null)
		         			{
		         				amap=(HashMap)fieldMap.get(plan_id+"");
		         			}
		         			else
		         			{
		         				amap=rrbo.getKhOrgField(plan_id+"", userView);
		         				fieldMap.put(plan_id+"", amap);
		         			}
		         			if(amap.get(plan_id+a0100)==null)
		         				continue;
						}
					}
		    		String opt = "0";// =0查看，=1可操作。2打分（自评）
			    	String optstatus = "0";
			    	if(body_id!=null&&body_id.length()>0){//本人不是主体的话，只能查看 zhaoxg add 2017-02-13
			    		if(isMarket&&!"4".equals(a_status)&&!"6".equals(a_status))
			    		{
			    			if (tzCount > 0&& "false".equalsIgnoreCase(taskAdjustNeedNew)) {
			    				spFlagDesc = spFlagDesc + "(调整后)";
			    			}
			    		}
			    		/** 分发和暂停的计划，一样的处理 */
			    		else if ("8".equals(a_status)) {
			    			/*
			    			 * if(kh_relations.equals("1")&&a_status.equals("5")) {
			    			 * opt="0"; optstatus="0"; } else {
			    			 */
			    			if ("true".equalsIgnoreCase(taskAdjustNeedNew)) {
			    				/** 打过分 */
			    				if ("5".equals(a_status) && "2".equals(pmstatus)) {
			    					opt = "0";
			    					optstatus = "0";
			    				} else if ("01".equals(sp_flag)) {
			    					opt = "1";
			    					optstatus = "2";
			    				} else if ("07".equalsIgnoreCase(sp_flag)) {
			    					opt = "1";
			    					optstatus = "2";
			    				} else if ("02".equals(sp_flag) || "03".equals(sp_flag)) {
			    					if ("03".equals(sp_flag)) {
			    						if ("true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&& !isMarket) {
			    							bs = "1";
			    						}
			    					}
			    					opt = "0";
			    					optstatus = "0";
			    				}
			    			} else {
			    				if (tzCount > 0) {
			    					spFlagDesc = spFlagDesc + "(调整后)";
			    					if ("01".equals(sp_flag) || "07".equals(sp_flag)) {
			    						opt = "1";
			    						optstatus = "2";
			    					} else if ("02".equals(sp_flag)) {
			    						opt = "0";
			    						optstatus = "0";
			    					} else if ("03".equals(sp_flag)) {
			    						if ("true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&& !isMarket) {
			    							bs = "1";
			    						}
			    						opt = "0";
			    						optstatus = "0";
			    					}
			    				} else {
			    					if ("01".equals(sp_flag) || "07".equals(sp_flag)) {
			    						opt = "1";
			    						optstatus = "2";
			    					} else if ("02".equals(sp_flag)) {
			    						opt = "0";
			    						optstatus = "0";
			    					} else if ("03".equals(sp_flag)) {
			    						if ("true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&& !isMarket) {
			    							bs = "1";
			    						}
			    						opt = "0";
			    						optstatus = "0";
			    					}
			    				}
			    			}
			    		} else if (("4".equals(a_status) || "6".equals(a_status))&& !"2".equals(pmstatus) && "1".equals(str)) {
			    			if (tzCount > 0&& "false".equalsIgnoreCase(taskAdjustNeedNew)) {
			    				spFlagDesc = spFlagDesc + "(调整后)";
			    			}
			    			if (("03".equals(sp_flag)&&body_id.length()>0|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&isCanScore) {//加了body_id的判断，自评时若没有选本人为考核主体，则只能查看  zhaoxg add 2014-12-1
			    				opt = "2";
			    				optstatus = "1";
			    			} else {
			    				opt = "0";
			    				optstatus = "0";
			    			}
			    		} else if ("5".equals(a_status)) {
			    			opt = "0";
			    			optstatus = "0";
			    			bs = "0";
			    		} else {
			    			if (tzCount > 0&& "false".equalsIgnoreCase(taskAdjustNeedNew)) {
			    				spFlagDesc = spFlagDesc + "(调整后)";
			    			}
			    			opt = "0";
			    			optstatus = "0";
			    		}
			    	}
			    	/** ***************************************************************** */
		    		bean.set("status", optstatus);
		    		bean.set("opt", opt);
			    	bean.set("bs", bs);
			    	if ("HJSJ".equalsIgnoreCase(rs.getString("b0110")))
			    		bean.set("b0110", ResourceFactory.getProperty("org.performance.publicresource"));
		    		else {
		    			String b0110 = AdminCode.getCodeName("UN", rs.getString("b0110"));
				    	if (b0110 == null || "".equals(b0110))
			   		    	b0110 = AdminCode.getCodeName("UM", rs.getString("b0110"));
			    		bean.set("b0110", b0110);
	       			}
	    			bean.set("sp_flag", sp_flag);
	    			String traceFlagDesc = "";
		    		String trace_flag = "";
		    		if ("03".equals(sp_flag)) {
		     			if ("true".equalsIgnoreCase(AllowLeaderTrace)) {//不论是否允许领导调整跟踪指标||AllowLeaderTrace.equalsIgnoreCase("false")
				    		trace_flag = rs.getString("trace_sp_flag");
					    	if (trace_flag == null)
					    		trace_flag = "01";
				     		if ("07".equals(trace_flag))
				    			traceFlagDesc = "退回";
				    		else
					    		traceFlagDesc = AdminCode.getCodeName("23",trace_flag);
			     			traceFlagDesc = "跟踪指标" + traceFlagDesc;
			    		} else {
			     			traceFlagDesc = spFlagDesc;
			    			trace_flag = sp_flag;
	  	    			}
	    			}
		    		bean.set("trace_flag", trace_flag);
	     			bean.set("tsp", traceFlagDesc);
                    bean.set("sp",spFlagDesc);
                    bean.set("isScoreUntread", isScoreUntread);
                    /*if(cycle.equals("7"))
        	    	{
        	    		bean.set("evaluate",rs.getString("sy")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("sm")+ResourceFactory.getProperty("datestyle.month")+rs.getString("sd")+ResourceFactory.getProperty("datestyle.day")+"  至  "+rs.getString("ey")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("em")+ResourceFactory.getProperty("datestyle.month")+rs.getString("ed")+ResourceFactory.getProperty("datestyle.day"));
        	    	}
        	    	else{
                       bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
        	    	}*/
                    if("3".equals(cycle))
        	    	{
        	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
        	    	}
        	    	else if("0".equals(cycle))
        	    	{
        	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
        	    	}else if("1".equals(cycle))
        	    	{
        	    		String a_quarter = rs.getString("thequarter");
    	    			if("1".equals(a_quarter))
    	    			{
    	    				bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
    	    			}
    	    			else
    	    			{
    	    				bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
    	    			}
        	    	}else if("2".equals(cycle))
        	    	{
        	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
        	    	}
        	    	else if("7".equals(cycle))
        	    	{
        	    		bean.set("evaluate",rs.getString("sy")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("sm")+ResourceFactory.getProperty("datestyle.month")+rs.getString("sd")+ResourceFactory.getProperty("datestyle.day")+"  至  "+rs.getString("ey")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("em")+ResourceFactory.getProperty("datestyle.month")+rs.getString("ed")+ResourceFactory.getProperty("datestyle.day"));
        	    	}
        	    	else
        	    	{
             	    	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
        	    	}
                    bean.set("objecttype","0");
                    
                    // 添加本人(主体)的打分确认标识(planOpt:1=确认, 0|null=打分) modify by lium
                    int planOpt = rs.getInt("planOpt");
                    bean.set("planOpt", Integer.valueOf(planOpt));
                    
                    list.add(bean);
                    if("7".equals(cycle))
                    {
                    	if(yearmap.get(rs.getString("sy"))==null)
                    	{
                    		CommonData cd = new CommonData(rs.getString("sy"),rs.getString("sy"));
                    		this.yearlist.add(cd);
                    		yearmap.put(rs.getString("sy"), rs.getString("sy"));
                    	}
                    }
                    else
                    {
                    	if(yearmap.get(rs.getString("theyear"))==null)
                    	{
                    		CommonData cd = new CommonData(rs.getString("theyear"),rs.getString("theyear"));
                    		this.yearlist.add(cd);
                    		yearmap.put(rs.getString("theyear"), rs.getString("theyear"));
                    	}
                    }
	    		}
	    		rs.close();
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	    }
	  /**
	     * 取得评估期年列表
	     * @param a0100
	     * @return
	     */
	    public ArrayList getYearList(String a0100)
	    {
	    	ArrayList list = new ArrayList();
	    	try
	    	{
	    		StringBuffer buf = new StringBuffer();
	    	    ContentDAO dao = new ContentDAO(this.conn);
		        RowSet rs = null;
		        list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	    		buf.append("select * from (");
	    		buf.append(" (select theyear from per_plan where plan_id in (");
	    		buf.append(" select ppb.plan_id from per_plan_body ppb,per_object po where ppb.body_id='5' and ppb.plan_id=po.plan_id and po.object_id='"+a0100+"')");
	    		buf.append(" and (status='8'  or status='4' or status='7' or status='5' or status='6')");
	    		buf.append(" and method='2' and cycle<>'7' and object_type='2')");
	            buf.append(" union ");
	          
	            buf.append(" ( select "+Sql_switcher.year("start_date")+" as theyear from per_plan where plan_id in (");
	           
	            buf.append(" select ppb.plan_id from per_plan_body ppb,per_object po where ppb.body_id='5' and ppb.plan_id=po.plan_id and po.object_id='"+a0100+"')");
	    		buf.append(" and (status='8'  or status='4' or status='7' or status='5' or status='6')");
	    		buf.append(" and method='2' and cycle='7' and object_type='2')");
	    		buf.append(" )temp order by theyear");
	    		rs = dao.search(buf.toString());
	    		HashMap map = new HashMap();
	    		while(rs.next())
	    		{
	    			if(map.get(rs.getString("theyear"))==null)
	    			{
	    				list.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
	    				map.put(rs.getString("theyear"),rs.getString("theyear"));
	    			}
	    		}
	    		rs.close();
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	    }
	    /**
	     * 取得评估期季度列表
	     * @param a0100
	     * @param year
	     * @return
	     */
	    private static ArrayList quarterList=null;
	    public ArrayList getQuarterList()
	    {
	    	if(quarterList==null)
	    	{
	    		quarterList = new ArrayList();
	         	try
	         	{
	        		StringBuffer buf = new StringBuffer();
	         		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='12'");
	        		ContentDAO dao = new ContentDAO(this.conn);
	    	    	RowSet rs = dao.search(buf.toString());
	    	    	quarterList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	    	    	while(rs.next())
	         		{
	    	    		quarterList.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
	        		}
	    	    	rs.close();
	        	}
	        	catch(Exception e)
	        	{
	         		e.printStackTrace();
	        	}
	    	}
	    	return quarterList;
	    }
	    private static ArrayList monthList = null;
	    public ArrayList getMonthList()
	    {
	    	if(monthList==null)
	    	{
	    		monthList = new ArrayList();
	        	try
	        	{
	        		StringBuffer buf = new StringBuffer();
	        		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='13'");
	        		ContentDAO dao = new ContentDAO(this.conn);
	        		RowSet rs = dao.search(buf.toString());
	        		monthList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	        		while(rs.next())
	        		{
	        			monthList.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
	        		}
	        		rs.close();
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	    	}
	    	return monthList;
	    }
	    private static ArrayList statusList=null;
	    public ArrayList getStatusList()
	    {
	    	if(statusList==null)
	    	{
	    		statusList = new ArrayList();
	        	try
	        	{
	        		statusList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	        		statusList.add(new CommonData("-2","执行中"));
	        		statusList.add(new CommonData("8",ResourceFactory.getProperty("org.performance.Published")));
	        		statusList.add(new CommonData("4",ResourceFactory.getProperty("org.performance.start")));
	        		statusList.add(new CommonData("5",ResourceFactory.getProperty("label.commend.stop")));
	        		statusList.add(new CommonData("6",ResourceFactory.getProperty("org.performance.pg")));
	        		statusList.add(new CommonData("7",ResourceFactory.getProperty("org.performance.end")));
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	    	}
	    	return statusList;
	    }
	  /**
	     * 取考核等级
	     * @param degreeid
	     * @return
	     */
	    public String getDegreeDesc(int plan_id,String object_id)
	    {
	    	String desc = "";
	        try
	         {
	        	
	        	String tableName = "per_result_"+plan_id;
	        	DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tableName);
	        	DbWizard dbWizard=new DbWizard(this.conn);  		
	    		Table table=new Table(tableName);
	    		if(dbWizard.isExistTable(table.getName(),false))
	    		{
	    			StringBuffer buf = new StringBuffer();
	    			buf.append(" select resultdesc from ");
	    			buf.append(tableName+" where object_id='"+object_id+"'");
	    			ContentDAO dao = new ContentDAO(this.conn);
	    			RowSet rs = dao.search(buf.toString());
	    			while(rs.next())
	    			{
	    				desc=rs.getString("resultdesc")==null?"":rs.getString("resultdesc");
	    			}
	    			rs.close();
	    		}
	    		if(desc==null)
	    			desc="";
	         }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	    	return desc;
	    }
	    public int getFirstMonthInQuarter(String quarter)
	    {
	    	int month=1;
	    	try
	    	{
	    		int a_quarter = Integer.parseInt(quarter);
	    		switch(a_quarter)
	    		{
	    		case 1:
	    			month=1;
	    			break;
	    		case 2:
	    			month=4;
	    			break;
	    		case 3:
	    			month=7;
	    			break;
	    		case 4:
	    			month=10;
	    			break;
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return month;
	    }
 
		public String getRejectReason(String object_id,String plan_id,String type)
		{
			StringBuffer reason= new StringBuffer("");
			try
			{
				String sql = " select reasons,mainbody_id,a0101,body_id from per_mainbody where object_id='"+object_id+"' and plan_id="+plan_id+" and reasons is not null";
				ContentDAO dao = new ContentDAO(this.conn);
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id", Integer.parseInt(plan_id));
				vo = dao.findByPrimaryKey(vo);
				/**=0为不记名*/
				int plan_type=vo.getInt("plan_type");
				HashMap levelMap = null;
				if(plan_type==0)
				{
					SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.conn);
					levelMap=bo.getLevelName();
				}
				RowSet rs = dao.search(sql);
				String name="";
				if("2".equals(type))
				{
					name=this.getName(object_id,plan_id);
				}
				int i=1;
				while(rs.next())
				{
					String body_id=rs.getString("body_id");
					String mainName=rs.getString("a0101");
					if(levelMap!=null)
					{
						if(levelMap.get(body_id)!=null)
						{
							mainName=(String)levelMap.get(body_id);
						}
						else
						{
							mainName="";
						}
					}
					if(Sql_switcher.readMemo(rs, "reasons")==null|| "".equals(Sql_switcher.readMemo(rs, "reasons").trim()))
						continue;
					if(i!=1)
						reason.append("\r\n");
					reason.append(i+".");
					if("1".equals(type))
					{
			    		reason.append(mainName+" 对您的"+ResourceFactory.getProperty("info.appleal.state10")+"原因为:\r\n");
					}
					else
					{
						reason.append(mainName+" 对 "+name+" 的"+ResourceFactory.getProperty("info.appleal.state10")+"原因为:\r\n");
					}
					reason.append("     "+Sql_switcher.readMemo(rs, "reasons").trim());
					i++;
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return reason.toString();
		}
		public String getDescript(String plan_id)
		{
			String descript="";
			try
			{
				String sql = " select descript from per_plan where plan_id="+plan_id;
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					descript=Sql_switcher.readMemo(rs, "descript");
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return descript;
		}
		public String getName(String a0100,String plan_id)
		{
			String name="";
			try
			{
				String sql = "select a0101 from per_object where object_id='"+a0100+"'";
				ContentDAO dao = new ContentDAO(conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					name=rs.getString("a0101");
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return name;
		}
		public static String getSpflagDesc(String sp_flag)
		{
			String desc="";
			
			if("-01".equals(sp_flag)) //状态初始化时，我将值设为-01了
				sp_flag="01";
			
			/**联通项目，保持原来的代码值*/
			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				desc=AdminCode.getCodeName("23",sp_flag);
			}
			else
			{
	    		if("07".equalsIgnoreCase(sp_flag))
	    		{
	    			desc=ResourceFactory.getProperty("info.appleal.state10");
	     		}
	    		else if("02".equals(sp_flag))
	    		{
	    			desc=ResourceFactory.getProperty("performance.spflag.yi")+ResourceFactory.getProperty("info.appleal.state7");
	    		}
	    		else if("03".equals(sp_flag))
	    		{
		    		desc=ResourceFactory.getProperty("performance.spflag.yi")+ResourceFactory.getProperty("info.appleal.state8");
	    		}
	    		else
	    		{
		    		desc=AdminCode.getCodeName("23",sp_flag);
	    		}
			}
			return desc;
		}
		public HSSFWorkbook getHwb() {
			return hwb;
		}
		public void setHwb(HSSFWorkbook hwb) {
			this.hwb = hwb;
		}
		public HSSFSheet getSheet() {
			return sheet;
		}
		public void setSheet(HSSFSheet sheet) {
			this.sheet = sheet;
		}
		public HSSFRow getRow() {
			return row;
		}
		public void setRow(HSSFRow row) {
			this.row = row;
		}
		public String getModel() {
			return model;
		}
		public void setModel(String model) {
			this.model = model;
		}
		
}
