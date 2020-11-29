package com.hjsj.hrms.module.gz.standard.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 *<p>Title:DownLoadXml.java</p>
 *<p>Description:</p>
 *<p>Company:HJHJ</p>
 *<p>Create time:Sep 7, 2007</p>
 *@author dengcan
 *@version 4.0
 */
public class DownLoadXml {
    public DownLoadXml() {

    }

    /**
     * 覆盖导入处理方法
     * @param cname
     * @param chz
     * @param salaryid
     * @param dao
     * @param cstate
     * @param opt =1覆盖=2追加
     * @return
     */
    static StringBuffer error = new StringBuffer("");

    static StringBuffer error1 = new StringBuffer("");
    static Pattern pattern = Pattern.compile("[0-9]*");

    public static StringBuffer getError1() {
        return error1;
    }

    public static void setError1(StringBuffer error1) {
        DownLoadXml.error1 = error1;
    }

    public static StringBuffer getError2() {
        return error2;
    }

    public static void setError2(StringBuffer error2) {
        DownLoadXml.error2 = error2;
    }

    static StringBuffer error2 = new StringBuffer("");

    public static StringBuffer getError() {
        return error;
    }

    public static void setError(StringBuffer error) {
        DownLoadXml.error = error;
    }


    /**
     * 不能覆盖的工资标准集合
     * @param pkgid
     * @param con
     * @param createOrgValue
     * @return
     */
    public static HashMap getHasNoCreateOrgStand(String pkgid,  String[] stand_ids,Connection con, String createOrgValue) {
        HashMap map = new HashMap();
        ArrayList<Object> msgList = new ArrayList();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(con);
            String sql="select id,pkg_id,createorg from gz_stand_history where pkg_id=? and id=?";
            ArrayList sqlList = new ArrayList();
            sqlList.add(pkgid);
            for(int i=0;i<stand_ids.length;i++) {
                sqlList.add(stand_ids[i]);
                rs = dao.search(sql,sqlList);
                sqlList.remove(1);
                while (rs.next()) {
                    String org = rs.getString("createorg");
                    if (StringUtils.isEmpty(org)) {
                        continue;
                    } else {
                        if (org.equalsIgnoreCase(createOrgValue)) {
                            continue;
                        } else {
                            map.put(rs.getString("id"), "1");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                PubFunc.closeDbObj(rs);
            }
        }
        return map;
    }

    public static void importData(Connection con, ArrayList gz_stand_data, ArrayList gz_item_data, String pkg_id, String createOrgValue,
            HashMap amap, String flag) {
        try {

            HashMap gz_stand_column_type = getColumnTypeMap(con, "gz_stand"); //取得
            HashMap gz_item_column_type = getColumnTypeMap(con, "gz_item");//取得标准表字段名 和类型
            HashMap gz_standHistory_column_type = getColumnTypeMap(con, "gz_stand_history");
            HashMap gz_itemHistory_column_type = getColumnTypeMap(con, "gz_item_history");

            importData2(con, gz_stand_data, gz_stand_column_type, 1, pkg_id, createOrgValue, amap, flag);
            importData2(con, gz_item_data, gz_item_column_type, 2, pkg_id, createOrgValue, amap, flag);
            importData2(con, gz_stand_data, gz_standHistory_column_type, 3, pkg_id, createOrgValue, amap, flag);
            importData2(con, gz_item_data, gz_itemHistory_column_type, 4, pkg_id, createOrgValue, amap, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param dataList
     * @param columnMap
     * @param flag 1:gz_stand  2:gz_item  3:gz_stand_history  4:gz_item_history
     */
    @SuppressWarnings("unlikely-arg-type")
    public static void importData2(Connection con, ArrayList dataList, HashMap columnMap, int flag, String pkg_id, String createOrgValue,
            HashMap amap, String aflag) {
        Set keySet = columnMap.keySet();
        ArrayList list = new ArrayList();
        ArrayList valuelist = new ArrayList();
        String sql = "";
        StringBuffer names = new StringBuffer();
        try {
            //afalg=1覆盖
            ContentDAO dao = new ContentDAO(con);
            Calendar d = Calendar.getInstance();
            int z = 0;
            for (int i = 0; i < dataList.size(); i++) {
                z++;//计数器，拼sql用的 循环一次获得字段就行了
                LazyDynaBean abean = (LazyDynaBean) dataList.get(i);//-------

                RecordVo record_vo = null;
                ArrayList paramerList = new ArrayList();

                if (flag == 1) {
                    record_vo = new RecordVo("gz_stand");
                    sql = "insert into gz_stand ";
                } else if (flag == 2) {
                    record_vo = new RecordVo("gz_item");
                    sql = "insert into gz_item ";
                } else if (flag == 3) {
                    record_vo = new RecordVo("gz_stand_history");
                    sql = "insert into gz_stand_history ";
                } else if (flag == 4) {
                    record_vo = new RecordVo("gz_item_history");
                    sql = "insert into gz_item_history ";
                }
                for (Iterator t = keySet.iterator(); t.hasNext();) {
                    String columnName = ((String) t.next()).toLowerCase();
                    if (columnName != null && !"".equals(columnName) && z == 1) {
                        names.append(columnName);
                        names.append(",");
                    }

                    if (flag == 1 && "flag".equalsIgnoreCase(columnName)) {
                        record_vo.setInt("flag", 1); //
                        paramerList.add(new Integer(1));
                    } else if (flag == 3 && "createtime".equalsIgnoreCase(columnName)) {
                        record_vo.setDate("createtime", d.getTime());
                        paramerList.add(DateUtils.getSqlDate(d.getTime()));
                    } else if (flag == 3 && "pkg_id".equalsIgnoreCase(columnName)) {
                        record_vo.setInt("pkg_id", Integer.parseInt(pkg_id));
                        paramerList.add(new Integer(pkg_id));
                    } else if (flag == 4 && "pkg_id".equalsIgnoreCase(columnName)) {
                        record_vo.setInt("pkg_id", Integer.parseInt(pkg_id));
                        paramerList.add(new Integer(pkg_id));
                    } else if (flag == 3 && "b0110".equalsIgnoreCase(columnName)) {
                        String id = (String) abean.get("id");
                        if (abean.get(columnName) == null || "".equals((String) abean.get(columnName))) {
                            record_vo.setString("b0110", null);
                            paramerList.add(null);
                        } else {
                            record_vo.setString("b0110", (String) abean.get(columnName));
                            paramerList.add((String) abean.get(columnName));
                        }
                    } else if (flag == 3 && "createorg".equalsIgnoreCase(columnName)) {
                        String id = (String) abean.get("id");
                        if (("1").equals(aflag) && amap.get(id) == null)//覆盖
                        {
                            if (abean.get(columnName) == null || "".equals((String) abean.get(columnName))) {
                                record_vo.setString("createorg", null);
                                paramerList.add(null);
                            } else {
                                record_vo.setString("createorg", (String) abean.get(columnName));
                                paramerList.add((String) abean.get(columnName));
                            }
                        } else {
                            record_vo.setString("createorg", createOrgValue);
                            paramerList.add(createOrgValue);
                        }
                    } else {
                        String type = (String) columnMap.get(columnName);
                        String value = (String) abean.get(columnName);
                        if (value != null && value.length() > 0) {
                            if ("D".equals(type)) {
                                String[] values = value.split("-");
                                Calendar dd = Calendar.getInstance();
                                dd.set(Calendar.YEAR, Integer.parseInt(values[0]));
                                dd.set(Calendar.MONTH, Integer.parseInt(values[1]) - 1);
                                dd.set(Calendar.DATE, Integer.parseInt(values[2]));
                                record_vo.setDate(columnName, dd.getTime());
                                paramerList.add(DateUtils.getSqlDate(dd.getTime()));
                            } else if ("F".equals(type)) {
                                record_vo.setDouble(columnName, Double.parseDouble(value));
                                paramerList.add(new Double(value));
                            } else if ("N".equals(type)) {
                                record_vo.setInt(columnName, Integer.parseInt(value));
                                paramerList.add(new Integer(value));
                            } else {
                                record_vo.setString(columnName, value);
                                paramerList.add(value);
                            }

                        } else {
                            if ("D".equals(type)) {
                                String[] values = value.split("-");
                                Calendar dd = Calendar.getInstance();
                                dd.set(Calendar.YEAR, Integer.parseInt(values[0]));
                                dd.set(Calendar.MONTH, Integer.parseInt(values[1]) - 1);
                                dd.set(Calendar.DATE, Integer.parseInt(values[2]));
                                record_vo.setDate(columnName, dd.getTime());
                                paramerList.add(DateUtils.getSqlDate(dd.getTime()));
                            } else if ("F".equals(type)) {
                                record_vo.setDouble(columnName, Double.parseDouble(value));
                                paramerList.add(new Double(0));
                            } else if ("N".equals(type)) {
                                record_vo.setInt(columnName, Integer.parseInt(value));
                                paramerList.add(new Integer(0));
                            } else {
                                paramerList.add(null);
                            }
                        }

                    }
                }
                list.add(record_vo);
                valuelist.add(paramerList);
            }
            //dao.addValueObject(list); zhaoxg add前程序
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < columnMap.size(); i++) {
                value.append("?");
                value.append(",");
            }
            String insertSql = sql + "(" + names.toString().substring(0, names.toString().length() - 1) + ") values ("
                    + value.toString().substring(0, value.length() - 1) + ")";
            int num = valuelist.size() / 1000;//批量增加，提速  zhaoxg add 2013-9-22
            if (valuelist.size() % 1000 != 0) {
                num++;
            }
            ArrayList templist = null;
            for (int n = 0; n < num; n++) {
                templist = new ArrayList();
                for (int x = n * 1000; x < (n + 1) * 1000; x++) {
                    if (x >= valuelist.size()) {
                        break;
                    }
                    templist.add(valuelist.get(x));
                }
                dao.batchInsert(insertSql, templist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得表列类型
     * @param tableName
     * @return
     */
    public static HashMap getColumnTypeMap(Connection con, String tableName) {
        HashMap columnTypeMap = new HashMap();
        ResultSet resultSet = null;
        try {
            ContentDAO dao = new ContentDAO(con);
            resultSet = dao.search("select * from " + tableName + " where 1=2");
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i).toLowerCase();
                String type = "A";
                int columnType = metaData.getColumnType(i);
                switch (columnType) {
                    case Types.LONGVARCHAR: {
                        type = "M";
                        break;
                    }
                    case Types.TINYINT: {
                        type = "N";
                        break;
                    }
                    case Types.SMALLINT: {
                        type = "N";
                        break;
                    }
                    case Types.INTEGER: {
                        type = "N";
                        break;
                    }
                    case Types.BIGINT: {
                        type = "N";
                        break;
                    }
                    case Types.FLOAT: {
                        type = "F";
                        break;
                    }
                    case Types.DOUBLE: {
                        type = "F";
                        break;
                    }
                    case Types.DECIMAL: {
                        type = "F";
                        break;
                    }
                    case Types.NUMERIC: {
                        type = "F";
                        break;
                    }
                    case Types.REAL: {
                        type = "F";
                        break;
                    }
                    case Types.DATE: {
                        type = "D";
                        break;
                    }
                    case Types.TIME: {
                        type = "D";
                        break;
                    }
                    case Types.TIMESTAMP: {
                        type = "D";
                        break;
                    }
                    default:
                        type = "A";
                        break;
                }
                columnTypeMap.put(name, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(resultSet);
        }
        return columnTypeMap;
    }

    /**
     *
     * @param con
     * @param dataList
     * @param standIdMap
     * @param flag  1 standData  2itemData
     * @return
     */
    public static ArrayList editStandDataList(Connection con, ArrayList dataList, HashMap standIdMap, int flag) {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(con);
            int maxID = 0;
            if (flag == 1) {
                rowSet = dao.search("select max(id) from gz_stand");
                if (rowSet.next()) {
                    maxID = rowSet.getInt(1);
                }
            }
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) dataList.get(i);
                String id = (String) abean.get("id");
                if (flag == 1) {
                    if (standIdMap.get(id) == null) {
                        standIdMap.put(id, String.valueOf(++maxID));
                    }
                }
                abean.set("id", (String) standIdMap.get(id));
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }

    public static ArrayList editStandDataList(Connection con, ArrayList dataList, HashMap standIdMap, int flag, HashMap map, HashMap amap) {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(con);
            int maxID = 0;
            if (flag == 1 && map.size() > 0) {
                rowSet = dao.search("select max(id) from gz_stand");
                if (rowSet.next()) {
                    maxID = rowSet.getInt(1);
                }
            }
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) dataList.get(i);
                String id = (String) abean.get("id");
                if (flag == 1) {
                    if (standIdMap.get(id) == null && map.get(id) != null) {
                        standIdMap.put(id, String.valueOf(++maxID));
                        amap.put(maxID + "", "1");
                    } else if (standIdMap.get(id) == null) {
                        standIdMap.put(id, String.valueOf(id));
                    }
                }
                abean.set("id", (String) standIdMap.get(id));
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null) {
                PubFunc.closeDbObj(rowSet);
            }
        }
        return list;
    }


    /**
     * 取得当前历史沿革
     * @param con
     * @return
     */
    public static String getStartPkgID(Connection con) {
        String pkg_id = "";
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(con);
            rowSet = dao.search("select pkg_id from gz_stand_pkg where status='1'");
            if (rowSet.next()) {
                pkg_id = rowSet.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return pkg_id;
    }

    public static void deleteFromExistData(Connection con, ArrayList gzStandardPackageInfo, String[] importStandardIds, String startPkgID,
        HashMap noCrtMap) throws GeneralException {
        ArrayList<Object> msgList = new ArrayList();
        try {
            StringBuffer allowIDs = new StringBuffer("");
            StringBuffer info = new StringBuffer("");
            for (int i = 0; i < importStandardIds.length; i++) {
                if (noCrtMap.get(importStandardIds[i]) != null)//没权限覆盖的
                {
                    info.append("," + importStandardIds[i]);
                }
                allowIDs.append("#" + importStandardIds[i] + "#");
            }

            if (info.length() > 0) {
                throw GeneralExceptionHandler.Handle(new Exception("下列薪资标准无权限覆盖！"+"\r\n"+"薪资标准表编号：" + info.substring(1)));
            }
            ContentDAO dao = new ContentDAO(con);
            int num = gzStandardPackageInfo.size() / 200;
            if (gzStandardPackageInfo.size() % 200 != 0) {
                num++;
            }
            for (int n = 0; n < num; n++) {
                StringBuffer pk_whl = new StringBuffer("");
                StringBuffer stand_whl = new StringBuffer("");
                StringBuffer stand_whl2 = new StringBuffer("");
                for (int i = n * 200; i < (n + 1) * 200; i++) {
                    if (gzStandardPackageInfo.size() > i) {
                        LazyDynaBean abean = (LazyDynaBean) gzStandardPackageInfo.get(i);
                        String flag = (String) abean.get("flag");
                        String name = (String) abean.get("name");
                        String id = (String) abean.get("id");
                        /**如果不是自己创建的，不能删除*/
                        if (allowIDs.indexOf("#" + id + "#") != -1) {
                            if (noCrtMap.get(id) != null)//没权限覆盖的
                            {
                                continue;
                            }
                            stand_whl2.append(" or  (pkg_id=" + startPkgID + " and id=" + id + ")");
                            stand_whl.append("," + id);
                        }

                    } else {
                        break;
                    }
                }
                if (stand_whl.length() > 0) {
                    dao.delete("delete from gz_stand where id in (" + stand_whl.substring(1) + ")", new ArrayList());
                    dao.delete("delete from gz_item where id in (" + stand_whl.substring(1) + ")", new ArrayList());
                    dao.delete("delete from gz_stand_history where " + stand_whl2.substring(3), new ArrayList());
                    dao.delete("delete from gz_item_history where " + stand_whl2.substring(3), new ArrayList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * //取得导入数据
     * @param doc
     * @param importStandardIds  需导入的数据id
     * @param type    1：gz_stand_pkg  2:gz_stand   3:gz_item   4:
     * @param userView
     * @return
     */
    public static ArrayList getTableData(Document doc, String[] importStandardIds, String type, UserView userView) {
        ArrayList<Object> msgList = new ArrayList();
        ArrayList list = new ArrayList();
        try {
            StringBuffer allowIDs = new StringBuffer("");
            if (importStandardIds != null) {
                for (int i = 0; i < importStandardIds.length; i++) {
                    allowIDs.append("#" + importStandardIds[i]);
                }
            }
            allowIDs.append("#");

            Element root = doc.getRootElement();
            List childrenList = root.getChildren();
            LazyDynaBean a_bean = null;
            for (Iterator t = childrenList.iterator(); t.hasNext();) {
                Element record = (Element) t.next();

                a_bean = new LazyDynaBean();
                List attributes = record.getAttributes();
                for (int i = 0; i < attributes.size(); i++) {
                    Attribute att = (Attribute) attributes.get(i);
                    a_bean.set(att.getName().toLowerCase(), att.getValue());
                }
                List children = record.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element att = (Element) children.get(i);
                    a_bean.set(att.getName().toLowerCase(), att.getValue());
                }

                boolean isAdd = false; //判断是否是需导入的数据
                if (importStandardIds == null) //所有
                {
                    isAdd = true;
                } else if ("1".equals(type)) //gz_stand_pkg
                {
                    String id = (String) a_bean.get("pkg_id");
                    if (!pattern.matcher(id).matches()) {
                        msgList.add("<ID>" + id + "</ID> " + id + ":必须为数字"); //检验id符合要求
                        exportErrorLog(msgList,userView);
                    }
                    if (allowIDs.indexOf("#" + id + "#") != -1) {
                        isAdd = true;
                    }

                } else if ("2".equals(type) || "3".equals(type)) //gz_stand
                {
                    String id = (String) a_bean.get("id");
                    if (!pattern.matcher(id).matches()) {
                        msgList.add("<ID>" + id + "</ID> " + id + ":必须为数字"); //检验id符合要求
                        exportErrorLog(msgList,userView);
                    }
                    if (allowIDs.indexOf("#" + id + "#") != -1) {
                        isAdd = true;
                    }
                }
                if (isAdd) {
                    list.add(a_bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
    * 输出导入标准表错误日志
    *
    * @param msgList:
    * @author: weiqb
     * @param userView
    * @return: java.lang.String 文件名
    * @date: 2019-12-10 17:10
    */
    public static String exportErrorLog(ArrayList<Object> msgList, UserView userView) {
        String logFileName = userView.getUserName()+"_历史沿革引用标准表导入错误日志.txt";
        String logPath = System.getProperty("java.io.tmpdir") + File.separator + logFileName;
        File file = new File(logPath);
        if (file.exists()) {
            file.delete();
        }
        //错误信息不为0输出错误日志
        StringBuffer textStr = new StringBuffer();
        textStr.append("错误情况如下：" + "\r\n");
        for (int i = 0; i < msgList.size(); i++) {
            String msgStr = (String) msgList.get(i);
            textStr.append(msgStr + "\r\n");
        }

        OutputStreamWriter osw = null;
        FileOutputStream out = null;
        try {
        	out = new FileOutputStream(logPath, false);
            osw = new OutputStreamWriter(out, "utf-8");
            osw.write(textStr.toString());
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(out);
            PubFunc.closeResource(osw);
        }
        return SafeCode.encode(PubFunc.encrypt(logFileName));
    }


    public static ArrayList getStandardList(String stand_context,UserView userView) {
        ArrayList list = new ArrayList();

        try {

            if (stand_context == null || stand_context.length() == 0) {
                return list;
            }
            byte[] b0 = stand_context.getBytes(StandardCharsets.UTF_8);
            InputStream ip0 = new ByteArrayInputStream(b0);
            Document standard_doc = PubFunc.generateDom(ip0);
            getStandardList(standard_doc, list,userView);//4

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    static void getStandardList(Document doc, ArrayList list,UserView userView) {
        ArrayList<Object> msgList = new ArrayList();
        try {
            LazyDynaBean a_bean = null;
            Element root = doc.getRootElement();
            List nodeList = root.getChildren();
            for (Iterator t = nodeList.iterator(); t.hasNext();) {
                Element record = (Element) t.next();
                String id = record.getAttributeValue("ID");
                if (!pattern.matcher(id).matches()) {
                    msgList.add("<ID>" + id + "</ID> " + id + ":必须为数字"); //检验id符合要求
                    exportErrorLog(msgList,userView);
                }
                XPath xPath0 = XPath.newInstance("./NAME");
                Element nameNode = (Element) xPath0.selectSingleNode(record);
                String name = nameNode.getValue(); //错误1

                a_bean = new LazyDynaBean();
                a_bean.set("flag", "1"); //工资标准默认为1 与xml中flag值无关
                a_bean.set("name", name);
                a_bean.set("id", id);
                list.add(a_bean);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取压缩包里的文件
     * @param inputStream
     * @return
     */
    public static HashMap extZipFileList(InputStream inputStream) {
        HashMap fileMap = new HashMap();
        try {
            ZipInputStream in = new ZipInputStream(inputStream);
            ZipEntry entry = null;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                } else {
                    String entryName = entry.getName();
                    BufferedReader ain = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    StringBuffer s = new StringBuffer("");
                    String line;
                    while ((line = ain.readLine()) != null) {
                        s.append(line);
                    }
                    in.closeEntry();
                    fileMap.put(entryName.toLowerCase(), s.toString());
                }
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    ////////////////////导出工资标准////////////////////////////

    /**
     * 导出工资标准
     * @param con
     * @param outFileName
     * @return
     */
    public static String outPutXmlInfo(Connection con, String ids,UserView userView, String outFileName,String pkg_id) {
        String fileName = userView.getUserName()+"_"+outFileName+".zip";
        ZipOutputStream outputStream = null;
        FileOutputStream fileOut = null;
        FileInputStream fileIn = null;
        BufferedInputStream origin = null;
        try {
            produceFolder(); //产生newdata文件夹
            SalaryStandardPackUtils utils = new SalaryStandardPackUtils(con);
            LazyDynaBean abean = null;
            String outName = "";
            abean = utils.getOutPutTableInfo(ids, "2", pkg_id);
            outName = "gz_stand.xml";
            try {
                fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "newdata"
                        + System.getProperty("file.separator") + outName);
                fileOut.write(getXmlContent(abean));
            } finally {
                PubFunc.closeResource(fileOut);
            }

            //导出gz_item
            abean = utils.getOutPutTableInfo(ids, "3", pkg_id);
            outName = "gz_item.xml";
            //生产xml文件
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "newdata"
                    + System.getProperty("file.separator") + outName);
            fileOut.write(getXmlContent(abean));
            fileOut.close();

            //压缩文件
            ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
            ArrayList files = new ArrayList(); // 存放文件对象

            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
            outputStream = new ZipOutputStream(fileOut);
            File rootFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "newdata");
            listFile(rootFile, fileNames, files);
            byte[] data = new byte[2048];

            for (int loop = 0; loop < files.size(); loop++) {
                String a_fileName = (String) fileNames.get(loop);
                if (!"gz_stand.xml".equalsIgnoreCase(a_fileName) && !"gz_item.xml".equalsIgnoreCase(a_fileName)) {
                    continue;
                }
                fileIn = new FileInputStream((File) files.get(loop));

                origin = new BufferedInputStream(fileIn, 2048);
                outputStream.putNextEntry(new ZipEntry((String) fileNames.get(loop)));
                int count;
                while ((count = origin.read(data, 0, 2048)) != -1) {
                    outputStream.write(data, 0, count);
                }
                origin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(outputStream);
            PubFunc.closeResource(origin);
            PubFunc.closeResource(fileIn);
            PubFunc.closeResource(fileOut);
        }
        return fileName;
    }

    public static byte[] getXmlContent(LazyDynaBean abean) {
        Element root = new Element("root");
        String columns = (String) abean.get("columns");
        String keycolumns = (String) abean.get("keycolumns");
        root.setAttribute("columns", columns);
        root.setAttribute("rowcount", (String) abean.get("rowcount"));
        root.setAttribute("keycolumns", keycolumns);

        ArrayList records = (ArrayList) abean.get("records");
        for (int i = 0; i < records.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) records.get(i);
            Element record = new Element("record");
            if (keycolumns.trim().length() > 0) {
                String[] temps = keycolumns.split(",");
                for (int j = 0; j < temps.length; j++) {
                    record.setAttribute(temps[j], (String) bean.get(temps[j]));
                }
            }

            if (columns.trim().length() > 0) {
                String[] temps = columns.split(",");
                for (int j = 0; j < temps.length; j++) {
                    Element d = new Element(temps[j]);
                    d.addContent((String) bean.get(temps[j]));
                    record.addContent(d);
                }
            }
            root.addContent(record);
        }
        Document myDocument = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        return outputter.outputString(myDocument).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 产生newdata文件夹
     * @param con
     * @return
     */
    public static void produceFolder() {
        if (!(new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "newdata/").isDirectory())) {
            new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "newdata/").mkdir();

        }
    }


    public static void listFile(File parentFile, List nameList, List fileList) {
        if (parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (int loop = 0; loop < files.length; loop++) {
                listFile(files[loop], nameList, fileList);
            }
        } else {
            fileList.add(parentFile);
            nameList.add(parentFile.getName());
        }
    }

}
