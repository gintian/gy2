package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 考勤日报表
 */
public class KqViewDailyBo
{
    private Connection conn = null;
    private ReportParseVo parsevo = null;
    private UserView userView = null;
    private String self_flag;
    private String whereIN;// 外部条件
    private String cardno = "";
    private boolean kqtablejudge; // 首钢增加 true展现本月出缺勤情况统计小计，否则原始
    private String a0100sql = "";
    private HashMap codemap = new HashMap();
    private String uplevel = "";
    private String dbtype;
    private boolean noSelected = false;
    private String sortItem;
    private String sortItemDesc;
    private String curTab;

    public String getSortItem()
    {
        return sortItem;
    }

    public void setSortItem(String sortItem)
    {
        this.sortItem = sortItem;
    }

    public String getCardno()
    {
        return cardno;
    }

    public void setCardno(String cardno)
    {
        this.cardno = cardno;
    }

    public KqViewDailyBo()
    {
    }

    /** 初始化 */
    public KqViewDailyBo(Connection conn)
    {
        this.conn = conn;

    }

    public void setSelf_flag(String self_flag)
    {
        this.self_flag = self_flag;
    }

    public String getSelf_flag()
    {
        if (this.self_flag == null || this.self_flag.length() <= 0) {
            this.self_flag = "";
        }
        return this.self_flag;
    }

    public KqViewDailyBo(Connection conn, ReportParseVo parsevo, UserView userView)
    {
        this.conn = conn;
        this.parsevo = parsevo;
        this.userView = userView;
    }

