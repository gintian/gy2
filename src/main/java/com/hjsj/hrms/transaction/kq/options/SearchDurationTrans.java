package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchDurationTrans extends IBusiness {

    public void execute() throws GeneralException {
        String kq_year = (String) this.getFormHM().get("kq_year");
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        try {
            if (kq_year == null || "".equals(kq_year)) {
                this.frowset = dao.search("select max(kq_year) as kq_year from kq_duration where finished='0'");
                if (this.frowset.next()) {
                    kq_year = this.frowset.getString("kq_year");
                }
                
                //没有考勤年度了
                if (kq_year == null) 
                    return; 
            }
            
            strsql.append("select kq_year,kq_duration, kq_start,kq_end,gz_year,gz_duration,finished");
            strsql.append(" from kq_duration");
            strsql.append(" where kq_year=");
            strsql.append("'" + kq_year.toString() + "'");
            
            this.frowset = dao.search(strsql.toString());
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo("kq_duration");
                vo.setString("kq_year", this.frowset.getString("kq_year"));
                vo.setString("kq_duration", this.frowset.getString("kq_duration"));
                vo.setDate("kq_start", this.frowset.getDate("kq_start"));
                vo.setDate("kq_end", this.frowset.getDate("kq_end"));
                vo.setString("gz_duration", this.frowset.getString("gz_duration"));
                vo.setString("finished", this.frowset.getString("finished"));
                list.add(vo);
            }
            
            this.getFormHM().put("kq_year", kq_year);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            this.getFormHM().put("durationlist", list);
            this.getFormHM().put("radio", "3");
        }
    }

}
