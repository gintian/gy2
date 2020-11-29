package com.hjsj.hrms.module.projectmanage.workhours.manprojecthours.businessobject;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ProjectManageBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * @author wangjl
 *         员工月工时报表
 *         2015/12/24
 */
public class ManMonthHoursBo {

  Connection conn;

  ContentDAO dao;

  UserView userview;

  public ManMonthHoursBo(Connection frameconn, UserView userView) {
    this.conn = frameconn;
    this.userview = userView;
  }

  /**
   * @param year
   * @param month
   * @return
   */
  public ArrayList<Object> getButtonList(String year, String month) {
    ArrayList<Object> buttonList = new ArrayList<Object>();
    buttonList.add("-");
    buttonList.add(this.newButton("返回", null, "manproject_me.achieveMent",
        null, "true"));
    buttonList
        .add(this
            .newButton("<a id = 'asd' href='javascript:manproject_me.click();' ><span id='timetitle'>"
                + year
                + "</span>年 <span id='monthtitle'>"
                + month
                + "</span>月 <img src='/workplan/image/jiantou.png' /></a>"));
    return buttonList;
  }

  /**
   * @param year
   * @param month
   * @return
   */
  public String getTableConfig(String year, String month) {
    // 获取用户权限
    ProjectManageBo manageBo = new ProjectManageBo(this.userview, this.conn);
    String where = manageBo.getWhere();
    // 排除指标 :
    String exceptFields = "p1101,p1311,p1301,p1313,p1325,p1315,p1317,guidkey";
    // 需要增加列宽的指标
    String isAddWidth = "p1309";
    // 需要锁的列
    String islock = "p1303";
    ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("P13", 1);
    /** 拼接sql */
    StringBuffer sql = new StringBuffer("SELECT DISTINCT ");
    ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
    ColumnsInfo columnsInfo = new ColumnsInfo();
    boolean flag = true;
    for (FieldItem fi : fieldList) {
      String itemid = fi.getItemid();
      if (exceptFields.indexOf(itemid.toLowerCase()) != -1) {
        continue;
      }
      // 判断数据字典是否要求显示
      String itemdesc = fi.getItemdesc();
      String codesetId = fi.getCodesetid();
      String columnType = fi.getItemtype();
      int columnLength = fi.getDisplaywidth();// 显示长度
      int decimalWidth = fi.getDecimalwidth();// 小数位
      if ("false".equals(fi.isVisible()) && "p1303".equals(itemid)) {
        flag = false;
      }
      if (fi.isVisible() || "p1301".equalsIgnoreCase(itemid)) {
        columnsInfo =
            this.getColumnsInfo(itemid, itemdesc, 119, codesetId, columnType,
                columnLength, decimalWidth);
        if ("p1301".equalsIgnoreCase(codesetId)) {
          sql.append("p1301");
          columnsInfo.setReadOnly(true);
        }
        else if ("p1319".equalsIgnoreCase(itemid)) {// 实际工时
          sql.append("b.sum_p1511 p1319,");
          columnsInfo.setTextAlign("right");
        }
        else if ("p1321".equalsIgnoreCase(itemid)) {// 标准工时
          sql.append("b.sum_p1513 p1321,");
          columnsInfo.setTextAlign("right");
        }
        else if ("p1323".equalsIgnoreCase(itemid)) {// 超额工时
          sql.append("b.sum_p1515 p1323");
          columnsInfo.setTextAlign("right");
        }
        else if ("a0100".equalsIgnoreCase(itemid)) {
          sql.append("b.a0100,");
        }
        else if ("nbase".equalsIgnoreCase(itemid)) {
          sql.append("(select DBName from DBName where Pre=b.nbase) nbase,");
        }
        else {
          sql.append(itemid + ",");
        }
        // 需要增加列宽的列
        if (!StringUtils.isEmpty(isAddWidth)) {
          if (isAddWidth.indexOf(itemid.toLowerCase()) != -1) {
            columnsInfo.setColumnWidth(145);// 显示列宽
          }
        }
        // 需要锁的列
        if (!StringUtils.isEmpty(islock)) {
          if (islock.indexOf(itemid.toLowerCase()) != -1) {
            columnsInfo.setLocked(true);
          }
        }
        columnsList.add(columnsInfo);
      }
    }
    sql.append(" from (select * from P13 p");
    sql.append(" where p1301 = (select max(p1301) from p13 pp");
    sql.append(" where p.guidkey=pp.guidkey group by guidkey)");
    sql.append(" ) ppp  ");
    sql.append(" right join ");
    sql.append(" (select a.guidkey,");
    sql.append(Sql_switcher.round("sum(a.p1511)/480.0", 2));
    sql.append(" sum_p1511, ");
    sql.append(Sql_switcher.round("sum(a.p1513)/480.0", 2));
    sql.append(" sum_p1513, ");
    sql.append(" (" + Sql_switcher.round("sum(P1511)/480.0", 2) + "-"
        + Sql_switcher.round("sum(p1513)/480.0", 2) + ") ");
    sql.append(" sum_p1515 ");
    sql.append(" from ");
    sql.append(" (select  p15.P1511,p15.P1513,p15.p1515,p13.guidkey ");
    sql.append(" from P15 inner join P13 ");
    sql.append(" on p13.P1301 = p15.P1301 ");
    sql.append(" and P15.P1519 = '1'");
    sql.append(" and " + Sql_switcher.year("p15.P1507") + "=" + year);
    sql.append(" and " + Sql_switcher.month("p15.P1507") + "=" + month);
    // 查询工时数据时添加权限
    if (!StringUtils.isEmpty(where)) {
      sql
          .append("  and exists (select p1101 from p11 where p15.p1101=p11.p1101 and ("
              + where + "))");
    }
    sql.append(" )a group by a.guidkey)b ");
    sql.append(" on b.guidkey= ppp.guidkey");

    /** 加载表格 */

    TableConfigBuilder builder =
        new TableConfigBuilder("manproject_id_001", columnsList, "manproject",
            this.userview, this.conn);
    builder.setLockable(true);
    builder.setDataSql(sql.toString());
    if (flag) {
      builder.setOrderBy(" order by p1303");
    }
    builder.setAutoRender(false);
    builder.setTitle("员工月工时报表");
    builder.setEditable(false);
    builder.setPageSize(20);
    builder.setSchemeSaveCallback("manproject_me.schemeSaveCallback");
    if (this.userview.hasTheFunction("3900400")) {
      builder.setSetScheme(true);
      builder.setScheme(true);
      if (this.userview.hasTheFunction("390040001")) {
        builder.setShowPublicPlan(true);
      }
      else {
        builder.setShowPublicPlan(false);
      }
    }
    else {
      builder.setSetScheme(false);
    }

    builder.setSelectable(false);
    builder.setTableTools(this.getButtonList(year, month));
    builder.setScheme(true);
    builder.setColumnFilter(true);
    builder.setConstantName("projectmanage/manprojecthours");
    return builder.createExtTableConfig();
  }

