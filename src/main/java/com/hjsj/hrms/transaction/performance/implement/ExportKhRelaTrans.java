package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:ExportKhRelaTrans.java</p>
 * <p>Description:自助考核实施导出excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-08-24 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ExportKhRelaTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String plan_id = (String) this.getFormHM().get("planID");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv_self(this.userView, plan_id)){	
        	return;
        }
		RecordVo vo = new RecordVo("per_plan");
		vo.setInt("plan_id", Integer.parseInt(plan_id));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    vo = dao.findByPrimaryKey(vo);
		} catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		String object_type = vo.getString("object_type");
		String plan_name = vo.getString("name");
	
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();

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
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.LEFT);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderBottom(BorderStyle.THIN);
			style1.setBorderLeft(BorderStyle.THIN);
			style1.setBorderRight(BorderStyle.THIN);
			style1.setBorderTop(BorderStyle.THIN);
			style1.setBottomBorderColor((short) 8);
			style1.setLeftBorderColor((short) 8);
			style1.setRightBorderColor((short) 8);
			style1.setTopBorderColor((short) 8);

			HSSFCellStyle style0 = wb.createCellStyle();
			style0.setAlignment(HorizontalAlignment.CENTER);
			style0.setVerticalAlignment(VerticalAlignment.CENTER);
			style0.setWrapText(true);
			HSSFFont fontStyle = wb.createFont();
			fontStyle.setFontName("宋体");
			fontStyle.setFontHeightInPoints((short) 15);
			fontStyle.setBold(true);
			style0.setFont(fontStyle);

			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) (15.625 * 40));
			HSSFCell cell;
			if ("2".equals(object_type))// 人员
			{
				ExportExcelUtil.mergeCell(sheet, 0, 0, 0, 8);

				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(plan_name));
				cell.setCellStyle(style0);

				row = sheet.createRow(1);
				ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short) 3);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("jx.datacol.khobj")));
//		    cell.setCellValue(cellStr("考核对象"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 1);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 2);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellStyle(style2);

				ExportExcelUtil.mergeCell(sheet, 1, (short) 4, 1, (short) 8);
				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBody")));
//		    cell.setCellValue(cellStr("考核主体"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 5);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 6);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 7);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 8);
				cell.setCellStyle(style2);

				row = sheet.createRow(2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
//		    cell.setCellValue(cellStr("职位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
//		    cell.setCellValue(cellStr("姓名"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 6);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
//		    cell.setCellValue(cellStr("职位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 7);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
//		    cell.setCellValue(cellStr("姓名"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 8);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBodySort")));
//		    cell.setCellValue(cellStr("主体类别"));
				cell.setCellStyle(style2);

			} else if ("1".equals(object_type))// 团队
			{
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 5);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(plan_name));
				cell.setCellStyle(style0);

				row = sheet.createRow(1);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("jx.datacol.khobj")));
//		    cell.setCellValue(cellStr("考核对象"));
				cell.setCellStyle(style2);

				ExportExcelUtil.mergeCell(sheet, 1, (short) 1, 1, (short) 5);
				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBody")));
//		    cell.setCellValue(cellStr("考核主体"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 2);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellStyle(style2);

				row = sheet.createRow(2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("org.performance.unorum")));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
//		    cell.setCellValue(cellStr("职位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
//		    cell.setCellValue(cellStr("姓名"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBodySort")));
//		    cell.setCellValue(cellStr("主体类别"));
				cell.setCellStyle(style2);

			} else if ("3".equals(object_type))// 单位
			{
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 5);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(plan_name));
				cell.setCellStyle(style0);

				row = sheet.createRow(1);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("jx.datacol.khobj")));
//		    cell.setCellValue(cellStr("考核对象"));
				cell.setCellStyle(style2);

				ExportExcelUtil.mergeCell(sheet, 1, (short) 1, 1, (short) 5);
				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBody")));
