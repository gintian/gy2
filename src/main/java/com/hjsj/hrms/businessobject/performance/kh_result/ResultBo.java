package com.hjsj.hrms.businessobject.performance.kh_result;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>Title:ResultBo.java</p>
 * <p>Description>:ResultBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-10 下午02:27:27</p>
 * <p>@version: 4.0</p>
 * <p>@author: JinChunhai
 */

public class ResultBo 
{	
	private Connection conn;
	private UserView userView = null;
	private Hashtable planParamSet = new Hashtable();
    private LoadXml loadxml = null;
    //以下变量是为了解决  按岗位素质模型测评 郭峰
    private boolean isByModel = false;//勾选了“按岗位素质模型测评”，并且该岗位有指标
    private boolean modelOnly = false;//仅仅勾选了“按岗位素质模型测评”，但是该岗位下没有指标
    private String object_id = "";
    public ResultBo(Connection conn)
    {
    	this.conn=conn;
    }
    
    public ResultBo(Connection conn, UserView userView)
    {
		this.conn = conn;
		this.userView = userView;
    }
    public ResultBo(Connection conn, UserView userView,String plan_id,String object_id)
    {
		this.conn = conn;
		this.userView = userView;
		this.object_id = object_id;
		initData(plan_id,object_id);
    }
    /**为isByModel赋值*/
    public void initData(String plan_id,String object_id){
		try{
			RowSet rs = null;
		    ContentDAO dao = new ContentDAO(this.conn);
		    
		    boolean isModel = SingleGradeBo.getByModel(plan_id,this.conn);
		    this.modelOnly = isModel;
	    	if(!isModel){
	    		this.isByModel = false;
	    		return;
	    	}
	    	boolean isHasPoint = SingleGradeBo.isHaveMatchByModel(object_id,this.conn);
			if(isModel && isHasPoint){
				this.isByModel = true;
			}
			if(rs!=null) {
                rs.close();
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
    }
    
    //取得考核计划的评估方法
    public int getPlanMethod(int plan_id)
    {
    	int method=1;  //1。 360度考核  2.目标考核
    	RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
	    	rowSet=dao.search("select method from per_plan where plan_id="+plan_id);
	    	if(rowSet.next())
	    	{
	    		if(rowSet.getString("method")!=null) {
                    method=rowSet.getInt("method");
                }
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rowSet!=null)
    			{
    				rowSet.close();
    			}
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return method;
    }
    
    public int getObjectType(String plan_id)
    {
    	int object_type=2; 
    	RowSet rowSet = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
	    	rowSet =dao.search("select object_type from per_plan where plan_id="+plan_id);
	    	if(rowSet.next())
	    	{
	    		if(rowSet.getString("object_type")!=null) {
                    object_type=rowSet.getInt("object_type");
                }
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rowSet!=null)
    			{
    				rowSet.close();
    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return object_type;
    }
    
    /***************  生成评测表excel ***************/
    
    private HSSFWorkbook workbook = new HSSFWorkbook();
    private HSSFSheet sheet = null;
    private HSSFCellStyle centerstyle = null;
    private int rowNum = 0; // 行坐标
    private short colIndex = 0; // 纵坐标
    private HSSFRow row = null;
    private HSSFCell csCell = null;
    
    public String excecuteExcel(String distinctionFlag,String object_id,String plan_id)
    {
    	
    	String outputFile = "perResultTable_"+this.userView.getUserId()+".xls";
    	FileOutputStream fileOut = null;
    	try
		{
    		LazyDynaBean abean=null;
    		workbook = new HSSFWorkbook();
		    sheet = workbook.createSheet("Sheet0");
//		    centerstyle = style(workbook, 1);
		    centerstyle = style(workbook, 6);		    
		    HSSFCellStyle centerstyle_l=style(workbook,5);
		    HSSFCellStyle centerstyle_2=style(workbook,7);
		    
		    HSSFCellStyle centerstyle_left=style(workbook,8);
		    HSSFCellStyle centerstyle_middle=style(workbook,9);
		    HSSFCellStyle centerstyle_right=style(workbook,10);
		  //  ArrayList drawList =getDrawList(distinctionFlag,getPlanMethod(Integer.parseInt(object_id)),plan_id);
		    String sql="select bymodel from per_plan where plan_id='"+plan_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			ResultSet rs = null;
			String byModel="";
			rs=dao.search(sql);
			while(rs.next()){
				byModel=rs.getString("byModel")==null?"":rs.getString("byModel");
			}
			HashMap map=new HashMap();
			if("1".equals(byModel)){
				map =getPersonalInformationByModel(object_id, plan_id);//按岗位素质模型
			}else{
			    map =getPersonalInformation(object_id, plan_id);
			}
			
			/**基本信息集*/
			LazyDynaBean infobean=(LazyDynaBean)map.get("1");
			HashMap pointMap=(HashMap)map.get("3");
			HashMap itemMap = (HashMap)map.get("2");
			/**测评说明信息集*/
			ArrayList evaluationDescription=getEvaluationDescription(object_id, plan_id);
			LoadXml parameter_content = new LoadXml(this.conn, plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String showAppraiseExplain=(String)params.get("showAppraiseExplain");
			String astrNodeKnowDegree = (String)params.get("NodeKnowDegree");
			String gatiShowDegree=(String)params.get("GATIShowDegree");
			String limitrule=(String)params.get("limitrule");
			String isShowWholeEval=(String)params.get("WholeEval");
			/**考核项目，要素信息集*/
			ArrayList itemList =new ArrayList();
			if("1".equals(byModel)){
				itemList =getItemListByModel(object_id, plan_id, itemMap, pointMap,gatiShowDegree,limitrule);
			}else{
				itemList =getItemList(object_id, plan_id, itemMap, pointMap,gatiShowDegree,limitrule);
			}

			/**总体评价信息集*/
			ArrayList overallRating =getOverallRating(object_id, plan_id);
			ArrayList overallRatingDetail = getOverallRatingDetail(object_id, plan_id);
			/**了解程度信息集*/
			ArrayList understandingOf=getUnderstandingOf(object_id, plan_id);
			ArrayList understandingOfDetail=getUnderstandingOfDetail(object_id, plan_id);
			
		    this.rowNum++;
		    int object_type = this.getObjectType(plan_id);		    
		    if(object_type==2) {
                executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"个\r\n人\r\n信\r\n息",centerstyle);
            } else if(object_type==1) {
                executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"团\r\n队\r\n信\r\n息",centerstyle);
            } else if(object_type==3) {
                executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"单\r\n位\r\n信\r\n息",centerstyle);
            } else if(object_type==4) {
                executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"部\r\n门\r\n信\r\n息",centerstyle);
            }
			this.colIndex++;
			
			StringBuffer context=new StringBuffer();
			StringBuffer valBuf=new StringBuffer();
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			if(object_type==2)
			{
				context.append("单位名称:");
				valBuf.append((String)infobean.get("b0110")+"\r\n");								
				context.append("\r\n"+ fielditem.getItemdesc() +":");
				valBuf.append((String)infobean.get("e0122")+"\r\n");
				context.append("\r\n姓名:");
				valBuf.append((String)infobean.get("a0101")+"\r\n");
			}else if(object_type==1)
			{
			    context.append("单位名称:");
			    valBuf.append((String)infobean.get("b0110")+"\r\n");
			    context.append("\r\n"+ fielditem.getItemdesc() +":");
			    valBuf.append((String)infobean.get("e0122")+"\r\n");
			}
			else if(object_type==3)
			{
			    context.append("单位名称:");
			    valBuf.append((String)infobean.get("b0110")+"\r\n");			 
			}
			else if(object_type==4)
			{
			    context.append(""+ fielditem.getItemdesc() +":");
			    valBuf.append((String)infobean.get("e0122")+"\r\n");			 
			}
			context.append("\r\n综合评分:");
			valBuf.append((String)infobean.get("score")+"\r\n");
			context.append("\r\n测评等级:");
			valBuf.append((String)infobean.get("resultdesc")+"\r\n");
			executeCell(this.rowNum,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+1)),context.toString(),centerstyle_left);
			executeCell(this.rowNum,(short)(this.colIndex+2),this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+4)),valBuf.toString(),centerstyle_right);
			this.colIndex++;this.colIndex++;this.colIndex++;this.colIndex++;this.colIndex++;
			if(showAppraiseExplain!=null&&!"".equals(showAppraiseExplain)&& "true".equalsIgnoreCase(showAppraiseExplain))
			{
		    	executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"测\r\n评\r\n说\r\n明",centerstyle);
			}
			else
			{
				executeCell(this.rowNum,this.colIndex,this.rowNum+7,this.colIndex,"备\r\n注",centerstyle);
			}
			this.colIndex++;
			context.setLength(0);
//			if(showAppraiseExplain!=null&&!showAppraiseExplain.equals("")&&showAppraiseExplain.equals("1"))
			   StringBuffer valueBuf1 = new StringBuffer();	
			    StringBuffer valueBuf2 = new StringBuffer();
			if(showAppraiseExplain!=null&&!"".equals(showAppraiseExplain)&& "true".equalsIgnoreCase(showAppraiseExplain))
			{
			 	
		    	for(int i=0;i<evaluationDescription.size();i++)
		    	{
		    		abean=(LazyDynaBean)evaluationDescription.get(i);  
		    		String bodyname = (String)abean.get("bodyname")+":";
//		    		context.append(bodyname+(String)abean.get("count")+"   "+(String)abean.get("score")+"\r\n");
		    		context.append(bodyname+"\r\n");
		    		valueBuf1.append((String)abean.get("count")+"\r\n");
		    		String score  = (String)abean.get("score");
		    		score=score.length()>0?score+"分":score;
		    		valueBuf2.append(score+"\r\n");
		    	}
			}
			executeCell(this.rowNum,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+1)),context.toString(),centerstyle_left);	
			executeCell(this.rowNum,(short)(this.colIndex+2),this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+2)),valueBuf1.toString(),centerstyle_middle);
			executeCell(this.rowNum,(short)(this.colIndex+3),this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+4)),valueBuf2.toString(),centerstyle_right);	  
		    this.rowNum=this.rowNum+8;
		    int current=this.rowNum;
		    this.colIndex=0;
		    int  totalNum=0;
		    if(itemList.size()%2==0) {
                totalNum=itemList.size()/2;
            } else {
                totalNum=itemList.size()/2+1;
            }
		    executeCell(this.rowNum,this.colIndex,this.rowNum+8*totalNum-1,this.colIndex,"测\r\n评\r\n评\r\n分",centerstyle);
		    int current2=this.rowNum+8*totalNum-1;
		    this.rowNum=current;
		    this.colIndex=1;
		    for(int i=0;i<itemList.size();i++)
		    {
		    	abean=(LazyDynaBean)itemList.get(i);
		      	if(i!=0)
		    	{
		    		 this.rowNum=this.rowNum+8;
		    		// this.rowNum++;
		    		 this.colIndex=1;
		    	}
		    	
		    	context.setLength(0);
		    	context.append("        "+(String)abean.get("itemname")+"  "+(String)abean.get("score")+"\r\n");
		    	
		  	executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.colIndex+4)),context.toString(),centerstyle_2);
		  	context.setLength(0);
		  	valueBuf1.setLength(0);	
		    	ArrayList sublist=(ArrayList)abean.get("sublist");
		    	for(int j=0;j<sublist.size();j++)
		    	{
		    		abean=(LazyDynaBean)sublist.get(j);
		    		context.append((j+1)+"."+(String)abean.get("pointname")+"\r\n");
		    		valueBuf1.append((String)abean.get("score")+"\r\n");
		    	}
		    	
//		    	executeCell(this.rowNum,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+4)),context.toString(),centerstyle_l);
		    	executeCell(this.rowNum+1,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+2)),context.toString(),centerstyle_left);
		    	executeCell(this.rowNum+1,(short)(this.colIndex+3),this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+4)),valueBuf1.toString(),centerstyle_right);
		    	this.colIndex++;this.colIndex++;this.colIndex++;this.colIndex++;this.colIndex++;
		    	context.setLength(0);
		    	if(itemList.size()>i+1)
		    	{
			    	abean=(LazyDynaBean)itemList.get(i+1);
			    	context.append("        "+(String)abean.get("itemname")+"  "+(String)abean.get("score")+"\r\n");
			    	
			    	executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.colIndex+5)),context.toString(),centerstyle_2);
			    	context.setLength(0);
			    	
			    	valueBuf1.setLength(0);
			    	sublist=(ArrayList)abean.get("sublist");
			    	for(int j=0;j<sublist.size();j++)
			    	{
			    	    abean=(LazyDynaBean)sublist.get(j);
			    	    context.append((j+1)+"."+(String)abean.get("pointname")+"\r\n");
			    	    valueBuf1.append((String)abean.get("score")+"\r\n");
			    	}
				executeCell(this.rowNum+1,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+3)),context.toString(),centerstyle_left);
				executeCell(this.rowNum+1,(short)(this.colIndex+4),this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+5)),valueBuf1.toString(),centerstyle_right);
		    	}
		    	else
//		    	executeCell(this.rowNum,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+5)),context.toString(),centerstyle_l);
                {
                    executeCell(this.rowNum,this.colIndex,this.rowNum+7,Short.parseShort(String.valueOf(this.colIndex+5)),context.toString(),centerstyle_l);
                }
				i++;
				
		    	
		    }
