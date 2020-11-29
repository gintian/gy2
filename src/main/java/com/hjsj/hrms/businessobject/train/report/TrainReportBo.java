package com.hjsj.hrms.businessobject.train.report;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:TrainReportBo.java
 * </p>
 * <p>
 * Description:培训报表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainReportBo
{
    private Connection cn;

    private String type;

    private String strSql;

    public TrainReportBo(Connection conn, String type)
    {

        this.cn = conn;
        this.type = type;
    }

    public ArrayList search(String code, String year, String quarter)
    {

        ArrayList list = new ArrayList();
        if (this.type == null || (this.type != null && "".equals(this.type))) {
            return list;
        }
        if ("1".equals(this.type)) {
            list = this.searchUn(code, year, quarter);
        } else if ("2".equals(this.type)) {
            list = this.searchTrainItem(code, year, quarter);
        } else if ("3".equals(this.type)) {
            list = this.searchTrainStu(code, year, quarter);
        }
        return list;
    }

    /**
     * 取得培训计划表相应条件（组织机构、年、季度）
     */
    private String getR25Whr(String code, String year, String quarter)
    {
        StringBuffer r25Whr = new StringBuffer();
        
        //组织机构条件
        if (code != null && code.length() > 2)
        {
//            String temp = code.substring(0, 2);
//            int len = code.length();
//            if (temp.equalsIgnoreCase("UN"))
//                r25Whr.append("and r25.b0110 like '" + code.substring(2, len) + "%' ");
//            
//            if (temp.equalsIgnoreCase("UM"))
//                r25Whr.append("and r25.e0122 like '" + code.substring(2, len) + "%' ");
        	String tmp[] = code.split("`");
        	r25Whr.append(" and (");
     		for (int i = 0; i < tmp.length; i++) {
     			String t = tmp[i];
     			if("UN".equalsIgnoreCase(t.substring(0,2))) {
                    r25Whr.append("r25.b0110 like '" + t.substring(2) + "%' or ");
                }
     			if("UM".equalsIgnoreCase(t.substring(0,2))) {
                    r25Whr.append("r25.e0122 like '" + t.substring(2) + "%' or ");
                }
     		}
     		r25Whr.setLength(r25Whr.length()-3);
     		r25Whr.append(")");
        }
        
        //年度条件
        if (year != null && !"".equals(year)) {
            r25Whr.append("and r25.r2503='" + year + "' ");
        }
        
        //季度条件
        if (quarter != null && !"".equals(quarter)) {
            r25Whr.append("and r25.r2504='" + quarter + "' ");
        }

        return r25Whr.toString();
    }
    
    // 培训报表--单位部门
    public ArrayList searchUn(String code, String year, String quarter)
    {
                
        ArrayList list = new ArrayList();
        
        String r25Whr = getR25Whr(code, year, quarter);
        
        StringBuffer strSql = new StringBuffer();
        //此处由于导excel顺序颠倒了 所以将sql语句中的usercount 和r2506换了一下位置
        strSql.append("select c.B0110,c.E0122,UserCount,r2506,R3111,ItemCount,R3110 FROM");
        strSql.append(" (select B0110,E0122,sum(r2506) as r2506 from r25");
        strSql.append(" where 1=1");
        if(!"".equals(r25Whr)) {
            strSql.append(r25Whr);
        }
        strSql.append(" group by B0110,E0122 ) c");
        strSql.append(" left join(");
        strSql.append(" select R31.B0110,R31.E0122,");
        strSql.append(" sum(a.UserCount) as UserCount,");
        strSql.append(" sum(R31.R3111) as R3111,sum(b.ItemCount) as ItemCount,");
        strSql.append(" sum(R31.R3110) as R3110");
        strSql.append(" from ((R31 left join");
        strSql.append(" (select R31.R3101, count(R41.R4101) as ItemCount");
        strSql.append(" from R31, R41 where R3127='06' and R31.R3101 = R41.R4103");
        strSql.append(" group by R31.R3101) b");
        strSql.append(" on r31.R3101=b.R3101)");
        strSql.append(" left join");
        strSql.append(" (select R31.R3101, count(*) as UserCount");
        strSql.append(" from R31 left join R40");
        strSql.append(" on R31.R3101=R40.R4005");
        strSql.append(" where R3127='06'");
        strSql.append(" group by R31.R3101) a");
        strSql.append(" on R31.R3101=a.R3101)");
        strSql.append(" where R31.R3127 = '06'");
        strSql.append(" group by R31.B0110, R31.E0122");
        strSql.append(" ) aaa");
        strSql.append(" on c.b0110=aaa.b0110"); 
        strSql.append(" where c.E0122 is not null");

        ContentDAO dao = new ContentDAO(this.cn);
        this.setStrSql(strSql.toString());
        try
        {
            RowSet rs = dao.search(strSql.toString());
            while (rs.next())
            {
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String userCount = rs.getString("usercount");
                String itemCount = rs.getString("itemcount");
                String r2506 = rs.getString("r2506");
                String r3111 = rs.getString("r3111");
                String r3110 = rs.getString("r3110");

                LazyDynaBean abean = new LazyDynaBean();
                abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
                abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
                abean.set("usercount", isNull(userCount));
                abean.set("itemcount", isNull(itemCount));
                abean.set("r2506", isNull(r2506));
                abean.set("r3111", isNull(r3111));
                abean.set("r3110", isNull(r3110));
                list.add(abean);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    // 培训报表--培训项目
    public ArrayList searchTrainItem(String code, String year, String quarter)
    {

        ArrayList list = new ArrayList();
        StringBuffer strSql = new StringBuffer("select R31.B0110,r31.e0122,r41.r4105,sum(r31.r3110) sumrs,sum(r41.r4112) sumks,sum(r41.r4116) sumfy ");
        strSql.append("from r31,r41 where r41.r4103=r31.r3101 and R31.R3127 = '06' ");
        if (code != null && code.length() > 2)
        {
//            String temp = code.substring(0, 2);
//            int len = code.length();
//            if (temp.equalsIgnoreCase("UN"))
//                strSql.append("and r31.b0110 like '" + code.substring(2, len) + "%' ");
//            if (temp.equalsIgnoreCase("UM"))
//                strSql.append("and r31.e0122 like '" + code.substring(2, len) + "%' ");
        	String tmp[] = code.split("`");
    	    strSql.append(" and (");
    		for (int i = 0; i < tmp.length; i++) {
    			String t = tmp[i];
    			if("UN".equalsIgnoreCase(t.substring(0,2))) {
                    strSql.append("r31.b0110 like '" + t.substring(2) + "%' or ");
                }
    			if("UM".equalsIgnoreCase(t.substring(0,2))) {
                    strSql.append("r31.e0122 like '" + t.substring(2) + "%' or ");
                }
    		}
    		strSql.setLength(strSql.length()-3);
    		strSql.append(")");
        }
        if (year != null && !"".equals(year)) {
            strSql.append("and  r31.r3119='" + year + "' ");
        }
        if (quarter != null && !"".equals(quarter)) {
            strSql.append("and r31.r3120='" + quarter + "' ");
        }
        strSql.append(" group by R31.B0110, R31.E0122,r41.r4105");
        this.setStrSql(strSql.toString());
        ContentDAO dao = new ContentDAO(this.cn);
        try
        {
            RowSet rs = dao.search(strSql.toString());
            while (rs.next())
            {
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String r4105 = rs.getString("r4105");// 培训类型
                String sumrs = rs.getString("sumrs");// 合计培训人数
                String sumks = rs.getString("sumks");// 合计培训课时
                String sumfy = rs.getString("sumfy");// 合计培训费用

                LazyDynaBean abean = new LazyDynaBean();
                abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
                abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
                abean.set("r4105", r4105 != null ? AdminCode.getCodeName("1_06", r4105) : "");
                abean.set("sumrs", isNull(sumrs));
                abean.set("sumks", isNull(sumks));
                abean.set("sumfy", isNull(sumfy));

                list.add(abean);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    // 培训报表--培训学员
    public ArrayList searchTrainStu(String code, String year, String quarter)
    {

        ArrayList list = new ArrayList();
        StringBuffer strSql = new StringBuffer("select R40.B0110, R40.E0122,R40.R4002,a.r4105,sum(a.ItemCount) itemcount,sum(R40.R4010) sumfy,sum(R40.R4008) sumks from  R40, (");
        strSql.append("select R31.R3101,R41.R4105, count(R41.R4101) as ItemCount from R31, R41 where R31.R3101 = R41.R4103 and R31.R3127 = '06' ");

        if (year != null && !"".equals(year)) {
            strSql.append("and  r31.r3119='" + year + "' ");
        }
        if (quarter != null && !"".equals(quarter)) {
            strSql.append("and r31.r3120='" + quarter + "' ");
        }
        strSql.append("group by R31.R3101,R41.R4105) a where R40.R4005 = a.R3101");
        if (code != null && code.length() > 2)
        {
//            String temp = code.substring(0, 2);
//            int len = code.length();
//            if (temp.equalsIgnoreCase("UN"))
//                strSql.append(" and r40.b0110 like '" + code.substring(2, len) + "%' ");
//            if (temp.equalsIgnoreCase("UM"))
//                strSql.append(" and r40.e0122 like '" + code.substring(2, len) + "%' ");
        	String tmp[] = code.split("`");
     	    strSql.append(" and (");
     		for (int i = 0; i < tmp.length; i++) {
     			String t = tmp[i];
     			if("UN".equalsIgnoreCase(t.substring(0,2))) {
                    strSql.append("r40.b0110 like '" + t.substring(2) + "%' or ");
                }
     			if("UM".equalsIgnoreCase(t.substring(0,2))) {
                    strSql.append("r40.e0122 like '" + t.substring(2) + "%' or ");
                }
     		}
     		strSql.setLength(strSql.length()-3);
     		strSql.append(")");
        }
        strSql.append(" group by R40.B0110, R40.E0122, R40.R4001, R40.R4002,a.R4105 order by R40.B0110, R40.E0122, R40.R4001, R40.R4002");
        this.setStrSql(strSql.toString());
        ContentDAO dao = new ContentDAO(this.cn);
        try
        {
            RowSet rs = dao.search(strSql.toString());
            while (rs.next())
            {
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String r4002 = rs.getString("r4002");
                String r4105 = rs.getString("r4105");
                String sumks = rs.getString("sumks");// 合计培训课时
                String sumfy = rs.getString("sumfy");// 合计培训费用
                String itemcount = rs.getString("itemcount");// 合计培训项目数

                LazyDynaBean abean = new LazyDynaBean();
                abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
                abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
                abean.set("r4105", r4105 != null ? AdminCode.getCodeName("1_06", r4105) : "");
                abean.set("r4002", isNull(r4002));
                abean.set("itemcount", isNull(itemcount));
                abean.set("sumks", isNull(sumks));
                abean.set("sumfy", PubFunc.DoFormatDecimal(sumfy, 2));

                list.add(abean);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    // 获得标题
    public ArrayList getTitle()
    {

        ArrayList list = new ArrayList();

        if ("1".equals(this.type))
        {
            LazyDynaBean abean = new LazyDynaBean();
            abean.set("title", "单位");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "部门");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "实有人数");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "计划预算");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "培训费用");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "培训项目数");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "培训人数");
            abean.set("datatype", "N");
            list.add(abean);
        }
        else if ("2".equals(this.type))
        {
            LazyDynaBean abean = new LazyDynaBean();
            abean.set("title", "单位");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "部门");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "培训类别");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训人数");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训课时");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训费用");
            abean.set("datatype", "N");
            list.add(abean);

        }
        else if ("3".equals(this.type))
        {
            LazyDynaBean abean = new LazyDynaBean();
            abean.set("title", "单位");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "部门");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "姓名");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "培训类别");
            abean.set("datatype", "A");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训项目数");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训费用");
            abean.set("datatype", "N");
            list.add(abean);

            abean = new LazyDynaBean();
            abean.set("title", "合计培训课时");
            abean.set("datatype", "N");
            list.add(abean);
        }
        return list;
    }

    public String isNull(String str)
    {

        if (str == null) {
            return "";
        }
        return str;
    }

    public String getStrSql()
    {

        return strSql;
    }

    public void setStrSql(String strSql)
    {

        this.strSql = strSql;
    }

    // 生成Excel
    public String createExcel(ArrayList titles, String strSql, String username)
    {
    	String title = "";
    	if ("1".equals(this.type)) {
            title = "单位部门报表";
        } else if ("2".equals(this.type)) {
            title = "培训类别报表";
        } else if ("3".equals(this.type)) {
            title = "学员培训报表";
        }
    	
    	String outputFile = title + "_" + username + ".xls";
    	// 创建excel报表并写入数据
    	HSSFWorkbook wb = new HSSFWorkbook();
    	FileOutputStream fileOut = null;
    	HSSFSheet sheet = null;
        try {
        	
        	// 定义两种格式HSSFCellStyle
        	// 第一种style--字体20，水平居中
        	HSSFFont font1 = wb.createFont();
        	font1.setFontHeightInPoints((short) 18);
        	HSSFCellStyle style1 = wb.createCellStyle();
        	style1.setFont(font1);
        	style1.setAlignment(HorizontalAlignment.CENTER);
        	
        	// 第二种style--字体10，水平居中，垂直居中，黑色边框，自动换行
        	HSSFFont font2 = wb.createFont();
        	font2.setFontHeightInPoints((short) 10);
        	HSSFCellStyle style2 = wb.createCellStyle();
        	style2.setFont(font2);
        	style2.setAlignment(HorizontalAlignment.CENTER);
        	style2.setVerticalAlignment(VerticalAlignment.CENTER);
        	style2.setWrapText(true);
        	style2.setBorderBottom(BorderStyle.THIN);
        	style2.setBorderLeft(BorderStyle.THIN);
        	style2.setBorderRight(BorderStyle.THIN);
        	style2.setBorderTop(BorderStyle.THIN);
        	style2.setBottomBorderColor((short) 8);
        	style2.setLeftBorderColor((short) 8);
        	style2.setRightBorderColor((short) 8);
        	style2.setTopBorderColor((short) 8);
        	
        	HSSFCellStyle style3 = wb.createCellStyle();
        	style3.setFont(font2);
        	style3.setAlignment(HorizontalAlignment.LEFT);
        	style3.setVerticalAlignment(VerticalAlignment.CENTER);
        	style3.setWrapText(true);
        	style3.setBorderBottom(BorderStyle.THIN);
        	style3.setBorderLeft(BorderStyle.THIN);
        	style3.setBorderRight(BorderStyle.THIN);
        	style3.setBorderTop(BorderStyle.THIN);
        	style3.setBottomBorderColor((short) 8);
        	style3.setLeftBorderColor((short) 8);
        	style3.setRightBorderColor((short) 8);
        	style3.setTopBorderColor((short) 8);
        	
        	HSSFCellStyle style4 = wb.createCellStyle();
        	style4.setFont(font2);
        	style4.setAlignment(HorizontalAlignment.RIGHT);
        	style4.setVerticalAlignment(VerticalAlignment.CENTER);
        	style4.setWrapText(true);
        	style4.setBorderBottom(BorderStyle.THIN);
        	style4.setBorderLeft(BorderStyle.THIN);
        	style4.setBorderRight(BorderStyle.THIN);
        	style4.setBorderTop(BorderStyle.THIN);
        	style4.setBottomBorderColor((short) 8);
        	style4.setLeftBorderColor((short) 8);
        	style4.setRightBorderColor((short) 8);
        	style4.setTopBorderColor((short) 8);
        	
        	sheet = wb.createSheet();
        	wb.setSheetName(0, title);
        	
        	int len = titles.size();
        	
        	// 写表头
        	HSSFRow row = sheet.createRow(0);
        	ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) len);
        	HSSFCell cell = row.createCell((short) 0);
        	// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        	cell.setCellValue(new HSSFRichTextString(title));
        	cell.setCellStyle(style1);
        	
        	// 写列头
        	HSSFRow row2 = sheet.createRow(1);
        	int i = 0;
        	HSSFCell cell2 = row2.createCell((short) 0);
        	// cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
        	cell2.setCellValue(new HSSFRichTextString("序号"));
        	cell2.setCellStyle(style2);
        	HashMap dataTypeMap = new HashMap();
        	sheet.setColumnWidth((short) 0, (short) 1600);
        	for (i = 0; i < titles.size(); i++)
        	{
        		cell2 = row2.createCell((short) (i + 1));
        		// cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
        		LazyDynaBean abean = (LazyDynaBean) titles.get(i);
        		String datatype = (String) abean.get("datatype");
        		dataTypeMap.put(new Integer(i + 1), datatype);
        		cell2.setCellValue(new HSSFRichTextString((String) abean.get("title")));
        		cell2.setCellStyle(style2);
        	}
        	
        	HSSFRow dataRow = null;
        	HSSFCell dataCell = null;
        	// 数据
        	int xuhao = 0;
        	ContentDAO dao = new ContentDAO(this.cn);
            RowSet rs = dao.search(strSql);

            while (rs.next())
            {
                dataRow = sheet.createRow(xuhao + 2);

                dataCell = dataRow.createCell((short) 0);
                // dataCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                dataCell.setCellValue(++xuhao);
                dataCell.setCellStyle(style2);

                for (int j = 1; j <= len; j++)
                {

                    dataCell = dataRow.createCell((short) (j));
                    // dataCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    String value = rs.getString(j);
                    if (j == 1)
                    {
                        value = value != null ? AdminCode.getCodeName("UN", value) : "";
                        if (xuhao == 1) {
                            sheet.setColumnWidth((short) j, (short) 4000);
                        }
                    }
                    else if (j == 2)
                    {
                        value = value != null ? AdminCode.getCodeName("UM", value) : "";
                        if (xuhao == 1) {
                            sheet.setColumnWidth((short) j, (short) 4000);
                        }
                    }
                    else
                    {
                        if ("2".equals(this.type) && j == 3) {
                            value = value != null ? AdminCode.getCodeName("1_06", value) : "";
                        }
                        if ("3".equals(this.type) && j == 4) {
                            value = value != null ? AdminCode.getCodeName("1_06", value) : "";
                        }
                    }
                    String datatype = (String) dataTypeMap.get(new Integer(j));
                    if ("N".equalsIgnoreCase(datatype))
                    {
                        dataCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        if ("3".equals(this.type) && j == 6) {
                            dataCell.setCellValue(Double.parseDouble(PubFunc.DoFormatDecimal(rs.getDouble(j) + "", 2)));
                        } else {
                            dataCell.setCellValue(rs.getDouble(j));
                        }
                        dataCell.setCellStyle(style4);
                    }
                    else if ("A".equalsIgnoreCase(datatype))
                    {
                        dataCell.setCellValue(new HSSFRichTextString(value));
                        dataCell.setCellStyle(style3);
                    }

                }
            }
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
            wb.write(fileOut);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}

        return outputFile;
    }

}
