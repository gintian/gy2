package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchEmployRecordTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String a_code = (String) hm.get("id");
            if (a_code == null || a_code.length() <= 0 || "root".equalsIgnoreCase(a_code))
                a_code = "UN";

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            String code = kqUtilsClass.getCodeFormA_code(a_code);
            String kind = kqUtilsClass.getKindFormA_code(a_code);
            ArrayList kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);

            String select_pre = (String) this.getFormHM().get("select_pre");
            ArrayList db_list = new ArrayList();
            if (select_pre == null || select_pre.length() <= 0 || "all".equalsIgnoreCase(select_pre)
                    || "0".equalsIgnoreCase(select_pre))
                db_list = kq_dbase_list;
            else
                db_list.add(select_pre);

            String kqTypeWhr = kqUtilsClass.getKqTypeWhere(KqConstant.KqType.STOP, true);

            StringBuffer strsql = new StringBuffer();
            for (int i = 0; i < db_list.size(); i++) {
                String nbase = (String) db_list.get(i);
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                
                strsql.append("select '");
                strsql.append(nbase);
                strsql.append("' as nbase,");
                strsql.append("b0110,e0122,e01a1,a0100,a0101");
                strsql.append(" from ");
                strsql.append(nbase);
                strsql.append("a01");
                strsql.append(" where ");
                if ("1".equals(kind)) {
                    strsql.append("e0122 like '" + code + "%'");
                } else if ("0".equals(kind)) {
                    strsql.append("e01a1 like '" + code + "%'");
                } else if ("2".equals(kind)) {
                    strsql.append("b0110 like '" + code + "%'");
                }

                //排除暂停考勤人员
                strsql.append(kqTypeWhr);

                strsql.append(" and NOT EXISTS(SELECT 1 FROM kq_group_emp");
                strsql.append(" where " + nbase + "A01.a0100=kq_group_emp.a0100");
                strsql.append(" and UPPER(kq_group_emp.nbase)='" + nbase.toUpperCase() + "')");
                strsql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ")");
                strsql.append(" UNION ");
            }
            strsql.setLength(strsql.length() - 7);
            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
            this.getFormHM().put("sqlstr", strsql.toString());
            
            String column = "nbase,b0110,e0122,e01a1,a0100,a0101";
            this.getFormHM().put("column", column);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
