package com.hjsj.hrms.businessobject.performance.batchGrade;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title:ExcelBo.java</p>
 * <p>Description>:多人考评导出Excel</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 03, 2010 09:15:57 AM</p>
 * <p>@author: JinChunhai
 * <p>@version: 5.0</p>
 */

public class ExcelBo 
{
	 private UserView userView=null;
	 private Connection conn = null;
	 private String planid="";
	 private Hashtable htxml = new Hashtable();
	 private String scoreflag = "2";  // =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
	 private String DegreeShowType = "1";  // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
	 private RecordVo planVo=null;
	 private HSSFCellStyle title_cellStyle=null;
	 private HSSFCellStyle titleNum_cellStyle=null;
	 private HSSFCellStyle body_cellStyle=null;
	 private HSSFCellStyle value_cellStyle=null;
	 private HSSFCellStyle noPowerValue_cellStyle=null;
	 
	 HSSFWorkbook workbook=null;
	 HSSFSheet sheet=null;
	 HSSFRow row=null;
	 HSSFCell csCell=null;
	 int rowNum=0;
	 int colNum=0;
	 
	
	public ExcelBo(Connection con,String planid,UserView userView)
	{
		this.conn=con;
		this.planid=planid;
		this.userView=userView;
		planVo=getPlanVo(this.planid);
		LoadXml loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(this.planid);
		this.htxml= loadxml.getDegreeWhole();
		this.scoreflag=(String)this.htxml.get("scoreflag");		// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
		this.DegreeShowType=(String)this.htxml.get("DegreeShowType"); // 标度显示形式  1.标准标度内容  2.指标标度内容  3.采集标准标度,显示指标标度内容
	}
	
