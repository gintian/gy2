package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.*;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqReportViewTran extends IBusiness {
    public void execute() throws GeneralException {
        String userbase = (String) this.getFormHM().get("userbase");
        String kind = (String) this.getFormHM().get("kind");
        String dbdbtype = (String) this.getFormHM().get("dbtype");
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String sortitem = (String) hm.get("sortitem");
        String code = (String) hm.get("code");
        String codesetid = getorg(code);
        if ("".equals(codesetid)) {
            code = "";
        }
        
        if (sortitem == null) {
            sortitem = "";
        } else {
            sortitem = SafeCode.decode(sortitem);
        }

        String history = (String) hm.get("history");
        if ("1".equals(history)) {
            dbdbtype = (String) hm.get("userbase");
            hm.remove("history");
        }
        
        if (dbdbtype == null || "".equals(dbdbtype))
            dbdbtype = "all";

        if (codesetid == null || codesetid.length() <= 0) {
            if (!this.userView.isSuper_admin()) {
                String privCode = RegisterInitInfoData.getKqPrivCode(userView);
                kind = getKind(privCode);
            }
        } else {
            kind = getKind(codesetid);
        }

        String coursedate = (String) this.getFormHM().get("coursedate");
        String curpage = (String) this.getFormHM().get("curpage");
        String report_id = (String) this.getFormHM().get("report_id");
        String self_flag = (String) this.getFormHM().get("self_flag");
        
        if (coursedate == null || coursedate.length() <= 0) {
            coursedate = RegisterDate.getKqDuration(this.getFrameconn());
        }
        
        if (!userView.isSuper_admin()) {
            if (kind == null || kind.length() <= 0) {
                LazyDynaBean bean = RegisterInitInfoData.getKqPrivCodeAndKind(userView);
                code = (String) bean.get("code");
                kind = (String) bean.get("kind");
            }
        } else {
            if (code == null || code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                code = managePrivCode.getPrivOrgId();
                kind = "2";
            }

        }
        
        if (curpage == null || curpage.length() <= 0) {
            curpage = "1";
        }
        
        if (kind == null || kind.length() <= 0)
            kind = "-2";
        
        if (!"-2".equals(kind) && (code == null || code.length() <= 0)) {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            code = managePrivCode.getPrivOrgId();

        }
        
        if (!"-2".equals(kind) && (code.length() < RegisterInitInfoData.getKqPrivCodeValue(userView).length() && "UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
            kind = "1";
        } else if (!"-2".equals(kind) && (code.length() < RegisterInitInfoData.getKqPrivCodeValue(userView).length() && "@K".equals(RegisterInitInfoData.getKqPrivCode(userView)))) {
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
            kind = "0";
        } else if (kind == null || kind.length() <= 0 || code == null || code.length() <= 0) {
            if (this.userView.getUserDeptId() != null && this.userView.getUserDeptId().length() > 0) {
                // code=this.userView.getUserDeptId();
                code = RegisterInitInfoData.getKqPrivCodeValue(userView);
                kind = "1";
            } else if (this.userView.getUserOrgId() != null && this.userView.getUserOrgId().length() > 0) {
                // code=this.userView.getUserOrgId();
                code = RegisterInitInfoData.getKqPrivCodeValue(userView);
                kind = "2";
            }
        }
        
        KqReportInit kqReportInit = new KqReportInit(this.getFrameconn());
        ReportParseVo parsevo = kqReportInit.getParseVo(report_id);
        //判断数据是否位归档数据
        kqReportInit.checkArcData(parsevo.getValue().trim(), coursedate);
        
        ArrayList tablelist = new ArrayList();
        if (self_flag == null || self_flag.length() <= 0)
            self_flag = "";
        KqParameter para = new KqParameter(this.getFormHM(), this.userView, "", this.getFrameconn());
        String cardn = para.getG_no();
        if ("q03".equals(parsevo.getValue().trim()) && !"select".equals(self_flag)) {
            KqViewDailyBo kqViewDaily = new KqViewDailyBo(this.getFrameconn());
            kqViewDaily.setCardno(cardn);
            kqViewDaily.setSelf_flag(self_flag);
            kqViewDaily.setDbtype(dbdbtype);
            kqViewDaily.setSortItem(sortitem);
            kqViewDaily.setCurTab(kqReportInit.getCurTab());
            
            tablelist = kqViewDaily.getKqReportHtml(code, kind, coursedate, curpage, parsevo, userView, this.getFormHM());
        } else if ("q03".equals(parsevo.getValue().trim()) && "select".equals(self_flag)) {
            String whereIN = (String) this.getFormHM().get("wherestr_s");
            KqViewDailyBo kqViewDaily = new KqViewDailyBo(this.getFrameconn());
            kqViewDaily.setCardno(cardn);
            kqViewDaily.setSelf_flag(self_flag);
            kqViewDaily.setWhereIN(whereIN);
            kqViewDaily.setDbtype(dbdbtype);
            kqViewDaily.setSortItem(sortitem);
            kqViewDaily.setCurTab(kqReportInit.getCurTab());
            
            tablelist = kqViewDaily.getKqReportHtml(code, kind, coursedate, curpage, parsevo, userView, this.getFormHM());
            this.getFormHM().put("wherestr_s", whereIN);
        } else if ("q05".equals(parsevo.getValue().trim())) {
            KqViewSumBo kqViewSumBo = new KqViewSumBo(this.getFrameconn());
            kqViewSumBo.setSelf_flag(self_flag);
            kqViewSumBo.setSortItem(sortitem);
            kqViewSumBo.setCurTab(kqReportInit.getCurTab());
            
            tablelist = kqViewSumBo.getKqReportHtml(code, kind, coursedate, curpage, parsevo, userView, this.getFormHM());
        }
        if (tablelist != null && tablelist.size() > 0) {
            String tableHtml = tablelist.get(0).toString();
            String turnTableHtml = tablelist.get(1).toString();
            this.getFormHM().put("tableHtml", tableHtml);
            this.getFormHM().put("turnTableHtml", turnTableHtml);
        }

        this.getFormHM().put("userbase", userbase);
        this.getFormHM().put("dbtype", dbdbtype);
        this.getFormHM().put("code", code);
        this.getFormHM().put("codeValue", code);
        this.getFormHM().put("kind", kind);
        this.getFormHM().put("coursedate", coursedate);
        this.getFormHM().put("curpage", curpage);
        this.getFormHM().put("report_id", report_id);
        this.getFormHM().put("self_flag", self_flag);
        this.getFormHM().put("parsevo", parsevo);

    }

    private String getorg(String code) {
        String codesetid = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql = new StringBuffer();
        sql.append("select codesetid from organization where codeitemid='" + code + "'");
        try {
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                codesetid = rowSet.getString("codesetid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return codesetid;
    }
    
    private String getKind(String codeSetId) {
        if ("UN".equals(codeSetId))
            return "2";
        
        if ("UM".equals(codeSetId))
            return "1";
        
        if ("@K".equals(codeSetId))
            return "0";
        
        return "";
    }
}
