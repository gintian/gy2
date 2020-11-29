package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p>Title:SearchHistoryStaticTrans.java</p>
 * <p>Description>:SearchHistoryStaticTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 23, 2010 10:37:12 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */

public class SearchHistoryStaticTrans extends IBusiness {

    private ArrayList yearlist  = new ArrayList();
    private ArrayList jfreelist = new ArrayList();
    private HashMap   jfreemap  = new HashMap();

    public void execute() throws GeneralException {
        String statid = (String) this.getFormHM().get("statid");
        String graph_style = (String) this.getFormHM().get("graph_style");
        graph_style = graph_style != null && graph_style.length() > 0 ? graph_style : "1";
        String acode = (String) this.getFormHM().get("acode");
        if (statid == null || statid.length() <= 0 || "-1".equalsIgnoreCase(statid))
            throw GeneralExceptionHandler.Handle(new GeneralException("统计项错误！或没有统计历史数据！"));
        
        ArrayList orglist = getOrgids(acode);
        if (orglist == null || orglist.size() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("请选择单位部门！"));
        
        String archive_type = "";
        String archive_set = "";
        String sname = "";
        String sql = "select name,archive_type,archive_set from sname where id='" + statid + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                sname = this.frowset.getString("name");
                archive_type = this.frowset.getString("archive_type");//归档周期
                archive_set = this.frowset.getString("archive_set");//归档信息集,单位一般按月变化子集
            }	

            FieldSet fs = DataDictionary.getFieldSetVo(archive_set);//归档信息集必须是按年月变化子集 zhangcq 2016-5-13
            String changeFlag = fs.getChangeflag();
            if(StringUtils.isEmpty(changeFlag) || "0".equalsIgnoreCase(changeFlag))
            	throw new GeneralException("归档信息集不是按月或按年变化子集！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String moreun = "false";//多个部门
        if (orglist.size() > 1)
            moreun = "true";
        
        this.getFormHM().put("moreun", moreun);
        String cyc_Sdate = "";
        String cyc_Edate = "";
        if ("true".equals(moreun)) {
            if ("4".equals(archive_type)) {
                String cyc_year = (String) this.getFormHM().get("cyc_year");
                if (cyc_year == null || cyc_year.length() <= 0) {
                    ArrayList yylist = cycleYearList(orglist, archive_set);
                    if (yylist != null && yylist.size() > 0) {
                        CommonData da = (CommonData) yylist.get(yylist.size() - 1);
                        cyc_year = da.getDataName();
                    } else {
                        cyc_year = PubFunc.getStringDate("yyyy");
                        CommonData da = new CommonData(cyc_year, cyc_year);
                        yylist.add(da);
                    }
                    this.getFormHM().put("yylist", yylist);
                    this.getFormHM().put("cyc_year", cyc_year);
                }
                cyc_Sdate = cyc_year + "-01-" + "01";
                cyc_Edate = cyc_year + "-" + "12" + "-" + "31";
            } else {
                String cyc_year = (String) this.getFormHM().get("cyc_year");
                String cyc_moth = (String) this.getFormHM().get("cyc_moth");
                if (cyc_year == null || cyc_year.length() <= 0) {
                    ArrayList yylist = cycleYearList(orglist, archive_set);
                    if (yylist != null && yylist.size() > 0) {
                        CommonData da = (CommonData) yylist.get(yylist.size() - 1);
                        cyc_year = da.getDataName();
                    } else {
                        cyc_year = PubFunc.getStringDate("yyyy");
                        CommonData da = new CommonData(cyc_year, cyc_year);
                        yylist.add(da);
                    }
                    this.getFormHM().put("yylist", yylist);
                    this.getFormHM().put("cyc_year", cyc_year);
                }
                if (cyc_moth == null || cyc_moth.length() <= 0) {
                    ArrayList mmlist = cycleMothList(orglist, cyc_year, archive_set);
                    if (mmlist != null && mmlist.size() > 0) {
                        CommonData da = (CommonData) mmlist.get(0);
                        cyc_moth = da.getDataName();
                    } else {
                        cyc_moth = PubFunc.getStringDate("MM");
                        CommonData da = new CommonData(cyc_moth, cyc_moth);
                        mmlist.add(da);
                    }
                }
                cyc_Sdate = cyc_year + "-" + cyc_moth + "-" + "01";
                cyc_Edate = cyc_Sdate;
                this.getFormHM().put("cyc_year", cyc_year);
                this.getFormHM().put("cyc_moth", cyc_moth);
            }
        } else {
            if ("4".equals(archive_type)) {
                String cyc_year = (String) this.getFormHM().get("cyc_year");
                String cyc_year_e = (String) this.getFormHM().get("cyc_year_e");
                if (cyc_year == null || cyc_year.length() <= 0 || cyc_year_e == null || cyc_year_e.length() <= 0) {
                    ArrayList yylist = cycleYearList(orglist, archive_set);
                    if (yylist != null && yylist.size() > 0) {
                        CommonData da = (CommonData) yylist.get(0);
                        cyc_year_e = da.getDataName();
                        da = (CommonData) yylist.get(yylist.size() - 1);
                        cyc_year = da.getDataName();
                    } else {
                        cyc_year = PubFunc.getStringDate("yyyy");
                        cyc_year_e = cyc_year;
                        CommonData da = new CommonData(cyc_year, cyc_year);
                        yylist.add(da);
                    }
                    this.getFormHM().put("yylist", yylist);
                    this.getFormHM().put("cyc_year", cyc_year);
                    this.getFormHM().put("cyc_year_e", cyc_year_e);
                }
                cyc_Sdate = cyc_year + "-" + "01" + "-" + "01";
                cyc_Edate = cyc_year_e + "-" + "12" + "-" + "31";
            } else {
                cyc_Sdate = (String) this.getFormHM().get("cyc_Sdate");
                cyc_Edate = (String) this.getFormHM().get("cyc_Edate");
                if (cyc_Sdate == null || cyc_Sdate.length() <= 0) {
                    cyc_Sdate = cycleScope(orglist, archive_set);
                    cyc_Edate = cyc_Sdate;
                }
                this.getFormHM().put("cyc_Sdate", cyc_Sdate);
                this.getFormHM().put("cyc_Edate", cyc_Edate);
                this.getFormHM().put("cyc_year", "");
                this.getFormHM().put("yylist", new ArrayList());
            }
        }
        ArrayList fields = getFielditems(statid);//归档指标
        this.yearlist = getYearlist(cyc_Sdate, cyc_Edate);
        if (fields == null || fields.size() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("统计条件没有关联归档指标！"));

        StringBuffer html = new StringBuffer();
        html.append(" <table   border='0' cellspacing='0' width='90%'  align='center' cellpadding='0' class='ListTable'>");
        html.append("<tr>");
        html.append("<td class='RecordRow_ltbr' height='30'  valign='middle' align='center'>");
        html.append("<font style='font-size:12pt'>" + sname + "</font>");//表头
        html.append("</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td class='RecordRow_ltbr'  valign='middle' align='center'>");
        String strhtml = strHtml(archive_type, archive_set, orglist, fields, cyc_Sdate, cyc_Edate);
        html.append(strhtml);//
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        this.getFormHM().put("reportHtml", html.toString());
        
        jFreecharData(archive_type, archive_set, orglist, fields, cyc_Sdate, cyc_Edate, graph_style);
        
        if ("1".equals(graph_style))
            this.getFormHM().put("jfreelist", this.jfreelist);//柱状图
        else
            this.getFormHM().put("jfreemap", this.jfreemap);//直线图
        
        if ("2".equals(graph_style))
            this.getFormHM().put("chartType", "1"); //折线图 bug 39476 折线图不对  wangb 20180809
        else if ("1".equals(graph_style))
            this.getFormHM().put("chartType", "29"); //分组柱状图
        
        this.getFormHM().put("chartTitle", sname);
        this.getFormHM().put("init", "false");
        this.getFormHM().put("archive_type", archive_type);
    }

