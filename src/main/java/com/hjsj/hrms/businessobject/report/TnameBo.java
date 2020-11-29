package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.hire.SendEmail;
import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TnameBo {

  private String appdate = null; // 截止日期

  private HashSet colCountExpreSet = new HashSet(); // 列计算公式集合

  private ArrayList colInfoBGrid = new ArrayList(); // 纵表栏底层单元格列表（按顺序排列）

  private ArrayList colInfoList = new ArrayList(); // 纵表栏按顺序排列的相关信息集和

  private HashMap colMap = new HashMap();

  private String colSerialNo = ""; // 纵表栏序号所在位置

  private Connection conn = null;

  private RecordVo dataArea = null;

  private boolean dataScopeEnabled = false; // 取数范围

  private String dataScopeEndDate = "";

  private String dataScopeNbases = "";

  private String dataScopeStartDate = "";

  private ArrayList dbList = new ArrayList(); // 扫描库

  private DBMetaModel dbmodel = null;

  private int[][] digitalResults = null; // 结果集对应的小数位

  private ExprUtil exprUtil = new ExprUtil();

  private HashMap factorListMap = new HashMap();

  private ArrayList gridList = new ArrayList(); // 表格列表

  private int[] itemGridArea = new int[4]; // 表项目区域 l,t,w,h

  private ArrayList itemGridList = new ArrayList();

  private MidVariableBo midVariableBo = null;

  private ArrayList midVariableList = new ArrayList(); // 变量列表

  private ArrayList midVariableNotContrlList = new ArrayList(); // 变量不按管理范围列表

  private int minTop_px = 0; // 表最上端标题top属性的值

  private String numberFieldSetCoordinate = "-1"; // 个数子集所在坐标抽 －1：无求个数子集 2：在横坐标

  // 1：在纵坐标

  private ArrayList pageList = new ArrayList(); // 标题列表

  private ArrayList paramenameList = new ArrayList();

  private HashMap paramMap = new HashMap(); // 参数列表

  private float percent = 0.26f;// zhaoxg add

  private String result = null; // 是否从查询结果库中取数

  private double[][] results = null; // 结果集

  private HashSet rowCountExpreSet = new HashSet(); // 行计算公式集合

  private ArrayList rowInfoBGrid = new ArrayList(); // 横表栏底层单元格列表（按顺序排列）

  private ArrayList rowInfoList = new ArrayList(); // 横表栏按顺序排列的相关信息集和

  private HashMap rowMap = new HashMap();

  private String rowSerialNo = ""; // 横表栏序号所在位置

  private String scopeid = "0"; // 统计口径 默认为0

  private HashMap scopeMap = new HashMap(); // 存放统计口径的表

  private boolean showReportHtmlPaper = true;

  private boolean showReportHtmlToolbar = true;

  private String startdate = null; // 起始日期

  private String statScopeCoordinate = "-1"; // 取值方法所在坐标抽 －1：无统计记录范围 2：在横坐标

  // 1：在纵坐标

  private String tabid = "";

  private ArrayList tableTermList = new ArrayList(); // 表条件相关信息集和

  private TgridBo tgridBo = null;

  private RecordVo tnameVo = null; // 表对象

  private TpageBo tpageBo = null;

  private String unitcode = ""; // 填报单位编码

  private String userName = "";

  private UserView userview = null;

  private String usrID = "";

  private int[] elementPosion={};//记录正文的位置  0:rtop最大，1rleft最大，rtop最大，rleft最小

  public int[] getElementPosion() {
    if(0==elementPosion.length)//xiegh add 20170720 bug:29088
    {
        elementPosion = getElementPostion(tabid);
    }
    return elementPosion;
  }

  public void setElementPosion(int[] elementPosion) {
    if(0==elementPosion.length){
      elementPosion = getElementPostion(tabid);
    }
    this.elementPosion = elementPosion;
  }

  public TnameBo(Connection conn) {
    this.conn = conn;
    this.tgridBo = new TgridBo(conn);
    this.dbmodel = new DBMetaModel(this.conn);
    this.scopeMap = this.getScopeMap();
  }

  // 精简的购造方法 只得到 colMap,rowMap,digitalResults,tabid
  public TnameBo(Connection conn, String tabid) {

    try {
      this.conn = conn;
      this.tgridBo = new TgridBo(conn);
      this.gridList = this.tgridBo.getGridInfoList(tabid);
      ArrayList list =
              this.tgridBo.getRowAndColInfoList3(this.gridList, tabid, this.rowMap,
                      this.colMap, this.rowInfoBGrid, this.colInfoBGrid);
      this.digitalResults = (int[][]) list.get(0);
      this.rowSerialNo = (String) list.get(1);
      this.colSerialNo = (String) list.get(2);
      this.tabid = tabid;
      this.dbmodel = new DBMetaModel(this.conn);
      this.scopeMap = this.getScopeMap();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public TnameBo(Connection conn, String tabid, String usrID, String userName) {
    try {
      this.conn = conn;
      this.tgridBo = new TgridBo(conn);
      this.midVariableBo = new MidVariableBo(conn);
      this.tpageBo = new TpageBo(conn);
      this.tabid = tabid;
      this.userName = userName;
      this.gridList = this.tgridBo.getGridInfoList(tabid);
      this.midVariableList = this.midVariableBo.getMidVariableList3(tabid);
      this.midVariableNotContrlList =
              this.tgridBo.MidVariableNotContrlList(tabid, this.midVariableList);
      // 分析表格,得到报表横表栏和纵表栏的相关信息集合
      ArrayList rowAndColInfoList =
              this.tgridBo.getRowAndColInfoList(this.gridList, tabid, this.rowMap,
                      this.colMap);
      this.rowInfoList = (ArrayList) rowAndColInfoList.get(0);
      this.colInfoList = (ArrayList) rowAndColInfoList.get(1);
      this.results =
              new double[this.colInfoList.size()][this.rowInfoList.size()];
      this.digitalResults =
              new int[this.colInfoList.size()][this.rowInfoList.size()];
      this.tableTermList = (ArrayList) rowAndColInfoList.get(2);
      this.itemGridList = (ArrayList) rowAndColInfoList.get(3);
      this.rowSerialNo = (String) rowAndColInfoList.get(4);
      this.colSerialNo = (String) rowAndColInfoList.get(5);
      this.itemGridArea = (int[]) rowAndColInfoList.get(6);
      this.tnameVo = this.getTnameVoById(this.tabid);
      this.unitcode = this.getUnitcode(userName);
      this.usrID = usrID;
      this.scopeMap = this.getScopeMap();
      this.initDataScope();
      this.dbmodel = new DBMetaModel(this.conn);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public TnameBo(Connection conn, String tabid, String usrID, String userName,
                 String userview) {
    try {
      this.conn = conn;
      // this.userview=userview;
      this.tgridBo = new TgridBo(conn);
      this.midVariableBo = new MidVariableBo(conn);
      this.tpageBo = new TpageBo(conn);
      this.tabid = tabid;
      this.gridList = this.tgridBo.getGridInfoList(tabid);
      this.midVariableList = this.midVariableBo.getMidVariableList3(tabid);
      this.midVariableNotContrlList =
              this.tgridBo.MidVariableNotContrlList(tabid, this.midVariableList);
      // 分析表格,得到报表横表栏和纵表栏的相关信息集合
      ArrayList rowAndColInfoList =
              this.tgridBo.getRowAndColInfoList2(this.gridList, tabid, this.rowMap,
                      this.colMap, this.rowInfoBGrid, this.colInfoBGrid);
      this.rowInfoList = (ArrayList) rowAndColInfoList.get(0);
      this.colInfoList = (ArrayList) rowAndColInfoList.get(1);
      this.results =
              new double[this.colInfoList.size()][this.rowInfoList.size()];
      this.digitalResults =
              new int[this.colInfoList.size()][this.rowInfoList.size()];
      this.tableTermList = (ArrayList) rowAndColInfoList.get(2);
      this.itemGridList = (ArrayList) rowAndColInfoList.get(3);
      this.dataArea = (RecordVo) rowAndColInfoList.get(4);
      this.rowSerialNo = (String) rowAndColInfoList.get(5);
      this.colSerialNo = (String) rowAndColInfoList.get(6);
      this.itemGridArea = (int[]) rowAndColInfoList.get(7);
      this.tnameVo = this.getTnameVoById(this.tabid);
      this.userName = userName;
      this.usrID = usrID;
      this.unitcode = this.getUnitcode(userName);
      this.scopeMap = this.getScopeMap();
      this.initDataScope();
      ArrayList pageAndParamList =
              this.tpageBo.getPageListAndTparam(tabid, this.tnameVo
                      .getInt("tsortid"), userName);
      this.pageList = (ArrayList) pageAndParamList.get(0);
      this.paramMap = (HashMap) pageAndParamList.get(1);
      this.paramenameList = (ArrayList) pageAndParamList.get(2);
      this.minTop_px = this.tpageBo.getMinTop_px();
      this.dbmodel = new DBMetaModel(this.conn);
      this.tgridBo.setDbList(this.dbList);
      this.tgridBo.setResult(this.result);
      this.tgridBo.setUserview(this.userview);

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /*
   * public TnameBo(Connection conn, String tabid, String usrID, String
   * userName,
   * UserView userview) {
   * this.userview = userview;
   * this.initData(conn, tabid, usrID, userName);
   * }
   */

  // 根据unitcode构造TnameBo
  public TnameBo(String tabid, String unitcode, String userName, String usrID,
                 Connection conn) {
    try {
      this.conn = conn;
      this.tgridBo = new TgridBo(conn);
      this.midVariableBo = new MidVariableBo(conn);
      this.tpageBo = new TpageBo(conn);
      this.tabid = tabid;
      this.gridList = this.tgridBo.getGridInfoList(tabid);
      this.midVariableList = this.midVariableBo.getMidVariableList3(tabid);
      // 分析表格,得到报表横表栏和纵表栏的相关信息集合
      ArrayList rowAndColInfoList =
              this.tgridBo.getRowAndColInfoList2(this.gridList, tabid, this.rowMap,
                      this.colMap, this.rowInfoBGrid, this.colInfoBGrid);
      this.rowInfoList = (ArrayList) rowAndColInfoList.get(0);
      this.colInfoList = (ArrayList) rowAndColInfoList.get(1);
      this.results =
              new double[this.colInfoList.size()][this.rowInfoList.size()];
      this.digitalResults =
              new int[this.colInfoList.size()][this.rowInfoList.size()];
      this.tableTermList = (ArrayList) rowAndColInfoList.get(2);
      this.itemGridList = (ArrayList) rowAndColInfoList.get(3);
      this.dataArea = (RecordVo) rowAndColInfoList.get(4);
      this.rowSerialNo = (String) rowAndColInfoList.get(5);
      this.colSerialNo = (String) rowAndColInfoList.get(6);
      this.itemGridArea = (int[]) rowAndColInfoList.get(7);
      this.tnameVo = this.getTnameVoById(this.tabid);
      this.userName = userName;
      this.usrID = usrID;
      this.unitcode = unitcode;
      this.scopeMap = this.getScopeMap();
      this.initDataScope();
      ArrayList pageAndParamList =
              this.tpageBo.getPageListAndTparam2(tabid, this.tnameVo
                      .getInt("tsortid"), unitcode);
      this.pageList = (ArrayList) pageAndParamList.get(0);
      this.paramMap = (HashMap) pageAndParamList.get(1);
      this.paramenameList = (ArrayList) pageAndParamList.get(2);
      this.minTop_px = this.tpageBo.getMinTop_px();
      this.dbmodel = new DBMetaModel(this.conn);

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }


  /**
   * 获取模板底部位置
   * @param tabid
   * @return
   * @throws GeneralException
   */
  public int getBottomHeight() {
    ContentDAO dao=new ContentDAO(this.conn);
    RowSet rs=null;
    int max_num=0;
    try {
      rs=dao.search("select rtop+rheight from TGrid3 where TabId="+this.tabid+" and  flag=3");
      while(rs.next()) {
        max_num=rs.getInt(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      PubFunc.closeDbObj(rs);
    }
    return max_num;
  }

  private int[] getElementPostion(String tabid) {
    String sql = " select min(rtop)  a,max(rleft)  b,max(rtop) c,min(rleft) d from tgrid2 where tabid= "+tabid;
    ContentDAO dao = new ContentDAO(conn);
    RowSet rs = null;
    int top = 0;
    int right = 0;
    int bottom = 0;
    int left = 0;
    try {
      rs = dao.search(sql);
      while(rs.next()){
        top = rs.getInt("a");
        right = rs.getInt("b");
        bottom  = rs.getInt("c");
        left = rs.getInt("d");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      PubFunc.closeDbObj(rs);
    }
    return new int[]{top,right,bottom,left};
  }

  public static void main(String[] arg) {
    String aa = "((@11-@144+@1)/34)";
    String a_index = "@1";
    String operate = "";
    String a = aa.toString();
    boolean flag = true;
    while (flag && a.length() > 0) {
      if (a.indexOf(a_index) + a_index.length() < a.length()) {
        operate =
                a.substring(a.indexOf(a_index) + a_index.length(), a
                        .indexOf(a_index)
                        + a_index.length() + 1);
      }
      else {
        operate = "";
      }
      if ("(".equals(operate) || ")".equals(operate) || "+".equals(operate)
              || "-".equals(operate) || "*".equals(operate) || "/".equals(operate)
              || "".equals(operate)) {
        flag = false;
      }
      else {
        a = a.substring(a.indexOf(a_index) + a_index.length());
      }
    }
    // System.out.println(":"+operate);
    String b =
            aa
                    .replaceAll(a_index + "[\\+|\\-|\\*|\\/|\\(|\\)|\\D]", "C2"
                            + operate);
    // System.out.println(b.substring(1,b.length()-1));

  }

  public void alertColumn(String tableName, Field _item, DbWizard dbw,
                          ContentDAO dao) {
    try {
      Field item = (Field) _item.clone();
      Table table = new Table(tableName);
      String item_id = item.getName();
      item.setName(item_id + "_x");
      // TableModel tm=new TableModel(tableName);

      RowSet rowSet = dao.search("select * from " + tableName + " where 1=2");
      ResultSetMetaData data = rowSet.getMetaData();
      HashMap columnMap = new HashMap();
      for (int i = 1; i <= data.getColumnCount(); i++) {
        columnMap.put(data.getColumnName(i).toLowerCase(), "1");
      }

      // if(!dbw.isExistField(tableName, item_id+"_x"))
      if (columnMap.get(item_id.toLowerCase() + "_x") == null) {
        ;
      }
      {
        table.addField((Field) item.clone());
        dbw.addColumns(table);
      }

      int length = item.getLength();
      dao.update("update " + tableName + " set " + item_id
              + "_x=substr(to_char(" + item_id + "),0," + length + ")");
      table.clear();

      item.setName(item_id);
      table.addField((Field) item.clone());
      dbw.dropColumns(table);
      dbw.addColumns(table);

      dao.update("update " + tableName + " set " + item_id + "=" + item_id
              + "_x");
      table.clear();
      item.setName(item_id + "_x");
      table.addField((Field) item.clone());
      dbw.dropColumns(table);
      item.setName(item_id);
      if (rowSet != null) {
        rowSet.close();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 报表归档表维护
   *
   * @param tabid
   */
  public void anaylseReportFileStruct(String tabid) {
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      RowSet recset = null;
      DbWizard dbWizard = new DbWizard(this.conn);
      String tname = "ta_" + tabid;
      Table table = new Table("ta_" + tabid);
      if (dbWizard.isExistTable(tname, false)) {
        recset = dao.search("select * from ta_" + tabid + " where 1=2");
        ResultSetMetaData data = recset.getMetaData();

        // 动态维护已归档数据(没按格标识信息),但现在表结构又已加上格标识信息的内容.
        this.dynamicMaintainTAStruct(tabid, data, dbWizard, dao);
        // 动态添加
        int isAdd = 0;
        for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
          RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
          String fieldname = "C" + (i + 1);
          if (vo.getString("archive_item") != null
                  && !"".equals(vo.getString("archive_item"))
                  && !" ".equals(vo.getString("archive_item"))) {
            fieldname = vo.getString("archive_item");
          }
          boolean isExistColumn = false;
          for (int a = 0; a < data.getColumnCount(); a++) {
            String columnName = data.getColumnName(a + 1).toLowerCase();
            if (columnName.equals(fieldname.toLowerCase())) {
              isExistColumn = true;
            }
            if (isExistColumn) {
              break;
            }
          }
          if (!isExistColumn) {
            Field obj = this.tgridBo.getField2(fieldname, fieldname, "N");
            table.addField(obj);
            isAdd++;
          }
        }
        if (isAdd > 0) {
          dbWizard.addColumns(table);
          if (this.dbmodel == null) {
            this.dbmodel = new DBMetaModel(this.conn);
          }
          this.dbmodel.reloadTableModel(table.getName());
        }

        RecordVo vo2 = new RecordVo(table.getName());
        if (!vo2.hasAttribute("weekid")) {

          table = new Table("ta_" + tabid);
          Field obj = new Field("weekid", "weekid");
          obj.setDatatype(DataType.INT);
          obj.setAlign("left");
          table.addField(obj);
          dbWizard.addColumns(table);
          if (this.dbmodel == null) {
            this.dbmodel = new DBMetaModel(this.conn);
          }
          this.dbmodel.reloadTableModel("ta_" + tabid);
        }

        // 动态删除列
        Table table2 = new Table("ta_" + tabid);
        int isdelete = 0;
        for (int a = 0; a < data.getColumnCount(); a++) {
          String columnName = data.getColumnName(a + 1).toLowerCase();
          if (!"unitcode".equals(columnName) && !"secid".equals(columnName)
                  && !"yearid".equals(columnName) && !"weekid".equals(columnName)
                  && !"countid".equals(columnName)
                  && !"row_item".equals(columnName)) {
            boolean isExistColumn = false;
            for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
              RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
              String fieldname = "C" + (i + 1);
              if (vo.getString("archive_item") != null
                      && !"".equals(vo.getString("archive_item"))
                      && !" ".equals(vo.getString("archive_item"))) {
                fieldname = vo.getString("archive_item").toLowerCase();
              }
              if (columnName.equals(fieldname.toLowerCase())
                      || "scopeid".equals(columnName)) {
                isExistColumn = true;
              }
              if (isExistColumn) {
                break;
              }
            }
            if (!isExistColumn) {
              Field obj = this.tgridBo.getField2(columnName, columnName, "N");
              table2.addField(obj);
              isdelete++;
            }
          }

        }
        if (data != null) {
          data = null;
        }
        if (isdelete > 0) {
          dbWizard.dropColumns(table2);
          if (this.dbmodel == null) {
            this.dbmodel = new DBMetaModel(this.conn);
          }
          this.dbmodel.reloadTableModel(table2.getName());
        }
      }
      else {
        ArrayList fieldList = this.getTa_TableFields();
        for (Iterator t = fieldList.iterator(); t.hasNext();) {
          Field temp = (Field) t.next();
          table.addField(temp);
        }
        table.setCreatekey(false);
        dbWizard.createTable(table);
        if (this.dbmodel == null) {
          this.dbmodel = new DBMetaModel(this.conn);
        }
        this.dbmodel.reloadTableModel(table.getName());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 表参数结构维护
   *
   * @param tabid
   */
  public void anaylseReportParamStruct(String tabid) {

    try {
      ContentDAO dao = new ContentDAO(this.conn);
      String sortid = "";
      RowSet rowSet =
              dao.search("select tsortid from tname where tabid=" + tabid);
      if (rowSet.next()) {
        sortid = rowSet.getString(1);
      }

      HashMap commParamMap = new HashMap();
      HashMap sortParamtMap = new HashMap();
      HashMap tabParamMap = new HashMap();

      rowSet =
              dao
                      .search("select tparam.* from tpage,tparam where tpage.hz=tparam.paramname and  tpage.tabid ="
                              + tabid + " and tpage.flag=9");
      while (rowSet.next()) {
        String paramename = rowSet.getString("paramename");
        String paramtype = rowSet.getString("paramtype");
        String paramlen = rowSet.getString("paramlen");
        String paramfmt = rowSet.getString("paramfmt");
        int paramscope = rowSet.getInt("paramscope");

        LazyDynaBean abean = new LazyDynaBean();
        abean.set("paramename", paramename);
        abean.set("paramtype", paramtype);
        abean.set("paramlen", paramlen);
        abean.set("paramfmt", paramfmt);
        if (paramscope == 0) {
          commParamMap.put(paramename.toLowerCase(), abean);
        }
        else if (paramscope == 1) {
          sortParamtMap.put(paramename.toLowerCase(), abean);
        }
        else if (paramscope == 2) {
          tabParamMap.put(paramename.toLowerCase(), abean);
        }
      }

      String tname = "";

      DbWizard dbWizard = new DbWizard(this.conn);
      if (commParamMap.size() > 0) {
        tname = "tt_p";
        this.anaylseReportParamStruct2(commParamMap, tname, dbWizard);
      }

      if (sortParamtMap.size() > 0) {
        tname = "tt_s" + sortid;
        this.anaylseReportParamStruct2(sortParamtMap, tname, dbWizard);
      }

      if (tabParamMap.size() > 0) {
        tname = "tt_t" + tabid;
        this.anaylseReportParamStruct2(tabParamMap, tname, dbWizard);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void anaylseReportParamStruct2(HashMap map, String tname,
                                        DbWizard dbWizard) {
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      if (!dbWizard.isExistTable(tname, false)) {
        return;
      }
      RowSet rowSet = dao.search("select * from " + tname + " where 1=2");
      // RowSet rowSet=dao.search("select * from usra01 where 1=2");
      ResultSetMetaData metaData = rowSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        int columnType = metaData.getColumnType(i + 1);
        String columnTypeName = metaData.getColumnTypeName(i + 1);
        String columnName = metaData.getColumnName(i + 1);
        int nlen = 0;
        if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
          nlen = metaData.getPrecision(i + 1);
        }
        else {
          nlen = metaData.getColumnDisplaySize(i + 1);
        }
        // System.out.println(columnType+"  "+columnTypeName+"  "+columnName);
        if (map.get(columnName.toLowerCase()) != null) {
          LazyDynaBean abean = (LazyDynaBean) map.get(columnName.toLowerCase());
          String paramtype = (String) abean.get("paramtype");
          String paramlen = (String) abean.get("paramlen");
          if (paramlen == null && paramlen.length() < 0) {
            paramlen = "0";
          }
          String paramfmt = (String) abean.get("paramfmt");

          if ("数值".equals(paramtype)) {
            if (!"0".equals(paramfmt) && "int".equals(columnTypeName)) {
              Table table = new Table(tname.toLowerCase());
              table.addField(this.tgridBo.getField2(columnName.toUpperCase(),
                      columnName.toUpperCase(), "I"));
              dbWizard.dropColumns(table);
              table = new Table(tname.toLowerCase());
              table.addField(this.tgridBo.getField2(columnName.toUpperCase(),
                      columnName.toUpperCase(), "N"));
              dbWizard.addColumns(table);

            }
            else if ("0".equals(paramfmt) && !"int".equals(columnTypeName)) {
              Table table = new Table(tname.toLowerCase());
              table.addField(this.tgridBo.getField2(columnName.toUpperCase(),
                      columnName.toUpperCase(), "N"));
              dbWizard.dropColumns(table);
              table = new Table(tname.toLowerCase());
              table.addField(this.tgridBo.getField2(columnName.toUpperCase(),
                      columnName.toUpperCase(), "I"));
              dbWizard.addColumns(table);
            }
          }
          else if ("字符".equals(paramtype)) {
            if ("0".equals(paramfmt) && nlen < Integer.parseInt(paramlen)) {
              Field field = new Field(columnName.toUpperCase());
              field.setDatatype(DataType.STRING);
              field.setLength(Integer.parseInt(paramlen));
              Table table = new Table(tname.toLowerCase());

              if (Sql_switcher.searchDbServer() != 2) // 不为oracle
              {
                table.addField(field);
                dbWizard.alterColumns(table);
                table.clear();
              }
              else {
                this.alertColumn(tname.toLowerCase(), field, dbWizard, dao);
              }

            }
          }
        }
      }

      this.dbmodel.reloadTableModel(tname);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void anaylseReportStruct(String tabid) {
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      DbWizard dbWizard = new DbWizard(this.conn);

      for (int j = 1; j < 3; j++) {

        String tname2 = "tb" + tabid;
        if (j == 2) {
          tname2 = "tt_" + tabid;
        }
        if (dbWizard.isExistTable(tname2, false)) {
          RowSet rowSet = dao.search("select * from " + tname2 + "  where 1=2");
          ResultSetMetaData metaData = rowSet.getMetaData();
          int c_count = metaData.getColumnCount() - 2;

          Table add_table = new Table(tname2);
          Table del_table = new Table(tname2);
          HashMap existMap = new HashMap();
          for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            if (columnName.charAt(0) == 'C' || columnName.charAt(0) == 'c') {
              existMap.put(columnName.toUpperCase(), "1");
            }
          }

          HashMap actMap = new HashMap();
          for (int i = 1; i <= this.rowInfoList.size(); i++) {
            String name = "C" + i;
            if (existMap.get(name) == null) {
              add_table.addField(this.tgridBo.getField2("C" + i, "C" + i, "N"));
            }
            actMap.put(name.toUpperCase(), "1");
          }

          for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            if (columnName.charAt(0) == 'C' || columnName.charAt(0) == 'c') {
              if (actMap.get(columnName.toUpperCase()) == null) {
                del_table.addField(this.tgridBo.getField2(columnName,
                        columnName, "N"));
              }

            }
          }
          if (add_table.size() > 0) {
            dbWizard.addColumns(add_table);
          }
          if (del_table.size() > 0) {
            dbWizard.dropColumns(del_table);
          }

        }
        else {
          Table table = new Table(tname2);
          ArrayList fieldList = new ArrayList();
          if (j == 1) {
            fieldList = this.tgridBo.getTB_TableFields(this.rowInfoList.size());
          }
          else if (j == 2) {
            fieldList = this.tgridBo.getTT_TableFields(this.rowInfoList.size());
          }
          for (Iterator t = fieldList.iterator(); t.hasNext();) {
            Field temp = (Field) t.next();
            table.addField(temp);
          }
          dbWizard.createTable(table);
        }
      }
      this.anaylseReportParamStruct(tabid);
      this.anaylseReportFileStruct(tabid);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 批量取数
   *
   * @param tabid 表号
   * @param userid 用户id
   * @param conditionSql 权限控制语句
   * @param isSuper_admin 是否是超级用户
   * @param tableTermsMap 表条件
   * @return info 0:成功 1:指标没有构库 2.插入数据出错 3.批量取数错误 4:报表未定义条件项
   */
  public int auto_fill_report(String userid, String username,
                              String conditionSql, HashMap tableTermsMap, UserView userview,
                              String updateflag) {
    int info = 0;
    try {
      this.userview = userview;
      this.tgridBo.setUserview(this.userview);
      // 得到报表条件因子里包含的标识
      ArrayList factorList =
              this.tgridBo.getFactor(this.tabid, this.gridList, this.rowInfoList,
                      this.colInfoList, this.midVariableList, tableTermsMap);

      int num = 0;
      for (int i = 0; i < 6; i++) {
        if (((HashSet) factorList.get(i)).size() != 0) {
          num++;
        }
      }
      /* 报表未定义条件项 */
      if (num == 0 && tableTermsMap.size() == 0) {
        info = 4;
        return info;
      }

      // 创建临时表
      ArrayList list =
              this.tgridBo.creatTempTable(factorList, username,
                      this.midVariableList);
      info = Integer.parseInt((String) list.get(6)); // 是否有指标没有构库 1:没构库
      if (info == 0) {
        // 往临时表里存入数据
        if (!this.insertTempTable(username, userid, list, tableTermsMap)) {
          info = 2;
          return info;
        }

        filterResult(tableTermsMap);

        // 批量取数
        if (!this.batchFillReport(username, tableTermsMap, updateflag)) {
          info = 3;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return info;
  }

  /**
   * 保存编辑后的报表信息
   *
   * @param operateObject // 1：编辑没上报表 2：编辑上报后的表
   * @param flagtype 计算公式类型 1表内行计算，2表内列计算，3表间行计算，4表间列计算，5表建格计算，6汇总计算,7:接收上报盘
   *          自动取数
   * @param secid 表行id
   * @param cid 表字段
   * @param tabid 表id
   * @param userName 用户名称
   * @return
   */
  public void autoUpdateDigitalResults(String operateObject, String flagtype,
                                       String secid, String cid, String tabid, String userName, String unitcode) {
    this.getBusinessDate();// 获得业务日期
    String info = "1";
    ContentDAO dao = new ContentDAO(this.conn);
    ArrayList resultList = null;
    RowSet recset = null;
    // 1: update tb1 set c1=12.1,c2=''... where username='' and secid="+secid+";
    // 2: update tb1 set "+cid+"=12.1 where username='' and secid="1";
    // ...
    // 3: update tb1 set c1=12.1,c2=''... where username='' and secid="+secid+";
    // 4: update tb1 set "+cid+"=12.1 where username='' and secid="1";
    // ..
    // 5: update tb1 set "+cid+"=12.1 where username='' and secid="+secid+" ;
    // 6: update tb1 set "+cid+"=12.1 where username='' and secid="+1+" ;
    // ..
    try {
      ReportResultBo resultbo = new ReportResultBo(this.conn);
      // 保存数据
      if ("1".equals(operateObject)) {
        resultList = resultbo.getTBxxResultList(tabid, userName);
      }
      else {
        resultList = resultbo.getTTxxResultList(tabid, unitcode);

      }
      ArrayList objectList = new ArrayList();
      TnameBo tnameBo = new TnameBo(this.conn, tabid); // FIXME 提速
      if (resultList.size() > 0) {

        for (int i = 0; i < resultList.size(); i++) {
          if ("1".equals(flagtype)) {
            if (!secid.equals(i + 1 + "")) {
              continue;
            }
          }
          else if ("2".equals(flagtype)) {
          }
          else if ("3".equals(flagtype)) {
            if (!secid.equals(i + 1 + "")) {
              continue;
            }
          }
          else if ("4".equals(flagtype)) {

          }
          else if ("5".equals(flagtype)) {
            if (!secid.equals(i + 1 + "")) {
              continue;
            }
          }
          else if ("6".equals(flagtype)) {
          }

          ArrayList subList = new ArrayList();
          RecordVo colVo = (RecordVo) tnameBo.getColInfoBGrid().get(i);

          // subList.add(new Integer(i+1));
          String[] result_arr = (String[]) resultList.get(i);
          // 判断是否要更新
          ArrayList list = new ArrayList();
          list.add(tabid);

          for (int j = 0; j < result_arr.length; j++) {
            if ("1".equals(flagtype)) {
            }
            else if ("2".equals(flagtype)) {
              if (!cid.equalsIgnoreCase("c" + (j + 1))) {
                continue;
              }

            }
            else if ("3".equals(flagtype)) {
            }
            else if ("4".equals(flagtype)) {
              if (!cid.equalsIgnoreCase("c" + (j + 1))) {
                continue;
              }
            }
            else if ("5".equals(flagtype)) {
              if (!cid.equalsIgnoreCase("c" + (j + 1))) {
                continue;
              }
            }
            else if ("6".equals(flagtype)) {
              if (!cid.equalsIgnoreCase("c" + (j + 1))) {
                continue;
              }
            }

            int npercent = 0;
            RecordVo rowVo = (RecordVo) tnameBo.getRowInfoBGrid().get(j);
            // if(rowVo.getInt("flag1")==3)
            // npercent=rowVo.getInt("npercent");
            // else if(colVo.getInt("flag1")==3)
            // npercent=colVo.getInt("npercent");
            // else
            npercent =
                    rowVo.getInt("npercent") >= colVo.getInt("npercent") ? rowVo
                            .getInt("npercent") : colVo.getInt("npercent");

            if ("1".equals(operateObject)) {
              subList.add(new Double(PubFunc.round(result_arr[j], npercent)));

            }
            else {
              subList.add(new Double(PubFunc.round(result_arr[j], npercent)));
            }

            if ("2".equals(flagtype)) {
              if ("1".equals(operateObject)) {
                subList.add(userName);
              }
              else {
                subList.add(unitcode);
              }
              subList.add(i + 1 + "");
              objectList.add(subList);
            }
            else if ("4".equals(flagtype)) {
              if ("1".equals(operateObject)) {
                subList.add(userName);
              }
              else {
                subList.add(unitcode);
              }
              subList.add(i + 1 + "");
              objectList.add(subList);

            }
            else if ("6".equals(flagtype)) {
              if ("1".equals(operateObject)) {
                subList.add(userName);
              }
              else {
                subList.add(unitcode);
              }
              subList.add(i + 1 + "");
              objectList.add(subList);
            }

          }
          if ("1".equals(flagtype)) {
            if ("1".equals(operateObject)) {
              subList.add(userName);
            }
            else {
              subList.add(unitcode);
            }
            subList.add(secid);
            objectList.add(subList);
          }
          else if ("3".equals(flagtype)) {
            if ("1".equals(operateObject)) {
              subList.add(userName);
            }
            else {
              subList.add(unitcode);
            }
            subList.add(secid);
            objectList.add(subList);
          }
          else if ("5".equals(flagtype)) {
            if ("1".equals(operateObject)) {
              subList.add(userName);
            }
            else {
              subList.add(unitcode);
            }
            subList.add(secid);
            objectList.add(subList);
          }
          else if ("7".equals(flagtype)) {
            if ("1".equals(operateObject)) {
              subList.add(userName);
            }
            else {
              subList.add(unitcode);
            }
            subList.add(i + 1 + "");
            objectList.add(subList);
          }
        }
        StringBuffer sql = new StringBuffer();
        StringBuffer sql_f = new StringBuffer("");
        StringBuffer sql_where = new StringBuffer("");
        if ("1".equals(operateObject)) {
          sql_f.append("update  tb" + tabid + "  set  ");
          sql_where.append(" where username=? and secid=? ");
        }
        else {
          sql_f.append("update  tt_" + tabid + " set  ");
          sql_where.append(" where unitcode=? and secid=? ");
        }
        String[] result_arr = (String[]) resultList.get(0);
        for (int i = 0; i < result_arr.length; i++) {
          if ("1".equals(flagtype)) {
            sql.append(",C" + (i + 1) + "=?");
          }
          else if ("2".equals(flagtype)) {
            sql.append("," + cid + "=?");
            break;
          }
          else if ("3".equals(flagtype)) {
            sql.append(",C" + (i + 1) + "=?");
          }
          else if ("4".equals(flagtype)) {
            sql.append("," + cid + "=?");
            break;
          }
          else if ("5".equals(flagtype)) {
            sql.append("," + cid + "=?");
            break;
          }
          else if ("6".equals(flagtype)) {
            sql.append("," + cid + "=?");
            break;
          }
          else if ("7".equals(flagtype)) {
            sql.append(",C" + (i + 1) + "=?");
          }
        }
        sql_f.append(" " + sql.toString().substring(1) + " ");
        sql_f.append(sql_where);
        dao.batchUpdate(sql_f.toString(), objectList);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (recset != null) {
          recset.close();
        }

      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * 批量取数
   *
   * @param aNameList 人员库临时表中包含的列名
   * @param userName 用户名
   * @return boolean
   */
  public boolean batchFillReport(String userName, HashMap tableTermsMap,
                                 String updateflag) {
    boolean flag = true;
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    RowSet rs = null;
    ArrayList updatelist = new ArrayList();
    ArrayList updaterowlimit = new ArrayList();
    String saveAppDate = ConstantParamter.getAppdate(userName);
    try {
      DbWizard dbWizard = new DbWizard(this.conn);
      if (!dbWizard.isExistTable("historyData_report", false)) {
        Table table = new Table("historyData_report");

        Field field = new Field("username", "username");
        field.setDatatype(DataType.STRING);
        field.setLength(30);
        field.setKeyable(true);
        field.setNullable(false);
        table.addField(field);

        field = new Field("column_name", "column_name");
        field.setDatatype(DataType.STRING);
        field.setLength(30);
        field.setKeyable(true);
        field.setNullable(false);
        table.addField(field);

        field = new Field("value", "value");
        field.setDatatype(DataType.FLOAT);
        field.setLength(15);
        field.setDecimalDigits(4);
        field.setNullable(true);
        table.addField(field);

        dbWizard.createTable(table);
        DBMetaModel dbmodel = new DBMetaModel(this.conn);
        dbmodel.reloadTableModel(table.getName());
      }
      // tbxxx数据存二维数组
      ReportResultBo resultbo = new ReportResultBo(this.conn);
      resultbo.setColinfolist(this.colInfoList);
      ArrayList resultlist = resultbo.getTBxxResultList(this.tabid, userName);
      for (int i = 0; i < this.colInfoList.size(); i++) // 循环行
      {

        dao.update("delete from historyData_report where username='"
                + this.userview.getUserName().toLowerCase() + "'");
        if (this.rowInfoList.size() > 70) // 解决表太大，造成sql语句出错
        {
          ArrayList colTermList = (ArrayList) this.colInfoList.get(i);
          this.getUpTbxxByRowLimit(colTermList, i, resultlist, updaterowlimit);// xgq
          // 2011
          // 02
          // 09
          String _sql = "";
          for (int j = 0; j < this.rowInfoList.size(); j++) {
            ArrayList rowTermList = (ArrayList) this.rowInfoList.get(j);
            _sql =
                    this.getPerGridSql(rowTermList, colTermList, i, j, userName,
                            tableTermsMap);
            this.getUpTbxxByNotCondition(rowTermList, colTermList, i, j,
                    resultlist, updatelist);// xgq 2010 03 15
            recset = dao.search(_sql.substring(3, _sql.length() - 6));
            if (recset.next()) {
              // 按小数位取数
              int digital = this.digitalResults[i][j];
              this.results[i][j] =
                      Double.parseDouble(PubFunc.round(String.valueOf(recset
                              .getDouble(1)), digital));
            }

          }

        }
        else {
          ArrayList colTermList = (ArrayList) this.colInfoList.get(i);
          StringBuffer subSql = new StringBuffer("select ");
          StringBuffer subSel = new StringBuffer("");
          StringBuffer subWhl = new StringBuffer("");
          this.getUpTbxxByRowLimit(colTermList, i, resultlist, updaterowlimit);// xgq
          // 2011
          // 02
          // 09

          for (int j = 0; j < this.rowInfoList.size(); j++) // 循环列
          {
            subSel.append(",v" + j);
            ArrayList rowTermList = (ArrayList) this.rowInfoList.get(j);
            subWhl.append(this.getPerGridSql(rowTermList, colTermList, i, j,
                    userName, tableTermsMap));
            this.getUpTbxxByNotCondition(rowTermList, colTermList, i, j,
                    resultlist, updatelist);// xgq 2010 03 15

          }
          subSql.append(subSel.substring(1));
          subSql.append(" from ");
          subSql.append(subWhl.substring(1));
          String aa = subWhl.toString().substring(3, subWhl.length() - 6);
          String tempSql = subSql.toString();

          recset = dao.search(tempSql); // 一行中所有列的数据

          if (recset.next()) {
            for (int k = 0; k < this.results[i].length; k++) {
              // 按小数位取数
              int digital = this.digitalResults[i][k];
              this.results[i][k] =
                      Double.parseDouble(PubFunc.round(String.valueOf(recset
                              .getDouble(k + 1)), digital));
            }
          }

        }

      }

      this.insertResultTable(this.results, userName, this.tabid); // 将结果保存到数据库中
      if (updaterowlimit.size() > 0) {
        dao.batchUpdate(updaterowlimit);
      }
      if (updatelist.size() > 0 && "1".equals(updateflag)) {
        dao.batchUpdate(updatelist);
      }

      // ---------归档数据取数 zhaoxg 2013-2-26------------------------
      for (int i = 0; i < this.getColInfoBGrid().size(); i++) {
        RecordVo colVo = (RecordVo) this.getColInfoBGrid().get(i);

        for (int j = 0; j < this.getRowInfoBGrid().size(); j++) {
          StringBuffer subSql = new StringBuffer("select ");
          StringBuffer subSel = new StringBuffer("");
          StringBuffer subWhl = new StringBuffer("");
          RecordVo rowVo = (RecordVo) this.getRowInfoBGrid().get(j);
          if ("5".equals(rowVo.getString("flag1"))) {
            if (!"3".equals(rowVo.getString("flag1"))
                    && !"4".equals(rowVo.getString("flag1"))) {
              // ArrayList rowTermList=(ArrayList)rowInfoBGrid.get(j);
              String strsql =
                      this.getTASql(this.tabid, this.unitcode, rowVo, colVo, i, j,
                              userName, tableTermsMap);
              if (strsql == null || "".equals(strsql)) {
                continue;
              }
              else {
                subSel.append("v");
                subWhl.append(strsql);
              }
              subSql.append(subSel);
              subSql.append(" from ");
              subSql.append(subWhl);

              String tempSql = subSql.toString();
              /* 标识：2371 定义了报表归档取值方法的，有张表取数的时候取不出来 xiaoyun 2014-6-9 start */
              tempSql += " a";
              /* 标识：2371 定义了报表归档取值方法的，有张表取数的时候取不出来 xiaoyun 2014-6-9 end */
              try { // wangcq 2015-1-9 报表未归档时，为不影响后面公式取数，让程序继续执行
                if (dbWizard.isExistTable("ta_" + this.tabid, false)) {// liuy
                  // 2015-1-22
                  // 6931：自动取数：对62号表重新取数，后台报错
                  rs = dao.search(tempSql);
                  if (rs.next()) {
                    StringBuffer sqll = new StringBuffer();
                    sqll.append("update tb" + this.tabid + " set C");
                    sqll.append(j + 1);
                    sqll.append("=");
                    String aa =
                            PubFunc.round(rs.getString("v"), Integer.parseInt(rowVo
                                    .getString("npercent")));
                    sqll.append(aa);
                    sqll.append("where secid=");
                    sqll.append(i + 1);
                    sqll.append("and username = '");
                    sqll.append(userName + "'");
                    dao.update(sqll.toString());
                  }
                }
              }
              catch (SQLException sqle) {
                sqle.printStackTrace();
              }
            }
          }
        }
      }
      // -============================================华丽的分割线===================
      for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
        RecordVo rowVo = (RecordVo) this.getRowInfoBGrid().get(i);

        for (int j = 0; j < this.getColInfoBGrid().size(); j++) {
          StringBuffer subSql = new StringBuffer("select ");
          StringBuffer subSel = new StringBuffer("");
          StringBuffer subWhl = new StringBuffer("");
          RecordVo colVo = (RecordVo) this.getColInfoBGrid().get(j);
          if ("5".equals(colVo.getString("flag1"))) {
            if (!"3".equals(colVo.getString("flag1"))
                    && !"4".equals(colVo.getString("flag1"))) {

              String strsql =
                      this.getTAsql1(this.tabid, this.unitcode, rowVo, colVo, i, j,
                              userName, tableTermsMap);
              if (strsql == null || "".equals(strsql)) {
                continue;
              }
              else {
                subSel.append("v");
                subWhl.append(strsql);
                /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-28 start */
                subWhl.append(" a");
                /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-28 end */
              }
              subSql.append(subSel);
              subSql.append(" from ");
              subSql.append(subWhl);

              String tempSql = subSql.toString();
              rs = dao.search(tempSql);
              if (rs.next()) {
                StringBuffer sqll = new StringBuffer();
                sqll.append("update tb" + this.tabid + " set C");
                sqll.append(i + 1);
                sqll.append("=");
                String aa =
                        PubFunc.round(rs.getString("v"), Integer.parseInt(colVo
                                .getString("npercent")));
                sqll.append(aa);
                sqll.append("where secid=");
                sqll.append(j + 1);
                sqll.append("and username = '");
                sqll.append(userName + "'");
                dao.update(sqll.toString());
              }
            }
          }
        }
      }
      // -------------------------------------------------------------

      // 求计算公式的值 先按列算 再按行算
      SQL_Util su = new SQL_Util();
      for (Iterator t = this.colCountExpreSet.iterator(); t.hasNext();) {
        String temp = (String) t.next();
        String[] temp1 = temp.split("#");
        int leftIndex = Integer.parseInt(temp1[0]);
        String rightExpre = temp1[1];
        this.calculate(rightExpre, 1, this.results, leftIndex, this.tabid,
                userName, su);

      }
      for (Iterator t = this.rowCountExpreSet.iterator(); t.hasNext();) {
        String temp = (String) t.next();
        String[] temp1 = temp.split("#");
        int leftIndex = Integer.parseInt(temp1[0]);
        if (temp1.length < 2) {
          continue;
        }
        String rightExpre = temp1[1];
        this.calculate(rightExpre, 2, this.results, leftIndex, this.tabid,
                userName, su);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (recset != null) {
          recset.close();
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      ConstantParamter.putAppdate(userName, saveAppDate);
    }
    return true;
  }

  /**
   * 根据计算公式求得其值
   *
   * @param expre 表达式
   * @param flag 2：横条件 1：列条件
   * @return
   */
  public void calculate(String expre, int flag, double[][] results, int index,
                        String tabid, String username, SQL_Util su) {

    ContentDAO dao = new ContentDAO(this.conn);

    try {
      if (flag == 1) {
        String a_expre = expre;
        ArrayList list = this.exprUtil.analyseStatExpr(a_expre);
        a_expre = "(" + a_expre + ")";

        for (int d = 0; d < list.size(); d++) {
          String temp = (String) list.get(d);
          String temp2 = "";
          int a_index = 0;
          String newValue = "";
          if (temp.indexOf("@") != -1) {

            temp2 = temp.substring(temp.indexOf("@") + 1);
            a_index = Integer.parseInt((String) this.colMap.get(temp2)) + 1;
            String operate = this.getOperate(a_expre, temp);
            a_expre =
                    a_expre.replaceAll(temp + "[\\" + operate + "]", "C" + a_index
                            + operate);
          }
        }

        String countColumn = "C" + (index + 1);
        String final_expre =
                su.sql_switch(a_expre.substring(1, a_expre.length() - 1));
        dao.update("update tb" + tabid + " set " + countColumn + "="
                + final_expre + " where userName='" + username + "'");

      }
      else {

        ArrayList list = this.exprUtil.analyseStatExpr(expre);
        ArrayList rows = new ArrayList();

        for (int i = 0; i < results[0].length; i++) {

          StringBuffer sql = new StringBuffer("");
          String a_expre = "(" + expre + ")";
          sql.append(", C" + (i + 1) + "=(");
          ArrayList rowid = new ArrayList();
          boolean a = false;
          for (int d = 0; d < list.size(); d++) {
            String temp = (String) list.get(d);
            String temp2 = "";
            int a_index = 0;
            if (temp.indexOf("@") != -1) {
              a = true;
              temp2 = temp.substring(temp.indexOf("@") + 1);
              String num =
                      String.valueOf(Integer.parseInt((String) this.rowMap
                              .get(temp2)) + 1);
              rowid.add(num);
              String operate = this.getOperate(a_expre, temp);
              a_expre =
                      a_expre.replaceAll(temp + "[\\" + operate + "]", ("a" + num
                              + ".C" + (i + 1) + operate));
              // a_expre=a_expre.replaceAll(temp,("a"+num+".C"+(i+1)));
            }
          }
          String final_expre = a_expre;
          if (a) {
            final_expre =
                    su.sql_switch(this.getSQL(a_expre.substring(1,
                            a_expre.length() - 1), i + 1, tabid, this.userName, rowid));
          }

          sql.append(final_expre);
          sql.append(" )");
          sql.append(" where userName='" + username + "' and secid="
                  + (index + 1));
          String tolSql = "update tb" + tabid + " set " + sql.substring(1);
          // System.out.println(tolSql.toString());
          dao.update(tolSql.toString());
        }
      }
      this.autoUpdateDigitalResults("1", "7", "", "", tabid, this.userName,
              this.unitcode);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 报表上报时判断上报的行列数是否与表样式中规定的行列数一致
   *
   * @param rows
   * @param cols
   * @return
   */
  public boolean checkRowColumnNumber(int rows, int cols) {
    boolean flag = false;
    if (rows == this.rowMap.size() && cols == this.colMap.size()) {
      flag = true;
    }
    return flag;
  }

  /*
   * 将tableName表的内容放入新建表tableName_c中
   * wangcq 2014-12-12
   */
  public void copyTempTable(String tableName, String tableName_c) {
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      String sql = "";
      if (Sql_switcher.searchDbServer() == 2) {
        sql = "create table " + tableName_c + " as select * from " + tableName;
      }
      else {
        sql = "select *  into " + tableName_c + "  from " + tableName;
      }
      DbWizard dbWizard = new DbWizard(this.conn);
      if (dbWizard.isExistTable(tableName_c, false)) {
        dbWizard.dropTable(tableName_c);
      }
      dao.update(sql);

      DBMetaModel dbmodel = new DBMetaModel(this.conn);
      dbmodel.reloadTableModel(tableName_c);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 动态维护已归档数据(没按格标识信息),但现在表结构又已加上格标识信息的内容.
  public void dynamicMaintainTAStruct(String tabid, ResultSetMetaData data,
                                      DbWizard dbWizard, ContentDAO dao) {

    try {
      if (this.getRowInfoBGrid().size() == 0) {
        return;
      }

      boolean isArchive_item = false;
      boolean isNumberColumn = false;
      for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
        RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
        if (vo.getString("archive_item") != null
                && !"".equals(vo.getString("archive_item"))
                && !" ".equals(vo.getString("archive_item"))) {
          isArchive_item = true;
          break;
        }
      }
      for (int a = 0; a < data.getColumnCount(); a++) {
        String columnName = data.getColumnName(a + 1).toLowerCase();
        if ("C1".equalsIgnoreCase(columnName)) {
          isNumberColumn = true;
          break;
        }
      }
      if (isArchive_item && isNumberColumn) {
        this.dynamicMaintainTAStruct2(tabid, data, dbWizard, dao);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void dynamicMaintainTAStruct2(String tabid, ResultSetMetaData data,
                                       DbWizard dbWizard, ContentDAO dao) {
    try {
      String tname = "ta_" + tabid;
      Table table = new Table("ta_" + tabid);
      for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
        RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
        String fieldname = "";
        if (vo.getString("archive_item") != null
                && !"".equals(vo.getString("archive_item"))
                && !" ".equals(vo.getString("archive_item"))) {
          fieldname = vo.getString("archive_item");
          Field obj = this.tgridBo.getField2(fieldname, fieldname, "N");
          table.addField(obj);
        }
      }
      dbWizard.addColumns(table);
      table = new Table("ta_" + tabid);
      for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
        RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
        String fieldname = "C" + (i + 1);
        String fieldname2 = "";
        if (vo.getString("archive_item") != null
                && !"".equals(vo.getString("archive_item"))
                && !" ".equals(vo.getString("archive_item"))) {
          fieldname2 = vo.getString("archive_item");
        }
        dao.update("update ta_" + tabid + " set " + fieldname2 + "="
                + fieldname);
        Field obj = this.tgridBo.getField2(fieldname, fieldname, "N");
        table.addField(obj);
      }
      dbWizard.dropColumns(table);
      for (int i = 0; i < this.getColInfoBGrid().size(); i++) {
        RecordVo vo = (RecordVo) this.getColInfoBGrid().get(i);
        String fieldname2 = "";
        if (vo.getString("archive_item") != null
                && !"".equals(vo.getString("archive_item"))
                && !" ".equals(vo.getString("archive_item"))) {
          fieldname2 = vo.getString("archive_item");
        }
        dao.update("update ta_" + tabid + " set row_item='" + fieldname2
                + "' where row_item='" + (i + 1) + "'");
      }
      if (this.dbmodel == null) {
        this.dbmodel = new DBMetaModel(this.conn);
      }
      this.dbmodel.reloadTableModel(table.getName());

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 生成绝对定位的背景页面
   *
   * @param top、left、width、height 表格的绝对位置
   */
  public String executeAbsoluteBackground(int top, int left, float width,
                                          float height, String html) {
    StringBuffer tempHtml = new StringBuffer("");

    tempHtml.append("<div id=idDIV ");
    tempHtml.append(" style='position:absolute;top:");
    tempHtml.append(top);
    tempHtml.append(";left:");
    tempHtml.append(left);
    tempHtml.append(";width:");
    tempHtml.append(width);
    tempHtml.append(";height:");
    tempHtml.append(height);
    tempHtml
            .append(";border:thin outset buttonface;background : #ffffff ;'> \n ");
    tempHtml.append(html);
    tempHtml.append("</div>");

    tempHtml.append("<div ");
    tempHtml.append(" style='position:absolute;top:");
    tempHtml.append(top);
    tempHtml.append(";left:");
    tempHtml.append(left + width);
    tempHtml.append(";width:");
    tempHtml.append(20);
    tempHtml.append(";height:");
    tempHtml.append(2);
    tempHtml.append(";'> \n ");
    tempHtml.append("&nbsp;</div>");
    return tempHtml.toString();
  }

  /**
   * 批量取数
   *
   * @param userview
   * @return
   * @see #auto_fill_report(String, String, String, HashMap, UserView, String)
   * @see #com ;
   */
  public boolean fillReport(UserView userview) {
    this.userview = userview;
    this.tgridBo.setUserview(this.userview);
    StringBuffer info = new StringBuffer("");
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      String usrID = this.userview.getUserId();
      String userName = this.userview.getUserName();
      // 权限控制
      String conditionSql = "";
      String operateObject = "1"; // 编辑报表
      String home = "";
      String zxgflag = "";
      String updateflag = "1"; // 仅定义统计条件单元格取数

      if (this.isApprove1(this.userview.getUserName())) {
        // userName = this.userview.getUserName();
        zxgflag = "0";
        // userView = new UserView(userName, this.frameconn);
        // userView.canLogin();
      }
      else {
        // userName = approve();// 不是负责人，找是不是有人报表给他
        zxgflag = "4";
        // if(userName==null||userName.equals("")){
        // userName = this.userview.getUserName();
        // }
        // userView = new UserView(userName, this.frameconn);
        // userView.canLogin();
      }

      ArrayList tableTermList = this.getTableTermList();
      // 兼容统计口径
      String scopeid = "";
      String units = "";
      if (scopeid != null && scopeid.length() > 0 && !"0".equals(scopeid)) {
        RecordVo vo = new RecordVo("tscope");
        vo.setInt("scopeid", Integer.parseInt(scopeid));
        try {
          vo = dao.findByPrimaryKey(vo);
          units = vo.getString("units");
        }
        catch (Exception e2) {

        }
      }
      ArrayList dbList = this.getDbList(); // 扫描库
      String result = this.getResult(); // 是否从结果表里取数
      boolean isResult = true;
      if (result != null && "true".equals(result)) {
        isResult = false;
      }
      HashMap tableTermsMap = new HashMap();
      HashSet tableTermFactorSet = new HashSet();

      for (int i = 0; i < dbList.size(); i++) {
        String pre = (String) dbList.get(i);
        // 表条件控制--人员库
        StringBuffer tableTermsConditionSql = new StringBuffer("");
        for (int a = 0; a < tableTermList.size(); a++) {
          String[] tableTerms = (String[]) tableTermList.get(a);
          if (tableTerms[3].length() > 1) {
            tableTermFactorSet.addAll(this.getTgridBo().getFactorSet(
                    tableTerms[3]));
            // 调用陈总提供的表达式分析器的到sql语句
            if (tableTerms[3].length() > 12
                    && tableTerms[3].indexOf("$THISUNIT[]") != -1) {

              if (userview.isSuper_admin()) {
                tableTerms[3] =
                        tableTerms[3].replaceAll("\\$THISUNIT\\[\\]", "*");
              }
              else {
                String unit_ids = userview.getUnit_id();
                if (unit_ids == null || unit_ids.trim().length() == 0
                        || "UN".equalsIgnoreCase(unit_ids.trim())) {
                  tableTerms[3] =
                          tableTerms[3].replaceAll("\\$THISUNIT\\[\\]", "##");
                }
                else {
                  String[] temps = unit_ids.split("`");
                  StringBuffer un = new StringBuffer("");
                  for (int j = 0; j < temps.length; j++) {
                    if (temps[j].trim().length() > 0) {
                      String temp = temps[j];
                      String pre2 = temp.substring(0, 2);
                      String value = temp.substring(2);
                      if ("UN".equalsIgnoreCase(pre2)) {
                        un.append("|" + value + "*");
                      }
                      else {
                        un.append("|" + this.getUnByUm(value, this.conn) + "*");
                      }
                    }

                  }
                  if (un.length() > 0) {
                    if (un.length() == 1) {// 条件为本单位且操作单位为全部，这时传*，sql拼成like ‘%’
                      // 形式 zhaoxg add 2013-12-31
                      tableTerms[3] =
                              tableTerms[3].replaceAll("\\$THISUNIT\\[\\]", "*");
                    }
                    else {
                      tableTerms[3] =
                              tableTerms[3].replaceAll("\\$THISUNIT\\[\\]", un
                                      .substring(1));
                    }
                  }
                  else {
                    tableTerms[3] =
                            tableTerms[3].replaceAll("\\$THISUNIT\\[\\]", "##");
                  }

                }

              }
            }
            // 起始日期 §§ 截止日期
            if (tableTerms[3].length() > 12
                    && (tableTerms[3].indexOf("$APPSTARTDATE[]") != -1 || tableTerms[3]
                    .indexOf("$APPDATE[]") != -1)) {
              Calendar d = Calendar.getInstance();
              String startdate = this.getStartdate();
              if (startdate == null || startdate.length() == 0) {
                startdate =
                        d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1)
                                + "-" + d.get(Calendar.DATE);
              }
              String _startdate = startdate.replaceAll("-", "\\.");
              if (tableTerms[3].indexOf("$APPSTARTDATE[]") != -1) {
                tableTerms[3] =
                        tableTerms[3].replaceAll("\\$APPSTARTDATE\\[\\]",
                                _startdate);
              }

              String _appdate = this.getAppdate();
              if (this.getAppdate() == null) {
                _appdate =
                        d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1)
                                + "-" + d.get(Calendar.DATE);
              }
              _appdate = _appdate.replaceAll("-", "\\.");
              if (tableTerms[3].indexOf("$APPDATE[]") != -1) {
                tableTerms[3] =
                        tableTerms[3].replaceAll("\\$APPDATE\\[\\]", _appdate);
              }
            }
            String strwhere = "";

            {
              String expr = tableTerms[4];
              String factor = tableTerms[3];
              boolean create_date = false;
              if (factor.indexOf("create_date") != -1) {
                factor = factor.replaceAll("create_date", "nbase");
                create_date = true;
              }
              FactorList factorlist =
                      new FactorList(expr, factor, pre, false, false, isResult, 1,
                              userName, "t#" + userview.getUserName() + "_tjb_A");
              if (create_date) {
                strwhere =
                        factorlist.getSingleTableSqlExpression("hr_emp_hisdata");
              }
              else {
                strwhere = factorlist.getSqlExpression();
              }
              if (strwhere.indexOf(pre + "t#") != -1) {
                strwhere = strwhere.replaceAll(pre + "t#", "t#");
              }
              if (create_date) {
                strwhere = strwhere.replaceAll("nbase", "create_date");
              }
            }
            // strwhere=" FROM "+pre+"A01 LEFT JOIN suAStatic ON suAStatic.a0100="+pre+"A01.a0100 WHERE (yk33>0) and lower(suAStatic.nbase)='"+pre.toLowerCase()+"' ";
            tableTermsConditionSql.append(" union  select " + pre
                    + "A01.A0100 " + strwhere);
          }

        }

        if (tableTermsConditionSql.length() > 2) {
          // 自动取数条件
          String sql = "";
          if (scopeid != null && scopeid.length() > 0 && !"0".equals(scopeid)) {
            String tablesql = tableTermsConditionSql.toString();

            String units2[] = units.split("`");
            String term3 = "and (";
            StringBuffer temp = new StringBuffer();
            for (int j = 0; j < units2.length; j++) {
              if (units2[j].indexOf("UN") != -1) {
                temp.append(" or " + pre + "A01.B0110 like '"
                        + units2[j].substring(2) + "%' ");
              }
              if (units2[j].indexOf("UM") != -1) {
                temp.append(" or " + pre + "A01.E0122 like '"
                        + units2[j].substring(2) + "%' ");
              }
            }
            if (temp.length() > 3) {
              term3 += temp.toString().substring(3) + ")";
            }
            if (term3.length() > 5) {
              tablesql =
                      tablesql.substring(0, tablesql.lastIndexOf(")")) + " "
                              + term3 + ")";
            }
            sql = "select * from (" + tablesql.substring(6) + " ) aaa ";
          }
          else {
            sql =
                    "select * from (" + tableTermsConditionSql.substring(6)
                            + " ) aaa ";
          }
          tableTermsMap.put(pre, sql);
        }
        else {
          String sql = "";
          if (scopeid != null && scopeid.length() > 0 && !"0".equals(scopeid)) {
            String tablesql =
                    " select " + pre + "A01.A0100 FROM " + pre + "A01 where 1=1  ";

            String units2[] = units.split("`");
            String term3 = "and (";
            StringBuffer temp = new StringBuffer();
            for (int j = 0; j < units2.length; j++) {
              if (units2[j].indexOf("UN") != -1) {
                temp.append(" or " + pre + "A01.B0110 like '"
                        + units2[j].substring(2) + "%' ");
              }
              if (units2[j].indexOf("UM") != -1) {
                temp.append(" or " + pre + "A01.E0122 like '"
                        + units2[j].substring(2) + "%' ");
              }
            }
            if (temp.length() > 3) {
              term3 += temp.toString().substring(3) + ")";
            }
            if (term3.length() > 5) {
              tablesql += term3;
            }
            else {
              tablesql = "";
            }
            if (tablesql.length() > 0) {
              sql = "select * from (" + tablesql + " ) aaa ";
            }
            tableTermsMap.put(pre, sql);
          }

        }
      }

      if (tableTermFactorSet.size() > 0) {
        // tableTermsMap.clear();
        this.setBorK_terms(tableTermsMap, tableTermFactorSet, tableTermList);

      }

      // info 0:成功 1:指标没有构库 2.插入数据出错 3.批量取数错误zs 4.没有设置条件
      int a_info =
              this.auto_fill_report(usrID, userName, conditionSql, tableTermsMap,
                      this.userview, updateflag);
      if (a_info == 0) {
        info.append(ResourceFactory.getProperty("edit_report.table")
                + this.tabid + ":"
                + ResourceFactory.getProperty("auto_fill_report.getDataSuccess")
                + "! \\n");
        // TTorganization tt_organization=new TTorganization(this.conn);
        // RecordVo
        // a_selfVo=tt_organization.getSelfUnit(this.userview.getUserName());//可以不用加有效日期
        // xgq
        // if(a_selfVo!=null){
        // String unitcode=a_selfVo.getString("unitcode");
        // dao.update("update treport_ctrl set status='"+zxgflag+"' where tabid="+tabid+" and unitcode='"+unitcode+"'");
        // }
        if (scopeid != null && scopeid.length() > 0 && !"0".equals(scopeid)) {
          dao.update("update tb" + this.tabid + " set scopeid=" + scopeid
                  + " where  username='" + this.userview.getUserName() + "'");
        }
        if (home != null && !"null".equals(home) && !"".equals(home)) {
          ReportOperationFormulaAnalyse reportOperationFormulaAnalyse = null;
          ArrayList formulaList = new ArrayList();
          RowSet rs1 =
                  dao.search("select * from tformula where tabid=" + this.tabid
                          + " order by  expid ");
          while (rs1.next()) {
            RecordVo vo = new RecordVo("tformula");
            vo.setInt("expid", rs1.getInt("expid"));
            vo.setInt("tabid", rs1.getInt("tabid"));
            vo.setString("cname", rs1.getString("cname"));
            vo.setString("lexpr", rs1.getString("lexpr"));
            vo.setString("rexpr", rs1.getString("rexpr"));
            vo.setInt("colrow", rs1.getInt("colrow"));
            formulaList.add(vo);
          }
          if ("1".equals(operateObject)) {
            reportOperationFormulaAnalyse =
                    new ReportOperationFormulaAnalyse(this.conn, this.tabid,
                            formulaList, Integer.parseInt(operateObject), userview
                            .getUserName());
          }
          else {
            reportOperationFormulaAnalyse =
                    new ReportOperationFormulaAnalyse(this.conn, this.tabid,
                            formulaList, Integer.parseInt(operateObject), userview
                            .getUnit_id());
          }
          reportOperationFormulaAnalyse.setUserView(this.userview);
          String info0 = reportOperationFormulaAnalyse.reportFormulaAnalyse();
        }

      }
      else if (a_info == 1) {
        info.append(ResourceFactory.getProperty("edit_report.table")
                + this.tabid
                + ":"
                + ResourceFactory
                .getProperty("auto_fill_report.batchFillData.info1") + "! \\n");
      }
      else if (a_info == 2) {
        info.append(ResourceFactory.getProperty("edit_report.table")
                + this.tabid
                + ":"
                + ResourceFactory
                .getProperty("auto_fill_report.batchFillData.info2") + "! \\n");
      }
      else if (a_info == 3) {
        info.append(ResourceFactory.getProperty("edit_report.table")
                + this.tabid
                + ":"
                + ResourceFactory
                .getProperty("auto_fill_report.batchFillData.info3") + "! \\n");
      }
      else if (a_info == 4) {
        info.append(ResourceFactory.getProperty("edit_report.table")
                + this.tabid
                + ":"
                + ResourceFactory
                .getProperty("auto_fill_report.batchFillData.info4") + "! \\n");
      }
      return a_info == 0;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    finally {
    }
  }

  /**
   * 获取全部没有用的参数，包括全局参数，表类
   *
   * @return
   */
  public ArrayList getAllParam() {
    ArrayList param = new ArrayList();
    ArrayList param0 = new ArrayList();// 全局参数
    ArrayList param1 = new ArrayList();// 表类参数
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      String sql =
              "select paramename,paramname from tparam  where paramname  not in (select Hz from tpage where Flag = '9') and paramscope = '0'";
      String sql1 =
              "select paramename,paramname from tparam  where paramname  not in (select Hz from tpage where Flag = '9') and paramscope = '1'";
      RowSet rs = dao.search(sql);
      RowSet rs1 = dao.search(sql1);
      while (rs.next()) {
        param0.add(rs.getString("paramename"));
      }
      while (rs1.next()) {
        param1.add(rs1.getString("paramename"));
      }
      param.add(param0);
      param.add(param1);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return param;
  }

  public String getAppdate() {
    return this.appdate;
  }

  /**
   * 产生扫描人员库临时表数据的sql语句
   *
   * @param userName
   * @param a_fieldNameList
   * @param a_variaFieldNameList
   * @param fieldSetMap
   * @param dbList
   * @param result
   * @param appdate
   * @param tableTermsCondition 表条件sql
   * @param isSuper_admin 是否是超级用户
   * @return
   */
  public String getATempSQL(String userName, HashSet a_fieldNameList,
                            HashSet a_variaFieldNameList, HashMap fieldSetMap, HashMap tableTermsMap) {
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    StringBuffer sql = new StringBuffer("");
    try {
      for (Iterator t = this.dbList.iterator(); t.hasNext();) {

        HashSet set = new HashSet(); // 涉及到的表名集合
        String pre = ((String) t.next()).toUpperCase();
        String tableTermCondition = (String) tableTermsMap.get(pre);
        StringBuffer sql_sub = new StringBuffer(" select ");
        // 产生select 前缀子句
        StringBuffer sql_sub_str = new StringBuffer("");
        for (Iterator t1 = a_fieldNameList.iterator(); t1.hasNext();) {
          String temp = (String) t1.next();
          String fieldSet = "";
          if ("A0100".equals(temp) || "B0110".equals(temp)
                  || "E0122".equals(temp) || "E01A1".equals(temp)
                  || "I9999".equals(temp) || "NBASE".equals(temp)) {
            fieldSet = "A01";
            // else if(temp.equalsIgnoreCase("create_date"))
            // fieldSet="hr_emp_hisdata";
          }
          else {
            fieldSet = (String) fieldSetMap.get(temp); // 表名
          }
          if ("NBASE".equals(temp)) {
            sql_sub_str.append(",'");
            sql_sub_str.append(pre + "'");
          }
          else if ("I9999".equals(temp)) {
            sql_sub_str.append(",1");
          }
          else {
            sql_sub_str.append(",");

            StringBuffer temp_sub = new StringBuffer("");
            if (fieldSet.charAt(0) == 'A') {
              temp_sub.append(pre + fieldSet);
            }
            else {
              temp_sub.append(fieldSet);
            }
            temp_sub.append(".");
            temp_sub.append(temp);
            // if(temp.equalsIgnoreCase("create_date")){
            // sql_sub_str.append(temp_sub.toString());
            // }else{
            FieldItem item = DataDictionary.getFieldItem(temp.toLowerCase());
            if (item != null && "N".equalsIgnoreCase(item.getItemtype())) {
              sql_sub_str.append(Sql_switcher.isnull(temp_sub.toString(), "0"));
            }
            else {
              sql_sub_str.append(temp_sub.toString());
              // }
            }
          }
          // 表名
          set.add(fieldSet);
        }

        sql_sub.append(sql_sub_str.substring(1));
        // 产生from where 子句
        sql_sub.append(" from " + pre + "A01");
        for (Iterator t1 = set.iterator(); t1.hasNext();) {
          String tempTable = (String) t1.next();
          if (!"A01".equals(tempTable)) {
            if (tempTable.charAt(2) == '1' && tempTable.charAt(1) == '0') {
              if (tempTable.charAt(0) == 'A') {
                sql_sub.append(" left join   " + pre + tempTable + " on " + pre
                        + "A01.A0100=" + pre + tempTable + ".A0100 ");
              }
              else if (tempTable.charAt(0) == 'B') {
                sql_sub.append(" left join   " + tempTable + " on " + pre
                        + "A01.B0110=" + tempTable + ".B0110 "/*
                 * +" or "+pre+"A01.E0122="
                 * +
                 * tempTable+".B0110 "
                 */); // 考虑到单位子集中的b0110可能存的是部门，所以这里加个or判断
                // zhaoxg add
                // 2014-1-15
              }
              else if (tempTable.charAt(0) == 'K') {
                sql_sub.append(" left join " + tempTable + " on " + pre
                        + "A01.E01A1=" + tempTable + ".E01A1 ");
              }

            }
            else {
              if (tempTable.charAt(0) == 'A') {
                sql_sub.append(" left join   ( select a.* from " + pre
                        + tempTable
                        + "  a where a.I9999=(select max( b.I9999 ) from " + pre
                        + tempTable + " b where a.a0100=b.a0100)) " + pre
                        + tempTable + " on " + pre + "A01.A0100=" + pre + tempTable
                        + ".A0100 ");
              }
              else if (tempTable.charAt(0) == 'B') {
                sql_sub.append(" left join  ( select a.* from " + tempTable
                        + "  a where a.I9999=(select max( b.I9999 ) from "
                        + tempTable + " b where a.B0110=b.B0110)) " + tempTable
                        + " on " + pre + "A01.B0110=" + tempTable + ".B0110 ");
              }
              else if (tempTable.charAt(0) == 'K') {
                sql_sub.append(" left join  ( select a.* from " + tempTable
                        + "  a where a.I9999=(select max( b.I9999 ) from "
                        + tempTable + " b where a.E01A1=b.E01A1))  " + tempTable
                        + " on " + pre + "A01.E01A1=" + tempTable + ".E01A1 ");
              }
            }
          }
        }
        // 从结果表里取数
        sql_sub.append(" where 1=1 ");
        /*
         * if(tableTermCondition!=null&&tableTermCondition.length()>0)
         * {
         *
         * sql_sub.append(" and "+pre+"A01.A0100 in ( "+tableTermCondition+" )");
         * }
         */

        if (this.result != null && "true".equals(this.result)) {
          // sql_sub.append(" and "+pre+"A01.A0100 in (select A0100 from "+userName+pre+"Result )");
          sql_sub.append(" and exists (select A0100 from " + userName + pre
                  + "Result where " + userName + pre + "Result.a0100=" + pre
                  + "A01.A0100 )");
        }

        /*
         * if (!this.userview.isSuper_admin()) {
         * sql_sub.append(" and "+pre+"A01.A0100 in ( select A0100 "+
         * this.userview.getPrivSQLExpression(pre, false)+" ) ");
         * //System.out.println(" and "+pre+"A01.A0100 in ( select A0100 "+
         * this.userview.getPrivSQLExpression(pre, false)+" ) ");
         * }
         */

        sql.append(" union " + sql_sub.toString());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    // System.out.println(sql.substring(6));

    return sql.substring(6);
  }

  /**
   * 产生扫描单位库临时表数据的sql语句
   *
   * @param userName
   * @param a_fieldNameList
   * @param a_variaFieldNameList
   * @param fieldSetMap
   * @param dbList
   * @param result
   * @param appdate
   * @param tableTermsCondition 表条件sql
   * @return
   */
  public String getBTempSQL(String userName, HashSet a_fieldNameList,
                            HashSet a_variaFieldNameList, HashMap fieldSetMap, HashMap tableTermsMap) {
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    StringBuffer sql_sub = new StringBuffer(" select ");
    try {

      HashSet set = new HashSet(); // 涉及到的表名集合
      // 产生select 前缀子句
      StringBuffer sql_sub_str = new StringBuffer("");
      for (Iterator t1 = a_fieldNameList.iterator(); t1.hasNext();) {
        String temp = (String) t1.next();
        String fieldSet = "";
        if ("B0110".equals(temp) || "I9999".equals(temp)) {
          fieldSet = "B01";
        }
        else {
          fieldSet = (String) fieldSetMap.get(temp); // 表名
        }

        if ("I9999".equals(temp)) {
          sql_sub_str.append(",1");
        }
        else {
          sql_sub_str.append("," + fieldSet);
          sql_sub_str.append(".");
          sql_sub_str.append(temp);
        }
        // 表名
        set.add(fieldSet);
      }
      sql_sub.append(sql_sub_str.substring(1));
      // 产生from where 子句
      sql_sub.append(" from " + "B01");
      for (Iterator t1 = set.iterator(); t1.hasNext();) {
        String tempTable = (String) t1.next();
        if (!"B01".equals(tempTable)) {
          sql_sub.append(" left join  ( select a.* from " + tempTable
                  + "  a where a.I9999=(select max( b.I9999 ) from " + tempTable
                  + " b where a.B0110=b.B0110)) " + tempTable + " on B01.B0110="
                  + tempTable + ".B0110 ");
        }
      }
      sql_sub.append(" where 1=1 ");
      if (this.result != null && "true".equals(this.result)) {
        sql_sub.append(" and B01.B0110 in  ( select B0110 from " + userName
                + "BResult ) ");
      }
      // if(tableTermsMap.get("B")!=null)
      // //sql_sub.append(" and B01.B0110 in  ( "+(String)tableTermsMap.get("B")+" ) ");
      if (!this.userview.isSuper_admin()) {
        sql_sub.append(" and B01.B0110 LIKE '"
                + this.userview.getManagePrivCodeValue() + "%'");
      }
      // StringBuffer ext_sql = new StringBuffer();
      // Calendar d=Calendar.getInstance();
      // int yy=d.get(Calendar.YEAR);
      // int mm=d.get(Calendar.MONTH)+1;
      // int dd=d.get(Calendar.DATE);
      // String date = getBusinessDate();
      // if(date!=null&&date.trim().length()>0)
      // {
      // d.setTime(Date.valueOf(date));
      // yy=d.get(Calendar.YEAR);
      // mm=d.get(Calendar.MONTH)+1;
      // dd=d.get(Calendar.DATE);
      // }
      // ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
      // ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
      // ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
      // ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
      // ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
      // ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
      // sql_sub.append(" and B01.B0110 in ( select codeitemid from organization  where 1=1 "+ext_sql+")");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql_sub.toString();
  }

  // 获得业务日期
  public String getBusinessDate() {

    String appdate = ""; // 截止日期
    RowSet rs = null;
    ContentDAO dao = new ContentDAO(this.conn);
    String xml = "";
    try {
      // 常量表中查找rp_param常量
      rs =
              dao
                      .search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
      if (rs.next()) {
        xml = Sql_switcher.readMemo(rs, "STR_VALUE");
        // xml文件分析类
        AnalyseParams aps = new AnalyseParams(xml);
        if (aps.checkUserid(this.userName)) {// DB中存在当前用户的扫描库配置信息
          // 用户配置信息封装在MAP内
          HashMap hm = aps.getAttributeValues(this.userName);
          appdate = (String) hm.get("appdate"); // 起始日期
        }
        else {
          if (ConstantParamter.getAppdate(this.userName) != null) {
            String value =
                    ConstantParamter.getAppdate(this.userName).replaceAll("\\.",
                            "-");
            appdate = value;
          }
        }

      }
      else {
        if (ConstantParamter.getAppdate(this.userName) != null) {
          String value =
                  ConstantParamter.getAppdate(this.userName).replaceAll("\\.", "-");
        }
      }
      rs.close();
    }

    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return appdate;

  }

  public HashSet getColCountExpreSet() {
    return this.colCountExpreSet;
  }

  public ArrayList getColInfoBGrid() {
    return this.colInfoBGrid;
  }

  public ArrayList getColInfoList() {
    return this.colInfoList;
  }

  public HashMap getColMap() {
    return this.colMap;
  }

  public String getColSerialNo() {
    return this.colSerialNo;
  }

  // public void copyTempTable(String tableName)
  // {
  // try
  // {
  // ContentDAO dao=new ContentDAO(this.conn);
  // String sql="";
  // if(Sql_switcher.searchDbServer()==2)
  // sql="create table "+(tableName+"_c")+" as select * from "+tableName;
  // else
  // sql="select *  into "+(tableName+"_c")+"  from "+tableName;
  // DbWizard dbWizard=new DbWizard(this.conn);
  // if(dbWizard.isExistTable(tableName+"_c", false))
  // dbWizard.dropTable(tableName+"_c");
  // dao.update(sql);
  //
  // DBMetaModel dbmodel=new DBMetaModel(this.conn);
  // dbmodel.reloadTableModel(tableName+"_c");
  // }
  // catch(Exception e)
  // {
  // e.printStackTrace();
  // }
  // }

  public RecordVo getDataArea() {
    return this.dataArea;
  }

  public ArrayList getDbList() {
    return this.dbList;
  }

  public int[][] getDigitalResults() {
    return this.digitalResults;
  }

  /**
   * 取得调用算法分析器时，限制权限范围的sql语句，给exist用的。
   *
   * @param info_flag
   * @param dbpre
   * @param tableName
   * @return
   */
  public String getExistSql(String info_flag, String dbpre, String tableName) {
    StringBuffer sql = new StringBuffer("");
    if ("3".equals(info_flag)) {
      sql.append(" select null from ");
      sql.append(tableName + " where ");
      sql.append("B01.B0110=" + tableName + ".B0110");
    }
    else if ("1".equals(info_flag)) {
      sql.append(" select null from ");
      sql.append(tableName + " where ");
      sql.append("K01.E01A1=" + tableName + ".E01A1");
    }
    else {
      sql.append(" select null from ");
      sql.append(tableName + " where ");
      sql.append(dbpre + "A01.a0100=" + tableName + ".a0100 and UPPER("
              + tableName + ".NBASE)='" + dbpre.toUpperCase() + "'  ");
    }
    return sql.toString();
  }

  public HashMap getFactorListMap() {
    return this.factorListMap;
  }

  public ArrayList getGridList() {
    return this.gridList;
  }

  public int[] getItemGridArea() {
    return this.itemGridArea;
  }

  public ArrayList getItemGridList() {
    return this.itemGridList;
  }

  /**
   * 产生扫描职位库临时表数据的sql语句
   *
   * @param userName
   * @param a_fieldNameList
   * @param a_variaFieldNameList
   * @param fieldSetMap
   * @param dbList
   * @param result
   * @param appdate
   * @param tableTermsCondition 表条件sql
   * @return
   */
  public String getKTempSQL(String userName, HashSet a_fieldNameList,
                            HashSet a_variaFieldNameList, HashMap fieldSetMap, HashMap tableTermsMap) {
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    StringBuffer sql_sub = new StringBuffer(" select ");
    try {
      HashSet set = new HashSet(); // 涉及到的表名集合
      // 产生select 前缀子句
      StringBuffer sql_sub_str = new StringBuffer("");
      for (Iterator t1 = a_fieldNameList.iterator(); t1.hasNext();) {
        String temp = (String) t1.next();
        String fieldSet = "";
        if ("E0122".equals(temp) || "E01A1".equals(temp)
                || "I9999".equals(temp)) {
          fieldSet = "K01";
        }
        else {
          fieldSet = (String) fieldSetMap.get(temp); // 表名
        }

        if ("I9999".equals(temp)) {
          sql_sub_str.append(",1");
        }
        else {
          sql_sub_str.append("," + fieldSet);
          sql_sub_str.append(".");
          sql_sub_str.append(temp);
        }
        // 表名
        set.add(fieldSet);
      }
      sql_sub.append(sql_sub_str.substring(1));
      // 产生from where 子句
      sql_sub.append(" from " + "K01");
      for (Iterator t1 = set.iterator(); t1.hasNext();) {
        String tempTable = (String) t1.next();
        if (!"K01".equals(tempTable)) {
          sql_sub.append(" left join  ( select a.* from " + tempTable
                  + "  a where a.I9999=(select max( b.I9999 ) from " + tempTable
                  + " b where a.E01A1=b.E01A1)) " + tempTable + " on K01.E01A1="
                  + tempTable + ".E01A1 ");
        }
      }
      sql_sub.append(" where 1=1 ");
      if (this.result != null && "true".equals(this.result)) {
        sql_sub.append(" and K01.E01A1 in  ( select E01A1 from " + userName
                + "KResult ) ");
      }
      if (tableTermsMap.get("K") != null) {
        sql_sub.append(" and K01.E01A1 in  ( "
                + (String) tableTermsMap.get("K") + " ) ");
      }
      if (!this.userview.isSuper_admin()) {
        sql_sub.append(" and K01.E01A1 LIKE '"
                + this.userview.getManagePrivCodeValue() + "%'");
      }
      StringBuffer ext_sql = new StringBuffer();
      Calendar d = Calendar.getInstance();
      int yy = d.get(Calendar.YEAR);
      int mm = d.get(Calendar.MONTH) + 1;
      int dd = d.get(Calendar.DATE);
      String date = this.getBusinessDate();
      if (date != null && date.trim().length() > 0) {
        d.setTime(Date.valueOf(date));
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
      sql_sub
              .append(" and K01.E01A1 in ( select codeitemid from organization  where 1=1 "
                      + ext_sql + ")");

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql_sub.toString();
  }

  /**
   * 获得报表的最大行或列号
   *
   * @return
   */
  public int getMaxRowNumber(HashMap map) {
    int kk = 1;
    while (true) {
      String tt = (String) map.get(String.valueOf(kk));
      if (tt != null) {
      }
      else {
        break;
      }
      kk++;
    }
    return kk - 1;
  }

  public MidVariableBo getMidVariableBo() {
    return this.midVariableBo;
  }

  public ArrayList getMidVariableList() {
    return this.midVariableList;
  }

  public int getMinTop_px() {
    return this.minTop_px;
  }

  public String getOperate(String a_expre, String a_index) {

    String operate = "";
    boolean a_flag = true;
    while (a_flag && a_expre.length() > 0) {
      if (a_expre.indexOf(a_index) + a_index.length() < a_expre.length()) {
        operate =
                a_expre.substring(a_expre.indexOf(a_index) + a_index.length(),
                        a_expre.indexOf(a_index) + a_index.length() + 1);
      }
      else {
        operate = "";
      }
      if ("(".equals(operate) || ")".equals(operate) || "+".equals(operate)
              || "-".equals(operate) || "*".equals(operate) || "/".equals(operate)
              || "".equals(operate)) {
        a_flag = false;
      }
      else {
        a_expre =
                a_expre.substring(a_expre.indexOf(a_index) + a_index.length());
      }
    }
    return operate;
  }

  public String getOwnerDate(String tabid) {
    String date = "";
    ContentDAO dao = new ContentDAO(this.conn);
    String sortid = "";
    RowSet rowSet = null;
    try {
      rowSet = dao.search("select tsortid from tname where tabid=" + tabid);
      if (rowSet.next()) {
        sortid = rowSet.getString(1);
      }
      String xml = "";
      // 常量表中查找rp_param常量
      rowSet =
              dao
                      .search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
      if (rowSet.next()) {
        xml = Sql_switcher.readMemo(rowSet, "STR_VALUE");
        // xml文件分析类
        AnalyseParams aps = new AnalyseParams(xml);
        if (aps.checkBelongdateSortid(sortid)) {// DB中存在当前用户的扫描库配置信息
          // 用户配置信息封装在MAP内
          HashMap hm = aps.getAttributeSortidValues(sortid);
          date = (String) hm.get(sortid); // 起始日期
          if (date == null || date.length() < 9) {
            if (aps.checkUserid(this.userName)) {// DB中存在当前用户的扫描库配置信息
              // 用户配置信息封装在MAP内
              hm = aps.getAttributeValues(this.userName);
              date = (String) hm.get("appdate"); // 起始日期
            }
            else {
              if (ConstantParamter.getAppdate(this.userName) != null) {
                String value =
                        ConstantParamter.getAppdate(this.userName).replaceAll(
                                "\\.", "-");
                date = value;
              }
            }
          }

        }
        else if (aps.checkUserid(this.userName)) {// DB中存在当前用户的扫描库配置信息
          // 用户配置信息封装在MAP内
          HashMap hm = aps.getAttributeValues(this.userName);
          date = (String) hm.get("appdate"); // 起始日期
        }
        else {
          if (ConstantParamter.getAppdate(this.userName) != null) {
            String value =
                    ConstantParamter.getAppdate(this.userName).replaceAll("\\.",
                            "-");
            date = value;
          }
        }
        if (date == null || date.length() < 9) {

          if (ConstantParamter.getAppdate(this.userName) != null) {
            String value =
                    ConstantParamter.getAppdate(this.userName).replaceAll("\\.",
                            "-");
            date = value;
          }

        }
      }
      else {
        if (ConstantParamter.getAppdate(this.userName) != null) {
          String value =
                  ConstantParamter.getAppdate(this.userName).replaceAll("\\.", "-");
          date = value;
        }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (rowSet != null) {
        try {
          rowSet.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return date;
  }

  public ArrayList getPageList() {
    return this.pageList;
  }

  public ArrayList getParamenameList() {
    return this.paramenameList;
  }

  public HashMap getParamMap() {
    return this.paramMap;
  }

  /**
   * 得到每个单元格值的 sql语句
   *
   * @param rowTermList 横表栏条件
   * @param colTermList 纵表栏条件
   * @param i 行号
   * @param j 列号
   * @param userName 用户名
   * @param tableTermsMap 表条件
   * @return
   */
  public String getPerGridSql(ArrayList rowTermList, ArrayList colTermList,
                              int i, int j, String userName, HashMap tableTermsMap) {
    StringBuffer sql = new StringBuffer(",(  ");
    // 返回查询条件集合 及 判断扫描哪个库中的数据
    ArrayList list = this.scanStore(rowTermList, colTermList, i, j);
    ArrayList infoList = (ArrayList) list.get(0);
    String scanMode = (String) list.get(1);
    // wangcq 2014-12-20 begin 行扫描部门时，统计条件有B0110(单位名称)的，改为按E0122(部门)处理；
    // 行扫描人员、列扫描部门，也是类似处理。
    for (Iterator ct = colTermList.iterator(); ct.hasNext();) { // colTermList为行条件，之前弄反了
      String[] coltemp = (String[]) ct.next();
      if (StringUtils.equals(coltemp[0], "4")) { // 行扫描部门时
        for (int s = 0; s < infoList.size(); s++) {
          String[] infotemp = new String[((String[]) infoList.get(s)).length];
          System.arraycopy((String[]) infoList.get(s), 0, infotemp, 0,
                  ((String[]) infoList.get(s)).length);
          if (infotemp[3].toUpperCase().contains("B0110")) {
            infotemp[3] =
                    infotemp[3].toUpperCase().replaceAll("B0110", "E0122");
          }
          infoList.set(s, infotemp);
        }
      }
      for (Iterator rt = rowTermList.iterator(); rt.hasNext();) {
        String[] rowtemp = (String[]) rt.next();
        if (StringUtils.equals(coltemp[0], "1")
                && StringUtils.equals(rowtemp[0], "4")) { // 行扫描人员、列扫描部门
          for (int s = 0; s < infoList.size(); s++) {
            String[] infotemp = new String[((String[]) infoList.get(s)).length];
            System.arraycopy((String[]) infoList.get(s), 0, infotemp, 0,
                    ((String[]) infoList.get(s)).length);
            if (infotemp[3].toUpperCase().contains("B0110")) {
              infotemp[3] =
                      infotemp[3].toUpperCase().replaceAll("B0110", "E0122");
            }
            infoList.set(s, infotemp);
          }
        }
      }
    }
    // wangcq 2014-12-20 end
    // wangcq 2014-11-29 begin 扫描单位、部门时，由于对应指标在单位库中e0122不存在，故改为相应指标b0110取的相应部门值
    if ("2".equals(scanMode) || "3".equals(scanMode) || "4".equals(scanMode)) {
      for (int s = 0; s < infoList.size(); s++) {
        String[] temp = new String[((String[]) infoList.get(s)).length];
        System.arraycopy((String[]) infoList.get(s), 0, temp, 0,
                ((String[]) infoList.get(s)).length);
        if (temp[3].toUpperCase().contains("E0122")) {
          temp[3] = temp[3].toUpperCase().replaceAll("E0122", "B0110");
        }
        infoList.set(s, temp);
      }
    }
    // wangcq 2014-11-29 end
    String[] a_rowCountExpre = (String[]) list.get(2);
    String[] a_colCountExpre = (String[]) list.get(3);
    String[] rowbottomtemp = (String[]) list.get(4);
    String[] colbottomtemp = (String[]) list.get(5);
    if (a_rowCountExpre != null) {
      this.colCountExpreSet.add(j + "#"
              + this.tgridBo.getCexpr2Context(1, a_rowCountExpre[5]));
    }
    if (a_colCountExpre != null) {
      this.rowCountExpreSet.add(i + "#"
              + this.tgridBo.getCexpr2Context(1, a_colCountExpre[5]));
    }
    try {
      // if(tableTermsMap.size()>0)
      // scanMode="1";
      this.tgridBo.setTableTermsMap(tableTermsMap);
      this.tgridBo.setDecimal(this.digitalResults[i][j]);
      this.tgridBo.setFactorListMap(this.factorListMap);
      sql.append(this.tgridBo.getTgridSqls(infoList, userName, j, scanMode,
              this.appdate, this.midVariableList, rowbottomtemp, colbottomtemp));
      this.digitalResults[i][j] = this.tgridBo.getDecimal();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    sql.append("    ) a" + j);

    return sql.toString();
  }

  /**
   * 得到单元格值的反查sql语句
   *
   * @param rowTermList 横表栏条件
   * @param colTermList 纵表栏条件
   * @param i 行号
   * @param j 列号
   * @param userName 用户名
   * @param tableTermsMap 表条件
   * @return
   */
  public String getPerGridSql2(ArrayList rowTermList, ArrayList colTermList,
                               int i, int j, String userName, HashMap tableTermsMap, ArrayList infoList,
                               String scanMode, ArrayList fieldItemSet) {
    StringBuffer sql = new StringBuffer("");
    try {
      // if(tableTermsMap.size()>0)
      // scanMode="1";
      ArrayList list = this.scanStore(rowTermList, colTermList, i, j);
      // ArrayList infoList=(ArrayList)list.get(0);
      // String scanMode=(String)list.get(1);
      String[] a_rowCountExpre = (String[]) list.get(2);
      String[] a_colCountExpre = (String[]) list.get(3);
      String[] rowbottomtemp = (String[]) list.get(4);
      String[] colbottomtemp = (String[]) list.get(5);
      // sql.append(tgridBo.getTgridSqls2(infoList,userName,j,scanMode,this.appdate,this.midVariableList,fieldItemSet,this.dbList));
      this.tgridBo.setFactorListMap(this.factorListMap);
      sql.append(this.tgridBo.getTgridSqls2(infoList, userName, j, scanMode,
              this.appdate, this.midVariableList, fieldItemSet, this.dbList,
              rowbottomtemp, colbottomtemp));

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql.toString();
  }

  /**
   * 生成报表编辑页面(HTML)
   *
   * @param status =-1，未填 =0,正在编辑 =1,已上报 =2,打回 =3,封存（基层单位的数据不让修改）
   * @param operateObject 表对象 1：编辑没上报表 2：编辑上报后的表
   * @return String
   */
  public String getReportHtml(String status, String userName,
                              String operateObject, String unitcode, String selfUnitCode) {
    StringBuffer htmlText = new StringBuffer("");
    RecordVo tnameVo = this.getTnameVoById(this.tabid);
    TnameHtmlBo tnameHtmlBo = new TnameHtmlBo(this.conn);
    tnameHtmlBo.setShowPaper(this.showReportHtmlPaper);
    // ReportNewHtmlBo bo = new
    // ReportNewHtmlBo(this.conn,this.tabid,operateObject,this.userview);
    if ("1".equals(operateObject)) {
      // htmlText.append(bo.createHtml());
      // htmlText.append(tnameHtmlBo.creatHtmlView(this.tabid,userName,this.gridList,this.pageList,this.rowInfoBGrid,this.colInfoBGrid,this.itemGridArea,status,this.dataArea,this.rowSerialNo,this.colSerialNo,this.tnameVo,this.paramMap,this.rowMap,this.colMap,30,operateObject,unitcode,selfUnitCode));
      int top = this.showReportHtmlToolbar ? 46 : 0;
      htmlText.append(tnameHtmlBo.creatHtmlView2(this, userName, status, top,
              operateObject, unitcode, selfUnitCode));
    }
    else {
      ArrayList pageAndParamList =
              this.tpageBo.getPageListAndTparam2(this.tabid, tnameVo
                      .getInt("tsortid"), unitcode);
      this.pageList = (ArrayList) pageAndParamList.get(0);
      this.paramMap = (HashMap) pageAndParamList.get(1);
      this.paramenameList = (ArrayList) pageAndParamList.get(2);
      // htmlText.append(tnameHtmlBo.creatHtmlView(this.tabid,userName,this.gridList,this.pageList,this.rowInfoBGrid,this.colInfoBGrid,this.itemGridArea,status,this.dataArea,this.rowSerialNo,this.colSerialNo,this.tnameVo,this.paramMap,this.rowMap,this.colMap,40,operateObject,unitcode,selfUnitCode));
      int top = this.showReportHtmlToolbar ? 50 : 0;
      htmlText.append(tnameHtmlBo.creatHtmlView2(this, userName, status, top,
              operateObject, unitcode, selfUnitCode));
    }
    return htmlText.toString();
  }

  /**
   * 取得当前表的状态
   *
   * @return
   */
  public String getReportStatus(String userName, String tabid, String unitcode,
                                int opt) {
    String status = "-1";
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      RowSet rowSet = null;
      if (opt == 1) {
        rowSet =
                dao
                        .search("select treport_ctrl.status from treport_ctrl,operuser  where treport_ctrl.unitcode=operuser.unitcode and tabid="
                                + tabid + " and operuser.userName='" + userName + "'");
      }
      else {
        rowSet =
                dao.search("select status from treport_ctrl where unitcode='"
                        + unitcode + "' and tabid=" + tabid);
      }
      if (rowSet.next()) {
        status = rowSet.getString("status");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return status;
  }

  public String getResult() {
    return this.result;
  }

  public double[][] getResults() {
    return this.results;
  }

  /**
   * 取得反查所需的所有条件
   *
   * @param tabid 表号
   * @param userid 用户id
   * @param conditionSql 权限控制语句
   * @param isSuper_admin 是否是超级用户
   * @param tableTermsMap 表条件
   * @return （sql,扫描库,指标集，代码值 map,指标类型 map, 指标对应的代码集 map）
   */
  public ArrayList getReverseValue(String userid, String username,
                                   String conditionSql, HashMap tableTermsMap, int i, int j,
                                   UserView userview) {
    ArrayList alist = new ArrayList();
    String sql = "";
    try {
      this.userview = userview;
      TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
      ArrayList colTermList = (ArrayList) this.colInfoList.get(i);
      ArrayList rowTermList = (ArrayList) this.rowInfoList.get(j);
      ArrayList aa_list = this.scanStore(rowTermList, colTermList, i, j);
      ArrayList infoList = (ArrayList) aa_list.get(0);
      // 扫描范围 1：人员库 2：单位 3：单位、部门 4：部门 5 职位库
      String scanMode = (String) aa_list.get(1);
      // wangcq 2014-12-20 begin 行扫描部门时，统计条件有B0110(单位名称)的，改为按E0122(部门)处理；
      // 行扫描人员、列扫描部门，也是类似处理。
      for (Iterator ct = colTermList.iterator(); ct.hasNext();) { // colTermList为行条件，之前弄反了
        String[] coltemp = (String[]) ct.next();
        if (StringUtils.equals(coltemp[0], "4")) { // 行扫描部门时
          for (int s = 0; s < infoList.size(); s++) {
            String[] infotemp = new String[((String[]) infoList.get(s)).length];
            System.arraycopy((String[]) infoList.get(s), 0, infotemp, 0,
                    ((String[]) infoList.get(s)).length);
            if (infotemp[3].toUpperCase().contains("B0110")) {
              infotemp[3] =
                      infotemp[3].toUpperCase().replaceAll("B0110", "E0122");
            }
            infoList.set(s, infotemp);
          }
        }
        for (Iterator rt = rowTermList.iterator(); rt.hasNext();) {
          String[] rowtemp = (String[]) rt.next();
          if (StringUtils.equals(coltemp[0], "1")
                  && StringUtils.equals(rowtemp[0], "4")) { // 行扫描人员、列扫描部门
            for (int s = 0; s < infoList.size(); s++) {
              String[] infotemp =
                      new String[((String[]) infoList.get(s)).length];
              System.arraycopy((String[]) infoList.get(s), 0, infotemp, 0,
                      ((String[]) infoList.get(s)).length);
              if (infotemp[3].toUpperCase().contains("B0110")) {
                infotemp[3] =
                        infotemp[3].toUpperCase().replaceAll("B0110", "E0122");
              }
              infoList.set(s, infotemp);
            }
          }
        }
      }
      // wangcq 2014-12-20 end
      // wangcq 2014-11-29 begin
      // 扫描单位、部门时，由于对应指标在单位库中e0122不存在，故改为相应指标b0110取的相应部门值
      if ("2".equals(scanMode) || "3".equals(scanMode) || "4".equals(scanMode)) {
        for (int s = 0; s < infoList.size(); s++) {
          String[] temp = new String[((String[]) infoList.get(s)).length];
          System.arraycopy((String[]) infoList.get(s), 0, temp, 0,
                  ((String[]) infoList.get(s)).length);
          if (temp[3].toUpperCase().contains("E0122")) {
            temp[3] = temp[3].toUpperCase().replaceAll("E0122", "B0110");
          }
          infoList.set(s, temp);
        }
      }
      // wangcq 2014-11-29 end
      // 取得查询条件下涉及到的指标信息
      ArrayList fieldItemSet = tnameExtendBo.getFieldInfo(infoList);
      if (fieldItemSet.size() == 0) {
        return new ArrayList();
      }
      HashMap codeValueMap = tnameExtendBo.getFieldCodeMap(fieldItemSet);
      ArrayList typeSetList = tnameExtendBo.getFieldTypeMap(fieldItemSet);
      HashMap typeMap = (HashMap) typeSetList.get(0);
      HashMap setMap = (HashMap) typeSetList.get(1);
      HashMap nameMap = (HashMap) typeSetList.get(2);
      String[] temp0 = (String[]) colTermList.get(0);
      String[] temp1 = (String[]) rowTermList.get(0);
      if ("4".equals(temp0[1]) || "4".equals(temp1[1])) { // 编号过滤，编号行列不让反查
        // zhaoxg 2013-7-16
        // add
        alist.add("b");
        return alist;
      }

      // 得到报表条件因子里包含的标识
      this.tgridBo.setUserview(userview);
      ArrayList factorList =
              this.tgridBo.getFactor2(this.gridList, rowTermList, colTermList,
                      this.midVariableList, tableTermsMap, scanMode);
      // 创建临时表
      // wangcq 2014-11-26 begin 当反查单元格无条件时，支持反查
      boolean factorIsNull = true;
      for (int s = 0; s < factorList.size() - 1; s++) {
        HashSet aSet = (HashSet) factorList.get(s++);
        if (aSet.size() > 0) {
          factorIsNull = false;
        }
      }
      ArrayList list = new ArrayList();
      if (factorIsNull) {
        list =
                this.tgridBo.creatTempTable(factorList, username,
                        this.midVariableList, scanMode);
      }
      else {
        list =
                this.tgridBo.creatTempTable(factorList, username,
                        this.midVariableList);
      }
      // wangcq 2014-11-26 begin end
      int info = Integer.parseInt((String) list.get(6)); // 是否有指标没有构库 1:没构库
      if (info == 0) {
        // 往临时表里存入数据
        if (!this.insertTempTable(username, userid, list, tableTermsMap)) {
          info = 2;
        }

        filterResult(tableTermsMap);
        // 批量取数
        sql =
                this.reverseReport(username, tableTermsMap, i, j, infoList,
                        scanMode, fieldItemSet);

      }
      alist.add(sql);
      alist.add(scanMode);
      alist.add(fieldItemSet);
      alist.add(codeValueMap);
      alist.add(typeMap);
      alist.add(setMap);
      alist.add(nameMap);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return alist;
  }

  /**
   * 过滤数据
   * @param tableTermsMap
   * @throws SQLException
   * @throws GeneralException
   */
  private void filterResult(HashMap tableTermsMap) throws SQLException, GeneralException {
    /*
     * wangcq 2014-12-11 begin
     * 判断是否创建表"t#"+userName+"_tt_AStaticc_d",此表为查询子集记录数又有项目格条件时，
     * 由于"t#"+userName+"_tt_AStaticc表数据只取了项目格条件下的当前记录，取子集记录存在问题
     */
    boolean createTable_d = false;
    if (tableTermsMap.size() > 0) { // 无项目条件时不创建
      TgridBo tgridBo = new TgridBo(this.conn);
      for (int s = 0; s < this.gridList.size(); s++) { // 无子集记录结果选取时，不创建
        RecordVo vo = (RecordVo) this.gridList.get(s);
        String cexpr2 = vo.getString("cexpr2");
        if (!"主集".equals(tgridBo.getCexpr2Context(4, cexpr2))) {
          createTable_d = true;
          break;
        } else if ("0".equals(tgridBo.getCexpr2Context(6, cexpr2))) {// liuy
          // 2015-5-13
          // 任意一个格不按管理范围就建表
          createTable_d = true;
          break;
        }
      }
    }
    // wangcq 2014-12-11 end

    // //删除不符合条件的数据
    ContentDAO dao = new ContentDAO(this.conn);
    String tableName = "t#" + this.userName + "_tjb_A";
    String tableName2 = tableName + "_c";
    DbWizard dbWizard = new DbWizard(this.conn);
    if (dbWizard.isExistTable(tableName, false)) {
      if (createTable_d) {
        this.copyTempTable(tableName, tableName + "_cd");//控制权限范围的表
      }
      for (int e = 0; e < this.dbList.size(); e++) {
        String pre = ((String) this.dbList.get(e)).toUpperCase();
        String tableTermCondition = (String) tableTermsMap.get(pre);
        // 从结果表里取数
        if (tableTermCondition != null && tableTermCondition.length() > 0) {
          dao.delete("delete from " + tableName + " where not exists ("
                  + tableTermCondition + "  where aaa.a0100=" + tableName
                  + ".a0100  ) and nbase='" + pre + "'", new ArrayList());
        }
        if (this.result != null && "true".equals(this.result)) {
          dao.delete("delete from  " + tableName
                  + "  where  not exists  (select A0100 from " + this.userName
                  + pre + "Result where " + this.userName + pre
                  + "Result.a0100=" + tableName + ".a0100   ) and nbase='"
                  + pre + "'", new ArrayList());
        }

      }
      /*tableName和tableName2处理方式一样，因此先处理完tableName数据再复制应该会快一些*/
      this.copyTempTable(tableName, tableName2);
      for (int e = 0; e < this.dbList.size(); e++) {
        String pre = ((String) this.dbList.get(e)).toUpperCase();
        if (!this.userview.isSuper_admin()) {
          dao.delete("delete from  " + tableName + "  where nbase='" + pre
                          + "' and   not exists ( select " + pre + "A01.A0100 "
                          + this.userview.getPrivSQLExpression(pre, false) + " and "
                          + pre + "A01.a0100=" + tableName + ".a0100 ) ",
                  new ArrayList());
        }
      }
    }
    String BtableName = "t#" + this.userName + "_tjb_B";
    if (dbWizard.isExistTable(BtableName, false)) {
      String tableTermCondition = (String) tableTermsMap.get("B");
      // 从结果表里取数
      if (tableTermCondition != null && tableTermCondition.length() > 0) {
        dao.delete("delete from " + BtableName + " where not exists ("
                + tableTermCondition + "  where aaa.b0110=" + BtableName
                + ".b0110  )", new ArrayList());
      }
    }
  }

  public HashSet getRowCountExpreSet() {
    return this.rowCountExpreSet;
  }

  public ArrayList getRowInfoBGrid() {
    return this.rowInfoBGrid;
  }

  // 产生上报参数表（全局，表类，表）

  public ArrayList getRowInfoList() {
    return this.rowInfoList;
  }

  public HashMap getRowMap() {
    return this.rowMap;
  }

  public String getRowSerialNo() {
    return this.rowSerialNo;
  }

  /**
   * 得到扫描库集合
   *
   * @param databaselist
   * @return
   */
  public ArrayList getScanRes(String databaselist) {

    ArrayList dbList = new ArrayList(); // 信息库集合
    String[] pre = databaselist.split(",");
    for (int i = 0; i < pre.length; i++) {
      dbList.add(pre[i]);
    }
    return dbList;
  }

  public String getScopeid() {
    return this.scopeid;
  }

  public HashMap getScopeMap() {
    HashMap map = new HashMap();
    String sql = "select tabid,xmlstyle from tname";
    RowSet rowSet = null;
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      rowSet = dao.search(sql);
      while (rowSet.next()) {
        String scope_cond = Sql_switcher.readMemo(rowSet, "xmlstyle");
        if (scope_cond != null && scope_cond.indexOf("<use_scope_cond>") != -1
                && scope_cond.indexOf("</use_scope_cond>") != -1) {
          String use_scope_cond =
                  scope_cond.substring(scope_cond.indexOf("<use_scope_cond>") + 16,
                          scope_cond.indexOf("</use_scope_cond>"));
          if ("1".equals(use_scope_cond)) {
            map.put("" + rowSet.getInt("tabid"), "1");
          }

        }
      }
    }
    catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      if (rowSet != null) {
        try {
          rowSet.close();
        }
        catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    }
    return map;
  }

  /**
   * 取得 驳回邮件 信息
   *
   * @param unitcode
   * @param sendName
   * @return
   */
  public ArrayList getSendMailInfo(String unitcode, String sendFromUserName,
                                   String desc, String tabid) {
    ArrayList list = new ArrayList();
    LazyDynaBean abean = null;
    try {
      ContentDAO dao = new ContentDAO(this.conn);
      String sendFromUnitName = "";
      String reportName = "";
      RowSet recset = dao.search("select name from tname where tabid=" + tabid);
      if (recset.next()) {
        reportName = recset.getString(1);
      }
      String subject =
              ResourceFactory.getProperty("label.module.bbgl") + "：" + reportName
                      + " " + ResourceFactory.getProperty("report_collect.rejected")
                      + "！";
      recset =
              dao
                      .search("select tt_organization.unitname,operuser.email,operuser.username  from operuser,tt_organization  where operuser.unitcode=tt_organization.unitcode and operuser.userName='"
                              + sendFromUserName + "'");
      if (recset.next()) {
        sendFromUnitName = recset.getString("unitname");
      }
      recset =
              dao
                      .search("select tt_organization.unitname,operuser.email,operuser.username  from operuser,tt_organization  where operuser.unitcode=tt_organization.unitcode and operuser.unitcode='"
                              + unitcode + "'");
      while (recset.next()) {
        abean = new LazyDynaBean();
        String unitname = recset.getString("unitname");
        String email =
                recset.getString("email") != null ? recset.getString("email") : "";
        StringBuffer context =
                new StringBuffer(unitname + ":\r\n       "
                        + ResourceFactory.getProperty("report_collect.hello") + "！\r\n");
        context.append("      " + reportName
                + ResourceFactory.getProperty("report_collect.rejectCause")
                + ":\r\n");
        context.append("      " + desc + "\r\n");
        context.append("                                                   "
                + ResourceFactory.getProperty("report_collect.rejectUnit") + "："
                + sendFromUnitName);

        abean.set("sendTo", email.trim());
        abean.set("subject", subject);
        abean.set("context", context.toString());
        list.add(abean);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  /**
   * 根据 2维结果表里的坐标 得到相关的甲栏编号
   *
   * @param opt 1:横 2;列
   * @return
   */
  public HashMap getSerialfromIndex(int opt) {
    HashMap map = new HashMap();
    Set set = null;
    if (opt == 1) {
      set = this.rowMap.keySet();
    }
    else {
      set = this.colMap.keySet();
    }
    for (Iterator t = set.iterator(); t.hasNext();) {
      String key = (String) t.next();
      String value = "";
      if (opt == 1) {
        value = (String) this.rowMap.get(key);
      }
      else {
        value = (String) this.colMap.get(key);
      }
      map.put(value, key);
    }
    return map;
  }

  /**
   * 得到sql子句
   *
   * @return
   */
  public String getSQL(String expr, int i, String tabid, String userName,
                       ArrayList rowid) {
    StringBuffer sql = new StringBuffer("");
    sql.append(" select " + expr + " from ");
    StringBuffer sql1 = new StringBuffer("");
    HashMap subTabMap = new HashMap();
    for (Iterator t = rowid.iterator(); t.hasNext();) {
      String var = (String) t.next();
      if (subTabMap.get("a" + var) == null) {
        sql1.append(", (select c" + i + "  from tb" + tabid
                + " where  userName='" + userName + "'  and secid=" + var + ")  a"
                + var);
        subTabMap.put("a" + var, "1");
      }

    }
    sql.append(sql1.substring(1));
    return sql.toString();
  }

  public String getStartdate() {
    return this.startdate;
  }

  /**
   * 得到统计结果归档表中列的集合
   *
   * @param
   * @param
   * @param
   * @return
   */
  public ArrayList getTa_TableFields() {

    ArrayList fieldsList = new ArrayList();
    fieldsList.add(this.tgridBo.getField1("unitcode", ResourceFactory
            .getProperty("ttOrganization.unit.unitcode"), "DataType.STRING", 30));
    Field temp21 =
            new Field("secid", ResourceFactory
                    .getProperty("ttOrganization.record.secid"));
    temp21.setDatatype(DataType.INT);
    temp21.setKeyable(true);
    temp21.setVisible(false);
    fieldsList.add(temp21);
    Field temp22 =
            new Field("yearid", ResourceFactory.getProperty("edit_report.year"));
    temp22.setDatatype(DataType.INT);
    temp22.setKeyable(true);
    temp22.setVisible(false);
    fieldsList.add(temp22);
    Field temp23 =
            new Field("countid", ResourceFactory
                    .getProperty("hmuster.label.counts"));
    temp23.setDatatype(DataType.INT);
    temp23.setKeyable(true);
    temp23.setVisible(false);
    fieldsList.add(temp23);

    Field temp33 = new Field("weekid", "weekid");
    temp33.setDatatype(DataType.INT);
    temp33.setKeyable(true);
    temp33.setVisible(false);
    fieldsList.add(temp33);

    fieldsList.add(this.tgridBo.getField1("row_item", ResourceFactory
            .getProperty("reportspacecheck.rowOtherName"), "DataType.STRING", 8));

    for (int i = 0; i < this.getRowInfoBGrid().size(); i++) {
      RecordVo vo = (RecordVo) this.getRowInfoBGrid().get(i);
      String fieldname = "C" + (i + 1);
      if (vo.getString("archive_item") != null
              && !"".equals(vo.getString("archive_item"))
              && !" ".equals(vo.getString("archive_item"))) {
        fieldname = vo.getString("archive_item");
      }

      Field obj = this.tgridBo.getField2(fieldname, fieldname, "N");
      fieldsList.add(obj);
    }

    return fieldsList;
  }

  /**
   * @return Returns the tabid.
   */
  public String getTabid() {
    return this.tabid;
  }

  public ArrayList getTableTermList() {
    return this.tableTermList;
  }

  public String getTableTermSql(int flags, HashMap tableTermsMap) {
    StringBuffer sql = new StringBuffer("");
    if (flags == 1) // 1:扫描人员库
    {
      sql.append(" A0100,NBASE,i9999 ) ");
      StringBuffer sql_str = new StringBuffer("");
      for (Iterator t = this.dbList.iterator(); t.hasNext();) {
        String pre = (String) t.next();
        String tableTermCondition = (String) tableTermsMap.get(pre);
        sql_str.append(" union all  select A0100,'" + pre + "',1 from " + pre
                + "A01 where " + pre + "A01.A0100 in (");
        sql_str.append(tableTermCondition + " ) ");

      }
      sql.append(sql_str.substring(10));
    }
    else if (flags == 2) // 2：单位库
    {
      // sql.append(" B0110 )  select B0110 from B01 WHERE B01.B0110 in  ( "+(String)tableTermsMap.get("B")+" ) ");
      sql.append(" B0110 )  select B0110 from B01 ");
      StringBuffer ext_sql = new StringBuffer();
      Calendar d = Calendar.getInstance();
      int yy = d.get(Calendar.YEAR);
      int mm = d.get(Calendar.MONTH) + 1;
      int dd = d.get(Calendar.DATE);
      String date = this.getBusinessDate();
      if (date != null && date.trim().length() > 0) {
        d.setTime(Date.valueOf(date));
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
      sql
              .append(" and K01.B0110 in ( select codeitemid from organization  where 1=1 "
                      + ext_sql + ")");

    }
    else if (flags == 3) // 3：职位库
    {
      sql.append(" E01A1 )  select E01A1 from K01 WHERE K01.E01A1 in  ( "
              + (String) tableTermsMap.get("K") + " ) ");
      StringBuffer ext_sql = new StringBuffer();
      Calendar d = Calendar.getInstance();
      int yy = d.get(Calendar.YEAR);
      int mm = d.get(Calendar.MONTH) + 1;
      int dd = d.get(Calendar.DATE);
      String date = this.getBusinessDate();
      if (date != null && date.trim().length() > 0) {
        d.setTime(Date.valueOf(date));
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
      sql
              .append(" and K01.E01A1 in ( select codeitemid from organization  where 1=1 "
                      + ext_sql + ")");

    }
    return sql.toString();
  }

  /**
   * 获取归档取数的sql zhaoxg 2013-2-26
   *
   * @return
   */
  public String getTASql(String tabid, String unitcode, RecordVo rowVo,
                         RecordVo colVo, int i, int j, String userName, HashMap tableTermsMap) {
    StringBuffer sql = new StringBuffer("");
    try {
      if ("5".equals(rowVo.getString("flag1"))) {
        if (!"3".equals(rowVo.getString("flag1"))
                || !"4".equals(rowVo.getString("flag1"))) {

          String cexpr1 = rowVo.getString("flag2");// 1:求和, 2:求均值, 3:求最大值,
          // 4:求最小值, 5:平均人数 6：取值
          String cexpr21 =
                  this.tgridBo.getCexpr2Context(14, rowVo.getString("cexpr2"));// 归档指标代号
          String cexpr22 =
                  this.tgridBo.getCexpr2Context(15, rowVo.getString("cexpr2"));// 归档类型
          // 1一般
          // 2年报
          // 3半年
          // 4季报
          // 5月报
          // 6周报
          String cexpr23 =
                  this.tgridBo.getCexpr2Context(16, rowVo.getString("cexpr2"));// 开始时间
          String cexpr24 =
                  this.tgridBo.getCexpr2Context(17, rowVo.getString("cexpr2"));// 结束时间
          String cexpr25 =
                  this.tgridBo.getCexpr2Context(18, rowVo.getString("cexpr2"));// 年份
          String yeartemp = "";
          String date = this.appdate;
          String date_year = date.split("-")[0];
          String upyear = Integer.parseInt(date_year) - 1 + "";
          String date_month = date.split("-")[1];
          if ("当前年".equals(cexpr25)) {
            yeartemp = date_year;
          }
          else {
            String[] zxgyear = cexpr25.split("年");
            yeartemp = zxgyear[0];
          }
          if ("1".equals(cexpr1)) // 求和
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select sum(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid = "
                      + yeartemp + " and countid between " + count + " and "
                      + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select sum(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid between "
                      + year + " and " + year2 + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              // sql.append("(select SUM(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              // wangcq 2014-11-24
              sql
                      .append("(select SUM(w) as v from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
          }
          else if ("2".equals(cexpr1)) // 求平均值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select avg(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid = "
                      + yeartemp + " and countid between " + count + " and "
                      + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select avg(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid between "
                      + year + " and " + year2 + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              // sql.append("(select avg(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
          }
          else if ("3".equals(cexpr1)) // 求最大值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select max(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid = "
                      + yeartemp + " and countid between " + count + " and "
                      + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select max(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid between "
                      + year + " and " + year2 + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              // sql.append("(select max(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
          }
          else if ("4".equals(cexpr1)) // 求最小值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select min(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid = "
                      + yeartemp + " and countid between " + count + " and "
                      + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select min(" + cexpr21 + ") as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid between "
                      + year + " and " + year2 + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              // sql.append("(select min(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+cexpr21+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + cexpr21
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + colVo.getString("archive_item")
                              + "' and xxxx between "
                              + start + " and " + end + " group by row_item) ");

            }
          }
          else if ("5".equals(cexpr1)) // 求平均人数
          {
            sql.append("");
          }
          else if ("6".equals(cexpr1)) // 取值
          {
            if ("1".equals(cexpr22)) {
              if ("上次".equals(cexpr23)) {
                sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                        + " where unitcode='" + unitcode + "' and row_item='"
                        + colVo.getString("archive_item") + "' and yearid = "
                        + yeartemp + " and countid=(select max(countid) from ta_"
                        + tabid + " where unitcode='" + unitcode + "')) ");
              }
              else {
                String count = cexpr23.split("次")[0];
                sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                        + " where unitcode='" + unitcode + "' and row_item='"
                        + colVo.getString("archive_item") + "' and yearid = "
                        + yeartemp + " and countid=" + count + ") ");
              }
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = Integer.parseInt(date_year) - 1 + "";
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid=" + year
                      + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int countid = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int countid = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int countid = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上一周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              sql.append("( select " + cexpr21 + " as v from ta_" + tabid
                      + " where unitcode='" + unitcode + "' and row_item='"
                      + colVo.getString("archive_item") + "' and yearid='" + year
                      + "' and countid='" + month + "' and weekid = " + weekid
                      + ") ");
            }
            // sql.append("( select "+cexpr21+" as v from ta_"+tabid+" where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"') as a"+j+"");
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql.toString();
  }

  /**
   * 产生临时表数据的sql语句
   *
   * @param a_fieldNameList 列集合
   * @param a_variaFieldNameList 临时变量列集合
   * @param fieldSetMap 指标与指标集对应关系
   * @param dbList 扫描范围
   * @param result 是否扫描结果库
   * @param appdate 扫描截止日期
   * @param userName 用户名
   * @param flag 1:扫描人员库 2：单位库 3：职位库
   * @param conditionSql 权限控制语句
   * @return
   */
  public String getTempSQL(String userName, HashSet a_fieldNameList,
                           HashSet a_variaFieldNameList, HashMap fieldSetMap, int flag,
                           HashMap tableTermsMap) {
    StringBuffer sql = new StringBuffer("");
    if (flag == 1) {
      sql.append(this.getATempSQL(userName, a_fieldNameList,
              a_variaFieldNameList, fieldSetMap, tableTermsMap));
    }
    else if (flag == 2) {
      sql.append(this.getBTempSQL(userName, a_fieldNameList,
              a_variaFieldNameList, fieldSetMap, tableTermsMap));
    }
    else if (flag == 3) {
      sql.append(this.getKTempSQL(userName, a_fieldNameList,
              a_variaFieldNameList, fieldSetMap, tableTermsMap));
    }

    return sql.toString();
  }

  public TgridBo getTgridBo() {
    return this.tgridBo;
  }

  /**
   * 获取当前时间
   *
   * @return
   */
  public String getTime() {
    String time = "";
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 时间格式
      java.util.Date nowDate = new java.util.Date();// 得到当前时间
      time = sdf.format(nowDate);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return time;
  }

  public RecordVo getTnameVo() {
    return this.tnameVo;
  }

  /**
   * 得到表对象
   *
   * @param tabid
   */
  public RecordVo getTnameVoById(String tabid) {
    RecordVo vo = new RecordVo("tname");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      if (tabid == null || tabid.trim().length() <= 0) {
        return vo;
      }
      recset = dao.search("select * from tname where tabid=" + tabid);
      if (recset.next()) {
        vo.setInt("tabid", recset.getInt("tabid"));
        vo.setInt("tsortid", recset.getInt("tsortid"));
        vo.setString("name", recset.getString("name"));
        vo.setDouble("tmargin", recset.getDouble("tmargin"));
        vo.setDouble("bmargin", recset.getDouble("bmargin"));
        vo.setDouble("lmargin", recset.getDouble("lmargin"));
        vo.setDouble("rmargin", recset.getDouble("rmargin"));
        vo.setInt("paper", recset.getInt("paper"));
        vo.setInt("paperori", recset.getInt("paperori"));
        vo.setDouble("paperw", recset.getDouble("paperw"));
        vo.setDouble("paperh", recset.getDouble("paperh"));
        vo.setString("moduleflag", recset.getString("moduleflag"));
        vo.setString("flag1", recset.getString("flag1"));
        vo.setString("flag2", recset.getString("flag2"));
        vo.setString("fontname", recset.getString("fontname"));
        vo.setInt("fonteffect", recset.getInt("fonteffect"));
        vo.setInt("fontsize", recset.getInt("fontsize"));
        vo.setInt("narch", recset.getInt("narch"));
        vo.setString("cbase", recset.getString("cbase"));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    return vo;
  }

  public TpageBo getTpageBo() {
    return this.tpageBo;
  }

  public String getUnitcode() {
    return this.unitcode;
  }

  // 根据用户名得到其填报单位的id
  public String getUnitcode(String userName) {
    String unitcode = "";
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      recset =
              dao.search("select unitcode from operuser where userName='"
                      + userName + "'");
      if (recset.next()) {
        unitcode = recset.getString(1);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    return unitcode;
  }

  /**
   * 获取单位邮箱地址
   */
  public ArrayList getUnitsList(String unitcode) {
    ArrayList unitslist = new ArrayList();
    StringBuffer sql = new StringBuffer();
    sql.append("select * from  (select * from operuser where (email is not null and "+Sql_switcher.length("email")+"!=0)or (phone is not null and "+Sql_switcher.length("phone")+"!=0) ) o inner join (select * from tt_organization where unitcode like '");
    sql.append(unitcode);
    sql.append("%') tt on o.unitcode=tt.unitcode");
    ContentDAO dao = new ContentDAO(this.conn);
    try {
      RowSet rs1 = dao.search(sql.toString());
      while (rs1.next()) {
        LazyDynaBean bean = new LazyDynaBean();
        if (rs1.getString("FullName") != null
                && rs1.getString("FullName").length() != 0) {
          bean.set("usrName", rs1.getString("FullName"));
        }
        else {
          bean.set("usrName", rs1.getString("UserName"));
        }
        bean.set("unitcode", rs1.getString("unitcode"));
        bean.set("phone", rs1.getString("phone"));
        bean.set("email", rs1.getString("email"));
        unitslist.add(bean);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return unitslist;
  }

  public void getUpTbxxByNotCondition(ArrayList rowTermList,
                                      ArrayList colTermList, int i, int j, ArrayList resultlist,
                                      ArrayList updatelist) {
    if (resultlist.size() > 0) {
      boolean upflag = true;
      for (Iterator t = rowTermList.iterator(); t.hasNext();) {
        String[] temp = (String[]) t.next();
        // flag1,flag2,cexpr1,cexpr2
        if (("2".equals(temp[1]) || "3".equals(temp[1])) && "1".equals(temp[2])
                && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
          upflag = upflag && true;
        }
        else {
          upflag = false;
        }
      }
      for (Iterator t = colTermList.iterator(); t.hasNext();) {
        String[] temp = (String[]) t.next();
        if (("2".equals(temp[1]) || "3".equals(temp[1])) && "1".equals(temp[2])
                && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
          upflag = upflag && true;
        }
        else {
          upflag = false;
        }
      }
      if (upflag) {
        String[] temp3 = (String[]) resultlist.get(i);
        String upstr =
                "update tb" + this.tabid + " set C" + (j + 1) + "=" + temp3[j]
                        + " where lower(userName)='"
                        + this.userview.getUserName().toLowerCase() + "' and secid="
                        + (i + 1);
        updatelist.add(upstr);

      }
    }
  }

  public void getUpTbxxByRowLimit(ArrayList colTermList, int i,
                                  ArrayList resultlist, ArrayList updaterowlimit) {
    if (resultlist.size() > 0) {
      boolean upflag = false;
      int fromIndex = 0;
      int toIndex = 0;
      String comp = "";
      for (Iterator t = colTermList.iterator(); t.hasNext();) {
        String[] temp = (String[]) t.next();
        if (temp[5].trim().length() > 0) {
          if (temp[5].toUpperCase().indexOf("<ROWDATE>") != -1
                  && temp[5].toUpperCase().indexOf("</ROWDATE>") != -1
                  && temp[5].toUpperCase().indexOf("<ROWDATETYPE>") != -1
                  && temp[5].toUpperCase().indexOf("</ROWDATETYPE>") != -1) {
            fromIndex = temp[5].toUpperCase().indexOf("<ROWDATE>");
            toIndex = temp[5].toUpperCase().indexOf("</ROWDATE>");
            String te1 =
                    temp[5].toUpperCase().substring(fromIndex + 9, toIndex).trim();
            fromIndex = temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
            toIndex = temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
            String te2 =
                    temp[5].toUpperCase().substring(fromIndex + 13, toIndex).trim();
            if (ConstantParamter.getAppdate(this.userview.getUserName()) != null) {
              String value =
                      ConstantParamter.getAppdate(this.userview.getUserName())
                              .replaceAll("\\.", "-");
              int app = 0;
              // int start = 0;
              if (value != null && value.length() > 7) {
                app = Integer.parseInt(value.substring(5, 7));
              }
              if (te2.indexOf("M") != -1 && app != 0) {
                if (te1.equals("" + app)) {
                  upflag = false;

                }
                else {
                  upflag = true;
                  break;

                }
              }
              if (te2.indexOf("Q") != -1 && app != 0) {
                if (0 < app && app < 4) {
                  comp = "1";
                }
                if (3 < app && app < 7) {
                  comp = "2";
                }
                if (6 < app && app < 10) {
                  comp = "3";
                }
                if (9 < app && app < 13) {
                  comp = "4";
                }
                if (te1.equals("" + comp)) {
                  upflag = false;

                }
                else {
                  upflag = true;
                  break;

                }
              }
              if (te2.indexOf("Y") != -1 && app != 0) {
                if (0 < app && app < 7) {
                  comp = "1";
                }
                if (6 < app && app < 13) {
                  comp = "2";
                }
                if (te1.equals("" + comp)) {
                  upflag = false;

                }
                else {
                  upflag = true;
                  break;

                }
              }
            }
          }
        }
      }
      if (upflag) {
        String[] temp3 = (String[]) resultlist.get(i);
        String upstr = "";
        for (int j = 0; j < this.rowInfoList.size(); j++) {
          upstr =
                  "update tb" + this.tabid + " set C" + (j + 1) + "=" + temp3[j]
                          + " where lower(userName)='"
                          + this.userview.getUserName().toLowerCase() + "' and secid="
                          + (i + 1);
          updaterowlimit.add(upstr);
        }

      }
    }
  }

  public String getUserName() {
    return this.userName;
  }

  public UserView getUserview() {
    return this.userview;
  }

  public String getUsrID() {
    return this.usrID;
  }

  /**
   * @param param 参数值
   * @param flag 1:有数据 0：无数据
   * @param tabid 表id
   * @param sortID 表类id
   * @param scope_id 0:全局参数 1：表类参数 2：表参数
   * @return
   */
  public void inserParam(String[] param, int flag, String tabName,
                         String scope_id, String userName) {

    StringBuffer sb = new StringBuffer("");
    StringBuffer up_str = new StringBuffer("");
    StringBuffer inser_f = new StringBuffer("");
    StringBuffer inser_e = new StringBuffer("");
    if (flag == 1) {
      sb.append("update " + tabName + " set ");
    }
    else {
      sb.append("insert into " + tabName + " ( unitcode ");
    }

    int num = 0;
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      for (int i = 0; i < param.length; i++) {
        String temp = (String) param[i];
        String[] param_arr = temp.split("#");
        if (param_arr[2].equals(scope_id)) {

          if (flag == 1) {
            if (param_arr[3].equals(ResourceFactory
                    .getProperty("kq.formula.counts"))) {
              if (param_arr[1].trim().length() > 0) {
                up_str.append(", " + param_arr[0] + "=" + param_arr[1]);
                num++;
              }
              else {
                up_str.append(", " + param_arr[0] + "=null");
                num++;
              }
            }
            else if (param_arr[3].equals(ResourceFactory
                    .getProperty("report.parse.d"))) {
              if ("".equalsIgnoreCase(param_arr[1])) {
                up_str.append(", " + param_arr[0] + "=null");
              }
              else {
                up_str.append(", " + param_arr[0] + "="
                        + Sql_switcher.charToDate("'" + param_arr[1] + "'"));
              }
              num++;
            }
            else {
              up_str.append(", " + param_arr[0] + "='" + param_arr[1] + "'");
              num++;
            }
          }
          else {

            if (param_arr[3].equals(ResourceFactory
                    .getProperty("kq.formula.counts"))) {
              inser_f.append(", " + param_arr[0]);
              if (param_arr[1].trim().length() > 0) {
                inser_e.append(", " + param_arr[1]);
              }
              else {
                inser_e.append(", null");
              }
              num++;
            }
            else if (param_arr[3].equals(ResourceFactory
                    .getProperty("report.parse.d"))) {
              inser_f.append(", " + param_arr[0]);
              if (param_arr[1].trim().length() > 0) {
                inser_e.append(", "
                        + Sql_switcher.charToDate("'" + param_arr[1] + "'"));
              }
              else {
                inser_e.append(", null");
              }
              num++;
            }
            else {
              inser_f.append(", " + param_arr[0]);
              inser_e.append(", " + "'" + param_arr[1] + "'");
              num++;
            }
          }
        }
      }
      if (num > 0) {
        if (flag == 1) {
          sb.append(up_str.substring(1) + " where unitcode='" + userName + "'");
          dao.update(sb.toString());
        }
        else {
          sb.append(inser_f.toString() + " ) values ( '" + userName + "'"
                  + inser_e.toString() + " )");
          dao.insert(sb.toString(), new ArrayList());
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void insertByRowLimit(String tabid, ContentDAO dao,
                               String operateObject, String username, String unitcode) {// 补起数据
    DbWizard dbwizard = new DbWizard(this.conn);
    String tablename = "tt_" + tabid;
    if (operateObject != null && "1".equals(operateObject)) {
      tablename = "tb" + tabid;
    }
    if (dbwizard.isExistTable(tablename, false)) {
      ArrayList colInfoList = this.colInfoList;
      ArrayList rowInfoList = this.rowInfoList;
      if (colInfoList == null || colInfoList.size() < 1 || rowInfoList == null
              || rowInfoList.size() < 1) {
        ArrayList rowAndColInfoList;
        TgridBo tgridBo = new TgridBo(this.conn);
        HashMap rowMap = new HashMap();
        HashMap colMap = new HashMap();
        try {
          rowAndColInfoList =
                  tgridBo.getRowAndColInfoList(tgridBo.getGridInfoList(tabid),
                          tabid, rowMap, colMap);
          rowInfoList = (ArrayList) rowAndColInfoList.get(0);
          colInfoList = (ArrayList) rowAndColInfoList.get(1);
        }
        catch (GeneralException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      ReportResultBo resultbo = new ReportResultBo(this.conn);
      ArrayList resultlist = resultbo.getTBxxResultList(tabid, username);
      RowSet rs = null;
      for (int j = 0; j < colInfoList.size(); j++) {

        if (resultlist.size() >= j) {

          try {
            if (operateObject != null && "1".equals(operateObject)) {
              rs =
                      dao.search("select * from  tb" + tabid
                              + " where username = '" + username + "' and secid="
                              + (j + 1));
            }
            else {
              rs =
                      dao.search("select * from tt_" + tabid
                              + " where unitcode = '" + unitcode + "' and secid="
                              + (j + 1));
            }

            if (rs != null && rs.next()) {

            }
            else {

              StringBuffer strb = new StringBuffer();
              StringBuffer vals = new StringBuffer();
              strb.append(" insert into ");
              if (operateObject != null && "1".equals(operateObject)) {
                strb.append(" tb" + tabid + " ");
              }
              else {
                strb.append(" tt_" + tabid + " ");
              }
              strb.append(" (");
              vals.append(" values(");
              for (int a = 0; a < rowInfoList.size(); a++) {
                strb.append("C" + (a + 1) + ",");
                vals.append("0.0,");
              }
              if (operateObject != null && "1".equals(operateObject)) {
                strb.append(" username,");
                vals.append("'" + username + "',");
              }
              else {
                strb.append(" unitcode,");
                vals.append("'" + unitcode + "',");
              }

              strb.append(" secid");
              strb.append(" )");
              vals.append((j + 1));
              vals.append(" )");
              strb.append(vals);
              dao.update(strb.toString());
            }
          }
          catch (SQLException e) {
            e.printStackTrace();
          }

        }
      }

      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  public void insertMidVariable(int flags, HashSet a_variaFieldNameList,
                                String tablename, ArrayList dbList, String result, String userName) {
    int infoGroup = 0; // forPerson 人员
    ArrayList alUsedFields = new ArrayList();

    if (flags == 2) {
      infoGroup = 3; // forUnit 单位
      alUsedFields =
              DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,
                      Constant.UNIT_FIELD_SET);
    }
    else if (flags == 3) {
      FieldItem fielditem = null;
      infoGroup = 1; // forPosition 职位
      ArrayList unitFieldList =
              alUsedFields =
                      DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,
                              Constant.UNIT_FIELD_SET);
      ArrayList positionFieldList =
              alUsedFields =
                      DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,
                              Constant.POS_FIELD_SET);
      for (Iterator t = unitFieldList.iterator(); t.hasNext();) {
        fielditem = (FieldItem) t.next();
        alUsedFields.add(fielditem);
      }
      for (Iterator t = positionFieldList.iterator(); t.hasNext();) {
        fielditem = (FieldItem) t.next();
        alUsedFields.add(fielditem);
      }
    }

    // 按顺序计算临时变量 dengcan 2017-01-18
    for (Iterator t3 = this.midVariableList.iterator(); t3.hasNext();) {
      RecordVo tempVo = (RecordVo) t3.next();
      String cname = tempVo.getString("cname");
      String cvalue = tempVo.getString("cvalue");

      if (a_variaFieldNameList.contains(cname)) {

        if (infoGroup == 0) {
          for (int i = 0; i < dbList.size(); i++) {
            String dbpre = (String) dbList.get(i);
            this.updateTempVariable(tablename, tempVo, dbpre, infoGroup,
                    alUsedFields, result, userName);
          }
        }
        else {
          this.updateTempVariable(tablename, tempVo, "Usr", infoGroup,
                  alUsedFields, result, userName);
        }

      }

    }

    /*
     * for(Iterator t2=a_variaFieldNameList.iterator();t2.hasNext();)
     * {
     * String variableTemp=(String)t2.next();
     * RecordVo tempVo=null;
     * for(Iterator t3=midVariableList.iterator();t3.hasNext();)
     * {
     * RecordVo vo=(RecordVo)t3.next();
     * if(vo.getString("cname").trim().equals(variableTemp))
     * {
     * tempVo=vo;
     * break;
     * }
     * }
     * if(tempVo!=null)
     * {
     * if(infoGroup==0)
     * {
     * for(int i=0;i<dbList.size();i++)
     * {
     * String dbpre=(String)dbList.get(i);
     * updateTempVariable(tablename,tempVo,dbpre,infoGroup,alUsedFields,result,
     * userName);
     * }
     * }
     * else
     * {
     * updateTempVariable(tablename,tempVo,"Usr",infoGroup,alUsedFields,result,
     * userName);
     * }
     * }
     * }
     */

  }

  /**
   * 将二维数组里的数据插入结果表
   *
   * @param results 计算结果
   * @param userName 用户名
   */
  public void insertResultTable(double[][] results, String userName,
                                String tabid) {

    ContentDAO dao = new ContentDAO(this.conn);
    String del_sql =
            "delete from TB" + tabid + " where UserName='" + userName + "'";
    try {
      this.isExistTable(tabid);
      dao.delete(del_sql, new ArrayList());
      for (int i = 0; i < results.length; i++) {
        StringBuffer sql = new StringBuffer("insert into TB");
        StringBuffer sqlSub = new StringBuffer("");
        sql.append(tabid);
        sql.append(" (UserName,SecId");
        for (int j = 0; j < results[i].length; j++) {
          sql.append(",C");
          sql.append(j + 1);
          sqlSub.append(",");
          sqlSub.append(results[i][j]);

        }
        sql.append(")values('");
        sql.append(userName + "'," + (i + 1));
        sql.append(sqlSub.toString());
        sql.append(")");
        dao.insert(sql.toString(), new ArrayList());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 往临时表插入数据
   *
   * @param fieldNameList 列集合
   * @param dbList 扫描范围
   * @param result 是否扫描结果库
   * @param appdate 扫描截止日期
   * @param flags 1:扫描人员库 2：单位库 3：职位库
   *  注意：flags=1时需要处理人员范围的情况 应该是最后处理了，参考tablename=t#"+username+"_tjb_A_cd的情况
   * @param conditionSql 权限控制语句
   * @param isSuper_admin 是否是超级用户
   * @return
   */
  public boolean insertTempDate(String username, HashSet a_fieldNameList,
                                HashSet a_variaFieldNameList, int flags, HashMap tableTermsMap) {
    boolean flag = true;
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      String tableName = "";
      String indexColumn = "";
      if (flags == 1) {
        tableName = "t#" + username + "_tjb_A";
        indexColumn = "A0100,NBASE";
      }
      else if (flags == 2) {
        tableName = "t#" + username + "_tjb_B";
        indexColumn = "B0110";
      }
      else if (flags == 3) {
        tableName = "t#" + username + "_tjb_K";
        indexColumn = "E01A1";
      }

      StringBuffer sql = new StringBuffer("insert into " + tableName + " (");
      if (a_fieldNameList.size() > 0) {
        HashMap fieldSetMap = this.tgridBo.getFieldSetMap(a_fieldNameList);
        // 得到与列相对应表信息的hashmap
        StringBuffer sql1 = new StringBuffer("");
        for (Iterator t1 = a_fieldNameList.iterator(); t1.hasNext();) {
          String temp = (String) t1.next();
          sql1.append(",");
          sql1.append(temp);

        }
        sql.append(sql1.substring(1));
        sql.append(" ) ");
        sql.append(this.getTempSQL(username, a_fieldNameList,
                a_variaFieldNameList, fieldSetMap, flags, tableTermsMap));

      }
      else {
        sql.append(this.getTableTermSql(flags, tableTermsMap));
      }
      dao.insert(sql.toString(), new ArrayList());
      // 插入临时变量
      dao.update("create index " + tableName + "id on " + tableName + " ("
              + indexColumn + ")");
      // liubq 给所有字段加上索引
      int idx = 0;
      for (Iterator t1 = a_fieldNameList.iterator(); t1.hasNext();) {
        String temp = (String) t1.next();
        if (StringUtils.equals("A0100", temp)
                || StringUtils.equals("NBASE", temp)
                || StringUtils.equals("B0110", temp)
                || StringUtils.equals("E01A1", temp)) {
          continue;
        }
        dao.update("create index " + tableName + "id" + idx + " on "
                + tableName + " (" + temp + ")");
        idx++;
      }
      // wangcq 2015-1-6 end
      boolean variflag = false;
      String tableTermConditioncopy = "";

      if (flags == 2) {
        // copyTempTable2(tableName);
        // dao.update("create index "+tableName+"_idxc on "+tableName+"_cc ("+indexColumn+")");

        String tableTermCondition = (String) tableTermsMap.get("B");

        // 从结果表里取数
        if (tableTermCondition != null && tableTermCondition.length() > 0) {
          String varivastr = "";
          tableTermConditioncopy = tableTermCondition;
          if (a_variaFieldNameList.size() > 0) {
            for (Iterator t2 = a_variaFieldNameList.iterator(); t2.hasNext();) {
              String variableTemp = (String) t2.next();
              varivastr = tableName + "." + variableTemp;
              if (tableTermCondition.toLowerCase().indexOf(
                      varivastr.toLowerCase()) != -1) {
                varivastr = "-1";
                variflag = true;
                break;
              }
            }
          }
        }
      }

      if (variflag) {
        if (tableTermConditioncopy != null
                && tableTermConditioncopy.length() > 0) {
          HashSet a_aricatitle = new HashSet();
          for (Iterator t2 = a_variaFieldNameList.iterator(); t2.hasNext();) {
            String variableTemp = (String) t2.next();
            String varivastr = tableName + "." + variableTemp;
            if (tableTermConditioncopy.toLowerCase().indexOf(
                    varivastr.toLowerCase()) != -1) {
              a_aricatitle.add(variableTemp);
            }
          }
          if (a_aricatitle.size() > 0) {
            for (Iterator t2 = a_aricatitle.iterator(); t2.hasNext();) {
              String variableTemp = (String) t2.next();
              a_variaFieldNameList.remove(variableTemp);
            }
            this.insertMidVariable(flags, a_aricatitle, tableName, this.dbList,
                    this.result, username);
          }
        }

      }
      if (a_variaFieldNameList.size() > 0) {
        this.insertMidVariable(flags, a_variaFieldNameList, tableName,
                this.dbList, this.result, username);
      }
    }
    catch (Exception e) {
      flag = false;
      e.printStackTrace();
    }

    return flag;
  }

  /**
   * 构建临时表里并插入数据
   *
   * @param userid 用户id
   * @param fieldNameList 列表项集合
   * @return
   */
  public boolean insertTempTable(String username, String userid,
                                 ArrayList fieldNameList, HashMap tableTermsMap) {
    boolean isSucc = true;
    try {

      for (int i = 0; i < fieldNameList.size() - 2; i++) {
        HashSet a_fieldNameList = (HashSet) fieldNameList.get(i++);
        HashSet a_variaFieldNameList = (HashSet) fieldNameList.get(i);
        if (i == 1 && (a_fieldNameList.size() > 0 || tableTermsMap.size() > 0)
                || i > 1 && a_fieldNameList.size() > 0) {
          int flag = 1;
          if (i == 3) {
            flag = 2;
          }
          else if (i == 5) {
            flag = 3;
          }
          this.insertTempDate(username, a_fieldNameList, a_variaFieldNameList,
                  flag, tableTermsMap);
        }
      }
    }
    catch (Exception e) {
      isSucc = false;
      e.printStackTrace();
    }
    return isSucc;
  }

  public boolean isDataScopeEnabled() {
    return this.dataScopeEnabled;
  }

  // 判断结果表中是否已提取数据
  public boolean isExistData(String tabid, String userName) {
    boolean isExistData = false;
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      recset =
              dao.search("select * from tb" + tabid + "  where userName='"
                      + userName + "' and secid=1");
      if (recset.next()) {
        isExistData = true;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    return isExistData;
  }

  // 判断结果表(tbXXX)是否存在
  public boolean isExistTable(String tabid) {
    boolean isExist = true;
    try {
      Table table = new Table("tb" + tabid);
      table.setCreatekey(true);
      DbWizard dbWizard = new DbWizard(this.conn);
      if (!dbWizard.isExistTable("tb" + tabid, false)) {
        isExist = false;
        // 如果不存在该统计结果表，则新建一个
        ArrayList fieldList =
                this.tgridBo.getTB_TableFields(this.rowInfoBGrid.size());
        for (Iterator t = fieldList.iterator(); t.hasNext();) {
          Field temp = (Field) t.next();
          table.addField(temp);
        }
        dbWizard.createTable(table);
        dbWizard.addPrimaryKey(table);
        if (this.dbmodel == null) {
          this.dbmodel = new DBMetaModel(this.conn);
        }
        this.dbmodel.reloadTableModel("tb" + tabid);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return isExist;
  }

  // 判断结果表(tbXXX)是否存在
  public boolean isExistTable(String tabid, int colnums) {
    boolean isExist = true;
    try {
      Table table = new Table("tb" + tabid);
      table.setCreatekey(false);
      DbWizard dbWizard = new DbWizard(this.conn);
      if (!dbWizard.isExistTable("tb" + tabid, false)) {
        isExist = false;
        // 如果不存在该统计结果表，则新建一个
        ArrayList fieldList = this.tgridBo.getTB_TableFields(colnums);
        for (Iterator t = fieldList.iterator(); t.hasNext();) {
          Field temp = (Field) t.next();
          table.addField(temp);
        }
        table.setCreatekey(false);
        dbWizard.createTable(table);
        if (this.dbmodel == null) {
          this.dbmodel = new DBMetaModel(this.conn);
        }
        this.dbmodel.reloadTableModel("tb" + tabid);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return isExist;
  }

  public boolean isReject(String unitcode, String tsort) {
    boolean reject = false;
    String sql =
            "select count(*) from treport_ctrl where tabid in (select tabid from tname where TSortId='"
                    + tsort + "') and unitcode='" + unitcode + "' and status<>2";
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      recset = dao.search(sql);
      if (recset.next()) {
        int n = recset.getInt(1);
        if (n != 0) {

        }
        else {
          reject = true;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return reject;

  }

  /**
   * 是否扫描库
   *
   * @param nbase
   * @return
   */
  public boolean isSelNbase(String nbase) {
    for (int i = 0; i < this.dbList.size(); i++) {
      if (this.dbList.get(i).toString().equalsIgnoreCase(nbase)) {
        return true;
      }
    }
    return false;
  }

  public boolean isShowReportHtmlPaper() {
    return this.showReportHtmlPaper;
  }

  public boolean isShowReportHtmlToolbar() {
    return this.showReportHtmlToolbar;
  }

  public boolean isUpdateDate(boolean ownerflag, int i, String datevalue,
                              String tabid) {
    boolean readycolflag = false;
    if (ownerflag) {// 该表指定所属时间范围

      HashMap rowMap = new HashMap();
      HashMap colMap = new HashMap();
      ArrayList rowAndColInfoList;
      try {
        rowAndColInfoList =
                this.tgridBo.getRowAndColInfoList(this.tgridBo
                        .getGridInfoList(tabid), tabid, rowMap, colMap);
        this.colInfoList = (ArrayList) rowAndColInfoList.get(1);
      }
      catch (GeneralException e) {
        e.printStackTrace();
      }
      ArrayList colTermList = (ArrayList) this.colInfoList.get(i);
      int fromIndex = 0;
      int toIndex = 0;
      String comp = "";
      for (Iterator t = colTermList.iterator(); t.hasNext();) {
        String[] temp = (String[]) t.next();
        if (temp[5].trim().length() > 0) {
          if (temp[5].toUpperCase().indexOf("<ROWDATE>") != -1
                  && temp[5].toUpperCase().indexOf("</ROWDATE>") != -1
                  && temp[5].toUpperCase().indexOf("<ROWDATETYPE>") != -1
                  && temp[5].toUpperCase().indexOf("</ROWDATETYPE>") != -1) {
            fromIndex = temp[5].toUpperCase().indexOf("<ROWDATE>");
            toIndex = temp[5].toUpperCase().indexOf("</ROWDATE>");
            String te1 =
                    temp[5].toUpperCase().substring(fromIndex + 9, toIndex).trim();
            fromIndex = temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
            toIndex = temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
            String te2 =
                    temp[5].toUpperCase().substring(fromIndex + 13, toIndex).trim();
            if (te1.length() > 0
                    && (te2.indexOf("M") != -1 || te2.indexOf("Q") != -1 || te2
                    .indexOf("Y") != -1)) {

              String value = datevalue;
              int app = 0;
              // int start = 0;
              if (value != null && value.length() > 7) {
                app = Integer.parseInt(value.substring(5, 7));
              }
              if (te2.indexOf("M") != -1 && app != 0) {
                if (te1.equals("" + app)) {
                  readycolflag = false;

                }
                else {
                  readycolflag = true;
                  break;

                }
              }
              if (te2.indexOf("Q") != -1 && app != 0) {
                if (0 < app && app < 4) {
                  comp = "1";
                }
                if (3 < app && app < 7) {
                  comp = "2";
                }
                if (6 < app && app < 10) {
                  comp = "3";
                }
                if (9 < app && app < 13) {
                  comp = "4";
                }
                if (te1.equals("" + comp)) {
                  readycolflag = false;

                }
                else {
                  readycolflag = true;
                  break;

                }
              }
              if (te2.indexOf("Y") != -1 && app != 0) {
                if (0 < app && app < 7) {
                  comp = "1";
                }
                if (6 < app && app < 13) {
                  comp = "2";
                }
                if (te1.equals("" + comp)) {
                  readycolflag = false;

                }
                else {
                  readycolflag = true;
                  break;

                }
              }
            }

          }
        }
      }

    }
    else {
      readycolflag = false;
    }
    return readycolflag;
  }

  /**
   * 报表上报
   *
   * @param resultList
   * @param paramValue
   * @param tabid
   * @param rows
   * @param cols
   * @param userid
   * @param userName
   * @param isChangeStatus 是否修改上报状态
   * @return info 1:上报成功 2:上报不成功
   */
  public String ReportAppeal(String tabid, String userName, String unitcode,
                             DbWizard dbWizard, TnameExtendBo tnameExtendBo, boolean isChangeStatus,
                             String content, String userfullname) {
    String info = "1";
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {

      // 修改报表上报状态
      if (isChangeStatus) {
        this.updateStatus(unitcode, tabid, content, 1, userfullname);
      }
      int cols = 0;
      recset =
              dao.search("select * from tb" + tabid + " where userName='"
                      + userName + "' and secid=1");
      ResultSetMetaData metaData = recset.getMetaData();
      cols = metaData.getColumnCount() - 2;
      if (dbWizard.isExistField("tb" + tabid, "scopeid", false)) {// 判断字段是否存在
        cols = cols - 1;
      }
      // 往统计结果表里写入数据
      this.tgridBo.execute_TT_table(tabid, cols);
      // 控制某些行不用修改
      recset =
              dao.search("select * from tb" + tabid + " where userName='"
                      + userName + "' order by secid");
      ArrayList list = new ArrayList();
      list.add(tabid);
      String secids = "";
      boolean ownerflag = this.tgridBo.isSetOwnerDate(list);
      this.userName = userName;
      String datevalue = this.getOwnerDate(tabid);
      while (recset.next()) {
        boolean updateflag =
                this.isUpdateDate(ownerflag, Integer.parseInt(recset
                        .getString("secid")) - 1, datevalue, tabid);
        if (!updateflag) {
          secids += recset.getString("secid") + ",";
        }
      }
      if (secids.length() > 0) {
        secids =
                " and secid in(" + secids.substring(0, secids.length() - 1) + ")";
      }
      dao.delete("delete from tt_" + tabid + " where unitcode='" + unitcode
              + "' " + secids + "", new ArrayList());

      StringBuffer sql_f =
              new StringBuffer("insert into tt_" + tabid + " (unitcode,secid");
      StringBuffer sql_w = new StringBuffer("select '" + unitcode + "',secid");
      for (int i = 1; i <= cols; i++) {
        sql_f.append(",C" + i);
        sql_w.append(",C" + i);

      }
      sql_f.append(") ");
      sql_f.append(sql_w.toString());
      sql_f.append(" from tb" + tabid + " where userName='" + userName + "' "
              + secids + " ");
      dao.insert(sql_f.toString(), new ArrayList());
      if (metaData != null) {
        metaData = null;
      }
      this.insertByRowLimit(tabid, dao, "2", userName, unitcode);
    }
    catch (Exception e) {
      e.printStackTrace();
      info = "2";
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */

    return info;

  }

  // 反查
  public String reverseReport(String userName, HashMap tableTermsMap, int i,
                              int j, ArrayList infoList, String scanMode, ArrayList fieldItemSet) {
    String sql = "";
    ArrayList list = new ArrayList();
    try {
      ArrayList colTermList = (ArrayList) this.colInfoList.get(i);
      ArrayList rowTermList = (ArrayList) this.rowInfoList.get(j);
      sql =
              this.getPerGridSql2(rowTermList, colTermList, i, j, userName,
                      tableTermsMap, infoList, scanMode, fieldItemSet);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql;
  }

  /**
   * 保存参数值
   *
   * @param tabid 表id
   * @param userName 用户名
   * @param setID 表类id
   * @param paramValue 参数值
   */
  public void saveParamValues(String tabid, String userName, String paramValue,
                              String operateObject, String unitcode) {
    RecordVo recordVo = this.getTnameVoById(tabid);
    int sortID = recordVo.getInt("tsortid");
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    DbWizard dbWizard = new DbWizard(this.conn);
    try {
      if ("2".equals(operateObject)) {
        TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
        tnameExtendBo.isExistAppealParamTable(3, tabid, "", dbWizard); // 表参数
        tnameExtendBo.isExistAppealParamTable(2, "", String.valueOf(sortID),
                dbWizard); // 表类参数
        tnameExtendBo.isExistAppealParamTable(1, "", "", dbWizard); // 全局参数
      }

      int is_tpt = 0;
      int is_tps = 0;
      int is_tpp = 0; // 判断相应表中是否有数据
      boolean isExistT = false;
      boolean isExistS = false;
      boolean isExistP = false;
      Table a_table = null; // 参数表

      if ("1".equals(operateObject)) {
        a_table = new Table("tp_p");
      }
      else {
        a_table = new Table("tt_p");
      }
      if (dbWizard.isExistTable(a_table.getName(), false)) {
        isExistP = true;
        if ("1".equals(operateObject)) {
          recset =
                  dao
                          .search("select * from tp_p where unitcode='" + userName
                                  + "'");
        }
        else {
          recset =
                  dao
                          .search("select * from tt_p where unitcode='" + unitcode
                                  + "'");
        }
        if (recset.next()) {
          is_tpp = 1;
        }
      }

      if ("1".equals(operateObject)) {
        a_table = new Table("tp_s" + sortID);
      }
      else {
        a_table = new Table("tt_s" + sortID);
      }
      if (dbWizard.isExistTable(a_table.getName(), false)) {
        isExistS = true;
        if ("1".equals(operateObject)) {
          recset =
                  dao.search("select * from tp_s" + sortID + " where unitcode='"
                          + userName + "'");
        }
        else {
          recset =
                  dao.search("select * from tt_s" + sortID + " where unitcode='"
                          + unitcode + "'");
        }

        if (recset.next()) {
          is_tps = 1;
        }
      }

      if ("1".equals(operateObject)) {
        a_table = new Table("tp_t" + tabid);
      }
      else {
        a_table = new Table("tt_t" + tabid);
      }
      if (dbWizard.isExistTable(a_table.getName(), false)) {
        isExistT = true;
        if ("1".equals(operateObject)) {
          recset =
                  dao.search("select * from tp_t" + tabid + " where unitcode='"
                          + userName + "'");
        }
        else {
          recset =
                  dao.search("select * from tt_t" + tabid + " where unitcode='"
                          + unitcode + "'");
        }
        if (recset.next()) {
          is_tpt = 1;
        }
      }

      StringBuffer tpt_sql = new StringBuffer("");
      StringBuffer tps_sql = new StringBuffer("");
      StringBuffer tpp_sql = new StringBuffer("");
      String[] param = paramValue.split("/");
      if ("1".equals(operateObject)) {
        if (isExistP) {
          this.inserParam(param, is_tpp, "tp_p", "0", userName); // 全局参数
        }
        if (isExistS) {
          this.inserParam(param, is_tps, "tp_s" + sortID, "1", userName); // 表类参数
        }
        if (isExistT) {
          this.inserParam(param, is_tpt, "tp_t" + tabid, "2", userName); // 表参数
        }
      }
      else {
        if (isExistP) {
          this.inserParam(param, is_tpp, "tt_p", "0", unitcode); // 全局参数
        }
        if (isExistS) {
          this.inserParam(param, is_tps, "tt_s" + sortID, "1", unitcode); // 表类参数
        }
        if (isExistT) {
          this.inserParam(param, is_tpt, "tt_t" + tabid, "2", unitcode); // 表参数
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 保存编辑后的报表信息
   *
   * @param resultList 报表数据
   * @param paramValue 参数值
   * @param tabid 表id
   * @param rows 行数
   * @param cols 列数
   * @param userid 用户id
   * @param userName 用户名称
   * @param flag 上报标识信息 1： 修改 0:不修改
   * @param operateObject // 1：编辑没上报表 2：编辑上报后的表
   * @return
   */
  public String saveReportInfo(ArrayList resultList, String paramValue,
                               String tabid, int rows, int cols, String userid, String userName,
                               int flag, String operateObject, String unitcode) {
    this.getBusinessDate();// 获得业务日期
    String info = "1";
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      String a_unitcode = "";
      boolean isSaveAs = false;
      ReportResultBo resultbo = new ReportResultBo(this.conn);
      ArrayList resultl = new ArrayList();
      ArrayList resultl2 = new ArrayList();
      // 保存数据
      if ("1".equals(operateObject)) {
        this.isExistTable(tabid);

        resultl = resultbo.getTBxxResultList(this.tabid, userName);
        dao.delete("delete from tb" + tabid + " where UserName='" + userName
                + "'", new ArrayList());
        // 如果 用户状态为 未填,编辑,打回 则相应的将结果写入上报数据区域
        TTorganization tt_organization = new TTorganization(this.conn);
        tt_organization.setValidedateflag("1");// dml
        RecordVo a_selfVo = tt_organization.getSelfUnit(userName);
        if (a_selfVo != null) {
          a_unitcode = (String) a_selfVo.getString("unitcode");
          recset =
                  dao.search("select status from treport_ctrl where unitcode='"
                          + a_unitcode + "' and tabid=" + tabid);
          String status = "";
          if (recset.next()) {
            status = recset.getString("status");
          }
          if ("0".equals(this.scopeid)
                  && ("-1".equals(status) || "0".equals(status) || "2"
                  .equals(status))) {
            isSaveAs = true;
          }
          if (isSaveAs) {
            this.tgridBo.execute_TT_table(tabid, cols);
            resultl2 = resultbo.getTTxxResultList(this.tabid, unitcode);
            dao.delete("delete from tt_" + tabid + " where unitcode='"
                    + a_unitcode + "'", new ArrayList());
          }
        }
        else {
          isSaveAs = false;// dml
        }
      }
      else {
        if (unitcode != null && !"".equals(unitcode)) {
          this.tgridBo.execute_TT_table(tabid, cols);
          resultl2 = resultbo.getTTxxResultList(this.tabid, unitcode);
          dao.delete("delete from tt_" + tabid + " where unitcode='" + unitcode
                  + "'", new ArrayList());

        }

      }
      ArrayList objectList = new ArrayList();
      ArrayList objectList2 = new ArrayList();
      // TnameBo tnameBo=new TnameBo(this.conn,tabid);
      // Calendar c1 = Calendar.getInstance();
      // int minute1 = c1.get(Calendar.MINUTE);
      // int second1 = c1.get(Calendar.SECOND);
      // System.out.println(minute1+":"+second1);
      ArrayList list = new ArrayList();
      list.add(tabid);
      boolean ownerflag = this.tgridBo.isSetOwnerDate(list);
      this.userName = userName;
      String datevalue = this.getOwnerDate(tabid);
      // Calendar c = Calendar.getInstance();
      // c.setTime(new java.util.Date());
      // int minute = c.get(Calendar.MINUTE);
      // int second = c.get(Calendar.SECOND);
      // System.out.println(minute+":"+second);
      int i, j;// 优化下for循环，速度能快点？？ zhaoxg 2013-6-6
      for (i = 0; i < resultList.size(); i++) {

        ArrayList subList = new ArrayList();
        ArrayList subList2 = new ArrayList();
        // RecordVo colVo=(RecordVo)tnameBo.getColInfoBGrid().get(i);
        if ("1".equals(operateObject)) {
          subList.add(userName);
          if (isSaveAs) {
            subList2.add(a_unitcode);
          }
        }
        else {
          if (unitcode != null && !"".equals(unitcode)) {
            subList.add(unitcode);
          }
        }
        Integer dd = new Integer(i + 1);
        subList.add(dd);
        subList2.add(dd);
        String result = (String) resultList.get(i);
        String resl[] = null;
        if (resultl.size() > 0 && resultl.size() == resultList.size()) {
          resl = (String[]) resultl.get(i);
        }

        String resl2[] = null;
        if (resultl2.size() > 0 && resultl2.size() == resultList.size()) {
          resl2 = (String[]) resultl2.get(i);
        }
        String[] result_arr = result.split("/");
        // 判断是否要更新

        boolean updateflag =
                this.isUpdateDate(ownerflag, i, datevalue, this.tabid);
        // updateflag为true修改不了，为false修改

        for (j = 0; j < result_arr.length; j++) {
          if ("1".equals(operateObject)) {
            if (resl != null && resl.length == result_arr.length) {// 对resultl的值进行四舍五入
              if (updateflag) {
                Double aa = new Double(resl[j]);
                subList.add(aa);
                subList2.add(aa);
              }
              else {
                Double bb = new Double(result_arr[j]);
                subList.add(bb);
                subList2.add(bb);
              }
            }
            else {
              Double cc = new Double(result_arr[j]);
              subList.add(cc);
              subList2.add(cc);
            }

          }
          else {
            if (resl2 != null && resl2.length == result_arr.length) {// 对resultl的值进行四舍五入
              if (updateflag) {
                subList.add(new Double(resl2[j]));
                subList2.add(new Double(resl2[j]));
              }
              else {
                subList.add(new Double(result_arr[j]));
                subList2.add(new Double(result_arr[j]));
              }
            }
            else {
              subList.add(new Double(result_arr[j]));
              subList2.add(new Double(result_arr[j]));
            }

          }
        }
        objectList.add(subList);
        objectList2.add(subList2);
      }
      StringBuffer sql = new StringBuffer();
      StringBuffer sql2 = new StringBuffer();
      StringBuffer sql_f = new StringBuffer("");
      if ("1".equals(operateObject)) {
        sql_f.append("insert into tb" + tabid + " (username,secid");
        if (isSaveAs) {
          sql2.append("insert into tt_" + tabid + " (unitcode,secid");
        }
      }
      else {
        sql_f.append("insert into tt_" + tabid + " (unitcode,secid");
      }

      for (i = 0; i < cols; i++) {
        sql.append(",?");
        sql2.append(",C" + (i + 1));
        sql_f.append(",C" + (i + 1));
      }
      sql_f.append(" ) values (?,?" + sql.toString() + ")");
      sql2.append(" ) values (?,?" + sql.toString() + ")");
      // Category.getInstance("com.hrms.frame.dao.ContentDAO").error("---start--tabid="+tabid+"  objectList="+objectList.size());
      int[] d = dao.batchInsert(sql_f.toString(), objectList);
      if (this.scopeid != null && !"0".equals(this.scopeid)) {
        dao.update("update tb" + tabid + " set scopeid=" + this.scopeid
                + " where  username='" + userName + "'");
      }
      // Category.getInstance("com.hrms.frame.dao.ContentDAO").error("-------"+d.length);
      if (isSaveAs) {
        dao.batchInsert(sql2.toString(), objectList2);
      }
      // 保存参数
      if (paramValue.length() > 0) {
        if ("1".equals(operateObject) || "2".equals(operateObject)
                && unitcode != null && !"".equals(unitcode)) {
          this.saveParamValues(tabid, userName, paramValue, operateObject,
                  unitcode);
        }
      }
      // 修改上报标识信息
      if ("1".equals(operateObject)) {
        if (flag == 1) {
          recset =
                  dao.search("select unitcode from operUser where userName='"
                          + userName + "'");
          if (recset.next()) {
            this.upOrInsertReport_ctrl(recset.getString(1), tabid, "", 0);
          }
        }
      }
      this.sytb(resultList, paramValue, tabid, rows, cols, userid, userName, 1,
              operateObject, unitcode);
    }
    catch (Exception e) {
      e.printStackTrace();
      info = "0";
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */

    return info;
  }

  /**
   * 判断扫描哪个库中的数据 及 返回查询条件集合
   *
   * @param rowTermList
   * @param colTermList
   * @return
   */
  public ArrayList scanStore(ArrayList rowTermList, ArrayList colTermList,
                             int i, int j) {
    int npercent = 0;

    ArrayList infoList = new ArrayList();
    ArrayList list = new ArrayList();
    boolean isr = false; // 是否扫描人员库
    boolean isd = false; // 是否扫描单位库
    boolean isb = false; // 是否扫描部门库
    boolean isz = false; // 是否扫描职位库
    boolean isbd = false; // 是否扫描部门、单位库
    boolean ishistorytimer = false; // 是否历史时点
    boolean ishistorytimec = false; // 是否历史时点
    boolean ishistorytime2 = false; // 是否历史时点

    String[] rowCountExpre = null; // 行计算公式
    String[] colCountExpre = null; // 列计算公式
    String[] rowbottomtemp = null; // 行表头最底层格
    String[] colbottomtemp = null; // 列表头最底层格

    boolean is_noDefine = false; // 是否有未定义的格 默认为的人
    // boolean row_npercent=true;
    // boolean col_npercent=true;
    for (Iterator t = rowTermList.iterator(); t.hasNext();) {
      String[] temp = (String[]) t.next();
      // if(row_npercent)
      {
        if (Integer.parseInt(temp[7]) > npercent) {
          npercent = Integer.parseInt(temp[7]);
        }
      }

      /*
       * if(Integer.parseInt(temp[1])==3) //如果是公式，则以公式为标准。
       * {
       * npercent=Integer.parseInt(temp[7]);
       * row_npercent=false;
       * }
       */

      list.add(temp);
      rowbottomtemp = temp;
      if ("4".equals(temp[1])) {
        continue;
      }
      if ("2".equals(temp[1]) && "1".equals(temp[2])
              && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
        if ("1".equals(temp[0])) {
          is_noDefine = true;
        }
        continue;
      }
      // if(temp[1].equals("5")) //编号过滤 取归档数据 zhaoxg 2013-2-26
      // continue;

      // str[1]=String.valueOf(tempVo.getInt("flag1")); // 取值方式标志
      // str[2]=String.valueOf(tempVo.getInt("flag2")); // 取值方法标志
      // str[3]=tempVo.getString("cfactor");
      // str[4]=tempVo.getString("cexpr1");
      // str[5]=tempVo.getString("cexpr2");

      if ("1".equals(temp[0])) {
        isr = true;
      }
      else if ("2".equals(temp[0])) {
        isd = true;
      }
      else if ("3".equals(temp[0])) {
        isbd = true;
      }
      else if ("4".equals(temp[0])) {
        isb = true;
      }
      else if ("5".equals(temp[0])) {
        isz = true;
      }
      else if ("6".equals(temp[0])) {
        ishistorytimer = true;
      }
      else {
        ishistorytime2 = true;
      }

      if ("2".equals(temp[1]) && "1".equals(temp[2])
              && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
        continue;
      }
      if ("3".equals(temp[1])) {
        rowCountExpre = temp;
      }

    }
    if ((isd == true || isbd == true || isb == true || isz == true)
            && is_noDefine == true) {
      isr = false;
      is_noDefine = false;
    }
    boolean c_isr = false; // 是否扫描人员库
    boolean c_isd = false; // 是否扫描单位库
    boolean c_isb = false; // 是否扫描部门库
    boolean c_isz = false; // 是否扫描职位库
    boolean c_isbd = false; // 是否扫描部门、单位库
    boolean c_ishistorytimer = false; // 是否历史时点
    boolean c_ishistorytimec = false; // 是否历史时点
    boolean c_ishistorytime2 = false; // 是否历史时点
    boolean c_is_noDefine = false; // 是否有未定义的格 默认为的人
    for (Iterator t = colTermList.iterator(); t.hasNext();) {
      String[] temp = (String[]) t.next();
      // if(col_npercent)
      {
        if (Integer.parseInt(temp[7]) > npercent) {
          npercent = Integer.parseInt(temp[7]);
        }
      }
      /*
       * if(Integer.parseInt(temp[1])==3) //如果是公式，则以公式为标准。
       * {
       * npercent=Integer.parseInt(temp[7]);
       * col_npercent=false;
       * }
       */

      list.add(temp);
      colbottomtemp = temp;
      if ("4".equals(temp[1])) {
        continue;
      }

      if ("2".equals(temp[1]) && "1".equals(temp[2])
              && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
        if ("1".equals(temp[0])) {
          c_is_noDefine = true;
        }
        continue;
      }
      // if(temp[1].equals("5")) //编号过滤 取归档数据 zhaoxg 2013-2-26
      // continue;
      if ("1".equals(temp[0])) {
        c_isr = true;
      }
      else if ("2".equals(temp[0])) {
        c_isd = true;
      }
      else if ("3".equals(temp[0])) {
        c_isbd = true;
      }
      else if ("4".equals(temp[0])) {
        c_isb = true;
      }
      else if ("5".equals(temp[0])) {
        c_isz = true;
      }
      else if ("6".equals(temp[0])) {
        c_ishistorytimec = true;
      }
      else {
        c_ishistorytime2 = true;
      }
      // is_noDefine=false; // 以最底层格为准
      if ("2".equals(temp[1]) && "1".equals(temp[2])
              && temp[4].trim().length() == 0 && temp[5].trim().length() == 0) {
        continue;
      }
      if ("3".equals(temp[1])) {
        colCountExpre = temp;
      }
    }
    if ((c_isd == true || c_isbd == true || c_isb == true || c_isz == true)
            && c_is_noDefine == true) {
      c_isr = false;
      c_is_noDefine = false;
    }
    infoList.add(list);
    if (isr || c_isr || is_noDefine || c_is_noDefine) {
      infoList.add("1");
    }
    else if (isz || c_isz) {
      infoList.add("5");
    }
    else if (isb || c_isb) {
      infoList.add("4");
    }
    else if (isd || c_isd) {
      infoList.add("2");
    }
    else {
      if ((ishistorytimec || ishistorytimer) && !ishistorytime2
              || (c_ishistorytimec || c_ishistorytimer) && !c_ishistorytime2) {// 多表头的多行与多列都必须定义历史时点，才走历史时点
        infoList.add("6");
      }
      else {
        infoList.add("3");
      }
    }

    this.digitalResults[i][j] = npercent;
    infoList.add(rowCountExpre);
    infoList.add(colCountExpre);
    infoList.add(rowbottomtemp);
    infoList.add(colbottomtemp);
    return infoList;
  }

  public void sendMail(String unitcode, String username, String desc,
                       String tsort) {
    ContentDAO dao = new ContentDAO(this.conn);
    String reportName = "";
    RowSet recset = null;
    String sql = "select Name from tsort where TsortId=" + tsort;
    String sendFromUnitName = "";
    ArrayList list = new ArrayList();
    LazyDynaBean abean = null;
    try {

      Calendar d = Calendar.getInstance();
      int yy = d.get(Calendar.YEAR);
      int mm = d.get(Calendar.MONTH) + 1;
      int dd = d.get(Calendar.DATE);
      String date = this.getBusinessDate();
      if (date != null && date.trim().length() > 0) {
        d.setTime(Date.valueOf(date));
        yy = d.get(Calendar.YEAR);
        mm = d.get(Calendar.MONTH) + 1;
        dd = d.get(Calendar.DATE);
      }
      StringBuffer ext_sql = new StringBuffer();
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
      recset = dao.search(sql);
      if (recset.next()) {
        reportName = recset.getString(1);
      }
      String subject =
              ResourceFactory.getProperty("label.module.bbgl") + "：" + reportName
                      + " " + ResourceFactory.getProperty("report_collect.rejected")
                      + "！";
      recset =
              dao
                      .search("select tt_organization.unitname,operuser.email,operuser.username  from operuser,tt_organization  where operuser.unitcode=tt_organization.unitcode and operuser.userName='"
                              + username + "'" + ext_sql.toString());
      if (recset.next()) {
        sendFromUnitName = recset.getString("unitname");
      }
      recset =
              dao
                      .search("select tt_organization.unitname,operuser.email,operuser.username  from operuser,tt_organization  where operuser.unitcode=tt_organization.unitcode and operuser.unitcode='"
                              + unitcode + "'" + ext_sql.toString());
      while (recset.next()) {
        abean = new LazyDynaBean();
        String unitname = recset.getString("unitname");
        String email =
                recset.getString("email") != null ? recset.getString("email") : "";
        StringBuffer context =
                new StringBuffer(unitname + ":\r\n       "
                        + ResourceFactory.getProperty("report_collect.hello") + "！\r\n");
        context.append("      " + reportName
                + ResourceFactory.getProperty("report_collect.rejectCause")
                + ":\r\n");
        context.append("      " + desc + "\r\n");
        context.append("                                                   "
                + ResourceFactory.getProperty("report_collect.rejectUnit") + "："
                + sendFromUnitName);

        abean.set("sendTo", email.trim());
        abean.set("subject", subject);
        abean.set("context", context.toString());
        list.add(abean);
      }
      SendEmail sendEmail = new SendEmail();
      sendEmail.setInfo();
      for (int i = 0; i < list.size(); i++) {
        LazyDynaBean abean1 = (LazyDynaBean) list.get(i);
        String sendTo = (String) abean1.get("sendTo");
        String subject1 = (String) abean1.get("subject");
        String context = (String) abean1.get("context");
        if (sendTo.length() > 0) {
          sendEmail.send(sendTo, subject, context);
        }

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void setAppdate(String appdate) {
    if(StringUtils.isEmpty(appdate)) {
        appdate  = getDateArray()[1];
    }
    this.appdate = appdate;
  }
  public String[] getDateArray() {
    RowSet rowSet  = null;
    String appdate ="";
    String startdate="";
    String[] array = new String[2];
    Connection con = null;
    try {
      con = AdminDb.getConnection();
      ContentDAO dao = new ContentDAO(con);
      rowSet = dao.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
      String xml ="";
      if(rowSet.next()) {
          xml = Sql_switcher.readMemo(rowSet, "STR_VALUE");
      }
      AnalyseParams aps = new AnalyseParams(xml);
      HashMap pub = aps.getAttributeValues(userName);
      appdate= (String) pub.get("appdate");//截止日期
      if(StringUtils.isEmpty(appdate)) {
          throw GeneralExceptionHandler.Handle(new Exception("此报表的截至日期不应该为空！"));
      }
      startdate= (String) pub.get("startdate"); //起始日期
      if(StringUtils.isEmpty(startdate)) {
          throw GeneralExceptionHandler.Handle(new Exception("此报表的起始时间不应该为空！"));
      }
      array[0] = startdate;
      array[1] = appdate;
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      PubFunc.closeDbObj(rowSet);
      PubFunc.closeDbObj(con);
    }
    return  array;
  }
  public void setDbList(ArrayList dbList) {
    this.dbList = dbList;
  }

  public void setDigitalResults() {
    this.digitalResults =
            new int[this.colInfoBGrid.size()][this.rowInfoBGrid.size()];
    for (int i = 0; i < this.colInfoBGrid.size(); i++) {
      RecordVo Vo = (RecordVo) this.colInfoBGrid.get(i);
      for (int j = 0; j < this.rowInfoBGrid.size(); j++) {
        RecordVo r_Vo = (RecordVo) this.rowInfoBGrid.get(j);
        this.digitalResults[i][j] =
                Vo.getInt("npercent") > r_Vo.getInt("npercent") ? Vo
                        .getInt("npercent") : r_Vo.getInt("npercent");
      }

    }
  }

  public void setFactorListMap(HashMap factorListMap) {
    this.factorListMap = factorListMap;
  }

  public void setScopeid(String scopeid) {
    this.scopeid = scopeid;
  }

  public void setScopeMap(HashMap scopeMap) {
    this.scopeMap = scopeMap;
  }

  public void setShowReportHtmlPaper(boolean showReportHtmlPaper) {
    this.showReportHtmlPaper = showReportHtmlPaper;
  }

  public void setShowReportHtmlToolbar(boolean showReportHtmlToolbar) {
    this.showReportHtmlToolbar = showReportHtmlToolbar;
  }

  public void setStartdate(String startdate) {
    if(StringUtils.isEmpty(startdate)) {
        startdate  = getDateArray()[0];
    }
    this.startdate = startdate;
  }

  public void setUnitsStatus(String tsort, String unitcode,
                             String selfunitcode, String username, String desc, HashSet set,
                             HashMap hmm, String uncode, HashSet sset) {
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    String parentid = "";
    String unitname = "";
    String sql =
            "update treport_ctrl set status = 2,description='" + desc
                    + "' where tabid in (select tabid from tname where tsortid="
                    + tsort + ") and unitcode='" + unitcode + "' and status=1";
    String sqlk =
            "select unitname,parentid from tt_organization where unitcode='"
                    + unitcode + "' and parentid<>unitcode";
    Calendar d = Calendar.getInstance();
    int yy = d.get(Calendar.YEAR);
    int mm = d.get(Calendar.MONTH) + 1;
    int dd = d.get(Calendar.DATE);
    String date = this.getBusinessDate();
    if (date != null && date.trim().length() > 0) {
      d.setTime(Date.valueOf(date));
      yy = d.get(Calendar.YEAR);
      mm = d.get(Calendar.MONTH) + 1;
      dd = d.get(Calendar.DATE);
    }
    StringBuffer ext_sql = new StringBuffer();
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
    sqlk = sqlk + ext_sql.toString();
    String ss = new String();
    try {
      recset = dao.search(sqlk);
      if (recset.next()) {
        parentid = recset.getString("parentid");
        unitname = recset.getString("unitname");

      }
      if (parentid != null && parentid.length() != 0
              && !parentid.equals(selfunitcode)) {
        if (set.size() == 0) {
          String description = unitname + "因：" + desc + "被驳回！\r\n";
          set.add(parentid);
          sset.add(parentid);
          if (hmm.get(parentid) != null) {
            ss = (String) hmm.get(parentid);
            hmm.remove(parentid);
            ss = ss + uncode + ",";
            hmm.put(parentid, ss);
          }
          else {
            ss = ss + uncode + ",";
            hmm.put(parentid, ss);
          }
          this.setUnitsStatus(tsort, parentid, selfunitcode, username,
                  description, set, hmm, uncode, sset);
        }
        else {
          set.add(parentid);
          sset.add(parentid);
          if (hmm.get(parentid) != null) {
            ss = (String) hmm.get(parentid);
            hmm.remove(parentid);
            ss = ss + uncode + ",";
            hmm.put(parentid, ss);
          }
          else {
            ss = ss + uncode + ",";
            hmm.put(parentid, ss);
          }
          this.setUnitsStatus(tsort, parentid, selfunitcode, username, desc,
                  set, hmm, uncode, sset);
        }

      }
      else {
        if (!parentid.equals(selfunitcode)) {
          if (set.size() == 0) {

          }
          else {
            set.add(unitcode);
            sset.add(unitcode);
            set.add(parentid);
            if (hmm.get(parentid) != null) {
              ss = (String) hmm.get(parentid);
              hmm.remove(parentid);
              ss = ss + uncode + ",";
              hmm.put(parentid, ss);
            }
            else {
              ss = ss + uncode + ",";
              hmm.put(parentid, ss);
            }
          }
          return;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {

      try {
        if (recset != null) {
          recset.close();
        }
      }
      catch (SQLException e) {
        e.printStackTrace();
      }

    }

  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setUserview(UserView userview) {
    this.userview = userview;
  }

  /**
   * 同步下级单位数据和参数（国电）
   * 并将下级单位为未填装态改为编辑
   */
  public void sytb(ArrayList resultList, String paramValue, String tabid,
                   int rows, int cols, String userid, String userName, int flag,
                   String operateObject, String unitcode) {
    ContentDAO dao = new ContentDAO(this.conn);

    if ("2".equals(operateObject)) {
      RowSet recset = null;
      ArrayList lusername = new ArrayList();
      try {
        recset =
                dao.search("select * from operuser where unitcode='" + unitcode
                        + "'");
        while (recset.next()) {
          if (userName.equalsIgnoreCase(recset.getString("UserName"))) {
            return;
          }
          lusername.add(recset.getString("UserName"));

        }
        TnameBo tnameBo = new TnameBo(this.conn, tabid);
        for (int k = 0; k < lusername.size(); k++) {
          // 获得表数据
          ReportResultBo resultbo = new ReportResultBo(this.conn);
          ArrayList resultl =
                  resultbo.getTBxxResultList(this.tabid, (String) lusername.get(k));
          if (unitcode != null && !"".equals(unitcode)) {

            dao.delete("delete from tb" + tabid + " where username='"
                    + (String) lusername.get(k) + "'", new ArrayList());
          }
          ArrayList objectList = new ArrayList();
          for (int i = 0; i < resultList.size(); i++) {
            ArrayList subList = new ArrayList();
            if (lusername.get(k) != null && !"".equals(lusername.get(k))) {
              subList.add((String) lusername.get(k));
            }
            subList.add(new Integer(i + 1));
            String result = (String) resultList.get(i);
            String[] result_arr = result.split("/");
            String resl[] = null;
            if (resultl.size() > 0 && resultl.size() == resultList.size()) {
              resl = (String[]) resultl.get(i);
            }
            RecordVo colVo = (RecordVo) tnameBo.getColInfoBGrid().get(i);
            // 判断是否要更新
            ArrayList list = new ArrayList();
            list.add(tabid);
            boolean ownerflag = this.tgridBo.isSetOwnerDate(list);
            String datevalue = this.getOwnerDate(tabid);

            boolean updateflag =
                    this.isUpdateDate(ownerflag, i, datevalue, this.tabid);
            // updateflag为true修改不了，为false修改
            for (int j = 0; j < result_arr.length; j++) {
              // int npercent=0;
              //
              // RecordVo rowVo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
              // if(rowVo.getInt("flag1")==3)
              // npercent=rowVo.getInt("npercent");
              // else if(colVo.getInt("flag1")==3)
              // npercent=colVo.getInt("npercent");
              // else
              // npercent=rowVo.getInt("npercent")>=colVo.getInt("npercent")?rowVo.getInt("npercent"):colVo.getInt("npercent");
              //
              // //平均人数 小数位
              // if(rowVo.getInt("flag2")==5&&rowVo.getInt("flag1")==1&&rowVo.getString("cexpr2").length()>0)
              // {
              // String[]
              // temp=rowVo.getString("cexpr2").substring(rowVo.getString("cexpr2").indexOf("(")+1,rowVo.getString("cexpr2").indexOf(")")).split(";");
              // npercent=0;
              // if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
              // npercent=Integer.parseInt(temp[2].trim());
              // }
              // if(colVo.getInt("flag2")==5&&colVo.getInt("flag1")==1)
              // {
              // String[]
              // temp=colVo.getString("cexpr2").substring(colVo.getString("cexpr2").indexOf("(")+1,colVo.getString("cexpr2").indexOf(")")).split(";");
              // npercent=0;
              // if(temp.length==3&&Integer.parseInt(temp[2].trim())>0)
              // npercent=Integer.parseInt(temp[2].trim());
              // }
              if (resl != null && resl.length == result_arr.length) {// 对resultl的值进行四舍五入
                // resultl[i][j];
                // if(PubFunc.round(resl[j],npercent).equals(result_arr[j])){
                if (updateflag) {
                  subList.add(new Double(resl[j]));
                }
                else {
                  subList.add(new Double(result_arr[j]));
                }
                // }else{
                // if(updateflag){
                // subList.add(new Double(resl[j]));
                // }else{
                // subList.add(new Double(result_arr[j]));
                // }
                // }
              }
              else {
                subList.add(new Double(result_arr[j]));
              }

            }
            objectList.add(subList);

          }
          StringBuffer sql = new StringBuffer();
          StringBuffer sql_f = new StringBuffer("");
          sql_f.append("insert into tb" + tabid + " (username,secid");
          for (int i = 0; i < cols; i++) {
            sql.append(",?");
            sql_f.append(",C" + (i + 1));
          }
          sql_f.append(" ) values (?,?" + sql.toString() + ")");
          dao.batchInsert(sql_f.toString(), objectList);
        }
        if (paramValue.length() > 0) {
          if ("1".equals(operateObject) || "2".equals(operateObject)
                  && unitcode != null && !"".equals(unitcode)) {
            RecordVo recordVo = this.getTnameVoById(tabid);
            int sortID = recordVo.getInt("tsortid");
            DbWizard dbWizard = new DbWizard(this.conn);
            TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
            tnameExtendBo.isExistAppealParamTable(3, tabid, "", dbWizard); // 表参数
            tnameExtendBo.isExistAppealParamTable(2, "",
                    String.valueOf(sortID), dbWizard); // 表类参数
            tnameExtendBo.isExistAppealParamTable(1, "", "", dbWizard); // 全局参数
            int is_tpt = 0;
            int is_tps = 0;
            int is_tpp = 0;
            boolean isExistT = false;
            boolean isExistS = false;
            boolean isExistP = false;
            Table a_table = null; // 参数表

            for (int k = 0; k < lusername.size(); k++) {
              a_table = new Table("tp_p");
              if (dbWizard.isExistTable(a_table.getName(), false)) {
                isExistP = true;
                recset =
                        dao.search("select * from tp_p where unitcode='"
                                + (String) lusername.get(k) + "'");
                if (recset.next()) {
                  is_tpp = 1;
                }
              }
              a_table = new Table("tp_s" + sortID);
              if (dbWizard.isExistTable(a_table.getName(), false)) {
                isExistS = true;
                recset =
                        dao
                                .search("select * from tp_s" + sortID
                                        + " where unitcode='" + (String) lusername.get(k)
                                        + "'");
                if (recset.next()) {
                  is_tps = 1;
                }
              }
              a_table = new Table("tp_t" + tabid);
              if (dbWizard.isExistTable(a_table.getName(), false)) {
                isExistT = true;
                recset =
                        dao
                                .search("select * from tp_t" + tabid
                                        + " where unitcode='" + (String) lusername.get(k)
                                        + "'");
                if (recset.next()) {
                  is_tpt = 1;
                }
              }
              String[] param = paramValue.split("/");
              if (isExistP) {
                this.inserParam(param, is_tpp, "tp_p", "0", (String) lusername
                        .get(k)); // 全局参数
              }
              if (isExistS) {
                this.inserParam(param, is_tps, "tp_s" + sortID, "1",
                        (String) lusername.get(k)); // 表类参数
              }
              if (isExistT) {
                this.inserParam(param, is_tpt, "tp_t" + tabid, "2",
                        (String) lusername.get(k)); // 表参数
              }
            }
          }
        }
        recset =
                dao.search("select * from treport_ctrl where unitcode='" + unitcode
                        + "' and tabid='" + tabid + "'");
        if (recset.next()) {
          String status = recset.getString("status");
          if (status != null && status.length() != 0) {
            if ("-1".equals(status)) {
              dao.update("update  treport_ctrl set status='0' where unitcode='"
                      + unitcode + "' and tabid='" + tabid + "'");
            }
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      finally {

        try {
          if (recset != null) {
            recset.close();
          }
        }
        catch (SQLException e) {
          e.printStackTrace();
        }

      }
    }
    else {
      return;
    }

  }

  /**
   * 报表上报是否支持审批，批准要有审批意见，zhaoxg 2013-2-16
   *
   * @param unitcode
   * @param tabid
   * @param description 审批意见
   * @param status
   */
  public void updateStatus(String unitcode, String tabid, String description,
                           int status, String userfullname) {
    ContentDAO dao = new ContentDAO(this.conn);
    String description1 = "";
    String content = "";
    try {
      if (description == null || "".equals(description)) {
        dao.update("update treport_ctrl set status=" + status
                + ",description='" + description1 + "' where unitcode='" + unitcode
                + "' and tabid=" + tabid);
      }
      else {
        RowSet recset =
                dao.search("select description from treport_ctrl where unitcode='"
                        + unitcode + "' and tabid=" + tabid);
        if (recset.next()) {
          description1 =
                  recset.getString("description") + ";" + this.getTime() + " "
                          + "由" + " " + userfullname + " " + "批准，" + "批准原因："
                          + description;
          content =
                  "你报批的表：" + tabid + this.getTime() + " " + "由" + " "
                          + userfullname + " " + "批准，" + "批准原因：" + description;
        }
        dao.update("update treport_ctrl set status=" + status
                + ",description='" + description1 + "' where unitcode='" + unitcode
                + "' and tabid=" + tabid);

        ArrayList unitslist = this.getUnitsList(unitcode);
        if (unitslist != null) {
          SendEmail sendEmail = new SendEmail();
          sendEmail.setInfo();
          for (int i = 0; i < unitslist.size(); i++) {
            LazyDynaBean abean1 = (LazyDynaBean) unitslist.get(i);
            String sendTo = (String) abean1.get("email");

            if (sendTo.length() > 0) {
              try {
                String title = "报表审批结果";
                sendEmail.send(sendTo, title, content);
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 插入临时变量结果
   *
   * @param tableName
   * @param tabid
   */
  public void updateTempVariable(String tableName, RecordVo midVo,
                                 String dbpre, int infoGroup, ArrayList alUsedFields, String result,
                                 String userName) {
    ContentDAO dao = new ContentDAO(this.conn);

    try {
      String whl = "  ";
      if (infoGroup == 0) // 职位
      {
        if (result != null && "true".equals(result)) {
          whl = " select A0100 from " + userName + dbpre + "Result";
        }
        else {
          whl = " select A0100 from " + dbpre + "A01";
        }
      }
      else if (infoGroup == 1) // 职位
      {
        if (result != null && "true".equals(result)) {
          whl = " select E01A1 from " + userName + "KResult";
        }
        else {
          whl = " select E01A1 from K01";
        }
      }
      else if (infoGroup == 3) // 单位
      {
        if (result != null && "true".equals(result)) {
          whl = " select B0110 from " + userName + "BResult";
        }
        else {
          whl = " select B0110 from B01";
        }
      }
      int fieldType = midVo.getInt("ntype");
      int fldlen = midVo.getInt("fldlen");
      String codeSet = null;
      String queryCond = midVo.getString("cvalue");
      if (queryCond.indexOf("取自于") != -1) {
        return;
      }

      // System.out.println(queryCond); // 替换 起始日期和截止日期 JinChunhai 2012.11.28
      Calendar d = Calendar.getInstance();
      String startdate = this.getStartdate();
      if (startdate == null || startdate.trim().length() <= 0) {
        startdate =
                d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-"
                        + d.get(Calendar.DATE);
      }
      String _startdate = startdate.replaceAll("-", "\\.");
      if (queryCond.indexOf("合同起始日期") != -1) {
        queryCond = queryCond.replaceAll("合同起始日期", "期日始起同合");
      }
      if (queryCond.indexOf("起始日期()") != -1) {
        queryCond =
                queryCond.replaceAll("起始日期\\(\\)", ("#" + _startdate + "#"));
      }
      else if (queryCond.indexOf("起始日期") != -1) {
        queryCond = queryCond.replaceAll("起始日期", ("#" + _startdate + "#"));
      }
      if (queryCond.indexOf("期日始起同合") != -1) {
        queryCond = queryCond.replaceAll("期日始起同合", "合同起始日期");
      }

      String _appdate = this.getAppdate();
      if (_appdate == null || _appdate.trim().length() <= 0) {
        _appdate =
                d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-"
                        + d.get(Calendar.DATE);
      }
      _appdate = _appdate.replaceAll("-", "\\.");
      if (queryCond.indexOf("合同截止日期") != -1) {
        queryCond = queryCond.replaceAll("合同截止日期", "期日止截同合");
      }
      if (queryCond.indexOf("截止日期()") != -1) {
        queryCond = queryCond.replaceAll("截止日期\\(\\)", ("#" + _appdate + "#"));
      }
      else if (queryCond.indexOf("截止日期") != -1) {
        queryCond = queryCond.replaceAll("截止日期", ("#" + _appdate + "#"));
      }
      if (queryCond.indexOf("期日止截同合") != -1) {
        queryCond = queryCond.replaceAll("期日止截同合", "合同截止日期");
        // System.out.println(queryCond);
      }

      int varType = 6; // float
      String fieldTypeStr = "N";
      if (fieldType == 3) {
        varType = 9; // date
        fieldTypeStr = "D";
      }
      else if (fieldType == 2) {
        fieldTypeStr = "A";
        varType = 7; // STRVALUE
      }

      if (fieldType == 4) {
        codeSet = midVo.getString("codesetid");
        fieldTypeStr = "A";
        varType = 7; // STRVALUE
      }

      String a_dbpre = "";
      if (infoGroup == 0) {
        a_dbpre = dbpre;
        alUsedFields =
                DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,
                        Constant.ALL_FIELD_SET);
      }

      // 指标中加入临时变量指标
      for (Iterator t3 = this.midVariableList.iterator(); t3.hasNext();) {
        RecordVo vo = (RecordVo) t3.next();

        FieldItem item = new FieldItem();
        item.setItemid(vo.getString("cname"));
        item.setFieldsetid(/* "A01" */"");// 没有实际含义
        item.setItemdesc(vo.getString("chz"));
        item.setItemlength(vo.getInt("fldlen"));
        item.setDecimalwidth(vo.getInt("flddec"));
        item.setFormula(vo.getString("cvalue"));
        item.setCodesetid(vo.getString("codesetid"));
        switch (vo.getInt("ntype")) {
          case 1://
            item.setItemtype("N");
            break;
          case 2:
          case 4:// 代码型
            item.setItemtype("A");
            break;
          case 3:
            item.setItemtype("D");
            break;
        }
        item.setVarible(1);
        alUsedFields.add(item);
      }
      ArrayList midVariableList2 =
              this.midVariableBo.getMidVariableList2(this.tabid);
      String saveAppDate = ConstantParamter.getAppdate(userName);
      if (this.appdate != null) {
        ConstantParamter.putAppdate(userName, this.appdate.substring(0, 4)
                + "." + this.appdate.substring(5, 7) + "."
                + this.appdate.substring(8));
      }
      try {
        YksjParser yp =
                new YksjParser(this.userview, alUsedFields, YksjParser.forSearch,
                        varType, infoGroup, "Ht", a_dbpre);
        yp.setVarList(midVariableList2);
        yp.setSupportVar(true);
        YearMonthCount ycm = null;
        // yp.setTargetFieldDecimal(midVo.getInt("flddec"));
        yp.setTargetFieldDecimal(5);
        if (infoGroup == 0) {
          boolean flag2 = false;
          for (Iterator t3 = this.midVariableNotContrlList.iterator(); t3
                  .hasNext();) {
            RecordVo vo = (RecordVo) t3.next();
            if (vo != null && vo.getString("cname").trim().length() > 0) {
              if (midVo.getString("cname").trim().equals(
                      vo.getString("cname").trim())) {
                flag2 = true;
                break;
              }
            }
          }
          if (!flag2) {
            // yp.setExistWhereText(getExistSql(""+infoGroup, dbpre,
            // tableName+"_cc")); //2017-1-5 dengcan 临时变量的引用没法确定是否按权限范围控制
            yp.setExistWhereText(this.getExistSql("" + infoGroup, dbpre,
                    tableName));
          }
        }
        yp.run(queryCond, ycm, midVo.getString("cname"), tableName, dao, whl,
                this.conn, fieldTypeStr, fldlen, 2, codeSet);
      }
      finally {
        if (this.dataScopeEnabled) {
          ConstantParamter.putAppdate(userName, saveAppDate); // 恢复设置
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 修改或插入上报表识信息表
  // status =-1，未填 =0,正在编辑 =1,已上报 =2,打回 =3,封存（基层单位的数据不让修改）
  public void upOrInsertReport_ctrl(String unitcode, String tabid,
                                    String description, int status) {
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    try {
      if (status == 0) {
        recset =
                dao.search("select status from treport_ctrl where unitcode='"
                        + unitcode + "' and tabid=" + tabid);
        if (recset.next()) {
          if (Integer.parseInt(recset.getString("status")) <= 0) {
            dao.update("update treport_ctrl set description='" + description
                    + "' ,status=" + status + " where unitcode='" + unitcode
                    + "' and tabid=" + tabid);
          }

        }
        else {
          dao.insert(
                  "insert into treport_ctrl(unitcode,tabid,description,status)values('"
                          + unitcode + "'," + tabid + ",''," + status + ")",
                  new ArrayList());
        }
      }
      else {
        dao.update("update treport_ctrl set description='" + description
                + "' ,status=" + status + " where unitcode='" + unitcode
                + "' and tabid=" + tabid);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    /*
     * finally
     * {
     * try
     * {
     * if(recset!=null)
     * recset.close();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
  }

  /**
   * 获取报批人信息 zhaoxg 2013-2-17
   *
   * @param userName
   * @return
   * @throws GeneralException
   * @throws SQLException
   */
  private String approve() throws GeneralException, SQLException {
    String approve = "";

    ResultSet rs = null;
    Connection conn = AdminDb.getConnection();
    Statement stmt = conn.createStatement();
    try {
      String sql = "select appuser,username from treport_ctrl";
      rs = stmt.executeQuery(sql.toString());
      while (rs.next()) {
        String appuser = rs.getString("appuser");
        if (appuser != null) {
          String[] aa = appuser.split(";");
          for (int i = 0; i < aa.length; i++) {
            if (aa[i].equals(this.userview.getUserFullName())) {
              approve = rs.getString("username");
            }
          }
        }

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {

		PubFunc.closeResource(rs);
		PubFunc.closeResource(stmt);
		PubFunc.closeResource(conn);
    }

    return approve;
  }

  /**
   * 取数范围日期参数值
   *
   * @param dateDef 日期定义, 分隔符为“.”
   * @return 分隔符为“-”
   */
  private String getDateValue(String dateDef) {
    String d = "";
    if ("$THISYSSTART[]".equals(dateDef) || "$APPDATE[]".equals(dateDef)) {
      String appdate =
              ConstantParamter.getAppdate(
                      this.userview != null ? this.userview.getUserName()
                              : this.userName).replaceAll("\\.", "-");
      if ("$APPDATE[]".equals(dateDef)) {
        d = appdate;
      }
      else {
        d = appdate.substring(0, 4) + "-01-01";
      }
    }
    else {
      d = dateDef.replaceAll("\\.", "-");
    }
    return d;
  }

  private String getTAsql1(String tabid, String unitcode, RecordVo rowVo,
                           RecordVo colVo, int i, int j, String userName, HashMap tableTermsMap) {

    StringBuffer sql = new StringBuffer("");
    try {
      if ("5".equals(colVo.getString("flag1"))) {
        if (!"3".equals(colVo.getString("flag1"))
                && !"4".equals(colVo.getString("flag1"))) {

          String cexpr1 = colVo.getString("flag2");// 1:求和, 2:求均值, 3:求最大值,
          // 4:求最小值, 5:平均人数 6：取值
          String cexpr21 =
                  this.tgridBo.getCexpr2Context(14, colVo.getString("cexpr2"));// 归档指标代号
          String cexpr22 =
                  this.tgridBo.getCexpr2Context(15, colVo.getString("cexpr2"));// 归档类型
          // 1一般
          // 2年报
          // 3半年
          // 4季报
          // 5月报
          // 6周报
          String cexpr23 =
                  this.tgridBo.getCexpr2Context(16, colVo.getString("cexpr2"));// 开始时间
          String cexpr24 =
                  this.tgridBo.getCexpr2Context(17, colVo.getString("cexpr2"));// 结束时间
          String cexpr25 =
                  this.tgridBo.getCexpr2Context(18, colVo.getString("cexpr2"));// 年份
          String yeartemp = "";
          String date = this.appdate;
          String date_year = date.split("-")[0];
          String upyear = Integer.parseInt(date_year) - 1 + "";
          String date_month = date.split("-")[1];
          if ("当前年".equals(cexpr25)) {
            yeartemp = date_year;
          }
          else {
            String[] zxgyear = cexpr25.split("年");
            yeartemp = zxgyear[0];
          }
          if ("1".equals(cexpr1)) // 求和
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select sum(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid = " + yeartemp
                      + " and countid between " + count + " and " + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select sum(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid between "
                      + year + " and " + year2 + " group by row_item) ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-28 start */
              sql
                      .append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              // sql.append("(select SUM(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-28 end */
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select SUM(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select SUM(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
          }
          else if ("2".equals(cexpr1)) // 求平均值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select avg(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid = " + yeartemp
                      + " and countid between " + count + " and " + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select avg(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid between "
                      + year + " and " + year2 + " group by row_item) ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select avg(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select avg(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
          }
          else if ("3".equals(cexpr1)) // 求最大值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select max(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid = " + yeartemp
                      + " and countid between " + count + " and " + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select max(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid between "
                      + year + " and " + year2 + " group by row_item) ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select max(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select max(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
          }
          else if ("4".equals(cexpr1)) // 求最小值
          {
            if ("1".equals(cexpr22)) {
              String count = "";
              String count2 = "";
              if ("上次".equals(cexpr23)) {
                count =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count = cexpr23.split("次")[0];
              }
              if ("上次".equals(cexpr24)) {
                count2 =
                        "(select max(countid) from ta_" + tabid
                                + " where unitcode='" + unitcode + "' and yearid = "
                                + date_year + ")";
              }
              else {
                count2 = cexpr24.split("次")[0];
              }
              sql.append("( select min(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid = " + yeartemp
                      + " and countid between " + count + " and " + count2 + ") ");
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              String year2 = cexpr24.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = upyear;
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              if ("上年".equals(cexpr24)) {
                year2 = upyear;
              }
              else if ("当前年".equals(cexpr24)) {
                year2 = date_year;
              }
              sql.append("( select min(" + rowVo.getString("archive_item")
                      + ") as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid between "
                      + year + " and " + year2 + " group by row_item) ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = year - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              if ("前半年".equals(cexpr24)) {
                if (month2 <= 6) {
                  year2 = year - 1;
                  countid2 = 2;
                }
              }
              else if ("下半年".equals(cexpr24)) {
                countid2 = 2;
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              if ("一季度".equals(cexpr24)) {
                countid2 = 1;
              }
              else if ("二季度".equals(cexpr24)) {
                countid2 = 2;
              }
              else if ("三季度".equals(cexpr24)) {
                countid2 = 3;
              }
              else if ("四季度".equals(cexpr24)) {
                countid2 = 4;
              }
              else if ("上季度".equals(cexpr24)) {
                if (month2 <= 3) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 4;
                }
                else if (month2 > 3 && month2 <= 6) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 1;
                }
                else if (month2 > 6 && month2 <= 9) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 2;
                }
                else if (month2 > 9 && month2 <= 12) {
                  year2 = Integer.parseInt(date_year);
                  countid2 = 3;
                }
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int countid = 1;
              int countid2 = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  year = Integer.parseInt(date_year);
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              if ("上月".equals(cexpr24)) {
                if (month2 == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  countid2 = 12;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  countid2 = month1 - 1;
                }

              }
              else {
                String[] month = cexpr24.split("月");
                countid2 = Integer.parseInt(month[0]);
              }
              int start = year * 100 + countid;
              int end = year2 * 100 + countid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item) ");
              sql
                      .append("(select min(w) as v  from (select (yearid*100 + countid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item) ");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              int year2 = Integer.parseInt(yeartemp);
              int month2 = Integer.parseInt(date_month);
              int weekid2 = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = 12;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              if ("第一周".equals(cexpr24)) {
                weekid2 = 1;
              }
              else if ("第二周".equals(cexpr24)) {
                weekid2 = 2;
              }
              else if ("第三周".equals(cexpr24)) {
                weekid2 = 3;
              }
              else if ("第四周".equals(cexpr24)) {
                weekid2 = 4;
              }
              else if ("上周".equals(cexpr24)) {
                if (month2 == 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year) - 1;
                  month2 = 12;
                  weekid2 = 5;
                }
                else if (month2 > 1 && weekid == 1) {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month) - 1;
                  weekid2 = 5;
                }
                else {
                  year2 = Integer.parseInt(date_year);
                  month2 = Integer.parseInt(date_month);
                  weekid2 = weekid2 - 1;
                }
              }
              int start = year * 1000 + month * 10 + weekid;
              int end = year2 * 1000 + month2 * 10 + weekid2;
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 start */
              // sql.append("(select min(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"+rowVo.getString("archive_item")+" as w,* from ta_"+tabid+") as r where unitcode='"+unitcode+"' and row_item='"+cexpr21+"' and xxxx between "+start+" and "+end+" group by row_item)");
              sql
                      .append("(select min(w) as v  from (select (yearid*1000 + countid*10 + weekid )as xxxx,"
                              + rowVo.getString("archive_item")
                              + " as w,ta_"
                              + tabid
                              + ".* from ta_"
                              + tabid
                              + ") r where unitcode='"
                              + unitcode
                              + "' and row_item='"
                              + cexpr21
                              + "' and xxxx between "
                              + start
                              + " and "
                              + end
                              + " group by row_item)");
              /* 标识：1935 报表取数，定义了归档数据取值方法的，bs取不出来数据 xiaoyun 2014-5-29 end */
            }
          }
          else if ("5".equals(cexpr1)) // 求平均人数
          {
            sql.append("");
          }
          else if ("6".equals(cexpr1)) // 取值
          {
            if ("1".equals(cexpr22)) {
              if ("上次".equals(cexpr23)) {
                sql.append("( select " + rowVo.getString("archive_item")
                        + " as v from ta_" + tabid + " where unitcode='" + unitcode
                        + "' and yearid = " + yeartemp + " and row_item='"
                        + cexpr21 + "' and countid=(select max(countid) from ta_"
                        + tabid + " where unitcode='" + unitcode + "')) ");
              }
              else {
                String count = cexpr23.split("次")[0];
                sql.append("( select " + rowVo.getString("archive_item")
                        + " as v from ta_" + tabid + " where unitcode='" + unitcode
                        + "' and yearid = " + yeartemp + " and row_item='"
                        + cexpr21 + "' and countid='" + count + "') ");
              }
            }
            else if ("2".equals(cexpr22)) {
              String year = cexpr23.split("年")[0];
              if ("上年".equals(cexpr23)) {
                year = Integer.parseInt(date_year) - 1 + "";
              }
              else if ("当前年".equals(cexpr23)) {
                year = date_year;
              }
              sql.append("( select " + rowVo.getString("archive_item")
                      + " as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid=" + year
                      + ") ");
            }
            else if ("3".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int countid = 1;
              if ("前半年".equals(cexpr23)) {
                if (month <= 6) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 2;
                }
              }
              else if ("下半年".equals(cexpr23)) {
                countid = 2;
              }
              sql.append("( select " + rowVo.getString("archive_item")
                      + " as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("4".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int countid = 1;
              if ("一季度".equals(cexpr23)) {
                countid = 1;
              }
              else if ("二季度".equals(cexpr23)) {
                countid = 2;
              }
              else if ("三季度".equals(cexpr23)) {
                countid = 3;
              }
              else if ("四季度".equals(cexpr23)) {
                countid = 4;
              }
              else if ("上季度".equals(cexpr23)) {
                if (month <= 3) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 4;
                }
                else if (month > 3 && month <= 6) {
                  year = Integer.parseInt(date_year);
                  countid = 1;
                }
                else if (month > 6 && month <= 9) {
                  year = Integer.parseInt(date_year);
                  countid = 2;
                }
                else if (month > 9 && month <= 12) {
                  year = Integer.parseInt(date_year);
                  countid = 3;
                }
              }
              sql.append("( select " + rowVo.getString("archive_item")
                      + " as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("5".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month1 = Integer.parseInt(date_month);
              int countid = 1;
              if ("上月".equals(cexpr23)) {
                if (month1 == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  countid = 12;
                }
                else {
                  countid = month1 - 1;
                }

              }
              else {
                String[] month = cexpr23.split("月");
                countid = Integer.parseInt(month[0]);
              }
              sql.append("( select " + rowVo.getString("archive_item")
                      + " as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid='" + year
                      + "' and countid='" + countid + "') ");
            }
            else if ("6".equals(cexpr22)) {
              int year = Integer.parseInt(yeartemp);
              int month = Integer.parseInt(date_month);
              int weekid = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
              if ("第一周".equals(cexpr23)) {
                weekid = 1;
              }
              else if ("第二周".equals(cexpr23)) {
                weekid = 2;
              }
              else if ("第三周".equals(cexpr23)) {
                weekid = 3;
              }
              else if ("第四周".equals(cexpr23)) {
                weekid = 4;
              }
              else if ("上一周".equals(cexpr23)) {
                if (month == 1 && weekid == 1) {
                  year = Integer.parseInt(date_year) - 1;
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else if (month > 1 && weekid == 1) {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month) - 1;
                  weekid = 5;
                }
                else {
                  year = Integer.parseInt(date_year);
                  month = Integer.parseInt(date_month);
                  weekid = weekid - 1;
                }
              }
              sql.append("( select " + rowVo.getString("archive_item")
                      + " as v from ta_" + tabid + " where unitcode='" + unitcode
                      + "' and row_item='" + cexpr21 + "' and yearid='" + year
                      + "' and countid='" + month + "' and weekid = " + weekid
                      + ") ");
            }
            // sql.append("( select "+cexpr21+" as v from ta_"+tabid+" where unitcode='"+unitcode+"' and row_item='"+colVo.getString("archive_item")+"') as a"+j+"");
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return sql.toString();

  }

  /**
   * 根据部门找单位
   */
  private String getUnByUm(String umCode, Connection conn) {
    String un = "##";
    try {
      ContentDAO dao = new ContentDAO(conn);
      RowSet recset = null;
      while (true) {
        String sql =
                "select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"
                        + umCode + "')";
        recset = dao.search(sql);
        if (recset.next()) {
          if ("UN".equalsIgnoreCase(recset.getString("codesetid"))) {
            un = recset.getString("codeitemid");
            break;
          }
          else {
            umCode = recset.getString("codeitemid");
          }
        }

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return un;
  }

  /**
   * 加载本报表取数范围设置
   */
  private void initDataScope() {
    try {
      AnalyseParams analyseParams = new AnalyseParams(this.conn);
      HashMap map = analyseParams.getAttributeValues(this.userName); // 从常量表中取得期统计范围和截止日期
      String databaselist = (String) map.get("databaselist");
      if (databaselist == null || databaselist.trim().length() == 0) {
        databaselist = "Usr,";
      }
      this.dbList = this.getScanRes(databaselist);
      this.appdate = (String) map.get("appdate");
      this.startdate = (String) map.get("startdate");
      this.tgridBo.setStartdate(this.startdate);
      this.result = (String) map.get("result");

      this.dataScopeEnabled = false;
      this.dataScopeNbases = "";
      this.dataScopeStartDate = "";
      this.dataScopeEndDate = "";
      String sql = "select xmlstyle from tname where tabid=" + this.tabid;
      RowSet rowSet = null;
      ContentDAO dao = new ContentDAO(this.conn);
      try {
        rowSet = dao.search(sql);
        if (rowSet.next()) {
          String xmlstyle = Sql_switcher.readMemo(rowSet, "xmlstyle");
          // if(xmlstyle!=null&&xmlstyle.length()>0){
          if (xmlstyle != null && xmlstyle.trim().length() > 0) {// liuy
            // 2015-1-27
            // 6988：自动取数/查阅：对3号表查阅和取数，后台报错
            Document doc =
                    new SAXBuilder().build(new ByteArrayInputStream(xmlstyle
                            .getBytes()));
            Element data_scope =
                    (Element) XPath.newInstance("/param/data_scope")
                            .selectSingleNode(doc);
            if (data_scope != null) {
              this.dataScopeEnabled =
                      "1".equals(data_scope.getAttributeValue("enable"));
              this.dataScopeNbases = data_scope.getAttributeValue("nbase");
              this.dataScopeStartDate =
                      data_scope.getAttributeValue("startdate");
              this.dataScopeEndDate = data_scope.getAttributeValue("enddate");
              if (this.dataScopeEnabled) {
                this.dbList = this.getScanRes(this.dataScopeNbases);
                this.appdate = this.getDateValue(this.dataScopeEndDate);
                this.startdate = this.getDateValue(this.dataScopeStartDate);
                this.tgridBo.setStartdate(this.startdate);
              }
            }
          }
        }
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      finally {
        if (rowSet != null) {
          try {
            rowSet.close();
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 判断当前用户是否负责报表 zhaoxg 2013-2-17
   *
   * @param username
   * @return
   * @throws GeneralException
   * @throws SQLException
   */
  private boolean isApprove1(String username) throws GeneralException,
          SQLException {
    boolean isapprove = false;
    Connection conn = AdminDb.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = null;
    try {

      String sql =
              "select username from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode";
      rs = stmt.executeQuery(sql.toString());
      while (rs.next()) {
        if (username.equals(rs.getString("username"))) {
          isapprove = true;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {

		PubFunc.closeResource(rs);
		PubFunc.closeResource(stmt);
		PubFunc.closeResource(conn);
    }

    return isapprove;
  }

  /**
   * 设置表条件---单位 或 职位
   */
  private void setBorK_terms(HashMap tableTermsMap, HashSet tableTermFactorSet,
                             ArrayList tableTermList) {
    ContentDAO dao = new ContentDAO(this.conn);
    TgridBo gridBo = new TgridBo(this.conn);
    RowSet recset = null;
    try {

      StringBuffer itemid_str = new StringBuffer("");
      boolean isB = false;
      boolean isK = false;
      boolean isR = false;
      // for (Iterator t1 = tableTermFactorSet.iterator(); t1.hasNext();) {
      // itemid_str.append(",'" + ((String) t1.next()).trim() + "'");
      // }
      // recset = dao
      // .search("select fieldsetid from fielditem where itemid in ( "
      // + itemid_str.substring(1) + " )");
      // while (recset.next()) {
      // String tt = recset.getString(1);
      // if (tt.substring(0, 1).equals("A")) {
      // isR = true;
      // break;
      // } else if (tt.substring(0, 1).equals("B"))
      // isB = true;
      // else if (tt.substring(0, 1).equals("K"))
      // isK = true;
      // }
      String[] aa = (String[]) tableTermList.get(0);
      if ("1".equals(aa[0])) {
        isR = true;

      }
      else if ("2".equals(aa[0]) || "3".equals(aa[0]) || "4".equals(aa[0])) {
        isB = true;
      }
      else if ("5".equals(aa[0])) {
        isK = true;
      }

      int flag = 0;
      if (!isR && isK) {
        flag = 3;
      }
      else if (!isR && isB) {
        flag = 2;
      }
      if (flag != 0) {
        StringBuffer a_tableTermsConditionSql = new StringBuffer("");

        boolean isHistory = false;
        for (int a = 0; a < tableTermList.size(); a++) {
          String[] tableTerms = (String[]) tableTermList.get(a);
          if (tableTerms[3].length() > 1 && tableTerms[5].length() > 0
                  && "1".equals(gridBo.getCexpr2Context(2, tableTerms[5]))) {
            isHistory = true;
          }
          else {
            isHistory = false;
          }
        }
        isHistory = false;// 暂时不考虑历史数据 zhaoxg 2013-4-27
        if (isHistory) {
          if (flag == 3) {
            tableTermsMap.put("K_history", tableTermList);
          }
          else if (flag == 2) {
            tableTermsMap.put("B_history", tableTermList);
          }
        }
        else {
          for (int a = 0; a < tableTermList.size(); a++) {
            String[] tableTerms = (String[]) tableTermList.get(a);
            if (tableTerms[3].length() > 1) {
              String strwhere = "";
              FactorList factorlist =
                      new FactorList(tableTerms[4], tableTerms[3], "", true, false,
                              true, flag, this.userview.getUserName(), "t#"
                              + this.userview.getUserName() + "_tjb_B");
              strwhere = factorlist.getSqlExpression();
              a_tableTermsConditionSql.append(" union  select ");
              if (flag == 3) {
                a_tableTermsConditionSql.append("K01.E01A1 " + strwhere);
              }
              else if (flag == 2) {
                a_tableTermsConditionSql.append("B01.B0110 " + strwhere);
              }
            }
          }

          if (a_tableTermsConditionSql.length() > 2) {
            String sql =
                    "select * from (" + a_tableTermsConditionSql.substring(6)
                            + " ) aaa ";
            if (flag == 3) {
              tableTermsMap.put("K", sql);
            }
            else if (flag == 2) {
              tableTermsMap.put("Usr",
                      "select a0100 from  (select * from usra01 ) aaa");
              tableTermsMap.put("B", sql);
            }
          }
        }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
