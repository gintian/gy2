/**
 * FileName: IDeclareServiceImpl
 * Author:   hssoft
 * Date:     2018/12/5 13:58
 * Description: 个税专项申报实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VFSUtil;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.VfsParam;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 〈类功能描述〉<br>
 * 〈个税专项申报实现类〉
 *
 * @author hssoft
 * @create 2018/12/5
 * @since 1.0.0
 */
public class DeclareServiceImpl implements IDeclareService {
    /**
     * 日志
     **/
    private Category log = Category.getInstance(DeclareServiceImpl.class);
    /**
     * 数据库链接
     **/
    private Connection conn;
    private UserView userView;

    public DeclareServiceImpl(Connection conn,UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 根据专项申报类型以及审批状态查询专项申报列表
     *
     * @param declareType  专项申报类型
     * @param approveState 审批状态
     * @return 列表数据
     */

    @Override
    public String searchDeclareList(String declareType, String approveState, UserView userView) throws GeneralException {
        String config = StringUtils.EMPTY;
        try {
            String selectSql = this.getDeclareListSql(declareType, approveState, userView, "");
            //sql组装完成，生成tableBulider
            /**1:生成数据列**/
            ArrayList<ColumnsInfo> columnsInfoList = getTableColumns();
            ArrayList buttonList = getButtonList(userView);
            String subModuleId = "declareListTable";
            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsInfoList, subModuleId, userView, conn);
            builder.setTitle(ResourceFactory.getProperty("gz.zxdeclare.lableTitle"));//个税专项申报
            builder.setDataSql(selectSql);
            builder.setOrderBy(" ORDER BY b0110,e0122,guidkey,create_date desc");
            builder.setSelectable(true);
            builder.setColumnFilter(true);
            builder.setPageSize(20);
            builder.setTableTools(buttonList);
            config = builder.createExtTableConfig();

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.approvelist"));
        }
        return config;
    }

    /**
     * 递归生成功能导航菜单的json串
     *
     * @param name 菜单名
     * @param list 菜单内容
     * @return
     */
    private String getMenuStr(String name, ArrayList list) {
        StringBuffer str = new StringBuffer();
        try {
            if (name.length() > 0) {
                str.append("<jsfn>{xtype:'button',text:'" + name + "'");
            }
            str.append(",menu:{items:[");
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if (i != 0)
                    str.append(",");
                str.append("{");
                if (bean.get("xtype") != null && bean.get("xtype").toString().length() > 0)
                    str.append("xtype:'" + bean.get("xtype") + "'");
                if (bean.get("text") != null && bean.get("text").toString().length() > 0)
                    str.append("text:'" + bean.get("text") + "'");
                if (bean.get("id") != null && bean.get("id").toString().length() > 0)
                    str.append(",id:'" + bean.get("id") + "'");
                if (bean.get("handler") != null && bean.get("handler").toString().length() > 0) {
                    if (bean.get("xtype") != null && "datepicker".equalsIgnoreCase(bean.get("xtype").toString())) {//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
                        str.append(",todayTip:''");//消除今天 按钮提示文字
                        str.append(",handler:function(picker, date){" + bean.get("handler") + ";}");
                    } else {
                        str.append(",handler:function(){" + bean.get("handler") + "();}");
                    }
                }
                if (bean.get("icon") != null && bean.get("icon").toString().length() > 0)
                    str.append(",icon:'" + bean.get("icon") + "'");
                if (bean.get("value") != null && bean.get("value").toString().length() > 0)
                    str.append(",value:" + bean.get("value") + "");
                ArrayList menulist = (ArrayList) bean.get("menu");
                if (menulist != null && menulist.size() > 0) {
                    str.append(this.getMenuStr("", (ArrayList) bean.get("menu")));
                }
                str.append("}");
            }
            str.append("]}");
            if (name.length() > 0) {
                str.append("}</jsfn>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * 生成按钮list
     *
     * @param userView 当前用户信息
     * @return 前台展现的按钮列表
     */
    private ArrayList getButtonList(UserView userView) {
        ArrayList buttonList = new ArrayList();
        //buttonList.add(newButton("数据初始化",null,"declareList.Init",null,"false"));

        ArrayList list = new ArrayList();//功能导航按钮下拉菜单拼装
        LazyDynaBean oneBean = new LazyDynaBean();

        /**新的导入导出布局*/
        //导入申报信息
        oneBean.set("text", IDeclareService.C_BUTTON_IMPORTDATA);
        oneBean.set("handler", "declareList.importFunc");
        list.add(oneBean);

        //导出申报信息
        oneBean = new LazyDynaBean();
        oneBean.set("text", IDeclareService.C_BUTTON_EXPORT_TEMPLATE_EXCEL);
        oneBean.set("handler", "declareList.exportTemplateExcel");
        list.add(oneBean);
        //功能导航
        buttonList.add(this.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), list));

        /*原来的导入导出布局*/
//        buttonList.add(newButton(IDeclareService.C_BUTTON_IMPORTDATA, null, "declareList.importFunc", null, "true"));
//        buttonList.add(newButton(IDeclareService.C_BUTTON_EXPORT_TEMPLATE_EXCEL, null, "declareList.exportTemplateExcel", null, "false"));


        buttonList.add(newButton(IDeclareService.C_BUTTON_AGREE, null, "declareList.agree", null, "true"));
        buttonList.add(newButton(IDeclareService.C_BUTTON_REJECT, null, "declareList.reject", null, "true"));
        if (userView.isSuper_admin() || userView.hasTheFunction("324023301")) {
            buttonList.add(newButton(IDeclareService.C_BUTTON_DELETE, null, "declareList.deleteFunc", null, "true"));
        }


        /*导出Excel*/
        ButtonInfo export = new ButtonInfo();
        export.setFunctype(ButtonInfo.FNTYPE_EXPORT);
        export.setText(IDeclareService.C_BUTTON_EXPORT_EXCEL);
        buttonList.add(export);

        //下载附件功能暂不开发
        //buttonList.add(newButton(IIDeclareService.C_BUTTON_DOWN_ATTACH,null,"declareList.downAttach",null,"true"));

        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("GZ00000702");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText(ResourceFactory.getProperty("gz.zxdeclare.msgPleaseInputName"));
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * @param text    按钮显示文字
     * @param id      按钮id
     * @param handler 按钮触发方法
     * @param icon    按钮图标
     * @param getdata 事件触发时是否获取选中数据
     * @return
     * @author lis
     * @Description: 生成按钮
     * @date 2015-11-3
     */
    private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if (icon != null)
            button.setIcon(icon);
        if (id != null)
            button.setId(id);
        return button;
    }

    /**
     * 刷新专项申报数据列表
     *
     * @param declareType  专项申报类型
     * @param approveState 审批状态
     * @param userView     登录用户数据
     * @return 成功 success 失败 fail
     */

    @Override
    public String refsDeclareList(String declareType, String approveState, UserView userView) throws GeneralException {
        try {
            String subModuleId = "declareListTable";
            String selectSql = this.getDeclareListSql(declareType, approveState, userView, "");
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(subModuleId);
            tableCache.setTableSql(selectSql);
            userView.getHm().put(subModuleId, tableCache);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.refApprovelist"));
        }
        return "success";
    }

    /**
     * 获取查询列表数据的sql
     *
     * @param declareType  专项申报类型
     * @param approveState 审批状态
     * @param userView     登录用户信息
     * @param extraSql     附加sql 用于扩充sql语句
     * @return 查询sql
     */
    private String getDeclareListSql(String declareType, String approveState, UserView userView, String extraSql) {
        StringBuffer selectSqlBuffer = new StringBuffer();
        try {
            //DbNameBo dbNameBo = new DbNameBo(this.conn);
            //List naseList = dbNameBo.getAllLoginDbNameList();//此处不再考虑没有配置登录人员库的问题
            //List userViewPrivDbList = userView.getPrivDbList();
            ///**循环依据权限过滤掉不在权限范围内的nbase**/
            //Iterator iterator = naseList.iterator();
            //while (iterator.hasNext()) {
            //    RecordVo recordVo = (RecordVo) iterator.next();
            //    String pre = recordVo.getString("pre");
            //    if (!userViewPrivDbList.contains(pre)) {//不包含当前人员库则移除
            //        iterator.remove();
            //    }
            //} fixme 不考虑登录认证应用库，只要此登录人员有权限的库就显示 wangz
            List naseList = userView.getPrivDbList();
            String select = "(select ";
            String columns = "id,declare_infor.guidkey guidkey,b0110,e0122,a0101,";
            columns = columns + Sql_switcher.substr("declare_type", "1", "2") + " as declare_type,create_date,deduct_type,deduct_money,approve_state,start_date,(case " + Sql_switcher.dateToChar("end_date", "yyyy-MM-dd") + " when '9999-12-31' then null else end_date end) as end_date,description ";
            String from = " from";
            String privUnitSql = getPrivUnitSql(userView);//获取权限范围
            for (int i = 0; i < naseList.size(); i++) {
                //RecordVo recordVo = (RecordVo) naseList.get(i);
                //String pre = recordVo.getString("pre");
                String pre = (String) naseList.get(i);
                selectSqlBuffer.append("select infor.id,infor.guidkey,B0110,E0122,A0101,declare_type,create_date,deduct_type,");
                if(Sql_switcher.searchDbServer()==Constant.ORACEL){
                    selectSqlBuffer.append("cast(case when approve_state ='03'then (case when infor.declare_type<>'01' then infor.deduct_money else t.money end) else null end as varchar2(20)) as deduct_money, ");
                }else{
                    selectSqlBuffer.append("case when approve_state ='03'then (case when infor.declare_type<>'01' then infor.deduct_money else t.money end) else null end  as deduct_money, ");
                }
                selectSqlBuffer.append("approve_state,start_date,end_date,description,a0100, nbase from ");
                selectSqlBuffer.append(select).append(columns).append(",a0100,").append("'").append(pre).append("' as nbase ").append(from);
                selectSqlBuffer.append(" declare_infor declare_infor,");
                selectSqlBuffer.append(pre).append("a01").append(" a01 where a01.guidkey=declare_infor.guidkey");
                //selectSqlBuffer.append(" where declare_type=? and approve_state=? ");
                if (StringUtils.equalsIgnoreCase(IDeclareService.C_DECLARE_TYPE_ALL, declareType)) {//如果是-1,则是查询全部分类
                    if (StringUtils.equalsIgnoreCase(IDeclareService.C_APPROVE_STATE_ALL, approveState)) {//审批状态是-1时查询不等于起草状态的
                        selectSqlBuffer.append(" and approve_state <>'01'");
                    } else {
                        selectSqlBuffer.append(" and approve_state ='").append(approveState).append("'");
                    }
                } else {
                    selectSqlBuffer.append(" and declare_type='").append(declareType).append("' ");
                    if (StringUtils.equalsIgnoreCase(IDeclareService.C_APPROVE_STATE_ALL, approveState)) {//审批状态是-1时查询不等于起草状态的
                        selectSqlBuffer.append(" and approve_state <>'01'");
                    } else {
                        selectSqlBuffer.append(" and approve_state ='").append(approveState).append("'");
                    }
                }
                selectSqlBuffer.append(privUnitSql);
                selectSqlBuffer.append(") infor ");
                selectSqlBuffer.append("left join (select sum(s.deduct_money) money,s.declare_id declare_id from declare_infor d, declare_sub s where d.id=s.declare_id and d.declare_type='01' and d.approve_state='03'");
                if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                    selectSqlBuffer.append(" and getDate()>s.start_date and getDate()< ");
                } else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    selectSqlBuffer.append(" and SYSDATE>s.start_date and SYSDATE<  ");
                }
                Calendar cale = null;
                cale = Calendar.getInstance();
                int year = cale.get(Calendar.YEAR);
                if (Sql_switcher.searchDbServer() == Constant.MSSQL) {//sqlserver
                    selectSqlBuffer.append("ISNULL(s.end_date,");
                    selectSqlBuffer.append("'");
                    selectSqlBuffer.append(year + "-12-31')");
                //Sql_switcher.searchDbServer()==Constant.ORACEL 判断方法 用于判断oracle和达梦数据库；
                //Sql_switcher.searchDbServerFlag()==Constant.DAMENG 判断方法 只用于判断达梦数据库
                } else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    selectSqlBuffer.append("nvl(s.end_date,");
                    selectSqlBuffer.append("to_date(");
                    selectSqlBuffer.append("'" + year + "-12-31','yyyy-MM-dd'))");
                }
                selectSqlBuffer.append("group by s.declare_id ) t on infor.id = t.declare_id ");
                if (!StringUtils.isEmpty(extraSql)) {
                    selectSqlBuffer.append(extraSql);//查询申报日期为当年的限制sql
                }
                if (i < naseList.size() - 1) {
                    selectSqlBuffer.append(" union all ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectSqlBuffer.toString();
    }

    /**
     * 数据列表
     *
     * @return 数据列表
     */
    private ArrayList<ColumnsInfo> getTableColumns() {
        //b0110,e0122,id,declare.guidkey,declare_type,create_date,deduct_type,approve_state,start_date,end_date,description
        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        FieldItem item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("guidkey");
        item.setItemdesc("guidkey");//
        item.setItemtype("A");
        item.setItemlength(38);
        item.setCodesetid("0");
        ColumnsInfo info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("id");
        item.setItemdesc("declareId");//
        item.setItemtype("A");
        item.setItemlength(38);
        item.setCodesetid("0");
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setEncrypted(true);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(info);

        item = DataDictionary.getFieldItem("B0110");
        item.setReadonly(true);
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);

        item = DataDictionary.getFieldItem("E0122");
        item.setReadonly(true);
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);

        item = DataDictionary.getFieldItem("A0101", "A01");
        item.setReadonly(true);
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setRendererFunc("declareList.declareInfor");
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);

