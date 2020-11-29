package com.hjsj.hrms.module.talentmarkets.utils;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateOptionField;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description 内部人才市场工具类 用于提供公共方法
 * @Author wangz
 * @Date 2019/7/26 9:37
 * @Version V1.0
 **/
public class TalentMarketsUtils {
    enum templateType {
        /**
         * 岗位竞聘模板
         */
        releasePostTemplate,
        /**
         * 录用审批模板
         */
        hireTemplate,
        /**
         * 应聘报名模板
         */
        applyTemplate
    }
    /**
     * 门户页面历史竞聘人次、竞聘岗位分析、人员列表
     * 查询结束状态的岗位数据状态值
     */
    public static String END_STATUS = " ('06','07','09') ";

    /**
     * 递归生成功能导航菜单的json串
     *
     * @param name 菜单名
     * @param list 菜单内容
     * @return
     */
    public static String getMenuStr(String name, ArrayList list) {
        StringBuffer str = new StringBuffer();
        try {
            if (name.length() > 0) {
                str.append("<jsfn>{xtype:'button',text:'" + name + "'");
            }
            str.append(",menu:{collapsible:false,items:[");
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if (i != 0) {
                    str.append(",");
                }
                str.append("{");
                if (bean.get("xtype") != null && bean.get("xtype").toString().length() > 0) {
                    str.append("xtype:'" + bean.get("xtype") + "',");
                }
                if (bean.get("text") != null && bean.get("text").toString().length() > 0) {
                    str.append("text:'" + bean.get("text") + "'");
                }
                if (bean.get("id") != null && bean.get("id").toString().length() > 0) {
                    str.append(",id:'" + bean.get("id") + "'");
                }
                if (bean.get("fntype") != null && bean.get("fntype").toString().length() > 0) {
                    str.append(",fntype:'" + bean.get("fntype") + "'");
                    str.append(",cusMenu:'cusMenu'");
                }
                if (bean.get("handler") != null && bean.get("handler").toString().length() > 0) {
                    //时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
                    if (bean.get("xtype") != null && "datepicker".equalsIgnoreCase(bean.get("xtype").toString())) {
                        //消除今天 按钮提示文字
                        str.append(",todayTip:''");
                        str.append(",handler:function(picker, date){" + bean.get("handler") + ";}");
                    } else {
                        str.append(",handler:function(){" + bean.get("handler") + "();}");
                    }
                }
                if (bean.get("icon") != null && bean.get("icon").toString().length() > 0) {
                    str.append(",icon:'" + bean.get("icon") + "'");
                }
                if (bean.get("value") != null && bean.get("value").toString().length() > 0) {
                    str.append(",value:" + bean.get("value") + "");
                }
                ArrayList menulist = (ArrayList) bean.get("menu");
                if (menulist != null && menulist.size() > 0) {
                    str.append(getMenuStr("", (ArrayList) bean.get("menu")));
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
     * 获取指定模块的权限sql
     * 4为组织机构
     *
     * @param nmoudle
     * @param userView
     * @param tableName 防止字段重名 规定哪个表
     * @return
     */
    public static String getPrivSql(String tableName, String nmoudle, UserView userView) {
        StringBuffer sqlBuffer = new StringBuffer();
        //组织机构业务范围>操作单位>人员范围
        String unitPriv = userView.getUnitIdByBusi(nmoudle);
        //没有任何权限,所有数据都不展示
        if(userView.isSuper_admin()||StringUtils.equalsIgnoreCase("UN`", unitPriv)){
            return " and 1=1 ";
        }
        if (StringUtils.isBlank(unitPriv) || StringUtils.equalsIgnoreCase("UN",unitPriv) ||StringUtils.equalsIgnoreCase("UM",unitPriv)) {
            return " and 1=2 ";
        }
        //每种权限分割符可能会不一样
        unitPriv = unitPriv.replaceAll("`", ",");
        String[] units = unitPriv.split(",");
        List<String> privUnitList = new ArrayList<String>();
        for (String unit : units) {
            if(StringUtils.isBlank(unit)){
                continue;
            }
            unit = unit.substring(2);
            privUnitList.add(unit);
        }
        String b0110LikeSql = loopConstrutLikeSql(tableName, "b0110", privUnitList);
        String e0122LikeSql = loopConstrutLikeSql(tableName, "e0122", privUnitList);
        sqlBuffer.append(" and( ").append(b0110LikeSql).append(" or ").append(e0122LikeSql).append(")");
        return sqlBuffer.toString();
    }

    /**
     * 循环生成likesql
     *
     * @param column 用哪个字段进行like
     * @param values 要被liske的值
     * @return 拼装成的sql
     */
    private static String loopConstrutLikeSql(String tableName, String column, List<String> values) {
        StringBuffer loopStr = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            String unit = values.get(i);
            if (StringUtils.isNotEmpty(tableName)) {
                loopStr.append(tableName.toLowerCase()).append(".");
            }
            loopStr.append(column).append("  like '").append(unit).append("%'");
            if (i < values.size() - 1) {
                loopStr.append(" or ");
            }
        }
        return loopStr.toString();
    }


    /**
     * 根据相关列数据生成相关列对象
     *
     * @param itemType
     * @param codeSetId
     * @param columnId
     * @param columnDesc
     * @param loadType
     * @param columnWidth
     * @param extraParam
     * @return
     */
    public static ColumnsInfo getColumnsInfo(String itemType, String codeSetId, String columnId, String columnDesc, int loadType, int columnWidth, Map<String, Object> extraParam) throws GeneralException {
        FieldItem fieldItem = new FieldItem();
        ColumnsInfo columnsInfo;
        fieldItem.setItemtype(itemType);
        fieldItem.setCodesetid(codeSetId);
        fieldItem.setItemid(columnId);
        fieldItem.setItemdesc(columnDesc);
        columnsInfo = TalentMarketsUtils.getColumnsInfo(fieldItem, loadType, columnWidth, extraParam);
        return columnsInfo;
    }

    /**
     * 根据相关列数据生成相关列对象
     *
     * @param fieldItem
     * @param loadType
     * @param columnWidth
     * @param extraParam
     * @return
     */
    public static ColumnsInfo getColumnsInfo(FieldItem fieldItem, int loadType, int columnWidth, Map<String, Object> extraParam) throws GeneralException {
        ColumnsInfo columnsInfo = new ColumnsInfo(fieldItem);
        try {
            columnsInfo.setLoadtype(loadType);
            columnsInfo.setColumnWidth(columnWidth);
            //额外参数赋值
            if (extraParam != null) {
                for (String key : extraParam.keySet()) {
                    Field field = columnsInfo.getClass().getDeclaredField(key);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(columnsInfo, extraParam.get(key));
                    }
                }
            }
        } catch (Exception e) {
            throw new GeneralException("getColumnsInfoError");
        }
        return columnsInfo;
    }

    /**
     * 加载指标集A`B`K`Y:***
     *
     * @param fieldset 指标集 子集代码号 如A01`A02`Y:z81  业务字典格式为Y:代码号
     * @param conn     数据库链接
     * @return
     * @throws GeneralException
     */
    public static ArrayList getFieldSetList(String fieldset, Connection conn, UserView userView) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        List privSetList = userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
        String privFieldSet = ",";
        for (int i = 0; i < privSetList.size(); i++) {
            FieldSet fieldSet = (FieldSet) privSetList.get(i);
            privFieldSet = privFieldSet + fieldSet.getFieldsetid() + ",";
        }
        RowSet rs = null;
        try {
            if (fieldset == null || fieldset.length() == 0) {
                return list;
            }
            String[] strs = fieldset.split("`");
            sql.append(" select fieldsetid,fieldsetdesc from ");
            sql.append(" fieldset where UseFlag=1 and (");
            StringBuffer sql2 = new StringBuffer("select fieldsetid,fieldsetdesc from ");
            for (int i = 0; i < strs.length; i++) {
                if (strs[i].contains("Y")) {
                    String[] fieldSetIds = strs[i].split(":");
                    sql2.append(" t_hr_busitable where UseFlag=1 and ");
                    sql2.append(" fieldsetid in( ");
                    for (int j = 1; j < fieldSetIds.length; j++) {
                        String fieldSetId = fieldSetIds[j];
                        sql2.append("'");
                        sql2.append(fieldSetId);
                        sql2.append("',");
                    }
                    sql2.setLength(sql2.length() - 1);
                    sql2.append(")");
                } else {
                    String fieldSetId = strs[i];
                    if (i == 0) {
                        sql.append(" fieldsetid like '");
                    } else {
                        sql.append(" or fieldsetid like '");
                    }
                    sql.append(fieldSetId);
                    sql.append("%'");
                }
            }
            sql.append(")");
            sql.append(" order by fieldSetId, Displayorder ");
            rs = dao.search(sql.toString());
            HashMap map;
            String fieldsetid;
            String fieldsetdesc;
            while (rs.next()) {
                map = new HashMap();
                fieldsetid = rs.getString("fieldsetid");
                fieldsetdesc = rs.getString("fieldsetdesc");
                map.put("id", fieldsetid);
                map.put("text", fieldsetdesc);
                //只显示权限范围内的子集
                if (userView.isSuper_admin()) {
                    list.add(map);
                } else if (StringUtils.contains(privFieldSet.toUpperCase(), "," + fieldsetid.toUpperCase() + ",")) {
                    list.add(map);
                }
            }
            rs = dao.search(sql2.toString());
            while (rs.next()) {
                map = new HashMap();
                fieldsetid = rs.getString("fieldsetid");
                fieldsetdesc = rs.getString("fieldsetdesc");
                map.put("id", "Y:" + fieldsetid);
                map.put("text", fieldsetdesc);
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    /**
     * 获取指标集中的所有指标
     *
     * @param value       子集代码号
     * @param conn        数据库连接
     * @param filterItems 过滤指标项 格式 fieldsetid:itemid 多个用,隔开 如 A01:a0110,B01:b0110
     * @return
     * @throws GeneralException
     */
    public static ArrayList getFieldItemList(String value, Connection conn, String filterItems, UserView userView) throws GeneralException {
        StringBuffer buf = new StringBuffer();
        ArrayList list = new ArrayList();
        boolean isBusiness = false;
        RowSet rs = null;
        List<FieldItem> privFieldList = new ArrayList();
        try {
            if (value == null || value.length() == 0) {
                return list;
            }
            buf.append(" select itemid,itemdesc,itemtype,fieldsetid,codesetid from ");
            if (!value.contains("Y:")) {
                buf.append(" fielditem ");
                privFieldList = userView.getPrivFieldList(value, Constant.USED_FIELD_SET);
            } else {
                buf.append(" t_hr_busifield ");
                value = value.split(":")[1];
                isBusiness = true;
            }
            buf.append(" where useflag=1 ");
            if(isBusiness){
                buf.append(" and state = 1");
            }
            if (StringUtils.isNotEmpty(value) && value.length() > 0) {
                buf.append("and fieldsetid=?");
                buf.append(" order by displayid");
                list.add(value);
            }

            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(buf.toString(), list);
            list.clear();
            HashMap map;
            while (rs.next()) {
                map = new HashMap();
                String itemId = rs.getString("itemid");
                String itemDesc = rs.getString("itemdesc");
                String itemType = rs.getString("itemtype");
                String fieldSetId = rs.getString("fieldsetid");
                if (StringUtils.isNotEmpty(filterItems)) {
                    if (("," + filterItems + ",").toUpperCase().contains(("," + fieldSetId+ ":" + itemId + ",").toUpperCase())) {
                        continue;
                    }
                }

                map.put("id", itemId);
                map.put("fieldItemId", itemId);
                map.put("text", itemDesc);
                map.put("fieldItemType", itemType);
                map.put("fieldSetId", fieldSetId);
                map.put("checked", false);
                map.put("leaf", Boolean.TRUE);
                if (!isBusiness) {
                    if (userView.isSuper_admin()) {
                        list.add(map);
                    } else {
                        for (FieldItem fieldItem : privFieldList) {
                            if (StringUtils.equalsIgnoreCase(itemId, fieldItem.getItemid())) {
                                list.add(map);
                                break;
                            }
                        }
                    }
                } else {
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    /**
     * 添加合并列头
     *
     * @param columns
     * @param mergedesc
     * @param mergedescIndex
     * @param num
     * @param info
     */
    public static void addTopHeadList(ArrayList columns, String mergedesc,
                                      int mergedescIndex, int num, ColumnsInfo info) {
        ArrayList tableheadlist = new ArrayList();
        Object obj = columns.get(mergedescIndex - num);
        String name = obj.getClass().getName();
        ColumnsInfo columstemp = null;
        if ("java.util.HashMap".equalsIgnoreCase(name)) {
            ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
            for (int n = 0; n < list.size(); n++) {
                columstemp = list.get(n);
                tableheadlist.add(columstemp);
            }
        } else {
            columstemp = (ColumnsInfo) columns.get(mergedescIndex - num);
            if ("custom_name".equals(columstemp.getColumnId())) {
                columstemp.setColumnDesc("流程状态");
            }
            tableheadlist.add(columstemp);
        }
        tableheadlist.add(info);
        HashMap topHead = new HashMap();
        topHead.put("text", mergedesc);
        topHead.put("items", tableheadlist);
        //当合并时移除最后一列
        columns.remove(mergedescIndex - num);
        columns.add(topHead);
    }

    /**
     * 获取配置信息
     *
     * @return 配置信息map
     */
    private static Map getSettingMap() throws GeneralException {
        JSONObject settingJsonObject;
        Map settingMap = new HashMap();
        try {
            RecordVo vo = ConstantParamter.getRealConstantVo("TALENTMARKETS_PARAM");
            if (vo == null) {
                return null;
            }
            JSONObject competition = null;
            String releasePostTemplateId = "";
            String applyTemplateTemplateId = "";
            String hireTemplateId = "";
            String applyResumeRnameId = "";
            String postDetailRnameId = "";
            String maxCompetitionPost = "";
            String talentDisplayTemplateId = "";
            String cancelTemplateId = "";
            String talentRname = "";
            JSONArray postFields = new JSONArray();
            JSONArray resumePostTypeField = new JSONArray();
            JSONArray resumeSelfIntroduction = new JSONArray();
            //岗位竞聘模板指标对应关系
            JSONObject releasePostTemplateRelation = null;
            //应聘报名模板指标对应关系
            JSONObject applyTemplateRelation = null;
            //录用审批模板指标对应关系
            JSONObject hireTemplateRelation = null;
            String settingJson = vo.getString("str_value");
            if (StringUtils.isNotEmpty(settingJson)) {
                settingJsonObject = JSONObject.fromObject(settingJson);
                Set set = settingJsonObject.keySet();
                if (set.contains("competition")) {
                    competition = settingJsonObject.getJSONObject("competition");
                    JSONObject templates = competition.getJSONObject("templates");
                    //岗位竞聘模板tabid
                    releasePostTemplateId = templates.getString("releasePost_template");
                    //竞聘报名模板tabid
                    applyTemplateTemplateId = templates.getString("apply_template");
                    //录用审批模板tabid
                    hireTemplateId = templates.getString("hire_template");
                    JSONObject rnames = competition.getJSONObject("rnames");
                    //应聘简历登记表 tabid
                    applyResumeRnameId = rnames.getString("applyResume_rname");
                    //竞聘岗位详情登记表 tabid
                    postDetailRnameId = rnames.getString("postDetail_ranme");
                    //最多可同时应聘职位数
                    maxCompetitionPost = competition.getString("maxCompetitionPost");
                    //竞聘岗位详情展示项
                    postFields = competition.getJSONArray("postFields");
                    if (competition.containsKey("templatesRelation")) {
                        JSONObject templatesRelation = competition.getJSONObject("templatesRelation");
                        if (templatesRelation.containsKey("releasePost_template")) {
                            releasePostTemplateRelation = templatesRelation.getJSONObject("releasePost_template");
                        }
                        if (templatesRelation.containsKey("apply_template")) {
                            applyTemplateRelation = templatesRelation.getJSONObject("apply_template");
                        }
                        if (templatesRelation.containsKey("hire_template")) {
                            hireTemplateRelation = templatesRelation.getJSONObject("hire_template");
                        }
                    }
                }
                if (set.contains("talentHall")) {
                    //竞聘岗位详情展示项
                    JSONObject talentHall = settingJsonObject.getJSONObject("talentHall");
                    JSONObject talentHallTemplates = talentHall.getJSONObject("templates");
                    //人才展示申请模版 tabid
                    talentDisplayTemplateId = talentHallTemplates.getString("apply_template");
                    //人才撤销展示模板 tabid
                    cancelTemplateId = talentHallTemplates.getString("cancel_template");
                    //人才卡片岗位类别项
                    resumePostTypeField = talentHall.getJSONArray("resumePostTypeField");
                    //人才卡片个人简介项
                    resumeSelfIntroduction = talentHall.getJSONArray("resumeSelfIntroduction");
                    JSONObject talentHallRnames = talentHall.getJSONObject("rnames");
                    //人才简历登记表 tabid
                    talentRname = talentHallRnames.getString("talent_rname");
                }
                settingMap.put("releasePost_template", releasePostTemplateId);
                settingMap.put("apply_template", applyTemplateTemplateId);
                settingMap.put("hire_template", hireTemplateId);
                settingMap.put("applyResume_rname", applyResumeRnameId);
                settingMap.put("postDetail_ranme", postDetailRnameId);
                settingMap.put("maxCompetitionPost", maxCompetitionPost);
                settingMap.put("postFields", postFields);
                settingMap.put("talentDisplayTemplate", talentDisplayTemplateId);
                settingMap.put("cancel_template", cancelTemplateId);
                settingMap.put("resumePostTypeField", resumePostTypeField);
                settingMap.put("resumeSelfIntroduction", resumeSelfIntroduction);
                settingMap.put("talent_rname", talentRname);
                settingMap.put("releasePostTemplateRelation", releasePostTemplateRelation);
                settingMap.put("applyTemplateRelation", applyTemplateRelation);
                settingMap.put("hireTemplateRelation", hireTemplateRelation);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

        return settingMap;
    }

    /**
     * 获取岗位竞聘模板号
     *
     * @return 岗位竞聘模板号
     */
    public static String getReleasePostTemplate() throws GeneralException {
        String releasePostTemplateId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            releasePostTemplateId = (String) settingMap.get("releasePost_template");
        }
        return releasePostTemplateId;
    }

    /**
     * 获取竞聘报名模板
     *
     * @return 竞聘报名模板号
     */
    public static String getApplyTemplate() throws GeneralException {
        String applyTemplateTemplateId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            applyTemplateTemplateId = (String) settingMap.get("apply_template");
        }
        return applyTemplateTemplateId;
    }

    /**
     * 获取录用审批模板
     *
     * @return 录用审批模板号
     */
    public static String getHireTemplate() throws GeneralException {
        String hireTemplateId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            hireTemplateId = (String) settingMap.get("hire_template");
        }
        return hireTemplateId;
    }

    /**
     * 获取应聘简历登记表
     *
     * @return 应聘简历登记表 id
     */
    public static String getApplyResumeRname() throws GeneralException {
        String applyResumeRnameId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            applyResumeRnameId = (String) settingMap.get("applyResume_rname");
        }
        return applyResumeRnameId;
    }

    /**
     * 获取竞聘岗位详情登记表
     *
     * @return 竞聘岗位详情登记表 id
     */
    public static String getCompetitionPostDetailRname() throws GeneralException {
        String postDetailRnameId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            postDetailRnameId = (String) settingMap.get("postDetail_ranme");
        }
        return postDetailRnameId;
    }

    /**
     * 最多可同时应聘职位数
     *
     * @return
     */
    public static int getMaxCompetitionPost() throws GeneralException {
        //默认为2
        int maxNum = 2;
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            maxNum = Integer.parseInt((String) settingMap.get("maxCompetitionPost"));
        }
        return maxNum;
    }

    /**
     * 竞聘岗位详情展示项
     *
     * @return
     */
    public static List<String> listPostFields() throws GeneralException {
        Map settingMap = TalentMarketsUtils.getSettingMap();
        List<String> postFieldsList = new ArrayList<String>();
        if (MapUtils.isNotEmpty(settingMap)) {
            JSONArray postFields = (JSONArray) settingMap.get("postFields");
            TalentMarketsUtils.jsonArrayToList(postFields, postFieldsList);
        }
        return postFieldsList;
    }

    /**
     * 人才展示申请模版
     *
     * @return
     */
    public static String getTalentDisplayTemplate() throws GeneralException {
        String talentDisplayTemplateId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            talentDisplayTemplateId = (String) settingMap.get("talentDisplayTemplate");
        }
        return talentDisplayTemplateId;
    }

    /**
     * 人才展示撤销模版
     *
     * @return
     */
    public static String getCancelTemplate() throws GeneralException {
        String cancelTemplateId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            cancelTemplateId = (String) settingMap.get("cancel_template");
        }
        return cancelTemplateId;
    }

    /**
     * 人才卡片岗位类别项
     *
     * @return
     */
    public static String getResumePostTypeField() throws GeneralException {
        Map settingMap = TalentMarketsUtils.getSettingMap();
        String resumePostTypeField = "";
        if (MapUtils.isNotEmpty(settingMap)) {
            JSONArray resumePostTypeFields = (JSONArray) settingMap.get("resumePostTypeField");
            if(resumePostTypeFields.size()>0){
                resumePostTypeField =(String) resumePostTypeFields.get(0);
            }
        }
        return resumePostTypeField;
    }


    /**
     * 人才简历登记表
     *
     * @return
     */
    public static String getTalentRname() throws GeneralException {
        String talentRnameId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            talentRnameId = (String) settingMap.get("talent_rname");
        }
        return talentRnameId;
    }