//		    if(showAppraiseExplain!=null&&!showAppraiseExplain.equals("")&&showAppraiseExplain.equals("1"))
//		    if(showAppraiseExplain!=null&&!showAppraiseExplain.equals("")&&showAppraiseExplain.equalsIgnoreCase("true"))
		    if((astrNodeKnowDegree!=null&&!"".equals(astrNodeKnowDegree)&& "true".equalsIgnoreCase(astrNodeKnowDegree)) && (isShowWholeEval!=null&&!"".equals(isShowWholeEval)&& "true".equalsIgnoreCase(isShowWholeEval)))
			{
		       this.rowNum=current2+1;
		       this.colIndex=0;
		       executeCell(this.rowNum,this.colIndex,this.rowNum+8,this.colIndex,"选\r\n票\r\n统\r\n计",centerstyle);
		       context.setLength(0);
		       context.append("    总体评价:");
		       for(int i=0;i<overallRating.size();i++)
		       {
		        	abean=(LazyDynaBean)overallRating.get(i);
		        	context.append((String)abean.get("itemname")+" ");
		        	context.append((String)abean.get("vote")+" 票占 ");
		        	context.append((String)abean.get("percent")+"     ");
//		        	if(i%3==0&&i!=0)
//		    	    		context.append("\r\n    ");
		        }
		       /**fzg add*/
		       for(int i=0;i<overallRatingDetail.size();i++)
		       {
			   context.append("\r\n    ");
			   abean=(LazyDynaBean)overallRatingDetail.get(i);
			   context.append((String)abean.get("bodyname")+": ");
			   ArrayList sublist = (ArrayList)abean.get("sublist");
			   for(int j=0;j<sublist.size();j++)
			   {
			       LazyDynaBean mybean = (LazyDynaBean)sublist.get(j);
			       String itemname = (String)mybean.get("itemname");
			       String vote = (String)mybean.get("vote");
			       String percent = (String)mybean.get("percent");
			       
			       context.append(itemname+": ");
			       context.append(vote+" 票占 ");
			       context.append(percent+"     ");
			   }			   
		       }		       

		       context.append("\n    了解程度:");
		       for(int i=0;i<understandingOf.size();i++)
		       {
		        	abean=(LazyDynaBean)understandingOf.get(i);
		        	context.append((String)abean.get("itemname")+" ");
		    	    context.append((String)abean.get("vote")+" 票占 ");
		        	context.append((String)abean.get("percent")+"     ");
		        }
		       
		       /**fzg add*/
		       for(int i=0;i<understandingOfDetail.size();i++)
		       {
			   context.append("\r\n    ");
			   abean=(LazyDynaBean)understandingOfDetail.get(i);
			   context.append((String)abean.get("bodyname")+": ");
			   ArrayList sublist = (ArrayList)abean.get("sublist");
			   for(int j=0;j<sublist.size();j++)
			   {
			       LazyDynaBean mybean = (LazyDynaBean)sublist.get(j);
			       String itemname = (String)mybean.get("itemname");
			       String vote = (String)mybean.get("vote");
			       String percent = (String)mybean.get("percent");
			       
			       context.append(itemname+": ");
			       context.append(vote+" 票占 ");
			       context.append(percent+"     ");
			   }			   
		       }
		       
		        context.append("\r\n");
		        colIndex++;
		        executeCell(this.rowNum,this.colIndex,this.rowNum+8,Short.parseShort(String.valueOf(this.colIndex+10)),context.toString(),centerstyle_l);
			}
		    
		    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
		    workbook.write(fileOut);
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		
    		PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
    	}
    	return outputFile;
    	
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
		    fonttitle.setBold(true);;// 加粗
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
		case 5:
			 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.TOP);
			    style.setAlignment(HorizontalAlignment.LEFT);
			  break;
		case 6:
		    fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10);
		    fonttitle.setBold(true);;// 加粗
		    style.setFont(fonttitle);
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    break;
		case 7:
		    fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10);
		    fonttitle.setBold(true);;// 加粗
		    style.setFont(fonttitle);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.TOP);
			    style.setAlignment(HorizontalAlignment.LEFT);
		    break;
		case 8:
			 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);		
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.TOP);
			    style.setAlignment(HorizontalAlignment.LEFT);
			    style.setBorderRight(BorderStyle.NONE);
			  break;
		case 9:
			 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.TOP);
			    style.setBorderLeft(BorderStyle.NONE);
			    style.setBorderRight(BorderStyle.NONE);
			    style.setAlignment(HorizontalAlignment.LEFT);
			  break;
		case 10:
			 style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderLeft(BorderStyle.NONE);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.TOP);
			    style.setAlignment(HorizontalAlignment.LEFT);
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
    
    /****************** end ********************/
    
    
    
    /**
     * 取考核等级
     * @param degreeid
     * @return
     */
    public LazyDynaBean getDegreeDesc(int plan_id,String object_id,String columnsName)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	RowSet rs=null;
        try
         {
        	String tableName = "per_result_"+plan_id;
        	DbWizard dbWizard=new DbWizard(this.conn);  		
    		Table table=new Table(tableName);
    		if(dbWizard.isExistTable(table.getName(),false))
    		{
    			//考核确认是新增加字段，可能旧的结果表中没有confirmflag字段，会导致错误 sunjian 2017-11-23
    			if (!dbWizard.isExistField("per_result_"+plan_id, "confirmflag",
    					false)) {
    				Field field = new Field("confirmflag","confirmflag");
    				field.setDatatype(DataType.INT);
    				field.setLength(2);
    				table.addField(field);
    				dbWizard.addColumns(table);
    			} 
    			StringBuffer buf = new StringBuffer();
    			buf.append(" select " + columnsName + " from ");
    			buf.append(tableName+" where object_id='"+object_id+"'");
    			ContentDAO dao = new ContentDAO(this.conn);
    			rs = dao.search(buf.toString());
    			while(rs.next())
    			{
    				bean.set("resultdesc", rs.getString("resultdesc")==null?"":rs.getString("resultdesc"));
    				bean.set("confirmflag", rs.getString("confirmflag")==null?"":rs.getString("confirmflag"));
    			}
    		}
         }
        catch(Exception e)
        {
        	e.printStackTrace();
        }finally{
        	try
        	{
        		if(rs!=null) {
                    rs.close();
                }
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        }
    	return bean;
    }
    /**
     * 得到个人考核计划列表
     * @param object_id  考核对象id
     * @param distinctionFlag  模块标志=0绩效考核=1民主测评
     * @return
     */
    public ArrayList getPlanList(String object_id,String distinctionFlag,String year,UserView view,String busitype)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs =null;
    	try
    	{
    		ExamPlanBo ebo = new ExamPlanBo(this.conn);
			String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
    		
    		StringBuffer buf = new StringBuffer();
    		buf.append("select per_plan.object_type,per_plan.plan_id,per_plan.name,per_plan.template_id,per_plan.parameter_content,");
    		buf.append(Sql_switcher.isnull("per_plan.a0000", "999999")+" as norder from per_plan,per_object where (per_plan.status=7 or (per_plan.status=6 and per_plan.feedback=1))");
    		if(!"-1".equals(year)) {
                buf.append(" and per_plan.theyear="+year);
            }
    		buf.append(" and per_plan.object_type='2' and per_plan.plan_id=per_object.plan_id and ");
    		buf.append(" per_object.object_id='"+object_id+"' ");
    		//添加绩效、能力素质区分  chent 20151210 start
    		if("0".equals(busitype)) {
    			buf.append("and "+Sql_switcher.isnull("busitype","0")+"=0");
    		}else if("1".equals(busitype)) {
    			buf.append("and "+Sql_switcher.isnull("busitype","0")+"=1");
    		}
    		//添加绩效、能力素质区分  chent 20151210 end
    		buf.append("order by norder asc,per_plan.plan_id desc ");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs= dao.search(buf.toString());
	       	LoadXml loadXml=new LoadXml();
	      //  HashMap scoreMap = this.getObjectScore(object_id, "",2);
	       	StringBuffer sql  = new StringBuffer("");
	       	int i=0;
	       	HashMap amap = new HashMap();
    		while(rs.next())
    		{
    			/** 取消参数“考核计划按模板权限控制”对“本人考核结果”的控制 by lium
    			if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
				{
					String template_id = rs.getString("template_id");				
					if(!(view.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!view.isHaveResource(IResourceConstant.KH_MODULE,template_id))					
							continue;					
					}
				}
				*/
                String xmlContent =Sql_switcher.readMemo(rs,"parameter_content");
                String performanceType=loadXml.getPerformanceType(xmlContent);
                if(distinctionFlag.equals(performanceType))
                {
                	String name=rs.getString("name");
                    int plan_id=rs.getInt("plan_id");
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("plan_id",plan_id+"");
                    bean.set("name",name);
                    /**联通专版，如果没定义登记表，不出现详细链接*/
                    if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
                    {
                    	ArrayList tablist =null;
                    	tablist=this.getRnameListFromPlanID(plan_id+"");
                    	if(tablist==null||tablist.size()==0) {
                            bean.set("visibleEdit", "0");
                        } else {
                            bean.set("visibleEdit", "1");
                        }
                    }
                    else
                    {
                    	bean.set("visibleEdit", "1");
                    }
                    String objecttype=ResourceFactory.getProperty("jx.jifen.person");
                    /*if(scoreMap.get(plan_id+object_id)!=null)
                    	bean.set("score",(String)scoreMap.get(plan_id+object_id));
                    else
                    	bean.set("score","");*/
                    bean.set("object_type",objecttype);
                    //bean.set("grade", this.getDegreeDesc(plan_id, object_id));
                    bean.set("object_id",object_id);
                    amap.put(plan_id+object_id, bean);
                    //list.add(bean);
    	    	    LoadXml  loadxml1 = new LoadXml(this.conn, String.valueOf(plan_id));
    	    	    Hashtable htxml1 = new Hashtable();   	
    	    	    htxml1 = loadxml1.getDegreeWhole();
    				String deviationScoreUsed=(String) htxml1.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是  zzk 
    				String total_score="score";
    				if("1".equals(deviationScoreUsed)){
    					total_score="reviseScore as score";
    				}
                    if(i!=0) {
                        sql.append(" union all ");
                    }
                    sql.append("(select "+rs.getInt("norder")+" as norder,"+plan_id+" as plan_id,object_id,"+total_score+",resultdesc,ConfirmFlag from per_result_"+plan_id+" where object_id='"+object_id+"') ");
                    DbWizard dbWizard = new DbWizard(this.conn);
                    //个人计划，考核确认是新增加字段，可能旧的结果表中没有confirmflag字段，会导致错误
                    if (!dbWizard.isExistField("per_result_"+plan_id, "confirmflag",
        					false)) {
                    	Table table = new Table("per_result_"+plan_id);
        				Field field = new Field("confirmflag","confirmflag");
        				field.setDatatype(DataType.INT);
        				field.setLength(2);
        				table.addField(field);
        				dbWizard.addColumns(table);
        			} 
                    i++;
                }
    		}
    		if(rs!=null) {
                rs.close();
            }
    		if(sql.toString().length()>0)
    		{
    			rs = dao.search("select * from ("+sql.toString()+") T order by norder asc,plan_id desc");
    			while(rs.next())
    			{
    				String score=rs.getString("score")==null?"":(PubFunc.round(rs.getString("score"),2));
    				String grade=rs.getString("resultdesc")==null?"":rs.getString("resultdesc");
    				String confirmFlag=rs.getString("ConfirmFlag")==null?"":rs.getString("ConfirmFlag");//添加结果确认
    				String planid=rs.getString("plan_id");
    				if(amap.get(planid+object_id)!=null)
    				{
    					LazyDynaBean abean=(LazyDynaBean)amap.get(planid+object_id);
    					abean.set("grade", grade);
    					abean.set("score", score);
    					abean.set("confirmFlag", confirmFlag);
    					abean.set("personOrTeamType", "person");
    					list.add(abean);
    				}
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		if(rs!=null)
    		{
    			try
    			{
    			   rs.close();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return list;
    }
    public ArrayList getYearList(String object_id,String distinctionFlag)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs=null;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select per_plan.object_type,per_plan.plan_id,per_plan.name,per_plan.parameter_content,per_plan.theyear from per_plan,per_object where (per_plan.status=7 or per_plan.status=6)");
    		buf.append(" and per_plan.object_type='2' and per_plan.plan_id=per_object.plan_id and per_object.object_id='"+object_id+"'");
    		buf.append(" order by per_plan.theyear desc");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
	       	LoadXml loadXml=new LoadXml();
	       	list.add(new CommonData("-1","全部"));
	       	//HashMap scoreMap = this.getObjectScore(object_id, "",2);
	       	HashMap map = new HashMap();
    		while(rs.next())
    		{
    			
                String xmlContent =Sql_switcher.readMemo(rs,"parameter_content");
                String performanceType=loadXml.getPerformanceType(xmlContent);
                if(distinctionFlag.equals(performanceType))
                {
                	String theyear=rs.getString("theyear");
                	if(map.get(theyear)==null)
                	{
                		map.put(theyear, "1");
                		list.add(new CommonData(theyear,theyear));
                	}
                }
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
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
    	return list;
    }
    /**
     * 取得团队考核计划列表
     * @param object_id
     * @param distinctionFlag
     * @return
     */
    public ArrayList getOrgPlanList(String object_id,String distinctionFlag,String modelType,String year,UserView view)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		ExamPlanBo ebo = new ExamPlanBo(this.conn);
			String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			
    		StringBuffer buf = new StringBuffer();
    		buf.append("select object_type,per_plan.plan_id,name,parameter_content,per_plan.template_id,object_id,theyear,"+Sql_switcher.isnull("per_plan.a0000", "999999")+" as norder from per_plan,per_object where (per_plan.status=7 or (per_plan.status=6 and per_plan.feedback=1)) ");
    		buf.append(" and ");
    		if("UU".equalsIgnoreCase(modelType))
    		{
    			buf.append(" (per_plan.object_type='1' or per_plan.object_type='3' or per_plan.object_type='4')");
    		}
    		if("UN".equalsIgnoreCase(modelType))
    		{
    			buf.append("  per_plan.object_type='3' ");
    		}
    		if("UM".equalsIgnoreCase(modelType))
    		{
    			buf.append("(per_plan.object_type='1' or per_plan.object_type='4') ");
    		}
    		if(!"-1".equals(year)) {
                buf.append(" and theyear="+year);
            }
    		buf.append(" and per_plan.plan_id = per_object.plan_id ");
    		
    		// 根据业务范围过滤掉当前用户不能看到的计划 lium
    		String[] ids = object_id.split("`");
    		if (ids.length == 1) {
    			buf.append(" and ");
    			if (ids[0].length() > 2) { // UM000003
    				buf.append(" per_object.object_id like '").append(ids[0].substring(2)).append("%'");
    			} else { // -1
    				if(!("UN".equals(ids[0])||"UN`".equals(ids[0]))) {
                        buf.append(" per_object.object_id like '").append(ids[0]).append("%'");
                    } else {
                        buf.append(" 1=1 ");
                    }
    			}
    		} else if (ids.length > 1) {
    			buf.append(" and ( 1<>1 ");
    			for (int i = 0; i < ids.length; i++) {
    				buf.append(" or per_object.object_id like '").append(ids[i].substring(2)).append("%'");
    			}
    			buf.append(")");
    		}
    		
    		buf.append(" order by norder asc,per_plan.plan_id desc");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
	       	LoadXml loadXml=new LoadXml();
	       	HashMap scoreMap = this.getObjectScore(object_id, modelType,1);
	       	StatisticPlan sp = new StatisticPlan(view,this.conn);
	       	HashMap map = new HashMap();
    		while(rs.next())
    		{
    			if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rs.getString("template_id");				
					if(!(view.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!view.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
                String xmlContent =Sql_switcher.readMemo(rs,"parameter_content");
                String performanceType=loadXml.getPerformanceType(xmlContent);
                if(distinctionFlag.equals(performanceType))
                {
                	String name=rs.getString("name");
                    int plan_id=rs.getInt("plan_id");
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("plan_id",plan_id+"");
                    bean.set("name",name);
                    /**联通专版，如果没定义登记表，不出现详细链接*/
                    if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
                    {
                    	ArrayList tablist =null;
                    	if(map.get(plan_id+"")!=null) {
                            tablist=(ArrayList)map.get(plan_id+"");
                        } else
                    	{
                    		tablist=sp.getRnameListFromPlanID(plan_id+"");
                    		map.put(plan_id+"", tablist);
                    	}
                    	if(tablist==null||tablist.size()==0) {
                            bean.set("visibleEdit", "0");
                        } else {
                            bean.set("visibleEdit", "1");
                        }
                    }
                    else
                    {
                    	bean.set("visibleEdit", "1");
                    }
                    String type=rs.getString("object_type");
                    String objecttype="";
                   /* String objecttype=ResourceFactory.getProperty("jx.khplan.unit");
                    String objecttype=ResourceFactory.getProperty("gz.columns.e0122");*/
                    if("1".equals(type)) {
                        objecttype=ResourceFactory.getProperty("jx.jifen.group");
                    } else if("3".equals(type)) {
                        objecttype=ResourceFactory.getProperty("jx.khplan.unit");
                    } else if("4".equals(type)) {
                        objecttype=ResourceFactory.getProperty("gz.columns.e0122");
                    }
                    if("UU".equalsIgnoreCase(modelType))
                    {
                        
                        String codevalue=AdminCode.getCodeName("UM",rs.getString("object_id"));
                        if(codevalue==null|| "".equals(codevalue.trim()))
                        {
                          	codevalue=AdminCode.getCodeName("UN", rs.getString("object_id"));
                         }
                        bean.set("object_type",objecttype+"("+codevalue+")");
                    }
                    if("UN".equalsIgnoreCase(modelType))
                    {
                       
                        String codevalue=AdminCode.getCodeName("UN", rs.getString("object_id"));

                        bean.set("object_type",objecttype+"("+codevalue+")");
                    }
                    if("UM".equalsIgnoreCase(modelType))
                    {
                       
                        String codevalue=AdminCode.getCodeName("UM", rs.getString("object_id"));
                        if(codevalue==null|| "".equals(codevalue.trim()))
                        {
                          	codevalue=AdminCode.getCodeName("UN", rs.getString("object_id"));
                        }
                        bean.set("object_type",objecttype+"("+codevalue+")");
                    }
                    if(scoreMap.get(plan_id+rs.getString("object_id"))!=null) {
                        bean.set("score",(String)scoreMap.get(plan_id+rs.getString("object_id")));
                    } else {
                        bean.set("score","");
                    }
                    LazyDynaBean listDesc = this.getDegreeDesc(plan_id, (String)rs.getString("object_id"), "resultdesc,confirmflag");
                    bean.set("grade", listDesc.get("resultdesc"));
                    bean.set("confirmFlag", listDesc.get("confirmflag"));
                    bean.set("object_id",rs.getString("object_id"));
                    
                    boolean isTeamLeader = getTeamLeader(plan_id, view, dao);
                    bean.set("isTeamLeader", isTeamLeader);
                    bean.set("personOrTeamType", "team");
                    list.add(bean);
                }
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return list;
    }
    
    /**
     * 获取是不是团队负责人
     * @param plan_id
     * @param body_id
     * @param view
     * @return
     */
    private boolean getTeamLeader(int plan_id,UserView view,ContentDAO dao) {
    	boolean isTeamLeader = false;
    	RowSet rs = null;
    	ArrayList list = new ArrayList();
    	try {
    		String sql = "select 1 from  per_mainbody where plan_id=? and body_id=? and mainbody_id = ?";
    		list.add(plan_id);
    		list.add("-1");
    		list.add(view.getA0100());
			rs = dao.search(sql,list);
			if(rs.next()) {
				isTeamLeader = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				PubFunc.closeResource(rs);
			}
		}
    	return isTeamLeader;
    }
    
    public ArrayList getYearList(String object_id,String distinctionFlag,String modelType)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs=null;
    	try
    	{
    		
    		StringBuffer buf = new StringBuffer();
    		buf.append("select object_type,per_plan.plan_id,name,parameter_content,object_id,theyear from per_plan,per_object where (per_plan.status=7 or per_plan.status=6) ");
    		buf.append(" and ");
    		if("UU".equalsIgnoreCase(modelType))
    		{
    			buf.append(" (per_plan.object_type='1' or per_plan.object_type='3' or per_plan.object_type='4')");
    		}
    		if("UN".equalsIgnoreCase(modelType))
    		{
    			buf.append("  per_plan.object_type='3' ");
    		}
    		if("UM".equalsIgnoreCase(modelType))
    		{
    			buf.append("(per_plan.object_type='1' or per_plan.object_type='4') ");
    		}
    		buf.append(" and per_plan.plan_id = per_object.plan_id and per_object.object_id  like '"+object_id+"%' order by per_plan.plan_id");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
	       	LoadXml loadXml=new LoadXml();
	       	list.add(new CommonData("-1","全部"));
	       	HashMap map = new HashMap();
    		while(rs.next())
    		{
    			
                String xmlContent =Sql_switcher.readMemo(rs,"parameter_content");
                String performanceType=loadXml.getPerformanceType(xmlContent);
                if(distinctionFlag.equals(performanceType))
                {
                	String theyear = rs.getString("theyear");
                	if(map.get(theyear)==null)
                	{
                		map.put(theyear, "1");
                		list.add(new CommonData(theyear,theyear));
                	}
                }
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 取得所有考核对象的总分
     * @param object_id
     * @param modelType
     * @param type=1单位，=2自己，=3员工
     * @return
     */
    public HashMap getObjectScore(String object_id,String modelType,int type)
    {
    	HashMap map = new HashMap();
    	
    	if (object_id == null || "".equals(object_id)) {
    		return map;
    	}
    	
    	RowSet rs =null;
    	try
    	{
    		/*if(modelType.equalsIgnoreCase("UU"))
    		{
    			buf.append(" (per_plan.object_type='1' or per_plan.object_type='3'  per_plan.object_type='4')");
    		}
    		if(modelType.equalsIgnoreCase("UN"))
    		{
    			buf.append("  per_plan.object_type='3' ");
    		}
    		if(modelType.equalsIgnoreCase("UM"))
    		{
    			buf.append("(per_plan.object_type='1' or per_plan.object_type='4') ");
    		}*/
    		StringBuffer buf = new StringBuffer();
    		buf.append("select plan_id from per_plan where (status=7 or status=6) and ");
    		if(type==1)
    		{
         		if("UU".equalsIgnoreCase(modelType))
        		{
         			buf.append(" (per_plan.object_type='1' or per_plan.object_type='3' or per_plan.object_type='4')");
        		}
        		if("UN".equalsIgnoreCase(modelType))
        		{
        			buf.append(" per_plan.object_type='3' ");
        		}
        		if("UM".equalsIgnoreCase(modelType))
    	    	{
    	    		buf.append(" (per_plan.object_type='1' or per_plan.object_type='4') ");
    	    	}
    		}
    		if(type==2)
    		{
    			buf.append(" object_type='2'");
    			buf.append(" and plan_id in (select plan_id from per_object where object_id='"+object_id+"')");
    		}
    		buf.append(" order by " + Sql_switcher.isnull("per_plan.a0000", "999999999") + " asc,plan_id desc ");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs= dao.search(buf.toString());
    		StringBuffer sql = new StringBuffer("");
    		int i=0;
    		while(rs.next())
    		{
    			String plan_id=rs.getString("plan_id");
	    	    LoadXml  loadxml1 = new LoadXml(this.conn, String.valueOf(plan_id));
	    	    Hashtable htxml1 = new Hashtable();   	
	    	    htxml1 = loadxml1.getDegreeWhole();
				String deviationScoreUsed=(String) htxml1.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是  zzk 
				String total_score="score";
				if("1".equals(deviationScoreUsed)){
					total_score="reviseScore as score";
				}
    			if(i>0) {
                    sql.append(" union ");
                }
    			sql.append("( select "+plan_id+" as plan_id,object_id,"+total_score+" from per_result_"+plan_id);
    			
    			if(type==1) { // 团队的object_id有可能会是这种形式:UM0101` lium
    				String _id = object_id.split("`")[0];
    				String _objectID = Pattern.matches("^[a-zA-z]{2}\\w*", _id) ? _id.substring(2) :_id;
    				sql.append(" where object_id like '"+_objectID+"%')");
    			}
    			if(type==2) {
                    sql.append(" where object_id='"+object_id+"')");
                }
    			i++;
    		}
    		if("".equals(sql.toString())) {
                return map;
            }
    		rs=dao.search("select * from ("+sql.toString()+") T ");
    		while(rs.next())
    		{
    			String planid=rs.getString("plan_id");
    			String objectid=rs.getString("object_id");
    			String score=rs.getString("score")==null?"":(PubFunc.round(rs.getString("score"),2));
    			map.put(planid+objectid,score);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return map;
    }
    public String getFirstLink(String drawId,String objectid,String planid)
    {
    	String link="";
    	if(drawId==null|| "".equals(drawId)) {
            return link;
        }
    	if("0".equals(drawId))
        {
             link="/performance/kh_result/kh_result_tables.do?b_init=link";
           
        }else if("2".equals(drawId))
        {
          link="/performance/kh_result/kh_result_reviews.do?b_init=init";
        }
        else if("3".equals(drawId))
        {
            link="/performance/kh_result/kh_result_overallrating.do?b_init=link&opt=1";
        }
        else if("1".equals(drawId))
        {
             link="/performance/kh_result/kh_result_figures.do?b_init=link&opt=2";
        }
        else if("4".equals(drawId))
        {
             link="/performance/showkhresult/showDirectionAnalyse.do?b_init=link&objectid="+objectid+"&operate=0";
        }
        else if("5".equals(drawId))
        {
              link="/performance/kh_result/kh_result_figures.do?b_objective=link&from_flag=0";
        }
        else if("6".equals(drawId))
        {
              link="/performance/kh_result/kh_result_interview.do?b_interview=link&object_id="+objectid+"&plan_id="+planid; 
        }
        else if("9".equals(drawId))
        {
              link="/performance/perAnalyse.do?b_personStation=query&opt=1&returnflag=9&a0100="+objectid+"&planIds="+planid; 
        }
        else
        {
           link="/performance/kh_result/kh_result_muster.do?b_init=link&tabid="+drawId;
        }
    	return link;
    }
    /**
     * 统计图列表
     * @param distinctionFlag
     * @param method 1:360度评估     2:目标评估 

     * @return
     */
    public ArrayList getDrawList(String distinctionFlag,int method,String planId,UserView userView){

    	ArrayList list = new ArrayList();
    	try
    	{
    		CommonData data=null;
    		AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.conn);
    		Hashtable ht_table=bo.analyseParameterXml();
    		/**360度评估*/
    		if(method==1)
    		{
    			String config="";
    			if(ht_table.get("evaluate")!=null) {
                    config=(String)ht_table.get("evaluate");
                }
    			config=config==null|| "".equals(config.trim())?"":(","+config+",");
    			/**模块区分标志=0是绩效考核，=1是民主测评*/
	    		if("0".equals(distinctionFlag))
	    		{
	    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
	    			{
	    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	list = sp.getRnameListFromPlanID(planId);
	    			}
	    			else
	    			{
	    	     		LoadXml parameter_content = new LoadXml(this.conn, planId);
	    	    		Hashtable params = parameter_content.getDegreeWhole();
	    		    	data = new CommonData("1",ResourceFactory.getProperty("general.inform.org.graph"));
	    		    	if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    		    	StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	ArrayList tablist = sp.getRnameListFromPlanID(planId);
	    		    	/**客户提出需求，不管是否选择登记表，都显示综合测评表 2010-07-30*/
	    		    	//if(tablist==null||tablist.size()<=0)
	    			    //{
	    	        	data= new CommonData("0",ResourceFactory.getProperty("org.performance.table"));
	    	        	if(config.toUpperCase().indexOf(",0,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    	        	if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config))
	    	        	{
		    	    		if(tablist!=null&&tablist.size()>0)
		    	    		{
		    		     		for(int i=0;i<tablist.size();i++)
		    		    		{
		    		    			list.add(tablist.get(i));
		    		    		}
		    		    	}
	    	        	}
	    	    		data= new CommonData("2",ResourceFactory.getProperty("org.performance.kp"));
	    	    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    	    		String wholeEval="";
	    	    		if(params.get("WholeEval")!=null) {
                            wholeEval=(String)params.get("WholeEval");
                        }
	    	    		String DescriptiveWholeEval="";
	    	    		if(params.get("DescriptiveWholeEval")!=null) {
                            DescriptiveWholeEval=(String)params.get("DescriptiveWholeEval");
                        }
	    	    		/**根据考核计划中的参数据顶是否显示总体评价*/
	    	    		if((wholeEval!=null&&!"".equals(wholeEval)&& "true".equalsIgnoreCase(wholeEval))||(DescriptiveWholeEval!=null&&!"".equals(DescriptiveWholeEval)&& "true".equalsIgnoreCase(DescriptiveWholeEval)))
	    		    	{
	                		data= new CommonData("3",ResourceFactory.getProperty("org.performance.zt"));
	                		if(config.toUpperCase().indexOf(",3,")!=-1|| "".equals(config)) {
                                list.add(data);
                            }
	    	    		}
	    			}
	    		}else
	    		{
	    			
	    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
	    			{
	    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	list = sp.getRnameListFromPlanID(planId);
	    			}
	    			else
	    			{
	    		    	data = new CommonData("3",ResourceFactory.getProperty("org.performance.zt"));
	    		    	if(config.toUpperCase().indexOf(",3,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    		    	data= new CommonData("4",ResourceFactory.getProperty("org.performance.qs"));
	    		    	if(config.toUpperCase().indexOf(",4,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    			}
	    		}
    		}
    		/**目标评估*/
    		else  if(method==2)
    		{
    			String config="";
    			if(ht_table.get("objective")!=null) {
                    config=(String)ht_table.get("objective");
                }
    			config=config==null|| "".equals(config.trim())?"":(","+config+",");
    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
    			{
    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
    		    	list = sp.getRnameListFromPlanID(planId);
    			}
    			else
    			{
         			data = new CommonData("1",ResourceFactory.getProperty("general.inform.org.graph"));
         			if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		data= new CommonData("5",ResourceFactory.getProperty("jx.khplan.khresulttable"));
    	    		if(config.toUpperCase().indexOf(",5,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		data= new CommonData("2",ResourceFactory.getProperty("org.performance.kp"));
    	    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
        			/**面谈记录*/
    	    		data=new CommonData("6",ResourceFactory.getProperty("jx.khplan.interview"));
    	    		if(config.toUpperCase().indexOf(",6,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		StatisticPlan sp = new StatisticPlan(userView,this.conn);
    		    	ArrayList alist = sp.getRnameListFromPlanID(planId);
    		    	if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config)) {
                        list.addAll(alist);
                    }
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    
    }
    public ArrayList getDrawList(String distinctionFlag,int method,String planId,UserView userView,String object_id,double percent,String model)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		CommonData data=null;
    		AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.conn);
    		Hashtable ht_table=bo.analyseParameterXml();
    		/**360度评估*/
    		if(method==1)
    		{
    			String config="";
    			
    			Hashtable returnHt=null;
				
    			AnalysePlanParameterBo AnalysePlanParameterBo = new AnalysePlanParameterBo(this.conn);
    			
    			ContentDAO dao1=new ContentDAO(this.conn);
    			RowSet rsPer_planXml=null;
    		 //---------------------------慧聪网需求   如果对象的参数里面设置了 结果反馈方式   那么优先走这个 否则走配置参数中的   zhaoxg add 2014-6-25--------------
    			String child = "";
    			double blind_point = 0.0;
    			try
    			{


    				rsPer_planXml = dao1.search("select parameter_content from per_plan where plan_id='"+planId+"'");
    			    if ( rsPer_planXml.next())
    			    {
    			    	String str_value = rsPer_planXml.getString("parameter_content");
    				if (str_value == null || (str_value != null && "".equals(str_value)))
    				{
    		
    				} 
    				else
    				{
    				    Document doc = PubFunc.generateDom(str_value);
    				    String xpath = "//PerPlan_Parameter/evaluate";
    				    XPath xpath_ = XPath.newInstance(xpath);
    				    Element ele = (Element) xpath_.selectSingleNode(doc);

    				    if (ele != null)
    				    {
    						
    						child=","+ele.getText()+",";
    						if (child.indexOf(",7,")!=-1&&ele.getAttributeValue("blind_point")!= null && !"".equals(ele.getAttributeValue("blind_point"))) {
                                blind_point=Double.parseDouble(ele.getAttributeValue("blind_point")==null||ele.getAttributeValue("blind_point").trim().length()<=0?"0.0":ele.getAttributeValue("blind_point"));
                            }
    						
    						if(blind_point!=0.0){
    							percent=blind_point;
    						}
    				    }
    					
    				  }
    			    }
    			} catch (Exception e)
    			{
    			    e.printStackTrace();
    			    throw GeneralExceptionHandler.Handle(e);
    			}
    			   			
				if(!"".equals(child)&&!",NO,".equals(child)) {
                    config=child;
                } else{
	    			if(ht_table.get("evaluate")!=null) {
                        config=(String)ht_table.get("evaluate");
                    }
					
				}
				//-------------------------------------------end----------------------------------------------------------------------
    			config=config==null|| "".equals(config.trim())?"":(","+config+",");
    			/**模块区分标志=0是绩效考核，=1是民主测评*/
	    		if("0".equals(distinctionFlag))
	    		{
	    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
	    			{
	    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	list = sp.getRnameListFromPlanID(planId);
	    			}
	    			else
	    			{
	    	     		LoadXml parameter_content = new LoadXml(this.conn, planId);
	    	    		Hashtable params = parameter_content.getDegreeWhole();
	    		    	data = new CommonData("1",ResourceFactory.getProperty("general.inform.org.graph"));
	    		    	if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    		    	StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	ArrayList tablist = sp.getRnameListFromPlanID(planId);
	    		    	/**客户提出需求，不管是否选择登记表，都显示综合测评表 2010-07-30*/
	    		    	//if(tablist==null||tablist.size()<=0)
	    			    //{
	    	        	data= new CommonData("0",ResourceFactory.getProperty("org.performance.table"));
	    	        	if(config.toUpperCase().indexOf(",0,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    	        	if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config))
	    	        	{
		    	    		if(tablist!=null&&tablist.size()>0)
		    	    		{
		    		     		for(int i=0;i<tablist.size();i++)
		    		    		{
		    		    			list.add(tablist.get(i));
		    		    		}
		    		    	}
	    	        	}
	    	    		data= new CommonData("2",ResourceFactory.getProperty("org.performance.kp"));
	    	    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    	    		String wholeEval="";
	    	    		if(params.get("WholeEval")!=null) {
                            wholeEval=(String)params.get("WholeEval");
                        }
	    	    		String DescriptiveWholeEval="";
	    	    		if(params.get("DescriptiveWholeEval")!=null) {
                            DescriptiveWholeEval=(String)params.get("DescriptiveWholeEval");
                        }
	    	    		/**根据考核计划中的参数据顶是否显示总体评价*/
	    	    		if((wholeEval!=null&&!"".equals(wholeEval)&& "true".equalsIgnoreCase(wholeEval))||(DescriptiveWholeEval!=null&&!"".equals(DescriptiveWholeEval)&& "true".equalsIgnoreCase(DescriptiveWholeEval)))
	    		    	{
	                		data= new CommonData("3",ResourceFactory.getProperty("org.performance.zt"));
	                		if(config.toUpperCase().indexOf(",3,")!=-1|| "".equals(config)) {
                                list.add(data);
                            }
	    	    		}
	    	    		
	    	    		//判断是否应该有评价盲点这个选项
	    	    		if(!"3".equalsIgnoreCase(model))
	    	    		{
		    	    		StringBuffer sb = new StringBuffer();
		    	    		sb.append("select * from per_table_"+planId+" where mainbody_id='"+object_id+"' and object_id='"+object_id+"'");
		    	    		ContentDAO dao = new ContentDAO(this.conn);
		    	    		RowSet rs = dao.search(sb.toString());
		    	    		if(rs.next()){//如果有考核主体为本人
		    	    			HashMap pointScoreMap = this.getFieldMap(object_id,planId,method);//得到所有的指标的分数
		    	    			ArrayList blindList = this.getBlindList(pointScoreMap,percent);
		    	    			ArrayList advantageList = (ArrayList)blindList.get(0);
		    	    			ArrayList weeknessList = (ArrayList)blindList.get(1);
		    	    			if((advantageList.size()>0 || weeknessList.size()>0) && percent>1e-6){
		    	    				data=new CommonData("7",ResourceFactory.getProperty("org.performance.blind"));
		    	    				if(config.toUpperCase().indexOf(",7,")!=-1|| "".equals(config)) {
                                        list.add(data);
                                    }
		    	    			}
		    	    		}
	    	    		}
	    	    		
	    	    		// 能力素质考核计划添加 岗位分析
	    	    		String busitype = String.valueOf(this.getPerPlanVo(planId).getInt("busitype"));
	    	    		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
	    	    		{
	    	    			data = new CommonData("9","岗位分析");
		    	    	    list.add(data);	    	    			
	    	    		}	    	    		
	    			}
	    		}else
	    		{
	    			
	    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
	    			{
	    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
	    		    	list = sp.getRnameListFromPlanID(planId);
	    			}
	    			else
	    			{
	    		    	data = new CommonData("3",ResourceFactory.getProperty("org.performance.zt"));
	    		    	if(config.toUpperCase().indexOf(",3,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    		    	data= new CommonData("4",ResourceFactory.getProperty("org.performance.qs"));
	    		    	if(config.toUpperCase().indexOf(",4,")!=-1|| "".equals(config)) {
                            list.add(data);
                        }
	    			}
	    		}
    		}
    		/**目标评估*/
    		else  if(method==2)
    		{
    			String config="";
    			
    			Hashtable returnHt=null;
				
    			AnalysePlanParameterBo AnalysePlanParameterBo = new AnalysePlanParameterBo(this.conn);
    			
    			ContentDAO dao1=new ContentDAO(this.conn);
    			RowSet rsPer_planXml=null;
			    //---------------------------慧聪网需求   如果对象的参数里面设置了 结果反馈方式   那么优先走这个 否则走配置参数中的   zhaoxg add 2014-6-25--------------
    			String child = "";
			    double blind_point=0.0;
    			try
    			{


    				rsPer_planXml = dao1.search("select parameter_content from per_plan where plan_id='"+planId+"'");
    			    if ( rsPer_planXml.next())
    			    {
    			    	String str_value = rsPer_planXml.getString("parameter_content");
    				if (str_value == null || (str_value != null && "".equals(str_value)))
    				{
    		
    				} 
    				else
    				{
						Document doc = PubFunc.generateDom(str_value);
    				    String xpath = "//PerPlan_Parameter/evaluate";
    				    XPath xpath_ = XPath.newInstance(xpath);
    				    Element ele = (Element) xpath_.selectSingleNode(doc);

    				    if (ele != null)
    				    {
    						
    				    	child=","+ele.getText()+",";
    						if (child.indexOf(",7,")!=-1&&ele.getAttributeValue("blind_point")!= null && !"".equals(ele.getAttributeValue("blind_point"))) {
                                blind_point=Double.parseDouble(ele.getAttributeValue("blind_point")==null||ele.getAttributeValue("blind_point").trim().length()<=0?"0.0":ele.getAttributeValue("blind_point"));
                            }
    						
    						if(blind_point!=0.0){
    							percent=blind_point;
    						}
    				    }
    					
    				  }
    			    }
    			} catch (Exception e)
    			{
    			    e.printStackTrace();
    			    throw GeneralExceptionHandler.Handle(e);
    			}
    			
				if(!"".equals(child)&&!",NO,".equals(child)) {
                    config=child;
                } else{
	    			if(ht_table.get("objective")!=null) {
                        config=(String)ht_table.get("objective");
                    }
				}
    			//------------------------------------end-----------------------------------------------------------
    			config=config==null|| "".equals(config.trim())?"":(","+config+",");
    			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
    			{
    				StatisticPlan sp = new StatisticPlan(userView,this.conn);
    		    	list = sp.getRnameListFromPlanID(planId);
    			}
    			else
    			{
    				// 查看是否保存了其他参数-目标考评中的几项。如没有保存，则走配置中默认保存的设置 chent 20161107 start
    				boolean saveConfig = false;// 
    				String[] configArray = config.split(",");
    				for(String s : configArray){
    					if(!StringUtils.isEmpty(s)){
    						saveConfig = true;
    						break;
    					}
    				}
    				if(!saveConfig){
    					RowSet rs1 = null;
    					ContentDAO dao11 = new ContentDAO(this.conn);
    					rs1 = dao11.search("select str_value from constant where constant='PER_PARAMETERS'");
    					if (rs1.next())
    					{
    						String str_value = rs1.getString("str_value");
    						if (str_value == null || (str_value != null && "".equals(str_value)))
    						{} else {
    							Document doc = PubFunc.generateDom(str_value);
    							String xpath = "//Per_Parameters";
    							XPath xpath_ = XPath.newInstance(xpath);
    							Element ele1 = (Element) xpath_.selectSingleNode(doc);
    							Element child1;
    							if (ele1 != null)
    							{
    								child1 = ele1.getChild("ResultVisible");
    								if(child1 != null) {
    									config = ","+child1.getAttributeValue("objective")+",";
    								}
    							}
    						}
    					}
    				}
    				// 查看是否保存了其他参数-目标考评中的几项。如没有保存，则走配置中默认保存的设置 chent 20161107 end
         			data = new CommonData("1",ResourceFactory.getProperty("general.inform.org.graph"));
         			if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		data= new CommonData("5",ResourceFactory.getProperty("jx.khplan.khresulttable"));
    	    		if(config.toUpperCase().indexOf(",5,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		data= new CommonData("2",ResourceFactory.getProperty("org.performance.kp"));
    	    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
        			/**面谈记录*/
    	    		data=new CommonData("6",ResourceFactory.getProperty("jx.khplan.interview"));
    	    		if(config.toUpperCase().indexOf(",6,")!=-1|| "".equals(config)) {
                        list.add(data);
                    }
    	    		StatisticPlan sp = new StatisticPlan(userView,this.conn);
    		    	ArrayList alist = sp.getRnameListFromPlanID(planId);
    		    	if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config)) {
                        list.addAll(alist);
                    }
    		    	
    		    	//判断是否应该有评价盲点这个选项
    		    	if(!"3".equalsIgnoreCase(model))
    	    		{
	    	    		StringBuffer sb = new StringBuffer();
	    	    		sb.append("select * from per_target_evaluation where plan_id='"+planId+"' and mainbody_id='"+object_id+"' and object_id='"+object_id+"'");
	    	    		ContentDAO dao = new ContentDAO(this.conn);
	    	    		RowSet rs = dao.search(sb.toString());
	    	    		if(rs.next()){//如果有考核主体为本人
	    	    			HashMap pointScoreMap = this.getFieldMap(object_id,planId,method);//得到所有的指标的分数
	    	    			ArrayList blindList = this.getBlindList(pointScoreMap,percent);
	    	    			ArrayList advantageList = (ArrayList)blindList.get(0);
	    	    			ArrayList weeknessList = (ArrayList)blindList.get(1);
	    	    			if((advantageList.size()>0 || weeknessList.size()>0) && percent>1e-6){
	    	    				data=new CommonData("7",ResourceFactory.getProperty("org.performance.blind"));
	    	    				if(config.toUpperCase().indexOf(",7,")!=-1|| "".equals(config)) {
                                    list.add(data);
                                }
	    	    			}
	    	    		}
    	    		}
    		    	
    		    	// 能力素质考核计划添加 岗位分析
    	    		String busitype = String.valueOf(this.getPerPlanVo(planId).getInt("busitype"));
    	    		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
    	    		{
    	    			data = new CommonData("9","岗位分析");
	    	    	    list.add(data);	    	    			
    	    		}
    			}
    		}   		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 取得模板要素列表
     * @param planid
     * @return
     */
    public ArrayList getPointList(String planid)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		ContentDAO dao = new ContentDAO(this.conn);

        		buf.append("select T.point_id,T.seq,pp.pointname from (select point_id,seq from per_template_point where item_id in (");
        		buf.append(" select item_id from per_template_item where (kind=1 or kind is null or "+Sql_switcher.length("kind")+"=0) and template_id=(");
        		buf.append(" select template_id from per_plan where plan_id=");
        		buf.append(planid+"))) T,per_point pp where T.point_id=pp.point_id order by T.seq");
        		rs = dao.search(buf.toString());
        		while(rs.next())
        		{
        			LazyDynaBean bean = new LazyDynaBean();
        			bean.set("point_id",rs.getString("point_id"));
        			bean.set("pointname",rs.getString("pointname"));
        			bean.set("type", "0");
        			list.add(bean);
        		}
        	    buf.setLength(0);
        		buf.append(" select item_id,itemdesc from per_template_item where kind=2 and template_id=(");
        		buf.append(" select template_id from per_plan where plan_id=");
        		buf.append(planid+") order by seq");
        	    rs = dao.search(buf.toString());
        		while(rs.next())
        		{
        			LazyDynaBean bean = new LazyDynaBean();
        			bean.set("point_id",rs.getString("item_id"));
        			bean.set("pointname",rs.getString("itemdesc"));
        			bean.set("type", "1");
        			list.add(bean);
        		}
	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**得到point_id与point_name的映射关系 郭峰*/
    public HashMap getPointIdNameMap(String plan_id,int flag){
    	//按岗位素质模型测评
    	HashMap map = new HashMap();
    	try{
    		StringBuffer buf = new StringBuffer("");
        	RowSet rs = null;
        	ContentDAO dao = new ContentDAO(this.conn);
        	if(flag==2){ //如果岗位有指标
        		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
        		Calendar calendar = Calendar.getInstance();				
        		String historyDate = sdf.format(calendar.getTime());
        		buf.append("select pcm.point_id,pp.pointname from per_competency_modal pcm left join per_point pp on pcm.point_id=pp.point_id where pcm.object_type='3' and pcm.object_id = (select "+Sql_switcher.isnull("e01a1","null")+" from usra01 where a0100='"+this.object_id+"')");
        		buf.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
        	}else if(flag==1){//如果岗位没有指标
        		buf.append("select pp.point_id,pp.pointname from per_template_item pti,per_template_point ptp,per_point pp ");
        		buf.append(" where pti.item_id=ptp.item_id and ptp.point_id=pp.point_id and pti.template_id=(select template_id from per_plan where plan_id="+plan_id+")");
        	}
    		rs = dao.search(buf.toString());
    		while(rs.next()){
    			String point_id = rs.getString("point_id")==null?"":rs.getString("point_id");
    			String point_name = rs.getString("pointname")==null?"":rs.getString("pointname");
    			map.put(point_id, point_name);
    		}
    		if(rs!=null) {
                rs.close();
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }
    
    public ArrayList getTemplateItemList(String planid)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append(" select item_id,itemdesc from per_template_item where template_id=(");
    		buf.append(" select template_id from per_plan where plan_id=");
    		buf.append(planid+")");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
    		 while(rs.next())
    		 {
    			 LazyDynaBean bean = new LazyDynaBean();
    			 bean.set("point_id",rs.getString("item_id"));
    			 bean.set("pointname",rs.getString("itemdesc"));
    			 list.add(bean);
    		 }
    				
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    
    /**
	  * 取得 分值序列str
	  * @param planIds
	  * @param a0100
	  * @param selectids
	  * @return
	  */
	 public String getSingleContrastStr(String planIds,String a0100,String selectids,String sameField,String modle)
	 {
		 StringBuffer str=new StringBuffer(",");
		 if(planIds.length()==0) {
             return "";
         }
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 
			 str.append(ResourceFactory.getProperty("lable.examine.maxfraction")+"~"+ResourceFactory.getProperty("lable.examine.maxfraction"));
			 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.maxfraction")+",")!=-1) {
                 str.append("~1");
             } else {
                 str.append("~0");
             }
			 str.append(","+ResourceFactory.getProperty("lable.examine.argfraction")+"~"+ResourceFactory.getProperty("lable.examine.argfraction"));
			 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.argfraction")+",")!=-1) {
                 str.append("~1");
             } else {
                 str.append("~0");
             }
		
			 RowSet rowSet=dao.search("select * from per_object where plan_id="+planIds+" and object_id='"+a0100+"'");
			 if(rowSet.next())
			 {	
				 if("3".equals(modle)){
					 str.append(","+ResourceFactory.getProperty("lable.examine.selfdepartscore")+"~"+ResourceFactory.getProperty("lable.examine.selfdepartscore"));
				 }else{
					 str.append(","+ResourceFactory.getProperty("lable.examine.selffraction")+"~"+ResourceFactory.getProperty("lable.examine.selffraction")); 
				 }
				 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selffraction")+",")!=-1) {
                     str.append("~1");
                 } else {
                     str.append("~0");
                 }
			 }
			 
			 if(sameField!=null && sameField.trim().length()>0 && !modelOnly)//并且没有勾选按岗位素质模型测评  郭峰
 			 {
				 str.append(","+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore")+"~"+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore"));
			//	 if((","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore")+",")!=-1)
				 if(selectids.trim().length()==0 || "null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore")+",")!=-1) {
                     str.append("~1");
                 } else {
                     str.append("~0");
                 }
 			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 return str.substring(1);
	 }
    
    /**
     * 得到生成统计图的map
     * @param planid
     * @param object_id
     * @param pointList
     * @param drawtype =0按分值画图=1按得分率画图
     * @return
     */
    public HashMap getFiguresMap(String planid,String object_id,ArrayList pointList,String drawtype,String chart_type,String selectids,String sameField,String model)
    {
    	HashMap map = new HashMap();
    	RowSet rs =null;
    	try
    	{
    		String tableName = "per_result_"+planid;
    		Table table = new Table(tableName);
    		boolean isHavePoint=true;
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(!dbWizard.isExistTable(table.getName(),false))
			{
    			if("3".equals(model)){//如果是团队
    				map.put(ResourceFactory.getProperty("lable.examine.selfdepartscore"), new ArrayList());
    			}else{
    				map.put(ResourceFactory.getProperty("lable.examine.selffraction"), new ArrayList());
    			}
				map.put(ResourceFactory.getProperty("lable.examine.maxfraction"), new ArrayList());
				map.put(ResourceFactory.getProperty("lable.examine.argfraction"), new ArrayList());
				map.put(ResourceFactory.getProperty("lable.examine.sameLevelAvgScore"), new ArrayList());
				return map;
			}
    		
    		double maxRadarScore = 0.0;//雷达图专用
    		
    		ArrayList selfList = new ArrayList();
    		ArrayList maxList = new ArrayList();
    		ArrayList avgList = new ArrayList();
    		ArrayList sleveList = new ArrayList();

    		if(modelOnly){//按岗位素质模型测评  郭峰
    			int flag = 1;//岗位没有指标
    			if(isByModel){
    				flag = 2;//岗位有指标
    			}
    			HashMap idnameMap = this.getPointIdNameMap(planid,flag);
    			StringBuffer sbSql = new StringBuffer();
    			sbSql.append("select self.point_id,self.self_score,maxscore.max_score,avgscore.avg_score from "); 
    			sbSql.append("(select point_id,score self_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0) self ");
    			sbSql.append("left join (select point_id,max(score) max_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0 group by point_id) maxscore on self.point_id=maxscore.point_id ");
    			sbSql.append("left join (select point_id,avg(score) avg_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0 group by point_id) avgscore on self.point_id=avgscore.point_id ");
    			ContentDAO dao = new ContentDAO(this.conn);
        		rs= dao.search(sbSql.toString());
        		while(rs.next()){
        			String point_id = rs.getString("point_id");
        			String pointname = (String)idnameMap.get(point_id);
        			if(pointname==null) {
                        pointname = "";
                    }
        			if("0".equals(drawtype))
        			{
        				String maxScore = rs.getString("max_score")==null?"0.0":rs.getString("max_score");
        				if(maxRadarScore < Double.parseDouble(maxScore)) {
                            maxRadarScore = Double.parseDouble(maxScore);
                        }
        				
        	     		CommonData M_data = new CommonData(rs.getString("max_score")==null?"0.0":rs.getString("max_score"),pointname);
        	    		CommonData A_data = new CommonData(rs.getString("avg_score")==null?"0.0":rs.getString("avg_score"),pointname);
        	    		CommonData S_data = new CommonData(rs.getString("self_score")==null?"0.0":rs.getString("self_score"),pointname);
            			maxList.add(M_data);
            			avgList.add(A_data);
            			selfList.add(S_data);
        			}
        			else
        			{
        				  BigDecimal self=new BigDecimal(rs.getString("self_score")==null?"0.0":rs.getString("self_score"));
        		    	  BigDecimal maxValue=new BigDecimal(rs.getString("max_score")==null?"1.0":rs.getString("max_score"));
        		    	  if(maxValue.compareTo(new BigDecimal("0"))==0) {
                              maxValue=new BigDecimal("1");
                          }
        		    	  String a_value= self.divide(maxValue,3,BigDecimal.ROUND_HALF_UP).toString();
        		    	  if(maxRadarScore < (Double.parseDouble(a_value)*100)) {
                              maxRadarScore = (Double.parseDouble(a_value)*100);
                          }
        		    	  CommonData s_data = new CommonData((Double.parseDouble(a_value)*100)+"",pointname);
        		    	  selfList.add(s_data);
        			}
        		}
    		}else{//如果不是按岗位素质模型测评
    			
    			if(pointList.size()<=0)
        		{
        			isHavePoint=false;
        			pointList=this.getTemplateItemList(planid);
        		}
    			
    			StringBuffer buf  = new StringBuffer();
        		StringBuffer point_buf = new StringBuffer();
        		StringBuffer self_buf = new StringBuffer();
        		StringBuffer slevel_buf = new StringBuffer();
    			for(int i=0;i<pointList.size();i++)
        		{
        			LazyDynaBean bean = (LazyDynaBean)pointList.get(i);
        			String point_id = (String)bean.get("point_id");
        			String type=(String)bean.get("type");
        			if(isHavePoint)
        			{
        				if("0".equals(type))
        				{
        	        		point_buf.append(" MAX(C_"+point_id+") as M_"+point_id+",");
        	        		point_buf.append(" AVG(C_"+point_id+") as A_"+point_id+",");
        	    	    	self_buf.append(" C_"+point_id+" as S_"+point_id+",");
        	    	    	slevel_buf.append(" AVG(C_"+point_id+") as SE_"+point_id+",");
        				}
        				else
        				{
        					point_buf.append(" MAX(T_"+point_id+") as M_"+point_id+",");
            	    		point_buf.append(" AVG(T_"+point_id+") as A_"+point_id+",");
            	    		self_buf.append(" T_"+point_id+" as S_"+point_id+",");
            	    		slevel_buf.append(" AVG(T_"+point_id+") as SE_"+point_id+",");
        				}
        			}
        			else
        			{
        				point_buf.append(" MAX(T_"+point_id+") as M_"+point_id+",");
        	    		point_buf.append(" AVG(T_"+point_id+") as A_"+point_id+",");
        	    		self_buf.append(" T_"+point_id+" as S_"+point_id+",");
        	    		slevel_buf.append(" AVG(T_"+point_id+") as SE_"+point_id+",");
        			}
        		}
    			
    			buf.append(" select ma.*,s.*,se.* from ");
        		buf.append(" (select "+point_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName+") ma left join (");
        		buf.append("select "+self_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName +" where object_id='"+object_id+"') s on ma.object_id = s.object_id");
        		buf.append(" left join (");
        		buf.append("select "+slevel_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName +" where 1=1 "+sameField+" ) se on ma.object_id = se.object_id");
        		
        		ContentDAO dao = new ContentDAO(this.conn);
        		
        		int pointNameLength=0;
        		for(int j=0;j<pointList.size();j++)
    			{
    				LazyDynaBean bean = (LazyDynaBean)pointList.get(j);
        			String point_id = (String)bean.get("point_id");
        			String pointname=((String)bean.get("pointname"));
        			if(pointname.length()>pointNameLength) {
                        pointNameLength=pointname.length();
                    }
    			}
        		
        		
        		rs= dao.search(buf.toString());
        		while(rs.next())
        		{
        			for(int j=0;j<pointList.size();j++)
        			{
        				LazyDynaBean bean = (LazyDynaBean)pointList.get(j);
            			String point_id = (String)bean.get("point_id");
            			String pointname=((String)bean.get("pointname"))/*.replaceAll("", "\r")*/;
            			if(this.userView!=null&&this.userView.getVersion()>=50) {
                            pointname=this.warpRowStr(pointname);
                        }
            	 		if(this.userView!=null&&this.userView.getVersion()<50&&pointNameLength>15) {
                            pointname=point_id;
                        }
            			if("0".equals(drawtype))
            			{
            				String maxScore = rs.getString("M_"+point_id)==null?"0.0":rs.getString("M_"+point_id);
            				if(maxRadarScore < Double.parseDouble(maxScore)) {
                                maxRadarScore = Double.parseDouble(maxScore);
                            }
            				
            	     		CommonData M_data = new CommonData(rs.getString("M_"+point_id)==null?"0.0":rs.getString("M_"+point_id),pointname);
            	    		CommonData A_data = new CommonData(rs.getString("A_"+point_id)==null?"0.0":rs.getString("A_"+point_id),pointname);
            	    		CommonData S_data = new CommonData(rs.getString("S_"+point_id)==null?"0.0":rs.getString("S_"+point_id),pointname);
            	    		CommonData SE_data = new CommonData(rs.getString("SE_"+point_id)==null?"0.0":rs.getString("SE_"+point_id),pointname);
                			maxList.add(M_data);
                			avgList.add(A_data);
                			selfList.add(S_data);
                			sleveList.add(SE_data);//统计人群得分分布图 用到这个list
            			}
            			else
            			{
            				  BigDecimal self=new BigDecimal(rs.getString("S_"+point_id)==null?"0.0":rs.getString("S_"+point_id));
            		    	  BigDecimal maxValue=new BigDecimal(rs.getString("M_"+point_id)==null?"1.0":rs.getString("M_"+point_id));
            		    	  if(maxValue.compareTo(new BigDecimal("0"))==0) {
                                  maxValue=new BigDecimal("1");
                              }
            		    	  String a_value= self.divide(maxValue,3,BigDecimal.ROUND_HALF_UP).toString();
            		    	  if(maxRadarScore < (Double.parseDouble(a_value)*100)) {
                                  maxRadarScore = (Double.parseDouble(a_value)*100);
                              }
            		    	  CommonData s_data = new CommonData((Double.parseDouble(a_value)*100)+"",pointname);
            		    	  selfList.add(s_data);
            			}
        			}
        		} //while结束
        		
    		}
    		
    		//数据准备为完毕，开始画图
    		if("0".equals(drawtype))//如果按分值画图
    		{
    			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selffraction")+",")!=-1
    					|| (","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selfdepartscore")+",")!=-1){
    				if("3".equals(model)){//如果是团队
    					map.put(ResourceFactory.getProperty("lable.examine.selfdepartscore"), selfList);
        			}else{
        				map.put(ResourceFactory.getProperty("lable.examine.selffraction"), selfList);
        			}
    			}
    			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.maxfraction")+",")!=-1) {
                    map.put(ResourceFactory.getProperty("lable.examine.maxfraction"),maxList);
                }
    			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.argfraction")+",")!=-1) {
                    map.put(ResourceFactory.getProperty("lable.examine.argfraction"), avgList);
                }
    			if(sameField!=null && sameField.trim().length()>0)
    			{
	    			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore")+",")!=-1) {
                        map.put(ResourceFactory.getProperty("lable.examine.sameLevelAvgScore"), sleveList);
                    }
    			}
    		}
    		else//如果按得分率画图
    		{
    			map.put(ResourceFactory.getProperty("lable.examine.selffractionlv"), selfList);
    		}
    		
    		if("41".equals(chart_type)) {
                map.put("minmax",0+","+maxRadarScore);
            }
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return map;
    }
    
    
    
    public String warpRowStr(String name)
	 {
		 if(name.length()>12)
			{
				int div = name.length()/12;
				String temp = "";
				for(int index=0;index<div;index++)
				{
					temp+=name.substring(index*12, (index+1)*12)+"\r";
				}
				temp+=name.substring(div*12);
				name=temp;
			}
		 return name;
	 }
    public ArrayList getFiguresList(String planid,String object_id,ArrayList pointList,String drawtype,String selectids,String sameField)
    {
    	ArrayList list = new ArrayList();
    	RowSet rowSet = null;
    	RowSet rs  =null;
    	try
    	{
    		String tableName = "per_result_"+planid;
    		Table table = new Table(tableName);
    		boolean isHavePoint=true;
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(!dbWizard.isExistTable(table.getName(),false))
			{
				return list;
			}
    		if(modelOnly){//按岗位素质模型测评
    			int flag=1;
    			if(isByModel){
    				flag = 2;
    			}
    			HashMap idnameMap = this.getPointIdNameMap(planid,flag);
    			StringBuffer sbSql = new StringBuffer();
    			sbSql.append("select self.point_id,self.self_score,maxscore.max_score,avgscore.avg_score from "); 
    			sbSql.append("(select point_id,score self_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0) self ");
    			sbSql.append("left join (select point_id,max(score) max_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0 group by point_id) maxscore on self.point_id=maxscore.point_id ");
    			sbSql.append("left join (select point_id,avg(score) avg_score from per_history_result where plan_id="+planid+" and object_id='"+object_id+"' and status=0 group by point_id) avgscore on self.point_id=avgscore.point_id ");
    			ContentDAO dao = new ContentDAO(this.conn);
        		rs= dao.search(sbSql.toString());
        		
    			ArrayList tempList = new ArrayList();
    			while(rs.next()){
    				tempList = new ArrayList();
    				String point_id = rs.getString("point_id");
        			String pointname = (String)idnameMap.get(point_id);
        			if(pointname==null) {
                        pointname = "";
                    }
        			LazyDynaBean abean=new LazyDynaBean();
        			if("0".equals(drawtype))
        			{
        				CommonData M_data = new CommonData(rs.getString("max_score")==null?"0.0":rs.getString("max_score"),ResourceFactory.getProperty("lable.examine.maxfraction"));
        	    		CommonData A_data = new CommonData(rs.getString("avg_score")==null?"0.0":rs.getString("avg_score"),ResourceFactory.getProperty("lable.examine.argfraction"));
        	    		CommonData S_data = new CommonData(rs.getString("self_score")==null?"0.0":rs.getString("self_score"),ResourceFactory.getProperty("lable.examine.selffraction"));
        	    		
        	    		if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selffraction")+",")!=-1) {
                            tempList.add(S_data);
                        }
            			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.maxfraction")+",")!=-1) {
                            tempList.add(M_data);
                        }
            			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.argfraction")+",")!=-1) {
                            tempList.add(A_data);
                        }
            			abean.set("categoryName", pointname/*.replaceAll("", "\r")*/);
       				    abean.set("dataList",tempList);
       				    list.add(abean);
        			}
        			else
        			{
        				  BigDecimal self=new BigDecimal(rs.getString("self_score")==null?"0.0":rs.getString("self_score"));
        		    	  BigDecimal maxValue=new BigDecimal(rs.getString("self_score")==null?"1.0":rs.getString("self_score"));
        		    	  if(maxValue.compareTo(new BigDecimal("0"))==0) {
                              maxValue=new BigDecimal("1");
                          }
        		    	  String a_value= self.divide(maxValue,3,BigDecimal.ROUND_HALF_UP).toString();
        		    	  CommonData s_data = new CommonData((Double.parseDouble(a_value)*100)+"",ResourceFactory.getProperty("lable.examine.selffraction"));
        		    	  tempList.add(s_data);
        		    	  abean.set("categoryName", pointname/*.replaceAll("", "\r")*/);
         				  abean.set("dataList",tempList);
         				  list.add(abean);
        		    	  
        			}
    			} //while结束
    			
    			
			
    		}else{
    			
    			if(pointList.size()<=0)
        		{
        			isHavePoint=false;
        			pointList=this.getTemplateItemList(planid);
        		}
    			StringBuffer buf  = new StringBuffer();
        		StringBuffer point_buf = new StringBuffer();
        		StringBuffer self_buf = new StringBuffer();
        		StringBuffer slevel_buf = new StringBuffer();
        		for(int i=0;i<pointList.size();i++)
        		{
        			LazyDynaBean bean = (LazyDynaBean)pointList.get(i);
        			String point_id = (String)bean.get("point_id");
        			String type=(String)bean.get("type");
        			if(isHavePoint)
        			{
        				if("0".equals(type))
        				{
        	        		point_buf.append(" MAX(C_"+point_id+") as M_"+point_id+",");
        	         		point_buf.append(" AVG(C_"+point_id+") as A_"+point_id+",");
        	         		self_buf.append(" C_"+point_id+" as S_"+point_id+",");
        	         		slevel_buf.append(" AVG(C_"+point_id+") as SE_"+point_id+",");
        				}
        				else
        				{
        					point_buf.append(" MAX(T_"+point_id+") as M_"+point_id+",");
            	    		point_buf.append(" AVG(T_"+point_id+") as A_"+point_id+",");
            	    		self_buf.append(" T_"+point_id+" as S_"+point_id+",");
            	    		slevel_buf.append(" AVG(T_"+point_id+") as SE_"+point_id+",");
        				}
        			}
        			else
        			{
        				point_buf.append(" MAX(T_"+point_id+") as M_"+point_id+",");
        	    		point_buf.append(" AVG(T_"+point_id+") as A_"+point_id+",");
        	    		self_buf.append(" T_"+point_id+" as S_"+point_id+",");
        	    		slevel_buf.append(" AVG(T_"+point_id+") as SE_"+point_id+",");
        			}
        		}
        		buf.append(" select ma.*,s.*,se.* from ");
        		buf.append(" (select "+point_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName+") ma left join (");
        		buf.append("select "+self_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName +" where object_id='"+object_id+"') s on ma.object_id = s.object_id");
        		buf.append(" left join (");
        		buf.append("select "+slevel_buf.toString()+"'"+object_id+"' as object_id from ");
        		buf.append(tableName +" where 1=1 "+sameField+" ) se on ma.object_id = se.object_id");
        		//准备sql语句结束。开始执行查询
        		ContentDAO dao = new ContentDAO(this.conn);
        		rs= dao.search(buf.toString());
        		ArrayList tempList = new ArrayList();
        		
        		int pointNameLength=0;
        		for(int j=0;j<pointList.size();j++)
    			{
    				LazyDynaBean bean = (LazyDynaBean)pointList.get(j);
        			String point_id = (String)bean.get("point_id");
        			String pointname=((String)bean.get("pointname"));
        			if(pointname.length()>pointNameLength) {
                        pointNameLength=pointname.length();
                    }
    			}
        		
        		
        		while(rs.next())
        		{
        			
        			for(int j=0;j<pointList.size();j++)
        			{
        				LazyDynaBean bean = (LazyDynaBean)pointList.get(j);
            			String point_id = (String)bean.get("point_id");
            			String pointname=((String)bean.get("pointname"));
            			if(this.userView!=null&&this.userView.getVersion()>=50) {
                            pointname=this.warpRowStr(pointname);
                        }
            			if(this.userView!=null&&this.userView.getVersion()<50&&pointNameLength>15) {
                            pointname=point_id;
                        }
            			
            		    tempList = new ArrayList();
            		    LazyDynaBean abean=new LazyDynaBean();
            			if("0".equals(drawtype))
            			{
            	     		CommonData M_data = new CommonData(rs.getString("M_"+point_id)==null?"0.0":rs.getString("M_"+point_id),ResourceFactory.getProperty("lable.examine.maxfraction"));
            	    		CommonData A_data = new CommonData(rs.getString("A_"+point_id)==null?"0.0":rs.getString("A_"+point_id),ResourceFactory.getProperty("lable.examine.argfraction"));
            	    		CommonData S_data = new CommonData(rs.getString("S_"+point_id)==null?"0.0":rs.getString("S_"+point_id),ResourceFactory.getProperty("lable.examine.selffraction"));
            	    		CommonData SE_data = new CommonData(rs.getString("SE_"+point_id)==null?"0.0":rs.getString("SE_"+point_id),ResourceFactory.getProperty("lable.examine.sameLevelAvgScore"));
            	    		
            	    		if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.selffraction")+",")!=-1) {
                                tempList.add(S_data);
                            }
                			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.maxfraction")+",")!=-1) {
                                tempList.add(M_data);
                            }
                			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.argfraction")+",")!=-1) {
                                tempList.add(A_data);
                            }
                			if(sameField!=null && sameField.trim().length()>0)
                			{
            	    			if("null".equals(selectids)||(","+selectids+",").indexOf(","+ResourceFactory.getProperty("lable.examine.sameLevelAvgScore")+",")!=-1) {
                                    tempList.add(SE_data);
                                }
                			}        	    		
                			abean.set("categoryName", pointname/*.replaceAll("", "\r")*/);
           				    abean.set("dataList",tempList);
           				    list.add(abean);
            			}
            			else
            			{
            				  BigDecimal self=new BigDecimal(rs.getString("S_"+point_id)==null?"0.0":rs.getString("S_"+point_id));
            		    	  BigDecimal maxValue=new BigDecimal(rs.getString("M_"+point_id)==null?"1.0":rs.getString("M_"+point_id));
            		    	  if(maxValue.compareTo(new BigDecimal("0"))==0) {
                                  maxValue=new BigDecimal("1");
                              }
            		    	  String a_value= self.divide(maxValue,3,BigDecimal.ROUND_HALF_UP).toString();
            		    	  CommonData s_data = new CommonData((Double.parseDouble(a_value)*100)+"",ResourceFactory.getProperty("lable.examine.selffraction"));
            		    	  tempList.add(s_data);
            		    	  abean.set("categoryName", pointname/*.replaceAll("", "\r")*/);
             				  abean.set("dataList",tempList);
             				  list.add(abean);
            		    	  
            			}
        			} //for结束
        		} //while结束
    		}
	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    			if(rowSet!=null) {
                    rowSet.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 取得考核对象基本信息以及考核项目和考核要素对应的分数
     * @param object_id 考核对象
     * @param plan_id 考核计划
     * @return HashMap
     */
    public HashMap getPersonalInformation(String object_id,String plan_id)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		/**考核要素列表 要素id对应分数*/
    		HashMap pointMap = new HashMap();
    		/**考核项目列表 项目id对应分数*/
    		HashMap itemMap = new HashMap();
    		/**考核对象基本信息*/
    		LazyDynaBean bean = new LazyDynaBean();
    		sql.append("select * from per_result_"+plan_id);
    		sql.append(" where object_id='"+object_id+"'");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(sql.toString());
    		ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			LoadXml parameter_content = null;
		    if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
						
		        parameter_content = new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String kd = "2";
			if( params.get("KeepDecimal")!=null)
			{
				kd=(String) params.get("KeepDecimal");
			}
			int KeepDecimal = Integer.parseInt(kd); // 保留小数位
			String deviationScoreUsed=(String) params.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是  zzk 
			String total_score="score";
			if("1".equals(deviationScoreUsed)){
				total_score="reviseScore";
			}
    		while(rs.next())
    		{
    			bean.set("b0110",rs.getString("B0110")==null?"":AdminCode.getCodeName("UN",rs.getString("B0110")));
    			bean.set("e0122",rs.getString("E0122")==null?"":AdminCode.getCodeName("UM",rs.getString("E0122")));
    			bean.set("a0101",rs.getString("A0101")==null?"":rs.getString("A0101"));
    			bean.set("score",round(PubFunc.NullToZero(rs.getString(total_score)),KeepDecimal));
    			bean.set("resultdesc",rs.getString("resultdesc")==null?"":rs.getString("resultdesc"));
    			for (int j = 0; j < columnCount; j++) {
    				String conlumnName = rsmd.getColumnLabel(j + 1).toString();
    				if ((conlumnName.startsWith("C") && "_".equals(conlumnName.substring(1, 2)))|| (conlumnName.startsWith("c") && "_".equals(conlumnName.substring(1, 2))))
    				{
    					String key = conlumnName.substring(2);
    					if (!pointMap.containsKey(key.toUpperCase())) 
    					{
    						pointMap.put(key.toUpperCase(),round(PubFunc.NullToZero(rs.getString(conlumnName)),KeepDecimal));
    					}
    				}

    				if ((conlumnName.startsWith("T") && "_".equals(conlumnName.substring(1, 2)))|| (conlumnName.startsWith("t") && "_".equals(conlumnName.substring(1, 2))))
    				{
    					String key = conlumnName.substring(2);
    					if (!itemMap.containsKey(key)) {
    						itemMap.put(key,round(PubFunc.NullToZero(rs.getString(conlumnName)),KeepDecimal)+ResourceFactory.getProperty("lable.performance.score"));
    					}
    				}
    			}
    		}
    		map.put("1",bean);
    		map.put("2",itemMap);
    		map.put("3",pointMap);
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return map;
    }
    /**
     * 按岗位素质模型取得考核对象基本信息以及考核项目和考核要素对应的分数
     * @param object_id 考核对象
     * @param plan_id 考核计划
     * @return HashMap 除了要素分 项目分 不能从per_result_xxx中取  其他都行
     * 按岗位素质模型  
     */
    public HashMap getPersonalInformationByModel(String object_id,String plan_id)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		/**考核要素列表 要素id对应分数*/
    		HashMap pointMap = new HashMap();
    		/**考核项目列表 项目id对应分数*/
    		HashMap itemMap = new HashMap();
    		/**考核对象基本信息*/
    		LazyDynaBean bean = new LazyDynaBean();
    		sql.append("select * from per_result_"+plan_id);
    		sql.append(" where object_id='"+object_id+"'");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(sql.toString());
			LoadXml parameter_content = null;
		    if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
						
		        parameter_content = new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String kd = "2";
			if( params.get("KeepDecimal")!=null)
			{
				kd=(String) params.get("KeepDecimal");
			}
			int KeepDecimal = Integer.parseInt(kd); // 保留小数位
    		while(rs.next())
    		{
    			bean.set("b0110",rs.getString("B0110")==null?"":AdminCode.getCodeName("UN",rs.getString("B0110")));
    			bean.set("e0122",rs.getString("E0122")==null?"":AdminCode.getCodeName("UM",rs.getString("E0122")));
    			bean.set("a0101",rs.getString("A0101")==null?"":rs.getString("A0101"));
    			bean.set("score",round(PubFunc.NullToZero(rs.getString("score")),KeepDecimal));
    			bean.set("resultdesc",rs.getString("resultdesc")==null?"":rs.getString("resultdesc"));

    		}
    		String ssl="select  * from per_history_result where object_id='"+object_id+"' and plan_id='"+plan_id+"'";
    		rs = dao.search(ssl);
    		String status="";//0要素 1项目 2总分
    		String point_id="";//指标要素/项目要素号
    		String score="";//得分
    		while(rs.next()){
    			status=rs.getString("status");
    			point_id=rs.getString("point_id");
    			score=rs.getString("score");
    			if("0".equals(status)){
					if (!pointMap.containsKey(point_id.toUpperCase())) 
					{
						pointMap.put(point_id.toUpperCase(),round(PubFunc.NullToZero(score),KeepDecimal));
					}
    			}else if("1".equals(status)){
					if (!itemMap.containsKey(point_id)) {
						itemMap.put(point_id,round(PubFunc.NullToZero(score),KeepDecimal)+ResourceFactory.getProperty("lable.performance.score"));
					}
    			}
    			
    		}
    		map.put("1",bean);
    		map.put("2",itemMap);
    		map.put("3",pointMap);
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return map;
    }
    /**
     * 得测评说明信息
     * @param object_id 考核对象
     * @param plan_id 考核计划
     * @return
     */
    public ArrayList getEvaluationDescription(String object_id,String plan_id)
    {
    	ArrayList list =new ArrayList();
    	RowSet rs=null;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select c.*,d.name from (");
    		buf.append("select a.*,b.S_total from (");
    		buf.append("select count(body_id) as B_total,body_id,plan_id from per_mainbody  where ");
    		buf.append("plan_id="+plan_id+" and object_id='"+object_id+"' group by body_id,plan_id) a left join ");
    		buf.append("( select SUM(score*point_rank) as S_total,body_id from Per_ScoreDetail where ");
    		buf.append(" plan_id="+plan_id+" and object_id='"+object_id+"' group by body_id ) b");
    		buf.append(" on a.body_id=b.body_id) c,per_mainbodyset d where c.body_id=d.body_id");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
    		LoadXml parameter_content = null;
		    if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
						
		        parameter_content = new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String kd = "2";
			if( params.get("KeepDecimal")!=null)
			{
				kd=(String) params.get("KeepDecimal");
			}
			int KeepDecimal = Integer.parseInt(kd); // 保留小数位
    		int count=0;
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("bodyname",rs.getString("name"));
    			bean.set("count",rs.getInt("B_total")+"");
    			bean.set("score",round(PubFunc.NullToZero(rs.getString("S_total")),KeepDecimal));
    			count+=rs.getInt("B_total");
    			list.add(bean);
    		}
    		LazyDynaBean bean = new LazyDynaBean();
    		bean.set("bodyname",ResourceFactory.getProperty("lable.statistic.thisgradenum"));
    		bean.set("count", count+"");
    		bean.set("score","");
    		list.add(0,bean);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 取得考核项目，考核要素信息列表
     * @param object_id 考核对象
     * @param plan_id 考核计划
     * @param itemMap 封装考核项目对应分数
     * @param pointMap 封装考核要素对应分数
     * @return
     */
    public ArrayList getItemList(String object_id,String plan_id,HashMap itemMap,HashMap pointMap,String gatiShowDegree,String limitrule)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		sql.append(" select T.*,I.itemdesc from ");
    		sql.append(" (select per_point.point_id,per_point.pointkind,per_template_point.score,per_point.pointname,per_template_point.item_id ");
    		sql.append(" from per_template_point,per_point where per_point.point_id=per_template_point.point_id) T,");
    		sql.append(" (select item_id,itemdesc from per_template_item where template_id=(select template_id ");
    		sql.append(" from per_plan where plan_id="+plan_id+")) I where I.item_id=T.item_id order by T.item_id,T.point_id");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs= dao.search(sql.toString());
    		int i=0;
    		String a_itemid="";
    		String itemdesc="";
    		LazyDynaBean bean=null;
    		LazyDynaBean a_bean=null;
    		ArrayList a_list = new ArrayList();
    		while(rs.next())
    		{
    			if(i==0)
    			{
    				a_itemid=rs.getString("item_id");
    				itemdesc=rs.getString("itemdesc");
    			}
    			if(!a_itemid.equalsIgnoreCase(rs.getString("item_id")))
    			{
    				 bean = new LazyDynaBean();
    				 bean.set("itemname",itemdesc);
    				 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"0");
    				 bean.set("sublist",a_list);
    				 list.add(bean);
    				 a_list = new ArrayList();
    				 a_itemid=rs.getString("item_id");
    				 itemdesc=rs.getString("itemdesc");
    			}
    			a_bean = new LazyDynaBean();
    			String point_id = rs.getString("point_id");
    			a_bean.set("pointname",rs.getString("pointname"));
    			/**分值显示成标度,要分定性和定量指标*/
    			if(gatiShowDegree!=null&&!"".equals(gatiShowDegree)&& "true".equalsIgnoreCase(gatiShowDegree))
    			{
    				String pointkind=rs.getString("pointkind");
    				if("0".equals(pointkind))
    				{
    					String pointDscore=rs.getString("score");
    					String pointscore=(pointMap.get(point_id.toUpperCase())!=null&&!"".equals((String)pointMap.get(point_id.toUpperCase())))?(String)pointMap.get(point_id.toUpperCase()).toString():"0";
    		    		String desc=this.getGradeDesc(point_id, pointscore, dao,pointDscore,limitrule);
    		    		a_bean.set("score",desc);
    				}
    				else
    				{
    					//limitrule// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
    		    		String pointscore=(pointMap.get(point_id.toUpperCase())!=null&&!"".equals((String)pointMap.get(point_id.toUpperCase())))?(String)pointMap.get(point_id.toUpperCase()).toString():"0";
    		    		String desc=this.getGradeDesc(point_id, pointscore, dao,limitrule);
    		    		a_bean.set("score",desc);
    				}
    			}
    			else
    			{
    	    		a_bean.set("score",(pointMap.get(point_id.toUpperCase())!=null?pointMap.get(point_id.toUpperCase()).toString():"0")+ResourceFactory.getProperty("lable.performance.score"));
    			}
    			a_list.add(a_bean);
    			i++;
    			
    		}
    		if(i>0)
    		{
    			 bean = new LazyDynaBean();
				 bean.set("itemname",itemdesc);
				 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"0");
				 bean.set("sublist",a_list);
				 list.add(bean);
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }

/**
 * 按岗位素质模型 取得考核项目，考核要素信息列表
 * @param object_id 考核对象
 * @param plan_id 考核计划
 * @param itemMap 封装考核项目对应分数
 * @param pointMap 封装考核要素对应分数
 * @return
 */
public ArrayList getItemListByModel(String object_id,String plan_id,HashMap itemMap,HashMap pointMap,String gatiShowDegree,String limitrule)
{
	ArrayList list = new ArrayList();
	RowSet rs = null;
	String k_object_id="";//对象所在岗位
	try
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String str="select object_id from per_competency_modal where object_id =(select e01a1 from usra01 where a0100='"+object_id+"') and object_type='3'";
		rs=dao.search(str);
		if(rs.next()){
			k_object_id=rs.getString("object_id");
		}
		/**取指标分类及对应指标**/
		StringBuffer sql = new StringBuffer();
		if(k_object_id!=null&&!"".equals(k_object_id)){//定义了岗位素质指标
			sql.append(" select T.*,I.itemdesc from ");
			sql.append(" (select per_point.point_id,per_point.pointkind,per_competency_modal.score,per_point.pointname,per_competency_modal.point_type item_id");
			sql.append(" from per_competency_modal,per_point where per_point.point_id=per_competency_modal.point_id ");
			sql.append("  and  per_competency_modal.object_id='"+k_object_id+"' and per_competency_modal.point_id in (select  point_id from per_history_result where object_id='"+object_id+"' and plan_id='"+plan_id+"' and status='0' ) ) T ");
			sql.append(" left join  ( select codeitemid item_id,codeitemdesc itemdesc from codeitem where CodeSetId='70'  ");
			sql.append(" ) I on I.item_id=T.item_id order by T.item_id desc,T.point_id");
		}else{//未定义岗位素质指标
    		sql.append(" select T.*,I.itemdesc from ");
    		sql.append(" (select per_point.point_id,per_point.pointkind,per_template_point.score,per_point.pointname,per_template_point.item_id ");
    		sql.append(" from per_template_point,per_point where per_point.point_id=per_template_point.point_id) T,");
    		sql.append(" (select item_id,itemdesc from per_template_item where template_id=(select template_id ");
    		sql.append(" from per_plan where plan_id="+plan_id+")) I where I.item_id=T.item_id order by T.item_id,T.point_id");
		}

		rs= dao.search(sql.toString());
		int i=0;
		String a_itemid="";
		String itemdesc="";
		LazyDynaBean bean=null;
		LazyDynaBean a_bean=null;
		ArrayList a_list = new ArrayList();
		while(rs.next())
		{
			// 如果指标分类为空，则说明这个人不需要该分类，直接略过  chent 20160921 start
			if(rs.getString("item_id")==null){
				continue;
			}
			// 如果指标分类为空，则说明这个人不需要该分类，直接略过  chent 20160921 end
			
			if(i==0)
			{
				a_itemid=rs.getString("item_id")==null?"":rs.getString("item_id");
				itemdesc=rs.getString("itemdesc")==null?"":rs.getString("itemdesc");
			}
			if(!a_itemid.equalsIgnoreCase(rs.getString("item_id")))
			{
				 bean = new LazyDynaBean();
				 bean.set("itemname",itemdesc);
				 if("".equals(itemdesc)){
					 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"");
				 }else{
					 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"0");
				 }
				 bean.set("sublist",a_list);
				 list.add(bean);
				 a_list = new ArrayList();
				 a_itemid=rs.getString("item_id")==null?"":rs.getString("item_id");
				 itemdesc=rs.getString("itemdesc")==null?"":rs.getString("itemdesc");
			}
			a_bean = new LazyDynaBean();
			String point_id = rs.getString("point_id");
			a_bean.set("pointname",rs.getString("pointname"));
			/**分值显示成标度,要分定性和定量指标*/
			if(gatiShowDegree!=null&&!"".equals(gatiShowDegree)&& "true".equalsIgnoreCase(gatiShowDegree))
			{
				String pointkind=rs.getString("pointkind");
				if("0".equals(pointkind))
				{
					String pointDscore=rs.getString("score");
					String pointscore=(pointMap.get(point_id.toUpperCase())!=null&&!"".equals((String)pointMap.get(point_id.toUpperCase())))?(String)pointMap.get(point_id.toUpperCase()).toString():"0";
		    		String desc=this.getGradeDesc(point_id, pointscore, dao,pointDscore,limitrule);
		    		a_bean.set("score",desc);
				}
				else
				{
					//limitrule// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
		    		String pointscore=(pointMap.get(point_id.toUpperCase())!=null&&!"".equals((String)pointMap.get(point_id.toUpperCase())))?(String)pointMap.get(point_id.toUpperCase()).toString():"0";
		    		String desc=this.getGradeDesc(point_id, pointscore, dao,limitrule);
		    		a_bean.set("score",desc);
				}
			}
			else
			{
	    		a_bean.set("score",(pointMap.get(point_id.toUpperCase())!=null?pointMap.get(point_id.toUpperCase()).toString():"0")+ResourceFactory.getProperty("lable.performance.score"));
			}
			a_list.add(a_bean);
			i++;
			
		}
		if(i>0)
		{
			 bean = new LazyDynaBean();
			 bean.set("itemname",itemdesc);
			 if("".equals(itemdesc)){
				 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"");
			 }else{
				 bean.set("score",itemMap.get(a_itemid)!=null?itemMap.get(a_itemid).toString():"0");
			 }
			 bean.set("sublist",a_list);
			 list.add(bean);
		}
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}finally{
		try
		{
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	return list;
}
    public String getGradeDesc(String itemid,String score,ContentDAO dao,String limitrule)
    {
    	String itemdesc="";
    	RowSet rs=null;
    	try
    	{
    		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(ppo.getComOrPer(itemid,"poi")) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
    		//limitrule// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
    		String sql="select a.gradedesc from per_grade b,"+per_comTable+" a where UPPER(b.point_id)='"+itemid.toUpperCase()+"' and "+score+"<=b.top_value and "+score+">=b.bottom_value and a.grade_template_id=b.gradecode ";
    		if(limitrule!=null&&!"".equals(limitrule)&& "2".equals(limitrule))
    		{
    			sql+=" order by a.top_value ";
    		}
    		else
    		{
    			sql+=" order by a.top_value desc ";
    		}
    		rs=dao.search(sql);
    		while(rs.next())
    		{
    			itemdesc=rs.getString("gradedesc");
    			break;
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return itemdesc;
    }
    
    public String getGradeDesc(String itemid,String score,ContentDAO dao,String pointscore,String limitrule)
    {
    	String itemdesc="";
    	RowSet rs=null;
    	try
    	{
    		PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(ppo.getComOrPer(itemid,"poi")) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
    		//limitrule// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
    		String sql="select a.gradedesc from per_grade b,"+per_comTable+" a where UPPER(b.point_id)='"+itemid.toUpperCase()+"' and "+score+"<=(b.top_value*"+pointscore+") and "+score+">=(b.bottom_value*"+pointscore+") and a.grade_template_id=b.gradecode ";
    		if(limitrule!=null&&!"".equals(limitrule)&& "2".equals(limitrule))
    		{
    			sql+=" order by a.top_value ";
    		}
    		else
    		{
    			sql+=" order by a.top_value desc  ";
    		}
    		
    		
    		rs=dao.search(sql);
    		while(rs.next())
    		{
    			itemdesc=rs.getString("gradedesc");
    			break;
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return itemdesc;
    }
    /**
     * 取总体评价信息
     * @param object_id
     * @param plan_id
     * @return
     */
    public ArrayList getOverallRating(String object_id,String plan_id)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs =null;
    	try
    	{
    		Hashtable htxml = new Hashtable();
    		LoadXml loadxml = new LoadXml(this.conn, plan_id);
    		htxml = loadxml.getDegreeWhole();
    		/********优先按计划参数总体评价等级分类**********/
    		String gradeId = htxml.get("EvalClass").toString();//计划参数总体评价等级分类
    		if(gradeId==null||gradeId.trim().length()==0|| "0".equals(gradeId)){
    			gradeId = htxml.get("GradeClass").toString();//启动计划等级分类
    		}
    		if(gradeId==null|| "".equals(gradeId)) {
                return list;
            }
    		StringBuffer buf = new StringBuffer();
    		buf.append("select id,itemname from per_degreedesc where degree_id="+gradeId+" order by id");
    		ArrayList dgreeList = new ArrayList();
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs= dao.search(buf.toString());
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("id",rs.getString("id"));
    			bean.set("itemname", rs.getString("itemname"));
    			dgreeList.add(bean);
    		}
    		buf.setLength(0);
    		buf.append("select count(*) as total from per_mainbody where object_id='"+object_id+"' and plan_id="+plan_id);
    		buf.append(" and whole_grade_id is not null ");
    		rs = dao.search(buf.toString());
    		String total = "0";
    		while(rs.next())
    		{
    			total = rs.getString("total");
    		}
    		/*if(total.equals("0"))
    			return list;*/
    		buf.setLength(0);
    		buf.append("select count(whole_grade_id) as wgcount,whole_grade_id from per_mainbody where object_id='");
			buf.append(object_id);
			buf.append("' and plan_id="+plan_id+" and whole_grade_id is not null group by whole_grade_id");
			rs = dao.search(buf.toString());
			/**封装了解程度与票数*/
			HashMap gradehm = new HashMap();
			while(rs.next())
			{
				gradehm.put(rs.getString("whole_grade_id"), PubFunc.NullToZero(rs.getString("wgcount")));
			}
			for(int i=0;i<dgreeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)dgreeList.get(i);
				String gradeid = (String)bean.get("id");
				String itemname= (String)bean.get("itemname");
				String vote ="0";
				if(gradehm.get(gradeid)!=null)
				{
					vote=(String)gradehm.get(gradeid);
				}
				String percent="0%";
				if(!"0".equals(vote))
				{
					percent=getPercent(vote,Integer.parseInt(total));
				}
				LazyDynaBean a_bean = new LazyDynaBean();
				a_bean.set("itemname",itemname);
				a_bean.set("vote",vote);
				a_bean.set("percent",percent);
				list.add(a_bean);	
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
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
    	return list;
    }
    /**
     * 取得总体评价的详细信息列表
     * @param object_id
     * @param plan_id
     * @return
     */
    public ArrayList getOverallRatingDetail(String object_id,String plan_id)
    {
    	RowSet rs=null;
    	ArrayList list = new ArrayList();
    	try
    	{
        	StringBuffer buf = new StringBuffer();
        	ContentDAO dao = new ContentDAO(this.conn);
        	Hashtable htxml = new Hashtable();
    		LoadXml loadxml = new LoadXml(this.conn, plan_id);
    		htxml = loadxml.getDegreeWhole();
    		/********优先按计划参数总体评价等级分类**********/
    		String gradeId = htxml.get("EvalClass").toString();//计划参数总体评价等级分类
    		if(gradeId==null||gradeId.trim().length()==0|| "0".equals(gradeId.trim())){
    			gradeId = htxml.get("GradeClass").toString();//启动计划等级分类
    		}
    		if(gradeId==null|| "".equals(gradeId)) {
                return list;
            }
    		buf.append("select id,itemname from per_degreedesc where degree_id="+gradeId+" order by id");
    		ArrayList dgreeList = new ArrayList();
    	    rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("id",rs.getString("id"));
    			bean.set("itemname", rs.getString("itemname"));
    			dgreeList.add(bean);
    		}
    		buf.setLength(0);
	    	buf.append(" select count(body_id) as B_total,body_id,whole_grade_id from per_mainbody");
	    	buf.append(" where plan_id="+plan_id+" and object_id='"+object_id+"' group by body_id,whole_grade_id");
	    	rs=dao.search(buf.toString()); 
	    	HashMap map =new HashMap();
	    	HashMap bodyMap = new HashMap();
	    	ArrayList bodyList =new ArrayList();
	    	int total=0;
	    	HashMap countMap = new HashMap();
	    	StringBuffer body_id_buf= new StringBuffer("");
	    	while(rs.next())
	    	{
			    total+=rs.getInt("B_total");
			    if(countMap.get(rs.getString("body_id"))==null)
			    {
			    	countMap.put(rs.getString("body_id"), rs.getString("B_total"));
			    }
			    else
			    {
			    	String ff=(String)countMap.get(rs.getString("body_id"));
			    	int ii=Integer.parseInt(ff)+rs.getInt("B_total");
			    	countMap.put(rs.getString("body_id"), ii+"");
			    }
			    if(!bodyList.contains((rs.getString("body_id"))))
			    {
			    	bodyList.add(rs.getString("body_id"));
			    	body_id_buf.append(","+rs.getString("body_id"));
			    }
			    map.put(rs.getString("body_id")+rs.getString("whole_grade_id"), rs.getString("B_total"));
	    	}
	    	buf.setLength(0);
	    	if(body_id_buf!=null&&!"".equals(body_id_buf.toString()))
	    	{
	        	buf.append("select body_id,name from per_mainbodyset where body_id in("+body_id_buf.substring(1)+")");
	        	rs=dao.search(buf.toString());
	        	while(rs.next())
	        	{
	        		bodyMap.put(rs.getString("body_id").toUpperCase(),rs.getString("name"));
	        	}
	    	}
	    	for(int i=0;i<bodyList.size();i++)
	    	{
	    		LazyDynaBean bean = new LazyDynaBean();
	    		String bodyid=(String)bodyList.get(i);
	    		String bodyTotal="0";
	    		if(countMap.get(bodyid)!=null) {
                    bodyTotal=(String)countMap.get(bodyid);
                }
	    		bean.set("bodyname",(String)bodyMap.get(bodyid.toUpperCase()));
	    		ArrayList subList = new ArrayList();
	    		for(int j=0;j<dgreeList.size();j++)
	    		{
	    			LazyDynaBean subbean=new LazyDynaBean();
	    			LazyDynaBean dgreebean=(LazyDynaBean)dgreeList.get(j);
	    			String gradeid = (String)dgreebean.get("id");
					String itemname= (String)dgreebean.get("itemname");
					subbean.set("itemname", itemname);
					String vote="0";
					if(map.get(bodyid+gradeid)!=null) {
                        vote=(String)map.get(bodyid+gradeid);
                    }
					subbean.set("vote",vote);
					String percent="0%";
					if(!"0".equals(vote))
					{
						percent=getPercent(vote,Integer.parseInt(bodyTotal));
					}
					subbean.set("vote",vote);
					subbean.set("percent",percent);
					subList.add(subbean);
	    		}
	    		bean.set("sublist", subList);
	    	    list.add(bean);
	    	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 取百分数
     * @param userCount
     * @param totalCount
     * @return
     */
    public String getPercent(String userCount, int totalCount) {
		double perdbl = 0;
		double userCountdbl = Double.parseDouble(userCount);
		double toaldbl = (double) totalCount;
		perdbl = userCountdbl / toaldbl;
		perdbl = perdbl * 100;
		DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
		String strPer = df.format(perdbl) ;
		df = new DecimalFormat("###############.#####");//去掉小数点后面的0
		strPer=df.format(Double.parseDouble(strPer));
		return strPer+ "%";
	}
    /**
     * 取了解程度信息
     * @param object_id
     * @param plan_id
     * @return
     */
    public ArrayList getUnderstandingOf(String object_id,String plan_id)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select know_id,name from per_know where status=1 order by seq");
    		ArrayList dgreeList = new ArrayList();
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("id",rs.getString("know_id"));
    			bean.set("itemname", rs.getString("name"));
    			dgreeList.add(bean);
    		}
    		buf.setLength(0);
    		buf.append("select count(*) as total from per_mainbody where object_id='"+object_id+"' and plan_id="+plan_id);
    		buf.append(" and know_id is not null ");
    		rs = dao.search(buf.toString());
    		String total = "0";
    		while(rs.next())
    		{
    			total = rs.getString("total");
    		}
    		//if(total.equals("0"))
    			//return list;
    		buf.setLength(0);
    		buf.append("select count(know_id) as wgcount,know_id from per_mainbody where object_id='");
			buf.append(object_id);
			buf.append("' and plan_id="+plan_id+" and know_id is not null group by know_id");
			rs = dao.search(buf.toString());
			/**封装了解程度与票数*/
			HashMap gradehm = new HashMap();
			while(rs.next())
			{
				gradehm.put(rs.getString("know_id"), PubFunc.NullToZero(rs.getString("wgcount")));
			}
			for(int i=0;i<dgreeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)dgreeList.get(i);
				String gradeid = (String)bean.get("id");
				String itemname= (String)bean.get("itemname");
				String vote ="0";
				if(gradehm.get(gradeid)!=null)
				{
					vote=(String)gradehm.get(gradeid);
				}
				String percent="0%";
				if(!"0".equals(vote))
				{
					percent=getPercent(vote,Integer.parseInt(total));
				}
				LazyDynaBean a_bean = new LazyDynaBean();
				a_bean.set("itemname",itemname);
				a_bean.set("vote",vote);
				a_bean.set("percent",percent);
				list.add(a_bean);
				
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    public ArrayList getUnderstandingOfDetail(String object_id,String plan_id)
    {
    	ArrayList list = new ArrayList();
    	RowSet rs=null;
    	try
    	{
        	StringBuffer buf = new StringBuffer();
        	ContentDAO dao = new ContentDAO(this.conn);
    		buf.append("select know_id,name from per_know where status=1 order by seq");
    		ArrayList dgreeList = new ArrayList();
    	    rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("id",rs.getString("know_id"));
    			bean.set("itemname", rs.getString("name"));
    			dgreeList.add(bean);
    		}
    		buf.setLength(0);
	    	buf.append(" select count(body_id) as B_total,body_id,know_id from per_mainbody");
	    	buf.append(" where plan_id="+plan_id+" and object_id='"+object_id+"' group by body_id,know_id");
	    	rs=dao.search(buf.toString()); 
	    	HashMap map =new HashMap();
	    	HashMap bodyMap = new HashMap();
	    	ArrayList bodyList =new ArrayList();
	    	int total=0;
	    	HashMap countMap = new HashMap();
	    	StringBuffer body_id_buf= new StringBuffer("");
	    	while(rs.next())
	    	{
			    total+=rs.getInt("B_total");
			    if(countMap.get(rs.getString("body_id"))==null)
			    {
			    	countMap.put(rs.getString("body_id"), rs.getString("B_total"));
			    }
			    else
			    {
			    	String ff=(String)countMap.get(rs.getString("body_id"));
			    	int ii=Integer.parseInt(ff)+rs.getInt("B_total");
			    	countMap.put(rs.getString("body_id"), ii+"");
			    }
			    if(!bodyList.contains((rs.getString("body_id"))))
			    {
			    	bodyList.add(rs.getString("body_id"));
			    	body_id_buf.append(","+rs.getString("body_id"));
			    }
			    map.put(rs.getString("body_id")+rs.getString("know_id"), rs.getString("B_total"));
	    	}
	    	buf.setLength(0);
	    	if(body_id_buf!=null&&!"".equals(body_id_buf.toString()))
	    	{
	        	buf.append("select body_id,name from per_mainbodyset where body_id in("+body_id_buf.substring(1)+")");
	        	rs=dao.search(buf.toString());
	        	while(rs.next())
	        	{
	        		bodyMap.put(rs.getString("body_id").toUpperCase(),rs.getString("name"));
	        	}
	    	}
	    	for(int i=0;i<bodyList.size();i++)
	    	{
	    		LazyDynaBean bean = new LazyDynaBean();
	    		String bodyid=(String)bodyList.get(i);
	    		String bodyTotal="0";
	    		if(countMap.get(bodyid)!=null) {
                    bodyTotal=(String)countMap.get(bodyid);
                }
	    		bean.set("bodyname",(String)bodyMap.get(bodyid.toUpperCase()));
	    		ArrayList subList = new ArrayList();
	    		for(int j=0;j<dgreeList.size();j++)
	    		{
	    			LazyDynaBean subbean=new LazyDynaBean();
	    			LazyDynaBean dgreebean=(LazyDynaBean)dgreeList.get(j);
	    			String gradeid = (String)dgreebean.get("id");
					String itemname= (String)dgreebean.get("itemname");
					subbean.set("itemname", itemname);
					String vote="0";
					if(map.get(bodyid+gradeid)!=null) {
                        vote=(String)map.get(bodyid+gradeid);
                    }
					subbean.set("vote",vote);
					String percent="0%";
					if(!"0".equals(vote))
					{
						percent=getPercent(vote,Integer.parseInt(bodyTotal));
					}
					subbean.set("vote",vote);
					subbean.set("percent",percent);
					subList.add(subbean);
	    		}
	    		bean.set("sublist", subList);
	    	    list.add(bean);
	    	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 取评语
     * @param object_id
     * @param plan_id
     * @return
     */
    public String getReviews(String object_id,String plan_id)
    {
    	String review ="";
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		sql.append("select appraise from per_result_"+plan_id);
    		sql.append(" where object_id='"+object_id+"'");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(sql.toString());
    		while(rs.next())
    		{
    			review=Sql_switcher.readMemo(rs,"appraise");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null) {
                    rs.close();
                }
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return review;
    }
    /**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
	/**
	 * 封装总体评价统计图数据
	 * @param planid
	 * @param objectid
	 * @return
	 */
	public ArrayList getTotalEvaluateLineList(String planid,String objectid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap map=new HashMap();
			String sql="select whole_grade_id,count(id) from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' group by whole_grade_id";
			rowSet =dao.search(sql);	
			while(rowSet.next())
			{
				map.put(rowSet.getString(1),rowSet.getString(2));
			}
			
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String gradeClass=(String)htxml.get("EvalClass");
			if(gradeClass==null || "".equals(gradeClass)|| "0".equals(gradeClass)) {
                gradeClass=(String)htxml.get("GradeClass");
            }
			//等级分类ID
			sql="select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String itemname=rowSet.getString(2);
				CommonData data=null;
				if(map.get(id)!=null)
				{
					data=new CommonData((String)map.get(id),itemname);
				}
				else {
                    data=new CommonData("0",itemname);
                }
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 封装总体评价统计图数据
	 * @param planid
	 * @param objectid
	 * @return
	 */
	public ArrayList getTotalEvaluateLineListByScore(String planid,String objectid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			HashMap map=new HashMap();
			String gradeClass=(String)htxml.get("EvalClass");
			if(gradeClass==null || "".equals(gradeClass)|| "0".equals(gradeClass)) {
                gradeClass=(String)htxml.get("GradeClass");
            }
			String sql=" select pdc.id,count(*) vote from per_degreedesc pdc,per_mainbody pmb ";
			sql+=" where whole_score>"+Sql_switcher.isnull("bottomscore", "0")+" and (whole_score<"+Sql_switcher.isnull("topscore", "9999")+" or whole_score="+Sql_switcher.isnull("topscore", "9999")+") ";
			sql+=" and plan_id="+planid+" and degree_id="+gradeClass+" and object_id='"+objectid+"' ";
			sql+=" group by pdc.id ";
			rowSet =dao.search(sql);	
			
			while(rowSet.next())
			{
				map.put(rowSet.getString(1),rowSet.getString(2));
			}
			sql="select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String itemname=rowSet.getString(2);
				CommonData data=null;
				if(map.get(id)!=null)
				{
					data=new CommonData((String)map.get(id),itemname);
				}
				else {
                    data=new CommonData("0",itemname);
                }
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	/**
	 * 封装总体评价统计图数据
	 * @param planid
	 * @param objectid
	 * @return
	 */
	public ArrayList getSumTotalEvaluateLineListByScore(String planid,String objectid,String bodyid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			int sumNumber = 0;
			ContentDAO dao = new ContentDAO(this.conn);
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			HashMap map=new HashMap();
			String gradeClass=(String)htxml.get("EvalClass");
			if(gradeClass==null || "".equals(gradeClass) || "0".equals(gradeClass)) {
                gradeClass=(String)htxml.get("GradeClass");
            }
			String sql=" select pdc.id,count(*) vote from per_degreedesc pdc,per_mainbody pmb ";
			sql+=" where whole_score>"+Sql_switcher.isnull("bottomscore", "0")+" and (whole_score<"+Sql_switcher.isnull("topscore", "9999")+")" ;
			sql+=" and plan_id="+planid+" and degree_id="+gradeClass+" and object_id='"+objectid+"' ";
			if (!"all".equals(bodyid)) {
                sql += " and pmb.body_id="+bodyid+" ";
            }
			sql+=" group by pdc.id ";
			rowSet =dao.search(sql);	
			while(rowSet.next())
			{
				String whole_grade_id = rowSet.getString(1);
				map.put(rowSet.getString(1),rowSet.getString(2));
				
				if(whole_grade_id!=null && whole_grade_id.trim().length()>0) {
                    sumNumber += Integer.parseInt(rowSet.getString(2));
                }
			}
			
			boolean wholeEvel = false;
			sql="select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String itemname=rowSet.getString(2);
				CommonData data=null;
				if(map.get(id)!=null)
				{
					data=new CommonData((String)map.get(id),itemname);
				}
				else {
                    data=new CommonData("0",itemname);
                }
				list.add(data);
				wholeEvel = true;
			}
			if(wholeEvel)
			{
				CommonData sumData = new CommonData(String.valueOf(sumNumber),"合计");
				list.add(sumData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 封装总体评价统计图数据
	 * @param planid
	 * @param objectid
	 * @return
	 */
	public ArrayList getSumTotalEvaluateLineList(String planid,String objectid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			int sumNumber = 0;
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap map=new HashMap();
			String sql="select whole_grade_id,count(id) from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' group by whole_grade_id";
			rowSet =dao.search(sql);	
			while(rowSet.next())
			{
				String whole_grade_id = rowSet.getString(1);
				map.put(rowSet.getString(1),rowSet.getString(2));
				
				if(whole_grade_id!=null && whole_grade_id.trim().length()>0) {
                    sumNumber += Integer.parseInt(rowSet.getString(2));
                }
			}
			
			boolean wholeEvel = false;
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String gradeClass=(String)htxml.get("EvalClass");
			if(gradeClass==null || "".equals(gradeClass)|| "0".equals(gradeClass)) {
                gradeClass=(String)htxml.get("GradeClass");					//等级分类ID
            }
			sql="select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String itemname=rowSet.getString(2);
				CommonData data=null;
				if(map.get(id)!=null)
				{
					data=new CommonData((String)map.get(id),itemname);
				}
				else {
                    data=new CommonData("0",itemname);
                }
				list.add(data);
				wholeEvel = true;
			}
			if(wholeEvel)
			{
				CommonData sumData = new CommonData(String.valueOf(sumNumber),"合计");
				list.add(sumData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 得到 评语和意见列表
	 * @param planid  计划id
	 * @param objectid 考核对象
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getReviewsAndViewsList(String planid,String objectid) throws GeneralException 
	{
		ArrayList list=new ArrayList();
		RowSet rowset = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms ");
			buf.append(" where pm.plan_id = "+planid+" and pm.body_id = pms.body_id ");
			buf.append(" and pm.object_id = '"+objectid+"' order by pm.body_id ");			
			rowset=dao.search(buf.toString());
			while(rowset.next())
			{
				String descctrl=rowset.getString("descctrl");
				if(descctrl==null|| "0".equals(descctrl))
				{
					String name = rowset.getString("name");
	    			String context=Sql_switcher.readMemo(rowset,"description").replaceAll("@#@","<br>");
		    		context=context.replaceAll("\r\n","<br>");
		     		context=context.replaceAll(" ","&nbsp;");
		     		String allContent=context;
		     		if(name!=null && name.trim().length()>0 && allContent.trim().length()>0) {
                        allContent="（"+name+"）"+context;
                    }
		    		if(allContent.trim().length()>0) {
                        list.add(allContent);
                    }
				}
				else
				{
					String un=rowset.getString("b0110");
					String undesc=AdminCode.getCodeName("UN",un==null?"":un);
					String um=rowset.getString("e0122");
					String umdesc=AdminCode.getCodeName("UM",um==null?"":um);
					String position=rowset.getString("a0101");
					//String positiondesc=AdminCode.getCodeName("@K",position==null?"":position);
					String context=Sql_switcher.readMemo(rowset,"description").replaceAll("@#@","<br>");
		    		context=context.replaceAll("\r\n","<br>");
		     		context=context.replaceAll(" ","&nbsp;");
		     		String allContent=undesc+"&nbsp;&nbsp;"+umdesc+"&nbsp;&nbsp;"+position+"<br>"+context;
		    		if(allContent.trim().length()>0) {
                        list.add(allContent);
                    }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowset!=null) {
                    rowset.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return list;
	}
	/**
	 * 总体评价的统计图标题
	 * @param objectid
	 * @return
	 */
	public String getTitle(String objectid,String planid)
	{
		String titlename="";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select a0101 from per_object where upper(object_id)='"+objectid.toUpperCase()+"' and plan_id="+planid);
			if(rowSet.next())
			{
				titlename=rowSet.getString(1)+"  "+ResourceFactory.getProperty("lable.performance.viewsTitle");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return titlename;
	}
	/**
	 * 管理范围内的人员
	 * @param code
	 * @param nbase
	 * @param view
	 * @return
	 */
	public HashMap getSql(String code,String nbase,UserView view)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer select_buf = new StringBuffer();
			StringBuffer columns_buf = new StringBuffer();
			StringBuffer where_buf = new StringBuffer();
			StringBuffer order_buf = new StringBuffer();
			//organization
			/**高级授权*/
			StringBuffer buf = new StringBuffer();
			String priStrSql = InfoUtils.getWhereINSql(view, nbase);
			buf.append("select "+nbase+"a01.A0100 ");
			if (priStrSql.length() > 0) {
                buf.append(priStrSql);
            } else {
                buf.append(" from "+nbase+"a01");
            }
     		select_buf.append(" select b0110,e0122,e01a1,a0101,a0100,a0000 ");
			where_buf.append(" from "+nbase+"a01 ");
			where_buf.append(" where ");
			if(code!=null&&code.length()>=2)
			{
				String c=code.substring(0,2);
				String v=code.substring(2);
				if("UN".equalsIgnoreCase(c))
				{
					where_buf.append(" (b0110 like '");
					where_buf.append(v+"%' ");
					if(v==null|| "".equals(v))
					{
						where_buf.append(" or b0110 is null ");
					}
					where_buf.append(")");
				}
				if("UM".equalsIgnoreCase(c))
				{
					where_buf.append(" (e0122 like '");
					where_buf.append(v+"%' ");
					if(v==null|| "".equals(v))
					{
						where_buf.append(" or e0122 is null ");
					}
					where_buf.append(")");
				}
				if("@K".equalsIgnoreCase(c))
				{
					where_buf.append(" (e01a1 like '");
					where_buf.append(v+"%' ");
					if(v==null|| "".equals(v))
					{
						where_buf.append(" or e01a1 is null ");
					}
					where_buf.append(")");
				}
			}
			else
			{
				where_buf.append(" 1=2 ");
			}
			if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
			{
				
			}
			else
			{
				String c=view.getManagePrivCode();
				String v=view.getManagePrivCodeValue();
				if(c!=null&&!"".equals(c))
				{
			    	
				}
				else
				{
					where_buf.append(" and ");
					String a0100=view.getA0100();
					String dbname=view.getDbname();
					if(dbname==null|| "".equals(dbname)||a0100==null|| "".equals(a0100))
					{
						where_buf.append(" 1=2 ");
					}
					else
					{
						RecordVo vo = new RecordVo(dbname+"a01");
						vo.setString("a0100", a0100);
						ContentDAO dao = new ContentDAO(this.conn);
						vo=dao.findByPrimaryKey(vo);
						if(vo!=null)
						{
							if(vo.getString("e0122")!=null&&!"".equals(vo.getString("e0122")))
							{
								where_buf.append(" e0122 like '");
					    		where_buf.append(vo.getString("e0122")+"%' ");
					    		
							}
							else if(vo.getString("b0110")!=null&&!"".equals(vo.getString("b0110")))
							{
								where_buf.append(" b0110 like '");
					    		where_buf.append(vo.getString("b0110")+"%' ");
					    		
							}
							else
							{
								where_buf.append(" 1=2 ");
							}
						}
					}
					
				}
				where_buf.append(" and a0100 in ("+buf.toString()+")");
			}
			where_buf.append(" and exists (select object_id from per_object,per_plan where per_object.plan_id=per_plan.plan_id and ((per_plan.status=6 and per_plan.feedback=1) or per_plan.status=7) and per_object.object_id="+nbase+"A01.a0100 )");
			columns_buf.append("A0100,B0110,E0122,E01A1,A0101");
			order_buf.append(" order by b0110,e0122,a0000");
			map.put("1",select_buf.toString());
			map.put("2", where_buf.toString());
			map.put("3",order_buf.toString());
			map.put("4",columns_buf.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList getPrivDblist(ArrayList privlist)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			if(privlist==null||privlist.size()<=0) {
                return list;
            }
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<privlist.size();i++)
			{
				String pre = (String)privlist.get(i);
				if(i!=0) {
                    buf.append(",");
                }
				buf.append("'"+pre+"'");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select pre,dbname from dbname where pre in (");
			sql.append(buf);
			sql.append(") order by dbid");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("pre"),rs.getString("dbname")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取登录用户的权限代码，业务用户取管理范围，自助用户取管理范围，没有管理范围取其所在单位或部门的代码
	 * @param view
	 * @return
	 */
	public String getManagePrivCode(UserView view)
	{
		String code="";
		RowSet rs=null;
		try
		{
			if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
			{
				
			}
			else
			{
	    		/**业务用户*/
	    		if(view.getStatus()==0)
	    		{
	    			String c=view.getManagePrivCode();
	    			String v=view.getManagePrivCodeValue();
	    			if(c==null|| "".equals(c))
	    			{
	    				StringBuffer buf = new StringBuffer();
	    				buf.append(" select b0110,e0122 from ");
	    				buf.append(view.getDbname()+"a01 where a0100='"+view.getA0100()+"'");
	    				ContentDAO dao = new ContentDAO(this.conn);
	    			
	    				rs=dao.search(buf.toString());
	    				while(rs.next())
	    				{
	    					String um=rs.getString("e0122");
	    					if(um==null|| "".equals(um))
	    					{
	    						code=rs.getString("b0110");
	    						if(code==null) {
                                    code="-1";
                                }
	    					}
	    					else
	    					{
	    						code=um;
	    					}
	    				}
	    			}
	    			else
	    			{
	    				code=v;
	    			}
	 			
	    		}
	    		else
	    		{
					String codevalue = view.getUnitIdByBusi("5");
					code = codevalue == null || "".equals(codevalue) ? "-1" : codevalue;
	    		}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return code;
	}
	

	public String getTaskIdByIns(String ins_id)
	{
		String task_id="0";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
		    rowSet=dao.search("select task_id from t_wf_task  where ins_id="+ins_id+" order by task_id desc");
			if(rowSet.next())
			{
				if(rowSet.getString("task_id")!=null) {
                    task_id=rowSet.getString("task_id");
                }
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return task_id;
	}
	
	/**
     * 取得考核计划信息
     * 
     * @param planid
     * @return
     */
	public RecordVo getPerPlanVo(String planid)
	{
	
		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}

	public ArrayList getInterviewList(String plan_id,String object_id,ContentDAO dao)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String sql = "select a.*,b.name,c.a0101 from per_interview a left join per_plan b on a.plan_id=b.plan_id left join per_mainbody c on a.plan_id=c.plan_id and a.mainbody_id=c.mainbody_id and a.object_id=c.object_id where a.plan_id="+plan_id+" and a.object_id='"+object_id+"'";
			
			rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id",rs.getString("id"));
				bean.set("mainbodyid", rs.getString("a0101")==null?"":rs.getString("a0101"));
				bean.set("create_date",new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("create_date")));
				bean.set("name",rs.getString("name")==null?"":rs.getString("name"));
				bean.set("object_id",rs.getString("object_id"));
				bean.set("mainid", rs.getString("mainbody_id"));
				String ins_id=((rs.getString("ins_id")==null|| "".equals(rs.getString("ins_id")))?"-1":rs.getString("ins_id"));
				String task_id="0";
				if(!"-1".equalsIgnoreCase(ins_id)) {
                    task_id=getTaskIdByIns(ins_id);
                }
				bean.set("ins_id",ins_id);
				bean.set("task_id",task_id);
				
				
				bean.set("plan_id", plan_id);
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public String getInterviewContent(String id)
	{
		String interview="";
		RowSet rs = null;
		try
		{
			String sql ="select interview from per_interview where id="+id;
			ContentDAO dao = new ContentDAO(this.conn);
			 rs = dao.search(sql);
			while(rs.next())
			{
				interview=Sql_switcher.readMemo(rs, "interview");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return interview;
	}
	/**
	 * ConfirmFlag = 'ConfirmFlag';  // 确认标志   0（Null),1,2=未阅，已阅，已确认
	 * @param plan_id
	 * @param object_id
	 */
	public void ConfirmFlag(String plan_id,String object_id,String personOrTeamType)
	{
		RowSet rs = null;
		ArrayList aList = new ArrayList();
		try
		{
            ContentDAO dao = new ContentDAO(this.conn);
            String sqlUp = "update per_result_"+plan_id+" set confirmflag=2 where object_id = ?";
            aList.add(object_id);
            dao.update(sqlUp,aList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 *本人结果时，才设置值
	 * @param plan_id
	 * @param object_id
	 * @param model
	 * @return
	 */
	public boolean isConfirm(String plan_id,String object_id)
	{
		boolean return_flag=true;
		RowSet rowSet=null;
		try
		{
			String sql = "select * from per_result_"+plan_id+" where 1=2";
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search(sql);
			ResultSetMetaData data=rowSet.getMetaData();
			boolean flag=true;
			for(int i=1;i<=data.getColumnCount();i++)
			{
				String columnName=data.getColumnName(i).toLowerCase();
				if("ConfirmFlag".equalsIgnoreCase(columnName))
				{
					flag=false;
				}
				else
				{
					continue;
				}
			}
			if(rowSet!=null)
			{
				rowSet.close();
			}
			if(flag)
			{
				Table table = new Table("per_result_"+plan_id);
				Field field = new Field("confirmflag","confirmflag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table.addField(field);
				DbWizard dbw=new DbWizard(this.conn);
				dbw.addColumns(table);
			    dao.update("update per_result_"+plan_id+" set confirmflag=1 where object_id='"+object_id+"'");
			    return_flag=false;
			}
			else
			{
				RowSet rs = dao.search(" select confirmflag from per_result_"+plan_id+" where object_id='"+object_id+"'");
				while(rs.next())
				{
					String confirmflag=rs.getString("confirmflag");
					if(confirmflag==null|| "0".equals(confirmflag)|| "1".equals(confirmflag)) {
                        return_flag=false;
                    }
					if(confirmflag==null|| "0".equals(confirmflag))
					{
						dao.update("update per_result_"+plan_id+" set confirmflag=1 where object_id='"+object_id+"'");
					}
				}
			}
		   

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rowSet!=null)
			{
				try
				{
					rowSet.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return return_flag;
	}
	/**联通专版，首页查看考核结果面板*/
	public ArrayList getMyResultList(String object_id,String distinctionFlag,String year,UserView view)
	{
		ArrayList list = new ArrayList();
		RowSet rs  = null;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select object_type,plan_id,name,parameter_content,"+Sql_switcher.isnull("a0000", "999999")+" as norder  from per_plan where (status=7 or status=6)");
			if(!"-1".equals(year)) {
                buf.append(" and theyear="+year);
            }
			buf.append(" and object_type='2' and plan_id in (select plan_id from per_object where object_id='"+object_id+"') order by norder asc,plan_id desc");
			ContentDAO dao = new ContentDAO(this.conn);
			rs= dao.search(buf.toString());
		    LoadXml loadXml=new LoadXml();
		    HashMap scoreMap = this.getObjectScore(object_id, "",2);
			StatisticPlan sp = new StatisticPlan(view,this.conn);
		    HashMap map = new HashMap();
			while(rs.next())
			{
			    String xmlContent =Sql_switcher.readMemo(rs,"parameter_content");
			    String performanceType=loadXml.getPerformanceType(xmlContent);
			    if(distinctionFlag.equals(performanceType))
			    {
			         String name=rs.getString("name");
			         int plan_id=rs.getInt("plan_id");
			         LoadXml parameter_content = null;
	    	         if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
					{
							
	    	         	parameter_content = new LoadXml(this.conn,plan_id+"");
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
					}
					else
					{
						parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
					}
					Hashtable params = parameter_content.getDegreeWhole();
					/**没指定登记表，不显示该计划*/
					if(params.get("ShowBackTables")==null|| "".equals((String)params.get("ShowBackTables"))) {
                        continue;
                    }
			         LazyDynaBean bean = new LazyDynaBean();
			         bean.set("plan_id",plan_id+"");
			        /* bean.set("name",name);
			          String objecttype="";
			          if(rs.getString("object_type").equals("1"))
			                 objecttype=ResourceFactory.getProperty("jx.jifen.group");
			          else if(rs.getString("object_type").equals("2"))
			                 objecttype=ResourceFactory.getProperty("jx.jifen.person");
			          if(scoreMap.get(plan_id+object_id)!=null)
			                 bean.set("score",(String)scoreMap.get(plan_id+object_id));
			          else
			                 bean.set("score","");
			          bean.set("object_type",objecttype);
			          bean.set("grade", this.getDegreeDesc(plan_id, object_id));*/
			          bean.set("object_id",object_id);
			          bean.set("distinctionFlag", distinctionFlag);
			          bean.set("model", "0");
			          list.add(bean);
			        }
			   }
			    		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	public ArrayList getRnameListFromPlanID(String plan_id)throws GeneralException
	  {
		  StringBuffer sql=new StringBuffer();	   
		  ArrayList list =new ArrayList();
		  CommonData vo=null;
		  ContentDAO dao=new ContentDAO(this.conn);
		  LoadXml parameter_content = null;
	         if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable params = parameter_content.getDegreeWhole();
            String tabids=(String)params.get("ShowBackTables");
            RowSet rs=null;
			if(tabids!=null&&tabids.length()>0)
			 {
				 try
				  {
					  sql=new StringBuffer();		  
					  sql.append("select tabid,name from rname");
					  sql.append(" where UPPER(FlagA)='P' and tabid in ("+tabids+")");
					 
					  rs=dao.search(sql.toString());
					  while(rs.next())
					  {
						  vo=new CommonData();
						  vo.setDataName(rs.getString("name"));
						  vo.setDataValue("P"+rs.getString("tabid"));
						  list.add(vo);
					  }
					  
				  }catch(Exception e)
				  {
					  throw GeneralExceptionHandler.Handle(e);
				  }finally{
					  if(rs!=null)
					  {
						  try
						  {
							  if(rs!=null) {
                                  rs.close();
                              }
						  }
						  catch(Exception e)
						  {
							  e.printStackTrace();
						  }
					  }
				  }
			 }		    
		  return list;
	  }
	
	public String getMusterList(String object_id)
	{
		String plan_id="-1";
		RowSet rs =null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("");
			buf.append(" select plan_id from per_plan ");
			buf.append(" where plan_id in (select plan_id from per_object where object_id='"+object_id+"')");
			buf.append(" and theyear=(select max(theyear) as theyear from per_plan where plan_id in(");
			buf.append(" select plan_id from per_object where object_id='"+object_id+"')) order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ");
			rs= dao.search(buf.toString());
			while(rs.next())
			{
				String planid=rs.getString("plan_id");
				ArrayList alist = getRnameListFromPlanID(planid);
				if(alist==null||alist.size()==0) {
                    continue;
                } else
				{
					plan_id=planid;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return plan_id;
	}
	// 获得图形下面的菜单
	public ArrayList getGraphTypeList(int flag,String model)
	{
		ArrayList list = new ArrayList();
		CommonData obj=new CommonData("1","个人得分概览");
		list.add(obj);
		obj=new CommonData("2","主体分类对比分析");
		list.add(obj);
		if(flag==1 && !"3".equalsIgnoreCase(model))
		{
			obj=new CommonData("3","同级人群得分分布图");
			list.add(obj);
		}
		return list;
	}
	//获得同级指标
	public String getSameField(String planid,String object_id)
	{
		String str = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			LoadXml loadxml = new LoadXml(conn, planid);
			String grpMenu1Name = "";
			String grpMenu2Name = "";
			ArrayList formulalist1 = loadxml.getRelatePlanValue("CustomOrderGrp", "GrpMenu1");				
			if(formulalist1!=null && formulalist1.size()>0)
			{
				if(((String) formulalist1.get(0)).split(";").length==2) {
                    grpMenu1Name = (((String) formulalist1.get(0)).split(";")[0]).toString();
                }
			}
			
			ArrayList formulalist2 = loadxml.getRelatePlanValue("CustomOrderGrp", "GrpMenu2");				
			if(formulalist2!=null && formulalist2.size()>0)
			{
				if(((String) formulalist2.get(0)).split(";").length==2) {
                    grpMenu2Name = (((String) formulalist2.get(0)).split(";")[0]).toString();
                }
			}
			
			
			if(grpMenu1Name!=null && grpMenu1Name.trim().length()>0)
			{									
				String sql = "select "+grpMenu1Name+" from per_result_"+planid+" where object_id ='"+object_id+"' ";
				rs = dao.search(sql);
				if(rs.next())
				{
					String level_id = rs.getString(1);
					if(level_id!=null && level_id.trim().length()>0) {
                        str += " and "+grpMenu1Name+"='"+level_id+"' ";
                    } else
					{
						if(!"body_id".equalsIgnoreCase(grpMenu1Name)) {
                            str += " and ("+grpMenu1Name+" is null or "+grpMenu1Name+"='') ";
                        }
					}
				}				
			}
			if(grpMenu2Name!=null && grpMenu2Name.trim().length()>0)
			{
				String sql = "select "+grpMenu2Name+" from per_result_"+planid+" where object_id ='"+object_id+"' ";
				rs = dao.search(sql);
				if(rs.next())
				{
					String level_id = rs.getString(1);
					if(level_id!=null && level_id.trim().length()>0) {
                        str += " and "+grpMenu2Name+"='"+level_id+"' ";
                    } else
					{
						if(!"body_id".equalsIgnoreCase(grpMenu2Name)) {
                            str += " and ("+grpMenu2Name+" is null or "+grpMenu2Name+"='') ";
                        }
					}
				}
			}
			if((grpMenu1Name==null || grpMenu1Name.trim().length()<=0) && (grpMenu2Name==null || grpMenu2Name.trim().length()<=0)) // 如果没有设置同级指标，则找对象类别
			{
				StringBuffer sb = new StringBuffer();
				sb.append("select body_id from per_object where plan_id='"+planid+"' and object_id ='"+object_id+"' ");
				rs = dao.search(sb.toString());
				while(rs.next())
				{
					String body_id = rs.getString("body_id");
					if(body_id!=null && body_id.trim().length()>0) {
                        str += " and body_id='"+body_id+"' ";
                    }
				}
			}
			
			if(rs!=null) {
                rs.close();
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return str;
	}
//	//得到部门层级
//	public String getDepartLevel(String planid){
//		String str = "0";
//		LoadXml parameter_content = new LoadXml(conn, planid);
//		Hashtable params = parameter_content.getDegreeWhole();
//		String strTemp = (String)params.get("DepartmentLevel");
//		if(strTemp!=null && !strTemp.equals(""))
//			str = strTemp;
//		return str;
//	}
	//获得同级分布人群的数据
    public ArrayList getTongjiList(String planid,String object_id,String sameField,String department)
    {
    	ArrayList list = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs  =null;
    	try
    	{
    		String[] temp = sameField.split("`");
    		String tableName = "per_result_"+planid;
    		Table table = new Table(tableName);
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(!dbWizard.isExistTable(table.getName(),false))//如果数据库中不存在这个表
			{
				return list;
			}
    		
    		StringBuffer sb = new StringBuffer();
    		sb.append("select object_id,score from per_result_"+planid+" where 1=1 "+sameField);
    		if(!"0".equals(department)){//如果部门不是"全部"
    			sb.append(" and e0122='"+department+"'");
    		}
    		String selfScore = "0.0";//本人的分数
    		ArrayList tempList = new ArrayList();
    		rs = dao.search(sb.toString());
    		boolean isHaveSelf = false;
    		int i=0;
    		while(rs.next()){
    			String objectId = rs.getString("object_id");
    			if(objectId.equalsIgnoreCase(object_id)){
    				isHaveSelf = true;
    				selfScore = rs.getString("score")==null?"0.0":rs.getString("score");
    				continue;
    			}
    			i++;
	    		tempList = new ArrayList();
	            LazyDynaBean abean=new LazyDynaBean();
	            CommonData A_data = new CommonData(rs.getString("score")==null?"0.0":rs.getString("score"),"同级分数");
	            tempList.add(A_data);
	            //abean.set("categoryName", rs.getString("a0101"));
	            abean.set("categoryName", "同级"+i);
	           	abean.set("dataList",tempList);
	           	list.add(abean);
    		}
    		
    		//再添加自己的分数
    		if(!isHaveSelf){//如果指定部门中没有本人，则要单独查找
    			StringBuffer self = new StringBuffer();
    			self.append("select score from per_result_"+planid+" where object_id='"+object_id+"'");
    			rs = dao.search(self.toString());
    			if(rs.next()){
    				selfScore = rs.getString("score")==null?"0.0":rs.getString("score");
    			}
    		}
	    	tempList = new ArrayList();
    		LazyDynaBean abean=new LazyDynaBean();
    	    CommonData A_data = new CommonData(selfScore,"本人分数");
    	    tempList.add(A_data);
        	abean.set("categoryName", "本人");
   			abean.set("dataList",tempList);
   			list.add(abean);

    		
    		
    	if(rs!=null) {
            rs.close();
        }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public HashMap getTongjiMap(String planid,String object_id,String sameField,String department,String chart_type){
    	HashMap map = new HashMap();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs =null;
    	try
    	{
    		double maxRadarScore = 0.0;
    		String tableName = "per_result_"+planid;
    		Table table = new Table(tableName);
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(!dbWizard.isExistTable(table.getName(),false))
			{
				map.put(ResourceFactory.getProperty("lable.examine.argfraction"), new ArrayList());
				return map;
			}
    		
    		StringBuffer sb = new StringBuffer();
    		sb.append("select object_id,score from per_result_"+planid+" where 1=1 "+sameField);
    		if(!"0".equals(department)){//如果部门不是"全部"
    			sb.append(" and e0122='"+department+"'");
    		}
    		String selfScore = "0.0";//本人的分数
    		ArrayList tempList = new ArrayList();
    		rs = dao.search(sb.toString());
    		boolean isHaveOwn = false;
    		int i=0;
    		while(rs.next()){
    			String objectId = rs.getString("object_id");
    			if(objectId.equalsIgnoreCase(object_id)){
    				isHaveOwn = true;
    				selfScore = rs.getString("score")==null?"0.0":rs.getString("score");
    				continue;
    			}
    			i++;
            	String tempScore = rs.getString("score")==null?"0.0":rs.getString("score");
            	//CommonData A_data = new CommonData(tempScore, rs.getString("a0101"));
            	CommonData A_data = new CommonData(tempScore, "同级"+i);
            	tempList.add(A_data);
            	if(maxRadarScore < Double.parseDouble(tempScore)) {
                    maxRadarScore = Double.parseDouble(tempScore);
                }
    		}
    		
    		//再添加自己的分数
    		if(!isHaveOwn){//如果指定部门中没有本人，则要单独查找
    			StringBuffer self = new StringBuffer();
    			self.append("select score from per_result_"+planid+" where object_id='"+object_id+"'");
    			rs = dao.search(self.toString());
    			if(rs.next()){
    				selfScore = rs.getString("score")==null?"0.0":rs.getString("score");
    			}
    		}
    		if(maxRadarScore < Double.parseDouble(selfScore)) {
                maxRadarScore = Double.parseDouble(selfScore);
            }
    		
    		ArrayList selfList = new ArrayList();
        	CommonData S_data = new CommonData(selfScore,"本人");
        	selfList.add(S_data);
        	map.put("本人分数", selfList);
        	map.put("同级分数", tempList);
    		
    		if("41".equals(chart_type))
			 {
    			map.put("minmax",0+","+maxRadarScore);
			 }
    		
    	if(rs!=null) {
            rs.close();
        }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    //得到该考核计划下的部门列表
    public ArrayList getDepartmentList(String plan_id,String sameField){
    	ArrayList list = new ArrayList();
    	try{
    		//全部为0，其余为部门号
    		CommonData obj=new CommonData("0","全部");
    		list.add(obj);
    		
    		ContentDAO dao = new ContentDAO(this.conn);
        	RowSet rs = null;
        	StringBuffer sb = new StringBuffer();
        	sb.append("select distinct e0122 from per_result_"+plan_id+" where 1=1 "+sameField);//得到和他同级的人的部门
        	rs = dao.search(sb.toString());
        	while(rs.next()){
        		String e0122 = rs.getString("e0122");
        		if(e0122!=null && !"".equals(e0122)){
        			obj=new CommonData(e0122,AdminCode.getCodeName("UM", e0122));
        			list.add(obj);
        		}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return list;
    }
//    //找出指定层级的部门号
//    public String getDepartmentId(String departmentId,String departLevel){
//    	String str = "";
//    	try{
//    		ContentDAO dao = new ContentDAO(this.conn);
//        	RowSet rs  =null;
//        	int levelTemp2 = Integer.parseInt(departLevel);//从计划参数中得到的部门层级
//        	StringBuffer sb = new StringBuffer();
//        	//先得到当前部门是第几级
//        	sb.append("select codeitemid,layer from organization where codeitemid='"+departmentId+"' and codesetid='UM'");
//        	rs = dao.search(sb.toString());
//        	if(rs.next()){
//        		String codeitemid = rs.getString("codeitemid");
//        		int layer = rs.getInt("layer");
//        		if(layer<=levelTemp2){//如果当前是2级，计划参数指定要5级，就按当前部门算
//        			str = codeitemid;
//        			return str;
//        		}else{//否则，如果当前是5级，计划参数指定要2级，则向上找
//        			str = getDepartment(codeitemid,departLevel);
//        			return str;
//                }
//        	}
//        	if(rs!=null){
//        		rs.close();
//        	}
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    	return str;
//    }
//   public String getDepartment(String codeitemid,String departLevel){
//	    String str = "";
//	    try{
//	    	ContentDAO dao = new ContentDAO(this.conn);
//	   	    RowSet rs  =null;
//	   		StringBuffer sb = new StringBuffer();
//	   		sb.append("select codeitemid,layer from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"' and codesetid='UM') and codesetid='UM'");
//	   		rs = dao.search(sb.toString());
//	   		if(rs.next()){
//	   			String codeitem_id = rs.getString("codeitemid");
//	   			int layer = rs.getInt("layer");
//	   			if(layer==Integer.parseInt(departLevel)){
//	   				str = codeitem_id;
//	   				return str;
//	   			}else{
//	   				str = getDepartment(codeitem_id,departLevel);
//	   			}
//	   		}
//	   		if(rs!=null){
//	   			rs.close();
//	   		}
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }
//	   return str;
//   }
   //评价盲点         获得盲点
   public ArrayList getBlindList(HashMap pointScoreMap,double percent){
	   ArrayList list = new ArrayList();
	   ArrayList advantageList = new ArrayList();//存储优势盲点
	   ArrayList weeknessList = new ArrayList();//存储劣势盲点
	   try
	   {
		   int KeepDecimal = Integer.parseInt((String)this.planParamSet.get("KeepDecimal"));
		    Set key = pointScoreMap.keySet();
		    for (Iterator it = key.iterator(); it.hasNext();) {
		    	String s = (String) it.next();
				if(s.indexOf("self`")!=-1) {
                    continue;     //剔除本人自评的打分
                }
				String[] temp = ((String)pointScoreMap.get(s)).split("`");
				double score = Double.parseDouble(temp[0]);//他评的总分
				int count = Integer.parseInt(temp[1]);//评价的人数（本人除外）
				String pointname = "";
				if(temp.length>2) {
					pointname = temp[2];
				}
				String tempScore=(String)pointScoreMap.get("self`"+s)==null?"0":(String)pointScoreMap.get("self`"+s);
				double selfScore = Double.parseDouble(tempScore);//自评分数
				double avgScore = score/count;//他评的平均分
				if(selfScore<1e-6 && avgScore>1e-6){//如果自评分是0分，并且他评不是0分，那么直接列为优势盲点
					ArrayList listTemp = new ArrayList();
					//listTemp.add(s);
					listTemp.add(pointname);
					listTemp.add(PubFunc.round(String.valueOf(selfScore), KeepDecimal));
					listTemp.add(PubFunc.round(String.valueOf(avgScore), KeepDecimal));
					advantageList.add(listTemp);
				}else{
					if(avgScore-selfScore>1e-6 && (avgScore-selfScore)/selfScore*100>=percent){//(他评的平均分-自评分）为正数，并且比例大于设定的比例
						//此为优势盲点
						ArrayList listTemp = new ArrayList();
						//listTemp.add(s);
						listTemp.add(pointname);
						listTemp.add(PubFunc.round(String.valueOf(selfScore), KeepDecimal));
						listTemp.add(PubFunc.round(String.valueOf(avgScore), KeepDecimal));
						advantageList.add(listTemp);
					}else if(avgScore-selfScore<1e-6 && (selfScore-avgScore)/selfScore*100>=percent){
						//此为劣势盲点
						ArrayList listTemp = new ArrayList();
						//listTemp.add(s);
						listTemp.add(pointname);
						listTemp.add(PubFunc.round(String.valueOf(selfScore), KeepDecimal));
						listTemp.add(PubFunc.round(String.valueOf(avgScore), KeepDecimal));
						weeknessList.add(listTemp);
					}
				}
		    }
		    list.add(advantageList);
		    list.add(weeknessList);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return list;
   }
   //得到指标的分数      格式如下：键：指标id     值：分数`人数`指标名称
   public HashMap getFieldMap(String object_id,String plan_id,int method){
	   HashMap map = new HashMap();
	   try
	   {
		   loadxml = new LoadXml(this.conn, plan_id);
		   planParamSet = loadxml.getDegreeWhole();
		   int KeepDecimal = Integer.parseInt((String)this.planParamSet.get("KeepDecimal"));
		   
		   ContentDAO dao = new ContentDAO(this.conn);
	   	   RowSet rs  =null;
	   	   StringBuffer sb = new StringBuffer();
		   if(method==1){//如果是360考核
			   String tableName = "per_table_"+plan_id;
			   sb.append("select ptx.point_id,ptx.score,ptx.mainbody_id,p.pointname from "+tableName+" ptx left join per_point p on p.point_id=ptx.point_id");
			   sb.append(" where ptx.object_id='"+object_id+"' and ptx.mainbody_id in (select distinct mainbody_id from per_table_"+plan_id+" where object_id='"+object_id+"') order by ptx.point_id");
		   }else if(method==2){//目标考核
			   sb.append("select p.p0401 point_id,pte.score,pte.mainbody_id,p.p0407 pointname ");
			   sb.append("from per_target_evaluation pte,p04 p ");
			   sb.append("where pte.p0400=p.p0400 and pte.plan_id='"+plan_id+"' and pte.object_id='"+object_id+"' and pte.mainbody_id in (select distinct mainbody_id from per_target_evaluation where plan_id='"+plan_id+"' and object_id='"+object_id+"') order by p.p0401");
		   }
		   
		   rs = dao.search(sb.toString());
		   int count = 1;//存储共有几个指标，也就是几个主体给它打分（当然，本人除外）
		   while(rs.next()){
			   String point_id = rs.getString("point_id");
			   String score = rs.getString("score")==null?"0.0":rs.getString("score");
			   String mainbody_id = rs.getString("mainbody_id");
			   String pointname = rs.getString("pointname")==null?"":rs.getString("pointname");
			   if(object_id.equalsIgnoreCase(mainbody_id)){//如果是本人打分
				   map.put("self`"+point_id, PubFunc.round(score, KeepDecimal));
			   }else{//如果不是本人打分
				   if(map.get(point_id)==null){//如果还没有把这个指标放到map中
					   count = 1;
					   map.put(point_id, PubFunc.round(score, KeepDecimal)+"`"+count+"`"+pointname);
				   }else{
					   count++;
					   double scoreTemp = Double.parseDouble(((String)map.get(point_id)).split("`")[0])+Double.parseDouble(score);
					   map.put(point_id, PubFunc.round(String.valueOf(scoreTemp), KeepDecimal)+"`"+count+"`"+pointname);
				   }
			   }
		   }
		   if(rs!=null){
			   rs.close();
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return map;
   }
   //得到盲点的百分比
   public double getPercent(int method){
	   double percent = 0.0;
	   try{
		    ContentDAO dao = new ContentDAO(conn);
		    RowSet rs = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if (rs.next()){
		    	String str_value = rs.getString("str_value");
		    	if (str_value == null || (str_value != null && "".equals(str_value))){
		    	}else{
		    	    Document doc = PubFunc.generateDom(str_value);
		    	    String xpath = "//Per_Parameters";
		    	    XPath xpath_ = XPath.newInstance(xpath);
		    	    Element ele = (Element) xpath_.selectSingleNode(doc);
		    	    Element child;
		    	    if (ele != null){
		    	    	child=ele.getChild("ResultVisible");
						if(child!=null){
							if(method==1) {
                                percent=Double.parseDouble(child.getAttributeValue("blind_360")==null||child.getAttributeValue("blind_360").trim().length()<=0?"0.0":child.getAttributeValue("blind_360"));
                            } else {
                                percent=Double.parseDouble(child.getAttributeValue("blind_goal")==null||child.getAttributeValue("blind_goal").trim().length()<=0?"0.0":child.getAttributeValue("blind_goal"));
                            }
						}
		    	    }
		    	}
		    }
		    if(rs!=null) {
                rs.close();
            }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return percent;
   }
   //画出盲点表格
   public String getBlindHtml(ArrayList blindList){
	   StringBuffer htmlContext = new StringBuffer();
		ArrayList advantageList = (ArrayList)blindList.get(0);//优势盲点
		ArrayList weeknessList = (ArrayList)blindList.get(1);//劣势盲点
		int advantageCount = advantageList.size();
		int weeknessCount = weeknessList.size();
		
		// 输出表头
		htmlContext.append("<table class='ListTable' >\r\n");
		htmlContext.append("<tr  height='20' >\r\n");
		
		htmlContext.append("<td class='TableRow' width='200' valign='middle' align='center' nowrap >"+ResourceFactory.getProperty("general.mediainfo.type")+"</td>\r\n");
		htmlContext.append("<td class='TableRow' width='200' valign='middle' align='center' nowrap >"+ResourceFactory.getProperty("org.performance.field")+"</td>\r\n");
		htmlContext.append("<td class='TableRow' width='100' valign='middle' align='center' nowrap >"+ResourceFactory.getProperty("org.performance.self.score")+"</td>\r\n");
		htmlContext.append("<td class='TableRow' width='100' valign='middle' align='center' nowrap >"+ResourceFactory.getProperty("org.performance.other.score")+"</td>\r\n");
		
		htmlContext.append("</tr>\r\n");
		
		//输出表体
		for(int i=0;i<advantageCount;i++){
			ArrayList tempList = (ArrayList)advantageList.get(i);
			String pointname = (String)tempList.get(0);//名字
			String selfScore = (String)tempList.get(1);//自评分数
			String avgScore = (String)tempList.get(2);//他评分数
			htmlContext.append("<tr>\r\n");
			if(i==0) {
                htmlContext.append("<td align='center' width='200' class='RecordRow' rowspan='"+advantageCount+"'>" + ResourceFactory.getProperty("org.performance.advantage.blind") + "</td>\r\n");
            }
			htmlContext.append("<td align='left' width='200' class='RecordRow' >" + pointname + "</td>\r\n");
			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>"+selfScore+"</td>\r\n");
			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>"+avgScore+"</td>\r\n");
			htmlContext.append("</tr>\r\n");
		}
		for(int i=0;i<weeknessCount;i++){
			ArrayList tempList = (ArrayList)weeknessList.get(i);
			String pointname = (String)tempList.get(0);//名字
			String selfScore = (String)tempList.get(1);//自评分数
			String avgScore = (String)tempList.get(2);//他评分数
			htmlContext.append("<tr>\r\n");
			if(i==0) {
                htmlContext.append("<td align='center' width='200' class='RecordRow' rowspan='"+weeknessCount+"'>" + ResourceFactory.getProperty("org.performance.weekness.blind") + "</td>\r\n");
            }
			htmlContext.append("<td align='left' width='200' class='RecordRow' >" + pointname + "</td>\r\n");
			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>"+selfScore+"</td>\r\n");
			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>"+avgScore+"</td>\r\n");
			htmlContext.append("</tr>\r\n");
		}
		htmlContext.append("</table>");
	   return htmlContext.toString();
   }
   
   
}
