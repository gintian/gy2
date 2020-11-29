package com.hjsj.hrms.businessobject.report.user_defined_reoprt;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import java.io.BufferedWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * Title:UserdefineSqlFile.java
 * </p>
 * <p>
 * Description>:UserdefineSqlFile.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Mar 8, 2010 10:21:25 AM
 * </p>
 * <p>
 * 
 * @version: 4.0
 *           </p>
 *           <p>
 * @author: s.xin
 */
public class UserdefineSqlFile {
	// xml document
	private Document doc = null;
	// 数据操作对象
	private ContentDAO dao = null;
	// 用户对象
	private UserView userView;
	// 是否取得权限范围内的数据
	private boolean ispriv = true;
	// 全局参数数据集
	private HashMap publicElementMap = new HashMap();
	// 全局参数值集合
	private HashMap publicParamMap = new HashMap();
	// 取得操作用户的管理范围代码类
	private String privcode = "";
	// 取得管理范围代码值和getManagePrivCode一块用
	private String privcodevalue = "";
	// 全部sql
	private HashMap sqlsMap = null;
	private String personPurviewSql = "";
	private Category cat = null;
	// 错误信息
	public String errorMr = "";
	// 错误标示
	public boolean errorFlag = false;
	// 多选
	private String field_getvaluetype_pubname;
	// 输入参数
	private HashMap inputParamMap = new HashMap();
	// java转换参数
	private HashMap transOldMap = new HashMap();
	// java转换参数<name, class路径>
	private Map transMap = new HashMap();
	
	// 数据库连接
	private Connection conn;


	public Map getTransMap() {
		return transMap;
	}

	public void setTransMap(Map transMap) {
		this.transMap = transMap;
	}

	public HashMap getTransOldMap() {
		return transOldMap;
	}

	public void setTransOldMap(HashMap transOldMap) {
		this.transOldMap = transOldMap;
	}

	// 参数设置
	private HashMap paramSetMap = new HashMap();
	// sql语句对象
	private ArrayList sql_List = new ArrayList();
	private BufferedWriter writer;
	/**
	 * 构造方法，初始化
	 * @param doc 
	 * @param userView
	 * @param dao
	 * @param ispriv
	 */
	public UserdefineSqlFile(Document doc, UserView userView, ContentDAO dao,
			boolean ispriv,Connection conn) {
		this.userView = userView;
		this.dao = dao;
		this.conn = conn;
		this.ispriv = ispriv;
		this.doc = doc;
		// 获得公共参数对象
		publicElement();
		// 获得控制参数
		paramSet();
		// 权限
		if (!this.userView.isSuper_admin()) {
			this.personPurviewSql = getPersonPurview();
			this.privcode = this.userView.getManagePrivCode();
			this.privcodevalue = this.userView.getManagePrivCodeValue();
		} else {
			this.privcode = this.userView.getManagePrivCode();
			if (this.privcode == null || this.privcode.length() <= 0) {
                this.privcode = "UN";
            }
			this.privcodevalue = this.userView.getManagePrivCodeValue();
			if (this.privcodevalue == null || this.privcodevalue.length() <= 0) {
                this.privcodevalue = "";
            }
		}
		initSystemParam();
		// sql数据集
		this.sqlsMap = getSqlParam();
		this.cat = Category.getInstance("com.hrms.frame.dao.DAODebug");
	}

	private void initSystemParam() {
		// 获得权限范围内的人员库
		String nbase = getDBNames(userView);
		publicParamMap.put("nbase", nbase);
		// 获得权限范围表的sql语句
		String privtablesql = getPersonPurview();
		if (nbase.length() == 0) {
			privtablesql = "select a0100,'Usr' nbase from usra01 where 1=2";
		} else {
			privtablesql = "select axxxx a0100,bxxxx nbase from ("+privtablesql+")mmm";
		}
		
		publicParamMap.put("privtablesql", privtablesql);
	}
	/**
	 * 获得用户管理的人员库
	 * @param userView
	 * @return String "Usr,Trs"
	 */
	private String getDBNames(UserView userView) {
			StringBuffer buff = new StringBuffer();
			ArrayList list = userView.getPrivDbList();
			for (int i = 0; i < list.size(); i++) {
				buff.append(",");
				buff.append((String) list.get(i));
			}
			if (buff.length() > 0) {
				return buff.substring(1);
			} else {
				return "";
			}
	}
	
