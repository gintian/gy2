package com.hjsj.hrms.module.projectmanage.project.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * Title: ManProjectHoursBo
 * </p>
 * <p>
 * Description: 项目添加修改BO
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-12-31 下午4:11:18
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class ManProjectHoursBo {
    Connection conn;
    UserView userview;

    public ManProjectHoursBo(Connection frameconn, UserView userView) {
        this.conn = frameconn;
        this.userview = userView;
    }

    /**
     * 
     * @Title:getColumnList
     * @Description：获取数据字典字段属性
     * @author liuyang
     * @param fi
     * @return
     */
    public HashMap getColumnList(FieldItem fi) {
        HashMap map = new HashMap();
        map.put("itemid", fi.getItemid());
        map.put("itemdesc", fi.getItemdesc());
        map.put("codesetId", fi.getCodesetid());
        map.put("columnType", fi.getItemtype());
        map.put("width", fi.getDecimalwidth());
        map.put("itemLength", fi.getItemlength());
        return map;
    }

    /**
     * 
     * @Title:getPhotoPath
     * @Description：获取照片路径
     * @author liuyang
     * @param nbase
     * @param a0100
     * @return
     */
    public String getPhotoPath(String nbase, String a0100) {
        PhotoImgBo imgBo = new PhotoImgBo(conn);
        return imgBo.getPhotoPathLowQuality(nbase, a0100);
    }

    /**
     * 
     * @Title:addProject
     * @Description：进行项目添加
     * @author liuyang
     * @param dataList
     * @param memebersId
     * @throws GeneralException
     */
    public String addProject(MorphDynaBean dataList, ArrayList memebersId) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        StringBuffer newData = new StringBuffer("{");
        try {
            ArrayList fieldList = DataDictionary.getFieldList("P11", Constant.USED_FIELD_SET);
            ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
            ArrayList listValues = new ArrayList();
            ColumnsInfo columnsInfo = new ColumnsInfo();
            // 单独处理指标
            String exceptFields = ",p1101,p1123,p1125,";
            StringBuffer stbf = new StringBuffer(" INSERT INTO P11 ");
            StringBuffer stInclude = new StringBuffer(" ( ");
            stbf.append(" ( ");
            IDGenerator idg = new IDGenerator(2, this.conn);
            String idString = idg.getId("P11.P1101");
            listValues.add(idString);
            newData.append("id:'project" + PubFunc.encrypt(idString) + "',");
            newData.append("p1101:'" + PubFunc.encrypt(idString) + "',");
            stbf.append("P1101,");
            stInclude.append("?,");
            Object p1117 = 0;
            for (int i = 0; i < fieldList.size(); i++) {

                FieldItem fi = (FieldItem) fieldList.get(i);
                // 去除未构库的指标
                if (!"1".equals(fi.getUseflag())) {
                    continue;
                }
                // 去除隐藏的指标
                if (!"1".equals(fi.getState())) {
                    continue;
                }
                // 单独处理指标
                if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
                    continue;
                }
                if (dataList.toString().indexOf(fi.getItemid() + "=") > 0) {

                    if ("p1107".equalsIgnoreCase(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + ",");
                        stInclude.append("? ,");
                        newData.append("p1107:'" + dataList.get(fi.getItemid())
                                .toString().substring(0, 10) + "',");
                        continue;
                    }

                    if ("p1109".equalsIgnoreCase(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + ",");
                        stInclude.append("? ,");
                        newData.append("p1109:'" + dataList.get(fi.getItemid())
                                .toString().substring(0, 10) + "',");
                        continue;
                    }

                    if ("p1111".equalsIgnoreCase(fi.getItemid())) {
                        Object value = dataList.get(fi.getItemid());
                        if (value == null)
                            value = 0;

                        listValues.add(value);
                        p1117 = value;
                        stbf.append(fi.getItemid() + ",");
                        stInclude.append("? ,");
                        int decimal = fi.getDecimalwidth();
                        value = new BigDecimal(Float.valueOf(value + "")).setScale(decimal, BigDecimal.ROUND_HALF_UP); 
                        newData.append(fi.getItemid() + ":'" + value + "',");
                        continue;
                    }

                    stbf.append(fi.getItemid() + ",");
                    stInclude.append("? ,");
                    listValues.add(dataList.get(fi.getItemid()));
                    if("N".equalsIgnoreCase(fi.getItemtype())){
                        String value = String.valueOf(dataList.get(fi.getItemid()));
                        value = StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value) ? "0" : value;
                        int decimal = fi.getDecimalwidth();
                        value = String.valueOf(new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP)); 
                        newData.append(fi.getItemid() + ":'" + value + "',");
                    } else if("p1121".equals(fi.getItemid()) || !"0".equalsIgnoreCase(fi.getCodesetid())){
                        String value = (String) dataList.get(fi.getItemid());
                        String codesetid = fi.getCodesetid();
                        
                        if ("p1121".equals(fi.getItemid()) || "UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid())) {
                        	String itemid = value;
                            value = AdminCode.getCodeName("UN", itemid);
                            if (StringUtils.isEmpty(value))
                                value = AdminCode.getCodeName("UM", itemid);
                        } else 
                            value = AdminCode.getCodeName(codesetid, value); 
                        
                        newData.append(fi.getItemid() + ":\"" + value.replace("\"", "\\\"") + "\",");
                        
                    } else
                        newData.append(fi.getItemid() + ":\"" + dataList.get(fi.getItemid()).toString().replace("\"", "\\\"") + "\",");
                        
                }
            }

            ArrayList values = new ArrayList();
            stbf.append(" p1113,p1115,p1117,P1123,p1125 )");
            stbf.append(" VALUES ");
            stbf.append(stInclude.toString() + " ?,?,?,?,? )");
            listValues.add(0);
            listValues.add(0);
            listValues.add(p1117);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String time = format.format(new Date());
            Date date = DateUtils.getTimestamp(time, "yyyy-MM-dd");
            listValues.add(date);
            listValues.add(userview.getUserFullName());
            dao.insert(stbf.toString(), listValues);
            savePorjectMembers(memebersId, idString, (String) dataList.get("p1107"),
                    (String) dataList.get("p1109"));
            
            if(newData.length() > 1) {
                newData.append("p1117:'" + p1117 + "',");
                newData.append("iconCls:'x-tree-project-iconCls',");
                newData.append("leaf: true}");
            } else 
                newData.setLength(0);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        return newData.toString();
    }

    /**
     * 
     * @Title:savePorjectMembers
     * @Description：保存项目下的人员信息
     * @author liuyang
     * @param memebersId
     * @param projectId
     * @param beginTime
     * @param endTime
     * @throws GeneralException
     */
    private void savePorjectMembers(ArrayList memebersId, String projectId, String beginTime,
            String endTime) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        try {
            IDGenerator idg = new IDGenerator(2, this.conn);
            ArrayList fieldList = DataDictionary.getFieldList("P13", 1);

            for (int i = 0; i < memebersId.size(); i++) {
                MorphDynaBean bean = new MorphDynaBean();
                bean = (MorphDynaBean) memebersId.get(i);
                ArrayList list = new ArrayList();
                StringBuffer stbf = new StringBuffer(" Insert into p13 ( ");
                StringBuffer sf = new StringBuffer(" ( ");
                for (int j = 0; j < fieldList.size(); j++) {
                    FieldItem fi = (FieldItem) fieldList.get(j);
                    if ("p1301".equals(fi.getItemid())) {
                        list.add(idg.getId("p13.p1301"));
                        stbf.append(" P1301, ");
                        sf.append(" ?, ");
                        continue;
                    }
                    
                    if ("p1101".equals(fi.getItemid())) {
                        list.add(projectId);
                        stbf.append(" P1101, ");
                        sf.append(" ?, ");
                        continue;
                    }
                    
                    if (bean.toString().indexOf(fi.getItemid() + "=") > 0) {

                        if ("a0100".equals(fi.getItemid())) {
                            list.add(PubFunc.decrypt(bean.get(fi.getItemid()).toString()).substring(0, 3));
                            stbf.append(" nbase, ");
                            sf.append(" ?, ");
                            list.add(PubFunc.decrypt(bean.get(fi.getItemid()).toString()).substring(3));
                            stbf.append(" " + fi.getItemid() + ", ");
                            sf.append(" ?, ");
                            list.add(getGuidkey(PubFunc.decrypt(bean.get(fi.getItemid()).toString())));
                            stbf.append(" GUIDKEY, ");
                            sf.append(" ?, ");
                            continue;
                        }
                        
                        if ("nbase".equals(fi.getItemid())) {
                            continue;
                        }

                        list.add(bean.get(fi.getItemid()));
                        stbf.append(" " + fi.getItemid() + ", ");
                        sf.append(" ?, ");
                        continue;
                    }
                    
                    if ("p1315".equals(fi.getItemid())) {
                        list.add(DateUtils.getTimestamp(beginTime.substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + ",");
                        sf.append("? ,");
                    }
                    //新增项目成员时，不设置终止时间
//                    if ("p1317".equals(fi.getItemid())) {
//                        list.add(DateUtils.getTimestamp(endTime.substring(0, 10), "yyyy-MM-dd"));
//                        stbf.append(fi.getItemid() + ",");
//                        sf.append("? ,");
//                    }
                }
                
                stbf.append("P1319,P1321,P1323 )");
                sf.append("?,?,? )");
                list.add(0);
                list.add(0);
                list.add(0);
                String sql = stbf.toString();
                sql = sql + "values";
                sql = sql + sf.toString();
                dao.insert(sql, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 
     * @Title:getGuidkey
     * @Description：获取人员Guidkey
     * @author liuyang
     * @param string
     * @return
     * @throws GeneralException
     */
    private String getGuidkey(String string) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        String guidkey = "";
        try {
            String sqlString = " select guidkey from " + string.substring(0, 3)
                    + "A01 where a0100 =? ";
            ArrayList values = new ArrayList();
            values.add(string.substring(3));
            RowSet rs = dao.search(sqlString, values);
            while (rs.next())
                guidkey = rs.getString("guidkey");
           
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return guidkey;
    }

    /**
     * 
     * @Title:getBeforeEditList
     * @Description： 获取编辑前页面列表数据
     * @author liuyang
     * @param projectId
     * @return
     * @throws GeneralException
     */
    public ArrayList getBeforeEditList(String projectId) throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        RowSet rst = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
            StringBuffer stbf = new StringBuffer();
            StringBuffer sql = new StringBuffer();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList valus = new ArrayList();
            sql.append(" select p1101 from p11 where 1=1 ");
            sql.append(" and p1109 <="
                    + Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd")
                            + " 00:00:00"));
            sql.append(" and p1101 = ? ");
            valus.add(projectId);
            rst = dao.search(sql.toString(), valus);
            stbf.append("select p1305,p1303,a0101,a0100,nbase,p1307,p1309,p1311 from P13 where p1101 = ?");
            if (!rst.next()) {
                stbf.append(" and (P1317 IS NULL OR P1317=''");
                stbf.append(" or P1317 >="
                        + Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd")
                                + " 00:00:00"));
                stbf.append(")");
            }
            rs = dao.search(stbf.toString(), valus);
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("p1305", rs.getString("p1305"));
                map.put("p1303", rs.getString("p1303"));
                map.put("a0101", rs.getString("a0101"));
                map.put("a0100", PubFunc.encrypt(rs.getString("nbase") + rs.getString("a0100")));
                map.put("p1309", rs.getString("p1309"));
                map.put("p1311", rs.getString("p1311"));
                map.put("p1307", rs.getString("p1307"));
                map.put("imageUrl", getPhotoPath(rs.getString("nbase"), rs.getString("a0100")));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();

            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    /**
     * 
     * @Title:getBeforeProjectList
     * @Description：确认编辑前列表
     * @author liuyang
     * @param projectId
     * @param listValues
     * @return
     * @throws GeneralException
     */
    public HashMap getBeforeProjectList(String projectId, ArrayList listValues)
            throws GeneralException {
        HashMap valueMap = new HashMap();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            StringBuffer stbf = new StringBuffer();
            stbf.append("select * from p11 where P1101 = ?");
            ArrayList values = new ArrayList();
            values.add(projectId);

            rs = dao.search(stbf.toString(), values);

            while (rs.next()) {
                for (int i = 0; i < listValues.size(); i++) {
                    HashMap map = (HashMap) listValues.get(i);
                    String type = (String) map.get("columnType");
                    if ("A".equals(type) || "M".equals(type)) {
                        if ("p1121".equals((String) map.get("itemid"))) {
                            valueMap.put((String) map.get("itemid"), rs.getString((String) map.get("itemid"))
                                    + "`" + getUnDsc(rs.getString((String) map.get("itemid"))));
                            continue;
                        }
                        if ("A".equals(type) && !"0".equals((String) map.get("codesetId"))) {
                            valueMap.put((String) map.get("itemid"), rs.getString((String) map.get("itemid"))
                                    + "`"
                                    + getUsualDsc(rs.getString((String) map.get("itemid")),
                                            (String) map.get("codesetId")));
                            continue;
                        }

                        valueMap.put((String) map.get("itemid"), rs.getString((String) map.get("itemid")));
                        continue;
                    }
                    if ("N".equals(type)) {
                        valueMap.put((String) map.get("itemid"), rs.getFloat((String) map.get("itemid")));
                        continue;
                    }
                    if ("D".equals(type)) {
                        Date value = rs.getDate((String) map.get("itemid"));
                        String date = DateUtils.format(value, "yyyy-MM-dd");
                        if (StringUtils.isNotEmpty(date)) {
                            date = date.substring(0, 10);
                        }
                        
                        valueMap.put((String) map.get("itemid"), date);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return valueMap;
    }

    /**
     * 
     * @Title:getUsualDsc
     * @Description：获取下拉数据
     * @author liuyang
     * @param coditemid
     * @param codesetid
     * @return
     * @throws GeneralException
     */
    private String getUsualDsc(String coditemid, String codesetid) throws GeneralException {
        if (StringUtils.isEmpty(codesetid) || StringUtils.isEmpty(coditemid))
            return "";
        
        String value = AdminCode.getCodeName(codesetid, coditemid);
        return StringUtils.isEmpty(value) ? "" : value;
    }

    /**
     * 
     * @Title:getUnDsc
     * @Description：获取下拉数据
     * @author liuyang
     * @param coditemid
     * @return
     * @throws GeneralException
     */
    private String getUnDsc(String coditemid) throws GeneralException {
        String value = "";
        RowSet rs = null;
        try {
            value = AdminCode.getCodeName("UN", coditemid);
            if (StringUtils.isEmpty(value))
                value = AdminCode.getCodeName("UM", coditemid);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return value;
    }

    /**
     * 
     * @Title:editProject
     * @Description：修改项目数据
     * @author liuyang
     * @param dataList
     * @param memebersId
     * @param projectId
     * @throws GeneralException
     */
    public String editProject(MorphDynaBean dataList, ArrayList memebersId, String projectId)
            throws GeneralException {
        StringBuffer newData = new StringBuffer("{");
        ContentDAO dao = new ContentDAO(conn);
        try {
            String newEndDate = "";
            ArrayList fieldList = DataDictionary.getFieldList("P11", 1);
            ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
            ArrayList listValues = new ArrayList();
            ColumnsInfo columnsInfo = new ColumnsInfo();
            // 单独处理指标
            String exceptFields = ",p1123,p1125,";
            StringBuffer stbf = new StringBuffer(" UPDATE  P11 ");
            StringBuffer stInclude = new StringBuffer("");
            stbf.append(" SET ");
            for (int i = 0; i < fieldList.size(); i++) {

                FieldItem fi = (FieldItem) fieldList.get(i);
                // 去除未构库的指标
                if (!"1".equals(fi.getUseflag()))
                    continue;
                
                // 去除隐藏的指标
                if (!"1".equals(fi.getState()))
                    continue;
                
                // 单独处理指标
                if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1)
                    continue;
                
                if (dataList.toString().indexOf(fi.getItemid() + "=") > 0) {
                    if ("p1107".equals(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + "=?,");
                        newData.append("p1107:'" + dataList.get(fi.getItemid())
                                .toString() + "',");
                        continue;
                    }

                    if ("p1109".equals(fi.getItemid())) {
                        newEndDate = dataList.get(fi.getItemid()).toString().substring(0, 10);
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + "=?,");
                        newData.append("p1109:'" + dataList.get(fi.getItemid())
                                .toString() + "',");
                        continue;
                    }

                    if ("p1111".equalsIgnoreCase(fi.getItemid())) {
                        Object value = dataList.get(fi.getItemid());
                        if (value == null)
                            value = 0;

                        listValues.add(value);
                        stbf.append(fi.getItemid() + "=?,");
                        int decimal = fi.getDecimalwidth();
                        value = new BigDecimal(Float.valueOf(value + "")).setScale(decimal, BigDecimal.ROUND_HALF_UP); 
                        newData.append("p1111:'" + value + "',");
                        continue;
                    }
                    
                    if ("p1117".equalsIgnoreCase(fi.getItemid())) {
                        float value = Float.valueOf(dataList.get("p1111").toString()) - Float.valueOf(dataList.get("p1115").toString());
                        
                        listValues.add(value+"");
                        stbf.append(fi.getItemid() + "=?,");
                        int decimal = fi.getDecimalwidth();
                        newData.append("p1117:'" + new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP) + "',");
                        continue;
                    }
                    

                    stbf.append(fi.getItemid() + "=?,");
                    if(!"0".equalsIgnoreCase(fi.getCodesetid())) {
                        String value = (String) dataList.get(fi.getItemid());
                        if(StringUtils.isNotEmpty(value) && value.indexOf("`") > -1)
                            value = value.substring(0, value.indexOf("`"));
                        
                        if("null".equalsIgnoreCase(value))
                            value = "";
                        
                        listValues.add(value);
                    } else
                        listValues.add(dataList.get(fi.getItemid()));
                    
                    if("N".equalsIgnoreCase(fi.getItemtype())){
                        String value = String.valueOf(dataList.get(fi.getItemid()));
                        value = StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value) ? "0" : value;
                        int decimal = fi.getDecimalwidth();
                        value = String.valueOf(new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP)); 
                        newData.append(fi.getItemid() + ":'" + value + "',");
                    } else if("p1121".equals(fi.getItemid()) || !"0".equalsIgnoreCase(fi.getCodesetid())){
                        String value = (String) dataList.get(fi.getItemid());
                        String codesetid = fi.getCodesetid();
                        
                        if ("p1121".equals(fi.getItemid()) || "UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid())) {
                        	String itemid = value;
                            value = AdminCode.getCodeName("UN", itemid);
                            if (StringUtils.isEmpty(value))
                                value = AdminCode.getCodeName("UM", itemid);
                        } else 
                            value = AdminCode.getCodeName(codesetid, value); 
                            
                        newData.append(fi.getItemid() + ":\"" + value.replace("\"", "\\\"") + "\",");
                        
                    } else
                        newData.append(fi.getItemid() + ":\"" + dataList.get(fi.getItemid()).toString().replace("\"", "\\\"") + "\",");
                }
            }
            
            String oldEndDate = getEndDate(projectId);
            if(StringUtils.isNotEmpty(newEndDate) && !newEndDate.equals(oldEndDate)){
                /*
                 *项目的结束时间更改没有结束的项目成员对应的结束时间，筛选项目成员的条件：
                 *  1.当项目结束时间延后时，项目成员中符合 结束时间>=项目原来的结束时间（具体筛选条件为：成员结束时间>=项目旧的结束时间或者成员结束时间>=项目新的结束时间，
                 *  由于成员结束时间>=项目旧的结束时间   筛选出的范围要比  成员结束时间>=项目新的结束时间   筛选出的范围大，所以只要符合 成员结束时间>=项目旧的结束时间 的
                 *  项目成员都要更改其结束时间）
                 *  2.当项目结束时间提前时，项目成员中符合 结束时间>=项目新的结束时间（具体筛选条件为：成员结束时间>=项目旧的结束时间或者成员结束时间>=项目新的结束时间，
                 *  由于  成员结束时间>=项目新的结束时间    筛选出的范围要比  成员结束时间>=项目旧的结束时间    筛选出的范围大，所以只要符合 成员结束时间>=项目旧的结束时间  的
                 *  项目成员都要更改其结束时间）
                 */
                StringBuffer sql = new StringBuffer();
                sql.append("UPDATE P13 SET P1317=? WHERE P1101=?");
                sql.append(" AND (" + Sql_switcher.dateToChar("p1317", "yyyy-mm-dd") + " >= ?");
                sql.append(" OR " + Sql_switcher.dateToChar("p1317", "yyyy-mm-dd") + " >= ?"); 
                sql.append(" OR P1317 IS NULL)"); 
                ArrayList valueList = new ArrayList();
                valueList.add(DateUtils.getTimestamp(newEndDate, "yyyy-MM-dd"));
                valueList.add(projectId);
                valueList.add(oldEndDate);
                valueList.add(newEndDate);
                dao.update(sql.toString(), valueList);
                
            }
            
            stInclude.append(stbf.subSequence(0, stbf.lastIndexOf(",")));
            stInclude.append(" where p1101 = ?");
            listValues.add(projectId);
            dao.update(stInclude.toString(), listValues);

            if(newData.length() > 1) {
                newData.append("id:'project" + PubFunc.encrypt(projectId) + "',");
                newData.append("p1101:'" + PubFunc.encrypt(projectId) + "',");
                newData.append("iconCls:'x-tree-project-iconCls'}");
            } else 
                newData.setLength(0);
                
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return newData.toString();
    }

    /**
     * 
     * @Title:updateIsNotExisMembers
     * @Description：修改项目人员信息
     * @author liuyang
     * @param memberId
     * @param projectId
     * @throws GeneralException
     */
    public String updateIsNotExisMembers(String memberId, String projectId) throws GeneralException {
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        Date date_memberEndTime = null;
        String tip = "1";
        try {
            String checkProject = "select P1511  from p15  where p1301 = (select p1301  from p13  where a0100 = ? and nbase = ? and p1101= ? ) and p1519 in ('1','0') ";
            // checkProject =
            // checkPr·oject+"and P1509 ="+Sql_switcher.dateValue(DateUtils.format(new
            // Date(), "yyyy-MM-dd HH:mm:ss"));
            values.add(memberId.substring(3));
            values.add(memberId.substring(0, 3));
            values.add(projectId);
            RowSet projectMemberDetail = dao.search(checkProject, values);
            if (!projectMemberDetail.next()) {
                tip = "0";
                deleteMember(memberId, projectId);
                return tip;
            }
            projectMemberDetail.close();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date_Today = DateUtils.getTimestamp(format.format(new Date()).substring(0, 10)
                    + " 00:00:00", "yyyy-MM-dd");
            checkProject = checkProject
                    + "and P1509 >="
                    + Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd")
                            + " 00:00:00");
            checkProject = checkProject
                    + "and P1509 <="
                    + Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd")
                            + " 23:59:59");
            RowSet projectMemberDate = dao.search(checkProject, values);
            if (projectMemberDate.next()) {
                date_memberEndTime = date_Today;
            } else {
                date_memberEndTime = DateUtils.getTimestamp(format.format(
                        new Date(new Date().getTime() - 24 * 60 * 60 * 1000)).substring(0, 10),
                        "yyyy-MM-dd");
                tip = "0";
            }
            StringBuffer sqlStr = new StringBuffer();
            String checkProjectTime = "select  P1311  from p13  where a0100 = ? and nbase = ? and p1101= ?  ";
            RowSet project = dao.search(checkProjectTime, values);
            sqlStr.append(" update p13 set p1317 = ? ");
            values.clear();
            values.add(date_memberEndTime);
            if (project.next()) {
                if ("01".equals(project.getString("p1311"))) {
                    sqlStr.append(" , P1311 = ?");
                    values.add("02");
                }
            }
            sqlStr.append(" where a0100 = ? and nbase = ? and p1101= ? ");
            values.add(memberId.substring(3));
            values.add(memberId.substring(0, 3));
            values.add(projectId);
            dao.update(sqlStr.toString(), values);
            return tip;
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:deleteMember
     * @Description：删除项目中没有工时的人
     * @author liuyang
     * @param memberId
     * @param projectId
     * @throws GeneralException
     */
    private void deleteMember(String memberId, String projectId) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(conn);
            String str = "delete from p13 where a0100 = ? and nbase = ? and p1101= ? ";
            ArrayList values = new ArrayList();
            values.add(memberId.substring(3));
            values.add(memberId.substring(0, 3));
            values.add(projectId);
            dao.delete(str, values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:updateMembers
     * @Description：修改项目人员信息
     * @author liuyang
     * @param memberId
     * @param projectId
     * @throws GeneralException
     */
    private void updateMembers(String memberId, String beginTime, String endTime, String projectId)
            throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(conn);

            String sqlStr = "update p13 set p1315 = ?,p1317 = ? where a0100 = ? and nbase = ? and p1101= ?";
            ArrayList values = new ArrayList();
            values.add(DateUtils.getTimestamp(beginTime, "yyyy-MM-dd"));
            values.add(DateUtils.getTimestamp(endTime, "yyyy-MM-dd"));
            values.add(memberId.substring(3));
            values.add(memberId.substring(0, 3));
            values.add(projectId);
            dao.update(sqlStr, values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:getBeforeLandMarkList
     * @Description：获取里程碑修改页面数据
     * @author liuyang
     * @param projectId
     * @param listValues
     * @param landMarkId
     * @return
     * @throws GeneralException
     */
    public HashMap getBeforeLandMarkList(String projectId, ArrayList listValues, String landMarkId)
            throws GeneralException {
        HashMap valueMap = new HashMap();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            StringBuffer stbf = new StringBuffer();
            stbf.append("select * from p12 where P1101 = ? and P1201 = ?");
            ArrayList values = new ArrayList();
            values.add(projectId);
            values.add(landMarkId);
            rs = dao.search(stbf.toString(), values);

            while (rs.next()) {
                for (int i = 0; i < listValues.size(); i++) {
                    HashMap map = (HashMap) listValues.get(i);
                    String type = (String) map.get("columnType");
                    if ("A".equals(type) || "M".equals(type)) {
                        if ("A".equals(type) && !"0".equals((String) map.get("codesetId"))) {
                            valueMap.put((String) map.get("itemid"), rs.getString((String) map
                                    .get("itemid"))
                                    + "`"
                                    + getUsualDsc(rs.getString((String) map.get("itemid")),
                                            (String) map.get("codesetId")));
                            continue;
                        }

                        valueMap.put((String) map.get("itemid"), rs.getString((String) map
                                .get("itemid")));
                        continue;
                    }
                    if ("N".equals(type)) {
                        valueMap.put((String) map.get("itemid"), rs.getFloat((String) map
                                .get("itemid")));
                        continue;
                    }
                    if ("D".equals(type)) {
                        Date value = rs.getDate((String) map.get("itemid"));
                        String date = DateUtils.format(value, "yyyy-MM-dd");
                        if (StringUtils.isNotEmpty(date)) {
                            date = date.substring(0, 10);
                        }
                        
                        valueMap.put((String) map.get("itemid"), date);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return valueMap;
    }

    /**
     * 
     * @Title:addLandMark
     * @Description：里程碑添加
     * @author liuyang
     * @param dataList
     * @param projectId
     * @throws GeneralException
     */
    public void addLandMark(MorphDynaBean dataList, String projectId) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        try {
            ArrayList fieldList = DataDictionary.getFieldList("P12", 1);
            ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
            ArrayList listValues = new ArrayList();
            ColumnsInfo columnsInfo = new ColumnsInfo();
            // 单独处理指标
            String exceptFields = ",p1201,p1219,p1213,p1215,p1217,P1221";
            StringBuffer stbf = new StringBuffer(" INSERT INTO P12 ");
            StringBuffer stInclude = new StringBuffer(" ( ");
            stbf.append(" ( ");
            IDGenerator idg = new IDGenerator(2, this.conn);
            String idString = idg.getId("P12.P1201");
            listValues.add(idString);
            stbf.append("P1201,");
            stInclude.append("?,");
            Object p1217 = 0;
            for (int i = 0; i < fieldList.size(); i++) {

                FieldItem fi = (FieldItem) fieldList.get(i);
                // 去除未构库的指标
                if (!"1".equals(fi.getUseflag())) {
                    continue;
                }
                if ("p1101".equals(fi.getItemid())) {
                    listValues.add(projectId);
                    stbf.append(fi.getItemid() + ",");
                    stInclude.append("? ,");
                    continue;
                }
                // 去除隐藏的指标
                if (!"1".equals(fi.getState())) {
                    continue;
                }
                // 单独处理指标
                if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
                    continue;
                }

                if (dataList.toString().indexOf(fi.getItemid() + "=") > 0) {

                    if ("p1207".equals(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + ",");
                        stInclude.append("? ,");
                        continue;
                    }
                    
                    if ("p1209".equals(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + ",");
                        stInclude.append("? ,");
                        continue;
                    }
                    
                    if ("p1211".equals(fi.getItemid())) {
                        p1217 = dataList.get(fi.getItemid());
                        p1217 = p1217 == null ? 0 : p1217;
                    }

                    stbf.append(fi.getItemid() + ",");
                    stInclude.append("? ,");
                    listValues.add(dataList.get(fi.getItemid()));
                }
            }

            ArrayList values = new ArrayList();
            stbf.append(" p1213,p1215,p1217,p1221 )");
            stbf.append(" VALUES ");
            stbf.append(stInclude.toString() + " ?,?,?,? )");
            listValues.add(0);
            listValues.add(0);
            listValues.add(p1217);
            listValues.add(1);
            dao.insert(stbf.toString(), listValues);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:editLandMark
     * @Description：里程碑修改
     * @author liuyang
     * @param dataList
     * @param projectId
     * @param landMarkId
     * @throws GeneralException
     */
    public String editLandMark(MorphDynaBean dataList, String projectId, String landMarkId)
            throws GeneralException {
        StringBuffer newData = new StringBuffer("{");
        ContentDAO dao = new ContentDAO(conn);
        try {
            ArrayList fieldList = DataDictionary.getFieldList("P12", 1);
            ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
            ArrayList listValues = new ArrayList();
            ColumnsInfo columnsInfo = new ColumnsInfo();
            // 单独处理指标
            String exceptFields = ",p1219,p1221,";
            StringBuffer stbf = new StringBuffer(" UPDATE  P12 ");
            StringBuffer stInclude = new StringBuffer("");
            stbf.append(" SET ");
            for (int i = 0; i < fieldList.size(); i++) {

                FieldItem fi = (FieldItem) fieldList.get(i);
                // 去除未构库的指标
                if (!"1".equals(fi.getUseflag())) {
                    continue;
                }
                // 去除隐藏的指标
                if (!"1".equals(fi.getState())) {
                    continue;
                }
                // 单独处理指标
                if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1) {
                    continue;
                }
                
                if (dataList.toString().indexOf(fi.getItemid() + "=") > 0) {

                    if ("p1207".equals(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + "=?,");
                        newData.append("p1107:'" + dataList.get(fi.getItemid())
                                .toString().substring(0, 10) + "',");
                        continue;
                    }
                    
                    if ("p1209".equals(fi.getItemid())) {
                        listValues.add(DateUtils.getTimestamp(dataList.get(fi.getItemid())
                                .toString().substring(0, 10), "yyyy-MM-dd"));
                        stbf.append(fi.getItemid() + "=?,");
                        newData.append("p1109:'" + dataList.get(fi.getItemid())
                                .toString().substring(0, 10) + "',");
                        continue;
                    }
                    
                    if ("p1217".equals(fi.getItemid())) {
                        float value = Float.valueOf(dataList.get("p1211").toString()) - Float.valueOf(dataList.get("p1215").toString());
                        listValues.add(value+"");
                        stbf.append(fi.getItemid() + "=?,");
                        int decimal = fi.getDecimalwidth();
                        newData.append(fi.getItemid().replace("p12", "p11") + ":'" 
                                + new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP) + "',");
                        continue;
                    }
                    
                    stbf.append(fi.getItemid() + "=?,");
                    listValues.add(dataList.get(fi.getItemid()));
                    
                    if("N".equalsIgnoreCase(fi.getItemtype())){
                        String value = String.valueOf(dataList.get(fi.getItemid()));
                        value = StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value) ? "0" : value;
                        int decimal = fi.getDecimalwidth();
                        value = String.valueOf(new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP)); 
                        newData.append(fi.getItemid().replace("p12", "p11") + ":'" + value + "',");
                    } else if(!"0".equalsIgnoreCase(fi.getCodesetid())){
                        String value = (String) dataList.get(fi.getItemid());
                        String codesetid = fi.getCodesetid();
                        value = AdminCode.getCodeName(codesetid, value); 
                        newData.append(fi.getItemid().replace("p12", "p11") + ":\"" + value.replace("\"", "\\\"") + "\",");
                        
                    } else
                        newData.append(fi.getItemid().replace("p12", "p11") + ":\"" + dataList.get(fi.getItemid()).toString().replace("\"", "\\\"") + "\",");
                }
            }
            
            stbf.append(" P1221 = ? ");
            listValues.add(1);
            stInclude.append(stbf.toString());
            stInclude.append(" where p1101 = ? and p1201 = ?");
            listValues.add(projectId);
            listValues.add(landMarkId);
            dao.update(stInclude.toString(), listValues);

            if(newData.length() > 1) {
                newData.append("pnodeId:'project" + PubFunc.encrypt(projectId) + "',");
                newData.append("id:'landmark" + PubFunc.encrypt(landMarkId) + "',");
                newData.append("p1101:'" + PubFunc.encrypt(projectId) + "',");
                newData.append("p1201:'" + PubFunc.encrypt(landMarkId) + "',");
                newData.append("iconCls:'x-tree-landmark-iconCls'}");
            } else 
                newData.setLength(0);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        return newData.toString();
    }

    /**
     * 
     * @Title:summarizationOfData
     * @Description：数据汇总
     * @author liuyang
     * @param tableName
     * @param personIds
     * @throws GeneralException
     */
    public void sumData(String tableName, String personIds) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        try {
            String destTab = tableName;

            StringBuffer srcTab = new StringBuffer();
            // 2016/1/7 wangjl 时间保存为天数
            // srcTab.append(" (select sum(p1511)/8.0 sum_p1511 ,sum(p1513)/8.0 sum_p1513, sum(P1511-p1513)/8.0 sum_p1515, ");
            srcTab.append("(select ");
            srcTab.append(Sql_switcher.round("sum(p1511)/480.0", 2) + " sum_p1511 ,");
            srcTab.append(Sql_switcher.round("sum(p1513)/480.0", 2) + " sum_p1513 ,");
            srcTab.append("(" + Sql_switcher.round("sum(P1511)/480.0", 2) + "-"
                    + Sql_switcher.round("sum(p1513)/480.0", 2) + ") sum_p1515 ,");
            srcTab.append(destTab + "01 ");
            srcTab.append(" from P15 ");
            srcTab.append(" where " + destTab + "01 in (" + personIds + ") ");
            // 2016/1/7 wangjl 只汇总已批准的工时
            srcTab.append(" and P1519='1' ");
            srcTab.append("	group by " + destTab + "01) a ");

            StringBuffer strJoin = new StringBuffer();

            strJoin.append(destTab + "." + destTab + "01=a." + destTab + "01 ");

            StringBuffer strSet = new StringBuffer();
            if ("p13".equalsIgnoreCase(destTab)) {//移动端项目工时 拼接符号不对  wangb 20190521
                strSet.append(" P1319=a.sum_p1511 ");
                strSet.append("`P1321=a.sum_p1513 ");
                strSet.append("`P1323=a.sum_p1515 ");
            }
            if ("p12".equalsIgnoreCase(destTab)) {
                strSet.append(" P1213=a.sum_p1511 ");
                strSet.append("`P1215=a.sum_p1513 ");
                strSet.append("`P1217=P1211-a.sum_p1513 ");
            }
            if ("p11".equalsIgnoreCase(destTab)) {
                strSet.append(" P1113=a.sum_p1511 ");
                strSet.append("`P1115=a.sum_p1513 ");
                strSet.append("`P1117=P1111-a.sum_p1513 ");
            }
            StringBuffer strDWhere = new StringBuffer();
            strDWhere.append(destTab + "." + destTab + "01 in (" + personIds + ") ");

            StringBuffer strSWhere = new StringBuffer();
            strSWhere.append(" a." + destTab + "01 in (" + personIds + ") ");

            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab.toString(), strJoin
                    .toString(), strSet.toString(), strDWhere.toString(), strSWhere.toString());
            ArrayList values = new ArrayList();

            dao.update(update);

            StringBuffer sql = new StringBuffer();
            StringBuffer where = new StringBuffer();
            sql.append("update " + destTab + " set");
            if ("p13".equalsIgnoreCase(destTab)) {
                sql.append(" P1319=0 ");
                sql.append(",P1321=0 ");
                sql.append(",P1323=0 ");
                where.append(" p15.P1301=p13.p1301 and p15.p1101=p13.p1101");
            }

            if ("p12".equalsIgnoreCase(destTab)) {
                sql.append(" P1213=0 ");
                sql.append(",P1215=0 ");
                sql.append(",P1217=P1211 ");
                where.append(" p15.P1201=p12.p1201 and p15.p1101=p12.p1101");
            }

            if ("p11".equalsIgnoreCase(destTab)) {
                sql.append(" P1113=0 ");
                sql.append(",P1115=0 ");
                sql.append(",P1117=P1111 ");
                where.append(" p15.p1101=p11.p1101");
            }

            sql.append(" where not exists (");
            sql.append(" select 1 from P15  where "+destTab+"01 in (");
            sql.append(personIds);
            sql.append(")  and P1519='1'");
            sql.append(" and " + where);
            sql.append(") and P1101 IN (");
            sql.append("select p1101 from "+destTab+" where "+destTab+"01 in (");
            sql.append(personIds);
            sql.append("))");
            dao.update(sql.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 删除项目
     * 
     * @Title: deleteProject
     * @Description: 根据项目id，删除项目数据 ，同时删除项目下的里程碑、项目成员、工时明细数据
     * @param projectIds
     *            格式 1,2,3
     * @return
     * @throws GeneralException
     */
    public boolean deleteProject(String projectIds) throws GeneralException {
        boolean isOK = true;

        if (projectIds == null || "".equals(projectIds))
            return isOK;

        String idsWhr = "";
        String[] ids = projectIds.split(",");
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            if (id == null || "".equals(id.trim()))
                continue;

            idsWhr = idsWhr + id + ",";
        }

        if ("".equals(idsWhr))
            return isOK;

        idsWhr = " P1101 IN (" + idsWhr.substring(0, idsWhr.length() - 1) + ")";

        ContentDAO dao = new ContentDAO(conn);
        try {
            // 删除项目
            String sql = "DELETE FROM P11 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());

            // 删除里程碑
            sql = "DELETE FROM P12 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());

            // 删除项目成员
            sql = "DELETE FROM P13 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());

            // 删除工时明细
            sql = "DELETE FROM P15 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());

        } catch (Exception e) {
            isOK = false;
            e.printStackTrace();
        }

        return isOK;
    }

    /**
     * 删除里程碑
     * 
     * @Title: deleteMilestone
     * @Description: 根据里程碑id,删除里程碑，同时删除属于里程碑的工时明细，并重新汇总项目的工时
     * @param milestoneIds
     *            格式 1,2,3
     * @return
     */
    public boolean deleteMilestone(String milestoneIds) {
        boolean isOK = true;

        if (milestoneIds == null || "".equals(milestoneIds))
            return isOK;

        String idsWhr = "";
        String[] ids = milestoneIds.split(",");
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            if (id == null || "".equals(id.trim()))
                continue;

            idsWhr = idsWhr + id + ",";
        }

        if ("".equals(idsWhr))
            return isOK;

        idsWhr = " P1201 IN (" + idsWhr.substring(0, idsWhr.length() - 1) + ")";

        RowSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
            // 删除工时明细
            String sql = "DELETE FROM P15 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());

            // 工时明细数据发生变化后，重新汇总项目工时
            String projectIds = "";
            sql = "SELECT distinct P1101 FROM P12 where " + idsWhr;
            rs = dao.search(sql);
            while (rs.next()) {
                String projectId = "" + rs.getInt("P1101");
                projectIds += projectId + ",";
            }

            if (!"".equals(projectIds)) {
                projectIds = projectIds.substring(0, projectIds.length() - 1);
                this.sumData("P11", projectIds);
            }

            // 删除里程碑
            sql = "DELETE FROM P12 WHERE " + idsWhr;
            dao.delete(sql, new ArrayList());
        } catch (Exception e) {
            isOK = false;
            e.printStackTrace();
        }

        return isOK;
    }

    /**
     * 
     * @Title:getmanDetailIds
     * @Description：获取项目下的人员明细id
     * @author liuyang
     * @param projectIds
     *            形如：1,2,3 或 select p1101 from ...
     * @return
     * @throws GeneralException
     */
    public String getManDetailIds(String projectIds) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        String manDetailId = "";
        RowSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select p1301 from p13");
            sql.append(" where p1101 in (").append(projectIds).append(")");

            rs = dao.search(sql.toString());
            while (rs.next()) {
                manDetailId = manDetailId + rs.getString("p1301");
                if (!rs.isLast())
                    manDetailId = manDetailId + ",";
            }
        } catch (SQLException e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return manDetailId;
    }

    /**
     * 
     * @Title:getProjectDate
     * @Description：修改里程碑时获取项目时间
     * @author liuyang
     * @param projectId
     * @throws GeneralException
     */
    public HashMap getProjectDate(String projectId) throws GeneralException {
        HashMap map = new HashMap();
        ContentDAO dao = new ContentDAO(conn);
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select P1107,P1109 from p11 where p1101 =" + projectId);

            RowSet rs = dao.search(sb.toString());
            while (rs.next()) {
                Date value = rs.getDate("p1107");
                String p1107 = DateUtils.format(value, "yyyy-MM-dd");
                map.put("p1107", p1107);
                value = rs.getDate("p1109");
                String p1109 = DateUtils.format(value, "yyyy-MM-dd");
                map.put("p1109", p1109);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }

    /**
     * 
     * @Title:editProjectManager
     * @Description：在换项目负责人时
     * @author liuyang
     * @param projectId
     *            项目ID
     * @param beforeManId
     *            修改前人员id
     * @param afterManId
     *            修改后人员信息(新添加的会传多个值)
     * @param memberToManager
     *            是否是从项目成员转成项目负责人 1-是转变 2-不是
     * @param endDate
     * @return
     */
    public String editProjectManager(String projectId, String beforeManId,
            MorphDynaBean afterManId, String memberToManager, String endDate) {
        String tip = "";
        try {
            if (StringUtils.isEmpty(beforeManId)) {
                tip = "0";
            } else {
                tip = updateIsNotExisMembers(beforeManId, projectId);
            }

            if ("1".equals(memberToManager) || manInProject(projectId, afterManId)) {
                memberToManager(projectId, afterManId, endDate);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String time = format.format(new Date());
                saveProMembers(afterManId, projectId, time, endDate);
            }

        } catch (GeneralException e) {

            e.printStackTrace();
        }
        return tip;

    }

    /**
     * 检测项目负责人转换后的人员在p13中是否存在
     * 
     * @param manId
     *            转换后的人员id
     * @return
     */
    private boolean manInProject(String projectId, MorphDynaBean manBean) {
        boolean flag = false;
        RowSet rs = null;
        try {
            String manId = PubFunc.decrypt((String) manBean.get("a0100"));
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT 1 FROM P13");
            sql.append(" WHERE NBASE=? AND A0100=? AND P1101=?");
            ArrayList<String> values = new ArrayList<String>();
            values.add(manId.substring(0, 3));
            values.add(manId.substring(3));
            values.add(projectId);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString(), values);
            if (rs.next())
                flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 
     * @Title:memberToManager
     * @Description：将成员转换成项目负责人
     * @author liuyang
     * @param projectId
     * @param beforeManId
     * @throws GeneralException
     */
    private void memberToManager(String projectId, MorphDynaBean afterManId, String endDate)
            throws GeneralException {
        try {
            MorphDynaBean bean = new MorphDynaBean();
            bean = afterManId;
            String manId = (String) afterManId.get("id");
            manId = PubFunc.decryption(manId);
            ContentDAO dao = new ContentDAO(conn);
            ArrayList values = new ArrayList();
            String sql = "update p13 set P1311 = '01',P1317=? where a0100 = ? and nbase = ? and p1101= ?";
            values.add(DateUtils.getTimestamp(endDate.substring(0, 10), "yyyy-MM-dd"));
            values.add(manId.substring(3));
            values.add(manId.substring(0, 3));
            values.add(projectId);
            dao.update(sql, values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void saveProMembers(MorphDynaBean afterManId, String projectId, String beginDate,
            String endDate) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        try {

            DbNameBo bo = new DbNameBo(conn, userview);
            IDGenerator idg = new IDGenerator(2, this.conn);

            ArrayList fieldList = DataDictionary.getFieldList("P13", 1);

            MorphDynaBean bean = new MorphDynaBean();
            bean = afterManId;
            ArrayList list = new ArrayList();
            StringBuffer stbf = new StringBuffer(" Insert into p13 ( ");
            StringBuffer sf = new StringBuffer(" ( ");
            for (int j = 0; j < fieldList.size(); j++) {
                FieldItem fi = (FieldItem) fieldList.get(j);
                if ("p1301".equals(fi.getItemid())) {
                    list.add(idg.getId("p13.p1301"));
                    stbf.append(" P1301, ");
                    sf.append(" ?, ");
                    continue;
                }
                if ("p1101".equals(fi.getItemid())) {
                    list.add(projectId);
                    stbf.append(" P1101, ");
                    sf.append(" ?, ");
                    continue;
                }
                if (bean.toString().indexOf(fi.getItemid() + "=") > 0) {

                    if ("a0100".equals(fi.getItemid())) {
                        list.add(PubFunc.decrypt(bean.get(fi.getItemid()).toString()).substring(0,
                                3));
                        stbf.append(" nbase, ");
                        sf.append(" ?, ");
                        list.add(PubFunc.decrypt(bean.get(fi.getItemid()).toString()).substring(3));
                        stbf.append(" " + fi.getItemid() + ", ");
                        sf.append(" ?, ");
                        list.add(getGuidkey(PubFunc.decrypt(bean.get(fi.getItemid()).toString())));
                        stbf.append(" GUIDKEY, ");
                        sf.append(" ?, ");
                        continue;
                    }
                    if ("nbase".equals(fi.getItemid())) {
                        continue;
                    }
                    list.add(bean.get(fi.getItemid()));
                    stbf.append(" " + fi.getItemid() + ", ");
                    sf.append(" ?, ");
                    continue;
                }
                if ("p1315".equals(fi.getItemid())) {
                    list.add(DateUtils.getTimestamp(beginDate.substring(0, 10), "yyyy-MM-dd"));
                    stbf.append(fi.getItemid() + ",");
                    sf.append("? ,");
                }
                if ("p1317".equals(fi.getItemid())) {
                    list.add(DateUtils.getTimestamp(endDate.substring(0, 10), "yyyy-MM-dd"));
                    stbf.append(fi.getItemid() + ",");
                    sf.append("? ,");
                }
            }
            stbf.append("P1319,P1321,P1323 )");
            sf.append("?,?,? )");
            list.add(0);
            list.add(0);
            list.add(0);
            String sql = stbf.toString();
            sql = sql + "values";
            sql = sql + sf.toString();
            dao.insert(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:beforeDeleMember
     * @Description：删除项目成员前判断是否有存在工时
     * @author liuyang
     * @param projectId
     * @param manId
     * @return
     * @throws GeneralException
     */
    public String beforeDeleMember(String projectId, String manId) throws GeneralException {
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        String result = "0";
        RowSet beforeDeleMemberDetail = null;
        try {
            String checkProject = "select P1511  from p15  where p1301 = (select p1301  from p13  where a0100 = ? and nbase = ? and p1101= ? ) and p1519 in ('1','0') ";
            values.add(manId.substring(3));
            values.add(manId.substring(0, 3));
            values.add(projectId);

            beforeDeleMemberDetail = dao.search(checkProject, values);

            if (!beforeDeleMemberDetail.next())
                result = "1";
        } catch (SQLException e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(beforeDeleMemberDetail);
        }
        return result;
    }

    public String beforeAddMembers(String manIds) throws GeneralException {

        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        String result = "0";
        RowSet beforeDeleMemberDetail = null;
        try {
            String checkProject = "select P1511  from p15  where p1301 in (select p1301  from p13  where "
                    + manIds + " ) and p1519 in ('1','0') ";

            beforeDeleMemberDetail = dao.search(checkProject);

            if (!beforeDeleMemberDetail.next())
                result = "1";
        } catch (SQLException e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(beforeDeleMemberDetail);
        }
        return result;

    }

    /**
     * 
     * @Title:addMembers
     * @Description：即时保存项目成员
     * @author liuyang
     * @param projectId
     * @param menData
     * @param beginDate2
     * @param endDate
     * @throws GeneralException
     */
    public void addMembers(String projectId, ArrayList menData, String beginDate, String endDate)
            throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        ArrayList values = new ArrayList();
        RowSet rs = null;
        try {
            for (int i = 0; i < menData.size(); i++) {
                MorphDynaBean bean = new MorphDynaBean();
                bean = (MorphDynaBean) menData.get(i);
                values.clear();
                String a0100 = PubFunc.decrypt(bean.get("a0100").toString()).substring(3);
                String nbase = PubFunc.decrypt(bean.get("a0100").toString()).substring(0, 3);
                String checkProject = "select P1511  from p15  where p1301 = (select p1301  from p13  where a0100 = ? and nbase = ? and p1101= ? ) and p1519 in ('1','0') ";
                values.add(a0100);
                values.add(nbase);
                values.add(projectId);

                rs = dao.search(checkProject, values);
                if (rs.next()) {
                    updateIsExisMember(projectId, a0100, nbase, endDate);
                } else {
                    ArrayList list = new ArrayList();
                    list.add(bean);
                    savePorjectMembers(list, projectId, beginDate, endDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void updateIsExisMember(String projectId, String a0100, String nbase, String endDate)
            throws GeneralException {
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        StringBuffer sqlStr = new StringBuffer();
        Date date_memberEndTime = null;
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date projectEndDate = DateUtils.getTimestamp(endDate, "yyyy-MM-dd");

            sqlStr.append(" update p13 set p1317 = ? ");
            values.add(projectEndDate);
            sqlStr.append(" where a0100 = ? and nbase = ? and p1101= ? ");
            values.add(a0100);
            values.add(nbase);
            values.add(projectId);
            dao.update(sqlStr.toString(), values);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 更新项目时获取项目的结束时间用以判断结束时间是否改变
     * 
     * @param projectId
     *            项目id
     * @return
     * @throws GeneralException
     */
    private String getEndDate(String projectId) throws GeneralException {
        String endDate = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            String sqlStr = " SELECT P1109 FROM P11 WHERE P1101=? ";
            ArrayList values = new ArrayList();
            values.add(projectId);
            rs = dao.search(sqlStr, values);
            if(rs.next()) {
                Date value = rs.getDate("P1109");
                endDate = DateUtils.format(value, "yyyy-MM-dd");
            }
            
            if(StringUtils.isNotEmpty(endDate))
                endDate = endDate.substring(0, 10);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return endDate;
    }
    /**
     * 获取里程碑
     * 
     * @param p1101
     *            项目编号
     * @param submoduleid
     *            栏目设置编号           
     */
    public ArrayList getChildren(String p1101, String submoduleid) {
        ArrayList childDataList = new ArrayList();
        Pattern pattern = Pattern.compile("[0-9]*");

        try {
            if (StringUtils.isNotEmpty(p1101) && pattern.matcher(p1101).matches()) {
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT P1101,P1201,P1203 P1103,P1205 P1105,P1207 P1107,P1209 P1109,P1211 P1111,P1213 P1113,");
                sql.append("P1215 P1115,P1217 P1117 FROM P12 WHERE P1101=");
                sql.append(p1101);
                sql.append(" ORDER BY P1201");
                ContentDAO dao = new ContentDAO(this.conn);
                ArrayList childrenList = dao.searchDynaList(sql.toString());
                ProjectManageBo bo = new ProjectManageBo(this.userview, this.conn);
                for (int i = 0; i < childrenList.size(); i++) {
                    LazyDynaBean data = (LazyDynaBean) childrenList.get(i);
                    getCodeName(data);
                    bo.DateStyle(data, submoduleid);
                    p1101 = (String) data.get("p1101");
                    
                    StringBuffer newdata = new StringBuffer("");
                    newdata.append("{p1101:'" + PubFunc.encrypt(p1101) + "',");
                    String p1201 = (String) data.get("p1201");
                    if(StringUtils.isNotEmpty(p1201))
                        newdata.append("p1201:'" + PubFunc.encrypt(p1201) + "',");
                    
                    newdata.append("p1103:'" + ((String)data.get("p1103")).replace("'", "\\'") + "',");
                    newdata.append("p1105:'" + ((String)data.get("p1105")).replace("'", "\\'") + "',");
                    newdata.append("p1107:'" + data.get("p1107") + "',");
                    newdata.append("p1109:'" + data.get("p1109") + "',");
                    newdata.append("p1111:'" + data.get("p1111") + "',");
                    newdata.append("p1113:'" + data.get("p1113") + "',");
                    newdata.append("p1115:'" + data.get("p1115") + "',");
                    newdata.append("p1117:'" + data.get("p1117") + "',");
                    newdata.append("leaf:true,");
                    newdata.append("iconCls:'x-tree-landmark-iconCls',");
                    newdata.append("id:'landmark" + PubFunc.encrypt(p1201) + "',");
                    newdata.append("pnodeId:'project" + PubFunc.encrypt(p1101) + "'}");
                    childDataList.add(newdata.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return childDataList;
    }
    
    /**
     * 把代码类转换为文字
     * 
     * @param data
     *            查询出的数据
     */
    private void getCodeName(LazyDynaBean data) {
        try {
            ArrayList<FieldItem> headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
            for (int m = 0; m < headList.size(); m++) {
                FieldItem fi = headList.get(m);
                if (!"p1121".equals(fi.getItemid()) && "0".equals(fi.getCodesetid()))
                    continue;
                
                String itemValue = (String) data.get(fi.getItemid());
                String value = "";

                if(StringUtils.isNotEmpty(itemValue) && itemValue.indexOf("`") > -1)
                    itemValue = itemValue.substring(0, itemValue.indexOf("`"));
                
                if ("p1121".equals(fi.getItemid()) || "UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid())) {
                    value = AdminCode.getCodeName("UN", itemValue);
                    if (StringUtils.isEmpty(value))
                        value = AdminCode.getCodeName("UM", itemValue);
                } else
                    value = AdminCode.getCodeName(fi.getCodesetid(), itemValue);

                data.set(fi.getItemid(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