  /**
   * 列头ColumnsInfo对象初始化
   * 
   * @param columnId id
   * @param columnDesc 名称
   * @param columnDesc 显示列宽
   * @return
   */
  private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
      int columnWidth, String codesetId, String columnType, int columnLength,
      int decimalWidth) {

    ColumnsInfo columnsInfo = new ColumnsInfo();
    columnsInfo.setColumnId(columnId);
    columnsInfo.setColumnDesc(columnDesc);
    columnsInfo.setColumnWidth(columnWidth);// 显示列宽
    columnsInfo.setCodesetId(codesetId);// 指标集
    columnsInfo.setColumnType(columnType);// 类型N|M|A|D
    columnsInfo.setColumnLength(columnLength);// 显示长度
    columnsInfo.setDecimalWidth(decimalWidth);// 小数位
    columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
    columnsInfo.setReadOnly(false);// 是否只读
    columnsInfo.setFromDict(true);// 是否从数据字典里来
    columnsInfo.setLocked(false);// 是否锁列
    return columnsInfo;
  }

  private ButtonInfo newButton(String text) {
    ButtonInfo button = new ButtonInfo(text);

    return button;
  }

  /**
   * @param text
   * @param id
   * @param handler
   * @param icon
   * @param getdata
   * @return
   */
  private ButtonInfo newButton(String text, String id, String handler,
      String icon, String getdata) {
    ButtonInfo button = new ButtonInfo(text, handler);
    if (getdata != null) {
      button.setGetData(Boolean.valueOf(getdata).booleanValue());
    }
    if (icon != null) {
      button.setIcon(icon);
    }
    if (id != null) {
      button.setId(id);
    }
    return button;
  }

}
