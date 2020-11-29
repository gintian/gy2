package com.hjsj.hrms.businessobject.general.template.templateanalyse;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.cyberneko.html.parsers.DOMParser;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHtml {

	private String htmlpath;
	private UserView userView;
	private String tabid;
	private Connection conn;
	private ArrayList inslist = new ArrayList();
	private String sp_batch;
	private ArrayList fielditemlist = new ArrayList();
	private ContentDAO dao = null;
	private String src_per = "";
	private String src_a0100 = "";
	private ArrayList partfieldlist = new ArrayList();
	private ArrayList parlist = new ArrayList();
	private ArrayList childlist = new ArrayList();
	private HashMap parttagmap = new HashMap();
	private boolean issubmitflag = false;
	private String error_fieldname = "";
	private String src_where = "";
	private String taskid = "";
	private TemplateDataBo dataBo = null;
	private String infor_type = "";

	public String getInfor_type() {
		return infor_type;
	}

	public void setInfor_type(String infor_type) {
		this.infor_type = infor_type;
	}

	public String getSrc_a0100() {
		return src_a0100;
	}

	public void setSrc_a0100(String src_a0100) {
		this.src_a0100 = src_a0100;
	}

	public String getSrc_per() {
		return src_per;
	}

	public void setSrc_per(String src_per) {
		this.src_per = src_per;
	}

	/**
	 * 构造器
	 * 
	 * @param filePath
	 */
	public ParseHtml(String htmlPath, UserView userView, String tabid, ArrayList inslist, String sp_batch,
			Connection conn) {
		this.htmlpath = htmlPath;
		this.userView = userView;
		this.tabid = tabid;
		this.conn = conn;
		this.inslist = inslist;
		this.sp_batch = sp_batch;
		this.dataBo = new TemplateDataBo(conn, userView, Integer.valueOf(tabid));
		getFieldList(); // 获得所有的这个模板里的字段
		dao = new ContentDAO(this.conn);
	}

	/**
	 * 构造器
	 * 
	 * @param filePath
	 */
	public ParseHtml(String htmlPath, UserView userView, String tabid, String taskid, ArrayList inslist,
			String sp_batch, Connection conn) {
		this.htmlpath = htmlPath;
		this.userView = userView;
		this.tabid = tabid;
		this.conn = conn;
		this.inslist = inslist;
		this.sp_batch = sp_batch;
		this.taskid = taskid;
		this.dataBo = new TemplateDataBo(conn, userView, Integer.valueOf(tabid));
		getFieldList(); // 获得所有的这个模板里的字段
		dao = new ContentDAO(this.conn);
	}

	public ParseHtml(String htmlPath, UserView userView, Connection conn, String tablename, String where) {
		this.htmlpath = htmlPath;
		this.userView = userView;
		this.conn = conn;
		getFieldList(tablename);
		dao = new ContentDAO(this.conn);
		this.src_where = where;
	}

	public void getFieldList(String tablename) {
		ArrayList fieldlist = DataDictionary.getFieldList(tablename, Constant.USED_FIELD_SET);// 字段名
		this.fielditemlist = fieldlist;
	}

	/*
	 * 
	 * 
	 */
	private void getFieldList() {
		try {
			TemplateTableBo tablebo = new TemplateTableBo(this.conn, Integer.parseInt(this.tabid), this.userView);
			this.fielditemlist = tablebo.getAllFieldItem();
		} catch (Exception e) {
			// System.out.println("fasdfasdf");
			e.printStackTrace();
		}
	}
	private String changeCodeIdValue(String fieldname, String fieldvalue,String disFormat,String formula) {
		String value = "";
		try {
			if (fieldvalue != null && fieldvalue.length() > 0) {
				/*
				 * for(int i=0;i<this.fielditemlist.size();i++) { FieldItem
				 * fielditem=(FieldItem)this.fielditemlist.get(i);
				 * if(fieldname.substring(0,5).equalsIgnoreCase(fielditem.
				 * getItemid())) { if("0".equals(fielditem.getCodesetid()))
				 * return fieldvalue; else return
				 * AdminCode.getCode(fielditem.getCodesetid(),fieldvalue)!=null?
				 * AdminCode.getCode(fielditem.getCodesetid(),fieldvalue).
				 * getCodename():""; } }
				 */
				FieldItem fielditem = DataDictionary.getFieldItem(fieldname.substring(0, 5));
				if (fielditem == null || "0".equals(fielditem.getCodesetid())){
					if("D".equalsIgnoreCase(fielditem.getItemtype())){
						if(disFormat.trim().length()==0){
							disFormat="-1";
						}
						if(StringUtils.isBlank(formula)){
							formula="";
						}
						fieldvalue=formatDateValue(fieldvalue,formula, Integer.parseInt(disFormat));
					}
					value=fieldvalue;
				}
				else{
					if("UM".equalsIgnoreCase(fielditem.getCodesetid())){
						Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
					  	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					  	if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                            display_e0122="0";
                        }
					  	if("0".equalsIgnoreCase(display_e0122)){
					  		value=AdminCode.getCode(fielditem.getCodesetid(), fieldvalue) != null
									? AdminCode.getCode(fielditem.getCodesetid(), fieldvalue).getCodename() : "";
					  	}else{
					  		CodeItem item=AdminCode.getCode("UM", fieldvalue,Integer.parseInt(display_e0122));
							if(item!=null)
					    	{
					    		value=item.getCodename();
							}
					    	else
					    	{
					    		value = AdminCode.getCodeName("UM",fieldvalue)!= null ? AdminCode.getCodeName("UM", fieldvalue): "";
					    	}
					  	}
					}else{
						String[] split = fieldvalue.split("`");//bug 36776 人事异动：模板中子集指标取最近1-2条，输出word时记录建议不要以~号区分开.
						for(int num=0;num<split.length;num++){
							if(num!=0){//代码型最后一个不加换行，否则页面显示和设计不同
								value+="\n";
							}
							value+=AdminCode.getCode(fielditem.getCodesetid(), split[num]) != null? AdminCode.getCode(fielditem.getCodesetid(), split[num]).getCodename() : "";
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	private String changeCodeIdValue(String fieldname, String fieldvalue) {
		String value = "";
		try {
			if (fieldvalue != null && fieldvalue.length() > 0) {

				/*
				 * for(int i=0;i<this.fielditemlist.size();i++) { FieldItem
				 * fielditem=(FieldItem)this.fielditemlist.get(i);
				 * if(fieldname.substring(0,5).equalsIgnoreCase(fielditem.
				 * getItemid())) { if("0".equals(fielditem.getCodesetid()))
				 * return fieldvalue; else return
				 * AdminCode.getCode(fielditem.getCodesetid(),fieldvalue)!=null?
				 * AdminCode.getCode(fielditem.getCodesetid(),fieldvalue).
				 * getCodename():""; } }
				 */
				FieldItem fielditem = DataDictionary.getFieldItem(fieldname.substring(0, 5));
				if (fielditem == null || "0".equals(fielditem.getCodesetid())){
					value= fieldvalue;
				}
				else {
                    value= AdminCode.getCode(fielditem.getCodesetid(), fieldvalue) != null
                            ? AdminCode.getCode(fielditem.getCodesetid(), fieldvalue).getCodename() : "";
                }

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 
	 *
	 */
	public void parseHtmlDocument() {
		try {
			DOMParser parser = new DOMParser();
			InputSource inputsource = new InputSource(new FormatHtml(this.htmlpath).formatHtmlDocument());
			parser.parse(inputsource);
			Document doc = parser.getDocument();
			// executeDocument(doc);
			// outTemplateDataDocument(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得templateDocument
	 */
	public Document getTemplateDocument() {
		try {
			DOMParser parser = new DOMParser();
			// FileInputStream stream=new FileInputStream(this.htmlpath);
			String str = new FormatHtml(this.htmlpath).formatHtmlDocument();
			byte[] bytes = str.getBytes();
			InputStream inputStream = new ByteArrayInputStream(bytes);

			InputSource inputsource = new InputSource(inputStream);
			parser.parse(inputsource);
			Document doc = parser.getDocument();
			return doc;
			// executeDocument(doc);
			// outTemplateDataDocument(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得模板head值
	 */
	public String getTemplateHeadDataValue() {
		try {
			String str = new FormatHtml(this.htmlpath).htmlHeadData();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * public void executeDocument(Node node) { Node child =
	 * node.getFirstChild(); while (child != null) { executeDocument(child);
	 * child = child.getNextSibling(); } }
	 */
	/**
	 * 获得table表中的指标名称
	 * 
	 * @param fieldstr
	 */
	private String getFieldName(String fieldstr) {
		String express = fieldstr.substring(fieldstr.indexOf("{*") + 2, fieldstr.indexOf("*}"));
		if (express.indexOf("(") != -1 && express.indexOf(")") != -1) {
            express = express.substring(express.indexOf("(") + 1, express.indexOf(")"));// {*姓名(A0101_1)*}兼容wlh
        } else {
			// {*列标题*}or{*现|拟[列标题]*}
			String fieldname = "";
			if (express.indexOf("[") != -1 && express.indexOf("]") != -1) {
				// {*现|拟[列标题]*}
				fieldname = express.substring(express.indexOf("[") + 1, express.indexOf("]"));
				if (express.trim().indexOf("现")==0) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname + "_1";
				} else if (express.trim().indexOf("拟")==0) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname + "_2";
				}else{
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname+"_1";//bug 35216 不加现或者拟默认按变化前处理
				}
			} else {
				fieldname=express;//bug 48576 fieldname没有赋值
				HashMap map = getFielditemid(fieldname);
				fieldname =(String) map.get("itemid");
				express = fieldname+"_1";//bug 35216 不加现或者拟默认按变化前处理
			}
		}
		return express;
	}

	/**
	 * 得到指标名称
	 * 
	 * @param name
	 * @return
	 */
	private HashMap getFielditemid(String name) {
		// liuyz 导出单人和多人模版
		String sql = "select field_name,sub_domain,disFormat,formula from template_set where (flag='A' or flag='B') and "
				+ Sql_switcher.trim("REPLACE(REPLACE(hz,'`',''),'','')") + "='" + name + "' and tabid=" + tabid;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String itemid = "";
		String disFormat = "";
		String formula="";
		try {
			rs = dao.search(sql);
			if (rs.next()){
				itemid = rs.getString(1);
				disFormat=rs.getString(3);
				formula=rs.getString(4);
				String sub_domain = Sql_switcher.readMemo(rs, "sub_domain");
				// 获得sub_domain_id
				String sub_domain_id = "";
				if (sub_domain != null && sub_domain.trim().length() > 0) {
					try{
						org.jdom.Document doc = PubFunc.generateDom(sub_domain);
						String xpath = "/sub_para/para";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
																	// xpath="/sub_para/para";
						List childlist = findPath.selectNodes(doc);
						if (childlist != null && childlist.size() > 0) {
							org.jdom.Element element = (Element) childlist.get(0);
							if (element.getAttributeValue("id") != null) {
								sub_domain_id = (String) element.getAttributeValue("id");
								if (sub_domain_id != null && sub_domain_id.trim().length() > 0) {
                                    itemid+="_"+sub_domain_id;
                                }
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((itemid == null || itemid.length() <= 0) && name != null && "单位名称".equals(name)) {
            itemid = "b0110";
        }
		// liuyz bug25502 //liuyz 导出单人和多人模版
		if ((itemid == null || itemid.length() <= 0) && name != null && "岗位名称".equals(name)) {
            itemid = "e01a1";
        }
		if ((itemid == null || itemid.length() <= 0) && name != null && "职位名称".equals(name)) {
            itemid = "e01a1";
        }
		HashMap map=new HashMap();
		map.put("itemid", itemid);
		map.put("disFormat",disFormat);
		map.put("formula",formula);
		return map;
	}

	/**
	 * 得到指标名称
	 * 
	 * @param name
	 *            子集指标名称
	 * @param fieldSetId
	 *            子集代码 A04
	 * @return liuyz 单人模版多人模版支持word输出
	 */
	private String getFielditemid(String name, String fieldSetId) {
		// liuyz 导出单人和多人模版
		String sql = "select itemid from fielditem where  itemdesc='" + name + "' and fieldSetId='" + fieldSetId
				+ "' and useFlag=1";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String itemid = "";
		try {
			rs = dao.search(sql);
			if (rs.next()) {
                itemid = rs.getString(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			PubFunc.closeDbObj(rs);
		}
		if ((itemid == null || itemid.length() <= 0) && name != null && "单位名称".equals(name)) {
            itemid = "b0110";
        }
		// liuyz bug25502 //liuyz 导出单人和多人模版
		if ((itemid == null || itemid.length() <= 0) && name != null && "岗位名称".equals(name)) {
            itemid = "e01a1";
        }
		return itemid;
	}

	/**
	 * 得到指标名称
	 * 
	 * @param name
	 * @return
	 */
	private String getFieldSetid(String name) {
		String sql = "select fieldsetid from fieldset where customdesc='" + name + "'";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String itemid = "";
		try {
			rs = dao.search(sql);
			if (rs.next()) {
                itemid = rs.getString(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemid;
	}

	// 查找子集根据用户设置的子集标题查找
	private HashMap getFieldSetidSub(String name) {
		HashMap fieldItemMap = new HashMap();
		String sql = "select setName,sub_domain from template_set where (flag='A' or flag='B') and "
				+ Sql_switcher.trim("REPLACE(REPLACE(REPLACE(REPLACE(hz,'`',''),'',''),'{',''),'}','')") + "='" + name
				+ "' and tabid=" + tabid;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String itemid = "";
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				String setName = rs.getString("setName");
				fieldItemMap.put("setName", setName);
				String sub_domain = Sql_switcher.readMemo(rs, "sub_domain");
				// 获得sub_domain_id
				String sub_domain_id = "";
				if (sub_domain != null && sub_domain.trim().length() > 0) {
					fieldItemMap.put("sub_domain", sub_domain);
					try {
						org.jdom.Document doc =PubFunc.generateDom(sub_domain);
						String xpath = "/sub_para/para";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
																	// xpath="/sub_para/para";
						List childlist = findPath.selectNodes(doc);
						if (childlist != null && childlist.size() > 0) {
							org.jdom.Element element = (Element) childlist.get(0);
							if (element.getAttributeValue("id") != null) {
								sub_domain_id = (String) element.getAttributeValue("id");
								if (sub_domain_id != null && sub_domain_id.trim().length() > 0) {
                                    fieldItemMap.put("sub_domain_id", sub_domain_id);
                                }
							}
						}
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldItemMap;
	}

	/**
	 * @param fieldstr
	 * @return
	 */
	private String getTdTitleValue(String fieldstr) {

		if (fieldstr.indexOf("(") != -1 && fieldstr.indexOf(")") != -1) {//// {*姓名(A0101_1)*}兼容wlh
			String express = fieldstr.substring(fieldstr.indexOf("{*"), fieldstr.indexOf("*}") + 2);
			// liuyz fieldstr拿到的值可能远大于express导致截取字符串出现问题。begin
			int endIndex = fieldstr.indexOf("(");
			return "[" + express.substring(express.indexOf("{*") + 2, endIndex) + "]";
		} else {
			// {*列标题*}or{*现|拟[列标题]*}
			String express = fieldstr.substring(fieldstr.indexOf("{*") + 2, fieldstr.indexOf("*}"));
			String fieldname = "";
			if (express.indexOf("[") != -1 && express.indexOf("]") != -1) {
				// {*现|拟[列标题]*}
				fieldname = express.substring(express.indexOf("[") + 1, express.indexOf("]"));
				return "[" + fieldname + "]";
			} else {
				return "[" + express + "]";
			}
		}
	}

	/**
	 * 括起来的为当前记录的指标值.
	 * 
	 * @param tablename
	 * @param fieldstr
	 * @param nid
	 * @param values
	 *            待解析的字符串
	 * @return liuyz 单人模版多人模版支持word输出
	 * @throws Exception
	 */
	private String getCurIndexValue(String tablename, String fieldstr, String nid, String values)  {
		try{
		String express = fieldstr.substring(fieldstr.indexOf("{&") + 2, fieldstr.indexOf("&}"));
		// {*列标题*}or{*现|拟[列标题]*}
		String fieldname = "";
		String disFormat="";
		String formula="";
		if (express.indexOf(":") == -1) {
			String value = "";
			if (express.indexOf("[") != -1 && express.indexOf("]") != -1) {
				// {*现|拟[列标题]*}
				fieldname = express.substring(express.indexOf("[") + 1, express.indexOf("]"));
				Boolean isPhoto = false;
				
				if (express.indexOf("现[") != -1 || express.indexOf("现") == 0) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					disFormat=(String) map.get("disFormat");
					formula=(String) map.get("formula");
					express = fieldname + "_1";
				} else if (express.indexOf("拟[") != -1 || express.indexOf("拟") == 0) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					disFormat=(String) map.get("disFormat");
					formula=(String) map.get("formula");
					express = fieldname + "_2";
				} else{
					if ("photo".equalsIgnoreCase(fieldname)) {
						express = " photo,ext ";
						isPhoto = true;
					}else{
						HashMap map = getFielditemid(fieldname);
						fieldname =(String) map.get("itemid"); //bug 35216 不加现或者拟默认按变化前处理
						disFormat=(String) map.get("disFormat");
						formula=(String) map.get("formula");
						express = fieldname + "_1";
					}
				}
				StringBuffer sql = new StringBuffer();
				
				sql.append("select " + express + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					// liuyz 导出单人和多人模版
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");//bug 35560、35562 取不出来值。
					if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
						sql.append(" and ins_id in (select ins_id from t_wf_task where task_id in (" + taskid + "))");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					RowSet rs = dao.search(sql.toString());
					if (rs.next()) {
						if (isPhoto) {
							value = ServletUtilities.createOleFile("photo", "ext", rs);
							if (value != null && value.trim().length() > 0){
								String path=System.getProperty("java.io.tmpdir");
								if(path.endsWith("\\")){
									value = System.getProperty("java.io.tmpdir") + value;
								}else{
									value = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + value;
								}
							}
						} else {
                            value =String.valueOf(rs.getObject(1));
                        }
					}
				} catch (Exception e) {
					e.printStackTrace();
					fieldstr = fieldstr;
				}
			} else {
				// liuyz 导出单人和多人模版
				Boolean isPhoto = false;
				if ("photo".equalsIgnoreCase(express)) {
					express = " photo,ext ";
					isPhoto = true;
				}else{
					HashMap map=getFielditemid(express);
					disFormat=(String) map.get("disFormat");
					fieldname =(String) map.get("itemid"); 
					formula=(String) map.get("formula");
					express = fieldname + "_1";
				}
				StringBuffer sql = new StringBuffer();
				sql.append("select " + express + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
					if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
						sql.append(" and ins_id in (select ins_id from t_wf_task where task_id in (" + taskid + "))");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					
					RowSet rs = dao.search(sql.toString());
					if (rs.next()){
						if (isPhoto) {
							value = ServletUtilities.createOleFile("photo", "ext", rs);
							if (value != null && value.trim().length() > 0){
								String photoPath=System.getProperty("java.io.tmpdir").replace("/", File.separator).replace("\\", File.separator);
								if(!photoPath.endsWith(File.separator)){
									photoPath=photoPath+File.separator;
								}
								value = photoPath + value;
							}
						} else {
                            value = rs.getString(1);
                        }
					}
				} catch (Exception e) {
					e.printStackTrace();
					fieldstr = fieldstr;
				}
			}
			if (!" photo,ext ".equalsIgnoreCase(express)) {//照片不需要走这个方法，否则会把照片路径清空。
				value = changeCodeIdValue(express, value,disFormat,formula).replace("`", "\n");
			}
			fieldstr = value;
			} else {
				// {&子集名称:指标名称&}
				String[] field = express.split(":");
				if (field.length > 1) {
					String fieldsete = field[0];
					String fielddesc = field[1];
					String fieldsetid = "";
					String sub_domainid = "";
					String sub_domain = "";
					if (fielddesc.indexOf("现") != -1 || fielddesc.indexOf("拟") != -1) {
						if (fielddesc.indexOf("现") != -1) {
							String prv = fielddesc.substring(fielddesc.indexOf("[") + 1, fielddesc.indexOf("]"));
							HashMap map = getFieldSetidSub(prv);
							if (map.containsKey("setName")) {
                                fieldsetid = (String) map.get("setName");
                            }
							if (map.containsKey("sub_domain_id")) {
                                sub_domainid = "_" + (String) map.get("sub_domain_id");
                            }
							if (map.containsKey("sub_domain")) {
                                sub_domain = (String) map.get("sub_domain");
                            }
							fieldname = "T_" + fieldsetid + sub_domainid + "_1";
	
						} else if (fielddesc.indexOf("拟") != -1) {
							String prv = fielddesc.substring(fielddesc.indexOf("[") + 1, fielddesc.indexOf("]"));
							HashMap map = getFieldSetidSub(prv);
							if (map.containsKey("setName")) {
                                fieldsetid = (String) map.get("setName");
                            }
							if (map.containsKey("sub_domain_id")) {
                                sub_domainid = "_" + (String) map.get("sub_domain_id");
                            }
							if (map.containsKey("sub_domain")) {
                                sub_domain = (String) map.get("sub_domain");
                            }
							fieldname = "T_" + fieldsetid + sub_domainid + "_1";
						}
					} else {
						fieldsetid = getFieldSetid(fieldsete);
						fieldname = "T_" + fieldsetid + "_1";
					}
					String columnName = getFielditemid(fielddesc, fieldsetid);
					StringBuffer sql = new StringBuffer();
					sql.append("select " + fieldname + " from " + tablename);
					if (nid != null && nid.length() > 3) {
						// liuyz 导出单人和多人模版
						sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");//bug 35560、35562 取不出来值。
						if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
							sql.append(" and task_id=(select pri_task_id from t_wf_task where task_id in (" + taskid + "))");
						}
					}
					ParseAsyXml parseAsyXml = new ParseAsyXml(this.conn);
					if(columnName!=null){
						FieldItem fieldItem = DataDictionary.getFieldItem(columnName);
						ArrayList valuelist = parseAsyXml.getFiledsetValue(sql.toString(), fieldname, columnName, sub_domain);
					}
				} else {
					fieldstr = fieldstr;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldstr;
	}

	/**
	 * 括起来的为当前记录的指标值.
	 * 
	 * @param tablename
	 * @param fieldstr
	 * @param nid
	 * @return
	 * @throws Exception
	 */
	private void getCurIndexValue(String tablename, String fieldstr, String nid, Node node) throws Exception {
		String express = fieldstr.substring(fieldstr.indexOf("{&") + 2, fieldstr.indexOf("&}"));
		// {*列标题*}or{*现|拟[列标题]*}
		String fieldname = "";
		String disFormat="";
		String formula="";
		if (express.indexOf(":") == -1) {
			String value = "";
			if (express.indexOf("[") != -1 && express.indexOf("]") != -1) {
				// {*现|拟[列标题]*}
				fieldname = express.substring(express.indexOf("[") + 1, express.indexOf("]"));
				if (express.indexOf("现") != -1) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					disFormat=(String) map.get("disFormat");
					formula=(String) map.get("formula");
					express = fieldname + "_1";
				} else if (express.indexOf("拟") != -1) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					disFormat=(String) map.get("disFormat");
					formula=(String) map.get("formula");
					express = fieldname + "_2";
				}
				StringBuffer sql = new StringBuffer();
				sql.append("select " + express + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					// liuyz 导出单人和多人模版
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
					if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
						sql.append(" and task_id=(select pri_task_id from t_wf_task where task_id in( " + taskid + "))");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					RowSet rs = dao.search(sql.toString());
					if (rs.next()) {
                        value = rs.getString(1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
					node.setNodeValue(fieldstr);
				}
			} else {
				// liuyz 导出单人和多人模版
				HashMap map=getFielditemid(express);
				fieldname =(String) map.get("itemid");
				disFormat=(String) map.get("disFormat");
				formula=(String) map.get("formula");
				express = fieldname + "_1";
				StringBuffer sql = new StringBuffer();
				sql.append("select " + express + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
					if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
						sql.append(" and task_id=(select pri_task_id from t_wf_task where task_id in (" + taskid + "))");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					RowSet rs = dao.search(sql.toString());
					if (rs.next()) {
                        value = rs.getString(1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
					node.setNodeValue(fieldstr);
				}
			}
			value = changeCodeIdValue(express, value,disFormat,formula);
			node.setNodeValue(value);
		} else {
			// {&子集名称:指标名称&}
			String[] field = express.split(":");
			if (field.length > 1) {
				String fieldsete = field[0];
				String fielddesc = field[1];
				String sub_domainid = "";
				String sub_domain = "";
				String fieldsetid = "";
				if (fielddesc.indexOf("现") != -1 || fielddesc.indexOf("拟") != -1) {
					if (fielddesc.indexOf("现") != -1) {
						String prv = fielddesc.substring(fielddesc.indexOf("[") + 1, fielddesc.indexOf("]"));
						HashMap map = getFieldSetidSub(prv);
						if (map.containsKey("setName")) {
                            fieldsetid = (String) map.get("setName");
                        }
						if (map.containsKey("sub_domain_id")) {
                            sub_domainid = "_" + (String) map.get("sub_domain_id");
                        }
						if (map.containsKey("sub_domain")) {
                            sub_domain = (String) map.get("sub_domain");
                        }
						fieldname = "T_" + fieldsetid + sub_domainid + "_1";
					} else if (fielddesc.indexOf("拟") != -1) {
						String prv = fielddesc.substring(fielddesc.indexOf("[") + 1, fielddesc.indexOf("]"));
						HashMap map = getFieldSetidSub(prv);
						if (map.containsKey("setName")) {
                            fieldsetid = (String) map.get("setName");
                        }
						if (map.containsKey("sub_domain_id")) {
                            sub_domainid = "_" + (String) map.get("sub_domain_id");
                        }
						if (map.containsKey("sub_domain")) {
                            sub_domain = (String) map.get("sub_domain");
                        }
						fieldname = "T_" + fieldsetid + sub_domainid + "_1";
					}
				} else {
					fieldsetid = getFieldSetid(fieldsete);
					fieldname = "T_" + fieldsetid + "_1";
				}
				String columnName = getFielditemid(fielddesc, fieldsetid);
				StringBuffer sql = new StringBuffer();
				sql.append("select " + fieldname + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					// liuyz 导出单人和多人模版
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
					if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
						sql.append(" and task_id=(select pri_task_id from t_wf_task where task_id in(" + taskid + "))");
					}
				}
				if(columnName!=null){
					FieldItem fieldItem = DataDictionary.getFieldItem(columnName);
					ParseAsyXml parseAsyXml = new ParseAsyXml(this.conn);
					ArrayList valuelist = parseAsyXml.getFiledsetValue(sql.toString(), fieldname, columnName, sub_domain);
				}
			} else {
				node.setNodeValue(fieldstr);
			}
		}
	}

	/**
	 * 根据模板生成数据文档
	 * 
	 * @param node
	 * @param tablename
	 * @param nid
	 */
	private ArrayList tableexpress = new ArrayList(); // 保存某个table中的所有表达式
	private boolean isfieldvalue = false; // table中否表达式开始
	private String fieldvalue = ""; // 一个或者多个完整表达式的字符串值
	private boolean isparagrphvalue = false; // 段落中是否表达式开始
	private boolean isconstantvalue = false; // 常量中是否表达式开始
	private boolean isprivindexvalue = false; // 当前指标
	private boolean isindexcodevalue = false; // 指标函数分解{@年|月|日(日期指标)@}”
	private boolean isprocessvalue = false;
	private boolean isPartvalue = false;
	private String partfieldvalue = "";
	private String nid = "";
	private String questionid = "";
	private String current_id = "";

	public void executeTemplateDocument(Node node, String tablename, String nid, LazyDynaBean paramBean)
			throws Exception {
		String[] nidarray = nid.split("`",3);
		nid=nidarray[0]+"`"+nidarray[1];
		String objectid="";
		for(int i=0;i<nidarray.length;i++){
			objectid=objectid+nidarray[i];
			if(i!=nidarray.length-1) {
                objectid=objectid+"`";
            }
		}
		// 工作流输出word，新增求视图-常量（髙怀云）
		if (paramBean.get("questionid") != null) {
			this.questionid = paramBean.get("questionid").toString();
		}
		if (paramBean.get("current_id") != null) {
            this.current_id = paramBean.get("current_id").toString();
        }
		Node child = node.getFirstChild();
		while (child != null) {

			executeTemplateDocument(child, tablename, nid, paramBean);
			// 处理table中的内容
			/** wlh版显示记录数据，或通过特殊函数显示记录数据的 */
			// if(child.getNodeValue()!=null&&child.getNodeValue().indexOf("身份说明")!=-1)
			/*
			 * System.out.println("###"+this.fieldvalue);
			 * System.out.println(child.getNodeName()+"###"+child.getNodeValue()
			 * );
			 */
			if ("TITLE".equalsIgnoreCase(child.getNodeName())) {
				this.tableexpress = new ArrayList();
				this.partfieldvalue = "";
				this.fieldvalue = "";
				this.childlist = new ArrayList();
				this.isPartvalue = false;
				this.isparagrphvalue = false;
				;
			}
			if (child.getNodeValue() != null && child.getNodeValue().indexOf(")]};") != -1) {
				this.partfieldvalue += child.getNodeValue();
				this.childlist.add(child.getNodeValue());
				this.isPartvalue = false;
				child.setNodeValue(expPartString(tablename, this.partfieldvalue, nid, this.childlist));
			}
			if (this.isPartvalue) {
				this.partfieldvalue += child.getNodeValue();
				this.childlist.add(child.getNodeValue());
				child.setNodeValue("");
			}
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("{*") != -1) {
				// liuyz 导出单人和多人模版
				String tempStr = child.getNodeValue();
				this.fieldvalue += child.getNodeValue();
				this.isfieldvalue = true;
				child.setNodeValue("");
				if (tempStr.indexOf("*}") != -1) {
					this.isfieldvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						String tempvalue = getFieldName(this.fieldvalue);
						if (tempvalue != null && tempvalue.trim().length() > 0) {
							this.tableexpress.add(getFieldName(this.fieldvalue));
						}
						// this.tableexpress.add(getFieldName(this.fieldvalue));
						child.setNodeValue(getTdTitleValue(this.fieldvalue));
						// liuyz 导出单人和多人模版
						if (tempStr.lastIndexOf("{*") == 0) {
							this.fieldvalue = "";
							this.isfieldvalue = false;
							child = child.getNextSibling();
						} else {
							this.fieldvalue = "{*";
							this.isfieldvalue = true;
							child = child.getNextSibling();
						}
						continue;
					}
				}
			}
			if (this.isfieldvalue && child.getNodeValue() != null && child.getNodeValue().indexOf("*}") != -1) {
				this.fieldvalue += child.getNodeValue();
				this.isfieldvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					String tempvalue = getFieldName(this.fieldvalue);
					if (tempvalue != null && tempvalue.trim().length() > 0) {
						this.tableexpress.add(getFieldName(this.fieldvalue));
					}
					// this.tableexpress.add(getFieldName(this.fieldvalue));
					child.setNodeValue(getTdTitleValue(this.fieldvalue));
					this.fieldvalue = "";
					this.isfieldvalue = false;
				}
			}

			if (this.isfieldvalue && child.getNodeValue() != null && child.getNodeValue().trim().length() > 0
					&& child.getNodeValue().indexOf("*}") == -1) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
			}
			/** wlh版显示记录数据，或通过特殊函数显示记录数据的，，结束 */
			/** 记录数据定义的表格 ***/
			if ("table".equalsIgnoreCase(child.getNodeName())) {
				this.isfieldvalue = false;
				if (this.tableexpress.size() > 0) {
                    setTableElementData(child, this.tableexpress, tablename);
                }
				this.tableexpress = new ArrayList();
			}
			/**** {&指标名称&}---括起来的为当前记录的指标值,只适用于个人的 ***/
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("{&") != -1) {
				// liuyz 导出单人和多人模版
				String tempStr = child.getNodeValue().replace("{&", "");
				this.fieldvalue += child.getNodeValue();
				this.isprivindexvalue = true;
				child.setNodeValue("");
				// liuyz 导出单人和多人模版
				if (tempStr.indexOf("&}") != -1) {
					this.isprivindexvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						getCurIndexValue(tablename, this.fieldvalue, nid, child);
						// liuyz 导出单人和多人模版
						this.fieldvalue = "{&";
						this.isprivindexvalue = true;
						child = child.getNextSibling();
						continue;
					}
				}
			}
			if (this.isprivindexvalue && child.getNodeValue() != null && child.getNodeValue().indexOf("&}") != -1) {
				this.fieldvalue += child.getNodeValue();
				this.isprivindexvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					getCurIndexValue(tablename, this.fieldvalue, nid, child);
					this.fieldvalue = "";
					this.isprivindexvalue = false;
				}
			}
			if (this.isprivindexvalue && child.getNodeValue() != null && child.getNodeValue().trim().length() > 0
					&& child.getNodeValue().indexOf("{&") == -1) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
			}
			/**** {&指标名称&}---括起来的为当前记录的指标值 ***/
			/***/
			// 处理段落中的内容
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("{[") != -1) {
				String nodevalue = child.getNodeValue();
				this.fieldvalue += child.getNodeValue();
				this.isparagrphvalue = true;
				// child.setNodeValue("");
				if (nodevalue.indexOf("{[分段(") != -1) {
					this.isPartvalue = true;
					if (child.getNodeValue().indexOf(")]};") != -1) {
						this.error_fieldname = this.fieldvalue;
						child.setNodeValue(getNodeValue(this.fieldvalue, tablename));
						this.isPartvalue = false;
					} else {
						this.partfieldvalue += nodevalue;
						this.childlist = new ArrayList();
						this.childlist.add(nodevalue);
					}
				} else if (child.getNodeValue().indexOf("]}") != -1) {
					this.isparagrphvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						child.setNodeValue(getNodeValue(this.fieldvalue, tablename));
					}
				} else {
                    child.setNodeValue("");
                }

			}
			if (this.isparagrphvalue && child.getNodeValue() != null && child.getNodeValue().indexOf("]}") != -1
					&& !this.isPartvalue) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
				if (this.fieldvalue.lastIndexOf("![endif]") != -1) {
                    this.fieldvalue = this.fieldvalue.substring(this.fieldvalue.lastIndexOf("![endif]") + 8);
                }
				this.isparagrphvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					if (this.fieldvalue.indexOf("{[分段(") != -1) {
						this.isPartvalue = true;
						if (child.getNodeValue().indexOf(")]};") != -1) {
							this.error_fieldname = this.fieldvalue;
							child.setNodeValue(getNodeValue(this.fieldvalue, tablename));
							this.isPartvalue = false;
						} else {
							this.partfieldvalue += this.fieldvalue;
							this.childlist = new ArrayList();
							this.childlist.add(this.fieldvalue);
						}
					} else {
						this.error_fieldname = this.fieldvalue;
						child.setNodeValue(getNodeValue(this.fieldvalue, tablename));
					}

				}
			}
			if (this.isparagrphvalue && child.getNodeValue() != null && child.getNodeValue().trim().length() > 0
					&& child.getNodeValue().indexOf("{[") == -1) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
			}
			// 处理常量内容
			// .......待扩展中....
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("{#") != -1) {
				// liuyz 导出单人和多人模版优化
				String tempStr = child.getNodeValue();
				this.fieldvalue += child.getNodeValue();
				this.isconstantvalue = true;
				child.setNodeValue("");// 跟下一节点分开
				if (tempStr.indexOf("#}") != -1)// 当括号标识和所求内容在一起时
				{
					this.isconstantvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						child.setNodeValue(getConstantValue(this.fieldvalue, tablename));
						// liuyz 导出单人和多人模版优化
						child = child.getNextSibling();
						this.fieldvalue = "{#";
						this.isconstantvalue = true;
						continue;
					}
				}
			}
			if (this.isconstantvalue && child.getNodeValue() != null && child.getNodeValue().indexOf("#}") != -1) {
				this.fieldvalue += child.getNodeValue();
				this.isconstantvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					child.setNodeValue(getConstantValue(this.fieldvalue, tablename));
				}
			}
			if (this.isconstantvalue && child.getNodeValue() != null && child.getNodeValue().trim().length() > 0
					&& child.getNodeValue().indexOf("{#") == -1) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
			}
			/***** 指标函数分解{@年|月|日(日期指标)@} **/
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("{@") != -1) {
				if (child.getNodeValue().indexOf("{@审批流程") != -1) {
					this.isprocessvalue = true;
					if (child.getNodeValue().indexOf("]}") != -1) {
						this.isprocessvalue = false;
						if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
							this.error_fieldname = this.fieldvalue;
							// liuyz 导出单人和多人模版优化
							child.setNodeValue(getProcessValue(this.fieldvalue, tablename, objectid));
							this.fieldvalue = "";
							this.isindexcodevalue = false;
						}
					}
				} else {
					this.fieldvalue += child.getNodeValue();
					this.isindexcodevalue = true;
					child.setNodeValue("");
					if (child.getNodeValue().indexOf("]}") != -1) {
						this.isindexcodevalue = false;
						if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
							this.error_fieldname = this.fieldvalue;
							// liuyz 导出单人和多人模版优化
							child.setNodeValue(getFunIndexValue(this.fieldvalue, tablename, nid));
						}
					}
				}
			}
			if ((this.isindexcodevalue||this.isprocessvalue) && child.getNodeValue() != null && child.getNodeValue().indexOf("@}") != -1) {
				if (!this.fieldvalue.equalsIgnoreCase(child.getNodeValue())) {
                    this.fieldvalue += child.getNodeValue();
                }
				if (this.fieldvalue .indexOf("{@审批流程") != -1) {
					this.isprocessvalue=true;
					this.isindexcodevalue=false;
				}
				if (this.isprocessvalue) {
					this.isprocessvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						// liuyz 导出单人和多人模版优化
						child.setNodeValue(getProcessValue(this.fieldvalue, tablename, objectid));
						this.fieldvalue = "";
						this.isindexcodevalue = false;
					}
				} else {
					this.isindexcodevalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						// liuyz 导出单人和多人模版优化
						child.setNodeValue(getFunIndexValue(this.fieldvalue, tablename, nid));
						this.fieldvalue = "";
						this.isindexcodevalue = false;
					}
				}
			}
			if (this.isindexcodevalue && child.getNodeValue() != null && child.getNodeValue().trim().length() > 0
					&& child.getNodeValue().indexOf("{@") == -1) {
				this.fieldvalue += child.getNodeValue();
				child.setNodeValue("");
			}
			/** {@结束@} **/

			child = child.getNextSibling();
		}
	}

	/**
	 * @param tablename
	 * @param nid
	 * @param value
	 *            待解析的字符串
	 * @param paramBean
	 *            参数
	 * @return liuyz 单人模版多人模版支持word输出
	 */
	public String executeTemplateDocument(String value, String tablename, String nid, LazyDynaBean paramBean)
			throws Exception {
		String[] nidarray = nid.split("`",3);
		nid=nidarray[0]+"`"+nidarray[1];
		String objectid="";
		for(int i=0;i<nidarray.length;i++){
			objectid=objectid+nidarray[i];
			if(i!=nidarray.length-1) {
                objectid=objectid+"`";
            }
		}
		String returnValue = "";
		// 工作流输出word，新增求视图-常量（髙怀云）
		if (paramBean.get("questionid") != null) {
			this.questionid = paramBean.get("questionid").toString();
		}
		if (paramBean.get("current_id") != null) {
            this.current_id = paramBean.get("current_id").toString();
        }
		// 处理table中的内容
		/** wlh版显示记录数据，或通过特殊函数显示记录数据的 */
		// if(value!=null&&value.indexOf("身份说明")!=-1)
		/*
		 * System.out.println("###"+this.fieldvalue);
		 * System.out.println(child.getNodeName()+"###"+value);
		 */
		this.tableexpress = new ArrayList();
		this.partfieldvalue = "";
		this.fieldvalue = "";
		this.childlist = new ArrayList();
		this.isPartvalue = false;
		this.isparagrphvalue = false;
		;
		if (value != null && value.indexOf(")]};") != -1) {
			this.partfieldvalue += value;
			this.childlist.add(value);
			this.isPartvalue = false;
			returnValue = expPartString(tablename, this.partfieldvalue, nid, this.childlist);
			return returnValue;
			// System.out.println(this.partfieldvalue);
		}
		if (this.isPartvalue) {
			this.partfieldvalue += value;
			this.childlist.add(value);
			returnValue = "";
		}
		if (value != null && value.indexOf("{*") != -1) {
			// liuyz 导出单人和多人模版
			String tempStr = value;
			this.fieldvalue += value;
			this.isfieldvalue = true;
			returnValue = "";
		}
		if (this.isfieldvalue && value != null && value.indexOf("*}") != -1) {
			this.fieldvalue += value;
			this.isfieldvalue = false;
			if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
				this.error_fieldname = this.fieldvalue;
				String tempvalue = getFieldName(this.fieldvalue);
				if (tempvalue != null && tempvalue.trim().length() > 0) {
					this.tableexpress.add(getFieldName(this.fieldvalue));
				}
				// this.tableexpress.add(getFieldName(this.fieldvalue));
				returnValue = getTdTitleValue(this.fieldvalue);
				this.fieldvalue = "";
				this.isfieldvalue = false;
			}
		}

		if (this.isfieldvalue && value != null && value.trim().length() > 0 && value.indexOf("*}") == -1) {
			this.fieldvalue += value;
			returnValue = "";
		}
		/**** {&指标名称&}---括起来的为当前记录的指标值,只适用于个人的 ***/
		if (value != null && value.indexOf("{&") != -1) {
			// liuyz 导出单人和多人模版
			String tempStr = value.replace("{&", "");
			this.fieldvalue += value;
			this.isprivindexvalue = true;
			returnValue = "";
		}
		if (this.isprivindexvalue && value != null && value.indexOf("&}") != -1) {
			this.fieldvalue += value;
			this.isprivindexvalue = false;
			if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
				this.error_fieldname = this.fieldvalue;
				returnValue = getCurIndexValue(tablename, this.fieldvalue, nid, value);
				this.fieldvalue = "";
				this.isprivindexvalue = false;
			}
		}
		if (this.isprivindexvalue && value != null && value.trim().length() > 0 && value.indexOf("{&") == -1) {
			this.fieldvalue += value;
			returnValue = "";
		}
		/**** {&指标名称&}---括起来的为当前记录的指标值 ***/
		/***/
		// 处理段落中的内容
		if (value != null && value.indexOf("{[") != -1) {
			String nodevalue = value;
			this.fieldvalue += value;
			this.isparagrphvalue = true;
			// child.setNodeValue("");
			if (nodevalue.indexOf("{[分段(") != -1) {
				this.isPartvalue = true;
				if (value.indexOf(")]};") != -1) {
					this.error_fieldname = this.fieldvalue;
					returnValue = getNodeValue(this.fieldvalue, tablename);
					this.isPartvalue = false;
				} else {
					this.partfieldvalue += nodevalue;
					this.childlist = new ArrayList();
					this.childlist.add(nodevalue);
				}
			} else if (value.indexOf("]}") != -1) {
				this.isparagrphvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					returnValue = getNodeValue(this.fieldvalue, tablename);
				}
			} else {
                returnValue = "";
            }

		}
		if (this.isparagrphvalue && value != null && value.indexOf("]}") != -1 && !this.isPartvalue) {
			this.fieldvalue += value;
			returnValue = "";
			if (this.fieldvalue.lastIndexOf("![endif]") != -1) {
                this.fieldvalue = this.fieldvalue.substring(this.fieldvalue.lastIndexOf("![endif]") + 8);
            }
			this.isparagrphvalue = false;
			if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
				if (this.fieldvalue.indexOf("{[分段(") != -1) {
					this.isPartvalue = true;
					if (value.indexOf(")]};") != -1) {
						this.error_fieldname = this.fieldvalue;
						returnValue = getNodeValue(this.fieldvalue, tablename);
						this.isPartvalue = false;
					} else {
						this.partfieldvalue += this.fieldvalue;
						this.childlist = new ArrayList();
						this.childlist.add(this.fieldvalue);
					}
				} else {
					this.error_fieldname = this.fieldvalue;
					returnValue = getNodeValue(this.fieldvalue, tablename);
				}

			}
		}
		if (this.isparagrphvalue && value != null && value.trim().length() > 0 && value.indexOf("{[") == -1) {
			this.fieldvalue += value;
			returnValue = "";
		}
		// 处理常量内容
		// .......待扩展中....
		if (value != null && value.indexOf("{#") != -1) {
			// liuyz 导出单人和多人模版优化
			String tempStr = value;
			this.fieldvalue = value;
			this.isconstantvalue = true;
			returnValue = "";// 跟下一节点分开
		}
		if (this.isconstantvalue && value != null && value.indexOf("#}") != -1) {
			this.fieldvalue = value;
			this.isconstantvalue = false;
			if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
				this.error_fieldname = this.fieldvalue;
				returnValue = getConstantValue(this.fieldvalue, tablename);
			}
		}
		if (this.isconstantvalue && value != null && value.trim().length() > 0 && value.indexOf("{#") == -1) {
			this.fieldvalue += value;
			returnValue = "";
		}
		/***** 指标函数分解{@年|月|日(日期指标)@} **/
		if (value != null && value.indexOf("{@") != -1) {
			if (value.indexOf("{@审批流程") != -1) {
				this.isprocessvalue = true;
				this.fieldvalue += value;
				if (value.indexOf("]}") != -1) {
					this.isprocessvalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						// liuyz 导出单人和多人模版优化
						returnValue = getProcessValue(this.fieldvalue, tablename, objectid);
					}
				}

			} else {
				this.fieldvalue += value;
				this.isindexcodevalue = true;
				returnValue = "";
				if (value.indexOf("]}") != -1) {
					this.isindexcodevalue = false;
					if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
						this.error_fieldname = this.fieldvalue;
						// liuyz 导出单人和多人模版优化
						returnValue = getFunIndexValue(this.fieldvalue, tablename, nid);
					}
				}
			}
		}
		if ((this.isindexcodevalue || this.isprocessvalue) && value != null && value.indexOf("@}") != -1) {
			if (!this.fieldvalue.equals(value)) {
                this.fieldvalue += value;
            }
			if (this.isprocessvalue) {
				this.isprocessvalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					// liuyz 导出单人和多人模版优化
					returnValue = getProcessValue(this.fieldvalue, tablename, objectid);
					this.fieldvalue = "";
					this.isprocessvalue = false;
				}
			} else {
				this.isindexcodevalue = false;
				if (this.fieldvalue != null && this.fieldvalue.trim().length() > 4) {
					this.error_fieldname = this.fieldvalue;
					// liuyz 导出单人和多人模版优化
					returnValue = getFunIndexValue(this.fieldvalue, tablename, nid);
					this.fieldvalue = "";
					this.isindexcodevalue = false;
				}
			}
		}
		if ((this.isindexcodevalue || this.isprocessvalue) && value != null && value.trim().length() > 0
				&& value.indexOf("{@") == -1) {
			this.fieldvalue += value;
			returnValue = "";
		}
		return returnValue;
		/** {@结束@} **/
	}

	/**
	 * 获得常量node的值
	 * 
	 * @param value
	 * @param tablename
	 */
	private String getConstantValue(String value, String tablename) {
		String express = "";
		StringBuffer valuebuffer = new StringBuffer();
		boolean ishas = true;
		if (value != null && value.indexOf("{#") != -1 && value.indexOf("#}") != -1) {
			express = value.substring(value.indexOf("{#"), value.indexOf("#}") + 2);
			ishas = true;
		} else {
			ishas = false;
			return value;
		}

		while (ishas) {
			String constantvalue = getConstantExpressValue(express, tablename);
			// String valuestr = "";
			valuebuffer.append(value.substring(0, value.indexOf(express)));
			valuebuffer.append(constantvalue);
			value = value.substring(value.indexOf(express) + express.length());
			if (value != null && value.indexOf("{#") != -1) {
				valuebuffer.append(value.substring(0, value.indexOf("{#")));
				value = value.substring(value.indexOf("{#"));
				valuebuffer.append(value.substring(0, value.indexOf("{#")));
			} else {
                valuebuffer.append(value);
            }

			if (value != null && value.indexOf("{#") != -1 && value.indexOf("#}") != -1) {
				express = value.substring(value.indexOf("{#"), value.indexOf("#}") + 2);
				ishas = true;
			} else {
				ishas = false;
				if (value != null && value.indexOf("{#") != -1) {
					valuebuffer.append(value.substring(0, value.indexOf("{#")));
					this.fieldvalue = value.substring(0, value.indexOf("{#"));
					this.isconstantvalue = true;
				} else {
					this.fieldvalue = "";
					this.isconstantvalue = false;
				}
			}
		}
		return valuebuffer.toString();
	}

	/**
	 * 获得常量表达式的值
	 * 
	 * @param value
	 */
	private String getConstantExpressValue(String express, String tablename) {
		String value = "";
		Calendar calendar = Calendar.getInstance();
		String constantexpress = express.substring(express.indexOf("{#") + 2, express.indexOf("#}"));
		if ("求年份".equals(constantexpress)) {
			value = String.valueOf(calendar.get(Calendar.YEAR));
		} else if ("求月份".equalsIgnoreCase(constantexpress)) {
			value = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		} else if ("求天".equalsIgnoreCase(constantexpress)) {
			value = String.valueOf(calendar.get(Calendar.DATE));
		} else if ("当前日期".equalsIgnoreCase(constantexpress)) {
			value = calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1) + "."
					+ calendar.get(Calendar.DATE);
		}else if ("当前时间".equalsIgnoreCase(constantexpress)) {//bug 35227 系统常量 {#当前时间#} 不能导出
			value = calendar.get(Calendar.HOUR_OF_DAY) + ":" + (calendar.get(Calendar.MINUTE)<10?"0"+calendar.get(Calendar.MINUTE):calendar.get(Calendar.MINUTE)) + ":"
					+ (calendar.get(Calendar.SECOND)<10?"0"+calendar.get(Calendar.SECOND):calendar.get(Calendar.SECOND));
		} else if ("制表人".equalsIgnoreCase(constantexpress)) {
			value = this.userView.getUserFullName();
		} else if ("记录数".equalsIgnoreCase(constantexpress)) {
			try {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(*) as a from " + tablename);
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql.toString());
				if (rs.next()) {
                    value = rs.getString(1);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("问卷计划号".equalsIgnoreCase(constantexpress)) {
			value = this.questionid;

		} else if ("CURRENT_ID".equalsIgnoreCase(constantexpress)) {
			value = this.current_id;
		} else if ("序号".equalsIgnoreCase(constantexpress)) {//bug 35236 序号不能导出
			value = "{#序号#}";
		}
		return value;
	}

	/**
	 * 设置table的值
	 * 
	 * @param value
	 * @param tablename
	 * @param tableexpress
	 */
	private void setTableElementData(Node node, ArrayList tableexpress, String tablename) throws Exception {
		NodeList nodelist = node.getChildNodes();
		int sum = 0;
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) != null && "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) {
                sum++;
            }
		}
		String sql = getTableElementSql(tableexpress, tablename);
		try {
			// System.out.println(sql);
			List rs = ExecuteSQL.executeMyQuery(sql, this.conn);
			// System.out.println(rs.getMaxRows());
			if (!rs.isEmpty()) {
				if (sum - 1 > rs.size()) {
					int n = nodelist.getLength();
					for (int i = 0; i < rs.size(); n--) {

						if (nodelist.item(n) != null && "tr".equalsIgnoreCase(nodelist.item(n).getNodeName())) {
							node.removeChild(nodelist.item(n));
							i++;
						} else if (nodelist.item(n) != null) {
							node.removeChild(nodelist.item(n));
						}
					}
				} else {
					boolean first = true;
					for (int i = 0; i < nodelist.getLength();) {
						if (nodelist.item(i) != null && "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) {
							if (first) {
								first = false;
								i++;
							} else {
								node.removeChild(nodelist.item(i));
							}

						} else {
							if (nodelist.item(i) != null && first == false) {
                                node.removeChild(nodelist.item(i));
                            } else {
                                i++;
                            }
						}
					}
				}
				for (int i = 0; i < sum - 1 && i < rs.size(); i++) {
					Node trchild = nodelist.item(1).cloneNode(true);
					LazyDynaBean rec = (LazyDynaBean) rs.get(i);
					ArrayList values = new ArrayList();
					for (int j = 0; j < tableexpress.size(); j++) {
						values.add(rec.get(tableexpress.get(j).toString().toLowerCase()) != null
								? changeCodeIdValue(tableexpress.get(j).toString().toLowerCase(),
										rec.get(tableexpress.get(j).toString().toLowerCase()).toString())
								: "");
					}
					setTableElementValue(trchild, values);

					// datalist.add(rec.get(fieldname.toLowerCase())!=null?rec.get(fieldname.toLowerCase()).toString():"");
					if (sum - 1 > rs.size()) {
						node.insertBefore(trchild, nodelist.item(3));
					} else {
                        node.appendChild(trchild);
                    }
				}
			}
			setTableTitle(nodelist.item(1));
		} catch (Exception e) {
			this.setError_fieldname(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 设置table的值
	 * 
	 * @param value
	 * @param tablename
	 * @param tableexpress
	 */
	/*
	 * private void setTableElementData(Node node,ArrayList tableexpress,String
	 * tablename,Connection conns,String s) { NodeList nodelist =
	 * node.getChildNodes(); int sum=0; for(int i=0;i<nodelist.getLength();i++)
	 * { if(nodelist.item(i)!=null &&
	 * "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) sum++; } String
	 * sql = getTableElementSql(tableexpress, tablename); try { List rs =
	 * ExecuteSQL.executeMyQuery(sql,conns); if(!rs.isEmpty()){
	 * if(sum-1>rs.size()) { int n=nodelist.getLength(); for(int
	 * i=0;i<rs.size();n--) { if(nodelist.item(n)!=null &&
	 * "tr".equalsIgnoreCase(nodelist.item(n).getNodeName())) {
	 * node.removeChild(nodelist.item(n)); i++; } else
	 * if(nodelist.item(n)!=null) node.removeChild(nodelist.item(n)); } }else {
	 * boolean first=true; for(int i=0;i<nodelist.getLength();) {
	 * if(nodelist.item(i) !=null &&
	 * "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) { if(first) {
	 * first=false; i++; } else { node.removeChild(nodelist.item(i)); }
	 * 
	 * }else { if(nodelist.item(i)!=null && first==false)
	 * node.removeChild(nodelist.item(i)); else i++; } } } for (int i =
	 * 0;i<sum-1 && i < rs.size(); i++) { Node trchild =
	 * nodelist.item(1).cloneNode(true); LazyDynaBean rec = (LazyDynaBean)
	 * rs.get(i); ArrayList values=new ArrayList(); for (int j = 0; j <
	 * tableexpress.size(); j++) {
	 * values.add(rec.get(tableexpress.get(j).toString().toLowerCase())!=null?
	 * changeCodeIdValue(tableexpress.get(j).toString().toLowerCase(),rec.get(
	 * tableexpress.get(j).toString().toLowerCase()).toString()):""); }
	 * 
	 * setTableElementValue(trchild,values);
	 * 
	 * // datalist.add(rec.get(fieldname.toLowerCase())!=null?rec.get(fieldname.
	 * toLowerCase()).toString():""); if(sum-1>rs.size()) {
	 * node.insertBefore(trchild,nodelist.item(3)); }else
	 * node.appendChild(trchild); } } setTableTitle(nodelist.item(1)); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */
	/**
	 * 设置table的值
	 * 
	 * @param num
	 *            要反回的数据条数
	 * @param tableexpress
	 *            带解析的内容
	 * @param tablename
	 *            表明
	 */
	private ArrayList setTableElementData(Integer num, ArrayList tableexpress, String tablename)  {
		ArrayList hzValues = new ArrayList();
		String sql = getTableElementSql(tableexpress, tablename);
		try {
			List rs = ExecuteSQL.executeMyQuery(sql, this.conn);
			for (int i = 0; (i < num && i < rs.size())||(num<0&& i < rs.size()); i++) {
				LazyDynaBean rec = (LazyDynaBean) rs.get(i);
				ArrayList values = new ArrayList();
				for (int j = 0; j < tableexpress.size(); j++) {
					String key=tableexpress.get(j).toString().toLowerCase();
					if(key.startsWith("{#")&&key.endsWith("#}")){//{#XX#}取的是常量，不再拼接到sql中去取。
						values.add(getConstantValue(key,tablename));
					}else{
						values.add(rec.get(key) != null
							? changeCodeIdValue(key,rec.get(key).toString()): "");
					}
				}
				hzValues.add(values);
			}
			
		} catch (Exception e) {
			this.setError_fieldname(e.getMessage());
			e.printStackTrace();
		}
		return hzValues;
	}

	/**
	 * 设置table的title信息
	 * 
	 * @param node
	 */
	public void setTableTitle(Node node) {
		Node child = node.getFirstChild();
		while (child != null) {
			setTableTitle(child);
			if (child.getNodeValue() != null && child.getNodeValue().indexOf("[") != -1
					&& child.getNodeValue().indexOf("]") != -1) {
				if ("求年龄".equals(child.getNodeValue().substring(1, child.getNodeValue().indexOf("]")))) {
                    child.setNodeValue("年龄");
                } else {
                    child.setNodeValue(child.getNodeValue().substring(1, child.getNodeValue().indexOf("]")));
                }

			}
			child = child.getNextSibling();
		}
	}

	/**
	 * 设置table的element值
	 * 
	 * @param node
	 * @param value
	 */
	public void setTableElementValue(Node node, ArrayList value) {
		Node child = node.getFirstChild();
		while (child != null) {
			setTableElementValue(child, value);
			child.setNodeValue(getTableElementValue(child.getNodeValue(), value));

			child = child.getNextSibling();
		}
	}

	/**
	 * 获得table的element值
	 * 
	 * @param node
	 * @param value
	 */
	public String getTableElementValue(String value, ArrayList values) {
		if (value != null && value.indexOf("[") != -1 && value.indexOf("]") != -1) {
			if ("求年龄".equals(value.substring(value.indexOf("[") + 1, value.indexOf("]")))) {
				value = GetHisAge(values.get(0).toString());
				values.remove(0);
			} else {
				value = values.get(0).toString();
				values.remove(0);
			}
		}
		return value;
	}

	/**
	 * 获得table中element的sql语句
	 * 
	 * @param tableexpress
	 */
	private String getTableElementSql(ArrayList tableexpress, String tablename) {
		FieldItem item = null;// 指标项
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		StringBuffer selectFiel = new StringBuffer();
		for (int i = 0; i < tableexpress.size(); i++) {
			/**
			 * gaohy 2015-11-6 针对日期型字段，按规定长度截取
			 * 
			 */
			if (i != 0) {
                selectFiel.append(",");
            }
			String itemid = (String) tableexpress.get(i);
			//35236 增加常量在表格中输出。
			if(itemid.startsWith("{#")&&itemid.endsWith("#}")){
				String value=getConstantValue(itemid,tablename);
				selectFiel.append("'"+itemid+"'");
			}
			else if (itemid.indexOf("_") != -1){
				itemid = itemid.substring(0, itemid.indexOf("_"));// 获得字段
				if(StringUtils.isNotBlank(itemid)){
					item = DataDictionary.getFieldItem(itemid);// 获得字段属性
					if (item!=null&& "D".equalsIgnoreCase(item.getItemtype())) {// 日期型 item可能为空
						switch (Sql_switcher.searchDbServer()) {
						case Constant.MSSQL: {
							selectFiel.append("CONVERT(char(" + item.getItemlength() + ")," + tableexpress.get(i) + ",120)");// 截取规定长度
							break;
						}
						case Constant.DB2: {
							selectFiel.append(Sql_switcher.substr(
									Sql_switcher.dateToChar(tableexpress.get(i).toString(), "yyyy-mm-dd HH24:mi:ss"), "0",
									item.getItemlength() + ""));// 截取规定长度
							break;
						}
						case Constant.ORACEL: {
							selectFiel.append(Sql_switcher.substr(
									Sql_switcher.dateToChar(tableexpress.get(i).toString(), "yyyy-mm-dd HH24:mi:ss"), "0",
									item.getItemlength() + ""));// 截取规定长度
							break;
						}
						}
						selectFiel.append(" " + tableexpress.get(i));// 增加别名
					} else {
                        selectFiel.append(tableexpress.get(i));
                    }
				}
			}
		}
		if(StringUtils.isBlank(selectFiel.toString())){
			selectFiel.append("''");
		}
		sql.append(selectFiel.toString());
		sql.append(" from ");
		sql.append(tablename);
		sql.append(" where ");
		sql.append(getConditionSql("", tablename));
		return sql.toString();
	}

	/**
	 * 获得node的值
	 * 
	 * @param value
	 * @param tablename
	 */
	private String getNodeValue(String value, String tablename) {
		String express = "";
		StringBuffer valuebuffer = new StringBuffer();
		boolean ishas = true;
		// System.out.println(value);

		if (value != null && value.indexOf("{[") != -1 && value.indexOf("]}") != -1) {
			express = value.substring(value.indexOf("{["), value.indexOf("]}") + 2);
			ishas = true;
		} else {
			ishas = false;
			return value;
		}
		while (ishas) {
			ArrayList valuelist = getExpressValue(express, tablename);
			String valuestr = "";
			String expresspre = express.substring(0, 2); // expresspre={[ 或者
															// [指 前缀
			String lv = null;
			String expresstop = express.substring(express.indexOf(expresspre) + 2, express.indexOf("("));
			expresstop = expresstop != null && expresstop.length() > 0 ? expresstop : "";
			if ("段".equals(expresstop))// {[段(求列表([B0110_1,A0101,C,…],[B0110_1]))v|h(*|n)]}
			{
				String expresslast = express.substring(express.indexOf("))") + 2); // 后表达式
				if (expresslast != null && expresslast.trim().length() >= 1) {
                    lv = expresslast.substring(0, 1);
                }
			} else {
				String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
				if (expresslast != null && expresslast.trim().length() >= 1) {
                    lv = expresslast.substring(0, 1);
                }
			}
			if ("{[".equals(expresspre)) {
				for (int i = 0; i < valuelist.size(); i++) {
					if (valuelist.get(i) == null || valuelist.get(i).toString().length() <= 0) {
                        continue;
                    }
					if ("求年龄".equals(express.substring(value.indexOf("{[") + 2, value.indexOf("(")))) {
						if (i > 0) {
                            if (lv != null && "v".equalsIgnoreCase(lv)) {
                                valuestr += "xrywlh888" + GetHisAge(valuelist.get(i).toString());
                            } else if (valuestr.length() > 0) {
                                valuestr += "、" + GetHisAge(valuelist.get(i).toString());
                            } else {
                                valuestr += GetHisAge(valuelist.get(i).toString());
                            }
                        } else {
                            valuestr += GetHisAge(valuelist.get(i).toString());
                        }
					} else {
						if (i > 0) {
                            if (lv != null && "v".equalsIgnoreCase(lv)) {
                                valuestr += "xrywlh888" + valuelist.get(i).toString();
                            } else if (valuestr.length() > 0) {
                                valuestr += "、" + valuelist.get(i).toString();
                            } else {
                                valuestr += valuelist.get(i).toString();
                            }
                        } else {
                            valuestr += valuelist.get(i).toString();
                        }
					}
				}
			}
			valuebuffer.append(value.substring(0, value.indexOf(express)));
			valuebuffer.append(valuestr);
			value = value.substring(value.indexOf(express) + express.length());
			if (value.indexOf("{[") != -1) {
				valuebuffer.append(value.substring(0, value.indexOf("{[")));
				value = value.substring(value.indexOf("{["));
			} else {
				valuebuffer.append(value);
			}
			if (value != null && value.indexOf("{[") != -1 && value.indexOf("]}") != -1) {
				express = value.substring(value.indexOf("{["), value.indexOf("]}") + 2);
				ishas = true;
			} else {
				ishas = false;
				if (value.indexOf("{[") != -1) {
					valuebuffer.append(value.substring(0, value.indexOf("{[")));
					this.fieldvalue = value.substring(0, value.indexOf("{["));
					this.isparagrphvalue = true;
				} else {
					this.fieldvalue = "";
					this.isparagrphvalue = false;
				}
			}
		}
		return valuebuffer.toString();
	}

	/**
	 * 获得表达式的sql
	 * 
	 * @param express
	 * @param tablename
	 */
	private ArrayList getExpressValue(String express, String tablename) {
		// Connection conn=null;
		String fieldname = "";
		ArrayList datalist = new ArrayList();
		String expresspre = express.substring(0, 2); // expresspre={[ 或者 [指 前缀
		if (express.indexOf("{[段") != -1 || express.indexOf("{[子集段") != -1) {

			if ("{[".equals(expresspre)) {
				datalist = getFieldNamesFieldDuan(express, tablename);
			}
		}
		/**
		 * gaohy 新增情况 新增求视图情况，并增加{#问卷计划号#}和{# CURRENT_ID#}
		 **/
		else if (express.indexOf("{[求视图_") != -1) {// 新增求视图情况，并增加{#问卷计划号#}和{#
													// CURRENT_ID#}

			StringBuffer sqlbuf = new StringBuffer();// 获取sql
			String base = express.substring(6, express.indexOf("("));// 表名
			String fieldName = express.substring(express.indexOf("(") + 1, express.indexOf(")"));// 所求字段
			sqlbuf.append("select " + fieldName + " from " + base + " where ");
			String condition = express.substring(express.lastIndexOf("(") + 1, express.lastIndexOf(")"));// 条件部分
			String condiValueOne = condition.replaceAll("\\{#", "\\|{#");
			String condiValueTwo = condiValueOne.replaceAll("\\#}", "\\#}|");

			String conditionValue[] = condiValueTwo.split("\\|");
			StringBuffer condit = new StringBuffer();// 最终得到的sql条件
			for (int i = 0; i < conditionValue.length; i++) {// 对条件部分condition进行处理
				if (conditionValue[i].indexOf("{#") != -1 && conditionValue[i].indexOf("#}") != -1) {
					conditionValue[i] = getConstantExpressValue(conditionValue[i], tablename);// 得到常量的值

				}
				condit.append(conditionValue[i]);
			}

			sqlbuf.append(condit);
			try {
				RowSet rs = dao.search(sqlbuf.toString());
				while (rs.next()) {
					datalist.add(rs.getString(fieldName));// 查询结果
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("统计".equals(express.substring(express.indexOf("{[") + 2, express.indexOf("{[") + 4)))// 求统计
		{
			if ("{[".equals(expresspre)) {
				fieldname = express.substring(express.indexOf("(") + 1, express.indexOf(")")); // A0101
				FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
				String sql = analyseOtherExpress(express, tablename);
				// System.out.println(express);
				// System.out.println(sql);
				if ("统计求和".equals(express.substring(express.indexOf("{[") + 2, express.indexOf("(")))) {
					try {
						// List rs = ExecuteSQL.executeMyQuery(sql,conns);
						if (sql != null && sql.length() > 0 && sql.toLowerCase().indexOf("from") != -1) {
							RowSet rs = dao.search(sql);
							if (rs.next()) {
								int dec = fielditem.getDecimalwidth();
								String valuef = rs.getString("field0") != null ? rs.getString("field0").toString()
										: "0";
								if (dec > 0 && !"0".equals(valuef)) {
									valuef = PubFunc.round(valuef, dec);
								}
								datalist.add(valuef);
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("统计求差".equals(express.substring(express.indexOf("{[") + 2, express.indexOf("(")))) {
					try {
						// List rs = ExecuteSQL.executeMyQuery(sql,conns);
						// System.out.println(sql);
						if (sql != null && sql.length() > 0 && sql.toLowerCase().indexOf("from") != -1) {
							String fieldnames = express.substring(express.indexOf("(") + 1, express.indexOf(")"))
									.trim();
							String[] fields = fieldnames.split(",");
							FieldItem fielditem0 = DataDictionary.getFieldItem(fields[0]);
							FieldItem fielditem1 = DataDictionary.getFieldItem(fields[1]);
							RowSet rs = dao.search(sql);
							float leaveLen = 0;
							if ("q15".equalsIgnoreCase(tablename) || "q13".equalsIgnoreCase(tablename)
									|| "q11".equalsIgnoreCase(tablename)) {
								String kq_item = "";
								AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
								while (rs.next()) {
									kq_item = rs.getString(tablename + "03");
									HashMap kqItem_hash = annualApply.count_Leave(kq_item);
									Date kq_start = rs.getTimestamp("field0");
									Date kq_end = rs.getTimestamp("field1");
									String q1501 = rs.getString(tablename + "01");
									String b0110 = rs.getString("b0110");
									float leave_tiem = annualApply.getLeaveTime(kq_start, kq_end, this.src_a0100,
											this.src_per, b0110, kqItem_hash);
									leaveLen = leave_tiem + leaveLen;
									if ("q15".equalsIgnoreCase(tablename)) {
										StringBuffer buf = new StringBuffer();
										buf.append("select q1501,q15z1,q15z3,b0110 from q15 where ");
										buf.append(
												" q1517='1' and q1519='" + q1501 + "' and q15z0='01' and q15z5='03'");
										// System.out.println(buf.toString());
										RowSet xjrs = dao.search(buf.toString());
										float xjtime = 0;
										while (xjrs.next()) {
											kq_start = xjrs.getTimestamp("q15z1");
											kq_end = xjrs.getTimestamp("q15z3");
											leave_tiem = annualApply.getLeaveTime(kq_start, kq_end, this.src_a0100,
													this.src_per, b0110, kqItem_hash);
											xjtime = xjtime + leave_tiem;
										}
										leaveLen = leaveLen - xjtime;
									}
								}
								if (leaveLen > 0) {
									String valuef = PubFunc.round(leaveLen + "", 2);
									datalist.add(valuef);
								}
							} else {

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("求差".equals(express.substring(express.indexOf("{[") + 2, express.indexOf("("))))// 求差
		{
			if ("{[".equals(expresspre)) {
				String sql = analyseExpress(express, tablename, "");
				if (sql != null && sql.length() > 0 && sql.toLowerCase().indexOf("from") != -1) {
					try {
						String lv = "";
						String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
						if (expresslast != null && expresslast.trim().length() >= 1) {
                            lv = expresslast.substring(0, 1);
                        }
						String fieldnames = express.substring(express.indexOf("(") + 1, express.indexOf(")")).trim();
						String[] fields = fieldnames.split(",");
						FieldItem fielditem0 = DataDictionary.getFieldItem(fields[0]);
						FieldItem fielditem1 = DataDictionary.getFieldItem(fields[1]);
						RowSet rs = dao.search(sql);
						if (rs.next()) {
							if (lv != null && "d".equals(lv)) {
								java.util.Date date0 = rs.getTimestamp("field0");
								java.util.Date date1 = rs.getTimestamp("field1");
								KqUtilsClass utils = new KqUtilsClass();
								float lg = utils.getPartMinute(date0, date1);
								if (lg > 0) {
									if ("q15".equalsIgnoreCase(tablename) || "q13".equalsIgnoreCase(tablename)) {
										float timeLen = utils.getAppDateTimeLen(this.conn, date0, date1);
										AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
										String kq_item = rs.getString(tablename + "03");
										HashMap kqItem_hash = annualApply.count_Leave(kq_item);
										timeLen = annualApply.getLeaveTime(date0, date1, this.src_a0100, this.src_per,
												this.userView.getUserOrgId(), kqItem_hash);
										if (timeLen == 0) {
                                            timeLen = lg / 60 / 24;
                                        }
										String valuef = PubFunc.round(timeLen + "", 2);
										datalist.add(valuef);
									} else {
										float ff = lg / 60 / 24;
										String valuef = PubFunc.round(ff + "", 2);
										datalist.add(valuef);
									}
								} else {
                                    datalist.add("");
                                }

							} else if (lv != null && "h".equals(lv)) {
								java.util.Date date0 = rs.getTimestamp("field0");
								java.util.Date date1 = rs.getTimestamp("field1");
								KqUtilsClass utils = new KqUtilsClass();
								float lg = utils.getPartMinute(date0, date1);
								if ("q15".equalsIgnoreCase(tablename)) {
									float timeLen = utils.getAppDateTimeLen(this.conn, date0, date1);
									AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
									String kq_item = rs.getString(tablename + "03");
									HashMap kqItem_hash = annualApply.count_Leave(kq_item);
									timeLen = annualApply.getLeaveTimeHours(date0, date1, this.src_a0100, this.src_per,
											this.userView.getUserOrgId(), kqItem_hash);

									if (timeLen == 0) {
                                        timeLen = lg / 60;
                                    }
									String valuef = PubFunc.round(timeLen + "", 2);
									datalist.add(valuef);
								} else {
									float ff = lg / 60;
									String valuef = PubFunc.round(ff + "", 2);
									datalist.add(valuef);
								}

							} else {
								int dec0 = fielditem0.getDecimalwidth();
								int dec1 = fielditem1.getDecimalwidth();
								if (dec0 > 0 || dec1 > 0) {
									float f0 = rs.getFloat("field0");
									float f1 = rs.getFloat("field1");
									float ff = f1 - f0;
									if (lv != null && lv.length() > 0) {
										String valuef = PubFunc.round(ff + "", Integer.parseInt(lv));
										datalist.add(valuef);
									} else {
										datalist.add(ff + "");
									}
								} else {
									int i0 = rs.getInt("field0");
									int i1 = rs.getInt("field1");
									int ii = i1 - i0;
									datalist.add(ii + "");
								}

							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} else {

			if ("{[".equals(expresspre)) {
				// for example{[A0101]...
				fieldname = express.substring(express.indexOf("(") + 1, express.indexOf(")")).trim(); // A0101
			}
			/**
			 * gaohy 新增情况 求列表中指标为子集
			 **/
			if (fieldname.indexOf("current") != -1) {// 如果有current则截取指标
				fieldname = fieldname.substring(fieldname.indexOf(":") + 1);
			}
			if(fieldname!=null){
				FieldItem fielditem = DataDictionary.getFieldItem(fieldname.substring(0, 5));
				String fieldxml = "";
				Boolean subFlag = false;
				if (fieldname.indexOf("t_") != -1) {// 如果为子集
					subFlag = true;
					String fieldvalue[] = fieldname.split(":");
					fieldxml = fieldvalue[0];// 获得子集内容xml
					fieldname = fieldvalue[1];// 获得指标
					if(fieldname!=null){
						fielditem = DataDictionary.getFieldItem(fieldname);
					}
				}
				Boolean isPhoto = false;
				if ("photo".equalsIgnoreCase(fieldname)) {
					isPhoto = true;
					fielditem = new FieldItem();
				}
				if (fielditem != null) {
					String fieldsetid = fielditem.getFieldsetid();
					String sql = "";
					if (!fieldsetid.equalsIgnoreCase(tablename) && fieldsetid.indexOf("A") == 0
							&& tablename.indexOf("templet") == -1) {
						String o_where = "";
						if (this.src_a0100 != null && this.src_a0100.length() > 0) {
							o_where = " a0100='" + this.src_a0100 + "'";
							if (!"A01".equals(fieldsetid)) {
								o_where = o_where + " and i9999=(select max(i9999) from " + this.src_per + fieldsetid
										+ " where a0100='" + this.src_a0100 + "')";
							}
						}
						sql = analyseExpress(express, this.src_per + fieldsetid, o_where);
					} else {
						sql = analyseExpress(express, tablename, "");
					}
					// System.out.println(sql);
					String expressvalue = express.substring(2, express.indexOf("(")); // 表达式的函数说明
					try {
						// List rs = ExecuteSQL.executeMyQuery(sql,conns);
						if (sql != null && sql.length() > 0 && sql.toLowerCase().indexOf("from") != -1) {
							RowSet rs = null;
							// Statement stmt = null;//判断字段是否存在
							try {
								StringBuffer strsql = new StringBuffer();
								strsql.append(sql);
	
								ContentDAO dao = new ContentDAO(conn);
								dao.search(strsql.toString());
							} catch (Exception ex) {
	
								try {
	
									return new ArrayList();
								} catch (Exception exx) {
									return new ArrayList();
								}
	
							}
	
							rs = dao.search(sql);
	
							/*
							 * if(!rs.isEmpty()) for(int i=0;i<rs.size();i++) {
							 * LazyDynaBean rec=(LazyDynaBean)rs.get(i);
							 * datalist.add(rec.get(fieldname.toLowerCase())!=null?
							 * changeCodeIdValue(fieldname.toLowerCase(),rec.get(
							 * fieldname.toLowerCase()).toString()):""); }
							 */
							while (rs.next()) {
								/**
								 * gaohy 新增情况 求列表中指标为子集
								 **/
								if (subFlag) {// 为子集时
	
									String xml = rs.getString(fieldxml);// 得到xml
									datalist = getDataList(xml, express, fieldname, expressvalue, fielditem);// 解析xml,返回结果
								} else if (isPhoto) {
									String filename = ServletUtilities.createOleFile("photo", "ext", rs);
									if (filename != null && filename.trim().length() > 0){
										String photoPath=System.getProperty("java.io.tmpdir").replace("/", File.separator).replace("\\", File.separator);
										if(!photoPath.endsWith(File.separator)){
											photoPath=photoPath+File.separator;
										}
										filename = photoPath + filename;
										System.out.println("filename:"+filename);
									}
									datalist.add(filename);
									
								} else {// 指标
									if ("求平均".equals(expressvalue)) {
										String valuef = rs.getString(fieldname.toLowerCase());
										if (valuef == null || valuef.length() <= 0) {
											datalist.add(rs.getString(fieldname.toLowerCase()) != null
													? changeCodeIdValue(fieldname.toLowerCase(),
															rs.getString(fieldname.toLowerCase()).toString())
													: "");
										} else {
											if (fieldname.indexOf("_1") != -1 || fieldname.indexOf("_2") != -1) {
                                                fieldname = fieldname.substring(0, fieldname.length() - 2);
                                            }
											int dec = fielditem.getDecimalwidth();
											if (dec > 0) {
												valuef = PubFunc.round(valuef, dec);
											}
											datalist.add(valuef);
										}
									} else if ("求列表".equals(expressvalue)) {
	
										if ("D".equals(fielditem.getItemtype())) {
											java.util.Date dd = rs.getTimestamp(fieldname.toLowerCase());
											if (dd != null) {
												// xyy20141127
												String tempValue = DateUtils.format(dd, "yyyy.MM.dd HH:mm:ss");
												int i = fielditem.getItemlength();
												if (fielditem.getItemlength() <= 10) {
													datalist.add(tempValue.substring(0, fielditem.getItemlength()));
	
												} else if (fielditem.getItemlength() > 19) {
													datalist.add(tempValue.substring(0, 19));
	
												} else {
													datalist.add(tempValue.substring(0, fielditem.getItemlength() + 1));
	
												}
	
											} else {
												datalist.add("");
											}
										} else if ("N".equals(fielditem.getItemtype()) && fielditem.getDecimalwidth() > 0) // xyy20141127取小数的
										{
											String temp = rs.getString(fieldname.toLowerCase()) != null
													? rs.getString(fieldname.toLowerCase()) : "0";
											int flotNum = fielditem.getDecimalwidth();
											if (temp.lastIndexOf(".") < 0) {
												for (int i = 0; i < flotNum; i++) {
													if (i == 0) {
														temp += ".0";
													} else {
														temp += "0";
													}
												}
											} else {
												int index = temp.lastIndexOf(".");
												if (temp.substring(index + 1).length() < flotNum) {
													for (int i = 0; i < flotNum - temp.substring(index + 1).length(); i++) {
														temp += "0";
													}
												}
											}
											datalist.add(temp);
										} else if ("q1104".equalsIgnoreCase(fieldname)
												|| "q1304".equalsIgnoreCase(fieldname)
												|| "q1504".equalsIgnoreCase(fieldname)) {
											/** 考勤班次 **/
											String valuef = rs.getString(fieldname.toLowerCase());
											datalist.add(getClassName(valuef));
										} else {
                                            datalist.add(rs.getString(fieldname.toLowerCase()) != null
                                                    ? changeCodeIdValue(fieldname.toLowerCase(),
                                                            rs.getString(fieldname.toLowerCase()).toString())
                                                    : "");
                                        }
	
									} else {
										datalist.add(rs.getString(fieldname.toLowerCase()) != null
												? changeCodeIdValue(fieldname.toLowerCase(),
														rs.getString(fieldname.toLowerCase()).toString())
												: "");
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return datalist;
	}

	/**
	 * 分析表达式
	 * 
	 * @param express
	 *            例如{[指标名]求列表[n]} {[指标名]求和} {[指标名]求最大} {[指标名]求最小} {[指标名]求平均}
	 *            {[指标名]求个数} {[指标名]年} {[指标名]月} {[指标名]日} [指标名] {[date]} {[year]}
	 *            {[month]} {[day]}
	 */
	public String analyseExpress(String express, String tablename, String selfwhere) {
		StringBuffer sql = new StringBuffer();
		String expressvalue = express.substring(2, express.indexOf("(")); // 表达式的函数说明
		// for example express={[A0101`求列表`n]}、express={[A0101`求和]}
		// for example{[A0101]...
		String fieldname = express.substring(express.indexOf("(") + 1, express.indexOf(")")); // 需要求的指标
		/**
		 * gaohy 新增情况 求列表{子集} 查出子集xml
		 **/
		Boolean subFlag = false;// 是否为子集
		if (fieldname.indexOf("current") != -1) {// 如果有current
			fieldname = fieldname.substring(fieldname.indexOf(":") + 1);
			if (fieldname.indexOf("t_") != -1) {// 为子集
				subFlag = true;
				String fieldvalue[] = fieldname.split(":");
				fieldname = fieldvalue[0];// 获得子集字段
			}
		}
		String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
		if (subFlag) {
			expresslast = "";
		}
		if ("photo".equalsIgnoreCase(fieldname)) {
			fieldname = " photo,ext ";
			expresslast = "";
		}
		if (expresslast.indexOf("(") != -1 && expresslast.indexOf(")") != -1) {
			// for example express={[求列表(A0101)(12)]}
			String expresslastvalue = expresslast.substring(expresslast.indexOf("(") + 1, expresslast.indexOf(")"));
			if ("求列表".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");
					// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(fieldname);
					sql.append(" from (select * from ");
					sql.append(tablename);
					// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					sql.append(" ) where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求年份".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求月份".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				sql.append("");
				// sql.append() //求前n个记录值
			} else if ("求天".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求日期".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求日".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql(selfwhere, tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
			}
		} else {
			if ("求列表".equals(expressvalue)) {
				sql.append("select ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
				// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
				if (dataBo != null) {
					if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
							&& (dataBo.getParamBo().getOperationType() == 8
									|| dataBo.getParamBo().getOperationType() == 9)) {
						String key = "b0110";
						if (dataBo.getParamBo().getInfor_type() == 3) {
                            key = "e01a1";
                        }
						sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
								+ "=to_id then 100000000 else a0000 end asc ");
					} else {
                        sql.append(" order by a0000");
                    }
				}
			} else if ("求最大".equals(expressvalue)) {
				sql.append("select ");
				sql.append("max(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求最小".equals(expressvalue)) {
				sql.append("select ");
				sql.append("min(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求平均".equals(expressvalue)) {
				sql.append("select ");
				sql.append("avg(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求个数".equals(expressvalue)) {
				sql.append("select ");
				sql.append("count(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求和".equals(expressvalue)) {
				sql.append("select ");
				sql.append("sum(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求年份".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.year(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求月份".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.month(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求天".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.day(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求日期".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.day(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求日".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.day(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			} else if ("求差".equals(expressvalue)) {
				String fieldnames = express.substring(express.indexOf("(") + 1, express.indexOf(")")).trim();
				String[] fields = fieldnames.split(",");
				if (fields.length == 2) {
					if ("q15".equalsIgnoreCase(tablename) || "q13".equalsIgnoreCase(tablename)
							|| "q11".equalsIgnoreCase(tablename)) {
                        sql.append("select " + tablename + "01,b0110," + tablename + "03," + fields[0] + " field1,"
                                + fields[1] + " field0");
                    } else {
                        sql.append("select " + fields[0] + " field1," + fields[1] + " field0");
                    }
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql(selfwhere, tablename));

				}
			} else {
				// for example {[A0101]}
				fieldname = express.substring(2, express.indexOf("]}"));
				sql.append("select ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql(selfwhere, tablename));
			}
		}
		return sql.toString();
	}

	public String analyseOtherExpress(String express, String tablename) {
		StringBuffer sql = new StringBuffer();
		String expressvalue = express.substring(2, express.indexOf("(")); // 表达式的函数说明
		// for example express={[A0101`求列表`n]}、express={[A0101`求和]}
		// for example{[A0101]...
		String fieldname = express.substring(express.indexOf("(") + 1, express.indexOf(")")); // A0101
		String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
		if (expresslast.indexOf("(") != -1 && expresslast.indexOf(")") != -1) {
			String expresslastvalues = expresslast.substring(expresslast.indexOf("(") + 1, expresslast.indexOf(")"));
			String[] lastvalues = expresslastvalues.split(",");
			StringBuffer whereSql = new StringBuffer();
			whereSql.append(" 1=1 ");

			for (int i = 0; i < lastvalues.length; i++) {
				String expresslastvalue = lastvalues[i];
				String oper = "=";
				if (expresslastvalue.indexOf("=") != -1) {
                    oper = "=";
                } else if (expresslastvalue.indexOf("<") != -1) {
                    oper = "<";
                } else if (expresslastvalue.indexOf(">") != -1) {
                    oper = ">";
                } else if (expresslastvalue.indexOf("<=") != -1) {
                    oper = "<=";
                } else if (expresslastvalue.indexOf(">=") != -1) {
                    oper = ">=";
                } else {
                    oper = "=";
                }
				String condition[] = expresslastvalue.split(oper);
				if (condition == null || condition.length != 2) {
                    return "";
                }
				FieldItem conditionField = DataDictionary.getFieldItem(condition[0].trim());
				String conditionType = conditionField.getItemtype();

				Date date = new Date();
				String year = DateUtils.format(date, "yyyy");
				String month = DateUtils.format(date, "MM");
				String day = DateUtils.format(date, "dd");
				if ("当前年".equalsIgnoreCase(condition[1])) {
					if ("D".equals(conditionType)) {
						whereSql.append(" and " + Sql_switcher.year(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + year);
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '" + year + "%'");
					}
				} else if ("当前月".equalsIgnoreCase(condition[1])) {
					if ("D".equals(conditionType)) {
						whereSql.append(" and " + Sql_switcher.year(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + year);
						whereSql.append(" and " + Sql_switcher.month(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + month);
					} else {
						whereSql.append(" and  " + condition[0]);
						whereSql.append(" like '" + year + "." + month + "%'");
					}
				} else if ("当前天".equalsIgnoreCase(condition[1])) {
					if ("D".equals(conditionType)) {
						whereSql.append(" and " + Sql_switcher.year(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + year);
						whereSql.append(" and " + Sql_switcher.month(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + month);
						whereSql.append(" and " + Sql_switcher.day(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + day);
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '" + year + "." + month + "." + day + "%'");
					}
				} else if ("截止日期年".equalsIgnoreCase(condition[1])) {
					String appdate = ConstantParamter.getAppdate(this.userView.getUserName());
					Date appD = DateUtils.getDate(appdate, "yyyy.MM.dd");
					if ("D".equals(conditionType)) {
						if (appdate != null && appdate.length() > 0) {
                            appdate = appdate.replaceAll("-", "\\.");
                        }
						whereSql.append(" and " + Sql_switcher.year(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.getYear(appD));
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '" + DateUtils.getYear(appD) + "%'");
					}
				} else if ("截止日期月".equalsIgnoreCase(condition[1])) {
					String appdate = ConstantParamter.getAppdate(this.userView.getUserName());
					Date appD = DateUtils.getDate(appdate, "yyyy.MM.dd");
					if ("D".equals(conditionType)) {
						if (appdate != null && appdate.length() > 0) {
                            appdate = appdate.replaceAll("-", "\\.");
                        }
						whereSql.append(" and " + Sql_switcher.month(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.format(appD, "MM"));
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '%." + DateUtils.format(appD, "MM") + ".%'");
					}
				} else if ("截止日期日".equalsIgnoreCase(condition[1])) {
					String appdate = ConstantParamter.getAppdate(this.userView.getUserName());
					Date appD = DateUtils.getDate(appdate, "yyyy.MM.dd");
					if ("D".equals(conditionType)) {
						if (appdate != null && appdate.length() > 0) {
                            appdate = appdate.replaceAll("-", "\\.");
                        }
						whereSql.append(" and " + Sql_switcher.day(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.format(appD, "dd"));
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '%." + DateUtils.format(appD, "dd") + "'");
					}
				} else if ("截止日期".equalsIgnoreCase(condition[1])) {
					String appdate = ConstantParamter.getAppdate(this.userView.getUserName());
					Date appD = DateUtils.getDate(appdate, "yyyy.MM.dd");
					if ("D".equals(conditionType)) {
						if (appdate != null && appdate.length() > 0) {
                            appdate = appdate.replaceAll("-", "\\.");
                        }
						whereSql.append(" and " + Sql_switcher.year(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.getYear(appD));
						whereSql.append(" and " + Sql_switcher.month(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.format(appD, "MM"));
						whereSql.append(" and " + Sql_switcher.day(condition[0]));
						whereSql.append(oper);
						whereSql.append("" + DateUtils.format(appD, "dd"));
					} else {
						whereSql.append(" and " + condition[0]);
						whereSql.append(" like '" + appdate + "%'");
					}
				} else {
					String codesetid = conditionField.getCodesetid();
					if (codesetid != null && !"0".equals(codesetid)) {

						String codeitemid = getCodeItemid(codesetid, condition[1]);
						if (codeitemid != null && codeitemid.length() > 0) {
							whereSql.append(" and " + condition[0]);
							whereSql.append(oper);
							whereSql.append("'" + codeitemid + "'");
						} else {
							whereSql.append(" and " + condition[0]);
							whereSql.append(oper);
							whereSql.append("'" + condition[1] + "'");
						}
					} else {
						if ("N".equals(conditionType)) {
							whereSql.append(" and " + condition[0]);
							whereSql.append(oper);
							whereSql.append(condition[1]);
						} else if ("D".equals(conditionType)) {
							whereSql.append(" and " + condition[0]);
							whereSql.append(oper);
							whereSql.append(Sql_switcher.dateValue(condition[1]));
						} else {
							whereSql.append(" and " + condition[0]);
							whereSql.append(oper);
							whereSql.append("'" + condition[1] + "'");
						}

					}

				}
			}
			if ("统计求和".equals(expressvalue)) {
				FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
				String othertablename = fielditem.getFieldsetid();
				sql.append("select ");
				sql.append("SUM(" + fielditem.getItemid() + ") field0");
				sql.append(" from ");
				sql.append(othertablename);
				sql.append(" where ");
				sql.append(whereSql.toString());
				sql.append(" and nbase='" + this.src_per + "' and a0100='" + this.src_a0100 + "'");
			} else if (expressvalue != null && "统计求差".equalsIgnoreCase(expressvalue)) {
				String[] fields = fieldname.split(",");
				if (fields.length == 2) {
					if ("q15".equalsIgnoreCase(tablename) || "q13".equalsIgnoreCase(tablename)
							|| "q11".equalsIgnoreCase(tablename)) {
						sql.append("select " + tablename + "01,b0110," + tablename + "03," + fields[0] + " field1,"
								+ fields[1] + " field0");
						sql.append(" from ");
						sql.append(tablename);
						sql.append(" where ");
						sql.append(whereSql.toString());
						sql.append(" and nbase='" + this.src_per + "' and a0100='" + this.src_a0100 + "'");
						sql.append(" and " + tablename + "z0='01' and " + tablename + "z5='03'");
						if ("q15".equalsIgnoreCase(tablename)) {
                            sql.append(" and " + Sql_switcher.isnull("q1517", "0") + "=0");
                        }
					} else {
						sql.append("select " + fields[0] + " field1," + fields[1] + " field0");
						sql.append(" from ");
						sql.append(tablename);
						sql.append(" where ");
						sql.append(whereSql.toString());
						sql.append(" and nbase='" + this.src_per + "' and a0100='" + this.src_a0100 + "'");
					}

				}
			}
		}
		return sql.toString();
	}

	/**
	 * 输出模版字符串数据
	 * 
	 * @param doc
	 * @param outpath
	 */
	public String outTemplateDataDocument(Document doc) {
		try {
			XMLSerializer serializer = new XMLSerializer();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			serializer.setOutputByteStream(output);
			// Insert your PipedOutputStream here instead of System.out!
			OutputFormat out = new OutputFormat();
			out.setEncoding("UTF-8");// liuyz 原为GB2312 由于此编码表中不是别玥字，导致导出乱码
			serializer.setOutputFormat(out);
			serializer.serialize(doc);
			String outputstr = new String(output.toByteArray(), "GBK");
			return outputstr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 输出审批魔板数据 的条件语句
	 */
	private String getConditionSql(String selfwhere, String tablename) {
		StringBuffer wheresql = new StringBuffer();

		if (this.sp_batch != null && !"0".equalsIgnoreCase(this.sp_batch)) {
			wheresql.append("( 1=2 ");
			for (int i = 0; i < this.inslist.size(); i++) {
				wheresql.append(" OR ins_id=");
				wheresql.append(this.inslist.get(i));
			}
			wheresql.append(")");
		} else {
			if (this.inslist != null && this.inslist.size() > 0) {
				if (this.inslist != null && "0".equals(this.inslist.get(0))) {
                    wheresql.append("( 1=1 )");
                } else {
					wheresql.append("( 1=2 ");
					for (int i = 0; i < this.inslist.size(); i++) {
						wheresql.append(" OR ins_id=");
						wheresql.append(this.inslist.get(i));
					}
					wheresql.append(")");
				}
			} else {
				wheresql.append("( 1=1 )");
			}

		}
		if (selfwhere == null || selfwhere.length() <= 0) {
			if (this.src_where != null && this.src_where.length() > 0) {
                wheresql.append(" and " + this.src_where);
            }
		} else {
			wheresql.append(" and " + selfwhere);
		}
		if (this.issubmitflag) {
			if (this.inslist != null && !"0".equals(this.inslist.get(0))) {

				wheresql.append(" and exists (select null from t_wf_task_objlink where " + tablename
						+ ".seqnum=t_wf_task_objlink.seqnum and " + tablename
						+ ".ins_id=t_wf_task_objlink.ins_id and t_wf_task_objlink.task_id in(" + taskid
						+ ") and t_wf_task_objlink.submitflag='1'   and (" + Sql_switcher.isnull("special_node", "0")
						+ "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"
						+ this.userView.getUserName().toLowerCase() + "' or lower(username)='"
						+ this.userView.getDbname().toLowerCase() + this.userView.getA0100() + "' ) ) ))");

			} else {
				wheresql.append(" and submitflag='1'");
			}
		}
		return wheresql.toString();
	}

	/**
	 * 获得年龄
	 * 
	 * @param int
	 *            ncMonth,ncDay,nYear,nMonth,nDay
	 * @param outage
	 */
	public String GetHisAge(String birthdaystr) {
		/*
		 * 根据日期获得年龄的运算
		 */
		int nYear = 0;
		int nMonth = 0;
		int nDay = 0;
		if (birthdaystr.length() >= 10) {
			birthdaystr = birthdaystr.substring(0, 10);
			String[] strAry = birthdaystr.split("-");
			nYear = Integer.parseInt(strAry[0]);
			nMonth = Integer.parseInt(strAry[1]);
			nDay = Integer.parseInt(strAry[2]);
		}
		int ncYear = Calendar.getInstance().get(Calendar.YEAR); // 获得当前年
		int ncMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // 获得当前月
		int ncDay = Calendar.getInstance().get(Calendar.DATE);
		int nAage, nMM, nDD, Result;
		nAage = ncYear - nYear;
		nMM = ncMonth - nMonth;
		nDD = ncDay - nDay;
		if (nMM > 0) {
			Result = nAage;
		} else if (nMM < 0) {
			Result = nAage - 1;
			if (Result < 0) {
				Result = 0;
			}
		} else {
			if (nDD >= 0) {
				Result = nAage;
			} else {
				Result = nAage - 1;
				if (Result < 0) {
                    Result = 0;
                }
			}
		}
		return String.valueOf(Result);
	}

	/**
	 * {@年|月|日(日期指标)@}”
	 * 
	 * @param value
	 * @param tablename
	 * @return
	 */
	// liuyz 导出单人和多人模版优化
	private String getFunIndexValue(String value, String tablename, String nid) {
		String express = "";
		StringBuffer valuebuffer = new StringBuffer();
		boolean ishas = true;
		if (value != null && value.indexOf("{@") != -1 && value.indexOf("@}") != -1) {
			express = value.substring(value.indexOf("{@"), value.indexOf("@}") + 2);
			ishas = true;
		} else {
			ishas = false;
			return value;
		}

		while (ishas) {
			// liuyz 导出单人和多人模版优化
			ArrayList valuelist = getExpressIndexValue(express, tablename, nid);
			String valuestr = "";
			String expresspre = express.substring(0, express.indexOf("(")); // expresspre={[
																			// 或者
			String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
			String lv = null;
			if (expresslast != null && expresslast.trim().length() >= 1) {
                lv = expresslast.substring(0, 1);
            }
			// liuyz 导出单人和多人模版优化
			if (expresspre.startsWith("{@")) {
				for (int i = 0; i < valuelist.size(); i++) {
					if (valuelist.get(i) == null || valuelist.get(i).toString().length() <= 0) {
                        continue;
                    }
					if ("求年龄".equals(express.substring(value.indexOf("{@") + 2, value.indexOf("(")))) {
						if (i > 0) {
                            if (lv != null && "v".equalsIgnoreCase(lv)) {
                                valuestr += "xrywlh888" + GetHisAge(valuelist.get(i).toString());
                            } else if (valuestr.length() > 0) {
                                valuestr += "、" + GetHisAge(valuelist.get(i).toString());
                            } else {
                                valuestr += GetHisAge(valuelist.get(i).toString());
                            }
                        } else {
                            valuestr += GetHisAge(valuelist.get(i).toString());
                        }
					} else {
						if (i > 0) {
                            if (lv != null && "v".equalsIgnoreCase(lv)) {
                                valuestr += "xrywlh888" + valuelist.get(i).toString();
                            } else if (valuestr.length() > 0) {
                                valuestr += "、" + valuelist.get(i).toString();
                            } else {
                                valuestr += valuelist.get(i).toString();
                            }
                        } else {
                            valuestr += valuelist.get(i).toString();
                        }
					}
				}
			}
			valuebuffer.append(value.substring(0, value.indexOf(express)));
			valuebuffer.append(valuestr);
			value = value.substring(value.indexOf(express) + express.length());
			if (value.indexOf("{@") != -1) {
				valuebuffer.append(value.substring(0, value.indexOf("{@")));
				value = value.substring(value.indexOf("{@"));
			} else {
				valuebuffer.append(value);
			}
			if (value != null && value.indexOf("{@") != -1 && value.indexOf("@}") != -1) {
				express = value.substring(value.indexOf("{@"), value.indexOf("@}") + 2);
				ishas = true;
			} else {
				ishas = false;
				if (value.indexOf("{@") != -1) {
					valuebuffer.append(value.substring(0, value.indexOf("{@")));
					this.fieldvalue = value.substring(0, value.indexOf("{@"));
					this.isparagrphvalue = true;
				} else {
					this.fieldvalue = "";
					this.isparagrphvalue = false;
				}
			}
		}
		return valuebuffer.toString();
	}

	/**
	 * 获得表达式的sql
	 * 
	 * @param express
	 * @param tablename
	 */
	// liuyz 导出单人和多人模版优化
	private ArrayList getExpressIndexValue(String express, String tablename, String nid) {
		// Connection conn=null;
		String fieldname = "";
		express = express.substring(express.indexOf("{@") + 2, express.indexOf("@}"));
		String expresspre = express.substring(0, express.indexOf("(")); // expresspre={[
																		// 或者 [指
																		// 前缀
		// for example{[A0101]...
		String fielddesc = express.substring(express.indexOf("(") + 1, express.indexOf(")")); // A0101
		if (fielddesc.indexOf("[") != -1 && fielddesc.indexOf("]") != -1) {
			// {*现|拟[列标题]*}
			fieldname = fielddesc.substring(fielddesc.indexOf("[") + 1, fielddesc.indexOf("]"));
			if (fielddesc.indexOf("现") != -1) {
				HashMap map = getFielditemid(fieldname);
				fieldname =(String) map.get("itemid");
				fieldname = fieldname + "_1";
			} else if (fielddesc.indexOf("拟") != -1) {
				HashMap map = getFielditemid(fieldname);
				fieldname =(String) map.get("itemid");
				fieldname = fieldname + "_2";
			}
		} else {
			HashMap map = getFielditemid(fielddesc);
			fieldname =(String) map.get("itemid");
			fieldname = fieldname + "_1";
		}
		ArrayList datalist = new ArrayList();
		String sql = analyseExpress(expresspre, fieldname, "", tablename, nid);// liuyz
																				// 导出单人和多人模版优化
		try {
			// List rs = ExecuteSQL.executeMyQuery(sql,conns);
			if (sql != null && sql.length() > 0 && sql.toLowerCase().indexOf("from") != -1) {
				RowSet rs = dao.search(sql);
				while (rs.next()) {
					datalist.add(rs.getString(fieldname.toLowerCase()) != null ? changeCodeIdValue(
							fieldname.toLowerCase(), rs.getString(fieldname.toLowerCase()).toString()) : "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datalist;
	}

	/**
	 * 输出模版文件数据
	 * 
	 * @param doc
	 * @param outpath
	 */
	public void outTemplateDataDocumentFile(Document doc, String outpath) {
		FileOutputStream fopStream = null;
		try {
			fopStream = new FileOutputStream("d:\\templatedata.doc");
			XMLSerializer serializer = new XMLSerializer();
			serializer.setOutputByteStream(fopStream);
			OutputFormat out = new OutputFormat();
			out.setEncoding("UTF-8");
			serializer.setOutputFormat(out);
			serializer.serialize(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(fopStream);
		}
	}

	/**
	 * 分析表达式 例如{[指标名]求列表[n]} {[指标名]求和} {[指标名]求最大} {[指标名]求最小} {[指标名]求平均}
	 * {[指标名]求个数} {[指标名]年} {[指标名]月} {[指标名]日} [指标名] {[date]} {[year]} {[month]}
	 * {[day]} {@年|月|日(日期指标)@}
	 * 
	 * @param expressvalue
	 *            函数---求列表
	 * @param fieldname
	 *            指标名称
	 * @param expresslast
	 *            函数后缀{[求列表(A0101)(n)]} de n
	 * @param tablename
	 * @return
	 */
	// liuyz 导出单人和多人模版优化
	public String analyseExpress(String expressvalue, String fieldname, String expresslast, String tablename,
			String nid) {
		StringBuffer sql = new StringBuffer();
		/*
		 * String expressvalue=express.substring(2,express.indexOf("("));
		 * //表达式的函数说明 String
		 * fieldname=express.substring(express.indexOf("(")+1,express.indexOf(
		 * ")")); //A0101 String
		 * expresslast=express.substring(express.indexOf(")")+1);
		 */ // 后表达式
		if (expresslast.indexOf("(") != -1 && expresslast.indexOf(")") != -1) {
			// for example express={[求列表(A0101)(12)]}
			String expresslastvalue = expresslast.substring(expresslast.indexOf("(") + 1, expresslast.indexOf(")"));
			if ("求列表".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");
					// liuyz 解决导出顺序与网页顺序不同的问题 bug27822
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					break;
				}
				case Constant.ORACEL: {
					sql.append("select ").append(fieldname).append(" from (");
					sql.append("select * ");
					sql.append(" from ");
					sql.append(tablename);
					if (dataBo != null) {
						if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
								&& (dataBo.getParamBo().getOperationType() == 8
										|| dataBo.getParamBo().getOperationType() == 9)) {
							String key = "b0110";
							if (dataBo.getParamBo().getInfor_type() == 3) {
                                key = "e01a1";
                            }
							sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key
									+ "=to_id then 100000000 else a0000 end asc ");
						} else {
                            sql.append(" order by a0000");
                        }
					}
					sql.append(" ) where  ");
					sql.append(getConditionSql("", tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求年份".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.year(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql("", tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求月份".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.month(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql("", tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				sql.append("");
				// sql.append() //求前n个记录值
			} else if ("求天".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.day(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql("", tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			} else if ("求日期".equals(expressvalue)) {
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL: {
					sql.append("select top ");
					sql.append(expresslastvalue);
					sql.append(" ");
					sql.append(Sql_switcher.dateValue(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					break;
				}
				case Constant.DB2: {
					sql.append("select ");
					sql.append(Sql_switcher.dateValue(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
					sql.append(" Fetch First ");
					sql.append(expresslastvalue);
					sql.append(" Rows Only");

					break;
				}
				case Constant.ORACEL: {
					sql.append("select ");
					sql.append(Sql_switcher.dateValue(fieldname));
					sql.append(" as ");
					sql.append(fieldname);
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where  ");
					sql.append(getConditionSql("", tablename));
					sql.append(" and RowNum <=");
					sql.append(expresslastvalue);
					break;
				}
				}
				// sql.append() //求前n个记录值
			}
		} else {
			if ("求列表".equals(expressvalue)) {
				sql.append("select ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求最大".equals(expressvalue)) {
				sql.append("select ");
				sql.append("max(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求最小".equals(expressvalue)) {
				sql.append("select ");
				sql.append("min(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求平均".equals(expressvalue)) {
				sql.append("select ");
				sql.append("avg(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求个数".equals(expressvalue)) {
				sql.append("select ");
				sql.append("count(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求和".equals(expressvalue)) {
				sql.append("select ");
				sql.append("sum(");
				sql.append(fieldname);
				sql.append(") as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求年份".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.year(dateValue(fieldname)));//bug 35283、bug 35284  sql取第几条字段是text类型无法直接用year取值。
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求月份".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.month(dateValue(fieldname)));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("求天".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.day(dateValue(fieldname)));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else if ("年".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.year(dateValue(fieldname)));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
				if (nid != null && nid.length() > 3) {//bug 35258 年，月，日导出的是左右人的不是当前人的
					sql.append(" and lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
				}
			} else if ("月".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.month(dateValue(fieldname)));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
				if (nid != null && nid.length() > 3) {
					sql.append(" and lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
				}
			} else if ("日".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.day(dateValue(fieldname)));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
				if (nid != null && nid.length() > 3) {
					sql.append(" and lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
				}
				// liuyz 导出单人和多人模版优化
			} else if ("年|月|日".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.sqlToChar(Sql_switcher.year(dateValue(fieldname))));
				sql.append(Sql_switcher.concat());
				sql.append("'年'");
				sql.append(Sql_switcher.concat());
				sql.append(Sql_switcher.sqlToChar(Sql_switcher.month(dateValue(fieldname))));
				sql.append(Sql_switcher.concat());
				sql.append("'月'");
				sql.append(Sql_switcher.concat());
				sql.append(Sql_switcher.sqlToChar(Sql_switcher.day(dateValue(fieldname))));
				sql.append(Sql_switcher.concat());
				sql.append("'日'");
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
				if (nid != null && nid.length() > 3) {
					sql.append(" and lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
				}
			} else if ("求日期".equals(expressvalue)) {
				sql.append("select ");
				sql.append(Sql_switcher.dateValue(fieldname));
				sql.append(" as ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			} else {
				// for example {[A0101]}
				sql.append("select ");
				sql.append(fieldname);
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			}
		}
		return sql.toString();
	}

	/**
	 * 设置table的值
	 * 
	 * @param node
	 * @param valuelist值
	 */
	private void setTableElementData(Node node, ArrayList valuelist) {
		NodeList nodelist = node.getChildNodes();
		int sum = 0;
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) != null && "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) {
                sum++;
            }
		}
		try {

			if (!valuelist.isEmpty()) {
				if (sum - 1 > valuelist.size()) {
					int n = nodelist.getLength();
					for (int i = 0; i < valuelist.size(); n--) {
						if (nodelist.item(n) != null && "tr".equalsIgnoreCase(nodelist.item(n).getNodeName())) {
							node.removeChild(nodelist.item(n));
							i++;
						} else if (nodelist.item(n) != null) {
                            node.removeChild(nodelist.item(n));
                        }

					}
				} else {
					boolean first = true;
					for (int i = 0; i < nodelist.getLength();) {
						if (nodelist.item(i) != null && "tr".equalsIgnoreCase(nodelist.item(i).getNodeName())) {
							if (first) {
								first = false;
								i++;
							} else {
								node.removeChild(nodelist.item(i));
							}

						} else {
							if (nodelist.item(i) != null && first == false) {
                                node.removeChild(nodelist.item(i));
                            } else {
                                i++;
                            }
						}
					}
				}
				for (int i = 0; i < sum - 1 && i < valuelist.size(); i++) {
					Node trchild = nodelist.item(1).cloneNode(true);

					ArrayList values = new ArrayList();
					for (int j = 0; j < valuelist.size(); j++) {
						values.add(valuelist.get(j).toString().toLowerCase());
					}
					// System.out.println(values);
					// setTableElementValue(trchild,values);

					// datalist.add(rec.get(fieldname.toLowerCase())!=null?rec.get(fieldname.toLowerCase()).toString():"");
					if (sum - 1 > valuelist.size()) {
						node.insertBefore(trchild, nodelist.item(3));
					} else {
                        node.appendChild(trchild);
                    }
				}
			}
			setTableTitle(nodelist.item(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 */
	private ArrayList getFieldNamesFieldDuan(String express, String tablename) {
		String fieldress = "";
		ArrayList datalist = new ArrayList();
		String expresspre = express.substring(0, 2); // {[段(求列表([B0110_1,A0101,C,…],[B0110_1]))v|h(*|n)]}
		String expresstop = express.substring(express.indexOf(expresspre) + 2, express.indexOf("("));
		/*
		 * if(express.indexOf("A0435_2")!=-1) System.out.println("1");
		 */
		if (expresstop == null || !"段".equals(expresstop)) {
			return datalist;
		}
		String expressvalue = express.substring(express.indexOf("(") + 1, express.indexOf("(["));
		int start_a = 0;
		if ("{[".equals(expresspre)) {
			boolean isCorrect = false;
			while (!isCorrect) {
				fieldress = express.substring(express.indexOf("(") + 1 + start_a, express.indexOf(")"));
				start_a = express.indexOf("(") + start_a + 1;
				if (fieldress.indexOf("(") == -1) {
                    isCorrect = true;
                }
			}
		}
		String fieldname = fieldress.substring(fieldress.indexOf("[") + 1, fieldress.indexOf("]"));
		String fieldnames[] = fieldname.split(",");
		String orderexpress = express.substring(express.indexOf("]") + 1);
		String orderfieldname = "";
		if (orderexpress.indexOf("[") != -1) {
            orderfieldname = orderexpress.substring(orderexpress.indexOf("[") + 1, orderexpress.indexOf("]"));
        }
		orderfieldname = orderfieldname != null && orderfieldname.trim().length() > 0
				? orderfieldname.trim().toLowerCase() : "";
		String expresslast = express.substring(express.indexOf(")") + 1); // 后表达式
		StringBuffer sql = new StringBuffer();
		if ("求列表".equalsIgnoreCase(expressvalue)) {
			StringBuffer fields = new StringBuffer();
			for (int r = 0; r < fieldnames.length; r++) {
				boolean isCorrect = false;
				String field = fieldnames[r] != null && fieldnames[r].length() > 0 ? fieldnames[r].toLowerCase() : "";
				if (field == null || field.length() < 5) {
                    field = field + "#####";
                }
				for (int i = 0; i < this.fielditemlist.size(); i++) {
					FieldItem fielditem = (FieldItem) this.fielditemlist.get(i);
					if (field.substring(0, 5).equalsIgnoreCase(fielditem.getItemid().toLowerCase())) {
						isCorrect = true;
						break;
					}
				}
				if (isCorrect) {
					if (field.length() == 5 && !"a0100".equalsIgnoreCase(field)) {
						fields.append(field + "_1" + " as " + field + ",");
					} else {
						fields.append(field + " as " + field + ",");
					}

				} else {
					switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {
						fields.append("'" + field + "' as " + field + ",");
						break;
					}
					case Constant.DB2: {
						fields.append("'" + field + "' as '" + field + "',");
						orderfieldname = "'" + orderfieldname.trim() + "'";
						break;
					}
					case Constant.ORACEL: {
						// liuyz oracle中 'asda' as 'asda' 会报错，所以这种情况就不加单引号
						if (field.indexOf("#####") > -1) {
							fields.append("'" + field + "' as " + field + ",");
						} else {
							fields.append("'" + field + "' as '" + field + "',");
						}
						orderfieldname = orderfieldname.trim();// liuyz
																// fields中的字段不带单引号，orderfieldname原来带单引号，造成不成筛选相同orderfieldname只显示一个。
						break;
					}
					}
				}
			}
			if (fields != null && fields.length() > 0) {
                fields.setLength(fields.length() - 1);
            }
			if (expresslast.indexOf("(") != -1 && expresslast.indexOf(")]") != -1) {
				String expresslastvalue = expresslast.substring(expresslast.indexOf("(") + 1,
						expresslast.indexOf(")]"));

				if (expresslastvalue != null && !"*".equals(expresslastvalue)) {
					switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {
						sql.append("select top ");
						sql.append(expresslastvalue);
						sql.append(" ");
						sql.append(fields.toString());
						sql.append(" from ");
						sql.append(tablename);
						sql.append(" where ");
						sql.append(getConditionSql("", tablename));
						break;
					}
					case Constant.DB2: {
						sql.append("select ");
						sql.append(fields.toString());
						sql.append(" from ");
						sql.append(tablename);
						sql.append(" where ");
						sql.append(getConditionSql("", tablename));
						sql.append(" Fetch First ");
						sql.append(expresslastvalue);
						sql.append(" Rows Only");

						break;
					}
					case Constant.ORACEL: {
						sql.append("select ");
						sql.append(fields.toString());
						sql.append(" from ");
						sql.append(tablename);
						sql.append(" where  ");
						sql.append(getConditionSql("", tablename));
						sql.append(" and RowNum <=");
						sql.append(expresslastvalue);
						break;
					}
					}
				} else {
					sql.append("select ");
					sql.append(fields.toString());
					sql.append(" from ");
					sql.append(tablename);
					sql.append(" where ");
					sql.append(getConditionSql("", tablename));
				}

			} else {

				sql.append("select ");
				sql.append(fields.toString());
				sql.append(" from ");
				sql.append(tablename);
				sql.append(" where ");
				sql.append(getConditionSql("", tablename));
			}
			if (sql != null && sql.length() > 0 && sql.toString().toLowerCase().indexOf("from") != -1) {
				try {
					if (orderfieldname != null && orderfieldname.length() > 0) {
                        sql.append(" order by " + orderfieldname);
                    }
					RowSet rs = dao.search(sql.toString());
					StringBuffer values = new StringBuffer();
					String orderfiled = "";
					String noworderfiled = "";
					while (rs.next()) {
						values = new StringBuffer();
						if (orderfieldname != null && orderfieldname.length() > 0) {
							for (int i = 0; i < fieldnames.length; i++) {
								// FieldItem
								// fielditem=DataDictionary.getFieldItem(fieldnames[i].substring(0,5));
								String field = fieldnames[i] != null && fieldnames[i].length() > 0
										? fieldnames[i].toLowerCase() : "";
								if (field == null || field.length() < 5) {
                                    field = field + "#####";
                                }
								FieldItem fielditem = DataDictionary.getFieldItem(field.substring(0, 5));
								if (fielditem == null) {
									values.append(fieldnames[i]);
									continue;
								}
								if (fieldnames[i] != null
										&& fieldnames[i].trim().equalsIgnoreCase(orderfieldname.trim())) {
									noworderfiled = rs.getString(orderfieldname.toLowerCase());
									if (noworderfiled != null && !noworderfiled.equalsIgnoreCase(orderfiled)) {
										orderfiled = noworderfiled;
										if (fielditem != null && "D".equals(fielditem.getItemtype())) {
											java.util.Date dd = rs.getDate(fieldnames[i].toLowerCase());
											if (dd != null) {
												values.append(DateUtils.format(dd, "yyyy.MM.dd"));
											} else {
												values.append("");
											}

										} else {
                                            values.append(
                                                    rs.getString(fieldnames[i].toLowerCase()) != null
                                                            ? changeCodeIdValue(fieldnames[i].toLowerCase(), rs
                                                                    .getString(fieldnames[i].toLowerCase()).toString())
                                                            : "");
                                        }
									}
								} else {
									if ("D".equals(fielditem.getItemtype())) {
										java.util.Date dd = rs.getDate(fieldnames[i].toLowerCase());
										if (dd != null) {
											values.append(DateUtils.format(dd, "yyyy.MM.dd"));
										} else {
											values.append("");
										}
									} else {
                                        values.append(
                                                rs.getString(fieldnames[i].toLowerCase()) != null
                                                        ? changeCodeIdValue(fieldnames[i].toLowerCase(),
                                                                rs.getString(fieldnames[i].toLowerCase()).toString())
                                                        : "");
                                    }
								}
							}

						} else {
							for (int i = 0; i < fieldnames.length; i++) {
								// values.append(rs.getString(fieldnames[i].toLowerCase())!=null?changeCodeIdValue(fieldnames[i].toLowerCase(),rs.getString(fieldnames[i].toLowerCase()).toString()):"");
								FieldItem fielditem = DataDictionary.getFieldItem(fieldnames[i].substring(0, 5));
								if ("D".equals(fielditem.getItemtype())) {
									java.util.Date dd = rs.getDate(fieldnames[i].toLowerCase());
									if (dd != null) {
										values.append(DateUtils.format(dd, "yyyy.MM.dd"));
									} else {
										values.append("");
									}

								} else {
                                    values.append(rs.getString(fieldnames[i].toLowerCase()) != null
                                            ? changeCodeIdValue(fieldnames[i].toLowerCase(),
                                                    rs.getString(fieldnames[i].toLowerCase()).toString())
                                            : "");
                                }
							}
						}
						datalist.add(values.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println(express);
		}

		return datalist;
	}

	private String getNoteExpress(String value) {
		String express = "";
		String expressvalue = value;
		if (value != null && value.length() > 0)
		// express = value.substring(value.indexOf("{["), value.indexOf("]}") +
		// 2);*/
		{
			expressvalue = expressvalue.substring(expressvalue.indexOf("{["), expressvalue.lastIndexOf("]}"));
		}
		return express;
	}

	/**
	 * {[分段(
	 * 
	 * @param tablename
	 * @param value
	 * @param nid
	 * @return
	 * @throws Exception
	 */
	private String expPartString(String tablename, String value, String nid, ArrayList childlist) throws Exception {
		if (value != null && value.length() > 0) {
			value = value.replace("wlhxryhrp", "").replace("null", "");
			value = value.substring(value.lastIndexOf("{[分段("), value.indexOf(")]};") + 4);
			value = value.replace("\n\n", "xrywlh888");
			if (value.indexOf("{[分段(") != -1 && value.indexOf(")]};") != -1) {
				this.partfieldlist = new ArrayList();
				this.parlist = new ArrayList();
				this.parttagmap = new HashMap();
				value = value.substring(value.lastIndexOf("{[分段(") + 5, value.indexOf(")]};"));
				while (value != null && value.length() > 0) {
					value = getOnePartField(value);
					// System.out.println(value);
				}
				HashMap parMap = getOnePartValue(tablename, nid);
				String countstr = (String) parMap.get("fieldcount");
				int count = Integer.parseInt(countstr);
				StringBuffer newvalue = new StringBuffer();
				String parvalue = "";
				String tagfield = "";
				for (int i = 0; i < count; i++) {
					for (int r = 0; r < this.parlist.size(); r++) {
						parvalue = (String) this.parlist.get(r);
						ArrayList valuelist = (ArrayList) parMap.get(parvalue);
						tagfield = (String) this.parttagmap.get(parvalue);
						if (tagfield == null || tagfield.length() <= 0) {
							newvalue.append(parvalue);
							continue;
						} else {
							if (valuelist == null || valuelist.size() <= 0) {
								newvalue.append("");
								continue;
							} else {
								if (valuelist.size() <= i) {
									newvalue.append(valuelist.get(valuelist.size() - 1));
								} else {
									newvalue.append(valuelist.get(i));
								}
							}
						}

					}
					newvalue.append("xrywlh888");
					newvalue.append("xrywlh888");
				}
				return newvalue.toString();
			}

		}
		return value;
	}

	private String getOnePartField(String partstr) {
		String prefield = "";
		String tagfield = "";
		boolean isfound = false;
		if (partstr.indexOf("{*") != -1 && !isfound) {
			isfound = true;
			prefield = partstr.substring(0, partstr.indexOf("{*"));
			if (partstr.indexOf("*}") != -1) {
				tagfield = partstr.substring(partstr.indexOf("{*"), partstr.indexOf("*}") + 2);
				partstr = partstr.substring(partstr.indexOf("*}") + 2);
			}
		}
		/**** {&指标名称&}---括起来的为当前记录的指标值,只适用于个人的 ***/
		if (partstr.indexOf("{&") != -1 && !isfound) {
			isfound = true;
			if (partstr.indexOf("&}") != -1) {
				tagfield = partstr.substring(partstr.indexOf("{&"), partstr.indexOf("&}") + 2);
				partstr = partstr.substring(partstr.indexOf("&}") + 2);
				// getCurIndexValue(tablename, this.fieldvalue,nid,child);
			}
		}
		/**** {&指标名称&}---括起来的为当前记录的指标值 ***/
		/***/
		// 处理段落中的内容
		if (partstr.indexOf("{[") != -1 && !isfound) {
			isfound = true;
			prefield = partstr.substring(0, partstr.indexOf("{["));
			if (partstr.indexOf("]}") != -1) {
				tagfield = partstr.substring(partstr.indexOf("{["), partstr.indexOf("]}") + 2);
				partstr = partstr.substring(partstr.indexOf("]}") + 2);
			}
		}
		// 处理常量内容
		// .......待扩展中....
		if (partstr.indexOf("{#") != -1 && !isfound) {
			isfound = true;
			prefield = partstr.substring(0, partstr.indexOf("{#"));
			if (partstr.indexOf("#}") != -1) {
				tagfield = partstr.substring(partstr.indexOf("{#"), partstr.indexOf("#}") + 2);
				/*
				 * newpartstr.append(getConstantValue(tagfield,tablename));
				 * newpartstr.append(partstr.substring(partstr.indexOf("#}")+2))
				 * ;
				 */
				partstr = partstr.substring(partstr.indexOf("#}") + 2);
			}
		}
		/***** 指标函数分解{@年|月|日(日期指标)@} **/
		if (partstr.indexOf("{@") != -1 && !isfound) {
			isfound = true;
			prefield = partstr.substring(0, partstr.indexOf("{@"));
			if (partstr.indexOf("@}") != -1) {
				tagfield = partstr.substring(partstr.indexOf("{@"), partstr.indexOf("@}") + 2);
				partstr = partstr.substring(partstr.indexOf("@}") + 2);
				// getFunIndexValue(this.fieldvalue,tablename);
			}
		}
		if (isfound) {
			this.parlist.add(prefield);
			this.parlist.add(tagfield);
			this.partfieldlist.add(tagfield);
			this.parttagmap.put(tagfield, tagfield);
		} else {
			this.parlist.add(partstr);
			partstr = "";
		}
		return partstr;
	}

	/**
	 * 
	 * @param tablename
	 * @param nid
	 * @return
	 * @throws Exception
	 */
	private HashMap getOnePartValue(String tablename, String nid) throws Exception {

		if (this.partfieldlist == null || this.partfieldlist.size() <= 0) {
            return null;
        }
		HashMap map = new HashMap();
		String partstr = "";
		ArrayList valuelist = null;
		int count = 0;
		for (int i = 0; i < this.partfieldlist.size(); i++) {
			partstr = this.partfieldlist.get(i).toString();
			valuelist = new ArrayList();
			if (partstr.indexOf("{*") != -1 && partstr.indexOf("*}") != -1) {
				valuelist.add(getTdTitleValue(partstr));
			}

			/**** {&指标名称&}---括起来的为当前记录的指标值,只适用于个人的 ***/
			if (partstr.indexOf("{&") != -1 && partstr.indexOf("&}") != -1) {
				valuelist.add(getCurIndexValue(tablename, this.fieldvalue, nid));
			}
			/**** {&指标名称&}---括起来的为当前记录的指标值 ***/
			/***/
			// 处理段落中的内容
			if (partstr.indexOf("{[") != -1 && partstr.indexOf("]}") != -1) {
				// xrywlh888,、
				String nodevalue = getNodeValue(partstr, tablename);
				if (nodevalue != null && nodevalue.indexOf("xrywlh888") != -1) {
					String vs[] = nodevalue.split("xrywlh888");
					for (int v = 0; v < vs.length; v++) {
						valuelist.add(vs[v]);
					}
				} else if (nodevalue != null && nodevalue.indexOf("、") != -1) {
					String vs[] = nodevalue.split("、");
					for (int v = 0; v < vs.length; v++) {
						valuelist.add(vs[v]);
					}
				} else {
					valuelist.add(nodevalue);

				}

			}
			// 处理常量内容
			// .......待扩展中....
			if (partstr.indexOf("{#") != -1 && partstr.indexOf("#}") != -1) {

				valuelist.add(getConstantValue(partstr, tablename));
			}
			/***** 指标函数分解{@年|月|日(日期指标)@} **/
			if (partstr.indexOf("{@") != -1 && partstr.indexOf("@}") != -1) {
				// liuyz 导出单人和多人模版优化
				String funvalue = getFunIndexValue(partstr, tablename, nid);
				if (funvalue != null && funvalue.indexOf("xrywlh888") != -1) {
					String vs[] = funvalue.split("xrywlh888");
					for (int v = 0; v < vs.length; v++) {
						valuelist.add(vs[v]);
					}
				} else if (funvalue != null && funvalue.indexOf("、") != -1) {
					String vs[] = funvalue.split("、");
					for (int v = 0; v < vs.length; v++) {
						valuelist.add(vs[v]);
					}
				} else {
					valuelist.add(funvalue);

				}
			}
			if (valuelist.size() > count) {
                count = valuelist.size();
            }
			map.put(partstr, valuelist);
		}
		map.put("fieldcount", count + "");
		return map;
	}

	/**
	 * 括起来的为当前记录的指标值.
	 * 
	 * @param tablename
	 * @param fieldstr
	 * @param nid
	 * @return
	 * @throws Exception
	 */
	private String getCurIndexValue(String tablename, String fieldstr, String nid) throws Exception {
		String express = fieldstr.substring(fieldstr.indexOf("{&") + 2, fieldstr.indexOf("&}"));
		// {*列标题*}or{*现|拟[列标题]*}
		String fieldname = "";
		String value = "";
		if (express.indexOf(":") == -1) {
			if (express.indexOf("[") != -1 && express.indexOf("]") != -1) {
				// {*现|拟[列标题]*}
				fieldname = express.substring(express.indexOf("[") + 1, express.indexOf("]"));
				if (express.indexOf("现") != -1) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname + "_1";
				} else if (express.indexOf("拟") != -1) {
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname + "_2";
				}else{//bug 35198 不写现或者拟默认为变化前指标
					HashMap map = getFielditemid(fieldname);
					fieldname =(String) map.get("itemid");
					express = fieldname + "_1";
				}
				StringBuffer sql = new StringBuffer();
				sql.append("select " + express + " from " + tablename);
				if (nid != null && nid.length() > 3) {
					// liuyz 导出单人和多人模版优化
					sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
				}
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					// System.out.println(sql);
					RowSet rs = dao.search(sql.toString());
					if (rs.next()) {
                        value = rs.getString(1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
					value = fieldstr;
				}
			} else {
				express = getFielditemid(express)+"_1";//bug 35198 不写现或者拟默认为变化前指标
				StringBuffer sql = new StringBuffer();
				sql.append("select " + express + " from " + this.src_per + "A01 where a0100='" + this.src_a0100 + "'");
				ContentDAO dao = new ContentDAO(this.conn);
				try {
					// System.out.println(sql);
					RowSet rs = dao.search(sql.toString());
					if (rs.next()) {
                        value = rs.getString(1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
					value = fieldstr;
				}
			}
			value = changeCodeIdValue(express, value);

		} else {
			// {&子集名称:指标名称&}
			String[] field = express.split(":");
			if (field.length > 1) {
				String fieldsete = field[0];
				String fielddesc = field[1];
				String fieldsetid = "";
				String sub_domainid = "";
				String sub_domain = "";
				if (fieldsete.indexOf("现[") != -1 || fieldsete.indexOf("拟[") != -1 || fieldsete.indexOf("现") == 0
						|| fieldsete.indexOf("拟") == 0) {
					if (fieldsete.indexOf("现[") != -1 || fieldsete.indexOf("现") == 0) {
						String prv = fieldsete.substring(fieldsete.indexOf("[") + 1, fieldsete.indexOf("]"));
						HashMap map = getFieldSetidSub(prv);
						if (map.containsKey("setName")) {
                            fieldsetid = (String) map.get("setName");
                        }
						if (map.containsKey("sub_domain_id")) {
                            sub_domainid = "_" + (String) map.get("sub_domain_id");
                        }
						if (map.containsKey("sub_domain")) {
                            sub_domain = (String) map.get("sub_domain");
                        }
						fieldname = "T_" + fieldsetid + sub_domainid + "_1";
					} else if (fieldsete.indexOf("拟[") != -1 || fieldsete.indexOf("拟") == 0) {
						String prv = fieldsete.substring(fieldsete.indexOf("[") + 1, fieldsete.indexOf("]"));
						HashMap map = getFieldSetidSub(prv);
						if (map.containsKey("setName")) {
                            fieldsetid = (String) map.get("setName");
                        }
						if (map.containsKey("sub_domain_id")) {
                            sub_domainid = "_" + (String) map.get("sub_domain_id");
                        }
						if (map.containsKey("sub_domain")) {
                            sub_domain = (String) map.get("sub_domain");
                        }
						fieldname = "T_" + fieldsetid + sub_domainid + "_2";

					}
				} else {
					fieldsetid = getFieldSetid(fieldsete);
					fieldname = "T_" + fieldsetid + "_1";
				}
				String columnName = getFielditemid(fielddesc, fieldsetid);
				if(StringUtils.isNotBlank(columnName)){
					FieldItem fieldItem = DataDictionary.getFieldItem(columnName);
					StringBuffer sql = new StringBuffer();
					sql.append("select " + fieldname + " from " + tablename);
					if (nid != null && nid.length() > 3) {
						// liuyz 导出单人和多人模版优化
						sql.append(" where lower(basepre)='" + nid.substring(0, 3).toLowerCase() + "' and a0100='" + nid.substring(4) + "'");
						if (taskid != null && !"".equals(taskid) && taskid.trim().length() > 0 && !"0".equals(taskid)) {
							sql.append(" and ins_id in (select ins_id from t_wf_task where task_id in (" + taskid + "))");
						}
					}
					ParseAsyXml parseAsyXml = new ParseAsyXml(this.conn);
					ArrayList valuelist = parseAsyXml.getFiledsetValue(sql.toString(), fieldname, columnName, sub_domain);
					for (int i = 0; i < valuelist.size(); i++) {
						if (fieldItem != null && fieldItem.isCode()) {
							value += AdminCode.getCodeName(fieldItem.getCodesetid(), (String) valuelist.get(i)) + "\n";
						} else {
                            value += valuelist.get(i) + "\n";//bug 36776 人事异动：模板中子集指标取最近1-2条，输出word时记录建议不要以~号区分开.
                        }
					}
					if(value.length()>0){
						value=value.substring(0,value.length()-1);
					}
				}
			} else {
				value = fieldstr;
			}
		}
		return value;
	}

	/**
	 * 判断issubmitflag字段是否存在
	 * 
	 * @param tablename
	 */
	public void isSubmitflagSave(String tablename) {
		String sql = "select * from " + tablename + " where 1=2";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			rs = dao.search(sql.toString());
			ResultSetMetaData rm = rs.getMetaData();
			int column_count = rm.getColumnCount();
			for (int i = 1; i <= column_count; i++) {
				String column_name = rm.getColumnName(i);
				if (column_name == null || column_name.length() <= 0) {
                    column_name = "";
                }
				if ("submitflag".equalsIgnoreCase(column_name)) {
					this.issubmitflag = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getError_fieldname() {
		if (error_fieldname != null && error_fieldname.length() > 0) {
			error_fieldname = error_fieldname.replace("wlhxryhrp", "");
		}
		return error_fieldname;
	}

	public void setError_fieldname(String error_fieldname) {
		this.error_fieldname = error_fieldname;
	}

	private String getClassName(String class_id) {
		if (class_id == null || class_id.length() <= 0) {
            return "休息班";
        }
		String sql = "select name from kq_class where class_id='" + class_id + "'";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			if (rs.next()) {
                return rs.getString("name");
            } else {
                return "";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "休息班";
	}

	/**
	 * 通过codeitemdesc得到codeitemid
	 * 
	 * @param codesetid
	 * @param codeitemdesc
	 * @return
	 */
	private String getCodeItemid(String codesetid, String codeitemdesc) {
		String sql = "select codeitemid from codeitem where codeitemdesc='" + codeitemdesc + "' and codesetid='"
				+ codesetid + "'";
		String codeitemid = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			if (rs.next()) {
                return rs.getString("codeitemid");
            } else {
                return "";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * gaohy 新增情况 求列表中指标为子集
	 **/
	private ArrayList getDataList(String xml, String express, String fieldname, String expressvalue,
			FieldItem fielditem) throws Exception {

		ArrayList datalist = new ArrayList();
		try {
			org.jdom.Document doc = PubFunc.generateDom(xml);
			Element element = null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath = "/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			element = (Element) findPath.selectSingleNode(doc);
			String expresslast = express.substring(express.indexOf(")") + 1);
			String expresslastvalue = "";// 获得取值顺序
			if (expresslast.indexOf("(") != -1 && expresslast.indexOf(")") != -1) {
				expresslastvalue = expresslast.substring(expresslast.indexOf("(") + 1, expresslast.indexOf(")"));
			}

			String xmlvalue = "";// 获得需求指标对应的值
			if (element != null) {// 获得子集中所有的指标和对应值
				String xmlColumns[] = element.getAttributeValue("columns").split("`");// 获取所有指标
				List record = new ArrayList();
				record = element.getChildren();
				List nList = new ArrayList();
				Pattern pattern = Pattern.compile("[0-9]*"); // 正则
				Matcher isNum = pattern.matcher(expresslastvalue);// 判断是否为数字
				if (expresslastvalue.indexOf("a") != -1) {// 前第几条
					int a = Integer.parseInt(expresslastvalue.substring(expresslastvalue.indexOf("a") + 1));
					if (record.size() >= a) {
                        nList = record.subList(a - 1, a);
                    }
				} else if (expresslastvalue.indexOf("d") != -1) {// 后第几条
					int d = record.size()
							- Integer.parseInt(expresslastvalue.substring(expresslastvalue.indexOf("d") + 1));
					if (d >= 0) {
                        nList = record.subList(d, d + 1);
                    }
				} else if (expresslastvalue.length() > 0 && isNum.matches()) {
					nList = record.subList(0, Integer.parseInt(expresslastvalue));
				} else {
					nList = record;
				}
				Element e = null;
				String xmlText[] = null;
				for (int i = 0; i < nList.size(); i++) {
					e = (Element) nList.get(i);
					xmlText = e.getText().trim().split("`");// 获取所有指标对应值
					for (int j = 0; j < xmlText.length; j++) {// 指标值不一定有，所有循环次数小于xmlText.length
						HashMap fieldMap = new HashMap();// 指标和对应值
						fieldMap.put(xmlColumns[j], xmlText[j]);
						String isvalue = (String) fieldMap.get(fieldname.toUpperCase());
						if (isvalue != null) {
                            xmlvalue += isvalue + ",";// 获得需求指标对应的值
                        }
					}
				}

			}
			if ("求列表".equals(expressvalue)) {

				if ("D".equals(fielditem.getItemtype()))// 指标为日期型
				{
					if (xmlvalue != null && !"".equals(xmlvalue)) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date = new java.util.Date();
						int i = fielditem.getItemlength();// 获取该指标规定长度

						if (xmlvalue.indexOf(",") != -1) {// 多条记录时
							String arrayVlue[] = xmlvalue.split(",");
							for (int j = 0; j < arrayVlue.length; j++) {
								date = sdf.parse(arrayVlue[j]);// 将指标值转换成对应的日期
								String tempValue = DateUtils.format(date, "yyyy.MM.dd HH:mm:ss");// 获得需要的格式
								if (i <= 10) {
									datalist.add(tempValue.substring(0, fielditem.getItemlength()));

								} else if (i > 19) {
									datalist.add(tempValue.substring(0, 19));

								} else {
									datalist.add(tempValue.substring(0, fielditem.getItemlength() + 1));

								}
							}
						} else {

							date = sdf.parse(xmlvalue);// 将指标值转换成对应的日期
							String tempValue = DateUtils.format(date, "yyyy.MM.dd HH:mm:ss");// 获得需要的格式
							if (i <= 10) {
								datalist.add(tempValue.substring(0, fielditem.getItemlength()));

							} else if (i > 19) {
								datalist.add(tempValue.substring(0, 19));

							} else {
								datalist.add(tempValue.substring(0, fielditem.getItemlength() + 1));

							}
						}
					} else {
						datalist.add("");
					}
				} else if ("N".equals(fielditem.getItemtype()) && fielditem.getDecimalwidth() > 0) // 为number型，xyy20141127取小数的
				{
					if (xmlvalue.indexOf(",") != -1) {// 多条记录时
						String arrayVlue[] = xmlvalue.split(",");
						for (int j = 0; j < arrayVlue.length; j++) {
							xmlvalue = arrayVlue[j];
							String temp = xmlvalue != null ? xmlvalue : "0";
							int flotNum = fielditem.getDecimalwidth();
							if (temp.lastIndexOf(".") < 0) {
								for (int i = 0; i < flotNum; i++) {
									if (i == 0) {
										temp += ".0";
									} else {
										temp += "0";
									}
								}
							} else {
								int index = temp.lastIndexOf(".");
								if (temp.substring(index + 1).length() < flotNum) {
									for (int i = 0; i < flotNum - temp.substring(index + 1).length(); i++) {
										temp += "0";
									}
								}
							}
							datalist.add(temp);
						}
					} else {// 一条记录
						String temp = xmlvalue != null ? xmlvalue : "0";
						int flotNum = fielditem.getDecimalwidth();
						if (temp.lastIndexOf(".") < 0) {
							for (int i = 0; i < flotNum; i++) {
								if (i == 0) {
									temp += ".0";
								} else {
									temp += "0";
								}
							}
						} else {
							int index = temp.lastIndexOf(".");
							if (temp.substring(index + 1).length() < flotNum) {
								for (int i = 0; i < flotNum - temp.substring(index + 1).length(); i++) {
									temp += "0";
								}
							}
						}
						datalist.add(temp);
					}
				} else if ("q1104".equalsIgnoreCase(fieldname) || "q1304".equalsIgnoreCase(fieldname)
						|| "q1504".equalsIgnoreCase(fieldname)) {
					/** 考勤班次 **/
					if (xmlvalue.indexOf(",") != -1) {// 多条记录时
						String arrayVlue[] = xmlvalue.split(",");
						for (int j = 0; j < arrayVlue.length; j++) {
							xmlvalue = arrayVlue[j];
							datalist.add(getClassName(xmlvalue));
						}
					} else {// 一条记录
						datalist.add(getClassName(xmlvalue));
					}
				} else {// 其他类型
					if (xmlvalue.indexOf(",") != -1) {// 多条记录时
						String arrayVlue[] = xmlvalue.split(",");
						for (int j = 0; j < arrayVlue.length; j++) {
							xmlvalue = arrayVlue[j];
							datalist.add(xmlvalue != null ? changeCodeIdValue(fieldname.toLowerCase(), xmlvalue) : "");
						}
					} else {// 一条记录
						datalist.add(xmlvalue != null ? changeCodeIdValue(fieldname.toLowerCase(), xmlvalue) : "");
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datalist;
	}

	/**
	 * hzValue 待解析的表格字符串 tablename 表格名称 nid2 paramBean 参数
	 * @param bean2 
	 */
	public ArrayList executeTemplateDocumentSubList(String hzValue, String tablename, String nid2,
			LazyDynaBean paramBean, Integer rowNum, LazyDynaBean bean2) throws Exception {
		String[] object=nid2.split("`");//nid2某些情况会拼有ins_id,再次做判断去掉。
		if(object.length>=2){
			nid2=object[0]+"`"+object[1];
		}
		ArrayList subList = new ArrayList();
		String[] hzItem = hzValue.split("`");
		ArrayList titleList = new ArrayList();
		Integer type = 0;// 表格格式0 花名册形式 1、子集数据 2、其他形式
		ArrayList hzList = new ArrayList();
		Boolean isFirstCol=true;//确定是不是输出了非常量标签，ture：没有输出，false：输出了一个
		// 循环表格中每一列的字段
		for (int num = 0; num < hzItem.length; num++) {
			String temp = hzItem[num];
			// 第一列确认表格格式
			if (isFirstCol) {
				if(temp.startsWith("{#")&&temp.endsWith("#}")){//bug 35236 常量在表格中可以显示
					hzList.add(temp);
					String title = temp.substring(temp.indexOf("{#") + 2, temp.indexOf("#}"));
					titleList.add(title);
				}
				else if (temp.endsWith(")*}")) {
					type = 0;// 花名册形式表格
					String value = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
					String title = temp.substring(temp.indexOf("{*") + 2, temp.indexOf("("));
					hzList.add(value);
					titleList.add(title);
					isFirstCol=false;
				} else if (temp.endsWith("&}") && temp.contains(":")) {
					type = 1;// 子集
					hzList.add(temp);
					isFirstCol=false;
				} else if (temp.endsWith("*}") && temp.startsWith("{*")) {
					type = 2;
					String value = getFieldName(temp);
					hzList.add(value);
					isFirstCol=false;
				}
			}
			else {
				if(temp.startsWith("{#")&&temp.endsWith("#}")){
					hzList.add(temp);
					String title = temp.substring(temp.indexOf("{#") + 2, temp.indexOf("#}"));
					titleList.add(title);
				}else{
					switch (type) {
					case 0: {
						if (temp.endsWith(")*}")) {
							String value = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
							String title = temp.substring(temp.indexOf("{*") + 2, temp.indexOf("("));
							hzList.add(value);
							titleList.add(title);
						} else {
							throw new Exception("模版中花名册形式表格定义错误");
						}
						break;
					}
					case 1: {
						if (temp.endsWith("&}") && temp.contains(":")) {
							hzList.add(temp);
						} else {
							throw new Exception("模版中子集表格定义错误");
						}
						break;
					}
					case 2: {
						if (temp.endsWith("*}") && temp.startsWith("{*")) {
							String value = getFieldName(temp);
							hzList.add(value);
						} else {
							throw new Exception("模版中表格格式定义错误");
						}
						break;
					}
					default:
						throw new Exception("模版中表格格式定义错误");
					}
				}
			}
		}
		// 花名册形式表格解析数据组返回的数组
		if (type == 0) {
			bean2.set("rowNum", rowNum);//根据模版指定的行数显示数据
			// 花名册形式表格需要返回标题列，所以获取的数据列行数为表格行数-1
			ArrayList list = setTableElementData(rowNum - 1, hzList, tablename);
			LazyDynaBean bean = new LazyDynaBean();
			// 设置返回的标题行数据
			for (int j = 0; j < hzItem.length; j++) {
				String temp = hzItem[j];
				String value = (String) titleList.get(j);
				bean.set(hzItem[j], value);
			}
			subList.add(bean);
			// 设置返回的数据
			for (int i = 0; i < list.size(); i++) {
				ArrayList rowList = (ArrayList) list.get(i);
				bean = new LazyDynaBean();
				for (int j = 0; j < hzItem.length; j++) {
					String value = "";
					if (j < rowList.size()) {
						value = (String) rowList.get(j);
						if("{#序号#}".equals(value)){
							value=String.valueOf(i+1);
						}
					}
					bean.set(hzItem[j], value);
				}
				subList.add(bean);
			}
		}
		// 子集表格
		else if (type == 1) {
			bean2.set("rowNum", rowNum);//根据模版指定的行数显示数据
			for (int i = 0; i < hzList.size(); i++) {
				// 获取解析的结果，返回的是每列所有值用`号分割。
				Boolean isNull = false;
				String value=null;
				String fieldname=String.valueOf(hzList.get(i));
				if(fieldname.startsWith("{#")&&fieldname.endsWith("#}")){
					value=getConstantValue(fieldname,tablename);
					if("{#序号#}".equals(value)){
						value=String.valueOf(i+1);
					}
				}
				else{
					value = getCurIndexValue(tablename, String.valueOf(hzList.get(i)), nid2);
				}
				if (value == null || value.trim().length() == 0) {
                    isNull = true;
                }
				String[] values = null;
				if (!isNull) {
					value=value.replace("\n", "`");
					values = value.split("`", -1);
					for (int j = 0; (j < values.length && j < rowNum)||(j < values.length &&  rowNum<0); j++) {
						if (j > subList.size() - 1) {
							LazyDynaBean bean = new LazyDynaBean();
							bean.set(String.valueOf(hzList.get(i)), values[j].replace("&nbsp;", " "));
							subList.add(bean);
						} else {
							LazyDynaBean bean = (LazyDynaBean) subList.get(j);
							bean.set(String.valueOf(hzList.get(i)), values[j].replace("&nbsp;", " "));
						}
					}
				} else {
					if (subList.size() == 0) {
						LazyDynaBean bean = new LazyDynaBean();
						bean.set(String.valueOf(hzList.get(i)), "");
						subList.add(bean);
					} else {
						LazyDynaBean bean = (LazyDynaBean) subList.get(0);
						bean.set(String.valueOf(hzList.get(i)), "");
					}
				}
			}
		}
		// 解析其他表格形式
		else if (type == 2) {
			ArrayList list = setTableElementData(-1, hzList, tablename);
			bean2.set("rowNum", list.size());//根据数据的行数自动增加表格行数
			LazyDynaBean bean = new LazyDynaBean();
			for (int i = 0; i < list.size(); i++) {
				ArrayList rowList = (ArrayList) list.get(i);
				bean = new LazyDynaBean();
				for (int j = 0; j < hzItem.length; j++) {
					String value = "";
					if (j < rowList.size()) {
						value = (String) rowList.get(j);
						if("{#序号#}".equals(value)){
							value=String.valueOf(i+1);
						}
					}
					bean.set(hzItem[j], value);
				}
				subList.add(bean);
			}
		}
		return subList;
	}

	// 解析图片
	public String executeTemplatePhoto(String hz, String tablename, String object_id, LazyDynaBean paramBean)
			throws Exception {
		String filename = executeTemplateDocument(hz, tablename, object_id, paramBean);
		filename = filename.replaceAll("wlhxryhrp", " ");
		filename = filename.replaceAll("xrywlh888", "\r\n");
		return filename;
	}

	// 解析审批过程 审批过程(节点定义的名称)
	private String getProcessValue(String express, String tablename, String nid) {
		String returnStr = "";
		try { 
			TemplateParam paramBo =this.dataBo.getParamBo();
			String fieldname = "";
			express = express.substring(express.indexOf("{@") + 2, express.indexOf("@}"));
			String expresspre = express.substring(0, express.indexOf("(")); // expresspre={[或者 [指前缀 for example{[A0101]...
			String fielddesc = express.substring(express.indexOf("(") + 1, express.indexOf(")")); // A0101
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                format_str="yyyy-MM-dd hh24:mi";
            }
				if (paramBo.getSp_mode() == 0) {// 自动流转
					ArrayList paramList = new ArrayList();
				String sql = "select content,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,a0101,sp_yj,actorid from t_wf_task where node_id in (select node_id from t_wf_node where tabid=? and nodename=?) and task_state<>'3' ";
					paramList.add(this.tabid);
					paramList.add(fielddesc);
					if (tablename.equalsIgnoreCase("templet_"+this.tabid)) {
						if ("1".equalsIgnoreCase(this.infor_type)) {
							String[] object = nid.split("`");
							String dbName = object[0];
							String a0100 = object[1];
							String ins_id = object[2];
							sql += " and task_id in (select twt.task_id from " + tablename
									+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where lower(a.basepre)=? and a.a0100=? and a.ins_id=?) order by end_date asc";
							paramList.add(dbName.toLowerCase());
							paramList.add(a0100);
							paramList.add(ins_id);
						} else if ("2".equalsIgnoreCase(this.infor_type)) {
							String[] object = nid.split("`");
							String dbName = object[0];
							String ins_id = object[1];
							sql += " and task_id in (select twt.task_id form " + tablename
									+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.b0110=? and a.ins_id=?)  order by end_date asc";
							paramList.add(dbName);
							paramList.add(ins_id);
						} else if ("3".equalsIgnoreCase(this.infor_type)) {
							String[] object = nid.split("`");
							String dbName = object[0];
							String ins_id = object[1];
							sql += " and task_id in (select twt.task_id form " + tablename
									+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.e01a1=?  and a.ins_id=?)  order by end_date asc";
							paramList.add(dbName);
							paramList.add(ins_id);
						}

					} else {
						return "";
					}

				RowSet rowset = dao.search(sql, paramList);
				int maxRows=0;
				String  returnStrOne="";
				String  returnStrTwo="";
				while (rowset.next()) {
					maxRows++;
					String content=rowset.getString("content");
					String end_date=rowset.getString("end_date");
					if(StringUtils.isBlank(content)||"null".equalsIgnoreCase(content))//bug 33914 如果获取的值是null，就置为空
                    {
                        content="";
                    }
					if(StringUtils.isBlank(end_date)||"null".equalsIgnoreCase(end_date)) {
                        end_date="";
                    }
					//bug37411 审批意见指标中换行不起作用
				    returnStrOne += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"wlhxryhrp"+end_date+"xrywlh888";
				    returnStrTwo += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"xrywlh888";
				}
				if(maxRows>1) {
                    returnStr=returnStrOne;
                } else {
                    returnStr=returnStrTwo;
                }
					PubFunc.closeDbObj(rowset);
				} else {
					if (paramBo.isAllow_defFlowSelf()) {// 自定义审批流程
						if (tablename.equalsIgnoreCase("templet_"+this.tabid)) {
							ArrayList paramList = new ArrayList();
						String sql = "select tw.task_id,tw.content content,"+Sql_switcher.dateToChar("tw.end_date",format_str)+" end_date,tw.a0101 a0101,tw.sp_yj sp_yj,tw.actorid actorid,tm.* from t_wf_task tw left join t_wf_node_manual tm on (tw.node_id=tm.node_id or tw.node_id=tm.id) and tm.actorname=? and tw.ins_id=tm.ins_id where tabid=? and tw.task_state<>'3' " ;
							paramList.add(fielddesc);
							paramList.add(this.tabid);
							if ("1".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String a0100 = object[1];
								String ins_id = object[2];
								sql += " and task_id in (select twt.task_id from " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where lower(a.basepre)=? and a.a0100=? and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName.toLowerCase());
								paramList.add(a0100);
								paramList.add(ins_id);
							} else if ("2".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String ins_id = object[1];
								sql += " and task_id in (select twt.task_id form " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.b0110=? and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName);
								paramList.add(ins_id);
							} else if ("3".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String ins_id = object[1];
								sql += " and task_id in (select twt.task_id form " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.e01a1=?  and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName);
								paramList.add(ins_id);
							}
							RowSet rowset = dao.search(sql,paramList);
							int maxRows=0;
						String  returnStrOne="";
						String  returnStrTwo="";
						while (rowset.next()) {
							maxRows++;
							//bug37411 审批意见指标中换行不起作用
							String content=rowset.getString("content");
							String end_date=rowset.getString("end_date");
							if(StringUtils.isBlank(content)||"null".equalsIgnoreCase(content))//bug 33914 如果获取的值是null，就置为空
                            {
                                content="";
                            }
							if(StringUtils.isBlank(end_date)||"null".equalsIgnoreCase(end_date)) {
                                end_date="";
                            }
						    returnStrOne += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"wlhxryhrp"+end_date+"xrywlh888";
						    returnStrTwo += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"xrywlh888";
						}
						if(maxRows>1) {
                            returnStr=returnStrOne;
                        } else {
                            returnStr=returnStrTwo;
                        }
							PubFunc.closeDbObj(rowset);
						} else {
                            return "";
                        }
					} else {// 手动审批

						if (tablename.equalsIgnoreCase("templet_"+this.tabid)) {
							ArrayList paramList = new ArrayList();
							String sql = "select content,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,a0101,sp_yj,actorid from t_wf_task  where actorname=? and task_state<>'3' ";
							paramList.add(fielddesc);
							if ("1".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String a0100 = object[1];
								String ins_id = object[2];
								sql += " and task_id in (select twt.task_id from " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where lower(a.basepre)=? and a.a0100=? and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName.toLowerCase());
								paramList.add(a0100);
								paramList.add(ins_id);
							} else if ("2".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String ins_id = object[1];
								sql += " and task_id in (select twt.task_id form " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.b0110=? and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName);
								paramList.add(ins_id);
							} else if ("3".equalsIgnoreCase(this.infor_type)) {
								String[] object = nid.split("`");
								String dbName = object[0];
								String ins_id = object[1];
								sql += " and task_id in (select twt.task_id form " + tablename
										+ " a left join t_wf_task_objlink twt on a.seqnum=twt.seqnum and a.ins_id=twt.ins_id where a.e01a1=?  and a.ins_id=?)  order by end_date asc";
								paramList.add(dbName);
								paramList.add(ins_id);
							}
							RowSet rowset = dao.search(sql,paramList);
							int maxRows=0;
						String  returnStrOne="";
						String  returnStrTwo="";
						while (rowset.next()) {
							maxRows++;
							//bug37411 审批意见指标中换行不起作用
							String content=rowset.getString("content");
							String end_date=rowset.getString("end_date");
							if(StringUtils.isBlank(content)||"null".equalsIgnoreCase(content))//bug 33914 如果获取的值是null，就置为空
                            {
                                content="";
                            }
							if(StringUtils.isBlank(end_date)||"null".equalsIgnoreCase(end_date)) {
                                end_date="";
                            }
						    returnStrOne += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"wlhxryhrp"+end_date+"xrywlh888";
						    returnStrTwo += content.replace("\r\n", "xrywlh888").replace("\r", "xrywlh888").replace("\n", "xrywlh888") +"xrywlh888";
						}
						if(maxRows>1) {
                            returnStr=returnStrOne;
                        } else {
                            returnStr=returnStrTwo;
                        }
							PubFunc.closeDbObj(rowset);
						} else {
                            return "";
                        }
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			returnStr = "";
		}
		return returnStr;
	}
	
	public static String dateValue(String value)
	{
	    if ((value == null) || ("".equals(value))) {
            return " NULL ";
        }
		switch (Sql_switcher.searchDbServer())
		{
			case 1:
				value ="CONVERT(varchar(100), "+value+", 120)";
				break;
			case 2:
			case 5:
			case 7:
				value = "TO_DATE(" + value + ")"; 
				break;
			case 3:
				value = "TO_DATE(" + value + ")";
				break; 
			case 4:
			case 6:
		}
	    return value;
	 }
	/**
     * 格式化日期字符串
     * 
     * @param value
     *            日期字段值 yyyy-mm-dd
     * @param ext
     *            扩展
     * @return
     */
	private String formatDateValue(String value,String ext ,int disformat)
	{
		StringBuffer buf=new StringBuffer();
		if(ext!=null&&ext.indexOf("<EXPR>")!=-1)
		{
			int f=ext.indexOf("<EXPR>");
			int t=ext.indexOf("</FACTOR>"); 
			String _temp=ext.substring(0,f);
			String _temp2=ext.substring(t+9);
			ext=_temp+_temp2; 
		}
		String prefix="",strext="";
		int idx=ext.indexOf(",");  //-,至今
		if(idx==-1)
		{
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else
		{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if("".equals(value))
		{
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else
		{
			buf.append(prefix);
		}
		Date date=null;
		//55083
		if(value.indexOf(".")>-1) {
			//yyyy.MM
			if(value.length()>4&&value.length()<=7) {
				date=DateUtils.getDate(value,"yyyy.MM");			
			}else if(value.length()==10) {
				date=DateUtils.getDate(value,"yyyy.MM.dd");			
			}else {
				date=DateUtils.getDate(value,"yyyy.MM.dd HH:mm:ss");
			}
		}else {
			if(value.length()>4&&value.length()<=7) {
				date=DateUtils.getDate(value,"yyyy-MM");			
			}else if(value.length()==10) {
				date=DateUtils.getDate(value,"yyyy-MM-dd");			
			}else {
				date=DateUtils.getDate(value,"yyyy-MM-dd HH:mm:ss");
			}
		}
		
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		int hour=DateUtils.getHour(date);
		int minute=DateUtils.getMinute(date);
		String strv[]=exchangNumToCn(year,month,day);	
		value=value.replaceAll("-",".");
		switch(disformat)
		{
		case 6: //1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 7: //91.12.3
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 8://1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);			
			break;
		case 9://1992.02
			buf.append(value.substring(0,7));
			break;
		case 10://92.2
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			break;
		case 11://98.02
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			break;
		case 12://一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 13://一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");			
			break;
		case 14://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 15://1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 16://91年1月2日
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 17://91年1月
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");			
			break;
		case 18://年龄
			buf.append(getAge(year,month,day));
			break;
		case 19://1991（年）
			buf.append(year);
			break;
		case 20://1 （月）
			buf.append(month);
			break;
		case 21://23 （日）
			buf.append(day);
			break;
		case 22://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 23://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 24://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		case 25://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}	
			if(hour>=10) {
                buf.append("  ").append(hour);
            } else
			{
				buf.append("  0");
				buf.append(hour);
			}		
			if(minute>=10) {
                buf.append(":").append(minute);
            } else
			{
				buf.append(":0");
				buf.append(minute);
			}		
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
	}
	/**
     * 计算年龄
     * 
     * @param nyear
     * @param nmonth
     * @param nday
     * @return
     */
    private String getAge(int nyear, int nmonth, int nday) {
        int ncyear, ncmonth, ncday;
        Date curdate = new Date();
        ncyear = DateUtils.getYear(curdate);
        ncmonth = DateUtils.getMonth(curdate);
        ncday = DateUtils.getDay(curdate);
        StringBuffer buf = new StringBuffer();

        int result = ncyear - nyear;
        if (nmonth > ncmonth) {
            result = result - 1;
        } else {
            if (nmonth == ncmonth) {
                if (nday > ncday) {
                    result = result - 1;
                }
            }
        }
        buf.append(result);
        return buf.toString();
    }
    /**
     * 解释Formula字段的内容 for example ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
     * 
     * @return
     */
    private String[] getPrefixCond(String formula) {
        String[] preCond = new String[3];
        int idx = formula.indexOf("<");
        if (idx == -1) {
            preCond[0] = formula;
        } else {
            preCond[0] = formula.substring(0, idx);
            preCond[2] = getPattern("FACTOR", formula) + ",";
            preCond[2] = preCond[2].replaceAll(",", "`");
            preCond[1] = getPattern("EXPR", formula);
        }
        return preCond;
    }
    private String getPattern(String strPattern, String formula) {
        int iS, iE;
        String result = "";
        String sSP = "<" + strPattern + ">";
        iS = formula.indexOf(sSP);
        String sEP = "</" + strPattern + ">";
        iE = formula.indexOf(sEP);
        if (iS >= 0 && iS < iE) {
            result = formula.substring(iS + sSP.length(), iE);
        }
        return result;
    }
    /**
     * 数字换算
     * 
     * @param strV
     * @param flag
     * @return
     */
    private String[] exchangNumToCn(int year, int month, int day) {
        String[] strarr = new String[3];
        StringBuffer buf = new StringBuffer();
        String value = String.valueOf(year);
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '1' :
                    buf.append("一");
                    break;
                case '2' :
                    buf.append("二");
                    break;
                case '3' :
                    buf.append("三");
                    break;
                case '4' :
                    buf.append("四");
                    break;
                case '5' :
                    buf.append("五");
                    break;
                case '6' :
                    buf.append("六");
                    break;
                case '7' :
                    buf.append("七");
                    break;
                case '8' :
                    buf.append("八");
                    break;
                case '9' :
                    buf.append("九");
                    break;
                case '0' :
                    buf.append("零");
                    break;
            }
        }
        strarr[0] = buf.toString();
        buf.setLength(0);
        switch (month) {
            case 1 :
                buf.append("一");
                break;
            case 2 :
                buf.append("二");
                break;
            case 3 :
                buf.append("三");
                break;
            case 4 :
                buf.append("四");
                break;
            case 5 :
                buf.append("五");
                break;
            case 6 :
                buf.append("六");
                break;
            case 7 :
                buf.append("七");
                break;
            case 8 :
                buf.append("八");
                break;
            case 9 :
                buf.append("九");
                break;
            case 10 :
                buf.append("十");
                break;
            case 11 :
                buf.append("十一");
                break;
            case 12 :
                buf.append("十二");
                break;
        }
        strarr[1] = buf.toString();
        buf.setLength(0);
        switch (day) {
            case 1 :
                buf.append("一");
                break;
            case 2 :
                buf.append("二");
                break;
            case 3 :
                buf.append("三");
                break;
            case 4 :
                buf.append("四");
                break;
            case 5 :
                buf.append("五");
                break;
            case 6 :
                buf.append("六");
                break;
            case 7 :
                buf.append("七");
                break;
            case 8 :
                buf.append("八");
                break;
            case 9 :
                buf.append("九");
                break;
            case 10 :
                buf.append("十");
                break;
            case 11 :
                buf.append("十一");
                break;
            case 12 :
                buf.append("十二");
                break;
            case 13 :
                buf.append("十三");
                break;
            case 14 :
                buf.append("十四");
                break;
            case 15 :
                buf.append("十五");
                break;
            case 16 :
                buf.append("十六");
                break;
            case 17 :
                buf.append("十七");
                break;
            case 18 :
                buf.append("十八");
                break;
            case 19 :
                buf.append("十九");
                break;
            case 20 :
                buf.append("二十");
                break;
            case 21 :
                buf.append("二十一");
                break;
            case 22 :
                buf.append("二十二");
                break;
            case 23 :
                buf.append("二十三");
                break;
            case 24 :
                buf.append("二十四");
                break;
            case 25 :
                buf.append("二十五");
                break;
            case 26 :
                buf.append("二十六");
                break;
            case 27 :
                buf.append("二十七");
                break;
            case 28 :
                buf.append("二十八");
                break;
            case 29 :
                buf.append("二十九");
                break;
            case 30 :
                buf.append("三十");
                break;
            case 31 :
                buf.append("三十一");
                break;
        }
        strarr[2] = buf.toString();
        return strarr;
    }
}
