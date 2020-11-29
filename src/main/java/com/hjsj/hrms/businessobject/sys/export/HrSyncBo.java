package com.hjsj.hrms.businessobject.sys.export;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 *<p>Title:HrSyncBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 24, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
/**
 * SYS_EXPORT_VIEW <root code="0" key_field=""> <base>Oth</base>
 * <fields>a0101,a0107,a0440,a0737</fields>人员指标 <code_fields />人员翻译指标
 * <org_fields>b0110,b0405,b0705</org_fields>机构指标 <org_code_fields />机构翻译指标 <a
 * name='a0101'>xxxx</a>自定义指标 <a name='a0107'>xxxx</a> <b name='b0110'>xxxx</b>
 * <b name='b0405'>xxxx</b> </root>
 */
public class HrSyncBo {

	public final static int BASE = 1; // #选择的库名
	public final static int FIELDS = 2; // #指标列表
	public final static int CODE = 3; // #
	public final static int KEY_FIELd = 4; // #
	public final static int CODE_FIELDS = 5;// #翻译指标
	public final static int ORG_FIELDS = 6;// #机构指标
	public final static int ORG_CODE_FIELDS = 7;// #机构翻译指标
	public final static int POST_FIELDS = 13;
	public final static int POST_CODE_FIELDS = 14;

	public final static int A = 8;// #人员指定指标
	public final static int B = 9;// #机构指定指标
	public final static int K = 16;// #岗位指定指标
	public final static int SYNC_A01 = 10;// 同步人员
	public final static int SYNC_B01 = 11;// 同步单位
	public final static int SYNC_K01 = 15;// 同步单位
	public final static int HR_ONLY_FIELD = 12;// 唯一性指标
	public final static int SYNC_MODE = 17;// 同步方式
	public final static int FAIL_LIMIT = 18;// 同步失败次数
	public final static int JZ_FIELD=19;//同步兼职
	public final static int photo = 22;// 同步照片
	public final static int FIELDCHANGE = 23;// 跟踪指标变化前后信息
	public final static int FIELDANDCODE = 25;// 翻译指标包含代码
	public final static int FIELDANDCODESEQ = 26;// 跟踪指标变化前后信息
	private boolean sync_oper = false;
	private boolean sync_org = false;
	private boolean sync_a01 = false;
	private boolean sync_b01 = false;
	private boolean sync_k01 = false;
	private boolean sync_photo = false;
	private boolean sync_fieldchange = false;
	private int UPDATE_FALG = 2;
	private int DEL_FLAG = 3;
	private int ADD_FLAG = 1;
	/** 数据库连接 */
	private Connection conn;
	private Document doc;
	/** 人员库列表 */
	private ArrayList dblist = new ArrayList();
	/** 指标列表 */
	private HashMap f_map = new HashMap();
	/** 组织机构列表 */
	private HashMap o_map = new HashMap();
	/** 组织岗位列表 */
	private HashMap k_map = new HashMap();
	/**
	 * 代码翻译标识 ＝为真不用进行代码翻译，否则需要进行代码转换
	 */
	private boolean bcode = true;

//	private String xmlcontent;

//	private HashMap fieldsMap;
	private String codefields;
	private String orgcodefields;
	private String postcodefields;
	private String photoFields;
	private int nlevel = 0;
	private String dept_table_name = "dept_table";
	private String t_hrSync_key_tab = "t_hrSync_key_tab";
	
	private String fieldAndCode;
	private String fieldAndCodeSeq;


	public String getFieldAndCode() {
		return fieldAndCode;
	}

	public void setFieldAndCode(String fieldAndCode) {
		this.fieldAndCode = fieldAndCode;
	}

	public String getFieldAndCodeSeq() {
		return fieldAndCodeSeq;
	}

	public void setFieldAndCodeSeq(String fieldAndCodeSeq) {
		this.fieldAndCodeSeq = fieldAndCodeSeq;
	}

	public String getCodefields() {
		return codefields;
	}

	public void setCodefields(String codefields) {
		this.codefields = codefields;
	}

	public HrSyncBo(Connection conn) {
		this.conn = conn;
		init();
	}

	public synchronized void HrSync(String dbname, String fieldstr) {
		ContentDAO dao = new ContentDAO(this.conn);
		this.delete(dao);
		ArrayList dblist = new ArrayList();
		if (!(dbname == null || "".equals(dbname))) {
			dblist = this.getHrDB(dbname);
		} else {
			dblist.add("usr");
		}
		this.insertRecord(dao, dblist);
		this.getDbnames(dbname, fieldstr, dao);
	}

