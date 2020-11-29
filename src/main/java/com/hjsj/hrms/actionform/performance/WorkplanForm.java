package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:WorkplanForm.java</p>
 * <p>Description:工作计划参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:11:11</p> 
 * @author JinChunhai
 * @version 6.0
 */

public class WorkplanForm extends FrameForm
{

    private String sp_relation = ""; // 审批关系
    private ArrayList sp_relationList = new ArrayList();
    private String sp_level = "1"; // 审批层级 1: 一级审批 2:逐级审批 默认为1
    private ArrayList sp_levelList = new ArrayList();
    private String record_grade = "False"; // 是否对纪实进行评分 False：不评 True：评 默认为 False
    private String defaultLines = "12"; // 工作计划默认显示行数
    private String dailyPlan_attachment = "True"; // 是否显示工作计划附件上传功能 False：不显示 True：显示 默认为 True
    private String dailySumm_attachment = "True"; // 是否显示工作总结附件上传功能 False：不显示 True：显示 默认为 True
    private String planTarget = ""; // 工作计划显示的日志指标
    private ArrayList plantargetList = new ArrayList(); 
    private String summTarget = ""; // 工作总结显示的日志指标
    private ArrayList summtargetList = new ArrayList();
    private String nbase = ""; // 授权的人员库
    private ArrayList dbnameList = new ArrayList(); // 人员库列表
    
    // 年报 工作计划参数
    private String valid14 = "True";
    private String prior_end14 = "";
    private String current_start14 = "";    
    private String refer_id14 = "";
    private String print_id14 = "";
	// 年报 工作总结参数
    private String valid24 = "True";
    private String current_end24 = "";
    private String last_start24 = "";
    private String refer_id24 = "";
    private String print_id24 = "";

	// 季报 工作计划参数
    private String valid13 = "True";
    private String prior_end13 = "";
    private String current_start13 = "";
    private String period13 = "";
    private String period131 = "False";
    private String period132 = "False";
    private String period133 = "False";
    private String period134 = "False";
    private String refer_id13 = "";
    private String print_id13 = "";
	// 季报 工作总结参数
    private String valid23 = "True";
    private String current_end23 = "";
    private String last_start23 = "";
    private String period23 = "";
    private String period231 = "False";
    private String period232 = "False";
    private String period233 = "False";
    private String period234 = "False";
    private String refer_id23 = "";
    private String print_id23 = "";
	
	// 月报 工作计划参数
    private String valid12 = "True";
    private String prior_end12 = "";
    private String current_start12 = "";
    private String period12 = "";
    private String period121 = "False";
    private String period122 = "False";
    private String period123 = "False";
    private String period124 = "False";
    private String period125 = "False";
    private String period126 = "False";
    private String period127 = "False";
    private String period128 = "False";
    private String period129 = "False";
    private String period1210 = "False";
    private String period1211 = "False";
    private String period1212 = "False";
    private String refer_id12 = "";
    private String print_id12 = "";
	// 月报 工作总结参数
    private String valid22 = "True";
    private String current_end22 = "";
    private String last_start22 = "";
    private String period22 = "";
    private String period221 = "False";
    private String period222 = "False";
    private String period223 = "False";
    private String period224 = "False";
    private String period225 = "False";
    private String period226 = "False";
    private String period227 = "False";
    private String period228 = "False";
    private String period229 = "False";
    private String period2210 = "False";
    private String period2211 = "False";
    private String period2212 = "False";
    private String refer_id22 = "";
    private String print_id22 = "";
	
	// 周报 工作计划参数
    private String valid11 = "True";
    private String prior_end11 = "";
    private String current_start11 = "";
    private String refer_id11 = "";
    private String print_id11 = "";
	// 周报 工作总结参数
    private String valid21 = "True";
    private String current_end21 = "";
    private String last_start21 = "";
    private String refer_id21 = "";
    private String print_id21 = "";
	
	// 日报 工作计划参数
    private String valid0 = "True";
    private String current_date = "0";
    private String time = "";
    private String limit_HH = "00";
    private String limit_MM = "00";
    private String refer_id0 = "";
    private String print_id0 = "";
    
    // 人员登记表
    private ArrayList personSheetList = new ArrayList();	
    
