package com.hjsj.hrms.businessobject.sys.job;


import com.dcfs.fts.client.FtpClientConfig;
import com.dcfs.fts.client.upload.FtpPut;
import com.dcfs.fts.common.error.FtpException;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 贵州银行
 * 以csv文件的方式推送基础数据到文件传输平台
 *
 * @author Pancs
 * @date 2020.03.09
 */
public class SyncBaseDataCsvToBigData implements Job {

    private Logger log = LoggerFactory.getLogger(SyncBaseDataCsvToBigData.class);

    /**
     * 存放连接ftp的参数信息
     **/
    private LazyDynaBean ftpBean;

    /**
     * 从xml获取的多个主集集合
     **/
    private List<Map<String, Object>> mainList;

    /**
     * hr数据库连接
     **/
    private Connection conn = null;

    /**
     * ftp上传工具类
     **/
    FtpUtilBo ftpUtilBo = null;
    /**
     * 影像头像唯一标识对应
     */
    private Map<String, LazyDynaBean> mappingMap = null;


    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("[数据同步-生成人员信息文件并上传文件传输平台]任务开始");
        long start = System.currentTimeMillis();
        try {
            // 获取数据库连接
            conn = AdminDb.getConnection();
            //需要初始化的参数
            init();
            String tempFile = System.getProperty("java.io.tmpdir");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String path = tempFile + File.separator + new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            // 主集处理
            for (int i = 0; i < mainList.size(); i++) {
                Map<String, Object> map = mainList.get(i);
                List<List> mainSetInfo = getMainSetInfo(map);
                String fileName = map.get("desttable") + "_" + getNowDateStr();
                File csvfile = createCSVFile(mainSetInfo, path, fileName);
                //改为对接文件传输平台，存放到指定路径下
                String tranCode = "hrs001";//传输交易码，该字段内容请向文件传输系统管理员申请
                String localFileName = csvfile.getPath();//本地文件绝对路径，例如：/home/zyb/apps/a.txt
                String remoteFileName = "/data/" + getNowDateStr() + "/" + csvfile.getName(); //文件传输系统保存的路径及文件名 例如：test/a.txt
                String escFilePath = putFile(localFileName, remoteFileName, tranCode);
                log.info("上传文件传输平台:escFilePath:{}", escFilePath);///hrs/hrs001/data/20200928/ORG_20200928.csv   POST_20200928.csv  EMP_20200928.csv
                //boolean flag = ftpUtilBo.uploadFtp(csvfile.getName(), csvfile.getPath().split(fileName)[0]);
                //只有生成csv文件生成成功才生成flg文件
/*                if (flag) {
                    File flgfile = createFLGFile(null, fileName);//加生成flg文件
                    ftpUtilBo.uploadFtp(flgfile.getName(), flgfile.getPath().split(fileName)[0]);
                } else {
                    log.error("同步文件传输平台上传服务器文件出错!");
                }*/

            }
            log.info("[数据同步-生成人员信息文件并上传文件传输平台]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("execute:推送文件传输平台出错!,ErrorMessage:{}", e);
            throw new JobExecutionException("推送文件传输平台出错!" + e);
        } finally {
            if (conn != null) {
                PubFunc.closeDbObj(conn);
            }
            //因为需要多次上传，注释掉了工具类中每次上传都关闭的方法
            //ftpUtilBo.closeConnect();
        }
    }

    /**
     * @param localFileName  本地文件绝对路径，例如：/home/zyb/apps/a.txt
     * @param remoteFileName 为文件传输系统保存的文件名，例如：test/a.txt
     * @param tranCode
     * @return
     * @throws FtpException
     * @throws IOException
     */
    public static String putFile(String localFileName, String remoteFileName, String tranCode) throws FtpException, IOException {
        FtpClientConfig config = FtpClientConfig.getInstance();
        FtpPut ftpPut = new FtpPut(localFileName, remoteFileName, tranCode, true, false, true, config);
        String filePath = ftpPut.doPutFile();
        return filePath;
    }

