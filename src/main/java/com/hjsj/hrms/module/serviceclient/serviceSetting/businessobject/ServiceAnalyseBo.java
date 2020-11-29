/*
 * @(#)ServiceAnalyseBo.java 2018年3月9日上午10:56:08 hrms Copyright 2018 HJSOFT,
 * Inc. All rights reserved. HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 自助终端服务分析业务类
 * 
 * @Titile: ServiceAnalyseBo
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年3月9日上午10:56:08
 * @author: hssoft
 * @version 1.0
 *
 */
public class ServiceAnalyseBo {
    /**服务类型全部服务**/
    private static final String C_PRINT_SERVICE_ALL = "-1";

    /**终端机选中类型全部**/
    private static final String C_PRINT_CLIENTID_ALL = "-1";

    /**时间间隔类型 7天**/
    private static final String C_DATE_TYPE_WEEK = "week";

    /**时间间隔类型 30天**/
    private static final String C_DAE_TYPE_MONTH = "month";

    /**时间间隔类型365天**/
    private static final String C_DATE_TYPE_YEAR = "year";

    /** userView对象存储用户的相关信息 **/
    private UserView userView;

    /** conn,数据库链接资源 **/
    private Connection conn;

    /**
     * 构造方法
     * 
     * @param userView
     *            用户信息
     * @param conn
     *            数据库连接
     */
    public ServiceAnalyseBo(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 获取自助终端服务分析的相关数据
     */
    public Map<String, Object> getInitData() {
        List<Map<String, String>> clientDataList = getClientDatas();// 终端数据
        List<Map<String, String>> allPrintServicesDataList = getAllPrintServicesData();// 所有打印服务的数据
        String initclientId = StringUtils.EMPTY;//默认为空，防止终端机不存在导致报错
        if(CollectionUtils.isNotEmpty(clientDataList)) {
            initclientId = clientDataList.get(0).get("clientId");
        }
        Map<String, Object> chartData = getChartData(C_PRINT_SERVICE_ALL, C_DATE_TYPE_WEEK, initclientId);
        String gridConfig = getGridConfigData(C_PRINT_SERVICE_ALL, C_DATE_TYPE_WEEK, initclientId);
        Map<String, Object> param = new HashMap<String, Object>();
        Map<String, Object> analyseData = new HashMap<String, Object>();
        analyseData.put("chartData", chartData);
        analyseData.put("gridConfig", gridConfig);
        param.put("clientList", clientDataList);
        param.put("serviceList", allPrintServicesDataList);
        param.put("analyseData", analyseData);
        return param;
    }

    /**
     * 获取分析数据
     * @param formHM
     * @return 分析数据
     */
    @SuppressWarnings("rawtypes")
    public Map getAnalyseData(String printServices, String dateType, String printClients) {
        String gridConfig = getGridConfigData(printServices, dateType, printClients);
        Map<String, Object> chartData = getChartData(printServices, dateType, printClients);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("gridConfig", gridConfig);
        param.put("chartData", chartData);
        return param;
    }

    /**
     * 获取折线图分析展现的数据
     * @param printServices 打印服务类型 -1：全部,否则以逗号分割的serviceId
     * @param dateType 日期间隔类型  7天  30天  1年
     * @param printClientIds 终端机选中类型默认全选 -1,否则以逗号分割的clientId
     */
    public Map<String, Object> getChartData(String printServices, String dateType, String printClientIds) {
        Map<String, Object> chartData = getSelectChartDatas(printServices, printClientIds, dateType);
        return chartData;
    }

    /**
     * 获取七天内的分析数据
     * @param printServices 打印服务类型 -1：全部,否则以逗号分割的serviceId
     * @param printClientIds 终端机选中类型默认全选 -1,否则以逗号分割的clientId
     * @param dateType 日期类型  7天 30天 1年
     */
    private Map<String, Object> getSelectChartDatas(String printServices, String printClientIds, String dateType) {
        //返回到前端的数据
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String formatString = "yyyy-MM-dd";
        Date currentDate = new Date();
        List<String> paramList = new ArrayList<String>();//存放sql查询语句中的参数
        int count = 7;//默认相差数据间隔为7
        if (StringUtils.equals(C_DAE_TYPE_MONTH, dateType)) {
            count = 30;//一个月为30天
        } else if (StringUtils.equals(C_DATE_TYPE_YEAR, dateType)) {
            count = 12;//用年做划分的话是12个月
            formatString = "yyyy-MM";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        StringBuffer sqlBuffer = new StringBuffer();//sql查询语句
        sqlBuffer.append("select service.serviceid as realserviceid,name,histroy.* from t_sys_serviceclient_service service left join (");
        sqlBuffer.append("select ");
        List<String> columnList = new ArrayList<String>();//查询出来的字段名,即前端x轴的字段名
        List<String> serviceIds = new ArrayList<String>();//服务ids
        /**无论是按天分割还是按月分割都应当包含当前,即按天分割包含当天,按月分割应当包含当月**/
        for (int i = 1; i <= count; i++) {
            int diffDayOrMonth = i - count;//相差几天/月(一年内统计,应当为月)
            Date loopDate = DateUtils.addDays(currentDate, diffDayOrMonth);//得到循环中的日期
            if (StringUtils.equals(C_DATE_TYPE_YEAR, dateType)) {//如果是年的话,处理函数应当不一样
                loopDate = DateUtils.addMonths(currentDate, diffDayOrMonth);
            }
            String asLoopDate = format.format(loopDate);//循环中的别名
            columnList.add(asLoopDate);//将字段名增加到list中
            sqlBuffer.append(" sum(case (");
            if (!StringUtils.equals(C_DATE_TYPE_YEAR, dateType)) {
                sqlBuffer.append(Sql_switcher.diffDays(Sql_switcher.charToDate(Sql_switcher.dateToChar("servetime")), Sql_switcher.today()));
            } else {//年
                sqlBuffer.append(Sql_switcher.diffMonths("servetime",Sql_switcher.today()));
            }
            sqlBuffer.append(") when ").append(diffDayOrMonth).append(" then 1 else 0 end ) as ").append("\"" + asLoopDate + "\"");
            sqlBuffer.append(",");
        }
        sqlBuffer.append("serviceid");
        sqlBuffer.append(" from t_sys_serviceclient_histroy");
        sqlBuffer.append(" where 1=1 ");

        boolean isAllServiceSelect = StringUtils.equals(C_PRINT_SERVICE_ALL, printServices);//是否是全选所有服务
        boolean isAllClientSelect = StringUtils.equals(C_PRINT_CLIENTID_ALL, printClientIds);//是否是全选所有的终端机

        if (!isAllServiceSelect) {//不是所有服务全选
            String[] serviceArray = printServices.split(",");
            sqlBuffer.append(" and serviceid in (");
            for (int i = 0; i < serviceArray.length; i++) {
                if (i < serviceArray.length - 1) {
                    sqlBuffer.append("?,");
                } else {
                    sqlBuffer.append("?)");
                }
                paramList.add(serviceArray[i]);
            }
        }
        if (!isAllClientSelect) {//不是终端机全选
            String[] clientIdArray = printClientIds.split(",");
            sqlBuffer.append(" and clientid in (");
            for (int i = 0; i < clientIdArray.length; i++) {
                if (i < clientIdArray.length - 1) {
                    sqlBuffer.append("?,");
                } else {
                    sqlBuffer.append("?)");
                }
                paramList.add(clientIdArray[i]);
            }
        }
        sqlBuffer.append(" group by serviceid");
        sqlBuffer.append(") histroy on  histroy.serviceid = service.serviceid where service.type = 1");
        if (!isAllServiceSelect) {//不是所有服务全选
            String[] serviceArray = printServices.split(",");
            sqlBuffer.append(" and service.serviceid in (");
            for (int i = 0; i < serviceArray.length; i++) {
                if (i < serviceArray.length - 1) {
                    sqlBuffer.append("?,");
                } else {
                    sqlBuffer.append("?)");
                }
                paramList.add(serviceArray[i]);
            }
        }
        sqlBuffer.append(" order by service.serviceid");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        List<Map<String, Object>> seriesData = new ArrayList<Map<String, Object>>();
        try {
            rs = dao.search(sqlBuffer.toString(), paramList);
            List<String> legendDataList = new ArrayList<String>();
            while (rs.next()) {
                Map<String, Object> dataMap = new HashMap<String, Object>();//每一条折线上的数据
                String serviceName = rs.getString("name");
                serviceName = PubFunc.hireKeyWord_filter(serviceName);
                String serviceid = rs.getString("serviceid");
                dataMap.put("serviceid", serviceid);
                serviceIds.add(serviceid);
                List<String> dataListValue = new ArrayList<String>();
                for (String column : columnList) {
                    String value = rs.getString(column);
                    if(StringUtils.isBlank(value)) {
                        value = "0";
                    }
                    dataListValue.add(value);
                }
                dataMap.put("data", dataListValue);
                dataMap.put("type", "line");
                dataMap.put("name", serviceName);
                legendDataList.add(serviceName);
                seriesData.add(dataMap);
            }
            Map<String, List<String>> legendMap = new HashMap<String, List<String>>();
            legendMap.put("data", legendDataList);
            resultMap.put("xAxisData", columnList);
            resultMap.put("seriesData", seriesData);
            resultMap.put("legend", legendMap);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return resultMap;
    }

    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId id
     * @param columnDesc 名称
     * @param columnDesc 显示列宽
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth,String columnType) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);//显示列宽
        columnsInfo.setColumnLength(200);// 显示长度 
        columnsInfo.setReadOnly(true);// 是否只读
        columnsInfo.setOrdertype("0");
        if("N".equals(columnType)) {
        columnsInfo.setTextAlign("right");}
        return columnsInfo;
    }

