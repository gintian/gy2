package com.hjsj.hrms.transaction.mobileapp.kq.holiday;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.transaction.mobileapp.kq.util.KqParamForApp;
import com.hjsj.hrms.transaction.mobileapp.utils.FieldTypeSwitch;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * <p>Title: HolidayBo </p>
 * <p>Description: 假期Bo</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-15 上午10:48:06</p>
 * @author tiany
 * @version 1.0
 */
public class HolidayBo {
    private Connection    conn     = null;
    private UserView      userView = null;
    private KqParamForApp kqParam  = null;

    public HolidayBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.kqParam = new KqParamForApp();
    }
    
    public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}

    /**
     * @throws SQLException 
     * @throws GeneralException 
     * 
     * @Title: searchMyHoliday   
     * @Description: 根据年份，人员编号，人员库查询假期情况   
     * @param @param year
     * @param @param a0100
     * @param @param nbase
     * @param @return 
     * @return List    
     * @throws
     */
    public List searchMyHoliday(String year, String a0100, String nbase, String b0110, List holidayTypes)
            throws GeneralException, SQLException {
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowset = null;
        try {
            //类似{06,年假}
            HashMap holidayTypesMap = getHolidayTypesMap(b0110);
            //类似{Q1703,Q1703对FieldItem(假期天数)}
            HashMap fieldItem = new HashMap();
            StringBuffer sql = getSql(year, a0100, nbase, b0110, fieldItem);
            //组装数据 //如 {年假，{ {假期天数，15天}，{可休天数，4天} }}
            HashMap holidayTypeTempMap = new HashMap();
            List holidayTypesData = new ArrayList();//存放所有假期类型的数据
            rowset = dao.search(sql.toString());
            while (rowset.next()) {
                String holidayType = (String) holidayTypesMap.get(rowset.getString("q1709"));
                //一种假期类型对应的若干指标信息
                List oneHolidayTypeData = null;
                //不存在该假期类型的数据
                if (!holidayTypeTempMap.containsKey(holidayType)) {
                    oneHolidayTypeData = new ArrayList();
                    holidayTypeTempMap.put(holidayType, oneHolidayTypeData);
                } else {
                    oneHolidayTypeData = (ArrayList) holidayTypeTempMap.get(holidayType);
                }
                
                int columnCount = rowset.getMetaData().getColumnCount();
                //遍历每个字段
                for (int i = 2; i < columnCount; i++) {
                    //字段名
                    String columnName = rowset.getMetaData().getColumnName(i).toLowerCase();
                    HashMap holidayTypeFieldValue = new HashMap();
                    /*根据指标字段获得指标描述*/
                    FieldItem oneFieldItem = (FieldItem)fieldItem.get(columnName);
                    holidayTypeFieldValue.put("fieldItem", oneFieldItem.getItemdesc());
                    /*根据指标获得值*/
                    Object itemValue = rowset.getObject(columnName);
                    if (null != itemValue){
                        String fieldItemValue = FieldTypeSwitch.getValueByFieldType(rowset, rowset.getMetaData(), i,oneFieldItem);
                        holidayTypeFieldValue.put("fieldItemValue", fieldItemValue);
                    }
                    else
                        holidayTypeFieldValue.put("fieldItemValue", "");
                    
                    oneHolidayTypeData.add(holidayTypeFieldValue);
                }
            }

            Iterator it = holidayTypeTempMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Entry) it.next();

                String holidayType = (String) entry.getKey();
                HashMap holidayTypeMap = new HashMap();
                holidayTypeMap.put("holidayType", holidayType);
                holidayTypes.add(holidayTypeMap);
                holidayTypesData.add((List) entry.getValue());
            }
            
            return holidayTypesData;
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
            
        } finally {
            if (null != rowset) {
                rowset.close();
            }
        }
    }

    /**
     * @throws GeneralException 
     * @Title: getMyHolidayYears   
     * @Description: 取得某人年假年度列表   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return List    
     * @throws
     */
    public List getMyHolidayYears(String nbase, String a0100) throws GeneralException {
        ArrayList years = new ArrayList();
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT q1701 FROM q17");
        sql.append(" WHERE nbase='" + nbase);
        sql.append("' AND a0100='" + a0100 + "'");
        sql.append(" ORDER BY q1701 DESC");
        
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                years.add(rs.getString("q1701"));                
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqParamForApp.closeRowSet(rs);
        }
        
        return years;
    }
    /**
     * @throws GeneralException 
     * @Title: getMyOvertimeForLeaveYears   
     * @Description: 取得某人调休年份列表   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return List    
     * @throws
     */
    public List getMyOvertimeForLeaveYears(String nbase, String a0100) throws GeneralException {
        ArrayList years = new ArrayList();
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT "+Sql_switcher.substr("q3303", "0", "5")+" q3303 FROM Q33");
        sql.append(" WHERE nbase='" + nbase);
        sql.append("' AND a0100='" + a0100 + "'");
        sql.append(" ORDER BY q3303 DESC");
        
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String year = rs.getString("q3303");
                if(year!=null){
                    years.add(year);
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqParamForApp.closeRowSet(rs);
        }
        
        return years;
    }
    
    /**
     * @throws GeneralException 
     * @Title: searchMyOvertimeForLeave   
     * @Description: 查询个人调休加班记录  
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return List    
     * @throws
     */
    public List searchMyOvertimeForLeave(String year, String nbase, String a0100,String pageIndex,String pageSize) throws GeneralException {
        ArrayList transferHolidayList = new ArrayList();
        String table="Q33";//Q33 调休加班明细表
        HashMap fieldItemMap = new HashMap();//存放Q33指标map<指标编码：q3303,fieldItem对象>
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            ArrayList fieldlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (!("b0110".equals(field.getItemid()) //过滤单位，姓名，隐藏，代码类型，岗位，部门
                        || "a0101".equals(field.getItemid())
                        || !field.isVisible()
                        || !"0".equals(field.getCodesetid())
                        || "e0122".equals(field.getItemid())
                        || field.getItemid().equals(
                        table.toLowerCase() + "01"))) {
                    fieldItemMap.put(field.getItemid(), field);
                }
            }
            //获取小数点位数
            int preci=0;
            String leaveUsedOvertime =kqParam.getContent(dao, "LEAVETIME_TYPE_USED_OVERTIME", "UN", "");//获得调休假 对应的假期类型
            HashMap kqItemMap = getKqItemById(leaveUsedOvertime); //获得考勤类型对应的考勤规则对象map
            String fielditemidStr = (String) kqItemMap.get("fielditemid");//获得规则对应的指标fielditemid
            if (fielditemidStr != null && fielditemidStr.length() > 0) 
            {
                FieldItem holidyTypefieldItem = DataDictionary.getFieldItem(fielditemidStr);//获得fieldItem对象
                preci = holidyTypefieldItem.getDecimalwidth();//获得小数点位数
            }
           
            /*
            q3303   加班日期
            Q3305   加班时长
            Q3307   调休时长
            Q3309   可用时长
            */
            int index, size;
            index = Integer.parseInt(pageIndex);
            size = Integer.parseInt(pageSize);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT q3303,Q3305,Q3307,Q3309 FROM q33");
            sql.append(" WHERE nbase='" + nbase);
            sql.append("' AND a0100='" + a0100 + "'");
            sql.append(" AND Q3303 LIKE '" + year + "%'");
            String sqlStr = "select * from ( select ROW_NUMBER() over(ORDER BY q3303 DESC ) numberCode "+
            ",A.* from ("+sql.toString()+") A) T where numberCode between "+((index-1)*size+1)+" and "+(size*index);;
        
            rs = dao.search(sqlStr);
         // 封装调休加班数据   
            while (rs.next()) {
                HashMap map = new HashMap();
                //遍历每个字段
                int columnCount = rs.getMetaData().getColumnCount();
                String info = "";
                for (int i = 1; i <= columnCount; i++) {
                    //字段名
                    String columnName = rs.getMetaData().getColumnName(i).toLowerCase();
                    FieldItem oneFieldItem = (FieldItem)fieldItemMap.get(columnName);
                    if(oneFieldItem!=null){//
                        String value = "";
                       
                        if("q3303".equalsIgnoreCase(oneFieldItem.getItemid())){//日期
                            value=FieldTypeSwitch.getValueByFieldType(rs, rs.getMetaData(), i,oneFieldItem);
                            value= value.replace(".", "-");
                            map.put("date",value); //和date前台接受一致
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date d =  sdf.parse(value);
                            map.put("week", getWeek(d));//和week前台接受一致
                        }else{//暂时其他的只存在数值类型，单位为小时
                            String fieldName= oneFieldItem.getItemdesc();
                            value=String.valueOf(rs.getDouble(i)/60); //分钟转换小时           
                            value=PubFunc.DoFormatDecimal(value, preci);
                            if(i!=columnCount){
                                info +=fieldName+" : "+value+" 小时\n"; 
                            }else{
                                info +=fieldName+" : "+value+" 小时";
                            }
                        }
                    }
                   
                }
                map.put("transferInfo",info);//和transferInfo前台接受一致
                transferHolidayList.add(map);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqParamForApp.closeRowSet(rs);
        }
        
        return transferHolidayList;
    }
  //根据日期取得星期几  
    public static String getWeek(Date date){   
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");  
        String week = sdf.format(date);  
        return week;  
    }
    /**
     * 
     * @Title: searchHolidayDetail   
     * @Description:   查询休假信息 
     * @param @param year
     * @param @param nbase
     * @param @param a0100
     * @param @param b0110
     * @param @param pageIndex
     * @param @param pageSize
     * @param @return
     * @param @throws GeneralException 
     * @return List    
     * @throws
     */
    public List searchHolidayDetail(String year, String nbase, String a0100, String b0110,String pageIndex,String pageSize) throws GeneralException {
        RowSet rs = null;
        ArrayList leaves = new ArrayList();
        try {
            HashMap rescindedLeavesMap = new HashMap();//存放撤销的休假map<请假单号,一条撤销休假记录的map>
            HashMap LeavesMap = new HashMap();//存放休假map<请假单号,一条休假记录的map>
            searchMyLeaves(year,a0100,nbase,b0110,pageIndex,pageSize,LeavesMap,rescindedLeavesMap);

            Iterator it = LeavesMap.entrySet().iterator();//遍历休假 使对应的撤销假紧跟其后
            while (it.hasNext()) {
                Map.Entry entry = (Entry) it.next();
                String no = (String) entry.getKey();
                leaves.add(entry.getValue()); 
                if(rescindedLeavesMap.containsKey(no)){//判断是否有撤销该休假
                    leaves.add(rescindedLeavesMap.get(no)); 
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqParamForApp.closeRowSet(rs);
        }
        
        return leaves;
    }
    /**
     * @throws GeneralException 
     * @Title: searchMyLeaves   
     * @Description: 查询个人休假信息（仅假期类记录，不包含普通假）   
     * @param @return 
     * @return List    
     * @throws
     */
    public void searchMyLeaves(String year, String nbase, String a0100, String b0110,String pageIndex,String pageSize,HashMap LeavesMap,HashMap rescindedLeavesMap) throws GeneralException {
        int index, size;
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            index = Integer.parseInt(pageIndex);
            size = Integer.parseInt(pageSize);
            StringBuffer sql = new StringBuffer();
            
          //Q1501:请假单号；Q1503：假期类型；Q15Z1起始时间；Q15Z3结束时间；Q1507：请假事由；Q1517：请/销假标识（0请/1销）；Q1519：原请假单号(销假该字段有值)
            sql.append("SELECT Q1501,Q1519,Q1517,Q1503,Q15Z1,Q15Z3,Q1507,Q15Z5 FROM q15");
            sql.append(" WHERE nbase='" + nbase);
            sql.append("' AND a0100='" + a0100 + "'");
           // sql.append(" AND Q15Z5='03'");//审批状态为已批
            sql.append("  AND q1503 IN (" + getHolidayTypesForWhrIn(b0110) + ")");
            sql.append(" AND (" + Sql_switcher.year("q15z3") + "=" + year);
            sql.append(" OR " + Sql_switcher.year("q15z1") + "=" + year);
            sql.append(") ");
        
            String sqlStr = "select * from ( select  ROW_NUMBER() over(ORDER BY q1503 DESC,q15Z1 DESC ) numberCode "+
            ",A.* from ("+sql.toString()+") A) T where numberCode between "+((index-1)*size+1)+" and "+(size*index);;
            rs = dao.search(sqlStr.toString());
            while (rs.next()) {
                // 封装请假数据     
                HashMap oneLeaveMap = new HashMap();
                boolean isRescindedLeave = false;
              //遍历每个字段
                int columnCount = rs.getMetaData().getColumnCount();
                String info = "";
                if(null==rs.getString("Q1519")||"".equals(rs.getString("Q1519").trim())){//请假记录
                    String no = rs.getString("Q1501");//使用单号作为key
                    LeavesMap.put(no, oneLeaveMap);
                }else{//销假记录
                    String no = rs.getString("Q1519");//使用撤销假期的单号做为key
                    isRescindedLeave = true;
                    rescindedLeavesMap.put(no, oneLeaveMap);
                }
                for (int i = 5; i <= columnCount; i++) {//从5开始去掉numbercode,Q1501,Q1519,Q1517
                    String columnName = rs.getMetaData().getColumnName(i).toLowerCase();
                    FieldItem holidyTypefieldItem = DataDictionary.getFieldItem(columnName);//获得fieldItem对象
                    String value=FieldTypeSwitch.getValueByFieldType(rs, rs.getMetaData(), i,holidyTypefieldItem);
                    if("Q15Z1".equalsIgnoreCase(holidyTypefieldItem.getItemid())){//开始时间获得星期几
                        value= value.replace(".", "-");
                        info+=holidyTypefieldItem.getItemdesc()+" : "+value+"\n";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date d =  sdf.parse(value);
                        oneLeaveMap.put("date",value.substring(0, 10)); //和date前台接受一致
                        oneLeaveMap.put("week", getWeek(d));//和week前台接受一致
                    }else{
                        if("A".equals(holidyTypefieldItem.getItemtype())&&!"0".equals(holidyTypefieldItem.getCodesetid())){
                             value = AdminCode.getCodeName(holidyTypefieldItem.getCodesetid(), value);
                             if("Q1503".equalsIgnoreCase(columnName)&&isRescindedLeave){//是撤销休假记录在假期类型后标注
                                 value+=value+"(销假)";
                             }
                        }
                        if(i==columnCount)
                            info+=holidyTypefieldItem.getItemdesc()+" : "+value;
                        else
                            info+=holidyTypefieldItem.getItemdesc()+" : "+value+"\n";
                            
                    }
                  
                }
                oneLeaveMap.put("holidayDetailInfo", info);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqParamForApp.closeRowSet(rs);
        }
        
    }
    
    /**
     * 
     * @Title: getSql   
     * @Description: 根据年份，人员编号，人员库，和假期类型 获得查询假期信息sql   
     * @param @param year
     * @param @param a0100
     * @param @param nbase
     * @param @param hols_type
     * @param @return
     * @param @throws GeneralException 
     * @return String    
     * @throws
     */
    private StringBuffer getSql(String year, String a0100, String nbase, String b0110, HashMap fieldItem)
            throws GeneralException {
        StringBuffer sql_str = new StringBuffer();
        String table = "Q17";
        ArrayList fieldlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
        sql_str.append("select q1709,");
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem field = (FieldItem) fieldlist.get(i);
            if (!("b0110".equals(field.getItemid())
                    || "a0101".equals(field.getItemid())
                    || !field.isVisible()
                    || !"0".equals(field.getCodesetid())
                    || "e0122".equals(field.getItemid())
                    || field.getItemid().equals(
                    table.toLowerCase() + "01"))) {
                sql_str.append(field.getItemid() + ",");
                fieldItem.put(field.getItemid(), field);
            }
        }
        sql_str.setLength(sql_str.length() - 1);

        sql_str.append(" from ");
        sql_str.append(table);
        sql_str.append(" where a0100='");
        sql_str.append(a0100);
        sql_str.append("' and nbase='");
        sql_str.append(nbase);
        sql_str.append("'");
        sql_str.append(" and q1701='" + year + "'");
        sql_str.append(" and q1709 in(");
        sql_str.append(getHolidayTypesForWhrIn(b0110));
        sql_str.append(")");
        return sql_str;
    }

    /**
     * @Title: getHolidayTypesMap   
     * @Description:   根据登陆人员机构权限 获得该单位下配置的假期类型Map信息（map<编号,描述>） 
     * @param @return
     * @param @throws GeneralException
     * @param @throws SQLException 
     * @return HashMap    
     * @throws
     */
    private HashMap getHolidayTypesMap(final String b0110) throws GeneralException, SQLException {
        HashMap holidayTypesMap = new HashMap();

        StringBuffer str = new StringBuffer();
        str.append("SELECT codeitemid,codeitemdesc");
        str.append(" FROM codeitem");
        str.append(" WHERE codesetid='27'");
        str.append(" AND codeitemid IN (");
        str.append(getHolidayTypesForWhrIn(b0110));
        str.append(")");
        str.append(" ORDER BY codeitemid");

        ContentDAO dao = new ContentDAO(conn);
        RowSet frowset = null;
        try {
            frowset = dao.search(str.toString());
            while (frowset.next()) {
                holidayTypesMap.put(frowset.getString("codeitemid"), frowset.getString("codeitemdesc"));
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (frowset != null) {
                frowset.close();
            }
        }
        return holidayTypesMap;
    }
    /**
     * @Title: getHolidayTypesForWhrIn   
     * @Description: 取得用于sql的in条件中的假期类型串（形如：'06','08')   
     * @param @param b0110 单位编码（假期类型是支持集团化应用的）
     * @param @return 
     * @return String    
     */
    private String getHolidayTypesForWhrIn(String b0110) {
        b0110 = b0110 == null ? "" : b0110;
        
        StringBuilder whrIn = new StringBuilder();

        String holidayTypes = kqParam.getHolidayTypes(this.conn, b0110);

        String[] types = holidayTypes.split(",");
        for (int i = 0; i < types.length; i++) {
            whrIn.append("'" + types[i] + "'");
            if (i < types.length - 1)
                whrIn.append(",");
        }

        return whrIn.toString();
    }
    
    /**
     * 考勤规则的一个hashmap集
     * 
     * @return
     * @throws GeneralException
     */
    public HashMap getKqItemById(String item_id) throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        kq_item_sql = kq_item_sql + " where item_id='" + item_id + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hashm_one = new HashMap();
        try {
            rs = dao.search(kq_item_sql);
            if (rs.next()) {

                hashm_one.put("fielditemid", rs.getString("fielditemid"));
                hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                hashm_one.put("item_id", item_id);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashm_one;
    }
}
