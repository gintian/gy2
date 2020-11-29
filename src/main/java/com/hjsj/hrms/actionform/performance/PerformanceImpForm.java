package com.hjsj.hrms.actionform.performance;


import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PerformanceImpForm extends FrameForm {
	
	
	
	/**当前页*/
	private String plan_id="0";  //绩效计划 id;
    
    private RecordVo exam_vo=new RecordVo("per_object");
   
    private String sql_str="select id,B0110,E0122,E01A1,A0101,object_id";
    private String where_str="";
    
    private String sql_str2="select id,b0110,e0122,e01a1,a0101,name "; //查询考核对象相应的主体sql的前置语句
    private String where_str2="";   //查询考核对象相应的主体的条件语句
    //考核计划id
    private String dbpre="";
    
    //主体类别id
    private String mainBodyID="";
    private String objectID="";
    private ArrayList objectIDList=new ArrayList();
    private String objectType="2";    //考核类型 1:部门  2：人员
    //主体类别集合
    private ArrayList mainBodySortList=new ArrayList();
    
    //考核计划集合
    private ArrayList dblist=new ArrayList();
	
    //考核计划状态
    private String status="";
    private String managerstr=""; //组织权限范围
    
   
    private ArrayList perPointPrivList=new ArrayList();  //某考核计划对象相应主体指标权限值集合
    
    
    
    private String   flag="1";  // 1: 保存考核对象(手工选人)  2:保存考核主体(手工选人)   3:批量保存考核主体
    private String[] right_fields;  //进行手工选人时的已选用户列表
 
    
    
    private String plan_b0110 = "HJSJ";
    
    private String planMthod = "1";
    
    private String templateId = "";
	private String a_code="";
	private String khobjname="";//考核对象名称
	private String busitype = "0";//0:绩效 1：能力素质  对于能力素质的计划，不显示“指标权限”这一列  郭峰
	public PerformanceImpForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
    public void outPutFormHM() {
		this.setA_code((String)this.getFormHM().get("a_code"));
		if(this.getFormHM().get("mainBodyID")!=null)
			this.setMainBodyID((String)this.getFormHM().get("mainBodyID"));
		if(this.getFormHM().get("dbpre")!=null)
			this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
		this.setWhere_str2((String)this.getFormHM().get("where_str2"));
		//得到考核计划的集合类
		this.setDblist((ArrayList)this.getFormHM().get("Dblist"));
		//得到相关考核计划的主题类别集合
		this.setMainBodySortList((ArrayList)this.getFormHM().get("mainBodySortList"));
		
		this.setStatus((String)this.getFormHM().get("status"));
		
		//得到考核计划某对象所有考核主体的指标权限数据
	
		this.setPerPointPrivList((ArrayList)this.getFormHM().get("perPointPrivList"));
		this.setObjectID((String)this.getFormHM().get("objectID"));
		this.setManagerstr((String)this.getFormHM().get("managerstr"));
		this.setObjectType((String)this.getFormHM().get("objectType"));
		if(this.getPagination()!=null)
			this.getPagination().setCurrent(1);
		this.setPlan_b0110((String)this.getFormHM().get("plan_b0110"));
		this.setPlanMthod((String)this.getFormHM().get("planMthod"));
		this.setTemplateId((String)this.getFormHM().get("templateId"));
		this.setKhobjname((String)this.getFormHM().get("khobjname"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
	}

	@Override
    public void inPutTransHM() {
			
		if(this.getPagination()!=null)  //2013.11.28 pjf
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("mainBodyID",this.getMainBodyID());	
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("plan_b0110",this.getPlan_b0110());
		this.getFormHM().put("planMthod",this.getPlanMthod());
		this.getFormHM().put("templateId",this.getTemplateId());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("khobjname",this.getKhobjname());
		this.getFormHM().put("busitype", this.getBusitype());
	}

	

	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
	   
	        if(("/selfservice/performance/performanceImplement".equals(arg0.getPath()) &&
	        		arg1.getParameter("b_query")!=null) || ("/selfservice/performance/performanceMainBodyImplement".equals(arg0.getPath()) &&
		        		arg1.getParameter("b_query")!=null))
	        {
	        	
	        	if(this.getPagination()!=null)
	        	    this.getPagination().firstPage();
//	        	  this.getPagination().setCurrent(this.getPagination().getPageCount());
	        
	        }
	 
       return super.validate(arg0, arg1);
	 }
	
	
	
	
	
	

	public RecordVo getExam_vo() {
		return exam_vo;
	}

	public void setExam_vo(RecordVo exam_vo) {
		this.exam_vo = exam_vo;
	}

	

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getWhere_str() {
		return where_str;
	}

	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMainBodyID() {
		return mainBodyID;
	}

	public void setMainBodyID(String mainBodyID) {
		this.mainBodyID = mainBodyID;
	}

	public ArrayList getMainBodySortList() {
		return mainBodySortList;
	}

	public void setMainBodySortList(ArrayList mainBodySortList) {
		this.mainBodySortList = mainBodySortList;
	}

	public String getSql_str2() {
		return sql_str2;
	}

	public void setSql_str2(String sql_str2) {
		this.sql_str2 = sql_str2;
	}

	public String getWhere_str2() {
		return where_str2;
	}

	public void setWhere_str2(String where_str2) {
		this.where_str2 = where_str2;
	}


	public ArrayList getPerPointPrivList() {
		return perPointPrivList;
	}

	public void setPerPointPrivList(ArrayList perPointPrivList) {
		this.perPointPrivList = perPointPrivList;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getManagerstr() {
		return managerstr;
	}

	public void setManagerstr(String managerstr) {
		this.managerstr = managerstr;
	}

	public ArrayList getObjectIDList() {
		return objectIDList;
	}

	public void setObjectIDList(ArrayList objectIDList) {
		this.objectIDList = objectIDList;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getPlan_b0110()
	{
	
	    return plan_b0110;
	}

	public void setPlan_b0110(String plan_b0110)
	{
	
	    this.plan_b0110 = plan_b0110;
	}

	public String getPlanMthod()
	{
	
	    return planMthod;
	}

	public void setPlanMthod(String planMthod)
	{
	
	    this.planMthod = planMthod;
	}

	public String getTemplateId()
	{
	
	    return templateId;
	}

	public void setTemplateId(String templateId)
	{
	
	    this.templateId = templateId;
	}

	public String getA_code()
	{
		return a_code;
	}

	public void setA_code(String a_code)
	{
		this.a_code = a_code;
	}

	public String getKhobjname()
	{
		return khobjname;
	}

	public void setKhobjname(String khobjname)
	{
		this.khobjname = khobjname;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
	
}
