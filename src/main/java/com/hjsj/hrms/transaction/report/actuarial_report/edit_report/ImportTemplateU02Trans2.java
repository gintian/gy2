package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImportTemplateU02Trans2 extends IBusiness {

	public void execute() throws GeneralException {
		Workbook wb = null;
		try {
			Sheet sheet = null;
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String report_id = (String) hm.get("report_id");
			report_id = report_id.trim().toUpperCase();
			this.getFormHM().put("report_id", report_id);
			String escope = "";
			escope = report_id.split("_")[1];
			String rootUnit = (String) this.getFormHM().get("rootUnit");
			EditReport editReport = new EditReport();
			ArrayList fieldlist = editReport.getU02FieldList(this.getFrameconn(), report_id, false);

			LazyDynaBean updownRuleBeans = editReport.getUpdownRuleBeans(this.getFrameconn(), fieldlist, report_id);

			String unitcode = (String) this.getFormHM().get("unitcode");
			String nameee = this.userView.getUserName();
			String selfunitcode = this.getUnitcode(this.userView.getUserName());
			String id = (String) this.getFormHM().get("id");
			int flag2 = editReport.getSelfUnitFlag(this.getFrameconn(), id, selfunitcode, report_id.toUpperCase());
			String kmethod = (String) this.getFormHM().get("kmethod");
			String U0207 = "";
			if (kmethod != null && "0".equals(kmethod)) {
				if (editReport.isBeforeCycle(this.getFrameconn(), id)) {
					U0207 = "3";
				} else {
					U0207 = "1";
				}
			} else {
				U0207 = "3";
			}

			FormFile form_file = (FormFile) getFormHM().get("file");
			String filename = form_file.getFileName();
			int indexInt = filename.lastIndexOf(".");
			String ext = filename.substring(indexInt + 1, filename.length());
			if (ext == null || ext.length() <= 0 || (!"xls".equals(ext) && !"xlsx".equals(ext)))
				throw GeneralExceptionHandler.Handle(new GeneralException("", "上传文件类型出错！", "", ""));
			InputStream filein = null;
			try {
				filein = form_file.getInputStream();
				wb = WorkbookFactory.create(filein);

				sheet = wb.getSheetAt(0);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeIoResource(filein);
			}

			Row row = sheet.getRow(0);
			HashMap map = new HashMap();
			if (row == null)
				throw new GeneralException("请用正确的模板Excel来导入数据！");
			Cell cell2 = row.getCell((short) 0);
			if (cell2 == null)
				throw new GeneralException("请用正确的模板Excel来导入数据！");
			int cols = row.getPhysicalNumberOfCells();
			int rowsd = sheet.getPhysicalNumberOfRows();
			int rows = sheet.getPhysicalNumberOfRows();
			int unitcodeid = -1;//代表部门或单位所在的列
			String unidcodestr = "";
			String unitcodes = "";//存放单位id
			int u0200id = -1;//代表精算编号id
			int decwidthu0200 = 0;
			int u0239id = -1;//代表备注信息
			int u0201id = -1;//身份证所在的列
			int u0207id = -1;
			StringBuffer codeBuf = new StringBuffer();
			//ArrayList list=editReport.getU02FieldList(this.getFrameconn(),report_id);
			StringBuffer sql = new StringBuffer();
			sql.append("update U02 set ");
			StringBuffer insert_sql = new StringBuffer();
			StringBuffer insert_value = new StringBuffer();
			insert_sql.append("insert into U02(");
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
					String field = cell.getCellComment().getString().toString();
					fields += field + ",";
					if ("unitcode".equalsIgnoreCase(field)) {
						unitcodeid = i;
						unidcodestr += i + ",";
						continue;
					}
					if ("u0207".equalsIgnoreCase(field)) {
						u0207id = i;
						continue;
					}
					if (!"1".equals(rootUnit) && flag2 == 2 && "u0243".equalsIgnoreCase(field)) {
						continue;
					}
					if ("u0201".equalsIgnoreCase(field)) {
						u0201id = i;
					}
					if ("u0239".equalsIgnoreCase(field)) {
						u0239id = i;
					}
					if ("u0200".equalsIgnoreCase(field)) {
						FieldItem fielditem = DataDictionary.getFieldItem(field);
						decwidthu0200 = fielditem.getDecimalwidth();
						u0200id = i;
					}
					FieldItem fielditem = DataDictionary.getFieldItem(field);
					if (fielditem == null)
						throw new GeneralException("指标不存在！请检查模板数据指标！！");
					String codesetid = fielditem.getCodesetid();
					if (!"0".equals(codesetid)) {
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid)) {
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
						} else {
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
									+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
						}
					}
					map.put(new Short(i), field + ":" + cell.getStringCellValue());
					if (!"u0200".equalsIgnoreCase(fielditem.getItemid())) {
						sql.append(field + "=?,");
						if ("62".equals(codesetid)) {
							isU0207 = true;

						} else {
							insert_sql.append(field + ",");
							insert_value.append("?,");
						}
					}
					factCols++;

				}
			}
			if (unidcodestr.length() > 0)
				unidcodestr = unidcodestr.substring(0, unidcodestr.length() - 1);
			if (fields.indexOf("unitcode") == -1)
				throw new GeneralException("标题行不存在unitcode批注！请设置单位或部门来导入数据！");
			if (fields.indexOf("u0200") == -1)
				throw new GeneralException("标题行不存在精确编号批注！请设置精确编号来导入数据！");
			sql.setLength(sql.length() - 1);
			sql.append(",editflag=? where u0200=? and unitcode=? and id='" + id + "'  and escope='" + escope + "'  ");
			insert_sql.append("editflag,escope,u0200,id,unitcode,u0207) values (");
			insert_sql.append(insert_value.toString());
			insert_sql.append("?,?,?,?,?,?)");
			HashMap codeColMap = new HashMap();
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				if (codeBuf.length() > 0) {
					codeBuf.setLength(codeBuf.length() - " union all ".length());
					RowSet rs = dao.search(codeBuf.toString());
					while (rs.next())
						codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ArrayList list_insert = new ArrayList();
			ArrayList list_update = new ArrayList();
			//System.out.println(cols);
			for (int j = 1; j <= rows; j++) {
				int num = 0;
				int num2 = 0;
				int mnum = j + 1;
				ArrayList list = new ArrayList();
				row = sheet.getRow(j);
				//   System.out.println(row);
				LazyDynaBean bean = new LazyDynaBean();
				for (short c = 1; c < cols; c++) {
					Cell cell1 = null;
					Cell _Insertcell = null;
					if (row != null) {
						cell1 = row.getCell(c);
						_Insertcell = row.getCell((short) 0);
					}
					if (_Insertcell != null)
						switch (_Insertcell.getCellType()) {
							case Cell.CELL_TYPE_BLANK:
								_Insertcell = null;
								break;

						}
					boolean isInsertcell = false;
					if (_Insertcell == null) {
						isInsertcell = true;
					}
					String fieldItems = (String) map.get(new Short(c));
					if (fieldItems == null)//过滤掉只读的列
						continue;
					if (u0207id != -1 && u0207id == c)//过滤掉人员分类
						continue;
					String[] fieldItem = fieldItems.split(":");
					String field = fieldItem[0];
					String fieldName = fieldItem[1];
					FieldItem fielditem = DataDictionary.getFieldItem(field);
					String itemtype = fielditem.getItemtype();
					String codesetid = fielditem.getCodesetid();
					if (isU0207 && isInsertcell && "62".equals(codesetid)) {
						continue;
					}
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
								} else {
									if (u0239id != -1 && u0239id == c) {
										list.add(new String(value));
									} else {
										if (u0201id != -1 && u0201id == c) {
											if (value.length() > 18) {
												value = value.trim().substring(0, 17);
											}
										}
										list.add(new Double((PubFunc.round(value, decwidth))));

									}
								}
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell1.getRichStringCellValue().toString();
								if (!"0".equals(codesetid) && !"".equals(codesetid)) {
									if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
										value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
									else
										value = null;
								}

								if ("D".equalsIgnoreCase(itemtype)) {
									if (value == null || value.length() <= 0) {
										list.add(null);
									} else if (value.matches("^[+-]?[\\d]+$") && value.trim().length() == 6) {
										value = value.substring(0, 4) + "-" + value.substring(4) + "-" + "01";
										list.add(value);
									}
								} else {
									if (u0201id != -1 && u0201id == c) {
										if (value.length() > 18) {
											value = value.trim().substring(0, 17);
										}
									}
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
//			    String msg = "";
//			    if ((itemtype.equals("N") || itemtype.equals("D"))&&value.trim().length()>0)
//			    {
//				    if (!this.isDataType(decwidth, itemtype, value))
//				    {
//				       msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
//				       throw new GeneralException(msg);
//				    }
//			    }
						bean.set(field, value);
					}
			 
			 /*HSSFCell _cell = row.getCell((short)0);
			 if(_cell!=null)
			 switch (_cell.getCellType())
			 {
				      case HSSFCell.CELL_TYPE_BLANK:
				         //throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
				    	  _cell=null;
				      break;
				      
			 }*/
					if (cell1 == null && c != cols - 1) {
						if (map.get(new Short(c)) != null) {
							list.add(null);
						}
					}
					num2++;
					if (cell1 == null) {

						num++;

					}
				}
				if (num == num2)
					break;
				//判断单位是否存在.

				if (unitcodeid != -1) {


					Cell cell = null;

					if (unidcodestr.indexOf(",") != -1) {
						String units[] = unidcodestr.split(",");
						for (int i = 0; i < units.length; i++) {
							cell = row.getCell(Integer.parseInt(units[i]));
							if (cell == null)
								continue;

							String unitname = cell.getStringCellValue();
							unitname = editReport.getUnitcode(unitname, this.getFrameconn(), selfunitcode);
							if (!"".equalsIgnoreCase(unitname)) {
								unitcodeid = Integer.parseInt(units[i]);
//		    				break;
							}
						}
					}
					cell = row.getCell((short) unitcodeid);
					if (cell == null)
						throw new GeneralException("第" + mnum + "行导入的excel中的数据单位不能为空!");
					String unitname = cell.getStringCellValue();
					unitname = editReport.getUnitcode(unitname, this.getFrameconn(), selfunitcode);
					if (!"".equalsIgnoreCase(unitname)) {
						unitcode = unitname;
						if (unitcodes.indexOf(unitcode) == -1)
							unitcodes += unitcode + ",";
					} else {

						throw new GeneralException("第" + mnum + "行导入的excel中的数据单位不存在!");
					}
					ActuarialReportBo ab = new ActuarialReportBo(this.getFrameconn(), this.getUserView());
					String flag = ab.isCollectUnit(unitcode);
					if ("1".equals(flag))
						throw new GeneralException("第" + mnum + "行不能导入汇总单位的数据!");
				}
				//判断精算编号数据库中是否存在
				boolean isInsert = false;
				String value = "";
				if (u0200id != -1) {
					Cell cell = row.getCell((short) u0200id);

					// String field = cell.getCellComment().getString().toString();
					// FieldItem fielditem= DataDictionary.getFieldItem(field);
					// int decwidth = fielditem.getDecimalwidth();
					if (cell != null) {

						switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell.getNumericCellValue();
								value = Double.toString(y);
								value = PubFunc.round(value, decwidthu0200);
								break;
							case Cell.CELL_TYPE_BLANK:
								throw new GeneralException("第" + mnum + "行精算编号存在空数据！");
							case Cell.CELL_TYPE_STRING:
								value = cell.getRichStringCellValue().toString();
								break;
						}
						if (!editReport.isExistData(this.getFrameconn(), value, unitcode, id, escope))
							throw new GeneralException("第" + mnum + "行库中不存在数据,请检查数据！");

					}
					isInsert = true;
				}

				Cell cell = row.getCell((short) u0200id);
				if (cell != null) {

//			    String error=editReport.estimateRule(updownRuleBeans,bean,U0207);
//				if(error!=null&&error.length()>0)
//				{
//					   throw new GeneralException(error);
//				}
//				if(!isInsert)
//				{
//					list.add("1");
//		            list.add(escope);
//		            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//		       		String insertid=idg.getId(("U02.U0200").toUpperCase());	
//		       		list.add(insertid);       		   
//		       		list.add(new Integer(id));
//		       		list.add(unitcode);
//		       		list.add(U0207);
//		       		//System.out.println("INSERT======="+list);
//		        	list_insert.add(list);      
//				}  else
//				{
					list.add("2");
					list.add(value);
					list.add(unitcode);
					//list.trimToSize();
					list_update.add(list);
					//	System.out.println("wsql:"+sql.toString()+"list.size:"+list.size());
					//}
					//判断上下限

				}
			}
			//dao.batchUpdate(sql.toString(), list_update);
			//dao.batchInsert(insert_sql.toString(), list_insert);

			StringBuffer info = new StringBuffer();
			info.append("驳回导入模板统计信息：<br>");
			info.append("&nbsp;&nbsp;驳回人员：" + list_update.size() + " 人；<br>");
			this.getFormHM().put("import_insertList", list_insert);
			this.getFormHM().put("import_updateList", list_update);
			this.getFormHM().put("import_insertSql", insert_sql.toString());
			this.getFormHM().put("import_updateSql", sql.toString());
			this.getFormHM().put("importInfo", info.toString());
			if (!"".equals(unitcodes))
				unitcodes = unitcodes.substring(0, unitcodes.length() - 1);
			this.getFormHM().put("unitcodes", unitcodes);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
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
	public String getUnitcode(String userName)
	{
		String unitcode="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet recset=null;
		try
		{
			recset=dao.search("select unitcode from operuser where userName='"+userName+"'");
			if(recset.next())
				unitcode=recset.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return unitcode;
	}
}
