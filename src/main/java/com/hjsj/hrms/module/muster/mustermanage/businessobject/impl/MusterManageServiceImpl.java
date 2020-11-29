package com.hjsj.hrms.module.muster.mustermanage.businessobject.impl;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;

public class MusterManageServiceImpl implements MusterManageService {
    private Connection conn;

    private UserView userView;

    public MusterManageServiceImpl(Connection conn, UserView userView) {
        super();
        this.conn = conn;
        this.userView = userView;
    }

    @Override
    public ArrayList getButtonList(String musterType) {
        ArrayList buttonList = new ArrayList();
        try {
            //花名册分类功能按钮不做权限控制
            buttonList.add(this.createMenu(ResourceFactory.getProperty("muster.musterclass")));
            if ("1".equals(musterType)) {//人员名册
                if (userView.isSuper_admin() || userView.hasTheFunction("2603101")||userView.hasTheFunction("030901")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.addmuster"), "musterManage.addMuster"));
                }
                if (userView.isSuper_admin() || userView.hasTheFunction("2603102")||userView.hasTheFunction("030902")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.delmuster"), "musterManage.delMuster"));
                }
            }else if("2".equals(musterType)) {//单位花名册
                if (userView.isSuper_admin() ||userView.hasTheFunction("2303101")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.addmuster"), "musterManage.addMuster"));
                }
                if (userView.isSuper_admin() ||userView.hasTheFunction("2303102")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.delmuster"), "musterManage.delMuster"));
                }
            }else if ("3".equals(musterType)) {//岗位花名册
                if (userView.isSuper_admin() ||userView.hasTheFunction("2503101")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.addmuster"), "musterManage.addMuster"));
                }
                if (userView.isSuper_admin() ||userView.hasTheFunction("2503102")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.delmuster"), "musterManage.delMuster"));
                }
            }else if ("4".equals(musterType)) {//基准岗位花名册
                if (userView.isSuper_admin() ||userView.hasTheFunction("2503101")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.addmuster"), "musterManage.addMuster"));
                }
                if (userView.isSuper_admin() ||userView.hasTheFunction("2503102")) {
                    buttonList.add(new ButtonInfo(ResourceFactory.getProperty("muster.delmuster"), "musterManage.delMuster"));
                }
            }
            ButtonInfo queryBox = new ButtonInfo();
            queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
            queryBox.setText(ResourceFactory.getProperty("muster.querymustertext"));
            queryBox.setFunctionId("MM01010002");
            buttonList.add(queryBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buttonList;
    }
    /**
     * 获取花名册分类 集合
     */
    @Override
    public List listMusterLstyle(String musterType) throws GeneralException {
        ArrayList list = new ArrayList();
        ResultSet rSet = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            String updateSql = "update lstyle set musterstyletype = '1' where musterstyletype is null or musterstyletype = ''";
            dao.update(updateSql);
            StringBuffer sql = new StringBuffer("select styleid,styledesc from lstyle where musterstyletype = '");
            sql.append(musterType);
            sql.append("' order by styledesc ");
            rSet = dao.search(sql.toString());
            HashMap seeAll = new HashMap();
            seeAll.put("styleid", "-1");
            seeAll.put("styledesc", "查看全部");
            list.add(seeAll);
            HashMap data; 
            while (rSet.next()) {
                data = new HashMap();
                data.put("styleid", rSet.getString("styleid"));
                data.put("styledesc", rSet.getString("styledesc"));
                list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rSet);
        }
        return list;
    }

    /**
     * 获取指定分类下的花名册
     */
    @Override
    public List listMuster(String styleid, String musterType) throws GeneralException {
        StringBuffer sqlbuffer = new StringBuffer("");
        ArrayList list = new ArrayList();
        ResultSet rSet = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            sqlbuffer.append("select tabid,hzname,B0110,create_name,create_date from lname where flag = '");
            sqlbuffer.append(musterType);
            sqlbuffer.append("' and ModuleFlag like '_");
            sqlbuffer.append(styleid);
            sqlbuffer.append("%'");
            rSet = dao.search(sqlbuffer.toString());
            HashMap data;
            while (rSet.next()) {
                data = new HashMap();
                data.put("tabid", rSet.getString("tabid"));
                data.put("hzname", rSet.getString("hzname"));
                data.put("B0110", rSet.getString("B0110"));
                data.put("create_name", rSet.getString("create_name"));
                Date date = rSet.getDate("create_date");
                data.put("create_date", PubFunc.FormatDate(date, "yyyy-MM-dd"));
                list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rSet);
        }
        return list;
    }