        info = new ColumnsInfo();
        info.setReadOnly(true);
        info.setColumnId("declare_type");
        info.setColumnDesc(ResourceFactory.getProperty("gz.zxdeclare.lableDeclareType"));//专项附加类型
        info.setColumnType("A");
        info.setColumnLength(38);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setOperationData(this.getDeclraTypeOperationData());
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("create_date");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableDeclareDate"));//申报时间
        item.setItemtype("D");
        info = new ColumnsInfo(item);
        info.setQueryable(true);
        info.setTextAlign("left");
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setColumnLength(10);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("deduct_type");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableDeductType"));//抵扣方式
        item.setItemtype("A");
        item.setCodesetid("0");
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setQueryable(false);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setOperationData(this.getdeductTypeOperationData());
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("deduct_money");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableDeductMoney"));//
        item.setItemtype("N");
        item.setItemlength(8);//先设置成8位？
        item.setDecimalwidth(2);//先设置成2位？
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);


        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("approve_state");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableApproveState"));//审核状态
        item.setItemtype("A");
        item.setItemlength(2);
        info = new ColumnsInfo(item);
        info.setOperationData(this.getApproveStateOperationData());
        info.setEditableValidFunc("false");
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setColumnWidth(150);
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("start_date");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableStartDate"));//起始日期
        item.setItemtype("D");
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setTextAlign("left");
        info.setColumnWidth(150);
        info.setColumnLength(10);
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("end_date");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableEndDate"));//结束日期
        item.setItemtype("D");
        info = new ColumnsInfo(item);
        info.setEditableValidFunc("false");
        info.setTextAlign("left");
        info.setColumnWidth(150);
        info.setColumnLength(10);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        columnsList.add(info);

        item = new FieldItem();
        item.setReadonly(true);
        item.setItemid("description");
        item.setItemdesc(ResourceFactory.getProperty("gz.zxdeclare.lableDescription"));//备注
        item.setItemtype("M");
        info = new ColumnsInfo(item);
        info.setInputType(0);
        info.setEditableValidFunc("false");
        info.setColumnWidth(150);
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setRendererFunc("declareList.renderLableDescription");
        columnsList.add(info);
        return columnsList;
    }

    /**
     * 根据权限管理范围生成权限相关的sql
     *
     * @param userView 登录用户信息
     * @return 权限条件sql
     */
    private String getPrivUnitSql(UserView userView) {
        if (userView.isSuper_admin()) {//超级用户视为全权
            return " and 1=1 ";
        }
        String b_units = userView.getUnitIdByBusi("1");//1:工资发放
        if (StringUtils.isBlank(b_units)) {//没有任何权限,所有数据都不展示
            return " and 1=2 ";
        }
        if (StringUtils.equalsIgnoreCase("UN", b_units) || StringUtils.equalsIgnoreCase("UN`", b_units)) {//全权
            return " and 1=1 ";
        }
        List<String> privUnitList = new ArrayList<String>();
        String[] units = b_units.split("`");
        for (String unit : units) {
            unit = unit.substring(2);
            privUnitList.add(unit);
        }
        StringBuffer privSqlBuf = new StringBuffer();
        privSqlBuf.append(" and (case when ((");
        if (Sql_switcher.searchDbServer() == 1) {//sqlserver
            privSqlBuf.append(Sql_switcher.isnull("e0122", "'#'")).append("='#' or e0122='') and (");

        } else if (Sql_switcher.searchDbServer() == 2) {
            privSqlBuf.append(Sql_switcher.isnull("e0122", "'#'")).append("='#') and (");
        }
        String b0110LikeSql = loopConstrutLikeSql("b0110", privUnitList);
        String e0122LikeSql = loopConstrutLikeSql("e0122", privUnitList);

        privSqlBuf.append(b0110LikeSql).append(")) then 1 ");
        privSqlBuf.append(" when (").append(b0110LikeSql).append(" or ").append(e0122LikeSql).append(") then 1 else 0 end)=1");
        //TODO xuchangshun 测试没有问题的话,去掉权限
        log.error(ResourceFactory.getProperty("gz.zxdeclare.error.currentFilterSql") + privSqlBuf.toString());
        return privSqlBuf.toString();
    }

    /**
     * 循环生成likesql
     *
     * @param column 用哪个字段进行like
     * @param values 要被liske的值
     * @return 拼装成的sql
     */
    private String loopConstrutLikeSql(String column, List<String> values) {
        StringBuffer loopStr = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            String unit = values.get(i);
            loopStr.append(column).append("  like '").append(unit).append("%'");
            if (i < values.size() - 1) {
                loopStr.append(" or ");
            }
        }
        return loopStr.toString();
    }

    /**
     * 同意通过专项申报
     *
     * @param declares 专项申报ids,使用逗号进行分割
     * @return 成功返回 success 失败返回 faile
     */

    @Override
    public String approveDeclares(String declares) throws GeneralException {
        try {
            String[] declareIds = declares.split(",");
            ContentDAO dao = new ContentDAO(conn);
            List<RecordVo> updateList = this.getDealRecordList(declares, IDeclareService.C_OPERATE_TYPE_APPROVE, null);
            dao.updateValueObject(updateList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.approveDeclare"));
        }
        return "success";
    }

    /**
     * 获取要处理的数据列表
     *
     * @param declares 要处理数据的ids
     * @param operType 处理类型  同意:通过 退回:未通过 删除:直接删除数据
     * @param param    存放扩展参数
     * @return 要处理的数据
     */
    private List<RecordVo> getDealRecordList(String declares, String operType, Map<String, Object> param) {
        String[] declareIds = declares.split(",");
        ContentDAO dao = new ContentDAO(conn);
        List<RecordVo> recordList = new ArrayList<RecordVo>();
        StringBuffer deletesql = new StringBuffer();
        deletesql.append("delete from declare_sub where ");
        deletesql.append(" declare_id in(");
        try {
            for (String declareId : declareIds) {
                declareId = PubFunc.decrypt(declareId);//获得到原始代码
                RecordVo vo = new RecordVo("declare_infor");
                vo.setInt("id", Integer.parseInt(declareId));
                vo = dao.findByPrimaryKey(vo);//找到这条数据
                if (IDeclareService.C_OPERATE_TYPE_APPROVE.equals(operType)) {
                    vo.setString("approve_state", IDeclareService.C_APPROVE_STATE_ADOPT);
                } else if (IDeclareService.C_OPERATE_TYPE_REJECT.equals(operType)) {
                    //获取驳回原因
                    if (declareIds.length == 1) {//只考虑单人驳回时  添加驳回意见，批量驳回未作处理
                        String approveDesc = (String) param.get("approveDesc");
                        vo.setString("approve_desc", approveDesc);
                    }
                    vo.setString("approve_state", IDeclareService.C_APPROVE_STATE_NOTPASS);
                } else if (IDeclareService.C_OPERATE_TYPE_DELETE.equals(operType)) {
                    deletesql.append(declareId);
                    deletesql.append(",");
                }
                recordList.add(vo);
            }
            if (IDeclareService.C_OPERATE_TYPE_DELETE.equals(operType)) {//删除declare_sub 对应记录
                deletesql.setLength(deletesql.length() - 1);//去掉最后一个逗号
                deletesql.append(")");
                dao.update(deletesql.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordList;
    }

    /**
     * 退回专项申报
     *
     * @param declares 专项申报ids,使用逗号进行分割
     * @param param    专项申报ids,使用逗号进行分割
     * @return 成功返回 success 失败返回 faile
     */

    @Override
    public String rejectDeclares(String declares, Map<String, Object> param) throws GeneralException {
        try {
            String[] declareIds = declares.split(",");
            ContentDAO dao = new ContentDAO(conn);
            List<RecordVo> updateList = this.getDealRecordList(declares, IDeclareService.C_OPERATE_TYPE_REJECT, param);
            dao.updateValueObject(updateList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.rejectDeclare"));
        }
        return "success";
    }

    /**
     * 删除专项申报
     *
     * @param declares 专项申报 ids,使用逗号进行分割
     * @return 成功返回success 失败返回faile
     */

    @Override
    public String deleteDeclares(String declares) throws GeneralException {
        try {
            String[] declareIds = declares.split(",");
            ContentDAO dao = new ContentDAO(conn);
            List<RecordVo> updateList = this.getDealRecordList(declares, IDeclareService.C_OPERATE_TYPE_DELETE, null);
            dao.deleteValueObject(updateList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.deleteDeclare"));
        }
        return "success";
    }

    /**
     * 获取申报记录
     */

    @Override
    public ArrayList<HashMap> listZXDeclare(String whereSql, ArrayList valueList, String orderSql, UserView userView)
            throws GeneralException {
//		this.revokeZXDeclare();
        ArrayList<HashMap> list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100());
        sql.append("select * from declare_infor where 1=1 ");
        if (whereSql != null && whereSql.trim().length() > 0) {
            sql.append(" and " + whereSql);
        }
        sql.append(" and guidkey=? ");
        if (orderSql != null && orderSql.trim().length() > 0) {
            sql.append(orderSql);
        } else {
            sql.append(" order by id ");
        }
        valueList.add(this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), valueList);
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                HashMap map = new HashMap();
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    if ("id".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        map.put(data.getColumnName(i).toLowerCase(), PubFunc.encrypt(String.valueOf(rs.getObject(data.getColumnName(i).toLowerCase()))));
                        continue;
                    }
                    if ("rent_house_province".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_province_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_city".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_city_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_id_type".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AC", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_id_type_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if (Types.DATE == data.getColumnType(i) || Types.TIMESTAMP == data.getColumnType(i)) {
                        String date = rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : sdf.format(rs.getObject(data.getColumnName(i).toLowerCase()));
                        if ("9999-12-31".equalsIgnoreCase(date))
                            date = "";
                        map.put(data.getColumnName(i).toLowerCase(), date);
                    } else {
                        map.put(data.getColumnName(i).toLowerCase(), rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : rs.getObject(data.getColumnName(i).toLowerCase()));
                    }
                }
                String declare_type = rs.getString("declare_type");
                if (declare_type.startsWith(C_DECLARE_TYPE_CHILDEDU))
                    map.put("sub_items", this.getDeclareSub(rs.getInt("id")));
                if (declare_type.startsWith(C_DECLARE_TYPE_SUPPORT_ELDERLY)) {
                    ArrayList sub_items = this.getDeclareSub(rs.getInt("id"));
                    ArrayList sub_old_items = new ArrayList();
                    ArrayList sub_child_items = new ArrayList();
                    for (int i = 0; i < sub_items.size(); i++) {
                        HashMap subHM = (HashMap) sub_items.get(i);
                        String relation_type = (String) subHM.get("relation_type");
                        if (C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD.equalsIgnoreCase(relation_type))
                            sub_old_items.add(subHM);
                        else
                            sub_child_items.add(subHM);
                    }
                    map.put("sub_old_items", sub_old_items);
                    map.put("sub_child_items", sub_child_items);
                }
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取申报记录数出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.listDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.listDeclareMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    @Override
    public String getMinPayDate(UserView userView) throws GeneralException {
        String minPayDate = StringUtils.EMPTY;//最早发薪日期
        StringBuffer sql = new StringBuffer();
        List<String> list = new ArrayList<String>();

        String nBase = userView.getDbname();
        String A0100 = userView.getA0100();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();
        String nowDateStr = sdf.format(nowDate);

        list.add(A0100);
        list.add(nBase);
        list.add(A0100);
        list.add(nBase);

        sql.append("select min(currentyeardate) from ");
        sql.append(" (select min(tax_date) as currentyeardate from gz_tax_mx mx ");
        sql.append(" where mx.A0100=? and mx.NBASE=? and mx.tax_date>=");
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append("DATEADD(yy, DATEDIFF(yy,0,GETDATE()),0)");
        } else {
            sql.append("trunc(SYSDATE,'yy')");
        }
        sql.append(" UNION ALL ");
        sql.append(" select min(tax_date) as currentyeardate from taxarchive ");
        sql.append(" where taxarchive.A0100=? and taxarchive.NBASE=? and taxarchive.tax_date>=");
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append("DATEADD(yy, DATEDIFF(yy,0,GETDATE()),0)");
        } else {
            sql.append("trunc(SYSDATE,'yy')");
        }
        sql.append(") temp");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                Date date = rs.getDate(1);
                if (date != null) {
                    minPayDate = sdf.format(rs.getDate(1));
                } else {
                    minPayDate = nowDateStr;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取当年最早计税日期出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getMinPayDate"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getMinPayDate"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return minPayDate;
    }

    /**
     * 获取6项申报记录
     */

    @Override
    public HashMap listZXDeclare(UserView userView) throws GeneralException {
//		this.revokeZXDeclare();
        HashMap HMData = new HashMap();
        StringBuffer sql = new StringBuffer();
        sql.append("select * from declare_infor where guidkey=? and approve_state<>? ");
//		if(Sql_switcher.searchDbServer() == 1)
//			sql.append(" and (year(end_date) = year(getDate()) or end_date > getDate()) ");
//		else if(Sql_switcher.searchDbServer() == 2)
//			sql.append(" and (to_char(end_date,'yyyy') = to_char(sysdate,'yyyy') or end_date > sysdate) ");
        sql.append(" order by declare_type");
        String guidkey = this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList list = new ArrayList();
        list.add(guidkey);
        list.add(C_APPROVE_STATE_FILED);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                HashMap map = new HashMap();
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    if ("id".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        map.put(data.getColumnName(i).toLowerCase(), PubFunc.encrypt(String.valueOf(rs.getObject(data.getColumnName(i).toLowerCase()))));
                        continue;
                    }
                    if ("rent_house_province".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_province_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_city".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_city_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_id_type".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AC", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("rent_house_id_type_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if (Types.DATE == data.getColumnType(i) || Types.TIMESTAMP == data.getColumnType(i)) {
                        String date = rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : sdf.format(rs.getObject(data.getColumnName(i).toLowerCase()));
                        if ("9999-12-31".equalsIgnoreCase(date))
                            date = "";
                        map.put(data.getColumnName(i).toLowerCase(), date);
                    } else {
                        map.put(data.getColumnName(i).toLowerCase(), rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : rs.getObject(data.getColumnName(i).toLowerCase()));
                    }
                }
                String declare_type = rs.getString("declare_type");
                String deduct_type = rs.getString("deduct_type");

                if (declare_type.startsWith(C_DECLARE_TYPE_SUPPORT_ELDERLY)) {
                    ArrayList sub_items = this.getDeclareSub(rs.getInt("id"));
                    ArrayList sub_old_items = new ArrayList();
                    ArrayList sub_child_items = new ArrayList();
                    for (int i = 0; i < sub_items.size(); i++) {
                        HashMap subHM = (HashMap) sub_items.get(i);
                        String relation_type = (String) subHM.get("relation_type");
                        if (C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD.equalsIgnoreCase(relation_type))
                            sub_old_items.add(subHM);
                        else
                            sub_child_items.add(subHM);
                    }
                    map.put("sub_old_items", sub_old_items);
                    map.put("sub_child_items", sub_child_items);
                } else {
                    map.put("sub_items", this.getDeclareSub(rs.getInt("id")));
                }
                if (HMData.containsKey("zx_" + declare_type)) {
                    ArrayList dataList = (ArrayList) HMData.get("zx_" + declare_type);
                    dataList.add(map);
                    HMData.put("zx_" + declare_type, dataList);
                } else {
                    ArrayList datalist = new ArrayList();
                    datalist.add(map);
                    HMData.put("zx_" + declare_type, datalist);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取6项专项申报记录出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.listDeclareDataMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.listDeclareDataMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return HMData;
    }


    /**
     * 获取单个专项申报的数据
     *
     * @param declarid 专项申报的id,加密的
     * @return 专项申报数据的Map集合
     * @throws GeneralException
     */

    @Override
    public Map getDeclareInfor(String declarid) throws GeneralException {
        Map map = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        ArrayList attach_path = new ArrayList();

        sql.append("select * from declare_infor where id=?");
        list.add(Integer.parseInt(PubFunc.decrypt(declarid)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    if (StringUtils.equalsIgnoreCase("id", data.getColumnName(i))) {
                        map.put(data.getColumnName(i).toLowerCase(), PubFunc.encrypt(String.valueOf(rs.getObject(data.getColumnName(i).toLowerCase()))));
                        continue;
                    }
                    if (StringUtils.equalsIgnoreCase("fileid", data.getColumnName(i))) {
                        String fileIdStrs = rs.getString("fileid");
                        if (StringUtils.isNotBlank(fileIdStrs)) {
                            String[] fileIdStrsArr = fileIdStrs.split("`");
                            for (String fileId : fileIdStrsArr) {
                                if(StringUtils.isNotBlank(fileId)){
                                    Map fileMap = new HashMap();
                                    VfsFileEntity fileEntity = VfsService.getFileEntity(fileId);
                                    fileMap.put("fileName", fileEntity.getName());
                                    fileMap.put("fileId", fileId);
                                    attach_path.add(fileMap);
                                }
                            }
                        }
                    }
                    if (Types.DATE == data.getColumnType(i) || Types.TIMESTAMP == data.getColumnType(i)) {
                        String date = rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : sdf.format(rs.getObject(data.getColumnName(i).toLowerCase()));
                        if ("9999-12-31".equalsIgnoreCase(date))
                            date = "";
                        map.put(data.getColumnName(i).toLowerCase(), date);
                    } else {
                        map.put(data.getColumnName(i).toLowerCase(), rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : rs.getObject(data.getColumnName(i).toLowerCase()));
                    }
                }
            }

            map.put("attach_path", attach_path);

            sql.setLength(0);
            sql.append("select * from declare_sub where declare_id=?");
            rs = dao.search(sql.toString(), list);
            data = rs.getMetaData();
            ArrayList subList = new ArrayList();
            while (rs.next()) {
                Map subHM = new HashMap();
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    if ("nationality".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AD", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("nationality_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("id_type".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AC", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("id_type_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_province".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("rent_house_province_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_city".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AB", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("rent_house_city_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("rent_house_id_type".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AC", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("rent_house_id_type_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("edu_nationality".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AD", rs.getString(data.getColumnName(i).toLowerCase()));
                        subHM.put("edu_nationality_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if (Types.DATE == data.getColumnType(i) || Types.TIMESTAMP == data.getColumnType(i)) {
                        String date = rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : sdf.format(rs.getObject(data.getColumnName(i).toLowerCase()));
                        if ("9999-12-31".equalsIgnoreCase(date))
                            date = "";
                        subHM.put(data.getColumnName(i).toLowerCase(), date);
                    } else {
                        subHM.put(data.getColumnName(i).toLowerCase(), rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : rs.getObject(data.getColumnName(i).toLowerCase()));
                    }
                }
                subList.add(subHM);
            }
            if (((String) map.get("declare_type")).startsWith(C_DECLARE_TYPE_SUPPORT_ELDERLY)) {
                ArrayList sub_old_items = new ArrayList();
                ArrayList sub_child_items = new ArrayList();
                for (int i = 0; i < subList.size(); i++) {
                    HashMap subHM = (HashMap) subList.get(i);
                    String relation_type = (String) subHM.get("relation_type");
                    if (C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD.equalsIgnoreCase(relation_type))
                        sub_old_items.add(subHM);
                    else
                        sub_child_items.add(subHM);
                }
                map.put("sub_old_items", sub_old_items);
                map.put("sub_child_items", sub_child_items);
            } else {
                map.put("sub_items", subList);
            }


            String guidkey = (String) map.get("guidkey");
            HashMap A01InfoHM = getA01Info(guidkey);
            String name = "";
            if (StringUtils.isNotEmpty((String) A01InfoHM.get("B0110")) && StringUtils.isNotEmpty((String) A01InfoHM.get("E0122")) && StringUtils.isNotEmpty((String) A01InfoHM.get("A0101"))) {
                name = A01InfoHM.get("B0110") + "/" + A01InfoHM.get("E0122") + "/" + A01InfoHM.get("A0101");
            } else if (StringUtils.isNotEmpty((String) A01InfoHM.get("B0110")) && StringUtils.isNotEmpty((String) A01InfoHM.get("A0101"))) {
                name = A01InfoHM.get("B0110") + "/"  + A01InfoHM.get("A0101");
            }else if(StringUtils.isNotEmpty((String) A01InfoHM.get("A0101"))){
                name = (String)A01InfoHM.get("A0101");
            }
            map.put("name", name);
        } catch (Exception e) {
            e.printStackTrace();
            //获取专项申报数据出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getDeclareMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 保存申报记录
     */

    @Override
    public String saveZXDeclare(HashMap param, UserView userView) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RecordVo vo = new RecordVo("declare_infor");
        String id = "";
        try {
            if (param.containsKey("id")) {
                id = PubFunc.decrypt((String) param.get("id"));
            } else {
                IDGenerator idGenerator = new IDGenerator(2, this.conn);
                id = idGenerator.getId("declare_infor.id");
                vo.setString("parent_id", null);
                vo.setString("approve_state", C_APPROVE_STATE_DRAFT);
                vo.setDate("create_date", sdf.format(new Date()));
            }
            vo.setInt("id", Integer.parseInt(id));
            if (param.containsKey("id")) {
                vo = dao.findByPrimaryKey(vo);
            }
            String declare_type = param.containsKey("declare_type") ? (String) param.get("declare_type") : "";
            String guidkey = this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100());
            sql.setLength(0);
            list.clear();

            vo.setString("guidkey", guidkey); //人员唯一标识
            vo.setString("declare_type", declare_type); // 申报类型
            vo.setString("deduct_type", param.containsKey("deduct_type") ? (String) param.get("deduct_type") : "");  //抵扣方式   现在默认01 按月
            vo.setDouble("deduct_money", param.containsKey("deduct_money") ? Double.parseDouble((String) param.get("deduct_money")) : 0);// 抵扣金额

            vo.setDate("start_date", param.containsKey("start_date") ? (String) param.get("start_date") : "");// 起始日期
            try {
                vo.setDate("end_date", param.containsKey("end_date") ? sdf.parse((String) param.get("end_date")) : sdf.parse("9999-12-31"));// 终止日期
            } catch (Exception e) {
                e.printStackTrace();
                // 保存时日期格式处理出错
                log.error(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
                throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
            }
            vo.setString("description", param.containsKey("description") ? (String) param.get("description") : "");  // 备注

            if (C_DECLARE_TYPE_CONTINU_EDU.equalsIgnoreCase(declare_type)) {//继续教育
                vo.setString("cuntin_edu_type", param.containsKey("cuntin_edu_type") ? (String) param.get("cuntin_edu_type") : "");//继续教育类型
            } else if (C_DECLARE_TYPE_HOUSING_RENT.equalsIgnoreCase(declare_type)) {//住房租金
                vo.setString("rent_city_type", param.containsKey("rent_city_type") ? (String) param.get("rent_city_type") : "");//城市类型
            } else if (C_DECLARE_TYPE_INTEREST_EXPENSE.equalsIgnoreCase(declare_type)) {//贷款利息
                vo.setString("house_address", param.containsKey("house_address") ? (String) param.get("house_address") : "");//房屋坐落地址
                vo.setString("loan_self_flag", param.containsKey("loan_self_flag") ? (String) param.get("loan_self_flag") : "");//本人是否借款人
                vo.setString("house_type", param.containsKey("house_type") ? (String) param.get("house_type") : "");//房屋证书类型
                vo.setString("house_number", param.containsKey("house_number") ? (String) param.get("house_number") : "");//房屋证书号码
                vo.setString("loan_flat", param.containsKey("loan_flat") ? (String) param.get("loan_flat") : "");//是否婚前各自首套贷款，且婚后分别扣除50%
            } else if (C_DECLARE_TYPE_SUPPORT_ELDERLY.equalsIgnoreCase(declare_type)) {//赡养老人
                vo.setInt("child_apportion", param.containsKey("child_apportion") ? (Integer) param.get("child_apportion") : 0);//平摊人数
                vo.setString("apportion_type", param.containsKey("apportion_type") ? (String) param.get("apportion_type") : "");//赡养方式
            }

            //将附件的fileId入库
            String oldFileIdStr = vo.getString("fileid");
            String addFileIdStrs = (String) param.get("addFileIdStrs");
            if (StringUtils.isBlank(oldFileIdStr)) {
                vo.setString("fileid", addFileIdStrs);
            }else{
                String delFileIdStrs = (String) param.get("delFileIdStrs");
                String[] delFileIdStrsArr = delFileIdStrs.split("`");
                for (int i = 0; i < delFileIdStrsArr.length; i++) {
                    String delFileId = delFileIdStrsArr[i];
                    if(StringUtils.isNotBlank(delFileId)){
                        VfsService.deleteFile(this.userView.getUserName(), delFileId);
                        oldFileIdStr = oldFileIdStr.replace("`" + delFileId, "");
                    }
                }
                String newFileIdStr = oldFileIdStr + addFileIdStrs;
                vo.setString("fileid", newFileIdStr);
            }


            if (param.containsKey("id")) {
                dao.updateValueObject(vo);
            } else {
                dao.addValueObject(vo);
            }


            ArrayList sub_items = null;
            if (param.containsKey("sub_items")) {
                sub_items = (ArrayList) param.get("sub_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            }
            if (param.containsKey("sub_old_items")) {
                sub_items = (ArrayList) param.get("sub_old_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            } else {
                this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD);
            }
            if (param.containsKey("sub_child_items")) {
                sub_items = (ArrayList) param.get("sub_child_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            } else {
                this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_CHILD);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //保存专项申报数据出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
        }
        return "success`" + PubFunc.encrypt(id);
    }

    /**
     * 提交申报记录
     */

    @Override
    public String submitZXDeclare(HashMap param, UserView userView) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RecordVo vo = new RecordVo("declare_infor");
        String id = "";
        try {
            if (param.containsKey("id")) {
                id = PubFunc.decrypt((String) param.get("id"));
            } else {
                IDGenerator idGenerator = new IDGenerator(2, this.conn);
                id = idGenerator.getId("declare_infor.id");
                vo.setString("parent_id", null);
                vo.setDate("create_date", sdf.format(new Date()));
            }
            vo.setInt("id", Integer.parseInt(id));
            if (param.containsKey("id")) {
                vo = dao.findByPrimaryKey(vo);
            }
            vo.setString("approve_state", C_APPROVE_STATE_INAUDIT);
            String declare_type = param.containsKey("declare_type") ? (String) param.get("declare_type") : "";
            String guidkey = this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100());
            sql.setLength(0);
            list.clear();

            vo.setString("guidkey", guidkey); //人员唯一标识
            vo.setString("declare_type", declare_type); // 申报类型
            vo.setString("deduct_type", param.containsKey("deduct_type") ? (String) param.get("deduct_type") : "");  //抵扣方式   现在默认01 按月
            vo.setDouble("deduct_money", param.containsKey("deduct_money") ? Double.parseDouble((String) param.get("deduct_money")) : 0);// 抵扣金额

            vo.setDate("start_date", param.containsKey("start_date") ? (String) param.get("start_date") : "");// 起始日期
            try {
                vo.setDate("end_date", param.containsKey("end_date") ? sdf.parse((String) param.get("end_date")) : sdf.parse("9999-12-31"));// 终止日期
            } catch (Exception e) {
                e.printStackTrace();
                // 保存时日期格式处理出错
                log.error(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
                throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
            }
            vo.setString("description", param.containsKey("description") ? (String) param.get("description") : "");  // 备注

            if (C_DECLARE_TYPE_CONTINU_EDU.equalsIgnoreCase(declare_type)) {//继续教育
                vo.setString("cuntin_edu_type", param.containsKey("cuntin_edu_type") ? (String) param.get("cuntin_edu_type") : "");//继续教育类型
            } else if (C_DECLARE_TYPE_HOUSING_RENT.equalsIgnoreCase(declare_type)) {//住房租金
                vo.setString("rent_city_type", param.containsKey("rent_city_type") ? (String) param.get("rent_city_type") : "");//城市类型
            } else if (C_DECLARE_TYPE_INTEREST_EXPENSE.equalsIgnoreCase(declare_type)) {//贷款利息
                vo.setString("house_address", param.containsKey("house_address") ? (String) param.get("house_address") : "");//房屋坐落地址
                vo.setString("loan_self_flag", param.containsKey("loan_self_flag") ? (String) param.get("loan_self_flag") : "");//本人是否借款人
                vo.setString("house_type", param.containsKey("house_type") ? (String) param.get("house_type") : "");//房屋证书类型
                vo.setString("house_number", param.containsKey("house_number") ? (String) param.get("house_number") : "");//房屋证书号码
                vo.setString("loan_flat", param.containsKey("loan_flat") ? (String) param.get("loan_flat") : "");//是否婚前各自首套贷款，且婚后分别扣除50%
            } else if (C_DECLARE_TYPE_SUPPORT_ELDERLY.equalsIgnoreCase(declare_type)) {//赡养老人
                vo.setInt("child_apportion", param.containsKey("child_apportion") ? (Integer) param.get("child_apportion") : 0);//平摊人数
                vo.setString("apportion_type", param.containsKey("apportion_type") ? (String) param.get("apportion_type") : "");//赡养方式
            }

            //将附件的fileId入库
            String oldFileIdStr = vo.getString("fileid");
            String addFileIdStrs = (String) param.get("addFileIdStrs");
            if (StringUtils.isBlank(oldFileIdStr)) {
                vo.setString("fileid", addFileIdStrs);
            }else{
                String delFileIdStrs = (String) param.get("delFileIdStrs");
                String[] delFileIdStrsArr = delFileIdStrs.split("`");
                for (int i = 0; i < delFileIdStrsArr.length; i++) {
                    String delFileId = delFileIdStrsArr[i];
                    if(StringUtils.isNotBlank(delFileId)){
                        VfsService.deleteFile(this.userView.getUserName(), delFileId);
                        oldFileIdStr = oldFileIdStr.replace("`" + delFileId, "");
                    }
                }
                String newFileIdStr = oldFileIdStr + addFileIdStrs;
                vo.setString("fileid", newFileIdStr);
            }

            if (param.containsKey("id")) {
                dao.updateValueObject(vo);
            } else {
                dao.addValueObject(vo);
            }

            ArrayList sub_items = null;
            if (param.containsKey("sub_items")) {
                sub_items = (ArrayList) param.get("sub_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            }
            if (param.containsKey("sub_old_items")) {
                sub_items = (ArrayList) param.get("sub_old_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            } else {
                this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD);
            }
            if (param.containsKey("sub_child_items")) {
                sub_items = (ArrayList) param.get("sub_child_items");
                this.saveDeclareSub(sub_items, id, guidkey);
            } else {
                this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_CHILD);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //保存专项申报数据出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
        }
        return "success";
    }

    /**
     * 变更记录
     */

    @Override
    public String changeZXDeclare(String id, UserView userView) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from declare_infor where id=? and guidkey=? ");
        ArrayList list = new ArrayList();
        HashMap map = new HashMap();
        list.add(Integer.parseInt(PubFunc.decrypt(id)));
        String guidkey = this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100());
        list.add(guidkey);
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo declare_vo = new RecordVo("declare_infor");
        IDGenerator idGenerator = new IDGenerator(2, this.conn);
        int declare_id = Integer.parseInt(idGenerator.getId("declare_infor.id"));
        RowSet rs = null;
        String declare_type = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                declare_vo.setInt("parent_id", rs.getInt("id"));
                declare_vo.setString("guidkey", rs.getString("guidkey"));
                declare_type = rs.getString("declare_type");
                declare_vo.setString("declare_type", declare_type);
                declare_vo.setDate("create_date", sdf.format(new Date()));
                declare_vo.setString("deduct_type", rs.getString("deduct_type"));
                declare_vo.setDouble("deduct_money", rs.getBigDecimal("deduct_money").doubleValue());
                declare_vo.setString("approve_state", C_APPROVE_STATE_DRAFT);
                if (Sql_switcher.searchDbServer() == 1) {
                    declare_vo.setDate("start_date", rs.getString("start_date"));
                    declare_vo.setDate("end_date", rs.getString("end_date"));
                } else if (Sql_switcher.searchDbServer() == 2) {
                    declare_vo.setDate("start_date", rs.getDate("start_date"));
                    declare_vo.setDate("end_date", rs.getDate("end_date"));
                }
                declare_vo.setString("description", rs.getString("description"));
                declare_vo.setString("approve_desc", rs.getString("approve_desc"));
                declare_vo.setString("cuntin_edu_type", rs.getString("cuntin_edu_type"));
                declare_vo.setString("house_address", rs.getString("house_address"));
                declare_vo.setString("loan_self_flag", rs.getString("loan_self_flag"));
                declare_vo.setString("house_type", rs.getString("house_type"));
                declare_vo.setString("house_number", rs.getString("house_number"));
                declare_vo.setString("loan_flat", rs.getString("loan_flat"));
                declare_vo.setString("rent_city_type", rs.getString("rent_city_type"));
                declare_vo.setInt("child_apportion", rs.getInt("child_apportion"));
                declare_vo.setString("apportion_type", rs.getString("apportion_type"));
                declare_vo.setString("fileid", rs.getString("fileid"));
            }

            declare_vo.setInt("id", declare_id);
            // 复制一条新的 起草 记录
            if (dao.addValueObject(declare_vo) <= 0)
                return "fail`" + ResourceFactory.getProperty("gz.zxdeclare.error.changeDeclareMsg");

            sql.setLength(0);
            list.clear();
            //原来记录 改成 已归档 结束时间未当前时间
            sql.append("update declare_infor set approve_state=? ");
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" , end_date =  CONVERT(varchar(100), GETDATE(), 23) ");
            } else if (Sql_switcher.searchDbServer() == 2) {
                sql.append(" , end_date = to_date(TO_CHAR(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') ");
            }
            sql.append(" where id=? and guidkey = ?");
            list.add(C_APPROVE_STATE_FILED);
            list.add(Integer.parseInt(PubFunc.decrypt(id)));
            list.add(guidkey);
            if (dao.update(sql.toString(), list) <= 0) {
                return "fail`" + ResourceFactory.getProperty("gz.zxdeclare.error.DeclareArchiveMsg");
            }

            //专项申报子表 数据 也要负责一份
            sql.setLength(0);
            list.clear();
            sql.append("select * from declare_sub where declare_id = ? ");
            list.add(Integer.parseInt(PubFunc.decrypt(id)));
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                RecordVo sub_vo = new RecordVo("declare_sub");
                int maxSub_id = this.getSubItemMaxId();
                sub_vo.setInt("id", maxSub_id + 1);
                sub_vo.setInt("declare_id", declare_id);
                sub_vo.setString("relation", rs.getString("relation"));
                sub_vo.setString("member_name", rs.getString("member_name"));
                sub_vo.setString("relation_type", rs.getString("relation_type"));
                sub_vo.setDate("birthday", rs.getDate("birthday"));
                sub_vo.setString("nationality", rs.getString("nationality"));
                sub_vo.setString("id_type", rs.getString("id_type"));
                sub_vo.setString("id_number", rs.getString("id_number"));
                sub_vo.setString("edu_level", rs.getString("edu_level"));
                sub_vo.setString("edu_institution", rs.getString("edu_institution"));
                sub_vo.setDate("start_date", rs.getDate("start_date"));
                sub_vo.setDate("end_date", rs.getDate("end_date"));
                sub_vo.setDate("edu_stop_date", rs.getDate("edu_stop_date"));
                sub_vo.setString("edu_nationality", rs.getString("edu_nationality"));
                sub_vo.setString("deduct_proportion", rs.getString("deduct_proportion"));
                sub_vo.setString("jx_edu_level", rs.getString("jx_edu_level"));
                sub_vo.setString("post_type", rs.getString("post_type"));
                sub_vo.setString("post_certificate_name", rs.getString("post_certificate_name"));
                sub_vo.setString("post_certificate_number", rs.getString("post_certificate_number"));
                sub_vo.setString("post_certificate_org", rs.getString("post_certificate_org"));
                sub_vo.setString("loan_bank", rs.getString("loan_bank"));
                sub_vo.setString("loan_contract_no", rs.getString("loan_contract_no"));
                sub_vo.setString("loan_type", rs.getString("loan_type"));
                sub_vo.setString("loan_alloted_time", rs.getString("loan_alloted_time"));
                sub_vo.setString("rent_house_province", rs.getString("rent_house_province"));
                sub_vo.setString("rent_house_city", rs.getString("rent_house_city"));
                sub_vo.setString("rent_house_type", rs.getString("rent_house_type"));
                sub_vo.setString("rent_house_name", rs.getString("rent_house_name"));
                sub_vo.setString("rent_house_id_type", rs.getString("rent_house_id_type"));
                sub_vo.setString("rent_house_id_number", rs.getString("rent_house_id_number"));
                sub_vo.setString("rent_house_address", rs.getString("rent_house_address"));
                sub_vo.setString("rent_house_no", rs.getString("rent_house_no"));
                sub_vo.setString("guidkey", guidkey);
                if (Sql_switcher.searchDbServer() == 1)
                    sub_vo.setDouble("deduct_money", rs.getDouble("deduct_money"));
                else if (Sql_switcher.searchDbServer() == 2)
                    sub_vo.setDouble("deduct_money", rs.getBigDecimal("deduct_money").doubleValue());
                dao.addValueObject(sub_vo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            //变更生成新的记录出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.changeNewDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.changeNewDeclareMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return "success`" + PubFunc.encrypt(String.valueOf(declare_id));
    }

    /**
     * 归档记录
     */

    @Override
    public String revokeZXDeclare(String id, UserView userView) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("update declare_infor set approve_state=?,");
        if (Sql_switcher.searchDbServer() == 1)
            sql.append("end_date= CONVERT(varchar(100), GETDATE(), 23) ");
        else if (Sql_switcher.searchDbServer() == 2)
            sql.append("end_date=to_date(TO_CHAR(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') ");

        sql.append(" where id = ? and guidkey=? ");

        ArrayList list = new ArrayList();
        list.add(C_APPROVE_STATE_FILED);
        int declare_id = Integer.parseInt(PubFunc.decrypt(id));
        list.add(declare_id);
        list.add(this.getGuidkey(userView.getDbname().toUpperCase(), userView.getA0100()));
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            if (dao.update(sql.toString(), list) > 0)
                return "success";
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //撤回操作出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.revokeDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.revokeDeclareMsg"));
        }
        return "fail`" + ResourceFactory.getProperty("gz.zxdeclare.error.revokeDeclareFailMsg");
    }


    @Override
    public void revokeZXDeclare() throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("update declare_infor set approve_state=? ");
        if (Sql_switcher.searchDbServer() == 1)//sql server
            sql.append("where end_date < getDate() and approve_state=?");
        else if (Sql_switcher.searchDbServer() == 2)
            sql.append("where end_date < sysdate and approve_state=?");
        ArrayList list = new ArrayList();
        list.add(C_APPROVE_STATE_FILED);
        list.add(C_APPROVE_STATE_ADOPT);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(sql.toString(), list);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.changeDeclareArchiveMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.changeDeclareArchiveMsg"));
        }
    }


    /**
     * 根据输入的内容生成查询条件
     *
     * @param valueList 输入的内容集合
     * @return sql语句
     */

    @Override
    public String getSqlCondition(List<String> valueList) throws GeneralException {
        StringBuffer buf = new StringBuffer("");
        try {
            for (int i = 0; i < valueList.size(); i++) {
                String queryVal = valueList.get(i);
                queryVal = SafeCode.decode(queryVal);
                if (i == 0) {
                    buf.append(" and ");
                    buf.append("(myGridData.a0101 like ");
                    buf.append("'%" + queryVal + "%'");
                } else {
                    buf.append(" or ");
                    buf.append(" myGridData.a0101 like ");
                    buf.append("'%" + queryVal + "%'");
                }
            }
            if (valueList.size() > 0) {
                buf.append(")");//组装成一个大条件
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclate.error.getDeclareSql"));
        }
        return buf.toString();
    }


    /**
     * 专项申报类型操作数据
     *
     * @return 操作数据列表
     */
    private ArrayList<CommonData> getDeclraTypeOperationData() {
        ArrayList<CommonData> declraTypeData = new ArrayList<CommonData>();
        CommonData commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeChildEdu"));//子女教育
        commonData.setDataValue("01");
        declraTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeContinuEdu"));//继续教育
        commonData.setDataValue("02");
        declraTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeHouseRent"));//住房租金
        commonData.setDataValue("03");
        declraTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeInterestExpense"));//房贷利息
        commonData.setDataValue("04");
        declraTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeIllnessMedicalcare"));//大病医疗
        commonData.setDataValue("05");
        declraTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.zxDeclareTypeSupportElderly"));//赡养老人
        commonData.setDataValue("06");
        declraTypeData.add(commonData);

        return declraTypeData;
    }

    /**
     * 获取审批状态操作类型
     *
     * @return 审批状态操作类型列表
     */
    private ArrayList<CommonData> getApproveStateOperationData() {
        ArrayList<CommonData> approveStateData = new ArrayList<CommonData>();
        CommonData commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.approveStateInaudit"));//审核中
        commonData.setDataValue("02");
        approveStateData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.approveStateAdopt"));
        commonData.setDataValue("03");
        approveStateData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.approveStateNotPass"));//未通过
        commonData.setDataValue("04");
        approveStateData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.approveStateFiled"));//已归档
        commonData.setDataValue("05");
        approveStateData.add(commonData);

        return approveStateData;
    }

    private ArrayList<CommonData> getdeductTypeOperationData() {
        ArrayList<CommonData> ductTypeData = new ArrayList<CommonData>();
        CommonData commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.ductTypeMonth"));//按月
        commonData.setDataValue("01");
        ductTypeData.add(commonData);

        commonData = new CommonData();
        commonData.setDataName(ResourceFactory.getProperty("gz.label.ductTypeYear"));//按年
        commonData.setDataValue("02");
        ductTypeData.add(commonData);
        return ductTypeData;
    }

    /**
     * 获取专项申报id 对应的 子表记录
     *
     * @param declare_id 申报id
     * @return
     * @throws GeneralException
     */
    private ArrayList<HashMap> getDeclareSub(int declare_id) throws GeneralException {
        ArrayList subList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select * from declare_sub where declare_id=?");
        ArrayList list = new ArrayList();
        list.add(declare_id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                HashMap map = new HashMap();
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    if ("nationality".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AD", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("nationality_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("id_type".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AC", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("id_type_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if ("edu_nationality".equalsIgnoreCase(data.getColumnName(i).toLowerCase())) {
                        CodeItem codeItem = AdminCode.getCode("AD", rs.getString(data.getColumnName(i).toLowerCase()));
                        map.put("edu_nationality_desc", codeItem == null ? "" : codeItem.getCodename());
                    }
                    if (Types.DATE == data.getColumnType(i) || Types.TIMESTAMP == data.getColumnType(i)) {
                        String date = rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : sdf.format(rs.getObject(data.getColumnName(i).toLowerCase()));
                        if ("9999-12-31".equalsIgnoreCase(date))
                            date = "";
                        map.put(data.getColumnName(i).toLowerCase(), date);
                    } else {
                        map.put(data.getColumnName(i).toLowerCase(), rs.getObject(data.getColumnName(i).toLowerCase()) == null ? "" : rs.getObject(data.getColumnName(i).toLowerCase()));
                    }
                }
                subList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取专项申报子表数据
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getDeclareSubDataMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getDeclareSubDataMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return subList;
    }

    /**
     * 获取人员唯一标识 guidkey
     *
     * @param nbase 人员库
     * @param A0100 人员编号
     * @return 人员唯一标识
     * @throws GeneralException
     */
    private String getGuidkey(String nbase, String A0100) throws GeneralException {
        String guidkey = null;
        ArrayList list = new ArrayList();
        String sql = "select guidkey from " + nbase + "A01 where A0100=?";
        list.add(A0100);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql, list);
            if (rs.next())
                guidkey = rs.getString("guidkey");
        } catch (SQLException e) {
            e.printStackTrace();
            //获取guidkey出错 获取人员唯一标识出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getA01GuidkeyMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getA01GuidkeyMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return guidkey;
    }


    private HashMap getA01Info(String guidkey) throws GeneralException {
        HashMap map = new HashMap();
        ArrayList dbList = ConstantParamter.getLoginDbNameList();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        for (int i = 0; i < dbList.size(); i++) {
            String dbname = (String) dbList.get(i);
            sql.append("select B0110,E0122,E01A1,A0101 from " + dbname + "A01 where guidkey=? ");
            sql.append(" union all ");
            list.add(guidkey);
        }
        if (dbList.size() < 0)
            return null;
        sql.setLength(sql.length() - 10);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                String B0110 = rs.getString("B0110");
                CodeItem b0110Item = AdminCode.getCode("UN", B0110);
                String E0122 = rs.getString("E0122");
                CodeItem e0122Item = AdminCode.getCode("UM", E0122);
                String E01A1 = rs.getString("E01A1");
                CodeItem e01A1Item = AdminCode.getCode("@K", E01A1);
                String A0101 = rs.getString("A0101");
                map.put("B0110", b0110Item == null ? "" : b0110Item.getCodename());
                map.put("E0122", e0122Item == null ? "" : e0122Item.getCodename());
                map.put("E01A1", e01A1Item == null ? "" : e01A1Item.getCodename());
                map.put("A0101", A0101);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员信息出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getA01InfoMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getA01InfoMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }


    /**
     * 获取上传证明所在路径
     *
     * @param guidkey      人员唯一标识
     * @param declare_type 专项申报类型
     * @param id           申报id
     * @return
     */
    private String getFilePath(String guidkey, String declare_type, int id) throws GeneralException {
        String filePath = "";
        try {
            VfsParam vfsParam = VFSUtil.getParam();
            String path = vfsParam.getPath() + "/multimedia/mobile/zxdeclare";
            filePath = path + "/" + guidkey + "/" + declare_type.substring(0, 2) + "/" + id;
            filePath = filePath.replace("//", "/").replace("\\", "/");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("");
        }
        return filePath;
    }

    /**
     * 删除专项申报子表记录
     *
     * @param sub_ids    保存记录的子表id
     * @param declare_id 申报表id号
     * @throws GeneralException
     */
    private void deleteSubItems(ArrayList sub_ids, int declare_id) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("delete declare_sub where declare_id=? ");
        list.add(declare_id);
        if (sub_ids.size() > 0) {
            sql.append(" and id not in(");
            for (int i = 0; i < sub_ids.size(); i++) {
                int sub_id = (Integer) sub_ids.get(i);
                sql.append("?,");
                list.add(sub_id);
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.delete(sql.toString(), list);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //删除专项申报子表记录出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.deleteDeclareSubMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.deleteDeclareSubMsg"));
        }
    }

    /**
     * 删除专项申报子表记录 赡养人或被赡养人记录
     *
     * @param sub_ids    保存记录的子表id
     * @param declare_id 申报表id号
     * @throws GeneralException
     */
    private void deleteSubItems(ArrayList sub_ids, int declare_id, String relation_type) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("delete declare_sub where declare_id=? and relation_type=?");
        list.add(declare_id);
        list.add(relation_type);
        if (sub_ids.size() > 0) {
            sql.append(" and id not in(");
            for (int i = 0; i < sub_ids.size(); i++) {
                int sub_id = (Integer) sub_ids.get(i);
                sql.append("?,");
                list.add(sub_id);
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.delete(sql.toString(), list);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //删除专项申报子表记录出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.deleteDeclareSubMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.deleteDeclareSubMsg"));
        }
    }

    /**
     * 组装专项申报子集 保存数据
     *
     * @param subMap     单条子集记录数据
     * @param declare_id 申报id
     * @return
     */
    private RecordVo setSubItem(HashMap subMap, int declare_id, String guidkey) {
        RecordVo vo = new RecordVo("declare_sub");
        String id = subMap.containsKey("sub_id") ? (String) subMap.get("sub_id") : null;
        String relation = subMap.containsKey("relation") ? (String) subMap.get("relation") : ""; //与本人关系
        String member_name = subMap.containsKey("member_name") ? (String) subMap.get("member_name") : "";//成员姓名
        String relation_type = subMap.containsKey("relation_type") ? (String) subMap.get("relation_type") : "";//赡养关系类型
        String birthday = subMap.containsKey("birthday") ? (String) subMap.get("birthday") : "";//出生日期
        String nationality = subMap.containsKey("nationality") ? (String) subMap.get("nationality") : "";//国籍
        String id_type = subMap.containsKey("id_type") ? (String) subMap.get("id_type") : "";//证件类别
        String id_number = subMap.containsKey("id_number") ? (String) subMap.get("id_number") : "";//证件号码
        String edu_level = subMap.containsKey("edu_level") ? (String) subMap.get("edu_level") : "";//当前受教育阶段
        String edu_institution = subMap.containsKey("edu_institution") ? (String) subMap.get("edu_institution") : "";//当前就读学校
        String start_date = subMap.containsKey("start_date") ? (String) subMap.get("start_date") : "";//当前受教育阶段起始时间
        String end_date = subMap.containsKey("end_date") ? (String) subMap.get("end_date") : "";//当前受教育阶段结束时间
        String edu_stop_date = subMap.containsKey("edu_stop_date") ? (String) subMap.get("edu_stop_date") : "";//教育终止时间
        String edu_nationality = subMap.containsKey("edu_nationality") ? (String) subMap.get("edu_nationality") : "";//当前就读国家
        String deduct_proportion = subMap.containsKey("deduct_proportion") ? (String) subMap.get("deduct_proportion") : "";//本人扣除比例
        String jx_edu_level = subMap.containsKey("jx_edu_level") ? (String) subMap.get("jx_edu_level") : "";//学历继续教育类型
        String post_type = subMap.containsKey("post_type") ? (String) subMap.get("post_type") : "";//职业继续教育类型
        String post_certificate_name = subMap.containsKey("post_certificate_name") ? (String) subMap.get("post_certificate_name") : "";//证书名称
        String post_certificate_number = subMap.containsKey("post_certificate_number") ? (String) subMap.get("post_certificate_number") : "";//证书编号
        String post_certificate_org = subMap.containsKey("post_certificate_org") ? (String) subMap.get("post_certificate_org") : "";//发证机关
        String loan_bank = subMap.containsKey("loan_bank") ? (String) subMap.get("loan_bank") : "";
        String loan_contract_no = subMap.containsKey("loan_contract_no") ? (String) subMap.get("loan_contract_no") : "";
        String loan_type = subMap.containsKey("loan_type") ? (String) subMap.get("loan_type") : "";
        String loan_alloted_time = subMap.containsKey("loan_alloted_time") ? (String) subMap.get("loan_alloted_time") : "";
//		String rent_city_type= subMap.containsKey("rent_city_type")?(String)subMap.get("rent_city_type"):"";
        String rent_house_province = subMap.containsKey("rent_house_province") ? (String) subMap.get("rent_house_province") : "";
        String rent_house_city = subMap.containsKey("rent_house_city") ? (String) subMap.get("rent_house_city") : "";
        String rent_house_type = subMap.containsKey("rent_house_type") ? (String) subMap.get("rent_house_type") : "";
        String rent_house_name = subMap.containsKey("rent_house_name") ? (String) subMap.get("rent_house_name") : "";
        String rent_house_id_type = subMap.containsKey("rent_house_id_type") ? (String) subMap.get("rent_house_id_type") : "";
        String rent_house_id_number = subMap.containsKey("rent_house_id_number") ? (String) subMap.get("rent_house_id_number") : "";
        String rent_house_address = subMap.containsKey("rent_house_address") ? (String) subMap.get("rent_house_address") : "";
        String rent_house_no = subMap.containsKey("rent_house_no") ? (String) subMap.get("rent_house_no") : "";

        String deduct_money = subMap.containsKey("deduct_money") ? (String) subMap.get("deduct_money") : "0";

        if (StringUtils.isNotBlank(edu_stop_date)) {
            end_date = edu_stop_date;
        }
        vo.setInt("id", (StringUtils.isNotBlank(id) ? Integer.parseInt(PubFunc.decrypt(id)) : 0));
        vo.setInt("declare_id", declare_id);
        vo.setString("relation", relation);
        vo.setString("member_name", member_name);
        vo.setString("relation_type", relation_type);
        vo.setDate("birthday", birthday);
        vo.setString("nationality", nationality);
        vo.setString("id_type", id_type);
        vo.setString("id_number", id_number);
        vo.setString("edu_level", edu_level);
        vo.setString("edu_institution", edu_institution);
        if (start_date.split("-").length < 3 && !StringUtils.isEmpty(start_date)) {
            start_date = start_date + "-01";
        }
        if (end_date.split("-").length < 3 && !StringUtils.isEmpty(end_date)) {
            end_date = end_date + "-31";
        }
        if (edu_stop_date.split("-").length < 3 && !StringUtils.isEmpty(edu_stop_date)) {
            edu_stop_date = edu_stop_date + "-31";
        }
        vo.setDate("start_date", start_date);
        vo.setDate("end_date", end_date);
        vo.setDate("edu_stop_date", edu_stop_date);
        vo.setString("edu_nationality", edu_nationality);
        vo.setString("deduct_proportion", deduct_proportion);
        vo.setString("jx_edu_level", jx_edu_level);
        vo.setString("post_type", post_type);
        vo.setString("post_certificate_name", post_certificate_name);
        vo.setString("post_certificate_number", post_certificate_number);
        vo.setString("post_certificate_org", post_certificate_org);
        vo.setString("loan_bank", loan_bank);
        vo.setString("loan_contract_no", loan_contract_no);
        vo.setString("loan_type", loan_type);
        vo.setString("loan_alloted_time", loan_alloted_time);
//		vo.setString("rent_city_type", rent_city_type);
        vo.setString("rent_house_province", rent_house_province);
        vo.setString("rent_house_city", rent_house_city);
        vo.setString("rent_house_type", rent_house_type);
        vo.setString("rent_house_name", rent_house_name);
        vo.setString("rent_house_id_type", rent_house_id_type);
        vo.setString("rent_house_id_number", rent_house_id_number);
        vo.setString("rent_house_address", rent_house_address);
        vo.setString("rent_house_no", rent_house_no);
        if (StringUtils.isBlank(deduct_money)) {
            deduct_money = "0";
        }
        vo.setDouble("deduct_money", Double.parseDouble(deduct_money));
        vo.setString("guidkey", guidkey);
        return vo;
    }

    /**
     * 获取专项申报子表最大id号
     *
     * @return
     * @throws GeneralException
     */
    private int getSubItemMaxId() throws GeneralException {
        String sql = "select Max(id) as max from declare_sub";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                Object max = rs.getObject("max");
                if (max == null || "null".equals(max))
                    return 1;
                if (Sql_switcher.searchDbServer() == 1)
                    return (Integer) max;
                else if (Sql_switcher.searchDbServer() == 2)
                    return ((BigDecimal) max).intValue();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //获取专项申报子表最大id号出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getMaxDeclareSubMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getMaxDeclareSubMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return 1;
    }

    /**
     * 保存申报子表数据
     *
     * @param sub_items 子表数据集合
     * @param id        申报id
     * @throws GeneralException
     */
    private void saveDeclareSub(ArrayList sub_items, String id, String guidkey) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList sub_ids = new ArrayList();
        String relation_type = "";
        try {
            if (id != null && id.trim().length() > 0) {
                for (int i = 0; i < sub_items.size(); i++) {
                    HashMap map = null;
                    if (sub_items.get(i) instanceof HashMap)
                        map = (HashMap) sub_items.get(i);
                    else
                        map = (HashMap) PubFunc.DynaBean2Map((MorphDynaBean) sub_items.get(i));

                    if (map.containsKey("sub_id"))
                        sub_ids.add(Integer.parseInt(PubFunc.decrypt((String) map.get("sub_id"))));
                    if (map.containsKey("relation_type"))
                        relation_type = (String) map.get("relation_type");
                }
                if (StringUtils.isBlank(relation_type))
                    this.deleteSubItems(sub_ids, Integer.parseInt(id));
                else
                    this.deleteSubItems(sub_ids, Integer.parseInt(id), relation_type);
                for (int i = 0; i < sub_items.size(); i++) {
                    HashMap map = null;
                    if (sub_items.get(i) instanceof HashMap)
                        map = (HashMap) sub_items.get(i);
                    else
                        map = (HashMap) PubFunc.DynaBean2Map((MorphDynaBean) sub_items.get(i));
                    if (map.containsKey("sub_id")) {
                        RecordVo sub_vo = this.setSubItem(map, Integer.parseInt(id), guidkey);
                        dao.updateValueObject(sub_vo);
                    } else {
                        RecordVo sub_vo = this.setSubItem(map, Integer.parseInt(id), guidkey);
                        int maxSub_id = this.getSubItemMaxId();
                        sub_vo.setInt("id", maxSub_id + 1);
                        dao.addValueObject(sub_vo);
                    }
                }
            } else {
                for (int i = 0; i < sub_items.size(); i++) {
                    HashMap map = (HashMap) PubFunc.DynaBean2Map((MorphDynaBean) sub_items.get(i));
                    RecordVo sub_vo = this.setSubItem(map, Integer.parseInt(id), guidkey);
                    int maxSub_id = this.getSubItemMaxId();
                    sub_vo.setInt("id", maxSub_id + 1);
                    dao.addValueObject(sub_vo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.saveSubDataError"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.saveSubDataError"));
        }
    }


    @Override
    public String getImportPath(UserView userView) throws GeneralException {
        String filePath = "";
        ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
        String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath").replace("\\", File.separator).replace("/", File.separator);
        String path = RootDir + "/multimedia/mobile/zxdeclare";
        filePath = path + "/upload/" + userView.getUserName();
        filePath = filePath.replace("//", "/").replace("\\", "/");
        return filePath;
    }


    @Override
    public Map importZXDeclareData(String fileid, UserView userView) throws GeneralException {
        Map paramMap = new HashMap();
        String rzFileName = userView.getUserName() + ResourceFactory.getProperty("gz.zxdeclare.error.importTaxDataReturnResult");
        String path = System.getProperty("java.io.tmpdir") + File.separator + rzFileName;
        String filePath = System.getProperty("java.io.tmpdir") +File.separator +userView.getUserName()+"_zxdeclare.zip";
        StringBuffer errorStr = new StringBuffer();
        int errorCount = 0;
        //StringBuffer successStr = new StringBuffer();
        int successCount = 0;
        //String successFlag = "success";
        ArrayList list = new ArrayList();
        StringBuffer msg = new StringBuffer();
        String returnMsg = "";
        ArrayList dataList = new ArrayList();
        RowSet rs = null;
        ZipFile zf = null;
        Enumeration enu;
        ArrayList dbList = userView.getPrivDbList();
        //获取到招聘人员库
        RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
        String zp_dbName = "";
        if (vo != null){
            zp_dbName = vo.getString("str_value");
        }
        //如果设置了招聘人员库  将人员库移除
        if(StringUtils.isNotEmpty(zp_dbName)){
            dbList.remove(zp_dbName);
        }
        Sys_Oth_Parameter sysBo = new Sys_Oth_Parameter(this.conn);
        String IDCardField = sysBo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
        if(IDCardField == null || IDCardField.trim().length() == 0)
        	throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getIDNumberField"));
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        for (int i = 0; i < dbList.size(); i++) {
            sql.append("select A0101 as name," + IDCardField + " as idnumber,guidkey from ")
                    .append(" " + dbList.get(i) + "A01 ")
                    .append(" union all ");
        }
        if (sql.length() > 0)
            sql.setLength(sql.length() - 10);
        HashMap infoHM;
        try {
            rs = dao.search(sql.toString());
            infoHM = new HashMap();
            while (rs.next()) {
                String name = rs.getString("name");
                String idnumber = rs.getString("idnumber");
                String guidkey = rs.getString("guidkey");
                infoHM.put(name + "`" + idnumber, guidkey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员信息出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.getA01InfoMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getA01InfoMsg"));
        }
        HashMap sheetByGuidkey = new HashMap();
        ArrayList guidkeyList = new ArrayList();
        OutputStream outputStream = null;
        try {
            InputStream inputStream = VfsService.getFile(fileid);
            outputStream = new FileOutputStream(filePath);
            IOUtils.copy(inputStream,outputStream);
            zf = new ZipFile(filePath);
            enu = (Enumeration) zf.getEntries();
            while (enu.hasMoreElements()) {
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream is = zf.getInputStream(zipElement);
                String fileName = zipElement.getName();
                String[] filetype = fileName.split("\\.");
                if (fileName != null && "xls".equalsIgnoreCase(filetype[filetype.length - 1])) {//是否为xls格式文件
                    Workbook wb = new HSSFWorkbook(is);
                    Sheet sheet = wb.getSheetAt(0);
                    String name = this.getCellValue(sheet.getRow(3).getCell(1)).replaceAll("\\s*|\t|\r|\n", "");//纳税人姓名
                    String idnumber = getCellValue(sheet.getRow(5).getCell(2)).replaceAll("\\s*|\t|\r|\n", "");
                    String guidkey = (String) infoHM.get(name + "`" + idnumber);
                    if (!infoHM.containsKey(name + "`" + idnumber)) {
                        //successFlag = "false";
                        //log.error("The failure person name:"+name+"idNumber:"+idnumber);
                        errorCount++;
                        errorStr.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.failReasonNameNotExist") + name + ResourceFactory.getProperty("gz.zxdeclare.error.andIdentityCardNum") + idnumber + ResourceFactory.getProperty("gz.zxdeclare.error.thePerson")).append(System.getProperty("line.separator"));
                        continue;
                    }
                    Map param = new HashMap();
                    param.put("sheet", sheet);
                    param.put("filename", fileName);
                    param.put("name", name);
                    param.put("idnumber", idnumber);
                    sheetByGuidkey.put(guidkey, param);
                    guidkeyList.add(guidkey);
                    //清空导入人员的 专项附加申报数据   除归档05状态的
                    //try{
                    //    returnMsg = this.saveImportData(sheet, guidkey);
                    //}catch(Exception e){
                    //    errorCount++;
                    //    errorStr.append("失败原因：解析"+fileName+"的excel文件出错\n");
                    //    continue;
                    //}
                    //if(!returnMsg.startsWith("success")){
                    //    errorCount++;
                    //    errorStr.append("失败原因：人员库中姓名为：\"+name+\"和身份证号：\"+idnumber+\"的人员,保存专项附加数据时出错。\n\t错误如下：\n");
                    //}
                    //successCount++;
                }
            }
            ///清空导入人员的 专项附加申报数据 审核通过状态数据、
            StringBuffer deleteInforSql = new StringBuffer();
            StringBuffer deleteSubSql = new StringBuffer();
            deleteInforSql.append("delete from declare_infor where guidkey in(");
            if (Sql_switcher.searchDbServer() == 1) {//sqlserver
                deleteSubSql.append("delete declare_sub from (select id from declare_infor where approve_state='03' and guidkey in (");
            } else if (Sql_switcher.searchDbServer() == 2) {
                deleteSubSql.append("delete  from declare_sub where declare_id=(select id from declare_infor where approve_state='03' and guidkey in (");
            }
            for (int i = 0; i < guidkeyList.size(); i++) {
                deleteInforSql.append("'").append(guidkeyList.get(i)).append("'");
                deleteSubSql.append("'").append(guidkeyList.get(i)).append("'");
                if (i < guidkeyList.size() - 1) {
                    deleteInforSql.append(",");
                    deleteSubSql.append(",");
                }
            }
            deleteInforSql.append(") and approve_state='03'");
            if (Sql_switcher.searchDbServer() == 1) {
                deleteSubSql.append(")) infor where infor.id = declare_sub.declare_id ");
            } else if (Sql_switcher.searchDbServer() == 2) {
                deleteSubSql.append(")and declare_infor.id = declare_sub.declare_id )");
            }
            if (guidkeyList.size() > 0) {
                dao.update(deleteSubSql.toString());
                dao.update(deleteInforSql.toString());
            }
            //导入
            for (int i = 0; i < guidkeyList.size(); i++) {
                String filename = "";
                Map param = null;
                try {
                    String guidkey = (String) guidkeyList.get(i);
                    param = (Map) sheetByGuidkey.get(guidkey);
                    Sheet sheet = (Sheet) param.get("sheet");
                    filename = (String) param.get("filename");
                    returnMsg = this.saveImportData(sheet, guidkey);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount++;
                    errorStr.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.failReasonAnalysis") + filename + ResourceFactory.getProperty("gz.zxdeclare.error.excelFileError")).append(System.getProperty("line.separator"));
                    continue;
                }
                if (!returnMsg.startsWith("success")) {
                    errorCount++;
                    errorStr.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.failReasonNbaseName") + param.get("name") + ResourceFactory.getProperty("gz.zxdeclare.error.andIdentityCardNum") + param.get("idnumber") + ResourceFactory.getProperty("gz.zxdeclare.error.thePersonSaveDeclareDataError") + System.getProperty("line.separator") + "\t" + ResourceFactory.getProperty("gz.zxdeclare.error.errorForEach")).append(System.getProperty("line.separator"));
                    errorStr.append(returnMsg);
                } else {
                    successCount++;
                }
            }
            File file = new File(filePath);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
            File file = new File(filePath);
            file.delete();
            //解析导入模板数据出错！
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelDataMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelDataMsg"));
        } catch (Exception e) {
            File file = new File(filePath);
            file.delete();
            deleteZXDeclare(list);
            //解析导入模板数据出错！
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelDataMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelDataMsg"));
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeIoResource(outputStream);
            StringBuffer textStr = new StringBuffer();
            textStr.append(ResourceFactory.getProperty("gz.zxdeclare.label.taxDataForEach") + "\r\n");
            textStr.append(ResourceFactory.getProperty("gz.zxdeclare.label.firstImportSuccess") + successCount + ResourceFactory.getProperty("gz.zxdeclare.label.itemZxDeclareMsg") + "\r\n");
            if (errorCount > 0) {
                textStr.append(ResourceFactory.getProperty("gz.zxdeclare.label.secondImportFail") + errorCount + ResourceFactory.getProperty("gz.zxdeclare.label.itemZxDeclareMsg") + "\r\n");
                textStr.append(errorStr.toString());
            }
            OutputStreamWriter osw = null;
            FileOutputStream out = null;
            // write
            try {
            	out = new FileOutputStream(path, false);
                osw = new OutputStreamWriter(out, "utf-8");
                osw.write(textStr.toString());
                osw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(osw);
                PubFunc.closeResource(out);
            }

        }
        paramMap.put("rzFileName", PubFunc.encrypt(rzFileName));
        paramMap.put("errorCount",String.valueOf(errorCount));
        paramMap.put("successCount",String.valueOf(successCount));
        return paramMap;
    }

    /**
     * 导入模板数据写入出错，删除导入成功数据
     *
     * @param list
     * @return
     * @throws GeneralException
     */
    private String deleteZXDeclare(ArrayList list) throws GeneralException {
        String msg = "success";
        StringBuffer sql = new StringBuffer();
        sql.append("delete declare_infor where id in (");
        if (list == null || list.size() == 0)
            return msg;
        for (int i = 0; i < list.size(); i++) {
            sql.append("?,");
        }
        sql.setLength(sql.length() - 1);
        sql.append(")");
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            if (dao.delete(sql.toString(), list) < 0)
                throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.delSuccessImportExcelAnnal"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelData") + ResourceFactory.getProperty("gz.zxdeclare.error.delSuccessImportExcelAnnal"));
        }
        return msg;
    }

    /**
     * 导入模板数据 保存到 专项附加申报表里
     *
     * @param sheet   excel工作簿 对象
     * @param guidkey 人员唯一标识
     * @return
     * @throws GeneralException
     */
    private String saveImportData(Sheet sheet, String guidkey) {
        Map declareTypeControlId = this.getDeclareIdByGuidkey(guidkey);
        StringBuffer msg = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String create_date = sdf.format(new Date());
        //获取首页信息 start
        Cell yearCell = sheet.getRow(2).getCell(5);//扣除年度
        String year = getCellValue(yearCell);//扣除年度
        Cell nameCell = sheet.getRow(3).getCell(1);//纳税人姓名
        String name = getCellValue(nameCell);//纳税人姓名
        Cell idNumberCell = sheet.getRow(5).getCell(2);//身份证号码
        String idNumber = getCellValue(idNumberCell);
        Cell spouseSituationCell = sheet.getRow(6).getCell(6);//配偶情况
        String spouseSituation = getCellValue(spouseSituationCell);
        Cell spouseNameCell = sheet.getRow(7).getCell(2);//配偶姓名
        String spouseName = getCellValue(spouseNameCell);
        Cell spouseIdTypeCell = sheet.getRow(7).getCell(4);//配偶身份证件类型
        String spouseIdType = getCellValue(spouseNameCell);
        Cell spouseIdNumberCell = sheet.getRow(7).getCell(6);//配偶身份证件号码
        String spouseIdNumber = getCellValue(spouseNameCell);
        //获取首页信息 end
        //获取各项专项起始单元格号
        int childEduRowIndex = 8;//子女教育
        int continuEduRowIndex = 14;//继续教育
        int houseRentRowIndex = 28;//住房租金
        int interestExpenseRowIndex = 19;//贷款利息
        int supportElderlyRowIndex = 35;//赡养老人
        int majorIllnessRowIndex = 43;//大病医疗
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i).getCell(0) != null) {
                if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.firstChildEduDeductInfo"))) {
                    childEduRowIndex = i;
                } else if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.secondContinueEduDeductInfo"))) {
                    continuEduRowIndex = i;
                } else if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.thirdloanInterestDeductInfo"))) {
                    interestExpenseRowIndex = i;
                } else if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.forthRentHouseDeductInfo"))) {
                    houseRentRowIndex = i;
                } else if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.fifthSupportElderDeductInfo"))) {
                    supportElderlyRowIndex = i;
                } else if (StringUtils.contains(sheet.getRow(i).getCell(0).getStringCellValue(), ResourceFactory.getProperty("gz.zxdeclare.label.sixthSeriousIllnessDeductInfo"))) {
                    majorIllnessRowIndex = i;
                }
            }
        }
        boolean isDateEffective = true;
        String tempStartDate = "";
        String tempEndDate = "";
        try {
            //获取子女教育信息 start
            HashMap childDateMap = new HashMap();
            int childCount = (continuEduRowIndex - 2 - childEduRowIndex) / 4;
            List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < childCount; i++) {
                Map<String, String> childMap = new HashMap<String, String>();
                Cell childNameCell = sheet.getRow(childEduRowIndex + 2 + i * 4).getCell(2);//子女姓名
                Cell childIdTypeCell = sheet.getRow(childEduRowIndex + 2 + i * 4).getCell(4);//子女身份证件类型
                Cell childIdNumberCell = sheet.getRow(childEduRowIndex + 2 + i * 4).getCell(6);//子女身份证件号码
                Cell childbirthdayCell = sheet.getRow(childEduRowIndex + 3 + i * 4).getCell(2);//子女出生日期
                Cell eduLevelCell = sheet.getRow(childEduRowIndex + 3 + i * 4).getCell(4);//当前受教育阶段
                Cell startDateCell = sheet.getRow(childEduRowIndex + 4 + i * 4).getCell(2);//  当前受教育阶段起始
                Cell endDateCell = sheet.getRow(childEduRowIndex + 4 + i * 4).getCell(4);//  当前受教育阶段结束
                Cell stopDateCell = sheet.getRow(childEduRowIndex + 4 + i * 4).getCell(6);//  子女教育终止时间
                Cell eduNationalityDescCell = sheet.getRow(childEduRowIndex + 5 + i * 4).getCell(2);//就读国家地区
                Cell eduInstitutionCell = sheet.getRow(childEduRowIndex + 5 + i * 4).getCell(4);//  就读学校
                Cell deductProportionCell = sheet.getRow(childEduRowIndex + 5 + i * 4).getCell(6);//本人扣除比例
                String childName = this.getCellValue(childNameCell);
                String childIdNumber = this.getCellValue(childIdNumberCell);
                String childbirthday = this.getCellValue(childbirthdayCell);
                String childIdTypeDesc = this.getCellValue(childIdTypeCell);
                ArrayList idtypeCodeList = AdminCode.getCodeItemList("AC");
                String childIdType = this.getCode(childIdTypeDesc, idtypeCodeList);
                String eduLevel = this.getCellValue(eduLevelCell);
                String startDate = this.getCellValue(startDateCell);
                String stopDate = this.getCellValue(stopDateCell);
                String endDate = this.getCellValue(endDateCell);
                String eduNationalityDesc = this.getCellValue(eduNationalityDescCell);
                ArrayList nationalityCodeList = AdminCode.getCodeItemList("AD");
                String nationality = this.getCode(eduNationalityDesc, nationalityCodeList);
                String eduInstitution = this.getCellValue(eduInstitutionCell);
                String deductProportion = this.getCellValue(deductProportionCell);
                if (StringUtils.equalsIgnoreCase(deductProportion, "1")) {
                    deductProportion = "100%";
                } else if (StringUtils.equalsIgnoreCase(deductProportion, "0.5")) {
                    deductProportion = "50%";
                }
                String deduct_money = "";
                if (StringUtils.contains(deductProportion, "100")) {
                    deduct_money = "1000";
                } else if (StringUtils.contains(deductProportion, "50")) {
                    deduct_money = "500";
                }
                if (StringUtils.isBlank(childName)) //姓名
                    continue;
                if (StringUtils.isBlank(startDate))//受教育起始时间
                    continue;
                if (StringUtils.isBlank(childbirthday))//出生日期
                    continue;
                if (StringUtils.isBlank(nationality))//国家
                    continue;
                if (StringUtils.isBlank(eduLevel))//教育阶段
                    continue;
                if (StringUtils.isBlank(childIdTypeDesc))//证件类型
                    continue;
                if (StringUtils.isBlank(deductProportion)) {//本人扣除比例
                    continue;
                }
                childMap.put("member_name", childName);
                childMap.put("birthday", childbirthday);
                childMap.put("id_type", childIdType);
                childMap.put("id_number", childIdNumber);
                childMap.put("edu_level", levelDescToCode(eduLevel));
                childMap.put("edu_institution", eduInstitution);
                childMap.put("start_date", startDate);
                childMap.put("edu_stop_date", stopDate);
                childMap.put("end_date", endDate);
                childMap.put("edu_nationality", nationality);
                childMap.put("deduct_proportion", deductProportion);
                childMap.put("guidkey", guidkey);
                childMap.put("deduct_money", deduct_money);
                if (startDate.split("-").length < 3) {
                    startDate = startDate + "-01";
                }
                if (endDate.split("-").length < 3 && StringUtils.isNotBlank(endDate)) {
                    endDate = endDate + "-31";
                }
                if (StringUtils.isBlank(tempStartDate)) {
                    tempStartDate = startDate;
                }
                if (StringUtils.isBlank(tempEndDate)) {
                    tempEndDate = endDate;
                }
                if (StringUtils.isNotBlank(tempStartDate) && StringUtils.isNotBlank(startDate)) {
                    try {
                        if (sdf.parse(tempStartDate).getTime() - sdf.parse(startDate).getTime() > 0) {
                            tempStartDate = startDate;
                        }
                        if (StringUtils.isNotBlank(tempEndDate) && StringUtils.isNotBlank(endDate)) {
                            if (sdf.parse(tempEndDate).getTime() - sdf.parse(endDate).getTime() < 0) {
                                tempEndDate = endDate;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                childList.add(childMap);
            }
            if (StringUtils.isNotBlank(tempStartDate)) {
                if (Integer.valueOf(tempStartDate.split("-")[0]).intValue() > Integer.valueOf(year).intValue()) {//如果最早的起始日期的年份大于起始日期
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.childEduNotBelongDeductYear")).append(System.getProperty("line.separator"));
                    isDateEffective = false;
                }
            }
            if (StringUtils.isNotBlank(tempEndDate)) {
                if (Integer.valueOf(tempEndDate.split("-")[0]).intValue() < Integer.valueOf(year).intValue()) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.childEduNotBelongDeductYear")).append(System.getProperty("line.separator"));
                    isDateEffective = false;
                }
            }
            if (childList.size() > 0 && isDateEffective) {
                childDateMap.put("declare_type", C_DECLARE_TYPE_CHILDEDU);
                childDateMap.put("deduct_type", C_DECLARE_TYPE_MONTH);
                childDateMap.put("start_date", year + "-01-01");
                childDateMap.put("end_date", year + "-12-31");
                childDateMap.put("sub_items", childList);
                String id = (String) declareTypeControlId.get("01");
                if (StringUtils.isNotEmpty(id)) {
                    childDateMap.put("id", id);
                }
                try {
                    saveZXDeclare(childDateMap, guidkey);
                } catch (GeneralException e) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.childEdu") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                }

            }
            //获取子女教育信息end
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.presenceDeclareAnnal").replace("{name}", name).replace("{idnumber}", idNumber));
            msg.append(ResourceFactory.getProperty("gz.zxdeclare.error.importExcelSheetChildEduError"));
        }

        HashMap saveDateMap = new HashMap();
        isDateEffective = true;//校验起始日期和结束日期是否合法
        try {
            //获取继续教育信息start
            int continuEduRowDataIndex = continuEduRowIndex + 2;
            String education = getCellValue(sheet.getRow(continuEduRowDataIndex + 1).getCell(0));
            List<Integer> educationRowIndexList = new ArrayList<Integer>();//学历学位继续教育的RowIndex
            List<Integer> continueEducationRowIndexList = new ArrayList<Integer>();//职业资格继续教育的RowIndex
            List<Map<String, String>> continueEducationDataList = new ArrayList<Map<String, String>>();//职业资格继续教育数据集合
            List<Map<String, String>> educationDataList = new ArrayList<Map<String, String>>();//学历学位继续教育数据集合
            for (int i = continuEduRowDataIndex; i < interestExpenseRowIndex; i++) {
                if (getCellValue(sheet.getRow(i).getCell(0)).contains(ResourceFactory.getProperty("gz.zxdeclare.label.schoolContinueEdu"))) {
                    educationRowIndexList.add(i);
                }
                if (getCellValue(sheet.getRow(i).getCell(0)).contains(ResourceFactory.getProperty("gz.zxdeclare.label.professionalQualification") + "\n" + ResourceFactory.getProperty("gz.zxdeclare.label.continueEdu"))) {
                    continueEducationRowIndexList.add(i);
                }
            }
            tempStartDate = "";
            tempEndDate = "";
            HashMap jxEduDateMap = new HashMap();//学历继续教育
            for (int i = 0; i < educationRowIndexList.size(); i++) {
                Map<String, String> educationMap = new HashMap<String, String>();
                Cell start_date_cell = sheet.getRow(educationRowIndexList.get(i)).getCell(2);//当前继续教育起始时间
                Cell end_date_cell = sheet.getRow(educationRowIndexList.get(i)).getCell(4);//当前继续教育结束时间
                Cell jx_edu_level_cell = sheet.getRow(educationRowIndexList.get(i)).getCell(6);//学历继续教育阶段
                String start_date = getCellValue(start_date_cell);
                String end_date = getCellValue(end_date_cell);
                String jx_edu_level_desc = getCellValue(jx_edu_level_cell);

                if (StringUtils.isBlank(start_date)) //起始时间
                    continue;
                if (StringUtils.isBlank(jx_edu_level_desc)) //学历继续教育阶段
                    continue;
                if (StringUtils.isBlank(end_date)) //结束时间
                    continue;
                educationMap.put("start_date", start_date);
                educationMap.put("end_date", end_date);
                educationMap.put("jx_edu_level", jxEdulevelDescToCode(jx_edu_level_desc));
                if (start_date.split("-").length < 3) {
                    start_date = start_date + "-01";
                }
                if (end_date.split("-").length < 3) {
                    end_date = end_date + "-31";
                }
                if (StringUtils.isBlank(tempStartDate)) {
                    tempStartDate = start_date;
                }
                if (StringUtils.isBlank(tempEndDate)) {
                    tempEndDate = end_date;
                }
                if (StringUtils.isNotBlank(tempStartDate) && StringUtils.isNotBlank(start_date)) {
                    try {
                        if (sdf.parse(tempStartDate).getTime() - sdf.parse(start_date).getTime() > 0) {
                            tempStartDate = start_date;
                        }
                        if (sdf.parse(tempEndDate).getTime() - sdf.parse(end_date).getTime() < 0) {
                            tempEndDate = end_date;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                educationDataList.add(educationMap);
            }
            if (StringUtils.isNotBlank(tempStartDate) && StringUtils.isNotBlank(tempEndDate)) {
                if (Integer.valueOf(tempStartDate.split("-")[0]).intValue() > Integer.valueOf(year).intValue()) {//如果最早的起始日期的年份大于起始日期
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.schoolContinueEduNotBelongDeductYear")).append(System.getProperty("line.separator"));
                    isDateEffective = false;
                }
                if (Integer.valueOf(tempEndDate.split("-")[0]).intValue() < Integer.valueOf(year).intValue()) {//如果最早的起始日期的年份大于起始日期
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.schoolContinueEduNotBelongDeductYear")).append(System.getProperty("line.separator"));
                    isDateEffective = false;
                }
            }
            if (educationDataList.size() > 0 && isDateEffective) {
                jxEduDateMap.put("declare_type", C_DECLARE_TYPE_CONTINU_EDU);
                jxEduDateMap.put("deduct_type", C_DECLARE_TYPE_MONTH);
                jxEduDateMap.put("deduct_money", "400");
                jxEduDateMap.put("cuntin_edu_type", C_DECLARE_TYPE_CONTINU_EDU_EDU);
                jxEduDateMap.put("start_date", this.getEffectiveDate(year, tempStartDate, "startDate"));
                jxEduDateMap.put("end_date", this.getEffectiveDate(year, tempEndDate, "endDate"));
                jxEduDateMap.put("sub_items", educationDataList);
                String id = (String) declareTypeControlId.get("0201");
                if (StringUtils.isNotEmpty(id)) {
                    jxEduDateMap.put("id", id);
                }
                try {
                    if (StringUtils.isNotBlank(this.getEffectiveDate(year, tempStartDate, "startDate")) && StringUtils.isNotBlank(this.getEffectiveDate(year, tempEndDate, "endDate"))) {
                        saveZXDeclare(jxEduDateMap, guidkey);
                    } else {
                        msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.schoolContinueEduDateError")).append(System.getProperty("line.separator"));
                    }
                } catch (GeneralException e) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.schoolContiEdu") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                }
            }
            tempStartDate = "";
            tempEndDate = "";
            isDateEffective = true;
            HashMap zyEduDateMap = new HashMap();//职业继续教育
            for (int i = 0; i < continueEducationRowIndexList.size(); i++) {
                Map<String, String> continueEducationMap = new HashMap<String, String>();
                Cell post_type_cell = sheet.getRow(continueEducationRowIndexList.get(i)).getCell(2);//职业继续教育类型
                Cell post_certificate_name_cell = sheet.getRow(continueEducationRowIndexList.get(i)).getCell(6);//证书名称
                Cell post_certificate_number_cell = sheet.getRow(continueEducationRowIndexList.get(i) + 1).getCell(2);//证书编号
                Cell post_certificate_org_cell = sheet.getRow(continueEducationRowIndexList.get(i) + 1).getCell(4);//发证机关
                Cell start_date_cell = sheet.getRow(continueEducationRowIndexList.get(i) + 1).getCell(6);//发证批准日期
                String post_certificate_name = getCellValue(post_certificate_name_cell);
                String post_type_desc = getCellValue(post_type_cell);
                String post_certificate_number = getCellValue(post_certificate_number_cell);
                String post_certificate_org = getCellValue(post_certificate_org_cell);
                String start_date = getCellValue(start_date_cell);
                if (StringUtils.isBlank(post_certificate_name))
                    continue;
                if (StringUtils.isBlank(post_type_desc))
                    continue;
                if (StringUtils.isBlank(post_certificate_number))
                    continue;
                if (StringUtils.isBlank(post_certificate_org))
                    continue;
                if (StringUtils.isBlank(start_date))
                    continue;

                continueEducationMap.put("post_certificate_name", post_certificate_name);
                continueEducationMap.put("post_type", postTypeDescToCode(post_type_desc));
                continueEducationMap.put("post_certificate_number", post_certificate_number);
                continueEducationMap.put("post_certificate_org", post_certificate_org);
                continueEducationMap.put("start_date", start_date);
                if (start_date.split("-").length < 2) {
                    start_date = start_date + "-01";
                }
                if (StringUtils.isBlank(tempStartDate)) {
                    tempStartDate = start_date;
                }
                try {
                    if (sdf.parse(tempStartDate).getTime() - sdf.parse(start_date).getTime() > 0) {
                        tempStartDate = start_date;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                continueEducationDataList.add(continueEducationMap);

            }
            if (StringUtils.isNotBlank(tempStartDate)) {
                if (Integer.valueOf(tempStartDate.split("-")[0]).intValue() != Integer.valueOf(year).intValue()) {//如果最早的起始日期的年份大于起始日期
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.jobContinueEduNotBelongDeductYear")).append(System.getProperty("line.separator"));
                    isDateEffective = false;
                }
            }
            if (continueEducationDataList.size() > 0 && isDateEffective) {
                zyEduDateMap.put("declare_type", C_DECLARE_TYPE_CONTINU_EDU);
                zyEduDateMap.put("deduct_type", C_DECLARE_TYPE_YEAR);
                zyEduDateMap.put("deduct_money", "3600");
                zyEduDateMap.put("cuntin_edu_type", C_DECLARE_TYPE_CONTINU_EDU_PROFESSION);
                zyEduDateMap.put("start_date", this.getEffectiveDate(year, tempStartDate, "startDate"));
                zyEduDateMap.put("end_date", year + "-12-31");
                zyEduDateMap.put("sub_items", continueEducationDataList);
                String id = (String) declareTypeControlId.get("0202");
                if (StringUtils.isNotEmpty(id)) {
                    zyEduDateMap.put("id", id);
                }
                try {
                    saveZXDeclare(zyEduDateMap, guidkey);
                } catch (GeneralException e) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.jobContinueEdu") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                }
            }
            //获取继续教育信息end
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.presenceDeclareAnnal").replace("{name}", name).replace("{idnumber}", idNumber));
            msg.append(ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetJXEduError"));
        }

        Cell rent_house_address_cell = null;
        Cell start_date_cell = null;
        String rent_house_address = "";
        String start_date = "";
        String deduct_money = "";
        try {
            //获取住房贷款利息数据 start
            tempStartDate = "";
            isDateEffective = true;
            String maxStartDate = "";
            boolean isEnd = false;//用于判断该记录是否已经结束   结束true 运行 false
            HashMap interestExpense = new HashMap();
            int interestExpenseRowDataIndex = interestExpenseRowIndex + 2;
            List interestExpenseDataList = new ArrayList();
            rent_house_address_cell = sheet.getRow(interestExpenseRowDataIndex).getCell(2);//住房坐落地址
            Cell house_type_cell = sheet.getRow(interestExpenseRowDataIndex + 1).getCell(3);//房屋证书类型
            Cell house_number_cell = sheet.getRow(interestExpenseRowDataIndex + 1).getCell(6);//房屋证书号码
            Cell loan_self_flag_cell = sheet.getRow(interestExpenseRowDataIndex + 2).getCell(3);//本人是否是借款人
            Cell loan_flat_cell = sheet.getRow(interestExpenseRowDataIndex + 2).getCell(6);//是否婚前各自首套贷款，且婚后分别扣除50%

            Cell loan_contract_no_cell = sheet.getRow(interestExpenseRowDataIndex + 3).getCell(3);//贷款合同号
            Cell loan_alloted_time_cell = sheet.getRow(interestExpenseRowDataIndex + 4).getCell(3);//贷款期限
            start_date_cell = sheet.getRow(interestExpenseRowDataIndex + 4).getCell(6);//首次还款日期

            rent_house_address = getCellValue(rent_house_address_cell);
            String house_type_desc = getCellValue(house_type_cell);
            String house_type = this.houseTypeDescToCode(house_type_desc);
            String house_number = getCellValue(house_number_cell);
            String loan_self_flag_desc = getCellValue(loan_self_flag_cell);
            String loan_self_flag = this.loanSelfFlatDescToCode(loan_self_flag_desc);
            String loan_flat_desc = getCellValue(loan_flat_cell);
            String loan_flat = this.loanFlatDescToCode(loan_flat_desc);
            if (StringUtils.equalsIgnoreCase("1", loan_flat)) {
                deduct_money = "500";
            } else if (StringUtils.equalsIgnoreCase("2", loan_flat)) {
                deduct_money = "1000";
            }
            String loan_contract_no = getCellValue(loan_contract_no_cell);
            String loanAllotedTimeTemp = "";
            String loan_alloted_time = getCellValue(loan_alloted_time_cell);
            loanAllotedTimeTemp = loan_alloted_time;
            start_date = getCellValue(start_date_cell);


            Map loanDataMap = new HashMap();
            String providentFundEndDate = "";//公积金贷款结束日期
            String commercialLoansEndDate = "";//商业贷款结束日期
            String maxEndDate = "";//最大贷款结束日期
            String theoryEndDate = "";//理论上最大的结束日期（即最早的首次还款日期+240月）
            if (!StringUtils.isBlank(rent_house_address) && !StringUtils.isBlank(house_number) && !StringUtils.isBlank(loan_self_flag_desc) &&
                    !StringUtils.isBlank(loan_contract_no) && !StringUtils.isBlank(loan_alloted_time) && !StringUtils.isBlank(start_date) &&
                    !StringUtils.isBlank(house_type_desc)) {
                loanDataMap.put("loan_contract_no", loan_contract_no);
                loanDataMap.put("loan_alloted_time", loan_alloted_time);
                loanDataMap.put("start_date", start_date);
                loanDataMap.put("loan_type", "01");
                providentFundEndDate = getEndDate(start_date, loan_alloted_time);
                if (StringUtils.isBlank(tempStartDate)) {
                    tempStartDate = start_date;
                }
                interestExpenseDataList.add(loanDataMap);
            }
            Map loanDataMap1 = new HashMap();
            loan_contract_no_cell = sheet.getRow(interestExpenseRowDataIndex + 5).getCell(3);//贷款合同号
            loan_alloted_time_cell = sheet.getRow(interestExpenseRowDataIndex + 6).getCell(3);//贷款期限
            start_date_cell = sheet.getRow(interestExpenseRowDataIndex + 6).getCell(6);//首次还款日期
            Cell loan_bank_cell = sheet.getRow(interestExpenseRowDataIndex + 5).getCell(6);//贷款银行
            loan_contract_no = getCellValue(loan_contract_no_cell);
            loan_alloted_time = getCellValue(loan_alloted_time_cell);
            if (StringUtils.isBlank(loanAllotedTimeTemp)) {
                loanAllotedTimeTemp = "0";
            }
            if (StringUtils.isNotBlank(loan_alloted_time)) {
                if (Integer.valueOf(loanAllotedTimeTemp).intValue() < Integer.valueOf(loan_alloted_time).intValue()) {//取贷款期限最长的
                    loanAllotedTimeTemp = loan_alloted_time;
                }
            }
            start_date = getCellValue(start_date_cell);
            String loan_bank = getCellValue(loan_bank_cell);
            if (!StringUtils.isBlank(rent_house_address) && !StringUtils.isBlank(house_number) && !StringUtils.isBlank(loan_self_flag_desc) &&
                    !StringUtils.isBlank(loan_contract_no) && !StringUtils.isBlank(loan_alloted_time) && !StringUtils.isBlank(start_date) &&
                    !StringUtils.isBlank(house_type_desc)) {
                loanDataMap1.put("loan_contract_no", loan_contract_no);
                loanDataMap1.put("loan_alloted_time", loan_alloted_time);
                loanDataMap1.put("start_date", start_date);
                loanDataMap1.put("loan_type", "02");
                commercialLoansEndDate = getEndDate(start_date, loan_alloted_time);
                if (StringUtils.isBlank(tempStartDate)) {
                    tempStartDate = start_date;
                }
                loanDataMap1.put("loan_bank", loan_bank);
                interestExpenseDataList.add(loanDataMap1);
            }
            if (StringUtils.isNotBlank(tempStartDate)) {
                if (StringUtils.isNotBlank(start_date)) {
                    try {
                        if (sdf.parse(tempStartDate).getTime() - sdf.parse(start_date).getTime() > 0) {
                            tempStartDate = start_date;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (Integer.valueOf(tempStartDate.split("-")[0]).intValue() > Integer.valueOf(year).intValue()) {//如果首次还款日期大于抵扣年份
                    isDateEffective = false;
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.loanInterestNotBelongDeductYear")).append(System.getProperty("line.separator"));
                }
                theoryEndDate = getEndDate(tempStartDate, "240");
            }
            if (StringUtils.isNotBlank(providentFundEndDate) && StringUtils.isNotBlank(commercialLoansEndDate)) {
                try {
                    if (sdf.parse(providentFundEndDate).getTime() - sdf.parse(commercialLoansEndDate).getTime() > 0) {
                        maxEndDate = providentFundEndDate;
                    } else {
                        maxEndDate = commercialLoansEndDate;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (StringUtils.isNotBlank(providentFundEndDate) && StringUtils.isBlank(commercialLoansEndDate)) {
                maxEndDate = providentFundEndDate;
            } else if (StringUtils.isNotBlank(commercialLoansEndDate) && StringUtils.isBlank(providentFundEndDate)) {
                maxEndDate = commercialLoansEndDate;
            }
            try {
                if (StringUtils.isNotBlank(theoryEndDate) && StringUtils.isNotBlank(maxEndDate)) {
                    if (sdf.parse(maxEndDate).getTime() - sdf.parse(theoryEndDate).getTime() > 0) {
                        maxEndDate = theoryEndDate;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (interestExpenseDataList.size() > 0 && isDateEffective) {
                interestExpense.put("declare_type", C_DECLARE_TYPE_INTEREST_EXPENSE);
                interestExpense.put("deduct_type", C_DECLARE_TYPE_MONTH);
                interestExpense.put("deduct_money", deduct_money);
                interestExpense.put("start_date", this.getEffectiveDate(year, tempStartDate, "startDate"));
                if (Integer.valueOf(maxEndDate.split("-")[0]).intValue() < Integer.valueOf(year).intValue()) {//贷款结束日期小于抵扣年份 代表着此单据已经结束
                    isEnd = true;
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.loanInterestNotBelongDeductYear")).append(System.getProperty("line.separator"));
                }
                interestExpense.put("end_date", this.getEffectiveDate(year, maxEndDate, "endDate"));
                interestExpense.put("sub_items", interestExpenseDataList);
                String id = (String) declareTypeControlId.get("04");
                if (StringUtils.isNotEmpty(id)) {
                    interestExpense.put("id", id);
                }
                interestExpense.put("house_address", rent_house_address);
                interestExpense.put("loan_self_flag", loan_self_flag);
                interestExpense.put("house_type", house_type);
                interestExpense.put("house_number", house_number);
                interestExpense.put("loan_flat", loan_flat);

                try {
                    if (!isEnd) {//非结束状态时导入
                        saveZXDeclare(interestExpense, guidkey);
                    }
                } catch (GeneralException e) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.loanInterest") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                }
            }
            //获取住房贷款利息 end
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.presenceDeclareAnnal").replace("{name}", name).replace("{idnumber}", idNumber));
            msg.append(ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetHouseLoanError"));
        }

        try {
            //获取住房租金支出扣除信息 start
            int houseRentDataRowIndex = houseRentRowIndex + 2;
            int childCount = (supportElderlyRowIndex- houseRentDataRowIndex)/5;
            for(int i = 0;i<childCount;i++){
                HashMap houseRentMap = new HashMap();
                isDateEffective = true;
                rent_house_address_cell = sheet.getRow(houseRentDataRowIndex + i*5).getCell(2);//住房坐落地址
                Cell rent_house_self_name_cell = sheet.getRow(houseRentDataRowIndex + 1 +i*5).getCell(2);//出租方(个人)姓名
                Cell rent_house_id_type_cell = sheet.getRow(houseRentDataRowIndex + 1 +i*5).getCell(4);//出租方身份证件类型
                Cell rent_house_id_number_cell = sheet.getRow(houseRentDataRowIndex + 1 +i*5).getCell(6);//出租方身份证件号码
                Cell rent_house_unit_name_cell = sheet.getRow(houseRentDataRowIndex + 2 + i*5).getCell(2);//出租方单位名称
                Cell taxpayerIdentificationNumberCell = sheet.getRow(houseRentDataRowIndex + 2 + i*5).getCell(6);//纳税人识别号
                Cell rent_house_city_cell = sheet.getRow(houseRentDataRowIndex + 3 + i*5).getCell(2);//主要工作城市
                Cell rent_house_no_cell = sheet.getRow(houseRentDataRowIndex + 3 + i*5).getCell(6);//住房租赁合同编号
                start_date_cell = sheet.getRow(houseRentDataRowIndex + 4 + i*5).getCell(2);//租赁期起
                Cell end_date_cell = sheet.getRow(houseRentDataRowIndex + 4 + i*5).getCell(6);//租赁期止

                rent_house_address = getCellValue(rent_house_address_cell);
                String rent_house_self_name = getCellValue(rent_house_self_name_cell);
                String rent_house_id_type_desc = getCellValue(rent_house_id_type_cell);
                String rent_house_id_type = this.getCode(rent_house_id_type_desc, AdminCode.getCodeItemList("AC"));
                String rent_house_id_number = getCellValue(rent_house_id_number_cell);
                String rent_house_unit_name = getCellValue(rent_house_unit_name_cell);
                String taxpayerIdentificationNumber = getCellValue(taxpayerIdentificationNumberCell);
                String rent_house_city_desc = getCellValue(rent_house_city_cell);
                String rent_house_city = this.getCode(rent_house_city_desc, AdminCode.getCodeItemList("AB"));
                String rent_house_province = null;
                if (StringUtils.isNotEmpty(rent_house_city_desc)) {
                    rent_house_province = AdminCode.getCode("AB", rent_house_city).getPcodeitem();
                }
                String rent_house_no = getCellValue(rent_house_no_cell);
                start_date = getCellValue(start_date_cell);
                String end_date = getCellValue(end_date_cell);

                Map houseRent = new HashMap();
                houseRent.put("rent_house_city", rent_house_city);
                houseRent.put("rent_house_address", rent_house_address);
                houseRent.put("rent_house_name", StringUtils.isEmpty(rent_house_self_name) ? rent_house_unit_name : rent_house_self_name);
                houseRent.put("rent_house_id_type", rent_house_id_type);
                houseRent.put("rent_house_id_number", StringUtils.isEmpty(rent_house_id_number) ? taxpayerIdentificationNumber : rent_house_id_number);
                houseRent.put("rent_house_no", rent_house_no);
                houseRent.put("start_date", start_date);
                houseRent.put("rent_house_type", StringUtils.isEmpty(rent_house_self_name) ? "02" : "01");
                houseRent.put("rent_house_province", rent_house_province);
                houseRent.put("end_date", end_date);
                if ((int) getRentDeductMoney(rent_house_city_desc) == 1500) {
                    houseRentMap.put("rent_city_type", "01");
                } else if (((int) getRentDeductMoney(rent_house_city_desc) == 1100)) {
                    houseRentMap.put("rent_city_type", "02");
                } else {
                    houseRentMap.put("rent_city_type", "03");
                }
                ArrayList temp = new ArrayList();
                if (StringUtils.isNotBlank(rent_house_city) && StringUtils.isNotBlank(rent_house_address) && StringUtils.isNotBlank(start_date) && StringUtils.isNotBlank(end_date)) {
                    temp.add(houseRent);
                    if (Integer.valueOf(start_date.split("-")[0]).intValue() > Integer.valueOf(year).intValue() || Integer.valueOf(end_date.split("-")[0]).intValue() < Integer.valueOf(year).intValue()) {//租赁期起大于抵扣年份  租赁期止小于抵扣年份
                        msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.rentHouseNotBelongDeductYear")).append(System.getProperty("line.separator"));
                        isDateEffective = false;
                    }

                }
                houseRentMap.put("declare_type", C_DECLARE_TYPE_HOUSING_RENT);
                houseRentMap.put("deduct_type", C_DECLARE_TYPE_MONTH);
                houseRentMap.put("deduct_money", String.valueOf((int) getRentDeductMoney(rent_house_city_desc)));
                houseRentMap.put("sub_items", temp);
                houseRentMap.put("start_date", this.getEffectiveDate(year, start_date, "startDate"));
                houseRentMap.put("end_date", this.getEffectiveDate(year, end_date, "endDate"));
                String id = (String) declareTypeControlId.get("03");
                if (StringUtils.isNotEmpty(id)) {
                    houseRentMap.put("id", id);
                }
                if (temp.size() > 0 && isDateEffective) {
                    try {
                        saveZXDeclare(houseRentMap, guidkey);
                    } catch (GeneralException e) {
                        msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.rentHouseMoney") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                    }
                }
                //saveDateMap.clear();
                //获取住房租金支出扣除信息 end
            }

        } catch (Exception e) {
            e.printStackTrace();
            msg.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.presenceDeclareAnnal").replace("{name}", name).replace("{idnumber}", idNumber));
            msg.append(ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetHouseRentError"));
        }

        try {
            //获取赡养老人支出信息start
            boolean isVaildDeductMoney = true;
            HashMap supportElderlyData = new HashMap();
            int supportElderlyDataIndex = supportElderlyRowIndex + 2;
            String minBirthday = "";//老人中出生最早的日期
            Cell isSelfChildCell = sheet.getRow(supportElderlyDataIndex).getCell(1);//是否是独生子女
            boolean isSelfChild = getCellValue(isSelfChildCell).equalsIgnoreCase(ResourceFactory.getProperty("gz.zxdeclare.label.yes")) ? true : false;
            Cell apportion_type_cell = sheet.getRow(supportElderlyDataIndex).getCell(4);//分摊方式
            String apportion_type_desc = getCellValue(apportion_type_cell);
            String apportion_type = this.apportionTypeDescToCode(apportion_type_desc);
            Cell deduct_money_cell = sheet.getRow(supportElderlyDataIndex).getCell(6);//本年度月扣除金额
            deduct_money = getCellValue(deduct_money_cell);
            List<Map<String, String>> subOldList = new ArrayList<Map<String, String>>();//被赡养人信息
            List<Map<String, String>> subChildList = new ArrayList<Map<String, String>>();//共同赡养人信息
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = supportElderlyDataIndex; i < majorIllnessRowIndex; i++) {
                if (StringUtils.contains(getCellValue(sheet.getRow(i).getCell(0)), ResourceFactory.getProperty("gz.zxdeclare.label.supportedElder"))) {
                    Map<String, String> supportElderlyTemp = new HashMap<String, String>();
                    Cell member_name_cell = sheet.getRow(i).getCell(2);//姓名
                    Cell id_type_cell = sheet.getRow(i).getCell(4);//身份证件类型
                    Cell id_number_cell = sheet.getRow(i).getCell(6);//身份证件号码
                    Cell birthday_cell = sheet.getRow(i + 1).getCell(2);//出生日期
                    Cell relation_cell = sheet.getRow(i + 1).getCell(4);//与本人关系
                    String member_name = getCellValue(member_name_cell);
                    String id_type_desc = getCellValue(id_type_cell);
                    String id_type = this.getCode(id_type_desc, AdminCode.getCodeItemList("AC"));
                    String id_number = getCellValue(id_number_cell);
                    String birthday = getCellValue(birthday_cell);
                    String relation = getCellValue(relation_cell);
                    if (StringUtils.isBlank(member_name)) {
                        continue;
                    }
                    if (StringUtils.isBlank(id_type_desc)) {
                        continue;
                    }
                    if (StringUtils.isBlank(id_number)) {
                        continue;
                    }
                    if (StringUtils.isBlank(birthday)) {
                        continue;
                    }
                    if (StringUtils.isBlank(relation)) {
                        continue;
                    }
                    supportElderlyTemp.put("member_name", member_name);
                    supportElderlyTemp.put("id_type", id_type);
                    supportElderlyTemp.put("id_number", id_number);
                    supportElderlyTemp.put("birthday", birthday);
                    supportElderlyTemp.put("relation", relation);
                    supportElderlyTemp.put("relation_type", "1");
                    if (StringUtils.isBlank(minBirthday)) {
                        minBirthday = birthday;
                    }
                    try {
                        if (simpleDateFormat.parse(minBirthday).getTime() - simpleDateFormat.parse(birthday).getTime() > 0) {
                            minBirthday = birthday;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    subOldList.add(supportElderlyTemp);
                } else if (StringUtils.contains(getCellValue(sheet.getRow(i).getCell(0)), ResourceFactory.getProperty("gz.zxdeclare.label.togetherSupportPerson"))) {
                    Map<String, String> supportElderlyTemp = new HashMap<String, String>();
                    Cell member_name_cell = sheet.getRow(i).getCell(2);//姓名
                    Cell id_type_cell = sheet.getRow(i).getCell(4);//身份证件类型
                    Cell id_number_cell = sheet.getRow(i).getCell(6);//身份证件号码
                    String member_name = getCellValue(member_name_cell);
                    String id_type_desc = getCellValue(id_type_cell);//需翻译
                    String id_type = this.getCode(id_type_desc, AdminCode.getCodeItemList("AC"));
                    String id_number = getCellValue(id_number_cell);
                    if (StringUtils.isBlank(member_name)) {
                        continue;
                    }
                    if (StringUtils.isBlank(id_type_desc)) {
                        continue;
                    }
                    if (StringUtils.isBlank(id_number)) {
                        continue;
                    }
                    supportElderlyTemp.put("member_name", member_name);
                    supportElderlyTemp.put("id_type", id_type);
                    supportElderlyTemp.put("id_number", id_number);
                    supportElderlyTemp.put("relation_type", "2");
                    subChildList.add(supportElderlyTemp);
                }
            }
            if (StringUtils.isNotBlank(deduct_money)) {
                deduct_money = deduct_money.replaceAll(",", "");
            }
            if (!isSelfChild) {
                supportElderlyData.put("apportion_type", apportion_type);
                supportElderlyData.put("child_apportion", subChildList.size() + 1);
                if (subChildList.size() > 0) {
                    supportElderlyData.put("sub_child_items", subChildList);
                }
                if (StringUtils.isNotBlank(deduct_money)) {
                    if (Double.valueOf(deduct_money).doubleValue() > 1000) {
                        isVaildDeductMoney = false;
                        msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.supElderDeductMoneyError")).append(System.getProperty("line.separator"));
                    }
                }
            }
            boolean isSixty = true;//是否满60岁
            if (subOldList.size() > 0) {
                supportElderlyData.put("declare_type", C_DECLARE_TYPE_SUPPORT_ELDERLY);
                supportElderlyData.put("deduct_type", C_DECLARE_TYPE_MONTH);
                supportElderlyData.put("deduct_money", String.valueOf(deduct_money));
                supportElderlyData.put("sub_old_items", subOldList);
                supportElderlyData.put("end_date", year + "-12-31");//赡养老人结束日期为申报年年底
                if (Integer.valueOf(year).intValue() - Integer.valueOf(minBirthday.substring(0, 4)).intValue() > 60) {
                    start_date = year + "-01-01";
                } else if (Integer.valueOf(year).intValue() - Integer.valueOf(minBirthday.substring(0, 4)).intValue() == 60) {
                    start_date = year + "-" + minBirthday.split("-")[1] + "-01";
                } else {
                    start_date = Integer.valueOf(minBirthday.substring(0, 4)) + 60 + "-" + minBirthday.split("-")[1] + "-01";
                    isSixty = false;
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.supElderAgeError")).append(System.getProperty("line.separator"));
                }
                supportElderlyData.put("start_date", start_date);
                String sid = (String) declareTypeControlId.get("06");
                if (StringUtils.isNotEmpty(sid)) {
                    supportElderlyData.put("id", sid);
                }
                try {
                    if (isSixty && isVaildDeductMoney) {
                        saveZXDeclare(supportElderlyData, guidkey);
                    }
                } catch (GeneralException e) {
                    msg.append("\t\t" + ResourceFactory.getProperty("gz.zxdeclare.label.supportElder") + e.getErrorDescription()).append(System.getProperty("line.separator"));
                }
            }
            //获取赡养老人支出信息end
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("\t" + ResourceFactory.getProperty("gz.zxdeclare.error.presenceDeclareAnnal").replace("{name}", name).replace("{idnumber}", idNumber));
            msg.append(ResourceFactory.getProperty("gz.zxdeclare.label.importExcelSheetSYOldError"));
        }

        if (msg.length() == 0)
            msg.append("success");
        return msg.toString();
    }


    /**
     * 保存申报数据     审核通过状态
     *
     * @param param   专项附加申报数据集合
     * @param guidkey 人员唯一标识
     * @return
     * @throws GeneralException
     */
    private String saveZXDeclare(HashMap param, String guidkey) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RecordVo vo = new RecordVo("declare_infor");
        String id = "";
        if (param.containsKey("id")) {
            id = (String) param.get("id");
        } else {
            IDGenerator idGenerator = new IDGenerator(2, this.conn);
            id = idGenerator.getId("declare_infor.id");
            vo.setString("parent_id", null);

            vo.setDate("create_date", sdf.format(new Date()));
        }
        vo.setString("approve_state", C_APPROVE_STATE_ADOPT);
        vo.setInt("id", Integer.parseInt(id));
        String declare_type = param.containsKey("declare_type") ? (String) param.get("declare_type") : "";
        String deduct_money = "0";
        if (param.containsKey("deduct_money")) {
            deduct_money = ((String) param.get("deduct_money")).replaceAll(",", "");
        }

        vo.setString("guidkey", guidkey); //人员唯一标识
        vo.setString("declare_type", declare_type); // 申报类型
        vo.setString("deduct_type", param.containsKey("deduct_type") ? (String) param.get("deduct_type") : "");//抵扣方式   现在默认01 按月
        vo.setDouble("deduct_money", Double.parseDouble(deduct_money));// 抵扣金额

        vo.setDate("start_date", param.containsKey("start_date") ? (String) param.get("start_date") : "");// 起始日期
        try {
            vo.setDate("end_date", param.containsKey("end_date") ? sdf.parse((String) param.get("end_date")) : sdf.parse("9999-12-31"));// 终止日期
        } catch (Exception e) {
            e.printStackTrace();
            // 保存时日期格式处理出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.dateSimpleMsg"));
        }
        vo.setString("description", param.containsKey("description") ? (String) param.get("description") : "");  // 备注

        if (C_DECLARE_TYPE_CONTINU_EDU.equalsIgnoreCase(declare_type)) {//继续教育
            vo.setString("cuntin_edu_type", param.containsKey("cuntin_edu_type") ? (String) param.get("cuntin_edu_type") : "");//继续教育类型
        } else if (C_DECLARE_TYPE_INTEREST_EXPENSE.equalsIgnoreCase(declare_type)) {//贷款利息
            vo.setString("house_address", param.containsKey("house_address") ? (String) param.get("house_address") : "");//房屋坐落地址
            vo.setString("loan_self_flag", param.containsKey("loan_self_flag") ? (String) param.get("loan_self_flag") : "");//本人是否借款人
            vo.setString("house_type", param.containsKey("house_type") ? (String) param.get("house_type") : "");//房屋证书类型
            vo.setString("house_number", param.containsKey("house_number") ? (String) param.get("house_number") : "");//房屋证书号码
            vo.setString("loan_flat", param.containsKey("loan_flat") ? (String) param.get("loan_flat") : "");//是否婚前各自首套贷款，且婚后分别扣除50%
        } else if (C_DECLARE_TYPE_SUPPORT_ELDERLY.equalsIgnoreCase(declare_type)) {//赡养老人
            vo.setInt("child_apportion", param.containsKey("child_apportion") ? (Integer) param.get("child_apportion") : 0);//平摊人数
            vo.setString("apportion_type", param.containsKey("apportion_type") ? (String) param.get("apportion_type") : "");//赡养方式
        } else if (C_DECLARE_TYPE_HOUSING_RENT.equalsIgnoreCase(declare_type)) {//住房租金
            vo.setString("rent_city_type", param.containsKey("rent_city_type") ? (String) param.get("rent_city_type") : "");//租房城市类型
        }

        try {
            if (param.containsKey("id")) {
                dao.updateValueObject(vo);
            } else {
                dao.addValueObject(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //保存专项申报数据出错
            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.saveDeclareMsg"));
        }

        ArrayList sub_items = null;
        if (param.containsKey("sub_items")) {
            sub_items = (ArrayList) param.get("sub_items");
            this.saveDeclareSub(sub_items, id, guidkey);
        }
        if (param.containsKey("sub_old_items")) {
            sub_items = (ArrayList) param.get("sub_old_items");
            this.saveDeclareSub(sub_items, id, guidkey);
        } else {
            this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_OLD);
        }
        if (param.containsKey("sub_child_items")) {
            sub_items = (ArrayList) param.get("sub_child_items");
            this.saveDeclareSub(sub_items, id, guidkey);
        } else {
            this.deleteSubItems(new ArrayList(), Integer.parseInt(id), C_DECLARE_TYPE_SUPPORT_ELDERLY_CHILD);
        }

        //保存附件 规则
        if (param.containsKey("attach_path")) {
            String filePath = this.getFilePath(guidkey, declare_type, Integer.parseInt(id));
            File newPath = new File(filePath);
            newPath.mkdirs();
            ArrayList fileList = (ArrayList) param.get("attach_path");
            for (int i = 0; i < fileList.size(); i++) {
                HashMap fileMap = (HashMap) PubFunc.DynaBean2Map((MorphDynaBean) fileList.get(i));
                String filename = (String) fileMap.get("file_name");
                if (!(fileMap.containsKey("i9999") || fileMap.containsKey("state")))
                    continue;
                if (fileMap.containsKey("i9999") && "-1".equals((String) fileMap.get("i9999"))) {//新增
                    String tempPath = System.getProperty("java.io.tmpdir");
                    if (!tempPath.endsWith(File.separator)) {//拼分隔符
                        tempPath = tempPath + File.separator;
                    }
                    String path = tempPath + filename;
                    File file = new File(path);
                    if (file.isFile()) {
                        try {
//							File newPath = new File(filePath);
//							if(!newPath.exists()) {
//								newPath.mkdirs();
//							}
                            File newFile = new File(filePath + "/" + filename);
                            if (newFile.isFile())
                                newFile.delete();
                            FileUtils.copyFile(file, newFile);
                            file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                            //附件保存出错
                            log.error(ResourceFactory.getProperty("gz.zxdeclare.error.attachSaveMsg"));
                            throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.attachSaveMsg"));
                        }
                    }
                }
                if (fileMap.containsKey("state") && "D".equalsIgnoreCase((String) fileMap.get("state"))) {//删除
                    File file = new File(filePath + "\\" + filename);
                    if (file.isFile())
                        file.delete();
                }
            }
        }

        return "success`" + PubFunc.encrypt(id);
    }


    /**
     * 子女教育描述转代码
     *
     * @return
     */
    private String levelDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.childFirstlevelDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.childSecondlevelDesc").equalsIgnoreCase(desc))
            code = "02";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.childThreelevelDesc").equalsIgnoreCase(desc))
            code = "03";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.childFourlevelDesc").equalsIgnoreCase(desc))
            code = "04";
        return code;
    }

    /**
     * 学历继续教育描述转代码
     *
     * @return
     */
    private String jxEdulevelDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFirstlevelDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.jxEduSecondlevelDesc").equalsIgnoreCase(desc))
            code = "02";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.jxEduThreelevelDesc").equalsIgnoreCase(desc))
            code = "03";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFourlevelDesc").equalsIgnoreCase(desc))
            code = "04";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFiveslevelDesc").equalsIgnoreCase(desc))
            code = "05";
        return code;
    }

    /**
     * 职业继续教育描述转代码
     *
     * @return
     */
    private String postTypeDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.postTypeFirstDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.postTypeSecondDesc").equalsIgnoreCase(desc))
            code = "02";
        return code;
    }

    /**
     * 出租方类型描述转代码
     *
     * @return
     */
    private String rentHouseTypeDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.rentHouseFirstTypeDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.rentHouseSecondTypeDesc").equalsIgnoreCase(desc))
            code = "02";
        return code;
    }

    /**
     * 贷款类型描述转代码
     *
     * @return
     */
    private String loanTypeDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.loanFirstTypeDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.loanSecondTypeDesc").equalsIgnoreCase(desc))
            code = "02";
        return code;
    }

    /**
     * 房屋证件类型描述转代码
     *
     * @return
     */
    private String houseTypeDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.houseFirstTypeDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.houseSecondTypeDesc").equalsIgnoreCase(desc))
            code = "02";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.houseThreeTypeDesc").equalsIgnoreCase(desc))
            code = "03";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.houseFourTypeDesc").equalsIgnoreCase(desc))
            code = "04";
        return code;
    }

    /**
     * 是否婚前各自首套贷款，且婚后分别扣除50%描述转代码
     *
     * @return
     */
    private String loanFlatDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.yes").equalsIgnoreCase(desc))
            code = "1";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.no").equalsIgnoreCase(desc))
            code = "2";
        return code;
    }

    /**
     * 本人是否借款人描述转代码
     *
     * @return
     */
    private String loanSelfFlatDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.yes").equalsIgnoreCase(desc))
            code = "1";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.no").equalsIgnoreCase(desc))
            code = "2";
        return code;
    }

    /**
     * 与父母关系描述转代码
     *
     * @return
     */
    private String relationDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.syOldParentDesc").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.syOldOtherDesc").equalsIgnoreCase(desc))
            code = "02";
        return code;
    }

    /**
     * 赡养方式描述转代码
     *
     * @return
     */
    private String apportionTypeDescToCode(String desc) {
        String code = "";
        if (ResourceFactory.getProperty("gz.zxdeclare.label.syOldFirstType").equalsIgnoreCase(desc))
            code = "01";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.syOldSecondType").equalsIgnoreCase(desc))
            code = "02";
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.syOldThreeType").equalsIgnoreCase(desc))
            code = "03";
        return code;
    }


    private double getRentDeductMoney(String city) {
        double money = 0;
        if (ResourceFactory.getProperty("gz.zxdeclare.label.firstCity").contains("," + city + ","))
            money = 1500;
        else if (ResourceFactory.getProperty("gz.zxdeclare.label.secondCity").contains("," + city + ","))
            money = 1100;
        else
            money = 800;
        return money;
    }


    /**
     * 系统代码描述转代码id号
     *
     * @param codeDesc
     * @param codeList
     * @return
     */
    private String getCode(String codeDesc, ArrayList codeList) {
        String codeid = "";
        if (StringUtils.isEmpty(codeDesc)) {
            return codeid;
        }
        for (int i = 0; i < codeList.size(); i++) {
            CodeItem codeItem = (CodeItem) codeList.get(i);
            //if (codeDesc.equalsIgnoreCase(codeItem.getCodename())) {
            //    codeid = codeItem.getCodeitem();
            //    break;
            //}
            if (codeItem.getCodename().endsWith(codeDesc)) {
                codeid = codeItem.getCodeitem();
                break;
            }
        }
        return codeid;
    }

    /**
     * 更新对应指标关系
     *
     * @param fieldsList 指标数据
     * @return 更新成功与否
     * @author wangz
     */

    @Override
    public String SaveRelation(List fieldsList) {
        String saveFlag = "fail";
        ContentDAO dao = new ContentDAO(this.conn);
        Document document = null;
        String xml = "";
        //定义根节点
        Element param = new Element("param");
        document = new Document(param);
        //声明一个Document对象
        for (int i = 0; i < fieldsList.size(); i++) {
            MorphDynaBean field = (MorphDynaBean) fieldsList.get(i);
            Element item = new Element("item");
            Attribute sourceField = new Attribute("sourceField", (String) field.get("sourceField"));
            Attribute fieldSetId = new Attribute("fieldSetId", (String) field.get("fieldsetid"));
            Attribute itemId = new Attribute("itemId", (String) field.get("itemid"));
            Attribute fielditemidesc = new Attribute("fielditemidesc", (String) field.get("fielditemidesc"));
            item.setAttribute(sourceField);
            item.setAttribute(fielditemidesc);
            item.setAttribute(fieldSetId);
            item.setAttribute(itemId);
            param.addContent(item);
        }
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");// 设置xml文件的字符为UTF-8，解决中文问题
        XMLOutputter xmlout = new XMLOutputter(format);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        RowSet rs = null;
        try {
            xmlout.output(document, bo);
            xml = bo.toString().replaceAll("\r\n", "");
            String upateSql = "update CONSTANT set str_value = ? where UPPER(CONSTANT)='ZXFJ_PARAM'";
            int count = dao.update(upateSql, Arrays.asList(xml));
            if (count > 0) {
                saveFlag = "success";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return saveFlag;
    }

    /**
     * 获取指标对应关系
     *
     * @author wangz
     */

    @Override
    public String getRelation() {
        RowSet rowSet = null;
        Element element = null;
        ContentDAO dao = new ContentDAO(this.conn);
        String sql = "select str_value  from CONSTANT where UPPER(CONSTANT)='ZXFJ_PARAM'";
        String xml = "";
        String returnData = "";
        try {
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                xml = Sql_switcher.readMemo(rowSet, "str_value");
                if (StringUtils.isEmpty(xml)) {//解决xml为空时 xml解析报错
                    return "";
                }
                Document document = PubFunc.generateDom(xml);
                String xpath = "/param";
                XPath findPath = XPath.newInstance(xpath);
                element = (Element) findPath.selectSingleNode(document);
                if (element != null) {
                    List list = element.getChildren("item");
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        Element temp = (Element) list.get(i);
                        String sourceField = temp.getAttributeValue("sourceField");
                        String fieldSetId = temp.getAttributeValue("fieldSetId");
                        String itemId = temp.getAttributeValue("itemId");
                        String fielditemidesc = temp.getAttributeValue("fielditemidesc");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sourceField", sourceField);
                        jsonObject.put("fieldSetId", fieldSetId);
                        jsonObject.put("itemId", itemId);
                        jsonObject.put("fielditemidesc", fielditemidesc);
                        jsonArray.add(jsonObject);
                    }
                    returnData = jsonArray.toString();
                }
            } else {
                ArrayList list = new ArrayList();
                list.add("ZXFJ_PARAM");
                list.add("A");
                list.add("");
                list.add("");
                dao.insert("insert into CONSTANT values(?,?,?,?)", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            PubFunc.closeResource(rowSet);
        }

        return returnData;
    }

    /**
     * @param userView
     * @return
     * @author wangz
     */

    @Override
    public Map exportTemplateExcel(UserView userView, String fileid) {
        String isExitsFile = "true";
        //1.判断模版文件是否存在
        //String fileName = ResourceFactory.getProperty("gz.zxdeclare.error.zxDeclareTemplate");
        //String path = PubFunc.decrypt(this.getTemplateFilePath()) + fileName;
        //File templateFile = new File(path + ".xls");
        Map temp = new HashMap();
        //if (!templateFile.exists()) {
        //    templateFile = new File(path + ".xlsx");
        //    if (!templateFile.exists()) {
        //        isExitsFile = "false";
        //        temp.put("isExitsFile", isExitsFile);
        //        return temp;
        //    }
        //}
        InputStream is = null;
        Workbook wb = null;
        try {
            is = VfsService.getFile(fileid);
            wb = new HSSFWorkbook(is);
            Sheet mainSheet = wb.getSheet(C_DECLARE_TYPE_MAIN_SHEET);//首页
            Sheet chileduSheet = wb.getSheet(C_DECLARE_TYPE_CHILDEDU_SHEET);//子女教育
            Sheet continueduSheet = wb.getSheet(C_DECLARE_TYPE_CONTINUEDU_SHEET);//继续教育
            Sheet housingrentSheet = wb.getSheet(C_DECLARE_TYPE_HOUSINGRENT_SHEET);//住房租金
            Sheet interestexpenseSheet = wb.getSheet(C_DECLARE_TYPE_INTERESTEXPENSE_SHEET);//贷款利息
            Sheet supportelderlySheet = wb.getSheet(C_DECLARE_TYPE_SUPPORTELDERLY_SHEET);//赡养老人
            if (mainSheet == null || chileduSheet == null || continueduSheet == null || housingrentSheet == null || interestexpenseSheet == null || supportelderlySheet == null) {
                temp.put("isNotTemplateFile", true);
                return temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            temp.put("successFlag", false);
            return temp;
        } finally {
            PubFunc.closeResource(is);
            PubFunc.closeResource(wb);
        }
        //2.判断权限取出符合薪资发放业务管理范围的人员申报数据（不确定的指标还没有数据）
        Map declateData = this.getDeclareDataByPriv(userView);
        Map returnMap = this.createExcelByTemplate(declateData, is, userView);
        return returnMap;
    }

    /**
     * 获取模版文件存放路径
     *
     * @return
     * @author wangz
     */

    @Override
    public String getTemplateFilePath() {
        StringBuffer filePath = new StringBuffer();
        String filePaths = "";
        ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
        String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath").replace("\\", File.separator).replace("/", File.separator);
        StringBuffer path = new StringBuffer();
        path.append(RootDir).append(File.separator).append("multimedia").append(File.separator).append("mobile");
        path.append(File.separator).append("zxdeclare");
        //String path = RootDir+"/multimedia/mobile/zxdeclare";
        //filePath = path + "/template/";
        filePath.append(path).append(File.separator).append("template").append(File.separator);
        filePaths = PubFunc.encrypt(filePath.toString());
        return filePaths;
    }

    /**
     * 保存模版文件到指定目录
     *
     * @author wangz
     */

    @Override
    public String saveTemplateFile(String fileid) {
        String returnMsg = "true";
        String fileName = ResourceFactory.getProperty("gz.zxdeclare.error.zxDeclareTemplate");
        Workbook wb = null;
        try {
            InputStream inputStream = VfsService.getFile(fileid);
            VfsFileEntity vfsFileEntity=  VfsService.getFileEntity(fileid);
            String ext = vfsFileEntity.getExtension();
            wb = new HSSFWorkbook(inputStream);
            Sheet mainSheet = wb.getSheet(C_DECLARE_TYPE_MAIN_SHEET);//首页
            Sheet chileduSheet = wb.getSheet(C_DECLARE_TYPE_CHILDEDU_SHEET);//子女教育
            Sheet continueduSheet = wb.getSheet(C_DECLARE_TYPE_CONTINUEDU_SHEET);//继续教育
            Sheet housingrentSheet = wb.getSheet(C_DECLARE_TYPE_HOUSINGRENT_SHEET);//住房租金
            Sheet interestexpenseSheet = wb.getSheet(C_DECLARE_TYPE_INTERESTEXPENSE_SHEET);//贷款利息
            Sheet supportelderlySheet = wb.getSheet(C_DECLARE_TYPE_SUPPORTELDERLY_SHEET);//赡养老人
            if (mainSheet == null || chileduSheet == null || continueduSheet == null || housingrentSheet == null || interestexpenseSheet == null || supportelderlySheet == null) {
                returnMsg = "isNotTemplateFile";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(wb);
        }

        return returnMsg;
    }

    /**
     * 获取符合登录用户业务管理范围内的申报信息
     *
     * @author wangz
     */
    private Map getDeclareDataByPriv(UserView userView) {
        Map paramMap = new HashMap();
        Map<String, Map> userInfo = new HashMap<String, Map>();//key 为guidkey 值为guidkey人员的六项申报信息
        Map<String, List> guidkeyMap = new HashMap<String, List>();//key 为guidkey 值为guidkey人员的申报记录的declareid
        Map<String, Map> guidkeyNameNbase = new HashMap<String, Map>();
        Map<String, ArrayList<String>> nbaseA0100Map = new HashMap<String, ArrayList<String>>();
        StringBuffer extraSql = new StringBuffer("and ");
        if (Sql_switcher.searchDbServer() == 1) {
            extraSql.append(" year(create_date) = year(getDate())");
        } else if (Sql_switcher.searchDbServer() == 2) {
            extraSql.append(" to_char(create_date,'yyyy') = to_char(sysdate,'yyyy')");
        }
        String sql = this.getDeclareListSql(IDeclareService.C_DECLARE_TYPE_ALL, C_APPROVE_STATE_ADOPT, userView, extraSql.toString());
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                String guidkey = rs.getString("guidkey");
                String declareId = rs.getString("id");
                String name = rs.getString("a0101");
                String nbase = rs.getString("nbase");
                String a0100 = rs.getString("a0100");
                Map tempParam = new HashMap();
                tempParam.put("name", name);
                tempParam.put("objectId", nbase + a0100);
                guidkeyNameNbase.put(guidkey, tempParam);
                if (!nbaseA0100Map.containsKey(nbase)) {
                    ArrayList<String> a0100List = new ArrayList<String>();
                    a0100List.add(a0100);
                    nbaseA0100Map.put(nbase, a0100List);
                } else {
                    if (!nbaseA0100Map.get(nbase).contains(a0100)) {//防止一个人有多条申报记录  lsit里面a0100重复
                        nbaseA0100Map.get(nbase).add(a0100);
                    }
                }
                if (!guidkeyMap.containsKey(guidkey)) {//如果不存在此人员，则创建此人员的容器用于存放信息
                    List<String> declareIdList = new ArrayList<String>();
                    declareIdList.add(declareId);
                    guidkeyMap.put(guidkey, declareIdList);
                } else {
                    guidkeyMap.get(guidkey).add(declareId);
                }
            }
            //取所有的人员的申报记录
            Set<String> keySet = guidkeyMap.keySet();
            for (String guidkey : keySet) {
                List<String> declareIdList = guidkeyMap.get(guidkey);
                for (String declareId : declareIdList) {
                    Map<String, String> singleDeclareData = this.getDeclareInfor(PubFunc.encrypt(declareId));
                    String declare_type = singleDeclareData.get("declare_type");
                    String cuntin_edu_type = singleDeclareData.get("cuntin_edu_type");
                    if (!userInfo.containsKey(guidkey)) {
                        Map<String, Map<String, String>> declareData = new HashMap<String, Map<String, String>>();
                        if (StringUtils.equalsIgnoreCase(declare_type, "02")) {//是继续教育
                            declareData.put(declare_type + cuntin_edu_type, singleDeclareData);
                        } else {
                            declareData.put(declare_type, singleDeclareData);
                        }
                        userInfo.put(guidkey, declareData);
                    } else {
                        if (StringUtils.equalsIgnoreCase(declare_type, "02")) {//是继续教育
                            userInfo.get(guidkey).put(declare_type + cuntin_edu_type, singleDeclareData);
                        } else {
                            userInfo.get(guidkey).put(declare_type, singleDeclareData);
                        }
                    }
                }
            }
            paramMap.put("userInfo", userInfo);
            paramMap.put("extraParam", guidkeyNameNbase);
            paramMap.put("nbaseA0100", nbaseA0100Map);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return paramMap;
    }

    /**
     * 创建excel文档并填充数据
     *
     * @author wangz
     */
    private Map createExcelByTemplate(Map<String, Map<String, Map>> param, InputStream is, UserView userView) {
        int fileCount = 0;
        Map retrunMap = new HashMap();
        ZipOutputStream outputStream = null;
        BufferedInputStream origin = null;
        FileInputStream fileIn = null;
        //在临时目录生成declare文件夹用于存放文件
        String path = System.getProperty("java.io.tmpdir") + File.separator + "declare";
        File filePath = new File(path);
        if (!filePath.isDirectory()) {//如果文件目录不存在则创建此目录
            filePath.mkdirs();
        }
        Map<String, Map> declareData = param.get("userInfo");
        Map<String, Map> guidkeyNameNbase = param.get("extraParam");
        Map nbaseA0100Map = param.get("nbaseA0100");
        Map homeData = this.getHomeData(nbaseA0100Map);//存储每个人的首页信息
        FileOutputStream fos = null;
        ContentDAO dao = new ContentDAO(this.conn);
        FileOutputStream fileOut = null;
        try {

            //获取工作薄
            Set<String> keySet = declareData.keySet();
            for (String guidkey : keySet) {
                Workbook wb = new HSSFWorkbook(is);//必须循环创建  否则会有脏数据
                Map<String, Map> singlePeopleData = declareData.get(guidkey);//个人申报数据
                String name = (String) guidkeyNameNbase.get(guidkey).get("name");
                String objectId = (String) guidkeyNameNbase.get(guidkey).get("objectId");
                Map peopleHomeData = (Map) homeData.get(objectId);
                String taxpayerIDType = "";
                String taxpayerIDNumber = "";
                String taxpayerIdentificationNumber = "";
                String contactAddress = "";
                String spouseSituation = "";
                String spouseName = "";
                String spouseIdType = "";
                String spouseIdNumber = "";
                if (peopleHomeData != null) {
                    taxpayerIDType = (String) peopleHomeData.get("taxpayerIDType") == null ? "" : (String) peopleHomeData.get("taxpayerIDType");//纳税人身份证件类型
                    taxpayerIDNumber = (String) peopleHomeData.get("taxpayerIDNumber") == null ? "" : (String) peopleHomeData.get("taxpayerIDNumber");//纳税人身份证件号码
                    taxpayerIdentificationNumber = (String) peopleHomeData.get("taxpayerIdentificationNumber") == null ? "" : (String) peopleHomeData.get("taxpayerIdentificationNumber");//纳税人识别号
                    contactAddress = (String) peopleHomeData.get("contactAddress") == null ? "" : (String) peopleHomeData.get("contactAddress");//联系地址
                    spouseSituation = (String) peopleHomeData.get("spouseSituation") == null ? "" : (String) peopleHomeData.get("spouseSituation");//配偶情况
                    spouseName = (String) peopleHomeData.get("spouseName") == null ? "" : (String) peopleHomeData.get("spouseName");//配偶姓名
                    spouseIdType = (String) peopleHomeData.get("spouseIdType") == null ? "" : (String) peopleHomeData.get("spouseIdType");//配偶身份类型
                    spouseIdNumber = (String) peopleHomeData.get("spouseIdNumber") == null ? "" : (String) peopleHomeData.get("spouseIdNumber");//配偶身份证件号码
                }
                Sheet sheet1 = wb.getSheet(C_DECLARE_TYPE_MAIN_SHEET);//首页
                Cell yearCell = sheet1.getRow(0).getCell(3);
                Date currentDate = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                String yearValue = simpleDateFormat.format(currentDate);
                yearCell.setCellValue(yearValue);//扣除年度为系统当前时间的年份
                Cell nameCell = sheet1.getRow(0).getCell(9);
                nameCell.setCellValue(name);
                sheet1.getRow(1).getCell(3).setCellValue(taxpayerIDType);
                sheet1.getRow(1).getCell(9).setCellValue(taxpayerIDNumber);
                //获取到手机号码指标
                String phoneFld = "";
                RecordVo avo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
                if (avo != null) {
                    phoneFld = avo.getString("str_value");
                }
                String emailId = ConstantParamter.getEmailField().toLowerCase();//电子邮箱
                String nbase = objectId.substring(0, 3);
                RecordVo usrVo = new RecordVo(nbase + "A01");
                usrVo.setString("a0100", objectId.substring(3));
                usrVo = dao.findByPrimaryKey(usrVo);
                String phoneNumber = usrVo.getString(phoneFld.toLowerCase());
                String email = usrVo.getString(emailId);
                //获取电子邮箱指标
                sheet1.getRow(2).getCell(3).setCellValue(phoneNumber == null ? "" : phoneNumber);//手机号码
                sheet1.getRow(2).getCell(9).setCellValue(taxpayerIdentificationNumber);//纳税人识别号
                sheet1.getRow(3).getCell(3).setCellValue(contactAddress);//联系地址
                sheet1.getRow(3).getCell(9).setCellValue(email == null ? "" : email);//电子邮箱
                sheet1.getRow(5).getCell(3).setCellValue(spouseSituation);
                sheet1.getRow(5).getCell(9).setCellValue(spouseName);
                sheet1.getRow(6).getCell(3).setCellValue(spouseIdType);
                sheet1.getRow(6).getCell(9).setCellValue(spouseIdNumber);
                if (singlePeopleData.containsKey("01")) {//子女教育
                    Map specialItem = singlePeopleData.get("01");
                    ArrayList<HashMap> subInfoList = (ArrayList) specialItem.get("sub_items");
                    int i = 0;
                    for (HashMap subInfo : subInfoList) {
                        String member_name = (String) subInfo.get("member_name");//子女姓名
                        String id_type_desc = (String) subInfo.get("id_type_desc");//身份证件类型
                        String id_number = (String) subInfo.get("id_number");//身份证件号码
                        String birthday = (String) subInfo.get("birthday");//出生日期
                        String nationality_desc = (String) subInfo.get("nationality_desc");//国籍
                        String edu_level = (String) subInfo.get("edu_level");//当前受教育阶段
                        String edu_level_desc = eduLevelToDesc(edu_level);//当前受教育阶段(描述)
                        String start_date = (String) subInfo.get("start_date");//当前受教育阶段起始时间
                        String end_date = (String) subInfo.get("end_date");//当前受教育阶段结束时间
                        String edu_stop_date = (String) subInfo.get("edu_stop_date");//教育终止时间
                        String edu_nationality_desc = (String) subInfo.get("edu_nationality_desc");//当前就读国家
                        String edu_institution = (String) subInfo.get("edu_institution");//当前就读学校
                        String deduct_proportion = (String) subInfo.get("deduct_proportion");//本人扣除比例
                        birthday = this.transDateToString(birthday, "yyyy-MM-dd");
                        start_date = this.transDateToString(start_date, "yyyy-MM");
                        end_date = this.transDateToString(end_date, "yyyy-MM");
                        edu_stop_date = this.transDateToString(edu_stop_date, "yyyy-MM");
                        Sheet sheet2 = wb.getSheet(C_DECLARE_TYPE_CHILDEDU_SHEET);
                        Row row = sheet2.getRow(2 + i);
                        row.getCell(2).setCellValue(member_name);
                        row.getCell(3).setCellValue(id_type_desc);
                        row.getCell(4).setCellValue(id_number);
                        row.getCell(5).setCellValue(birthday);
                        row.getCell(6).setCellValue(nationality_desc);
                        row.getCell(7).setCellValue(edu_level_desc);
                        //fixme 此单元格有数据校验验证 为了可以将填写值赋值进去做此处理  不知改后国税局是否承认此模板
                        row.getCell(8).setCellFormula(null);
                        row.getCell(8).setCellValue(start_date);
                        row.getCell(9).setCellValue(end_date);
                        row.getCell(10).setCellValue(edu_stop_date);
                        row.getCell(11).setCellValue(edu_nationality_desc);
                        row.getCell(12).setCellValue(edu_institution);
                        row.getCell(13).setCellValue(deduct_proportion);
                        i++;
                    }
                }
                if (singlePeopleData.containsKey("0201") || singlePeopleData.containsKey("0202")) {//继续教育
                    Sheet sheet3 = wb.getSheet(C_DECLARE_TYPE_CONTINUEDU_SHEET);
                    if (singlePeopleData.containsKey("0201")) {//学历学位继续教育
                        Map specialItem = singlePeopleData.get("0201");
                        ArrayList<HashMap> subInfoList = (ArrayList) specialItem.get("sub_items");
                        int i = 0;
                        for (HashMap subInfo : subInfoList) {
                            String start_date = (String) subInfo.get("start_date");//当前继续教育起始时间
                            String end_date = (String) subInfo.get("end_date");//当前继续教育结束时间
                            start_date = this.transDateToString(start_date, "yyyy-MM");
                            end_date = this.transDateToString(end_date, "yyyy-MM");
                            String jx_edu_level = (String) subInfo.get("jx_edu_level");//继续教育阶段
                            String jx_edu_level_desc = jxEduLevelToDesc(jx_edu_level);//继续教育阶段
                            Row row3 = sheet3.getRow(3 + i);
                            row3.getCell(1).setCellValue(start_date);
                            row3.getCell(2).setCellValue(end_date);
                            row3.getCell(3).setCellValue(jx_edu_level_desc);
                            i++;
                        }
                    }
                    if (singlePeopleData.containsKey("0202")) { //职业资格继续教育
                        Map specialItem = singlePeopleData.get("0202");
                        ArrayList<HashMap> subInfoList = (ArrayList) specialItem.get("sub_items");
                        int i = 0;
                        for (HashMap subInfo : subInfoList) {
                            String post_type = (String) subInfo.get("post_type");//继续教育类型
                            String post_type_desc = postTypeToDesc(post_type);//继续教育类型
                            String start_date = (String) subInfo.get("start_date");//发证批准日期
                            start_date = this.transDateToString(start_date, "yyyy-MM-dd");
                            String post_certificate_name = (String) subInfo.get("post_certificate_name");//证书名称
                            String post_certificate_number = (String) subInfo.get("post_certificate_number");//证书编号
                            String post_certificate_org = (String) subInfo.get("post_certificate_org");//发证机关
                            Row row33 = sheet3.getRow(9 + i);
                            row33.getCell(1).setCellValue(post_type_desc);
                            row33.getCell(2).setCellValue(start_date);
                            row33.getCell(3).setCellValue(post_certificate_name);
                            row33.getCell(4).setCellValue(post_certificate_number);
                            row33.getCell(5).setCellValue(post_certificate_org);
                            i++;
                        }

                    }
                }
                if (singlePeopleData.containsKey("03")) {//住房租金
                    Map specialItem = singlePeopleData.get("03");
                    ArrayList<HashMap> subInfoList = (ArrayList) specialItem.get("sub_items");
                    int i = 0;
                    for (HashMap subInfo : subInfoList) {
                        String rent_house_province_desc = (String) subInfo.get("rent_house_province_desc");//主要工作省份
                        String rent_house_city_desc = (String) subInfo.get("rent_house_city_desc");//主要工作城市
                        String rent_house_type = (String) subInfo.get("rent_house_type");//类型
                        String rent_house_type_desc = rentHouseTypeToDesc(rent_house_type);//类型
                        String rent_house_name = (String) subInfo.get("rent_house_name");//出租方姓名
                        String rent_house_id_type_desc = (String) subInfo.get("rent_house_id_type_desc");//出租方证件类型
                        String rent_house_id_number = (String) subInfo.get("rent_house_id_number");//身份证件号码
                        String rent_house_address = (String) subInfo.get("rent_house_address");//住房坐落地址
                        String rent_house_no = (String) subInfo.get("rent_house_no");//住房租赁合同编号
                        String start_date = (String) subInfo.get("start_date");//租赁期起
                        String end_date = (String) subInfo.get("end_date");//租赁期止
                        start_date = this.transDateToString(start_date, "yyyy-MM");
                        end_date = this.transDateToString(end_date, "yyyy-MM");
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM");//租赁期起租赁期止 格式为2019-01
                        Date startDate = simpleDateFormat1.parse(start_date);
                        Date endDate = simpleDateFormat1.parse(end_date);
                        start_date = simpleDateFormat1.format(startDate);
                        end_date = simpleDateFormat1.format(endDate);
                        Sheet sheet4 = wb.getSheet(C_DECLARE_TYPE_HOUSINGRENT_SHEET);//住房租金支出
                        Row row4 = sheet4.getRow(3 + i);
                        row4.getCell(1).setCellValue(rent_house_province_desc);
                        row4.getCell(2).setCellValue(rent_house_city_desc);
                        row4.getCell(3).setCellValue(rent_house_type_desc);
                        row4.getCell(4).setCellValue(rent_house_name);
                        row4.getCell(5).setCellValue(rent_house_id_type_desc);
                        row4.getCell(6).setCellValue(rent_house_id_number);
                        row4.getCell(7).setCellValue(rent_house_address);
                        row4.getCell(8).setCellValue(rent_house_no);
                        row4.getCell(9).setCellValue(start_date);
                        row4.getCell(10).setCellValue(end_date);
                        i++;
                    }

                }
                if (singlePeopleData.containsKey("04")) {//住房贷款利息
                    Map specialItem = singlePeopleData.get("04");
                    ArrayList<HashMap> subInfoList = (ArrayList) specialItem.get("sub_items");
                    String house_address = (String) specialItem.get("house_address");//房屋坐落地址
                    String loan_self_flag = (String) specialItem.get("loan_self_flag");//本人是否是借款人
                    String loan_self_flag_desc = loanSelfFlagToDesc(loan_self_flag);//本人是否是借款人
                    String house_type = (String) specialItem.get("house_type");//房屋证书类型
                    String house_type_desc = houseTypeToDesc(house_type);//房屋证书类型
                    String house_number = (String) specialItem.get("house_number");//房屋证书号码
                    String loan_flat = (String) specialItem.get("loan_flat");//是否婚前各自首套贷款且婚后分别扣除50%
                    String loan_flat_desc = StringUtils.equalsIgnoreCase("1", loan_flat) ? "是" : "否";//是否婚前各自首套贷款且婚后分别扣除50%
                    Sheet sheet5 = wb.getSheet(C_DECLARE_TYPE_INTERESTEXPENSE_SHEET);
                    sheet5.getRow(1).getCell(2).setCellValue(house_address);
                    sheet5.getRow(2).getCell(1).setCellValue(loan_self_flag_desc);
                    sheet5.getRow(2).getCell(3).setCellValue(house_type_desc);
                    sheet5.getRow(2).getCell(5).setCellValue(house_number);
                    sheet5.getRow(2).getCell(7).setCellValue(loan_flat_desc);
                    int i = 0;
                    for (HashMap subInfo : subInfoList) {
                        String loan_type = (String) subInfo.get("loan_type");//贷款类型
                        String loan_type_desc = loanTypeToDesc(loan_type);//贷款类型
                        String loan_bank = (String) subInfo.get("loan_bank");//贷款银行
                        String loan_contract_no = (String) subInfo.get("loan_contract_no");//贷款合同编号
                        String start_date = (String) subInfo.get("start_date");//首次还款日期
                        start_date = this.transDateToString(start_date, "yyyy-MM-dd");
                        String loan_alloted_time = (String) subInfo.get("loan_alloted_time");//贷款期限

                        sheet5.getRow(4 + i).getCell(1).setCellValue(loan_type_desc);
                        sheet5.getRow(4 + i).getCell(3).setCellValue(loan_bank);
                        sheet5.getRow(4 + i).getCell(4).setCellValue(loan_contract_no);
                        sheet5.getRow(4 + i).getCell(5).setCellValue(start_date);
                        sheet5.getRow(4 + i).getCell(7).setCellValue(loan_alloted_time);
                        i++;
                    }
                }
                if (singlePeopleData.containsKey("06")) {//赡养老人
                    Map specialItem = singlePeopleData.get("06");
                    ArrayList<HashMap> sub_child_items = (ArrayList) specialItem.get("sub_child_items");//共同赡养人
                    ArrayList<HashMap> sub_old_items = (ArrayList) specialItem.get("sub_old_items");//被赡养人
                    String child_apportion = String.valueOf(specialItem.get("child_apportion"));//平摊人数
                    String apportion_type = (String) specialItem.get("apportion_type");//平摊方式
                    boolean selfChild = StringUtils.isEmpty(apportion_type);
                    String apportion_type_desc = apportionTypeToDesc(apportion_type);//平摊方式
                    Sheet sheet6 = wb.getSheet(C_DECLARE_TYPE_SUPPORTELDERLY_SHEET);
                    sheet6.getRow(1).getCell(2).setCellValue(selfChild ? "是" : "否");
                    sheet6.getRow(1).getCell(4).setCellValue(apportion_type_desc);
                    sheet6.getRow(1).getCell(6).getCellFormula();
                    sheet6.getRow(1).getCell(6).setCellValue(1000.00);
                    sheet6.getRow(1).getCell(6).setCellFormula("IF(C2=\"是\",2000,1000)");
                    int i = 0;
                    for (HashMap subInfo : sub_child_items) {//共同赡养人
                        String member_name = (String) subInfo.get("member_name");//姓名
                        String id_type_desc = (String) subInfo.get("id_type_desc");//身份证件类型
                        String id_number = (String) subInfo.get("id_number");//身份证件号码
                        String nationality_desc = (String) subInfo.get("nationality_desc");//国籍地区
                        sheet6.getRow(10 + i).getCell(1).setCellValue(member_name);
                        sheet6.getRow(10 + i).getCell(2).setCellValue(id_type_desc);
                        sheet6.getRow(10 + i).getCell(3).setCellValue(id_number);
                        sheet6.getRow(10 + i).getCell(4).setCellValue(nationality_desc);
                        i++;
                    }
                    i = 0;
                    for (HashMap subInfo : sub_old_items) {//被赡养人
                        String member_name = (String) subInfo.get("member_name");//姓名
                        String id_type_desc = (String) subInfo.get("id_type_desc");//身份证件类型
                        String id_number = (String) subInfo.get("id_number");//身份证件号码
                        String nationality_desc = (String) subInfo.get("nationality_desc");//国籍地区
                        String relation = (String) subInfo.get("relation");//与本人关系
                        String birthday = (String) subInfo.get("birthday");//出生日期
                        birthday = this.transDateToString(birthday, "yyyy-MM");
                        sheet6.getRow(4 + i).getCell(1).setCellValue(member_name);
                        sheet6.getRow(4 + i).getCell(2).setCellValue(id_type_desc);
                        sheet6.getRow(4 + i).getCell(3).setCellValue(id_number);
                        sheet6.getRow(4 + i).getCell(4).setCellValue(nationality_desc);
                        sheet6.getRow(4 + i).getCell(5).setCellValue(relation);
                        sheet6.getRow(4 + i).getCell(6).setCellValue(birthday);
                        i++;
                    }
                }
                String filename = path + File.separator + name + ".xls";
                fos = new FileOutputStream(filename);
                wb.write(fos);
                fos.close();
                is.close();
            }
            // 压缩文件 start
            String zipFileName = userView.getUserName() + ResourceFactory.getProperty("gz.zxdeclare.error.gsZxDeclare");
            ArrayList fileNames = new ArrayList(); // 存放文件名
            ArrayList files = new ArrayList(); // 存放文件对象
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + zipFileName);
            outputStream = new ZipOutputStream(fileOut);
            File rootFile = new File(path);
            listFile(rootFile, fileNames, files);
            byte[] data = new byte[2048];
            for (int loop = 0; loop < files.size(); loop++) {
                String a_fileName = (String) fileNames.get(loop);
                fileCount++;
                fileIn = new FileInputStream((File) files.get(loop));
                origin = new BufferedInputStream(fileIn, 2048);
                outputStream.putNextEntry(new ZipEntry(a_fileName));
                int count;
                while ((count = origin.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                }
                outputStream.setEncoding("UTF-8");
                outputStream.setComment(ResourceFactory.getProperty("gz.zxdeclare.label.chineseTest"));
                origin.close();
            }
            zipFileName = PubFunc.encrypt(zipFileName);
            zipFileName = SafeCode.encode(zipFileName);
            //生成zip包后删除临时文件 以免造成临时目录数据量过大
            this.deleteDir(new File(path));
            retrunMap.put("zipFileName", zipFileName);
            retrunMap.put("msg", ResourceFactory.getProperty("gz.zxdeclare.label.gsZxDeclareExportSuccess") + fileCount + ResourceFactory.getProperty("gz.zxdeclare.label.fileCount"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(fileOut);
            PubFunc.closeIoResource(outputStream);
            PubFunc.closeIoResource(fileIn);
            PubFunc.closeIoResource(origin);
            PubFunc.closeIoResource(fos);
        }
        return retrunMap;
    }

    private String eduLevelToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.childFirstlevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.childSecondlevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "03")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.childThreelevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "04")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.childFourlevelDesc");
        }
        return desc;
    }

    private String jxEduLevelToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFirstlevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.jxEduSecondlevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "03")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.jxEduThreelevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "04")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFourlevelDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "05")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.jxEduFiveslevelDesc");
        }
        return desc;
    }

    private String postTypeToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.postTypeFirstDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.postTypeSecondDesc");
        }
        return desc;
    }

    private String rentHouseTypeToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.personal");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.Org");
        }
        return desc;
    }

    private String loanSelfFlagToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "1")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.yes");
        } else if (StringUtils.equalsIgnoreCase(code, "2")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.no");
        }
        return desc;
    }

    private String houseTypeToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.houseFirstTypeDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.houseSecondTypeDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "03")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.houseThreeTypeDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "04")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.houseFourTypeDesc");
        }
        return desc;
    }

    private String loanTypeToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.loanFirstTypeDesc");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.loanSecondTypeDesc");
        }
        return desc;
    }

    private String apportionTypeToDesc(String code) {
        String desc = "";
        if (StringUtils.equalsIgnoreCase(code, "01")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.syOldFirstType");
        } else if (StringUtils.equalsIgnoreCase(code, "02")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.syOldSecondType");
        } else if (StringUtils.equalsIgnoreCase(code, "03")) {
            desc = ResourceFactory.getProperty("gz.zxdeclare.label.syOldThreeType");
        }
        return desc;
    }

    /**
     * 获取专项申报模板首页所需数据
     *
     * @return
     * @author wangz
     */
    private Map<String, Map<String, String>> getHomeData(Map<String, ArrayList> nbaseA0100) {
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        Map<String, Map<String, String>> peopleHomeInfo = new HashMap<String, Map<String, String>>();
        try {
            //1.获取指标对应关系
            String fieldRelation = this.getRelation();
            if (StringUtils.isBlank(fieldRelation)) {
                return peopleHomeInfo;
            }
            JSONArray fieldRelationArray = JSONArray.fromObject(fieldRelation);
            Map<String, Map<String, String>> relationMap = new HashMap();
            Map<String, List> fieldSourceFieldMap = new HashMap<String, List>();
            for (int i = 0; i < fieldRelationArray.size(); i++) {
                JSONObject fieldObject = fieldRelationArray.getJSONObject(i);
                String fieldSetId = fieldObject.getString("fieldSetId");
                String sourceField = fieldObject.getString("sourceField");
                if (StringUtils.isBlank(fieldObject.getString("itemId"))) {
                    continue;
                }
                Map temp = new HashMap();
                temp.put("fieldSetId", fieldSetId);
                temp.put("itemId", fieldObject.getString("itemId"));
                relationMap.put(sourceField, temp);
                if (!fieldSourceFieldMap.containsKey(fieldSetId)) {
                    List fieldSetList = new ArrayList();
                    fieldSetList.add(sourceField);
                    fieldSourceFieldMap.put(fieldSetId, fieldSetList);
                } else {
                    fieldSourceFieldMap.get(fieldSetId).add(sourceField);
                }
            }

            Set<String> keySet = fieldSourceFieldMap.keySet();
            Set<String> nbaseSet = nbaseA0100.keySet();
            int s = 0;
            for (String fieldsetid : keySet) {
                StringBuffer sql = new StringBuffer();
                List<String> sourceFieldList = fieldSourceFieldMap.get(fieldsetid);
                if (StringUtils.equalsIgnoreCase(fieldsetid, "A01")) {
                    for (String nbase : nbaseSet) {
                        sql.append("select ");
                        sql.append("'").append(nbase).append("' as nbase,");
                        sql.append("a0100");
                        if (sourceFieldList.size() > 0) {
                            sql.append(",");
                        }
                        for (int g = 0; g < sourceFieldList.size(); g++) {
                            String sourceField = sourceFieldList.get(g);
                            String itemid = relationMap.get(sourceField).get("itemId");
                            sql.append(itemid).append(" as ").append(sourceField);
                            if (g < sourceFieldList.size() - 1) {
                                sql.append(",");
                            }
                        }
                        sql.append(" from ").append(nbase).append(fieldsetid);
                        sql.append(" where a0100 in(");
                        ArrayList a0100List = nbaseA0100.get(nbase);
                        //todo in条件超过1000个未分组处理
                        for (int i = 0; i < a0100List.size(); i++) {
                            sql.append(a0100List.get(i));
                            if (i < a0100List.size() - 1) {
                                sql.append(",");
                            }
                        }
                        sql.append(")");
                        if (s < nbaseSet.size() - 1) {
                            sql.append("union all");
                        }
                    }
                } else {
                    for (String nbase : nbaseSet) {
                        sql.append("select ");
                        sql.append("'").append(nbase).append("' as nbase,");
                        sql.append(nbase.substring(0, 1));
                        sql.append(".a0100,");
                        sql.append(nbase.substring(0, 1));
                        sql.append(".i9999");
                        if (sourceFieldList.size() > 0) {
                            sql.append(",");
                        }
                        for (int g = 0; g < sourceFieldList.size(); g++) {
                            String sourceField = sourceFieldList.get(g);
                            String itemid = relationMap.get(sourceField).get("itemId");
                            sql.append(itemid).append(" as ").append(sourceField);
                            if (g < sourceFieldList.size() - 1) {
                                sql.append(",");
                            }
                        }
                        sql.append(" from ").append(nbase).append(fieldsetid).append(" ");
                        sql.append(nbase.substring(0, 1)).append(",");
                        sql.append("(select Max(i9999) as I9999,A0100 from (select  i9999,a0100 from ").append(nbase).append(fieldsetid);
                        sql.append(" where a0100 in(");
                        ArrayList a0100List = nbaseA0100.get(nbase);
                        for (int i = 0; i < a0100List.size(); i++) {
                            sql.append(a0100List.get(i));
                            if (i < a0100List.size() - 1) {
                                sql.append(",");
                            }
                        }
                        sql.append(")) ").append(nbase.substring(1, 2)).append(" group by a0100)").append(" ").append(nbase);
                        sql.append(" where ").append(nbase.substring(0, 1)).append(".a0100").append("=").append(nbase).append(".a0100");
                        sql.append(" and ").append(nbase.substring(0, 1)).append(".i9999").append("=").append(nbase).append(".i9999");
                        if (s < nbaseSet.size() - 1) {
                            sql.append("union all");
                        }
                    }
                }
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    String a0100 = rs.getString("a0100");
                    String nbase = rs.getString("nbase");

                    if (!peopleHomeInfo.containsKey(nbase + a0100)) {
                        Map tempValue = new HashMap();
                        for (String sourceField : sourceFieldList) {
                            String sourceFieldValue = rs.getString(sourceField);
                            String itemid = relationMap.get(sourceField).get("itemId");
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                            String codesetid = fieldItem.getCodesetid();
                            if ((!StringUtils.equalsIgnoreCase("0", codesetid) && !StringUtils.isEmpty(codesetid))) {//代表是代码型指标
                                sourceFieldValue = AdminCode.getCodeName(codesetid, sourceFieldValue);
                            }
                            tempValue.put(sourceField, sourceFieldValue);
                        }
                        peopleHomeInfo.put(nbase + a0100, tempValue);
                    } else {
                        for (String sourceField : sourceFieldList) {
                            String sourceFieldValue = rs.getString(sourceField);
                            String itemid = relationMap.get(sourceField).get("itemId");
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                            String codesetid = fieldItem.getCodesetid();
                            if ((!StringUtils.equalsIgnoreCase("0", codesetid) && !StringUtils.isEmpty(codesetid))) {//代表是代码型指标
                                sourceFieldValue = AdminCode.getCodeName(codesetid, sourceFieldValue);
                            }
                            peopleHomeInfo.get(nbase + a0100).put(sourceField, sourceFieldValue);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return peopleHomeInfo;
    }

    /**
     * 封装压缩文件信息
     *
     * @param parentFile 父目录
     * @param nameList   父目录下的所有文件名集合
     * @param fileList   父目录下所有文件的集合
     * @author wangz
     */
    private void listFile(File parentFile, List nameList, List fileList) {
        if (parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (int loop = 0; loop < files.length; loop++) {
                listFile(files[loop], nameList, fileList);
            }
        } else {
            fileList.add(parentFile);
            nameList.add(parentFile.getName());
        }
    }

    /**
     * 递归删除指定目录下的文件
     *
     * @param dir 指定目录
     * @author wangz
     */
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
        }
        dir.delete();
    }

    /**
     * 转换日期格式
     *
     * @return 转换后的日期字符串
     */
    private String transDateToString(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date tempDate = null;
        String dateString = "";
        if (StringUtils.isEmpty(date)) {
            return "";
        }
        try {
            tempDate = simpleDateFormat.parse(date);
            dateString = simpleDateFormat.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 处理不同类型的单元格的值
     *
     * @param cell 单元格值
     * @return
     * @author wangz
     */
    private String getCellValue(Cell cell) {
        String value = "";
        if (cell == null) {
            return "";
        }
        if (cell.getCellTypeEnum() == CellType.STRING) {
            value = cell.getStringCellValue();
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            //如果为时间格式的内容
            if (HSSFDateUtil.isCellDateFormatted(cell)) {//判断单元格内容是否为日期型
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//存到数据库时统一按年月日的格式
                value = sdf.format(cell.getDateCellValue()).toString();
            } else {
                double numberValue = cell.getNumericCellValue();
                CellStyle style = cell.getCellStyle();
                DecimalFormat format = new DecimalFormat();
                String temp = style.getDataFormatString();
                // 单元格设置成常规
                if ("General".equals(temp)) {
                    format.applyPattern("#");
                }
                value = format.format(numberValue);
            }
        } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            value = String.valueOf(cell.getBooleanCellValue()).trim();
        } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
            value = String.valueOf(cell.getCellFormula());
        } else if (cell.getCellTypeEnum() == CellType.BLANK) {
            value = "";
        } else {
            value = "";
        }
        return value;
    }

    /**
     * @param year     抵扣年份
     * @param date     日期
     * @param dateType 区分起始日期和结束日期
     * @return 有效日期
     */
    private String getEffectiveDate(String year, String date, String dateType) {
        String effectiveDate = "";
        if (StringUtils.isEmpty(date)) {
            return effectiveDate;
        }
        String[] dates = date.split("-");
        String tempYear = dates[0];//年
        String tempMonth = dates[1];//月
        if (StringUtils.equalsIgnoreCase(year, tempYear)) {//如果抵扣年度和日期一样
            effectiveDate = year + "-" + tempMonth + "-01";
            if (StringUtils.equalsIgnoreCase("endDate", dateType)) {
                Calendar cal = Calendar.getInstance();
                //设置年份
                cal.set(Calendar.YEAR, Integer.valueOf(year));
                //设置月份
                cal.set(Calendar.MONTH, Integer.valueOf(tempMonth) - 1);
                //获取某月最大天数
                int lastDay = cal.getActualMaximum(Calendar.DATE);
                effectiveDate = year + "-" + tempMonth + "-" + lastDay;
            }
        } else if (Integer.valueOf(tempYear) < Integer.valueOf(year)) {//如果日期大于抵扣年份
            if (StringUtils.equalsIgnoreCase("startDate", dateType)) {
                effectiveDate = year + "-01-01";
            } else if (StringUtils.equalsIgnoreCase("endDate", dateType)) {
                effectiveDate = year + "-12-31";
            }
        } else if (Integer.valueOf(tempYear) > Integer.valueOf(year)) {
            if (StringUtils.equalsIgnoreCase("endDate", dateType)) {
                effectiveDate = year + "-12-31";
            }
        }
        return effectiveDate;
    }

    /**
     * 获取guidkey对应人员的非归档记录的申报记录信息（导入数据专用）
     *
     * @param guidkey
     * @return
     * @author wangz
     */
    private Map getDeclareIdByGuidkey(String guidkey) {
        Map map = new HashMap();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        sql.append("select id,declare_type,cuntin_edu_type from declare_infor where approve_state <>'05' and ");
        sql.append("guidkey = ");
        sql.append("'" + guidkey + "'");
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String declare_id = rs.getString("id");
                String declare_type = rs.getString("declare_type");
                if (StringUtils.equalsIgnoreCase(declare_type, "02")) {
                    String cuntin_edu_type = rs.getString("cuntin_edu_type");
                    map.put(declare_type + cuntin_edu_type, declare_id);
                } else {
                    map.put(declare_type, declare_id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return map;
    }

    /**
     * @param startDate           开始日期
     * @param loanAllotedTimeTemp 贷款期限
     * @return 结束日期
     */
    private String getEndDate(String startDate, String loanAllotedTimeTemp) {
        String returnEndDate = "";
        Date endDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            endDate = simpleDateFormat.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);//设置起时间
            cal.add(Calendar.MONTH, Integer.valueOf(loanAllotedTimeTemp) - 1);
            endDate = cal.getTime();
            returnEndDate = simpleDateFormat.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnEndDate;
    }

    /**
     * 获取当前人员权限内的子集
     *
     * @param userView
     * @return
     */
    public List getPrivFieldSetList(UserView userView) {
        ArrayList fieldsetlist = userView.getPrivFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);//获取权限子集
        log.error("zxfjsb"+fieldsetlist.toString());
        ArrayList fieldSetDataList = new ArrayList();
        for (int i = 0; i < fieldsetlist.size(); i++) {
            HashMap fieldsetMap = new HashMap();
            FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
            if ("0".equalsIgnoreCase(fieldset.getUseflag()))
                continue;
            fieldsetMap.put("fieldsetid", fieldset.getFieldsetid());
            fieldsetMap.put("fieldsetdesc", fieldset.getCustomdesc());
            fieldSetDataList.add(fieldsetMap);
        }
        log.error("zxfjsbfield"+fieldSetDataList.toString());
        return fieldSetDataList;
    }

    /**
     * 根据fieldsetid获取指定子集权限内的指标集合
     *
     * @param userView
     * @param fieldSetId
     * @return
     */
    public List getPricFieldByFieldSetId(UserView userView, String fieldSetId) {
        List fieldListData = new ArrayList();
        List fieldList = userView.getPrivFieldList(fieldSetId);
        for (int i = 0; i < fieldList.size(); i++) {
            Map itemMap = new HashMap();
            FieldItem fieldItem = (FieldItem) fieldList.get(i);
            itemMap.put("valueitemid", fieldItem.getItemid());
            itemMap.put("valuedesc", fieldItem.getItemdesc());
            fieldListData.add(itemMap);
        }
        Map blankMap = new HashMap();
        blankMap.put("valueitemid", " ");
        blankMap.put("valuedesc", "  ");

        fieldListData.add(0, blankMap);
        return fieldListData;
    }

    /**
     * 获取stepview currentIndex
     * @return
     */
    public String getCurrentIndex() {
        String currentIndex = "0";
        String isExitsFile = "true";
        String isNotTemplateFile = "false";
        //1.判断模版文件是否存在
        String fileName = ResourceFactory.getProperty("gz.zxdeclare.error.zxDeclareTemplate");
        String path = PubFunc.decrypt(this.getTemplateFilePath()) + fileName;
        File templateFile = new File(path + ".xls");
        if (!templateFile.exists()) {
            templateFile = new File(path + ".xlsx");
            if (!templateFile.exists()) {
                isExitsFile = "false";
                return "0";
            }
        }
        InputStream is = null;
        Workbook wb = null;
        try {
            is = new FileInputStream(templateFile);
            wb = new HSSFWorkbook(is);
            Sheet mainSheet = wb.getSheet(C_DECLARE_TYPE_MAIN_SHEET);//首页
            Sheet chileduSheet = wb.getSheet(C_DECLARE_TYPE_CHILDEDU_SHEET);//子女教育
            Sheet continueduSheet = wb.getSheet(C_DECLARE_TYPE_CONTINUEDU_SHEET);//继续教育
            Sheet housingrentSheet = wb.getSheet(C_DECLARE_TYPE_HOUSINGRENT_SHEET);//住房租金
            Sheet interestexpenseSheet = wb.getSheet(C_DECLARE_TYPE_INTERESTEXPENSE_SHEET);//贷款利息
            Sheet supportelderlySheet = wb.getSheet(C_DECLARE_TYPE_SUPPORTELDERLY_SHEET);//赡养老人
            if (mainSheet == null || chileduSheet == null || continueduSheet == null || housingrentSheet == null || interestexpenseSheet == null || supportelderlySheet == null) {
                isNotTemplateFile = "true";
                templateFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(is);
            PubFunc.closeIoResource(wb);
        }
        boolean blank = false;
        String relationStr = this.getRelation();
        if(StringUtils.isEmpty(relationStr)){
            relationStr = "[]";
            blank = true;
        }
        JSONArray relation = JSONArray.fromObject(relationStr);
        for (int i = 0; i < relation.size(); i++) {
            JSONObject temp = relation.getJSONObject(i);
            if (StringUtils.equalsIgnoreCase(temp.getString("sourceField"), "taxpayerIDType") || StringUtils.equalsIgnoreCase(temp.getString("sourceField"), "taxpayerIDNumber") || StringUtils.equalsIgnoreCase(temp.getString("sourceField"), "spouseSituation")) {  //必填项判断其值是否为null或者为空
                if (StringUtils.isEmpty(temp.getString("itemId")) || StringUtils.isEmpty(temp.getString("fieldSetId"))) {
                    blank = true;
                }
            }
        }
        if (StringUtils.equalsIgnoreCase("false", isExitsFile) || StringUtils.equalsIgnoreCase("true", isNotTemplateFile)) {//模板文件不存在或者不是官方模板
            currentIndex = "0";
        }else{
            if (blank) {
                currentIndex = "1";
            }else{
                currentIndex = "2";
            }
        }
        return currentIndex;
    }
    public String isExitesTemplateFile(){
        String isExitsFile = "true";
        //1.判断模版文件是否存在
        String fileName = ResourceFactory.getProperty("gz.zxdeclare.error.zxDeclareTemplate");
        String path = PubFunc.decrypt(this.getTemplateFilePath()) + fileName;
        File templateFile = new File(path + ".xls");
        if (!templateFile.exists()) {
            templateFile = new File(path + ".xlsx");
            if (!templateFile.exists()) {
                isExitsFile = "false";
            }
        }
        return isExitsFile;
    }

}
