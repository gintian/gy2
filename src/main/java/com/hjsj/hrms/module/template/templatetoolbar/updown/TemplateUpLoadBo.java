package com.hjsj.hrms.module.template.templatetoolbar.updown;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.RowSet;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Mar 3, 2010 11:39:22 AM
 * </p>
 * 
 * @author dengc
 * @version 5.0
 */
public class TemplateUpLoadBo {
    private Connection conn = null;
    private ArrayList templateSetList = new ArrayList(); // 模板指标集
    private String tabid = "";
    private RecordVo table_vo = null;
    private UserView userview = null;
    /**
     * 业务类型 对人员调入的业务单独处理 =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3调动,
     * =10其它不作特殊处理的业务 如果目标库未指定的话，则按源库进行处理
     */
    private int operationtype = 0;
    private TemplateTableBo bo = null;
    private String hmuster_sql = ""; // 当前模板处理人员的sql
    private int Infor_type = 1; // 1人员，2单位，3职务
    private int class_type = 0; // 0本身调用,1为HistoryDataBo调用
    private HashMap hiddenMap = new HashMap();
    private String taskid = "0";
    private Integer isMobile=-1;//-1获取全部，0只获取电脑模版，1只获取手机模版

    public String getTaskid() {
        return taskid;
    }
    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
    public TemplateUpLoadBo(String tabid, Connection con, UserView userview) {
        this.conn = con;
        this.tabid = tabid;
        try {
            this.userview = userview;
            table_vo =TemplateUtilBo.getTableVo(Integer.parseInt(this.tabid),con);
            String operationcode = this.table_vo.getString("operationcode");
            this.operationtype = findOperationType(operationcode);
            // 隐藏指标
            this.hiddenMap = hiddenfielditem();
            bo = new TemplateTableBo(con, table_vo, userview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getOperationtype(String operationtype) {

        return this.operationtype;
    }

    /**
     * 人员调入模板
     */
    public void autoAddRecord(TemplateTableBo tablebo, String tablename) throws GeneralException {
        ContentDAO dao = null;
        try {
            if (!(tablebo.getOperationtype() == 0 || tablebo.getOperationtype() == 5))
                return;
            dao = new ContentDAO(this.conn);

            StringBuffer buf = new StringBuffer();
            buf.append("select count(*) as nrec from ");
            buf.append(tablename);
            RowSet rset = dao.search(buf.toString());
            int irow = 0;
            if (rset.next())
                irow = rset.getInt("nrec");
            if (irow != 0)
                return;
            String a0100 = null;
            RecordVo vo = new RecordVo(tablename);
            IDGenerator idg = new IDGenerator(2, this.conn);

            /**
             * 查找变化前的历史记录单元格 保存时把这部分单元格的内容 过滤掉，不作处理
             */
            HashMap sub_map = tablebo.getHisModeSubCell();
            a0100 = idg.getId("rsbd.a0100");
            if (tablebo.getInfor_type() == 2 || tablebo.getInfor_type() == 3)
                a0100 = "B" + a0100;
            if (tablebo.getInfor_type() == 1) {
                vo.setString("a0100", a0100);

            } else if (tablebo.getInfor_type() == 2) {
                vo.setString("b0110", a0100);
            } else if (tablebo.getInfor_type() == 3) {
                vo.setString("e01a1", a0100);
            }
            // vo.setString("a0100",a0100);
            /*
             * if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
             * if(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0)
             * throw new GeneralException("人员调入业务模板未定义目标库!");
             * vo.setString("basepre",tablebo.getDest_base()); } else
             * vo.setString("basepre","");
             */
            if (tablebo.getInfor_type() == 1 && (tablebo.getDest_base() == null || tablebo.getDest_base().length() == 0))
                throw new GeneralException("人员调入业务模板未定义目标库!");
            ArrayList dbList = DataDictionary.getDbpreList();
            if (tablebo.getInfor_type() == 1) {
                String dbpre = tablebo.getDest_base();
                for (int i = 0; i < dbList.size(); i++) {
                    String pre = (String) dbList.get(i);
                    if (pre.equalsIgnoreCase(tablebo.getDest_base()))
                        dbpre = pre;
                }
                vo.setString("basepre", dbpre);
                if (vo.hasAttribute("a0101_2")) {
                    vo.setString("a0101_2", "--");
                }
                if (vo.hasAttribute("a0101_1")) {
                    vo.setString("a0101_1", "--");
                }
            } else {
                if (vo.hasAttribute("codeitemdesc_2")) {
                    vo.setString("codeitemdesc_2", "--");
                }
                if (vo.hasAttribute("codeitemdesc_1")) {
                    vo.setString("codeitemdesc_1", "--");
                }

            }
            Iterator iterator = sub_map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String field_name = entry.getKey().toString();
                TemplateSetBo setbo = (TemplateSetBo) entry.getValue();
                TSubSetDomain setdomain = new TSubSetDomain(setbo.getXml_param());
                String xml = setdomain.outContentxml();
                vo.setString(field_name.toLowerCase(), xml);
            }
            String seqnum = CreateSequence.getUUID();
            vo.setString("seqnum", seqnum);
            dao.addValueObject(vo);
            if ("1".equals(tablebo.getId_gen_manual())) {

            } else {
                tablebo.filloutSequence(a0100, tablebo.getDest_base(), tablename);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }

    /**
     * 批量修改某个薪资项目数据
     * 
     * @param itemid
     *            目标指标
     * @param formula
     *            计算公式（替换后的内容。里面可以包含字符串，由函数向导生成的公式。即：那块文本域中可以存放任何内容）
     * @param cond
     *            修改条件
     * @param whl
     *            过滤条件
     * @return
     * @throws GeneralException
     */
    public boolean batchUpdateItem(String itemid, String formula, String cond, String whl, String tablename, String taskid, String selchecked, String needcondition) throws GeneralException {
        boolean bflag = true;
        try {
            YksjParser yp = null;
            /** 加载一次 */
            ArrayList fldvarlist = bo.getAllFieldItem();// 模板中所有字段（因为交易类是ajax，所以这些字段必须重新查一遍）
            /** 必填项校验 */
            ArrayList fieldlist = bo.getMidVariableList();
            HashMap map = bo.getSub_domain_map();
            fldvarlist.addAll(fieldlist);

            if (this.bo.getInfor_type() == 2)// 如果是单位
            {
                needcondition = needcondition.replaceAll("b0110_1", "b0110");
                needcondition = needcondition.replaceAll("B0110_1", "b0110");
            }
            if (this.bo.getInfor_type() == 3)// 如果是岗位
            {
                needcondition = needcondition.replaceAll("e01a1_1", "e01a1");
                needcondition = needcondition.replaceAll("E01A1_1", "e01a1");
            }
            // /作用是
            if (this.operationtype != 0)// 不是人员调入
            {
                if (bo.getInfor_type() == 1) {
                    FieldItem item = DataDictionary.getFieldItem("B0110");
                    if (item != null) {
                        /** 可以增加模板指标与字典表指标进行校验 */
                        FieldItem tempitem = (FieldItem) item.cloneItem();
                        tempitem.setNChgstate(1);
                        fldvarlist.add(tempitem);
                    }

                    item = DataDictionary.getFieldItem("E0122");
                    if (item != null) {
                        /** 可以增加模板指标与字典表指标进行校验 */
                        FieldItem tempitem = (FieldItem) item.cloneItem();
                        tempitem.setNChgstate(1);
                        fldvarlist.add(tempitem);
                    }

                    item = DataDictionary.getFieldItem("E01A1");
                    if (item != null) {
                        /** 可以增加模板指标与字典表指标进行校验 */
                        FieldItem tempitem = (FieldItem) item.cloneItem();
                        tempitem.setNChgstate(1);
                        fldvarlist.add(tempitem);
                    }

                    item = DataDictionary.getFieldItem("A0101");
                    if (item != null) {
                        /** 可以增加模板指标与字典表指标进行校验 */
                        FieldItem tempitem = (FieldItem) item.cloneItem();
                        tempitem.setNChgstate(1);
                        fldvarlist.add(tempitem);
                    }
                }
            }
            /*
             * if(bo.getInfor_type()==2||bo.getInfor_type()==3){ FieldItem
             * item=DataDictionary.getFieldItem("B0110"); if(item!=null) {
             * //可以增加模板指标与字典表指标进行校验 FieldItem
             * tempitem=(FieldItem)item.cloneItem(); tempitem.setNChgstate(1);
             * fldvarlist.add(tempitem); } }
             */

            // 为fldvarlist的每个fielditem的指标加上变化前变化后标志。如a0101_1,b0110_2。
            for (int i = 0; i < fldvarlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fldvarlist.get(i);
                if (fielditem.getVarible() != 1)// 如果不是临时变量
                {
                    if (fielditem.isChangeAfter()) {
                        fielditem.setItemid(fielditem.getItemid() + "_2");
                        String desc = (String) fielditem.getItemdesc();
                        if (desc != null && desc.trim().length() > 0 && "拟".equalsIgnoreCase(desc.substring(0, 1))) {
                            fielditem.setItemdesc("" + fielditem.getItemdesc());
                        } else
                            fielditem.setItemdesc("拟" + fielditem.getItemdesc());
                    }
                    if (fielditem.isChangeBefore()) {
                        // 多个变化前加上_id
                        if (map != null && map.get("" + i) != null && map.get("" + i).toString().trim().length() > 0) {
                            fielditem.setItemid(fielditem.getItemid() + "_" + map.get("" + i) + "_1 ");
                            fielditem.setItemdesc("" + map.get("" + i + "hz"));
                        } else {
                            fielditem.setItemid(fielditem.getItemid() + "_1 ");
                        }
                    }
                }
            }
            // 获取sql语句查询条件（sql语句中where的写法）
            StringBuffer strwhere = new StringBuffer();
            if (cond.length() == 0 || "undefined".equalsIgnoreCase(cond))// 如果没有条件
                strwhere.append(" where 1=1");
            else {
                strwhere.append(" where ");
                yp = new YksjParser(this.userview, fldvarlist, YksjParser.forNormal, YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");
                yp.run_where(cond);
                String strfilter = yp.getSQL();
                strwhere.append(strfilter);
            }
            if (needcondition != null && needcondition.trim().length() > 0)
                strwhere.append(" " + needcondition);
            if (whl != null && whl.trim().length() > 0)
                strwhere.append(" and " + whl);

            String _itemid = itemid;// 去掉变化前变化后标识后缀的指标
            if (_itemid.indexOf("_2") != -1 || _itemid.indexOf("_1") != -1)
                _itemid = _itemid.substring(0, _itemid.lastIndexOf("_"));

            FieldItem fielditem = DataDictionary.getFieldItem(_itemid);
            if (fielditem == null) {
                if ("codesetid".equalsIgnoreCase(_itemid) || "codeitemdesc".equalsIgnoreCase(_itemid) || "corcode".equalsIgnoreCase(_itemid) || "parentid".equalsIgnoreCase(_itemid) || "start_date".equalsIgnoreCase(_itemid)) {
                    if (bo.getInfor_type() != 1) {
                        fielditem = new FieldItem();
                        if (!"start_date".equalsIgnoreCase(_itemid)) {
                            fielditem.setItemtype("A");
                            fielditem.setItemlength(50);
                        } else
                            fielditem.setItemtype("D");
                        fielditem.setUseflag("1");
                    }
                }
            }
            String datatype = fielditem.getItemtype();// 需要改变的指标的指标类型
            int fieldlength = fielditem.getItemlength();
            String strexpr = "";
            if ("D".equals(datatype) && (formula.split("\\.").length == 3 || formula.split("-").length == 3))// 如果是符合规范的日期型指标
            {
                if (formula.charAt(0) != '#' || formula.charAt(formula.length() - 1) != '#')
                    throw GeneralExceptionHandler.Handle(new Exception("日期格式不正确,格式为 #yyyy-mm-dd#!"));
                formula = formula.replaceAll("#", "");
                String[] temp = null;
                if (formula.split("\\.").length == 3)
                    temp = formula.split("\\.");
                else
                    temp = formula.split("-");
                Calendar d = Calendar.getInstance();
                try {
                    d.set(Calendar.YEAR, Integer.parseInt(temp[0]));
                    d.set(Calendar.MONTH, Integer.parseInt(temp[1]) - 1);
                    d.set(Calendar.DATE, Integer.parseInt(temp[2]));
                } catch (Exception ee) {
                    throw GeneralExceptionHandler.Handle(new Exception("日期格式不正确!"));
                }
                StringBuffer buf = new StringBuffer();
                buf.append("update " + tablename + " set ");
                buf.append(itemid);
                buf.append("=?");
                buf.append(strwhere.toString());

                if (taskid != null && !"0".equals(taskid)) {
                    buf.append(" and exists (select null from t_wf_task_objlink where templet_" + this.tabid + ".seqnum=t_wf_task_objlink.seqnum and templet_" + this.tabid + ".ins_id=t_wf_task_objlink.ins_id ");
                    if ("1".equals(selchecked))
                        buf.append(" and  submitflag=1 ");
                    buf.append(" and task_id in(" + taskid + ") ) ");

                } else {
                    if ("1".equals(selchecked))
                        buf.append(" and submitflag=1");
                }

                java.sql.Date date = new java.sql.Date(d.getTimeInMillis());
                ContentDAO dao = new ContentDAO(conn);
                ArrayList paramList = new ArrayList();
                paramList.add(date);
                dao.update(buf.toString(), paramList);
                // pr.setDate(1,date);
                // pr.execute();
                // pr.close();

            } else// 如果是除日期型外其它类型的指标
            {
                ContentDAO dao = new ContentDAO(this.conn);
                RecordVo vo = new RecordVo(tablename);
                StringBuffer sb = new StringBuffer("");
                sb.append("select " + itemid + " from " + tablename + " " + strwhere.toString());
                RowSet rowSet = dao.search(sb.toString());
                sb.setLength(0);
                ResultSetMetaData data = rowSet.getMetaData();
                int columnType = data.getColumnType(1);
                if (columnType == java.sql.Types.CLOB || columnType == -1)// 如果目标字段（要修改的字段）是备注型
                {

                    if (taskid != null && !"0".equals(taskid)) {
                        strwhere.append(" and exists (select null from t_wf_task_objlink where templet_" + this.tabid + ".seqnum=t_wf_task_objlink.seqnum and templet_" + this.tabid + ".ins_id=t_wf_task_objlink.ins_id ");
                        if ("1".equals(selchecked))
                            strwhere.append("  and  submitflag=1 ");
                        strwhere.append(" and task_id in(" + taskid + ") ) ");

                    } else {
                        if ("1".equals(selchecked))
                            strwhere.append(" and submitflag=1");
                    }
                    sb.append("select * from " + tablename + " " + strwhere.toString());
                    rowSet = dao.search(sb.toString());
                    while (rowSet.next()) {
                        if (this.bo != null && this.bo.getInfor_type() == 1) {// 如果是人事业务
                            vo.setString("a0100", rowSet.getString("a0100"));
                            vo.setString("basepre", rowSet.getString("basepre"));
                        } else if (this.bo != null && this.bo.getInfor_type() == 2) {// 单位或部门
                            vo.setString("b0110", rowSet.getString("b0110"));
                        } else if (this.bo != null && this.bo.getInfor_type() == 3) {// 岗位
                            vo.setString("e01a1", rowSet.getString("e01a1"));
                        } else {
                            vo.setString("a0100", rowSet.getString("a0100"));
                            vo.setString("basepre", rowSet.getString("basepre"));
                        }
                        if (tablename.equalsIgnoreCase("templet_" + this.tabid))
                            vo.setInt("ins_id", rowSet.getInt("ins_id"));
                        vo = dao.findByPrimaryKey(vo);
                        vo.setString(itemid.toLowerCase(), formula);// 备注型也不会检查替换后的内容是否合法
                        dao.updateValueObject(vo);
                    }
                } else {// 如果字段是数值型、字符型或代码型

                    yp = new YksjParser(this.userview, fldvarlist, YksjParser.forNormal, getDataType(datatype), YksjParser.forPerson, "Ht", "");
                    yp.run(formula, this.conn, cond, tablename);
                    /** 单表计算 */
                    strexpr = yp.getSQL();
                    /** 为空不计算 */
                    if (strexpr.trim().length() == 0)
                        return true;
                    // 超过字段的长度报错
                    StringBuffer buf = new StringBuffer();
                    buf.append("update " + tablename + " set ");
                    buf.append(itemid);
                    buf.append("=");
                    buf.append(strexpr);
                    buf.append(strwhere.toString());

                    if (taskid != null && !"0".equals(taskid)) {
                        buf.append(" and exists (select null from t_wf_task_objlink where " + tablename + ".seqnum=t_wf_task_objlink.seqnum and " + tablename + ".ins_id=t_wf_task_objlink.ins_id ");
                        if ("1".equals(selchecked))
                            buf.append("  and  submitflag=1 ");
                        buf.append(" and task_id in(" + taskid + ") ) ");

                    } else {
                        if ("1".equals(selchecked))
                            buf.append(" and submitflag=1");
                    }

                    // System.out.println("buf.toString():"+buf.toString());
                    try {
                        dao.update(buf.toString());
                    } catch (Exception e2) {
                        if (strexpr.trim().replace("'", "").getBytes().length > fieldlength && fieldlength > 0)
                            throw GeneralExceptionHandler.Handle(new Exception(strexpr.trim().replace("'", "") + "的长度不能超过" + fieldlength + "!\r\n注：一个汉字的长度是2"));
                    }
                }

            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            bflag = false;
            throw GeneralExceptionHandler.Handle(ex);
        }
        return bflag;
    }

    /**
     * 
     * 
     * @Title: batchUpdateFields
     * 
     * @Description: TODO
     * 
     * @param field_item_list
     *            要修改的字段的id
     * @param field_value_list
     *            要修改的字段所对应的值
     * @param field_type_list
     *            要修改的字段的类型
     * @param tablename
     *            涉及的模版表
     * @param taskid
     *            任务号
     * @param selchecked
     *            （全部记录|勾选记录）
     * @return boolean
     * 
     * @throws
     */
    public boolean batchUpdateFields(ArrayList field_item_list, ArrayList field_value_list, ArrayList field_type_list, String tablename, String taskid, String selchecked) {
        boolean bflag = true;
        try {
            StringBuffer buf = new StringBuffer();
            buf.append("update " + tablename + " set ");

            ArrayList datalist = new ArrayList();// 存放要修改的字段的
            String updateValue = "";// 要修改的字段
            for (int i = 0; i < field_item_list.size(); i++) {
                String fieldvalue = (String) field_value_list.get(i);
                String fieldtype = (String) field_type_list.get(i);
                String itemid = (String) field_item_list.get(i);
                if ("M".equalsIgnoreCase(fieldtype)) {
                    fieldvalue = SafeCode.decode(fieldvalue);
                }
                if ("A".equalsIgnoreCase(fieldtype) || "M".equalsIgnoreCase(fieldtype)) {
                    datalist.add(fieldvalue);
                } else if ("N".equalsIgnoreCase(fieldtype)) {
                    if ("".equals(fieldvalue)) {
                        continue;
                    }
                    int index = itemid.indexOf("_");
                    String citemid = itemid;
                    if (index > -1)
                        citemid = itemid.substring(0, index);// 另起一个变量，因为下面还需要使用itemid
                                                                // wangrd
                                                                // 2014-01-06
                    FieldItem item = DataDictionary.getFieldItem(citemid);
                    int length = item.getDecimalwidth();
                    if (length > 0) {
                        Double value = new Double(fieldvalue);
                        datalist.add(value);
                    } else {
                        Integer value = new Integer(fieldvalue);
                        datalist.add(value);
                    }

                } else if ("D".equalsIgnoreCase(fieldtype)) {
                    if ("".equals(fieldvalue)) {
                        continue;
                    }
                    java.sql.Date date = java.sql.Date.valueOf(fieldvalue);
                    datalist.add(date);
                }
                updateValue = updateValue + itemid + "=?,";
            }
            if (updateValue.trim().length() > 0) {
                updateValue = updateValue.substring(0, updateValue.length() - 1);
            }
            buf.append(updateValue);

            if (taskid != null && !"0".equals(taskid)) {
                buf.append(" where exists (select null from t_wf_task_objlink where " + tablename + ".seqnum=t_wf_task_objlink.seqnum and " + tablename + ".ins_id=t_wf_task_objlink.ins_id ");
                if ("1".equals(selchecked))
                    buf.append("  and  submitflag=1 ");
                buf.append(" and task_id in(" + taskid + ") ) ");
            } else {
                if ("1".equals(selchecked))
                    buf.append(" where submitflag=1");
            }

            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(buf.toString(), datalist);
        } catch (Exception ex) {
            bflag = false;
            ex.printStackTrace();
        }
        return bflag;
    }

    /**
     * 数值类型进行转换
     * 
     * @param type
     * @return
     */
    private int getDataType(String type) {
        int datatype = 0;
        switch (type.charAt(0)) {
            case 'A' :
                datatype = YksjParser.STRVALUE;
                break;
            case 'D' :
                datatype = YksjParser.DATEVALUE;
                break;
            case 'N' :
                datatype = YksjParser.FLOAT;
                break;
        }
        return datatype;
    }

    /**
     * 取得权限范围内的人员库列表
     * 
     * @return
     * @throws GeneralException
     */
    public String getDbStr() throws GeneralException {
        ArrayList dblist = this.userview.getPrivDbList();
        DbNameBo dbvo = new DbNameBo(this.conn);
        dblist = dbvo.getDbNameVoList(dblist);
        StringBuffer strdb = new StringBuffer();
        for (int i = 0; i < dblist.size(); i++) {
            RecordVo dbname = (RecordVo) dblist.get(i);
            strdb.append(dbname.getString("pre"));
            strdb.append(",");
        }
        if (strdb.length() > 0)
            strdb.setLength(strdb.length() - 1);
        return strdb.toString();
    }

    /**
     * 取得权限范围内的人员库列表
     * 
     * @return
     * @throws GeneralException
     */
    public String getDbStr(String type) throws GeneralException {

        ArrayList dblist = this.userview.getPrivDbList();
        if (type != null && "23".equals(type)) {
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, userview);
            dblist = kqUtilsClass.setKqPerList(null, "2");
        }
        DbNameBo dbvo = new DbNameBo(this.conn);
        dblist = dbvo.getDbNameVoList(dblist);
        StringBuffer strdb = new StringBuffer();
        for (int i = 0; i < dblist.size(); i++) {
            RecordVo dbname = (RecordVo) dblist.get(i);
            strdb.append(dbname.getString("pre"));
            strdb.append(",");
        }
        if (strdb.length() > 0)
            strdb.setLength(strdb.length() - 1);
        return strdb.toString();
    }

    /**
     * 查找业务类型 0,1,2,3,4,10 对人员调入，人员调出等业务对一些特殊的规则
     * 
     * @param operationcode
     * @return
     */
    private int findOperationType(String operationcode) {
    	/*
        StringBuffer strsql = new StringBuffer();
        strsql.append("select operationtype from operation where operationcode='");
        strsql.append(operationcode);
        strsql.append("'");
        ContentDAO dao = new ContentDAO(this.conn);
        int flag = -1;
        try {
            RowSet rset = dao.search(strsql.toString());
            if (rset.next())
                flag = rset.getInt("operationtype");
            // flag=0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    	int flag=TemplateStaticDataBo.getOperationType(operationcode, conn);
        return flag;
    }

    /**
     * 取得表头
     * 
     * @param cellList
     * @param hiddenItem
     * @param lockedItemStr
     * @return
     */
    public ArrayList getTableHeadSetList(ArrayList cellList, String fieldSetSortStr, String hiddenItem, String lockedItemStr, String isCompare, ArrayList tasklist) {
        ArrayList list = new ArrayList();
        HashMap preMap = new HashMap();
        HashMap endMap = new HashMap();
        try {
            if (hiddenItem.length() > 0)
                hiddenItem = ("," + hiddenItem + ",").toLowerCase();
            if (lockedItemStr.length() > 0)
                lockedItemStr = ("," + lockedItemStr + ",").toLowerCase();
            String taskid = this.taskid;
            if (tasklist != null && tasklist.size() > 0) {
                if (tasklist.get(0).toString().length() > 0)
                    taskid = "" + tasklist.get(0);
            }
            HashMap filedPrivByNode = getFieldPriv(taskid, conn);

            LazyDynaBean cellBean = null;
            LazyDynaBean headBean = null;
            if (fieldSetSortStr.length() > 0) //
            {
                String[] strs = fieldSetSortStr.split(",");
                HashMap map = new HashMap();
                for (int i = 0; i < cellList.size(); i++) {

                    cellBean = (LazyDynaBean) cellList.get(i);
                    String chgstate = (String) cellBean.get("chgstate"); // 1:变化前
                                                                            // 2：变化后
                    String isvar = (String) cellBean.get("isvar");
                    String _fieldName = ((String) cellBean.get("field_name")).toLowerCase();
                    String sub_domain_id = "";
                    if (cellBean.get("sub_domain_id") != null && "1".equals(cellBean.get("chgstate"))) {
                        sub_domain_id = (String) cellBean.get("sub_domain_id");
                        if (sub_domain_id != null && sub_domain_id.length() > 0) {
                            sub_domain_id = "_" + sub_domain_id;
                        } else {
                            sub_domain_id = "";
                        }
                    }
                    if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3)) {

                        if ("1".equals(cellBean.get("subflag"))) {
                            if ("codesetid".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "codeitemdesc".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "corcode".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "parentid".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "start_date".equalsIgnoreCase(cellBean.get("setname").toString().trim())) {
                                if (filedPrivByNode != null && filedPrivByNode.size() > 0 && filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate) != null && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                    String state = "";
                                    String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                    if (editable != null)
                                        state = editable;
                                    if ("0".equals(state))
                                        continue;

                                }
                            } else {
                                if (!this.userview.isSuper_admin() && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                    // 引入节点控制指标权限
                                    String state = "";
                                    String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                    if (editable != null)
                                        state = editable;
                                    if ("0".equals(state)) {
                                        continue;
                                    } else {
                                        if ("".equals(state) && "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim())))
                                            continue;
                                    }

                                }
                            }
                        } else {
                            if ("codesetid".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "codeitemdesc".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "corcode".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "parentid".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "start_date".equalsIgnoreCase(cellBean.get("field_name").toString().trim())) {
                                if (filedPrivByNode != null && filedPrivByNode.size() > 0 && filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate) != null && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                    String state = "";
                                    String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                    if (editable != null)
                                        state = editable;
                                    if ("0".equals(state))
                                        continue;

                                }
                            } else {
                                if (!this.userview.isSuper_admin() && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                    // 引入节点控制指标权限
                                    String state = "";
                                    String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                    if (editable != null)
                                        state = editable;
                                    if ("0".equals(state)) {
                                        continue;
                                    } else {
                                        if ("".equals(state) && "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim())))
                                            continue;
                                    }

                                }
                            }
                        }

                    } else {
                        if ("1".equals(cellBean.get("subflag"))) {
                            if (!this.userview.isSuper_admin() && "0".equalsIgnoreCase(this.userview.analyseTablePriv(cellBean.get("setname").toString().trim())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input()))
                                continue;
                        } else {
                            if (!this.userview.isSuper_admin() && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                // 引入节点控制指标权限

                                String state = "";
                                String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                if (editable != null)
                                    state = editable;
                                if ("0".equals(state)) {
                                    continue;
                                } else {
                                    if ("".equals(state) && "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim())))
                                        continue;
                                }
                            }
                        }
                    }
                    if ("0".equals(isvar))
                        _fieldName += sub_domain_id + "_" + chgstate;

                    if ("1".equals(cellBean.get("subflag").toString().trim())) {
                        map.put(cellBean.get("pageid").toString().trim() + "_" + cellBean.get("gridno").toString().trim(), cellBean);
                    } else {
                        map.put(_fieldName, cellBean);
                    }

                }
                ArrayList _list = new ArrayList();
                for (int i = 0; i < strs.length; i++) {
                    if (strs[i] == null || strs[i].trim().length() == 0)
                        continue;
                    if (map.get(strs[i].toLowerCase()) != null)
                        _list.add((LazyDynaBean) map.get(strs[i].toLowerCase()));
                }
                cellList = _list;
            }

            // 添加一个参数，如果titleflag为false，则列表方式显示的列标题与模板设置的标题一致；否则列表方式显示的列标题为"拟["模板设置的标题"]",liuzy
            // 20151125
            boolean titleflag = false;

            for (int i = 0; i < cellList.size(); i++) {
                cellBean = (LazyDynaBean) cellList.get(i);
                headBean = new LazyDynaBean();
                String chgstate = (String) cellBean.get("chgstate"); // 1:变化前
                                                                        // 2：变化后
                String isvar = (String) cellBean.get("isvar");
                String subflag = (String) cellBean.get("subflag");

                String hismode = (String) cellBean.get("hismode");
                String mode = (String) cellBean.get("mode");
                if (mode == null)
                    mode = "";
                if (hismode == null)
                    hismode = "";

                String sub_domain_id = "";
                if (cellBean.get("sub_domain_id") != null && "1".equals(cellBean.get("chgstate"))) {
                    sub_domain_id = (String) cellBean.get("sub_domain_id");
                    if (sub_domain_id != null && sub_domain_id.length() > 0) {
                        sub_domain_id = "_" + sub_domain_id;
                    } else {
                        sub_domain_id = "";
                    }
                }
                if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3)) {

                    if ("1".equals(cellBean.get("subflag"))) {
                        if ("codesetid".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "codeitemdesc".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "corcode".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "parentid".equalsIgnoreCase(cellBean.get("setname").toString().trim()) || "start_date".equalsIgnoreCase(cellBean.get("setname").toString().trim())) {

                        } else {
                            if (!this.userview.isSuper_admin() && "0".equalsIgnoreCase(this.userview.analyseTablePriv(cellBean.get("setname").toString().trim())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input()))
                                continue;
                        }
                    } else {
                        if ("codesetid".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "codeitemdesc".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "corcode".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "parentid".equalsIgnoreCase(cellBean.get("field_name").toString().trim()) || "start_date".equalsIgnoreCase(cellBean.get("field_name").toString().trim())) {
                            if (filedPrivByNode != null && filedPrivByNode.size() > 0 && filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate) != null && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                String state = "";
                                String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                if (editable != null)
                                    state = editable;
                                if ("0".equals(state))
                                    continue;

                            }
                        } else {
                            if (!this.userview.isSuper_admin() && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                // 引入节点控制指标权限

                                String state = "";
                                String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                                if (editable != null)
                                    state = editable;
                                if ("0".equals(state)) {
                                    continue;
                                } else {
                                    if ("".equals(state) && "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim())))
                                        continue;
                                }

                            }
                        }
                    }

                } else {
                    if ("1".equals(cellBean.get("subflag"))) {
                        if (!this.userview.isSuper_admin() && "0".equalsIgnoreCase(this.userview.analyseTablePriv(cellBean.get("setname").toString().trim())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input()))
                            continue;
                    } else {
                        if (!this.userview.isSuper_admin() && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                            // 引入节点控制指标权限
                            String state = "";
                            String editable = (String) filedPrivByNode.get(cellBean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                            if (editable != null)
                                state = editable;
                            if ("0".equals(state)) {
                                continue;
                            } else {
                                if ("".equals(state) && "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim())))
                                    continue;
                            }

                        } else {

                        }

                    }
                }

                String desc = (String) cellBean.get("hz");
                if (desc != null && desc.trim().length() > 0 && "拟".equalsIgnoreCase(desc.substring(0, 1))) {
                } else {
                    if (titleflag) {
                        desc = "拟[" + (String) cellBean.get("hz") + "]";
                    } else {
                        desc = (String) cellBean.get("hz");
                    }
                }
                if ("2".equals(chgstate) && "0".equals(isvar) && "0".equals(isCompare)) {
                    if ("1".equals(subflag)) {
                        headBean.set("hz", desc.replaceAll("\\{", "").replaceAll("\\}", ""));
                        headBean.set("_hz", desc.replaceAll("\\{", "").replaceAll("\\}", ""));
                    } else {
                        headBean.set("hz", desc);
                        headBean.set("_hz", desc);
                    }
                } else {

                    if ("1".equals(subflag)) {
                        if ("2".equals(chgstate)) {
                            headBean.set("hz", desc.replaceAll("\\{", "").replaceAll("\\}", ""));
                            headBean.set("_hz", desc.replaceAll("\\{", "").replaceAll("\\}", ""));
                        } else
                            headBean.set("hz", ((String) cellBean.get("hz")).replaceAll("\\{", "").replaceAll("\\}", ""));
                    } else
                        headBean.set("hz", (String) cellBean.get("hz"));
                }
                String field_name = (String) cellBean.get("field_name");

                String _fieldName = field_name.toLowerCase();
                if ("0".equals(isvar))
                    _fieldName += sub_domain_id + "_" + chgstate;
                if ("1".equals(subflag)) {
                    if (hiddenItem.length() > 0 && hiddenItem.indexOf("," + cellBean.get("pageid").toString().trim() + "_" + cellBean.get("gridno").toString().trim() + ",") != -1)
                        continue;
                } else {
                    if (hiddenItem.length() > 0 && hiddenItem.indexOf("," + field_name.toLowerCase() + ",") != -1)
                        continue;
                }
                if ("1".equals(subflag)) {
                    if (lockedItemStr.length() > 0 && lockedItemStr.indexOf("," + cellBean.get("pageid").toString().trim() + "_" + cellBean.get("gridno").toString().trim() + ",") != -1)
                        headBean.set("isLock", "1");
                    else
                        headBean.set("isLock", "0");

                } else {
                    if (lockedItemStr.length() > 0 && lockedItemStr.indexOf("," + _fieldName.toLowerCase() + ",") != -1) {
                        headBean.set("isLock", "1");
                    } else {
                        if (lockedItemStr.length() > 0) {
                            headBean.set("isLock", "0");
                        } else {
                            if (fieldSetSortStr.length() <= 0) {
                                if (this.bo != null && this.bo.getInfor_type() == 1 && "1".equals(chgstate) && ("B0110".equalsIgnoreCase(field_name) || "E0122".equalsIgnoreCase(field_name) || "E01A1".equalsIgnoreCase(field_name) || "A0101".equalsIgnoreCase(field_name)))
                                    headBean.set("isLock", "1");
                                else if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && ((this.operationtype == 5 && "2".equals(chgstate)) || (this.operationtype != 5 && "1".equals(chgstate))) && ("parentid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name)))
                                    headBean.set("isLock", "1");
                                else
                                    headBean.set("isLock", "0");
                            } else
                                headBean.set("isLock", "0");
                        }

                    }
                }
                headBean.set("field_name", field_name);
                headBean.set("field_type", (String) cellBean.get("field_type"));
                headBean.set("field_hz", (String) cellBean.get("field_hz"));
                headBean.set("codeid", (String) cellBean.get("codeid"));
                headBean.set("setname", (String) cellBean.get("setname"));
                headBean.set("chgstate", (String) cellBean.get("chgstate"));
                headBean.set("hismode", (String) cellBean.get("hismode"));
                headBean.set("subflag", (String) cellBean.get("subflag"));
                headBean.set("isvar", (String) cellBean.get("isvar"));
                headBean.set("pageid", (String) cellBean.get("pageid"));
                headBean.set("gridno", (String) cellBean.get("gridno"));

                headBean.set("hismode", hismode);
                headBean.set("mode", mode);

                if (cellBean.get("itemlength") != null) {
                    headBean.set("itemlength", (String) cellBean.get("itemlength"));
                }
                headBean.set("formula", cellBean.get("formula") == null ? "" : (String) cellBean.get("formula"));
                headBean.set("disformat", cellBean.get("disformat") == null ? "0" : cellBean.get("disformat"));
                headBean.set("sub_domain", cellBean.get("sub_domain") == null ? "" : (String) cellBean.get("sub_domain"));
                headBean.set("sub_domain_id", cellBean.get("sub_domain_id") == null ? "" : (String) cellBean.get("sub_domain_id"));
                if ("0".equals(cellBean.get("subflag"))) {
                    if ("1".equals(cellBean.get("chgstate")))
                        preMap.put(field_name.toLowerCase(), headBean);
                    else
                        endMap.put(field_name.toLowerCase(), headBean);
                }
                list.add(headBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList list2 = list;
        ArrayList list3 = new ArrayList();
        if ("1".equals(isCompare)) {
            String temp = ",";

            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean tempbean = (LazyDynaBean) list.get(i);
                if (tempbean.get("isvar") != null && !"0".equals(tempbean.get("isvar")))
                    continue;
                if ("0".equals(tempbean.get("subflag")) && endMap.get(tempbean.get("field_name").toString().toLowerCase()) != null && preMap.get(tempbean.get("field_name").toString().toLowerCase()) != null) {
                    tempbean = (LazyDynaBean) endMap.get(tempbean.get("field_name").toString().toLowerCase());
                    if (temp.indexOf("," + tempbean.get("field_name").toString().toLowerCase() + ",") == -1) {
                        temp += tempbean.get("field_name").toString().toLowerCase() + ",";
                        list3.add(tempbean);
                        continue;
                    } else {
                        continue;
                    }
                }
                list3.add(tempbean);
            }
            list2 = list3;

            // caseStr+= ",B0110_1,E0122_1,E01A1_1,A0101,";
            LazyDynaBean tempbean;

            if (this.operationtype != 0) {

                if (endMap.get("b0110") != null) {
                    list2.remove(endMap.get("b0110"));
                    tempbean = (LazyDynaBean) endMap.get("b0110");
                    if (preMap.get("b0110") != null && "1".equals(((LazyDynaBean) preMap.get("b0110")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(0, tempbean);
                }
                if (endMap.get("e0122") != null) {
                    list2.remove(endMap.get("e0122"));
                    tempbean = (LazyDynaBean) endMap.get("e0122");
                    if (preMap.get("e0122") != null && "1".equals(((LazyDynaBean) preMap.get("e0122")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(1, (LazyDynaBean) endMap.get("e0122"));
                }
                if (endMap.get("e01a1") != null) {
                    list2.remove(endMap.get("e01a1"));
                    tempbean = (LazyDynaBean) endMap.get("e01a1");
                    if (preMap.get("e01a1") != null && "1".equals(((LazyDynaBean) preMap.get("e01a1")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(2, (LazyDynaBean) endMap.get("e01a1"));
                }
                if (endMap.get("a0101") != null) {
                    list2.remove(endMap.get("a0101"));
                    tempbean = (LazyDynaBean) endMap.get("a0101");
                    if (preMap.get("a0101") != null && "1".equals(((LazyDynaBean) preMap.get("a0101")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(3, (LazyDynaBean) endMap.get("a0101"));
                }
            } else {
                if (endMap.get("a0101") != null) {
                    list2.remove(endMap.get("a0101"));
                    tempbean = (LazyDynaBean) endMap.get("a0101");
                    if (preMap.get("a0101") != null && "1".equals(((LazyDynaBean) preMap.get("a0101")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(0, (LazyDynaBean) endMap.get("a0101"));
                }
            }
        }
        return list2;
    }

    /**
     * 判断表头是否包含姓名列
     * 
     * @return
     */
    public String validateIsName(ArrayList tableHeadSetList) {
        String isName = "0";
        LazyDynaBean cellBean = null;
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            cellBean = (LazyDynaBean) tableHeadSetList.get(i);
            String field_name = (String) cellBean.get("field_name");
            String chgstate = (String) cellBean.get("chgstate");
            if (this.operationtype != 0) {
                if ("a0101".equalsIgnoreCase(field_name) && "1".equals(chgstate)) {
                    isName = "1";
                    break;

                }
            } else {
                if ("a0101".equalsIgnoreCase(field_name) && "2".equals(chgstate)) {
                    isName = "1";
                    break;

                }
            }

        }
        return isName;
    }

    public String getAffixfileTableHtml(String task_id) {
        StringBuffer html = new StringBuffer("");
        int total_width = 0;
        try {
            html.append("<table border='0' valign='top' align='left' bgColor='#FFFFFF' style='margin-top:-1' width='100%' cellspacing='0' cellpadding='0'>\n");
            html.append("<tr>\n");
            html.append("<td  align='center' class='head_left common_background_color common_border_color' width='40%' >" + ResourceFactory.getProperty("column.name") + "</td>\n");
            html.append("<td  align='center' class='head_middle_right common_background_color common_border_color' width='15%' >" + ResourceFactory.getProperty("conlumn.resource_list.name") + ResourceFactory.getProperty("label.org.type_org") + "</td>\n");
            html.append("<td  align='center' class='head_middle_right common_background_color common_border_color' width='15%' >" + ResourceFactory.getProperty("lable.hiremanage.staff_id") + "</td>\n");
            html.append("<td  align='center' class='head_middle_right common_background_color common_border_color' width='15%' >" + ResourceFactory.getProperty("lable.hiremanage.create_date") + "</td>\n");
            html.append("<td  align='center' class='head_middle_right common_background_color common_border_color' width='15%' >" + ResourceFactory.getProperty("reportcyclelist.option") + "</td>\n");
            html.append("</tr>\n");
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rowSet = dao.search("select * from t_wf_file where ins_id=(select ins_id from t_wf_task where task_id=" + task_id + ") order by create_time");
            String desc = ResourceFactory.getProperty("label.view");
            while (rowSet.next()) {
                String file_id = rowSet.getString("file_id");
                String name = rowSet.getString("name");
                String ext = rowSet.getString("ext");
                String ins_id = rowSet.getString("ins_id");
                Date d_create = rowSet.getDate("create_time");
                String d_str = DateUtils.format(d_create, "yyyy.MM.dd");
                String create_time = d_str;
                String create_user = rowSet.getString("create_user");
                String typedesc = "";
                String attachmenttype = rowSet.getString("attachmenttype");
                if ("1".equals(attachmenttype)) {
                    typedesc = "单据对象关联文件";
                } else {
                    typedesc = "变动调整说明文件";
                }
                html.append("<tr>\n<td align='left' class='record_left' nowrap>&nbsp;" + name + "</td>\n");
                html.append("<td align='center' class='record_middle_right' nowrap>&nbsp;" + typedesc + "</td>\n");
                html.append("<td align='center' class='record_middle_right' nowrap>&nbsp;" + create_user + "</td>\n");
                html.append("<td align='center' class='record_middle_right' nowrap>&nbsp;" + create_time + "</td>\n");
                html.append("<td align='center' class='record_middle_right' nowrap>&nbsp;");
                html.append("<a href='/servlet/AffixDownLoad?ext_file_id=" + PubFunc.encrypt(file_id) + "&modeflag=2' target=_blank>" + desc + "</a></td>\n</tr>\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        html.append("</table>\n");
        return html.toString();
    }

    /**
     * 获得模板记录子集信息 <?xml version="1.0" encoding="GB2312"?> <records
     * columns="a0415`a0430`a0435`a0410`a0405`a0440"> <record
     * I9999="1">`2009-03-01`法国勒阿弗尔大学`电力系统及自动化`11`</record></records>
     * 
     * @return
     */
    public String getSubSetTableHtml(String table_name, String a0100, String basepre, String isAppealTable, String seqnum, String columnName, String sub_domain) {
        StringBuffer html = new StringBuffer("");
        int total_width = 0;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select " + columnName + " from " + table_name + " where a0100='" + a0100 + "' and lower(basepre)='" + basepre.toLowerCase() + "'  ";
            if (this.bo.getInfor_type() == 2) {
                sql = "select " + columnName + " from " + table_name + " where b0110='" + a0100 + "'  ";
            } else if (this.bo.getInfor_type() == 3) {
                sql = "select " + columnName + " from " + table_name + " where e01a1='" + a0100 + "'  ";
            }
            if ("1".equals(isAppealTable))
                sql += " and seqnum='" + seqnum + "'";
            if ("22".equals(isAppealTable))// 历史归档数据
                sql += " and id='" + seqnum + "'";
            sub_domain = SafeCode.decode(sub_domain);
            // ///////////////////////

            String data = sub_domain;
            Document doc2 = null;
            Element element2 = null;
            HashMap hmap = new HashMap();
            HashMap itemTitleMap = new HashMap();

            if (sub_domain != null && sub_domain.length() > 0) {
                doc2 = PubFunc.generateDom(sub_domain);;
                String xpath = "/sub_para/field";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist = findPath.selectNodes(doc2);
                if (childlist != null && childlist.size() > 0) {
                    for (int i = 0; i < childlist.size(); i++) {
                        element2 = (Element) childlist.get(i);
                        if (element2 != null && element2.getAttributeValue("name") != null) {
                            FieldItem item = DataDictionary.getFieldItem(element2.getAttributeValue("name"));
                            if (item != null) {
                                itemTitleMap.put(element2.getAttributeValue("name").toLowerCase(), element2.getAttributeValue("title"));
                                if (element2.getAttributeValue("slop") != null && !"".equals(element2.getAttributeValue("slop"))) {
                                    if ("D".equalsIgnoreCase(item.getItemtype())) {
                                        hmap.put(element2.getAttributeValue("name").toLowerCase(), element2.getAttributeValue("slop"));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // //////////////////////////
            FieldItem item = null;
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                String str = Sql_switcher.readMemo(rowSet, columnName);
                if (str.trim().length() > 0) {
                    Document doc = null;
                    Element element = null;
                    doc = PubFunc.generateDom(str);
                    String xpath = "/records";

                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    List childlist = findPath.selectNodes(doc);
                    String columns = "";
                    if (childlist != null && childlist.size() > 0) {
                        element = (Element) childlist.get(0);
                        columns = element.getAttributeValue("columns");
                    }

                    String[] temps = columns.split("`");
                    html.append("<tr>");

                    html.append("<td  align='center'   class='TableRow' width=50  >&nbsp;序号&nbsp;</td>");
                    total_width += 50;
                    for (int i = 0; i < temps.length; i++) {
                        if (temps[i].trim().length() == 0)
                            continue;
                        item = DataDictionary.getFieldItem(temps[i]);
                        if (item == null)
                            continue;
                        String itemtype = item.getItemtype();
                        int width = 150;
                        if ("M".equals(itemtype))
                            width = 200;
                        else if ("D".equals(itemtype))
                            width = 100;
                        total_width += width;
                        String itemTitle = (String) itemTitleMap.get(item.getItemid().toLowerCase());
                        if (itemTitle == null || itemTitle.length() < 1) {
                            itemTitle = item.getItemdesc();
                        }
                        html.append("<td  align='center'   class='TableRow' width=" + width + "  >&nbsp;&nbsp;" + itemTitle + "&nbsp;&nbsp;</td>");
                    }
                    html.append("</tr>");

                    xpath = "/records/record";
                    findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    childlist = findPath.selectNodes(doc);
                    if (childlist != null && childlist.size() > 0) {
                        int m = 0;
                        for (int i = 0; i < childlist.size(); i++) {
                            element = (Element) childlist.get(i);
                            String state = element.getAttributeValue("state");
                            if (state != null && "D".equalsIgnoreCase(state))
                                continue;
                            html.append("<tr>");
                            String context = element.getText();
                            String[] _temps = context.split("`");
                            html.append("<td class='RecordRow' align='center'>" + (m + 1) + "</td>");
                            m++;
                            for (int j = 0; j < temps.length; j++) {
                                if (temps[j].trim().length() == 0)
                                    continue;
                                item = DataDictionary.getFieldItem(temps[j]);
                                if (item == null)
                                    continue;
                                String codesetid = item.getCodesetid();
                                String itemtype = item.getItemtype();
                                String itemid = item.getItemid();
                                html.append("<td  class='RecordRow' ");
                                if ("N".equalsIgnoreCase(itemtype))
                                    html.append(" align='right'    ");
                                else {
                                    html.append(" align='left' ");
                                }
                                html.append("     > ");
                                if (j < _temps.length) {
                                    String value = _temps[j];
                                    if ("A".equalsIgnoreCase(itemtype) && !"0".equals(codesetid) && value.length() > 0) {
                                        html.append(AdminCode.getCodeName(codesetid, value));
                                    } else if ("D".equalsIgnoreCase(itemtype) && hmap.get(item.getItemid().toLowerCase()) != null) {
                                        value = value.replace(".", "-");
                                        if (value != null && value.length() > 9) {
                                            html.append(formatDateFiledsetValue(value, "", Integer.parseInt(hmap.get(item.getItemid().toLowerCase()).toString())));
                                        } else {
                                            html.append(value);
                                        }
                                    } else
                                        html.append(value);
                                } else {
                                    html.append("&nbsp;");
                                }
                                html.append("</td>");
                            }
                            html.append("</tr>");
                        }

                    } else {
                        html.append("<tr>");
                        html.append("<td  class='RecordRow' >&nbsp;</td>");
                        for (int j = 0; j < temps.length; j++) {
                            if (temps[j].trim().length() == 0)
                                continue;
                            html.append("<td  class='RecordRow' >&nbsp;</td>");
                        }
                        html.append("</tr>");
                    }

                } else {
                    return getEmptySubSetHtml(columnName, tabid);
                }
            }
            html.append("</table>");
            if (rowSet != null)
                rowSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = "<table border='0'  valign='top' align='left'  bgColor='#FFFFFF'  style='margin-top:-1'  width='" + total_width + "'  cellspacing='0'   ' cellpadding='0'   class='ListTable'  >";
        return s + html.toString();
    }

    /**
     * 获取子集空列表
     * 
     * @param columnName
     * @param tabid
     * @return
     */
    public String getEmptySubSetHtml(String columnName, String tabid) {
        StringBuffer html = new StringBuffer("");
        int total_width = 0;
        try {
            String[] temps = columnName.split("_");
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select  sub_domain  from template_set where tabid=" + tabid + " and subflag=1 and chgstate=" + temps[2] + " and lower(setname)='" + temps[1].toLowerCase() + "'";
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                String content = Sql_switcher.readMemo(rowSet, "sub_domain");
                TSubSetDomain setDomain = new TSubSetDomain(content);
                String fields = setDomain.getFields();
                temps = fields.split("`");
                html.append("<tr>");
                html.append("<td  align='center'    class='TableRow'  width=50  >&nbsp;序号&nbsp;</td>");
                total_width += 50;
                FieldItem item = null;
                StringBuffer rowHtml = new StringBuffer("<tr><td  class='RecordRow'   >&nbsp;</td>");
                for (int i = 0; i < temps.length; i++) {
                    if (temps[i].trim().length() == 0)
                        continue;
                    item = DataDictionary.getFieldItem(temps[i]);
                    String itemtype = item.getItemtype();
                    int width = 150;
                    if ("M".equals(itemtype))
                        width = 200;
                    else if ("D".equals(itemtype))
                        width = 100;
                    total_width += width;
                    html.append("<td  align='center'   class='TableRow' width=" + width + "  >&nbsp;&nbsp;" + item.getItemdesc() + "&nbsp;&nbsp;</td>");
                    rowHtml.append("<td  class='RecordRow' >&nbsp;</td>");
                }
                html.append("</tr>");
                rowHtml.append("</tr>");
                html.append(rowHtml.toString());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = "<table border='0'  valign='top' align='left'  bgColor='#FFFFFF'  style='margin-top:-1'  width='" + total_width + "'  cellspacing='0'   ' cellpadding='0'   class='ListTable'  >";
        return s + html.toString();

    }

    public String getCondition(ArrayList headSetList, ArrayList tasklist, String _codeid) {
        StringBuffer sql = new StringBuffer();
        // sql.append("select * from templet_"+this.tabid+" where 1=1 ");
        if (tasklist != null && tasklist.size() > 0) {
            StringBuffer strins = new StringBuffer();
            for (int i = 0; i < tasklist.size(); i++)// 按任务号查询需要审批的对象20080418
            {
                if (i != 0)
                    strins.append(",");
                strins.append((String) tasklist.get(i));
            }
            sql.append(" and ( task_id in(");
            sql.append(strins.toString());
            sql.append(")");
            // 角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，
            sql.append(" or exists (select null from t_wf_task_objlink where templet_" + this.tabid + ".seqnum=t_wf_task_objlink.seqnum and templet_" + this.tabid + ".ins_id=t_wf_task_objlink.ins_id ");
            sql.append("  and task_id in (" + strins.toString() + ") and state=0 and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ) ");

        }
        String tempname = "";
        if (this.bo.getInfor_type() == 2) {
            tempname = "b0110";
            if (this.operationtype == 5) {
                DbWizard dbwizard = new DbWizard(this.conn);
                if (tasklist == null || tasklist.size() == 0) {
                    if (dbwizard.isExistField(this.userview.getUserName() + "templet_" + this.tabid, "parentid_2", false)) {
                        tempname = "parentid_2";
                    }
                } else {
                    if (dbwizard.isExistField("templet_" + this.tabid, "parentid_2", false)) {
                        tempname = "parentid_2";
                    }
                }
            }
        }
        if (this.bo.getInfor_type() == 3) {
            tempname = "e01a1";
            if (this.operationtype == 5) {
                DbWizard dbwizard = new DbWizard(this.conn);
                if (tasklist == null || tasklist.size() == 0) {
                    if (dbwizard.isExistField(this.userview.getUserName() + "templet_" + this.tabid, "parentid_2", false)) {
                        tempname = "parentid_2";
                    }
                } else {
                    if (dbwizard.isExistField("templet_" + this.tabid, "parentid_2", false)) {
                        tempname = "parentid_2";
                    }
                }
            }
        }
        if (_codeid != null && _codeid.trim().length() > 2) {
            String value = _codeid.substring(2);
            if (this.operationtype != 0) {
                if (this.bo.getInfor_type() == 1) {
                    if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and b0110_1 like '" + value + "%'");
                    } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and e0122_1 like '" + value + "%'");
                    } else if ("@K".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and e01a1_1 like '" + value + "%'");
                    }
                } else if (this.bo.getInfor_type() == 2) {
                    if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and " + tempname + " like '" + value + "%'");
                    } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and " + tempname + " like '" + value + "%'");
                    }
                } else if (this.bo.getInfor_type() == 3) {
                    if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and " + tempname + " like '" + value + "%'");
                    } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                        sql.append(" and " + tempname + " like '" + value + "%'");
                    }

                }
            } else {
                for (int i = 0; i < headSetList.size(); i++) {
                    LazyDynaBean abean = (LazyDynaBean) headSetList.get(i);
                    if (this.bo.getInfor_type() == 1) {
                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2)) && "b0110".equalsIgnoreCase(abean.get("field_name").toString())) {
                            sql.append(" and b0110_2 like '" + value + "%'");
                            break;
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2)) && "e0122".equalsIgnoreCase(abean.get("field_name").toString())) {
                            sql.append(" and e0122_2 like '" + value + "%'");
                            break;
                        } else if ("@K".equalsIgnoreCase(_codeid.substring(0, 2)) && "e01a1".equalsIgnoreCase(abean.get("field_name").toString())) {
                            sql.append(" and e01a1_2 like '" + value + "%'");
                            break;
                        }
                    } else if (this.bo.getInfor_type() == 2) {
                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                            break;
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                            break;
                        }
                    } else if (this.bo.getInfor_type() == 3) {

                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                            break;
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                            break;
                        }

                    }
                }

            }
        }
        return sql.toString();
    }

    /**
     * 取得列表数据
     * 
     * @param headSetList
     * @param orderStr
     * @param filterStr
     * @param isCompare
     * @return
     */
    private String hasRecordFromMessage = "0"; // 数据中是否有来自消息的记录
    public ArrayList getTableData(ArrayList headSetList, String orderStr, String filterStr, String isCompare, ArrayList tasklist, String _codeid, HashMap prechangemap) {
        ArrayList list = new ArrayList();
        HashMap endMap = new HashMap();
        HashMap preMap = new HashMap();
        HashMap submitMap = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rowSet = null;
            StringBuffer sql = new StringBuffer();
            sql.append("select * from templet_" + this.tabid + " where 1=1 ");
            if (tasklist != null && tasklist.size() > 0) {
                StringBuffer strins = new StringBuffer();
                for (int i = 0; i < tasklist.size(); i++)// 按任务号查询需要审批的对象20080418
                {
                    if (i != 0)
                        strins.append(",");
                    strins.append((String) tasklist.get(i));
                }
                sql.append(" and   exists (select null from t_wf_task_objlink where templet_" + this.tabid + ".seqnum=t_wf_task_objlink.seqnum and templet_" + this.tabid + ".ins_id=t_wf_task_objlink.ins_id ");
                sql.append("  and task_id in (" + strins.toString() + ") and tab_id=" + this.tabid + " and ( state=0 or  state is null ) and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) ) )  ");

                String _sql = " select submitflag,seqnum,task_id from t_wf_task_objlink where  task_id in (" + strins.toString() + ") and tab_id=" + this.tabid + " and ( state=0 or  state is null ) and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) ) ";
                rowSet = dao.search(_sql);
                while (rowSet.next()) {
                    String submitflag = "0";
                    if (rowSet.getString("submitflag") != null && "1".equals(rowSet.getString("submitflag")))
                        submitflag = "1";
                    submitMap.put(rowSet.getString("seqnum"), rowSet.getString("task_id") + "`" + submitflag);
                }

            }
            String tempname = "";
            if (this.bo.getInfor_type() == 2) {
                tempname = "b0110";
                if (this.operationtype == 5) {
                    DbWizard dbwizard = new DbWizard(this.conn);
                    if (tasklist == null || tasklist.size() == 0) {
                        if (dbwizard.isExistField(this.userview.getUserName() + "templet_" + this.tabid, "parentid_2", false)) {
                            tempname = "parentid_2";
                        }
                    } else {
                        if (dbwizard.isExistField("templet_" + this.tabid, "parentid_2", false)) {
                            tempname = "parentid_2";
                        }
                    }
                }
            }
            if (this.bo.getInfor_type() == 3) {
                tempname = "e01a1";
                if (this.operationtype == 5) {
                    DbWizard dbwizard = new DbWizard(this.conn);
                    if (tasklist == null || tasklist.size() == 0) {
                        if (dbwizard.isExistField(this.userview.getUserName() + "templet_" + this.tabid, "parentid_2", false)) {
                            tempname = "parentid_2";
                        }
                    } else {
                        if (dbwizard.isExistField("templet_" + this.tabid, "parentid_2", false)) {
                            tempname = "parentid_2";
                        }
                    }
                }
            }
            if (_codeid != null && _codeid.trim().length() > 2) {
                String value = _codeid.substring(2);
                if (this.operationtype != 0) {
                    if (this.bo.getInfor_type() == 1) {
                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and b0110_1 like '" + value + "%'");
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and e0122_1 like '" + value + "%'");
                        } else if ("@K".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and e01a1_1 like '" + value + "%'");
                        }
                    } else if (this.bo.getInfor_type() == 2) {
                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                        }
                    } else if (this.bo.getInfor_type() == 3) {
                        if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                        } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                            sql.append(" and " + tempname + " like '" + value + "%'");
                        }

                    }
                } else {
                    for (int i = 0; i < headSetList.size(); i++) {
                        LazyDynaBean abean = (LazyDynaBean) headSetList.get(i);
                        if (this.bo.getInfor_type() == 1) {
                            if ("UN".equalsIgnoreCase(_codeid.substring(0, 2)) && "b0110".equalsIgnoreCase(abean.get("field_name").toString())) {
                                sql.append(" and b0110_2 like '" + value + "%'");
                                break;
                            } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2)) && "e0122".equalsIgnoreCase(abean.get("field_name").toString())) {
                                sql.append(" and e0122_2 like '" + value + "%'");
                                break;
                            } else if ("@K".equalsIgnoreCase(_codeid.substring(0, 2)) && "e01a1".equalsIgnoreCase(abean.get("field_name").toString())) {
                                sql.append(" and e01a1_2 like '" + value + "%'");
                                break;
                            }
                        } else if (this.bo.getInfor_type() == 2) {
                            if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                                sql.append(" and " + tempname + " like '" + value + "%'");
                                break;
                            } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                                sql.append(" and " + tempname + " like '" + value + "%'");
                                break;
                            }
                        } else if (this.bo.getInfor_type() == 3) {

                            if ("UN".equalsIgnoreCase(_codeid.substring(0, 2))) {
                                sql.append(" and " + tempname + " like '" + value + "%'");
                                break;
                            } else if ("UM".equalsIgnoreCase(_codeid.substring(0, 2))) {
                                sql.append(" and " + tempname + " like '" + value + "%'");
                                break;
                            }

                        }
                    }

                }
            }
            String strsql = sql.toString();
            if (tasklist == null || tasklist.size() == 0)
                strsql = strsql.replaceAll("templet_" + tabid, this.userview.getUserName() + "templet_" + tabid);
            filterStr = PubFunc.keyWord_reback(filterStr);
            if (filterStr.length() > 0)
                filterStr = " and " + filterStr;
            strsql += filterStr + " ";
            // sql.append(filterStr);
            if (orderStr.length() == 0) {
                if ((this.bo.getInfor_type() == 3 && this.operationtype == 8) || ((this.bo.getInfor_type() == 2) && (this.operationtype == 8 || this.operationtype == 9))) {
                    String key = "b0110";
                    if (this.bo.getInfor_type() == 3)
                        key = "e01a1";
                    strsql += "  order by " + Sql_switcher.isnull("to_id", "'bb0000000'") + ",case when " + key + "=to_id then 100000000 else a0000 end asc ";
                } else
                    strsql += " order by a0000";
            } else
                strsql += orderStr;
            this.hmuster_sql = strsql;

            HashMap compareDataMap = new HashMap();
            if ("1".equals(isCompare)) // 对照
                compareDataMap = getBeforeChgState(strsql, tasklist, headSetList);

            rowSet = dao.search(strsql);
            // System.out.println("strsql"+strsql);
            ResultSetMetaData mt = rowSet.getMetaData();
            HashMap existTableColumnMap = new HashMap();
            HashMap orderMap = new HashMap();
            int a = 1;
            for (int i = 1; i <= mt.getColumnCount(); i++)
                existTableColumnMap.put(mt.getColumnName(i).toUpperCase(), "1");

            LazyDynaBean _abean = new LazyDynaBean();// 存放将要显示的数据
            LazyDynaBean _abean2 = new LazyDynaBean();
            LazyDynaBean before_bean = new LazyDynaBean();
            LazyDynaBean abean = null;// 存放headSetList中的数据
            int num = 0;
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122))
                display_e0122 = "0";
            while (rowSet.next())// 从
                                    // (this.userview.getUserName()+"templet_"+tabid)
                                    // 中查询数据
            {
                num++;
                String state = rowSet.getString("state") != null ? rowSet.getString("state") : "0"; // 是否来自消息的记录
                if ("1".equals(state))
                    hasRecordFromMessage = "1";
                String a0100 = "";
                if (this.bo != null && this.bo.getInfor_type() == 1) {
                    a0100 = rowSet.getString("a0100");
                } else if (this.bo != null && this.bo.getInfor_type() == 2) {
                    a0100 = rowSet.getString("b0110");
                } else if (this.bo != null && this.bo.getInfor_type() == 3) {
                    a0100 = rowSet.getString("e01a1");
                } else {
                    a0100 = rowSet.getString("a0100");
                }
                String basepre = "";
                _abean = new LazyDynaBean();
                if ((this.bo.getInfor_type() == 3 && this.operationtype == 8) || ((this.bo.getInfor_type() == 2) && (this.operationtype == 8 || this.operationtype == 9))) {
                    if (existTableColumnMap.get("TO_ID") != null) {
                        if (rowSet.getString("TO_ID") != null && !"".equals(rowSet.getString("TO_ID"))) {
                            if (this.bo.getInfor_type() == 2 && rowSet.getString("b0110").equals(rowSet.getString("TO_ID"))) {
                                _abean.set("color", "1");
                            }
                            if (this.bo.getInfor_type() == 3 && rowSet.getString("e01a1").equals(rowSet.getString("TO_ID"))) {
                                _abean.set("color", "1");
                            }
                            if (orderMap.get(rowSet.getString("TO_ID")) == null) {
                                orderMap.put(rowSet.getString("TO_ID"), "" + a);
                                _abean.set("num", "" + a);
                                a++;
                            } else {
                                _abean.set("num", orderMap.get(rowSet.getString("TO_ID")));
                            }
                        } else {
                            _abean.set("num", " ");
                        }
                    } else
                        _abean.set("num", " ");
                } else
                    _abean.set("num", String.valueOf(num));
                _abean.set("a0100", a0100);
                if (this.bo != null && this.bo.getInfor_type() == 1) {
                    basepre = rowSet.getString("basepre");
                    _abean.set("basepre", basepre);
                }
                _abean.set("chg", "2");

                _abean.set("submitflag", rowSet.getString("submitflag") != null ? rowSet.getString("submitflag") : "0");

                for (int i = 0; i < headSetList.size(); i++) {
                    abean = (LazyDynaBean) headSetList.get(i);
                    String field_name = (String) abean.get("field_name");
                    String field_type = (String) abean.get("field_type");
                    String isvar = (String) abean.get("isvar");
                    String subflag = (String) abean.get("subflag");
                    String codeid = (String) abean.get("codeid");// codesetid
                    if ("orgType".equalsIgnoreCase(codeid)) {// 郭峰
                                                                // 如果codeid不为空，就会根据AdminCode.getCodeName来取值，就取不到值了。
                        codeid = "";
                    }
                    String chgstate = (String) abean.get("chgstate");// 1:变化前
                                                                        // 2：变化后

                    String hismode = "";
                    if (abean.get("hismode") != null)
                        hismode = (String) abean.get("hismode");
                    String mode = "";
                    if (abean.get("mode") != null)
                        mode = (String) abean.get("mode");

                    int disformat = Integer.parseInt(abean.get("disformat") == null ? "0" : (String) abean.get("disformat"));
                    String formula = abean.get("formula") == null ? "" : "" + abean.get("formula");
                    String sub_domain_id = "";
                    if (abean.get("sub_domain_id") != null && "1".equals(abean.get("chgstate"))) {
                        sub_domain_id = (String) abean.get("sub_domain_id");
                        if (sub_domain_id != null && sub_domain_id.length() > 0) {
                            sub_domain_id = "_" + sub_domain_id;
                        } else {
                            sub_domain_id = "";
                        }
                    }
                    if ("0".equals(isvar)) {
                        field_name = field_name + sub_domain_id + "_" + chgstate;
                    }

                    if ("1".equals(isvar) && existTableColumnMap.get(field_name.toUpperCase()) == null) {
                        _abean.set(field_name, "");
                    } else if ("1".equals(subflag)) {
                        _abean.set(field_name, "......");
                    } else {
                        if ("M".equalsIgnoreCase(field_type)) {

                            // 判断数据字典里的指标类型
                            FieldItem item = DataDictionary.getFieldItem("" + abean.get("field_name"));
                            if (item != null && item.getItemtype() != null) {

                                if ("M".equalsIgnoreCase(item.getItemtype())) {
                                    _abean.set(field_name, SafeCode.encode(Sql_switcher.readMemo(rowSet, field_name).replace("\r\n", "<br>").replace("`", "<br>")));
                                } else if ("D".equalsIgnoreCase(item.getItemtype())) {
                                    /** yyyy-MM-dd */
                                    String str = Sql_switcher.readMemo(rowSet, field_name);
                                    String values = "";
                                    if (str.indexOf("`") != -1) {
                                        String[] strs = str.split("`");
                                        for (int j = 0; j < strs.length; j++) {
                                            if (strs[j].trim().length() > 0) {
                                                values += formatDateValue(strs[j], formula, disformat);
                                                if (j < strs.length - 1) {
                                                    values += "`";
                                                }
                                            }
                                        }
                                    } else {
                                        values = formatDateValue(str, formula, disformat);
                                    }
                                    _abean.set(field_name, SafeCode.encode(values.replace("\r\n", "<br>").replace("`", "<br>")));
                                } else if ("N".equalsIgnoreCase(item.getItemtype())) {
                                    int ndec = disformat;// 小数点位数
                                    String prefix = ((formula == null) ? "" : formula);
                                    String str = Sql_switcher.readMemo(rowSet, field_name);
                                    String values = "";
                                    if (str.indexOf("`") != -1) {
                                        String[] strs = str.split("`");
                                        for (int j = 0; j < strs.length; j++) {
                                            if (strs[j].trim().length() > 0) {
                                                values += prefix + PubFunc.DoFormatDecimal(strs[j], ndec);
                                                if (j < strs.length - 1) {
                                                    values += "`";
                                                }
                                            }
                                        }
                                    } else {
                                        values = prefix + PubFunc.DoFormatDecimal(str, ndec);
                                    }
                                    _abean.set(field_name, SafeCode.encode(values.replace("\r\n", "<br>").replace("`", "<br>")));

                                } else {
                                    // if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
                                    String str = Sql_switcher.readMemo(rowSet, field_name);
                                    String values = "";
                                    if (str.indexOf("`") != -1) {
                                        String[] strs = str.split("`");
                                        for (int j = 0; j < strs.length; j++) {
                                            if (strs[j].trim().length() > 0) {
                                                if (codeid != null && !"0".equals(codeid))
                                                    values += AdminCode.getCodeName(codeid, strs[j]);
                                                else
                                                    values += strs[j];
                                                if (j < strs.length - 1) {
                                                    values += "`";
                                                }
                                            }
                                        }
                                    } else {
                                        if (codeid != null && !"0".equals(codeid))
                                            values = AdminCode.getCodeName(codeid, str);
                                        else
                                            values = str;
                                    }
                                    _abean.set(field_name, SafeCode.encode(values.replace("\r\n", "<br>").replace("`", "<br>")));
                                    // }else{
                                    // list.add(Sql_switcher.readMemo(rset,field_name));
                                    // }
                                }

                                // 取历史数据 最近第 、最初第
                                if (hismode != null && "4".equals(hismode) && mode != null && ("0".equals(mode) || "2".equals(mode))) {
                                    if (_abean.get(field_name) != null) {
                                        String _value = (String) _abean.get(field_name);
                                        _abean.set(field_name, SafeCode.decode(_value));
                                    }
                                }

                            }

                            //						
                            // if(sub_domain_id!=null&&sub_domain_id.length()>0){
                            // String str =
                            // Sql_switcher.readMemo(rowSet,field_name);
                            // String values ="";
                            // if(str.indexOf("`")!=-1){
                            // String strs[] =str.split("`");
                            // for(int n=0;n<strs.length;n++){
                            // if(strs[n].trim().length()>0){
                            // values += AdminCode.getCodeName(codeid,strs[n]);
                            // if(n<strs.length-1){
                            // values+="`";
                            // }
                            // }
                            // }
                            // }else{
                            // values = AdminCode.getCodeName(codeid,str);
                            // }
                            // _abean.set(field_name,SafeCode.encode(values.replace("\r\n","<br>").replace("`","<br>")));
                            // }
                            // else
                            // _abean.set(field_name,SafeCode.encode(Sql_switcher.readMemo(rowSet,field_name).replace("\r\n","<br>")));
                        }// 如果是备注型
                        else if ("D".equalsIgnoreCase(field_type)) {
                            if (rowSet.getDate(field_name) != null)
                                _abean.set(field_name, formatDateValue(PubFunc.FormatDate(rowSet.getDate(field_name)), formula, disformat));
                            else
                                _abean.set(field_name, "");
                        }// 如果是日期型
                        else// 如果是字符型或代码型
                        {

                            if (rowSet.getString(field_name) == null)// 如果字段名称是空
                            {
                                _abean.set(field_name, "");
                            } else// 如果字段名称不是空
                            {
                                if ("A".equalsIgnoreCase(field_type) && codeid.length() > 0 && !"0".equals(codeid))// 如果是代码型
                                {
                                    if ("UM".equals(codeid.toUpperCase())) {

                                        if ("".equals(AdminCode.getCodeName(codeid, rowSet.getString(field_name))))
                                            codeid = "UN";
                                    }
                                    if ("1".equals(isvar) && "A".equalsIgnoreCase(field_type) && "".equals(codeid)) {// 如果是临时变量
                                        _abean.set(field_name, rowSet.getString(field_name));
                                    } else {// 如果不是临时变量
                                        if ("UM".equals(codeid.toUpperCase())) {// 如果是部门
                                            String value = "";
                                            if (Integer.parseInt(display_e0122) == 0) {
                                                value = AdminCode.getCodeName("UM", rowSet.getString(field_name));
                                            } else {
                                                CodeItem item = AdminCode.getCode("UM", rowSet.getString(field_name), Integer.parseInt(display_e0122));
                                                if (item != null) {
                                                    value = item.getCodename();
                                                } else {
                                                    value = AdminCode.getCodeName("UM", rowSet.getString(field_name));
                                                }

                                            }
                                            _abean.set(field_name, value);
                                        } else {// 如果不是部门
                                            _abean.set(field_name, AdminCode.getCodeName(codeid, rowSet.getString(field_name)));
                                        }
                                    }
                                } // 如果是代码型
                                else {// 如果不是代码型（特地处理组织单元类型）
                                    if (field_name.lastIndexOf("_") != -1 && "codesetid".equalsIgnoreCase(field_name.substring(0, field_name.lastIndexOf("_")))) {
                                        String name = "";
                                        if ("UN".equalsIgnoreCase(rowSet.getString(field_name))) {
                                            name = "单位";
                                        } else if ("UM".equalsIgnoreCase(rowSet.getString(field_name))) {
                                            name = "部门";
                                        } else if ("@K".equalsIgnoreCase(rowSet.getString(field_name))) {
                                            name = "职位";
                                        }
                                        _abean.set(field_name, name);
                                    } else
                                        _abean.set(field_name, rowSet.getString(field_name).replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
                                }// 如果不是代码型
                            } // 如果字段名称不是空
                        } // 如果是字符型或代码型或数字型
                    } // 如果subflag不是1
                }// headSetList循环结束
                if (tasklist != null && tasklist.size() > 0) {
                    _abean.set("ins_id", rowSet.getString("ins_id") != null ? rowSet.getString("ins_id") : "0");
                    _abean.set("task_id", rowSet.getString("task_id") != null ? rowSet.getString("task_id") : "0");
                    String _str = (String) submitMap.get(rowSet.getString("seqnum"));
                    _abean.set("submitflag", _str.split("`")[1]);
                    _abean.set("task_id", _str.split("`")[0]);
                } else {
                    _abean.set("seqnum", "");
                    _abean.set("ins_id", "0");
                    _abean.set("task_id", "0");
                }
                _abean.set("seqnum", rowSet.getString("seqnum"));

                if ("1".equals(isCompare)) // 对照
                {
                    _abean2 = new LazyDynaBean();
                    _abean2.set("num", String.valueOf(num));
                    _abean2.set("a0100", a0100);
                    _abean2.set("basepre", basepre);
                    _abean2.set("chg", "1");

                    before_bean = (LazyDynaBean) compareDataMap.get(basepre.toLowerCase() + a0100);
                    if (before_bean != null) {
                        for (int i = 0; i < headSetList.size(); i++) {
                            abean = (LazyDynaBean) headSetList.get(i);
                            String field_name = (String) abean.get("field_name");
                            String field_type = (String) abean.get("field_type");
                            String isvar = (String) abean.get("isvar");
                            String subflag = (String) abean.get("subflag");
                            String chgstate = (String) abean.get("chgstate");
                            String codeid = (String) abean.get("codeid");
                            int disformat = Integer.parseInt(abean.get("disformat") == null ? "0" : (String) abean.get("disformat"));
                            String formula = abean.get("formula") == null ? "" : (String) abean.get("formula");
                            String sub_domain_id = "";
                            if (abean.get("sub_domain_id") != null && "1".equals(abean.get("chgstate"))) {
                                sub_domain_id = (String) abean.get("sub_domain_id");
                                if (sub_domain_id != null && sub_domain_id.length() > 0) {
                                    sub_domain_id = "_" + sub_domain_id;
                                } else {
                                    sub_domain_id = "";
                                }
                            }
                            if ("1".equals(isvar))
                                continue;
                            if ("1".equals(subflag))
                                continue;

                            String _fieldname = field_name;
                            if ("0".equals(isvar)) {
                                _fieldname = field_name + sub_domain_id + "_" + chgstate;
                            }

                            if ("1".equals(isvar) || "1".equals(chgstate)) {
                                if ("M".equalsIgnoreCase(field_type)) {
                                    _abean2.set(_fieldname, SafeCode.encode(Sql_switcher.readMemo(rowSet, _fieldname)));
                                } else if ("D".equalsIgnoreCase(field_type)) {
                                    if (rowSet.getDate(_fieldname) != null)
                                        _abean2.set(_fieldname, formatDateValue(PubFunc.FormatDate(rowSet.getDate(_fieldname)), formula, disformat));
                                    else
                                        _abean2.set(_fieldname, "");
                                } else {
                                    if (rowSet.getString(_fieldname) == null) {
                                        _abean2.set(_fieldname, "");
                                    } else {
                                        if ("A".equalsIgnoreCase(field_type) && codeid.length() > 0 && !"0".equals(codeid)) {
                                            if ("UM".equals(codeid.toUpperCase())) {
                                                if ("".equals(AdminCode.getCodeName(codeid, rowSet.getString(_fieldname))))
                                                    codeid = "UN";
                                            }

                                            if ("UM".equals(codeid.toUpperCase())) {
                                                String value = "";
                                                if (Integer.parseInt(display_e0122) == 0) {
                                                    value = AdminCode.getCodeName("UM", rowSet.getString(_fieldname));
                                                } else {
                                                    CodeItem item = AdminCode.getCode("UM", rowSet.getString(_fieldname), Integer.parseInt(display_e0122));
                                                    if (item != null) {
                                                        value = item.getCodename();
                                                    } else {
                                                        value = AdminCode.getCodeName("UM", rowSet.getString(_fieldname));
                                                    }

                                                }
                                                _abean2.set(_fieldname, value);
                                            } else
                                                _abean2.set(_fieldname, AdminCode.getCodeName(codeid, rowSet.getString(_fieldname)));

                                        } else
                                            _abean2.set(_fieldname, rowSet.getString(_fieldname));
                                    }
                                }
                            } else if ("1".equals(subflag)) {
                                _abean2.set(_fieldname, "......");
                            } else if (before_bean.get(field_name) != null)
                                _abean2.set(_fieldname, (String) before_bean.get(field_name));
                        }

                        _abean2.set("submitflag", (String) _abean.get("submitflag"));
                        _abean2.set("seqnum", (String) _abean.get("seqnum"));
                        _abean2.set("ins_id", (String) _abean.get("ins_id"));
                        _abean2.set("task_id", (String) _abean.get("task_id"));

                    }
                    // list.add(_abean2);
                    String key = basepre.toLowerCase() + a0100;
                    prechangemap.put(key, _abean2);
                }
                list.add(_abean);
                if (_abean.get("subflag") != null && "0".equals(_abean.get("subflag"))) {
                    if ("1".equals(_abean.get("chgstate")))
                        preMap.put(_abean.get("field_name").toString().toLowerCase(), _abean);
                    else
                        endMap.put(_abean.get("field_name").toString().toLowerCase(), _abean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList list2 = list;
        ArrayList list3 = new ArrayList();
        if ("1".equals(isCompare)) {
            String temp = ",";
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean tempbean = (LazyDynaBean) list.get(i);
                if (tempbean.get("isvar") != null && !"0".equals(tempbean.get("isvar")))
                    continue;
                if (tempbean.get("subflag") != null && "0".equals(tempbean.get("subflag")) && endMap.get(tempbean.get("field_name").toString().toLowerCase()) != null && prechangemap.get(tempbean.get("field_name").toString().toLowerCase()) != null) {
                    tempbean = (LazyDynaBean) endMap.get(tempbean.get("field_name").toString().toLowerCase());
                    if (temp.indexOf("," + tempbean.get("field_name").toString().toLowerCase() + ",") == -1) {
                        temp += tempbean.get("field_name").toString().toLowerCase() + ",";
                        list3.add(tempbean);
                        continue;
                    } else {
                        continue;
                    }
                }
                list3.add(tempbean);
            }
            list2 = list3;
            LazyDynaBean tempbean;
            if (this.operationtype != 0) {
                if (endMap.get("b0110") != null) {
                    list2.remove(endMap.get("b0110"));
                    tempbean = (LazyDynaBean) endMap.get("b0110");
                    if (preMap.get("b0110") != null && "1".equals(((LazyDynaBean) preMap.get("b0110")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(0, tempbean);
                }
                if (endMap.get("e0122") != null) {
                    list2.remove(endMap.get("e0122"));
                    tempbean = (LazyDynaBean) endMap.get("e0122");
                    if (preMap.get("e0122") != null && "1".equals(((LazyDynaBean) preMap.get("e0122")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(1, (LazyDynaBean) endMap.get("e0122"));
                }
                if (endMap.get("e01a1") != null) {
                    list2.remove(endMap.get("e01a1"));
                    tempbean = (LazyDynaBean) endMap.get("e01a1");
                    if (preMap.get("e01a1") != null && "1".equals(((LazyDynaBean) preMap.get("e01a1")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(2, (LazyDynaBean) endMap.get("e01a1"));
                }
                if (endMap.get("a0101") != null) {
                    list2.remove(endMap.get("a0101"));
                    tempbean = (LazyDynaBean) endMap.get("a0101");
                    if (preMap.get("a0101") != null && "1".equals(((LazyDynaBean) preMap.get("a0101")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(3, (LazyDynaBean) endMap.get("a0101"));
                }
            } else {
                if (endMap.get("a0101") != null) {
                    list2.remove(endMap.get("a0101"));
                    tempbean = (LazyDynaBean) endMap.get("a0101");
                    if (preMap.get("a0101") != null && "1".equals(((LazyDynaBean) preMap.get("a0101")).get("isLock")))
                        tempbean.set("isLock", "1");
                    list2.add(0, (LazyDynaBean) endMap.get("a0101"));
                }
            }
        }
        return list2;
    }

    public static void main(String[] args) {
        String ss = "alter table temp_su drop column YK16118";
        int from_index = ss.indexOf(" table ");
        int to_index = ss.indexOf(" drop ");
        System.out.println(ss.substring(from_index + 6, to_index));

        from_index = ss.indexOf(" column ");
        System.out.println(ss.substring(from_index + 7));

    }

    // 取得变化后指标涉及到的子集
    public HashMap getSetToFieldsMap(ArrayList headSetList) {
        HashMap map = new HashMap();
        LazyDynaBean abean = null;
        for (int i = 0; i < headSetList.size(); i++) {
            abean = (LazyDynaBean) headSetList.get(i);
            String setname = ((String) abean.get("setname")).toUpperCase();
            String isvar = (String) abean.get("isvar");
            String subflag = (String) abean.get("subflag");
            String field_type = (String) abean.get("field_type");
            if ("1".equals(isvar))
                continue;
            if ("1".equals(subflag))
                continue;
            // if(field_type.equalsIgnoreCase("M"))
            // continue;
            String chgstate = (String) abean.get("chgstate");
            if ("2".equals(chgstate)) {
                if (map.get(setname) == null) {
                    ArrayList list = new ArrayList();
                    list.add(abean);
                    map.put(setname, list);
                } else {
                    ArrayList list = (ArrayList) map.get(setname);
                    list.add(abean);
                }

            }
        }
        return map;
    }

    /**
     * 取得变化后指标在库中的值
     * 
     * @param sql
     * @param tasklist
     * @param headSetList
     * @return
     */
    public HashMap getBeforeChgState(String sql, ArrayList tasklist, ArrayList headSetList) {
        HashMap map = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            HashMap setFieldsMap = getSetToFieldsMap(headSetList);
            StringBuffer sql_0 = new StringBuffer("");
            String _sql = "";
            if (this.bo != null && this.bo.getInfor_type() == 1)
                _sql = sql.replaceAll("\\*", " distinct basepre ");
            else
                _sql = sql;
            _sql = _sql.substring(0, _sql.indexOf(" order "));
            RowSet rowSet = dao.search(_sql);
            RowSet rowSet2 = null;
            LazyDynaBean abean = null;

            while (rowSet.next()) {
                String from_str = "";
                Set keySet = setFieldsMap.keySet();
                String where = "";
                String basepre = "";
                if (this.bo != null && this.bo.getInfor_type() == 1) {
                    basepre = rowSet.getString("basepre");
                    sql_0.setLength(0);
                    sql_0.append("select " + basepre + "A01.A0100");

                    from_str = " from " + basepre + "A01";
                    String where_str = " 1=1 ";
                    for (Iterator t = keySet.iterator(); t.hasNext();) {
                        String key = (String) t.next();
                        ArrayList tmpList = (ArrayList) setFieldsMap.get(key);
                        for (int i = 0; i < tmpList.size(); i++) {
                            abean = (LazyDynaBean) tmpList.get(i);
                            String field_name = (String) abean.get("field_name");
                            if ("A0100".equalsIgnoreCase(field_name))
                                continue;
                            sql_0.append("," + basepre + key + "." + field_name);
                        }

                        String tmp_name = basepre + key;
                        if (!"A01".equalsIgnoreCase(key)) {
                            from_str += " left join (select * from " + tmp_name + " a where a.i9999=(select max(i9999) from " + tmp_name + " b where a.a0100=b.a0100 )) " + tmp_name;
                            from_str += " on " + basepre + "A01.a0100=" + tmp_name + ".A0100";

                        }
                    }
                    String tablename = "templet_" + tabid;
                    if (tasklist == null || tasklist.size() == 0)
                        tablename = this.userview.getUserName() + "templet_" + tabid;

                    if (_sql.toLowerCase().indexOf("where") == -1)
                        where = " where exists ( " + _sql.replaceAll(" distinct basepre ", " null ") + " where " + basepre + "A01.a0100=" + tablename + ".a0100 and lower(basepre)='" + basepre.toLowerCase() + "' )";
                    else
                        where = " where exists ( " + _sql.replaceAll(" distinct basepre ", " null ") + " and " + basepre + "A01.a0100=" + tablename + ".a0100 and lower(basepre)='" + basepre.toLowerCase() + "' )";
                } else if (this.bo != null && this.bo.getInfor_type() == 2) {

                    basepre = rowSet.getString("b0110");
                    sql_0.setLength(0);
                    sql_0.append("select b01.b0110 ");

                    from_str = " from b01 ";
                    String where_str = " 1=1 ";
                    for (Iterator t = keySet.iterator(); t.hasNext();) {
                        String key = (String) t.next();
                        ArrayList tmpList = (ArrayList) setFieldsMap.get(key);
                        for (int i = 0; i < tmpList.size(); i++) {
                            abean = (LazyDynaBean) tmpList.get(i);
                            String field_name = (String) abean.get("field_name");
                            if ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {
                                continue;
                            }
                            if ("b0110".equalsIgnoreCase(field_name))
                                continue;
                            sql_0.append("," + key + "." + field_name);
                        }

                        String tmp_name = key;
                        if (!"b01".equalsIgnoreCase(key)) {
                            from_str += " left join (select * from " + tmp_name + " a where a.i9999=(select max(i9999) from " + tmp_name + " b where a.b0110=b.b0110 )) " + tmp_name;
                            from_str += " on b01.b0110=" + tmp_name + ".b0110";
                        }

                    }
                    String tablename = "templet_" + tabid;
                    if (tasklist == null || tasklist.size() == 0)
                        tablename = this.userview.getUserName() + "templet_" + tabid;

                    if (_sql.toLowerCase().indexOf("where") == -1)
                        where = " where exists ( " + _sql.replaceAll("\\*", " null ") + " where b01.b0110=" + tablename + ".b0110  )";
                    else
                        where = " where exists ( " + _sql.replaceAll("\\*", " null ") + " and b01.b0110=" + tablename + ".b0110  )";

                } else if (this.bo != null && this.bo.getInfor_type() == 3) {

                    basepre = rowSet.getString("e01a1");
                    sql_0.setLength(0);
                    sql_0.append("select k01.e01a1 ");

                    from_str = " from k01 ";
                    String where_str = " 1=1 ";
                    for (Iterator t = keySet.iterator(); t.hasNext();) {
                        String key = (String) t.next();
                        ArrayList tmpList = (ArrayList) setFieldsMap.get(key);
                        for (int i = 0; i < tmpList.size(); i++) {
                            abean = (LazyDynaBean) tmpList.get(i);
                            String field_name = (String) abean.get("field_name");
                            if ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {
                                continue;
                            }
                            if ("e01a1".equalsIgnoreCase(field_name))
                                continue;
                            sql_0.append("," + key + "." + field_name);
                        }

                        String tmp_name = key;
                        if (!"k01".equalsIgnoreCase(key)) {
                            from_str += " left join (select * from " + tmp_name + " a where a.i9999=(select max(i9999) from " + tmp_name + " b where a.e01a1=b.e01a1 )) " + tmp_name;
                            from_str += " on k01.e01a1=" + tmp_name + ".e01a1";
                        }

                    }
                    String tablename = "templet_" + tabid;
                    if (tasklist == null || tasklist.size() == 0)
                        tablename = this.userview.getUserName() + "templet_" + tabid;

                    if (_sql.toLowerCase().indexOf("where") == -1)
                        where = " where exists ( " + _sql.replaceAll("\\*", " null ") + " where k01.e01a1=" + tablename + ".e01a1  )";
                    else
                        where = " where exists ( " + _sql.replaceAll("\\*", " null ") + " and k01.e01a1=" + tablename + ".e01a1  )";

                }

                rowSet2 = dao.search(sql_0.toString() + from_str + where);
                while (rowSet2.next()) {
                    LazyDynaBean _bean = new LazyDynaBean();
                    if (this.bo != null && this.bo.getInfor_type() == 1) {
                        _bean.set("a0100", rowSet2.getString("a0100"));
                    } else if (this.bo != null && this.bo.getInfor_type() == 2) {
                        _bean.set("a0100", rowSet2.getString("b0110"));
                    } else if (this.bo != null && this.bo.getInfor_type() == 3) {
                        _bean.set("a0100", rowSet2.getString("e01a1"));
                    } else {
                        _bean.set("a0100", rowSet2.getString("a0100"));
                    }

                    for (Iterator t = keySet.iterator(); t.hasNext();) {
                        String key = (String) t.next();
                        ArrayList tmpList = (ArrayList) setFieldsMap.get(key);
                        for (int i = 0; i < tmpList.size(); i++) {
                            abean = (LazyDynaBean) tmpList.get(i);
                            String field_name = (String) abean.get("field_name");
                            String field_type = (String) abean.get("field_type");
                            String codeid = (String) abean.get("codeid");

                            if ((this.bo != null && this.bo.getInfor_type() != 1) && ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name))) {
                                continue;
                            }
                            int disformat = Integer.parseInt(abean.get("disformat") == null ? "0" : (String) abean.get("disformat"));
                            String formula = abean.get("formula") == null ? "" : (String) abean.get("formula");
                            if ("M".equalsIgnoreCase(field_type)) {
                                _bean.set(field_name, SafeCode.encode(Sql_switcher.readMemo(rowSet2, field_name)));
                            } else if ("D".equalsIgnoreCase(field_type)) {
                                if (rowSet2.getDate(field_name) != null)
                                    _bean.set(field_name, formatDateValue(PubFunc.FormatDate(rowSet2.getDate(field_name)), formula, disformat));
                                else
                                    _bean.set(field_name, "");
                            } else {
                                if (rowSet2.getString(field_name) == null) {
                                    _bean.set(field_name, "");
                                } else {
                                    if ("A".equalsIgnoreCase(field_type) && codeid.length() > 0 && !"0".equals(codeid)) {
                                        _bean.set(field_name, AdminCode.getCodeName(codeid, rowSet2.getString(field_name)));
                                    } else
                                        _bean.set(field_name, rowSet2.getString(field_name));
                                }
                            }
                        }
                    }
                    if (this.bo != null && this.bo.getInfor_type() == 1) {
                        map.put(basepre.toLowerCase() + rowSet2.getString("a0100"), _bean);
                    } else if (this.bo != null && this.bo.getInfor_type() == 2) {
                        map.put(rowSet2.getString("b0110"), _bean);
                    } else if (this.bo != null && this.bo.getInfor_type() == 3) {
                        map.put(rowSet2.getString("e01a1"), _bean);
                    } else {
                        map.put(basepre.toLowerCase() + rowSet2.getString("a0100"), _bean);
                    }

                }
            }

            if (rowSet != null)
                rowSet.close();
            if (rowSet2 != null)
                rowSet2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
	/**
	 * 读所有打印页(没有设置此页不打印的页)的单元格的信息
	 * @return 列表中存放的是TemplateSetBo对象
	 */
    public ArrayList getAllCell(){
    	ArrayList list=getAllCells();
    	return (ArrayList) list.get(0);
    }
    /**
     * 读所有单元格的信息（包括设置此页不打印的页）
     * 
     * @return 列表中存放的是TemplateSetBo对象
     */
    public ArrayList getAllCells() {
        ArrayList list = new ArrayList();
        ArrayList isNoprnList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rset = null;
        String temp = null;
        String caseStr = "";
        try {

            LazyDynaBean abean = null;

            if (this.bo != null && this.bo.getInfor_type() == 1 && this.operationtype != 0) {
                list.add(getBean(ResourceFactory.getProperty("b0110.label"), "B0110", "A", "UN", "A01", "1", "0", "0", "0"));
                list.add(getBean(ResourceFactory.getProperty("e0122.label"), "E0122", "A", "UM", "A01", "1", "0", "0", "0"));
                list.add(getBean(ResourceFactory.getProperty("e01a1.label"), "E01A1", "A", "@K", "A01", "1", "0", "0", "0"));
                list.add(getBean("姓名", "A0101", "A", "0", "A01", "1", "0", "0", "0"));
                caseStr += ",B0110_1,E0122_1,E01A1_1,A0101_1,";
            }

            sql.append("select s.*,page.isShow,page.ismobile from Template_Set s ");// xcs
                                                                        // page.isPrn
                                                                        // 打印控制标识
            sql.append("left join Template_Page page on s.tabid=page.tabid ");
            sql.append(" where   s.pageid = page.pageid and s.tabid= ");
            sql.append(this.tabid);
            if (this.bo != null && this.bo.getInfor_type() == 1) {
                sql.append("  and upper(s.flag)='A' ");
            } else if (this.bo != null && this.bo.getInfor_type() == 2) {
                sql.append("  and upper(s.flag)='B' ");
            } else if (this.bo != null && this.bo.getInfor_type() == 3) {
                sql.append("  and upper(s.flag)='K' ");
            } else
                sql.append("  and upper(s.flag)='A' ");
            if (this.operationtype == 0 || this.operationtype == 5)
                sql.append(" and s.chgstate=2 ");
            sql.append(" order by s.nSort,s.rtop,s.rleft");
            // System.out.println("sql:"+sql);
            RowSet rowSet = dao.search(sql.toString());

            boolean flag = false;
            LazyDynaBean _bean = null;
            LazyDynaBean _bean2 = null; // 上级组织单元
            LazyDynaBean _bean3 = null; // 组织单元
            Document doc = null;
            Element element = null;
            String xpath = "/sub_para/para";
            while (rowSet.next()) {
                abean = new LazyDynaBean();
                int isShow=1;
                if(rowSet.getString("isShow")!=null)
                	isShow = rowSet.getInt("isShow");
                if (this.isMobile!=-1&&this.isMobile!=rowSet.getInt("isMobile")) {
					continue;
				}

                if (this.bo != null && this.bo.getInfor_type() == 1 && this.operationtype != 0 && rowSet.getString("field_name") != null) {
                    String name = rowSet.getString("field_name");
                    String chgstate = rowSet.getString("chgstate");
                    if ("B0110".equalsIgnoreCase(name) || "E0122".equalsIgnoreCase(name) || "E01A1".equalsIgnoreCase(name) || "A0101".equalsIgnoreCase(name)) {
                        if ("1".equals(chgstate))
                            continue;
                    }
                }
                String subflag = rowSet.getString("subflag");// 字段控制标识符 0：字段
                                                                // 1：子集
                String itemid = rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "";
                FieldItem item = DataDictionary.getFieldItem(itemid);

                if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && itemid != null && DataDictionary.getFieldItem(itemid.toLowerCase()) == null) {// 如果是单位部门或岗位
                    if ("codesetid".equalsIgnoreCase(itemid) || "codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid) || "parentid".equalsIgnoreCase(itemid) || "start_date".equalsIgnoreCase(itemid)) {
                        item = new FieldItem();
                        item.setItemid(itemid);
                        item.setItemdesc(rowSet.getString("hz"));
                        item.setFieldsetid(rowSet.getString("setname"));
                        item.setItemtype(rowSet.getString("Field_type"));
                        if ("codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid))
                            item.setCodesetid("0");
                        else if ("codesetid".equalsIgnoreCase(itemid))
                            item.setCodesetid("orgType");
                        else
                            item.setCodesetid(rowSet.getString("codeid"));

                        if (!"start_date".equalsIgnoreCase(itemid))
                            item.setItemlength(50);
                    }
                }
                if (item == null && (subflag == null || "0".equals(subflag)))
                    continue;
                abean.set("hz", rowSet.getString("hz") != null ? rowSet.getString("hz") : "");
                abean.set("field_name", rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "");
                abean.set("field_type", rowSet.getString("field_type") != null ? rowSet.getString("field_type") : "");
                if (!"1".equals(rowSet.getString("subflag")) && "1".equals(rowSet.getString("chgstate"))) {// 如果是字段,且必须是变化前的
                    if (Sql_switcher.searchDbServer() == 2) {
                        // if(("2".equals(""+rowSet.getInt("HisMode"))&&("1".equals(""+rowSet.getInt("Mode_o"))||"3".equals(""+rowSet.getInt("Mode_o"))))||
                        // //2014-04-01 dengcan
                        if ("2".equals("" + rowSet.getInt("HisMode")) || "3".equals("" + rowSet.getInt("HisMode")) || "4".equals("" + rowSet.getInt("HisMode"))) {
                            abean.set("field_type", "M");// 猜测
                                                            // 因为是某个子集的一个字段的多条记录
                                                            // 所以采用M备注型
                        }
                    } else {
                        if ("2".equals("" + rowSet.getInt("HisMode")) || "3".equals("" + rowSet.getInt("HisMode")) || "4".equals("" + rowSet.getInt("HisMode"))) {
                            abean.set("field_type", "M");
                        }
                    }
                }
                abean.set("field_hz", rowSet.getString("field_hz") != null ? rowSet.getString("field_hz") : "");
                if (item == null) {
                    abean.set("codeid", rowSet.getString("codeid") != null ? rowSet.getString("codeid") : "");
                } else {
                    abean.set("codeid", item.getCodesetid() == null ? "0" : item.getCodesetid());
                }

                abean.set("setname", rowSet.getString("setname") != null ? rowSet.getString("setname") : "");// 子集代码
                abean.set("chgstate", rowSet.getString("chgstate") != null ? rowSet.getString("chgstate") : "");// 变化前、变化后
                abean.set("hismode", rowSet.getString("hismode") != null ? rowSet.getString("hismode") : "");// 历史定位方式
                                                                                                                // 1：一条记录
                                                                                                                // 2：多条记录
                                                                                                                // 3：条件定位
                                                                                                                // 4：条件序号

                if (Sql_switcher.searchDbServer() == 2)
                    abean.set("mode", rowSet.getString("Mode_o") != null ? rowSet.getString("Mode_o") : "");
                else
                    abean.set("mode", rowSet.getString("Mode") != null ? rowSet.getString("Mode") : "");

                // 特殊处理针对子集的subflag
                String subflag2 = rowSet.getString("subflag") != null ? rowSet.getString("subflag") : "";
                String field_name = rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "";
                String field_type = rowSet.getString("field_type") != null ? rowSet.getString("field_type") : "";
                if ("".equals(subflag2) && "".equals(field_name) && "".equals(field_type)) {
                    abean.set("subflag", "1");
                } else {
                    abean.set("subflag", rowSet.getString("subflag") != null ? rowSet.getString("subflag") : "");
                }
                abean.set("isvar", "0");
                abean.set("pageid", "" + rowSet.getInt("pageid"));// 表页号
                abean.set("gridno", "" + rowSet.getInt("gridno"));// 单元格编号
                abean.set("yneed", "" + rowSet.getInt("yneed"));// 是否必填
                abean.set("disformat", "" + rowSet.getInt("disformat"));// 数据显示格式
                String formula = Sql_switcher.readMemo(rowSet, "formula");
                abean.set("formula", formula != null ? formula : "");
                String sub_domain = Sql_switcher.readMemo(rowSet, "sub_domain");
                abean.set("sub_domain", "" + SafeCode.encode(sub_domain));
                // 获得sub_domain_id
                String sub_domain_id = "";
                abean.set("sub_domain_id", sub_domain_id);
                if (sub_domain != null && sub_domain.trim().length() > 0 && "1".equals("" + rowSet.getInt("chgstate"))) {// 子集信息不为空且是变化前的
                    try {
                        doc = PubFunc.generateDom(sub_domain);;
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0) {
                            element = (Element) childlist.get(0);
                            if (element.getAttributeValue("id") != null) {
                                sub_domain_id = (String) element.getAttributeValue("id");
                                if (sub_domain_id != null && sub_domain_id.trim().length() > 0)
                                    abean.set("sub_domain_id", sub_domain_id);
                                else
                                    sub_domain_id = "";
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                if (rowSet.getString("field_name") != null) {
                    if (item != null) {
                        abean.set("itemlength", "" + item.getItemlength());
                        abean.set("decimalwidth", "" + item.getDecimalwidth());
                    }
                }
                if (this.bo != null && this.bo.getInfor_type() == 1 && this.operationtype == 0 && rowSet.getString("field_name") != null && "a0101".equalsIgnoreCase(rowSet.getString("field_name")) && "2".equals(rowSet.getString("chgstate"))) {// 如果是对人员的操作，而且是人员调入，并且当前指标名称是姓名，并且是变化后的
                    flag = true;
                    _bean = abean;
                    continue;
                } else if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && (("2".equals(rowSet.getString("chgstate")) && this.operationtype == 5) || ("1".equals(rowSet.getString("chgstate")) && this.operationtype != 5)) && rowSet.getString("field_name") != null && ("parentid".equalsIgnoreCase(rowSet.getString("field_name")))) {
                    flag = true;
                    _bean2 = abean;
                    continue;
                } else if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && (("2".equals(rowSet.getString("chgstate")) && this.operationtype == 5) || ("1".equals(rowSet.getString("chgstate")) && this.operationtype != 5)) && rowSet.getString("field_name") != null && ("codeitemdesc".equalsIgnoreCase(rowSet.getString("field_name")))) {
                    flag = true;
                    _bean3 = abean;
                    continue;
                }
                if (sub_domain_id.length() > 0)
                    sub_domain_id = "_" + sub_domain_id;
                if ("1".equals(abean.get("subflag"))) {
                    String tempstr = "," + abean.get("hz").toString() + sub_domain_id + "_" + abean.get("chgstate").toString() + ",";
                    if (caseStr.indexOf(tempstr) == -1)
                        caseStr += abean.get("hz").toString() + sub_domain_id + "_" + abean.get("chgstate").toString() + ",";
                    else
                        continue;
                } else {
                    String tempstr = "," + abean.get("field_name").toString() + sub_domain_id + "_" + abean.get("chgstate").toString() + ",";
                    if (caseStr.indexOf(tempstr) == -1)
                        caseStr += abean.get("field_name").toString() + sub_domain_id + "_" + abean.get("chgstate").toString() + ",";
                    else
                        continue;
                }
                if (class_type == 1) {

                } else {
				   //如果设置了此页不打印,指标放入isNoprnList中
                   if(isShow==0){
                	    //多个相同指标在多个页签时会存在某页不印，导致打印页指标导出不显示，
						//不打印时去除caseStr已添加的指标 
						caseStr = caseStr.replace(abean.get("field_name").toString()+sub_domain_id+"_"+abean.get("chgstate").toString()+",","");
            			isNoprnList.add(abean);
						continue;
           			 }
                }
				list.add(abean);
            } // while end

            if (flag == true) { // 如果为调入模板 ，姓名放第一列位置
                if (_bean != null)
                    list.add(0, _bean);
                if (_bean3 != null)
                    list.add(0, _bean3);
                if (_bean2 != null)
                    list.add(0, _bean2);
            }

            getAllVariableHm(list);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ArrayList returnList=new ArrayList();
        returnList.add(list);
        returnList.add(isNoprnList);
        return returnList;
    }

    public LazyDynaBean getBean(String hz, String fieldname, String fieldType, String codeid, String setname, String chgstate, String hismode, String subflag, String isvar) {
        LazyDynaBean abean = new LazyDynaBean();
        abean.set("hz", hz);
        abean.set("field_name", fieldname);
        abean.set("field_type", fieldType);
        abean.set("field_hz", hz);
        abean.set("codeid", codeid);
        abean.set("setname", setname);
        abean.set("chgstate", chgstate);
        abean.set("hismode", hismode);
        abean.set("model", "");
        abean.set("subflag", subflag);
        abean.set("isvar", isvar);
        return abean;
    }

    /**
     * 取得变量表
     * 
     * @return
     */
    private HashMap getAllVariableHm(ArrayList list) {
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hm = new HashMap();
        try {
            strsql.append("select * from MidVariable where nflag=0 and templetid= ");
            strsql.append(this.tabid);
            strsql.append(" order by sorting");
            RowSet rset = dao.search(strsql.toString());

            HashMap ntypeToType = new HashMap();
            ntypeToType.put("1", "N");
            ntypeToType.put("2", "A");
            ntypeToType.put("3", "D");
            ntypeToType.put("4", "A");

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

                LazyDynaBean abean = new LazyDynaBean();
                abean.set("hz", rset.getString("chz"));
                abean.set("field_name", rset.getString("cname"));
                abean.set("field_type", (String) ntypeToType.get(rset.getString("ntype").trim()));
                abean.set("field_hz", rset.getString("chz"));
                abean.set("codeid", rset.getString("codesetid") != null ? rset.getString("codesetid") : "");
                abean.set("setname", "");
                abean.set("chgstate", "");
                abean.set("hismode", "");
                abean.set("mode", "");
                abean.set("subflag", "");
                abean.set("isvar", "1");
                abean.set("formula", "");
                list.add(abean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hm;
    }

    // 取得默认隐藏列指标串
    public String getDefaultHiddenitemStr(ArrayList list) {
        StringBuffer str = new StringBuffer("");
        LazyDynaBean abean = null;
        for (int i = 0; i < list.size(); i++) {
            abean = (LazyDynaBean) list.get(i);
            String isvar = (String) abean.get("isvar");
            // String sub_domain_id = "";
            // if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
            // sub_domain_id = (String)abean.get("sub_domain_id");
            // if(sub_domain_id!=null&&sub_domain_id.length()>0){
            // sub_domain_id ="_"+sub_domain_id;
            // }else{
            // sub_domain_id="";
            // }
            // }
            if ("1".equals(isvar))
                str.append((String) abean.get("field_name") + ",");
            else {
                // 隐藏指标
                FieldItem item = DataDictionary.getFieldItem("" + abean.get("field_name"));
                if (item != null) {
                    String fieldset = item.getFieldsetid();
                    if (this.hiddenMap != null && this.hiddenMap.get(fieldset + ":" + abean.get("field_name")) != null) {
                        str.append((String) abean.get("field_name") + ",");
                        // str.append((String)abean.get("field_name")+sub_domain_id+"_1,");
                        // str.append((String)abean.get("field_name")+"_2,");
                    }

                }
            }

        }
        return str.toString();
    }

    // /------------ xgq ----------------
    public boolean isFixedTarget(LazyDynaBean abean) {
        boolean flag = false;

        if ((this.operationtype != 0 && "1".equals(abean.get("chgstate"))) || (this.operationtype == 0 && "A0101".equalsIgnoreCase(abean.get("field_name").toString()) && "2".equals(abean.get("chgstate")))) {
            flag = true;
        }
        return flag;
    }
    /**
     * 取得变量表
     * 
     * @return
     */
    public LazyDynaBean getAllVariableBean(String fieldname) {
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        LazyDynaBean abean = null;
        try {
            strsql.append("select * from MidVariable where nflag=0 and cname='" + fieldname + "' and templetid= ");
            strsql.append(this.tabid);
            strsql.append(" order by sorting");
            RowSet rset = dao.search(strsql.toString());

            HashMap ntypeToType = new HashMap();
            ntypeToType.put("1", "N");
            ntypeToType.put("2", "A");
            ntypeToType.put("3", "D");
            ntypeToType.put("4", "A");

            while (rset.next()) {

                abean = new LazyDynaBean();
                abean.set("hz", rset.getString("chz"));
                abean.set("field_name", rset.getString("cname"));
                abean.set("field_type", (String) ntypeToType.get(rset.getString("ntype").trim()));
                abean.set("field_hz", rset.getString("chz"));
                abean.set("codeid", rset.getString("codesetid") != null ? rset.getString("codesetid") : "0");
                abean.set("setname", "");
                abean.set("chgstate", "");
                abean.set("hismode", "");
                abean.set("subflag", "");
                abean.set("isvar", "1");
                abean.set("fldlen", "" + rset.getInt("fldlen"));
                abean.set("flddec", "" + rset.getInt("flddec"));

                // list.add(abean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return abean;
    }
    /**
     * 获得最后一个锁定指标的bean 锁定字符串 排序字符串 默认锁定
     * 
     * @return LazyDynaBean abean
     */
    public LazyDynaBean getLastLockTarget(ArrayList tableHeadSetList, String fieldSetSortStr, String lockedItemStr, String isCompare) {
        LazyDynaBean cellBean = new LazyDynaBean();
        LazyDynaBean cellBean2 = null;
        LazyDynaBean cellBean3 = new LazyDynaBean();
        if (lockedItemStr.length() > 0)
            lockedItemStr = ("," + lockedItemStr + ",").toLowerCase();
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            cellBean = (LazyDynaBean) tableHeadSetList.get(i);
            String field_name = (String) cellBean.get("field_name");
            String chgstate = (String) cellBean.get("chgstate");
            String isvar = (String) cellBean.get("isvar");
            String _fieldName = ((String) cellBean.get("field_name")).toLowerCase();
            String sub_domain_id = "";
            if (cellBean.get("sub_domain_id") != null && "1".equals(cellBean.get("chgstate"))) {
                sub_domain_id = (String) cellBean.get("sub_domain_id");
                if (sub_domain_id != null && sub_domain_id.length() > 0) {
                    sub_domain_id = "_" + sub_domain_id;
                } else {
                    sub_domain_id = "";
                }
            }
            if ("0".equals(isvar))
                _fieldName += sub_domain_id + "_" + chgstate;

            if (lockedItemStr.length() > 0) {
                if ("1".equals(cellBean.get("subflag"))) {
                    if (lockedItemStr.indexOf("," + cellBean.get("pageid").toString().trim() + "_" + cellBean.get("gridno").toString().trim() + ",") != -1)
                        cellBean2 = cellBean;
                } else {
                    if ("1".equals(isCompare) && "0".equals(isvar)) {

                        if (this.operationtype != 0) {

                            if (lockedItemStr.toLowerCase().indexOf(",b0110_1,") != -1 && "b0110_2".equalsIgnoreCase(_fieldName.toLowerCase())) {
                                cellBean2 = cellBean;
                            }
                            if (lockedItemStr.toLowerCase().indexOf(",e0122_1,") != -1 && "e0122_2".equalsIgnoreCase(_fieldName.toLowerCase())) {
                                cellBean2 = cellBean;
                            }
                            if (lockedItemStr.toLowerCase().indexOf(",e01a1_1,") != -1 && "e01a1_2".equalsIgnoreCase(_fieldName.toLowerCase())) {
                                cellBean2 = cellBean;
                            }
                            if (lockedItemStr.toLowerCase().indexOf(",a0101_1,") != -1 && "a0101_2".equalsIgnoreCase(_fieldName.toLowerCase())) {
                                cellBean2 = cellBean;
                            }
                        } else {
                            if (lockedItemStr.toLowerCase().indexOf(",a0101_1,") != -1 && "a0101_2".equalsIgnoreCase(_fieldName.toLowerCase())) {
                                cellBean2 = cellBean;

                            }
                        }
                    } else {
                        if (lockedItemStr.indexOf("," + _fieldName.toLowerCase() + ",") != -1)
                            cellBean2 = cellBean;
                    }
                }
            } else {

                if (fieldSetSortStr.length() <= 0) {
                    if (this.bo.getInfor_type() == 1 && "1".equals(chgstate) && ("B0110".equalsIgnoreCase(field_name) || "E0122".equalsIgnoreCase(field_name) || "E01A1".equalsIgnoreCase(field_name) || "A0101".equalsIgnoreCase(field_name)))
                        cellBean2 = cellBean;
                    if ((this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && (("2".equals(chgstate) && this.operationtype == 5) || ("1".equals(chgstate) && this.operationtype != 5)) && field_name != null && ("codeitemdesc".equalsIgnoreCase(field_name)))
                        cellBean2 = cellBean;
                }

            }

        }
        // if(cellBean2==null){
        // cellBean2 = new LazyDynaBean();
        // if(this.bo.getInfor_type()==2||this.bo.getInfor_type()==3)
        // cellBean2 =cellBean3;
        //			
        // }
        return cellBean2;
    }
    public ArrayList selectField(ArrayList templateSetList, String hiddenItem) {
        // 结合隐藏指标。
        // 主集下的变化前指标
        ArrayList list = new ArrayList();
        for (int i = 0; i < templateSetList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
            String field_name = (String) abean.get("field_name");
            String setname = (String) abean.get("setname");

            if (this.bo.getInfor_type() == 1) {
                if ("A01".equalsIgnoreCase(setname)) {
                    if (!"1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_1");
                        list.add(item_0);
                    }
                }
            } else if (this.bo.getInfor_type() == 2) {
                if ("B01".equalsIgnoreCase(setname)) {
                    if (!"1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_1");
                        list.add(item_0);
                    } else {
                        if ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {

                            item = new FieldItem();
                            item.setItemid(field_name);
                            item.setItemdesc(abean.get("hz").toString().replace("`", ""));
                            item.setFieldsetid(setname);
                            item.setItemtype((String) abean.get("field_type"));
                            item.setCodesetid(abean.get("codeid").toString());
                            if (!"start_date".equalsIgnoreCase(field_name))
                                item.setItemlength(50);
                            item.setUseflag("1");

                            FieldItem item_0 = (FieldItem) item.clone();
                            if (!"1".equals(item_0.getUseflag()))
                                continue;
                            if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                                int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                                item_0.setItemlength(count);
                            }
                            item_0.setItemid(field_name + "_1");
                            list.add(item_0);

                        }
                    }
                }
            } else if (this.bo.getInfor_type() == 3) {
                if ("K01".equalsIgnoreCase(setname)) {
                    if (!"1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_1");
                        list.add(item_0);
                    } else {
                        if ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {

                            item = new FieldItem();
                            item.setItemid(field_name);
                            item.setItemdesc(abean.get("hz").toString().replace("`", ""));
                            item.setFieldsetid(setname);
                            item.setItemtype((String) abean.get("field_type"));
                            item.setCodesetid(abean.get("codeid").toString());
                            if (!"start_date".equalsIgnoreCase(field_name))
                                item.setItemlength(50);
                            item.setUseflag("1");

                            FieldItem item_0 = (FieldItem) item.clone();
                            if (!"1".equals(item_0.getUseflag()))
                                continue;
                            if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                                int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                                item_0.setItemlength(count);
                            }
                            item_0.setItemid(field_name + "_1");
                            list.add(item_0);

                        }
                    }
                }
            }
        }
        // }
        return list;

    }
    public ArrayList selectField2(ArrayList templateSetList, String hiddenItem) {
        // 结合隐藏指标。
        // 主集下的变化前指标
        ArrayList list = new ArrayList();
        for (int i = 0; i < templateSetList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
            String field_name = (String) abean.get("field_name");
            String setname = (String) abean.get("setname");
            String yneed = (String) abean.get("yneed");

            if (this.bo.getInfor_type() == 1) {
                if ("A01".equalsIgnoreCase(setname)) {
                    if ("1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_2");
                        list.add(item_0);
                    }
                }
            } else if (this.bo.getInfor_type() == 2) {
                if ("B01".equalsIgnoreCase(setname)) {
                    if ("1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_2");
                        if ("1".equals(yneed))
                            item_0.setFillable(true);
                        list.add(item_0);
                    } else {
                        if ("codesetid".equalsIgnoreCase(field_name) || "codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {

                            item = new FieldItem();
                            item.setItemid(field_name);
                            item.setItemdesc(abean.get("hz").toString().replace("`", ""));
                            item.setFieldsetid(setname);
                            item.setItemtype((String) abean.get("field_type"));
                            item.setCodesetid(abean.get("codeid").toString());
                            if (!"start_date".equalsIgnoreCase(field_name))
                                item.setItemlength(50);
                            item.setUseflag("1");

                            FieldItem item_0 = (FieldItem) item.clone();
                            if (!"1".equals(item_0.getUseflag()))
                                continue;
                            if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                                int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                                item_0.setItemlength(count);
                            }
                            item_0.setItemid(field_name + "_2");
                            if ("1".equals(yneed))
                                item_0.setFillable(true);
                            list.add(item_0);

                        }
                    }
                }
            } else if (this.bo.getInfor_type() == 3) {
                if ("K01".equalsIgnoreCase(setname)) {
                    if ("1".equals(abean.get("chgstate")))
                        continue;
                    // hiddenItem=","+hiddenItem;
                    // hiddenItem= hiddenItem.replace("_1", "");
                    // if(hiddenItem.indexOf(","+field_name+",")==-1){
                    FieldItem item = DataDictionary.getFieldItem(field_name);

                    if (item != null) {
                        FieldItem item_0 = (FieldItem) item.clone();
                        if (!"1".equals(item_0.getUseflag()))
                            continue;
                        if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                            int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                            item_0.setItemlength(count);
                        }
                        item_0.setItemid(field_name + "_2");
                        if ("1".equals(yneed))
                            item_0.setFillable(true);
                        list.add(item_0);
                    } else {
                        if ("codeitemdesc".equalsIgnoreCase(field_name) || "corcode".equalsIgnoreCase(field_name) || "parentid".equalsIgnoreCase(field_name) || "start_date".equalsIgnoreCase(field_name)) {

                            item = new FieldItem();
                            item.setItemid(field_name);
                            item.setItemdesc(abean.get("hz").toString().replace("`", ""));
                            item.setFieldsetid(setname);
                            item.setItemtype((String) abean.get("field_type"));
                            item.setCodesetid(abean.get("codeid").toString());
                            if (!"start_date".equalsIgnoreCase(field_name))
                                item.setItemlength(50);
                            item.setUseflag("1");

                            FieldItem item_0 = (FieldItem) item.clone();
                            if (!"1".equals(item_0.getUseflag()))
                                continue;
                            if (item_0.getCodesetid() != null && !"0".equals(item_0.getCodesetid())) {
                                int count = getCodeSetidChildLen(item_0.getCodesetid(), conn);
                                item_0.setItemlength(count);
                            }
                            item_0.setItemid(field_name + "_2");
                            if ("1".equals(yneed))
                                item_0.setFillable(true);
                            list.add(item_0);

                        }
                    }
                }
            }
        }
        // }
        return list;

    }
    private int getCodeSetidChildLen(String codesetid, Connection conn) {
        String sql = "select count(*) aa from codeitem where codesetid = '" + codesetid + "'";
        RowSet rs = null;
        int count = 0;
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql);
            if (rs.next())
                count = rs.getInt("aa");
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
        return count;
    }
    /** 组合查询SQL */
    public String combine_SQL(UserView userView, ArrayList factorlist, String like, String userid, HashMap map, String tablename) throws GeneralException {

        int j = 1;
        boolean bresult = true;
        boolean blike = false;

        if ("1".equals(like))
            blike = true;
        StringBuffer strexpr = new StringBuffer();
        StringBuffer strfactor = new StringBuffer();
        ArrayList checklist = new ArrayList();
        InfoUtils infoUtils = new InfoUtils();
        for (int i = 0; i < factorlist.size(); i++) {
            FieldItem item = (FieldItem) factorlist.get(i);
            // System.out.println(item.getItemdesc());
            /** 如果值未填的话，default是否为不查 */
            if ((item.getValue() == null || "".equals(item.getValue())) && (!"D".equals(item.getItemtype())))
                continue;
            if (((item.getValue() == null || "".equals(item.getValue())) && (item.getViewvalue() == null || "".equals(item.getViewvalue()))) && ("D".equals(item.getItemtype())))
                continue;

            if ("D".equals(item.getItemtype())) {
                int sf = analyFieldDate(item, strexpr, strfactor, j);
                if (sf == 1) {
                    throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
                }
                j = j + sf;
            } else {
                /** 组合表达式串 */
                if (j == 1) {
                    strexpr.append(j);
                } else {
                    strexpr.append("*");
                    strexpr.append(j);
                }

                if ("A".equals(item.getItemtype()) || "M".equals(item.getItemtype())) {
                    String q_v = item.getValue().trim();
                    if ("1".equals(like) && (!(q_v == null || "".equals(q_v)))) {

                        strfactor.append(item.getItemid().toUpperCase());
                        if ("0".equals(item.getCodesetid()))
                            strfactor.append("=*");
                        else
                            strfactor.append("=");
                        strfactor.append(PubFunc.getStr(item.getValue()));
                        strfactor.append("*`");
                    } else {

                        strfactor.append(item.getItemid().toUpperCase());
                        strfactor.append("=");
                        strfactor.append(PubFunc.getStr(item.getValue()));
                        strfactor.append("`");
                    }
                } else {
                    strfactor.append(item.getItemid().toUpperCase());
                    strfactor.append("=");
                    strfactor.append(PubFunc.getStr(item.getValue()));
                    strfactor.append("`");
                }
                ++j;
            }
        }

        FactorList factor_bo = new FactorList(strexpr.toString(), strfactor.toString().toUpperCase(), userid, map);

        String sql = factor_bo.getSingleTableSqlExpression(tablename);
        return sql;

    }
    public ArrayList getCodesetid() {
        ArrayList list = new ArrayList();
        if (this.bo.getInfor_type() == 2) {
            CommonData cd = new CommonData("", "");
            list.add(cd);
            cd = new CommonData("UN", "单位");
            list.add(cd);
            cd = new CommonData("UM", "部门");
            list.add(cd);
        } else {
            CommonData cd = new CommonData("", "");
            list.add(cd);
            cd = new CommonData("@K", "职位");
            list.add(cd);
        }
        return list;
    }
    public int analyFieldDate(FieldItem item, StringBuffer strexpr, StringBuffer strfactor, int pos) {
        String s_str_date = item.getValue();
        String e_str_date = item.getViewvalue();
        s_str_date = s_str_date.replaceAll("\\.", "-");
        e_str_date = e_str_date.replaceAll("\\.", "-");
        // item.setValue(s_str_date);
        // item.setViewvalue(e_str_date);

        try {
            Date s_date = DateStyle.parseDate(s_str_date);
            Date e_date = DateStyle.parseDate(e_str_date);
            /** 起始日期及终止日期格式全对 */
            if (s_date != null && e_date != null) {
                if (strexpr.length() == 0) {
                    strexpr.append(pos);
                    strexpr.append("*");
                    strexpr.append(pos + 1);

                } else {
                    strexpr.append("*(");
                    strexpr.append(pos);
                    strexpr.append("*");
                    strexpr.append(pos + 1);
                    strexpr.append(")");
                }
                strfactor.append(item.getItemid().toUpperCase());
                strfactor.append(">=");
                strfactor.append(item.getValue().replaceAll("-", "."));
                strfactor.append("`");
                strfactor.append(item.getItemid().toUpperCase());
                strfactor.append("<=");
                strfactor.append(item.getViewvalue().replaceAll("-", "."));
                strfactor.append("`");
                return 2;
            } else if (isnumber(s_str_date) && isnumber(e_str_date)) {
                if (strexpr.length() == 0) {
                    strexpr.append(pos);
                    strexpr.append("*");
                    strexpr.append(pos + 1);

                } else {
                    strexpr.append("*(");
                    strexpr.append(pos);
                    strexpr.append("*");
                    strexpr.append(pos + 1);
                    strexpr.append(")");
                }
                strfactor.append(item.getItemid().toUpperCase());
                strfactor.append(">=$YRS[");
                strfactor.append(item.getValue());
                strfactor.append("]`");
                strfactor.append(item.getItemid().toUpperCase());
                strfactor.append("<=$YRS[");
                strfactor.append(item.getViewvalue());
                strfactor.append("]`");
                return 2;
            } else {
                if (strexpr.length() == 0) {
                    strexpr.append(pos);
                } else {
                    strexpr.append("*");
                    strexpr.append(pos);
                }
                strfactor.append(item.getItemid().toUpperCase());
                strfactor.append("=");
                strfactor.append("`");
                return 1;
            }
        } catch (Exception ex) {
            return 1;
        }
    }
    /** 分析字符串是否为数值型 */
    private boolean isnumber(String strvalue) {
        boolean bflag = true;
        try {
            Float.parseFloat(strvalue.replaceAll("-", "."));
        } catch (NumberFormatException ne) {
            bflag = false;
        }
        return bflag;
    }
    /**
     * 格式化日期字符串
     * 
     * @param value
     *            日期字段值 yyyy-mm-dd
     * @param ext
     *            扩展
     * @return
     */
    private String formatDateValue(String value, String ext, int disformat) {
        StringBuffer buf = new StringBuffer();
        if (ext != null && ext.indexOf("<EXPR>") != -1) {

            int f = ext.indexOf("<EXPR>");
            int t = ext.indexOf("</FACTOR>");
            String _temp = ext.substring(0, f);
            String _temp2 = ext.substring(t + 9);
            ext = _temp + _temp2;
        }
        int idx = ext.indexOf(","); // -,至今
        String prefix = "", strext = "";
        if (idx == -1) {
            String[] preCond = getPrefixCond(ext);
            prefix = preCond[0];
        } else {
            prefix = ext.substring(0, idx);
            strext = ext.substring(idx + 1);
        }
        if ("".equals(value)) {
            buf.append(prefix);
            buf.append(strext);
            return buf.toString();
        } else {
            buf.append(prefix);
        }
        Date date = DateUtils.getDate(value, "yyyy-MM-dd");
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int day = DateUtils.getDay(date);
        String[] strv = exchangNumToCn(year, month, day);
        value = value.replaceAll("-", ".");
        switch (disformat) {
            case 6 : // 1991.12.3
                buf.append(year);
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
            case 7 : // 91.12.3
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
            case 8 :// 1991.2
                buf.append(year);
                buf.append(".");
                buf.append(month);
                break;
            case 9 :// 1992.02
                buf.append(value.substring(0, 7));
                break;
            case 10 :// 92.2
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                buf.append(month);
                break;
            case 11 :// 98.02
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                break;
            case 12 :// 一九九一年一月二日

                buf.append(strv[0]);
                buf.append("年");
                buf.append(strv[1]);
                buf.append("月");
                buf.append(strv[2]);
                buf.append("日");
                break;
            case 13 :// 一九九一年一月
                buf.append(strv[0]);
                buf.append("年");
                buf.append(strv[1]);
                buf.append("月");
                break;
            case 14 :// 1991年1月2日
                buf.append(year);
                buf.append("年");
                buf.append(month);
                buf.append("月");
                buf.append(day);
                buf.append("日");
                break;
            case 15 :// 1991年1月
                buf.append(year);
                buf.append("年");
                buf.append(month);
                buf.append("月");
                break;
            case 16 :// 91年1月2日
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append("年");
                buf.append(month);
                buf.append("月");
                buf.append(day);
                buf.append("日");
                break;
            case 17 :// 91年1月
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append("年");
                buf.append(month);
                buf.append("月");
                break;
            case 18 :// 年龄
                buf.append(getAge(year, month, day));
                break;
            case 19 :// 1991（年）
                buf.append(year);
                break;
            case 20 :// 1 （月）
                buf.append(month);
                break;
            case 21 :// 23 （日）
                buf.append(day);
                break;
            case 22 :// 1999年02月
                buf.append(year);
                buf.append("年");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append("月");
                break;
            case 23 :// 1999年02月03日
                buf.append(year);
                buf.append("年");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append("月");
                if (day >= 10)
                    buf.append(day);
                else {
                    buf.append("0");
                    buf.append(day);
                }
                buf.append("日");
                break;
            case 24 :// 1992.02.01
                buf.append(year);
                buf.append(".");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append(".");
                if (day >= 10)
                    buf.append(day);
                else {
                    buf.append("0");
                    buf.append(day);
                }
                break;
            default :
                buf.append(year);
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
        }
        return buf.toString();
    }
    /**
     * 子集中格式化日期字符串
     * 
     * @param value
     *            日期字段值 yyyy-mm-dd
     * @param ext
     *            扩展
     * @return
     */
    private String formatDateFiledsetValue(String value, String ext, int disformat) {
        StringBuffer buf = new StringBuffer();
        int idx = ext.indexOf(","); // -,至今
        String prefix = "", strext = "";
        if (idx == -1) {
            String[] preCond = getPrefixCond(ext);
            prefix = preCond[0];
        } else {
            prefix = ext.substring(0, idx);
            strext = ext.substring(idx + 1);
        }
        if ("".equals(value)) {
            buf.append(prefix);
            buf.append(strext);
            return buf.toString();
        } else {
            buf.append(prefix);
        }
        Date date = DateUtils.getDate(value, "yyyy-MM-dd");
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int day = DateUtils.getDay(date);
        String[] strv = exchangNumToCn(year, month, day);
        value = value.replaceAll("-", ".");
        switch (disformat) {
            case 0 : // 1991.12.3
                buf.append(year);
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
            case 1 : // 91.12.3
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
            case 2 :// 1991.2
                buf.append(year);
                buf.append(".");
                buf.append(month);
                break;
            case 3 :// 1992.02
                buf.append(value.substring(0, 7));
                break;
            case 4 :// 92.2
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                buf.append(month);
                break;
            case 5 :// 98.02
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append(".");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                break;
            case 6 :// 一九九一年一月二日

                buf.append(strv[0]);
                buf.append("年");
                buf.append(strv[1]);
                buf.append("月");
                buf.append(strv[2]);
                buf.append("日");
                break;
            case 7 :// 一九九一年一月
                buf.append(strv[0]);
                buf.append("年");
                buf.append(strv[1]);
                buf.append("月");
                break;
            case 8 :// 1991年1月2日
                buf.append(year);
                buf.append("年");
                buf.append(month);
                buf.append("月");
                buf.append(day);
                buf.append("日");
                break;
            case 9 :// 1991年1月
                buf.append(year);
                buf.append("年");
                buf.append(month);
                buf.append("月");
                break;
            case 10 :// 91年1月2日
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append("年");
                buf.append(month);
                buf.append("月");
                buf.append(day);
                buf.append("日");
                break;
            case 11 :// 91年1月
                if (year >= 2000)
                    buf.append(year);
                else {
                    String temp = String.valueOf(year);
                    buf.append(temp.substring(2));
                }
                buf.append("年");
                buf.append(month);
                buf.append("月");
                break;
            case 12 :// 年龄
                buf.append(getAge(year, month, day));
                break;
            case 13 :// 1991（年）
                buf.append(year);
                break;
            case 14 :// 1 （月）
                buf.append(month);
                break;
            case 15 :// 23 （日）
                buf.append(day);
                break;
            case 16 :// 1999年02月
                buf.append(year);
                buf.append("年");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append("月");
                break;
            case 17 :// 1999年02月03日
                buf.append(year);
                buf.append("年");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append("月");
                if (day >= 10)
                    buf.append(day);
                else {
                    buf.append("0");
                    buf.append(day);
                }
                buf.append("日");
                break;
            case 18 :// 1992.02.01
                buf.append(year);
                buf.append(".");
                if (month >= 10)
                    buf.append(month);
                else {
                    buf.append("0");
                    buf.append(month);
                }
                buf.append(".");
                if (day >= 10)
                    buf.append(day);
                else {
                    buf.append("0");
                    buf.append(day);
                }
                break;
            default :
                buf.append(year);
                buf.append(".");
                buf.append(month);
                buf.append(".");
                buf.append(day);
                break;
        }
        return buf.toString();
    }
    /**
     * 解释Formula字段的内容 for example ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
     * 
     * @return
     */
    private String[] getPrefixCond(String formula) {
        String[] preCond = new String[3];
        int idx = formula.indexOf("<");
        if (idx == -1) {
            preCond[0] = formula;
        } else {
            preCond[0] = formula.substring(0, idx);
            preCond[2] = getPattern("FACTOR", formula) + ",";
            preCond[2] = preCond[2].replaceAll(",", "`");
            preCond[1] = getPattern("EXPR", formula);
        }
        return preCond;
    }
    /**
     * 数字换算
     * 
     * @param strV
     * @param flag
     * @return
     */
    private String[] exchangNumToCn(int year, int month, int day) {
        String[] strarr = new String[3];
        StringBuffer buf = new StringBuffer();
        String value = String.valueOf(year);
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '1' :
                    buf.append("一");
                    break;
                case '2' :
                    buf.append("二");
                    break;
                case '3' :
                    buf.append("三");
                    break;
                case '4' :
                    buf.append("四");
                    break;
                case '5' :
                    buf.append("五");
                    break;
                case '6' :
                    buf.append("六");
                    break;
                case '7' :
                    buf.append("七");
                    break;
                case '8' :
                    buf.append("八");
                    break;
                case '9' :
                    buf.append("九");
                    break;
                case '0' :
                    buf.append("零");
                    break;
            }
        }
        strarr[0] = buf.toString();
        buf.setLength(0);
        switch (month) {
            case 1 :
                buf.append("一");
                break;
            case 2 :
                buf.append("二");
                break;
            case 3 :
                buf.append("三");
                break;
            case 4 :
                buf.append("四");
                break;
            case 5 :
                buf.append("五");
                break;
            case 6 :
                buf.append("六");
                break;
            case 7 :
                buf.append("七");
                break;
            case 8 :
                buf.append("八");
                break;
            case 9 :
                buf.append("九");
                break;
            case 10 :
                buf.append("十");
                break;
            case 11 :
                buf.append("十一");
                break;
            case 12 :
                buf.append("十二");
                break;
        }
        strarr[1] = buf.toString();
        buf.setLength(0);
        switch (day) {
            case 1 :
                buf.append("一");
                break;
            case 2 :
                buf.append("二");
                break;
            case 3 :
                buf.append("三");
                break;
            case 4 :
                buf.append("四");
                break;
            case 5 :
                buf.append("五");
                break;
            case 6 :
                buf.append("六");
                break;
            case 7 :
                buf.append("七");
                break;
            case 8 :
                buf.append("八");
                break;
            case 9 :
                buf.append("九");
                break;
            case 10 :
                buf.append("十");
                break;
            case 11 :
                buf.append("十一");
                break;
            case 12 :
                buf.append("十二");
                break;
            case 13 :
                buf.append("十三");
                break;
            case 14 :
                buf.append("十四");
                break;
            case 15 :
                buf.append("十五");
                break;
            case 16 :
                buf.append("十六");
                break;
            case 17 :
                buf.append("十七");
                break;
            case 18 :
                buf.append("十八");
                break;
            case 19 :
                buf.append("十九");
                break;
            case 20 :
                buf.append("二十");
                break;
            case 21 :
                buf.append("二十一");
                break;
            case 22 :
                buf.append("二十二");
                break;
            case 23 :
                buf.append("二十三");
                break;
            case 24 :
                buf.append("二十四");
                break;
            case 25 :
                buf.append("二十五");
                break;
            case 26 :
                buf.append("二十六");
                break;
            case 27 :
                buf.append("二十七");
                break;
            case 28 :
                buf.append("二十八");
                break;
            case 29 :
                buf.append("二十九");
                break;
            case 30 :
                buf.append("三十");
                break;
            case 31 :
                buf.append("三十一");
                break;
        }
        strarr[2] = buf.toString();
        return strarr;
    }
    /**
     * 计算年龄
     * 
     * @param nyear
     * @param nmonth
     * @param nday
     * @return
     */
    private String getAge(int nyear, int nmonth, int nday) {
        int ncyear, ncmonth, ncday;
        Date curdate = new Date();
        ncyear = DateUtils.getYear(curdate);
        ncmonth = DateUtils.getMonth(curdate);
        ncday = DateUtils.getDay(curdate);
        StringBuffer buf = new StringBuffer();

        /*
         * double fcage=ncyear+ncmonth*0.01+ncday*0.0001; double
         * fage=nyear+nmonth*0.01+nday*0.0001; long nage=
         * Math.round(fcage-fage); buf.append(nage);
         */
        int result = ncyear - nyear;
        if (nmonth > ncmonth) {
            result = result - 1;
        } else {
            if (nmonth == ncmonth) {
                if (nday > ncday) {
                    result = result - 1;
                }
            }
        }
        buf.append(result);
        return buf.toString();
    }
    private String getPattern(String strPattern, String formula) {
        int iS, iE;
        String result = "";
        String sSP = "<" + strPattern + ">";
        iS = formula.indexOf(sSP);
        String sEP = "</" + strPattern + ">";
        iE = formula.indexOf(sEP);
        if (iS >= 0 && iS < iE) {
            result = formula.substring(iS + sSP.length(), iE);
        }
        return result;
    }
    private HashMap hiddenfielditem() {
        HashMap map = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            RowSet rowset = dao.search(" select fieldsetid, itemid,displaywidth from fielditem where displaywidth=0 ");
            while (rowset.next()) {
                map.put(rowset.getString("fieldsetid") + ":" + rowset.getString("itemid"), rowset.getString("itemid"));
            }
            rowset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    public void getExcel(HSSFWorkbook wb, ArrayList fieldlist, StringBuffer strsql, String table_name, TemplateTableBo tablebo, String cname) {
        HSSFSheet sheet = wb.createSheet(cname);
        HSSFFont font2 = wb.createFont();
        font2.setFontHeightInPoints((short) 10);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        style2.setWrapText(true);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setBottomBorderColor((short) 8);
        style2.setLeftBorderColor((short) 8);
        style2.setRightBorderColor((short) 8);
        style2.setTopBorderColor((short) 8);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setFont(font2);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setVerticalAlignment(VerticalAlignment.CENTER);
        style1.setWrapText(true);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBottomBorderColor((short) 8);
        style1.setLeftBorderColor((short) 8);
        style1.setRightBorderColor((short) 8);
        style1.setTopBorderColor((short) 8);
        style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        HSSFCellStyle styles = wb.createCellStyle();
        styles.setFont(font2);
        styles.setAlignment(HorizontalAlignment.CENTER);
        styles.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.setWrapText(true);
        // styles.setBorderBottom(BorderStyle.THIN);
        // styles.setBorderLeft(BorderStyle.THIN);
        // styles.setBorderRight(BorderStyle.THIN);
        // styles.setBorderTop(BorderStyle.THIN);
        // styles.setBottomBorderColor((short) 8);
        // styles.setLeftBorderColor((short) 8);
        // styles.setRightBorderColor((short) 8);
        // styles.setTopBorderColor((short) 8);
        styles.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

        HSSFCellStyle styleN = dataStyle(wb);
        styleN.setAlignment(HorizontalAlignment.RIGHT);
        styleN.setWrapText(true);
        HSSFDataFormat df = wb.createDataFormat();
        styleN.setDataFormat(df.getFormat(decimalwidth(0)));
        HSSFCellStyle styleN2 = dataStyle(wb);
        // styleN2.setAlignment(HorizontalAlignment.RIGHT);
        // styleN2.setWrapText(true);
        // HSSFDataFormat df3 = wb.createDataFormat();
        styleN2.setDataFormat(df.getFormat(decimalwidth(0)));
        HSSFCellStyle styleCol0 = dataStyle(wb);
        HSSFFont font0 = wb.createFont();
        font0.setFontHeightInPoints((short) 5);
        styleCol0.setFont(font0);
        styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleCol0_title = dataStyle(wb);
        styleCol0_title.setFont(font2);
        styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleF1 = dataStyle(wb);
        styleF1.setAlignment(HorizontalAlignment.RIGHT);
        styleF1.setWrapText(true);
        HSSFDataFormat df1 = wb.createDataFormat();
        styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

        HSSFCellStyle styleF2 = dataStyle(wb);
        styleF2.setAlignment(HorizontalAlignment.RIGHT);
        styleF2.setWrapText(true);
        HSSFDataFormat df2 = wb.createDataFormat();
        styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

        HSSFCellStyle styleF3 = dataStyle(wb);
        styleF3.setAlignment(HorizontalAlignment.RIGHT);
        styleF3.setWrapText(true);
        HSSFDataFormat df3 = wb.createDataFormat();
        styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

        HSSFCellStyle styleF4 = dataStyle(wb);
        styleF4.setAlignment(HorizontalAlignment.RIGHT);
        styleF4.setWrapText(true);
        HSSFDataFormat df4 = wb.createDataFormat();
        styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

        HSSFCellStyle styleF5 = dataStyle(wb);
        styleF5.setAlignment(HorizontalAlignment.RIGHT);
        styleF5.setWrapText(true);
        HSSFDataFormat df5 = wb.createDataFormat();
        styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
        ArrayList codeCols = new ArrayList();
        HashMap cell_param_map = null;
        try {
            cell_param_map = tablebo.getModeCell4();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        getExcelTittle(wb, sheet, styleCol0_title, fieldlist, style2, codeCols, "0");

        HashMap onlynamemap = new HashMap();
        String onlyname = "";
        this.getExcelBody(wb, sheet, styleCol0_title, fieldlist, styleN, styleF1, styleF2, styleF3, styleF4, style1, styles, strsql, table_name, styleCol0, codeCols, cell_param_map, onlynamemap, onlyname, "0");

    }
    public void getExcel2(HSSFWorkbook wb, ArrayList fieldlist, StringBuffer strsql, String table_name, TemplateTableBo tablebo, String fieldset_2, HashMap fieldsetmap, String cname, HashMap nameMap, String onlyname, String onlyflag, String wherestr) {

        HSSFFont font2 = wb.createFont();
        font2.setFontHeightInPoints((short) 10);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        style2.setWrapText(true);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setBottomBorderColor((short) 8);
        style2.setLeftBorderColor((short) 8);
        style2.setRightBorderColor((short) 8);
        style2.setTopBorderColor((short) 8);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setFont(font2);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setVerticalAlignment(VerticalAlignment.CENTER);
        style1.setWrapText(true);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBottomBorderColor((short) 8);
        style1.setLeftBorderColor((short) 8);
        style1.setRightBorderColor((short) 8);
        style1.setTopBorderColor((short) 8);
        style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        HSSFCellStyle styles = wb.createCellStyle();
        styles.setFont(font2);
        styles.setAlignment(HorizontalAlignment.CENTER);
        styles.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.setWrapText(true);
        styles.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

        HSSFCellStyle styleN = dataStyle(wb);
        styleN.setAlignment(HorizontalAlignment.RIGHT);
        styleN.setWrapText(true);
        HSSFDataFormat df = wb.createDataFormat();
        styleN.setDataFormat(df.getFormat(decimalwidth(0)));
        HSSFCellStyle styleN2 = dataStyle(wb);
        styleN2.setDataFormat(df.getFormat(decimalwidth(0)));
        HSSFCellStyle styleCol0 = dataStyle(wb);
        HSSFFont font0 = wb.createFont();
        font0.setFontHeightInPoints((short) 5);
        styleCol0.setFont(font0);
        styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleCol0_title = dataStyle(wb);
        styleCol0_title.setFont(font2);
        styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleF1 = dataStyle(wb);
        styleF1.setAlignment(HorizontalAlignment.RIGHT);
        styleF1.setWrapText(true);
        HSSFDataFormat df1 = wb.createDataFormat();
        styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

        HSSFCellStyle styleF2 = dataStyle(wb);
        styleF2.setAlignment(HorizontalAlignment.RIGHT);
        styleF2.setWrapText(true);
        HSSFDataFormat df2 = wb.createDataFormat();
        styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

        HSSFCellStyle styleF3 = dataStyle(wb);
        styleF3.setAlignment(HorizontalAlignment.RIGHT);
        styleF3.setWrapText(true);
        HSSFDataFormat df3 = wb.createDataFormat();
        styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

        HSSFCellStyle styleF4 = dataStyle(wb);
        styleF4.setAlignment(HorizontalAlignment.RIGHT);
        styleF4.setWrapText(true);
        HSSFDataFormat df4 = wb.createDataFormat();
        styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

        HSSFCellStyle styleF5 = dataStyle(wb);
        styleF5.setAlignment(HorizontalAlignment.RIGHT);
        styleF5.setWrapText(true);
        HSSFDataFormat df5 = wb.createDataFormat();
        styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
        ArrayList codeCols = new ArrayList();
        HashMap cell_param_map = null;
        try {
            cell_param_map = tablebo.getModeCell4();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        HashMap onlynamemap = new HashMap();
        if (fieldset_2 == null || fieldset_2.trim().length() == 0) {
            HSSFSheet sheet = wb.createSheet(cname);
            getExcelTittle(wb, sheet, styleCol0_title, fieldlist, style2, codeCols, onlyflag);
            getExcelBody(wb, sheet, styleCol0_title, fieldlist, styleN, styleF1, styleF2, styleF3, styleF4, style1, styles, strsql, table_name, styleCol0, codeCols, cell_param_map, onlynamemap, onlyname, onlyflag);
        } else {
            HSSFSheet sheet = wb.createSheet(cname);

            getExcelTittle(wb, sheet, styleCol0_title, fieldlist, style2, codeCols, onlyflag);
            getExcelBody(wb, sheet, styleCol0_title, fieldlist, styleN, styleF1, styleF2, styleF3, styleF4, style1, styles, strsql, table_name, styleCol0, codeCols, cell_param_map, onlynamemap, onlyname, onlyflag);
            if (fieldset_2.endsWith(",")) {
                fieldset_2 = fieldset_2.substring(0, fieldset_2.length() - 1);
            }
            String[] fieldset2 = fieldset_2.split(",");
            for (int i = 0; i < fieldset2.length; i++) {
                String fieldset = fieldset2[i];
                if (fieldset != null && fieldset.trim().length() > 0) {
                    String name = "";
                    if (nameMap != null && nameMap.get(fieldset) != null)
                        name = nameMap.get(fieldset) + "(t_" + fieldset + "_2)";
                    sheet = wb.createSheet(name);
                    String sub_domain = (String) fieldsetmap.get(fieldset);
                    ArrayList codeCol2s = new ArrayList();
                    ArrayList fieldlist2 = new ArrayList();
                    this.getSubSetTableExcelTitle(sub_domain, wb, sheet, styleCol0_title, fieldlist2, style2, codeCol2s, onlyname);
                    this.getSubSetTableExcelBody(wb, sheet, styleCol0_title, fieldlist, styleN, styleF1, styleF2, styleF3, styleF4, style1, styles, strsql, table_name, styleCol0, codeCol2s, cell_param_map, fieldset, onlyname, onlynamemap, onlyflag, wherestr, fieldlist2);
                }
            }
        }
    }
    public void getExcelTittle(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleCol0_title, ArrayList fieldlist, HSSFCellStyle style2, ArrayList codeCols, String onlyflag) {
        sheet.setColumnWidth((short) 0, (short) 0);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
        HSSFPatriarch patr = sheet.createDrawingPatriarch();

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        HSSFComment comm = null;
        if ("0".equals(onlyflag)) {
            cell = row.createCell((short) 0);
            cell.setCellValue(cellStr("主键标识串"));
            cell.setCellStyle(styleCol0_title);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));
            comm.setString(new HSSFRichTextString("主键标识串"));
            cell.setCellComment(comm);
        }
        String fieldExplain = "";
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem field = (FieldItem) fieldlist.get(i);
            String fieldName = field.getItemid().toLowerCase();
            String fieldLabel = field.getItemdesc();
            if ("0".equals(onlyflag)) {
                if (field.isFillable())
                    fieldLabel += "*";
                sheet.setColumnWidth((short) (i + 1), (short) 3500);
                if ("UN".equalsIgnoreCase(field.getCodesetid()) || "UM".equalsIgnoreCase(field.getCodesetid()) || "@k".equalsIgnoreCase(field.getCodesetid()))
                    sheet.setColumnWidth((short) (i + 1), (short) 5000);

                cell = row.createCell((short) (i + 1));

                cell.setCellValue(cellStr(fieldLabel));
                cell.setCellStyle(style2);
                comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
                comm.setString(new HSSFRichTextString(fieldName));
                cell.setCellComment(comm);
                if (!"0".equals(field.getCodesetid()))
                    codeCols.add(field.getCodesetid() + ":" + new Integer(i + 1).toString() + ":" + fieldName);
                else {// 不是代码类时要考虑到组织机构类型的特殊性
                    if (this.getBo() != null && this.getBo().getInfor_type() != 1 && fieldName.startsWith("codesetid_")) {
                        codeCols.add("codesetid" + ":" + new Integer(i + 1).toString() + ":" + fieldName);
                    }
                }
            } else {

                if (field.isFillable())
                    fieldLabel += "*";
                sheet.setColumnWidth((short) (i), (short) 3500);
                if ("UN".equalsIgnoreCase(field.getCodesetid()) || "UM".equalsIgnoreCase(field.getCodesetid()) || "@k".equalsIgnoreCase(field.getCodesetid()))
                    sheet.setColumnWidth((short) (i), (short) 5000);

                cell = row.createCell((short) (i));

                cell.setCellValue(cellStr(fieldLabel));
                cell.setCellStyle(style2);
                comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
                comm.setString(new HSSFRichTextString(fieldName));
                cell.setCellComment(comm);
                if (!"0".equals(field.getCodesetid()))
                    codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString() + ":" + fieldName);
                else {// 不是代码类时要考虑到组织机构类型的特殊性
                    if (this.getBo() != null && this.getBo().getInfor_type() != 1 && fieldName.startsWith("codesetid_")) {
                        codeCols.add("codesetid" + ":" + new Integer(i).toString() + ":" + fieldName);
                    }
                }
            }
        }

    }
    public void getExcelBody(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleCol0_title, ArrayList fieldlist, HSSFCellStyle styleN, HSSFCellStyle styleF1, HSSFCellStyle styleF2, HSSFCellStyle styleF3, HSSFCellStyle styleF4, HSSFCellStyle style1, HSSFCellStyle styles, StringBuffer strsql, String table_name, HSSFCellStyle styleCol0, ArrayList codeCols, HashMap cell_param_map, HashMap onlynamemap, String onlyname, String onlyflag) {

        HSSFRow row = null;
        HSSFCell cell = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            onlynamemap.put(onlyname, "");
            RowSet rset = null;
            int rowCount = 1;
            rset = dao.search(strsql.toString());
            short onlyflag2 = 1;
            if ("1".equals(onlyflag) || "2".equals(onlyflag)) {
                onlyflag2 = 0;
            }
            while (rset.next()) {

                String nASE = "";
                String a0100 = "";
                String flag = "";
                if (this.getBo() != null && this.getBo().getInfor_type() == 1) {
                    nASE = rset.getString("BasePre");
                    a0100 = rset.getString("A0100");
                    flag = nASE + "|" + a0100;
                } else if (this.getBo() != null && this.getBo().getInfor_type() == 2) {
                    a0100 = rset.getString("b0110");
                    flag = a0100;
                } else if (this.getBo() != null && this.getBo().getInfor_type() == 3) {
                    a0100 = rset.getString("e01a1");
                    flag = a0100;
                } else {
                    nASE = rset.getString("BasePre");
                    a0100 = rset.getString("A0100");
                    flag = nASE + "|" + a0100;
                }

                if (table_name.startsWith("templet")) {
                    int ins_id = rset.getInt("ins_id");
                    int task_id = rset.getInt("task_id");
                    flag += "|" + ins_id + "|" + task_id;
                }
                row = sheet.createRow(rowCount++);
                if ("0".equals(onlyflag)) {
                    cell = row.createCell((short) 0);

                    cell.setCellValue(cellStr(flag));
                    cell.setCellStyle(styleCol0);
                }
                for (int i = 0; i < fieldlist.size(); i++) {
                    FieldItem field = (FieldItem) fieldlist.get(i);
                    String fieldName = field.getItemid().toLowerCase();
                    String itemtype = field.getItemtype();
                    int decwidth = field.getDecimalwidth();
                    String codesetid = field.getCodesetid();
                    int itemlength = field.getItemlength();

                    cell = row.createCell((short) (onlyflag2 + i));
                    if ("N".equals(itemtype)) {
                        if (decwidth == 0)
                            cell.setCellStyle(styleN);
                        else if (decwidth == 1)
                            cell.setCellStyle(styleF1);
                        else if (decwidth == 2)
                            cell.setCellStyle(styleF2);
                        else if (decwidth == 3)
                            cell.setCellStyle(styleF3);
                        else if (decwidth == 4)
                            cell.setCellStyle(styleF4);
                        // else if(decwidth==5)
                        // cell.setCellStyle(styleF5);
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(rset.getDouble(fieldName));
                        if (fieldName.equals(onlyname + "_" + onlyflag + ""))
                            onlynamemap.put(onlyname, onlynamemap.get(onlyname) + "," + rset.getDouble(fieldName));
                    } else if ("D".equals(itemtype)) {
                        cell.setCellStyle(style1);

                        Date date = rset.getDate(fieldName);
                        if (date == null) {
                            cell.setCellValue("");
                        } else {

                            String value = DateUtils.format(date, "yyyy-MM-dd");
                            cell.setCellValue(new HSSFRichTextString(value));
                            if (fieldName.equals(onlyname + "_" + onlyflag + ""))
                                onlynamemap.put(onlyname, onlynamemap.get(onlyname) + "," + value);
                        }

                    } else if ("M".equalsIgnoreCase(itemtype)) {
                        cell.setCellStyle(styleN);
                        // 判断数据字典里的指标类型
                        FieldItem item = DataDictionary.getFieldItem("" + fieldName.substring(0, fieldName.indexOf("_")));
                        if (item != null && item.getItemtype() != null) {
                            if ("M".equalsIgnoreCase(item.getItemtype())) {
                                String value = Sql_switcher.readMemo(rset, fieldName);
                                cell.setCellValue(new HSSFRichTextString(value));
                            } else if ("D".equalsIgnoreCase(item.getItemtype())) {
                                /** yyyy-MM-dd */
                                String str = Sql_switcher.readMemo(rset, fieldName);
                                cell.setCellValue(new HSSFRichTextString(str.replace("`", "\r\n")));
                            } else if ("N".equalsIgnoreCase(item.getItemtype())) {
                                String str = Sql_switcher.readMemo(rset, fieldName);
                                cell.setCellValue(new HSSFRichTextString(str.replace("`", "\r\n")));

                            } else {// 这个里面剩余的是字符型和代码型
                                // if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
                                String str = Sql_switcher.readMemo(rset, fieldName);
                                String values = "";
                                if (str.indexOf("`") != -1) {// 判断是不是字符型
                                    String[] strs = str.split("`");
                                    for (int j = 0; j < strs.length; j++) {
                                        if (strs[j].trim().length() > 0) {
                                            if (codesetid != null && !"0".equals(codesetid))
                                                values += AdminCode.getCodeName(codesetid, strs[j]);
                                            else
                                                values += strs[j];
                                            if (j < strs.length - 1) {
                                                values += "`";
                                            }
                                        }
                                    }
                                } else {// 代码型
                                    if (codesetid != null && !"0".equals(codesetid))
                                        values = AdminCode.getCodeName(codesetid, str);
                                    else
                                        values = str;
                                }
                                cell.setCellValue(new HSSFRichTextString(values.replace("`", "\r\n")));
                            }
                        }

                    } else {
                        String value = rset.getString(fieldName);
                        if (fieldName.equals(onlyname + "_" + onlyflag + ""))
                            onlynamemap.put(onlyname, onlynamemap.get(onlyname) + "," + value);
                        if (value != null) {
                            String codevalue = value;
                            if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))// 如果是代码型
                                value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
                            if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                                if (value.length() == 0) {
                                    if ("UM".equalsIgnoreCase(codesetid)) {
                                        value = AdminCode.getCode("UN", codevalue) != null ? AdminCode.getCode("UN", codevalue).getCodename() : "";
                                    }
                                }
                                if (codevalue != null && codevalue.trim().length() > 0)
                                    cell.setCellValue(new HSSFRichTextString(codevalue + ":" + value));
                                else
                                    cell.setCellValue(new HSSFRichTextString(value));

                            } else {
                                if (this.getBo() != null && this.getBo().getInfor_type() != 1 && fieldName.startsWith("codesetid_")) {
                                    if ("UM".equals(value))
                                        value = "UM:部门";
                                    if ("UN".equals(value))
                                        value = "UN:单位";
                                }
                                cell.setCellValue(new HSSFRichTextString(value));
                            }

                        }
                        cell.setCellStyle(style1);
                    }

                }
            }
            for (int i = rowCount; i < 1000; i++) {
                row = sheet.createRow(rowCount++);
                for (int j = 0; j < fieldlist.size(); j++) {
                    FieldItem field = (FieldItem) fieldlist.get(j);
                    String itemtype = field.getItemtype();
                    int decwidth = field.getDecimalwidth();

                    if ("N".equals(itemtype)) {
                    } else if ("D".equals(itemtype)) {
                        cell = row.createCell((short) (onlyflag2 + j));
                        cell.setCellStyle(styles);
                    } else {
                        cell = row.createCell((short) (onlyflag2 + j));
                        cell.setCellStyle(styles);
                    }

                }
            }
            /** 取总记录的条数加上2000行作为代码类开始部署,xcs 2014-10-17* */
            int recordCount = rowCount + 2000;
            rowCount--;
            rowCount = 1000;// 默认设置1000行代码型指标有下拉框
            int index = 0;
            String[] lettersUpper = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
            for (int n = 0; n < codeCols.size(); n++) {
                ArrayList list = new ArrayList();
                int m = recordCount;
                String codeCol = (String) codeCols.get(n);
                String[] temp = codeCol.split(":");
                String codesetid = temp[0];
                int codeCol1 = Integer.valueOf(temp[1]).intValue();
                String filename = temp[2];
                String rangecond = "";// /变化后的单位、部门、岗位，可以按管理范围控制
                if (this.getBo() != null && this.getBo().getInfor_type() == 1 && ("b0110_2".equalsIgnoreCase(filename) || "e0122_2".equalsIgnoreCase(filename) || "E01A1_2".equalsIgnoreCase(filename))) {
                    rangecond = isPriv_ctrl(cell_param_map, filename);
                }
                /** xcs modify resolve organization no code in Excel begain* */
                if ("codesetid".equalsIgnoreCase(codesetid)) {
                    LazyDynaBean orgbean1 = new LazyDynaBean();
                    orgbean1.set("value", "UM:部门");
                    LazyDynaBean orgbean2 = new LazyDynaBean();
                    orgbean2.set("value", "UN:单位");
                    list.add(orgbean2);
                    list.add(orgbean1);
                }
                /** xcs modify resolve organization no code in Excel end* */
                else {
                    StringBuffer codeBuf = new StringBuffer();
                    StringBuffer contrlBuf = new StringBuffer();
                    if (!"UM".equalsIgnoreCase(codesetid) && !"UN".equalsIgnoreCase(codesetid) && !"@k".equalsIgnoreCase(codesetid))// 如果不是单位、部门或岗位
                    {
                        codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid.toUpperCase() + "' and invalid='1'"); // 加上invalid='1'将设置为无效的指标过滤掉
                                                                                                                                            // liuzy
                                                                                                                                            // 20150730
                        rset = dao.search(codeBuf.toString());
                        if (rset.next()) {
                            codeBuf.setLength(0);/*
                                                     * xcs 尝试用递归方法
                                                     * 显示导出Excel中代码类的层级问题
                                                     */
                            // codeBuf.append("select
                            // codesetid,codeitemid,codeitemdesc,layer grade
                            // from codeitem where codesetid='" +
                            // codesetid.toUpperCase() + "' order by
                            // a0000,codeitemid ");
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,layer grade from codeitem where codesetid='" + codesetid.toUpperCase() + "'and codeitemid=parentid and invalid='1' and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                            contrlBuf.append("select count(*) from codeitem where codesetid='" + codesetid.toUpperCase() + "' and invalid='1' and " + Sql_switcher.sqlNow() + " between start_date and end_date");

                        }
                    } else {// 如果是单位、部门或岗位
                        if (!"UN".equalsIgnoreCase(codesetid)) {// 如果是部门或岗位
                            if (this.getBo() != null && this.getBo().getInfor_type() != 1 && "parentid_2".equalsIgnoreCase(filename)) {
                                codeBuf.append("select count(*) from organization where codesetid<>'@K' " + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                            } else {
                                codeBuf.append("select count(*) from organization where codesetid='" + codesetid.toUpperCase() + "'" + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                            }
                            rset = dao.search(codeBuf.toString());
                            if (rset.next()) {
                                codeBuf.setLength(0);
                                if (this.getBo() != null && this.getBo().getInfor_type() != 1 && "parentid_2".equalsIgnoreCase(filename)) {/*
                                                                                                                                             * xcs
                                                                                                                                             * 尝试用递归方法
                                                                                                                                             * 显示导出Excel中代码类的层级问题
                                                                                                                                             */
                                    if ("".equals(rangecond)) {
                                        codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid<>'@K' and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                    } else {
                                        codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid<>'@K' " + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                    }
                                    contrlBuf.append("select count(*) from organization where codesetid<>'@k' " + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                                } else {
                                    if ("UM".equalsIgnoreCase(codesetid)) {
                                        if ("".equals(rangecond)) {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        } else {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM') " + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        }
                                        contrlBuf.append("select count(*) from organization where codesetid in ('UN','UM') " + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                                    } else {
                                        if ("".equals(rangecond)) {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM','@K') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        } else {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM','@K') " + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        }
                                        contrlBuf.append("select count(*) from organization where codesetid in ('UN','UM','@K') " + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                                    }
                                }
                            }
                        } else if ("UN".equalsIgnoreCase(codesetid)) {// 如果是单位
                            codeBuf.append("select count(*) from organization where codesetid='UN'" + rangecond);
                            rset = dao.search(codeBuf.toString());
                            if (rset.next()) {
                                codeBuf.setLength(0);
                                if ("".equals(rangecond)) {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid='UN' and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid");
                                } else {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid='UN'" + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid");
                                }
                                contrlBuf.append("select count(*) from organization where codesetid='UN' " + rangecond + " and " + Sql_switcher.sqlNow() + " between start_date and end_date");
                            }
                        }
                    }
                    rset = dao.search(contrlBuf.toString());
                    if (rset.next()) {
                        int countNum = rset.getInt(1);
                        if (countNum > 15000) {// 原设定组织机构大于10000行不导出代码行，现改为15000行，bug号：【15718】，gaohy.2016-1-9
                            continue;
                        }
                    }
                    rset = dao.search(codeBuf.toString());

                    while (rset.next()) {
                        // row = sheet.getRow(m + 0);
                        // if(row==null)
                        // row = sheet.createRow(m + 0);
                        // cell = row.createCell((short) (208+index));
                        /*------------------------xcs modify start--------------------------*/
                        // String setid=rset.getString("codesetid");
                        String itemid = rset.getString("codeitemid");
                        String itemdesc = rset.getString("codeitemdesc");
                        StringBuffer sb = new StringBuffer();
                        String strgrade = rset.getString("grade") == null ? "1" : rset.getString("grade");
                        int grade = Integer.parseInt(strgrade);
                        for (int i = 1; i < grade; i++) {
                            sb.append("　");
                        }
                        LazyDynaBean bean = new LazyDynaBean();
                        if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                            bean.set("value", sb.toString() + itemid + ":" + itemdesc);
                        } else {
                            bean.set("value", sb.toString() + itemdesc);
                        }
                        list.add(bean);
                        rangecond = "";
                        list = FindChild(itemid, list, codesetid, rangecond);
                        /*------------------------xcs modify end--------------------------*/
                        // StringBuffer sb=new StringBuffer();
                        // String
                        // strgrade=rset.getString("grade")==null?"1":rset.getString("grade");
                        // int grade = Integer.parseInt(strgrade);
                        // for(int i=1;i<grade;i++){
                        // sb.append("--");//xcs测试
                        // }
                        // if(codesetid.equalsIgnoreCase("UN")||codesetid.equalsIgnoreCase("UM")||codesetid.equalsIgnoreCase("@K")){
                        // cell.setCellValue(new
                        // HSSFRichTextString(sb.toString()+rset.getString("codeitemid")+":"+rset.getString("codeitemdesc")));
                        // }else{
                        // cell.setCellValue(new
                        // HSSFRichTextString(sb.toString()+rset.getString("codeitemdesc")));
                        // }
                        // m++;
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    row = sheet.getRow(m + 0);
                    if (row == null)
                        row = sheet.createRow(m + 0);
                    // cell = row.createCell((short) (208+index));
                    if (i == 0) {
                        cell = row.createCell(0);
                        cell.setCellValue(new HSSFRichTextString("HJSJCODEROW"));
                    }
                    cell = row.createCell(26 + index);
                    LazyDynaBean bean = (LazyDynaBean) list.get(i);
                    String cellValue = (String) bean.get("value");
                    cell.setCellValue(new HSSFRichTextString(cellValue));
                    m++;
                }

                // sheet.setColumnWidth((short)(208+index), (short)0);
                // sheet.setColumnWidth(208+index, 0);这里的宽度就不设置了免得影响上面的数据
                if (m > recordCount && filename.indexOf("_1") == -1) {// 如果
                                                                        // 代码类有代码
                                                                        // 并且
                                                                        // 指标是变化后的
                                                                        // 那么需要设置让Excel有下拉列表
                    int div = 0;
                    int mod = 0;
                    div = index / 26;
                    mod = index % 26;// 最多的变化后代码类为AA~ZZ,在多出来的就没法处理了
                    String strFormula = "$" + lettersUpper[div] + "" + lettersUpper[mod] + "$" + recordCount + ":$" + lettersUpper[div] + "" + lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
                    // 设置数据有效性加载在哪个单元格上。
                    // 四个参数分别是：起始行、终止行、起始列、终止列
                    CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);
                    // CellRangeAddressList addressList = new
                    // CellRangeAddressList();
                    // 加载下拉列表内容
                    DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                    // 数据有效性对象
                    HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                    dataValidation.setSuppressDropDownArrow(false);
                    sheet.addValidationData(dataValidation);
                }
                index++;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
    /**
     * 获得模板记录子集信息excel表头 <?xml version="1.0" encoding="GB2312"?> <records
     * columns="a0415`a0430`a0435`a0410`a0405`a0440"> <record
     * I9999="1">`2009-03-01`法国勒阿弗尔大学`电力系统及自动化`11`</record></records>
     * 
     * @return
     */
    public void getSubSetTableExcelTitle(String sub_domain, HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleCol0_title, ArrayList fieldlist, HSSFCellStyle style2, ArrayList codeCols, String onlyname) {
        try {

            sub_domain = SafeCode.decode(sub_domain);
            TSubSetDomain setDomain = new TSubSetDomain(sub_domain);
            String fields = setDomain.getFields();
            String[] temps = fields.split("`");
            FieldItem item = null;
            item = DataDictionary.getFieldItem(onlyname);
            if (item != null) {
                fieldlist.add(item);
            }
            for (int i = 0; i < temps.length; i++) {
                if (temps[i].trim().length() == 0)
                    continue;

                item = DataDictionary.getFieldItem(temps[i]);
                if (item != null) {
                    fieldlist.add(item);
                }
            }
            sheet.setColumnWidth((short) 0, (short) 0);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
            HSSFPatriarch patr = sheet.createDrawingPatriarch();

            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = null;
            HSSFComment comm = null;

            cell = row.createCell((short) 0);
            cell.setCellValue(cellStr("i9999标识"));
            cell.setCellStyle(styleCol0_title);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));
            comm.setString(new HSSFRichTextString("i9999"));
            cell.setCellComment(comm);

            String fieldExplain = "";
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (field == null)
                    continue;

                String fieldName = field.getItemid().toLowerCase();
                String fieldLabel = field.getItemdesc();

                if (field.isFillable())
                    fieldLabel += "*";
                sheet.setColumnWidth((short) (i + 1), (short) 3500);
                if ("UN".equalsIgnoreCase(field.getCodesetid()) || "UM".equalsIgnoreCase(field.getCodesetid()) || "@k".equalsIgnoreCase(field.getCodesetid()))
                    sheet.setColumnWidth((short) (i + 1), (short) 5000);

                cell = row.createCell((short) (i + 1));

                cell.setCellValue(cellStr(fieldLabel));
                cell.setCellStyle(style2);
                comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
                comm.setString(new HSSFRichTextString(fieldName));
                cell.setCellComment(comm);
                if (!"0".equals(field.getCodesetid()))
                    codeCols.add(field.getCodesetid() + ":" + new Integer(i + 1).toString() + ":" + fieldName);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSubSetTableExcelBody(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleCol0_title, ArrayList fieldlist, HSSFCellStyle styleN, HSSFCellStyle styleF1, HSSFCellStyle styleF2, HSSFCellStyle styleF3, HSSFCellStyle styleF4, HSSFCellStyle style1, HSSFCellStyle styles, StringBuffer strsql, String table_name, HSSFCellStyle styleCol0, ArrayList codeCols, HashMap cell_param_map, String fieldset, String onlyname, HashMap onlynamemap, String onlyflag, String wherestr, ArrayList fieldlist2) {

        HSSFRow row = null;
        HSSFCell cell = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            RowSet rset = null;
            int rowCount = 1;
            if (onlynamemap == null && onlynamemap.get(onlyname) == null)
                return;
            String onlynames = (String) onlynamemap.get(onlyname);
            String[] onlys = onlynames.split(",");
            String names = "";
            for (int i = 0; i < onlys.length; i++) {
                if (onlys[i].trim().length() > 0) {
                    names += "'" + onlys[i].trim() + "',";
                }
            }
            short onlyflag2 = 2;
            if (names.length() > 0)
                names = names.substring(0, names.length() - 1);
            if (names.length() >= 1) {
                // return;
                if (wherestr.indexOf("where") != -1) {
                    wherestr = " and " + wherestr.substring(wherestr.indexOf("where") + 5, wherestr.length());
                }

                String sql = "select t_" + fieldset + "_2," + onlyname + "_" + onlyflag + "  from " + table_name + " where " + onlyname + "_" + onlyflag + " in (" + names + ") " + wherestr;
                rset = dao.search(sql);

                while (rset.next()) {

                    String str2 = Sql_switcher.readMemo(rset, "t_" + fieldset + "_2");
                    String onlyvalue = rset.getString(onlyname + "_" + onlyflag + "");
                    if (str2 == null || str2.trim().length() == 0)
                        continue;
                    Document doc = null;
                    Element element = null;
                    doc = PubFunc.generateDom(str2);;

                    String xpath = "/records";

                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    List childlist = findPath.selectNodes(doc);
                    String columns = "";
                    if (childlist != null && childlist.size() > 0) {
                        element = (Element) childlist.get(0);
                        columns = element.getAttributeValue("columns");
                    }

                    String[] temps = columns.split("`");
                    xpath = "/records/record";
                    findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    childlist = findPath.selectNodes(doc);
                    if (childlist != null && childlist.size() > 0) {
                        int m = 0;

                        for (int j = 0; j < childlist.size(); j++) {
                            row = sheet.createRow(rowCount++);
                            element = (Element) childlist.get(j);
                            // String state =
                            // element.getAttributeValue("state");
                            // if(state!=null&&state.equalsIgnoreCase("D"))
                            // continue;
                            String I9999 = element.getAttributeValue("I9999");

                            cell = row.createCell((short) 0);
                            cell.setCellValue(cellStr(I9999));
                            cell.setCellStyle(styleCol0);

                            // cell = row.createCell((short) 1);
                            // cell.setCellValue(cellStr(onlyvalue));
                            // cell.setCellStyle(styleCol0);
                            String context = element.getText();
                            String[] _temps = context.split("`");
                            int d = 0;
                            for (int i = 0; i < fieldlist2.size(); i++) {
                                FieldItem field = (FieldItem) fieldlist2.get(i);
                                if (field == null) {
                                    d++;
                                    continue;
                                }

                                String fieldName = field.getItemid().toLowerCase();
                                String itemtype = field.getItemtype();
                                int decwidth = field.getDecimalwidth();
                                String codesetid = field.getCodesetid();
                                int itemlength = field.getItemlength();
                                cell = row.createCell((short) (onlyflag2 + i - d - 1));
                                int n = 0;
                                String value = "";

                                for (; n < temps.length; n++) {
                                    if (fieldName.equalsIgnoreCase(temps[n]) && _temps.length > n) {
                                        value = _temps[n];
                                        break;
                                    }
                                }
                                if (fieldName.equalsIgnoreCase(onlyname)) {
                                    value = onlyvalue;
                                }

                                if ("N".equals(itemtype)) {
                                    if (decwidth == 0)
                                        cell.setCellStyle(styleN);
                                    else if (decwidth == 1)
                                        cell.setCellStyle(styleF1);
                                    else if (decwidth == 2)
                                        cell.setCellStyle(styleF2);
                                    else if (decwidth == 3)
                                        cell.setCellStyle(styleF3);
                                    else if (decwidth == 4)
                                        cell.setCellStyle(styleF4);
                                    // else if(decwidth==5)
                                    // cell.setCellStyle(styleF5);
                                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue(value);
                                } else if ("D".equals(itemtype)) {
                                    cell.setCellStyle(style1);
                                    cell.setCellValue(new HSSFRichTextString(value));
                                } else if ("M".equalsIgnoreCase(itemtype)) {
                                    cell.setCellStyle(styleN);
                                    cell.setCellValue(new HSSFRichTextString(value));

                                } else {
                                    if (value != null) {
                                        String codevalue = value;
                                        if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
                                            value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
                                        if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                                            if (value.length() == 0) {
                                                if ("UM".equalsIgnoreCase(codesetid)) {
                                                    value = AdminCode.getCode("UN", codevalue) != null ? AdminCode.getCode("UN", codevalue).getCodename() : "";
                                                }
                                            }
                                            if (codevalue != null && codevalue.trim().length() > 0)
                                                cell.setCellValue(new HSSFRichTextString(codevalue + ":" + value));
                                            else
                                                cell.setCellValue(new HSSFRichTextString(value));

                                        } else {
                                            if (this.getBo() != null && this.getBo().getInfor_type() != 1 && fieldName.startsWith("codesetid_")) {
                                                if ("UM".equals(value))
                                                    value = "部门";
                                                if ("UN".equals(value))
                                                    value = "单位";
                                            }
                                            cell.setCellValue(new HSSFRichTextString(value));
                                        }
                                    }
                                    cell.setCellStyle(style1);
                                }

                            }
                        }

                    }
                }
            }
            for (int i = rowCount; i < 1000; i++) {
                row = sheet.createRow(rowCount++);
                int d = 0;
                for (int j = 0; j < fieldlist2.size(); j++) {
                    FieldItem field = (FieldItem) fieldlist2.get(j);
                    if (field == null) {
                        d++;
                        continue;
                    }
                    String itemtype = field.getItemtype();
                    int decwidth = field.getDecimalwidth();
                    cell = row.createCell((short) (onlyflag2 + j - d - 1));
                    if ("N".equals(itemtype)) {
                        if (decwidth == 0)
                            cell.setCellStyle(styleN);
                        else if (decwidth == 1)
                            cell.setCellStyle(styleF1);
                        else if (decwidth == 2)
                            cell.setCellStyle(styleF2);
                        else if (decwidth == 3)
                            cell.setCellStyle(styleF3);
                        else if (decwidth == 4)
                            cell.setCellStyle(styleF4);

                    } else if ("D".equals(itemtype)) {
                        cell.setCellStyle(style1);
                        // cell.setCellStyle(styles);
                    } else {
                        cell.setCellStyle(style1);
                    }

                }
            }
            int recordCount = rowCount + 2000;
            rowCount--;
            rowCount = 1000;// 默认设置1000行代码型指标有下拉框
            int index = 0;
            String[] lettersUpper = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
            for (int n = 0; n < codeCols.size(); n++) {
                ArrayList list = new ArrayList();
                int m = recordCount;
                String codeCol = (String) codeCols.get(n);
                String[] temp = codeCol.split(":");
                String codesetid = temp[0];
                int codeCol1 = Integer.valueOf(temp[1]).intValue();
                String filename = temp[2];
                String rangecond = ""; // 变化后的单位、部门、岗位，可以按管理范围控制
                if (this.getBo() != null && this.getBo().getInfor_type() == 1 && ("b0110_2".equalsIgnoreCase(filename) || "e0122_2".equalsIgnoreCase(filename) || "E01A1_2".equalsIgnoreCase(filename))) {
                    rangecond = isPriv_ctrl(cell_param_map, filename);
                }
                if ("codesetid".equalsIgnoreCase(codesetid)) {
                    LazyDynaBean orgbean1 = new LazyDynaBean();
                    orgbean1.set("value", "UM:部门");
                    LazyDynaBean orgbean2 = new LazyDynaBean();
                    orgbean2.set("value", "UN:单位");
                    list.add(orgbean2);
                    list.add(orgbean1);
                } else {
                    StringBuffer codeBuf = new StringBuffer();
                    if (!"UM".equalsIgnoreCase(codesetid) && !"UN".equalsIgnoreCase(codesetid) && !"@k".equalsIgnoreCase(codesetid))// 如果不是单位、部门或岗位
                    {
                        codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid.toUpperCase() + "'");
                        rset = dao.search(codeBuf.toString());
                        if (rset.next()) {
                            codeBuf.setLength(0);
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,layer grade from codeitem where codesetid='" + codesetid.toUpperCase() + "' order by a0000,codeitemid ");
                        }
                    } else {// 如果是单位、部门或岗位
                        if (!"UN".equalsIgnoreCase(codesetid)) {// 如果是部门或岗位
                            if (this.getBo() != null && this.getBo().getInfor_type() != 1 && "parentid_2".equalsIgnoreCase(filename)) {
                                codeBuf.append("select count(*) from organization where codesetid<>'@K' " + rangecond + " ");
                            } else {
                                codeBuf.append("select count(*) from organization where codesetid='" + codesetid.toUpperCase() + "'" + rangecond);
                            }
                            rset = dao.search(codeBuf.toString());
                            if (rset.next()) {
                                codeBuf.setLength(0);
                                if (this.getBo() != null && this.getBo().getInfor_type() != 1 && "parentid_2".equalsIgnoreCase(filename)) {
                                    if ("".equals(rangecond)) {
                                        codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid<>'@K' and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                    } else {
                                        codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid<>'@K' " + rangecond + " and  " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                    }
                                } else {
                                    if ("UM".equalsIgnoreCase(codesetid)) {
                                        if ("".equals(rangecond)) {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        } else {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM') " + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        }
                                    } else {
                                        if ("".equals(rangecond)) {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM','@K') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        } else {
                                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid in ('UN','UM','@K') " + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ");
                                        }
                                    }
                                }
                            }
                        } else if ("UN".equalsIgnoreCase(codesetid)) {// 如果是单位
                            codeBuf.append("select count(*) from organization where codesetid='UN'" + rangecond);
                            rset = dao.search(codeBuf.toString());
                            if (rset.next()) {
                                codeBuf.setLength(0);
                                if ("".equals(rangecond)) { // liuzy 20151113
                                                            // 管理范围操作时不应模糊时，不应
                                                            // codeitemid=parentid，那只适应于顶级节点。
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid='UN' and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid");
                                } else {
                                    codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade grade from organization where codesetid='UN'" + rangecond + "  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid");
                                }
                            }
                        }
                    }
                    rset = dao.search(codeBuf.toString());

                    while (rset.next()) {
                        String itemid = rset.getString("codeitemid");
                        String itemdesc = rset.getString("codeitemdesc");
                        StringBuffer sb = new StringBuffer();
                        String strgrade = rset.getString("grade") == null ? "1" : rset.getString("grade");
                        int grade = Integer.parseInt(strgrade);
                        for (int i = 1; i < grade; i++) {
                            sb.append("　");
                        }
                        LazyDynaBean bean = new LazyDynaBean();
                        if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                            bean.set("value", sb.toString() + itemid + ":" + itemdesc);
                        } else {
                            bean.set("value", sb.toString() + itemdesc);
                        }
                        list.add(bean);
                        rangecond = "";
                        list = FindChild(itemid, list, codesetid, rangecond);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    row = sheet.getRow(m + 0);
                    if (row == null)
                        row = sheet.createRow(m + 0);
                    // cell = row.createCell((short) (208+index));
                    if (i == 0) {
                        cell = row.createCell(0);
                        cell.setCellValue(new HSSFRichTextString("HJSJCODEROW"));
                    }
                    cell = row.createCell(26 + index);
                    LazyDynaBean bean = (LazyDynaBean) list.get(i);
                    String cellValue = (String) bean.get("value");
                    cell.setCellValue(new HSSFRichTextString(cellValue));
                    m++;
                }
                sheet.setColumnWidth((short) (208 + index), (short) 0);

                if (m > recordCount && filename.indexOf("_1") == -1) {// 如果
                                                                        // 代码类有代码
                                                                        // 并且
                                                                        // 指标是变化后的
                                                                        // 那么需要设置让Excel有下拉列表
                    int div = 0;
                    int mod = 0;
                    div = index / 26;
                    mod = index % 26;// 最多的变化后代码类为AA~ZZ,在多出来的就没法处理了
                    String strFormula = "$" + lettersUpper[div] + "" + lettersUpper[mod] + "$" + recordCount + ":$" + lettersUpper[div] + "" + lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
                    // 设置数据有效性加载在哪个单元格上。
                    // 四个参数分别是：起始行、终止行、起始列、终止列
                    CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);
                    // CellRangeAddressList addressList = new
                    // CellRangeAddressList();
                    // 加载下拉列表内容
                    DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                    // 数据有效性对象
                    HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                    dataValidation.setSuppressDropDownArrow(false);
                    sheet.addValidationData(dataValidation);
                }
                index++;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public String[] importMainExcel(Sheet sheet, String table_name, HashMap nameMap, HashMap name2map, HashMap map,
			HashMap codeColMap, ArrayList listxuhao, ArrayList insertlist, HashMap tablemap, StringBuffer errorStr,
			ArrayList list2, int updateFidsCount, StringBuffer sql, StringBuffer insertsql, String onlyflag,
			String onlyname, int onlynamesit, HashMap onlynameMap, File form_file, HashMap codeItemColMap,
			ArrayList codeLeafSetList, ArrayList filedUpdateList) {
		return this.importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist,
				tablemap, errorStr, list2, updateFidsCount, sql, insertsql, onlyflag, onlyname, onlynamesit,
				onlynameMap, form_file, codeItemColMap, codeLeafSetList, new ArrayList(), "", new HashMap(), null,
				filedUpdateList, "");
	}
	/**
	 * 改造为只传file_name,filename 只用于生成提示excle
	 */
	public String[] importMainExcel(Sheet sheet, String table_name, HashMap nameMap, HashMap name2map, HashMap map,
			HashMap codeColMap, ArrayList listxuhao, ArrayList insertlist, HashMap tablemap, StringBuffer errorStr,
			ArrayList list2, int updateFidsCount, StringBuffer sql, StringBuffer insertsql, String onlyflag,
			String onlyname, int onlynamesit, HashMap onlynameMap, String file_name, HashMap codeItemColMap,
			ArrayList codeLeafSetList, ArrayList mainImmportList, String onlyKey, HashMap chidlFatherMap,
			TemplateParam paramBo, ArrayList filedUpdateList, String ins_id) {
        String errorFileName = "";// 生成提示excel
        int updateCount=0;//更新成功的人数
        int importCount=0;//引入成功的人数
        try {
        	TemplateBo templateBo = new TemplateBo(this.conn, this.userview, Integer.parseInt(tabid));
            StringBuffer cascadeerror = new StringBuffer("");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Row row = sheet.getRow(0);
            HashMap standMap = new HashMap();// xcs 尝试 将唯一性标识放到提示信息中去
            int cols = row.getPhysicalNumberOfCells();// 总列数
            int rows = sheet.getPhysicalNumberOfRows();// 总行数
            RowSet rset = null;
            RowSet rssql = null;
            int num = 0;
            int num2 = 1;
            String onlynamedesc = DataDictionary.getFieldItem(onlyname).getItemdesc();
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            StringBuffer strerror = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.conn);
            //取得权限下的人员库 start
            ArrayList onlybase = new ArrayList();// 权限库
            String initbase="";
    		if(paramBo!=null){
    			initbase=paramBo.getInit_base();
    		}
        	if(userview.isSuper_admin()){
        		if(StringUtils.isBlank(initbase)){
	        		rset = dao.search("select Pre from DBName");
	                while (rset.next()) {
	                    onlybase.add(rset.getString("Pre"));
	                }
        		}else{
        			onlybase.add(initbase);
        		}
        	}else{
        		String onglydb = userview.getDbpriv().toString();// 获取该用户权限下的人员库
        		if(StringUtils.isBlank(initbase)){
	        		String[] onlyarray = null;
	                if (onglydb.length() > 0) {
	                    onlyarray = onglydb.substring(1).split(",");
	                    for (int i = 0; i < onlyarray.length; i++) {
	                    		onlybase.add(onlyarray[i]);
	                    }
	                }
        		}else{
        			if((","+onglydb+",").toUpperCase().indexOf(","+initbase.toUpperCase()+",")!=-1){
        				onlybase.add(initbase);
        			};
        		}
        	}
        	//取得权限下的人员库 end
        	//找出失效的组织机构 start
        	ArrayList invalidCodeList  = new ArrayList();
        	if(this.operationtype==5) {
	        	Timestamp dateTime = new Timestamp((new Date()).getTime());
	    		ArrayList param = new ArrayList();
	    		param.add(dateTime);
	        	rset = dao.search("select codeitemid from organization where end_date<=?",param);
	        	while(rset.next()) {
	        		String codeitemid = rset.getString("codeitemid");
	        		invalidCodeList.add(codeitemid);
	        	}
        	}
        	//找出失效的组织机构 end
        	//获得a0000最大值,用于导入新增 start
        	int max_a0000 = 0;
        	if(!table_name.startsWith("templet")) {
        		if (this.operationtype == 0 || this.operationtype == 5) {
        			rset = dao.search("select " + Sql_switcher.isnull("max(a0000)", "0") + "+1 from " + table_name);
        			if (rset.next())
        				max_a0000=rset.getInt(1);
        		}
        	}
        	//获得a0000最大值,用于导入新增 end
            // 获取模版中已存在的人员列表（唯一标识）
            ArrayList onlylist = new ArrayList();
            if (!"no".equals(onlyname) && !"0".equals(onlyflag) && this.operationtype != 0 && this.operationtype != 5) {
                String onlyfld = onlyname + "_1";// 根据唯一标识获得数据库字段
                DbWizard dbw = new DbWizard(this.conn);
                if (!dbw.isExistField(table_name, onlyfld, false)) {// 判断数据库表中是否存在该字段
                    onlyfld = onlyname + "_2";
                    if (!dbw.isExistField(table_name, onlyfld, false)) {
                        return new String[]{errorFileName,String.valueOf(updateCount),String.valueOf(importCount)};
                    }
                }

                rset = dao.search("select " + onlyfld + " from " + table_name);
                while (rset.next() && rset.getString(onlyfld) != null) {
                    onlylist.add(rset.getString(onlyfld).toString());
                }
            }
            String dest_base = this.bo.getDest_base();// 得到人员库
            ArrayList dbList = DataDictionary.getDbpreList();// 所有人员库
            for (int i = 0; i < dbList.size(); i++) {
                String pre = (String) dbList.get(i);
                if (pre.equalsIgnoreCase(dest_base)) {
                    dest_base = pre;
                    break;
                }
            }
            ArrayList importSuccess = new ArrayList();// 导入成功的行号
            ArrayList alreadyExists = new ArrayList();// 已经存在的行号
            ArrayList countAll = new ArrayList();// 所有行的行号
            ArrayList importFaild = new ArrayList();// 导入失败的行号
            ArrayList A0100list = new ArrayList();// 存在且在权限下的模版新增人员
            HashSet hashpre = new HashSet();// 新增数据的所有人员库前缀,去重
            for (int j = 1; j < rows; j++) {

                int num3 = 0;
                int num4 = 0;
                ArrayList list = new ArrayList();
                row = sheet.getRow(j);
                if (row == null)
                    row = sheet.createRow(j);

                Cell flagCol = null;
                Cell codeCol = row.getCell(0);// 得到代码行的第一列,这里面存有特殊的字段HJSJCODEROW
                if (codeCol != null) {
                    String value = codeCol.getRichStringCellValue().toString();
                    if (value != null && value.trim().length() > 0 && "HJSJCODEROW".equals(value)) {
                        break;
                    }
                }
                if ("0".equals(onlyflag)) {// 如果系统中没有唯一性指标 或者说 系统中有唯一性指标
                                            // ，而当前导入的模版中没有包含唯一性指标，那么主集中的主键标识是被隐藏的
                    flagCol = row.getCell((short) 0);
                } else {
                    flagCol = row.getCell((short) onlynamesit);
                }
                // 遇到整个行都是空的截止
                boolean flag = false;// 判断是否有单元格不为null
                if ("0".equals(onlyflag)) {
                    if (flagCol != null) {

                        if (table_name.startsWith("templet")) {// 审批状态
                            if (this.operationtype != 0 && this.operationtype != 5) {// ==0是调入模板
                                                                                        // 需要特别处理
                                                                                        // 如没主键，就新增
                                switch (flagCol.getCellType()) {
                                    case Cell.CELL_TYPE_BLANK :
                                        errorStr.append("主键标识串列存在空数据，导入数据失败！");
                                        throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
                                    case Cell.CELL_TYPE_STRING :
                                        if (flagCol.getRichStringCellValue().toString().trim().length() == 0) {
                                            errorStr.append("主键标识串列存在空数据，导入数据失败！");
                                            throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
                                        }
                                }
                                String[] temp = flagCol.getStringCellValue().split("\\|");
                                if ((this.bo.getInfor_type() == 1 && temp.length != 4) || (this.bo.getInfor_type() != 1 && temp.length != 3))
                                    continue;
                                if ((this.bo.getInfor_type() == 1 && (temp[0].trim().length() == 0 || temp[1].trim().length() == 0 || temp[2].trim().length() == 0 || temp[3].trim().length() == 0)) || (this.bo.getInfor_type() != 1 && (temp[0].trim().length() == 0 || temp[1].trim().length() == 0 || temp[2].trim().length() == 0)))
                                    continue;
                            } else {

                            }

                        } else {

                            if (this.operationtype != 0 && this.operationtype != 5) {// ==0是调入模板
                                                                                        // 需要特别处理
                                                                                        // 如没主键，就新增
                                switch (flagCol.getCellType()) {
                                    case Cell.CELL_TYPE_BLANK :
                                        errorStr.append("主键标识串列存在空数据，导入数据失败！");
                                        throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
                                    case Cell.CELL_TYPE_STRING :
                                        if (flagCol.getRichStringCellValue().toString().trim().length() == 0) {
                                            errorStr.append("主键标识串列存在空数据，导入数据失败！");
                                            throw new GeneralException("主键标识串列存在空数据，导入数据失败！");
                                        }
                                }
                                String[] temp = flagCol.getStringCellValue().split("\\|");
                                if ((this.bo.getInfor_type() == 1 && temp.length != 2) || (this.bo.getInfor_type() != 1 && temp.length != 1))
                                    continue;

                                if ((this.bo.getInfor_type() == 1 && (temp[0].trim().length() == 0 || temp[1].trim().length() == 0)) || (this.bo.getInfor_type() != 1 && temp[0].trim().length() == 0))
                                    continue;
                                /**
                                 * 模版里没有唯一标识的时候，按隐藏的主键标识导入数据 gaohy
                                 */
                                if (temp.length >= 2 && "0".equals(onlyflag)&&(tablemap == null||(tablemap != null && ((this.bo.getInfor_type() == 1 && tablemap.get(temp[1]) == null) || (this.bo.getInfor_type() != 1 && tablemap.get(temp[0]) == null))))) {// 模版中没有唯一性标识，按隐藏主键来新增来 且这个人不再单据中
                                    A0100list.add(temp[1] + "|" + temp[0]);// 添加所有符合权限的新增数据
                                    hashpre.add(temp[0]);// 所有新增数据包含的库
                                }else if(this.bo.getInfor_type() != 1&&onlyflag.equals("0")&&(tablemap == null||(tablemap != null && tablemap.get(temp[0]) == null))) {
                                    A0100list.add(temp[0]);
                                }
                            } else {

                            }

                        }

                        flag = true;
                    }
                } else {
                    if (flagCol != null) {

                        switch (flagCol.getCellType()) {
                            case Cell.CELL_TYPE_BLANK :
                                Cell a_cell = null;
                                boolean isNullRow = true;
                                for (short n = 0; n < cols; n++) {
                                    a_cell = row.getCell(n);
                                    if (a_cell != null && a_cell.getCellType() != Cell.CELL_TYPE_BLANK)
                                        isNullRow = false;
                                }
                                if (!isNullRow) {
                                    errorStr.append("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                    throw new GeneralException("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                }
                                break;
                            case Cell.CELL_TYPE_STRING :
                                if (flagCol.getRichStringCellValue().toString().trim().length() == 0) {
                                    errorStr.append("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                    throw new GeneralException("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                } else {
                                    onlynameMap.put(flagCol.getRichStringCellValue().toString().trim(), flagCol.getRichStringCellValue().toString().trim());
                                    standMap.put(j + 1 + "", flagCol.getRichStringCellValue().toString().trim());
                                }
                                break;
                        }

                    }
                }

                StringBuffer priverror = new StringBuffer();
                // RecordVo vo = new RecordVo(table_name);

                // 用于控制单位、部门、岗位的级联 郭峰 2013-4-3
                ArrayList cascadelist = new ArrayList();// 0:单位 1：部门 2：岗位
                cascadelist.add("");
                cascadelist.add("");
                cascadelist.add("");
                HashMap codeCadeMap=new HashMap();
                // 开始分析excel中的数据
                HashMap fatherCodeCadeMap=new HashMap();
                for (short c = 0; c < cols; c++) {
                    if ("0".equals(onlyflag) && c == 0) {// 如果系统中没有唯一性指标 或者说
                                                            // 系统中有唯一性指标且当前导入的模版中没有包含唯一性指标时，第一列是主键标识
                        continue;
                    }
                    Cell cell1 = row.getCell(c);
                    String fieldtemp = (String) name2map.get(new Short(c));// 所有列
                    if (cell1 != null) {
                        flag = true;
                        String fieldItems = (String) map.get(new Short(c));// 所有变化后的指标，获得的fieldItems都为空。

                        // 新增数据
                        if (!"no".equals(onlyname) && !"0".equals(onlyflag) && this.operationtype != 0 && this.operationtype != 5) {// 有唯一标识，并且不属于人员调入和新建
                            String[] fieldtemps = fieldtemp.split(":");
                            String fieldvalue = fieldtemps[0];// 解析列标题段
                            String onlyfield = "";// 从列标题中获得的数据库字段
                            Boolean equalflag = false;// 表示是否已存在该行
                            if (fieldvalue.indexOf("_") != -1)
                                onlyfield = fieldvalue.substring(0, fieldvalue.lastIndexOf("_"));
                            if (onlyfield.equalsIgnoreCase(onlyname)) {// 判断该列为唯一标识时执行
                                if (cell1.toString().length() > 0)
                                    countAll.add(Integer.toString(j));// 总行号
                                for (int s = 0; s < onlylist.size(); s++) {
                                    if (cell1.toString().equalsIgnoreCase(onlylist.get(s).toString())) {
                                        equalflag = true;// 表示是否已存在该唯一标识
                                        alreadyExists.add(Integer.toString(j));// 已经存在的行

                                        break;

                                    }
                                }
                                if (!equalflag && cell1.toString().length() > 0) {
                                    // 按唯一标识从人员库中循环查出库和A0100
                                    ArrayList fieldlist = new ArrayList();

                                    TemplateTableParamBo parambo = new TemplateTableParamBo(this.conn);
                                    boolean kqflag = parambo.isKqTempalte(Integer.parseInt(tabid)); // 判断当前模板是否定义了考勤参数
                                                                                                    // liuzy
                                                                                                    // 20151128
                                    Boolean isImp = true;// 在所有权限库中是否存在
                                    if(onlybase.size()==0){
                                    	isImp=false;
                                    }
                                    if(this.bo.getInfor_type()==1) {
                                    	for (int i = 0; i < onlybase.size(); i++) {
                                    		String preStr = "";// 新增人员所在库
                                    		preStr = onlybase.get(i).toString();
                                    		// 根据唯一标识，从权限的库中查出要新增的人员号
                                    		// String strWhere =
                                    		// userview.getPrivSQLExpression("",preStr,false,true,fieldlist);//经过权限过滤
                                    		String strWhere = "";
                                    		String filtersql = "";
                                    		String strsql = "";
                                    		if (!kqflag) { // 关联考勤模板后走的是考勤业务范围
                                    			// liuzy 20151128
                                    			strWhere = InfoUtils.getWhereINSql(userview, preStr);
                                    		} else {
                                    			strWhere = RegisterInitInfoData.getWhereINSql(userview, preStr);
                                    		}
                                    		String factor = paramBo.getFactor();
                                    		if("1".equals(paramBo.getFilter_by_factor())) {
                                    			if(StringUtils.isNotBlank(factor)) {
                                    				String whereIN = "select a0100 " + strWhere;
                                    				if ("1".equals(paramBo.getNo_priv_ctrl()))
                                    					whereIN = "";
                                    				YksjParser yp = new YksjParser(userview, alUsedFields, YksjParser.forSearch, 8, 0, "Ht", preStr);
                                    				YearMonthCount ymc = null;
                                    				yp.setSupportVar(true, "select  *  from   midvariable where nflag=0 and templetid= " + tabid); // 支持临时变量
                                    				yp.run_Where(factor, ymc, "", "", dao, whereIN, conn, "A", null);
                                    				String tempTableName = yp.getTempTableName();
                                    				filtersql+="select * from "+tempTableName;
                                    				filtersql+=" where (" + yp.getSQL()+")";
                                    				strsql+="select b.* from";
                                    				strsql+=" ("+filtersql+") a,"+preStr+"A01 b where a.a0100=b.a0100 and";
                                    				strsql+=" b."+onlyname + "='" + cell1 + "'";
                                    			}else {//检索条件为空
                                    				if ("1".equals(paramBo.getNo_priv_ctrl())) {//不按管理范围
                                    					strWhere = "from "+preStr+"A01 where ";
                                    					strsql = "select * " +strWhere+ onlyname + "='" + cell1 + "'";
                                    				}else {
                                    					if (strWhere.length() > 0) {
                                    						int idx = strWhere.toUpperCase().indexOf("WHERE");
                                    						if (idx == -1) {
                                    							strWhere = strWhere + " where " + onlyname + "='" + cell1 + "'";
                                    						} else {
                                    							strWhere = strWhere + " and " + onlyname + "='" + cell1 + "'";
                                    						}
                                    					}
                                    					strsql = "select * " + strWhere;
                                    				}
                                    			}
                                    		}else {
                                    			if (strWhere.length() > 0) {
                                    				int idx = strWhere.toUpperCase().indexOf("WHERE");
                                    				if (idx == -1) {
                                    					strWhere = strWhere + " where " + onlyname + "='" + cell1 + "'";
                                    				} else {
                                    					strWhere = strWhere + " and " + onlyname + "='" + cell1 + "'";
                                    				}
                                    			}
                                    			strsql = "select * " + strWhere;
                                    		}
                                    		rset = dao.search(strsql);
                                    		// 当该库存在该员工时执行
                                    		if (rset.next()) {
                                    			String a0100 = rset.getString("A0100");
                                    			// 向发起申请的数据表中插入数据
                                    			if (a0100 != null) {
                                    				A0100list.add(a0100 + "|" + preStr);// 添加所有符合权限的新增数据
                                    				hashpre.add(preStr);// 所有新增数据包含的库
                                    				importSuccess.add(Integer.toString(j));// 导入成功的行号
                                    				isImp = true;
                                    				break;
                                    			}
                                    		}
                                    		isImp = false;
                                    	}
                                    }else {
                                    	 StringBuffer sql_str = new StringBuffer();
                                         String fieldcode = "b0110";
                                         String key = "B01";
                                         String BasePre = "B";
                                         int infoGroup=YksjParser.forUnit;
                         				if(this.bo.getInfor_type()==3)
                         				{
                         					fieldcode = "e01a1";
                         					infoGroup=YksjParser.forPosition;
                         					BasePre = "K";
                         					key = "K01";
                         				}
                         				sql_str.append("select "+fieldcode+" from ");
                         				if("1".equals(paramBo.getFilter_by_factor())) {
                         					 int varType = 8;
                                             String whereIN = "";
                                             //获得管理范围sql
                                             whereIN = "select "+fieldcode+" from "+key+" "+templateBo.getPrivSQL(fieldcode);
                                             if ("1".equals(paramBo.getNo_priv_ctrl()))
                                                 whereIN = "";
                                             YksjParser yp = new YksjParser(this.userview, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", "");
                                             YearMonthCount ymc = null;
                                             yp.setSupportVar(true, "select  *  from   midvariable where nflag=0 and templetid= " + tabid); // 支持临时变量
                                             yp.run_Where(paramBo.getFactor(), ymc, "", "", dao, whereIN, this.conn, "A", null);
                                             String tempTableName = yp.getTempTableName();
                                             sql_str.append(tempTableName);
                                             sql_str.append(" where " + yp.getSQL());
                                             if ("2".equals(flag)) {
                                            	 sql_str.append(" and "+fieldcode+" not in (select "+fieldcode+" from ");
                                            	 sql_str.append(this.userview.getUserName() + "templet_" + tabid);
                                            	 sql_str.append(")");
                                             }
                                        	
                         				}else {
                         					sql_str.append(key+" "+templateBo.getPrivSQL(fieldcode));
                         				}
                         				if(sql_str.toString().toUpperCase().indexOf("WHERE")>-1) {
                         					sql_str.append(" and "+onlyname + "='" + cell1 + "'");
                         				}else {
                         					sql_str.append(" where "+onlyname + "='" + cell1 + "'");
                         				}
                                    	rset = dao.search(sql_str.toString());
                                    	if(rset.next()) {
                                    		A0100list.add(rset.getString(fieldcode));// 添加所有符合权限的新增数据
                                    		importSuccess.add(Integer.toString(j));// 导入成功的行号
                                    		isImp = true;
                                    	}else {
                                    		isImp = false;
                                    	}
                                    }
                                    if (!isImp)
                                        importFaild.add(Integer.toString(j));// 导入失败的行
                                }
                            }
                        }

                        if (fieldItems == null)// 过滤掉只读的列，即变化前的指标
                            continue;

                        String[] fieldItem = fieldItems.split(":");
                        String field = fieldItem[0];
                        String fieldName = fieldItem[1];
                        String tempfield = "";
                        if (field.indexOf("_") != -1)
                            tempfield = field.substring(0, field.lastIndexOf("_"));
                        String itemtype = "";
                        String codesetid = "";
                        int decwidth = 0;
                        int itemlength = 0;
                        if (DataDictionary.getFieldItem(tempfield) != null) {
                            itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();// 类型
                            codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();// 代码类
                            decwidth = DataDictionary.getFieldItem(tempfield).getDecimalwidth();// 小数位数
                            itemlength = DataDictionary.getFieldItem(tempfield).getItemlength();// 字符长度或数值的整数部分位数
                        }
                        // if(itemtype.equals("M")){
                        // vo.setString(field,
                        // cell1.getRichStringCellValue().toString()) ;
                        // continue;//大字段类型单独处理
                        // }
                        if ("codesetid".equalsIgnoreCase(tempfield) || "codeitemdesc".equalsIgnoreCase(tempfield) || "corcode".equalsIgnoreCase(tempfield) || "parentid".equalsIgnoreCase(tempfield) || "start_date".equalsIgnoreCase(tempfield)) {
                            if ("start_date".equalsIgnoreCase(tempfield)) {
                                itemtype = "D";
                                itemlength = 10;
                            } else {
                                itemtype = "A";
                                itemlength = 50;
                            }

                        }
                        // String pri =
                        // this.userView.analyseFieldPriv(fieldName);
                        // if (pri.equals("1") || pri.equals("0")) //只读或者是没有权限
                        // continue;

                        String value = "";
                        String value_ = "";
                        switch (cell1.getCellType()) {
                            case Cell.CELL_TYPE_FORMULA :
                                break;
                            case Cell.CELL_TYPE_NUMERIC :
                                if ("D".equals(itemtype)) {
                                    value = "" + cell1.getNumericCellValue();

                                    if (value != null && value.length() > 0 && value.matches("^[+-]?[\\d]*[.]?[\\d]+")) {
                                        Date d_value = cell1.getDateCellValue();
                                        value = df.format(d_value);
                                    }
                                    if (value.length() > 0 && this.isDataTimeType(decwidth, itemtype, value)) {
                                        list.add(Timestamp.valueOf(value.trim()));
                                    } else
                                        list.add(null);

                                } else {
                                	 if ((cell1 == null) || ("".equals(cell1))) { //空值数值型不导入0，导入空
                                        list.add(null);
                                      } else {
                                        double y = cell1.getNumericCellValue();
                                        value = Double.toString(y);
                                        value = PubFunc.round(value, decwidth);
                                        list.add(new Double(PubFunc.round(value, decwidth)));
                                      }
                                }
                                if (fieldtemp != null) {// 为了抛出提示信息出现名字
                                    String[] fieldItem2 = fieldtemp.split(":");
                                    String field2 = fieldItem2[0];
                                    if (this.bo.getInfor_type() == 1) {

                                        if ("a0101_1".equalsIgnoreCase(field2)) {
                                            nameMap.put(j + 1 + "", value);
                                        } else if ("a0101_2".equalsIgnoreCase(field2)) {
                                            nameMap.put(j + 1 + "", value);
                                        }

                                    } else {
                                        if ("codeitemdesc_1".equalsIgnoreCase(field2)) {
                                            if (value.length() > 0) {
                                                nameMap.put(j + 1 + "", value);
                                            }
                                        } else if ("codeitemdesc_2".equalsIgnoreCase(field2)) {
                                            if (value.length() > 0) {
                                                nameMap.put(j + 1 + "", value);
                                            }
                                        }
                                    }
                                }
                                break;
                            case Cell.CELL_TYPE_STRING :
                                value = cell1.getRichStringCellValue().toString();
                                value = value.replaceAll("　", " ").trim();
                                value_ = value;
                                if (!"0".equals(codesetid) && !"".equals(codesetid)) {// 当是代码类的时候
                                    String key = codesetid + "a04v2u" + value.trim();
                                    if (codeColMap.get(key) == null && "UM".equalsIgnoreCase(codesetid))
                                        key = "UNa04v2u" + value.trim();
                                    if (codeColMap.get(key) != null) {
                                        value = (String) codeColMap.get(key);
                                        if ("UN".equalsIgnoreCase(codesetid) && ("B0110".equalsIgnoreCase(tempfield) || "E0122".equalsIgnoreCase(tempfield))) {
                                            cascadelist.remove(0);
                                            cascadelist.add(0, value);
                                        } else if ("UM".equalsIgnoreCase(codesetid) && ("E0122".equalsIgnoreCase(tempfield) || "B0110".equalsIgnoreCase(tempfield))) {
                                            cascadelist.remove(1);
                                            cascadelist.add(1, value);
                                        } else if ("@K".equalsIgnoreCase(codesetid) && "E01A1".equalsIgnoreCase(tempfield)) {
                                            cascadelist.remove(2);
                                            cascadelist.add(2, value);
                                        }else{
                                        	codeCadeMap.put(field.toLowerCase(), value);
                                        }
                                        if(codeLeafSetList.contains(codesetid)&&!codeItemColMap.containsKey(key)){
                                    		String msg="\"" + fieldName + "\"列仅可选择末级代码，\""+value_+"\"不可选择!";
                                    		errorStr.append(msg);
                                    		throw new GeneralException(msg);
                                    	}

                                    } else
                                        value = null;
                                } else {
                                    if (this.bo != null && this.bo.getInfor_type() != 1 && field.startsWith("codesetid_2")) {
                                        if ("UM:部门".equals(value))
                                            value = "UM";
                                        if ("UN:单位".equals(value))
                                            value = "UN";
                                    }
                                    if (this.bo != null && this.bo.getInfor_type() != 1 && field.startsWith("parentid")) {
                                        if (codeColMap.get("UN" + "a04v2u" + value.trim()) != null)
                                            value = (String) codeColMap.get("UN" + "a04v2u" + value.trim());
                                        else if (codeColMap.get("UM" + "a04v2u" + value.trim()) != null)
                                            value = (String) codeColMap.get("UM" + "a04v2u" + value.trim());
                                        else
                                            value = null;
                                    }
                                }
                                // 单位，部门，职位是否进行了权限控制
                                // if(codesetid.equals("UN")||codesetid.equals("UM")||codesetid.equals("@K")){
                                // String error
                                // =isPriv_ctrl(cell_param_map,field,value,j,fieldName);
                                // if(error.length()>0)
                                // priverror.append(error);
                                // }
                                if ("D".equals(itemtype)) {// 日期型
                                    if (value.length() > 0 && this.isDataType(decwidth, itemtype, value)) {
                                        value = returnDataType(value);
                                        list.add(java.sql.Date.valueOf(value.trim()));
                                      //syl 20200114add  56620  V77新考勤/考勤申请：下载的excel模板，时间类型的含有时分的，导入系统的时候会提示格式不对，没有考虑时分的情况
                                    } else if (value.length() > 0 && this.isDataTimeType(decwidth, itemtype, value)) {
                                    	value = returnDataTimeType(value);
                                        list.add(Timestamp.valueOf(value.trim()));
                                    } else
                                        list.add(null);
                                } else {
									//空值传null不保存0
                                	if(StringUtils.isNotBlank(value))
                                		list.add(value);
                                	else
                                		list.add(null);
                                }
                                if (fieldtemp != null) {// 为了抛出提示信息出现名字
                                    String[] fieldItem2 = fieldtemp.split(":");
                                    String field2 = fieldItem2[0];
                                    if (this.bo.getInfor_type() == 1) {
                                        if ("a0101_1".equalsIgnoreCase(field2)) {
                                            nameMap.put(j + 1 + "", value);
                                        } else if ("a0101_2".equalsIgnoreCase(field2)) {
                                            nameMap.put(j + 1 + "", value);
                                        }
                                    } else {
                                        if ("codeitemdesc_1".equalsIgnoreCase(field2)) {
                                            if (value.length() > 0) {
                                                nameMap.put(j + 1 + "", value);
                                            }
                                        } else if ("codeitemdesc_2".equalsIgnoreCase(field2)) {
                                            if (value.length() > 0) {
                                                nameMap.put(j + 1 + "", value);
                                            }
                                        }
                                    }
                                }
                                break;
                            case Cell.CELL_TYPE_BLANK :// 如果什么也不填的话数值就默认更新为0
                                if ("N".equals(itemtype)) {
									//空值传null不保存0
                                	if(StringUtils.isNotBlank(value)){
	                                    value = PubFunc.round(value, decwidth);
	                                    list.add(new Double(value));
                                    }else{
                                    	list.add(null);
                                    }
                                } else if ("D".equals(itemtype)) {
                                    if (value.length() > 0 && this.isDataType(decwidth, itemtype, value)) {
                                        value = returnDataType(value);
                                        list.add(java.sql.Date.valueOf(value.trim()));
                                    } else
                                        list.add(null);

                                } else
                                    list.add(null);
                                break;
                            default :
                                list.add(null);
                        }
                        fatherCodeCadeMap.put(field.toLowerCase(), value+":"+fieldName);
                        String msg = "";
                        if ("N".equals(itemtype) || "D".equals(itemtype)) {
                            Date d_value = null;
                            if (value != null)
                                value = value.trim();
                            // 郭峰注释 不符合直接报错即可。
                            // if(value!=null&&value.length()>0&&itemtype.equalsIgnoreCase("D")&&value.matches("^[+-]?[\\d]*[.]?[\\d]+"))
                            // {
                            // d_value= cell1.getDateCellValue();
                            // value=df.format(d_value);
                            // }
                            if (value.length() > 0 && !this.isDataType(decwidth, itemtype, value)&& !this.isDataTimeType(decwidth, itemtype, value)) {
                                if ("D".equals(itemtype))
                                    msg = "源数据(" + fieldName + ")第" + (j + 1) + "行中数据:" + value + " 不符合格式<br>格式为yyyy-MM-dd或者yyyy.MM.dd或者yyyy/MM/dd!<br>时间格式为yyyy-MM-dd HH或者yyyy-MM-dd HH:mm或者yyyy-MM-dd HH:mm:ss或者yyyy.MM.dd HH或者yyyy.MM.dd HH:mm或者yyyy.MM.dd HH:mm:ss!";
                                 else
                                    msg = "源数据(" + fieldName + ")第" + (j + 1) + "行中数据:" + value + " 不符合格式!";
                                errorStr.append(msg);
                                throw new GeneralException(msg);
                            }
                        }
                        if (value != null && "D".equals(itemtype) && value.length() > 19) {
                            errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                            throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                        }
                        if (value != null && "N".equals(itemtype) && value.length() > itemlength) {
                            if (decwidth > 0 && value.length() > itemlength + decwidth + 1) {
                                errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                                throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                            }
                        }
                        if (value != null && "A".equals(itemtype)) {
                            byte[] valuelength = value.getBytes();// 得到系统默认编码的字节数组
                            if (valuelength.length > itemlength) {
                                errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                                throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:长度超过数据库定义的长度，导入失败!");
                            }
                            if(this.operationtype==5&&"parentid_2".equals(field)&&!"".equals(value)) {
                            	if(invalidCodeList.contains(value)) {
                            		errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行数据:"+value_.substring(value_.indexOf(":")+1,value_.length())+"已失效，导入失败!");
                                    throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行数据:"+value_.substring(value_.indexOf(":")+1,value_.length())+"已失效，导入失败!");
                            	}	
                            }
                        }
						//级联指标是否级联正确判断。
                        if(StringUtils.isNotBlank(value)&&mainImmportList.contains(field.toLowerCase())){
                        	String[] personList = value.split("、");
                        	for(int i=0;i<personList.length;i++){
                        		String person=personList[i];
                        		String[] personInfo = person.split(":");
                        		if(personInfo.length!=2){
                        			errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                                    throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                        		}
                        		String a0101=personInfo[0];
                        		String onlyKeyValue=personInfo[1];
                        		String searchSql="";
                        		for(int dbnum=0;dbnum<dbList.size();dbnum++){
            		        		if(StringUtils.isNotBlank(searchSql)){
            		        			searchSql+=" UNION  ";
            		        		}
            		        		searchSql+="select '"+dbList.get(dbnum)+"' as pre,a0100 from "+dbList.get(dbnum)+"a01 where lower(a0101)=lower('"+a0101+"') and "+onlyKey+"='"+onlyKeyValue+"'";
            		        	}
                        		try{
                        			rset=dao.search(searchSql);
                        		}catch(Exception ex){
                        			errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                                    throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                        		}
                        		if(!rset.next()){
                        			errorStr.append("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                                    throw new GeneralException("源数据(" + fieldName + ")第" + (j + 1) + "行中数据:"+person+"信息有误，导入失败!");
                        		}
                        	}
                        }

                    }
                    // if(cell1==null&&c!=cols-1)//现已经将所有变化后指标无值的都置为Null
                    // {
                    // if(map.get(new Short(c))!=null)
                    // {
                    // list.add(null);
                    // }
                    // }
                    if (cell1 == null) {// cell1为null时，并且是变化后指标，则值为空（髙怀云）
                        String[] fieldtempsCell = fieldtemp.split(":");
                        String fieldvalueCell = fieldtempsCell[0];// 解析列标题段
                        if (fieldvalueCell.indexOf("_") != -1) {
                            String[] itemCell = fieldvalueCell.split("_");
                            if ("2".equals(itemCell[1].trim()))// 是否为变化后指标
                                list.add(null);
                        }
                    }
                    num3++;
                    if (cell1 == null || list.get(list.size() - 1) == null || "".equals(list.get(list.size() - 1))) {

                        num4++;

                    } else {
                        // System.out.println(list.get(list.size()-1));
                    }
                }
                // 处理单位、部门和岗位的级联
                String tempunit = (String) cascadelist.get(0);
                String tempdepart = (String) cascadelist.get(1);
                String tempjob = (String) cascadelist.get(2);
                if ("".equals(tempunit)) {// 如果单位是空
                    if ("".equals(tempdepart)) {// 如果部门是空

                    } else {// 如果部门不是空
                        if ("".equals(tempjob)) {// 如果岗位是空

                        } else {// 如果岗位不是空
                            // 单位是空，部门和岗位不是空的处理
                            if ((tempdepart.length() < tempjob.length()) && tempjob.indexOf(tempdepart) == 0) {

                            } else {
                                cascadeerror.append("源数据在第" + (j + 1) + "行中维护的部门和岗位之间未关联，请修改！<br>");
                            }
                        }
                    }
                } else {// 如果单位不是空
                    if ("".equals(tempdepart)) {// 如果部门是空
                        if ("".equals(tempjob)) {// 如果岗位是空

                        } else {// 如果岗位不是空
                            // 单位不是空，部门是空，岗位不是空的处理
                            if ((tempunit.length() < tempjob.length()) && tempjob.indexOf(tempunit) == 0) {

                            } else {
                                cascadeerror.append("源数据在第" + (j + 1) + "行中维护的单位和岗位之间未关联，请修改！<br>");
                            }
                        }
                    } else {// 如果部门不是空
                        if ("".equals(tempjob)) {// 如果岗位是空
                            // 单位不是空，部门不是空，岗位是空的处理
                            if ((tempunit.length() < tempdepart.length()) && tempdepart.indexOf(tempunit) == 0) {

                            } else {
                                cascadeerror.append("源数据在第" + (j + 1) + "行中维护的单位和部门之间未关联，请修改！<br>");
                            }
                        } else {// 如果岗位不是空
                            // 如果三者都不为空
                            if ((tempunit.length() < tempdepart.length()) && (tempdepart.indexOf(tempunit) == 0) && (tempdepart.length() < tempjob.length()) && (tempjob.indexOf(tempdepart) == 0)) {

                            } else {
                                cascadeerror.append("源数据在第" + (j + 1) + "行中维护的单位、部门和岗位之间未关联，请修改！<br>");
                            }
                        }
                    }
                }// 判断级联终止
                Iterator iterator = chidlFatherMap.entrySet().iterator();
                while(iterator.hasNext()){
                	Entry entry=(Entry) iterator.next();
                	String key = (String) entry.getKey();
                	String fatherId=(String) entry.getValue();
                	String childValut = (String) fatherCodeCadeMap.get(key);
                	String childFieldName="";
                	if(StringUtils.isNotBlank(childValut)){
                		String[] split = childValut.split(":");
                		childValut=split[0];
                		childFieldName=split[1];
                	}
                	String fatherValut = (String) fatherCodeCadeMap.get(fatherId);
                	String fatherFieldName="";
                	if(StringUtils.isNotBlank(fatherValut)){
                		String[] split = fatherValut.split(":");
                		fatherValut=split[0];
                		fatherFieldName=split[1];
                	}
                	if(StringUtils.isNotBlank(childValut)&&fatherValut!=null){
                		if("".equalsIgnoreCase(fatherValut)||fatherValut==null){
                			cascadeerror.append("源数据在第" + (j + 1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");
                		}else if(childValut.indexOf(fatherValut)!=0||childValut.indexOf(fatherValut)==0&&childValut.length()==fatherValut.length()){//bug 43490 关联指标选择相同代码值，应该提示。
                			cascadeerror.append("源数据在第" + (j + 1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");
                		}
                	}
                }
                // if(priverror.length()>0)
                // throw new Exception(priverror.toString());
                if (num3 == num4)
                    continue; // 跳过该行
                if (!flag)// 整行为空过滤掉
                    continue;
                if (flagCol != null) {
                    if (!"0".equals(onlyflag)) {
                        switch (flagCol.getCellType()) {
                            case Cell.CELL_TYPE_BLANK :
                                errorStr.append("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                throw new GeneralException("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                            case Cell.CELL_TYPE_STRING :
                                if (flagCol.getRichStringCellValue().toString().trim().length() == 0) {
                                    errorStr.append("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                    throw new GeneralException("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                                }

                        }
                    }

                } else {
                    if (!"0".equals(onlyflag)) {
                        errorStr.append("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                        throw new GeneralException("第" + (j + 1) + "行唯一标识列："+ onlynamedesc +"存在空数据，导入数据失败！");
                    }
                }
                ArrayList insert = (ArrayList) list.clone();// list存放的是该行所有变化后的指标的数据，现在做一个list实例的浅表副本

                // a0100s.append("'" + temp[1] + "',");
                if (flagCol == null) {
                    if (table_name.startsWith("templet")) {// 审批状态 审批模式下没有新增
                        continue;

                    } else {
                        if (this.operationtype != 0 && this.operationtype != 5) {// ==0是调入模板
                                                                                    // 需要特别处理
                                                                                    // 如没主键，就新增
                            continue;
                        } else {// 判断新增或者更新
                            // 新增
                            IDGenerator idg = new IDGenerator(2, this.conn);

                            if (this.bo.getDest_base() == null || this.bo.getDest_base().length() == 0) {
                                if (this.bo.getInfor_type() == 1) {
                                    errorStr.append("人员调入业务模板未定义目标库!");
                                    throw new GeneralException("人员调入业务模板未定义目标库!");
                                } else {
                                    // throw new
                                    // GeneralException("人员调入业务模板未定义目标库!");
                                }
                            }
                            String id = idg.getId("rsbd.a0100");
                            if (this.bo.getInfor_type() == 1) {
                                listxuhao.add(id);
                                insert.add(dest_base);
                                insert.add(id);
                                if (max_a0000>0)
                                    insert.add("" + (max_a0000 + num++));

                                insert.add(CreateSequence.getUUID());
                                insertlist.add(insert);
                                // vo.setString("BasePre",
                                // tablebo.getDest_base().toUpperCase()) ;
                                // vo.setString("A0100", id) ;
                                // vo.setString("A0000", rset.getInt(1)+num+"")
                                // ;
                            } else {
                                listxuhao.add("B" + id);
                                insert.add("B" + id);
                                if (max_a0000>0)
                                    insert.add("" + (max_a0000 + num++));

                                insert.add(CreateSequence.getUUID());
                                insertlist.add(insert);
                                // if(this.bo.getInfor_type()==2){
                                // vo.setString("B0110", id) ;
                                // vo.setString("A0000", rset.getInt(1)+num+"")
                                // ;
                                // }else{
                                // vo.setString("E01a1", id) ;
                                // vo.setString("A0000", rset.getInt(1)+num+"")
                                // ;
                                // }
                            }
                            continue;

                        }
                    }
                } else {

                    if ("0".equals(onlyflag)) {
                        String[] temp = flagCol.getStringCellValue().split("\\|");
                        if (temp != null && temp.length > 1 && temp[1] != null) {
                            if (tablemap == null) {
                                if (this.bo.getInfor_type() == 1) {
                                    errorStr.append("当前模板库中不存在人员!");
                                    throw new GeneralException("当前模板库中不存在人员!");
                                } else {
                                    errorStr.append("当前模板库中不存在记录!");
                                    throw new GeneralException("当前模板库中不存在记录!");
                                }

                            }
                            if (tablemap != null && ((this.bo.getInfor_type() == 1 && tablemap.get(temp[1]) == null) || (this.bo.getInfor_type() != 1 && tablemap.get(temp[0]) == null))) {
                            	if(A0100list.size()>0&&!A0100list.contains(temp[1]+"|"+temp[0])){//不在待引入的人员中
	                                if (this.bo.getInfor_type() == 1) {
	                                    strerror.append(" " + num2 + "、第" + (j + 1) + "行     姓名：" + nameMap.get(j + 1 + "") + "<br>");
	                                } else {
	                                    strerror.append(" " + num2 + "、第" + (j + 1) + "行  ：" + nameMap.get(j + 1 + "") + "<br>");
	                                }

	                                num2++;
                            	}
                            }
                            // throw new
                            // GeneralException("第"+(j+1)+"行中主键标识下的人员id:"+temp[1]+"不在当前模板库中!");
                        }
                        if (table_name.startsWith("templet")) {// 审批状态
                                                                // 审批模式下没有新增
                            if (temp.length == 4) {
                                list.add(temp[0]);
                                list.add(temp[1]);
                                list.add(temp[2]);
                                list.add(temp[3]);

                            } else if (temp.length == 3) {
                                list.add(temp[0]);
                                list.add(temp[1]);
                                list.add(temp[2]);

                            } else {
                                continue;
                            }

                        } else {
                            if (this.operationtype != 0 && this.operationtype != 5) {// ==0是调入模板
                                                                                        // 需要特别处理
                                                                                        // 如没主键，就新增
                                if (temp.length == 2) {
                                    list.add(temp[0]);
                                    list.add(temp[1]);
                                } else if (temp.length == 1) {
                                    list.add(temp[0]);
                                } else {
                                    continue;
                                }
                            } else {// 判断新增或者更新
                            	/***syl 20200207 add 判断唯一标识在当前记录 中是否有值，有对应记录则更新，否则新增*/
                            	boolean bl=true;//判断唯一标识是否 有记录
                            	StringBuffer search= new StringBuffer();
                            	search.append("select 1 from ");
                            	search.append(table_name);
                        		ArrayList seachList=new ArrayList();
                        	//人员的 程序其他地方有校验。本次不再校验处理
                            	if (bo.getInfor_type() == 2&&temp.length == 1) {
                                	search.append(" where  b0110=? ");
                                	seachList.add(temp[0]);
                                	rset=dao.search(search.toString(),seachList);
                            		if(rset.next()){
                            			bl=false;
                            		}
                                } else if (bo.getInfor_type() == 3&&temp.length == 1) {
                                	search.append(" where  e01a1=? ");
                                	seachList.add(temp[0]);
                                	rset=dao.search(search.toString(),seachList);
                            		if(rset.next()){
                            			bl=false;
                            		}
                                }else if(bo.getInfor_type() == 1&&temp.length == 2) {
                                	search.append(" where basepre=? and   a0100=? ");
                                	seachList.add(temp[0]);
                                	seachList.add(temp[1]);
                                	rset=dao.search(search.toString(),seachList);
                            		if(rset.next()){
                            			bl=false;
                            		}
                                } else {
                                	bl=false;
                                }
                            	
                                if (temp.length == 0 || (temp.length == 1 && "".equals(temp[0]))||bl) {// 新增
                                    IDGenerator idg = new IDGenerator(2, this.conn);

                                    if (this.bo.getDest_base() == null || this.bo.getDest_base().length() == 0) {
                                        if (this.bo.getInfor_type() == 1) {
                                            errorStr.append("人员调入业务模板未定义目标库!");
                                            throw new GeneralException("人员调入业务模板未定义目标库!");
                                        } else {
                                            // throw new
                                            // GeneralException("当前模板库中不存在记录!");
                                        }

                                    }
                                    String id = idg.getId("rsbd.a0100");
                                    if (this.bo.getInfor_type() == 1) {
                                        listxuhao.add(id);
                                        insert.add(dest_base);
                                        insert.add(id);
                                        if (max_a0000>0)
                                            insert.add("" + (max_a0000 + num++));
                                        insert.add(CreateSequence.getUUID());
                                        insertlist.add(insert);
                                    } else {
                                        listxuhao.add("B" + id);
                                        insert.add("B" + id);
                                        if (max_a0000>0)
                                            insert.add("" + (max_a0000 + num++));

                                        insert.add(CreateSequence.getUUID());
                                        insertlist.add(insert);
                                    }
                                    continue;
                                } else {
                                    if (temp.length == 2) {
                                        list.add(temp[0]);
                                        list.add(temp[1]);
                                    } else if (temp.length == 1) {
                                        list.add(temp[0]);
                                    } else {
                                        if (this.bo.getInfor_type() == 1) {
                                            list.add(null);
                                            list.add(null);
                                        } else {
                                            list.add(null);
                                        }

                                    }
                                }

                            }
                        }
                    } else {

                        if (table_name.startsWith("templet")) {// 审批状态
                                                                // 审批模式下没有新增

                            list.add(flagCol.getStringCellValue());
                            if (tablemap == null) {
                                if (this.bo.getInfor_type() == 1) {
                                    errorStr.append("当前模板库中不存在人员!");
                                    throw new GeneralException("当前模板库中不存在人员!");
                                } else {
                                    errorStr.append("当前模板库中不存在记录!");
                                    throw new GeneralException("当前模板库中不存在记录!");
                                }

                            }
                            if (tablemap != null && tablemap.get(flagCol.getStringCellValue()) == null) {// 某行记录导入失败
                                                                                                            // 因为唯一性表示未录入
                                if (this.bo.getInfor_type() == 1) {
                                    String showmessage = (String) standMap.get(j + 1 + "");// begin
                                                                                            // xcs
                                    if (showmessage == null) {
                                        showmessage = "";
                                    }
                                    strerror.append(" " + num2 + "、第" + (j + 1) + "行 " + onlynamedesc + ": " + showmessage + "<br>"); // end
                                                                                                                                        // xcs
                                } else {
                                    strerror.append(" " + num2 + "、第" + (j + 1) + "行  ：" + nameMap.get(j + 1 + "") + "<br>");
                                }

                                num2++;
                            }

                        } else {

                            if (this.operationtype != 0 && this.operationtype != 5) {
                                // 非调入模板，如果人员不存在，则调用手工选人模式 将人员加入。wangrd
                                // 2014-02-26
                                if (!table_name.startsWith("templet")) {// 审批状态
                                                                        // 审批模式下没有新增
                                    String value = flagCol.getStringCellValue();
                                    if ((value != null) && (!"".equals(value))) {
                                        if ((tablemap == null) || (tablemap != null && tablemap.get(value) == null)) {
                                            FieldItem onlyFileld = DataDictionary.getFieldItem(onlyname);
                                            // 新增
                                            ArrayList a0100list = new ArrayList();
                                            String impPre = "";
                                            if (this.bo.getInfor_type() == 1) {
                                                ArrayList privDbList = this.userview.getPrivDbList();
                                                String onglydb= userview.getDbpriv().toString();// 获取该用户权限下的人员库
                                                if(StringUtils.isNotBlank(initbase)&&(","+onglydb+",").toUpperCase().indexOf(","+initbase.toUpperCase()+",")!=-1){
                                                	privDbList.clear();
                                                	privDbList.add(initbase);
                                    			};
                                                for (int tmpN = 0; tmpN < privDbList.size(); tmpN++) {
                                                    String pre = (String) privDbList.get(tmpN);
                                                    impPre = pre;
                                                    String strSql = "select * from " + pre + "A01 where " + onlyname + "='" + value + "'";

                                                    String strWhere = this.userview.getPrivSQLExpression(impPre, false);
                                                    if (!"".equals(strWhere)) {
                                                        strSql = strSql + " and A0100 in (select A0100 " + strWhere + ")";
                                                    }

                                                    RowSet rs = dao.search(strSql);
                                                    if (rs.next()) {
                                                        a0100list.add(rs.getString("a0100"));
                                                        break;
                                                    }
                                                }
                                            } else if (this.bo.getInfor_type() == 2) {
                                                impPre = "B";
                                                String strSql = "select * from B01 where " + onlyname + "='" + value + "'";
                                                String strWhere = this.userview.getUnitPosWhereByPriv("b0110");
                                                if (!"".equals(strWhere)) {
                                                    strSql = strSql + " and (" + strWhere + ")";
                                                }
                                                RowSet rs = dao.search(strSql);
                                                if (rs.next()) {
                                                    a0100list.add(rs.getString("b0110"));
                                                }

                                            } else if (this.bo.getInfor_type() == 3) {
                                                impPre = "K";
                                                String strSql = "select * from K01 where " + onlyname + "='" + value + "'";
                                                String strWhere = this.userview.getUnitPosWhereByPriv("e01a1");
                                                if (!"".equals(strWhere)) {
                                                    strSql = strSql + " and (" + strWhere + ")";
                                                }
                                                RowSet rs = dao.search(strSql);
                                                if (rs.next()) {
                                                    a0100list.add(rs.getString("e01a1"));
                                                }

                                            }
                                            if (a0100list.size() > 0) {
                                                // this.bo.impDataFromArchive(a0100list,
                                                // impPre);
                                                tablemap.put(value, value);
                                            }

                                        }

                                    }
                                }
                                list.add(flagCol.getStringCellValue());
                            } else {// 判断新增或者更新
                                if (tablemap == null || tablemap.get(flagCol.getStringCellValue()) == null) {// 新增
                                    IDGenerator idg = new IDGenerator(2, this.conn);

                                    if (this.bo.getDest_base() == null || this.bo.getDest_base().length() == 0) {
                                        if (this.bo.getInfor_type() == 1) {
                                            errorStr.append("人员调入业务模板未定义目标库!");
                                            throw new GeneralException("人员调入业务模板未定义目标库!");
                                        } else {
                                            // throw new
                                            // GeneralException("当前模板库中不存在记录!");
                                        }

                                    }
                                    String id = idg.getId("rsbd.a0100");
                                    if (this.bo.getInfor_type() == 1) {
                                        listxuhao.add(id);
                                        insert.add(dest_base);
                                        insert.add(id);
                                        if (max_a0000>0)
                                            insert.add("" + (max_a0000 + num++));
                                        insert.add(CreateSequence.getUUID());
                                        insertlist.add(insert);
                                    } else {
                                        listxuhao.add("B" + id);
                                        insert.add("B" + id);
                                        if (max_a0000>0)
                                            insert.add("" + (max_a0000 + num++));
                                        insert.add(CreateSequence.getUUID());
                                        insertlist.add(insert);
                                    }
                                    continue;
                                } else {
                                    list.add(flagCol.getStringCellValue());
                                }

                            }
                        }

                    }
                }

                list2.add(list);
                // listvo.add(vo);
            }// 遍历excel的所有行 结束
            // 导入新增数据，非人员调入或者新建模版
            if (A0100list.size() > 0) {

                // if(hashpre.contains(st[1])){//优化，减少循环次数
                if(this.bo.getInfor_type()==1) {
                    Iterator ir = hashpre.iterator(); // 遍历获得人员库前缀
                    while (ir.hasNext()) {
                        String pre = (String) ir.next();
                        ArrayList A0100listUsr = new ArrayList();
                        for (int i = 0; i < A0100list.size(); i++) {
                            String st[] = A0100list.get(i).toString().split("\\|");
                            if (st[1].equalsIgnoreCase(pre)) {// 获得所有人员变换
                                A0100listUsr.add(st[0]);
                            }
                        }
                        // }
                        importCount+=A0100listUsr.size();//按人员库引入人员，计算引入人数需要累加
                        bo.impDataFromArchive(A0100listUsr, pre);// 将人员库对应的所有人员导入库中

                    }
                }else {
                    importCount+=A0100list.size();
                    bo.impDataFromArchive(A0100list, "");
                }
            }

            HashSet h = new HashSet(importFaild);// 遍历去重
            importFaild.clear();
            importFaild.addAll(h);// 得到导入失败的数据
            // System.out.println("导入失败的行："+importFaild);
            int errorCount = importFaild.size();// 需要提示出的有问题数据
            int okCount = importCount;// 成功导入的条数
            // this.getFormHM().put("okCount", okCount + "");

            if (errorCount >0) {
                // 生成提示excel
                errorFileName = this.generateErrorFile(importFaild, sheet, file_name, okCount);
                /* 薪资发放：新建/导入数据 出现空白页 xiaoyun 2014-9-22 start */
                // errorFileName = errorFileName.replace(".xls", "#");
//                errorFileName = SafeCode.encode(PubFunc.encrypt(errorFileName));
                //20/3/6 xus vfs改造
                errorFileName = PubFunc.encrypt(errorFileName);
                /* 薪资发放：新建/导入数据 出现空白页 xiaoyun 2014-9-22 end */
            }
            // this.getFormHM().put("errorFileName", errorFileName);

            String tempstr = cascadeerror.toString();
            if (!"".equals(tempstr)) {// 如果是级联错误
                errorStr.append(cascadeerror.toString());
                throw new GeneralException(cascadeerror.toString());
            }

            if (strerror.toString().length() > 2) {
                if (this.bo.getInfor_type() == 1) {// 人员处理模板
                    if ("0".equals(onlyflag)) {
                        errorStr.append("EXCEL模板中的人员：<br>" + strerror.toString() + "在业务表单中不存在!");
                        throw new GeneralException("EXCEL模板中的人员：<br>" + strerror.toString() + "在业务表单中不存在!");
                    } else {
                        errorStr.append("EXCEL模板人员,在业务表单中不存在:<br>" + strerror.toString());
                        throw new GeneralException("EXCEL模板人员,在业务表单中不存在:<br>" + strerror.toString());
                        // throw new
                        // GeneralException("EXCEL模板人员：<br>"+strerror.toString()+"的唯一标识在业务表单中不存在!");
                    }
                } else {

                    if ("0".equals(onlyflag)) {
                        errorStr.append("EXCEL模板中的记录：<br>" + strerror.toString() + "在业务表单中不存在!");
                        throw new GeneralException("EXCEL模板中的记录：<br>" + strerror.toString() + "在业务表单中不存在!");
                    } else {
                        errorStr.append("EXCEL模板人员：<br>" + strerror.toString() + "的唯一标识在业务表单中不存在!");
                        throw new GeneralException("EXCEL模板人员：<br>" + strerror.toString() + "的唯一标识在业务表单中不存在!");
                    }

                }
            }
            if (updateFidsCount == 0)
                return new String[]{errorFileName,String.valueOf(updateCount),String.valueOf(importCount)};
            if (list2.size() > 0){
            	RowSet searchRowSet=null;
                try{
                	TemplateUtilBo utilBo= new TemplateUtilBo(this.conn,this.userview);
                	Boolean isAotuLog = paramBo.getIsAotuLog();
        			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
        			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(ins_id)){
        				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(ins_id);
        				if(haveReject){
        					isAotuLog=true;
        				}
        			}
                	if(isAotuLog&&!("0".equalsIgnoreCase(ins_id)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
		            	TempletChgLogBo chgLogBO=new TempletChgLogBo(this.conn,this.userview,paramBo);
		            	ArrayList cellList= utilBo.getAllCell(Integer.parseInt(this.tabid));
		            	//syl 58018 V771包：renn1登陆，修改信息项之后将模板导入，变动的信息项需要来回进模板2次才能看到，不能及时刷新
		            	//产生原因：cellList2.remove(i); 虽然会提高查询效率，但是会删除变化后的 指标，因为有缓冲缘故，导致不能及时刷新
		            	ArrayList cellList2=(ArrayList) cellList.clone();
		            	ArrayList setBoList=new ArrayList();
		            	for(int j=0;j<filedUpdateList.size();j++){
		            		String fieldname = (String) filedUpdateList.get(j);
		            		for(int i=0;i<cellList2.size();i++){
		            			TemplateSet setBo = (TemplateSet) cellList2.get(i);
		            			String tableFieldName = setBo.getTableFieldName();
		            			if(fieldname.equalsIgnoreCase(tableFieldName)){
		            				setBoList.add(setBo);
		            				cellList2.remove(i);
		            				break;
		            			}
		            		}
		            	}
		            	 if ("0".equals(onlyflag)) {
		                     if (!table_name.startsWith("templet")) {
		                    	 for(int k=0;k<list2.size();k++){
		                    		 ArrayList list = (ArrayList) list2.get(k);
		                    		 if (getBo() != null && getBo().getInfor_type() == 1) {
		                    			 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", list.get(list.size()-2)+"`"+list.get(list.size()-1), table_name, this.bo.getInfor_type());
		                             } else if (getBo() != null && getBo().getInfor_type() == 2) {
		                    			 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", list.get(list.size()-1)+"", table_name, this.bo.getInfor_type());
		                             } else if (getBo() != null && getBo().getInfor_type() == 3) {
		                    			 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", list.get(list.size()-1)+"", table_name, this.bo.getInfor_type());
		                             } else {
		                    			 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", list.get(list.size()-2)+"`"+list.get(list.size()-1), table_name, this.bo.getInfor_type());
		                             }
		                    	 }
		                     }
		                 }else{
		                	 for(int k=0;k<list2.size();k++){
		                		
		                		 ArrayList list = (ArrayList) list2.get(k);
		                    	 if (!table_name.startsWith("templet")) {
		                    		 if (getBo() != null && getBo().getInfor_type() == 1) {
				                    	 String search="select basepre,a0100 from "+table_name+" where "+onlyname + "_" + onlyflag+"=?";
				                    	 ArrayList seachList=new ArrayList();
				                    	 seachList.add(list.get(list.size()-1));
				                    	 searchRowSet=dao.search(search,seachList);
				                    	 if(searchRowSet.next()){
				                    		 String a0100=searchRowSet.getString("a0100");
				                    		 String basepre=searchRowSet.getString("basepre");
				                    		 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0",basepre+"`"+a0100, table_name, this.bo.getInfor_type());
				                    	 }
		                             } else if (getBo() != null && getBo().getInfor_type() == 2) {
		                            	 String search="select b0110 from "+table_name+" where "+onlyname + "_" + onlyflag+"=?";
				                    	 ArrayList seachList=new ArrayList();
				                    	 seachList.add(list.get(list.size()-1));
				                    	 searchRowSet=dao.search(search,seachList);
				                    	 if(searchRowSet.next()){
				                    		 String b0110=searchRowSet.getString("b0110");
				                    		 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", b0110+"", table_name, this.bo.getInfor_type());
				                    	 }
		                             } else if (getBo() != null && getBo().getInfor_type() == 3) {
		                            	 String search="select e01a1 from "+table_name+" where "+onlyname + "_" + onlyflag+"=?";
				                    	 ArrayList seachList=new ArrayList();
				                    	 seachList.add(list.get(list.size()-1));
				                    	 searchRowSet=dao.search(search,seachList);
				                    	 if(searchRowSet.next()){
				                    		 String e01a1=searchRowSet.getString("e01a1");
				                    		 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0", e01a1+"", table_name, this.bo.getInfor_type());
				                    	 }
		                             } else {
		                            	 String search="select basepre,a0100 from "+table_name+" where "+onlyname + "_" + onlyflag+"=?";
				                    	 ArrayList seachList=new ArrayList();
				                    	 seachList.add(list.get(list.size()-1));
				                    	 searchRowSet=dao.search(search,seachList);
				                    	 if(searchRowSet.next()){
				                    		 String a0100=searchRowSet.getString("a0100");
				                    		 String basepre=searchRowSet.getString("basepre");
				                    		 chgLogBO.insertOrUpdateAllLogger(filedUpdateList, setBoList, list, "0",  "0",basepre+"`"+a0100, table_name, this.bo.getInfor_type());
				                    	 }
		                             }
		                    	 }
		                	 }
		                 }
		            }
                }catch(Exception ex){
                	ex.printStackTrace();
                }finally {
					PubFunc.closeDbObj(searchRowSet);
				}
                updateCount=0;
              //syl 55865 hotfixes包机构模板：新增机构下载模板导入数据，提示成功修改2条，实际导入了3条
                int[] upsucess=dao.batchUpdate(sql.toString(), list2);
                if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                	updateCount=list2.size();
                }else {
                	for(int k=0;k<upsucess.length;k++){
                		updateCount+=upsucess[k];
                	}
                }
            }
            if (!table_name.startsWith("templet") && (this.operationtype == 0 || this.operationtype == 5) && insertlist.size() > 0) {
                //syl 55865 hotfixes包机构模板：新增机构下载模板导入数据，提示成功修改2条，实际导入了3条
            	int[] upsuccess2=dao.batchUpdate(insertsql.toString(), insertlist);
            	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            		importCount=insertlist.size();
            	}else {
            		for(int u=0;u<upsuccess2.length;u++){
                    	importCount+=upsuccess2[u];
                    }
            	}
                
            }
            // 更新自动生成序号
            if (listxuhao.size() > 0) {
                if ("1".equals(this.bo.getId_gen_manual())) {

                } else {
                    for (int i = 0; i < listxuhao.size(); i++) {
                        this.bo.filloutSequence("" + listxuhao.get(i), this.bo.getDest_base(), table_name);
                    }
                }

            }
            if (this.operationtype == 0) {
                if (sql.toString().indexOf("a0101_2") != -1 || insertsql.toString().indexOf("a0101_2") != -1) {
                    dao.update(" update " + table_name + " set a0101_1= a0101_2 ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{errorFileName,String.valueOf(updateCount),String.valueOf(importCount)};
    
	}
	
	public String[] importMainExcel(Sheet sheet, String table_name, HashMap nameMap, HashMap name2map, HashMap map,
			HashMap codeColMap, ArrayList listxuhao, ArrayList insertlist, HashMap tablemap, StringBuffer errorStr,
			ArrayList list2, int updateFidsCount, StringBuffer sql, StringBuffer insertsql, String onlyflag,
			String onlyname, int onlynamesit, HashMap onlynameMap, File form_file, HashMap codeItemColMap,
			ArrayList codeLeafSetList, ArrayList mainImmportList, String onlyKey, HashMap chidlFatherMap,
			TemplateParam paramBo, ArrayList filedUpdateList, String ins_id) {
		String file_name = form_file.getName().substring(0, form_file.getName().length() - 4) ;
		return importMainExcel(sheet, table_name, nameMap, name2map, map, codeColMap, listxuhao, insertlist, tablemap,
				errorStr, list2, updateFidsCount, sql, insertsql, onlyflag, onlyname, onlynamesit, onlynameMap,
				file_name, codeItemColMap, codeLeafSetList, mainImmportList, onlyKey, chidlFatherMap, paramBo,
				filedUpdateList, ins_id);
	}

    public int importSubExcel(Sheet sheet, String table_name, HashMap nameMap, HashMap map, ArrayList listxuhao, ArrayList insertlist, HashMap tablemap, StringBuffer errorStr, ArrayList list2, int updateFidsCount, String onlyflag, String onlyname, HashMap onlynameMap, String setname,ArrayList subImpeopleList,String onlyKey,HashMap chidlFatherMap,TemplateSet cellBo,TemplateParam paramBo,String ins_id) {
        int subUpdateCount=0;
        try {
            Row row = sheet.getRow(0);
            ArrayList dbList=DataDictionary.getDbpreList();
            String sheetname = sheet.getSheetName();
            int cols = row.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            RowSet rset = null;
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer codeBuf = new StringBuffer();
            int i9999sit = 0;
            int onlynamesit = 0;
            boolean onlynamesitflag = false;
            String fieldnames = "";
            HashMap codeColMap = new HashMap();
            HashMap name2map = new HashMap();
            StringBuffer insertsql = new StringBuffer();
            StringBuffer updatesql = new StringBuffer();
            StringBuffer delsql = new StringBuffer();
            boolean titleflag = false;
            HashMap filedsit = new HashMap();
            HashMap filedname = new HashMap();
            HashMap onlynamelistmap = new HashMap();
            ArrayList fieldlist = new ArrayList();
            String onlynametitle = "";
            String codeSetStr ="";
            HashMap codeLeafItefColMap = new HashMap();//存放控制只选择叶子节点代码类的叶子节点代码
            ArrayList codeLeafSetColList = new ArrayList();//存放控制只选择叶子节点的代码类
            map = new HashMap();
            for (short c = 0; c < cols; c++) {

                Cell cell = row.getCell(c);
                if (cell != null) {
                    String title = "";
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_FORMULA :
                            break;
                        case Cell.CELL_TYPE_NUMERIC :
                            double y = cell.getNumericCellValue();
                            title = Double.toString(y);
                            break;
                        case Cell.CELL_TYPE_STRING :
                            title = cell.getStringCellValue();
                            break;
                        default :
                            title = "";
                    }
                    String field = cell.getCellComment().getString().toString();
                    fieldnames += field + ",";
                    filedsit.put("" + c, field.toLowerCase());
                    filedname.put(field.toLowerCase(), "" + c);
                    if ("".equals(field.trim()))
                        titleflag = true;
                    if ("i9999".equalsIgnoreCase(field)) {
                        i9999sit = c;
                        continue;
                    }
                    if (field.equalsIgnoreCase(onlyname)) {
                        onlynamesit = c;
                        onlynamesitflag = true;
                        onlynametitle = title;
                        continue;
                    }
                    fieldlist.add(field);
                    if ("".equals(title.trim()))
                        titleflag = true;

                    String tempfield = "";
                    tempfield = field;

                    if (field.indexOf("_") != -1) {
                        continue;
                    }

                    String itemtype = "";
                    String codesetid = "";
                    if (DataDictionary.getFieldItem(tempfield) != null && DataDictionary.getFieldItem(tempfield).getCodesetid().length() > 0) {
                        codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();
                        itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();
                    } else {
                        codesetid = "0";
                    }

                    if (field.indexOf("_1") != -1)
                        continue;
                    if ("codesetid".equalsIgnoreCase(tempfield) || "codeitemdesc".equalsIgnoreCase(tempfield) || "corcode".equalsIgnoreCase(tempfield) || "parentid".equalsIgnoreCase(tempfield) || "start_date".equalsIgnoreCase(tempfield)) {

                    } else {
                        // if(!this.userview.isSuper_admin()&&!this.userview.analyseFieldPriv(tempfield).equalsIgnoreCase("2")&&this.bo.getUnrestrictedMenuPriv_Input().equals("0"))
                        // continue; //无权限的去掉
                        if (this.bo.getOpinion_field() != null && this.bo.getOpinion_field().length() > 0 && this.bo.getOpinion_field().equalsIgnoreCase(tempfield))
                            continue;
                    }

                    if (!"0".equals(codesetid))// 如果是代码类
                    {
                        if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                        	if(codeSetStr.trim().length()==0){
                        		codeSetStr="'"+codesetid+"'";
                        	}else
                        		codeSetStr=codeSetStr+",'"+codesetid+"'";
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where upper(codesetid)='" + codesetid + "'   union all ");
                        } else {
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)='" + codesetid
                            // 因为导入的时候有可能更新为非叶子机构所以在此放开限制为叶子部门的代码 + "' and
                            // codeitemid not in (select parentid from
                            // organization where codesetid='" + codesetid + "')
                            // union all ");
                                    + "' union all ");
                        }
                    } else {// 如果不是代码类
                        if ("parentid".equals(tempfield) && this.bo.getInfor_type() != 1) {
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where upper(codesetid)<>'@K' union all ");
                        }
                    }
                    // if ("a0100,a0101".indexOf(tempfield.toLowerCase()) ==
                    // -1)//单位 部门 姓名字段不更新b0110,e01a1,e0122,
                    // {
                    name2map.put(new Short(c), field + ":" + cell.getStringCellValue());

                    // if(itemtype.equals("M"))
                    // continue;//大字段类型单独处理
                    // insertsql.append(field+",");
                    // tempinsertstr.append("?,");
                    // sql.append(field + "=?,");
                    map.put(new Short(c), field + ":" + cell.getStringCellValue());
                    updateFidsCount++;
                    // }
                } else
                    break;
            }
            if (!onlynamesitflag) {// 不存在唯一指标
                errorStr.append(sheetname + "页中不存在唯一标识，该页数据无法操作！");
            }
            if (codeBuf.length() > 0) {
                codeBuf.setLength(codeBuf.length() - " union all ".length());
                try {
                    rset = dao.search(codeBuf.toString());
                    while (rset.next()) {
                        codeColMap.put(rset.getString("codesetid") + "a04v2u" + rset.getString("codeitemid") + ":" + rset.getString("codeitemdesc"), rset.getString("codeitemid"));
                        codeColMap.put(rset.getString("codesetid") + "a04v2u" + rset.getString("codeitemdesc"), rset.getString("codeitemid"));
                        codeColMap.put(rset.getString("codesetid") + "a04v2u" + rset.getString("codeitemid"), rset.getString("codeitemid"));// 考虑手工写入指标代码
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if(codeSetStr.length()>0)
            {
     			String searchCodeset="SELECT  cm.codesetid,cm.codeitemid ,cm.codeitemdesc FROM codeitem cm LEFT JOIN ( SELECT  COUNT(1) AS num ,codesetid,parentid FROM    codeitem WHERE codeitemid<>parentid and  "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date   GROUP BY parentid ,codesetid)  cnum ON cm.codesetid = cnum.codesetid AND cm.codeitemid = cnum.parentid left join codeset c on cm.codesetid=c.codesetid WHERE "+Sql_switcher.isnull("cnum.num", "0")+"= 0 and upper(cm.codesetid) in("+ codeSetStr + ") and "+Sql_switcher.isnull("c.leaf_node","0")+"='1'  ";
            	RowSet rs=null;
                try {
                    rs = dao.search(searchCodeset.toString());
                    while (rs.next()) {
                    	if(!codeLeafSetColList.contains(rs.getString("codesetid")))
                    		codeLeafSetColList.add(rs.getString("codesetid"));
                    	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid") + ":" + rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));//liuyz 代码项中前后有空格，在导入数据会去掉空格时造成匹配失败，不能导入。
                    	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc").trim(), rs.getString("codeitemid"));
                    	codeLeafItefColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemid"), rs.getString("codeitemid"));// 考虑手工写入指标代码
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
					PubFunc.closeDbObj(rs);
				}
            }

            if (fieldnames.toUpperCase().indexOf("I9999") == -1 || fieldnames.toUpperCase().indexOf(onlyname.toUpperCase()) == -1)// 如果子集中不含有I9999列或不含唯一性标识列那么无法导入
                return subUpdateCount;
            if (titleflag)
                return subUpdateCount;

            int num = 0;
            int num2 = 1;

            for (int j = 1; j < rows; j++) {
                int num3 = 0;
                int num4 = 0;

                ArrayList onlynamelist = new ArrayList();
                row = sheet.getRow(j);
                if (row == null)
                    row = sheet.createRow(j);

                Cell flagCol = null;

                flagCol = row.getCell((short) onlynamesit);

                // 遇到整个行都是空的截止
                boolean flag = false;// 判断是否有单元格不为null
                String onlynamevalue = "";
                if (flagCol != null) {
                    switch (flagCol.getCellType()) {
                        case Cell.CELL_TYPE_BLANK :
                            break;
                        case Cell.CELL_TYPE_STRING :
                            if (flagCol.getRichStringCellValue().toString().trim().length() == 0) {
                                continue;
                            } else {
                                onlynamevalue = flagCol.getRichStringCellValue().toString().trim();
                            }
                            break;
                    }
                }

                ArrayList list = new ArrayList();

                Cell flagCol2 = row.getCell(i9999sit);// i9999
                if (flagCol2 != null) {
                    switch (flagCol2.getCellType()) {
                        case Cell.CELL_TYPE_BLANK :
                            break;
                        case Cell.CELL_TYPE_STRING :
                            if (flagCol2.getRichStringCellValue().toString().trim().length() == 0)
                                continue;
                            else {
                                // onlynameMap2.put(flagCol2.getRichStringCellValue().toString().trim(),
                                // flagCol.2getRichStringCellValue().toString().trim());
                            }
                            break;
                    }
                }

                StringBuffer priverror = new StringBuffer();
                // RecordVo vo = new RecordVo(table_name);
                HashMap fatherCodeCadeMap=new HashMap();
                for (short c = 0; c < cols; c++) {
                    Cell cell1 = row.getCell(c);
                    if (cell1 == null && i9999sit == c) {
                        list.add("-1");
                        continue;
                    }
                    if (cell1 != null && flagCol != null) {
                        if (!flag) {// 判断当前行是否有值 wangrd 2015-02-07
                            String cellValue = "";
                            switch (cell1.getCellType()) {
                                case Cell.CELL_TYPE_FORMULA :
                                    cellValue = "";
                                    break;
                                case Cell.CELL_TYPE_BOOLEAN :
                                    cellValue = String.valueOf(cell1.getBooleanCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC :
                                    cellValue = String.valueOf(cell1.getNumericCellValue());
                                    break;
                                case Cell.CELL_TYPE_STRING :
                                    cellValue = cell1.getRichStringCellValue().toString();
                                    break;
                                case Cell.CELL_TYPE_BLANK :
                                    cellValue = "";
                                    break;
                                default :
                                    cellValue = cell1.getRichStringCellValue().toString();
                            }
                            if (cellValue != null && cellValue.length() > 0) {
                                flag = true;
                            }
                        }

                        String fieldItems = (String) map.get(new Short(c));
                        // String fieldtemp = (String)name2map.get(new
                        // Short(c));
                        if (onlynamesit == c || i9999sit == c) {
                            String value = "";
                            switch (cell1.getCellType()) {
                                case Cell.CELL_TYPE_BLANK :
                                    break;
                                case Cell.CELL_TYPE_STRING :
                                    if (cell1.getRichStringCellValue().toString().trim().length() == 0)
                                        continue;
                                    else {
                                        value = cell1.getRichStringCellValue().toString().trim();
                                    }
                                    break;
                            }
                            num3++;
                            list.add(value);
                            continue;
                        }
                        if (fieldItems == null)// 过滤掉只读的列
                            continue;

                        String[] fieldItem = fieldItems.split(":");
                        String field = fieldItem[0];
                        String fieldName = fieldItem[1];
                        String tempfield = "";
                        if (field.indexOf("_") != -1)
                            tempfield = field.substring(0, field.lastIndexOf("_"));
                        else
                            tempfield = field;
                        String itemtype = "";
                        String codesetid = "";
                        int decwidth = 0;
                        int itemlength = 0;
                        if (DataDictionary.getFieldItem(tempfield) != null) {
                            itemtype = DataDictionary.getFieldItem(tempfield).getItemtype();
                            codesetid = DataDictionary.getFieldItem(tempfield).getCodesetid();
                            decwidth = DataDictionary.getFieldItem(tempfield).getDecimalwidth();
                            itemlength = DataDictionary.getFieldItem(tempfield).getItemlength();
                        }
                        // if(itemtype.equals("M")){
                        // vo.setString(field,
                        // cell1.getRichStringCellValue().toString()) ;
                        // continue;//大字段类型单独处理
                        // }
                        if ("codesetid".equalsIgnoreCase(tempfield) || "codeitemdesc".equalsIgnoreCase(tempfield) || "corcode".equalsIgnoreCase(tempfield) || "parentid".equalsIgnoreCase(tempfield) || "start_date".equalsIgnoreCase(tempfield)) {
                            if ("start_date".equalsIgnoreCase(tempfield)) {
                                itemtype = "D";
                                itemlength = 10;
                            } else {
                                itemtype = "A";
                                itemlength = 50;
                            }

                        }

                        String value = "";

                        switch (cell1.getCellType()) {
                            case Cell.CELL_TYPE_FORMULA :
                                break;
                            case Cell.CELL_TYPE_NUMERIC :
                                if ("D".equals(itemtype)) {
                                    value = "" + cell1.getNumericCellValue();
                                    
                                    if (value != null && value.length() > 0 && value.matches("^[+-]?[\\d]*[.]?[\\d]+")) {
                                        Date d_value = cell1.getDateCellValue();
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        value = df.format(d_value);
                                    }
                                    if (value.length() > 0 && this.isDataType(decwidth, itemtype, value)) {
                                        list.add(value);
                                    } else
                                        list.add(null);

                                } else {

                                    double y = cell1.getNumericCellValue();

                                    value = Double.toString(y);
                                    value = PubFunc.round(value, decwidth);
                                    list.add(new Double((PubFunc.round(value, decwidth))) + "");
                                }

                                break;
                            case Cell.CELL_TYPE_STRING :
                                value = cell1.getRichStringCellValue().toString();
                                value = value.replaceAll("　", " ").trim();
                                if (!"0".equals(codesetid) && !"".equals(codesetid)) {
                                	String key = codesetid + "a04v2u" + value.trim();
                                    if (codeColMap.get(key) != null) {
                                        String value_code = (String) codeColMap.get(key);
                                        key = codesetid + "a04v2u" + value_code.trim();
                                        if (codeColMap.get(key) == null && "UM".equalsIgnoreCase(codesetid))
                                            key = "UNa04v2u" + value.trim();
                                        if(codeLeafSetColList.contains(codesetid)&&!codeLeafItefColMap.containsKey(key)){
                                    		String msg="\"" + fieldName + "\"列仅可选择末级代码，\""+value+"\"不可选择!";
                                    		errorStr.append(msg);
                                    		throw new GeneralException(msg);
                                    	}
                                        value=value_code;//bug 39281 应该存入代码否则前台保存时重组子集会变成

                                    } else
                                        value = null;
                                } else {
                                    if (this.bo != null && this.bo.getInfor_type() != 1 && field.startsWith("codesetid_2")) {
                                        if ("部门".equals(value))
                                            value = "UM";
                                        if ("单位".equals(value))
                                            value = "UN";
                                    }
                                    if (this.bo != null && this.bo.getInfor_type() != 1 && field.startsWith("parentid")) {
                                        if (codeColMap.get("UN" + "a04v2u" + value.trim()) != null)
                                            value = (String) codeColMap.get("UN" + "a04v2u" + value.trim());
                                        else if (codeColMap.get("UM" + "a04v2u" + value.trim()) != null)
                                            value = (String) codeColMap.get("UM" + "a04v2u" + value.trim());
                                        else
                                            value = null;
                                    }
                                }
                                // 单位，部门，职位是否进行了权限控制
                                // if(codesetid.equals("UN")||codesetid.equals("UM")||codesetid.equals("@K")){
                                // String error
                                // =isPriv_ctrl(cell_param_map,field,value,j,fieldName);
                                // if(error.length()>0)
                                // priverror.append(error);
                                // }
                                if ("D".equals(itemtype)) {
                                    if (value.length() > 0 && this.isDataType(decwidth, itemtype, value)) {
                                        value = returnDataType(value);
                                        list.add(value.trim());
                                    } else
                                        list.add(null);
                                } else {
                                    list.add(value);
                                }

                                break;
                            case Cell.CELL_TYPE_BLANK :// 如果什么也不填的话数值就默认更新为0
                                if ("N".equals(itemtype)) {
                                	if(value==null||value.trim().length()==0){ //bug 32919 数值型指标不填写默认为0，改为空
                                		value="";
                                		list.add("");
                                	}
                                	else{
	                                    value = PubFunc.round(value, decwidth);
	                                    list.add(new Double(value) + "");
                                	}
                                } else if ("D".equals(itemtype)) {
                                    if (value.length() > 0 && this.isDataType(decwidth, itemtype, value)) {
                                        value = returnDataType(value);
                                        list.add(value.trim());
                                    } else
                                        list.add(null);

                                } else
                                    list.add(null);
                                break;
                            default :
                                list.add(null);
                        }
                        fatherCodeCadeMap.put(field,value+":"+fieldName);
                        String msg = "";
                        if ("N".equals(itemtype) || "D".equals(itemtype)) {
                            if (value != null)
                                value = value.trim();
                            if (value.length() > 0 && !this.isDataType(decwidth, itemtype, value)) {
                                if ("D".equals(itemtype))
                                    msg = "源数据(" + fieldName + ")第" + (j + 1) + "行中数据:" + value + " 不符合格式<br>格式为yyyy-MM-dd或者yyyy.MM.dd或者yyyy/MM/dd<br>请将Excel中填写日期内容的单元格式设置为文本型!<br><br>";
                                else
                                    msg = "源数据(" + fieldName + ")第" + (j + 1) + "行中数据:" + value + " 不符合格式!<br><br>";
                                errorStr.append(msg);
                                // throw new GeneralException(msg);
                            }
                        }
                        // if(value!=null&&itemtype.equals("D")&&value.length()>10)
                        // throw new GeneralException("源数据(" + fieldName +
                        // ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
                        if (value != null && "N".equals(itemtype) && value.length() > itemlength) {
                            // if(decwidth>0&&value.length()>itemlength+decwidth+1)
                            // throw new GeneralException("源数据(" + fieldName +
                            // ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
                        }
                        if (value != null && "A".equals(itemtype)) {
                            byte[] valuelength = value.getBytes();
                            // if(valuelength.length>itemlength)
                            // throw new GeneralException("源数据(" + fieldName +
                            // ")第"+(j+1)+"行中数据:长度超过数据库定义的长度，导入失败!");
                        }
                        if(StringUtils.isNotBlank(value)&&subImpeopleList.contains(field.toLowerCase())){
                        	String[] personList = value.split("、");
                        	for(int i=0;i<personList.length;i++){
                        		String person=personList[i];
                        		String[] personInfo = person.split(":");
                        		if(personInfo.length!=2){
                        			errorStr.append("源数据(" +sheetname +")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                                    throw new GeneralException("源数据(" +sheetname+ ")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                        		}
                        		String a0101=personInfo[0];
                        		String onlyKeyValue=personInfo[1];
                        		String searchSql="";
                        		for(int dbnum=0;dbnum<dbList.size();dbnum++){
            		        		if(StringUtils.isNotBlank(searchSql)){
            		        			searchSql+=" UNION  ";
            		        		}
            		        		searchSql+="select '"+dbList.get(dbnum)+"' as pre,a0100 from "+dbList.get(dbnum)+"a01 where lower(a0101)=lower('"+a0101+"') and "+onlyKey+"='"+onlyKeyValue+"'";
            		        	}
                        		try{
	                        		rset=dao.search(searchSql);
                        		}catch(Exception ex){
                        			ex.printStackTrace();
                        			errorStr.append("源数据(" + sheetname+ ")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                        			throw new GeneralException("源数据(" +sheetname + ")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                        		}
                        		if(!rset.next()){
                        			errorStr.append("源数据(" + sheetname+ ")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                        			throw new GeneralException("源数据(" +sheetname + ")第" + (j + 1) + "行中"+ fieldName +"列数据:"+person+"信息有误，导入失败!");
                        		}
                        	}
                        }
                    }
                    if (cell1 == null && c != cols) {
                        if (map.get(new Short(c)) != null) {
                            list.add(null);
                        }
                    }
                    num3++;
                    if (cell1 == null || flagCol == null || list.get(list.size() - 1) == null || "".equals(list.get(list.size() - 1))) {

                        num4++;

                    } else {
                        // System.out.println(list.get(list.size()-1));
                    }
                }
                // if(priverror.length()>0)
                // throw new Exception(priverror.toString());
                if (num3 == num4)
                    continue; // 跳过该行
                if (!flag)// 整行为空过滤掉
                    continue;
                if (onlynamevalue == null || onlynamevalue.trim().length() == 0) {
                    if (list.size() > 0) {
                        boolean listflag = false;
                        for (int w = 0; w < list.size(); w++) {
                            if (list.get(w) != null && !"".equals(list.get(w).toString().trim()) && i9999sit != w) {
                                listflag = true;
                                break;
                            }
                        }
                        if (listflag) {
                            errorStr.append(sheetname + "页中第" + (j + 1) + "行的唯一标识：" + onlynametitle + "存在空数据，该行没执行导入操作");
                            continue;
                        }
                    } else {
                        errorStr.append(sheetname + "页中第" + (j + 1) + "行的唯一标识：" + onlynametitle + "存在空数据，该行没执行导入操作");
                        continue;
                    }

                }
                Iterator iterator = chidlFatherMap.entrySet().iterator();
                StringBuffer cascadeerror = new StringBuffer("");
                while(iterator.hasNext()){
                	Entry entry=(Entry) iterator.next();
                	String key = (String) entry.getKey();
                	String fatherId=(String) entry.getValue();
                	String childValut = (String) fatherCodeCadeMap.get(key);
                	String childFieldName="";
                	if(StringUtils.isNotBlank(childValut)){
                		String[] split = childValut.split(":");
                		childValut=split[0];
                		childFieldName=split[1];
                	}
                	String fatherValut = (String) fatherCodeCadeMap.get(fatherId);
                	String fatherFieldName="";
                	if(StringUtils.isNotBlank(fatherValut)){
                		String[] split = fatherValut.split(":");
                		fatherValut=split[0];
                		fatherFieldName=split[1];
                	}
                	if(StringUtils.isNotBlank(childValut)&&fatherValut!=null){
                		if("".equalsIgnoreCase(fatherValut)||fatherValut==null){
                			cascadeerror.append("源数据(" +sheetname +")在第" + (j + 1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");
                		}else if(childValut.indexOf(fatherValut)!=0||childValut.indexOf(fatherValut)==0&&childValut.length()==fatherValut.length()){//bug 43490 关联指标选择相同代码值，应该提示。
                			cascadeerror.append("源数据(" +sheetname +")在第" + (j + 1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");
                		}
                	}
                }
                String tempstr = cascadeerror.toString();
                if (!"".equals(tempstr)) {// 如果是级联错误
                    errorStr.append(cascadeerror.toString());
                    throw new GeneralException(cascadeerror.toString());
                }
                ArrayList insert = (ArrayList) list.clone();
                // a0100s.append("'" + temp[1] + "',");
                // if(flagCol!=null){
                // list.add(onlynamevalue);
                // }

                list2.add(list);
                if (onlynamevalue != null) {
                    if (onlynamelistmap != null && onlynamelistmap.get(onlynamevalue.toLowerCase()) != null) {
                        onlynamelist = (ArrayList) onlynamelistmap.get(onlynamevalue.toLowerCase());
                        onlynamelist.add(list);
                        onlynamelistmap.put(onlynamevalue.toLowerCase(), onlynamelist);
                    } else {
                        onlynamelist.add(list);
                        onlynamelistmap.put(onlynamevalue.toLowerCase(), onlynamelist);
                    }
                }
                
                // listvo.add(vo);
            }
            // if(errorStr.toString().length()>2){
            // if(this.bo.getInfor_type()==1){
            // throw new
            // GeneralException("EXCEL模板隐藏的第一列主键标识串中的人员：<br>"+errorStr.toString()+"在业务表单中不存在!");
            // }else{
            // throw new
            // GeneralException("EXCEL模板隐藏的第一列主键标识串中的记录：<br>"+errorStr.toString()+"在业务表单中不存在!");
            // }
            //		    	
            // }
            rset = dao.search(" select " + onlyname + "_" + onlyflag + ",t_" + setname + "_2 from " + table_name + " ");
            Document doc = null;
            Element element = null;
            while (rset.next()) {
                String onlyvalue = rset.getString(onlyname + "_" + onlyflag);
                if (onlyvalue == null)
                    onlyvalue = "";
                String _onlyvalue = onlyvalue;
                onlyvalue = onlyvalue.toLowerCase();
                String setxml = Sql_switcher.readMemo(rset, "t_" + setname + "_2");
                String oldXml=setxml;
                StringBuffer buf = new StringBuffer();
                if (onlynameMap == null || (onlynameMap.get(onlyvalue) == null && onlynameMap.get(_onlyvalue) == null))
                    continue;
                if (onlynamelistmap == null || onlynamelistmap.get(onlyvalue) == null) {
                    if (setxml != null && setxml.length() > 3) {
                        doc = PubFunc.generateDom(setxml);
                        String xpath = "/records";

                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist2 = findPath.selectNodes(doc);
                        String columns = "";
                        if (childlist2 != null && childlist2.size() > 0) {
                            element = (Element) childlist2.get(0);
                            columns = element.getAttributeValue("columns");
                        }
                        String[] temps = columns.split("`");
                        xpath = "/records/record";
                        findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0) {

                            for (int j = 0; j < childlist.size(); j++) {
                                element = (Element) childlist.get(j);
                                Element parent = element.getParentElement();
                                parent.removeContent(element);
                            }
                        }
                        XMLOutputter outputter = new XMLOutputter();
                        Format format = Format.getPrettyFormat();
                        format.setEncoding("UTF-8");
                        outputter.setFormat(format);
                        setxml = outputter.outputString(doc);
                    }
                } else {
                    ArrayList onlylist = (ArrayList) onlynamelistmap.get(onlyvalue);
                    if (setxml == null || setxml.length() < 3) {

                        // if(reclist.size()==0||fieldlist.size()==0)
                        // return "";
                        buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
                        buf.append("<records columns=\"");
                        StringBuffer fields = new StringBuffer();
                        for (int i = 0; i < fieldlist.size(); i++) {
                            FieldItem item = DataDictionary.getFieldItem("" + fieldlist.get(i));
                            fields.append(item.getItemid());
                            fields.append("`");
                        }
                        fields.setLength(fields.length() - 1);
                        buf.append(fields.toString());
                        buf.append("\">");

                        for (int i = 0; i < onlylist.size(); i++) {
                            ArrayList valuelist = (ArrayList) onlylist.get(i);
                            buf.append("<record I9999=\"");
                            String value = "-1";
                            if (filedname != null && filedname.get("i9999") != null) {
                                value = "" + valuelist.get(Integer.parseInt("" + filedname.get("i9999")));
                                if (value.trim().length() == 0)
                                    value = "-1";
                            }
                            buf.append(value);
                            buf.append("\" deleted=\"0\"  ");
                            buf.append(" >");
                            fields.setLength(0);
                            for (int j = 0; j < valuelist.size(); j++) {
                                if (filedsit != null && filedsit.get("" + j) != null) {
                                    String name = "" + filedsit.get("" + j);
                                    if ("i9999".equalsIgnoreCase(name) || name.equalsIgnoreCase(onlyname))
                                        continue;
                                }
                                fields.append(valuelist.get(j) == null ? "" : valuelist.get(j));
                                fields.append("`");
                            }
                            fields.setLength(fields.length() - 1);
                            buf.append(fields.toString());
                            buf.append("</record>");
                        }
                        buf.append("</records>");
                        setxml = buf.toString();
                    } else {
                        LinkedHashMap i9999map = new LinkedHashMap();
                        HashMap i9999map2 = new HashMap();
                        int n = 0;
                        for (int i = 0; i < onlylist.size(); i++) {
                            ArrayList valuelist = (ArrayList) onlylist.get(i);
                            String value = "-1";
                            if (filedname != null && filedname.get("i9999") != null) {
                                value = "" + valuelist.get(Integer.parseInt("" + filedname.get("i9999")));
                                if (value.trim().length() == 0)
                                    value = "-1";
                            }
                            if ("-1".equals(value)) {
                                i9999map.put(value + "`" + n, valuelist);
                                n++;
                            } else {
                                i9999map2.put(value, valuelist);
                            }

                        }
                        doc = PubFunc.generateDom(setxml);
                        String xpath = "/records";

                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist2 = findPath.selectNodes(doc);
                        String columns = "";
                        if (childlist2 != null && childlist2.size() > 0) {
                            element = (Element) childlist2.get(0);
                            columns = element.getAttributeValue("columns");
                        }
                        String[] temps = columns.split("`");
                        xpath = "/records/record";
                        findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0) {
                            n = -1;
                            for (int j = 0; j < childlist.size(); j++) {
                                element = (Element) childlist.get(j);
                                String oldContentValue = element.getText() + "　`";// 在后面加上
                                                                                    // `的原因是如果最后一个没有内容那么是无法被分割出来的
                                String[] oldContenArray = oldContentValue.split("`");
                                String I9999 = element.getAttributeValue("I9999");
                                String edit = element.getAttributeValue("edit");
                                if (i9999map2 != null && i9999map2.get("" + I9999) == null && !"-1".equals(I9999)) {// 删掉的记录
                                    Element parent = element.getParentElement();
                                    parent.removeContent(element);
                                    i9999map2.remove("" + I9999);
                                    continue;
                                }
                                if ("-1".equals(I9999)) {
                                    n++;
                                }
                                if (i9999map != null && i9999map.get(I9999 + "`" + n) == null && "-1".equals(I9999)) {// 删掉的记录
                                    Element parent = element.getParentElement();
                                    parent.removeContent(element);
                                    i9999map.remove(I9999 + "`" + n);
                                    continue;
                                }
                                if ("-1".equals(I9999)) {
                                    ArrayList valuelist = (ArrayList) i9999map.get(I9999 + "`" + n);
                                    String context = "";
                                    for (int i = 0; i < temps.length; i++) {
                                        if (temps[i].trim().length() == 0)
                                            continue;
                                        String value = "";
                                        FieldItem field = DataDictionary.getFieldItem(temps[i]);
                                        if (field != null && filedname != null && filedname.get(field.getItemid().toLowerCase()) != null) {
                                            if (!this.userview.isSuper_admin() && !"2".equalsIgnoreCase(this.userview.analyseFieldPriv(field.getItemid().toLowerCase())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                                String tempsize = (String) filedname.get(field.getItemid().toLowerCase());
                                                int tempIntvalue = Integer.parseInt(tempsize);
                                                value = oldContenArray[tempIntvalue - 2];
                                            } else {
                                                value = (String) valuelist.get(Integer.parseInt("" + filedname.get(field.getItemid().toLowerCase())));
                                            }
                                            // String value =
                                            // (String)valuelist.get(Integer.parseInt(""+filedname.get(field.getItemid().toLowerCase())));
                                            if (value == null)
                                                value = "";
                                            context += value + "`";
                                        } else {
                                            context += "`";
                                        }
                                    }
                                    if (context.endsWith("`"))
                                        context = context.substring(0, context.length() - 1);
                                    element.setText(context);
                                    i9999map.remove(I9999 + "`" + n);
                                } else {
                                	if(StringUtils.isNotBlank(edit)&&"0".equals(edit)){//设置了历史记录只读的导入数据时不做修改
                                		i9999map2.remove("" + I9999);
                                		continue;
                                	}
                                    ArrayList valuelist = (ArrayList) i9999map2.get(I9999);
                                    String context = "";
                                    for (int i = 0; i < temps.length; i++) {
                                        if (temps[i].trim().length() == 0)
                                            continue;
                                        String value = "";
                                        FieldItem field = DataDictionary.getFieldItem(temps[i]);
                                        if (field != null && filedname != null && filedname.get(field.getItemid().toLowerCase()) != null) {
                                            if (!this.userview.isSuper_admin() && !"2".equalsIgnoreCase(this.userview.analyseFieldPriv(field.getItemid().toLowerCase())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                                String tempsize = (String) filedname.get(field.getItemid().toLowerCase());
                                                int tempIntvalue = Integer.parseInt(tempsize);
                                                value = oldContenArray[tempIntvalue - 2];
                                            } else {
                                                value = (String) valuelist.get(Integer.parseInt("" + filedname.get(field.getItemid().toLowerCase())));
                                            }
                                            if (value == null)
                                                value = "";
                                            context += value + "`";
                                        } else {
                                            context += "`";
                                        }
                                    }
                                    if (context.endsWith("`"))
                                        context = context.substring(0, context.length() - 1);
                                    element.setText(context);
                                    i9999map2.remove("" + I9999);
                                }
                            }
                        }
                        // 新增记录
                        Iterator iter = i9999map2.entrySet().iterator();
                        while (iter.hasNext()) {
                            Entry entry = (Entry) iter.next();
                            ArrayList valuelist = (ArrayList) entry.getValue();
                            String context = "";
                            for (int i = 0; i < temps.length; i++) {
                                if (temps[i].trim().length() == 0)
                                    continue;
                                String value = "";
                                FieldItem field = DataDictionary.getFieldItem(temps[i]);
                                if (field != null && filedname != null && filedname.get(field.getItemid().toLowerCase()) != null) {
                                    if (!this.userview.isSuper_admin() && !"2".equalsIgnoreCase(this.userview.analyseFieldPriv(field.getItemid().toLowerCase())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                        value = "";
                                    } else {
                                        value = (String) valuelist.get(Integer.parseInt("" + filedname.get(field.getItemid().toLowerCase())));
                                    }

                                    // String value =
                                    // (String)valuelist.get(Integer.parseInt(""+filedname.get(field.getItemid().toLowerCase())));
                                    if (value == null)
                                        value = "";
                                    context += value + "`";
                                } else {
                                    context += "`";
                                }
                            }
                            if (context.endsWith("`"))
                                context = context.substring(0, context.length() - 1);
                            if (childlist2 != null && childlist2.size() > 0) {
                                String value = "-1";
                                if (filedname != null && filedname.get("i9999") != null) {
                                    value = "" + valuelist.get(Integer.parseInt("" + filedname.get("i9999")));
                                    if (value.trim().length() == 0)
                                        value = "-1";
                                }
                                element = (Element) childlist2.get(0);
                                Element children = new Element("record");
                                children.setAttribute("I9999", value);
                                children.setAttribute("deleted", "0");
                                children.setText(context);
                                element.addContent(children);

                            }
                        }
                        iter = i9999map.entrySet().iterator();
                        while (iter.hasNext()) {
                            Entry entry = (Entry) iter.next();
                            ArrayList valuelist = (ArrayList) entry.getValue();
                            String context = "";
                            for (int i = 0; i < temps.length; i++) {
                                if (temps[i].trim().length() == 0)
                                    continue;
                                String value = "";
                                FieldItem field = DataDictionary.getFieldItem(temps[i]);
                                if (field != null && filedname != null && filedname.get(field.getItemid().toLowerCase()) != null) {
                                    if (!this.userview.isSuper_admin() && !"2".equalsIgnoreCase(this.userview.analyseFieldPriv(field.getItemid().toLowerCase())) && "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                                        value = "";
                                    } else {
                                        value = (String) valuelist.get(Integer.parseInt("" + filedname.get(field.getItemid().toLowerCase())));
                                    }
                                    // String value =
                                    // ""+valuelist.get(Integer.parseInt(""+filedname.get(field.getItemid().toLowerCase())));
                                    if (value == null)
                                        value = "";
                                    context += value + "`";
                                } else {
                                    context += "`";
                                }
                            }
                            if (context.endsWith("`"))
                                context = context.substring(0, context.length() - 1);
                            if (childlist2 != null && childlist2.size() > 0) {
                                element = (Element) childlist2.get(0);
                                Element children = new Element("record");
                                children.setAttribute("I9999", "-1");
                                children.setAttribute("deleted", "0");
                                children.setText(context);
                                element.addContent(children);

                            }
                        }
                        XMLOutputter outputter = new XMLOutputter();
                        Format format = Format.getPrettyFormat();
                        format.setEncoding("UTF-8");
                        outputter.setFormat(format);
                        setxml = outputter.outputString(doc);

                    }
                }
                RowSet searchRowSet=null;
                try{
                	TemplateUtilBo utilBo= new TemplateUtilBo(this.conn,this.userview);//bug 49243
        			Boolean isAutoLog=paramBo.getIsAotuLog();
        			Boolean isRejectAutoLog=paramBo.getIsRejectAotuLog();
        			if(isRejectAutoLog&&!"0".equalsIgnoreCase(ins_id)){
        				Boolean isHaveRejecttask=utilBo.isHaveRejectTaskByInsId(ins_id);
        				if(isHaveRejecttask){
        					isAutoLog=true;
        				}
        			}
        			if(isAutoLog&&!("0".equalsIgnoreCase(ins_id)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
        				TempletChgLogBo chgLogBO=new TempletChgLogBo(this.conn,this.userview,paramBo);
	                	if(!table_name.startsWith("templet_")){
		                	 if (getBo() != null && getBo().getInfor_type() == 1) {
		                    	 String search="select basepre,a0100 from "+table_name+" where lower("+onlyname + "_" + onlyflag+")=?";
		                    	 ArrayList seachList=new ArrayList();
		                    	 seachList.add(onlyvalue.toLowerCase());
		                    	 searchRowSet=dao.search(search,seachList);
		                    	 if(searchRowSet.next()){
		                    		 String a0100=searchRowSet.getString("a0100");
		                    		 String basepre=searchRowSet.getString("basepre");
		                         	 chgLogBO.insertOrUpdateOneSubsetLogger("t_" + setname + "_2", oldXml, setxml, "0", "0", basepre+"`"+a0100, table_name, this.bo.getInfor_type(), cellBo);
		                    	 }
		                     } else if (getBo() != null && getBo().getInfor_type() == 2) {
		                    	 String search="select b0110 from "+table_name+" where lower("+onlyname + "_" + onlyflag+")=?";
		                    	 ArrayList seachList=new ArrayList();
		                    	 seachList.add(onlyvalue.toLowerCase());
		                    	 searchRowSet=dao.search(search,seachList);
		                    	 if(searchRowSet.next()){
		                    		 String b0110=searchRowSet.getString("b0110");
		                         	 chgLogBO.insertOrUpdateOneSubsetLogger("t_" + setname + "_2", oldXml, setxml, "0", "0", b0110, table_name, this.bo.getInfor_type(), cellBo);
		                    	 }
		                     } else if (getBo() != null && getBo().getInfor_type() == 3) {
		                    	 String search="select e01a1 from "+table_name+" where lower("+onlyname + "_" + onlyflag+")=?";
		                    	 ArrayList seachList=new ArrayList();
		                    	 seachList.add(onlyvalue.toLowerCase());
		                    	 searchRowSet=dao.search(search,seachList);
		                    	 if(searchRowSet.next()){
		                    		 String e01a1=searchRowSet.getString("e01a1");
		                         	 chgLogBO.insertOrUpdateOneSubsetLogger("t_" + setname + "_2", oldXml, setxml, "0", "0", e01a1, table_name, this.bo.getInfor_type(), cellBo);
		                    	 }
		                     } else {
		                    	 String search="select basepre,a0100 from "+table_name+" where lower("+onlyname + "_" + onlyflag+")=?";
		                    	 ArrayList seachList=new ArrayList();
		                    	 seachList.add(onlyvalue.toLowerCase());
		                    	 searchRowSet=dao.search(search,seachList);
		                    	 if(searchRowSet.next()){
		                    		 String a0100=searchRowSet.getString("a0100");
		                    		 String basepre=searchRowSet.getString("basepre");
			                         chgLogBO.insertOrUpdateOneSubsetLogger("t_" + setname + "_2", oldXml, setxml, "0", "0", basepre+"`"+a0100, table_name, this.bo.getInfor_type(),cellBo);
		                    	 }
		                     }
	                	}
        			}
                }catch(Exception ex){
                	ex.printStackTrace();
                }finally {
					PubFunc.closeDbObj(searchRowSet);
				}
                String updateSql = " update " + table_name + " set t_" + setname + "_2=? where lower(" + onlyname + "_" + onlyflag + ")='" + onlyvalue.toLowerCase() + "'";
                ArrayList param = new ArrayList();
                param.add(setxml.toString());
                dao.update(updateSql,param);
                subUpdateCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return subUpdateCount;
    }
    public String isPriv_ctrl(HashMap cell_param_map, String field) {
        String sub_domain = "";
        Document doc = null;
        Element element = null;
        StringBuffer sb = new StringBuffer();
        LazyDynaBean bean = (LazyDynaBean) cell_param_map.get(field);
        if (bean != null && bean.get("sub_domain") != null)
            sub_domain = (String) bean.get("sub_domain");
        sub_domain = SafeCode.decode(sub_domain);
        if (sub_domain != null && sub_domain.length() > 0) {
            try {
                doc = PubFunc.generateDom(sub_domain);
                String xpath = "/sub_para/para";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist = findPath.selectNodes(doc);
                if (childlist != null && childlist.size() > 0) {
                    element = (Element) childlist.get(0);
                    String priv = (String) element.getAttributeValue("limit_manage_priv");
                    if ("1".equals(priv)) {
                        if (!this.userview.isSuper_admin()) {
                            if (this.userview.getManagePrivCodeValue() != null && this.userview.getManagePrivCodeValue().length() > 2)
                                sb.append(" and  codeitemid = '" + this.userview.getManagePrivCodeValue() + "'");
                        }
                    }
                }
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /**
     * 判断 值类型是否与 要求的类型一致
     * 
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
    public boolean isDataType(int decwidth, String itemtype, String value) {
    						
        boolean flag = true;
        if ("N".equals(itemtype)) {
            if (decwidth == 0) {
                flag = value.matches("^[+-]?[\\d]+$");
            } else {
                flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
            }

        } else if ("D".equals(itemtype)) {
            value = value.replace(".", "-");
            value = value.replace("/", "-");
            if (value.matches("[0-9]{4}") && value.length() == 4) {// 2010
                flag = true;
                value = value + "-01-01";
            } else if (value.matches("[0-9]{4}[-]") && value.length() == 5) {// 2010-
                flag = true;
                value = value + "01-01";
            } else if (value.matches("[0-9]{4}[-][0-9]{1}") && value.length() == 6) {// 2010-5
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str + "-01";
            } else if (value.matches("[0-9]{4}[-][0-9]{2}") && value.length() == 7) {// 2010-05
                flag = true;
                value = value + "-01";
            } else if (value.matches("[0-9]{4}[-][0-9]{1}[-]") && value.length() == 7) {// 2010-5-
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str + "-01";
            } else if (value.matches("[0-9]{4}[-][0-9]{2}[-]") && value.length() == 8) {// 2010-05-
                flag = true;
                value = value + "01";
            } else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}") && value.length() == 8) {// 2010-5-5
                flag = true;
                String str1 = value.substring(5, 6);
                String str2 = value.substring(7, 8);
                value = value.substring(0, 5) + "0" + str1 + "-0" + str2;
            } else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}") && value.length() == 9) {// 2010-05-5
                flag = true;
                String str2 = value.substring(value.length() - 1, value.length());
                value = value.substring(0, value.length() - 1) + "0" + str2;
            } else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}") && value.length() == 9) {// 2010-5-15
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str;
            }

            else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}") && value.length() == 10) {
                flag = true;
            } else {
                flag = false;
            }
            if (value.matches("[0-9]{4}") && value.length() > 4) {
                String str = value.substring(0, 4);
                if (Integer.parseInt(str) < 1800)
                    flag = false;
            }

            String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
            Pattern p = Pattern.compile(eL);
            Matcher m = p.matcher(value);
            boolean b = m.matches();
            if (!b) {
                flag = false;
            }

        }
        return flag;
    }
    /**
     * 判断 值类型是否与 要求的日期类型一致 syl 20200114 add
     * 56620  V77新考勤/考勤申请：下载的excel模板，时间类型的含有时分的，导入系统的时候会提示格式不对，没有考虑时分的情况
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
    public boolean isDataTimeType(int decwidth, String itemtype, String value) {

        boolean flag = true;
        if ("N".equals(itemtype)) {
            if (decwidth == 0) {
                flag = value.matches("^[+-]?[\\d]+$");
            } else {
                flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
            }

        } else if ("D".equals(itemtype)) {
            value = value.replace(".", "-");
            value = value.replace("/", "-");
            if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 11) {// 2010-5-5 00
                flag = true;
                String str1 = value.substring(5, 6);
                String str2 = value.substring(7, 8);
                String str3 = value.substring(8, value.length());
                value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3+":00:00";
            }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 14) {// 2010-5-5 00:00
                flag = true;
                String str1 = value.substring(5, 6);
                String str2 = value.substring(7, 8);
                String str3 = value.substring(8, value.length());
                value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3+":00";
            }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 17) {// 2010-5-5 00:00:00
                flag = true;
                String str1 = value.substring(5, 6);
                String str2 = value.substring(7, 8);
                String str3 = value.substring(8, value.length());
                value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3;
            }
            
            else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 12) {// 2010-05-5 00
                flag = true;
                String str2 = value.substring(8, value.length());
                value = value.substring(0, 8) + "0" + str2+":00:00";
            }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 15) {// 2010-05-5 00:00
                flag = true;
                String str2 = value.substring(8, value.length());
                value = value.substring(0, 8) + "0" + str2+":00";
            }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 18) {// 2010-05-5 00:00:00
                flag = true;
                String str2 = value.substring(8, value.length());
                value = value.substring(0, 8) + "0" + str2;
            }
            
            else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 12) {// 2010-5-15 00
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str+":00:00";
            }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 15) {// 2010-5-15 00:00
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str+":00";
            }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 18) {// 2010-5-15 00:00:00
                flag = true;
                String str = value.substring(5, value.length());
                value = value.substring(0, 5) + "0" + str;
            }
            else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 13) {// 2010-05-15 00
                flag = true;
                value = value+":00:00";
            }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 16) {// 2010-05-15 00:00
                flag = true;
                value = value+":00";
            }
//            System.out.println("-->"+value);
            //2010-05-15 00:00:00
            if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}(\\s+([0-2][0-9]):[0-5][0-9]:[0-5][0-9])") && value.length() == 19) {// 2010-05-15 00:00:00
                flag = true;
            }
            else {
                flag = false;
            }
            if (value.matches("[0-9]{4}") && value.length() > 4) {
                String str = value.substring(0, 4);
                if (Integer.parseInt(str) < 1800)
                    flag = false;
            }
        }
//        System.out.println("++"+value);
        return flag;
    }
    /**
     * 返回日期类型
     * 
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
    public String returnDataTimeType(String value) {

        value = value.replace(".", "-");
        value = value.replace("/", "-");
        if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 11) {// 2010-5-5 00
            String str1 = value.substring(5, 6);
            String str2 = value.substring(7, 8);
            String str3 = value.substring(8, value.length());
            value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3+":00:00";
        }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 14) {// 2010-5-5 00:00
            String str1 = value.substring(5, 6);
            String str2 = value.substring(7, 8);
            String str3 = value.substring(8, value.length());
            value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3+":00";
        }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 17) {// 2010-5-5 00:00:00
            String str1 = value.substring(5, 6);
            String str2 = value.substring(7, 8);
            String str3 = value.substring(8, value.length());
            value = value.substring(0, 5) + "0" + str1 + "-0" + str2+str3;
        }
        
        else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 12) {// 2010-05-5 00
            String str2 = value.substring(8, value.length());
            value = value.substring(0, 8) + "0" + str2+":00:00";
        }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 15) {// 2010-05-5 00:00
            String str2 = value.substring(8, value.length());
            value = value.substring(0, 8) + "0" + str2+":00";
        }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 18) {// 2010-05-5 00:00:00
            String str2 = value.substring(8, value.length());
            value = value.substring(0, 8) + "0" + str2;
        }
        
        else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 12) {// 2010-5-15 00
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str+":00:00";
        }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 15) {// 2010-5-15 00:00
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str+":00";
        }else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])") && value.length() == 18) {// 2010-5-15 00:00:00
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str;
        }
        else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}(\\s+([0-1]?[4-9]|[0-2]?[0-3]))") && value.length() == 13) {// 2010-05-15 00
            value = value+":00:00";
        }else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{2}(\\s+([01]?[0-9]|2[0-3]):[0-5][0-9])") && value.length() == 16) {// 2010-05-15 00:00
            value = value+":00";
        }
//        System.out.println(value);
        return value;
    }
    /**
     * 返回日期类型
     * 
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
    public String returnDataType(String value) {

        value = value.replace(".", "-");
        value = value.replace("/", "-");
        if (value.matches("[0-9]{4}") && value.length() == 4) {// 2010
            value = value + "-01-01";
        } else if (value.matches("[0-9]{4}[-]") && value.length() == 5) {// 2010-
            value = value + "01-01";
        } else if (value.matches("[0-9]{4}[-][0-9]{1}") && value.length() == 6) {// 2010-5
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str + "-01";
        } else if (value.matches("[0-9]{4}[-][0-9]{2}") && value.length() == 7) {// 2010-05
            value = value + "-01";
        } else if (value.matches("[0-9]{4}[-][0-9]{1}[-]") && value.length() == 7) {// 2010-5-
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str + "01";
        } else if (value.matches("[0-9]{4}[-][0-9]{2}[-]") && value.length() == 8) {// 2010-05-
            value = value + "01";
        } else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{1}") && value.length() == 8) {// 2010-5-5
            String str1 = value.substring(5, 6);
            String str2 = value.substring(7, 8);
            value = value.substring(0, 5) + "0" + str1 + "-0" + str2;
        } else if (value.matches("[0-9]{4}[-][0-9]{2}[-][0-9]{1}") && value.length() == 9) {// 2010-05-5
            String str2 = value.substring(value.length() - 1, value.length());
            value = value.substring(0, value.length() - 1) + "0" + str2;
        } else if (value.matches("[0-9]{4}[-][0-9]{1}[-][0-9]{2}") && value.length() == 9) {// 2010-5-15
            String str = value.substring(5, value.length());
            value = value.substring(0, 5) + "0" + str;
        }

        return value;
    }
    public HSSFRichTextString cellStr(String context) {

        HSSFRichTextString textstr = new HSSFRichTextString(context);
        return textstr;
    }
    public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBottomBorderColor((short) 8);
        style.setLeftBorderColor((short) 8);
        style.setRightBorderColor((short) 8);
        style.setTopBorderColor((short) 8);
        return style;
    }
    public String decimalwidth(int len) {

        StringBuffer decimal = new StringBuffer("0");
        if (len > 0)
            decimal.append(".");
        for (int i = 0; i < len; i++) {
            decimal.append("0");
        }
        decimal.append("_ ");
        return decimal.toString();
    }
    public int getOperationtype() {
        return operationtype;
    }

    public void setOperationtype(int operationtype) {
        this.operationtype = operationtype;
    }

    public TemplateTableBo getBo() {
        return bo;
    }

    public void setBo(TemplateTableBo bo) {
        this.bo = bo;
    }

    public String getHasRecordFromMessage() {
        return hasRecordFromMessage;
    }

    public void setHasRecordFromMessage(String hasRecordFromMessage) {
        this.hasRecordFromMessage = hasRecordFromMessage;
    }

    public String getHmuster_sql() {
        return hmuster_sql;
    }

    public void setHmuster_sql(String hmuster_sql) {
        this.hmuster_sql = hmuster_sql;
    }
    public int getClass_type() {
        return class_type;
    }
    public void setClass_type(int class_type) {
        this.class_type = class_type;
    }

    /**
     * 获得节点定义的指标权限
     * 
     * @param task_id
     * @return
     */
    public HashMap getFieldPriv(String task_id, Connection conn) {
        HashMap _map = new HashMap();
        Document doc = null;
        Element element = null;
        try {
        	if(task_id!=null&&!"0".equals(task_id.trim()))
			{
	            ContentDAO dao = new ContentDAO(conn);
	            String sql = "select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id=" + task_id + " )";
	            RowSet rowSet = dao.search(sql);
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
     * 
     * @Title: FindChild
     * 
     * @Description: TODO
     * 
     * @param codeitemid
     *            代码编码或者组织机构编码
     * @param list
     *            代码排序后的list
     * @param codesetid
     *            代码类编码
     * @return ArrayList
     * 
     * @throws
     */
    public ArrayList FindChild(String codeitemid, ArrayList list, String codesetid, String rangecond) {
        String sql = "";
        RowSet rs = null;
        if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
            if (!"UN".equalsIgnoreCase(codesetid)) {
                if ("UM".equalsIgnoreCase(codesetid)) {
                    sql = "select codesetid,codeitemid,codeitemdesc,childid,grade grade from organization where codesetid in ('UN','UM') " + rangecond + " and parentid='" + codeitemid + "'and parentid <> codeitemid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ";
                } else {
                    sql = "select codesetid,codeitemid,codeitemdesc,childid,grade grade from organization where codesetid in ('UN','UM','@K') " + rangecond + " and parentid='" + codeitemid + "'and parentid <> codeitemid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ";
                }
            } else {
                sql = "select codesetid,codeitemid,codeitemdesc,childid,grade grade from organization where parentid='" + codeitemid + "' and codesetid='UN'" + rangecond + " and parentid <> codeitemid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid";
            }

        } else {
            sql = "select codesetid,codeitemid,codeitemdesc,childid,layer grade from codeitem where parentid='" + codeitemid + "' and codesetid='" + codesetid + "' and parentid <> codeitemid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid";
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                StringBuffer sb = new StringBuffer();
                // String setid =rs.getString("codesetid");
                String itemid = rs.getString("codeitemid");
                // String parentid=rs.getString("parentid");
                String childid = rs.getString("childid");
                String codeitemdesc = rs.getString("codeitemdesc");
                int grade = rs.getInt("grade");
                for (int i = 0; i < grade; i++) {
                    sb.append("　");
                }
                if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                    bean.set("value", sb.toString() + itemid + ":" + codeitemdesc);
                } else {
                    bean.set("value", sb.toString() + codeitemdesc);
                }
                list.add(bean);
                if (!itemid.equals(childid)) {
                    FindChild(itemid, list, codesetid, rangecond);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * @Title: getAllCellForXml
     * @Description: 为导出xml格式
     * @return ArrayList
     * @throws
     */
    public ArrayList getAllCellForXml() {

        ArrayList itemList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        String varString = "";
        try {
            LazyDynaBean abean = null;
            sql.append("select * from Template_set where tabid=");
            sql.append(this.tabid);
            HashMap itemidMap = new HashMap();
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                String filedFlag = rowSet.getString("flag");
                if (filedFlag == null || "".equals(filedFlag) || "null".equalsIgnoreCase(filedFlag) || "H".equalsIgnoreCase(filedFlag) || "S".equalsIgnoreCase(filedFlag) || "C".equalsIgnoreCase(filedFlag)) {
                    continue;
                }
                abean = new LazyDynaBean();
                String subflag = rowSet.getString("subflag");// 字段控制标识符 0：字段
                                                                // 1：子集
                String itemid = rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "";
                FieldItem item = DataDictionary.getFieldItem(itemid);

                if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && itemid != null && DataDictionary.getFieldItem(itemid.toLowerCase()) == null) {// 如果是单位部门或岗位
                    if ("codesetid".equalsIgnoreCase(itemid) || "codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid) || "parentid".equalsIgnoreCase(itemid) || "start_date".equalsIgnoreCase(itemid)) {
                        item = new FieldItem();
                        item.setItemid(itemid);
                        item.setItemdesc(rowSet.getString("hz"));
                        item.setFieldsetid(rowSet.getString("setname"));
                        item.setItemtype(rowSet.getString("Field_type"));
                        if ("codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid))
                            item.setCodesetid("0");
                        else if ("codesetid".equalsIgnoreCase(itemid))
                            item.setCodesetid("orgType");
                        else
                            item.setCodesetid(rowSet.getString("codeid"));

                        if (!"start_date".equalsIgnoreCase(itemid))
                            item.setItemlength(50);
                    }
                }
                if ("0".equals(subflag)) {// 如果是字段或者说是照片或者是附件
                    abean.set("subflag", subflag);
                    abean.set("flag", filedFlag);
                    if ("P".equalsIgnoreCase(filedFlag) || "F".equalsIgnoreCase(filedFlag)) {

                    } else if ("V".equalsIgnoreCase(filedFlag)) {// 如果是临时变量在最后统一处理
                        varString = varString + itemid + ",";
                    } else {
                        if (item == null) {
                            continue;
                        }
                        if (itemidMap.get(itemid.toUpperCase()) == null) {
                            itemidMap.put(itemid.toUpperCase(), "true");
                        } else if (itemidMap.get(itemid.toUpperCase()) != null) {
                            continue;
                        }
                        // System.out.println(rowSet.getString("field_name"));
                        abean.set("fieldsetid", rowSet.getString("SetName") != null ? rowSet.getString("SetName") : "");
                        abean.set("itemid", rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "");
                        if (abean.get("itemid") == null || "null".equals((String) abean.get("itemid")) || "".equals((String) abean.get("itemid"))) {
                            System.out.println("字段Error");
                        }
                        abean.set("itemdesc", item.getItemdesc());
                        abean.set("itemlength", "" + item.getItemlength());
                        abean.set("decimalwidth", "" + item.getDecimalwidth());
                        abean.set("itemtype", item.getItemtype());
                        abean.set("codesetid", rowSet.getString("codeid") != null ? rowSet.getString("codeid") : "");
                        itemList.add(abean);
                    }
                } else {// 子集
                    String sub_domain = rowSet.getString("sub_domain");
                    getSubTableCell(itemList, itemidMap, sub_domain);
                    continue;
                }
            }
            getAllVariableHmForXML(itemList, varString);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rowSet != null) {
                try {
                    rowSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return itemList;
    }
    /**
     * @param varString
     * @Title: getAllVariableHmForXML
     * @Description: TODO
     * @param itemList
     *            void
     * @throws
     */
    private void getAllVariableHmForXML(ArrayList itemList, String varString) {
        String tempvar = varString;
        if (tempvar.length() > 0) {
            tempvar = tempvar.substring(0, tempvar.length() - 1);
        } else {
            return;
        }
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            strsql.append("select * from MidVariable where nflag=0 and templetid= ");
            strsql.append(this.tabid);
            strsql.append(" and Cname in(");
            strsql.append(tempvar + ")");
            strsql.append(" order by sorting");
            RowSet rset = dao.search(strsql.toString());

            HashMap ntypeToType = new HashMap();
            ntypeToType.put("1", "N");
            ntypeToType.put("2", "A");
            ntypeToType.put("3", "D");
            ntypeToType.put("4", "A");

            while (rset.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("itemid", rset.getString("cname"));
                if (abean.get("itemid") == null || "null".equals((String) abean.get("itemid")) || "".equals((String) abean.get("itemid"))) {
                    System.out.println("临时变量Error");
                }
                abean.set("itemtype", (String) ntypeToType.get(rset.getString("ntype").trim()));
                abean.set("itemdesc", rset.getString("chz"));
                abean.set("codesetid", rset.getString("codesetid") != null ? rset.getString("codesetid") : "");
                abean.set("fieldsetid", "");
                abean.set("itemlength", rset.getString("Fldlen") != null ? rset.getString("Fldlen") : "");
                abean.set("decimalwidth", rset.getString("Flddec") != null ? rset.getString("Flddec") : "");
                itemList.add(abean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * @Title: getSubTableCell
     * @Description: 获得插入的子集中涉及到的字段
     * @param itemList:存放的是itembean对象
     * @param itemidMap：存放的是已经存在的itemid
     * @param subDomain:subDomain数据
     * @throws
     */
    private void getSubTableCell(ArrayList itemList, HashMap itemidMap, String subDomain) {
        String xpath = "/sub_para/para";
        Document doc = null;
        Element element = null;
        String fields = "";
        String[] fieldArray = null;
        LazyDynaBean bean = null;
        FieldItem item = null;
        try {
            doc = PubFunc.generateDom(subDomain);;
            XPath findPath = XPath.newInstance(xpath);
            List childlist = findPath.selectNodes(doc);
            if (childlist != null && childlist.size() > 0) {
                element = (Element) childlist.get(0);
                if (element.getAttributeValue("fields") != null) {
                    fields = (String) element.getAttributeValue("fields");
                }
            }
            if (fields.length() > 0) {
                fieldArray = fields.split("`");
                for (int i = 0; i < fieldArray.length; i++) {
                    String itemid = fieldArray[i];
                    if (itemidMap.get(itemid.toUpperCase()) != null) {
                        continue;
                    } else {
                        itemidMap.put(itemid.toUpperCase(), "1");
                        item = DataDictionary.getFieldItem(itemid);
                        if (item == null) {
                            continue;
                        }
                        bean = new LazyDynaBean();
                        bean.set("subflag", "1");
                        bean.set("flag", "sub");// 这个主要是为了好理解因为如果这个bean中是子集中的字段那么就没有必要去判断flag啦
                        bean.set("fieldsetid", item.getFieldsetid());
                        bean.set("itemid", itemid);
                        if (bean.get("itemid") == null || "null".equals((String) bean.get("itemid")) || "".equals((String) bean.get("itemid"))) {
                            System.out.println("子集字段Error");
                        }
                        bean.set("itemdesc", item.getItemdesc());
                        bean.set("itemlength", "" + item.getItemlength());
                        bean.set("decimalwidth", "" + item.getDecimalwidth());
                        bean.set("itemtype", item.getItemtype());
                        bean.set("codesetid", item.getCodesetid());
                        itemList.add(bean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * @Title: getColumns
     * @Description: TODO
     * @return String
     * @throws
     */
    public ArrayList getColAndType() {
        ArrayList itemList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer columns = new StringBuffer();
        StringBuffer types = new StringBuffer();
        RowSet rowSet = null;
        Document doc = null;
        Element element = null;
        String varString = "";
        HashMap contianMap = new HashMap();// 用于存放是否包含 照片和附件Key ：P||F
        try {
            sql.append("select * from Template_set where tabid=");
            sql.append(this.tabid);

            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                String filedFlag = rowSet.getString("flag");
                if (filedFlag == null || "".equals(filedFlag) || "null".equalsIgnoreCase(filedFlag) || "H".equalsIgnoreCase(filedFlag) || "S".equalsIgnoreCase(filedFlag) || "C".equalsIgnoreCase(filedFlag)) {
                    continue;
                }
                String subflag = rowSet.getString("subflag");// 字段控制标识符 0：字段
                // 1：子集
                String itemid = rowSet.getString("field_name") != null ? rowSet.getString("field_name") : "";// 字段名字
                int chgstate = rowSet.getInt("Chgstate");// 获得变化前后的标志
                String sub_domain = Sql_switcher.readMemo(rowSet, "sub_domain");
                FieldItem item = DataDictionary.getFieldItem(itemid);
                if (this.bo != null && (this.bo.getInfor_type() == 2 || this.bo.getInfor_type() == 3) && itemid != null && DataDictionary.getFieldItem(itemid.toLowerCase()) == null) {// 如果是单位部门或岗位
                    if ("codesetid".equalsIgnoreCase(itemid) || "codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid) || "parentid".equalsIgnoreCase(itemid) || "start_date".equalsIgnoreCase(itemid)) {
                        item = new FieldItem();
                        item.setItemid(itemid);
                        item.setItemdesc(rowSet.getString("hz"));
                        item.setFieldsetid(rowSet.getString("setname"));
                        item.setItemtype(rowSet.getString("Field_type"));
                        if ("codeitemdesc".equalsIgnoreCase(itemid) || "corcode".equalsIgnoreCase(itemid))
                            item.setCodesetid("0");
                        else if ("codesetid".equalsIgnoreCase(itemid))
                            item.setCodesetid("orgType");
                        else
                            item.setCodesetid(rowSet.getString("codeid"));

                        if (!"start_date".equalsIgnoreCase(itemid))
                            item.setItemlength(50);
                    }
                }
                String sub_domain_id = "";
                String xpath = "/sub_para/para";
                if (sub_domain != null && sub_domain.trim().length() > 0 && chgstate == 1) {
                    doc = PubFunc.generateDom(sub_domain);
                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    // xpath="/sub_para/para";
                    List childlist = findPath.selectNodes(doc);
                    if (childlist != null && childlist.size() > 0) {
                        element = (Element) childlist.get(0);
                        if (element.getAttributeValue("id") != null) {
                            sub_domain_id = (String) element.getAttributeValue("id");
                        }
                    }
                }

                if ("0".equals(subflag)) {// 如果是字段或者说是照片或者是附件
                    if ("P".equalsIgnoreCase(filedFlag)) {
                        contianMap.put("P", "true");
                    } else if ("F".equalsIgnoreCase(filedFlag)) {
                        contianMap.put("F", "true");
                    } else if ("V".equalsIgnoreCase(filedFlag)) {// 如果是临时变量在最后统一处理
                        varString = varString + itemid + ",";
                    } else {
                        if (item == null) {
                            continue;
                        }
                        columns.append(item.getItemid() + "_");
                        if (sub_domain_id.length() > 0) {
                            columns.append(sub_domain_id + "_");
                        }
                        columns.append(chgstate + "`");
                        String Field_type = item.getItemtype();
                        if ("0".equals(subflag) && chgstate == 1) {// 变化前的字段
                            if (Sql_switcher.searchDbServer() == 2) {
                                if (("2".equals("" + rowSet.getInt("HisMode"))) || "3".equals("" + rowSet.getInt("HisMode")) || "4".equals("" + rowSet.getInt("HisMode"))) {// (序号定位&&(最近||最初))
                                                                                                                                                                            // ||
                                                                                                                                                                            // 条件定位||条件序号
                                    Field_type = "M";
                                }
                            } else {
                                if ("2".equals("" + rowSet.getInt("HisMode")) || "3".equals("" + rowSet.getInt("HisMode")) || "4".equals("" + rowSet.getInt("HisMode"))) { // 2014-04-01
                                                                                                                                                                            // dengcan
                                    Field_type = "M";
                                }
                            }
                        }
                        String xmlTypelength = "";
                        int itemLength = item.getItemlength();
                        int itemdecLength = item.getDecimalwidth();
                        xmlTypelength = String.valueOf(itemLength);
                        if ("N".equalsIgnoreCase(Field_type)) {
                            if (itemdecLength != 0) {
                                xmlTypelength = itemLength + "." + itemdecLength;
                            }
                        }
                        if ("M".equalsIgnoreCase(Field_type) || "D".equalsIgnoreCase(Field_type)) {
                            xmlTypelength = "";
                        }
                        types.append(Field_type + xmlTypelength + "`");
                    }
                } else {// 子集
                    String columnName = "T_" + rowSet.getString("setname").toUpperCase() + "_" + chgstate;
                    columns.append(columnName + "`");
                    types.append("M" + "`");
                    continue;
                }
            }
            getAllVariableHmForXMLCT(columns, types, varString);
            itemList.add(columns.toString());
            itemList.add(types.toString());
            itemList.add(contianMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rowSet != null) {
                try {
                    rowSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return itemList;
    }
    /**
     * @param varString
     * @Title: getAllVariableHmForXMLCT
     * @Description: 得到临时变量的字段类型长度 等等
     * @param columns：列名
     * @param types
     *            字段类型
     * @throws
     */
    private void getAllVariableHmForXMLCT(StringBuffer columns, StringBuffer types, String varString) {

        String tempvar = varString;
        if (tempvar.length() > 0) {
            tempvar = tempvar.substring(0, tempvar.length() - 1);
        } else {
            return;
        }
        StringBuffer strsql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rset = null;
        try {
            strsql.append("select * from MidVariable where nflag=0 and templetid= ");
            strsql.append(this.tabid);
            strsql.append(" and Cname in(");
            strsql.append(tempvar + ")");
            strsql.append(" order by sorting");
            rset = dao.search(strsql.toString());

            HashMap ntypeToType = new HashMap();
            ntypeToType.put("1", "N");
            ntypeToType.put("2", "A");
            ntypeToType.put("3", "D");
            ntypeToType.put("4", "A");
            String xmlTypelength = "";
            while (rset.next()) {
                String columnname = rset.getString("cname");
                String itemtype = (String) ntypeToType.get(rset.getString("ntype").trim());
                String itemlength = rset.getString("Fldlen") != null ? rset.getString("Fldlen") : "";
                String decimalwidth = rset.getString("Flddec") != null ? rset.getString("Flddec") : "";
                columns.append(columnname + "`");

                if ("N".equalsIgnoreCase(itemtype)) {
                    if (!"".equals(decimalwidth)) {
                        xmlTypelength = itemlength + "." + decimalwidth;
                    }
                }
                if ("M".equalsIgnoreCase(itemtype) || "D".equalsIgnoreCase(itemtype)) {
                    xmlTypelength = "";
                }
                types.append(itemtype + xmlTypelength + "`");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @param types
     * 
     * @Title: getXmlRow
     * @Description:用于生成导出xml模版数据时每一行
     * @param dao
     * @param contianMap//是否包含
     *            照片和附件
     * @param columns
     *            //要查询的字段
     * @param tasklist//任务号
     * @param tablebo
     * @param uniqueitem唯一性指标编号
     * @throws
     */
    public ArrayList getXmlRow(HashMap contianMap, String columns, String strsql, TemplateTableBo tablebo, ContentDAO dao, String uniqueitem, String types) {
        String tabid = String.valueOf(tablebo.getTabid());
        String uniqueid = uniqueitem;
        String[] columnsArray = columns.split("`");
        String[] typesArray = types.split("`");
        boolean findAignUniqueid = true;// 用于确定唯一性指标是否需要从人员库中再查一次
        boolean findAignObjectname = true;// 用于确定姓名是否需要从人员库中在取一次
        String operationType = String.valueOf(tablebo.getOperationtype());// 得到操作类型
                                                                            // 0和5代表的是调入型
        String infortype = String.valueOf(tablebo.getInfor_type());
        String photoFind = (String) contianMap.get("P");// 当前模版下是否包含照片
        String fileFind = (String) contianMap.get("F");// 当前模版下是否包含附件
        LazyDynaBean bean = null;
        ArrayList rowList = new ArrayList();
        RowSet rs = null;
        if ("0".equalsIgnoreCase(operationType) || "5".equalsIgnoreCase(operationType)) {
            if (columns.toUpperCase().indexOf(uniqueitem.toUpperCase() + "_2") == -1) {// 当前模版中不包含唯一性指标
                findAignUniqueid = false;
            }
            if (columns.toUpperCase().indexOf("A0101_2") == -1) {// 当前模版中不包含姓名
                findAignObjectname = false;
            }
        } else {
            if (columns.toUpperCase().indexOf(uniqueitem.toUpperCase() + "_1") == -1) {// 当前模版中不包含唯一性指标
                findAignUniqueid = false;
            }
            if (columns.toUpperCase().indexOf("A0101_1") == -1) {
                findAignObjectname = false;
            }
        }
        try {

            rs = dao.search(strsql);
            while (rs.next()) {
                bean = new LazyDynaBean();
                if ("1".equals(infortype)) {
                    String basepre = rs.getString("basepre");// 人员库
                    String objectid = rs.getString("A0100");// 人员编号
                    bean.set("basepre", basepre);
                    bean.set("objectid", objectid.toUpperCase());
                } else if ("2".equals(infortype)) {
                    String objectid = rs.getString("B0100");// 机构编码
                    bean.set("basepre", "");
                    bean.set("objectid", objectid.toUpperCase());
                } else if ("3".equals(infortype)) {
                    String objectid = rs.getString("E01A1");// 机构编码
                    bean.set("basepre", "");
                    bean.set("objectid", objectid.toUpperCase());
                }
                if (findAignUniqueid) {// 如果惟一性指标不需要查寻
                    String value = "";
                    String col = "";
                    if ("0".equalsIgnoreCase(operationType) || "5".equalsIgnoreCase(operationType)) {
                        col = uniqueitem + "_2";
                    } else {
                        col = uniqueitem + "_1";
                    }
                    FieldItem item = DataDictionary.getFieldItem(uniqueitem);
                    String itemtype = item.getItemtype();
                    int declength = item.getDecimalwidth();
                    if ("A".equalsIgnoreCase(itemtype)) {
                        value = rs.getString(col);
                    } else if ("N".equalsIgnoreCase(itemtype)) {
                        if (declength > 0) {
                            value = String.valueOf(rs.getFloat(col));
                        } else {
                            value = String.valueOf(rs.getInt(col));
                        }
                    } else if ("D".equalsIgnoreCase(itemtype)) {
                        Date date = rs.getDate(col);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        String zone_offset = String.valueOf((calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000)));
                        bean.set(uniqueid, format.format(date) + "+" + zone_offset + ":00");
                    } else if ("M".equalsIgnoreCase(itemtype)) {
                        value = Sql_switcher.readMemo(rs, col);
                    }
                    bean.set("uniqueid", value);
                } else {
                    if ("0".equalsIgnoreCase(operationType) || "5".equalsIgnoreCase(operationType)) {
                        bean.set("uniqueid", "");
                    } else {
                        bean.set("uniqueid", "checkagin");// 从数据库中再次查询
                    }

                }
                if (findAignObjectname) {// 如果姓名不需要查询
                    String value = "";
                    if ("0".equalsIgnoreCase(operationType) || "5".equalsIgnoreCase(operationType)) {
                        value = rs.getString("A0101_2");
                    } else {
                        value = rs.getString("A0101_1");
                    }
                    bean.set("objectname", value);
                } else {
                    if ("0".equalsIgnoreCase(operationType) || "5".equalsIgnoreCase(operationType)) {
                        bean.set("objectname", "");
                    } else {
                        bean.set("objectname", "checkagin");
                    }

                }
                if (photoFind != null) {
                    String ext = rs.getString("ext");
                    if (ext != null && ext.trim().length() > 0 && !"null".equalsIgnoreCase(ext)) {
                        FileOutputStream fout = null;
                        InputStream in = null;
                        File tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"), new File(System.getProperty("java.io.tmpdir")));

                        try {
                            in = rs.getBinaryStream("photo");
                            fout = new FileOutputStream(tempFile);
                            int len;
                            byte[] buf = new byte[1024];
                            while ((len = in.read(buf, 0, 1024)) != -1) {
                                fout.write(buf, 0, len);
                            }
                            fout.close();
                            String filename = tempFile.getName();
                            bean.set("filename", filename);
                            bean.set("ext", ext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            PubFunc.closeIoResource(fout); // 关闭资源
                            PubFunc.closeIoResource(in);
                        }

                    }
                }
                if (fileFind != null) {
                    bean.set("fileFind", taskid + "`" + tabid);
                }
                for (int j = 0; j < columnsArray.length; j++) {
                    String type = typesArray[j];// 得到字段的类型
                    if (type.startsWith("A")) {
                        String value = rs.getString(columnsArray[j]) == null ? "" : rs.getString(columnsArray[j]);
                        int index = columnsArray[j].indexOf("_");
                        String tempid = "";
                        if (index != -1) {
                            tempid = columnsArray[j].substring(0, index);
                        }
                        FieldItem item = DataDictionary.getFieldItem(tempid);
                        if (item != null) {
                            String codesetid = item.getCodesetid();
                            if (codesetid != null && !"".equalsIgnoreCase(codesetid) && !"0".equals(codesetid)) {
                                String desc = AdminCode.getCodeName(codesetid, value);
                                if (desc == null || "".equals(desc)) {
                                    value = "";
                                }
                            }
                        }
                        bean.set(columnsArray[j], value);
                    }
                    if (type.startsWith("N")) {
                        if (type.indexOf(".") == -1) {
                            bean.set(columnsArray[j], String.valueOf(rs.getInt(columnsArray[j])) == null ? "" : String.valueOf(rs.getInt(columnsArray[j])));
                        } else {
                            bean.set(columnsArray[j], String.valueOf(rs.getFloat(columnsArray[j])) == null ? "" : String.valueOf(rs.getFloat(columnsArray[j])));
                        }
                    }
                    if (type.startsWith("D")) {
                        String value = "";
                        Date date = rs.getDate(columnsArray[j]);
                        if (date != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            String zone_offset = String.valueOf((calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000)));
                            value = format.format(date) + "+" + zone_offset + ":00";
                        }

                        bean.set(columnsArray[j], value);
                    }
                    if (type.startsWith("M")) {
                        if (columnsArray[j].startsWith("T_")) {// 子集数据
                            String tablevalue = Sql_switcher.readMemo(rs, columnsArray[j]);
                            Document doc = PubFunc.generateDom(tablevalue);
                            Element rootElement = doc.getRootElement();
                            Element valueElemt = (Element) rootElement.clone();
                            bean.set(columnsArray[j], valueElemt);// 不可以直接塞rootElement
                        } else {
                            String value = Sql_switcher.readMemo(rs, columnsArray[j]);
                            bean.set(columnsArray[j], value == null ? "" : value);
                        }
                    }
                }
                String A0000 = rs.getString("A0000");
                if (A0000 == null || "null".equalsIgnoreCase(A0000)) {
                    A0000 = "";
                }
                bean.set("A0000", A0000);
                rowList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return rowList;
    }
    /**
     * @param contianMap
     * @param columns
     * @param dao
     * @Title: creatRowElement
     * @Description: 创建row数据结点
     * @param rowList包含row节点所用到的数据
     * @param rows
     *            row结点的上一级结点
     * @param uniqueitem
     *            唯一性指标
     * @throws
     */
    public void creatRowElement(ArrayList rowList, Element rows, String uniqueitem, ContentDAO dao, String columns) {
        RowSet rs = null;
        String[] columsArray = columns.split("`");
        FileOutputStream fout = null;
        InputStream in = null;
        try {
            for (int i = 0; i < rowList.size(); i++) {
                Element row = new Element("row");
                LazyDynaBean bean = (LazyDynaBean) rowList.get(i);
                String basepre = (String) bean.get("basepre");
                String objectid = (String) bean.get("objectid");
                String objectname = (String) bean.get("objectname");
                String uniqueid = (String) bean.get("uniqueid");
                String A0000 = (String) bean.get("A0000");
                if ("checkagin".equalsIgnoreCase(objectname)) {
                    rs = dao.search("select A0101 from " + basepre + "A01 where A0100=" + objectid);
                    if (rs.next()) {
                        objectname = rs.getString(1);
                    }
                }
                if ("checkagin".equalsIgnoreCase(uniqueid)) {
                    FieldItem item = DataDictionary.getFieldItem(uniqueitem);
                    String itemType = item.getItemtype();
                    int itemdecmalength = item.getDecimalwidth();// 小数点后的位数
                    String setid = item.getFieldsetid();
                    rs = dao.search("select " + uniqueitem + " from " + basepre + setid.toUpperCase() + " where A0100=" + objectid);
                    if (rs.next()) {
                        if ("N".equalsIgnoreCase(itemType)) {
                            if (itemdecmalength > 0) {
                                float value = rs.getFloat(1);
                                uniqueid = String.valueOf(value);
                            } else {
                                int value = rs.getInt(1);
                                uniqueid = String.valueOf(value);
                            }
                        } else if ("A".equalsIgnoreCase(itemType)) {
                            String value = rs.getString(1);
                            uniqueid = value;
                        } else if ("D".equalsIgnoreCase(itemType)) {
                            Date date = rs.getDate(1);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            String zone_offset = String.valueOf((calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000)));
                            String value = format.format(date) + "+" + zone_offset + ":00";
                            uniqueid = value;
                        } else {
                            String value = Sql_switcher.readMemo(rs, uniqueitem);
                            uniqueid = value;
                        }
                    }
                }
                if (uniqueid == null) {
                    uniqueid = "";
                }
                row.setAttribute("basepre", basepre.toUpperCase());
                row.setAttribute("objectid", objectid);
                row.setAttribute("objectname", objectname);
                row.setAttribute("uniqueid", uniqueid);
                row.setAttribute("A0000", (String) bean.get("A0000"));
                for (int j = 0; j < columsArray.length; j++) {
                    Element containElement = new Element(columsArray[j]);
                    if (columsArray[j].startsWith("T_")) {
                        Element tableElement = (Element) bean.get(columsArray[j]);
                        containElement.addContent(tableElement);
                    } else {
                        String textValue = bean.get(columsArray[j]) == null ? "" : (String) bean.get(columsArray[j]);
                        containElement.setText(textValue);
                    }
                    row.addContent(containElement);
                }
                String ext = bean.get("ext") == null ? "" : (String) bean.get("ext");
                String fileFind = bean.get("fileFind") == null ? "" : (String) bean.get("fileFind");
                if (!"".equals(ext) || !"".equals(fileFind)) {
                    Element filesElement = new Element("files");
                    if (!"".equals(ext)) {
                        Element fileElement = new Element("file");
                        fileElement.setAttribute("flag", "p");
                        fileElement.setAttribute("name", "photo");
                        fileElement.setAttribute("ext", ext);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        String zone_offset = String.valueOf((calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000)));
                        String value = format.format(date) + "+" + zone_offset + ":00";
                        fileElement.setAttribute("create_time", value);
                        fileElement.setAttribute("create_user", this.userview.getUserName());
                        fileElement.setAttribute("filename", (String) bean.get("filename"));
                        filesElement.addContent(fileElement);
                    } else {
                        String[] array = fileFind.split("`");
                        String task_id = array[0];
                        String tabid = array[1];
                        String ins_id = "";
                        String sql = "select file_id,name,filetype,content,ext,create_time,create_user where tabid=" + tabid + " and ins_id=(select ins_id from t_wf_task where task_id=" + task_id + ") and attachmenttype=1";
                        rs = dao.search(sql);

                        while (rs.next()) {
                            Element fileElement = new Element("file");
                            String fileEXT = rs.getString("ext");
                            String create_user = rs.getString("create_user");
                            int filetype = rs.getInt("filetype");
                            String name = rs.getString("name");
                            Date date = rs.getDate("create_time");
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String create_time = format.format(date);

                            File tempFile = File.createTempFile(String.valueOf(rs.getInt("file_id")), rs.getString("ext"), new File(System.getProperty("java.io.tmpdir")));
                            in = rs.getBinaryStream("content");
                            fout = new FileOutputStream(tempFile);
                            int len;
                            byte[] buf = new byte[1024];
                            while ((len = in.read(buf, 0, 1024)) != -1) {
                                fout.write(buf, 0, len);
                            }
                            String filename = tempFile.getName();
                            fileElement.setAttribute("flag", "" + filetype);
                            fileElement.setAttribute("name", name);
                            fileElement.setAttribute("ext", "." + fileEXT);
                            fileElement.setAttribute("create_time", create_time);
                            fileElement.setAttribute("create_user", create_user);
                            fileElement.setAttribute("filename", filename);
                            filesElement.addContent(fileElement);
                        }
                    }
                    row.addContent(filesElement);
                }
                rows.addContent(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(fout);// 资源释放 jingq 2014.12.29
            PubFunc.closeResource(in);
        }

    }
    /**
     * @param inforType
     * @param tableName
     * @param tabid
     *            模版号
     * @param operationtype
     *            操作类型 调入||非调入
     * @param uniqueitem
     *            系统惟一性指标
     * @Title: reserverXml
     * @Description: 解析导入的xml文件
     * @param doc
     *            导入的doc
     * @throws
     */
    public ArrayList reserverXml(Document doc, String uniqueitem, String operationtype, String tabid, String tableName, String inforType) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ResultSetMetaData rsmd = null;
        RowSet rs = null;

        ArrayList objectList = new ArrayList();// 存放所有导入人员的信息
        try {
            String xpath = "";
            String codesetid = "";// 关联的代码类 字符型有效
            String fieldsetid = "";// 指标所属的子集
            String itemid = "";// 指标编号
            String itemdesc = "";// 指标描述
            String itemtype = "";// 指标类型
            int decimalwidth = 0;// 指标的小数位数
            int itemlength = 0;// 指标的整数位数
            Element element = null;
            StringBuffer notImportItem = new StringBuffer();
            HashMap colunmNameMap = new HashMap();// 存放的接收模版的数据库中的字段
            HashMap notImportItemMap = new HashMap();// 存放不能导入的指标
            if (doc == null) {
            } else {
                /** 开始分析fielditems结点的属性数据* */
                xpath = "/templatepackage/fielditems";// 得到所有的指标值段
                XPath findPath = XPath.newInstance(xpath);
                List childlist = findPath.selectNodes(doc);
                if (childlist != null && childlist.size() > 0) {// 去判断那些指标可以导入
                    element = (Element) childlist.get(0);// 得到fielditems节点
                    String import_uniqueitem = element.getAttributeValue("uniqueitem");
                    if (import_uniqueitem == null || import_uniqueitem.trim().length() < 1) {
                        throw new GeneralException("导入的数据中未定义唯一性指标!");
                    } else if (import_uniqueitem.equalsIgnoreCase(uniqueitem)) {
                        throw new GeneralException("导入的数据中唯一性指标和当前库中的唯一性指标不一致!");
                    } else {
                        childlist = element.getChildren();// 得到所有的fielditem
                        /** 开始分析fielditem结点的数据* */
                        if (childlist != null && childlist.size() > 0) {// 对所有的fielditem进行分析看看那些指标是不可以导入的
                            for (int i = 0; i < childlist.size(); i++) {
                                element = (Element) childlist.get(i);
                                itemid = element.getAttributeValue("itemid");
                                itemdesc = element.getAttributeValue("itemdesc");
                                FieldItem newItem = DataDictionary.getFieldItem(itemid);
                                if (newItem == null) {// 指标不存在无法导入
                                    notImportItem.append(itemdesc + ",");
                                    notImportItemMap.put(itemid, "1");
                                    continue;
                                } else {
                                    String newFieldSetid = newItem.getFieldsetid();
                                    fieldsetid = element.getAttributeValue("fieldsetid");
                                    if (!newFieldSetid.equalsIgnoreCase(fieldsetid)) {// 所属子集不一致无法导入
                                        notImportItem.append(itemdesc + ",");
                                        notImportItemMap.put(itemid, "1");
                                        continue;
                                    }
                                    String newItemType = newItem.getItemtype();
                                    itemtype = element.getAttributeValue("itemtype");
                                    if (itemtype == null || !itemtype.equalsIgnoreCase(newItemType)) {// 类型不一致无法导入
                                        notImportItem.append(itemdesc + ",");
                                        notImportItemMap.put(itemid, "1");
                                        continue;
                                    }
                                    int newItemLength = newItem.getItemlength();
                                    itemlength = Integer.parseInt(element.getAttributeValue("itemlength"));
                                    if (newItemLength < itemlength) {// 现位数比原位数短无法导入
                                        notImportItem.append(itemdesc + ",");
                                        notImportItemMap.put(itemid, "1");
                                        continue;
                                    }
                                    int newItemdecimalwidth = newItem.getDecimalwidth();
                                    decimalwidth = Integer.parseInt(element.getAttributeValue("decimalwidth"));
                                    if (newItemdecimalwidth < decimalwidth) {// 小数点位数小于导入的数据，无法导入
                                        notImportItem.append(itemdesc + ",");
                                        notImportItemMap.put(itemid, "1");
                                        continue;
                                    }
                                    if ("A".equalsIgnoreCase(newItemType)) {
                                        String newCodesetid = newItem.getCodesetid();
                                        codesetid = element.getAttributeValue("codesetid");
                                        if (!newCodesetid.equalsIgnoreCase(codesetid)) {// 如果关联代码类不相同，也不可以导入
                                            notImportItem.append(itemdesc + ",");
                                            notImportItemMap.put(itemid, "1");
                                            continue;
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                // 生成导入数据的sql 考虑使用vo的方式是不是更好的可以解决存储的问题

                String sql = "select null from " + tableName;
                rs = dao.search(sql);
                rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String colunmName = rsmd.getColumnName(i).toLowerCase();
                    colunmNameMap.put(colunmName, colunmName);
                }
                /** 开始分析rows结点的数据* */
                HashMap canImportcol = new HashMap();
                xpath = "/templatepackage/rows";// 得到rows
                findPath = XPath.newInstance(xpath);
                childlist = findPath.selectNodes(doc);
                if (childlist != null && childlist.size() > 0) {
                    element = (Element) childlist.get(0);// rows结点
                    String columns = element.getAttributeValue("columns");
                    String types = element.getAttributeValue("types");
                    if (columns != null && columns.trim().length() > 0) {
                        String[] columnsArray = columns.split("`");
                        String[] typesArray = types.split("`");
                        String inItemid = "";
                        // if(this.Infor_type==1){//人员
                        // canImportcol.put("basepre", "A");
                        // canImportcol.put("a0100", "A");
                        // canImportcol.put("a0000", "A");
                        // canImportcol.put("a0101_1","A");
                        // }
                        // if(this.Infor_type==2){//单位部门（考虑在内暂时不实现）可能考虑不全
                        // canImportcol.put("b0100", "A");
                        // canImportcol.put("a0000", "A");
                        // canImportcol.put("codeitemdesc_1","A");
                        // }
                        // if(this.Infor_type==3){
                        // canImportcol.put("e01a1", "A");
                        // canImportcol.put("a0000", "A");
                        // canImportcol.put("codeitemdesc_1","A");
                        // }
                        for (int i = 0; i < columnsArray.length; i++) {
                            String colName = columnsArray[i];
                            if (colName.startsWith("T_")) {// 表明这一列是子集，
                                if (colunmNameMap.get(colName) != null) {// 接收库中含有这一列能导入
                                    canImportcol.put(colName, typesArray[i]);
                                }
                                continue;
                            }
                            if (colName.indexOf("_") != -1) {// 不是临时变量
                                inItemid = colName.substring(0, colName.indexOf("_"));
                            } else {
                                inItemid = colName;
                            }
                            if (notImportItemMap.get(inItemid) != null) {// 如果指标不能导入那么移除
                                continue;
                            } else {
                                if (colunmNameMap.get(colName) != null) {// 接收库中含有这一列能导入
                                    canImportcol.put(colName, typesArray[i]);
                                }
                                continue;
                            }
                        }
                    }
                    /** 开始分析rows的孩子结点即row结点的数据* */
                    childlist = element.getChildren();
                    for (int i = 0; i < childlist.size(); i++) {
                        HashMap vbMap = new HashMap();// 存放 vo和附件相关属性的beanList
                        element = (Element) childlist.get(i);
                        String basepre = element.getAttributeValue("basepre");// 对于组织机构这个指标不存在
                        String objectid = element.getAttributeValue("objectid");// 对于人员：a0100
                                                                                // 对于单位：b0100
                                                                                // 对于部门：e01a1
                        String objectname = element.getAttributeValue("objectname");// 对于人员:a0101_1
                                                                                    // 调入型a0101_2
                                                                                    // （对于组织机构codeitemdesc_1
                                                                                    // 调入型
                                                                                    // codeitemdesc_2未确认)
                        String uniqueid = element.getAttributeValue("uniqueid");// 系统的唯一性标识
                        String A0000 = element.getAttributeValue("A0000");
                        RecordVo vo = new RecordVo(tableName);
                        if ("0".equals(operationtype) || "5".equals(operationtype)) {// 调入型
                            if ("1".equals(inforType)) {
                                vo.setString("basepre", basepre);
                                vo.setString("a0100", objectid);
                                vo.setString("a0101_1", objectname);
                                vo.setString("a0000", A0000);
                            } else if ("2".equals(inforType)) {
                                vo.setString("b0100", objectid);
                                vo.setString("codeitemdesc_1", objectname);
                                vo.setString("a0000", A0000);
                            } else {
                                vo.setString("e01a1", objectid);
                                vo.setString("codeitemdesc_1", objectname);
                                vo.setString("a0000", A0000);
                            }
                        }
                        Iterator it = canImportcol.keySet().iterator();
                        while (it.hasNext()) {
                            String colName = (String) it.next();// 得到列名
                            String datatype = (String) canImportcol.get(colName);
                            if (datatype.startsWith("A")) {
                                vo.setString(colName, element.getText() == null ? "" : element.getText());
                            }
                            if (datatype.startsWith("N")) {
                                if (datatype.indexOf(".") != -1) {// 是有小数位
                                    vo.setDouble(colName, Double.parseDouble(element.getText() == null ? "0" : element.getText()));
                                } else {
                                    vo.setInt(colName, Integer.parseInt(element.getText() == null ? "0" : element.getText()));
                                }
                            }
                            if (datatype.startsWith("D")) {
                                String value = element.getText() == null ? "" : element.getText();
                                // 1991-01-07T00:00:00+8:00 格式是这样的
                                int indexPlus = value.indexOf("+");
                                int indexT = value.indexOf("T");
                                if (value.trim().length() > 0) {
                                    value = value.substring(0, indexT) + value.substring(indexT + 1, indexPlus);
                                }
                                vo.setDate(colName, value);
                            }
                            if (datatype.startsWith("M")) {
                                if (colName.startsWith("T_")) {// 子集需要特殊处理
                                    Element records = element.getChild("records");
                                    /** 输出超文标志 */
                                    XMLOutputter outputter = new XMLOutputter();
                                    Format format = Format.getPrettyFormat();
                                    format.setExpandEmptyElements(true);// must
                                    format.setEncoding("UTF-8");
                                    outputter.setFormat(format);
                                    String value = outputter.outputString(records);
                                    vo.setString(colName, value);
                                } else {
                                    String value = element.getText() == null ? "" : element.getText();
                                    vo.setString(colName, value);
                                }

                            }
                        }
                        /** 分析附件和照片* */
                        Element files = element.getChild("files");
                        if (files != null) {
                            List fileList = files.getChildren();// 得到所有的file结点
                            if (fileList != null && fileList.size() > 0) {
                                ArrayList beanList = new ArrayList();
                                for (int j = 0; j < fileList.size(); j++) {
                                    Element file = (Element) fileList.get(j);
                                    String flag = file.getAttributeValue("flag");
                                    String name = file.getAttributeValue("name");
                                    String ext = file.getAttributeValue("ext");
                                    String create_user = file.getAttributeValue("create_user");
                                    String create_time = file.getAttributeValue("create_time");
                                    String filename = file.getAttributeValue("filename");
                                    LazyDynaBean abean = new LazyDynaBean();
                                    abean.set("flag", flag);
                                    abean.set("name", name);
                                    abean.set("ext", ext);
                                    abean.set("create_user", create_user);
                                    abean.set("create_time", create_time);
                                    abean.set("filename", filename);
                                    beanList.add(abean);
                                }
                                vbMap.put("beanList", beanList);
                            }
                        }
                        vbMap.put("vo", vo);
                        vbMap.put("uniqueid", uniqueid);// 将唯一性标识放到map中去
                        objectList.add(vbMap);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return objectList;

    }
    /**
     * @param inforType
     * @param dao
     * @param zipFile
     *            上传的zipFile文件
     * @param zs
     *            ZipInputStream 字节流
     * @param fileHeaderList
     * @param objectList
     *            存储vo以及附件bean的list
     * @Title: setXmlDate
     * @Description: 解析obejectList 生成临时表用的vo,将附件的相关数据导入到附件表中
     * @throws
     */
    public void setXmlDate(ArrayList objectList, List fileHeaderList, ZipInputStream zs, ZipFile zipFile, ContentDAO dao, String inforType) {
        InputStream is = null;
        try {
            for (int i = 0; i < objectList.size(); i++) {

                HashMap vbMap = (HashMap) objectList.get(i);
                RecordVo vo = (RecordVo) vbMap.get("vo");
                ArrayList beanList = (ArrayList) vbMap.get("beanList");

                if (beanList != null && beanList.size() > 0) {// 如果有照片或者附件

                    for (int j = 0; j < beanList.size(); j++) {

                        LazyDynaBean bean = (LazyDynaBean) beanList.get(i);
                        String flag = (String) bean.get("flag");
                        if (flag != null) {

                            String ext = (String) bean.get("ext");
                            String filename = (String) bean.get("filename");

                            for (int n = 0; n < fileHeaderList.size(); n++) {

                                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                                String fileName = "";
                                String extention = "";

                                if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
                                    int m = fileHeader.getFileName().lastIndexOf(".");
                                    int k = fileHeader.getFileName().lastIndexOf("/");
                                    if (m > -1 && m < fileHeader.getFileName().length() && !fileHeader.isDirectory()) {
                                        fileName = fileHeader.getFileName().substring(k + 1, m); // --文件名
                                        extention = fileHeader.getFileName().substring(m + 1); // --扩展名
                                    }
                                }
                                if (extention.equalsIgnoreCase(ext) && fileName.equalsIgnoreCase(filename) && "p".equalsIgnoreCase(flag)) {// 照片是需要直接导入到临时表里面去的
                                    zs = zipFile.getInputStream(fileHeader);
                                    is = zs;
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    byte[] tempbytes = new byte[1024];
                                    int byteread = 0;
                                    while ((byteread = is.read(tempbytes)) != -1) {
                                        out.write(tempbytes, 0, byteread);// 将数据写入到os输出流中
                                    }
                                    vo.setObject("photo", out.toByteArray());
                                    vo.setString("ext", ext);
                                    break;
                                } else if (extention.equalsIgnoreCase(ext) && fileName.equalsIgnoreCase(filename)) {// 如果不是照片那就是附件里面的了,附件是无论如何都是要导入到t_wf_file中去的
                                    String Importname = (String) bean.get("name");// 导入到库中的名字
                                    String create_time = (String) bean.get("create_time");
                                    if (create_time.trim().length() > 0) {
                                        int indexPlus = create_time.indexOf("+");
                                        int indexT = create_time.indexOf("T");
                                        create_time = create_time.substring(0, indexT) + create_time.substring(indexT + 1, indexPlus);
                                    }
                                    String create_user = (String) bean.get("create_user");
                                    String objectid = "";
                                    String basepre = "";
                                    int ins_id = 0;
                                    int tabid = Integer.parseInt(this.tabid);
                                    int attachmenttype = 1;
                                    RecordVo filevo = new RecordVo("t_wf_file");
                                    if ("1".equals(inforType)) {
                                        objectid = vo.getString("a0100");
                                        basepre = vo.getString("basepre");
                                    } else if ("2".equals(inforType)) {
                                        objectid = vo.getString("b0100");
                                    } else {
                                        objectid = vo.getString("e01a1");
                                    }
                                    zs = zipFile.getInputStream(fileHeader);
                                    is = zs;
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    byte[] tempbytes = new byte[1024];
                                    int byteread = 0;
                                    while ((byteread = is.read(tempbytes)) != -1) {
                                        out.write(tempbytes, 0, byteread);// 将数据写入到os输出流中
                                    }
                                    filevo.setObject("content", out.toByteArray());
                                    filevo.setInt("filetype", Integer.parseInt(flag));
                                    filevo.setInt("ins_id", ins_id);
                                    filevo.setString("name", Importname);
                                    filevo.setInt("tabid", tabid);
                                    filevo.setString("ext", ext);
                                    filevo.setDate("create_time", create_time);
                                    filevo.setString("create_user", create_user);
                                    filevo.setString("objectid", objectid);
                                    filevo.setString("basepre", basepre);
                                    filevo.setInt("attachmenttype", attachmenttype);
                                    // 得到主键
                                    String id = getMaxEitId(this.conn);
                                    filevo.setString("file_id", id);
                                    dao.addValueObject(filevo);
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     * 得到附件表的主键值
     * 
     * @param conn
     * @return
     */
    public String getMaxEitId(Connection conn) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from id_factory where sequence_name='t_wf_file.file_id'");
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (!rs.next()) {
                StringBuffer insertSQL = new StringBuffer();
                insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
                insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
                ArrayList list = new ArrayList();
                dao.insert(insertSQL.toString(), list);
            }
            IDGenerator idg = new IDGenerator(2, this.conn);
            String file_id = idg.getId("t_wf_file.file_id");
            return file_id;
        } catch (Exception e) {
            return null;
        }

    }
    /**
     * gaohy 生成提示excel
     * 
     * @throws GeneralException
     */
    public String generateErrorFile(ArrayList onlyValueRepeat, Sheet sheet1, String form_file, int okCount) throws GeneralException {
    	HSSFWorkbook wb = null;
        String errorFileName = form_file+"_错误提示.xls";
        try {
            wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
            HSSFSheet sheet2 = wb.createSheet();
            HSSFRow row2 = sheet2.createRow(0);

            HSSFFont font2 = wb.createFont();
            font2.setFontHeightInPoints((short) 10);
            HSSFCellStyle style2 = wb.createCellStyle();
            style2.setFont(font2);
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);
            style2.setWrapText(true);
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBottomBorderColor((short) 8);
            style2.setLeftBorderColor((short) 8);
            style2.setRightBorderColor((short) 8);
            style2.setTopBorderColor((short) 8);
            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

            Row row1 = sheet1.getRow(0);
            int cols = row1.getPhysicalNumberOfCells();

            HSSFCell cell2 = row2.createCell(0);
            cell2.setCellValue("成功导入" + okCount + "条。");

            HSSFComment comm = null;
            HSSFPatriarch patr = sheet2.createDrawingPatriarch();
            row2 = sheet2.createRow(1);
            if (row1 != null) {
                int titleCount = 0;
                for (int i = 0; i < cols; i++) {
                    Cell cell = row1.getCell(i);
                    if (cell != null) {
                        cell2 = row2.createCell(i);
                        cell2.setCellValue(cell.getStringCellValue());
                        cell2.setCellStyle(style2);
                        comm = patr.createComment(new HSSFClientAnchor(1, 1, 1, 2, (short) (i + 1), 0, (short) (i + 2), 2));
                        comm.setString(new HSSFRichTextString(cell.getCellComment().getString().getString()));
                        cell2.setCellComment(comm);
                        titleCount++;
                    }
                }
                cols = titleCount;
                ExportExcelUtil.mergeCell(sheet2, 0, 0, 0, cols - 1);
            }

            int rowIndex = 2;
            for (int i = 0; i < onlyValueRepeat.size(); i++) {
                String temp = (String) onlyValueRepeat.get(i);
                row2 = sheet2.createRow(rowIndex++);

                row1 = sheet1.getRow(Integer.parseInt(temp));
                for (int k = 0; k < cols; k++) {
                    Cell cell = row1.getCell(k);
                    if (cell != null) {
                        cell2 = row2.createCell(k);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC :
                                cell2.setCellValue(cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_STRING :
                                cell2.setCellValue(cell.getStringCellValue());
                                break;
                        }
                        // cell2.setCellStyle(cell.getCellStyle());
                    }
                }
            }

            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + errorFileName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeResource(wb);
        }
        return errorFileName;
    }
    /**
     * gaohy 生成提示excel
     * 
     * @throws GeneralException
     */
    public String generateErrorFile(ArrayList onlyValueRepeat, Sheet sheet1, File form_file, int okCount) throws GeneralException {
    	String form_name=form_file.getName().substring(0, form_file.getName().length() - 4) ;
    	return generateErrorFile(onlyValueRepeat, sheet1, form_name, okCount);
    }
	public Integer getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(Integer isMobile) {
		this.isMobile = isMobile;
	}
}