    /**
     * CSV文件生成方法
     *
     * @param dataList   数据列表
     * @param outPutPath 文件输出路径
     * @param filename   文件名
     * @return
     */
    public File createCSVFile(List<List> dataList, String outPutPath, String filename) throws IOException {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            if (outPutPath == null) {
                csvFile = File.createTempFile(filename, ".csv");//创建临时文件
                if (csvFile.exists()) {// 解决临时文件名字随机的问题
                    String name2 = csvFile.getPath().split(filename)[0] + filename + ".csv";
                    File file = new File(name2);
                    FileUtils.copyFile(csvFile, file);
                    csvFile.delete();
                    csvFile = file;
                }
            } else {
                csvFile = new File(outPutPath + File.separator + filename + ".csv");
            }
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // UTF-8使正确读取分隔符"||"
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "UTF-8"), 1024);

            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            log.error("createCSVFile:生成本地csv临时文件出错!,desc{}", e);
            throw e;
        } finally {
            try {
                if (csvFile != null) {
                    csvWtriter.close();
                }
            } catch (IOException e) {
                log.error("createCSVFile:关闭文件流出错!,ErrorMessage{}", e);
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * FLG文件生成方法
     *
     * @param outPutPath 文件输出路径
     * @param filename   文件名
     * @return
     */
    public File createFLGFile(String outPutPath, String filename) {

        File flgFile = null;
        BufferedWriter flgWtriter = null;
        try {
            if (outPutPath == null) {
                flgFile = File.createTempFile(filename, ".flg");//创建临时文件
                if (flgFile.exists()) {// 解决临时文件名字随机的问题
                    String name2 = flgFile.getPath().split(filename)[0] + filename + ".flg";
                    File file = new File(name2);
                    FileUtils.copyFile(flgFile, file);
                    flgFile.delete();
                    flgFile = file;
                }
            } else {
                flgFile = new File(outPutPath + File.separator + filename + ".flg");
            }
            File parent = flgFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            flgFile.createNewFile();

            // UTF-8使正确读取分隔符"||"
            flgWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    flgFile), "UTF-8"), 1024);

        } catch (Exception e) {
            log.error("createCSVFile:生成本地flg临时文件出错!,desc{}", e);
            e.printStackTrace();
        } finally {
            try {
                if (flgFile != null) {
                    flgWtriter.close();
                }
            } catch (IOException e) {
                log.error("createFLGFile:关闭文件流出错!,desc{}", e);
                e.printStackTrace();
            }
        }
        return flgFile;
    }

    /**
     * 写一行数据方法
     *
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        // 写入文件
        Object data = null;
        for (int i = 0; i < row.size(); i++) {
            data = row.get(i) == null ? "" : row.get(i);
            data = data.toString().replaceAll("\r", "").replaceAll("\n", "").replace("\r\n", "").replaceAll("\\|\\|", "").trim();
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append(data).append("||").toString();
            if (i == row.size() - 1) {
                csvWriter.write(rowStr.substring(0, rowStr.length() - 2));
            } else {
                csvWriter.write(rowStr);
            }
        }
        csvWriter.newLine();
    }

    /**
     * 需要初始化的数据
     */
    private void init() throws GeneralException {
        File file = getFile();
        if (file == null) {
            throw GeneralExceptionHandler.Handle(new Exception("未找到BIGDATA.xml文件"));
        }
        /*主集初始化*/
        mainList = getTableRefList("sync/fields_ref/mainset", file, false);
        /*赋值ftp配置信息*/
        //ftpBean = getFtpConfig();
        // 初始化ftp工具类
        //ftpUtilBo = initFtpUtilBo();
        mappingMap = setPhotoVfsMapping();

    }

    /**
     * 获取文件的File对象
     *
     * @return 若没有则为null
     */
    private File getFile() {
        //当前程序的配置文件
        String fileName = "BIGDATA.xml";
        //获取加载的配置文件地址
        String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        File file = new File(path + "/" + fileName);
        if (file == null) {
            log.error("同步数据到文件传输平台初始化出错,未找到 {} 文件", "BIGDATA.xml");
            log.info("同步文件传输平台配置文件路径path:{}", path + "/" + fileName);
        }
        return file;
    }

    /**
     * 获取表的映射集合
     *
     * @param path
     * @param file
     * @param isSub 是否是子集
     * @return
     */
    private List<Map<String, Object>> getTableRefList(String path, File file, boolean isSub) {
        //初始化xml解析类
        PareXmlUtils pareXmlUtils = new PareXmlUtils(file);
        List nodes = pareXmlUtils.getNodes(path);

        /*初始化返回的集合*/
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> map = null;
        List<LazyDynaBean> list = null;
        LazyDynaBean bean = null;
        Element node = null;
        for (int i = 0; i < nodes.size(); i++) {
            node = (Element) nodes.get(i);
            map = new HashMap();
            map.put("hrtable", node.getAttributeValue("hrtable"));
            map.put("desttable", node.getAttributeValue("desttable"));
            if (isSub) {
                map.put("maintable", node.getAttributeValue("maintable"));
                map.put("relationfield", node.getAttributeValue("relationfield"));
                map.put("hrkey", node.getAttributeValue("hrkey"));
                map.put("destkey", node.getAttributeValue("destkey"));
            }
            List fieldRef = node.getChildren();
            list = new ArrayList<>();
            for (int j = 0; j < fieldRef.size(); j++) {
                //node重新赋值
                node = (Element) fieldRef.get(j);
                bean = new LazyDynaBean();
                bean.set("hrfield", node.getAttributeValue("hrfield"));
                bean.set("destfield", node.getAttributeValue("destfield"));
                bean.set("desc", node.getAttributeValue("desc"));
                list.add(bean);
            }
            map.put("field_ref", list);
            listMap.add(map);
        }
        return listMap;
    }

    /**
     * 获取ftp配置信息
     *
     * @return
     */
    private LazyDynaBean getFtpConfig() {

        LazyDynaBean bean = new LazyDynaBean();
        String ip = SystemConfig.getPropertyValue("bigData_ip");
        if (ip == null || "".equals(ip)) {
            log.error("未配置 {} 参数，请在system.properties文件中配置", "bigData_ip");
        } else {
            bean.set("ip", ip);
        }
        String port = SystemConfig.getPropertyValue("bigData_port");
        if (port == null || "".equals(port)) {
            log.error("未配置 {} 参数，请在system.properties文件中配置", "bigData_port");
        } else {
            bean.set("port", port);
        }
        String pathBase = SystemConfig.getPropertyValue("bigData_pathBase");
        if (pathBase == null || "".equals(pathBase)) {
            log.error("未配置  参数，请在system.properties文件中配置", "bigData_pathBase");
        } else {
            bean.set("pathBase", pathBase);
        }
        String username = SystemConfig.getPropertyValue("bigData_username");
        if (username == null || "".equals(username)) {
            log.error("未配置 {} 参数，请在system.properties文件中配置", "bigData_username");
        } else {
            bean.set("username", username);
        }
        String password = SystemConfig.getPropertyValue("bigData_password");
        if (password == null || "".equals(password)) {
            log.error("未配置 {} 参数，请在system.properties文件中配置", "bigData_password");
        } else {
            bean.set("password", password);
        }
        log.info("文件传输平台服务器配置ip:{}, port:{}, pathBase:{}, username:{}", ip, port, pathBase, username);
        return bean;
    }

    /**
     * 获取主集信息
     *
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    private List<List> getMainSetInfo(Map<String, Object> map) throws SQLException, ParseException {
        // 用于返回的list
        List<List> lists = new ArrayList<List>();
        List<String> list = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        List<LazyDynaBean> fieldRef = (List<LazyDynaBean>) map.get("field_ref");
        LazyDynaBean bean = null;
        for (int i = 0; i < fieldRef.size(); i++) {
            bean = fieldRef.get(i);
            String hrfield = bean.get("hrfield").toString();
            if ("B01AB".equalsIgnoreCase(hrfield)) {//机构成立日期
                hrfield = "(select START_DATE from organization where GUIDKEY = UNIQUE_ID)";
            } else if ("B01AT".equalsIgnoreCase(hrfield)) {//机构结束日期
                hrfield = "(select END_DATE from organization where GUIDKEY = UNIQUE_ID)";
            } else if ("K01AI".equalsIgnoreCase(hrfield)) {//岗位生效日期
                hrfield = "(select START_DATE from organization where GUIDKEY = UNIQUE_ID)";
            } else if ("K01AJ".equalsIgnoreCase(hrfield)) {//岗位撤销日期
                hrfield = "(select END_DATE from organization where GUIDKEY = UNIQUE_ID)";
            }
            String destfield = bean.get("destfield").toString();
            sql.append(hrfield);
            sql.append(" ");
            sql.append(destfield);
            if (i == fieldRef.size() - 1) {
                sql.append(" ");
            } else {
                sql.append(",");
            }
        }
        if ("t_hr_view".equals(map.get("hrtable"))) {
            sql.append(",(select FILEID from usra00 where UsrA00.Flag = 'P' and i9999 = 1 and a0100 = t_hr_view.A0100 ) fileid");
        }
        sql.append(" from ");
        sql.append((String) map.get("hrtable"));
        if ("t_org_view".equals(map.get("hrtable"))) {
            sql.append(" where B01AA is not null and B01AD is not null");
        } else if ("t_post_view".equals(map.get("hrtable"))) {
            sql.append(" where K01AH is not null and CODEITEMDESC is not null");
        } else {
            sql.append(" where A0177 is not null and A0144 is not null and A0101 is not null");
        }

        ResultSet search = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql.toString());
            search = ps.executeQuery();
            while (search.next()) {
                list = new ArrayList<String>();
                for (int i = 0; i < fieldRef.size(); i++) {
                    bean = fieldRef.get(i);
                    String destField = (String) bean.get("destfield");
                    String hrField = (String) bean.get("hrfield");
                    // 是否按类型获取？？？？
                    destField = search.getString(destField);
					/*ResultSetMetaData metaData = search.getMetaData();
					int columnType = metaData.getColumnType(i+1);

					if(columnType == Types.VARCHAR) {
						destField = search.getString(destField);
					}else if(columnType == Types.TIMESTAMP){
						java.sql.Date date = search.getDate(destField);
					}*/
                    // 日期类特殊处理
                    FieldItem fieldItem = DataDictionary.getFieldItem(hrField);
                    if (fieldItem != null) {
                        String itemtype = fieldItem.getItemtype();
                        if ("D".equalsIgnoreCase(itemtype)) {
                            if (destField != null && !"".equals(destField)) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = df.parse(destField);
                                destField = df.format(date);
                                destField = destField.replace("-", "");
                            }
                        }
                    }
                    list.add(destField);
                }
                if ("t_hr_view".equals(map.get("hrtable"))) {
                    String fileid = search.getString("fileid");
                    if (mappingMap.containsKey(PubFunc.decrypt(fileid))) {
                        LazyDynaBean ldb = mappingMap.get(PubFunc.decrypt(fileid));
                        list.add(ldb.get("filelocation") + "");
                        list.add(ldb.get("busino") + "");
                    } else {
                        list.add("");
                        list.add("");
                    }
                }
                lists.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(search);
            PubFunc.closeDbObj(ps);
        }

        return lists;
    }

    /**
     * 获取子集信息
     *
     * @param map
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    private List<List> getSubSetInfo(Map<String, Object> map) throws SQLException, ParseException {
        // 用于返回的list
        List<List> lists = new ArrayList<List>();
        List<String> list = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        // 添加特殊处理的字段
        sql.append((String) map.get("relationfield"));
        sql.append(",");
        sql.append("i9999");
        sql.append(",");

        List<LazyDynaBean> fieldRef = (List<LazyDynaBean>) map.get("field_ref");
        LazyDynaBean bean = null;
        for (int i = 0; i < fieldRef.size(); i++) {
            bean = fieldRef.get(i);
            sql.append(bean.get("hrfield").toString());
            sql.append(" ");
            sql.append(bean.get("destfield").toString());
            if (i == fieldRef.size() - 1) {
                sql.append(" ");
            } else {
                sql.append(",");
            }
        }
        sql.append(" from ");
        sql.append((String) map.get("hrtable"));
        //sql.append(" where sys_flag in (1, 2, 0)");
        PreparedStatement ps = null;
        ResultSet search = null;
        try {
            ps = conn.prepareStatement(sql.toString());
            search = ps.executeQuery();
            while (search.next()) {
                list = new ArrayList<String>();
                // ......处理特殊字段开始

                String relationfieldValue = search.getString((String) map.get("relationfield"));
                list.add(getKeyValue(relationfieldValue, map));
                list.add(search.getString("i9999"));

                // ......处理特殊字段结束
                for (int i = 0; i < fieldRef.size(); i++) {

                    bean = fieldRef.get(i);
                    String destField = (String) bean.get("destfield");
                    String hrField = (String) bean.get("hrfield");
                    // 是否按类型获取？？？？
                    destField = search.getString(destField);
                    // 日期类特殊处理
                    FieldItem fieldItem = DataDictionary.getFieldItem(hrField);
                    if (fieldItem != null && destField != null) {
                        String itemtype = fieldItem.getItemtype();
                        if ("D".equalsIgnoreCase(itemtype)) {
                            if (!"".equals(destField) && destField != null) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = df.parse(destField);
                                destField = df.format(date);
                                destField = destField.replace("-", "");
                            }
                        }
                    }
                    list.add(destField);
                }
                lists.add(list);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(search);
            PubFunc.closeResource(ps);
        }
        return lists;
    }

    /**
     * 初始化工具类
     *
     * @return
     */
    private FtpUtilBo initFtpUtilBo() {
        /** FTP地址 **/
        String FTP_ADDRESS = (String) ftpBean.get("ip");
        /** FTP端口 **/
        int FTP_PORT = Integer.parseInt((String) ftpBean.get("port"));
        /** FTP用户名 **/
        String FTP_USERNAME = (String) ftpBean.get("username");
        /** FTP密码 **/
        String FTP_PASSWORD = (String) ftpBean.get("password");
        /** FTP基础目录 **/
        String BASE_PATH = (String) ftpBean.get("pathBase");
        String seq = BASE_PATH.substring(BASE_PATH.length() - 1);
        // 按照大数据的规则对日期进行特殊处理
        if ("/".equals(seq)) {
            BASE_PATH = BASE_PATH + getNowDateStr();
        } else {
            BASE_PATH = BASE_PATH + "/" + getNowDateStr();
        }

        FtpUtilBo ftpUtilBo = new FtpUtilBo(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD, BASE_PATH);
        return ftpUtilBo;
    }

    /**
     * 根据子表的唯一性标识查找对外的唯一性标识的值
     *
     * @param relationfieldValue
     * @param map
     * @return
     */
    private String getKeyValue(String relationfieldValue, Map<String, Object> map) {
        String value = "";

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        sql.append((String) map.get("hrkey"));
        sql.append(" from ");
        sql.append((String) map.get("maintable"));
        sql.append(" where ");
        sql.append((String) map.get("relationfield"));
        sql.append(" = ");
        sql.append(relationfieldValue);
        ContentDAO dao = new ContentDAO(conn);
        RowSet search = null;
        try {
            search = dao.search(sql.toString());
            while (search.next()) {
                value = search.getString((String) map.get("hrkey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * 获取当前日期的八位str值
     *
     * @return
     */
    private String getNowDateStr() {

        StringBuffer dateStr = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        dateStr.append(new SimpleDateFormat("yyyyMMdd").format(calendar.getTime()));
        return dateStr.toString();
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
}
