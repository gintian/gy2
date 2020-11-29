package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 得到人员信息
 * 
 * @author Owner
 * 
 */
public class QueryByA0101Trans extends IBusiness {

    public void execute() throws GeneralException {
        String a0101 = (String) this.getFormHM().get("a0101");
        ArrayList objlist = new ArrayList();
        this.getFormHM().put("objlist", objlist);
        
        if (a0101 == null || a0101.length() <= 0) {
            return;
        }
        
        a0101 = SafeCode.decode(a0101);
        a0101 = PubFunc.getStr(a0101);
        String a_code = (String) this.getFormHM().get("a_code");
        if (a_code == null || a_code.length() <= 0)
            a_code = "";
        
        if ("root".equals(a_code))
            a_code = "";
        
        String kind = "";
        String code = "";
        if (a_code == null || a_code.length() <= 0) {
            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
            if ("UN".equalsIgnoreCase(privcode))
                kind = "2";
            else if ("UM".equalsIgnoreCase(privcode))
                kind = "1";
            else if ("@K".equalsIgnoreCase(privcode))
                kind = "0";
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        } else {
            if (a_code.indexOf("UN") != -1) {
                kind = "2";
            } else if (a_code.indexOf("UM") != -1) {
                kind = "1";
            } else if (a_code.indexOf("@K") != -1) {
                kind = "0";
            }
            code = a_code.substring(2);
        }
        
        ArrayList kq_dbase_list = new ArrayList();
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
        String nbase = "";
        StringBuffer sql = new StringBuffer();
        String where = "";
        if ("1".equals(kind)) {
            where = " and e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            where = " and e01a1 like '" + code + "%'";
        } else {
            where = " and b0110 like '" + code + "%'";
        }
        
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
        String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name"); // 唯一性指标
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String g_no = (String) hashmap.get("g_no");
        String cardno = (String) hashmap.get("cardno");
        String kqType = para.getKq_type();
        
        //考勤方式条件：不包括“暂停考勤”人员
        String kqTypeWhr = " AND " + Sql_switcher.sqlNull(kqType, "04") + "<>'04'";
        if (Sql_switcher.searchDbServer() == Constant.MSSQL)
            kqTypeWhr = kqTypeWhr + " AND " + kqType + "<>''";
        
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        String select_type = (String) this.formHM.get("select_type");
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            nbase = kq_dbase_list.get(i).toString();
            if (onlyname != null && onlyname.trim().length() > 0) {
                sql.append("select '" + nbase + "' as nbase,a0100,b0110,a0101," + onlyname + " ");
                sql.append(" from " + nbase + "A01");
            } else {
                sql.append("select '" + nbase + "' as nbase,a0100,b0110,a0101 from " + nbase + "A01");
            }
            
            if ("0".equals(select_type)) {
                sql.append(" where a0101 like '" + a0101 + "%'");
            } else if ("1".equals(select_type)) {
                sql.append(" where " + g_no + " like '" + a0101 + "%'");
            } else {
                sql.append(" where " + cardno + " like '" + a0101 + "%'");
            }
            
            sql.append(kqTypeWhr);
            
            sql.append(" " + where);
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            sql.append(" and a0100 in(select a0100 " + whereIN + ") ");
            sql.append(" UNION ");
        }
        if (sql.length() > 0)
            sql.setLength(sql.length() - 7);
        // System.out.println(sql.toString());

        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                // 根据李群要求更改：修改为：人员姓名+后台指定的唯一性指标
                CommonData objvo = new CommonData();
                if (onlyname != null && onlyname.trim().length() > 0) {
                    String onlyname1 = this.frowset.getString(onlyname) != null ? this.frowset.getString(onlyname) : "";
                    objvo.setDataName(onlyname1 + "(" + this.frowset.getString("a0101") + ")");
                    objvo.setDataValue(this.frowset.getString("nbase") + this.frowset.getString("a0100"));
                    objlist.add(objvo);
                } else {
                    String b0110 = this.frowset.getString("b0110");
                    String name = AdminCode.getCodeName("UN", b0110);
                    objvo.setDataName(name + "(" + this.frowset.getString("a0101") + ")");
                    objvo.setDataValue(this.frowset.getString("nbase") + this.frowset.getString("a0100"));
                    objlist.add(objvo);
                }
                if (this.frowset.getRow() > 100) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getFormHM().put("objlist", objlist);
    }

}
