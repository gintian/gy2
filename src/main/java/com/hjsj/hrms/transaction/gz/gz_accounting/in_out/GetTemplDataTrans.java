package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:薪资导入模板数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-11-11 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class GetTemplDataTrans extends IBusiness
{
	public void execute() throws GeneralException
	{

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String gz_module=(String)this.getFormHM().get("gz_module");
		String salaryid = (String) hm.get("salaryid");
		String oper = (String) hm.get("oper");
		// hm.remove("oper");
		// 业务日期和发放次数 这里只是针对审批导入
		String _bosdate = (String) this.getFormHM().get("bosdate");
		String _count = (String) this.getFormHM().get("count");

		FormFile form_file = (FormFile) getFormHM().get("file");
		
		 
		//如果用户没有当前薪资类别的资源权限   20140903  dengcan
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,gz_module);
		
		
		/* 薪资 安全问题：文件上传漏洞 xiaoyun 2014-9-3 start */
		try {
			// 检查文件实际类型和通过文件名获取的类型是否一致
			boolean isOk = FileTypeUtil.isFileTypeEqual(form_file);
			if(!isOk) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		/* 薪资 安全问题：文件上传漏洞 xiaoyun 2014-9-3 end */		

		/** 薪资类别 */
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
		String tablename = "";
		String manager = null;
		boolean isShare = true;
		boolean isGZmanager = false;
		boolean isApprove = false;// 是否走审批
		if ("sp".equalsIgnoreCase(oper))
			tablename = "salaryhistory";
		else if ("fafang".equalsIgnoreCase(oper))
		{
			tablename = gzbo.getGz_tablename();
			manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if (manager == null || (manager != null && manager.length() == 0))// 不共享
				isShare = false;
			if (this.userView.getUserName().equalsIgnoreCase(manager))
				isGZmanager = true;
			isApprove = gzbo.isApprove();
		}
		
		HashMap fieldMap =new HashMap();
		ArrayList fieldlist=gzbo.getFieldlist();
		for(int i=0;i<fieldlist.size();i++)
		{
			Field field=(Field)fieldlist.get(i);
			String name=field.getName().toUpperCase();
			fieldMap.put(name,"1");
		}

		StringBuffer sql = new StringBuffer();
		sql.append("update " + tablename + " set ");
		int updateFidsCount = 0;// 将要更新的字段数目
		
		StringBuffer update_item_str=new StringBuffer("");
		Workbook wb = null;
		Sheet sheet = null;
		InputStream _in = null;
		try
		{
			_in = form_file.getInputStream();
			wb = WorkbookFactory.create(_in);
			sheet = wb.getSheetAt(0);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(wb);
			PubFunc.closeIoResource(_in);
		}
		try
		{
			String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
                
			HashMap relationFieldMap=new HashMap(); //关联指标对应的列号
			ArrayList relationFieldList=new ArrayList();
			FieldItem aitem=null;
			if("1".equals(royalty_valid))
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						aitem = DataDictionary.getFieldItem(temps[i].trim());
						relationFieldList.add(aitem);
					}
				}
			}
			
			ArrayList headList=new ArrayList();
			String onlyname = "";// 唯一指标
			int onlyColIndex = 0; // 唯一指标所在的列
			HashMap onlyValueMap = new HashMap();// 唯一性指标值 （指标值,行号）
			HashMap flagColValMap = new HashMap();// 数据库中正确的唯一性指标(主键标识串)值参照
			HashMap maxZ1ByPersonMap=new HashMap(); //每个人员的最大次数
			HashMap a0101ValueMap=new HashMap();  //唯一性指标无值的姓名
			HashMap a0101ValueMap2=new HashMap();  //唯一性指标有值的姓名
			ArrayList onlyValueRepeat = new ArrayList();// 记录唯一性指标重复行
			HashMap maxA0Z1ByPersonMap=new HashMap(); //每个人员的最大归属次数,网易需求，lis
			/**
			 * ↓记录唯一性指标，遍历的时候如果里面有的话就说明此行重复；value存放前面出现几个重复的，方便判断对应哪条记录 zhaoxg add 2015-9-29
			 */
			HashMap onlyRepeat = new HashMap();
			HashMap allOnlyFieldInExcel = new HashMap();//存放excel中所有的人
			ArrayList onlyRepeatList = new ArrayList();//存放新插入记录的更新值，用于计算后更新  zhaoxg add 2015-9-29
			StringBuffer repeatsql = new StringBuffer("");//计算后更新的sql zhaoxg add 2015-9-29
			
			int totalRowCount = 0;// 有效行数
			int maxColCount = 0;// 有效列数
			HashMap a0101map=new HashMap();
			HashMap map = new HashMap();
			Row row = sheet.getRow(0);
			if (row == null)
				throw new GeneralException("请用导出的模板Excel来导入数据！");
			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			StringBuffer a0100s = new StringBuffer();
			HashMap a0100sMap = new HashMap();
			StringBuffer codeBuf = new StringBuffer();
			int x = 0;
			ContentDAO dao = new ContentDAO(this.frameconn);
			HashMap codeColMap = new HashMap();
			if (row != null)
			{
				boolean errorflag = false;
				if (cols < 1 || rows < 1)
					errorflag = true;
				else
				{
					for (int i = 0; i < 1; i++)
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
					}

					 
					// 检查用户自定义模板(第一列不是主键标识串)
					if (errorflag)
					{ 
							Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
							onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
							String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
							if ("0".equals(uniquenessvalid) || "".equals(onlyname))// 检查是否设置了唯一性指标
								throw new GeneralException("唯一性指标没有设置！");
							else
							// 检查是否有唯一性指标列
							{
								
								//	检查是否有唯一性指标列
								String a_royalty_relation_fields=","+royalty_relation_fields+","; 
								HashMap afieldMap=new HashMap();
								
								for (int i = 0; i < cols; i++)
								{
									String colname = "";
									Cell cell = row.getCell((short) i);
									if (cell != null)
									{
										switch (cell.getCellType())
										{
										case Cell.CELL_TYPE_STRING:
											if (cell.getCellComment() == null)
												throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");
											else
												colname = cell.getCellComment().getString().getString().trim();
											break;
										default:
											colname = "";
										}
									} else
									{
										//errorflag = true;
										break;
									}
									colname = colname.replaceAll("\\r", "").replaceAll("\\n", "");
									
									if("1".equals(royalty_valid)&&colname.length()>0)
									{
										afieldMap.put(colname.toLowerCase(),"1");
										if(a_royalty_relation_fields.indexOf(","+colname.toLowerCase()+",")!=-1)
											relationFieldMap.put(colname.toLowerCase(),String.valueOf(i));
									} 
									if (colname.equalsIgnoreCase(onlyname))
									{
										errorflag = false;
										onlyColIndex = i;
									
									}
								}
								
								
								if("1".equals(royalty_valid))
								{
									String[] temps=royalty_relation_fields.toLowerCase().split(",");
									boolean flag=false;
									for(int i=0;i<temps.length;i++)
									{
										if(temps[i]!=null&&temps[i].trim().length()>0)
										{
											if(afieldMap.get(temps[i])==null)
											{
												flag=true;
											}
										}
									}
									errorflag=flag;
									if (errorflag)
										throw new GeneralException("关联指标列在导入文件中不存在！");
								}
								
								
								
								if (errorflag)
									throw new GeneralException("唯一性指标列在导入文件中不存在！");
							}
						
						
						 
						
						
					}
				}
				if (errorflag)
					throw new GeneralException("请用导出的模板Excel来导入数据！");

				short startCol = 1;
				
				if("1".equals(royalty_valid))
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select *  from " + tablename+" where 1=1 ");
			/*		if (oper.equalsIgnoreCase("sp"))
					{
						buf.append(" and salaryid=" + salaryid);
						buf.append(" and (( curr_user='" + this.userView.getUserId() + "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( AppUser is null or AppUser Like '%;" + this.userView.getUserName()
								+ ";%' ) and  ( sp_flag='06'  ) ) ) ");
						if ( _count != null && _bosdate != null)
						{
							buf.append(" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
							buf.append(Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m"));
						}
					} */
					this.frowset = dao.search(buf.toString());
					SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd"); 
					while (this.frowset.next())
					{
						String only_value = this.frowset.getString(onlyname);  
						String flag =only_value ;
						String[] temps=royalty_relation_fields.toLowerCase().split(",");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0&&!temps[i].equalsIgnoreCase(onlyname))
							{
								aitem = DataDictionary.getFieldItem(temps[i].trim());
								 
								if("A".equalsIgnoreCase(aitem.getItemtype()))
								{
										if(this.frowset.getString(temps[i])==null||this.frowset.getString(temps[i]).trim().length()==0)
											flag+="|null";
										else
											flag+="|"+this.frowset.getString(temps[i]);
								} 
								else if("D".equalsIgnoreCase(aitem.getItemtype()))
								{
										if(this.frowset.getDate(temps[i])==null)
											flag+="|null";
										else
											flag+="|"+df.format(this.frowset.getDate(temps[i]));
								}
								 
							}
						}
						
						if(maxZ1ByPersonMap.get(only_value)==null)
							maxZ1ByPersonMap.put(only_value,this.frowset.getString("a00z1"));
						else
						{
							int z1=Integer.parseInt((String)maxZ1ByPersonMap.get(only_value));
							if(this.frowset.getInt("a00z1")>z1)
								maxZ1ByPersonMap.put(only_value,this.frowset.getString("a00z1"));
							
						}
						
						flagColValMap.put(flag, "");
					}						
				}
				else if (onlyname.length() > 0)// 自定义模板的情况
				{
					startCol = 0;
					String _sql="select distinct " + onlyname + " from " + tablename+" where 1=1 ";
					if ("sp".equalsIgnoreCase(oper))
					{
						_sql+=" and salaryid=" + salaryid;
						_sql+=" and (( curr_user='" + this.userView.getUserId() + "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  ) or AppUser Like '%;" + this.userView.getUserName()
								+ ";%' ) and  ( sp_flag='06') ) ) ";
						if ( _count != null && _bosdate != null)
						{
							_sql+=" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ";
							_sql+=Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m");
						}
					}
					
					this.frowset = dao.search(_sql);
					while (this.frowset.next())
						flagColValMap.put(this.frowset.getString(1), "");
					
					if (onlyname.length()>0&&SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					{
						_sql=_sql.replaceAll("distinct " + onlyname," distinct a0101 ");
						String _sql2=_sql;
						_sql+=" and ("+onlyname+" is null or "+onlyname+"='' ) ";
						this.frowset = dao.search(_sql);
						while (this.frowset.next())
							a0101ValueMap.put(this.frowset.getString(1).trim(), "");
						
						 
						this.frowset = dao.search(_sql2);
						while (this.frowset.next())
							a0101ValueMap2.put(this.frowset.getString(1).trim(), "");
						
					}
					
					
				}else
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select *  from " + tablename+" where 1=1 ");
					if ("sp".equalsIgnoreCase(oper))
					{
						buf.append(" and salaryid=" + salaryid);
						buf.append(" and (( curr_user='" + this.userView.getUserId() + "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  ) or AppUser Like '%;" + this.userView.getUserName()
								+ ";%' ) and  ( sp_flag='06'  ) ) ) ");
						if ( _count != null && _bosdate != null)
						{
							buf.append(" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
							buf.append(Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m"));
						}
					}
					this.frowset = dao.search(buf.toString());
					while (this.frowset.next())
					{
						String nASE = this.frowset.getString("NBASE");
						String a0100 = this.frowset.getString("A0100");
						String a00Z0 = this.frowset.getDate("A00Z0").toString();
						String a00Z1 = this.frowset.getString("A00Z1");
						String flag = nASE + "|" + a0100 + "|" + a00Z0 + "|" + a00Z1;
						flagColValMap.put(flag, "");
					}						
				}
				
				 
				
				for (short c = startCol; c < cols; c++)
				{
					Cell cell = row.getCell(c);
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
							throw new GeneralException("标题行存在空标题！");
						if (cell.getCellComment() == null)
							throw new GeneralException("请设置列[" + cell.getStringCellValue() + "]的批注！");

						String field = cell.getCellComment().getString().toString().trim().replaceAll("\\r", "").replaceAll("\\n", "");	
						if(DataDictionary.getFieldItem(field)==null)
						{
							if (onlyname.length() > 0)// 自定义模板的情况
								throw new GeneralException("后台库表不存在字段[" + cell.getStringCellValue() + "]！");
							else
								throw new GeneralException("请用导出的模板Excel来导入数据！");
						}
						if(fieldMap.get(field.toUpperCase())==null)
							throw new GeneralException("当前类别中无【"+title+"】项目,不允许导入！");
						
						
						if("a0101".equalsIgnoreCase(field.toLowerCase()))
							a0101map.put(new Short(c), field + ":" + cell.getStringCellValue());	
						
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
						
						String pri = this.userView.analyseFieldPriv(field);
						if ("1".equals(pri) || "0".equals(pri)) // 只读或者是没有权限
							continue;

						if (!"0".equals(codesetid))
						{
							if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid))
							{
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'  and codeitemid=childid  union all ");
							} else
							{
								if("UM".equalsIgnoreCase(codesetid))
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) union all ");
								else
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
								// 因为导入的时候有可能更新为非叶子机构所以在此放开限制为叶子部门的代码 + "' and codeitemid not in (select parentid from organization where codesetid='" + codesetid + "') union all ");
										+ "' union all ");
							}
						}
						
						
						 
						if ((onlyname + ",b0110,e01a1,e0122,a0100,a0101").indexOf(field.toLowerCase()) == -1)// 单位 部门 姓名字段不更新
						{ 
							headList.add(DataDictionary.getFieldItem(field));
							map.put(new Short(c), field + ":" + cell.getStringCellValue());
							sql.append(field + "=?,");
							update_item_str.append(","+field);
							updateFidsCount++;
							maxColCount = c;
						}
					} else
						break;
				}
				if (codeBuf.length() > 0)
				{
					codeBuf.setLength(codeBuf.length() - " union all ".length());
					try
					{
						RowSet rs = dao.search(codeBuf.toString());
						while (rs.next())
						{
							if (!"UM".equalsIgnoreCase(rs.getString("codesetid")) && !"UN".equalsIgnoreCase(rs.getString("codesetid")) && !"@K".equalsIgnoreCase(rs.getString("codesetid")))
								codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
							else
								codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid") + ":" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
						}

					} catch (SQLException e)
					{
						e.printStackTrace();
					}

				}

				sql.setLength(sql.length() - 1);
				repeatsql.append(sql.toString()); //更新数据，网易需求，lis
				
				if("1".equals(royalty_valid))
				{
					/*
					sql.append(" where   "+onlyname+"=? ");
					 
					for(int i=0;i<relationFieldList.size();i++)
					{
						aitem=(FieldItem)relationFieldList.get(i);
						if(!aitem.getItemid().equalsIgnoreCase(onlyname))
						{ 
							if(aitem.getItemtype().equalsIgnoreCase("D"))
								sql.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(aitem.getItemid(),"yyyy-MM-dd"),"''")+"=? ");
							else
								sql.append(" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"=? "); 
						}
					}
					*/
					sql.append(" where 1=1 ");
				}
				else if (onlyname.length() == 0)
					sql.append(" where NBASE=? and A0100=? and A00Z0=? and A00Z1=?");
				else
				// 用户自定义模板方式
				{
					repeatsql.append(" where 1=1 ");
					sql.append(" where "+onlyname +"=? ");
					if ("sp".equalsIgnoreCase(oper) && _count != null && _bosdate != null)// 审批中还要受次数和业务日期的限制 发放中只是用唯一标志来更新
					{
						sql.append(" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
						sql.append(Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m"));
					}
				}

				if ("sp".equalsIgnoreCase(oper))
				{
					sql.append(" and salaryid=" + salaryid);
					x = sql.length() + 4;
          
					sql.append(" and (( curr_user='" + this.userView.getUserId() 
					        + "' and ( sp_flag='02' or sp_flag='07' ) ) ");
					if (gzbo.isAllowEditSubdata_Sp(gz_module)){  //原来状态是sp_flag='06' or  sp_flag='03'， 应该只有06才对 wangrd 2013-11-15
    					sql.append(" or ( ( (AppUser is null  "
    					        +gzbo.getPrivWhlStr("")+"  ) or AppUser Like '%;" + this.userView.getUserName()
    							+ ";%' ) and  ( sp_flag='06' ) ) ");
					}
					sql.append(") ");
					
					if ( _count != null && _bosdate != null)
					{
						sql.append(" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
						sql.append(Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m"));
					}
				} else if ("fafang".equalsIgnoreCase(oper))
				{
					int sqlLen = sql.length();
					if (isShare)// 薪资发放-共享
					{
						
						
						if (isGZmanager == false)// 薪资发放-共享-非管理员
						{
							// 要控制人员范围
							
//							if(gzbo.getControlByUnitcode().equals("1"))
//							{ 
//								String whl_str=gzbo.getWhlByUnits();
//								sql.append(whl_str);
//								repeatsql.append(whl_str);
//								 
//							}
//							else  
							{
								
								/**导入数据*/
								String dbpres=gzbo.getTemplatevo().getString("cbase");
								/**应用库前缀*/
								String[] dbarr=StringUtils.split(dbpres, ",");
								StringBuffer sub_str=new StringBuffer("");
								
								String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
								String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
								if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
									/**导入数据*/
									/**应用库前缀*/
									for(int i=0;i<dbarr.length;i++)
									{
										String pre=dbarr[i];
										if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
										{
											sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and 1=2 )");
										}
										else
										{
											sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and upper(" + gzbo.getGz_tablename() + ".a0100) in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
										}
									}
									if(sub_str.length()>0)
									{
										sql.append(" and ( "+sub_str.substring(3)+" )");
										sql.append(" and ( "+sub_str.substring(3)+" )");
									}
								}else{
									String privsql = gzbo.getPrivSQL("", "", salaryid, b_units);
									sql.append(" and ("+privsql+")");
									sql.append(" and ("+privsql+")");
								}
							}
							
							
							/*
							String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
							if (a_code.length() >= 2)
							{
								String codesetid = a_code.substring(0, 2);
								String value = a_code.substring(2);
								if (codesetid.equalsIgnoreCase("UN"))
								{
									sql.append(" and (B0110 like '");
									sql.append(value);
									sql.append("%'");
									if (value.equalsIgnoreCase(""))
										sql.append(" or B0110 is null");
									sql.append(")");
								} else if (codesetid.equalsIgnoreCase("UM"))
								{
									sql.append(" and E0122 like '");
									sql.append(value);
									sql.append("%'");
								}
							} else if (a_code.trim().length() == 0)// 没有管理权限
								sql.append(" and 1=2 ");
							*/
							
							
							
							
							
							// 只能修改sp_flag2为起草（01）和驳回（07）的记录
							sql.append(" and sp_flag2 in ('01','07')");
							repeatsql.append(" and sp_flag2 in ('01','07')");
						} else
						// 薪资发放-共享-管理员
						{
							if (isApprove)// 走审批 只能修改起草和驳回
							{
								sql.append(" and sp_flag in ('01','07')");
								repeatsql.append(" and sp_flag in ('01','07')");
							}
							else {//不走审批的需要控制提交后不能导入  wangrd 2013-11-15
							    if (!gzbo.isAllowEditSubdata()){
							        sql.append(" and sp_flag in ('01','07')"); 
							        repeatsql.append(" and sp_flag in ('01','07')");
							    }
							}
						}
					} else
					// 薪资发放-不共享
					{
						if (isApprove)// 走审批
						{	        
                            sql.append(" and sp_flag in ('01','07')");
                            repeatsql.append(" and sp_flag in ('01','07')");
						}
						else {
						    if (!gzbo.isAllowEditSubdata()){//不走审批的需要控制提交后不能导入  wangrd 2013-11-15
                                sql.append(" and sp_flag in ('01','07')");  
                                repeatsql.append(" and sp_flag in ('01','07')");
                            }
						    
						}
					}
					if (sql.length() > sqlLen)
						x = sqlLen + 4;
					else
						x = sqlLen;
				}
			}
			
			repeatsql.append(" and a0100=? and nbase=? and a00z0=? and a00z1=?");//更新数据，网易需求，lis
			ArrayList list2 = new ArrayList();
			ArrayList nullOnlyValueList=new ArrayList();
			SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
			ArrayList newRecordList=new ArrayList(); //新增记录
			HashMap updateWheres = new HashMap();//where条件集合，网易，lis
			ArrayList wyNewRecordList=new ArrayList(); //新增记录，网易，lis 2015-10-8
			//对excel数据遍历
			for (int j = 1; j < rows; j++)
			{
				ArrayList list = new ArrayList();
				row = sheet.getRow(j);
				
				if (onlyname.length() == 0)
				{
					Cell flagCol = row.getCell((short) 0);
					if (flagCol != null)
					{
						totalRowCount++;
						switch (flagCol.getCellType())
						{
						case Cell.CELL_TYPE_BLANK:
							throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
						case Cell.CELL_TYPE_STRING:
							if (flagCol.getRichStringCellValue().toString().trim().length() == 0)
								throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
						}
					} else
					{
						if (row.getCell(1) == null)// 说明是空行
							continue;
						else
						{
							onlyValueRepeat.add(Integer.toString(j));
							continue;
						}
					}
					
					String flagColVal = flagCol.getStringCellValue();
					if(flagColValMap.get(flagColVal)==null)//数据库中不存在
					{
						onlyValueRepeat.add(Integer.toString(j));
						continue;
					}					
					
					String[] temp = flagCol.getStringCellValue().split("\\|");					
					for (short c = 1; c <= maxColCount; c++)
					{
						Cell cell1 = row.getCell(c);
						String fieldItems = (String) map.get(new Short(c));
						if (fieldItems == null)// 过滤掉只读的列
							continue;
						String[] fieldItem = fieldItems.split(":");
						String field = fieldItem[0];
						String fieldName = fieldItem[1];
						String itemtype = DataDictionary.getFieldItem(field).getItemtype();
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
						int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();

						// String pri =
						// this.userView.analyseFieldPriv(fieldName);
						// if (pri.equals("1") || pri.equals("0")) //只读或者是没有权限
						// continue;

						String value = "";
						if (cell1 != null)
						{
							switch (cell1.getCellType())
							{
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell1.getNumericCellValue();
								value = Double.toString(y);
								if (value.indexOf("E") > -1)
								{
									String x1 = value.substring(0, value.indexOf("E"));
									String y1 = value.substring(value.indexOf("E") + 1);

									value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								}
								value = PubFunc.round(value, decwidth);
								list.add(new Double((PubFunc.round(value, decwidth))));
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell1.getRichStringCellValue().toString();
								if (!"0".equals(codesetid) && !"".equals(codesetid))
								{
									if("UM".equalsIgnoreCase(codesetid))
									{
										if (codeColMap.get("UMa04v2u" + value.trim()) != null)
											value = (String) codeColMap.get("UMa04v2u" + value.trim());
										else if (codeColMap.get("UNa04v2u" + value.trim()) != null)
											value = (String) codeColMap.get("UNa04v2u" + value.trim());
										else
											value=null;
									}
									else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
											value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
									else
											value = null;
								}
								list.add(value);
								break;
							case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
								if ("N".equals(itemtype))
								{
									value = PubFunc.round(value, decwidth);
									list.add(new Double(value));
								} else
									list.add(null);
								break;
							default:
								list.add(null);
							}

							String msg = "";
							if ("N".equals(itemtype) || "D".equals(itemtype))
							{
								if (!this.isDataType(decwidth, itemtype, value))
								{
									msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
									throw new GeneralException(msg);
								}
							}
						} else
							list.add(null);

					}
					a0100s.append("a0100 ='" + temp[1] + "' or ");
					a0100sMap.put(flagCol.getStringCellValue(), "" + j);
					list.add(temp[0]);
					list.add(temp[1]);
					list.add(Date.valueOf(temp[2]));
					list.add(new Integer(temp[3]));
					list2.add(list);
				} else
				// 用户自定义模板的方式
				{
					boolean isNull_onlyCol=false;  //北京移动，唯一标识是否为空
					String  a0101_value="";        //姓名列的值
					
					Cell flagCol = row.getCell((short) onlyColIndex);
					String onlyFildValue = "";
					if (flagCol != null)
					{
						totalRowCount++;
						switch (flagCol.getCellType())
						{
						case Cell.CELL_TYPE_BLANK:
							if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
								onlyFildValue = "";
							else
								throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
						case Cell.CELL_TYPE_STRING:
							onlyFildValue = flagCol.getRichStringCellValue().toString().trim();
							break;
						case Cell.CELL_TYPE_FORMULA:
							if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
								onlyFildValue = "";
							else
								throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
						case Cell.CELL_TYPE_NUMERIC:
							double y = flagCol.getNumericCellValue();
							onlyFildValue = y + "";
							if (onlyFildValue.indexOf("E") > -1)
							{
								String x1 = onlyFildValue.substring(0, onlyFildValue.indexOf("E"));
								String y1 = onlyFildValue.substring(onlyFildValue.indexOf("E") + 1);

								onlyFildValue = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								int decwidth = DataDictionary.getFieldItem(onlyname).getDecimalwidth();
								onlyFildValue = PubFunc.round(onlyFildValue, decwidth);
							}
							break;
						default:
							onlyFildValue = "";
						}
					} else
					// 唯一性指标值没有填写过
					{
						if (onlyColIndex > 0)
						{
							if (row.getCell(onlyColIndex - 1) == null && row.getCell(0) == null)// 说明是空行
								continue;
						} else if (onlyColIndex == 0)
						{
							if (row.getCell(1) == null)// 说明是空行
								continue;
						}
						if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
							totalRowCount++;
						else
						{
							onlyValueRepeat.add(Integer.toString(j));
							continue;
						}
					}

					if (onlyFildValue.trim().length() == 0)// 唯一性指标值为空串
					{
						
						if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
						{
							
							isNull_onlyCol=true;
						}
						else
						{
							onlyValueRepeat.add(Integer.toString(j));
							continue;
						}
						
					} else  //唯一性指标不为空
					{
						
						if(!"1".equals(royalty_valid))//不是提成薪资
						{
							//-------------------网易需求----------------------------
							if (!"sp".equalsIgnoreCase(oper)){
								ArrayList repeatList = new ArrayList();
								ArrayList updateWhere = new ArrayList();//一个人可能有多条数据，如果大于一，则从第二条数据更新
								String temp = (String) onlyRepeat.get(onlyFildValue);
								int isRepeat = 0;
								//excel中是重复数据且在数据库中存在
								if(StringUtils.isNotBlank(temp)){
									if(updateWheres.containsKey(onlyFildValue))
										updateWhere = (ArrayList)updateWheres.get(onlyFildValue);
									if(updateWhere.size()==0){
										
										//人员排序
										String orderBy = null;
										String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView); 
										if(order_str!=null&&order_str.trim().length()>0&&isExistErrorItem(order_str,gzbo))
											orderBy = order_str;
										else
											orderBy = " dbid,a0000, A00Z0, A00Z1";
										//根据人员唯一性指标在薪资表中查询当前人员的a0100、nbase、a00z0、a00z1
										String sqlrepeat = "select a0100,nbase,a00z0,a00z1 from "+tablename+" where "+onlyname+"='"+onlyFildValue+"' order by " + orderBy;
										RowSet rs = dao.search(sqlrepeat);
										while(rs.next()){
											LazyDynaBean bean = new LazyDynaBean();
											bean.set("a0100", rs.getString("a0100"));
											bean.set("nbase", rs.getString("nbase"));
											bean.set("a00z0", rs.getDate("a00z0"));
											bean.set("a00z1", rs.getString("a00z1"));
											updateWhere.add(bean);
											
											//得到薪资表中人员的最大发放次数
											if(maxZ1ByPersonMap.get(onlyFildValue)==null)
												maxZ1ByPersonMap.put(onlyFildValue,rs.getString("a00z1"));
											else
											{
												int z1=Integer.parseInt((String)maxZ1ByPersonMap.get(onlyFildValue));
												if(rs.getInt("a00z1")>z1)
													maxZ1ByPersonMap.put(onlyFildValue,rs.getString("a00z1"));
												
											}
										}
										updateWheres.put(onlyFildValue, updateWhere);
									}
									if(updateWhere.size() >= Integer.parseInt(temp)+1){	//如果数据库中一个人存在多条数据
										//从第二条重复数据开始继续更新对应excel中的重复数据
										isRepeat = 1;
									}else{
										isRepeat = 2;//重复数据新增
										repeatList.add(onlyFildValue);
									}
								}else if(flagColValMap.get(onlyFildValue) == null){
									isRepeat = 3;//数据库中不存在，新增
									repeatList.add(onlyFildValue);
								}
								
								for (short c = 0; c <= maxColCount; c++)
								{
									Cell cell1 = row.getCell(c);
									
									String fieldItems = (String) map.get(new Short(c)); 
									
									if (fieldItems == null)// 过滤掉只读的列
										continue;
									String[] fieldItem = fieldItems.split(":");
									String field = fieldItem[0];
									String fieldName = fieldItem[1];
									String itemtype = DataDictionary.getFieldItem(field).getItemtype();
									String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
									int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();
									 
									// String pri =
									// this.userView.analyseFieldPriv(fieldName);
									// if (pri.equals("1") || pri.equals("0")) //只读或者是没有权限
									// continue;
									 
									String value = "";
									if (cell1 != null)
									{
										switch (cell1.getCellType())
										{
										case Cell.CELL_TYPE_FORMULA:
											break;
										case Cell.CELL_TYPE_NUMERIC:
											double y = cell1.getNumericCellValue();
											value = Double.toString(y);
											if (value.indexOf("E") > -1)
											{
												String x1 = value.substring(0, value.indexOf("E"));
												String y1 = value.substring(value.indexOf("E") + 1);
	
												value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
											}
											value = PubFunc.round(value, decwidth);
											repeatList.add(new Double((PubFunc.round(value, decwidth))));
											break;
										case Cell.CELL_TYPE_STRING:
											value = cell1.getRichStringCellValue().toString(); 
											if (!"0".equals(codesetid) && !"".equals(codesetid))
											{
												if("UM".equalsIgnoreCase(codesetid))
												{
													if (codeColMap.get("UMa04v2u" + value.trim()) != null)
														value = (String) codeColMap.get("UMa04v2u" + value.trim());
													else if (codeColMap.get("UNa04v2u" + value.trim()) != null)
														value = (String) codeColMap.get("UNa04v2u" + value.trim());
													else
														value=null;
												}
												else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
														value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
												else
														value = null;
											}
											if("D".equals(itemtype)&&value!=null&&value.trim().length()==0)
												value=null;
											
											if("N".equals(itemtype)&&value!=null)
											{
												value = PubFunc.round(value, decwidth);
												if(decwidth==0){
													repeatList.add(new Integer(value));
												}
												else{
													repeatList.add(new Double(value));
												}
											}
											else if("D".equals(itemtype)&&value!=null&&value.trim().length()>0)
											{
												java.sql.Date d_t=null;
												value=value.replaceAll("\\.","-");
												java.util.Date src_d_t=DateUtils.getDate(value,"yyyy-MM-dd"); 
												if(src_d_t!=null)
													d_t=new java.sql.Date(src_d_t.getTime());
												repeatList.add(d_t);
											}
											else{
												repeatList.add(value);
											}
											break;
										case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
											if ("N".equals(itemtype))
											{
												if(value==null||value.trim().length()==0)
													value="0";
												else
													value = PubFunc.round(value, decwidth);
												repeatList.add(new Double(value));
											} else{
												repeatList.add(null);
											}
											break;
										default:
											repeatList.add(null);
										}
										String msg = "";
										if ("N".equals(itemtype) || "D".equals(itemtype))
										{
											if (value!=null&&value.trim().length()>0&&!this.isDataType(decwidth, itemtype, value))
											{
												msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
												throw new GeneralException(msg);
											}
										}
									} else{
										repeatList.add(null);
									}
									 
								}
								
								//excel中是重复数据且在数据库中存在
								if(isRepeat == 1){
									LazyDynaBean tempbean = (LazyDynaBean) updateWhere.get(Integer.parseInt(temp));
									if(tempbean != null){	//当前excel中重复的行在数据库中有对应数据
										repeatList.add(tempbean.get("a0100"));
										repeatList.add(tempbean.get("nbase"));
										repeatList.add(tempbean.get("a00z0"));
										repeatList.add(tempbean.get("a00z1"));
										onlyRepeatList.add(repeatList);
										onlyRepeat.put(onlyFildValue, (Integer.parseInt(temp)+1)+"");
									}
								}else if(isRepeat == 2 || isRepeat == 3){//新增数据
									wyNewRecordList.add(repeatList);//excel中重复数据，但是在数据库中存在的，或是在数据库中不存在的，网易需求，lis
								}
								
								//excel中不是重复数据且在数据库中存在
								if (onlyValueMap.get(onlyFildValue) == null && flagColValMap.get(onlyFildValue) != null)
									onlyRepeat.put(onlyFildValue, 1+"");
								
								//存放excel中所有的人
								if (allOnlyFieldInExcel.get(onlyFildValue) == null)
									allOnlyFieldInExcel.put(Integer.toString(j), onlyFildValue);
							}
							//----------------------------网易需求 end --------------------------------------------
							
							//excel中不是重复数据且在数据库中存在
							if (onlyValueMap.get(onlyFildValue) == null && flagColValMap.get(onlyFildValue) != null)
								onlyValueMap.put(onlyFildValue, Integer.toString(j));
							else
							{
								if("sp".equals(oper))
									onlyValueRepeat.add(Integer.toString(j));
								continue;
							}
						}
					}
					
					
					String key=onlyFildValue;
					for(int i=0;i<relationFieldList.size();i++)
					{
						aitem=(FieldItem)relationFieldList.get(i);
						String codesetid=aitem.getCodesetid();
						String index=(String)relationFieldMap.get(aitem.getItemid());
						if(!aitem.getItemid().equalsIgnoreCase(onlyname))
						{
							String value="null";
							Cell cell1 = row.getCell(Integer.parseInt(index));
							if(cell1!=null)
							{
								if(cell1.getCellType()==Cell.CELL_TYPE_STRING){
										value = cell1.getRichStringCellValue().toString(); 
										if (!"0".equals(codesetid) && !"".equals(codesetid))
										{
											if("UM".equalsIgnoreCase(codesetid))
											{
												if (codeColMap.get("UMa04v2u" + value.trim()) != null)
													value = (String) codeColMap.get("UMa04v2u" + value.trim());
												else if (codeColMap.get("UNa04v2u" + value.trim()) != null)
													value = (String) codeColMap.get("UNa04v2u" + value.trim());
												else
													value="";
											}
											else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
													value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
											else
													value ="";
										}   
								}
							}
							
							if(value.length()>0&& "D".equalsIgnoreCase(aitem.getItemtype()))
							{
								value=value.replaceAll("\\-", "/");  //需完善  邓灿
							}
							if(value.trim().length()==0)
								key+="|null";
							else
								key+="|"+value;
							
						} 
					}
					
					boolean isNewRecord=false;
					if("1".equals(royalty_valid)&&flagColValMap.get(key)==null)
						isNewRecord=true;
					
					if("1".equals(royalty_valid))
					{
						if (onlyValueMap.get(key) == null)
							onlyValueMap.put(key, Integer.toString(j));
						else
						{
							onlyValueRepeat.add(Integer.toString(j));
							continue;
						}
					}
					
					if(isNewRecord)
						list.add(onlyFildValue);
					for (short c = 0; c <= maxColCount; c++)
					{
						Cell cell1 = row.getCell(c);
						
						 
						if(a0101map.get(new Short(c))!=null)
						{
							if (cell1 != null&&cell1.getCellType()==Cell.CELL_TYPE_STRING)
							{	 
								    String _value = cell1.getRichStringCellValue().toString();
									a0101_value=_value;
							}		
							continue;
						}
						String fieldItems = (String) map.get(new Short(c)); 
						
						if (fieldItems == null)// 过滤掉只读的列
							continue;
						String[] fieldItem = fieldItems.split(":");
						String field = fieldItem[0];
						String fieldName = fieldItem[1];
						String itemtype = DataDictionary.getFieldItem(field).getItemtype();
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
						int decwidth = DataDictionary.getFieldItem(field).getDecimalwidth();
						 
						// String pri =
						// this.userView.analyseFieldPriv(fieldName);
						// if (pri.equals("1") || pri.equals("0")) //只读或者是没有权限
						// continue;
						 
						String value = "";
						if (cell1 != null)
						{
							switch (cell1.getCellType())
							{
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double y = cell1.getNumericCellValue();
								value = Double.toString(y);
								if (value.indexOf("E") > -1)
								{
									String x1 = value.substring(0, value.indexOf("E"));
									String y1 = value.substring(value.indexOf("E") + 1);

									value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								}
								value = PubFunc.round(value, decwidth);
								list.add(new Double((PubFunc.round(value, decwidth))));
								break;
							case Cell.CELL_TYPE_STRING:
								value = cell1.getRichStringCellValue().toString(); 
								if (!"0".equals(codesetid) && !"".equals(codesetid))
								{
									if("UM".equalsIgnoreCase(codesetid))
									{
										if (codeColMap.get("UMa04v2u" + value.trim()) != null)
											value = (String) codeColMap.get("UMa04v2u" + value.trim());
										else if (codeColMap.get("UNa04v2u" + value.trim()) != null)
											value = (String) codeColMap.get("UNa04v2u" + value.trim());
										else
											value=null;
									}
									else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
											value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
									else
											value = null;
								}
								if("D".equals(itemtype)&&value!=null&&value.trim().length()==0)
									value=null;
								
								if("N".equals(itemtype)&&value!=null)
								{
									value = PubFunc.round(value, decwidth);
									if(decwidth==0){
										list.add(new Integer(value));
									}
									else{
										list.add(new Double(value));
									}
								}
								else if("D".equals(itemtype)&&value!=null&&value.trim().length()>0)
								{
									java.sql.Date d_t=null;
									value=value.replaceAll("\\.","-");
									java.util.Date src_d_t=DateUtils.getDate(value,"yyyy-MM-dd"); 
									if(src_d_t!=null)
										d_t=new java.sql.Date(src_d_t.getTime());
									list.add(d_t);
								}
								else{
									list.add(value);
								}
								break;
							case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
								if ("N".equals(itemtype))
								{
									if(value==null||value.trim().length()==0)
										value="0";
									else
										value = PubFunc.round(value, decwidth);
									list.add(new Double(value));
								} else{
									list.add(null);
								}
								break;
							default:
								list.add(null);
							}
							String msg = "";
							if ("N".equals(itemtype) || "D".equals(itemtype))
							{
								if (value!=null&&value.trim().length()>0&&!this.isDataType(decwidth, itemtype, value))
								{
									msg = "源数据(" + fieldName + ")中数据:" + value + " 不符合格式!";
									throw new GeneralException(msg);
								}
							}
						} else{
							list.add(null);
						}
					}
					 
					if(isNull_onlyCol)
					{
						if(a0101_value==null||a0101_value.trim().length()==0)
						{
							String msg = "源数据当唯一指标的值为空时需参考的姓名列不能有空值!";
							throw new GeneralException(msg);
						}
						else
						{
							if(a0101ValueMap2.get(a0101_value.trim())==null)
							{
								onlyValueRepeat.add(Integer.toString(j));
								continue;
							}
							
							
							if(a0101ValueMap.get(a0101_value.trim())==null)
							{
								String msg = a0101_value+" 源数据唯一性指标的值为空,但库中唯一性指标有值 不符合格式!";
								throw new GeneralException(msg);
							}
							list.add(a0101_value);
							nullOnlyValueList.add(list);
						}
						
					}
					else
					{
						if(isNewRecord)
							newRecordList.add(list);
						else  
						{
							if("1".equals(royalty_valid))
							{ 
								String[] temps=key.split("\\|");
							/*
								for(int i=0;i<temps.length;i++)
								{
									if(temps[i].equalsIgnoreCase("null"))
										list.add("");
									else
										list.add(temps[i]);
								}
								list2.add(list);
								*/
								
								String tempSql=sql.toString();
								tempSql+=" and   "+onlyname+"='"+temps[0]+"'";
								
								int n=1;
								for(int i=0;i<relationFieldList.size();i++)
								{
										aitem=(FieldItem)relationFieldList.get(i);
										if(!aitem.getItemid().equalsIgnoreCase(onlyname))
										{ 
											if(!"null".equalsIgnoreCase(temps[n]))
											{
												if("D".equalsIgnoreCase(aitem.getItemtype()))
													tempSql+=" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+temps[n].replaceAll("\\/", "-")+"' ";
												else
													tempSql+=" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='"+temps[n]+"' "; 
											}
											else
											{ 
												if(Sql_switcher.searchDbServer()==Constant.ORACEL)
												  tempSql+=" and "+ aitem.getItemid() +" is null "; 
												else
												  tempSql+=" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='' "; 
											}
											n++;
										}
								}
								dao.update(tempSql,list);
							}
							else
							{
								a0100s.append(onlyname + "='" + onlyFildValue + "' or ");
								a0100sMap.put(onlyFildValue, "" + j);
								list.add(onlyFildValue);
								list2.add(list);
							}
						}
					}
				}

			}
			if (updateFidsCount == 0)
			{
				this.getFormHM().put("okCount", "0");
				return;
			}
			
			if(!"1".equals(royalty_valid))
			{				
				try
				{
					if(list2.size()>0)
						dao.batchUpdate(sql.toString(), list2);
					if(!"sp".equalsIgnoreCase(oper)){
						if(onlyRepeatList.size()>0)//更新excel中与薪资表中对应的的重复数据
							dao.batchUpdate(repeatsql.toString(), onlyRepeatList);
						
						if(wyNewRecordList.size()>0)  //新曾数据集合
						{
							HashMap mapData=insertTempTable(dao,repeatsql.toString(), onlyname, wyNewRecordList, gzbo, maxZ1ByPersonMap);
							int  inserNum = (Integer)mapData.get("num");
							HashMap errorOnlyFieldMap = (HashMap)mapData.get("errorOnlyFieldMap");
							
							//取得不在权限范围内的人集合
							Iterator iter2 = allOnlyFieldInExcel.entrySet().iterator();
							while (iter2.hasNext()) {
								Map.Entry entry = (Map.Entry) iter2.next();
								String key = (String)entry.getKey();//行数
								String val = (String)entry.getValue();//唯一性指标
								if(errorOnlyFieldMap.containsKey(val))
									onlyValueRepeat.add(key);
							}
						}
					}
				}
				catch(Exception ee)
				{				 
					ee.printStackTrace();
					String message=ee.getMessage();
					if(message.indexOf("data is not corrected")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
					if(message.indexOf("转换为数据类型")!=-1)
						throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	 
				}
			}
			if(newRecordList.size()>0)  //新建数据
			{
				int inserNum=insertNewRecords(dao ,headList,newRecordList,onlyname,maxZ1ByPersonMap,gzbo);
				if(newRecordList.size()>inserNum)
					totalRowCount=totalRowCount-(newRecordList.size()-inserNum);
			}
			
			if (onlyname.length()>0&&SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				if(nullOnlyValueList.size()>0)
				{
					String tempSql=sql.toString();
					tempSql=tempSql.replaceAll(onlyname +"=\\?"," a0101=? and ("+onlyname+" is null or "+onlyname+"='' )");
					dao.batchUpdate(tempSql, nullOnlyValueList);
					
				}
			}
			
			if ("sp".equalsIgnoreCase(oper))   //如果是审批模块的导入，需同步临时表数据。
			{
				StringBuffer whl=new StringBuffer("");
				whl.append(" and salaryhistory.salaryid=" + salaryid);
				whl.append(" and (( salaryhistory.curr_user='" + this.userView.getUserId() + "' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) ) or ( ( (salaryhistory.AppUser is null  "+gzbo.getPrivWhlStr("salaryhistory")+"  ) or salaryhistory.AppUser Like '%;" + this.userView.getUserName()
						+ ";%' ) and  ( salaryhistory.sp_flag='06' or  salaryhistory.sp_flag='03' ) ) ) ");
				if ( _count != null && _bosdate != null)
				{
					whl.append(" and salaryhistory.a00z3=" + _count + " and  " + Sql_switcher.year("salaryhistory.a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
					whl.append(Sql_switcher.month("salaryhistory.a00z2") + "=" + getDatePart(_bosdate, "m"));
					if(update_item_str.length()>0)
					{
						synTempData(whl.toString(),update_item_str.toString(),salaryid);
					}
				}
			}
			

			StringBuffer buf = new StringBuffer("select * from " + tablename + " where 1=1 ");
			if ("sp".equalsIgnoreCase(oper))
				buf.append(" and salaryid=" + salaryid);
			if (sql.length() > x)//说明存在范围条件
			{
				if ("fafang".equalsIgnoreCase(oper))
				{
					buf.append(" and  not (" + sql.substring(x, sql.length()) + ") ");
					if("1".equals(gzbo.getControlByUnitcode())&&onlyname.length()!= 0)
					{
						String tempsql=sql.substring(x, sql.length());
						tempsql=tempsql.replaceAll(gzbo.getGz_tablename(),"aa");
						buf.append(" and not exists (select null from "+gzbo.getGz_tablename()+" aa where "+tempsql+" and "+gzbo.getGz_tablename()+".nbase=aa.nbase   and "+gzbo.getGz_tablename()+".a0100=aa.a0100  )");
						
					}
				
				}
				else if ("sp".equalsIgnoreCase(oper))
				{	
					StringBuffer tempBuf = new StringBuffer();
					if( _count != null && _bosdate != null)
					{
						tempBuf.append(" and a00z3=" + _count + " and  " + Sql_switcher.year("a00z2") + "=" + getDatePart(_bosdate, "y") + " and ");
						tempBuf.append(Sql_switcher.month("a00z2") + "=" + getDatePart(_bosdate, "m"));
						
						buf.append(" and  not (" + sql.substring(x, sql.length()-tempBuf.length()) + ") ");
						buf.append(tempBuf);
					}else
						buf.append(" and  not (" + sql.substring(x, sql.length()) + ") ");
				}

				this.frowset = dao.search(buf.toString());//判断在不可更新数据中是否有excel中数据
				while (this.frowset.next())
				{
					if (onlyname.length() == 0)
					{
						String nASE = this.frowset.getString("NBASE");
						String a0100 = this.frowset.getString("A0100");
						String a00Z0 = this.frowset.getDate("A00Z0").toString();
						String a00Z1 = this.frowset.getString("A00Z1");
						String flag = nASE + "|" + a0100 + "|" + a00Z0 + "|" + a00Z1;
						if (a0100sMap.get(flag) != null)//excel中所有人
						{
							String rowIndex = (String) a0100sMap.get(flag);
							onlyValueRepeat.add(rowIndex);
						}
					} else
					{
						String onlynamevalue = this.frowset.getString(onlyname);
						if (a0100sMap.get(onlynamevalue) != null)
						{
							String rowIndex = (String) a0100sMap.get(onlynamevalue);
							onlyValueRepeat.add(rowIndex);
						}
					}
				}
			}
			int errorCount = onlyValueRepeat.size();// 需要提示出的有问题数据
			int okCount = totalRowCount - errorCount;// 成功导入的条数
			this.getFormHM().put("okCount", okCount + "");
			String errorFileName = "";
			if (errorCount > 0)
			{
				// 生成提示excel
				errorFileName = this.generateErrorFile(onlyValueRepeat, sheet, form_file, okCount);
				/* 薪资发放：新建/导入数据 出现空白页 xiaoyun 2014-9-22 start */
				// errorFileName = errorFileName.replace(".xls", "#");
				errorFileName = SafeCode.encode(PubFunc.encrypt(errorFileName));
				/* 薪资发放：新建/导入数据 出现空白页 xiaoyun 2014-9-22 end */
			}
			this.getFormHM().put("errorFileName", errorFileName);

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 插入新记录
	 * @param headList
	 * @param newRecordList
	 * @param onlyColIndex
	 * @param maxZ1ByPersonMap
	 * @return
	 */
	public int insertNewRecords(ContentDAO dao ,ArrayList headList,ArrayList newRecordList,String onlyField,HashMap maxZ1ByPersonMap,SalaryTemplateBo gzbo) throws GeneralException
	{
		int num=0;
		try
		{
			String standardGzItemStr=gzbo.getStandardGzItemStr();
			String standardGzItemStr2="/A0100/A0101/A01Z0/USERFLAG/NBASE/A00Z2/A00Z3/A00Z0/A00Z1/SP_FLAG/SP_FLAG2/B0110/E0122";
			String dbpres=gzbo.getTemplatevo().getString("cbase");
			HashMap ffsjMap=gzbo.getYearMonthCount("01");
			String[] dbarr=StringUtils.split(dbpres, ",");
			
			String pay_flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PAY_FLAG);
			StringBuffer buf=new StringBuffer("");
			StringBuffer sub_buf=new StringBuffer("");
			buf.append("insert into ");
			buf.append(gzbo.getGz_tablename());
			boolean isSysItem = false;
			String sysItem = ",A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1,";
			if(sysItem.indexOf("," + onlyField.toUpperCase() + ",") > 0){
				buf.append(" (A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1");
				isSysItem = true;
			}
			else
				buf.append(" (A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1,"+onlyField);
			if(gzbo.getManager().length()>0)
			{
				buf.append(",sp_flag2");
				sub_buf.append(",?");
			}
			
			FieldItem aItem=null;
			boolean has_pay_flag =false;//在数据库中是否存在该字段
			for(int i=0;i<headList.size();i++)
			{
				aItem=(FieldItem)headList.get(i);
				if(pay_flag.toLowerCase().equals(aItem.getItemid().toLowerCase())){
					has_pay_flag = true;
				}
				if("M".equalsIgnoreCase(aItem.getItemtype()))
					continue;
				if(aItem.getItemid().equalsIgnoreCase(onlyField))
					continue;
				if(standardGzItemStr.indexOf("/"+aItem.getItemid().toUpperCase()+"/")!=-1&&standardGzItemStr2.indexOf("/"+aItem.getItemid().toUpperCase()+"/")==-1)
				{
					buf.append(","+aItem.getItemid());
					sub_buf.append(",?");
				}
			}
				
			if(StringUtils.isNotBlank(pay_flag) && !has_pay_flag)
			{
				buf.append(",");
				buf.append(pay_flag);
				standardGzItemStr2+=pay_flag.toUpperCase()+"/";
				sub_buf.append(",?");
			}
			
			if(isSysItem)
				buf.append(" ) values (?,?,?,?,?,?,?,?,?,?,?"+sub_buf.toString()+")");
			else
				buf.append(" ) values (?,?,?,?,?,?,?,?,?,?,?,?"+sub_buf.toString()+")");
			String strYm=(String)ffsjMap.get("ym");
			String count=(String)ffsjMap.get("count");
			java.util.Date src_d=DateUtils.getDate(strYm,"yyyy-MM-dd");
			java.sql.Date d=new java.sql.Date(src_d.getTime());
			
			LazyDynaBean abean=null;
			ArrayList insertInfoList=new ArrayList();
			HashMap insertRecordKey=new HashMap();
			for(int i=0;i<newRecordList.size();i++)
			{
				ArrayList tempList=(ArrayList)newRecordList.get(i); 
				String onlyValue=(String)tempList.get(0);
				if(onlyValue.trim().length()==0)
					continue;
				ArrayList list=new ArrayList();
				StringBuffer buf1=new StringBuffer(buf.toString());
				for(int j=0;j<dbarr.length;j++)
				{
						String pre=dbarr[j];
						if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
							continue;
						this.frowset=dao.search("select * from "+pre+"A01 where "+onlyField+"='"+onlyValue+"'");		
						if(this.frowset.next())
						{
						
							list.add(this.frowset.getString("a0100"));
							list.add(this.frowset.getString("a0101")); 
							list.add(this.userView.getUserName());
							list.add(pre.toUpperCase());
							list.add(this.frowset.getString("b0110"));
							list.add(this.frowset.getString("e0122"));
							list.add("01");
							list.add(d);
							list.add(new Integer(count));
							list.add(d);
							
							int acount=1;
							if(maxZ1ByPersonMap.get(onlyValue)!=null)
								acount=Integer.parseInt((String)maxZ1ByPersonMap.get(onlyValue))+1;
							maxZ1ByPersonMap.put(onlyValue, String.valueOf(acount));
							list.add(new Integer(acount));
							if(!isSysItem)
								list.add(onlyValue);
							insertRecordKey.put(pre.toLowerCase()+"|"+this.frowset.getString("a0100")+"|"+strYm+"|"+acount,"1");
							
							if(gzbo.getManager().length()>0)
								list.add("01");
							/*if(pay_flag.length()!=0)
								list.add(this.frowset.getString(pay_flag));*/
							 
							for(int n=0;n<headList.size();n++)
							{
								aItem=(FieldItem)headList.get(n);
								if("M".equalsIgnoreCase(aItem.getItemtype()))
									continue;
								if(aItem.getItemid().equalsIgnoreCase(onlyField))
									continue;
								if(standardGzItemStr.indexOf("/"+aItem.getItemid().toUpperCase()+"/")!=-1&&standardGzItemStr2.indexOf("/"+aItem.getItemid().toUpperCase()+"/")==-1)
								{
									
									if("D".equalsIgnoreCase(aItem.getItemtype()))
									{
										if(tempList.get(n+1)==null)
											list.add(null);
										else
										{
											list.add((java.sql.Date)tempList.get(n+1));
											/*
											java.sql.Date d_t=null; 
											if(((String)tempList.get(n+1)).length()>0)
											{
												java.util.Date src_d_t=DateUtils.getDate((String)tempList.get(n+1),"yyyy-MM-dd"); 
												if(src_d_t!=null)
													d_t=new java.sql.Date(src_d_t.getTime());
											}
											list.add(d_t);*/
										}
									}
									else if("N".equalsIgnoreCase(aItem.getItemtype()))
									{
										if(tempList.get(n+1)==null)
											list.add(null);
										else
										{
											if(tempList.get(n+1) instanceof Double)
												list.add((Double)tempList.get(n+1));
											else if(tempList.get(n+1) instanceof Integer)
												list.add((Integer)tempList.get(n+1)); 
											else if(tempList.get(n+1) instanceof String)
												list.add(new Double((String)tempList.get(n+1)));
										}
										
									}
									else if("A".equalsIgnoreCase(aItem.getItemtype()))
									{
										if(tempList.get(n+1)==null)
											list.add(null);
										else
										{
											list.add((String)tempList.get(n+1));
										}
									} 
								} 
							}
							
							DbWizard dw = new DbWizard(this.getFrameconn());
							if (StringUtils.isNotBlank(pay_flag) && !has_pay_flag){
								if(dw.isExistField(pre + "A01", pay_flag, false))
									list.add(this.frowset.getString(pay_flag));
								else list.add(null);
							}
							
							insertInfoList.add(list);	
							break;
						}		
				}
			
			}
			num=insertInfoList.size();
			dao.batchInsert(buf.toString(), insertInfoList);
			
			//导入时删除不在条件范围中的人员
			int deleNum=gzbo.delNoConditionData3(gzbo.getGz_tablename(),insertRecordKey);
			num=num-deleNum;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return num;
	}
	
	/**
	 * 生成提示excel
	 * 
	 * @throws GeneralException
	 */
	public String generateErrorFile(ArrayList onlyValueRepeat, Sheet sheet1, FormFile form_file, int okCount) throws GeneralException
	{
		String errorFileName = form_file.getFileName().substring(0, form_file.getFileName().length() - 4) + "_错误提示.xls";
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		try
		{
			HSSFSheet sheet2 = wb.createSheet();
			HSSFRow row2 = sheet2.createRow(0);

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

			Row row1 = sheet1.getRow(0);
			int cols = row1.getPhysicalNumberOfCells();

			HSSFCell cell2 = row2.createCell(0);
			cell2.setCellValue("成功导入" + okCount + "条。");

			HSSFComment comm = null;
			HSSFPatriarch patr = sheet2.createDrawingPatriarch();
			row2 = sheet2.createRow(1);
			if (row1 != null)
			{
				int titleCount = 0;
				for (int i = 0; i < cols; i++)
				{
					Cell cell = row1.getCell(i);
					if (cell != null)
					{
						cell2 = row2.createCell(i);
						cell2.setCellValue(cell.getStringCellValue());
						cell2.setCellStyle(style2);
						comm = patr.createComment(new HSSFClientAnchor(1, 1, 1, 2, (short) (i + 1), 0, (short) (i + 2), 2));
						comm.setString(new HSSFRichTextString(cell.getCellComment().getString().getString()));
						cell2.setCellComment(comm);
						titleCount++;
					}
				}
				cols = titleCount;
				ExportExcelUtil.mergeCell(sheet2, 0, 0, 0, cols - 1);
			}

			int rowIndex = 2;
			for (int i = 0; i < onlyValueRepeat.size(); i++)
			{
				String temp = (String) onlyValueRepeat.get(i);
				row2 = sheet2.createRow(rowIndex++);

				row1 = sheet1.getRow(Integer.parseInt(temp));
				for (int k = 0; k < cols; k++)
				{
					Cell cell = row1.getCell(k);
					if (cell != null)
					{
						cell2 = row2.createCell(k);
						switch (cell.getCellType())
						{
						case Cell.CELL_TYPE_NUMERIC:
							cell2.setCellValue(cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							cell2.setCellValue(cell.getStringCellValue());
							break;
						}
						// cell2.setCellStyle(cell.getCellStyle());
					}
				}
			}

			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + errorFileName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(wb);
		}
		return errorFileName;
	}

	public String getDatePart(String mydate, String datepart)
	{

		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
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
	
	
	
	public void synTempData(String whl_str,String update_str,String salaryid)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql2=new StringBuffer("select distinct userflag from salaryhistory where 1=1 ");
			sql2.append(whl_str);
			RowSet rowSet=dao.search(sql2.toString());
			while(rowSet.next())
			{
				String userflag=rowSet.getString("userflag");
				if(userflag==null||userflag.trim().length()==0)
					continue; 
				String primitiveDataTable=userflag+"_salary_"+salaryid;
				
				StringBuffer sql0=new StringBuffer("");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					
					String[] temps=update_str.split(",");
					StringBuffer str1=new StringBuffer("");
					StringBuffer str2=new StringBuffer("");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].trim().length()>0)
						{
							str1.append(","+primitiveDataTable+"."+temps[i]);
							str2.append(",salaryhistory."+temps[i]);
						}
					}
					
					sql0.append("update "+primitiveDataTable+" set ("+str1.substring(1)+")=(select  "+str2.substring(1)+"  from salaryhistory where ");
					sql0.append("  salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1 "); 
					sql0.append("  "+whl_str+" ) where exists (select null  from salaryhistory where ");
					sql0.append(" salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1  "+whl_str+" )"); 
				}
				else
				{
					String[] temps=update_str.split(",");
					StringBuffer str1=new StringBuffer("");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].trim().length()>0)
						{
							str1.append(","+primitiveDataTable+"."+temps[i]+"=salaryhistory."+temps[i]);
						}
					}
					
					sql0.append("update  "+primitiveDataTable+"   set  "+str1.substring(1)); 
					sql0.append(" from   salaryhistory");
					sql0.append(" where  salaryhistory.a0100="+primitiveDataTable+".a0100 and ");
					sql0.append(" upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) ");
					sql0.append(" and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and ");
					sql0.append(" salaryhistory.a00z1="+primitiveDataTable+".a00z1   "+whl_str);
					
				}
				dao.update(sql0.toString());
			} 
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 如果排序指标是否有效
	 * @param sort_str
	 * @param gzbo
	 * @return
	 */
	private boolean isExistErrorItem(String sort_str,SalaryTemplateBo gzbo)
	{
		boolean flag=true;
		String[] temps=sort_str.toUpperCase().split(",");
		String zgItemStr=gzbo.getStandardGzItemStr();
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].length()>0)
			{
				String _str=temps[i].replaceAll("ASC", "");
				_str=_str.replaceAll("DESC", "");
				_str=_str.trim();
				if(DataDictionary.getFieldItem(_str.toLowerCase())!=null&&zgItemStr.indexOf(_str+"/")==-1)
				{
					flag=false;
					break;
				}
			}
			
			
		}
		return flag;
	}
	
	/**
	 * @Title: insertTempTable 
	 * @Description: 插入新数据 
	 * @param dao
	 * @param updateSql 更新插入的数据sql
	 * @param onlyField 唯一性指标
	 * @param wyNewRecordList 更新数据集合
	 * @param gzbo
	 * @param maxZ1ByPersonMap 最大归属次数map集合
	 * @return int
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-10-9 下午01:55:54
	 */
	private HashMap insertTempTable(ContentDAO dao,String updateSql,String onlyField,ArrayList wyNewRecordList,SalaryTemplateBo gzbo,HashMap maxZ1ByPersonMap) throws GeneralException{
		int num=0;
		HashMap returnData = new HashMap();
		try
		{
			String tableName = "t#"+this.userView.getUserName()+"_inst";
			//创建临时表
			gzbo.createImportTable(tableName);
			
			String dbpres=gzbo.getTemplatevo().getString("cbase");
			HashMap ffsjMap=gzbo.getYearMonthCount("01");
			String[] dbarr=StringUtils.split(dbpres, ",");
			
			//向临时表中插入数据的slq语句
			StringBuffer buf=new StringBuffer("");
			buf.append("insert into ");
			buf.append(tableName);
			buf.append("(NBASE,A0100,A0101,A00Z0,A00Z1,B0110,E0122,dbid,A0000) values (?,?,?,?,?,?,?,?,?)");
			
			FieldItem aItem=null;
			String strYm=(String)ffsjMap.get("ym");
			String count=(String)ffsjMap.get("count");
			java.util.Date src_d=DateUtils.getDate(strYm,"yyyy-MM-dd");
			java.sql.Date d=new java.sql.Date(src_d.getTime());
			
			LazyDynaBean abean = null;
			ArrayList insertInfoList = new ArrayList();
			HashMap errorOnlyField = new HashMap();
			HashMap allOnlyFiel = new HashMap();
			HashMap updateRecordKey = new HashMap();
			Map insertRecordKey = new HashMap();
			for(int i = 0;i<wyNewRecordList.size();i++)
			{
				ArrayList tempList = (ArrayList)wyNewRecordList.get(i); 
				String onlyValue = (String)tempList.get(0);
				boolean isInDb = false;//当前人是否在薪资权限人员库中存在
				tempList.remove(0);
				if(onlyValue.trim().length() == 0)
					continue;
				
				for(int j = 0;j<dbarr.length;j++)
				{
						ArrayList list=new ArrayList();
						String pre=dbarr[j];
						if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
							continue;
						this.frowset=dao.search("select * from "+pre+"A01 where "+onlyField+"='"+onlyValue+"'");		
						if(this.frowset.next())
						{
							int dbid = 0;
							String dbidSql = "select dbid from dbname where upper(dbname.pre)='"+pre+"'";
							RowSet rowset = dao.search(dbidSql);
							if(rowset.next())
								dbid = rowset.getInt("dbid");
							list.add(pre.toUpperCase());
							list.add(this.frowset.getString("a0100"));
							list.add(this.frowset.getString("a0101")); 
							list.add(d);
							
							int acount=1;
							if(maxZ1ByPersonMap.get(onlyValue)!=null)
								acount=Integer.parseInt((String)maxZ1ByPersonMap.get(onlyValue))+1;
							maxZ1ByPersonMap.put(onlyValue, String.valueOf(acount));
							list.add(new Integer(acount));
							
							list.add(this.frowset.getString("b0110"));
							list.add(this.frowset.getString("e0122"));
							list.add(dbid);
							list.add(this.frowset.getString("a0000"));
							
							tempList.add(this.frowset.getString("a0100"));
							tempList.add(pre.toUpperCase());
							tempList.add(d);
							tempList.add(new Integer(acount));
							
							insertInfoList.add(list);	
							
							String key = pre.toLowerCase()+"|"+this.frowset.getString("a0100")+"|"+strYm+"|"+acount;
							allOnlyFiel.put(key, onlyValue);
							updateRecordKey.put(key,tempList);
							insertRecordKey.put(key,list);
							isInDb = true;
							break;
						}
				}
				if(!isInDb){
					allOnlyFiel.put(i, onlyValue);
				}
			
			}
			
			num = insertInfoList.size();
			if(num > 0)
				dao.batchInsert(buf.toString(), insertInfoList);
			
			int deleNum=gzbo.delNoConditionData3(tableName,updateRecordKey);
			
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(gzbo.getGz_tablename());
			buf.append("(NBASE,A0100,A0101,A00Z0,A00Z1,B0110,E0122,dbid,A0000,A00Z2,A00Z3,sp_flag,userflag) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			Map insertRecordTemp=new HashMap();
			
			Iterator iter = updateRecordKey.entrySet().iterator();
			insertInfoList.clear();
			ArrayList updateInfoList = new ArrayList();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey();
				ArrayList val = (ArrayList)entry.getValue();
				ArrayList list=new ArrayList();
				list = (ArrayList)insertRecordKey.get(key);
				list.add(d);
				list.add(new Integer(count));
				list.add("01");
				
				list.add(this.userView.getUserName());
				updateInfoList.add(val);
				insertInfoList.add(list);
				
				//取得不在权限范围内的人集合
				if(allOnlyFiel.containsKey(key))
					allOnlyFiel.remove(key);
			}
			
			
			//将临时表中数据插入到新增表中
			if(insertInfoList.size() > 0){
				dao.batchInsert(buf.toString(), insertInfoList);
			}

			//取得不在权限范围内的人集合
			Iterator iter2 = allOnlyFiel.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				String val = (String)entry.getValue();
				
				errorOnlyField.put(val, "");
			}
			
			num=num-deleNum;
			if(insertInfoList.size() > 0){
				//导入后计算
				for(int j=0;j<dbarr.length;j++)
				{
					String pre=dbarr[j];
					StringBuffer strWhere = new StringBuffer(" ");
					strWhere.append(" exists (select null from ");
					strWhere.append(tableName);
					strWhere.append(" where ");
					strWhere.append(tableName + ".NBASE=" + gzbo.getGz_tablename() + ".NBASE and ");
					strWhere.append(tableName + ".A00Z0=" + gzbo.getGz_tablename() + ".A00Z0  and ");
					strWhere.append(tableName + ".A00Z1=" + gzbo.getGz_tablename() + ".A00Z1 and ");
					strWhere.append(tableName + ".A0100=" + gzbo.getGz_tablename() + ".A0100 ");
					strWhere.append(")");
					gzbo.firstComputing(strWhere.toString(), pre, true, null);
				}
				
				//计算完后更新数据
				if(updateInfoList.size() > 0){
					dao.batchUpdate(updateSql, updateInfoList);
				}
			}
			
			returnData.put("num", num);
			returnData.put("errorOnlyFieldMap", errorOnlyField);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return returnData;
	}
}