    /***************************************************************************
     * 得到页面内容
     * 
     * @param userbase
     *            应用库前缀
     * @param code
     *            链接级别
     * @param coursedate
     *            考勤期
     * @param curpage
     *            当前页
     * @param parsevo
     *            xml参数
     * @return 员工日考勤页面
     **************************************************************************/
    public ArrayList getKqReportHtml(String code, String kind, String kq_duration, String curpagestr, ReportParseVo parsevo, UserView userView, HashMap formHM) throws GeneralException
    {
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (uplevel == null || uplevel.length() == 0) {
            uplevel = "0";
        }
        ArrayList htmlList = new ArrayList();
        try
        {
            this.kqtablejudge = getkqtablejudge(this.conn); // 考勤表：true=展现本月出缺勤情况统计小计
                                                            // false=不展现
            this.parsevo = parsevo;
            this.userView = userView;
            if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) && (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) && (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) && (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) && (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm())))
            {
                this.noSelected = true;

            }
            // System.out.println("实际高度="+getFactHeight());
            // System.out.println("实际宽度="+getFactWidth());
            KqReportInit kqReprotInit = new KqReportInit(this.conn);
            ArrayList item_list = kqReprotInit.getKq_Item_list();// 考勤参数
            double spare_h = getSpareHieght(item_list);
            int pagesize = 0;
            if ("#pr[1]".equals(parsevo.getBody_pr()) && parsevo.getBody_rn() != null && parsevo.getBody_rn().length() > 0)
            {
                pagesize = Integer.parseInt(parsevo.getBody_rn());
            }
            else
            {
                pagesize = (int) spare_h / (Integer.parseInt(parsevo.getBody_fz()) + 13) -1;
                // pagesize=1;
            }
            // ArrayList kq_dbase_list=userView.getPrivDbList(); //这取的是全部人员库
            // 这里数据考勤表有个错误，这里得到的是全部人员库，但是应该是考勤员权限下的人员库才对
            /** 得到考勤权限下的人员库* */
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            /** 结束* */
            ArrayList datelist = getDateList(this.conn, kq_duration);
            if ("self".equals(this.getSelf_flag()))
            {
                this.whereIN = " from " + this.getCurTab();
                if (this.userView.getUserDeptId() != null && this.userView.getUserDeptId().length() > 0) {
                    this.whereIN += " WHERE e0122 like '" + this.userView.getUserDeptId() + "%'";
                } else if (this.userView.getUserOrgId() != null && this.userView.getUserOrgId().length() > 0) {
                    this.whereIN += "  WHERE b0110 like '" + this.userView.getUserOrgId() + "%'";
                } else {
                    this.whereIN += " WHERE a0100='" + this.userView.getA0100() + "' and nbase='" + this.userView.getDbname() + "'";
                }
            }
            int allrows;
            String dbt = this.getDbtype();
            if ("all".equalsIgnoreCase(dbt))
            {
                allrows = getAllRecordNum(code, kind, datelist, kq_dbase_list);
            }
            else
            {
                allrows = getAllRecordNum2(code, kind, datelist, dbt);
            }
            // int allrows =getAllRecordNum(code,kind,datelist,kq_dbase_list);

            int curpage = 1;
            if (curpagestr != null && curpagestr.length() > 0)
            {
                curpage = Integer.parseInt(curpagestr);
            }
            
            if(pagesize==0) {
                pagesize = 1;
            }
            int sum_page = (allrows - 1) / pagesize + 1;
            curpage = getCurpage(curpage, pagesize, sum_page);

            // System.out.println("当前页="+curpage+" 行数="+pagesize+"
            // 总页数="+sum_page+" 总行数="+allrows+"剩余高度="+spare_h);
            StringBuffer html = new StringBuffer();
            String titleHtml = getTableTitle();
            String width = getFactWidth();

            String headHtml = getTableHead(code, kq_duration, curpage, sum_page, datelist, kqtablejudge);
            html.append(" <table   border='0' cellspacing='0'  align='center' cellpadding='0'");
            html.append(" class='BackText' ");
            html.append(" style='position:absolute;top: " + parsevo.getTop() + "");
            html.append(";left: " + parsevo.getLeft() + ";width: " + width + "'> \n ");
            html.append(" <tr valign='middle' align='center'> \n ");
            html.append(" <td >");
            html.append(titleHtml);// 标题信息
            html.append("</td></tr><tr><td>");
            html.append(headHtml);// 表头信息
            html.append("</td></tr><tr><td>");
            // body信息
            ArrayList keylist = new ArrayList();
            // keylist.add("q03z0");
            keylist.add("a0100");
            keylist.add("nbase");
            // 前面固定值
            ArrayList a0100list = getA0100List(code, kind, datelist, curpage, pagesize, kq_dbase_list, keylist, allrows);

            // html.append(getBodyHtml(code,kind,datelist,curpage,pagesize,a0100list,item_list));
            // //原始
            // kq_duration考勤区间，用来到Q05进行查询,kqtablejudge=true 展现首钢小计
            html.append(getBodyHtml(code, kind, datelist, curpage, pagesize, a0100list, item_list, kq_duration, kqtablejudge));

            html.append("</td></tr>");
            html.append(getTileHtml(code, kind, curpage, sum_page, item_list));
            html.append("");
            html.append("<tr><td>");
            // body高度
            int body_h = a0100list.size() * (Integer.parseInt(parsevo.getBody_fz()) + 13);

            // System.out.println("BODY高度="+body_h);
            // double isUser_height=getIsUseHieght(item_list);//以用高度
            // String isUser_h=KqReportInit.round(isUser_height+"",0);
            // int
            // turnpage_sytle_h=Integer.parseInt(isUser_h)+Integer.parseInt(getPxFromMm(parsevo.getTop()))+body_h;
            /** *显示翻页,表格的绝对定位** */
            // String
            // turnpage_sytle="position:absolute;top:"+turnpage_sytle_h+";left:
            // "+parsevo.getLeft()+";width: "+width;
            String turnTable = getTurnPageCode(curpage, "", sum_page);
            html.append(turnTable);
            html.append("</td></tr></table>");
            htmlList.add(html.toString());

            htmlList.add("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return htmlList;
    }

    /**
     * 得到标题内容
     * 
     */
    public String getTableTitle()
    {

        String width = getFactWidth();
        String height = getPxFromMm(parsevo.getTitle_h());
        String sytle = getFontStyle(parsevo.getTitle_fn(), parsevo.getTitle_fi(), parsevo.getTitle_fu(), parsevo.getTitle_fb(), parsevo.getTitle_fz());
        String style_name = getStyleName("3");
        StringBuffer titleTable = new StringBuffer("");
        String[] temp = transAlign(7);
        String aValign = temp[0];
        String aAlign = temp[1];
        // titleTable.append(" <table cellspacing='0' align='left' valign='top'
        // cellpadding='0' border='0'> \n");

        // 将表格宽度改为100%，防止该表格和后面的表格对不齐 --wangzhongjun 2010.4.9
        titleTable.append(" <table width='100%'  cellspacing='0'  align='left'  valign='top' cellpadding='0' border='0'> \n");

        titleTable.append(" <tr valign='middle' align='center'> \n ");
        titleTable.append(" <td width='" + width + "' height='" + height + "'");

        titleTable.append(" class='" + style_name + " common_border_color' ");
        titleTable.append(" valign='");
        titleTable.append(aValign);
        titleTable.append("' align='");
        titleTable.append(aAlign);
        titleTable.append("'> \n ");

        titleTable.append(" <font face='");
        if (parsevo.getTitle_fn() != null && parsevo.getTitle_fn().length() > 0)
        {
            titleTable.append(parsevo.getTitle_fn());
        }
        else
        {
            titleTable.append(parsevo.getName());
        }
        titleTable.append("' style='");
        titleTable.append(sytle);
        titleTable.append("' > \n ");
        if (parsevo.getTitle_fw() == null || parsevo.getTitle_fw().length() <= 0)
        {
            titleTable.append(parsevo.getName());
        }
        else
        {
            titleTable.append(parsevo.getTitle_fw());
        }
        titleTable.append("</font></td></tr></table> \n ");
        return titleTable.toString();
    }

    /**
     * 得到表头内容 #p（页码） #c 总页数 #e 制作人 #u 制作人所在的单位 #d 日期 #t 时间 #fn宋体 字体名称 #fz15 字体大小
     * #fb[0|1] 黑体 #fi[0|1] 斜体 #fu[0|1] 下划线 #pr[0|1] 页行数 #fh40 首钢增加 kqtablejudge
     * true展现本月出缺勤情况统计小计
     */
    public String getTableHead(String code, String coursedate, int curpage, int sum_page, ArrayList datelist, boolean kqtablejudge) throws GeneralException
    {
        StringBuffer headHtml = new StringBuffer();
        String[] codeitem = getCodeItemDesc(code);
        String fact_width = getFactWidth();
        String head_height = getPxFromMm(parsevo.getHead_h());
        String sytle = getFontStyle(parsevo.getHead_fn(), parsevo.getHead_fi(), parsevo.getHead_fu(), parsevo.getHead_fb(), parsevo.getHead_fz());
        try
        {
            // headHtml.append("<table cellspacing='0' align='left' valign='top'
            // width='"+fact_width+"'> \n");

            // 将表格宽度改为100%,防止表格对不齐， ---wangzhongjun 2010.4.9
            headHtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");

            headHtml.append(" <tr valign='middle' align='center'> \n ");
            /** 单位* */
            String dv_content = "";
            if (codeitem[1] == null || codeitem[1].length() <= 0)
            {
                dv_content = "&nbsp;&nbsp;单位：";
            }
            else
            {
                dv_content = "&nbsp;&nbsp;单位：" + codeitem[1];
            }

            String dv_style_name = getStyleName("2");

            headHtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, head_height, dv_content, dv_style_name));
            /** 部门 */
            String bm_content = "";
            if (codeitem[0] == null || codeitem[0].length() <= 0)
            {
                bm_content = "&nbsp;&nbsp;部门：";
            }
            else
            {
                bm_content = "&nbsp;&nbsp;部门：" + codeitem[0];
            }

            String bm_style_name = getStyleName("2");
            // if (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) &&
            // !(kqtablejudge &&
            // "#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
            // bm_style_name = getStyleName("");
            // }
            headHtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, head_height, bm_content, bm_style_name));
            /** 日期 */
            // if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) ||
            // this.noSelected) {
            CommonData vo = (CommonData) datelist.get(0);
            String start_date = vo.getDataName();
            vo = (CommonData) datelist.get(datelist.size() - 1);
            String end_date = vo.getDataName();
            String da_content = "&nbsp;&nbsp;  " + coursedate + " (" + start_date + "~" + end_date + ")";
            String da_style_name = getStyleName("");
            headHtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, head_height, da_content, da_style_name));
            // }
            // 首钢 本月出缺勤情况统计小计 true展现
            if (kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected))
            {
                headHtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, head_height, "本月出缺勤情况统计小计", getStyleName("0")));
            }
            headHtml.append("</tr></table>");
            headHtml.append("</td></tr>");
            /** 基本参数* */

            // headHtml.append("<table cellspacing='0' align='left' valign='top'
            // width='"+fact_width+"'> \n");
            // 将表格宽度改为100%,防止表格对不齐， ---wangzhongjun 2010.4.9
            if ("#e".equals(parsevo.getHead_e().trim()) || "#u".equals(parsevo.getHead_u().trim()) || "#d".equals(parsevo.getHead_d().trim()) || "#t".equals(parsevo.getHead_t().trim()) || "#p".equals(parsevo.getHead_p().trim()) || "#c".equals(parsevo.getHead_c().trim()))
            {
                headHtml.append("<tr><td><table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
                headHtml.append(" <tr valign='middle' align='center'> \n ");
                /** 制作人* */
                int i = 0;
                String temp_p = "5";
                if ("#e".equals(parsevo.getHead_e().trim()))
                {

                    String e_str = "&nbsp;&nbsp;制作人: " + this.userView.getUserFullName();
                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    
                    else
                    {
                        temp_p = "5";
                    }

                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, e_str, getStyleName(temp_p)));
                }
                /** 制作人单位* */
                if ("#u".equals(parsevo.getHead_u().trim()))
                {
                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    else
                    {
                        temp_p = "5";
                    }

                    String u_code = "";
                    if (!userView.isSuper_admin())
                    {
                        if (userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0)
                        {
                            u_code = userView.getUserOrgId();
                        }
                        else
                        {
                            u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
                        }
                    }
                    String[] u_codeitem = getCodeItemDesc(u_code);
                    String u_str = "&nbsp;制作人单位: " + u_codeitem[0];
                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, u_str, getStyleName(temp_p)));
                }
                /** 制作日期* */
                if ("#d".equals(parsevo.getHead_d().trim()))
                {

                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    else
                    {
                        temp_p = "5";
                    }

                    String d_str = "&nbsp;&nbsp;制作日期: " + PubFunc.getStringDate("yyyy.MM.dd");
                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, d_str, getStyleName(temp_p)));
                }
                /** 制作时间* */
                if ("#t".equals(parsevo.getHead_t().trim()))
                {

                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    else
                    {
                        temp_p = "5";
                    }
                    String t_str = "&nbsp;&nbsp;时间: " + PubFunc.getStringDate("HH:mm:ss");
                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, t_str, getStyleName(temp_p)));
                }
                /** 页码* */
                if ("#p".equals(parsevo.getHead_p().trim()))
                {

                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    else
                    {
                        temp_p = "5";
                    }
                    String p_str = "&nbsp;&nbsp;页码:" + curpage + "";
                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, p_str, getStyleName(temp_p)));
                }
                /** 总页码* */
                if ("#c".equals(parsevo.getHead_c().trim()))
                {

                    if (i == 0)
                    {
                        temp_p = "1";
                        i = 1;
                    }
                    else
                    {
                        temp_p = "5";
                    }

                    String c_str = "&nbsp;&nbsp;总页码:" + sum_page + "";
                    headHtml.append(executeTable(1, 6, parsevo.getHead_fn(), sytle, head_height, c_str, getStyleName(temp_p)));
                }
                headHtml.append("</td></tr></table>");

            }
            /** 结束* */
            // headHtml.append("</td></tr></table>");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return headHtml.toString();
    }

    /**
     * 得到body头内容
     * 
     * 
     */
    public String getBodyHeadHtml(ArrayList datelist) throws GeneralException
    {
        StringBuffer bodyheadhtml = new StringBuffer();
        String sytle = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz());
        int body_height = Integer.parseInt(parsevo.getBody_fz()) + 13;
        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "序号", getStyleName("1")));
        // bodyheadhtml.append(executeTable(1,7,parsevo.getHead_fn(),sytle,body_height+"","人员库",getStyleName("6")));
        if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected)
        {
            bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "部门", getStyleName("5")));
        }
        if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected)
        {
            KqParameter para = new KqParameter();
            if (!"1".equals(para.getKq_orgView_post())) {
                bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "岗位", getStyleName("5")));
            }
        }
        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "姓名", getStyleName("5")));
        bodyheadhtml.append(executeTable2(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "签到薄", getStyleName("5")));
        if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected)
        {
            bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", "工号", getStyleName("5")));
        }
        ArrayList kqq03list = getKqBookItemList();
        if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected)
        {
            for (int i = 1; i <= datelist.size(); i++)
            {
                CommonData vo = (CommonData) datelist.get(i - 1);
                String date = vo.getDataValue();
                if (i == datelist.size())
                {
                    if (kqq03list.size() > 0)
                    {
                        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", date, getStyleName("5")));
                    }
                    else
                    {
                        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", date, getStyleName("5")));
                    }
                }
                else
                {
                    bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", date, getStyleName("5")));
                }
            }
        }
        String kqtable = SystemConfig.getPropertyValue("kqtable");// 用来控制考勤表头竖型展现
                                                                    // 0和null都是正常展现
        if (kqq03list.size() > 0 && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected))
        {
            for (int t = 0; t < kqq03list.size(); t++)
            {
                FieldItem fielditem = (FieldItem) kqq03list.get(t);
                String name = fielditem.getItemdesc();
                if (kqtable != null && kqtable.length() > 0 && "5".equals(kqtable))
                {
                    if ((t + 1) == kqq03list.size())
                    {
                        bodyheadhtml.append(executeTables(1, 7, parsevo.getHead_fn(), sytle, body_height + "", name, getStyleName("5")));
                    }
                    else
                    {
                        bodyheadhtml.append(executeTables(1, 7, parsevo.getHead_fn(), sytle, body_height + "", name, getStyleName("5")));
                    }
                }
                else
                {
                    if ((t + 1) == kqq03list.size())
                    {
                        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", name, getStyleName("5")));
                    }
                    else
                    {
                        bodyheadhtml.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_height + "", name, getStyleName("5")));
                    }
                }
            }
        }
        return bodyheadhtml.toString();
    }

    /***************************************************************************
     * 得到body信息内容 首钢新增参数：kqtablejudge=trun 展现小计 kq_duration 考勤表年月用来到Q05里进行查询
     **************************************************************************/
    public String getBodyHtml(String code, String kind, ArrayList datelist, int curpage, int pagesize, ArrayList a0100list, ArrayList item_list, String kq_duration, boolean kqtablejudge) throws GeneralException
    {
        StringBuffer bodyhtml = new StringBuffer();
        int body_hieght = Integer.parseInt(parsevo.getBody_fz()) + 13;
        String fact_width = getFactWidth();
        String sytle = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz());
        bodyhtml.append("<table  cellspacing='0'  align='left'  valign='top' width='" + fact_width + "'> \n");
        bodyhtml.append(" <tr valign='middle' align='center'> \n ");
        bodyhtml.append(getBodyHeadHtml(datelist)); // 得到body头内容
        bodyhtml.append("</tr>");

        for (int i = 0; i < a0100list.size(); i++)
        {
            int num = (curpage - 1) * pagesize + i + 1;
            bodyhtml.append("<tr>");
            // 添加序号
            bodyhtml.append(executeTable(28, 7, parsevo.getBody_fn(), sytle, body_hieght + "", num + "", getStyleName("1")));
            String a0100[] = (String[]) a0100list.get(i); // 有姓名
            String one_date = getOneA0100Data(code, kind, a0100, datelist, body_hieght, item_list); // 头下面每个人的1到30号的内容
            bodyhtml.append(one_date);
            if (kqtablejudge && "#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)
            {
                String one_value = getOneA0100Value(code, kind, a0100, kq_duration, body_hieght);
                bodyhtml.append(one_value);
            }
            bodyhtml.append("</tr>");

        }
        bodyhtml.append("</table>");
        return bodyhtml.toString();
    }

    /***************************************************************************
     * 得到表尾数据
     **************************************************************************/
    public String getTileHtml(String code, String kind, int curpage, int sum_page, ArrayList item_list) throws GeneralException
    {
        StringBuffer tilehtml = new StringBuffer();
        String fact_width = getFactWidth();
        String tile_height = getPxFromMm(parsevo.getTile_h());
        ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);

        String sytle = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz());

        StringBuffer note_str = new StringBuffer();
        StringBuffer note_len_str = new StringBuffer();
        note_str.append("备注：1.");
        note_len_str.append("备注：1.");
        for (int i = 0; i < fielditemlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            // System.out.println(fielditem.getItemdesc());
            /*
             * if("N".equals(fielditem.getItemtype())) {
             */
            if (!"i9999".equals(fielditem.getItemid()))
            {

                String kq_item[] = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                if (kq_item[0] != null && kq_item[0].length() > 0)
                {
                    note_str.append(fielditem.getItemdesc() + "(");
                    note_str.append("<font color='" + kq_item[1] + "'>");
                    note_str.append(kq_item[0]);
                    note_str.append("</font>)");
                    note_len_str.append(fielditem.getItemdesc() + "(" + kq_item[0] + ")");
                }
            }
            // }
        }
        int strlen = note_len_str.toString().length() * Integer.parseInt(parsevo.getBody_fz());
        int note_h_1 = Integer.parseInt(parsevo.getBody_fz()) + 6;
        int numrow_1 = getNumRow(strlen);
        if (numrow_1 != 0)
        {
            note_h_1 = note_h_1 * numrow_1;
        }
        // tilehtml.append("<table cellspacing='0' align='left' valign='top'
        // width='"+fact_width+"'> \n");
        if (("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) || (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0))
        {
            tilehtml.append("<tr><td>");
            // 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun 2010.4.9
            tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");
            if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected)
            {
                tilehtml.append(" <tr valign='middle' align='center'> \n ");
                tilehtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, note_h_1 + "", note_str.toString(), getStyleName("1")));
                tilehtml.append("</tr>");
            }
            if (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0)
            {
                tilehtml.append("<tr>");
                String tile_fw = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2." + parsevo.getTile_fw();
                int str_tile_2 = tile_fw.length() * Integer.parseInt(parsevo.getBody_fz());
                int numrow_2 = getNumRow(str_tile_2);
                int note_h_2 = Integer.parseInt(parsevo.getBody_fz()) + 6;
                if (numrow_2 != 0)
                {
                    note_h_2 = note_h_2 * numrow_2;
                }

                tilehtml.append(executeTable(1, 6, parsevo.getBody_fn(), sytle, note_h_2 + "", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2." + parsevo.getTile_fw(), getStyleName("1")));
                tilehtml.append("</tr>");
            }
            tilehtml.append("</table></td></tr>");
        }

        /** 基本参数* */
        String sytle_t = getFontStyle(parsevo.getTile_fn(), parsevo.getTile_fi(), parsevo.getTile_fu(), parsevo.getTile_fb(), parsevo.getTile_fz());
        // tilehtml.append("<table cellspacing='0' align='left' valign='top'
        // width='"+fact_width+"'> \n");
        if ("#u".equals(parsevo.getTile_u().trim()) || "#d".equals(parsevo.getTile_d().trim()) || "#e".equals(parsevo.getTile_e().trim()) || "#t".equals(parsevo.getTile_t().trim()) || "#p".equals(parsevo.getTile_p().trim()) || "#c".equals(parsevo.getTile_c().trim()))
        {
            tilehtml.append("<tr><td>");
            // 将表格宽度设置为100%，防止与其他表格对不齐 ---wangzhongjun 2010.4.9
            tilehtml.append("<table  cellspacing='0'  align='left'  valign='top' width='100%'> \n");

            tilehtml.append(" <tr valign='middle' align='center'> \n ");
            /** 制作人* */
            int i = 0;
            String temp_p = "5";
            /** 制作人单位* */
            if ("#u".equals(parsevo.getTile_u().trim()))
            {
                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }

                String u_code = "";
                if (!userView.isSuper_admin())
                {
                    if (userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0)
                    {
                        u_code = userView.getUserOrgId();
                    }
                    else
                    {
                        u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
                    }
                }
                String[] u_codeitem = getCodeItemDesc(u_code);
                String u_str = "&nbsp;制作人单位: " + u_codeitem[0];
                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, u_str, getStyleName(temp_p)));
            }
            /** 制作日期* */
            if ("#d".equals(parsevo.getTile_d().trim()))
            {

                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }

                // String d_str="&nbsp;&nbsp;制作日期:
                // "+PubFunc.getStringDate("yyyy.MM.dd");//原来的 取得系统时间
                String d_str = "&nbsp;&nbsp;制作日期:<input type='text' name='element' size='10' value='" + PubFunc.getStringDate("yyyy.MM.dd") + "' class='text' id='sjelement' maxlength='10'>";
                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, d_str, getStyleName(temp_p)));
            }
            if ("#e".equals(parsevo.getTile_e().trim()))
            {

                String e_str = "&nbsp;&nbsp;制作人: " + this.userView.getUserFullName();
                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }

                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, e_str, getStyleName(temp_p)));
            }
            /** 制作时间* */
            if ("#t".equals(parsevo.getTile_t().trim()))
            {

                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }
                // String t_str="&nbsp;&nbsp;时间:
                // "+PubFunc.getStringDate("HH:mm:ss");
                String t_str = "&nbsp;&nbsp;时间:<input type='text' name='timexr' size='10' value='" + PubFunc.getStringDate("HH:mm:ss") + "' class='text' id='timeqd' maxlength='8'>";
                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, t_str, getStyleName(temp_p)));
            }
            /** 页码* */
            if ("#p".equals(parsevo.getTile_p().trim()))
            {

                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }
                String p_str = "&nbsp;&nbsp;页码:" + curpage + "";
                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, p_str, getStyleName(temp_p)));
            }
            /** 总页码* */
            if ("#c".equals(parsevo.getTile_c().trim()))
            {

                if (i == 0)
                {
                    temp_p = "1";
                    i = 1;
                }
                else
                {
                    temp_p = "5";
                }

                String c_str = "&nbsp;&nbsp;总页码:" + sum_page + "";
                tilehtml.append(executeTable(1, 6, parsevo.getTile_fn(), sytle_t, tile_height, c_str, getStyleName(temp_p)));
            }

            tilehtml.append("</tr></table>");

            tilehtml.append("</td></tr>");
        }
        // System.out.println(tilehtml.toString());
        return tilehtml.toString();
    }

    /**
     * 生成一个单元格)
     * 
     * @param border
     *            边宽
     * @param align
     *            字体布局位置
     * @param type
     *            1:表格 2：标题 30:名字
     * @param context
     *            内容
     */
    public String executeTable(int type, int Align, String fontName, String fontStyle, String height, String context, String style_name)
    {
        return executeTable(type, Align, fontName, fontStyle, height, context, style_name, null);
    }
    
    /**
     * 生成一个单元格
     * @Title: executeTable   
     * @Description:    
     * @param type
     * @param Align
     * @param fontName
     * @param fontStyle
     * @param height
     * @param context
     * @param style_name
     * @param alink 单元格内容超链接
     * @return
     */
    private String executeTable(int type, int Align, String fontName, String fontStyle, String height, String context, String style_name, String alink)
    {

        StringBuffer tempTable = new StringBuffer("");
        String[] temp = transAlign(Align);
        String aValign = temp[0];
        String aAlign = temp[1];

        tempTable.append(" <td height='" + height + "'");
        if (type == 1)
        {
            tempTable.append(" class='" + style_name + " common_border_color' ");
        }
        // 姓名样式
        if (type == 30)
        {
            tempTable.append(" class='" + style_name + " common_border_color' ");
            tempTable.append(" nowrap='nowrap'");
            tempTable.append("style='width:50px;'");
        }
        // 工号样式
        if (type == 29)
        {
            tempTable.append(" class='" + style_name + " common_border_color' ");
            tempTable.append(" nowrap='nowrap'");
            tempTable.append("style='width:70px;'");
        }
        // 序号样式
        if (type == 28)
        {
            tempTable.append(" class='" + style_name + " common_border_color' ");
            tempTable.append(" nowrap='nowrap'");
            tempTable.append("style='width:40px;'");
        }
        tempTable.append(" valign='");
        tempTable.append(aValign);
        tempTable.append("' align='");
        tempTable.append(aAlign);
        tempTable.append("'> \n ");
        
        if (null != alink && !"".equals(alink)) {
            tempTable.append("<a ");
            tempTable.append(alink);
            tempTable.append(">");
        }

        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0)
        {
            tempTable.append(" <font face='");
            tempTable.append(fontName);
            tempTable.append("' style='");
            tempTable.append(fontStyle);
            tempTable.append("' > \n ");
        }

        if (context != null && context.trim().length() > 0)
        {
            tempTable.append(context);
        }
        else
        {
            tempTable.append("&nbsp;&nbsp;");
        }
        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0) {
            tempTable.append("</font>");
        }
        
        if (null != alink && !"".equals(alink)) {
            tempTable.append("</a>");
        }

        tempTable.append("</td> \n ");

        return tempTable.toString();
    }

    // 字体竖型排
    public String executeTables(int type, int Align, String fontName, String fontStyle, String height, String context, String style_name)
    {

        StringBuffer tempTable = new StringBuffer("");
        String[] temp = transAlign(Align);
        String aValign = temp[0];
        String aAlign = temp[1];

        tempTable.append(" <td height='" + height + "' width='20'");
        if (type == 1)
        {
            tempTable.append(" class='" + style_name + " common_border_color'");
        }
        tempTable.append(" valign='");
        tempTable.append(aValign);
        tempTable.append("' align='");
        tempTable.append(aAlign);
        tempTable.append("'> \n ");
        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0)
        {
            tempTable.append(" <font face='");
            tempTable.append(fontName);
            tempTable.append("' style='");
            tempTable.append(fontStyle);
            tempTable.append("' > \n ");
        }

        if (context != null && context.length() > 0)
        {
            // System.out.println("TTTT = "+context.length());
            for (int p = 0; p < context.length(); p++)
            {

                String d = context.substring(p, p + 1);
                tempTable.append(d);
                tempTable.append("<br>");
            }
            // tempTable.append(context);
        }
        else
        {
            tempTable.append("&nbsp;&nbsp;");
        }
        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0) {
            tempTable.append("</font>");
        }

        tempTable.append("</td> \n ");

        return tempTable.toString();
    }

    // 签到簿宽度
    public String executeTable2(int type, int Align, String fontName, String fontStyle, String height, String context, String style_name)
    {

        StringBuffer tempTable = new StringBuffer("");
        String[] temp = transAlign(Align);
        String aValign = temp[0];
        String aAlign = temp[1];

        tempTable.append(" <td height='" + height + "' width='40'");
        if (type == 1)
        {
            tempTable.append(" class='" + style_name + " common_border_color' ");
        }
        tempTable.append(" valign='");
        tempTable.append(aValign);
        tempTable.append("' align='");
        tempTable.append(aAlign);
        tempTable.append("'> \n ");
        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0)
        {
            tempTable.append(" <font face='");
            tempTable.append(fontName);
            tempTable.append("' style='");
            tempTable.append(fontStyle);
            tempTable.append("' > \n ");
        }

        if (context != null && context.length() > 0)
        {
            tempTable.append(context);
        }
        else
        {
            tempTable.append("&nbsp;&nbsp;");
        }
        if (fontName != null && fontName.length() > 0 && fontStyle != null && fontStyle.length() > 0) {
            tempTable.append("</font>");
        }

        tempTable.append("</td> \n ");

        return tempTable.toString();
    }

    /**
     * 生成一个单元格)
     * 
     * @param border
     *            边宽
     * @param align
     *            字体布局位置
     * @param type
     *            1:表格 2：标题
     * @param context
     *            内容
     */
    // public String executeFont(String fontName,String fontStyle,String
    // context)
    // {
    //		
    // StringBuffer tempTable=new StringBuffer("");
    // tempTable.append("<font face='");
    // tempTable.append(fontName);
    // tempTable.append("' style='");
    // tempTable.append(fontStyle);
    // tempTable.append("' >");
    // if(context!=null&&context.length()>0)
    // {
    // tempTable.append(context);
    // }
    // else
    // {
    // tempTable.append("&nbsp;&nbsp;");
    // }
    // tempTable.append("</font>");
    //		
    // return tempTable.toString();
    // }
    public String executeFont(String fontName, String fontStyle, String context)
    {

        StringBuffer tempTable = new StringBuffer("");
        if (context != null && context.length() > 0)
        {
            tempTable.append("<font face='");
            tempTable.append(fontName);
            tempTable.append("' style='");
            tempTable.append(fontStyle);
            tempTable.append("' >");
            tempTable.append(context);
            tempTable.append("</font>");
        }
        return tempTable.toString();
    }

    /**
     * 转换成字体布局字符
     */
    private String[] transAlign(int Align)
    {
        String[] temp = new String[2];
        if (Align == 0)
        {
            temp[0] = "top";
            temp[1] = "left";
        }
        else if (Align == 1)
        {
            temp[0] = "top";
            temp[1] = "center";
        }
        else if (Align == 2)
        {
            temp[0] = "top";
            temp[1] = "right";
        }
        else if (Align == 3)
        {
            temp[0] = "bottom";
            temp[1] = "left";
        }
        else if (Align == 4)
        {
            temp[0] = "bottom";
            temp[1] = "center";
        }
        else if (Align == 5)
        {
            temp[0] = "bottom";
            temp[1] = "right";
        }
        else if (Align == 6)
        {
            temp[0] = "middle";
            temp[1] = "left";
        }
        else if (Align == 7)
        {
            temp[0] = "middle";
            temp[1] = "center";
        }
        else if (Align == 8)
        {
            temp[0] = "middle";
            temp[1] = "right";
        }
        return temp;
    }

    public String getFont(String font_size, String color, String info)
    {
        StringBuffer fontStr = new StringBuffer();
        fontStr.append("<font face='宋体' style='font-weight:normal;font-size:" + font_size + "' >");
        fontStr.append(info);
        fontStr.append("</font></td></tr></table>");

        return fontStr.toString();
    }

    /**
     * 通过code得到codeItemDesc
     */
    public String[] getCodeItemDesc(String code)
    {
    	// 36926 linbz 初始进来code为空，故重新取该用户的考勤权限
    	if(StringUtils.isEmpty(code)) {
    		code = RegisterInitInfoData.getKqPrivCode(userView);
    	}
        RowSet rowSet = null;
        String codeItemDesc[] = new String[2];
        String parentid = "";
        String sql = "select codeitemdesc,parentid from organization where codeitemid=?";
        if("UN".equalsIgnoreCase(code)) {
            sql = "select codeitemdesc,parentid from organization where codesetid=?";
        }
        ArrayList list = new ArrayList();
        list.add(code);
        String desc1 = "";
        String desc2 = "";
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            rowSet = dao.search(sql, list);
            if (rowSet.next())
            {
                codeItemDesc[0] = rowSet.getString("codeitemdesc");
                desc1 = rowSet.getString("codeitemdesc");
                if (desc1 == null || desc1.length() <= 0) {
                    desc1 = "";
                }
                parentid = rowSet.getString("parentid");
                list = new ArrayList();
                list.add(parentid);
                String sqlp = "select codeitemdesc from organization where codeitemid=?";
                rowSet = dao.search(sqlp, list);
                if (rowSet.next())
                {
                    desc2 = rowSet.getString("codeitemdesc");
                    if (desc2 == null || desc2.length() <= 0) {
                        desc2 = "";
                    }
                    codeItemDesc[1] = rowSet.getString("codeitemdesc");
                }
            }
            if (desc2.equals(desc1))
            {
                codeItemDesc[0] = "";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return codeItemDesc;
    }

    /**
     * 得到样式
     * 
     * @param fn
     *            字体
     * @param fi
     *            斜体
     * @param fu
     *            下划线
     * @param fb
     *            粗体
     * @param fz
     *            大小
     * @return 样式内容
     */
    public String getFontStyle(String fn, String fi, String fu, String fb, String fz)
    {
        StringBuffer style = new StringBuffer();
        if (fn != null && fn.length() > 0)
        {
            style.append("font-family: " + fn + ";");
        }
        else
        {
            style.append("font-family: '宋体';");
        }
        if ("#fi[1]".equals(fi))// 斜体
        {
            style.append("font-style: italic;");
        }
        if ("#fu[1]".equals(fu))// 下划线
        {
            style.append("text-decoration: underline;");
        }
        if ("#fb[1]".equals(fb))
        {
            style.append("font-weight: bolder;");
        }
        if (fz != null && fz.length() > 0)
        {
            style.append("font-size: " + fz + "px;");
        }
        else
        {
            style.append("font-size: 12px;");
        }
        return style.toString();
    }

    /**
     * 得到样式
     * 
     * @param fn
     *            字体
     * @param fi
     *            斜体
     * @param fu
     *            下划线
     * @param fb
     *            粗体
     * @param fz
     *            大小
     * @return 样式内容
     */
    public String getFontStyle(String fn, String fi, String fu, String fb, String fz, String color)
    {
        StringBuffer style = new StringBuffer();
        if (fn != null && fn.length() > 0)
        {
            style.append("font-family: " + fn + ";");
        }
        else
        {
            style.append("font-family: '宋体';");
        }
        if ("#fi[1]".equals(fi))// 斜体
        {
            style.append("font-style: italic;");
        }
        if ("#fu[1]".equals(fu))// 下划线
        {
            style.append("text-decoration: underline;");
        }
        if ("#fb[1]".equals(fb))
        {
            style.append("font-weight: bolder;");
        }
        if (fz != null && fz.length() > 0)
        {
            style.append("font-size: " + fz + "px;");
        }
        else
        {
            style.append("font-size: 12px;");
        }
        if (color != null && color.length() > 0)
        {
            style.append("color: " + color + ";");
        }
        else
        {
            style.append("color: #FF0000;");
        }

        return style.toString();
    }

    /*
     * 处理页面显示虚线
     */
    public String getStyleName(String temp)
	{
		//处理虚线	L,T,R,B,
	    String style_name="RecordRow_self common_border_color";
	    if("0".equals(temp))
	    {
	    	style_name="RecordRow_self_l common_border_color";
	    }
	    else if("1".equals(temp))
	    {
	    	style_name="RecordRow_self_t common_border_color";
	    }
	    else if("2".equals(temp))
	    {
	    	style_name="RecordRow_self_r common_border_color";
	    }
	    else if("3".equals(temp))
	    {
	    	style_name="RecordRow_self_b common_border_color";
	    }else if("4".equals(temp))
	    {
	    	style_name="RecordRow_self_two common_border_color";
	    }else if("5".equals(temp))
	    {
	    	style_name="RecordRow_self_t_l common_border_color";
	    }else if("6".equals(temp))
	    {
	    	style_name="RecordRow_self_t_r common_border_color";
	    }
	    
		return style_name;
	}

    // 得到在考勤范围内的部门员工编号，并添加到list中
    public ArrayList getA0100List(String code, String kind, ArrayList datelist, int curpage, int pagesize, ArrayList kq_dbase_list, ArrayList keylist, int allNum) throws GeneralException
    {

        CommonData start_vo = (CommonData) datelist.get(0);
        String start_date = start_vo.getDataName();
        CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
        String end_date = end_vo.getDataName();
        ArrayList a0100whereIN = new ArrayList();
        String dbt = this.getDbtype();
        if (!"self".equals(this.getSelf_flag()))
        {
            if ("all".equalsIgnoreCase(dbt))
            {
                for (int i = 0; i < kq_dbase_list.size(); i++)
                {
                    String dbase = kq_dbase_list.get(i).toString();
                    String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);
                    a0100whereIN.add(whereA0100In);
                }
            }
            else
            {
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbt);
                a0100whereIN.add(whereA0100In);
            }
        }

        String sort = "";
        String desc = "";
        StringBuffer coloum = new StringBuffer();
        SortBo bo = new SortBo(this.conn, this.userView);
        KqReportInit init = new KqReportInit(this.conn);
