package com.hjsj.hrms.service.syncdata;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.StringReader;
import java.sql.*;
import java.util.*;

/**
 * <p>
 * Title:SyncDataUtil
 * </p>
 * <p>
 * Description:数据同步集成
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2017-9-28
 * </p>
 *
 * @author duxl
 * @version 1.0
 */

public class SyncDataUtil {
    private Logger log = LoggerFactory.getLogger(SyncDataUtil.class);
    private Connection conn = null;
    private SyncDataParam syncDataParam = null;
    private Map<String, LazyDynaBean> mappingMap = null;

    public SyncDataUtil() {
        // TODO Auto-generated constructor stub
    }

    public SyncDataUtil(Connection aconn, SyncDataParam Param) {
        conn = aconn;
        syncDataParam = Param;
        mappingMap = setPhotoVfsMapping();
    }


    /**
     * 解析xml，获得数据库信息
     *
     * @param xmlMessage String xml信息
     * @return LazyDynaBean 数据库信息
     */
    public LazyDynaBean parseXml(String xmlMessage) {
        LazyDynaBean bean = new LazyDynaBean();
        try {
            SAXBuilder saxbuilder = new SAXBuilder();
            StringReader reader = new StringReader(xmlMessage);
            Document doc = saxbuilder.build(reader);
            String path = "/hr/recs/rec";
            XPath xpath = XPath.newInstance(path);
            List list = xpath.selectNodes(doc);
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                Element el = (Element) list.get(i);
                if (el.getTextTrim().length() > 0) {
                    str.append(",");
                    str.append(el.getText());
                }
            }
            if (str.length() > 0) {
                bean.set("rec", str.substring(1));
            } else {
                bean.set("rec", "");
            }
            path = "/hr/jdbc";
            Element eleObj = (Element) xpath.selectSingleNode(doc, path);
            list = eleObj.getChildren();
            for (int i = 0; i < list.size(); i++) {
                Element el = (Element) list.get(i);
                String name = el.getName();
                String value = el.getText();

                bean.set(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回
        return bean;
    }

    /**
     * 根据bean创建数据库连接
     *
     * @param bean
     * @return Connection ,如果不能创建成功，将返回null
     */
    public Connection createConnByBean(LazyDynaBean bean) {
        Connection conn = null;
        // 数据库类型
        String datatype = (String) bean.get("datatype");
        // 用户名
        String username = (String) bean.get("username");
        // 密码
        String pass = (String) bean.get("pass");
        // 数据库url
        StringBuffer url = new StringBuffer();
        // 根据数据库类型创建
        try {
            if ("mssql".equalsIgnoreCase(datatype)) {
                // url
                url.append("jdbc:sqlserver://");
                url.append(bean.get("ip_addr"));
                url.append(":");
                url.append(bean.get("port"));
                url.append(";databaseName=");
                url.append(bean.get("database"));
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn = DriverManager.getConnection(url.toString(), username,
                        pass);
            } else if ("oracle".equalsIgnoreCase(datatype)) {
                // url
                url.append("jdbc:oracle:thin:@");
                url.append(bean.get("ip_addr"));
                url.append(":");
                url.append(bean.get("port"));
                url.append(":");
                url.append(bean.get("database"));

                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection(url.toString(), username,
                        pass);
            } else if ("db2".equalsIgnoreCase(datatype)) {
                Class.forName("Com.ibm.db2.jdbc.net.DB2Driver");
                url.append("jdbc:db2://");
                url.append(bean.get("ip_addr"));
                url.append(":");
                url.append(bean.get("port"));
                url.append("/");
                url.append(bean.get("database"));

                conn = DriverManager.getConnection(url.toString(), username,
                        pass);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 根据文件名获取文件，没有则返回null
     *
     * @param filename 文件名
     * @return File
     * @Title: getFile
     * @Description:
     */
    public File getFile(String filename) {
        String classPath = System.getProperty("java.class.path");
        String sep = System.getProperty("path.separator");
        String[] path = classPath.split(sep);
        String path2 = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        File file = file = new File(path2, filename);
		/*for (int i = 0; i < path.length; i++) {
			log.debug("path:"+path[i].toString());
			file = new File(path[i], filename);
			if (file.exists()) {
				break;
			} else {
				file = null;
			}
		}*/
        return file;
    }

    /**
     * 获得待同步的机构数据
     *
     * @return orgDataList
     * @Title: getOrgSyncData
     * @Description:
     */
    public ArrayList<LazyDynaBean> getOrgSyncData() {
        //系统指标
        String sysIndex = "b0110_0,unique_id,codesetid,parentid,grade,parentguidkey";
        List sysIndexList = Arrays.asList(sysIndex.split(","));
        ArrayList<LazyDynaBean> orgDataList = new ArrayList<LazyDynaBean>();
        ArrayList<FieldRefBean> orgFieldRefList = syncDataParam.getOrgFieldRefList();
        String sql = "";
        String columns = "";
        String sysId = syncDataParam.getDestSysId();
        String orgWhereSql = syncDataParam.getOrgWhereSql();
        for (int i = 0; i < orgFieldRefList.size(); i++) {
            FieldRefBean fildreBean = orgFieldRefList.get(i);
            String destfield = fildreBean.getDestField();
            String hrfield = fildreBean.getHrField();
            if ("B01AB".equalsIgnoreCase(hrfield)) {//机构成立日期
                hrfield = "(select START_DATE from organization where GUIDKEY = UNIQUE_ID)";
            } else if ("B01AT".equalsIgnoreCase(hrfield)) {//机构结束日期
                hrfield = "(select END_DATE from organization where GUIDKEY = UNIQUE_ID)";
            }
            columns += hrfield + " " + destfield + ",";
        }
        columns += sysIndex + "," + sysId;

        if (syncDataParam.getIsComplete()) {
            sql = "select " + columns + " from t_org_view where " + orgWhereSql + " and B01AA is not null and B01AD is not null";//去除 机构编码、机构名称为空的数据
        } else {
            sql = "select " + columns + " from t_org_view where " + orgWhereSql + " and " + sysId + " in (1, 2, 3)" + " and B01AA is not null and B01AD is not null";//去除 机构编码、机构名称为空的数据
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                LazyDynaBean obj = setparameter(orgFieldRefList, sysIndexList, rs);
                obj.set("status", rs.getString(sysId));
                orgDataList.add(obj);
            }
        } catch (Exception e) {
            log.error("getOrgSyncData:获取同步组织数据出错!,sql:{},ErrorMessage:{}", sql, e);
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(ps);
        }

        return orgDataList;
    }

    /**
     * 获得待同步的人员数据
     *
     * @return empDataList
     * @Title: getEmpSyncData
     * @Description:
     */
    public ArrayList<LazyDynaBean> getEmpSyncData() {
        //系统指标
        String sysIndex = "nbase_0,b0110_0,unique_id,a0000,a0100,username";
        List sysIndexList = Arrays.asList(sysIndex.split(","));
        ArrayList<LazyDynaBean> empDataList = new ArrayList<LazyDynaBean>();
        ArrayList<FieldRefBean> empFieldRefList = syncDataParam.getEmpFieldRefList();
        String sql = "";
        String columns = "";
        String sysId = syncDataParam.getDestSysId();
        String empWhereSql = syncDataParam.getEmpWhereSql();
        for (int i = 0; i < empFieldRefList.size(); i++) {
            FieldRefBean fildreBean = empFieldRefList.get(i);
            String destfield = fildreBean.getDestField();
            String hrfield = fildreBean.getHrField();
            if ("".equals(hrfield)) {
                columns += "' '  " + destfield + ",";
            } else {
                columns += hrfield + " " + destfield + ",";
            }
        }
        columns += sysIndex + "," + sysId;
        if (syncDataParam.getIsComplete()) {
            if (Sql_switcher.searchDbServer() == 1) {
                sql = "select " + columns + ", (select top 1 unique_id from t_org_view where b0110_0=t.e0122_0) e0122_unique_id,(select top 1 unique_id from t_post_view where e01a1_0=t.e01a1_0) e01a1_unique_id,(select top 1 unique_id from t_org_view where b0110_0=t.b0110_0) b0110_unique_id from t_hr_view t where " + empWhereSql + " and " + sysId + " in (0)";

            } else {
                sql = "select " + columns + ", (select unique_id from t_org_view where b0110_0=t.e0122_0 and rownum=1) e0122_unique_id,(select unique_id from t_post_view where e01a1_0=t.e01a1_0 and rownum=1) e01a1_unique_id,(select unique_id from t_org_view where b0110_0=t.b0110_0 and rownum=1) b0110_unique_id,(select FILEID from usra00 where UsrA00.Flag = 'P' and i9999 = 1 and a0100 = t.A0100 ) fileid  from t_hr_view t where " + empWhereSql + " and A0177 is not null and A0144 is not null and A0101 is not null";
            }
        } else {
            if (Sql_switcher.searchDbServer() == 1) {
                sql = "select " + columns + ", (select top 1 unique_id from t_org_view where b0110_0=t.e0122_0) e0122_unique_id,(select top 1 unique_id from t_post_view where e01a1_0=t.e01a1_0) e01a1_unique_id,(select top 1 unique_id from t_org_view where b0110_0=t.b0110_0) b0110_unique_id from t_hr_view t where " + empWhereSql + " and " + sysId + " in (1, 2, 3)";

            } else {
                sql = "select " + columns + ", (select unique_id from t_org_view where b0110_0=t.e0122_0 and rownum=1) e0122_unique_id,(select unique_id from t_post_view where e01a1_0=t.e01a1_0 and rownum=1) e01a1_unique_id,(select unique_id from t_org_view where b0110_0=t.b0110_0 and rownum=1) b0110_unique_id,(select FILEID from usra00 where UsrA00.Flag = 'P' and i9999 = 1 and a0100 = t.A0100 ) fileid  from t_hr_view t where " + empWhereSql + " and " + sysId + " in (1, 2, 3)" + " and A0177 is not null and A0144 is not null and A0101 is not null";
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                LazyDynaBean obj = setparameter(empFieldRefList, sysIndexList, rs);
                obj.set("status", rs.getString(sysId));
                obj.set("e0122_unique_id", rs.getString("e0122_unique_id"));
                obj.set("e01a1_unique_id", rs.getString("e01a1_unique_id"));
                obj.set("b0110_unique_id", rs.getString("b0110_unique_id"));
                String fileid = rs.getString("fileid");
                if (mappingMap.containsKey(PubFunc.decrypt(fileid))) {
                    LazyDynaBean bean = mappingMap.get(PubFunc.decrypt(fileid));
                    obj.set("fileid", bean.get("filelocation"));
                    obj.set("busiNo", bean.get("busino"));
                } else {
                    obj.set("fileid", "");
                    obj.set("busiNo", "");
                }
                empDataList.add(obj);
            }
        } catch (Exception e) {
            log.error("getEmpSyncData:获取同步人员数据出错!,sql:{},ErrorMessage:{}", sql, e);
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(ps);
        }

        return empDataList;
    }

    /**
     * 获取id 与 filelocation 与 busino对应关系
     *
     * @return
     */
    private Map<String, LazyDynaBean> setPhotoVfsMapping() {
        Map<String, LazyDynaBean> mappingMap = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "select id,filelocation,busino from vfs_file_mapping where STATUS = '2' and EXTENSION in ('jpg','png') and FILELOCATION is not null and BUSINO is not null";
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                String id = rs.getString("id");
                String filelocation = rs.getString("filelocation");
                String busino = rs.getString("busino");
                bean.set("filelocation", filelocation);
                bean.set("busino", busino);
                mappingMap.put(id, bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return mappingMap;
    }

    /**
     * 获得待同步的岗位数据
     *
     * @return postDataList
     * @Title: getPostSyncData
     * @Description:
     */
    public ArrayList<LazyDynaBean> getPostSyncData() {
        //系统指标
        String sysIndex = "unique_id,a0000,codeitemdesc,parentid,parentdesc";
        List sysIndexList = Arrays.asList(sysIndex.split(","));
        ArrayList<LazyDynaBean> postDataList = new ArrayList<LazyDynaBean>();
        ArrayList<FieldRefBean> postFieldRefList = syncDataParam.getPostFieldRefList();
        String sql = "";
        String columns = "";
        String sysId = syncDataParam.getDestSysId();
        String postWhereSql = syncDataParam.getPostWhereSql();
        for (int i = 0; i < postFieldRefList.size(); i++) {
            FieldRefBean fildreBean = postFieldRefList.get(i);
            String destfield = fildreBean.getDestField();
            String hrfield = fildreBean.getHrField();
            if ("K01AI".equalsIgnoreCase(hrfield)) {//岗位生效日期
                hrfield = "(select START_DATE from organization where GUIDKEY = UNIQUE_ID)";
            } else if ("K01AJ".equalsIgnoreCase(hrfield)) {//岗位撤销日期
                hrfield = "(select END_DATE from organization where GUIDKEY = UNIQUE_ID)";
            }
            columns += hrfield + " " + destfield + ",";
        }
        columns += sysIndex + "," + sysId;
        if (syncDataParam.getIsComplete()) {
            sql = "select " + columns + " from t_post_view where " + postWhereSql + " and K01AH is not null and CODEITEMDESC is not null";
        } else {
            sql = "select " + columns + " from t_post_view where " + postWhereSql + " and " + sysId + " in (1, 2, 3)" + " and K01AH is not null and CODEITEMDESC is not null";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                LazyDynaBean obj = setparameter(postFieldRefList, sysIndexList, rs);
                obj.set("status", rs.getString(sysId));
                postDataList.add(obj);
            }
        } catch (Exception e) {
            log.error("getPostSyncData:获取同步岗位数据出错!,sql:{},ErrorMessage:{}", sql, e);
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(ps);
        }

        return postDataList;
    }


    /**
     * 同步成功人员数据
     *
     * @param unique_id 主键
     */
    public void sucessSyncEmp(String unique_id) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String status = syncDataParam.getDestSysId();
            String sql = "update t_hr_view set " + status + " = 0 WHERE unique_id = '" + unique_id + "'";
            dao.update(sql);
            log.debug("同步人员数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步成功组织机构数据
     *
     * @param unique_id 主键
     */
    public void sucessSyncOrg(String unique_id) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String status = syncDataParam.getDestSysId();
            String sql = "update t_org_view set " + status + " = 0 WHERE unique_id = '" + unique_id + "'";
            dao.update(sql);
            log.debug("同步组织机构数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步成功岗位数据
     *
     * @param unique_id 主键
     */
    public void sucessSyncPost(String unique_id) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String status = syncDataParam.getDestSysId();
            String sql = "update t_post_view set " + status + " = 0 WHERE unique_id = '" + unique_id + "'";
            dao.update(sql);
            log.debug("同步岗位数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LazyDynaBean setparameter(ArrayList<FieldRefBean> FieldRefList, List sysIndexList, ResultSet rs) {
        LazyDynaBean obj = new LazyDynaBean();
        try {
            for (int i = 0; i < FieldRefList.size(); i++) {
                FieldRefBean fildreBean = FieldRefList.get(i);
                String destfield = fildreBean.getDestField();
                obj.set(destfield, rs.getString(destfield) == null ? "" : rs.getString(destfield));
            }
            for (int j = 0; j < sysIndexList.size(); j++) {
                String index = sysIndexList.get(j).toString();
                obj.set(index, rs.getString(index) == null ? "" : rs.getString(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


}
