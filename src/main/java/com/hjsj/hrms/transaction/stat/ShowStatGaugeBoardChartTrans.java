package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ShowStatGaugeBoardChartTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String statId = (String) this.getFormHM().get("statid");
        //String chart_type = (String) this.getFormHM().get("chart_type");
        String statImgType = (String) hm.get("statimgtype");
        String sformula = (String) this.getFormHM().get("sformula");
        if (sformula == null || sformula.length() < 1)
            sformula = (String) hm.get("sformula");
        String fromwhere = (String) this.getFormHM().get("fromwhere");
        this.getFormHM().remove("fromwhere");
        String year = (String) this.getFormHM().get("year");
        this.getFormHM().remove("year");
        if (year == null || year.length() < 1) {
            year = (String) hm.get("year");
            hm.remove("year");
        }

        String parentid = (String) hm.get("parentid");
        String lexprName = (String) hm.get("lexprName");
        String treeuncode = ((String) hm.get("a_code"));
        String notem = ((String) hm.get("notem"));
        if (notem != null) {
            this.formHM.put("notem", notem);
        }
        if (lexprName != null && lexprName.length() > 0)
            lexprName = SafeCode.decode(lexprName);

        hm.remove("lexprName");
        String showpage = (String) hm.get("page");
        sformula = sformula == null ? "" : sformula;
        boolean definedSformula = hm.containsKey("sformula");
        hm.remove("sformula");
        if (sformula.length() == 0 && !definedSformula) // 安徽高速，设置了统计方式的，默认定位第一个
            sformula = getFirstSformula(statId);

        String years = (String) hm.get("years");
        hm.remove("years");
        String SNameDisplay;
        // 统计图联动的条件
        String where = "";
        String infokind = "";// 信息群
        String userbase = "";// 人员库
        ArrayList dblist = new ArrayList();
        String history = "";
        boolean isresult = true;
        if (statId == null || statId.length() <= 0)
            return;
        String sql1 = "select * from sname where id='" + statId + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String condid = "";
        try {
            this.frowset = dao.search(sql1);
            if (this.frowset.next()) {
                infokind = this.frowset.getString("infokind");
                userbase = this.frowset.getString("nbase");
                userbase = userbase == null || "null".equalsIgnoreCase(userbase) ? "" : userbase;
                if (userbase != null && userbase.length() > 0) {
                    String[] baseS = userbase.split(",");
                    for (int i = 0; i < baseS.length; i++) {
                        if (baseS[i] != null && baseS[i].length() > 0) {
                            if (this.userView.hasTheDbName(baseS[i]))
                                dblist.add(baseS[i]);
                        }
                    }
                }
                if (dblist == null || dblist.size() <= 0)
                    dblist = this.userView.getPrivDbList();
                String flag = this.frowset.getString("flag");
                if (flag != null && "1".equals(flag))
                    isresult = false; // false时才查询，查询结果表
                condid = this.frowset.getString("condid");
            }
            this.formHM.put("infokind", infokind);
            this.getFormHM().put("dblist", dblist);
            if (dblist != null && dblist.size() > 0) {
                this.formHM.put("userbases", userbase.replaceAll(",", "`"));
                this.formHM.put("userbase", dblist.get(0));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList condlist = getCondlist(condid, dao);
        this.formHM.put("condlist", condlist);
        // liuy 2015-1-6 6516：领导桌面：人员结构的分类中，选择什么都显示全部人员，下面的统计图能够按照选择的正确显示 start
        String lexprId = (String) this.getFormHM().get("lexprId");// 常用查询条件
        if (StringUtils.isEmpty(lexprId))
            lexprId = (String) hm.get("lexprId");
        if (StringUtils.isNotEmpty(lexprId))
            this.formHM.put("lexprId", lexprId);
        // liuy end
        if (StringUtils.isEmpty(lexprId)) {
            if (condlist != null && condlist.size() > 0) {
                CommonData da = (CommonData) condlist.get(0);
                lexprId = da.getDataValue();
            }
        }
        String[] curr_id = null;
        String commlexr = null;
        String commfacor = null;
        if (lexprId != null && lexprId.length() > 0) {
            // 加上常用查询进行的统计
            curr_id = new String[1];
            curr_id[0] = lexprId;
        }
        if (curr_id != null && curr_id.length > 0) {
            GeneralQueryStat generalstat = new GeneralQueryStat();
            generalstat.getGeneralQueryLexrfacor(curr_id, userbase, "", this.getFrameconn());
            commlexr = generalstat.getLexpr();
            commfacor = generalstat.getLfactor();
            history = generalstat.getHistory();
        }

        int[] statvalues = null;
        double[] statvaluess = null;
        String[] fieldDisplay;
        String preresult = "";
        ArrayList list = new ArrayList();
        StatDataEncapsulation simplestat = new StatDataEncapsulation();
        String querycond = (String) this.getFormHM().get("querycond");// 组织机构
        String orgName = "";
        try {
            if (querycond == null || querycond.length() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String backdate = sdf.format(new Date());
                String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid=parentid  and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date";
                if (!this.userView.isSuper_admin()) {
                    String manage = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
                    if (manage.length() > 2) {
                        sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid='" + this.userView.getManagePrivCodeValue() + "'  and " + Sql_switcher.dateValue(backdate)
                                + " between start_date and end_date";
                    } else if (manage.length() == 2) {
                        sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid=parentid  and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date";
                    } else {
                        sql = "select codesetid,codeitemid,codeitemdesc from organization where 1=2  and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date";
                    }
                }
                this.frowset = dao.search(sql);
                int index = 0;
                while (this.frowset.next()) {
                    if (index == 1) {
                        querycond = "";
                        orgName = "";
                        break;
                    }
                    querycond = this.frowset.getString("codesetid") + this.frowset.getString("codeitemid");
                    orgName = this.getFrowset().getString("codeitemdesc");
                    index++;
                }
                this.getFormHM().put("querycond", querycond);
                this.getFormHM().put("orgName", orgName);
            } else if ("root".equalsIgnoreCase(querycond)) {
                querycond = "";
            }

            if (sformula.length() > 0) {
                SformulaXml xml = new SformulaXml(this.frameconn, statId);
                Element element = xml.getElement(sformula);
                if (element == null) {
                    if ("pcw".equalsIgnoreCase(showpage)) {
                        List sformulalist = xml.getAllChildren();
                        sformula = setSformula(sformulalist);
                    } else {
                        sformula = "";
                        this.getFormHM().put("decimalwidth", "0");
                        this.getFormHM().put("isneedsum", "true");
                    }
                } else {
                    String decimalwidth = element.getAttributeValue("decimalwidth");
                    decimalwidth = (decimalwidth == null || decimalwidth.length() == 0) ? "2" : decimalwidth;
                    this.getFormHM().put("decimalwidth", decimalwidth);
                    String type = element.getAttributeValue("type");
                    if ("sum".equalsIgnoreCase(type) || "count".equalsIgnoreCase(type))
                        this.getFormHM().put("isneedsum", "true");
                    else
                        this.getFormHM().put("isneedsum", "false");
                }
            } else {
                this.getFormHM().put("decimalwidth", "0");
                this.getFormHM().put("isneedsum", "true");
            }

            // 只有单位统计才需要联动
            if ("2".equalsIgnoreCase(infokind)) {
                if ("pcw".equalsIgnoreCase(showpage) || "rate".equalsIgnoreCase(showpage)) {
                    if (!statId.equalsIgnoreCase(parentid)) {
                        where = getLexpr(parentid, lexprName, infokind);
                        if (where != null && where.length() > 1)
                            where = where.replaceAll("usrA01", "B01");
                    }
                } else if ("tree".equals(showpage)) {
                    if (treeuncode != null) {
                        if (treeuncode.contains("*")) {
                            treeuncode = treeuncode.replace("UN", "");
                            where = " and B01.B0110 like '" + treeuncode + "%'";
                        } else {
                            treeuncode = treeuncode.replace("UN", "");
                            where = " and B01.B0110 = '" + treeuncode + "'";
                        }

                    } else
                        where = " and B01.B0110 = '" + "01'";
                }else if(fromwhere!=null&&fromwhere.length() > 0){
                    if (treeuncode != null) {
                        if (treeuncode.contains("*")) {
                            treeuncode = treeuncode.replace("UN", "");
                            where = " and B01.B0110 like '" + treeuncode + "%'";
                        } else {
                            treeuncode = treeuncode.replace("UN", "");
                            where = " and B01.B0110 = '" + treeuncode + "'";
                        }

                    } else
                        where = " and B01.B0110 = '" + "01'";
                }
            }
            String result = (String)this.getFormHM().get("result");
            if (sformula.length() > 0)
                statvaluess = simplestat.getLexprDataSformula(dblist, Integer.parseInt(statId), querycond, userView.getUserName(), userView.getManagePrivCode(), userView, infokind, isresult,
                        commlexr, commfacor, preresult, history, sformula, this.frameconn, year, where, years,showpage,parentid,statId,lexprName,treeuncode,fromwhere,result);
            else
                statvalues = simplestat.getLexprData(dblist, Integer.parseInt(statId), querycond, userView.getUserName(), userView.getManagePrivCode(), userView, infokind, isresult, commlexr,
                        commfacor, preresult, history, year, where, years);
            SNameDisplay = simplestat.getSNameDisplay();
            if ((sformula.length() == 0 && statvalues != null && statvalues.length > 0) || (sformula.length() > 0 && statvaluess != null && statvaluess.length > 0)) {
                fieldDisplay = simplestat.getDisplay();

                if (years != null && years.length() > 0)
                    fieldDisplay = simplestat.getYearDisplay(Integer.valueOf(year), years);

                int statTotal = 0;
                double statTotals = 0;
                if (sformula.length() == 0)
                    for (int i = 0; i < statvalues.length; i++) {
                        CommonData vo = new CommonData();
                        String str = fieldDisplay[i];
                        vo.setDataName(str);
                        vo.setDataValue(String.valueOf(statvalues[i]));
                        list.add(vo);
                        statTotal += statvalues[i];
                    }
                else
                    for (int i = 0; i < statvaluess.length; i++) {
                        CommonData vo = new CommonData();
                        String str = fieldDisplay[i];
                        vo.setDataName(str);
                        vo.setDataValue(String.valueOf(statvaluess[i]));
                        list.add(vo);
                        statTotal += statvaluess[i];
                    }
                this.getFormHM().put("snamedisplay", SNameDisplay);
                this.getFormHM().put("list", list);
            } else {
                StringBuffer sql = new StringBuffer();
                sql.append("select * from SName where id=");
                sql.append(statId);
                List rs = ExecuteSQL.executeMyQuery(sql.toString());
                if (!rs.isEmpty()) {
                    LazyDynaBean rec = (LazyDynaBean) rs.get(0);
                    SNameDisplay = rec.get("name") != null ? rec.get("name").toString() : "";
                }
                CommonData vo = new CommonData();
                vo.setDataName("");
                vo.setDataValue("0");
                list.add(vo);
                this.getFormHM().put("snamedisplay", SNameDisplay);
                this.getFormHM().put("list", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("", e.toString(), "", "");
        }
        /* 领导桌面组织单元是否可选的开关 xiaoyun 2014-5-15 start */
        String isHideBiPanelOrg = SystemConfig.getPropertyValue("isHideBiPanelOrg");
        if (StringUtils.isNotEmpty(isHideBiPanelOrg)) {
            this.getFormHM().put("isHideBiPanelOrg", isHideBiPanelOrg.trim());
        }
        /* 领导桌面组织单元是否可选的开关 xiaoyun 2014-5-15 end */
        HashMap jfreemap = new HashMap();
        jfreemap.put(SNameDisplay, list);
        this.getFormHM().put("jfreemap", jfreemap);
        String archive_set = "";
        String viewtype = "12";
        StringBuffer sql = new StringBuffer();
        sql.append("select archive_set,viewtype from SName where id=");
        sql.append(statId);
        List rs = ExecuteSQL.executeMyQuery(sql.toString());
        if (!rs.isEmpty()) {
            LazyDynaBean rec = (LazyDynaBean) rs.get(0);
            archive_set = rec.get("archive_set") != null ? rec.get("archive_set").toString() : "";
            viewtype = rec.get("viewtype") != null ? rec.get("viewtype").toString() : "";
        }
        
        if(StringUtils.isNotEmpty(statImgType)){
        	if("1".equals(statImgType))
        		viewtype ="11";
        	else if("2".equals(statImgType))
                viewtype ="1000";
        }
        
        if ("42".equals(viewtype) || "43".equals(viewtype) || "44".equals(viewtype)) {
            ArrayList valvesList = new ArrayList();
            valvesList = getValve(userbase.replaceAll(",", "`"), userbase.toUpperCase(), statId, querycond, userView.getUserName(), userView.getManagePrivCode(), userView, infokind, isresult,
                    commlexr, commfacor, preresult, history, sformula, this.frameconn, where);
            if (valvesList.size() == 3) {
                if (valvesList.get(0) != null && valvesList.get(1) != null && valvesList.get(2) != null && ("42".equals(viewtype) || "43".equals(viewtype) || "44".equals(viewtype))) {
                    this.getFormHM().put("minvalue", valvesList.get(0));
                    this.getFormHM().put("maxvalue", valvesList.get(1));
                    this.getFormHM().put("valves", valvesList.get(2));
                } else {
                    ArrayList minv = new ArrayList();
                    minv.add("0");
                    this.getFormHM().put("minvalue", minv);
                    this.getFormHM().put("maxvalue", minv);
                    this.getFormHM().put("valves", minv);
                }
            } else {
                ArrayList minv = new ArrayList();
                minv.add("0");
                ArrayList maxv = new ArrayList();
                maxv.add("0");
                ArrayList valv = new ArrayList();
                valv.add("0");
                this.getFormHM().put("minvalue", minv);
                this.getFormHM().put("maxvalue", maxv);
                this.getFormHM().put("valves", valv);
            }
        }
        this.getFormHM().put("queryconde", querycond);
        this.getFormHM().put("chart_type", viewtype);
        this.getSformula(statId);
        this.getFormHM().put("sformula", sformula);
        String xangle = AnychartBo.computeXangle(list);
        this.getFormHM().put("xangle", xangle);
        /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 start */
        this.getFormHM().put("total", getSumLength(list) + "");
        /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 end */
    }

    /**
     * 获取该集合中字符串长度之和
     * 
     * @param list
     * @return
     * @author xiaoyun 2014-7-7
     */
    private int getSumLength(ArrayList list) {
        int sum = 0;
        CommonData cd = null;
        for (int i = 0; i < list.size(); i++) {
            cd = (CommonData) list.get(i);
            if (StringUtils.isNotEmpty(cd.getDataName())) {
                sum += cd.getDataName().length();
            }
        }
        return sum;
    }

    private ArrayList getCondlist(String condid, ContentDAO dao) {
        ArrayList list = new ArrayList();
        CommonData da = new CommonData();

        if (condid != null && condid.length() > 0) {
            String[] condids = condid.split(",");
            RowSet rs = null;
            try {
                for (int i = 0; i < condids.length; i++) {
                    String sql = "select id,name from lexpr where id='" + condids[i] + "'";
                    rs = dao.search(sql);
                    if (rs.next()) {
                        if (this.userView.isHaveResource(2, rs.getInt("id") + "")) {
                            da = new CommonData();
                            da.setDataValue(rs.getInt("id") + "");
                            da.setDataName(rs.getString("name"));
                            list.add(da);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null)
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        }
        return list;
    }

    /**
     * 取第一个统计方式
     * 
     * @param statid
     * @return
     */
    private String getFirstSformula(String statid) {
        SformulaXml xml = new SformulaXml(this.frameconn, statid);
        List list = xml.getAllChildren();
        String sformula = "";
        if (list != null && list.size() > 0) {
            Element element = (Element) list.get(0);
            sformula = element.getAttributeValue("id");
        }
        return sformula;
    }

    private void getSformula(String statid) {
        SformulaXml xml = new SformulaXml(this.frameconn, statid);
        List list = xml.getAllChildren();
        ArrayList sformulalist = new ArrayList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                CommonData cd = new CommonData(element.getAttributeValue("id"), element.getAttributeValue("title"));
                sformulalist.add(cd);
            }
            CommonData cd = new CommonData("", ResourceFactory.getProperty("kq.formula.count")); // 安徽高速，个数放最后
            sformulalist.add(cd);
            this.getFormHM().put("sformulalist", sformulalist);
            this.getFormHM().put("showsformula", "1");
        } else {
            this.getFormHM().put("showsformula", "0");
        }
    }

    /**
     * 获取各个阀值
     * 
     * valvesList get（0） String 最小值 get（1） String 最大值 get（2） ArrayList 阀值
     * 
     * 
     * */
    private ArrayList getValve(String userbases, String userbase, String statId, String querycond, String username, String manageprive, UserView userView, String infokind, boolean isresult,
            String commlexr, String commfacor, String preresult, String history, String sformula, Connection conn, String where) {
        String valvetype = "0";
        ArrayList valvesList = new ArrayList();
        ArrayList valves = new ArrayList();
        int[] statvalues = null;
        double[] statvaluess = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select valve from SLegend where id=");
        sql.append(statId);
        List rs = ExecuteSQL.executeMyQuery(sql.toString());
        StatDataEncapsulation simplestat = new StatDataEncapsulation();
        if (!rs.isEmpty()) {
            LazyDynaBean rec = (LazyDynaBean) rs.get(0);
            String valve = rec.get("valve") != null ? rec.get("valve").toString() : "";
            if (valve == null || "".equals(valve) || valve == "") {
            } else {
                try {
                    Document doc = PubFunc.generateDom(valve.toString());
                    Element et = doc.getRootElement();
                    ;
                    ArrayList minvalue = new ArrayList();
                    minvalue.add(((Element) et.getChild("minValue")).getText());
                    ArrayList maxvalue = new ArrayList();
                    maxvalue.add(((Element) et.getChild("maxValue")).getText());
                    List valveList = et.getChild("valves").getChildren();
                    for (int i = 0; i < valveList.size(); i++) {
                        valves.add(((Element) valveList.get(i)).getText());
                    }
                    valvesList.add(minvalue);
                    valvesList.add(maxvalue);
                    valvesList.add(valves);

                } catch (JDOMException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (Exception e) {
                	e.printStackTrace();
				}
                try {
                    if (userbases == null || userbases.length() == 0) {
                        if (userbase == null || userbase.length() == 0) {

                        }
                        this.getFormHM().put("userbases", userbase);
                        /*
                         * ContentDAO dao = new ContentDAO(this.frameconn); try
                         * { this.frowset =
                         * dao.search("select dbname from DBName where upper(pre)='"
                         * +userbase.toUpperCase()+"'");
                         * if(this.frowset.next()){
                         * this.getFormHM().put("viewuserbases",
                         * this.frowset.getString("dbname")); } } catch
                         * (SQLException e) { e.printStackTrace(); }
                         */
                        this.getFormHM().put("viewuserbases", AdminCode.getCodeName("@@", userbase.toUpperCase()));
                        if (sformula.length() > 0)
                            statvaluess = simplestat.getLexprDataSformula(userbase, Integer.parseInt(statId), querycond, username, manageprive, userView, infokind, isresult, commlexr, commfacor,
                                    preresult, history, sformula, this.frameconn, valvetype, valves, userbases, where);// getLexprDataSformula(userbase.toUpperCase(),
                                                                                                                       // Integer.parseInt(statId),
                                                                                                                       // querycond,
                                                                                                                       // userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn,valvetype,valves);
                        else
                            statvalues = simplestat.getLexprData(userbase, Integer.parseInt(statId), querycond, userView.getUserName(), userView.getManagePrivCode(), userView, infokind, isresult,
                                    commlexr, commfacor, preresult, history);
                    } else {
                        String[] nbases = userbases.split("`");
                        if (userbase == null || userbase.length() < 1)
                            userbase = nbases[0];

                        StringBuffer viewbase = new StringBuffer();
                        for (int i = 0; i < nbases.length; i++) {
                            viewbase.append(";" + AdminCode.getCodeName("@@", nbases[i].toUpperCase()));
                        }
                        if (viewbase.length() > 1)
                            this.getFormHM().put("viewuserbases", viewbase.substring(1));
                        if (sformula.length() > 0)
                            statvaluess = simplestat.getLexprDataSformula(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(), userView.getManagePrivCode(), userView,
                                    infokind, isresult, commlexr, commfacor, preresult, history, userbases, sformula, this.frameconn, valvetype, valves, where);
                        else
                            statvalues = simplestat.getLexprData(userbase.toUpperCase(), Integer.parseInt(statId), querycond, userView.getUserName(), userView.getManagePrivCode(), userView, infokind,
                                    isresult, commlexr, commfacor, preresult, history, userbases);
                    }
                    valves.add(statvaluess[0] + "");
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        return valvesList;
    }

    /**
     * 获取联动时点击图例的统计条件 注：此方法中的人员库固定为usr，只用于传递参数，目前只有单位的统计图会有联动
     * 
     * @param statid
     *            点击图例的统计id
     * @param lexprName
     *            统计条件的名称
     * @param infokind
     *            是单位还是人员的统计
     * @return
     */
    private String getLexpr(String statid, String lexprName, String infokind) {
        if (statid == null || statid.length() < 1)
            return "";

        String wheresql = "";
        try {
            if (lexprName != null && lexprName.length() > 0) {
                lexprName = lexprName.replaceAll("\n", "");
                lexprName = lexprName.replaceAll("\r", "");
            }
            LazyDynaBean bean = getStatDataForName(statid, lexprName);
            String history = (String) bean.get("history");
            String strlexpr = (String) bean.get("lexpr");
            String strfactor = (String) bean.get("factor");
            strfactor = strfactor + "`";
            strfactor = PubFunc.keyWord_reback(strfactor);
            StatCondAnalyse cond = new StatCondAnalyse();
            boolean ishavehistory = false;
            if (history != null && "1".equals(history))
                ishavehistory = true;

            String querycond = (String) this.getFormHM().get("querycond");

            boolean isresult = true;
            String result = (String) this.getFormHM().get("result");
            if (result == null || "".equals(result)) {
                StringBuffer sql = new StringBuffer();
                sql.append("select flag from SName where id=");
                sql.append(statid);
                List rs = ExecuteSQL.executeMyQuery(sql.toString());
                if (!rs.isEmpty()) {
                    LazyDynaBean rec = (LazyDynaBean) rs.get(0);
                    String flag = rec.get("flag") != null ? rec.get("flag").toString() : "";
                    if (flag != null && "1".equals(flag))
                        isresult = false; // false时才查询，查询结果表
                }
            } else if ("1".equals(result))
                isresult = false;

            wheresql = cond.getCondQueryString(strlexpr, strfactor, "usr", ishavehistory, userView.getUserName(), querycond, userView, infokind, isresult, false);
            if (wheresql.indexOf("WHERE") != -1)
                wheresql = wheresql.substring(wheresql.indexOf("WHERE") + 5);

            if (wheresql.trim().length() > 0) {
                wheresql = " and " + wheresql;
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return wheresql;

    }

    /**
     * 获取查询条件
     * 
     * @param statid
     *            统计图id
     * @param name
     *            统计图中某个查询条件的名字
     * @return
     */
    private LazyDynaBean getStatDataForName(String statid, String name) {
        String sql = "select * from slegend where id=" + statid;
        if (name != null && name.length() > 0)
            sql = sql + " and legend='" + name + "'";

        sql += " order by norder";

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        LazyDynaBean bean = new LazyDynaBean();
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                bean.set("lexpr", this.frowset.getString("lexpr") != null ? this.frowset.getString("lexpr") : "");
                bean.set("factor", this.frowset.getString("factor") != null ? this.frowset.getString("factor") : "");
                bean.set("norder", this.frowset.getString("norder") != null ? this.frowset.getString("norder") : "");
                bean.set("history", this.frowset.getString("flag") != null ? this.frowset.getString("flag") : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取查询条件
     * 
     * @param statid
     *            统计图id
     * @param name
     *            统计图中某个查询条件的名字
     * @return
     */
    private String setSformula(List list) {
        String sformula = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                String del = element.getAttributeValue("del");
                if ("1".equalsIgnoreCase(del))
                    continue;

                if (element != null) {
                    sformula = element.getAttributeValue("id");
                    String decimalwidth = element.getAttributeValue("decimalwidth");
                    decimalwidth = (decimalwidth == null || decimalwidth.length() == 0) ? "2" : decimalwidth;
                    this.getFormHM().put("decimalwidth", decimalwidth);
                    String type = element.getAttributeValue("type");
                    if ("sum".equalsIgnoreCase(type) || "count".equalsIgnoreCase(type))
                        this.getFormHM().put("isneedsum", "true");
                    else
                        this.getFormHM().put("isneedsum", "false");

                    break;
                }
            }
        }

        if (sformula == null || sformula.length() < 1) {
            sformula = "";
            this.getFormHM().put("decimalwidth", "0");
            this.getFormHM().put("isneedsum", "true");
        }

        return sformula;
    }
}
