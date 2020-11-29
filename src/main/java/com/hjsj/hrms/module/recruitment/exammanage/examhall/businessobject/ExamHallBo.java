package com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class ExamHallBo {
    Connection conn;
    ContentDAO dao;
    UserView userview;

    public ExamHallBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }

    /**
     * @author lis
     * @Description: 获取列头、表格渲染
     * @date 2015-11-3
     * @return ArrayList<ColumnsInfo>
     */
    public ArrayList<ColumnsInfo> getColumnList() {
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        try {
            // 考场号
            ColumnsInfo idColumn = getColumnsInfo("idx", "考场id", 60, "N");
            idColumn.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(idColumn);
            // 考场号
            ColumnsInfo hallId = getColumnsInfo("hall_id", "考场号", 60, "A");
            hallId.setOrdertype("1");
            columnTmp.add(hallId);

            // 考场名称
            ColumnsInfo hallName = getColumnsInfo("hall_name", "考场名称", 180, "A"); 
            hallName.setRendererFunc("examhall_me.hallNameRenderer");
            hallName.setLocked(true);
            columnTmp.add(hallName);

            // 考场地址
            ColumnsInfo hallAddres = getColumnsInfo("hall_address", "地址", 200, "A");
            columnTmp.add(hallAddres);

            // 招聘批次
            ColumnsInfo batchId = getColumnsInfo("batch_name", "招聘批次", 180, "A");
            columnTmp.add(batchId);
            
            // 招聘批次标号
            ColumnsInfo batch_Id = getColumnsInfo("batch_id", "招聘批次编号", 180, "A");
            batch_Id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnTmp.add(batch_Id);

            // 所属单位
            ColumnsInfo b0110 = getColumnsInfo("b0110", "所属单位", 180, "A");
            b0110.setCodesetId("UN");
            columnTmp.add(b0110);

            // 考试日期
            ColumnsInfo examDate = getColumnsInfo("exam_date", "考试日期", 150, "D");
            examDate.setColumnLength(10);
            columnTmp.add(examDate);

            // 考试时间
            ColumnsInfo examTime = getColumnsInfo("exam_time", "考试时间", 146, "A");
            columnTmp.add(examTime);

            // 座位数
            ColumnsInfo seatNum = getColumnsInfo("seat_num", "座位数", 94, "N");
            columnTmp.add(seatNum);

            // 考生人数
            ColumnsInfo peopleNum = getColumnsInfo("people_num", "考生人数", 94, "N");
            peopleNum.setRendererFunc("examhall_me.peopleNumRenderer");
            columnTmp.add(peopleNum);

            // 剩余数
            ColumnsInfo surplusNum = getColumnsInfo("surplus_num", "剩余数", 94, "N");
            surplusNum.setReadOnly(true);
            columnTmp.add(surplusNum);
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
        return columnTmp;
    }

    /**
     * 列头ColumnsInfo对象初始化
     * 
     * @param columnId
     *            id
     * @param columnDesc
     *            名称
     * @param columnDesc
     *            显示列宽
     * @param type
     *            列的数据类型
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位

        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }

    /**
     * 查询功能按钮
     * 
     * @return
     */
    public ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();
        ArrayList menuList = new ArrayList();

//        if (userview.isSuper_admin() || userview.hasTheFunction("311080501")) {
//            LazyDynaBean oneBean = new LazyDynaBean();
//            menuList.add(this.getMenuBean("导出Excel", "", "examhall_me.exportData",
//                    "/module/recruitment/image/export.gif", new ArrayList()));
//            oneBean.set("menu", menuList);
//            String menu = this.getMenuStr("输出", menuList);
//            buttonList.add(menu);
//        }
//
        if (userview.isSuper_admin() || userview.hasTheFunction("311080502"))
            buttonList.add(newButton("新增考场", null, "examhall_me.insert", null, "true"));
        if (userview.isSuper_admin() || userview.hasTheFunction("311080504"))
            buttonList.add(newButton("删除考场", null, "examhall_me.dele", null, "true"));

        buttonList.add("-");
        if (userview.isSuper_admin() || userview.hasTheFunction("311080505"))
            buttonList.add(newButton("考场分派", "assignExamHall", "examhall_me.assignExamHall", null, "true"));
        
        //buttonList.add(new ButtonInfo(ButtonInfo.BUTTON_SPACE));//分隔符，使查询框靠右
        // 加搜索条
//        buttonList.add(new ButtonInfo("<div id='fastsearch'> </div>"));
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("ZP0000002501");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入考场号、考场名称、招聘批次或地址...");
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 递归生成功能导航菜单的json串
     * 
     * @param name
     *            菜单名
     * @param list
     *            菜单内容
     * @return
     */
    public String getMenuStr(String name, ArrayList list) {
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
                    if (bean.get("xtype") != null && "datepicker".equalsIgnoreCase(bean.get("xtype").toString())) {// 时间控件单独处理一下
                                                                                                                   // 方法GzGlobal.aaa(picker,
                                                                                                                   // date)这样写
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
     * 生成菜单的bean
     * 
     * @param text
     *            名称
     * @param id
     *            主键
     * @param handler
     *            触发事件
     * @param icon
     *            图标
     * @return
     */
    public LazyDynaBean getMenuBean(String text, String id, String handler, String icon, ArrayList list) {
        LazyDynaBean bean = new LazyDynaBean();
        try {
            if (text != null && text.length() > 0)
                bean.set("text", text);
            if (id != null && id.length() > 0)
                bean.set("id", id);
            if (icon != null && icon.length() > 0)
                bean.set("icon", icon);
            if (handler != null && handler.length() > 0) {
                if (list != null && list.size() > 0) {
                    bean.set("menu", list);
                } else {
                    bean.set("handler", handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * @author lis
     * @Description: 生成按钮
     * @date 2015-11-3
     * @param text
     *            按钮显示文字
     * @param id
     *            按钮id
     * @param handler
     *            按钮触发方法
     * @param icon
     *            按钮图标
     * @param getdata
     *            事件触发时是否获取选中数据
     * @return
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
     * @author lis
     * @Description: 根据查询内容生成sql条件语句
     * @date 2015-11-3
     * @param valuesList
     *            查询内容
     * @return
     */
    public String getConditon(ArrayList<String> valuesList) {
        StringBuffer buf = new StringBuffer("");
        try {
            boolean isMul = false;//是否是多个值
            if(valuesList.size()>1)
                isMul = true;
            // 快速查询
            for (int i = 0; i < valuesList.size(); i++) {
                String queryVal = valuesList.get(i);
                queryVal = SafeCode.decode(queryVal);
                if (i == 0) {
                    buf.append(" and ");
                    if(isMul)
                        buf.append("((myGridData.hall_id like ");
                    else buf.append("(myGridData.hall_id like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.hall_name like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.batch_name like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.hall_address like ");
                    buf.append("'%" + queryVal + "%') ");
                } else {
                    buf.append(" and ");
                    buf.append("(myGridData.hall_id like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.hall_name like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.batch_name like ");
                    buf.append("'%" + queryVal + "%'");

                    buf.append(" or myGridData.hall_address like ");
                    if (isMul)
                        buf.append("'%" + queryVal + "%') ");
                }
            }
            if (isMul)
            	buf.append(")");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
        return buf.toString();
    }

    public String exportFile() throws GeneralException {
        String fileName = "";
        try {
            TableDataConfigCache tableCache = (TableDataConfigCache) userview.getHm().get("zp_exam_hall_id_001");
            ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();

            // 只导出页面显示的字段
            StringBuffer setFields = new StringBuffer("");
            for (ColumnsInfo columnsInfo : (ArrayList<ColumnsInfo>) tableCache.getTableColumns()) {
                if (columnsInfo.getLoadtype() == 1) {
                    columns.add(columnsInfo);
                    if ("idx".equals(columnsInfo.getColumnId())) {
                        setFields.append(",id as idx");
                    } else if ("surplus_num".equals(columnsInfo.getColumnId())) {
                        setFields.append(",seat_num-people_num as surplus_num");
                    } else {
                        setFields.append("," + columnsInfo.getColumnId());
                    }
                }
            }
            String tableSql = tableCache.getTableSql();// 取得sql
            String[] sql = tableSql.split("where");
            String sortSql = tableCache.getSortSql();// 取得oder by
            String seletSql = "select "
                    + setFields.toString().substring(1)
                    + " from (select batch_id, id as idx,hall_id,hall_name,hall_address,b0110,exam_date,exam_time,seat_num,people_num,seat_num-people_num as surplus_num,z0103 as batch_name from zp_exam_hall left join z01 on Z0101=batch_id) where"
                    + sql[1] + sortSql;

            ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
            fileName = this.userview.getUserName() + "_zp_exam_hall" + ".xls";
            excelUtil.exportExcelByColum(fileName, "zp_exam_hall", null, columns, seletSql, null, 0);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fileName;
    }

    /**
     * 
     * @Title:getAffiliationList
     * @Description：获取班次下拉数据
     * @author liuyang
     * @return
     * @throws GeneralException
     */
    public ArrayList getAffiliationList() throws GeneralException {
        ArrayList affiliationList = new ArrayList();
        RowSet rs  = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            ArrayList res = new ArrayList();
            RecruitPrivBo rpb = new RecruitPrivBo();

            StringBuffer sql = new StringBuffer(" select z0101 ,z0103  ");
            sql.append(" from Z01");
            sql.append(" where 1=1");
            sql.append(" and ").append(" z0129 ='04' ");
            sql.append(" and ").append(
                    rpb.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD));
            sql.append(" order by z0101 desc");
            rs = dao.search(sql.toString());
            HashMap map = new HashMap();
            affiliationList.add(map);
            while (rs.next()) {
                map = new HashMap();
                map.put("id", rs.getString("z0101"));
                map.put("des", rs.getString("z0103"));
                affiliationList.add(map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }

        return affiliationList;
    }

    /**
     * 
     * @Title:examHallAdd
     * @Description：数据添加
     * @author liuyang
     * @param addList
     *            已组装的数据
     * @throws Exception
     */
    public String addExamHall(ArrayList addList) throws Exception {
        String tip = "1";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            IDGenerator idg = new IDGenerator(2, this.conn);
            String file_id = idg.getId("zp_exam_hall.id");
            ArrayList list = new ArrayList();
            list.add(file_id);
            for (int i = 0; i < addList.size(); i++) {
                    list.add(addList.get(i));
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            Date date = DateUtils.getTimestamp(time, "yyyy-MM-dd HH:mm:ss");
            list.add(0);
            list.add(date);
            list.add(userview.getUserName());
            StringBuffer sqlStr = new StringBuffer("");
            sqlStr.append(" INSERT  INTO zp_exam_hall ");
            sqlStr.append("(");
            sqlStr.append("id").append(",");
            sqlStr.append("hall_id").append(",");
            sqlStr.append("hall_name").append(",");
            sqlStr.append("batch_id").append(",");
            sqlStr.append("hall_address").append(",");
            sqlStr.append("seat_num").append(",");
            sqlStr.append("exam_date").append(",");
            sqlStr.append("exam_time").append(",");
            sqlStr.append("B0110").append(",");
            sqlStr.append("people_num").append(",");
            sqlStr.append("create_time").append(",");
            sqlStr.append("Create_user");
            sqlStr.append(")");
            sqlStr.append(" VALUES ");
            sqlStr.append(" (?,?,?,?,?,?,?,?,?,?,?,?) ");
            dao.insert(sqlStr.toString(), list);
        } catch (Exception e) {
            tip = "0";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tip;
    }

    /**
     * 
     * @Title:editList
     * @Description：编辑前查看时 数据获取
     * @author liuyang
     * @param id
     *            主键id
     * @return 编辑前查看时 数据
     * @throws GeneralException
     */
    public ArrayList editList(String id) throws GeneralException {
        ArrayList returnList = new ArrayList();
        RowSet rs  = null;
        try {
            StringBuffer sqlStr = new StringBuffer("");
            sqlStr.append(" select id,hall_name,Hall_id,batch_id,hall_address,seat_num,exam_date,exam_time,B0110");
            sqlStr.append(" from zp_exam_hall");
            sqlStr.append(" where id=?");
            ArrayList values = new ArrayList();
            values.add(id);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sqlStr.toString(), values);
            while (rs.next()) {
                returnList.add(rs.getString("id"));
                returnList.add(rs.getString("Hall_id"));
                returnList.add(rs.getString("hall_name"));
                returnList.add(rs.getString("batch_id"));
                returnList.add(rs.getString("hall_address"));
                returnList.add(rs.getString("seat_num"));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String time ="";
                if(rs.getDate("exam_date")!=null)
                time = format.format(rs.getDate("exam_date"));
                returnList.add(time);
                returnList.add(getOrganization(rs.getString("B0110")));

                if (StringUtils.isNotEmpty(rs.getString("exam_time"))) {
                    String exam_time = rs.getString("exam_time");
                    String[] timeArange = exam_time.split("-");
                    String[] begintime = timeArange[0].split(":");
                    String[] endtime = timeArange[1].split(":");

                    if (begintime.length > 0) {
                        returnList.add(begintime[0]);
                        if (begintime.length > 1)
                            returnList.add(begintime[1]);
                    }

                    if (endtime.length > 0) {
                        returnList.add(endtime[0]);
                        if (endtime.length > 1)
                            returnList.add(endtime[1]);
                    }
                }
                returnList.add(rs.getString("B0110"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return returnList;
    }

    /**
     * 
     * @Title:examHallEdit
     * @Description：修改数据
     * @author liuyang
     * @param addList
     *            修改数据list
     * @throws GeneralException
     */
    public String updateExamHall(ArrayList addList) throws GeneralException {
        String tip = "1";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList list = new ArrayList();
            ArrayList asignList = new ArrayList();
            for (int i = 0; i < addList.size() - 1; i++) {
                list.add(addList.get(i));
            }
            list.add(addList.get(8));
            StringBuffer sqlStr = new StringBuffer("");
            sqlStr.append("  UPDATE   ");
            sqlStr.append(" zp_exam_hall   ");
            sqlStr.append("  SET  ");
            sqlStr.append(" hall_id=?,");
            sqlStr.append(" hall_name=?,");
            sqlStr.append(" batch_id=?,");
            sqlStr.append(" hall_address=?,");
            sqlStr.append(" seat_num=?,");
            sqlStr.append(" exam_date=?,");
            sqlStr.append(" exam_time=?,");
            sqlStr.append(" B0110 =?");
            sqlStr.append(" where id =?");
            dao.update(sqlStr.toString(), list);
            
            asignList.add(list.get(0));
            asignList.add(list.get(1));
            asignList.add(list.get(8));
            StringBuffer asignSqlStr = new StringBuffer("");
            asignSqlStr.append("  UPDATE  zp_exam_assign ");
            asignSqlStr.append("  SET  ");
            asignSqlStr.append(" hall_id=?,");
            asignSqlStr.append(" hall_name=?");
            asignSqlStr.append(" where exam_hall_id=?");
            dao.update(asignSqlStr.toString(), asignList);
            
        } catch (Exception e) {
            tip = "0";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tip;
    }

    /**
     * 
     * @Title:isExis
     * @Description：判断相同班次内是否存在相同考场号
     * @author liuyang
     * @param batch_id
     *            班次id
     * @param Hall_id
     *            考场号
     * @param id
     * @return
     * @throws GeneralException
     */
    public ArrayList checkIdPeole(String batch_id, String Hall_id, String id, String sitNumber) throws GeneralException {

        ArrayList list = new ArrayList();
        ArrayList listIdPeole = new ArrayList();
        RowSet rs = null;
        String bl = "0";
        String ple = "0";
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            StringBuffer sql = new StringBuffer(" select id,people_num ");
            sql.append(" from zp_exam_hall ");
            sql.append(" where batch_id = ?");
            sql.append(" and Hall_id = ?");
            list.add(batch_id);
            list.add(Hall_id);
            rs = dao.search(sql.toString(), list);

            while (rs.next()) {
                if (StringUtils.isNotEmpty(id)) {
                    if (id.equals(rs.getString("id"))) {
                        bl = "2";
                    } else {
                        bl = "1";
                    }
                } else {
                    bl = "1";
                }
                listIdPeole.add(bl);
                String peopleNum = (String)rs.getString("people_num");
                int sitNum = Integer.valueOf(sitNumber).intValue();//座位数
                int pelNum = Integer.valueOf(peopleNum).intValue();//考生人数
                if(sitNum < pelNum){
                	ple = "1";
                }else{
                	ple = "2";
                }
                listIdPeole.add(ple);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return listIdPeole;
    }

    /**
     * 
     * @Title:getOrganization
     * @Description：获取所属单位名称
     * @author liuyang
     * @param b0110
     * @return 所属单位名称
     * @throws GeneralException
     */
    private String getOrganization(String b0110) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        String codeitemdesc = "";
        RowSet rs = null;
        try {
            StringBuffer stbf = new StringBuffer("");
            stbf.append(" SELECT codeitemdesc FROM ");
            stbf.append(" organization ");
            stbf.append(" WHERE ");
            stbf.append(" codesetid = ? ");
            stbf.append(" and ").append(" codeitemid = ? ");

            ArrayList values = new ArrayList();
            values.add("UN");
            values.add(b0110);

            rs = dao.search(stbf.toString(), values);
            if (rs.next())
                codeitemdesc = rs.getString("codeitemdesc");
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return codeitemdesc;
    }

    /**
     * 
     * @Title:query
     * @Description：添加时 添加必要时间数值
     * @author liuyang
     * @return 包含时间数据的初始值
     */
    public ArrayList query() {
        ArrayList returnList = new ArrayList();
        returnList.add("");
        returnList.add("");
        returnList.add("");
        returnList.add("");
        returnList.add("");
        returnList.add("");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(new Date());
        returnList.add(time);
        returnList.add("");
        returnList.add("00");
        returnList.add("00");
        returnList.add("23");
        returnList.add("59");
        returnList.add("");
        return returnList;
    }
    /**
     * 
     * @Title:examEditRight
     * @Description：
     * @author liuyang
     * @param ids 需要过滤的id
     * @param type 删除或者修改情况
     * @return 删除时返回 不可删除的 会议名 、可删除的会议id
     *         修改时返回是否具有权限 tip 1-有权限 0-无权限 2-全无权限删除
     * @throws GeneralException
     */
    public HashMap examEditRight(String ids, String type) throws GeneralException {
        String tip = "1";
        HashMap returnMap = new HashMap();
        RecruitPrivBo rpb = new RecruitPrivBo();
        String canBeDeleIds = ids;
        ContentDAO dao = new ContentDAO(this.conn);
        String isExistStudent ="";
        RowSet rs = null;
        try {
            if ("dele".equals(type)){
                isExistStudent = isExistStudent(ids);
                returnMap.put("isExistStudent", isExistStudent);
            }
            if ("edit".equals(type)&&!userview.hasTheFunction("311080503")){
                tip = "0";
                returnMap.put("tip", tip);
                return returnMap;
            }
            boolean judgment = false;
            if ("1=1".equals(rpb.getPrivB0110Whr(userview, "B0110", RecruitPrivBo.LEVEL_PARENT))) {
                judgment = true;
            }
            if (userview.isSuper_admin() || userview.isAdmin() || judgment) {
                tip = "1";
                returnMap.put("tip", tip);
                return returnMap;
            }
            StringBuffer sql = new StringBuffer(" select id , Hall_name ");
            sql.append(" from zp_exam_hall ");
            sql.append(" where id in (" + ids + ")");
            sql.append(" and ").append(rpb.getPrivB0110Whr(userview, "B0110", RecruitPrivBo.LEVEL_PARENT));
            ArrayList values = new ArrayList();
            rs = dao.search(sql.toString(), values);
            StringBuffer stbf = new StringBuffer("");
            while (rs.next()) {
                tip = "0";
                canBeDeleIds = canBeDeleIds.replace(rs.getInt("id") + "", "none");
                stbf.append("[ " + rs.getString("Hall_name") + " ]").append(",");
            }
            // 去掉不能删除的
            if ("dele".equals(type)) {
                if (canBeDeleIds.indexOf(",") > 0) {
                    String[] canBeDeleIdGroup = canBeDeleIds.split(",");
                    canBeDeleIds = "";
                    for (int i = 0; i < canBeDeleIdGroup.length; i++) {
                        if (!"none".equals(canBeDeleIdGroup[i])) {
                            canBeDeleIds = canBeDeleIds + canBeDeleIdGroup[i] + ",";
                        }
                    }
                }

                if ("none".equals(canBeDeleIds) || StringUtils.isEmpty(canBeDeleIds)) {
                    tip = "2";
                } else {
                    if (canBeDeleIds.indexOf(",") > 0)
                        returnMap.put("canBeDeleIds", canBeDeleIds.substring(0, canBeDeleIds.lastIndexOf(",")));
                    else
                        returnMap.put("canBeDeleIds", canBeDeleIds);
                }

                if (StringUtils.isNotEmpty(stbf.toString())) {
                    if (stbf.toString().split(",").length > 2) {
                        String[] nameValues = stbf.toString().split(",");
                        String namesTip = nameValues[0] + "," + nameValues[0] + "等" + nameValues.length + "个";
                        returnMap.put("tipNames", namesTip);
                    } else
                        returnMap.put("tipNames", stbf.substring(0, stbf.lastIndexOf(",")).toString());
                } else
                    returnMap.put("tipNames", stbf.toString());
            }
            returnMap.put("tip", tip);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeResource(rs);
        }
        return returnMap;
    }

    /**
     * 
     * @Title:getHallIds
     * @Description：
     * @author liuyang
     * @param ids
     * @return 考场ids
     * @throws GeneralException
     */
    public String getHallIds(String ids) throws GeneralException {
        RowSet rs = null;
        StringBuffer stbf = new StringBuffer("");
        try {
            StringBuffer sql = new StringBuffer("  Hall_id ");
            sql.append(" from zp_exam_hall ");
            sql.append(" where id in (" + ids + ")");
            ArrayList values = new ArrayList();
            rs = dao.search(sql.toString(), values);
            while (rs.next()) {
                stbf.append(rs.getString("Hall_id")).append(",");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return stbf.substring(0, stbf.lastIndexOf(",")).toString();
    }
    /**
     * 自动计算考生统计(未分派人数)
     * @param batchId 批次
     * @param subject 考试科目
     * @param z0357 职位类型
     * @param z0321 报考单位
     * @return
     * @throws GeneralException 
     */
    public int toAutoComputeExaminee(String z0321, String z0357, String subject, String batchId)throws GeneralException {
        int count = 0;
        RowSet rt = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            StringBuffer sql = new StringBuffer();
            if (StringUtils.isEmpty(z0321) && StringUtils.isEmpty(z0357) && StringUtils.isEmpty(subject)) {
                return count;
            }
            sql.append(" select count(a0100) as count");
            getAdaptExaminee(z0321, z0357, subject, batchId, sql);
            rt = dao.search(sql.toString());
            while (rt.next()) {
                count = rt.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeResource(rt);
        }
        return count;
    }

    /**
     * 考生适配条件
     * 
     * @param z0321
     * @param z0357
     * @param subject
     * @param batchId
     * @param sql
     */
    private void getAdaptExaminee(String z0321, String z0357, String subject, String batchId, StringBuffer sql) {
    	sql.append(" from zp_exam_assign ");
    	sql.append(" where  1=1 and exam_hall_id is null and hall_id is null and seat_id is null");
    //	sql.append("  and p.resume_flag not in ('0106','0206','0308','0408','0508','0604','0704','0806','1003','1005') ");
    	int dbflag = Sql_switcher.searchDbServer();
    	// 报考单位
        if (z0321.length() > 0 && z0321 != null) {
            z0321 = z0321.substring(1);
            String[] z0321s = z0321.split(",");
            this.getLikeSql(sql, dbflag, z0321s, "z0321");
            sql.append(") ");
        }
        // 职位类别
        if (z0357.length() > 0 && z0357 != null) {
            z0357 = z0357.substring(1);
            sql.append(" and z0301 in (");
            sql.append("select z0301 from z03 where z0357 in(");
            sql.append(z0357);
            sql.append(") ");
            sql.append(")");
        }
        // 科目
        if (subject != null && subject.length() > 0) {
            subject = subject.substring(1);
            String[] subjects = subject.split(",");
            sql.append(" and (");
            ArrayList set = new ArrayList();
            for (int i = 0; i < subjects.length; i++) {
                if(StringUtils.isEmpty(subjects[i]))
                    continue;
                
                String flag = subjects[i].substring(1, subjects[i].lastIndexOf("'"));
                set.add(flag);
            }
            for (int j = 0; j < set.size(); j++) {
            	String codeId = getRootParentId((String)set.toArray()[j]);
                if (j > 0) {
                    sql.append(" or");
                }
                
                // 如果选择的是一级科目，那么查询科目下所有科目
                if (subjects[j].equalsIgnoreCase("'" + codeId + "'"))
                    sql.append(" subject_" + codeId).append(" like '").append(subjects[j].replace("'", "")).append("%' ");
                else
                    sql.append(" subject_" + codeId).append("=").append(subjects[j]);
            }
            sql.append(" ) ");
        }
        sql.append(" and  z0301 in (select z0301 from z03 where z0101=");        
        sql.append("'" + batchId + "')");
        //给还处于笔试阶段的人分派考场
        sql.append(" and A0100 in (select A0100 from zp_pos_tache where resume_flag = '0301' and zp_exam_assign.Z0301=zp_pos_tache.ZP_POS_ID)");
    }

    /**
     * 获取满足筛选条件的人员编号
     * 
     * @param z0321s
     *            所属单位
     * @param z0357s
     *            职位类别
     * @param subjects
     *            报考科目
     * @param batchId
     *            批次id
     * @return
     * @throws GeneralException
     */
    public ArrayList getA0100s(String z0321s, String z0357s, String subjects, String batchId) throws GeneralException {
        ArrayList res = new ArrayList();
        StringBuffer sql = new StringBuffer("select distinct(a0100),z0321,z0325,z0301");
        this.getAdaptExaminee(z0321s, z0357s, subjects, batchId, sql);
        sql.append(" order by z0321,z0325,z0301 asc");

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());

            while (rs.next())
                res.add(rs.getString("a0100"));

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 获取指定考场共可以分配多少人（即座位数）
     * 
     * @param hallId
     * @return
     * @throws GeneralException
     */
    public int getAllSeats(String hallId) throws GeneralException {
        int res = 0;
        String sql = "select seat_num from zp_exam_hall where id=" + hallId + "";

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);

            while (rs.next())
                res = rs.getInt("seat_num");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 获取指定考场已分配的座位号
     * 
     * @param hallId
     * @return
     * @throws GeneralException
     */
    public ArrayList getAlreadySeatIds(String hallId) throws GeneralException {
        ArrayList res = new ArrayList();
        String sql = "select seat_id from zp_exam_assign where exam_hall_id=" + hallId + " order by seat_id asc";

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);

            while (rs.next())
                res.add(rs.getString("seat_id"));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 获取未分配的座位号
     * 
     * @param hallId
     * @return
     * @throws GeneralException
     */
    public ArrayList getRestSeatIds(String hallId) throws GeneralException {
        // 总座位数
        int seatNum = this.getAllSeats(hallId);
        // 所有座位号
        ArrayList total = new ArrayList();
        for (int i = 1; i <= seatNum; i++) {
            total.add(i + "");
        }
        // 已分配的座位号
        ArrayList alreadyAssign = this.getAlreadySeatIds(hallId);
        // 剩余未分配座位号
        total.removeAll(alreadyAssign);
        return total;
    }

    /**
     * 获取考场信息
     * 
     * @param hallid
     * @return
     * @throws GeneralException
     */
    public LazyDynaBean getHallInfos(String hallid) throws GeneralException {
        LazyDynaBean res = new LazyDynaBean();
        String sql = "select * from zp_exam_hall where id=" + hallid + "";

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);

            while (rs.next()) {
                res.set("hall_id", rs.getString("hall_id"));
                res.set("hall_name", rs.getString("hall_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 安排考场
     * 
     * @param hallIds
     * @param z0321s
     * @param z0357s
     * @param subjects
     * @param batchId
     * @param distrubuteRule 0:顺序分配座位号 1:随机分配座位号
     * @throws Exception
     */
    public void assignExamHall(String hallIds, String z0321s, String z0357s, String subjects, String batchId,String distrubuteRule)
            throws Exception {
        StringBuffer sql = new StringBuffer("update zp_exam_assign set exam_hall_id=?,hall_id=?,hall_name=?,seat_id=? where a0100 = ? " +
        		" and z0301 in (select z0301 from z03 where z0101=?");
        int dbflag = Sql_switcher.searchDbServer();
        if(StringUtils.isNotEmpty(z0321s)){
        	String z0321snew = z0321s.substring(1);
        	String[] split = z0321snew.split(",");
        	this.getLikeSql(sql, dbflag, split, "Z0321");
        	sql.append(")");
        }
        if(StringUtils.isNotEmpty(z0357s)){
        	String z0357snew = z0357s.substring(1);
        	String[] split = z0357snew.split(",");
        	this.getLikeSql(sql, dbflag, split, "Z0357");
        	sql.append(")");
        }
        String subjectnew = "";
        if(StringUtils.isNotEmpty(subjects)){
        	subjectnew = subjects.substring(1);
            String[] subject = subjectnew.split(",");
            sql.append(" and (");
            ArrayList set = new ArrayList();
            for (int i = 0; i < subject.length; i++) {
                if(StringUtils.isEmpty(subject[i]))
                    continue;
            	
                String flag = subject[i].substring(1, subject[i].lastIndexOf("'"));
                set.add(flag);
            }
            for (int j = 0; j < set.size(); j++) {
                String codeId = getRootParentId((String)set.toArray()[j]);

                if (j > 0) {
                    sql.append(" or");
                }
                
                // 如果选择的是一级科目，那么查询科目下所有科目
                if (subject[j].equalsIgnoreCase("'" + codeId + "'"))
                    sql.append(" subject_" + codeId).append(" like '").append(subject[j].replace("'", "")).append("%' ");
                else
                    sql.append(" subject_" + codeId).append("=").append(subject[j]);
            }
            sql.append(")");
        }
        
        sql.append(")");
        ArrayList values = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);

        if (StringUtils.isEmpty(hallIds))
            return;

        ArrayList a0100s = this.getA0100s(z0321s, z0357s, subjects, batchId);
        //如果随机分配，将A0100S顺序打乱，既可以随机分配座位号
        if ("1".equals(distrubuteRule)) {
        	Collections.shuffle(a0100s);
		}
        if (a0100s.size() == 0)
            throw GeneralExceptionHandler.Handle(new Exception("没有满足条件的考生,请重新设置条件"));

        String[] hallids = hallIds.split(",");
        ArrayList tem = null;
        LazyDynaBean hallBean = null;
        ArrayList restPerson = new ArrayList();
        StringBuffer selSql = new StringBuffer("select exam_hall_id,hall_id,hall_name,seat_id from zp_exam_assign where seat_id is not null and seat_id <>'' ");
        String whereSql = sql.substring(sql.indexOf("where")+5);
        selSql.append(" and " + whereSql);
        RowSet search = null;
        for (int i = 0; i < hallids.length; i++) {
            tem = this.getRestSeatIds(hallids[i]);
            // 所选的考场没有剩余的座位（即已分配完）
            if (tem.size() == 0)
                continue;
            if(a0100s.size() ==0 )
            	break;
           
            hallBean = this.getHallInfos(hallids[i]);

            restPerson.clear();
            restPerson.addAll(a0100s);
         // 考生数大于剩余座位数，将剩余座位数安排满后，将剩余的其他人安排到下一个考场中
            for (int j = 0; j < tem.size(); j++) {
            	values.clear();
            	values.add(a0100s.get(j));
                values.add(batchId);
            	search = dao.search(selSql.toString(),values);
            	values.clear();
            	if(search.next()){
            		values.add(search.getString("exam_hall_id"));
                    values.add(search.getString("hall_id"));
                    values.add(search.getString("hall_name"));
                    values.add(search.getString("seat_id"));
                    values.add(a0100s.get(j));
                	values.add(batchId);
                	dao.update(sql.toString(), values);
                	//为了不空开座位
                	// 移除已安排座位的人，计算剩余的人
                	restPerson.remove(a0100s.get(j));
                	a0100s.remove(a0100s.get(j));
	                j--;
            	}else{
	                values.add(hallids[i].replace("'", ""));
	                values.add(hallBean.get("hall_id"));
	                values.add(hallBean.get("hall_name"));
	                values.add(tem.get(j));
	                values.add(a0100s.get(j));
	                values.add(batchId);
	                dao.update(sql.toString(), values);
	                // 移除已安排座位的人，计算剩余的人
	                restPerson.remove(a0100s.get(j));
            	}
                //考生安排完跳出
                if(restPerson.size()==0)
                	break;
                	
            }
            // 将剩余的考生赋值给a0100s
            a0100s.clear();
            a0100s.addAll(restPerson);
            this.updateHallPerNum(hallids[i].replace("'", ""));
        }
    }

	private void getLikeSql(StringBuffer sql, int dbflag, String[] split, String fieldname) {
		sql.append(" and ");
		if(dbflag==1){
			sql.append("(");
			for (String string : split) {
				sql.append(fieldname+" like '");
				if("Z0357".equals(fieldname))
					sql.append(string.substring(1, string.lastIndexOf("'"))+"%' or ");
				else
					sql.append(PubFunc.decrypt(string)+"%' or ");
			}
			sql.setLength(sql.length()-3);
		}else{
			sql.append(" regexp_like (");
			sql.append(fieldname+",'^");
			for (String string : split) {
				if("Z0357".equals(fieldname))
					sql.append(string.substring(1, string.lastIndexOf("'"))+"|");
				else
					sql.append(PubFunc.decrypt(string)+"|");
			}
			sql.setLength(sql.length()-1);
			sql.append("'");
		}
	}

    /**
     * 查询指定考场已分派人数
     * 
     * @param hallId
     * @return
     * @throws GeneralException
     */
    public int getHallPerNums(String hallId) throws GeneralException {
        int res = 0;

        StringBuffer sql = new StringBuffer("select count(A.A0100) perNum from ");
        sql.append("(select distinct A0100 ");
        sql.append(" from zp_exam_assign ass left join zp_exam_hall hall");
        sql.append(" on hall.id=ass.exam_hall_id");
        sql.append(" and ass.exam_hall_id is not null and ass.hall_id is not null and ass.hall_name is not null and ass.seat_id is not null");
        sql.append(" where ass.exam_hall_id='" + hallId + "'");
        sql.append(" group by a0100,nbase,z0301) A ");

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());

            while (rs.next())
                res = rs.getInt("perNum");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return res;
    }

    /**
     * 更新考场中已分配人数
     * 
     * @param hallId
     * @throws GeneralException
     */
    public void updateHallPerNum(String hallId) throws GeneralException {
        String sql = "update zp_exam_hall set people_num=? where id=?";
        ContentDAO dao = new ContentDAO(this.conn);

        ArrayList values = new ArrayList();
        values.add(this.getHallPerNums(hallId));
        values.add(hallId);
        try {
            dao.update(sql, values);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @Title:isExistStudent
     * @Description：
     * @author liuyang
     * @param ids
     * @return
     * @throws GeneralException 
     */
    private String isExistStudent(String ids) throws GeneralException {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        String isExistStudent = "0";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer(" select exam_hall_id ");
            sql.append(" from zp_exam_assign ");
            sql.append(" where exam_hall_id in (" + ids + ")");

            rs = dao.search(sql.toString(), list);
            if(rs.next())
                isExistStudent = "1";   
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return isExistStudent;
    }


	/**
	 * 考场分派--添加考场
	 * @param batchId 批次id
	 * @param ids 已存在的考场号
	 * @param value 搜索框的关键字
	 * @author sunming
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList toAddExamHall(String batchId, String ids, String value) throws GeneralException {
		StringBuffer buf = new StringBuffer();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			if(Sql_switcher.searchDbServer() == Constant.ORACEL){
				buf.append(" select zp_exam_hall.hall_id as itemid,zp_exam_hall.hall_name as itemdesc,rownum from zp_exam_hall  where rownum<=20");
				buf.append(" and batch_id=");
			}else{
				buf.append("select top 20 hall_id as itemid,hall_name as itemdesc from zp_exam_hall");
				buf.append(" where batch_id=");
			}
			buf.append("'"+batchId+"'");
			buf.append(" and seat_num != people_num");
			if(ids!=null && ids.length()>1){
				buf.append(" and id not in (");
				buf.append(ids.substring(1));
				buf.append(")");
			}
			if(value!=null && value.length()>0){
				buf.append(" and hall_name like");
				buf.append(" '%");
				buf.append(value);
				buf.append("%'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("itemid", rs.getString("itemid"));
				map.put("itemdesc", rs.getString("itemdesc"));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 根据考场id和批次编号获取座位数、未分派、剩余数、考场名称等指标
	 * @param batchId 批次id
	 * @param alist 考场id的集合
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getAddExamHallList(String batchId, ArrayList alist) throws GeneralException {
		ArrayList rList = new ArrayList();
		//考场id
		ArrayList hallIds = new ArrayList();
		//考场名称
		ArrayList records = new ArrayList();
		//剩余数量数组
		ArrayList surplusNums = new ArrayList();
		//座位数
		ArrayList seatNums = new ArrayList();
		//剩余数量
		int surplusNum=0;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append(" select id,hall_name,seat_num,people_num,(seat_num-people_num) as plus_num from zp_exam_hall ");
			buf.append(" where batch_id=");
			buf.append("'"+batchId+"'");
			buf.append(" and hall_id in (");
			for(int i=0;i<alist.size();i++){
				buf.append("'"+alist.get(i).toString().split("`")[0]+"'");
				if(i<alist.size()-1){
					buf.append(",");
				}
			}
			buf.append(")");
			rs = dao.search(buf.toString());
			while(rs.next()){
				hallIds.add(rs.getString("id"));
				records.add(rs.getString("hall_name"));
				surplusNums.add(rs.getInt("plus_num"));
				seatNums.add(rs.getInt("seat_num"));
				surplusNum+=rs.getInt("plus_num");
			}
			rList.add(hallIds);
			rList.add(records);
			rList.add(surplusNums);
			rList.add(seatNums);
			rList.add(surplusNum);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return rList;
	}
	
	/**
	 * 考场分派机构树查询已分派和未分派人数
	 * @param unitId 机构号
	 * @return
	 */
	public HashMap<String, Integer> getExamNum(String unitId){
		HashMap<String,Integer> map = null;

		RowSet search = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select COUNT(a0100) num,has_exam from (");
			sql.append("select z63.a0100 as a0100,");
			sql.append("case when zp.EXAM_HALL_ID is null or zp.EXAM_HALL_ID ='' then 0 else 1 end as has_exam from z63 ");
			sql.append(" inner join zp_exam_assign zp ");
			sql.append(" on z63.a0100=zp.A0100 and z63.NBASE=zp.NBASE and z63.Z0301=zp.Z0301 ");
			//处于当前批次的
			sql.append(" and  zp.z0301 in (select z0301 from z03 where z0101=");
			sql.append("'" + this.userview.getHm().get("batchId") + "')");
			//给还处于笔试阶段的人分派考场
			sql.append(" and zp.A0100 in (select A0100 from zp_pos_tache where resume_flag = '0301' and zp.Z0301=zp_pos_tache.ZP_POS_ID)");
			sql.append(" and z63.Z0321 like ?");
			sql.append(") tmp group by has_exam ");
			ArrayList<String> list = new ArrayList<String>();
			list.add(unitId+"%");
			search = dao.search(sql.toString(),list);
			map = new HashMap<String,Integer>();
			while(search.next()){
				if("1".equals(search.getString("has_exam")))
					map.put("has_exam", search.getInt("num"));
				else
					map.put("no_exam", search.getInt("num"));
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return map;
	}
	
	/**
	 * 获取考试代码类最上级节点
	 * @param codeItemId
	 * @return
	 */
	private String getRootParentId(String codeItemId) {
		String rootId = codeItemId;
		CodeItem code = AdminCode.getCode("79", codeItemId);
    	if(code!=null) {
    		if(!codeItemId.equals(code.getPcodeitem())) {
    			rootId = getRootParentId(code.getPcodeitem());
    		}
    	}
		return rootId;
	}

}
