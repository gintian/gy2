package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class TaxMxExportTrans extends IBusiness{
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String declaredate)
	{
		StringBuffer buf=new StringBuffer();
		if(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate))
			return "";
		String[] datearr=StringUtils.split(declaredate, ".");
		String theyear=datearr[0];
		String themonth=datearr[1];
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);		
		return buf.toString();
	}
	public void execute() throws GeneralException 
	{
		try
		{
			TaxMxBo tmb = new TaxMxBo(this.getFrameconn(),this.getUserView());
			String declaredate=(String)this.getFormHM().get("declaredate");
			String a_code=(String)this.getFormHM().get("a_code");
			String condtionsql=(String)this.getFormHM().get("condtionsql");
			/* 安全问题处理 所得税管理 sql-in-url xiaoyun 2014-9-12 start */
			condtionsql = PubFunc.decrypt(SafeCode.decode(condtionsql));
			/* 安全问题处理 所得税管理 sql-in-url xiaoyun 2014-9-12 end */
			String filterByMdule=(String)this.getFormHM().get("filterByMdule");
			String fromtable=(String)this.getFormHM().get("fromtable");
			if(fromtable==null)
				fromtable="gz_tax_mx";
			String condition = getFilterCond(declaredate);
			StringBuffer strwhere = new StringBuffer();
			/**exporttype=1是按合计导出*///3个人扣缴申报表
			String exporttype=(String)this.getFormHM().get("exporttype");
			if(condition.length()>0)
			{
				strwhere.append(" and "+condition);
			}
			String deptid=tmb.getDeptID();
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
				String codesetid=a_code.substring(0, 2);
	    		String value=a_code.substring(2);
				
					if("UN".equalsIgnoreCase(codesetid))
			    	{
				    	strwhere.append(" and b0110 like '");
				    	strwhere.append(value);
				    	strwhere.append("%'");
				    }
			    	if("UM".equalsIgnoreCase(codesetid))
			    	{
				    	strwhere.append(" and e0122 like '");
				    	strwhere.append(value);
				    	strwhere.append("%'");
			    	}
				
				/*if(deptid.equalsIgnoreCase("false")&&filterByMdule.equals("1"))
				{
	    			String codesetid=a_code.substring(0, 2);
		    		String value=a_code.substring(2);
		    		if(codesetid.equalsIgnoreCase("UN"))
		    		{
			    		strwhere.append(" and b0110 like '");
			    		strwhere.append(value);
			    		strwhere.append("%'");
			    	}
		    		if(codesetid.equalsIgnoreCase("UM"))
		    		{
			    		strwhere.append(" and e0122 like '");
			    		strwhere.append(value);
			    		strwhere.append("%'");
		    		}
				}
				else
				{
					
					String value=a_code.substring(2);
					if(value!=null&&!value.equals(""))
				    	strwhere.append(" and  deptid='"+value+"'");
				}*/
			}
			if(!(condtionsql==null|| "".equalsIgnoreCase(condtionsql)))
			{
				strwhere.append(" and "+condtionsql.replace("gz_tax_mx.", ""));
			}
			/*if(!this.userView.isAdmin()&&!this.userView.getGroupId().equals("1")&&filterByMdule.equals("0"))
			{
				String code=this.userView.getManagePrivCode();
	        	 String value=this.userView.getManagePrivCodeValue();
	        	 if(code==null)
	        	 {
	        		 strwhere.append(" and 1=2 ");
	        	 }
	        	 else if(code.equalsIgnoreCase("UN"))
	        	 {
	        		 strwhere.append(" and (b0110 like '");
	        		 strwhere.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 strwhere.append(" or b0110 is null ");
	        		 }
	        		 strwhere.append(")");
	        	 }
	        	 else if(code.equalsIgnoreCase("UM"))
	        	 {
	        		 strwhere.append(" and (e0122 like '");
	        		 strwhere.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 strwhere.append(" or e0122 is null ");
	        		 }
	        		 strwhere.append(")");
	        	 }
			}*/
//			System.out.println(strwhere.toString());
			/* 安全问题 所得税管理 文件下载 xiaoyun 2014-9-12 start */
			//String outname=tmb.exportMxExcel(fromtable,strwhere.toString(),exporttype,this.getUserView(),filterByMdule).replaceAll(".xls","#");
			//String outname=tmb.exportMxExcel(fromtable,strwhere.toString(),exporttype,this.getUserView(),filterByMdule);
			String outname= "";
			if("3".equals(exporttype))
				outname=this.exportTaxDeclarationExcel(fromtable,strwhere.toString(),exporttype,this.getUserView(),filterByMdule,tmb);
			else
				outname=tmb.exportMxExcel(fromtable,strwhere.toString(),exporttype,this.getUserView(),filterByMdule);
			
			//this.getFormHM().put("outName",outname);
			this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outname)));
			/* 安全问题 所得税管理 文件下载 xiaoyun 2014-9-12 end */
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private HashMap getImportData() {
		HashMap map1 = new HashMap();
		FileInputStream fis=null;
		HSSFWorkbook wb=null;
		try {
			String splitF = System.getProperty("file.separator");
			String url = System.getProperty("catalina.home") + splitF + "webapps" + splitF + "hrms" + splitF + "templatefile" + splitF + "TaxDeclaration.xls";
			File file = new File(url);
			fis =  new FileInputStream(file);
			wb= new HSSFWorkbook(fis);
			HSSFSheet sheet=wb.getSheetAt(0);

			ArrayList list_code = new ArrayList();
			map1.putAll(getComment(sheet,4));
			map1.putAll(getComment(sheet,5));
			map1.putAll(getComment(sheet,6));
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(wb);
			PubFunc.closeResource(fis);
		}
		return map1;
	}
	
	private HashMap getComment(HSSFSheet sheet, int rowNum) {
		HashMap map = new HashMap();
		ArrayList list=new ArrayList();
		int rows = sheet.getPhysicalNumberOfRows();
		String not_insert = ",21,23,29,37,38,32,33,34,35,37,";
		if(rows<1)
			return null;
		HSSFRow row = sheet.getRow(rowNum);
		if (row != null) {
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) {
				if(not_insert.indexOf("," + c + ",") == -1) {
					String value = "";
					HSSFCell cell = row.getCell(c);
					HSSFComment cc = cell.getCellComment();
					String comment = "";
					if (cell != null) {
						comment = cell.getCellComment()!= null?cell.getCellComment().getString().getString():"";
					}
					if(StringUtils.isNotBlank(comment)) {
						if(24 <= cell.getColumnIndex() && cell.getColumnIndex() <= 29 && comment.length() > 5) {
							comment = comment.substring(1);
						}
					}
					if("AXXXX".equalsIgnoreCase(comment.trim())) {
						comment = "";
					}
					if(StringUtils.isNotBlank(cell.getStringCellValue()))
						map.put(cell.getColumnIndex(), comment.trim() + "," + cell.getStringCellValue());
				}
			}
		}
		return map;
	}
	
	public ArrayList getFieldlist(TaxMxBo taxbo) {
		/**个税明细表固定字段列表*/
		ArrayList list = new ArrayList();
		list.addAll(taxbo.searchCommonItemList());
		list.addAll(taxbo.searchDynaItemList());
		return list;
	}
	
	/**
	 * 生成个税明细表
	 * @return,返回的为文件名,包括目录结构
	 */
	public String exportTaxDeclarationExcel(String fromtable,String strwhere,String exporttype,UserView view,String filterByMdule, TaxMxBo taxbo)
	{
		ContentDAO dao = new ContentDAO(this.frameconn);
		String outname="ExportTaxMx_"+PubFunc.getStrg()+".xls";
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		String filename=outname;
		String firstRow = ",1,2,3,4,5,6,39,30,31,";
		String secondRow = ",10,22,32,33,34,35,36,37,38,";
		RowSet rs = null;
		StringBuffer ss = new StringBuffer();
		HSSFWorkbook workbook = new HSSFWorkbook();

		int rowFirst = 4;
		int rowSecond = 5;
		int rowThird = 6;
		try{
			//权限
			String pre = taxbo.getPrivPre(filterByMdule);
			rs = dao.search("select taxmode from " + fromtable + " where (" + pre + ")" + strwhere);
			while(rs.next()) {
				String tt = rs.getString("taxmode");
				if(StringUtils.isBlank(ss.toString()) || (ss + ",").indexOf("," + tt + ",") == -1) {
					ss.append("," + rs.getString("taxmode"));
				}
			}
			if(StringUtils.isNotBlank(ss.toString())) {
				strwhere = strwhere + " and taxMode in (" + ss.substring(1) + ")";
			}
			
			ArrayList<Field> itemlist = (ArrayList)this.getFieldlist(taxbo);
			HashMap<Integer, ArrayList<LazyDynaBean>> map_ = new HashMap<Integer, ArrayList<LazyDynaBean>>();
			HashMap map1 = getImportData();//获取模板中的数据
			HashMap map_other = (HashMap) map1.clone();
			String comment = "";
			Iterator iter = map1.entrySet().iterator();
			while (iter.hasNext()) {
				String comment_ = "";
				Map.Entry entry = (Map.Entry) iter.next();
				int key = (Integer) entry.getKey();
				String val = (String) entry.getValue();
				
				String value = val.split(",")[0];
				if(StringUtils.isBlank(value)){
					int fromRow = rowThird;
					int toRow = rowThird;
					if(firstRow.indexOf("," + key + ",") != -1) {
						fromRow = rowFirst;toRow = rowThird;
					}else if(secondRow.indexOf("," + key + ",") != -1){
						fromRow = rowSecond;toRow = rowThird;
					}
					map_.put(key, getStyleList(val.split(",")[1], "", "", "A", fromRow, toRow, key, key, 3000, "0"));
					map_other.remove(key);
					continue;
				}
				if(value.contains("+") && StringUtils.isNotBlank(value)) {
					int row_num = rowThird;
					if(firstRow.indexOf("," + key + ",") != -1) {
						row_num = rowFirst;
					}else if(secondRow.indexOf("," + key + ",") != -1){
						row_num = rowSecond;
					}
					comment_ = value;
					map_.put(key, getStyleList(val.split(",")[1], comment_.replace("+", "_"), comment_, 
							"N", row_num, rowThird, key, key, 3000, "0"));
				}
				for (Field field : itemlist) {
					if(val.split(",")[0].equalsIgnoreCase(field.getName())) {
						if(firstRow.indexOf("," + key + ",") != -1) {
							comment_ = val.split(",")[0].toUpperCase();
							map_.put(key, getStyleList(val.split(",")[1], field.getName(), comment_, 
									taxbo.getvarType(field.getDatatype()), rowFirst, rowThird, key, key, 3000, field.getCodesetid()));
						}else if(secondRow.indexOf("," + key + ",") != -1){
							comment_ = val.split(",")[0].toUpperCase();
							map_.put(key, getStyleList(val.split(",")[1], field.getName(), comment_, 
									taxbo.getvarType(field.getDatatype()), rowSecond, rowThird, key, key, 3000, field.getCodesetid()));
						}else if(24 <= key && key <= 39){
							comment_ = "∑" + val.split(",")[0].toUpperCase();
							map_.put(key, getStyleList(val.split(",")[1], field.getName(), comment_, 
									taxbo.getvarType(field.getDatatype()), rowThird, rowThird, key, key, 3000, field.getCodesetid()));
						}else {
							comment_ = val.split(",")[0].toUpperCase();
							map_.put(key, getStyleList(val.split(",")[1], field.getName(), comment_, 
									taxbo.getvarType(field.getDatatype()), rowThird, rowThird, key, key, 3000, field.getCodesetid()));
						}
					}
				}
				if(StringUtils.isBlank(comment_)) {
					int fromRow = rowThird;
					int toRow = rowThird;
					if(firstRow.indexOf("," + key + ",") != -1) {
						fromRow = rowFirst;toRow = rowThird;
					}else if(secondRow.indexOf("," + key + ",") != -1){
						fromRow = rowSecond;toRow = rowThird;
					}
					map_.put(key, getStyleList(val.split(",")[1], "", value, "A", fromRow, toRow, key, key, 3000, "0"));
					map_other.remove(key);
				}
			}
			HashMap<String,Object> hashMap = getExportSqlForDetail(fromtable, strwhere, map_other, filterByMdule);
			
			map_.put(0, getStyleList("序号", "id", "", "", rowFirst, rowThird, 0, 0, 3000, "0"));
			map_.put(21, getStyleList("累计收入额", "income", hashMap.get("income_field") != null?(String)hashMap.get("income_field"):"", "N", rowSecond, rowThird, 21, 21, 3000, "0"));
			map_.put(23, getStyleList("累计专项扣除", "special", hashMap.get("specialDed_field") != null?(String)hashMap.get("specialDed_field"):"", "N", rowSecond, rowThird, 23, 23, 3000, "0"));
			map_.put(29, getStyleList("累计其他扣除", "other", hashMap.get("otherDed_field") != null?(String)hashMap.get("otherDed_field"):"", "N", rowThird, rowThird, 29, 29, 3000, "0"));
			map_.put(38, getStyleList("应补（退）税额", "YBSE", hashMap.get("ybse") != null?(String)hashMap.get("ybse"):"", "N", rowSecond, rowThird, 38, 38, 3000, "0"));
			
			if(map_.get(1) == null) 
				map_.put(1, getStyleList("姓名", "A0101", "A0101", "A", rowFirst, rowThird, 1, 1, 3000, "0"));
			if(map_.get(6) == null) 
				map_.put(6, getStyleList("所得项目", "TaxMode", "TaxMode", "A", rowFirst, rowThird, 6, 6, 3000, "0"));
			if(map_.get(22) == null) 
				map_.put(22, getStyleList("累计减除费用", "lj_basedata", "lj_basedata", "N", rowSecond, rowThird, 22, 22, 3000, "0"));
			if(map_.get(32) == null)
				map_.put(32, getStyleList("应纳税所得额", "LJSDE", "LJSDE", "N", rowSecond, rowThird, 32, 32, 3000, "0"));
			if(map_.get(33) == null)
				map_.put(33, getStyleList("税率/预扣率", "SL", "SL", "N", rowSecond, rowThird, 33, 33, 3000, "0"));
			if(map_.get(34) == null)
				map_.put(34, getStyleList("速算扣除数", "Sskcs", "Sskcs", "N", rowSecond, rowThird, 34, 34, 3000, "0"));
			if(map_.get(35) == null)
				map_.put(35, getStyleList("应纳税额", "LJSE", "LJSE", "N", rowSecond, rowThird, 35, 35, 3000, "0"));
			if(map_.get(37) == null)
				map_.put(37, getStyleList("已扣缴税额", "YJSE", "∑SDS", "N", rowSecond, rowThird, 37, 37, 3000, "0"));
			int size = map_.size();
			//排序
			for(int i = 0; i < size; i++) {
				if(map_.get(i) != null) 
					headList.addAll((ArrayList<LazyDynaBean>)map_.get(i));
			}
			
			StringBuffer sql = (StringBuffer) hashMap.get("sql");
			sql.append(" order by id asc ");
			RowSet rset=dao.search(sql.toString());
			rset.last();
			int len = rset.getRow();
			rset.beforeFirst();
			ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
			mergedCellList.addAll(mergedCellStyleMap("本月（次）情况", rowFirst, rowFirst, 7, 20, 3000*14, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("收入额计算", rowSecond, rowSecond, 7, 9, 3000*3, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("专项扣除", rowSecond, rowSecond, 11, 14, 3000*4, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("其他扣除", rowSecond, rowSecond, 15, 20, 3000*6, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("累计情况（工资、薪金）", rowFirst, rowFirst, 21, 29, 3000*9, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("累计专项附加扣除", rowSecond, rowSecond, 24, 29, 3000*6, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("税款计算", rowFirst, rowFirst, 32, 38, 3000*7, HorizontalAlignment.CENTER, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("个人所得税扣缴申报表", 0, 0, 0, 39, 3000*39, HorizontalAlignment.CENTER, (short)-1, true));
			mergedCellList.addAll(mergedCellStyleMap("税款所属日期：    年     月     日至     年      月      日", 1, 1, 0, 39, 3000*39, HorizontalAlignment.LEFT, (short)-1, true));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人名称：", 2, 2, 0, 39, 3000*39, HorizontalAlignment.LEFT, (short)-1, true));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人纳税人识别号（统一社会信用代码）：", 3, 3, 0, 30, 3000*30, HorizontalAlignment.LEFT, (short)-1, true));
			mergedCellList.addAll(mergedCellStyleMap("金额单位：         人民币元（列至角分）", 3, 3, 31, 39, 3000*9, HorizontalAlignment.RIGHT, (short)-1, true));
			mergedCellList.addAll(mergedCellStyleMap("谨声明:本扣缴申报表是根据国家税收法律法规及相关规定填报的，是真实的、可靠的、完整的。", len + rowThird + 2, 
					len + rowThird + 2, 0, 39, 3000*39, HorizontalAlignment.LEFT, (short)-1, false));
			mergedCellList.addAll(mergedCellStyleMap("扣缴义务人（签章）：                                                        年     月       日", len + rowThird + 3, 
					len + rowThird + 3, 0, 39, 3000*39, HorizontalAlignment.RIGHT, (short)-1, false));
			mergedCellList.addAll(mergedCellStyleMap("代理机构签章：\r\n\r\n代理机构统一社会信用代码：\r\n\r\n经办人签字：\r\n\r\n经办人身份证件号码：", len + rowThird + 4, 
					len + rowThird + 10, 0, 19, 3000*20, HorizontalAlignment.LEFT, (short)1, true));
			mergedCellList.addAll(mergedCellStyleMap("受理人：\r\n\r\n\r\n受理税务机关（章）：\r\n\r\n\r\n受理日期:    年     月     日", len + rowThird + 4, 
					len + rowThird + 10, 20, 39, 3000*20, HorizontalAlignment.LEFT, (short)1, true));
			

			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT); 
			//  形成SQL语句
			HashMap map = new HashMap();
			BigDecimal bigDecimal_ = new BigDecimal(0);
			int n=rowThird + 1;//数据开始的行数
			/**由于excel每sheet里最多65535条记录，默认每页显示65535条，多余则从新建一个sheet*/
			String notCount = ",0,1,2,3,4,5,6,39,";//这些列不需要合计值，也无法合计值
			String macth="(-)?+[0-9]+(.[0-9]+)?";
			int sheetPerRows=1;
			int sheetPage=1;
			HSSFSheet sheet = workbook.createSheet(sheetPage+"");
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			
			HSSFCellStyle bodyStyle = workbook.createCellStyle();
			bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			bodyStyle.setBorderBottom(BorderStyle.valueOf((short) 1));
			bodyStyle.setBorderLeft(BorderStyle.valueOf((short) 1));
			bodyStyle.setBorderRight(BorderStyle.valueOf((short) 1));
			bodyStyle.setBorderTop(BorderStyle.valueOf((short) 1));
			bodyStyle.setWrapText(true);
			while(rset.next()){
				//System.out.println("sheetPerRows="+sheetPerRows);
				//System.out.println("n="+n);
				if(sheetPerRows>=((25000*sheetPage)))
				{
					sheetPage++;
					sheet = workbook.createSheet(sheetPage+"");
					/**建表头*/
					int t=0;
					String strr="";
					for(int i=0;i<headList.size();i++){
						LazyDynaBean headBean = (LazyDynaBean) headList.get(i);
						String datatype=(String)headBean.get("colType");//该列的样式
						String itemid = (String) headBean.get("itemid");// 代码类id
						String codesetid = (String) headBean.get("codesetid");// 代码类id
						String itemdesc = (String) headBean.get("content");//当前列名标题
						comment = (String) headBean.get("comment");//当前列名标题的注释
						
						strr+=","+itemid+",";
						row = sheet.getRow((short)0);
						if(row==null)
							row = sheet.createRow((short)0);
						csCell =row.createCell((short)(t));
											
						csCell.setCellValue(itemdesc);
						csCell.setCellStyle(style);
						t++;
					}
					n=1;
				}
				if(sheetPerRows==1)
				{
					for(int i = 0; i < mergedCellList.size(); i++) {
						LazyDynaBean headBean = (LazyDynaBean) mergedCellList.get(i);
						String itemdesc = (String) headBean.get("content");//当前列名标题
						int fromRowNum = (Integer) headBean.get("fromRowNum");
						int toRowNum = (Integer) headBean.get("toRowNum");
						int fromColNum = (Integer) headBean.get("fromColNum");
						int toColNum = (Integer) headBean.get("toColNum");
						
						//获得该单元格样式
						HashMap headStyleMap = (HashMap) headBean.get("mergedCellStyleMap");//当前列名标题样式
						sheet = setCell(workbook, headStyleMap, fromRowNum, fromColNum, toRowNum, toColNum, itemdesc, sheet, null, patr);
					}
					/**建表头*/
					int t=0;
					for(int i=0;i<headList.size();i++){
						LazyDynaBean headBean = (LazyDynaBean) headList.get(i);
						String itemdesc = (String) headBean.get("content");//当前列名标题
						comment = (String) headBean.get("comment");//当前列名标题的注释
						int fromRowNum = (Integer) headBean.get("fromRowNum");
						int toRowNum = (Integer) headBean.get("toRowNum");
						int fromColNum = (Integer) headBean.get("fromColNum");
						int toColNum = (Integer) headBean.get("toColNum");
						sheet.setColumnWidth((short) (fromColNum), 3000); 
						//获得该单元格样式
						HashMap headStyleMap = (HashMap) headBean.get("headStyleMap");//当前列名标题样式
						sheet = setCell(workbook, headStyleMap, fromRowNum, fromColNum, toRowNum, toColNum, itemdesc, sheet, comment, patr);
						t++;
					}
				}
				int m = 0;
				row = sheet.createRow((short)n);
				DataFormat df = workbook.createDataFormat();
				String old_dataType = "";
				for(int i=0;i<headList.size();i++){
					LazyDynaBean headBean = (LazyDynaBean) headList.get(i);
					String datatype=(String)headBean.get("colType");//该列的样式
					String itemid = (String) headBean.get("itemid");// 代码类id
					String codesetid = (String) headBean.get("codesetid");// 代码类id
					comment = (String) headBean.get("comment");//当前列名标题的注释
					int deciwidth = headBean.get("decwidth") == null ? 2 : Integer.parseInt((String) headBean.get("decwidth"));
					if("N".equalsIgnoreCase(datatype)) {
						bodyStyle.setAlignment(HorizontalAlignment.RIGHT);
					}else {
						bodyStyle.setAlignment(HorizontalAlignment.CENTER);
					}
					old_dataType = 	datatype;
					ResultSetMetaData rsetmd=rset.getMetaData();
					String fieldesc = "";
					
					if(StringUtils.isNotBlank(itemid)) {
						if("id".equalsIgnoreCase(itemid)) {
							if("id".equalsIgnoreCase(itemid)) {
								fieldesc = rset.getString("id");
							}else {
								fieldesc = taxbo.getColumStr(rset,rsetmd,itemid);
							}
						}else {
							fieldesc = taxbo.getColumStr(rset,rsetmd,itemid);
						}
					}else {
						if("N".equals(datatype)){
							fieldesc = "0.00";
						}else {
							fieldesc = "";
						}
					}
					Pattern pattern = Pattern.compile("^-?[0-9]+.*[0-9]*$");
					if(notCount.indexOf("," + i + ",") == -1) {
						if(StringUtils.isNotBlank(fieldesc) && pattern.matcher(fieldesc).matches()) {
							BigDecimal sum_ =  (map.get(i) == null?bigDecimal_:(BigDecimal)map.get(i));
							BigDecimal value_ = new BigDecimal(PubFunc.round(fieldesc, deciwidth));
							map.put(i, sum_.add(value_)); 
						}
					}
					fieldesc = fieldesc!=null?fieldesc:"";
					String desc = "";
					if(!"0".equals(codesetid)){
						desc = AdminCode.getCodeName(codesetid,fieldesc);
					}else{
									
						if("N".equals(datatype)){
							if(fieldesc!=null&&fieldesc.trim().length()>0){
								desc = PubFunc.round(fieldesc,2);
							}else{
								desc = "";
							}
						} else{
							desc = fieldesc;
						}
					}
					csCell =row.createCell((short)(m));
					
					if("N".equals(datatype))
					{
			    		if(desc!=null&&!"".equals(desc))
				    	{
					    	csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    	int scale = deciwidth;
							StringBuffer formatStyle = new StringBuffer();
							for (int k = 0; k < scale; k++) {
								formatStyle.append("0");
							}
							
							String format = "";
							if (scale > 0) {// 没有数据格式则设置默认格式
								format = "0." + formatStyle.toString() + "_ ";
								bodyStyle.setDataFormat(df.getFormat(format));
							}else {
								bodyStyle.setDataFormat(df.getFormat("0.00"));
							}
				    		csCell.setCellValue(Double.parseDouble(desc));
				    	}
				    	else
				        	csCell.setCellValue(desc);
					}
					else
						csCell.setCellValue(desc);
					csCell.setCellStyle(bodyStyle);
					m++;
				}
				n++;
				sheetPerRows++;
			}
			mergedCellList = new ArrayList<LazyDynaBean>();
			mergedCellList.addAll(mergedCellStyleMap("合计", len+rowThird+1, len+rowThird+1, 0, 6, 3000*9, HorizontalAlignment.CENTER, (short)1, true));
			for(int i = 7; i < 40; i++) {
				String val = map.get(i) == null?"0.00":String.valueOf((BigDecimal) map.get(i));
				if(i == 39) {
					val = "";
				}
				mergedCellList.addAll(mergedCellStyleMap(val, len+rowThird+1, len+rowThird+1, i, i, 3000, HorizontalAlignment.RIGHT, (short)1, true));
			}
			for(int i = 0; i < mergedCellList.size(); i++) {
				LazyDynaBean headBean = (LazyDynaBean) mergedCellList.get(i);
				String itemdesc = (String) headBean.get("content");//当前列名标题
				int fromRowNum = (Integer) headBean.get("fromRowNum");
				int toRowNum = (Integer) headBean.get("toRowNum");
				int fromColNum = (Integer) headBean.get("fromColNum");
				int toColNum = (Integer) headBean.get("toColNum");
				
				//获得该单元格样式
				HashMap headStyleMap = (HashMap) headBean.get("mergedCellStyleMap");//当前列名标题样式
				sheet = setCell(workbook, headStyleMap, fromRowNum, fromColNum, toRowNum, toColNum, itemdesc, sheet, null, patr);
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;	
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
		return filename;
	}
	
	private HSSFSheet setCell(HSSFWorkbook workbook,HashMap headStyleMap, int fromRowNum,int fromColNum, int toRowNum, int toColNum, 
			String itemdesc,HSSFSheet sheet,String comment,HSSFPatriarch patr) {
		try {
			HSSFCellStyle headStyle = workbook.createCellStyle();
			short border = 1;
			if(headStyleMap.get("border") != null) {
				border = (Short) headStyleMap.get("border");
			}
			if(border != -1) {
				headStyle.setBorderBottom(BorderStyle.valueOf(border));
				headStyle.setBorderLeft(BorderStyle.valueOf(border));
				headStyle.setBorderRight(BorderStyle.valueOf(border));
				headStyle.setBorderTop(BorderStyle.valueOf(border));
			}
			HorizontalAlignment align = HorizontalAlignment.CENTER;
			if (headStyleMap.get("align") != null) {
				align = (HorizontalAlignment) headStyleMap.get("align");
			}
			headStyle.setAlignment(align);
			headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headStyle.setWrapText(true); 
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 12);
			font.setFontName("黑体");
			headStyle.setFont(font);
			sheet = this.executeCell(fromRowNum, fromColNum, toRowNum, toColNum, itemdesc, headStyle ,comment,patr, sheet);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sheet;
	}
	private ArrayList<LazyDynaBean> getStyleList(String name, String itemid, String comment, String colType, int fromRowNum, int toRowNum, int fromColNum, int toColNum, 
    		int columnWidth, String codeitemid) {
    	ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
    	try {
	    	LazyDynaBean bean = new LazyDynaBean(); 
	    	HashMap colStyleMap = new HashMap();
			bean.set("content", name);
			bean.set("itemid", itemid);// 列头代码，必须与sql语句的查询字段一一对应，必?选；
			bean.set("comment", comment);// 列头注释:主键标识串，可?选；
			bean.set("colType", colType);// 该列数据类型 ，A是字符型，D是日期型，N是数字型，可选，默认是A；
			bean.set("fromRowNum", fromRowNum);
			bean.set("toRowNum", toRowNum);
			bean.set("fromColNum", fromColNum);
			bean.set("toColNum", toColNum);
			bean.set("decwidth", "2");
			bean.set("codesetid", codeitemid);
			colStyleMap.put("align",HorizontalAlignment.CENTER);
			HashMap headStyleMap = new HashMap();
			headStyleMap.put("columnWidth", columnWidth);
			headStyleMap.put("fontSize", 12);
			headStyleMap.put("fontName", "黑体");
			bean.set("colStyleMap", colStyleMap);
			bean.set("headStyleMap", headStyleMap);
			headList.add(bean);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return headList;
    }
	
	private ArrayList<LazyDynaBean> mergedCellStyleMap(String name, int fromRowNum, int toRowNum, int fromColNum, int toColNum, int columnWidth,
			HorizontalAlignment align, Short border, boolean show_btborder) {
    	ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
    	try {
    		LazyDynaBean bean = new LazyDynaBean(); 
			bean.set("content", name);
			bean.set("fromRowNum", fromRowNum);
			bean.set("toRowNum", toRowNum);
			bean.set("fromColNum", fromColNum);
			bean.set("toColNum", toColNum);
			HashMap headStyleMap = new HashMap();
			headStyleMap.put("columnWidth", columnWidth);
			headStyleMap.put("fontSize", 12);
			headStyleMap.put("fontName", "黑体");
			headStyleMap.put("align", align);
			headStyleMap.put("border", border);
			if(!show_btborder) {
				headStyleMap.put("border_top", (short)-1);
				headStyleMap.put("border_bottom", (short)-1);
			}
			bean.set("mergedCellStyleMap", headStyleMap);
			headList.add(bean);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return headList;
    }
	
	/**
	 * @param fromtable 表名
	 * @param salaryid
	 * @param datetime 时间
	 * @param exporttype 导出类型
	 * @return 返回导出表头list和sql
	 */
	public HashMap<String, Object> getExportSqlForDetail(String fromtable ,String strwhere, HashMap list_code,String filterByMdule){
		HashMap<String, Object> map = new HashMap<String, Object>();
		//导出excel头部列
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean = null;
		//个税明细表获取固定字段
		TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView);
        StringBuffer income_sum = new StringBuffer();//累计收入额
        StringBuffer specialDed_sum = new StringBuffer();//累计专项扣除
        StringBuffer otherDed_sum = new StringBuffer();//累计其他扣除
        
        StringBuffer income_field = new StringBuffer();//累计收入额指标
        StringBuffer specialDed_field = new StringBuffer();//累计专项扣除指标
        StringBuffer otherDed_field = new StringBuffer();//累计其他扣除指标
        
        StringBuffer sum_sp = new StringBuffer();//子女教育等指标
        String jmse = "";//减免税额
		StringBuffer feildstr = new StringBuffer();
		StringBuffer exits_filed = new StringBuffer(",");
		String itemType = "A";
		String needStr = ",a0000,a00z0,a00z1,b0110,e0122,";
		feildstr.append(",row_number() over(  order by max(dbid),max(a0000),max(a00z0),max(a00z1),max(b0110),max(e0122))  as id");
		Iterator iter = list_code.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (Integer) entry.getKey();
			String val = (String) entry.getValue();
			String value_id = val.split(",")[0];
			
			if(exits_filed.indexOf("," + value_id + ",") != -1) {
				continue;
			}
			if(StringUtils.isNotBlank(value_id)) {
				String[] val_temp =  value_id.split("\\+");
				for(int i = 0; i < val_temp.length; i++) {
					String value_id_ = val_temp[i].trim().toUpperCase();
					if(key == 7 || key == 8) {
		            	 income_sum.append("+" + Sql_switcher.isnull("sum(" + value_id_ + ")", "0"));
		            	 income_field.append("+" + value_id_);
					}else if(key == 11 || key == 12 || key == 13 || key == 14) {
		            	 specialDed_sum.append("+" + Sql_switcher.isnull("sum(" + value_id_ + ")","0"));
		            	 specialDed_field.append("+" + value_id_);
		             }else if(key == 15 || key == 16 || key == 17 || key == 20) {
		            	 otherDed_sum.append("+" + Sql_switcher.isnull("sum(" + value_id_ + ")","0"));
		            	 otherDed_field.append("+" + value_id_);
		             }else if(key == 36) {
		            	 jmse = value_id_;
		             }
				}
				
			}
			bean = new LazyDynaBean();
			exits_filed.append(value_id + ",");
			
			if(value_id.contains("+")) {
				String[] val_temp =  value_id.split("\\+");
				val = "";
				for(int i = 0; i < val_temp.length; i++) {
					if(StringUtils.isNotBlank(val_temp[i].trim())) {
						val += "+" + Sql_switcher.isnull("sum("+val_temp[i].trim()+")","0");
					}
				}
				feildstr.append("," + val.substring(1) + " as " + value_id.replace("+", "_"));
			}else if(24 <= key && key <= 28) {
				sum_sp.append(",sum("+value_id+") as "+value_id);
			}else if((7 <= key && key <= 20 && key != 10) || key == 30 || key == 31 || key == 36) {
				val="sum("+value_id+") as "+value_id;
		 		feildstr.append(","+val);
			}else if(StringUtils.isNotBlank(value_id)) {
				val="max("+value_id+") as "+value_id;
		 		feildstr.append(","+val);
			}
		}
		StringBuffer sqlsb = new StringBuffer("select a0100,max(A0000) as a0000,max(a00z0) as a00z0,max(a00z1) as a00z1,max(b0110) as b0110,"
				+ "max(e0122) as e0122,max(nbase) as nbase," + Sql_switcher.isnull("sum(SDS)", "0") + " as YBSE_,sum(SDS)" + (StringUtils.isNotBlank(jmse)?("-" + Sql_switcher.isnull("sum(" + jmse + ")","0")):"") + " AS YBSE ");
		//权限
		String pre = taxbo.getPrivPre(filterByMdule);
		//日期
		sqlsb.append(feildstr+" from "+fromtable+" left join (select dbid,pre from dbname) dbname on upper("+fromtable+".nbase)=upper(dbname.pre) ");
		sqlsb.append(" where ("+pre+")");
		if(StringUtils.isNotBlank(strwhere))
		{
			sqlsb.append(strwhere);
		}
		
		
		sqlsb.append(" group by upper(nbase),a0100,"+Sql_switcher.dateToChar("tax_date","yyyy-MM")+",taxmode,taxunit");
		StringBuffer ss= new StringBuffer();
		if (Sql_switcher.searchDbServer() == 2) {  //oracle
			ss.append(" select t.*,f.*,H.*,F.sds_all-T.YBSE_ as YJSE  from (");
		}else {
			ss.append(" select *,F.sds_all-T.YBSE_ as YJSE  from (");
		}
		ss.append(sqlsb.toString());
		ss.append(") T,(select max(nbase) as nbase,a0100,sum(income) as income, SUM(special) as special, SUM(other) as other" + sum_sp + ",sum(sds_all) AS sds_all from (");
		ss.append(getSql(income_field.toString(), income_sum.toString(), specialDed_field.toString(), specialDed_sum.toString(), 
				otherDed_field.toString(), otherDed_sum.toString(), sum_sp.toString(), "gz_tax_mx", strwhere));
		ss.append(" union all ");
		ss.append(getSql(income_field.toString(), income_sum.toString(), specialDed_field.toString(), specialDed_sum.toString(), 
				otherDed_field.toString(), otherDed_sum.toString(), sum_sp.toString(), "taxarchive", strwhere));
		ss.append(") d group by upper(nbase),a0100) F");//这里为了在
		ss.append(",(select max(ljsde) as ljsde,max(sl) as sl,max(sskcs) as sskcs,max(ljse) as ljse,max(A0100) as a0100,max(nbase) as nbase from " + fromtable + " where ("+pre+")" +
					(StringUtils.isNotBlank(strwhere)?strwhere:"") + " group by a0100,upper(nbase)) H ");
		ss.append(" where upper(T.nbase) = upper(F.nbase) and T.a0100 = F.a0100 and upper(T.nbase) = upper(H.nbase) and T.a0100 = H.a0100");
		
		sqlsb.setLength(0);
		sqlsb.append(ss.toString());
		
		if(StringUtils.isNotBlank(income_field.toString()))
			map.put("income_field", "∑(" + income_field.substring(1) + ")");
		if(StringUtils.isNotBlank(specialDed_field.toString()))
			map.put("specialDed_field", "∑(" + specialDed_field.substring(1) + ")");
		if(StringUtils.isNotBlank(otherDed_field.toString()))
			map.put("otherDed_field", "∑(" + otherDed_field.substring(1) + ")");
		map.put("ybse", "SDS " + (StringUtils.isNotBlank(jmse)?("-" + jmse):"") );
		map.put("sql", sqlsb);
		return map;
	}
	
	private String getSql(String income_field, String income_sum, String specialDed_field, String specialDed_sum, 
			String otherDed_field, String otherDed_sum, String sum_sp, String fromtable, String strwhere) {
		StringBuffer ss = new StringBuffer();
		try {
			ss.append("select max(nbase) as nbase,a0100");
			if(StringUtils.isNotBlank(income_field.toString()))
				ss.append("," + income_sum.substring(1) + " as income");
			else {
				ss.append(",0  as income");
			}
			if(StringUtils.isNotBlank(specialDed_field.toString()))
				ss.append("," + specialDed_sum.substring(1) + " as special");
			else {
				ss.append(",0  as special");
			}
			
			if(StringUtils.isNotBlank(otherDed_field.toString()))
				ss.append("," + otherDed_sum.substring(1) + " as other");
			else {
				ss.append(",0  as other");
			}
			strwhere = strwhere.replace("MONTH(Declare_tax)=", "MONTH(Declare_tax)<=").replace("EXTRACT(MONTH FROM Declare_tax)=", "EXTRACT(MONTH FROM Declare_tax)<=");
			ss.append(sum_sp + ",sum(SDS) AS sds_all from " + fromtable + " where 1=1 " + strwhere);
			ss.append(" group by upper(nbase),a0100");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ss.toString();
	}
	
	public HSSFSheet executeCell(int fromRowNum, int fromColNum, int toRowNum, int toColNum, String content,HSSFCellStyle cellStyle,String comment,HSSFPatriarch patr,HSSFSheet sheet) {
		try {
			//取得第fromRowNum行
			HSSFRow row = sheet.getRow(fromRowNum);
			if(row==null)
				row = sheet.createRow(fromRowNum);
			//取得fromColNum列的单元格
			HSSFCell cell = row.getCell(fromColNum);
			if(cell==null)
				cell = row.createCell(fromColNum);
			//设置该单元格样式
			row.setHeight((short)600);
			//给该单元格赋值
			Pattern pattern = Pattern.compile("^-?[0-9]+.*[0-9]*$");
			
			if(StringUtils.isNotBlank(content) && pattern.matcher(content).matches()) {
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
				cell.setCellValue(Double.parseDouble(content));
			}else {
				cell.setCellValue(new HSSFRichTextString(content));
			}
			cell.setCellStyle(cellStyle);
			if(StringUtils.isNotBlank(comment)){//当注释不为空时
				HSSFComment comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (fromColNum + 1), 0, (short) (fromColNum + 2), 1));
				comm.setString(new HSSFRichTextString(comment));
				cell.setCellComment(comm);
			}
			
			int fromColNum1 = fromColNum;
			while (++fromColNum1 <= toColNum) {
				cell = row.getCell(fromColNum1);
				if(cell==null)
					cell = row.createCell(fromColNum1);
				cell.setCellStyle(cellStyle);
			}
			for (int fromRowNum1 = fromRowNum + 1; fromRowNum1 <= toRowNum; fromRowNum1++) {
				row = sheet.getRow(fromRowNum1);
				if(row==null)
					row = sheet.createRow(fromRowNum1);
				fromColNum1 = fromColNum;
				while (fromColNum1 <= toColNum) {
					cell = row.getCell(fromColNum1);
					if(cell==null)
						cell = row.createCell(fromColNum1);
					cell.setCellStyle(cellStyle);
					fromColNum1++;
				}
			}
			//合并单元格
			if(toRowNum > fromRowNum || toColNum > fromColNum)
				sheet.addMergedRegion(new CellRangeAddress(fromRowNum, toRowNum, (short) fromColNum, (short)toColNum));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sheet;
	}
	
	/**
	 * 取得权限过滤语句
	 * @return
	 * @see #hasModulePriv()
	 */
	public String getPrivPre(String fromTable)
	{
		TaxMxBo tax = new TaxMxBo(this.frameconn, this.userView);
		StringBuffer pre=new StringBuffer();
		StringBuffer prelast=new StringBuffer(" (");
		try {
			ArrayList list = userView.getPrivDbList();
			StringBuffer nbases = new StringBuffer();
			for (Object object : list) {
				nbases.append("'"+object+"',");
			}
			nbases.setLength(nbases.length()-1);
			if(userView.isSuper_admin()){
				prelast.append("1=1)");
			}else
			{
				if(list==null||list.size()<=0)
				{
					pre.append("1=2");
				}
				else
				{
					String nunit=userView.getUnitIdByBusi("3");
					pre.append(" 1=2 ");
					String unitarr[] =nunit.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
						String codeid=unitarr[i];
						if(codeid==null|| "".equals(codeid))
							continue;
						if(codeid!=null&&codeid.trim().length()>2)
						{
							if("true".equalsIgnoreCase(tax.getDeptID())){
								pre.append(" or (case when nullif(" + fromTable + ".deptid,'') is not null then deptid ");
								if("UN".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" else " + fromTable + ".B0110 end like  '"+codeid.substring(2)+"%') ");
								}
								else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" else " + fromTable + ".e0122 end like  '"+codeid.substring(2)+"%') ");
								}
							}else{
								if("UN".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" or " + fromTable + ".B0110  like  '"+codeid.substring(2)+"%' ");
								}
								else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" or " + fromTable + ".e0122  like  '"+codeid.substring(2)+"%' ");
								}
							}
			         	}
						else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
						{
							pre.append(" or 1=1 ");
			         	}
					}
					pre.append(")");
					if(nbases.length()>0){
						//Oracle 数据库区分大小写
						pre.append(" and UPPER(" + fromTable + ".nbase) in ("+nbases.toString().toUpperCase()+") ");
					} else {
						pre.append(" and 1=2 ");
					}
				}
				prelast.append(pre);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return prelast.toString();
	}
	
	public boolean compareMap2(HashMap old_data,HashMap new_data){
		boolean flag = true;
		try {
	        if(old_data==null || old_data.size() == 0 || new_data == null || new_data.size() == 0) {
	        	return false;
	        }
	        Iterator iter1 = old_data.entrySet().iterator();
	        while(iter1.hasNext()){
	        	Map.Entry entry = (Map.Entry) iter1.next();
	        	Object key = entry.getKey();
	        	Object val = entry.getValue();
	            String m1value = val == null?"":String.valueOf(val);
	            String m2value =  new_data.get(key)==null?"":String.valueOf(new_data.get(key));
	                  	
	            if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
	            	flag = false;
	            	break;
	            }
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
    }
}