    public static String getResumeSelfIntroduction() throws GeneralException{
        String resumeSelfIntroduction = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            if (((JSONArray) settingMap.get("resumeSelfIntroduction")).size() > 0) {
                resumeSelfIntroduction = (String) ((JSONArray) settingMap.get("resumeSelfIntroduction")).get(0);
            }
        }
        return resumeSelfIntroduction;
    }

    /**
     * 获取岗位竞聘模板指标对应关系
     *
     * @return
     */
    public static JSONObject getReleasePostTemplateRelation() throws GeneralException {
        JSONObject releasePostTemplateRelation = null;
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            releasePostTemplateRelation = (JSONObject) settingMap.get("releasePostTemplateRelation");
        }
        return releasePostTemplateRelation;
    }

    /**
     * 获取应聘报名模板指标对应关系
     *
     * @return
     */
    public static JSONObject getApplyTemplateRelation() throws GeneralException {
        JSONObject releasePostTemplateRelation = null;
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            releasePostTemplateRelation = (JSONObject) settingMap.get("applyTemplateRelation");
        }
        return releasePostTemplateRelation;
    }

    /**
     * 获取录用审批模板指标对应关系
     *
     * @return
     */
    public static JSONObject getHireTemplateRelation() throws GeneralException {
        JSONObject hireTemplateRelation = null;
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            hireTemplateRelation = (JSONObject) settingMap.get("hireTemplateRelation");
        }
        return hireTemplateRelation;
    }

    private static void jsonArrayToList(JSONArray jsonArray, List dataList) {
        if (jsonArray != null) {
            for (Object field : jsonArray) {
                dataList.add((String) field);
            }
        }
    }

    /**
     *
     * 与人事异动集成  创建临时表并初始化记录
     *
     * @param templateType 是哪个模板
     * @param conn         数据库连接
     * @param userView
     * @param records      记录数据
     * @return tabId 模板id
     */
    public static String initTempTemplateTable(String templateType, Connection conn, UserView userView, List<MorphDynaBean> records) throws GeneralException {
        String tabId = "";
        //岗位竞聘模板
        if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.releasePostTemplate.toString())) {
            tabId = TalentMarketsUtils.getReleasePostTemplate();
        } else if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.hireTemplate.toString())) {
            tabId = TalentMarketsUtils.getHireTemplate();
        } else if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.applyTemplate.toString())) {
            tabId = TalentMarketsUtils.getApplyTemplate();
        }
        if (StringUtils.isEmpty(tabId)) {
            throw new GeneralException("noSetingData");
        }
        String tableName = "";
        ContentDAO dao = new ContentDAO(conn);
        //自助用户
        int isBusinessUser = 0;
        //业务用户
        int selfServiceUser = 4;
        TemplateBo templateBo = new TemplateBo(conn, userView, Integer.parseInt(tabId));
        //岗位管理 模板用的模块号都是1 人事异动
        templateBo.setModuleId("1");
        //因为是流程并没有发起 所以taskid是0
        templateBo.setTaskId("0");
        DbWizard dbwizard = new DbWizard(conn);
        StringBuffer a0100s = new StringBuffer();
        String a0100Temp = userView.getA0100();
        try {
            if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.applyTemplate.toString())) {
                tableName = "g_templet_" + tabId;
                templateBo.createTempTemplateTable("");
            } else if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.releasePostTemplate.toString())||StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.hireTemplate.toString())) {
                tableName = userView.getUserName() + "templet_" + tabId;
                templateBo.createTempTemplateTable(userView.getUserName());
            }
            if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.releasePostTemplate.toString())) {
                JSONObject releasePostTemplateRelation = TalentMarketsUtils.getReleasePostTemplateRelation();
                if (releasePostTemplateRelation == null) {
                    throw new GeneralException("notSetReleasePostTemplatePlan");
                } else {
                    if (releasePostTemplateRelation.isEmpty()||!releasePostTemplateRelation.containsKey("z8101")||!releasePostTemplateRelation.containsKey("z8105")||!releasePostTemplateRelation.containsKey("z8107")) {
                        throw new GeneralException("notSetReleasePostTemplatePlan");
                    }
                }
                //获取模板表与业务字典表z81的对应关系。
                String primaryKeyField = releasePostTemplateRelation.getString("z8101");
                for (MorphDynaBean record : records) {
                    String e01a1Value = (String) record.get("e01a1");
                    String e01a1RealValue = e01a1Value.split("`")[0];
                    String e01a1Desc = e01a1Value.split("`")[1];
                    RecordVo tabVo = new RecordVo(tableName);
                    tabVo.setObject("e01a1", e01a1RealValue);
                    tabVo.setObject("submitflag", "1");
                    IDGenerator idg = new IDGenerator(2, conn);
                    //已存在该岗位
                    if (dao.isExistRecordVo(tabVo)) {
                        tabVo.setObject("submitflag", "1");
                        dao.updateValueObject(tabVo);
                        continue;
                    }
                    a0100s.append("'").append(e01a1RealValue).append("'").append(",");
                    tabVo.setString("seqnum", CreateSequence.getUUID());
                    tabVo.setObject("state", 0);
                    dao.addValueObject(tabVo);
                    ArrayList paramList = new ArrayList();
                    paramList.add(e01a1RealValue);
                    templateBo.impDataFromArchive(paramList,"@K"); //岗位
                    String primaryKey = idg.getId("z81.z8101");
                    tabVo.setObject(primaryKeyField.toLowerCase(), primaryKey);
                    tabVo.setObject("codeitemdesc_1", e01a1Desc);
                    dao.updateValueObject(tabVo);
                    //String e0122RealValue = ((String) record.get("e0122")).split("`")[0];
                    //paramList.add(tabVo);
                }
                //dao.addValueObject(paramList);

                //更新a0000
                if (a0100s.length() > 0) {
                    a0100s.setLength(a0100s.length() - 1);
                    StringBuffer sql = new StringBuffer();
                    sql.append("update ").append(tableName).append(" set a0000=(select a0000 from organization where ").
                            append(tableName).append(".e01a1").append("=organization.codeitemid ) where ").append(tableName).append(".e01a1 in(").append(a0100s.toString()).append(")");
                    dbwizard.execute(sql.toString());
                }
            } else if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.hireTemplate.toString())) {
                JSONObject hireTemplateRelation = TalentMarketsUtils.getHireTemplateRelation();
                if (hireTemplateRelation == null) {
                    throw new GeneralException("notSetHireTemplateRelation");
                } else {
                    if (hireTemplateRelation.isEmpty()) {
                        throw new GeneralException("notSetHireTemplateRelation");
                    }
                    String primaryKeyField = hireTemplateRelation.getString("z8101");
                    String z8301Field = "";
                    String e01a1Field = "";
                    String e0122Field = "";
                    String b0110Field = "";
                    if(hireTemplateRelation.containsKey("z8301")){
                        z8301Field = hireTemplateRelation.getString("z8301");
                    }
                    if(hireTemplateRelation.containsKey("e01a1")){
                        e01a1Field = hireTemplateRelation.getString("e01a1");
                    }
                    if(hireTemplateRelation.containsKey("e0122")){
                        e0122Field = hireTemplateRelation.getString("e0122");
                    }
                    if(hireTemplateRelation.containsKey("b0110")){
                        b0110Field = hireTemplateRelation.getString("b0110");
                    }
                    //面试分数
                    String interviewScoreField = "";
                    if (hireTemplateRelation.containsKey("interviewScore")) {
                        interviewScoreField = hireTemplateRelation.getString("interviewScore");
                    }
                    // basepre a0100
                    for (MorphDynaBean record : records) {
                        String nbase = PubFunc.decrypt((String) record.get("nbase_e"));
                        String a0100 = PubFunc.decrypt((String) record.get("a0100_e"));
                        String z8101 = PubFunc.decrypt((String) record.get("z8101_e"));
                        String z8301 = (String) record.get("z8301");
                        String z8305 = (String) record.get("z8305");
                        String z8307 = "";
                        boolean openInterview = TalentMarketsUtils.getOpenInterview(conn);
                        if (openInterview) {
                            z8307 = String.valueOf(record.get("z8307"));
                        }
                        String e01a1Value =z8305.split("`")[0];
                        String b0110Value = TalentMarketsUtils.getOrgItemid(e01a1Value,dao,"UN");
                        String e0122Value = TalentMarketsUtils.getOrgItemid(e01a1Value,dao,"UM");
                        RecordVo vo = new RecordVo(tableName);
                        vo.setObject("basepre", nbase);
                        vo.setObject("a0100", a0100);
                        vo.setObject("state", 0);
                        vo.setObject("submitflag", "1");
                        if (dao.isExistRecordVo(vo)) {
                            vo = dao.findByPrimaryKey(vo);
                            if(StringUtils.isNotEmpty(b0110Field)){
                                if (dbwizard.isExistField(tableName, b0110Field)) {
                                    vo.setObject(b0110Field.toLowerCase(), b0110Value);
                                }
                            }
                            if(StringUtils.isNotEmpty(e01a1Field)){
                                if (dbwizard.isExistField(tableName, e01a1Field)) {
                                    vo.setObject(e01a1Field.toLowerCase(), e01a1Value);
                                }
                            }
                            if(StringUtils.isNotEmpty(e0122Field)){
                                if (dbwizard.isExistField(tableName, e0122Field)) {
                                    vo.setObject(e0122Field.toLowerCase(), e0122Value);
                                }
                            }
                            vo.setObject(primaryKeyField, z8101);
                            vo.setObject(z8301Field, z8301);
                            vo.setString("seqnum", CreateSequence.getUUID());
                            if(openInterview){
                                if(StringUtils.isNotEmpty(interviewScoreField)){
                                    if (dbwizard.isExistField(tableName, interviewScoreField)) {
                                        vo.setObject(interviewScoreField, z8307);
                                    }
                                }
                            }
                            if (vo.hasAttribute("photo")) {
                                vo.removeValue("photo");
                            }
                            dao.updateValueObject(vo);
                        } else {
                            vo.setString("seqnum", CreateSequence.getUUID());
                            ArrayList a0100List= new ArrayList();
                            a0100List.add(a0100);
                            dao.addValueObject(vo);
                            templateBo.impDataFromArchive(a0100List,nbase);
                            if(StringUtils.isNotEmpty(b0110Field)){
                                if (dbwizard.isExistField(tableName, b0110Field)) {
                                    vo.setObject(b0110Field.toLowerCase(), b0110Value);
                                }
                            }
                            if(StringUtils.isNotEmpty(e01a1Field)){
                                if (dbwizard.isExistField(tableName, e01a1Field)) {
                                    vo.setObject(e01a1Field.toLowerCase(), e01a1Value);
                                }
                            }
                            if(StringUtils.isNotEmpty(e0122Field)){
                                if (dbwizard.isExistField(tableName, e0122Field)) {
                                    vo.setObject(e0122Field.toLowerCase(), e0122Value);
                                }
                            }
                            if(openInterview){
                                if(StringUtils.isNotEmpty(interviewScoreField)){
                                    if (dbwizard.isExistField(tableName, interviewScoreField)) {
                                        vo.setObject(interviewScoreField, z8307);
                                    }
                                }
                            }
                            vo.setObject(primaryKeyField, z8101);
                            vo.setObject(z8301Field, z8301);
                            dao.updateValueObject(vo);
                        }
                    }

                }
            } else if (StringUtils.equalsIgnoreCase(templateType, TalentMarketsUtils.templateType.applyTemplate.toString())) {
                tableName = "g_templet_" + tabId;
                templateBo.createTempTemplateTable("");
                JSONObject applyTemplateRelation = TalentMarketsUtils.getApplyTemplateRelation();
                if (applyTemplateRelation == null) {
                    throw new GeneralException("notSetApplyPostTemplatePlan");
                } else {
                    if (applyTemplateRelation.isEmpty()) {
                        throw new GeneralException("notSetApplyPostTemplatePlan");
                    }
                }
                String primaryKeyField = applyTemplateRelation.getString("z8101");
                String z8301Field = "";
                String e01a1Field = "";
                String e0122Field = "";
                String b0110Field = "";
                if(applyTemplateRelation.containsKey("z8301")){
                    z8301Field = applyTemplateRelation.getString("z8301");
                }
                if(applyTemplateRelation.containsKey("e01a1")){
                    e01a1Field = applyTemplateRelation.getString("e01a1");
                }
                if(applyTemplateRelation.containsKey("e0122")){
                    e0122Field = applyTemplateRelation.getString("e0122");
                }
                if(applyTemplateRelation.containsKey("b0110")){
                    b0110Field = applyTemplateRelation.getString("b0110");
                }
                for (MorphDynaBean record : records) {
                    RecordVo tabVo = new RecordVo(tableName);
                    String primaryKeyValue = (String) record.get("z8101");
                    String guidKey = (String) record.get("guidkey");
                    String a0100 = (String) record.get("a0100");
                    String nbase = (String) record.get("nbase");
                    tabVo.setObject("a0100", a0100);
                    tabVo.setObject("basepre", nbase);
                    String e01a1Value = (String) record.get("e01a1");
                    String b0110Value = (String) record.get("b0110");
                    String e0122Value = (String) record.get("e0122");
                    if (dao.isExistRecordVo(tabVo)) {
                        tabVo = dao.findByPrimaryKey(tabVo);
                        if(StringUtils.isNotEmpty(b0110Field)){
                            if (dbwizard.isExistField(tableName, b0110Field)) {
                                tabVo.setObject(b0110Field.toLowerCase(), b0110Value);
                            }
                        }
                        if(StringUtils.isNotEmpty(e01a1Field)){
                            if (dbwizard.isExistField(tableName, e01a1Field)) {
                                tabVo.setObject(e01a1Field.toLowerCase(), e01a1Value);
                            }
                        }
                        if(StringUtils.isNotEmpty(e0122Field)){
                            if (dbwizard.isExistField(tableName, e0122Field)) {
                                tabVo.setObject(e0122Field.toLowerCase(), e0122Value);
                            }
                        }
                        tabVo.setObject(primaryKeyField, primaryKeyValue);
                        tabVo.setObject(z8301Field, guidKey);
                        tabVo.setString("seqnum", CreateSequence.getUUID());
                        tabVo.setObject("submitflag", "1");
                        if (tabVo.hasAttribute("photo")) {
                            tabVo.removeValue("photo");
                        }
                        dao.updateValueObject(tabVo);
                    } else {
                        tabVo.setString("seqnum", CreateSequence.getUUID());
                        dao.addValueObject(tabVo);
                        //移动端为自助用户
                        templateBo.setModuleId("9");
                        tabVo.setObject("state", 0);
                        templateBo.impDataFromArchive(a0100, nbase, 0, new ArrayList());
                        if(StringUtils.isNotEmpty(b0110Field)){
                            if (dbwizard.isExistField(tableName, b0110Field)) {
                                tabVo.setObject(b0110Field.toLowerCase(), b0110Value);
                            }
                        }
                        if(StringUtils.isNotEmpty(e01a1Field)){
                            if (dbwizard.isExistField(tableName, e01a1Field)) {
                                tabVo.setObject(e01a1Field.toLowerCase(), e01a1Value);
                            }
                        }
                        if(StringUtils.isNotEmpty(e0122Field)){
                            if (dbwizard.isExistField(tableName, e0122Field)) {
                                tabVo.setObject(e0122Field.toLowerCase(), e0122Value);
                            }
                        }
                        tabVo.setObject(primaryKeyField, primaryKeyValue);
                        tabVo.setObject(z8301Field, guidKey);
                        tabVo.setObject("submitflag", "1");
                        dao.updateValueObject(tabVo);
                    }
                }
            }
            return tabId;
        } catch (Exception ex) {
            String msg = "initTempTemplateTableError";
            if (ex instanceof GeneralException) {
                msg = ((GeneralException) ex).getErrorDescription();
            }
            ex.printStackTrace();
            throw new GeneralException(msg);
        }


    }

    /**
     * 获取组织机构权限sql
     *
     * @param userView 用户信息
     * @param nmoudle  模块号
     * @param itemId   指标编号
     * @param tablePre 指标前缀
     * @return String
     * @author wangbs
     */
    public static String getConditionsSql(UserView userView, String nmoudle, String itemId, String tablePre) {
        //条件sql
        StringBuffer conditionsSql = new StringBuffer();
        //组织机构业务范围>操作单位>人员范围
        String unitPriv = userView.getUnitIdByBusi(nmoudle);
        //没有任何权限
        if (userView.isSuper_admin() || StringUtils.equalsIgnoreCase("UN`", unitPriv)) {
            conditionsSql.append("1=1");
        } else if (StringUtils.isBlank(unitPriv) || StringUtils.equalsIgnoreCase("UM", unitPriv) || StringUtils.equalsIgnoreCase("UN", unitPriv)) {
            conditionsSql.append("1=2");
        } else {
            StringBuffer tempSql = new StringBuffer();
            //每种权限分割符可能会不一样
            unitPriv = unitPriv.replaceAll("`", ",");
            String[] units = unitPriv.split(",");
            for (String unit : units) {
                unit = unit.substring(2);
                tempSql.append(" or ");
                if (StringUtils.isNotBlank(tablePre)) {
                    tempSql.append(tablePre + ".");
                }
                tempSql.append(itemId + " like '" + unit + "%'");
            }
            // 将开始的or截掉
            conditionsSql.append(tempSql.substring(4));
        }
        return conditionsSql.toString();
    }

    /**
     * 获取当前用户有无人员列表或岗位列表权限
     *
     * @return Map
     * @author wangbs
     */
    public static Map getPsnOrPosPriv(UserView userView) {
        Map privMap = new HashMap();
        //默认无人员和岗位列表权限权限
        privMap.put("psnPriv", false);
        privMap.put("posPriv", false);

        //竞聘人员
        if (userView.isSuper_admin() || userView.hasTheFunction("40102")) {
            privMap.put("psnPriv", true);
        }
        //竞聘岗位
        if (userView.isSuper_admin() || userView.hasTheFunction("40101")) {
            privMap.put("posPriv", true);
        }
        return privMap;
    }
    /**
     * 获取登录认证库
     *
     * @return String
     * @author wangbs
     */
    public static String getLoginTableStr() {
        String loginTableStr = "";
        RecordVo vo = ConstantParamter.getRealConstantVo("SS_LOGIN");
        if (vo != null) {
            loginTableStr = vo.getString("str_value");
        }
        return loginTableStr;
    }

    /**
     * 获取竞聘岗位详情登记表
     *
     * @return 竞聘岗位详情登记表 id
     */
    public static String getPostDetailRname() throws GeneralException {
        String postDetailRnameId = "";
        Map settingMap = TalentMarketsUtils.getSettingMap();
        if (MapUtils.isNotEmpty(settingMap)) {
            postDetailRnameId = (String) settingMap.get("postDetail_ranme");
        }
        return postDetailRnameId;
    }

    /**
     * 获取组织机构业务范围内的所有机构
     *
     * @param userView 用户信息
     * @return String
     * @author wangbs
     */
    public static String getAllPrivOrgIdStr(UserView userView) {
        StringBuffer allPrivOrgIdStr = new StringBuffer();
        //组织机构业务范围>操作单位>人员范围
        String unitPriv = userView.getUnitIdByBusi("4");

        if (userView.isSuper_admin() || StringUtils.equalsIgnoreCase("UN`", unitPriv)) {
            return allPrivOrgIdStr.toString();
        } else if (StringUtils.isBlank(unitPriv) || StringUtils.equalsIgnoreCase("UN", unitPriv) || StringUtils.equalsIgnoreCase("UM", unitPriv)) {
            //没有任何权限
            allPrivOrgIdStr.append("no");
            return allPrivOrgIdStr.toString();
        } else {
            //每种权限分割符可能会不一样
            unitPriv = unitPriv.replaceAll("`", ",");
            String[] units = unitPriv.split(",");
            for (int i = 0; i < units.length; i++) {
                String unit = units[i];
                unit = unit.substring(2);
                allPrivOrgIdStr.append(unit);

                if (i != units.length - 1) {
                    allPrivOrgIdStr.append(",");
                }
            }
        }
        return allPrivOrgIdStr.toString();
    }

    /**
     * 发送短信校验
     *
     * @return boolean
     * @author wangbs
     */
    public static boolean isSendSMS() {
        if (isSMSBOInterface() || iSMSCat() || isSMSGateway()) {
            return true;
        }
        return false;
    }

    /**
     * 校验是否配置短信业务接口且启用
     *
     * @return
     */
    private static boolean isSMSBOInterface() {
        RecordVo vo = ConstantParamter.getRealConstantVo("ACCEPTMESSAGESET");
        if (vo == null) {
            return false;
        }
        String str_value = vo.getString("str_value");
        //短信业务接口配置参数不存在
        if (str_value == null || str_value.trim().length() == 0 || str_value.toLowerCase().indexOf("xml") == -1) {
            return false;
        }
        Document doc = null;
        try {
            doc = PubFunc.generateDom(str_value);

            String path = "/messageset/set";
            XPath xpath = XPath.newInstance(path);
            List elList = xpath.selectNodes(doc);
            for (int i = 0; i < elList.size(); i++) {
                Element el = (Element) elList.get(i);
                if ("1".equals(el.getAttributeValue("status"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验是否配置短信猫且启用
     *
     * @return
     */
    private static boolean iSMSCat() {
        RecordVo vo = ConstantParamter.getRealConstantVo("SS_SMS_OPTIONS");
        if (vo == null) {
            return false;
        }
        String str_value = vo.getString("str_value");
        //短信业务接口配置参数不存在
        if (str_value == null || str_value.trim().length() == 0 || str_value.toLowerCase().indexOf("xml") == -1) {
            return false;
        }
        Document doc = null;
        try {
            doc = PubFunc.generateDom(str_value);
            String path = "/ports/port";
            XPath xpath = XPath.newInstance(path);
            List elList = xpath.selectNodes(doc);
            for (int i = 0; i < elList.size(); i++) {
                Element el = (Element) elList.get(i);
                if ("true".equals(el.getAttributeValue("valid"))) {//短信猫 启用
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验是否配置了短信网关,且启用
     *
     * @return
     */
    private static boolean isSMSGateway() {
        RecordVo vo = ConstantParamter.getRealConstantVo("SS_SMS_OPTIONS");
        if (vo == null) {
            return false;
        }
        String str_value = vo.getString("str_value");
        //短信业务接口配置参数不存在
        if (str_value == null || str_value.trim().length() == 0 || str_value.toLowerCase().indexOf("xml") == -1) {
            return false;
        }
        Document doc = null;
        try {
            doc = PubFunc.generateDom(str_value);
            // 取得根节点ports
            Element root = doc.getRootElement();
            if (root == null) {
                return false;
            }
            // 获取ports 属性flag 值为1时, 启用
            if ("1".equals(root.getAttributeValue("flag"))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析并且格式化审批意见。
     *
     * @param optionFieldValue
     * @return
     */
    public static ArrayList formatOptionFiledValue(String optionFieldValue) {
        ArrayList<String> valueList = new ArrayList<String>();
        if (StringUtils.isBlank(optionFieldValue)) {
            return valueList;
        }
        optionFieldValue = optionFieldValue.replace("\\n", "\n").replace("\r\n", "\n").replace("\r", "\n").replace("\n\n", "\n");
        String[] rowValue = optionFieldValue.split("\n");// 根据\n分隔审批意见
        int optionFormatType = 0;
        int rowIndex = 0;// 记录每个节点的行数
        int nodeIndex = 0;// 记录是第几个节点，区分是申请节点还是审批节点
        TemplateOptionField optionFile = null;// 定义审批意见对象
        for (int row = 0; row < rowValue.length; row++) {
            String rowInfo = rowValue[row];
            if (StringUtils.isBlank(rowInfo.trim())) {
                rowIndex++;
                continue;
            }
            //包含申请时间：
            boolean isContainsAppliCationTime = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"));
            //包含申请人：
            boolean isContainsProposer = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer"));
            //包含审批人：
            boolean isContainsApprover2 = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver2"));
            //包含审批时间:
            boolean isContainsApproverTime = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime"));
            //包含意见：同意
            boolean isContainsOpinionAgree = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionAgree"));
            //包含意见：不同意
            boolean isContainsOpinionDisagree = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionDisagree"));
            //包含批注：
            boolean isContainsAnnotation = rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"));


            if ((isContainsAppliCationTime && isContainsProposer) || (isContainsApprover2 && isContainsApproverTime)) {

                if (isContainsAppliCationTime && isContainsProposer) {
                    nodeIndex = 0;
                }
                if (isContainsApprover2 && isContainsApproverTime) {
                    nodeIndex = 1;
                }
                /**
                 * 申请人:xxx 申请时间:xxx 意见:同意
                 * 批注:
                 * 审批人:xxx 审批时间:xxx 意见:同意
                 * 批注:
                 **/
                optionFormatType = 1;
                rowIndex = 0;
            } else if (((!isContainsAppliCationTime) && isContainsProposer) || ((!isContainsApproverTime) && (isContainsOpinionAgree || isContainsOpinionDisagree) && (!isContainsAnnotation))) {
                if ((!isContainsAppliCationTime) && isContainsProposer) {
                    nodeIndex = 0;
                }
                if ((!isContainsApproverTime) && (isContainsOpinionAgree || isContainsOpinionDisagree) && (!isContainsAnnotation)) {
                    nodeIndex = 1;
                }
                /**
                 * 申请人： xxx 2017-03-09 09:23
                 * xxx意见：同意 xxx 2017-03-09 21:52
                 * 批注：不同意
                 */
                optionFormatType = 2;
                rowIndex = 0;
            } else if (rowInfo.contains("(") && rowInfo.contains(")：")) {
                /**
                 * 总部/研发中心/项目研发部(申请人)：
                 * 王俊琪 2018-08-13 13:45
                 *
                 * 总部/研发中心/项目研发部(审批人)：
                 * 同意 王建华 2018-08-13 13:56 批注：通过
                 */
                if (row + 1 < rowValue.length) {
                    String nextRowInfo = rowValue[row + 1];
                    //包含填写
                    boolean isContainsFill = nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fill"));
                    //包含填写意见
                    boolean isContainsFillOpinion = nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fillopinion"));
                    if (isContainsFill || isContainsFillOpinion) {
                        /**
                         * 总部/研发中心/项目研发部(申请人)：
                         *  员工填写
                         * 王俊琪   2018-08-13 13:45
                         *
                         * 总部/研发中心/项目研发部(审批人)：
                         *  部门领导填写
                         *  同意 王建华 2018-08-13 13:56
                         *  批注：通过
                         */
                        //包含(申请人)：
                        if (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer1"))) {
                            nodeIndex = 0;
                        }
                        //包含(审批人)：
                        if (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver1"))) {
                            nodeIndex = 1;
                        }
                        optionFormatType = 4;
                    } else {
                        if (nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.agree")) || nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.disagree"))) {
                            nodeIndex = 1;
                        } else {
                            nodeIndex = 0;
                        }
                        optionFormatType = 3;
                    }
                } else {
                    optionFormatType = 3;
                }
                rowIndex = 0;
            }
            if (rowIndex == 0) {
                if (row != 0) {
                    if (optionFile != null) {
                        valueList.add(JSON.toString(optionFile.changeObjectToMap()));
                        optionFile = new TemplateOptionField();
                    }
                }
            }
            if (optionFile == null) {
                optionFile = new TemplateOptionField();
            }
            switch (optionFormatType) {
                case 1: {
                    String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据去得每行中每列数据
                    for (int col = 0; col < colValue.length; col++) {// 循环一行没列的值
                        String colInfo = colValue[col];
                        String[] itemInfo = colInfo.split(":", 2);
                        if (rowIndex % 2 == 0) {// 奇数行第一列是审批人、审批时间、审批意见
                            switch (col) {
                                case 0: {
                                    optionFile.setApproverType(itemInfo[0]);// 申请人或审批人
                                    optionFile.setApproverName(itemInfo[1]);// 姓名
                                    break;
                                }
                                case 1: {
                                    optionFile.setApprovalTime(itemInfo[1]);// 审批时间
                                    break;
                                }
                                case 2: {
                                    optionFile.setApproverType(itemInfo[1]);// 同意、不同意
                                    break;
                                }
                            }
                        } else {// 偶数行是批注
                            optionFile.setApproverAnnotation(itemInfo[1]);// 批注
                        }
                    }
                    if (nodeIndex == 0) {
                        optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
                    }
                    rowIndex++;
                    break;
                }
                case 2: {
                    String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据
                    for (int col = 0; col < colValue.length; col++) {
                        String colInfo = colValue[col];
                        String[] itemInfo = colInfo.split(":", 2);
                        if (nodeIndex == 0) {// 第一行固定是申请人信息
                            switch (col) {
                                case 0: {
                                    optionFile.setApproverType(itemInfo[0]);// 申请人
                                    break;
                                }
                                case 1: {
                                    optionFile.setApproverName(itemInfo[0]);// 申请人姓名
                                    break;
                                }
                                case 2: {
                                    optionFile.setApprovalTime(itemInfo[0]);// 申请时间
                                    break;
                                }
                            }
                        } else {
                            if (!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"))) {// 如果不包含批注说明是审批人、审批意见、审批时间记录行。
                                switch (col) {
                                    case 0: {
                                        optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
                                        optionFile.setApproverRole(itemInfo[0]
                                                .replace(ResourceFactory.getProperty("format.optionfield.opinion"), ""));// 节点名称
                                        optionFile.setApproverOpinion(itemInfo[1]);// 同意、不同意
                                        break;
                                    }
                                    case 1: {
                                        optionFile.setApproverName(itemInfo[0]);// 审批人姓名
                                        break;
                                    }
                                    case 2: {
                                        optionFile.setApprovalTime(itemInfo[0]);// 审批时间
                                        break;
                                    }
                                }
                            } else {// 批注信息
                                if (itemInfo.length > 0) {
                                    optionFile.setApproverAnnotation(itemInfo[1]);// 批注
                                }
                            }
                        }
                    }
                    if (nodeIndex == 0) {
                        optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
                    }
                    rowIndex++;
                    break;
                }
                case 3: {
                    //判定 是否是终止流程
                    boolean isStop = false;
                    switch (rowIndex) {// 格式3数据比较规范，可以根据节点行数拆分每行的数据
                        case 0: {// 审批人\申请人 单位部门信息 节点类型信息
                            if (nodeIndex == 0) {// 申请节点
                                optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));// 申请人
                            } else {
                                optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
                            }
                            if (rowInfo.trim().length() > 0) {// 如果替换后的还有内容，则进行分析单位、部门信息
                                int num = rowInfo.indexOf("(");
                                int lastNum = rowInfo.lastIndexOf(")");
                                String roleInfo = rowInfo.substring(num + 1, lastNum);
                                if (nodeIndex == 0) {
                                    optionFile.setApproverRole(roleInfo);
                                } else {
                                    optionFile.setApproverRole(roleInfo);
                                }
                                rowInfo = rowInfo.substring(0, num);
                                int firstIndex = rowInfo.indexOf("/");
                                int lastIndex = rowInfo.lastIndexOf("/");
                                if (firstIndex > -1 && lastIndex > -1) {
                                    optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
                                    optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
                                }
                            }
                            break;
                        }
                        case 1: {// 审批人\申请人 姓名 时间
                            String[] colInfo = rowInfo.split("\\s+", 4);
                            if (nodeIndex == 0) {
                                optionFile.setApproverName(colInfo[0]);
                                optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
                            } else {
                                optionFile.setApproverOpinion(colInfo[0]);
                                optionFile.setApproverName(colInfo[1]);
                                optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
                            }
                            break;
                        }
                        case 2: {// 审批人\申请人 批注
                            String[] colInfo = rowInfo.split("：", 2);
                            if (nodeIndex != 0) {
                                if (colInfo.length > 0) {
                                    optionFile.setApproverAnnotation(colInfo[1]);
                                }
                            } else {
                                if (colInfo.length > 0) {
                                    if (StringUtils.contains(colInfo[1], "终止流程")) {
                                        isStop = true;
                                    }
                                }

                            }

                            break;
                        }
                    }
                    if (nodeIndex == 0) {
                        optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
                        if(isStop){
                            optionFile.setApproverAnnotation("终止流程");
                        }
                    }
                    rowIndex++;
                    break;
                }
                case 4: {
                    switch (rowIndex) {
                        case 0: {
                            if (nodeIndex == 0) {
                                optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));
                            } else {
                                optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));
                            }
                            if (rowInfo.trim().length() > 0) {
                                int num = rowInfo.indexOf("(");
                                int lastNum = rowInfo.lastIndexOf(")");
                                String roleInfo = rowInfo.substring(num + 1, lastNum);
                                optionFile.setApproverRole(roleInfo);
                                rowInfo = rowInfo.substring(0, num);
                                int firstIndex = rowInfo.indexOf("/");
                                int lastIndex = rowInfo.lastIndexOf("/");
                                if (firstIndex > -1 && lastIndex > -1) {
                                    optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
                                    optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
                                }
                            }
                            break;
                        }
                        case 1: {
                            optionFile.setApproverRole(rowInfo);
                            break;
                        }
                        case 2: {
                            String[] colInfo = rowInfo.split("\\s+", 4);
                            if (nodeIndex == 0) {
                                optionFile.setApproverName(colInfo[0]);
                                optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
                            } else {
                                optionFile.setApproverOpinion(colInfo[0]);
                                optionFile.setApproverName(colInfo[1]);
                                optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
                            }
                            break;
                        }
                        case 3: {
                            if (nodeIndex != 0) {
                                String[] colInfo = rowInfo.split("：", 2);
                                if (colInfo.length > 0) {
                                    optionFile.setApproverAnnotation(colInfo[1]);
                                }
                            }
                            break;
                        }
                    }
                    if (nodeIndex == 0) {
                        optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
                    }
                    rowIndex++;
                    break;
                }
                default: {
                    break;
                }
            }
        }
        valueList.add(JSON.toString(optionFile.changeObjectToMap()));
        optionFile = new TemplateOptionField();
        return valueList;
    }
    /**
     * 获取组织机构岗位编制参数配置项信息
     * @param  conn 数据库链接
     * @return 岗位编制参数配置信息项
     */
    public static Map<String, String> getSubsetConfig(Connection conn) {
        Map<String, String> subSetConfig = new HashMap();
        //获取岗位编制配置指标,实时查询数据库
        RecordVo vo = ConstantParamter.getRealConstantVo("PS_WORKOUT", conn);
        String strValue = vo.getString("str_value");
        String[] strArr = strValue.split("\\|");
        //岗位编制子集
        String psSet = strArr[0];
        String[] psItems = strArr[1].split(",");
        //定员人数
        String psWorkfixed = psItems[0];
        //实有人数
        String psWorkexist = psItems[1];

        subSetConfig.put("psSet", psSet);
        subSetConfig.put("psWorkfixed", psWorkfixed);
        subSetConfig.put("psWorkexist", psWorkexist);
        return subSetConfig;
    }

    /**
     * 根据岗位codeitemid 获取到单位codeitemid
     *
     * @param e01a1
     * @param dao
     * @param codeType UM UN
     * @return
     * @throws GeneralException
     */
    public static String getOrgItemid(String e01a1, ContentDAO dao, String codeType) throws GeneralException {
        String sql = "SELECT max(O.codeitemid) uncode FROM organization o where o.codeitemid="+ Sql_switcher.substr("?","1",Sql_switcher.length("O.codeitemid")) +" and codesetid = " + "'" + codeType + "'";
        RowSet rs = null;
        String b0110 = "";
        try {
            rs = dao.search(sql, Arrays.asList(e01a1));
            if (rs.next()) {
                b0110 = rs.getString("uncode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException("getOrgItemidError");
        } finally {
            PubFunc.closeResource(rs);
        }
        return b0110;
    }

    /**
     *
     * @param conn 数据库连接
     * @return 返回人才市场竞聘岗位发布仅支持二级流程的配置项，默认关闭快速审批：false
     */
    public static boolean getQuickApprove(Connection conn){
        boolean quickApprove = false;
        RecordVo constantVo = ConstantParamter.getRealConstantVo("TALENTMARKETS_PARAM",conn);
        if(constantVo != null){
            String strValue = constantVo.getString("str_value");
            if (StringUtils.isNotEmpty(strValue)) {
                JSONObject configObj = JSONObject.fromObject(strValue);
                JSONObject competition = configObj.getJSONObject("competition");
                //JSONObject templates = competition.getJSONObject("templates");
                if (competition.containsKey("quickApprove")) {
                    quickApprove = competition.getBoolean("quickApprove");
                }
            }
        }
        return quickApprove;
    }

    /**
     *
     * @param conn 数据库连接
     * @return 返回人才市场是否开启面试环节的配置项，默认开启面试环节：true
     */
    public static boolean getOpenInterview(Connection conn){
        boolean openInterview = true;
        RecordVo constantVo = ConstantParamter.getRealConstantVo("TALENTMARKETS_PARAM",conn);
        if(constantVo !=null){
            String strValue = constantVo.getString("str_value");
            if (StringUtils.isNotEmpty(strValue)) {
                JSONObject configObj = JSONObject.fromObject(strValue);
                JSONObject competition = configObj.getJSONObject("competition");
                //JSONObject templates = competition.getJSONObject("templates");
                if (competition.containsKey("openInterview")) {
                    openInterview = competition.getBoolean("openInterview");
                }
            }
        }
        return openInterview;
    }

    /**
     * 根据e0122 取上级单位
     * @param codeItemid
     * @return
     */
    public static String getB0100(String codeItemid){
        if(StringUtils.isEmpty(codeItemid)){
            return "";
        }
        CodeItem codeItem = AdminCode.getCode("UM",codeItemid);
        String parentCodeItemId = "";
        if(codeItem != null){
            parentCodeItemId = codeItem.getPcodeitem();
            return TalentMarketsUtils.getB0100(parentCodeItemId);
        }else{
            codeItem = AdminCode.getCode("UN",codeItemid);
            if(codeItem != null){
                parentCodeItemId = codeItemid;
            }
            return parentCodeItemId;
        }
    }

}
