package com.hjsj.hrms.module.gz.salarytemplate.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ApprovalSituationBo {

    private String salaryid;
    private UserView userView;
    private Connection conn;

    private ContentDAO dao;

    public ApprovalSituationBo(Connection conn, UserView userView) {
        this.setConn(conn);
        this.setUserView(userView);
        this.dao = new ContentDAO(this.getConn());
    }

    public ApprovalSituationBo(Connection conn, UserView userView, String salaryid) {
        this.setConn(conn);
        this.setUserView(userView);
        this.dao = new ContentDAO(this.getConn());
        this.setSalaryid(salaryid);
    }

    /**
     * 获取是否需要用到归档表
     *
     * @param filterSql
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    public boolean isNeedSalaryarchive(String filterSql) throws GeneralException {
        StringBuffer strSql = new StringBuffer();
        RowSet rs = null;
        strSql.append(" SELECT COUNT(*) AS NUM FROM SALARYARCHIVE WHERE 1=1 ").append(filterSql.toUpperCase().replaceAll("SALARYHISTORY", "SALARYARCHIVE"));
        try {
            rs = this.dao.search(strSql.toString());
            while (rs.next()) {
                if (rs.getInt("NUM") > 0) {
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return false;

    }

    /**
     * 获取所有具有已提交数据的年份
     *
     * @param salaryid
     * @param manager  管理员id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 21:23 2018/8/28
     */
    public ArrayList<CommonData> getSalaryHistroyYear(String salaryid, String manager) throws GeneralException {
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer();
        strSql.append("select " + Sql_switcher.year("A00Z2") + " as A00Z2 from gz_extend_log where salaryid=? and upper(userName)=upper(?) and sp_flag='06' group by " + Sql_switcher.year("A00Z2") + " order by A00Z2 desc ");
        ArrayList datalist = new ArrayList();
        datalist.add(salaryid);
        datalist.add(StringUtils.isBlank(manager) ? this.getUserView().getUserName() : manager);
        ArrayList<CommonData> list = new ArrayList();

        try {
            rs = this.dao.search(strSql.toString(), datalist);
            while (rs.next()) {
                CommonData commonData = new CommonData();
                commonData.setDataValue(rs.getString("A00Z2"));
                commonData.setDataName(rs.getString("A00Z2"));
                list.add(commonData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;

    }

    /**
     * 获取应用机构下拉列表store数据
     *
     * @param salaryid
     * @param userName
     * @return
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    public ArrayList<CommonData> getAgency(String salaryid, String userName) {
        SalaryCtrlParamBo ctrlParamBo = new SalaryCtrlParamBo(this.getConn(), Integer.parseInt(salaryid));
        ArrayList<CommonData> commonDataArrayList = new ArrayList();
        CommonData commonData = new CommonData("all", "全部");
        commonDataArrayList.add(commonData);
        List list = ctrlParamBo.getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);
            String id = element.getAttributeValue("org_id");

            if (StringUtils.isBlank(userName) || element.getAttributeValue("username").equalsIgnoreCase(userName)) {
                String type = id.substring(0, 2);
                String text = AdminCode.getCodeName(type, id.substring(2));
                commonData = new CommonData(id, text);
                commonDataArrayList.add(commonData);
            }
        }

        return commonDataArrayList;


    }

    /**
     * 获取应用机构数据
     *
     * @param salaryid
     * @param userName
     * @return
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    public ArrayList<String> getAgencyList(String salaryid, String userName) {
        SalaryCtrlParamBo ctrlParamBo = new SalaryCtrlParamBo(this.getConn(), Integer.parseInt(salaryid));
        ArrayList<String> commonDataArrayList = new ArrayList();

        List list = ctrlParamBo.getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);
            String id = element.getAttributeValue("org_id");
            if (StringUtils.isBlank(userName) || element.getAttributeValue("username").equalsIgnoreCase(userName)) {
                commonDataArrayList.add(id);
            }
        }

        return commonDataArrayList;


    }

    /**
     * 拼接权限sql
     *
     * @param tableName
     * @param filterAgency
     * @param ctrlparam
     * @return
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    public String buildstrSql(String tableName, ArrayList<String> filterAgency, SalaryCtrlParamBo ctrlparam) {
        StringBuffer strSql = new StringBuffer();
        String orgid = "";
        String deptid = "";

        //归属单位
        orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
        //归属部门
        deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid");
        if (StringUtils.isBlank(orgid) && StringUtils.isBlank(deptid)) {
            orgid = "B0110";
            deptid = "E0122";
        }

        if (StringUtils.isNotBlank(orgid) && StringUtils.isNotBlank(deptid)) {
            for (String str : filterAgency) {
                if (StringUtils.isBlank(str)) {
                    continue;
                }
                String id = str.substring(2);
                String type = str.substring(0, 2);
                strSql.append(" or " + tableName + "." + deptid + " LIKE '" + id + "%' ");
                if ("UN".equals(type)) {
                    strSql.append(" or " + tableName + "." + orgid + " LIKE '" + id + "%' ");
                }
            }


        } else if (StringUtils.isNotBlank(orgid)) {
            for (String str : filterAgency) {
                if (StringUtils.isBlank(str)) {
                    continue;
                }
                String id = str.substring(2);
                String type = str.substring(0, 2);
                if ("UN".equals(type)) {
                    strSql.append(" or " + tableName + "." + orgid + " LIKE '" + id + "%' ");
                }
            }
        } else if (StringUtils.isNotBlank(deptid)) {
            for (String str : filterAgency) {
                if (StringUtils.isBlank(str)) {
                    continue;
                }
                String id = str.substring(2);
                String type = str.substring(0, 2);
                strSql.append(" or " + tableName + "." + deptid + " LIKE '" + id + "%' ");
            }
        }
        if (strSql.length() == 0) {
            strSql.append("1=2 ");
        } else {
            strSql.delete(0, 3);
        }

        return strSql.toString();
    }


    /**
     * 获取非管理员且设置了应用机构，历史数据主页面数据
     *
     * @param strSql
     * @param filterYear
     * @param columnsInfos
     * @param agencyFilter 机构
     * @param ctrlparam
     * @return
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    public ArrayList getSelfHelpDataList(String strSql, String filterYear, ArrayList<ColumnsInfo> columnsInfos, ArrayList<String> agencyFilter, SalaryCtrlParamBo ctrlparam) {

        LinkedHashMap<String, LazyDynaBean> dataMap = new LinkedHashMap();
        LazyDynaBean tempbean = new LazyDynaBean();

        String orgid = "";
        String deptid = "";

        //归属单位
        orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
        //归属部门
        deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid");
        if (StringUtils.isBlank(orgid) && StringUtils.isBlank(deptid)) {
            orgid = "B0110";
            deptid = "E0122";
        }

        ArrayList<String> countList = this.getDateCountList(filterYear);

        //首先插入 应用机构-发放日期 行结构
        for (String s : agencyFilter) {
            for (String count : countList) {
                tempbean = new LazyDynaBean();
                for (ColumnsInfo columnsInfo : columnsInfos) {
                    String columnId = columnsInfo.getColumnId();
                    //应用机构id
                    if ("agencyid".equalsIgnoreCase(columnId)) {
                        tempbean.set(columnId, s);
                        continue;
                    }
                    //应用机构名称
                    if ("agencyname".equalsIgnoreCase(columnId)) {
                        tempbean.set(columnId, s.substring(2));
                        continue;
                    }
                    if ("a00z2".equalsIgnoreCase(columnId)) {
                        tempbean.set(columnId, count.split("//")[0]);
                        continue;
                    }
                    if ("a00z3".equalsIgnoreCase(columnId)) {
                        tempbean.set(columnId, Double.parseDouble(count.split("//")[1]));
                        continue;
                    }


                    if ("n".equalsIgnoreCase(columnsInfo.getColumnType())) {
                        tempbean.set(columnsInfo.getColumnId(), "");
                    } else {
                        tempbean.set(columnsInfo.getColumnId(), "");
                    }
                }
                dataMap.put(s.substring(2) + count, tempbean);
            }
        }

        RowSet rowSet = null;

        //其次根据行结构插入数据
        try {
            rowSet = this.dao.search(strSql);
            while (rowSet.next()) {
                String agency = "";
                String b0110 = "";
                String e0122 = "";
                if(StringUtils.isNotBlank(orgid)) {
                	b0110 = rowSet.getString(orgid);
                }
                if(StringUtils.isNotBlank(deptid)) {
                	e0122 = rowSet.getString(deptid);
                }
                for (String strAgency : agencyFilter) {
                	agency = e0122;
                	//如果应用机构是单位，则根据查出来的数据是单位的进行统计
                	if("UN".equalsIgnoreCase(strAgency.substring(0, 2))) {
                		agency = b0110;
                	}
                    if (StringUtils.isBlank(agency) || !agency.toUpperCase().startsWith(strAgency.substring(2).toUpperCase())) {
                        continue;
                    }
                    String a00z2 = rowSet.getString("a00z2");
                    String a00z3 = rowSet.getString("a00z3");

                    tempbean = dataMap.get(agency + a00z2 + "//" + a00z3);
                    for (ColumnsInfo columnsInfo : columnsInfos) {
                        String columnId = columnsInfo.getColumnId();
                        if ("agencyid".equalsIgnoreCase(columnId)) {
                            continue;
                        }
                        if ("agencyname".equalsIgnoreCase(columnId)) {
                            continue;
                        }
                        if ("a00z3".equalsIgnoreCase(columnId)) {
                            continue;
                        }


                        if ("N".equalsIgnoreCase(columnsInfo.getColumnType())) {
                            String str = rowSet.getString(columnId);
                            if (StringUtils.isNotBlank(str)) {
                                double value = Double.parseDouble(str);
                                value += StringUtils.isBlank((String) tempbean.get(columnId)) ? 0d : Double.parseDouble((String) tempbean.get(columnId));
                                tempbean.set(columnsInfo.getColumnId(), String.valueOf(value));
                            }
                        } else {
                            tempbean.set(columnsInfo.getColumnId(), rowSet.getString(columnId));
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }

        ArrayList selfHelpDataList = new ArrayList();
        Iterator iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LazyDynaBean> entry = (Map.Entry<String, LazyDynaBean>) iterator.next();
            LazyDynaBean bean=entry.getValue();
            double num= NumberUtils.isNumber((String)bean.get("num"))?Double.parseDouble((String)bean.get("num")):0d;
            if(num>0) {
                selfHelpDataList.add(entry.getValue());
            }

        }
        return selfHelpDataList;
    }

    /**
     * 获取薪资该年份所有发放次数
     *
     * @param filterYear
     * @return
     * @author ZhangHua
     * @date 21:26 2018/8/28
     */
    private ArrayList<String> getDateCountList(String filterYear) {

        ArrayList<String> list = new ArrayList();
        StringBuffer strSql = new StringBuffer();
        strSql.append(" select "+Sql_switcher.dateToChar("A00Z2","YYYY-MM-DD")+" as A00Z2,A00Z3 from gz_extend_log where sp_flag='06' AND ");
        strSql.append(Sql_switcher.year("A00Z2")).append("='").append(filterYear).append("' AND");
        strSql.append(" salaryid=").append(this.getSalaryid());
        strSql.append(" order by A00Z2 desc,A00Z3 ");
        RowSet rs = null;

        try {
            rs = this.dao.search(strSql.toString());
            while (rs.next()) {
                list.add(rs.getString("A00Z2") + "//" + rs.getString("A00Z3"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return list;
    }


    public String getOnlyName(SalaryCtrlParamBo ctrlparam){
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getConn());
        //是否定义唯一性指标 0：没定义
        String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
        String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
        if(!this.isExistFieldItem(onlyname)){
            return "";
        }
        return onlyname;

    }

    /**
     * 获取查询框内默认显示汉字内容
     * @return
     */
    public String getLookStr(SalaryCtrlParamBo ctrlparam){
        String onlyname=this.getOnlyName(ctrlparam);
        //动态显示查询框内容
        StringBuffer lookStr = new StringBuffer(ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName"));
        try{
            if(StringUtils.isNotBlank(onlyname)&&!"a0101".equalsIgnoreCase(onlyname)){
                lookStr.append(",");
                FieldItem item = DataDictionary.getFieldItem(onlyname);
                if(item!=null){
                    lookStr.append(item.getItemdesc());
                }else{
                    //唯一性指标
                    lookStr.append(ResourceFactory.getProperty("sys.options.param.uniquenesstarget"));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return lookStr.toString();
    }
    /**
     * 查找出唯一性指标是否在salaryset表中存在
     * @return
     */
    private boolean isExistFieldItem( String onlyName) {
        RowSet rowSet = null;
        try {
            if(StringUtils.isBlank(onlyName)){
                return false;
            }
            ContentDAO dao =new ContentDAO(this.getConn());
            String sql = "select 1 from salaryset where salaryid=? and upper(itemid)=?";
            ArrayList list = new ArrayList();
            list.add(this.getSalaryid());
            list.add(onlyName.toUpperCase());
            rowSet = dao.search(sql, list);
            if (rowSet.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }

        return false;
    }


    public String getSalaryid() {
        return salaryid;
    }

    public void setSalaryid(String salaryid) {
        this.salaryid = salaryid;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
