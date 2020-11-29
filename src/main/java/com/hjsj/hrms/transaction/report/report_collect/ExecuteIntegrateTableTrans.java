package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableHtmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:根据条件生成综合表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 26, 2006:5:55:54 PM
 * </p>
 * 
 * @author dengcan
 * @version 1.0
 */
public class ExecuteIntegrateTableTrans extends IBusiness {

  @Override
  public void execute() throws GeneralException {
    // try
    // {
    ContentDAO dao = new ContentDAO(this.getFrameconn());
    RowSet recset = null;

    String[] right_fields = (String[]) this.getFormHM().get("right_fields"); // 生成综合表的条件
    if (right_fields != null) {
      for (int i = 0; i < right_fields.length; i++) {
        right_fields[i] = PubFunc.keyWord_reback(right_fields[i]);
      }
    }
    String nums = (String) this.getFormHM().get("nums"); // aXX:列选择 bXX：行选择
    // nums="a2";
    String tabid = (String) this.getFormHM().get("tabid");
    String unitcode = (String) this.getFormHM().get("unitcode");
    String cols = (String) this.getFormHM().get("cols");
    String sortid = "0"; // 表类别id
    String tname = ""; // 表名称
    try {
      recset =
          dao.search("select tsortid,name from tname where tabid=" + tabid);

      if (recset.next()) {
        sortid = recset.getString("tsortid");
        tname = recset.getString("name");
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    TnameBo tnameBo =
        new TnameBo(this.getFrameconn(), tabid, this.getUserView().getUserId(),
            this.getUserView().getUserName(), " ");
    // 判断是否有编号和甲
    ArrayList collist = tnameBo.getColInfoBGrid();
    boolean colflag = false;
    for (Iterator t = collist.iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      if ("4".equals(vo.getString("flag1"))) {
        colflag = true;
      }

    }
    ArrayList rowlist = tnameBo.getRowInfoBGrid();
    boolean rowflag = false;
    for (Iterator t = rowlist.iterator(); t.hasNext();) {
      RecordVo vo = (RecordVo) t.next();
      if ("4".equals(vo.getString("flag1"))) {
        rowflag = true;
      }

    }
    String num2s = "";
    if (nums.indexOf(",a") != -1) // 选中列
    {
      if (!rowflag) {

        String temp[] = nums.split(",");
        for (int i = 0; i < temp.length; i++) {
          if (temp[i].indexOf("a") != -1) {
            int j = Integer.parseInt(temp[i].substring(1));
            j++;
            num2s += "a" + j + ",";
          }
          else {
            num2s += temp[i] + ",";
          }
        }
      }
      else {
        num2s = nums;
      }
    }
    else if (nums.indexOf(",b") != -1) // 选中行
    {

      if (!colflag) {

        String temp[] = nums.split(",");
        for (int i = 0; i < temp.length; i++) {
          if (temp[i].indexOf("b") != -1) {
            int j = Integer.parseInt(temp[i].substring(1));
            j++;
            num2s += "b" + j + ",";
          }
          else {
            num2s += temp[i] + ",";
          }
        }
      }
      else {
        num2s = nums;
      }

    }

    IntegrateTableBo integrateTableBo =
        new IntegrateTableBo(this.getFrameconn());
    ArrayList resultList =
        integrateTableBo.getIntegrateTableData(right_fields, tabid, sortid,
            nums, unitcode, Integer.parseInt(cols));

    IntegrateTableHtmlBo htmlBo = new IntegrateTableHtmlBo();
    
    	
    String html =
        htmlBo
            .creatHtmlView(tnameBo, 40, resultList, nums, right_fields, tname);
    StringBuffer integrateValues = new StringBuffer("");
    for (Iterator t = resultList.iterator(); t.hasNext();) {
      String[] temp = (String[]) t.next();
      integrateValues.append("`");
      StringBuffer value = new StringBuffer("");
      for (int i = 0; i < temp.length; i++) {
        if (temp[i] == null && !num2s.equals(nums)) {

        }
        else {
          value.append(":" + temp[i]);
        }
      }
      integrateValues.append(value.substring(1));
    }
    if (integrateValues.length() > 0) {
      this.getFormHM().put("integrateValues", integrateValues.substring(1));
    }
    else {
      this.getFormHM().put("integrateValues", "");
    }

    this.getFormHM().put("num2s", num2s);

    this.getFormHM().put("html", html);
    this.getFormHM().put("rowSerialNo", tnameBo.getRowSerialNo());
    this.getFormHM().put("colSerialNo", tnameBo.getColSerialNo());
    // }
    // catch(Exception e)
    // {
    // e.printStackTrace();
    // throw new GeneralException(""+e.getStackTrace().toString());
    // }

  }

}
