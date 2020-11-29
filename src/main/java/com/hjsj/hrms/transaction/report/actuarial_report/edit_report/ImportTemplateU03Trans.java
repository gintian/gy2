package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.fill_cycle.ReportCycleBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ImportTemplateU03Trans extends IBusiness {

	public void execute() throws GeneralException {
		Workbook wb = null;
		try {
			Sheet sheet = null;
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String report_id = (String) hm.get("report_id");
			this.getFormHM().put("report_id", report_id);
			ReportCycleBo reportCycleBo = new ReportCycleBo();
			ArrayList fieldlist = reportCycleBo.getU03FieldList(report_id, false);
			String unitcode = "";

			String id = (String) this.getFormHM().get("id");
			RecordVo vo = new RecordVo("tt_cycle");
			vo.setInt("id", Integer.parseInt(id));
			ContentDAO dao = new ContentDAO(this.frameconn);
			String year = "";
			FormFile form_file = (FormFile) getFormHM().get("file");
			String filename = form_file.getFileName();
			int indexInt = filename.lastIndexOf(".");
			String ext = filename.substring(indexInt + 1, filename.length());
			if (ext == null || ext.length() <= 0 || (!"xls".equals(ext) && !"xlsx".equals(ext)))
				throw GeneralExceptionHandler.Handle(new GeneralException("", "上传文件类型出错！", "", ""));
			InputStream stream = null;
			try {
				stream = form_file.getInputStream();
				wb = WorkbookFactory.create(stream);
				sheet = wb.getSheetAt(0);
				vo = dao.findByPrimaryKey(vo);
				year = vo.getString("theyear");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeIoResource(stream);
			}
			Row row = sheet.getRow(0);
			HashMap map = new HashMap();
			if (row == null)
				throw new GeneralException("请用正确的模板Excel来导入数据！");
			Cell cell2 = row.getCell((short) 0);
			if (cell2 == null)
				throw new GeneralException("请用正确的模板Excel来导入数据！");
			int cols = row.getPhysicalNumberOfCells();
			//int rowsd = sheet.getPhysicalNumberOfRows();
			int rows = sheet.getPhysicalNumberOfRows();
			int unitcodeid = -1;//代表部门或单位所在的列
			String unitcodes = "";//存放单位id
			int persontype = -1;//判断人员分类
			//StringBuffer codeBuf = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			sql.append("update U03 set ");

			StringBuffer insert_sql = new StringBuffer();
			StringBuffer insert_value = new StringBuffer();
			insert_sql.append("insert into U03(");
			int factCols = 0;
			boolean isU0207 = false;
			String fields = "";
			for (short i = 0; i < cols; i++) {
				Cell cell = row.getCell((short) (i));
				if (cell != null) {
					String title = "";
					switch (cell.getCellType()) {
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double y = cell.getNumericCellValue();
							title = Double.toString(y);
							break;
						case Cell.CELL_TYPE_STRING:
							title = cell.getStringCellValue();
							break;
						default:
							title = "";
					}
					//System.out.println(title);
					if ("".equals(title.trim()))
						throw new GeneralException("标题行存在空标题！请用正确的模板Excel来导入数据！");
					if (cell.getCellComment() == null)
						throw new GeneralException("标题行存在空批注！请用正确的模板Excel来导入数据！");
					String field = cell.getCellComment().getString().toString();//获得批注
					fields += field + ",";
					// System.out.println("field： " + field);
					if ("unitcode".equalsIgnoreCase(field)) {
						unitcodeid = i;
						continue;
					}
					if ("U0301".equalsIgnoreCase(field)) {
						persontype = i;
						continue;
					}
					FieldItem fielditem = DataDictionary.getFieldItem(field);
					if (fielditem == null)
						throw new GeneralException("标题行批注有问题！请用正确的模板Excel来导入数据！");
					String codesetid = fielditem.getCodesetid();
					map.put(new Short(i), field + ":" + cell.getStringCellValue());

					sql.append(field + "=?,");
					insert_sql.append(field + ",");
					insert_value.append("?,");


					factCols++;

				}
			}

			if (fields.indexOf("unitcode") == -1)
				throw new GeneralException("标题行不存在unitcode批注！请设置单位或部门来导入数据！");
			if (fields.indexOf("U0301") == -1)
				throw new GeneralException("标题行不存在人员分类的U0301批注！请设置人员分类来导入数据！");


			sql.setLength(sql.length() - 1);
			sql.append(" where u0301=? and unitcode=? and id='" + id + "' ");
			insert_sql.append("U0301,U0303,editflag,id,unitcode) values (");
			insert_sql.append(insert_value.toString());
			insert_sql.append("?,?,?,?,?)");
			//HashMap codeColMap = new HashMap();

			ArrayList list_insert = new ArrayList();
			ArrayList list_update = new ArrayList();
			ArrayList list_delete = new ArrayList();
			//System.out.println(cols);
			for (int j = 1; j <= rows; j++) {
				int num = 0;
				int num2 = 0;
				int mnum = j + 1;
				ArrayList list = new ArrayList();
				row = sheet.getRow(j);
				if (row == null)
					row = sheet.createRow((short) j);
				//   System.out.println(row);
				LazyDynaBean bean = new LazyDynaBean();
				for (short c = 1; c < cols; c++) {
					Cell cell1 = row.getCell(c);

					String fieldItems = (String) map.get(new Short(c));
					if (fieldItems == null)//过滤掉只读的列
						continue;
					String[] fieldItem = fieldItems.split(":");
					String field = fieldItem[0];
					String fieldName = fieldItem[1];
					FieldItem fielditem = DataDictionary.getFieldItem(field);
					String itemtype = fielditem.getItemtype();
					String codesetid = fielditem.getCodesetid();

					if (cell1 != null) {
						int decwidth = fielditem.getDecimalwidth();
						String value = "";
						switch (cell1.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell1.getNumericCellValue();
								value = Double.toString(y);
								value = PubFunc.round(value, decwidth);
								if ("D".equalsIgnoreCase(itemtype) && value.trim().length() == 6) {
									value = value.substring(0, 4) + "-" + value.substring(4) + "-" + "01";
									list.add(value);
								} else
									list.add(new Double((PubFunc.round(value, decwidth))));


								break;
							case Cell.CELL_TYPE_STRING:
								value = cell1.getRichStringCellValue().toString();
								if ("D".equalsIgnoreCase(itemtype)) {
									if (value == null || value.length() <= 0) {
										list.add(null);
									} else if (value.matches("^[+-]?[\\d]+$") && value.trim().length() == 6) {
										value = value.substring(0, 4) + "-" + value.substring(4) + "-" + "01";
										list.add(value);
									}
								} else {
									list.add(value);
								}


								break;
							case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
								if ("N".equals(itemtype)) {
									value = PubFunc.round(value, decwidth);
									list.add(new Double(value));
								} else
									list.add(null);
								break;
							default:
								list.add(null);
						}
						String msg = "";
						if (("N".equals(itemtype) || "D".equals(itemtype)) && value.trim().length() > 0) {
							if (!this.isDataType(decwidth, itemtype, value)) {
								msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
								throw new GeneralException(msg);
							}
						}
						bean.set(field, value);
					}

					if (cell1 == null && c != cols - 1) {
						if (map.get(new Short(c)) != null) {
							list.add(null);
						}
					}
					num2++;
					if (cell1 == null && c != cols - 1) {

						num++;

					}
				}
				if (num == num2)
					break;
				//判断单位是否存在.
				if (unitcodeid != -1) {
					Cell cell = row.getCell((short) unitcodeid);
					if (cell == null)
						throw new GeneralException("第" + mnum + "行导入的excel中的数据单位不能为空!");

					String unitname = cell.getStringCellValue();
					unitname = reportCycleBo.getUnitcode(unitname, this.getFrameconn());
					if (!"".equalsIgnoreCase(unitname)) {
						unitcode = unitname;
						if (unitcodes.indexOf(unitcode) == -1)
							unitcodes += unitcode + ",";
					} else {
						throw new GeneralException("第" + mnum + "行库中不存在单位:" + cell.getStringCellValue() + "!");
					}
				}
				//判断人员分类是否存在.
				String persondesc = "";
				if (persontype != -1) {
					Cell cell = row.getCell((short) persontype);
					if (cell == null)
						throw new GeneralException("第" + mnum + "行导入的excel中的数据人员分类不能为空!");

					persondesc = cell.getStringCellValue();
					if (persondesc != null)
						if (!persondesc.trim().equals(AdminCode.getCodeName("62", "1")) && !persondesc.trim().equals(AdminCode.getCodeName("62", "2"))) {
							throw new GeneralException("第" + mnum + "行人员分类有不符合的数据!");
						} else {
							if (persondesc.trim().equals(AdminCode.getCodeName("62", "1"))) {
								persondesc = "1";
							} else {
								persondesc = "2";
							}

						}
				}
				ActuarialReportBo ab = new ActuarialReportBo(this.getFrameconn(), this.getUserView());
				String flag = ab.isCollectUnit(unitcode);
				if ("1".equals(flag))
					throw new GeneralException("第" + mnum + "行不能导入汇总单位的数据!");

				boolean isInsert = false;
				//U0301,U0303,editflag,id,unitcode
//			    ArrayList listup =list;
//				listup.add(persondesc);
//	        	listup.add(unitcode);
//	        	list_update.add(listup);
				list.add(persondesc);//人员类别
//					list.add(year);//年度
//		            list.add("1");//编辑状态
//		       		list.add(new Integer(id));//周期
				list.add(unitcode);//单位id
				list_update.add(list);
				//list_insert.add(list);
				ArrayList listdel = new ArrayList();
				//listdel.add(persondesc);
				listdel.add(unitcode);
				list_delete.add(listdel);


				//System.out.println(sql.toString()+"list.size"+list.size());
			}
			//dao.batchUpdate(sql.toString(), list_update);
			//dao.batchInsert(insert_sql.toString(), list_insert);
			StringBuffer info = new StringBuffer();
			info.append("导入模板统计信息：<br>");
			//info.append("&nbsp;&nbsp;修改人员："+list_update.size()+" 人；<br>");
			info.append("&nbsp;&nbsp;导入：" + list_update.size() + " 条；");
			this.getFormHM().put("import_insertList", list_insert);
			this.getFormHM().put("import_updateList", list_update);
			this.getFormHM().put("import_insertSql", insert_sql.toString());
			this.getFormHM().put("import_updateSql", sql.toString());
			this.getFormHM().put("importInfo", info.toString());
			this.getFormHM().put("import_deleteinfo", list_delete);
			if (unitcodes.indexOf(",") != -1)
				unitcodes = unitcodes.substring(0, unitcodes.length() - 1);
			this.getFormHM().put("unitcodes", unitcodes);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			PubFunc.closeResource(wb);
		}
	}
	public boolean isDataType(int decwidth, String itemtype, String value)
	{

		boolean flag = true;
		if ("N".equals(itemtype))
		{
		    if (decwidth == 0)
		    {
			flag = value.matches("^[+-]?[\\d]+$");
		    } else
		    {
			flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
		    }

		} else if ("D".equals(itemtype))
		{
			if(value.matches("^[+-]?[\\d]+$")&&value.trim().length()==6)
				flag=true;
			else
				flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
    }
}