    @Override
    public void inPutTransHM()
    { 
    	
    	this.getFormHM().put("valid14", this.getValid14());
    	this.getFormHM().put("prior_end14", this.getPrior_end14());
    	this.getFormHM().put("current_start14", this.getCurrent_start14());
    	this.getFormHM().put("refer_id14", this.getRefer_id14());
    	this.getFormHM().put("print_id14", this.getPrint_id14());
    	this.getFormHM().put("valid24", this.getValid24());
    	this.getFormHM().put("current_end24", this.getCurrent_end24());
    	this.getFormHM().put("last_start24", this.getLast_start24());
    	this.getFormHM().put("refer_id24", this.getRefer_id24());
    	this.getFormHM().put("print_id24", this.getPrint_id24());
    	
    	this.getFormHM().put("valid13", this.getValid13());
    	this.getFormHM().put("prior_end13", this.getPrior_end13());
    	this.getFormHM().put("current_start13", this.getCurrent_start13());
    	this.getFormHM().put("period13", this.getPeriod13());
    	this.getFormHM().put("period131", this.getPeriod131());
    	this.getFormHM().put("period132", this.getPeriod132());
    	this.getFormHM().put("period133", this.getPeriod133());
    	this.getFormHM().put("period134", this.getPeriod134());    	
    	this.getFormHM().put("refer_id13", this.getRefer_id13());
    	this.getFormHM().put("print_id13", this.getPrint_id13());
    	this.getFormHM().put("valid23", this.getValid23());
    	this.getFormHM().put("current_end23", this.getCurrent_end23());
    	this.getFormHM().put("last_start23", this.getLast_start23());
    	this.getFormHM().put("period23", this.getPeriod23());
    	this.getFormHM().put("period231", this.getPeriod231());
    	this.getFormHM().put("period232", this.getPeriod232());
    	this.getFormHM().put("period233", this.getPeriod233());
    	this.getFormHM().put("period234", this.getPeriod234());
    	this.getFormHM().put("refer_id23", this.getRefer_id23());
    	this.getFormHM().put("print_id23", this.getPrint_id23());
    	
    	this.getFormHM().put("valid12", this.getValid12());
    	this.getFormHM().put("prior_end12", this.getPrior_end12());
    	this.getFormHM().put("current_start12", this.getCurrent_start12());
    	this.getFormHM().put("period12", this.getPeriod12());
    	this.getFormHM().put("period121", this.getPeriod121());
    	this.getFormHM().put("period122", this.getPeriod122());
    	this.getFormHM().put("period123", this.getPeriod123());
    	this.getFormHM().put("period124", this.getPeriod124());
    	this.getFormHM().put("period125", this.getPeriod125());
    	this.getFormHM().put("period126", this.getPeriod126());
    	this.getFormHM().put("period127", this.getPeriod127());
    	this.getFormHM().put("period128", this.getPeriod128());
    	this.getFormHM().put("period129", this.getPeriod129());
    	this.getFormHM().put("period1210", this.getPeriod1210());
    	this.getFormHM().put("period1211", this.getPeriod1211());
    	this.getFormHM().put("period1212", this.getPeriod1212());
    	this.getFormHM().put("refer_id12", this.getRefer_id12());
    	this.getFormHM().put("print_id12", this.getPrint_id12());
    	this.getFormHM().put("valid22", this.getValid22());
    	this.getFormHM().put("current_end22", this.getCurrent_end22());
    	this.getFormHM().put("last_start22", this.getLast_start22());
    	this.getFormHM().put("period22", this.getPeriod22());
    	this.getFormHM().put("period221", this.getPeriod221());
    	this.getFormHM().put("period222", this.getPeriod222());
    	this.getFormHM().put("period223", this.getPeriod223());
    	this.getFormHM().put("period224", this.getPeriod224());
    	this.getFormHM().put("period225", this.getPeriod225());
    	this.getFormHM().put("period226", this.getPeriod226());
    	this.getFormHM().put("period227", this.getPeriod227());
    	this.getFormHM().put("period228", this.getPeriod228());
    	this.getFormHM().put("period229", this.getPeriod229());
    	this.getFormHM().put("period2210", this.getPeriod2210());
    	this.getFormHM().put("period2211", this.getPeriod2211());
    	this.getFormHM().put("period2212", this.getPeriod2212());
    	this.getFormHM().put("refer_id22", this.getRefer_id22());
    	this.getFormHM().put("print_id22", this.getPrint_id22());
    	
    	this.getFormHM().put("valid11", this.getValid11());
    	this.getFormHM().put("prior_end11", this.getPrior_end11());
    	this.getFormHM().put("current_start11", this.getCurrent_start11());
    	this.getFormHM().put("refer_id11", this.getRefer_id11());
    	this.getFormHM().put("print_id11", this.getPrint_id11());
    	this.getFormHM().put("valid21", this.getValid21());
    	this.getFormHM().put("current_end21", this.getCurrent_end21());
    	this.getFormHM().put("last_start21", this.getLast_start21());
    	this.getFormHM().put("refer_id21", this.getRefer_id21());
    	this.getFormHM().put("print_id21", this.getPrint_id21());
    	
    	this.getFormHM().put("valid0", this.getValid0());
    	this.getFormHM().put("current_date", this.getCurrent_date());
    	this.getFormHM().put("time", this.getTime());
    	this.getFormHM().put("limit_HH", this.getLimit_HH());
    	this.getFormHM().put("limit_MM", this.getLimit_MM());
    	this.getFormHM().put("refer_id0", this.getRefer_id0());
    	this.getFormHM().put("print_id0", this.getPrint_id0());
    	
    	this.getFormHM().put("sp_relation", this.getSp_relation());
    	this.getFormHM().put("sp_relationList", this.getSp_relationList());   
    	this.getFormHM().put("sp_level", this.getSp_level());
    	this.getFormHM().put("sp_levelList", this.getSp_levelList());   	
    	this.getFormHM().put("record_grade", this.getRecord_grade());
    	this.getFormHM().put("defaultLines", this.getDefaultLines());
    	this.getFormHM().put("dailyPlan_attachment", this.getDailyPlan_attachment());
    	this.getFormHM().put("dailySumm_attachment", this.getDailySumm_attachment());
    	this.getFormHM().put("planTarget", this.getPlanTarget());
    	this.getFormHM().put("plantargetList", this.getPlantargetList());
    	this.getFormHM().put("summTarget", this.getSummTarget());
    	this.getFormHM().put("summtargetList", this.getSummtargetList());
    	this.getFormHM().put("nbase", this.getNbase());
    	this.getFormHM().put("dbnameList", this.getDbnameList());
    	this.getFormHM().put("personSheetList", this.getPersonSheetList());
    }
    