	public void delete(ContentDAO dao) {
		StringBuffer sb = new StringBuffer();
		sb.append(" delete t_hr_view ");
		try {
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateHr(String dbname, String fieldstr, ContentDAO dao) {
		StringBuffer sb = new StringBuffer();
		FieldItem fi = DataDictionary.getFieldItem(fieldstr);
		if (!(fi == null || "a01".equalsIgnoreCase(fi.getFieldsetid())))// 不是主集指标
		{
			String table = fi.getFieldsetid();
			sb.append(" update t_hr_view ");
			sb.append(" set " + fieldstr + "= ");
			sb.append(" (select " + fieldstr + " ");
			sb.append(" from " + dbname + table + " ");
			sb.append(" where a0100=t_hr_view.a0100");
			sb.append(" and i9999=");
			sb.append(" (select max(i9999) ");
			sb.append(" from " + dbname + table + " ");
			sb.append(" where a0100=t_hr_view.a0100");
			sb.append(" ))");
			sb.append(" ");
			sb.append(" where nbase_0 like '" + dbname + "'");
		} else {
			sb.append(" update t_hr_view ");
			sb.append(" set " + fieldstr + "= ");
			sb.append(" (select " + fieldstr + " ");
			sb.append(" from " + dbname + "a01 ");
			sb.append(" where a0100=t_hr_view.a0100)");
			sb.append(" where nbase_0 like '" + dbname + "'");
		}
		try {
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getDbnames(String dbnamestr, String fieldstr, ContentDAO dao) {
		if (!(dbnamestr == null || "".equals(dbnamestr))) {
			String[] dbname = dbnamestr.split(",");
			for (int i = 0; i < dbname.length; i++) {
				this.getFields(dbname[i], fieldstr, dao);
			}
		} else {
			this.getFields("usr", fieldstr, dao);
		}

	}

	public void getFields(String dbname, String fieldstr, ContentDAO dao) {
		if (!(fieldstr == null || "".equals(fieldstr))) {
			String[] fields = fieldstr.split(",");
			for (int i = 0; i < fields.length; i++) {
				if (!("a0100".equalsIgnoreCase(fields[i])
						|| "b0110".equalsIgnoreCase(fields[i])
						|| "e0122".equalsIgnoreCase(fields[i])
						|| "a0101".equalsIgnoreCase(fields[i])
						|| "e01A1".equalsIgnoreCase(fields[i])
						|| "username".equalsIgnoreCase(fields[i])
						|| "userpassword".equalsIgnoreCase(fields[i]) || "flag"
						.equalsIgnoreCase(fields[i]))) {
					this.updateHr(dbname, fields[i], dao);
				}
			}
		}
	}

	public ArrayList getHrDB(String str) {
		ArrayList list = new ArrayList();
		String[] db = str.split(",");
		for (int i = 0; i < db.length; i++) {
			if (!(db[i] == null || "".equals(db[i]))) {
				list.add(db[i]);
			}
		}
		return list;
	}

	public boolean insertRecord(ContentDAO dao, ArrayList dblist) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		String dbname = "";
		try {
			for (int i = 0; i < dblist.size(); i++) {
				dbname = (String) dblist.get(i);
				String hz_dbname = AdminCode.getCodeName("@@", dbname);
				sql.append("insert into t_hr_view");
				sql.append("(nbase,nbase_0,A0100,");
				sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
				sql.append("username,userpassword,flag)");
				sql.append("( select '" + hz_dbname + "','" + dbname + "',");
				sql.append(" A0100,B0110,E0122,A0101,E01A1,");
				sql.append("username,userpassword," + this.ADD_FLAG + " from "
						+ dbname + "a01)");
				sql.append(" ");
				// System.out.println(sql.toString());
				dao.update(sql.toString());
				sql.delete(0, sql.length());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 导出Excel
	 * 
	 * @param dbnamestr
	 * @return
	 */
	public String exportExcel(String a_code) {
		ContentDAO dao = new ContentDAO(this.conn);
		String outname = "ExportHrView.xls";
		String filename = outname;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = null;
		FileOutputStream fileOut = null;
		try {
			sheet = workbook.createSheet();
			HSSFRow row = null;
			HSSFCell csCell = null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT);
			// 形成第一行标题
			String[] itemlist = this.getExportFields().split(",");
			int t = 0;
			// 形成SQL语句
			for (int i = 0; i < itemlist.length; i++) {
				String itemdesc = "";
				if ("username".equalsIgnoreCase(itemlist[i])) {
                    itemdesc = "用户名";
                } else if ("userpassword".equalsIgnoreCase(itemlist[i])) {
                    itemdesc = "密码";
                } else if ("nbase".equalsIgnoreCase(itemlist[i])) {
                    itemdesc = "人员类别";
                } else {
					FieldItem fi = DataDictionary.getFieldItem(itemlist[i]);
					if (fi != null) {
						itemdesc = fi.getItemdesc();
					}
				}
				row = sheet.createRow((short) 0);
				csCell = row.createCell(t);
				// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(itemdesc);
				csCell.setCellStyle(style);
				t++;
			}
			String sql = this.getExportSql();

			RowSet rset = dao.search(sql);
			int n = 1;

			while (rset.next()) {
				int m = 0;
				row = sheet.createRow((short) n);
				for (int i = 0; i < itemlist.length; i++) {
					String desc = "";
					String fieldesc = "";
					if ("username".equalsIgnoreCase(itemlist[i])) {
                        desc = rset.getString("username");
                    } else if ("userpassword".equalsIgnoreCase(itemlist[i])) {
                        desc = rset.getString("userpassword");
                    } else if ("nbase".equalsIgnoreCase(itemlist[i])) {
                        desc = rset.getString("nbase");
                    } else {
						FieldItem fi = DataDictionary.getFieldItem(itemlist[i]);
						Field fielditem = fi.cloneField();
						ResultSetMetaData rsetmd = rset.getMetaData();
						fieldesc = getColumStr(rset, rsetmd, fielditem
								.getName());
						fieldesc = fieldesc != null ? fieldesc : "";
						if (fielditem.isCode() && bcode) {
							if ("b0110".equalsIgnoreCase(fi.getItemid())) {
                                desc = AdminCode.getCodeName("UN", fieldesc);
                            } else if ("e0122".equalsIgnoreCase(fi.getItemid())) {
                                desc = AdminCode.getCodeName("UM", fieldesc);
                            } else if ("e01a1".equalsIgnoreCase(fi.getItemid())) {
                                desc = AdminCode.getCodeName("@K", fieldesc);
                            } else {
                                desc = AdminCode.getCodeName(fi.getCodesetid(),
                                        fieldesc);
                            }
						} else {
							int type = fielditem.getDatatype();
							if (type == 6) {
								if (fieldesc != null
										&& fieldesc.trim().length() > 0) {
									desc = getFloat(fieldesc, fielditem
											.getDecimalDigits());
								} else {
									desc = "";
								}
							} else if (type == 4) {
								if (fieldesc != null
										&& fieldesc.trim().length() > 0) {
									desc = Math.round(Float
											.parseFloat(fieldesc))
											+ "";
									if ("0".equals(desc)) {
										desc = "";
									}
								} else {
									desc = "";
								}
							} else {
								desc = fieldesc;
							}
						}
					}

					csCell = row.createCell(m);
					// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					csCell.setCellValue(desc);
					csCell.setCellStyle(style);
					m++;
				}
				n++;
			}

			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outname);
			workbook.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return filename;

	}

	/**
	 * 定义的指标列表
	 * 
	 * @return
	 */
	public HashMap getFieldsMap() {
		HashMap ret = new HashMap();
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		String[] fields = t_fields.split(",");
		for (int i = 0; i < fields.length; i++) {
			if (!(fields[i] == null || "".equals(fields[i]))) {
				ret.put(fields[i].toLowerCase(), fields[i].toLowerCase());
			}
		}
		return ret;
	}

	public HashMap getCustomFieldsMap(int type) {
		HashMap ret = new HashMap();
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		String[] fields = t_fields.split(",");
		for (int i = 0; i < fields.length; i++) {
			String value = this.getAppAttributeValue(type, fields[i]);
			ret.put(value, value);
		}
		return ret;
	}

	public HashMap getOrgFieldsMap() {
		HashMap ret = new HashMap();
		String t_fields = this.getTextValue(HrSyncBo.ORG_FIELDS);
		String[] fields = t_fields.split(",");
		for (int i = 0; i < fields.length; i++) {
			if (!(fields[i] == null || "".equals(fields[i]))) {
				ret.put(fields[i].toLowerCase(), fields[i].toLowerCase());
			}
		}
		return ret;
	}

	public HashMap getPostFieldsMap() {
		HashMap ret = new HashMap();
		String t_fields = this.getTextValue(HrSyncBo.POST_FIELDS);
		String[] fields = t_fields.split(",");
		for (int i = 0; i < fields.length; i++) {
			if (!(fields[i] == null || "".equals(fields[i]))) {
				ret.put(fields[i].toLowerCase(), fields[i].toLowerCase());
			}
		}
		return ret;
	}

	/**
	 * 获得要保存的字段
	 * 
	 * @param code_fields
	 * @return
	 */
	public String getSaveFieldsStr(ArrayList code_fields) {
		StringBuffer buf = new StringBuffer();
		if (code_fields == null || code_fields.size() <= 0) {
            buf.append("");
        } else {
			for (int i = 0; i < code_fields.size(); i++) {
				buf.append("" + code_fields.get(i).toString() + ",");
			}
			buf.setLength(buf.length() - 1);
		}

		return buf.toString();
	}

	/**
	 * 获得要保存的字段
	 * 
	 * @param code_fields
	 * @return
	 */
	public HashMap getSaveFieldsMap(ArrayList code_fields) {
		HashMap ret = new HashMap();
		StringBuffer buf = new StringBuffer();
		if (code_fields == null || code_fields.size() <= 0) {
            buf.append("");
        } else {
			for (int i = 0; i < code_fields.size(); i++) {
				buf.append("" + code_fields.get(i).toString() + ",");
			}
			buf.setLength(buf.length() - 1);
		}
		String[] fields = buf.toString().split(",");
		for (int i = 0; i < fields.length; i++) {
			if (!(fields[i] == null || "".equals(fields[i]))) {
				ret.put(fields[i], fields[i]);
			}
		}
		return ret;
	}

	/**
	 * 获取关联更新指标
	 */
	public ArrayList searcSyncFields() {
		ArrayList temp = new ArrayList();
		ArrayList sync_fieldlist = new ArrayList();
		try {

			temp = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
			for (int i = 0; i < temp.size(); i++) {
				FieldItem fi = (FieldItem) temp.get(i);
				if ("A".equalsIgnoreCase(fi.getItemtype())
						&& ("0".equals(fi.getCodesetid()))) {
					CommonData data = new CommonData();
					data.setDataValue(fi.getItemid());
					data.setDataName(fi.getItemdesc());
					sync_fieldlist.add(data);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sync_fieldlist;
	}

	public String getDBMess(String dbpre) {
		ResultSet rs=null;
		String[] pres = dbpre.trim().split(",");
		StringBuffer dbpres = new StringBuffer();
		ArrayList dblist = new ArrayList();
		String sql = "select dbname,pre from dbname";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next()) {
				RecordVo vo = new RecordVo("dbname");
				vo.setString("pre", rs.getString("pre"));
				vo.setString("dbname", rs.getString("dbname"));
				dblist.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		for (int i = 0; i < pres.length; i++) {
			for (int j = 0; j < dblist.size(); j++) {
				RecordVo db = (RecordVo) dblist.get(j);
				if (pres[i].equalsIgnoreCase(db.getString("pre").toString())) {
                    dbpres.append(db.getString("dbname").toString());
                }
			}
			if ((i + 1) % 3 == 0) {
                dbpres.append("<br>");
            } else {
                dbpres.append(",");
            }
		}
		dbpres.setLength(dbpres.length() - 1);
		return dbpres.toString();
	}

	public String getMess(ArrayList list) {
		StringBuffer mess = new StringBuffer();
		if (list == null || list.size() <= 0) {
            return "";
        }
		int r = 1;
		for (int i = 0; i < list.size(); i++) {
			CommonData dataobj = (CommonData) list.get(i);
			mess.append(dataobj.getDataName());
			if (r % 5 == 0) {
                mess.append("<br>");
            } else {
                mess.append(",");
            }
			r++;
		}
		return mess.toString().substring(0,mess.length()-1);//去掉最后逗号
	}

	/**
	 * 得到指标
	 * 
	 * @param field
	 * @return
	 */
	public ArrayList getFields(String field) {
		ArrayList list = new ArrayList();
		if (field == null || field.length() <= 0) {
            return list;
        }
		String[] fields = field.split(",");
		if (fields == null || fields.length <= 0) {
            return list;
        }
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "";
			for (int i = 0; i < fields.length; i++) {

				String itemid = fields[i];
				if (itemid != null && "b0110".equals(itemid)) {
					CommonData data = new CommonData();
					data.setDataName("单位名称(" + itemid + ")");
					data.setDataValue(itemid);
					list.add(data);
				}
				if (itemid != null && "e01a1".equals(itemid)) {
					CommonData data = new CommonData();
					data.setDataName("岗位名称(" + itemid + ")");
					data.setDataValue(itemid);
					list.add(data);
				}
				sql = "select itemdesc from fielditem where Upper(itemid)='"
						+ itemid.toUpperCase() + "'";
				RowSet rs = dao.search(sql);
				if (rs.next()) {
					CommonData data = new CommonData();
					data.setDataName(rs.getString("itemdesc") + "(" + itemid
							+ ")");
					data.setDataValue(itemid);
					list.add(data);
				}
			}
		} catch (Exception e) {

		}
		return list;
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	public ArrayList getSimpleFields(String field) {
		String[] fields = field.split(",");
		ArrayList list = new ArrayList();
		if (fields == null || fields.length <= 0) {
            return list;
        }
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "";
			for (int i = 0; i < fields.length; i++) {

				String itemid = fields[i];
				if (itemid != null && "b0110".equals(itemid)) {
					CommonData data = new CommonData();
					data.setDataName("单位名称");
					data.setDataValue(itemid);
					list.add(data);
				}
				if (itemid != null && "e01a1".equals(itemid)) {
					CommonData data = new CommonData();
					data.setDataName("岗位名称");
					data.setDataValue(itemid);
					list.add(data);
				}
				sql = "select itemdesc from fielditem where Upper(itemid)='"
						+ itemid.toUpperCase() + "'";
				RowSet rs = dao.search(sql);
				if (rs.next()) {
					CommonData data = new CommonData();
					data.setDataName(rs.getString("itemdesc"));
					data.setDataValue(itemid);
					list.add(data);
				}
			}
		} catch (Exception e) {

		}
		return list;
	}

	/**
	 * 保存XML
	 * 
	 * @param dao
	 */
	public void saveParameter(ContentDAO dao) {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String updatestr = outputter.outputString(doc);
		// System.out.println(retstr);
		ArrayList list = new ArrayList();
		list.add(updatestr);
		String sql = " update constant set str_value = ? where constant = 'SYS_EXPORT_VIEW'";
		try {
			dao.update(sql, list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 设置节点属性
	 * 
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setAttributeValue(int param_type, String value) {
		boolean bflag = true;
		String name = getElementName(param_type);
		if (value == null) {
            value = "";
        }
		if (!"".equals(name)) {
			try {
				String str_path = "/root";
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				if (childlist.size() > 0) {
					element = (Element) childlist.get(0);
					if ("code".equalsIgnoreCase(name)) {
						element.setAttribute("code", value);
					} else if ("sync_a01".equalsIgnoreCase(name)) {
						element.setAttribute("sync_a01", value);
					} else if ("sync_b01".equalsIgnoreCase(name)) {
						element.setAttribute("sync_b01", value);
					} else if ("hr_only_field".equalsIgnoreCase(name)) {
						element.setAttribute("hr_only_field", value);
					} else if ("sync_mode".equalsIgnoreCase(name)) {
						element.setAttribute("sync_mode", value);
					} else if ("fail_limit".equalsIgnoreCase(name)) {
						element.setAttribute("fail_limit", value);
					} else if("jz_field".equalsIgnoreCase(name)){
						element.setAttribute("jz_field", value);
					} else if ("photo".equalsIgnoreCase(name)){
						element.setAttribute("photo", value);
					} else if ("fieldChange".equalsIgnoreCase(name)){
						element.setAttribute("fieldChange",value);
					} else if ("fieldAndCodeSeq".equals(name)) {
						element.setAttribute("fieldAndCodeSeq",value);
					} else if ("fieldAndCode".equals(name)) {
						element.setAttribute("fieldAndCode",value);
					}else {
						element.setAttribute("key_field", value);
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
				bflag = false;
			}
		}
		return bflag;
	}

	/**
	 * 获得节点属性
	 * 
	 * @param param_type
	 * @param value
	 * @return
	 */
	public String getAttributeValue(int param_type) {
		String ret = "";
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root";
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				if (childlist.size() > 0) {
					element = (Element) childlist.get(0);
					if ("code".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("code");
					} else if ("sync_a01".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("sync_a01");
					} else if ("sync_b01".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("sync_b01");
					} else if ("hr_only_field".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("hr_only_field");
					} else if ("sync_mode".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("sync_mode");
					} else if ("fail_limit".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("fail_limit");
					} else if ("jz_field".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("jz_field");
					} else if ("photo".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("photo");
					}else if ("fieldChange".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("fieldChange");
					}else if ("fieldAndCode".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("fieldAndCode");
					}else if ("fieldAndCodeSeq".equalsIgnoreCase(name)) {
						ret = element.getAttributeValue("fieldAndCodeSeq");
					}else {
						ret = element.getAttributeValue("key_field");
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
		return ret;
	}

	/**
	 * 设置节点内容
	 * 
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setTextValue(int param_type, String value) {
		boolean bflag = true;
		String name = getElementName(param_type);
		if (value == null) {
            value = "";
        }
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				if (childlist.size() == 0) {
					element = new Element(name);
					element.setText(value);
					this.doc.getRootElement().addContent(element);
				} else {
					element = (Element) childlist.get(0);
					element.setText(value);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				bflag = false;
			}
		}
		return bflag;
	}

	/**
	 * 获得节点内容
	 * 
	 * @param param_type
	 * @param value
	 * @return
	 */
	public String getTextValue(int param_type) {
		String ret = "";
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				if (childlist.size() != 0) {
					element = (Element) childlist.get(0);
					ret = element.getText();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * 根据xml路径获取节点
	 * @param path
	 * @return
	 */
	public List getNodeList(String path) {
		List list = new ArrayList();
		try {
			XPath xpath = XPath.newInstance(path);
			List childlist = xpath.selectNodes(this.doc);
			if (childlist != null) {
				list.addAll(childlist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 设置指定对应关系的
	 * 
	 * @param param_type
	 * @param attribute
	 * @param value
	 * @return
	 */
	public boolean setAppAttributeValue(int param_type, String attribute,
			String value) {
		boolean bflag = true;
		String name = getElementName(param_type);
		if (value == null) {
            value = "";
        }
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				if (childlist.size() == 0) {
					element = new Element(name);
					element.setAttribute("name", attribute);
					element.setText(value);
					this.doc.getRootElement().addContent(element);
				} else {
					boolean b = true;
					String str = "";
					int index = 0;
					for (int i = 0; i < childlist.size(); i++) {
						element = (Element) childlist.get(i);
						str = element.getAttributeValue("name");
						if (attribute.equalsIgnoreCase(str)) {
							b = false;
							index = i;
							break;
						}
					}
					if (b) {
						element = new Element(name);
						element.setAttribute("name", attribute);
						element.setText(value);
						this.doc.getRootElement().addContent(element);
					} else {
						element = (Element) childlist.get(index);
						element.setAttribute("name", attribute);
						element.setText(value);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				bflag = false;
			}
		}
		return bflag;
	}

	/**
	 * 通过传入我们库指标，得到用户自定义指标
	 * 
	 * @param param_type
	 * @param attribute
	 * @return
	 */
	public String getAppAttributeValue(int param_type, String attribute) {
		String ret = "";
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				for (int i = 0; i < childlist.size(); i++) {
					element = (Element) childlist.get(i);
					if (attribute.equalsIgnoreCase(element
							.getAttributeValue("name"))) {
                        ret = element.getText();
                    }
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
		return ret;
	}

	/**
	 * 通过传入用户自定义指标得到对应我们库的指标
	 * 
	 * @param param_type
	 * @param attribute
	 * @return
	 */
	public String getAppAttribute(int param_type, String attribute) {
		String ret = "";
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				for (int i = 0; i < childlist.size(); i++) {
					element = (Element) childlist.get(i);
					if (attribute.equalsIgnoreCase(element.getText())) {
                        ret = element.getAttributeValue("name");
                    }
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
		return ret;
	}

	public String delAppAttributeValue(int param_type, String attribute) {
		String ret = "";
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				str_path = "/root";
				xpath = XPath.newInstance(str_path);
				List rootlist = xpath.selectNodes(this.doc);
				Element relement = (Element) rootlist.get(0);
				Element element = null;
				for (int i = 0; i < childlist.size(); i++) {
					element = (Element) childlist.get(i);
					if (attribute.equalsIgnoreCase(element
							.getAttributeValue("name"))) {
                        relement.removeContent(element);
                    }
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
		return ret;
	}

	public HashMap getAppList(int param_type) {
		HashMap map = new HashMap();

		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Element element = null;
				for (int i = 0; i < childlist.size(); i++) {
					element = (Element) childlist.get(i);
					map.put(element.getAttributeValue("name"), element
							.getText());
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}

		}
		return map;
	}

	public ArrayList getAppFieldList(int param_type) {
		ArrayList list = new ArrayList();
		String name = getElementName(param_type);
		if (!"".equals(name)) {
			try {
				String str_path = "/root/" + name;
				String field_path = "" ;
				if("a".equalsIgnoreCase(name)){
					field_path = "/root/fields";
				}else if("b".equalsIgnoreCase(name)){
					field_path = "/root/org_fields";
				}else if("k".equalsIgnoreCase(name)){
					field_path = "/root/post_fields";
				}
				Element ele = (Element)XPath.newInstance(field_path).selectSingleNode(this.doc);
				String [] fieldArr =  ele.getText().split(",");
				XPath xpath = XPath.newInstance(str_path);
				List childlist = xpath.selectNodes(this.doc);
				Map<String,String> itemMap = new HashMap<String,String>();
				Element element = null;
				for (int i = 0; i < childlist.size(); i++) {
					element = (Element) childlist.get(i);
					itemMap.put(element.getAttributeValue("name"),element.getText());
				}
				//【60925】系统管理：数据视图中调整指标顺序后，界面指标顺序显示不一致
				for(String fieldStr :fieldArr){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("name", fieldStr);
					bean.set("text", itemMap.get(fieldStr));
					list.add(bean);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}

		}
		return list;
	}

	/**
	 * 获得Document对象
	 * 
	 * @return Document
	 */
	public Document getDoc() {
		Document doc = null;
		try {
			RecordVo ctrlvo = ConstantParamter.getRealConstantVo(
					"SYS_EXPORT_VIEW", conn);
			if (!(ctrlvo == null || "".equals(ctrlvo.getString("str_value")))) {
				doc = PubFunc.generateDom(ctrlvo.getString("str_value"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 创建新XML
	 */
	public void createXml() {

		ContentDAO dao = new ContentDAO(this.conn);
		String xmlstr = "";
		Element root = new Element("root");
		root.setAttribute("code", "0");
		root.setAttribute("key_field", "A0101");
		Element base = new Element("base");
		// base.setText("usr");
		Element fields = new Element("fields");
		// fields.setText("a0101");
		// ArrayList fieldlist = new ArrayList();
		root.addContent(base);
		root.addContent(fields);
		Document t_doc = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		xmlstr = outputter.outputString(t_doc);
		String sql = "update constant set str_value = '" + xmlstr
				+ "' where constant like 'SYS_EXPORT_VIEW' ";
		try {
			dao.update(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 查询常量表中的SYS_EXPORT_VIEW
	 * 
	 * @return RecordVo
	 */
	public RecordVo getRecordVo() {
		RecordVo ctrlvo = null;
		try {
			ctrlvo = ConstantParamter
					.getRealConstantVo("SYS_EXPORT_VIEW", conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ctrlvo;
	}

	/**
	 * 取得人员基本情况子集的指标
	 * 
	 * @return
	 */
	private String getMainSetFields() {
		StringBuffer buf = new StringBuffer();
		Iterator it = f_map.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String fieldname = (String) entry.getKey();
			FieldItem item = DataDictionary.getFieldItem(fieldname);
			if (item == null) {
                continue;
            }
			if ("0".equalsIgnoreCase(item.getUseflag()))// fzg add
            {
                continue;
            }

			if (item.isMainSet()) {
				if ("a0101"
						.equalsIgnoreCase(this.getAppAttributeValue(HrSyncBo.A, fieldname)))// fzg add
                {
                    continue;
                }

				buf.append(fieldname);
				buf.append(",");
			}
		}
		if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }
		return buf.toString();
	}

	private String getCustomMainSetFiedlds() {
		StringBuffer buf = new StringBuffer();
		Iterator it = f_map.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String fieldname = (String) entry.getKey();
			FieldItem item = DataDictionary.getFieldItem(fieldname);
			if (item == null) {
                continue;
            }
			/*
			 * if(item.getUseflag().equalsIgnoreCase("0")||fieldname.equalsIgnoreCase("a0101"))
			 * continue;
			 */
			if ("0".equalsIgnoreCase(item.getUseflag()))// fzg add
            {
                continue;
            }
			if (item.isMainSet()) {
				if ("a0101"
						.equalsIgnoreCase(this.getAppAttributeValue(HrSyncBo.A, fieldname)))// fzg add
                {
                    continue;
                }

				String valus = this.getAppAttributeValue(HrSyncBo.A, fieldname);
				buf.append(valus);
				buf.append(",");
			}
		}
		if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }
		return buf.toString();
	}

//	private String getSQLCustomMainSetFiedlds() {
//		StringBuffer buf = new StringBuffer();
//		Iterator it = f_map.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry entry = (Entry) it.next();
//			String fieldname = (String) entry.getKey();
//			FieldItem item = DataDictionary.getFieldItem(fieldname);
//			if (item == null)
//				continue;
//			if (item.getUseflag().equalsIgnoreCase("0")
//					|| fieldname.equalsIgnoreCase("a0101"))
//				continue;
//			if (item.isMainSet()) {
//				buf.append(fieldname + " as "
//						+ this.getAppAttributeValue(HrSyncBo.A, fieldname));
//				buf.append(",");
//			}
//		}
//		if (buf.length() > 0)
//			buf.setLength(buf.length() - 1);
//		return buf.toString();
//	}

	/**
	 * 重新导入数据
	 */
	public void importData(String destTab) throws GeneralException {
		if (!this.sync_a01) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		dropTable(destTab);
		creatHrTable(destTab);
		buf.append("delete from " + destTab);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }
			String userpwd = getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			String fields = getMainSetFields();
			String customfields = getCustomMainSetFiedlds();
			for (int i = 0; i < dblist.size(); i++) {
				buf.setLength(0);
				String dbname = (String) dblist.get(i);
				String hz_dbname = AdminCode.getCodeName("@@", dbname);
				buf.append("insert into " + destTab + " ");
				buf.append("(unique_id,nbase,nbase_0,A0100,");
				buf.append("B0110_0,E0122_0,A0101,E01A1_0,");
				buf.append("username,userpassword,flag,sDate");
				if (customfields.length() > 0) {
					buf.append(",");
					String cloumn = customfields;
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer str = new StringBuffer();
						str
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						str.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						str.setLength(str.length() - 1);
						cloumn = str.toString();
					}
					buf.append(cloumn);
				}
				buf.append(")");
				buf.append("( select guidkey, '" + hz_dbname + "','" + dbname + "',");
				buf.append(" A0100,B0110,E0122,A0101,E01A1,");
				buf.append(userpwd);
				buf.append("," + this.ADD_FLAG + " ");
				buf.append("," + Sql_switcher.dateValue(datestr));
				if (fields.length() > 0) {
					buf.append(",");
					String cloumn = fields;
					if (cloumn != null && cloumn.indexOf("a0101") != -1
							&& customfields != null
							&& customfields.indexOf("a0100") != -1) {
						StringBuffer str = new StringBuffer();
						str
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						str.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						str.setLength(str.length() - 1);
						cloumn = str.toString();
					}
					buf.append(cloumn);
				}
				buf.append(" from " + dbname + "a01)");
				buf.append(" ");
				initGuidKey(dbname + "A01");//初始化A01
				dao.update(buf.toString());
				/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
				importSubSetData(destTab, dbname, "");
			}// for i loop end.
			if (!isBcode())// **代码翻译*//*
            {
                updateCodeItemdesc(destTab,"");
            } else if (this.codefields.length() > 0) {
                updateGivenCodeItemdesc(destTab, this.codefields,"");
            }

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public void importPartData() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();

		try {
			if (!dbw.isExistTable("t_hr_view", false)) {
                return;
            }

			/** 先导入固定项目及人员主集指标项目 */
			String fields = getMainSetFields();
			String[] field = fields.split(",");
			ArrayList fieldlist = new ArrayList();
			for (int i = 0; i < field.length; i++) {
				fieldlist.add(field[i]);
			}
			String sql = "select name from syscolumns where id=object_id('t_hr_view') ";
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				if (fieldlist.indexOf(rs.getString("name")) != -1) {
                    fieldlist.remove(rs.getString("name"));
                }
				if (fieldlist.size() <= 0) {
                    break;
                }
			}

			if (fields.length() > 0) {
				creatHrTable("t_hr_view");
				for (int i = 0; i < dblist.size(); i++) {
					String dbname = (String) dblist.get(i);
					for (int j = 0; j < fieldlist.size(); j++) {
						buf.setLength(0);
						buf.append("update t_hr_view set " + fieldlist.get(j)
								+ " = (select " + fieldlist.get(j) + " from "
								+ dbname + "a01  where t_hr_view.a0100 = "
								+ dbname + "a01.a0100) ");
						buf.append(" where exists (select null from " + dbname
								+ "a01  where t_hr_view.a0100 = " + dbname
								+ "a01.a0100)");
						dao.update(buf.toString());
					}
					/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
					importPartSubSetData(dbname);
				}

				if (!isBcode())// **代码翻译*//*
                {
                    updateCodeItemdesc("t_hr_view","");
                } else if (this.codefields.length() > 0) {
                    updateGivenCodeItemdesc("t_hr_view", this.codefields,"");
                }

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public void importOrgPartData(String destTab) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer fieldname = new StringBuffer();
		// buf.append("delete from t_hr_view");
		RowSet rs = null;
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }

			String fields = null;
			Iterator it = o_map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				fieldname.append((String) entry.getKey() + ",");

			}
			fields = fieldname.toString();
			if (fields.length() <= 0) {
                return;
            }
			String[] field = fields.split(",");
			ArrayList fieldlist = new ArrayList();
			for (int i = 0; i < field.length; i++) {
				FieldItem item = DataDictionary.getFieldItem(field[i]);
				// if(!item.getFieldsetid().equalsIgnoreCase("b01"))
				if (!"b0110".equalsIgnoreCase(item.getItemid())
						&& !"b01".equalsIgnoreCase(item.getFieldsetid())) {
                    continue;
                }
				fieldlist.add(field[i]);
			}
			String sql = "select name from syscolumns where id=object_id('"
					+ destTab + "') ";
			rs = dao.search(sql);
			while (rs.next()) {
				if (fieldlist.indexOf(rs.getString("name")) != -1) {
                    fieldlist.remove(rs.getString("name"));
                }
				if (fieldlist.size() <= 0) {
                    break;
                }
			}
			creatOrgTable(destTab);
			if (fields.length() > 0) {
				if (fieldlist.size() > 0) {
                    for (int j = 0; j < fieldlist.size(); j++) {
                        buf.setLength(0);
                        buf
                                .append("update t_org_view set "
                                        + fieldlist.get(j)
                                        + " = (select "
                                        + fieldlist.get(j)
                                        + " from b01  where t_org_view.b0110_0 = b01.b0110) ");
                        buf
                                .append(" where exists (select null from b01  where t_org_view.b0110_0 = b01.b0110)");
                        dao.update(buf.toString());
                    }
                }
				/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
				importOrgPartSubSetData(destTab);

				if (this.orgcodefields.length() > 0) {
                    updateOrgGivenCodeItemdesc(destTab, this.orgcodefields);
                }
				/** 代码翻译 */
				if (!isBcode()) {
                    updateOrgCodeItemdesc(destTab);
                }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}

	public void importOrgData(String destTab) throws GeneralException {
		if (!this.sync_b01) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer fieldname = new StringBuffer();
		creatOrgTable(destTab);
		buf.append("delete from " + destTab + " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }
			// String userpwd=getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			// String field=this.orgcodefields;
			// 修改过
			String field = null;
			Iterator it = o_map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				fieldname.append((String) entry.getKey() + ",");

			}
			field = fieldname.toString();
			if (field.length() <= 0) {
                return;
            }
			String[] bfields = field.split(",");
			String fields = "", customfields = "";

			for (int i = 0; i < bfields.length; i++) {
				FieldItem item = DataDictionary.getFieldItem(bfields[i]);
				if (!"b0110".equalsIgnoreCase(item.getItemid())
						&& !"b01".equalsIgnoreCase(item.getFieldsetid())) {
                    continue;
                }
				fields += "," + bfields[i];
				customfields += ","
						+ this.getAppAttributeValue(HrSyncBo.B, bfields[i]);
			}
			buf.setLength(0);

			buf.append("insert into " + destTab + " ");
			buf.append("(b0110_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,");
			buf.append("grade,flag,sDate");
			if (fields.length() > 0) {
				buf.append(customfields);
			}
			buf.append(")");
			buf
					.append("(select o1.codeitemid,o1.guidkey,o1.codesetid,o1.codeitemdesc,o1.parentid,o.codeitemdesc as parentdesc,o1.grade,o1.flag ");
			buf.append("," + Sql_switcher.dateValue(datestr));
			if (fields.length() > 0) {
				String ofields = fields.replaceAll(",", ",o1.");
				buf.append(ofields);
			}
			buf.append(" from organization o,");
			buf
					.append("( select codeitemid,organization.guidkey,codesetid,codeitemdesc,parentid,parentid as parentdesc,grade");
			buf.append("," + this.ADD_FLAG + " flag");
			if (fields.length() > 0) {
				buf.append(fields);
			}
			buf.append(" from organization ");
			if (fields.length() > 0) {
				buf.append("left join b01 on organization.codeitemid = b01.b0110");
			}
			buf.append(" ) o1 where  o1.parentid = o.codeitemid)");
			initGuidKey("organization");//初始化organization
			dao.update(buf.toString());
			// 导入子集－把人员库中当前记录导入至视图t_hr_view
			importOrgSubSetData(destTab, "");

			/** 代码翻译 */
			if (!isBcode()) {
                updateOrgCodeItemdesc(destTab);
            } else if (this.orgcodefields.length() > 0) {
                updateOrgGivenCodeItemdesc(destTab, this.orgcodefields);
            }
			synchronizationInitOrgA0000(destTab);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public void importPostData(String destTab) throws GeneralException {
		if (!this.sync_k01) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer fieldname = new StringBuffer();
		creatPostTable(destTab);
		buf.append("delete from " + destTab + " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }
			// String userpwd=getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			// String field=this.orgcodefields;
			// 修改过
			String field = null;
			Iterator it = k_map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				fieldname.append((String) entry.getKey() + ",");

			}
			field = fieldname.toString();
			if (field.length() <= 0) {
                return;
            }
			String[] bfields = field.split(",");
			String fields = "", customfields = "";

			for (int i = 0; i < bfields.length; i++) {
				FieldItem item = DataDictionary.getFieldItem(bfields[i]);
				if (item!=null && item.getFieldsetid()!=null && !"k01".equalsIgnoreCase(item.getFieldsetid())) {
					if (item!=null && item.getItemid()!=null && !"e0122".equalsIgnoreCase(item.getItemid())
							&& !"e01a1".equalsIgnoreCase(item.getItemid())) {
                        continue;
                    }
				}

				fields += "," + bfields[i];
				customfields += ","
						+ this.getAppAttributeValue(HrSyncBo.K, bfields[i]);
			}
			buf.setLength(0);

			buf.append("insert into " + destTab + " ");
			buf.append("(e01a1_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,");
			buf.append("grade,flag,sDate,e0122_0");
			if (fields.length() > 0) {
				buf.append(customfields);
			}
			buf.append(")");
			buf
					.append("(select o1.codeitemid,o1.guidkey,o1.codesetid,o1.codeitemdesc,o1.parentid,o.codeitemdesc as parentdesc,o1.grade,o1.flag ");
			buf.append("," + Sql_switcher.dateValue(datestr));
			buf.append(",o1.e0122_0");
			if (fields.length() > 0) {
				String ofields = fields.replaceAll(",", ",o1.");
				buf.append(ofields);
			}
			buf.append(" from organization o,");
			buf
					.append("( select codeitemid,guidkey,codesetid,codeitemdesc,parentid,parentid as parentdesc,grade");
			buf.append("," + this.ADD_FLAG + " flag,e0122 e0122_0");
			if (fields.length() > 0) {
				buf.append(fields);
			}
			buf.append(" from organization ");
			// if(fields.length()>0){
			buf.append("left join k01 on organization.codeitemid = k01.e01a1");
			// }
			buf.append(" ) o1 where  o1.parentid = o.codeitemid)");
			// System.out.println(buf.toString());
			initGuidKey("organization");
			dao.update(buf.toString());
			// 导入子集－把人员库中当前记录导入至视图t_hr_view
			importPostSubSetData(destTab, "");

			/** 代码翻译 */
			if (!isBcode()) {
                updatePostCodeItemdesc(destTab);
            } else if (this.postcodefields.length() > 0) {
                updatePostGivenCodeItemdesc(destTab, this.postcodefields);
            }
			synchronizationInitPostA0000(destTab);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 代码翻译
	 * 
	 * @throws GeneralException
	 */
	private void updateCodeItemdesc(String destTab,String where) throws GeneralException {
		try {
			Iterator it = f_map.entrySet().iterator();
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			// String destTab="t_hr_view";
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& "UM".equalsIgnoreCase(item.getCodesetid())) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
				}
				strJoin.append(destTab);
				strJoin.append(".");
				// strJoin.append(fieldname);
				strJoin
						.append(this
								.getAppAttributeValue(HrSyncBo.A, fieldname));
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");
				// strSet.append(fieldname);
				strSet.append(this.getAppAttributeValue(HrSyncBo.A, fieldname));
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".codeitemdesc");

				strSWhere.append(" codesetid='");
				strSWhere.append(item.getCodesetid());
				strSWhere.append("'");

				if ("organization".equals(srcTab)
						&& "UN".equals(item.getCodesetid())) {
					strSWhere.setLength(0);
					strSWhere.append(" ( codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' or codesetid='UM' ) ");
				}

				dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
						strSet.toString(), where, strSWhere.toString());
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void updateOrgCodeItemdesc(String destTab) throws GeneralException {
		try {
			Iterator it = o_map.entrySet().iterator();
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			// String destTab="t_org_view";
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& ("UM".equalsIgnoreCase(item.getCodesetid()) || "UN".equalsIgnoreCase(item
									.getCodesetid()))) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
				}
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin
						.append(this
								.getAppAttributeValue(HrSyncBo.B, fieldname));
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");
				strSet.append(this.getAppAttributeValue(HrSyncBo.B, fieldname));
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".codeitemdesc");

				strSWhere.append("" + srcTab + ".codesetid='");
				strSWhere.append(item.getCodesetid());
				strSWhere.append("'");

				if (srcTab.equals(this.dept_table_name)) {
					strSWhere.setLength(0);
					strSWhere.append(" ( " + srcTab + ".codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' or " + srcTab + ".codesetid='UM' ) ");
				}
				if ("organization".equals(srcTab)
						&& "UN".equals(item.getCodesetid())) {
					strSWhere.setLength(0);
					strSWhere.append(" ( " + srcTab + ".codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' or " + srcTab + ".codesetid='UM' ) ");
				}

				dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
						strSet.toString(), "", strSWhere.toString());
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void updatePostCodeItemdesc(String destTab) throws GeneralException {
		try {
			Iterator it = k_map.entrySet().iterator();
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			// String destTab="t_org_view";
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& ("UM".equalsIgnoreCase(item.getCodesetid()) || "UN".equalsIgnoreCase(item
									.getCodesetid()))) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
				}
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin
						.append(this
								.getAppAttributeValue(HrSyncBo.K, fieldname));
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");
				strSet.append(this.getAppAttributeValue(HrSyncBo.K, fieldname));
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".codeitemdesc");

				strSWhere.append("" + srcTab + ".codesetid='");
				strSWhere.append(item.getCodesetid());
				strSWhere.append("'");

				if (srcTab.equals(this.dept_table_name)) {
					strSWhere.setLength(0);
//					strSWhere.append(" ( " + srcTab + ".codesetid='");
//					strSWhere.append(item.getCodesetid());
//					strSWhere.append("' or " + srcTab + ".codesetid='UM' ) ");
					strSWhere.append(srcTab + ". codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("'");
				}
				if ("organization".equals(srcTab)) {
					strSWhere.setLength(0);
					strSWhere.append(" ( " + srcTab + ".codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("') ");
				}

				dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
						strSet.toString(), "", strSWhere.toString());
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void updateGivenCodeItemdesc(String destTab, String codestr,String where)
			throws GeneralException {
		try {

			String[] str = codestr.split(",");
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			String fieldAndCodeSeq = this.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
			String strFieldAndCode = this.getAttributeValue(HrSyncBo.FIELDANDCODE);
			
			for (int i = 0; i < str.length; i++) {

				String fieldname = str[i];
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				boolean fieldAndCode = false;

				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& "UM".equalsIgnoreCase(item.getCodesetid())) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
					
					if ("1".equals(strFieldAndCode)) {
						fieldAndCode = true;
						
					}
				}
				strJoin.append(destTab);
				strJoin.append(".");
				;
				strJoin
						.append(this
								.getAppAttributeValue(HrSyncBo.A, fieldname));
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");

				strSet.append(this.getAppAttributeValue(HrSyncBo.A, fieldname));
				strSet.append("=");
				if (fieldAndCode) {
					if (Sql_switcher.searchDbServer() == 1) {
						strSet.append(srcTab);
						strSet.append(".codeitemid+ '" + fieldAndCodeSeq + "'+"+ srcTab +".codeitemdesc");
					} else {
						strSet.append(srcTab);
						strSet.append(".codeitemid||'" + fieldAndCodeSeq + "'||"+ srcTab +".codeitemdesc");
					}
				} else {
					strSet.append(srcTab);
					strSet.append(".codeitemdesc");
				}

				strSWhere.append(" codesetid='");
				strSWhere.append(item.getCodesetid());
				strSWhere.append("'");

				if ("organization".equals(srcTab)
						&& "UN".equals(item.getCodesetid())) {
					strSWhere.setLength(0);
					strSWhere.append(" ( codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' or codesetid='UM' ) ");
				}

				dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
						strSet.toString(), where, strSWhere.toString());
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void updateOrgGivenCodeItemdesc(String destTab, String codestr)
			throws GeneralException {
		try {

			String[] str = codestr.split(",");
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
//			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			String fieldAndCodeSeq = this.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
			String strFieldAndCode = this.getAttributeValue(HrSyncBo.FIELDANDCODE);

			for (int i = 0; i < str.length; i++) {

				String fieldname = str[i];
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				
				boolean fieldAndCode = false;
				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& ("UM".equalsIgnoreCase(item.getCodesetid()) || "UN".equalsIgnoreCase(item
									.getCodesetid()))) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
					
					if ("1".equals(strFieldAndCode)) {
						fieldAndCode = true;
						
					}
				}
				if (srcTab.equals(this.dept_table_name)) {
					strJoin.append(destTab);
					strJoin.append(".");
					strJoin.append("b0110_0");
				} else {
					strJoin.append(destTab);
					strJoin.append(".");
					strJoin.append(this.getAppAttributeValue(HrSyncBo.B,
							fieldname));
				}

				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");
				strSet.append(this.getAppAttributeValue(HrSyncBo.B, fieldname));
				strSet.append("=");
//				strSet.append(srcTab);
//				strSet.append(".codeitemdesc");
				
				if (fieldAndCode) {
					if (Sql_switcher.searchDbServer() == 1) {
						strSet.append(srcTab);
						strSet.append(".codeitemid + '" + fieldAndCodeSeq + "'+"+ srcTab +".codeitemdesc");
					} else {
						strSet.append(srcTab);
						strSet.append(".codeitemid||'" + fieldAndCodeSeq + "'||"+ srcTab +".codeitemdesc");
					}
				} else {
					strSet.append(srcTab);
					strSet.append(".codeitemdesc");
				}

				if (("organization".equals(srcTab) || srcTab
						.equals(this.dept_table_name))
						&& "UN".equals(item.getCodesetid())) {
					strSWhere.setLength(0);
					strSWhere.append(" ( " + srcTab + ".codesetid='"); // 更改指定更新表，要不回报错误
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' or " + srcTab + ".codesetid='UM' ) ");
				} else {
					strSWhere.append(" " + srcTab + ".codesetid='");
					strSWhere.append(item.getCodesetid());
					strSWhere.append("'");
				}
				String update = Sql_switcher.getUpdateSqlTwoTable(destTab,
						srcTab, strJoin.toString(), strSet.toString(), "",
						strSWhere.toString());
				// System.out.println(update);
				ContentDAO dao = new ContentDAO(this.conn);
				;
				dao.update(update);
				/*
				 * dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
				 * strSet.toString(), "", strSWhere.toString());
				 */
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void updatePostGivenCodeItemdesc(String destTab, String codestr)
			throws GeneralException {
		try {

			String[] str = codestr.split(",");
			StringBuffer strJoin = new StringBuffer();
			StringBuffer strSet = new StringBuffer();
			StringBuffer strSWhere = new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.conn);
			String srcTab = null;
			
			String fieldAndCodeSeq = this.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
			String strFieldAndCode = this.getAttributeValue(HrSyncBo.FIELDANDCODE);

			for (int i = 0; i < str.length; i++) {

				String fieldname = str[i];
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (!item.isCode()) {
                    continue;
                }
				
				boolean fieldAndCode = false;
				
				// /
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					if (this.nlevel > 0
							&& ("UM".equalsIgnoreCase(item.getCodesetid()) || "UN".equalsIgnoreCase(item
									.getCodesetid()))) {
                        srcTab = this.dept_table_name;
                    } else {
                        srcTab = "organization";
                    }
				} else {
					srcTab = "codeitem";
					
					if ("1".equals(strFieldAndCode)) {
						fieldAndCode = true;
						
					}
				}

				strJoin.append(destTab);
				strJoin.append(".");
				strJoin
						.append(this
								.getAppAttributeValue(HrSyncBo.K, fieldname));
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");

				strSet.append(destTab);
				strSet.append(".");
				strSet.append(this.getAppAttributeValue(HrSyncBo.K, fieldname));
				strSet.append("=");
//				strSet.append(srcTab);
//				strSet.append(".codeitemdesc");
				
				if (fieldAndCode) {
					if (Sql_switcher.searchDbServer() == 1) {
						strSet.append(srcTab);
						strSet.append(".codeitemid + '" + fieldAndCodeSeq + "'+"+ srcTab +".codeitemdesc");
					} else {
						strSet.append(srcTab);
						strSet.append(".codeitemid||'" + fieldAndCodeSeq + "'||"+ srcTab +".codeitemdesc");
					}
				} else {
					strSet.append(srcTab);
					strSet.append(".codeitemdesc");
				}

				strSWhere.append(" " + srcTab + ".codesetid='");
				strSWhere.append(item.getCodesetid());
				strSWhere.append("'");

				if ("organization".equals(srcTab)) {
					strSWhere.setLength(0);
					strSWhere.append(" ( " + srcTab + ".codesetid='"); // 更改指定更新表，要不回报错误
					strSWhere.append(item.getCodesetid());
					strSWhere.append("' ) ");
				}

				dbWizard.updateRecord(destTab, srcTab, strJoin.toString(),
						strSet.toString(), "", strSWhere.toString());
				/** 清空字符串 */
				strJoin.setLength(0);
				strSWhere.setLength(0);
				strSet.setLength(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 取得登记用户名及口令指标
	 * 
	 * @return
	 */
	private String getUserPwd() {
		StringBuffer buf = new StringBuffer();
		DbNameBo dbbo = new DbNameBo(this.conn);
		String username = dbbo.getLogonUserNameField();
		String password = dbbo.getLogonPassWordField();
		buf.append(username);
		buf.append(",");
		buf.append(password);
		return buf.toString();
	}

	/**
	 * 把人员库中当前记录导入至视图t_hr_view
	 * 
	 * @param dbname
	 *            应用库前缀
	 * @throws GeneralException
	 */
	private void importSubSetData(String strDesT, String dbname, String flag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			Iterator it = f_map.entrySet().iterator();
			/** 不考虑同一子集指标同时导入啦，指标不会太多 */
			StringBuffer strUpdate = new StringBuffer();
			int db_type = Sql_switcher.searchDbServer();// 数据库类型
			String fieldstr, cfieldstr;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				String customfieldname = this.getAppAttributeValue(HrSyncBo.A,
						fieldname);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (item.isMainSet()) {
                    continue;
                }
				strUpdate.setLength(0);
				String setname = item.getFieldsetid();
				String strSrcT = dbname + setname;
				// String strDesT="t_hr_view";
				if (db_type == 2 || db_type == 3) {
					fieldstr = "U." + fieldname;
					cfieldstr = "T." + customfieldname;
				} else {
					fieldstr = "T." + customfieldname + "=U." + fieldname;
					cfieldstr = "T." + customfieldname + "=U." + fieldname;
				}

				if (db_type == 2 || db_type == 3) // oracle,db2
				{
					strUpdate.append("update ");
					strUpdate.append(strDesT);
					strUpdate.append(" T set (");
					strUpdate.append(cfieldstr);
					strUpdate.append(")=(select ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U Where T.A0100=U.A0100");
				} else {
					strUpdate.append("Update T set ");
					strUpdate.append(cfieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strDesT);
					strUpdate.append(" T Left join ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U ON T.A0100=U.A0100");
				}
				if (db_type == 2 || db_type == 3) {
					strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".A0100=U.A0100) or U.I9999 is null) ");
					strUpdate.append(") where (");
					strUpdate.append(" nbase_0='");
					strUpdate.append(dbname);
					strUpdate.append("')");
				} else {
					strUpdate
							.append(" where (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".A0100=U.A0100) or U.I9999 is null) ");
					strUpdate.append(" and nbase_0='");
					strUpdate.append(dbname);
					strUpdate.append("'");
				}
				if (flag != null && flag.length() > 0) {
					strUpdate.append(" and flag='" + flag + "'");
				}
				dao.update(strUpdate.toString());
			}
			
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void importOrgSubSetData(String strDesT, String flag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			Iterator it = o_map.entrySet().iterator();
			/** 不考虑同一子集指标同时导入啦，指标不会太多 */
			StringBuffer strUpdate = new StringBuffer();
			int db_type = Sql_switcher.searchDbServer();// 数据库类型
			String fieldstr, cfieldstr;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				String customfieldname = this.getAppAttributeValue(HrSyncBo.B,
						fieldname);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (item.isMainSet()) // 判断是否为主集的指标；
                {
                    continue;
                }
				strUpdate.setLength(0);
				String setname = item.getFieldsetid();
				String strSrcT = setname;
				if (db_type == 2 || db_type == 3) {
					fieldstr = "U." + fieldname;
					cfieldstr = "T." + customfieldname;
				} else {
					fieldstr = "T." + fieldname + "=U." + fieldname;
					cfieldstr = "T." + customfieldname + "=U." + fieldname;
				}

				if (db_type == 2 || db_type == 3) // oracle,db2
				{
					strUpdate.append("update ");
					strUpdate.append(strDesT);
					strUpdate.append(" T set (");
					strUpdate.append(cfieldstr);
					strUpdate.append(")=(select ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U Where T.B0110_0=U.B0110");
				} else {
					strUpdate.append("Update T set ");
					strUpdate.append(cfieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strDesT);
					strUpdate.append(" T Left join ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U ON T.B0110_0=U.B0110");
				}
				if (db_type == 2 || db_type == 3) {
					strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".B0110=U.B0110) or U.I9999 is null)) ");
				} else {
					strUpdate
							.append(" where (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".B0110=U.B0110) or U.I9999 is null) ");
				}
				if (flag != null && flag.length() > 0) {
					if (db_type == 2 || db_type == 3) {
                        strUpdate.append(" where flag='" + flag + "'");
                    } else {
                        strUpdate.append(" and flag='" + flag + "'");
                    }
				}
				dao.update(strUpdate.toString());
			}// while loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void importPostSubSetData(String strDesT, String flag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			Iterator it = k_map.entrySet().iterator();
			/** 不考虑同一子集指标同时导入啦，指标不会太多 */
			StringBuffer strUpdate = new StringBuffer();
			int db_type = Sql_switcher.searchDbServer();// 数据库类型
			String fieldstr, cfieldstr;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				String customfieldname = this.getAppAttributeValue(HrSyncBo.K,
						fieldname);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (item.isMainSet()) // 判断是否为主集的指标；
                {
                    continue;
                }
				strUpdate.setLength(0);
				String setname = item.getFieldsetid();
				String strSrcT = setname;
				if (db_type == 2 || db_type == 3) {
					fieldstr = "U." + fieldname;
					cfieldstr = "T." + customfieldname;
				} else {
					fieldstr = "T." + fieldname + "=U." + fieldname;
					cfieldstr = "T." + customfieldname + "=U." + fieldname;
				}

				if (db_type == 2 || db_type == 3) // oracle,db2
				{
					strUpdate.append("update ");
					strUpdate.append(strDesT);
					strUpdate.append(" T set (");
					strUpdate.append(cfieldstr);
					strUpdate.append(")=(select ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U Where T.e01a1_0=U.e01a1");
				} else {
					strUpdate.append("Update T set ");
					strUpdate.append(cfieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strDesT);
					strUpdate.append(" T Left join ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U ON T.e01a1_0=U.e01a1");
				}
				if (db_type == 2 || db_type == 3) {
					strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".e01a1=U.e01a1) or U.I9999 is null)) ");
				} else {
					strUpdate
							.append(" where (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".e01a1=U.e01a1) or U.I9999 is null) ");
				}
				if (flag != null && flag.length() > 0) {
					if (db_type == 2 || db_type == 3) {
                        strUpdate.append(" where flag='" + flag + "'");
                    } else {
                        strUpdate.append(" and flag='" + flag + "'");
                    }
				}
				dao.update(strUpdate.toString());
			}// while loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void importPartSubSetData(String dbname) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			Iterator it = f_map.entrySet().iterator();
			/** 不考虑同一子集指标同时导入啦，指标不会太多 */
			StringBuffer strUpdate = new StringBuffer();
			int db_type = Sql_switcher.searchDbServer();// 数据库类型
			String fieldstr;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (item.isMainSet()) {
                    continue;
                }
				creatHrTable("t_hr_view");

				strUpdate.setLength(0);
				String setname = item.getFieldsetid();
				String strSrcT = dbname + setname;
				String strDesT = "t_hr_view";
				if (db_type == 2 || db_type == 3) {
					fieldstr = "T." + fieldname;
				} else {
                    fieldstr = "T." + fieldname + "=U." + fieldname;
                }

				if (db_type == 2 || db_type == 3) // oracle,db2
				{
					strUpdate.append("update ");
					strUpdate.append(strDesT);
					strUpdate.append(" T set (");
					strUpdate.append(fieldstr);
					strUpdate.append(")=(select ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U Where T.A0100=U.A0100");
				} else {
					strUpdate.append("Update T set ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strDesT);
					strUpdate.append(" T Left join ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U ON T.A0100=U.A0100");
				}
				if (db_type == 2 || db_type == 3) {
					strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".A0100=U.A0100) or U.I9999 is null) ");
					strUpdate.append(") where (");
					strUpdate.append(" nbase_0='");
					strUpdate.append(dbname);
					strUpdate.append("'");
				} else {
					strUpdate
							.append(" where (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".A0100=U.A0100) or U.I9999 is null) ");
					strUpdate.append(" and nbase_0='");
					strUpdate.append(dbname);
					strUpdate.append("'");
				}
				dao.update(strUpdate.toString());
			}// while loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private void importOrgPartSubSetData(String strDesT)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			Iterator it = o_map.entrySet().iterator();
			/** 不考虑同一子集指标同时导入啦，指标不会太多 */
			StringBuffer strUpdate = new StringBuffer();
			int db_type = Sql_switcher.searchDbServer();// 数据库类型
			String fieldstr;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String fieldname = (String) entry.getKey();
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				if ("0".equalsIgnoreCase(item.getUseflag())) {
                    continue;
                }
				if (item.isMainSet()) {
                    continue;
                }
				creatOrgTable(strDesT);
				strUpdate.setLength(0);
				String setname = item.getFieldsetid();
				String strSrcT = setname;
				// String strDesT="t_org_view";
				if (db_type == 2 || db_type == 3) {
					fieldstr = "T." + fieldname;
				} else {
                    fieldstr = "T." + fieldname + "=U." + fieldname;
                }

				if (db_type == 2 || db_type == 3) // oracle,db2
				{
					strUpdate.append("update ");
					strUpdate.append(strDesT);
					strUpdate.append(" T set (");
					strUpdate.append(fieldstr);
					strUpdate.append(")=(select ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U Where T.B0110_0=U.B0110");
				} else {
					strUpdate.append("Update T set ");
					strUpdate.append(fieldstr);
					strUpdate.append(" from ");
					strUpdate.append(strDesT);
					strUpdate.append(" T Left join ");
					strUpdate.append(strSrcT);
					strUpdate.append(" U ON T.B0110_0=U.B0110");
				}
				if (db_type == 2 || db_type == 3) {
					strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".B0110=U.B0110) or U.I9999 is null)) ");

				} else {
					strUpdate
							.append(" where (U.I9999=(select Max(I9999) from ");
					strUpdate.append(strSrcT);
					strUpdate.append(" where ");
					strUpdate.append(strSrcT);
					strUpdate.append(".B0110=U.B0110) or U.I9999 is null) ");

				}
				dao.update(strUpdate.toString());
			}// while loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 创建人员视图表
	 * 
	 * @throws GeneralException
	 */
	public void creatHrTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		String only_field = getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("nbase", "nbase");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("nbase_0", "nbase_0");
			field.setDatatype(DataType.STRING);
			field.setLength(3);
			table.addField(field);

			field = new Field("unique_id", "unique_id");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);

			field = new Field("A0100", "A0100");
			field.setDatatype(DataType.STRING);
			field.setLength(10);
			table.addField(field);

			field = new Field("B0110_0", "B0110_0");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("E0122_0", "E0122_0");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("E01A1_0", "E01A1_0");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("B0110_code", "B0110_code");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("E0122_code", "E0122_code");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("E01A1_code", "E01A1_code");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("A0101", "A0101");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);

			

			field = new Field("username", "username");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("userpassword", "userpassword");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("sDate", "sDate");
			field.setDatatype(DataType.DATE);
			table.addField(field);
			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);
			field = new Field("sys_flag", "sys_flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);
			field = new Field("a0000", "a0000");
			field.setDatatype(DataType.INT);
			table.addField(field);
//			field = new Field("corcode", "corcode");
//			field.setDatatype(DataType.STRING);
//			field.setLength(100);
//			table.addField(field);
			if("1".equals(this.getAttributeValue(this.JZ_FIELD))){
				field = new Field("jz_field","jz_field");
				field.setDatatype(DataType.STRING);
				field.setLength(255);
				table.addField(field);
			}
			syncStruts(table, only_field);
			dbw.createTable(table);
			dbmodel.reloadTableModel(destTab);
		} else {
			ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
					this.conn);
			if (!reconstructionKqField.checkFieldSave(destTab, "nbase")) {
				Field field = new Field("nbase", "nbase");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "nbase_0")) {
				Field field = new Field("nbase_0", "nbase_0");
				field.setDatatype(DataType.STRING);
				field.setNullable(false);
				if (only_field == null || only_field.length() <= 0) {
                    field.setKeyable(true);
                }
				field.setLength(3);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "unique_id")) {
				Field field = new Field("unique_id", "unique_id");
				field.setDatatype(DataType.STRING);
				field.setLength(38);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "A0100")) {
				Field field = new Field("A0100", "A0100");
				field.setDatatype(DataType.STRING);
				field.setNullable(false);
				if (only_field == null || only_field.length() <= 0) {
                    field.setKeyable(true);
                }
				field.setLength(10);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "B0110_0")) {
				Field field = new Field("B0110_0", "B0110_0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "E0122_0")) {
				Field field = new Field("E0122_0", "E0122_0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "E01A1_0")) {
				Field field = new Field("E01A1_0", "E01A1_0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			//保存的organization表中的corcode内容
			if (!reconstructionKqField.checkFieldSave(destTab, "B0110_code")) {
				Field field = new Field("B0110_code", "B0110_code");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "E0122_code")) {
				Field field = new Field("E0122_code", "E0122_code");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "E01A1_code")) {
				Field field = new Field("E01A1_code", "E01A1_code");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "A0101")) {
				Field field = new Field("A0101", "A0101");
				field.setDatatype(DataType.STRING);
				field.setLength(500);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
			if (!reconstructionKqField.checkFieldSave(destTab, "username")) {
				Field field = new Field("username", "username");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "userpassword")) {
				Field field = new Field("userpassword", "userpassword");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "flag")) {
				Field field = new Field("flag", "flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sDate")) {
				Field field = new Field("sDate", "sDate");
				field.setDatatype(DataType.DATE);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sys_flag")) {
				Field field = new Field("sys_flag", "sys_flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "a0000")) {
				Field field = new Field("a0000", "a0000");
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
//			if (!reconstructionKqField.checkFieldSave(destTab, "corcode")) {
//				Field field = new Field("corcode", "corcode");
//				field.setDatatype(DataType.STRING);
//				field.setLength(100);
//				table = new Table(destTab);
//				table.addField(field);
//				dbw.addColumns(table);
//				dbmodel.reloadTableModel(destTab);
//			}
			if (!reconstructionKqField.checkFieldSave(destTab, "jz_field")&&"1".equals(this.getAttributeValue(this.JZ_FIELD))) {
				Field field = new Field("jz_field", "jz_field");
				field.setDatatype(DataType.STRING);
				field.setLength(255);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			syncStruts(destTab, only_field);
		}
		/** 同步表结构 */
		dbmodel.reloadTableModel(destTab);

	}

	public void creatOrgTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("b0110_0", "b0110_0");
			field.setDatatype(DataType.STRING);
//			field.setKeyable(true);
//			field.setNullable(false);
			field.setLength(50);
			table.addField(field);

			field = new Field("unique_id", "unique_id");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);
			
			field = new Field("codesetid", "codesetid");
			field.setDatatype(DataType.STRING);
			field.setLength(2);
			table.addField(field);

			field = new Field("codeitemdesc", "codeitemdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);

			field = new Field("parentid", "parentid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("parentdesc", "parentdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);
			
			/*记录上级机构唯一标识 wangb 20170811*/
			field = new Field("parentGUIDKEY", "parentGUIDKEY");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);
			
			/*记录原机构编码   wangb 20170811*/
			field = new Field("origincodeitemid", "origincodeitemid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			
			field = new Field("grade", "grade");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("sDate", "sDate");
			field.setDatatype(DataType.DATE);
			table.addField(field);

			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);
			
			field = new Field("sys_flag", "sys_flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("a0000", "a0000");
			field.setDatatype(DataType.INT);
			table.addField(field);

			field = new Field("corcode", "corcode");
			field.setDatatype(DataType.STRING);
			field.setLength(100);
			table.addField(field);
			/*同级排序字段  wangb  20170811*/
			field = new Field("levelA0000", "levelA0000");
			field.setDatatype(DataType.INT);
			table.addField(field);
			
			dbw.createTable(table);
			dbmodel.reloadTableModel(destTab);
			/** 同步表结构 */
			OrgsyncStruts(destTab,false);
		} else {
			ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
			if (!reconstructionKqField.checkFieldSave(destTab, "b0110_0")) {
				Field field = new Field("b0110_0", "b0110_0");
				field.setDatatype(DataType.STRING);
				field.setKeyable(true);
				field.setNullable(false);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "unique_id")) {
				Field field = new Field("unique_id", "unique_id");
				field.setDatatype(DataType.STRING);
				field.setLength(38);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "codesetid")) {
				Field field = new Field("codesetid", "codesetid");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "parentid")) {
				Field field = new Field("parentid", "parentid");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "parentdesc")) {
				Field field = new Field("parentdesc", "parentdesc");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断parentGUIDKEY 上级机构唯一标识 字段是否存在  wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "parentGUIDKEY")) {
				Field field = new Field("parentGUIDKEY", "parentGUIDKEY");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断origincodeitemid 原机构编码 字段是否存在  wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "origincodeitemid")) {
				Field field = new Field("origincodeitemid", "origincodeitemid");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
			if (!reconstructionKqField.checkFieldSave(destTab, "corcode")) {
				Field field = new Field("corcode", "corcode");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "grade")) {
				Field field = new Field("grade", "grade");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "flag")) {
				Field field = new Field("flag", "flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sys_flag")) {
				Field field = new Field("sys_flag", "sys_flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sDate")) {
				Field field = new Field("sDate", "sDate");
				field.setDatatype(DataType.DATE);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "codeitemdesc")) {
				Field field = new Field("codeitemdesc", "codeitemdesc");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "a0000")) {
				Field field = new Field("a0000", "a0000");
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断是否存在levelA0000 wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "levelA0000")) {
				Field field = new Field("levelA0000", "levelA0000");
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/** 同步表结构 */
			OrgsyncStruts(destTab,true);
		}
		dbmodel.reloadTableModel(destTab);
		//
	}

