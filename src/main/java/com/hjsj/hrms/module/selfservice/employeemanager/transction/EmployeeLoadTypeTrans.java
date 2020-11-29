package com.hjsj.hrms.module.selfservice.employeemanager.transction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Title EmployeeLoadTypeTrans
 * @Description 获取用户权限内的组织机构编码
 * @Company hjsj
 * @Date 2020/06/04
 * @Version 1.0.0
 */
public class EmployeeLoadTypeTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        String funcType = hm.get("funcType").toString();

        String orgDesc = "";
        String orgType = this.userView.getManagePrivCode();
        String orgCode = this.userView.getManagePrivCodeValue();
        if (orgCode.length() > 0) {//获取机构id和desc
            CodeItem co = AdminCode.getCode(orgType, orgCode);
            orgDesc = co.getCodename();
        } else if ("UN".equals(orgType) || userView.isSuper_admin()) {//超级用户，查询顶级机构
//				CodeItem co = getTopOrg();
//				orgCode = co.getCodeitem();
//				orgDesc = co.getCodename();
            orgCode = "ALL";
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            orgDesc = sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
            if ("".equals(orgDesc))
                orgDesc = "组织机构";
        } else {
            //没有管理范围
        }
        this.getFormHM().put("orgCode", orgCode);
        this.getFormHM().put("orgDesc", orgDesc);
        if ("2".equals(funcType)) {// “2”：本人档案
            String a0100 = "";
            String nbase = "";
            if (hm.get("a0100") == null || "".equals(hm.get("a0100"))) {
                a0100 = this.userView.getA0100();
                nbase = this.userView.getDbname();
            } else {
                a0100 = hm.get("a0100").toString();
                nbase = hm.get("nbase").toString();
            }
            int count = getTotalCount(a0100, nbase);
            hm.put("a0100", a0100);
            hm.put("nbase", nbase);
            hm.put("totalCount", count);
        }

    }

    //获取员工档案下属人员数
    private int getTotalCount(String a0100, String nbase) {
        int totalCount = 0;
        HashMap hm = this.getFormHM();
        ContentDAO dao;
        String objectId = "";
        String nbaseCond = "";
        String KCond = "";
        String UMCond = "";
        String UNCond = "";
        String sql = "select object_id from t_wf_mainbody  WHERE   relation_id =  (select relation_id from t_wf_relation where  default_line='1' and actor_type = '1') and sp_grade='9' and mainbody_id='" + nbase + a0100 + "'";
        try {
            dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql.toString());
            String whereSql = "where 1<>1 ";
            while (this.frowset.next()) {
                objectId = this.frowset.getString("object_id");
                if ("".equals(objectId))
                    continue;
                if ("@K".equalsIgnoreCase(objectId.substring(0, 2))) {
                    KCond += "'" + objectId.substring(2) + "',";
                } else if ("UM".equalsIgnoreCase(objectId.substring(0, 2))) {
                    UMCond += "'" + objectId.substring(2) + "',";
                } else if ("UN".equalsIgnoreCase(objectId.substring(0, 2))) {
                    UNCond += "'" + objectId.substring(2) + "',";
                } else if (nbase.equalsIgnoreCase(objectId.substring(0, 3))) {
                    nbaseCond += "'" + objectId.substring(3) + "',";
                }
            }
            //通过objectid生成岗位，部门，机构，人员的过滤范围
            if ("".equals(KCond) && "".equals(UMCond) && "".equals(UNCond) && "".equals(nbaseCond))
                return 0;

            if (!"".equals(KCond)) {
                whereSql += " or E01A1 in (" + KCond.substring(0, KCond.length() - 1) + ") ";
            }
            if (!"".equals(UMCond)) {
                whereSql += " or E0122 in (" + KCond.substring(0, KCond.length() - 1) + ") ";
            }
            if (!"".equals(UNCond)) {
                whereSql += " or B0110 in (" + KCond.substring(0, KCond.length() - 1) + ") ";
            }
            if (!"".equals(nbaseCond)) {
                whereSql += " or A0100 in (" + nbaseCond.substring(0, nbaseCond.length() - 1) + ") ";
            }
            sql = "SELECT  count(1) count FROM " + nbase + "A01  " + whereSql;
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                totalCount = this.frowset.getInt("count");
            }
        } catch (Exception e) {

        }
        return totalCount;
    }

    //获取最大权限code
    private CodeItem getTopOrg() {
        CodeItem code = new CodeItem();
        code.setCodename("");
        code.setCodeitem("");
        String sql = "select codeitemid,codeitemdesc from organization where parentid=codeitemid ";
        try {
            this.frowset = new ContentDAO(this.frameconn).search(sql);
            if (this.frowset.next()) {
                code.setCodeitem(this.frowset.getString("codeitemid"));
                code.setCodename(this.frowset.getString("codeitemdesc"));
            }
        } catch (Exception e) {

        }
        return code;
    }
}