    @Override
    public void outPutFormHM()
    {
    	
    	this.setValid14((String)this.getFormHM().get("valid14"));
    	this.setPrior_end14((String)this.getFormHM().get("prior_end14"));
    	this.setCurrent_start14((String)this.getFormHM().get("current_start14"));
    	this.setRefer_id14((String)this.getFormHM().get("refer_id14"));
    	this.setPrint_id14((String)this.getFormHM().get("print_id14"));
    	this.setValid24((String)this.getFormHM().get("valid24"));
    	this.setCurrent_end24((String)this.getFormHM().get("current_end24"));
    	this.setLast_start24((String)this.getFormHM().get("last_start24"));
    	this.setRefer_id24((String)this.getFormHM().get("refer_id24"));
    	this.setPrint_id24((String)this.getFormHM().get("print_id24"));
    	
    	this.setValid13((String)this.getFormHM().get("valid13"));
    	this.setPrior_end13((String)this.getFormHM().get("prior_end13"));
    	this.setCurrent_start13((String)this.getFormHM().get("current_start13"));
    	this.setPeriod13((String)this.getFormHM().get("period13"));
    	this.setPeriod131((String)this.getFormHM().get("period131"));
    	this.setPeriod132((String)this.getFormHM().get("period132"));
    	this.setPeriod133((String)this.getFormHM().get("period133"));
    	this.setPeriod134((String)this.getFormHM().get("period134"));
    	this.setRefer_id13((String)this.getFormHM().get("refer_id13"));
    	this.setPrint_id13((String)this.getFormHM().get("print_id13"));
    	this.setValid23((String)this.getFormHM().get("valid23"));
    	this.setCurrent_end23((String)this.getFormHM().get("current_end23"));
    	this.setLast_start23((String)this.getFormHM().get("last_start23"));
    	this.setPeriod23((String)this.getFormHM().get("period23"));
    	this.setPeriod231((String)this.getFormHM().get("period231"));
    	this.setPeriod232((String)this.getFormHM().get("period232"));
    	this.setPeriod233((String)this.getFormHM().get("period233"));
    	this.setPeriod234((String)this.getFormHM().get("period234"));
    	this.setRefer_id23((String)this.getFormHM().get("refer_id23"));
    	this.setPrint_id23((String)this.getFormHM().get("print_id23"));
    	
    	this.setValid12((String)this.getFormHM().get("valid12"));
    	this.setPrior_end12((String)this.getFormHM().get("prior_end12"));
    	this.setCurrent_start12((String)this.getFormHM().get("current_start12"));
    	this.setPeriod12((String)this.getFormHM().get("period12"));
    	this.setPeriod121((String)this.getFormHM().get("period121"));
    	this.setPeriod122((String)this.getFormHM().get("period122"));
    	this.setPeriod123((String)this.getFormHM().get("period123"));
    	this.setPeriod124((String)this.getFormHM().get("period124"));
    	this.setPeriod125((String)this.getFormHM().get("period125"));
    	this.setPeriod126((String)this.getFormHM().get("period126"));
    	this.setPeriod127((String)this.getFormHM().get("period127"));
    	this.setPeriod128((String)this.getFormHM().get("period128"));
    	this.setPeriod129((String)this.getFormHM().get("period129"));
    	this.setPeriod1210((String)this.getFormHM().get("period1210"));
    	this.setPeriod1211((String)this.getFormHM().get("period1211"));
    	this.setPeriod1212((String)this.getFormHM().get("period1212"));
    	this.setRefer_id12((String)this.getFormHM().get("refer_id12"));
    	this.setPrint_id12((String)this.getFormHM().get("print_id12"));
    	this.setValid22((String)this.getFormHM().get("valid22"));
    	this.setCurrent_end22((String)this.getFormHM().get("current_end22"));
    	this.setLast_start22((String)this.getFormHM().get("last_start22"));
    	this.setPeriod22((String)this.getFormHM().get("period22"));
    	this.setPeriod221((String)this.getFormHM().get("period221"));
    	this.setPeriod222((String)this.getFormHM().get("period222"));
    	this.setPeriod223((String)this.getFormHM().get("period223"));
    	this.setPeriod224((String)this.getFormHM().get("period224"));
    	this.setPeriod225((String)this.getFormHM().get("period225"));
    	this.setPeriod226((String)this.getFormHM().get("period226"));
    	this.setPeriod227((String)this.getFormHM().get("period227"));
    	this.setPeriod228((String)this.getFormHM().get("period228"));
    	this.setPeriod229((String)this.getFormHM().get("period229"));
    	this.setPeriod2210((String)this.getFormHM().get("period2210"));
    	this.setPeriod2211((String)this.getFormHM().get("period2211"));
    	this.setPeriod2212((String)this.getFormHM().get("period2212"));
    	this.setRefer_id22((String)this.getFormHM().get("refer_id22"));
    	this.setPrint_id22((String)this.getFormHM().get("print_id22"));
    	
    	this.setValid11((String)this.getFormHM().get("valid11"));
    	this.setPrior_end11((String)this.getFormHM().get("prior_end11"));
    	this.setCurrent_start11((String)this.getFormHM().get("current_start11"));
    	this.setRefer_id11((String)this.getFormHM().get("refer_id11"));
    	this.setPrint_id11((String)this.getFormHM().get("print_id11"));
    	this.setValid21((String)this.getFormHM().get("valid21"));
    	this.setCurrent_end21((String)this.getFormHM().get("current_end21"));
    	this.setLast_start21((String)this.getFormHM().get("last_start21"));
    	this.setRefer_id21((String)this.getFormHM().get("refer_id21"));
    	this.setPrint_id21((String)this.getFormHM().get("print_id21"));
    	
    	this.setValid0((String)this.getFormHM().get("valid0"));
    	this.setCurrent_date((String)this.getFormHM().get("current_date"));
    	this.setTime((String)this.getFormHM().get("time"));
    	this.setLimit_HH((String)this.getFormHM().get("limit_HH"));
    	this.setLimit_MM((String)this.getFormHM().get("limit_MM"));
    	this.setRefer_id0((String)this.getFormHM().get("refer_id0"));
    	this.setPrint_id0((String)this.getFormHM().get("print_id0"));
    	
    	this.setSp_relation((String)this.getFormHM().get("sp_relation"));
    	this.setSp_relationList((ArrayList)this.getFormHM().get("sp_relationList"));
    	this.setSp_level((String)this.getFormHM().get("sp_level"));
    	this.setSp_levelList((ArrayList)this.getFormHM().get("sp_levelList"));
    	this.setRecord_grade((String)this.getFormHM().get("record_grade"));
    	this.setDefaultLines((String)this.getFormHM().get("defaultLines"));
    	this.setDailyPlan_attachment((String)this.getFormHM().get("dailyPlan_attachment"));
    	this.setDailySumm_attachment((String)this.getFormHM().get("dailySumm_attachment"));
    	this.setPlanTarget((String)this.getFormHM().get("planTarget"));
    	this.setPlantargetList((ArrayList)this.getFormHM().get("plantargetList"));
    	this.setSummTarget((String)this.getFormHM().get("summTarget"));
    	this.setSummtargetList((ArrayList)this.getFormHM().get("summtargetList"));
    	this.setNbase((String)this.getFormHM().get("nbase"));
    	this.setDbnameList((ArrayList)this.getFormHM().get("dbnameList"));
    	this.setPersonSheetList((ArrayList)this.getFormHM().get("personSheetList"));
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {   			
    //	if ((arg0.getPath().equals("/performance/workplan/configParameter") && arg1.getParameter("b_query") != null && arg1.getParameter("b_query").equals("link")))
  	    {	
    		super.reset(arg0, arg1);
    		this.setValid14("0");
    		this.setValid24("0");    		
    		this.setValid13("0");
    		this.setPeriod131("0");
    		this.setPeriod132("0");
    		this.setPeriod133("0");
    		this.setPeriod134("0");
    		this.setValid23("0");
    		this.setPeriod231("0");
    		this.setPeriod232("0");
    		this.setPeriod233("0");
    		this.setPeriod234("0");
    		this.setValid12("0");
    		this.setPeriod121("0");
    		this.setPeriod122("0");
    		this.setPeriod123("0");
    		this.setPeriod124("0");
    		this.setPeriod125("0");
    		this.setPeriod126("0");
    		this.setPeriod127("0");
    		this.setPeriod128("0");
    		this.setPeriod129("0");
    		this.setPeriod1210("0");
    		this.setPeriod1211("0");
    		this.setPeriod1212("0");
    		this.setValid22("0");
    		this.setPeriod221("0");
    		this.setPeriod222("0");
    		this.setPeriod223("0");
    		this.setPeriod224("0");
    		this.setPeriod225("0");
    		this.setPeriod226("0");
    		this.setPeriod227("0");
    		this.setPeriod228("0");
    		this.setPeriod229("0");
    		this.setPeriod2210("0");
    		this.setPeriod2211("0");
    		this.setPeriod2212("0");
    		this.setValid11("0");
    		this.setValid21("0");
    		this.setValid0("0");   		
    		this.setRecord_grade("0");  
    		this.setDailyPlan_attachment("0");
    		this.setDailySumm_attachment("0");
  	    }	
    }
    
	public String getSp_relation() {
		return sp_relation;
	}

	public void setSp_relation(String sp_relation) {
		this.sp_relation = sp_relation;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getValid14() {
		return valid14;
	}

	public void setValid14(String valid14) {
		this.valid14 = valid14;
	}

	public String getPrior_end14() {
		return prior_end14;
	}

	public void setPrior_end14(String prior_end14) {
		this.prior_end14 = prior_end14;
	}

	public String getCurrent_start14() {
		return current_start14;
	}

	public void setCurrent_start14(String current_start14) {
		this.current_start14 = current_start14;
	}

	public String getRefer_id14() {
		return refer_id14;
	}

	public void setRefer_id14(String refer_id14) {
		this.refer_id14 = refer_id14;
	}

	public String getPrint_id14() {
		return print_id14;
	}

	public void setPrint_id14(String print_id14) {
		this.print_id14 = print_id14;
	}

	public String getValid24() {
		return valid24;
	}

	public void setValid24(String valid24) {
		this.valid24 = valid24;
	}

	public String getCurrent_end24() {
		return current_end24;
	}

	public void setCurrent_end24(String current_end24) {
		this.current_end24 = current_end24;
	}

	public String getLast_start24() {
		return last_start24;
	}

	public void setLast_start24(String last_start24) {
		this.last_start24 = last_start24;
	}

	public String getRefer_id24() {
		return refer_id24;
	}

	public void setRefer_id24(String refer_id24) {
		this.refer_id24 = refer_id24;
	}

	public String getPrint_id24() {
		return print_id24;
	}

	public void setPrint_id24(String print_id24) {
		this.print_id24 = print_id24;
	}

	public String getValid13() {
		return valid13;
	}

	public void setValid13(String valid13) {
		this.valid13 = valid13;
	}

	public String getPrior_end13() {
		return prior_end13;
	}

	public void setPrior_end13(String prior_end13) {
		this.prior_end13 = prior_end13;
	}

	public String getCurrent_start13() {
		return current_start13;
	}

	public void setCurrent_start13(String current_start13) {
		this.current_start13 = current_start13;
	}

	public String getPeriod13() {
		return period13;
	}

	public void setPeriod13(String period13) {
		this.period13 = period13;
	}

	public String getRefer_id13() {
		return refer_id13;
	}

	public void setRefer_id13(String refer_id13) {
		this.refer_id13 = refer_id13;
	}

	public String getPrint_id13() {
		return print_id13;
	}

	public void setPrint_id13(String print_id13) {
		this.print_id13 = print_id13;
	}

	public String getValid23() {
		return valid23;
	}

	public void setValid23(String valid23) {
		this.valid23 = valid23;
	}

	public String getCurrent_end23() {
		return current_end23;
	}

	public void setCurrent_end23(String current_end23) {
		this.current_end23 = current_end23;
	}

	public String getLast_start23() {
		return last_start23;
	}

	public void setLast_start23(String last_start23) {
		this.last_start23 = last_start23;
	}

	public String getPeriod23() {
		return period23;
	}

	public void setPeriod23(String period23) {
		this.period23 = period23;
	}

	public String getRefer_id23() {
		return refer_id23;
	}

	public void setRefer_id23(String refer_id23) {
		this.refer_id23 = refer_id23;
	}

	public String getPrint_id23() {
		return print_id23;
	}

	public void setPrint_id23(String print_id23) {
		this.print_id23 = print_id23;
	}

	public String getValid12() {
		return valid12;
	}

	public void setValid12(String valid12) {
		this.valid12 = valid12;
	}

	public String getPrior_end12() {
		return prior_end12;
	}

	public void setPrior_end12(String prior_end12) {
		this.prior_end12 = prior_end12;
	}

	public String getCurrent_start12() {
		return current_start12;
	}

	public void setCurrent_start12(String current_start12) {
		this.current_start12 = current_start12;
	}

	public String getPeriod12() {
		return period12;
	}

	public void setPeriod12(String period12) {
		this.period12 = period12;
	}

	public String getRefer_id12() {
		return refer_id12;
	}

	public void setRefer_id12(String refer_id12) {
		this.refer_id12 = refer_id12;
	}

	public String getPrint_id12() {
		return print_id12;
	}

	public void setPrint_id12(String print_id12) {
		this.print_id12 = print_id12;
	}

	public String getValid22() {
		return valid22;
	}

	public void setValid22(String valid22) {
		this.valid22 = valid22;
	}

	public String getCurrent_end22() {
		return current_end22;
	}

	public void setCurrent_end22(String current_end22) {
		this.current_end22 = current_end22;
	}

	public String getLast_start22() {
		return last_start22;
	}

	public void setLast_start22(String last_start22) {
		this.last_start22 = last_start22;
	}

	public String getPeriod22() {
		return period22;
	}

	public void setPeriod22(String period22) {
		this.period22 = period22;
	}

	public String getRefer_id22() {
		return refer_id22;
	}

	public void setRefer_id22(String refer_id22) {
		this.refer_id22 = refer_id22;
	}

	public String getPrint_id22() {
		return print_id22;
	}

	public void setPrint_id22(String print_id22) {
		this.print_id22 = print_id22;
	}

	public String getValid11() {
		return valid11;
	}

	public void setValid11(String valid11) {
		this.valid11 = valid11;
	}

	public String getPrior_end11() {
		return prior_end11;
	}

	public void setPrior_end11(String prior_end11) {
		this.prior_end11 = prior_end11;
	}

	public String getCurrent_start11() {
		return current_start11;
	}

	public void setCurrent_start11(String current_start11) {
		this.current_start11 = current_start11;
	}

	public String getRefer_id11() {
		return refer_id11;
	}

	public void setRefer_id11(String refer_id11) {
		this.refer_id11 = refer_id11;
	}

	public String getPrint_id11() {
		return print_id11;
	}

	public void setPrint_id11(String print_id11) {
		this.print_id11 = print_id11;
	}

	public String getValid21() {
		return valid21;
	}

	public void setValid21(String valid21) {
		this.valid21 = valid21;
	}

	public String getCurrent_end21() {
		return current_end21;
	}

	public void setCurrent_end21(String current_end21) {
		this.current_end21 = current_end21;
	}

	public String getLast_start21() {
		return last_start21;
	}

	public void setLast_start21(String last_start21) {
		this.last_start21 = last_start21;
	}

	public String getRefer_id21() {
		return refer_id21;
	}

	public void setRefer_id21(String refer_id21) {
		this.refer_id21 = refer_id21;
	}

	public String getPrint_id21() {
		return print_id21;
	}

	public void setPrint_id21(String print_id21) {
		this.print_id21 = print_id21;
	}

	public String getValid0() {
		return valid0;
	}

	public void setValid0(String valid0) {
		this.valid0 = valid0;
	}

	public String getCurrent_date() {
		return current_date;
	}

	public void setCurrent_date(String current_date) {
		this.current_date = current_date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRefer_id0() {
		return refer_id0;
	}

	public void setRefer_id0(String refer_id0) {
		this.refer_id0 = refer_id0;
	}

	public String getPrint_id0() {
		return print_id0;
	}

	public void setPrint_id0(String print_id0) {
		this.print_id0 = print_id0;
	}

	public String getLimit_HH() {
		return limit_HH;
	}

	public void setLimit_HH(String limit_HH) {
		this.limit_HH = limit_HH;
	}

	public String getLimit_MM() {
		return limit_MM;
	}

	public void setLimit_MM(String limit_MM) {
		this.limit_MM = limit_MM;
	}

	public String getPeriod131() {
		return period131;
	}

	public void setPeriod131(String period131) {
		this.period131 = period131;
	}

	public String getPeriod132() {
		return period132;
	}

	public void setPeriod132(String period132) {
		this.period132 = period132;
	}

	public String getPeriod133() {
		return period133;
	}

	public void setPeriod133(String period133) {
		this.period133 = period133;
	}

	public String getPeriod134() {
		return period134;
	}

	public void setPeriod134(String period134) {
		this.period134 = period134;
	}

	public String getPeriod231() {
		return period231;
	}

	public void setPeriod231(String period231) {
		this.period231 = period231;
	}

	public String getPeriod232() {
		return period232;
	}

	public void setPeriod232(String period232) {
		this.period232 = period232;
	}

	public String getPeriod233() {
		return period233;
	}

	public void setPeriod233(String period233) {
		this.period233 = period233;
	}

	public String getPeriod234() {
		return period234;
	}

	public void setPeriod234(String period234) {
		this.period234 = period234;
	}

	public String getPeriod121() {
		return period121;
	}

	public void setPeriod121(String period121) {
		this.period121 = period121;
	}

	public String getPeriod122() {
		return period122;
	}

	public void setPeriod122(String period122) {
		this.period122 = period122;
	}

	public String getPeriod123() {
		return period123;
	}

	public void setPeriod123(String period123) {
		this.period123 = period123;
	}

	public String getPeriod124() {
		return period124;
	}

	public void setPeriod124(String period124) {
		this.period124 = period124;
	}

	public String getPeriod125() {
		return period125;
	}

	public void setPeriod125(String period125) {
		this.period125 = period125;
	}

	public String getPeriod126() {
		return period126;
	}

	public void setPeriod126(String period126) {
		this.period126 = period126;
	}

	public String getPeriod127() {
		return period127;
	}

	public void setPeriod127(String period127) {
		this.period127 = period127;
	}

	public String getPeriod128() {
		return period128;
	}

	public void setPeriod128(String period128) {
		this.period128 = period128;
	}

	public String getPeriod129() {
		return period129;
	}

	public void setPeriod129(String period129) {
		this.period129 = period129;
	}

	public String getPeriod1210() {
		return period1210;
	}

	public void setPeriod1210(String period1210) {
		this.period1210 = period1210;
	}

	public String getPeriod1211() {
		return period1211;
	}

	public void setPeriod1211(String period1211) {
		this.period1211 = period1211;
	}

	public String getPeriod1212() {
		return period1212;
	}

	public void setPeriod1212(String period1212) {
		this.period1212 = period1212;
	}

	public String getPeriod221() {
		return period221;
	}

	public void setPeriod221(String period221) {
		this.period221 = period221;
	}

	public String getPeriod222() {
		return period222;
	}

	public void setPeriod222(String period222) {
		this.period222 = period222;
	}

	public String getPeriod223() {
		return period223;
	}

	public void setPeriod223(String period223) {
		this.period223 = period223;
	}

	public String getPeriod224() {
		return period224;
	}

	public void setPeriod224(String period224) {
		this.period224 = period224;
	}

	public String getPeriod225() {
		return period225;
	}

	public void setPeriod225(String period225) {
		this.period225 = period225;
	}

	public String getPeriod226() {
		return period226;
	}

	public void setPeriod226(String period226) {
		this.period226 = period226;
	}

	public String getPeriod227() {
		return period227;
	}

	public void setPeriod227(String period227) {
		this.period227 = period227;
	}

	public String getPeriod228() {
		return period228;
	}

	public void setPeriod228(String period228) {
		this.period228 = period228;
	}

	public String getPeriod229() {
		return period229;
	}

	public void setPeriod229(String period229) {
		this.period229 = period229;
	}

	public String getPeriod2210() {
		return period2210;
	}

	public void setPeriod2210(String period2210) {
		this.period2210 = period2210;
	}

	public String getPeriod2211() {
		return period2211;
	}

	public void setPeriod2211(String period2211) {
		this.period2211 = period2211;
	}

	public String getPeriod2212() {
		return period2212;
	}

	public void setPeriod2212(String period2212) {
		this.period2212 = period2212;
	}

	public ArrayList getPersonSheetList() {
		return personSheetList;
	}

	public void setPersonSheetList(ArrayList personSheetList) {
		this.personSheetList = personSheetList;
	}

	public ArrayList getSp_relationList() {
		return sp_relationList;
	}

	public void setSp_relationList(ArrayList sp_relationList) {
		this.sp_relationList = sp_relationList;
	}

	public ArrayList getDbnameList() {
		return dbnameList;
	}

	public void setDbnameList(ArrayList dbnameList) {
		this.dbnameList = dbnameList;
	}

	public String getSp_level() {
		return sp_level;
	}

	public void setSp_level(String sp_level) {
		this.sp_level = sp_level;
	}

	public ArrayList getSp_levelList() {
		return sp_levelList;
	}

	public void setSp_levelList(ArrayList sp_levelList) {
		this.sp_levelList = sp_levelList;
	}

	public String getRecord_grade() {
		return record_grade;
	}

	public void setRecord_grade(String record_grade) {
		this.record_grade = record_grade;
	}

	public String getDailyPlan_attachment() {
		return dailyPlan_attachment;
	}

	public void setDailyPlan_attachment(String dailyPlan_attachment) {
		this.dailyPlan_attachment = dailyPlan_attachment;
	}

	public String getDailySumm_attachment() {
		return dailySumm_attachment;
	}

	public void setDailySumm_attachment(String dailySumm_attachment) {
		this.dailySumm_attachment = dailySumm_attachment;
	}	

	public String getPlanTarget() {
		return planTarget;
	}

	public void setPlanTarget(String planTarget) {
		this.planTarget = planTarget;
	}

	public String getSummTarget() {
		return summTarget;
	}

	public void setSummTarget(String summTarget) {
		this.summTarget = summTarget;
	}

	public ArrayList getPlantargetList() {
		return plantargetList;
	}

	public void setPlantargetList(ArrayList plantargetList) {
		this.plantargetList = plantargetList;
	}

	public ArrayList getSummtargetList() {
		return summtargetList;
	}

	public void setSummtargetList(ArrayList summtargetList) {
		this.summtargetList = summtargetList;
	}

	public String getDefaultLines() {
		return defaultLines;
	}

	public void setDefaultLines(String defaultLines) {
		this.defaultLines = defaultLines;
	}
	   
}