	/**
	 * 创建职位视图
	 * 
	 * @param destTab
	 * @throws GeneralException
	 */
	public void creatPostTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("e01a1_0", "e01a1_0");
			field.setDatatype(DataType.STRING);
//			field.setKeyable(true);
//			field.setNullable(false);
			field.setLength(50);
			table.addField(field);
			field = new Field("e0122_0", "e0122_0");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("unique_id", "unique_id");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);
			
			field = new Field("codesetid", "codesetid");
			field.setDatatype(DataType.STRING);
			field.setLength(2);
			table.addField(field);

			field = new Field("codeitemdesc", "codeitemdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);

			field = new Field("parentid", "parentid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("parentdesc", "parentdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);

			/*记录上级机构唯一标识 wangb 20170811*/
			field = new Field("parentGUIDKEY", "parentGUIDKEY");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);
			
			/*记录原机构编码   wangb 20170811*/
			field = new Field("origincodeitemid", "origincodeitemid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			
			field = new Field("grade", "grade");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("sDate", "sDate");
			field.setDatatype(DataType.DATE);
			table.addField(field);

			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("sys_flag", "sys_flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);
			
			field = new Field("a0000", "a0000");
			field.setDatatype(DataType.INT);
			table.addField(field);
			
			field = new Field("corcode", "corcode");
			field.setDatatype(DataType.STRING);
			field.setLength(100);
			table.addField(field);
			/*添加 同级排序字段 levelA0000 wangb 20170811*/
			field = new Field("levelA0000", "levelA0000");
			field.setDatatype(DataType.INT);
			table.addField(field);
			
			dbw.createTable(table);
			dbmodel.reloadTableModel(destTab);
			/** 同步表结构 */
			PostsyncStruts(destTab,false);
		} else {
			ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
					this.conn);
			if (!reconstructionKqField.checkFieldSave(destTab, "e01a1_0")) {
				Field field = new Field("e01a1_0", "e01a1_0");
				field.setDatatype(DataType.STRING);
				field.setKeyable(true);
				field.setNullable(false);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "e0122_0")) {
				Field field = new Field("e0122_0", "e0122_0");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "unique_id")) {
				Field field = new Field("unique_id", "unique_id");
				field.setDatatype(DataType.STRING);
				field.setLength(38);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "codesetid")) {
				Field field = new Field("codesetid", "codesetid");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "parentid")) {
				Field field = new Field("parentid", "parentid");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "parentdesc")) {
				Field field = new Field("parentdesc", "parentdesc");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断parentGUIDKEY 上级机构唯一标识 字段是否存在  wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "parentGUIDKEY")) {
				Field field = new Field("parentGUIDKEY", "parentGUIDKEY");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断origincodeitemid 原机构编码 字段是否存在  wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "origincodeitemid")) {
				Field field = new Field("origincodeitemid", "origincodeitemid");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
			if (!reconstructionKqField.checkFieldSave(destTab, "corcode")) {
				Field field = new Field("corcode", "corcode");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "grade")) {
				Field field = new Field("grade", "grade");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "flag")) {
				Field field = new Field("flag", "flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sys_flag")) {
				Field field = new Field("sys_flag", "sys_flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sDate")) {
				Field field = new Field("sDate", "sDate");
				field.setDatatype(DataType.DATE);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "codeitemdesc")) {
				Field field = new Field("codeitemdesc", "codeitemdesc");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "a0000")) {
				Field field = new Field("a0000", "a0000");
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/*判断 是否存在levelA0000 字段  wangb 20170811*/
			if (!reconstructionKqField.checkFieldSave(destTab, "levelA0000")) {
				Field field = new Field("levelA0000", "levelA0000");
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			/** 同步表结构 */
			PostsyncStruts(destTab,true);
		}
		dbmodel.reloadTableModel(destTab);
	}
	
	/**
	 * 创建变动信息日志表
	 * 
	 * @param destTab
	 * @throws GeneralException
	 */
	public void creatFieldChangeTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			 
			Field field = new Field("unique_id", "unique_id");
			field.setDatatype(DataType.STRING);
			field.setKeyable(true);
			field.setNullable(false);
			field.setLength(38);
			table.addField(field);
			
			field = new Field("fielditemid", "fielditemid");
			field.setDatatype(DataType.STRING);
			field.setKeyable(true);
			field.setNullable(false);
			field.setLength(30);
			table.addField(field);

			field = new Field("oldvalue", "oldvalue");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);
			
			field = new Field("newvalue", "newvalue");
			field.setDatatype(DataType.STRING);
			field.setLength(500);
			table.addField(field);
			

			field = new Field("sysid", "sysid");
			field.setDatatype(DataType.STRING);
			field.setKeyable(true);
			field.setNullable(false);
			field.setLength(30);
			table.addField(field);

			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			dbw.createTable(table);
			dbmodel.reloadTableModel(destTab);
		} else {
			ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
					this.conn);
			if (!reconstructionKqField.checkFieldSave(destTab, "unique_id")) {
				Field field = new Field("unique_id", "unique_id");
				field.setDatatype(DataType.STRING);
				field.setKeyable(true);
				field.setNullable(false);
				field.setLength(38);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "fielditemid")) {
				Field field = new Field("fielditemid", "fielditemid");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				field.setKeyable(true);
				field.setNullable(false);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "oldvalue")) {
				Field field = new Field("oldvalue", "oldvalue");
				field.setDatatype(DataType.STRING);
				field.setLength(500);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
			if (!reconstructionKqField.checkFieldSave(destTab, "newvalue")) {
				Field field = new Field("newvalue", "newvalue");
				field.setDatatype(DataType.STRING);
				field.setLength(500);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "sysid")) {
				Field field = new Field("sysid", "sysid");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				field.setKeyable(true);
				field.setNullable(false);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
			if (!reconstructionKqField.checkFieldSave(destTab, "flag")) {
				Field field = new Field("flag", "flag");
				field.setDatatype(DataType.INT);
				field.setLength(2);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			
		}
		/** 同步表结构 */
//		PostsyncStruts(destTab);
		dbmodel.reloadTableModel(destTab);
	}

	/**
	 * 创建业务人员视图表
	 * 
	 * @throws GeneralException
	 */
	public void creatOperUserTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("username", "username");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("password", "password");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("fullname", "fullname");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("nbase", "nbase");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("A0100", "A0100");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			field = new Field("state", "state");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("sDate", "sDate");
			field.setDatatype(DataType.DATE);
			table.addField(field);

			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);
			dbw.createTable(table);
			dbmodel.reloadTableModel(destTab);
		}
	}

	/**
	 * t_hr_view表结构同步（实际表与定义指标之间）
	 * 新增指标,修改外部系统同步状态   bug 35624  wangb 20180316
	 */
	private void syncStruts(String destTab, String only_field)
			throws GeneralException {
		HashMap deffieldshm = this.getFieldsMap();// getFieldsMap();//定义指标
		HashMap curfieldshm = this.getCustomCurrHrView(destTab);// getCurrHrView(destTab);//当前表中根据定义实际构建的指标
		ArrayList droplist = getDropFields(deffieldshm, curfieldshm);
		Table table = new Table(destTab);
		DbWizard dbw = new DbWizard(this.conn);
		if (droplist.size() > 0) {
			for (int i = 0; i < droplist.size(); i++) {
				Field field = new Field((String) droplist.get(i));
				table.addField(field);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.dropColumns(table);
            }
		}
		ArrayList addlist = getAddFields(deffieldshm, curfieldshm);
		table.clear();
		if (addlist.size() > 0) {
			for (int i = 0; i < addlist.size(); i++) {
				String fieldname = (String) addlist.get(i);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }

				FieldItem tempitem = (FieldItem) item.cloneItem();
				//去除代码类指标长度写死300
				/*if (tempitem.isCode()) {
                    tempitem.setItemlength(300);
                }*/
				String ss = getAppAttributeValue(HrSyncBo.A, fieldname);
				tempitem.setItemid(ss);
				/*if (only_field != null && only_field.length() > 0
						&& only_field.equalsIgnoreCase(item.getItemid()))// 唯一性主键的时候用到,与人员库前缀和员工编号同用
					tempitem.setKeyable(true);*/
				table.addField(tempitem);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.addColumns(table);
            }
			
			updateSyncField(destTab, addlist);//更新数据视图字段数据   addlist 字段集合   destTab 数据视图表名 wangb 20170619
			updateSyncFlag(destTab);//添加数据视图字段更新外部系统同步状态     已同步的改为修改状态  wangb 20180301
		}
	}

	private void syncStruts(Table table, String only_field)
			throws GeneralException {
		HashMap deffieldshm = this.getFieldsMap();// getFieldsMap();//定义指标
		HashMap curfieldshm = this.getCustomCurrHrView(table.getName());// getCurrHrView(destTab);//当前表中根据定义实际构建的指标
		ArrayList droplist = getDropFields(deffieldshm, curfieldshm);
		if (droplist.size() > 0) {
			for (int i = 0; i < droplist.size(); i++) {
				Field field = new Field((String) droplist.get(i));
				table.addField(field);
			}// for i loop end.
		}
		ArrayList addlist = getAddFields(deffieldshm, curfieldshm);
		if (addlist.size() > 0) {
			for (int i = 0; i < addlist.size(); i++) {
				String fieldname = (String) addlist.get(i);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }

				FieldItem tempitem = (FieldItem) item.cloneItem();
				//去除代码类指标长度写死300
				/*if (tempitem.isCode()) {
                    tempitem.setItemlength(300);
                }*/
				String ss = getAppAttributeValue(A, fieldname);
				tempitem.setItemid(ss);
				table.addField(tempitem);
			}// for i loop end.
		}
	}
	
	/**
	 * @param destTab
	 * @param strutsFlag * 新增单位视图指标,修改外部系统同步状态  true 修改   false 不修改   bug 35624  wangb 20180316
	 * @throws GeneralException
	 */
	private void OrgsyncStruts(String destTab, boolean strutsFlag) throws GeneralException {
		HashMap deffieldshm = getOrgFieldsMap();// 定义指标
		HashMap curfieldshm = getOrgCustomCurrHrView(destTab);// getOrgCurrHrView(destTab);//当前表中根据定义实际构建的指标
		ArrayList droplist = getDropFields(deffieldshm, curfieldshm);
		Table table = new Table(destTab);
		DbWizard dbw = new DbWizard(this.conn);
		if (droplist.size() > 0) {
			for (int i = 0; i < droplist.size(); i++) {
				Field field = new Field((String) droplist.get(i));
				table.addField(field);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.dropColumns(table);
            }
		}
		ArrayList addlist = getAddFields(deffieldshm, curfieldshm);
		table.clear();
		if (addlist.size() > 0) {
			for (int i = 0; i < addlist.size(); i++) {
				String fieldname = (String) addlist.get(i);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				FieldItem tempitem = (FieldItem) item.cloneItem();
				//去除代码类指标长度写死300
				/*if (tempitem.isCode()) {
                    tempitem.setItemlength(300);
                }*/
				String ss = getAppAttributeValue(HrSyncBo.B, fieldname);
				tempitem.setItemid(ss);
				table.addField(tempitem);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.addColumns(table);
            }
			
			updateSyncField(destTab, addlist);//更新数据视图字段数据   addlist 字段集合   destTab 数据视图表名 wangb 20170619
			if(strutsFlag) {
                updateSyncFlag(destTab);//添加数据视图字段更新外部系统同步状态     已同步的改为修改状态  wangb 20180301
            }
		}	
	}
	
	/**
	 * @param destTab
	 * @param strutsFlag   新增岗位视图指标,修改外部系统同步状态  true 修改 ,false 不修改  bug 35624  wangb 20180316
	 * @throws GeneralException
	 */
	private void PostsyncStruts(String destTab, boolean strutsFlag) throws GeneralException {
		HashMap deffieldshm = getPostFieldsMap();// 定义指标
		HashMap curfieldshm = getPostCustomCurrView(destTab);// 当前表中根据定义实际构建的指标
		ArrayList droplist = getDropFields(deffieldshm, curfieldshm);
		Table table = new Table(destTab);
		DbWizard dbw = new DbWizard(this.conn);
		if (droplist.size() > 0) {
			for (int i = 0; i < droplist.size(); i++) {
				Field field = new Field((String) droplist.get(i));
				table.addField(field);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.dropColumns(table);
            }
		}
		ArrayList addlist = getPostAddFields(deffieldshm, curfieldshm);
		table.clear();
		if (addlist.size() > 0) {
			for (int i = 0; i < addlist.size(); i++) {
				String fieldname = (String) addlist.get(i);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if (item == null) {
                    continue;
                }
				FieldItem tempitem = (FieldItem) item.cloneItem();
				//去除代码类指标长度写死300
				/*if (tempitem.isCode()) {
                    tempitem.setItemlength(300);
                }*/

				String ss = getAppAttributeValue(HrSyncBo.K, fieldname);
				tempitem.setItemid(ss);
				table.addField(tempitem);
			}// for i loop end.
			if (table.size() > 0) {
                dbw.addColumns(table);
            }
			updateSyncField(destTab, addlist);//更新数据视图字段数据   addlist 字段集合   destTab 数据视图表名 wangb 20170619
			if(strutsFlag) {
                updateSyncFlag(destTab);//添加数据视图字段更新外部系统同步状态     已同步的改为修改状态  wangb 20180301
            }
		}
		
	}
	
	/**
	 * 
	 * 同步翻译指标  wangb 20170615
	 * 
	 * @param destTab 数据视图
	 * @param code_fields 保存同步指标
	 */
	public void syncFieldTrans(String destTab,ArrayList code_fields)throws GeneralException{
		String saveCodeFiled=null;
		/*
		 *获取XML文件保存的翻译指标 
		 */
		if("t_org_view".equalsIgnoreCase(destTab)){
			saveCodeFiled=getTextValue(ORG_CODE_FIELDS);
		}else if("t_post_view".equalsIgnoreCase(destTab)){
			saveCodeFiled=getTextValue(POST_CODE_FIELDS);
		}else if("t_hr_view".equalsIgnoreCase(destTab)){
			saveCodeFiled=getTextValue(CODE_FIELDS);
		}
		List saveFieldList=new ArrayList();
		if(saveCodeFiled==null || saveCodeFiled.length()>0){
			saveFieldList=Arrays.asList(saveCodeFiled.split(","));
		}
		ArrayList droplist=new ArrayList();
		for(int i=0 ; i < saveFieldList.size() ; i++){
			if(code_fields==null || code_fields.size()==0){//保存时，没有翻译指标
				droplist.add(saveFieldList.get(i));
				continue;
			}
			if(!code_fields.contains(saveFieldList.get(i))){//对比 获取删除的翻译指标
				droplist.add(saveFieldList.get(i));
			}
		}
		if(droplist.size()>0)//删除翻译指标 重新变为原来的值
        {
            updateSyncField(destTab, droplist);
        }
		String addfield="";
		if(code_fields != null && code_fields.size() > 0){
			for(int i=0 ; i < code_fields.size() ; i++){
				if(!saveFieldList.contains(code_fields.get(i))){//对比 获取新增翻译指标
					addfield+=","+code_fields.get(i);
				}
			}
		}
		if(addfield.length()>0){//新增翻译指标   对数据视图指标数据翻译
			addfield=addfield.substring(1);
			try {
				if("t_hr_view".equalsIgnoreCase(destTab)) {
                    updateGivenCodeItemdesc(destTab, addfield,"");
                } else if("t_post_view".equalsIgnoreCase(destTab)) {
                    updatePostGivenCodeItemdesc(destTab, addfield);
                } else if("t_org_view".equalsIgnoreCase(destTab)) {
                    updateOrgGivenCodeItemdesc(destTab,addfield);
                }
				updateSyncFlag(destTab);//添加数据视图字段更新外部系统同步状态     已同步的改为修改状态  wangb 20180301
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}
	/**
	 * 更新数据视图字段数据     数据视图增加字段和翻译指标设置使用 wangb  20170619
	 * @param destTab 数据视图名
	 * @param code_fileds 更新字段集合
	 */
	private void updateSyncField(String destTab,ArrayList code_fileds){
		if("t_hr_view".equalsIgnoreCase(destTab)){//人员视图
			String ret = getTextValue(BASE);//获取人员库
			String[] db=ret.split(",");
			//每个人员库都要同步
			for(int n = 0 ; n < db.length ; n++){
				Map updatefield=new HashMap();// key 表名， value值：t_hr_view.字段=(select 字段 from 表   where 条件)  wangb 20170619
				String dbName=db[n];
				for(int i = 0 ; i < code_fileds.size(); i++){
					String fieldname=(String)code_fileds.get(i);
					FieldItem item = DataDictionary.getFieldItem(fieldname);
					if(item == null)//指标体系中 指标不存在 直接跳过 不 翻译   wangb 20171208 33246
                    {
                        continue;
                    }
					if(updatefield.containsKey(item.getFieldsetid())){
//						updatefield.put(item.getFieldsetid(), updatefield.get(item.getFieldsetid()) +","
//								+ destTab +"."+ fieldname +"=A."+fieldname);
						if("A01".equalsIgnoreCase(item.getFieldsetid())) {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ dbName + item.getFieldsetid() +" A where A.A0100=t_hr_view.A0100 and "+ destTab +".nbase_0='"+ dbName +"')");
                        } else {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ dbName + item.getFieldsetid() +" A where A.A0100=t_hr_view.A0100 and "+ destTab +".nbase_0='"+ dbName +"' and A.I9999=(select MAX(I9999) from "+ dbName + item.getFieldsetid() +" where A0100=A.A0100))");
                        }
						continue;
					}
//					updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=A."+ fieldname);
					if("A01".equalsIgnoreCase(item.getFieldsetid())) {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ dbName + item.getFieldsetid() +" A where A.A0100=t_hr_view.A0100 and "+ destTab +".nbase_0='"+ dbName +"')");
                    } else {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ dbName + item.getFieldsetid() +" A where A.A0100=t_hr_view.A0100 and "+ destTab +".nbase_0='"+ dbName +"' and A.I9999=(select MAX(I9999) from "+ dbName + item.getFieldsetid() +" where A0100=A.A0100))");
                    }
				}
				Set fset=updatefield.keySet();
				Iterator it=fset.iterator();
				while(it.hasNext()){
					String key=(String) it.next();
					/*
					 * 拼接sql语句 更新人员视图 字段数据
					 */
					StringBuffer strSql=new StringBuffer();
					strSql.append("update ");
					strSql.append(destTab + " ");
					strSql.append("set "+ updatefield.get(key) + " ");
					strSql.append("where t_hr_view.nbase_0='" +dbName+ "'");// 32178 wangb 20171020 数据视图设置多个人员库 删除翻译指标只还原最后一个人员库记录，其他人员库数据为空
					ContentDAO dao = new ContentDAO(this.conn);
					try {
						dao.update(strSql.toString());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}else{//非人员视图指标
			Map updatefield=new HashMap();// key 表名， value值：t_hr_view.字段=(select 字段 from 表   where 条件)  wangb 20170615
			for(int i = 0; i < code_fileds.size(); i++){
				String fieldname=(String)code_fileds.get(i);
				FieldItem item = DataDictionary.getFieldItem(fieldname);
				if(item == null)//指标体系中 指标不存在 直接跳过 不 翻译   wangb 20171208 33246
                {
                    continue;
                }
				if(updatefield.containsKey(item.getFieldsetid())){
					if("t_post_view".equalsIgnoreCase(destTab)){
//						updatefield.put(item.getFieldsetid(), updatefield.get(item.getFieldsetid()) +","
//								+ destTab +"."+ fieldname +"=K."+fieldname);
						if("K01".equalsIgnoreCase(item.getFieldsetid())) {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" K where K.E01A1=t_post_view.E01A1_0)");
                        } else {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" K where K.E01A1=t_post_view.E01A1_0 and K.I9999=(select MAX(I9999) from "+ item.getFieldsetid() +" where E01A1=K.E01A1))");
                        }
					}else if("t_org_view".equalsIgnoreCase(destTab)){
//						updatefield.put(item.getFieldsetid(), updatefield.get(item.getFieldsetid()) +","
//								+ destTab +"."+ fieldname +"=B."+fieldname);
						if("B01".equalsIgnoreCase(item.getFieldsetid())) {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" B where B.B0110=t_org_view.B0110_0)");
                        } else {
                            updatefield.put(item.getFieldsetid(),updatefield.get(item.getFieldsetid()) +","+ destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" B where B.B0110=t_org_view.B0110_0 and B.I9999=(select MAX(I9999) from "+ item.getFieldsetid() +" where B0110=B.B0110))");
                        }
					}
					continue;
				}
				if("t_post_view".equalsIgnoreCase(destTab)){
//					updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=K."+ fieldname);
					if("A01".equalsIgnoreCase(item.getFieldsetid())) {
                        updatefield.put("K01", destTab +"."+ fieldname +"=(select "+ fieldname +" from K01 K where K.E01A1=t_post_view.E01A1_0)");
                    } else if("K01".equalsIgnoreCase(item.getFieldsetid())) {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" K where K.E01A1=t_post_view.E01A1_0)");
                    } else {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" K where K.E01A1=t_post_view.E01A1_0 and K.I9999=(select MAX(I9999) from "+ item.getFieldsetid() +" where E01A1=K.E01A1))");
                    }
				}else if("t_org_view".equalsIgnoreCase(destTab)){
//					updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=B."+ fieldname);
					if("A01".equalsIgnoreCase(item.getFieldsetid())) {
                        updatefield.put("B01", destTab +"."+ fieldname +"=(select "+ fieldname +" from B01 B where B.B0110=t_org_view.B0110_0)");
                    } else if("B01".equalsIgnoreCase(item.getFieldsetid())) {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" B where B.B0110=t_org_view.B0110_0)");
                    } else {
                        updatefield.put(item.getFieldsetid(), destTab +"."+ fieldname +"=(select "+ fieldname +" from "+ item.getFieldsetid() +" B where B.B0110=t_org_view.B0110_0 and B.I9999=(select MAX(I9999) from "+ item.getFieldsetid() +" where B0110=B.B0110))");
                    }
				}
			}
			Set fset=updatefield.keySet();
			Iterator it=fset.iterator();
			while(it.hasNext()){
				String key=(String) it.next();
				/*
				 * 拼接sql语句 更新岗位和单位视图 字段数据
				 */
				StringBuffer strSql=new StringBuffer();
				strSql.append("update ");
				strSql.append(destTab + " set " +updatefield.get(key));
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					dao.update(strSql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 当数据视图指标新增或删除时，更新视图同步状态  bug 34955 wangb 20180301
	 * @param destTab  视图表
	 */
	public void updateSyncFlag(String destTab){
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT sys_id FROM t_sys_outsync WHERE state=?");
		ArrayList list = new ArrayList();
		list.add(1);
		ArrayList synclist = new ArrayList();
		RowSet rowset = null;
		try {
			rowset = dao.search(sql.toString(), list);
			while(rowset.next()){
				synclist.add(rowset.getString("sys_id"));
			}
			sql.setLength(0);
			list.clear();
			//数据视图初始化时,报 外部系统字段 无效  bug 35624  wangb 20180315
			if(synclist.size()==0)//当数据视图没有外部系统时,直接结束
            {
                return;
            }
			sql.append("update " + destTab + " set ");
			for( int i=0 ; i < synclist.size() ; i++)//判断数据视图有没有对应的外部系统字段
            {
                sql.append(synclist.get(i) + "=CASE WHEN sys_flag<>3 AND " + synclist.get(i) + "= 0 THEN 2 ELSE "+ synclist.get(i) +" END,");
            }
			
			sql.setLength(sql.length()-1);
			dao.update(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowset);
		}
	}
	
	/**
	 * 取得应删除指标列表
	 * 
	 * @param deffieldshm
	 * @param curfieldshm
	 * @return
	 */
	private ArrayList getDropFields(HashMap deffieldshm, HashMap curfieldshm) {
		ArrayList fieldlist = new ArrayList();
		Iterator it = curfieldshm.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String fieldname = (String) entry.getKey();
			if (!deffieldshm.containsKey(fieldname)) {
				fieldlist.add(fieldname);
			}
		}
		return fieldlist;
	}

	/**
	 * 取得应增加指标列表
	 * 
	 * @param deffieldshm
	 * @param curfieldshm
	 * @return
	 */
	private ArrayList getAddFields(HashMap deffieldshm, HashMap curfieldshm) {
		ArrayList fieldlist = new ArrayList();
		Iterator it = deffieldshm.entrySet().iterator();
		String sysfields = "flag,userpassword,username,E01A1_0,A0101,E0122_0,B0110_0,A0100,nbase_0,nbase,"
				.toLowerCase();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String fieldname = (String) entry.getKey();
			if (sysfields.indexOf(fieldname + ",") != -1) {
				if ("a0101".equals(fieldname)) {
					String syfieldname = this.getAppAttributeValue(HrSyncBo.A,
							fieldname);
					if ("a0101".equalsIgnoreCase(syfieldname)) {
                        continue;
                    }
				} else {
                    continue;
                }
			}
			if (!curfieldshm.containsKey(fieldname)) {
				fieldlist.add(fieldname);
			}
		}
		return fieldlist;
	}

