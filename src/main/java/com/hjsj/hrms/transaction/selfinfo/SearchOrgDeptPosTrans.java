package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgDeptPosTrans extends IBusiness {

    public void execute() throws GeneralException {
        String pretype = (String) this.getFormHM().get("pretype");
        String orgparentcode = (String) this.getFormHM().get("orgparentcodestart");
        String deptparentcode = (String) this.getFormHM().get("deptparentcodestart");
        String posparentcode = (String) this.getFormHM().get("posparentcodestart");

        if ("UN".equalsIgnoreCase(pretype)) {
            // 为了在选择代码时方便而压入权限码开始
            this.getFormHM().put("orgparentcode",
                    userView.isSuper_admin() ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
            // 为了在选择代码时方便而压入权限码结束
            if (orgparentcode.length() != 0) {
                this.getFormHM().put("deptparentcode", orgparentcode);
                this.getFormHM().put("posparentcode", orgparentcode);
            } else {
                // 如果单位设置为空的话，部门就直接设置权限范围，要不然会越权guodd 2015-03-25
                this.getFormHM().put("deptparentcode",
                        userView.isSuper_admin() ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
                this.getFormHM().put("posparentcode", posparentcode);
            }
        } else if ("UM".equalsIgnoreCase(pretype)) {
            if (deptparentcode != null && deptparentcode.trim().length() > 0) {
                List savePos = getStationPos(deptparentcode, "UM");
                for (int n = 0; n < savePos.size(); n++) {
                    StationPosView posview = (StationPosView) savePos.get(n);
                    if ("b0110".equalsIgnoreCase(posview.getItem())) {
                        this.getFormHM().put("orgvalue", posview.getItemvalue());
                        this.getFormHM().put("orgviewvalue", posview.getItemviewvalue());
                        this.getFormHM().put("orgparentcode",
                                userView.isSuper_admin() ? userView.getManagePrivCodeValue()
                                        : userView.getUnitIdByBusi("4"));
                        this.getFormHM().put("deptparentcode", posview.getItemvalue());
                        this.getFormHM().put("posparentcode", deptparentcode);
                        break;
                    }
                }
            } else {// 如果部门设置为空的话，岗位就直接设置权限范围，要不然会越权 guodd 2015-03-25
                this.getFormHM().put("deptparentcode",
                        userView.isSuper_admin() ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
                this.getFormHM().put("posparentcode",
                        userView.isSuper_admin() ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
            }
        } else if ("@K".equalsIgnoreCase(pretype)) {
            this.getFormHM().put("deptvalue", "");
            this.getFormHM().put("deptviewvalue", "");
            this.getFormHM().put("posparentcode", "");
            this.getFormHM().put("orgparentcode",
                    userView.isSuper_admin() ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
            if (posparentcode != null && posparentcode.trim().length() > 0) {
                List savePos = getStationPos(posparentcode, "@K");
                for (int n = 0; n < savePos.size(); n++) {
                    StationPosView posview = (StationPosView) savePos.get(n);
                    if ("b0110".equalsIgnoreCase(posview.getItem())) {
                        this.getFormHM().put("orgvalue", posview.getItemvalue());
                        this.getFormHM().put("orgviewvalue", posview.getItemviewvalue());
                        this.getFormHM().put("deptparentcode", posview.getItemvalue());
                        this.getFormHM().put("orgparentcode",
                                userView.isSuper_admin() ? userView.getManagePrivCodeValue()
                                        : userView.getUnitIdByBusi("4"));
                        this.getFormHM().put("posparentcode", posview.getItemvalue());
                    }
                    
                    if ("e0122".equalsIgnoreCase(posview.getItem())) {
                        this.getFormHM().put("deptvalue", posview.getItemvalue());
                        this.getFormHM().put("deptviewvalue", posview.getItemviewvalue());
                        this.getFormHM().put("posparentcode", posview.getItemvalue());
                        this.getFormHM().put("orgparentcode",
                                userView.isSuper_admin() ? userView.getManagePrivCodeValue()
                                        : userView.getUnitIdByBusi("4"));
                    }
                }
            }
        }
    }

    private ArrayList getStationPos(String code, String pre) {
        ArrayList poslist = new ArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        boolean ispos = false;
        boolean isdep = false;
        boolean isorg = false;
        StringBuffer strsql = new StringBuffer();
        try {
            if ("UN".equals(pre)) {
                strsql.append("select * from organization");
                strsql.append(" where codeitemid='");
                strsql.append(code);
                strsql.append("'");
                ContentDAO db = new ContentDAO(this.frameconn);
                rs = db.search(strsql.toString());
                if (rs.next()) {
                    StationPosView posview = new StationPosView();
                    posview.setItem("b0110");
                    posview.setItemvalue(rs.getString("codeitemid"));
                    posview.setItemviewvalue(rs.getString("codeitemdesc"));
                    poslist.add(posview);
                }
            } else {
                ContentDAO db = new ContentDAO(this.frameconn);
                while (!"UN".equalsIgnoreCase(pre)) {
                    strsql.delete(0, strsql.length());
                    strsql.append("select * from organization");
                    strsql.append(" where codeitemid='");
                    strsql.append(code);
                    strsql.append("'");
                    rs = db.search(strsql.toString()); // 执行当前查询的sql语句
                    if (rs.next()) {
                        StationPosView posview = new StationPosView();
                        pre = rs.getString("codesetid");
                        if ("@K".equalsIgnoreCase(pre)) {
                            if (ispos == false) {
                                posview.setItem("e01a1");
                                posview.setItemvalue(rs.getString("codeitemid"));
                                posview.setItemviewvalue(rs.getString("codeitemdesc"));
                                ispos = true;
                                poslist.add(posview);
                            }
                        } else if ("UM".equalsIgnoreCase(pre)) {
                            if (isdep == false) {
                                posview.setItem("e0122");
                                posview.setItemvalue(rs.getString("codeitemid"));
                                posview.setItemviewvalue(rs.getString("codeitemdesc"));
                                isdep = true;
                                poslist.add(posview);
                            }
                        } else if ("UN".equalsIgnoreCase(pre)) {
                            if (isorg == false) {
                                posview.setItem("b0110");
                                posview.setItemvalue(rs.getString("codeitemid"));
                                posview.setItemviewvalue(rs.getString("codeitemdesc"));
                                isorg = true;
                                poslist.add(posview);
                            }
                        }
                        code = rs.getString("parentid");
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }

        return poslist;
    }
}
