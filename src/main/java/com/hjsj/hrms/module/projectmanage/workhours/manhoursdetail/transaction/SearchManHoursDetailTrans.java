package com.hjsj.hrms.module.projectmanage.workhours.manhoursdetail.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manhoursdetail.businessobject.ManHoursDetailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>
 * Title: SearchManHoursDetailTrans
 * </p>
 * <p>
 * Description: 项目人员信息
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-12-28 下午1:14:20
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class SearchManHoursDetailTrans extends IBusiness {

  @Override
  public void execute() throws GeneralException {
    try {
      ManHoursDetailBo bo = new ManHoursDetailBo(this.frameconn, this.userView);
      // 获取项目ID
      String P1101 = (String) this.getFormHM().get("projectId");
      if (StringUtils.isNotEmpty(P1101)) {
        P1101 = PubFunc.decrypt(P1101);
      }
      // 获取项目名称
      String title = (String) this.getFormHM().get("title");
      // 排除指标 :
      String exceptFields = ",p1101,p1325,guidkey,a0100,nbase,";
      // 可编辑指标
      String EditFields = ",p1307,p1309,p1311,p1313,p1315,p1317,";
      // 需要增加列宽的指标
      String isAddWidth = ",p1309,";
      // 锁列
      String islock = ",p1303,";

      /** 获取列头 */
      ArrayList columnList =
          bo.getColumnList(exceptFields, EditFields, isAddWidth, islock);

      /** 拼接sql */
      StringBuffer sql =
          new StringBuffer(
              "SELECT P1301,P1303,P1305,A0101,P1307,P1309,P1311,P1313,P1315,P1317,");
      sql.append(Sql_switcher.sqlNull("P1319", "0")).append(" P1319,");
      sql.append(Sql_switcher.sqlNull("P1321", "0")).append(" P1321,");
      sql.append(Sql_switcher.sqlNull("P1323", "0")).append(" P1323");
      sql.append(" FROM P13 ");
      sql.append(" WHERE P1101= " + P1101);
      String orderBy = " order by P1311,P1301";

      /** 获取操作按钮 */
      ArrayList buttonList = bo.getButtonList();

      /** 加载表格 */
      TableConfigBuilder builder =
          new TableConfigBuilder("manhoursdetail_id_001", columnList,
              "manhoursdetail", this.userView, this.getFrameconn());
      builder.setLockable(true);
      builder.setDataSql(sql.toString());
      builder.setOrderBy(orderBy);
      builder.setAutoRender(false);
      builder.setTitle(title + "项目-成员工时汇总");
      builder.setSetScheme(true);
      
      if (this.userView.hasTheFunction("3900202")) {
        builder.setEditable(true);
      }
      else {
        builder.setEditable(false);
      }
      builder.setPageSize(20);
      builder.setTableTools(buttonList);
      builder.setSelectable(true);
      builder.setSchemeSaveCallback("manhoursdetail_me.schemeSaveCallback");
      if (this.userView.hasTheFunction("3900200")) {
        builder.setScheme(true);
        if (this.userView.hasTheFunction("390020001")) {
          builder.setShowPublicPlan(true);
        }
        else {
          builder.setShowPublicPlan(false);
        }
      }
      builder.setConstantName("projectmanage/manhoursdetail");
      String config = builder.createExtTableConfig();
      this.getFormHM().put("tableConfig", config.toString());

    }
    catch (Exception e) {
      e.printStackTrace();
      throw GeneralExceptionHandler.Handle(e);
    }
  }

}
