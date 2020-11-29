package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 假期管理请假明细数据
 * @Title:        ShowLeaveDetailedTrans.java
 * @Description:  展示假期管理请假明细调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:17:32
 * @author        chenxg
 * @version       1.0
 */
public class ShowLeaveDetailedTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        try {
        	// 34146 linbz 增加primarykey_e为usr|00000009| 库和编号的隐藏列参数，防止栏目设置不显示库标识取不到
            String primaryKey = (String) this.getFormHM().get("primaryKey");
            primaryKey = PubFunc.decrypt(primaryKey);
            if(primaryKey.indexOf("|") == -1)
            	return;
            
            String nbase = primaryKey.split("\\|")[0];
            String a0100 = primaryKey.split("\\|")[1];
            String holidayType = (String) this.getFormHM().get("holidayType");
            holidayType = PubFunc.decrypt(holidayType);
            
            // 34362 开始结束时间改为由年来拼接
            String holidayYear = (String) this.getFormHM().get("holidayYear");
            
            StringBuffer sql = new StringBuffer();
            KqVer kqVer = new KqVer();
            if (kqVer.getVersion() == KqConstant.Version.UNIVERSITY_HOSPITAL) {
                KqPrivForHospitalUtil kqUtil = new KqPrivForHospitalUtil(userView, this.getFrameconn());
                String leaveSubset = kqUtil.getLeave_setid();
                if (StringUtils.isNotBlank(leaveSubset)) {
                    String startFld = kqUtil.getLeave_start();
                    String endFld = kqUtil.getLeave_end();
                    String reasonFld = kqUtil.getLeave_reason();
                    String typeFld = kqUtil.getLeave_type();
                    
                    sql.append("select A.b0110,A.e0122,A.a0101,");
                    sql.append("B.").append(startFld).append(" as q15z1,");
                    sql.append("B.").append(endFld).append(" as q15z3,");
                    sql.append("B.").append(reasonFld).append(" as q1507,");
                    sql.append(" ");
                    sql.append(" '请假' as q1519");
                    sql.append(" from ").append(nbase).append(leaveSubset).append(" B left join ").append(nbase).append("A01 A");
                    sql.append(" on B.a0100=A.a0100");
                    sql.append(" where B.a0100='" + a0100 + "'");
                    sql.append(" and ").append(startFld).append(">=" + Sql_switcher.dateValue(holidayYear + "-01-01 00:00:00") + "");
                    sql.append(" and ").append(endFld).append("<=" + Sql_switcher.dateValue(holidayYear + "-12-31 23:59:59"));
                    sql.append(" and ").append(typeFld).append(" IN (" + HolidayBo.getMapTypeIdsFromHolidayMap(holidayType) + ")");
                }
            }
            
            if (StringUtils.isBlank(sql.toString())) {
                sql.append("select q15z1,q15z3,q1507,b0110,e0122,a0101,");
                sql.append("CASE WHEN ").append(Sql_switcher.length("q1519")).append(">0 THEN '销假' ELSE '请假' END as q1519");
                sql.append(" from q15 where ");
                sql.append(" a0100='" + a0100 + "'");
                sql.append(" and nbase='" + nbase + "'");
                sql.append(" and q15z1>=" + Sql_switcher.dateValue(holidayYear + "-01-01 00:00:00") + "");
                sql.append(" and q15z3<=" + Sql_switcher.dateValue(holidayYear + "-12-31 23:59:59"));
                sql.append(" and q1503 IN (" + HolidayBo.getMapTypeIdsFromHolidayMap(holidayType) + ")");
                sql.append(" and q15z5 = '03'");
            }

            ArrayList<ColumnsInfo> columnList =  HolidayBo.getColumnList();
            /** 加载表格 */
            TableConfigBuilder builder = new TableConfigBuilder("holidayLeaveDetailed_0001", columnList,
                    "holidayLeaveDetailed_0001", this.userView, this.frameconn);
            builder.setDataSql(sql.toString());
            builder.setOrderBy("order by q15z1");
            builder.setAutoRender(false);
            builder.setPageSize(20);
            builder.setColumnFilter(true);
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config.toString());
            this.getFormHM().put("typeName", AdminCode.getCodeName("27", holidayType));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
