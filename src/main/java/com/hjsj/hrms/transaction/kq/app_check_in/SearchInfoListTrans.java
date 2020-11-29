/*
 * Created on 2006-1-1
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author wxh
*
*/
public class SearchInfoListTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String a_code = (String) this.getFormHM().get("a_code");
            String flag = (String) this.getFormHM().get("flag");
            String bytype = (String) hm.get("bytype");
            
            String dbpre = (String) this.getFormHM().get("dbpre");
            if (dbpre == null || "".equals(dbpre)) {
                dbpre = "all";
            }
            
            String byname = (String) hm.get("byname");
            hm.remove("byname");
            
            if ("9".equals(flag))
                a_code = "UN";
            
            if (a_code == null || "".equals(a_code)) {
                a_code = "UN";
            }
            
            String codesetid = a_code.substring(0, 2);
            String codevalue = a_code.substring(2);
    
            String paramOrgId = "";        
            if (a_code.indexOf("UN") != -1 && (codevalue == null || codevalue.length() <= 0)) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                String userOrgId = managePrivCode.getPrivOrgId();
                paramOrgId = "UN" + userOrgId;
            } else if (a_code.indexOf("UN") != -1 && (codevalue != null && codevalue.length() > 0)) {
                paramOrgId = codevalue;
            } else if (codevalue != null && codevalue.length() > 0) {
                String b0110 = RegisterInitInfoData.getDbB0100(codevalue, "0", this.getFormHM(),
                        this.userView, this.getFrameconn());
                paramOrgId = b0110;
            }
            KqParameter para = new KqParameter(this.getFormHM(), this.userView, paramOrgId, this.getFrameconn());
    
            KqUtilsClass kqUtils = new KqUtilsClass(this.frameconn, this.userView);
            ArrayList dbaselist = kqUtils.getKqPreList();
            
            ArrayList alistd = new ArrayList();
            alistd = this.getDbase(dbaselist);
            if (alistd.size() == 0) {
                this.getFormHM().put("cond_str", "");
                this.getFormHM().put("sql_str", "");
                this.getFormHM().put("columns", "");
                this.getFormHM().put("cond_order", "");
                return;
            }
            
            this.getFormHM().put("dblist", alistd);
            /**相关代码类及代码值*/

            //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
            HashMap hashmap = para.getKqParamterMap();
            String g_no = (String) hashmap.get("g_no");
            String cardno = (String) hashmap.get("cardno");
            //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
            StringBuffer strexpr = new StringBuffer();
            StringBuffer strfactor = new StringBuffer();

            if ("UN".equals(codesetid)) {
                if (codevalue == null || "".equals(codevalue)) {
                    strfactor.append("B0110=`B0110=");
                    strfactor.append(codevalue);
                    strfactor.append("*`");
                    strexpr.append("1+2");
                } else {
                    strfactor.append("B0110=");
                    strfactor.append(codevalue);
                    strfactor.append("*`");
                    strexpr.append("1");
                }
            } else if ("UM".equals(codesetid)) {
                strfactor.append("E0122=");
                strfactor.append(codevalue);
                strfactor.append("*`");
                strexpr.append("1");
            } else if ("@K".equals(codesetid)) {
                strfactor.append("E01A1=");
                strfactor.append(codevalue);
                strfactor.append("*`");
                strexpr.append("1");
            } else {
                strfactor.append("B0110=*`B0110=`");
                strexpr.append("1+2");
            }
            ArrayList fieldlist = new ArrayList();
            String strwhere = "";
            StringBuffer strsql = new StringBuffer();
             
            for (int i = 0; i < dbaselist.size(); i++)
            {
                String nbase = (String)dbaselist.get(i);
                if (!nbase.equals(dbpre) && !"all".equals(dbpre))
                    continue;
                
                strsql.append("select distinct a0000,a0100 ");
                strsql.append("," + g_no + " gno," + cardno);
                strsql.append(" cardno ,b0110,e0122,e01a1,a0101, ");
                strsql.append("'" + nbase +  "' nbase ");
                if (!userView.isSuper_admin()) {
                    if (userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue()))
                        strwhere += userView.getKqPrivSQLExpression(strexpr.toString() + "|"
                                + strfactor.toString(), nbase, fieldlist);
                    else
                        strwhere += userView.getPrivSQLExpression(strexpr.toString() + "|"
                                + strfactor.toString(), nbase, true, fieldlist);
                } else {
                    FactorList factorlist = new FactorList(strexpr.toString(), strfactor.toString(),
                            nbase, false, true, true, 1, userView.getUserName());
                    strwhere += factorlist.getSqlExpression();
                }
                
                //不包括暂停考勤人员
                strwhere = strwhere + kqUtils.getKqTypeWhere(KqConstant.KqType.STOP, true);
                //不包括不考勤人员
                strwhere = strwhere + kqUtils.getKqTypeWhere(KqConstant.KqType.NO, true);
                
                if (byname != null && byname.length() > 0) {
                    byname = SafeCode.decode(byname);
                    if ("0".equals(bytype)) {
                        strwhere += " AND (a0101 LIKE '%" + byname + "%'";
                        //拼音简码
                        String pinyinFld = kqUtils.getPinYinFld();
                        if (!"".equals(pinyinFld))
                            strwhere += " OR " + pinyinFld + " LIKE '%" + byname + "%'";
                        strwhere += ")";
                    } else if ("1".equals(bytype)) {
                        strwhere += " AND " + g_no + " LIKE '%" + byname + "%'";
                    } else if ("2".equals(bytype)) {
                        strwhere += " AND " + cardno + " LIKE '%" + byname + "%'";
                    }
                }
                
                strsql.append(strwhere);
                strsql.append(" union ");
                strwhere="";
            }
            
            if (strsql.length() > 0)
                strsql.setLength(strsql.length() - 7);
            
            this.getFormHM().put("bytype", bytype);
            this.getFormHM().put("cond_str", "");
            /**条件列表*/

            
            this.getFormHM().put("sql_str", strsql.toString());
            /**字段列表*/
            strsql.setLength(0);
            strsql.append("a0100,gno,cardno,b0110,e0122,e01a1,a0101,nbase");

            this.getFormHM().put("columns", strsql.toString());
            this.getFormHM().put("cond_order", " order by b0110,e0122");
            this.getFormHM().put("flag", "8");

            //显示部门层数
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
            this.getFormHM().put("viewPost", para.getKq_orgView_post());
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private String getFirstDbase(ArrayList dblist) throws GeneralException {

        CommonData vo = (CommonData) dblist.get(0);
        return vo.getDataValue();

    }

    private ArrayList getDbase(ArrayList dbaselist) throws GeneralException {

        StringBuffer stb = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
        ArrayList slist = new ArrayList();
        CommonData cd = new CommonData("all","全部人员库");
        slist.add(cd);
        try {
            stb.append("select * from dbname ORDER BY dbid");
            this.frowset = dao.search(stb.toString());
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre");
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if (!dbpre.equalsIgnoreCase(userbase)) 
                        continue;
                    
                    CommonData vo = new CommonData(this.frowset.getString("pre"), this.frowset.getString("dbname"));
                    slist.add(vo);
                }
            }
            if(slist.size() == 2)
                slist.remove(0);
                
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        return slist;

    }
}
