package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * <p>
 * Title:SearchMyTestTrans.java
 * </p>
 * <p>
 * Description:保存自测答案
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-23 14:28:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SaveMyTestAnswerTrans extends IBusiness {

    public void execute() throws GeneralException {

        HashMap map = this.getFormHM();
        ArrayList insertList = new ArrayList();
        ArrayList updateList = new ArrayList();
        // 考试编号
        String paper_id = (String) map.get("paper_id");
        paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
        if (paper_id == null || ("").equals(paper_id)) {
            return;
        }
        //课程编号
        String r5000 = (String)this.getFormHM().get("r5000");
        if(!StringUtils.isEmpty(r5000))
            r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
        // 试卷编号
        String r5300 = (String) map.get("r5300");
        r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
        String flag = (String) map.get("flag");
        // 获得客观题答案
        String s_answer = (String) map.get("s_answer");
        map.remove("s_answer");
        s_answer = PubFunc.keyWord_reback(s_answer);
        String[] answers = s_answer.split(";");
        // 当前页
        String current = (String) map.get("current");
        // 状态
        String state = (String) map.get("state");
        state = state == null ? "" : state;
        //是否是提交试卷=1是自测提交试卷，其他不是（此参数用于判断是否调用自测合格归档的存储过程）
        String submit = (String) map.get("submit");
        submit = PubFunc.nullToStr(submit);
        
        // 标志
        String sign = "ok";
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            // flag=2考试中保存时判断试卷是否为已交卷状态
            int tmp = 0;
            if ("2".equals(flag)) {
                this.frowset = dao.search("select r5513 from r55 where r5400=" + paper_id + " and nbase='" + this.userView.getDbname() + "' and a0100='" + this.userView.getA0100() + "'");
                if (this.frowset.next()) {
                    tmp = this.frowset.getInt("r5513");
                }
            }

            if (tmp != 1) {// 只有flag=2 且为已交卷状态 tmp才会是1
                ArrayList upValuelist = new ArrayList();
                ArrayList inValuelist = new ArrayList();

                String nbase = this.userView.getDbname();
                String a0100 = this.userView.getA0100();
                int exam_no = Integer.parseInt(paper_id);
                int exam_type = 1;
                int r5200 = 0;

                for (int i = 0; i < answers.length; i++) {
                    upValuelist = new ArrayList();
                    inValuelist = new ArrayList();

                    String[] que = answers[i].split(":");
                    if (que.length > 0 && que[0].startsWith("answer_")) {
                        if ("2".equals(flag)) {
                            exam_type = 2;
                        }
                        r5200 = Integer.parseInt(que[0].substring(7).replaceAll("_" + flag + "_answer", ""));
                        // 判断将要保存的数据是否有重复的：有则continue；没有则更新/新增
                        HashMap hm = new HashMap();
                        String key = nbase + ":" + a0100 + ":" + exam_no + ":" + exam_type + ":" + r5200;
                        if (hm.containsKey(key))
                            continue;
                        else
                            hm.put(key, "1");

                        inValuelist.add(nbase);
                        inValuelist.add(a0100);
                        inValuelist.add(new Integer(exam_type));
                        inValuelist.add(new Integer(exam_no));
                        inValuelist.add(new Integer(r5200));

                        boolean msg = isExist(nbase, a0100, exam_no, exam_type, r5200);
                        if (msg) {
                            upValuelist.add(new Integer(r5300));
                            upValuelist.add(new Integer(0));
                            if (que.length > 1)
                                upValuelist.add(que[1]);
                            else
                                upValuelist.add("");

                            upValuelist.add(r5200 + "");
                            updateList.add(upValuelist);
                        } else {
                            inValuelist.add(new Integer(r5300));
                            inValuelist.add(new Integer(0));
                            if (que.length > 1)
                                inValuelist.add(que[1]);
                            else
                                inValuelist.add("");

                            insertList.add(inValuelist);
                        }
                    }
                }

                StringBuffer upsql = new StringBuffer();
                upsql.append("update tr_exam_answer set");
                upsql.append(" r5300=?,");
                upsql.append(" score=?,");
                upsql.append(" o_answer=?");
                upsql.append(" where nbase='" + nbase + "'");
                upsql.append(" and a0100='" + a0100 + "'");
                upsql.append(" and exam_no=" + exam_no);
                upsql.append(" and exam_type=" + exam_type);
                upsql.append(" and r5200=?");

                StringBuffer insql = new StringBuffer();
                insql.append("insert into tr_exam_answer (nbase,a0100,exam_type,exam_no,r5200,r5300,score,o_answer)");
                insql.append(" values (?,?,?,?,?,?,?,?)");

                dao.batchUpdate(upsql.toString(), updateList);
                dao.batchUpdate(insql.toString(), insertList);

                ArrayList upsqllist = new ArrayList();
                ArrayList insqllist = new ArrayList();

                // 获得主观题答案
                Set set = map.entrySet();
                Iterator it = set.iterator();
                while (it.hasNext()) {

                    upsql = new StringBuffer();
                    insql = new StringBuffer();

                    upValuelist = new ArrayList();
                    inValuelist = new ArrayList();

                    Map.Entry entry = (Map.Entry) it.next();
                    String key = entry.getKey().toString();
                    if (key.startsWith("answer_") && !key.startsWith("answer_o_")) {
                        if ("2".equals(flag)) {
                            exam_type = 2;
                        }

                        if (key.contains("answer_o_")) {
                            r5200 = Integer.parseInt(key.substring(9).replaceAll("_" + flag + "_answer", ""));
                        } else {
                            r5200 = Integer.parseInt(key.substring(7).replaceAll("_" + flag + "_answer", ""));
                        }

                        // 判断将要保存的数据是否有重复的：有则continue；没有则更新/新增
                        HashMap hm = new HashMap();
                        String hmkey = nbase + ":" + a0100 + ":" + exam_no + ":" + exam_type + ":" + r5200;
                        if (hm.containsKey(hmkey))
                            continue;
                        else
                            hm.put(hmkey, "1");

                        boolean msg = isExist(nbase, a0100, exam_no, exam_type, r5200);

                        if (msg) {
                            upsql.append("update tr_exam_answer set");
                            upsql.append(" r5300=" + r5300 + ",");
                            upsql.append(" score=0");

                            if (!(key.startsWith("answer_o_"))) {
                                upsql.append(", s_answer='" + SafeCode.decode(entry.getValue().toString()) + "'");
                            }

                            if (map.containsKey("answer_o_" + r5200)) {
                                upsql.append(", o_answer='" + SafeCode.decode(map.get(new StringBuffer("answer_o_").append(r5200).toString()).toString()) + "'");
                            }

                            upsql.append(" where nbase='" + nbase + "'");
                            upsql.append(" and a0100='" + a0100 + "'");
                            upsql.append(" and exam_no=" + exam_no);
                            upsql.append(" and exam_type=" + exam_type);
                            upsql.append(" and r5200=" + r5200);

                            upsqllist.add(upsql.toString());
                        } else {
                            String inc = "nbase,a0100,exam_type,exam_no,r5200,r5300,score";
                            String values = "'" + nbase + "','" + a0100 + "'," + exam_type + "," + exam_no + "," + r5200 + "," + Integer.parseInt(r5300) + ",0";

                            if (!(key.startsWith("answer_o_"))) {
                                inc = inc + ",s_answer";
                                values = values + ",'" + SafeCode.decode(entry.getValue().toString()) + "'";
                            }
                            if (map.containsKey("answer_o_" + r5200)) {
                                inc = inc + ",o_answer";
                                values = values + ",'" + SafeCode.decode(map.get(new StringBuffer("answer_o_").append(r5200).toString()).toString()) + "'";
                            }

                            insql.append("insert into tr_exam_answer (" + inc + ")");
                            insql.append(" values (" + values + ")");

                            insqllist.add(insql.toString());
                        }
                    }
                }

                dao.batchUpdate(upsqllist);
                dao.batchUpdate(insqllist);

                // 更新分数
                StringBuffer sql = new StringBuffer();
                sql.append("select an.r5200,an.o_answer,r.r5208,r.r5209,an.r5300,r.r5213 from tr_exam_answer an left join r52 r on an.r5200=r.r5200 where an.nbase='");
                sql.append(this.userView.getDbname());
                sql.append("' and an.a0100='" + this.userView.getA0100() + "' and an.exam_type=");
                if ("5".equals(flag)) {
                    sql.append(1 + "");
                } else if ("2".equals(flag)) {
                    sql.append(2 + "");
                }
                sql.append(" and an.exam_no=");
                sql.append(paper_id);
                sql.append(" and r5300=");
                sql.append(r5300);

                this.frowset = dao.search(sql.toString());
                while (this.frowset.next()) {
                    // 试题编号
                    r5200 = this.frowset.getInt("r5200");
                    // 用户答案
                    String answer = this.frowset.getString("o_answer");
                    answer = answer == null ? "" : answer;
                    if (answer.startsWith(",")) {
                        answer = answer.substring(1);
                    }

                    // 试题正确答案
                    String quest = this.frowset.getString("r5209");
                    if (quest == null || quest.length() < 1)
                        quest = this.frowset.getString("r5208");

                    quest = quest == null ? "" : quest;
                    if(StringUtils.isNotEmpty(quest) && quest.length() > 1) {
                        String[] quests = quest.split(",");
                        quest = "";
                        for(int i = 0; i < quests.length; i++) {
                            if(StringUtils.isEmpty(quests[i]))
                                continue;
                            
                            char[] ch = quests[i].toCharArray();
                            if (ch[0] >= 'A' && ch[0] <= 'Z') {
                                quest += quests[i] + ",";
                            }
                        }
                    }

                    if (quest.startsWith(",")) {
                        quest = quest.substring(1);
                    }

                    // 试题分数
                    float score = this.frowset.getFloat("r5213");

                    // 判断答案是否正确
                    boolean judge = true;

                    if (answer.split(",").length == quest.split(",").length) {
                        String[] qu = quest.split(",");
                        for (int i = 0; i < qu.length; i++) {
                            if (!answer.contains(qu[i])) {
                                judge = false;
                                break;
                            }
                        }
                    } else {
                        judge = false;
                    }

                    if (quest.length() <= 0) {
                        judge = false;
                    }

                    // 保存分数
                    upsql = new StringBuffer();
                    upsql.append("update tr_exam_answer set");
                    upsql.append(" r5300=" + r5300 + ",");

                    exam_type = 1;
                    if ("2".equals(flag)) {
                        exam_type = 2;
                    }

                    if (judge)
                        upsql.append(" score=" + score + ",");
                    else {
                        upsql.append(" score=0,");
                    }
                    upsql.append("o_answer='" + answer + "'");

                    upsql.append(" where nbase='" + this.userView.getDbname() + "'");
                    upsql.append(" and a0100='" + this.userView.getA0100() + "'");
                    upsql.append(" and exam_no=" + exam_no);
                    upsql.append(" and exam_type=" + exam_type);
                    upsql.append(" and r5200=" + r5200);
                    upsqllist.add(upsql.toString());
                }

                dao.batchUpdate(upsqllist);

                if ("5".equals(flag)) {
                    // 更新自测分数
                    MyTestBo bo = new MyTestBo(this.frameconn);
                    bo.updateScore(this.userView, paper_id, "1",r5300,r5000,submit);
                }

                if ("2".equals(flag)) {
                    String paperState = (String) this.getFormHM().get("paperState");

                    // 更新结束时间
                    RecordVo vo = new RecordVo("r55");
                    vo.setString("nbase", this.userView.getDbname());
                    vo.setString("a0100", this.userView.getA0100());
                    vo.setInt("r5400", Integer.parseInt(paper_id));

                    vo = dao.findByPrimaryKey(vo);
                    vo.setDate("r5507", new Date());
                    if (paperState != null && paperState.length() > 0) {
                        vo.setInt("r5513", Integer.parseInt(paperState));
                    }

                    // 开始时间
                    Date start = vo.getDate("r5506");
                    Date end = vo.getDate("r5507");
                    long timeLen = (end.getTime() - start.getTime()) / (1000 * 60);
                    vo.setInt("r5509", (int) timeLen);
                    dao.updateValueObject(vo);

                    int s = vo.getInt("r5513");
                    this.getFormHM().put("r5313", s + "");

                    // 判断是否自动阅卷 2013-12-06 gdd
                    TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn);
                    planBo.loadMessageParam(paper_id);
                    Boolean autoCompute = planBo.getAutoCompute();
                    Boolean autoRelease = planBo.getAutoRelease();
                    MyTestBo bo = new MyTestBo(this.frameconn);
                    if (autoCompute.booleanValue() && paperState != null && paperState.length() > 0)
                        // 自动阅卷
                        bo.autoComputeExam(this.userView, paper_id, autoRelease);
                    else
                        // 更新自测分数
                        bo.updateTestScore(this.userView, paper_id, "2");

                }

            } else {
                sign = "已交试卷不允许再修改！";
            }

        } catch (Exception e) {
            sign = "no";
            e.printStackTrace();
        }

        this.getFormHM().put("biaozhi", sign);
        this.getFormHM().put("flag", flag);
        this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(paper_id)));
        this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
        this.getFormHM().put("state", state);
        this.getFormHM().put("current", current);

    }

    /**
     * 查询考试答案表中是否存在
     * 
     * @param nbase
     *            人员库
     * @param a0100
     *            人员编号
     * @param exam_no
     *            考试计划编号
     * @param exam_type
     *            考试类型
     * @param r5200
     *            试题编号
     * @return true:存在|false：不存在
     */
    public boolean isExist(String nbase, String a0100, int exam_no, int exam_type, int r5200) {
        boolean flag = false;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer sql = new StringBuffer();
            sql.append("select 1 from tr_exam_answer");
            sql.append(" where nbase='" + nbase + "'");
            sql.append(" and a0100='" + a0100 + "'");
            sql.append(" and exam_no='" + exam_no + "'");
            sql.append(" and exam_type='" + exam_type + "'");
            sql.append(" and r5200='" + r5200 + "'");

            this.frowset = dao.search(sql.toString());
            if (this.frowset.next())
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
