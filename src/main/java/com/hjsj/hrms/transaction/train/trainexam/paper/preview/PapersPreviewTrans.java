package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.businessobject.train.resource.MyLessonBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.*;

public class PapersPreviewTrans extends IBusiness {

    public void execute() throws GeneralException {
        Map map = (HashMap) this.getFormHM().get("requestPamaHM");
        String flag = (String) map.get("flag");
        String r5300 = (String) map.get("r5300");//试卷id
        r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
        String exam_type = (String) map.get("exam_type");//考试类型
        // 课程id
        String r5000 = (String) map.get("r5000");
        r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
        // paper_id
        String paper_id = (String) map.get("paper_id");
        paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
        // 计划id
        String plan_id = (String) map.get("plan_id");
        plan_id = PubFunc.decrypt(SafeCode.decode(plan_id));
        // 人员编号
        String a0100 = (String) map.get("a0100");
        a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
        map.remove("a0100");
        // 人员库
        String nbase = (String) map.get("nbase");
        nbase = PubFunc.decrypt(SafeCode.decode(nbase));
        map.remove("nbase");
        
        String home = (String) map.get("home");
        this.getFormHM().put("home", home);
        
        if((a0100 == null || a0100.length()<1) && (nbase == null || nbase.length()<1)) {
            a0100 = this.userView.getA0100();
            nbase = this.userView.getDbname();
        }
        
        // 查询的列
        String columns = "";
        String strsql = "";
        String strwhere = "";
        String order = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            if ("6".equals(flag)||"8".equals(flag)) {
                String sql = "select r5300,r5405,r5406 from r54 where r5400=" + paper_id;
                this.frowset = dao.search(sql);
                Date examStart = null;
                Date examEnd = null;
                if (this.frowset.next()) {
                    r5300 = this.frowset.getString("r5300");
                    examStart = this.frowset.getTimestamp("r5405");
                    examEnd = this.frowset.getTimestamp("r5406");
                }
                
                sql = "select * from r55 where r5400=" + paper_id + " and a0100='" + this.userView.getA0100() + "' and nbase='" + this.userView.getDbname() + "'";
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    
                    Date start = this.frowset.getTimestamp("r5506");
                    Date end = this.frowset.getTimestamp("r5507");
                    if (start != null && end != null) {
                        this.getFormHM().put("startTime",DateUtils.format(this.frowset.getTimestamp("r5506"), "yyyy-MM-dd HH:mm"));
                        this.getFormHM().put("endTime",DateUtils.format(this.frowset.getTimestamp("r5507"), "yyyy-MM-dd HH:mm"));
                        long over = (end.getTime() - start.getTime())/(1000 * 60);
                        this.getFormHM().put("examTimeLength", over + "");
                    } else {
                        if (examStart != null && examEnd != null) {
                            this.getFormHM().put("startTime", DateUtils.format(examStart, "yyyy-MM-dd HH:mm"));
                            this.getFormHM().put("endTime", DateUtils.format(examEnd, "yyyy-MM-dd HH:mm"));
                        } else {
                            this.getFormHM().put("startTime","");
                            this.getFormHM().put("endTime","");
                        }
                        this.getFormHM().put("examTimeLength", "0");
                    }
                    this.getFormHM().put("score", this.frowset.getFloat("r5504") + "");
                } else {
                    if (examStart != null && examEnd != null) {
                        this.getFormHM().put("startTime", DateUtils.format(examStart, "yyyy-MM-dd HH:mm"));
                        this.getFormHM().put("endTime", DateUtils.format(examEnd, "yyyy-MM-dd HH:mm"));
                    } else {
                        this.getFormHM().put("startTime","");
                        this.getFormHM().put("endTime","");
                    }
                    this.getFormHM().put("examTimeLength", "0");
                    this.getFormHM().put("score", "0");
                }
                
            }
            
            if ("7".equals(flag)) {
                String sql = "select r5300,r5405,r5406 from r54 where r5400=" + paper_id;
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    r5300 = this.frowset.getString("r5300");                    
                }
                
