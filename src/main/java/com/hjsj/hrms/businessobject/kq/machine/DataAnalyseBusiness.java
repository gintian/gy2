package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据处理业务处理
 * <p>
 * Title:DataAnalyseBusiness.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Oct 25, 2007
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 */
public class DataAnalyseBusiness
{

    private Connection conn;
    private UserView userView;
    private boolean isPost = false;

    public DataAnalyseBusiness(Connection conn, UserView userView)
    {
        this.conn = conn;
        this.userView = userView;
    }

    public DataAnalyseBusiness(Connection conn, UserView userView, String viewPost)
    {
        this.conn = conn;
        this.userView = userView;
        KqParameter para = new KqParameter();
        this.isPost = "kq".equalsIgnoreCase(viewPost) && "1".equalsIgnoreCase(para.getKq_orgView_post()) ? true : false;
    }

    private ArrayList fieldList = new ArrayList();
    private String column;
    private int lockedNum;

    public int getLockedNum()
    {
        return lockedNum;
    }

    public void setLockedNum(int lockedNum)
    {
        this.lockedNum = lockedNum;
    }

    /**
     * 处理结果字段信息
     * 
     * @param fieldlist
     */
    public void analyseResultBusi(ArrayList fieldlist, String table)
    {
        StringBuffer columns = new StringBuffer();
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("结果");
        fielditem_c.setItemid("isok");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        // 数据处理是不应该 展现上岛标识
        lockedNum = 0;
        boolean isC = true;
        String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); // 得到上岛标识
                                                                                        // 对应的字段
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if ("A".equals(fielditem.getItemtype()) || "N".equals(fielditem.getItemtype()))
            {
                if ("state".equals(fielditem.getItemid()) || "i9999".equals(fielditem.getItemid()) || "q03z2".equals(fielditem.getItemid()) || "ctime".equals(fielditem.getItemid()) || "isok".equals(fielditem.getItemid())) {
                    continue;
                }
                // 数据处了部分有问题：生成临时表字段要根据考勤规则展现这里没有；等年后改
                if (!"".equals(sdao_count_field) || sdao_count_field.length() > 0)
                {
                    if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()) && !sdao_count_field.equalsIgnoreCase(fielditem.getItemid()) && !"C010K".equalsIgnoreCase(fielditem.getItemid()))
                    {
                        if ("a0100".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(false);
                        }
                        if ("q03z0".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(true);
                        }
                        else
                        {
                            if ("1".equals(fielditem.getState()))
                            {
                                fielditem.setVisible(true);
                            }
                            else
                            {
                                fielditem.setVisible(false);
                            }
                        }
                    }
                    else
                    {
                        fielditem.setVisible(false);
                    }
                }
                else
                {
                    if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()))
                    {
                        if ("a0100".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(false);
                        }
                        if ("q03z0".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(true);
                        }
                        else
                        {
                            if ("1".equals(fielditem.getState()))
                            {
                                fielditem.setVisible(true);
                            }
                            else
                            {
                                fielditem.setVisible(false);
                            }
                        }
                    }
                    else
                    {
                        fielditem.setVisible(false);
                    }
                }
                list.add(fielditem.clone());
                columns.append(fielditem.getItemid() + ",");
                // 加入班组指标
                if ("E01A1".equalsIgnoreCase(fielditem.getItemid()))
                {
                    fielditem_c = new FieldItem();
                    fielditem_c.setItemdesc("班次");
                    fielditem_c.setItemid("name");
                    fielditem_c.setItemtype("A");
                    fielditem_c.setCodesetid("0");
                    fielditem_c.setVisible(true);
                    list.add(fielditem_c);
                    columns.append("name,");
                }
                if ("a0101".equals(fielditem.getItemid()))
                {
                    if (getG_no(table))
                    {
                        fielditem_c = new FieldItem();
                        fielditem_c.setItemdesc("工号");
                        fielditem_c.setItemid("g_no");
                        fielditem_c.setItemtype("A");
                        fielditem_c.setCodesetid("0");
                        fielditem_c.setVisible(true);
                        list.add(fielditem_c);
                        columns.append("g_no,");
                    }

                }
            }
        }
        for (int i = 0; i < list.size(); i++)
        {
            FieldItem fielditem = (FieldItem) list.get(i);
            if (fielditem.isVisible())
            {
                if (isC && "A".equalsIgnoreCase(fielditem.getItemtype()))
                {
                    lockedNum++;
                    if ("a0101".equalsIgnoreCase(fielditem.getItemid()))
                    {
                        isC = false;
                    }
                }
                else
                {
                    isC = false;
                }
            }
        }
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班一");
        fielditem_c.setItemid("onduty_1");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班一");
        fielditem_c.setItemid("offduty_1");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班二");
        fielditem_c.setItemid("onduty_2");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班二");
        fielditem_c.setItemid("offduty_2");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班三");
        fielditem_c.setItemid("onduty_3");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班三");
        fielditem_c.setItemid("offduty_3");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班上");
        fielditem_c.setItemid("onduty_4");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班下");
        fielditem_c.setItemid("offduty_4");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("刷卡时间");
        fielditem_c.setItemid("card_time");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("有效状态");
        fielditem_c.setItemid("flag");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        /*
         * fielditem_c=new FieldItem(); fielditem_c.setItemdesc("结果");
         * fielditem_c.setItemid("isok"); fielditem_c.setItemtype("A");
         * fielditem_c.setCodesetid("0"); fielditem_c.setVisible(true);
         * list.add(fielditem_c);
         */
        columns.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4,card_time,flag,isok");
        this.fieldList = list;
        this.column = columns.toString();
    }

    /**
     * 处理结果字段信息
     * 
     * @param fieldlist
     */
    public void analyseResultBusi1(ArrayList fieldlist, String table)
    {
        StringBuffer columns = new StringBuffer();
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("结果");
        fielditem_c.setItemid("isok");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("刷卡时间");
        fielditem_c.setItemid("card_time");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        // 数据处理是不应该 展现上岛标识
        lockedNum = 0;
        boolean isC = true;
        String sdao_count_field = SystemConfig.getPropertyValue("sdao_count_field"); // 得到上岛标识
                                                                                        // 对应的字段
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        HashMap hashmap = para.getKqParamterMap();
        String g_no = ((String) hashmap.get("g_no")).toLowerCase();
        String cardno = ((String) hashmap.get("cardno")).toLowerCase();
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        
        DbWizard dbWizard = new DbWizard(this.conn);
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            
            if(!dbWizard.isExistField(table, fielditem.getItemid(), false)){
                continue;
            }
            
            if ("A".equals(fielditem.getItemtype()) || "N".equals(fielditem.getItemtype()))
            {
                if ("state".equals(fielditem.getItemid()) || "i9999".equals(fielditem.getItemid()) || "q03z2".equals(fielditem.getItemid()) || "ctime".equals(fielditem.getItemid()) || "isok".equals(fielditem.getItemid())) {
                    continue;
                }
                // 数据处了部分有问题：生成临时表字段要根据考勤规则展现这里没有；等年后改
                if (!"".equals(sdao_count_field) || sdao_count_field.length() > 0)
                {
                    if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()) && !sdao_count_field.equalsIgnoreCase(fielditem.getItemid()) && !"C010K".equalsIgnoreCase(fielditem.getItemid()))
                    {
                        if ("a0100".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(false);
                        }
                        if ("q03z0".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(true);
                        }
                        else
                        {
                            if ("1".equals(fielditem.getState()))
                            {
                                fielditem.setVisible(true);
                            }
                            else
                            {
                                fielditem.setVisible(false);
                            }
                        }
                    }
                    else
                    {
                        fielditem.setVisible(false);
                    }
                }
                else
                {
                    if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()))
                    {
                        if ("a0100".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(false);
                        }
                        if ("q03z0".equals(fielditem.getItemid()))
                        {
                            fielditem.setVisible(true);
                        }
                        else
                        {
                            if ("1".equals(fielditem.getState()))
                            {
                                fielditem.setVisible(true);
                            }
                            else
                            {
                                fielditem.setVisible(false);
                            }
                        }
                    }
                    else
                    {
                        fielditem.setVisible(false);
                    }
                }
                if (!fielditem.getItemid().equalsIgnoreCase(g_no) && !fielditem.getItemid().equalsIgnoreCase(cardno))
                {
                    list.add(fielditem.clone());
                    columns.append(fielditem.getItemid() + ",");
                }
                // 加入班组指标
                if ("E01A1".equalsIgnoreCase(fielditem.getItemid()))
                {
                    fielditem_c = new FieldItem();
                    fielditem_c.setItemdesc("班次");
                    fielditem_c.setItemid("name");
                    fielditem_c.setItemtype("A");
                    fielditem_c.setCodesetid("0");
                    fielditem_c.setVisible(true);
                    list.add(fielditem_c);
                    columns.append("name,");
                }
                if ("a0101".equals(fielditem.getItemid()))
                {
                    if (getG_no(table))
                    {
                        fielditem_c = new FieldItem();
                        fielditem_c.setItemdesc("工号");
                        fielditem_c.setItemid("g_no");
                        fielditem_c.setItemtype("A");
                        fielditem_c.setCodesetid("0");
                        fielditem_c.setVisible(true);
                        list.add(fielditem_c);
                        columns.append("g_no,");
                    }
                    if (getCard_no(table))
                    {
                        fielditem_c = new FieldItem();
                        fielditem_c.setItemdesc("考勤卡号");
                        fielditem_c.setItemid("card_no");
                        fielditem_c.setItemtype("A");
                        fielditem_c.setCodesetid("0");
                        fielditem_c.setVisible(true);
                        list.add(fielditem_c);
                        columns.append("card_no,");
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++)
        {
            FieldItem fielditem = (FieldItem) list.get(i);
            if (fielditem.isVisible())
            {
                if (isC && "A".equalsIgnoreCase(fielditem.getItemtype()))
                {
                    lockedNum++;
                    if ("name".equalsIgnoreCase(fielditem.getItemid()))
                    {
                        isC = false;
                    }
                }
                else
                {
                    isC = false;
                }
            }
        }
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班一");
        fielditem_c.setItemid("onduty_1");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班一");
        fielditem_c.setItemid("offduty_1");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班二");
        fielditem_c.setItemid("onduty_2");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班二");
        fielditem_c.setItemid("offduty_2");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("上班三");
        fielditem_c.setItemid("onduty_3");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("下班三");
        fielditem_c.setItemid("offduty_3");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班上");
        fielditem_c.setItemid("onduty_4");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班下");
        fielditem_c.setItemid("offduty_4");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);

        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("有效状态");
        fielditem_c.setItemid("flag");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        /*
         * fielditem_c=new FieldItem(); fielditem_c.setItemdesc("结果");
         * fielditem_c.setItemid("isok"); fielditem_c.setItemtype("A");
         * fielditem_c.setCodesetid("0"); fielditem_c.setVisible(true);
         * list.add(fielditem_c);
         */
        columns.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4,card_time,flag,isok");
        this.fieldList = list;
        this.column = columns.toString();
    }

    /**
     * 异常表
     * 
     */
    public void analyseExceptCard()
    {
        StringBuffer columns = new StringBuffer();
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
        fielditem_c.setItemid("nbase");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("@@");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("b0110.label"));
        fielditem_c.setItemid("b0110");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UN");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("e0122.label"));
        fielditem_c.setItemid("e0122");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UM");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        if (!isPost)
        {
            fielditem_c = new FieldItem();
            fielditem_c.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
            fielditem_c.setItemid("e01a1");
            fielditem_c.setItemtype("A");
            fielditem_c.setCodesetid("@K");
            fielditem_c.setVisible(true);
            list.add(fielditem_c);
        }
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("人员编号");
        fielditem_c.setItemid("a0100");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("label.title.name"));
        fielditem_c.setItemid("a0101");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("考勤卡号");
        fielditem_c.setItemid("card_no");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("日期");
        fielditem_c.setItemid("work_date");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("时间");
        fielditem_c.setItemid("work_time");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("机器");
        fielditem_c.setItemid("machine_no");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("地址");
        fielditem_c.setItemid("location");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        columns.append("nbase,b0110,e0122,e01a1,a0100,a0101,card_no,work_date,work_time,");
        columns.append("machine_no,location");
        this.fieldList = list;
        this.column = columns.toString();
    }

    /**
     * 延时加班
     * 
     */
    public void analyseTranOverTimeTab(String tab)
    {
        StringBuffer columns = new StringBuffer();
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
        fielditem_c.setItemid("nbase");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("@@");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("b0110.label"));
        fielditem_c.setItemid("b0110");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UN");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("e0122.label"));
        fielditem_c.setItemid("e0122");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UM");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        if (!isPost)
        {
            fielditem_c = new FieldItem();
            fielditem_c.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
            fielditem_c.setItemid("e01a1");
            fielditem_c.setItemtype("A");
            fielditem_c.setCodesetid("@K");
            fielditem_c.setVisible(true);
            list.add(fielditem_c);
        }
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("人员编号");
        fielditem_c.setItemid("a0100");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("label.title.name"));
        fielditem_c.setItemid("a0101");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("开始时间");
        fielditem_c.setItemid("begin_date");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        fielditem_c.setItemlength(18);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("结束时间");
        fielditem_c.setItemid("end_date");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        fielditem_c.setItemlength(18);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班类型");
        fielditem_c.setItemid("overtime_type");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("27");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        columns.append("nbase,b0110,e0122,e01a1,a0100,a0101,begin_date,end_date,overtime_type");
        
        DbWizard dbWizard = new DbWizard(this.conn);
        if (dbWizard.isExistField(tab, "timelen", false))
        {
            fielditem_c = new FieldItem();
            fielditem_c.setItemdesc("加班时长");
            fielditem_c.setItemid("timelen");
            fielditem_c.setItemtype("N");
            fielditem_c.setCodesetid("0");
            fielditem_c.setVisible(true);
            list.add(fielditem_c);
            columns.append(",timelen");
        }        
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("状态");
        fielditem_c.setItemid("status");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        columns.append(",status");
        
        this.fieldList = list;
        this.column = columns.toString();
    }

    public void analayseCardToOverTimeTab()
    {
        StringBuffer columns = new StringBuffer();  
        
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
        fielditem_c.setItemid("nbase");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("@@");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("人员编号");
        fielditem_c.setItemid("a0100");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("人员姓名");
        fielditem_c.setItemid("a0101");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("b0110.label"));
        fielditem_c.setItemid("b0110");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UN");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("e0122.label"));
        fielditem_c.setItemid("e0122");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UM");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
        fielditem_c.setItemid("e01a1");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("@K");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("开始时间");
        fielditem_c.setItemid("begin_date");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        fielditem_c.setItemlength(18);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("结束时间");
        fielditem_c.setItemid("end_date");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        fielditem_c.setItemlength(18);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班类型");
        fielditem_c.setItemid("overtime_type");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("27");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("加班时长");
        fielditem_c.setItemid("time_len");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        
        String existQ11xx = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
        if (existQ11xx != null && existQ11xx.length() > 0) 
		{
        	fielditem_c = new FieldItem();
        	fielditem_c.setItemdesc("休息扣除数");
        	fielditem_c.setItemid("q11xx");
        	fielditem_c.setItemtype("N");
        	fielditem_c.setCodesetid("0");
        	fielditem_c.setVisible(true);
        	list.add(fielditem_c);
		}
        
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("状态");
        fielditem_c.setItemid("status");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        
        columns.append("nbase,b0110,e0122,e01a1,a0100,a0101,");
        columns.append("begin_date,");
        columns.append("end_date,");
        columns.append("overtime_type,time_len");
        
        if (existQ11xx != null && existQ11xx.length() > 0) {
            columns.append(",q11xx");
        }
        columns.append(",status");
        this.fieldList = list;
        this.column = columns.toString();
    }

    /**
     * 申请比对
     * 
     */
    public void analyseCompareBusiWithFactTab()
    {
        StringBuffer columns = new StringBuffer();
        FieldItem fielditem_c = new FieldItem();
        ArrayList list = new ArrayList();
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("编号");
        fielditem_c.setItemid("id");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("申请编号");
        fielditem_c.setItemid("appid");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
        fielditem_c.setItemid("nbase");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("@@");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("b0110.label"));
        fielditem_c.setItemid("b0110");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UN");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("e0122.label"));
        fielditem_c.setItemid("e0122");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("UM");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        if (!isPost)
        {
            fielditem_c = new FieldItem();
            fielditem_c.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
            fielditem_c.setItemid("e01a1");
            fielditem_c.setItemtype("A");
            fielditem_c.setCodesetid("@K");
            fielditem_c.setVisible(true);
            list.add(fielditem_c);
        }
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("人员编号");
        fielditem_c.setItemid("a0100");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc(ResourceFactory.getProperty("label.title.name"));
        fielditem_c.setItemid("a0101");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        /*
         * fielditem_c=new FieldItem(); fielditem_c.setItemdesc("工号");
         * fielditem_c.setItemid("g_no"); fielditem_c.setItemtype("A");
         * fielditem_c.setCodesetid("0"); fielditem_c.setVisible(true);
         * list.add(fielditem_c);
         */
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("申请类型");
        fielditem_c.setItemid("busi_type");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("27");
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("申请开始");
        fielditem_c.setItemid("busi_begin");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setItemlength(18);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("申请结束");
        fielditem_c.setItemid("busi_end");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setItemlength(18);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("申请时长");
        fielditem_c.setItemid("busi_timelen");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setDecimalwidth(2);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("刷卡开始");
        fielditem_c.setItemid("fact_begin");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setItemlength(18);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("刷卡结束");
        fielditem_c.setItemid("fact_end");
        fielditem_c.setItemtype("A");
        fielditem_c.setCodesetid("0");
        fielditem_c.setItemlength(18);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("刷卡时长");
        fielditem_c.setItemid("fact_timelen");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setDecimalwidth(2);
        fielditem_c.setVisible(true);
        list.add(fielditem_c);
        fielditem_c = new FieldItem();
        fielditem_c.setItemdesc("状态");
        fielditem_c.setItemid("status");
        fielditem_c.setItemtype("N");
        fielditem_c.setCodesetid("0");
        fielditem_c.setVisible(false);
        list.add(fielditem_c);
        columns.append("id,appid,nbase,b0110,e0122,e01a1,a0100,a0101,busi_type,busi_begin,busi_end,busi_timelen,");
        columns.append("fact_begin,fact_end,fact_timelen,status");
        this.fieldList = list;
        this.column = columns.toString();
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public ArrayList getFieldList()
    {
        return fieldList;
    }

    public void setFieldList(ArrayList fieldList)
    {
        this.fieldList = fieldList;
    }

    /**
     * 申请类型list
     * 
     * @return
     */
    public ArrayList getAppTypeList()
    {
        ArrayList list = new ArrayList();
        CommonData da = new CommonData();
        da.setDataName("全部");
        da.setDataValue("all");
        list.add(da);
        da = new CommonData();
        da.setDataName("加班");
        da.setDataValue("q11");
        list.add(da);
        da = new CommonData();
        da.setDataName("请假");
        da.setDataValue("q15");
        list.add(da);
        da = new CommonData();
        da.setDataName("公出");
        da.setDataValue("q13");
        list.add(da);
        return list;

    }

    public boolean getG_no(String table)
    {
        boolean flag = true;
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
        if (!reconstructionKqField.checkFieldSave(table, "g_no"))
        {
            flag = false;
        }
        return flag;
    }

    public boolean getCard_no(String table)
    {
        boolean flag = true;
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
        if (!reconstructionKqField.checkFieldSave(table, "card_no"))
        {
            flag = false;
        }
        return flag;
    }
}
