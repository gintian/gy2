package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EmpBaseChangeTrans extends IBusiness {
    public void execute() throws GeneralException {
        ArrayList addlist = new ArrayList();
        try {
            String TabName = (String) this.getFormHM().get("TabName");

            // 部门变动日期（考勤参数中的设置）
            String field = KqParam.getInstance().getDeptChangeDateField();
            // 是否设置了部门变动时间,默认为0没有设置，1为已设置
            String deptChange = "0";
            if (field.length() > 0) {
                deptChange = "1";
            }
            
            StringBuffer sql = new StringBuffer();
            sql.append("select nbase,A0100,B0110,OB0110,E0122,OE0122,E01A1,E01A1,OE01A1,A0101,OA0101,flag");
            if (field.length() > 0) {
                sql.append(",change_date from " + TabName);
            } else {
                sql.append(" from " + TabName);
            }
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo(TabName.toLowerCase());
                vo.setString("nbase", this.frowset.getString("nbase"));
                vo.setString("a0100", this.frowset.getString("A0100"));
                vo.setString("b0110", this.frowset.getString("B0110"));
                vo.setString("ob0110", this.frowset.getString("OB0110"));
                vo.setString("e0122", this.frowset.getString("E0122"));

                // 添加变动时间
                if (field.length() > 0) {
                    vo.setString("change_date", this.frowset.getString("change_date"));
                }

                vo.setString("oe0122", this.frowset.getString("OE0122"));
                vo.setString("e01a1", this.frowset.getString("E01A1"));
                vo.setString("oe01a1", this.frowset.getString("OE01A1"));
                vo.setString("a0101", this.frowset.getString("A0101"));
                vo.setString("oa0101", this.frowset.getString("OA0101"));
                addlist.add(vo);
            }
            
            this.getFormHM().put("changelist", addlist);
            this.getFormHM().put("deptChange", deptChange);
            if (addlist != null && addlist.size() > 0) {
                this.getFormHM().put("changestatus", "2");
                this.getFormHM().put("base_count", addlist.size() + "");
            } else {
                this.getFormHM().put("changestatus", "2");
                this.getFormHM().put("base_count", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
       
    }

}
