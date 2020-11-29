package com.hjsj.hrms.module.projectmanage.workhours.manhourssum.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manhourssum.businessobject.ManHoursSumBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 项目管理成员工时明细
 * <p>
 * Title: SearchManHoursSumTrans
 * </p>
 * <p>
 * Description: 获取表格
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-12-28 下午1:45:59
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class SearchManHoursSumTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ManHoursSumBo bo = new ManHoursSumBo(this.frameconn, this.userView);
            // 获取项目ID
            String projectId = (String) this.getFormHM().get("projectId");
            if (StringUtils.isNotEmpty(projectId))
                projectId = PubFunc.decrypt(projectId);

            // 获取项目名称
            String title = (String) this.getFormHM().get("title");
            // 传入页面 1-项目管理 2-工时汇总
            String type = (String) this.getFormHM().get("type");

            String dateRange = (String) this.getFormHM().get("dateRange");
            // 成员Id
            String manDetailId = "";
            // 里程碑Id
            String milestone = "";
            // type =1 -从主表穿透
            // type =2 -从明细表穿透
            if ("2".equals(type)) {
                manDetailId = (String) this.getFormHM().get("manDetailId");
                if (StringUtils.isNotEmpty(manDetailId))
                    manDetailId = PubFunc.decrypt(manDetailId);

            } else if ("1".equals(type)) {
                milestone = (String) this.getFormHM().get("milestone");
                if (StringUtils.isNotEmpty(milestone))
                    milestone = PubFunc.decrypt(milestone);

            }

            // 排除指标 :
            String exceptFields = ",p1101,p1521,";
            // 可编辑指标
            String EditFields = ",,";
            // 需要增加列宽的指标
            String isAddWidth = ",p1507,p1509,";
            // 锁列
            String islock = ",,";

            /** 获取列头 */
            ArrayList columnList = bo.getColumnList(exceptFields, EditFields, isAddWidth, islock);

            /** 拼接sql */
            StringBuffer sql = new StringBuffer("SELECT P13.P1303,P13.P1305,P13.A0101,");
            sql.append(" (case when P1519 = '0' then '提交' ");
            sql.append(" when P1519 = '1' then '批准'  ");
            sql.append(" when P1519 = '2' then '退回' end ) AS workHours, ");
            sql.append(bo.getP15FieldSql());
            sql.append(" ,(SELECT P1311 FROM P13 ");
            sql.append(" WHERE A0100 ='" + this.userView.getA0100() + "'");
            sql.append(" AND NBASE ='" + this.userView.getDbname() + "'");
            sql.append(" AND p1101= " + projectId);
            sql.append(")AS p1311 ");
            sql.append(",( CASE WHEN P15.P1201 ='' OR P15.P1201 IS NULL THEN ''");
            sql.append(" ELSE(SELECT P1203 FROM P12 WHERE P12.P1201=P15.P1201) END )AS P1203");
            sql.append(" FROM P15,P13 ");
            sql.append(" where p13.P1301 = p15.P1301 ");
            sql.append(" and p15.P1101 = " + projectId);
            if (StringUtils.isNotEmpty(manDetailId))
                sql.append(" and p15.P1301 = " + manDetailId);

            if (StringUtils.isNotEmpty(milestone))
                sql.append(" and p15.P1201 = " + milestone);

            sql.append(bo.getDateRange(dateRange));

            String orderBy = " order by P1301";

            /** 获取操作按钮 */
            ArrayList buttonList = bo.getButtonList(type);

            /** 加载表格 */
            TableConfigBuilder builder = new TableConfigBuilder("manhoursSum_id_001", columnList, "manhoursSum",
                    this.userView, this.getFrameconn());
            builder.setLockable(true);
            builder.setDataSql(sql.toString());
            builder.setOrderBy(orderBy);
            builder.setAutoRender(false);
            builder.setTitle(title + "项目-成员工时明细表");
            builder.setSetScheme(true);
            builder.setPageSize(20);
            builder.setTableTools(buttonList);
            builder.setSelectable(true);
            if (this.userView.hasTheFunction("3900300")) {
                builder.setScheme(true);
                if (this.userView.hasTheFunction("390030001"))
                    builder.setShowPublicPlan(true);
                else
                    builder.setShowPublicPlan(false);

            }

            builder.setConstantName("projectmanage/manhourssum");
            builder.setSchemeSaveCallback("ManHoursSum_me.schemeSaveCallback");
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