//        if (sortItem != null && sortItem.length() > 0 && !"not".equalsIgnoreCase(sortItem))
//        {
//            sort = bo.getSortSql(sortItem, "");
//            desc = bo.getSortSql(sortItem, "", false);
//            String[] strs = sortItem.split("`");
//            for (int i = 0; i < strs.length; i++)
//            {
//                String[] str = strs[i].split(":");
//                if (!",a0100,a0101,e0122,nbase,b0110,e01a1,dbid,a0000,".contains("," + str[0].toLowerCase() + ","))
//                {
//                    coloum.append(str[0]);
//                    coloum.append(",");
//                }
//            }
//        }
//        else if (bo.isExistSort())
//        {
//            sort = bo.getSortSqlTable("");
//            desc = bo.getSortSqlTable("", false);
//
//            String[] strs = bo.querrySort().split("`");
//            for (int i = 0; i < strs.length; i++)
//            {
//                String[] str = strs[i].split(":");
//                if (!",a0100,a0101,e0122,nbase,b0110,e01a1,dbid,a0000,".contains("," + str[0].toLowerCase() + ","))
//                {
//                    coloum.append(str[0]);
//                    coloum.append(",");
//                }
//            }
//        }
//        else
//        {
            sort = "order by dbid,a0000";
            desc = "order by dbid desc,a0000 desc";
