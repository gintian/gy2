package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ExportExcelTrans extends IBusiness {

	private boolean haveData = true; //判断数据是否来自于页面，页面数据代码类指标不需要解析，sql查询内容 代码类需要翻译
	private static final int excelRows=5000;
	
	@Override
    public void execute() throws GeneralException {
         String subModuleId = (String)this.getFormHM().get("subModuleId");
         TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
         ArrayList outputcolumns = (ArrayList)this.getFormHM().get("outputcolumns");
         int headLevel = (Integer)this.getFormHM().get("headLevel");
         HashMap columnMap = tableCache.getColumnMap();
         String gridTitle = (String)tableCache.get("title");
         gridTitle = gridTitle==null?"grid":gridTitle;
		 String tableName = getUserName() + "_" + gridTitle;
         deatilDataFile(tableName, outputcolumns, columnMap, tableCache, headLevel);
	}
	
	private void deatilDataFile(String tableName,ArrayList outputcolumns,HashMap columnMap,TableDataConfigCache tableCache ,int headLevel) {
		 ArrayList dataList = getDataList(tableCache);
		 String tableSql = (String)tableCache.get("combineSql");
		 String sortSql = tableCache.getSortSql();
         RowSet rs=null;
         ContentDAO dao=new ContentDAO(this.frameconn);
         SXSSFWorkbook xwb=new SXSSFWorkbook(100);
         HSSFWorkbook  wb=new HSSFWorkbook();
         HSSFSheet  sheet=null;
    	 SXSSFSheet xsheet=null;
         FileOutputStream fileOut = null;
         String filename="";
         int rownum=0;
		 try {
			 boolean isXlsxFlag=false;
			 if(dataList==null) {//获取完整的查询sql ，包含快速过滤和方案查询的条件
				 rs =dao.search("select count(*) maxRowCount from ( "+tableSql+" )  tableCache");
	        	 int rowCount=0;
	        	 if(rs.next()) {
	        		 rowCount=rs.getInt("maxRowCount");
	        	 }
	        	if(rowCount>120000||outputcolumns.size()>255||rowCount*outputcolumns.size()>10000000) {
	        		isXlsxFlag=true;
	        	} 
	        	String[] fields=new String[outputcolumns.size()];
	         	for(int i=0;i<outputcolumns.size();i++) {
	         		fields[i]=((DynaBean)outputcolumns.get(i)).get("columnid").toString();
	         	}
	         	haveData =false;
	         	PaginationManager paginationm =null;
	 	        paginationm=new PaginationManager(tableSql,"","",sortSql,fields,"");
	 	        paginationm.setBAllMemo(true);
	 	        paginationm.setPagerows(excelRows);
	 	        paginationm.setKeylist(splitKeys(tableCache.getIndexkey()));
	        	 int pageIndex=0;
	        	 do {
	        		 dataList=(ArrayList)paginationm.getPage(pageIndex+1);
	        		 if(isXlsxFlag) {
	        			 HashMap<String,CellStyle> cellStyleMap= createCellStyle(xwb);
	        			 xsheet=xwb.createSheet("第"+(pageIndex+1)+"页");
	        			 rownum=createExcelFile(xwb, xsheet, cellStyleMap, dataList, outputcolumns, columnMap, headLevel);
	        		 }else {
	        			 HashMap<String,CellStyle> cellStyleMap= createCellStyle(wb);
	        			 sheet=wb.createSheet("第"+(pageIndex+1)+"页");
	        			 rownum= createExcelFile(wb, sheet, cellStyleMap, dataList, outputcolumns, columnMap, headLevel);
	        		 }
	        		 pageIndex++;
	        	 }while(pageIndex<Math.ceil((double)rowCount/(double)excelRows));//五千条数据分一页
	        	 
			 }else {//页面获取选中数据
				 HashMap<String,CellStyle> cellStyleMap= createCellStyle(wb);
    			 sheet=wb.createSheet("第"+(1)+"页");
    			 rownum=createExcelFile(wb, sheet, cellStyleMap, dataList, outputcolumns, columnMap, headLevel);
			 }
			 
			 
			 if(this.formHM.containsKey("summaryData")){
				 if(isXlsxFlag)
					 createSumData(xwb, xsheet, rownum, outputcolumns);
				 else
					 createSumData(wb, sheet, rownum, outputcolumns);
			 }  
			 
			 //生成文件
			 if(isXlsxFlag) {
				 filename=tableName+".xlsx";
			 }else {
				 filename=tableName+".xls";
			 }
			 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
			 if(isXlsxFlag) {
				 xwb.write(fileOut);
				 fileOut.flush();
				 xwb.dispose();
			 }else {
				 wb.write(fileOut);
			 }
			
			 fileOut.flush();
			 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }finally {
			 PubFunc.closeIoResource(fileOut);
			 PubFunc.closeDbObj(rs);
			 PubFunc.closeResource(xwb);
			 PubFunc.closeResource(wb);
		 }
			filename = PubFunc.encrypt(filename);
			this.getFormHM().put("filename",filename);
		
	}
	
	private void createSumData(Workbook wb,Sheet sheet,int rownum,ArrayList outputcolumns) {

     		DynaBean sumBean = (DynaBean)this.formHM.get("summaryData");
     		CellStyle sumStyle = wb.createCellStyle();
     		sumStyle.setAlignment(HorizontalAlignment.RIGHT);
     		sumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
     		sumStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
     		sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
     		sumStyle.setBorderBottom(BorderStyle.THIN);
     		sumStyle.setBorderTop(BorderStyle.THIN);
     		sumStyle.setBorderLeft(BorderStyle.THIN);
     		sumStyle.setBorderRight(BorderStyle.THIN);
     		HashMap map = PubFunc.DynaBean2Map(sumBean);
     		Row sumrow = sheet.createRow(rownum);
     		sumrow.setHeight((short)400);
     		for(int k=0;k<outputcolumns.size();k++){
     			
     			DynaBean column = (DynaBean)outputcolumns.get(k);
     			String columnid = column.get("columnid").toString();
     			Cell scell = sumrow.createCell(k);
     			scell.setCellStyle(sumStyle);
     			if(map.containsKey(columnid))
     				scell.setCellValue(Double.parseDouble((String)map.get(columnid)));
     			else if(k==0)
     				scell.setCellValue(ResourceFactory.getProperty("planar.stat.total"));
     			
     		}
     		 
     	 
	}
	
	private int createExcelFile(Workbook wb,Sheet sheet,HashMap<String,CellStyle> cellStyleMap,
								ArrayList dataList,ArrayList outputcolumns,HashMap columnMap,int headLevel) {
			int colnum = 0;
			int rownum=0;
	        CellStyle cellRight=cellStyleMap.get("cellRight");
	        CellStyle cellstyle=cellStyleMap.get("cellstyle");//表头样式
	        CellStyle cellCenter=cellStyleMap.get("cellCenter");
	        CellStyle cellLeft=cellStyleMap.get("cellLeft");
	        
	  		 //为了方便合并，先把表头单元格全部创建
	      	 for(;rownum<headLevel;rownum++){
	          	 Row row = sheet.createRow(rownum);
	          	row.setHeight((short)400);
	          	 for(int col=0;col<outputcolumns.size();col++){
	          		 Cell cell = row.createCell(col);
	          		 cell.setCellStyle(cellstyle);
	          	 }
	           }
	      	 ArrayList alignlist = new ArrayList();
      	  //写列头
	         for(int i=0;i<outputcolumns.size();i++){
		        	 DynaBean column = (DynaBean)outputcolumns.get(i);
		        	 String columnid = column.get("columnid").toString();
		        	 Integer width = (Integer)column.get("width");
		        	 ColumnsInfo ci = (ColumnsInfo)columnMap.get(columnid);
		        	 String align = ci.getTextAlign(); // 获取 列 水平对齐方式 wangb 20170803 30286
		        	if("right".equalsIgnoreCase(align))
		        		alignlist.add(3);
		        	 else if("center".equalsIgnoreCase(align))
		        		alignlist.add(2);
		        	 else 
		        		alignlist.add(1);//水平对齐 没有值 默认是左对齐   wangb  20170831 31031
		        	 ArrayList ups = (ArrayList)column.get("ups");
		        	 Collections.reverse(ups);
		        	 int b=0;
		        	 for(;b<ups.size();b++){
		        		 	  Cell cell = sheet.getRow(b).getCell(colnum);
		        	    	  cell.setCellValue(ups.get(b).toString());
		        	  }
		        	
		        	 Cell cell = sheet.getRow(b).getCell(colnum);
	        		 cell.setCellValue(ci.getColumnDesc());
	        		 cell.setCellStyle(cellstyle);//标题设置默认居中
		        	 sheet.setColumnWidth(colnum, width.intValue()*40);
		        	 if(b < headLevel-1)
		        		 sheet.addMergedRegion(new CellRangeAddress(b,headLevel-1,colnum,colnum));
		        	 colnum++;
	         }
	         colnum = 0;
	         
	       //合并相同的列
	         for(int k=0;k<headLevel-1;k++){
	        	 Row currentRow = sheet.getRow(k);
	        	 String cellValue = "";
	        	 String cellId = "";
	        	 int startIndex = 0;
	        	 for(int c=0;c<outputcolumns.size();c++){
	        		 if(currentRow.getCell(c)==null){
	        			 startIndex = c;
	        			 continue;
	        		 }
	        		 String value = currentRow.getCell(c).getStringCellValue();
	        		 currentRow.getCell(c).setCellValue(value.split("`")[0]);
	        		 if(!cellValue.equals(value)){
	        			 if(c-startIndex>1&&!"".equals(cellValue)){//防止同一行连续多列内容为空时，导致列其他行数据重叠  changxy 20160727
	        				 try {
	        					 sheet.addMergedRegion(new CellRangeAddress(k,k,startIndex,c-1));
						} catch (Exception e) {
							cellValue = value;
	   	        			startIndex = c;
	   	        			continue;
						}
	        			 }
	        			 cellValue = value;
	        			 startIndex = c;
	        			 continue;
	        		 }
	        		 if(c==outputcolumns.size()-1 && c-startIndex>=1){
	        			 try {//三层表头合并单元格异常处理 changxy
	        				 sheet.addMergedRegion(new CellRangeAddress(k,k,startIndex,c));
						} catch (Exception e) {
						}
	        		 }
	        		 
	        	 }
	        	 
	         }
	         
      	 
	         
	         Cell cell = null;
	         Row row = null;
	         //写数据
	        
	         	for(int i =0;i<dataList.size();i++){
	        	 DynaBean ldb = (DynaBean)dataList.get(i);
	  	        	 if(ldb == null)
	  	        	     continue;
	  	        	 row = sheet.createRow(rownum);
	  	        	 row.setHeight((short)400);
	  	        	 rownum++;
	  	        	 colnum = 0;
  	        	 
  	        	 for(int k=0;k<outputcolumns.size();k++){
  	        		 cell = row.createCell(colnum);
  	        		 //列数据重新创建 单元格样式 不然会共用 对齐方式都一样  wangb  30286
  	        		CellStyle cloumnstyle=null;
  	        		switch ((Integer)alignlist.get(k)) {
					case 3:
						cloumnstyle=cellRight;
						break;
					case 2:
						cloumnstyle=cellCenter;
						break;
					default:
						cloumnstyle=cellLeft;
						break;
					}
  	        		 cloumnstyle.setWrapText(true);//haosl 内容超出单元格自动换行
  	        		 cell.setCellStyle(cloumnstyle);
  	        		 DynaBean column = (DynaBean)outputcolumns.get(k);
  		        	 String columnid = column.get("columnid").toString();
  	        		 String value = (!PubFunc.DynaBean2Map(ldb).containsKey(columnid))?"":ldb.get(columnid).toString();
  	        		 if(value.length()<1){
  	        			 cell.setCellValue(value);
  		        		 colnum++;
  		        		 continue;
  	        		 }
  	        		 ColumnsInfo ci = (ColumnsInfo)columnMap.get(columnid);
  	        		 ArrayList operationData = (ArrayList)column.get("operationData");
  	        		 if(operationData.size()>0){
  	        			  for(int c=0;c<operationData.size();c++){
  	        				  DynaBean valueBean = (DynaBean)operationData.get(c);
  	        				  if(valueBean.get("dataValue").toString().equals(value)){
  	        					  value = valueBean.get("dataName").toString();
  	        					  cell.setCellValue(value);
  	        					  break;
  	        				  }
  	        			  }
  	        		 }else if(StringUtils.isNotEmpty(ci.getCodesetId())&& !"0".equals(ci.getCodesetId())){
  	        			 String codeName = "";
  	        			 if(haveData)
  	        				 codeName = value.split("`").length>1?value.split("`")[1]:AdminCode.getCodeName(ci.getCodesetId(),value);
  	        			 else{
  	        				 //当codesetid时UM时，兼容UN
  	        				 codeName = AdminCode.getCodeName(ci.getCodesetId(),value);
  	        				 if("UM".equalsIgnoreCase(ci.getCodesetId()) && codeName.length()<1)
  	        					 codeName = AdminCode.getCodeName("UN",value);
  	        				if("UN".equalsIgnoreCase(ci.getCodesetId()) && codeName.length()<1)
 	        					 codeName = AdminCode.getCodeName("UM",value);
  	        			 }
  	        			 cell.setCellValue(codeName);
  	        		 }else if("D".equals(ci.getColumnType())){
  	        			String datevalue = value.replace(".", "-");
  	        			if(ci.getColumnLength()>0 && ci.getColumnLength()<17 && datevalue.length()>ci.getColumnLength())//{
  				    		datevalue = datevalue.substring(0,ci.getColumnLength());
  	        			cell.setCellValue(datevalue);
  	        		 }else if("M".equals(ci.getColumnType())){
  	        			 value = value.replace("<br>", "\n");
  	        			 value = value.replace("&nbsp;", " ");
  	        			 cell.setCellValue(value);
  	        		 }else if("N".equals(ci.getColumnType())){
  	        			 if(ci.getDecimalWidth()>0){
  	        				 cell.setCellValue(Double.parseDouble(value));
  	        				 //设置小数点后格式  changxy 
  	        				 String str="0000000000";
  	        				// CellStyle style=cell.getCellStyle();
  	        				cell.getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("0."+str.substring(1, ci.getDecimalWidth()+1)));
  	        				// cellstyle.setDataFormat();
  	        				 //cell.setCellStyle(style);
  	        			 }
  	        			 else{
  	        				if (value.contains(".")){//兼容数据库中为float类型的情况 取出值为0.00;
				    	        int k1= value.indexOf(".");
				    	        value=value.substring(0,k1);
				    	    }
  	        				 cell.setCellValue(Integer.parseInt(value));
  	        			 }
  	        		 }else{
						 if (StringUtils.equalsIgnoreCase(ci.getColumnId(), "z8115")) {
							 StringBuffer desc = new StringBuffer();
							 if (StringUtils.isNotEmpty(value)) {
								 String itemIds = value.split("`")[0];
								 String[] itemIdArray = itemIds.split(",");
								 for (String item : itemIdArray) {
									 if (StringUtils.isEmpty(item)) {
										 continue;
									 }
									 //String codesetid = item.substring(0, 2);
									 //String itemid = item.substring(2);
									 String itemdesc = AdminCode.getCodeName("UN", item);
									 if (StringUtils.isBlank(itemdesc)) {
										 itemdesc = AdminCode.getCodeName("UM", item);
									 }
									 desc.append(itemdesc).append(",");
								 }
								 if (desc.length() > 0) {
									 desc.setLength(desc.length() - 1);
								 }
							 }
							 value = desc.toString();
						 }
  	        			 cell.setCellValue(value);
  	        		 }
  	        		 colnum++;
  	        	 }
	        	 

        }
	        return rownum;
	}
	
	private HashMap<String,CellStyle> createCellStyle(Workbook wb){
		HashMap<String,CellStyle> map=new HashMap<String, CellStyle>();
		
		CellStyle cellstyle = wb.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellstyle.setBorderBottom(BorderStyle.THIN);
        cellstyle.setBorderTop(BorderStyle.THIN);
        cellstyle.setBorderLeft(BorderStyle.THIN);
        cellstyle.setBorderRight(BorderStyle.THIN);
        
        CellStyle cellCenter = wb.createCellStyle();//1
		cellCenter.setAlignment(HorizontalAlignment.CENTER);//居中
		cellCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		cellCenter.setBorderBottom(BorderStyle.THIN);
		cellCenter.setBorderTop(BorderStyle.THIN);
		cellCenter.setBorderLeft(BorderStyle.THIN);
		cellCenter.setBorderRight(BorderStyle.THIN);
		
		CellStyle cellLeft=wb.createCellStyle();//2
		cellLeft.setAlignment(HorizontalAlignment.LEFT);//居左
		cellLeft.setVerticalAlignment(VerticalAlignment.CENTER);
		cellLeft.setBorderBottom(BorderStyle.THIN);
		cellLeft.setBorderTop(BorderStyle.THIN);
		cellLeft.setBorderLeft(BorderStyle.THIN);
		cellLeft.setBorderRight(BorderStyle.THIN);
		cellLeft.setVerticalAlignment(VerticalAlignment.CENTER);
		
		CellStyle cellRight=wb.createCellStyle();//3
		cellRight.setAlignment(HorizontalAlignment.RIGHT);//居右
		cellRight.setVerticalAlignment(VerticalAlignment.CENTER);
		cellRight.setBorderBottom(BorderStyle.THIN);
		cellRight.setBorderTop(BorderStyle.THIN);
		cellRight.setBorderLeft(BorderStyle.THIN);
		cellRight.setBorderRight(BorderStyle.THIN);
		map.put("cellRight", cellRight);
		map.put("cellLeft", cellLeft);
		map.put("cellCenter", cellCenter);
		map.put("cellstyle", cellstyle);
		
		return map;
	}
	
	//获取数据
	private ArrayList getDataList(TableDataConfigCache tableCache){
		ArrayList list = null;
		if(this.getFormHM().containsKey("outputdata")){
			list = (ArrayList)this.getFormHM().get("outputdata");
		}else{ 
			if(tableCache.getTableData()!=null){
				list = tableCache.getTableData();
			}
		}
		
		return list;
	}

	
	private String getUserName(){
		String username = userView.getUserName();
		//username = PubFunc.getPinYin(username);
		return username;
	}
	
	private ArrayList splitKeys(String indexkey)
    {
    	if(indexkey==null|| "".equals(indexkey))
    		return null;
        ArrayList list=new ArrayList();
        String temp=indexkey.toLowerCase();
        StringTokenizer st = new StringTokenizer(temp, ",");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }   
        return list;
    }
	
}
