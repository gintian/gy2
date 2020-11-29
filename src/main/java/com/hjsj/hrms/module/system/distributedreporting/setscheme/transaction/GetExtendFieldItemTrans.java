package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 指标对应——获取中间库中需要对应的指标
 *
 * @author caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class GetExtendFieldItemTrans extends IBusiness {

	private static final long serialVersionUID = 6373408717751876367L;

	@Override
	public void execute() throws GeneralException {
		Connection middbcon = null;// 中间库数据库连接
		ArrayList<HashMap<String, String>> list = null;// 返回前台的对应数据
		HashMap<String, String> matchMap = null;// 用于对应数据的Map
		HashMap<String, String> matchDescMap = null;// 用于对应指标代码名的Map
		try {
			// 获取当前的子集指标
			ContentDAO dao = new ContentDAO(getFrameconn());
			String fieldsetid = (String) this.getFormHM().get("setid");
			String schemeid = (String) this.getFormHM().get("schemeid");
			String fielditemid = (String) this.getFormHM().get("itemid");
			String unitcode = (String) this.getFormHM().get("unitcode");
			if (StringUtils.isBlank(fieldsetid)) {
				return;
			}
			// 获取hr系统系统中代码项的全名，用于对应数据
			matchDescMap = getMatchDescMap(unitcode, fielditemid, dao);
			// 获取hr系统中已配置的对应数据
			matchMap = getMatchMap(unitcode, fielditemid, dao);
			// 获取中间库链接
			middbcon = getMidDbConnection(dao, schemeid);
			// 查询中间库相应字段，与已配置数据去重
			list = getMatchFieldItemList(middbcon, fieldsetid, fielditemid, matchMap,matchDescMap);
			// 封装数据
			this.getFormHM().put("middesclist", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(middbcon);
		}
	}
	/**
	 * 获取hr系统系统中代码项的全名，用于对应数据
	 * @param unitcode
	 * @param fielditemid
	 * @param dao
	 * @return
	 */
	private HashMap<String, String> getMatchDescMap(String unitcode, String fielditemid, ContentDAO dao) {
		HashMap<String, String> matchDescMap = new HashMap<String, String>();
		RowSet rs = null;
		String sql = "SELECT a.srccodeid,b.codeitemdesc FROM t_sys_asyn_code a,codeitem b WHERE a.destcodeid=b.codeitemid and a.unitcode='"+unitcode+"'";
		sql+=" and a.codesetid=b.codesetid and b.codesetid=(SELECT codesetid FROM fielditem WHERE itemid='"+fielditemid+"')";
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String srccodeid = rs.getString("srccodeid");
				String codeitemdesc = rs.getString("codeitemdesc");
				matchDescMap.put(srccodeid, codeitemdesc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return matchDescMap;
	}

	/**
	 * 获取需要已经对应过的数据，用于去重
	 *
	 * @param unitcode
	 * @param fielditemid
	 * @param dao
	 * @return
	 */
	private HashMap<String, String> getMatchMap(String unitcode, String fielditemid, ContentDAO dao) {
		HashMap<String, String> matchMap = new HashMap<String, String>();
		RowSet rs = null;
		try {
			String sql = "SELECT srccodeid,destcodeid FROM t_sys_asyn_code WHERE unitcode = '" + unitcode
					+ "' and codesetid = (SELECT codesetid FROM fielditem WHERE itemid = '"+fielditemid+"')";
			rs = dao.search(sql);
			while (rs.next()) {
				String srccodeid = rs.getString("srccodeid");
				String destcodeid = rs.getString("destcodeid");
				matchMap.put(srccodeid, destcodeid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return matchMap;
	}

	/**
	 * 获得中间库指标项
	 *
	 * @param middbcon
	 * @param fieldsetid
	 * @param fielditemid
	 * @param matchMap
	 * @param matchDescMap
	 * @return
	 */
	private ArrayList getMatchFieldItemList(Connection middbcon, String fieldsetid, String fielditemid,
			HashMap<String, String> matchMap, HashMap<String, String> matchDescMap) {
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		ArrayList<String> midCodeDescList = new ArrayList<String>();// 中间库取出的指标名称list
		HashMap<String, String> map = null;
		if(middbcon!=null) {
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(middbcon);
			try {
				rs = dao.search("SELECT DISTINCT " + fielditemid + " FROM T_" + fieldsetid);
				while (rs.next()) {
					String midcodedesc = rs.getString(fielditemid);
					if(StringUtils.isNotBlank(midcodedesc)) {
						midCodeDescList.add(midcodedesc);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeResource(rs);
			}
		}
		// 遍历添加已配置的数据
		for (Entry<String, String> entry : matchMap.entrySet()) {
			map = new HashMap<String,String>();
			String midcodedesc = entry.getKey();
			String codeitemid = entry.getValue();
			String codedesc = matchDescMap.get(midcodedesc);
			map.put("midcodedesc", midcodedesc);
			map.put("codedesc", codedesc);
			map.put("codesetid", codeitemid);
			list.add(map);
		}
		if(midCodeDescList.size()!=0) {
			// 遍历添加中间库的数据，如果已配置过不添加
			for(int i = 0;i<midCodeDescList.size();i++) {
				String midcodedesc = midCodeDescList.get(i);
				if(!matchMap.containsKey(midcodedesc)) {
					map = new HashMap<String, String>();
					map.put("midcodedesc", midcodedesc);
					map.put("codedesc", "");
					map.put("codesetid", "");
					list.add(map);
				}
			}
		}else if(middbcon!=null){
			map = new HashMap<String, String>();
			map.put("midcodedesc", "");
			map.put("codedesc", "");
			map.put("codesetid", "");
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取中间库数据库连接
	 *
	 * @param dao      dao类
	 * @param schemeid id
	 * @return
	 */
	private Connection getMidDbConnection(ContentDAO dao, String schemeid) {
		Connection middbcon = null;
		RowSet rs = null;
		try {
			String testDbUrl = "";// jdbc驱动连接url
			String dbtype = "";// sqlserver或oracle
			String dburl = "";// 数据库链接
			String port = "";// 接口
			String dbusername = "";// 用户名
			String password = "";// 密码
			String dbname = "";// 数据库名
			rs = dao.search("SELECT schemeparam FROM t_sys_asyn_scheme WHERE schemeid=" + schemeid);
			if (rs.next()) {
				String schemeparamxml = rs.getString("schemeparam");
				Document doc = null;
				if(StringUtils.isBlank(schemeparamxml)) {
					return null;
				}
				doc = PubFunc.generateDom(schemeparamxml);
				String xpath = "/scheme/mid_db";
				XPath path = XPath.newInstance(xpath);
				Element fielditemslist = (Element) path.selectSingleNode(doc);
				dbtype = fielditemslist.getAttributeValue("type");// 子集指标代码
				dburl = fielditemslist.getAttributeValue("ip");// 子集指标代码
				port = fielditemslist.getAttributeValue("port");// 子集名称
				dbname = fielditemslist.getAttributeValue("dbname");// 子集名称
				dbusername = fielditemslist.getAttributeValue("user");// 子集名称
				password = fielditemslist.getAttributeValue("password");// 子集名称
			}

			if ("2".equalsIgnoreCase(dbtype)) {// mssql
				testDbUrl = "jdbc:sqlserver://" + dburl + ":" + port + ";databaseName=" + dbname;
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} else if ("1".equalsIgnoreCase(dbtype)) {
				testDbUrl = "jdbc:oracle:thin:@"+dburl+":"+port+":"+dbname;
				Class.forName("oracle.jdbc.driver.OracleDriver");
			}
			middbcon = DriverManager.getConnection(testDbUrl, dbusername, password);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			PubFunc.closeResource(rs);
		}
		return middbcon;
	}

}