//	private ArrayList getOrgAddFields(HashMap deffieldshm, HashMap curfieldshm) {
//		ArrayList fieldlist = new ArrayList();
//		Iterator it = deffieldshm.entrySet().iterator();
//		String sysfields = "flag,B0110_0,Codesetid,parentid,parentdesc,Grade"
//				.toLowerCase();
//		while (it.hasNext()) {
//			Entry entry = (Entry) it.next();
//			String fieldname = (String) entry.getKey();
//			if (sysfields.indexOf(fieldname + ",") != -1)
//				continue;
//			if (!curfieldshm.containsKey(fieldname)) {
//				fieldlist.add(fieldname);
//			}
//		}
//		return fieldlist;
//	}

	private ArrayList getPostAddFields(HashMap deffieldshm, HashMap curfieldshm) {
		ArrayList fieldlist = new ArrayList();
		Iterator it = deffieldshm.entrySet().iterator();
		String sysfields = "flag,e01a1_0,e0122_0,Codesetid,parentid,parentdesc,Grade"
				.toLowerCase();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String fieldname = (String) entry.getKey();
			if (sysfields.indexOf(fieldname + ",") != -1) {
                continue;
            }
			if (!curfieldshm.containsKey(fieldname)) {
				fieldlist.add(fieldname);
			}
		}
		return fieldlist;
	}

	/**
	 * 取得当前表中根据定义的指标创建的字段列表
	 * 
	 * @return
	 */
//	private HashMap getCurrHrView(String destTab) {
//		HashMap map = new HashMap();
//		RecordVo vo = new RecordVo(destTab);
//		ArrayList fieldlist = vo.getModelAttrs();
//		String sysfields = "flag,userpassword,username,E01A1_0,A0101,E0122_0,B0110_0,A0100,nbase_0,nbase,"
//				.toLowerCase();
//		for (int i = 0; i < fieldlist.size(); i++) {
//			String fieldname = ((String) fieldlist.get(i)).toLowerCase();
//			if (sysfields.indexOf(fieldname + ",") == -1) {
//				map.put(fieldname, fieldname);
//			}
//		}
//		return map;
//	}

	private HashMap getCustomCurrHrView(String destTab) {
		HashMap map = new HashMap();
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		dbmodel.reloadTableModel(destTab);
		RecordVo vo = new RecordVo(destTab);
		ArrayList fieldlist = vo.getModelAttrs();
		String sysfields = "flag,userpassword,username,E01A1_0,A0101,E0122_0,B0110_0,A0100,nbase_0,nbase,"
				.toLowerCase();
		for (int i = 0; i < fieldlist.size(); i++) {
			String fieldname = ((String) fieldlist.get(i)).toLowerCase();
			fieldname = this.getAppAttribute(HrSyncBo.A, fieldname).toLowerCase();
			if (sysfields.indexOf(fieldname + ",") == -1) {
				map.put(fieldname, fieldname);
			} else {
				if ("a0101".equals(fieldname)) {
					String syfieldname = this.getAppAttributeValue(HrSyncBo.A,
							fieldname);
					if (!"a0101".equalsIgnoreCase(syfieldname)) {
						map.put(fieldname, fieldname);
					}
				}
			}
		}
		return map;
	}

