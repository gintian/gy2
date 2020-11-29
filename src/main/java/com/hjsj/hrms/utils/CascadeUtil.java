package com.hjsj.hrms.utils;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.util.*;

public class CascadeUtil {
    /**
     * 获取权限范围内的单位名称列表
     * @param userView
     * @param dao
     * @return
     */
    public static List<String> getOrgNameList(UserView userView,ContentDAO dao,String rangecondLike,String flag) {
        List<String> orgNameList = new ArrayList<String>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codesetid = "UN";
        String codeitemid = "";
        String codeitemdesc = "";
        int grade = 0;
        RowSet rs = null;
        try{
            //zhangh 2019-11-4 按照权限不同来查询数据
            if(userView.isSuper_admin()){
                sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                        + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
            }else{
                //权限需要按照 业务范围最优先，其他操作单位，最后人员范围
                String manpriv=userView.getManagePrivCode();
                String manprivv=userView.getManagePrivCodeValue();
                String busiv = userView.getBusiPriv("8");
                String busivStr = "";
                String [] busiArr = null;
                String org_dept = userView.getUnit_id();
                if("UN".equalsIgnoreCase(org_dept)){
                    org_dept = "";
                }
                String org_deptStr = "";
                String [] OrgArr = null;
                //业务模板不勾选按管理范围控制
                if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)&&!"employee".equalsIgnoreCase(flag)){
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                    busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                    for(String str:busiArr){
                        busivStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(busivStr)){
                        busivStr = busivStr.substring(0, busivStr.length()-2);
                        busivStr = " and (" + busivStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(busivStr)){
                        sqlBuffer.append(busivStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");


                }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                    OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                    for(String str:OrgArr){
                        org_deptStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(org_deptStr)){
                        org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                        org_deptStr = " and (" + org_deptStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(org_deptStr)){
                        sqlBuffer.append(org_deptStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(manprivv.length()>0){
                    rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                    if(rs.next()){
                        grade = rs.getInt("grade");
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(rangecondLike)||grade == 1){
                        sqlBuffer.append(" and codeitemid like '"+manprivv+"%' ");
                    }
                    
                    String HighPrivWhere = getHeihtPivWhere(userView);
                    if(StringUtils.isNotEmpty(HighPrivWhere)) {
                    	sqlBuffer.append(HighPrivWhere);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }
                else if(manpriv.length()>=2){
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }
                else{
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                }
            }
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()){
                String space = "";
                codeitemid = rs.getString("codeitemid");
                codeitemdesc = rs.getString("codeitemdesc");
                grade = rs.getInt("grade");
                for(int x=1;x<grade;x++){
                    space +="  ";
                }
                //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                if("employee".equalsIgnoreCase(flag)){
                    orgNameList.add(space + codeitemdesc + "(" + codeitemid + ")");
                }else{
                    orgNameList.add(space + codeitemid + ":" + codeitemdesc);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  orgNameList;
    }
    /**
     * 获取权限范围内的单位列表
     * @param userView
     * @param dao
     * @return
     */
    public static List<String> getOrgList(UserView userView,ContentDAO dao,String rangecondLike,String flag) {
        List<String> orgList = new ArrayList<String>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codesetid = "UN";
        RowSet rs = null;
        int grade = 0;
        try{
            //zhangh 2019-11-4 按照权限不同来查询数据
            if(userView.isSuper_admin()){
                sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                        + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
            }else{
                String manpriv=userView.getManagePrivCode();
                String manprivv=userView.getManagePrivCodeValue();
                String busiv = userView.getBusiPriv("8");
                String busivStr = "";
                String [] busiArr = null;
                String org_dept = userView.getUnit_id();
                if("UN".equalsIgnoreCase(org_dept)){
                    org_dept = "";
                }
                String org_deptStr = "";
                String [] OrgArr = null;
                //业务模板不勾选按管理范围控制
                if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                    busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                    for(String str:busiArr){
                        busivStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(busivStr)){
                        busivStr = busivStr.substring(0, busivStr.length()-2);
                        busivStr = " and (" + busivStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(busivStr)){
                        sqlBuffer.append(busivStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");


                }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                    OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                    for(String str:OrgArr){
                        org_deptStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(org_deptStr)){
                        org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                        org_deptStr = " and (" + org_deptStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(org_deptStr)){
                        sqlBuffer.append(org_deptStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else if(manprivv.length()>0){
                	
                    rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                    if(rs.next()){
                        grade = rs.getInt("grade");
                    }
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(rangecondLike)||grade ==1){
                        sqlBuffer.append(" and codeitemid like '"+manprivv+"%' ");
                    }
                    
                    String HighPrivWhere = getHeihtPivWhere(userView);
                    if(StringUtils.isNotEmpty(HighPrivWhere)) {
                    	sqlBuffer.append(HighPrivWhere);
                    }
                    
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }
                else if(manpriv.length()>=2)
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                else
                    sqlBuffer.append("select codeitemid from organization where 1=2");
            }
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()){
                codeitemid = rs.getString("codeitemid");
                orgList.add(codeitemid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  orgList;
    }
    /**
     * 获取权限范围内的部门列表
     * @param userView
     * @param dao
     * @return
     */
    public static List<String> getDeptList(UserView userView,ContentDAO dao,String rangecondLike,String flag) {
        List<String> deptList = new ArrayList<String>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codesetid = "UM";
        int grade = 0;
        RowSet rs = null;
        try{
            //zhangh 2019-11-4 按照权限不同来查询数据
            if(userView.isSuper_admin()){
                sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                        + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
            }else{
                String manpriv=userView.getManagePrivCode();
                String manprivv=userView.getManagePrivCodeValue();
                String busiv = userView.getBusiPriv("8");
                String busivStr = "";
                String [] busiArr = null;
                String org_dept = userView.getUnit_id();
                if("UN".equalsIgnoreCase(org_dept)){
                    org_dept = "";
                }
                String org_deptStr = "";
                String [] OrgArr = null;
                if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                    busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                    for(String str:busiArr){
                        busivStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(busivStr)){
                        busivStr = busivStr.substring(0, busivStr.length()-2);
                        busivStr = " and (" + busivStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(busivStr)){
                        sqlBuffer.append(busivStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                    OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                    for(String str:OrgArr){
                        org_deptStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(org_deptStr)){
                        org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                        org_deptStr = " and (" + org_deptStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(org_deptStr)){
                        sqlBuffer.append(org_deptStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }	else if(manprivv.length()>0){
                    rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                    if(rs.next()){
                        grade = rs.getInt("grade");
                    }
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(rangecondLike)||grade == 1){
                        sqlBuffer.append(" and codeitemid like '"+manprivv+"%' ");
                    }
                    
                    String HighPrivWhere = getHeihtPivWhere(userView);
                    if(StringUtils.isNotEmpty(HighPrivWhere)) {
                    	sqlBuffer.append(HighPrivWhere);
                    }
                    
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }
                else if(manpriv.length()>=2)
                    sqlBuffer.append("select codeitemid from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                else
                    sqlBuffer.append("select codeitemid from organization where 1=2");
            }
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()){
                codeitemid = rs.getString("codeitemid");
                deptList.add(codeitemid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  deptList;
    }

    /**
     * 获取权限范围内的部门名称列表
     * @param userView
     * @param dao
     * @return
     */
    public static List<String> getDeptNameList(UserView userView,ContentDAO dao,String rangecondLike,String flag) {
        List<String> deptNameList = new ArrayList<String>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codeitemdesc = "";
        String codesetid = "UM";
        int grade = 0;
        RowSet rs = null;
        try{
            //zhangh 2019-11-4 按照权限不同来查询数据
            if(userView.isSuper_admin()){
                sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                        + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
            }else{
                String manpriv=userView.getManagePrivCode();
                String manprivv=userView.getManagePrivCodeValue();
                String busiv = userView.getBusiPriv("8");
                String busivStr = "";
                String [] busiArr = null;
                String org_dept = userView.getUnit_id();
                if("UN".equalsIgnoreCase(org_dept)){
                    org_dept = "";
                }
                String org_deptStr = "";
                String [] OrgArr = null;
                if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                    busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                    for(String str:busiArr){
                        busivStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(busivStr)){
                        busivStr = busivStr.substring(0, busivStr.length()-2);
                        busivStr = " and (" + busivStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(busivStr)){
                        sqlBuffer.append(busivStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                    OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                    for(String str:OrgArr){
                        org_deptStr += " codeitemid like '"+str+"%' or";
                    }
                    if(StringUtils.isNotBlank(org_deptStr)){
                        org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                        org_deptStr = " and (" + org_deptStr + " )";
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(org_deptStr)){
                        sqlBuffer.append(org_deptStr);
                    }
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else if(manprivv.length()>0){
                    rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                    if(rs.next()){
                        grade = rs.getInt("grade");
                    }
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' ");
                    if(StringUtils.isNotBlank(rangecondLike)||grade ==1){
                        sqlBuffer.append(" and codeitemid like '"+manprivv+"%' ");
                    }
                    
                    String HighPrivWhere = getHeihtPivWhere(userView);
                    if(StringUtils.isNotEmpty(HighPrivWhere)) {
                    	sqlBuffer.append(HighPrivWhere);
                    }
                    
                    sqlBuffer.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                }
                else if(manpriv.length()>=2)
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                            + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                else
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
            }
            rs = dao.search(sqlBuffer.toString());
            while (rs.next()){
                codeitemid = rs.getString("codeitemid");
                codeitemdesc = rs.getString("codeitemdesc");
                //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                if("employee".equalsIgnoreCase(flag)){
                    deptNameList.add(codeitemdesc + "(" + codeitemid + ")");
                }else{
                    deptNameList.add(codeitemid + ":" + codeitemdesc);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  deptNameList;
    }

    /**
     * 获取每个单位下对应的下属部门
     * @param userView
     * @param dao
     * @param orgList
     * @param orgNameList
     * @return
     */
    public static Map<String,List<String>> getOrgMap(UserView userView,ContentDAO dao, List<String> orgList, List<String> orgNameList,String rangecondLike,String flag) {
        Map<String,List<String>> map = new LinkedHashMap<String,List<String>>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codeitemdesc = "";
        String space = "";
        String codesetid = "('UN','UM')";
        int grade = 0;
        List<String> deptNameList = null;
        RowSet rs = null;
        try{
            for(int x=0;x<orgList.size();x++){
                //zhangh 2019-11-4 按照权限不同来查询数据
                sqlBuffer.setLength(0);
                if(userView.isSuper_admin()){
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                            + " and codeitemid like '"+orgList.get(x)+"%' and codeitemid <> '"+orgList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else{
                    String manpriv=userView.getManagePrivCode();
                    String manprivv=userView.getManagePrivCodeValue();
                    String busiv = userView.getBusiPriv("8");
                    String busivStr = "";
                    String [] busiArr = null;
                    String org_dept = userView.getUnit_id();
                    if("UN".equalsIgnoreCase(org_dept)){
                        org_dept = "";
                    }
                    String org_deptStr = "";
                    String [] OrgArr = null;
                    if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                                + " and codeitemid like '"+orgList.get(x)+"%' and codeitemid <> '"+orgList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                        busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                        for(String str:busiArr){
                            busivStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(busivStr)){
                            busivStr = busivStr.substring(0, busivStr.length()-2);
                            busivStr = " and (" + busivStr + " )";
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(busivStr)){
                            sqlBuffer.append(busivStr);
                        }
                        sqlBuffer.append(" and codeitemid <> '"+orgList.get(x)+"' and codeitemid like '"+orgList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                    }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                        OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                        for(String str:OrgArr){
                            org_deptStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(org_deptStr)){
                            org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                            org_deptStr = " and (" + org_deptStr + " )";
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(org_deptStr)){
                            sqlBuffer.append(org_deptStr);
                        }
                        sqlBuffer.append(" and codeitemid <> '"+orgList.get(x)+"' and codeitemid like '"+orgList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                    }else if(manprivv.length()>0){
                        rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                        if(rs.next()){
                            grade = rs.getInt("grade");
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(rangecondLike)||grade ==1){
                            sqlBuffer.append("  and codeitemid like '"+manprivv+"%' ");
                        }
                        
                        String HighPrivWhere = getHeihtPivWhere(userView);
                        if(StringUtils.isNotEmpty(HighPrivWhere)) {
                        	sqlBuffer.append(HighPrivWhere);
                        }
                        
                        sqlBuffer.append(" and codeitemid <> '"+orgList.get(x)+"' and codeitemid like '"+orgList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }
                    else if(manpriv.length()>=2)
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                                + " and codeitemid like '"+orgList.get(x)+"%' and codeitemid <> '"+orgList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    else
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                }
                deptNameList = new ArrayList<String>();
                rs = dao.search(sqlBuffer.toString());
                while (rs.next()){
                    space = "";
                    codeitemid = rs.getString("codeitemid");
                    codeitemdesc = rs.getString("codeitemdesc");
                    grade = rs.getInt("grade");
                    for(int y=1;y<grade;y++){
                        space +="  ";
                    }

                    //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                    if("employee".equalsIgnoreCase(flag)){
                        deptNameList.add(space + codeitemdesc + "(" + codeitemid +")");
                    }else{
                        deptNameList.add(space + codeitemid+ ":" + codeitemdesc);
                    }
                }
                map.put(orgNameList.get(x).trim(),deptNameList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  map;
    }
    /**
     * 获取每个部门下对应的直接的岗位
     * @param userView
     * @param dao
     * @param orgList
     * @param orgNameList
     * @return
     */
    public static Map<String,List<String>> getDeptMap(UserView userView,ContentDAO dao,List<String> deptList,List<String> deptNameList,String rangecondLike,String flag) {
        Map<String,List<String>> map = new LinkedHashMap<String,List<String>>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codeitemdesc = "";
        String space = "";
        String codesetid = "('UM','@K')";
        int grade = 0;
        List<String> postNameList = null;
        RowSet rs = null;
        try{
            for(int x=0;x<deptList.size();x++){
                //zhangh 2019-11-4 按照权限不同来查询数据
                sqlBuffer.setLength(0);
                if(userView.isSuper_admin()){
                    sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                            + " and codeitemid like '"+deptList.get(x)+"%' and codeitemid <> '"+deptList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else{
                    String manpriv=userView.getManagePrivCode();
                    String manprivv=userView.getManagePrivCodeValue();
                    String busiv = userView.getBusiPriv("8");
                    String busivStr = "";
                    String [] busiArr = null;
                    String org_dept = userView.getUnit_id();
                    if("UN".equalsIgnoreCase(org_dept)){
                        org_dept = "";
                    }
                    String org_deptStr = "";
                    String [] OrgArr = null;
                    if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                                + " and codeitemid like '"+deptList.get(x)+"%' and codeitemid <> '"+deptList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                    }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                        busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                        for(String str:busiArr){
                            busivStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(busivStr)){
                            busivStr = busivStr.substring(0, busivStr.length()-2);
                            busivStr = " and (" + busivStr + " )";
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(busivStr)){
                            sqlBuffer.append(busivStr);
                        }
                        sqlBuffer.append(" and codeitemid <> '"+deptList.get(x)+"' and codeitemid like '"+deptList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                    }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                        OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                        for(String str:OrgArr){
                            org_deptStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(org_deptStr)){
                            org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                            org_deptStr = " and (" + org_deptStr + " )";
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(org_deptStr)){
                            sqlBuffer.append(org_deptStr);
                        }
                        sqlBuffer.append(" and codeitemid <> '"+deptList.get(x)+"' and codeitemid like '"+deptList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }else if(manprivv.length()>0){
                        rs = dao.search("select grade from organization where codeitemid='"+manprivv+"'");
                        if(rs.next()){
                            grade = rs.getInt("grade");
                        }
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid);
                        if(StringUtils.isNotBlank(rangecondLike)||grade ==1){
                            sqlBuffer.append("  and codeitemid like '"+manprivv+"%' ");
                        }
                        
                        String HighPrivWhere = getHeihtPivWhere(userView);
                        if(StringUtils.isNotEmpty(HighPrivWhere)) {
                        	sqlBuffer.append(HighPrivWhere);
                        }
                        
                        sqlBuffer.append(" and codeitemid <> '"+deptList.get(x)+"' and codeitemid like '"+deptList.get(x)+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }
                    else if(manpriv.length()>=2)
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid in " + codesetid
                                + " and codeitemid like '"+deptList.get(x)+"%' and codeitemid <> '"+deptList.get(x)+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    else
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                }
                postNameList = new ArrayList<String>();
                rs = dao.search(sqlBuffer.toString());
                while (rs.next()){
                    space = "";
                    codeitemid = rs.getString("codeitemid");
                    codeitemdesc = rs.getString("codeitemdesc");
                    grade = rs.getInt("grade");
                    for(int y=1;y<grade;y++){
                        space +="  ";
                    }

                    //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                    if("employee".equalsIgnoreCase(flag)){
                        postNameList.add(space + codeitemdesc + "(" + codeitemid + ")" );
                    }else{
                        postNameList.add(space + codeitemid + ":" + codeitemdesc );
                    }
                }
                map.put(deptNameList.get(x).trim(),postNameList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return  map;
    }

    /**
     * 获取单位下直接挂载的岗位
     * @param userView
     * @param dao
     * @param orgList
     * @param orgNameList
     * @return
     */
    public static List<String> getUnitPost(UserView userView,ContentDAO dao,String rangecondLike,String flag) {
        List<String> unitPostList = new ArrayList<String>();
        StringBuffer sqlBuffer = new StringBuffer();
        String codeitemid = "";
        String codeitemdesc = "";
        String sql = "";
        String space = "  ";
        RowSet rs = null;
        RowSet rsTemp = null;
        boolean isHavePost = false;
        try{
            //获取所有的直接挂岗位的单位
            sql  = "select distinct org2.codeitemid,org2.codeitemdesc from  organization org1" +
                    " left join organization org2 on org1.parentid = org2.codeitemid" +
                    " where org1.codesetid='@K' and org2.codesetid='UN' and "
                    + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between org1.start_date and org1.end_date ";
            rs = dao.search(sql);
            while(rs.next()){
                codeitemid = rs.getString("codeitemid");
                codeitemdesc = rs.getString("codeitemdesc");
                //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                if("employee".equalsIgnoreCase(flag)){
                    unitPostList.add(codeitemdesc + "(" + codeitemid + ")");
                }else{
                    unitPostList.add(codeitemid + ":" + codeitemdesc);
                }

                //zhangh 2019-11-4 按照权限不同来查询数据
                sqlBuffer.setLength(0);
                if(userView.isSuper_admin()){
                    sqlBuffer.append("select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='@K' "
                            + " and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                }else{
                    String manpriv=userView.getManagePrivCode();
                    String manprivv=userView.getManagePrivCodeValue();
                    String busiv = userView.getBusiPriv("8");
                    String busivStr = "";
                    String [] busiArr = null;
                    String org_dept = userView.getUnit_id();
                    if("UN".equalsIgnoreCase(org_dept)){
                        org_dept = "";
                    }
                    String org_deptStr = "";
                    String [] OrgArr = null;
                    if(!"1".equals(rangecondLike)&&!"employee".equalsIgnoreCase(flag)){
                        sqlBuffer.append("select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='@K' "
                                + " and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");

                    }else if(StringUtils.isNotBlank(busiv)&&!"employee".equalsIgnoreCase(flag)){
                        busiArr = busiv.replace("UN", "").replace("UM", "").split("`");
                        for(String str:busiArr){
                            busivStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(busivStr)){
                            busivStr = busivStr.substring(0, busivStr.length()-2);
                            busivStr = " and (" + busivStr + " )";
                        }
                        sqlBuffer.append("select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='@K' ");
                        if(StringUtils.isNotBlank(busivStr)){
                            sqlBuffer.append(busivStr);
                        }
                        sqlBuffer.append(" and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }else if(StringUtils.isNotBlank(org_dept)&&!"employee".equalsIgnoreCase(flag)){
                        OrgArr = org_dept.replace("UN", "").replace("UM", "").split("`");
                        for(String str:OrgArr){
                            org_deptStr += " codeitemid like '"+str+"%' or";
                        }
                        if(StringUtils.isNotBlank(org_deptStr)){
                            org_deptStr = org_deptStr.substring(0, org_deptStr.length()-2);
                            org_deptStr = " and (" + busivStr + " )";
                        }
                        sqlBuffer.append("select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='@K' ");
                        if(StringUtils.isNotBlank(busivStr)){
                            sqlBuffer.append(busivStr);
                        }
                        sqlBuffer.append(" and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }else if(manprivv.length()>0){
                        sqlBuffer.append("select codeitemid,codeitemdesc from organization where parentid='"+codeitemid+"' and codesetid='@K' ");
                        if(StringUtils.isNotBlank(rangecondLike)){
                            sqlBuffer.append("  and codeitemid like '"+manprivv+"%' ");
                        }
                        
                        String HighPrivWhere = getHeihtPivWhere(userView);
                        if(StringUtils.isNotEmpty(HighPrivWhere)) {
                        	sqlBuffer.append(HighPrivWhere);
                        }
                        
                        sqlBuffer.append(" and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                    }
                    else
                        sqlBuffer.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                }
                rsTemp = dao.search(sqlBuffer.toString());
                while (rsTemp.next()){
                    isHavePost = true;
                    codeitemid = rsTemp.getString("codeitemid");
                    codeitemdesc = rsTemp.getString("codeitemdesc");
                    //员工管理和人事异动导出的时候格式不一样，没办法只能兼容一下
                    if("employee".equalsIgnoreCase(flag)){
                        unitPostList.add(space + codeitemdesc + "(" + codeitemid + ")");
                    }else{
                        unitPostList.add(space + codeitemid + ":" + codeitemdesc);
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rsTemp);
            PubFunc.closeDbObj(rs);
        }
        if(!isHavePost){
            unitPostList = new ArrayList<String>();
        }
        return  unitPostList;
    }

    /**
     * 写入单位、部门、岗位数据
     * @param workbook
     * @param wsSheet
     * @param orgNameList
     * @param orgMap
     * @param deptNameList
     * @param deptMap
     * @param isHavePost 是否处理岗位信息
     */
    public static void initInfo(HSSFWorkbook workbook, HSSFSheet wsSheet, List<String> orgNameList, Map<String, List<String>> orgMap, List<String> deptNameList, Map<String, List<String>> deptMap, boolean isHavePost,String flag,List<String> unitPostList) {
        //写入单位数据
        for (int i = 0; i < orgNameList.size(); i++) {
            HSSFRow row = wsSheet.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(orgNameList.get(i));
        }
        wsSheet.setColumnWidth(0, 5000);
        //设置单位名称管理器
        if(orgNameList!=null&&orgNameList.size()>0){
            initNameMapping(workbook, wsSheet.getSheetName(), "单位", 0,1, orgNameList.size());
        }
        //写入部门数据
        int transcolNum = 0;
        int rowCount = 0;
        int referColNum = 0;
        int tempRowCount = wsSheet.getLastRowNum();
        for (int i = 0; i <orgNameList.size(); i++) {
            //另起一行开始写部门
            referColNum = i;
            transcolNum = referColNum;
            if(referColNum>=20){
                transcolNum = referColNum%20;
            }
            //因为有层级关系，所以前面多了空格，需要去除掉
            String orgName = orgNameList.get(i).trim();
            String deptName = "";
            List<String> deptNameTempList = orgMap.get(orgName);
            //写入部门数据
            //获取最后一行的行数
            rowCount = wsSheet.getLastRowNum();
            if(rowCount == 0 && wsSheet.getRow(0) == null ) {
                wsSheet.createRow(0);
            }
            if(referColNum%20==0){
                tempRowCount = wsSheet.getLastRowNum();

            }
            if (CollectionUtils.isNotEmpty(deptNameTempList)) {
                for (int j = 0; j < deptNameTempList.size(); j++) {
                    //office 2003版本，最大支持255列,可以最多使用250列，超出后换行
                    rowCount = wsSheet.getLastRowNum();
                    //前面创建过的行，直接获取行，创建列
                    if (j < rowCount - tempRowCount) {
                        wsSheet.setColumnWidth(transcolNum, 5000);
                        //设置对应单元格的值
                        wsSheet.getRow(tempRowCount + 1 + j).createCell(transcolNum).setCellValue(deptNameTempList.get(j));
                    } else { //未创建过的行，直接创建行、创建列
                        //设置每列的列宽
                        wsSheet.setColumnWidth(transcolNum, 5000);
                        //创建行、创建列
                        //设置对应单元格的值
                        wsSheet.createRow(tempRowCount + 1 + j).createCell(transcolNum).setCellValue(deptNameTempList.get(j)); //设置对应单元格的值
                    }

                }
                //编码值是唯一的，以编码作为管理器的名称
                if("employee".equalsIgnoreCase(flag)){
                    orgName =  orgName.substring(orgName.lastIndexOf("(") + 1);
                    orgName = orgName.replace(")","");
                }else{
                    orgName = orgName.substring(0, orgName.lastIndexOf(":"));
                }
                initNameMapping(workbook, wsSheet.getSheetName(), "UN" + orgName, transcolNum,tempRowCount + 2,tempRowCount + 1 + deptNameTempList.size());
            }

        }

        //写入岗位数据
        if(isHavePost){
            for (int i = 0; i <deptNameList.size(); i++) {
                //另起一行开始写岗位
                referColNum = i;
                transcolNum = referColNum;
                if(referColNum>=20){
                    transcolNum = referColNum%20;
                }
                //因为有层级关系，所以前面多了空格，需要去除掉
                String deptName = deptNameList.get(i).trim();
                String postName = "";
                List<String> postNameTempList = deptMap.get(deptName);
                //获取最后一行的行数
                rowCount = wsSheet.getLastRowNum();
                if(rowCount == 0 && wsSheet.getRow(0) == null ) {
                    wsSheet.createRow(0);
                }
                if(referColNum%20==0){
                    tempRowCount = wsSheet.getLastRowNum();

                }
                if (CollectionUtils.isNotEmpty(postNameTempList)) {
                    for (int j = 0; j < postNameTempList.size(); j++) {
                        //office 2003版本，最大支持255列,可以最多使用250列，超出后换行
                        rowCount = wsSheet.getLastRowNum();
                        //前面创建过的行，直接获取行，创建列
                        if (j < rowCount - tempRowCount) {
                            wsSheet.setColumnWidth(transcolNum, 5000);
                            //设置对应单元格的值
                            wsSheet.getRow(tempRowCount + 1 + j).createCell(transcolNum).setCellValue(postNameTempList.get(j));
                        } else { //未创建过的行，直接创建行、创建列
                            //设置每列的列宽
                            wsSheet.setColumnWidth(transcolNum, 5000);
                            //创建行、创建列
                            //设置对应单元格的值
                            wsSheet.createRow(tempRowCount + 1 + j).createCell(transcolNum).setCellValue(postNameTempList.get(j)); //设置对应单元格的值
                        }

                    }
                    if("employee".equalsIgnoreCase(flag)){
                        deptName =  deptName.substring(deptName.lastIndexOf("(") + 1);
                        deptName = deptName.replace(")","");
                    }else{
                        deptName = deptName.substring(0, deptName.lastIndexOf(":"));
                    }
                    initNameMapping(workbook, wsSheet.getSheetName(), "UM" + deptName, transcolNum,tempRowCount + 2,tempRowCount + 1 + postNameTempList.size());
                }

            }
            tempRowCount = wsSheet.getLastRowNum();
            int tempCount = tempRowCount;
            if(unitPostList!=null&&unitPostList.size()>0){
                for(int x =0;x<unitPostList.size();x++){
                    HSSFRow row = wsSheet.createRow(x + tempRowCount +1);
                    HSSFCell cell = row.createCell(0);
                    cell.setCellValue(unitPostList.get(x));
                }
                tempRowCount = wsSheet.getLastRowNum();
                //单独处理一下单位下面直接挂岗位的问题
                initNameMapping(workbook, wsSheet.getSheetName(), "UM", 0,tempCount + 2,tempCount + unitPostList.size()+1);
            }

        }

    }


    /**
     * 根据数据值确定单元格位置（比如：0-A, 27-AB）
     * @param index
     * @return
     */
    public static String getColumnName(int index) {
        StringBuilder s = new StringBuilder();
        while (index >= 26) {
            s.insert(0, (char) ('A' + index % 26));
            index = index / 26 - 1;
        }
        s.insert(0, (char) ('A' + index));
        return s.toString();
    }


    /**
     * 写入单位、部门、岗位数据
     * @param workbook
     * @param wsSheet
     * @param orgNameList
     * @param orgMap
     * @param deptNameList
     * @param deptMap
     */

    /**
     * 设置名称管理器
     * @param workbook
     * @param wsSheetName
     * @param nameStr
     * @param referColNum
     * @param startRowNum
     * @param endRowNum
     */
    private static void initNameMapping(HSSFWorkbook workbook, String wsSheetName, String nameStr, int referColNum, int startRowNum,int endRowNum) {
        Name name = workbook.createName();
        //有好多特殊字符需要特殊处理一下
        nameStr = strFormat(nameStr);
        // 设置名称
        name.setNameName(nameStr);
        String referColName = getColumnName(referColNum);
        // 设置范围
        String formula = wsSheetName + "!$" + referColName + "$"+startRowNum+":$" + referColName + "$" + endRowNum;
        name.setRefersToFormula(formula);
    }
    private static String strFormat(String nameStr){
        nameStr = nameStr.trim();
/*    	nameStr = nameStr.replace("/", "").replace("／", "").replace("∕", "").replace("（", "").replace("）", "").replace(" ", "").replace("、", "");

  	//判断字符串是不是以数字开头
    	Pattern pattern = Pattern.compile("[0-9]*");
    	Matcher isNum = pattern.matcher(nameStr.charAt(0)+"");
    	if (isNum.matches()) {
    	}*/
        nameStr = "_" + nameStr;

        return nameStr;
    }

    /**
     * 主sheet中下拉框初始化
     *
     * @param mainSheet
     */
    public static void initSheetValidation(HSSFSheet mainSheet,Map<String,Integer> orderMap,boolean isHavePost,String flag,String officeType) {
        String org_name = "_单位";
        String orgLetter = getColumnName(orderMap.get("UN"));
        String deptLetter = getColumnName(orderMap.get("UM"));
        DataValidation orgValidation = getOrgDataValidationByFormula(org_name, orderMap.get("UN"));
        mainSheet.addValidationData(orgValidation);

        DataValidation deptValidation = null;
        DataValidation postValidation = null;
        //zhangh 2019-11-20 根据office，设置不同的公式.1:office2007以上版本，2：office2007及以下版本
        if("2".equals(officeType)){
            for(int x=2;x<1000;x++){
                if("employee".equalsIgnoreCase(flag)){
                    deptValidation = getDataValidationByFormula("INDIRECT(\"_UN\"&TRIM(SUBSTITUTE(RIGHT(SUBSTITUTE($"+orgLetter+x+",\"(\",REPT(\" \",LEN($"+orgLetter+x+"))),LEN($"+orgLetter+x+")),\")\",\"\")))", orderMap.get("UM"),x-1);
                }else{
                    deptValidation = getDataValidationByFormula("INDIRECT(IF(LEN($"+orgLetter+x+")=0,\"_UN\",\"_UN\"&TRIM(LEFT($"+orgLetter+x+",(FIND(\":\",$"+orgLetter+x+")-1)))))", orderMap.get("UM"),x-1);
                }
                // 主sheet添加验证数据
                mainSheet.addValidationData(deptValidation);
                if(isHavePost){
                    if("employee".equalsIgnoreCase(flag)){
                        postValidation = getDataValidationByFormula("INDIRECT(\"_UM\"&TRIM(SUBSTITUTE(RIGHT(SUBSTITUTE($"+deptLetter+x+",\"(\",REPT(\" \",LEN($"+deptLetter+x+"))),LEN($"+deptLetter+x+")),\")\",\"\")))", orderMap.get("@K"),x-1);
                    }else{
                        postValidation = getDataValidationByFormula("INDIRECT(IF(LEN($"+deptLetter+x+")=0,\"_UM\",\"_UM\"&TRIM(LEFT($"+deptLetter+x+",(FIND(\":\",$"+deptLetter+x+")-1)))))", orderMap.get("@K"),x-1);
                    }
                    mainSheet.addValidationData(postValidation);
                }
            }
        }else{
            if ("employee".equalsIgnoreCase(flag)) {
                deptValidation = getOrgDataValidationByFormula("INDIRECT(\"_UN\"&TRIM(SUBSTITUTE(RIGHT(SUBSTITUTE($" + orgLetter + "1,\"(\",REPT(\" \",LEN($" + orgLetter + "1))),LEN($" + orgLetter + "1)),\")\",\"\")))", ((Integer)orderMap.get("UM")).intValue());
            } else {
                deptValidation = getOrgDataValidationByFormula("INDIRECT(IF(LEN($" + orgLetter + "1)=0,\"_UN\",\"_UN\"&TRIM(LEFT($" + orgLetter + "1,(FIND(\":\",$" + orgLetter + "1)-1)))))", ((Integer)orderMap.get("UM")).intValue());
            }
            mainSheet.addValidationData(orgValidation);
            mainSheet.addValidationData(deptValidation);
            if (isHavePost)
            {
                if ("employee".equalsIgnoreCase(flag)) {
                    postValidation = getOrgDataValidationByFormula("INDIRECT(\"_UM\"&TRIM(SUBSTITUTE(RIGHT(SUBSTITUTE($" + deptLetter + "1,\"(\",REPT(\" \",LEN($" + deptLetter + "1))),LEN($" + deptLetter + "1)),\")\",\"\")))", ((Integer)orderMap.get("@K")).intValue());
                } else {
                    postValidation = getOrgDataValidationByFormula("INDIRECT(IF(LEN($" + deptLetter + "1)=0,\"_UM\",\"_UM\"&TRIM(LEFT($" + deptLetter + "1,(FIND(\":\",$" + deptLetter + "1)-1)))))", ((Integer)orderMap.get("@K")).intValue());
                }
                mainSheet.addValidationData(postValidation);
            }
        }

    }
    /**
     * 生成下拉框
     *
     * @param formulaString
     * @param columnIndex
     * @return
     */
    public static DataValidation getOrgDataValidationByFormula(String formulaString, int columnIndex) {
        int max_row = 65535;
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(1, max_row, columnIndex, columnIndex);
        // 数据有效性对象
        DataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
        return dataValidationList;
    }

    /**
     * 生成下拉框
     *
     * @param formulaString
     * @param columnIndex
     * @return
     */
    public static DataValidation getDataValidationByFormula(String formulaString, int columnIndex, int row) {
        //int max_row = 65535;
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(row, row, columnIndex, columnIndex);
        // 数据有效性对象
        DataValidation dataValidation = new HSSFDataValidation(regions, constraint);
        return dataValidation;
    }
    /**
     * 获取人员高级权限的条件
     * @param userView 登录用户
     * @return
     */
    private static String getHeihtPivWhere(UserView userView) {
    	String HighPrivWhere = "";
    	try {
    		String hegihtPiv = userView.getHighPrivExpression();
    		if(StringUtils.isNotEmpty(hegihtPiv)) {
    			HighPrivWhere = userView.getPrivSQLExpression(hegihtPiv, "Usr", false, true, true, null);
    			HighPrivWhere = HighPrivWhere.substring(HighPrivWhere.toLowerCase().indexOf("where") + 5);
    			HighPrivWhere = HighPrivWhere.replace("UsrA01.B0110", "codeitemid");
    			HighPrivWhere = HighPrivWhere.replace("UsrA01.E0122", "codeitemid");
    			HighPrivWhere = " and (" + HighPrivWhere + ")";
    		}
    	} catch (GeneralException e) {
    		e.printStackTrace();
    	}
    			
		return HighPrivWhere;
	}
}
