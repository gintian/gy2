package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class ExportApprovalTrans extends IBusiness {

	/**
	 * szk 20140211
	 * 加班汇总导出excel
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String sql = (String) this.getFormHM().get("sql");
		String addtypenamelist = (String) this.getFormHM().get("addtypenamelist");
		String addtypeidlist = (String) this.getFormHM().get("addtypeidlist");
		addtypenamelist=addtypenamelist.replace("[","");
		addtypenamelist=addtypenamelist.replace("]","");
		addtypenamelist=addtypenamelist.replace(" ","");
		String[] addtypenamelists = addtypenamelist.split(",");
		addtypeidlist=addtypeidlist.replace("[","");
		addtypeidlist=addtypeidlist.replace("]","");
		addtypeidlist=addtypeidlist.replace(" ","");
		String[] addtypeidlists = addtypeidlist.split(",");
 		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		String tableName = "加班汇总审批表";
		//列属性
		ArrayList selectFileList = getfileList(addtypenamelists,addtypeidlists);
	
		HSSFWorkbook work = new HSSFWorkbook();
		HSSFSheet sheet = work.createSheet(tableName);
		HSSFCellStyle style = work.createCellStyle();
		sql = PubFunc.keyWord_reback(sql);
		try {
			rs = dao.search(sql);
			HSSFFont font = work.createFont();
			HSSFRow row = sheet.createRow(1);
			row.setHeight((short)1024);
			font.setFontHeight((short)500);
			//font.setItalic(true);
			ExportExcelUtil.mergeCell(sheet, 1,1,1,selectFileList.size());
			//加标题
			HSSFCell cell = row.createCell(1);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setFont(font);
			//style.setAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(style);
			cell.setCellValue(tableName);
			row = sheet.createRow(2);
			style = this.getTableStyle(work);
			font = work.createFont();
			font.setBold(true);
			style.setFont(font);
			//加表头
			for (int n = 0; n < selectFileList.size(); n++) {
				FieldItem field = (FieldItem) selectFileList.get(n);
				cell = row.createCell(n + 1);
				cell.setCellStyle(style);
				cell.setCellValue(field.getItemdesc());
			}
			Map classMap = new KqUtilsClass(this.frameconn).getClassDescMap();
			style =getTableStyle(work);
			//数据
			while (rs.next()) {
				row = sheet.createRow(rs.getRow() + 2);
				for (int i = 0; i < selectFileList.size(); i++) {
					cell = row.createCell(i + 1);
					cell.setCellType(HSSFCell.ENCODING_UTF_16);
					cell.setCellStyle(style);
					FieldItem field = (FieldItem) selectFileList.get(i);
					String type = field.getItemtype();
					Object obj = (Object) rs.getObject(field.getItemid());
					if (obj == null) {
					} else if ("A".equals(type)) {
						if ("0".equals(field.getCodesetid())) {
                                cell.setCellValue((String) obj);
						} else {
							if ("e0122".equalsIgnoreCase(field.getItemid()))
							{
								Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.frameconn);
								String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
								if (uplevel == null || uplevel.length() <= 0) {
									uplevel = "0";
								}
								CodeItem codeItem = AdminCode.getCode(field.getCodesetid(), PubFunc.nullToStr((String) obj),Integer.parseInt(uplevel));										
								String value = codeItem == null ? "" : codeItem.getCodename();
								cell.setCellValue(value);
							}else 
							{
								cell.setCellValue(AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr((String) obj)));
							}
						}
					}  else {
						cell.setCellValue(obj.toString());
					}
				}
			}
			for(int i = 1; i < (selectFileList.size()+1); i++){
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
			}
			//返回文件名
			String xlsName = "";
			if(this.userView.getStatus() == 0){
				xlsName = "AppAllExcel_" + PubFunc.getStrg() + this.userView.getUserName() + ".xls";
			}else{
				xlsName = "AppAllExcel_" + PubFunc.getStrg() + this.userView.getA0100() + this.userView.getUserFullName() + ".xls";
			}
			String pathFile = System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + xlsName;
			File file = new File(pathFile);
			FileOutputStream out = null;
			try{				
				out = new FileOutputStream(file);
				work.write(out);
				xlsName=PubFunc.encrypt(xlsName);
				this.getFormHM().put("name", xlsName);
			}finally{
				PubFunc.closeIoResource(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
	        KqUtilsClass.closeDBResource(rs);
	    }
	}

	private HSSFCellStyle getTableStyle(HSSFWorkbook work){
		HSSFCellStyle style = work.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(false);
		return style;
	}

	/**
	 * 编辑一个数据FieldItem集
	 * @return
	 */
	private ArrayList getfileList(String[] addtypenamelists,String[] addtypeidlists)
	{
		   
		   ArrayList list =new ArrayList();
		   FieldItem fielditem=new FieldItem();
		   fielditem=new FieldItem();
		   fielditem.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
		   fielditem.setItemid("nbase");
		   fielditem.setItemtype("A");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("@@");
		   list.add(fielditem);	 
		   fielditem=new FieldItem();
		   fielditem.setItemdesc(ResourceFactory.getProperty("b0110.label"));
		   fielditem.setItemid("b0110");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("UN");
		   fielditem.setVisible(true);
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc(ResourceFactory.getProperty("e0122.label"));
		   fielditem.setItemid("e0122");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("UM");
		   fielditem.setVisible(true);
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("岗位名称");
		   fielditem.setItemid("e01a1");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("@K");
		   fielditem.setVisible(true);
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc(ResourceFactory.getProperty("label.title.name"));
		   fielditem.setItemid("a0101");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("0");
		   fielditem.setVisible(true);
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("工号");
		   fielditem.setItemid("gno");
		   fielditem.setItemtype("A");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("0");
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("考勤卡号");
		   fielditem.setItemid("cardno");
		   fielditem.setItemtype("A");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("0");
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("加班总时长");
		   fielditem.setItemid("q1");
		   fielditem.setItemtype("N");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("0");
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("已批时长");
		   fielditem.setItemid("q2");
		   fielditem.setItemtype("N");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("0");
		   list.add(fielditem);
		   fielditem=new FieldItem();
		   fielditem.setItemdesc("未批时长");
		   fielditem.setItemid("q3");
		   fielditem.setItemtype("N");
		   fielditem.setVisible(true);
		   fielditem.setCodesetid("0");
		   list.add(fielditem);
		   for (int i = 0; i < addtypeidlists.length; i++)
		{
			   fielditem=new FieldItem();
			   fielditem.setItemdesc(""+addtypenamelists[i]);
			   fielditem.setItemid("q"+addtypeidlists[i]);
			   fielditem.setItemtype("N");
			   fielditem.setVisible(true);
			   fielditem.setCodesetid("0");
			   list.add(fielditem);
		}
		   return list;
		
	}

	
}
