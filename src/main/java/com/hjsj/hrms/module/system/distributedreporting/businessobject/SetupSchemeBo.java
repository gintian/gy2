package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
/**
 * @Description: 分布式上报BO类
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:32:31
 * @version: 1.0
 */
public class SetupSchemeBo {
	public UserView userView;
	public Connection conn;
	public SetupSchemeBo(UserView userView,Connection connection){
		this.userView = userView;
		this.conn = connection;
	}
	/**
	 * 判断是否已定义数据标准
	 * @return 存在返回true,否则false
	 */
	public boolean existenceStandard() {
		boolean haveSchemeFlag = true;
		RowSet rs = null;
		try {
			//验证是否配置数据标准
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("SELECT Str_Value FROM Constant WHERE Constant = 'BS_ASYN_PLAN_S'");
			if(rs.next()) {
				String tempParam = rs.getString("Str_Value");
				if(StringUtils.isBlank(tempParam)) {
					haveSchemeFlag = false;
				}
			}else {
				haveSchemeFlag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return haveSchemeFlag;

	}
	/**
	 * 上报方式 中间库 -测试数据库连接
	 * @return 连接成功返回true 否则返回false
	 */
	public boolean testdbconnection(HashMap map) {
		boolean flag = false;
		Connection connection = null;
		try {
			//根据数据库类型加载驱动
			String dbname = (String) map.get("dbname");// 数据库名
			String dbtype = (String) map.get("dbtype");// 数据库类型1oracle,2sqlserver
			String dburl = (String) map.get("dburl");// 数据库链接
			String dbusername = (String) map.get("dbusername");// 数据库用户名
			String password = (String) map.get("password");// 密码
			String port = (String) map.get("port");// 端口号
			String testDbUrl = "";//jdbc驱动连接url
			if ("2".equalsIgnoreCase(dbtype)) {// mssql
				testDbUrl = "jdbc:sqlserver://" + dburl + ":" + port + ";databaseName=" + dbname;
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} else if ("1".equalsIgnoreCase(dbtype)) {
				testDbUrl = "jdbc:oracle:thin:@"+dburl+":"+port+":"+dbname;
				Class.forName("oracle.jdbc.driver.OracleDriver");
			}
			connection = DriverManager.getConnection(testDbUrl, dbusername, password);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			PubFunc.closeDbObj(connection);
		}
		return flag;
	}
	/**
	 * 上报方式 ftp -测试ftp连接
	 * @return 连接成功返回true 否则返回false
	 */
	public boolean testftpconnection(HashMap map) {
		boolean flag = false;
		try {
			String ip = (String) map.get("ip");
			String username = (String) map.get("username");// 用户名
			String password = (String) map.get("password");// 密码
			String port = (String) map.get("port");// 端口号
			flag = FtpUtilBo.testConnect(ip, Integer.parseInt(port), username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 保存上报方式
	 * @param map
	 * @return 保存成功返回true 否则返回false
	 */
	public boolean savescheme(HashMap map) {
		boolean flag = false;
		try {
			//方案配置
			String schemeids = (String) map.get("schemeids");// id
			String import_type = (String) map.get("import_type");// 上报方式
			String isenable = (String) map.get("isenable");// 是否启用方案
			String reporttype = (String) map.get("reporttype");// 增量还是全量
			ContentDAO dao = new ContentDAO(conn);
			String xml = getXmlByType(import_type,map);
			//更新数据库信息
			String sql = "UPDATE t_sys_asyn_scheme set schemeparam='"+xml+"',schemetype='"+reporttype+"',state='"+isenable+"' WHERE schemeid in ("+schemeids+")";
			dao.update(sql);
			flag =true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}
	private String getXmlByType(String import_type,HashMap map) {
		//封装数据库配置的xml
		String dbxml = "";
		try {
			dbxml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
			dbxml += "<scheme>";
			dbxml += "<import_type>";
			dbxml += import_type;
			dbxml += "</import_type>";
			if (StringUtils.equals("1", import_type)) {//中间库
				//根据数据库类型加载驱动
				String dbname = (String) map.get("dbname");// 数据库名
				String dbtype = (String) map.get("dbtype");// 数据库类型1oracle,2sqlserver
				String dburl = (String) map.get("dburl");// 数据库链接
				String dbusername = (String) map.get("dbusername");// 数据库用户名
				String password = (String) map.get("password");// 密码
				String port = (String) map.get("port");// 端口号
				dbxml += "<mid_db type=\"" + dbtype + "\" ip=\"" + dburl + "\" port=\"" + port + "\" dbname=\"" + dbname
						+ "\" user=\"" + dbusername + "\" password=\"" + password + "\">";
				dbxml += "</mid_db>";
			}
			if (StringUtils.equals("2", import_type)) {//FTP
				String ip = (String) map.get("ip");
				String datapath = (String) map.get("datapath");//路径
				String username = (String) map.get("username");// 用户名
				String password = (String) map.get("password");// 密码
				String port = (String) map.get("port");// 端口号
				dbxml += "<ftp  ip=\"" + ip + "\" port=\"" + port + "\" datapath=\"" + datapath
						+ "\" user=\"" + username + "\" password=\"" + password + "\">";
				dbxml += "</ftp>";
			}
			if (StringUtils.equals("3", import_type)) {//Webservice
				String serviceUrl = (String) map.get("serviceUrl");
				dbxml += "<http serviceUrl=\"" + serviceUrl+ "\">";
				dbxml += "</http>";
			}
			dbxml += "</scheme>";
			Document doc = PubFunc.generateDom(dbxml);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setExpandEmptyElements(true);// must
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			dbxml = outputter.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbxml;
	}
	/**
	 * 根据方案id获得上报的具体方案
	 * @param id
	 * @return 上报的具体方案 map
	 */
	public  HashMap<String, String> getSchemeparam(String id) {
		RowSet rs = null;
		HashMap<String, String> dbConfigMap = new HashMap<String, String>();
		try {
			ContentDAO dao = new ContentDAO(conn);
			//查询配置的数据标准子集
			rs = dao.search("SELECT schemename,schemeparam,schemetype,state,unitcode FROM "
					+ "t_sys_asyn_scheme WHERE schemeid = "+id);
			String import_type = "0";//上报方式  默认为手工上报
			String planname = "";// 方案名
			String isenable = "";// 是否启用
			String reporttype = "";// 上报方式
			if (rs.next()) {
				ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
				String unitcode = rs.getString("unitcode");
	            String path = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator+unitcode+File.separator+"zip";
	            dbConfigMap.put("path", path);
				planname = rs.getString("schemename") == null ? "" : rs.getString("schemename");
				reporttype = rs.getString("schemetype") == null ? "" : rs.getString("schemetype");
				isenable = rs.getString("state") == null ? "" : rs.getString("state");
				String schemeparamxml = rs.getString("schemeparam") == null ? "" : rs.getString("schemeparam");
				if(StringUtils.isNotBlank(schemeparamxml)) {
					PareXmlUtils utils = new PareXmlUtils(schemeparamxml);
					String xpath = "/scheme/import_type";
					Element tempitem = utils.getSingleNode(xpath);
					import_type = tempitem.getValue();
					if (StringUtils.equals("1", import_type)) {
						xpath = "/scheme/mid_db";
						tempitem = utils.getSingleNode(xpath);
					    String dbtype = tempitem.getAttributeValue("type") == null ? "" : tempitem.getAttributeValue("type");//数据库类型
						String dburl = tempitem.getAttributeValue("ip") == null ? "" : tempitem.getAttributeValue("ip");//
						String port = tempitem.getAttributeValue("port") == null ? "" : tempitem.getAttributeValue("port");//
						String dbname = tempitem.getAttributeValue("dbname") == null ? "" : tempitem.getAttributeValue("dbname");//
						String dbusername = tempitem.getAttributeValue("user") == null ? "" : tempitem.getAttributeValue("user");//
						String password = tempitem.getAttributeValue("password") == null ? "" : tempitem.getAttributeValue("password");
						dbConfigMap.put("dbname", dbname);
						dbConfigMap.put("dbtype", dbtype);
						dbConfigMap.put("dburl", dburl);
						dbConfigMap.put("dbusername", dbusername);
						dbConfigMap.put("password", password);
						dbConfigMap.put("port", port);
					}
					if (StringUtils.equals("2", import_type)) {
						xpath = "/scheme/ftp";
						tempitem = utils.getSingleNode(xpath);
						String ip = tempitem.getAttributeValue("ip") == null ? "" : tempitem.getAttributeValue("ip");
						String port = tempitem.getAttributeValue("port") == null ? "" : tempitem.getAttributeValue("port");
						String datapath = tempitem.getAttributeValue("datapath") == null ? "" : tempitem.getAttributeValue("datapath");
						String username = tempitem.getAttributeValue("user") == null ? "" : tempitem.getAttributeValue("user");
						String password = tempitem.getAttributeValue("password") == null ? "" : tempitem.getAttributeValue("password");
						dbConfigMap.put("ip", ip);
						dbConfigMap.put("datapath", datapath);
						dbConfigMap.put("username", username);
						dbConfigMap.put("ftppassword", password);
						dbConfigMap.put("ftpport", port);
					}
					if (StringUtils.equals("3", import_type)) {
						xpath = "/scheme/http";
						tempitem = utils.getSingleNode(xpath);
						String serviceUrl = tempitem.getAttributeValue("serviceUrl") == null ? "" : tempitem.getAttributeValue("serviceUrl");
						dbConfigMap.put("serviceUrl", serviceUrl);
					}
				}
			}
			dbConfigMap.put("import_type", import_type);
			dbConfigMap.put("planname", planname);
			dbConfigMap.put("isenable", isenable);
			dbConfigMap.put("reporttype", reporttype);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return dbConfigMap;
	}
	/**
	 * 获得constant表里BS_ASYN_PLAN_S 的Str_Value
	 */
	public String  getStr_Value() {
		   String Str_Value="";
		   try {
		       RecordVo constant_vo=ConstantParamter.getRealConstantVo("BS_ASYN_PLAN_S",this.conn);
	           HashMap<String, String> map =constant_vo.getValues();
	           Str_Value = (String) map.get("str_value");
           } catch (Exception e) {
               e.printStackTrace();
           }
		   return Str_Value;
	}
	/**
	 * 根据受保护指标获得字段名
	 * @param protectPeopleFieldtwo /a0101/e0122/b0110
	 * @return
	 */
	public String getFielddesc(String protectPeopleFieldtwo) {
		StringBuffer buffer = new StringBuffer();
		String[] array =protectPeopleFieldtwo.substring(1, protectPeopleFieldtwo.length()).split("/");
		for(int i=0;i<array.length;i++) {
			FieldItem item = DataDictionary.getFieldItem(array[i]);
			if (i!=0) {
				buffer.append(",");
			}
			buffer.append(item.getItemdesc());
		}
		return buffer.toString();
	}
	/**
	 * 根据人员库前缀获得人员库描述
	 * @param dbpre usr,ret
	 * @return
	 */
	public String getDbname(String dbpre) {
		StringBuffer buffer = new StringBuffer("");
		String[] preArray= dbpre.split(",");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet = null;
		for(int i=0;i<preArray.length;i++) {
			String sql = "select dbname from dbname where pre='"+preArray[i]+"'";
			try {
				rowSet=dao.search(sql);
				if (rowSet.next()) {
					if (i!=0) {
						buffer.append(",");
					}
					buffer.append(rowSet.getString(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeResource(rowSet);
			}
		}
		return buffer.toString();
	}
	/**
	 * 根据Str_value 获得document 对象
	 * @param Str_value xml字符串
	 * @return
	 */
	public Document getDocument(String Str_value) {
		Document document = null;
		if (null!=Str_value&&!"".equals(Str_value)) {
			try {
				document = PubFunc.generateDom(Str_value);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(Str_value);
			}
		}
		return document;
	}
	/**
	 * 根据方案id 删除方案
	 * @param schemeids
	 */
	public void deleteProgramme(String schemeids) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			//拼接删除的sql语句
			StringBuffer sqlbuff=new StringBuffer("");
			sqlbuff.append("delete from t_sys_asyn_scheme where schemeid in (");
			String[] idarray =schemeids.split(",");
			for(int i=0;i<idarray.length;i++) {
				sqlbuff.append(idarray[i]).append(",");
			}
			String	sql=sqlbuff.substring(0, sqlbuff.length()-1)+")";
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据主键ID删除日志表中的记录
	 * @param ids
	 */
	public void deleteLogRecord(String ids) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			//拼接删除的sql语句
			StringBuffer sqlbuff=new StringBuffer("");
			sqlbuff.append("delete from t_sys_asyn_acceptinfo where id in (");
			String[] idarray =ids.split(",");
			for(int i=0;i<idarray.length;i++) {
				sqlbuff.append(idarray[i]).append(",");
			}
			String	sql=sqlbuff.substring(0, sqlbuff.length()-1)+")";
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据checkIds删除记录
	 * @param checkIds
	 */
	public void deleteValidateRules(String checkIds) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			//拼接删除的sql语句
			StringBuffer sqlbuff=new StringBuffer("");
			sqlbuff.append("delete from t_sys_asyn_validaterules where checkId in (");
			String[] idarray =checkIds.split(",");
			for(int i=0;i<idarray.length;i++) {
				sqlbuff.append(idarray[i]).append(",");
			}
			String	sql=sqlbuff.substring(0, sqlbuff.length()-1)+")";
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据选中的指标创建中间表
	 * @param arr
	 */
	public void alertMiddleTable(String arr) {
		  DbWizard dbWizard=null;
		  dbWizard=new DbWizard(this.conn);
		  try {//1、将选中指标的自己存入map
		  String[] array =arr.split(",");
		  Map<String, String> map = new HashMap<String, String>();
		  for(int i=0;i<array.length;i++) {
			  String fieldString = array[i];
			  String[] fieldArray = fieldString.split("-");
			  String desc= map.get(fieldArray[0]);
			  if (null==desc) {
				map.put(fieldArray[0], fieldArray[1]);
			  }
		  }
		  boolean conB01= map.containsKey("B01");
		  if (!conB01) {
            map.put("B01", DataDictionary.getFieldSetVo("B01").getFieldsetdesc());
          }
		  boolean conK01= map.containsKey("K01");
          if (!conK01) {
            map.put("K01", DataDictionary.getFieldSetVo("K01").getFieldsetdesc());
          }
		  //2、将选中的指标存入list
		  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		  for(int i=0;i<array.length;i++) {
			  String fieldString = array[i];
			  String[] fieldArray = fieldString.split("-");
			  FieldItem item =DataDictionary.getFieldItem(fieldArray[2]);
			  Map<String, Object> itemMap = new HashMap<String, Object>();
			  itemMap.put("fieldsetid", item.getFieldsetid());
			  itemMap.put("itemid", item.getItemid());
			  itemMap.put("itemdesc", item.getItemdesc());
			  itemMap.put("codesetid", item.getCodesetid());
			  itemMap.put("itemtype", item.getItemtype());
			  itemMap.put("itemlength", item.getItemlength());
			  itemMap.put("decimalwidth", item.getDecimalwidth());
			  list.add(itemMap);
		  }
		  Field guidkeyfield = new Field("GUIDKEY","唯一标识码");
		  guidkeyfield.setDatatype(DataType.STRING);
		  guidkeyfield.setLength(38);
		  guidkeyfield.setNullable(true);
		  if (dbWizard.isExistTable("organization",false)) {//如果原先的organization表中不存在guidkey字段则增加guidkey字段
			 addField("organization", guidkeyfield);
		  }
		  //3、判断S_ASYN_PHOTO ，如果不存在则创建
		  String orgTable = "S_ASYN_PHOTO";
		  if (!dbWizard.isExistTable(orgTable,false)){
			  Table table=new Table("S_ASYN_PHOTO");
			  Field field = new Field("emp_id","人员主键");
			  field.setDatatype(DataType.STRING);
			  field.setLength(38);
			  field.setNullable(false);
			  field.setKeyable(true);
			  table.addField(field);
			  field = new Field("photo","照片");
			  field.setDatatype(DataType.BLOB);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("ext","照片后缀");
			  field.setDatatype(DataType.STRING);
			  field.setLength(10);
			  field.setNullable(false);
			  table.addField(field);
			  field  = new Field("nbase", "人员库");
			  field.setDatatype(DataType.STRING);
			  field.setNullable(true);
			  field.setLength(3);
			  table.addField(field);
			  field  = new Field("A0100", "人员编号");
			  field.setDatatype(DataType.STRING);
			  field.setNullable(true);
			  field.setLength(8);
			  table.addField(field);
			  field  = new Field("i9999", "子集记顺序号");
			  field.setDatatype(DataType.INT);
			  field.setNullable(false);
			  field.setLength(8);
			  table.addField(field);
			  field = new Field("modtime","更新时间");
			  field.setDatatype(DataType.DATETIME);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("modState","更新标记");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("vailid","有效标记");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(true);
			  table.addField(field);
			  dbWizard.createTable(table);
		  }
		  //3、先判断机构成绩表S_ASYN_ORG ，如果不存在则创建
		  orgTable = "S_ASYN_ORG";
		  if (!dbWizard.isExistTable(orgTable,false)){
			  Table table=new Table("S_ASYN_ORG");
			  Field field = new Field("guidkey","唯一标识码");
			  field.setDatatype(DataType.STRING);
			  field.setLength(38);
			  field.setNullable(false);
			  field.setKeyable(true);
			  table.addField(field);
			  field = new Field("codeitemid","机构编码");
			  field.setDatatype(DataType.STRING);
			  field.setLength(30);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("codeitemdesc","机构名称");
			  field.setDatatype(DataType.STRING);
			  field.setLength(200);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("codesetid","机构标识");
			  field.setDatatype(DataType.STRING);
			  field.setLength(2);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("parentid","上级机构编码");
			  field.setDatatype(DataType.STRING);
			  field.setLength(30);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("start_date","机构有效期始");
			  field.setDatatype(DataType.DATE);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("end_date","机构有效期止");
			  field.setDatatype(DataType.DATE);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("modtime","修改时间");
			  field.setDatatype(DataType.DATE);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("modState","更新标记");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(false);
			  table.addField(field);
			  field = new Field("vailid","有效标记");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("A0000","机构顺序");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(true);
			  table.addField(field);
			  field = new Field("GRADE","机构等级");
			  field.setDatatype(DataType.INT);
			  field.setLength(8);
			  field.setNullable(true);
			  table.addField(field);
			  dbWizard.createTable(table);
		  }
		  //4、遍历map 根据map的key(A01 B01 K01 ) 来判断表是否存在
		  for (Map.Entry<String, String> entry : map.entrySet()) {
			  String fieldset=entry.getKey();
			  ArrayList<String> dbPreList = getDbPreList();
			  for (String string : dbPreList) {
				  if (fieldset.toUpperCase().startsWith("A")) {
					  if (dbWizard.isExistTable(string+fieldset,false)) {//如果原先的表（A01 AXX ） 不存在guidkey字段则增加该字段
						  addField(string+fieldset, guidkeyfield);
					  }
				  }else {
					  if (dbWizard.isExistTable(fieldset,false)) {//如果原先的表（B01 BXX K01 KXX） 不存在guidkey字段则增加该字段
						  addField(fieldset, guidkeyfield);
					  }
				  }
			  }
			  String firstLetter=fieldset.substring(0,1).toUpperCase();//A B K
			  String tableName="S_ASYN_"+fieldset.toUpperCase();
			  if (!dbWizard.isExistTable(tableName,false)){//如果不存在此表,则创建
				  Table table = new Table(tableName);
				  //先添加固定指标
				  Field field = new Field("guidkey","唯一标识码");
				  field.setDatatype(DataType.STRING);
				  field.setLength(38);
				  field.setNullable(false);
				  field.setKeyable(true);
				  table.addField(field);
				  field  = new Field("modtime", "更新时间");
				  field.setDatatype(DataType.DATE);
				  field.setNullable(false);
				  table.addField(field);
				  field  = new Field("createtime", "创建时间");
				  field.setDatatype(DataType.DATE);
				  field.setNullable(true);
				  table.addField(field);
				  field  = new Field("modState", "更新标记");
				  field.setDatatype(DataType.INT);
				  field.setNullable(false);
				  field.setLength(8);
				  table.addField(field);
				  field  = new Field("vailid", "有效标记");
				  field.setDatatype(DataType.INT);
				  field.setNullable(true);
				  field.setLength(8);
				  table.addField(field);
				  if ("A01".equalsIgnoreCase(fieldset)) {//如果是人员主集
					  field  = new Field("B0110", "上报单位编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);
					  /*field  = new Field("E0122", "部门");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(40);
					  table.addField(field);
					  field  = new Field("E01A1", "岗位");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(30);
					  table.addField(field);*/
					  field  = new Field("nbase", "人员库");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(3);
					  table.addField(field);
					  field  = new Field("srcnbase", "源人员库");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(3);
					  table.addField(field);
					  field  = new Field("A0100", "人员编号");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(8);
					  table.addField(field);
					  field  = new Field("protectstatus", "保护指标状态");
					  field.setDatatype(DataType.INT);
					  field.setNullable(true);
					  field.setLength(8);
					  table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }else if (!"A01".equalsIgnoreCase(fieldset)&&"A".equalsIgnoreCase(firstLetter)) {//人员子集
					/*  field  = new Field("childB0110", "上报单位编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);*/
					  field  = new Field("emp_id", "人员主键");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(38);
					  table.addField(field);
					  field  = new Field("nbase", "人员库");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(3);
					  table.addField(field);
					  field  = new Field("A0100", "人员编号");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(true);
					  field.setLength(8);
					  table.addField(field);
					  field  = new Field("i9999", "子集记顺序号");
					  field.setDatatype(DataType.INT);
					  field.setNullable(false);
					  field.setLength(8);
					  table.addField(field);
					  field  = new Field("protectstatus", "保护指标状态");
					  field.setDatatype(DataType.INT);
					  field.setNullable(true);
					  field.setLength(8);
					  table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }else if ("B01".equalsIgnoreCase(fieldset)) {//机构主集
					  field  = new Field("B0110", "机构编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }else if (!"B01".equalsIgnoreCase(fieldset)&&"B".equalsIgnoreCase(firstLetter)){//机构子集
					  field  = new Field("B0110", "机构编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);
					  field  = new Field("i9999", "子集记顺序号");
					  field.setDatatype(DataType.INT);
					  field.setNullable(false);
					  field.setLength(8);
					  table.addField(field);
					  field  = new Field("org_id", "机构主键");
                      field.setDatatype(DataType.STRING);
                      field.setNullable(false);
                      field.setLength(38);
                      table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }else if ("K01".equalsIgnoreCase(fieldset)) {
					  field  = new Field("E01A1", "岗位编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }else if (!"K01".equalsIgnoreCase(fieldset)&&"K".equalsIgnoreCase(firstLetter)) {
					  field  = new Field("E01A1", "岗位编码");
					  field.setDatatype(DataType.STRING);
					  field.setNullable(false);
					  field.setLength(30);
					  table.addField(field);
					  field  = new Field("i9999", "子集记顺序号");
					  field.setDatatype(DataType.INT);
					  field.setNullable(false);
					  field.setLength(8);
					  table.addField(field);
					  field  = new Field("post_id", "岗位主键");
                      field.setDatatype(DataType.STRING);
                      field.setNullable(false);
                      field.setLength(38);
                      table.addField(field);
					  addFields(fieldset, list,table);//增加选中的指标
				  }
				  try {
					  dbWizard.createTable(table);
				  } catch (GeneralException e) {
					  e.printStackTrace();
				  }
			  }else {//如果存在,获得表机构
				  ArrayList<String> fixedFieldList = new ArrayList<String>();//把固定指标存入list
				  fixedFieldList.add("guidkey");
				  fixedFieldList.add("modtime");
				  fixedFieldList.add("createtime");
				  fixedFieldList.add("modState");
				  fixedFieldList.add("vailid");
				  if ("A01".equalsIgnoreCase(fieldset)) {//如果是人员主集
					  fixedFieldList.add("B0110");
					  fixedFieldList.add("nbase");
					  fixedFieldList.add("srcnbase");
					  fixedFieldList.add("A0100");
					  fixedFieldList.add("protectstatus");
				  }else if (!"A01".equalsIgnoreCase(fieldset)&&"A".equals(firstLetter)) {//人员子集
					  fixedFieldList.add("emp_id");
					  fixedFieldList.add("nbase");
					  fixedFieldList.add("A0100");
					  fixedFieldList.add("i9999");
					  fixedFieldList.add("protectstatus");
				  }else if ("B01".equalsIgnoreCase(fieldset)) {//机构主集
					  fixedFieldList.add("B0110");
				  }else if (!"B01".equalsIgnoreCase(fieldset)&&"B".equals(firstLetter)){//机构子集
					  fixedFieldList.add("B0110");
					  fixedFieldList.add("i9999");
					  fixedFieldList.add("org_id");
				  }else if ("K01".equalsIgnoreCase(fieldset)) {
					  fixedFieldList.add("E01A1");
				  }else if (!"K01".equalsIgnoreCase(fieldset)&&"K".equals(firstLetter)) {
					  fixedFieldList.add("E01A1");
					  fixedFieldList.add("i9999");
					  fixedFieldList.add("post_id");
				  }
				  ContentDAO dao=new ContentDAO(this.conn);
				  boolean alertFields=false;//字段的长度改变
				  boolean addFields=false;//新增加字段
				  boolean dropFields=false;//删除字段
				  RowSet rowSet= null;
				  try {
					rowSet=dao.search("select * from "+tableName+" where 1=2");
					ResultSetMetaData mt=rowSet.getMetaData();
					Table tableAddcolumn = new Table(tableName);
					Table tableAlertcolumn = new Table(tableName);
					for(int j=0;j<list.size();j++) {//遍历定义数据规范添加的指标
						HashMap<String, Object> map2 = (HashMap<String, Object>) list.get(j);
						String itemid= (String) map2.get("itemid");
						String fieldsetid = (String) map2.get("fieldsetid");
						if (fieldset.equalsIgnoreCase(fieldsetid)) {
							boolean result=false;
							for(int i=0;i<mt.getColumnCount();i++) {
								String columnName= mt.getColumnName(i+1);
								if (columnName.equalsIgnoreCase(itemid)) {//如果指标已经在表里，判断指标长度是否一致，如果不一致的话，换成新的
									 result=true;
									 int itemlength = (Integer) map2.get("itemlength");
									 int columnDisplaySize = mt.getColumnDisplaySize(i+1);
									 if (itemlength>columnDisplaySize) {
										 alertFields=true;
										 Field field = new Field(columnName);
										 String lable = (String) map2.get("itemdesc");
										 String itemtypeStr = (String) map2.get("itemtype");
										 int decimalwidth = (Integer) map2.get("decimalwidth");
										 int itemtype = getItemtype(itemtypeStr,decimalwidth);
										 field.setLabel(lable);
										 field.setDatatype(itemtype);
										 field.setLength(itemlength);
										 if (decimalwidth>0) {
											 field.setDecimalDigits(decimalwidth);
										 }
										 tableAlertcolumn.addField(field);
									 }
								}
							}
							if (!result) {
								addFields=true;
								String itemtypeStr = (String) map2.get("itemtype");
								int decimalwidth = (Integer) map2.get("decimalwidth");
								int itemtype = getItemtype(itemtypeStr,decimalwidth);
								int itemlength = (Integer) map2.get("itemlength");
								Field  field  = new Field(itemid, (String)map2.get("itemdesc"));
								field.setDatatype(itemtype);
								field.setNullable(true);
								field.setLength(itemlength);
								if (decimalwidth>0) {
									 field.setDecimalDigits(decimalwidth);
								}
								tableAddcolumn.addField(field);
							}
						}
					}

					Table tableDropcolumns = new Table(tableName);
					for(int i=0;i<mt.getColumnCount();i++) {
						String columnName= mt.getColumnName(i+1);
						boolean result = false;
						for(int j=0;j<list.size();j++) {//遍历定义数据规范添加的指标
							HashMap<String, Object> map2 = (HashMap<String, Object>) list.get(j);
							String itemid= (String) map2.get("itemid");
							String fieldsetid = (String) map2.get("fieldsetid");
							if (fieldset.equalsIgnoreCase(fieldsetid)&&itemid.equalsIgnoreCase(columnName)) {
								result = true;
							}
						}
						for(int k = 0;k<fixedFieldList.size();k++) {
							String itemid = fixedFieldList.get(k);
							if (itemid.equalsIgnoreCase(columnName)) {
								result = true;
							}
						}
						if (!result) {
							Field field = new Field(columnName);
							tableDropcolumns.addField(field);
							dropFields = true;
						}

					}
					if (dropFields) {
						dbWizard.dropColumns(tableDropcolumns);//删除字段
					}
					if (alertFields) {//如果有需要修改的字段
						dbWizard.alterColumns(tableAlertcolumn);//修改字段
					}
					if(!dbWizard.isExistField(tableName,"createtime")){
						  Table tableAddCreateTime = new Table(tableName);
						  Field field  = new Field("createtime", "创建时间");
						  field.setDatatype(DataType.DATE);
						  field.setNullable(true);
						  tableAddCreateTime.addField(field);
						  dbWizard.addColumns(tableAddCreateTime);
					}
					//s_asyn_org表新增默认字段（grade），需添加
					if(!dbWizard.isExistField("S_ASYN_ORG","GRADE")){
						Table tableAddORG = new Table("S_ASYN_ORG");
						Field field  = new Field("GRADE", "机构等级");
						field.setDatatype(DataType.INT);
						field.setLength(8);
						field.setNullable(true);
						tableAddORG.addField(field);
						dbWizard.addColumns(tableAddORG);
					}
					//s_asyn_org表新增默认字段（a0000），需添加
					if(!dbWizard.isExistField("S_ASYN_ORG","A0000")){
						Table tableAddORG = new Table("S_ASYN_ORG");
						Field field = new Field("A0000","机构顺序");
						field.setDatatype(DataType.INT);
						field.setLength(8);
						field.setNullable(true);
						tableAddORG.addField(field);
						dbWizard.addColumns(tableAddORG);
					}
					if (addFields) {
						dbWizard.addColumns(tableAddcolumn);//增加字段
					}
				 }catch (GeneralException e) {
					e.printStackTrace();
				 } catch (SQLException e) {
					e.printStackTrace();
				 } finally {
					PubFunc.closeResource(rowSet);
				 }
			  }
		  }
		 }catch (Exception e) {
			e.printStackTrace();
		 }
	}
	/**
	 * 如果表中不存在该字段则增加(数据库中已经存在此表)
	 * @param tablename
	 * @param field
	 */
	public void addField(String tableName,Field  field) {
		String fieldname = field.getName();//获取字段名
		DbWizard dbWizard=new DbWizard(this.conn);
		if (dbWizard.isExistTable(tableName,false)) {
			RowSet rowSet= null;
			ContentDAO dao=new ContentDAO(this.conn);
			try {
				rowSet = dao.search("select * from "+tableName+" where 1=2");
				boolean result = false;
				ResultSetMetaData mt=rowSet.getMetaData();
				for(int i=0;i<mt.getColumnCount();i++) {
					String columnName= mt.getColumnName(i+1);
					if (columnName.equalsIgnoreCase(fieldname)) {
						result=true;
						break;
					}
				}
				if (!result) {//如果不存在该字段
					Table table = new Table(tableName);
					table.addField(field);
					dbWizard.addColumns(table);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeResource(rowSet);
			}
		}
	}
	/**
	 * 根据a'd'n'm 返回字符串 itemtype
	 * @param itemtypeStr A D　N M
	 * @return
	 */
	public int getItemtype(String itemtypeStr, int decimalwidth) {
		int itemtype = 0;
		if ("A".equalsIgnoreCase(itemtypeStr)) {//字符或则代码
		   itemtype = DataType.STRING;
		}else if ("D".equalsIgnoreCase(itemtypeStr)) {//日期
		   itemtype = DataType.DATE;
		}else if ("N".equalsIgnoreCase(itemtypeStr)) {//数值
			if (decimalwidth>0) {
				itemtype = DataType.FLOAT;
			}else {
				itemtype = DataType.INT;
			}
		}else if ("M".equalsIgnoreCase(itemtypeStr)) {//备注
		   itemtype = DataType.CLOB;
		}
		return itemtype;
	}
	/**
	 * 根据选中的指标动态往表里增加字段
	 * @param fieldset子集名 A01 A02 B01 B02
	 * @param list 指标list
	 * @param table
	 */
	public void addFields(String fieldset,List<Map<String, Object>> list,Table table) {
		for(int i=0;i<list.size();i++) {
		  HashMap<String, Object> map = (HashMap<String, Object>) list.get(i);
		  String fieldsetid = (String) map.get("fieldsetid");
		  if (fieldset.equalsIgnoreCase(fieldsetid)) {
			  String itemid= (String) map.get("itemid");
			  String itemdesc=(String) map.get("itemdesc");
			  int itemlength = (Integer) map.get("itemlength");
			  String itemtypeStr = (String) map.get("itemtype");
			  int decimalwidth = (Integer) map.get("decimalwidth");
			  int itemtype = getItemtype(itemtypeStr,decimalwidth);
			  Field field=table.getField(itemid);
			  if (null==field) {//为了避免系统固定指标和选中的指标冲突的问题。
				  field  = new Field(itemid, itemdesc);
				  field.setNullable(true);
				  field.setLength(itemlength);
				  field.setDatatype(itemtype);
				  if (decimalwidth>0) {
						 field.setDecimalDigits(decimalwidth);
				  }
				  table.addField(field);
			  }
		 }
	  }
	}
	/**
	 * 判断要增加的单位在表中是否存在，存在返回true，不存在返回false
	 * @param orgid
	 * @return
	 */
	public boolean getOrg(String orgid) {
		boolean result=false;
		String sql="select * from t_sys_asyn_scheme where unitguid='"+orgid+"'";
		ContentDAO dao= new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet=dao.search(sql);
			if (rowSet.next()) {
				result=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return result;
	}
	/**
	 * 查询出最大的方案id加1
	 * @return
	 */
	public int getMaxSchemeid() {
		int i=0;
		String sql="select MAX(schemeid)+1 maxSchemeid from t_sys_asyn_scheme";
		ContentDAO dao= new ContentDAO(conn);
		RowSet rowSet = null;
		try {
			rowSet=dao.search(sql);
			if (rowSet.next()) {
				i=rowSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return i;
	}
	/**
	 * 新增上报单位
	 * @param Schemeid方案id，主键
	 * @param UnitCode单位编码
	 */
	public void addReportingUnit(int Schemeid,String unitguid,String unitcode) {
		String sql="insert into t_sys_asyn_scheme (schemeid,UnitCode,unitguid,State) values (?,?,?,2)";
		List list=new ArrayList();
		list.add(Schemeid);
		list.add(unitcode);
		list.add(unitguid);
		ContentDAO dao= new ContentDAO(conn);
		try {
			dao.update(sql, list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * //删除上报单位对应的导入方案表t_sys_asyn_scheme，过滤记录方案表t_sys_asyn_filtercondition ，代码映射表t_sys_asyn_code 中的记录
	 * @param removebuff
	 */
	public void delReportingUnit(String unitcodes,String unitguids) {
		try {
		    String[] removeArray =unitguids.split(",");

            StringBuffer removeGuidbuff = new StringBuffer("(");
            for(int i=0;i<removeArray.length;i++) {
                if (i!=0) {
                    removeGuidbuff.append(",");
                }
                removeGuidbuff.append("'");
                removeGuidbuff.append(removeArray[i]);
                removeGuidbuff.append("'");
            }
            removeGuidbuff.append(")");

 			ContentDAO dao= new ContentDAO(conn);
			StringBuffer buffer = new StringBuffer();
			buffer.append("delete from t_sys_asyn_scheme where unitguid in ");
			buffer.append(removeGuidbuff);
			dao.update(buffer.toString());

		    String[] removeCodeArray =unitcodes.split(",");
            StringBuffer removebuff = new StringBuffer("(");
            for(int i=0;i<removeCodeArray.length;i++) {
                if (i!=0) {
                    removebuff.append(",");
                }
                removebuff.append("'");
                removebuff.append(removeCodeArray[i]);
                removebuff.append("'");
            }
            removebuff.append(")");

			buffer = new StringBuffer();
			buffer.append("delete from t_sys_asyn_filtercondition where unitcode in ");
			buffer.append(removebuff);
			dao.update(buffer.toString());
			buffer = new StringBuffer();
			buffer.append("delete from t_sys_asyn_code where unitcode in ");
			buffer.append(removebuff);
			dao.update(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 查询出已经选的上报单位
	 * @return
	 */
	public JSONArray getSelectReportingUnit() {
		RowSet rowSet = null;
		JSONArray jsonArray = new JSONArray();
		try {
			//String sql="select t.unitguid,t.unitcode,o.codeitemdesc from  t_sys_asyn_scheme t,organization o where t.unitguid=o.guidkey";
			String sql = "select unitguid,unitcode,"+Sql_switcher.isnull("codeitemdesc","'已删除机构'")+" codeitemdesc from t_sys_asyn_scheme t left join organization o on t.unitguid=o.guidkey";
			ContentDAO dao= new ContentDAO(conn);
			rowSet = dao.search(sql);
			JSONObject jsonObject =null;
			while (rowSet.next()) {
				jsonObject = new JSONObject();
				jsonObject.put("unitcode", rowSet.getString("unitcode"));
				jsonObject.put("unitguid", rowSet.getString("unitguid"));
				jsonObject.put("codeitemdesc", rowSet.getString("codeitemdesc"));
				jsonArray.add(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return jsonArray;
	}
	/**
	 * 将规范保存到constant表中
	 * @param arr
	 * @param photoCheck
	 */
	public void addConstant(String arr,String photoCheck,String peopleCheckbox,String protectDbname, String protectPeople,
			String fieldCheckbox,String protectFieldDbname,String protectPeopleFieldOne,String protectPeopleFieldTwo,String dbnameRelationField ,String dbnameRelationCodeitemid) {
	  //1、拼装XML
	  SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String date=dateFormat.format(new Date());
	  StringBuffer stringBuffer = new StringBuffer("");
	  stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
	  stringBuffer.append("<scheme>");
	  stringBuffer.append("<param createTime=\"");
	  stringBuffer.append(date);
	  stringBuffer.append("\" reportPhoto=\"");
	  stringBuffer.append(photoCheck);
	  stringBuffer.append("\" />");
	  stringBuffer.append("<fieldSet>");
	  String[] array =arr.toUpperCase().split(",");
	  Map<String, String> map = new LinkedHashMap<String, String>();
	  for(int i=0;i<array.length;i++) {
		  String fieldString = array[i];
		  String[] fieldArray = fieldString.split("-");
		  String desc= map.get(fieldArray[0]);
		  if (null==desc) {
			map.put(fieldArray[0], fieldArray[1]);
		  }
	  }
	  for (Map.Entry<String, String> entry : map.entrySet()) {
		  stringBuffer.append("<set setid=\"");
		  stringBuffer.append(entry.getKey().toUpperCase());
		  stringBuffer.append("\" desc=\"");
		  stringBuffer.append(entry.getValue());
		  stringBuffer.append("\" />");
	  }
	  stringBuffer.append("</fieldSet>");
	  stringBuffer.append("<fieldItem>");
	  for(int i=0;i<array.length;i++) {
		  String fieldString = array[i];
		  String[] fieldArray = fieldString.split("-");
		  FieldItem item =DataDictionary.getFieldItem(fieldArray[2]);
		  stringBuffer.append("<item setid=\"");
		  stringBuffer.append(fieldArray[0].toUpperCase());
		  stringBuffer.append("\" itemid=\"");
		  stringBuffer.append(fieldArray[2].toUpperCase());
		  stringBuffer.append("\" itemdesc=\"");
		  stringBuffer.append(item.getItemdesc());
		  stringBuffer.append("\" codesetid=\"");
		  stringBuffer.append(item.getCodesetid().toUpperCase());
		  stringBuffer.append("\" itemtype=\"");
		  stringBuffer.append(item.getItemtype().toUpperCase());
		  stringBuffer.append("\" itemlength=\"");
		  stringBuffer.append(item.getItemlength());
		  stringBuffer.append("\" itemdecimal=\"");
		  stringBuffer.append(item.getDecimalwidth());
		  stringBuffer.append("\" mustfill=\"");
		  stringBuffer.append(fieldArray[4]);
		  stringBuffer.append("\" uniq=\"");
		  stringBuffer.append(fieldArray[5]);
		  stringBuffer.append("\" />");
	  }
	  stringBuffer.append("</fieldItem>");
	  stringBuffer.append("<protectPeople checkbox=\"");
	  stringBuffer.append(peopleCheckbox);
	  stringBuffer.append("\"> <dbname pre=\"");
	  if ("null".equalsIgnoreCase(protectDbname)||"undefined".equals(protectDbname)||null==protectDbname) {
		  protectDbname = "";
	  }
	  stringBuffer.append(protectDbname);
	  stringBuffer.append("\" /> <peopleCondition  condition=\"");
	  if ("null".equalsIgnoreCase(protectPeople)||"undefined".equals(protectPeople)||null==protectPeople) {
		  protectPeople = "";
	  }
	  stringBuffer.append(protectPeople);
	  stringBuffer.append("\" />");
	  stringBuffer.append("</protectPeople>");
	  stringBuffer.append("<protectField checkbox=\"");
	  stringBuffer.append(fieldCheckbox);
	  stringBuffer.append("\"> <dbname pre=\"");
	  if ("null".equalsIgnoreCase(protectFieldDbname)||"undefined".equals(protectFieldDbname)||null==protectFieldDbname) {
		  protectFieldDbname = "";
	  }
	  stringBuffer.append(protectFieldDbname);
	  stringBuffer.append("\" /> <peopleCondition condition=\"");
	  if ("null".equalsIgnoreCase(protectPeopleFieldOne)||"undefined".equals(protectPeopleFieldOne)||null==protectPeopleFieldOne) {
		  protectPeopleFieldOne = "";
	  }
	  stringBuffer.append(protectPeopleFieldOne);
	  stringBuffer.append("\" /> <fieldCondition  condition=\"");
	  if ("null".equalsIgnoreCase(protectPeopleFieldTwo)||"undefined".equals(protectPeopleFieldTwo)||null==protectPeopleFieldTwo) {
		  protectPeopleFieldTwo = "";
	  }
	  stringBuffer.append(protectPeopleFieldTwo);
	  stringBuffer.append("\" />");
	  stringBuffer.append("</protectField>");
	  stringBuffer.append("<personStatus>");
	  //人员状态指标与人员库的对应
	  if (!"".equals(dbnameRelationCodeitemid)) {
		  stringBuffer.append("<personItemid>");
		  stringBuffer.append(dbnameRelationField);
		  stringBuffer.append("</personItemid>");
		  String[] dbnameCodeitemidArray= dbnameRelationCodeitemid.split(",");
		  HashMap<String, String> dbnameCodeitemidmap = new HashMap<String, String>();
		  for(int i=0;i<dbnameCodeitemidArray.length;i++) {
			  String dbnameCodeitemid=dbnameCodeitemidArray[i];
			  String[] relationArray = dbnameCodeitemid.split("-");
			  String dbnamepre = relationArray[1];
			  String codeitemid = relationArray[0];
			  if (dbnameCodeitemidmap.get(dbnamepre)==null) {
				  dbnameCodeitemidmap.put(dbnamepre, codeitemid);
			  }else {
				 String oldcodeitemid=dbnameCodeitemidmap.get(dbnamepre);
				 dbnameCodeitemidmap.put(dbnamepre,oldcodeitemid+","+codeitemid);
			  }
		  }
		  for(String key:dbnameCodeitemidmap.keySet()){
			  stringBuffer.append("<mapping pre=\"");
			  stringBuffer.append(key);
			  stringBuffer.append("\" personMapping=\"");
			  stringBuffer.append(dbnameCodeitemidmap.get(key));
			  stringBuffer.append("\"></mapping>");
	      }
	  }else {
		  stringBuffer.append("<personItemid>");
		  stringBuffer.append("</personItemid>");
	  }
	  stringBuffer.append("</personStatus>");
	  stringBuffer.append("</scheme>");
	  //格式化xml
	  Document doc = getDocument(stringBuffer.toString());
	  XMLOutputter outputter = new XMLOutputter();
	  Format format = Format.getPrettyFormat();
	  format.setExpandEmptyElements(true);// must
	  format.setEncoding("UTF-8");
	  outputter.setFormat(format);
	  String dbxml = outputter.outputString(doc);
	  //2、保存进数据库
	/*  String sql="update Constant set Str_Value=? where constant='BS_ASYN_PLAN_S'";
	  ArrayList<String> list = new ArrayList<String>();
	  list.add(dbxml);
	  ContentDAO dao = new ContentDAO(this.conn);*/
	  ConstantXml constantXml = new ConstantXml(conn);
	  constantXml.saveValue("BS_ASYN_PLAN_S", dbxml);
	  try {
		//dao.update(sql,list);
		constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
        String jsonPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"scheme"+File.separator+"server"+File.separator+"sch";
        FileUtil.deleteFile(jsonPath+File.separator+"menus.json");
        FileUtil.deleteFile(jsonPath+File.separator+"codeitems.json");
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}
	/**
	 * 获得操作按钮
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getButtonList() throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			String a = ResourceFactory.getProperty("dr_define.data.specification");
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_define.reporting.unit"),"SetupschemeGlobal.defineReportingUnit"));
			buttonList.add("-");
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_define.data.specification"),"SetupschemeGlobal.defineDataSpecification"));
            //buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_set.correspondence"),"SetupschemeGlobal.setMatching"));
            buttonList.add("-");
        	buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_validate.Rules"),"SetupschemeGlobal.validateRules"));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	/**
	 * 获得日志记录表的工具栏
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getLogRecordButtonList() throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_delete.logs"),"DatacontrolGlobal.deleteLogRecord"));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	/**
	 * 获得方案表的列
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<ColumnsInfo> getColumnList() throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
			FieldItem fieldItem0 = new FieldItem();
			fieldItem0.setItemid("schemeid");
			fieldItem0.setItemtype("A");
			fieldItem0.setCodesetid("0");
			ColumnsInfo info0 = new ColumnsInfo(fieldItem0);
			info0.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info0);
			FieldItem fieldItem7 = new FieldItem();
			fieldItem7.setItemid("unitguid");
			fieldItem7.setItemtype("A");
			fieldItem7.setCodesetid("0");
            ColumnsInfo info7 = new ColumnsInfo(fieldItem0);
            info7.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            list.add(info7);
			FieldItem fieldItem1 = new FieldItem();
			fieldItem1.setItemid("unitcode");
			fieldItem1.setItemdesc(ResourceFactory.getProperty("dr_reporting.unit"));
			fieldItem1.setItemtype("A");
			fieldItem1.setCodesetid("UN");
			ColumnsInfo info1 = new ColumnsInfo(fieldItem1);
			info1.setColumnWidth(200);
			info1.setLocked(false);
			info1.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info1);
			FieldItem fieldItem3 = new FieldItem();
			fieldItem3.setItemid("state");
			fieldItem3.setItemdesc(ResourceFactory.getProperty("dr_scheme.state"));
			fieldItem3.setItemtype("A");
			fieldItem3.setCodesetid("0");
			ColumnsInfo info3 = new ColumnsInfo(fieldItem3);
			info3.setLocked(false);
			info3.setRendererFunc("SetupschemeGlobal.schemeState");
			info3.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info3);
			FieldItem fieldItem4 = new FieldItem();
			fieldItem4.setItemid("reporttype");
			fieldItem4.setItemdesc(ResourceFactory.getProperty("dr_report.type"));
			fieldItem4.setItemtype("A");
			fieldItem4.setCodesetid("0");
			ColumnsInfo info4 = new ColumnsInfo(fieldItem4);
			info4.setLocked(false);
			info4.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info4.setRendererFunc("SetupschemeGlobal.reporttype");
			list.add(info4);
			FieldItem fieldItem6 = new FieldItem();
			fieldItem6.setItemid("reportleader");
			fieldItem6.setItemdesc(ResourceFactory.getProperty("dr_report.leader"));
			fieldItem6.setItemtype("A");
			fieldItem6.setCodesetid("0");
			ColumnsInfo info6 = new ColumnsInfo(fieldItem6);
			info6.setLocked(false);
			info6.setEditableValidFunc("false");
			info6.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info6.setRendererFunc("SetupschemeGlobal.reportleader");
			list.add(info6);
			FieldItem fieldItem5 = new FieldItem();
			fieldItem5.setItemid("operation");
			fieldItem5.setItemdesc(ResourceFactory.getProperty("dr_operation"));//操作
			fieldItem5.setItemtype("A");
			fieldItem5.setReadonly(true);
			fieldItem5.setItemlength(50);
			fieldItem5.setCodesetid("0");
			ColumnsInfo info5 = new ColumnsInfo(fieldItem5);
			info5.setTextAlign("center");
			info5.setEditableValidFunc("false");
			info5.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			info5.setRendererFunc("setup_scheme.lineOperation");
			info5.setColumnWidth(200);
			info5.setSortable(false);
			list.add(info5);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 获得展示方案数据的tablebuid数据
	 * @param sql
	 * @return
	 */
	public ArrayList<LazyDynaBean> getSchemeData() {
		ArrayList<LazyDynaBean> datalist = new ArrayList<LazyDynaBean>();
		RowSet rowSet = null;
		try {
			String sql = "select a.unitcode,a.schemeid,a.state,a.schemeparam,a.reportleader,b.fullname from t_sys_asyn_scheme a left join operuser b on a.reportleader = b.username  order by a.unitcode ";
		    ContentDAO dao = new ContentDAO(conn);
		    rowSet = dao.search(sql);
		    while (rowSet.next()) {
		    	LazyDynaBean bean= new LazyDynaBean();
				String codeitemid = rowSet.getString("unitcode");
				String schemeid = rowSet.getString("schemeid");
				String state  = rowSet.getString("state")==null?"":rowSet.getString("state");
				String schemeparam = rowSet.getString("schemeparam");
				String reportleader = rowSet.getString("reportleader")==null?"":rowSet.getString("reportleader");
				String fullname = rowSet.getString("fullname")==null?"":rowSet.getString("fullname");
				bean.set("schemeid", schemeid);
				bean.set("unitcode", codeitemid);
				bean.set("state", state);
				if (StringUtils.isNotEmpty(schemeparam)) {
					PareXmlUtils pareXmlUtils = new PareXmlUtils(schemeparam);
		    		Element element = pareXmlUtils.getSingleNode("/scheme/import_type");
		    		String import_type= element.getValue();
		    		bean.set("reporttype", import_type);
				}else {
					bean.set("reporttype", "");
				}
				if (StringUtils.isNotEmpty(fullname)) {
					reportleader = fullname;
				}
				bean.set("reportleader", reportleader);
				datalist.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return datalist;
	}
	/**
	 * 通过业务用户的用户名获得其关联自助用户的真实用户名
	 * @param username
	 * @return
	 */
	@SuppressWarnings("resource")
	private String getRealname(String username) {
		RowSet rowSet = null;
		try {
			String sql = "select a0100,nbase from operuser where username = '"+username+"'";
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sql);
			String a0100 = "";
			String nbase = "";
			if (rowSet.next()) {
				 a0100 = rowSet.getString("a0100");
				 nbase = rowSet.getString("nbase");
			}
			if (StringUtils.isNotEmpty(a0100)&&StringUtils.isNotEmpty(nbase)) {
				sql = "select a0101 from "+nbase+"a01 where a0100 = '"+a0100+"'";
				rowSet = dao.search(sql);
				if (rowSet.next()) {
				     String realname = rowSet.getString("a0101");
				     if (StringUtils.isNotEmpty(realname)) {
						username = realname;
					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return username;
	}
	/**
	 * 获得日志记录表的列
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<ColumnsInfo> getLogRecordColumnList() throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
		    //id UnitCode acceptTime Status
			FieldItem fieldItem0 = new FieldItem();
			fieldItem0.setItemid("id");
			fieldItem0.setItemdesc("id");
			fieldItem0.setItemtype("N");
			fieldItem0.setCodesetid("0");
			ColumnsInfo info0 = new ColumnsInfo(fieldItem0);
			info0.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info0);
			FieldItem fieldItem1 = new FieldItem();
			fieldItem1.setItemid("UnitCode");
			fieldItem1.setItemdesc(ResourceFactory.getProperty("dr_unit.name"));
			fieldItem1.setItemtype("A");
			fieldItem1.setCodesetid("UN");
			ColumnsInfo info1 = new ColumnsInfo(fieldItem1);
			info1.setColumnWidth(200);
			info1.setEditableValidFunc("false");
			info1.setLocked(false);
			info1.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info1);
			FieldItem fieldItem2 = new FieldItem();
			fieldItem2.setItemid("acceptTime");
			fieldItem2.setItemdesc(ResourceFactory.getProperty("dr_accept.time"));
			fieldItem2.setItemtype("A");
			fieldItem2.setCodesetid("0");
			ColumnsInfo info2 = new ColumnsInfo(fieldItem2);
			info2.setTextAlign("left");
			info2.setColumnWidth(200);
			info2.setLocked(false);
			info2.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info2);
			FieldItem fieldItem4 = new FieldItem();
			fieldItem4.setItemid("Status");
			fieldItem4.setItemdesc(ResourceFactory.getProperty("dr_accept.status"));
			fieldItem4.setItemtype("A");
			fieldItem4.setCodesetid("0");
			ColumnsInfo info4 = new ColumnsInfo(fieldItem4);
			info4.setLocked(false);
			//info4.setRendererFunc("DatacontrolGlobal.acceptStatus");
			info4.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info4);
			FieldItem fieldItem3 = new FieldItem();
			fieldItem3.setItemid("downloadLog");
			fieldItem3.setItemdesc(ResourceFactory.getProperty("dr_accept.logs"));
			fieldItem3.setItemtype("A");
			fieldItem3.setCodesetid("0");
			ColumnsInfo info3 = new ColumnsInfo(fieldItem3);
			info3.setTextAlign("center");
			info3.setLocked(false);
			info3.setSortable(false);//不排序
			info3.setRendererFunc("DatacontrolGlobal.downloadingLog");
			info3.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			list.add(info3);
			FieldItem fieldItem5 = new FieldItem();
			fieldItem5.setItemid("situation");
			fieldItem5.setItemdesc(ResourceFactory.getProperty("dr_accept.situation"));
			fieldItem5.setItemtype("A");
			fieldItem5.setCodesetid("0");
            ColumnsInfo info5 = new ColumnsInfo(fieldItem5);
            info5.setTextAlign("left");
            info5.setLocked(false);
            info5.setSortable(false);//不排序
            info5.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info5);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 检验上报方式是否已经定义
	 * @param map
	 * @return 定义则返回true,未定义则返回false
	 */
	public boolean checkSchemeparam(HashMap map) {
		boolean flag = false;
		RowSet rowSet = null;
		try {
			String schemeid = (String) map.get("schemeid");
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select schemeparam from t_sys_asyn_scheme where schemeid = "+Integer.parseInt(schemeid);
		    rowSet = dao.search(sql);
		    if (rowSet.next()) {
				String schemeparam = rowSet.getString("schemeparam");
				if (StringUtils.isNotEmpty(schemeparam)) {
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return flag;
	}
	/**
	 * 导出上报方案
	 * @param map
	 */
	public String exportScheme(HashMap map) {
		RowSet rowSet = null;
		String zipName = "";
		try {
			String schemeid = (String) map.get("schemeid");
			//获取单位编码和单位名称
			String uncodeitemid = (String) map.get("unitcode");
			String undesc = uncodeitemid.split("`")[1];
			uncodeitemid=uncodeitemid.split("`")[0];
			//1、获得json文件的存放路径
			ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String jsonPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"scheme"+File.separator+"server"+File.separator+"sch";
            String zipPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"scheme"+File.separator+"server";
            String clientPath = System.getProperty("java.io.tmpdir");//客户端创建一份
		    //2、重新创建menus.json文件
            boolean menusflag = FileUtil.fileExistence(jsonPath, "menus.json");
            if (menusflag) {
                FileUtil.delfile(jsonPath, "menus.json");
			}
            createMenusFile(jsonPath);
            //3、重新创建codeitems.json文件
            boolean codeitemsflag = FileUtil.fileExistence(jsonPath, "codeitems.json");
            if (codeitemsflag) {
                FileUtil.delfile(jsonPath, "codeitems.json");
			}
            createCodeitemsFile(jsonPath);
            //4、重新创建reportunit_【上报单位编码】.json文件
            boolean reportunitflag = FileUtil.fileExistence(jsonPath, "reportunit_"+uncodeitemid+".json");
            if (reportunitflag) {
            	FileUtil.delfile(jsonPath, "reportunit_"+uncodeitemid+".json");
			}
            createReportunitFile(jsonPath, uncodeitemid, undesc,schemeid);
            //5、将三个json文件组成压缩包 文件存放根目录\asyn\scheme\loc\FA0201_20190502131110.zip。
            zipName = "FA"+uncodeitemid+"_"+PubFunc.FormatDate(new Date(), "yyyyMMddHHmmss")+".zip";
            zipPath = zipPath+File.separator+zipName;
            ArrayList<String> list = new ArrayList<String>();
            list.add(jsonPath+File.separator+"menus.json");
            list.add(jsonPath+File.separator+"codeitems.json");
            list.add(jsonPath+File.separator+"reportunit_"+uncodeitemid+".json");
            String password = SystemConfig.getPropertyValue("asyn_zip_password");
            String zipPassword = DrConstant.ZIP_PASSWORD;
            if (StringUtils.isNotEmpty(password)) {
				zipPassword = password;
			}
            FileUtil.createEncrypZip(zipPath, list, zipPassword);
            FileUtil.createEncrypZip(clientPath+File.separator+zipName, list, zipPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return PubFunc.encrypt(zipName);
	}
	/**
	 * 创建reportunit_【上报单位编码】.json文件
	 * @param path 文件路径
	 * @param uncodeitemid 单位编码
	 * @param undesc 单位名称
	 */
	private void createReportunitFile(String path,String uncodeitemid,String undesc,String schemeid) {
		String filename = "reportunit_"+uncodeitemid;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("unitcode", uncodeitemid);
		jsonObject.put("unitname", undesc);
		jsonObject.put("pkgtime", PubFunc.FormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		RowSet rowSet = null;
		try {
			String sql = "select unitcode,schemeparam,schemetype,unitguid from t_sys_asyn_scheme where schemeid = "+Integer.parseInt(schemeid);
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sql);
			if (rowSet.next()) {
				String schemeparam = rowSet.getString("schemeparam");
				String schemetype = rowSet.getString("schemetype");
				String unitguid = rowSet.getString("unitguid");
				jsonObject.put("unitguid", unitguid);
				jsonObject.put("pkgtype", schemetype);
				PareXmlUtils pareXmlUtils = new  PareXmlUtils(schemeparam);
				Element element = pareXmlUtils.getSingleNode("/scheme/import_type");
				String import_type = element.getValue();
				jsonObject.put("import_type", import_type);
				//如果是中间库方式
				if (StringUtils.equals(import_type, "1")) {
					element = pareXmlUtils.getSingleNode("/scheme/mid_db");
					JSONObject dbjsonObject = new JSONObject();
					String type = element.getAttributeValue("type");//数据库类型 1oracle 2MSSQL
					String ip = element.getAttributeValue("ip");
					String port = element.getAttributeValue("port");
					String dbname = element.getAttributeValue("dbname");
					String user = element.getAttributeValue("user");
					String password = element.getAttributeValue("password");
					dbjsonObject.put("type", type);
					dbjsonObject.put("ip", ip);
					dbjsonObject.put("port", port);
					dbjsonObject.put("dbname", dbname);
					dbjsonObject.put("username", user);
					dbjsonObject.put("pwd", password);
					jsonObject.put("middb", dbjsonObject);
				}
				//ftp
				if (StringUtils.equals(import_type, "2")) {
					element = pareXmlUtils.getSingleNode("/scheme/ftp");
					String ip = element.getAttributeValue("ip");
					String port = element.getAttributeValue("port");
					String datapath = element.getAttributeValue("datapath");
					String user = element.getAttributeValue("user");
					String password = element.getAttributeValue("password");
					JSONObject ftpjsonObject = new JSONObject();
					ftpjsonObject.put("ip", ip);
					ftpjsonObject.put("port", port);
					ftpjsonObject.put("username", user);
					ftpjsonObject.put("data_path", datapath);
					ftpjsonObject.put("pwd", password);
					jsonObject.put("ftp", ftpjsonObject);
				}
				//wsdl
				if (StringUtils.equals(import_type, "3")) {
					element = pareXmlUtils.getSingleNode("/scheme/http");
					String sjwsdl = element.getAttributeValue("serviceUrl");
					JSONObject wsdljsonObject = new JSONObject();
					wsdljsonObject.put("sjwsdl", sjwsdl);
					jsonObject.put("wsdl", wsdljsonObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {

		}
		FileUtil.createJsonFile(jsonObject.toString(), path, filename);
	}
	/**
	 * 创建codeitems.json文件
	 * @param path
	 */
	private void createCodeitemsFile(String path) {
		HashMap<String, String> codesetMap = new HashMap<String, String>();
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rowSet =dao.search("select codesetid,codesetdesc from codeset");
			while (rowSet.next()) {
				codesetMap.put(rowSet.getString("codesetid"),rowSet.getString("codesetdesc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		//查询出上报指标有多少代码项
		ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
		List<Element> itemlist =constantXml.getElementList("/scheme/fieldItem/item");
		Set<String> codesetList = new HashSet<String>();
		for (Element element : itemlist) {
			String codesetid = element.getAttributeValue("codesetid");
			if (StringUtils.isNotEmpty(codesetid)&&!StringUtils.equals("0", codesetid)) {
				codesetList.add(codesetid);
			}
		}
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put("codeset_list", jsonArray);
		for (String codesetid : codesetList) {
			JSONObject codesetJsonobject = new JSONObject();
			codesetJsonobject.put("codeset_id", codesetid);
			codesetJsonobject.put("codeset_name", codesetMap.get(codesetid));
			ArrayList<CodeItem> codeItemlist=AdminCode.getCodeItemList(codesetid);
			JSONArray codeitemArray = new JSONArray();
			for (CodeItem codeItem : codeItemlist) {
				JSONObject codeitemObject = new JSONObject();
				String itemid = codeItem.getCodeitem();
				String itemdesc = codeItem.getCodename();
				String parentid = codeItem.getPcodeitem();
				codeitemObject.put("itemid", itemid);
				codeitemObject.put("itemdesc", itemdesc);
				codeitemObject.put("parentid", parentid);
				codeitemArray.add(codeitemObject);
			}
			codesetJsonobject.put("item_list", codeitemArray);
			jsonArray.add(codesetJsonobject);
		}
		jsonObject.put("codeset_list", jsonArray);
		FileUtil.createJsonFile(jsonObject.toString(), path, "codeitems");
	}
	/**
	 * 创建menus.json文件
	 * @param 文件路径
	 */
	private void createMenusFile(String path)throws Exception {
		JSONObject jsonObject = new JSONObject();
		ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
		//1、拼装信息集
		List<Element> setlist =constantXml.getElementList("/scheme/fieldSet/set");
		JSONArray setJsonArray = new JSONArray();
		for (Element element : setlist) {
			JSONObject setjsonobject = new JSONObject();
			String set_id = element.getAttributeValue("setid");
			String set_name = element.getAttributeValue("desc");
			setjsonobject.put("set_id", set_id);
			setjsonobject.put("set_name", set_name);
			JSONArray itemJsonArray = new JSONArray();
			List<Element> itemlist =constantXml.getElementList("/scheme/fieldItem/item");
			for (Element element2 : itemlist) {
				JSONObject itemjsonobject = new JSONObject();
				String setid = element2.getAttributeValue("setid");
				if (StringUtils.equals(set_id, setid)) {
					String itemid = element2.getAttributeValue("itemid");
					//为了让从库结构修改的指标信息刷新业务字典后立即生效，指标信息从库机构从直接获得。
					FieldItem fieldItem = DataDictionary.getFieldItem(itemid, set_id);
					if (fieldItem == null) {
                        continue;
                    }
					String itemdesc = fieldItem.getItemdesc();
					String itemtype = fieldItem.getItemtype();
					String itemlength = fieldItem.getItemlength()+"";
					String decimalwidth = fieldItem.getDecimalwidth()+"";
					String codesetid = fieldItem.getCodesetid();
					String mustbe = element2.getAttributeValue("mustfill");
					String uniqueflag = element2.getAttributeValue("uniq");
					itemjsonobject.put("itemid", itemid);
					itemjsonobject.put("itemdesc", itemdesc);
					itemjsonobject.put("itemtype", itemtype);
					itemjsonobject.put("itemlength", itemlength);
					itemjsonobject.put("decimalwidth", decimalwidth);
					itemjsonobject.put("codesetid", codesetid);
					itemjsonobject.put("mustbe", mustbe);
					itemjsonobject.put("uniqueflag", uniqueflag);
					itemJsonArray.add(itemjsonobject);
				}
			}
			setjsonobject.put("fielditem_list", itemJsonArray);
			setJsonArray.add(setjsonobject);
		}
		jsonObject.put("set_list", setJsonArray);

		//2、是否包含照片
		Element photoElement= constantXml.getElement("/scheme/param");
		String photoFlag = photoElement.getAttributeValue("reportPhoto");
		if (StringUtils.equals("false", photoFlag)) {
			jsonObject.put("photo", "0");
		}else {
			jsonObject.put("photo", "1");
		}

		//3、人员状态指标
		Element statusElement= constantXml.getElement("/scheme/personStatus/personItemid");
		String psn_status = statusElement.getValue();
		jsonObject.put("psn_status", psn_status);

		//4、校验规则
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rowSet =dao.search("select checkId,checkname,checkField,condition,forcestate,valid from t_sys_asyn_validaterules where belong = 0 ");
		    JSONArray vfJsonArray = new JSONArray();
			while (rowSet.next()) {
				JSONObject vfJsonObject = new JSONObject();
				String vfid = rowSet.getString("checkId");
				String vfname = rowSet.getString("checkname");
				String vfmenus = rowSet.getString("checkField");
				String vfcond = rowSet.getString("condition");
				String vfforcestate = rowSet.getString("forcestate");
				String vfvalid = rowSet.getString("valid");
				vfJsonObject.put("vfid", vfid);
				vfJsonObject.put("vfname", vfname);
				vfJsonObject.put("vfmenus", vfmenus.replaceAll(",", "`"));
				vfJsonObject.put("vfcond", vfcond);
				vfJsonObject.put("vfforcestate", vfforcestate);
				vfJsonObject.put("vfvalid", vfvalid);
				vfJsonArray.add(vfJsonObject);
			}
			jsonObject.put("verify_list", vfJsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		FileUtil.createJsonFile(jsonObject.toString(), path, "menus");
	}
	/**
	 * 获得日志记录表的sql;
	 * @param value
	 * @return
	 */
	public String getRecordLogSql(String value) {
		String sql = "";
		DbWizard dbWizard = new DbWizard(conn);
		int flag = DbWizard.dbflag;
		////id UnitCode acceptTime Status
		StringBuffer sqlbuffer = new StringBuffer();
		sqlbuffer.append(" id,UnitCode,");
		sqlbuffer.append(Sql_switcher.dateToChar("acceptTime", "yyyy-MM-dd HH24:mi:ss"));
		sqlbuffer.append(" acceptTime, Status ");
		if("all".endsWith(value)) {//全部
			sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where 1=1 ";
		}else if ("today".endsWith(value)) {//今天
			if (flag==2) {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where acceptTime >= trunc(sysdate) and acceptTime < trunc(sysdate)+1";
			}else {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,acceptTime,getdate())=0";
			}

		}else if ("week".endsWith(value)) {//7天
			if (flag==2) {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where trunc(sysdate)-7 <= acceptTime";
			}else {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,acceptTime,getdate())<=7";
			}
		}else {//30天
			if (flag==2) {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where trunc(sysdate)-30 <= acceptTime";
			}else {
				sql = "select "+sqlbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,acceptTime,getdate())<=30";
			}
		}
		String prv = getUnitIdByBusi(userView);
        if (!"UN".equals(prv)&&StringUtils.isNotEmpty(prv)) {
            String[] prvArray = prv.split(",");
            StringBuffer buffer = new StringBuffer("(");
            for (int i = 0; i < prvArray.length; i++) {
              if (i!=0) {
                buffer.append(",");
              }
              buffer.append("'");
              buffer.append(prvArray[i]);
              buffer.append("'");
            }
            buffer.append(")");
            sql+=" and unitcode in "+buffer.toString();
        }
        if (prv==null||"".equals(prv)) {
            sql+=" and 1=2";
        }
		return sql;
	}
	/**
    * 获得当前用户的业务范围
    * @param userView
    * @return
    */
    private String getUnitIdByBusi(UserView userView){
        String orgPriv = null;
        StringBuffer priv = new StringBuffer("");
        try {
            orgPriv = userView.getUnitIdByBusi("4");
            if(orgPriv == null || orgPriv.trim().length() == 0)
                return "";
            orgPriv = orgPriv.replaceAll("`",",");
            String[] orgPrivs = orgPriv.split(",");
            for(int i = 0 ; i < orgPrivs.length ; i++){
                if (i>0) {
                    priv.append(",");
                }
                if ("UN".equals(orgPrivs[i].toUpperCase())) {
                    return "UN";
                }else if (orgPrivs[i].toUpperCase().startsWith("UN")&&orgPrivs[i].length()>2) {
                    priv.append(orgPrivs[i].substring(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priv.toString();
    }
	/**
	 * 根据fieldsetid 获得fieldsetdesc
	 * @param fieldsetid
	 * @return
	 */
    public String getFieldsetdesc(String fieldsetid) {
    	String fieldsetdesc="";
    	FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldsetid);
    	fieldsetdesc=fieldSet.getFieldsetdesc();
    	return fieldsetdesc;
    }
    /**
     * 根据单位编码获得单位描述
     * @param id 记录表的主键
     */
    public String getUnitDesc(String id,String type) {
		String UnitDesc = "";
		ContentDAO dao = new ContentDAO(conn);
		String sql = "select codeitemdesc from organization where codesetid='UN' and codeitemid = (select UnitCode from ";
		if (StringUtils.isNotEmpty(type)) {
		    sql +="t_sys_asyn_sendinfo ";
        }else {
            sql +="t_sys_asyn_acceptinfo ";
        }
		sql += " where id = '"+id+"')";
		RowSet rowSet = null;
		try {
			rowSet=dao.search(sql);
			if (rowSet.next()) {
				UnitDesc = rowSet.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return UnitDesc;
	}
    /**
     * 获取接收情况
     * @param id
     * @return
     */
    public String getSituation(String id,String type) {
		String situation = "";
		ContentDAO dao = new ContentDAO(conn);
		String sql = "select situation from ";
		if (StringUtils.isNotEmpty(type)) {
            sql +="t_sys_asyn_sendinfo ";
        }else {
            sql +="t_sys_asyn_acceptinfo ";
        }
        sql += "where id = '"+id+"'";
		RowSet rowSet = null;
		try {
			rowSet=dao.search(sql);
			if (rowSet.next()) {
				situation = rowSet.getString("situation");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return situation;
	}
    /**
     * 获得t_sys_asyn_record的对应的日志记录
     * @param id
     * @return
     */
    public ArrayList<ArrayList<String>> getRecord(String id) {
    	ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    	String sql ="select guidkey,name,extMemo from t_sys_asyn_record where Code =(select GUIdkey from t_sys_asyn_acceptinfo where id ='"+id+"')";
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rowSet = null;
    	ArrayList<String> listdata = null;
    	try {
			rowSet=dao.search(sql);
			while(rowSet.next()) {
				listdata = new ArrayList<String>();
				String guidkey = rowSet.getString("guidkey");
				String name = rowSet.getString("name");
				String extMemo = rowSet.getString("extMemo");
				listdata.add(guidkey);
				listdata.add(name);
				listdata.add(extMemo);
				list.add(listdata);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
    	return list;
	}
    /**
     * 获得上报的错误子集，以此来确定建立几个页签
     * @param id
     * @return
     */
    public ArrayList<String> getRecordSetid(String id,String type) {
    	ArrayList<String> list = new ArrayList<String>();
    	String sql ="select Setid from t_sys_asyn_record where mainGUIDkey = (select GUIdkey from ";
        if (StringUtils.isNotEmpty(type)) {
            sql +="t_sys_asyn_sendinfo ";
        }else {
            sql +="t_sys_asyn_acceptinfo ";
        }
    	sql += "where id =?) group by Setid ";
    	List list2 = new ArrayList();
    	list2.add(Integer.parseInt(id));
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rowSet = null;
    	try {
			rowSet=dao.search(sql,list2);
			while(rowSet.next()) {
			    list.add(rowSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
    	return list;
	}
    /**
     * 根据错误子集获得记录
     * @param setid
     * @return
     */
    public ArrayList<ArrayList<String>> getRecordList(String setid ,String id,String type) {
    	ArrayList<String> list = null;
    	ArrayList<ArrayList<String>> list2 = new ArrayList<ArrayList<String>>();
    	String sql = "select guidkey,name,extMemo from t_sys_asyn_record where Setid = '"+setid+"' and mainGUIDkey = (select GUIdkey from ";
    	if (StringUtils.isNotEmpty(type)) {
            sql+="t_sys_asyn_sendinfo where id =?)";
        } else {
            sql+="t_sys_asyn_acceptinfo where id =?)";
        }


    	List idlist = new ArrayList();
    	idlist.add(Integer.parseInt(id));
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rowSet = null;
    	try {
			rowSet=dao.search(sql,idlist);
			while(rowSet.next()) {
				list = new ArrayList<String>();
			    list.add(rowSet.getString(1));
			    list.add(rowSet.getString(2));
			    list.add(rowSet.getString(3));
			    list2.add(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
    	return list2;
	}
    /**
     * 获得导出excel总体情况的表述
     * @param recordSetidList
     * @return
     */
    public String  getExpression( ArrayList<String> recordSetidList,String id,String type) {
		StringBuffer wholebuffer = new StringBuffer("");
		StringBuffer subsetbuffer = new StringBuffer("");
		int wholeNumber = 0;
		for(int i = 0;i<recordSetidList.size();i++) {
			String setid = recordSetidList.get(i);
			String sheetName="";
			if ("PHOTO".equalsIgnoreCase(setid)) {
				sheetName="Photo";
			}else if("RULE".equalsIgnoreCase(setid)){
				sheetName="校验规则";
			}else {
				FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
				sheetName=fieldSet.getFieldsetdesc();//页签名称
			}
        	if (i!=0) {
        		subsetbuffer.append(";");
			}
        	subsetbuffer.append(sheetName);
        	subsetbuffer.append("校验了");
        	int subsetNumber = 0;
        	//根据setid获得错误的记录
        	ArrayList<ArrayList<String>> recordList = getRecordList(setid,id,type);
        	for (int j = 0; j < recordList.size(); j++) {
        		ArrayList<String> record = recordList.get(j);
        		String extMemo = record.get(2);
        		Document document = getDocument(extMemo);
        		 // 4.通过document对象获取xml文件的根节点
    	        Element rootElement = document.getRootElement();
    	        // 5.获取根节点下的子节点的List集合
    	        List<Element> bodyList = rootElement.getChildren();
    	        subsetNumber+=bodyList.size();
    	        wholeNumber +=bodyList.size();
			}
        	subsetbuffer.append(subsetNumber);
        	subsetbuffer.append("条,详情请看");
        	subsetbuffer.append(sheetName);
        	subsetbuffer.append("页签");
        	if (i==recordSetidList.size()-1) {
        		subsetbuffer.append("。");
			}
		}
		if (recordSetidList.size()>0) {
			wholebuffer.append("一共校验了");
			wholebuffer.append(wholeNumber);
			wholebuffer.append("条。其中");
			wholebuffer.append(subsetbuffer.toString());
		}else {
			wholebuffer.append("没有校验记录");
		}
		return wholebuffer.toString();
	}
    /**
     * 获得的校验规则表的主键id+1
     */
    public int getMaxCheckedid() {
		int checkId = 0;
		String sql = "select max(checkId)+1 as checkId from t_sys_asyn_validaterules";
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet = null;
		try {
			rowSet=dao.search(sql);
			if (rowSet.next()) {
				checkId = rowSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return checkId;
	}
    /**
     * 增加校验规则记录
     */
    public void addValidateRules(int checkId,String checkname,String checkField,String condition,int forcestate,int valid) {
		String sql = "insert into t_sys_asyn_validaterules values(?,?,?,?,?,?,?)";
		ArrayList list = new ArrayList();
		list.add(checkId);
		list.add(checkname);
		list.add(checkField);
		list.add(condition);
		list.add(forcestate);
		list.add(valid);
		list.add(0);
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.insert(sql, list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    /**
     * 修改校验规则记录
     */
    public void updataValidateRules(int checkId,String checkname,String checkField,String condition,int forcestate,int valid ) {
		String sql = "update  t_sys_asyn_validaterules set checkname=?, checkField =? , condition=? ,forcestate=? ,valid =? where checkId = ?";
		ArrayList list = new ArrayList();
		list.add(checkname);
		list.add(checkField);
		list.add(condition);
		list.add(forcestate);
		list.add(valid);
		list.add(checkId);
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.update(sql, list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    /**
     * 修改校验规则记录
     */
    public void updataValidateRules(int checkId,int forcestate,int valid,String condition) {
		String sql = "update  t_sys_asyn_validaterules set forcestate=? ,valid =? , condition=? where checkId = ?";
		ArrayList list = new ArrayList();
		if ("null".equals(condition)) {
			condition = null;
		}
		list.add(forcestate);
		list.add(valid);
		list.add(condition);
		list.add(checkId);
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.update(sql, list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    /**
     * 获得所有的人员库前缀
     * @return
     */
    public ArrayList<String> getDbPreList() {
		ArrayList<String> list = new ArrayList<String>();
		Connection connection =null;
		RowSet rowSet = null;
		try {
			connection = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(connection);
			String sql = "select pre from dbname";
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				list.add(rowSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeResource(rowSet);
			 PubFunc.closeResource(connection);
		}
		return list;
	}
    /**
     * 获得主集中代码型指标
     * @return
     */
    public ArrayList<HashMap<String,String>> getfieldDbnameList() {
    	ArrayList<HashMap<String,String>> setlist = new ArrayList<HashMap<String,String>>();
    	RowSet rowSet = null;
    	try {
    		String sql = "SELECT ITEMID,ITEMDESC from FIELDITEM where ITEMTYPE='A' AND CODESETID <> '0' AND FIELDSETID = 'A01' AND CODESETID <> 'UM' ";
        	ContentDAO dao = new ContentDAO(conn);
        	rowSet = dao.search(sql);
        	while (rowSet.next()) {
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("fieldName", itemid+":"+itemdesc);
				map.put("fieldValue", itemid);
				setlist.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
    	return setlist;
	}
    /**
     * 获得设置人员库映射的grid panel 的store
     * @param fieldsetid
     * @return
     */
    public ArrayList<HashMap<String,String>> getfieldcodeList(String fielditem) {
    	ArrayList<HashMap<String,String>> fieldlist = new ArrayList<HashMap<String,String>>();
    	if(fielditem!=null&&!"".equals(fielditem)&&!"undefined".equals(fielditem)) {
    		FieldItem fItem = DataDictionary.getFieldItem(fielditem);
        	String codesetid=fItem.getCodesetid();
    		ArrayList list = AdminCode.getCodeItemList(codesetid);
    		String Str_value=getStr_Value();
    		Document document = null;
    		List<Element> fielddbnamelist = new ArrayList<Element>();
    		if (null!=Str_value&&!"".equals(Str_value)) {
    			document = getDocument(Str_value);
    			// 4.通过document对象获取xml文件的根节点
    	        Element rootElement = document.getRootElement();
    	        // 5.获取根节点下的子节点的List集合
    	        List<Element> bodyList = rootElement.getChildren();
    	        if (bodyList.size()>=6) {
    	        	Element fielddbnameelement=bodyList.get(5);
    				fielddbnamelist=fielddbnameelement.getChildren();
    	        }
    		}
    		for (int i = 0;i<list.size();i++) {
    			CodeItem codeItem = (CodeItem) list.get(i);
    			String codeitemid=codeItem.getCodeitem();
    			String codeitemdesc=codeItem.getCodename();
    			HashMap<String, String> map = new HashMap<String, String>();
    			map.put("codeitemid", codeitemid);
    			map.put("codeitemdesc", codeitemdesc);
    			if (fielddbnamelist.size()>0) {
    				String personItemid=fielddbnamelist.get(0).getValue();
    				if (personItemid.equalsIgnoreCase(fielditem)) {
    					for(int j=1;j<fielddbnamelist.size();j++) {
    						Element fieldelement = fielddbnamelist.get(j);
    						String pre = fieldelement.getAttributeValue("pre");
    						String personMapping = fieldelement.getAttributeValue("personMapping");
    						String[] personMappingDbname= personMapping.split(",");
    						for (int k = 0; k < personMappingDbname.length; k++) {
								String personMappingVlaue = personMappingDbname[k];
								if (StringUtils.equals(codeitemid, personMappingVlaue)) {
	    							map.put("dbnamepre", pre);
	    							map.put("dbname", pre);
	    							break;
	    						}
							}
    					}
    				}
    			}
    			fieldlist.add(map);
    		}
    	}
		return fieldlist;
	}
    /**
     * 获得所有人员库的list
     * @return
     */
    public ArrayList<HashMap<String,String>> getDbnameList() {
    	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    	RowSet rowSet = null;
    	try {
			String sql = "select dbname,pre from dbname";
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				String dbname = rowSet.getString("dbname");
				String pre = rowSet.getString("pre");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("dbnameCombox", dbname);
				map.put("dbnamepreCombox", pre);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return list;
	}
    /**
     * 获取全部的备选指标
     * @param parentId
     * @return
     */
    @SuppressWarnings("unchecked")
	public ArrayList<LinkedHashMap<String, Object>> getALLAlternativeList() {
    	ArrayList<LinkedHashMap<String, Object>> alternativeList =new ArrayList<LinkedHashMap<String,Object>>();
    	RowSet rowSet = null;
    	try {
    		ContentDAO dao = new ContentDAO(conn);
    		//获取指标集下面的指标
    			String sql = "SELECT a.fieldsetid,a.itemid,a.itemdesc,b.fieldSetDesc FROM fielditem a LEFT JOIN fieldSet b on a.fieldsetid = b.fieldSetId WHERE a.useflag = '1' ORDER BY a.fieldsetid,a.displayid,a.itemid";
    			rowSet = dao.search(sql);
    			String oldFieldsetid = null;
    			LinkedHashMap<String, Object> fieldsetmap = null;
    			ArrayList<LinkedHashMap<String, Object>> childrenList =null;
    			LinkedHashMap<String, Object> fielditemmap = null;
    			while (rowSet.next()) {
    				fieldsetmap = new LinkedHashMap<String, Object>();
    				String fieldsetid = rowSet.getString("fieldsetid");
    				FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldsetid);
                    String fieldSetType= fieldSet.getInfoGroup().getPrefix();
                    if ("A".equals(fieldSetType)||"B".equals(fieldSetType)||"K".equals(fieldSetType)) {
                        String fieldSetDesc = rowSet.getString("fieldSetDesc");
                        String itemid = rowSet.getString("itemid");
                        String itemdesc = rowSet.getString("itemdesc");
                        if (!fieldsetid.equalsIgnoreCase(oldFieldsetid)) {
                            childrenList =new ArrayList<LinkedHashMap<String,Object>>();
                        }else {
                            childrenList = (ArrayList<LinkedHashMap<String, Object>>) alternativeList.get(alternativeList.size()-1).get("children");
                        }
                        if ("A01".equalsIgnoreCase(fieldsetid)&&oldFieldsetid==null) {
                            fielditemmap = new LinkedHashMap<String, Object>();
                            FieldItem fieldItem = DataDictionary.getFieldItem("B0110");
                            fielditemmap.put("id", "B0110");
                            fielditemmap.put("text", fieldItem.getItemdesc());
                            fielditemmap.put("leaf", true);
                            fielditemmap.put("checked", false);
                            childrenList.add(fielditemmap);
                            fielditemmap = new LinkedHashMap<String, Object>();
                            fieldItem = DataDictionary.getFieldItem("E0122");
                            fielditemmap.put("id", "E0122");
                            fielditemmap.put("text", fieldItem.getItemdesc());
                            fielditemmap.put("leaf", true);
                            fielditemmap.put("checked", false);
                            childrenList.add(fielditemmap);
                            fielditemmap = new LinkedHashMap<String, Object>();
                            fieldItem = DataDictionary.getFieldItem("E01A1");
                            fielditemmap.put("id", "E01A1");
                            fielditemmap.put("text", fieldItem.getItemdesc());
                            fielditemmap.put("leaf", true);
                            fielditemmap.put("checked", false);
                            childrenList.add(fielditemmap);
                        }
                        if (!"E0122".equalsIgnoreCase(itemid)) {
                            fielditemmap = new LinkedHashMap<String, Object>();
                            fielditemmap.put("id", itemid);
                            fielditemmap.put("text", itemdesc);
                            fielditemmap.put("leaf", true);
                            fielditemmap.put("checked", false);
                            childrenList.add(fielditemmap);
                        }
                        if (!fieldsetid.equalsIgnoreCase(oldFieldsetid)) {
                            fieldsetmap.put("id", fieldsetid);
                            fieldsetmap.put("text", fieldSetDesc);
                            fieldsetmap.put("leaf", false);
                            fieldsetmap.put("checked", false);
                            fieldsetmap.put("children", childrenList);
                            alternativeList.add(fieldsetmap);
                        }else {
                            alternativeList.get(alternativeList.size()-1).put("children", childrenList);
                        }
                        oldFieldsetid=fieldsetid;
                    }
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
    	return alternativeList;
	}
    /**
     * 获取备选指标列表
     * @param parentId
     * @return
     */
    public ArrayList<LinkedHashMap<String, Object>> getAlternativeList(String parentId) {
    	ArrayList<LinkedHashMap<String, Object>> alternativeList =new ArrayList<LinkedHashMap<String,Object>>();
    	RowSet rowSet = null;
    	try {
    		ContentDAO dao = new ContentDAO(conn);
    		if ("root".equals(parentId)) {//获取指标集列表
    			String sql = "select fieldsetid,fieldsetdesc from fieldset order by fieldsetid ";
    			rowSet = dao.search(sql);
    			while (rowSet.next()) {
					String fieldsetid = rowSet.getString("fieldsetid");
					String fieldsetdesc = rowSet.getString("fieldsetdesc");
					LinkedHashMap<String, Object> fieldsetmap = new LinkedHashMap<String, Object>();
					fieldsetmap.put("id", fieldsetid);
					fieldsetmap.put("text", fieldsetdesc);
					//fieldsetmap.put("checked", false);
					alternativeList.add(fieldsetmap);
				}
    		}else {//获取指标集下面的指标
    			String sql = "select itemid,itemdesc from fielditem where fieldsetid = '"+parentId+"'";
    			rowSet = dao.search(sql);
    			if ("A01".equalsIgnoreCase(parentId)) {
    				LinkedHashMap<String, Object> fielditemmap = new LinkedHashMap<String, Object>();
    				FieldItem fieldItem = DataDictionary.getFieldItem("B0110");
    				fielditemmap.put("id", "B0110");
					fielditemmap.put("text", fieldItem.getItemdesc());
					fielditemmap.put("leaf", true);
					//fielditemmap.put("checked", false);
					alternativeList.add(fielditemmap);
					fielditemmap = new LinkedHashMap<String, Object>();
					fieldItem = DataDictionary.getFieldItem("E0122");
    				fielditemmap.put("id", "E0122");
					fielditemmap.put("text", fieldItem.getItemdesc());
					fielditemmap.put("leaf", true);
					//fielditemmap.put("checked", false);
					alternativeList.add(fielditemmap);
					fielditemmap = new LinkedHashMap<String, Object>();
					fieldItem = DataDictionary.getFieldItem("E01A1");
    				fielditemmap.put("id", "E01A1");
					fielditemmap.put("text",  fieldItem.getItemdesc());
					fielditemmap.put("leaf", true);
					//fielditemmap.put("checked", false);
					alternativeList.add(fielditemmap);
				}
    			while (rowSet.next()) {
					String itemid = rowSet.getString("itemid");
					String itemdesc = rowSet.getString("itemdesc");
					if (!"E0122".equalsIgnoreCase(itemid)) {
						LinkedHashMap<String, Object> fielditemmap = new LinkedHashMap<String, Object>();
						fielditemmap.put("id", itemid);
						fielditemmap.put("text", itemdesc);
						fielditemmap.put("leaf", true);
						//fielditemmap.put("checked", false);
						alternativeList.add(fielditemmap);
					}
				}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
    	return alternativeList;
	}
    /**
     * 获取已选指标列表
     * @param parentId
     * @return
     */
    public ArrayList<LinkedHashMap<String, Object>> getSelectedList(String parentId) {
    	ArrayList<LinkedHashMap<String, Object>> selectedList =new ArrayList<LinkedHashMap<String,Object>>();
    	try {
    		String Str_value=getStr_Value();
			Document document = null;
			if (null!=Str_value&&!"".equals(Str_value)) {
				document=getDocument(Str_value);
				 // 4.通过document对象获取xml文件的根节点
	            Element rootElement = document.getRootElement();
	            // 5.获取根节点下的子节点的List集合
	            List<Element> bodyList = rootElement.getChildren();
	            if ("root".equals(parentId)) {
	            	Element fieldElement=bodyList.get(1);
	 	            // 5.获取根节点下的子节点的List集合
	 	            List arrayList = (List) fieldElement.getChildren();
	 			    for(int i=0;i<arrayList.size();i++) {
	 			    	Element elment = (Element) arrayList.get(i);
	 			    	String itemid=elment.getAttributeValue("setid");
	 			    	FieldSet fieldSet = DataDictionary.getFieldSetVo(itemid);
	 			    	String itemdesc=fieldSet.getFieldsetdesc();
	 			    	LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
	 			    	map.put("id", itemid);
	 			    	map.put("text", itemdesc);
	 			    	selectedList.add(map);
	 			    }
				}else {
					Element fieldElement=bodyList.get(2);
	 	            // 5.获取根节点下的子节点的List集合
	 	            List arrayList = (List) fieldElement.getChildren();
	 			    for(int i=0;i<arrayList.size();i++) {
	 			    	Element elment = (Element) arrayList.get(i);
	 			    	String setid=elment.getAttributeValue("setid");
	 			    	if (setid.equalsIgnoreCase(parentId)) {
	 			    		String itemid=elment.getAttributeValue("itemid");
		 			    	String itemdesc=elment.getAttributeValue("itemdesc");
		 			    	LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		 			    	map.put("id", itemid);
		 			    	map.put("text", itemdesc);
		 			    	map.put("leaf", true);
		 			    	selectedList.add(map);
						}
	 			    }
				}

			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return selectedList;
	}
    /**
     * 获取全部的已选指标列表
     * @param parentId
     * @return
     */
    public ArrayList<LinkedHashMap<String, Object>> getAllSelectedList() {
    	ArrayList<LinkedHashMap<String, Object>> selectedList =new ArrayList<LinkedHashMap<String,Object>>();
    	try {
    		String Str_value=getStr_Value();
			Document document = null;
			if (null!=Str_value&&!"".equals(Str_value)) {
				document=getDocument(Str_value);
				 // 4.通过document对象获取xml文件的根节点
	            Element rootElement = document.getRootElement();
	            // 5.获取根节点下的子节点的List集合
	            List<Element> bodyList = rootElement.getChildren();
            	Element fieldElement=bodyList.get(1);
 	            // 5.获取根节点下的子节点的List集合
 	            List arrayList = (List) fieldElement.getChildren();
 			    for(int i=0;i<arrayList.size();i++) {
 			    	Element elment = (Element) arrayList.get(i);
 			    	String setid=elment.getAttributeValue("setid");
 			    	FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
 			    	String setdesc=fieldSet.getFieldsetdesc();
					fieldElement=bodyList.get(2);
	 	            // 5.获取根节点下的子节点的List集合
	 	            List arrayListFielditem = (List) fieldElement.getChildren();
	 	            ArrayList<LinkedHashMap<String, Object>> childrenList = new ArrayList<LinkedHashMap<String,Object>>();
	 			    for(int j=0;j<arrayListFielditem.size();j++) {
	 			    	Element elmentFielditem = (Element) arrayListFielditem.get(j);
	 			    	String fieldsetId=elmentFielditem.getAttributeValue("setid");
	 			    	if (setid.equalsIgnoreCase(fieldsetId)) {
	 			    		String itemid=elmentFielditem.getAttributeValue("itemid");
	 			    		FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
	 			    		if (null==fieldItem) {
                                continue;
                            }
		 			    	String itemdesc=elmentFielditem.getAttributeValue("itemdesc");
		 			    	LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		 			    	map.put("id", itemid.toUpperCase());
		 			    	map.put("text", itemdesc);
		 			    	map.put("leaf", true);
		 			    	map.put("checked", false);
		 			    	childrenList.add(map);
						}
	 			    }
	 			    if (childrenList.size()>0) {
	 			        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
	                    map.put("id", setid.toUpperCase());
	                    map.put("text", setdesc);
	                    map.put("checked", false);
	                    map.put("children", childrenList);
	                    selectedList.add(map);
                    }
 			    }
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return selectedList;
	}
    /**
     * 获得所有顶级的单位
     */
    public ArrayList<LinkedHashMap<String, Object>> getAllUnJsonList() {
    	ArrayList<LinkedHashMap<String, Object>> jsonList = new ArrayList<LinkedHashMap<String,Object>>();
    	RowSet rowSet =null;
    	try {
			StringBuffer sqlbuff = new StringBuffer("");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sqlbuff.append("SELECT codesetid,codeitemid,codeitemdesc,childid,guidkey FROM organization WHERE codesetid = 'UN' AND ");
			sqlbuff.append(Sql_switcher.dateValue(format.format(new Date())));
			//sqlbuff.append(" BETWEEN start_date AND end_date AND parentid <> codeitemid AND parentid in ");
			//sqlbuff.append(parentid);
			sqlbuff.append(" BETWEEN start_date AND end_date  AND parentid = codeitemid");
			sqlbuff.append(" ORDER BY a0000,codeitemid");
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sqlbuff.toString());
			while (rowSet.next()) {
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				String guidkey = rowSet.getString("guidkey");
				String codesetid = rowSet.getString("codesetid");
				String codeitemid =  rowSet.getString("codeitemid");
				String codeitemdesc =  rowSet.getString("codeitemdesc");
				map.put("id",guidkey);
				map.put("unitcode",codeitemid);
				map.put("text",codeitemdesc);
				map.put("icon","/images/unit.gif");
				map.put("itemdesc",codeitemdesc);
				map.put("qtip","ID:"+codeitemid);
				map.put("checked",false);
				//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,codeitemid);
	    		if(!"".equals(tempcodeitemid)) {
	    			map.put("leaf", false);
	    		    map.put("children", getUnitList(codeitemid));
	    		}else{
	    			map.put("leaf", true);

	    		}
	    		jsonList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
    	return jsonList;
	}
    /**
     * 获得所有的单位
     */
    public ArrayList<LinkedHashMap<String, Object>> getUnitList(String parentid) {
    	ArrayList<LinkedHashMap<String, Object>> jsonList = new ArrayList<LinkedHashMap<String,Object>>();
    	RowSet rowSet =null;
    	try {
			StringBuffer sqlbuff = new StringBuffer("");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sqlbuff.append("SELECT codesetid,codeitemid,codeitemdesc,childid,guidkey FROM organization WHERE codesetid = 'UN' AND ");
			sqlbuff.append(Sql_switcher.dateValue(format.format(new Date())));
			sqlbuff.append(" BETWEEN start_date AND end_date AND parentid <> codeitemid AND parentid ='");
			sqlbuff.append(parentid);
			sqlbuff.append("' ORDER BY a0000,codeitemid");
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sqlbuff.toString());
			while (rowSet.next()) {
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				String guidkey = rowSet.getString("guidkey");
				String codesetid = rowSet.getString("codesetid");
				String codeitemid =  rowSet.getString("codeitemid");
				String codeitemdesc =  rowSet.getString("codeitemdesc");
				map.put("id",guidkey);
				map.put("text",codeitemdesc);
				map.put("icon","/images/unit.gif");
				map.put("itemdesc",codeitemdesc);
				map.put("qtip","ID:"+codeitemid);
				map.put("checked",false);
				//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,codeitemid);
	    		if(!"".equals(tempcodeitemid)) {
	    			map.put("leaf", false);
	    		    map.put("children", getUnitList(codeitemid));
	    		}else{
	    			map.put("leaf", true);

	    		}
	    		jsonList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
    	return jsonList;
	}
    /**
     * 获取顶级机构的id
     * @return
     */
    public String getTopUnId() {
		StringBuffer codeitemid= new StringBuffer(" (");
		RowSet rowSet = null;
		try {
			String sql = "select codeitemid from organization where parentid = codeitemid";
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(sql);
			int i = 0;
			while (rowSet.next()) {
				if (i!=0) {
					codeitemid.append(",");
				}
				codeitemid.append("'");
				codeitemid.append(rowSet.getString("codeitemid"));
				codeitemid.append("'");
				i++;
			}
			codeitemid.append(") ");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return codeitemid.toString();
	}
    /**
     * 判断是否有孩子节点
     * @param codesetid
     * @param codeitemid
     * @return
     */
    public String getTempCodeItemid(String codesetid,String codeitemid) {
    	String str = "";
		ResultSet rset = null;
    	try{
    		ContentDAO dao = new ContentDAO(conn);
    		StringBuffer sb = new StringBuffer("");
    		sb.append("select codeitemid from ");
			if ("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)) {
				sb.append("organization where codeitemid in (select codeitemid from organization where parentid='"+codeitemid+"' and parentid<>codeitemid) and (codesetid='");
				if ("UN".equals(codesetid)) {
					sb.append("UN')");
				} else if ("UM".equals(codesetid)) {
					sb.append("UM' or codesetid='UN')");
				} else if ("@K".equals(codesetid)) {
					sb.append("@K' or codesetid='UM' or codesetid='UN')");
				}
				 String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				 sb.append(" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+now);
				 sb.append(" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+now);
				 sb.append(" order by a0000,codeitemid");
			}
    		rset = dao.search(sb.toString());
    		if(rset.next()){
    			str = rset.getString("codeitemid")==null?"":rset.getString("codeitemid");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    	  PubFunc.closeResource(rset);
    	}
		return str;
    }
    /**
	 * 查询出t_sys_asyn_scheme表里面的UnitCode存入arraylist中去。用于分布式上报 定义数据规范
	 * @return
	 */
	public ArrayList<String> getUnitCodeList() {
		ArrayList<String> unitCodeList = new ArrayList<String>();
		String sql="select unitguid from t_sys_asyn_scheme";
		ContentDAO dao = new ContentDAO(conn);
		RowSet frowset = null;
		try {
			frowset = dao.search(sql);
			while (frowset.next()) {
				unitCodeList.add(frowset.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(frowset);
		}
		return unitCodeList;
	}
	/**
	 * 根据方案id 保存上报的负责人
	 * @param schemeid
	 * @param leadername
	 */
	public void saveSelectLeader(String schemeid,String leadername) {
		try {
			String sql = " update t_sys_asyn_scheme set reportleader = '"+leadername+"' where schemeid = "+Integer.parseInt(schemeid);
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
     * 根据方案id 删除上报的负责人
     * @param schemeid
     */
    public void delLeader(String schemeid) {
        try {
            String sql = " update t_sys_asyn_scheme set reportleader = null where schemeid = "+Integer.parseInt(schemeid);
            ContentDAO dao = new ContentDAO(conn);
            dao.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