	/**
	 * 根据用户自定义的人员库获得权限表sql
	 */
	private void initCustomPrivSQL() {
		if (publicParamMap.containsKey("customnbase")) {
			String customnbase = (String) publicParamMap.get("customnbase");
			if (customnbase != null && customnbase.length() > 0) {
				String privtablesql = getPersonPurview(customnbase);
				if (privtablesql.length() == 0) {
					privtablesql = "select a0100,'Usr' nbase from usra01 where 1=2";
				} else {
					privtablesql = "select axxxx a0100,bxxxx nbase from ("+privtablesql+")mmm";
				}
				publicParamMap.put("customprivtablesql", privtablesql);
			} else {
				publicParamMap.put("customprivtablesql", "");
			}
		} else {
			publicParamMap.put("customprivtablesql", "");
		}
	}
	/**
	 * 全局参数
	 */
	private void publicElement() {
		// 参数路径
		String str_path = "/sqls/public_params/param";
		XPath xpath;
		try {
			xpath = XPath.newInstance(str_path);
			List lists = xpath.selectNodes(this.doc);
			if (lists.size() != 0) {
				
				for (Iterator t = lists.iterator(); t.hasNext();) {
					Element element = (Element) t.next();
					String name = element.getAttributeValue("name");
					String value = element.getAttributeValue("value");
					value = value == null ? "" : value;
					// 第一次加载sql查询的值
					String sql = element.getAttributeValue("sql");
					if (sql != null && sql.length() > 0) {
						value = query(sql);
					}
					
					
					String type = element.getAttributeValue("type");
					// 是否是当前时间
					String iscurrent = element.getAttributeValue("iscurrent");
					if (type != null && "D".equalsIgnoreCase(type)) {
						// 时间格式，默认为yyyy-MM-dd
						String formate = element.getAttributeValue("formate");
						if (formate == null || formate.length() <= 0) {
							formate = "yyyy-MM-dd";
						}
						// 时间格式转化
						SimpleDateFormat simple = new SimpleDateFormat(formate);
						if (iscurrent != null
								&& "true".equalsIgnoreCase(iscurrent)) {
							value = simple.format(new Date());
						}
					}
					
					// java代码参数
					String javacode = element.getAttributeValue("javacode");
					
					try {
						if (javacode != null && javacode.length() > 0) {
							IJavaCode iCode = (IJavaCode) Class.forName(javacode).newInstance();
							// 获得参数
							String paramStr = element.getAttributeValue("paramstr");
							value = iCode.getValue(paramStr);
							value = value == null ? "" : value;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					// java 自定义转换参数
					String transcodeclass = element.getAttributeValue("transcodeclass");
					try {
						if (transcodeclass != null && transcodeclass.length() > 0) {
							transMap.put(name, transcodeclass);
							transOldMap.put(name, value);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					publicElementMap.put(name, element);
					publicParamMap.put(name, value);
				}
				Iterator it = transMap.entrySet().iterator();
				try {
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry)it.next();
						ITransCode transCode = (ITransCode) Class.forName(entry.getValue().toString()).newInstance();
						String value = transCode.transCode(publicParamMap, entry.getKey().toString());
						publicParamMap.put(entry.getKey().toString(), value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据sql语句查询值
	 * @param sql
	 * @return 逗号分开的值
	 */
	private String query(String sql) {
		String value = "";
		ResultSet rs = null;
		Statement st = null;
		StringBuffer buff = new StringBuffer();
		try {
			rs = this.dao.search(sql);
			while (rs.next()) {
				String tem = rs.getString(1);
				if (tem != null && tem.length() > 0) {
					buff.append(",");
					buff.append(tem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (buff.length() > 0) {
			value = buff.substring(1);
		}
		return value;
	}
	/**
	 * 属性设置,控制一些按钮的显示或隐藏
	 */
	private void paramSet() {
		String str_path = "/sqls/params_set/set";
		XPath xpath;
		try {
			xpath = XPath.newInstance(str_path);
			List lists = xpath.selectNodes(this.doc);
			if (lists.size() != 0) {
				for (Iterator t = lists.iterator(); t.hasNext();) {
					Element element = (Element) t.next();
					String name = element.getAttributeValue("name");
					String value = element.getAttributeValue("value");
					paramSetMap.put(name.toLowerCase(), value);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将列表值转换成html代码
	 * 
	 * @return String html代码
	 */
	public String getSelectedListString(String tabid) {
		
		// 将转化后的值再保存到公共参数中
		Iterator its = this.transOldMap.entrySet().iterator();
		while(its.hasNext()) {
			Map.Entry entry = (Map.Entry) its.next();
			this.publicParamMap.put(entry.getKey().toString(), entry.getValue());
		}
		
		HashMap map = new HashMap();
		StringBuffer buff = new StringBuffer();
		buff.append(" <link rel='stylesheet' href='/css/css1.css' type='text/css'>\r\n");
		buff.append(" <link rel='stylesheet' type='text/css' href='/ajax/skin.css'></link>\r\n");
		buff.append(" <script language='javascript' src='/ajax/constant.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/basic.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/common.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/control.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/dataset.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/editor.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/dropdown.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/table.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/menu.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/tree.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/pagepilot.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/command.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/ajax/format.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/js/validate.js'></script>\r\n");
		buff.append(" <script language='javascript' src='/js/constant.js'></script>\r\n");
		buff.append(" <script language='JavaScript' src='/js/validate.js'></script>\r\n");
		buff.append(" <script language='JavaScript' src='/js/function.js'></script>\r\n");
		buff.append(" <script language='JavaScript' src='/js/popcalendarFormat.js'></script>\r\n");
		buff.append(" <SCRIPT Language='JavaScript'>dateFormat='yyyy.mm.dd'</SCRIPT>\r\n");
		buff.append(" <script language='JavaScript' src='/js/calendar2.js'></script>\r\n");
		buff.append(" <script language='JavaScript' src='/system/options/customreport/win.js'></script>\r\n");
		buff.append(" <style media='print'><!-- .noprint{display:none; }; --></style>\r\n");
		// 多选控件的css样式
		buff.append("<style>");
		buff.append(".liclass {");
		buff.append("margin:0px;");
		buff.append("padding:0px;");
		buff.append("height:20px;");
		buff.append("text-align:left;");
		buff.append("font-size:12px;");	
		buff.append("text-overflow: ellipsis;");		
		buff.append("overflow-x:visible;}");
		buff.append("</style>");
		
		buff.append(" <style><!-- \r\n .lin{border-bottom:1px groove black} --></style>\r\n");
		buff.append(" <div id='topss' class='noprint lin' width='100%' style='position:relative;'><form id='form1' name='form1'");
		buff.append("action='/system/options/customreport/displaycustomreportservlet?ispriv=1&id=");
		buff.append(tabid);
		buff.append("' method='post' onsubmit='return false;'>\r\n");
		buff.append(" <input type='hidden' id='customid' name='customid' value='");
		buff.append(tabid);
		buff.append("'/>\r\n");

		// 将参数转化后的html代码放入map中，然后进行排序
		HashMap indexParaMap = new HashMap();
		// 顺序号
		int index = 0;
		// 数组角标
		int iArr = 0;
		// 保存index的数组
		String[] indexArr = new String[publicElementMap.size()];

		StringBuffer isNotNullName = new StringBuffer();
		for (Iterator it = publicElementMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			Element element = (Element) e.getValue();
			StringBuffer htmlBuff = new StringBuffer();
			String isshow = element.getAttributeValue("isshow");
			if (isshow != null && "false".equalsIgnoreCase(isshow)) {
				continue;
			}
			List childenList = element.getChildren();
			String codeSetId = element.getAttributeValue("codesetid");

			// 是否允许为空
			String isNull = element.getAttributeValue("isnull");
			String title = element.getAttributeValue("title");
			String name = element.getAttributeValue("name");
			if ("false".equalsIgnoreCase(isNull)) {
				isNotNullName.append(",");
				isNotNullName.append(title);
				isNotNullName.append(":");
				isNotNullName.append(name);
			}
			
			// 定义为列表
			if (codeSetId != null && codeSetId.length() > 0) {
				// 根据codesetid获得列表
				ArrayList list = getCodeList(codeSetId);
				String value = (String) publicParamMap.get(element
						.getAttributeValue("name"));
				;
				htmlBuff.append("&nbsp;&nbsp;&nbsp;");
				if (title != null && title.length() > 0) {
					htmlBuff.append(title);
				}
				htmlBuff
						.append("<input type='text' class='textColorRead' id='");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("report");
				htmlBuff.append("' name='");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("report");
				htmlBuff.append("' value='");
				// 查询代码对应的值
				String paraName = element.getAttributeValue("name");
				String paraValue = (String) publicParamMap.get(paraName);
				htmlBuff.append(getDesc(codeSetId, paraValue));
				htmlBuff
						.append("' style='vertical-align:middle' size='10'/>\r\n");
				/**************update by xiegh on 20171128 bug:32907***********************/
				htmlBuff
						.append("<input type=\"hidden\" ");
				htmlBuff.append(" name=\"");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("reporthidden\"  />\r\n");
				/*************************************/
				htmlBuff.append(" <img  src='/images/code.gif' ");
				htmlBuff.append("onclick=\"");
				// 判断是否为级联树
				String iscascade = element.getAttributeValue("is_cascade_tree");
				// 是否要加载权限 0为不加载权限，1为加载权限（根据管理权限）
				String priv = element.getAttributeValue("priv");
				if (priv == null || priv.length() == 0) {
					priv = "0";
				}
				
				// 多选类型
				String checkmodel = element.getAttributeValue("checkmodel");
				if (checkmodel == null || checkmodel.length() == 0 || "0".equalsIgnoreCase(checkmodel)) {
					checkmodel = "childCascade";
				} else if ("1".equalsIgnoreCase(checkmodel)) {
					checkmodel = "parentCascade";
				} else if ("2".equalsIgnoreCase(checkmodel)) {
					checkmodel = "cascade";
				}else if ("3".equalsIgnoreCase(checkmodel)) {
					checkmodel = "single";
				} else {
					checkmodel = "childCascade";
				}
				
				// 级联多选树的层级
				String level = element.getAttributeValue("level");
				if (level == null || level.length() <= 0) {
					level = "9999";
				} else {
					if (! level.matches("/^[0-9]*$/")) {
						level = "9999";
					} 
				}
				
				if (iscascade != null && "true".equalsIgnoreCase(iscascade)) {
					htmlBuff
							.append("javascript:openCodeCustomReportCascadetree(");
					htmlBuff.append("'" + codeSetId + "',");
					htmlBuff.append("'");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("report");
					htmlBuff.append("'");
					htmlBuff.append(",");
					htmlBuff.append("'");
					htmlBuff.append(priv);
					htmlBuff.append("','"+checkmodel+"','"+level+"');\"");
				} else {
					htmlBuff.append("javascript:openCodeCustomReportDialog('");
					htmlBuff.append(codeSetId);
					htmlBuff.append("','");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("report");
					htmlBuff.append("','");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("reporthidden");
					htmlBuff.append("','");
					// 获得控制代码树的属性字符窜
					htmlBuff.append("".equals(getFeatures(element))?"0":getFeatures(element));//是否限制只能选和codesetid相同的代码 =0:是，=1：否;
					htmlBuff.append("');\"");
				}
				htmlBuff.append(" style='vertical-align:middle'/>");
				htmlBuff.append("<input type='hidden' name='");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("' value='");
				htmlBuff.append((String) publicParamMap.get(element
						.getAttributeValue("name")));

				// 是否自动加载事件
				if (isAutoSubmit(element)) {
					htmlBuff
							.append("' onpropertychange='changeValue(event)'/>\r\n");
				} else {
					htmlBuff.append("' />\r\n");
				}

			} else {

				if (childenList != null && childenList.size() > 0) {

					String value = (String) publicParamMap.get(element
							.getAttributeValue("name"));
					htmlBuff.append("&nbsp;&nbsp;&nbsp;");
					if (title != null && title.length() > 0) {
						htmlBuff.append(title);
					}
					
					// 是否使用多选组件
					boolean flag = false;
					String components = element.getAttributeValue("components");
					if (components != null && "true".equalsIgnoreCase(components)){
						flag = true;
					}
					
					if (isMultiple(element) && flag) {
						// 组件下拉列表的宽度
						String width = element.getAttributeValue("width");
						if (width == null || width.length() == 0) {
							width = "200";
						}
						// 组件下拉列表的高度
						String height = element.getAttributeValue("height");
						if (height == null || height.length() == 0) {
							height = "300";
						}
						StringBuffer htmlBuff2 = new StringBuffer();
						StringBuffer lableBuff = new StringBuffer();
						// 添加子项
						for (int i = 0; i < childenList.size(); i++) {
							Element el = (Element) childenList.get(i);
							String value2 = el.getAttributeValue("value");
							htmlBuff2.append("<li class='liclass'>\r\n");
							htmlBuff2.append("<input type='checkbox' name='sel_"+name+"_box_"+i+"' id='sel_"+name+"_box_"+i+"'");
							
							value = "," + value + ",";
							if (value.contains("," + value2 + ",")) {
								htmlBuff2.append(" checked='checked' ");
								lableBuff.append(",");
								lableBuff.append(el.getAttributeValue("name"));
							} 
							htmlBuff2.append(" value='"+value2+"'");
							htmlBuff2.append("/>\r\n");
							htmlBuff2.append(" <label id='sel_"+name+"_box_"+i+"_lable' for='sel_"+name+"_box_"+i+"' title='");
							htmlBuff2.append(el.getAttributeValue("name"));
							htmlBuff2.append("'>");
							htmlBuff2.append(el.getAttributeValue("name"));
							htmlBuff2.append("</label>\r\n");
							htmlBuff2.append("</li>\r\n");
							
						}
						
						//htmlBuff.append("<div style='display:inline; width:150px;'>\r\n");
						htmlBuff.append("<input type='text' class='textColorRead' readonly='readonly' name='"+name+"report' onclick=\"dclick('"+name+"');\" value='");
						if (lableBuff.length() > 0) {
							htmlBuff.append(lableBuff.substring(1, lableBuff.length()));
						}
						htmlBuff.append("' style=' position:relative; left:0px; top:0px; margin:0px;vertical-align:middle;'/>\r\n");
						htmlBuff.append("<img src='/images/downselect.gif' onclick=\"dclick('"+name+"');\" style=' cursor:pointer; left:0px;margin:0px;vertical-align:middle;'/>\r\n");
						htmlBuff.append("<div id='li_"+name+"' style='position:absolute;display:none;left:0px;top:0px;  width:"+width+"px;height:"+height+"px;z-index:999;'>\r\n");
						htmlBuff.append("<iframe name='frame' scrolling='auto' frameborder='0' width='100%' height='"+height+"px' style='position:absolute;z-index:-1;margin:0px;padding:0px;'></iframe>\r\n");
						htmlBuff.append("<ul style='position:absolute;width:100%;margin:0px;padding:0px;z-index:9999;height:"+height+"px;overflow-y:auto;border:#C4D8EE solid 1px;'>\r\n");
						htmlBuff.append("<li class='liclass' style='border-bottom:#cccccc solid 1px;'> <input type='checkbox' name='sel_selectAll"+name+"_box' id='sel_selectAll"+name+"_box' title='全选' value='' onclick=\"selectAll('"+name+"');saveSelValue('"+name+"')\"/>\r\n");
						htmlBuff.append(" <label id='sel_selectAll"+name+"_box_lable' for='sel_selectAll"+name+"_box' title='");
						htmlBuff.append("全选' style='height:16px;'>全选</label><img src='/images/close2.gif' onclick=\"disnone('"+name+"');\" style='position:absolute;right:1px;top:1px;cursor:pointer;margin:0px;'/>");
						htmlBuff.append("</li>\r\n");
						
						htmlBuff.append(htmlBuff2.toString());
						htmlBuff.append("</ul>\r\n");
						htmlBuff.append("</div>\r\n");
						//htmlBuff.append("</div>");
						
						// 隐藏文本域，保存值
						htmlBuff.append("<input type=\"hidden\" name=\"");
						htmlBuff.append(name);
						htmlBuff.append("\" value=\"");
						htmlBuff.append((String) publicParamMap.get(name));

						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							htmlBuff
									.append("\" onpropertychange=\"changeValue(event)\"/>");
						} else {
							htmlBuff.append("\" />");
						}
					} else {
						htmlBuff.append("<select name='");
						htmlBuff.append(element.getAttributeValue("name"));
	
						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							if (isMultiple(element)) {
								String size = element.getAttributeValue("size");
								htmlBuff.append("' size='");
								htmlBuff.append(size);
								htmlBuff.append("' Multiple>\r\n");
							} else {
								htmlBuff.append("' onchange='changeTrans()'>\r\n");
							}
						} else {
							if (isMultiple(element)) {
								String size = element.getAttributeValue("size");
								htmlBuff.append("' size='");
								htmlBuff.append(size);
								htmlBuff.append("' Multiple>\r\n");
							} else {
								htmlBuff.append("' >\r\n");
							}
						}
						// 加载子项
						for (int i = 0; i < childenList.size(); i++) {
							Element el = (Element) childenList.get(i);
							htmlBuff.append("<option value=\"");
							htmlBuff.append(el.getAttributeValue("value"));
							String value2 = el.getAttributeValue("value");
							value = "," + value + ",";
							if (value.contains("," + value2 + ",")) {
								htmlBuff.append("\" selected=\"selected\">");
							} else {
								htmlBuff.append("\">");
							}
							htmlBuff.append(el.getAttributeValue("name"));
							htmlBuff.append("</option>\r\n");
						}
						htmlBuff.append("</select>\r\n");
					}
				} else {
					// 将公共参数显示到页面
					String value = element.getAttributeValue("value");
					if (value == null) {
						value = "";
					}
					htmlBuff.append("&nbsp;&nbsp;&nbsp;");
					htmlBuff.append(element.getAttributeValue("title"));
					htmlBuff.append("&nbsp;");
					htmlBuff.append("<input type='text' name='");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("' value='");
					if (publicParamMap.get(element.getAttributeValue("name")) != null) {
						htmlBuff.append((String) publicParamMap.get(element
								.getAttributeValue("name")));
					} else {
						htmlBuff.append(value);
					}
					htmlBuff.append("'");
					String type = element.getAttributeValue("type");
					// 是否为当前时间
					String iscurrent = element.getAttributeValue("iscurrent");
					// 是否加载时间选择框
					String iscalendar = element.getAttributeValue("iscalendar");
					if (type != null && "d".equalsIgnoreCase(type)) {
						if (iscalendar == null 
								|| iscalendar.length() == 0 
								|| "true".equalsIgnoreCase(iscalendar)) {
							htmlBuff.append(getCalendar (value));
						}

						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							htmlBuff
									.append(" onpropertychange='changeValue(event)'");
						}

					}
					htmlBuff
							.append(" onkeydown='dokeypess(event)' size='10'/>\r\n");
					/*if (type != null && type.equalsIgnoreCase("d")
							&& (iscurrent == null || iscurrent.length() <= 0)) {
						htmlBuff
								.append("<SCRIPT Language='JavaScript'>WebCalendar.format='");
						htmlBuff.append(element.getAttributeValue("value"));
						htmlBuff.append("'</SCRIPT>\r\n");
					}*/
					
					
				}
			}
			// 获得定义的序号
			String indexStr = element.getAttributeValue("index");
			if (indexStr != null && indexStr.length() > 0) {
				index++;
				indexArr[iArr] = indexStr;
				iArr++;
				// 将html字符窜按顺序保存
				indexParaMap.put(indexStr, htmlBuff.toString());
			} else {
				index++;
				indexArr[iArr] = String.valueOf(index);
				iArr++;
				// 将html字符窜按顺序保存
				indexParaMap.put(String.valueOf(index), htmlBuff.toString());
			}
		}

		// 为数组排序
		indexArr = getIndexString(indexArr);
		// 按顺序将html代码保存到buff中
		for (int i = 0; i < indexArr.length; i++) {
			String htmlStr = (String) indexParaMap.get(indexArr[i]);
			if (htmlStr == null) {
				continue;
			} else {
				buff.append(htmlStr);
			}
		}

		buff.append("<input type='hidden' name='hiddenValueName' id='hiddenValueName' />\r\n");
		
		// 外部输入参数
		buff.append("<input type='hidden' name='inputParam' id='inputParam' value='");
		buff.append(getInputParamStr());
		buff.append("'/>\r\n");
		
		// 保存需要验证的title及name，value格式如下: title:name,title2:name2
		buff.append("<input type=\"hidden\" name=\"isNotNullName\" ");
		buff.append("id=\"isNotNullName\" value='");
		if (isNotNullName.length() > 0) {
			buff.append(isNotNullName.substring(1));
		}
		buff.append("'/>");
		
		// 是否添加提交按钮
		String submit = (String) paramSetMap.get("submit");
		if (!(submit != null && "false".equalsIgnoreCase(submit))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type='button' class='mybutton' name='tijiao'");
			String subName = (String) paramSetMap.get("submitname");
			buff.append(" value='");
			
			if (subName == null || subName.length() == 0) {
				buff.append("提交");
			} else {
				buff.append(subName);
			}
			buff.append("' onclick='changeTrans()' />\r\n");
		}

		// 是否添加打印预览按钮
		String printview = (String) paramSetMap.get("printview");
		if (!(printview != null && "false".equalsIgnoreCase(printview))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type='button' class='mybutton' ");
			buff.append("name='printview' value='打印预览' onclick='printpr()' />\r\n");
		}

		// 是否添加打印按钮
		String printall = (String) paramSetMap.get("printall");
		if (!(printall != null && "false".equalsIgnoreCase(printall))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type='button' class='mybutton' name='printall'");
			buff.append(" value='打印' onclick='window.print();' />\r\n");
		}

		buff.append("</form>\r\n");
		buff.append("</div>\r\n");
		// 添加javascript脚本
		buff.append("<script>	\r\n");
		buff.append("	function ss () { \r\n");
		buff.append("		var sele = document.getElementsByTagName('select');\r\n");
		buff.append("		var inp = document.getElementsByTagName('input');\r\n");
		buff.append("		var val = '';\r\n");
		buff.append("		for (i = 0; i < sele.length; i++) {\r\n");
		buff.append("			if (sele[i].multiple == true) {");
		buff.append("				val +=(sele[i].name+\":M,\");\r\n");
		buff.append("			} else {\r\n");
		buff.append("				val +=(sele[i].name+\",\");\r\n");
		buff.append("			}");
		buff.append("		}\r\n");
		buff.append("		for(j = 0; j < inp.length; j++) {\r\n");
		buff.append("  			if (inp[j].type=='text' || inp[j].type=='hidden') {\r\n");
		buff.append("  				val += (inp[j].name + \",\");\r\n");
		buff.append("  			}\r\n");
		buff.append("  		}\r\n");
		buff.append("		var hid = document.getElementById(\"hiddenValueName\");\r\n");
		buff.append("		hid.value = val;\r\n");
		buff.append("	}\r\n");
		buff.append("	ss();\r\n");
		buff.append("	function changeTrans() {if (validata()) {\r\n");
		buff.append("		openwin();} else {return false;}\r\n");
		buff.append("	} \r\n");
		buff.append("	function changeValue(event) {\r\n");
		buff.append("		if(event.propertyName == \"value\"){if (validata()) {\r\n");
		buff.append("			openwin();} else {return false;}\r\n");
		buff.append("		} \r\n");
		buff.append("	} \r\n");
		buff.append("	function dokeypess(event) {\r\n");
		buff.append("		var s=event.keyCode;\r\n");
		buff.append("  		if (s==13){ if (validata()) {\r\n");
		buff.append("	  		openwin();} else {return false;}\r\n");
		buff.append("  		} \r\n");
		buff.append("	}\r\n");
		buff.append("	function browsers(){\r\n");
		buff.append(" 		var l = window.location.href;\r\n");
		buff.append("		window.open(l); \r\n");
		buff.append("	}\r\n");
		// 引用webbrower插件
		buff.append("</script><object id=\"WebBrowser\" width=0 height=0");
		buff.append(" classid=\"CLSID:8856F961-340A-11D0-A96B-00C04FD705A2\"></object>\r\n");
		buff.append("<div id='wait' style='position:absolute;display:none;");
		buff.append("z-index: 999999;width:311;height:88;'>\r\n");
		buff.append("<iframe style=\"border-width: 0px; width: 311; height: 88;\" ");
		buff.append("src=\"/system/options/customreport/scroll.html\" frameborder=\"no\" border=\"0\" >\r\n");
		buff.append("</iframe></div>\r\n");
		
		buff.append("<script language='javascript' type='text/javascript'>");
		buff.append("	var win;\r\n");
		buff.append("	var first_to_page_mark=0;\r\n");
		buff.append("	function printpr(){\r\n");
		buff.append("		var l = window.location.href;setMark();\r\n");
		buff.append(" 		win = window.open(l,'_blank','width='+screen.width+',height='+screen.height);\r\n");
		buff.append(" 		win.moveTo(-10000,-10000);\r\n");
		buff.append("	}");
		buff.append("	function show() {win.printview();}\r\n");
		buff.append("	function printview(){\r\n");
		buff.append("		document.all.WebBrowser.ExecWB(7,1);");
		buff.append("		setTimeout('clo()', 15000);\r\n");
		buff.append("	} function clo() {document.all.WebBrowser.ExecWB(45,1);}\r\n");
		buff.append(" 	function getMark() {return first_to_page_mark;}\r\n");
		buff.append(" 	function setMark() {first_to_page_mark = 1;}\r\n");
		// 多选组件的按钮
		buff.append(getSelectScript());
		
		buff.append("</script>\r\n");

		return buff.toString();
	}
	
	private String getCalendar (String value) {
		String str = "";
		if (value == null) {
			value = "";
		}
		//simpledate 4 yyyy
		//simpledate 6、7 yyyy.MM
		//simpledate 5、8、9、10 yyyy.MM.dd
		//date					yyyy-MM-dd
		//time					hh:mm:ss
		//datetime				yyyy-MM-dd hh:mm:ss
		//默认yyyy-MM-dd
		String dateType = "";
		if (value.indexOf(".") != -1) {
			dateType = "simpledate";
		} else if (value.indexOf("-") != -1) {
			dateType = "date";
		} else {
			dateType = "dateType";
		}
		
		int length = value.length();
		if (length == 6) {
			length = 7;
		} else if (length != 4) {
			length = 10;
		}
		str = " field='gt' extra='editor' itemlength='"+length+"' dataType='"+dateType+"' dropDown='dropDownDate' style='width:100px;height:20px;'";
		return str;
	}

	/**
	 * 没有sql模板时
	 * @return
	 */
	public String getNoSqlHtmlString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<link rel='stylesheet' href='/css/css1.css' type='text/css'>");
		buff.append("<style><!--  .lin{border-bottom:1px groove black} --></style>");
		buff.append("<style media=\"print\"><!-- .noprint{display:none; }; --></style>");
		buff.append("<div id=\"topss\" class=\"noprint lin\" width=\"100%\">");
		buff.append("<form id=\"form1\" name=\"form1\" method=\"post\" onsubmit=\"return false;\"");
		buff.append("action=\"");
		buff.append("\">");

		// 是否添加打印预览按钮
		String printview = (String) paramSetMap.get("printview");
		if (!(printview != null && "false".equalsIgnoreCase(printview))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" ");
			buff.append("name=\"printview\" value=\"打印预览\" onclick=\"printpr()\" />");
		}

		// 是否添加打印按钮
		String printall = (String) paramSetMap.get("printall");
		if (!(printall != null && "false".equalsIgnoreCase(printall))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" ");
			buff.append("name=\"printall\" value=\"打印\" onclick=\"window.print();\" />");
		}

		buff.append("</form><object id=\"WebBrowser\" width=0 height=0");
		buff.append(" classid=\"CLSID:8856F961-340A-11D0-A96B-00C04FD705A2\"></object>");
		buff.append("</div>");
		buff.append("<script language='javascript' type='text/javascript'>var win;\r\n");
		buff.append("	var first_to_page_mark=0;\r\n");
		buff.append("	function printpr(){\r\n");
		buff.append("		var l = window.location.href;setMark();\r\n");
		buff.append(" 		win = window.open(l,'_blank','width='+screen.width+',height='+screen.height);\r\n");
		buff.append(" 		win.moveTo(-10000,-10000);\r\n");
		buff.append("	} function show() {win.printview();}\r\n");
		buff.append("	function printview(){\r\n");
		buff.append("		document.all.WebBrowser.ExecWB(7,1);setTimeout('clo()', 15000);\r\n");
		buff.append("	} function clo() {document.all.WebBrowser.ExecWB(45,1);}\r\n");
		buff.append(" 	function getMark() {return first_to_page_mark;}\r\n");
		buff.append(" 	function setMark() {first_to_page_mark = 1;}\r\n");
		buff.append("</script>");
		return buff.toString();
	}
	
	private String getSelectScript() {
		StringBuffer buff = new StringBuffer();
		buff.append("function dclick (namestr) {\r\n");
		buff.append("	var li = document.getElementById('li_'+namestr);\r\n");
		buff.append("	var re = document.getElementById(namestr+'report');");
		buff.append("	if (li.style.display == 'none') {\r\n");
		buff.append("		li.style.display = '';\r\n");
		buff.append("		li.style.left = re.offsetLeft - 4;");
		buff.append("		li.style.top = re.offsetTop + re.offsetHeight - 1;");
		
		buff.append("	} else {\r\n");
		buff.append("		li.style.display = 'none';\r\n");
		buff.append("		saveSelValue(namestr);\r\n");
		buff.append("	}\r\n");		
		buff.append("}\r\n");
		buff.append("function disnone (namestr) {\r\n");
		buff.append("	var li = document.getElementById('li_'+namestr);\r\n");
		buff.append("	li.style.display = 'none';\r\n");
		buff.append("	saveSelValue(namestr);");
		buff.append("}\r\n");
		buff.append("function selectAll(namestr) {\r\n");
		buff.append("	var box = document.getElementById('sel_selectAll'+namestr+'_box');\r\n");
		buff.append("	if (box.checked) {\r\n");
		buff.append("		doCheckbox(true, namestr);\r\n");
		buff.append("	} else {\r\n");
		buff.append("		doCheckbox(false, namestr);\r\n");
		buff.append("	}\r\n");
		buff.append("}\r\n");

		buff.append("function doCheckbox (types, namestr) {\r\n");
		buff.append("	var boxes = document.getElementsByTagName('input');\r\n");
		buff.append("	for (i = 0; i < boxes.length; i++) {\r\n");
		buff.append("		if (boxes[i].type == 'checkbox') {\r\n");
		buff.append("			var boxName = boxes[i].name;\r\n");
		buff.append("			var boxNa = boxName.substring(4,boxName.length);\r\n");
		buff.append("			var index = boxNa.indexOf('_');\r\n");
		buff.append("			if (boxName.substring(0,4) == 'sel_' && boxNa.substring(0,index) == namestr) {\r\n");
		buff.append("				boxes[i].checked = types;\r\n");
		buff.append("			}\r\n");
		buff.append("		}\r\n");
		buff.append("	}\r\n");
		buff.append("}\r\n");
		
		buff.append("function saveSelValue(namestr) {\r\n");
		buff.append("		var boxes = document.getElementsByTagName('input');\r\n");
		buff.append("		var value = '';\r\n");
		buff.append("		var lable = '';\r\n");
		buff.append("		for (i = 0; i < boxes.length; i++) {\r\n");
		buff.append("			if (boxes[i].type == 'checkbox') {\r\n");
		buff.append("				var boxName = boxes[i].name;\r\n");
		buff.append("				var boxNa = boxName.substring(4,boxName.length);\r\n");
		buff.append("				var index = boxNa.indexOf('_');\r\n");
		buff.append("				if (boxName.substring(0,4) == 'sel_' && boxNa.substring(0,index) == namestr) {\r\n");
		buff.append("					if (boxes[i].checked) {\r\n");
		buff.append("						value +=',' + boxes[i].value;\r\n");
		buff.append("						var obj = document.getElementById(boxes[i].id +'_lable');\r\n");
		buff.append("						lable += ','+obj.innerText;\r\n");
		buff.append("					}\r\n");
		buff.append("				}\r\n");
		buff.append("			}\r\n");
		buff.append("		}\r\n");
		buff.append("		var valueObj = document.getElementsByName(namestr)[0];\r\n");
		buff.append("		if (valueObj.value != value.substring(1,value.length)){\r\n");
		buff.append("			valueObj.value = value.substring(1,value.length);\r\n");
		buff.append("		}\r\n");
		buff.append("		var lableObj = document.getElementsByName(namestr+'report')[0];\r\n");
		buff.append("		if (lableObj.value != lable.substring(1,lable.length)) {\r\n");
		buff.append("			lableObj.value = lable.substring(1,lable.length);\r\n");
		buff.append("		}\r\n");
		buff.append("	}\r\n");

		buff.append("	function changeSaveSelValue (obj,namestr) {\r\n");
		buff.append("		var valueObj = document.getElementsByName(namestr)[0];\r\n");
		buff.append("		var value = ','+valueObj.value+',';\r\n");
		buff.append("		if (obj.checked) {\r\n");
		buff.append("			if (value.indexOf(','+obj.value+',') == -1) {\r\n");
		buff.append("				valueObj.value = value.substring(1,value.length - 1) + ',' + obj.value;\r\n");
		buff.append("			}\r\n");
		buff.append("		} else {\r\n");
		buff.append("			var index = value.indexOf(','+obj.value+',');\r\n");
		buff.append("			if ( index != -1) {\r\n");
		buff.append("				var subValue = value.substring(1,index);\r\n");
		buff.append("				var subValue2 = value.substring(index + obj.value.length + 1,value.length - 1);\r\n");
		buff.append("				valueObj.value = subValue + subValue2;\r\n");
		buff.append("			}\r\n");
		buff.append("		}\r\n");
		buff.append("	}\r\n");
		
		// 验证非空
		buff.append("	function validata() {\r\n");
		buff.append("		var namesObj = document.getElementById('isNotNullName');\r\n");
		buff.append("		var names;if(namesObj){names=namesObj.value;} else {names='';}");
		buff.append("		if (names.length > 0) {\r\n");
		buff.append("			var nameArr = names.split(',');\r\n");
		buff.append("			var mark = 0;\r\n");
		buff.append("			for (i = 0; i < nameArr.length; i++) {\r\n");
		buff.append("				var titleNameArr = nameArr[i].split(':');\r\n");
		buff.append("				var title = titleNameArr[0];\r\n");
		buff.append("				var name = titleNameArr[1];\r\n");
		buff.append("				var obj = document.getElementsByName(name);\r\n");
		buff.append("				var value = obj[0].value;\r\n");
		buff.append("				if (value.length == 0) {\r\n");
		buff.append("					alert(title + '不能为空!');\r\n");
		buff.append("					mark = 1;\r\n");
		buff.append("					break;\r\n");
		buff.append("				}\r\n");
		buff.append("			}\r\n");
		buff.append("			if (mark == 0) {\r\n");
		buff.append("				return true;\r\n");
		buff.append("			} else {\r\n");
		buff.append("				return false;\r\n");
		buff.append("			}\r\n");
		buff.append("			\r\n");
		buff.append("		} else {\r\n");
		buff.append("			return true;\r\n");
		buff.append("		}\r\n");
		buff.append("	}\r\n");
		return buff.toString();
	}
	/**
	 * 将列表值转换成html代码
	 * 
	 * @return
	 */
	public String getExcelHtmlString(String tabid) {
		
		// 将转化后的值再保存到公共参数中
		Iterator its = this.transOldMap.entrySet().iterator();
		while(its.hasNext()) {
			Map.Entry entry = (Map.Entry) its.next();
			this.publicParamMap.put(entry.getKey().toString(), entry.getValue());
		}
		
		StringBuffer buff = new StringBuffer();
		// 多选控件的css样式
		buff.append("<style>");
		buff.append(".liclass {");
		buff.append("margin:0px;");
		buff.append("padding:0px;");
		buff.append("height:20px;");
		buff.append("text-align:left;");
		buff.append("font-size:12px;");	
		buff.append("text-overflow: ellipsis;");		
		buff.append("overflow-x:visible;}");
		buff.append("</style>");
		
		buff.append(" <link rel='stylesheet' href='/css/css1.css' type='text/css'>\r\n");
		buff.append("<div id=\"div\" class=\"noprint lin\" style='position:relative;'><form id=\"form1\" name=\"form1\" ");
		buff.append("action=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id=");
		buff.append(tabid);
		buff.append("\" method=\"post\" onsubmit=\"return false;\">");
		buff.append("<input type=\"hidden\" id=\"customid\" name=\"customid\" value=\"");
		buff.append(tabid);
		buff.append("\"/>");
		buff.append("<input type=\"hidden\" id=\"html\" name=\"html\" value=\"\"/>");
		//添加table，为报表关联模块报表管理报表输出页面顶部元素添加间距  jingq add 2014.07.21
		buff.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"5px;\">");
		buff.append("<tr height=\"35px;\">");
		// 将参数转化后的html代码放入map中，然后进行排序
		HashMap indexParaMap = new HashMap();
		// 没有配置顺序的html代码放进list中，输出到最后
		ArrayList paramList = new ArrayList();
		// 顺序号
		int index = 0;
		// 数组角标
		int iArr = 0;
		// 保存index的数组
		String[] indexArr = new String[publicElementMap.size()];
		// 不能为空的参数名称
		StringBuffer isNotNullName = new StringBuffer();
		for (Iterator it = publicElementMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			Element element = (Element) e.getValue();
			String isshow = element.getAttributeValue("isshow");
			StringBuffer htmlBuff = new StringBuffer();
			if (isshow != null && "false".equalsIgnoreCase(isshow)) {
				continue;
			}
			List childenList = element.getChildren();
			String codeSetId = element.getAttributeValue("codesetid");
			// 参数描述
			String title = element.getAttributeValue("title");
			// 参数名称
			String name = element.getAttributeValue("name");
			// 是否允许为空
			String isNull = element.getAttributeValue("isnull");
			
			if ("false".equalsIgnoreCase(isNull)) {
				isNotNullName.append(",");
				isNotNullName.append(title);
				isNotNullName.append(":");
				isNotNullName.append(name);
			}
			// 定义为列表
			if (codeSetId != null && codeSetId.length() > 0) {
				// 根据codesetid获得列表
				ArrayList list = getCodeList(codeSetId);
				String value = (String) publicParamMap.get(element
						.getAttributeValue("name"));
				htmlBuff.append("<td>");
				if (title != null && title.length() > 0) {
					htmlBuff.append(title);
				}
				htmlBuff.append("</td>");
				htmlBuff.append("<td>");
				htmlBuff
						.append("<input type=\"text\" class=\"textColorRead\" id=\"");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("report");
				htmlBuff.append("\" name=\"");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("report");
				htmlBuff.append("\" value=\"");
				htmlBuff.append(getDesc(codeSetId, (String) publicParamMap
						.get(element.getAttributeValue("name"))));
				htmlBuff
						.append("\" style=\"vertical-align:middle\" size=\"10\"/>");
				/**************update by xiegh on 20171128 bug:32907***********************/
				htmlBuff
						.append("<input type=\"hidden\"  ");
				htmlBuff.append(" name=\"");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("reporthidden\" ");
				htmlBuff.append(" value=\""+(String) publicParamMap
						.get(element.getAttributeValue("name"))+"\"/>");
				/*************************************/
				htmlBuff.append("</td>");
				htmlBuff.append("<td>");
				htmlBuff.append("<img  src=\"/images/code.gif\" ");
				htmlBuff.append("onclick=\"");
				// 判断是否为级联树
				String iscascade = element.getAttributeValue("is_cascade_tree");
				// 是否要加载权限 0为不加载权限，1为加载权限（根据管理权限）
				String priv = element.getAttributeValue("priv");
				if (priv == null || priv.length() == 0) {
					priv = "0";
				}
				// 是否是多选
				String checkmodel = element.getAttributeValue("checkmodel");
				if (checkmodel == null || checkmodel.length() == 0 || "0".equalsIgnoreCase(checkmodel)) {
					checkmodel = "childCascade";
				} else if ("1".equalsIgnoreCase(checkmodel)) {
					checkmodel = "parentCascade";
				} else if ("2".equalsIgnoreCase(checkmodel)) {
					checkmodel = "cascade";
				}else if ("3".equalsIgnoreCase(checkmodel)) {
					checkmodel = "single";
				} else {
					checkmodel = "childCascade";
				}
				
				// 级联多选树的层级
				String level = element.getAttributeValue("level");
				if (level == null || level.length() <= 0) {
					level = "9999";
				} else {
					if (! level.matches("[0-9]*")) {
						level = "9999";
					} 
				}
				
				if (iscascade != null && "true".equalsIgnoreCase(iscascade)) {
					htmlBuff
							.append("javascript:openCodeCustomReportCascadetree(");
					htmlBuff.append("'");
					htmlBuff.append(codeSetId);
					htmlBuff.append("',");
					htmlBuff.append("'");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("report");
					htmlBuff.append("'");
					htmlBuff.append(",'");
					htmlBuff.append(priv);
					htmlBuff.append("','"+checkmodel+"','"+level+"');\"");
				} else {
					htmlBuff.append("javascript:openCodeCustomReportDialog('");
					htmlBuff.append(codeSetId);
					htmlBuff.append("','");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("report");
					htmlBuff.append("','");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("reporthidden");
					htmlBuff.append("','");
					// 获得控制代码树的属性字符窜
					htmlBuff.append("".equals(getFeatures(element))?"0":getFeatures(element));//是否限制只能选和codesetid相同的代码 =0:是，=1：否;
					htmlBuff.append("');\"");
				}
				htmlBuff.append(" style=\"vertical-align:middle\"/>");
				htmlBuff.append("<input type=\"hidden\" name=\"");
				htmlBuff.append(element.getAttributeValue("name"));
				htmlBuff.append("\" value=\"");
				htmlBuff.append((String) publicParamMap.get(element
						.getAttributeValue("name")));

				// 是否自动加载事件
				if (isAutoSubmit(element)) {
					htmlBuff
							.append("\" onpropertychange=\"changeValue(event)\"/>");
				} else {
					htmlBuff.append("\" />");
				}
				htmlBuff.append("</td>");
			} else {

				if (childenList != null && childenList.size() > 0) {

					String value = (String) publicParamMap.get(element
							.getAttributeValue("name"));
					htmlBuff.append("<td>");
					if (title != null && title.length() > 0) {
						htmlBuff.append(title);
					}
					htmlBuff.append("</td>");
					// 是否使用多选组件
					boolean flag = false;
					String components = element.getAttributeValue("components");
					if (components != null && "true".equalsIgnoreCase(components)){
						flag = true;
					}
					
					if (isMultiple(element) && flag) {
						StringBuffer htmlBuff2 = new StringBuffer();
						StringBuffer lableBuff = new StringBuffer();
						// 组件下拉列表的宽度
						String width = element.getAttributeValue("width");
						if (width == null || width.length() == 0) {
							width = "200";
						}
						// 组件下拉列表的高度
						String height = element.getAttributeValue("height");
						if (height == null || height.length() == 0) {
							height = "300";
						}
						// 添加子项
						for (int i = 0; i < childenList.size(); i++) {
							Element el = (Element) childenList.get(i);
							String value2 = el.getAttributeValue("value");
							htmlBuff2.append("<li class='liclass'>\r\n");
							htmlBuff2.append("<input type='checkbox' name='sel_"+name+"_box_"+i+"' id='sel_"+name+"_box_"+i+"'");
							
							value = "," + value + ",";
							if (value.contains("," + value2 + ",")) {
								htmlBuff2.append(" checked='checked' ");
								lableBuff.append(",");
								lableBuff.append(el.getAttributeValue("name"));
							} 
							htmlBuff2.append(" value='"+value2+"'");
							htmlBuff2.append("/>\r\n");
							htmlBuff2.append(" <label id='sel_"+name+"_box_"+i+"_lable' for='sel_"+name+"_box_"+i+"' title='");
							htmlBuff2.append(el.getAttributeValue("name"));
							htmlBuff2.append("'>");
							htmlBuff2.append(el.getAttributeValue("name"));
							htmlBuff2.append("</label>\r\n");
							htmlBuff2.append("</li>\r\n");
							
						}
						
						//htmlBuff.append("<div style='display:inline; width:150px;'>\r\n");
						/*【46925】少个td，导致input显示位置不对 guodd 2019-04-26*/
						htmlBuff.append("<td><input type='text' readonly='readonly' class='textColorRead' name='"+name+"report' onclick=\"dclick('"+name+"');\" value='");
						if (lableBuff.length() > 0) {
							htmlBuff.append(lableBuff.substring(1, lableBuff.length()));
						}
						htmlBuff.append("' style=' position:relative; left:0px; top:0px; margin:0px;vertical-align:middle;'/>\r\n");
						htmlBuff.append("<img src='/images/downselect.gif' onclick=\"dclick('"+name+"');\" style=' cursor:pointer; left:0px;margin:0px;vertical-align:middle;'/>\r\n");
						htmlBuff.append("<div id='li_"+name+"' style='position:absolute;display:none;left:0px;top:0px;  width:"+width+"px;height:"+height+"px;z-index:999;'>\r\n");
						htmlBuff.append("<iframe name='frame' scrolling='auto' frameborder='0' width='100%' height='"+height+"px' style='position:absolute;z-index:-1;margin:0px;padding:0px;'></iframe>\r\n");
						htmlBuff.append("<ul style='position:absolute;width:100%;margin:0px;padding:0px;z-index:9999;height:"+height+"px;overflow-y:auto;border:#C4D8EE solid 1px;'>\r\n");
						htmlBuff.append("<li class='liclass' style='position:relative;border-bottom:#cccccc solid 1px;height:16px;'> <input type='checkbox' name='sel_selectAll"+name+"_box' id='sel_selectAll"+name+"_box' title='全选' value='' onclick=\"selectAll('"+name+"');saveSelValue('"+name+"')\"/>\r\n");
						htmlBuff.append(" <label id='sel_selectAll"+name+"_box_lable' for='sel_selectAll"+name+"_box' title='");
						htmlBuff.append("全选' style='height:16px;'>全选</label><img src='/images/close2.gif' onclick=\"disnone('"+name+"');\" style='position:absolute;right:1px;top:1px;cursor:pointer;margin:0px;'/>");
						htmlBuff.append("</li>\r\n");
						
						htmlBuff.append(htmlBuff2.toString());
						htmlBuff.append("</ul>\r\n");
						htmlBuff.append("</div>\r\n");
						//htmlBuff.append("</div>");
						
						// 隐藏文本域，保存值
						htmlBuff.append("<input type=\"hidden\" name=\"");
						htmlBuff.append(name);
						htmlBuff.append("\" value=\"");
						htmlBuff.append((String) publicParamMap.get(name));

						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							htmlBuff
									.append("\" onpropertychange=\"changeValue(event)\"/>");
						} else {
							htmlBuff.append("\" />");
						}
						htmlBuff.append("</td>");
					} else {
						htmlBuff.append("<td padding-left:5px;valign=\"middle\">");
						htmlBuff.append("<select name=\"");
						htmlBuff.append(element.getAttributeValue("name"));
	
						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							if (isMultiple(element)) {
								String size = element.getAttributeValue("size");
								htmlBuff.append("\" size='");
								htmlBuff.append(size);
								htmlBuff.append("' Multiple>\r\n");
							} else {
								htmlBuff.append("\" onchange=\"changeTrans()\">");
							}
						} else {
							if (isMultiple(element)) {
								String size = element.getAttributeValue("size");
								htmlBuff.append("\" size='");
								htmlBuff.append(size);
								htmlBuff.append("' Multiple>\r\n");
							} else {
								htmlBuff.append("\" >");
							}
						}
						// 加载子项
						for (int i = 0; i < childenList.size(); i++) {
							Element el = (Element) childenList.get(i);
							htmlBuff.append("<option value=\"");
							htmlBuff.append(el.getAttributeValue("value"));
							String value2 = el.getAttributeValue("value");
							value = "," + value + ",";
							if (value.contains("," + value2 + ",")) {
								htmlBuff.append("\" selected=\"selected\">");
							} else {
								htmlBuff.append("\">");
							}
							htmlBuff.append(el.getAttributeValue("name"));
							htmlBuff.append("</option>");
						}
						htmlBuff.append("</select>");
						htmlBuff.append("</td>");
					}
				} else {
					// 将公共参数显示到页面
					String value = element.getAttributeValue("value");
					if (value == null) {
						value = "";
					}
					htmlBuff.append("<td>");
					htmlBuff.append("&nbsp;&nbsp;&nbsp;");
					htmlBuff.append(title);
					htmlBuff.append("&nbsp;");
					htmlBuff.append("<input type=\"text\" name=\"");
					htmlBuff.append(element.getAttributeValue("name"));
					htmlBuff.append("\" value=\"");
					if (publicParamMap.get(element.getAttributeValue("name")) != null) {
						htmlBuff.append((String) publicParamMap.get(element
								.getAttributeValue("name")));
					} else {
						htmlBuff.append(value);
					}
					htmlBuff.append("\" ");
					String type = element.getAttributeValue("type");
					String iscurrent = element.getAttributeValue("iscurrent");
					String iscalendar = element.getAttributeValue("iscalendar");
					if (type != null && "d".equalsIgnoreCase(type)) {
						if (iscalendar == null 
								|| iscalendar.length() == 0 
								|| "true".equalsIgnoreCase(iscalendar)) {
//							htmlBuff.append(" onclick=\"calendar()\"");
							htmlBuff.append(getCalendar(value));
						} 

						// 是否自动加载事件
						if (isAutoSubmit(element)) {
							htmlBuff
									.append(" onpropertychange=\"changeValue(event)\"");
						}

					}
					htmlBuff
							.append("  size=\"10\"/>");
					/*if (type != null && type.equalsIgnoreCase("d")
							&& (iscurrent == null || iscurrent.length() <= 0)) {

						htmlBuff
								.append("<SCRIPT Language=\"JavaScript\">WebCalendar.format='");
						htmlBuff.append(element.getAttributeValue("value"));
						htmlBuff.append("'</SCRIPT>");
					}*/

				}
			}

			// 获得定义的序号
			String indexStr = element.getAttributeValue("index");
			if (indexStr != null && indexStr.length() > 0) {
				index++;
				indexArr[iArr] = indexStr;
				iArr++;
				// 将html字符窜按顺序保存
				indexParaMap.put(indexStr, htmlBuff.toString());
			} else {
				/*
				index++;
				indexArr[iArr] = String.valueOf(index);
				iArr++;
				// 将html字符窜按顺序保存
				indexParaMap.put(String.valueOf(index), htmlBuff.toString());
				 */
				paramList.add(htmlBuff.toString());
			}

		}

		// 为数组排序
		indexArr = getIndexString(indexArr);
		// 按顺序将html代码保存到buff中
		for (int i = 0; i < indexArr.length; i++) {
			String htmlStr = (String) indexParaMap.get(indexArr[i]);
			if (htmlStr == null) {
				continue;
			} else {
				buff.append(htmlStr);
			}
		}
		/*没有设置顺序的排在最后*/
		for (int i = 0; i < paramList.size(); i++) {
				buff.append(paramList.get(i));
		}
		buff.append("<td>");
		// 保存参数的名称
		buff.append("<input type=\"hidden\" name=\"hiddenValueName\" ");
		buff.append("id=\"hiddenValueName\" />");
		
		// 外部输入参数
		buff.append("<input type='hidden' name='inputParam' id='inputParam' value='");
		buff.append(getInputParamStr());
		buff.append("'/>\r\n");
		
		// 保存需要验证的title及name，value格式如下: title:name,title2:name2
		buff.append("<input type=\"hidden\" name=\"isNotNullName\" ");
		buff.append("id=\"isNotNullName\" value='");
		if (isNotNullName.length() > 0) {
			buff.append(isNotNullName.substring(1));
		}
		buff.append("'/>");
		
		// 是否添加提交按钮
		String submit = (String) paramSetMap.get("submit");
		if (!(submit != null && "false".equalsIgnoreCase(submit))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" name=\"tijiao\"");
			String subName = (String) paramSetMap.get("submitname");
			buff.append(" value=\"");
			if (subName == null || subName.length() == 0) {
				buff.append("提交");
			} else {
				buff.append(subName);
			}
			
			buff.append("\" onclick=\"changeTrans()\" />");
		}

		// 是否添加页面设置按钮
		String pageset = (String) paramSetMap.get("pageset");
		if (pageset != null && "true".equalsIgnoreCase(pageset)) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" ");
			buff.append("name=\"pageset\" value=\"页面设置\" onclick=\"excelbutton('1')\" />");
		}
		
//		// 是否添加打印预览按钮
//		String excelprintview = (String) paramSetMap.get("excelprintview");
//		if (excelprintview != null && excelprintview.equalsIgnoreCase("true")) {
//			buff.append("&nbsp;&nbsp;");
//			buff.append("<input type=\"button\" class=\"mybutton\" ");
//			buff.append("name=\"excelprintview\" value=\"打印预览\" onclick=\"excelbutton('2')\" />");
//		}
		
		// 是否添加打印按钮
		String excelprint = (String) paramSetMap.get("excelprint");
		if (excelprint != null && "true".equalsIgnoreCase(excelprint)) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" ");
			buff.append("name=\"excelprint\" value=\"打印\" onclick=\"excelbutton('3')\" />");
		}
		
		// 是否添加下载按钮
		String download = (String) paramSetMap.get("download");
		if (!(download != null && "false".equalsIgnoreCase(download))) {
			buff.append("&nbsp;&nbsp;");
			buff.append("<input type=\"button\" class=\"mybutton\" ");
			String downName = (String) paramSetMap.get("downloadname");
			buff.append("name=\"download\" value=\"");
			if (downName == null || downName.length() == 0) {
				buff.append("下载");
			} else {
				buff.append(downName);
			}
			
			buff.append("\" onclick=\"downLoadExcel()\" />");
		}
		
		buff.append("</td>");
		buff.append("</tr></table>");
		buff.append("</form>");
		buff.append("</div>");
		// 添加javascript脚本
		buff.append("<script>");
		buff.append("	function ss () {\r\n");
		buff.append("var sele = document.getElementsByTagName(\"select\");\r\n");
		buff.append("var inp = document.getElementsByTagName(\"input\");\r\n");
		buff.append("var val = \"\";");
		buff.append("for (i = 0; i < sele.length; i++) {\r\n");
		buff.append("	if (sele[i].multiple == true) {\r\n");
		buff.append("	val +=(sele[i].name+\":M,\");\r\n");
		buff.append("} else {\r\n");
		buff.append("	val +=(sele[i].name+\",\");\r\n");
		buff.append("}\r\n");
		buff.append("}\r\n");
		buff.append("for(j = 0; j < inp.length; j++) {\r\n");
		buff
				.append("  	if (inp[j].type==\"text\" || inp[j].type==\"hidden\") {\r\n");
		buff.append("  		val += (inp[j].name + \",\");\r\n");
		buff.append("  	}\r\n");
		buff.append("  }\r\n");
		buff.append("var hid = document.getElementById(\"hiddenValueName\");\r\n");
		buff.append("hid.value = val;\r\n");
		buff.append("}\r\n");
		buff.append("ss();\r\n");
		buff.append("function changeTrans() { if (validata()) {\r\n");
		buff.append("openwin();} else {return false;}\r\n");
		buff.append("} ");
		buff.append("function changeValue(event) {\r\n");
		buff.append("if(event.propertyName == \"value\"){if (validata()) {\r\n");
		buff.append("openwin();} else {return false;}\r\n");
		buff.append("} \r\n");
		buff.append("} \r\n");
		buff.append("function dokeypess(event) {\r\n");
		buff.append("var s=event.keyCode;\r\n");
		buff.append("  if (s==13){if (validata()) {\r\n");
		buff.append("	  openwin();} else {return false;}\r\n");
		buff.append("  } \r\n");
		buff.append("}\r\n");
		buff.append("function excelbutton(flag) {\r\n ");
		buff.append("	//fram.window.document.getElementById('oframe').Save('c:/1',true);\r\n");
		buff.append("	if (flag == \"1\") {// 页面设置\r\n");
		buff.append("		fram.window.pageset();\r\n");
		buff.append("	} else if (flag == \"2\") {// 打印预览\r\n");
		buff.append("		fram.window.document.all.OA1.PrintPreview();\r\n");
		buff.append("var pagesetobj = document.getElementsByName('pageset')[0];\r\n");
		buff.append("if (pagesetobj) {\r\n");
		buff.append("	pagesetobj.disabled= true;\r\n");
		buff.append("}\r\n");
		buff.append("var excelprintobj = document.getElementsByName('excelprint')[0];\r\n");
		buff.append("if (excelprintobj) {\r\n");
		buff.append("	excelprintobj.disabled= true;\r\n");
		buff.append("}\r\n");
		buff.append("	} else if (flag == \"3\") {// 打印\r\n");
		buff.append(		"fram.window.document.all.OA1.PrintDialog();\r\n");
		buff.append("	}\r\n");
		buff.append("}\r\n");
		buff.append(getSelectScript());
		buff.append(getScript());
		buff.append("</script>");
		return buff.toString();
	}

	private String getScript() {
		StringBuffer buff = new StringBuffer();
		buff.append("function getScript(){\r\n");
		
		// 是否隐藏鼠标右键
		String right = (String) paramSetMap.get("mouseright");
		if (right != null && "false".equalsIgnoreCase(right)) {
			buff.append("var oj = fram.window.document.all.oframe.GetDocumentObject();\r\n");
			buff.append("oj.Application.CommandBars('cell').Enabled = false;\r\n");
		}
		// 是否设置保护
		String protect = (String) paramSetMap.get("protect");
		if (protect != null && !"-1".equalsIgnoreCase(protect)) {
			buff.append("fram.window.document.all.oframe.ProtectDoc(1, 2, '"+protect+"');\r\n");
		}
		
		buff.append("}\r\n");
		return buff.toString();
	}
	/**
	 * 为数组排序，按从小到大的顺序
	 * 
	 * @param str
	 *            数组
	 * @return 排序后的数组
	 */
	private String[] getIndexString(String[] str) {
		String[] index = new String[str.length];
		for (int i = 0; i < str.length - 1; i++) {
			for (int j = 0; j < str.length - i - 1; j++) {
				if (str[j] == null) {
					str[j] = "";
				}
				if (str[j + 1] == null) {
					str[j + 1] = "";
				} 
				if (str[j].compareTo(str[j + 1]) > 0) {
					String tem = str[j];
					str[j] = str[j + 1];
					str[j + 1] = tem;
				}
			}
		}

		return str;
	}

	/**
	 * 获得控制代码树的属性字符窜
	 * 
	 * @param element
	 * @return
	 */
	private String getFeatures(Element element) {

		// 控制代码树的属性
		String features = element.getAttributeValue("features");
		StringBuffer fea = new StringBuffer();
		if (features != null && features.trim().length() > 0) {
			String featur[] = features.split(",");
			for (int i = 0; i < featur.length; i++) {
				fea.append("Global.");
				fea.append(featur[i]);
				fea.append(";");
			}
		} else {
			fea.append("");
		}

		return fea.toString();
	}

	/**
	 * 内容改变是否自动提交表单，默认自动提交
	 * 
	 * @param element
	 * @return
	 */
	private boolean isAutoSubmit(Element element) {

		// 是否自动提交
		boolean isAuto = true;
		String features = element.getAttributeValue("isautosubmit");
		StringBuffer fea = new StringBuffer();
		if (features != null && features.trim().length() > 0
				&& "false".equalsIgnoreCase(features)) {
			isAuto = false;
		}

		return isAuto;
	}

	private boolean isMultiple(Element element) {
		// 是否自动提交
		boolean isAuto = false;
		String features = element.getAttributeValue("multiple");
		if (features != null && features.trim().length() > 0
				&& "true".equalsIgnoreCase(features)) {
			isAuto = true;
		}

		return isAuto;

	}

	public String getExcelHtmlString2(String tabid) {
		HashMap map = new HashMap();
		StringBuffer buff = new StringBuffer();
		buff.append("<div id=\"div\" class=\"noprint lin\">");
		buff.append("<form id=\"form1\" name=\"form1\" method=\"post\" onsubmit=\"return false;\"");
		buff.append("action=\"/system/options/customreport/displaycustomreportservlet?ispriv=1&id=");
		buff.append(tabid);
		buff.append("\">");
		buff.append("<input type=\"hidden\" id=\"customid\" name=\"customid\" value=\"");
		buff.append(tabid);
		buff.append("\"/>");
		for (Iterator it = publicElementMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			Element element = (Element) e.getValue();
			String isshow = element.getAttributeValue("isshow");
			if (isshow != null && "false".equalsIgnoreCase(isshow)) {
				continue;
			}
			List childenList = element.getChildren();
			String codeSetId = element.getAttributeValue("codesetid");

			// 定义为列表
			if (codeSetId != null && codeSetId.length() > 0) {
				// 根据codesetid获得列表
				ArrayList list = getCodeList(codeSetId);
				String title = element.getAttributeValue("title");
				String value = (String) publicParamMap.get(element
						.getAttributeValue("name"));
				;
				buff.append("&nbsp;&nbsp;&nbsp;");
				if (title != null && title.length() > 0) {
					buff.append(title);
				}
				buff
						.append("<input type=\"text\" class=\"textColorRead\" id=\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("report");
				buff.append("\" name=\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("report");
				buff.append("\" value=\"");
				buff.append(getDesc(codeSetId, (String) publicParamMap
						.get(element.getAttributeValue("name"))));
				buff.append("\" style=\"vertical-align:middle\" size=\"10\"/>");
				/**************update by xiegh on 20171128 bug:32907***********************/
				buff
						.append("<input type=\"hidden\" class=\"textColorRead\" ");
				buff.append(" name=\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("reporthidden\"  />\r\n");
				/*************************************/
				buff.append("<img  src=\"/images/code.gif\" ");
				buff.append("onclick='");
				buff.append("javascript:openCodeCustomReportDialog(\"");
				buff.append(codeSetId);
				buff.append("\",\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("report");
				buff.append("\",\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("reporthidden");
				buff.append("\",\"");
				// 获得控制代码树的属性字符窜
				buff.append("".equals(getFeatures(element))?"0":getFeatures(element));//是否限制只能选和codesetid相同的代码 =0:是，=1：否;
				buff.append("\");'");
				buff.append(" style=\"vertical-align:middle\"/>");
				buff.append("<input type=\"hidden\" name=\"");
				buff.append(element.getAttributeValue("name"));
				buff.append("\" value=\"");
				buff.append((String) publicParamMap.get(element
						.getAttributeValue("name")));
				buff.append("\" onpropertychange=\"changeValue(event)\"/>");

			} else {

				if (childenList != null && childenList.size() > 0) {

					String title = element.getAttributeValue("title");
					String value = (String) publicParamMap.get(element
							.getAttributeValue("name"));
					buff.append("&nbsp;&nbsp;&nbsp;");
					if (title != null && title.length() > 0) {
						buff.append(title);
					}
					buff.append("<select name=\"");
					buff.append(element.getAttributeValue("name"));
					buff.append("\" onchange=\"changeTrans()\">");
					// 加载子项
					for (int i = 0; i < childenList.size(); i++) {
						Element el = (Element) childenList.get(i);
						buff.append("<option value=\"");
						buff.append(el.getAttributeValue("value"));
						String value2 = el.getAttributeValue("value");
						if (value2.equals(value)) {
							buff.append("\" selected=\"selected\">");
						} else {
							buff.append("\">");
						}
						buff.append(el.getAttributeValue("name"));
						buff.append("</option>");
					}
					buff.append("</select>");
				} else {
					// 将公共参数显示到页面
					buff.append("&nbsp;&nbsp;&nbsp;");
					buff.append(element.getAttributeValue("title"));
					buff.append("&nbsp;");
					buff.append("<input class='' type=\"text\" name=\"");
					buff.append(element.getAttributeValue("name"));
					buff.append("\" value=\"");
					if (publicParamMap.get(element.getAttributeValue("name")) != null) {
						buff.append((String) publicParamMap.get(element
								.getAttributeValue("name")));
					} else {
						buff.append(element.getAttributeValue("value"));
					}
					buff.append("\" ");
					String type = element.getAttributeValue("type");
					String iscurrent = element.getAttributeValue("iscurrent");
					if (type != null && "d".equalsIgnoreCase(type)
							&& (iscurrent == null || iscurrent.length() <= 0)) {
						buff.append(" onclick=\"calendar()\"");
						buff.append("onpropertychange=\"changeValue(event)\"");

					}
					buff
							.append(" onkeydown=\"dokeypess(event)\" size=\"10\"/>");
					if (type != null && "d".equalsIgnoreCase(type)
							&& (iscurrent == null || iscurrent.length() <= 0)) {

						buff
								.append("<SCRIPT Language=\"JavaScript\">WebCalendar.format='");
						buff.append(element.getAttributeValue("value"));
						buff.append("'</SCRIPT>");
					}

				}
			}
		}
		buff
				.append("<input type=\"hidden\" name=\"hiddenValueName\" id=\"hiddenValueName\" />");
		
		// 外部输入参数
		buff.append("<input type='hidden' name='inputParam' id='inputParam' value='");
		buff.append(getInputParamStr());
		buff.append("'/>\r\n");
		
		buff.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		// buff.append("<input type=\"button\" class=\"mybutton\"
		// name=\"download\" value=\"下载\" onclick=\"downLoadExcel()\" />");
		// 是否添加打印预览按钮
		String printview = (String) paramSetMap.get("printview");
		if (!(printview != null && "false".equalsIgnoreCase(printview))) {
			buff.append("&nbsp;&nbsp;");
			buff
					.append("<input type=\"button\" class=\"mybutton\" name=\"printview\" value=\"打印预览\" onclick=\" document.all.WebBrowser.ExecWB(7,1)\" />");
		}

		// 是否添加打印按钮
		String printall = (String) paramSetMap.get("printall");
		if (!(printall != null && "false".equalsIgnoreCase(printall))) {
			buff.append("&nbsp;&nbsp;");
			buff
					.append("<input type=\"button\" class=\"mybutton\" name=\"printall\" value=\"打印\" onclick=\"window.print();\" />");
		}
		buff.append("</form>");
		buff.append("</div>");
		// 添加javascript脚本
		buff.append("<script>");
		buff.append("	function ss () {");
		buff.append("var sele = document.getElementsByTagName(\"select\");");
		buff.append("var inp = document.getElementsByTagName(\"input\");");
		buff.append("var val = \"\";");
		buff.append("for (i = 0; i < sele.length; i++) {");
		buff.append("	val +=(sele[i].name+\",\");");
		buff.append("}");
		buff.append("for(j = 0; j < inp.length; j++) {");
		buff
				.append("  	if (inp[j].type==\"text\" || inp[j].type==\"hidden\") {");
		buff.append("  		val += (inp[j].name + \",\");");
		buff.append("  	}");
		buff.append("  }");
		buff.append("var hid = document.getElementById(\"hiddenValueName\");");
		buff.append("hid.value = val;");
		buff.append("}");
		buff.append("ss();");
		buff.append("function changeTrans() {");
		// buff.append("form1.submit();");
		buff.append("openwin();");
		buff.append("} ");
		buff.append("function changeValue(event) {");
		buff.append("if(event.propertyName == \"value\"){");
		// buff.append("form1.submit();");
		buff.append("openwin();");
		buff.append("} ");
		buff.append("} ");
		buff.append("function dokeypess(event) {");
		buff.append("var s=event.keyCode;");
		buff.append("  if (s==13){");
		// buff.append(" form1.submit();");
		buff.append("	  openwin();");
		buff.append("  } ");
		buff.append("}");
		buff.append("</script>");
		return buff.toString();
	}

	/**
	 * 根据codesetid获得所有代码项
	 * 
	 * @param codesetid
	 * @return
	 */
	private ArrayList getCodeList(String codesetid) {
		ArrayList list = new ArrayList();
		String sql = "select codeitemid,codeitemdesc "
				+ "from codeitem where codesetid='" + codesetid + "'";
		try {
			ResultSet rs = dao.search(sql);
			while (rs.next()) {
				RecordVo vo = new RecordVo("codeitem");
				vo.setString("codeitemid", rs.getString("codeitemid"));
				vo.setString("codeitemdesc", rs.getString("codeitemdesc"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 根据代码类和代码项的值获得描述
	 * 
	 * @param codesetid
	 *            代码类id
	 * @param codeitemid
	 *            代码项id
	 * @return
	 */
	private String getDesc(String codesetid, String codeitemid) {
		StringBuffer desc = new StringBuffer();
		// 处理codeitemid的值，
		String[] str = codeitemid.split(",");
		StringBuffer inBuff = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			inBuff.append("'");
			inBuff.append(str[i]);
			inBuff.append("'");
			inBuff.append(",");
		}
		String in = inBuff.substring(0, inBuff.length() - 1);
		StringBuffer sql = new StringBuffer();

		if ("um".equalsIgnoreCase(codesetid)
				|| "un".equalsIgnoreCase(codesetid)
				|| "@k".equalsIgnoreCase(codesetid)) {
			sql.append("(select codeitemdesc from ");
			sql.append("organization ");
			sql.append("where codesetid='");
			sql.append(codesetid);
			sql.append("' and codeitemid in (");
			sql.append(in);
			sql.append(")");
			sql.append(" )");
			sql.append("union(select ");
			sql.append("codeitemdesc from vorganization ");
			sql.append("where codesetid='");
			sql.append(codesetid);
			sql.append("' and codeitemid in (");
			sql.append(in);
			sql.append(")");
			sql.append(" )");
		} else {
			sql.append("select codeitemdesc from ");
			sql.append("codeitem ");
			sql.append("where codesetid='");
			sql.append(codesetid);
			sql.append("' and codeitemid in (");
			sql.append(in);
			sql.append(")");
		}

		// 查询操作
		ResultSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				desc.append(rs.getString("codeitemdesc"));
				desc.append(",");
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
		if (desc.length() == 0) {
			return "";
		}
		return desc.substring(0, desc.length() - 1);
	}

	/**
	 * 返回cell数据集
	 * 
	 * @return
	 */
	public HashMap getCellElement() {
		String str_path = "/sqls/cells/sheet";
		HashMap cellsMap = new HashMap();
		XPath xpath;
		try {
			xpath = XPath.newInstance(str_path);
			List lists = xpath.selectNodes(this.doc);
			if (lists.size() != 0) {

				Element element = (Element) lists.get(0);
				List childlist = element.getChildren();
				for (Iterator t = childlist.iterator(); t.hasNext();) {
					Element elementc = (Element) t.next();
					String name = elementc.getAttributeValue("cell_name");
					cellsMap.put(name, elementc);
					sql_List.add(elementc);

				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cellsMap;
	}

	/**
	 * 返回带也签的cell数据集
	 * 
	 * @param sheet
	 * @return
	 */
	public HashMap getSheetCellValue(String sheetname) {
		this.sql_List.clear();
		String str_path = "/sqls/cells/sheet[@name='" + sheetname + "']";
		HashMap cellsMap = new HashMap();
		XPath xpath;
		try {
			xpath = XPath.newInstance(str_path);
			List lists = xpath.selectNodes(this.doc);
			if (lists.size() != 0) {
				Element sheeet_element = (Element) lists.get(0);
				List childlist = sheeet_element.getChildren();
				for (Iterator t = childlist.iterator(); t.hasNext();) {
					Element element = (Element) t.next();
					String name = element.getAttributeValue("cell_name");
					cellsMap.put(name.toLowerCase(), element);
					this.sql_List.add(element);
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cellsMap;
	}

	/**
	 * 得到sql数据集
	 * 
	 * @return
	 */
	private HashMap getSqlParam() {
		String str_path = "/sqls/sql_define";
		HashMap sqlsMap = new HashMap();
		XPath xpath;
		try {
			xpath = XPath.newInstance(str_path);
			List lists = xpath.selectNodes(this.doc);
			if (lists.size() != 0) {
				Element element = (Element) lists.get(0);
				List childlist = element.getChildren();
				for (Iterator t = childlist.iterator(); t.hasNext();) {
					Element elementc = (Element) t.next();
					String name = elementc.getAttributeValue("sql_id");
					sqlsMap.put(name.toLowerCase(), elementc);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return sqlsMap;
	}

	/**
	 * 取得单元格值
	 * 
	 * @param cellElement
	 * @return 返回二维list
	 */
	public ArrayList getCellValueList(Element cellElement)
			throws GeneralException {
		this.initCustomPrivSQL();
		// 替换参数后的sql语句
		String sql = "";
		// 数据集合
		ArrayList valuelist = new ArrayList();
		// 错误标示
		this.errorFlag = false;
		// sql的id， sql模板中sql语句的唯一标志
		String sql_id = cellElement.getAttributeValue("sql_id");
		// 定义的单元格名称
		String cell_name = cellElement.getAttributeValue("cell_name");
		// 是否保存值，以便下面的sql用该值作为参数
		String isSave = cellElement.getAttributeValue("value_is_save");
		// 一个单元格放多列多行值时，用来拼接字符窜
		StringBuffer buffVlue = new StringBuffer();
		/*
		 * 一个sql对象 <sql sql_id="参数连起来" > <!-- type:A|D|N|(字符型|日期型|数值型)
		 * deci:小数点位数，数值型才有效 --> <text type="N" deci="0"> select count(a0100)
		 * from usra01 where
		 * createtime>to_date(':year}-:month}-:da','yyyy-MM-dd') <!--select
		 * count(a0100) from usra01 where createtime &lt;
		 * ':year}-:month}-:da'--> </text> </sql>
		 */
		Element sqlElement = (Element) this.sqlsMap.get(sql_id.toLowerCase());
		if (sqlElement == null) {
            return null;
        }

		// 获得所有单元格的名称和值
		List list = cellElement.getAttributes();
		HashMap cellParamMap = new HashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Attribute e = (Attribute) it.next();
			cellParamMap.put(e.getName().toLowerCase(), e.getValue());

		}

		// 多选情况,
		String fgp = sqlElement.getAttributeValue("field_getvaluetype_pubname");
		this.setField_getvaluetype_pubname(fgp);

		List sqlList = sqlElement.getChildren();
		if (sqlList.size() != 0) {
			String sqlText = "";
			String type = "";
			String deci = "";
			HashMap sqlSelfParamMap = new HashMap();
			for (Iterator t = sqlList.iterator(); t.hasNext();) {
				Element element = (Element) t.next();
				String name = element.getName();
				if ("text".equalsIgnoreCase(name)) {
					sqlText = element.getText();
					type = element.getAttributeValue("type");
					deci = element.getAttributeValue("deci");

				} else if ("param".equalsIgnoreCase(name)) {
					sqlSelfParamMap.put(element.getAttributeValue("name")
							.toLowerCase(), element.getAttributeValue("value"));
				}
			}
			if (sqlText == null || sqlText.length() <= 0) {
                return valuelist;
            }
			sqlText = sqlText.trim();
			this.cat.error(sqlText);

			ArrayList sqlParamslist = getSqlParamsList(sqlText);// 得到sql中相关的参数
			if (sqlParamslist != null && sqlParamslist.size() == 1) {
				// 处理唯一参数
				String contvalue = diffeConstant(sqlText);
				if (contvalue != null && contvalue.length() > 0) {
					valuelist.add(contvalue);
					return valuelist;
				}
			}

			// 处理将多选的情况
			if (field_getvaluetype_pubname != null
					&& field_getvaluetype_pubname.trim().length() > 0) {
				sqlText = dealWithSQL(field_getvaluetype_pubname, sqlText);
			}
			sqlParamslist = getSqlParamsList(sqlText);
			sql = makeupSql(sqlText, sqlParamslist, sqlSelfParamMap,
					cellParamMap);// 根据sql模板和提取中的参数，组合sql
			// 替换所有的{ 和 }及 ]
			sql = sql.replaceAll("\\{", "").replaceAll("\\}", "");
			sql = sql.replaceAll("\\]", "");
			
			//this.cat.error(sqlText);
			
			ResultSet rs = null;
			Statement sta = null;
			try {

				//rs = this.dao.search(sql);
				//rs = new ContentDAO(myconn).search(sql);//sql语句过于复杂时，用contentdao 会查询超时，现在改用Statement xiegh 20180412 bug36620 
				sta = conn.createStatement();
				rs = sta.executeQuery(sql);
				ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();

				while (rs.next()) {
					ArrayList olist = new ArrayList();
					for (int i = 0; i < columnCount; i++) {
						String vv = rs.getString(i + 1);
						if (vv == null || vv.length() == 0) {
							vv = "";
						}

						// 数字类型
						if (type != null && "N".equalsIgnoreCase(type)) {
							if (vv == null || vv.length() <= 0) {
								vv = "0";
							}
						}

						// 数字类型有小数点
						if (type != null && "N".equalsIgnoreCase(type)
								&& deci != null && deci.length() > 0) {

							// 有小数点
							if (vv != null && vv.indexOf(".") != -1) {
								int num = Integer.parseInt(deci);
								olist.add(PubFunc.round(vv, num));

								buffVlue.append(",");
								buffVlue.append(PubFunc.round(vv, num));
							} else {
								int num = Integer.parseInt(deci);
								for (int k = 0; k < num; k++) {
									if (k == 0) {
										vv += "." + "0";
									} else {
										vv += "0";
									}
								}
								olist.add(PubFunc.round(vv, num));
								buffVlue.append(",");
								buffVlue.append(PubFunc.round(vv, num));

							}
						} else {

							olist.add(vv);

							buffVlue.append(",");
							buffVlue.append(vv);

						}
					}
					valuelist.add(olist);
				}
			}catch (SQLException e){
				e.printStackTrace();
				this.errorFlag = true;
				this.errorMr = "cell_name为\"" + cell_name + "\"中的参数定义与sql_id\""
						+ sql_id + "\"的sql组合后出错\r\n";
				this.errorMr = this.errorMr + "sql模板为：" + sqlText + "\r\n";
				this.errorMr = this.errorMr + "组合后的sql语句为:" + sql;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(rs);
				PubFunc.closeResource(sta);
			}

		} else {
			return valuelist;
		}
		
		// 保存值
		if (isSave != null && "true".equalsIgnoreCase(isSave)) {
			String vlue = "";
			if (buffVlue != null || buffVlue.length() > 0) {
				vlue = buffVlue.substring(1);
			}
			publicParamMap.put(cell_name, vlue);
			Element el = new Element("param");
			el.setAttribute("name", cell_name);
			el.setAttribute("value", vlue);
			String isshow = cellElement.getAttributeValue("isshow");
			if (isshow == null || isshow.length() <= 0) {
				isshow = "true";
			}
			el.setAttribute("isshow", isshow);
			this.publicElementMap.put(cell_name, el);
		}
		return valuelist;
	}

	/**
	 * 处理多选的情况
	 * 
	 * @param field_getvaluetype
	 * @param sqlText
	 * @return
	 */
	private String dealWithSQL(String field_getvaluetype, String sqlText) {

		// 去掉左右空格
		sqlText = sqlText.trim();
		// 是否成功的标志,0为成功，1为失败
		int flag = 0;
		String[] fieldAndType = field_getvaluetype.split(",");
		for (int i = 0; i < fieldAndType.length; i++) {
			String[] eles = fieldAndType[i].split(":");
			String value = "";
			if (eles.length != 0) {
				value = (String) publicParamMap.get(eles[eles.length - 1]+"reporthidden");//update by xiegh on 20180627 生成标签的时候里面加了后缀 现在取数时也需要加后缀，否则取不到数值
				//也页面的是能取到了，但是报表内置参数加了后缀取不到了，导致报表取不出数。兼容一下 guodd 2018-09-30
				if(value==null) {
                    value = (String) publicParamMap.get(eles[eles.length - 1]);
                }
				value = value==null?"":value.replaceAll("\\|", ",");//下面的代码对于多选的情况是按照，分隔；但是前台传过来的是 1|2
			}

			if (eles.length == 0) {// ::形式，不合法
				flag = 1;
			} else if (eles.length == 1) { // ::name形式，否则不合法
				if (field_getvaluetype.equalsIgnoreCase("::" + eles[0])) {
					sqlText = matchString(sqlText, "", "no", eles[0], value);
				} else {
					flag = 1;
				}
			} else if (eles.length == 2) { // :type:name或者field::name形式，否则不合法
				if (field_getvaluetype.equalsIgnoreCase(":" + eles[0] + ":"
						+ eles[1])) {
					sqlText = matchString(sqlText, "", eles[0], eles[1], value);
				} else if (field_getvaluetype.equalsIgnoreCase(eles[0] + "::"
						+ eles[1])) {
					sqlText = matchString(sqlText, eles[0], "no", eles[1],
							value);
				} else {
					flag = 1;
				}
			} else if (eles.length == 3) { // field:type:name形式
				sqlText = matchString(sqlText, eles[0], eles[1], eles[2], value);
			}

			if (flag == 1) {
				this.errorFlag = true;
				this.errorMr += "sql语句为：" + sqlText
						+ "中的field_getvaluetype_pubname定义错误！";
				break;
			}

		}
		return sqlText;
	}

	/**
	 * 正则表达式匹配并替换字符窜
	 * 
	 * @param sqlText
	 * @param field
	 * @param getvaluetype
	 * @param pubname
	 * @param value
	 * @return
	 */
	private String matchString(String sqlText, String field,
			String getvaluetype, String pubname, String value) {

		String reg = "\\{.*?" + field + ".*?:" + pubname + ".*?\\}";
		Pattern pattern = Pattern.compile(reg, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sqlText);
		while (matcher.find()) {
			if ("no".equalsIgnoreCase(getvaluetype)) {
				String oldsql = matcher.group();
				String newsql = oldsql.replace(":" + pubname, value);
				// newsql = newsql.replace("{", "").replace("}", "");
				newsql = newsql.substring(1, newsql.length() - 1);
				newsql = newsql.replaceAll("\\{", "").replaceAll("\\}", "");
				sqlText = sqlText.replace(matcher.group(), newsql);
			} else if ("in".equalsIgnoreCase(getvaluetype)) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				for (int i = 0; i < val.length; i++) {
					buff.append(",'");
					buff.append(val[i]);
					buff.append("'");
				}
				if (buff.length() > 0) {
					value = buff.substring(1);
				} else {
					value = "''";
				}
				String oldsql = matcher.group();
				// oldsql = oldsql.replaceAll("'", "");
				String newsql = oldsql.replaceAll("\\:" + pubname, value);
				newsql = newsql.substring(1, newsql.length() - 1);
				newsql = newsql.replaceAll("\\{", "").replaceAll("\\}", "");
				String ma = matcher.group();
				sqlText = sqlText.replace(matcher.group(), newsql);

			} else if ("and".equalsIgnoreCase(getvaluetype)) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" and ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(4);
				value = value.replaceAll("\\{", "").replaceAll("\\}", "");
				value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(matcher.group(), value);
			} else if ("or".equalsIgnoreCase(getvaluetype)) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" or ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(3);
				value = value.replaceAll("\\{", "").replaceAll("\\}", "");
				//value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(oldsql, value);
			} else if ("union".equalsIgnoreCase(getvaluetype)) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" union ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(6);
				 value = value.replace("\\{", "").replace("\\}", "");
				value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(oldsql, value);
			} else if ("except".equalsIgnoreCase(getvaluetype)
					|| "minus".equalsIgnoreCase(getvaluetype)
					|| "intersect".equalsIgnoreCase(getvaluetype)) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" ");
					buff.append(getvaluetype);
					buff.append(" ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(getvaluetype.length() + 1);
				value = value.replace("\\{", "").replace("\\}", "");
				value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(oldsql, value);
			} else if (getvaluetype.toLowerCase().endsWith("all")) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" ");
					buff.append(getvaluetype.substring(0, getvaluetype.length() - 3));
					buff.append(" all ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(getvaluetype.length() + 2);
				 value = value.replace("\\{", "").replace("\\}", "");
				value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(oldsql, value);
			} else if (getvaluetype.toLowerCase().endsWith("distinct")) {
				String[] val = value.split(",");
				StringBuffer buff = new StringBuffer();
				String oldsql = matcher.group();
				for (int i = 0; i < val.length; i++) {
					buff.append(" ");
					buff.append(getvaluetype.substring(0, getvaluetype.length() - 8));
					buff.append(" distinct ");
					buff.append(oldsql.replaceAll("\\:" + pubname, val[i]));
				}
				buff.append(")");
				value = "(" + buff.substring(getvaluetype.length() + 2);
				 value = value.replace("\\{", "").replace("\\}", "");
				value = value.substring(1, value.length() - 1);
				sqlText = sqlText.replace(oldsql, value);
			} else {
				String oldsql = matcher.group();
				String newsql = oldsql.replaceAll("\\:" + pubname, value);
				newsql = newsql.substring(1, newsql.length() - 1);
				newsql = newsql.replaceAll("\\{", "").replaceAll("\\}", "");
				sqlText = sqlText.replace(matcher.group(), newsql);
			}
		}
		return sqlText;
	}

	private String getTypeValue() {
		return "";
	}

	/**
	 * 判别是否sqlText是否仅是常量
	 * 
	 * @param sqlText
	 * @return
	 */
	private String diffeConstant(String sqlText) {

		String reText = "";
		if (":username".equalsIgnoreCase(sqlText))// 登录用户名
		{
			reText = this.userView.getUserName();

		} else if (":AppDate".equalsIgnoreCase(sqlText))// 登录业务时间
		{
			reText = this.userView.getAppdate();
		} else if (":privcode".equalsIgnoreCase(sqlText)) {
			if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode())) {
				// reText="单位";
			} else if ("UM".equalsIgnoreCase(this.userView.getManagePrivCode())) {
				// reText="部门";
			} else if ("@K".equalsIgnoreCase(this.userView.getManagePrivCode())) {
				// reText="职位";
			}
			reText = this.userView.getManagePrivCode();
		} else if (":temp_username".equalsIgnoreCase(sqlText)) {
			reText = this.userView.getUserName();
		} else if (":privcodevalue".equalsIgnoreCase(sqlText)) {
			reText = this.userView.getManagePrivCodeValue();
		} else if (":nbase".equalsIgnoreCase(sqlText)){
			List list = this.userView.getPrivDbList();
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				buff.append(",");
				buff.append(list.get(i));
			}
			if (buff.length() > 0) {
				reText = buff.substring(1);
			} else {
				reText = "";
			}
		}
		
		StringBuffer tt = new StringBuffer();
		for (Iterator it = this.publicElementMap.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			Element publicelement = (Element) e.getValue();
			String name = publicelement.getAttributeValue("name");
			if (sqlText.equalsIgnoreCase(":" + name)) {
				if (field_getvaluetype_pubname != null
						&& field_getvaluetype_pubname.trim().length() > 0) {
					sqlText = this.dealWithSQL(field_getvaluetype_pubname,
							sqlText);
					tt.append(sqlText);
				} else {
					String title = publicelement.getAttributeValue("title");
					String value = (String) publicParamMap.get(name);
					title = title != null ? title : "";
					value = value != null ? value : "";
					// tt.append(title+":"+value+" ");
					tt.append(value + " ");
				}
			}
		}
		if (tt != null && tt.length() > 0) {
            reText = reText + tt.toString();
        }
		return reText;
	}

	/**
	 * 组合sql语句
	 * 
	 * @param sqlText
	 *            定义sql
	 * @param sqlParamslist
	 *            sql所需参数
	 * @param sqlSelfParamMap
	 *            sql参数和参数值
	 * @param cellParamMap
	 *            cell定义参数和参数值
	 * @return
	 */
	private String makeupSql(String sqlText, ArrayList sqlParamslist,
			HashMap sqlSelfParamMap, HashMap cellParamMap)
			throws GeneralException {
		sqlText = PubFunc.Replace(sqlText, "@[", "<");// 转换小于号
		sqlText = PubFunc.Replace(sqlText, "@]", ">");// 转换大于号
		if (sqlParamslist == null || sqlParamslist.size() <= 0) {
            return sqlText;
        }
		// sqlText=sqlText.toLowerCase();
		StringBuffer sql = new StringBuffer();
		String param = "";
		try {
			for (int i = 0; i < sqlParamslist.size(); i++) {
				param = (String) sqlParamslist.get(i);
				if (":priva0100".equalsIgnoreCase(param))// 人员管理范围
				{
					int statr = sqlText.indexOf("a0100=" + param);
					if (statr == -1) {
						statr = sqlText.indexOf("a0100 =" + param);
					}
					if (statr == -1) {
						statr = sqlText.indexOf("a0100= " + param);
					}
					if (statr == -1) {
						statr = sqlText.indexOf("a0100 = " + param);
					}
					sql.append(sqlText.substring(0, statr));
					sqlText = sqlText.substring(statr
							+ ("a0100=" + param).length());
					if (!this.userView.isSuper_admin()
							&& this.personPurviewSql != null
							&& this.personPurviewSql.length() > 0)// 人员权限如果是超级管理员就不添加了
					{
						sql.append("EXISTS (");
						sql.append(this.personPurviewSql);
						sql.append(" where ppp.axxxx=a0100");
						sql.append(") ");
					} else {
						sql.append(" 1=1 ");
					}

				} else if (":username".equalsIgnoreCase(param))// 登录用户名
				{
					int statr = sqlText.indexOf(param);
					sql.append(sqlText.substring(0, statr));
					sqlText = sqlText.substring(statr + param.length());
					sql.append("'" + this.userView.getUserName() + "'");

				} else if (":temp_username".equalsIgnoreCase(param))// 登录用户名
				{
					int statr = sqlText.indexOf(param);
					sql.append(sqlText.substring(0, statr));
					sqlText = sqlText.substring(statr + param.length());
					sql.append("" + this.userView.getUserName() + "");

				} else if (":AppDate".equalsIgnoreCase(param))// 登录业务时间
				{
					int statr = sqlText.indexOf(param);
					sql.append(sqlText.substring(0, statr));
					sqlText = sqlText.substring(statr + param.length());
					sql.append("" + this.userView.getAppdate() + "");

				} else {
					int statr = sqlText.toLowerCase().indexOf(
							param.toLowerCase());
					if (statr == -1) {
						continue;
					}
					sql.append(sqlText.substring(0, statr));
					sqlText = sqlText.substring(statr + param.length());
					if (":privcode".equalsIgnoreCase(param))// 用户的管理范围代码类
					{
						sql.append("" + this.privcode + "");
					} else if (":privcodevalue".equalsIgnoreCase(param))// 管理范围代码值
					{
						sql.append("" + this.privcodevalue + "");
					} else {
						String paramKey = param.substring(1);
						String paramvalue = "";
						if (cellParamMap.get(paramKey) != null)// 表格参数
						{
							paramvalue = (String) cellParamMap.get(paramKey);
						} else if (sqlSelfParamMap.get(paramKey) != null)// sql参数
						{
							paramvalue = (String) sqlSelfParamMap.get(paramKey);
						} else if (this.publicParamMap.get(paramKey) != null)// 全局参数
						{
							paramvalue = (String) this.publicParamMap.get(paramKey);
							//add by xiegh on date 20171226 代码可以多选，如果为代码类型的字段 需要sql语句 in(value) 为value加''并以,分割
							Element ele = (Element)publicElementMap.get(paramKey);
							//根据sql判断是否支持多选。如果变量前是单引号，不支持多选，否则sql会出错 guodd 2019-05-05
							if(ele!=null && !sql.toString().trim().endsWith("'")){
								String codesetid = ele.getAttributeValue("codesetid");
								if(codesetid!=null&&!"".equals(codesetid)){
									String[] paramvalues = paramvalue.split(",");
									String child = "'";
									for(int m =0;m<paramvalues.length;m++){
										if(m==0) {
                                            child = child+paramvalues[m];
                                        } else {
                                            child = child+"','"+paramvalues[m];
                                        }
									}
									child =child+"'";
									paramvalue = child;
								}
							}
						}
						sql.append(paramvalue);
					}
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"SQL模板错误！在参数\"" + param + "\"附近有语法错误！"));
		}

		sql.append(sqlText);
		String sqlStr = sql.toString();
		sqlStr = sqlStr.replace("}", "");
		return sqlStr;
	}

	/**
	 * 得到sql中的参数配置
	 * 
	 * @return
	 */
	private ArrayList getSqlParamsList(String sqlText) {
		ArrayList paramList = new ArrayList();
		if (sqlText == null || sqlText.length() <= 0) {
            return null;
        }
		sqlText = sqlText.trim();
		sqlText = sqlText + " ";
		byte[] charArr = sqlText.getBytes();
		int i = 0;

		boolean isParam = false;
		String param_name = "";

		while (i < charArr.length) {
			if ((char) (charArr[i]) == ':') {
				isParam = true;
			}
			// System.out.println((char)(charArr[i])+"--"+(charArr[i])+"--");
			if (isParam) {

				if ((char) (charArr[i]) == ' ' || (char) (charArr[i]) == '　'
						|| (char) (charArr[i]) == '\''
						|| (char) (charArr[i]) == '}'
						|| (char) (charArr[i]) == '%'
						|| (char) (charArr[i]) == ')'
						|| (char) (charArr[i]) == '('
						|| (char) (charArr[i]) == ','
						|| (char) (charArr[i]) == '='
						|| (char) (charArr[i]) == '>'
						|| (char) (charArr[i]) == '<'
						|| (char) (charArr[i]) == '!'
						|| (char) (charArr[i]) == '+'
						|| (char) (charArr[i]) == '-'
						|| (char) (charArr[i]) == '*'
						|| (char) (charArr[i]) == '/'
						|| (char) (charArr[i]) == '.'
						|| (char) (charArr[i]) == ']'
						|| (char) (charArr[i]) == '\t'
						|| (char) (charArr[i]) == '\r'
						|| (char) (charArr[i]) == '\n'
						) {
					isParam = false;
					paramList.add(param_name.toLowerCase());

					param_name = "";
				} else {
                    param_name = param_name + (char) charArr[i];
                }
			}
			i++;
		}
		return paramList;
	}

	/**
	 * 人员权限
	 * 
	 * @return
	 */
	private String getPersonPurview() {
		if (this.userView.getPrivDbList() == null
				|| this.userView.getPrivDbList().size() <= 0) {
            return "";
        }
		StringBuffer sql = new StringBuffer();
		sql.append("select  axxxx,bxxxx from (");
		for (int i = 0; i < this.userView.getPrivDbList().size(); i++) {
			String dbase = this.userView.getPrivDbList().get(i).toString();
			String whereIN = getWhereINSql(userView, dbase);
			sql.append("select '"+dbase+"' as bxxxx, a0100 as axxxx" + whereIN + " ");
			sql.append(" union ");
		}
		if (sql.length() > 7) {
            sql.setLength(sql.length() - 7);
        }
		sql.append(") ppp");
		return sql.toString();
	}
	
	/**
	 * 人员权限
	 * 
	 * @return
	 */
	private String getPersonPurview(String dbname) {
		if (dbname == null || dbname.length() == 0) {
			return "";
		}
		String[] nbase = dbname.split(",");
		StringBuffer sql = new StringBuffer();
		sql.append("select  axxxx,bxxxx from (");
		for (int i = 0; i < nbase.length; i++) {
			String dbase = nbase[i];
			String whereIN = getWhereINSql(userView, dbase);
			sql.append("select '"+dbase+"' as bxxxx, a0100 as axxxx" + whereIN + " ");
			sql.append(" union ");
		}
		if (sql.length() > 7) {
            sql.setLength(sql.length() - 7);
        }
		sql.append(") ppp");
		return sql.toString();
	}

	/**
	 * 根据权限,生成select.IN中的查询串
	 * 
	 * @param userView
	 * @param userbase
	 * @return
	 */
	private static String getWhereINSql(UserView userView, String userbase) {
		String strwhere = "";
		String kind = "";
		if (!userView.isSuper_admin()) {
			String expr = "1";
			String factor = "";
			if ("UN".equals(userView.getManagePrivCode())) {
				factor = "B0110=";
				kind = "2";
				if (userView.getManagePrivCodeValue() != null
						&& userView.getManagePrivCodeValue().length() > 0) {
					factor += userView.getManagePrivCodeValue();
					factor += "%`";
				} else {
					factor += "%`B0110=`";
					expr = "1+2";
				}
			} else if ("UM".equals(userView.getManagePrivCode())) {
				factor = "E0122=";
				kind = "1";
				if (userView.getManagePrivCodeValue() != null
						&& userView.getManagePrivCodeValue().length() > 0) {
					factor += userView.getManagePrivCodeValue();
					factor += "%`";
				} else {
					factor += "%`E0122=`";
					expr = "1+2";
				}
			} else if ("@K".equals(userView.getManagePrivCode())) {
				factor = "E01A1=";
				kind = "0";
				if (userView.getManagePrivCodeValue() != null
						&& userView.getManagePrivCodeValue().length() > 0) {
					factor += userView.getManagePrivCodeValue();
					factor += "%`";
				} else {
					factor += "%`E01A1=`";
					expr = "1+2";
				}
			} else {
				expr = "1+2";
				factor = "B0110=";
				kind = "2";
				if (userView.getManagePrivCodeValue() != null
						&& userView.getManagePrivCodeValue().length() > 0) {
                    factor += userView.getManagePrivCodeValue();
                }
				factor += "%`B0110=`";
			}
			ArrayList fieldlist = new ArrayList();
			try {

				/** 表过式分析 */
				/** 非超级用户且对人员库进行查询 */
				if (userView.getKqManageValue() != null
						&& !"".equals(userView.getKqManageValue())) {
                    strwhere = userView.getKqPrivSQLExpression("", userbase,
                            fieldlist);
                } else {
                    strwhere = userView.getPrivSQLExpression(expr + "|"
                            + factor, userbase, false, fieldlist);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			StringBuffer wheresql = new StringBuffer();
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 ");
			kind = "2";
			strwhere = wheresql.toString();
		}
		
		return strwhere;
	}
	
	private String getInputParamStr() {
		StringBuffer buff = new StringBuffer();
		if (this.inputParamMap != null) {
			Iterator it = this.inputParamMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				buff.append(entry.getKey());
				buff.append(":");
				buff.append(entry.getValue());
				buff.append(",");
			}			
		}
		
		if (buff.length() > 0) {
			return buff.substring(0, buff.length() - 1);
		} else {
			return "";
		}
	}
	
	public HashMap getPublicElementMap() {
		return publicElementMap;
	}

	public void setPublicElementMap(HashMap publicElementMap) {
		this.publicElementMap = publicElementMap;
	}

	public HashMap getPublicParamMap() {
		return publicParamMap;
	}

	public void setPublicParamMap(HashMap publicParamMap) {
		this.publicParamMap = publicParamMap;
	}
	public ArrayList getSql_List() {
		return sql_List;
	}

	public void setSql_List(ArrayList sql_List) {
		this.sql_List = sql_List;
	}

	public String getField_getvaluetype_pubname() {
		return field_getvaluetype_pubname;
	}

	public void setField_getvaluetype_pubname(String field_getvaluetype_pubname) {
		this.field_getvaluetype_pubname = field_getvaluetype_pubname;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public HashMap getParamSetMap() {
		return paramSetMap;
	}

	public void setParamSetMap(HashMap paramSetMap) {
		this.paramSetMap = paramSetMap;
	}
	

	public HashMap getInputParamMap() {
		return inputParamMap;
	}

	public void setInputParamMap(HashMap inputParamMap) {
		this.inputParamMap = inputParamMap;
	}
}
