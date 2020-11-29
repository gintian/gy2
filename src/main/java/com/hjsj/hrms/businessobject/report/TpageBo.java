package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class TpageBo {
  private Connection conn = null;

  private int minTop_px = 0; // 最上端标题所在的顶端位置

  private UserView userview = null;

  public TpageBo(Connection conn) {
    this.conn = conn;
  }

  public TpageBo(Connection conn, UserView userview) {
    this.conn = conn;
    this.userview = userview;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  public int getMinTop_px() {
    return this.minTop_px;
  }

  /**
   * 得到表的标题信息集合
   * 
   * @param tabid
   * @return
   */
  public ArrayList getPageList(String tabid) {
    ArrayList list = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    int i = 0;
    try {
      recset = dao.search("select * from tpage where tabid=" + tabid);
      while (recset.next()) {
        if (i == 0) {
          this.minTop_px = recset.getInt("rtop");
        }
        else {
          if (recset.getInt("rtop") < this.minTop_px) {
            this.minTop_px = recset.getInt("rtop");
          }
        }

        InputStream ism = null;
        try {
          RecordVo vo = new RecordVo("TPage");
          vo.setInt("tabid", recset.getInt("tabid"));
          vo.setInt("gridno", recset.getInt("gridno"));
          vo.setString("hz", recset.getString("hz"));
          vo.setInt("rleft", recset.getInt("rleft"));
          vo.setInt("rtop", recset.getInt("rtop"));
          vo.setInt("rwidth", recset.getInt("rwidth"));
          vo.setInt("rheight", recset.getInt("rheight"));
          vo.setInt("fontsize", recset.getInt("fontsize"));
          vo.setString("fontname", recset.getString("fontname"));
          vo.setInt("fonteffect", recset.getInt("fonteffect"));
          vo.setInt("flag", recset.getInt("flag"));
          vo.setString("extendattr", Sql_switcher
              .readMemo(recset, "extendattr"));// 配合照片时用到
          ism = recset.getBinaryStream("content");
          vo.setObject("content", ism);// 照片
          list.add(vo);
          i++;
        }
        finally {
          PubFunc.closeIoResource(ism);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  // 自动取默认值

  // 返回表题和参数集合
  public ArrayList getPageListAndTparam(String tabid, int sortID,
      String userName) {
    ArrayList list = new ArrayList();
    ArrayList pageList = new ArrayList();
    HashMap paramMap = new HashMap();
    ArrayList paramenameList = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    RowSet tpsRowSet = null;
    RowSet tppRowSet = null;
    RowSet tptRowSet = null;
    RowSet codeSet = null;
    SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");
    int i = 0;
    try {
      if (tabid == null || tabid.trim().length() <= 0) {
        return list;
      }
      recset =
          dao.search("select * from tpage where tabid=" + tabid
              + " order by rtop");
      StringBuffer sql_whl = new StringBuffer("");
      while (recset.next()) {
        if (i == 0) {
          this.minTop_px = recset.getInt("rtop");
        }
        else {
          if (recset.getInt("rtop") < this.minTop_px) {
            this.minTop_px = recset.getInt("rtop");
          }
        }
        RecordVo vo = new RecordVo("TPage");
        vo.setInt("tabid", recset.getInt("tabid"));
        vo.setInt("gridno", recset.getInt("gridno"));
        String hz = recset.getString("hz");
        vo.setString("hz", hz);
        vo.setInt("rleft", recset.getInt("rleft"));
        vo.setInt("rtop", recset.getInt("rtop"));
        vo.setInt("rwidth", recset.getInt("rwidth"));
        vo.setInt("rheight", recset.getInt("rheight"));
        vo.setInt("fontsize", recset.getInt("fontsize"));
        vo.setString("fontname", recset.getString("fontname"));
        vo.setInt("fonteffect", recset.getInt("fonteffect"));
        vo.setInt("flag", recset.getInt("flag"));
        vo.setString("extendattr", Sql_switcher.readMemo(recset, "extendattr"));// 配合照片时用到
        vo.setObject("content", recset.getBinaryStream("content"));// 照片
        pageList.add(vo);
        if (recset.getInt("flag") == 9) {

          sql_whl.append(" or paramname='" + recset.getString("hz") + "'");
        }
        i++;
      }
      String sql = "";
      if (sql_whl.toString().length() > 0) {
        sql = "select * from tparam where " + sql_whl.substring(3);
        int tps = 0;
        int tpt = 0;
        int tpp = 0;
        Table tps_table = new Table("tp_s" + sortID);
        Table tpt_table = new Table("tp_t" + tabid);
        DbWizard dbWizard = new DbWizard(this.conn);
        if (dbWizard.isExistTable(tps_table.getName(), false)) {
          tpsRowSet =
              dao.search("select * from tp_s" + sortID + " where unitcode='"
                  + userName + "' ");
        }
        if (dbWizard.isExistTable(tpt_table.getName(), false)) {
          tptRowSet =
              dao.search("select * from tp_t" + tabid + " where unitcode='"
                  + userName + "' ");
        }
        tppRowSet =
            dao.search("select * from tp_p where unitcode='" + userName + "' ");
        if (tpsRowSet != null && tpsRowSet.next()) {
          tps = 1;
        }
        if (tptRowSet != null && tptRowSet.next()) {
          tpt = 1;
        }
        if (tppRowSet.next()) {
          tpp = 1;
        }
        recset = dao.search(sql);
        while (recset.next()) {
          HashMap map = new HashMap();
          String type = recset.getString("paramtype");
          map.put("paramname", recset.getString("paramname"));
          map.put("paramename", recset.getString("paramename"));
          map.put("paramtype", recset.getString("paramtype"));
          map.put("paramlen", recset.getString("paramlen"));
          map.put("paramfmt", recset.getString("paramfmt"));
          map.put("paramscope", recset.getString("paramscope"));
          map.put("paramcode", recset.getString("paramcode"));
          map.put("paramnull", recset.getString("paramnull"));
          map.put("a_value", "");

          String values = "";
          if (recset.getInt("paramscope") == 0) // 全局参数
          {
            if (tpp == 1
                && dbWizard.isExistField("tp_p", ""
                    + recset.getString("paramename"), false)) {
              if (type.equals(ResourceFactory.getProperty("report.parse.d"))
                  && tppRowSet.getDate(recset.getString("paramename")) != null) {
                values =
                    fm
                        .format(tppRowSet.getDate(recset
                            .getString("paramename")));
              }
              else if (type.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tppRowSet, recset
                        .getString("paramename"));
              }
              else if (tppRowSet.getString(recset.getString("paramename")) != null) {
                if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("kq.formula.character"))
                    || "数值".equals(recset.getString("paramtype"))) {
                  values = tppRowSet.getString(recset.getString("paramename"));
                  if (recset.getString("paramtype").equals(
                      ResourceFactory.getProperty("kq.formula.character"))
                      && recset.getString("paramcon") != null
                      && recset.getString("paramcon").trim().startsWith("$")
                      && "".equals(values)) {
                    values =
                        this.getReportConstantParam(recset
                            .getString("paramcon").trim());
                  }
                }
                else if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("orglist.reportunitlist.code"))) {
                  codeSet =
                      dao
                          .search("select codeitemdesc from codeitem where codesetid='"
                              + recset.getString("paramcode")
                              + "'  and codeitemid='"
                              + tppRowSet.getString(recset
                                  .getString("paramename")) + "'");
                  if (codeSet.next()) {
                    values =
                        tppRowSet.getString(recset.getString("paramename"))
                            + "/" + codeSet.getString(1);
                  }
                }
              }
            }
            else {
              // if(recset.getString("paramtype").equals(ResourceFactory.getProperty("kq.formula.character"))&&recset.getString("paramcon")!=null&&recset.getString("paramcon").trim().startsWith("$")&&values.equals("")){
              // liuy 2014-10-21 4047 首页：统计报表88，超过一页纸张大小时，后台报空指针异常，前台展现也不对。
              // start
              if (recset.getString("paramcon") != null) {
                if (ResourceFactory.getProperty("kq.formula.character").equals(
                    recset.getString("paramtype"))
                    && recset.getString("paramcon").trim().startsWith("$")
                    && "".equals(values)) {
                  values =
                      this.getReportConstantParam(recset.getString("paramcon")
                          .trim());
                }
              }
              // liuy end
            }
          }
          else if (recset.getInt("paramscope") == 1) // 表类参数
          {
            if (tps == 1
                && dbWizard.isExistField(tps_table.getName(), ""
                    + recset.getString("paramename"), false)) {
              if (type.equals(ResourceFactory.getProperty("report.parse.d"))
                  && tpsRowSet.getDate(recset.getString("paramename")) != null) {
                values =
                    fm
                        .format(tpsRowSet.getDate(recset
                            .getString("paramename")));
              }
              else if (type.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tpsRowSet, recset
                        .getString("paramename"));
              }
              else if (tpsRowSet.getString(recset.getString("paramename")) != null) {
                if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("kq.formula.character"))
                    || "数值".equals(recset.getString("paramtype"))) {
                  values = tpsRowSet.getString(recset.getString("paramename"));
                  if (recset.getString("paramtype").equals(
                      ResourceFactory.getProperty("kq.formula.character"))
                      && recset.getString("paramcon") != null
                      && recset.getString("paramcon").trim().startsWith("$")
                      && "".equals(values)) {
                    values =
                        this.getReportConstantParam(recset
                            .getString("paramcon").trim());
                  }
                }
                else if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("orglist.reportunitlist.code"))) {
                  codeSet =
                      dao
                          .search("select codeitemdesc from codeitem where codesetid='"
                              + recset.getString("paramcode")
                              + "'  and codeitemid='"
                              + tpsRowSet.getString(recset
                                  .getString("paramename")) + "'");
                  if (codeSet.next()) {
                    values =
                        tpsRowSet.getString(recset.getString("paramename"))
                            + "/" + codeSet.getString(1);
                  }
                }
              }
            }
            else {
              if (recset.getString("paramtype").equals(
                  ResourceFactory.getProperty("kq.formula.character"))
                  && recset.getString("paramcon") != null
                  && recset.getString("paramcon").trim().startsWith("$")
                  && "".equals(values)) {
                values =
                    this.getReportConstantParam(recset.getString("paramcon")
                        .trim());
              }
            }
          }
          else if (recset.getInt("paramscope") == 2) // 表参数
          {
            if (tpt == 1
                && dbWizard.isExistField(tpt_table.getName(), ""
                    + recset.getString("paramename"), false)) {
              if (type.equals(ResourceFactory.getProperty("report.parse.d"))
                  && tptRowSet.getDate(recset.getString("paramename")) != null) {
                values =
                    fm
                        .format(tptRowSet.getDate(recset
                            .getString("paramename")));
              }
              else if (type.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tptRowSet, recset
                        .getString("paramename"));
              }
              else if (tptRowSet.getString(recset.getString("paramename")) != null) {
                if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("kq.formula.character"))
                    || "数值".equals(recset.getString("paramtype"))) {
                  values = tptRowSet.getString(recset.getString("paramename"));
                  if (recset.getString("paramtype").equals(
                      ResourceFactory.getProperty("kq.formula.character"))
                      && recset.getString("paramcon") != null
                      && recset.getString("paramcon").trim().startsWith("$")
                      && "".equals(values)) {
                    values =
                        this.getReportConstantParam(recset
                            .getString("paramcon").trim());
                  }
                }
                else if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("orglist.reportunitlist.code"))) {
                  codeSet =
                      dao
                          .search("select codeitemdesc from codeitem where codesetid='"
                              + recset.getString("paramcode")
                              + "'  and codeitemid='"
                              + tptRowSet.getString(recset
                                  .getString("paramename")) + "'");
                  if (codeSet.next()) {
                    values =
                        tptRowSet.getString(recset.getString("paramename"))
                            + "/" + codeSet.getString(1);
                  }
                }
              }
            }
            else {
              if (recset.getString("paramtype").equals(
                  ResourceFactory.getProperty("kq.formula.character"))
                  && recset.getString("paramcon") != null
                  && recset.getString("paramcon").trim().startsWith("$")
                  && "".equals(values)) {
                values =
                    this.getReportConstantParam(recset.getString("paramcon")
                        .trim());
              }
            }
          }
          map.put("a_value", values);

          paramMap.put(recset.getString("paramname"), map);
          paramenameList.add(recset.getString("paramename") + "#"
              + recset.getString("paramtype") + "#"
              + recset.getString("paramnull") + "#"
              + recset.getInt("paramscope") + "#"
              + recset.getString("paramlen"));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    list.add(pageList);
    list.add(paramMap);
    list.add(paramenameList);
    return list;
  }

  // 返回表题和参数集合(上报表)
  public ArrayList getPageListAndTparam2(String tabid, int sortID,
      String unitcode) {
    ArrayList list = new ArrayList();
    ArrayList pageList = new ArrayList();
    HashMap paramMap = new HashMap();
    ArrayList paramenameList = new ArrayList();
    ContentDAO dao = new ContentDAO(this.conn);
    RowSet recset = null;
    RowSet tpsRowSet = null;
    RowSet tppRowSet = null;
    RowSet tptRowSet = null;
    RowSet codeSet = null;
    int i = 0;
    try {
      recset =
          dao.search("select * from tpage where tabid=" + tabid
              + " order by rtop");
      StringBuffer sql_whl = new StringBuffer("");
      while (recset.next()) {
        if (i == 0) {
          this.minTop_px = recset.getInt("rtop");
        }
        else {
          if (recset.getInt("rtop") < this.minTop_px) {
            this.minTop_px = recset.getInt("rtop");
          }
        }
        RecordVo vo = new RecordVo("TPage");
        vo.setInt("tabid", recset.getInt("tabid"));
        vo.setInt("gridno", recset.getInt("gridno"));
        String hz = recset.getString("hz");
        if (recset.getInt("flag") == 0) {
          if (hz != null) {
            // hz=hz.replaceAll(" ","&nbsp;");//dml 2011-03-21 jiejue bug
            // 0025655
          }

        }

        vo.setString("hz", hz);
        vo.setInt("rleft", recset.getInt("rleft"));
        vo.setInt("rtop", recset.getInt("rtop"));
        vo.setInt("rwidth", recset.getInt("rwidth"));
        vo.setInt("rheight", recset.getInt("rheight"));
        vo.setInt("fontsize", recset.getInt("fontsize"));
        vo.setString("fontname", recset.getString("fontname"));
        vo.setInt("fonteffect", recset.getInt("fonteffect"));
        vo.setInt("flag", recset.getInt("flag"));
        vo.setString("extendattr", Sql_switcher.readMemo(recset, "extendattr"));// 配合照片时用到
        InputStream content = null;
        try {
          content = recset.getBinaryStream("content");
          vo.setObject("content", content);// 照片
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        finally {
          PubFunc.closeIoResource(content);// 关闭流 guodd 2014-12-29
        }
        pageList.add(vo);
        if (recset.getInt("flag") == 9) {

          sql_whl.append(" or paramname='" + recset.getString("hz") + "'");
        }
        i++;
      }
      String sql = "";
      if (sql_whl.toString().length() > 0) {
        sql = "select * from tparam where " + sql_whl.substring(3);
        int tps = 0;
        int tpt = 0;
        int tpp = 0;
        Table tts_table = new Table("tt_s" + sortID);
        Table ttt_table = new Table("tt_t" + tabid);
        Table ttp_table = new Table("tt_p");
        DbWizard dbWizard = new DbWizard(this.conn);
        if (dbWizard.isExistTable(tts_table.getName(), false)) {
          tpsRowSet =
              dao.search("select * from tt_s" + sortID + " where unitcode='"
                  + unitcode + "' ");
        }
        if (dbWizard.isExistTable(ttt_table.getName(), false)) {
          tptRowSet =
              dao.search("select * from tt_t" + tabid + " where unitcode='"
                  + unitcode + "' ");
        }
        if (dbWizard.isExistTable(ttp_table.getName(), false)) {
          tppRowSet =
              dao.search("select * from tt_p where unitcode='" + unitcode
                  + "' ");
        }
        if (tpsRowSet != null && tpsRowSet.next()) {
          tps = 1;
        }
        if (tptRowSet != null && tptRowSet.next()) {
          tpt = 1;
        }
        if (tppRowSet != null && tppRowSet.next()) {
          tpp = 1;
        }
        recset = dao.search(sql);
        while (recset.next()) {
          HashMap map = new HashMap();
          map.put("paramname", recset.getString("paramname"));
          map.put("paramename", recset.getString("paramename"));
          map.put("paramtype", recset.getString("paramtype"));
          map.put("paramlen", recset.getString("paramlen"));
          map.put("paramfmt", recset.getString("paramfmt"));
          map.put("paramscope", recset.getString("paramscope"));
          map.put("paramcode", recset.getString("paramcode"));
          map.put("paramnull", recset.getString("paramnull"));
          map.put("a_value", "");
          String values = "";
          if (recset.getInt("paramscope") == 0) // 全局参数
          {
            // wangcq 2014-12-03 begin 获取全局、表类、表参数时先判断类型，再取数
            String paramename = recset.getString("paramename");
            String paramtype = recset.getString("paramtype");
            String tppValue = null;
            if (tpp == 1) {
              if ("日期".equals(paramtype)
                  && tppRowSet.getDate(paramename) != null) {
                tppValue = tppRowSet.getDate(paramename).toString();
                // else if("数值".equals(paramtype))
                // tppValue = String.valueOf(tppRowSet.getInt(paramename)); //
                // 带小数会报错
              }
              else {
                tppValue = tppRowSet.getString(paramename);
              }
            }
            if (tpp == 1 && tppValue != null) {
              if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.d"))) {
                if (tppValue != null) {
                  values = tppValue.substring(0, 10);
                }
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tppRowSet, recset
                        .getString("paramename"));
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  || "数值".equals(paramtype)) {
                if (tppValue != null) {
                  values = tppValue;
                }
                if (paramtype.equals(ResourceFactory
                    .getProperty("kq.formula.character"))
                    && recset.getString("paramcon") != null
                    && recset.getString("paramcon").trim().startsWith("$")
                    && "".equals(values)) {
                  values =
                      this.getReportConstantParam(recset.getString("paramcon")
                          .trim());
                }

              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("orglist.reportunitlist.code"))) {
                if (tppValue != null) {
                  codeSet =
                      dao
                          .search("select codeitemdesc from codeitem where codesetid='"
                              + recset.getString("paramcode")
                              + "'  and codeitemid='"
                              + tppRowSet.getString(recset
                                  .getString("paramename")) + "'");
                  if (codeSet.next()) {
                    values = tppValue + "/" + codeSet.getString(1);
                  }
                }
              }
            }
            else {
              if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  && recset.getString("paramcon") != null
                  && recset.getString("paramcon").trim().startsWith("$")
                  && "".equals(values)) {
                values =
                    this.getReportConstantParam(recset.getString("paramcon")
                        .trim());
              }
            }
          }
          else if (recset.getInt("paramscope") == 1) // 表类参数
          {
            String paramename = recset.getString("paramename");
            String paramtype = recset.getString("paramtype");
            String tpsValue = null;
            if (tps == 1) {
              if ("日期".equals(paramtype)
                  && tpsRowSet.getDate(paramename) != null) {
                tpsValue = tpsRowSet.getDate(paramename).toString();
                // else if("数值".equals(paramtype))
                // tpsValue = String.valueOf(tpsRowSet.getInt(paramename));
              }
              else {
                tpsValue = tpsRowSet.getString(paramename);
              }
            }
            if (tps == 1 && tpsValue != null) {
              if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.d"))) {
                values = tpsValue.substring(0, 10);
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tpsRowSet, recset
                        .getString("paramename"));
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  || "数值".equals(paramtype)) {
                if (tpsValue != null) {
                  values = tpsValue;
                }
                if (recset.getString("paramtype").equals(
                    ResourceFactory.getProperty("kq.formula.character"))
                    && recset.getString("paramcon") != null
                    && recset.getString("paramcon").trim().startsWith("$")
                    && "".equals(values)) {
                  values =
                      this.getReportConstantParam(recset.getString("paramcon")
                          .trim());
                }
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("orglist.reportunitlist.code"))) {
                codeSet =
                    dao
                        .search("select codeitemdesc from codeitem where codesetid='"
                            + recset.getString("paramcode")
                            + "'  and codeitemid='"
                            + tpsRowSet.getString(recset
                                .getString("paramename")) + "'");
                if (codeSet.next()) {
                  values = tpsValue + "/" + codeSet.getString(1);
                }
              }
            }
            else {
              if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  && recset.getString("paramcon") != null
                  && recset.getString("paramcon").trim().startsWith("$")
                  && "".equals(values)) {
                values =
                    this.getReportConstantParam(recset.getString("paramcon")
                        .trim());
              }
            }
          }
          else if (recset.getInt("paramscope") == 2) // 表参数
          {
            String paramename = recset.getString("paramename");
            String paramtype = recset.getString("paramtype");
            String tptValue = null;
            if (tpt == 1) {
              if ("日期".equals(paramtype)
                  && tptRowSet.getDate(paramename) != null) {
                tptValue = tptRowSet.getDate(paramename).toString();
                // else if("数值".equals(paramtype))
                // tptValue = String.valueOf(tptRowSet.getInt(paramename));
              }
              else {
                tptValue = tptRowSet.getString(paramename);
              }
            }
            if (tpt == 1 && tptValue != null) {
              if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.d"))) {
                if (tptValue != null) {
                  values = tptValue.substring(0, 10);
                }
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("report.parse.text"))) {
                values =
                    Sql_switcher.readMemo(tptRowSet, recset
                        .getString("paramename"));
              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  || "数值".equals(paramtype)) {
                values = tptValue;
                if (paramtype.equals(ResourceFactory
                    .getProperty("kq.formula.character"))
                    && recset.getString("paramcon") != null
                    && recset.getString("paramcon").trim().startsWith("$")
                    && "".equals(values)) {
                  values =
                      this.getReportConstantParam(recset.getString("paramcon")
                          .trim());
                }

              }
              else if (paramtype.equals(ResourceFactory
                  .getProperty("orglist.reportunitlist.code"))) {
                codeSet =
                    dao
                        .search("select codeitemdesc from codeitem where codesetid='"
                            + recset.getString("paramcode")
                            + "'  and codeitemid='"
                            + tptRowSet.getString(recset
                                .getString("paramename")) + "'");
                if (codeSet.next()) {
                  values = tptValue + "/" + codeSet.getString(1);
                }
              }
            }
            else {
              if (paramtype.equals(ResourceFactory
                  .getProperty("kq.formula.character"))
                  && recset.getString("paramcon") != null
                  && recset.getString("paramcon").trim().startsWith("$")
                  && "".equals(values)) {
                values =
                    this.getReportConstantParam(recset.getString("paramcon")
                        .trim());
              }
            }
            // wangcq 2014-12-03 end
          }
          map.put("a_value", values);
          // System.out.println(map);

          paramMap.put(recset.getString("paramname"), map);
          paramenameList.add(recset.getString("paramename") + "#"
              + recset.getString("paramtype") + "#"
              + recset.getString("paramnull") + "#"
              + recset.getInt("paramscope") + "#"
              + recset.getString("paramlen"));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      PubFunc.closeIoResource(recset);
      PubFunc.closeIoResource(tpsRowSet);
      PubFunc.closeIoResource(tppRowSet);
      PubFunc.closeIoResource(tptRowSet);
      PubFunc.closeIoResource(codeSet);
    }

    list.add(pageList);
    list.add(paramMap);
    list.add(paramenameList);
    return list;
  }

  public String getReportConstantParam(String param) {
    String value = "";
    ConstantXml xml = new ConstantXml(this.conn, "RP_PARAM", "param");
    // xml.saveStrValue();
    // String appdate =
    // xml.getNodeAttributeValue("/param/user/database","appdate");//此方式只能取到第一个用户的默认值
    // 25571 changxy 取当前操作人对应的截止日期

    String appdate = "";
    if (this.userview != null) {
      appdate =
          xml.getNodeByUserAttributeValue("/param/user/database", "appdate",
              "id", this.userview);
    }
    else {
      appdate = xml.getNodeAttributeValue("/param/user/database", "appdate");// 此方式只能取到第一个用户的默认值
    }

    if (appdate != null
        && (appdate.indexOf("-") != -1 || appdate.indexOf(".") != -1)) {
      String adate[] = null;
      if (appdate.indexOf("-") != -1) {
        adate = appdate.split("-");
      }
      else if (appdate.indexOf(".") != -1) {
        adate = appdate.split(".");
      }
      if ("$THISMONTH[]".equalsIgnoreCase(param)) {
        value = adate[1];
      }
      else if ("$THISYS[]".equalsIgnoreCase(param)) {
        value = adate[0];
      }
      else if ("$THISQTR[]".equalsIgnoreCase(param)) {
        value = (Integer.parseInt(adate[1]) - 1) / 3 + 1 + "";
      }
      else if ("$APPDATE[]".equalsIgnoreCase(param)) {
        value = appdate;
      }
    }
    return value;
  }

  public void setMinTop_px(int minTop_px) {
    this.minTop_px = minTop_px;
  }

}
