package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DelPosCourseTrans extends IBusiness {

    public void execute() throws GeneralException {
        String state = (String) this.getFormHM().get("state");
        ArrayList posCourselist = (ArrayList) this.getFormHM().get("selectedList");
        HashMap hm = new HashMap();
        String tmpDel = "";
        ContentDAO dao = new ContentDAO(this.frameconn);
        RecordVo vo = null;
        Savepoint sv = null;
        try {
            frameconn.setAutoCommit(false);
            sv = frameconn.setSavepoint();
            for (int i = 0; i < posCourselist.size(); i++) {
                LazyDynaBean dynaBean = (LazyDynaBean) posCourselist.get(i);
                vo = new RecordVo("tr_job_course");
                vo.setString("job_id", (String) dynaBean.get("job_id"));
                vo.setString("r5000", (String) dynaBean.get("r5000"));
                vo.setInt("state", Integer.parseInt(state));
                dao.deleteValueObject(vo);

                if (hm.containsKey((String) dynaBean.get("job_id"))) {
                    String lessonid = (String) hm.get((String) dynaBean.get("job_id"));
                    if (lessonid != null && lessonid.length() > 0)
                        lessonid += ",";

                    lessonid += (String) dynaBean.get("r5000");
                    hm.put((String) dynaBean.get("job_id"), lessonid);
                } else
                    hm.put((String) dynaBean.get("job_id"), (String) dynaBean.get("r5000"));

                if (tmpDel != null && tmpDel.indexOf((String) dynaBean.get("job_id")) == -1)
                    tmpDel += (String) dynaBean.get("job_id") + ",";
                else
                    tmpDel = (String) dynaBean.get("job_id") + ",";

                if (tmpDel.endsWith(","))
                    tmpDel = tmpDel.substring(0, tmpDel.length() - 1);

            }

            // 删除关联学员（推送）
            ArrayList sqllist = new ArrayList();
            ArrayList whereList = getWhere(tmpDel);
            String sql = "delete tr_selected_lesson where";
            if ("1".equals(state))
                sql += " lesson_from=0";
            else if ("2".equals(state))
                sql += " lesson_from=4";

            if (whereList == null){
                sql += " and 1=2";
                sqllist.add(sql);
            }else {
                for (int i = 0; i < whereList.size(); i++) {
                    String where = (String) whereList.get(i);
                    String[] wheres = where.split("::");
                    String jobid = wheres[0];

                    String wherestr = wheres[1];
                    String[] wherestrs = wherestr.split("@@");
                    String sqlwhere = "";
                    for (int n = 0; n < wherestrs.length; n++) {
                        String strwhere = wherestrs[n];
                        String[] strwheres = strwhere.split("&&");
                        sqlwhere += " and (nbase ='" + strwheres[0] + "' and a0100 in (" + strwheres[1] + "))";
                    }

                    sql += sqlwhere;
                    String r5000 = (String) hm.get(jobid);
                    String[] r5000s = r5000.split(",");
                    int m = 0;
                    String id = "";
                    for (int s = 0; s < r5000s.length; s++) {
                        if (m > 0)
                            id += ",";
                        id += "'" + r5000s[s] + "'";
                        m++;

                        if (m == 1000) {
                            sqllist.add(sql += " and r5000 in(" + id + ")");
                            // 删除自测分数记录
                            sqllist.add("delete from tr_selfexam_paper where " + sqlwhere + " and r5300 in (select r5300 from tr_lesson_paper where r5000 in (" + id + "))");
                            // 删除自测考试答案记录
                            sqllist.add("delete from tr_exam_answer where " + sqlwhere + " and exam_type = 1 and r5300 in (select r5300 from tr_lesson_paper where r5000 in (" + id + "))");
                            //删除对应的scorm课件记录
                            sqllist.add("delete from tr_selected_course_scorm where " + sqlwhere + " and r5100 in (select r5100 from r51 where r5000 in (" + id + "))");
                            // 删除对应的课件记录
                            sqllist.add("delete from tr_selected_course where " + sqlwhere + " and r5100 in (select r5100 from r51 where r5000 in (" + id + "))");
                            id = "";
                            m = 0;
                        }
                    }

                    if (id.length() > 0) {
                        sqllist.add(sql += " and r5000 in(" + id + ")");
                        // 删除自测分数记录
                        sqllist.add("delete from tr_selfexam_paper where " + sqlwhere.substring(5) + " and r5300 in (select r5300 from tr_lesson_paper where r5000 in (" + id + "))");
                        // 删除自测考试答案记录
                        sqllist.add("delete from tr_exam_answer where " + sqlwhere.substring(5) + " and exam_type = 1 and r5300 in (select r5300 from tr_lesson_paper where r5000 in (" + id + "))");
                        //删除对应的scorm课件记录
                        sqllist.add("delete from tr_selected_course_scorm where " + sqlwhere.substring(5) + " and r5100 in (select r5100 from r51 where r5000 in (" + id + "))");
                        // 删除对应的课件记录
                        sqllist.add("delete from tr_selected_course where " + sqlwhere.substring(5) + " and r5100 in (select r5100 from r51 where r5000 in (" + id + "))");
                    }
                }
            }
            dao.batchUpdate(sqllist);
            frameconn.commit();
        } catch (Exception e) {
            try {
                this.frameconn.rollback(sv);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        } finally {
            try {
                sv = null;
                frameconn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList getWhere(String selectid) throws Exception {
        String codesetid = (String) this.getFormHM().get("codesetid");
        String state = (String) this.getFormHM().get("state");
        TrainCourseBo trainCourseBo = new TrainCourseBo(this.userView, this.frameconn);
        ArrayList returnList = new ArrayList();
        try {
            ArrayList dbprivlist = trainCourseBo.getTrainNbases(state);
            // 查看人员基本情况子集中是否有管理codesetid的指标
            String fielditemid = "";
            ArrayList list = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
            for (int i = list.size() - 1; i >= 0; i--) {
                FieldItem item = (FieldItem) list.get(i);
                if (codesetid.equals(item.getCodesetid())) {
                    fielditemid = item.getItemid();
                    break;
                }
            }
            HashSet job_idset = new HashSet();
            String[] selectids = null;
            if (selectid != null && selectid.length() > 0)
                selectids = selectid.split(",");

            for (int i = selectids.length - 1; i >= 0; i--) {
                String id = (String) selectids[i];
                job_idset.add(id);
            }

            HashMap job_idMap = new HashMap();
            if (fielditemid.length() < 1) {
                RecordVo ps_job_vo = null;
                String temp = "";
                if ("1".equals(state))// 岗位
                    ps_job_vo = ConstantParamter.getRealConstantVo("PS_C_JOB", this.getFrameconn());
                else
                    ps_job_vo = ConstantParamter.getRealConstantVo("PS_JOB", this.getFrameconn());

                if (ps_job_vo != null) {
                    String ps_job = ps_job_vo.getString("str_value");
                    if (ps_job.replaceAll("#", "").length() > 0) {
                        for (Iterator i = job_idset.iterator(); i.hasNext();) {
                            String job_id = (String) i.next();
                            String sql = "select e01a1 from k01 where " + ps_job + " like '" + job_id + "%'";
                            job_idMap.put(job_id, sql);
                        }
                    } else {
                        if ("1".equals(state))// 岗位
                            temp = ResourceFactory.getProperty("pos.posbusiness.nosetposccode.job");
                        else
                            temp = ResourceFactory.getProperty("pos.posbusiness.nosetposcode.job");
                        throw new Exception(temp);
                    }
                } else {
                    if ("1".equals(state))// 岗位
                        temp = ResourceFactory.getProperty("pos.posbusiness.nosetposccode.job");
                    else
                        temp = ResourceFactory.getProperty("pos.posbusiness.nosetposcode.job");
                    throw new Exception(temp);
                }

            }

            String sql = "";
            for (int n = selectids.length - 1; n >= 0; n--) {
                String id = selectids[n];
                String sqls = "";
                if (fielditemid.length() < 1) {
                    for (int i = dbprivlist.size() - 1; i >= 0; i--) {
                        String dbpre = (String) dbprivlist.get(i);
                        String wheresql = (String) job_idMap.get(id);
                        sql = "select a0100 from " + dbpre + "a01 where e01a1 in (" + wheresql + ")";
                        if (sqls != null && sqls.length() > 0)
                            sqls += "@@";
                        sqls += dbpre + "&&" + sql;
                    }
                    returnList.add(id + "::" + sqls);
                } else {
                    for (int i = dbprivlist.size() - 1; i >= 0; i--) {
                        String dbpre = (String) dbprivlist.get(i);
                        sql = "select a0100 from " + dbpre + "a01 where " + fielditemid + "='" + id + "'";
                        if (sqls != null && sqls.length() > 0)
                            sqls += "@@";
                        sqls += dbpre + "&&" + sql;
                    }
                    returnList.add(id + "::" + sqls);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return returnList;
    }
}
