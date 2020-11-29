package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.statfx.RegisterStatBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 点击树展现 页面
 * 
 * @author Owner wangyao
 */
public class QueryDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        
        String code = (String) this.getFormHM().get("code");
        this.getFormHM().remove("code");
        if (code == null || code.length() <= 0) {
            code = "";
        }
        
        String kind = (String) this.getFormHM().get("kind");
        if (kind == null || kind.length() <= 0) {
            kind = "2";
        }
        
        String start_datetj = (String) this.getFormHM().get("start_datetj");
        String file = (String) hm.get("root");
        String dbpre = (String) this.getFormHM().get("dbpre");
        if (dbpre.length() == 0) {
        	dbpre="all";
        }
        ArrayList kq_dbase_list =  RegisterInitInfoData.getDase3(this.getFormHM(),this.userView, this.getFrameconn());
       
        /**
         *页面头展现
         */
        ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        String kqname = "KQ_PARAM";
        
        String codesetid = "UN";
        if (!userView.isSuper_admin() && "UM".equals(RegisterInitInfoData.getKqPrivCode(userView))) {
                codesetid = "UM";
        }
        ArrayList kqq03list = RegisterStatBo.savekqq03list(kqname, this.getFrameconn(), fielditemlist);
        kqq03list = RegisterStatBo.newFieldItemListQ09(kqq03list, codesetid);

        //zxj changed 20140305 取权限组织机构代码
        //（原逻辑：如果权限组织机构不是单位，那么取其上级单位。此逻辑错误的。）
        String userOrgId = RegisterInitInfoData.getKqPrivCodeValue(this.userView);
        String b0100s = "";

        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
        if (uplevel == null || uplevel.length() == 0)
            uplevel = "0";
        this.getFormHM().put("uplevel", uplevel);
        
        String B0110z = selectB0110(code);
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        ArrayList sqllist = RegisterStatBo.getSqlstrHistory(kqq03list, b0100s, start_datetj.substring(0, 4) + "-PT", "Q09", userOrgId, code, B0110z, this.getFrameconn(), file);
        this.getFormHM().put("kqq03list", kqq03list);
        this.getFormHM().put("kq_dbase_list", kq_dbase_list);
        //this.getFormHM().put("code", code);
        this.getFormHM().put("codetj", code);
        
        if (kind != null && !"".equals(kind))
            kind = "";        
        this.getFormHM().put("kind", kind);
        
        this.getFormHM().put("sqlstr", sqllist.get(0).toString());
        this.getFormHM().put("strwhere", sqllist.get(1).toString());
        
        if (dbpre.length() != 3) {
            
            this.getFormHM().put("strwhere", sqllist.get(1).toString().substring(0, sqllist.get(1).toString().indexOf("where") + 5) + "  1=2 ");
        }        
        this.getFormHM().put("dbpre", dbpre);
    	this.getFormHM().put("slist",kqUtilsClass.getKqNbaseList(kq_dbase_list));
        this.getFormHM().put("orderby", sqllist.get(2).toString());
        this.getFormHM().put("columns", sqllist.get(3).toString());
    }

    /**
     * 通过 organization 得到对应的B0110
     * 
     * @param code
     * @return
     */
    private String selectB0110(String code) {
        String zh = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql1 = new StringBuffer();
        RowSet rowSet = null;
        try {
            sql1.append("select codeitemid from organization where grade='1' order by codeitemid");
            rowSet = dao.search(sql1.toString());
            while (rowSet.next()) {
                zh = rowSet.getString("codeitemid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return zh;
    }

    
}
