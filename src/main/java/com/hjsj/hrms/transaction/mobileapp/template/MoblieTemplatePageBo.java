/**
 * 
 */
package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.businessobject.general.template.TTitle;
import com.hjsj.hrms.transaction.mobileapp.template.util.SubTable;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名称:TemplatePageBo 类描述: 创建人: xucs 创建时间:2013-11-28 下午03:42:58 修改人:xucs
 * 修改时间:2013-11-28 下午03:42:58 修改备注:
 * 
 * @version
 * 
 */
public class MoblieTemplatePageBo {
    private UserView userview = null;
    private Connection conn = null;
    private HashMap field_name_map = new HashMap();
    private int tabid;
    private int pageid;
    private String task_id;

    public MoblieTemplatePageBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }

    /**
     * @param conn
     * @param tabid
     * @param pageid
     * @param task_id
     */
    public MoblieTemplatePageBo(Connection conn, int tabid, int pageid, String task_id, UserView userview) {
        this.conn = conn;
        this.tabid = tabid;
        this.pageid = pageid;
        this.task_id = task_id;
        this.userview = userview;
    }

    /**
     * 取得对应标题内容
     * 
     * @return
     */
    public ArrayList getAllTitle() {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rset = null;
        try {
            sql.append("select * from template_title where tabid=");
            sql.append(this.tabid);
            sql.append(" and pageid=");
            sql.append(this.pageid);
            rset = dao.search(sql.toString());
            while (rset.next()) {
                TTitle title = new TTitle();
                title.setGridno(rset.getInt("gridno"));
                title.setPageid(rset.getInt("pageid"));
                title.setTabid(rset.getInt("tabid"));
                title.setFlag(rset.getInt("flag"));
                title.setFonteffect(rset.getInt("Fonteffect"));
                title.setFontname(rset.getString("Fontname"));
                title.setFontsize(rset.getInt("Fontsize"));
                title.setHz(rset.getString("hz") == null ? "" : rset.getString("hz"));
                title.setRtop(rset.getInt("rtop"));
                title.setRleft(rset.getInt("rleft"));
                title.setRwidth(rset.getInt("rwidth"));
                title.setRheight(rset.getInt("rheight"));
                title.setExtendattr(Sql_switcher.readMemo(rset, "extendattr"));
                list.add(title);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * @param c 
     * 
     * @Title: getAllCell
     * @Description: 
     * @param tabid
     * @param pagenum
     * @param taskid
     * @return ArrayList
     * @throws
     */
    public ArrayList getAllCell(int tabid, int pagenum, String taskid) {
        RowSet rset = null;
        ArrayList sqlList = new ArrayList();
        ArrayList cellList = new ArrayList();
        ArrayList setBoList = new ArrayList();
        sqlList.add(Integer.valueOf(tabid));
        sqlList.add(Integer.valueOf(pagenum));
        HashMap var_hm = getAllVariableHm(tabid);
        String sql = "select * from Template_Set where Tabid=? and Pageid=?";
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rset = dao.search(sql, sqlList);
            Element element = null;
            Document doc = null;
           
            Rectangle rect = getBorderRect(tabid, pagenum);
            HashMap fieldmap = new HashMap();
            if (taskid != null && taskid.trim().length() > 0 && !"0".equals(taskid)) {
                fieldmap = getFieldPriv(taskid, this.conn);
            }
            String temp = "";
            StringBuffer querbuffer = new StringBuffer();
            ArrayList op_list = new ArrayList();
            while (rset.next()) {

                MobileTemplateSetBo setbo = new MobileTemplateSetBo(this.conn, this.userview);
                setbo.setHz(rset.getString("hz"));// 设置表格的汉字描述
                setbo.setSetname(rset.getString("setname"));// 设置子集的代码
                setbo.setCodeid(rset.getString("codeid"));// 相关的代码类
                setbo.setField_hz(rset.getString("Field_hz"));// 字段的汉子描述
                setbo.setField_name(rset.getString("Field_name"));// 指标的代码
                String flag = rset.getString("Flag") == null ? "" : rset.getString("Flag");// 数据源的标识（文本描述、照片......）
                if (!"P".equalsIgnoreCase(flag) && !"V".equalsIgnoreCase(flag) && !"S".equalsIgnoreCase(flag) && !"F".equalsIgnoreCase(flag) && rset.getString("Field_name") != null && rset.getString("Field_type") != null && rset.getString("subflag") != null && "0".equals(rset.getString("subflag")) && rset.getString("Field_name").trim().length() > 0 && rset.getString("Field_type").trim().length() > 0) {
                    if ("codesetid".equalsIgnoreCase(rset.getString("Field_name")) || "codeitemdesc".equalsIgnoreCase(rset.getString("Field_name")) || "corcode".equalsIgnoreCase(rset.getString("Field_name")) || "parentid".equalsIgnoreCase(rset.getString("Field_name")) || "start_date".equalsIgnoreCase(rset.getString("Field_name"))) {
                        // 这些特殊的字段的是不能从数据字典里获得的
                    } else {
                        FieldItem item = DataDictionary.getFieldItem(rset.getString("Field_name").trim());
                        if (item == null) {// 数据字典里为空
                            continue;
                        }
                    }
                }

                setbo.setFlag(rset.getString("Flag"));// 设置数据源的标识
                setbo.setFormula(Sql_switcher.readMemo(rset, "Formula"));// 设置字段的计算公式
                setbo.setAlign(rset.getInt("Align"));// 文字在单元格中的排列方式
                setbo.setDisformat(rset.getInt("DisFormat"));// 设置数据的格式
                                                             // 1,2,3,4对数值型为数值精度
                                                             // 后面是对时间的控制

                /** 变量 */
                if ("V".equalsIgnoreCase(flag)) {
                    RecordVo vo = (RecordVo) var_hm.get(rset.getString("Field_name"));
                    if (vo != null) {
                        setbo.setDisformat(vo.getInt("flddec"));// 如果是临时变量
                                                                // 那么要根据临时变量表里面的小数位数来设置
                        setbo.setVarVo(vo);
                    }
                }
                setbo.setChgstate(rset.getInt("ChgState"));// 设置字段是变化前还是变化后

                setbo.setFonteffect(rset.getInt("Fonteffect"));// 设置字体效果
                setbo.setFontname(rset.getString("FontName"));// 设置字体名称
                setbo.setFontsize(rset.getInt("Fontsize"));// 设置字体大小
                setbo.setHismode(rset.getInt("HisMode"));// 设置历史定位方式
                if (Sql_switcher.searchDbServer() == 2)
                    setbo.setMode(rset.getInt("Mode_o"));
                else
                    setbo.setMode(rset.getInt("Mode"));// 多条记录的时候 那几种选择
                                                       // (最近..最初..)
                setbo.setNsort(rset.getInt("nSort"));// 相同指示顺序号
                setbo.setGridno(rset.getInt("gridno"));// 单元格号
                setbo.setPageid(pagenum);//页签号
                setbo.setRcount(rset.getInt("Rcount"));// 记录数 和HisMode
                                                       // 配合试用（标识最近（Rcount条））
                setbo.setRheight(rset.getInt("RHeight"));// 设置单元格高度
                setbo.setRleft(rset.getInt("RLeft"));// 单元格左边的坐标值
                setbo.setRwidth(rset.getInt("RWidth"));// 单元格的宽度
                setbo.setRtop(rset.getInt("RTop"));// 单元格上边坐标值
                setbo.setL(rset.getInt("L")/*
                                            * getRline(this.tabid,this.pageid,"l"
                                            * ,rset.getInt("L"),setbo,dao)
                                            */);
                /** LBRT 代表着表格左下右上是否有线 **/
                setbo.setB(rset.getInt("B")/*
                                            * getRline(this.tabid,this.pageid,"b"
                                            * ,rset.getInt("B"),setbo,dao)
                                            */);
                setbo.setR(rset.getInt("R")/*
                                            * getRline(this.tabid,this.pageid,"r"
                                            * ,rset.getInt("R"),setbo,dao)
                                            */);
                setbo.setT(rset.getInt("T")/*
                                            * getRline(this.tabid,this.pageid,"t"
                                            * ,rset.getInt("T"),setbo,dao)
                                            */);
                temp = rset.getString("subflag");// 子表控制符 0：字段 1：子集
                if (temp == null || "".equals(temp) || "0".equals(temp))
                    setbo.setSubflag(false);
                else
                    setbo.setSubflag(true);
                // 节点必填项
                if (taskid != null && taskid.trim().length() > 0 && !"0".equals(taskid)) {// 这里应该是判断在流程结点中定义的必填项

                    if (fieldmap != null && fieldmap.get((setbo.getField_name() + "_" + setbo.getChgstate()).toLowerCase()) != null && "3".equals((String) fieldmap.get((setbo.getField_name() + "_" + setbo.getChgstate()).toLowerCase()))) {
                        setbo.setYneed(true);

                    } else {
                        if (rset.getInt("yneed") == 0)
                            setbo.setYneed(false);
                        else
                            setbo.setYneed(true);

                    }
                } else {

                    if (rset.getInt("yneed") == 0)
                        setbo.setYneed(false);
                    else
                        setbo.setYneed(true);
                }
                String sub_domain = Sql_switcher.readMemo(rset, "sub_domain");
                setbo.setXml_param(sub_domain);
                // 获得sub_domain_id
                String sub_domain_id = "";
                String titleHeight = "";
                String fields="";
                setbo.setSub_domain_id(sub_domain_id);
                if (sub_domain != null && sub_domain.trim().length() > 0) {
                    try {
                        doc = PubFunc.generateDom(sub_domain);
                        String xpath = "/sub_para/para";
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                                                                  // xpath="/sub_para/para";
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0) {
                            element = (Element) childlist.get(0);
                            if (element.getAttributeValue("limit_manage_priv") != null) {
            				    setbo.setbLimitManagePriv("1".equals((String) element.getAttributeValue("limit_manage_priv")));
            				}
                            titleHeight = element.getAttributeValue("colheadheight") == null ? "" : element.getAttributeValue("colheadheight");
                            fields = element.getAttributeValue("fields")==null?"":element.getAttributeValue("fields");
                            if (element.getAttributeValue("id") != null) {
                                sub_domain_id = (String) element.getAttributeValue("id");
                                if (sub_domain_id != null && sub_domain_id.trim().length() > 0)
                                    setbo.setSub_domain_id(sub_domain_id);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                setbo.setFields(fields);
                /** 由于子集的表头和子集的数据由后台想前台输送，而不是在前台动态加载所以这里为setBo新增了方法和属性 **/
                if (sub_domain != null && sub_domain.trim().length() > 0) {
                   String xpath = "/sub_para/field";
                    try {
                        ArrayList sublist = new ArrayList();
                        doc = PubFunc.generateDom(sub_domain);
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                                                                  // xpath="/sub_para/field";
                        List childlist = findPath.selectNodes(doc);

                        for (int i = 0; i < childlist.size(); i++) {
                            element = (Element) childlist.get(i);
                            SubTable subtable = new SubTable();
                            subtable.setFieldname(element.getAttributeValue("name") == null ? "" : element.getAttributeValue("name"));
                            subtable.setAlign(element.getAttributeValue("align") == null ? "" : element.getAttributeValue("align"));
                            subtable.setDefaultvalue(element.getAttributeValue("default") == null ? "" : element.getAttributeValue("default"));
                            subtable.setNeed(element.getAttributeValue("need") == null ? "" : element.getAttributeValue("need"));
                            subtable.setPre(element.getAttributeValue("pre") == null ? "" : element.getAttributeValue("pre"));
                            subtable.setSlop(element.getAttributeValue("slop") == null ? "" : element.getAttributeValue("slop"));
                            subtable.setTitle(element.getAttributeValue("title") == null ? "" : element.getAttributeValue("title"));
                            subtable.setValign(element.getAttributeValue("valign") == null ? "" : element.getAttributeValue("valign"));
//                            subtable.setWidth(element.getAttributeValue("width") == null ? "" : element.getAttributeValue("width"));
                            String width =element.getAttributeValue("width")==null?"0":element.getAttributeValue("width");
                            int pt =(Integer.valueOf(width).intValue())*8;
                            subtable.setWidth(String.valueOf(pt));
                            subtable.setTitleheight(titleHeight);
                            sublist.add(subtable);
                        }
                        setbo.setSuTableList(sublist);
                    } catch (Exception e) {

                    }
                }
                setbo.setField_type(rset.getString("Field_type"));
                if (!setbo.isSubflag() && "1".equals("" + rset.getInt("ChgState")) && flag != null && !"H".equals(flag.toUpperCase())) {
                    if (Sql_switcher.searchDbServer() == 2) {
                        if (("2".equals("" + rset.getInt("HisMode")) && ("1".equals("" + rset.getInt("Mode_o")) || "3".equals("" + rset.getInt("Mode_o")))) || "3".equals("" + rset.getInt("HisMode")) || "4".equals("" + rset.getInt("HisMode"))) {// (序号定位&&(最近||最初))
                                                                                                                                                                                                                                                    // ||
                                                                                                                                                                                                                                                    // 条件定位||条件序号
                            setbo.setField_type("M");
                            if (setbo.getField_name() != null && setbo.getField_name().length() > 0) {
                                if (setbo.getSub_domain_id() != null && setbo.getSub_domain_id().length() > 0) {
                                    this.field_name_map.put(setbo.getField_name().toLowerCase() + "_" + setbo.getSub_domain_id() + "_" + rset.getInt("ChgState"), setbo.getField_name() + "_" + setbo.getSub_domain_id() + "_" + rset.getInt("ChgState"));
                                } else {
                                    this.field_name_map.put(setbo.getField_name().toLowerCase() + "_" + rset.getInt("ChgState"), setbo.getField_name() + "_" + rset.getInt("ChgState"));
                                }
                            }

                        }
                    } else {
                        if (("2".equals("" + rset.getInt("HisMode")) && ("1".equals("" + rset.getInt("Mode")) || "3".equals("" + rset.getInt("Mode")))) || "3".equals("" + rset.getInt("HisMode")) || "4".equals("" + rset.getInt("HisMode"))) {
                            setbo.setField_type("M");
                            if (setbo.getField_name() != null && setbo.getField_name().length() > 0) {
                                if (setbo.getSub_domain_id() != null && setbo.getSub_domain_id().length() > 0) {
                                    this.field_name_map.put(setbo.getField_name().toLowerCase() + "_" + setbo.getSub_domain_id() + "_" + rset.getInt("ChgState"), setbo.getField_name() + "_" + setbo.getSub_domain_id() + "_" + rset.getInt("ChgState"));
                                } else {
                                    this.field_name_map.put(setbo.getField_name().toLowerCase() + "_" + rset.getInt("ChgState"), setbo.getField_name() + "_" + rset.getInt("ChgState"));
                                }
                            }
                        }
                    }
                }
                setbo.setRect(rect);// 设置表格的区域
                if (rset.getString("nhide") != null)
                    setbo.setNhide(rset.getInt("nhide"));
                else
                    setbo.setNhide(0);// 打印还是隐藏 0：打印 1：隐藏
                //linbz 29367 增加校验Field_name是否为空
                if (setbo.getField_name() != null && setbo.getField_name().length() > 0) {
	                FieldItem fielditem = DataDictionary.getFieldItem(setbo.getField_name());
	                if (fielditem!=null){//wangrd 20160902 以指标体系为准 防止指标类型发生改变
	                	setbo.setCodeid(fielditem.getCodesetid());
	                }
                }
                if (!"H".equals(flag)) {// 从数据库中查询表格中应该填入的数据
                    if (setbo.isSubflag()) {
                        querbuffer.append("t_");
                    }
                    if(setbo.isSubflag()){
                        querbuffer.append(setbo.getSetname()+"_");
                    }else{
                        querbuffer.append(setbo.getField_name() + "_");
                    }
                    
                    if (setbo.getSub_domain_id() != null && setbo.getSub_domain_id().length() > 0) {
                        querbuffer.append(setbo.getSub_domain_id() + "_");
                    }
                    querbuffer.append(setbo.getChgstate() + ",");
                }

                setBoList.add(setbo);
                op_list.add(setbo.clone());
            }
            ArrayList new_setbo = new ArrayList();
            int b = 0;
            int l = 0;
            int r = 0;
            int t = 0;
            for (int i = 0; i < op_list.size(); i++) {
                MobileTemplateSetBo cur_setbo = (MobileTemplateSetBo) op_list.get(i);

                b = getRlineForList(setBoList, "b", cur_setbo.getB(), cur_setbo);
                l = getRlineForList(setBoList, "l", cur_setbo.getL(), cur_setbo);
                r = getRlineForList(setBoList, "r", cur_setbo.getR(), cur_setbo);
                t = getRlineForList(setBoList, "t", cur_setbo.getT(), cur_setbo);
                cur_setbo.setB(b);
                cur_setbo.setL(l);
                cur_setbo.setR(r);
                cur_setbo.setT(t);
                new_setbo.add(cur_setbo);
            }
            cellList.add(new_setbo);
            if (querbuffer.length() > 1) {
                querbuffer.setLength(querbuffer.length() - 1);
            }
            cellList.add(querbuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cellList;
    }

    /**
     * @Title: getRlineForList
     * @Description: 
     * @param setBoList
     * @param string
     * @param Equal
     * @param curSetbo
     * @return
     * @throws int
     */
    private int getRlineForList(ArrayList list, String flag, int line, MobileTemplateSetBo cur_setbo) {
        if(line==0)
            return line;
        else
        {
            float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
            float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
            float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
            float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
            MobileTemplateSetBo setbo;  
            float rtop=0;
            float rheight=0;
            float rleft=0;
            float rwidth=0;
            int b=0;
            int t=0;
            int r=0;
            int l=0;
            int cur_gridno=cur_setbo.getGridno();
            int gridno=0;
            try
            {  
                for(int i=0;i<list.size();i++)
                {
                    setbo=(MobileTemplateSetBo)list.get(i);  
                    rtop=setbo.getRtop();
                    rheight=setbo.getRheight();
                    rleft=setbo.getRleft();
                    rwidth=setbo.getRwidth();
                    gridno=setbo.getGridno();
                    if(cur_gridno==gridno)
                        continue;
                    if("t".equals(flag))
                    {
                       b=setbo.getB();//得到每一个单元格的下部                    
                       if(b==0)
                       {
                         if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
                          {
                             line=0;
                             break;
                          }
                       }
                    }else if("b".equals(flag))
                    {
                        t=setbo.getT();
                        if(t==0)
                        {
                            if(rtop==(cur_rtop+cur_rheight)&&
                                ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
                                 (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
                                )
                              )
                            {
                                line=0;
                                 break;
                            }
                        }                       
                    }else if("l".equals(flag))
                    {
                        r=setbo.getR();
                        if(r==0)
                        {
                            if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
                            {
                                line=0;
                                break;
                            }
                        }                       
                    }else if("r".equals(flag))
                    {
                        l=setbo.getL();
                        if(l==0)
                        {
                            if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
                            {
                                line=0;
                                break;
                            }
                        }
                    }
                }
                
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }       
        return line; 
    }

    /**
     * @Title: getFieldPriv
     * @Description: 用来判断结点的必填项
     * @param taskid
     *            模版号
     * @param conn
     * @return HashMap
     * @throws
     */

    private HashMap getFieldPriv(String taskid, Connection conn) {
        HashMap _map = new HashMap();
        Document doc = null;
        Element element = null;
        ArrayList sqlList = new ArrayList();
        sqlList.add(taskid);
        try {
            ContentDAO dao = new ContentDAO(conn);
            String sql = "select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id=?)";
            RowSet rowSet = dao.search(sql, sqlList);
            if (rowSet.next()) {
                String ext_param = Sql_switcher.readMemo(rowSet, "ext_param");
                if (ext_param != null && ext_param.trim().length() > 0) {
                    doc = PubFunc.generateDom(ext_param);
                    String xpath = "/params/field_priv/field";
                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    List childlist = findPath.selectNodes(doc);
                    if (childlist.size() == 0) {
                        xpath = "/params/field_priv/field";
                        findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        childlist = findPath.selectNodes(doc);
                    }
                    if (childlist != null && childlist.size() > 0) {
                        for (int i = 0; i < childlist.size(); i++) {
                            element = (Element) childlist.get(i);
                            String editable = "";
                            // 0|1|2(无|读|写)
                            if (element != null && element.getAttributeValue("editable") != null)
                                editable = element.getAttributeValue("editable");
                            if (editable != null && editable.trim().length() > 0) {
                                String columnname = element.getAttributeValue("name").toLowerCase();
                                if (columnname.endsWith("_2")) {
                                    if ("1".equals(editable))
                                        editable = "0";
                                    String fillable = element.getAttributeValue("fillable");
                                    if ("2".equals(editable) && fillable != null && "true".equalsIgnoreCase(fillable))
                                        editable = "3";
                                    _map.put(columnname, editable);
                                }

                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _map;
    }

    /**
     * 
     * @Title: getAllVariableHm
     * @Description: 
     * @return HashMap
     * @throws
     */
    private HashMap getAllVariableHm(int tabid) {
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hm = new HashMap();
        try {
            strsql.append("select * from midvariable where nflag=0 and templetId <> 0 and (templetId = "+tabid+" or cstate = '1')"); //包含共享临时变量 2014-02-22
            strsql.append(" order by sorting");         
            RowSet rset = dao.search(strsql.toString());
            while (rset.next()) {
                RecordVo vo = new RecordVo("midvariable");
                vo.setString("cname", rset.getString("cname"));
                vo.setString("chz", rset.getString("chz"));
                vo.setInt("ntype", rset.getInt("ntype"));
                vo.setString("cvalue", rset.getString("cValue"));
                String codesetid = rset.getString("codesetid");
                if (codesetid == null || "".equalsIgnoreCase(codesetid))
                    codesetid = "0";
                vo.setString("codesetid", codesetid);
                vo.setInt("fldlen", rset.getInt("fldlen"));
                vo.setInt("flddec", rset.getInt("flddec"));
                hm.put(rset.getString("cname"), vo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hm;
    }

    /**
     * 求外边框的区域
     */
    private Rectangle getBorderRect(int tabid, int pageid) {
        ArrayList sqlList = new ArrayList();
        sqlList.add(Integer.valueOf(tabid));
        sqlList.add(Integer.valueOf(pageid));
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        RowSet rset = null;
        try {
            sql.append("select min(rtop) as ltop,min(rleft) as lleft,");
            sql.append("max(rtop+rheight) as dtop,max(RLeft + RWidth) as dleft from template_set ");
            sql.append(" where tabid=? and pageid=?");
            rset = dao.search(sql.toString(), sqlList);
            if (rset.next()) {
                int x = rset.getInt("lleft");
                int y = rset.getInt("ltop");
                int width = rset.getInt("dleft") - x;
                int height = rset.getInt("dtop") - y;
                rect.setRect(x, y, width, height);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rect;
    }
}
