package com.hjsj.hrms.module.system.regothersys;

import com.hjsj.hrms.service.core.util.ServiceType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * 集成注册服务Bo类
 * @author cuibl
 *
 */
public class SysRegBo{

	// 日志
	private Category log = Category.getInstance(getClass().getName());
	
	/**
	 * 初始化系统认证列表
	 * @param userView userView对象
	 * @param frameconn 数据库链接
	 * @return
	 */
	public String initSysRegList(UserView userView,Connection frameconn) {
		// 表格列头集合
		ArrayList columns = new ArrayList();
		// 创建列对象
		ColumnsInfo id = new ColumnsInfo();
		id.setColumnId("id");
		//id.setColumnDesc("编号");
		id.setColumnType("A");
		id.setColumnWidth(0);
		id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载,不显示
		columns.add(id);
		ColumnsInfo syscode = new ColumnsInfo();
		syscode.setColumnId("syscode");
		syscode.setColumnDesc("系统编码");
		syscode.setColumnType("A");
		syscode.setColumnWidth(80);
		columns.add(syscode);
		ColumnsInfo sysname = new ColumnsInfo();
		sysname.setColumnId("sysname");
		sysname.setColumnDesc("系统名称");
		sysname.setColumnType("A");
		sysname.setColumnWidth(100);
		columns.add(sysname);
		ColumnsInfo sysetoken = new ColumnsInfo();
		sysetoken.setColumnId("sysetoken");
		sysetoken.setColumnDesc("认证标识");
		sysetoken.setColumnType("A");
		sysetoken.setColumnWidth(300);
		columns.add(sysetoken);
		ColumnsInfo sysdesc = new ColumnsInfo();
		sysdesc.setColumnId("sysdesc");
		sysdesc.setColumnDesc("应用描述");
		sysdesc.setColumnType("M");
		sysdesc.setColumnWidth(300);
		columns.add(sysdesc);
		ColumnsInfo valid = new ColumnsInfo();
		valid.setColumnId("valid");
		valid.setColumnDesc("状态");
		valid.setColumnType("A");
		//valid.setCodesetId("");//代码型,需要转换
		valid.setColumnWidth(80);
		//valid.setTextAlign("center");
		valid.setRendererFunc("sysRegList_me.transState");
		columns.add(valid);
		ColumnsInfo logView = new ColumnsInfo();
		logView.setColumnId("syscode");
		logView.setColumnDesc("查看日志");
		logView.setColumnType("A");
		logView.setColumnWidth(80);
		logView.setTextAlign("center");
		logView.setRendererFunc("sysRegList_me.transDownloadLog");
		columns.add(logView);
		//指定sql语句及排序
		String sql = "select id,syscode,sysname,sysetoken,sysdesc,valid from t_sys_reg_services ";
		String order = " order by id ";
		//创建按钮集合
		ButtonInfo splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//页面的显示按钮之间的分割线
		ArrayList buttonList = new ArrayList();
		if (userView.isSuper_admin() || userView.hasTheFunction("300471")){
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.insert"),"sysRegList_me.insertSysReg"));
		}
		if (userView.isSuper_admin() || userView.hasTheFunction("300472")){
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"),"sysRegList_me.deleteSysReg"));
		}
		//buttonList.add(splitButton);
		//buttonList.add(splitButton);
		if (userView.isSuper_admin() || userView.hasTheFunction("300473")){
			buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.edit"),"sysRegList_me.toEditSysReg"));
		}
		/**
		 * 参数1：subModuleId （表格唯一标识,格式为 模块_功能_编号,必须保证整个程序内唯一。此id作用比较多，如果重复，会导致数据混乱）
		 * 参数2：表格列头集合
		 * 参数3：页面元素 id前缀，如果所在页面有两个或以上表格，不能重复
		 * 参数4：userView对象
		 * 参数5：connection
		 */
		TableConfigBuilder builder = new TableConfigBuilder("sys_reg_services", columns, "sysRegGridTable", userView,frameconn);
		builder.setTitle("集成服务注册");
		builder.setDataSql(sql);
		builder.setOrderBy(order);
		// builder.setScheme(true);
		builder.setSetScheme(true);
		builder.setShowPublicPlan(true);
		builder.setSelectable(true);
		builder.setAutoRender(true);
		builder.setTableTools(buttonList);
		String tableConfig = builder.createExtTableConfig();
		return tableConfig;
	}
	
