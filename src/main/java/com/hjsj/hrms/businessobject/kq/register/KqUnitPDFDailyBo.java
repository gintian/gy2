package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * <p>
 * Title:KqUnitPDFDailyBo
 * </p>
 * <p>
 * Description:生成pdf考勤簿
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-05-23
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class KqUnitPDFDailyBo {
	
	private Connection conn = null;
	//首钢增加 true展现本月出缺勤情况统计小计，否则原始
	private boolean kqtablejudge; 
	// 当前页面设置
	private ReportParseVo parsevo = null;
	// 登录用户
	private UserView userView = null;
	// 报表id
	private HashMap codemap = new HashMap();
	
	// 最大列数
	private int maxColnum = 17;
	
	HashMap styleMap = new HashMap();
	
	private float rate = 0.24f;
	
    private String curTab = "";	
	
	
	public KqUnitPDFDailyBo(Connection conn) {
		this.conn = conn;		
	}
	
	public KqUnitPDFDailyBo(Connection conn,UserView userView) {
		this.conn = conn;	
		this.userView = userView;
	}
		
	/**
	 * 生成excel考勤簿
	 * @param a0100 String 人员编号
	 * @param kq_duration String 考勤期间
	 * @param ReportParseVo String 当前页面设置对象
	 * @param userView UserView 用户
	 * @param nbase String 人员库
	 * @param name String 姓名
	 * @return String excle文件名
	 * @throws GeneralException
	 */
	public String getKqReportPDF(String a0100, String kq_duration, 
				ReportParseVo parsevo, String nbase, 
				String name, String reportId)throws GeneralException{
		KqViewDailyBo kqviewdailybo = new KqViewDailyBo();
		//考勤表：true=展现本月出缺勤情况统计小计 false=不展现
		this.kqtablejudge = kqviewdailybo.getkqtablejudge(this.conn);  
		this.parsevo = parsevo;
		
		// 生成的文件名
		String context = "";
		if (parsevo.getTitle_fw() != null && parsevo.getTitle_fw().length() > 0) {
			context = parsevo.getTitle_fw();
		} else {
			context = parsevo.getName();
		}
		String filename = userView.getUserName()+ "_" + context.trim() + ".pdf";
		// 创建document
		Document document = null;
		// 纸张宽度
		float width = Float.parseFloat(parsevo.getWidth()) / this.rate;
		// 纸张高度
		float height = Float.parseFloat(parsevo.getHeight()) / this.rate;
		
		Rectangle rec = new Rectangle(width, height);
		FileOutputStream fileOut = null;
		try {
			// 页边距 上
			float top = getPx(parsevo.getTop());
			// 页边距 下
			float bottom = getPx(parsevo.getBottom());
			// 页边距 左
			float left = getPx(parsevo.getLeft());
			// 页边距 右
			float right = getPx(parsevo.getRight());
			// 宽度
			String factWidth = getFactWidth();
			// 日期生成表头
			ArrayList datelist = getDateList(this.conn, kq_duration); 
			if (datelist.size() < 31) {
				// 最大列数
				maxColnum = 16;
			}
			// 每个格子的宽度
			double avgWidth = 1d / this.maxColnum;
			
			// 纸是横向还是纵向
			if ("1".equals(parsevo.getOrientation())) {// 横向设置				
				document = new Document(rec.rotate());
			} else {
				document = new Document(rec);
			}
			
			document.setMargins(left, right, top, bottom);
			
			// 书写器
			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") 
					+ filename);
			// 57752 59327 实例化 PdfWriter
			PdfWriter.getInstance(document, fileOut);
			// 获得考勤项目参数,底部的符号；
			KqReportInit kqReprotInit = new KqReportInit(this.conn);			
			ArrayList item_list = kqReprotInit.getKq_Item_listPdf();
			
			float[] widths = new float[this.maxColnum];
			for (int i = 0; i < this.maxColnum; i++) {
				widths[i] = Float.parseFloat(avgWidth + "");
			}
			
			PdfPTable table = new PdfPTable(widths);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.setTotalWidth(Float.parseFloat(factWidth));
			document.open();
			// 得到表头
			getTableTitle(table, name);			  
			getTableHead(table,a0100,kq_duration,datelist,nbase);
			//body信息	
			getBodyExcel(table,a0100,nbase,name,datelist,kqtablejudge,item_list,kq_duration);
			//得到表尾数据
			getTileHtml(table, item_list); 
			table.setWidthPercentage(100);
			document.add(table);
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
		    PubFunc.closeIoResource(document);
		    PubFunc.closeIoResource(fileOut);
		}
		 
		 return filename;
	}
	
	private float getPx(String length) throws Exception, Exception {
		String unit = parsevo.getUnit();
		if ("px".equalsIgnoreCase(unit)) {
			return  Float.parseFloat(round(length,0));
		}  else {
			float f = Float.parseFloat(length) / this.rate;
			return Float.parseFloat(round(f + "", 0));
		}
	}
	
	/**
	 * 得到标题内容
	 * 
	 * */	 
	private String getTableTitle(PdfPTable table ,String username) {
        // 高度
        float height = Float.parseFloat(getPxFormMm(parsevo.getTitle_h()));
        ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf();
        // 创建标题的字体
        Font titleFont = daily.getFont(parsevo.getTitle_fn(), 
        		parsevo.getTitle_fb(), parsevo.getTitle_fi(), 
        		parsevo.getTitle_fu(), parsevo.getTitle_fz(), 
        		 "");
        
        
        // 标题
        String title = "";
		
		if(parsevo.getTitle_fw() == null || parsevo.getTitle_fw().length() <= 0) {
			title = username + "签到簿";
		}else
		{
			title = parsevo.getTitle_fw();
		}
		
		Paragraph graph = null;
		if (null != titleFont) {
            graph = new Paragraph(title, titleFont);
        } else {
            graph = new Paragraph(title);
        }
				
		PdfPCell cell = new PdfPCell(graph);
		cell.setMinimumHeight(height);
		cell.setFixedHeight(height);
		cell = daily.excecute(cell, 7, daily.getLtrb("1"));
		cell.setColspan(this.maxColnum);
		table.addCell(cell);
		       
        return title;
	}
	
	/**
	 * 
	 * @param codea0100
	 * @param userbase 人员库
	 * @param username
	 * @param datelist 日期1-31号
	 * @param kqtablejudge =true 展现本月出勤小计
	 * @param item_list 符号
	 * @param kq_duration 2009-11
	 * @return
	 * @throws GeneralException
	 */
	private void getBodyExcel(PdfPTable table, String codea0100,String userbase,String username,ArrayList datelist,boolean kqtablejudge,ArrayList item_list,String kq_duration)throws GeneralException
	{
		// 高度
		int body_height = Integer.parseInt(parsevo.getBody_fz())+13; 
		
		// 创建标题的字体
		ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf(this.conn);
        Font headFont = daily.getFont(parsevo.getBody_fn(), 
        		parsevo.getBody_fb(), parsevo.getBody_fi(), 
        		parsevo.getBody_fu(), parsevo.getBody_fz(), 
        		"");
    	
        ArrayList dataList=getOneA0100Data(codea0100,userbase,datelist,body_height,item_list);
        ArrayList dealList = new ArrayList();
        for (int i = 0; i < dataList.size(); i++) {
        	ArrayList fuList = (ArrayList) dataList.get(i);
        	Paragraph grap = new Paragraph();
        	for (int j = 0; j < fuList.size() - 1; j++) {
        		ArrayList temp = (ArrayList) fuList.get(j);
        		String fu = (String) temp.get(2);
        		String color = (String) temp.get(3);
        		Font font = daily.getFont(parsevo.getBody_fn(), 
                		parsevo.getBody_fb(), parsevo.getBody_fi(), 
                		parsevo.getBody_fu(), parsevo.getBody_fz(), 
                		color);
        		grap.add(new Chunk(fu, font));
        	
        	}
        	dealList.add(grap);
        }
        
        // 日期
        table.addCell(getCell(headFont, body_height, "日期"));
        
    	// 将日期写到pdf中，1-15日
    	for (int i = 0; i < datelist.size() ; i++) {
    		if (i < 15) {
    			CommonData data = (CommonData) datelist.get(i);
    			String value = data.getDataValue();    			
    			table.addCell(getCell(headFont, body_height, value));
    		}  		
    	}
    	
    	if (this.maxColnum == 17) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	// 签名
    	table.addCell(getCell(headFont, body_height, "签名"));
    	
        // 将签名写到pdf中，1-15日
    	for (int i = 0; i < datelist.size() ; i++) {
    		if (i < 15) {
    			table.addCell(getCell(headFont, body_height, username));
    		}
    		   		
    	}
    	
    	if (this.maxColnum == 17) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	// 数据
    	table.addCell(getCell(headFont, body_height, "考勤"));
    	 // 将考勤数据写到pdf中，1-15日
    	for (int i = 0; i < dealList.size() ; i++) {
    		if (i < 15) {
    			Paragraph grap = (Paragraph) dealList.get(i);
    			table.addCell(getCell(body_height, grap));
    		}
    		   		
    	}
    	if (this.maxColnum == 17) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	
    	
    	// 日期
    	table.addCell(getCell(headFont, body_height, "日期"));
    	
        
    	// 将日期写到pdf中，16日以后的数据
    	for (int i = 0; i < datelist.size() ; i++) {
    		if (i > 14) {
    			CommonData data = (CommonData) datelist.get(i);
    			String value = data.getDataValue();    			
    			table.addCell(getCell(headFont, body_height, value));
    		}  		
    	}
    	
    	for (int i = 0; i < this.maxColnum -(datelist.size() - 15) - 1; i++) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	
    	// 签名
    	table.addCell(getCell(headFont, body_height, "签名"));
    	// 将签名写到pdf中，16日以后
    	for (int i = 0; i < datelist.size() ; i++) {
    		if (i > 14) {
    			table.addCell(getCell(headFont, body_height, username));
    		}
    		   		
    	}
    	
    	for (int i = 0; i < this.maxColnum -(datelist.size() - 15) - 1; i++) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	// 数据
    	table.addCell(getCell(headFont, body_height, "考勤"));
    	 // 将考勤数据写到pdf中，1-15日
    	for (int i = 0; i < dealList.size() ; i++) {
    		if (i > 14) {
    			Paragraph grap = (Paragraph) dealList.get(i);
    			table.addCell(getCell(body_height, grap));
    		}
    		   		
    	}
    	
    	for (int i = 0; i < this.maxColnum -(datelist.size() - 15) - 1; i++) {    		
            table.addCell(getCell(headFont, body_height, " "));
    	}
    	
    	if(kqtablejudge) {   		
    		
    		KqViewDailyBo kqviewdailybo =new KqViewDailyBo();
        	ArrayList kqq03list=kqviewdailybo.getKqBookItemList();
        	ArrayList list = getOneA0100Value(codea0100,userbase,kq_duration,body_height,kqq03list);
        	ArrayList colList = (ArrayList) list.get(0);
        	ArrayList valList = (ArrayList) list.get(1);
        	
        	// 行数
        	int rows = (colList.size() - 1) / this.maxColnum + 1;
        	// 最后需要填的空白数
        	int nullNum = 0;
        	if (colList.size() % this.maxColnum != 0) {
        		nullNum = this.maxColnum - colList.size() % this.maxColnum;
        	}
        	
        	for (int i = 0; i < rows; i++) {
        		for (int j = 0; j < colList.size(); j++) {
        			if (j >= i * this.maxColnum && j < (i + 1) * this.maxColnum) {
        				String value = (String) colList.get(j);
        				table.addCell(getCell(headFont, body_height, value));
        			}
        		}
        		
        		if (i == rows - 1) {
        			for (int j = 0; j < nullNum; j++) {
        				table.addCell(getCell(headFont, body_height, " "));
        			}
        		}
        		
        		for (int j = 0; j < valList.size(); j++) {
        			if (j >= i * this.maxColnum && j < (i + 1) * this.maxColnum) {
        				String value = (String) valList.get(j);
        				table.addCell(getCell(headFont, body_height, value));
        			}
        		}
        		
        		if (i == rows - 1) {
        			for (int j = 0; j < nullNum; j++) {
        				table.addCell(getCell(headFont, body_height, " "));
        			}
        		}
        	}
  
        	
    	}

    	
	}
	
	private PdfPCell getCell(Font font, float height, String value) {
		ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf(this.conn);
        Paragraph grap = new Paragraph(value, font);
        PdfPCell cell = new PdfPCell(grap);
        cell.setMinimumHeight(height);
//        cell.setFixedHeight(height);
        cell = daily.excecute(cell, 7, daily.getLtrb("1"));
        cell.setColspan(1);
        return cell;
	}
	private PdfPCell getCell(float height, Paragraph value) {
		ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf(this.conn);
        PdfPCell cell = new PdfPCell(value);
        cell.setMinimumHeight(height);
        cell = daily.excecute(cell, 7, daily.getLtrb("1"));
        cell.setColspan(1);
        
        return cell;
	}
	public String getBodyHolsEndHtml(ArrayList kqq03list)
	{
		StringBuffer bodyheadhtml = new StringBuffer();
		String sytle=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz());
    	int body_height=Integer.parseInt(parsevo.getBody_fz())+13;
    	bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","假别",getStyleName("6")));
    	String kqtable=SystemConfig.getPropertyValue("kqtable");//用来控制考勤表头竖型展现 0和null都是正常展现
    	for(int i=17;i<=33;i++)
    	{
    		if(i<=kqq03list.size())
    		{
    			for(int p=16;p<kqq03list.size();p++)
        		{
    				if(p<=31)
    				{
    					FieldItem fielditem=(FieldItem)kqq03list.get(p);		
            			String name = fielditem.getItemdesc();
            			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
            			{
            				if(p<=31)
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}else
            			{
            				if(p<=31)
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("6")));	
                        	}else
                        	{
                        		bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"",name,getStyleName("1")));
                        		i++;
                        		break;
                        	}
            			}
