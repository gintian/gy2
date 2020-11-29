package com.hjsj.hrms.transaction.sys.dbinit.gather;

import com.hjsj.hrms.businessobject.sys.gathertable.GatherTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:采集表指标导出(excel)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 29, 2008:11:07:04 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndexExportTrans extends IBusiness{

	public void execute() throws GeneralException {
		HSSFWorkbook workbook= null;
		try{
			System.gc();
		String set=(String)this.getFormHM().get("set");
		String num=(String)this.getFormHM().get("num");
		GatherTableBo gather = new GatherTableBo(this.getFrameconn());
		
		HashMap indexNmenmap = gather.indexexportexcel();  //头名称
		workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
		//HSSFSheet sheet = workbook.createSheet("explain"); //生成一张表;
		HSSFRow row = null;  //行
		HSSFCell cell=null;   //单元格
		HSSFComment comment=null;  //定义注释
		String outName=this.userView.getUserName()+"_explain.xls";  //名称
		short n=0;
		//n=setindexHead(n,indexNmenmap,workbook,sheet); //生成表头
		
		//ArrayList indexinfolist = getIndexTableInfo(set); //查询出的内容
		HashMap map=getIndexTableInfo(set);
		ArrayList indexinfolist=(ArrayList)map.get("1");
		HashMap amap = (HashMap)map.get("2");
		setIndexData(amap,n,indexinfolist,workbook,/*sheet,*/row,cell); //写内容
		
		FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
		workbook.write(fileOut);
		fileOut.close();	
		//sheet=null;
		outName = PubFunc.encrypt(outName);
		this.getFormHM().put("outName",outName);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(workbook);
		}
	}
	
	public HashMap getIndexTableInfo(String set){
		HashMap map = new HashMap();
		try{
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		StringBuffer sql= new StringBuffer("select fieldsetid,customdesc from fieldset where fieldsetid in(");
		if(set.indexOf("/")==-1){
			buf.append("'");
			buf.append(set);
			buf.append("'");
			sql.append(buf.toString());
		}else{
			String[] arr = set.split("/");
			for(int i=0;i<arr.length;i++){
				buf.append(",");
				buf.append("'");
				buf.append(arr[i]);
				buf.append("'");
			}
			sql.append(buf.toString().substring(1));
		}
		sql.append(")order by fieldsetid,displayorder");
		ContentDAO da = new ContentDAO(this.getFrameconn());
		this.frowset = da.search(sql.toString());
		while(this.frowset.next()){
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("fieldsetid",this.frowset.getString("fieldsetid"));
			bean.set("customdesc", this.frowset.getString("customdesc"));
			list.add(bean);  //有两个指标;id与name
		}
		//指标
		StringBuffer bufs = new StringBuffer();
		StringBuffer sql2 = new StringBuffer("select displayid,itemid,itemdesc,itemmemo,fieldsetid,itemtype,itemlength,decimalwidth,codesetid from fielditem where fieldsetid in(");
		if(set.indexOf("/")==-1){
			bufs.append("'");
			bufs.append(set);
			bufs.append("'");
			sql2.append(buf.toString());
		}else{
			String[] arrs = set.split("/");

			for(int i=0;i<arrs.length;i++){
				bufs.append(",");
				bufs.append("'");
				bufs.append(arrs[i]);
				bufs.append("'");
			}
			sql2.append(bufs.toString().substring(1));
		}
		sql2.append(") and useflag='1' order by fieldsetid,displayid");
		ContentDAO das = new ContentDAO(this.getFrameconn());
		this.frowset = das.search(sql2.toString());
		HashMap amap= new HashMap();
		while(this.frowset.next()){
			LazyDynaBean beans = new LazyDynaBean();
			beans.set("displayid", this.frowset.getString("displayid"));
			beans.set("itemid", this.frowset.getString("itemid"));
			beans.set("itemdesc", this.frowset.getString("itemdesc"));
			String codesetid = this.frowset.getString("codesetid");
			if(codesetid==null||"0".equals(codesetid)||"null".equals(codesetid)){
				codesetid="";
			}
			beans.set("codesetid", codesetid);
			String itemtype=this.frowset.getString("itemtype");
			if("A".equals(itemtype)||"D".equals(itemtype)){
				itemtype=itemtype+"("+this.frowset.getInt("itemlength")+")";
			}else if("N".equals(itemtype)){
				itemtype=itemtype+"("+this.frowset.getInt("itemlength")+","+this.frowset.getInt("decimalwidth")+")";
			}
			beans.set("itemtype", itemtype);
			String s = this.frowset.getString("itemmemo");
			if("null".equals(s)||s==null){
				s = " ";
				beans.set("itemmemo", s);
			}
			else{
				beans.set("itemmemo", this.frowset.getString("itemmemo"));
			}
			
			beans.set("fieldsetid", this.frowset.getString("fieldsetid"));
			if(amap.get(frowset.getString("fieldsetid").toUpperCase())==null)
			{
				ArrayList setlist = new ArrayList();
				setlist.add(beans);
				amap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
			}
			else
			{
				ArrayList setlist=(ArrayList)amap.get(frowset.getString("fieldsetid").toUpperCase());
				setlist.add(beans);
				amap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
			}
			
		}
//		for(int i=0;i<list.size();i++)
//		{
//			LazyDynaBean bean = (LazyDynaBean)list.get(i);
//			String fieldsetid=(String)bean.get("fieldsetid");
//			//String customdesc=(String)bean.get("customdesc");
//			ArrayList itemlist = (ArrayList)amap.get(fieldsetid.toUpperCase());
//		}
		map.put("1", list);
		map.put("2",amap);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
	//设置指标导出头;
	private short setindexHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet){
		short i=n;
		try{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFPatriarch patr=sheet.createDrawingPatriarch();  //声明一个画图的顶级管理器

			
			row=sheet.createRow(i); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
			csCell=row.createCell((short)2);
			
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
		    csCell.setCellValue((String)map.get("indexexplain")); //往单元格里输入内容
		    i++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
		
	}
	
	public void setIndexData(HashMap map,short n,ArrayList infoList,HSSFWorkbook workbook,/*HSSFSheet sheet,*/HSSFRow row,HSSFCell cell){
		try{

		
		for(int i=0;i<infoList.size();i++){
			short h=n;
			LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
			HSSFSheet sheet = workbook.createSheet((String)bean.get("customdesc")+"("+(String)bean.get("fieldsetid")+")");
			sheet.setColumnWidth(1,4000);
			sheet.setColumnWidth(2,5000);
			sheet.setColumnWidth(3,4000);
			sheet.setColumnWidth(4,4000);
			sheet.setColumnWidth(5,20000);
			row = sheet.getRow(h);
			if(row==null)
				row = sheet.createRow(h);
			cell=row.createCell(0);  //写入的单元各位置;
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.xuhao"));
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell(1);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("gz.templateset.itemcode"));  
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell(2);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kh.field.field_n")); 
    		cell.setCellStyle(this.settopStyle(workbook));
    		
    		cell=row.createCell(3);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kh.field.field_type")); 
    		cell.setCellStyle(this.settopStyle(workbook));
    		
    		cell=row.createCell(4);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.guanliancode")); 
    		cell.setCellStyle(this.settopStyle(workbook));
    		
    		cell=row.createCell(5);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.explain")); 
    		cell.setCellStyle(this.settopStyle(workbook));
    		
    		
    		
    		
    		ArrayList fieldlist = (ArrayList)map.get((String)bean.get("fieldsetid")); //对应的fieldsetid
    		for(int j=0;j<fieldlist.size();j++)
    		{
    			h++;
    			LazyDynaBean abean = (LazyDynaBean)fieldlist.get(j);
    			row = sheet.createRow(h);
    			cell=row.createCell(0);
        		cell.setCellValue(j+1+"");
        		
        		cell=row.createCell(1);
        		cell.setCellValue((String)abean.get("itemid"));
        		
        		cell=row.createCell(2);
        		cell.setCellValue((String)abean.get("itemdesc"));
        		
        		cell=row.createCell(3);
        		cell.setCellValue((String)abean.get("itemtype"));
        		
        		cell=row.createCell(4);
        		cell.setCellValue((String)abean.get("codesetid"));
        		
        		cell=row.createCell(5);
        		cell.setCellValue((String)abean.get("itemmemo"));
    		}
    		sheet=null;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private HSSFCellStyle settopStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12); // 字体大小
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);//颜色黄色
		style.setWrapText(true);  //换行
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边

        return style;

	}
}
