package com.hjsj.hrms.module.performance.score.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 考核评分bo
 *
 * @author ZhangHua
 * @date 15:33 2018/5/16
 */
public class KhScoreBo {


    private Connection conn;
    private String relation_Id;
    private String model;
    private UserView userView;
    private String mainBody_Id;


    public KhScoreBo(Connection conn, UserView userView, String relation_Id, String model, String mainBody_Id) {
        this.setUserView(userView);
        this.setConn(conn);
        this.setModel(model);
        this.setRelation_Id(relation_Id);
        this.setMainBody_Id(mainBody_Id);
    }

    /**
     * 获取考核主体信息
     *
     * @param object_Key  考核对象主键id
     * @param template_id 模板id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:03 2018/4/24
     */
    public HashMap<String, String> getMainbodyStatus(String object_Key, String template_id) throws GeneralException {
        RowSet rs = null;
        HashMap<String, String> templateMap = new HashMap<String, String>();
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append("select kh_object_id ,id,Status,Mainbody_id,  ");
            strSql.append(Sql_switcher.isnull("score", "0")).append(" as score ");
            strSql.append(" from kh_mainbody where upper(Mainbody_id)=upper(?) ");
            strSql.append(" and kh_object_id= ? and Relation_id=? and upper(template_id)=? ");
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<String> dataList = new ArrayList<String>();
            dataList.add(this.getMainBody_Id());
            dataList.add(object_Key);
            dataList.add(this.relation_Id);
            dataList.add(template_id);
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                //kh_object 表的id
                templateMap.put("kh_object_id", object_Key);
                //考核主体名称
                templateMap.put("Mainbody_Id", rs.getString("Mainbody_id"));
                //kh_mainbody 表id
                templateMap.put("Mainbody_Key", rs.getString("id"));
                //Status 0:未打分 1:正在编辑 2:已提交
                templateMap.put("Status", rs.getString("Status"));
                //score 总分
                templateMap.put("score", rs.getString("score"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return templateMap;
    }

    /**
     * 获取批量考核主体信息
     *
     * @param object_List 考核对象主键id list
     * @param template_id 模板id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:04 2018/4/24
     */
    public HashMap<String, HashMap<String, String>> getMainbodyStatus(ArrayList<String> object_List, String template_id) throws GeneralException {
        RowSet rs = null;
        HashMap<String, HashMap<String, String>> MainbodyMap = new HashMap<String, HashMap<String, String>>();
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append("select kh_object_id ,id,Status,Mainbody_id,  ");
            strSql.append(Sql_switcher.isnull("score", "0")).append(" as score ");
            strSql.append(" from kh_mainbody where upper(Mainbody_id)=? ");
            strSql.append("  and Relation_id=? and upper(template_id)=?  and kh_object_id in(");
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<String> dataList = new ArrayList<String>();
            dataList.add(this.getMainBody_Id());

            dataList.add(this.relation_Id);
            dataList.add(template_id);
            for (String id : object_List) {
                strSql.append("?,");
                dataList.add(id);
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("kh_object_id", rs.getString("kh_object_id"));
                map.put("Mainbody_Id", rs.getString("Mainbody_id"));
                map.put("Mainbody_Key", rs.getString("id"));
                map.put("Status", rs.getString("Status"));
                map.put("score", rs.getString("score"));
                MainbodyMap.put(rs.getString("id"), map);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return MainbodyMap;
    }

    /**
     * 获取模板模型
     *
     * @param template_Id
     * @param MainbodyStatus 如果只取模板不要评分 传null即可
     * @return LinkedHashMap<String, LazyDynaBean>
     * key为(模板项目id)，
     * LazyDynaBean 具有2个属性 ：namePath 模板项目导航 类似 项目1/项目1.1/项目1.1.2
     * template_Item_Map 模板项目中包含的要素map，key为要素id value为要素属性
     * @author ZhangHua
     * @date 17:45 2018/4/11
     */
    public LinkedHashMap<String, LazyDynaBean> getTemplateMap(String template_Id, HashMap<String, String> MainbodyStatus) throws GeneralException {
        RowSet rs = null;
        LinkedHashMap<String, LazyDynaBean> templateMap = new LinkedHashMap<String, LazyDynaBean>();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String detailName = "", object_Id = "", mainbody_Key = "";
            if (MainbodyStatus != null && MainbodyStatus.size() > 0) {
                detailName = "kh_detail";
                object_Id = MainbodyStatus.get("kh_object_id");
                mainbody_Key = MainbodyStatus.get("Mainbody_Key");
            }

            StringBuffer strSql = new StringBuffer();
            strSql.append("select pti.parent_id,pti.seq,pti.item_id AS templateItemId,pti.itemdesc,ptp.score AS totalScore,ppt.pointname,ppt.pointkind,ppt.description,ppt.status as scoreMode, ");
            strSql.append(Sql_switcher.isnull(" ppt.point_id", "''")).append(" as point_id ");
            if (StringUtils.isNotBlank(object_Id)) {
                strSql.append(",kd.Score");
            }

            strSql.append(" FROM per_template_item pti ");
            strSql.append(" left JOIN per_template_point ptp ON ptp.item_id = pti.item_id ");
            strSql.append(" left JOIN per_point ppt ON ptp.point_id=ppt.point_id  and  ppt.Validflag=1 ");
            if (StringUtils.isNotBlank(object_Id)) {
                strSql.append(" LEFT JOIN ").append(detailName).append(" kd ON kd.Point_id = ppt.point_id AND kd.kh_object_id=? AND kd.Kh_mainbody_id=? ");
            }
            strSql.append(" WHERE pti.template_id=? ");
            strSql.append(" ORDER BY pti.seq,ptp.seq ");

            ArrayList<String> dataList = new ArrayList<String>();
            if (StringUtils.isNotBlank(object_Id)) {
                dataList.add(object_Id);
                dataList.add(mainbody_Key);
            }
            dataList.add(template_Id);

            rs = dao.search(strSql.toString(), dataList);

            LazyDynaBean dataBean;
            LinkedHashMap<String, HashMap<String, String>> template_Item_Map;
            HashMap<String, String> template_Point_Map;


            /**
             * 拼接模板模型map
             * sql排序后，从上到下遍历所有模板项目和要素。如果发现map中不存在当前项目，则新增一个bean bean中的namePath 取父节点的namePath 加当前项目名称
             * 如果当前项目存在要素，则创建一个template_Item_Map 添加进去。
             * 如果发现map中存在当前项目。则说明当前项目存在多条要素。将当前要素插入template_Item_Map即可。
             */
            while (rs.next()) {
                String templateItemId = rs.getString("templateItemId");
                String parent_id = rs.getString("parent_id");

                if (templateMap.containsKey(templateItemId)) {
                    dataBean = templateMap.get(templateItemId);
                    template_Item_Map = (LinkedHashMap<String, HashMap<String, String>>) dataBean.get("template_Item_Map");
                    if (StringUtils.isNotBlank(rs.getString("point_id"))) {
                        template_Point_Map = new HashMap<String, String>();
                        template_Point_Map.put("pointname", rs.getString("pointname"));//要素名称
                        String totalScore=rs.getString("totalScore");
                        totalScore = numberFormat(totalScore);//去掉多余的0
                        template_Point_Map.put("totalScore",totalScore );//总分
                        if (StringUtils.isNotBlank(object_Id)) {
                            String Score=rs.getString("Score")==null?"":rs.getString("Score");
                            if(Score.endsWith(".0")) {
                                Score = Score.substring(0, Score.indexOf("."));
                            }
                            template_Point_Map.put("Score", Score);//评分
                        }
                        template_Point_Map.put("Pointkind", rs.getString("Pointkind"));//要素类型

                        String strDescription = rs.getString("description");
                        if (StringUtils.isBlank(strDescription)) {
                            strDescription = "";
                        }

                        template_Point_Map.put("description", strDescription);//说明
                        template_Point_Map.put("scoreMode", rs.getString("scoreMode"));//打分方式 0 需要打分 1不需要

                        template_Item_Map.put(rs.getString("point_id"), template_Point_Map);
                    }
                    dataBean.set("template_Item_Map", template_Item_Map);
                } else {
                    dataBean = new LazyDynaBean();
                    String namePath = rs.getString("itemdesc");
                    if (StringUtils.isNotBlank(parent_id) && templateMap.containsKey(parent_id)) {
                        namePath = templateMap.get(parent_id).get("namePath") + "/" + namePath;
                    }
                    template_Item_Map = new LinkedHashMap<String, HashMap<String, String>>();
                    if (StringUtils.isNotBlank(rs.getString("point_id"))) {
                        template_Point_Map = new HashMap<String, String>();
                        template_Point_Map.put("pointname", rs.getString("pointname"));//要素名称
                        String totalScore=rs.getString("totalScore");
                        totalScore = numberFormat(totalScore);//去掉多余的0
                        template_Point_Map.put("totalScore", totalScore);//总分
                        if (StringUtils.isNotBlank(object_Id)) {
                            String Score=rs.getString("Score")==null?"":rs.getString("Score");
                            if(Score.endsWith(".0")) {
                                Score = Score.substring(0, Score.indexOf("."));
                            }
                            template_Point_Map.put("Score", Score);//评分
                        }
                        template_Point_Map.put("Pointkind", rs.getString("Pointkind"));//要素类型
                        String strDescription = rs.getString("description");
                        if (StringUtils.isBlank(strDescription)) {
                            strDescription = "";
                        }

                        template_Point_Map.put("description", strDescription);//说明
                        template_Point_Map.put("scoreMode", rs.getString("scoreMode"));//打分方式 0 需要打分 1不需要

                        template_Item_Map.put(rs.getString("point_id"), template_Point_Map);
                    }
                    dataBean.set("namePath", namePath);
                    dataBean.set("template_Item_Map", template_Item_Map);
                    templateMap.put(templateItemId, dataBean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return templateMap;
    }

    /**
     * 获取当前模板的指标数量
     * @param template_Id 模板id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 19:45 2018/5/23
     */
    public int getPointNum(String template_Id) throws GeneralException {
        int num=0;
        RowSet rs=null;
        try{
            ContentDAO dao=new ContentDAO(this.getConn());
            StringBuffer strSql=new StringBuffer();
            strSql.append(" SELECT COUNT(*) AS num ");
            strSql.append(" FROM per_template_item pti ");
            strSql.append(" INNER JOIN per_template_point ptp ON ptp.item_id = pti.item_id ");
            strSql.append(" INNER JOIN per_point ppt ON ptp.point_id = ppt.point_id AND ppt.validflag = 1 ");
            strSql.append(" WHERE pti.template_id = ? ");
            rs=dao.search(strSql.toString(),Arrays.asList(new String[]{template_Id}));
            if(rs.next()){
                num=rs.getInt("num");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return num;
    }

    /**
     * 获取当前模板下已经打分的指标数量
     * @param template_Id
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 19:45 2018/5/23
     */
    public HashMap<String,String> getMemberScoreNum(String template_Id) throws GeneralException {
        RowSet rs=null;
        HashMap<String,String> scoreMap=new HashMap<String, String>();
        try{
            ContentDAO dao=new ContentDAO(this.getConn());
            StringBuffer strSql=new StringBuffer();
            ArrayList dataList=new ArrayList();
            strSql.append(" SELECT kd.kh_object_id,COUNT(*) AS num FROM kh_mainbody km ");
            strSql.append(" INNER JOIN kh_detail kd ON kd.kh_object_id = km.kh_object_id AND km.id=kd.Kh_mainbody_id ");
            strSql.append(" WHERE upper(km.Mainbody_id)=? AND km.template_id=? AND km.Relation_id=? ");
            strSql.append(" GROUP BY kd.kh_object_id  ");
            dataList.add(this.getMainBody_Id());
            dataList.add(template_Id);
            dataList.add(this.getRelation_Id());
            rs=dao.search(strSql.toString(),dataList);
            while(rs.next()){
                scoreMap.put(rs.getString("kh_object_id"),rs.getString("num"));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return scoreMap;
    }

    /**
     * 获取评分考核对象信息，若object_List size为0 则取当前计划下全部考核对象
     *
     * @param object_List
     * @return
     * @author ZhangHua
     * @date 20:13 2018/4/12
     */
    public ArrayList<HashMap<String, String>> getObjectListInfo(ArrayList<String> object_List, String template_Id) throws GeneralException {
        RowSet rs = null;
        ArrayList<HashMap<String, String>> objectInfo = new ArrayList<HashMap<String, String>>();
        try {
            ContentDAO dao = new ContentDAO(this.getConn());
            StringBuffer strSql = new StringBuffer();
            strSql.append(" SELECT  km.id as mainBodyKey,").append(Sql_switcher.isnull("km.score", "0")).append(" as score,km.Status ,");
            strSql.append(" ko.Objectname ,ko.B0110 ,ko.E0122,ko.id as objectKey,ko.Object_id  ");
            strSql.append(" FROM  kh_mainbody km ");
            strSql.append(" INNER JOIN kh_object ko ON ko.Relation_id = km.Relation_id AND ko.id = km.kh_object_id and ko.template_Id=km.template_Id ");
            strSql.append(" WHERE  upper(km.Mainbody_id) =?  AND km.Relation_id =?  and upper(ko.template_Id)=? ");

            ArrayList dataList = new ArrayList();
            dataList.add(this.getMainBody_Id());
            dataList.add(this.getRelation_Id());
            dataList.add(template_Id);
            if (object_List.size() > 0) {
                strSql.append(" and ko.object_id in(");
                for (String id : object_List) {
                    strSql.append("?,");
                    dataList.add(PubFunc.decrypt(id));
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(" ) ");
            }
            strSql.append(" ORDER BY " + Sql_switcher.isnull("km.score", "0") + " desc,ko.seq");
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                HashMap<String, String> tempMap = new HashMap<String, String>();
                tempMap.put("objectKey", rs.getString("objectKey"));
                tempMap.put("object_id", rs.getString("Object_id"));
                tempMap.put("mainBodyKey", rs.getString("mainBodyKey"));
                String score=String.valueOf((float) Math.round(rs.getFloat("score")*100)/100);
                if(score.endsWith(".0")) {
                    score = score.substring(0, score.indexOf("."));
                }
                tempMap.put("score", score);
                tempMap.put("status", rs.getString("Status"));
                tempMap.put("object_name", rs.getString("Objectname"));
                tempMap.put("b0110", rs.getString("B0110"));
                tempMap.put("e0122", rs.getString("E0122"));
                objectInfo.add(tempMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return objectInfo;
    }

    /**
     * 获取评分考核模板，若object_List size为0 则取当前计划下全部考核模板
     *
     * @param object_List
     * @return
     * @author ZhangHua
     * @date 20:13 2018/4/12
     */
    public ArrayList<HashMap<String, String>> getTemplateInfo(ArrayList<String> object_List) throws GeneralException {
        RowSet rs = null;
        ArrayList<HashMap<String, String>> templateInfo = new ArrayList<HashMap<String, String>>();
        try {
            ContentDAO dao = new ContentDAO(this.getConn());
            StringBuffer strSql = new StringBuffer();
            strSql.append(" select Template_id ,Name from per_template where per_template.template_id in (SELECT  ko.template_id ");
            strSql.append(" FROM  kh_mainbody km ");
            strSql.append(" INNER JOIN kh_object ko ON ko.Relation_id = km.Relation_id AND ko.id = km.kh_object_id AND ko.template_id = km.template_id ");
            strSql.append(" WHERE  upper(km.Mainbody_id) =?  AND km.Relation_id =? ");

            ArrayList dataList = new ArrayList();
            dataList.add(this.getMainBody_Id());
            dataList.add(this.getRelation_Id());
            if (object_List.size() > 0) {
                strSql.append(" and  ko.object_id in(");
                for (String id : object_List) {
                    strSql.append("?,");
                    dataList.add(PubFunc.decrypt(SafeCode.decode(id)));
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(") ");
            }
            strSql.append(" group by ko.template_id ) order by per_template.Seq ");
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                HashMap<String, String> tempMap = new HashMap<String, String>();
                tempMap.put("template_Id", rs.getString("Template_id"));
                tempMap.put("template_Name", rs.getString("Name"));
                templateInfo.add(tempMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return templateInfo;
    }


    /**
     * 保存考核评分
     *
     * @param mainBodyStates 通过 getMainbodyStatus方法获取的考核主体状态
     * @param object_Key     考核对象主键id
     * @param dataList       保存数据集
     * @param template_id    模板id
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:05 2018/4/24
     */
    public void saveKh_Score(HashMap<String, String> mainBodyStates, String object_Key, ArrayList<MorphDynaBean> dataList, String template_id) throws GeneralException {
        try {
            HashMap<String, String> existsPointIdMap = new HashMap<String, String>();
            ContentDAO dao = new ContentDAO(this.getConn());
            //Status 0:未打分 1:正在编辑 2:已提交
            if (!"0".equals(mainBodyStates.get("Status"))) {
                existsPointIdMap = this.getExistsScoreDetailPointId(dao, mainBodyStates.get("Mainbody_Key"), object_Key);//获取需要更新的要素分数
            }
            ArrayList<MorphDynaBean> updateList = new ArrayList<MorphDynaBean>();
            ArrayList<MorphDynaBean> insertList = new ArrayList<MorphDynaBean>();
            if (existsPointIdMap.size() > 0) {
                for (MorphDynaBean bean : dataList) {
                    if (existsPointIdMap.containsKey(bean.get("id").toString().toLowerCase())) {
                        updateList.add(bean);
                    } else {
                        insertList.add(bean);
                    }
                }
                this.updateScoreDetail(dao, mainBodyStates.get("Mainbody_Key"), object_Key, updateList);//更新分数
            } else {
                insertList = dataList;
            }
            this.insertScoreDetail(dao, mainBodyStates.get("Mainbody_Key"), object_Key, insertList, template_id);//插入新分数
            String updatestr = "";


            //更新打分状态
            if ("0".equals(mainBodyStates.get("Status"))) {
                updatestr = " Status=1 , ";
            }
            String strSql = "update kh_mainbody set " + updatestr + " score=(select sum(Score) from kh_detail where Kh_mainbody_id=? and kh_object_id=? ) where id=? ";

            ArrayList list = new ArrayList();
            list.add(mainBodyStates.get("Mainbody_Key"));
            list.add(mainBodyStates.get("kh_object_id"));
            list.add(mainBodyStates.get("Mainbody_Key"));
            dao.update(strSql, list);


        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新打分记录
     *
     * @param dao
     * @param mainbody_Key
     * @param object_Key
     * @param dataList
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:08 2018/4/24
     */
    private void updateScoreDetail(ContentDAO dao, String mainbody_Key, String object_Key, ArrayList<MorphDynaBean> dataList) throws GeneralException {
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append("update kh_detail set Score=? where Kh_mainbody_id=? and kh_object_id=? and upper(Point_id)=? ");
            ArrayList updateList = new ArrayList();
            for (MorphDynaBean bean : dataList) {
                ArrayList list = new ArrayList();
                list.add(bean.get("score"));
                list.add(mainbody_Key);
                list.add(object_Key);
                list.add(bean.get("id").toString().toUpperCase());
                updateList.add(list);
            }
            if (updateList.size() > 0) {
                dao.batchUpdate(strSql.toString(), updateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 新增打分记录
     *
     * @param dao
     * @param mainbody_Key
     * @param object_Key
     * @param dataList
     * @param template_id
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:08 2018/4/24
     */
    private void insertScoreDetail(ContentDAO dao, String mainbody_Key, String object_Key, ArrayList<MorphDynaBean> dataList, String template_id) throws GeneralException {
        try {
            IDFactoryBean idf = new IDFactoryBean();
            StringBuffer strSql = new StringBuffer();
            strSql.append("insert into kh_detail( id,kh_object_id ,Kh_mainbody_id ,Score ,Amount ,template_id , Point_id ,Degree_id ,Reasons) values(?,?,?,?,?,?,?,?,?) ");
            ArrayList insertList = new ArrayList();
            for (MorphDynaBean bean : dataList) {
                ArrayList list = new ArrayList();
                list.add(idf.getId("kh_detail.id", "", this.getConn()));
                list.add(object_Key);
                list.add(mainbody_Key);
                list.add(bean.get("score"));//score
                list.add(0);//Amount
                list.add(template_id);//template_id
                list.add(bean.get("id"));//Point_id
                list.add("");//Degree_id
                list.add("");//Reasons

                insertList.add(list);
            }
            if (insertList.size() > 0) {
                dao.batchUpdate(strSql.toString(), insertList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取所有存在的要素分数
     *
     * @param dao
     * @param mainbody_Key
     * @param object_Key
     * @return
     * @throws GeneralException
     */
    private HashMap<String, String> getExistsScoreDetailPointId(ContentDAO dao, String mainbody_Key, String object_Key) throws GeneralException {
        HashMap<String, String> pointIdMap = new HashMap<String, String>();
        RowSet rs = null;
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append("select id,point_id from  kh_detail where Kh_mainbody_id=? and kh_object_id=?  ");
            ArrayList list = new ArrayList();
            list.add(mainbody_Key);
            list.add(object_Key);
            rs = dao.search(strSql.toString(), list);
            while (rs.next()) {
                pointIdMap.put(rs.getString("point_id").toLowerCase(), rs.getString("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return pointIdMap;
    }


    /**
     * 提交打发记录
     *
     * @param W0301
     * @param W0555
     * @param template_Id
     * @param submitPreList
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:09 2018/4/24
     */
    public void submitKhScore(String W0301, String W0555, String template_Id, ArrayList<String> submitPreList, ArrayList<String> object_List) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.getConn());
            //更新考核主体状态
            this.updateKh_mainbodyStatus(dao, submitPreList, template_Id);
            //更新考核对象表分数
            this.updateObjectScore(dao, submitPreList, template_Id);
            if (object_List.size() != 0 || "1".equals(this.getModel())) {
                //职称评审 需要排名整组人
                this.updateObjectRanking(dao, submitPreList, template_Id, this.getNeedSeqMember(dao, object_List, W0301, W0555));
            } else {
                //更新考核对象表排名
                this.updateObjectRanking(dao, submitPreList, template_Id, object_List);
            }
            //更新w05表
            this.updateW05(dao, W0301, W0555, template_Id, submitPreList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取职称评审时 当前组的全部人员
     *
     * @param dao
     * @param object_List
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:38 2018/5/21
     */
    private ArrayList<String> getNeedSeqMember(ContentDAO dao, ArrayList<String> object_List, String W0301, String W0555) throws GeneralException {
        ArrayList<String> memberList = new ArrayList<String>();
        RowSet rs = null;
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append(" SELECT W0505 FROM W05 INNER JOIN zc_categories_relations zc ON zc.w0501 = W05.W0501 ");
            strSql.append(" WHERE zc.categories_id in ");
            strSql.append(" (SELECT categories_id FROM zc_categories_relations ");
            strSql.append(" WHERE W0501=(SELECT w0501 FROM w05 WHERE W0505=? AND W0301=? AND  W0555=?))");
            strSql.append(" and zc.categories_id in (SELECT categories_id FROM zc_personnel_categories ");
            strSql.append(" WHERE W0301=? and review_links =?) ");
            ArrayList dataList = new ArrayList();
            dataList.add(object_List.get(0));
            dataList.add(W0301);
            dataList.add(W0555);
            dataList.add(W0301);
            dataList.add(W0555);
            rs = dao.search(strSql.toString(), dataList);
            while (rs.next()) {
                memberList.add(rs.getString("W0505"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return memberList;

    }

    /**
     * 提交到w05表
     *
     * @param dao
     * @param W0301         会议ID
     * @param W0555         评审环节ID
     * @param template_Id   模板ID
     * @param submitPreList 待提交的人员a0100
     * @throws GeneralException
     */
    private void updateW05(ContentDAO dao, String W0301, String W0555, String template_Id, ArrayList<String> submitPreList) throws GeneralException {
        try {
            String scoreCol = "C_" + template_Id, seqCol = "C_" + template_Id + "_seq";
            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();

            // 更新分数
            if (Sql_switcher.searchDbServer() == 2) {//oracle
            	//后面都需要更新排名，这里先更新为0.后面判断不为null的更新，保证总的排名正确
                strSql.append("update w05 set ").append(seqCol).append("=0,").append(scoreCol).append("=(select score from kh_object ko where template_id=? and Relation_id=? and ko.Object_id=w05.W0505 ) ");
                strSql.append(" where w05.W0505 in (select Object_id from kh_object where template_id=? and Relation_id=? and Id in(");
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                for (String str : submitPreList) {
                    strSql.append("?,");
                    dataList.add(str);
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(")) and W0301=? and W0555=?");
                dataList.add(W0301);
                dataList.add(W0555);


            } else {
                strSql.append("UPDATE w05 set ").append(scoreCol).append("=ko.score,").append(seqCol).append(" =ko.seq FROM w05 ");
                strSql.append("inner join kh_object ko on ko.object_id=w05.w0505 and ko.template_id=? and ko.Relation_id=? ");
                strSql.append("where ko.id in(");
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                for (String str : submitPreList) {
                    strSql.append("?,");
                    dataList.add(str);
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(") and  W0301=? and W0555=?");
                dataList.add(W0301);
                dataList.add(W0555);
            }
            dao.update(strSql.toString(), dataList);

            strSql.setLength(0);
            dataList.clear();


            //更新排名

            if (Sql_switcher.searchDbServer() == 2) {//oracle
                strSql.append("update w05 set ");
                strSql.append(seqCol).append("=(select seq from kh_object ko where template_id=? and Relation_id=? and ko.Object_id=w05.W0505 )  ");
                strSql.append(" where w05.W0505 in (select Object_id from kh_object where template_id=? and Relation_id=? ");
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                strSql.append(") and W0301=? and W0555=? and ").append(seqCol).append(" is not null");
                dataList.add(W0301);
                dataList.add(W0555);


            } else {
                strSql.append("UPDATE w05 set ").append(seqCol).append(" =ko.seq FROM w05 ");
                strSql.append("inner join kh_object ko on ko.object_id=w05.w0505 and ko.template_id=? and ko.Relation_id=? ");
                strSql.append("where  W0301=? and W0555=? and ").append(seqCol).append(" is not null");
                dataList.add(template_Id);
                dataList.add(this.getRelation_Id());
                dataList.add(W0301);
                dataList.add(W0555);
            }
            dao.update(strSql.toString(), dataList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新考核对象总分
     *
     * @param dao
     * @param submitPreList
     * @param template_Id
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:15 2018/4/24
     */
    private void updateObjectScore(ContentDAO dao, ArrayList<String> submitPreList, String template_Id) throws GeneralException {
        try {

            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();
            if (Sql_switcher.searchDbServer() == 2) {//oracle

                strSql.append("UPDATE kh_object SET score=(SELECT CAST(AVG("+ Sql_switcher.isnull(Sql_switcher.isnull("kh_mainbody.Score", "0"),"0") + ") AS DECIMAL(10, 2))  AS score FROM kh_mainbody ");
                strSql.append("where (Status=2) and Relation_id=? and template_id=? and kh_mainbody.kh_object_id=kh_object.id )");
                strSql.append(" where id in (");
                // dataList.add(this.getMainBody_Id());
                dataList.add(this.getRelation_Id());
                dataList.add(template_Id);
                for (String str : submitPreList) {
                    strSql.append("?,");
                    dataList.add(str);
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(")");
            } else {
                strSql.append("UPDATE kh_object SET score=kh.Score FROM kh_object ko ");
                strSql.append("INNER JOIN (SELECT CAST(AVG("+ Sql_switcher.isnull(Sql_switcher.isnull("kh_mainbody.Score", "0"),"0") + ") AS DECIMAL(10, 2))  AS score ,kh_object_id FROM kh_mainbody");
                strSql.append(" where (Status=2) and Relation_id=? and template_id=? and  kh_object_id in (");
                // dataList.add(this.getMainBody_Id());
                dataList.add(this.getRelation_Id());
                dataList.add(template_Id);
                for (String str : submitPreList) {
                    strSql.append("?,");
                    dataList.add(str);
                }
                strSql.deleteCharAt(strSql.length() - 1);
                strSql.append(") GROUP BY kh_object_id) kh ON kh.kh_object_id=ko.id ");

            }

            dao.update(strSql.toString(), dataList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新考核对象打分排名
     *
     * @param dao
     * @param submitPreList 待提交的人员a0100
     * @param template_id   模板id
     * @throws GeneralException
     * @author ZhangHua
     * @date 10:16 2018/4/24
     */
    private void updateObjectRanking(ContentDAO dao, ArrayList<String> submitPreList, String template_id, ArrayList<String> object_List) throws GeneralException {
        try {

            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();

            //更新排名 要重新计算所有人的排名
            if (Sql_switcher.searchDbServer() == 2) {//oracle
                strSql.append("");//rownum
                strSql.append("UPDATE kh_object SET seq=");
                strSql.append("(SELECT ko.seq FROM (select dense_rank() OVER( ORDER BY nvl(score,0)desc ) as seq, id from kh_object where template_id=? and Relation_id=? ");
                dataList.add(template_id);
                dataList.add(this.getRelation_Id());
                if (object_List.size() > 0) {
                    strSql.append(" and object_id in (");
                    for (String str : object_List) {
                        dataList.add(str);
                        strSql.append("?,");
                    }
                    strSql.deleteCharAt(strSql.length() - 1);
                    strSql.append(")");
                }
                strSql.append(" ) ko where kh_object.id=ko.id)   ");
                strSql.append(" where template_id=? and Relation_id=?");
                dataList.add(template_id);
                dataList.add(this.getRelation_Id());

                if (object_List.size() > 0) {
                    strSql.append(" and kh_object.object_id in (");
                    for (String str : object_List) {
                        dataList.add(str);
                        strSql.append("?,");
                    }
                    strSql.deleteCharAt(strSql.length() - 1);
                    strSql.append(")");
                }

            } else {
                int version = 8;
                DatabaseMetaData dbMeta = this.conn.getMetaData();
                version = dbMeta.getDatabaseMajorVersion();// sql2000=8 sql2005=9 sql2008=10 sql2012=11

                if (version == 8) {//sql2000 没有RANK() 函数，使用自联结的方式排名

                    strSql.append(" UPDATE kh_object SET seq= ISNULL(temp.seq, 0) + 1 FROM kh_object");
                    strSql.append("LEFT JOIN ( ");
                    strSql.append("SELECT  COUNT(1) AS seq ,ko.Object_id ,ko.template_id,ko.Relation_id,FROM kh_object ko");
                    strSql.append("INNER JOIN (SELECT ? AS Relation_id, ? AS template_id,score FROM kh_object WHERE Relation_id=? AND template_id=? ");
                    dataList.add(this.getRelation_Id());
                    dataList.add(template_id);
                    dataList.add(this.getRelation_Id());
                    dataList.add(template_id);
                    if (object_List.size() > 0) {
                        strSql.append(" and object_id in (");
                        for (String str : object_List) {
                            dataList.add(str);
                            strSql.append("?,");
                        }
                        strSql.deleteCharAt(strSql.length() - 1);
                        strSql.append(")");
                    }
                    strSql.append(" GROUP BY score) ko1 ON ko1.Relation_id = ko.Relation_id AND ko1.template_id = ko.template_id AND ko.score < ko1.score");
                    if (object_List.size() > 0) {
                        strSql.append(" where  ko.object_id in (");
                        for (String str : object_List) {
                            dataList.add(str);
                            strSql.append("?,");
                        }
                        strSql.deleteCharAt(strSql.length() - 1);
                        strSql.append(")");
                    }
                    strSql.append(" GROUP BY ko.Object_id ,ko.Relation_id ,ko.template_id");
                    strSql.append(" ) temp ON temp.Object_id = kh_object.Object_id AND temp.Relation_id = kh_object.Relation_id AND temp.template_id = kh_object.template_id");
                    strSql.append("WHERE kh_object.Relation_id=? AND kh_object.template_id=?");
                    dataList.add(this.getRelation_Id());
                    dataList.add(template_id);
                    if (object_List.size() > 0) {
                        strSql.append(" and  kh_object.object_id in (");
                        for (String str : object_List) {
                            dataList.add(str);
                            strSql.append("?,");
                        }
                        strSql.deleteCharAt(strSql.length() - 1);
                        strSql.append(")");
                    }

                } else {
                    strSql.append("UPDATE kh_object SET seq=tko.seq FROM kh_object ko ");
                    strSql.append("INNER JOIN (SELECT dense_rank() over ( ORDER BY isnull(score,0) DESC) AS seq ,id FROM kh_object where template_id=? and Relation_id=? ");
                    dataList.add(template_id);
                    dataList.add(this.getRelation_Id());
                    if (object_List.size() > 0) {
                        strSql.append(" and object_id in (");
                        for (String str : object_List) {
                            dataList.add(str);
                            strSql.append("?,");
                        }
                        strSql.deleteCharAt(strSql.length() - 1);
                        strSql.append(")");
                    }
                    strSql.append(") tko ON tko.id = ko.id ");

                }
            }
            dao.update(strSql.toString(), dataList);


        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新考核主体打分状态
     *
     * @param dao
     * @param submitPreList 带提交的人员a0100
     * @param template_id   模板id
     * @throws GeneralException
     */
    private void updateKh_mainbodyStatus(ContentDAO dao, ArrayList<String> submitPreList, String template_id) throws GeneralException {
        try {

            StringBuffer strSql = new StringBuffer();
            ArrayList dataList = new ArrayList();
            strSql.append("update kh_mainbody set Status=2 where template_id=? and Relation_id=? and upper(Mainbody_id)=? and kh_object_id in(");
            dataList.add(template_id);
            dataList.add(this.getRelation_Id());
            dataList.add(this.getMainBody_Id());
            for (String str : submitPreList) {
                strSql.append("?,");
                dataList.add(str);
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");
            dao.update(strSql.toString(), dataList);
        } catch (Exception e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public HashMap<String, HashMap<String, HashMap<String, Object>>> getScoreFromObejctList(ArrayList<String> objectKeyList, String templateId, String mainbodyId, String scoreTableName) throws GeneralException {
        HashMap<String, HashMap<String, HashMap<String, Object>>> dataMap = new HashMap<String, HashMap<String, HashMap<String, Object>>>();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.getConn());
            StringBuffer strSql = new StringBuffer();
            strSql.append("select km.kh_object_id as object_Id,st.Point_id,st.Score from kh_mainbody km ");
            strSql.append(" left join ").append(scoreTableName).append(" st on st.Kh_mainbody_id=km.id and st.kh_object_id=km.kh_object_id ");
            strSql.append("where km.Relation_id=? and km.template_id=? and upper(km.Mainbody_id)=? and km.kh_object_id in(");
            ArrayList list = new ArrayList();
            list.add(this.getRelation_Id());
            list.add(templateId);
            list.add(this.getMainBody_Id());
            for (String str : objectKeyList) {
                strSql.append("?,");
                list.add(str);
            }
            strSql.deleteCharAt(strSql.length() - 1);
            strSql.append(")");

            rs = dao.search(strSql.toString(), list);

            while (rs.next()) {
                String object_Id = rs.getString("object_Id");
                if (dataMap.containsKey(object_Id)) {
                    HashMap<String, HashMap<String, Object>> Pointmap = dataMap.get(object_Id);
                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("score", rs.getString("Score"));
                    Pointmap.put(rs.getString("Point_id"), data);
                    dataMap.put(object_Id, Pointmap);
                } else {
                    HashMap<String, HashMap<String, Object>> Pointmap = new HashMap<String, HashMap<String, Object>>();
                    HashMap<String, Object> data = new HashMap<String, Object>();
                    data.put("score", rs.getString("Score"));
                    Pointmap.put(rs.getString("Point_id"), data);
                    dataMap.put(object_Id, Pointmap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataMap;
    }


    public String getMainBody_Id() {
        return mainBody_Id.toUpperCase();
    }

    public void setMainBody_Id(String mainBody_Id) {
        this.mainBody_Id = mainBody_Id;
    }

    public String getRelation_Id() {
        return relation_Id;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public void setRelation_Id(String relation_Id) {
        this.relation_Id = relation_Id;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    private String numberFormat(String number) {
    	DecimalFormat df = new DecimalFormat("###############.##########");
    	if (StringUtils.isBlank(number))
		    return "";
		return df.format(Double.parseDouble(number));
    }
}
