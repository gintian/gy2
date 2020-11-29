package com.hjsj.hrms.module.certificate.dashboard.businessobject;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.utils.CertificatePrivBo;
import com.hjsj.hrms.module.certificate.utils.CertificateUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @Title:        CertificateDashboardBo.java
 * @Description:  证书管理门户数据方法Bo类
 * @Company:      hjsj
 * @Create time:  2018-5-23 上午11:02:23
 * @author        linbz
 * @version       1.0
 */
public class CertificateDashboardBo {

    private Connection conn;
    private UserView userView;
    CertificateConfigBo certificateConfigBo;
    CertificatePrivBo certificatePrivBo;

    public CertificateDashboardBo(Connection conn, UserView userView) {

        this.conn = conn;
        this.userView = userView;
        this.certificateConfigBo = new CertificateConfigBo(this.conn, this.userView);
        this.certificatePrivBo = new CertificatePrivBo();
    }

    /**
     * 校验证书配置是否齐全
     * @return
     * @throws GeneralException
     */
    public boolean checkCerConfig() throws GeneralException{

        boolean cerflag = true;
        try {
            // 48651 校验是否构库
            String flag = certificateConfigBo.checkCertMap(certificateConfigBo.getCertMap());
            if("1".equals(flag))
                return false;

            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            if(null==dbnames || dbnames.size() < 1)
                return false;
            // 证书类别代码类
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            if(StringUtils.isBlank(certCategoryCode))
                return false;
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            if(StringUtils.isBlank(certSubset))
                return false;
            // 证书名称
            String certName = certificateConfigBo.getCertName();
            if(StringUtils.isBlank(certName))
                return false;
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            if(StringUtils.isBlank(certCategoryItemId))
                return false;
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            if(StringUtils.isBlank(certNOItemId))
                return false;
            // 证书到期指标
            String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
            if(StringUtils.isBlank(certEndDateItemId))
                return false;
            // 证书状态
            String certStatus = certificateConfigBo.getCertStatus();
            if(StringUtils.isBlank(certStatus))
                return false;
            // 证书所属组织
            String certOrganization = certificateConfigBo.getCertOrganization();
            if(StringUtils.isBlank(certOrganization))
                return false;
            // 证书是否借出
            String certBorrowState = certificateConfigBo.getCertBorrowState();
            if(StringUtils.isBlank(certBorrowState))
                return false;
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            if(StringUtils.isBlank(certBorrowSubset))
                return false;

            certificateConfigBo.checkCategoryFieldItem();
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return cerflag;
    }
    /**
     * 管理员门户-获取证书总数
     * @return
     * @throws GeneralException
     */
    public HashMap getTotalNum() throws GeneralException{

        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书状态
            String certStatus = certificateConfigBo.getCertStatus();

            // 证书总数
            int certTotalNum = 0;
            // 持证人数
            int empCertTotalNum = 0;
            // 总人数
            int empTotalNum = 0;
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");
            // 人员权限条件
            String privCode = "";
            if (!userView.isSuper_admin())
                privCode = certificatePrivBo.getB0110(this.userView);
            StringBuffer whereStr = new StringBuffer("");
            String[] privCodes = privCode.split("`");
            for (int i = 0; i < privCodes.length; i++) {
                if (i != 0)
                    whereStr.append(" or ");
                whereStr.append(" b0110 like '" + privCodes[i] + "%' ");
            }

            ContentDAO dao = new ContentDAO(conn);
            StringBuffer certTotalsql = new StringBuffer("");
            StringBuffer empCertsql = new StringBuffer("");
            StringBuffer empTotalsql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {

                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                // 查证书总数 	distinct
                certTotalsql.append("select ").append(certNOItemId).append(",").append(certCategoryItemId);
                certTotalsql.append(" from ").append(dbname).append(certSubset);
                // 证书状态为可用01  或 为空
                certTotalsql.append(" where (").append(certStatus).append("='01' "
                        + "or ").append(certStatus).append(" IS NULL or ").append(certStatus).append("='') ");
                certTotalsql.append(" and ").append(sqlWhere);

                // 查持证人数  	 COUNT(DISTINCT A0100) num
                empCertsql.append("select COUNT(DISTINCT A0100) num");
                empCertsql.append(" from ").append(dbname).append(certSubset);
                // 证书状态为可用01  或 为空
                empCertsql.append(" where (").append(certStatus).append("='01' "
                        + "or ").append(certStatus).append(" IS NULL or ").append(certStatus).append("='') ");
                empCertsql.append(" and ").append(sqlWhere);

                // 查总人数
                empTotalsql.append("select COUNT(DISTINCT A0100) num");
                empTotalsql.append(" from ").append(dbname).append("A01");
                if(StringUtils.isNotEmpty(whereStr.toString()))
                    empTotalsql.append(" where (").append(whereStr.toString()).append(") ");

                if(i < dbnames.size()-1) {
                    certTotalsql.append(" union all ");
                    empCertsql.append(" union all ");
                    empTotalsql.append(" union all ");
                }
            }

            // 查证书总数 distinct
            if(StringUtils.isNotBlank(certTotalsql.toString())) {
                rs = dao.search(certTotalsql.toString());
                while(rs.next()) {
                    certTotalNum++;
                }
            }

            // 查持证人数
            if(StringUtils.isNotBlank(empCertsql.toString())) {

                rs = dao.search(empCertsql.toString());
                while(rs.next()) {
                    empCertTotalNum += rs.getInt("num");
                }
            }

            // 查总人数
            if(StringUtils.isNotBlank(empTotalsql.toString())) {

                rs = dao.search(empTotalsql.toString());
                while(rs.next()) {
                    empTotalNum += rs.getInt("num");
                }
            }

            map.put("certTotalNum", certTotalNum+"");
            map.put("empCertTotalNum", empCertTotalNum+"");
            map.put("empTotalNum", empTotalNum+"");

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 获取证书表格控件对象
     * @param subModuleId
     * @param canBowFlag
     * @return
     * @throws GeneralException
     */
    public String getTableConfigCers(String subModuleId, String whereCode) throws GeneralException {

        String config = "";
        try {
            // 获取列头集合
            ArrayList list = this.getManagerSqlColumns(subModuleId, whereCode);

            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, (ArrayList<ColumnsInfo>)list.get(1)
                    , subModuleId, this.userView, this.conn);

            builder.setDataSql((String)list.get(0));
            builder.setOrderBy(" order by nbase, A0100");
            builder.setColumnFilter(true);//统计过滤
            builder.setScheme(false);//栏目设置
            builder.setSelectable(false);//选框
            builder.setEditable(false);//表格编辑
            builder.setPageSize(20);//每页条数
            builder.setLockable(true);
            config = builder.createExtTableConfig();

        }catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return config;
    }

    /**
     * 获取列集合 与 SQL
     * @param whereCode  分类代码
     * @return
     * @throws GeneralException
     */
    public ArrayList getManagerSqlColumns(String subModuleId, String whereCode)throws GeneralException {

        // 证书人员库
        ArrayList dbnames = certificateConfigBo.getCertNbase();
        if(null==dbnames || dbnames.size() < 1)
            return null;
        // 证书类别代码类
        String certCategoryCode = certificateConfigBo.getCertCategoryCode();
        if(StringUtils.isBlank(certCategoryCode))
            return null;
        // 证书信息集
        String certSubset = certificateConfigBo.getCertSubset();
        if(StringUtils.isBlank(certSubset))
            return null;
        // 证书类别指标
        String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
        if(StringUtils.isBlank(certCategoryItemId))
            return null;
        // 证书名称
        String certName = certificateConfigBo.getCertName();
        if(StringUtils.isBlank(certName))
            return null;
        // 证书编号
        String certNOItemId = certificateConfigBo.getCertNOItemId();
        if(StringUtils.isBlank(certNOItemId))
            return null;
        // 证书到期指标
        String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
        if(StringUtils.isBlank(certEndDateItemId))
            return null;
        // 证书状态
        String certStatus = certificateConfigBo.getCertStatus();
        if(StringUtils.isBlank(certStatus))
            return null;
        // 证书所属组织
        String certOrganization = certificateConfigBo.getCertOrganization();
        if(StringUtils.isBlank(certOrganization))
            return null;

        // 证书权限条件
        String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "a");

        StringBuffer certTotalsql = new StringBuffer("");
        for(int i=0;i<dbnames.size();i++) {

            String dbname = (String)dbnames.get(i);
            if(StringUtils.isBlank(dbname))
                continue;

            // 查证书总数 distinct
            certTotalsql.append("select '"+dbname+"'nbase,b.A0100,b.A0101,a.").append(certCategoryItemId).append(",a.").append(certNOItemId);
            certTotalsql.append(",a.").append(certName).append(",a.").append(certStatus).append(",a.").append(certEndDateItemId);
            certTotalsql.append(",a.").append(certOrganization);
            certTotalsql.append(" from ").append(dbname).append(certSubset);
            certTotalsql.append(" a left join ").append(dbname).append("A01 b on a.A0100=b.A0100 ");
            // 证书状态为可用01  或 为空
            certTotalsql.append(" where (a.").append(certStatus).append("='01' "
                    + "or a.").append(certStatus).append(" IS NULL or a.").append(certStatus).append("='') ");
            if(StringUtils.isNotEmpty(whereCode) && "allCers_02".equalsIgnoreCase(subModuleId)) {
                certTotalsql.append(" and a."+certCategoryItemId+" like '"+whereCode+"%' ");
            }else if(StringUtils.isNotEmpty(whereCode) && "allCers_03".equalsIgnoreCase(subModuleId)) {
                certTotalsql.append(" and ").append(whereCode);
            }
            // 增加权限条件
            certTotalsql.append(" and ").append(sqlWhere);

            if(i < dbnames.size()-1)
                certTotalsql.append(" union all ");
        }
        StringBuffer sql = new StringBuffer("");
        if(StringUtils.isNotEmpty(certTotalsql.toString())) {
            sql.append("select * from (").append(certTotalsql.toString()).append(") m where 1=1");
//					sql.append(" order by nbase, A0100");
        }

        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo = new ColumnsInfo();
        // 证书类别指标
        FieldItem item = DataDictionary.getFieldItem(certCategoryItemId, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 证书编号
        item = DataDictionary.getFieldItem(certNOItemId, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 证书名称
        item = DataDictionary.getFieldItem(certName, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 姓名
        item = DataDictionary.getFieldItem("A0101", "A01");
        columnsInfo = getColumnsInfoByFi(item, 80);
        columnsList.add(columnsInfo);
        // 证书归属组织
        item = DataDictionary.getFieldItem(certOrganization, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 证书到期指标
        item = DataDictionary.getFieldItem(certEndDateItemId, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 证书状态
        item = DataDictionary.getFieldItem(certStatus, certSubset);
        columnsInfo = getColumnsInfoByFi(item, 70);
        columnsList.add(columnsInfo);

//		columnsInfo = getColumnsInfo("borrowing_id", "操作", 180, "0", "A", 100, 0, "");
//		columnsInfo.setRendererFunc("employeeDashboard.borrowingOneCer");
//		columnsInfo.setTextAlign("center");
//		columnsList.add(columnsInfo);

        ArrayList list = new ArrayList();
        list.add(certTotalsql.toString());
        list.add(columnsList);

        return list;
    }
    /**
     * 借阅表格控件
     * @param subModuleId
     * @param whereCode
     * @return
     * @throws GeneralException	overdueCers
     */
    public String getTableConfigOverdueCers(String subModuleId, String whereCode) throws GeneralException {

        String config = "";
        try {
            // 获取列头集合  借阅表格控件
            ArrayList list = this.getOverdueCersSqlColumns(subModuleId, whereCode);

            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, (ArrayList<ColumnsInfo>)list.get(1)
                    , subModuleId, this.userView, this.conn);

            builder.setDataSql((String)list.get(0));
            builder.setOrderBy(" order by nbase, A0100");
            builder.setColumnFilter(true);//统计过滤
            builder.setScheme(false);//栏目设置
            builder.setSelectable(false);//选框
            builder.setEditable(false);//表格编辑
            builder.setPageSize(20);//每页条数
            builder.setLockable(true);
            config = builder.createExtTableConfig();

        }catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return config;
    }

    /**
     * 获取逾期未还 列集合 与 SQL
     * @param whereCode  分类代码
     * @return
     * @throws GeneralException
     */
    public ArrayList getOverdueCersSqlColumns(String subModuleId, String whereCode)throws GeneralException {

        // 证书人员库
        ArrayList dbnames = certificateConfigBo.getCertNbase();
        // 取参数借阅子集
        String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
        // 证书信息集
        String certSubset = certificateConfigBo.getCertSubset();
        // 证书类别
        String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
        // 证书编号
        String certNOItemId = certificateConfigBo.getCertNOItemId();
        // 证书权限条件
        String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

        String nowdate = DateUtils.format(new Date(), "yyyy-MM-dd");
        // 借阅集查询
        StringBuffer bowSql = new StringBuffer("");
        // 证书集查询
        StringBuffer cerSql = new StringBuffer("");
        for(int i=0;i<dbnames.size();i++) {

            String dbname = (String)dbnames.get(i);
            if(StringUtils.isBlank(dbname))
                continue;
            // 借阅集信息
            bowSql.append("select '"+dbname+"' nbase,c.A0101,c.A0100,"+certBorrowSubset + "01 browCertCategory,"+certBorrowSubset+"03 browCertNum,"
                    +certBorrowSubset + "05,"+certBorrowSubset+"07,"+certBorrowSubset+"09,"+certBorrowSubset + "11");
            bowSql.append(" from ").append(dbname).append(certBorrowSubset).append(" a ");
            bowSql.append(" left join ").append(dbname).append("A01 c on a.A0100=c.A0100");
            bowSql.append(" where a.").append(certBorrowSubset+"19='03' ");
            bowSql.append(" and a.").append(certBorrowSubset+"23='2' ");
            // 当前日期超过 预计归还日期
            if("overdueCers_01".equalsIgnoreCase(subModuleId)) {
                bowSql.append(" and ").append(Sql_switcher.dateToChar("a."+certBorrowSubset+"11","yyyy-mm-dd")).append("<'").append(nowdate).append("'");
            }else if("overdueCers_02".equalsIgnoreCase(subModuleId)) {
                bowSql.append(" and a."+certBorrowSubset+"01 like '"+whereCode+"%' ");
            }else if("overdueCers_03".equalsIgnoreCase(subModuleId)) {
                bowSql.append(" and ").append(whereCode);
            }
            // 证书集信息
            cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
            cerSql.append(" from ").append(dbname).append(certSubset);
            // 证书权限条件语句
            cerSql.append(" where ").append(sqlWhere);

            if(i < dbnames.size()-1) {
                bowSql.append(" union all ");
                cerSql.append(" union all ");
            }
        }

        StringBuffer sql = new StringBuffer("");
        if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {
            sql.append("select * from (");
            sql.append("select * from (").append(bowSql.toString()).append(") z");
            sql.append(" left join (").append(cerSql.toString()).append(") b");
            sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
            sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
            if (Constant.MSSQL == Sql_switcher.dbflag) {
                sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
            }
            sql.append(") m where 1=1");
        }

        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo = new ColumnsInfo();
        // 证书名称
        FieldItem item = DataDictionary.getFieldItem(certBorrowSubset+"05", certBorrowSubset);
        columnsInfo = getColumnsInfoByFi(item, 200);
        columnsList.add(columnsInfo);
        // 证书所有人
        item = DataDictionary.getFieldItem(certBorrowSubset+"07", certBorrowSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 借阅时间
        item = DataDictionary.getFieldItem(certBorrowSubset+"09", certBorrowSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 预计归还日期
        item = DataDictionary.getFieldItem(certBorrowSubset+"11", certBorrowSubset);
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        // 借阅人
        item = DataDictionary.getFieldItem("A0101", "A01");
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsInfo.setColumnDesc("借阅人");
        columnsList.add(columnsInfo);
//		// 证书到期指标
//		item = DataDictionary.getFieldItem(certEndDateItemId, certSubset);
//		columnsInfo = getColumnsInfoByFi(item, 100);
//		columnsList.add(columnsInfo);
//		// 证书状态
//		item = DataDictionary.getFieldItem(certStatus, certSubset);
//		columnsInfo = getColumnsInfoByFi(item, 70);
//		columnsList.add(columnsInfo);
        // 逾期提醒
        if("overdueCers_01".equalsIgnoreCase(subModuleId)) {

            columnsInfo = getColumnsInfo("remind_id", "提醒", 80, "0", "A", 80, 0, "");
            columnsInfo.setRendererFunc("");
            columnsInfo.setTextAlign("center");
            columnsInfo.setRendererFunc("managerDashboard.remindFunc");
            columnsList.add(columnsInfo);

            columnsInfo = new ColumnsInfo();
            columnsInfo.setColumnId("nbase");
            columnsInfo.setColumnDesc("人员库加密");
            columnsInfo.setEncrypted(true);
            columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnsList.add(columnsInfo);

            columnsInfo = new ColumnsInfo();
            columnsInfo.setColumnId("a0100");
            columnsInfo.setColumnDesc("人员编号加密");
            columnsInfo.setEncrypted(true);
            columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnsList.add(columnsInfo);
        }

        ArrayList list = new ArrayList();
        list.add(sql.toString());
        list.add(columnsList);

        return list;
    }

    /**
     * 获取逾期未还数
     * @return		数据集合list
     * @throws GeneralException
     */
    public ArrayList getOverdueNum() throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            // （预计归还日期 AXX11） （AXX19审批状态03：已批，） （AXX23	归还标识45号是否代码类1：已归还；2：未归还）
            String nowdate = DateUtils.format(new Date(), "yyyy-MM-dd");
            // 借阅集查询
            StringBuffer bowSql = new StringBuffer("");
            // 证书集查询
            StringBuffer cerSql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {
                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                bowSql.append("select ").append(certBorrowSubset).append("01 browCertCategory,").append(certBorrowSubset).append("03 browCertNum,")
                        .append(certBorrowSubset + "05,"+certBorrowSubset+"07,"+certBorrowSubset+"09,"+certBorrowSubset + "11,"+certBorrowSubset + "17");
                bowSql.append(" from ").append(dbname).append(certBorrowSubset);
                bowSql.append(" where ").append(certBorrowSubset+"19='03' ");
                bowSql.append(" and ").append(certBorrowSubset+"23='2' ");
                bowSql.append(" and ").append(Sql_switcher.dateToChar(""+certBorrowSubset+"11", "yyyy-mm-dd")).append("<'").append(nowdate).append("'");

                cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                cerSql.append(" from ").append(dbname).append(certSubset);
                // 证书权限条件语句
                cerSql.append(" where ").append(sqlWhere);

                if(i < dbnames.size()-1) {
                    bowSql.append(" union all ");
                    cerSql.append(" union all ");
                }
            }

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");

            if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                sql.append("select * from (").append(bowSql.toString()).append(") z");
                sql.append(" left join (").append(cerSql.toString()).append(") b");
                sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                if (Constant.MSSQL == Sql_switcher.dbflag) {
                    sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                }
            }
            rs = dao.search(sql.toString());
            while(rs.next()) {
                // 证书名称
                String AXX05 = rs.getString(certBorrowSubset + "05");
                // 证书所有人
                String AXX07 = rs.getString(certBorrowSubset + "07");
                // 借用日期
                String AXX09 = (null==rs.getTimestamp(certBorrowSubset + "09"))
                        ? "" : DateUtils.format(rs.getTimestamp(certBorrowSubset + "09"), "yyyy-MM-dd");
                // 预计归还日期
                String AXX11 = (null==rs.getTimestamp(certBorrowSubset + "11"))
                        ? "" : DateUtils.format(rs.getTimestamp(certBorrowSubset + "11"), "yyyy-MM-dd");
                // 归还人/借阅人姓名
                String AXX17 = rs.getString(certBorrowSubset + "17");
                // 审批状态
//    				String AXX19 = rs.getString(certBorrowSubset + "19");

                map = new HashMap();
                map.put("certName", AXX05);
                map.put("certPer", AXX07);
                map.put("bowDate", AXX09);
                map.put("returnDate", AXX11);
                map.put("perName", AXX17);
//    				map.put("AXX19", AXX19);
                dataList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }
    /**
     * 获取待办数据
     * @return		数据集合list
     * @throws GeneralException
     */
    public ArrayList getDealtNum() throws GeneralException{

        ArrayList infoList = new ArrayList();
        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        try {
            ArrayList dataGroupList = new ArrayList();
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            // （预计归还日期 AXX11） （AXX19审批状态03：已批，） （AXX23	归还标识45号是否代码类1：已归还；2：未归还）

            /**
             * 查询待办记录，
             * 先查询所有库的符合审批状态条件的借阅集记录，
             * 同时查询所有库符合权限条件的证书集记录，
             * 然后再关联查
             select * from (
             select 'Usr' nbase,c.A0100,c.A0101,a.A0E01,a.A0E03 from UsrA0E  a
             left join UsrA01 c on a.A0100=c.A0100
             where A0E19='02'
             union all
             select 'Oth' nbase,c.A0100,c.A0101,a.A0E01,a.A0E03  from OthA0E a
             left join UsrA01 c on a.A0100=c.A0100
             where A0E19='02'
             ) z
             left join
             (
             select a0d01,a0d02 from UsrA0D
             where ( 1=2  OR (a0d06 LIKE '303%' and a0d06<>'303') OR a0d06='303')
             union all
             select a0d01,a0d02 from OthA0D
             where ( 1=2  OR (a0d06 LIKE '303%' and a0d06<>'303') OR a0d06='303')
             ) b
             on z.A0E01=b.a0d01 and z.A0E03=b.a0d02
             where b.a0d02 <>'' and b.a0d01<>''
             **/
            // 借阅集查询
            StringBuffer bowSql = new StringBuffer("");
            // 证书集查询
            StringBuffer cerSql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {
                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                bowSql.append("select '").append(dbname).append("' nbase,c.A0100,c.A0101,a.CreateTime,a.")
                        .append(certBorrowSubset).append("01 browCertCategory,a.").append(certBorrowSubset).append("03 browCertNum,a.")
                        .append(certBorrowSubset).append("05,a.").append(certBorrowSubset).append("07,a.").append(certBorrowSubset).append("09,a.")
                        .append(certBorrowSubset).append("11,a.").append(certBorrowSubset).append("13");
                bowSql.append(" from ").append(dbname).append(certBorrowSubset).append(" a");
                // 与主集联查姓名
                bowSql.append(" left join ").append(dbname).append("A01 c on a.A0100=c.A0100 ");
                // 已报批状态
                bowSql.append(" where a.").append(certBorrowSubset+"19='02'");

                cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                cerSql.append(" from ").append(dbname).append(certSubset);
                // 证书权限条件语句
                cerSql.append(" where ").append(sqlWhere);

                if(i < dbnames.size()-1) {
                    bowSql.append(" union all ");
                    cerSql.append(" union all ");
                }
            }

            StringBuffer sql = new StringBuffer("");
            if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                sql.append("select * from (").append(bowSql.toString()).append(") z");
                sql.append(" left join (").append(cerSql.toString()).append(") b");
                sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                if (Constant.MSSQL == Sql_switcher.dbflag) {
                    sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                }
                sql.append(" order by nbase,A0100,A0101 ");
            }

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            while(rs.next()) {
                // 创建日期
                String createTime = (null==rs.getTimestamp("CreateTime"))
                        ? "" : DateUtils.format(rs.getTimestamp("CreateTime"), "yyyy-MM-dd");
                // 借用日期
                String AXX09 = (null==rs.getTimestamp(certBorrowSubset + "09"))
                        ? "" : DateUtils.format(rs.getTimestamp(certBorrowSubset + "09"), "yyyy-MM-dd");
                // 预计归还日期
                String AXX11 = (null==rs.getTimestamp(certBorrowSubset + "11"))
                        ? "" : DateUtils.format(rs.getTimestamp(certBorrowSubset + "11"), "yyyy-MM-dd");

                map = new HashMap();
                map.put("nbase", rs.getString("nbase"));
                map.put("A0100", rs.getString("A0100"));
                map.put("A0101", rs.getString("A0101"));
                map.put("cerType", AdminCode.getCodeName(certCategoryCode, rs.getString("browCertCategory")));
                map.put("cerNum", rs.getString("browCertNum"));
                map.put("cerName", rs.getString(certBorrowSubset + "05"));
                map.put("cerPerName", rs.getString(certBorrowSubset + "07"));
                map.put("borrowDate", AXX09);
                map.put("returnDate", AXX11);
                map.put("borrowCause", rs.getString(certBorrowSubset + "13"));
                map.put("createDate", StringUtils.isBlank(createTime) ? AXX09 : createTime);

                dataList.add(map);
            }
            StringBuffer jsonInfo = new StringBuffer("");
            // 诸葛借阅一级建造师1本，造价工程师1本，安全工程师等5证件
            StringBuffer infos = new StringBuffer("");
            HashMap map2 = new HashMap();
            int num = 1;
            for(int i=0;i<dataList.size();i++) {

                map = (HashMap)dataList.get(i);
                String nbase1 = (String) map.get("nbase");
                String A01001 = (String) map.get("A0100");
                String borrowDate1 = (String) map.get("borrowDate");
                String returnDate1 = (String) map.get("returnDate");
                String borrowCause1 = (String) map.get("borrowCause");
                num = 1;
//				infos.setLength(0);
//				infos.append((String) map.get("A0101") + "借阅" + (String) map.get("cerName"));

                for (int j=dataList.size()-1;j>i;j--){
                    map2 = (HashMap)dataList.get(j);
                    String nbase2 = (String) map2.get("nbase");
                    String A01002 = (String) map2.get("A0100");
                    String borrowDate2 = (String) map2.get("borrowDate");
                    String returnDate2 = (String) map2.get("returnDate");
                    String borrowCause2 = (String) map2.get("borrowCause");

                    if(nbase1.equalsIgnoreCase(nbase2) && A01001.equalsIgnoreCase(A01002)
                            && borrowDate1.equalsIgnoreCase(borrowDate2) && returnDate1.equalsIgnoreCase(returnDate2)
                            && borrowCause1.equalsIgnoreCase(borrowCause2) ) {

                        num++;
//						if(num < 4)
//							infos.append("、" + (String) map2.get("cerName"));
                        dataList.remove(j);
                    }
                }
//				if(i > 3)
//					infos.append("等");
                infos.setLength(0);
                infos.append((String) map.get("A0101") + "借阅" + num + "本证件");
                jsonInfo.setLength(0);
                jsonInfo.append("{'info':'").append(infos.toString()).append("'");
                jsonInfo.append(",'nbase':'").append(PubFunc.encryption(nbase1)).append("'");
                jsonInfo.append(",'A0100':'").append(PubFunc.encryption(A01001)).append("'");
                jsonInfo.append(",'A0101':'").append((String) map.get("A0101")).append("'");
                jsonInfo.append(",'borrowDate':'").append(borrowDate1).append("'");
                jsonInfo.append(",'returnDate':'").append(returnDate1).append("'");
                jsonInfo.append(",'createDate':'").append((String) map.get("createDate")).append("'");
                jsonInfo.append(",'borrowCause':'").append(SafeCode.encode(borrowCause1)).append("'}");
                infoList.add(jsonInfo.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return infoList;
    }
    /**
     * 获取证书分布情况数据
     * @throws GeneralException
     */
    public ArrayList getCurveSituation(String type) throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书状态
            String certStatus = certificateConfigBo.getCertStatus();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");
//    		String nowdate = DateUtils.format(new Date(), "yyyy-MM-dd");
            ArrayList valueList = new ArrayList();
            valueList.add(certCategoryCode);

            sql.append(" select codeitemid,codeitemdesc from codeitem  where codesetid=? and codeitemid=parentid order by a0000");
            rs = dao.search(sql.toString(), valueList);
            while(rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                String codeitemdesc = rs.getString("codeitemdesc");
                int count = 0;
                sql.setLength(0);
                for(int i=0;i<dbnames.size();i++) {
                    String dbname = (String)dbnames.get(i);
                    sql.append("select "+certCategoryItemId+" from ").append(dbname).append(certSubset);
                    sql.append(" where "+certCategoryItemId+" like '"+codeitemid+"%' ");
                    // 证书状态为可用01  或 为空
                    sql.append(" and (").append(certStatus).append("='01' "
                            + "or ").append(certStatus).append(" IS NULL or ").append(certStatus).append("='') ");
                    // 增加证书权限条件
                    sql.append(" and ").append(sqlWhere);

                    if(i < dbnames.size()-1)
                        sql.append(" union all ");
                }
                rowSet = dao.search(sql.toString());
                while(rowSet.next()) {
                    count++;
                }

                map = new HashMap();
                map.put("codeitemid", codeitemid);
                map.put("codeitemdesc", codeitemdesc);
                map.put("count", count);
                dataList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }
    /**
     * 获取证书分布情况数据
     * @throws GeneralException
     */
    public ArrayList clickChildItem(String childItem) throws GeneralException{

        ArrayList list = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            ArrayList dataList = new ArrayList();
            // =1有子分类 =2没有子分类直接显示childItem该分类下的证书
            String flag = "1";
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书状态
            String certStatus = certificateConfigBo.getCertStatus();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");
//    		String nowdate = DateUtils.format(new Date(), "yyyy-MM-dd");
            ArrayList valueList = new ArrayList();
            valueList.add(certCategoryCode);
            valueList.add(childItem);

            sql.append(" select codeitemid,codeitemdesc from codeitem where codesetid=? and parentid=? and codeitemid<>parentid ");
            rs = dao.search(sql.toString(), valueList);
            while(rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                String codeitemdesc = rs.getString("codeitemdesc");
                int count = 0;
                sql.setLength(0);
                for(int i=0;i<dbnames.size();i++) {
                    String dbname = (String)dbnames.get(i);
                    sql.append("select "+certCategoryItemId+" from ").append(dbname).append(certSubset);
                    sql.append(" where "+certCategoryItemId+" like '"+codeitemid+"%' ");
                    // 证书状态为可用01  或 为空
                    sql.append(" and (").append(certStatus).append("='01' "
                            + "or ").append(certStatus).append(" IS NULL or ").append(certStatus).append("='') ");
                    // 增加证书权限条件
                    sql.append(" and ").append(sqlWhere);
                    if(i < dbnames.size()-1)
                        sql.append(" union all ");
                }
                rowSet = dao.search(sql.toString());
                while(rowSet.next()) {
                    count++;
                }

                map = new HashMap();
                map.put("codeitemid", codeitemid);
                map.put("codeitemdesc", codeitemdesc);
                map.put("count", count);
                dataList.add(map);

                list.add(flag);
                list.add(dataList);
            }
            // 如果 查子分类下的分类为空 则 显示childItem该分类下的证书
            if(dataList.size() == 0) {
                flag = "2";
                String tableConfig = getTableConfigCers("allCers_02", childItem);

                list.add(flag);
                list.add(tableConfig);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    private void setColumnJson(StringBuffer columnJson, FieldItem fi) {
        columnJson.append("{text: '" + fi.getItemdesc() + "',");
        columnJson.append("dataIndex: '" + fi.getItemid().toLowerCase() + "',");
        if("operation".equals(fi.getItemid()))
            columnJson.append("renderer:certificateManage.rendererBorrowFun,");
        else if(!"0".equalsIgnoreCase(fi.getCodesetid()))
            columnJson.append("renderer:certificateManage.showCodeitemDesc,");

        if("operation".equals(fi.getItemid()))
            columnJson.append("align:'center',");
        else if("N".equalsIgnoreCase(fi.getItemtype()))
            columnJson.append("align:'right',");
        else
            columnJson.append("align:'left',");

        columnJson.append("sortable: false,width:100,menuDisabled:true},");
    }
    /**
     * 获取单条待办的借阅证书集合
     * @param nbase
     * @param A0100
     * @param borrowDate
     * @param returnDate
     * @param borrowCause
     * @return
     * @throws GeneralException
     */
    public ArrayList borrowWinData(String nbase, String A0100, String borrowDate
            , String returnDate, String borrowCause) throws GeneralException{

        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书类别代码类
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");


            StringBuffer bowSql = new StringBuffer("");
            bowSql.append("select "+certBorrowSubset+"01 browCertCategory,"+certBorrowSubset+"03 browCertNum,"
                    +certBorrowSubset+"05 cerName,"+certBorrowSubset+"07 cerPerName");
            // 查询借阅子集条件
            StringBuffer bowWhereSql = new StringBuffer("");
            bowWhereSql.append(" from ").append(PubFunc.decrypt(nbase)+certBorrowSubset);
            bowWhereSql.append(" where A0100=? and ").append(Sql_switcher.dateToChar(certBorrowSubset+"09", "yyyy-mm-dd")+"=? ");
            bowWhereSql.append(" and ").append(Sql_switcher.dateToChar(certBorrowSubset+"11", "yyyy-mm-dd")+"=? "
                    + "and ").append("cast("+certBorrowSubset+"13 as varchar(1000)) = ?"  + " ");//Sql_switcher.concat()
            bowWhereSql.append(" and ").append(certBorrowSubset+"19='02' ");

            bowSql.append(bowWhereSql.toString());
            // 关联证书集 用户权限范围
            StringBuffer cerSql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {
                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;
                cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                cerSql.append(" from ").append(dbname).append(certSubset);
                // 证书权限条件语句
                cerSql.append(" where ").append(sqlWhere);

                if(i < dbnames.size()-1)
                    cerSql.append(" union all ");
            }

            StringBuffer sql = new StringBuffer("");
            if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                sql.append("select * from (").append(bowSql.toString()).append(") z");
                sql.append(" left join (").append(cerSql.toString()).append(") b");
                sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                if (Constant.MSSQL == Sql_switcher.dbflag) {
                    sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                }
            }
            ArrayList valueList = new ArrayList();
            valueList.add(PubFunc.decrypt(A0100));
            valueList.add(borrowDate);
            valueList.add(returnDate);
            valueList.add(PubFunc.keyWord_reback(SafeCode.decode(borrowCause)));

            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString(), valueList);
            ArrayList dataList = new ArrayList();
            HashMap map = new HashMap();
            while(rs.next()) {
                map = new HashMap();
                map.put("cerType", AdminCode.getCodeName(certCategoryCode, rs.getString("browCertCategory")));
                map.put("cerNum", rs.getString("browCertNum"));
                map.put("cerName", rs.getString("cerName"));
                map.put("cerPerName", rs.getString("cerPerName"));

                dataList.add(map);
            }
            list.add(dataList);
            // 处理借阅子集其他指标项
            ArrayList fieldlist = this.getfieldSetList();
            String fieldSql = "select * " + bowWhereSql.toString();
            rs = dao.search(fieldSql, valueList);
            if(rs.next()) {
                for(int i=0;i<fieldlist.size();i++) {
                    LazyDynaBean bean = (LazyDynaBean)fieldlist.get(i);
                    String itemid = (String)bean.get("itemid");
                    String itemtype = (String)bean.get("itemtype");
                    String codesetid = (String)bean.get("codesetid");
                    if("N".equalsIgnoreCase(itemtype))
                        bean.set("value", String.valueOf(rs.getDouble(itemid)));
                    else if("D".equalsIgnoreCase(itemtype)) {
                        int len = (Integer)bean.get("itemlength");
                        String format = "";
                        if(4 == len){
                            format = "yyyy";
                        }else if(7 == len){
                            format = "yyyy-MM";
                        }else if(10 == len){
                            format = "yyyy-MM-dd";
                        }else if(16 == len){
                            format = "yyyy-MM-dd HH:mm";
                        }else if(18 == len){
                            format = "yyyy-MM-dd HH:mm:ss";
                        }
                        Date date = rs.getTimestamp(itemid);
                        bean.set("value", null==date?"":DateUtils.format(date, format));
                    }else if("A".equalsIgnoreCase(itemtype) && !"0".equals(codesetid)) {
                        String codevalue = rs.getString(itemid);
                        bean.set("value", codevalue + "`" +AdminCode.getCodeName(codesetid, codevalue));
                    }else
                        bean.set("value", rs.getString(itemid));
                }
            }
            list.add(fieldlist);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }
    /**
     * 获取该用户的单位/部门/岗位
     * @param nbase
     * @param A0100
     * @return	单位/部门/岗位
     * @throws GeneralException
     */
    public String getUserInfo(String nbase, String A0100) throws GeneralException{
        String info = "";
        RowSet rs = null;
        try {
            if(StringUtils.isBlank(nbase) || StringUtils.isBlank(A0100))
                return info;

            StringBuffer sql = new StringBuffer("");
            sql.append("select B0110,E0122,E01A1 from ").append(PubFunc.decrypt(nbase)+"A01");
            sql.append(" where A0100=? ");
            ArrayList valueList = new ArrayList();
            valueList.add(PubFunc.decrypt(A0100));

            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString(), valueList);
            if(rs.next()) {
                String B0110 = rs.getString("B0110");
                if(StringUtils.isNotEmpty(B0110))
                    info = AdminCode.getCodeName("UN", B0110);
                String E0122 = rs.getString("E0122");
                if(StringUtils.isNotEmpty(E0122))
                    info = info + "/" + AdminCode.getCodeName("UM", E0122);
                String E01A1 = rs.getString("E01A1");
                if(StringUtils.isNotEmpty(E01A1))
                    info = info + "/" + AdminCode.getCodeName("@K", E01A1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return info;
    }
    /**
     * 更新借阅待办审批状态
     * @param map
     * @return
     * @throws GeneralException
     */
    public String borwApproveData(HashMap map) throws GeneralException{

        String flag = "1";
        RowSet rs = null;
        try {

            String bowNbase = PubFunc.decrypt((String)map.get("nbase"));
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            StringBuffer sql = new StringBuffer("");
            sql.append("update ").append(bowNbase+certBorrowSubset)
                    .append(" set ModTime=?,ModUserName=?,").append(certBorrowSubset+"19=?, ").append(certBorrowSubset+"21=?, ").append(certBorrowSubset+"23=? ");
            sql.append(" where A0100=? and ").append(Sql_switcher.dateToChar(certBorrowSubset+"09", "yyyy-mm-dd")+"=? ");
            sql.append(" and ").append(Sql_switcher.dateToChar(certBorrowSubset+"11", "yyyy-mm-dd")+"=? "
                    + "and ").append("cast("+certBorrowSubset+"13 as varchar(1000)) = ?"  + " ");

            ArrayList valueList = new ArrayList();
            valueList.add(new Timestamp(new Date().getTime()));
            valueList.add(this.userView.getUserFullName());

            valueList.add((String)map.get("approveFlag"));
            valueList.add((String)map.get("appOpinValue"));
            // 归还标识 =2 为归还
            valueList.add("03".equals((String)map.get("approveFlag")) ? "2" : "1");
            valueList.add(PubFunc.decrypt((String)map.get("A0100")));
            valueList.add((String)map.get("borrowDate"));
            valueList.add((String)map.get("returnDate"));
            valueList.add(PubFunc.keyWord_reback(SafeCode.decode((String)map.get("borrowCause"))));

            ContentDAO dao = new ContentDAO(conn);
            dao.update(sql.toString(), valueList);

            /**
             * 更改证书借出标识
             */
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书是否借出
            String certBorrowState = certificateConfigBo.getCertBorrowState();

            // 获取需要更改借出状态的证书 类别 与 编号
            sql.setLength(0);
            sql.append("select ").append(certBorrowSubset).append("01 certCategory,").append(certBorrowSubset).append("03 certNum "
                    + "from ").append(bowNbase+certBorrowSubset);
            sql.append(" where A0100=? and ").append(Sql_switcher.dateToChar(certBorrowSubset+"09", "yyyy-mm-dd")+"=? ");
            sql.append(" and ").append(Sql_switcher.dateToChar(certBorrowSubset+"11", "yyyy-mm-dd")+"=? "
                    + "and ").append("cast("+certBorrowSubset+"13 as varchar(1000)) = ?"  + " ");
            valueList = new ArrayList();
            valueList.add(PubFunc.decrypt((String)map.get("A0100")));
            valueList.add((String)map.get("borrowDate"));
            valueList.add((String)map.get("returnDate"));
            valueList.add(PubFunc.keyWord_reback(SafeCode.decode((String)map.get("borrowCause"))));

            ArrayList list = new ArrayList();
            ArrayList onelist = new ArrayList();
            rs = dao.search(sql.toString(), valueList);
            while(rs.next()) {

                String certCategory = rs.getString("certCategory");
                String certNum = rs.getString("certNum");

                if(StringUtils.isNotBlank(certCategory) && StringUtils.isNotBlank(certNum)) {
                    onelist = new ArrayList();
                    onelist.add(certCategory);
                    onelist.add(certNum);
                    list.add(onelist);
                }
            }
            // 同意是已借出  驳回是未借出
            String certBorrowStateValue = "03".equals((String)map.get("approveFlag")) ? "1" : "2";

            for(int i=0;i<list.size();i++) {
                onelist = (ArrayList)list.get(i);

                for(int j=0;j<dbnames.size();j++) {

                    String dbname = (String)dbnames.get(j);
                    if(StringUtils.isBlank(dbname))
                        continue;
                    sql.setLength(0);
                    sql.append("update ").append(dbname+certSubset).append(" set ").append(certBorrowState+"='").append(certBorrowStateValue).append("'");
                    sql.append(" where ").append(certCategoryItemId).append("=? and ").append(certNOItemId).append("=? ");

                    int num = dao.update(sql.toString(), onelist);
                    if(num == 1)
                        break;
                }
            }

            // 发送通知信息
            this.sendMesges(map);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }
    /**
     * 审批借阅单据 发送通知
     * @param map
     * @throws GeneralException
     */
    public void sendMesges(HashMap map) throws GeneralException{

        try {
            String nbase = PubFunc.decrypt((String)map.get("nbase"));
            String A0100 = PubFunc.decrypt((String)map.get("A0100"));
            String approveFlag = (String)map.get("approveFlag");
            String appOpinValue = (String)map.get("appOpinValue");

            String content = "您好，"+this.userView.getUserFullName()+("03".equalsIgnoreCase(approveFlag)?"同意":"退回")
                    +"您提交的证书借阅单据，审批意见是："+appOpinValue+"。";

            CertificateUtilsBo certificateUtilsBo = new CertificateUtilsBo(this.conn, this.userView);
            certificateUtilsBo.sendMode(nbase, A0100, "证书借阅审批", content);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 提醒逾期未还数据记录操作
     * @param map
     * @throws GeneralException
     */
    public String sendRemindCer(HashMap map) throws GeneralException{

        String msg = "";
        try {
            String nbase = PubFunc.decrypt((String)map.get("nbase"));
            String A0100 = PubFunc.decrypt((String)map.get("A0100"));
            String A0101 = (String)map.get("A0101");
            String cerName = (String)map.get("cerName");
            String cerPerName = (String)map.get("cerPerName");
            String borrowDate = (String)map.get("borrowDate");
            String returnDate = (String)map.get("returnDate");

            int num = DateUtils.dayDiff(DateUtils.getDate(returnDate, "yyyy-MM-dd"), new Date());
            String content = A0101+"  您好，您借阅的证书（"+cerName+"）应于"+returnDate+"归还，当前已逾期"+num+"天，请及时归还。";

            CertificateUtilsBo certificateUtilsBo = new CertificateUtilsBo(this.conn, this.userView);
            certificateUtilsBo.sendMode(nbase, A0100, "证书逾期提醒", content);

        } catch (Exception e) {
            e.printStackTrace();
            msg = e.toString();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg;
    }

    /**
     * 获取该人员范围内所有证书
     * @param nbase
     * @param A0100
     * @return
     * @throws GeneralException
     */
    public ArrayList showAllCersData(String nbase, String A0100) throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书类别代码类
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书名称
            String certName = certificateConfigBo.getCertName();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书到期指标
            String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
            // 证书状态
            String certStatus = certificateConfigBo.getCertStatus();
            // 证书所属组织
            String certOrganization = certificateConfigBo.getCertOrganization();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "a");

            StringBuffer certTotalsql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {

                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                certTotalsql.append("select '"+dbname+"'nbase,b.A0101,a.* ");
                certTotalsql.append(" from ").append(dbname).append(certSubset);
                certTotalsql.append(" a left join ").append(dbname).append("A01 b on a.A0100=b.A0100 ");
                // 增加权限条件
                certTotalsql.append(" where ").append(sqlWhere);

                if(i < dbnames.size()-1)
                    certTotalsql.append(" union all ");
            }
            StringBuffer sql = new StringBuffer("");
            if(StringUtils.isNotEmpty(certTotalsql.toString())) {
                sql.append("select * from (").append(certTotalsql.toString()).append(") m where 1=1");
                sql.append(" order by nbase, A0100");
            }
            ContentDAO dao = new ContentDAO(conn);
            HashMap map = new HashMap();
            rs = dao.search(sql.toString());
            while(rs.next()) {
                map = new HashMap();
                map.put("certPer", rs.getString("A0101"));
                map.put("cerType", AdminCode.getCodeName(certCategoryCode, rs.getString(certCategoryItemId)));
                map.put("cerNum", rs.getString(certNOItemId));
                map.put("cerName", rs.getString(certName));
                map.put("certStat", AdminCode.getCodeName("83", rs.getString(certStatus)));
                String certEndDate = (null==rs.getTimestamp(certEndDateItemId)) ? "" : DateUtils.format(rs.getTimestamp(certEndDateItemId), "yyyy-MM-dd");
                map.put("certEndDate", certEndDate);
                map.put("certOrg", AdminCode.getCodeName("UN", rs.getString(certOrganization)));
                dataList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }
    /**
     * 证书到期情况统计
     * @return
     * @throws GeneralException
     */
    public ArrayList exprieBution() throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书到期指标
            String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");
            // 证书状态条件
            String statusWhere = "1=1";
            if (StringUtils.isNotBlank(certificateConfigBo.getCertStatus())) {
                statusWhere = "(" + Sql_switcher.isnull(certificateConfigBo.getCertStatus(), "'01'") + "='01'"
                        + " or " + certificateConfigBo.getCertStatus() + "='')";
            }
            // 到期日期范围条件
            Date nowDate = new Date();
            String warnDate = DateUtils.FormatDate(DateUtils.addDays(nowDate, 180), "yyyy-MM-dd");
            String warnDateWhere = certEndDateItemId + "<=" + Sql_switcher.dateValue(warnDate);

            // 已逾期、3天内到期、7天内到期、30天内到期、60天内到期、180天内到期
            int overCount = 0;
            int threeCount = 0;
            int sevenCount = 0;
            int thirtyCount = 0;
            int sixtyCount = 0;
            int oneEightyCount = 0;

            StringBuffer certTotalsql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {
                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                certTotalsql.append("select "+certEndDateItemId);
                certTotalsql.append(" from ").append(dbname).append(certSubset);
                // 增加权限条件
                certTotalsql.append(" where ").append(sqlWhere);
                certTotalsql.append(" and ").append(statusWhere);
                certTotalsql.append(" and ").append(warnDateWhere);

                if(i < dbnames.size()-1)
                    certTotalsql.append(" union all ");
            }

            ContentDAO dao = new ContentDAO(conn);
            HashMap map = new HashMap();
            rs = dao.search(certTotalsql.toString());
            while(rs.next()) {
                if(null == rs.getTimestamp(certEndDateItemId))
                    continue;

                Date certEndDate = rs.getTimestamp(certEndDateItemId);
                int num = DateUtils.dayDiff(nowDate ,certEndDate);
                // 已逾期
                if(num < 0)
                    overCount++;
                else if(num < 3)
                    threeCount++;
                else if(num < 7)
                    sevenCount++;
                else if(num < 30)
                    thirtyCount++;
                else if(num < 60)
                    sixtyCount++;
                else if(num < 180)
                    oneEightyCount++;
            }
            // 逾期
            map = new HashMap();
            map.put("codeitemid", "-1");
            map.put("codeitemdesc", "已逾期");
            map.put("count", overCount);
            dataList.add(map);
            // 3天
            map = new HashMap();
            map.put("codeitemid", "3");
            map.put("codeitemdesc", "3天内到期");
            map.put("count", threeCount);
            dataList.add(map);
            // 7天
            map = new HashMap();
            map.put("codeitemid", "7");
            map.put("codeitemdesc", "7天内到期");
            map.put("count", sevenCount);
            dataList.add(map);
            // 30天
            map = new HashMap();
            map.put("codeitemid", "30");
            map.put("codeitemdesc", "30天内到期");
            map.put("count", thirtyCount);
            dataList.add(map);
            // 60天
            map = new HashMap();
            map.put("codeitemid", "60");
            map.put("codeitemdesc", "60天内到期");
            map.put("count", sixtyCount);
            dataList.add(map);
            // 180天
            map = new HashMap();
            map.put("codeitemid", "180");
            map.put("codeitemdesc", "180天内到期");
            map.put("count", oneEightyCount);
            dataList.add(map);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dataList;
    }
    /**
     * 证书到期情况统计穿透进去
     * @param childFlag
     * @return
     * @throws GeneralException
     */
    public String clickChildExprieBution(String childFlag) throws GeneralException{

//    	ArrayList dataList = new ArrayList();
        String tableConfig = "";
        try {
            String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
            if(StringUtils.isBlank(certEndDateItemId))
                return null;
            // 已逾期、3天内到期、7天内到期、30天内到期、60天内到期、180天内到期
            Date nowDate = new Date();
            // 当前日期超过 预计归还日期
//			sql.append(" where ").append(Sql_switcher.dateToChar("a."+certBorrowSubset+"11", "yyyy-mm-dd")).append("<'").append(nowdate).append("'");
            String dateSql = "";
            String nowParam = DateUtils.format(nowDate, "yyyy-MM-dd");
            String threeParam = DateUtils.format(DateUtils.addDays(nowDate, +3), "yyyy-MM-dd");
            String sevenParam = DateUtils.format(DateUtils.addDays(nowDate, +7), "yyyy-MM-dd");
            String thirtyParam = DateUtils.format(DateUtils.addDays(nowDate, +30), "yyyy-MM-dd");
            String sixtyParam = DateUtils.format(DateUtils.addDays(nowDate, +60), "yyyy-MM-dd");
            String oneEightyParam = DateUtils.format(DateUtils.addDays(nowDate, +180), "yyyy-MM-dd");
            if("-1".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<'" + nowParam + "' ";
            }else if("3".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + ">='" + nowParam + "' "
                        + "and " + Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<='" + threeParam + "' ";
            }else if("7".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + ">'" + threeParam + "' "
                        + "and " + Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<='" + sevenParam + "' ";
            }else if("30".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + ">'" + sevenParam + "'"
                        + "and " + Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<='" + thirtyParam + "' ";
            }else if("60".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + ">'" + thirtyParam + "'"
                        + "and " + Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<='" + sixtyParam + "' ";
            }else if("180".equals(childFlag)) {
                dateSql = Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + ">'" + sixtyParam + "'"
                        + "and " + Sql_switcher.dateToChar("a." + certEndDateItemId+"", "yyyy-mm-dd") + "<='" + oneEightyParam + "' ";
            }else
                return null;

            tableConfig = getTableConfigCers("allCers_03", dateSql);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tableConfig.toString();
    }
    /**
     * 借阅证书分布情况
     * @param borflag =1按类别分类；=2按预计归还日期分类
     * @return
     * @throws GeneralException
     */
    public ArrayList borrowBution(String borflag) throws GeneralException{

        ArrayList dataList = new ArrayList();
        try {
            if("1".equals(borflag))
                dataList = this.borrowTypeBution();
                // 按日期
            else if("2".equals(borflag))
                dataList = this.borrowDateBution();

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return dataList;
    }
    /**
     * 借阅证书分布情况(按类别分类)
     * @return
     * @throws GeneralException
     */
    public ArrayList borrowTypeBution() throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            ArrayList valueList = new ArrayList();
            valueList.add(certCategoryCode);

            StringBuffer sql = new StringBuffer("");
            sql.append(" select codeitemid,codeitemdesc from codeitem  where codesetid=? and codeitemid=parentid");

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString(), valueList);
            while(rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                String codeitemdesc = rs.getString("codeitemdesc");
                // 借阅集查询
                StringBuffer bowSql = new StringBuffer("");
                // 证书集查询
                StringBuffer cerSql = new StringBuffer("");
                for(int i=0;i<dbnames.size();i++) {
                    String dbname = (String)dbnames.get(i);
                    if(StringUtils.isBlank(dbname))
                        continue;

                    bowSql.append("select ").append(certBorrowSubset).append("01 browCertCategory,").append(certBorrowSubset).append("03 browCertNum");
                    bowSql.append(" from ").append(dbname).append(certBorrowSubset);
                    bowSql.append(" where "+certBorrowSubset+"01 like '"+codeitemid+"%' ");
                    bowSql.append(" and ").append(certBorrowSubset+"19='03' ");
                    bowSql.append(" and ").append(certBorrowSubset+"23='2' ");

                    cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                    cerSql.append(" from ").append(dbname).append(certSubset);
                    // 证书权限条件语句
                    cerSql.append(" where ").append(sqlWhere);

                    if(i < dbnames.size()-1) {
                        bowSql.append(" union all ");
                        cerSql.append(" union all ");
                    }
                }

                int count = 0;
                sql.setLength(0);
                if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                    sql.append("select * from (").append(bowSql.toString()).append(") z");
                    sql.append(" left join (").append(cerSql.toString()).append(") b");
                    sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                    sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                    if (Constant.MSSQL == Sql_switcher.dbflag) {
                        sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                    }
                }
                rowSet = dao.search(sql.toString());
                while(rowSet.next()) {
                    count++;
                }

                map = new HashMap();
                map.put("codeitemid", codeitemid);
                map.put("codeitemdesc", codeitemdesc);
                map.put("count", count);
                dataList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }
    /**
     * 借阅证书分布情况(按预计归还日期分类)
     * @return
     * @throws GeneralException
     */
    public ArrayList borrowDateBution() throws GeneralException{

        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            // 借阅集查询
            StringBuffer bowSql = new StringBuffer("");
            // 证书集查询
            StringBuffer cerSql = new StringBuffer("");
            for(int i=0;i<dbnames.size();i++) {
                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                bowSql.append("select ").append(certBorrowSubset).append("01 browCertCategory,").append(certBorrowSubset).append("03 browCertNum")
                        .append(",").append(certBorrowSubset).append("11 ");
                bowSql.append(" from ").append(dbname).append(certBorrowSubset);
                bowSql.append(" where ").append(certBorrowSubset+"19='03' ");
                bowSql.append(" and ").append(certBorrowSubset+"23='2' ");

                cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                cerSql.append(" from ").append(dbname).append(certSubset);
                // 证书权限条件语句
                cerSql.append(" where ").append(sqlWhere);

                if(i < dbnames.size()-1) {
                    bowSql.append(" union all ");
                    cerSql.append(" union all ");
                }
            }

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");

            if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                sql.append("select * from (").append(bowSql.toString()).append(") z");
                sql.append(" left join (").append(cerSql.toString()).append(") b");
                sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                if (Constant.MSSQL == Sql_switcher.dbflag) {
                    sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                }
            }

            //分别统计管理范围内已逾期、7天内需归还、30天内需归还、90天内需归还、全部需归还证书的数量
            Date nowDate = new Date();
            int overCount = 0;
            Date sevenDayin =  DateUtils.addDays(nowDate, +7);
            int sevenCount = 0;
            Date thirtyDayin = DateUtils.addDays(nowDate, +30);
            int thirtyCount = 0;
            Date ninetyDayin = DateUtils.addDays(nowDate, +90);
            int ninetyCount = 0;
            int allCount = 0;
            rs = dao.search(sql.toString());
            while(rs.next()) {
                // 获取预计归还日期
                if(null == rs.getTimestamp(certBorrowSubset+"11"))
                    continue;

                Date browDate = rs.getTimestamp(certBorrowSubset+"11");
                int num = DateUtils.dayDiff(nowDate ,browDate);
                // 已逾期
                if(num < 0)
                    overCount++;
                else if((num == 0  || num > 0 ) && num < 7)
                    sevenCount++;
                else if((num == 7  || num > 7 ) && num < 30)
                    thirtyCount++;
                else if((num == 30  || num > 30 ) && num < 90)
                    ninetyCount++;

                allCount++;
            }
            // 逾期
            map = new HashMap();
            map.put("codeitemid", "-1");
            map.put("codeitemdesc", "已逾期归还");
            map.put("count", overCount);
            dataList.add(map);
            // 7天
            map = new HashMap();
            map.put("codeitemid", "7");
            map.put("codeitemdesc", "7天内需归还");
            map.put("count", sevenCount);
            dataList.add(map);
            // 30天
            map = new HashMap();
            map.put("codeitemid", "30");
            map.put("codeitemdesc", "30天内需归还");
            map.put("count", thirtyCount);
            dataList.add(map);
            // 90天
            map = new HashMap();
            map.put("codeitemid", "90");
            map.put("codeitemdesc", "90天内需归还");
            map.put("count", ninetyCount);
            dataList.add(map);
            // 全部
            map = new HashMap();
            map.put("codeitemid", "all");
            map.put("codeitemdesc", "全部需归还");
            map.put("count", allCount);
            dataList.add(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }
    /**
     * 借阅证书分布情况
     * @param childItem		分类代码项/日期范围
     * @param borflag		=1按证书类别统计；=2按预计归还日期统计
     * @return
     * @throws GeneralException
     */
    public ArrayList clickChildBorrowBution(String childItem, String borflag) throws GeneralException{

        ArrayList list = new ArrayList();
        try {
            if("1".equals(borflag))
                list = this.clickChildBorrowType(childItem);
            else if("2".equals(borflag))
                list = this.clickChildBorrowDate(childItem);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**
     * 借阅证书分布情况(按预计归还日期分类穿透详细信息)
     * @return
     * @throws GeneralException
     */
    public ArrayList clickChildBorrowDate(String type) throws GeneralException{

        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            ArrayList dataList = new ArrayList();
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            if(null==dbnames || dbnames.size() < 1)
                return dataList;
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            if(StringUtils.isBlank(certBorrowSubset))
                return null;
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            if(StringUtils.isBlank(certCategoryCode))
                return null;

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");
            //分别统计管理范围内已逾期、7天内需归还、30天内需归还、90天内需归还、全部需归还证书的数量
            Date nowDate = new Date();
            // 当前日期超过 预计归还日期
//			sql.append(" where ").append(Sql_switcher.dateToChar("a."+certBorrowSubset+"11", "yyyy-mm-dd")).append("<'").append(nowdate).append("'");
            String dateSql = "";
            String nowParam = DateUtils.format(nowDate, "yyyy-MM-dd");
            String sevenParam = DateUtils.format(DateUtils.addDays(nowDate, +7), "yyyy-MM-dd");
            String thirtyParam = DateUtils.format(DateUtils.addDays(nowDate, +30), "yyyy-MM-dd");
            String ninetyParam = DateUtils.format(DateUtils.addDays(nowDate, +90), "yyyy-MM-dd");
            if("-1".equals(type)) {
                dateSql = Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + "<'" + nowParam + "' ";
            }else if("7".equals(type)) {
                dateSql = Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + ">='" + nowParam + "' "
                        + "and " + Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + "<='" + sevenParam + "' ";
            }else if("30".equals(type)) {
                dateSql = Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + ">'" + sevenParam + "'"
                        + "and " + Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + "<='" + thirtyParam + "' ";
            }else if("90".equals(type)) {
                dateSql = Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + ">'" + thirtyParam + "'"
                        + "and " + Sql_switcher.dateToChar("a." + certBorrowSubset+"11", "yyyy-mm-dd") + "<='" + ninetyParam + "' ";
            }else if("all".equals(type)) {
                dateSql = " 1=1 ";
            }else
                return null;

            String config = this.getTableConfigOverdueCers("overdueCers_03", dateSql);

            list.add("2");
            list.add(config);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }
    /**
     * 借阅证书类别分布情况
     * @param childItem		分类代码项
     * @return
     * @throws GeneralException
     */
    public ArrayList clickChildBorrowType(String childItem) throws GeneralException{

        ArrayList list = new ArrayList();
        RowSet rs = null;
        RowSet rowSet = null;
        try {
            ArrayList dataList = new ArrayList();
            // =1有子分类 =2没有子分类直接显示childItem该分类下的证书
            String flag = "1";
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            // 证书类别
            String certCategoryCode = certificateConfigBo.getCertCategoryCode();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书权限条件
            String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");

            HashMap map = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("");
//    		String nowdate = DateUtils.format(new Date(), "yyyy-MM-dd");
            ArrayList valueList = new ArrayList();
            valueList.add(certCategoryCode);
            valueList.add(childItem);

            sql.append(" select codeitemid,codeitemdesc from codeitem where codesetid=? and parentid=? and codeitemid<>parentid ");
            rs = dao.search(sql.toString(), valueList);
            while(rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                String codeitemdesc = rs.getString("codeitemdesc");
                // 借阅集查询
                StringBuffer bowSql = new StringBuffer("");
                // 证书集查询
                StringBuffer cerSql = new StringBuffer("");
                for(int i=0;i<dbnames.size();i++) {
                    String dbname = (String)dbnames.get(i);
                    if(StringUtils.isBlank(dbname))
                        continue;

                    bowSql.append("select ").append(certBorrowSubset).append("01 browCertCategory,").append(certBorrowSubset).append("03 browCertNum");
                    bowSql.append(" from ").append(dbname).append(certBorrowSubset);
                    bowSql.append(" where "+certBorrowSubset+"01 like '"+codeitemid+"%' ");
                    bowSql.append(" and ").append(certBorrowSubset+"19='03' ");
                    bowSql.append(" and ").append(certBorrowSubset+"23='2' ");

                    cerSql.append("select ").append(certCategoryItemId).append(" certCategory, ").append(certNOItemId).append(" certNum");
                    cerSql.append(" from ").append(dbname).append(certSubset);
                    // 证书权限条件语句
                    cerSql.append(" where ").append(sqlWhere);

                    if(i < dbnames.size()-1) {
                        bowSql.append(" union all ");
                        cerSql.append(" union all ");
                    }
                }

                int count = 0;
                sql.setLength(0);
                if(StringUtils.isNotEmpty(bowSql.toString()) && StringUtils.isNotEmpty(cerSql.toString())) {

                    sql.append("select * from (").append(bowSql.toString()).append(") z");
                    sql.append(" left join (").append(cerSql.toString()).append(") b");
                    sql.append(" on z.browCertCategory=b.certCategory and z.browCertNum=b.certNum ");
                    sql.append(" where b.certCategory IS NOT NULL and b.certNum IS NOT NULL ");
                    if (Constant.MSSQL == Sql_switcher.dbflag) {
                        sql.append(" and b.certCategory<>'' and b.certNum<>'' ");
                    }
                }

                rowSet = dao.search(sql.toString());
                while(rowSet.next()) {
                    count++;
                }

                map = new HashMap();
                map.put("codeitemid", codeitemid);
                map.put("codeitemdesc", codeitemdesc);
                map.put("count", count);
                dataList.add(map);

                list.add(flag);
                list.add(dataList);
            }
            // 如果 查子分类下的分类为空 则 显示childItem该分类下的证书
            if(dataList.size() == 0) {

                flag = "2";
                String config = this.getTableConfigOverdueCers("overdueCers_02", childItem);

                list.add(flag);
                list.add(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rowSet);
        }
        return list;
    }
    //**自助用户 证书门户**//
    /**
     * 我的证书
     * @param map
     * @return
     * @throws GeneralException
     */
    public ArrayList getCertSubetMsg() throws GeneralException{

        RowSet rs = null;
        RowSet rsBorrow = null;
        ArrayList certSubetMsg = new ArrayList();
        try {
            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书子集
            String certsubet = certificateConfigBo.getCertSubset();
            String certName = certificateConfigBo.getCertName();
            String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
            String certOrganization = certificateConfigBo.getCertOrganization();
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 借阅子集
            String certBorrowSubet = certificateConfigBo.getCertBorrowSubset();

            String a0100 = this.userView.getA0100();
            String nbase = this.userView.getDbname();
            // 证书权限条件
//	    	String sqlWhere = certificatePrivBo.getCertOrgWhere(conn, userView, "");
            String certMsg = "";
            int cerNumber = 0;
            int number = 0;
            String name = "";
            ArrayList paramList = new ArrayList();
            StringBuffer sql = new StringBuffer("");
            String nbasecertsubet = nbase + certsubet;
            String nbasecert  =  nbase + certBorrowSubet;
            // 获取证书总数
            sql.append("select "+certName+", ").append(certEndDateItemId+", ").append(certOrganization+", ").append(certNOItemId+"  from ");
            sql.append(nbasecertsubet);
            sql.append(" WHERE A0100 =  '");
            sql.append(a0100);
            sql.append("' and "+certName+" is not null ");
            sql.append(" and "+certNOItemId+" is not null ");
            if (Constant.MSSQL == Sql_switcher.dbflag) {
                sql.append(" and "+certName+" <> '' ");
                sql.append(" and "+certNOItemId+" <> '' ");
            }
            // 权限条件
//			sql.append(" and ").append(sqlWhere);
            String returnFlag = "";
            String certNameValue = "";
            String certEndDateItemIdValue = "";
            String certOrganizationValue = "";
            String certNOItemIdValue = "";
            String certOrganizationUM = "";
            HashMap certMap = new HashMap();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            while(rs.next()){
                certMap = new HashMap();
                cerNumber++;
                certNameValue = rs.getString(certName);
                certEndDateItemIdValue = (null==rs.getTimestamp(certEndDateItemId))
                        ? "" : DateUtils.format(rs.getTimestamp(certEndDateItemId), "yyyy-MM-dd");
                certOrganizationValue = StringUtils.isBlank(rs.getString(certOrganization))
                        ? "" : rs.getString(certOrganization);

                certOrganizationUM =  AdminCode.getCodeName("UM", certOrganizationValue);
                if(StringUtils.isEmpty(certOrganizationUM))
                    certOrganizationValue =  AdminCode.getCodeName("UN", certOrganizationValue);
                else
                    certOrganizationValue = certOrganizationUM;

                certNOItemIdValue = rs.getString(certNOItemId);
                paramList.clear();
                sql.setLength(0);
                number = 0;
                for(int i=0;i<dbnames.size();i++) {
                    String dbname = (String)dbnames.get(i);
                    if(StringUtils.isBlank(dbname))
                        continue;
                    nbasecert  =  dbname + certBorrowSubet;
                    if(number != 0)
                        sql.append(" union all ");
                    sql.append("select  "+certBorrowSubet+ "23 ,  "+certBorrowSubet+ "09");
                    sql.append(" from " +nbasecert);
                    sql.append(" where "+ certBorrowSubet + "03 =  ?");
                    sql.append(" AND "+ certBorrowSubet + "19 = '03' ");
                    paramList.add(certNOItemIdValue);
                    number++;
                }
                sql.append(" order by "+certBorrowSubet+ "09 asc");
                rsBorrow = dao.search(sql.toString(), paramList);
                number = 0;
                name = "";
                // 44782  借出标识
                returnFlag = "";
                while(rsBorrow.next()){
                    number++;
                    returnFlag = rsBorrow.getString(certBorrowSubet + "23");
                }

                certMap.put("certNameValue", certNameValue);
                certMap.put("certOrganizationValue",certOrganizationValue);
                certMap.put("certEndDateItemIdValue", certEndDateItemIdValue);
                // 45号是否代码类（1：已归还；2：未归还）
                certMap.put("returnFlag", returnFlag);
                certMap.put("certNOItemIdValue", certNOItemIdValue);
                certMap.put("number", number);

                certSubetMsg.add(certMap);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rsBorrow);
        }
        return certSubetMsg;
    }


    /**
     * 我的证书借阅情况
     * @param map
     * @return
     * @throws GeneralException
     */
    public ArrayList getCertBorrowSubetMsg(String certNOItemIdValue) throws GeneralException{

        RowSet rsBorrow = null;
        RowSet rs = null;
        ArrayList certBorrowSubetMsg = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(conn);
            String certBorrowSubet = certificateConfigBo.getCertBorrowSubset();
            String nbase = this.userView.getDbname();
            int number = 0;
            String name = "";

            boolean returnFlag = true;
            ArrayList paramList = new ArrayList();
            /**
             * 查询本人的证书借阅情况  需查询全部人员库的员工借阅情况
             */
            StringBuffer sql = new StringBuffer("");
            sql.append("select Pre, DBName from dbname ");
            rs = dao.search(sql.toString());
            sql = new StringBuffer("");
            while(rs.next()) {
                paramList.add(certNOItemIdValue);
                nbase = rs.getString("Pre");
                String nbasecert  =  nbase + certBorrowSubet;
                if(number != 0)
                    sql.append(" union all ");

                number++;
                sql.append("select  '"+nbase+ "' nbase, A0100, "+certBorrowSubet+ "23, "+certBorrowSubet+ "05, "+certBorrowSubet+ "17, ");
                sql.append( certBorrowSubet + "13, "+ certBorrowSubet + "09, "+ certBorrowSubet + "15   from " +nbasecert);
                sql.append(" where "+ certBorrowSubet + "03 =  ?");
                sql.append(" AND "+ certBorrowSubet + "19 = '03' ");
            }
            sql.append(" order by "+certBorrowSubet+ "09 desc");

            rsBorrow = dao.search(sql.toString(), paramList);
            while(rsBorrow.next()){
                HashMap certBorrowMap = new HashMap();
                returnFlag = true;

                if("2".equalsIgnoreCase(rsBorrow.getString(certBorrowSubet + "23")))
                    returnFlag = false;

                paramList.clear();
                nbase = rsBorrow.getString("nbase");
                paramList.add(rsBorrow.getString("A0100"));
                sql = new StringBuffer("");
                sql.append(" select  A0101  from  "+nbase+"A01 where A0100 = ? ");
                rs = dao.search(sql.toString(), paramList);
                if(rs.next()) {
                    name = rs.getString("A0101");
                }

                Date date =rsBorrow.getDate(certBorrowSubet + "09") ;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String borrowDate = formatter.format(date);

                certBorrowMap.put("borrowName", name);
                certBorrowMap.put("borrowReason", rsBorrow.getString(certBorrowSubet + "13"));
                certBorrowMap.put("borrowDate", borrowDate);
                certBorrowMap.put("returnFlag", returnFlag);
                certBorrowMap.put("certificateName", rsBorrow.getString(certBorrowSubet + "05"));

                if(returnFlag){
                    Date returnDate =rsBorrow.getDate(certBorrowSubet + "15") ;
                    String returnDay = formatter.format(returnDate);
                    certBorrowMap.put("returnDate", returnDay);
                }


                certBorrowSubetMsg.add(certBorrowMap);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rsBorrow);
            PubFunc.closeDbObj(rs);
        }
        return certBorrowSubetMsg;
    }
    /**
     * 获取 借阅证书列表 表格控件配置
     * @param subModuleId
     * @return
     * @throws GeneralException
     */
    public String getAlreadyBorrowCertTableConfig(String subModuleId) throws GeneralException {

        String config = "";
        try {

            // 获取列头集合
            ArrayList<ColumnsInfo> columnList = getAlreadyBorrowCertColumns();
            // 获取sql
            String sql = getAlreadyBorrowCertSql(columnList);

            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnList, subModuleId, this.userView, this.conn);
            builder.setDataSql(sql);
            builder.setOrderBy("");
            builder.setColumnFilter(true);//统计过滤
            builder.setScheme(false);//栏目设置
            builder.setSelectable(false);//选框
            builder.setEditable(false);//表格编辑
            builder.setPageSize(20);//每页条数
            builder.setLockable(true);
            config = builder.createExtTableConfig();

        }catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return config;
    }
    /**
     * 获取 借阅证书列表 表格控件配置
     * @param subModuleId
     * @param canBowFlag =1显示全部证书  =2显示可借阅证书
     * @return
     * @throws GeneralException
     */
    public String getTableConfigForDiff(String subModuleId, String canBowFlag) throws GeneralException {

        String config = "";
        try {
            // 获取列头集合
            ArrayList<ColumnsInfo> columnsList = this.getCerColumnList(subModuleId);
            // 获取sql
            String datasql = this.getCerSql(canBowFlag, columnsList);
            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsList, subModuleId, this.userView, this.conn);

            builder.setDataSql(datasql);
            builder.setOrderBy(" order by nbase, A0100");
            builder.setColumnFilter(true);//统计过滤
            builder.setScheme(true);//栏目设置
            builder.setSelectable(true);//选框
            builder.setEditable(false);//表格编辑
            builder.setPageSize(20);//每页条数
            builder.setSchemeItemKey("A");
            builder.setLockable(true);
            builder.setTableTools(this.getButtonList(""));//表格工具栏功能
            // 45555 增加栏目设置回调，防止刷新整个页面
            builder.setSchemeSaveCallback("employeeDashboard.borrowedCertificates");
            config = builder.createExtTableConfig();

        }catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return config;
    }
    /**
     * 获取按钮列表
     *
     * @return
     */
    public ArrayList<ButtonInfo> getButtonList(String type) {

        ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
        ButtonInfo querybox = new ButtonInfo();
        querybox.setText("栏目设置");
        querybox.setFunctype(ButtonInfo.FNTYPE_SCHEME);
        buttonList.add(querybox);

        querybox = new ButtonInfo();
        querybox.setFunctionId("CF01030002");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入姓名、证书名称...");
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 获取SQL
     * @param canBowFlag
     * @return
     * @throws GeneralException
     */
    public String getCerSql(String canBowFlag, ArrayList<ColumnsInfo> columnsList)throws GeneralException {

        // 证书人员库
        ArrayList dbnames = certificateConfigBo.getCertNbase();
        // 证书信息集
        String certSubset = certificateConfigBo.getCertSubset();
        // 证书状态
        String certStatus = certificateConfigBo.getCertStatus();
        // 证书类别指标
        String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
        // 证书编号
        String certNOItemId = certificateConfigBo.getCertNOItemId();
        // 借阅子集
        String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
        // 证书是否借出
        String certBorrowState = certificateConfigBo.getCertBorrowState();
        // 证书到期指标
        String certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
        // 证书分类代码类
        String certCategoryCode = certificateConfigBo.getCertCategoryCode();
        // 自助用户证书权限条件
        String sqlWhere = certificatePrivBo.getSelfCertOrgWhere(conn, userView, "a");

        StringBuffer columns = new StringBuffer("");
        for (int i = 0; i < columnsList.size(); i++) {
            ColumnsInfo column = columnsList.get(i);
            String columnid = column.getColumnId();
            if("nbase".equalsIgnoreCase(columnid) || "A0101".equalsIgnoreCase(columnid)
                    || "borrowing_id".equalsIgnoreCase(columnid))
                continue;
            String fieldSetid = StringUtils.isBlank(column.getFieldsetid()) ? "" : column.getFieldsetid().toLowerCase();
            if(certSubset.equalsIgnoreCase(fieldSetid))
                columns.append("a." + columnid + ",");
            else
                columns.append(fieldSetid+"." + columnid + ",");
        }

        columns.setLength(columns.length() - 1);

        String nowParam = DateUtils.format(new Date(), "yyyy-MM-dd");
        StringBuffer certTotalsql = new StringBuffer("");
        for(int i=0;i<dbnames.size();i++) {

            String dbname = (String)dbnames.get(i);
            if(StringUtils.isBlank(dbname))
                continue;

            certTotalsql.append("select '借阅' borrowing_id,'"+dbname+"' nbase,a01.A0101, ").append(columns.toString());
            certTotalsql.append(" from ").append(dbname).append(certSubset);
            certTotalsql.append(" a left join ").append(dbname).append("A01 a01 on a.A0100=a01.A0100 ");

            StringBuffer setidsql = new StringBuffer(",");
            for (int j = 0; j < columnsList.size(); j++) {
                ColumnsInfo column = columnsList.get(j);
                String fieldSetid = StringUtils.isBlank(column.getFieldsetid()) ? "" : column.getFieldsetid().toLowerCase();
                if("A01".equalsIgnoreCase(fieldSetid) || fieldSetid.equalsIgnoreCase(certSubset)
                        || StringUtils.isBlank(fieldSetid))
                    continue;

                if(!StringUtils.contains(setidsql.toString(), ","+fieldSetid+",")){
                    setidsql.append(fieldSetid + ",");
//					if(fi.isPerson()){      //人员子集
                    String tablename = dbname + column.getFieldsetid().toLowerCase();
                    certTotalsql.append(" left join "+tablename+" "+fieldSetid+" on " + "a.A0100="+fieldSetid+".A0100");
//					}
                }
            }

            certTotalsql.append(" where 1=1");
            // 增加权限条件
            certTotalsql.append(" and ").append(sqlWhere);
            /**
             * 可借阅证书 规则
             * 1、证书状态为可用
             * 2、证书状态为空
             * 3、该证书未在借阅子集中 或 在借阅子集中为已归还状态
             * （即：该证书在借阅子集中不存在  未归还 的记录）
             * 4、已到期证书不可借阅
             */
            if("2".equals(canBowFlag)) {
                certTotalsql.append(" and (a.").append(certStatus + "='01'"
                        + "or a.").append(certStatus).append(" IS NULL or a.").append(certStatus).append("='') ");
                certTotalsql.append(" and a.").append(certBorrowState + "<>'1' ");
                certTotalsql.append(" and (").append(Sql_switcher.dateToChar("a." + certEndDateItemId, "yyyy-mm-dd")+">'"+nowParam+"' "
                        // 49411 到期日期为空的证书也可以借阅
                        + " or (a."+ certEndDateItemId+" IS NULL ");
                if(Sql_switcher.dbflag == Constant.MSSQL)
                    certTotalsql.append(" or a."+ certEndDateItemId+"='' ");
                certTotalsql.append("))");
                //and (NOT EXISTS (SELECT A0C03 FROM UsrA0C c WHERE c.A0C01=a.A0D01 and c.A0C03=a.A0D03 and c.A0C23='2'))
                certTotalsql.append(" and (NOT EXISTS (SELECT "+certBorrowSubset+"03 FROM ").append(dbname).append(certBorrowSubset).append(" c "
                        + " WHERE c."+certBorrowSubset+"01=a."+certCategoryItemId+" "
                        + "and c."+certBorrowSubset+"03=a."+certNOItemId+" and c."+certBorrowSubset+"23='2'))");
            }
            if(i < dbnames.size()-1)
                certTotalsql.append(" union all ");
        }
        // 过滤撤销的证书分类下的证书
        certTotalsql.insert(0, "select * from (");
        certTotalsql.append(") temp where " + certCategoryItemId + " in (");
        certTotalsql.append("select codeitemid from codeitem");
        certTotalsql.append(" where " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
        certTotalsql.append(">='" + nowParam + "'");
        certTotalsql.append(" and codesetid='" + certCategoryCode + "'");
        certTotalsql.append(")");

        return certTotalsql.toString();
    }
    /**
     * 获取证书列集合
     * @param type
     * @return
     * @throws GeneralException
     */
    public ArrayList getCerColumnList(String subModuleId)throws GeneralException {

        ArrayList columnsList = new ArrayList();
        ColumnsInfo columnsInfo = new ColumnsInfo();
        // 证书信息集
        String certSubset = certificateConfigBo.getCertSubset();
        // 证书所属组织
        String certOrganization = certificateConfigBo.getCertOrganization();

        TableFactoryBO tableBo = new TableFactoryBO(subModuleId, this.userView, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        String coulumns = ",";
        if (scheme != null) {
            Integer schemeId = (Integer) scheme.get("schemeId");
            ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);
            for(int i = 0; i < columnConfigList.size(); i++){
                ColumnConfig column = columnConfigList.get(i);
                String columnid = column.getItemid();
                if(null == column)
                    continue;
                if("A0101".equalsIgnoreCase(columnid) || "borrowing_id".equalsIgnoreCase(columnid)
                        || "nbase".equalsIgnoreCase(columnid) || "A0100".equalsIgnoreCase(columnid))
                    continue;

                FieldItem fi = DataDictionary.getFieldItem(columnid, column.getFieldsetid());
                if(null == fi)
                    continue;
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(column.getItemid());
                info.setColumnDesc(StringUtils.isEmpty(column.getItemdesc()) ? fi.getItemdesc() : column.getItemdesc());
                info.setColumnType(column.getItemtype());
                info.setColumnWidth(100);
                info.setColumnLength(fi.getItemlength());
                info.setFieldsetid(column.getFieldsetid());
                info.setSortable(true);
                info.setCodesetId(fi.getCodesetid());
                info.setTextAlign(column.getAlign()+"");

                columnsList.add(info);
                coulumns += columnid + ",";
            }
        }
        // 业务字典指标
        ArrayList fieldList = DataDictionary.getFieldList(certSubset, 1);

        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem fi = (FieldItem) fieldList.get(i);
            // 去除没有启用的指标
            if (!"1".equals(fi.getUseflag()))
                continue;
            // 去除隐藏的指标
            if (!"1".equals(fi.getState()))
                continue;

            String itemid = fi.getItemid();
            // 去除不需要的指标
            if (coulumns.indexOf("," + itemid.toLowerCase() + ",") != -1)
                continue;

            columnsInfo = getColumnsInfoByFi(fi, 100);
            // 所属机构列暂时不走权限范围
            if(certOrganization.equalsIgnoreCase(itemid)) {
                columnsInfo.setCtrltype("3");
                columnsInfo.setNmodule("10");
            }
            columnsList.add(columnsInfo);
        }

        FieldItem item = DataDictionary.getFieldItem("A0101", "A01");
        columnsInfo = getColumnsInfoByFi(item, 100);
        columnsList.add(columnsInfo);
        columnsInfo = getColumnsInfo("borrowing_id", "操作", 180, "0", "A", 100, 0, "");
        columnsInfo.setRendererFunc("employeeDashboard.borrowingOneCer");
        columnsInfo.setTextAlign("center");
        columnsList.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("nbase");
        columnsInfo.setColumnDesc("人员库");
        // 加密
        columnsInfo.setEncrypted(true);
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("a0100");
        columnsInfo.setColumnDesc("人员编号");
        columnsInfo.setFieldsetid(certSubset);
        // 加密
        columnsInfo.setEncrypted(true);
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(columnsInfo);

        return columnsList;
    }
    /**初始化列对象ColumnsInfo
     * @param fi
     * @param columnWidth
     * @return
     */
    private ColumnsInfo getColumnsInfoByFi(FieldItem fi, int columnWidth){
        ColumnsInfo co = new ColumnsInfo();

        String itemid = fi.getItemid();
        String itemdesc = fi.getItemdesc();
        String codesetId = fi.getCodesetid();
        String columnType = fi.getItemtype();
        int columnLength = fi.getDisplaywidth();// 显示长度
        int decimalWidth = fi.getDecimalwidth();// 小数位
        String fieldsetid = fi.getFieldsetid();
        co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
                columnType, columnLength, decimalWidth, fieldsetid);

        return co;
    }
    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId		id
     * @param columnDesc	描述
     * @param columnWidth	列宽
     * @param codesetId		指标集
     * @param columnType	类型N|M|A|D
     * @param columnLength	显示长度
     * @param decimalWidth	小数位
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
                                       int columnWidth, String codesetId, String columnType,
                                       int columnLength, int decimalWidth, String fieldsetid) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnLength(columnLength);// 显示长度
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setFieldsetid(fieldsetid);
//		columnsInfo.setReadOnly(true);// 是否只读

        return columnsInfo;
    }
    /**
     * 获取借阅证书列表 表头显示列
     *
     * @return
     */
    public ArrayList<ColumnsInfo> getAlreadyBorrowCertColumns() {

        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
        ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList(certBorrowSubset, Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldItemList.size(); i++) {
            FieldItem fi = fieldItemList.get(i);
            if(null == fi)
                continue;

            ColumnsInfo info = this.getColumnsInfoByFi(fi, 100);
            // 证书类别 编号 名称 锁列
            if(fi.getItemid().equalsIgnoreCase(certBorrowSubset + "01")
                    || fi.getItemid().equalsIgnoreCase(certBorrowSubset + "03")
                    || fi.getItemid().equalsIgnoreCase(certBorrowSubset + "05")) {

                info.setLocked(true);
            }
            // 单独处理
            if(fi.getItemid().equalsIgnoreCase(certBorrowSubset + "23")){
                info.setColumnId(fi.getItemid());
                info.setColumnDesc("归还标识");
                info.setColumnType(fi.getItemtype());
                info.setColumnWidth(100);
                info.setFieldsetid(fi.getFieldsetid());
                info.setColumnLength(100);
                info.setSortable(true);
                info.setCodesetId(fi.getCodesetid());
                info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            } else if(fi.getItemid().equalsIgnoreCase(certBorrowSubset + "19")){
                info.setColumnId(fi.getItemid());
                info.setColumnDesc("状态");
                info.setColumnType(fi.getItemtype());
                info.setColumnWidth(100);
                info.setFieldsetid(fi.getFieldsetid());
                info.setColumnLength(100);
                info.setSortable(true);
                info.setCodesetId(fi.getCodesetid());
                info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            } else
                info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);

            if ("N".equalsIgnoreCase(fi.getItemtype())) {
                info.setDecimalWidth(fi.getDecimalwidth());
                info.setTextAlign("right");
            }

            columnList.add(info);
        }
        return columnList;
    }

    /**
     * 获取自助用户借阅证书列表 查询SQL
     * @param columnList
     * @return
     */
    public String getAlreadyBorrowCertSql(ArrayList<ColumnsInfo> columnList) {

        String a0100 = this.userView.getA0100();
        String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
        StringBuffer sqlColumns = new StringBuffer("select ");
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < columnList.size(); i++) {
            ColumnsInfo column = columnList.get(i);
            sqlColumns.append(column.getColumnId() + ",");
        }
        sqlColumns.setLength(sqlColumns.length() - 1);

        sql.append(sqlColumns);
        sql.append(" from ").append(this.userView.getDbname() + certBorrowSubset);
        sql.append(" where a0100 = '").append(this.userView.getA0100()).append("' ");
        // 证书类别指标
        String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
        // 证书类别代码类
        String certCategoryCode = certificateConfigBo.getCertCategoryCode();
        String nowParam = DateUtils.format(new Date(), "yyyy-MM-dd");
        // 过滤撤销的证书分类下的证书
        sql.append(" and " + certBorrowSubset + "01 in (");
        sql.append("select codeitemid from codeitem");
        sql.append(" where " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
        sql.append(">='" + nowParam + "'");
        sql.append(" and codesetid='" + certCategoryCode + "'");
        sql.append(")");
        return sql.toString();
    }
    /**
     * 获取证书子集的对应指标
     * @return
     */
    public HashMap getCerFieldsetid() {
        //'cerNum', 'cerName', 'cerType', 'cerPerName'(a0101)
        HashMap map = new HashMap();
        // 证书编号
        map.put("cerNum", certificateConfigBo.getCertNOItemId());
        // 证书名称
        map.put("cerName", certificateConfigBo.getCertName());
        // 证书类别指标
        map.put("cerType", certificateConfigBo.getCertCategoryItemId());
        // 证书状态
        map.put("cerState", certificateConfigBo.getCertStatus());
        // 证书到期指标
        map.put("certEndDate", certificateConfigBo.getCertEndDateItemId());
        // 证书是否借出
        map.put("certBorrowState", certificateConfigBo.getCertBorrowState());
        // 借阅证书子集
        map.put("certBorrowSubset", certificateConfigBo.getCertBorrowSubset().toLowerCase());

        return map;
    }
    /**
     * 借阅人  借阅功能
     * @param browStoreData		借阅的证书集合
     * @param browDate			借阅日期
     * @param retunDate			预计归还日期
     * @param browReason		借阅原因
     * @param fieldsData		借阅子集维护的其他指标集合
     * @return
     * @throws GeneralException
     */
    public String addBrowRecords(ArrayList browStoreData, String browDate, String retunDate
            , String browReason, ArrayList fieldsData) throws GeneralException{
        String str = "";
        RowSet rs = null;
        try {
            // 取参数借阅子集
            String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
            if(StringUtils.isBlank(certBorrowSubset))
                return null;
            String nbase = this.userView.getDbname();
            if(StringUtils.isBlank(nbase))
                return null;
            String A0100 = this.userView.getA0100();
            if(StringUtils.isBlank(A0100))
                return null;

            StringBuffer sql = new StringBuffer("");
            ContentDAO dao = new ContentDAO(conn);

            int num = 0;
            sql.append("select max(I9999) I9999  from ").append(nbase).append(certBorrowSubset);
            rs = dao.search(sql.toString());
            while(rs.next()) {
                num = rs.getInt("I9999");
            }
            Timestamp browDateT = new Timestamp(DateUtils.getDate(browDate, "yyyy-MM-dd").getTime());
            Timestamp retunDateT = new Timestamp(DateUtils.getDate(retunDate, "yyyy-MM-dd").getTime());

            StringBuffer insertSql = new StringBuffer("");
            insertSql.append("insert into ").append(nbase).append(certBorrowSubset);
            insertSql.append(" (I9999,A0100,").append(certBorrowSubset).append("01,").append(certBorrowSubset).append("03,").append(certBorrowSubset).append("05,")
                    .append(certBorrowSubset).append("07,").append(certBorrowSubset).append("09,").append(certBorrowSubset).append("11,")
                    .append(certBorrowSubset).append("13,").append(certBorrowSubset).append("19,").append(certBorrowSubset).append("23");
            insertSql.append(",CreateTime,ModTime,CreateUserName,ModUserName");

            StringBuffer insertValueSql = new StringBuffer("");
            insertValueSql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            // 处理其他指标集合
            ArrayList fieldValues = new ArrayList();
            for(int i=0;i<fieldsData.size();i++) {
                MorphDynaBean map = (MorphDynaBean)fieldsData.get(i);
                String itemid = (String)map.get("itemid");
                String itemtype = (String)map.get("itemtype");
                String codesetid = (String)map.get("codesetid");
                String value = (String)map.get("value");

                if("A".equalsIgnoreCase(itemtype)) {
                    if(!"0".equals(codesetid))
                        value = value.split("`")[0];
                    fieldValues.add(value);
                }else if("N".equalsIgnoreCase(itemtype)) {
                    fieldValues.add(Integer.parseInt(value));
                }else if("D".equalsIgnoreCase(itemtype)) {
                    Timestamp date = null;
                    if(StringUtils.isNotBlank(value))
                        date = new Timestamp(DateUtils.getDate(value, "yyyy-MM-dd").getTime());
                    fieldValues.add(date);
                }else
                    fieldValues.add(value);

                insertSql.append(","+itemid);
                insertValueSql.append(",?");
            }
            insertSql.append(")");
            insertValueSql.append(")");
            sql.setLength(0);
            sql.append(insertSql.toString()).append(" values ").append(insertValueSql.toString());

            // 证书人员库
            ArrayList dbnames = certificateConfigBo.getCertNbase();
            // 证书信息集
            String certSubset = certificateConfigBo.getCertSubset();
            // 证书类别指标
            String certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
            // 证书编号
            String certNOItemId = certificateConfigBo.getCertNOItemId();
            // 证书是否借出
            String certBorrowState = certificateConfigBo.getCertBorrowState();
            // 更改证书信息集 的借出状态
            StringBuffer sqlCer = new StringBuffer("");
            ArrayList cersList = new ArrayList();
            for(int i=0;i<dbnames.size();i++) {

                String dbname = (String)dbnames.get(i);
                if(StringUtils.isBlank(dbname))
                    continue;

                sqlCer.setLength(0);
                cersList = new ArrayList();
                sqlCer.append("update ").append(dbname).append(certSubset).append(" set ")
                        .append(certBorrowState).append("='1' where ").append(certCategoryItemId).append("=? and ").append(certNOItemId).append("=? ");

                for(int j=0;j<browStoreData.size();j++) {
                    MorphDynaBean map = (MorphDynaBean)browStoreData.get(j);
                    String nbased = PubFunc.decrypt((String)map.get("nbase"));
                    if(dbname.equalsIgnoreCase(nbased)) {
                        ArrayList cerList = new ArrayList();
                        String cerType = (String)map.get("cerType");
                        cerList.add(cerType.split("`")[0]);
                        cerList.add((String)map.get("cerNum"));
                        cersList.add(cerList);
                    }
                }
                // 分不同人员库 进行update
                if(cersList.size() > 0) {
                    dao.batchUpdate(sqlCer.toString(), cersList);
                }
            }

            Timestamp timestamp = new Timestamp(new Date().getTime());
            String userFullName = this.userView.getUserFullName();
            ArrayList valuesList = new ArrayList();
            for(int i=0;i<browStoreData.size();i++) {
                num++;
                MorphDynaBean map = (MorphDynaBean)browStoreData.get(i);
                ArrayList valueList = new ArrayList();
                valueList.add(num);
                valueList.add(A0100);
                String cerType = (String)map.get("cerType");
                valueList.add(cerType.split("`")[0]);
                valueList.add((String)map.get("cerNum"));
                valueList.add((String)map.get("cerName"));
                valueList.add((String)map.get("cerPerName"));
                valueList.add(browDateT);
                valueList.add(retunDateT);
                valueList.add(browReason);
                valueList.add("02");
                // 归还标识45号是否代码类（1：已归还；2：未归还）
                valueList.add("2");
                valueList.add(timestamp);
                valueList.add(timestamp);
                valueList.add(userFullName);
                valueList.add(userFullName);
                // 增加其他指标数据
                valueList.addAll(fieldValues);

                valuesList.add(valueList);
            }
//    		dao.batchUpdate(sql.toString(), cersList);
            dao.batchInsert(sql.toString(), valuesList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return str;
    }

    public ArrayList getfieldSetList() throws GeneralException{

        ArrayList list = new ArrayList();
        try {
            // 取参数借阅子集
            String borset = certificateConfigBo.getCertBorrowSubset();
            //查询出所有权限范围内的指标
            ArrayList fieldprivlist=this.userView.getPrivFieldList(borset);
            for(Object obj:fieldprivlist){
                FieldItem item=(FieldItem)obj;
                String itemid = item.getItemid();
                // 系统定义的不显示
                if((borset+"01").equalsIgnoreCase(itemid) || (borset+"03").equalsIgnoreCase(itemid) || (borset+"05").equalsIgnoreCase(itemid)
                        || (borset+"07").equalsIgnoreCase(itemid) || (borset+"09").equalsIgnoreCase(itemid) || (borset+"11").equalsIgnoreCase(itemid)
                        || (borset+"13").equalsIgnoreCase(itemid) || (borset+"15").equalsIgnoreCase(itemid) || (borset+"17").equalsIgnoreCase(itemid)
                        || (borset+"19").equalsIgnoreCase(itemid) || (borset+"21").equalsIgnoreCase(itemid) || (borset+"23").equalsIgnoreCase(itemid)
                        || (borset+"25").equalsIgnoreCase(itemid)) {
                    continue;
                }
                LazyDynaBean bean=new LazyDynaBean();
                bean.set("fieldsetid", item.getFieldsetid());
                bean.set("itemid", itemid);
                bean.set("itemdesc", item.getItemdesc());
                bean.set("itemtype", item.getItemtype());
                bean.set("itemlength", item.getItemlength());
                bean.set("codesetid", item.getCodesetid());
                bean.set("allowblank", item.isFillable());
                bean.set("demicallength",item.getDecimalwidth());
                bean.set("value", "");
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        }
        return list;
    }
}
