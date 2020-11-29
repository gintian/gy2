package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CheckCodeIsParentTrans extends IBusiness {

    /*
     * 培训课程 检测代码项是否为上级
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        String isParent = "no";
        if (!this.userView.isSuper_admin()) {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String codeitemid = (String) this.getFormHM().get("codeitemid");
            codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
            String id = (String) this.getFormHM().get("id");
            String flag = (String) this.getFormHM().get("flag");
            String[] ids = id.split(",");
            id = "";
            for (int i = 0; i < ids.length; i++) {
                id += PubFunc.decrypt(SafeCode.decode(ids[i])) + ",";
            }

            if (id.endsWith(","))
                id = id.substring(0, id.length() - 1);

            if (id != null && id.length() > 0)
                codeitemid = getR5020(id);
            codeitemid = codeitemid == null ? "" : codeitemid;
            String codearr[] = codeitemid.split(",");
            if ("tree".equalsIgnoreCase(flag)) {
                for (int i = 0; i < codearr.length; i++) {
                    codeitemid = codearr[i];
                    if (codeitemid == null || codeitemid.length() < 1 || "null".equalsIgnoreCase(codeitemid))
                        continue;
                    StringBuffer sqlstr = new StringBuffer();
                    String setId = (String) this.getFormHM().get("setid");
                    setId = PubFunc.decrypt(SafeCode.decode(setId));
                    sqlstr.append("select b0110 from codeitem where codesetid='" + setId + "'");
                    sqlstr.append(" and codeitemid='" + codeitemid + "'");
                    try {
                        String tmpb0110 = "";
                        this.frowset = dao.search(sqlstr.toString());
                        if (this.frowset.next()) {
                            tmpb0110 = this.frowset.getString("b0110");
                        }
                        
                        if (tmpb0110 != null && tmpb0110.length() > 0) {
                            TrainCourseBo tbo = new TrainCourseBo(this.userView);
                            if (tbo.isUserParent(tmpb0110) == 2 || tbo.isUserParent(tmpb0110) == -1) {
                                isParent = "yes";
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                for (int i = 0; i < codearr.length; i++) {
                    codeitemid = codearr[i];
                    if (codeitemid == null || codeitemid.length() < 1 || "null".equalsIgnoreCase(codeitemid))
                        continue;
                    
                    TrainCourseBo tbo = new TrainCourseBo(this.userView);
                    if (tbo.isUserParent(codeitemid) == 2 || tbo.isUserParent(codeitemid) == -1) {
                        isParent = "yes";
                        break;
                    }
                }
            }
        }
        this.getFormHM().put("isParent", isParent);
    }

    private String getR5020(String id) {
        String codes = "";
        if (id.endsWith(","))
            id = id.substring(0, id.length() - 1);
        String sql = "select r5020 from r50 where r5000 in (" + id + ")";
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                codes += this.frowset.getString("r5020") + ",";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codes;
    }
}