//		    cell.setCellValue(cellStr("考核主体"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 2);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellStyle(style2);

				row = sheet.createRow(2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
//		    cell.setCellValue(cellStr("职位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
//		    cell.setCellValue(cellStr("姓名"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBodySort")));
//		    cell.setCellValue(cellStr("主体类别"));
				cell.setCellStyle(style2);

			} else if ("4".equals(object_type))// 部门
			{
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 6);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(plan_name));
				cell.setCellStyle(style0);

				row = sheet.createRow(1);
				ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short) 1);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("jx.datacol.khobj")));
//		    cell.setCellValue(cellStr("考核对象"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 1);
				cell.setCellStyle(style2);

				ExportExcelUtil.mergeCell(sheet, 1, (short) 2, 1, (short) 6);
				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBody")));
//		    cell.setCellValue(cellStr("考核主体"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 3);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellStyle(style2);
				cell = row.createCell((short) 6);
				cell.setCellStyle(style2);

				row = sheet.createRow(2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);

				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("b0110.label")));
//		    cell.setCellValue(cellStr("单位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 3);
				cell.setCellValue(cellStr(fielditem.getItemdesc()));
//		    cell.setCellValue(cellStr("部门"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("e01a1.label")));
//		    cell.setCellValue(cellStr("职位"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 5);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("hire.employActualize.name")));
//		    cell.setCellValue(cellStr("姓名"));
				cell.setCellStyle(style2);
				cell = row.createCell((short) 6);
				cell.setCellValue(cellStr(ResourceFactory.getProperty("lable.performance.perMainBodySort")));