    /**
     * 删除花名册分类
     * @param styleid 分类id
     */
    @Override
    public void deleteMusterLstyle(String styleid) throws GeneralException {
        StringBuffer sqlbuffer = new StringBuffer("");
        ArrayList list = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(conn);
            sqlbuffer.append("delete from lstyle where styleid = ?");
            list.add(styleid);
            dao.delete(sqlbuffer.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMuster(String tabid) throws GeneralException {
        try {
            String[] tabidArray = tabid.split(",");
            StringBuffer dellbase = new StringBuffer("");
            StringBuffer dellname = new StringBuffer("");
            ContentDAO dao = new ContentDAO(conn);
            dellbase.append("delete from lbase where tabid  in (");
            dellname.append("delete from lname where tabid  in (");
            for (int i = 0; i < tabidArray.length; i++) {
                if (i != 0) {
                    dellbase.append(",");
                    dellname.append(",");
                }
                dellbase.append(tabidArray[i]);
                dellname.append(tabidArray[i]);
            }
            dellbase.append(")");
            dellname.append(")");
            dao.delete(dellbase.toString(), null);
            dao.delete(dellname.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存花名册分类
	 * @param data 花名册分类集合
     */
    @Override
    public String saveMusterLstyle(Map data) throws GeneralException {
        ArrayList list = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(conn);
            String styleid = (String) data.get("styleid");
            String styledesc = (String) data.get("styledesc");
            String type = (String) data.get("type");
            if ("save_lstyle".equals(type)) {
                list.add(styledesc);
                list.add(styleid);
                StringBuffer sBuffer = new StringBuffer("");
                sBuffer.append("update lstyle set styledesc = ? where styleid = ?");
                dao.update(sBuffer.toString(), list);
            } else if ("add_lstyle".equals(type)) {
                String newStyleid  = getStyleid(dao);
                RecordVo lstyleVo = new RecordVo("lstyle");
                lstyleVo.setString("styleid", newStyleid);
                lstyleVo.setString("styledesc", styledesc);
                lstyleVo.setString("musterstyletype",(String) data.get("musterstyletype"));
                dao.addValueObject(lstyleVo);
               }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return "0";
    }

    @Override
    public void saveMuster(RecordVo lnameVo, String musterItem) throws GeneralException {
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            String[] field = null;
            if (musterItem.indexOf("#!#") > -1) {
                field = musterItem.split("#!#");
            }
            if (field != null) {
                dao.addValueObject(lnameVo);
                int tabid = lnameVo.getInt("tabid");
                String delsql = "delete from lbase where tabid = ?";
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(tabid);
                dao.delete(delsql, list);
                String sqlLBase = "insert into lbase (tabid,baseid,Field_name,ColHz,field_type,Align) values(?,?,?,?,?,?)";
                List<ArrayList<Object>> bathListData = new ArrayList<ArrayList<Object>>();
                for (int i = 0; i < field.length; i++) {
                    String fieldId = "";
                    String fieldName = "";
                    String fieldType = "";
                    String item = field[i];
                    if (item.indexOf(":") > -1) {
                        fieldId = item.split(":")[0];
                        fieldName = item.split(":")[1];
                        fieldType = item.split(":")[2];
                    }
                    ArrayList<Object> listData = new ArrayList<Object>();
                    listData.add(tabid);
                    listData.add(i);
                    listData.add(fieldId);
                    listData.add(fieldName);
                    listData.add(fieldType);
                    if (StringUtils.equals(fieldType, "A")) {
                        listData.add(1);
                    }else if (StringUtils.equals(fieldType, "D")||StringUtils.equals(fieldType, "N")) {
                        listData.add(3);
                    }else {
                        listData.add(2);
                    }
                    bathListData.add(listData);
                }
                if (bathListData.size()>0) {
                    dao.batchInsert(sqlLBase, bathListData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ArrayList<ColumnsInfo> getColumnList() {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try {
            FieldItem fieldItem0 = new FieldItem();
            fieldItem0.setItemid("tabid");
            fieldItem0.setItemdesc(ResourceFactory.getProperty("muster.number"));
            fieldItem0.setItemtype("A");
            fieldItem0.setCodesetid("0");
            fieldItem0.setReadonly(true);
            ColumnsInfo info0 = new ColumnsInfo(fieldItem0);
            info0.setEncrypted(false);
            info0.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            info0.setColumnWidth(60);
            info0.setTextAlign("right");
            list.add(info0);
            FieldItem fieldItem1 = new FieldItem();
            fieldItem1.setItemid("hzname");
            fieldItem1.setItemdesc(ResourceFactory.getProperty("muster.name"));
            fieldItem1.setItemtype("A");
            fieldItem1.setReadonly(true);
            fieldItem1.setItemlength(50);
            fieldItem1.setCodesetid("0");
            ColumnsInfo info1 = new ColumnsInfo(fieldItem1);
            info1.setEditableValidFunc("false");
            info1.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            info1.setRendererFunc("musterManage.hznameShow");
            info1.setColumnWidth(200);
            //info1.setSortable(false);排序
            list.add(info1);
            FieldItem fieldItem3 = new FieldItem();
            fieldItem3.setItemid("B0110");
            fieldItem3.setItemdesc(ResourceFactory.getProperty("muster.belongtoun"));
            fieldItem3.setItemtype("A");
            fieldItem3.setCodesetid("UN");
            ColumnsInfo info3 = new ColumnsInfo(fieldItem3);
            info3.setLocked(false);
            info3.setColumnWidth(200);
            info3.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info3);
            FieldItem fieldItem7 = new FieldItem();
            fieldItem7.setItemid("styledesc");
            fieldItem7.setItemdesc(ResourceFactory.getProperty("muster.belongstyle"));
            fieldItem7.setItemtype("A");
            fieldItem7.setCodesetid("0");
            ColumnsInfo info7 = new ColumnsInfo(fieldItem7);
            info7.setLocked(false);
            info7.setColumnWidth(200);
            info7.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info7);
            FieldItem fieldItem4 = new FieldItem();
            fieldItem4.setItemid("create_name");
            fieldItem4.setItemdesc(ResourceFactory.getProperty("muster.createpeople"));
            fieldItem4.setItemtype("A");
            fieldItem4.setCodesetid("0");
            ColumnsInfo info4 = new ColumnsInfo(fieldItem4);
            info4.setLocked(false);
            info4.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
            list.add(info4);
            FieldItem fieldItem6 = new FieldItem();
            fieldItem6.setItemid("create_date");
            fieldItem6.setItemdesc(ResourceFactory.getProperty("muster.createdate"));
            fieldItem6.setItemtype("D");
            fieldItem6.setCodesetid("0");
            ColumnsInfo info6 = new ColumnsInfo(fieldItem6);
            info6.setLocked(false);
            info6.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            info6.setRendererFunc("musterManage.createdateShow");
            list.add(info6);
            FieldItem fieldItem5 = new FieldItem();
            fieldItem5.setItemid("operation");
            fieldItem5.setItemdesc(ResourceFactory.getProperty("muster.operation"));//操作
            fieldItem5.setItemtype("A");
            fieldItem5.setReadonly(true);
            fieldItem5.setItemlength(50);
            fieldItem5.setCodesetid("0");
            ColumnsInfo info5 = new ColumnsInfo(fieldItem5);
            info5.setQueryable(false);
            info5.setEditableValidFunc("false");
            info5.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            info5.setRendererFunc("musterManage.lineOperation");
            info5.setColumnWidth(150);
            info5.setSortable(false);
            info5.setFilterable(false);
            list.add(info5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String createMenu(String name) {
        StringBuffer str = new StringBuffer();
        str.append("<jsfn>{id:'style_button',xtype:'button',text:'" + name + "'");
        str.append(",menu:{id:'style_menu'");
        str.append("},handler:function(){musterManage.musterClass();}");
        str.append("}</jsfn>");
        return str.toString();
    }
    /**
     * 为了展示数据库中存在的老的花名册，把lname表中的styleid列数据写上
     */
    private void writeStyleid() {
        List<ArrayList<String>> listData = null;
        RowSet rowSet = null;
        try {
            listData = new ArrayList<ArrayList<String>>();
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search("select tabid,moduleflag from lname where styleid is null or styleid = ''");
            while(rowSet.next()) {
                ArrayList<String> datalist = new ArrayList<String>();
                datalist.add(rowSet.getString("moduleflag").substring(1,3));
                datalist.add(rowSet.getString("tabid"));
                listData.add(datalist);
            }
            if (listData.size()>0) {
                dao.batchUpdate("update lname set styleid = ? where tabid = ?", listData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rowSet);
        }
    }
    @Override
    public String getMusterMainSql(String musterType,String moduleId) throws GeneralException {
        String priv = null;
        StringBuffer sqlbuffer = new StringBuffer("");
        try {
            writeStyleid();
            priv = getMusterPriv(moduleId);
            sqlbuffer.append("select tabid,hzname,B0110,create_name,create_date,ModuleFlag,styledesc from lname,lstyle where flag =");
            sqlbuffer.append(" '");
            sqlbuffer.append(musterType);
            sqlbuffer.append("' ");
            if (!"UN".equals(priv) && !"".equals(priv) && null != priv&&!"|".equals(priv)) {
                String[] array = priv.split("\\|");
                if (array.length > 1) {
                    sqlbuffer.append(" and (B0110 in (");
                    //上级权限单位
                    String[] unidArray = array[1].split(",");
                    for (int j = 0; j < unidArray.length; j++) {
                        String unid = unidArray[j];
                        if (StringUtils.isNotBlank(unid)) {
                            sqlbuffer.append("'");
                            sqlbuffer.append(unid);
                            sqlbuffer.append("',");
                        }
                    }
                    if (sqlbuffer.toString().endsWith(",")) {
                        sqlbuffer.deleteCharAt(sqlbuffer.length() - 1);
                    }
                    sqlbuffer.append(")");
                    sqlbuffer.append(" or");
                    //权限单位
                    String[] unArray = array[0].split(",");
                    for (int j = 0; j < unArray.length; j++) {
                        String unid = unArray[j];
                        if (StringUtils.isNotBlank(unid)) {
                            sqlbuffer.append(" B0110 like");
                            sqlbuffer.append("'");
                            sqlbuffer.append(unid);
                            sqlbuffer.append("%' or");
                        }
                    }
                    if (sqlbuffer.toString().endsWith("or")) {
                        sqlbuffer.deleteCharAt(sqlbuffer.lastIndexOf("or"));
                        sqlbuffer.deleteCharAt(sqlbuffer.lastIndexOf("r"));
                    }
                    sqlbuffer.append(")");
                }else if(array.length == 1){
                    sqlbuffer.append(" and (");
                    String[] unidArray = array[0].split(",");
                    for (int j = 0; j < unidArray.length; j++) {
                        String unid = unidArray[j];
                        if (StringUtils.isNotBlank(unid)) {
                            sqlbuffer.append(" B0110 like");
                            sqlbuffer.append("'");
                            sqlbuffer.append(unid);
                            sqlbuffer.append("%' or");
                        }
                    }
                    if (sqlbuffer.toString().endsWith("or")) {
                        sqlbuffer.deleteCharAt(sqlbuffer.lastIndexOf("or"));
                        sqlbuffer.deleteCharAt(sqlbuffer.lastIndexOf("r"));
                    }
                    sqlbuffer.append(")");
                }
            } else {
                if ("".equals(priv) || null == priv||"|".equals(priv)) {
                    sqlbuffer.append(" and 1=2 ");
                }
            }
            sqlbuffer.append(" and lname.Styleid = lstyle.styleid ");//此处的lname.Styleid 不要修改，否则会影响点击分类时的展现
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlbuffer.toString();
    }

    /**
      * 获取当前登录人的花名册权限  
      * @param userview
      * @return 超级管理管返回UN  非超级管理员 返回  xxx,xxx | xxx,xxx  权限内机构 | 上级机构    
      */
    @Override
    public String getMusterPriv(String moduleId) {
        String codePriv = "";
        try {
            if (userView.isSuper_admin()) {
                return "UN";
            }
            if("0".equalsIgnoreCase(moduleId)){
                //员工管理只获取人员范围与人员管理中的高级权限
                codePriv = this.getEmpWhere(userView);
            }else{
                //1、业务用户 ：业务操作单位》操作单位》管理范围
                //2、自助用户 ：先取关联用户的（业务操作单位》操作单位）》自身的业务操作单位》管理范围》所属单位
                codePriv = this.getUnitIdByBusi(userView,"4");//代表组织机构
            }
            String[] array = codePriv.split(",");
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            Collections.sort(list,new listComparator());
            if (!"UN".equals(codePriv)) {
                codePriv = codePriv + "|";
                for (String codeitemid : list) {
                    String parentid = getParentid(codeitemid);
                    if (StringUtils.isNotBlank(parentid)) {
                        codePriv+=parentid+",";
                    }
                }
            }
            if (codePriv.endsWith(",")) {
                codePriv = codePriv.substring(0, codePriv.length()-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codePriv;
    }

    /**
     * 初始化花名册分类类型usterstyletype
     */
    @Override
    public void updateMusterstyletype() {
        try {
            ContentDAO dao = new ContentDAO(conn);
            String updateSql = "update lstyle set musterstyletype = '1' where musterstyletype is null or musterstyletype = ''";
            dao.update(updateSql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
      * @return 所需要的数据集合
      */
    @Override
    public ArrayList addMusterInfo(int operate, String musterType, String musterName, String tabid,
            String FieldSet, String data) throws GeneralException {
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            if (operate == 1) { //初始化人员库信息
                ArrayList userDb = userView.getPrivDbList(); //人员权限下的人员库
                for (Object dbPre : userDb) {
                	String dbName= AdminCode.getCodeName("@@", (String)dbPre);
                	HashMap map = new HashMap();
                	map.put("pre", (String)dbPre);
                    map.put("DBName", dbName);
                    list.add(map);
				}
            } else if (operate == 2) { //指标树信息查询   
                String setUseflag = ""; //子集是否构库
                String fieldsetid = "";//子集id
                String fieldsetdesc = "";
                String itemid = "";//指标id
                String itemdesc = "";//指标名称
                String itemUseflag = ""; //指标是否构库
                String codeSetId = "";
                ArrayList useFieldSet = null;//
                //花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；默认为"1"。
                if ("1".equals(musterType)) {
                    useFieldSet = userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
                } else if ("2".equals(musterType)) {
                    useFieldSet = userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
                } else if ("3".equals(musterType)) {
                    useFieldSet = userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
                } else if ("4".equals(musterType)) {
                    useFieldSet = userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);
                }
                for (int i = 0; i < useFieldSet.size(); i++) {
                    HashMap map = new HashMap();
                    FieldSet fieldset = (FieldSet) useFieldSet.get(i);
                    String changeflag = fieldset.getChangeflag();
                    fieldsetid = fieldset.getFieldsetid(); //子集ID
                    fieldsetdesc = fieldset.getFieldsetdesc(); //子集名称
                    setUseflag = fieldset.getUseflag(); //是否构库 1=构库；2=不构库
                    fieldsetid = fieldsetid.toUpperCase(); //转化大写
                    if (!"A00".equals(fieldsetid) && !"多媒体子集".equals(fieldsetdesc)) { //过滤多媒体子集
                        if ("1".equals(setUseflag)) {//构库才查
                            map.put("text", fieldsetdesc);
                            map.put("id", fieldsetid);
                            map.put("checked", false);
                            ArrayList fielditemlist = userView.getPrivFieldList(fieldsetid); //获取集合下有权限的指标
                            if (fielditemlist != null) {
                                ArrayList leaf = new ArrayList();
                                if ("A01".equalsIgnoreCase(fieldsetid)) { //添加一个单位指标
                                	for (int j = 0; j < fielditemlist.size(); j++) {
										FieldItem fieldItem = (FieldItem) fielditemlist.get(j);
										if ("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
											HashMap mp = new HashMap();
		                                    mp.put("text", "单位名称");
		                                    mp.put("leaf", true);
		                                    mp.put("id", "A01.B0110.A.0.UN");
		                                    mp.put("flag", "normal");
		                                    mp.put("checked", false);
		                                    leaf.add(mp);
		                                    break;
										}
									}
                                }
                                if ("B01".equalsIgnoreCase(fieldsetid)) {
                                    HashMap mp = new HashMap();
                                    mp.put("text", "单位名称");
                                    mp.put("leaf", true);
                                    mp.put("id", "B01.B0110.A.0.UN");
                                    mp.put("flag", "normal");
                                    mp.put("checked", false);
                                    leaf.add(mp);
                                }
                                if ("K01".equalsIgnoreCase(fieldsetid)) {
                                    HashMap mp = new HashMap();
                                    FieldItem fieldItem = DataDictionary.getFieldItem("E0122");
                                    mp.put("text", fieldItem.getItemdesc());
                                    mp.put("leaf", true);
                                    mp.put("id", "K01.E0122.A.0.UM");
                                    mp.put("flag", "normal");
                                    mp.put("checked", false);
                                    leaf.add(mp);
                                    mp = new HashMap();
                                    mp.put("text", "岗位名称");
                                    mp.put("leaf", true);
                                    mp.put("id", "K01.E01A1.A.0.@K");
                                    mp.put("flag", "normal");
                                    mp.put("checked", false);
                                    leaf.add(mp);
                                   
                                }
                                
                                if ("H01".equalsIgnoreCase(fieldsetid)) {
                                	for (int j = 0; j < fielditemlist.size(); j++) {
										FieldItem fieldItem = (FieldItem) fielditemlist.get(j);
										if ("h0100".equalsIgnoreCase(fieldItem.getItemid())) {
											HashMap mp = new HashMap();
											mp.put("text","基准岗位名称"); 
											mp.put("leaf", true);
											mp.put("checked", false);
											RecordVo vo=ConstantParamter.getRealConstantVo("PS_C_CODE",conn);
											String codesetid = (String) (vo.getValues().get("str_value")==null?"TO":vo.getValues().get("str_value"));
											mp.put("id", "H01.H0100.A.0."+codesetid);
											mp.put("flag", "normal"); 
											leaf.add(mp); 
		                                    break;
										}
									}
                                      
                                 }
                                 
                                for (int j = 0; j < fielditemlist.size(); j++) {
                                    FieldItem item = (FieldItem) fielditemlist.get(j);
                                    itemid = item.getItemid(); //指标ID
                                    itemid = itemid.toUpperCase(); //转大写
                                    itemdesc = item.getItemdesc(); //指标名称
                                    itemUseflag = item.getUseflag(); //是否构库
                                    codeSetId = item.getCodesetid(); //代码项ID
                                    String setItemid = item.getFieldsetid(); //子集ID
                                    setItemid = setItemid.toUpperCase(); //转大写
                                    String itemType = item.getItemtype(); //子集类型  0=普通子集；1=按月变化子集；2按年变化子集
                                    String showId = fieldsetid + "." + itemid + "." + itemType + "." + changeflag + "." + codeSetId;
                                    if ("1".equals(itemUseflag)) {//构库才查
                                        HashMap mp = new HashMap();
                                        mp.put("text", itemdesc);
                                        mp.put("leaf", true);
                                        mp.put("id", showId);
                                        mp.put("checked", false);
                                        mp.put("flag", "normal");
                                        leaf.add(mp);
                                    }
                                    if ("E0122".equalsIgnoreCase(itemid) && "A01".equalsIgnoreCase(fieldsetid)) { //添加一个岗位指标
                                        HashMap mp = new HashMap();
                                        mp.put("text", "岗位名称");
                                        mp.put("leaf", true);
                                        mp.put("checked", false);
                                        mp.put("id", "A01.E01A1.A.0.@K");
                                        mp.put("flag", "normal");
                                        leaf.add(mp);
                                    }
                                }

                                map.put("children", leaf);
                            }
                            list.add(map);
                        }
                    }
                }
            } else if (operate == 3) { //查询数据过滤条件   id=-1 为无
                HashMap map = new HashMap();
                map.put("name", "无");
                map.put("id", "-1");
                list.add(map);
                String sql = "select Name,Id from LExpr";
                rs = dao.search(sql);
                while (rs.next()) {
                    map = new HashMap();
                    String name = rs.getString("Name");
                    int id = rs.getInt("Id");
                    map.put("name", name);
                    map.put("id", id);
                    list.add(map);
                }
            } else if (operate == 4) { //查询对应模块的花名册类型
                String sql = "select styleid,styledesc from lstyle where musterstyletype = ? order by styledesc asc";
                ArrayList value = new ArrayList();
                value.add(musterType);
                rs = dao.search(sql, value);
                while (rs.next()) {
                    HashMap map = new HashMap();
                    String id = rs.getString("styleid");
                    String name = rs.getString("styledesc");
                    map.put("name", name);
                    map.put("id", id);
                    list.add(map);
                }
            } else if (operate == 5) { //查重；本花名册类型下查重
                boolean nameFlag = false;
                String sql = "";
                if (!"".equals(tabid) && tabid != null) {
                    sql = "select Hzname from Lname where ModuleFlag like '_" + musterType + "%' and tabid <> " + tabid;
                } else {
                    sql = "select Hzname from Lname where ModuleFlag like '_" + musterType + "%'";
                }
                rs = dao.search(sql);
                while (rs.next()) {
                    String name = rs.getString("Hzname");
                    if (name.equals(musterName)) {
                        nameFlag = true;
                        break;
                    }
                }
                list.add(nameFlag);
            } else if (operate == 6) { //查询子集名称
                FieldSet fieldset = DataDictionary.getFieldSetVo(FieldSet);
                String setName = fieldset.getFieldsetdesc();
                list.add(setName);
            } else if (operate == 7) {
                String moreDataRecord = SafeCode.decode(data);
                if (moreDataRecord.indexOf("|") > 0) {
                    String lexpr = moreDataRecord.split("\\|")[0];
                    String factor = moreDataRecord.split("\\|")[1];
                    FactorList  factorslist=new FactorList(lexpr,factor.toUpperCase(),"",true,false,true,Integer.parseInt(musterType),userView.getUserId());
                    for (Object object : factorslist) {
                        Factor facto = (Factor) object;
                        HashMap map = new HashMap();
                        map.put("itemid",facto.getFieldname());
                        map.put("name", facto.getHz());
                        map.put("cord", facto.getValue());
                        list.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    @Override
    public void updataMuster(ArrayList list, String tabid, String musterItem) throws GeneralException {
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            String[] field = null;
            if (musterItem.indexOf("#!#") > -1) {
                field = musterItem.split("#!#");
            }
            if (field != null) {
                int updataTabid = Integer.parseInt(tabid);
                String updataLnameSql = "UPDATE Lname SET hzname=?,flag=?,title=?,moduleflag=?,styleid=?,sortField=?,b0110=?,Nbases=?,DataRange=? WHERE tabid = ? ";
                dao.update(updataLnameSql, list);
                String delSql = "DELETE FROM lbase where tabid = ?";
                ArrayList delValue = new ArrayList();
                delValue.add(updataTabid);
                dao.delete(delSql, delValue);
                for (int i = 0; i < field.length; i++) {
                    String fieldId = "";
                    String fieldName = "";
                    String fieldType = "";
                    String item = field[i];
                    if (item.indexOf(":") > -1) {
                        fieldId = item.split(":")[0];
                        fieldName = item.split(":")[1];
                        fieldType = item.split(":")[2];
                    }
                    //	int baseid = this.baseid(dao, tabid);
                    String sqlLBase = "insert into lbase (tabid,baseid,Field_name,ColHz,field_type) values(?,?,?,?,?)";
                    ArrayList values = new ArrayList();
                    values.add(updataTabid);
                    int baseid = 0;
                    String sql = "select Max(baseid) id from lbase where tabid = ?";
                    ArrayList value = new ArrayList();
                    value.add(updataTabid);
                    rs = dao.search(sql, value);
                    while (rs.next()) {
                        baseid = rs.getInt("id");
                    }
                    baseid += 1;
                    values.add(baseid);
                    values.add(fieldId);
                    values.add(fieldName);
                    values.add(fieldType);
                    dao.insert(sqlLBase, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList editMusterInit(String tabid, String dataFlag,String musterType) throws GeneralException {
         ResultSet rs = null;
        ArrayList data = new ArrayList();
        try {
            ArrayList value = new ArrayList();
            value.add(tabid);
            ContentDAO dao = new ContentDAO(conn);
            if ("otherData".equals(dataFlag)) {
                String sql = "select Hzname,ModuleFlag,SortField,B0110,Nbases,DataRange from lname where tabid = ?";
                rs = dao.search(sql, value);
                String selModuleFlag = "";
                String name = "";
                String SortField = "";
                String Nbase = "";
                String DataRange = "";
                String DBname = "";
                String musOrg = "";
                String musOrgName = "";
                while (rs.next()) {
                    name = rs.getString("Hzname");
                    selModuleFlag = rs.getString("ModuleFlag");
                    SortField = rs.getString("SortField");
                    Nbase = rs.getString("Nbases");
                    DataRange = rs.getString("DataRange");
                    DBname = rs.getString("Nbases");
                    musOrg = rs.getString("B0110");
                }
                //获取老版花名册常用查询ID
                String commonQueryId=this.getParamValue(1, "usual_query",tabid);
                //判断老版花名册是否设置取数条件，若设置将数据迁移至新字段 DataRange
                DataRange = this.operateDataRange(DataRange,commonQueryId,tabid);

                musOrgName = AdminCode.getCodeName("UN", musOrg);
                if (StringUtils.isBlank(musOrgName)) {
                	 musOrgName = AdminCode.getCodeName("UM", musOrg);
				}
                //musOrg = PubFunc.encrypt(musOrg); 不使用加密,否则在前端再选择的时候无法加解密了
                if ("".equals(DataRange) || DataRange == null) {
                    DataRange = "{\"filter\":null,\"condition\":null,\"range_type\":\"0\"}";
                }
                if ("".equals(DBname) || DBname == null) {
                    DBname = "Usr,";
                }
                String musterStyleType = "";
                if (selModuleFlag.indexOf("`") > -1) {
                    String musPro = selModuleFlag.split("`")[0];
                    musPro.substring(2, 4);
                    musterStyleType = musPro.substring(1, 3);
                }
                JSONObject obj = JSONObject.fromObject(DataRange);
                String selfilter = obj.getString("filter");
                String condition = obj.getString("condition");
                String range_type = obj.getString("range_type");
                boolean parttimejobvalue = false;
                if (obj.containsKey("parttimejobvalue")) {
                    parttimejobvalue = obj.getBoolean("parttimejobvalue");
                }
                String from = "";
                String to = "";
                ArrayList list = new ArrayList();
                if ("3".equals(range_type)) {
                	ArrayList<FieldSet> prvfieldsetList =userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
                	boolean result = false;
                	for (FieldSet fieldSet : prvfieldsetList) {
                		if (result) {
							break;
						}
						String condtionFieldset = condition.split(",")[0].split("\\.")[0];
						if (fieldSet.getFieldsetid().equalsIgnoreCase(condtionFieldset)) {
							ArrayList<FieldItem> prvfielditem = userView.getPrivFieldList(condtionFieldset);
							for (FieldItem fieldItem : prvfielditem) {
								if (fieldItem.getItemid().equalsIgnoreCase(condition.split(",")[0].split("\\.")[1])) {
									result = true;
									break;
								}
							}
						}
					}
                	if (result) {
                		String itemid = condition.split(",")[0].split("\\.")[1];
                        FieldItem fielditem = DataDictionary.getFieldItem(itemid);
                        String codeSetId = fielditem.getCodesetid();//代码类id
                        String itemType = fielditem.getItemtype();
                        if (condition.indexOf("|") > -1) {
                            from = condition.split(",")[1].split("\\|")[0];
                            to = condition.split(",")[1].split("\\|")[1];
                            if ("A".equals(itemType) && !"0".equals(codeSetId)) {
                                from = from + "`" + AdminCode.getCodeName(codeSetId, from);
                                to = to + "`" + AdminCode.getCodeName(codeSetId, to);
                            }
                        }
                        condition = condition.split(",")[0] + "." + codeSetId + "." + itemType;
					}else {
						range_type = "0";
						condition = null;
					}
                    

                } else if ("2".equals(range_type)) {
                	
                    if(condition.indexOf("|")>-1) {
                        String lexpr = condition.split("\\|")[0];
                        String factor = condition.split("\\|")[1];
                        FactorList  factorslist=new FactorList(lexpr,factor.toUpperCase(),"",true,false,true,Integer.parseInt(musterType),userView.getUserId());
                        for (Object object : factorslist) {
                        	boolean result = false;
                            Factor facto = (Factor) object;
                            String itemid = facto.getFieldname();
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                            if (fieldItem!=null&& "N".equals(fieldItem.getItemtype())) {
                                if (factor.indexOf(itemid+"=null")!=-1) {
                                    condition= condition.replace(itemid+"=null", itemid+"=");
                                }else if (factor.indexOf(itemid+">=null")!=-1) {
                                    condition= condition.replace(itemid+">=null", itemid+">=");
                                }else if (factor.indexOf(itemid+"<=null")!=-1) {
                                    condition= condition.replace(itemid+"<=null", itemid+"<=");
                                }else if (factor.indexOf(itemid+"<>null")!=-1) {
                                    condition= condition.replace(itemid+"<>null", itemid+"<>");
                                }else if (factor.indexOf(itemid+">null")!=-1) {
                                    condition= condition.replace(itemid+">null", itemid+">");
                                }else if (factor.indexOf(itemid+"<null")!=-1) {
                                    condition= condition.replace(itemid+"<null", itemid+"<");
                                }
                            }
                            String fieldsetid = fieldItem.getFieldsetid();
                            ArrayList<FieldItem> fieldItemList= userView.getPrivFieldList(fieldsetid);
                            for (FieldItem fieldItem2 : fieldItemList) {
								if (fieldItem2.getItemid().equalsIgnoreCase(itemid)) {
									result = true;
									break;
								}
							}
                            if (result) {
                            	HashMap map = new HashMap();
                                map.put("itemid",itemid);
                                map.put("name", facto.getHz());
                                list.add(map);
							}else {
								range_type = "0";
								condition = "";
								break;
							}
                            
                        }
                        condition = SafeCode.encode(condition);
                        
                    }else {
                    	range_type = "0";
						condition = null;
					}
                    
                }

                if (selfilter == "null") {
                    selfilter = "-1";
                }
                //排序指标需要再处理
                ArrayList<String> fieldList = getPrivFieldList(musterType);
                StringBuffer sortFieldbuffer= new  StringBuffer("");
                if (StringUtils.isNotBlank(SortField)) {
                    String[]  array = SortField.split(",");
                    for (int i = 0; i < array.length; i++) {
                        if (StringUtils.isNotBlank(array[i])) {
                            String fielditem = array[i].split("\\.")[1].substring(0, array[i].split("\\.")[1].length()-1);
                            if (fieldList.contains(fielditem.toLowerCase())||("e01a1".equals(fielditem.toLowerCase())&&"K01".equalsIgnoreCase(array[i].split("\\.")[0]))) {
                                sortFieldbuffer.append(array[i]);
                                sortFieldbuffer.append(",");
                            }
                        }
                    }
                }
                HashMap otherData = new HashMap();
                otherData.put("name", name);
                otherData.put("SortField", sortFieldbuffer.toString());
                otherData.put("filter", selfilter);
                otherData.put("condition", condition);
                otherData.put("range_type", range_type);
                otherData.put("musType", musterStyleType);
                otherData.put("DBname", DBname);
                otherData.put("from", from);
                otherData.put("to", to);
                otherData.put("musOrg", musOrg);
                otherData.put("musOrgName", musOrgName);
                otherData.put("moreFieldCord", list);
                otherData.put("parttimejobvalue", parttimejobvalue);
                data.add(otherData);
            } else if ("fieldData".equals(dataFlag)) {
                String sqlBase = "select Field_name,ColHz,field_type from lbase where tabid = ?";
                rs = dao.search(sqlBase, value);
                ArrayList<String> fieldList = getPrivFieldList(musterType);
                while (rs.next()) {
                    HashMap map = new HashMap();
                    String fieldId = rs.getString("Field_name");
                    String fieldType = rs.getString("field_type");
                    String fieldSetId = "";
                    String fieldItemId = "";
                    if (fieldId.indexOf(".") > -1) {
                        fieldSetId = fieldId.split("\\.")[0];
                        fieldItemId = fieldId.split("\\.")[1];
                    }

                    FieldSet fieldset = DataDictionary.getFieldSetVo(fieldSetId);
                    String changeFlag = fieldset.getChangeflag();//年月变化标识
                    FieldItem fielditem = DataDictionary.getFieldItem(fieldItemId);
                    if (fielditem != null && (fieldList.contains(fieldItemId.toLowerCase()) || "K01.E01A1".equalsIgnoreCase(fieldId))) {
                        //代码类id
                        String codeSetId = fielditem.getCodesetid();
                        //获取最新的指标描述
                        String fieldName = fielditem.getItemdesc();
                        //			fielditem.setSortable(true);
                        String itemid = fieldId + "." + fieldType + "." + changeFlag + "." + codeSetId + ":" + fieldName;
                        map.put("name", itemid);
                        data.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 老版取数条件迁移至新字段
     * @param dataRange
     * @param commonQueryId
     * @param tabid
     * @return
     */
    private String operateDataRange(String dataRange,String commonQueryId,String tabid) {
        try {
            ContentDAO dao = new ContentDAO(conn);
            if(StringUtils.isBlank(dataRange)){
                if(!StringUtils.isBlank(commonQueryId)){
                    dataRange = "{\"filter\":\""+commonQueryId+"\",\"condition\":null,\"range_type\":\"0\"}";
                    RecordVo vo=new RecordVo("lname");
                    vo.setString("tabid",tabid);
                    vo.setString("datarange",dataRange);
                    dao.updateValueObject(vo);
                }
            }else{
                JSONObject dataRangeJson = JSONObject.fromObject(dataRange);
                String filteridTemp = dataRangeJson.getString("filter");
                String conditionTemp = dataRangeJson.getString("condition");
                String rangeTypeTemp = dataRangeJson.getString("range_type");
                boolean parttimejobvalueTemp = false;
                if (dataRangeJson.containsKey("parttimejobvalue")) {
                    parttimejobvalueTemp = dataRangeJson.getBoolean("parttimejobvalue");
                }
                if(!StringUtils.isBlank(commonQueryId) && (StringUtils.isBlank(filteridTemp) || StringUtils.equalsIgnoreCase(filteridTemp,"null"))){
                    dataRange = "{\"filter\":\""+commonQueryId+"\",";
                    if(!StringUtils.isBlank(conditionTemp) && !StringUtils.equalsIgnoreCase(conditionTemp,"null")){
                        dataRange += "\"condition\":\""+conditionTemp+"\",";
                    }else{
                        dataRange += "\"condition\":null,";
                    }
                    dataRange += "\"parttimejobvalue\":"+parttimejobvalueTemp+",";
                    if(!StringUtils.isBlank(rangeTypeTemp) && !StringUtils.equalsIgnoreCase(rangeTypeTemp,"null")){
                        dataRange += "\"range_type\":\""+rangeTypeTemp+"\"}";
                    }else {
                        dataRange += "\"range_type\":\"0\"}";
                    }
                    RecordVo vo=new RecordVo("lname");
                    vo.setString("tabid",tabid);
                    vo.setString("datarange",dataRange);
                    dao.updateValueObject(vo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dataRange;
    }

    /**
     *
     * @param type 节点类型
     * @param property 节点名称
     * @param tabid 花名册id
     * @return
     */
    private String getParamValue(int type, String property, String tabid) {
        String paramvalue = "";
        String path = "/report/param";
        switch(type){
            case 1:
                path = "/report/param";
                break;
            case 2:
                path = "/report";
                break;
        }
        XPath xpath;
        try {
            Document doc = getDocumentValue(tabid);
            xpath = XPath.newInstance(path);
            Element element=(Element)xpath.selectSingleNode(doc);
            if(element!=null){
                paramvalue = element.getAttributeValue(property);
                paramvalue=paramvalue!=null&&paramvalue.trim().length()>0?paramvalue:"";
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return paramvalue;
    }

    /**
     * 获取老版花名册取数规则xml
     * @param tabid 花名册id
     * @return
     */
    private Document getDocumentValue(String tabid) {
        Document doc = null;
        String xml = "";
        RecordVo vo=new RecordVo("lname");
        vo.setString("tabid",tabid);
        StringBuffer temp_xml=new StringBuffer();
        temp_xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
        temp_xml.append("<report>");
        temp_xml.append("</report>");
        try{
            ContentDAO dao=new ContentDAO(this.conn);
            if(dao.isExistRecordVo(vo))
            {
                vo=dao.findByPrimaryKey(vo);
                if(vo!=null)
                    xml=vo.getString("xml_style");
            }
            if(StringUtils.isEmpty(xml)){
                xml=temp_xml.toString();
            }
            doc=PubFunc.generateDom(xml);
        }catch(Exception ex){
            xml=temp_xml.toString();
        }
        return doc;
    }

    /**
     * 获取新建分类的styleid
     * @param dao
     * @return
     */
    private String getStyleid( ContentDAO dao) {
        RowSet rowSet = null;
        String newStyleid = "0";
        try {
            ArrayList<String> styleidList = new ArrayList<String>();
            rowSet = dao.search("select styleid from LSTYLE");
            while (rowSet.next()) {
                    styleidList.add(rowSet.getString("styleid"));
            }
            if (styleidList.size()>=776) {
                return "1";
            }else if (styleidList.size()>=100) {
                String[] letter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
                List<String> styleidlist = new ArrayList<String>();
                for (int i = 0; i < letter.length; i++) {
                    for (int j = 0; j < letter.length; j++) {
                        styleidlist.add(letter[i]+letter[j]);
                    }
                }
                for (String styleid : styleidlist) {
                    if (!styleidList.contains(styleid)) {
                        newStyleid = styleid;
                        break;
                    }
                }
            }else{
                if (styleidList.size()==0) {
                    newStyleid = "00";
                }else {
                    String[] number = {"0","1","2","3","4","5","6","7","8","9"};
                    List<String> styleidlist = new ArrayList<String>();
                    for (int i = 0; i < number.length; i++) {
                        for (int j = 0; j < number.length; j++) {
                            styleidlist.add(number[i]+number[j]);
                        }
                    }
                    for (String styleid : styleidlist) {
                        if (!styleidList.contains(styleid)) {
                            newStyleid = styleid;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return newStyleid;
    }
    /**
     * 获取人员范围与人员范围中高级权限
     * @param userView
     * @param nbase 人员库
     * @return
     * @throws GeneralException 
     */
    private String getEmpWhere(UserView userView) {
        StringBuffer b0110 = new StringBuffer("");
        try {
            String condPriv = userView.getPrivExpression();//人员范围
            if(condPriv ==null || condPriv.trim().length()==0)
                return "";
            String[] tmps =condPriv.split("\\|");
            if(tmps.length==2){
                FactorList factorslist=new FactorList(tmps[0],tmps[1].toUpperCase(),userView.getDbname(),false,false,true,1,userView.getUserId());
                for (int i =factorslist.size()-1;i>-1;i--) {
                    Factor factor = (Factor) factorslist.get(i);
                    String codeid =  factor.getCodeid();
                  /*  if (StringUtils.equals(codeid, "UM")) {
                        factorslist.remove(i);
                    }*/
                }
                for (int i =0;i<factorslist.size();i++) {
                    if (i>0) {
                        b0110.append(",");
                    }
                    Factor factor = (Factor) factorslist.get(i);
                    String codeid =  factor.getCodeid();
                    if (StringUtils.equals(codeid, "UN")||StringUtils.equals(codeid, "UM")) {
                        String value = factor.getValue();
                        if (StringUtils.equals(value, "*")) {
                             ArrayList<String> list = getTopOrgList();
                             for (int j = 0; j < list.size(); j++) {
                                if (j>0) {
                                    b0110.append(",");
                                }
                                b0110.append(list.get(j));
                            }
                        }else {
                            if (value.endsWith("*")) {
                                b0110.append(value.substring(0, value.length()-1));
                            }else {
                                b0110.append(value.substring(0, value.length()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110.toString();
    }
    /**
     * 获取组织机构权限范围
     * @param busiID
     * @return
     */
    private String getUnitIdByBusi(UserView userView,String busiID){
        String orgPriv = null;
        StringBuffer priv = new StringBuffer("");
        try {
            orgPriv = userView.getUnitIdByBusi(busiID);
            if(orgPriv == null || orgPriv.trim().length() == 0)
                return "";
            orgPriv = orgPriv.replaceAll("`",",");
            String[] orgPrivs = orgPriv.split(",");
            for(int i = 0 ; i < orgPrivs.length ; i++){
                if (i>0) {
                    priv.append(",");
                }
                if ("UN".equals(orgPrivs[i].toUpperCase())) {
                    return "UN";
                }else if (orgPrivs[i].toUpperCase().startsWith("UN")&&orgPrivs[i].length()>2) {
                    priv.append(orgPrivs[i].substring(2));
                }else if (orgPrivs[i].toUpperCase().startsWith("UM")&&orgPrivs[i].length()>2) {
                    priv.append(orgPrivs[i].substring(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priv.toString();
    }
    /**
     * 获取顶级机构的单位编码
     * @return
     */
    private ArrayList<String> getTopOrgList() {
        ArrayList<String> list = new ArrayList<String>();
        RowSet rowSet = null;
        try {
            String sql = "select codeitemid from organization where codeitemid = parentid and codesetid = 'UN'";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                list.add(rowSet.getString("codeitemid"));
            }
        } catch (Exception e) {
           e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return list;
    }
    /**
     * 
     * @param codeitemid
     * @return
     */
    private String getParentid(String codeitemid) {
        String parentid = "";
        RowSet rowSet = null;
        try {
            String sql = "select parentid from organization where codeitemid <> parentid and codeitemid = '"+codeitemid+"'";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql);
            while (rowSet.next()) {
              parentid = rowSet.getString("parentid");
            }
        } catch (Exception e) {
           e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return parentid;
    }
    private ArrayList<String> getPrivFieldList(String musterType) {
        ArrayList<String> privFieldList = new ArrayList<String>();
        ArrayList<FieldSet> privFieldSetList = new ArrayList<FieldSet>();
        if ("1".equals(musterType)) {
            privFieldSetList = userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
        }else if ("2".equals(musterType)) {
            privFieldSetList = userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
        }else if ("3".equals(musterType)) {
            privFieldSetList = userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
        }else if ("4".equals(musterType)) {
            privFieldSetList = userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);
        }
        //遍历privFieldSetList 移除未构库的子集
        for (int i = privFieldSetList.size()-1; i >-1; i--) {
            FieldSet fieldset = (FieldSet) privFieldSetList.get(i);
            if("0".equalsIgnoreCase(fieldset.getUseflag())) {
                privFieldSetList.remove(i);
            }
        }
        for (int i = privFieldSetList.size()-1; i >-1; i--) {
            FieldSet fieldset = (FieldSet) privFieldSetList.get(i);
            ArrayList<FieldItem> fieldItemList = userView.getPrivFieldList(fieldset.getFieldsetid());
            if (fieldItemList!=null) {
                for (int j = 0; j <fieldItemList.size(); j++) {
                    FieldItem fieldItem = fieldItemList.get(j);
                    if (!"0".equalsIgnoreCase(fieldItem.getUseflag())) {
                        privFieldList.add(fieldItem.getItemid());
                    }
                }
            }
        }
        return privFieldList;
    }
    class listComparator implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            if (o1.length()>o2.length()) {
                return 1;
            }else if (o1.length()<o2.length()) {
                return -1;
            }else {
                if (o1.compareTo(o2)<0) {
                    return -1;
                }else {
                    return 1;
                }
            }
           
        }
        
    }
    @Override
    public List getFielditemList(String fielditemids) throws GeneralException {
        List  list = new ArrayList<Object>();
        try {
            String[] itemarray = fielditemids.split("\\.");
            FieldItem fieldItem  = DataDictionary.getFieldItem(itemarray[1],itemarray[0]);
            List<FieldItem> itemlist= userView.getPrivFieldList(fieldItem.getFieldsetid(), 1);
            for (FieldItem item : itemlist) {
                if ("M".equals(item.getItemtype())) {
                    continue;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("dataName", item.getItemdesc());
                map.put("dataValue", item.getFieldsetid()+"."+item.getItemid().toUpperCase()+"."+item.getCodesetid()+"."+item.getItemtype());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List getItemList(String fielditemid) throws GeneralException {
        List  list = new ArrayList<Object>();
        try {
            String[] itemarray = fielditemid.split("\\.");
            FieldItem fieldItem  = DataDictionary.getFieldItem(itemarray[1],itemarray[0]);
            List<FieldItem> itemlist= userView.getPrivFieldList(fieldItem.getFieldsetid(), 1);
            for (FieldItem item : itemlist) {
                if ("M".equals(item.getItemtype())) {
                    continue;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("dataName", item.getItemdesc());
                map.put("dataValue",item.getItemid().toUpperCase());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
