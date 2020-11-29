package com.hjsj.hrms.actionform.train.report;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class LessonAnalyseForm extends FrameForm {
    
    private String a_code;
    private String name;
    private String r5000;
    private String classValue;
    private String classViewvalue;
    private String lprogress_t;
    private String lprogress_d;
    private ArrayList itemlist;
    private String columns;
    private String strsql;
    private String strwhere;
    private String order_by;
    private String uplevel;
    
    private String mail;
    private String sms;
    private String template;
    
    private String trainNbases;
    
    private String restTime;
    
    private String weixin;
    
    private String enable_arch ;
    
    private String disable_exam_learning ;
    //判断存储过程存不存在
    private String existPro;

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

	private String speed;
	
	private ArrayList viewItemList;
	
	private ArrayList fieldItemList;
	
	private String viewItems;

	private String[] left_fields;
	
    private String[] right_fields;
    
    private String dingTalk;

    @Override
    public void inPutTransHM()
    {
        this.getFormHM().put("name", this.getName());
        this.getFormHM().put("classValue", this.getClassValue());
        this.getFormHM().put("classViewvalue", this.getClassViewvalue());
        this.getFormHM().put("r5000", this.getR5000());
        this.getFormHM().put("lprogress_t", this.getLprogress_t());
        this.getFormHM().put("lprogress_d", this.getLprogress_d());
        this.getFormHM().put("trainnbases", this.getTrainNbases());
        this.getFormHM().put("restTime", this.getRestTime());
        this.getFormHM().put("enable_arch", this.getEnable_arch());
        this.getFormHM().put("disable_exam_learning", this.getDisable_exam_learning());
        this.getFormHM().put("existPro", this.getExistPro());
		this.getFormHM().put("speed", this.getSpeed());
		this.getFormHM().put("viewItemList", this.getViewItemList());
		this.getFormHM().put("viewItems", this.getViewItems());
		this.getFormHM().put("fieldItemList", this.getFieldItemList());
		this.getFormHM().put("left_fields",this.getLeft_fields());
        this.getFormHM().put("right_fileds",this.getRight_fields());
        this.getFormHM().put("dingTalk",this.getDingTalk());
		
    }

    @Override
    public void outPutFormHM()
    {
        this.setR5000((String)this.getFormHM().get("r5000"));
        this.setClassValue((String)this.getFormHM().get("classValue"));
        this.setClassViewvalue((String)this.getFormHM().get("classViewvalue"));
        this.setName((String)this.getFormHM().get("name"));
        this.setLprogress_t((String)this.getFormHM().get("lprogress_t"));
        this.setLprogress_d((String)this.getFormHM().get("lprogress_d"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrsql((String)this.getFormHM().get("strsql"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));
        this.setOrder_by((String)this.getFormHM().get("order_by"));
        this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
        
        this.setMail((String)this.getFormHM().get("mail"));
        this.setSms((String)this.getFormHM().get("sms"));
        this.setTemplate((String)this.getFormHM().get("template"));
        this.setTrainNbases((String)this.getFormHM().get("trainnbases"));
        
        this.setRestTime((String)this.getFormHM().get("restTime"));
        
        this.setWeixin((String) this.getFormHM().get("weixin"));
        this.setEnable_arch((String) this.getFormHM().get("enable_arch"));
        this.setDisable_exam_learning((String) this.getFormHM().get("disable_exam_learning"));
        this.setExistPro((String) this.getFormHM().get("existPro"));
		this.setSpeed((String)this.getFormHM().get("speed"));
		this.setViewItemList((ArrayList)this.getFormHM().get("viewItemList"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("fieldItemList"));
		this.setViewItems((String)this.getFormHM().get("viewItems"));
		this.setDingTalk((String)this.getFormHM().get("dingTalk"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/train/report/lessonAnalyse".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            if(this.getPagination()!=null)
                this.getPagination().firstPage();   
        }
        return super.validate(arg0, arg1);
    }
    
    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
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

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getUplevel() {
        return uplevel;
    }

    public void setUplevel(String uplevel) {
        this.uplevel = uplevel;
    }

    public ArrayList getItemlist() {
        return itemlist;
    }

    public void setItemlist(ArrayList itemlist) {
        this.itemlist = itemlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getR5000() {
        return r5000;
    }

    public void setR5000(String r5000) {
        this.r5000 = r5000;
    }

    public String getA_code() {
        return a_code;
    }

    public void setA_code(String a_code) {
        this.a_code = a_code;
    }

    public String getLprogress_t() {
        return lprogress_t;
    }

    public void setLprogress_t(String lprogress_t) {
        this.lprogress_t = lprogress_t;
    }

    public String getLprogress_d() {
        return lprogress_d;
    }

    public void setLprogress_d(String lprogress_d) {
        this.lprogress_d = lprogress_d;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
    
    public String getTrainNbases() {
        return trainNbases;
    }

    public void setTrainNbases(String trainNbases) {
        this.trainNbases = trainNbases;
    }
    
    public String getRestTime() {
        return restTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    public String getEnable_arch() {
        return enable_arch;
    }

    public void setEnable_arch(String enable_arch) {
        this.enable_arch = enable_arch;
    }

    public String getDisable_exam_learning() {
        return disable_exam_learning;
    }

    public void setDisable_exam_learning(String disable_exam_learning) {
        this.disable_exam_learning = disable_exam_learning;
    }

    public String getClassValue() {
        return classValue;
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }

    public String getClassViewvalue() {
        return classViewvalue;
    }

    public void setClassViewvalue(String classViewvalue) {
        this.classViewvalue = classViewvalue;
    }

    public String getExistPro() {
        return existPro;
    }

    public void setExistPro(String existPro) {
        this.existPro = existPro;
    }
    
    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public ArrayList getViewItemList() {
        return viewItemList;
    }

    public void setViewItemList(ArrayList viewItemList) {
        this.viewItemList = viewItemList;
    }

    public String getViewItems() {
        return viewItems;
    }

    public void setViewItems(String viewItems) {
        this.viewItems = viewItems;
    }

    public ArrayList getFieldItemList() {
        return fieldItemList;
    }

    public void setFieldItemList(ArrayList fieldItemList) {
        this.fieldItemList = fieldItemList;
    }

    public String[] getLeft_fields() {
        return left_fields;
    }

    public void setLeft_fields(String[] leftFields) {
        left_fields = leftFields;
    }

    public String[] getRight_fields() {
        return right_fields;
    }

    public void setRight_fields(String[] rightFields) {
        right_fields = rightFields;
    }

    public String getDingTalk() {
        return dingTalk;
    }

    public void setDingTalk(String dingTalk) {
        this.dingTalk = dingTalk;
    }
	
}
