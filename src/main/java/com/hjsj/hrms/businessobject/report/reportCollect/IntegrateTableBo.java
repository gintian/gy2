package com.hjsj.hrms.businessobject.report.reportCollect;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class IntegrateTableBo {
  private String backdate = "";

  private String reportTypes = "";

  private String tabid = "";

  private String totalnum = "";

  private String unitcode = "";

  private String userName = "";

  private String usrID = "";

  Connection conn = null;

  public IntegrateTableBo(Connection conn) {
    this.conn = conn;
  }

  public IntegrateTableBo(Connection conn, String totalnum) {
    this.conn = conn;
    this.totalnum = totalnum;
  }

  public IntegrateTableBo(Connection conn, String userID, String userName) {
    this.conn = conn;
    this.usrID = userID;
    this.userName = userName;
  }

  public IntegrateTableBo(Connection conn, String userID, String userName,
      String tabid, String unitcode, String reportTypes) {
    this.conn = conn;
    this.usrID = userID;
    this.userName = userName;
    this.tabid = tabid;
    this.unitcode = unitcode;
    this.reportTypes = reportTypes;
  }

  public static void main(String[] arg) {
    String ss = "ad,sdfasdf,";

    // System.out.println(ss.substring(0,ss.lastIndexOf(",")));

  }

  // 删除方案
  public void delScheme(String unitcode, String tabid, int secid, String flag)
      throws GeneralException {
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      dao.delete("delete from IntegrateScheme where unitcode='" + unitcode
          + "' and secid=" + secid + " and flag=" + flag, new ArrayList());
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }

  }

  public String getBackdate() {
    return this.backdate;
  }

  public String getCaculateMethod(RecordVo vo) {
    // 计算方式
    String method = "sum";
    int flag = Integer.parseInt(vo.getString("flag2")); // 1:求和, 2:求均值, 3:求最大值,
    // 4:求最小值, 5:平均人数,默认为求和

    switch (flag) {
      case 1:
        method = "sum";
        break;
      case 2:
        method = "avg";
        break;
      case 3:
        method = "max";
        break;
      case 4:
        method = "min";
        break;
      case 5:
        method = "avg";
        break;
    }
    // 计算方式end
    return method;
  }

  /**
   * 得到综合表的数据
   * 
   * @param condition 条件列表
   * @param tabid
   * @param sortid
   * @param nums 选中的行列标记 aXX :列 bXX:行
   * @param selfUnitcode
   * @param colNum 结果集的列数
   * @return
   */
  public ArrayList getIntegrateTableAnalyseData(String[] condition,
      String tabid, String sortid, String nums, String selfUnitcode,
      int colNum, String userName, String userID, String reportTypes,
      String yearid, String countid, String weekid) {
    String sql = "";
    String num = nums.substring(1);
    ArrayList resultList = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    TnameBo tbo = new TnameBo(this.conn, tabid, userID, userName, "temp");
    RowSet recset = null;
    RowSet recset2 = null;
    String atemp = num.substring(3);
    String[] temps = atemp.split(",");
    try {
      TTorganization organization = new TTorganization(this.conn);
      ArrayList grassRootsUnitList =
          organization.getGrassRootsUnit2(selfUnitcode); // 取得该节点下的基层单位
      if (condition.length > 50 || condition.length * temps.length > 600) {
        int aa = 0;
        int n = 0;
        HashMap map = new HashMap();
        String condition2[] = null;
        boolean flag = false;
        // 定义a值的大小？产生行太多数据库一行记录超过8060长度会报错
        int asize = 0;
        if (nums.indexOf(",a") != -1) // 选中列
        {
          condition2 = new String[condition.length * temps.length + 1];
          asize = 600 / temps.length;
          if (asize > 50) {
            asize = 50;
          }
          if (condition.length < asize) {
            asize = condition.length - 1;
          }
        }
        else if (nums.indexOf(",b") != -1) // 选中行
        {
          if (condition.length > 50) {
            asize = 50;
          }
          else {
            asize = 600 / temps.length;
            if (asize > 50) {
              asize = 50;
            }
            if (condition.length < asize) {
              asize = condition.length - 1;
            }
          }

        }
        for (int a = asize; a < condition.length;) {
          String conditiontemp[] = new String[asize];
          for (int i = 0; i < conditiontemp.length; i++) {
            conditiontemp[i] = condition[i + a - asize];
          }

          if (nums.indexOf(",a") != -1) // 选中列
          {
            sql =
                this.getSelectColumnAnalyseSql(conditiontemp, tabid, sortid,
                    nums, selfUnitcode, grassRootsUnitList, tbo, reportTypes,
                    yearid, countid, weekid);
          }
          else if (nums.indexOf(",b") != -1) // 选中行
          {
            sql =
                this.getSelectRowAnalyseSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, colNum, tbo, reportTypes,
                    yearid, countid, weekid);
          }
          // System.out.println(sql);
          recset = dao.search(sql);

          ResultSetMetaData data = recset.getMetaData();
          n = 0;
          while (recset.next()) {
            n++;
            if (nums.indexOf(",a") != -1) // 选中列
            {
              String[] result = new String[data.getColumnCount() + 1];
              if (map != null && map.get("" + n) != null) {
                condition2 = (String[]) map.get("" + n);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  condition2[i + (a - asize) * temps.length] = "0";
                }
                else {
                  condition2[i + (a - asize) * temps.length] =
                      recset.getString(i);
                }
              }
              map.put("" + n, condition2.clone());
            }
            else {

              String[] result = new String[data.getColumnCount()];

              if (a == asize && !flag) {
                flag = true;
                for (int i = 0; i < result.length; i++) {
                  result[i] = "0";
                }
                resultList.add(result);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  result[i - 1] = "0";
                }
                else {
                  result[i - 1] = recset.getString(i);
                }
              }
              resultList.add(result);
            }
          }
          recset.close();
          if (data != null) {
            data = null;
          }
          a += asize;
          aa = a;

        }
        if (condition.length - (aa - asize) > 0) {
          String conditiontemp[] = new String[condition.length - (aa - asize)];
          for (int i = 0; i < conditiontemp.length; i++) {
            conditiontemp[i] = condition[i + aa - asize];
          }

          if (nums.indexOf(",a") != -1) // 选中列
          {
            sql =
                this.getSelectColumnAnalyseSql(conditiontemp, tabid, sortid,
                    nums, selfUnitcode, grassRootsUnitList, tbo, reportTypes,
                    yearid, countid, weekid);
          }
          else if (nums.indexOf(",b") != -1) // 选中行
          {
            sql =
                this.getSelectRowAnalyseSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, colNum, tbo, reportTypes,
                    yearid, countid, weekid);
          }

          recset = dao.search(sql);
          ResultSetMetaData data = recset.getMetaData();
          n = 0;
          while (recset.next()) {
            n++;
            if (nums.indexOf(",a") != -1) // 选中列
            {
              if (map != null && map.get("" + n) != null) {
                condition2 = (String[]) map.get("" + n);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  condition2[i + (aa - asize) * temps.length] = "0";
                }
                else {
                  condition2[i + (aa - asize) * temps.length] =
                      recset.getString(i);
                }
              }
              map.put("" + n, condition2.clone());
            }
            else {
              String[] result = new String[data.getColumnCount()];
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  result[i - 1] = "0";
                }
                else {
                  result[i - 1] = recset.getString(i);
                }
              }
              resultList.add(result);
            }

          }
          recset.close();
          if (data != null) {
            data = null;
          }
        }

        for (int i = 1; i <= n; i++) {
          if (nums.indexOf(",a") != -1) // 选中列
          {
            resultList.add(map.get("" + i));
          }
          else {

          }
        }
      }
      else {

        if (nums.indexOf(",a") != -1) // 选中列
        {
          sql =
              this.getSelectColumnAnalyseSql(condition, tabid, sortid, nums,
                  selfUnitcode, grassRootsUnitList, tbo, reportTypes, yearid,
                  countid, weekid);
        }
        else if (nums.indexOf(",b") != -1) // 选中行
        {
          sql =
              this.getSelectRowAnalyseSql(condition, tabid, sortid, nums,
                  selfUnitcode, grassRootsUnitList, colNum, tbo, reportTypes,
                  yearid, countid, weekid);
        }

        recset = dao.search(sql);
        ResultSetMetaData data = recset.getMetaData();

        if (nums.indexOf(",b") != -1) // 选中行
        {
          String[] result = new String[data.getColumnCount()];
          for (int i = 0; i < result.length; i++) {
            result[i] = "0";
          }
          resultList.add(result);
        }
        int row = 0;
        while (recset.next()) {
          row++;
          if (nums.indexOf(",a") != -1) // 选中列
          {
            String[] result = new String[data.getColumnCount() + 1];
            for (int i = 1; i <= data.getColumnCount(); i++) {
              if (recset.getString(i) == null) {
                result[i] = "0";
              }
              else {
                ArrayList list =
                    this.getSelectColumnAnalyseSql2(condition, tabid, sortid,
                        nums, selfUnitcode, grassRootsUnitList, tbo,
                        reportTypes, yearid, countid, weekid, i, row);
                if (list != null && list.size() == 2
                    && i == Integer.parseInt(list.get(0).toString())) {
                  String sqlstr = list.get(1).toString();
                  recset2 = dao.search(sqlstr);
                  if (recset2.next()) {
                    result[i] = recset2.getString(1);
                  }
                }
                else {
                  result[i] = recset.getString(i);
                }

              }
            }
            resultList.add(result);
          }
          else {
            String[] result = new String[data.getColumnCount()];
            for (int i = 1; i <= data.getColumnCount(); i++) {
              if (recset.getString(i) == null) {
                result[i - 1] = "0";
              }
              else {
                result[i - 1] = recset.getString(i);
              }
            }
            resultList.add(result);
          }

        }
        if (data != null) {
          data = null;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      // throw GeneralExceptionHandler.Handle(e);
    }

    return resultList;
  }

  /**
   * 得到综合表的数据
   * 
   * @param condition 条件列表
   * @param tabid
   * @param sortid
   * @param nums 选中的行列标记 aXX :列 bXX:行
   * @param selfUnitcode
   * @param colNum 结果集的列数
   * @return
   */
  public ArrayList getIntegrateTableData(String[] condition, String tabid,
      String sortid, String nums, String selfUnitcode, int colNum)
      throws GeneralException {
    String sql = "";
    String num = nums.substring(1);
    ArrayList resultList = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    TnameBo tbo = new TnameBo(this.conn, tabid, this.usrID, this.userName, "");
    RowSet recset = null;
    String atemp = num.substring(3);
    String[] temps = atemp.split(",");
    try {
      TTorganization organization = new TTorganization(this.conn);
      ArrayList grassRootsUnitList =
          organization.getGrassRootsUnit(selfUnitcode); // 取得该节点下的基层单位
      if (condition.length > 50 || condition.length * temps.length > 600) {
        int aa = 0;
        int n = 0;
        HashMap map = new HashMap();
        String condition2[] = null;
        boolean flag = false;
        // 定义a值的大小？产生行太多数据库一行记录超过8060长度会报错
        int asize = 0;
        if (nums.indexOf(",a") != -1) // 选中列
        {
          condition2 = new String[condition.length * temps.length + 1];
          asize = 600 / temps.length;
          if (asize > 50) {
            asize = 50;
          }
          if (condition.length < asize) {
            asize = condition.length - 1;
          }
        }
        else if (nums.indexOf(",b") != -1) // 选中行
        {
          if (condition.length > 50) {
            asize = 50;
          }
          else {
            asize = 600 / temps.length;
            if (asize > 50) {
              asize = 50;
            }
            if (condition.length < asize) {
              asize = condition.length - 1;
            }
          }

        }
        for (int a = asize; a < condition.length;) {
          String conditiontemp[] = new String[asize];
          for (int i = 0; i < conditiontemp.length; i++) {
            conditiontemp[i] = condition[i + a - asize];
          }

          if (nums.indexOf(",a") != -1) // 选中列
          {
            sql =
                this.getSelectColumnSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, tbo);
          }
          else if (nums.indexOf(",b") != -1) // 选中行
          {
            sql =
                this.getSelectRowSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, colNum, tbo);
          }
          // System.out.println(sql);
          recset = dao.search(sql);

          ResultSetMetaData data = recset.getMetaData();
          n = 0;
          while (recset.next()) {
            n++;
            if (nums.indexOf(",a") != -1) // 选中列
            {
              String[] result = new String[data.getColumnCount() + 1];
              if (map != null && map.get("" + n) != null) {
                condition2 = (String[]) map.get("" + n);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  condition2[i + (a - asize) * temps.length] = "0";
                }
                else {
                  condition2[i + (a - asize) * temps.length] =
                      recset.getString(i);
                }
              }
              map.put("" + n, condition2.clone());
            }
            else {

              String[] result = new String[data.getColumnCount()];

              if (a == asize && !flag) {
                flag = true;
                for (int i = 0; i < result.length; i++) {
                  result[i] = "0";
                }
                resultList.add(result);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  result[i - 1] = "0";
                }
                else {
                  result[i - 1] = recset.getString(i);
                }
              }
              resultList.add(result);
            }
          }
          recset.close();
          if (data != null) {
            data = null;
          }
          a += asize;
          aa = a;

        }
        if (condition.length - (aa - asize) > 0) {
          String conditiontemp[] = new String[condition.length - (aa - asize)];
          for (int i = 0; i < conditiontemp.length; i++) {
            conditiontemp[i] = condition[i + aa - asize];
          }

          if (nums.indexOf(",a") != -1) // 选中列
          {
            sql =
                this.getSelectColumnSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, tbo);
          }
          else if (nums.indexOf(",b") != -1) // 选中行
          {
            sql =
                this.getSelectRowSql(conditiontemp, tabid, sortid, nums,
                    selfUnitcode, grassRootsUnitList, colNum, tbo);
          }

          recset = dao.search(sql);
          ResultSetMetaData data = recset.getMetaData();
          n = 0;
          while (recset.next()) {
            n++;
            if (nums.indexOf(",a") != -1) // 选中列
            {
              if (map != null && map.get("" + n) != null) {
                condition2 = (String[]) map.get("" + n);
              }
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  condition2[i + (aa - asize) * temps.length] = "0";
                }
                else {
                  condition2[i + (aa - asize) * temps.length] =
                      recset.getString(i);
                }
              }
              map.put("" + n, condition2.clone());
            }
            else {
              String[] result = new String[data.getColumnCount()];
              for (int i = 1; i <= data.getColumnCount(); i++) {
                if (recset.getString(i) == null) {
                  result[i - 1] = "0";
                }
                else {
                  result[i - 1] = recset.getString(i);
                }
              }
              resultList.add(result);
            }

          }
          recset.close();
          if (data != null) {
            data = null;
          }
        }

        for (int i = 1; i <= n; i++) {
          if (nums.indexOf(",a") != -1) // 选中列
          {
            resultList.add(map.get("" + i));
          }
          else {

          }
        }
      }
      else {
        if (nums.indexOf(",a") != -1) // 选中列
        {
          sql =
              this.getSelectColumnSql(condition, tabid, sortid, nums,
                  selfUnitcode, grassRootsUnitList, tbo);
        }
        else if (nums.indexOf(",b") != -1) // 选中行
        {
          sql =
              this.getSelectRowSql(condition, tabid, sortid, nums,
                  selfUnitcode, grassRootsUnitList, colNum, tbo);
        }
        // System.out.println(sql);
        recset = dao.search(sql);
        ResultSetMetaData data = recset.getMetaData();

        if (nums.indexOf(",b") != -1) // 选中行
        {
          String[] result = new String[data.getColumnCount()];
          for (int i = 0; i < result.length; i++) {
            result[i] = "0";
          }
          resultList.add(result);
        }

        while (recset.next()) {

          if (nums.indexOf(",a") != -1) // 选中列
          {
            String[] result = new String[data.getColumnCount() + 1];
            for (int i = 1; i <= data.getColumnCount(); i++) {
              if (recset.getString(i) == null) {
                result[i] = "0";
              }
              else {
                result[i] = recset.getString(i);
              }
            }
            resultList.add(result);
          }
          else {
            String[] result = new String[data.getColumnCount()];
            for (int i = 1; i <= data.getColumnCount(); i++) {
              if (recset.getString(i) == null) {
                result[i - 1] = "0";
              }
              else {
                result[i - 1] = recset.getString(i);
              }
            }
            resultList.add(result);
          }

        }
        if (data != null) {
          data = null;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new GeneralException(e.getMessage());
    }
    finally {
      if (recset != null) {
        try {
          recset.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return resultList;
  }

  // 根据备选项得到详细子项列表
  // flag 2:DynaBean 1:CommonData
  public ArrayList getLeftFields(String prepareID, String flag)
      throws GeneralException {
    ArrayList list = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      if ("##".equals(prepareID.substring(0, 2))) {
        StringBuffer ext_sql = new StringBuffer();

        Calendar d = Calendar.getInstance();
        int yy = d.get(Calendar.YEAR);
        int mm = d.get(Calendar.MONTH) + 1;
        int dd = d.get(Calendar.DATE);
        if (this.backdate != null && this.backdate.trim().length() > 0) {
          d.setTime(Date.valueOf(this.backdate));
          yy = d.get(Calendar.YEAR);
          mm = d.get(Calendar.MONTH) + 1;
          dd = d.get(Calendar.DATE);
        }

        ext_sql.append(" and ( " + Sql_switcher.year("end_date") + ">" + yy);
        ext_sql.append(" or ( " + Sql_switcher.year("end_date") + "=" + yy
            + " and " + Sql_switcher.month("end_date") + ">" + mm + " ) ");
        ext_sql.append(" or ( " + Sql_switcher.year("end_date") + "=" + yy
            + " and " + Sql_switcher.month("end_date") + "=" + mm + " and "
            + Sql_switcher.day("end_date") + ">=" + dd + " ) ) ");

        ext_sql.append(" and ( " + Sql_switcher.year("start_date") + "<" + yy);
        ext_sql.append(" or ( " + Sql_switcher.year("start_date") + "=" + yy
            + " and " + Sql_switcher.month("start_date") + "<" + mm + " ) ");
        ext_sql.append(" or ( " + Sql_switcher.year("start_date") + "=" + yy
            + " and " + Sql_switcher.month("start_date") + "=" + mm + " and "
            + Sql_switcher.day("start_date") + "<=" + dd + " ) ) "); 
        ext_sql.append("  order by a0000  ");//xiegh 20170331 add 填报单位排序

        recset =
            dao.search("select * from tt_organization where  (parentid like '"
                + prepareID.substring(2) + "%' or unitcode='"
                + prepareID.substring(2)
                + "') and reporttypes is not null and start_date<end_date"
                + ext_sql);
        while (recset.next()) {
          String value = recset.getString("unitcode");
          String name =
              recset.getString("unitcode") + ":" + recset.getString("unitname");
          if ("1".equals(flag)) {
            CommonData aCommonData = new CommonData(value, name);
            list.add(aCommonData);
          }
          else {
            DynaBean a_bean = new LazyDynaBean();
            a_bean.set("value", value);
            a_bean.set("name", name);
            list.add(a_bean);
          }
        }
      }
      else if ("#$".equals(prepareID.substring(0, 2))) {
        list =
            this.getPigeonHoleList(this.tabid, this.unitcode, this.reportTypes,
                1);
      }
      else {

        String[] info = prepareID.split("##");
        // System.out.println("select * from codeitem where codesetid='"+info[0]+"'");
        recset =
            dao.search("select * from codeitem where codesetid='" + info[0]
                + "'");
        while (recset.next()) {
          String value = recset.getString("codeitemid");
          String name =
              recset.getString("codeitemid") + ":"
                  + recset.getString("codeitemdesc");
          if ("1".equals(flag)) {
            CommonData aCommonData = new CommonData(value, name);
            list.add(aCommonData);
          }
          else {
            DynaBean a_bean = new LazyDynaBean();
            a_bean.set("value", value);
            a_bean.set("name", name);
            list.add(a_bean);
          }

        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
    return list;
  }

  // 选年度
  public ArrayList getPigeonHoleList(String tabid, String unitcode,
      String reportTypes, int flag) throws GeneralException {
    ArrayList provisionTermList = new ArrayList();
    ArrayList provisionTermList2 = new ArrayList();
    // 报表归档类型为年报
    StringBuffer sql = new StringBuffer();
    if ("1".equals(reportTypes)) {// 一般
      sql.append("select DISTINCT  yearid ,countid from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }
    else if ("2".equals(reportTypes)) {// 年
      sql.append("select DISTINCT  yearid,countid  from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }
    else if ("3".equals(reportTypes)) {// 半年
      sql.append("select DISTINCT  yearid  ,countid from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }
    else if ("4".equals(reportTypes)) {// 季报
      sql.append("select DISTINCT yearid,countid  from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }
    else if ("5".equals(reportTypes)) {// 月报
      sql.append("select DISTINCT  yearid ,countid from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }
    else if ("6".equals(reportTypes)) {// 周报
      sql.append("select DISTINCT  yearid ,countid ,weekid from ta_");
      sql.append(tabid);
      sql.append(" where unitcode = '");
      sql.append(unitcode);
      sql.append("'");
      sql.append("  order by yearid");
    }

    RowSet recset = null;
    ContentDAO dao = new ContentDAO(this.conn);
    String yearid = "";
    String countid = "";
    try {
      recset = dao.search(sql.toString());
      while (recset.next()) {
        DynaBean a_bean = new LazyDynaBean();
        yearid = String.valueOf(recset.getInt("yearid"));
        countid = String.valueOf(recset.getInt("countid"));
        if ("1".equals(reportTypes)) {// 一般
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";");
          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year") + countid
              + ResourceFactory.getProperty("hmuster.label.count"));
        }
        else if ("2".equals(reportTypes)) {// 年
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";");
          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year"));
        }
        else if ("3".equals(reportTypes)) {// 半年
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";");
          if ("1".equals(countid)) {
            countid =
                ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
          }
          else {
            countid =
                ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
          }

          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year") + countid);

        }
        else if ("4".equals(reportTypes)) {// 季报
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";");
          if ("1".equals(countid)) {
            countid =
                ResourceFactory.getProperty("report.pigionhole.oneQuarter");
          }
          else if ("2".equals(countid)) {
            countid =
                ResourceFactory.getProperty("report.pigionhole.twoQuarter");
          }
          else if ("3".equals(countid)) {
            countid =
                ResourceFactory.getProperty("report.pigionhole.threeQuarter");
          }
          else if ("4".equals(countid)) {
            countid =
                ResourceFactory.getProperty("report.pigionhole.fourQuarter");
          }

          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year") + countid);
        }
        else if ("5".equals(reportTypes)) {// 月报
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";");
          if ("1".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.january");
          }
          else if ("2".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.february");
          }
          else if ("3".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.march");
          }
          else if ("4".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.april");
          }
          else if ("5".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.may");
          }
          else if ("6".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.june");
          }
          else if ("7".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.july");
          }
          else if ("8".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.auguest");
          }
          else if ("9".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.september");
          }
          else if ("10".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.october");
          }
          else if ("11".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.november");
          }
          else if ("12".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.december");
          }

          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year") + countid);
        }
        else if ("6".equals(reportTypes)) {// 周报
          String weekid = String.valueOf(recset.getInt("weekid"));
          a_bean.set("value", yearid + ";" + countid + ";" + reportTypes + ";"
              + weekid);

          if ("1".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.january");
          }
          else if ("2".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.february");
          }
          else if ("3".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.march");
          }
          else if ("4".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.april");
          }
          else if ("5".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.may");
          }
          else if ("6".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.june");
          }
          else if ("7".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.july");
          }
          else if ("8".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.auguest");
          }
          else if ("9".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.september");
          }
          else if ("10".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.october");
          }
          else if ("11".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.november");
          }
          else if ("12".equals(countid)) {
            countid = ResourceFactory.getProperty("date.month.december");
          }
          if ("1".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.one.week");
          }
          else if ("2".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.two.week");
          }
          else if ("3".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.three.week");
          }
          else if ("4".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.four.week");
          }
          else if ("5".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.five.week");
          }
          else if ("6".equals(weekid)) {
            weekid =
                ResourceFactory.getProperty("performance.workdiary.six.week");
          }
          a_bean.set("name", yearid
              + ResourceFactory.getProperty("columns.archive.year") + countid
              + weekid);
        }

        provisionTermList.add(a_bean);
        // System.out.println(a_bean.get("value").toString()+"--"+a_bean.get("name").toString());
        CommonData aCommonData =
            new CommonData(a_bean.get("value").toString(), a_bean.get("name")
                .toString());
        // CommonData aCommonData=new CommonData("2005;1;2;","2005年");
        provisionTermList2.add(aCommonData);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }

    if (flag == 1) {
      return provisionTermList2;
    }
    else {
      return provisionTermList;
    }
  }

  // 待选条件列表
  public ArrayList getProvisionTermList(String unitcode)
      throws GeneralException {
    ArrayList provisionTermList = new ArrayList();
    DynaBean bean = new LazyDynaBean();
    bean.set("value", "#$" + unitcode);
    bean.set("flag2", "1");
    bean.set("name", ResourceFactory.getProperty("report.appealtime"));
    provisionTermList.add(bean);

    bean = new LazyDynaBean();
    bean.set("value", "##" + unitcode);
    bean.set("flag2", "0");
    bean.set("name", ResourceFactory.getProperty("report.appealUnit"));

    provisionTermList.add(bean);

    return provisionTermList;
  }

  // 待选条件列表
  public ArrayList getProvisionTermList(String tabid, String unitcode)
      throws GeneralException {
    ArrayList provisionTermList = new ArrayList();
    DynaBean bean = new LazyDynaBean();
    bean.set("value", "##" + unitcode);
    bean.set("name", ResourceFactory.getProperty("report.appealUnit"));
    provisionTermList.add(bean);
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      recset =
          dao
              .search("select codesetid,codesetdesc,b.paramename from codeset a ,"
                  + "(select paramcode,paramename from tparam  where paramscope=2 and paramname in (select hz from tpage where flag=9 and tabid="
                  + tabid
                  + ") and paramtype='代码'"
                  + " union select paramcode,paramename from tparam where paramscope=0 and paramtype='代码' "
                  + " union select paramcode,paramename from tparam where paramscope=1 and  paramname in (select hz from tpage where flag=9 and tabid in ( "
                  + " select tabid from tname where tsortid=(select tsortid from tname where tabid="
                  + tabid
                  + ") ) ) and paramtype='代码' ) b "
                  + " where a.codesetid=b.paramcode");
      while (recset.next()) {
        DynaBean a_bean = new LazyDynaBean();
        a_bean.set("value", recset.getString("codesetid") + "##"
            + recset.getString("paramename"));
        a_bean.set("name", recset.getString("codesetdesc"));
        provisionTermList.add(a_bean);

      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
    return provisionTermList;
  }

  // 得到方案具体内容
  public String getSchemeContent(String unitcode, String tabid, String secid,
      String flag) throws GeneralException {
    String content = "";
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      recset =
          dao.search("select content from IntegrateScheme where secid=" + secid
              + " and tabid=" + tabid + " and unitcode='" + unitcode
              + "' and flag=" + flag);
      if (recset.next()) {
        content = Sql_switcher.readMemo(recset, "content");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
    return content;
  }

  // 得到方案列表
  public ArrayList getSchemeList(String unitcode, String tabid, String flag)
      throws GeneralException {
    ArrayList schemeList = new ArrayList();
    DynaBean bean = new LazyDynaBean();
    bean.set("value", "0");
    bean.set("name", " ");
    schemeList.add(bean);
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      this.isExistIntegrateSchemeTable();

      recset =
          dao.search("select secid from IntegrateScheme where unitcode='"
              + unitcode + "' and tabid=" + tabid + " and flag=" + flag);
      while (recset.next()) {
        DynaBean a_bean = new LazyDynaBean();
        a_bean.set("value", recset.getString("secid"));
        a_bean.set("name", ResourceFactory.getProperty("report.project")
            + recset.getString("secid"));
        schemeList.add(a_bean);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
    return schemeList;
  }

  // 得到选中列的综合表的sql语句
  public String getSelectColumnAnalyseSql(String[] condition, String tabid,
      String sortid, String num, String selfUnitcode,
      ArrayList grassRootsUnitList, TnameBo tnameBo, String reportTypes,
      String yearid, String countid, String weekid) {
    // System.out.println(condition.toString()+"  "+tabid+"   "+sortid+"  "+num+"   "+selfUnitcode+"   "+grassRootsUnitList.size());

    StringBuffer sql = new StringBuffer("");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    String atemp = num.substring(3);
    String[] temps = atemp.split(",");
    String columnname = " ";
    if ("".equals(yearid)) {
      columnname += " and yearid=1";
    }
    else {
      columnname += " and yearid=" + yearid;
    }
    if ("".equals(countid)) {
      columnname += " and countid=1";
    }
    else {
      columnname += " and countid=" + countid;
    }
    if (!"".equals(weekid)) {
      columnname += " and weekid=" + weekid;
    }
    try {

      StringBuffer units = new StringBuffer("");
      for (Iterator t = grassRootsUnitList.iterator(); t.hasNext();) {
        RecordVo temp = (RecordVo) t.next();
        units.append(",'");
        units.append(temp.getString("unitcode"));
        units.append("'");
      }
      // units.append(",'");
      // units.append(selfUnitcode);
      // units.append("'");
      // System.out.println(units.toString());
      ArrayList sqlList = new ArrayList();
      for (int i = 0; i < condition.length; i++) {
        String a_condition = (String) condition[i];
        String[] condition_arr = a_condition.split(":");
        if (a_condition.indexOf(";") == -1) { // 填报单位
          if ("2".equals(condition_arr[3])) // 总计
          {

            StringBuffer a_sql = new StringBuffer("(select ");
            StringBuffer t_str = new StringBuffer("");
            for (int j = 0; j < temps.length; j++) {
              RecordVo vo =
                  (RecordVo) tnameBo.getRowInfoBGrid().get(
                      Integer.parseInt(temps[j].substring(1)));

              String method = "sum";

              if (vo.getString("archive_item") != null
                  && !"".equals(vo.getString("archive_item"))
                  && !" ".equals(vo.getString("archive_item"))) {
                t_str.append("," + method + "(" + vo.getString("archive_item")
                    + ") value" + i + j);
              }
              else {
                t_str.append("," + method + "(c"
                    + (Integer.parseInt(temps[j].substring(1)) + 1) + ") value"
                    + i + j);
              }
            }
            a_sql.append(t_str.substring(1) + ",secid  from ta_" + tabid
                + " where unitcode in (");
            a_sql.append(units.substring(1));
            a_sql.append(" )  " + columnname + " group by secid ) a" + i);
            sqlList.add(a_sql.toString());

          }
          else // 合并 或 选择
          {
            StringBuffer a_sql = new StringBuffer("");
            if (condition_arr.length == 4) // unit条件
            {
              int size = condition_arr[2].split(",").length;
              a_sql.append("(select ");
              StringBuffer t_sr = new StringBuffer("");
              for (int j = 0; j < temps.length; j++) {
                RecordVo vo =
                    (RecordVo) tnameBo.getRowInfoBGrid().get(
                        Integer.parseInt(temps[j].substring(1)));
                // 计算方式
                String method = "sum";
                int a_num = Integer.parseInt(temps[j].substring(1));
                if ("1".equals(condition_arr[3])) { // 合并
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append("," + method + "("
                        + vo.getString("archive_item") + ")");
                  }
                  else {
                    t_sr.append("," + method + "(c" + (a_num + 1) + ")");
                  }
                }
                else if ("0".equals(condition_arr[3])) { // 选择
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append("," + vo.getString("archive_item"));
                  }
                  else {
                    t_sr.append(",c" + (a_num + 1));
                  }
                }
                else if ("5".equals(condition_arr[3])) { // 平均
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append("," + method + "("
                        + vo.getString("archive_item") + ")/" + size + "");
                  }
                  else {
                    t_sr.append("," + method + "(c" + (a_num + 1) + ")/" + size
                        + "");
                  }
                }
                else if ("4".equals(condition_arr[3])) { // 最大
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append(",max(" + vo.getString("archive_item") + ")");
                  }
                  else {
                    t_sr.append(",max(c" + (a_num + 1) + ")");
                  }
                }
                else if ("3".equals(condition_arr[3])) { // 最小
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append(",min(" + vo.getString("archive_item") + ")");
                  }
                  else {
                    t_sr.append(",min(c" + (a_num + 1) + ")");
                  }
                }
                t_sr.append(" value" + i + j);
              }
              a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                  + " where unitcode");
              if ("1".equals(condition_arr[3])) { // 合并
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )  " + columnname
                    + "   group by secid ");
              }
              else if ("0".equals(condition_arr[3])) {
                a_sql.append("=" + condition_arr[2] + "  " + columnname + " ");
              }
              else if ("5".equals(condition_arr[3])) { // 平均
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )  " + columnname
                    + "   group by secid ");
              }
              else if ("4".equals(condition_arr[3])) { // 最大
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )  " + columnname
                    + "   group by secid ");
              }
              else if ("3".equals(condition_arr[3])) { // 最小
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )  " + columnname
                    + "   group by secid ");
              }
              a_sql.append(" ) a" + i);
            }

            sqlList.add(a_sql.toString());

          }

        }
        else {
          if ("2".equals(condition_arr[3])) // 总计
          {

            StringBuffer a_sql = new StringBuffer("(select ");
            StringBuffer t_str = new StringBuffer("");
            for (int j = 0; j < temps.length; j++) {
              RecordVo vo =
                  (RecordVo) tnameBo.getRowInfoBGrid().get(
                      Integer.parseInt(temps[j].substring(1)));
              // 计算方式
              String cexpr2 = vo.getString("cexpr2");
              String method = this.getSumFlag(cexpr2); // 1:求和, 2:求均值, 3:求最大值,
              // 4:求最小值, 5:平均人数,默认为求和

              // String method=getCaculateMethod(vo);
              if ("avg".equalsIgnoreCase(method)) {
                if (vo.getString("archive_item") != null
                    && !"".equals(vo.getString("archive_item"))
                    && !" ".equals(vo.getString("archive_item"))) {
                  t_str.append(",sum(" + vo.getString("archive_item") + ")/"
                      + this.totalnum + " value" + i + j);
                }
                else {
                  t_str.append(",sum(c"
                      + (Integer.parseInt(temps[j].substring(1)) + 1) + ")/"
                      + this.totalnum + " value" + i + j);
                }
              }
              else {
                if (vo.getString("archive_item") != null
                    && !"".equals(vo.getString("archive_item"))
                    && !" ".equals(vo.getString("archive_item"))) {
                  t_str.append("," + method + "("
                      + vo.getString("archive_item") + ") value" + i + j);
                }
                else {
                  t_str.append("," + method + "(c"
                      + (Integer.parseInt(temps[j].substring(1)) + 1)
                      + ") value" + i + j);
                }
              }
            }
            a_sql.append(t_str.substring(1) + ",secid  from ta_" + tabid
                + " where unitcode='" + selfUnitcode + "' ");// 当前单位
            a_sql.append("  group by secid ) a" + i);
            sqlList.add(a_sql.toString());

          }
          else // 合并 或 选择
          {
            StringBuffer a_sql = new StringBuffer("");

            if (condition_arr.length == 4) // unit条件
            {

              int size = condition_arr[2].split(",").length;
              a_sql.append("(select ");
              StringBuffer t_sr = new StringBuffer("");
              for (int j = 0; j < temps.length; j++) {
                int a_num = Integer.parseInt(temps[j].substring(1));
                RecordVo vo =
                    (RecordVo) tnameBo.getRowInfoBGrid().get(
                        Integer.parseInt(temps[j].substring(1)));
                String cexpr2 = vo.getString("cexpr2");
                String method = this.getSumFlag(cexpr2); // 1:求和, 2:求均值, 3:求最大值,
                // 4:求最小值, 5:平均人数,默认为求和
                if ("5".equals(condition_arr[3])) {
                  method = "avg";
                }
                else if ("4".equals(condition_arr[3])) {
                  method = "max";
                }
                else if ("3".equals(condition_arr[3])) {
                  method = "min";
                }
                // String method=getCaculateMethod(vo);
                if ("1".equals(condition_arr[3])
                    || "5".equals(condition_arr[3])
                    || "4".equals(condition_arr[3])
                    || "3".equals(condition_arr[3])) { // 合并,平均，最大，最小
                  if ("avg".equalsIgnoreCase(method)) {
                    if (vo.getString("archive_item") != null
                        && !"".equals(vo.getString("archive_item"))
                        && !" ".equals(vo.getString("archive_item"))) {
                      t_sr.append(",sum(" + vo.getString("archive_item") + ")/"
                          + size + "");
                    }
                    else {
                      t_sr.append(",sum(c" + (a_num + 1) + ")/" + size + "");
                    }
                  }
                  else {
                    if (vo.getString("archive_item") != null
                        && !"".equals(vo.getString("archive_item"))
                        && !" ".equals(vo.getString("archive_item"))) {
                      t_sr.append("," + method + "("
                          + vo.getString("archive_item") + ")");
                    }
                    else {
                      t_sr.append("," + method + "(c" + (a_num + 1) + ")");
                    }
                  }
                }
                else if ("0".equals(condition_arr[3])) // 选择
                {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    t_sr.append("," + vo.getString("archive_item"));
                  }
                  else {
                    t_sr.append(",c" + (a_num + 1));
                  }
                }
                t_sr.append(" value" + i + j);
              }
              // a_sql.append(t_sr.substring(1)+",secid  from ta_"+tabid+" where yearid");
              if ("1".equals(condition_arr[3]) || "5".equals(condition_arr[3])
                  || "4".equals(condition_arr[3])
                  || "3".equals(condition_arr[3])) { // 合并,平均，最大，最小

                String condition2[] = condition_arr[2].split(",");
                if (condition2[0].indexOf(";") == -1) {
                  continue;
                }
                String condition22[] = condition2[0].split(";");
                if ("2".equals(condition22[2])) {
                  a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                      + " where yearid in (");
                  for (int a = 0; a < condition2.length - 1; a++) {
                    if (condition2[a].indexOf(";") != -1) {
                      String conditiontemp[] = condition2[a].split(";");
                      a_sql.append(conditiontemp[0] + ",");
                    }

                  }

                  String conditiontemp[] =
                      condition2[condition2.length - 1].split(";");
                  a_sql.append(conditiontemp[0] + ")and unitcode='"
                      + selfUnitcode + "' group by secid ");

                }
                else if ("6".equals(condition22[2])) {

                  a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                      + " where (");
                  for (int a = 0; a < condition2.length - 1; a++) {
                    String conditiontemp[] = condition2[a].split(";");
                    a_sql.append(" (yearid=" + conditiontemp[0]
                        + " and countid=" + conditiontemp[1] + " and weekid="
                        + conditiontemp[3] + ") or");
                  }
                  String conditiontemp[] =
                      condition2[condition2.length - 1].split(";");
                  a_sql.append(" (yearid=" + conditiontemp[0] + " and countid="
                      + conditiontemp[1] + " and weekid=" + conditiontemp[3]
                      + ")) ");
                  a_sql.append("  and unitcode='" + selfUnitcode
                      + "' group by secid ");

                }
                else {

                  a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                      + " where (");
                  for (int a = 0; a < condition2.length - 1; a++) {
                    String conditiontemp[] = condition2[a].split(";");
                    a_sql.append(" (yearid=" + conditiontemp[0]
                        + " and countid=" + conditiontemp[1] + " ) or");
                  }
                  String conditiontemp[] =
                      condition2[condition2.length - 1].split(";");
                  a_sql.append(" (yearid=" + conditiontemp[0] + " and countid="
                      + conditiontemp[1] + " )) ");
                  a_sql.append("  and unitcode='" + selfUnitcode
                      + "' group by secid ");

                }
                // a_sql.append(" in ( "+condition_arr[2].substring(0,condition_arr[2].lastIndexOf(","))+" ) and unitcode='"+selfUnitcode+"' group by secid ");
              }
              else if ("0".equals(condition_arr[3])) { // 选择

                String condition2[] = condition_arr[2].split(";");
                if (!"6".equals(condition2[2])) {
                  a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                      + " where yearid");
                  a_sql.append("=" + condition2[0] + " and unitcode='"
                      + selfUnitcode + "' and countid=" + condition2[1]);
                }
                else {
                  a_sql.append(t_sr.substring(1) + ",secid  from ta_" + tabid
                      + " where yearid");
                  a_sql.append("=" + condition2[0] + " and unitcode='"
                      + selfUnitcode + "' and countid=" + condition2[1]
                      + " and weekid=" + condition2[3]);
                }
              }
              a_sql.append(" ) a" + i);
            }

            sqlList.add(a_sql.toString());

          }
        }
      }
      StringBuffer sql_s = new StringBuffer("");
      StringBuffer sql_f = new StringBuffer("");
      StringBuffer sql_w = new StringBuffer("");
      for (int i = 0; i < sqlList.size(); i++) {
        for (int j = 0; j < temps.length; j++) {
          // RecordVo
          // vo=(RecordVo)tnameBo.getRowInfoBGrid().get(Integer.parseInt(temps[j].substring(1)));
          // if(vo.getString("archive_item")!=null&&!vo.getString("archive_item").equals("")&&!vo.getString("archive_item").equals(" "))
          // t_str.append(",sum("+vo.getString("archive_item")+") value"+i+j);
          sql_s.append(",a" + i + ".value" + i + j);
        }
        sql_f.append(" left outer join  " + (String) sqlList.get(i)
            + " on a00.secid=a" + i + ".secid ");
      }
      sql.append("select " + sql_s.substring(1)
          + " from  (select distinct secid from ta_" + tabid + " ) a00 ");
      sql.append(sql_f.toString());
      sql.append(" order by a00.secid");
    }

    catch (Exception e) {
      e.printStackTrace();
      // throw GeneralExceptionHandler.Handle(e);
    }
    // System.out.println("---------"+sql.toString());
    return sql.toString();
  }

  // 获得选中列单元格和sql语句
  public ArrayList getSelectColumnAnalyseSql2(String[] condition, String tabid,
      String sortid, String num, String selfUnitcode,
      ArrayList grassRootsUnitList, TnameBo tnameBo, String reportTypes,
      String yearid, String countid, String weekid, int col, int row) {

    int n = 0;
    ArrayList list = new ArrayList();
    String atemp = num.substring(3);
    String[] temps = atemp.split(",");
    String columnname = " ";
    if ("".equals(yearid)) {
      columnname += " and yearid=1";
    }
    else {
      columnname += " and yearid=" + yearid;
    }
    if ("".equals(countid)) {
      columnname += " and countid=1";
    }
    else {
      columnname += " and countid=" + countid;
    }
    if (!"".equals(weekid)) {
      columnname += " and weekid=" + weekid;
    }
    if (row - 1 >= tnameBo.getColInfoBGrid().size()) {
      return list;
    }
    try {
      for (int i = 0; i < condition.length; i++) {

        String a_condition = (String) condition[i];
        String[] condition_arr = a_condition.split(":");
        if (a_condition.indexOf(";") == -1) {
          list.add("");
          break;
        }
        else {
          if ("2".equals(condition_arr[3])) // 总计
          {

            StringBuffer t_str = new StringBuffer("");
            for (int j = 0; j < temps.length; j++) {
              n++;
              if (col == n) {
                RecordVo vo =
                    (RecordVo) tnameBo.getRowInfoBGrid().get(
                        Integer.parseInt(temps[j].substring(1)));
                // 计算方式
                String cexpr2 = vo.getString("cexpr2");
                RecordVo vo2 =
                    (RecordVo) tnameBo.getColInfoBGrid().get(row - 1);
                int r_flag = this.getSumFlag2(cexpr2);
                cexpr2 = vo2.getString("cexpr2");
                int c_flag = this.getSumFlag2(cexpr2);
                if (c_flag > r_flag) {
                  String method = this.getSumFlag(cexpr2); // 1:求和, 2:求均值,
                  // 3:求最大值, 4:求最小值,
                  // 5:平均人数,默认为求和
                  if ("avg".equalsIgnoreCase(method)) {
                    if (vo.getString("archive_item") != null
                        && !"".equals(vo.getString("archive_item"))
                        && !" ".equals(vo.getString("archive_item"))) {
                      t_str.append("sum(" + vo.getString("archive_item") + ")/"
                          + this.totalnum + " value" + i + j);
                    }
                    else {
                      t_str.append("sum(c"
                          + (Integer.parseInt(temps[j].substring(1)) + 1)
                          + ")/" + this.totalnum + " value" + i + j);
                    }
                  }
                  else {
                    if (vo.getString("archive_item") != null
                        && !"".equals(vo.getString("archive_item"))
                        && !" ".equals(vo.getString("archive_item"))) {
                      t_str.append(method + "(" + vo.getString("archive_item")
                          + ") value" + i + j);
                    }
                    else {
                      t_str.append(method + "(c"
                          + (Integer.parseInt(temps[j].substring(1)) + 1)
                          + ") value" + i + j);
                    }
                  }
                  list.add("" + col);
                  list.add("select " + t_str.toString() + "  from ta_" + tabid
                      + " where unitcode='" + selfUnitcode + "' and secid="
                      + row + " ");// 当前单位
                  break;
                }
                else {
                  list.add("");
                  break;
                }
              }
            }
          }
          else // 合并 或 选择
          {
            StringBuffer a_sql = new StringBuffer("");
            if (condition_arr.length == 4) // unit条件
            {
              a_sql.append("select ");
              int size = condition_arr[2].split(",").length;
              StringBuffer t_sr = new StringBuffer("");
              for (int j = 0; j < temps.length; j++) {
                n++;
                if ("1".equals(condition_arr[3])) { // 合并

                  if (col == n) {
                    RecordVo vo =
                        (RecordVo) tnameBo.getRowInfoBGrid().get(
                            Integer.parseInt(temps[j].substring(1)));
                    // 计算方式
                    String cexpr2 = vo.getString("cexpr2");
                    RecordVo vo2 =
                        (RecordVo) tnameBo.getColInfoBGrid().get(row - 1);
                    int r_flag = this.getSumFlag2(cexpr2);
                    cexpr2 = vo2.getString("cexpr2");
                    int c_flag = this.getSumFlag2(cexpr2);
                    if (c_flag > r_flag) {
                      String method = this.getSumFlag(cexpr2); // 1:求和, 2:求均值,
                      // 3:求最大值,
                      // 4:求最小值,
                      // 5:平均人数,默认为求和
                      if ("avg".equalsIgnoreCase(method)) {
                        if (vo.getString("archive_item") != null
                            && !"".equals(vo.getString("archive_item"))
                            && !" ".equals(vo.getString("archive_item"))) {
                          t_sr.append("sum(" + vo.getString("archive_item")
                              + ")/" + size + " value" + i + j);
                        }
                        else {
                          t_sr.append("sum(c"
                              + (Integer.parseInt(temps[j].substring(1)) + 1)
                              + ")/" + size + " value" + i + j);
                        }
                      }
                      else {
                        if (vo.getString("archive_item") != null
                            && !"".equals(vo.getString("archive_item"))
                            && !" ".equals(vo.getString("archive_item"))) {
                          t_sr.append(method + "("
                              + vo.getString("archive_item") + ") value" + i
                              + j);
                        }
                        else {
                          t_sr.append(method + "(c"
                              + (Integer.parseInt(temps[j].substring(1)) + 1)
                              + ") value" + i + j);
                        }
                      }
                      String condition2[] = condition_arr[2].split(",");
                      if (condition2[0].indexOf(";") == -1) {
                        continue;
                      }
                      String condition22[] = condition2[0].split(";");
                      if ("2".equals(condition22[2])) {
                        a_sql.append(t_sr.toString() + "  from ta_" + tabid
                            + " where secid=" + row + " and yearid in (");
                        for (int a = 0; a < condition2.length - 1; a++) {
                          if (condition2[a].indexOf(";") != -1) {
                            String conditiontemp[] = condition2[a].split(";");
                            a_sql.append(conditiontemp[0] + ",");
                          }

                        }

                        String conditiontemp[] =
                            condition2[condition2.length - 1].split(";");
                        a_sql.append(conditiontemp[0] + ")and unitcode='"
                            + selfUnitcode + "'  ");

                      }
                      else if ("6".equals(condition22[2])) {

                        a_sql.append(t_sr.toString() + "  from ta_" + tabid
                            + " where (");
                        for (int a = 0; a < condition2.length - 1; a++) {
                          String conditiontemp[] = condition2[a].split(";");
                          a_sql.append(" (yearid=" + conditiontemp[0]
                              + " and countid=" + conditiontemp[1]
                              + " and weekid=" + conditiontemp[3] + ") or");
                        }
                        String conditiontemp[] =
                            condition2[condition2.length - 1].split(";");
                        a_sql.append(" (yearid=" + conditiontemp[0]
                            + " and countid=" + conditiontemp[1]
                            + " and weekid=" + conditiontemp[3] + ")) ");
                        a_sql.append("  and unitcode='" + selfUnitcode
                            + "' and secid=" + row + "  ");

                      }
                      else {

                        a_sql.append(t_sr.toString() + "  from ta_" + tabid
                            + " where (");
                        for (int a = 0; a < condition2.length - 1; a++) {
                          String conditiontemp[] = condition2[a].split(";");
                          a_sql.append(" (yearid=" + conditiontemp[0]
                              + " and countid=" + conditiontemp[1] + " ) or");
                        }
                        String conditiontemp[] =
                            condition2[condition2.length - 1].split(";");
                        a_sql.append(" (yearid=" + conditiontemp[0]
                            + " and countid=" + conditiontemp[1] + " )) ");
                        a_sql.append("  and unitcode='" + selfUnitcode
                            + "'  and secid=" + row + " ");

                      }

                      list.add("" + col);
                      list.add(a_sql.toString());// 当前单位
                      break;
                    }
                    else {
                      list.add("");
                      break;
                    }
                  }

                }

              }

            }
          }
        }
      }

    }

    catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  // 得到选中列的综合表的sql语句
  public String getSelectColumnSql(String[] condition, String tabid,
      String sortid, String num, String selfUnitcode,
      ArrayList grassRootsUnitList, TnameBo tnameBo) {
    // System.out.println(condition.toString()+"  "+tabid+"   "+sortid+"  "+num+"   "+selfUnitcode+"   "+grassRootsUnitList.size());

    StringBuffer sql = new StringBuffer("");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    String atemp = num.substring(3);
    String[] temps = atemp.split(",");
    try {

      StringBuffer units = new StringBuffer("");
      for (Iterator t = grassRootsUnitList.iterator(); t.hasNext();) {
        RecordVo temp = (RecordVo) t.next();
        units.append(",'");
        units.append(temp.getString("unitcode"));
        units.append("'");
      }

      ArrayList sqlList = new ArrayList();
      for (int i = 0; i < condition.length; i++) {
        String a_condition = (String) condition[i];
        String[] condition_arr = a_condition.split(":");
        if ("2".equals(condition_arr[3])) // 总计
        {

          StringBuffer a_sql = new StringBuffer("(select ");
          StringBuffer t_str = new StringBuffer("");
          for (int j = 0; j < temps.length; j++) {
            RecordVo vo =
                (RecordVo) tnameBo.getRowInfoBGrid().get(
                    Integer.parseInt(temps[j].substring(1)));
            // 计算方式
            String method = "sum";
            t_str.append("," + method + "(c"
                + (Integer.parseInt(temps[j].substring(1)) + 1) + ") value" + i
                + j);
          }
          a_sql.append(t_str.substring(1) + ",secid  from tt_" + tabid
              + " where unitcode in (");
          a_sql.append(units.substring(1));
          a_sql.append(" ) group by secid ) a" + i);
          sqlList.add(a_sql.toString());

        }
        else // 合并 或 选择
        {
          StringBuffer a_sql = new StringBuffer("");
          if (condition_arr.length == 4) // unit条件
          {
            a_sql.append("(select ");
            StringBuffer t_sr = new StringBuffer("");
            for (int j = 0; j < temps.length; j++) {
              RecordVo vo =
                  (RecordVo) tnameBo.getRowInfoBGrid().get(
                      Integer.parseInt(temps[j].substring(1)));
              // 计算方式
              String method = "sum";
              int a_num = Integer.parseInt(temps[j].substring(1));
              if ("1".equals(condition_arr[3])) {
                t_sr.append("," + method + "(c" + (a_num + 1) + ")");
              }
              else if ("0".equals(condition_arr[3])) {
                t_sr.append(",c" + (a_num + 1));
              }
              else if ("5".equals(condition_arr[3])) { // 平均值
                int numavg =
                    condition_arr[2].substring(0,
                        condition_arr[2].lastIndexOf(",")).split(",").length;
                t_sr.append(",sum("
                    + Sql_switcher.isnull("c" + (a_num + 1), "0") + ")/"
                    + numavg + "");
              }
              else if ("4".equals(condition_arr[3])) { // 最大值
                int numavg =
                    condition_arr[2].substring(0,
                        condition_arr[2].lastIndexOf(",")).split(",").length;
                t_sr.append(",max("
                    + Sql_switcher.isnull("c" + (a_num + 1), "0") + ")");
              }
              else if ("3".equals(condition_arr[3])) { // 最小值
                int numavg =
                    condition_arr[2].substring(0,
                        condition_arr[2].lastIndexOf(",")).split(",").length;
                t_sr.append(",min("
                    + Sql_switcher.isnull("c" + (a_num + 1), "0") + ")");
              }
              t_sr.append(" value" + i + j);
            }
            a_sql.append(t_sr.substring(1) + ",secid  from tt_" + tabid
                + " where unitcode");
            if ("1".equals(condition_arr[3])) {
              a_sql.append(" in ( "
                  + condition_arr[2].substring(0, condition_arr[2]
                      .lastIndexOf(",")) + " )  group by secid ");
            }
            else if ("0".equals(condition_arr[3])) {
              a_sql.append("=" + condition_arr[2]);
            }
            else if ("5".equals(condition_arr[3])) {
              a_sql.append(" in ( "
                  + condition_arr[2].substring(0, condition_arr[2]
                      .lastIndexOf(",")) + " )  group by secid ");
            }
            else if ("4".equals(condition_arr[3])) {
              a_sql.append(" in ( "
                  + condition_arr[2].substring(0, condition_arr[2]
                      .lastIndexOf(",")) + " )  group by secid ");
            }
            else if ("3".equals(condition_arr[3])) {
              a_sql.append(" in ( "
                  + condition_arr[2].substring(0, condition_arr[2]
                      .lastIndexOf(",")) + " )  group by secid ");
            }
            a_sql.append(" ) a" + i);
          }
          else // 代码条件
          {
            recset =
                dao.search("select paramscope from tparam  where paramename='"
                    + condition_arr[4] + "'");
            if (recset.next()) {
              int scope = recset.getInt("paramscope");
              String paramTable = "";
              if (scope == 0) // 全局参数
              {
                paramTable = "tt_p";
              }
              else if (scope == 1) // 表类参数
              {
                paramTable = "tt_s" + sortid;

              }
              else if (scope == 2) // 表参数
              {
                paramTable = "tt_t" + tabid;
              }

              a_sql.append("(select ");
              StringBuffer t_sr = new StringBuffer("");
              for (int j = 0; j < temps.length; j++) {
                int a_num = Integer.parseInt(temps[j].substring(1));
                if ("5".equals(condition_arr[3])) {// 平均值
                  int numavg =
                      condition_arr[2].substring(0,
                          condition_arr[2].lastIndexOf(",")).split(",").length;
                  t_sr.append(",sum("
                      + Sql_switcher.isnull("tt_" + tabid + ".c" + (a_num + 1),
                          "0") + ")/" + numavg + "  value" + i + j);
                }
                else if ("4".equals(condition_arr[3])) {
                  t_sr.append(",max(tt_" + tabid + ".c" + (a_num + 1)
                      + ")  value" + i + j);
                }
                else if ("3".equals(condition_arr[3])) {
                  t_sr.append(",min(tt_" + tabid + ".c" + (a_num + 1)
                      + ")  value" + i + j);
                }
                else {
                  t_sr.append(",sum(tt_" + tabid + ".c" + (a_num + 1)
                      + ")  value" + i + j);
                }

              }
              a_sql.append(t_sr.substring(1) + ",secid from tt_" + tabid + ","
                  + paramTable + " where tt_" + tabid + ".unitcode="
                  + paramTable + ".unitcode and " + paramTable + "."
                  + condition_arr[4]);
              if ("1".equals(condition_arr[3])) {
                a_sql.append(" in( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " ) ");
              }
              else if ("0".equals(condition_arr[3])) {
                a_sql.append("=" + condition_arr[2]);
              }
              else if ("5".equals(condition_arr[3])) {
                a_sql.append(" in( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " ) ");
              }
              else if ("4".equals(condition_arr[3])) {
                a_sql.append(" in( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " ) ");
              }
              else if ("3".equals(condition_arr[3])) {
                a_sql.append(" in( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " ) ");
              }
              a_sql.append(" and tt_" + tabid + ".unitcode in ("
                  + units.substring(1) + ") ");
              a_sql.append(" group by secid ) a" + i);
            }
          }
          sqlList.add(a_sql.toString());

        }
      }
      StringBuffer sql_s = new StringBuffer("");
      StringBuffer sql_f = new StringBuffer("");
      StringBuffer sql_w = new StringBuffer("");
      for (int i = 0; i < sqlList.size(); i++) {
        for (int j = 0; j < temps.length; j++) {
          sql_s.append(",a" + i + ".value" + i + j);
        }
        sql_f.append(" left outer join  " + (String) sqlList.get(i)
            + " on a00.secid=a" + i + ".secid ");
      }
      sql.append("select " + sql_s.substring(1)
          + " from  (select distinct secid from tt_" + tabid + " ) a00 ");
      sql.append(sql_f.toString());
      sql.append(" order by a00.secid");
    }
    catch (Exception e) {
      e.printStackTrace();
      // throw GeneralExceptionHandler.Handle(e);
    }
    // System.out.println("---------"+sql.toString());
    return sql.toString();
  }

  // 得到选中行的综合表的sql语句
  public String getSelectRowAnalyseSql(String[] condition, String tabid,
      String sortid, String nums, String selfUnitcode,
      ArrayList grassRootsUnitList, int colnums, TnameBo tnameBo,
      String reportTypes, String yearid, String countid, String weekid) {
    StringBuffer sql = new StringBuffer("");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    String atemp = nums.substring(3);
    String[] temps = atemp.split(",");
    String colname = "";
    String columnname = " ";
    if ("".equals(yearid)) {
      columnname += " and yearid=1";
    }
    else {
      columnname += " and yearid=" + yearid;
    }
    if ("".equals(countid)) {
      columnname += " and countid=1";
    }
    else {
      columnname += " and countid=" + countid;
    }
    if (!"".equals(weekid)) {
      columnname += " and weekid=" + weekid;
    }
    try {
      StringBuffer units = new StringBuffer("");
      for (Iterator t = grassRootsUnitList.iterator(); t.hasNext();) {
        RecordVo temp = (RecordVo) t.next();
        units.append(",'");
        units.append(temp.getString("unitcode"));
        units.append("'");
      }
      // units.append(",'");
      // units.append(selfUnitcode);
      // units.append("'");
      ArrayList sqlList = new ArrayList();
      for (int i = 0; i < condition.length; i++) {
        String a_condition = (String) condition[i];
        if (a_condition.indexOf(";") == -1) {
          String[] condition_arr = a_condition.split(":");
          if ("2".equals(condition_arr[3])) // 总计
          {
            for (int e = 0; e < temps.length; e++) {
              int num = Integer.parseInt(temps[e].substring(1));
              RecordVo vo2 =
                  (RecordVo) tnameBo.getColInfoBGrid().get(
                      Integer.parseInt(temps[e].substring(1)));
              String method = "sum";
              StringBuffer a_sql = new StringBuffer("select ");
              StringBuffer sql_s = new StringBuffer("");
              for (int j = 0; j < colnums; j++) {
                RecordVo vo = (RecordVo) tnameBo.getRowInfoBGrid().get(j);

                if (vo.getString("archive_item") != null
                    && !"".equals(vo.getString("archive_item"))
                    && !" ".equals(vo.getString("archive_item"))) {
                  sql_s.append("," + method + "("
                      + vo.getString("archive_item") + ") value" + i + j);
                }
                else {
                  sql_s.append("," + method + "(c" + (j + 1) + ") c" + (j + 1));
                }
              }
              a_sql.append(sql_s.substring(1));
              a_sql.append(" from ta_" + tabid + " where unitcode in (");
              a_sql.append(units.substring(1) + " ) " + columnname
                  + " and secid=" + (num + 1));
              sqlList.add(a_sql.toString());
            }
          }
          else // 合并 或 选择
          {
            for (int e = 0; e < temps.length; e++) {
              int num = Integer.parseInt(temps[e].substring(1));
              RecordVo vo2 =
                  (RecordVo) tnameBo.getColInfoBGrid().get(
                      Integer.parseInt(temps[e].substring(1)));
              String method = "sum";
              StringBuffer a_sql = new StringBuffer("");
              StringBuffer sql_s = new StringBuffer("");
              for (int j = 0; j < colnums; j++) {
                RecordVo vo = (RecordVo) tnameBo.getRowInfoBGrid().get(j);
                if ("5".equals(condition_arr[3])) {
                  int size = condition_arr[2].split(",").length;
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append(",sum(" + vo.getString("archive_item") + ")/"
                        + size + " ");
                  }
                  else {
                    sql_s.append(",sum(c" + (j + 1) + ")/" + size + " c"
                        + (j + 1));
                  }
                }
                else if ("4".equals(condition_arr[3])) {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append(",max(" + vo.getString("archive_item") + ")");
                  }
                  else {
                    sql_s.append(",max(c" + (j + 1) + ") c" + (j + 1));
                  }
                }
                else if ("3".equals(condition_arr[3])) {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append(",min(" + vo.getString("archive_item") + ")");
                  }
                  else {
                    sql_s.append(",min(c" + (j + 1) + ") c" + (j + 1));
                  }
                }
                else {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append("," + method + "("
                        + vo.getString("archive_item") + ")");
                  }
                  else {
                    sql_s.append("," + method + "(c" + (j + 1) + ") c"
                        + (j + 1));
                  }
                }
              }

              if (condition_arr.length == 4) // unit条件
              {
                a_sql.append("select " + sql_s.substring(1));
                a_sql.append("  from ta_" + tabid + " where unitcode");
                if ("1".equals(condition_arr[3])) {
                  a_sql.append(" in ( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) " + columnname + "");
                }
                else if ("5".equals(condition_arr[3])) {
                  a_sql.append(" in ( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) " + columnname + "");
                }
                else if ("4".equals(condition_arr[3])) {
                  a_sql.append(" in ( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) " + columnname + "");
                }
                else if ("3".equals(condition_arr[3])) {
                  a_sql.append(" in ( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) " + columnname + "");
                }
                else if ("0".equals(condition_arr[3])) {
                  a_sql.append("=" + condition_arr[2] + "" + columnname);
                }
                a_sql.append(" and secid=" + (num + 1));

              }

              sqlList.add(a_sql.toString());
            }
          }

        }
        else {
          String[] condition_arr = a_condition.split(":");
          if ("2".equals(condition_arr[3])) // 总计
          {
            for (int e = 0; e < temps.length; e++) {
              int num = Integer.parseInt(temps[e].substring(1));
              RecordVo vo2 =
                  (RecordVo) tnameBo.getColInfoBGrid().get(
                      Integer.parseInt(temps[e].substring(1)));

              // String method=getCaculateMethod(vo2);
              StringBuffer a_sql = new StringBuffer("select ");
              StringBuffer sql_s = new StringBuffer("");

              for (int j = 0; j < colnums; j++) {
                RecordVo vo = (RecordVo) tnameBo.getRowInfoBGrid().get(j);
                String cexpr2 = vo2.getString("cexpr2");
                String method = this.getSumFlag(cexpr2); // 1:求和, 2:求均值, 3:求最大值,
                // 4:求最小值, 5:平均人数,默认为求和
                int r_flag = this.getSumFlag2(cexpr2);
                cexpr2 = vo.getString("cexpr2");
                int c_flag = this.getSumFlag2(cexpr2);
                if (c_flag > r_flag) {
                  method = this.getSumFlag(cexpr2);
                }
                if ("avg".equalsIgnoreCase(method)) {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append(",sum(" + vo.getString("archive_item") + ")/"
                        + this.totalnum + " value" + i + j);
                  }
                  else {
                    sql_s.append(",sum(c" + (j + 1) + ")/" + this.totalnum
                        + " c" + (j + 1));
                  }
                }
                else {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append("," + method + "("
                        + vo.getString("archive_item") + ") value" + i + j);
                  }
                  else {
                    sql_s.append("," + method + "(c" + (j + 1) + ") c"
                        + (j + 1));
                  }
                }
              }
              // colname =" and row_item in(";

              vo2 =
                  (RecordVo) tnameBo.getColInfoBGrid().get(
                      Integer.parseInt(temps[e].substring(1)));
              // if(vo2.getString("archive_item")!=null&&!vo2.getString("archive_item").equals("")&&!vo2.getString("archive_item").equals(" "))
              // colname += " '"+vo2.getString("archive_item")+"',";
              // else
              // colname += (e+2)+",";

              // colname = colname.substring(0, colname.length()-1);
              // colname +=")";
              a_sql.append(sql_s.substring(1));
              a_sql.append(" from ta_" + tabid + " where unitcode ='"
                  + selfUnitcode + "' ");// 去掉colname
              a_sql.append("  and secid=" + (num + 1));
              sqlList.add(a_sql.toString());
            }
          }
          else // 合并 或 选择
          {
            int size = condition_arr[2].split(",").length;
            for (int e = 0; e < temps.length; e++) {
              RecordVo vo2 =
                  (RecordVo) tnameBo.getColInfoBGrid().get(
                      Integer.parseInt(temps[e].substring(1)));
              String method = "sum";

              int num = Integer.parseInt(temps[e].substring(1));

              StringBuffer a_sql = new StringBuffer("");
              StringBuffer sql_s = new StringBuffer("");
              for (int j = 0; j < colnums; j++) {
                RecordVo vo = (RecordVo) tnameBo.getRowInfoBGrid().get(j);
                if ("1".equals(condition_arr[3])) {
                  String cexpr2 = vo2.getString("cexpr2");
                  method = this.getSumFlag(cexpr2);
                  int r_flag = this.getSumFlag2(cexpr2);
                  cexpr2 = vo.getString("cexpr2");
                  int c_flag = this.getSumFlag2(cexpr2);
                  if (c_flag > r_flag) {
                    method = this.getSumFlag(cexpr2);
                  }
                }
                else if ("5".equals(condition_arr[3])) {
                  method = "avg";
                }
                else if ("4".equals(condition_arr[3])) {
                  method = "max";
                }
                else if ("3".equals(condition_arr[3])) {
                  method = "min";
                }
                if ("avg".equalsIgnoreCase(method)) {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append(",sum(" + vo.getString("archive_item") + ")/"
                        + size + "");
                  }
                  else {
                    sql_s.append(",sum(c" + (j + 1) + ")/" + size + " c"
                        + (j + 1));
                  }
                }
                else {
                  if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                    sql_s.append("," + method + "("
                        + vo.getString("archive_item") + ")");
                  }
                  else {
                    sql_s.append("," + method + "(c" + (j + 1) + ") c"
                        + (j + 1));
                  }
                }
              }

              // if(vo2.getString("archive_item")!=null&&!vo2.getString("archive_item").equals("")&&!vo2.getString("archive_item").equals(" "))
              // {
              // if(condition_arr[3].equals("1")){
              // colname ="  row_item in(";
              // for(int e1=0;e1<temps.length;e1++){
              // vo2=(RecordVo)tnameBo.getColInfoBGrid().get(Integer.parseInt(temps[e1].substring(1)));
              // if(vo2.getString("archive_item")!=null&&!vo2.getString("archive_item").equals("")&&!vo2.getString("archive_item").equals(" "))
              // colname += " '"+vo2.getString("archive_item")+"',";
              // else
              // colname += (e1+1)+",";
              // }
              // colname = colname.substring(0, colname.length()-1);
              // colname +=") and ";
              // }
              // else{
              // if(vo2.getString("archive_item")!=null&&!vo2.getString("archive_item").equals("")&&!vo2.getString("archive_item").equals(" "))
              // colname =
              // "  row_item='"+vo2.getString("archive_item")+"' and ";
              // else
              // colname = "  row_item="+(e+1)+" and ";
              // }
              // }
              // sql_s.append(","+vo2.getString("archive_item"));

              if (condition_arr.length == 4) // unit条件
              {

                if ("1".equals(condition_arr[3])
                    || "5".equals(condition_arr[3])
                    || "4".equals(condition_arr[3])
                    || "3".equals(condition_arr[3])) { // 合并，平均，最大，最小

                  String condition2[] = condition_arr[2].split(",");
                  if (condition2[0].indexOf(";") == -1) {
                    continue;
                  }
                  String condition22[] = condition2[0].split(";");
                  if ("2".equals(condition22[2])) {
                    a_sql.append("select " + sql_s.substring(1));
                    a_sql.append("  from ta_" + tabid + " where " + colname
                        + "  yearid in (");
                    for (int a = 0; a < condition2.length - 1; a++) {
                      if (condition2[a].indexOf(";") != -1) {
                        String conditiontemp[] = condition2[a].split(";");
                        a_sql.append(conditiontemp[0] + ",");
                      }

                    }

                    String conditiontemp[] =
                        condition2[condition2.length - 1].split(";");
                    a_sql.append(conditiontemp[0] + ")and unitcode='"
                        + selfUnitcode + "' and secid =" + (num + 1));

                  }
                  else if ("6".equals(condition22[2])) {
                    a_sql.append("select " + sql_s.substring(1));
                    a_sql.append("  from ta_" + tabid + " where " + colname
                        + " (");

                    for (int a = 0; a < condition2.length - 1; a++) {
                      String conditiontemp[] = condition2[a].split(";");
                      a_sql.append(" (yearid=" + conditiontemp[0]
                          + " and countid=" + conditiontemp[1] + " and weekid="
                          + conditiontemp[3] + ") or");
                    }
                    String conditiontemp[] =
                        condition2[condition2.length - 1].split(";");
                    a_sql.append(" (yearid=" + conditiontemp[0]
                        + " and countid=" + conditiontemp[1] + " and weekid="
                        + conditiontemp[3] + ")) ");
                    a_sql.append("  and unitcode='" + selfUnitcode
                        + "' and secid  =" + (num + 1));

                  }
                  else {
                    a_sql.append("select " + sql_s.substring(1));
                    a_sql.append("  from ta_" + tabid + " where " + colname
                        + " (");

                    for (int a = 0; a < condition2.length - 1; a++) {
                      String conditiontemp[] = condition2[a].split(";");
                      a_sql.append(" (yearid=" + conditiontemp[0]
                          + " and countid=" + conditiontemp[1] + " ) or");
                    }
                    String conditiontemp[] =
                        condition2[condition2.length - 1].split(";");
                    a_sql.append(" (yearid=" + conditiontemp[0]
                        + " and countid=" + conditiontemp[1] + " )) ");
                    a_sql.append("  and unitcode='" + selfUnitcode
                        + "' and secid  =" + (num + 1));

                  }

                }

                else if ("0".equals(condition_arr[3])) { // 选择

                  String condition2[] = condition_arr[2].split(";");
                  if (!"6".equals(condition2[2])) {
                    a_sql.append("select " + sql_s.substring(1));
                    a_sql.append("  from ta_" + tabid + " where  yearid ");
                    a_sql.append("=" + condition2[0] + " and unitcode='"
                        + selfUnitcode + "' and countid=" + condition2[1]);
                  }
                  else {
                    a_sql.append("select " + sql_s.substring(1));
                    a_sql.append("  from ta_" + tabid + " where  yearid ");
                    a_sql.append("=" + condition2[0] + " and unitcode='"
                        + selfUnitcode + "' and countid=" + condition2[1]
                        + " and weekid=" + condition2[3]);
                  }
                  a_sql.append(" and secid=" + (num + 1));
                }

              }

              sqlList.add(a_sql.toString());
            }
          }
        }
      }

      for (int i = 0; i < sqlList.size(); i++) {
        sql.append(" union all  " + (String) sqlList.get(i));
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      // throw GeneralExceptionHandler.Handle(e);
    }
    return sql.substring(10);
  }

  // 得到选中行的综合表的sql语句
  public String getSelectRowSql(String[] condition, String tabid,
      String sortid, String nums, String selfUnitcode,
      ArrayList grassRootsUnitList, int colnums, TnameBo tnameBo) {
    StringBuffer sql = new StringBuffer("");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;

    String atemp = nums.substring(3);
    String[] temps = atemp.split(",");
    try {
      StringBuffer units = new StringBuffer("");
      for (Iterator t = grassRootsUnitList.iterator(); t.hasNext();) {
        RecordVo temp = (RecordVo) t.next();
        units.append(",'");
        units.append(temp.getString("unitcode"));
        units.append("'");
      }

      ArrayList sqlList = new ArrayList();
      for (int i = 0; i < condition.length; i++) {
        String a_condition = (String) condition[i];
        String[] condition_arr = a_condition.split(":");
        if ("2".equals(condition_arr[3])) // 总计
        {
          for (int e = 0; e < temps.length; e++) {
            int num = Integer.parseInt(temps[e].substring(1));
            RecordVo vo2 =
                (RecordVo) tnameBo.getColInfoBGrid().get(
                    Integer.parseInt(temps[e].substring(1)));
            String method = "sum";
            StringBuffer a_sql = new StringBuffer("select ");
            StringBuffer sql_s = new StringBuffer("");
            for (int j = 1; j <= colnums; j++) {
              sql_s.append("," + method + "(c" + j + ") c" + j);
            }
            a_sql.append(sql_s.substring(1));
            a_sql.append(" from tt_" + tabid + " where unitcode in (");
            a_sql.append(units.substring(1) + " ) and secid=" + (num + 1));
            sqlList.add(a_sql.toString());
          }
        }
        else // 合并 或 选择
        {
          for (int e = 0; e < temps.length; e++) {
            int num = Integer.parseInt(temps[e].substring(1));
            RecordVo vo2 =
                (RecordVo) tnameBo.getColInfoBGrid().get(
                    Integer.parseInt(temps[e].substring(1)));
            String method = "sum";
            StringBuffer a_sql = new StringBuffer("");
            StringBuffer sql_s = new StringBuffer("");
            for (int j = 1; j <= colnums; j++) {
              if ("5".equals(condition_arr[3])) {// 平均值
                int numavg =
                    condition_arr[2].substring(0,
                        condition_arr[2].lastIndexOf(",")).split(",").length;
                sql_s.append(",sum(" + Sql_switcher.isnull("c" + j, "0") + ")/"
                    + numavg + " c" + j + " ");
              }
              else if ("4".equals(condition_arr[3])) {
                sql_s.append(",max(c" + j + ") c" + j);
              }
              else if ("3".equals(condition_arr[3])) {
                sql_s.append(",min(c" + j + ") c" + j);
              }
              else {
                sql_s.append("," + method + "(c" + j + ") c" + j);
              }
            }

            if (condition_arr.length == 4) // unit条件
            {
              a_sql.append("select " + sql_s.substring(1));
              a_sql.append("  from tt_" + tabid + " where unitcode");
              if ("1".equals(condition_arr[3])) {
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )");
              }
              else if ("5".equals(condition_arr[3])) {
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )");
              }
              else if ("4".equals(condition_arr[3])) {
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )");
              }
              else if ("3".equals(condition_arr[3])) {
                a_sql.append(" in ( "
                    + condition_arr[2].substring(0, condition_arr[2]
                        .lastIndexOf(",")) + " )");
              }
              else if ("0".equals(condition_arr[3])) {
                a_sql.append("=" + condition_arr[2]);
              }
              a_sql.append(" and secid=" + (num + 1));

            }
            else // 代码条件
            {
              recset =
                  dao
                      .search("select paramscope from tparam  where paramename='"
                          + condition_arr[4] + "'");
              if (recset.next()) {
                int scope = recset.getInt("paramscope");
                String paramTable = "";
                if (scope == 0) // 全局参数
                {
                  paramTable = "tt_p";
                }
                else if (scope == 1) // 表类参数
                {
                  paramTable = "tt_s" + sortid;

                }
                else if (scope == 2) // 表参数
                {
                  paramTable = "tt_t" + tabid;
                }

                a_sql.append("select " + sql_s.substring(1));
                a_sql.append(" from tt_" + tabid + "," + paramTable
                    + " where tt_" + tabid + ".unitcode=" + paramTable
                    + ".unitcode and " + paramTable + "." + condition_arr[4]);
                if ("1".equals(condition_arr[3])) {
                  a_sql.append(" in( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) ");
                }
                else if ("5".equals(condition_arr[3])) {
                  a_sql.append(" in( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) ");
                }
                else if ("4".equals(condition_arr[3])) {
                  a_sql.append(" in( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) ");
                }
                else if ("3".equals(condition_arr[3])) {
                  a_sql.append(" in( "
                      + condition_arr[2].substring(0, condition_arr[2]
                          .lastIndexOf(",")) + " ) ");
                }
                else if ("0".equals(condition_arr[3])) {
                  a_sql.append("=" + condition_arr[2]);
                }
                a_sql.append(" and secid=" + (num + 1));

                a_sql.append(" and tt_" + tabid + ".unitcode in ("
                    + units.substring(1) + ") ");
              }
            }
            sqlList.add(a_sql.toString());
          }
        }
      }

      for (int i = 0; i < sqlList.size(); i++) {
        sql.append(" union all  " + (String) sqlList.get(i));
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      // throw GeneralExceptionHandler.Handle(e);
    }
    return sql.substring(10);
  }

  /**
   * 取得列定义归档数据汇总标记
   *1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和
   */
  public String getSumFlag(String cexpr2) {
    String flag2 = "sum";
    if (cexpr2 != null && cexpr2.trim().length() > 0
        && cexpr2.toUpperCase().indexOf("<SUMFLAG>") != -1) {
      cexpr2 = cexpr2.toUpperCase();
      int fromIndex = cexpr2.indexOf("<SUMFLAG>");
      int toIndex = cexpr2.indexOf("</SUMFLAG>");
      String flag = cexpr2.substring(fromIndex + 9, toIndex).trim();
      if (flag.length() == 0) {
        flag = "1";
      }
      int flag3 = Integer.parseInt(flag);
      switch (flag3) {
        case 1:
          flag2 = "sum";
          break;
        case 2:
          flag2 = "avg";
          break;
        case 3:
          flag2 = "max";
          break;
        case 4:
          flag2 = "min";
          break;
        case 5:
          flag2 = "avg";
          break;
      }
    }

    return flag2;
  }

  /**
   * 取得列定义归档数据汇总标记
   *1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和
   */
  public int getSumFlag2(String cexpr2) {
    int flag2 = 1;
    if (cexpr2 != null && cexpr2.trim().length() > 0
        && cexpr2.toUpperCase().indexOf("<SUMFLAG>") != -1) {
      cexpr2 = cexpr2.toUpperCase();
      int fromIndex = cexpr2.indexOf("<SUMFLAG>");
      int toIndex = cexpr2.indexOf("</SUMFLAG>");
      String flag = cexpr2.substring(fromIndex + 9, toIndex).trim();
      if (flag.length() == 0) {
        flag = "1";
      }
      flag2 = Integer.parseInt(flag);

    }

    return flag2;
  }

  // 判断是否存在综合表生成方案，如果没有，则生成
  public void isExistIntegrateSchemeTable() throws GeneralException {
    DbWizard dbWizard = new DbWizard(this.conn);
    Table table = new Table("IntegrateScheme");
    try {
      if (!dbWizard.isExistTable(table.getName(), false)) {

        TgridBo tgridBo = new TgridBo(this.conn);
        table.addField(tgridBo
            .getField1("unitcode", ResourceFactory
                .getProperty("ttOrganization.unit.unitcode"),
                "DataType.STRING", 30));
        table.addField(tgridBo.getField2("tabid", ResourceFactory
            .getProperty("report.reportlist.reportid"), "I"));
        table.addField(tgridBo.getField2("secid", ResourceFactory
            .getProperty("ttOrganization.record.secid"), "I"));
        table.addField(tgridBo.getField2("content", ResourceFactory
            .getProperty("report.projectContext"), "M"));
        table.addField(tgridBo.getField2("flag", ResourceFactory
            .getProperty("report.projectFlag"), "I"));
        table.setCreatekey(false);
        dbWizard.createTable(table);

      }
      else {

        if (dbWizard.isExistField("IntegrateScheme", "flag")) {// 判断字段是否存在，没则生成，同时付默认值0
        }
        else {
          TgridBo tgridBo = new TgridBo(this.conn);
          table.addField(tgridBo.getField2("flag", ResourceFactory
              .getProperty("report.projectFlag"), "I"));
          dbWizard.addColumns(table);
          ContentDAO dao = new ContentDAO(this.conn);
          dao.update(" update IntegrateScheme set flag=0 where flag is null");
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
  }

  // 保存方案
  public void saveScheme(String unitcode, String tabid, int secid,
      String content, String flag) throws GeneralException {
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      dao.delete("delete from IntegrateScheme where unitcode='" + unitcode
          + "' and secid=" + secid + " and tabid=" + tabid + " and flag="
          + flag, new ArrayList());
      dao.insert(
          "insert into IntegrateScheme(unitcode,tabid,secid,content,flag)values('"
              + unitcode + "'," + tabid + "," + secid + ",'" + content + "',"
              + flag + ")", new ArrayList());
    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }

  }

  public void setBackdate(String backdate) {
    this.backdate = backdate;
  }

}
