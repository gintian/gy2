package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KqFeastCountJob implements Job {

    private Logger log = LoggerFactory.getLogger(KqFeastCountJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[考勤计算年假后台作业]任务开始");
        long start = System.currentTimeMillis();
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = (Connection) AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String password = "";
            rs = dao.search("select Password from operuser where UserName='su'");
            String check = "0";
            if (rs.next()) {
                password = rs.getString("Password");
                password = password != null ? password : "";
                check = "1";
            }
            UserView uv = new UserView("su", password, conn);
            uv.canLogin();
            if (uv != null && "1".equals(check)) {
                countKqFeast(conn, uv);
            }
            log.info("[考勤计算年假后台作业]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            log.error("考勤计算年假后台作业执行sql报错!,desc:{}", e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("考勤计算年假后台作业发生异常,desc:{}", e);
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
    }

    /**
     * 计算考勤年假
     *
     * @param conn
     * @param userView
     */
    private void countKqFeast(Connection conn, UserView userView) {
        KqVer kqVer = new KqVer();
        int ver = kqVer.getVersion();


        String hols_type = "";

        HashMap hashmap = new HashMap();
        String b0110 = "";
        FeastComputer feastComputer = new FeastComputer(conn, userView);
        if (ver == KqConstant.Version.STANDARD) {
            //标准班考勤取假期管理参数中设置的假期类型
            do {
                ArrayList list = feastComputer.getHolsType("");
                hashmap = (HashMap) list.get(0);
                b0110 = (String) hashmap.get("b0110");
            } while (b0110 == null || b0110.length() <= 0);

            hols_type = (String) hashmap.get("type");
        } else {
            // 高校医院班考勤无假期管理设置，默认只管理年假
            hols_type = "06";
        }
        if (hols_type == null || hols_type.length() <= 0) {
            return;
        }


        // 获取假期管理的类别
        ArrayList holi_list = feastComputer.getHolsList(hols_type);

        int year = DateUtils.getYear(new Date());
        String theYear = year + "";
        String countStart = year + "-01-01";
        String countEnd = year + "-12-31";
        // 优化为 新的假期管理计算规则
        HolidayBo bo = new HolidayBo(conn, userView);
        ArrayList<HashMap<String, String>> fieldlist = bo.getKqFormulaFields();
        StringBuffer fieldJson = new StringBuffer("");
        for (int i = 0; i < fieldlist.size(); i++) {
            HashMap<String, String> map = fieldlist.get(i);
            fieldJson.append(map.get("itemname") + ",");
        }
        if (StringUtils.isEmpty(fieldJson.toString())) {
            fieldJson.append("q1703");
        }
        // 循环假期种类
        for (int i = 0; i < holi_list.size(); i++) {
            CommonData cd = (CommonData) holi_list.get(i);
            String hols_status = cd.getDataValue();

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("fieldData", fieldJson.toString());
            map.put("nbase", "ALL");
            map.put("holidayType", hols_status);
            map.put("holidayYear", theYear);
            map.put("countStart", countStart);
            map.put("countEnd", countEnd);
            map.put("balanceValue", "0");
            map.put("balanceEndDate", "");
            map.put("clearZone", "1");

            bo.calCulateHoliday(map);
        }
    }
}