	public RecordVo getPlanVo(String planid)
    {

		RecordVo vo = new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id", Integer.parseInt(planid));
			ContentDAO dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public String getExcelFileName()
	{
		String outputFile =PubFunc.getStrg() + "_batchGrade.xls";
		FileOutputStream fileOut = null;
		try
		{
			ArrayList objList=getObjList(); //获得考核对象信息
			ArrayList pointList=getPointList(); //获得计划下模板的指标集						
			/* 得到某计划考核主体给对象的评分结果hashMap */
		    HashMap valueMap = getPerTableXXX(objList);		
		    
		    BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn,this.planid);
		    HashMap objPointMap = batchGradeBo.getPointprivMap(this.planid, this.userView.getA0100()); // 得到指标权限信息
		    												
			workbook = new HSSFWorkbook();
			this.title_cellStyle = style(this.workbook,0);
			this.titleNum_cellStyle = style(this.workbook,1);
			this.body_cellStyle = style(this.workbook,3);
			this.value_cellStyle = style(this.workbook,4);
			this.noPowerValue_cellStyle = style(this.workbook,5);
			
			this.sheet = workbook.createSheet("评分主页"); //主评分页
			executeHeader(this.sheet,0,objList,"all");
			executeBody(this.sheet,0,objList,pointList,valueMap,objPointMap,"all");	
			
			//this.sheet.protectSheet(new String("1")); // 保护工作表 并设置密码为1
			
						
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;						
			String[] lettersUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			int rowi =0;				
			for(int y=0;y<pointList.size();y++)
			{
    			LazyDynaBean hdbean=(LazyDynaBean)pointList.get(y);	
    			String pointid=(String)hdbean.get("pointid");
    			
    			int index = 0;
				int div = 0;
				int mod = 0;
				int layNum = 3;			
				rowi++;
				
    			StringBuffer sql = new StringBuffer();
    			if("1".equals(this.scoreflag) && ("2".equals(this.DegreeShowType) || "3".equals(this.DegreeShowType)))
				{					    						    
					sql.append("select gradecode,gradedesc from per_grade where point_id='" + pointid + "' order by gradecode ");								
						
				}else if("1".equals(this.scoreflag) && "1".equals(this.DegreeShowType))
				{
					String per_comTable = "per_grade_template"; // 绩效标准标度
					if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1)
						per_comTable = "per_grade_competence"; // 能力素质标准标度
					sql.append("select grade_template_id,gradedesc from "+per_comTable+" order by grade_template_id ");														
				}
    			if(sql!=null && sql.toString().trim().length()>0)
			    {
				    int m = 0;
					rowSet = dao.search(sql.toString());																	   			    				  				   				
	    			while (rowSet.next())
	    			{
	    				row = sheet.getRow(pointList.size()*rowi+m+0);
	    				if (row == null)
	    					row = sheet.createRow(pointList.size()*rowi+m + 0);
	    				csCell = row.createCell((short) (208 + index));   
	    				if("1".equals(this.scoreflag) && ("2".equals(this.DegreeShowType) || "3".equals(this.DegreeShowType)))
	    					csCell.setCellValue(new HSSFRichTextString(rowSet.getString("gradecode")+":"+rowSet.getString("gradedesc")));
	    				else if("1".equals(this.scoreflag) && "1".equals(this.DegreeShowType))
	    					csCell.setCellValue(new HSSFRichTextString(rowSet.getString("grade_template_id")+":"+rowSet.getString("gradedesc")));
	    				m++;
	    			}
    				
    				if(m==0)
    					m=2;
    				m = m+pointList.size()*rowi;
    				int m1 =1;
    				m1=m1+pointList.size()*rowi;
    				sheet.setColumnWidth((short) (208 + index), (short) 0);	
    				div = (index)/26;
    				mod = (index)%26;
    				
	    			for(int x=0;x<objList.size();x++)
	    			{       			
	    				String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$"+m1+":$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
	    				
	    				CellRangeAddressList addressList = new CellRangeAddressList(rowi+3, rowi+3, layNum, layNum);
	    				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
	    				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	    				dataValidation.setSuppressDropDownArrow(false);
	    				sheet.addValidationData(dataValidation);
	
	    			//	index++;  
	    				layNum++;
				    }
				}			
			}

			
			LazyDynaBean bean=null;
			for(int i=0;i<objList.size();i++)
			{
				bean=(LazyDynaBean)objList.get(i);
				String object_id=(String)bean.get("object_id");
				String a0101=(String)bean.get("a0101");
				if(this.planVo.getInt("object_type")==2){
					this.sheet = workbook.createSheet(a0101+"("+object_id+")"); //单一考核对象评分页
				}else{
					String a0101Desc=(String)bean.get("a0101Desc");
					this.sheet = workbook.createSheet(a0101Desc); //单一考核对象评分页
				}
				executeHeader(this.sheet,0,objList,object_id);
				executeBody(this.sheet,0,objList,pointList,valueMap,objPointMap,object_id+"`"+i);
				
				//this.sheet.protectSheet(new String("1")); // 保护工作表 并设置密码为1
			}
				
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		
		return outputFile;
	}
		
	
	public void executeHeader(HSSFSheet sheet,int flag,ArrayList objList,String objectLogo)
	{
		LazyDynaBean bean=null;
		this.rowNum=0;
		this.colNum=0; 
		row=sheet.createRow(this.rowNum);
		
		//指标
		executeMergeCell(this.rowNum,this.colNum,this.rowNum+2,this.colNum,"指标",title_cellStyle,"String"); 		
		++this.colNum;	
		
		if("all".equalsIgnoreCase(objectLogo))
		{
			//指标解释
			executeMergeCell(this.rowNum,this.colNum,this.rowNum+2,this.colNum,"指标定义",title_cellStyle,"String"); 
			++this.colNum;
		}		
		//评分标准			
		executeMergeCell(this.rowNum,this.colNum,this.rowNum+2,this.colNum,"评分标准",title_cellStyle,"String"); 
		++this.colNum;		
		
		//评分对象得分				
		if("all".equalsIgnoreCase(objectLogo))
			executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum+(objList.size()-1),"评分对象得分",title_cellStyle,"String"); 
		else
			executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum,"评分对象得分",title_cellStyle,"String"); 
		//评分对象
		++this.rowNum; 
		
		HashSet bodyNameSet=new HashSet();
		for (int a1 =0; a1 <objList.size(); a1++) 
		{
			bean=(LazyDynaBean)objList.get(a1);
			String object_id=(String)bean.get("object_id");
			String e0122=(String)bean.get("e0122");
			String e01a1=(String)bean.get("e01a1");
			String a0101=(String)bean.get("a0101");			
			String context="";
			if(this.planVo.getInt("object_type")==2)  //人员
				context=e0122+" "+e01a1+" "+a0101;			
			else			
				context=a0101;			
						
			if("all".equalsIgnoreCase(objectLogo))
			{
				bodyNameSet.add((String)bean.get("bodyname"));
				executeCell(this.rowNum,this.colNum+a1,context,title_cellStyle,"String"); 
			}
			else if(objectLogo.equalsIgnoreCase(object_id))
			{
				bodyNameSet.add((String)bean.get("bodyname"));
				executeCell(this.rowNum,this.colNum,context,title_cellStyle,"String"); 
				break;
			}
		}
		
		//编号
		this.rowNum++;
		this.colNum=0; 
