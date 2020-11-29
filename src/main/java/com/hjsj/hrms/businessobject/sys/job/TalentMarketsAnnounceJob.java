package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

/**
 * @Description 人才市场公示后台交易类
 * @Author wangz
 * @Date 2019/8/26 14:53
 * @Version V1.0
 **/
public class TalentMarketsAnnounceJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection conn = null;
        try {
            conn =  AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer();
            sql.append("update z83 set z8303 = '13' where z8101 in (select z8101  from z81 where ")
                    .append(Sql_switcher.sqlNow()).append(">pub_enddate and z8103 = '07' ").append(")");
            sql.append(" and z8303 = '10' ");
            dao.update(sql.toString());
            sql.setLength(0);
            sql.append("update z81 set z8103 = '09' where ").append(Sql_switcher.sqlNow()).append(">pub_enddate and z8103 = '07'");
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(conn);
        }
    }
}
