package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 历史查询 统计分析 树
 * 
 * @author Owner wangyao
 */
public class LoadInfoOrgtreeTrans extends IBusiness {

    public void execute() throws GeneralException {
        /** 得到当前考勤期间 **/
        String cur_course = "";
        String end_date = "";
        //点击统计分析时对code进行销毁，防止统计时code含有其他值对统计范围造成影响
        this.getFormHM().remove("code");
        ArrayList list = RegisterDate.getKqDayList(this.getFrameconn());
        if (list != null && list.size() > 0) {
            cur_course = list.get(0).toString(); // 当前考勤期间开始时间 1号
            end_date = list.get(1).toString(); // 30号，结束时间
        } else {
            throw new GeneralException(ResourceFactory.getProperty("kq.register.session.nosave"));
        }

        try {
            TreeItemView treeItem = new TreeItemView();
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            List infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET); // 获得所有权限的子集
            String action = (String) hm.get("action");
            String target = (String) hm.get("target");
            String treetype = "org";// org,duty,employee,noum
            treeItem.setName("root");
            treeItem.setIcon("/images/unit.gif");
            String kind = "2";
            treeItem.setTarget(target);

            String rootdesc = "";
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            rootdesc = sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
            if (rootdesc == null || rootdesc.length() <= 0) {
                rootdesc = ResourceFactory.getProperty("tree.orgroot.orgdesc");
            }

            treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
            treeItem.setText(rootdesc);
            treeItem.setTitle(rootdesc);
            if (userView.isSuper_admin())
                treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + RegisterInitInfoData.getKqPrivCode(userView) + RegisterInitInfoData.getKqPrivCodeValue(userView));
            else {
                if (userView.getStatus() == 4 || userView.getStatus() == 0)
                    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + RegisterInitInfoData.getKqPrivCode(userView) + RegisterInitInfoData.getKqPrivCodeValue(userView));
                else
                    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + RegisterInitInfoData.getKqPrivCode(userView) + "no");
                if ("UN".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    kind = "2";
                else if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    kind = "1";
                else if ("@K".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    kind = "0";
            }
            treeItem.setAction(action + "?b_search=link&isroot=1&code=" + RegisterInitInfoData.getKqPrivCodeValue(userView) + "&kind=" + kind + "&root=1");
            String isFire = (String) hm.get("isFire");
            if (isFire != null && "noOpen".equals(isFire)) {
                this.getFormHM().put("treeCode", treeItem.toJS());
                hm.remove("isFire");
            } else
                this.getFormHM().put("treeCode", treeItem.toJS());

            /** 应用库过滤前缀符号 */
            ArrayList dblist = userView.getPrivDbList();
            StringBuffer cond = new StringBuffer();
            cond.append("select pre,dbname from dbname where pre in (");
            String userbase = "";
            if (dblist.size() > 0) {
                userbase = dblist.get(0).toString();
            } else
                userbase = "usr";
            for (int i = 0; i < dblist.size(); i++) {

                if (i != 0)
                    cond.append(",");
                cond.append("'");
                cond.append((String) dblist.get(i));
                cond.append("'");
            }
            if (dblist.size() == 0)
                cond.append("''");
            cond.append(")");
            cond.append(" order by dbid");
            /** 应用库前缀过滤条件 */
            cat.debug("-----userbase------>" + userbase);
            this.getFormHM().put("userbase", userbase);
            this.getFormHM().put("dbcond", cond.toString());
            this.getFormHM().put("setprv", getEditSetPriv(infoSetList, "A01"));

            String code = "";
            if (this.userView.isSuper_admin()) {
                code = "";
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                code = managePrivCode.getPrivOrgId();
            }

            String cur_d = cur_course.substring(0, 4);
            cur_d = cur_d + "-PT";
            ArrayList nbaseandscope = this.getNbaseAndScope(cur_d, code); // 进入页面显示上次统计的时间段
            String time = (String) nbaseandscope.get(1);
            if (time != null && !"".equals(time)) {
                cur_course = time.substring(0, 10);
                end_date = time.substring(time.length() - 10, time.length());
            }
            cur_course = cur_course.replaceAll("\\.", "-");
            end_date = end_date.replaceAll("\\.", "-");
            this.getFormHM().put("start_datetj", cur_course);
            this.getFormHM().put("end_datetj", end_date);

            KqParameter para = new KqParameter(this.userView, code, this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String two = (String) hashmap.get("nbase");
            ContentDAO dao = new ContentDAO(this.frameconn);
            ArrayList slist = new ArrayList();
            
            this.frowset = dao.search(cond.toString());
            String sum_pre = (String) nbaseandscope.get(0);
            String[] sum_pres = null;
            if (sum_pre.length() > 3)
                sum_pres = sum_pre.substring(0, sum_pre.length() - 1).split(",");
            else if (sum_pre.length() == 3)
                sum_pres = new String[] { sum_pre };
            
            String have_pre = "";
            String select_pre = "";
            
            CommonData vo = new CommonData("", "");
            slist.add(vo);
            
            vo = new CommonData("all", "全部人员库");
            slist.add(vo);
            
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre").toLowerCase();
                if ((two.toLowerCase()).indexOf(dbpre) != -1) {
                    have_pre += dbpre + ",";
                    vo = new CommonData(this.frowset.getString("pre"), this.frowset.getString("dbname"));
                    slist.add(vo);
                }
            }
            //没有人员库就不用加“全部人员库”了
            if (2==slist.size())
                slist.remove(1);
            
            if (sum_pres != null)
                for (int i = 0; i < sum_pres.length; i++) {
                    if (have_pre.indexOf(sum_pres[i].toLowerCase()) == -1 && !"".equals(sum_pres[i])) {
                        select_pre = "`";
                    } else {
                        select_pre = sum_pres[i];
                    }
                }
            else
                select_pre = "`";
            String[] have_pres = null;
            if (have_pre.length() > 3)
                have_pres = have_pre.substring(0, have_pre.length() - 1).split(",");
            else
                have_pres = new String[] {};
            if (!"`".equals(select_pre) && !"".equals(select_pre)) {
                if (sum_pres.length == have_pres.length)
                    select_pre = "all";
                else
                    select_pre = sum_pre;
            } else {
                select_pre = "";
            }
            this.formHM.put("slist", slist);
            this.formHM.put("dbpre", "all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
     * 
     * @param infoSetList
     * @param infoFieldSetList
     * @param setname
     * @return
     */
    private String getEditSetPriv(List infoSetList, String setname) {
        String setpriv = "1";
        /** 先根据子集分析 */
        for (int p = 0; p < infoSetList.size(); p++) {
            FieldSet fieldset = (FieldSet) infoSetList.get(p);
            if (setname.equalsIgnoreCase(fieldset.getFieldsetid())) {
                setpriv = String.valueOf(fieldset.getPriv_status());
                break;
            }
        }
        return setpriv;
    }

    public ArrayList getNbaseAndScope(String cur_d, String orgpre) {
        ArrayList str = new ArrayList();
        String nbase = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        try {
            sql.append("select scope,nbase from Q05 where Q03z0='" + cur_d + "' ");
            sql.append(" and (b0110 like '" + orgpre + "%' or e0122 like '" + orgpre + "%' or e01a1 like '" + orgpre + "%') ");
            sql.append("group by nbase,scope");
            rowSet = dao.search(sql.toString());
            Set nbaseset = new HashSet();
            Set scopeset = new HashSet();
            while (rowSet.next()) {
                nbaseset.add(rowSet.getString("nbase"));
                scopeset.add(rowSet.getString("scope"));
            }
            
            if (nbaseset.size() > 1) {
                for (Iterator i = nbaseset.iterator(); i.hasNext();) {
                    nbase += i.next() + ",";
                }
            } else if (nbaseset.size() > 0) {
                nbase = (String) (nbaseset.iterator().next());
            } else {
                nbase = "";
            }
            
            str.add(nbase);
            if (nbase.length() > 0)
                str.add((String) (scopeset.iterator().next()));
            else
                str.add("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }
}
