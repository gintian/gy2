package com.hjsj.hrms.module.kq.holiday.businessobject;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.module.kq.util.*;
import com.hjsj.hrms.utils.FuncVersion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;
/**
 * 假期管理业务类
 * @Title:        HolidayBo.java
 * @Description:  处理假期管理中的相关业务
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:00:42
 * @author        chenxg
 * @version       1.0
 */
public class HolidayBo {
    private Connection conn;
    private UserView userView;
    private String holidTpye;
    private String holidYear;
    private String leaveTimeTypeUsedOverTime;
    private String leaveActiveTime;

    public HolidayBo(Connection conn) {
        this.conn = conn;
    }
    
    public HolidayBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }
    
    public String getHolidTpye() {
        return holidTpye;
    }
    
    public String getHolidYear() {
        return holidYear;
    }

    /**
     * 获取表头显示列
     * 
     * @return
     */
    public ArrayList<ColumnsInfo> getColumns(String submoduleid) {
        
        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        TableFactoryBO tableBo = new TableFactoryBO(submoduleid, this.userView, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        StringBuffer coulumns = new StringBuffer(",");
        if (scheme != null) {
            Integer schemeId = (Integer) scheme.get("schemeId");
            ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);
            
            for(int i = 0; i < columnConfigList.size(); i++){
                ColumnConfig column = columnConfigList.get(i);
                if(null == column)
                    continue;
                
                if("jobNumber".equalsIgnoreCase(column.getItemid())) {
                    
                    ColumnsInfo kqNumberInfo = new ColumnsInfo();
                    kqNumberInfo.setColumnId("jobNumber");
                    kqNumberInfo.setColumnDesc("工号");
                    kqNumberInfo.setColumnType("A");
                    kqNumberInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                    kqNumberInfo.setColumnWidth(100);
                    kqNumberInfo.setFieldsetid("");
                    kqNumberInfo.setSortable(true);
                    kqNumberInfo.setEditableValidFunc("false");
                    columnList.add(kqNumberInfo);
                }else {
                    
                    FieldItem fi = DataDictionary.getFieldItem(column.getItemid(), column.getFieldsetid());
                    
                    if(null == fi)
                        continue;
                    
                    ColumnsInfo info = this.getHolidayColumn(column, fi, column.getFieldsetid(), true);
                    KqPrivBo.setKqPrivCodeSource(fi, info);
                    columnList.add(info);
                    coulumns.append(column.getItemid() + ",");
                }
            }
        } 
        
        ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldItemList.size(); i++) {
            FieldItem fi = fieldItemList.get(i);
            if(null == fi)
                continue;
            String itemid = fi.getItemid();
            //隐藏指标，排序用到的b0110,e0122,e01a1,a0100例外
            if ("0".equals(fi.getState()) && !",nbase,b0110,e0122,e01a1,a0100,q1709,".contains("," + itemid + ","))
                continue;
            
            if(coulumns.toString().contains("," + itemid + ","))
                continue;
            
            coulumns.append(itemid + ",");
            ColumnsInfo info = this.getHolidayColumn(fi, "Q17", true);
            KqPrivBo.setKqPrivCodeSource(fi, info);
            columnList.add(info);
            // 把工号指标固定插入到a0100之后
            if(scheme == null && "a0100".equalsIgnoreCase(itemid)) {
                ColumnsInfo kqNumberInfo = new ColumnsInfo();
                kqNumberInfo.setColumnId("jobNumber");
                kqNumberInfo.setColumnDesc("工号");
                kqNumberInfo.setColumnType("A");
                kqNumberInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                kqNumberInfo.setColumnWidth(100);
                kqNumberInfo.setFieldsetid("");
                kqNumberInfo.setSortable(true);
                kqNumberInfo.setEditableValidFunc("false");
                // 42941 增加工号不需添加到i+1,直接添加即可
                columnList.add(kqNumberInfo);
            }
        }
        // 34146 增加 库名|a0100|拼接字段隐藏列，用于查询个人明细信息
        ColumnsInfo info = new ColumnsInfo();
        info.setColumnId("primaryKey");
        info.setColumnDesc("人员编号人员库");
        info.setColumnType("A");
        info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        info.setEncrypted(true);
        columnList.add(info);
        
        return columnList;
    }

    /**
     * 获取没有栏目设置保存过的列头对象
     * @param fieldItem 业务字典获取的指标对象
     * @param table     Q17 或 Q33或A01
     * @return
     */
    private ColumnsInfo getHolidayColumn(FieldItem fieldItem, String table, boolean displayFlag) {
        ColumnsInfo info = this.getHolidayColumn(null, fieldItem, table, displayFlag);
        return info;
    }
    
    /**
     * 获取有栏目设置保存过的列头对象
     * @param column    表格栏目设置列头对象
     * @param fieldItem 业务字典获取的指标对象
     * @param table     Q17 或 Q33
     * @return
     */
    private ColumnsInfo getHolidayColumn(ColumnConfig column, FieldItem fieldItem, String table, 
            boolean displayFlag) {

        HashMap<String, String> fieldMap = new HashMap<String, String>();
        // 假期管理休假表需要用
        if("Q17".equalsIgnoreCase(table)) {
            fieldMap.put("NBASE", "");
            fieldMap.put("Q1701", "");
            fieldMap.put("I9999", "");
            fieldMap.put("A0100", "");
            fieldMap.put("B0110", "");
            fieldMap.put("E0122", "");
            fieldMap.put("E01A1", "");
            fieldMap.put("A0101", "");
            fieldMap.put("Q1709", "");
            fieldMap.put("Q1707", "");
        }
        
        ColumnsInfo info = new ColumnsInfo();
        if(null == column) {
            // 栏目设置没有私有方案时column为null
            info.setColumnId(fieldItem.getItemid());
            info.setColumnDesc(fieldItem.getItemdesc());
            info.setColumnType(fieldItem.getItemtype());
            info.setColumnWidth(100);
            info.setFieldsetid(fieldItem.getFieldsetid());
            info.setColumnLength(fieldItem.getItemlength());
            info.setSortable(true);
            info.setCodesetId(fieldItem.getCodesetid());
        }else {
            // column不为null说明该表格栏目设置有私有方案
            info.setColumnId(column.getItemid());
            info.setColumnDesc(StringUtils.isEmpty(column.getItemdesc()) ? fieldItem.getItemdesc() : column.getItemdesc());
            info.setColumnType(column.getItemtype());
            info.setColumnWidth(100);
            info.setColumnLength(fieldItem.getItemlength());
            info.setFieldsetid(column.getFieldsetid());
            info.setSortable(true);
            info.setCodesetId(fieldItem.getCodesetid());
            info.setTextAlign(column.getAlign()+"");
        }
        // a0100单独处理
        if("a0100".equalsIgnoreCase(fieldItem.getItemid())){
            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            info.setEncrypted(true);
        } else if(!displayFlag)
            info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
        else
            info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
        
        String itemid = fieldItem.getItemid();
        if(column != null)
            itemid = column.getItemid();
        
        if("nbase".equalsIgnoreCase(itemid) || "q1701".equalsIgnoreCase(itemid)
                || "q1709".equalsIgnoreCase(itemid))
            info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        
        if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
            info.setDecimalWidth(fieldItem.getDecimalwidth());
            if(null == column)
                info.setTextAlign("right");
        }
        // 假期管理的处理
        if("Q17".equalsIgnoreCase(table)) {
            if(fieldMap.containsKey(fieldItem.getItemid().toUpperCase()))
                info.setEditableValidFunc("false");
            if("Q17Z1".equalsIgnoreCase(fieldItem.getItemid()))
                info.setValidFunc("holidayManage.startDate");
            else if("Q17Z3".equalsIgnoreCase(fieldItem.getItemid()))
                info.setValidFunc("holidayManage.endDate");
            else if("Q1703".equalsIgnoreCase(fieldItem.getItemid()))
                info.setValidFunc("holidayManage.holidaySum");
            else if("Q1705".equalsIgnoreCase(fieldItem.getItemid()))
                info.setValidFunc("holidayManage.holidayOff");
        }
        // 调休假的处理
        else if("Q33".equalsIgnoreCase(table)) {
            info.setEditableValidFunc("false");
        }
        // 渲染姓名链接函数 
        if("A0101".equalsIgnoreCase(fieldItem.getItemid()))
            info.setRendererFunc("holidayManage.showLeaveDetail");
        
        return info;
    }
    /**
     * 获取调休假表头显示列
     * 
     * @return
     */
    public ArrayList<ColumnsInfo> getLeaveTimeTypeColumns(String submoduleid) {
        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        try{
            HashMap leavetimeUnitMap = getLeavetimeUnit();
            // 获取调休时长单位长度
            int decimalwidth = (Integer)leavetimeUnitMap.get("decimalwidth");
                    
            TableFactoryBO tableBo = new TableFactoryBO(submoduleid, this.userView, conn);
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
                    if("Q3301".equalsIgnoreCase(columnid) || "Q3303".equalsIgnoreCase(columnid))
                        continue;
                    
                    if("jobNumber".equalsIgnoreCase(columnid)) {
                        ColumnsInfo kqNumberInfo = new ColumnsInfo();
                        kqNumberInfo.setColumnId(columnid);
                        kqNumberInfo.setColumnDesc("工号");
                        kqNumberInfo.setColumnType("A");
                        kqNumberInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                        kqNumberInfo.setColumnWidth(100);
                        kqNumberInfo.setFieldsetid("");
                        kqNumberInfo.setSortable(true);
                        kqNumberInfo.setEditableValidFunc("false");
                        columnList.add(kqNumberInfo);
                    }
                    else {
                        String columnidF = columnid;
                        String fieldsetid = column.getFieldsetid();
                        // 是否是自定义列
                        boolean isQ33FBool = ("Q3305F".equalsIgnoreCase(columnid) || "Q3307F".equalsIgnoreCase(columnid)
                                || "Q3309F".equalsIgnoreCase(columnid));
                        if(isQ33FBool) { 
                            fieldsetid = "Q33";
                            columnidF = columnid.substring(0, columnid.length()-1);
                            column.setItemtype("N");
                        }
                        FieldItem fi = DataDictionary.getFieldItem(columnidF, fieldsetid);
                        if(null == fi)
                            continue;
                        ColumnsInfo info = this.getHolidayColumn(column, fi, fieldsetid, true);
                        if(isQ33FBool)  
                            info.setDecimalWidth(decimalwidth);
                        KqPrivBo.setKqPrivCodeSource(fi, info);
                        columnList.add(info);
                        coulumns += columnidF + ",";
                    }
                }
            }
            
            ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("Q33", Constant.USED_FIELD_SET);
            for (int i = 0; i < fieldItemList.size(); i++) {
                FieldItem fi = fieldItemList.get(i);
                if(null == fi)
                    continue;
                String itemid = fi.getItemid();
                if ("0".equals(fi.getState()) && !",nbase,b0110,e0122,e01a1,a0100,".contains("," + itemid + ","))
                    continue;
                
                if(coulumns.contains(","+itemid+",")
                        || "Q3301".equalsIgnoreCase(itemid) || "Q3303".equalsIgnoreCase(itemid))
                    continue;
                
                ColumnsInfo info = this.getHolidayColumn(fi, "Q33", true);
                // 单位小时 显示unit位小数
                if ("q3305".equals(itemid) || "q3307".equals(itemid) || "q3309".equals(itemid)) {
                    info.setDecimalWidth(decimalwidth);
                    info.setColumnId(itemid + "F");
                    info.setFieldsetid("");
//                  info.setRendererFunc("holidayManage.showTimeFormat");                   
                }
                KqPrivBo.setKqPrivCodeSource(fi, info);
                columnList.add(info);
                
                //把工号指标固定插入到a0100之后
                if(scheme == null && "a0100".equalsIgnoreCase(itemid)) {
                    ColumnsInfo kqNumberInfo = new ColumnsInfo();
                    kqNumberInfo.setColumnId("jobNumber");
                    kqNumberInfo.setColumnDesc("工号");
                    kqNumberInfo.setColumnType("A");
                    kqNumberInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                    kqNumberInfo.setColumnWidth(100);
                    kqNumberInfo.setFieldsetid("");
                    kqNumberInfo.setSortable(true);
                    kqNumberInfo.setEditableValidFunc("false");
                    columnList.add(kqNumberInfo);
                }
            }
            // 34146 linbz 增加 库名|a0100|拼接字段隐藏列，用于查询个人明细信息
            ColumnsInfo info = new ColumnsInfo();
            info.setColumnId("primaryKey");
            info.setColumnDesc("人员编号人员库");
            info.setColumnType("A");
            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            info.setEncrypted(true);
            columnList.add(info);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return columnList;
    }
    /**
     * 获取查询数据的sql
     * 
     * @return
     */
    public String getHolidaySql(ArrayList<ColumnsInfo> columnList, String holidTpye, String year) {
        StringBuffer sql = new StringBuffer("select primaryKey,sort,");
        try {
            if(StringUtils.isEmpty(holidTpye))
                holidTpye = this.holidTpye;
            
            if(StringUtils.isEmpty(year))
                year = this.holidYear;
            
            StringBuffer columns = new StringBuffer();
            for (int i = 0; i < columnList.size(); i++) {
                ColumnsInfo column = columnList.get(i);
                // 34146 primaryKey为拼接字段单独拼接处理不需加入columns集合里
                if("primaryKey".equalsIgnoreCase(column.getColumnId()))
                    continue;
                sql.append(column.getColumnId() + ",");
                if("jobNumber".equalsIgnoreCase(column.getColumnId()))
                    columns.append( "a01." + getGNo() + " jobNumber,");
                else if("A01".equalsIgnoreCase(column.getFieldsetid()))
                    columns.append( "a01." + column.getColumnId() + ",");
                else
                    columns.append( "q17." + column.getColumnId() + ",");
            }
            
            sql.setLength(sql.length() - 1);
            columns.setLength(columns.length() - 1);
            ArrayList<String> dbnameList = KqPrivBo.getB0110Dase(this.userView, this.conn);
            sql.append(" from (");
            // 61996 兼容新考勤 权限范围问题
            String whereIN = "";
            KqVer kqVer = new KqVer();
            boolean bool = (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL);
            if(bool) {
            	String whereB0110 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "a.b0110", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
            	String whereE0122 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "a.e0122", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
            	String whereE01a1 = KqPrivForHospitalUtil.getPrivB0110Whr(userView, "a.e01a1", KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
            	whereIN = "("+whereB0110+" or "+whereE0122+" or "+whereE01a1+")";
            }
            
            for(int i = 0; i < dbnameList.size(); i++){
                String dbname = dbnameList.get(i);
                if(i > 0)
                    sql.append(" UNION ALL ");
                // 33584 linbz 查询后拼接字符串连接符 数据库不兼容Sql_switcher.concat()
                sql.append("select q17.nbase").append(Sql_switcher.concat()).append("'|'").append(Sql_switcher.concat()).append("q17.A0100").append(Sql_switcher.concat()).append("'|' primaryKey,b.dbid sort,");
                sql.append(columns);
                sql.append(" from q17");
                sql.append(" inner join ").append(dbname).append("a01 a01");
                sql.append(" on q17.a0100=a01.a0100");
                sql.append(" join DBName b on q17.nbase=b.Pre");
                sql.append(" where q17.nbase='").append(dbname).append("'");
                sql.append(" and q17.a0100 in(select a0100 ");
                if(bool) {
                	sql.append(" FROM ").append(dbname).append("A01 a WHERE ").append(whereIN);
                }else {
                	whereIN = KqPrivBo.getWhereINSql(userView, dbname);
                	sql.append(whereIN);
                }
                sql.append(") ");
            }
            
            sql.append(") a");
            sql.append(" where Q1709 ='" + holidTpye + "'");
            sql.append(" and Q1701 ='" + year + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }

    /**
     * 获取调休假查询数据的sql
     * 
     * @return
     */
    public String getHolidayLeaveTimeTypeSql(ArrayList<ColumnsInfo> columnList, String date) {
        StringBuffer sql = new StringBuffer("select ");
        try {
            String startTime = "";
            String endTime = "";
            
            if(StringUtils.isEmpty(date)) {
                KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.conn, this.userView);
                HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod();
                if(period != null && !period.isEmpty()) {
                    startTime = period.get("from").toString();
                    endTime = period.get("to").toString();
                }
            } else {
                KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.conn, this.userView);
                Date adate = null;
                // 传 年月日 或 只传年份时特殊处理
                if(date.length() == 10)
                    adate = DateUtils.getDate(date, "yyyy-MM-dd");
                else if(date.length() == 4)
                    adate = DateUtils.getDate(date+"-01-01", "yyyy-MM-dd");
                HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod(adate);
                if(period != null && !period.isEmpty()) {
                    startTime = period.get("from").toString();
                    endTime = period.get("to").toString();
                }
            }
            
            this.leaveActiveTime = startTime + "~" + endTime;
            
            HashMap leavetimeUnitMap = getLeavetimeUnit();
            // 获取调休时长单位长度
            int unit = (Integer)leavetimeUnitMap.get("decimalwidth");
            String itemUnit = (String)leavetimeUnitMap.get("item_unit");
            
            String standardUnit = KqParam.getInstance().getSTANDARD_HOURS();
            String tranUnit = standardUnit;
            if (itemUnit == null || itemUnit.length() <= 0)
                itemUnit = KqConstant.Unit.HOUR;
            
            if (itemUnit.equals(KqConstant.Unit.HOUR))
                tranUnit = "60.0";
            else if (itemUnit.equals(KqConstant.Unit.MINUTE))
                tranUnit = "1.0";
            else if (itemUnit.equals(KqConstant.Unit.DAY))
                tranUnit = "(60.0*" + standardUnit + ")";
            
            StringBuffer columns = new StringBuffer();
            for (int i = 0; i < columnList.size(); i++) {
                ColumnsInfo column = columnList.get(i);
                String columnid = column.getColumnId();
                if("q3303".equalsIgnoreCase(columnid))
                    continue;
                
                if("q3305f".equalsIgnoreCase(columnid) || "q3307f".equalsIgnoreCase(columnid)
                        || "q3309f".equalsIgnoreCase(columnid)) {
                    int len = column.getColumnLength();
                    columnid = columnid.substring(0, columnid.length()-1);
                    sql.append("CAST(");
                    sql.append("ROUND(SUM(" + columnid + ")/" + tranUnit + "," + unit + ") AS NUMERIC("+ len +","+ unit +" ) "
                            +" ) AS "+ columnid +"F,");
                }
                else
                    sql.append("max(" + columnid + ") as " + columnid + ",");
                
                // primaryKey为拼接字段单独拼接处理不需加入columns集合里
                if("primaryKey".equalsIgnoreCase(columnid)) 
                    continue;
                if("jobNumber".equalsIgnoreCase(columnid))
                    columns.append("a01." + getGNo() + " jobNumber,");
                else if("A01".equalsIgnoreCase(column.getFieldsetid()))
                    columns.append( "a01." + column.getColumnId() + ",");
                else
                    columns.append("q33." + columnid + ",");
            }
            
            sql.setLength(sql.length() - 1);
            sql.append(" from (");
            columns.setLength(columns.length() - 1);
            ArrayList<String> dbnameList = KqPrivBo.getB0110Dase(this.userView, this.conn);
            for(int i = 0; i < dbnameList.size(); i++){
                String dbname = dbnameList.get(i);
                // 34152 由于只用UNION拼接会去重，该查询中会有记录完全重复的情况所以数据出错，改为UNION ALL
                if(i > 0)
                    sql.append(" UNION ALL ");
                
                String whereIN = KqPrivBo.getWhereINSql(userView, dbname);
                sql.append("select q33.nbase").append(Sql_switcher.concat()).append("'|'").append(Sql_switcher.concat()).append("q33.A0100").append(Sql_switcher.concat()).append("'|' primaryKey, ");
                sql.append(columns);
                sql.append(" from q33 INNER JOIN "+ dbname + "A01 a01");
                sql.append(" on q33.a0100 = a01.a0100");
                sql.append(" where nbase='" + dbname + "'");
                sql.append(" and q3303 >= '" + startTime.replace("-", ".") + "'");
                sql.append(" and q3303 <= '" + endTime.replace("-", ".") + "' ");
                sql.append(" and q33.a0100 in(select a0100 " + whereIN + ")");
            }
            
            sql.append(") a group by nbase,A0100");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }
    /**
     * 获取调休假导入数据下载模板SQL
     * 
     * @return
     */
    public String getDownHolidayLeaveSql(ArrayList<ColumnsInfo> columnList, String leaveActiveTime) {
        StringBuffer sql = new StringBuffer("select ");
        try {
            String startTime = "";
            String endTime = "";
            if(StringUtils.isNotEmpty(leaveActiveTime) && leaveActiveTime.indexOf("~")!=-1) {
                 startTime = leaveActiveTime.split("~")[0];
                 endTime = leaveActiveTime.split("~")[1];
            }else {
                KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.conn, this.userView);
                HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod();
                if(period != null && !period.isEmpty()) {
                    startTime = period.get("from").toString();
                    endTime = period.get("to").toString();
                }
            }
            
            HashMap leavetimeUnitMap = getLeavetimeUnit();
            // 获取调休时长单位长度
            int unit = (Integer)leavetimeUnitMap.get("decimalwidth");
            String itemUnit = (String)leavetimeUnitMap.get("item_unit");
            
            String standardUnit = KqParam.getInstance().getSTANDARD_HOURS();
            String tranUnit = standardUnit;
            if (itemUnit == null || itemUnit.length() <= 0)
                itemUnit = KqConstant.Unit.HOUR;
            
            if (itemUnit.equals(KqConstant.Unit.HOUR))
                tranUnit = "60.0";
            else if (itemUnit.equals(KqConstant.Unit.MINUTE))
                tranUnit = "1.0";
            else if (itemUnit.equals(KqConstant.Unit.DAY))
                tranUnit = "(60.0*" + standardUnit + ")";
            
            StringBuffer columns = new StringBuffer();
            for (int i = 0; i < columnList.size(); i++) {
                ColumnsInfo column = columnList.get(i);
                String columnid = column.getColumnId();
                if("q3305".equalsIgnoreCase(columnid) || "q3307".equalsIgnoreCase(columnid)
                        || "q3309".equalsIgnoreCase(columnid))
                    sql.append("ROUND(" + columnid + "/" + tranUnit + "," + unit + ") as "
                            + columnid +",");
                else
                    sql.append(  columnid + ",");
                
                // primaryKey为拼接字段单独拼接处理不需加入columns集合里
                if("primaryKey".equalsIgnoreCase(columnid)) 
                    continue;
                if("jobNumber".equalsIgnoreCase(columnid))
                    columns.append("a01." + getGNo() + " jobNumber,");
                else
                    columns.append("q33." + columnid + ",");
            }
            
            sql.setLength(sql.length() - 1);
            sql.append(" from (");
            columns.setLength(columns.length() - 1);
            ArrayList<String> dbnameList = KqPrivBo.getB0110Dase(this.userView, this.conn);
            for(int i = 0; i < dbnameList.size(); i++){
                String dbname = dbnameList.get(i);
                if(i > 0)
                    sql.append(" UNION ALL ");
                
                String whereIN = KqPrivBo.getWhereINSql(userView, dbname);
                sql.append("select q33.nbase").append(Sql_switcher.concat()).append("'|'").append(Sql_switcher.concat()).append("q33.A0100").append(Sql_switcher.concat()).append("'|' primaryKey, ");
                sql.append(columns);
                sql.append(" from q33 INNER JOIN "+ dbname + "A01 a01");
                sql.append(" on q33.a0100 = a01.a0100");
                sql.append(" where nbase='" + dbname + "'");
                sql.append(" and q3303 >= '" + startTime.replace("-", ".") + "'");
                sql.append(" and q3303 <= '" + endTime.replace("-", ".") + "' ");
                sql.append(" and q33.a0100 in(select a0100 " + whereIN + ")");
            }
            
            sql.append(") a ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }
    /**
     * 生成一个按钮
     * 
     * @param text
     *            按钮名称
     * @param id
     *            按钮id
     * @param handler
     *            点击按钮触发的事件
     * @param icon
     *            按钮显示的图标
     * @param getdata
     *            事件触发时是否获取选中数据
     * @return
     */
    private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());

        if (icon != null)
            button.setIcon(icon);

        if (id != null)
            button.setId(id);

        return button;
    }

    /**
     * 获取按钮列表
     * 
     * @return
     */
    public ArrayList<ButtonInfo> getButtonList(String holidayType) {
        ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
        ButtonInfo querybox = new ButtonInfo();
        querybox.setText("栏目设置");
        querybox.setFunctype(ButtonInfo.FNTYPE_SCHEME);
        buttonList.add(querybox);
        // 33609 linbz 若不设置调休假类型则显示假期管理假别
        if(!holidayType.equalsIgnoreCase(this.getLeaveTimeTypeUsedOverTime()) || StringUtils.isEmpty(this.getLeaveTimeTypeUsedOverTime())) {
            if(this.userView.hasTheFunction("27041"))
                buttonList.add(newButton("计算", "calculate", "holidayManage.calculate", null, "true"));
            
            if(this.userView.hasTheFunction("27040")) {
                ButtonInfo saveButton = new ButtonInfo();
                saveButton.setFunctionId("KQ00010009");
                saveButton.setFunctype(ButtonInfo.FNTYPE_SAVE);
                saveButton.setText("保存");
                buttonList.add(saveButton);
            }
            
            if(this.userView.hasTheFunction("27040"))
                buttonList.add(newButton("删除", "delete", "holidayManage.deletePersonHoliday", null, "true"));
        } else {
            String otForLeaveCycle = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_CYCLE();
            if("4".equalsIgnoreCase(otForLeaveCycle)) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH) + 1;
                buttonList.add(new ButtonInfo("<a id='asd' href='javascript:void(0)' onclick='holidayManage.SwitchMonth()'>"
                        + "<span id='year'>" 
                        + year + "</span>年<span id='month'>"
                        + month + "</span>月<img id='xiaimg' style='margin-left:2px;margin-right:3px;'"
                        + " src='/workplan/image/jiantou.png' /></a>"));
            }
            
        }
        
        querybox = new ButtonInfo();
        querybox.setFunctionId("KQ00010002");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入姓名、拼音简称、部门名称...");
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 得到假期管理项目的描述
     * 
     * @param hols_type
     * @return
     * @throws GeneralException 
     */
    public String getHolsList(String holidayType) throws GeneralException {
        String code = "";
        RowSet rs = null;
        StringBuffer holidayTpyeJson = new StringBuffer();
        try {
            if (this.userView.isSuper_admin())
                code = "";
            else {
                KqPrivBo KqBo = new KqPrivBo(this.userView, this.conn);
                code = KqBo.getPrivCode();
            }
            
            // 默认只有年假，新版考勤没有假期类型设置功能
            String hols_type = "06";
            
            KqVer kqVer = new KqVer();
            if (kqVer.getVersion() == KqConstant.Version.STANDARD) {
                // 标准版考勤假期类型从假期管理参数中获取
                hols_type = KqParam.getInstance().getHolidayTypes(this.conn, code);
            }
            
            if (hols_type == null || hols_type.length() <= 0)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "没有定义假期管理项目！", "", ""));
            
            String[] types = hols_type.split(",");
            StringBuffer typeIN = new StringBuffer();
            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                if (type == null || hols_type.length() <= 0)
                    continue;
                
                typeIN.append("'" + type + "',");
            }
            
            typeIN.setLength(typeIN.length() - 1);
            holidayTpyeJson.append("[{xtype:'label',text:'假期类别：'}");
            
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid,codeitemdesc from codeitem");
            sql.append(" where codesetid='27' and parentid like '0%' and codeitemid<>parentid");
            sql.append(" and codeitemid in(").append(typeIN.toString()).append(")");
            // 33177 linbz 假期管理的假别排序问题
            sql.append(" order by a0000,codeitemid ");
            rs = dao.search(sql.toString());
            int i = 0;
            while (rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                if (StringUtils.isEmpty(codeitemid)) 
                    continue;
                
                holidayTpyeJson.append(",{xtype:'label',id:'label" + i + "',html:");
                holidayTpyeJson.append("\"<a href='###' onclick=\\\"holidayManage.switchHolidayClass('label" + i + "','");
                holidayTpyeJson.append(PubFunc.encrypt(rs.getString("codeitemid")));
                holidayTpyeJson.append("')\\\">" + rs.getString("codeitemdesc") + "</a>\",");
                if((StringUtils.isEmpty(holidayType) && i == 0) || codeitemid.equalsIgnoreCase(holidayType)){
                    holidayTpyeJson.append("cls:'type-selected-cls',");
                    this.holidTpye = codeitemid;
                }

                holidayTpyeJson.append("style:'margin-left:10px;'}");
                i++;
            }
            
            DbWizard dbWizard = new DbWizard(this.conn);
            FuncVersion fv = new FuncVersion(this.userView);
            // 33609 linbz 增加校验调休假类型 是否为空
            if (fv.haveKqLeaveTypeUsedOverTimeFunc() && StringUtils.isNotEmpty(this.getLeaveTimeTypeUsedOverTime())) {
                String OVERTIME_FOR_LEAVETIME_LIMIT = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();      
                OVERTIME_FOR_LEAVETIME_LIMIT = StringUtils.isEmpty(OVERTIME_FOR_LEAVETIME_LIMIT) ? "0" : OVERTIME_FOR_LEAVETIME_LIMIT;
                
                ArrayList fielditemlist = DataDictionary.getFieldList("Q33",Constant.USED_FIELD_SET);
                
                if (dbWizard.isExistTable("Q33", false) && fielditemlist != null
                        && !"0".equals(OVERTIME_FOR_LEAVETIME_LIMIT) && !"".equals(KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME())) {
                    this.leaveTimeTypeUsedOverTime = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
                    holidayTpyeJson.append(",{xtype:'label',id:'label" + i + "',html:");
                    holidayTpyeJson.append("\"<a href='###' onclick=\\\"holidayManage.switchHolidayClass('label" + i + "','");
                    holidayTpyeJson.append(PubFunc.encrypt(this.leaveTimeTypeUsedOverTime));
                    holidayTpyeJson.append("')\\\">调休假</a>\",");
                    if(this.leaveTimeTypeUsedOverTime.equalsIgnoreCase(holidayType))
                        holidayTpyeJson.append("cls:'type-selected-cls',");
                        
                    holidayTpyeJson.append("style:'margin-left:10px;'}");
                }
            }
            
            holidayTpyeJson.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return holidayTpyeJson.toString();
    }
    /**
     * 得到考勤的年
     * @return
     * @throws GeneralException
     */
    public String getAllHolidayYear() throws GeneralException {

        StringBuffer yearJson = new StringBuffer("["); 
        RowSet rowSet = null;
        try {
            if(StringUtils.isEmpty(this.holidYear))
                getHolidayYear("0");
            
            if(StringUtils.isEmpty(this.holidYear))
                getHolidayYear("1");
            
            StringBuffer strsql = new StringBuffer();
            strsql.append("SELECT DISTINCT kq_year FROM kq_duration order by kq_year desc");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(strsql.toString());
            int i = 0;
            while (rowSet.next()) {
                String holidYear = rowSet.getString("kq_year");
                if(i == 0 && StringUtils.isEmpty(this.holidYear))
                    this.holidYear = holidYear;

                yearJson.append("{id:'" + holidYear + "',name:'" + holidYear + "'},");
            }
            
            if(yearJson.toString().endsWith(","))
                yearJson.setLength(yearJson.length() - 1);
            
            yearJson.append("]");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("", ResourceFactory.getProperty("kq.register.session.nosave"), "", "");
        } finally {
            PubFunc.closeResource(rowSet);
        }

        return yearJson.toString();
    }
    
    /**
     * 得到封存或未封存的考勤的年
     * @param finish 
     * @return
     * @throws GeneralException
     */
    private void getHolidayYear(String finish) throws GeneralException {

        RowSet rowSet = null;
        try {
            KqVer kqVer = new KqVer();
            String curYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

            StringBuffer strsql = new StringBuffer();
            strsql.append("SELECT distinct kq_year FROM kq_duration");
            strsql.append("  where finished=" + finish);
            if("0".equalsIgnoreCase(finish))
                strsql.append(" order by kq_year desc");
            else if("1".equalsIgnoreCase(finish))
                strsql.append(" order by kq_year");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(strsql.toString());
            while (rowSet.next()) {
                String kqYear = rowSet.getString("kq_year");
                // 新考勤期间没有封存状态，尽可能取当前年度
                if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
                    if (StringUtils.equalsIgnoreCase(kqYear, curYear)) {
                        this.holidYear = kqYear;
                        break;
                    }
                }

                this.holidYear = kqYear;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
    }
    
    public String getOrgIds(String name) {
        String orgids = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid");
            sql.append(" from organization");
            sql.append(" where codesetid = 'UM'");
            sql.append(" and (codeitemid in (");
            sql.append(" select e0122");
            sql.append(" from q17)");
            sql.append(" or codeitemid in (");
            sql.append(" select b0110");
            sql.append(" from q17))");
            sql.append(" and codeitemdesc like '%" + name + "%'");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                orgids += ",'" + codeitemid + "'";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return orgids;
    }
    
    /**
     * 获取导出需要的表头的合并列
     * @param fieldList 表头的列头
     * @return
     */
    public ArrayList<LazyDynaBean> getExcleMergedList(ArrayList<ColumnsInfo> fieldList) {
        ArrayList<LazyDynaBean> mergedList = new ArrayList<LazyDynaBean>();
        int num = 0;
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            ColumnsInfo columnsInfo = fieldList.get(i);
            ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if(!childColumns.isEmpty()) {
                LazyDynaBean bean = new LazyDynaBean();
                //设置合并列的起始行
                bean.set("fromRowNum", 0);
                //设置合并列的起始列
                bean.set("fromColNum", ind + num);
                //设置合并列的终止行
                bean.set("toRowNum", 0);
                //设置合并列的终止列
                bean.set("toColNum", ind + num + childColumns.size() - 1);
                //设置合并列的名称
                bean.set("content", columnsInfo.getColumnDesc());
                mergedList.add(bean);
                num = num + childColumns.size() - 1;
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                String itemid = info.getColumnId();
                if("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype()
                        || "primarykey".equalsIgnoreCase(itemid))
                    continue;
            }
            
            ind++;
        }
        
        return mergedList;

    }
    /**
     * 获取导出数据的列头
     * 
     * @param fieldList
     *            表格中显示的列
     * @param mergedList
     *            合并的列
     * @param flag
     *            是否是合并列中的子列
     * @param index
     *            从第几列开始
     * @return
     */
    public ArrayList<LazyDynaBean> getHeadList(ArrayList fieldList, ArrayList<LazyDynaBean> mergedList, boolean flag ,int index) {
        int num = 0;
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            ColumnsInfo columnsInfo = (ColumnsInfo) fieldList.get(i);
            ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if(!childColumns.isEmpty()) {
                //获取合并列中包含的指标
                headList.addAll(getHeadList(childColumns, mergedList, true, ind + num));
                num = num + childColumns.size();
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String itemid = info.getColumnId();
                if("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype()
                        || "primarykey".equalsIgnoreCase(itemid))
                    continue;
                
                bean.set("itemid", itemid);
                bean.set("content", info.getColumnDesc()+ "");
                bean.set("codesetid", info.getCodesetId());
                bean.set("colType", info.getColumnType());
                bean.set("decwidth", info.getDecimalWidth() + "");
                if(mergedList != null && mergedList.size() > 0){
                    if(flag) {
                        //设置合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 1);
                        bean.set("toRowNum", 1);
                        //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + index);
                        bean.set("toColNum", ind + index);
                    } else {
                        //设置非合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 0);
                        bean.set("toRowNum", 1);
                        //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + num);
                        bean.set("toColNum", ind + num);
                    }
                }

                headList.add(bean);
                ind++;
            }
        }
        
        return headList;

    }
    /**
     * 获取导出时查询数据的sql
     * @param selectDatas 选择的数据
     * @return
     */
    public String getExprotSql(ArrayList<String> selectDatas, ArrayList<LazyDynaBean> columnList) {
        String holidayType = "";
        HashMap<String, ArrayList<String[]>> dataMap = new HashMap<String, ArrayList<String[]>>();
        for(int i = 0; i < selectDatas.size(); i++) {
            String[] datas = new String[2];
            String selectData = selectDatas.get(i);
            String[] valueData = selectData.split(":");
            if(valueData.length < 3)
                continue;
            // 34588 优化为隐藏列primarykey隐藏列代替人员库和人员编号，不受栏目设置影响
            String primaryKey = valueData[0];
            primaryKey = PubFunc.decrypt(primaryKey);
            if(primaryKey.indexOf("|") == -1)
                continue;
            String nbase = primaryKey.split("\\|")[0];
            String a0100 = primaryKey.split("\\|")[1];
            String q1701 = valueData[1];
            // 35329 如果栏目设置隐藏假期类型，则获取失败，改为由已加密的全局变量假期类别参数
            String q1709 = PubFunc.decrypt(valueData[2]);
            
            if(StringUtils.isEmpty(holidayType))
                holidayType = q1709;
            
            datas[0] = a0100;
            datas[1] = q1701;
            
            if(dataMap.containsKey(nbase)){
                ArrayList<String[]> dataList = dataMap.get(nbase);
                dataList.add(datas);
                dataMap.put(nbase, dataList);
            } else {
                ArrayList<String[]> dataList = new ArrayList<String[]>();
                dataList.add(datas);
                dataMap.put(nbase, dataList);
            }
        }
        
        StringBuffer columns = new StringBuffer();
        StringBuffer sqlStr = new StringBuffer("select ");
        for (int i = 0; i < columnList.size(); i++) {
            LazyDynaBean column = columnList.get(i);
            sqlStr.append(column.get("itemid") + ",");
            if("jobNumber".equalsIgnoreCase((String) column.get("itemid")))
                columns.append( "a." + getGNo() + " jobNumber,");
            else
                columns.append( "q." + column.get("itemid") + ",");
                
        }
        
        sqlStr.setLength(sqlStr.length() - 1);
        columns.setLength(columns.length() - 1);
        
        sqlStr.append(" from (");
        Iterator<Entry<String, ArrayList<String[]>>> iter = dataMap.entrySet().iterator();
        int num = 0;
        while (iter.hasNext()) {
            Entry<String, ArrayList<String[]>> entry = (Entry<String, ArrayList<String[]>>) iter.next();
            String key = entry.getKey();
            ArrayList<String[]> values = entry.getValue();
            if(num > 0)
                sqlStr.append(" union all ");
            
            sqlStr.append("select " + columns + " from q17 q inner join ");
            sqlStr.append(key + "a01 a");
            sqlStr.append(" on q.a0100=a.a0100");
            sqlStr.append(" where q.q1709='" + holidayType + "'");
            sqlStr.append(" and q.nbase='" + key + "'");
            sqlStr.append(" and (");
            for (int i = 0; i < values.size(); i++){
                String[] value = values.get(i);
                if(i > 0)
                    sqlStr.append(" or");
                    
                sqlStr.append(" q.a0100='" + value[0] + "' and q.q1701='" + value[1] + "'");
            }
            
            sqlStr.append(")");
            num++;
        }
        
        sqlStr.append(") a");
        return sqlStr.toString();
    }
    /**
     * 获取导出时调休假查询数据的sql
     * @param selectDatas 选择的数据
     * @return
     */
    public String getExprotLeaveDatasSql(String sql, ArrayList<String> selectDatas, ArrayList<LazyDynaBean> columnList) {
        StringBuffer sqlStr = new StringBuffer("select * from (");
        HashMap<String, String> dataMap = new HashMap<String, String>();
        for(int i = 0; i < selectDatas.size(); i++) {
            String primaryKey = selectDatas.get(i);
            // 34588 优化为隐藏列primarykey隐藏列代替人员库和人员编号，不受栏目设置影响
            primaryKey = PubFunc.decrypt(primaryKey);
            if(primaryKey.indexOf("|") == -1)
                continue;
            String nbase = primaryKey.split("\\|")[0];
            String a0100 = primaryKey.split("\\|")[1];
            
            if(dataMap.containsKey(nbase)){
                String a0100s = dataMap.get(nbase);
                a0100s += ",'" + a0100 + "'";
                dataMap.put(nbase, a0100s);
            } else {
                dataMap.put(nbase, "'" + a0100 + "'");
            }
        }
        // 34295 linbz 选择人员时排序条件不应放在这里，SQL语句错误
        if(sql.indexOf("order") != -1)
            sql = sql.split("order")[0];
        sqlStr.append(sql);
        sqlStr.append(")b where  ("); 
        Iterator<Entry<String, String>> iter = dataMap.entrySet().iterator();
        int num = 0;
        while (iter.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) iter.next();
            String key = entry.getKey();
            String values = entry.getValue();
            if(num > 0)
                sqlStr.append(" or ");
            
            sqlStr.append("(b.nbase='" + key + "'");
            sqlStr.append(" and b.a0100 in (" + values + "))");
            num++;
        }
        
        sqlStr.append(")");
        return sqlStr.toString();
    }
    /**
     * 获取导出模板的列头
     * 
     * @param fieldList
     *            表格中显示的列
     * @param mergedList
     *            合并的列
     * @param flag
     *            是否是合并列中的子列
     * @param index
     *            从第几列开始
     * @return
     */
    public ArrayList<LazyDynaBean> getTemplateHeadList(ArrayList<ColumnsInfo> fieldList) {
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        for(int i = 0; i < fieldList.size(); i++) {
            ColumnsInfo columnsInfo = (ColumnsInfo) fieldList.get(i);
            ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if(!childColumns.isEmpty() && childColumns.size() > 0) {
                //获取合并列中包含的指标
                headList.addAll(getTemplateHeadList(childColumns));
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String itemid = info.getColumnId();
                if("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype())
                    continue;
                
                bean.set("itemid", itemid);
                bean.set("comment", itemid);
                if("q3305".equalsIgnoreCase(itemid) || "q3307".equalsIgnoreCase(itemid)
                        || "q3309".equalsIgnoreCase(itemid))
                    bean.set("content", info.getColumnDesc()+ "（小时）");
                else
                    bean.set("content", info.getColumnDesc()+ "");
                bean.set("codesetid", info.getCodesetId());
                bean.set("colType", info.getColumnType());
                bean.set("decwidth", info.getDecimalWidth() + "");
                if("primaryKey".equalsIgnoreCase(itemid))
                    bean.set("columnHidden", true);
                        
                headList.add(bean);
            }
        }
        
        return headList;

    }
    /**
     * 获取设置的工号对应的指标
     * @return
     */
    public String getGNo() {
        String gNO = "";
        HashMap<String, String> map = KqPrivBo.getKqParameter(this.conn);
        gNO = map.get("g_no");
        return gNO;
    }
    
    /**
     * 获取考勤假期管理可以设置计算公式的指标
     * @return
     */
    public ArrayList<HashMap<String,String>> getKqFormulaFields() {
        ArrayList<HashMap<String,String>> kqList = new ArrayList<HashMap<String,String>>();
        ArrayList fieldlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
        HashMap<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("NBASE", "");
        fieldMap.put("Q1701", "");
        fieldMap.put("I9999", "");
        fieldMap.put("A0100", "");
        fieldMap.put("B0110", "");
        fieldMap.put("E0122", "");
        fieldMap.put("E01A1", "");
        fieldMap.put("A0101", "");
        fieldMap.put("Q1709", "");
        
        for (int i = 0; i < fieldlist.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if(fieldMap.containsKey(fielditem.getItemid().toUpperCase()))
                continue;
            
            map.put("itemname", fielditem.getItemid());
            map.put("hzname", fielditem.getItemid().toUpperCase() + ":" + fielditem.getItemdesc());
            kqList.add(map);
        }
        
        return kqList;
    }
    /**
     * 获取假期管理计算公式的内容
     * @param codeitemid 单位代码
     * @param field 指标
     * @param hoildayType 假期类型
     * @param hoildayYear 假期年份
     * @return
     * @throws GeneralException
     */
    public String getParameter(String codeitemid, String field, String holidayType, String holidayYear)
        throws GeneralException {
        String content = "";
        try {
            if(StringUtils.isBlank(codeitemid)) {
                if (this.userView.isSuper_admin()) {
                    codeitemid = "UN";
                }else {
                    KqPrivBo bo = new KqPrivBo(userView, conn);
                    codeitemid = bo.getUNB0110();
                }
            }
            
            ContentDAO dao = new ContentDAO(this.conn);
            content = KqParam.getInstance().getContent(dao, "REST_" + field.toUpperCase() + "_" + holidayType,
                    codeitemid, "", holidayYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return content;
    }
    
    /**
     * 验证年假的长度是否》=结余
     * 
     * @return
     * @author szk 2014-5-29下午05:21:51
     * @throws GeneralException
     */
    public String checklength() throws GeneralException {
        FieldItem q17z4 = DataDictionary.getFieldItem(this.getBalance());
        FieldItem q1707 = DataDictionary.getFieldItem("q1707");
        int q1707len = q1707.getItemlength();
        int q17z4len = q17z4.getItemlength();
        int q17z4min = q17z4.getDecimalwidth();
        int q1707min = q1707.getDecimalwidth();
        if (q1707min > q17z4min)
            return "可休天数的小数位数大于上年结余的小数位数，计算时可能出现错误，请修改业务字典之后再计算！";
        
        if (q1707len > q17z4len)
            return "可休天数的长度大于上年结余的长度，计算时可能出现错误，请修改业务字典之后再计算！";
        
        return "";
    }

    /**
     * 获得没有插入Q17的人员信息
     * 
     * @param nbase
     * @param theYear
     * @param whereIN
     * @param hols_status
     * @return
     */
    public ArrayList selectFeastUser(String nbase, String theYear, String whereIN,
            String hols_status) {
        ArrayList userList = new ArrayList();
        RowSet rs = null;
        try {
            StringBuffer sel = new StringBuffer();
            sel.append(" select '");
            sel.append(nbase);
            sel.append("' nbase,a0100 from ");
            sel.append(nbase);
            sel.append("A01");
            sel.append(" WHERE NOT EXISTS(SELECT * FROM q17");
            sel.append(" where q17.a0100=");
            sel.append(nbase);
            sel.append("A01.a0100 and ");
            String q17_b0110 = Sql_switcher.isnull("q17.b0110", "'a'");
            String a01_b0110 = Sql_switcher.isnull(nbase + "A01.b0110", "'a'");
            sel.append(q17_b0110);
            sel.append("=");
            sel.append(a01_b0110);
            sel.append(" and q17.q1701='");
            sel.append(theYear);
            sel.append("' and q17.q1709='");
            sel.append(hols_status);
            sel.append("' and q17.nbase='");
            sel.append(nbase);
            sel.append("') AND a0100 in(select a0100 ");
            sel.append(whereIN);
            sel.append(")");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sel.toString());
            while (rs.next()) {
                ArrayList list = new ArrayList();
                list.add(rs.getString("nbase"));
                list.add(rs.getString("a0100"));
                list.add(rs.getString("nbase"));
                list.add(rs.getString("a0100"));
                userList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 更新上年结余为null的记录
     * 
     * @param theYear
     * @param hols_status
     * @param nbase
     * @param b0110_one
     * @param whereIN
     */
    private void updateNullBalance(String theYear, String hols_status, String nbase,
            String b0110_one, String whereIN) {
        try {
            StringBuffer sql = new StringBuffer();
            int currYear = Integer.parseInt(theYear);
            String topYear = String.valueOf(currYear - 1);
            sql.append("update q17 set ");
            sql.append(this.getBalance());
            
            // 查看结余剩余字段
            String field = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
            sql.append("=(select ");
            sql.append("  q1707 from q17 m where ");
            sql.append("m.nbase='" + nbase + "' and m.a0100=q17.a0100 and m.q1701='");
            sql.append(topYear);
            sql.append("' and m.q1709='");
            sql.append(hols_status);
            sql.append("') , " + field + "=" + this.getBalance() + "-" + field);
            
            sql.append(" where nbase='" + nbase + "' and q1701='");
            sql.append(theYear);
            sql.append("' and q1709='");
            sql.append(hols_status);
            sql.append("'");
            sql.append("and b0110='" + b0110_one + "'");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新结余剩余
     * 
     * @param theYear
     * @param hols_status
     * @param nbase
     * @param b0110_one
     * @param whereIN
     */
    private void updateNullBalanceReamain(String theYear, String hols_status, String nbase,
            String b0110_one, String whereIN) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update q17 set ");
            // 查看结余剩余字段
            String field = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
            sql.append(field);
            sql.append("=");
            sql.append(Sql_switcher.isnull(this.getBalance() + "-" + field, this.getBalance()));
            
            sql.append(" where b0110='" + b0110_one + "'");
            sql.append(" and q1701='" + theYear + "'");
            sql.append(" and nbase='" + nbase + "'");
            sql.append(" and q1709='" + hols_status + "'");
            sql.append(" and a0100 in(select a0100 " + whereIN + ")");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新上年结余截止日期
     * 
     * @param theYear
     * @param hols_status
     * @param nbase
     * @param b0110_one
     * @param whereIN
     */
    private void updateBalanceEnd(String theYear, String hols_status, String nbase,
            String b0110_one, String whereIN, String balanceEndDate) {
        try {
            StringBuffer sql = new StringBuffer();
            String field = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
            if (balanceEndDate.length() > 0 && field.length() > 0) {
                sql.append("update q17 set ");
                sql.append(field);
                sql.append(" =");
                sql.append(Sql_switcher.dateValue(balanceEndDate));
                sql.append(" where b0110='" + b0110_one + "'");
                sql.append(" and nbase='" + nbase + "'");
                sql.append(" and q1701='" + theYear + "'");
                sql.append(" and q1709='" + hols_status + "'");
                sql.append(" and a0100 in(select a0100 " + whereIN + ")");
                ContentDAO dao = new ContentDAO(this.conn);
                dao.update(sql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将上年结余更新到本年的数据中
     * 
     * @param nbase
     * @param b0110
     * @param theYear
     */
    private void updateBalance(String theYear, String hols_status, ArrayList list) {
        try {
            StringBuffer sql = new StringBuffer();
            int currYear = Integer.parseInt(theYear);
            String topYear = String.valueOf(currYear - 1);
            sql.append("update q17 set ");
            sql.append(this.getBalance());
            sql.append("=(select case when ");
            sql.append(Sql_switcher.isnull("q1707", "0"));
            sql.append(" < ");
            sql.append(Sql_switcher.isnull("q1703", "0"));
            sql.append(" then ");
            sql.append(Sql_switcher.isnull("q1707", "0"));
            sql.append(" else ");
            sql.append(Sql_switcher.isnull("q1703", "0"));
            sql.append(" end q1707 from q17 where ");
            sql.append("nbase=? and a0100=? and q1701='");
            sql.append(topYear);
            sql.append("' and q1709='");
            sql.append(hols_status);
            sql.append("') where nbase=? and a0100=? and q1701='");
            sql.append(theYear);
            sql.append("' and q1709='");
            sql.append(hols_status);
            sql.append("'");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.batchUpdate(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算
     * 
     * @param alUsedFields
     *            所用的fieldlist
     * @param nbase
     *            人员库前缀
     * @param exc_p
     *            公式
     * @param whl
     *            select 过滤语句
     */
    private void countExc_p(ArrayList alUsedFields, String nbase, String exc_field, String exc_p,
            String whl, String theYear, String hols_status) {
        if (exc_p == null || exc_p.length() <= 0)
            return;
        // forPerson 人员
        int infoGroup = 0; 
        int varType = KqUtil.getFieldType(exc_field);
        String varTypes = KqUtil.getFieldTypes(exc_field);
        YearMonthCount ycm = null;
        FieldItem exc_item = DataDictionary.getFieldItem(exc_field);
        ContentDAO dao = new ContentDAO(this.conn);
        YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType,
                infoGroup, "Ht", nbase);
        yp.setRenew_term(" q1701='" + theYear + "' and q1709='" + hols_status + "'");
        // 33241 传指标长度错误  改为exc_item.getItemlength()
        yp.run(exc_p, ycm, exc_field, "q17", dao, whl, this.conn, varTypes, exc_item.getItemlength(), exc_item.getDecimalwidth(), 2, null);
    }

    /**
     * 获得上年结余的字段名称
     * 
     * @return
     */
    public String getBalance() {
        // 获得年假结余的列名
        String balance = "";

        ArrayList fieldList = DataDictionary.getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("上年结余".equalsIgnoreCase(item.getItemdesc()))
                balance = item.getItemid();
        }

        return balance;
    }

    /**
     * 删除不存在的用户
     * 
     * @param nbase
     *            人员库
     * @param b0110
     *            单位编码
     * @param theYear
     *            假期年份
     * @param countStart
     *            假期计算开始时间
     * @param countEnd
     *            假期计算结束时间
     * @param whereIN
     *            关于权限的sql
     * @throws GeneralException
     */
    private void deleteNoFeastUser(String nbase, String b0110, String theYear, String hols_status)
            throws GeneralException {

        try {
            StringBuffer delete = new StringBuffer();
            delete.append("DELETE FROM q17 WHERE NOT A0100 IN (SELECT A0100 FROM " + nbase
                    + "A01 where b0110='" + b0110 + "')");
            delete.append(" AND q17.q1701='" + theYear + "'");
            delete.append(" AND q17.nbase='" + nbase + "'");
            delete.append(" and q17.q1709='" + hols_status + "'");
            delete.append(" AND b0110='" + b0110 + "'");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(delete.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除数据
     * 
     * @param userbase
     *            人员库
     * @param b0110
     *            单位代码
     * @param whereIN
     *            权限的sql
     * @param year
     *            假期年份
     * @param hols_status
     *            假期类型
     * @return
     */
    private boolean deleteData(String userbase, String b0110, String whereIN, String year,
            String hols_status) {
        boolean isCollect = false;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("delete from Q17");
            strsql.append(" where nbase=?");
            strsql.append(" and b0110=?");
            strsql.append(" and q1701=?");
            strsql.append(" and q1709=?");
            strsql.append(" and a0100 in(select a0100 " + whereIN + ")");
            ArrayList<String> deletelist = new ArrayList<String>();
            deletelist.add(userbase);
            deletelist.add(b0110);
            deletelist.add(year);
            deletelist.add(hols_status);
            ContentDAO dao = new ContentDAO(this.conn);
            dao.delete(strsql.toString(), deletelist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCollect;
    }

    /**
     * 更新可休天数
     * 
     * @param nbase
     *            人员库
     * @param b0110
     *            单位代码
     * @param theYear
     *            假期年分
     * @param whereIN
     *            关于权限的sql
     * @param hols_status
     *            假期类型
     */
    private void updateData(String nbase, String b0110, String theYear, String whereIN,
            String hols_status) {
        try {
            StringBuffer update = new StringBuffer();
            update.append("update q17 set");
            update.append(" q1707=" + Sql_switcher.isnull("q1703", "0") +"-" + Sql_switcher.isnull("q1705", "0"));
            update.append(" where b0110='" + b0110 + "'");
            update.append(" and q1701='" + theYear + "'");
            update.append(" and nbase='" + nbase + "'");
            update.append(" and q1709='" + hols_status + "'");
            update.append(" and a0100 in(select a0100 " + whereIN + ")");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(update.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加不存在的用户
     * 
     * @param nbase
     *            人员库
     * @param b0110
     *            单位编码
     * @param holidayYear
     *            假期年份
     * @param countStart
     *            假期计算开始时间
     * @param countEnd
     *            假期计算结束时间
     * @param whereIN
     *            关于权限的sql
     * @throws GeneralException
     */
    private void insertFeastUser(String nbase, String holidayYear, String countStart,
            String countEnd, String whereIN, String holidayType, String[] fieldDatas,
            String balanceValue) throws GeneralException {
        try {
            StringBuffer insert = new StringBuffer();
            synchronizationInit(nbase, holidayYear, whereIN, holidayType, countStart, countEnd,
                    fieldDatas);
            String char_to_date_start = Sql_switcher.dateValue(countStart);
            String char_to_date_end = Sql_switcher.dateValue(countEnd);
            insert.append("INSERT INTO q17 (nbase,A0100,Q1701,B0110,E0122,E01A1,A0101,");
            insert.append("Q1703,");// 年假天数
            insert.append("Q17Z1,");// 年假开始
            insert.append("Q17Z3,");// 年假结束
            insert.append("Q1705,");// 已休天数
            insert.append("Q1707,");// 可休天数
            // 上年结余
            if ("1".equals(balanceValue)) {
                HolidayBo bo = new HolidayBo(this.conn, this.userView);
                insert.append(bo.getBalance());
                insert.append(",");
            }

            insert.append("Q1709) ");
            insert.append(" select '" + nbase + "',a0100,'" + holidayYear + "',");
            insert.append(" B0110,E0122,E01A1,A0101,0,");
            insert.append("" + char_to_date_start + ",");
            insert.append("" + char_to_date_end + ",");
            // 上年结余
            if ("1".equals(balanceValue))
                insert.append("0,0,0,'" + holidayType + "' from " + nbase + "A01");
            else
                insert.append("0,0,'" + holidayType + "' from " + nbase + "A01");

            insert.append(" WHERE NOT EXISTS(SELECT * FROM q17");
            insert.append(" where q17.a0100=" + nbase + "A01.a0100");
            String q17_b0110 = Sql_switcher.isnull("q17.b0110", "'a'");
            String a01_b0110 = Sql_switcher.isnull(nbase + "A01.b0110", "'a'");
            insert.append(" and " + q17_b0110 + "=" + a01_b0110 + "");
            insert.append(" and q17.q1701='" + holidayYear + "'");
            insert.append(" and q17.q1709='" + holidayType + "'");
            insert.append(" and q17.nbase='" + nbase + "')");
            insert.append(" AND a0100 in(select a0100 " + whereIN + ")");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.insert(insert.toString(), new ArrayList<String>());
        } catch (Exception e) {
            e = new GeneralException("", ResourceFactory.getProperty("kq.error.insert.emp"), "", "");
            e.printStackTrace();
        }
    }

    /**
     * 更新q17表中的数据
     * 
     * @param nbase
     *            人员库
     * @param holidayYear
     *            假期年份
     * @param whereIN
     *            关于权限的sql
     * @param holidayType
     *            假期类型
     * @param countStart
     *            假期计算开始时间
     * @param countEnd
     *            假期计算结束时间
     * @param fieldDatas
     *            更新数据的指标
     * @throws GeneralException
     */
    public void synchronizationInit(String nbase, String holidayYear, String whereIN,
            String holidayType, String countStart, String countEnd, String[] fieldDatas)
            throws GeneralException {
        try {
            String char_to_date_start = Sql_switcher.dateValue(countStart);
            String char_to_date_end = Sql_switcher.dateValue(countEnd);
            // 目标表
            String destTab = "q17";
            // 源表
            String srcTab = nbase + "A01";
            // 关联串
            String strJoin = "Q17.A0100=" + srcTab + ".A0100";
            // xxx.field_name=yyyy.field_namex,....
            String start = "";
            String end = "";
            String q17z1 = null;
            String q17z3 = null;
            // 起始时间,结束时间...
            for (int i = 0; i < fieldDatas.length; i++) {
                if ("q17z1".equalsIgnoreCase(fieldDatas[i]))
                    q17z1 = fieldDatas[i];
                else if ("q17z3".equalsIgnoreCase(fieldDatas[i]))
                    q17z3 = fieldDatas[i];
            }

            if (!"q17z1".equalsIgnoreCase(q17z1))
                start = "`Q17.Q17Z1=" + char_to_date_start;

            if (!"q17z3".equalsIgnoreCase(q17z3))
                end = "`Q17.Q17Z3=" + char_to_date_end;
            // 更新串
            String strSet = "Q17.B0110=" + srcTab + ".B0110`Q17.E0122=" + srcTab
                    + ".E0122`Q17.E01A1=" + srcTab + ".E01A1`Q17.A0101=" + srcTab + ".A0101"
                    + start + end;
            // xxx.field_name=yyyy.field_namex,....
            // 更新目标的表过滤条件
            String strDWhere = "q17.q1701='" + holidayYear + "' and q17.nbase='" + nbase
                    + "' and q17.q1709='" + holidayType + "'";
            // 源表的过滤条件
            String strSWhere = srcTab + ".a0100 in (select a0100 " + whereIN + ")";
            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet,
                    strDWhere, strSWhere);
            String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(update);
        } catch (Exception e) {
            e = new GeneralException("", ResourceFactory.getProperty("kq.error.data.synchronization"), "", "");
            e.printStackTrace();
        }
    }

    /**
     * 计算考勤假期
     * 
     * @param map
     *            包含： 
     *            fieldData 计算数据的指标 
     *            nbase 人员库 
     *            holidayType 假期类型 
     *            holidayYear 假期年份 
     *            countStart 假期计算的开始时间 
     *            countEnd 假期计算的结束时间 
     *            balanceValue 是否结余
     *            balanceEndDate 最后结余日期 
     *            strsql 更新数据的sql 
     *            clearZone 不享有此假期的人是否生成记录
     *          
     *  后台作业年假计算也调用该方法；
     */
    public void calCulateHoliday(HashMap<String, String> map) {
        String fieldData = map.get("fieldData");
        String nbase = map.get("nbase");
        String holidayType = map.get("holidayType");
        String holidayYear = map.get("holidayYear");
        String countStart = map.get("countStart");
        String countEnd = map.get("countEnd");
        String balanceValue = map.get("balanceValue");
        String balanceEndDate = map.get("balanceEndDate");
        String clearZone = map.get("clearZone");
        try {
            String[] fieldDatas = fieldData.split(",");
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,
                    Constant.ALL_FIELD_SET);

            String exc_p = "";
            
            ArrayList<String> dblist = new ArrayList<String>();
            if (StringUtils.isEmpty(nbase) || "All".equalsIgnoreCase(nbase)) {                
                dblist = KqPrivBo.getB0110Dase(this.userView, this.conn);
            } else
                dblist.add(nbase);
            
            for (int i = 0; i < dblist.size(); i++) {
                String userbase = dblist.get(i).toString();
                String whereIN = KqPrivBo.getWhereINSql(userView, userbase);
                String whereB0110 = KqPrivBo.selcetB0100OrgId(userbase, null, "b0110", whereIN);
                ArrayList orgidb0110List = KqPrivBo.getQrgE0122List(this.conn, whereB0110, "b0110");
                for (int t = 0; t < orgidb0110List.size(); t++) {
                    String b0110_one = orgidb0110List.get(t).toString();
                    /******** 按照该单位的人员库的操作 *********/
                    if (StringUtils.isNotEmpty(userbase)) {
                        for (int s = 0; s < fieldDatas.length; s++) {
                            String exp_field = fieldDatas[s];
                            if (StringUtils.isEmpty(exp_field)) 
                                continue;
                            // 51284 公式前缀固定是UN
                            exc_p = this.getParameter("UN"+b0110_one, exp_field, holidayType, holidayYear); 
                            if (StringUtils.isEmpty(exc_p))
                                continue;

                            ArrayList userList = this.selectFeastUser(userbase, holidayYear, whereIN, holidayType);
                            insertFeastUser(userbase, holidayYear, countStart, countEnd, whereIN, holidayType, fieldDatas, balanceValue);
                            if ("1".equals(balanceValue))
                                // 将上年结余天数更新到今年的记录中
                                this.updateBalance(holidayYear, holidayType, userList);

                            this.deleteNoFeastUser(userbase, b0110_one, holidayYear, holidayType);

                            StringBuffer whl = new StringBuffer();
                            whl.append("select a0100 from q17");
                            whl.append(" where b0110='" + b0110_one + "'");
                            whl.append(" and nbase='" + userbase + "'");
                            whl.append(" and q1701='" + holidayYear + "'");
                            whl.append(" and q1709='" + holidayType + "'");
                            whl.append(" and a0100 in(select a0100 " + whereIN + ")");
                            this.countExc_p(alUsedFields, userbase, exp_field, exc_p, whl.toString(), holidayYear, holidayType);
                            this.updateData(userbase, b0110_one, holidayYear, whereIN, holidayType);
                        }
                        // 把上年结余为null的全部重新计算出来
                        if ("1".equals(balanceValue)) {
                            // 更新结余截止时间
                            this.updateBalanceEnd(holidayYear, holidayType, userbase, b0110_one, whereIN, balanceEndDate);
                            // szk将上年结余天数更新到今年的记录中并计算结余中已申请的天数暂时存到q17z6
                            this.updateNullBalance(holidayYear, holidayType, userbase, b0110_one, whereIN);
                            // 更新结余剩余天数
                            this.updateNullBalanceReamain(holidayYear, holidayType, userbase, b0110_one, whereIN);
                            // 更新可用天数
                            this.updateData(userbase, b0110_one, holidayYear, whereIN, holidayType);
                        }

                    } else
                        /******** 防止改变考勤人员库参数 ***********/
                        this.deleteData(userbase, b0110_one, whereIN, holidayYear, holidayType);
                }
            }

            if ("1".equals(clearZone)) {
                //
                StringBuilder dSql = new StringBuilder();
                dSql.append("delete from q17");
                dSql.append(" where q1701='").append(holidayYear).append("'");
                dSql.append(" and q1709='").append(holidayType).append("'");
                //假期天数为0
                dSql.append(" and ").append(Sql_switcher.isnull("q1703", "0")).append("=0");
                //结余天数为0
                if (StringUtils.isNotBlank(this.getBalance()))
                    dSql.append(" and ").append(Sql_switcher.isnull(this.getBalance(), "0")).append("=0");
                
                ContentDAO dao = new ContentDAO(this.conn);
                dao.update(dSql.toString());

                String updateStart = "update q17 set q17.q17z1=" + Sql_switcher.charToDate("'"+countStart+"'");
                String updateEnd = "update q17 set q17.q17z3=" + Sql_switcher.charToDate("'"+countEnd+"'");
                KqPrivBo bo = new KqPrivBo(this.userView, this.conn);
                String code = bo.getPrivCode();
                for (int j = 0; j < dblist.size(); j++) {
                    String dbname = dblist.get(j);
                    StringBuffer where = new StringBuffer();
                    where.append(" where q17.nbase='" + dbname + "'");
                    where.append(" and q17.b0110 like '" + code + "%'");
                    where.append(" and q17.q1701='" + holidayYear + "'");
                    where.append(" and q17.q1709='" + holidayType + "'");
                    where.append(" and q17.a0100 in (select a0100 from " + dbname + "A01 )");
                    String updateStarts = updateStart + where.toString()
                            + " and Q17.q17z1 is null";
                    String updateEnds = updateEnd + where
                            + " and Q17.q17z3 is null";
                    dao.update(updateStarts);
                    dao.update(updateEnds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取假期明细显示的列
     * @return
     */
    public static ArrayList<ColumnsInfo> getColumnList() {

        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        try{
            
            HashMap<String, String> fieldMap = new HashMap<String, String>();
            fieldMap.put("Q15Z1", "");
            fieldMap.put("Q15Z3", "");
            fieldMap.put("Q1507", "");
            fieldMap.put("Q1519", "");
            
            ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("Q15", Constant.USED_FIELD_SET);
            for (int i = 0; i < fieldItemList.size(); i++) {
                FieldItem fi = fieldItemList.get(i);
                if(!fieldMap.containsKey(fi.getItemid().toUpperCase()))
                    continue;
                
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(fi.getItemid());
                if("Q1519".equalsIgnoreCase(fi.getItemid()))
                    info.setColumnDesc("请假/销假");
                else    
                    info.setColumnDesc(fi.getItemdesc());
                
                info.setColumnType(fi.getItemtype());
                
                if("Q15Z1".equalsIgnoreCase(fi.getItemid().toUpperCase()) || "Q15Z3".equalsIgnoreCase(fi.getItemid().toUpperCase()))
                    info.setColumnWidth(120);
                else if("Q1507".equalsIgnoreCase(fi.getItemid().toUpperCase()))
                    info.setColumnWidth(268);
                else if("Q1519".equalsIgnoreCase(fi.getItemid().toUpperCase())) {
                    info.setColumnWidth(80);
                    info.setTextAlign("center");
                }
                
                info.setFieldsetid(fi.getFieldsetid());
                info.setColumnLength(fi.getItemlength());
                info.setSortable(true);
                info.setCodesetId(fi.getCodesetid());
                info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                
                if ("N".equalsIgnoreCase(fi.getItemtype())) {
                    info.setTextAlign("right");
                    info.setDecimalWidth(fi.getDecimalwidth());
                }
                columnList.add(info);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return columnList;
    }
    
    /**
     * 获取调休假明细显示的列
     * @param unit  时长的小数位数
     * @return
     */
    public static ArrayList<ColumnsInfo> getOverTimeColumnList(int decimalwidth) {

        HashMap<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("Q3303", "");
        fieldMap.put("Q3305", "");
        fieldMap.put("Q3307", "");
        fieldMap.put("Q3309", "");
        
        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("Q33", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldItemList.size(); i++) {
            FieldItem fi = fieldItemList.get(i);
            if(!fieldMap.containsKey(fi.getItemid().toUpperCase()))
                continue;
            
            ColumnsInfo info = new ColumnsInfo();
            info.setColumnId(fi.getItemid());
            info.setColumnDesc(fi.getItemdesc());
            info.setColumnType(fi.getItemtype());
            info.setColumnWidth(147);
            info.setFieldsetid(fi.getFieldsetid());
            info.setColumnLength(fi.getItemlength());
            info.setSortable(true);
            info.setCodesetId(fi.getCodesetid());
            info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            
            if ("N".equalsIgnoreCase(fi.getItemtype())) {
                info.setTextAlign("right");
                info.setDecimalWidth(fi.getDecimalwidth());
            }
            // 单位小时 显示unit位小数
            if ("q3305".equals(fi.getItemid()) || "q3307".equals(fi.getItemid()) || "q3309".equals(fi.getItemid())) {
                info.setDecimalWidth(decimalwidth);
                info.setColumnId(fi.getItemid() + "F");
            }
            
            columnList.add(info);
        }
            
        return columnList;
    
    }
    
    public static String getMapTypeIdsFromHolidayMap(String holidayId) {
        String mapTypeIds = "'" + holidayId + "'";
        
        String holidayMaps = SystemConfig.getPropertyValue("kq_holiday_map");
        holidayMaps = null == holidayMaps ? "" : holidayMaps.trim();
        if ("".equals(holidayMaps))
            return mapTypeIds;
        
        String[] holidayMap = holidayMaps.split(";");
        for (int i=0; i<holidayMap.length; i++) {
            String aMap = holidayMap[i].trim();
            
            if ("".equals(aMap))
                continue;
            
            if (!aMap.startsWith("[") || !aMap.endsWith("]") || !aMap.contains(":"))
                continue;
            
            int pos = aMap.indexOf(":");
            String targetHolidayId = aMap.substring(1, pos);
            if (!holidayId.equals(targetHolidayId))
                continue;
            
            String srcHolidayIds = aMap.substring(pos+1, aMap.length()-1);
            if ("".equals(srcHolidayIds))
                continue;
            
            String[] srcIds = srcHolidayIds.split(",");
            for (int j=0; j<srcIds.length; j++) {
                String aSrcId = srcIds[j].trim();
                if ("".equals(aSrcId))
                    continue;
                
                mapTypeIds = mapTypeIds + ",'" + aSrcId + "'"; 
            }
                
            break;
        }    
        
        return mapTypeIds;
    }
    /**
     * 获取调休假的假期类型
     * @return
     */
    public String getLeaveTimeTypeUsedOverTime() {
        this.leaveTimeTypeUsedOverTime = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
        if(StringUtils.isEmpty(this.leaveTimeTypeUsedOverTime))
            this.leaveTimeTypeUsedOverTime = "";
        
        return leaveTimeTypeUsedOverTime;
    }
    /**
     * 获取调休假的有效时间范围
     * @return
     */
    public String getLeaveActiveTime() {
        return leaveActiveTime;
    }
    /**
     * 获取年假是否显示导入按钮
     * @param holidayYear
     * @return
     */
    public boolean checkDuration(String holidayYear) {
        boolean flag = true;
        RowSet rs = null;
        try {
            String sql = "SELECT finished FROM kq_duration where kq_year=? order by kq_duration desc";
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<String> value = new ArrayList<String>();
            value.add(holidayYear);
            rs = dao.search(sql, value);
            if(rs.next()) {
                if("1".equalsIgnoreCase(rs.getString("finished")))
                    flag = false;
            }
                
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return flag;
    }
    /**
     * 获取调休假是否显示导入按钮
     * @param date  所选的有效范围日期
     * @return
     */
    public boolean checkImportLeaveFlag(String date) {
        boolean flag = false;
        
        KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.conn, this.userView);
        Date adate = null;
        // 传 年月日 或 只传年份时特殊处理
        if(date.length() == 10)
            adate = DateUtils.getDate(date, "yyyy-MM-dd");
        else if(date.length() == 4)
            adate = DateUtils.getDate(date+"-01-01", "yyyy-MM-dd");
        
        HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod(adate);
        Date from = DateUtils.getDate((String)period.get("from"), "yyyy-MM-dd");
        Date to = DateUtils.getDate((String)period.get("to"), "yyyy-MM-dd");
        // 调休假导入规则：当前考勤期间与当前调休有效范围不符的 不显示导入按钮
        ArrayList list = RegisterDate.getKqDayList(this.conn);
        Date fromKqDate = DateUtils.getDate((String) list.get(0), "yyyy.MM.dd");
        Date toKqDate = DateUtils.getDate((String) list.get(1), "yyyy.MM.dd");
        
        if(!fromKqDate.before(from) && !fromKqDate.after(to)
                && !toKqDate.before(from) && !toKqDate.after(to))
            flag = true;
        
        return flag;
    }
    /**
     * 获取配置的高级花名册
     * @return
     */
    public String getKqMuster() {
        StringBuffer musterJson = new StringBuffer("[");
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select tabid,cname from Muster_Name");
            sql.append(" where nPrint=17 and SortId in (");
            sql.append("select sortid from Muster_Sort where nmodule='81')");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String tabId = rs.getString("tabid");
                // 暂时取消花名册校验
//              if(!this.userView.isSuper_admin() && this.userView.isHaveResource(IResourceConstant.MUSTER, tabId))
//                  continue;
                // 34325  要是该用户没有高级花名册权限的也不显示
                if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, tabId))
                    continue;
                
                musterJson.append("{text:'" + rs.getString("cname") + "',id:'" + tabId + "', handler:holidayManage.showMuster},");
            }
            
            if(musterJson.toString().endsWith(","))
                musterJson.setLength(musterJson.length() - 1);
            
            musterJson.append("]");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return musterJson.toString();
    }
    /**
     * 获取代码型 数据  下拉列表数据集合
     * 
     * @param fieldCodeSetId 
     * @return desclist 下拉列表数据集合
     */
    public ArrayList<String> getCodeByDesc(String fieldCodeSetId) throws GeneralException{
        ArrayList<String> desclist = new ArrayList<String>();
        String tableName = "";
        if("UN".equalsIgnoreCase(fieldCodeSetId) 
                || "UM".equalsIgnoreCase(fieldCodeSetId)
                ||"@K".equalsIgnoreCase(fieldCodeSetId))
            tableName = "organization";
        else
            tableName = "codeitem";
        StringBuffer sql = new StringBuffer("");
        sql.append("select codeitemdesc from ").append(tableName);
        sql.append(" where codesetid='").append(fieldCodeSetId).append("' ");
        sql.append(" and ").append(Sql_switcher.isnull("invalid", "1")).append("='1'");
        // 人员库特殊处理
        if("@@".equalsIgnoreCase(fieldCodeSetId)){
            sql.setLength(0);
            // 34284 增加考勤人员库校验
            ArrayList<String> dblist = KqPrivBo.getB0110Dase(this.userView, this.conn);
            String dbWhere = "";
            for(int i=0;i<dblist.size();i++) {
                if(i>0)
                    dbWhere += ",";
                dbWhere += "'"+dblist.get(i)+"'";
            }
            sql.append("select DBName codeitemdesc from DBName");
            if(StringUtils.isNotEmpty(dbWhere))
                sql.append(" where pre in(").append(dbWhere).append(")");
        }
                
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs=dao.search(sql.toString());
            while(rs.next()){
                String codeitemdesc=rs.getString("codeitemdesc");
                desclist.add(codeitemdesc);
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return desclist;
    }
    /**
     * 获取高级花名册查询SQL
     * @param code
     * @param kind
     * @param q1701
     * @param hols_status
     * @param whereSelectIN
     * @return
     * @throws GeneralException
     */
    public String getCondition(String code, String kind, String q1701, String hols_status, String whereSelectIN, HashMap formHM) throws GeneralException {
        ArrayList dblist = getDbList(code, kind, formHM);
        StringBuffer condition = new StringBuffer();
        String select_where = whereSelectIN;
        if (code != null | code.length() <= 0) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        }
        if ("1".equals(kind)) {
            condition.append("e0122 like '" + code + "%'");
        } else if ("0".equals(kind)) {
            condition.append("e01a1 like '" + code + "%'");
        } else {
            condition.append("b0110 like '" + code + "%'");
        }
        condition.append(" and q1701='" + q1701 + "'");
        condition.append(" and q1709='" + hols_status + "'");
        if (StringUtils.isNotEmpty(select_where))
            condition.append(" " + select_where);
        String isWhere = RegisterInitInfoData.getPrvListWhere(dblist, this.userView);
        if (isWhere != null && isWhere.length() > 0) {
            condition.append(" " + isWhere);
        }
        return condition.toString();
    }
    
    /**
     * 取登录考勤用户库的列表
     * 
     * @return
     * @throws Exception
     */
    private ArrayList getDbList(String code, String kind, HashMap formHM) throws GeneralException {
        String b0110 = code;
        String codesetid = "";
        if ("1".equals(kind) || "0".equals(kind)) {
            codesetid = code;
            do {
                String[] codeset = getB0100(b0110);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
            } while (!"UN".equals(codesetid));
        }
        ArrayList dblist = RegisterInitInfoData.getB0110Dase(formHM, this.userView, conn, b0110);

        return dblist;
    }
    
    public String[] getB0100(String codeitemid) throws GeneralException {
        String[] codeset = new String[2];
        String parentid = "";
        RowSet rs = null;
        try {
            String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + codeitemid + "'";
            ContentDAO dao = new ContentDAO(conn);

            rs = dao.search(orgSql);
            if (rs.next()) {
                parentid = rs.getString("parentid");
                if (parentid.equals(codeitemid)) {
                    codeset[0] = "UN";
                    codeset[1] = parentid;
                } else {
                    orgSql = "SELECT parentid,codesetid from organization where codeitemid='" + parentid + "'";
                    rs = dao.search(orgSql);
                    if (rs.next()) {
                        codeset[0] = rs.getString("codesetid");
                        codeset[1] = parentid;
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        
        return codeset;
    }
    
    /**
     * 根据code，获得组织机构的codeset
     * 
     * @param code
     * @return
     */
    public String getCodeSetByCode(String code) {
        String codeSet = "";
        String sql = "select codesetid from organization where codeitemid=?";
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            ArrayList params = new ArrayList();
            params.add(code);
            rs = dao.search(sql, params);
            if (rs.next()) {
                codeSet = rs.getString("codesetid");
                if (codeSet == null) {
                    codeSet = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }

        return codeSet;
    }
    /**
     * 导入数据-下载模板-获取固定列头
     * @param flag =Q17（假期管理） ; =Q33（调休假）
     * @return
     */
    public ArrayList<ColumnsInfo> getExportTemplate(String flag){
        
        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>(); 
        
        ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList(flag, Constant.USED_FIELD_SET);
        
        for (int i = 0; i < fieldItemList.size(); i++) {
            FieldItem fi = fieldItemList.get(i);
            if(null == fi)
                continue;
            String itemid = fi.getItemid();
            // 隐藏指标，排序用到的b0110,e0122,e01a1,a0100例外
            if ("Q17".equalsIgnoreCase(flag)
                    && "0".equals(fi.getState()) && !",nbase,b0110,e0122,e01a1,a0100,q1709,".contains("," + itemid + ","))
                continue;
            
            if ("Q33".equalsIgnoreCase(flag) 
                    && !"1".equals(fi.getState()) 
                    && !",nbase,b0110,e0122,e01a1,a0101,Q3303,Q3305,Q3307,Q3309,".contains("," + itemid + ","))
                continue;
            
            ColumnsInfo info = this.getColumnsInfo(fi);
            columnsList.add(info);
            
            //把工号指标固定插入到a0100之后
            if("a0100".equalsIgnoreCase(itemid)) {
                ColumnsInfo kqNumberInfo = new ColumnsInfo();
                kqNumberInfo.setColumnId("jobNumber");
                kqNumberInfo.setColumnDesc("工号");
                kqNumberInfo.setColumnType("A");
                kqNumberInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                kqNumberInfo.setColumnWidth(100);
                kqNumberInfo.setFieldsetid("");
                kqNumberInfo.setSortable(true);
                kqNumberInfo.setEditableValidFunc("false");
                columnsList.add(kqNumberInfo);
            }
        }
        
        return columnsList;
    }
    /**
     * 获取模板列头对象
     * @param fi
     * @return
     */
    private ColumnsInfo getColumnsInfo (FieldItem fi) {
        
        ColumnsInfo column = new ColumnsInfo();
        column.setColumnDesc(fi.getItemdesc());
        column.setColumnId(fi.getItemid());
        column.setColumnLength(fi.getItemlength());
        column.setColumnType(fi.getItemtype());
        column.setCodesetId(fi.getCodesetid());
        column.setColumnWidth(100);
        column.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
        if ("N".equalsIgnoreCase(fi.getItemtype())) {
            column.setDecimalWidth(fi.getDecimalwidth());
        }
        return column;
    }
    /**
     * 获取调休指标的单位及小数位长度等
     * @return
     */
    public HashMap getLeavetimeUnit() {
        
        HashMap leavetimeUnitMap = new HashMap();
        try {
            int unit = 1;
            String itemUnit = "01";
            String itemId = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
            KqItem kqItem = new KqItem(this.conn);
            HashMap<String, HashMap<String, String>> kqItemsMap = kqItem.getKqItem();
            if(kqItemsMap != null && kqItemsMap.size() > 0) {
                HashMap<String, String> kqItemMap = kqItemsMap.get(itemId);
                if(kqItemMap != null && kqItemMap.size() > 0) {
                    String fielditemid = kqItemMap.get("fielditemid");
                    // 默认一个小数位
                    if (StringUtils.isNotEmpty(fielditemid)) {
                        FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
                        unit = fieldItem.getDecimalwidth();
                    }
                    
                    itemUnit = (String)kqItemMap.get("item_unit");
                    itemUnit = StringUtils.isEmpty(itemUnit) ? "01" : itemUnit;
                }
            }
            
            leavetimeUnitMap.put("decimalwidth", unit);
            leavetimeUnitMap.put("item_unit", itemUnit);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leavetimeUnitMap;
    }
    
}
