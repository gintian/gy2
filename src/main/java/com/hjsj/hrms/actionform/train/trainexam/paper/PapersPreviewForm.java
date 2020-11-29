package com.hjsj.hrms.actionform.train.trainexam.paper;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class PapersPreviewForm extends FrameForm {
	
	private String returnId;//0:关闭、1:添加题型中预览试卷(人工组卷)、2：返回到自测考试、3：history.back()，4,自测考试自评中的返回，返回到自测考试
	private String flag;//flag=1 预览试卷 flag=2 线上考试 flag=3自测考试评卷   flag=4自测考试结果查看  flag=5自测考试 flag=6考试成绩查看,flag=7考试阅卷,flag=8答卷后查看
	private String exam_type;//考试类型，1自测，2考试
	private String r5300;
	private String title;//试卷标题
	private String examtime;//考试时间
	private String examscore;//考试分数
	private String examdescribe;//试卷描述
	private String isSingle;//是否启用单题计时   1=是，2=否
	private String count;//试卷试题总条数
	private String current;//当前记录数
	
	private String remaintime;//考试剩余时间（秒）
	
	private HashMap questionMap = new HashMap();
	
	// 课程id
	private String r5000;
	
	private String strsql;
	private String strwhere;
	private String columns;
	private String order_by;
	
	// 开始时间
	private String startTime;
	// 结束时间
	private String endTime;
	// 自测考试编号
	private String paper_id;
	// 考试计划id
	private String plan_id;
	// 用户考试得分
	private String score;
	// 剩余时间
	private String over;
	
	// 人员编号
	private String a0100;
	// 人员库
	private String nbase;
	// 考试时长
	private String examTimeLength;
	// 计划说明
	private String plandesc;
	
	// 允许光标离开次数，默认为-1
	private String r5413;
	// 是否是计时考试
	private String r5415;
	// 阅卷状态
	private String marking;
	//
	private String enableArch;

	private String home;
	

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public String getPlandesc() {
		return plandesc;
	}

	public void setPlandesc(String plandesc) {
		this.plandesc = plandesc;
	}

	public String getExamTimeLength() {
		return examTimeLength;
	}

	public void setExamTimeLength(String examTimeLength) {
		this.examTimeLength = examTimeLength;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getOver() {
		return over;
	}

	public void setOver(String over) {
		this.over = over;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("exam_type", exam_type);
		this.getFormHM().put("r5300", r5300);
	}

	public String getPaper_id() {
		return paper_id;
	}

	public void setPaper_id(String paper_id) {
		this.paper_id = paper_id;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTitle((String)this.getFormHM().get("title"));
		this.setExamtime((String)this.getFormHM().get("examtime"));
		this.setExamscore((String)this.getFormHM().get("examscore"));
		this.setExamdescribe((String)this.getFormHM().get("examdescribe"));
		this.setIsSingle((String)this.getFormHM().get("isSingle"));
		this.setRemaintime((String)this.getFormHM().get("remaintime"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setCurrent((String)this.getFormHM().get("current"));
		this.setQuestionMap((HashMap)this.getFormHM().get("questionMap"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setR5000((String) this.getFormHM().get("r5000"));
		this.setStartTime((String) this.getFormHM().get("startTime"));
		this.setEndTime((String) this.getFormHM().get("endTime"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setR5300((String) this.getFormHM().get("r5300"));
		this.setExam_type((String) this.getFormHM().get("exam_type"));
		this.setPaper_id((String) this.getFormHM().get("paper_id"));
		this.setScore((String) this.getFormHM().get("score"));	
		this.setPlan_id((String) this.getFormHM().get("plan_id"));
		this.setOver((String) this.getFormHM().get("over"));
		this.setA0100((String) this.getFormHM().get("a0100"));
		this.setNbase((String) this.getFormHM().get("nbase"));
		this.setExamTimeLength((String) this.getFormHM().get("examTimeLength"));
		this.setPlandesc((String) this.getFormHM().get("plandesc"));
		this.setR5413((String) this.getFormHM().get("r5413"));
		this.setR5415((String) this.getFormHM().get("r5415"));
		this.setMarking((String) this.getFormHM().get("marking"));
		this.setEnableArch((String) this.getFormHM().get("enableArch"));
		this.setHome((String) this.getFormHM().get("home"));
	
	}

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		 if("/train/trainexam/question/questiones/questiones".equals(arg0.getPath()) && arg1.getParameter("b_query") != null){
		        if(this.getPagination() != null) { 
		        	this.getPagination().firstPage();
		        }
		 }
		return super.validate(arg0, arg1);
	}
	
	public String getR5300() {
		return r5300;
	}

	public void setR5300(String r5300) {
		this.r5300 = r5300;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExamtime() {
		return examtime;
	}

	public void setExamtime(String examtime) {
		this.examtime = examtime;
	}

	public String getExamscore() {
		return examscore;
	}

	public void setExamscore(String examscore) {
		this.examscore = examscore;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getExam_type() {
		return exam_type;
	}

	public void setExam_type(String exam_type) {
		this.exam_type = exam_type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getExamdescribe() {
		return examdescribe;
	}

	public void setExamdescribe(String examdescribe) {
		this.examdescribe = examdescribe;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public HashMap getQuestionMap() {
		return questionMap;
	}

	public void setQuestionMap(HashMap questionMap) {
		this.questionMap = questionMap;
	}

	public String getRemaintime() {
		return remaintime;
	}

	public void setRemaintime(String remaintime) {
		this.remaintime = remaintime;
	}

	public String getIsSingle() {
		return isSingle;
	}

	public void setIsSingle(String isSingle) {
		this.isSingle = isSingle;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getR5413() {
		return r5413;
	}

	public void setR5413(String r5413) {
		this.r5413 = r5413;
	}

	public String getR5415() {
		return r5415;
	}

	public void setR5415(String r5415) {
		this.r5415 = r5415;
	}

    public String getEnableArch() {
        return enableArch;
    }

    public void setEnableArch(String enableArch) {
        this.enableArch = enableArch;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

}