    private void jFreecharData(String archive_type, String archive_set, ArrayList orglist, ArrayList fields, String cyc_Sdate,
            String cyc_Edate, String graph_style) {

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        HashMap map = new HashMap();
        int years = this.yearlist.size();
        int orgs = orglist.size();
        this.getFormHM().put("orderlist", null);
        if (orgs > 1)//多个单位
        {
            //多个单位
            int course = getArchiveCourse(archive_type, cyc_Sdate, cyc_Edate);
            if (course == 1)//程序目前更改为多个机构只能选择一个期间
            {
                //多个单位一个期间
                String yearStr = (String) this.yearlist.get(0);
                String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                String ss = cycs[0];
                int statrI = Integer.parseInt(ss) - 1;
                int cyc = statrI + 1;
                if ("1".equals(graph_style)) {
                    for (int f = 0; f < fields.size(); f++)//直方图
                    {
                        FieldItem fielditem = (FieldItem) fields.get(f);
                        String itemdesc = fielditem.getItemdesc();
                        String itemid = fielditem.getItemid();
                        ArrayList commonList = new ArrayList();
                        getOneCourseStatBean(archive_set, archive_type, orglist, itemid, yearStr, cyc_Sdate, cyc_Edate, cyc,
                                commonList, dao);
                        LazyDynaBean abean = new LazyDynaBean();
                        abean.set("categoryName", itemdesc);//统计项
                        abean.set("dataList", commonList);
                        list.add(abean);
                    }
                } else {
                    //线状图，一个单位一根线
                    for (int s = 0; s < orglist.size(); s++) {
                        ArrayList commonList = new ArrayList();
                        String codeitemid = (String) orglist.get(s);
                        codeitemid = codeitemid.substring(2);
                        String codedesc = this.getCodeitemdesc(codeitemid);
                        getLineMulti_UNBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cyc, codeitemid,
                                commonList, dao);
                        /*getOrderYearBean(archive_set,archive_type,cyc_Sdate,cyc_Edate,itemid,codeitemid,commonList,dao);*/
                        map.put(codedesc, commonList);
                    }
                }

            } else {
                //多个单位多个期间
                if ("1".equals(graph_style)) {
                    for (int y = 0; y < years; y++) {
                        String yearStr = (String) this.yearlist.get(y);
                        String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                        String ss = cycs[0];
                        int statrI = Integer.parseInt(ss) - 1;
                        int endI = Integer.parseInt(cycs[1]);

                        for (int i = statrI; i < endI; i++) {
                            ArrayList commonList = new ArrayList();

                            for (int f = 0; f < fields.size(); f++)//直方图
                            {
                                FieldItem fielditem = (FieldItem) fields.get(f);
                                String itemdesc = fielditem.getItemdesc();
                                String itemid = fielditem.getItemid();
                                getMultiStatItemBean(archive_set, archive_type, orglist, itemid, itemdesc, yearStr, cyc_Sdate,
                                        cyc_Edate, i + 1, commonList, dao);

                            }
                            
                            LazyDynaBean abean = new LazyDynaBean();
                            String distinct = yearStr + " " + getCycleStr(archive_type, (i + 1));
                            abean.set("categoryName", distinct);
                            abean.set("dataList", commonList);
                            list.add(abean);
                        }
                    }
                } else {
                    ArrayList orderlist = new ArrayList();
                    for (int s = 0; s < orglist.size(); s++) {
                        String codeitemid = (String) orglist.get(s);
                        codeitemid = codeitemid.substring(2);
                        String codedesc = this.getCodeitemdesc(codeitemid);
                        for (int f = 0; f < fields.size(); f++) {
                            FieldItem fielditem = (FieldItem) fields.get(f);//线性
                            String itemdesc = fielditem.getItemdesc();
                            String itemid = fielditem.getItemid();
                            ArrayList commonList = new ArrayList();
                            getOrderYearBean(archive_set, archive_type, cyc_Sdate, cyc_Edate, itemid, codeitemid, commonList, dao);
                            map.put(codedesc + "_" + itemdesc, commonList);
                            orderlist.add(codedesc + "_" + itemdesc);
                        }

                    }
                    this.getFormHM().put("orderlist", orderlist);
                }
            }
        } else//一个单位
        {
            //yige 单位
            if ("1".equals(graph_style))//柱状图
            {
                if (years == 1) {
                    String yearStr = (String) this.yearlist.get(0);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    String ss = cycs[0];
                    int statrI = Integer.parseInt(ss) - 1;
                    int endI = Integer.parseInt(cycs[1]);
                    int bears = endI - statrI;
                    if (bears <= 1) {
                        //一个期间
                        for (int i = statrI; i < endI; i++) {
                            ArrayList commonList = new ArrayList();
                            for (int s = 0; s < orglist.size(); s++) {
                                String codeitemid = (String) orglist.get(s);
                                codeitemid = codeitemid.substring(2);
                                getOneUNBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, i + 1, codeitemid,
                                        commonList, dao);
                            }
                            LazyDynaBean abean = new LazyDynaBean();
                            String distinct = yearStr + " " + getCycleStr(archive_type, (i + 1));
                            abean.set("categoryName", distinct);
                            abean.set("dataList", commonList);
                            list.add(abean);
                        }
                    } else {
                        //一年多个期间
                        for (int f = 0; f < fields.size(); f++) {
                            FieldItem fielditem = (FieldItem) fields.get(f);
                            String itemdesc = fielditem.getItemdesc();
                            String itemid = fielditem.getItemid();

                            for (int s = 0; s < orglist.size(); s++) {
                                ArrayList commonList = new ArrayList();
                                String codeitemid = (String) orglist.get(s);
                                codeitemid = codeitemid.substring(2);
                                getOrderYearBean(archive_set, archive_type, cyc_Sdate, cyc_Edate, itemid, codeitemid, commonList,
                                        dao);
                                LazyDynaBean abean = new LazyDynaBean();
                                abean.set("categoryName", itemdesc);
                                abean.set("dataList", commonList);
                                list.add(abean);
                            }
                        }
                    }
                } else {
                    //多个年（多个期间）
                    for (int f = 0; f < fields.size(); f++) {
                        FieldItem fielditem = (FieldItem) fields.get(f);
                        String itemdesc = fielditem.getItemdesc();
                        String itemid = fielditem.getItemid();

                        for (int s = 0; s < orglist.size(); s++) {
                            ArrayList commonList = new ArrayList();
                            String codeitemid = (String) orglist.get(s);
                            codeitemid = codeitemid.substring(2);
                            getOrderYearBean(archive_set, archive_type, cyc_Sdate, cyc_Edate, itemid, codeitemid, commonList, dao);
                            LazyDynaBean abean = new LazyDynaBean();
                            abean.set("categoryName", itemdesc);
                            abean.set("dataList", commonList);
                            list.add(abean);
                        }
                    }
                }
            } else {
                int course = getArchiveCourse(archive_type, cyc_Sdate, cyc_Edate);
                if (course == 1) {
                    String yearStr = (String) this.yearlist.get(0);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    String ss = cycs[0];
                    int statrI = Integer.parseInt(ss) - 1;
                    int cyc = statrI + 1;
                    for (int s = 0; s < orglist.size(); s++) {
                        ArrayList commonList = new ArrayList();
                        String codeitemid = (String) orglist.get(s);
                        codeitemid = codeitemid.substring(2);
                        String codedesc = this.getCodeitemdesc(codeitemid);
                        //getOrderMultiUNBean(archive_set,archive_type,fields,yearStr,cyc_Sdate,cyc_Edate,cyc,codeitemid,commonList, dao);//多个单位
                        getOneUNBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cyc, codeitemid,
                                commonList, dao);
                        map.put(codedesc, commonList);
                    }
                } else {
                    for (int y = 0; y < years; y++) {
                        String yearStr = (String) this.yearlist.get(y);
                        String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                        String ss = cycs[0];
                        int statrI = Integer.parseInt(ss) - 1;
                        int endI = Integer.parseInt(cycs[1]);

                        for (int i = statrI; i < endI; i++) {
                            ArrayList commonList = new ArrayList();

                            for (int f = 0; f < fields.size(); f++)//直方图
                            {
                                FieldItem fielditem = (FieldItem) fields.get(f);
                                String itemdesc = fielditem.getItemdesc();
                                String itemid = fielditem.getItemid();
                                getMultiStatItemBean(archive_set, archive_type, orglist, itemid, itemdesc, yearStr, cyc_Sdate,
                                        cyc_Edate, i + 1, commonList, dao);

                            }
                            String distinct = yearStr + " " + getCycleStr(archive_type, (i + 1));
                            map.put(distinct, commonList);
                        }
                    }
                }

            }

        }
        this.jfreemap = map;
        this.jfreelist = list;
    }

    /**
     * 统计期间
     * @param archive_type
     * @param cyc_Sdate
     * @param cyc_Edate
     * @return
     */
    private int getArchiveCourse(String archive_type, String cyc_Sdate, String cyc_Edate) {
        int course = this.yearlist.size();
        if (course > 1)
            return course;
        if ("1".equals(archive_type) || "2".equals(archive_type) || "3".equals(archive_type)) {
            String yearStr = (String) this.yearlist.get(0);
            String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
            String ss = cycs[0];
            int statrI = Integer.parseInt(ss) - 1;
            int endI = Integer.parseInt(cycs[1]);
            int bears = endI - statrI;
            course = bears;
        }
        return course;
    }

    /**
     * 
     * @param archive_type
     * @param archive_set
     * @param statid
     * @param orglist
     * @param yearlist
     * @param fields
     * @param rs
     * @return 
     */
    private String strHtml(String archive_type, String archive_set, ArrayList orglist, ArrayList fields, String cyc_Sdate,
            String cyc_Edate) {
        StringBuffer html = new StringBuffer();

        int bears = 1;
        if ("1".equals(archive_type))//=1 月报
        {
            bears = 12;
            html.append(" <table border='0' width='100%' cellspacing='0' valign='middle' align='center' cellpadding='0'>");
            html.append("<tr>");
            html.append("<td  class='RecordCell_r'  valign='middle' align='center' width='100'>");
            html.append("机构名称");
            html.append("<td width='80' class='RecordCell_r'  valign='middle' align='center'>年度</td>");
            html.append("<td width='80' class='RecordCell_r'  valign='middle' align='center'>月份</td>");
            for (int i = 0; i < fields.size(); i++) {
                FieldItem fielditem = (FieldItem) fields.get(i);
                if (i == fields.size() - 1)
                    html.append("<td  valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
                else
                    html.append("<td class='RecordCell_r' valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
            }
            html.append("</tr>");
            
            for (int s = 0; s < orglist.size(); s++) {
                String codeitemid = (String) orglist.get(s);
                codeitemid = codeitemid.substring(2);

                int years = this.yearlist.size();
                for (int y = 0; y < years; y++) {
                    String yearStr = (String) this.yearlist.get(y);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    String ss = cycs[0];
                    int statrI = Integer.parseInt(ss) - 1;
                    int endI = Integer.parseInt(cycs[1]);
                    bears = endI - statrI;
                    List rs = getSetBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cycs, codeitemid);
                    HashMap rsmap = rsMap(rs, archive_type);
                    html.append("<tr>");
                    if (y == 0) {
                        html.append("<td rowspan='" + getRowspan(archive_type, cyc_Sdate, cyc_Edate)
                                + "'  class='RecordCell_rt'  valign='middle' align='center'>" + getCodeitemdesc(codeitemid)
                                + "</td>");
                    }
                    html.append("<td rowspan='" + bears + "'  class='RecordCell_rt'   valign='middle' align='center'>" + yearStr
                            + "</td>");
                    
                    for (int i = statrI; i < endI; i++) {
                        if (i > statrI)
                            html.append("<tr>");
                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>"
                                + getCycleStr(archive_type, (i + 1)) + "</td>");
                        //以下循环指标
                        if (rs != null && rs.size() > 0) {
                            LazyDynaBean rec = (LazyDynaBean) rsmap.get(yearStr + "" + (i + 1));
                            if (rec != null) {
                                for (int f = 0; f < fields.size(); f++) {
                                    FieldItem fielditem = (FieldItem) fields.get(f);
                                    String value = (String) rec.get(fielditem.getItemid());
                                    value = value != null && value.length() > 0 ? value : "0";
                                    if (f == fields.size() - 1)
                                        html.append("<td  valign='middle'  class='RecordCell_t' align='center'>" + value
                                                + "</td>");
                                    else
                                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>" + value
                                                + "</td>");
                                }
                            } else {
                                disposalNull(html, fields);
                            }
                        } else {
                            disposalNull(html, fields);
                        }
                        html.append("</tr>");//行结束，包括机构的行结束，年的行结束，和月的行结束
                    }

                }

            }
            html.append("</table>");
        } else if ("2".equals(archive_type))//		=2 季报
        {
            bears = 4;
            html.append(" <table border='0' width='100%' cellspacing='0' valign='middle' align='center' cellpadding='1'>");
            html.append("<tr>");
            html.append("<td  class='RecordCell_r'  valign='middle' align='center' width='100'>");
            html.append("机构名称");
            html.append("<td width='100' class='RecordCell_r' valign='middle' align='center'>年度</td>");
            html.append("<td width='107' class='RecordCell_r' valign='middle' align='center'>季度</td>");
            for (int i = 0; i < fields.size(); i++) {
                FieldItem fielditem = (FieldItem) fields.get(i);
                if (i == fields.size() - 1)
                    html.append("<td  valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
                else
                    html.append("<td class='RecordCell_r' valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
            }
            html.append("</tr>");
            
            for (int s = 0; s < orglist.size(); s++) {
                String codeitemid = (String) orglist.get(s);
                codeitemid = codeitemid.substring(2);
                int years = this.yearlist.size();
                for (int y = 0; y < years; y++) {
                    String yearStr = (String) this.yearlist.get(y);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    String ss = cycs[0];
                    int statrI = Integer.parseInt(ss) - 1;
                    int endI = Integer.parseInt(cycs[1]);
                    bears = endI - statrI;
                    List rs = getSetBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cycs, codeitemid);
                    HashMap rsmap = rsMap(rs, archive_type);
                    html.append("<tr>");
                    if (y == 0) {
                        html.append("<td rowspan='" + getRowspan(archive_type, cyc_Sdate, cyc_Edate)
                                + "' class='RecordCell_rt'  valign='middle' align='center'>" + getCodeitemdesc(codeitemid)
                                + "</td>");
                    }
                    html.append("<td rowspan='" + bears + "' class='RecordCell_rt'  valign='middle' align='center'>" + yearStr
                            + "</td>");
                    
                    for (int i = statrI; i < endI; i++) {
                        if (i > statrI)
                            html.append("<tr>");
                        
                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>"
                                + getCycleStr(archive_type, (i + 1)) + "</td>");
                        //以下循环指标
                        if (rs != null && rs.size() > 0) {

                            LazyDynaBean rec = (LazyDynaBean) rsmap.get(yearStr + "" + (i + 1));
                            if (rec != null) {
                                for (int f = 0; f < fields.size(); f++) {
                                    FieldItem fielditem = (FieldItem) fields.get(f);
                                    String value = (String) rec.get(fielditem.getItemid());
                                    value = value != null && value.length() > 0 ? value : "0";
                                    if (f == fields.size() - 1)
                                        html.append("<td  valign='middle'  class='RecordCell_t' align='center'>" + value
                                                + "</td>");
                                    else
                                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>" + value
                                                + "</td>");
                                }
                            } else {
                                disposalNull(html, fields);
                            }
                        } else {
                            disposalNull(html, fields);
                        }
                        html.append("</tr>");//行结束，包括机构的行结束，年的行结束，和月的行结束
                    }

                }

            }
            html.append("</table>");
        } else if ("3".equals(archive_type))//=3 半年
        {
            bears = 2;
            html.append(" <table border='0' width='100%' cellspacing='0' valign='middle' align='center' cellpadding='1'>");
            html.append("<tr>");
            html.append("<td  class='RecordCell_r'  valign='middle' align='center' width='150'>");
            html.append("机构名称");
            html.append("<td width='100' class='RecordCell_r'  valign='middle' align='center'>年度</td>");
            html.append("<td width='107'>&nbsp;</td>");
            for (int i = 0; i < fields.size(); i++) {
                FieldItem fielditem = (FieldItem) fields.get(i);
                if (i == fields.size() - 1)
                    html.append("<td  valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
                else
                    html.append("<td class='RecordCell_r' valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
            }
            html.append("</tr>");
            for (int s = 0; s < orglist.size(); s++) {
                String codeitemid = (String) orglist.get(s);
                codeitemid = codeitemid.substring(2);

                int years = this.yearlist.size();
                for (int y = 0; y < years; y++) {
                    String yearStr = (String) this.yearlist.get(y);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    String ss = cycs[0];
                    int statrI = Integer.parseInt(ss) - 1;
                    int endI = Integer.parseInt(cycs[1]);
                    bears = endI - statrI;
                    List rs = getSetBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cycs, codeitemid);
                    HashMap rsmap = rsMap(rs, archive_type);
                    html.append("<tr>");
                    if (y == 0) {
                        html.append("<td rowspan='" + getRowspan(archive_type, cyc_Sdate, cyc_Edate)
                                + "' class='RecordCell_rt'  valign='middle' align='center'>" + getCodeitemdesc(codeitemid)
                                + "</td>");
                    }
                    html.append("<td rowspan='" + bears + "' class='RecordCell_rt'  valign='middle' align='center'>" + yearStr
                            + "</td>");
                    for (int i = statrI; i < endI; i++) {
                        if (i > statrI)
                            html.append("<tr>");
                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>"
                                + getCycleStr(archive_type, (i + 1)) + "</td>");
                        //以下循环指标
                        if (rs != null && rs.size() > 0) {
                            LazyDynaBean rec = (LazyDynaBean) rsmap.get(yearStr + "" + (i + 1));
                            if (rec != null) {
                                for (int f = 0; f < fields.size(); f++) {
                                    FieldItem fielditem = (FieldItem) fields.get(f);
                                    String value = (String) rec.get(fielditem.getItemid());
                                    value = value != null && value.length() > 0 ? value : "0";
                                    if (f == fields.size() - 1)
                                        html.append("<td  valign='middle'  class='RecordCell_t' align='center'>" + value
                                                + "</td>");
                                    else
                                        html.append("<td class='RecordCell_rt'  valign='middle' align='center'>" + value
                                                + "</td>");
                                }
                            } else {
                                disposalNull(html, fields);
                            }
                        } else {
                            disposalNull(html, fields);
                        }
                        html.append("</tr>");//行结束，包括机构的行结束，年的行结束，和月的行结束
                    }

                }

            }
            html.append("</table>");
        } else if ("4".equals(archive_type))//=4 年报
        {
            bears = 1;
            html.append(" <table border='0' width='100%' cellspacing='0' valign='middle' align='center' cellpadding='1'>");
            html.append("<tr>");
            html.append("<td  class='RecordCell_r'  valign='middle' align='center' width='150'>");
            html.append("机构名称");
            html.append("<td width='100' class='RecordCell_r'  valign='middle' align='center'>年度</td>");
            for (int i = 0; i < fields.size(); i++) {
                FieldItem fielditem = (FieldItem) fields.get(i);
                if (i == fields.size() - 1)
                    html.append("<td  valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
                else
                    html.append("<td class='RecordCell_r' valign='middle' align='center'>" + fielditem.getItemdesc() + "</td>");
            }
            html.append("</tr>");
            for (int s = 0; s < orglist.size(); s++) {
                String codeitemid = (String) orglist.get(s);
                codeitemid = codeitemid.substring(2);
                int years = this.yearlist.size();
                for (int y = 0; y < years; y++) {
                    String yearStr = (String) yearlist.get(y);
                    String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                    List rs = getSetBean(archive_set, archive_type, fields, yearStr, cyc_Sdate, cyc_Edate, cycs, codeitemid);
                    HashMap rsmap = rsMap(rs, archive_type);
                    html.append("<tr>");
                    if (y == 0) {
                        html.append("<td rowspan='" + (bears * (years))
                                + "' class='RecordCell_rt'  valign='middle' align='center'>" + getCodeitemdesc(codeitemid)
                                + "</td>");
                    }
                    html.append("<td rowspan='" + bears + "' class='RecordCell_rt'  valign='middle' align='center'>" + yearStr
                            + "</td>");
                    
                    //以下循环指标
                    if (rs != null && rs.size() > 0) {
                        LazyDynaBean rec = (LazyDynaBean) rsmap.get(yearStr + "1");
                        if (rec != null) {
                            for (int f = 0; f < fields.size(); f++) {
                                FieldItem fielditem = (FieldItem) fields.get(f);
                                String value = (String) rec.get(fielditem.getItemid());
                                value = value != null && value.length() > 0 ? value : "0";
                                if (f == fields.size() - 1)
                                    html.append("<td  valign='middle'  class='RecordCell_t' align='center'>" + value + "</td>");
                                else
                                    html.append("<td class='RecordCell_rt'  valign='middle' align='center'>" + value + "</td>");
                            }
                        } else {
                            disposalNull(html, fields);
                        }
                    } else {
                        disposalNull(html, fields);
                    }
                    html.append("</tr>");//行结束，包括机构的行结束，年的行结束，和月的行结束
                }
            }
            html.append("</table>");
        }
        return html.toString();
    }

    /**
     * 处理空
     * @param html
     * @param fields
     */
    private void disposalNull(StringBuffer html, ArrayList fields) {
        for (int f = 0; f < fields.size(); f++) {
            if (f == fields.size() - 1)
                html.append("<td  valign='middle' class='RecordCell_t' align='center'>&nbsp;</td>");
            else
                html.append("<td class='RecordCell_rt'  valign='middle' align='center'>&nbsp;</td>");
        }
    }

    /**
     * 得到归档子集中的相关数据
     * @param archive_set
     * @param archive_type
     * @param fields
     * @param cyc_Sdate
     * @param cyc_Edate
     * @return
     */
    /*子集为按月变化，用户填写规则为
    2009-01-01 1
    2009-02-01 1
    2009-03-01 1
    。。。。
    陈猛清(分机224)() 14:14:37
    按年变化子集
    2009-01-01 1
    按季变化
    2009-01-01 1
    2009-04-01 2
    2009-07-01 3
    2009-10-01 4
    半年变化
    2009-01-01 1
    2009-07-01 2*/
    private List getSetBean(String archive_set, String archive_type, ArrayList fields, String year, String cyc_Sdate,
            String cyc_Edate, String[] cyc, String codeitemdid) {

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (int i = 0; i < fields.size(); i++) {
            FieldItem fielditem = (FieldItem) fields.get(i);
            sql.append(fielditem.getItemid() + ",");
        }
        sql.append(Sql_switcher.year(archive_set + "z0") + " z0");
        sql.append("," + Sql_switcher.month(archive_set + "z0") + " zm");
        sql.append("," + archive_set + "z1 z1");
        Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
        cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
        date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
        int mm = DateUtils.getMonth(date);
        if (mm == 12) {
            cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (1) + "-" + "01";
        } else
            cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";

        String z01 = cyc[0];
        String z02 = cyc[1];
        sql.append(" from " + archive_set);
        sql.append(" where b0110='" + codeitemdid + "'");
        sql.append(" and " + Sql_switcher.year(archive_set + "z0") + "=" + year);
        if ("1".equals(archive_type)) {
            sql.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
            sql.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
        } else {
            sql.append(" and " + archive_set + "z1>=" + z01);
            sql.append(" and " + archive_set + "z1<=" + z02);
        }
        //sql.append(" order by "+archive_set+"z1"); //原来的 报错
        sql.append(" order by z1");
        List rs = ExecuteSQL.executeMyQuery(sql.toString());
        return rs;
    }

    /**
     * 一个单位多个期间的线状图
     * @param archive_set
     * @param archive_type
     * @param fields
     * @param year
     * @param cyc_Sdate
     * @param cyc_Edate
     * @param cyc
     * @param orgid
     * @param commonList
     * @param dao
     */
    private void getLineMulti_UNBean(String archive_set, String archive_type, ArrayList fields, String year, String cyc_Sdate,
            String cyc_Edate, int cyc, String orgid, ArrayList commonList, ContentDAO dao) {

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (int i = 0; i < fields.size(); i++) {
            FieldItem fielditem = (FieldItem) fields.get(i);
            sql.append(fielditem.getItemid() + ",");
        }
        sql.append(Sql_switcher.year(archive_set + "z0") + " z0");
        sql.append("," + archive_set + "z1 z1");
        sql.append(",codeitemdesc");
        sql.append(" from " + archive_set + " a,organization b,");
        sql.append("(select MAX(" + archive_set + "z1) as z1," + archive_set + "Z0 as z0,b0110 from " + archive_set
                + " where b0110 ='" + orgid + "' group by b0110," + archive_set + "Z0)c");
        sql.append(" where a.b0110=b.codeitemid");
        sql.append(" and a.B0110=c.B0110 and a." + archive_set + "z0=c.z0 and a." + archive_set + "z1=c.z1");
        sql.append(" and a.b0110='" + orgid + "'");
        sql.append(" and " + Sql_switcher.year("a." + archive_set + "z0") + "=" + year);

        if ("1".equals(archive_type)) {
            Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
            cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
            date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
            int mm = DateUtils.getMonth(date);
            if (mm == 12) {
                cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (1) + "-" + "01";
            } else
                cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";
            sql.append(" and " + Sql_switcher.month("a." + archive_set + "z0") + "=" + cyc);
            sql.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
            sql.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
        } else {
            sql.append(" and a." + archive_set + "z1=" + cyc);
        }
        sql.append(" order by a." + archive_set + "z1");
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                for (int f = 0; f < fields.size(); f++) {
                    FieldItem fielditem = (FieldItem) fields.get(f);
                    String value = (String) this.frowset.getString(fielditem.getItemid());
                    value = value != null && value.length() >= 0 ? value : "0";
                    CommonData vo = new CommonData(String.valueOf(value), fielditem.getItemdesc());
                    commonList.add(vo);
                }
            } else {
                for (int f = 0; f < fields.size(); f++) {
                    FieldItem fielditem = (FieldItem) fields.get(f);
                    CommonData vo = new CommonData("0", fielditem.getItemdesc());
                    commonList.add(vo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一个单位，不带统计名称(一个单位一根线)
     * @param archive_set
     * @param archive_type
     * @param fields
     * @param year
     * @param cyc_Sdate
     * @param cyc_Edate
     * @param cyc
     * @param orgid
     * @param commonList
     * @param dao
     */
    private void getOneUNBean(String archive_set, String archive_type, ArrayList fields, String year, String cyc_Sdate,
            String cyc_Edate, int cyc, String orgid, ArrayList commonList, ContentDAO dao) {

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (int i = 0; i < fields.size(); i++) {
            FieldItem fielditem = (FieldItem) fields.get(i);
            sql.append(fielditem.getItemid() + ",");
        }
        sql.append(Sql_switcher.year(archive_set + "z0") + " z0");
        sql.append("," + archive_set + "z1 z1");
        sql.append(",codeitemdesc");
        sql.append(" from " + archive_set + " a,organization b,");
        sql.append("(select MAX(" + archive_set + "z1) as z1," + archive_set + "Z0 as z0,b0110 from " + archive_set
                + " where b0110 ='" + orgid + "' group by b0110," + archive_set + "Z0)c");
        sql.append(" where a.b0110=b.codeitemid");
        sql.append(" and a.B0110=c.B0110 and a." + archive_set + "z0=c.z0 and a." + archive_set + "z1=c.z1");
        sql.append(" and a.b0110='" + orgid + "'");
        sql.append(" and " + Sql_switcher.year("a." + archive_set + "z0") + "=" + year);

        if ("1".equals(archive_type)) {
            Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
            cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
            date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
            int mm = DateUtils.getMonth(date);
            if (mm == 12) {
                cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (1) + "-" + "01";
            } else
                cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";
            sql.append(" and " + Sql_switcher.month("a." + archive_set + "z0") + "=" + cyc);
            sql.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
            sql.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
        } else {
            sql.append(" and a." + archive_set + "z1=" + cyc);
        }
        sql.append(" order by a." + archive_set + "z1");
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                for (int f = 0; f < fields.size(); f++) {
                    FieldItem fielditem = (FieldItem) fields.get(f);
                    String value = (String) this.frowset.getString(fielditem.getItemid());
                    value = value != null && value.length() >= 0 ? value : "0";
                    CommonData vo = new CommonData(String.valueOf(value), fielditem.getItemdesc());
                    commonList.add(vo);
                }
            } else {
                for (int f = 0; f < fields.size(); f++) {
                    FieldItem fielditem = (FieldItem) fields.get(f);
                    CommonData vo = new CommonData("0", fielditem.getItemdesc());
                    commonList.add(vo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一个单位多期间的
     * @param archive_set
     * @param archive_type
     * @param cyc_Sdate
     * @param cyc_Edate
     * @param itemid
     * @param orgid
     * @param commonList
     * @param dao
     */
    private void getOrderYearBean(String archive_set, String archive_type, String cyc_Sdate, String cyc_Edate, String itemid,
            String orgid, ArrayList commonList, ContentDAO dao) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + itemid + " itemid,");
        sql.append("" + Sql_switcher.year("a." + archive_set + "z0") + " z0");
        sql.append(",a." + archive_set + "z1 z1");
        sql.append(",codeitemdesc");
        sql.append(" from " + archive_set + " a,organization b,");
        sql.append("(select MAX(" + archive_set + "z1) as z1," + archive_set + "Z0 as z0,b0110 from " + archive_set
                + " where b0110='" + orgid + "' group by b0110," + archive_set + "Z0)c");
        sql.append(" where a.b0110=b.codeitemid");
        sql.append(" and a.B0110=c.B0110 and a." + archive_set + "z0=c.z0 and a." + archive_set + "z1=c.z1");
        sql.append(" and a.b0110='" + orgid + "'");
        StringBuffer whereBuf = new StringBuffer();
        //System.out.println(sql.toString());
        int years = this.yearlist.size();
        try {
            for (int y = 0; y < years; y++) {
                String yearStr = (String) this.yearlist.get(y);
                String[] cycs = getArchiveZ0(archive_type, yearStr, cyc_Sdate, cyc_Edate);
                String ss = cycs[0];
                int statrI = Integer.parseInt(ss) - 1;
                int endI = Integer.parseInt(cycs[1]);
                for (int i = statrI; i < endI; i++) {
                    int cyc = i + 1;
                    whereBuf.setLength(0);
                    whereBuf.append(sql.toString());
                    whereBuf.append(" and " + Sql_switcher.year("a." + archive_set + "z0") + "=" + yearStr);
                    if ("1".equals(archive_type)) {
                        Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
                        cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
                        date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
                        int mm = DateUtils.getMonth(date);
                        if (mm == 12) {
                            cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (12) + "-" + "31";
                        } else {
                            cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";
                            cyc_Edate = DateUtils.format(DateUtils.addDays(DateUtils.getDate(cyc_Edate, "yyyy-MM-dd"), -1),
                                    "yyyy-MM-dd");
                        }
                        whereBuf.append(" and " + Sql_switcher.month("a." + archive_set + "z0") + "=" + cyc);
                        whereBuf.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
                        whereBuf.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
                    } else {
                        whereBuf.append(" and a." + archive_set + "z1=" + cyc);
                    }
                    whereBuf.append(" order by a." + archive_set + "z1");
                    //System.out.println(whereBuf.toString());
                    String distinct = yearStr + " " + getCycleStr(archive_type, (i + 1));
                    this.frowset = dao.search(whereBuf.toString());
                    if (this.frowset.next()) {

                        String value = (String) this.frowset.getString("itemid");
                        value = value != null && value.length() >= 0 ? value : "0";
                        CommonData vo = new CommonData(String.valueOf(value), distinct);
                        commonList.add(vo);
                    } else {
                        CommonData vo = new CommonData("0", distinct);
                        commonList.add(vo);
                    }

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getMultiStatItemBean(String archive_set, String archive_type, ArrayList orglist, String itemid, String itemdesc,
            String year, String cyc_Sdate, String cyc_Edate, int cyc, ArrayList commonList, ContentDAO dao) {

        StringBuffer sql = new StringBuffer();
        StringBuffer orgids = new StringBuffer();
        orgids.append("('',");
        for (int i = 0; i < orglist.size(); i++) {
            String codeitemid = (String) orglist.get(i);
            codeitemid = codeitemid.substring(2);
            orgids.append("'" + codeitemid + "',");
        }
        orgids.setLength(orgids.length() - 1);
        orgids.append(")");
        sql.append("select " + itemid + " itemid,");

        sql.append(Sql_switcher.year(archive_set + "z0") + " z0");
        sql.append("," + archive_set + "z1 z1");
        sql.append(",codeitemdesc");
        sql.append(" from " + archive_set + " a,organization b,");
        sql.append("(select MAX(" + archive_set + "z1) as z1," + archive_set + "Z0 as z0,b0110 from " + archive_set
                + " where b0110 in " + orgids + " group by b0110," + archive_set + "Z0)c");
        sql.append(" where a.b0110=b.codeitemid");
        sql.append(" and a.B0110=c.B0110 and a." + archive_set + "z0=c.z0 and a." + archive_set + "z1=c.z1");
        sql.append(" and a.b0110 in " + orgids);
        sql.append(" and " + Sql_switcher.year("a." + archive_set + "z0") + "=" + year);
        if ("1".equals(archive_type)) {
            Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
            cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
            date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
            int mm = DateUtils.getMonth(date);
            if (mm == 12) {
                cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (1) + "-" + "01";
            } else
                cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";
            sql.append(" and " + Sql_switcher.month("a." + archive_set + "z0") + "=" + cyc);
            sql.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
            sql.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
        } else {
            sql.append(" and a." + archive_set + "z1=" + cyc);
        }
        sql.append(" order by a.b0110");
        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                String value = this.frowset.getString("itemid");
                value = value != null && value.length() >= 0 ? value : "0";
                CommonData vo = new CommonData(String.valueOf(value), itemdesc);
                commonList.add(vo);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 一个期间多个单位柱状图
     * @param archive_set
     * @param archive_type
     * @param orglist
     * @param itemid
     * @param year
     * @param cyc_Sdate
     * @param cyc_Edate
     * @param cyc
     * @param commonList
     * @param dao
     */
    private void getOneCourseStatBean(String archive_set, String archive_type, ArrayList orglist, String itemid, String year,
            String cyc_Sdate, String cyc_Edate, int cyc, ArrayList commonList, ContentDAO dao) {

        StringBuffer sql = new StringBuffer();
        StringBuffer orgids = new StringBuffer();
        orgids.append("('',");
        for (int i = 0; i < orglist.size(); i++) {
            String codeitemid = (String) orglist.get(i);
            codeitemid = codeitemid.substring(2);
            orgids.append("'" + codeitemid + "',");
        }
        orgids.setLength(orgids.length() - 1);
        orgids.append(")");
        sql.append("select " + itemid + " itemid,");

        sql.append(Sql_switcher.year(archive_set + "z0") + " z0");
        sql.append("," + archive_set + "z1 z1");
        sql.append(",codeitemdesc");
        sql.append(" from " + archive_set + " a,organization b,");
        sql.append("(select MAX(" + archive_set + "z1) as z1," + archive_set + "Z0 as z0,b0110 from " + archive_set
                + " where b0110 in " + orgids + " group by b0110," + archive_set + "Z0)c");
        sql.append(" where a.b0110=b.codeitemid");
        sql.append(" and a.B0110=c.B0110 and a." + archive_set + "z0=c.z0 and a." + archive_set + "z1=c.z1");
        sql.append(" and a.b0110 in " + orgids);
        sql.append(" and " + Sql_switcher.year("a." + archive_set + "z0") + "=" + year);
        if ("1".equals(archive_type)) {
            Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
            cyc_Sdate = DateUtils.getYear(date) + "-" + DateUtils.getMonth(date) + "-" + "01";
            date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
            int mm = DateUtils.getMonth(date);
            if (mm == 12) {
                cyc_Edate = (DateUtils.getYear(date) + 1) + "-" + (1) + "-" + "01";
            } else
                cyc_Edate = (DateUtils.getYear(date)) + "-" + (mm + 1) + "-" + "01";
            sql.append(" and " + Sql_switcher.month("a." + archive_set + "z0") + "=" + cyc);
            sql.append(" and " + archive_set + "z0>=" + Sql_switcher.dateValue(cyc_Sdate));
            sql.append(" and " + archive_set + "z0<=" + Sql_switcher.dateValue(cyc_Edate));
        } else {
            sql.append(" and a." + archive_set + "z1=" + cyc);
        }
        sql.append(" order by a.b0110");
        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                String value = this.frowset.getString("itemid");
                value = value != null && value.length() >= 0 ? value : "0";
                String desc = this.frowset.getString("codeitemdesc");
                CommonData vo = new CommonData(String.valueOf(value), desc);
                commonList.add(vo);
            }
        } catch (Exception e) {

        }
    }

    private String[] getArchiveZ0(String archive_type, String yearStr, String cyc_Sdate, String cyc_Edate) {
        Date date = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
        int s_year = DateUtils.getYear(date);
        date = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
        int e_year = DateUtils.getYear(date);
        int year = Integer.parseInt(yearStr);
        String ss[] = new String[3];
        int diff = e_year - s_year;
        if (diff == 0) {
            ss[0] = getCyclyZ0(archive_type, cyc_Sdate);
            ss[1] = getCyclyZ0(archive_type, cyc_Edate);
        } else {
            if (s_year == year) {
                ss[0] = getCyclyZ0(archive_type, cyc_Sdate);
                ss[1] = getCyclyZ0(archive_type, "");
            } else if (e_year == year) {
                ss[0] = "1";
                ss[1] = getCyclyZ0(archive_type, cyc_Edate);
            } else if (s_year < year && year < e_year) {
                ss[0] = "1";
                ss[1] = getCyclyZ0(archive_type, "");
            }
        }

        int s1 = Integer.parseInt(ss[1]);
        int s0 = Integer.parseInt(ss[0]);
        int beas = s1 - s0 + 1;
        ss[2] = beas + "";
        return ss;
    }

    private String getCyclyZ0(String archive_type, String dateStr) {
        String z0s = "";

        if (dateStr == null || dateStr.length() <= 0) {
            if ("1".equals(archive_type)) {
                z0s = "12";
            } else if ("2".equals(archive_type))//		=2 季报
            {
                z0s = "4";
            } else if ("3".equals(archive_type))//
            {
                z0s = "2";
            } else if ("4".equals(archive_type)) {
                z0s = "1";
            }
        } else {
            Date date = DateUtils.getDate(dateStr, "yyyy-MM-dd");
            int month = DateUtils.getMonth(date);
            if ("1".equals(archive_type)) {
                z0s = month + "";
            } else if ("2".equals(archive_type))//		=2 季报
            {
                if (month < 4) {
                    z0s = "1";
                } else if (month >= 4 && month < 7) {
                    z0s = "2";
                } else if (month >= 7 && month < 10) {
                    z0s = "3";
                } else if (month >= 10) {
                    z0s = "4";
                }
            } else if ("3".equals(archive_type))//
            {
                if (month < 7) {
                    z0s = "1";
                } else
                    z0s = "2";
            } else if ("4".equals(archive_type)) {
                z0s = "1";
            }
        }
        return z0s;
    }

    private HashMap rsMap(List rs, String archive_type) {
        HashMap map = new HashMap();
        if (!rs.isEmpty()) {
            for (int i = 0; i < rs.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) rs.get(i);
                String z0 = (String) rec.get("z0");
                String z1 = "";
                if ("1".equals(archive_type))
                    z1 = (String) rec.get("zm");
                else
                    z1 = (String) rec.get("z1");
                map.put(z0 + z1, rec);

            }
        }
        return map;
    }

    /**
     * 得到年的list
     * @param cyc_Sdate开始时间
     * @param cyc_Edate结束时间
     * @return
     */
    private ArrayList getYearlist(String cyc_Sdate, String cyc_Edate) {
        Date dateS = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
        Date dateE = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
        int s_year = DateUtils.getYear(dateS);
        int e_year = DateUtils.getYear(dateE);
        int diff = e_year - s_year;
        ArrayList yearlist = new ArrayList();
        if (diff == 0)
            yearlist.add(String.valueOf(e_year));
        else
            for (int i = 0; i <= diff; i++) {
                yearlist.add(String.valueOf(s_year + i));
            }
        return yearlist;
    }

    /**
     * 得到父亲节点
     * @return
     */
    private String getParentid() {
        String sql = "select codesetid,codeitemid from organization where codeitemid=parentid";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String orgid = "";
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                orgid = this.frowset.getString("codesetid") + this.frowset.getString("codeitemid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orgid;
    }

    /**
     * 得到所选单位
     * @param orgs
     * @return
     */
    private ArrayList getOrgids(String orgs) {
        if (orgs == null || orgs.length() <= 0) {
            if (this.userView.isSuper_admin())
                orgs = getParentid();
            else
                orgs = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
        }
        ArrayList list = new ArrayList();
        String orgArr[] = orgs.split(",");
        for (int i = 0; i < orgArr.length; i++) {
            if (orgArr[i] != null && orgArr[i].length() > 0)
                list.add(orgArr[i]);
        }
        if (list == null || list.size() <= 0) {
            if (this.userView.isSuper_admin())
                orgs = getParentid();
            else
                orgs = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
            list.add(orgs);
        }
        return list;
    }

    /**
     * 得到归档指标
     * @param statid
     * @return
     */
    private String getRowspan(String archive_type, String cyc_Sdate, String cyc_Edate) {
        String rowspan = "";
        Date dateS = DateUtils.getDate(cyc_Sdate, "yyyy-MM-dd");
        Date dateE = DateUtils.getDate(cyc_Edate, "yyyy-MM-dd");
        int s_year = DateUtils.getYear(dateS);
        int e_year = DateUtils.getYear(dateE);
        int diff = e_year - s_year;

        if (diff == 0) {
            String st = getCyclyZ0(archive_type, cyc_Sdate);
            String end = getCyclyZ0(archive_type, cyc_Edate);
            int row = Integer.parseInt(end) - Integer.parseInt(st) + 1;
            rowspan = row + "";
        } else {
            int row = 0;
            for (int i = 0; i <= diff; i++) {
                if (i == 0) {
                    String st = getCyclyZ0(archive_type, cyc_Sdate);
                    String end = getCyclyZ0(archive_type, "");
                    row = Integer.parseInt(end) - Integer.parseInt(st) + 1 + row;
                } else if (diff == i) {

                    String end = getCyclyZ0(archive_type, cyc_Edate);
                    row = Integer.parseInt(end) + row;
                } else {
                    String end = getCyclyZ0(archive_type, "");
                    row = Integer.parseInt(end) + row;
                }
            }
            rowspan = row + "";
        }
        return rowspan;

    }

    private ArrayList getFielditems(String statid) {
        ArrayList fields = new ArrayList();
        String sql = "select archive_field from slegend where id='" + statid + "' order by norder";
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next())//归档指标
            {
                if (this.frowset.getString("archive_field") != null && this.frowset.getString("archive_field").length() > 0) {
                    FieldItem fielditem = DataDictionary.getFieldItem(this.frowset.getString("archive_field"));
                    fields.add(fielditem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fields;
    }

    /**
     * 得到单位名称
     * @param codeitemid
     * @return
     */
    private String getCodeitemdesc(String codeitemid) {
        String sql = "select codeitemdesc from organization where codeitemid='" + codeitemid + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String codeitemdesc = "";
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                codeitemdesc = this.frowset.getString("codeitemdesc");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeitemdesc;
    }

    /**
     * =1 月报=2 季报=3 半年=4 年报
     * @param archive_type
     * @param cyc
     * @return
     */
    private String getCycleStr(String archive_type, int cyc) {
        String str = "";
        if ("1".equals(archive_type)) {
            switch (cyc) {
            case 1:
                str = "1月";
                break;
            case 2:
                str = "2月";
                break;
            case 3:
                str = "3月";
                break;
            case 4:
                str = "4月";
                break;
            case 5:
                str = "5月";
                break;
            case 6:
                str = "6月";
                break;
            case 7:
                str = "7月";
                break;
            case 8:
                str = "8月";
                break;
            case 9:
                str = "9月";
                break;
            case 10:
                str = "10月";
                break;
            case 11:
                str = "11月";
                break;
            case 12:
                str = "12月";
                break;
            }
        } else if ("2".equals(archive_type))//		=2 季报
        {
            switch (cyc) {
            case 1:
                str = "第一季度";
                break;
            case 2:
                str = "第二季度";
                break;
            case 3:
                str = "第三季度";
                break;
            case 4:
                str = "第四季度";
                break;

            }
        } else if ("3".equals(archive_type))//
        {
            switch (cyc) {
            case 1:
                str = "上半年";
                break;
            case 2:
                str = "下半年";
                break;
            }
        }
        return str;
    }

    /**
     * 
     * @param orglist
     * @param setid
     */
    private ArrayList cycleYearList(ArrayList orglist, String setid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select DISTINCT(" + Sql_switcher.year(setid + "z0") + ") yy from " + setid);
        sql.append(" where b0110 in('',");
        for (int s = 0; s < orglist.size(); s++) {
            String codeitemid = (String) orglist.get(s);
            codeitemid = codeitemid.substring(2);
            sql.append("'" + codeitemid + "',");
        }
        sql.setLength(sql.length() - 1);
        sql.append(")");
        //zxj 20140521 排除年月标识为空的无效数据
        sql.append(" AND " + setid + "z0 IS NOT NULL");
        sql.append(" order by yy desc");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        int i = 0;
        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                if (i >= 3)
                    break;
                String yy = this.frowset.getString("yy");
                CommonData da = new CommonData(yy, yy);
                list.add(da);
                i++;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList cycleMothList(ArrayList orglist, String year, String setid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select DISTINCT(" + Sql_switcher.month(setid + "z0") + ") mm from " + setid);
        sql.append(" where " + Sql_switcher.year(setid + "z0") + "=" + year + " and b0110 in('',");
        for (int s = 0; s < orglist.size(); s++) {
            String codeitemid = (String) orglist.get(s);
            codeitemid = codeitemid.substring(2);
            sql.append("'" + codeitemid + "',");
        }
        sql.setLength(sql.length() - 1);
        sql.append(") order by mm");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        int i = 0;
        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                if (i >= 3)
                    break;
                String yy = this.frowset.getString("mm");
                CommonData da = new CommonData(yy, yy);
                list.add(da);
                i++;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 
     * @param orglist
     * @param setid
     * @return
     */
    private String cycleScope(ArrayList orglist, String setid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + setid + "z0 z0 from " + setid);
        sql.append(" where b0110 in('',");
        for (int s = 0; s < orglist.size(); s++) {
            String codeitemid = (String) orglist.get(s);
            codeitemid = codeitemid.substring(2);
            sql.append("'" + codeitemid + "',");
        }
        sql.setLength(sql.length() - 1);
        sql.append(")");
        //zxj 20140521 排除年月标识为空的无效数据
        sql.append(" AND " + setid + "z0 IS NOT NULL");
        sql.append(" order by z0");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String cycdate = "";
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                java.sql.Date date = this.frowset.getDate("z0");
                cycdate = DateUtils.format(date, "yyyy-MM-dd");
            } else
                cycdate = PubFunc.getStringDate("yyyy-MM-dd");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cycdate;
    }
}
