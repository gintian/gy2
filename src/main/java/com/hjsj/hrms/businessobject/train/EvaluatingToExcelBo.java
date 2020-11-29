package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.actionform.askinv.EndViewForm;
import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.jxcell.*;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class EvaluatingToExcelBo {
	
	
	private Connection conn=null;
	private String r3101="";  //活动编号 （针对培训模块）
	private  int    contentment=0; //满意度个数
	private HSSFSheet sheet=null;
	private HSSFCellStyle style=null;
	private HSSFCellStyle style_l=null;
	private ArrayList pointList=new ArrayList();
	short rowNum=1;
	private UserView userview = null;
	
	public EvaluatingToExcelBo(Connection con)
	{
		this.conn=con;
	}

	
	public EvaluatingToExcelBo(Connection conn, UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}




	public HSSFCellStyle getStyle(String align,HSSFWorkbook wb)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		if("c".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
        } else if("l".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.LEFT);
        }
		return a_style;
	}
	
	
	
	 public void executeCell2(short columnIndex,String value)
	 {
		 //HSSFRow row = this.sheet.createRow( rowNum);
		 HSSFRow row = this.sheet.getRow(rowNum);
			if(row==null) {
                row = this.sheet.createRow( rowNum);
            }
		 HSSFCell cell = row.createCell(columnIndex); 
		 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		 cell.setCellValue(value); 
	 }
	 
	 
	 public void executeCell(int a,short b,int c,short d,String content,String style,short num)
	 {
		 try {
			 //HSSFRow row = sheet.createRow(a);
			 HSSFRow row = sheet.getRow(a);
			 if(row==null) {
                 row = sheet.createRow(a);
             }
			 
			 if("h".equals(style)) {
                 row.setHeight((short)num);
             }
			 HSSFCell cell = row.createCell(b);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellStyle(this.style_l);
			 cell.setCellValue(content);
			 
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 // row = sheet.createRow(a1);
				 row = sheet.getRow(a1);
				 if(row==null) {
                     row = sheet.createRow(a1);
                 }
				 
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		 } catch (Exception e) {
			 e.printStackTrace();
		}
	 }
	 
	 public void executeCell(int a,short b,int c,short d,String content,String style)
	 {
		 try {
			 //HSSFRow row = sheet.createRow(a);
			 HSSFRow row = sheet.getRow(a);
			 if(row==null) {
                 row = sheet.createRow(a);
             }
			 
			 row.setHeight((short)400);
			 HSSFCell cell = row.createCell(b);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 if("c".equalsIgnoreCase(style)) {
                 cell.setCellStyle(this.style);
             } else if("l".equalsIgnoreCase(style)) {
                 cell.setCellStyle(this.style_l);
             }
			 cell.setCellValue(content);
			 
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 //row = sheet.createRow(a1);
				 row = sheet.getRow(a1);
				 if(row==null) {
                     row = sheet.createRow(a1);
                 }
				 
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		 } catch (Exception e) {
			 e.printStackTrace();
		}
	 }
	 
	 
	 //****------------------         问卷调查excel         --------------------------
	 /**
	  * @descr 问卷调查生成excel代码重构  将poi改成jxcell
	  * @author xiegh
	  * @date 2017/3/16
	  * 
	  */
	 public String getInvestigateExcel(ArrayList itemwhilelst,ArrayList itemtxtlist,String name)throws GeneralException 
	 {
		 //创建表格对象
		 View view = new View();
			view.getLock();
		 try
		 {
		    /*******初始化布局参数***************/
			// 设置颜色
			view.setPaletteEntry(1, new Color(230, 230, 230));// 浅灰色
			view.setPaletteEntry(2, new Color(217, 217, 217));// 深灰色

			// 表头样式
			// view.setDefaultRowHeight(800);
			CellFormat cfTitle = view.getCellFormat();
			cfTitle.setFontSize(10);// 设置字体大小
			cfTitle.setBottomBorder((short) 1);// 设置边框为细实线
			cfTitle.setTopBorder(cfTitle.PatternSolid);
			cfTitle.setLeftBorder((short) 1);
			cfTitle.setRightBorder((short) 1);
			cfTitle.setMergeCells(true);// 合并单元格
			cfTitle.setWordWrap(true);
			// 水平对齐方式
			cfTitle.setHorizontalAlignment(cfTitle.HorizontalAlignmentCenter);
			// 垂直对齐方式
			cfTitle.setVerticalAlignment(cfTitle.VerticalAlignmentTop);

			// 内容样式
			CellFormat cfBody = view.getCellFormat();
			cfBody.setFontSize(10);
			cfBody.setBottomBorder(cfBody.PatternSolid);// 设置边框为细实线
			cfBody.setTopBorder(cfBody.PatternSolid);
			cfBody.setLeftBorder(cfBody.PatternSolid);
			cfBody.setRightBorder(cfBody.PatternSolid);
			cfBody.setHorizontalAlignment(cfBody.HorizontalAlignmentLeft);// 水平居左
			cfBody.setVerticalAlignment(cfBody.VerticalAlignmentCenter);// 垂直居中

			// 表头样式
			CellFormat cfcolum = view.getCellFormat();
			cfcolum.setFontSize(10);
			cfcolum.setBottomBorder(cfcolum.PatternSolid);// 设置边框为细实线
			cfcolum.setTopBorder(cfcolum.PatternSolid);
			cfcolum.setLeftBorder(cfcolum.PatternSolid);
			cfcolum.setRightBorder(cfcolum.PatternSolid);
			cfcolum.setHorizontalAlignment(cfcolum.HorizontalAlignmentCenter);// 水平居中
			cfcolum.setVerticalAlignment(cfcolum.VerticalAlignmentCenter);// 垂直居中

			view.setDefaultColWidth(25 * 150);// 固定列宽
			//记录行下标
			int count = 0;
			view.setText(count, 0, ResourceFactory.getProperty("lable.topicname")+":"+name); // 设置标题
			count+=2;
			ArrayList collist = null;//横向标 
			if(itemwhilelst.size()>0) {
                for(int i = 0;i<itemwhilelst.size();i++){
                    EndViewForm evform = (EndViewForm)itemwhilelst.get(i);
                    ArrayList rowlist = evform.getEndviewlst();
                    ArrayList picList = evform.getPicList();

                    //生成标题
                    view.setText(count, 0, evform.getItemName()); // 设置标题
                    cfTitle.setPattern(CellFormat.PatternSolid);
                    cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
                    view.setCellFormat(cfTitle, count, 0, count,2); // 设置标题区域 和样式
                    count++;

                    //生成列表头
                    String questionItem = ResourceFactory.getProperty("conlumn.investigate.questionItem");
String ballot=ResourceFactory.getProperty("lable.welcome.invtextresult.ballot");
String percent= ResourceFactory.getProperty("train.evaluationStencil.percent");
                    String[] columnTitle ={questionItem,ballot,percent};
                    for (int j = 1; j < 4; j++) {
                        view.setColWidth(j, 20 * 256);
                        view.setText(count, j-1, columnTitle[j-1]);	// 内容
                        cfcolum.setPattern(CellFormat.PatternSolid);
                        cfcolum.setPatternFG(view.getPaletteEntry(2));
                        view.setCellFormat(cfcolum, count, 0, count, j-1);// 设置colum列头样式
                    }
                    count++;

                    //生成选项行 {"选项","票数","比例"};
                    int flag = count;
                    if (rowlist.size() > 0) {
                        for (int k = 0; k < rowlist.size(); k++) {
                            EndViewForm evf = (EndViewForm) rowlist.get(k);
                            String pointname = evf.getPointName();
                            String percentValue = evf.getPrecent();
                            String sumNum = evf.getSumNum();
                            collist = new ArrayList();
                            collist.add(pointname);
                            collist.add(sumNum);
                            collist.add(percentValue);
                            int index = 0;
                            for (int j = 0; j < collist.size(); j++) {
                                String data = (String) collist.get(j);
                                if (1 == j) {
                                    view.setNumber(count, j, Double.parseDouble(data));
                                } else {
                                    view.setText(count, j, data);
                                }
                                view.setCellFormat(cfBody, count, 0, count, index);
                                index++;
                            }
                            count++;
                        }
                    }
                    ChartShape chartshape = null;
                     //设置chart图的样式
                    chartshape = view.addChart(collist.size()+1,//x,y,x1,y1 坐标
                            flag - 2, 3*collist.size()+1, count
                                    + (rowlist.size()>2?rowlist.size()-1 : 2));
                    ChartFormat chartFormat = chartshape.getChartFormat();
                    chartFormat = chartshape.getMajorGridFormat(
                            chartshape.YAxis, 0);
                    chartFormat.setFontSize(166);
                /*	chartFormat.setFillAuto(true);*/
                    chartshape.setMajorGridFormat(chartshape.YAxis, 0,
                            chartFormat);// 设置坐标轴字体大小
                    chartshape.setAxisScaleReversed(chartshape.XAxis, 0,
                            true);// 将x坐标轴放置上方 逆序类别
                    chartFormat = chartshape.getLegendFormat();
                    chartFormat.setFontSize(180);
                    chartshape.setLegendFormat(chartFormat);
                    chartshape.setAxisFormat(chartshape.XAxis, 0,
                            chartFormat);
                    //设置取数范围
                    chartshape.initData(new RangeRef(0, flag - 1, collist
                            .size() - 2, count - 1), true);
                    chartshape.setTitle(evform.getItemName());// 设置标题
                    chartFormat = chartshape.getTitleFormat();
                    chartFormat.setFontSize(166);
                    chartshape.setTitleFormat(chartFormat);// 设置标题样式
                    chartshape.setChartType(chartshape.TypeBar);// 条状图
                    count+=collist.size();
                    chartFormat = chartshape.getAxisFormat(chartshape.XAxis, 0);
                    chartFormat.setFontItalic(false);
                    chartFormat.setFontSize(166);
                    chartshape.setAxisFormat(chartshape.XAxis, 0,
                            chartFormat);
                }
            }
			
			//将填空题的内容布置在view中 
			executeTextArea(itemtxtlist, view, cfBody, cfcolum, count, collist);
			String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"evaluatingExcel_"+(userview==null?"":userview.getUserName())+".xls";
			FileOutputStream fileOut = new FileOutputStream(url.toString());
			view.write(fileOut);
			fileOut.close();
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }finally {
			 	if(this.conn!=null) {
                    this.conn=null;
                }
				view.releaseLock();
			}
		 return "evaluatingExcel_"+(userview==null?"":userview.getUserName())+".xls";
	 }


	private void executeTextArea(ArrayList itemtxtlist, View view,
			CellFormat cfBody, CellFormat cfcolum, int count, ArrayList list)
			throws CellException, SQLException {
		RowSet frowset = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			if (itemtxtlist.size() > 0) {
                for (int p = 0; p < itemtxtlist.size(); p++) {
                    WelcomeForm wf = (WelcomeForm) itemtxtlist.get(p);
                    /**
                     * title
                     */
                    view.setText(count, 0, ResourceFactory.getProperty("lable.welcome.invtextresult.topicname")+":" + wf.getItemName());
                    cfcolum.setPattern(CellFormat.PatternSolid);// 和样式
                    cfcolum.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
                    view.setCellFormat(cfcolum, count, 0, count, 2); // 设置标题区域
                    count++;
                    /*
                     * 内容
                     */
                    StringBuffer sbtp = new StringBuffer();
                    ArrayList params = new ArrayList();
                    params.add(wf.getItemid());
                    sbtp.append("select investigate_item.name,investigate_content.staff_id,investigate_content.context from investigate_item ");
                    sbtp.append(",investigate_content ");
                    sbtp.append(" where investigate_item.itemid=investigate_content.itemid and investigate_item.itemid= ? ");
                    frowset= dao.search(sbtp.toString(),params);
                    int index = 1;
                    while (frowset.next()) {
                        view.setText(count, 0, ResourceFactory.getProperty("lable.welcome.invtextresult.username")+":" + index);
                        view.setCellFormat(cfBody, count, 0, count, 0);
                        String textcontent = frowset.getString("context");
                        view.setText(count, 1, ResourceFactory.getProperty("lable.welcome.invtextresult.context")+":" + textcontent);
                        cfBody.setWordWrap(true);//设置可自动换行
                        cfBody.setMergeCells(true);// 设置标题行合并单元格
                        view.setCellFormat(cfBody, count, 1, count, 2);
                        index++;
                        count++;
                    }
                    count += list.size();
                }
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(null!=frowset) {
                frowset.close();
            }
			
		}
	}

	 
	 //****--------------------------------------------    评估结果表excel      ----------------------------------------
	 
	
	
	//生成Excel文件名
	 public String getEvaluatingExcel(String templateid,String titleName)throws GeneralException 
	 {
   		 String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
			+"evaluatingExcel.xls";
   		 HSSFWorkbook wb = new HSSFWorkbook();
   		 FileOutputStream fileOut = null;
		 try
		 {
			 this.sheet = wb.createSheet("new sheet");
			 this.sheet.setColumnWidth((short)1,
				     (short)5000);

			 
			 this.style = getStyle("c",wb);
			 this.style_l=getStyle("l",wb);
			 
			 getTitle(this.r3101,titleName);
			 getBody(this.r3101,templateid);
			 getBottomTitle();
			 
			 
			 fileOut = new FileOutputStream(url);
			 wb.write(fileOut);
			 fileOut.close();
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }finally {
			 PubFunc.closeResource(fileOut);
			 PubFunc.closeResource(wb);
		 }
		 return "evaluatingExcel.xls";
	 }
	
	 
	
	
	 
	 public void getBottomTitle()throws GeneralException
	 {
		 this.rowNum++;
		 Calendar d=Calendar.getInstance();
		 String date=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
		
		 executeCell2((short)1,ResourceFactory.getProperty("train.evaluatingStencil.weave")+":");
		 executeCell2((short)4,ResourceFactory.getProperty("approve.personinfo.oks")+":");
	     this.rowNum++;
		 
		 executeCell2((short)8,ResourceFactory.getProperty("train.evaluatingStencil.statDate")+":");
		 executeCell2((short)9,date);
	
		
	 }
	 
	 
	 

	 public void getBody(String r3101,String templateid)throws GeneralException
	 {
		
		 ArrayList gradeList=getPerGradeTemplateList(templateid);   //标准标度列表
		 try
		 {
			 int questionCount=getQuestionCount(this.r3101,templateid);
			
			 getTableBodyHead(gradeList);        //产生表头
		     getTableBodyContent(gradeList,templateid,questionCount);
			 getTableBodyBasicInstance(templateid,questionCount,gradeList);
		 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	 }
	 
	 
	 
	 //取得总分
	 public String getTotalScore(String templateid)
	 {
		 String sum="0";
		 try
		 {
			 ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
			 StringBuffer sql=new StringBuffer("");
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 String[] temp=(String[])t.next();
				 sql.append("+avg("+Sql_switcher.isnull("C_"+temp[0],"0")+")");
			 }
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select "+sql.substring(1)+" from TRA_EVAL_"+templateid+" where r3101='"+this.r3101+"'");
			 if(rowSet.next()) {
                 sum=PubFunc.round(rowSet.getString(1),1);
             }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return sum;
	 }
	 
	 
	 
	 public void getTableBodyBasicInstance(String templateid,int questionCount,ArrayList gradeList)throws GeneralException
	 {
		
		 try
		 {
			int menCount=getTrainClassMenCount(this.r3101);  //培训班人数
			String percent="";//问卷回收比列
			String acontentment="";//满意度
			ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
		    if(questionCount!=0)
		    {
		    	int aa=a_pointList.size()*questionCount;
		    	
		    	acontentment=(new BigDecimal(this.contentment)).divide(new BigDecimal(aa),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
		    	acontentment=PubFunc.round(acontentment,1);
		    	
		    }
			 if(menCount!=0)
			 {
				 percent=(new BigDecimal(questionCount)).divide(new BigDecimal(String.valueOf(menCount)),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
				 percent=PubFunc.round(percent,1);
			 }
			
			 //调查基本情况
			 StringBuffer str=new StringBuffer(ResourceFactory.getProperty("train.evaluationStencil.investigateState")+":  ");
			 str.append("       "+ResourceFactory.getProperty("train.evaluationStencil.trainMenCount")+":"+menCount+"人，  "+ResourceFactory.getProperty("train.evaluationStencil.returnQuestion")+":"+questionCount+"份，  "+ResourceFactory.getProperty("train.evaluationStencil.returnQuestionPercent")+":"+percent+"%   "+ResourceFactory.getProperty("train.evaluationStencil.contentment")+":"+acontentment+"%");
			 str.append("  总分: "+getTotalScore(templateid)+"分");
			 executeCell(rowNum,(short)1,rowNum,Short.parseShort(String.valueOf(((gradeList.size()+1)*2+1))),str.toString(),"h",(short)1000);
		
			
			//培训效果分析
			this.rowNum++;
			executeCell(rowNum,(short)1,rowNum,Short.parseShort(String.valueOf(((gradeList.size()+1)*2+1))),ResourceFactory.getProperty("train.evaluationStencil.trainEffectAnalyse")+":","h",(short)1000);
			
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		
	 }
	 
	 
	 
	 public void getTableBodyContent(ArrayList gradeList,String templateid,int questionCount)throws GeneralException
	 {
		  
		 try
		 {
			 HashMap gradeValueMap=getGradeValueMap(templateid);
			 ArrayList a_pointList=(ArrayList)this.pointList.get(1);            //指标集 
			 HashMap totalMap=new HashMap();
			 int a=0;
			 for(Iterator t=a_pointList.iterator();t.hasNext();)
			 {
				 a++;
				 String[] temp=(String[])t.next();
				 
				 executeCell(rowNum,(short)1,rowNum,(short)1,a+". "+temp[1],"l");
				 HashMap pointGradeValueMap=new HashMap();
				 if(gradeValueMap!=null&&gradeValueMap.get("c_"+temp[0])!=null) {
                     pointGradeValueMap=(HashMap)gradeValueMap.get("c_"+temp[0]);
                 }
				 
				 for(int i=0;i<gradeList.size();i++)
				 {
					 LazyDynaBean abean=(LazyDynaBean)gradeList.get(i);
					 String gradeTemplateId=(String)abean.get("gradeTemplateId");
					 {
						 if(pointGradeValueMap.get(gradeTemplateId)!=null&&i==0)
						 {
							String count=(String)pointGradeValueMap.get(gradeTemplateId);
							if(count!=null&&!"".equals(count))
							{
								this.contentment+=Integer.parseInt(count);
							}
						 }
					 }
					 
					 String count="";
					 String percent="";
					 if(pointGradeValueMap.get(gradeTemplateId)!=null) {
                         count=(String)pointGradeValueMap.get(gradeTemplateId);
                     }
					 
					 
					 if(!"".equals(count)&&questionCount>0)
					 {
						
						 percent=(new BigDecimal(count)).divide(new BigDecimal(String.valueOf(questionCount)),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
						 percent=PubFunc.round(percent,1)+"%";
					 }
					 
					 if(totalMap.get(gradeTemplateId)!=null)
					 {
						int totalCount=Integer.parseInt((String)totalMap.get(gradeTemplateId));
						if(!"".equals(count)) {
                            totalCount+=Integer.parseInt(count);
                        }
						totalMap.put(gradeTemplateId,String.valueOf(totalCount));
					 }
					 else
					 {
						 if(!"".equals(count)) {
                             totalMap.put(gradeTemplateId,String.valueOf(count));
                         }
					 }
					 
					 
					 executeCell(rowNum,Short.parseShort(String.valueOf(2+i*2)),rowNum,Short.parseShort(String.valueOf(2+i*2)),count,"C");
					 executeCell(rowNum,Short.parseShort(String.valueOf(2+i*2+1)),rowNum,Short.parseShort(String.valueOf(2+i*2+1)),percent,"C");
					
				 }
//				未选择
				 
				 
				 
				 String count="";
				 String percent="";
				 if(pointGradeValueMap.get("unSelect")!=null) {
                     count=(String)pointGradeValueMap.get("unSelect");
                 } else
				 {
					 Set set=(Set)pointGradeValueMap.entrySet();
					 int num=0;
					 for(Iterator f=pointGradeValueMap.values().iterator();f.hasNext();)
					 {
						 
						num+=Integer.parseInt((String)f.next()); 
					 }
					 if(questionCount-num!=0) {
                         count=String.valueOf(questionCount-num);
                     }
					 
				 }
				 
				 
				 if(!"".equals(count)&&questionCount>0)
				 {
					
					 percent=(new BigDecimal(count)).divide(new BigDecimal(String.valueOf(questionCount)),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).toString();
					 percent=PubFunc.round(percent,1)+"%";
				 }
				 
				 if(totalMap.get("unSelect")!=null)
				 {
					int totalCount=Integer.parseInt((String)totalMap.get("unSelect"));
					if(!"".equals(count)) {
                        totalCount+=Integer.parseInt(count);
                    }
					totalMap.put("unSelect",String.valueOf(totalCount));
				 }
				 else
				 {
					 if(!"".equals(count)) {
                         totalMap.put("unSelect",String.valueOf(count));
                     }
				 }
				 executeCell(rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),count,"C");
				 executeCell(rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),percent,"C");
		
				 rowNum++;
			 }
			 
			 //总计
			 executeCell(rowNum,(short)1,rowNum,(short)1,ResourceFactory.getProperty("train.evaluationStencil.total"),"l");
			 for(int i=0;i<gradeList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)gradeList.get(i);
				 String gradeTemplateId=(String)abean.get("gradeTemplateId");
				 String count="";
				 if(totalMap.get(gradeTemplateId)!=null) {
                     count=(String)totalMap.get(gradeTemplateId);
                 }
				 
				 executeCell(rowNum,Short.parseShort(String.valueOf(2+i*2)),rowNum,Short.parseShort(String.valueOf(2+i*2+1)),count,"c");
				
			 }
			 String gradeTemplateId="unSelect";
			 String count="";
			 if(totalMap.get(gradeTemplateId)!=null) {
                 count=(String)totalMap.get(gradeTemplateId);
             }
			 executeCell(rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),rowNum,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),count,"C");
		     rowNum++;
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
		
	 }
	 
	 
	 
	 public void getTableBodyHead(ArrayList gradeList)throws GeneralException
	 {
		
		 try
		 {
			 executeCell(rowNum,(short)1,rowNum+2,(short)1,ResourceFactory.getProperty("train.evaluationStencil.evaluateItem"),"C");
			 executeCell(rowNum,(short)2,rowNum,Short.parseShort(String.valueOf((gradeList.size()+1)*2+1)),ResourceFactory.getProperty("train.evaluationStencil.evaluateResultStat"),"C");
			
			 for(int i=0;i<gradeList.size();i++)
			 {
				 LazyDynaBean abean=(LazyDynaBean)gradeList.get(i);
				 
				 executeCell(rowNum+1,Short.parseShort(String.valueOf(2+i*2)),rowNum+1,Short.parseShort(String.valueOf(2+i*2+1)),(String)abean.get("gradedesc"),"C");
				 executeCell(rowNum+2,Short.parseShort(String.valueOf(2+i*2)),rowNum+2,Short.parseShort(String.valueOf(2+i*2)),ResourceFactory.getProperty("train.evaluationStencil.frequency"),"C");
				 executeCell(rowNum+2,Short.parseShort(String.valueOf(2+i*2+1)),rowNum+2,Short.parseShort(String.valueOf(2+i*2+1)),ResourceFactory.getProperty("train.evaluationStencil.percent"),"C");
            }
			 executeCell(rowNum+1,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),rowNum+1,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),ResourceFactory.getProperty("train.evaluationStencil.unselect"),"C");
			 executeCell(rowNum+2,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),rowNum+2,Short.parseShort(String.valueOf(2+(gradeList.size())*2)),ResourceFactory.getProperty("train.evaluationStencil.frequency"),"C");
			 executeCell(rowNum+2,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),rowNum+2,Short.parseShort(String.valueOf(2+(gradeList.size())*2+1)),ResourceFactory.getProperty("train.evaluationStencil.percent"),"C");
			 
			 rowNum++;
			 rowNum++;
			 rowNum++;
			
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
	 }
	 
	 
	 
	 
	 