//		    cell.setCellValue(cellStr("主体类别"));
				cell.setCellStyle(style2);
			}

			String sql = "select per_object.b0110 object_b0110,per_object.e0122 object_e0122,per_object.e01a1 object_e01a1,per_object.object_id,";
			sql += "per_mainbody.b0110 mainbody_b0110,per_mainbody.e0122 mainbody_e0122,per_mainbody.e01a1 mainbody_e01a1,per_mainbody.body_id mainbodyid,";
			sql += "per_mainbody.a0101 mainbodyName,per_object.a0101 objectName from per_object left join per_mainbody on per_object.object_id=per_mainbody.object_id ";
			sql += "and per_object.plan_id=per_mainbody.plan_id where per_object.plan_id=" + plan_id;

			//	// 加管理范围
			//	String code = this.userView.getManagePrivCode();
			//	String value = this.userView.getManagePrivCodeValue();
			//	if (code == null)
			//	{
			//	    sql += " and 1=2 ";
			//	} else if (code.equalsIgnoreCase("UN"))
			//	{
			//	    sql += " and (per_object.b0110 like '";
			//	    sql += ((value == null ? "" : value) + "%'");
			//	    if (value == null)
			//	    {
			//		sql += " or per_object.b0110 is null ";
			//	    }
			//	    sql += ")";
			//	} else if (code.equalsIgnoreCase("UM"))
			//	{
			//	    sql += " and (per_object.e0122 like '";
			//	    sql += (value == null ? "" : value) + "%'";
			//	    if (value == null)
			//	    {
			//		sql += (" or per_object.e0122 is null ");
			//	    }
			//	    sql += ")";
			//	}
			//
			//	if (object_type.equals("2") && !userView.isSuper_admin() && userView.getPrivExpression()!=null)
			//	{
			//	    String conditionSql = " select UsrA01.A0100 " + userView.getPrivSQLExpression("Usr", true);
			//	    sql +=" and per_object.object_id in (" + conditionSql + " )";
			//	}

			//考虑登录用户的权限
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn(), this.userView, plan_id);
			String privWhl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围
			sql += " and per_object.object_id in ( select object_id from per_object where plan_id=" + plan_id + " " + privWhl + " )";
			sql += " order by per_object.b0110,per_object.e0122,per_object.e01a1,per_object.a0000,per_mainbody.body_id desc";

			try {
				RowSet rowSet = dao.search(sql);
				int rowCount = 3;
				HashMap map = this.getMainBodyTypes();
				while (rowSet.next()) {
					row = sheet.createRow(rowCount++);
					String object_b0110 = rowSet.getString("object_b0110") == null ? "" : rowSet.getString("object_b0110");
					String object_e0122 = rowSet.getString("object_e0122") == null ? "" : rowSet.getString("object_e0122");
					String object_e01a1 = rowSet.getString("object_e01a1") == null ? "" : rowSet.getString("object_e01a1");
					String mainbody_b0110 = rowSet.getString("mainbody_b0110") == null ? "" : rowSet.getString("mainbody_b0110");
					String mainbody_e0122 = rowSet.getString("mainbody_e0122") == null ? "" : rowSet.getString("mainbody_e0122");
					String mainbody_e01a1 = rowSet.getString("mainbody_e01a1") == null ? "" : rowSet.getString("mainbody_e01a1");
					String mainbodyid = rowSet.getString("mainbodyid") == null ? "" : rowSet.getString("mainbodyid");
					String objectName = rowSet.getString("objectName") == null ? "" : rowSet.getString("objectName");
					String mainbodyName = rowSet.getString("mainbodyName") == null ? "" : rowSet.getString("mainbodyName");

					int i = 0;
					if ("2".equals(object_type)) {
						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("UN", object_b0110)));
						cell.setCellStyle(style1);

						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("UM", object_e0122)));
						cell.setCellStyle(style1);

						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("@K", object_e01a1)));
						cell.setCellStyle(style1);

						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(objectName));
						cell.setCellStyle(style1);
						// content.append("[" + AdminCode.getCodeName("UN",
						// b0110) + "/" + AdminCode.getCodeName("UM", e0122) +
						// "/" + AdminCode.getCodeName("@K", e01a1) + "招聘需求的订单："
						// + orders[i] + "]");
					} else if ("1".equals(object_type)) {
						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(objectName));
						cell.setCellStyle(style1);

					} else if ("3".equals(object_type)) {
						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("UN", object_b0110)));
						cell.setCellStyle(style1);

					} else if ("4".equals(object_type)) {
						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("UN", object_b0110)));
						cell.setCellStyle(style1);

						cell = row.createCell((short) i++);
						cell.setCellValue(cellStr(AdminCode.getCodeName("UM", object_e0122)));
						cell.setCellStyle(style1);
					}

					cell = row.createCell((short) i++);
					cell.setCellValue(cellStr(AdminCode.getCodeName("UN", mainbody_b0110)));
					cell.setCellStyle(style1);

					cell = row.createCell((short) i++);
					cell.setCellValue(cellStr(AdminCode.getCodeName("UM", mainbody_e0122)));
					cell.setCellStyle(style1);

					cell = row.createCell((short) i++);
					cell.setCellValue(cellStr(AdminCode.getCodeName("@K", mainbody_e01a1)));
					cell.setCellStyle(style1);

					cell = row.createCell((short) i++);
					cell.setCellValue(cellStr(mainbodyName));
					cell.setCellStyle(style1);

					cell = row.createCell((short) i++);
					cell.setCellValue(cellStr(map.get(mainbodyid) == null ? "" : (String) map.get(mainbodyid)));
					cell.setCellStyle(style1);
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}

			String outName = PubFunc.getStrg() + ".xls";
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally {
				PubFunc.closeResource(fileOut);
			}
//		outName = outName.replace(".xls", "#");
			outName = PubFunc.encrypt(outName);
			//20/3/5 xus vfs改造
			getFormHM().put("outName", outName);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(wb);
		}
    }

    /** 获得考核主体类别 */
    public HashMap getMainBodyTypes() throws GeneralException
    {
	
		HashMap map = new HashMap();
		try
		{
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=0 or body_type is null");
		    RowSet rowSet = dao.search(sql.toString());
		    while (rowSet.next())
		    	map.put(rowSet.getString("body_id"), rowSet.getString("name"));
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return map;
    }

    public HSSFRichTextString cellStr(String context)
    {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
    }
}