	/**
	 * 删除认证系统
	 * @param datalist 选中系统的id集合
	 */
	public void deleteSys(ArrayList<String> datalist) {
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			List<String> list = new ArrayList<String>();
			String sql = "delete from t_sys_reg_services where id = ?";
			for (String id : datalist) {
				list.clear();//清空上次删除数据内容
				list.add(id);
				dao.delete(sql, list);
			}
		} catch (GeneralException e1) {
			e1.printStackTrace();
			log.error("删除认证系统:创建conn链接出错!");
			log.error(e1);
		} catch (SQLException e) {
			log.error("删除认证系统:执行删除sql出错!");
			log.error(e);
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(conn);
		}
	}
	
	/**
	 * 拼接服务选项XML并返回
	 * @param serviceList 服务启用项列表
	 * @param orgExper 机构过滤条件
	 * @param postExper 岗位过滤条件
	 * @param empExper 人员过滤条件
	 * @return 数据库存储的XML格式
	 */
	public String spliceXML(ArrayList<String> serviceList,String orgExper,String postExper,String empExper) {
		StringBuffer strXML = new StringBuffer();
		String orgTemp = "0";
		String postTemp = "0";
		String empTemp = "0";
		String matterTemp = "0";
		String boardTemp = "0";
		String warnTemp = "0";
		String staticsTemp = "0";
		String reportTemp = "0";
		String processTemp = "0";
		String kqInfoTemp = "0";
		String holidayTemp = "0";
		String updholidayTemp = "0";
		String userEtokenTemp = "0";
		for (String str : serviceList) {
			if(ServiceType.ORG.equalsIgnoreCase(str)) {
				orgTemp = "1";
			}else if(ServiceType.POST.equalsIgnoreCase(str)) {
				postTemp = "1";
			}else if(ServiceType.EMP.equalsIgnoreCase(str)) {
				empTemp = "1";
			}else if(ServiceType.MATTER.equalsIgnoreCase(str)) {
				matterTemp = "1";
			}else if(ServiceType.BOARD.equalsIgnoreCase(str)) {
				boardTemp = "1";
			}else if(ServiceType.WARN.equalsIgnoreCase(str)) {
				warnTemp = "1";
			}else if(ServiceType.STATICS.equalsIgnoreCase(str)) {
				staticsTemp = "1";
			}else if(ServiceType.REPORT.equalsIgnoreCase(str)) {
				reportTemp = "1";
			}else if(ServiceType.PROCESS.equalsIgnoreCase(str)) {
				processTemp = "1";
			}else if(ServiceType.KQINFO.equalsIgnoreCase(str)) {
				kqInfoTemp = "1";
			}else if(ServiceType.GET_HOLIDAY.equalsIgnoreCase(str)) {
				holidayTemp = "1";
			}else if(ServiceType.UPD_HOLIDAY.equalsIgnoreCase(str)) {
				updholidayTemp = "1";
			}else if(ServiceType.USERETOKEN.equalsIgnoreCase(str)) {
				userEtokenTemp = "1";
			}
		}
		strXML.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><services>");
		strXML.append("<service id =\"getOrg\"><valid>"+ orgTemp +"</valid><sqlscope>"+ orgExper +"</sqlscope></service>");
		strXML.append("<service id =\"getPost\"><valid>"+ postTemp +"</valid><sqlscope>"+ postExper +"</sqlscope></service>");
		strXML.append("<service id =\"getEmp\"><valid>"+ empTemp +"</valid><sqlscope>"+ empExper +"</sqlscope></service>");
		strXML.append("<service id =\"getMatter\"><valid>"+ matterTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getBoard\"><valid>"+ boardTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getWarn\"><valid>"+ warnTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getStatics\"><valid>"+ staticsTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getReport\"><valid>"+ reportTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"syncProcess\"><valid>"+ processTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getKqInfo\"><valid>"+ kqInfoTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getHoliday\"><valid>"+ holidayTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"updHoliday\"><valid>"+ updholidayTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("<service id =\"getUserEtoken\"><valid>"+ userEtokenTemp +"</valid><sqlscope></sqlscope></service>");
		strXML.append("</services>");
		return strXML.toString();
	}
	
	/**
	 * 验证系统编码是否唯一
	 * @param opt 新增(add)/修改(edit)
	 * @param checkCode 系统编码
	 * @param editSysEtoken 认证标识
	 * @return
	 */
	public String checkCode(String opt,String checkCode,String editSysEtoken) {
		String result = "true";
		Connection conn = null;
		RowSet rs = null;
		try {
			String selSql = "SELECT sysetoken from t_sys_reg_services where sysCode = ?";
			List<String> valList = new ArrayList<String>();
			valList.add(checkCode);
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(selSql,valList);
			while(rs.next()) {
				if("add".equals(opt)) {
					return "code";
				}else if("edit".equals(opt)) {
					String sysetoken = rs.getString("sysetoken");
					if (!sysetoken.equals(editSysEtoken)) {
						return "code";
					}
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			log.error("验证系统编码是否唯一:创建conn链接出错!");
			log.error(e);
			result = "false";
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("验证系统编码是否唯一:执行查询sql出错!");
			log.error(e);
			result = "false";
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	
	/**
	 * 验证系统名称是否唯一
	 * @param opt 新增(add)/修改(edit)
	 * @param checkName 系统名称
	 * @param editSysEtoken 认证标识
	 * @return
	 */
	public String checkName(String opt,String checkName,String editSysEtoken) {
		String result = "true";
		Connection conn = null;
		RowSet rs = null;
		try {
			String selSql = "SELECT sysetoken from t_sys_reg_services where sysName = ?";
			List<String> valList = new ArrayList<String>();
			valList.add(checkName);
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(selSql,valList);
			while(rs.next()) {
				if("add".equals(opt)) {
					result = "name";
					return result;
				}else if("edit".equals(opt)) {
					String sysetoken = rs.getString("sysetoken");
					if (!sysetoken.equals(editSysEtoken)) {
						result = "name";
						return result;
					}
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			log.error("验证系统名称是否唯一:创建conn链接出错!");
			log.error(e);
			result = "false";
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("验证系统名称是否唯一:执行查询sql出错!");
			log.error(e);
			result = "false";
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	/**
	 * 添加认证系统
	 * @param map 新增系统的数据
	 * @param serviceList 服务列表数据
	 * @return 新增是否成功
	 */
	public boolean addSys(Map<String, String> map,ArrayList<String> serviceList) {
		boolean result = true;
		//新增认证系统,准备数据开始
		String syscode = map.get("sysCode");
		String sysname = map.get("sysName");
		String sysetoken = map.get("etoken");
		String sysdesc = map.get("description");
		String valid = map.get("valid");
		String dynaCode = map.get("dynaCode");
		String orgExper = map.get("orgExper");
		String postExper = map.get("postExper");
		String empExper = map.get("empExper");
		
		orgExper=orgExper!=null&&orgExper.trim().length()>0?orgExper:"";
		orgExper=SafeCode.decode(orgExper);//将传来的字符串进行解码
		postExper=postExper!=null&&postExper.trim().length()>0?postExper:"";
		postExper=SafeCode.decode(postExper);//将传来的字符串进行解码
		empExper=empExper!=null&&empExper.trim().length()>0?empExper:"";
		empExper=SafeCode.decode(empExper);//将传来的字符串进行解码
		
		String strXML = this.spliceXML(serviceList, orgExper, postExper, empExper);
		
		Date date = new Date();
		Timestamp timestamp = DateUtils.getTimestamp(date);//获取当天时间的时间戳
		//新增认证系统,准备数据完毕
		
		//准备数据库
		Connection conn = null;
		RowSet rs = null;
		try {
			String id = "";
			String selSql = "SELECT MAX(id)+1 id from t_sys_reg_services";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(selSql);
			if(rs.next()) {
				id = rs.getString("id")==null?"1":rs.getString("id");
			}
			if(StringUtils.isBlank(id)) {
				log.error("新增认证系统:获取id最大值出错!");
				return false;
			}
			ArrayList<Object> insertList = new ArrayList<Object>();
			insertList.add(Integer.parseInt(id));
			insertList.add(syscode);
			insertList.add(sysname);
			insertList.add(sysetoken);
			insertList.add(sysdesc);
			insertList.add(Integer.parseInt(valid));
			insertList.add(timestamp);
			insertList.add(strXML);
			insertList.add(Integer.parseInt(dynaCode));
			String insertSql = "INSERT INTO t_sys_reg_services(id,syscode,sysname,sysetoken,sysdesc,valid,createtime,servicedetail,validateway) VALUES(?,?,?,?,?,?,?,?,?)";
			dao.insert(insertSql, insertList);
		} catch (GeneralException e) {
			e.printStackTrace();
			log.error("新增认证系统:创建conn链接出错!");
			log.error(e);
			result = false;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("新增认证系统:执行新增sql出错!");
			log.error(e);
			result = false;
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	/**
	 * 解析serviceXML并将生成的List返回
	 * @param str
	 * @return
	 */
	public ArrayList parseXML(String str) {
		ArrayList<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		try {
			Document doc = PubFunc.generateDom(str);
			XPath xpath =null;
			xpath = XPath.newInstance("/services/service");
			List<Element> selectNodes = xpath.selectNodes(doc);
			for (Element element : selectNodes) {
				Map<String, String> map = new HashMap<String, String>();
				Attribute attribute = element.getAttribute("id");
				String value = attribute.getValue();
				String valid = element.getChild("valid").getText();
				map.put("serviceMethod", value);
				map.put("valid", valid);
				if("getOrg".equalsIgnoreCase(value) || "getPost".equalsIgnoreCase(value) || "getEmp".equalsIgnoreCase(value)) {
					String sqlscope = element.getChild("sqlscope").getText();
					map.put("sqlscope", sqlscope);
				}
				resultList.add(map);
			}
		} catch (Exception e) {
			log.error("解析XML出错!");
			log.error(e);
			e.printStackTrace();
		}
		return resultList;
	}
	
	/**
	 * 修改认证系统
	 */
	public boolean editSys(Map<String, String> map,ArrayList<String> serviceList) {
		boolean result = true;
		//修改认证系统,准备数据开始
		String id = map.get("id");
  		String syscode = map.get("sysCode");
		String sysname = map.get("sysName");
		String sysdesc = map.get("description");
		String valid = map.get("valid");
		String dynaCode = map.get("dynaCode");
		String orgExper = map.get("orgExper");
		String postExper = map.get("postExper");
		String empExper = map.get("empExper");

		orgExper=orgExper!=null&&orgExper.trim().length()>0?orgExper:"";
		orgExper=SafeCode.decode(orgExper);//将传来的字符串进行解码
		postExper=postExper!=null&&postExper.trim().length()>0?postExper:"";
		postExper=SafeCode.decode(postExper);//将传来的字符串进行解码
		empExper=empExper!=null&&empExper.trim().length()>0?empExper:"";
		empExper=SafeCode.decode(empExper);//将传来的字符串进行解码
		
		String strXML = this.spliceXML(serviceList, orgExper, postExper, empExper);
		//修改认证系统,准备数据结束
		
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			ArrayList<String> updateList = new ArrayList<String>();
			//updateList.add(syscode);
			updateList.add(sysname);
			updateList.add(sysdesc);
			updateList.add(valid);
			updateList.add(strXML);
			updateList.add(dynaCode);
			updateList.add(id);
			String updateSql = "UPDATE t_sys_reg_services SET sysname=?, sysdesc=?, valid=?, servicedetail=?, validateway=? WHERE id = ?";
			dao.update(updateSql, updateList);
		} catch (GeneralException e) {
			e.printStackTrace();
			log.error("修改认证系统:创建conn链接出错!");
			log.error(e);
			result = false;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("修改认证系统:执行修改sql出错!");
			log.error(e);
			result = false;
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return result;
	}
	
	
	/**
	 * 去修改页面
	 * @param id 要修改系统的id
	 * @return 要修改系统的数据
	 */
	public Map<String, Object> toEditSys(String id) {
		boolean flag = true;
		//String id = (String) this.getFormHM().get("id");
		Connection conn = null;
		RowSet rs = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		String servicedetail = "";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql = "SELECT id,syscode,sysname,sysetoken,sysdesc,valid,servicedetail,validateway FROM t_sys_reg_services WHERE id = ?";
			List<String> valList = new ArrayList<String>();
			valList.add(id);
			rs = dao.search(sql,valList);
			if(rs.next()) {
				String syscode = rs.getString("syscode");
				String sysname = rs.getString("sysname");
				String sysetoken = rs.getString("sysetoken");
				String sysdesc = rs.getString("sysdesc");
				String valid = rs.getString("valid");
				String validateway = rs.getString("validateway");
				servicedetail = rs.getString("servicedetail");
				map.put("id", id);
				map.put("syscode", syscode);
				map.put("sysname", sysname);
				map.put("sysetoken", sysetoken);
				map.put("sysdesc", sysdesc);
				map.put("valid", valid);
				map.put("validateway", validateway);
			}else {
				log.error("编辑认证系统:未找到对应的认证系统,id:"+id);
			}
		} catch (GeneralException e1) {
			e1.printStackTrace();
			log.error("编辑认证系统:创建conn链接出错!");
			log.error(e1);
			flag = false;
		} catch (SQLException e) {
			log.error("编辑认证系统:执行查询sql出错!");
			log.error(e);
			e.printStackTrace();
			flag = false;
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		List<Map<String, String>> editServiceList = this.editServiceList(servicedetail);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", flag);
		resultMap.put("targetSystem", map);
		resultMap.put("serviceList", editServiceList);
		return resultMap;
	}
	
	/**
	 * 获取文件下载路径及加密文件名和路径
	 * @param name 下载文件的文件名
	 * @return 文件是否存在,文件路径,文件名
	 */
	public Map<String, Object> getDownLoadPath(String name) {
		boolean fileExists = false;
		SysRegLogger othLog = new SysRegLogger(name);
		String filePath = othLog.getPath();
		filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
		File file = new File(filePath);
		fileExists = file.exists();
		if(fileExists) {
			othLog.start("下载日志");
		}
		name = PubFunc.encryption("RegService_"+name+".log");
		filePath = PubFunc.encryption(filePath);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("result", fileExists);
    	map.put("filePath", filePath);
    	map.put("fileName", name);
    	return map;
	}
	
	/**
	 * 生成serviceList的修改页面的值
	 * @param servicedetail
	 */
	public List<Map<String, String>> editServiceList(String servicedetail) {
		ArrayList<Map<String, String>> serviceList = this.parseXML(servicedetail);
		List<Map<String, String>> beanList= new ArrayList<Map<String,String>>();
		for (Map<String, String> map : serviceList) {
			String method = map.get("serviceMethod");
			if(ServiceType.ORG.equalsIgnoreCase(method)) {
				Map<String, String> orgMap = new HashMap<String, String>();
				orgMap.put("serviceMethod", ServiceType.ORG);
				orgMap.put("serviceName", "获取机构数据");
				orgMap.put("dataScope", "1");
				orgMap.put("valid", map.get("valid"));
				orgMap.put("sqlscope", map.get("sqlscope"));
				beanList.add(orgMap);
			}else if(ServiceType.POST.equalsIgnoreCase(method)) {
				Map<String, String> postMap = new HashMap<String, String>();
				postMap.put("serviceMethod", ServiceType.POST);
				postMap.put("serviceName", "获取岗位数据");
				postMap.put("dataScope", "1");
				postMap.put("valid", map.get("valid"));
				postMap.put("sqlscope", map.get("sqlscope"));
				beanList.add(postMap);
			}else if(ServiceType.EMP.equalsIgnoreCase(method)) {
				Map<String, String> empMap = new HashMap<String, String>();
				empMap.put("serviceMethod", ServiceType.EMP);
				empMap.put("serviceName", "获取人员数据");
				empMap.put("dataScope", "1");
				empMap.put("valid", map.get("valid"));
				empMap.put("sqlscope", map.get("sqlscope"));
				beanList.add(empMap);
			}else if(ServiceType.MATTER.equalsIgnoreCase(method)) {
				Map<String, String> matterMap = new HashMap<String, String>();
				matterMap.put("serviceMethod", ServiceType.MATTER);
				matterMap.put("serviceName", "获取待办数据");
				matterMap.put("dataScope", "0");
				matterMap.put("valid", map.get("valid"));
				beanList.add(matterMap);
			}else if(ServiceType.BOARD.equalsIgnoreCase(method)) {
				Map<String, String> boardMap = new HashMap<String, String>();
				boardMap.put("serviceMethod", ServiceType.BOARD);
				boardMap.put("serviceName", "获取公告数据");
				boardMap.put("dataScope", "0");
				boardMap.put("valid", map.get("valid"));
				beanList.add(boardMap);
			}else if(ServiceType.WARN.equalsIgnoreCase(method)) {
				Map<String, String> warnMap = new HashMap<String, String>();
				warnMap.put("serviceMethod", ServiceType.WARN);
				warnMap.put("serviceName", "获取预警数据");
				warnMap.put("dataScope", "0");
				warnMap.put("valid", map.get("valid"));
				beanList.add(warnMap);
			}else if(ServiceType.STATICS.equalsIgnoreCase(method)) {
				Map<String, String> staticsMap = new HashMap<String, String>();
				staticsMap.put("serviceMethod", ServiceType.STATICS);
				staticsMap.put("serviceName", "获取常用统计数据");
				staticsMap.put("dataScope", "0");
				staticsMap.put("valid", map.get("valid"));
				beanList.add(staticsMap);
			}else if(ServiceType.REPORT.equalsIgnoreCase(method)) {
				Map<String, String> reportMap = new HashMap<String, String>();
				reportMap.put("serviceMethod", ServiceType.REPORT);
				reportMap.put("serviceName", "获取报表数据");
				reportMap.put("dataScope", "0");
				reportMap.put("valid", map.get("valid"));
				beanList.add(reportMap);
			}else if(ServiceType.PROCESS.equalsIgnoreCase(method)) {
				Map<String, String> processMap = new HashMap<String, String>();
				processMap.put("serviceMethod", ServiceType.PROCESS);
				processMap.put("serviceName", "更新信息集");
				processMap.put("dataScope", "0");
				processMap.put("valid", map.get("valid"));
				beanList.add(processMap);
			}else if(ServiceType.KQINFO.equalsIgnoreCase(method)) {
				Map<String, String> kqInfoMap = new HashMap<String, String>();
				kqInfoMap.put("serviceMethod", ServiceType.KQINFO);
				kqInfoMap.put("serviceName", "获取考勤报批数据");
				kqInfoMap.put("dataScope", "0");
				kqInfoMap.put("valid", map.get("valid"));
				beanList.add(kqInfoMap);
			}else if(ServiceType.GET_HOLIDAY.equalsIgnoreCase(method)) {
				Map<String, String> getHolidayMap = new HashMap<String, String>();
				getHolidayMap.put("serviceMethod", ServiceType.GET_HOLIDAY);
				getHolidayMap.put("serviceName", "获取年假天数（已休、可休）");
				getHolidayMap.put("dataScope", "0");
				getHolidayMap.put("valid", map.get("valid"));
				beanList.add(getHolidayMap);
			}else if(ServiceType.UPD_HOLIDAY.equalsIgnoreCase(method)) {
				Map<String, String> updHolidayMap = new HashMap<String, String>();
				updHolidayMap.put("serviceMethod", ServiceType.UPD_HOLIDAY);
				updHolidayMap.put("serviceName", "更新年假天数");
				updHolidayMap.put("dataScope", "0");
				updHolidayMap.put("valid", map.get("valid"));
				beanList.add(updHolidayMap);
			}else if(ServiceType.USERETOKEN.equalsIgnoreCase(method)) {
				Map<String, String> tokenMap = new HashMap<String, String>();
				tokenMap.put("serviceMethod", ServiceType.USERETOKEN);
				tokenMap.put("serviceName", "获取用户登录标识");
				tokenMap.put("dataScope", "0");
				tokenMap.put("valid", map.get("valid"));
				beanList.add(tokenMap);
			}
			
		}
		return beanList;
	}
	
	
	/**
	 * 获取服务列表
	 */
	public List<Map<String, String>> getServiceList() {
		List<Map<String, String>> beanList= new ArrayList<Map<String,String>>();
		Map<String, String> orgMap = new HashMap<String, String>();
		orgMap.put("serviceMethod", ServiceType.ORG);
		orgMap.put("serviceName", "获取机构数据");
		orgMap.put("dataScope", "1");
		beanList.add(orgMap);
		
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("serviceMethod", ServiceType.POST);
		postMap.put("serviceName", "获取岗位数据");
		postMap.put("dataScope", "1");
		beanList.add(postMap);
		
		Map<String, String> empMap = new HashMap<String, String>();
		empMap.put("serviceMethod", ServiceType.EMP);
		empMap.put("serviceName", "获取人员数据");
		empMap.put("dataScope", "1");
		beanList.add(empMap);
		
		Map<String, String> matterMap = new HashMap<String, String>();
		matterMap.put("serviceMethod", ServiceType.MATTER);
		matterMap.put("serviceName", "获取待办数据");
		matterMap.put("dataScope", "0");
		beanList.add(matterMap);
		
		Map<String, String> boardMap = new HashMap<String, String>();
		boardMap.put("serviceMethod", ServiceType.BOARD);
		boardMap.put("serviceName", "获取公告数据");
		boardMap.put("dataScope", "0");
		beanList.add(boardMap);
		
		Map<String, String> warnMap = new HashMap<String, String>();
		warnMap.put("serviceMethod", ServiceType.WARN);
		warnMap.put("serviceName", "获取预警数据");
		warnMap.put("dataScope", "0");
		beanList.add(warnMap);
		
		Map<String, String> staticsMap = new HashMap<String, String>();
		staticsMap.put("serviceMethod", ServiceType.STATICS);
		staticsMap.put("serviceName", "获取常用统计数据");
		staticsMap.put("dataScope", "0");
		beanList.add(staticsMap);
		
		Map<String, String> reportMap = new HashMap<String, String>();
		reportMap.put("serviceMethod", ServiceType.REPORT);
		reportMap.put("serviceName", "获取报表数据");
		reportMap.put("dataScope", "0");
		beanList.add(reportMap);
		
		Map<String, String> processMap = new HashMap<String, String>();
		processMap.put("serviceMethod", ServiceType.PROCESS);
		processMap.put("serviceName", "更新信息集");
		processMap.put("dataScope", "0");
		beanList.add(processMap);
		
		Map<String, String> kqInfoMap = new HashMap<String, String>();
		kqInfoMap.put("serviceMethod", ServiceType.KQINFO);
		kqInfoMap.put("serviceName", "获取考勤报批数据");
		kqInfoMap.put("dataScope", "0");
		beanList.add(kqInfoMap);
		
		Map<String, String> getHolidayMap = new HashMap<String, String>();
		getHolidayMap.put("serviceMethod", ServiceType.GET_HOLIDAY);
		getHolidayMap.put("serviceName", "获取年假天数（已休、可休）");
		getHolidayMap.put("dataScope", "0");
		beanList.add(getHolidayMap);
		
		Map<String, String> updHolidayMap = new HashMap<String, String>();
		updHolidayMap.put("serviceMethod", ServiceType.UPD_HOLIDAY);
		updHolidayMap.put("serviceName", "更新年假天数");
		updHolidayMap.put("dataScope", "0");
		beanList.add(updHolidayMap);
		
		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("serviceMethod", ServiceType.USERETOKEN);
		tokenMap.put("serviceName", "获取用户登录标识");
		tokenMap.put("dataScope", "0");
		beanList.add(tokenMap);
		return beanList;
	}
	
}