//                    	System.out.println("ppp = "+p);
                    	i++;
    				}
        			
        		}
    		}else
    		{
    			if(kqtable!=null&&kqtable.length()>0&& "1".equals(kqtable))
    			{
    				if(i==33)
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTables(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}else
    			{
    				if(i==33)
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("1")));
        			}else
        			{
        				bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","",getStyleName("6")));
        			}
    			}
    		}
//    		System.out.println("123 = "+i);
    	}
    	return bodyheadhtml.toString();
	}
	/**
	 * 本月出勤小计
	 */
	private ArrayList getOneA0100Value(String codea0100,String userbase,String kq_duration,int body_hieght,ArrayList kqq03list) {
		ArrayList list = new ArrayList();
		StringBuffer column = new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList colList = new ArrayList();
		for(int i = 0; i < kqq03list.size(); i++) {
			FieldItem fielditem = (FieldItem)kqq03list.get(i);
    		if(!"i9999".equals(fielditem.getItemid())) {
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem);
   	    		if (colList.size() % this.maxColnum == 0) {
   	    			colList.add("假别");
   	    			colList.add(fielditem.getItemdesc());
   	    		} else {
   	    			colList.add(fielditem.getItemdesc());
   	    		}
   	    	}
    	}
		
		list.add(colList);
		ArrayList valList = new ArrayList();
		
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++) {
         		StringBuffer sql_on_a0100=new StringBuffer();
         		FieldItem fielditem = (FieldItem)columnlist.get(t);
         		String itd=fielditem.getItemid();
         		
         		sql_on_a0100.append("select " + itd + " as one");
                sql_on_a0100.append(" from Q05");
                if (this.getCurTab().toLowerCase().contains("_arc")) {
                    sql_on_a0100.append("_arc");
                }
                sql_on_a0100.append(" where Q03Z0='").append(kq_duration).append("'");
                sql_on_a0100.append(" and nbase='").append(userbase).append("'");
                sql_on_a0100.append(" and A0100='").append(codea0100).append("'");
                
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next()) {
        			itemid = rowSet.getString("one");
        			if(itemid != null) {
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid)) {
        					itemid="";
        				} else {
        					if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
        						int num = fielditem.getDecimalwidth();
								if(itemid.indexOf(".") != -1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid,num);
								} else {
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										} else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid,num);
									
								}
        					} else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									if (!codemap.containsKey(setid+itemid)) {
										String codesetid = itemid;
										itemid = AdminCode.getCodeName(setid, itemid);
										codemap.put(setid+codesetid, itemid);
									} else {
										itemid = (String) codemap.get(setid+itemid);
									}
								}
							} 
        				}
        			} else {
        				itemid = "";
        			}
        			
        			if (valList.size() % this.maxColnum == 0) {
        				valList.add("工日");
        				valList.add(itemid);
        				
        			} else {
        				valList.add(itemid);
        			}
        			
        		}else {
        			if (valList.size() % this.maxColnum == 0) {
        				valList.add("工日");
        				valList.add("");
        				
        			} else {
        				valList.add("");
        			}
        		}
         	}

         	list.add(valList);
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		PubFunc.closeDbObj(rowSet);
		 } 
    	return list;
	}
	
	/**
	 * 转换成字体布局字符
	 */
	private String[] transAlign(int Align)
	{
		String[] temp=new String[2];
		if(Align==0)
		{
			temp[0]="top";
			temp[1]="left";
		}
		else if(Align==1)
		{
			temp[0]="top";
			temp[1]="center";
		}
		else if(Align==2)
		{
			temp[0]="top";
			temp[1]="right";
		}
		else if(Align==3)
		{
			temp[0]="bottom";
			temp[1]="left";
		}
		else if(Align==4)
		{
			temp[0]="bottom";
			temp[1]="center";
		}
		else if(Align==5)
		{
			temp[0]="bottom";
			temp[1]="right";
		}
		else if(Align==6)
		{
			temp[0]="middle";
			temp[1]="left";
		}
		else if(Align==7)
		{
			temp[0]="middle";
			temp[1]="center";
		}
		else if(Align==8)
		{
			temp[0]="middle";
			temp[1]="right";
		}		
		return temp;		
	}

	/**
	 * 计算表格实际总宽度
	 * 首钢更改，注销是原始的 
	 * */
	private String getFactWidth()
	{
		String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
		if("1".equals(parsevo.getOrientation().trim()))
		{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}else{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}
	}
	 /**
     * 转换，毫米转换为像素
     * */
    private String getPxFormMm(String value)
    {
    	if(value==null || value.length()<=0) {
            return "0";
        }
    	String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
    	if("mm".equals(unit))
		{
    		double dv=Double.parseDouble(value)/this.rate;
    		return KqReportInit.round(dv+"",0);
		}else
		{
			return KqReportInit.round(value,0);
		}
    }

	 /**
	 * 计算在规定的字数中，一串字符，有多少行
	 * */
    private int getNumRow(int strlen)
	{
		 int factwidth=Integer.parseInt(getFactWidth());
		 
			 int ss=strlen/factwidth;
			 int dd=strlen%factwidth;
	  	     if(dd!=0)
	  	     {
	  	    	ss=ss+1;
	  	     } 
	  	   return ss; 		 	   
	}
	
	/**
	 * 
	 * 得到表头内容 
	 *   #p（页码）  #c 总页数  #e 制作人  #u 制作人所在的单位  #d 日期  #t  时间  #fn宋体 字体名称  #fz15    字体大小
	 *   #fb[0|1]  黑体  #fi[0|1]   斜体  #fu[0|1]   下划线  #pr[0|1]  页行数
	 * 
	 * @param codea0100 String 人员编号
	 * @param coursedate Sring 考勤期间
	 * @param curpage int 当前页 
	 * @param sum_page int 总共页数
	 * @param datelist ArrayList 日期
	 * @param userbase String 人员库
	 * @throws GeneralException
	 */
    private void getTableHead(PdfPTable table, String codea0100,String coursedate,ArrayList datelist,String userbase)throws GeneralException {
      
        String [] codeitem = getCodeItemDesc(codea0100,userbase);
        float head_height = Float.parseFloat(getPxFormMm(parsevo.getHead_h()));
        ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf();
        
        // 创建标题的字体
        Font headFont = daily.getFont(parsevo.getHead_fn(), 
        		parsevo.getHead_fb(), parsevo.getHead_fi(), 
        		parsevo.getHead_fu(), parsevo.getHead_fz(), 
        		"");
        Paragraph  grap = null;
        PdfPCell cell = null;
 	   	
        
        try { 
        	
            
           /**单位**/  
           String dv_content = "";
           if(codeitem[0] == null || codeitem[0].length() <= 0) {
        	   dv_content = "单位：";
           } else {
        	   dv_content = "单位：" + codeitem[0];
           }
           
           grap = new Paragraph(dv_content, headFont);
           cell = new PdfPCell(grap);
           cell.setMinimumHeight(head_height);
           cell.setFixedHeight(head_height);
           cell = daily.excecute(cell, 7, daily.getLtrb("1"));
           cell.setColspan(this.maxColnum / 3);
           table.addCell(cell);
           
          
           /**部门*/   
           String bm_content = "";
           if(codeitem[1] == null || codeitem[1].length() <= 0) {
        	   bm_content = "部门：";
           } else {
        	   bm_content = "部门：" + codeitem[1];
           }
           
           grap = new Paragraph(bm_content, headFont);
           cell = new PdfPCell(grap);
           cell.setMinimumHeight(head_height);
           cell.setFixedHeight(head_height);
           cell = daily.excecute(cell, 7, daily.getLtrb("1"));
           cell.setColspan(this.maxColnum / 3);
           table.addCell(cell);
           
            /**日期*/      
           CommonData vo = (CommonData)datelist.get(0);	           
           String start_date = vo.getDataName();
           vo = (CommonData)datelist.get(datelist.size()-1);	 
           String end_date = vo.getDataName();    
           String da_content = coursedate+" ("+start_date+"~"+end_date+")";
           
           grap = new Paragraph(da_content, headFont);
           cell = new PdfPCell(grap);
           cell.setMinimumHeight(head_height);
           cell.setFixedHeight(head_height);
           cell = daily.excecute(cell, 7, daily.getLtrb("1"));
           cell.setColspan(this.maxColnum - (this.maxColnum / 3) * 2);
           table.addCell(cell);

           
           if ("#e".equals(parsevo.getHead_e().trim()) || "#u".equals(parsevo.getHead_u().trim()) 
        		   || "#d".equals(parsevo.getHead_d().trim()) || "#t".equals(parsevo.getHead_t().trim()) 
        		   || "#p".equals(parsevo.getHead_p().trim()) || "#c".equals(parsevo.getHead_c().trim())){

        	   // 计算有几个值
        	   int i = 0;
        	   
        	   if("#u".equals(parsevo.getHead_u().trim())) {
        		   i ++;
        	   }
        	   if("#e".equals(parsevo.getHead_e().trim())) {
        		   i ++;
        	   }
        	   if("#d".equals(parsevo.getHead_d().trim())) {
        		   i ++;
        	   }
        	   if("#t".equals(parsevo.getHead_t().trim())) {
        		   i ++;
        	   }
        	   if("#p".equals(parsevo.getHead_p().trim())) {
        		   i ++;
        	   }
        	   if("#c".equals(parsevo.getHead_c().trim())) {
        		   i ++;
        	   }
        	   
        	   int j = 0;
        	   
        	   // 制作人
        	   if("#e".equals(parsevo.getHead_e().trim())) {
        	   
        		   String e_str = "制作人: "+this.userView.getUserFullName();
        		   
        		   grap = new Paragraph(e_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j - 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                   table.addCell(cell);
                   
 
        	   }
        	   
        	   // 制作人单位
        	   if("#u".equals(parsevo.getHead_u().trim())) {
        	   
        		   String u_code = "";
        		   if(!userView.isSuper_admin()) {
        			   if(userView.getUserOrgId() != null 
        					   && userView.getUserOrgId().trim().length() > 0) {
        				   u_code = userView.getUserOrgId();
        			   } else {
        				   u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        			   }
        		   }
        		   String [] u_codeitem = getCodeItemDesclow(u_code);
        		   String u_str = "制作人单位: "+u_codeitem[0];
        	   
        		   grap = new Paragraph(u_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                   
                   table.addCell(cell);
        	   }
        	   
        	   // 制作日期
        	   if("#d".equals(parsevo.getHead_d().trim())) {
        	   
        		   String d_str = "制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
        		   grap = new Paragraph(d_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j - 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                   table.addCell(cell);  
        	   }
        	   
        	   // 制作时间
        	   if("#t".equals(parsevo.getHead_t().trim())) {

        		   String t_str = "时间: "+PubFunc.getStringDate("HH:mm:ss");
        		   grap = new Paragraph(t_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j - 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                   table.addCell(cell);
        	   }
        	   
        	   // 页码
        	   if("#p".equals(parsevo.getHead_p().trim())) {
        		   String p_str = "页码:1";
        		   grap = new Paragraph(p_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j - 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                  
                   table.addCell(cell);
        		   
        	   }
        	   
        	   // 总页码
        	   if("#c".equals(parsevo.getHead_c().trim())) {
        	          	   
	        	   String c_str = "总页码:1";
	        	   grap = new Paragraph(c_str, headFont);
                   cell = new PdfPCell(grap);
                   cell.setMinimumHeight(head_height);
                   cell.setFixedHeight(head_height);
                   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
                   j ++;
                   if (i == j) {
                	   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j - 1));
                   } else {
                	   cell.setColspan(this.maxColnum / i);
                   }
                   
                   table.addCell(cell);
        	   }
        	   
        	 
           
           }
        } catch(Exception e) {
			e.printStackTrace();
		}
    }
	/**
	 * 通过code得到codeItemDesc
	 * */
    private String[] getCodeItemDesc(String code,String userbase)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String sql="select b0110,e0122 from "+userbase+"A01 where A0100='"+code+"'";
	  String B0110="";
	  String E0122="";
	  String desc="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		B0110 = rowSet.getString("b0110");
	    		E0122 = rowSet.getString("e0122");
	    	}	  
	    	PubFunc.closeDbObj(rowSet);
	    	
	    	if(!"".equals(B0110)||B0110.length()>0)
	    	{
	    		String sqlb ="select codeitemdesc from organization where  codeitemid='"+B0110+"'";
	    		rowSet=dao.search(sqlb);
	    		if(rowSet.next())
	    		{
	    			desc=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc)||desc.length()>0)
	    		{
	    			codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    	if(!"".equals(E0122)||E0122.length()>0)
	    	{
	    		String sqle ="select codeitemdesc from organization where  codeitemid='"+E0122+"'";
	    		rowSet=dao.search(sqle);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc2)||desc2.length()>0)
	    		{
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	     {
				PubFunc.closeDbObj(rowSet);
		 } 
	   return codeItemDesc;
	}
	/*
	 * 处理页面显示虚线
	 */
    private String getStyleName(String temp)
	{
		//处理虚线	L,T,R,B,
	    String style_name="RecordRow_self";
	    if("0".equals(temp))
	    {
	    	style_name="RecordRow_self_l";
	    }
	    else if("1".equals(temp))
	    {
	    	style_name="RecordRow_self_t";
	    }
	    else if("2".equals(temp))
	    {
	    	style_name="RecordRow_self_r";
	    }
	    else if("3".equals(temp))
	    {
	    	style_name="RecordRow_self_b";
	    }else if("4".equals(temp))
	    {
	    	style_name="RecordRow_self_two";
	    }else if("5".equals(temp))
	    {
	    	style_name="RecordRow_self_t_l";
	    }else if("6".equals(temp))
	    {
	    	style_name="RecordRow_self_t_r";
	    }
	    
		return style_name;
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    private String getFontStyle(String fn,String fi,String fu,String fb,String fz)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
    private String executeTable(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-break:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		
		if (context == null || context.trim().length() <= 0) {
			tempTable.append("&nbsp;");
		}
		
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
			tempTable.append(context);	
		}  
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	

	//字体竖型排
    private String executeTables(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-wrap:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		if (context == null || context.length() <= 0) {
			tempTable.append("&nbsp;");
		}
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
			for(int p=0;p<context.length();p++)
			{
				
				String d =context.substring(p,p+1);
				tempTable.append(d);
				tempTable.append("<br>");
			}
		} 
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	
	
	/**
	 * 通过考勤期间得到考勤日期
	 * @param conn Connection 数据库连接
	 * @param coursedate String 当前考勤期间
	 * @return ArrayList<CommonData>
	 * @throws GeneralException
	 */
    private ArrayList getDateList(Connection conn, String coursedate) throws GeneralException {
		ArrayList dateList = new ArrayList();
		RowSet rowSet = null;
		String[] date = coursedate.split("-");
		String kq_year = date[0];
		String kq_duration = date[1];
		String kq_start;
		String kq_dd;
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT kq_start,kq_end FROM kq_duration where kq_year='");
		sql.append(kq_year);
		sql.append("'and kq_duration='");
		sql.append(kq_duration);
		sql.append("'");
		
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(sql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				int spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				SimpleDateFormat format2 = new SimpleDateFormat("dd");
				
				for (int i = 0; i <= spacedate; i++) {	          	  
            	  	CommonData vo = new CommonData();									
					kq_start = format1.format(d1);
					kq_dd = format2.format(d1);
					vo.setDataName(kq_start);
					vo.setDataValue(kq_dd);
					dateList.add(vo);					
					d1 = DateUtils.addDays(d1,1);					
				}				
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
		} finally {
			PubFunc.closeDbObj(rowSet);
		} 
		
		return dateList;
  }
  /**
	 * 通过code得到codeItemDesc
	 * */
    private String[] getCodeItemDesclow(String code)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select codeitemdesc,parentid from organization where codeitemid='"+code+"'";
	  String desc1="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		desc1=rowSet.getString("codeitemdesc");
	    		if(desc1==null||desc1.length()<=0) {
                    desc1="";
                }
	    		parentid=rowSet.getString("parentid");
	    		PubFunc.closeDbObj(rowSet);
	    		
	    		String sqlp="select codeitemdesc from organization where codeitemid='"+parentid+"'";
	    		rowSet=dao.search(sqlp);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
		    		if(desc2==null||desc2.length()<=0) {
                        desc2="";
                    }
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");
	    		}
	    	}	  
	    	if(desc2.equals(desc1))
			{
				codeItemDesc[0]="";
			}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	     {
			PubFunc.closeDbObj(rowSet);
		} 
	   return codeItemDesc;
	}
	/**
     * 通过一个员工编号得到该员工考勤期间的数据
     * file=1 的时候输出 1到15号
     * file=2 的时候输出 16到最后
     *   姓名在这里加链接
     * */ 
    private ArrayList getOneA0100Data(String codea0100,String userbase,ArrayList datelist,
			int body_hieght,ArrayList item_list)throws GeneralException {
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	
    	StringBuffer column = new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	
    	for(int i = 0; i < fielditemlist.size(); i++) {
    		FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     
   	    	if(!"i9999".equals(fielditem.getItemid())) {
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
   		}
    	
    	
    	//开始时间
    	CommonData start_vo = (CommonData)datelist.get(0);		
        String start_date=start_vo.getDataName();
        
        //结束时间	
        start_vo = (CommonData)datelist.get(datelist.size()-1);
        String end_date = start_vo.getDataName();     
    	
    	ContentDAO dao = new ContentDAO(this.conn);            
        RowSet rowSet=null;
        ArrayList dataList = new ArrayList();
        try{
        	String sql_one_a0100 = selcet_kq_one_emp(codea0100,userbase,start_date,end_date,column.toString());
         	rowSet=dao.search(sql_one_a0100 + " order by q03z0 ");
         	HashMap kq_item_map = new HashMap();
         	
         	while(rowSet.next()) {
         		Paragraph graph = new Paragraph();
         		String q03z0=rowSet.getString("q03z0").trim();     
        	    ArrayList list =new ArrayList();
        	    ArrayList z1_list=new ArrayList();
        	    ArrayList scq_list=new ArrayList();
        	    HashMap kq_item_all = this.querryKq_item();
        	    int rownum = 0;
        	    for(int i = 0; i < fielditemlist.size(); i++) {
        	    	FieldItem fielditem=(FieldItem)fielditemlist.get(i);
        	    	if("N".equals(fielditem.getItemtype()) && !"i9999".equals(fielditem.getItemid())) {
        	    		if("q03z1".equalsIgnoreCase(fielditem.getItemid())||(fielditem.getItemdesc().indexOf("出勤")!=-1
        	    				||fielditem.getItemdesc().indexOf("出勤率")!=-1)) {
        	    			if("q03z1".equalsIgnoreCase(fielditem.getItemid())) {
        	    				double  dv=rowSet.getDouble(fielditem.getItemid());
        	    				if(dv != 0){
        	    					String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
        	    					z1_list.add(kq_item);
        	    					z1_list.add(new Double(dv));             			        	
        	    				}
        	    			} else {
        	    				double  dv=rowSet.getDouble(fielditem.getItemid());
        	    				if(dv != 0) {
        	    					String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
        	    					scq_list.add(kq_item);
        	    					scq_list.add(new Double(dv));             			        
        	    				}
        	    			}     
        	    		} else {        
        	    			String value = rowSet.getString(fielditem.getItemid());
           			       	if(value == null || value.length() <= 0) {
           			       		value="0";
           			       	}
           			       	double dv = Double.parseDouble(value);
           			       	if(dv != 0) {
           			       		String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);        			         			  
           			       		ArrayList one_list= new ArrayList();
           			       		one_list.add(kq_item);
           			       		one_list.add(new Double(dv));
           			       		list.add(one_list);
               			      
           			       		if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
           			       			rownum ++;
           			       		}

           			       		continue;
           			       	} else {
               		    	   continue;
           			       	}
        	    		}
        	    	}else if(!"q03z0".equals(fielditem.getItemid()) && !"nbase".equals(fielditem.getItemid())
        	    			&&!"a0100".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid())
        	    			&&!"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid())
        	    			&&!"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid())
        	    			&&!"state".equals(fielditem.getItemid()) && !"a0101".equals(fielditem.getItemid())
        	    			&&!"i9999".equals(fielditem.getItemid())) {

        	    		String  sr = rowSet.getString(fielditem.getItemid());

        	    		if(sr != null && sr.length()>0 && !"0".equals(sr)) {
        	    			String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(),item_list);    
        	    			ArrayList one_list = new ArrayList();
         			        one_list.add(kq_item);
         			        one_list.add(new Double(1));
         			        list.add(one_list);
        	    		}
        	      }
           	 }             	    
  
        	 if (rownum == 0) {
        	 	list.add(scq_list);
        	 }
         			
           	  kq_item_map.put(q03z0,list);
           }

         	
         	for(int s = 0; s < datelist.size(); s++) {
         		ArrayList fuList = new ArrayList();
         		StringBuffer str = new StringBuffer();
         		CommonData cur_vo = (CommonData)datelist.get(s);		
                String cur_date=cur_vo.getDataName().trim();                   
                ArrayList kq_item_list=(ArrayList)kq_item_map.get(cur_date);  //符号
                if(kq_item_list != null && kq_item_list.size() > 0) {
                	StringBuffer font_str=new StringBuffer();
               	   	for(int t = 0;t < kq_item_list.size(); t++) {
               	   		ArrayList one_list = (ArrayList)kq_item_list.get(t);
               	   		if(one_list != null && one_list.size() > 0) {
               			   
               			   String [] kq_item = (String [])one_list.get(0);                	   
                       	   Double dv = (Double)one_list.get(1);
                       	   double value = dv.doubleValue();
                       	   if(value != 0) {
                       		   if (kq_item[0] != null) {
	                       		   ArrayList temp = new ArrayList();
	                       		   temp.add(Integer.valueOf(str.length()));
	                       		   str.append(kq_item[0]);
	                       		   temp.add(Integer.valueOf(str.length()));
	                       		   temp.add(kq_item[0]);
	                       		   temp.add(kq_item[1]);
	                       		   fuList.add(temp);
                       		   }
                       	   } 	  
               			}
               	   }
               	   
                } 
                fuList.add(str.toString());
                
                dataList.add(fuList);
        	  }
        }catch(Exception e){ 
        	e.printStackTrace();
        } finally {
			PubFunc.closeDbObj(rowSet);
	    } 
        return dataList;
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    private String getFontStyle(String fn,String fi,String fu,String fb,String fz,String color)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	if(color!=null&&color.length()>0)
    	{
    		style.append("color: "+color+";");
    	}else
    	{
    		style.append("color: #FF0000;");
    	}
    	
    	
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
    private String executeFont(String fontName,String fontStyle,String context)
	{
		
		StringBuffer tempTable=new StringBuffer("");
		if(context!=null&&context.trim().length()>0) {
			tempTable.append("<font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' >");
			tempTable.append(context);			
			tempTable.append("</font>");
		} 
		
		return tempTable.toString();
	}
	/**
	 * 组成查询内容的sql
	 * @param codea0100
	 * @param userbase
	 * @param start_date
	 * @param end_date
	 * @param column
	 * @return
	 */
    private String selcet_kq_one_emp(String codea0100,String userbase,String start_date,String end_date,String column)
	{
		StringBuffer sqlstr= new StringBuffer();
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
	 	sqlstr.append("select "+columnstr+" from ").append(this.getCurTab()); 	   
	 	sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
	 	sqlstr.append(" and Q03Z0 <= '"+end_date+"%'");
	 	sqlstr.append(" and a0100="+codea0100+"");
	 	sqlstr.append(" and nbase='"+userbase+"'");
	 	return sqlstr.toString();
	}
	/**
     * 得到表尾数据
     * **/
    private void getTileHtml(PdfPTable table,ArrayList item_list)throws GeneralException {
    	Paragraph graph = new Paragraph();
    	
    	// 高度
    	float tile_height = Float.parseFloat(getPxFormMm(parsevo.getTile_h()));
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	 
    	  	
    
    	ExecuteKqDailyPdf daily = new ExecuteKqDailyPdf();
        
        // 创建标题的字体
        Font titleFont = daily.getFont(parsevo.getBody_fn(), 
        		parsevo.getBody_fb(), parsevo.getBody_fi(), 
        		parsevo.getBody_fu(), parsevo.getBody_fz(), 
        		"");
    	
        StringBuffer note_len_str = new StringBuffer();
    	graph.add(new Chunk("备注：1.",titleFont));
    	note_len_str.append("备注：1.");
    	
    	
    	ExecuteKqDailyExcel dailyExcel = new ExecuteKqDailyExcel(this.conn);
    	
    	
    	for(int i = 0; i < fielditemlist.size(); i++) {
    		FieldItem fielditem = (FieldItem)fielditemlist.get(i);
   	    	if(!"i9999".equals(fielditem.getItemid())) {
   	    		
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0) {
   	    			
   	    			graph.add(new Chunk(fielditem.getItemdesc()+"(", titleFont));
   	    			note_len_str.append(fielditem.getItemdesc()+"(");
   	    			
   	    			Font font = daily.getFont(parsevo.getBody_fn(),
							parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo
							.getBody_fu(), parsevo.getBody_fz(),
							kq_item[1]);
   	   	 
   	    			graph.add(new Chunk(kq_item[0], font));
   	    			note_len_str.append(kq_item[0]);
   	    			graph.add(new Chunk(")", titleFont));
   	    			note_len_str.append(")");
   	   	    	    
   	   	    	}  	    		
   	    	}				
   	    }   
    	   	
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());    	
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+6; 
    	int numrow_1=getNumRow(strlen);    	
    	if(numrow_1!=0) {
    		note_h_1=note_h_1*numrow_1;	
    	}
    	
    	PdfPCell cell = new PdfPCell(graph);
        cell.setMinimumHeight(note_h_1);
        cell = daily.excecute(cell, 7, daily.getLtrb("1"));
        cell.setColspan(this.maxColnum);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	table.addCell(cell);
	    
	    //linbz	27104 导出第二条备注时，另建个单元格
    	if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    	{
    		graph = new Paragraph();
    		graph.add(new Chunk("　　　2."+parsevo.getTile_fw(), titleFont));
    		String tile_fw="     2."+parsevo.getTile_fw();
        	int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
        	int numrow_2=getNumRow(str_tile_2);
        	int note_h_2=Integer.parseInt(parsevo.getBody_fz())+6;
        	if(numrow_2!=0)
        	{
        		note_h_2=note_h_2*numrow_2;
        	}
        	cell = new PdfPCell(graph);
    		cell.setMinimumHeight(note_h_2);
            cell = daily.excecute(cell, 7, daily.getLtrb("1"));
            cell.setColspan(this.maxColnum);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        	table.addCell(cell);
    	} 
    	
    	if ("#u".equals(parsevo.getTile_u().trim()) || "#d".equals(parsevo.getTile_d().trim()) 
    			|| "#e".equals(parsevo.getTile_e().trim()) || "#t".equals(parsevo.getTile_t().trim()) 
    			|| "#p".equals(parsevo.getTile_p().trim()) || "#c".equals(parsevo.getTile_c().trim())){
    		
    		graph = new Paragraph();
    		// 创建标题的字体
    		Font font = daily.getFont(parsevo.getTile_fn(), 
        		parsevo.getTile_fb(), parsevo.getTile_fi(), 
        		parsevo.getTile_fu(), parsevo.getTile_fz(), 
        		"");
    	 

	    	// 有几个列
	        int i = 0;
	        if ("#u".equals(parsevo.getTile_u().trim())) {
	        	i ++;
	        }
	        if("#d".equals(parsevo.getTile_d().trim())) {
	        	i ++;
	        }
	        if("#e".equals(parsevo.getTile_e().trim())) {
	        	i ++;
	        }
	        if("#t".equals(parsevo.getTile_t().trim())) {
	        	i ++;
	        }
	        if("#p".equals(parsevo.getTile_p().trim())) {
	        	i ++;
	        }
	        if("#c".equals(parsevo.getTile_c().trim())) {
	        	i ++;
	        }
	      
	        int j = 0;
	        
	        // 制作人单位
	        if("#u".equals(parsevo.getTile_u().trim())) {
	     	   
	     	   String u_code = "";
	     	   if(!userView.isSuper_admin()) {
	 			  if(userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0) {
	 				 u_code = userView.getUserOrgId();
	 			  } else {
	 				 u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
	 			  }
	 		   }
	     	   String [] u_codeitem=getCodeItemDesclow(u_code);
	     	   String u_str = "制作人单位: "+u_codeitem[0];
	     	   
	     	   graph = new Paragraph(u_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
              
              table.addCell(cell);
	        }
	        
	        // 制作日期
	        if("#d".equals(parsevo.getTile_d().trim())) {
	     	   String d_str="制作日期:"+PubFunc.getStringDate("yyyy.MM.dd");
	     	  graph = new Paragraph(d_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
             
             table.addCell(cell);  
	        }
	        
	        // 制作人
	        if("#e".equals(parsevo.getTile_e().trim())) {
	     	   
	     	   String e_str="制作人: "+this.userView.getUserFullName();
	     	  graph = new Paragraph(e_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
             
             table.addCell(cell);   
	        }
	        
	        // 制作时间
	        if("#t".equals(parsevo.getTile_t().trim())) {
	     	   String t_str="时间:"+PubFunc.getStringDate("HH:mm:ss");
	     	  graph = new Paragraph(t_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
             
             table.addCell(cell);
	        }
	        
	        // 页码
	        if("#p".equals(parsevo.getTile_p().trim())) {
	     	   
	     	   String p_str="页码:1";
	     	  graph = new Paragraph(p_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
             
             table.addCell(cell);
	        }
	        // 总页码
	        if("#c".equals(parsevo.getTile_c().trim())) {    	   
	     	   String c_str="总页码:1";
	     	  graph = new Paragraph(c_str, font);
	     	   cell = new PdfPCell(graph);
	     	   cell.setMinimumHeight(tile_height);
	     	   cell = daily.excecute(cell, 7, daily.getLtrb("1"));
	     	   j ++;
	     	   if (i == j) {
	     		   cell.setColspan(this.maxColnum - (this.maxColnum / i) * (j- 1));
	     	   } else {
	     		   cell.setColspan(this.maxColnum / i);
	     	   }
             
             table.addCell(cell);
	        }
	        
	    }
    }
 
  
    /**
     * 查询考勤期间的所有指标
     * @return
     */
    private HashMap querryKq_item () {
    	HashMap map = new HashMap();
    	String sql = "select item_symbol,fielditemid from kq_item";
    	ContentDAO dao = new ContentDAO(this.conn);
    	ResultSet rs = null;
    	try {
			rs = dao.search(sql);
			while (rs.next()) {
				String key = rs.getString("fielditemid");
				String value = rs.getString("item_symbol");
				if (key != null) {
					map.put(key.toLowerCase(), value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			PubFunc.closeDbObj(rs);
		}
    	return map;
    }
    
    /**
     * 四舍五入算法
     * @param value
     * @param deci
     * @return
     * @throws Exception
     */
    private String round(String value, int deci) throws Exception {
    	if (deci < 0) {
    		throw new Exception("deci不能小于零！");
    	}
    	
    	BigDecimal big = new BigDecimal(value);
    	BigDecimal one = new BigDecimal("1");
    	return big.divide(one, deci,BigDecimal.ROUND_HALF_UP).toString();
    }

	public int getMaxColnum() {
		return maxColnum;
	}

	public void setMaxColnum(int maxColnum) {
		this.maxColnum = maxColnum;
	}

    public String getCurTab() {
        return curTab;
    }

    public void setCurTab(String curTab) {
        this.curTab = curTab;
    }
}
