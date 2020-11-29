package com.hjsj.hrms.module.talentmarkets.parameter.businessobject.impl;

import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.TalentMarketsParameterService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @version 1.0
 * @Titile TalentMarketsParameterServiceImpl
 * @Description 参数配置实现类
 * @Company hjsj
 * @Create time: 2019年8月8日下午6:17:30
 * @author wangd
 */
public class TalentMarketsParameterServiceImpl implements TalentMarketsParameterService {
    private Connection conn;

    public TalentMarketsParameterServiceImpl(Connection frameconn) {
        this.conn = frameconn;
    }

    @Override
    public String saveSettings(String str) {
        String message = "";
        Boolean isExist = isExist(this.conn);
        // 判断constant数据是否存在，不存在就新增，存在就修改
        try {
            if (!isExist) {
                insertCons(str);
            } else {
                updateCons(str);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        message = "保存成功";
        return message;
    }

    @Override
    public JSONObject loadSettings() {
        JSONObject jsonObj = null;
        String strValue = "{}";
        RecordVo constantVo = ConstantParamter.getRealConstantVo("TALENTMARKETS_PARAM");
        if(constantVo !=null) {
            strValue=constantVo.getString("str_value");
        }
        jsonObj  = JSONObject.fromObject(strValue);
        // 二次处理，添加模板、登记表、指标项名称
        if (!"{}".equals(strValue)) {
            // 竞聘岗位详情指标项
            JSONObject competition=jsonObj.getJSONObject("competition");
            JSONArray postFields = competition.getJSONArray("postFields");
            postFields=reEditFields(postFields,"z81");
            competition.remove("postFields");
            competition.put("postFields",postFields);
            // 岗位类别指标项
            JSONObject talentHall=jsonObj.getJSONObject("talentHall");
            JSONArray resumePostTypeField = talentHall.getJSONArray("resumePostTypeField");
            resumePostTypeField=reEditFields(resumePostTypeField,"k01");
            talentHall.remove("resumePostTypeField");
            talentHall.put("resumePostTypeField",resumePostTypeField);
            // 个人简介指标项
            JSONArray resumeSelfIntroduction = talentHall.getJSONArray("resumeSelfIntroduction");
            resumeSelfIntroduction=reEditFields(resumeSelfIntroduction,"a01");
            talentHall.remove("resumeSelfIntroduction");
            talentHall.put("resumeSelfIntroduction",resumeSelfIntroduction);
            // 岗位发布模板
            JSONObject competition_templates = competition.getJSONObject("templates");
            HashMap<String, HashMap<String, String>> templateMap=reSetTemplates(competition_templates,"releasePost_template","template");
            competition_templates.putAll(templateMap);
            // 2竞聘报名模板
            templateMap=reSetTemplates(competition_templates,"apply_template","template");
            competition_templates.putAll(templateMap);
            // 3录用审批模板
            templateMap=reSetTemplates(competition_templates,"hire_template","template");
            competition_templates.putAll(templateMap);
            // 4应聘简历登记表
            JSONObject competition_rnames = competition.getJSONObject("rnames");
            templateMap=reSetTemplates(competition_rnames,"applyResume_rname","rname");
            competition_rnames.putAll(templateMap);
            // 5竞聘岗位详情登记表
            templateMap=reSetTemplates(competition_rnames,"postDetail_ranme","rname");
            competition_rnames.putAll(templateMap);
            // 6申请模板
            JSONObject talentHall_templates = talentHall.getJSONObject("templates");
            templateMap=reSetTemplates(talentHall_templates,"apply_template","template");
            talentHall_templates.putAll(templateMap);
            // 7撤销模板
            templateMap=reSetTemplates(talentHall_templates,"cancel_template","template");
            talentHall_templates.putAll(templateMap);
            // 8简历登记表
            JSONObject talentHall_rname = talentHall.getJSONObject("rnames");
            templateMap=reSetTemplates(talentHall_rname,"talent_rname","rname");
            talentHall_rname.putAll(templateMap);
        }
        return jsonObj;
    }

    /**
     * 为模板和登记表添加名称
     *
     * @param superior_templates 已经选中的模板
     * @param templatesName 模板名称
     * @param type 区分是模板还是登记表template或rname
     * @return 增加模板名称后的数据
     */
    private HashMap<String, HashMap<String, String>> reSetTemplates(JSONObject superior_templates, String templatesName, String type) {
        String tabId = superior_templates.getString(templatesName);
        String tabName = getTemOrRnameName(tabId, type);
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, HashMap<String, String>> templateMap = new HashMap<String, HashMap<String, String>>();
        map.put("tabId", tabId);
        if (StringUtils.isNotEmpty(tabName)) {
            map.put("name", tabId+"."+tabName);
        }
        templateMap.put(templatesName, map);
        return templateMap;
    }

    /**
     * 岗位详情展示项顺序调整
     * @param oriItemList
     * @param toItemid
     * @param dropPosition
     * @return
     */
    @Override
    public String dragAndDropSort(ArrayList<String> oriItemList, String toItemid, String dropPosition) {
        String message = "";
        if(oriItemList.size() > 1){
            if(StringUtils.equalsIgnoreCase(dropPosition,"before")){
                for(String oriItemid : oriItemList){
                    String toDisplayid = checkSecStr(toItemid.toUpperCase());
                    String oriDisplayid = checkSecStr(oriItemid.toUpperCase());
                    adjustSec(oriItemid, oriDisplayid, toDisplayid,dropPosition);
                }
            }else{
                for(int i=oriItemList.size()-1;i>=0;i--){
                    String oriItemid = oriItemList.get(i);
                    String toDisplayid = checkSecStr(toItemid.toUpperCase());
                    String oriDisplayid = checkSecStr(oriItemid.toUpperCase());
                    adjustSec(oriItemid.toUpperCase(), oriDisplayid, toDisplayid,dropPosition);
                }
            }
        }else{
            String toDisplayid = checkSecStr(toItemid.toUpperCase());
            String oriItemid = oriItemList.get(0);
            String oriDisplayid = checkSecStr(oriItemid.toUpperCase());
            adjustSec(oriItemid.toUpperCase(), oriDisplayid, toDisplayid,dropPosition);
        }
        message = "保存成功!";
        return message;
    }

    /**
     * 为指标项添加名称
     *
     * @param postFields 选中的岗位指标想
     * @param fieldSetId 子集itemid
     * @return 拼接字段描述后的数据
     */
    private JSONArray reEditFields(JSONArray postFields,String fieldSetId) {
        JSONArray refreshPostFields = new JSONArray();
        for (Iterator<?> iter = postFields.iterator(); iter.hasNext(); ) {
            String itemId = (String) iter.next();
            String itemdesc = DataDictionary.getFieldItem(itemId,fieldSetId).getItemdesc();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("itemid", itemId);
            map.put("itemdesc", itemdesc);
            refreshPostFields.add(map);
        }
        return refreshPostFields;
    }

    /**
     * 插入constant表TALENTMARKETS_PARAM记录
     *
     * @param configValue 配置页面的参数值
     * @throws SQLException 执行插入时报错
     */
    private void insertCons(String configValue) throws SQLException {
        ArrayList<String> value;
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        value = new ArrayList<String>();
        sql.append("insert into Constant(constant,describe,str_value) values(?,?,?)");
        value.add("TALENTMARKETS_PARAM");
        value.add("人才市场参数配置");
        value.add(configValue);
        dao.insert(sql.toString(), value);
    }

    /**
     * 修改constant表TALENTMARKETS_PARAM记录数据
     *
     * @param configValue 配置页面的参数值
     * @throws SQLException 执行插入时报错
     */
    private void updateCons(String configValue) throws SQLException {
        StringBuffer sql;
        ArrayList<String> value;
        ContentDAO dao = new ContentDAO(this.conn);
        sql = new StringBuffer();
        value = new ArrayList<String>();
        sql.append("update Constant set str_value=? where constant=?");
        value.add(configValue);
        value.add("TALENTMARKETS_PARAM");
        dao.update(sql.toString(), value);
    }

    /**
     * 查询业务字典表displayid，顺序号
     * @param itemid 选中的itemid
     * @return 更新后的itemid顺序
     */
    private String checkSecStr(String itemid) {
        String displayid = "";
        StringBuffer sql = new StringBuffer();
        List<String> value = new ArrayList<String>();
        RowSet rowset = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            sql.append("select displayid  from t_hr_busifield where FieldSetId='Z81' and itemid=?");
            value.add(itemid);
            rowset = dao.search(sql.toString(), value);
            while (rowset.next()) {
                displayid = rowset.getString("displayid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowset);
        }
        return displayid;
    }

    /**
     * 调整顺序，包含上移和下移
     * @param ori_itemid 原始itemid
     * @param ori_displayid 原始的显示序号
     * @param to_displayid 要调整成为的显示顺序号
     * @param dropPosition 调整位置
     */
    private void adjustSec(String ori_itemid, String ori_displayid, String to_displayid, String dropPosition) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            boolean isTop = true;
            if (Integer.parseInt(ori_displayid) > Integer.parseInt(to_displayid)) {
                isTop = false;
            }
            if ("before".equals(dropPosition)) {// 上移
                // 在移动对象和目标对象之间的对象SORTID都加1.
                ArrayList sortList = new ArrayList();
                StringBuffer sortSql = new StringBuffer();

                ArrayList updateList = new ArrayList();
                StringBuffer updateSql = new StringBuffer();
                if(isTop){
                    sortSql.append("update t_hr_busifield set displayid = displayid-1 where displayid > ? and displayid < ? and FieldSetId='Z81' and itemid<>?");
                    sortList.add(ori_displayid);
                    sortList.add(to_displayid);
                    sortList.add(ori_itemid);

                    updateSql.append("update t_hr_busifield set displayid=? where FieldSetId='Z81' and itemid=?");
                    int toId = Integer.parseInt(to_displayid) - 1;
                    updateList.add(Integer.toString(toId));
                    updateList.add(ori_itemid);
                }else{
                    sortSql.setLength(0);
                    sortSql.append("update t_hr_busifield set displayid = displayid+1 where displayid >= ? and displayid < ? and FieldSetId='Z81' and itemid<>?");
                    sortList.add(to_displayid);
                    sortList.add(ori_displayid);
                    sortList.add(ori_itemid);

                    updateSql.setLength(0);
                    updateSql.append("update t_hr_busifield set displayid=? where FieldSetId='Z81' and itemid=?");
                    updateList.add(to_displayid);
                    updateList.add(ori_itemid);
                }
                dao.update(sortSql.toString(), sortList);
                dao.update(updateSql.toString(), updateList);
            } else if ("after".equals(dropPosition)) {// 下移
                ArrayList sortList = new ArrayList();
                StringBuffer sortSql = new StringBuffer();
                ArrayList updateList = new ArrayList();
                StringBuffer updateSql = new StringBuffer();
                if(isTop){
                    sortSql.append("update t_hr_busifield set displayid = displayid-1 where displayid > ? and displayid <= ? and  FieldSetId='Z81' and itemid<>?");
                    sortList.add(ori_displayid);
                    sortList.add(to_displayid);
                    sortList.add(ori_itemid);

                    updateSql.append("update t_hr_busifield set displayid=? where FieldSetId='Z81' and itemid=?");
                    updateList.add(to_displayid);
                    updateList.add(ori_itemid);
                }else{
                    sortSql.setLength(0);
                    sortSql.append("update t_hr_busifield set displayid = displayid+1 where displayid < ? and displayid > ? and  FieldSetId='Z81' and itemid<>?");
                    sortList.add(ori_displayid);
                    sortList.add(to_displayid);
                    sortList.add(ori_itemid);

                    updateSql.setLength(0);
                    updateSql.append("update t_hr_busifield set displayid=? where FieldSetId='Z81' and itemid=?");
                    int toId = Integer.parseInt(to_displayid) + 1;
                    updateList.add(Integer.toString(toId));
                    updateList.add(ori_itemid);
                }
                dao.update(sortSql.toString(), sortList);
                dao.update(updateSql.toString(), updateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模板或登记表名称
     *
     * @param tabId 登记表或模版id
     * @param type 区分是模板还是登记表
     * @return 返回登记表或者模版的名称
     */
    private String getTemOrRnameName(String tabId, String type) {
        String tabName = "";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql;
            ArrayList<String> value;
            sql = new StringBuffer();
            value = new ArrayList<String>();
            if ("template".equals(type)) {
                sql.append("select name  from template_table where TabId=?");
            } else {
                sql.append("select name  from rname where TabId=?");
            }
            value.add(tabId);
            RowSet rowset = dao.search(sql.toString(), value);
            while (rowset.next()) {
                tabName = rowset.getString("name");
            }
            rowset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tabName;
    }

    /**
     * 判断constant表TALENTMARKETS_PARAM记录是否存在
     *
     * @param frameconn 数据库链接
     * @return 数据库中是否存在该数据
     */
    private Boolean isExist(Connection frameconn) {
        Boolean isExist = false;
        ContentDAO dao = new ContentDAO(frameconn);
        StringBuffer sql = new StringBuffer();
        sql.append("select * from Constant where Constant='TALENTMARKETS_PARAM'");
        RowSet rowset = null;
        try {
            rowset = dao.search(sql.toString());
            if (rowset.next()) {
                isExist = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowset);
        }
        return isExist;
    }

    @Override
    public List<LazyDynaBean> queryFieldItem(String type) {
        List<LazyDynaBean> valueList = new ArrayList<LazyDynaBean>();
        ArrayList<?> list = DataDictionary.getFieldList(type, Constant.USED_FIELD_SET);//业务字典表指标，t_hr_busitable,构库号
        LazyDynaBean bean;//需要排序和另一个display这个字段
        RowSet rowSet = null;
        ArrayList paramList = new ArrayList();
        StringBuffer searchSql = new StringBuffer();
        searchSql.append("select * from t_hr_busifield where FieldSetId='Z81' and itemid in (");
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            if(StringUtils.equalsIgnoreCase("z81",type)){
                for(int i=0;i<list.size();i++) {
                    FieldItem item=(FieldItem) list.get(i);
                    boolean visible = item.isVisible();
                    String itemId=item.getItemid();
                    if(!visible){
                        continue;
                    }
                    if("z8105".equals(itemId)||"z8103".equals(itemId) || "z8115".equals(itemId) || "z8117".equals(itemId) || "b0110".equals(itemId)
                            || "e0122".equals(itemId) || "E01A1".equalsIgnoreCase(itemId)){
                        continue;
                    }
                    if(!"z8101".equals(itemId)&&!"z8111".equals(itemId)&&!"z8113".equals(itemId)) {
                        searchSql.append("?").append(",");
                        paramList.add(itemId.toUpperCase());

                    }
                }
                searchSql.setLength(searchSql.length() - 1);
                searchSql.append(") order by displayid");
                rowSet = dao.search(searchSql.toString(),paramList);
                while(rowSet.next()){
                    bean=new LazyDynaBean();
                    bean.set("itemid",rowSet.getString("itemId").toLowerCase());
                    bean.set("itemdesc",rowSet.getString("itemdesc"));
                    valueList.add(bean);
                }
            }else if (StringUtils.equalsIgnoreCase("a01",type)){
                for(int i=0;i<list.size();i++) {
                    FieldItem item=(FieldItem) list.get(i);
                    String itemId=item.getItemid();
                    String itemDesc = item.getItemdesc();
                    String itemType = item.getItemtype();
                    String codesetId = item.getCodesetid();
                    boolean visible = item.isVisible();
                    if(!visible){
                        continue;
                    }
                    if(!StringUtils.equalsIgnoreCase("0",codesetId)){
                        continue;
                    }
                    if(!StringUtils.equalsIgnoreCase("A",itemType) && !StringUtils.equalsIgnoreCase("M",itemType)){
                        continue;
                    }
                    if("z8103".equals(itemId) || "z8115".equals(itemId) || "z8117".equals(itemId)){
                        continue;
                    }
                    if(!"z8101".equals(itemId)&&!"z8111".equals(itemId)&&!"z8113".equals(itemId)) {
                        bean=new LazyDynaBean();
                        bean.set("itemid",itemId);
                        bean.set("itemdesc",itemDesc);
                        valueList.add(bean);
                    }
                }
            }else {
                RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_LEVEL_CODE", this.conn);
                String strValue = vo.getString("str_value");
                for(int i=0;i<list.size();i++) {
                    FieldItem item=(FieldItem) list.get(i);
                    String codesetId = item.getCodesetid();
                    if(StringUtils.equalsIgnoreCase(strValue,codesetId)){
                        String itemId=item.getItemid();
                        String itemDesc = item.getItemdesc();
                        bean=new LazyDynaBean();
                        bean.set("itemid",itemId);
                        bean.set("itemdesc",itemDesc);
                        valueList.add(bean);
                    }
                }
                /*rowSet = dao.search(postSql,codesetList);
                while (rowSet.next()){
                    bean=new LazyDynaBean();
                    String codeItemId = rowSet.getString("CODEITEMID");
                    String codeItemDesc = rowSet.getString("CODEITEMDESC");
                    bean.set("itemid",codeItemId);
                    bean.set("itemdesc",codeItemDesc);
                    valueList.add(bean);
                }*/
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return valueList;
    }

    /**
     * 对应指标数据加载
     *
     * @param fieldType 指标类型
     * @param tabId 模板id
     * @param changeType 要想更换的类型
     * @param lengthLimit
     * @param searchType
     * @return 可以对应的指标列表
     */
    @Override
    public ArrayList loadTemplateItems(String fieldType, String tabId, String changeType, String lengthLimit, String searchType) {
        StringBuffer loadSql = new StringBuffer();
        ArrayList recordList = new ArrayList();
        ArrayList paramList = new ArrayList();
        if(fieldType.contains("-")){
            if(StringUtils.equalsIgnoreCase("afterChange",changeType)){
                loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type in (?,?,?) and TabId = ? and ChgState = '2'");
            }else{
                loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type in (?,?,?) and TabId = ?");
            }
            String[] fieldTypeArr = fieldType.split("-");
            paramList.add(fieldTypeArr[0]);
            paramList.add(fieldTypeArr[1]);
            paramList.add(fieldTypeArr[2]);
        }else{
            if(StringUtils.equalsIgnoreCase("afterChange",changeType)){
                if(StringUtils.equalsIgnoreCase("lengthLimit",lengthLimit)){
                    //查询字符型限制长度大于30
                    loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set,fielditem where itemid = Field_name and Field_type = ? and TabId = ? and ChgState = '2' and itemlength > 30 and CODEID = '0'");
                }else if(StringUtils.equalsIgnoreCase("un",searchType)){
                    //代码型 单位
                    loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type = ? and TabId = ? and ChgState = '2' and CodeId = 'UN'");
                }else if(StringUtils.equalsIgnoreCase("um",searchType)){
                    //代码型 部门
                    loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type = ? and TabId = ? and ChgState = '2' and CodeId = 'UM'");
                }else if(StringUtils.equalsIgnoreCase("@k",searchType)){
                    //代码型 岗位
                    loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type = ? and TabId = ? and ChgState = '2' and CodeId = '@K'");
                }else{
                    //查询字符型、备注型
                    loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type = ? and TabId = ? and ChgState = '2' and CODEID = '0'");
                }
            }else{
                loadSql.append("select distinct Field_name,ChgState,Field_hz,Flag from Template_Set where Field_type = ? and TabId = ?");
            }
            paramList.add(fieldType);
        }
        paramList.add(tabId);

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try {
            rowSet = dao.search(loadSql.toString(),paramList);
            while (rowSet.next()){
                HashMap data = new HashMap();
                data.put("DestItemId",rowSet.getString("Field_name").toLowerCase() + "_" + rowSet.getString("ChgState"));
                if(StringUtils.equalsIgnoreCase("2",rowSet.getString("ChgState")) && (!StringUtils.contains(rowSet.getString("Field_hz"),"拟"))){
                    data.put("fieldHz","拟" + rowSet.getString("Field_hz"));
                }else{
                    data.put("fieldHz",rowSet.getString("Field_hz"));
                }
                recordList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return recordList;
    }

    /**
     * 检验模板是否处于流程中
     * @return
     */
    @Override
    public HashMap checkConfigurable() {
        RowSet rowSet = null;
        String releasePostSql = "select Z8101,Z8103 from Z81 where Z8103 = '02'";
        String applySql = "select Z8101,Z8303 from z83 where Z8303 = '01'";
        String hireSql = "select Z8101,Z8303 from z83 where Z8303 = '07'";
        HashMap  configMap = new HashMap();
        configMap.put("postConfigurable",true);
        configMap.put("applyConfigurable",true);
        configMap.put("hireConfigurable",true);

        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(releasePostSql);
            if(rowSet.next()){
                configMap.put("postConfigurable",false);
            }
            rowSet = dao.search(applySql);
            if(rowSet.next()){
                configMap.put("applyConfigurable",false);
            }
            rowSet = dao.search(hireSql);
            if(rowSet.next()){
                configMap.put("hireConfigurable",false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return configMap;
    }
}