//	取得标题 html
	 public void getTitle(String r3101,String titleName)
	 {
		 try {
			 StringBuffer sub_html=new StringBuffer("");
			 
			 //HSSFRow row = sheet.createRow((short) rowNum);
			 HSSFRow row = sheet.getRow(rowNum);
			 if(row==null) {
                 row = sheet.createRow(rowNum);
             }
			 
			 HSSFCell cell = row.createCell((short) 5); 
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellValue(ResourceFactory.getProperty("train.evaluationStencil.trainEffectStat"));
			 ExportExcelUtil.mergeCell(sheet, rowNum,(short)5,rowNum,(short)10);
			 rowNum++;
			 //row = sheet.createRow((short) rowNum);
			 row = sheet.getRow(rowNum);
			 if(row==null) {
                 row = sheet.createRow(rowNum);
             }
			 cell = row.createCell((short)1);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellValue(ResourceFactory.getProperty("train.evaluationStencil.no")+":");
			 
			 rowNum++;
			 //row = sheet.createRow((short) rowNum);
			 row = sheet.getRow(rowNum);
			 if(row==null) {
                 row = sheet.createRow(rowNum);
             }
			 cell = row.createCell((short)1);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellValue(ResourceFactory.getProperty("train.evaluationStencil.investigateName")+":");
			 cell = row.createCell((short)2);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellValue(titleName);
			 
			 cell = row.createCell((short)8);
			 //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			 cell.setCellValue(ResourceFactory.getProperty("train.evaluationStencil.teacher")+":");
			 
			 rowNum++;
			 rowNum++;
		 } catch (Exception e) {
			 e.printStackTrace();
		}
	 }
	 
	 
	 
	 
	 /**
		 * 取得某活动调查评估的回收问卷数
		 */
		 public int getQuestionCount(String r3101,String templateid)throws GeneralException 
		 {
			int count=0;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=null;
			try
			{
				rowSet=dao.search("select count(a0100) from tra_eval_"+templateid+" where r3101='"+r3101+"'");
				if(rowSet.next())
				{
					count=rowSet.getInt(1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			return count;
		 }
	 
	 
	 public ArrayList getPerGradeTemplateList(String templateID)throws GeneralException
	 {
		 ArrayList list=new ArrayList();
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			 String per_comTable = "per_grade_template"; // 绩效标准标度
			 if(ppo.getComOrPer(templateID,"temp")) {
                 per_comTable = "per_grade_competence"; // 能力素质标准标度
             }
			 rowSet=dao.search("select grade_template_id,gradedesc from "+per_comTable+" order by gradevalue desc");
			 while(rowSet.next())
			 {
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("gradeTemplateId",rowSet.getString(1));
				 abean.set("gradedesc",rowSet.getString(2));
				 list.add(abean);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return list;
	 }
	 
	 

	 
	 public String[] getPerGradeTemplateArraye(String templateID)
	 {
		 StringBuffer gradeId=new StringBuffer("");
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			 String per_comTable = "per_grade_template"; // 绩效标准标度
			 if(ppo.getComOrPer(templateID,"temp")) {
                 per_comTable = "per_grade_competence"; // 能力素质标准标度
             }
			 rowSet=dao.search("select * from "+per_comTable+"");
			 while(rowSet.next())
			 {
				gradeId.append("/");
				gradeId.append(rowSet.getString("grade_template_id")); 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 String str=gradeId.substring(1);
		 return str.split("/");
		 
	 }
	 
	 /**
	  * 取得某活动下 各指标相应标度的选择数量
	  * @param templateID
	  * @return
	  */
	 public HashMap getGradeValueMap(String templateID)throws GeneralException
	 {
		 HashMap map=new HashMap();
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet rowSet=null;
		 try
		 {
			 HashMap rankMap=getPointRank(templateID);	
			 
			 this.pointList=getPerPointList(templateID);
			 ArrayList pointInfoList=(ArrayList)pointList.get(0);         //指标详细集
			 ArrayList pointList0=(ArrayList)pointList.get(1);             //指标集 
			 String[]  perGradeTemplate=getPerGradeTemplateArraye(templateID);      //模版标度
			 
			 rowSet=dao.search("select * from TRA_EVAL_"+templateID+" where r3101='"+this.r3101+"'");
			 while(rowSet.next())
			 {
				 for(Iterator t=pointList0.iterator();t.hasNext();)
				 {
					 String[] temp = (String[])t.next();
					 if(rowSet.getString("C_"+temp[0])!=null)
					 {
						 double score=rowSet.getDouble("C_"+temp[0]);
						 score=score/Double.parseDouble((String)rankMap.get(temp[0]));  // 分值除以权重
						 HashMap pointValueMap=new HashMap();
						 if(map.get("c_"+temp[0])!=null)
						 {
							 pointValueMap=(HashMap)map.get("c_"+temp[0]);
							 
						 }
						 map.put("c_"+temp[0],getPointValueMap(pointValueMap,perGradeTemplate,score,pointInfoList,temp));
					 }
					 else
					 {	
						 HashMap pointValueMap=new HashMap();
						 if(map.get("c_"+temp[0])!=null)
						 {
							 pointValueMap=(HashMap)map.get("c_"+temp[0]); 
						 }
						 if(pointValueMap.get("unSelect")!=null)
						 {
							 int count=Integer.parseInt((String)pointValueMap.get("unSelect"));
							 pointValueMap.put("unSelect",String.valueOf(++count));
						 }
						 else
						 {
							 pointValueMap.put("unSelect","1");
						 }
						 map.put("c_"+temp[0],pointValueMap);
					 }
				}
					 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 return map;
	 }
	
	 

	 
	 public HashMap getPointValueMap(HashMap pointValueMap,String[] perGradeTemplate,double score,ArrayList pointInfoList,String[] temp)
	 {
		 String[] temp2=null;
		 for(int i=0;i<pointInfoList.size();i++)
		 {
			 String[] temp1 = (String[])pointInfoList.get(i);
			 if (temp[0].equals(temp1[1])) {				
				 	boolean isEnd=true;
				 	if((i+1)<pointInfoList.size())
				 	{
				 		String[] temp_bak = (String[])pointInfoList.get((i+1));
				 		if(temp_bak[1].equalsIgnoreCase(temp[0])) {
                            isEnd=false;
                        }
				 	}
					if (score<= Float
							.parseFloat(temp1[9])*Float
							.parseFloat(temp1[8])
							&&(score > Float
							.parseFloat(temp1[7])*Float
							.parseFloat(temp1[8])||(isEnd&&score >=Float
									.parseFloat(temp1[7])*Float
									.parseFloat(temp1[8]))))
					{
						temp2 = temp1;
						break;
					}
				}
		 }
		 if(temp2!=null)
		 {
			 for(int i=0;i<perGradeTemplate.length;i++)
			 {
				 String gradeId=perGradeTemplate[i];
				 if(gradeId.equalsIgnoreCase(temp2[5]))
				 {
					 if(pointValueMap.get(gradeId)!=null)
					 {
						 int count=Integer.parseInt((String)pointValueMap.get(gradeId));
						 pointValueMap.put(gradeId,String.valueOf(++count));
					 }
					 else
					 {
						 pointValueMap.put(gradeId,"1");
					 }
					
					 break;
				 }
			 }
		 }
		 return pointValueMap;
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
			if(ppo.getComOrPer(templateID,"temp")) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			HashMap   map2=new HashMap();
			String     sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
							+" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='"+templateID+"'  order by pp.seq";  //pi.seq,
			try
			{
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
					String[] temp=new String[13];
					for(int i=0;i<13;i++)
					{
						if(i==2) {
                            temp[i]=Sql_switcher.readMemo(rowSet,"pointname");
                        } else if(i==4) {
                            temp[i]=Sql_switcher.readMemo(rowSet,"gradedesc");
                        } else {
                            temp[i]=rowSet.getString(i+1);
                        }
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
				rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");	  //pi.seq,
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
				parameterSetBo.anaylseTemplateTable(apointList,layItemList,itemPoint,templateID,"");
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
						if(tt[1].toLowerCase().equals(temp)) {
                            pointGrageList.add(tt);
                        }
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
	 
	 
	 /**
		 * 根据考核模版得到各指标的权重
		 * @param templateID
		 * @return
		 */
		public HashMap getPointRank(String templateID)
		{
			HashMap rankMap=new HashMap();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=null;
			try
			{
				rowSet=dao.search("select point_id,per_template_point.rank  from per_template_item,per_template_point where per_template_item.item_id=per_template_point.item_id  and  template_id='"+templateID+"'");
				while(rowSet.next())
				{
					rankMap.put(rowSet.getString("point_id"),rowSet.getString("rank"));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return rankMap;
		}
	 
		 //得到培训班的人数
		 public int getTrainClassMenCount(String r3101)throws GeneralException 
		 {
			 int count=0;
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=null;
			 try
			 {
				 rowSet=dao.search("select count(*) from r40 where r4005='"+r3101+"' and r4013 not in('01','02','07','08')");
				 if(rowSet.next())
				 {
						count=rowSet.getInt(1);
				 }
			 }
			 catch(Exception e)
			 {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
			 }
			return count;
		 }
	 
		 
		 
		 
		 
		 
		 
		 
		 
		 
	public String getR3101() {
		return r3101;
	}

	public void setR3101(String r3101) {
		this.r3101 = r3101;
	} 
	
	
	
	
	

}