    /**
     * 获取列头、表格渲染
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnList() {
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        columnTmp.add(getColumnsInfo("b0110", "单位名称", 200,"A"));
        columnTmp.add(getColumnsInfo("e0122", "部门", 120,"A"));
        columnTmp.add(getColumnsInfo("a0101", "姓名", 100,"A"));
        columnTmp.add(getColumnsInfo("servicename", "应用服务名称", 150,"A"));
        columnTmp.add(getColumnsInfo("printcount", "打印份数", 80,"N"));
        columnTmp.add(getColumnsInfo("clientname", "来自于终端", 100,"A"));
        columnTmp.add(getColumnsInfo("servetime", "操作时间", 120,"A"));
        return columnTmp;
    }

    /**
     * 获取列表分析展现的数据
     * @param printServices 打印服务类型 -1：全部,否则以逗号分割的serviceId
     * @param dateType 日期间隔类型  7天  30天  1年
     * @param printClients  终端机 -1：全部,否则以逗号分割的clientId
     */
    private String getGridConfigData(String printServices, String dateType, String printClients) {
        ContentDAO dao = new ContentDAO(this.conn);
        DbNameBo dbbo = new DbNameBo(this.conn);
        RowSet rs = null;
        ArrayList<ColumnsInfo> columnsInfo = getColumnList();
        ArrayList paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ArrayList<RecordVo> nbaseList = new ArrayList<RecordVo>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        /** 加载表格 */
        TableConfigBuilder builder = new TableConfigBuilder("optionsDetail", columnsInfo, "optionsDetail", userView, this.conn);
        
        builder.setAutoRender(false);//不设置页面自动渲染
        builder.setLockable(false);
        builder.setSortable(true);//不排序
        builder.setPageSize(15);
        boolean isEmptyServices = StringUtils.isBlank(printServices);//是否空的服务
        boolean isEmptyClients = StringUtils.isBlank(printClients);//是否是空的终端机
        
        String defaultFiled = "username";//默认使用username进行数据关联
        String loginField = dbbo.getLogonUserNameField();//当前设置的登录认证指标
        if(StringUtils.isNotBlank(loginField)) {
            defaultFiled = loginField;
        }
        
        //获取所有数据库的前缀
        try {
             nbaseList = dbbo.getAllLoginDbNameList();
            
        } catch (GeneralException e1) {
            e1.printStackTrace();
        }
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("h.A0101, s.name  serviceName, c.name  clientName,  h.printCount, h.servetime from t_sys_serviceclient_histroy h");
        sqlStr.append(" left join t_sys_serviceclient_service s on h.serviceid = s.serviceid left join t_sys_serviceclient_client c on h.clientid = c.clientid ");
        if (!isEmptyServices && !isEmptyClients) {
            for (int j = 0; j < nbaseList.size(); j++) {
                RecordVo dbvo =  (RecordVo)nbaseList.get(j);
                String nbase = dbvo.getString("pre");
            sql.append("select ").append(nbase).append(".B0110,").append(nbase).append(".E0122,");
            sql.append(sqlStr);
            sql.append("left join ").append(nbase).append("A01 ").append(nbase).append(" on h.username").append("= ").append(nbase);
            sql.append(".").append(defaultFiled);
            sql.append(" where h.servetime>=");

            String diff = "-6";
            if (C_DAE_TYPE_MONTH.equals(dateType)) {
                diff = "-29";
            }
            if (C_DATE_TYPE_YEAR.equals(dateType)) {
                diff = "-11";
            }
            if (C_DATE_TYPE_YEAR.equals(dateType)) {
                sql.append(Sql_switcher.addMonths(Sql_switcher.sqlNow(), diff));
            } else {
                sql.append(Sql_switcher.addDays(Sql_switcher.sqlNow(), diff));
            }

            if (!C_PRINT_SERVICE_ALL.equals(printServices)) {
                String[] serviceIds = printServices.split(",");
                sql.append("and h.serviceid in(");
                for (int i = 0; i < serviceIds.length; i++) {
                    if (i < serviceIds.length - 1) {
                        sql.append("?,");
                    } else {
                        sql.append("?)");
                    }
                    paramList.add(serviceIds[i]);
                }

            }
            if (!C_PRINT_CLIENTID_ALL.equals(printClients)) {
                if (StringUtils.isNotEmpty(printClients)) {
                    String[] clientIds = printClients.split(",");
                    sql.append(" and h.clientid in( ");
                    for (int i = 0; i < clientIds.length; i++) {
                        if (i < clientIds.length - 1) {
                            sql.append("?,");
                        } else {
                            sql.append("?)");
                        }
                        paramList.add(clientIds[i]);
                    }
                }
            }
            sql.append(" and").append(" h.username in").append("(select ").append(defaultFiled).append(" from ").append(nbase).append("A01 )");
            if(nbaseList.size()>1&&j<nbaseList.size()-1) {
            sql.append(" union  ");
                }
            }
            String orderby = " order by servetime desc";
            sql.append(orderby);
            try {
                rs = dao.search(sql.toString(),paramList);
                while (rs.next()) {
                    LazyDynaBean bean = new LazyDynaBean();
                    Date servetime = rs.getDate("servetime");
                    bean.set("b0110", AdminCode.getCodeName("UN", rs.getString("B0110")));
                    bean.set("e0122", AdminCode.getCodeName("UM", rs.getString("E0122")));

                    bean.set("a0101", rs.getString("A0101"));
                    bean.set("servicename", rs.getString("serviceName"));
                    bean.set("clientname", rs.getString("clientName"));
                    bean.set("printcount", rs.getInt("printCount"));
                    bean.set("servetime", df.format(servetime));
                    dataList.add(bean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                PubFunc.closeResource(rs);
            }
        }
        builder.setDataList(dataList);
        String config = builder.createExtTableConfig();
        return config;
    }

    /**
     * 获取自助服务的终端数据
     * 
     * @return
     */
    private List<Map<String, String>> getClientDatas() {
        StringBuffer sqlBuffer = new StringBuffer();// sqlBuffer,数据库查询语句
        sqlBuffer.append("select clientid,name,ip_address,pageCount from t_sys_serviceclient_client order by clientid");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        List<Map<String, String>> clientDataList = new ArrayList<Map<String, String>>();
        try {
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()) {
                Map<String, String> clientData = new HashMap<String, String>();
                // 终端ID
                String clientId = rs.getString("clientid");
                // 终端名称
                String name = rs.getString("name");
                // 终端IP地址
                String ip = rs.getString("ip_address");
                // 剩余打印页数
                String pageCount = rs.getString("pageCount");
                clientData.put("clientId", clientId);
                clientData.put("name", name);
                clientData.put("ip_address", ip);
                clientData.put("pageCount", pageCount);
                clientDataList.add(clientData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return clientDataList;
    }

    /**
     * 获得所有打印服务的list
     * 
     * @return
     */
    private List<Map<String, String>> getAllPrintServicesData() {
        StringBuffer sqlBuffer = new StringBuffer();// sqlBuffer,数据库查询语句
        // 只查询打印服务的数据
        sqlBuffer.append("select serviceid,name,icon from t_sys_serviceclient_service where type=1 order by serviceid");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        List<Map<String, String>> servicesDataList = new ArrayList<Map<String, String>>();
        try {
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()) {
                Map<String, String> serviceData = new HashMap<String, String>();
                // 服务ID
                String serviceid = rs.getString("serviceid");
                // 服务名称
                String name = rs.getString("name");
                // 服务的图片名称
                String icon = rs.getString("icon");
                serviceData.put("serviceid", serviceid);
                serviceData.put("name", name);
                serviceData.put("icon", icon);
                servicesDataList.add(serviceData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return servicesDataList;
    }

    /**
     * 更新终端纸张数
     * 
     * @param pageCount
     *            纸张数
     */
    public void updateClient(int pageCount, int clientId) {

        try {
            ContentDAO dao = new ContentDAO(this.conn);

            RecordVo vo = new RecordVo("t_sys_serviceclient_client");
            vo.setInt("clientid", clientId);
            vo.setInt("pagecount", pageCount);

            dao.updateValueObject(vo);
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除终端
     * @param clientId  终端id
     */
    public void deleteClient(int clientId) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            RecordVo vo = new RecordVo("t_sys_serviceclient_client");
            vo.setInt("clientid", clientId);

            dao.deleteValueObject(vo);
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增终端
     * @param formHM   参数集合
     * @return clientId 终端机id
     */
    public int addClient(Map formHM) {
    	int clientId = 0;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            clientId = getClientId(dao);

            RecordVo vo = new RecordVo("t_sys_serviceclient_client");
            vo.setInt("clientid", clientId);
            vo.setString("name", (String) formHM.get("name"));
            vo.setString("ip_address", (String) formHM.get("ip_address"));
            vo.setInt("pagecount", Integer.parseInt((String) formHM.get("pageCount")));
            vo.setString("description", (String) formHM.get("description"));

            dao.addValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientId;
    }

    /**
     * 获取clientId
     * @return
     */
    private int getClientId(ContentDAO dao) {
        int clientId = 0;
        RowSet rowSet = null;
        try {
            String sql = "SELECT MAX(clientid) as num FROM t_sys_serviceclient_client";
            rowSet = dao.search(sql);
            int clientIdMax = 0;
            if (rowSet.next()) {
                if (rowSet.getInt("num") == 0) {
                    clientId = 1;
                } else {
                    clientIdMax = rowSet.getInt("num");
                    clientId = clientIdMax + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return clientId;
    }
    /**
     * 获取已有的终端机名称
     * @return
     */
    public Map<String,String> check(String name,String ip) {
        StringBuffer sqlBuffer = new StringBuffer();// sqlBuffer,数据库查询语句
        List<String> param = new ArrayList<String>();
        sqlBuffer.append("select name,ip_address from t_sys_serviceclient_client where name = ? or ip_address = ?");
        param.add(name);
        param.add(ip);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        Map<String,String> messageMap = new HashMap<String,String>();
        try {
            rs = dao.search(sqlBuffer.toString(),param);
            while(rs.next()) {
                String selectName =rs.getString("name");
                String selectIp = rs.getString("ip_address");
                if(name.equals(selectName)) {
                    messageMap.put("name", "true");
                }
                if(ip.equals(selectIp)) {
                    messageMap.put("ip", "true");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return messageMap;
    }

    /**
     * @return the userView
     */
    public UserView getUserView() {
        return userView;
    }

    /**
     * @param userView
     *            the userView to set
     */
    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * @param conn
     *            the conn to set
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }


}
