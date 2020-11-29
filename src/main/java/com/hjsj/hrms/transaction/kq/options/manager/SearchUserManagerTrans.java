package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchUserManagerTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String code = (String) hm.get("code");
            String kind = (String) hm.get("kind");
            String menu = (String) hm.get("menu");
            String sort = (String) hm.get("sort");
            hm.remove("code");
            hm.remove("kind");
            hm.remove("menu");
            hm.remove("sort");

            ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");
            if (menu != null && menu.length() > 0 && "1".equals(menu)) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                String kqcode = userView.getKqManageValue();
            	if (kqcode == null || kqcode.length() <= 0)
				{
            		code = managePrivCode.getPrivOrgId();
				}
            	else {
					code=RegisterInitInfoData.getKqPrivCodeValue(userView);
				}
                String orgPri = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UM".equalsIgnoreCase(orgPri))
                    kind = "1";
                else if ("@K".equalsIgnoreCase(orgPri))
                    kind = "0";
                else if ("UN".equalsIgnoreCase(orgPri))
                    kind = "2";

                hm.put("menu", "0");
            }

            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            if (code == null || code.length() <= 0) {
            	String kqcode = userView.getKqManageValue();
            	if (kqcode == null || kqcode.length() <= 0)
				{
            		code = managePrivCode.getPrivOrgId();
				}
            	else {
					code=RegisterInitInfoData.getKqPrivCodeValue(userView);
				}
                
                String orgPri = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UM".equalsIgnoreCase(orgPri)) {
                    kind = "1";
                } else if ("UN".equalsIgnoreCase(orgPri)) {
                    kind = "2";
                } else {
                    kind = "";
                }
            } else if (kind == null || kind.length() <= 0) {
            	String kqcode = userView.getKqManageValue();
            	if (kqcode == null || kqcode.length() <= 0)
				{
            		code = managePrivCode.getPrivOrgId();
				}
            	else {
					code=RegisterInitInfoData.getKqPrivCodeValue(userView);
				}
                String orgPri = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UM".equalsIgnoreCase(orgPri))
                    kind = "1";
                else if ("UN".equalsIgnoreCase(orgPri))
                    kind = "2";
            } else {
                if (AdminCode.getCode("UM", code) != null)
                    kind = "1";
                else if (AdminCode.getCode("UN", code) != null)
                    kind = "2";
            }
            
            if ("1".equals(kind)) {
                this.getFormHM().put("kq_code", "UM" + code);
            } else if ("0".equals(kind)) {
            	this.getFormHM().put("kq_code", "@K" + code);
			} else {
                this.getFormHM().put("kq_code", "UN" + code);
            }

            // issearch=1 是页面中点击查询进入 select_name为查询的人员姓名
            String select_name = "";
            String isSearch = (String) hm.get("issearch");
            hm.remove("issearch");
            if (null != isSearch && "1".equals(isSearch)) {
                select_name = (String) this.getFormHM().get("select_name");
            } else {
                hm.remove("select_name");
            }
            this.getFormHM().put("select_name", select_name);
            
            String select_pre = (String) this.getFormHM().get("select_pre");
            this.getFormHM().put("select_pre", select_pre);
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            
            String select_flag = (String) hm.get("select_flag");
            String where_c = "";
            if ("2".equals(select_flag)) {
                String whereIN = (String) hm.get("selectResult");
                where_c = " AND" + new CodingAnalytical().analytical(whereIN);
            } else {
                where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
            }
            hm.remove("select_flag");
            
            KqParameter kq_paramter = new KqParameter(this.userView, code, this.getFrameconn());
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String kq_gno = (String) hashmap.get("g_no");
            //		ArrayList dblist=RegisterInitInfoData.getDbList(code,kind,this.getFormHM(),this.userView,this.getFrameconn());
            ArrayList dblist = kqUtilsClass.setKqPerList(code, kind);
            UserManager userManager = new UserManager(this.userView, this.getFrameconn());
            fieldlist = userManager.getFieldList(kq_type, kq_cardno, kq_gno);
            
            ArrayList sql_db_list = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                sql_db_list.add(select_pre);
            } else {
                sql_db_list = dblist;
            }
            
            String strsql = getQueryString(sql_db_list, code, kind, fieldlist, kq_type, kq_cardno, kq_gno, where_c);
            String columns = userManager.getDisplayColumns(fieldlist, kq_type, kq_cardno, kq_gno);
            this.getFormHM().put("typelist", userManager.getKqTypeList(this.getFrameconn()));
            this.getFormHM().put("strsql", strsql);
            this.getFormHM().put("columns", columns);
            this.getFormHM().put("code", code);
            this.getFormHM().put("kind", kind);
            this.getFormHM().put("kq_type", kq_type);
            this.getFormHM().put("kq_cardno", kq_cardno);
            this.getFormHM().put("kq_gno", kq_gno);
            this.getFormHM().put("manageWhere", managerWhere(kind, code));
            this.getFormHM().put("nbaselist", dblist);
            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(dblist));

            String orderby = kqUtilsClass.getSortOrderBY(sort);
            if (sql_db_list != null && sql_db_list.size() > 0) {
                if (orderby == null || orderby.length() <= 0)
                    orderby = "order by i,a0000";
            } else
                orderby = "";
            this.getFormHM().put("orderby", orderby);

            this.getFormHM().put("fieldlist", fieldlist);

            String magcard_flag = (String) this.getFormHM().get("magcard_flag");
            if (magcard_flag == null || magcard_flag.length() <= 0)
                magcard_flag = KqParam.getInstance().getMagcardFlag();
            this.getFormHM().put("magcard_flag", magcard_flag);

            String magcard_cardid = (String) this.getFormHM().get("magcard_cardid");
            if (magcard_cardid == null || magcard_cardid.length() <= 0) {
                magcard_cardid = KqParam.getInstance().getMagcardCardId();
            }
            this.getFormHM().put("magcard_cardid", magcard_cardid);

            String dbType = Sql_switcher.searchDbServer() + "";
            this.getFormHM().put("dbType", dbType);

            //显示部门层数
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 组合查询SQL串
     * @param type
     * @param fieldlist
     * @return
     */
    private String getQueryString(ArrayList dblist, String code, String kind, ArrayList fieldlist, String kq_type,
            String kq_cardno, String kq_gno, String where_c) throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        if (kq_cardno == null || kq_cardno.length() <= 0)
            kq_cardno = "";
        if (kq_gno == null || kq_gno.length() <= 0)
            kq_gno = "";
        if (kq_type == null || kq_type.length() <= 0)
            kq_type = "";
        try {

            if (dblist.size() <= 0 || dblist == null) {
                this.getFormHM().put("codenull", "1");
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                        .getProperty("kq.param.nosave.userbase"), "", ""));
            } else {
                for (int i = 0; i < dblist.size(); i++) {
                    String nbase = (String) dblist.get(i);
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    strsql.append("select " + i + " as i,a0000,");

                    //strsql.append("b0110,e0122,e01a1,a0100,a0101,");

                    for (int j = 0; j < fieldlist.size(); j++) {
                        FieldItem item = (FieldItem) fieldlist.get(j);
                        String name = item.getItemid();
                        if ("nbase".equalsIgnoreCase(name)) {
                            strsql.append("'" + nbase);
                            strsql.append("' as nbase,");
                        } else if (kq_cardno != null && kq_cardno.length() > 0 && "t1".equals(name)) {
                            strsql.append(kq_cardno);
                            strsql.append(" ");
                            strsql.append(item.getItemid());
                            strsql.append(",");

                            continue;
                        } else if (kq_gno != null && kq_gno.length() > 0 && "t2".equals(name)) {
                            strsql.append(kq_gno);
                            strsql.append(" ");
                            strsql.append(item.getItemid());
                            strsql.append(",");

                            continue;
                        } else if (kq_type != null && kq_type.length() > 0 && "t3".equals(name)) {
                            strsql.append(kq_type);
                            strsql.append(" ");
                            strsql.append(item.getItemid());
                            strsql.append(",");

                            continue;
                        } else if (!item.getItemid().equalsIgnoreCase(kq_type) && !item.getItemid().equalsIgnoreCase(kq_gno)
                                && !item.getItemid().equalsIgnoreCase(kq_cardno)) {
                            strsql.append(item.getItemid() + ",");

                        }
                    }
                    strsql.setLength(strsql.length() - 1);
                    strsql.append(" from ");
                    strsql.append(nbase);
                    strsql.append("a01");
                    strsql.append(" where ");
                    if ("1".equals(kind)) {
                        strsql.append("e0122 like '" + code + "%'");
                    } else if ("0".equals(kind)) {
                        strsql.append("e01a1 like '" + code + "%'");
                    } else {
                        strsql.append("b0110 like '" + code + "%'");
                        //strsql.append("b0110 like '214%'");
                    }
                    if (where_c != null && where_c.length() > 0) {
                        where_c = this.changeWhere_c(kq_type, kq_cardno, kq_gno, where_c);
                        strsql.append(" " + where_c);
                    }
                    strsql.append(" and exists (select 1 from (select a0100 " + whereIN + ")  A where A.a0100=" + nbase + "a01.a0100)");
                    strsql.append(" UNION ");
                }
                strsql.setLength(strsql.length() - 7);
                this.getFormHM().put("codenull", "0");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return strsql.toString();
    }

    private String managerWhere(String kind, String code) {
        StringBuffer where = new StringBuffer();
        if ("1".equals(kind)) {
            where.append("e0122 like '" + code + "%'");
        } else if ("0".equals(kind)) {
            where.append("e01a1 like '" + code + "%'");
        } else {
            where.append("b0110 like '" + code + "%'");
        }
        return where.toString();
    }

    private String changeWhere_c(String kq_type, String kq_cardno, String kq_gno, String where_c) {
        for (int i = 1; i < 4; i++) {
            if (i == 1 && kq_cardno != null && kq_cardno.length() > 0 && where_c.indexOf("t" + (i)) != -1) {
                where_c = where_c.replace("t" + (i), kq_cardno);
                continue;
            } else if (i == 2 && kq_gno != null && kq_gno.length() > 0 && where_c.indexOf("t" + (i)) != -1) {
                where_c = where_c.replace("t" + (i), kq_gno);
                continue;
            } else if (i > 2 && kq_type != null && kq_type.length() > 0 && where_c.indexOf("t" + (i)) != -1) {
                where_c = where_c.replace("t" + (i), kq_type);
                continue;
            }
        }
        return where_c;
    }

}