//		executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum+1,onlyFieldName,title_cellStyle,"String"); 
		this.colNum++;this.colNum++;
		if("all".equalsIgnoreCase(objectLogo))
			this.colNum++;
		for (int a1 =0; a1 <objList.size(); a1++) 
		{
			bean=(LazyDynaBean)objList.get(a1);
			String object_id=(String)bean.get("object_id");
			String onlyValue=(String)bean.get("onlyValue"); 
			
			if("all".equalsIgnoreCase(objectLogo))
				executeCell(this.rowNum,this.colNum+a1,onlyValue,titleNum_cellStyle,"String"); 
			else if(objectLogo.equalsIgnoreCase(object_id))
			{
				executeCell(this.rowNum,this.colNum,onlyValue,titleNum_cellStyle,"String"); 
				break;
			}
		} 
		//主体类别行
		this.rowNum++;
		this.colNum=0;  
		StringBuffer buf=new StringBuffer("");
		for(Iterator t=bodyNameSet.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			buf.append("、"+temp);
		}
//		executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum+2,"评价角度("+buf.substring(1)+")",title_cellStyle,"String"); 
		
		if("all".equalsIgnoreCase(objectLogo))
			executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum+2,"评价角度",title_cellStyle,"String"); 
		else
			executeMergeCell(this.rowNum,this.colNum,this.rowNum,this.colNum+1,"评价角度",title_cellStyle,"String");
		this.colNum++;this.colNum++;
		if("all".equalsIgnoreCase(objectLogo))
			this.colNum++;
		for (int a1 =0; a1 <objList.size(); a1++) 
		{
			bean=(LazyDynaBean)objList.get(a1);
			String object_id=(String)bean.get("object_id");
			String bodyname=(String)bean.get("bodyname"); 
			
			if("all".equalsIgnoreCase(objectLogo))
				executeCell(this.rowNum,this.colNum+a1,bodyname,title_cellStyle,"String"); 
			else if(objectLogo.equalsIgnoreCase(object_id))
			{
				executeCell(this.rowNum,this.colNum,bodyname,title_cellStyle,"String"); 
				break;
			}
		} 
					 
		this.row = sheet.getRow(0);
		this.row.setHeight((short)500);
		this.row = sheet.getRow(1);
		this.row.setHeight((short)1000);
		this.row = sheet.getRow(2);
		this.row.setHeight((short)500);
		this.row = sheet.getRow(3);
		this.row.setHeight((short)500);
		 
		this.sheet.setColumnWidth(0,6000);
		this.sheet.setColumnWidth(1,10000);	
		int n = 2;
		if("all".equalsIgnoreCase(objectLogo))
		{
			n = 3;
			this.sheet.setColumnWidth(2,10000);
		}
		
		for (int a1 =0; a1 <objList.size(); a1++) 
		{
			bean=(LazyDynaBean)objList.get(a1);
			String object_id=(String)bean.get("object_id");
			
			if("all".equalsIgnoreCase(objectLogo))
				this.sheet.setColumnWidth(n+a1,6000); 
			else if(objectLogo.equalsIgnoreCase(object_id))
			{
				this.sheet.setColumnWidth(n,7000);
				break;
			}
		}
	}
	
	
	public void executeBody(HSSFSheet sheet,int flag,ArrayList objList,ArrayList pointList,HashMap valueMap,HashMap objPointMap,String objectLogo)
	{
		LazyDynaBean bean=null;
		for(int i=0;i<pointList.size();i++)
		{
			this.rowNum++;
			this.colNum=0;
			bean=(LazyDynaBean)pointList.get(i);
			String pointid=(String)bean.get("pointid");
			String pointname=(String)bean.get("pointname");
			String description=(String)bean.get("description");
			String gd_principle=(String)bean.get("gd_principle");
			
			executeCell(this.rowNum,this.colNum,pointname,body_cellStyle,"String"); 
			this.colNum++;
			if("all".equalsIgnoreCase(objectLogo))
			{
				executeCell(this.rowNum,this.colNum,description,body_cellStyle,"String"); 
				this.colNum++;
			}			
			executeCell(this.rowNum,this.colNum,gd_principle,body_cellStyle,"String"); 
			this.colNum++;
			
			for (int a1 =0; a1 <objList.size(); a1++) 
			{
				bean=(LazyDynaBean)objList.get(a1); 
				String object_id=(String)bean.get("object_id");
				// 考核对象的考核结果				
				HashMap objectResultMap = (HashMap) valueMap.get(object_id);
				LazyDynaBean abean = (LazyDynaBean) objectResultMap.get(pointid);
				// 得到具有某考核对象的指标权限map 
				HashMap pointMap = (HashMap) objPointMap.get(object_id); 				

				String gradevalue = "";
				if(objectResultMap!=null && objectResultMap.size()>0 && abean!=null)
				{
					if("1".equals(this.scoreflag) && ("2".equals(this.DegreeShowType) || "3".equals(this.DegreeShowType)))
				    {					    						    					
						String degree_id=(String)abean.get("degree_id");
						String gradedesc=(String)abean.get("gradedesc");	
						gradevalue = degree_id + ":" + gradedesc;
						
				    }else if("1".equals(this.scoreflag) && "1".equals(this.DegreeShowType))
				    {
				    	String degree_id=(String)abean.get("degree_id");
				    	String gradedesc=(String)abean.get("gradedesc");	
				    	gradevalue = degree_id + ":" + gradedesc;
				    }else
				    {
				    	String score=(String)abean.get("score");	
				    	gradevalue = score;
				    }
				}
				if (pointMap.get(pointid) != null && "0".equals((String) pointMap.get(pointid)))
			    {
					gradevalue = "---";			    				
					if("all".equalsIgnoreCase(objectLogo))
						executeCell(this.rowNum,this.colNum+a1,gradevalue,noPowerValue_cellStyle,"String"); 
					else if(object_id.equalsIgnoreCase(objectLogo.substring(0,objectLogo.indexOf("`"))))
					{
						executeCell(this.rowNum,this.colNum,gradevalue,noPowerValue_cellStyle,"objStr"+"&"+(objectLogo.substring(objectLogo.indexOf("`")+1,objectLogo.length()))); 
						break;
					}
				}else
				{
					if("all".equalsIgnoreCase(objectLogo))
						executeCell(this.rowNum,this.colNum+a1,gradevalue,value_cellStyle,"String"); 
					else if(object_id.equalsIgnoreCase(objectLogo.substring(0,objectLogo.indexOf("`"))))
					{
						executeCell(this.rowNum,this.colNum,gradevalue,value_cellStyle,"objStr"+"&"+(objectLogo.substring(objectLogo.indexOf("`")+1,objectLogo.length()))); 
						break;
					}
				}
			}
			
			this.row = sheet.getRow(this.rowNum);
			this.row.setHeight((short)1000);
		//	this.row.setHeight((short)1500);  // 广东中烟的行高是 1500
		}
	}
	
	
	/**
	 * 画excel的格子
	 * @param a
	 * @param b
	 * @param content
	 * @param aStyle
	 */
	public void executeCell(int a, int b, String content,HSSFCellStyle aStyle,String type) 
	{		
		 
		HSSFComment comm = null;
		row = this.sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		
		csCell = row.getCell(b);
		if(csCell==null)
			csCell = row.createCell(b);
		if("num".equalsIgnoreCase(type)&& (content!=null&&content.length()>0))
		{
			csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
			csCell.setCellValue(new Double(content).doubleValue());
			
		}else if(type.indexOf("objStr")!=-1)
		{			
			HashMap map = getNextLetter();
			String ss = (String)map.get(String.valueOf(b+1+Integer.parseInt((type.substring(type.indexOf("&")+1,type.length())))));
						
			csCell.setCellValue(new HSSFRichTextString(content));
			csCell.setCellFormula("评分主页!"+ss+(a+1));			
			
		}else
		{
			csCell.setCellValue(new HSSFRichTextString(content));
		}
		csCell.setCellStyle(aStyle); 
		
		
		/*
		 * 
		 * 
		 * Private Sub Worksheet_SelectionChange(ByVal Target As Range)
			[A1] = Target.Address
			End Sub

		row=sheet.createRow(0);
		csCell=row.createCell(0);
		csCell.setCellValue("ssss");
	
		
		sheet = workbook.createSheet("a1");
		row=sheet.createRow(0);
		csCell=row.createCell(0);
		csCell.setCellFormula("a!A1");
		*/
		
	}
	
	public HashMap getNextLetter()
	{
		HashMap map = new HashMap();
		try
		{	
			String[] lettersUpper={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};		  
			
			for(int i=0;i<257;i++)
			{	
				String x = "";
				if(i>=26)										
					x = lettersUpper[i/26-1]+lettersUpper[i%26];					    				        				    				    										
				else									
					x = lettersUpper[i];					    				        				    				    					
				
				map.put(String.valueOf(i),x);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 画excel的格子（合并单元格）
	 * @param a rowNum
	 * @param b colNum
	 * @param c rowNumLast
	 * @param d colNumLast
	 * @param content
	 * @param aStyle
	 */
	public void executeMergeCell(int a, int b, int c, int d, String content,HSSFCellStyle aStyle,String type) 
	{		
		try {
			HSSFComment comm = null;
			row = this.sheet.getRow(a);
			if(row==null)
				row = sheet.createRow(a);
			
			csCell = row.getCell(b);
			if(csCell==null)
				csCell = row.createCell(b);
			
			if("num".equalsIgnoreCase(type)&& (content!=null&&content.length()>0))
			{
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);	
				csCell.setCellValue(new Double(content).doubleValue());
			}else
			{
				csCell.setCellValue(new HSSFRichTextString(content));
			}
			csCell.setCellStyle(aStyle);
			
			int b1 = b;
			while (++b1 <= d) 
			{
				csCell = row.getCell(b1);
				if(csCell==null)
					csCell = row.createCell(b1);
				csCell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++) 
			{
				row = sheet.getRow(a1);
				if(row==null)
					row = sheet.createRow(a1);
				b1 = b;
				while (b1 <= d)
				{
					csCell = row.getCell(b1);
					if(csCell==null)
						csCell = row.createCell(b1);
					csCell.setCellStyle(aStyle);
					b1++;
				}
			} 
			
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
		} catch (Exception e) {
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
				//设置此列style为锁定   true:锁定  false: 非锁定
			    style.setLocked(true);
			    
			    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.fsgb.font"), 12);
			    fonttitle.setBold(false);// 加粗
			    style.setFont(fonttitle);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);			    			    
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);	
				//style.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);				
			    style.setAlignment(HorizontalAlignment.CENTER);			    			      
			    break;
			case 1:
				//设置此列style为锁定   true:锁定  false: 非锁定
			    style.setLocked(true);
			    
			    HSSFFont fontNumtitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12);
			    fontNumtitle.setBold(false);// 加粗
			    style.setFont(fontNumtitle);
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);			    			    
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);	
				//style.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);				
			    style.setAlignment(HorizontalAlignment.CENTER);			    			      
			    break;
			case 2:
				//设置此列style为非锁定   true:锁定  false: 非锁定    
			    style.setLocked(false);
			    
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.RIGHT);
			    style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式			    			    
			    break;
			case 3:
				//设置此列style为锁定   true:锁定  false: 非锁定  
			    style.setLocked(true);
				
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));				
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.LEFT);			    			    
			    break;
			case 4:
				//设置此列style为非锁定   true:锁定  false: 非锁定  
			    style.setLocked(false);
				
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.CENTER);			    			    
			    break;
			case 5:
				//设置此列style为非锁定   true:锁定  false: 非锁定  
			    style.setLocked(true);
				
				style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			    style.setBorderBottom(BorderStyle.THIN);
			    style.setBorderLeft(BorderStyle.THIN);
			    style.setBorderRight(BorderStyle.THIN);
			    style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.CENTER);			    			    
			    break;
			case 6:
				//设置此列style为非锁定   true:锁定  false: 非锁定  
			    style.setLocked(false);
				
				HSSFFont font15=fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12);
				font15.setBold(true);
				style.setFont(font15);
			    //style.setBorderBottom(BorderStyle.THIN);
			    //style.setBorderLeft(BorderStyle.THIN);
			   // style.setBorderRight(BorderStyle.THIN);
			   // style.setBorderTop(BorderStyle.THIN);
			    style.setVerticalAlignment(VerticalAlignment.CENTER);
			    style.setAlignment(HorizontalAlignment.CENTER);
			    //style.setFillPattern(HorizontalAlignment.CENTER);
			   // style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);			    			    
			    break;
			default:
				//设置此列style为非锁定   true:锁定  false: 非锁定  
			    style.setLocked(false);
				
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
	
	/**
	 * 获得考核对象信息
	 * @return
	 */
	public ArrayList getObjList()
	{
		ArrayList objList = new ArrayList();
		RowSet rowSet = null;
		try
		{
			String mitiScoreMergeSelfEval=(String) htxml.get("mitiScoreMergeSelfEval");
			if(mitiScoreMergeSelfEval==null||mitiScoreMergeSelfEval.length()==0)
				mitiScoreMergeSelfEval="False";
			String onlyname="";  //唯一性指标 
			String onlyField="";
			if(this.planVo.getInt("object_type")==2){  //人员
				 Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
				 onlyField=sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		    	 onlyname =",Usra01."+onlyField;
			}else {
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.conn);
				if(unit_code_field_constant_vo!=null)
				{
					onlyField=unit_code_field_constant_vo.getString("str_value");
					if(onlyField!=null&&!"#".equals(onlyField)){
					    onlyname=",B01."+onlyField;
					}

				}	
			} 
			 
			ContentDAO dao = new ContentDAO(this.conn);
			String _str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				_str="pms.level_o";
			else
				_str="pms.level ";
			
			StringBuffer sql=new StringBuffer("");
			if(this.planVo.getInt("object_type")==2){  //人员
				sql.append(" select pm.object_id,pms.name,po.e0122,po.e01a1,po.a0101"+onlyname+" from per_mainbody pm,per_object po,per_mainbodyset pms");
				if(onlyname.length()>0)
					sql.append(",Usra01 ");
				sql.append(" where pm.object_id=po.object_id and pm.body_id=pms.body_id ");
				if(onlyname.length()>0)
					sql.append(" and po.object_id=Usra01.a0100 ");
				sql.append(" and pm.plan_id="+this.planid+" and po.plan_id="+this.planid+" and pm.mainbody_id='"+this.userView.getA0100()+"' ");
				if ("False".equalsIgnoreCase(mitiScoreMergeSelfEval))
				{
					sql.append(" and pm.object_id<>'"+this.userView.getA0100()+"'");
				}
				sql.append(" order by po.a0000,po.b0110,po.e0122,po.object_id ");
			}
			else
			{
				sql.append(" select pm.object_id,pms.name,po.e0122,po.e01a1,po.a0101"+onlyname+",organization.corCode from per_mainbody pm,per_object po,per_mainbodyset pms,organization");
				if(onlyname.length()>0)
					sql.append(",B01");
				sql.append(" where pm.object_id=po.object_id and pm.body_id=pms.body_id and po.object_id=organization.codeitemid");
				if(onlyname.length()>0)	
					sql.append("  and po.object_id=b01.b0110 ");
				sql.append(" and pm.plan_id="+this.planid+" and po.plan_id="+this.planid+" and pm.mainbody_id='"+this.userView.getA0100()+"' ");
				if ("False".equalsIgnoreCase(mitiScoreMergeSelfEval))
				{
					sql.append(" and ( "+_str+" is null or "+_str+"<>5 ) ");
				}
				sql.append(" order by po.a0000,po.b0110,po.object_id ");
			}	
			LazyDynaBean bean=null;
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				bean=new LazyDynaBean();
				bean.set("object_id",rowSet.getString("object_id"));
				bean.set("bodyname",rowSet.getString("name"));
				if(rowSet.getString("e0122")!=null)
				{
					String e0122=rowSet.getString("e0122");
					bean.set("e0122",AdminCode.getCodeName("UM",e0122));
				}
				else
					bean.set("e0122","");
				
				if(rowSet.getString("e01a1")!=null)
				{
					String e01a1=rowSet.getString("e01a1");
					bean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
				}
				else
					bean.set("e01a1","");
				
				bean.set("a0101",rowSet.getString("a0101"));
				if(this.planVo.getInt("object_type")!=2){//非人员  为了防止工作表重名 团队、部门考核取名称+单位、部门代码或编码 zzk2013/11/14
					if(rowSet.getString("corCode")!=null&&!"".equals(rowSet.getString("corCode").trim())){
						bean.set("a0101Desc",rowSet.getString("a0101")+"("+rowSet.getString("corCode")+")");
					}else{
						bean.set("a0101Desc",rowSet.getString("a0101")+"("+rowSet.getString("object_id")+")");
					}
				}
				if(onlyname.length()>0)
				{
					FieldItem item=DataDictionary.getFieldItem(onlyField.toLowerCase());
					bean.set("onlyFieldName",item.getItemdesc());
					if(rowSet.getString(onlyField)!=null)
						bean.set("onlyValue",rowSet.getString(onlyField));
					else
						bean.set("onlyValue","");
				}
				else
				{
					if(this.planVo.getInt("object_type")==2){  //人员
						bean.set("onlyFieldName","人员编号");
					}
					else
					{
						bean.set("onlyFieldName","机构编号");
					}
					bean.set("onlyValue",rowSet.getString("object_id"));
				}
				objList.add(bean);
			}
			if(rowSet!=null)
		    	rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objList;
	}
	
	
	/**
	 * 获得计划下模板的指标集
	 * @return
	 */
	public ArrayList getPointList()
	{
		ArrayList pointlist=new ArrayList();
		
		ArrayList list0=(ArrayList)BatchGradeBo.plan_perPointMap.get(this.planid);
		ArrayList list = (ArrayList) list0.get(1); // 指标集
		String[] temp=null;
		LazyDynaBean bean=null;
		for(int i=0;i<list.size();i++)
		{
			temp=(String[])list.get(i);
			bean=new LazyDynaBean();
			bean.set("pointid",temp[0]);
			bean.set("pointname",temp[1]);
			bean.set("gd_principle",temp[10]);
			bean.set("description",temp[11]);
			pointlist.add(bean);
		}
		
		return pointlist;
	}
	
	
	/**
     * 得到某计划考核主体给对象的评分结果hashMap
     * 
     * @param plan_id
     *                考核计划id
     * @param mainbodyID
     *                考核主体id
     * @param object_id
     *                考核对象列表
     * @return HashMap
     */
	public HashMap getPerTableXXX(ArrayList objList) throws GeneralException
	{
	
		HashMap hashMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{		    
		    DecimalFormat myformat1 = new DecimalFormat("##########.#####");//
		    DbWizard dbWizard = new DbWizard(this.conn);
		    for(int i=0;i<objList.size();i++)
			{
		    	LazyDynaBean bean=(LazyDynaBean)objList.get(i);
				String object_id=(String)bean.get("object_id");
						    
				HashMap map = new HashMap();
				StringBuffer sql = new StringBuffer();
				if (dbWizard.isExistTable("per_table_" + this.planid))
				{
				    if("1".equals(this.scoreflag) && ("2".equals(this.DegreeShowType) || "3".equals(this.DegreeShowType)))
					{					    						    
						sql.append("select * from per_table_" + this.planid + " pt,per_grade pg ");
						sql.append(" where pt.mainbody_id='" + this.userView.getA0100() + "' and pt.object_id='" + object_id + "' ");	
						sql.append(" and pt.point_id=pg.point_id and pt.degree_id=pg.gradecode ");		
							
					}else if("1".equals(this.scoreflag) && "1".equals(this.DegreeShowType))
					{
						String per_comTable = "per_grade_template"; // 绩效标准标度
						if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1)
							per_comTable = "per_grade_competence"; // 能力素质标准标度
					    sql.append("select * from per_table_" + this.planid + " pt,"+per_comTable+" pg ");
					    sql.append(" where pt.mainbody_id='" + this.userView.getA0100() + "' and pt.object_id='" + object_id + "' ");			    	
					    sql.append(" and pt.degree_id=pg.grade_template_id ");	
														
					}else
					{
					    sql.append("select * from per_table_" + this.planid + " where mainbody_id='" + this.userView.getA0100() + "' and object_id='" + object_id + "' ");												    						    	
					}
				    	
					rowSet = dao.search(sql.toString());						
					while (rowSet.next())
					{
						LazyDynaBean abean = new LazyDynaBean();	
						String point_id = rowSet.getString("point_id")!=null?rowSet.getString("point_id"):"";
							
						if("1".equals(this.scoreflag) && ("2".equals(this.DegreeShowType) || "3".equals(this.DegreeShowType)))
						{					    						    													    	
							abean.set("degree_id",rowSet.getString("degree_id")!=null?rowSet.getString("degree_id"):"");
							abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");	
								
						}else if("1".equals(this.scoreflag) && "1".equals(this.DegreeShowType))
						{						    
						    abean.set("degree_id",rowSet.getString("degree_id")!=null?rowSet.getString("degree_id"):"");
						    abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");						    																
						}else
						{						    
						    abean.set("score",myformat1.format(rowSet.getDouble("score")));						    										    						    	
						}
							
						map.put(point_id, abean);// key取得是point_id字段
					}		
				}				    
				hashMap.put(object_id, map);
				
		    }
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}	
		return hashMap;
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
    		ContentDAO dao = new ContentDAO(this.conn);
    		rowSet = dao.search("select * from per_grade_template order by gradevalue desc");
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
	

}
