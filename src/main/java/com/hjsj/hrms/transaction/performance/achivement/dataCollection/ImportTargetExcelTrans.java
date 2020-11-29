package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ImportTargetExcelTrans.java</p>
 * <p>Description:数据采集目标跟踪导入Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-11 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ImportTargetExcelTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		FormFile form_file = (FormFile) getFormHM().get("file");
		InputStream _in = null;
		Workbook wb = null;
		try
		{
			String plan_id = (String) this.getFormHM().get("planId");
	    	boolean flag = FileTypeUtil.isFileTypeEqual(form_file);
	    	if(!flag){
	    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
	    	}
			DataCollectBo bo = new DataCollectBo(this.getFrameconn(), plan_id, this.userView);
			RecordVo vo = bo.getPlanVo();
			String object_type = vo.getString("object_type");
	
			ArrayList list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
			HashMap itemMap = new HashMap();
			HashMap codeColMap = new HashMap();
			for (int i = 0; i < list.size(); i++)
			{
				FieldItem item = (FieldItem) list.get(i);
				itemMap.put(item.getItemdesc(), item.getItemid());
			}
	
			String targetCollectItem = "";
			String targetTraceItem = "";// 可以更新数据的指标
			String targetCalcItem = ""; // 目标卡计算指标属性，P04中指标，以逗号分隔，顺序从前到后	
			
			// 取得目标跟踪显示和采集指标
			// 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
			if ("true".equals(targetTraceEnabled))
			{
				targetTraceItem = (String) params.get("TargetTraceItem");
				targetCollectItem = (String) params.get("TargetCollectItem");
				if (params.get("TargetCalcItem") != null && ((String) params.get("TargetCalcItem")).trim().length() > 0)
					targetCalcItem = ((String) params.get("TargetCalcItem")).trim();
			} else
			// 2.从绩效模块参数配置中取目标跟踪显示和采集指标
			{
				ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
				targetTraceItem = configParamBo.getTargetTraceItem();
				targetCollectItem = configParamBo.getTargetCollectItem();
				targetCalcItem = configParamBo.getTargetCalcItem();
			}
	
			short n = 0;// 变动字段开始列
			if ("2".equals(object_type))
				n = 5;
			else
				n = 3;
	
			int updateFidsCount = 0;// 将要更新的字段数目
			// HSSFWorkbook wb = null;
			// HSSFSheet sheet = null;
			Sheet sheet = null;
			StringBuffer buf = new StringBuffer();
			buf.append("update p04 set ");


			// wb = new HSSFWorkbook(form_file.getInputStream());
			// sheet = wb.getSheetAt(0);

			_in = form_file.getInputStream();
			wb = WorkbookFactory.create(_in);
			sheet = wb.getSheetAt(0);

			HashMap map = new HashMap();
			Row row = sheet.getRow(0);
			if (row == null)
				throw new GeneralException("文件格式不正确，请用下载的模板维护数据并导入！");
			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			StringBuffer codeBuf = new StringBuffer();

			ContentDAO dao = new ContentDAO(this.frameconn);
			if (row != null)
			{
				boolean errorflag = false;
				if (cols < 2 || rows < 1)
					errorflag = true;
				else
				{
					// 判断是否用导出德模板来导入数据
					for (int i = 0; i < 2; i++)
					{
						String value = "";
						Cell cell = row.getCell((short) i);
						if (cell != null)
						{
							switch (cell.getCellType())
							{
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell.getNumericCellValue();
								value = Double.toString(y);
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							default:
								value = "";
							}
						} else
						{
							errorflag = true;
							break;
						}

						if (i == 0 && !"主键标识串".equalsIgnoreCase(value))
							errorflag = true;
						else if (i == (n-2) && !"考核对象".equalsIgnoreCase(value))
							errorflag = true;

						if (errorflag)
							break;
					}
				}
				if (errorflag)
					throw new GeneralException("文件格式不正确，请用下载的模板维护数据并导入！");

				for (short m = n; m < cols; m++)
				{
					Cell cell = row.getCell(m);
					if (cell != null)
					{
						String title = "";
						switch (cell.getCellType())
						{
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

						if ("".equals(title.trim()))
							throw new GeneralException("标题行存在空标题！文件格式不正确，请用下载的模板维护数据并导入！");
						String field = (String) itemMap.get(title.trim());
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();

						if (!"0".equals(codesetid))
						{
							if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
							{
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
							} else
							{
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
										+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
							}
						}
						if (targetTraceItem.toLowerCase().indexOf(field.toLowerCase()) != -1)
						{
							map.put(new Short(m), field + ":" + cell.getStringCellValue());
							buf.append(field + "=?,");
							updateFidsCount++;
						}
					} else
						break;
				}
				if (codeBuf.length() > 0)
				{
					codeBuf.setLength(codeBuf.length() - " union all ".length());

					RowSet rs = dao.search(codeBuf.toString());
					while (rs.next())
						codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));

				}
				buf.setLength(buf.length() - 1);
				buf.append(" where p0400=? and plan_id=" + plan_id);
				
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
				String whl = pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
				buf.append(" "+whl);
			}

			// 取数据行
			ArrayList list2 = new ArrayList();
			for (int j = 1; j < rows; j++)
			{
				ArrayList list1 = new ArrayList();
				row = sheet.getRow(j);
				Cell flagCol = row.getCell((short) 0);
				String p0400 = "";
				if (flagCol != null)
				{
					switch (flagCol.getCellType())
					{
					case Cell.CELL_TYPE_BLANK:
						throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
					case Cell.CELL_TYPE_STRING:
						if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
							throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
						else
							p0400 = flagCol.getRichStringCellValue().toString().trim();
					}
				} else
					continue;

				for (short c = n; c < cols; c++)
				{

					String fieldItems = (String) map.get(new Short(c));
					if (fieldItems == null)
						continue;
					String[] fieldItem = fieldItems.split(":");
					String field = fieldItem[0];
					String fieldName = fieldItem[1];
					String itemtype = DataDictionary.getFieldItem(field).getItemtype();
					String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
					int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();
					Cell cell1 = row.getCell(c);
					if (cell1 != null)
					{
						String value = "";
						String msg = "";
						switch (cell1.getCellType())
						{
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double y = cell1.getNumericCellValue();
							value = Double.toString(y);
							value = PubFunc.round(value, decwidth);

							if ("N".equals(itemtype))
							{
								list1.add(new Double(value));
								 if("p0419".equalsIgnoreCase(field))
								 {
									 if(Double.parseDouble(value)>Double.parseDouble("100") || Double.parseDouble(value)<Double.parseDouble("0"))
										 throw new GeneralException("["+DataDictionary.getFieldItem(field).getItemdesc()+"]列请输入0-100之间的数值！"); 
								 }
							}
								
							else if ("D".equals(itemtype))
							{
								value = changeNumToDate(value);
								list1.add(java.sql.Date.valueOf(value));
							}
							break;
						case Cell.CELL_TYPE_STRING:
							value = cell1.getRichStringCellValue().toString();
							if (!"0".equals(codesetid) && !"".equals(codesetid))
								if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
									value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
								else
									value = null;

							if ("D".equals(itemtype) && value != null && value.trim().length() > 0)
							{
								if (!this.isDataType(decwidth, itemtype, value))
								{
									msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
									throw new GeneralException(msg);
								}
								value = PubFunc.replace(value, ".", "-");
								list1.add(java.sql.Date.valueOf(value));
							} else
								list1.add(value);
							break;
						case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
							if ("N".equals(itemtype))
							{
								value = PubFunc.round(value, decwidth);
								list1.add(new Double(value));
							} else
								list1.add(null);
							break;
						default:
							list1.add(null);
						}

						if (("N".equals(itemtype) || "D".equals(itemtype)) && value.trim().length() > 0)
						{
							if (!this.isDataType(decwidth, itemtype, value))
							{
								msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
								throw new GeneralException(msg);
							}
						}
					} else
					{
						if ("N".equals(itemtype))
						{
							list1.add(new Double(0.00));
						} else
							list1.add(null);
					}
				}
				list1.add(new Integer(p0400));
				list2.add(list1);
			}

			if (updateFidsCount == 0)
				return;
			dao.batchUpdate(buf.toString(), list2);
			
			// 更新定义了公式的字段
			if (targetCalcItem.length() > 0)
			{
				String[] temps = targetCalcItem.split(",");
				ArrayList fieldList = (ArrayList) DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET).clone();
				FieldItem item = new FieldItem();
				item.setItemid("per_target_evaluation.score");
				item.setItemdesc("评分");
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				fieldList.add(item);
				for (int i = 0; i < temps.length; i++)
				{
					if (temps[i].length() > 0)
					{
						this.frowset = dao.search("select expression,itemtype from t_hr_busiField  where upper(fieldsetid)='P04'  and upper(itemid)='" + temps[i].toUpperCase() + "' ");
						if (this.frowset.next())
						{
							String expression = Sql_switcher.readMemo(this.frowset, "expression");
							String itemtype = this.frowset.getString("itemtype");
							int y_type = YksjParser.FLOAT;
							if ("A".equalsIgnoreCase(itemtype))
								y_type = YksjParser.STRVALUE;
							if ("D".equalsIgnoreCase(itemtype))
								y_type = YksjParser.DATEVALUE;
							if (expression.trim().length() > 0)
							{
								YksjParser yp = new YksjParser(this.userView, fieldList, YksjParser.forNormal, y_type, YksjParser.forPerson, "Ht", "");
								yp.run(expression, this.frameconn, "", "p04");
								String formular_sql = yp.getSQL();
								if (!"task_score".equalsIgnoreCase(temps[i].trim()))
								{
									if (formular_sql.toLowerCase().indexOf("per_target_evaluation.score") == -1)
									{
										dao.update("update p04 set " + temps[i] + "=(" + formular_sql + ") where plan_id=" + plan_id + "   and ( p04.chg_type<>3 or p04.chg_type is null ) ");
									}
								} else
								{

								}
							}
						}
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(_in);
			PubFunc.closeResource(wb);
		}
	}

	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
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
			flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}

	public static String changeNumToDate(String s)
	{

		String rtn = "1900-01-01";
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date1 = new java.util.Date();
			date1 = format.parse("1900-01-01");
			long i1 = date1.getTime();

			// 这里要减去2，(Long.parseLong(s)-2) 不然日期会提前2天，具体原因不清楚，

			// 估计和java计时是从1970-01-01开始有关
			// 而excel里面的计算是从1900-01-01开始
			i1 = i1 / 1000 + ((Long.parseLong(s) - 2) * 24 * 3600);
			date1.setTime(i1 * 1000);
			rtn = format.format(date1);
		} catch (Exception e)
		{
			rtn = "1900-01-01";
		}
		return rtn;

	}
}