//	private HashMap getOrgCurrHrView(String destTab) {
//		HashMap map = new HashMap();
//		RecordVo vo = new RecordVo(destTab);
//		ArrayList fieldlist = vo.getModelAttrs();
//		String sysfields = "flag,B0110_0,Codesetid,parentid,parentdesc,Grade,"
//				.toLowerCase();
//		for (int i = 0; i < fieldlist.size(); i++) {
//			String fieldname = ((String) fieldlist.get(i)).toLowerCase();
//			if (sysfields.indexOf(fieldname + ",") == -1) {
//				map.put(fieldname, fieldname);
//			}
//		}
//		return map;
//	}

	private HashMap getOrgCustomCurrHrView(String destTab) {
		HashMap map = new HashMap();
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		dbmodel.reloadTableModel(destTab);
		RecordVo vo = new RecordVo(destTab);
		ArrayList fieldlist = vo.getModelAttrs();
		String sysfields = "flag,B0110_0,Codesetid,parentid,parentdesc,Grade,"
				.toLowerCase();
		for (int i = 0; i < fieldlist.size(); i++) {
			String fieldname = ((String) fieldlist.get(i)).toLowerCase();
			fieldname = this.getAppAttribute(HrSyncBo.B, fieldname);
			if (sysfields.indexOf(fieldname + ",") == -1) {
				map.put(fieldname, fieldname);
			}
		}
		return map;
	}

	private HashMap getPostCustomCurrView(String destTab) {
		HashMap map = new HashMap();
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		dbmodel.reloadTableModel(destTab);
		RecordVo vo = new RecordVo(destTab);
		ArrayList fieldlist = vo.getModelAttrs();
		String sysfields = "flag,E01a1_0,E0122_0,Codesetid,parentid,parentdesc,Grade,"
				.toLowerCase();
		for (int i = 0; i < fieldlist.size(); i++) {
			String fieldname = ((String) fieldlist.get(i)).toLowerCase();
			fieldname = this.getAppAttribute(HrSyncBo.K, fieldname);
			if (sysfields.indexOf(fieldname + ",") == -1) {
				map.put(fieldname, fieldname);
			}
		}
		return map;
	}

	private String getElementName(int param_type) {
		String name = "";
		switch (param_type) {
		case BASE:
			name = "base";
			break;
		case FIELDS:
			name = "fields";
			break;
		case CODE:
			name = "code";
			break;
		case KEY_FIELd:
			name = "key_field";
			break;
		case CODE_FIELDS:
			name = "code_fields";
			break;
		case ORG_FIELDS:
			name = "org_fields";
			break;
		case ORG_CODE_FIELDS:
			name = "org_code_fields";
			break;
		case POST_FIELDS:
			name = "post_fields";
			break;
		case POST_CODE_FIELDS:
			name = "post_code_fields";
			break;
		case A:
			name = "a";
			break;
		case B:
			name = "b";
			break;
		case K:
			name = "k";
			break;
		case SYNC_B01:
			name = "sync_b01";
			break;
		case SYNC_A01:
			name = "sync_a01";
			break;
		case SYNC_K01:
			name = "sync_k01";
			break;
		case HR_ONLY_FIELD:
			name = "hr_only_field";
			break;
		case SYNC_MODE:
			name = "sync_mode";
			break;
		case FAIL_LIMIT:
			name = "fail_limit";
			break;
		case JZ_FIELD:
			name = "jz_field";
			break;
		case photo:
			name = "photo";
			break;
		
		case FIELDCHANGE:
			name = "fieldChange";
			break;
		case FIELDANDCODESEQ:
			name = "fieldAndCodeSeq";
			break;
		case FIELDANDCODE:
			name = "fieldAndCode";
			break;
		}
		return name;
	}

	/**
	 * 初始化
	 */
	private void init() {		
		if (this.checkConstant()) {
			RecordVo ctrlvo = this.getRecordVo();
			if ((ctrlvo == null || "".equals(ctrlvo.getString("str_value")))) {
				this.createXml();
				this.doc = this.getDoc();
			} else {
				this.doc = this.getDoc();
			}
		} else {
			this.insert();
			this.createXml();
			this.doc = this.getDoc();
		}
		/** 人员库前缀列表 */
		String dbnamestr = getTextValue(BASE);
		String[] dbarr = StringUtils.split(dbnamestr, ",");
		for (int i = 0; i < dbarr.length; i++) {
			dblist.add(dbarr[i]);
		}// for i loop end.
		/** 取得视图定义的指标 */
		f_map = getFieldsMap();
		o_map = getOrgFieldsMap();
		k_map = getPostFieldsMap();
		String codeflag = getAttributeValue(CODE);
		if (codeflag == null || "0".equalsIgnoreCase(codeflag)
				|| "".equalsIgnoreCase(codeflag)) {
            bcode = true;
        } else {
            bcode = false;
        }
		/** 判断人员同步 */
		String sync_A01 = getAttributeValue(HrSyncBo.SYNC_A01);
		sync_A01 = sync_A01 != null && sync_A01.trim().length() > 0 ? sync_A01
				: "0";
		if ("1".equals(sync_A01)) {
            this.sync_a01 = true;
        } else {
            this.sync_a01 = false;
        }
		/** 判断单位同步 */
		String sync_B01 = getAttributeValue(HrSyncBo.SYNC_B01);
		sync_B01 = sync_B01 != null && sync_B01.trim().length() > 0 ? sync_B01
				: "0";
		if ("1".equals(sync_B01)) {
            this.sync_b01 = true;
        } else {
            this.sync_b01 = false;
        }
		String sync_K01 = getAttributeValue(HrSyncBo.SYNC_K01);
		sync_K01 = sync_K01 != null && sync_K01.trim().length() > 0 ? sync_K01
				: "0";
		if ("1".equals(sync_K01)) {
            this.sync_k01 = true;
        } else {
            this.sync_k01 = false;
        }
		
		String sync_photo = getAttributeValue(HrSyncBo.photo);
		sync_photo = sync_photo != null && sync_photo.trim().length() > 0 ? sync_photo
				: "0";
		if ("1".equals(sync_photo)) {
            this.sync_photo = true;
        } else {
            this.sync_photo = false;
        }
		
		String sync_fieldchange = getAttributeValue(HrSyncBo.FIELDCHANGE);
		sync_fieldchange = sync_fieldchange != null && sync_fieldchange.trim().length() > 0 ? sync_fieldchange
				: "0";
		if ("1".equals(sync_fieldchange)) {
            this.sync_fieldchange = true;
        } else {
            this.sync_fieldchange = false;
        }
		
		checkSystemConfigSynchInfo();// 查看配置文件中的同步信息设置
		//判断版本
		String sync_mode =getAttributeValue(HrSyncBo.SYNC_MODE);
		if (sync_mode ==null||!"trigger".equalsIgnoreCase(sync_mode))
		{
			String sync_version = SystemConfig.getPropertyValue("sync_version");
			if (sync_version != null && "old".equals(sync_version)) {
				this.UPDATE_FALG = 1;
				this.DEL_FLAG = 2;
				this.ADD_FLAG = 0;
			}
		}
	}

	/**
	 * 插入数据到常量表
	 * 
	 * @return
	 */
	public boolean insert() {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into constant values ('SYS_EXPORT_VIEW',null,'','')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 判断常量表中的常量
	 * 
	 * @return
	 */
	public boolean checkConstant() {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from constant where constant = 'SYS_EXPORT_VIEW'");
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs=null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				ret = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return ret;
	}

	public String getColumn() {
		StringBuffer sb = new StringBuffer();
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]) || "a0101"
						.equalsIgnoreCase(t_field[i]))) {
					sb
							.append(","
									+ this.getAppAttributeValue(HrSyncBo.A,
											t_field[i]));
				}
			}

		}
		return sb.toString();
	}

	public String getOrgColumn() {
		StringBuffer sb = new StringBuffer();
		String t_fields = this.getTextValue(HrSyncBo.ORG_FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				sb.append(","
						+ this.getAppAttributeValue(HrSyncBo.B, t_field[i]));
			}

		}
		return sb.toString();
	}

	public String getPostColumn() {
		StringBuffer sb = new StringBuffer();
		String t_fields = this.getTextValue(HrSyncBo.POST_FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				sb.append(","
						+ this.getAppAttributeValue(HrSyncBo.K, t_field[i]));
			}

		}
		return sb.toString();
	}

	public ArrayList getFieldsList() {
		ArrayList ret = new ArrayList();
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]))) {
					// String datavalue = "";
					FieldItem fi = DataDictionary.getFieldItem(t_field[i]);
					// 修改：增加 "_0"

					if (fi != null) {
						fi = (FieldItem) fi.cloneItem();
						if (!isBcode()) {
                            fi.setCodesetid("0");
                        }
						ret.add(fi);
					}
				}
			}
		}
		return ret;
	}

	public ArrayList getCustomFieldsList() {
		ArrayList ret = new ArrayList();
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]) || "a0101"
						.equalsIgnoreCase(t_field[i]))) {
					// String datavalue = "";
					FieldItem fi = DataDictionary.getFieldItem(t_field[i]);
					// 修改：增加 "_0"

					if (fi != null) {
						fi = (FieldItem) fi.cloneItem();
						if (!isBcode()) {
                            fi.setCodesetid("0");
                        }
						fi.setItemid(this.getAppAttributeValue(HrSyncBo.A,
								t_field[i]));
						ret.add(fi);
					}
				}
			}
		}
		return ret;
	}

	public ArrayList getOrgCustomFieldsList() {
		ArrayList ret = new ArrayList();
		String t_fields = this.getTextValue(HrSyncBo.ORG_FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]))) {
					// String datavalue = "";
					FieldItem fi = DataDictionary.getFieldItem(t_field[i]);
					// 修改：增加 "_0"

					if (fi != null) {
						fi = (FieldItem) fi.cloneItem();
						if (!isBcode()) {
                            fi.setCodesetid("0");
                        }
						fi.setItemid(this.getAppAttributeValue(HrSyncBo.B,
								t_field[i]));
						ret.add(fi);
					}
				}
			}
		}
		return ret;
	}

	public ArrayList getPostCustomFieldsList() {
		ArrayList ret = new ArrayList();
		String t_fields = this.getTextValue(HrSyncBo.POST_FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]))) {
					// String datavalue = "";
					FieldItem fi = DataDictionary.getFieldItem(t_field[i]);
					// 修改：增加 "_0"

					if (fi != null) {
						fi = (FieldItem) fi.cloneItem();
						if (!isBcode()) {
                            fi.setCodesetid("0");
                        }
						fi.setItemid(this.getAppAttributeValue(HrSyncBo.K,
								t_field[i]));
						ret.add(fi);
					}
				}
			}
		}
		return ret;
	}

	public String getXMLFields() {
		String ret = "";

		return ret;
	}

	/**
	 * 获得要导出Excel的字段
	 * 
	 * @return
	 */
	public String getExportFields() {
		StringBuffer sb = new StringBuffer();
		sb.append("nbase,a0101,username,userpassword");
		String t_fields = this.getTextValue(HrSyncBo.FIELDS);
		if (!(t_fields == null || "".equals(t_fields))) {
			String[] t_field = t_fields.split(",");
			for (int i = 0; i < t_field.length; i++) {
				if (!(t_field[i] == null || "".equals(t_field[i]) || "a0101"
						.equalsIgnoreCase(t_field[i]))) {
					sb.append("," + t_field[i]);
				}
			}

		}
		return sb.toString();
	}

	/**
	 * 导出数据的SQL
	 * 
	 * @return
	 */
	public String getExportSql() {
		String fields = this.getExportFields();
		StringBuffer sb = new StringBuffer();
		sb.append("select " + fields
				+ " from t_hr_view order by nbase_0,B0110_0,E0122_0,a0101");
		return sb.toString();
	}

	public String getColumStr(RowSet rset, ResultSetMetaData rsetmd, String str)
			throws SQLException {
		int j = rset.findColumn(str);
		String temp = null;
		switch (rsetmd.getColumnType(j)) {

		case Types.DATE:
			temp = PubFunc.FormatDate(rset.getDate(j));
			break;
		case Types.TIMESTAMP:
			temp = PubFunc.FormatDate(rset.getDate(j), "yyyy-MM-dd hh:mm:ss");
			if (temp.indexOf("12:00:00") != -1) {
                temp = PubFunc.FormatDate(rset.getDate(j));
            }
			break;
		case Types.CLOB:
			temp = Sql_switcher.readMemo(rset, rsetmd.getColumnName(j));
			break;
		case Types.BLOB:
			temp = "二进制文件";
			break;
		case Types.NUMERIC:
			int preci = rsetmd.getScale(j);
			temp = String.valueOf(rset.getDouble(j));
			temp = PubFunc.DoFormatDecimal(temp, preci);
			break;
		default:
			temp = rset.getString(j);
			break;
		}
		return temp;
	}

	/**
	 * 处理小数点位数
	 * 
	 * @param desc
	 * @param decimalwidth
	 * @return
	 */
	public String getFloat(String desc, int decimalwidth) {
		String fielddesc = "";
		StringBuffer temp = new StringBuffer("#0.");
		for (int i = 0; i < decimalwidth; i++) {
			temp.append("0");
		}

		DecimalFormat format = new DecimalFormat(temp.toString());
		double a = 0;
		if (desc != null && desc.trim().length() > 0) {
			a = Double.parseDouble(desc);
			fielddesc = format.format(a);
		}

		return fielddesc;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public boolean isBcode() {
		return bcode;
	}

	public String filtration(String fields) {
		String[] field = fields.split(",");
		String rfileds = "";
		for (int i = 0; i < field.length; i++) {
			FieldItem item = DataDictionary.getFieldItem(field[i]);
			if (item == null) {
                continue;
            }
			if ("0".equalsIgnoreCase(item.getUseflag())) {
                continue;
            }
			if (!item.isCode()) {
                continue;
            }
			rfileds += field[i] + ",";
		}
		if (rfileds.length() > 0) {
            rfileds = rfileds.substring(0, rfileds.length() - 1);
        }
		return rfileds;
	}

	/**
	 * 
	 * @param type
	 * @param fields
	 * @return
	 */
	public String Customtransition(int type, String fields) {
		String[] field = fields.split(",");
		String rfileds = "";
		for (int i = 0; i < field.length; i++) {
			rfileds += this.getAppAttributeValue(type, field[i]) + ",";
		}
		if (rfileds.length() > 0) {
            rfileds = rfileds.substring(0, rfileds.length() - 1);
        }
		return rfileds;
	}

	/**
	 * 用唯一性指标同步
	 * 
	 * @param codefields
	 */
	private void empSynchronizationOnlyFiled(String codefields, String onlyfield) {
		String t_onlyfield = Customtransition(HrSyncBo.A, onlyfield);
		ContentDAO dao = new ContentDAO(this.conn);
		String[] ss = codefields.split(",");
		ArrayList codelist = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		
		ArrayList sysList = selectSysid2();
		for (int i = 0; i < ss.length; i++) {
			String codefield = ss[i];
			FieldItem item = DataDictionary.getFieldItem(codefield);
			if (item == null) {
                continue;
            }
			String setname = item.getFieldsetid();
			if (codelist.indexOf(setname) != -1) {
                continue;
            }
			codelist.add(setname);
		}
		String fields = getMainSetFields();
		
		String columns = getTextValue(HrSyncBo.FIELDS);//获得已选的人员字段
		// 翻译型代码
		String codefieldstr = "";
		if (!isBcode()) {//判断是否选择的全部翻译成代码描述
			codefieldstr = filtration(columns);//获得全部的翻译型代码
		} else {
			codefieldstr = getTextValue(HrSyncBo.CODE_FIELDS);//获得选择的翻译型代码
		}
		Map tranfieldsMap = getFields(codefieldstr, "HR_FLAG");//整理字段按照表归类
		Map fieldsMap = getFields(columns, "HR_FLAG");//整理字段按照表归类
		
		Map a01Trans = (HashMap) tranfieldsMap.get("A01");
		Map a01Fields = (HashMap) fieldsMap.get("A01");
		
		
//		String customfields = getCustomMainSetFiedlds();
		for (int i = 0; i < dblist.size(); i++) {
			String dbname = (String) dblist.get(i);
			String hz_dbname = AdminCode.getCodeName("@@", dbname);
			StringBuffer sql = new StringBuffer();
			sql.append("insert into t_hr_view");
			sql.append("(unique_id,nbase,nbase_0,A0100,");
			sql.append("B0110_0,E0122_0,A0101,E01A1_0,b0110_code,e0122_code,e01a1_code");
			sql.append(",username,userpassword,flag,sDate");
			Iterator it = a01Fields.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry)it.next();
				String cloumn = (String) entry.getKey();
				if ("A0101".equalsIgnoreCase(cloumn)) {
					continue;
				}
				
				sql.append(",");
				
				sql.append(getAppAttributeValue(A,cloumn));
			}
			
			if (sysList.size() > 0) {
				for (int j = 0; j < sysList.size(); j++) {
					sql.append(",");
					sql.append(sysList.get(j)); 
				}
			}
			
			sql.append(")");
			sql.append("( select guidkey,'" + hz_dbname + "','"
					+ dbname + "',");
			sql.append(" A0100,B0110,E0122,A0101,E01A1,(select corcode from organization where codeitemid=A.b0110) b0110_code,(select corcode from organization where codeitemid=A.e0122) e0122_code,(select corcode from organization where codeitemid=A.e01a1) e01a1_code,");
			sql.append(getUserPwd());
			sql.append(",1 ");
			sql.append("," + Sql_switcher.dateValue(datestr));

			Iterator it2 = a01Fields.entrySet().iterator();
			while (it2.hasNext()) {
				Entry entry = (Entry)it2.next();
				
				String cloumn = (String) entry.getKey();
				String type = (String) entry.getValue();
				if ("A0101".equalsIgnoreCase(cloumn)) {
					continue;
				}
				sql.append(",");
				// 需要将代码转为汉子的指标
				if (a01Trans != null && a01Trans.containsKey(cloumn.toUpperCase())){
					RowSet rs = null;
					try {
						if (Sql_switcher.searchDbServer() == 1) { // sql server 库 查询 FUN_GET_CODEDESC 函数是否存在  wangb 20171227
							rs = dao.search("select 1 from dbo.sysobjects where id = object_id('[dbo].[FUN_GET_CODEDESC]') and xtype='FN'");
							if(rs.next()) {
                                sql.append("dbo.FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + cloumn + "', A." + cloumn + ") ");
                            }
						} else if(Sql_switcher.searchDbServer() == 2){// oracle 库 查询 FUN_GET_CODEDESC 函数是否存在  wangb 20171227
							rs = dao.search("select 1 from user_objects where object_type='FUNCTION' and object_name='FUN_GET_CODEDESC'");
							if(rs.next()) {
                                sql.append("FUN_GET_CODEDESC('A01','" + cloumn + "', A." + cloumn + ",null,null) ");
                            }
						}else{
							sql.append("FUN_GET_CODEDESC('A01','" + cloumn + "', A." + cloumn + ",null,null) ");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						PubFunc.closeResource(rs);
					}
					sql.append(cloumn);
				} else {
					sql.append(cloumn);
				}
			}
			
			if (sysList.size() > 0) {
				for (int j = 0; j < sysList.size(); j++) {
					sql.append(",");
					sql.append("1");
				}
			}
			
			sql.append(" from " + dbname + "a01 A");

			sql.append(" WHERE  NOT EXISTS( ");
			sql.append(" SELECT * FROM t_hr_view t WHERE  A.guidkey" 
					+ "=t." + onlyfield + " ) ");

			sql.append(")");
			try {
				/** ****检查新增人员***** */
				dao.insert(sql.toString(), new ArrayList());
				/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
//				importSubSetData("t_hr_view", dbname, "9");
				updateData(fieldsMap,tranfieldsMap, dbname, "A",sysList);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql.setLength(0);
//			try {
//				this.importData("t_hr_view_t");
//			} catch (GeneralException e1) {
//				e1.printStackTrace();
//			}
			
			
			jzUpdate(dbname,sysList);
		}
		jzDel();
		deletedData(dblist,"A",sysList);
	}
	
	private void jzDel(){
		String isJz = getAttributeValue(HrSyncBo.JZ_FIELD);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 /**兼职参数*/
		String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid").toUpperCase();
		 /** 兼职单位 */
		String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
		 /** 兼职部门 */
		String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
		/**兼职岗位*/
		String post_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		/**任免标志*/
		String appoint_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
		/**排序*/
		String order_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		
		if("1".equals(isJz)){
			if(setid != null && setid.length() > 0 && unit_field != null && unit_field.length() > 0 && dept_field != null && dept_field.length() > 0 && appoint_filed != null && appoint_filed.length() > 0 && post_field != null && post_field.length() > 0){
				
			} else {
				return;
			}
		} else {
			return;
		}
		
		StringBuffer sql = new StringBuffer();
		if("1".equals(isJz)){
			
			sql.append("update t_hr_view set jz_field='' where not exists(select 1 from (");
			for (int i = 0; i <dblist.size(); i++ ) {
				String dbName = (String) dblist.get(i);
				if (i != 0) {
					sql.append(" union all ");
				}
				sql.append(" SELECT a0100,'" + dbName + "' nbase  FROM " + dbName + setid + " WHERE "+ appoint_filed +"='0' group by a0100");
				
			}
			
			sql.append(") a where a.a0100=t_hr_view.a0100 and upper(a.nbase)=upper(t_hr_view.nbase_0)) and jz_field is not null");
			
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				dao.update(sql.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 兼职的更新
	 * @param dbName
	 * @param sysList
	 */
	private void jzUpdate(String dbName, ArrayList sysList) {
		String isJz = getAttributeValue(HrSyncBo.JZ_FIELD);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 /**兼职参数*/
		String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid").toUpperCase();
		 /** 兼职单位 */
		String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
		 /** 兼职部门 */
		String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
		/**兼职岗位*/
		String post_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		/**任免标志*/
		String appoint_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
		/**排序*/
		String order_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		
		if("1".equals(isJz)){
			if(setid != null && setid.length() > 0 && unit_field != null && unit_field.length() > 0 && dept_field != null && dept_field.length() > 0 && appoint_filed != null && appoint_filed.length() > 0 && post_field != null && post_field.length() > 0){
				
			} else {
				return;
			}
		} else {
			return;
		}
		
		
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			Map dataMap = new HashMap();
			
			if("1".equals(isJz) && "".equals(post_field)){
				
				if (order_filed != null && order_filed.length() > 0) {
					sql.append(" SELECT a0100," + unit_field + "," + dept_field + " FROM " + dbName + setid + " WHERE "+ appoint_filed +"='0' order by " + order_filed + ";\n");
				} else {
					sql.append(" SELECT a0100," + unit_field + "," + dept_field + " FROM " + dbName + setid + " WHERE "+ appoint_filed +"='0' order by i9999;\n");
				}
				
				rs = dao.search(sql.toString());
				
				while (rs.next()) {
					String a0100 = rs.getString("a0100");
					
					String unit = rs.getString(unit_field);
					
					String dept = rs.getString(dept_field);
					
					if (unit != null && unit.length() > 0 && dept != null && dept.length() > 0) {
						if (dataMap.containsKey(dbName + "," + a0100)) {
							String jzStr = (String) dataMap.get(dbName + "," + a0100);
							dataMap.put(dbName + "," + a0100, jzStr + ",/" + unit + "/" + dept + "/");
						} else {
							dataMap.put(dbName + "," + a0100, "/" + unit + "/" + dept + "/");
						}
					}
				}
	
			}else if("1".equals(isJz) && !"".equals(post_field)){
				if (order_filed != null && order_filed.length() > 0) {
					sql.append(" SELECT a0100," + unit_field + "," + dept_field + "," + post_field + " FROM " + dbName + setid + " WHERE "+ appoint_filed +"='0' order by " + order_filed + "");
				} else {
					sql.append(" SELECT a0100," + unit_field + "," + dept_field + "," + post_field + " FROM " + dbName + setid + " WHERE "+ appoint_filed +"='0' order by i9999");
				}
				
				rs = dao.search(sql.toString());
				
				while (rs.next()) {
					String a0100 = rs.getString("a0100");
					
					String unit = rs.getString(unit_field);
					
					String dept = rs.getString(dept_field);
					
					String post = rs.getString(post_field);
					
					if (unit != null && unit.length() > 0 && dept != null && dept.length() > 0 && post != null && post.length() > 0) {
						if (dataMap.containsKey(dbName + "," + a0100)) {
							String jzStr = (String) dataMap.get(dbName + "," + a0100);
							dataMap.put(dbName + "," + a0100, jzStr + ",/" + unit + "/" + dept + "/" + post + "/");
						} else {
							dataMap.put(dbName + "," + a0100, "/" + unit + "/" + dept + "/" + post + "/");
						}
					}
				}
	
			}
			
			
			sql.delete(0, sql.length());
			
			sql.append("update t_hr_view set jz_field=?,flag=(case when flag=0 then 2 else flag end)");
			
			for (int i = 0; i < sysList.size(); i++) {
				sql.append(",");
				sql.append(sysList.get(i));
				sql.append("=(case when ");
				sql.append(sysList.get(i));
				sql.append("=0 then 2 else ");
				sql.append(sysList.get(i));
				sql.append(" end");
				sql.append(")");
				
			}
			
			sql.append("where a0100=? and upper(nbase_0)=? and jz_field<>?");
			
			Iterator it = dataMap.entrySet().iterator();
			ArrayList list = new ArrayList();
			ArrayList list2 = new ArrayList();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				
				String[] str = key.split(",");
				
				ArrayList temp = new ArrayList();
				ArrayList temp2 = new ArrayList();
				temp.add(value);
				temp.add(str[1]);
				temp.add(str[0].toUpperCase());
				temp.add(value);
				
				list.add(temp);
				list2.add(temp2);
			}
			
			dao.batchUpdate(sql.toString(), list);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

	private void deletedData(ArrayList dbList,String type,ArrayList sysList) {
		StringBuffer sql = new StringBuffer();
		
		// 源表
		String srcTable = "";
		//目标表
		String desTable = "";
		// 清空某字段
		String clearnFile = "";
		if ("A".equals(type)) {
			for (int i = 0; i < dbList.size(); i++) {
				String dbName = (String)dbList.get(i);
				if (i != 0) {
					srcTable += " union all ";
				}
				
				srcTable += "select guidkey from " + dbName + "A01";
			}
			
			srcTable = "(" + srcTable + ")";
			desTable = "t_hr_view";
			clearnFile = "a0101";
		} else if ("B".equals(type)) {
			srcTable = "(select guidkey from organization where (codesetid='UM' or codesetid='UN') and " +Sql_switcher.sqlNow() + " between start_date and end_date )";
			desTable = "t_org_view";
			clearnFile = "codeitemdesc";
		} else if ("K".equals(type)) {
			srcTable = "(select guidkey from organization where (codesetid='@K') and " +Sql_switcher.sqlNow() + " between start_date and end_date )";
			desTable = "t_post_view";
			clearnFile = "codeitemdesc";
		}
		
		sql.append("update ");
		sql.append(desTable);
		sql.append(" set flag =3");
		sql.append(",");
		sql.append(clearnFile);
		sql.append("=(case when ");
		sql.append(Sql_switcher.substr(clearnFile, Sql_switcher.length(clearnFile) + "- 3", "4"));
		sql.append("='(已删)' then " + clearnFile +" else ");
		sql.append(clearnFile);
		sql.append(Sql_switcher.concat());
		
		sql.append("'(已删)' end)");
		
		for (int i = 0; i < sysList.size(); i++) {
			sql.append(",");
			sql.append(sysList.get(i));
			sql.append("=3");
		}
		
		sql.append(" where " + Sql_switcher.substr(clearnFile, Sql_switcher.length(clearnFile) + "- 3", "4") + " <>'(已删)' and not exists(select 1 from " +srcTable+ " a where a.guidkey=" + desTable + ".unique_id)");
		
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新子集数据
	 * @param map
	 * @param transMap
	 * @param dbName
	 */
	private void updateData(Map map,Map transMap, String dbName, String type, ArrayList sysList) {

		RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	    String fielduser=user_vo.getString("str_value");
		
	    //验证用户名和密码字段
		String Userfield = "username";
		String PWfield = "UserPassword";
			     
		if(fielduser != null && fielduser.indexOf(",")>0  ){
			Userfield = fielduser.substring(0,fielduser.indexOf(","));
			if ("".equals(Userfield) || "#".equals(Userfield)) {
                Userfield = "username";
            }
					 
				PWfield = fielduser.substring(fielduser.indexOf(",")+1);
				if ("".equals(PWfield) || "#".equals(PWfield)) {
                    PWfield = "UserPassword";
                }
		}
		
		
		Iterator it = map.entrySet().iterator();
		//视图配置指标时，需要取自定义指标，不然查询sql报错字段找不到 wangbo 2019-12-26 
		int ZDY_TYPE = 0;
		while (it.hasNext()) {
			Entry entry = (Entry)it.next();
			String key = (String) entry.getKey();
			Map subMap = (HashMap) entry.getValue();
			Map subTransMap = (HashMap) transMap.get(key);
			StringBuffer sql = new StringBuffer();
			if (key.startsWith(type)) {
				
				// 需要更新的目标表
				String desTable = "";
				// 源表
				String srcTable = "";
				
				if ("A".equals(type)) {
					desTable = "t_hr_view";
					srcTable = dbName + key;
					ZDY_TYPE = A;
				} else if ("B".equals(type)) {
					desTable = "t_org_view";
					srcTable = key;
					ZDY_TYPE = B;
				} else if ("K".equalsIgnoreCase(type)) {
					desTable = "t_post_view";
					srcTable = key;
					ZDY_TYPE = K;
				}
				
				
				sql.append("update ");
				sql.append(desTable);
				sql.append(" set ");
				
				if ("A01".equals(key)) {
					String hz_dbname = AdminCode.getCodeName("@@", dbName);
					String a0101 = getAppAttributeValue(ZDY_TYPE, "A0101");
					a0101 = "".equalsIgnoreCase(a0101)? "A0101":a0101;
					if (Sql_switcher.searchDbServer() == 1) {
						String a01_str = "A0101".equalsIgnoreCase(a0101)? a0101: "a0101=a.0101,"+a0101;
					sql.append("sdate=getdate(),nbase='" + hz_dbname + "',nbase_0='" +dbName+ "',A0100=a.a0100,a0000=a.a0000,B0110_0=a.b0110,E0122_0=a.e0122,"+a01_str+"=a.a0101,E01A1_0=a.e01a1,"
						+ "username=a."+Userfield+",userpassword=a."+PWfield);
					
					Iterator its = subMap.entrySet().iterator();
					while (its.hasNext()) {
						Entry ent = (Entry)its.next();
						String field = (String) ent.getKey();
						String codeset = (String) ent.getValue();
						if ("a0101".equalsIgnoreCase(field)) {
							continue;
						}
						
						sql.append(",");
						if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
							sql.append(getAppAttributeValue(A, field));
							sql.append("=");
							if(Sql_switcher.searchDbServer() == 1) {
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + field + "', a." + field + ") ");
							} else {
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) ");
							}
						} else {
							sql.append(getAppAttributeValue(A, field));
							sql.append("=");
							sql.append("a.");
							sql.append(field);
						}
					}
					
					if (sysList.size() > 0) {
						for (int i = 0; i < sysList.size(); i++) {
							String sys = (String)sysList.get(i);
							sql.append(",");
							sql.append(sys);
							sql.append("=");
							sql.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
						}
					}
					
					sql.append(" from ");
					sql.append(srcTable);
					sql.append(" a where a.guidkey=");
					sql.append(desTable);
					sql.append(".unique_id and (nbase<>'" + hz_dbname + "' or nbase_0<>'" +dbName+ "' or " + desTable + ".A0100<>a.a0100");
					sql.append(" or ((" + desTable + ".a0000<>a.a0000 and " + desTable + ".a0000 is not null and a.a0000 is not null) or (" + desTable + ".a0000 is null and a.a0000 is not null) or (" + desTable + ".a0000 is not null and a.a0000 is null))");
					sql.append(" or ((B0110_0<>a.b0110 and B0110_0 is not null and a.b0110 is not null) or (B0110_0 is null and a.b0110 is not null) or (B0110_0 is not null and a.b0110 is null))");
					sql.append(" or ((E0122_0<>a.e0122 and E0122_0 is not null and a.e0122 is not null) or (E0122_0 is null and a.e0122 is not null) or (E0122_0 is not null and a.e0122 is null))");
					sql.append(" or ((" + desTable + ".A0101<>a.a0101 and " + desTable + ".A0101 is not null and a.a0101 is not null) or (" + desTable + ".A0101 is null and a.a0101 is not null) or (" + desTable + ".A0101 is not null and a.a0101 is null))");
					sql.append(" or ((E01A1_0<>a.e01a1 and E01A1_0 is not null and a.e01a1 is not null) or (E01A1_0 is null and a.e01a1 is not null) or (E01A1_0 is not null and a.e01a1 is null))");
					sql.append(" or ((" + desTable + ".username<>a."+Userfield+" and " + desTable + ".username is not null and a."+Userfield+" is not null) or (" + desTable + ".username is null and a."+Userfield+" is not null) or (" + desTable + ".username is not null and a."+Userfield+" is null))");
					sql.append(" or ((" + desTable + ".userpassword<>a."+PWfield+" and " + desTable + ".userpassword is not null and a."+PWfield+" is not null) or (" + desTable + ".userpassword is null and a."+PWfield+" is not null) or (" + desTable + ".userpassword is not null and a."+PWfield+" is null))");
					Iterator its2 = subMap.entrySet().iterator();				
					while (its2.hasNext()) {
						Entry ent = (Entry)its2.next();
						String field = (String) ent.getKey();
						String codeset = (String) ent.getValue();
						sql.append(" or ");
						if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
							sql.append("((" + desTable + ".");
							sql.append(getAppAttributeValue(A, field));
							sql.append("<>");
							if(Sql_switcher.searchDbServer() == 1) {
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + field + "', a." + field + ") and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
							} else {
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
							}
							if(Sql_switcher.searchDbServer() == 1) {
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + field + "', a." + field + ") is not null");
							} else {
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is not null");
							}
							sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
							if(Sql_switcher.searchDbServer() == 1) {
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + field + "', a." + field + ") is not null");
							} else {
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is not null");
							}
							
							sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
							if(Sql_switcher.searchDbServer() == 1) {
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'A01','" + field + "', a." + field + ") is null");
							} else {
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is null");
							}
							sql.append("))");
						} else {
							sql.append("((" + desTable + ".");
							sql.append(getAppAttributeValue(A, field));
							sql.append("<>");
							sql.append("a.");
							sql.append(field);
							sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
							sql.append(")");
						}
					}
					
					
					
					sql.append(")");
					
					} else {
						StringBuffer sql2 = new StringBuffer();
						String a01_str = "A0101".equalsIgnoreCase(a0101)? a0101: "a0101,"+a0101;
						String a01_value = "A0101".equalsIgnoreCase(a0101)? "a0101": "a0101,a0101";
						sql.append("(sdate,nbase,nbase_0,A0100,a0000,B0110_0,E0122_0,"+a01_str+",E01A1_0,"
							+ "username,userpassword");
						sql2.append("=(select sysdate,'" + hz_dbname + "','" +dbName+ "' ,a0100,a0000,b0110,e0122,"+a01_value+",e01a1,"+Userfield+","+PWfield + "");
						Iterator its = subMap.entrySet().iterator();
						while (its.hasNext()) {
							Entry ent = (Entry)its.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							if ("a0101".equalsIgnoreCase(field)) {
								continue;
							}
							
							sql.append(",");
							sql2.append(",");
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								
								sql2.append("FUN_GET_CODEDESC('A01','" + field + "'," + field + ",null,null) ");

							} else {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
	
								sql2.append(field);
							}
						}
						
						if (sysList.size() > 0) {
							for (int i = 0; i < sysList.size(); i++) {
								String sys = (String)sysList.get(i);
								sql.append(",");
								sql2.append(",");
								sql.append(sys);
								
								sql2.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
							}
						}
							
						sql.append(")");
						
						sql2.append(" from ");
						sql2.append(srcTable);
						sql2.append(" where " + srcTable + ".guidkey=");
						sql2.append(desTable);
						sql2.append(".unique_id)");
						
						
						sql.append(sql2.toString());
						
						sql.append(" where exists(select 1 from " + srcTable + " a where a.guidkey=" + desTable + ".unique_id and ( nbase<>'" + hz_dbname + "' or nbase_0<>'" +dbName+ "' or " + desTable + ".A0100<>a.a0100");
						sql.append(" or ((" + desTable + ".a0000<>a.a0000 and " + desTable + ".a0000 is not null and a.a0000 is not null) or (" + desTable + ".a0000 is null and a.a0000 is not null) or (" + desTable + ".a0000 is not null and a.a0000 is null))");
						sql.append(" or ((B0110_0<>a.b0110 and B0110_0 is not null and a.b0110 is not null) or (B0110_0 is null and a.b0110 is not null) or (B0110_0 is not null and a.b0110 is null))");
						sql.append(" or ((E0122_0<>a.e0122 and E0122_0 is not null and a.e0122 is not null) or (E0122_0 is null and a.e0122 is not null) or (E0122_0 is not null and a.e0122 is null))");
						sql.append(" or ((" + desTable + ".A0101<>a.a0101 and " + desTable + ".A0101 is not null and a.a0101 is not null) or (" + desTable + ".A0101 is null and a.a0101 is not null) or (" + desTable + ".A0101 is not null and a.a0101 is null))");
						sql.append(" or ((E01A1_0<>a.e01a1 and E01A1_0 is not null and a.e01a1 is not null) or (E01A1_0 is null and a.e01a1 is not null) or (E01A1_0 is not null and a.e01a1 is null))");
						sql.append(" or ((" + desTable + ".username<>a."+Userfield+" and " + desTable + ".username is not null and a."+Userfield+" is not null) or (" + desTable + ".username is null and a."+Userfield+" is not null) or (" + desTable + ".username is not null and a."+Userfield+" is null))");
						sql.append(" or ((" + desTable + ".userpassword<>a."+PWfield+" and " + desTable + ".userpassword is not null and a."+PWfield+" is not null) or (" + desTable + ".userpassword is null and a."+PWfield+" is not null) or (" + desTable + ".userpassword is not null and a."+PWfield+" is null))");
						Iterator its2 = subMap.entrySet().iterator();				
						while (its2.hasNext()) {
							Entry ent = (Entry)its2.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							sql.append(" or ");
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(A, field));
								sql.append("<>");
								
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								
								
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is not null");
								
								sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
								
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is not null");
								
								
								sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								
								sql.append("FUN_GET_CODEDESC('A01','" + field + "', a." + field + ",null,null) is null");
								
								sql.append("))");
							} else {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(A, field));
								sql.append("<>");
								sql.append("a.");
								sql.append(field);
								sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
								sql.append(")");
							}
						}
							 
						sql.append("))");	
							
					}
				} else if ("B01".equals(key) || "K01".equals(key)) {
					if (Sql_switcher.searchDbServer() == 1) {
						Iterator its = subMap.entrySet().iterator();
						while (its.hasNext()) {
							Entry ent = (Entry)its.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("=");
								if(Sql_switcher.searchDbServer() == 1) {
									sql.append("dbo.");
									sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") ");
								} else {
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) ");
								}
							} else {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("=");
								sql.append("a.");
								sql.append(field);
							}
							
							if (its.hasNext()) {
								sql.append(",");
							}
						}
						
						if (sysList.size() > 0) {
							for (int i = 0; i < sysList.size(); i++) {
								String sys = (String)sysList.get(i);
								sql.append(",");
								sql.append(sys);
								sql.append("=");
								sql.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
							}
						}
						
						sql.append(" from ");
						sql.append(srcTable);
						if ("B01".equals(key)) {
							sql.append(" a where a.b0110=");
							sql.append(desTable);
							sql.append(".b0110_0");
						} else  if ("K01".equals(key)) {
							sql.append(" a where a.e01a1=");
							sql.append(desTable);
							sql.append(".e01a1_0");
						}
						sql.append(" and (");
						Iterator its2 = subMap.entrySet().iterator();
											
						while (its2.hasNext()) {
							Entry ent = (Entry)its2.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("<>");
								if(Sql_switcher.searchDbServer() == 1) {
									sql.append("dbo.");
									sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								} else {
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								}
								if(Sql_switcher.searchDbServer() == 1) {
									sql.append("dbo.");
									sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is not null");
								} else {
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
								}
								sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
								if(Sql_switcher.searchDbServer() == 1) {
									sql.append("dbo.");
									sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is not null");
								} else {
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
								}
								
								sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								if(Sql_switcher.searchDbServer() == 1) {
									sql.append("dbo.");
									sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is null");
								} else {
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is null");
								}
								sql.append("))");
							} else {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("<>");
								sql.append("a.");
								sql.append(field);
								sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
								sql.append(")");
							}
							
							if (its2.hasNext()) {
								sql.append(" or ");
							}
						}
						
											
						sql.append(")");
					} else {
						StringBuffer sql2 = new StringBuffer();
						sql2.append("=(select ");
						sql.append("(");
						Iterator its = subMap.entrySet().iterator();
						while (its.hasNext()) {
							Entry ent = (Entry)its.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));

								sql2.append("FUN_GET_CODEDESC('" + key + "','" + field + "', " + field + ",null,null) ");
								
							} else {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql2.append(field);
							}
							
							if (its.hasNext()) {
								sql.append(",");
								sql2.append(",");
							}
						}
						
						if (sysList.size() > 0) {
							for (int i = 0; i < sysList.size(); i++) {
								String sys = (String)sysList.get(i);
								sql.append(",");
								sql2.append(",");
								sql.append(sys);

								sql2.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
							}
						}
						sql.append(")");
						
						sql2.append(" from ");
						sql2.append(srcTable);
						sql2.append(" where ");
						
						

						if ("B01".equals(key)) {
							sql2.append(" b0110=");
							sql2.append(desTable);
							sql2.append(".b0110_0");
						} else  if ("K01".equals(key)) {
							sql2.append(" e01a1=");
							sql2.append(desTable);
							sql2.append(".e01a1_0");
						}
						
						sql2.append(")");
						
						sql.append(sql2.toString());
						
						sql.append(" where exists(select 1 from " + srcTable + " a where");
						
						if ("B01".equals(key)) {
							sql.append(" a.b0110=");
							sql.append(desTable);
							sql.append(".b0110_0");
						} else  if ("K01".equals(key)) {
							sql.append(" a.e01a1=");
							sql.append(desTable);
							sql.append(".e01a1_0");
						}

						Iterator its2 = subMap.entrySet().iterator();
						
						if (its2.hasNext()) {
							sql.append(" and (");
						
											
							while (its2.hasNext()) {
								Entry ent = (Entry)its2.next();
								String field = (String) ent.getKey();
								String codeset = (String) ent.getValue();
								
								if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
									sql.append("((" + desTable + ".");
									sql.append(getAppAttributeValue(ZDY_TYPE, field));
									sql.append("<>");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
									
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
									
									sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
									
									
									sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is null");
									
									sql.append("))");
								} else {
									sql.append("((" + desTable + ".");
									sql.append(getAppAttributeValue(ZDY_TYPE, field));
									sql.append("<>");
									sql.append("a.");
									sql.append(field);
									sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
									sql.append(")");
								}
								
								if (its2.hasNext()) {
									sql.append(" or ");
								}
							}
							sql.append(")");
						
						}
											
						sql.append(")");
					
						
					}
				} else if (key.startsWith("A") || key.startsWith("B") || key.startsWith("K")) {
					if (Sql_switcher.searchDbServer() == 1) {
						Iterator its = subMap.entrySet().iterator();
						while (its.hasNext()) {
							Entry ent = (Entry)its.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("=");
							
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") ");
								
							} else {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("=");
								sql.append("a.");
								sql.append(field);
							}
							
							if (its.hasNext()) {
								sql.append(",");
							}
						}
						
						if (sysList.size() > 0) {
							for (int i = 0; i < sysList.size(); i++) {
								String sys = (String)sysList.get(i);
								sql.append(",");
								sql.append(sys);
								sql.append("=");
								sql.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
							}
						}
						
						sql.append(" from ");
						if (key.startsWith("A")){
							sql.append("(select n.* from (select A0100,MAX(I9999) i9999 from " +srcTable + " group by a0100) m left join " + srcTable + " n on m.A0100=n.a0100 and m.i9999=n.I9999)");
							sql.append(" a where a.a0100=");
							sql.append(desTable);
							sql.append(".a0100 and upper(nbase_0)='" +dbName.toUpperCase()+ "'");
						} else if (key.startsWith("B")) {
							sql.append("(select n.* from (select b0110,MAX(I9999) i9999 from " +srcTable + " group by b0110) m left join " + srcTable + " n on m.b0110=n.b0110 and m.i9999=n.I9999)");
							sql.append(" a where a.b0110=");
							sql.append(desTable);
							sql.append(".b0110_0");
						} else if (key.startsWith("K")) {
							sql.append("(select n.* from (select e01a1,MAX(I9999) i9999 from " +srcTable + " group by e01a1) m left join " + srcTable + " n on m.e01a1=n.e01a1 and m.i9999=n.I9999)");
							sql.append(" a where a.e01a1=");
							sql.append(desTable);
							sql.append(".e01a1_0");
						}
	
						sql.append(" and (");
						Iterator its2 = subMap.entrySet().iterator();				
						while (its2.hasNext()) {
							Entry ent = (Entry)its2.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("<>");
							
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								
								
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is not null");
								
								sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
								
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is not null");
								
								
								sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
								
								sql.append("dbo.");
								sql.append("FUN_GET_CODEDESC(" + Sql_switcher.sqlNow() + ",'" + key + "','" + field + "', a." + field + ") is null");
								
								sql.append("))");
							} else {
								sql.append("((" + desTable + ".");
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								sql.append("<>");
								sql.append("a.");
								sql.append(field);
								sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
								sql.append(")");
							}
							
							if (its2.hasNext()) {
								sql.append(" or ");
							}
						}
						
						
						
						sql.append(")");
					} else {
						
						StringBuffer sql2 = new StringBuffer();
						sql2.append("=(select ");
						sql.append("(");
						Iterator its = subMap.entrySet().iterator();
						while (its.hasNext()) {
							Entry ent = (Entry)its.next();
							String field = (String) ent.getKey();
							String codeset = (String) ent.getValue();
							
							if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));
								
								
								sql2.append("FUN_GET_CODEDESC('" + key + "','" + field + "', " + field + ",null,null) ");
								
							} else {
								sql.append(getAppAttributeValue(ZDY_TYPE, field));

								sql2.append(field);
							}
							
							if (its.hasNext()) {
								sql.append(",");
								sql2.append(",");
							}
						}
						
						if (sysList.size() > 0) {
							for (int i = 0; i < sysList.size(); i++) {
								String sys = (String)sysList.get(i);
								sql.append(",");
								sql2.append(",");
								sql.append(sys);

								sql2.append("(case when "+sys+"=0 then 2  else  " + sys + " end)");
							}
						}
						sql.append(")");
						sql2.append(" from ");
						if (key.startsWith("A")){
							sql2.append("(select n.* from (select A0100,MAX(I9999) i9999 from " +srcTable + " group by a0100) m left join " + srcTable + " n on m.A0100=n.a0100 and m.i9999=n.I9999)");
							sql2.append(" b where b.a0100=");
							sql2.append(desTable);
							sql2.append(".a0100 and upper(nbase_0)='" +dbName.toUpperCase()+ "'");
						} else if (key.startsWith("B")) {
							sql2.append("(select n.* from (select b0110,MAX(I9999) i9999 from " +srcTable + " group by b0110) m left join " + srcTable + " n on m.b0110=n.b0110 and m.i9999=n.I9999)");
							sql2.append(" b where b.b0110=");
							sql2.append(desTable);
							sql2.append(".b0110_0");
						} else if (key.startsWith("K")) {
							sql2.append("(select n.* from (select e01a1,MAX(I9999) i9999 from " +srcTable + " group by e01a1) m left join " + srcTable + " n on m.e01a1=n.e01a1 and m.i9999=n.I9999)");
							sql2.append(" b where b.e01a1=");
							sql2.append(desTable);
							sql2.append(".e01a1_0");
						}
						
						sql2.append(")");
						
						sql.append(sql2.toString());
						sql.append(" where exists(select 1 from ");
						
						
						if (key.startsWith("A")){
							sql.append("(select n.* from (select A0100,MAX(I9999) i9999 from " +srcTable + " group by a0100) m left join " + srcTable + " n on m.A0100=n.a0100 and m.i9999=n.I9999)");
							sql.append(" a ");
						} else if (key.startsWith("B")) {
							sql.append("(select n.* from (select b0110,MAX(I9999) i9999 from " +srcTable + " group by b0110) m left join " + srcTable + " n on m.b0110=n.b0110 and m.i9999=n.I9999)");
							sql.append(" a ");

						} else if (key.startsWith("K")) {
							sql.append("(select n.* from (select e01a1,MAX(I9999) i9999 from " +srcTable + " group by e01a1) m left join " + srcTable + " n on m.e01a1=n.e01a1 and m.i9999=n.I9999)");
							sql.append(" a  ");
						}
	
						sql.append(" where  ");
						
						if (key.startsWith("A")){
							
							sql.append(" a.a0100=");
							sql.append(desTable);
							sql.append(".a0100 and upper(nbase_0)='" +dbName.toUpperCase()+ "'");
						} else if (key.startsWith("B")) {
							
							sql.append(" a.b0110=");
							sql.append(desTable);
							sql.append(".b0110_0");
						} else if (key.startsWith("K")) {
							
							sql.append(" a.e01a1=");
							sql.append(desTable);
							sql.append(".e01a1_0");
						}
						
						Iterator its2 = subMap.entrySet().iterator();
						
						if (its2.hasNext()) {
							sql.append(" and (");
						
						
							while (its2.hasNext()) {
								Entry ent = (Entry)its2.next();
								String field = (String) ent.getKey();
								String codeset = (String) ent.getValue();
								
								if (subTransMap != null && subTransMap.containsKey(field.toUpperCase())) {
									sql.append("((" + desTable + ".");
									sql.append(getAppAttributeValue(ZDY_TYPE, field));
									sql.append("<>");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) and " + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
									
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
									
									sql.append(" ) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and ");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is not null");
									
									
									sql.append(") or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is not null and ");
									
									sql.append("FUN_GET_CODEDESC('" + key + "','" + field + "', a." + field + ",null,null) is null");
									
									sql.append("))");
								} else {
									sql.append("((" + desTable + ".");
									sql.append(getAppAttributeValue(ZDY_TYPE, field));
									sql.append("<>");
									sql.append("a.");
									sql.append(field);
									sql.append(" and " + desTable + "." + getAppAttributeValue(ZDY_TYPE, field) + " is not null and a."+ field +" is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is null and a." + field + " is not null) or (" + desTable + "."+getAppAttributeValue(ZDY_TYPE, field)+" is  not null and a." + field + " is null)");
									sql.append(")");
								}
								
								if (its2.hasNext()) {
									sql.append(" or ");
								}
							}
						
						sql.append(")");
						}
						
						sql.append(")");
											
						
						
					}
					
				}
						
				
				
			}
			
			try {
				sql.append(" and ");
				sql.append(this.getDelWhere(type, dblist));
				ContentDAO dao = new ContentDAO(this.conn);
//				System.out.println(sql.toString());
				dao.update(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	/**
	 * 按照表和字段的对应关系 整理字段
	 * @param columns
	 * @return
	 */
	private Map getFields(String columns, String uro) {
		String column[] = columns.split(",");
		StringBuffer s_columns = new StringBuffer();
		Map reMap = new HashMap();
		for (int i = 0; i < column.length; i++) {
			s_columns.append("'" + column[i].toUpperCase() + "',");
		}
		s_columns.deleteCharAt(s_columns.length() - 1);
		String sql = "SELECT FIELDSETID,ITEMID,ITEMDESC,itemtype,codesetid "
				+ "FROM fielditem WHERE UPPER(ITEMID) IN ("
				+ s_columns.toString() + ") AND USEFLAG = 1 "
				+ "ORDER BY FIELDSETID,ITEMID";
		ContentDAO dao = new ContentDAO(conn);
		columns = columns + ",";
		String upFIELDSETID = "";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String FIELDSETID = rs.getString("FIELDSETID");
				String ITEMID = rs.getString("ITEMID");
				String codeSetId = rs.getString("codesetid");
				String itemType = rs.getString("itemtype");
				
				if (FIELDSETID != null && FIELDSETID.length() > 0) {
					Map map = null;
					if(reMap.containsKey(FIELDSETID.toUpperCase())) {
						map = (HashMap)reMap.get(FIELDSETID.toUpperCase());
												
					} else {
						map = new HashMap();
					}
					
					if ("A".equals(itemType)) {
						map.put(ITEMID.toUpperCase(), codeSetId);
					} else {
						map.put(ITEMID.toUpperCase(), "0");
					}
					
					reMap.put(FIELDSETID.toUpperCase(), map);
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (s_columns.indexOf("B0110") != -1) {
			if ("HR_FLAG".equalsIgnoreCase(uro)) {
				Map aList = null;
				if (!reMap.containsKey("A01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("A01");
				}
				
				aList.put("B0110", "UN");
				reMap.put("A01", aList);
			} else if ("ORG_FLAG".equalsIgnoreCase(uro)) {
				Map aList = null;
				if (!reMap.containsKey("B01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("B01");
				}
				
				aList.put("B0110", "UN");
				reMap.put("B01", aList);
			}
		}
		if (s_columns.indexOf("E01A1") != -1) {
			if ("HR_FLAG".equalsIgnoreCase(uro)) {
				Map aList = null;
				if (!reMap.containsKey("A01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("A01");
				}
				
				aList.put("E01A1", "@K");
				reMap.put("A01", aList);
			}else if("POST_FLAG".equalsIgnoreCase(uro)){
				Map aList = null;
				if (!reMap.containsKey("K01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("K01");
				}
				
				aList.put("E01A1", "@K");
				reMap.put("K01", aList);
			}
		}
		if (s_columns.indexOf("E0122") != -1) {
			if ("HR_FLAG".equalsIgnoreCase(uro)){
				Map aList = null;
				if (!reMap.containsKey("A01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("A01");
				}
				
				aList.put("E0122", "UM");
				reMap.put("A01", aList);
			} else if ("ORG_FLAG".equalsIgnoreCase(uro)) {
				reMap.remove("A01");
			} else if ("POST_FLAG".equalsIgnoreCase(uro)) {
				reMap.remove("A01");
				Map aList = null;
				if (!reMap.containsKey("K01")) {
					aList = new HashMap();
					
				} else {
					aList = (HashMap) reMap.get("K01");
				}
				
				aList.put("E0122", "UM");
				reMap.put("K01", aList);
			}
		}
		return reMap;
	}
	
	
	/**
	 * 
	 * @param codefields
	 */
	private void empSynchronizationA0100(String codefields) {
		ContentDAO dao = new ContentDAO(this.conn);
		/*
		 * codefields = codefields.replaceAll(",a0101",""); codefields =
		 * codefields.replaceAll("a0101,","");
		 */
		String[] ss = codefields.split(",");
		ArrayList codelist = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		for (int i = 0; i < ss.length; i++) {
			String codefield = ss[i];
			FieldItem item = DataDictionary.getFieldItem(codefield);
			if (item == null) {
                continue;
            }
			String setname = item.getFieldsetid();
			if (codelist.indexOf(setname) != -1) {
                continue;
            }
			codelist.add(setname);
		}
		// 新增
		String fields = getMainSetFields();
		String customfields = getCustomMainSetFiedlds();
		for (int i = 0; i < dblist.size(); i++) {
			String dbname = (String) dblist.get(i);
			String hz_dbname = AdminCode.getCodeName("@@", dbname);
			StringBuffer sql = new StringBuffer();
			sql.append("insert into t_hr_view");
			sql.append("(unique_id,nbase,nbase_0,A0100,");
			sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
			sql.append("username,userpassword,flag,sDate");
			if (customfields.length() > 0) {
				sql.append(",");
				String cloumn = customfields;
				if (cloumn != null && cloumn.indexOf("a0101") != -1) {
					StringBuffer str = new StringBuffer();
					str.append(cloumn.substring(0, cloumn.indexOf("a0101")));
					;
					str.append((cloumn + ",")
							.substring(cloumn.indexOf("a0101") + 6));
					str.setLength(str.length() - 1);
					cloumn = str.toString();
				}
				sql.append(cloumn);
			}
			sql.append(")");
			sql.append("( select guidkey,'" + hz_dbname
					+ "','" + dbname + "',");
			sql.append(" A0100,B0110,E0122,A0101,E01A1,");
			sql.append(getUserPwd());
			sql.append(",9 ");
			sql.append("," + Sql_switcher.dateValue(datestr));
			if (fields.length() > 0) {
				sql.append(",");
				String cloumn = fields;
				if (cloumn != null && cloumn.indexOf("a0101") != -1
						&& customfields != null
						&& customfields.indexOf("a0100") != -1) {
					StringBuffer str = new StringBuffer();
					str.append(cloumn.substring(0, cloumn.indexOf("a0101")));
					;
					str.append((cloumn + ",")
							.substring(cloumn.indexOf("a0101") + 6));
					str.setLength(str.length() - 1);
					cloumn = str.toString();
				}
				sql.append(cloumn);
			}
			sql.append(" from " + dbname + "a01 A");
			sql.append(" WHERE  NOT EXISTS( ");
			sql
					.append(" SELECT * FROM t_hr_view t WHERE  A.guidkey=t.unique_id and t.nbase_0='"
							+ dbname + "' ) ");
			sql.append(")");
			try {
				/** ****检查新增人员***** */
			    //System.out.println(sql.toString());
				dao.insert(sql.toString(), new ArrayList());
				/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
				importSubSetData("t_hr_view", dbname, "9");
				sql.setLength(0);
				if (!isBcode())// **代码翻译*//*
                {
                    updateCodeItemdesc("t_hr_view","t_hr_view.flag=9");
                } else if (this.codefields.length() > 0) {
                    updateGivenCodeItemdesc("t_hr_view", this.codefields,"t_hr_view.flag=9");
                }
				sql.append("update t_hr_view set flag=" + this.ADD_FLAG
						+ " where flag=9");
				dao.update(sql.toString());
				
			} catch (Exception e) {
				// e.printStackTrace();
			}
			// 新增结束
			sql.setLength(0);
			try {
				this.importData("t_hr_view_t");
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
			/*
			 * // 将删除的人，再新增，修改Flag，新增 String destTab = "t_hr_view";// 目标表 String
			 * srcTab = "t_hr_view_t";// 源表 StringBuffer strJoin = new
			 * StringBuffer(); strJoin.append(destTab + ".A0100=" + srcTab +
			 * ".A0100 and " + destTab + ".nbase_0=" + srcTab + ".nbase_0");
			 * strJoin.append(" and " + Sql_switcher.isnull(destTab +
			 * ".b0110_0", "'####'") + "=" + Sql_switcher.isnull(srcTab +
			 * ".b0110_0", "'####'") + ""); strJoin.append(" and " +
			 * Sql_switcher.isnull(destTab + ".e0122_0", "'####'") + "=" +
			 * Sql_switcher.isnull(srcTab + ".e0122_0", "'####'") + "");
			 * strJoin.append(" and " + Sql_switcher.isnull(destTab +
			 * ".e01a1_0", "'####'") + "=" + Sql_switcher.isnull(srcTab +
			 * ".e01a1_0", "'####'") + ""); strJoin.append(" and " + destTab +
			 * ".A0101=" + srcTab + ".A0101 "); String strSet = destTab +
			 * ".flag=0"; String strDWhere = destTab + ".flag=2 and "+destTab +
			 * ".nbase_0='"+dbname+"'";// 更新目标的表过滤条件 String strSWhere =
			 * srcTab+".nbase_0='"+dbname+"'";// 源表的过滤条件 String update =
			 * Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
			 * strJoin.toString(), strSet, strDWhere, strSWhere); update =
			 * KqUtilsClass.repairSqlTwoTable(srcTab, strJoin.toString(),
			 * update, strDWhere, "");
			 * 
			 * try { //System.out.println(update); dao.update(update); } catch
			 * (Exception e) { // e.printStackTrace(); }
			 */
			// 分情况一、新增后或修改后继续修改的 flag=2
			sql.setLength(0);
			sql.append("select unique_id from (");
			sql.append(" select unique_id from (");
			sql.append(" select nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			String stemp = "";
			for (int t = 0; t < ss.length; t++) {
				String codefield = ss[t];
				/*
				 * if (codefield.equalsIgnoreCase("b0110") ||
				 * codefield.equalsIgnoreCase("e0122") ||
				 * codefield.equalsIgnoreCase("e01a1") ||
				 * codefield.equalsIgnoreCase("a0101"))
				 */
				if ("a0101".equalsIgnoreCase(codefield)) {
                    continue;
                }
				FieldItem item = DataDictionary.getFieldItem(codefield);
				if (item == null) {
                    continue;
                }
				String custom = this
						.getAppAttributeValue(HrSyncBo.A, codefield);
				stemp += "," + custom;
			}
			sql.append(stemp);
			sql.append(" from t_hr_view  where nbase_0 = '" + dbname
					+ "' and flag<>" + this.DEL_FLAG + "" + " union all ");
			sql.append(" select nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			sql.append(stemp);
			sql.append(" from t_hr_view_t where nbase_0 = '" + dbname + "' "
					+ ") t");
			sql.append(" group by nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			sql.append(stemp);
			sql.append(" having count(*)=1");
			sql.append(") tt group by unique_id having count(*)>1");
			/** ****比对得到修改数据的人员名单***** */
			ArrayList a0100list = new ArrayList();
			RowSet rs = null;
			try {
				// System.out.println(sql.toString());
				rs = dao.search(sql.toString());
				while (rs.next()) {
					a0100list.add(rs.getString("unique_id"));
				}
			} catch (SQLException e1) {
				// e1.printStackTrace();
			} finally {
				if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // e.printStackTrace();
                    }
                }
			}
			if (a0100list.size() > 0) {
				insertA0100KeyTable(a0100list);
				sql.setLength(0);
				sql.append("delete t_hr_view where nbase_0='" + dbname
						+ "' and unique_id in(");
				sql.append("select a0100 from " + this.t_hrSync_key_tab);
				sql.append(")");
				try {
					/** 删除修改人员原纪录 */
					dao.update(sql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sql.setLength(0);

				sql.append("insert into t_hr_view");
				sql.append("(unique_id,nbase,nbase_0,A0100,");
				sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
				sql.append("username,userpassword,flag,sDate");
				if (codefields.length() > 0) {
					sql.append(",");
					String cloumn = Customtransition(HrSyncBo.A, codefields);
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer buf = new StringBuffer();
						buf
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						buf.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						buf.setLength(buf.length() - 1);
						cloumn = buf.toString();
					}
					sql.append(cloumn);

				}
				sql.append(")");
				sql.append("( select unique_id,nbase,nbase_0,A0100,B0110_0,E0122_0,A0101,E01A1_0,"
						+ "username,userpassword," + this.UPDATE_FALG
						+ " as flag," + Sql_switcher.dateValue(datestr));
				if (codefields.length() > 0) {
					sql.append(",");
					// sql.append(Customtransition(HrSyncBo.A,codefields));
					String cloumn = Customtransition(HrSyncBo.A, codefields);
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer buf = new StringBuffer();
						buf
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						buf.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						buf.setLength(buf.length() - 1);
						cloumn = buf.toString();
					}
					sql.append(cloumn);
				}
				sql.append(" from t_hr_view_t ");
				sql.append(" where nbase_0='" + dbname + "' and unique_id in (");
				sql.append("select a0100 from " + this.t_hrSync_key_tab);
				sql.append("))");
				try {
					/** *添加修改纪录 */
					// System.out.println(sql.toString());
					dao.insert(sql.toString(), new ArrayList());
				} catch (SQLException e1) {
					// e1.printStackTrace();
				}
			}
			// 情况二，删除后新增用了删除人的id，要标记为新增 flag=0
			sql.setLength(0);
			sql.append("select unique_id from (");
			sql.append(" select unique_id from (");
			sql.append(" select nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			stemp = "";
			for (int t = 0; t < ss.length; t++) {
				String codefield = ss[t];
				if ("b0110".equalsIgnoreCase(codefield)
						|| "e0122".equalsIgnoreCase(codefield)
						|| "e01a1".equalsIgnoreCase(codefield)
						|| "a0101".equalsIgnoreCase(codefield)) {
                    continue;
                }
				FieldItem item = DataDictionary.getFieldItem(codefield);
				if (item == null) {
                    continue;
                }
				String custom = this
						.getAppAttributeValue(HrSyncBo.A, codefield);
				stemp += "," + custom;
			}
			sql.append(stemp);
			sql.append(" from t_hr_view  where nbase_0 = '" + dbname
					+ "' and flag=" + this.DEL_FLAG + "" + " union all ");
			sql.append(" select nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			sql.append(stemp);
			sql.append(" from t_hr_view_t where nbase_0 = '" + dbname + "' "
					+ ") t");
			sql.append(" group by nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0");
			sql.append(",username,userpassword");
			sql.append(stemp);
			sql.append(" having count(*)=1");
			sql.append(") tt group by unique_id having count(*)>1");
			/** ****比对得到修改数据的人员名单***** */
			a0100list = new ArrayList();
			rs = null;
			try {
				// System.out.println(sql.toString());
				rs = dao.search(sql.toString());
				while (rs.next()) {
					a0100list.add(rs.getString("unique_id"));
				}
			} catch (SQLException e1) {
				// e1.printStackTrace();
			} finally {
				if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // e.printStackTrace();
                    }
                }
			}
			if (a0100list.size() > 0) {
				insertA0100KeyTable(a0100list);
				sql.setLength(0);
				sql.append("delete t_hr_view where nbase_0='" + dbname
						+ "' and unique_id in(");
				sql.append("select a0100 from " + this.t_hrSync_key_tab);
				sql.append(")");
				try {
					/** 删除修改人员原纪录 */
					dao.update(sql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sql.setLength(0);

				sql.append("insert into t_hr_view");
				sql.append("(unique_id,nbase,nbase_0,A0100,");
				sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
				sql.append("username,userpassword,flag,sDate");
				if (codefields.length() > 0) {
					sql.append(",");
					String cloumn = Customtransition(HrSyncBo.A, codefields);
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer buf = new StringBuffer();
						buf
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						buf.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						buf.setLength(buf.length() - 1);
						cloumn = buf.toString();
					}
					sql.append(cloumn);

				}
				sql.append(")");
				sql.append("( select unique_id,nbase,nbase_0,A0100,B0110_0,E0122_0,A0101,E01A1_0,"
						+ "username,userpassword," + this.ADD_FLAG
						+ " as flag," + Sql_switcher.dateValue(datestr));
				if (codefields.length() > 0) {
					sql.append(",");
					// sql.append(Customtransition(HrSyncBo.A,codefields));
					String cloumn = Customtransition(HrSyncBo.A, codefields);
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer buf = new StringBuffer();
						buf
								.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						buf.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						buf.setLength(buf.length() - 1);
						cloumn = buf.toString();
					}
					sql.append(cloumn);
				}
				sql.append(" from t_hr_view_t ");
				sql.append(" where nbase_0='" + dbname + "' and unique_id in (");
				sql.append("select a0100 from " + this.t_hrSync_key_tab);
				sql.append("))");
				try {
					/** *添加修改纪录 */
					// System.out.println(sql.toString());
					dao.insert(sql.toString(), new ArrayList());
				} catch (SQLException e1) {
					// e1.printStackTrace();
				}
			}
			/** 标记删除 */
			sql.setLength(0);
			sql.append("update t_hr_view set flag=" + this.DEL_FLAG + ",sDate="
					+ Sql_switcher.dateValue(datestr) + " where nbase_0 = '"
					+ dblist.get(i) + "' and  unique_id in ");
			sql.append("( select unique_id");
			sql.append(" from t_hr_view t ");
			sql.append(" WHERE nbase_0 = '" + dblist.get(i)
					+ "' and  NOT EXISTS( ");
			sql.append(" SELECT guidkey from " + dblist.get(i)
					+ "a01 A WHERE  A.guidkey=t.unique_id))");
			try {
				dao.update(sql.toString());
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 同步人员
	 * 
	 * @param codefields
	 */
	public void empSynchronization(String codefields) {
		if (!this.sync_a01) {
            return;
        }
//		creatKeyTable();
//		String only_field = getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
//		if (only_field == null || only_field.length() <= 0)
//			empSynchronizationA0100(codefields);
//		else
		
		// 按照新程序，唯一字段只有uniquer_id
		if(isSync_a01()) {
            empSynchronizationOnlyFiled(codefields, "unique_id");
        }
		// 以前的标记仍然保留，但新接口不再使用此标记，兼容以前的程序。
//		transactSynchLose("t_hr_view");
	}

	/**
	 * 同步operuser表数据
	 * 
	 * @throws GeneralException
	 */
	public void operUserSynchronization() throws GeneralException {
		if (!this.sync_oper) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		creatOperUserTable("t_user_view");
		DbWizard dbw = new DbWizard(this.conn);
		if (!dbw.isExistTable("t_user_view", false)) {
            return;
        }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		StringBuffer sql = new StringBuffer();

		sql.append("insert into t_user_view");// ?
		sql.append("(nbase,A0100,");
		sql.append("username,password,fullname,state,flag,sDate");

		sql.append(")");
		sql.append("( select A.nbase,");
		sql.append(" A.A0100,username,password,fullname,state");
		sql.append("," + this.ADD_FLAG + " ");
		sql.append("," + Sql_switcher.dateValue(datestr));

		sql.append(" from operuser A");
		sql.append(" WHERE  NOT EXISTS( ");
		sql
				.append(" SELECT * FROM t_user_view t WHERE  A.username=t.username)) ");
		try {
			/** ****检查新增人员***** */
			dao.insert(sql.toString(), new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 将删除的人，再新增，修改Flag，新增
		String destTab = "t_user_view";// 目标表
		String srcTab = "operuser";// 源表
		StringBuffer strJoin = new StringBuffer();
		strJoin.append(destTab + ".username=" + srcTab + ".username");
		String strSet = destTab + ".flag=" + this.ADD_FLAG + " ";
		String strDWhere = destTab + ".flag=" + this.DEL_FLAG + "";// 更新目标的表过滤条件
		String strSWhere = "";// 源表的过滤条件
		String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
				strJoin.toString(), strSet, strDWhere, strSWhere);
		try {
			dao.update(update);// 将删除的人，再新增，修改Flag，新增
		} catch (Exception e) {
			// e.printStackTrace();
		}

		sql.setLength(0);
		sql.append("select username from (");
		sql.append(" select username from (");
		sql.append(" select nbase,a0100");
		sql.append(",username,password,fullname,state");
		sql.append(" from t_user_view  ");
		sql.append("union all ");
		sql.append(" select nbase,a0100");
		sql.append(",username,password,fullname,state");
		sql.append(" from operuser) t");
		sql.append(" group by nbase,a0100");
		sql.append(",username,password,fullname,state");
		sql.append(" having count(*)=1");
		sql.append(") tt group by username having count(*)>1");
		ArrayList a0100list = new ArrayList();
		RowSet rs = null;
		try {
			// System.out.println(sql.toString());
			rs = dao.search(sql.toString());
			while (rs.next()) {
				a0100list.add(rs.getString("username"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}

		if (a0100list.size() > 0) {
			sql.setLength(0);
			sql.append("delete t_user_view where username in(");
			for (int j = 0; j < a0100list.size(); j++) {
				sql.append("'" + a0100list.get(j) + "',");
			}
			sql.setLength(sql.length() - 1);
			sql.append(")");
			try {
				/** 删除修改人员原纪录 */
				dao.update(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			sql.setLength(0);

			sql.append("insert into t_user_view");
			sql.append("(nbase,A0100,");
			sql.append("username,password,fullname,state,flag,sDate");

			sql.append(")");
			sql.append("( select nbase,A0100,username,password,fullname,state,"
					+ "" + this.UPDATE_FALG + " as flag,"
					+ Sql_switcher.dateValue(datestr));

			sql.append(" from operuser ");
			sql.append(" where username in (");
			for (int j = 0; j < a0100list.size(); j++) {
				sql.append("'" + a0100list.get(j) + "',");
			}
			sql.setLength(sql.length() - 1);
			sql.append("))");
			try {
				/** *添加修改纪录 */
				dao.insert(sql.toString(), new ArrayList());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		sql.setLength(0);

		sql.append("update t_user_view set flag=" + this.DEL_FLAG + ",sDate="
				+ Sql_switcher.dateValue(datestr) + " where username in ");
		sql.append("( select username");
		sql.append(" from t_user_view t ");
		sql.append(" WHERE  NOT EXISTS( ");
		sql
				.append(" SELECT username from operuser A WHERE  A.username=t.username))");
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		transactSynchLose("t_user_view");
	}

	/**
	 * 同步单位部门
	 * 
	 * @param codefields
	 */
	public void orgSynchronization(String codefields) {
		if (!this.sync_b01) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
//		addFieldToTable("t_org_view", "b");
		String[] bfields = codefields.split(",");
//		ArrayList codelist = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		ArrayList sysList = selectSysid2();
		String fields = "", customfields = "";
		for (int i = 0; i < bfields.length; i++) {
			FieldItem item = DataDictionary.getFieldItem(bfields[i]);
			if (!"b0110".equalsIgnoreCase(item.getItemid())
					&& !"b01".equalsIgnoreCase(item.getFieldsetid())) {
                continue;
            }
			fields += "," + bfields[i];
			customfields += ","
					+ this.getAppAttributeValue(HrSyncBo.B, bfields[i]);
		}
		
		String columns = getTextValue(HrSyncBo.ORG_FIELDS);//获得已选的机构字段
		// 翻译型代码
		String codefieldstr = "";
		if (!isBcode()) {//判断是否选择的全部翻译成代码描述
			codefieldstr = filtration(columns);//获得全部的翻译型代码
		} else {
			codefieldstr = getTextValue(HrSyncBo.ORG_CODE_FIELDS);//获得选择的翻译型代码
		}
		Map tranfieldsMap = getFields(codefieldstr, "ORG_FLAG");//整理字段按照表归类
		Map fieldsMap = getFields(columns, "ORG_FLAG");//整理字段按照表归类
		
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);

		sql.append("insert into t_org_view");
		sql
				.append("(unique_id,b0110_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
		sql.append("grade,flag,sDate");
		
		for (int i = 0; i < sysList.size(); i++) {
			sql.append(",");
			sql.append(sysList.get(i));
		}
		
		sql.append(")");
		sql
				.append("select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=o.parentid) parentdesc,corcode,grade,flag ");
		sql.append("," + Sql_switcher.dateValue(datestr));
		
		for (int i = 0; i < sysList.size(); i++) {
			sql.append(",");
			sql.append("1");
		}
		
		sql.append(" from organization o ");
		
		sql.append("  where  (codesetid='UM' or codesetid='UN') and ");
		sql.append(Sql_switcher.sqlNow());
		sql.append(" between start_date and end_date and  NOT EXISTS( ");
		sql.append(" SELECT 1 FROM t_org_view t WHERE  o.guidkey=t.unique_id) ");
		
		
		
		StringBuffer updateSql = new StringBuffer();
		
		if (Sql_switcher.searchDbServer() == 1) {
			updateSql.append("update t_org_view");
			updateSql
					.append(" set b0110_0=o.codeitemid,codesetid=o.codesetid,codeitemdesc=o.codeitemdesc,parentid=o.parentid,parentdesc=o.parentdesc,corcode=o.corcode,");
			updateSql.append("grade=o.grade,flag=(case when flag=0 then 2 else flag end),sDate=");
			updateSql.append(Sql_switcher.dateValue(datestr));
			
			for (int i = 0; i < sysList.size(); i++) {
				String sys = (String)sysList.get(i);
				updateSql.append(",");
				updateSql.append(sys);
				updateSql.append("=(case when " + sys + "=0 then 2 else " + sys + " end)");
			}
			
			updateSql.append(" from ");
			updateSql
					.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
			
			updateSql.append(" from organization t where (codesetid='UM' or codesetid='UN') and  ");
			updateSql.append(Sql_switcher.sqlNow());
			updateSql.append(" between start_date and end_date ) o");
			
			updateSql.append("  where unique_id=o.guidkey");
			updateSql.append(" and  EXISTS( ");
			updateSql.append(" SELECT 1 FROM t_org_view t WHERE  o.guidkey=t.unique_id) and (");
			updateSql.append(" ((t_org_view.b0110_0<>o.codeitemid and t_org_view.b0110_0 is not null and o.codeitemid is not null) or (t_org_view.b0110_0 is null and o.codeitemid is not null) or (t_org_view.b0110_0 is not null and o.codeitemid is null))");
			updateSql.append(" or ((t_org_view.codesetid<>o.codesetid and t_org_view.codesetid is not null ) or (t_org_view.codesetid is not null and o.codesetid is null) or (t_org_view.codesetid is null and o.codesetid is not null))");
			updateSql.append(" or ((t_org_view.codeitemdesc<>o.codeitemdesc and t_org_view.codeitemdesc is not null and o.codeitemdesc is not null) or(t_org_view.codeitemdesc is null and o.codeitemdesc is not null) or (t_org_view.codeitemdesc is not null and o.codeitemdesc is null) )");
			updateSql.append(" or ((t_org_view.parentid<>o.parentid and t_org_view.parentid is not null and o.parentid is not null) or(t_org_view.parentid is null and o.parentid is not null) or (t_org_view.parentid is not null and o.parentid is null) )");
			updateSql.append(" or ((t_org_view.parentdesc<>o.parentdesc and t_org_view.parentdesc is not null and o.parentdesc is not null) or(t_org_view.parentdesc is null and o.parentdesc is not null) or (t_org_view.parentdesc is not null and o.parentdesc is null) )");
			updateSql.append(" or ((t_org_view.corcode<>o.corcode and t_org_view.corcode is not null and o.corcode is not null) or(t_org_view.corcode is null and o.corcode is not null) or (t_org_view.corcode is not null and o.corcode is null) )");
			updateSql.append(" or ((t_org_view.grade<>o.grade and t_org_view.grade is not null and o.grade is not null) or(t_org_view.grade is null and o.grade is not null) or (t_org_view.grade is not null and o.grade is null) )");
			
			updateSql.append(")");
		
		} else {
			
			StringBuffer updateSql2 = new StringBuffer();
			
			updateSql.append("update t_org_view");
			updateSql.append(" set (b0110_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,grade,flag,sDate");
			updateSql2.append("=(select codeitemid,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
			updateSql2.append("grade,(case when flag=0 then 2 else flag end),");
			updateSql2.append(Sql_switcher.dateValue(datestr));
			
			for (int i = 0; i < sysList.size(); i++) {
				String sys = (String)sysList.get(i);
				updateSql.append(",");
				updateSql2.append(",");
				updateSql.append(sys);
				updateSql2.append("(case when " + sys + "=0 then 2 else " + sys + " end)");
			}
			
			updateSql.append(")");
			updateSql2.append(" from ");
			updateSql2
					.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
			
			updateSql2.append(" from organization t where (codesetid='UM' or codesetid='UN') and  ");
			updateSql2.append(Sql_switcher.sqlNow());
			updateSql2.append(" between start_date and end_date ) c");
			
			
			
			updateSql2.append("  where unique_id=c.guidkey)");
			
			updateSql.append(updateSql2.toString());
			
			updateSql.append(" where ");
			updateSql.append(" EXISTS( ");
			updateSql.append(" SELECT 1 FROM ");
			
			updateSql
			.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
	
			updateSql.append(" from organization t where (codesetid='UM' or codesetid='UN') and  ");
			updateSql.append(Sql_switcher.sqlNow());
			updateSql.append(" between start_date and end_date )");
			
			
			updateSql.append(" o WHERE  o.guidkey=t_org_view.unique_id and (");
			updateSql.append(" ((t_org_view.b0110_0<>o.codeitemid and t_org_view.b0110_0 is not null and o.codeitemid is not null) or (t_org_view.b0110_0 is null and o.codeitemid is not null) or (t_org_view.b0110_0 is not null and o.codeitemid is null))");
			updateSql.append(" or ((t_org_view.codesetid<>o.codesetid and t_org_view.codesetid is not null ) or (t_org_view.codesetid is not null and o.codesetid is null) or (t_org_view.codesetid is null and o.codesetid is not null))");
			updateSql.append(" or ((t_org_view.codeitemdesc<>o.codeitemdesc and t_org_view.codeitemdesc is not null and o.codeitemdesc is not null) or(t_org_view.codeitemdesc is null and o.codeitemdesc is not null) or (t_org_view.codeitemdesc is not null and o.codeitemdesc is null) )");
			updateSql.append(" or ((t_org_view.parentid<>o.parentid and t_org_view.parentid is not null and o.parentid is not null) or(t_org_view.parentid is null and o.parentid is not null) or (t_org_view.parentid is not null and o.parentid is null) )");
			updateSql.append(" or ((t_org_view.parentdesc<>o.parentdesc and t_org_view.parentdesc is not null and o.parentdesc is not null) or(t_org_view.parentdesc is null and o.parentdesc is not null) or (t_org_view.parentdesc is not null and o.parentdesc is null) )");
			updateSql.append(" or ((t_org_view.corcode<>o.corcode and t_org_view.corcode is not null and o.corcode is not null) or(t_org_view.corcode is null and o.corcode is not null) or (t_org_view.corcode is not null and o.corcode is null) )");
			updateSql.append(" or ((t_org_view.grade<>o.grade and t_org_view.grade is not null and o.grade is not null) or(t_org_view.grade is null and o.grade is not null) or (t_org_view.grade is not null and o.grade is null) )");
			
			updateSql.append("))");
		}
		
		updateSql.append(" and ");
		updateSql.append(this.getDelWhere("B", null));

		try {
			// System.out.println(sql.toString());
			dao.insert(sql.toString(), new ArrayList());
			// 导入子集－把人员库中当前记录导入至视图t_hr_view
			
			dao.update(updateSql.toString());
			this.updateData(fieldsMap, tranfieldsMap, "", "B", sysList);
			
			this.deletedData(null, "B", sysList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 同步岗位
	 * 
	 * @param codefields
	 */
	public void postSynchronization(String codefields) {
		if (!this.sync_k01) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		addFieldToTable("t_post_view", "b");
		String[] bfields = codefields.split(",");
//		ArrayList codelist = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());

		String fields = "", customfields = "";
		for (int i = 0; i < bfields.length; i++) {
			FieldItem item = DataDictionary.getFieldItem(bfields[i]);
			if (!"k01".equalsIgnoreCase(item.getFieldsetid())) {
				if (!"e0122".equalsIgnoreCase(item.getItemid())
						&& !"e01a1".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
			}
			fields += "," + bfields[i];
			customfields += ","
					+ this.getAppAttributeValue(HrSyncBo.K, bfields[i]);
		}
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);
		
		String columns = getTextValue(HrSyncBo.POST_FIELDS);//获得已选的机构字段
		// 翻译型代码
		String codefieldstr = "";
		if (!isBcode()) {//判断是否选择的全部翻译成代码描述
			codefieldstr = filtration(columns);//获得全部的翻译型代码
		} else {
			codefieldstr = getTextValue(HrSyncBo.POST_CODE_FIELDS);//获得选择的翻译型代码
		}
		Map tranfieldsMap = getFields(codefieldstr, "POST_FLAG");//整理字段按照表归类
		Map fieldsMap = getFields(columns, "POST_FLAG");//整理字段按照表归类

		sql.append("insert into t_post_view");
		sql.append("(unique_id,e01a1_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
		sql.append("grade,flag,sDate,e0122_0");
		ArrayList sysList = selectSysid2();
		for (int i = 0; i < sysList.size(); i++) {
			sql.append(",");
			sql.append(sysList.get(i));
		}
		sql.append(")");
		
		
		sql.append("select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=o.parentid) parentdesc,corcode,grade,flag ");
		sql.append("," + Sql_switcher.dateValue(datestr));
		sql.append(",parentid");
		
		for (int i = 0; i < sysList.size(); i++) {
			sql.append(",");
			sql.append("1");
		}
		
		sql.append(" from organization o ");
		
		sql.append("  where  (codesetid='@K') and ");
		sql.append(Sql_switcher.sqlNow());
		sql.append(" between start_date and end_date and  NOT EXISTS( ");
		sql.append(" SELECT 1 FROM t_post_view t WHERE  o.guidkey=t.unique_id) ");
		
		
		StringBuffer updateSql = new StringBuffer();
		
		
		if (Sql_switcher.searchDbServer() == 1) {
			updateSql.append("update t_post_view");
			updateSql
					.append(" set e01a1_0=o.codeitemid,codesetid=o.codesetid,codeitemdesc=o.codeitemdesc,parentid=o.parentid,parentdesc=o.parentdesc,corcode=o.corcode,");
			updateSql.append("grade=o.grade,flag=(case when flag=0 then 2 else flag end),sDate=");
			updateSql.append(Sql_switcher.dateValue(datestr));
			updateSql.append(",e0122_0=o.parentid");
			
			for (int i = 0; i < sysList.size(); i++) {
				String sys = (String)sysList.get(i);
				updateSql.append(",");
				updateSql.append(sys);
				updateSql.append("=(case when " + sys + "=0 then 2 else " + sys + " end)");
			}
			
			updateSql.append(" from ");
			updateSql
					.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
			
			updateSql.append(" from organization t where codesetid='@K' and  ");
			updateSql.append(Sql_switcher.sqlNow());
			updateSql.append(" between start_date and end_date)o ");
			
			updateSql.append("  where unique_id=o.guidkey");
			updateSql.append(" and  EXISTS( ");
			updateSql.append(" SELECT 1 FROM t_post_view t WHERE  o.guidkey=t.unique_id) ");
			updateSql.append(" and (");
			updateSql.append(" ((t_post_view.e01a1_0<>o.codeitemid and t_post_view.e01a1_0 is not null and o.codeitemid is not null) or (t_post_view.e01a1_0 is null and o.codeitemid is not null) or (t_post_view.e01a1_0 is not null and o.codeitemid is null))");
			updateSql.append(" or ((t_post_view.codesetid<>o.codesetid and t_post_view.codesetid is not null and o.codesetid is not null) or (t_post_view.codesetid is null and o.codesetid is not null) or (t_post_view.codesetid is not null and o.codesetid is null))");
			
			updateSql.append(" or ((t_post_view.codeitemdesc<>o.codeitemdesc and t_post_view.codeitemdesc is not null and o.codeitemdesc is not null) or (t_post_view.codeitemdesc is null and o.codeitemdesc is not null) or (t_post_view.codeitemdesc is not null and o.codeitemdesc is null))");
			updateSql.append(" or ((t_post_view.parentid<>o.parentid and t_post_view.parentid is not null and o.parentid is not null) or (t_post_view.parentid is null and o.parentid is not null) or (t_post_view.parentid is not null and o.parentid is null))");
			updateSql.append(" or ((t_post_view.parentdesc<>o.parentdesc and t_post_view.parentdesc is not null and o.parentdesc is not null) or (t_post_view.parentdesc is null and o.parentdesc is not null) or (t_post_view.parentdesc is not null and o.parentdesc is null))");
			updateSql.append(" or ((t_post_view.corcode<>o.corcode and t_post_view.corcode is not null and o.corcode is not null) or (t_post_view.corcode is null and o.corcode is not null) or (t_post_view.corcode is not null and o.corcode is null))");
			updateSql.append(" or ((t_post_view.grade<>o.grade and t_post_view.grade is not null and o.grade is not null) or (t_post_view.grade is null and o.grade is not null) or (t_post_view.grade is not null and o.grade is null))");
			updateSql.append(" or ((t_post_view.e0122_0<>o.parentid and t_post_view.e0122_0 is not null and o.parentid is not null) or (t_post_view.e0122_0 is null and o.parentid is not null) or (t_post_view.e0122_0 is not null and o.parentid is null))");
			updateSql.append(")");
		} else {
			StringBuffer updateSql2 = new StringBuffer();
			updateSql.append("update t_post_view set");
			updateSql2.append("=(select codeitemid,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
			updateSql.append("  (e01a1_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
			updateSql2.append("grade,(case when flag=0 then 2 else flag end),");
			updateSql.append("grade,flag,sDate,e0122_0");
			updateSql2.append(Sql_switcher.dateValue(datestr));
			updateSql2.append(",parentid");
			
			for (int i = 0; i < sysList.size(); i++) {
				String sys = (String)sysList.get(i);
				updateSql.append(",");
				updateSql2.append(",");
				updateSql.append(sys);
				updateSql2.append("(case when " + sys + "=0 then 2 else " + sys + " end)");
			}
			updateSql.append(")");
			updateSql2.append(" from ");
			updateSql2
					.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
			
			updateSql2.append(" from organization t where codesetid='@K' and  ");
			updateSql2.append(Sql_switcher.sqlNow());
			updateSql2.append(" between start_date and end_date) b ");
			updateSql2.append("  where unique_id=b.guidkey)");
			
			updateSql.append(updateSql2.toString());
			
			
			updateSql.append("  where ");
			updateSql.append("  EXISTS( ");
			updateSql.append(" SELECT 1 FROM ");
			
			updateSql
			.append("(select guidkey,codeitemid,codesetid,codeitemdesc,parentid,(select codeitemdesc from organization where codeitemid=t.parentid) parentdesc,corcode,grade");
	
			updateSql.append(" from organization t where codesetid='@K' and  ");
			updateSql.append(Sql_switcher.sqlNow());
			updateSql.append(" between start_date and end_date) o ");
			
			updateSql.append(" where o.guidkey=t_post_view.unique_id and (");
			updateSql.append(" ");
			updateSql.append(" ((t_post_view.e01a1_0<>o.codeitemid and t_post_view.e01a1_0 is not null and o.codeitemid is not null) or (t_post_view.e01a1_0 is null and o.codeitemid is not null) or (t_post_view.e01a1_0 is not null and o.codeitemid is null))");
			updateSql.append(" or ((t_post_view.codesetid<>o.codesetid and t_post_view.codesetid is not null and o.codesetid is not null) or (t_post_view.codesetid is null and o.codesetid is not null) or (t_post_view.codesetid is not null and o.codesetid is null))");
			
			updateSql.append(" or ((t_post_view.codeitemdesc<>o.codeitemdesc and t_post_view.codeitemdesc is not null and o.codeitemdesc is not null) or (t_post_view.codeitemdesc is null and o.codeitemdesc is not null) or (t_post_view.codeitemdesc is not null and o.codeitemdesc is null))");
			updateSql.append(" or ((t_post_view.parentid<>o.parentid and t_post_view.parentid is not null and o.parentid is not null) or (t_post_view.parentid is null and o.parentid is not null) or (t_post_view.parentid is not null and o.parentid is null))");
			updateSql.append(" or ((t_post_view.parentdesc<>o.parentdesc and t_post_view.parentdesc is not null and o.parentdesc is not null) or (t_post_view.parentdesc is null and o.parentdesc is not null) or (t_post_view.parentdesc is not null and o.parentdesc is null))");
			updateSql.append(" or ((t_post_view.corcode<>o.corcode and t_post_view.corcode is not null and o.corcode is not null) or (t_post_view.corcode is null and o.corcode is not null) or (t_post_view.corcode is not null and o.corcode is null))");
			updateSql.append(" or ((t_post_view.grade<>o.grade and t_post_view.grade is not null and o.grade is not null) or (t_post_view.grade is null and o.grade is not null) or (t_post_view.grade is not null and o.grade is null))");
			updateSql.append(" or ((t_post_view.e0122_0<>o.parentid and t_post_view.e0122_0 is not null and o.parentid is not null) or (t_post_view.e0122_0 is null and o.parentid is not null) or (t_post_view.e0122_0 is not null and o.parentid is null))");
			updateSql.append("))");
		}
		// 添加过滤条件

		try {
			
			updateSql.append(" and ");
			updateSql.append(this.getDelWhere("K", dblist));
			// System.out.println(sql.toString());
			dao.insert(sql.toString(), new ArrayList());
			
			dao.update(updateSql.toString());
			
			this.updateData(fieldsMap, tranfieldsMap, "", "K", sysList);
			
			this.deletedData(null, "K", sysList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public String getOrgcodefields() {
		return orgcodefields;
	}

	public void setOrgcodefields(String orgcodefields) {
		this.orgcodefields = orgcodefields;
	}

	public void updateColumn(String table, String field, String editname) {
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			String sql = "EXEC sp_rename '" + table + ".[" + field + "]', '"
					+ editname + "', 'COLUMN'";
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		}
		case Constant.ORACEL: {

			String sql = "ALTER TABLE " + table + " RENAME COLUMN " + field
					+ " TO " + editname + "";
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		}
		}

	}

	public void deleteColumn(String table, String filedname) {
		String sql = "ALTER   TABLE   " + table + "   DROP   COLUMN  "
				+ filedname;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * destTab t_organization_view 创建对应组织机构图视图
	 * 
	 * @throws GeneralException
	 */
	public void creatOrganizationTable(String destTab) throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("codesetid", "codesetid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemid", "codeitemid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemdesc", "codeitemdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("parentid", "parentid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("childid", "childid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("grade", "grade");
			field.setDatatype(DataType.INT);
			table.addField(field);

			field = new Field("sDate", "sDate");
			field.setDatatype(DataType.DATE);
			table.addField(field);

			field = new Field("flag", "flag");
			field.setDatatype(DataType.INT);
			field.setLength(2);
			table.addField(field);

			field = new Field("corcode", "corcode");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("a0000", "a0000");
			field.setDatatype(DataType.INT);
			table.addField(field);
			dbw.createTable(table);
			
			field = new Field("guidkey", "guidkey");
			field.setDatatype(DataType.STRING);
			field.setLength(38);
			table.addField(field);
			dbmodel.reloadTableModel(destTab);
		} else {
			ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
					this.conn);
			if (!reconstructionKqField.checkFieldSave(destTab, "a0000")) {
				Field field = new Field("a0000", "a0000");
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
			if (!reconstructionKqField.checkFieldSave(destTab, "corcode")) {
				Field field = new Field("corcode", "corcode");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
		}
	}

	/**
	 * 单位视图
	 * 
	 * @throws GeneralException
	 */

	public void organizationSynchronization() throws GeneralException {
		if (!this.sync_org) {
            return;
        }
		ContentDAO dao = new ContentDAO(this.conn);
		creatOrganizationTable("t_organization_view");
		DbWizard dbw = new DbWizard(this.conn);
		if (!dbw.isExistTable("t_organization_view", false)) {
            return;
        }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		StringBuffer sql = new StringBuffer();

		sql.append("insert into t_organization_view");
		sql.append("(codesetid,codeitemid,");
		sql
				.append("codeitemdesc,parentid,childid,grade,flag,sDate,corcode,a0000,guidkey");
		sql.append(")");
		sql.append("( select A.codesetid,A.codeitemid,");
		sql.append(" A.codeitemdesc,A.parentid,A.childid,A.grade");
		sql.append("," + this.ADD_FLAG + " ");
		sql.append("," + Sql_switcher.dateValue(datestr));

		sql.append(",A.corcode,A.a0000,A.guidkey from organization A");
		sql.append(" WHERE  NOT EXISTS( ");
		sql
				.append(" SELECT * FROM t_organization_view t WHERE  A.guidkey=t.guidkey)) ");
		try {
			/** ****检查新增人员***** */
			dao.insert(sql.toString(), new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 将删除的机构，再新增，修改Flag，新增
		String destTab = "t_organization_view";// 目标表
		String srcTab = "organization";// 源表
		StringBuffer strJoin = new StringBuffer();
		//strJoin.append(destTab + ".codeitemid=" + srcTab + ".codeitemid ");
		//strJoin.append(" and " + destTab + ".parentid=" + srcTab + ".parentid ");
		//strJoin.append(" and " + destTab + ".codesetid=" + srcTab + ".codesetid ");
		//strJoin.append(" and " + destTab + ".codeitemdesc=" + srcTab + ".codeitemdesc ");
		strJoin.append(destTab + ".guidkey=" + srcTab + ".guidkey ");
		String strSet = destTab + ".flag=" + this.ADD_FLAG + " ";
		String strDWhere = destTab + ".flag=" + this.DEL_FLAG + "";// 更新目标的表过滤条件
		String strSWhere = "";// 源表的过滤条件
		String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
				strJoin.toString(), strSet, strDWhere, strSWhere);
		try {
			// System.out.println(update);
			dao.update(update);// 将删除的机构，再新增，修改Flag，新增
		} catch (Exception e) {
			// e.printStackTrace();
		}

		sql.setLength(0);
		sql.append("select guidkey from (");
		sql.append(" select guidkey from (");
		sql.append(" select guidkey,codesetid,codeitemid");
		sql.append(",codeitemdesc,parentid,childid,grade,corcode");
		sql.append(" from organization  ");
		sql.append("union all ");
		sql.append(" select guidkey,codesetid,codeitemid");
		sql.append(",codeitemdesc,parentid,childid,grade,corcode");
		sql.append(" from t_organization_view) t");
		sql.append(" group by guidkey,codesetid,codeitemid");
		sql.append(",codeitemdesc,parentid,childid,grade,corcode");
		sql.append(" having count(*)=1");
		sql.append(") tt group by guidkey having count(*)>1");
		ArrayList a0100list = new ArrayList();
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				a0100list.add(rs.getString("guidkey"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		if (a0100list.size() > 0) {
			sql.setLength(0);
			sql.append("delete t_organization_view where guidkey in(");
			for (int j = 0; j < a0100list.size(); j++) {
				sql.append("'" + a0100list.get(j) + "',");
			}
			sql.setLength(sql.length() - 1);
			sql.append(")");
			try {
				/** 删除修改人员原纪录 */
				dao.update(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			sql.setLength(0);

			sql.append("insert into t_organization_view");
			sql.append("(codesetid,codeitemid,");
			sql
					.append("codeitemdesc,parentid,childid,grade,flag,sDate,corcode,a0000,guidkey");

			sql.append(")");
			sql
					.append("( select codesetid,codeitemid,codeitemdesc,parentid,childid,grade,"
							+ ""
							+ this.UPDATE_FALG
							+ " as flag,"
							+ Sql_switcher.dateValue(datestr));

			sql.append(",corcode,a0000,guidkey from organization ");
			sql.append(" where guidkey in (");
			for (int j = 0; j < a0100list.size(); j++) {
				sql.append("'" + a0100list.get(j) + "',");
			}
			sql.setLength(sql.length() - 1);
			sql.append("))");
			try {
				/** *添加修改纪录 */
				// System.out.println(sql.toString());
				dao.insert(sql.toString(), new ArrayList());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		sql.setLength(0);

		sql.append("update t_organization_view set flag=" + this.DEL_FLAG
				+ ",sDate=" + Sql_switcher.dateValue(datestr)
				+ " where guidkey in ");
		sql.append("( select guidkey");
		sql.append(" from t_organization_view t ");
		sql.append(" WHERE  NOT EXISTS( ");
		sql
				.append(" SELECT guidkey from organization A WHERE  A.guidkey=t.guidkey))");
		try {
			dao.update(sql.toString());
			String clientName = SystemConfig.getPropertyValue("clientName");// cssc是中船
			if (clientName != null && "cssc".equalsIgnoreCase(clientName)) {
				sql.setLength(0);
				sql.append("update t_organization_view set parentid='' where  codeitemid=parentid");
				dao.update(sql.toString());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		transactSynchLose("t_organization_view");
	}

	/**
	 * 查看配置文件中的同步信息设置 在system.properties文件中的synchronization配置
	 * 如果有oper则同步operuser业务人员表 如果有org 则同步organization组织机构表 中间用“|”隔开
	 * #同步业务人员库和组织机构 synchronization=oper|org
	 */
	private void checkSystemConfigSynchInfo() {

		String synchronization = SystemConfig
				.getPropertyValue("synchronization");
		if (synchronization == null || synchronization.length() <= 0) {
			this.sync_oper = false;
			this.sync_org = false;
			return;
		}
		String strs[] = synchronization.split(",");
		if (strs == null) {
			this.sync_oper = false;
			this.sync_org = false;
			return;
		}
		String str = "";
		for (int i = 0; i < strs.length; i++) {
			str = strs[i];
			if (str == null || str.length() <= 0) {
                continue;
            }
			if ("oper".equalsIgnoreCase(str)) {
                this.sync_oper = true;
            } else if ("org".equalsIgnoreCase(str)) {
                this.sync_org = true;
            }
		}
	}

	/**
	 * 公司根据北大青鸟公司对处理失败数据的标记，重新提供失败数据信息。其中“flag”为6的标记为1，7标记为2，8标记为3。
	 * 
	 * @param table
	 * @return
	 */
	private boolean transactSynchLose(String table) {
		String sql = "update " + table + " set flag='1' where flag='6'";
		boolean isCorrect = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql);
			sql = "update " + table + " set flag='2' where flag='7'";
			dao.update(sql);
			sql = "update " + table + " set flag='3' where flag='8'";
			dao.update(sql);
			isCorrect = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isCorrect;
	}

	/**
	 * 组织机构描述分层级
	 * 
	 * @param destTab
	 */
	public void uplevelDeptTable(String destTab) {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("codesetid", "codesetid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemid", "codeitemid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemdesc", "codeitemdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(200);
			table.addField(field);
			field = new Field("parentid", "parentid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			try {
				dbw.createTable(table);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			dbmodel.reloadTableModel(destTab);
		}
		String del = "delete from " + destTab;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(del);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer();
		sql.append("insert into " + destTab + "");
		sql.append("(codesetid,codeitemid,codeitemdesc)");
		sql.append("( select A.codesetid,A.codeitemid,");
		sql.append(" A.codeitemdesc");
		sql.append(" from organization A");
		sql.append(" where codesetid='UM' or codesetid='UN')");
		try {
			dao.insert(sql.toString(), new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql.setLength(0);
		sql.append("select codeitemid,codesetid from " + destTab);
		RowSet rs = null;
		try {
			String codeitemid = "";
			String codesetid = "";
			rs = dao.search(sql.toString());
			ArrayList list = new ArrayList();

			while (rs.next()) {
				codeitemid = rs.getString("codeitemid");
				codesetid = rs.getString("codesetid");
				CodeItem codeitem = null;
				if (codesetid != null && "UN".equalsIgnoreCase(codesetid)) {
					codeitem = AdminCode.getCode("UN", codeitemid, this.nlevel);
				} else {
					codeitem = AdminCode.getCode("UM", codeitemid, this.nlevel);
				}

				if (codeitem != null) {
					ArrayList list_1 = new ArrayList();

					list_1.add(codeitem.getCodename());
					
					list_1.add(codeitemid);
					list.add(list_1);
				}
			}
			sql.setLength(0);
			sql.append("update " + destTab
					+ " set codeitemdesc=? where codeitemid=?");
			dao.batchUpdate(sql.toString(), list);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}

	public int getNlevel() {
		return nlevel;
	}

	public void setNlevel(int nlevel) {
		this.nlevel = nlevel;
	}

	/**
	 * 更新组织机构的排序
	 * 
	 * @param table_temp
	 * @throws GeneralException
	 */
	public void synchronizationInitOrgA0000(String destTab) {

		String srcTab = "organization";// 源表
		String strJoin = destTab + ".unique_id=" + srcTab + ".guidkey";// 关联串
		// xxx.field_name=yyyy.field_namex,....

//		String strSet = destTab + ".a0000=" + srcTab + ".a0000";// 更新串
		String strSet = destTab + ".levelA0000=" + srcTab + ".levelA0000";//更新串 A0000 替换为levelA0000 wangb 20170807
		// xxx.field_name=yyyy.field_namex,....
		String strDWhere = "";// 更新目标的表过滤条件
		String strSWhere = srcTab + ".codesetid in ('UN','UM')";// 源表的过滤条件
		String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
				strJoin, strSet, strDWhere, strSWhere);
		update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update,
				strDWhere, "");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			// System.out.println(update);
			dao.update(update, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新组织机构岗位的排序
	 * 
	 * @param table_temp
	 * @throws GeneralException
	 */
	public void synchronizationInitPostA0000(String destTab) {

		String srcTab = "organization";// 源表
		String strJoin = destTab + ".unique_id=" + srcTab + ".guidkey";// 关联串
		// xxx.field_name=yyyy.field_namex,....

//		String strSet = destTab + ".a0000=" + srcTab + ".a0000";// 更新串
		String strSet = destTab + ".levelA0000=" + srcTab + ".levelA0000";// 更新串 A0000 替换为levelA0000 wangb 20170807
		// xxx.field_name=yyyy.field_namex,....
		String strDWhere = "";// 更新目标的表过滤条件
		String strSWhere = srcTab + ".codesetid in ('@K')";// 源表的过滤条件
		String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab,
				strJoin, strSet, strDWhere, strSWhere);
		update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update,
				strDWhere, "");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			// System.out.println(update);
			dao.update(update, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加增加的固定相
	 * 
	 * @param destTab
	 * @param flag
	 */
	public void addFieldToTable(String destTab, String flag) {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
				this.conn);
		try {
			if ("b".equalsIgnoreCase(flag)) {
				if (!reconstructionKqField.checkFieldSave(destTab, "a0000")) {
					Field field = new Field("a0000", "a0000");
					field.setDatatype(DataType.INT);
					table.addField(field);
					dbw.addColumns(table);
					dbmodel.reloadTableModel(destTab);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String creatKeyTable() {
		String destTab = this.t_hrSync_key_tab;
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("a0100", "a0100");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			try {
				dbw.createTable(table);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			dbmodel.reloadTableModel(destTab);
		}
		return destTab;
	}

	private void insertA0100KeyTable(ArrayList a0100list) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		String sql = "delete from " + this.t_hrSync_key_tab;
		try {
			// System.out.println(update);
			dao.delete(sql, new ArrayList());

			for (int i = 0; i < a0100list.size(); i++) {
				ArrayList list_1 = new ArrayList();
				list_1.add(a0100list.get(i));
				list.add(list_1);
			}
			sql = "insert into " + this.t_hrSync_key_tab + " (a0100) values(?)";
			dao.batchInsert(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除临时表
	 * 
	 * @param tablename
	 */
	public void dropTable(String tablename) {
		Table table = new Table(tablename);
		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable(tablename, false)) {
			String deleteSQL = "delete from " + tablename + "";
			ArrayList deletelist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				dao.delete(deleteSQL, deletelist);
				dbWizard.dropTable(table);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除视图
	 * @param tablename String 视图名称
	 */
	public void dropView(String tablename) {
		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable(tablename, false)) {
			String deleteSQL = "drop view " + tablename ;
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				dao.update(deleteSQL);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createPhotoView(String viewName, String []dbNames, String keyField) {
		
		StringBuffer createSQL = new StringBuffer();
		createSQL.append("create view ");
		createSQL.append(viewName);
		createSQL.append(" as ");
		try {
			for (int i = 0; i < dbNames.length; i++) {
				createSQL.append("select a0.*,a1.a0101,a1.guidkey");
				if (keyField != null && keyField.trim().length() > 0) {
					createSQL.append(",a1."+keyField);
				}
				createSQL.append(" from (");
				createSQL.append("select '"+dbNames[i].toUpperCase()+"' nbase, a0100,i9999,ole,ext from "+dbNames[i]+"a00 where flag='P') a0 left join "+dbNames[i]+"a01 a1 on a0.a0100=a1.a0100 where a0.a0100 is not null");
				if (i != dbNames.length -1) {
					createSQL.append(" union all ");
				}
			}
			
			ContentDAO dao = new ContentDAO(this.conn);
		
			dao.update(createSQL.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	

	public boolean isSync_a01() {
		return sync_a01;
	}

	public void setSync_a01(boolean sync_a01) {
		this.sync_a01 = sync_a01;
	}

	public boolean isSync_b01() {
		return sync_b01;
	}

	public void setSync_b01(boolean sync_b01) {
		this.sync_b01 = sync_b01;
	}

	public boolean isSync_k01() {
		return sync_k01;
	}

	public boolean isSync_photo() {
		return this.sync_photo;
	}
	
	public boolean isSync_fieldchange() {
		return this.sync_fieldchange;
	}
	
	public void setSync_fieldchange(boolean sync_fieldchange) {
		 this.sync_fieldchange = sync_fieldchange;
	}
	
	public void setSync_k01(boolean sync_k01) {
		this.sync_k01 = sync_k01;
	}

	public String getPostcodefields() {
		return postcodefields;
	}

	public void setPostcodefields(String postcodefields) {
		this.postcodefields = postcodefields;
	}

	/** ************触发器任务****************** */
	/**
	 * 同步机构
	 */
	public void importOrgDataTriggerMode() throws GeneralException {
		/*Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.conn);
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		int nlevel = Integer.parseInt(uplevel);
		if (nlevel > 0) {
			setNlevel(nlevel);
			// uplevelDeptTable("dept_table");
			uplevelDeptTableTriggerMode("dept_table", "UM");
		}*/
		String destTab = "t_org_view";
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer fieldname = new StringBuffer();
		creatOrgTable(destTab);
		buf.append("delete from " + destTab + " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }
			ArrayList sysout_list = getSysOutSyncFlag();// 得到外部系统代号
			addSysOutsyncFlag(destTab, sysout_list);// 将外部系统代号添加到同步表中
			// String userpwd=getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			// String field=this.orgcodefields;
			// 修改过
			String field = null;
			Iterator it = o_map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				fieldname.append((String) entry.getKey() + ",");

			}
			field = fieldname.toString();
//			if (field.length() <= 0)
//				return;
			String[] bfields = field.split(",");
			String fields = "", customfields = "";
			for (int i = 0; i < bfields.length; i++) {
				if(bfields[0] == null || bfields[0].length() <1){
					continue;
				}
				FieldItem item = DataDictionary.getFieldItem(bfields[i]);
				if (!"b0110".equalsIgnoreCase(item.getItemid())
						&& !"b01".equalsIgnoreCase(item.getFieldsetid())) {
                    continue;
                }
				fields += "," + bfields[i];
				customfields += ","
						+ this.getAppAttributeValue(HrSyncBo.B, bfields[i]);
			}
			buf.setLength(0);
			
			buf.append("INSERT INTO " + destTab + " ");
			buf.append("(UNIQUE_ID,b0110_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
			buf.append("grade,flag,sys_flag,sDate)");
			buf.append("(SELECT o.GUIDKEY,o.codeitemid,o.codesetid,o.codeitemdesc,o.parentid,o1.codeitemdesc as parentdesc,o.corcode,o.grade");
			buf.append("," + this.ADD_FLAG + " flag," + this.ADD_FLAG + " sys_flag");
			buf.append("," + Sql_switcher.dateValue(datestr));
			buf.append(" FROM organization o LEFT JOIN organization o1 ON o.parentid = o1.codeitemid WHERE " + Sql_switcher.isnull("o.codesetid", "'@K'") + " <> '@K' AND " + Sql_switcher.today() + " BETWEEN o.start_date AND o.end_date)");
			//System.out.println(buf.toString());
			dao.update(buf.toString());
			//buf.setLength(0);
			if (fields.length() > 0) {
				String fieldSet[] = fields.split(",");
				String cusromfieldSet[] = customfields.split(",");
				for(int i = 0;i<fieldSet.length;i++){
					if(fieldSet[i] != null && fieldSet[i].length()>1 && cusromfieldSet[i] != null && cusromfieldSet[i].length() >1){
						buf.setLength(0);
						buf.append("UPDATE " + destTab + " SET ");
						buf.append(cusromfieldSet[i]);
						buf.append("=(SELECT " + fieldSet[i]);
						buf.append(" FROM B01 b WHERE " + destTab + ".B0110_0 = b.B0110)");
						dao.update(buf.toString());
					}
				}
			}
			buf.setLength(0);
			String isnull = SystemConfig.getPropertyValue("sync_org_isnull");
			if("true".equalsIgnoreCase(isnull)){
				buf.append("UPDATE  " + destTab + " SET parentid = null,parentdesc=null WHERE B0110_0 = parentid");
				dao.update(buf.toString());
			}
			// 导入子集－把人员库中当前记录导入至视图t_hr_view
			importOrgSubSetData(destTab, "");
			/** 代码翻译 */
			if (!isBcode()) {
                updateOrgCodeItemdesc(destTab);
            } else if (this.orgcodefields !=null && this.orgcodefields.length() > 0) {
                updateOrgGivenCodeItemdesc(destTab, this.orgcodefields);
            }
			synchronizationInitOrgA0000(destTab);
			// 修改外部代号状态
			updateSysOutSyncFlag(destTab, sysout_list);
			importSyncFlagIDData(destTab, sysout_list);
			updateParentGuidkey(destTab);//同步更新 上级机构唯一标识
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 查询已启用的系统
	 */
	private ArrayList selectSysid() {
		String sql = "select sys_id from t_sys_outsync where state != 0";
		ArrayList sysid = new ArrayList();
		CommonData cda = new CommonData();
		cda.setDataName("全部");
		cda.setDataValue("全部");
		sysid.add(cda);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String str = rs.getString("sys_id");
				sysid.add(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sysid;
	}
	
	private ArrayList selectSysid2() {
		String sql = "select sys_id from t_sys_outsync where state != 0";
		ArrayList sysid = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String str = rs.getString("sys_id");
				sysid.add(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sysid;
	}

	/**
	 * 触发器初始化同步人员数据
	 * 
	 * @throws GeneralException
	 */
	public void importHrDataTriggerMode() throws GeneralException {

		/*Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.conn);
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		int nlevel = Integer.parseInt(uplevel);
		if (nlevel > 0) {
			setNlevel(nlevel);
			// uplevelDeptTable("dept_table");
			uplevelDeptTableTriggerMode("dept_table", "UM");
		}*/
		
		// 导入兼职信息
			
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 /**兼职参数*/
		String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid").toUpperCase();
		 /** 兼职单位 */
		String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
		 /** 兼职部门 */
		String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
		/**兼职职务*/
		String post_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		/**任免标志*/
		String appoint_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
		/**排序*/
		String order_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		
		
		String destTab = "t_hr_view";
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		creatHrTable(destTab);
		buf.append("delete from " + destTab);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		ResultSet rs = null;
		try {
			if (!dbw.isExistTable(destTab, false)) {
                return;
            }
			ArrayList sysout_list = getSysOutSyncFlag2();// 得到外部系统代号
			addSysOutsyncFlag(destTab, sysout_list);// 将外部系统代号添加到同步表中
			String userpwd = getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			String fields = getMainSetFields();
			String customfields = getCustomMainSetFiedlds();
			for (int i = 0; i < dblist.size(); i++) {
				buf.setLength(0);
				String dbname = (String) dblist.get(i);
				String hz_dbname = AdminCode.getCodeName("@@", dbname);
				buf.append("insert into " + destTab + " ");
				buf.append("(A0000,unique_id,nbase,nbase_0,A0100,");
				buf.append("B0110_0,E0122_0,A0101,E01A1_0,USERNAME,USERPASSWORD,");
				buf.append("flag,sys_flag,sDate");
				if (customfields.length() > 0) {
					buf.append(",");
					String cloumn = customfields;
					if (cloumn != null && cloumn.indexOf("a0101") != -1) {
						StringBuffer str = new StringBuffer();
						str.append(cloumn.substring(0, cloumn
										.indexOf("a0101")));
						;
						str.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						str.setLength(str.length() - 1);
						cloumn = str.toString();
					}
					buf.append(cloumn);
				}
				buf.append(")");
				buf.append("( select A0000,GUIDKEY,'" + hz_dbname
						+ "','" + dbname + "',");
				buf.append(" A0100,B0110,E0122,A0101,E01A1,");
				buf.append(userpwd);
				buf.append("," + this.ADD_FLAG + "," + this.ADD_FLAG + ","
						+ Sql_switcher.dateValue(datestr));
				if (fields.length() > 0) {
					buf.append(",");
					String cloumn = fields;
					if (cloumn != null && cloumn.indexOf("a0101") != -1
							&& customfields != null
							&& customfields.indexOf("a0100") != -1) {
						StringBuffer str = new StringBuffer();
						str.append(cloumn.substring(0, cloumn.indexOf("a0101")));
						;
						str.append((cloumn + ",").substring(cloumn
								.indexOf("a0101") + 6));
						str.setLength(str.length() - 1);
						cloumn = str.toString();
					}
					buf.append(cloumn);
				}
				buf.append(" from " + dbname + "a01");
				String only_field = getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
				if(only_field!=null&&only_field.length()>0)
				{
					buf.append(" where "+ SqlDifference.isNotNull(only_field));
				}
				buf.append(" )");
				//System.out.println(buf.toString());
				dao.update(buf.toString());
				/** 导入子集－把人员库中当前记录导入至视图t_hr_view */
				importSubSetData(destTab, dbname, "");
				
				if("1".equals(getAttributeValue(this.JZ_FIELD))){
					Map map = new HashMap();
					if(setid != null && setid.length() > 0 && unit_field != null && unit_field.length() > 0 && dept_field != null && dept_field.length() > 0 && appoint_filed != null && appoint_filed.length() > 0 && post_field != null && post_field.length() > 0){
						StringBuffer sqlStr = new StringBuffer();
						sqlStr.append("select a0100,");
						sqlStr.append(unit_field);
						sqlStr.append(" u,");
						sqlStr.append(dept_field);
						sqlStr.append(" d,");
						sqlStr.append(post_field);
						sqlStr.append(" p from ");
						sqlStr.append(dbname);
						sqlStr.append(setid);
						sqlStr.append(" where ");
						sqlStr.append(appoint_filed);
						sqlStr.append("='0'");
						sqlStr.append(" order by a0100,");
						
						if(order_filed != null && order_filed.trim().length() > 0) {
							sqlStr.append(order_filed);
						} else {
							sqlStr.append("i9999");
						}
						
						
						rs = dao.search(sqlStr.toString());
						
						while (rs.next()) {
							String a0100 = rs.getString("a0100");
							
							String unit = rs.getString("u");
							String dept = rs.getString("d");
							String post = rs.getString("p");
							
							if (map.containsKey(a0100)) {
								if (unit != null && unit.length() > 0 && dept != null && dept.length() > 0 && post != null && post.length() > 0) {
									String str = (String) map.get(a0100);
									map.put(a0100, str + "," + "/" + unit + "/" + dept + "/" + post + "/");
								}
							} else {
								if (unit != null && unit.length() > 0 && dept != null && dept.length() > 0 && post != null && post.length() > 0) {
									map.put(a0100, "/" + unit + "/" + dept + "/" + post + "/");
								}
							}
							
						}
						
						
						Iterator it = map.entrySet().iterator();
						sqlStr.delete(0, sqlStr.length());
						sqlStr.append("update ");
						sqlStr.append(destTab);
						sqlStr.append(" set jz_field=? where nbase_0=? and a0100=?");
						ArrayList list = new ArrayList();
						while (it.hasNext()) {
							ArrayList paramList = new ArrayList();
							Entry entry = (Entry)it.next();
							paramList.add((String) entry.getValue());
							paramList.add(dbname);
							paramList.add(entry.getKey());
							list.add(paramList);
						}
						
						dao.batchUpdate(sqlStr.toString(), list);
												
					} else {
						throw new GeneralException("请把人员兼职设置完整后在操作！");
					}
				}
				
				
				//修改corcode				
			}// for i loop end.			
			if (!isBcode())// **代码翻译*//*
            {
                updateCodeItemdesc(destTab,"");
            } else if (codefields != null && this.codefields.length() > 0) {
                updateGivenCodeItemdesc(destTab, this.codefields,"");
            }
			// 修改外部代号状态
			importCorcodeToHr(destTab);
			updateSysOutSyncFlag(destTab, sysout_list);
			importSyncFlagIDData(destTab, sysout_list);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void importSyncFlagIDData(String destTab, ArrayList sysout_list) {
		if (sysout_list == null || sysout_list.size() == 0) {
			return;
		}
		DbWizard dbw = new DbWizard(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		for (int i = 0; i < sysout_list.size(); i++) {
			String sysFlag = (String) sysout_list.get(i);
			
			String type = "A";
			if ("t_hr_view".equalsIgnoreCase(destTab)) {
				type = "A";
			} else if("t_org_view".equalsIgnoreCase(destTab)) {
				type = "B";
			} else if("t_post_view".equalsIgnoreCase(destTab)){
				type = "K";
			}
			
			String sql = "update " + destTab + " set " + sysFlag + "_id=(select " + sysFlag + "_id from t_sys_relation_id where unique_id=" + destTab+ ".unique_id)";
			if (dbw.isExistField(destTab, sysFlag + "_id", false) && dbw.isExistField("t_sys_relation_id", sysFlag + "_id",false)) {
				try {
					dao.update(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 触发器初始化同步岗位数据
	 * 
	 * @throws GeneralException
	 */
	public void importPostDataTriggerMode() throws GeneralException {

		/*Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.conn);
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		int nlevel = Integer.parseInt(uplevel);
		if (nlevel > 0) {
			setNlevel(nlevel);
			// uplevelDeptTable("dept_table");
			uplevelDeptTableTriggerMode("dept_table", "UM");
		}*/
		String destTab = "t_post_view";
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		StringBuffer buf = new StringBuffer();
		StringBuffer fieldname = new StringBuffer();
		creatPostTable(destTab);
		buf.append("delete from " + destTab + " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr = sdf.format(new Date());
		if (!dbw.isExistTable(destTab,true)) {
            return;
        }
		try {
			ArrayList sysout_list = getSysOutSyncFlag();// 得到外部系统代号
			addSysOutsyncFlag(destTab, sysout_list);// 将外部系统代号添加到同步表中
			// String userpwd=getUserPwd();
			/** 先清空视图数据 */
			dao.update(buf.toString());
			/** 先导入固定项目及人员主集指标项目 */
			// String field=this.orgcodefields;
			// 修改过
			String field = null;
			Iterator it = k_map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				fieldname.append((String) entry.getKey() + ",");

			}
			field = fieldname.toString();
//			if (field.length() <= 0)
//				return;
			String[] bfields = field.split(",");
			String fields = "", customfields = "";

			for (int i = 0; i < bfields.length; i++) {
				if(bfields[i] == null || bfields[i].length() < 1){
					continue;
				}
				FieldItem item = DataDictionary.getFieldItem(bfields[i]);
				if (item!=null && item.getFieldsetid()!=null && !"k01".equalsIgnoreCase(item.getFieldsetid())) {
					if (item!=null && item.getItemid()!=null && !"e0122".equalsIgnoreCase(item.getItemid())
							&& !"e01a1".equalsIgnoreCase(item.getItemid())) {
                        continue;
                    }
				}

				fields += "," + bfields[i];
				customfields += ","
						+ this.getAppAttributeValue(HrSyncBo.K, bfields[i]);
			}
			buf.setLength(0);
			
			buf.append("INSERT INTO " + destTab + " ");
			buf.append("(UNIQUE_ID,e01a1_0,codesetid,codeitemdesc,parentid,parentdesc,corcode,");
			buf.append("grade,flag,sys_flag,sDate)");
			buf.append("(SELECT o.GUIDKEY,o.codeitemid,o.codesetid,o.codeitemdesc,o.parentid,o1.codeitemdesc as parentdesc,o.corcode,o.grade");
			buf.append("," + this.ADD_FLAG + " flag," + this.ADD_FLAG + " sys_flag");
			buf.append("," + Sql_switcher.dateValue(datestr) + " sDate");
			buf.append(" FROM organization o INNER JOIN organization o1 ON o.parentid = o1.codeitemid WHERE " + Sql_switcher.isnull("o.codesetid", "'##'") + "='@K' AND " + Sql_switcher.today() + " BETWEEN o.start_date AND o.end_date)");
			dao.update(buf.toString());
			
			fields = "E0122" + fields;
			customfields = "E0122_0" + customfields;
			if (fields.length() > 0) {
				String fieldSet[] = fields.split(",");
				String cusromfieldSet[] = customfields.split(",");
				for(int i = 0;i<fieldSet.length;i++){
					if(fieldSet[i] != null && fieldSet[i].length()>1 && cusromfieldSet[i] != null && cusromfieldSet[i].length() >1){
						buf.setLength(0);
						buf.append("UPDATE " + destTab + " SET ");
						buf.append(cusromfieldSet[i]);
						buf.append("=(SELECT " + fieldSet[i]);
						buf.append(" FROM K01 k WHERE " + destTab + ".E01A1_0 = k.E01A1)");
						dao.update(buf.toString());
					}
				}
			}
			buf.setLength(0);
			// 导入子集－把人员库中当前记录导入至视图t_hr_view
			importPostSubSetData(destTab, "");

			/** 代码翻译 */
			if (!isBcode()) {
                updatePostCodeItemdesc(destTab);
            } else if (this.postcodefields != null && this.postcodefields.length() > 0) {
                updatePostGivenCodeItemdesc(destTab, this.postcodefields);
            }
			synchronizationInitPostA0000(destTab);
			updateSysOutSyncFlag(destTab, sysout_list);
			importSyncFlagIDData(destTab, sysout_list);
			updateParentGuidkey(destTab);//同步更新 上级机构唯一标识
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 添加外部系统关键字
	 * 
	 * @param destTable
	 * @param list
	 * @throws GeneralException
	 */

	public synchronized void addSysOutsyncFlag(String destTable, ArrayList list)
			throws GeneralException {
		if (list == null || list.size() <= 0) {
            return;
        }
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		if (!dbw.isExistTable(destTable, false)) {
            return;
        }
		ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
		String id = "";
		try {
			for (int i = 0; i < list.size(); i++) {
				id = (String) list.get(i);
				if (!reconstructionKqField.checkFieldSave(destTable, id)) {
					Field field = new Field(id, id);
					field.setDatatype(DataType.INT);
					Table table = new Table(destTable);
					table.addField(field);
					dbw.addColumns(table);
					dbmodel.reloadTableModel(destTable);

					ContentDAO dao = new ContentDAO(this.conn);
					if (dbw.isExistField(destTable, "sys_flag", false)) {
						dao.update("UPDATE " + destTable + " SET " + id	+ "=0 where sys_flag=3");
						dao.update("UPDATE " + destTable + " SET " + id	+ "=1 where sys_flag<>3");
					} 
					 //else
					//	dao.update("UPDATE " + destTable + " SET " + id + "=1");
				}
				
				if (!reconstructionKqField.checkFieldSave(destTable, id + "_id")) {
					// 外部系统ID字段，用来保存外部系统返回的ID
					Field field_id = new Field(id + "_id", id + "_id");
					field_id.setDatatype(DataType.STRING);
					field_id.setLength(1000);
					Table table = new Table(destTable);

					table.addField(field_id);

										
					dbw.addColumns(table);
					dbmodel.reloadTableModel(destTable);
				}
				
				// 在记录外部系统id中间表中添加字段
				if (!reconstructionKqField.checkFieldSave("t_sys_relation_id", id + "_id")) {
					Field field = new Field(id + "_id", id + "_id");
					field.setDatatype(DataType.STRING);
					field.setLength(1000);
					Table table = new Table("t_sys_relation_id");
					table.addField(field);
					
					dbw.addColumns(table);
					dbmodel.reloadTableModel("t_sys_relation_id");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

	/**
	 * 修改外部系统代号为新增状态
	 * 
	 * @param destTable
	 * @param list
	 */
	private void updateSysOutSyncFlag(String destTable, ArrayList list) {
		if (list == null || list.size() <= 0) {
            return;
        }
		StringBuffer buf = new StringBuffer();
		buf.append("update " + destTable + " set ");
		String id = "";
		for (int i = 0; i < list.size(); i++) {
			id = (String) list.get(i);
			buf.append(id + "=1,");
		}
		buf.setLength(buf.length() - 1);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除外部系统关键字 destTable 表名 list id列表
	 * 
	 * @return
	 */

	public synchronized void delSysOutsyncFlag(String destTable, ArrayList list)
			throws GeneralException {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		if (!dbw.isExistTable(destTable, false)) {
            return;
        }

		ReconstructionKqField reconstructionKqField = new ReconstructionKqField(
				this.conn);
		String id = "";
		try {
			for (int i = 0; i < list.size(); i++) {
				id = (String) list.get(i);
				if (reconstructionKqField.checkFieldSave(destTable, id)) {
					Field field = new Field(id, id);
					field.setDatatype(DataType.INT);
					Table table = new Table(destTable);
					table.addField(field);
					// 外部系统ID字段，用来保存外部系统返回的ID
					Field field_id = new Field(id + "_id", id + "_id");
					field.setDatatype(DataType.STRING);
					field.setLength(1000);
					if (dbw.isExistField(destTable, id + "_id", false)) {
						table.addField(field_id);
					}
					dbw.dropColumns(table);
					dbmodel.reloadTableModel(destTable);
				}
				if (reconstructionKqField.checkFieldSave(destTable, id + "_id")) {
					Table table = new Table(destTable);
									
					// 外部系统ID字段，用来保存外部系统返回的ID
					Field field_id = new Field(id + "_id", id + "_id");
					field_id.setDatatype(DataType.STRING);
				
					field_id.setLength(1000);

					table.addField(field_id);

					dbw.dropColumns(table);
					dbmodel.reloadTableModel(destTable);
				}
				
				// 在记录外部系统id中间表中删除字段
				if (reconstructionKqField.checkFieldSave("t_sys_relation_id", id + "_id")) {
					Field field = new Field(id + "_id", id + "_id");
					field.setDatatype(DataType.STRING);
					Table table = new Table("t_sys_relation_id");
					table.addField(field);
					
					dbw.dropColumns(table);
					dbmodel.reloadTableModel("t_sys_relation_id");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	public ArrayList getSysOutSyncFlag() {
		String sql = "select sys_id from t_sys_outsync where state=1";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				list.add(rs.getString("sys_id"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return list;
	}
	
	public ArrayList getSysOutSyncFlag2() {
		String sql = "select sys_id from t_sys_outsync where state=1";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				list.add(rs.getString("sys_id"));
				if (this.sync_photo) {
					list.add(rs.getString("sys_id")+"P");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return list;
	}

	/**
	 * 组织机构描述分层级
	 * 
	 * @param destTab
	 */
	public void uplevelDeptTableTriggerMode(String destTab, String setid) {
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		/** 新建临时表 */
		if (!dbw.isExistTable(destTab, false)) {
			Field field = new Field("codesetid", "codesetid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemid", "codeitemid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);

			field = new Field("codeitemdesc", "codeitemdesc");
			field.setDatatype(DataType.STRING);
			field.setLength(200);
			table.addField(field);
			field = new Field("parentid", "parentid");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			try {
				dbw.createTable(table);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			dbmodel.reloadTableModel(destTab);
		}
		String del = "delete from " + destTab;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(del);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer();
		sql.append("insert into " + destTab + "");
		sql.append("(codesetid,codeitemid,codeitemdesc)");
		sql.append("( select A.codesetid,A.codeitemid,");
		sql.append(" A.codeitemdesc");
		sql.append(" from organization A");
		sql.append(" where codesetid='UM' or codesetid='UN')");
		try {
			dao.insert(sql.toString(), new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql.setLength(0);
		sql.append("select codeitemid,codesetid from " + destTab);
		RowSet rs = null;
		try {
			String codeitemid = "";
//			String codesetid = "";
			String codeitemdesc = "";
			rs = dao.search(sql.toString());
			ArrayList list = new ArrayList();
			while (rs.next()) {

				codeitemid = rs.getString("codeitemid");
				codeitemdesc = getUplevelDescTriggerMode(codeitemid, setid, dao);
				ArrayList list_1 = new ArrayList();
				list_1.add(codeitemdesc);
				list_1.add(codeitemid);
				list.add(list_1);
			}
			sql.setLength(0);
			sql.append("update " + destTab
					+ " set codeitemdesc=? where codeitemid=?");
			dao.batchUpdate(sql.toString(), list);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}

	/**
	 * 触发器式层级关系
	 * 
	 * @param codeitemid
	 * @param setid
	 * @param dao
	 * @return
	 */
	private String getUplevelDescTriggerMode(String codeitemid, String setid,
			ContentDAO dao) {
		String sql = "select codeitemdesc,parentid from organization where codeitemid='"
				+ codeitemid + "' ";
		RowSet rs = null;
		String buf = "";
		try {
			String parentid = "";
			String codeitemdesc = "";
			rs = dao.search(sql.toString());
			if (rs.next()) {
				codeitemdesc = rs.getString("codeitemdesc");
				parentid = rs.getString("parentid");
			}
			if (parentid.equals(codeitemid)) {
//				return "/" + codeitemdesc;
				return codeitemdesc;
			} else {
//				buf = "/" + codeitemdesc;
				buf =  codeitemdesc;
				String b0110 = parentid;
				String codesetid = "";
//				boolean jump = false;
				do {
					String codeset[] = getB0100(b0110, conn);
					if (codeset != null && codeset.length >= 0) {

						codesetid = codeset[0];
						b0110 = codeset[1];
						codeitemdesc = codeset[2];
						if ("UM".equals(setid)
								&& ("UN".equals(codesetid) || ""
										.equals(codesetid))) {
							break;
						} // jump=true;
						buf =  codeitemdesc + "/" + buf;
						codeset = getB0100(b0110, conn);
					}
				} while (b0110 != null && b0110.length() > 0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return buf;
	}

	public String[] getB0100(String codeitemid, Connection conn) {
		String codeset[] = new String[3];
		String parentid = "";
		String codesetid = "";
		String codeitemdesc = "";
		RowSet rs = null;
		try {
			String orgSql = "SELECT parentid,codeitemid,codeitemdesc,codesetid  from organization where codeitemid='"
					+ codeitemid + "'";
			ContentDAO dao = new ContentDAO(conn);

			rs = dao.search(orgSql);
			if (rs.next()) {
				parentid = rs.getString("parentid");
				codesetid = rs.getString("codesetid");
				codeitemdesc = rs.getString("codeitemdesc");
				if (parentid.equals(codeitemid)) {
					codeset[0] = "";
					codeset[1] = "";
					codeset[2] = codeitemdesc;
				} else {
					codeset[0] = codesetid;
					codeset[1] = parentid;
					codeset[2] = codeitemdesc;
				}
			} else {
				codeset[0] = "";
				codeset[1] = "";
				codeset[2] = "";
				// throw GeneralExceptionHandler.Handle(new
				// GeneralException("",ResourceFactory.getProperty("kq.param.nosave.userbase"),"",""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return codeset;
	}

	public void createGuidkey(){
		String dbnamestr = getTextValue(HrSyncBo.BASE);//获得已选人员库
		String[] dbnames = dbnamestr.split(",");
		DbWizard db = new DbWizard(this.conn);
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		int DBMark = Sql_switcher.searchDbServer();
		try {
			if(dbnames != null && dbnamestr != null && dbnamestr.length() > 0){
				for(int i = 0;i< dbnames.length;i++){
					String tableName = dbnames[i]+"A01";
					if (!db.isExistField(tableName,"GUIDKEY",false)) {
						sql.setLength(0);
						if (DBMark == 1) {
							sql.append("ALTER TABLE " + tableName + " ADD GUIDKEY VARCHAR(38)");
						}else{
							sql.append("ALTER TABLE " + tableName + " ADD GUIDKEY VARCHAR2(38)");
						}
						dao.update(sql.toString());
					}
					initGuidKey(tableName);
				}
			}
			if (!db.isExistField("ORGANIZATION","GUIDKEY",false)) {
				sql.setLength(0);
				if (DBMark == 1) {
					sql.append("ALTER TABLE ORGANIZATION ADD GUIDKEY VARCHAR(38)");
				}else{
					sql.append("ALTER TABLE ORGANIZATION ADD GUIDKEY VARCHAR2(38)");
				}
				dao.update(sql.toString());
			}
			initGuidKey("ORGANIZATION");
			
			if (!db.isExistField("VORGANIZATION","GUIDKEY",false)) {
				sql.setLength(0);
				if (DBMark == 1) {
					sql.append("ALTER TABLE VORGANIZATION ADD GUIDKEY VARCHAR(38)");
				}else{
					sql.append("ALTER TABLE VORGANIZATION ADD GUIDKEY VARCHAR2(38)");
				}
				dao.update(sql.toString());
			}
			initGuidKey("VORGANIZATION");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void initGuidKey(String table){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update("UPDATE " + table + " SET GUIDKEY="+ this.getDBGuid() + " WHERE " + SqlDifference.isNull("GUIDKEY"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getDBGuid() {
		int DBMark = Sql_switcher.searchDbServer();
		String guid = "";
		int dbserver = Sql_switcher.searchDbServerFlag();
		if(dbserver == Constant.DAMENG) {
			guid = "guid()";
			return guid;
		}
		if (DBMark == 1) {
			guid = "newid()";
		} else if (DBMark == 2) {
			guid = "sys_guid()";
		}
		return guid;
	}
	/**
	 * 向人员表插入corcode
	 * @param destTab
	 */
	private void importCorcodeToHr(String destTab)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			 String srcTab="organization";//源表
			 String strJoin=destTab+".b0110_0="+srcTab+".codeitemid";//关联串  xxx.field_name=yyyy.field_namex,....
			 String strSet=destTab+".b0110_code="+srcTab+".corcode";//更新串  xxx.field_name=yyyy.field_namex,....
			 String strDWhere=Sql_switcher.isnull("b0110_0", "'##'")+"<>'##'";//更新目标的表过滤条件
			 String strSWhere=Sql_switcher.isnull("corcode", "'##'")+"<>'##'";
			 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
			// System.out.println(update);
			//修改单位指标
			dao.update(update, new ArrayList());
			//修改部门指标
			strJoin=destTab+".e0122_0="+srcTab+".codeitemid";//关联串  xxx.field_name=yyyy.field_namex,....
			strSet=destTab+".e0122_code="+srcTab+".corcode";//更新串  xxx.field_name=yyyy.field_namex,....
			strDWhere=Sql_switcher.isnull("e0122_0", "'##'")+"<>'##'";//更新目标的表过滤条件
			update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");			
			dao.update(update, new ArrayList());
			//修改职位指标
			strJoin=destTab+".e01a1_0="+srcTab+".codeitemid";//关联串  xxx.field_name=yyyy.field_namex,....
			strSet=destTab+".e01a1_code="+srcTab+".corcode";//更新串  xxx.field_name=yyyy.field_namex,....
			strDWhere=Sql_switcher.isnull("e01a1_0", "'##'")+"<>'##'";//更新目标的表过滤条件
			update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
			update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");			
			dao.update(update, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getDelWhere(String type, List dbList) {
		// 源表
		String srcTable = "";
		//目标表
		String desTable = "";
		// 清空某字段
		String clearnFile = "";
		if ("A".equals(type)) {
				for (int i = 0; i < dbList.size(); i++) {
					String dbName = (String)dbList.get(i);
					if (i != 0) {
						srcTable += " union all ";
					}
					
					srcTable += "select guidkey from " + dbName + "A01";
				}
				
				srcTable = "(" + srcTable + ")";
				desTable = "t_hr_view";
				clearnFile = "a0101";
			} else if ("B".equals(type)) {
				srcTable = "(select guidkey from organization where (codesetid='UM' or codesetid='UN') and " +Sql_switcher.sqlNow() + " between start_date and end_date )";
				desTable = "t_org_view";
				clearnFile = "codeitemdesc";
			} else if ("K".equals(type)) {
				srcTable = "(select guidkey from organization where (codesetid='@K') and " +Sql_switcher.sqlNow() + " between start_date and end_date )";
				desTable = "t_post_view";
				clearnFile = "codeitemdesc";
			}
		
		StringBuffer buff = new StringBuffer();
		buff.append("exists(select 1 from " +srcTable+ " a where a.guidkey=" + desTable + ".unique_id)");
		
		return buff.toString();
		
	}	

	public String getPhotoFields() {
		return photoFields;
	}

	public void setPhotoFields(String photoFields) {
		this.photoFields = photoFields;
	}
	/**
	 * 更新数据视图上级机构唯一标识  wangb 20170811
	 * @param destTab 视图表
	 */
	private void updateParentGuidkey(String destTab){
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "update "+ destTab +" set parentGuidkey=(select guidkey from organization o where "+ destTab +".parentid=o.codeitemid)";
		try {
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 保存时数据视图参数信息时 
	 * 检查数据视图 指标和翻译指标 在指标体系中存在
	 * 不存在指标和翻译指标 删除掉
	 * wangb  33246 20171208
	 * @param dao
	 * @param fieldtype  人员、单位、岗位 指标类型
	 * @param codefieldtype 人员、单位、岗位 翻译指标类型
	 */
	public void updateFieldAndCodeField(ContentDAO dao, int fieldtype ,int codefieldtype){
		String postfield = getTextValue(fieldtype);
		String[] postfields = postfield.split(",");
		for (int i = 0; i < postfields.length; i++) {
			FieldItem item=DataDictionary.getFieldItem(postfields[i]);
			if(item !=null) {
                continue;
            }
			if(i==0 && postfields[i] !=null && postfields[i].trim().length() > 0) {
                postfield = postfield.replaceAll(postfields[i]+",", "");
            } else if( i== postfields.length-1) {
                postfield = postfield.replaceAll(","+postfields[i], "");
            } else {
                postfield = postfield.replaceAll(","+postfields[i], "");
            }
			 delAppAttributeValue(K,postfields[i]);
		}
		setTextValue(fieldtype, postfield);
		String postCodefield = getTextValue(codefieldtype);
		postCodefield = filtration(postCodefield);
		setTextValue(codefieldtype, postCodefield);
		saveParameter(dao);
	}
	
	/**
	 * 数据视图缺少外部系统字段时,添加对应外部系统字段  wangb 20180321 bug 35643
	 * @param destable 数据视图
	 * @throws GeneralException 
	 */
	public void addSysOutsyncFlag(String destTab) throws GeneralException{
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		Table table = new Table(destTab);
		ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
		ArrayList sysout_list = getSysOutSyncFlag();// 得到外部系统代号
		for( int i = 0 ; i < sysout_list.size(); i++){
			String sysout = (String) sysout_list.get(i);
			/*判断是否存在外部系统字段  wangb */
			if (!reconstructionKqField.checkFieldSave(destTab,sysout)) {
				Field field = new Field(sysout,sysout);
				field.setDatatype(DataType.INT);
				table = new Table(destTab);
				table.addField(field);
				dbw.addColumns(table);
				dbmodel.reloadTableModel(destTab);
			}
		}
		
	}
}
