/**
 * 创建日期 2005-7-7
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author luangaojiong
 * 绩效统计图
 */
public class SearchStatisticDrawingTrans extends IBusiness {

	Hashtable selfHt=new Hashtable();  //本人的分数
	Hashtable maxHt=new Hashtable();	//最大分值
	Hashtable argHt=new Hashtable();	//平均分值
	Hashtable allHt=new Hashtable();	//所有要素名称与id号
	private String picFlag="-1";
	private String tabid="";
	private String a_planid="";
	public void execute() throws GeneralException {
		/**
		 * 图形与表格的控制
		 */
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String model=(String)this.getFormHM().get("model");
		if(hm.get("picFlag")==null)		{
			this.picFlag="1";
			this.getFormHM().put("drawingFlag","1");	//图形控制
			this.getFormHM().put("enrol_flag","0");
		//	this.getFormHM().put("drawingFlag","1");	//表格控制
		//	this.getFormHM().put("enrol_flag","0");
		}
		else if("0".equals(hm.get("picFlag").toString()))
		{
			this.picFlag=hm.get("picFlag").toString();
			this.getFormHM().put("drawingFlag","0");	//表格控制
			this.getFormHM().put("enrol_flag","0");
		}else if(hm.get("picFlag").toString().indexOf("P")!=-1)
		{
			this.picFlag=hm.get("picFlag").toString();
			this.tabid=this.picFlag.substring(1);
			this.getFormHM().put("tabid",this.tabid);
			this.getFormHM().put("drawingFlag",this.picFlag);	
			this.getFormHM().put("enrol_flag","5");				
			
		}
		else
		{
			this.picFlag=hm.get("picFlag").toString();
			this.getFormHM().put("drawingFlag","1");	//图形控制
			this.getFormHM().put("enrol_flag","0");
		}
		
		
		
		getUserGrade();
		
		  ArrayList list=(ArrayList)this.getFormHM().get("planList");
		  if("0".equals(model))
		  {
		  if (hm.get("planNum") != null) {
	  			String planId = hm.get("planNum").toString();
	  			boolean flag=false;
	  			for(int i=0;i<list.size();i++)
	  			{
	  				CommonData d=(CommonData)list.get(i);
	  				if(d.getDataValue().equalsIgnoreCase(planId))
	  					flag=true;
	  			}
	  			
	  			if(!flag)
	  			{
	  				this.getFormHM().put("statisticDrawHm",new HashMap());
	  				
	  				
	  				/****/
	  			
	  				this.getFormHM().put("GATIShowDegree","");
	  				this.getFormHM().put("planNum","");
	  				this.getFormHM().put("totalGrade","");
	  				this.getFormHM().put("grade_id","");
	  				this.getFormHM().put("examinelist",new ArrayList());

	  				this.getFormHM().put("itemwhilelist",new ArrayList());
	  
	  				this.getFormHM().put("elevellist",new ArrayList());

	  				this.getFormHM().put("statisticDrawHm",new HashMap());
	  				this.getFormHM().put("pointNotelst",new ArrayList());
	  				this.getFormHM().put("knowlist",new ArrayList());

	  				//dengcan  add 20070320
	  				this.getFormHM().put("nodeKnowDegree","");  //了解程度
	  				this.getFormHM().put("wholeEval","");            //总体评价
	  				this.getFormHM().put("isShowStatistic","");
	  				
	  			//	this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
	  			    this.getFormHM().put("enrol_list",new ArrayList());	  			  
	  			    this.getFormHM().put("page_list",new ArrayList());
	  				
	  				
	  				
	  				
	  				/****/
	  				
	  				
	  				
					return;
	  			}
	          }
		  }
		  else
		  {
			  this.getFormHM().put("statisticDrawHm",new HashMap());
				
				
				/****/
			
				this.getFormHM().put("GATIShowDegree","");
				this.getFormHM().put("planNum","");
				this.getFormHM().put("totalGrade","");
				this.getFormHM().put("grade_id","");
				this.getFormHM().put("examinelist",new ArrayList());

				this.getFormHM().put("itemwhilelist",new ArrayList());

				this.getFormHM().put("elevellist",new ArrayList());

				this.getFormHM().put("statisticDrawHm",new HashMap());
				this.getFormHM().put("pointNotelst",new ArrayList());
				this.getFormHM().put("knowlist",new ArrayList());

				//dengcan  add 20070320
				this.getFormHM().put("nodeKnowDegree","");  //了解程度
				this.getFormHM().put("wholeEval","");            //总体评价
				this.getFormHM().put("isShowStatistic","");
				
			//	this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
			    this.getFormHM().put("enrol_list",new ArrayList());	  			  
			    this.getFormHM().put("page_list",new ArrayList());
		  }
	}
	/**
	 * 控制函数
	 *
	 */
	void getUserGrade() {

		/**
		 * 得到计划号
		 */
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = "0"; //计划号

		if (hm.get("planNum") != null) {
			planId = hm.get("planNum").toString();
			this.getFormHM().put("planNum", planId);
			
		} else {
				if(this.getFormHM().get("planNum")==null || "".equals(this.getFormHM().get("planNum").toString()))
				{
					this.getFormHM().put("statisticDrawHm",new HashMap());
					
					return;
					
				}
				else
				{
					planId=this.getFormHM().get("planNum").toString();
				}
				
		}
		a_planid=planId;
		if("#".equals(planId.trim()))
		{
			this.getFormHM().put("planNum","");
			HashMap drawingHm=new HashMap();
			drawingHm.put(ResourceFactory.getProperty("lable.examine.selffraction"),new ArrayList());
			drawingHm.put(ResourceFactory.getProperty("lable.examine.maxfraction"),new ArrayList());
			drawingHm.put(ResourceFactory.getProperty("lable.examine.argfraction"),new ArrayList());
			this.getFormHM().put("statisticDrawHm",drawingHm);	
			this.getFormHM().put("pointNotelst",new ArrayList());
			return;
		}
		
		//***************************取传过来用户id
		
		String objectId="0";
				
		
		ArrayList list = new ArrayList();		//列Arraylist
		String whereStr=" where 1=1";
		/**
		 * 得到结果表中的信息
		 */
		ResultSet resultset = null;
		try {
			
			StringBuffer sb = new StringBuffer();
			
			/**
			 * 根据是否是领导查看标识处理条件
			 */
			if("1".equals(hm.get("planFlag").toString()))
			{
				if(hm.get("objectId")!=null)
				{
					StringBuffer sbTemp=new StringBuffer();
					String objectId1=this.getFormHM().get("objectId").toString();
					sbTemp.append(" where object_id='");
					sbTemp.append(objectId1);
					sbTemp.append("'");
					whereStr=sbTemp.toString();	
					
					this.getFormHM().put("nid",objectId1);
				}
				else
				{
					/**
					 * 清除图形及注释操作
					 */
					HashMap drawingHm=new HashMap();
					
					drawingHm.put(ResourceFactory.getProperty("lable.examine.selffraction"),new ArrayList());
					drawingHm.put(ResourceFactory.getProperty("lable.examine.maxfraction"),new ArrayList());
					drawingHm.put(ResourceFactory.getProperty("lable.examine.argfraction"),new ArrayList());
					this.getFormHM().put("statisticDrawHm",drawingHm);	
					this.getFormHM().put("pointNotelst",new ArrayList());
					//throw new GeneralException("","用户的ID为空!","","");
				
				}
			}
			else 
			{
				StringBuffer sbTemp=new StringBuffer();
				sbTemp.append(" where object_id='");
				sbTemp.append(this.userView.getA0100());
				sbTemp.append("'");
				whereStr=sbTemp.toString();	
				this.getFormHM().put("nid",this.userView.getA0100());
				//System.out.println("-------->userId-->"+this.userView.getUserId());		
			}
			
			sb.append("select * from per_result_");
			sb.append(planId);
			sb.append(whereStr);
						
			String resuTableName="per_result_"+planId;		//取得表名
			{
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel(resuTableName);
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			resultset = dao.search(sb.toString());
			
			
			/**
			 * 得到列数
			 */
			
			list=getColumnlst(sb.toString());
			ArrayList pointidlist=new ArrayList();	//结果表中要素的编号
			
			/**
			 * 得到自己的分值Hashtable对象
			 */
			String pointIdName = "";
			String pointScoreValue = "";
			if (resultset.next()) {

				for (int i = 0; i < list.size(); i++) {
					
					pointScoreValue= PubFunc.NullToZero(resultset.getString(list.get(i).toString()));
					pointIdName = list.get(i).toString();
					pointIdName = pointIdName.substring(2, pointIdName.length());
					pointidlist.add(pointIdName);
					if(!selfHt.containsKey(pointIdName))
					{
						selfHt.put(pointIdName,pointScoreValue);
					}
				
				}

			} else 
			{
				/**
				 * 清除图形及注释操作
				 */
				
			    HashMap drawingHm=new HashMap();
			    drawingHm.put(ResourceFactory.getProperty("lable.examine.selffraction"),new ArrayList());
			    drawingHm.put(ResourceFactory.getProperty("lable.examine.maxfraction"),new ArrayList());
			    drawingHm.put(ResourceFactory.getProperty("lable.examine.argfraction"),new ArrayList());
			    this.getFormHM().put("statisticDrawHm",drawingHm);	
			    this.getFormHM().put("pointNotelst",new ArrayList());
				return;
			//	throw new GeneralException("","该用户的没有考核结果!","","");
							
			}
			resultset.close();
			
			DrawingHelp dh=new DrawingHelp();
			
			/**
			 * 得到所有要素
			 */			
			allHt=dh.getAllPoint(this.getFrameconn());			
			/**
			 * 得到最大
			 */			
			maxHt=dh.getMaxFunction(this.getFrameconn(),list,resuTableName);			
			/**
			 * 得到结果表总列数
			 */
			resultset=dao.search("select count(*) as count from "+resuTableName);
			String count="0";
			if(resultset.next())
			{
				count=resultset.getString("count");
			}
			/**
			 * 得到平均Hashtable
			 */
			argHt=dh.getArgHashtable(this.getFrameconn(),list,resuTableName,count);
			if(this.picFlag!=null&& "1".equals(this.picFlag))
			{
				ArrayList drawinglist=new ArrayList();
				/**
				 * 得到要素统计图对象ArrayList 
				 */
				drawinglist=getPointlist(pointidlist);
				/**
				 * 前台要素说明
				 */
				this.getFormHM().put("pointNotelst",drawinglist);
				/**
				 * 压入前台统计图形
				 */
				doDrawing(drawinglist);
			}
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
		}finally {
			PubFunc.closeResource(resultset);
		}
		
		
		
	}
	
	
	/**
	 * 得到要素统计图对象ArrayList对象
	 * 
	 * @param drawinglist
	 */
	public ArrayList getPointlist(ArrayList pointidlist)
	{
		ArrayList drawinglisttemp=new ArrayList();
		String pointId="0";
		for(int i=0;i<pointidlist.size();i++)
		{
			pointId=pointidlist.get(i).toString();
			DrawingBean db=new DrawingBean();
			db.setPointId(pointId);
			if(allHt.containsKey(pointId))
			{
				db.setPointName(allHt.get(pointId).toString());
			}
			if(selfHt.containsKey(pointId))
			{
				db.setSelfFraction(selfHt.get(pointId).toString());
			}
			
			if(maxHt.containsKey(pointId))
			{
				db.setMaxFraction(maxHt.get(pointId).toString());
			}
			
			if(argHt.containsKey(pointId))
			{
				db.setAverageFraction(argHt.get(pointId).toString());
			}
			drawinglisttemp.add(db);
		}
		return drawinglisttemp;
	}
	
	
	public HashMap getPerPointScore(String planid)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select ptp.point_id,ptp.score from per_plan pp, per_template_item pti,per_template_point ptp " 
					+" where pp.template_id=pti.template_id and pti.item_id=ptp.item_id and  pp.plan_id="+planid;
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				map.put(this.frowset.getString(1).toLowerCase(),this.frowset.getString(2));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	/**
	 * 得到输出的图形的HashMap
	 * @param sql
	 * @return
	 */
	public void doDrawing(ArrayList drawinglist)
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String showtype=(String)hm.get("showtype");  //显示模式  0:按分值  1:按比例
		if(showtype==null)
			showtype="0";
		this.getFormHM().put("showtype",showtype);
		
		ArrayList selflist=new ArrayList();		//本人的分数
		ArrayList maxlist=new ArrayList();		//最大分数
		ArrayList averagelist=new ArrayList();	//平均分数
		String pointId="";
		
		HashMap map=getPerPointScore(a_planid);
		
		for(int i=0;i<drawinglist.size();i++)
		{
			pointId=((DrawingBean)drawinglist.get(i)).getPointId();
			
			 CommonData voself=new CommonData();
		      voself.setDataName(pointId);
		      if("0".equals(showtype))
		    	  voself.setDataValue(((DrawingBean)drawinglist.get(i)).getSelfFraction());
		      else
		      {
		    	  String self_value=((DrawingBean)drawinglist.get(i)).getSelfFraction()==null?"0":((DrawingBean)drawinglist.get(i)).getSelfFraction();
		    	  BigDecimal self=new BigDecimal(self_value);
		    	  BigDecimal maxValue=new BigDecimal((String)map.get(pointId.toLowerCase()));
		    	  String a_value= self.divide(maxValue,3,BigDecimal.ROUND_HALF_UP).toString();
		    	  voself.setDataValue(a_value);
		      }
		      
		      
		      selflist.add(voself);
		     CommonData vomax=new CommonData();
		      vomax.setDataName(pointId);
		      vomax.setDataValue(((DrawingBean)drawinglist.get(i)).getMaxFraction());
		      maxlist.add(vomax);
		     CommonData voaverage=new CommonData();
		      voaverage.setDataName(pointId);
		      voaverage.setDataValue(((DrawingBean)drawinglist.get(i)).getAverageFraction());
		      averagelist.add(voaverage);
    	}
		
		HashMap drawingHm=new HashMap();
			
		
		
		if("0".equals(showtype))
		{
			drawingHm.put(ResourceFactory.getProperty("lable.examine.selffraction"),selflist);
			drawingHm.put(ResourceFactory.getProperty("lable.examine.maxfraction"),maxlist);
			drawingHm.put(ResourceFactory.getProperty("lable.examine.argfraction"),averagelist);
		}
		else
			drawingHm.put(ResourceFactory.getProperty("lable.examine.selffractionlv"),selflist);
		this.getFormHM().put("statisticDrawHm",drawingHm);		//压入图形输出HashMap中
		String chartsets = (String) (((HashMap) (this.getFormHM().get("requestPamaHM"))).get("chartParameters"));
		ChartParameter chartParameter = new ChartParameter(); //图形参数对象
		if(chartsets == null || "".equals(chartsets)){
			
		}else{
			try {
				chartsets = new String(chartsets.getBytes("ISO-8859-1"),"GB2312");
				chartParameter.analyseChartParameter(chartsets);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}	
		this.getFormHM().put("chartParameter",chartParameter);
		this.getFormHM().put("title",chartParameter.getChartTitle());
	}
	/**
	 * 得到列对象
	 * @author luangaojiong
	 *
	 */
	public ArrayList getColumnlst(String sql)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try(
			ResultSet resultset = dao.search(sql);
		)
		{
		ResultSetMetaData resultsetmetadata = resultset.getMetaData();
		int columnCount = resultsetmetadata.getColumnCount();
		String conlumnName = "";
		
		/**
		 * 得到列数
		 */
		for (int j = 0; j < columnCount; j++) {
			conlumnName = resultsetmetadata.getColumnLabel(j + 1).toString();

			//conlumnName = conlumnName.toLowerCase();

			if ((conlumnName.startsWith("c") && "_".equals(conlumnName.substring(1, 2))) ||(conlumnName.startsWith("C") && "_".equals(conlumnName.substring(1, 2)))) {
				list.add(conlumnName);
				}

			}
		}
		catch(Exception ex)
		{
			
		}
		return list;
	}

}