                sql = "select * from r55 where r5400=" + paper_id + " and a0100='" + a0100 + "' and nbase='" + nbase + "'";
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    // 阅卷状态
                    int marking = this.frowset.getInt("r5515");
                    Date start = this.frowset.getTimestamp("r5506");
                    Date end = this.frowset.getTimestamp("r5507");
                    if(start != null)
                        this.getFormHM().put("startTime",DateUtils.format(this.frowset.getTimestamp("r5506"), "yyyy-MM-dd HH:mm"));
                    if (end != null) 
                        this.getFormHM().put("endTime",DateUtils.format(this.frowset.getTimestamp("r5507"), "yyyy-MM-dd HH:mm"));
                    if (start != null && end != null) {
                        long over = (end.getTime() - start.getTime())/(1000 * 60);
                        this.getFormHM().put("examTimeLength", over + "");
                    } else {
                        this.getFormHM().put("examTimeLength", "0");
                    }
                    this.getFormHM().put("score", this.frowset.getFloat("r5504") + "");
                    this.getFormHM().put("marking", marking + "");
                } else {
                    this.getFormHM().put("examTimeLength", "0");
                    this.getFormHM().put("score", this.frowset.getFloat("r5504") + "");
                    this.getFormHM().put("marking", "0");
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if ("5".equals(flag) || "3".equals(flag)||"4".equals(flag)) {// 自测考试
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+floatToChar(Sql_switcher.isnull("a.score", "0.0"), "0.0")+" single_score,s_answer,o_answer";
            strwhere=" from (select * from tr_selfexam_test where paper_id="+paper_id+") t left join r52 r on r.r5200=t.r5200";
            strwhere+=" left join ( select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 ";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.paper_id="+paper_id;
            order = " order by q.norder ,t.norder";
        } else if("2".equals(flag) ){// 我的考试
        
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+floatToChar(Sql_switcher.isnull("a.score", "0.0"), "0.0")+" single_score,s_answer,o_answer";
            strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
            strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+plan_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.r5300="+r5300;
            if (Integer.parseInt(userView.getA0100()) % 2 == 1) {
                order = " order by q.norder,t.norder";
            } else {
                order = " order by q.norder,t.norder desc";
            }
            
        } else if("6".equals(flag)){// 我的考试
        
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+floatToChar(Sql_switcher.isnull("a.score", "0.0"), "0.0")+" single_score,s_answer,o_answer";
            strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
            strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.r5300="+r5300;
            
            order = " order by q.norder,t.norder";
            
            
        } else if("8".equals(flag)){// 我的考试
        
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+floatToChar(Sql_switcher.isnull("a.score", "0.0"), "0.0")+" single_score,s_answer,o_answer";
            strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
            strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.r5300="+r5300;
            
            order = " order by q.norder,t.norder";
            
            
        } else if("7".equals(flag)){// 我的考试
        
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+floatToChar(Sql_switcher.isnull("a.score", "0.0"), "0.0")+" single_score,s_answer,o_answer";
            strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
            strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+nbase+"' and a0100='"+a0100+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+nbase+"' and a0100='"+a0100+"'";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.r5300="+r5300;
            
            order = " order by q.norder,t.norder";
            
            
        }else {
            //试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
            columns="r5200,r5205,r5207,r5208,r5209,r5210,r5213,type_id,single_score,s_answer,o_answer";
            strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5213,t.type_id,"+/*Sql_switcher.isnull("a.score", "0.0")+*/"'' single_score,'' s_answer,'' o_answer";
            strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
            //strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+nbase+"' and a0100='"+a0100+"'";
            strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
            strwhere+=" where t.r5300="+r5300;
            order = " order by q.norder ,t.norder";
        }
        
        
        
        String sql = "select r5301,r5303,r5304,r5305 from r53 where r5300="+r5300;
        try {
            this.frowset = dao.search(sql);
            if(this.frowset.next()){
                this.getFormHM().put("title", this.frowset.getString("r5301"));
                this.getFormHM().put("examtime", this.frowset.getString("r5305"));
                this.getFormHM().put("examscore", this.frowset.getString("r5304"));
                String examdescribe = this.frowset.getString("r5303");
                examdescribe=examdescribe!=null&&examdescribe.length()>1?examdescribe.replace("\r\n", "<br/>"):"";
                this.getFormHM().put("examdescribe", examdescribe);
                
            }
            
            // 获得考试分数
            if ("4".equals(flag)) {
                String sqls = "select score from tr_selfexam_paper where paper_id=" + paper_id;
                this.frowset = dao.search(sqls);
                if(this.frowset.next()){
                    this.getFormHM().put("score", this.frowset.getFloat("score") + "");
                }
            }
            
            // 获得考试分数
            if ("6".equals(flag)) {
                String sqls = "select r5504 from r55 where a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"' and r5400=" + paper_id;
                this.frowset = dao.search(sqls);
                if(this.frowset.next()){
                    this.getFormHM().put("score", this.frowset.getFloat("r5504") + "");
                }
            }
            
            
            // 获得开始时间，结束时间，剩余时间
            if ("2".equals(flag)) {
                //陈旭光修改：暂时将考试时间按试卷的处理
                String sqlp = "select r5305 from r53 where r5300="+r5300;
                this.frowset = dao.search(sqlp);
                int r5305 = 0;
                if (this.frowset.next()) {
                 r5305 = this.frowset.getInt("r5305");
                }
                String sql2 = "select r5506,r5507 from r55 where r5400=" + plan_id + " and a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"'";
                this.frowset = dao.search(sql2);
                Date start = null;
                Date end = null;
                if (this.frowset.next()) {
                    start = this.frowset.getTimestamp("r5506");
                    end = this.frowset.getTimestamp("r5507");
                }
                String sqls = "select r5405,r5406,R5413,r5415,r5407 from r54 where r5400=" + plan_id;
                this.frowset = dao.search(sqls);
                if(this.frowset.next()){
                    String r5415 = this.frowset.getString("r5415");
                    r5415 = r5415 == null ? "2" : r5415;
                    
                    int over = r5305*60;
                    this.getFormHM().put("over", "" + over);
                    
                    if ("2".equals(r5415)) {
                        this.getFormHM().put("startTime",DateUtils.format(this.frowset.getTimestamp("r5405"), "yyyy-MM-dd HH:mm"));
                        this.getFormHM().put("endTime",DateUtils.format(this.frowset.getTimestamp("r5406"), "yyyy-MM-dd HH:mm"));
                        long tim = this.frowset.getTimestamp("r5406").getTime();
                        long nowt = new Date().getTime();
                        long over2 = (tim - nowt)/1000;
                        this.getFormHM().put("over", "" + over2);
                    } else {
                        Date d = new Date();
                        if (start != null) {
                            d = start;
                        }
                        this.getFormHM().put("startTime",DateUtils.format(d , "yyyy-MM-dd HH:mm"));
                        Calendar ca = Calendar.getInstance();
                        ca.setTime(d);
                        ca.add(Calendar.SECOND, over);
                        if (ca.getTimeInMillis() > this.frowset.getTimestamp("r5406").getTime()) {
                            this.getFormHM().put("over", "" + (this.frowset.getTimestamp("r5406").getTime() - new Date().getTime())/1000);
                            this.getFormHM().put("endTime",DateUtils.format(this.frowset.getTimestamp("r5406"), "yyyy-MM-dd HH:mm"));
                        } else {
                            this.getFormHM().put("over", "" + (ca.getTimeInMillis() - new Date().getTime())/1000);
                            this.getFormHM().put("endTime",DateUtils.format(ca.getTime(), "yyyy-MM-dd HH:mm"));
                        }
                    }
                    

                    
                    
                    String r5413 = this.frowset.getString("r5413");
                    r5413 = r5413 == null ? "-1" : r5413;
                    
                    
                    
                    this.getFormHM().put("r5413", r5413);
                    this.getFormHM().put("r5415", r5415);
                }
                
                
                // 更新考试状态
                String paperState = (String) map.get("paperState");
                
                // 更新结束时间
                RecordVo vo = new RecordVo("r55");
                vo.setString("nbase", this.userView.getDbname());
                vo.setString("a0100", this.userView.getA0100());
                vo.setInt("r5400", Integer.parseInt(plan_id));
                
                vo = dao.findByPrimaryKey(vo);
                Date date = vo.getDate("r5506");
                if (date == null) {
                    vo.setDate("r5506", new Date());
                }
                if (paperState != null && paperState.length() > 0) {
                    vo.setInt("r5513", Integer.parseInt(paperState));
                }
                dao.updateValueObject(vo);
                
            }
            
            // 添加计划描述
            if ("2".equals(flag) || "6".equals(flag) || "8".equals(flag)|| "7".equals(flag)|| "1".equals(flag)) {
            
                String sqls = "select r5403 from r54 where r5400=";
                if ("2".equals(flag)) {
                    if(plan_id != null && plan_id.length() > 0)
                        sqls += plan_id;
                    else
                        sqls += "null";
                } else {
                    if(paper_id != null && paper_id.length() > 0)
                        sqls += paper_id;
                    else
                        sqls += "null";
                }
                
                this.frowset = dao.search(sqls);
                if (this.frowset.next()) {
                    String str = this.frowset.getString("r5403");
                    if (str == null) {
                        str = "";
                    }
                    this.getFormHM().put("plandesc", str);
                } else {
                    this.getFormHM().put("plandesc", "");
                }
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if ("5".equals(flag)) {
            String examTime = (String) this.getFormHM().get("examtime");
            int timeLength = Integer.parseInt(examTime);
            Calendar ca = Calendar.getInstance();
            int startHour =  ca.get(Calendar.HOUR_OF_DAY);
            int startMinute = ca.get(Calendar.MINUTE);
            String startTime ="";
            if (startHour > 9) {
                startTime += startHour;
            } else {
                startTime += "0" + startHour;
            }
            startTime += ":";
            
            if (startMinute > 9) {
                startTime += startMinute;
            } else {
                startTime += "0" + startMinute;
            }
            
            
            ca.add(Calendar.MINUTE, timeLength);
            int endHour =  ca.get(Calendar.HOUR_OF_DAY);
            int endMinute = ca.get(Calendar.MINUTE);
            String endTime ="";
            if (endHour > 9) {
                endTime += endHour;
            } else {
                endTime += "0" + endHour;
            }
            endTime += ":";
            
            if (endMinute > 9) {
                endTime += endMinute;
            } else {
                endTime += "0" + endMinute;
            }
            
            this.getFormHM().put("startTime", startTime);
            this.getFormHM().put("endTime", endTime);
        }
        
        if("2".equals(flag) || "8".equals(flag)) {
            TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn, this.userView);
            ArrayList<String> msg = new ArrayList<String>();
            if("2".equals(flag))
                msg.add(plan_id);
            else
                msg.add(paper_id);
                
            msg.add(this.userView.getDbname());
            msg.add(this.userView.getA0100());
            ArrayList<ArrayList<String>> students = new ArrayList<ArrayList<String>>();
            students.add(msg);
            try {
                bo.updatePendingTask(students, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        MyLessonBo bo = new MyLessonBo(this.frameconn, this.userView);
        String enableArch = bo.getDisableExamOrEnableArch("");
        
        this.getFormHM().put("columns", columns);
        this.getFormHM().put("strsql", strsql);
        this.getFormHM().put("strwhere", strwhere);
        this.getFormHM().put("order_by", order);
        this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
        this.getFormHM().put("flag", flag);
        this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
        this.getFormHM().put("exam_type", exam_type);
        this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(paper_id)));
        this.getFormHM().put("plan_id", SafeCode.encode(PubFunc.encrypt(plan_id)));
        this.getFormHM().put("a0100", SafeCode.encode(PubFunc.encrypt(a0100)));
        this.getFormHM().put("nbase", SafeCode.encode(PubFunc.encrypt(nbase)));
        this.getFormHM().put("enableArch", enableArch);

    }
    
    private String floatToChar(String itemid,String f){
        StringBuffer strvalue = new StringBuffer();
        switch (Sql_switcher.searchDbServer())
        {
            case Constant.MSSQL:
                strvalue.append("CAST(");
                strvalue.append(itemid);
                strvalue.append(" AS NUMERIC(8,1))");
                break;
            case Constant.ORACEL:
                strvalue.append("TRIM(TO_CHAR(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
            case Constant.DB2:
                strvalue.append("CHAR(INT(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
        }
        return strvalue.toString();
    }
}
