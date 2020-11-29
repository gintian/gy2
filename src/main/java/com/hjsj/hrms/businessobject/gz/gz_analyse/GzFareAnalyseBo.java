package com.hjsj.hrms.businessobject.gz.gz_analyse;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class GzFareAnalyseBo {
	private Connection conn;
	private String totalAmount="";//计划额总额
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public GzFareAnalyseBo(Connection conn)
	{
		this.conn=conn;
	}
	public HashMap getXtlist(String setid,String planitem,String realitem,String balanceitem,String yearf,String code,int type)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select sum(");
			buf.append(planitem);
			buf.append(") as ");
			buf.append(planitem);
			buf.append(",sum(");
			buf.append(realitem);
			buf.append(") as ");
			buf.append(realitem);
			buf.append(",sum(");
			buf.append(balanceitem);
			buf.append(") as "+balanceitem+" from ");
			buf.append(setid+",organization o ");
			buf.append(" where o.codeitemid="+setid+".b0110 and ");
			buf.append(Sql_switcher.year(setid+"."+setid+"z0")+"='");
			buf.append(yearf+"' ");
			String codeSql = getCodeSql(code);
			buf.append(codeSql);
			buf.append(" group by "+setid+"."+setid+"z0 order by "+setid+"."+setid+"z0");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			//System.out.println(buf);
			rs = dao.search(buf.toString());
			if(type==1)
			{
     			ArrayList planlist = new ArrayList();
	    		ArrayList reallist = new ArrayList();
    			int i=1;
    			BigDecimal all=new BigDecimal("0");
     			while(rs.next())
    			{
	    			CommonData plan = new CommonData();
	    			all=all.add(new BigDecimal((rs.getString(planitem)==null?"0":rs.getString(planitem))));
	    			plan.setDataValue(rs.getString(planitem)==null?"0":rs.getString(planitem));
	     			plan.setDataName(i+"月");
	    			planlist.add(plan);
	    			CommonData real = new CommonData();
	    			real.setDataName(i+"月");
		    		real.setDataValue(rs.getString(realitem)==null?"0":rs.getString(realitem));

		    		reallist.add(real);
		    		i++;
	    		}
     			this.totalAmount=GzAnalyseBo.div(all.toString(), "1",2);
	    		map.put("计划工资额", planlist);
	    		map.put("实发工资额", reallist);
			}
			else if(type==3)
			{
				ArrayList list = new ArrayList();
				int i=1;
				BigDecimal all=new BigDecimal("0");
     			while(rs.next())
    			{
     				ArrayList alist = new ArrayList();
     				LazyDynaBean abean = new LazyDynaBean();
	    			CommonData plan = new CommonData();
	    			all=all.add(new BigDecimal((rs.getString(planitem)==null?"0":rs.getString(planitem))));
	    			plan.setDataValue(rs.getString(planitem)==null?"0":rs.getString(planitem));
	     			plan.setDataName("计划工资额");
	    			CommonData real = new CommonData();
	    			real.setDataName("实发工资额");
		    		real.setDataValue(rs.getString(realitem)==null?"0":rs.getString(realitem));
		    		alist.add(plan);
		    		alist.add(real);
		    		abean.set("categoryName", i+"月");
   				    abean.set("dataList",alist);
   				    list.add(abean);
		    		i++;
	    		}
     			this.totalAmount=GzAnalyseBo.div(all.toString(), "1",2);
     			map.put("list", list);
			}
			else if(type==2)
			{
				ArrayList list = new ArrayList();
				int i=1;
				BigDecimal all=new BigDecimal("0");
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("month",i+"");
					all=all.add(new BigDecimal((rs.getString(planitem)==null?"0":rs.getString(planitem))));
					bean.set("planitem",GzAnalyseBo.div(rs.getString(planitem)==null?"0":rs.getString(planitem), "1", 2));
					bean.set("realitem",GzAnalyseBo.div(rs.getString(realitem)==null?"0":rs.getString(realitem), "1", 2));
					bean.set("balanceitem",GzAnalyseBo.div(rs.getString(balanceitem)==null?"0":rs.getString(balanceitem), "1", 2));
					String str=GzAnalyseBo.sub(rs.getString(realitem)==null?"0":rs.getString(realitem), rs.getString(planitem)==null?"0":rs.getString(planitem), 2);
					if(str.startsWith("-"))
					{
						bean.set("adde","");
					}
					else
					{
				    	String temp=GzAnalyseBo.div(str,rs.getString(planitem)==null?"1":rs.getString(planitem), 4);
				    	bean.set("adde",GzAnalyseBo.div(String.valueOf(Double.parseDouble(temp)*100), "1", 2));
					}
					list.add(bean);
					i++;
				}
				this.totalAmount=GzAnalyseBo.div(all.toString(), "1",2);
				map.put("list",list);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList getZtlist(String setid,String planitem,String realitem,String balanceitem,String yearf,String code)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select sum(");
			buf.append(planitem);
			buf.append(") as ");
			buf.append(planitem);
			buf.append(",sum(");
			buf.append(realitem);
			buf.append(") as ");
			buf.append(realitem);
			buf.append(",sum(");
			buf.append(balanceitem);
			buf.append(") as "+balanceitem+" from ");
			buf.append(setid+",organization o ");
			buf.append(" where o.codeitemid="+setid+".b0110 and ");
			buf.append(Sql_switcher.year(setid+"."+setid+"z0")+"= '");
			buf.append(yearf+"' ");
			String codeSql = getCodeSql(code);
			buf.append(codeSql);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			//System.out.println(buf);
			rs = dao.search(buf.toString());
			BigDecimal all=new BigDecimal("0");
			while(rs.next())
			{
				all=all.add(new BigDecimal((rs.getString(planitem)==null?"0":rs.getString(planitem))));
				CommonData data = new CommonData(rs.getString(planitem)==null?"0":rs.getString(planitem),"全年计划总额");
				list.add(data);
				data =new CommonData(rs.getString(realitem)==null?"0":rs.getString(realitem),"累计实发额");
				list.add(data);
				data = new CommonData(rs.getString(balanceitem)==null?"0":rs.getString(balanceitem),"全年剩余计划总额");
				list.add(data);
			}
			this.totalAmount=GzAnalyseBo.div(all.toString(), "1",2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getBtlist(String setid,String planitem,String realitem,String balanceitem,String yearf,String code)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select sum(");
			buf.append(planitem);
			buf.append(") as ");
			buf.append(planitem);
			buf.append(",sum(");
			buf.append(realitem);
			buf.append(") as ");
			buf.append(realitem);
			buf.append(",sum(");
			buf.append(balanceitem);
			buf.append(") as "+balanceitem+" from ");
			buf.append(setid+",organization o ");
			buf.append(" where o.codeitemid="+setid+".b0110 and ");
			buf.append(Sql_switcher.year(setid+"."+setid+"z0")+" = '");
			buf.append(yearf+"' ");
			String codeSql = getCodeSql(code);
			buf.append(codeSql);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			//System.out.println(buf);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				if(rs.getString(balanceitem)!=null)
				{	
			    	CommonData data =new CommonData(rs.getString(realitem)==null?"0":rs.getString(realitem),"累计实发额");
			     	list.add(data);
			     	data = new CommonData(rs.getString(balanceitem),"全年剩余计划总额");
			    	list.add(data);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getFileName(UserView view,ArrayList list,String year,String code,String setid,String chartkind,String name,String planItemDesc,String adjustSet,String adjustDesc)
	{
		String filename=view.getUserFullName()+"_"+PubFunc.getStrg()+".xls";
		try
		{
			String str=name;
			if(name.endsWith("总额"))
				str=name;
			else
				str=name+"总额";
			HashMap map = this.getYdList(setid, year, code, list);
			String total=(String)map.get("total");
			ArrayList dataList = (ArrayList)map.get("list");
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFFont font = workbook.createFont();
			String codesetid=code.substring(0,2);
			String codevalue=code.substring(2);
			String title=year+"年度"+AdminCode.getCodeName(codesetid,codevalue)+("".equals(str)?"工资总额":str)+"情况使用表";
			sheet=workbook.createSheet();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFCellStyle aStyle=workbook.createCellStyle();
			aStyle.setFont(font);
			aStyle.setAlignment(HorizontalAlignment.LEFT);
			aStyle.setBorderBottom(BorderStyle.THIN);
			aStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			aStyle.setBorderLeft(BorderStyle.THIN);
			aStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			aStyle.setBorderRight(BorderStyle.THIN);
			aStyle.setRightBorderColor(HSSFColor.BLACK.index);
			aStyle.setBorderTop(BorderStyle.THIN);
			aStyle.setTopBorderColor(HSSFColor.BLACK.index);
			aStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFFont afont = workbook.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			HSSFCellStyle abStyle=workbook.createCellStyle();
			abStyle.setFont(afont);
			abStyle.setAlignment(HorizontalAlignment.CENTER);
			abStyle.setBorderBottom(BorderStyle.THIN);
			abStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderLeft(BorderStyle.THIN);
			abStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderRight(BorderStyle.THIN);
			abStyle.setRightBorderColor(HSSFColor.BLACK.index);
			abStyle.setBorderTop(BorderStyle.THIN);
			abStyle.setTopBorderColor(HSSFColor.BLACK.index);
			abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			// 这里添加左右位置的，否则判断修改abStyle的setAlignment不起效果
			HSSFCellStyle leftDataStyle = getStyle(workbook, afont, HorizontalAlignment.LEFT);
			HSSFCellStyle rightDataStyle = getStyle(workbook, afont, HorizontalAlignment.RIGHT);
			
			short n=0;
			row=sheet.getRow(n);
			if(row==null)
				row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(title);
			cell.setCellStyle(cellStyle);
			for(int i=1;i<=(list.size()*2+4);i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0, (short)(list.size()*2+4));
			n++;
			row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(str+":"+total);
			cell.setCellStyle(aStyle);
			for(int i=1;i<=(list.size()*2+4);i++)
			{
				cell=row.createCell(i);
				cell.setCellStyle(cellStyle);
			}
			ExportExcelUtil.mergeCell(sheet, 1, (short)0, 1, (short)(list.size()*2+4));
			//-------------------
			n++;
			row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("月份");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, (short)0, n+1, (short)(0));
			short cloumnIndex=1;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String itemdesc=(String)bean.get("planitemdesc");
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(itemdesc);
				cell.setCellStyle(cellStyle);
				ExportExcelUtil.mergeCell(sheet, n, cloumnIndex, n, (short)(cloumnIndex+1));
				cloumnIndex++;
				cloumnIndex++;
			}
			cell=row.createCell(cloumnIndex);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("月度发生额");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, cloumnIndex, n+1, (short)(cloumnIndex));
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("年累计发生额");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, cloumnIndex, n+1, (short)(cloumnIndex));
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("年度剩余额");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, cloumnIndex, n+1, (short)(cloumnIndex));
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue("预算完成率");
			cell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, n, cloumnIndex, n+1, (short)(cloumnIndex));
			cloumnIndex++;
			n++;
			cloumnIndex=1;
			row=sheet.createRow(n);
			cell=row.createCell(0);
			cell.setCellStyle(cellStyle);
			for(int i=0;i<list.size();i++)
			{
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("月度发生额");
				cell.setCellStyle(cellStyle);
				cloumnIndex++;
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("使用占比");
				cell.setCellStyle(cellStyle);
				cloumnIndex++;
			}
			cell=row.createCell(cloumnIndex);
			cell.setCellStyle(cellStyle);
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellStyle(cellStyle);
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellStyle(cellStyle);
			cloumnIndex++;
			cell=row.createCell(cloumnIndex);
			cell.setCellStyle(cellStyle);
			cloumnIndex++;
			n++;
			for(int i=0;i<dataList.size();i++)
			{
				row=sheet.createRow(n);
				cloumnIndex=0;
				LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
				cell=row.createCell(cloumnIndex);
				// 根据type来判断，month是日期，除了数值的CELL_TYPE_NUMERIC，其余都是靠做对齐
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(leftDataStyle);
				cell.setCellValue((String)bean.get("month"));
				cloumnIndex++;
				for(int j=0;j<list.size();j++)
				{
					LazyDynaBean abean=(LazyDynaBean)list.get(j);
					String planitem=(String)abean.get("planitem");
					String realitem=(String)abean.get("realitem");
					cell=row.createCell(cloumnIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(rightDataStyle);
					cell.setCellValue(Double.parseDouble((String)bean.get(planitem.toLowerCase())));
					cloumnIndex++;
					cell=row.createCell(cloumnIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(rightDataStyle);
					cell.setCellValue((String)bean.get(realitem.toLowerCase()));
					cloumnIndex++;
				}
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(rightDataStyle);
				cell.setCellValue(Double.parseDouble((String)bean.get("ydfse")));
				cloumnIndex++;
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(rightDataStyle);
				cell.setCellValue(Double.parseDouble((String)bean.get("ljfs")));
				cloumnIndex++;
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(rightDataStyle);
				cell.setCellValue(Double.parseDouble((String)bean.get("ndsy")));
				cloumnIndex++;
				cell=row.createCell(cloumnIndex);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(leftDataStyle);
				cell.setCellValue((String)bean.get("yswcl"));
				cloumnIndex++;
				n++;
			}
			ArrayList tableHeaderList = new ArrayList();
			ArrayList adjustDataList = new ArrayList();
			if(!"-1".equals(adjustSet)&&!" ".equals(adjustSet))//没有设置总额调整子集，就不要导出总额调整记录了，否则sql语句报错  zhaoxg add 2015-3-25
			{
				adjustDataList = this.getAdjustList(adjustSet, code, planItemDesc, year, adjustDesc);
				tableHeaderList = this.getTableHeaderList();
				row=sheet.createRow(n);
				cell=row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellStyle(abStyle);
				cell.setCellValue("总额调整记录");
				n++;
				row=sheet.createRow(n);
				for(int h=0;h<tableHeaderList.size();h++)
				{
					FieldItem item = (FieldItem)tableHeaderList.get(h);
					cell=row.createCell(h);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(abStyle);
					cell.setCellValue(item.getItemdesc());
				}
				n++;
				for(int h=0;h<adjustDataList.size();h++)
				{
					row=sheet.createRow(n);
					LazyDynaBean bean =(LazyDynaBean)adjustDataList.get(h);
					for(int l=0;l<tableHeaderList.size();l++)
					{
						FieldItem fi = (FieldItem)tableHeaderList.get(l);
						cell=row.createCell(l);
						if("N".equalsIgnoreCase(fi.getItemtype()))
						{
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellStyle(rightDataStyle);
						}
						else
						{
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellStyle(leftDataStyle);
						}
						String value=(String)bean.get(fi.getItemid().toLowerCase());
						if(value!=null&&!"".equals(value))
						{
							if("N".equalsIgnoreCase(fi.getItemtype()))
								cell.setCellValue(Double.parseDouble(value));
							else
								cell.setCellValue(value);
						}
					}
					n++;
				}
			}
			int cloumn=list.size()+8;
			if(tableHeaderList.size()>cloumn)
				cloumn=tableHeaderList.size();
			for(int i = 0; i <=cloumn; i++)
			{
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)5000);
			}
			for(int i = 0; i <=n; i++)
			{
			    row = sheet.getRow(i);
				if(row==null)
					 row = sheet.createRow(i);
			    row.setHeight((short) 400);
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
			workbook = null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return filename;
	}
	
	/**
	 * 设置cellStyle的样式，主要是字体和居左还是居右
	 * @param workbook
	 * @param afont
	 * @param align
	 * @return
	 */
	private HSSFCellStyle getStyle(HSSFWorkbook workbook, HSSFFont afont, HorizontalAlignment align) {
		HSSFCellStyle abStyle=workbook.createCellStyle();
		abStyle.setFont(afont);
		abStyle.setAlignment(align);
		abStyle.setBorderBottom(BorderStyle.THIN);
		abStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderLeft(BorderStyle.THIN);
		abStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderRight(BorderStyle.THIN);
		abStyle.setRightBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderTop(BorderStyle.THIN);
		abStyle.setTopBorderColor(HSSFColor.BLACK.index);
		abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return abStyle;
	}
	/**
	 * 北京移动需求单独的方法
	 * @param setid
	 * @param yearf
	 * @param code
	 * @param plist
	 * @return
	 */
	public HashMap getYdList(String setid,String yearf,String code,ArrayList plist)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ArrayList list = new ArrayList();
			StringBuffer select = new StringBuffer();
			StringBuffer sumsql = new StringBuffer("");
			for(int i=0;i<plist.size();i++)
			{
				LazyDynaBean bean =(LazyDynaBean)plist.get(i);
				String planitem=(String)bean.get("planitem");
				String realitem=(String)bean.get("realitem");
				String balanceitem=(String)bean.get("balanceitem");
				select.append(","+planitem+","+realitem+","+balanceitem);
				sumsql.append(",sum("+planitem+") as "+planitem);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			String codeSql = getCodeSql(code);
			StringBuffer buf = new StringBuffer("");
			buf.append("select "+sumsql.toString().substring(1));
			buf.append(" from "+setid+",organization o ");
			buf.append(" where o.codeitemid="+setid+".b0110 and ");
			buf.append(Sql_switcher.year(setid+"."+setid+"z0")+" = '");
			buf.append(yearf+"' ");
			buf.append(codeSql);
			rs = dao.search(buf.toString());
			BigDecimal totalbd=new BigDecimal("0");
			while(rs.next())
			{
				for(int i=0;i<plist.size();i++)
				{
					LazyDynaBean bean =(LazyDynaBean)plist.get(i);
					String planitem=(String)bean.get("planitem");
					totalbd=totalbd.add(new BigDecimal(rs.getString(planitem)==null?"0":rs.getString(planitem)));
				}
			}
			if(rs!=null)
			{
				rs=null;
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select "+(select.toString().substring(1)));
			sql.append(","+setid+"."+setid+"z0 from "+setid+",organization o ");
			sql.append(" where o.codeitemid="+setid+".b0110 and ");
			sql.append(Sql_switcher.year(setid+"."+setid+"z0")+" = '");
			sql.append(yearf+"' ");
			sql.append(codeSql+" order by "+setid+"."+setid+"z0");//" group by "+setid+"."+setid+"z0
			rs=dao.search(sql.toString());
			int i=1;
			BigDecimal one = new BigDecimal("1");
			BigDecimal zero = new BigDecimal("0");
			BigDecimal ljReal = new BigDecimal("0");
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM");
			BigDecimal a100 = new BigDecimal("100");
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				BigDecimal plan=new BigDecimal("0");
				BigDecimal real=new BigDecimal("0");
				BigDecimal balance=new BigDecimal("0");
				for(int j=0;j<plist.size();j++)
				{
					LazyDynaBean abean =(LazyDynaBean)plist.get(j);
					String planitem=(String)abean.get("planitem");
					String realitem=(String)abean.get("realitem");
					String balanceitem=(String)abean.get("balanceitem");
					if(j==0)
					{
				    	for(int k=0;k<plist.size();k++)
				    	{
				    		LazyDynaBean bbean=(LazyDynaBean)plist.get(k);
					    	String aplanitem=(String)bbean.get("planitem");
					    	String arealitem=(String)bbean.get("realitem");
						    String abalanceitem=(String)bbean.get("balanceitem");
					    	plan=plan.add(new BigDecimal((rs.getString(aplanitem)==null?"0":rs.getString(aplanitem))));
					    	real=real.add(new BigDecimal((rs.getString(arealitem)==null?"0":rs.getString(arealitem))));
					        balance=balance.add(new BigDecimal((rs.getString(abalanceitem)==null?"0":rs.getString(abalanceitem))));				        
				    	}
				    	ljReal=ljReal.add(real);
					}
					/*if(values.indexOf("E")!=-1)
					{
						String temp=values.substring(0,values.indexOf("E"));
						String aa=values.substring(values.indexOf("E")+1);
						//System.out.println((new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).toString()));
						values=(new BigDecimal(Math.pow(10,Integer.parseInt(aa.trim()))).multiply(new BigDecimal(temp))).toString();//         Double.parseDouble(temp)*Math.pow(10,Double.parseDouble(aa));
						//values=tt+"";
					}
					
					values=values!=null&&values.trim().length()>0?values:"0.0";
					dvalues = values.substring(0,values.indexOf("."));*/
					BigDecimal rbd=new BigDecimal((rs.getString(realitem)==null?"0":rs.getString(realitem)));
					bean.set(planitem.toLowerCase(), rbd.divide(one, 2,BigDecimal.ROUND_HALF_UP).toString());
					if(real.compareTo(zero)==0)
						 bean.set(realitem.toLowerCase(), PubFunc.round(rbd.divide(one, 4,BigDecimal.ROUND_HALF_UP).multiply(a100).toString(),2)+"%");
					else
					   bean.set(realitem.toLowerCase(), PubFunc.round(rbd.divide(real, 4,BigDecimal.ROUND_HALF_UP).multiply(a100).toString(),2)+"%");
				}
				bean.set("month",rs.getDate(setid+"z0")==null?"":format.format(rs.getDate(setid+"z0")));
				bean.set("ydfse", real.divide(one, 2).toString());
				bean.set("ljfs", ljReal.divide(one, 2).toString());
				bean.set("ndsy", totalbd.subtract(ljReal).divide(one, 2).toString());
				if(totalbd.compareTo(zero)==0)
					bean.set("yswcl", PubFunc.round(ljReal.divide(one, 4,BigDecimal.ROUND_HALF_UP).multiply(a100).toString(), 2)+"%");
				else
			    	bean.set("yswcl", PubFunc.round(ljReal.divide(totalbd, 4,BigDecimal.ROUND_HALF_UP).multiply(a100).toString(), 2)+"%");
				list.add(bean);
			}
			map.put("list", list);
			map.put("total",totalbd.divide(one, 2).toString());
			
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
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	/**
	 * 单位或部门过滤的sql
	 * @param codeValue
	 * @return
	 */
	public String getCodeSql(String codeValue)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			if(codeValue==null|| "".equals(codeValue))
				buf.append("");
			else
			{
				String code = codeValue.substring(0,2);
				String value=codeValue.substring(2);
				if("UN".equalsIgnoreCase(code))
				{
					buf.append(" and o.codesetid='UN'");
					buf.append(" and (o.codeitemid ='");
					buf.append(value);
					buf.append("' ");
					if(value==null|| "".equals(value))
					{
						buf.append(" or o.codeitemid is null");
					}
					buf.append(")");
				}
				else if("UM".equalsIgnoreCase(code))
				{
					buf.append(" and o.codesetid='UM'");
					buf.append(" and (o.codeitemid ='");
					buf.append(value);
					buf.append("' ");
					if(value==null|| "".equals(value))
					{
						buf.append(" or o.codeitemid is null");
					}
					buf.append(")");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
		
	}
	public ArrayList getItemListByClass(ArrayList list,UserView userView)
	{
		ArrayList alist = new ArrayList();
		try
		{
			HashMap amap = new HashMap();
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String className=(String)bean.get("classname");
				if(className==null|| "".equals(className.trim()))
				{
					if("0".equals(userView.analyseFieldPriv((String)bean.get("planitem"))))
						continue;
					alist.add(new CommonData("3`"+(String)bean.get("planitem"),(String)bean.get("planitemdesc")));
				}
				else
				{
					if(amap.get(className.toUpperCase())==null)
					{
						if("0".equals(userView.analyseFieldPriv((String)bean.get("planitem"))))
							continue;
						alist.add(new CommonData("5`"+className,className));
						amap.put(className.toUpperCase(), "1");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return alist;
	}
	private ArrayList tableHeaderList = new ArrayList();
	public ArrayList getAdjustList(String setid,String code,String desc,String year,String descField)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			 StringBuffer buf = new StringBuffer();
			  StringBuffer select = new StringBuffer();
			  ArrayList fielditemlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
			  if(fielditemlist==null)
				  fielditemlist=new ArrayList();
			  for(int i=0;i<fielditemlist.size();i++)
			  {
				  FieldItem item=(FieldItem)fielditemlist.get(i);
				  if("1".equals(item.getState())&&!item.getItemid().equalsIgnoreCase(setid+"z0")&&!item.getItemid().equalsIgnoreCase(setid+"z1"))
				  {
			    	  select.append(","+item.getItemid());
			    	  tableHeaderList.add(item);
				  }
			  }
			  buf.append("select B0110,I9999"+select.toString());
			  buf.append(" from "+setid);
			  buf.append(" where ");
			  buf.append(Sql_switcher.year(setid+"z0")+"="+year);
			  if(code.length()==2)
			  {
				  
			  }
			  else
			  {
				  buf.append(" and b0110='"+code.substring(2)+"'");
			  }
			 FieldItem itemfield = DataDictionary.getFieldItem(descField);
			 if(itemfield==null)
				 buf.append(" and 1=2 ");
			 else
			 {
	    		 if(itemfield.isCode())
		    	 {
		    		 buf.append(" and "+descField+" in(select codeitemid from codeitem where UPPER(codesetid)='"+itemfield.getCodesetid()+"' and ");
		     		 buf.append("UPPER(codeitemdesc)='"+desc.toUpperCase()+"')");
		    	 }
		    	 else
		    	 {
		    	    buf.append(" and UPPER("+descField+")='"+desc.toUpperCase()+"'");
		    	 }
			 }
			  ContentDAO dao = new ContentDAO(this.conn);
			  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			  rs=dao.search(buf.toString());
			  while(rs.next())
			  {
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("b0110", rs.getString("b0110"));
				  bean.set("i9999", rs.getString("i9999"));
				  for(int i=0;i<fielditemlist.size();i++)
				  {
					  FieldItem item=(FieldItem)fielditemlist.get(i);
					  if("1".equals(item.getState())&&!item.getItemid().equalsIgnoreCase(setid+"z0")&&!item.getItemid().equalsIgnoreCase(setid+"z1"))
					  {
						  if("N".equalsIgnoreCase(item.getItemtype()))
						  {
							  bean.set(item.getItemid().toLowerCase(), PubFunc.round(rs.getString(item.getItemid()), item.getDecimalwidth()));
						  }
						  else if("D".equalsIgnoreCase(item.getItemtype()))
						  {
							  bean.set(item.getItemid().toLowerCase(), rs.getDate(item.getItemid())==null?"":format.format(rs.getDate(item.getItemid())));
						  }
						  else if("A".equalsIgnoreCase(item.getItemtype()))
						  {
							  if(!"0".equals(item.getCodesetid()))
							  {
								  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":AdminCode.getCodeName(item.getCodesetid(), rs.getString(item.getItemid())));
							  }
							  else
							  {
								  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid()));
							  }
						  }
						  else if("M".equalsIgnoreCase(item.getItemtype()))
						  {
							  bean.set(item.getItemid().toLowerCase(), Sql_switcher.readMemo(rs, item.getItemid()));
						  }
						  else
						  {
							  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid()));
						  }
					  }
				  }
				  list.add(bean);
			  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
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
	public ArrayList getTableHeaderList() {
		return tableHeaderList;
	}
	public void setTableHeaderList(ArrayList tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}

}