//        }
        init.setColums(coloum.toString());
        // 前面 固定值
        String whereA0100 = "";

        if ("all".equalsIgnoreCase(dbt))
        {
            whereA0100 = init.select_kq_Distincta0100(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, kq_dbase_list, this.whereIN);
        }
        else
        {
            whereA0100 = init.select_kq_Distincta01002(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, dbt, this.whereIN);
        }

        String old_where = whereA0100;

        if (sort != null && sort.length() > 0)
        {
            whereA0100 = whereA0100.substring(0, whereA0100.lastIndexOf("order")) + " " + sort;
        }

        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList a0100list = new ArrayList();
        RowSet rowSet = null;
        try
        {

            /*
             * rowSet = dao.search(whereA0100); int num=1; int
             * start_record=(curpage-1)*pagesize+1; int
             * end_record=curpage*pagesize;
             * 
             * while(rowSet.next()) { if(num>=start_record&&num<=end_record) {
             * String a0100[] = new String[2];
             * a0100[0]=rowSet.getString("a0100");
             * a0100[1]=rowSet.getString("a0101"); a0100list.add(a0100); num++;
             * }else if(num<start_record) { num++; continue; }else
             * if(num>end_record) { num++; break; } }
             */
            // Sql中有 排序 还传入 list，会出现问题；所以可以不传入list;
            // rowSet=dao.search(whereA0100.toString(),pagesize,curpage,keylist);
            StringBuffer sql = new StringBuffer();
            if (Sql_switcher.searchDbServer() == 2)
            {
                sql.append("select * from (select b.*,rownum m from (");
                sql.append(whereA0100);
                sql.append(") b) c where m between ");
                sql.append(pagesize * (curpage - 1) + 1);
                sql.append(" and ");
                sql.append(pagesize * curpage);
            }
            else
            {
                int vp = 0;
                if ((pagesize * curpage) > allNum)
                {
                    vp = allNum - (pagesize * (curpage - 1));
                }
                else
                {
                    vp = pagesize;
                }
                sql.append("select * from (select top " + vp + " * from (select top ");
                sql.append(pagesize * curpage);
                sql.append("* from (");
                sql.append(whereA0100.substring(0, whereA0100.lastIndexOf("order")));
                sql.append(") qqq ");
                sql.append(sort);
                sql.append(") vvv ");
                sql.append(desc);
                sql.append(") mmm ");
                sql.append(sort);
            }

            // rowSet=dao.search(whereA0100.toString(),pagesize,curpage);
            rowSet = dao.search(sql.toString());
            while (rowSet.next())
            {
                String a0100[] = new String[6];
                a0100[0] = rowSet.getString("a0100");
                a0100[1] = rowSet.getString("a0101");
                a0100[2] = rowSet.getString("nbase");
                a0100[3] = getCardNO(rowSet.getString("a0100"), rowSet.getString("nbase"), dao);
                a0100[4] = rowSet.getString("e0122"); // 考勤表加入部门
                a0100[5] = rowSet.getString("e01a1"); // 考勤表加入职位
                a0100list.add(a0100);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        this.a0100sql = whereA0100.toString();
        a0100sql = a0100sql.replace("order by b0110,e0122,e01a1,a0100", "");
        a0100sql = a0100sql.replace("order by q03.b0110,q03.e0122,q03.e01a1,q03.a0100,q03.nbase", " ");
        a0100sql = a0100sql.replace("order by q03_arc.b0110,q03_arc.e0122,q03_arc.e01a1,q03_arc.a0100,q03_arc.nbase", " ");
        return a0100list;
    }

    public ArrayList getList(String sql)
    {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try
        {
            rowSet = dao.search(sql);
            while (rowSet.next())
            {
                String a0100[] = new String[6];
                a0100[0] = rowSet.getString("a0100");
                a0100[1] = rowSet.getString("a0101");
                a0100[2] = rowSet.getString("nbase");
                a0100[3] = getCardNO(rowSet.getString("a0100"), rowSet.getString("nbase"), dao);
                a0100[4] = rowSet.getString("e0122"); // 考勤表加入部门
                a0100[5] = rowSet.getString("e01a1"); // 考勤表加入职位
                list.add(a0100);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return list;
    }

    // 得到在考勤范围内的部门员工编号，并添加到list中;非全部人员库的时候
    public ArrayList getA0100Listusr(String code, String kind, ArrayList datelist, int curpage, int pagesize, ArrayList kq_dbase_list, ArrayList keylist, String dbt, int allNum) throws GeneralException
    {

        CommonData start_vo = (CommonData) datelist.get(0);
        String start_date = start_vo.getDataName();
        CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
        String end_date = end_vo.getDataName();
        ArrayList a0100whereIN = new ArrayList();

        if (!"self".equals(this.getSelf_flag()))
        {
            if ("all".equalsIgnoreCase(dbt))
            {
                for (int i = 0; i < kq_dbase_list.size(); i++)
                {
                    String dbase = kq_dbase_list.get(i).toString();
                    String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);
                    a0100whereIN.add(whereA0100In);
                }
            }
            else
            {
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbt);
                a0100whereIN.add(whereA0100In);
            }
        }

        String sort = "";
        String desc = "";
        StringBuffer coloum = new StringBuffer();
        SortBo bo = new SortBo(this.conn, this.userView);
        KqReportInit init = new KqReportInit(this.conn);
//        if (sortItem != null && sortItem.length() > 0 && !"not".equalsIgnoreCase(sortItem))
//        {
//            sort = bo.getSortSql(sortItem, "");
//            desc = bo.getSortSql(sortItem, "", false);
//            String[] strs = sortItem.split("`");
//            for (int i = 0; i < strs.length; i++)
//            {
//                String[] str = strs[i].split(":");
//                if (!",a0100,a0101,e0122,nbase,b0110,e01a1,dbid,a0000".contains("," + str[0].toLowerCase() + ","))
//                {
//                    coloum.append(str[0]);
//                    coloum.append(",");
//                }
//            }
//        }
//        else if (bo.isExistSort())
//        {
//            sort = bo.getSortSqlTable("");
//            desc = bo.getSortSqlTable("", false);
//
//            String[] strs = bo.querrySort().split(",");
//            for (int i = 0; i < strs.length; i++)
//            {
//                String[] str = strs[i].split(":");
//                if (!",a0100,a0101,e0122,nbase,b0110,e01a1,dbid,a0000".contains("," + str[0].toLowerCase() + ","))
//                {
//                    coloum.append(str[0]);
//                    coloum.append(",");
//                }
//            }
//        }
//        else
//        {
            sort = "order by dbid,a0000";
            desc = "order by dbid desc,a0000 desc";
//        }
        init.setColums(coloum.toString());

        // 前面 固定值
        String whereA0100 = "";
        if ("all".equalsIgnoreCase(dbt))
        {
            whereA0100 = init.select_kq_Distincta0100(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, kq_dbase_list, this.whereIN);
        }
        else
        {
            whereA0100 = init.select_kq_Distincta01002(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, dbt, this.whereIN);
        }
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList a0100list = new ArrayList();
        RowSet rowSet = null;

        if (sort != null && sort.length() > 0)
        {
            whereA0100 = "select * from (" + whereA0100.substring(0, whereA0100.lastIndexOf("order")) + ") bn " + sort;
        }
        try
        {

            /*
             * rowSet = dao.search(whereA0100); int num=1; int
             * start_record=(curpage-1)*pagesize+1; int
             * end_record=curpage*pagesize;
             * 
             * while(rowSet.next()) { if(num>=start_record&&num<=end_record) {
             * String a0100[] = new String[2];
             * a0100[0]=rowSet.getString("a0100");
             * a0100[1]=rowSet.getString("a0101"); a0100list.add(a0100); num++;
             * }else if(num<start_record) { num++; continue; }else
             * if(num>end_record) { num++; break; } }
             */
            // Sql中有 排序 还传入 list，会出现问题；所以可以不传入list;
            // rowSet=dao.search(whereA0100.toString(),pagesize,curpage,keylist);
            // rowSet=dao.search(whereA0100.toString(),pagesize,curpage);
            StringBuffer sql = new StringBuffer();
            if (Sql_switcher.searchDbServer() == 2)
            {
                sql.append("select * from (select b.*,rownum m from (");
                sql.append(whereA0100);
                sql.append(") b) c where m between ");
                sql.append(pagesize * (curpage - 1) + 1);
                sql.append(" and ");
                sql.append(pagesize * curpage);
            }
            else
            {
                int vp = 0;
                if ((pagesize * curpage) > allNum)
                {
                    vp = allNum - (pagesize * (curpage - 1));
                }
                else
                {
                    vp = pagesize;
                }
                sql.append("select * from (select top " + vp + " * from (select top ");
                sql.append(pagesize * curpage);
                sql.append("* from (");
                sql.append(whereA0100.substring(0, whereA0100.lastIndexOf("order")));
                sql.append(") qqq ");
                sql.append(sort);
                sql.append(") vvv ");
                sql.append(desc);
                sql.append(") mmm ");
                sql.append(sort);
            }
            rowSet = dao.search(sql.toString());
            while (rowSet.next())
            {
                String a0100[] = new String[6];
                a0100[0] = rowSet.getString("a0100");
                a0100[1] = rowSet.getString("a0101");
                a0100[2] = rowSet.getString("nbase");
                a0100[3] = getCardNO(rowSet.getString("a0100"), rowSet.getString("nbase"), dao);
                a0100[4] = rowSet.getString("e0122"); // 考勤表加入部门
                a0100[5] = rowSet.getString("e01a1"); // 考勤表加入职位
                a0100list.add(a0100);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        this.a0100sql = whereA0100.toString();

        a0100sql = a0100sql.replace("order by b0110,e0122,e01a1,a0100", "");
        a0100sql = a0100sql.replace("order by q03.b0110,q03.e0122,q03.e01a1,q03.a0100,q03.nbase", " ");
        a0100sql = a0100sql.replace("order by q03_arc.b0110,q03_arc.e0122,q03_arc.e01a1,q03_arc.a0100,q03_arc.nbase", " ");
        return a0100list;
    }

    /**
     * 通过一个员工编号得到该员工考勤期间的数据 姓名在这里加链接
     */
    public String getOneA0100Data(String code, String kind, String a0100[], ArrayList datelist, int body_hieght, ArrayList item_list) throws GeneralException
    {
        ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);
        StringBuffer column = new StringBuffer();
        ArrayList columnlist = new ArrayList();

        for (int i = 0; i < fielditemlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            /*
             * if("N".equals(fielditem.getItemtype())) {
             */
            if (!"i9999".equals(fielditem.getItemid()))
            {
                column.append("" + fielditem.getItemid() + ",");
                columnlist.add(fielditem.getItemid());
            }
        }
        // }
        CommonData start_vo = (CommonData) datelist.get(0);
        String start_date = start_vo.getDataName(); // 开始时间
        start_vo = (CommonData) datelist.get(datelist.size() - 1);
        String end_date = start_vo.getDataName(); // 结束时间
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer one_date = new StringBuffer();
        String sytle = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz());
        if (a0100[1] != null && a0100[1].length() == 2)
        {
            StringBuffer buff = new StringBuffer();
            buff.append(a0100[1].substring(0, 1));
            buff.append("　");
            buff.append(a0100[1].substring(1, 2));
            a0100[1] = buff.toString();
        }

        // 人员库
        // String
        // dbname=AdminCode.getCode("@@",a0100[2])!=null?AdminCode.getCode("@@",a0100[2]).getCodename():"";
        // one_date.append(executeTable(29,7,parsevo.getHead_fn(),sytle,body_hieght+"",dbname,getStyleName("5")));
        // 部门
        if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected)
        {
            String dd = AdminCode.getCode("UM", a0100[4]) != null ? AdminCode.getCode("UM", a0100[4], Integer.parseInt(uplevel)).getCodename() : "";
            one_date.append(executeTable(29, 7, parsevo.getHead_fn(), sytle, body_hieght + "", dd, getStyleName("5")));
        }
        // 职位
        if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected)
        {
            KqParameter para = new KqParameter();
            if (!"1".equals(para.getKq_orgView_post()))
            {
                String e01 = AdminCode.getCode("@K", a0100[5]) != null ? AdminCode.getCode("@K", a0100[5]).getCodename() : "";
                one_date.append(executeTable(29, 7, parsevo.getHead_fn(), sytle, body_hieght + "", e01, getStyleName("5")));
            }
        }
        // 姓名
        String str = a0100[1];
        String a01001 = PubFunc.encrypt(a0100[0]);
        String a01002 = PubFunc.encrypt(a0100[2]);
        String alink = "href='###' onclick='openwin(\"" + a01001 + "\",\"" + a01002 + "\",\"" + start_date + "\",\"" + end_date + "\");'" ;
        one_date.append(executeTable(30, 7, parsevo.getHead_fn(), sytle, body_hieght + "", str, getStyleName("5"), alink));
        // 为首钢增加 签到薄
        String strft = "<a href='###' onclick='openwintable(\"" + a01001 + "\",\"" + str + "\",\"" + a01002 + "\",\"" + start_date + "\",\"" + end_date + "\");'><img src=/images/view.gif border=0></a>";
        one_date.append(executeTable(1, 7, parsevo.getHead_fn(), sytle, body_hieght + "", strft, getStyleName("5")));
        // 工号
        if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected)
        {
            if (this.cardno != null && this.cardno.length() > 0) {
                one_date.append(executeTable(29, 7, parsevo.getHead_fn(), sytle, body_hieght + "", a0100[3], getStyleName("5")));
            } else {
                one_date.append(executeTable(29, 7, parsevo.getHead_fn(), sytle, body_hieght + "", a0100[0], getStyleName("5")));
            }
        }
        RowSet rowSet = null;
        try
        {
            String sql_one_a0100 = KqReportInit.select_kq_one_emp(this.getCurTab(), a0100[2], a0100[0], start_date, end_date, code, kind, column.toString());
            rowSet = dao.search(sql_one_a0100);
            HashMap kq_item_map = new HashMap();
            HashMap kq_item_all = querryKq_item();
            while (rowSet.next())
            {
                int existOther = 0;
                String q03z0 = rowSet.getString("q03z0").trim();
                ArrayList list = new ArrayList();
                ArrayList z1_list = new ArrayList();
                ArrayList scq_list = new ArrayList();
                for (int i = 0; i < fielditemlist.size(); i++)
                {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    if ("N".equals(fielditem.getItemtype()) && !"i9999".equals(fielditem.getItemid()))
                    {
                        if ("q03z1".equalsIgnoreCase(fielditem.getItemid()) || ("实出勤".equals(fielditem.getItemdesc()) || fielditem.getItemdesc().indexOf("出勤率") != -1))
                        {
                            if ("q03z1".equalsIgnoreCase(fielditem.getItemid()))
                            {
                                double dv = rowSet.getDouble(fielditem.getItemid());
                                if (dv != 0)
                                {
                                    String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                                    if (kq_item[0] == null) {
                                        continue;
                                    }
                                    
                                    z1_list.add(kq_item);
                                    z1_list.add(new Double(dv));
                                }
                            }
                            else
                            {
                                double dv = rowSet.getDouble(fielditem.getItemid());
                                if (dv != 0)
                                {
                                    String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                                    if (kq_item[0] == null) {
                                        continue;
                                    }
                                    
                                    scq_list.add(kq_item);
                                    scq_list.add(new Double(dv));
                                }
                            }
                            // continue;
                        }
                        else
                        {
                            String value = rowSet.getString(fielditem.getItemid());
                            if (value == null || value.length() <= 0)
                            {
                                value = "0";
                            }
                            double dv = Double.parseDouble(value);
                            if (dv != 0)
                            {
                                String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                            if (kq_item[0] == null) {
                                continue;
                            }
                            
                                ArrayList one_list = new ArrayList();
                                one_list.add(kq_item);
                                one_list.add(new Double(dv));
                                list.add(one_list);
                                if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase()))
                                {
                                    existOther++;
                                }
                                // kq_item_map.put(q03z0,list);
                                continue;
                            }
                            else
                            {
                                continue;
                            }
                        }
                    }
                    else if (!"q03z0".equals(fielditem.getItemid()) && !"nbase".equals(fielditem.getItemid()) && !"a0100".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid()) && !"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"a0101".equals(fielditem.getItemid()) && !"i9999".equals(fielditem.getItemid()))
                    {
                    	String sr ="";
                    	 if ("D".equals(fielditem.getItemtype())) {
                    		   Timestamp tt = rowSet.getTimestamp(fielditem.getItemid());
                    		   if (tt != null ) {
                                   sr=tt.toString();
                               }
                    	 }else {
                    		 sr = rowSet.getString(fielditem.getItemid());
						}
                        if (sr != null && sr.length() > 0 && !"0".equals(sr))
                        {
                            String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                            if (kq_item[0] == null) {
                                continue;
                            }
                            
                            ArrayList one_list = new ArrayList();
                            one_list.add(kq_item);
                            one_list.add(new Double(1));
                            list.add(one_list);
                        }
                    }
                }
                
                 if(list!=null && list.size()<=0)
                 {
                     if(scq_list==null||scq_list.size()<=0)
                     {
                        list.add(z1_list);
                     }
                 }

                if (existOther == 0)
                {
                    if (scq_list != null && scq_list.size() > 0)
                    {
                        list.add(scq_list);
                    }
                }

                // System.out.println(list);
                kq_item_map.put(q03z0, list);
            }
            if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected)
            {
                for (int s = 0; s < datelist.size(); s++)
                {
                    CommonData cur_vo = (CommonData) datelist.get(s);
                    String cur_date = cur_vo.getDataName().trim();
                    ArrayList kq_item_list = (ArrayList) kq_item_map.get(cur_date); // 符号
                    if (kq_item_list != null && kq_item_list.size() > 0)
                    {
                        StringBuffer font_str = new StringBuffer();
                        for (int t = 0; t < kq_item_list.size(); t++)
                        {
                            ArrayList one_list = (ArrayList) kq_item_list.get(t);
                            if (one_list != null && one_list.size() > 0)
                            {
                                String[] kq_item = (String[]) one_list.get(0);
                                Double dv = (Double) one_list.get(1);
                                double value = dv.doubleValue();
                                if (value != 0)
                                {
                                    String sytle1 = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz(), kq_item[1]);
                                    // one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"",kq_item[0],getStyleName("5")));
                                    font_str.append(executeFont(parsevo.getHead_fn(), sytle1, kq_item[0]));
                                }

                                /*
                                 * else { String
                                 * sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),kq_item[1]);
                                 * one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); }
                                 */
                            }/*
                                 * else { String
                                 * sytle1=getFontStyle(parsevo.getBody_fn(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fb(),parsevo.getBody_fz(),"");
                                 * one_date.append(executeTable(1,7,parsevo.getHead_fn(),sytle1,body_hieght+"","&nbsp;&nbsp;",getStyleName("5"))); }
                                 */
                        }
                        // if(a0100[0].equals("00000147")&&a0100[2].equals("Usr"))
                        one_date.append(executeTable(1, 7, parsevo.getHead_fn(), "", body_hieght + "", font_str.toString(), getStyleName("5")));
                    }
                    else
                    {
                        String sytle1 = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz(), "");
                        one_date.append(executeTable(1, 7, parsevo.getHead_fn(), sytle1, body_hieght + "", "&nbsp;&nbsp;", getStyleName("5")));
                    }
                }
            }
        }
        catch (Exception e)
        {
            // throw GeneralExceptionHandler.Handle(e);
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return one_date.toString();
    }

    /**
     * 首钢 增加 本月出缺勤情况统计小计
     * 
     * @param code
     * @param kind
     * @param a0100
     * @param kq_duration
     *            考勤时间
     * @return
     */
    public String getOneA0100Value(String code, String kind, String a0100[], String kq_duration, int body_hieght)
    {
        ArrayList kqq03list = getKqBookItemList();
        StringBuffer column = new StringBuffer();
        ArrayList columnlist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        for (int i = 0; i < kqq03list.size(); i++)
        {
            FieldItem fielditem = (FieldItem) kqq03list.get(i);
            if (!"i9999".equals(fielditem.getItemid()))
            {
                column.append("" + fielditem.getItemid() + ",");
                columnlist.add(fielditem.getItemid());
            }
        }
        StringBuffer one_date = new StringBuffer();
        RowSet rowSet = null;
        String itemid = "";
        Date itemDate = null;
        try
        {
            // int l=column.toString().length()-1;
            // String columnstr=column.toString().substring(0,l);
            for (int t = 0; t < kqq03list.size(); t++)
            {
                FieldItem fielditem = (FieldItem) kqq03list.get(t);
                if ("i9999".equals(fielditem.getItemid()))
                {
                    continue;
                }
                StringBuffer sql_on_a0100 = new StringBuffer();
                String itd = fielditem.getItemid();
                String itemType = fielditem.getItemtype();
                sql_on_a0100.append("select " + itd + " as one");
                if(!this.getCurTab().toLowerCase().contains("_arc")) {
                    sql_on_a0100.append(" from Q05");
                } else {
                    sql_on_a0100.append(" from Q05_arc");
                }
                sql_on_a0100.append(" where Q03Z0='" + kq_duration + "' and ");
                sql_on_a0100.append("nbase='" + a0100[2] + "' and A0100='" + a0100[0] + "'");
                rowSet = dao.search(sql_on_a0100.toString());

                StringBuffer font_str = new StringBuffer();
                if (rowSet.next())
                {
                	// 36919 增加日期型字段校验
                	if("D".equalsIgnoreCase(itemType)) {
                		
                		itemDate = rowSet.getTimestamp("one");
                	}else {
                		
                		itemid = rowSet.getString("one");
                	}
                    if ((null!=itemid && !"D".equalsIgnoreCase(itemType)) || (null!=itemDate && "D".equalsIgnoreCase(itemType)))
                    {
                        if (("".equals(itemid) || "0E-8".equalsIgnoreCase(itemid) || "0".equalsIgnoreCase(itemid)) && !"D".equalsIgnoreCase(itemType)) {
                            itemid = "";
                        } else{
                            if ("N".equalsIgnoreCase(itemType)){
                                int num = fielditem.getDecimalwidth();
                                if (itemid.indexOf(".") != -1){
                                    for (int k = 0; k < num; k++){
                                        itemid += "0";
                                    }
                                    itemid = PubFunc.round(itemid, num);
                                }else{
                                    for (int k = 0; k < num; k++){
                                        if (k == 0) {
                                            itemid += "." + "0";
                                        } else {
                                            itemid += "0";
                                        }
                                    }
                                    itemid = PubFunc.round(itemid, num);
                                }
                            }else if ("A".equalsIgnoreCase(itemType)){
                                String setid = fielditem.getCodesetid();
                                if (!"0".equalsIgnoreCase(setid)){
                                    if (!codemap.containsKey(setid + itemid)){
                                        String codesetid = itemid;
                                        itemid = AdminCode.getCodeName(setid, itemid);
                                        codemap.put(setid + codesetid, itemid);
                                    }else {
                                        itemid = (String) codemap.get(setid + itemid);
                                    }
                                }
                            }
                            // 36919 增加日期型字段校验
                            else if("D".equalsIgnoreCase(itemType)) {
                            	// 取日期指标相应的长度格式
                            	String formatstr = this.getFormatStr(fielditem.getItemlength());
                            	if(StringUtils.isNotEmpty(formatstr)) {
                                    itemid = DateUtils.format(itemDate, formatstr);
                                }
                            }
                        }
                    }else{
                        itemid = "";
                    }
                    String sytle1 = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz(), "#0000FF");
                    font_str.append(executeFont(parsevo.getHead_fn(), sytle1, itemid));
                    one_date.append(executeTable(1, 7, parsevo.getHead_fn(), "", body_hieght + "", font_str.toString(), getStyleName("5")));
                }
                else
                {
                    String sytle1 = getFontStyle(parsevo.getBody_fn(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fb(), parsevo.getBody_fz(), "#0000FF");
                    font_str.append(executeFont(parsevo.getHead_fn(), sytle1, itemid));
                    one_date.append(executeTable(1, 7, parsevo.getHead_fn(), "", body_hieght + "", font_str.toString(), getStyleName("5")));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return one_date.toString();
    }

    /**
     * 根据长度取相应的日期格式
     * @param len
     * @return
     */
    private String getFormatStr (int len) {
    	String formatstr = "";
    	if(4 == len) {
            formatstr = "yyyy";
        } else if(7 == len) {
            formatstr = "yyyy.MM";
        } else if(10 == len) {
            formatstr = "yyyy.MM.dd";
        } else if(16 == len) {
            formatstr = "yyyy.MM.dd HH:mm";
        }
    	
    	return formatstr;
    }
    /**
     * 判断页码
     * 
     */
    public int getCurpage(int curpage, int pagesize, int sum_page)
    {
        if (curpage <= 0)
        {
            curpage = 1;
        }
        else if (curpage > sum_page)
        {
            curpage = sum_page;
        }
        return curpage;
    }

    /**
     * 总纪录数
     */
    // 得到在考勤范围内的部门员工编号，并添加到list中
    public int getAllRecordNum(String code, String kind, ArrayList datelist, ArrayList kq_dbase_list) throws GeneralException
    {
        CommonData start_vo = (CommonData) datelist.get(0);
        String start_date = start_vo.getDataName();
        CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
        String end_date = end_vo.getDataName();

        ArrayList a0100whereIN = new ArrayList();
        if (!"self".equals(this.getSelf_flag()))
        {
            for (int i = 0; i < kq_dbase_list.size(); i++)
            {
                String dbase = kq_dbase_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);
                if((whereA0100In.toUpperCase()).indexOf("WHERE") != -1){
                	whereA0100In += " AND nbase='" + dbase + "'";
                }else{
                	whereA0100In += " WHERE  nbase='" + dbase + "'";
                }
                a0100whereIN.add(whereA0100In);
            }
        }
        String whereA0100 = KqReportInit.count_kq_a0100(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, this.whereIN, "");
        ContentDAO dao = new ContentDAO(this.conn);
        String count = "";
        int allrow = 0;
        RowSet rowSet = null;
        try
        {

            rowSet = dao.search(whereA0100);
            while (rowSet.next())
            {
                count = rowSet.getString("a");
                allrow = allrow + Integer.parseInt(count);
            }
        }
        catch (Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return allrow;
    }

    // 得到在考勤范围内的部门员工编号，并添加到list中
    public int getAllRecordNum2(String code, String kind, ArrayList datelist, String dbtype) throws GeneralException
    {
        CommonData start_vo = (CommonData) datelist.get(0);
        String start_date = start_vo.getDataName();
        CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
        String end_date = end_vo.getDataName();

        ArrayList a0100whereIN = new ArrayList();
        if (!"self".equals(this.getSelf_flag()))
        {
            // String dbase=kq_dbase_list.get(i).toString();
            String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbtype);
            a0100whereIN.add(whereA0100In);

        }
        String whereA0100 = KqReportInit.count_kq_a0100(this.getCurTab(), code, kind, start_date, end_date, a0100whereIN, this.whereIN, dbtype);
        ContentDAO dao = new ContentDAO(this.conn);
        String count = "";
        int allrow = 0;
        RowSet rowSet = null;
        try
        {

            rowSet = dao.search(whereA0100);
            while (rowSet.next())
            {
                count = rowSet.getString("a");
                allrow = allrow + Integer.parseInt(count);
            }
        }
        catch (Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }

        return allrow;
    }

    /***************************************************************************
     * 以用高度
     **************************************************************************/
    public double getIsUseHieght(ArrayList item_list) throws GeneralException
    {
        String top    = getPxFromMm(parsevo.getTop());
        String bottom = getPxFromMm(parsevo.getBottom());
        String titleH = getPxFromMm(parsevo.getTitle_h());
        String headH  = getPxFromMm(parsevo.getHead_h());
        
        double height = Double.parseDouble(top) 
                      + Double.parseDouble(bottom) 
                      + Double.parseDouble(titleH)
                      + Double.parseDouble(headH);
        height = height + Double.parseDouble(parsevo.getBody_fz()) + 13;

        ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);
        // 计算备注1
        StringBuffer note_len_str = new StringBuffer();
        note_len_str.append("备注：1.");
        for (int i = 0; i < fielditemlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            if ("N".equals(fielditem.getItemtype()))
            {
                if (!"i9999".equals(fielditem.getItemid()))
                {
                    String kq_item[] = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                    if (kq_item[0] != null && kq_item[0].length() > 0)
                    {
                        note_len_str.append(fielditem.getItemdesc() + "(" + kq_item[0] + ")");
                    }
                }
            }
        }
        int strlen = note_len_str.toString().length() * Integer.parseInt(parsevo.getBody_fz());
        int numrow_tile_1 = getNumRow(strlen);
        if (numrow_tile_1 != 0)
        {
            height = height + (Double.parseDouble(parsevo.getBody_fz()) + 6) * numrow_tile_1;
        }
        else
        {
            height = height + (Double.parseDouble(parsevo.getBody_fz()) + 6);
        }

        // 计算表尾客户添加的文本内容
        if (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0)
        {
            /** ***#代表一个空格*** */
            String tile_fw = "#####2." + parsevo.getTile_fw();
            int str_tile_2 = tile_fw.length() * Integer.parseInt(parsevo.getBody_fz());

            int note_tile_2 = getNumRow(str_tile_2);

            if (note_tile_2 != 0)
            {
                height = height + (Double.parseDouble(parsevo.getBody_fz()) + 6) * note_tile_2;
            }
        }

        if ("#c".equals(parsevo.getHead_c()) || "#p".equals(parsevo.getHead_p()) || "#e".equals(parsevo.getHead_e()) || "#u".equals(parsevo.getHead_u()) || "#d".equals(parsevo.getHead_d()) || "#t".equals(parsevo.getHead_t()))
        {
            height = height + Double.parseDouble(getPxFromMm(parsevo.getHead_h()));
        }

        if ("#c".equals(parsevo.getTile_c()) || "#p".equals(parsevo.getTile_p()) || "#e".equals(parsevo.getTile_e()) || "#u".equals(parsevo.getTile_u()) || "#d".equals(parsevo.getTile_d()) || "#t".equals(parsevo.getTile_t()))
        {
            height = height + Double.parseDouble(getPxFromMm(parsevo.getTile_h()));
        }

        return height;
    }

    /***************************************************************************
     * 剩余高度
     */

    public double getSpareHieght(ArrayList item_list) throws GeneralException
    {
        double spare_hieght = 0;
        //单位px
        double height = getIsUseHieght(item_list);
        //单位与参数一致
        double factHeight = Double.parseDouble(getFactHeight());
        
        String unit = parsevo.getUnit().trim();
        if ("px".equalsIgnoreCase(unit))
        {
            spare_hieght = factHeight - height;
        }
        else
        {
            spare_hieght = factHeight / 0.26 - height; 
        }
        
        if(spare_hieght<0) {
            spare_hieght = 0;
        }
        
        return spare_hieght;
    }

    /**
     * 转换，毫米转换为像素
     */
    public String getPxFromMm(String value)
    {
        String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素
        if ("mm".equals(unit))
        {
            double dv = Double.parseDouble(value) / 0.26;
            return KqReportInit.round(dv + "", 0);
        }
        else
        {
            return KqReportInit.round(value, 0);
        }
    }

    /**
     * 计算表格实际总宽度 首钢更改，注销是原始的
     */
    public String getFactWidth()
    {
        String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素

        if ("1".equals(parsevo.getOrientation().trim()))
        {
            if ("px".equals(unit))
            {
                double width;
                if (this.kqtablejudge)
                {
                    width = Double.parseDouble(parsevo.getHeight()) / 0.14 - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                else
                {
                    width = Double.parseDouble(parsevo.getHeight()) / 0.26 - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                return KqReportInit.round(width + "", 0);
            }
            else
            {
                double width;
                if (this.kqtablejudge)
                {
                    width = Double.parseDouble(parsevo.getHeight()) - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                else
                {
                    width = Double.parseDouble(parsevo.getHeight()) - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                return KqReportInit.round(width + "", 0);
            }
        }
        else
        {
            if ("px".equals(unit))
            {
                double width;
                if (this.kqtablejudge)
                {
                    width = Double.parseDouble(parsevo.getWidth()) / 0.14 - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                else
                {
                    width = Double.parseDouble(parsevo.getWidth()) / 0.26 - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                return KqReportInit.round(width + "", 0);
            }
            else
            {
                double width;
                if (this.kqtablejudge)
                {
                    width = Double.parseDouble(parsevo.getWidth()) - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                else
                {
                    width = Double.parseDouble(parsevo.getWidth()) - Double.parseDouble(getPxFromMm(parsevo.getLeft())) - Double.parseDouble(getPxFromMm(parsevo.getRight()));
                }
                return KqReportInit.round(width + "", 0);
            }
        }
    }

    /**
     * 计算表格实际总高度
     */
    public String getFactHeight()
    {
        double height = 0;
        String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素
        
        if ("1".equals(parsevo.getOrientation().trim()))
        {
            height = Double.parseDouble(parsevo.getWidth());
        }
        else
        {
            height = Double.parseDouble(parsevo.getHeight());
        }
        
        //纸张的宽和高的单位是mm
        if("px".equalsIgnoreCase(unit))
        {
            height = height / 0.26;
        }
        
        height = height 
               - Double.parseDouble(parsevo.getTop()) 
               - Double.parseDouble(parsevo.getBottom()); 
        
        return KqReportInit.round(height + "", 0);
    }

    /**
     * 产生翻页代码 和 返回按钮
     * 
     */
    private String getTurnPageCode(int curPage, String turnpage_sytle, int sum_page)
    {
        StringBuffer code = new StringBuffer("");
        code.append("<table width='50%' height='30' align='center' style='" + turnpage_sytle + "'>");
        code.append("<tr><td align='center'> \n");

        code.append("<select name='curpage' size='1' onchange='javascript:change()'>");
        for (int i = 1; i <= sum_page; i++)
        {
            if (i == curPage)
            {
                code.append("<option value='" + i + "' selected='selected'>第" + i + "页</option>");
            }
            else
            {
                code.append("<option value='" + i + "'>第" + i + "页</option>");
            }
        }

        code.append("</select>");
        // code.append("&nbsp;&nbsp;<hrms:priv func_id='27020131,0C31031'><input
        // type='button' value='生成PDF' onclick='excecutePDF()'
        // class='mybutton'></hrms:priv>");
        if (this.userView.hasTheFunction("27020131") || this.userView.hasTheFunction("0C31031"))
        {
            code.append("&nbsp;&nbsp;<input type='button' value='生成PDF' onclick='excecutePDF(\"" + this.sortItem + "\")' class='mybutton'>");
        }
        code.append("&nbsp;&nbsp;<input type='button' value='生成Excel' onclick='exportExcel(\"" + this.sortItem + "\")' class='mybutton'>");
        if ("coll".equals(this.getSelf_flag()))
        {
            if (this.userView.hasTheFunction("27020132") || this.userView.hasTheFunction("0C31032"))
            {
                code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
            }

            code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(2);' class='mybutton'>");
        }
        else if ("select".equals(this.getSelf_flag()))
        {
            code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(4);' class='mybutton'>");
        }
        else if ("hist".equals(this.getSelf_flag()))
        {
            code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(5);' class='mybutton'>");
        }
        else if ("back".equals(this.getSelf_flag()))
        {
            if (this.userView.hasTheFunction("270202801"))
            {
                code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
            }

            code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(7);' class='mybutton'>");
        }
        else if (!"self".equals(this.getSelf_flag()))
        {
            if (this.userView.hasTheFunction("27020132") || this.userView.hasTheFunction("0C31032"))
            {// 部门0C31032
                code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
            }
            code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back(1);' class='mybutton'>");
        }
        code.append("</td></tr></table>");
        return code.toString();
    }

    /**
     * 计算在规定的字数中，一串字符，有多少行
     */
    public int getNumRow(int strlen)
    {
        int factwidth = Integer.parseInt(getFactWidth());

        int ss = strlen / factwidth;
        int dd = strlen % factwidth;
        if (dd != 0)
        {
            ss = ss + 1;
        }
        return ss;
    }

    /***************************************************************************
     * 通过考勤期间得到考勤日期
     * 
     * @param coursedate
     *            考勤期间
     * 
     * @return datelist 日期 只有日期
     */
    public ArrayList getDateList(Connection conn, String coursedate) throws GeneralException
    {
        ArrayList datelist = new ArrayList();
        RowSet rowSet = null;
        String[] date = coursedate.split("-");
        String kq_year = date[0];
        String kq_duration = date[1];
        String kq_start;
        String kq_dd;
        String sql = "SELECT kq_start,kq_end FROM kq_duration where kq_year='" + kq_year + "'and kq_duration='" + kq_duration + "'";
        ContentDAO dao = new ContentDAO(conn);
        try
        {
            rowSet = dao.search(sql.toString());
            if (rowSet.next())
            {
                Date d1 = rowSet.getDate("kq_start");
                Date d2 = rowSet.getDate("kq_end");

                int spacedate = DateUtils.dayDiff(d1, d2);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
                SimpleDateFormat format2 = new SimpleDateFormat("dd");
                for (int i = 0; i <= spacedate; i++)
                {
                    CommonData vo = new CommonData();
                    kq_start = format1.format(d1);
                    kq_dd = format2.format(d1);
                    vo.setDataName(kq_start);
                    vo.setDataValue(kq_dd);
                    datelist.add(vo);
                    d1 = DateUtils.addDays(d1, 1);
                }
            }
            else
            {
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.register.session.nosave"), "", ""));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.register.session.nosave"), "", ""));
        }
        finally
        {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return datelist;
    }

    public String getWhereIN()
    {
        return whereIN;
    }

    public void setWhereIN(String whereIN)
    {
        this.whereIN = whereIN;
    }

    public String getCardNO(String a0100, String nbase, ContentDAO dao) throws GeneralException
    {
        if (this.cardno == null || this.cardno.length() <= 0) {
            return a0100;
        }
        if (a0100 == null || a0100.length() <= 0) {
            return a0100;
        }
        if (nbase == null || nbase.length() <= 0) {
            return a0100;
        }
        String sql = "select " + this.cardno + " as cardno from " + nbase + "A01 where a0100='" + a0100 + "'";
        String card = "";
        RowSet rr = null;
        try
        {
            rr = dao.search(sql);
            if (rr.next()) {
                card = rr.getString("cardno");
            }
        }
        catch (Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rr);
        }
        return card;
    }

    /*
     * 考勤表 展现的值；
     */
    public ArrayList getKqBookItemList()
    {
        ArrayList list = new ArrayList();
        
        String content = KqParam.getInstance().getKqBookItems();
        String[] con = content.split(",");
        
        ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
                Constant.USED_FIELD_SET);
        
        for (int j = 0; j < fielditemlist.size(); j++)
        {
            FieldItem fielditem = (FieldItem) fielditemlist.get(j);
            for (int i = 0; i < con.length; i++)
            {
                if (con[i].equalsIgnoreCase(fielditem.getItemid()))
                {
                    list.add(fielditem.clone());
                }
            }
        }
        return list;
    }

    /**
     * 考勤表：true=展现本月出缺勤情况统计小计 false=不展现
     * 
     * @param conn
     * @return
     */
    public boolean getkqtablejudge(Connection conn)
    {
        boolean kqjudge = false;
        
        String content = KqParam.getInstance().getKqBookItems();
        
        String[] con;
        if (!"".equals(content) || content.length() > 0)
        {
            con = content.split(",");
            if (con.length > 0)
            {
                kqjudge = true;
            }
        }
        
        return kqjudge;
    }

    /**
     * 查询考勤期间的所有指标
     * 
     * @return
     */
    private HashMap querryKq_item()
    {
        HashMap map = new HashMap();
        String sql = "select item_symbol,fielditemid from kq_item";
        ContentDAO dao = new ContentDAO(this.conn);
        ResultSet rs = null;
        try
        {
            rs = dao.search(sql);
            while (rs.next())
            {
                String key = rs.getString("fielditemid");
                String value = rs.getString("item_symbol");
                if (key != null)
                {
                    map.put(key.toLowerCase(), value);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return map;
    }

    public String getA0100sql()
    {
        return a0100sql;
    }

    public void setA0100sql(String a0100sql)
    {
        this.a0100sql = a0100sql;
    }

    public String getDbtype()
    {
        return dbtype;
    }

    public void setDbtype(String dbtype)
    {
        this.dbtype = dbtype;
    }

    public String getSortItemDesc()
    {
        return sortItemDesc;
    }

    public void setSortItemDesc(String sortItemDesc)
    {
        this.sortItemDesc = sortItemDesc;
    }

    public String getCurTab() {
        return curTab;
    }

    public void setCurTab(String curTab) {
        this.curTab = curTab;
    }
}
