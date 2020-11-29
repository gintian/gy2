package com.hjsj.hrms.actionform.performance.achivement;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:业绩任务书</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 8, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class AchievementTaskForm extends FrameForm 
{
	private ArrayList targetColumnList=new ArrayList();
	private ArrayList pointClassList=new ArrayList();  //指标分类列表
	private String   classid="";
	private String[]  right_fields=null;
	private ArrayList pointList=new ArrayList();
	private ArrayList selectedPointList=new ArrayList();
	private String    target_id="";
	private String    selectedIds="";
	private RecordVo  perTargetVo=null;
	private String    sql_whl="";
	private String    object_type="2";
	private String    number="";
	private String    codeitemdesc="";
	private String    orgCode="";
	private String    returnURL="";   //返回路径
	private String    target="";
	private String    obj_type="";
	private ArrayList orgLinks=new ArrayList();
	private String    hjsoft="";
	private int    zbnumber=0;
	private String    object_id="";
	
	private String    root_url="";   //树路径
	private ArrayList targetDataList=new ArrayList();
	private PaginationForm targetDataListform=new PaginationForm();
	
	private String tree_loadtype="0";  //0（单位|部门|职位）  =1 (单位|部门)
	private String tree_flag="1";  //1:加载人员信息  0:不加载
	private ArrayList cycleList=new ArrayList();
	private String cycle="";
	
	private ArrayList objectCycleList=new ArrayList();//单一考核对象的考核区间
	private ArrayList objectPointList=new ArrayList();//单一考核对象的考核指标	
	
	private String pointId="";
	private String point_value="";
	private String sql_whl2="";
	private FormFile file;
	private String okcount="";
	private String errorname="";
	private String targetDataListSql = ""; // 取得查询目标任务书数据的Sql语句

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		/*if(arg1.getParameter("onePage")!=null){
            *//**定位到首页,*//*
            if(this.getTargetDataListform().getPagination()!=null)
            	this.getTargetDataListform().getPagination().firstPage();
      
        }	*/
		return super.validate(arg0, arg1);
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
//		this.setOrgCode("");
    }
	
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("targetDataListSql",this.getTargetDataListSql());
		this.getFormHM().put("object_id",this.getObject_id());
		this.getFormHM().put("zbnumber",new Integer(this.getZbnumber()));
		this.getFormHM().put("hjsoft",this.getHjsoft());
		this.getFormHM().put("orgLinks",this.getOrgLinks());
		this.getFormHM().put("obj_type",this.getObj_type());
		this.getFormHM().put("orgCode",this.getOrgCode());
		this.getFormHM().put("codeitemdesc",this.getCodeitemdesc());
		this.getFormHM().put("number",this.getNumber());
		this.getFormHM().put("objectCycleList",this.getObjectCycleList());
		this.getFormHM().put("objectPointList", this.getObjectPointList());
		
		this.getFormHM().put("pointId",this.getPointId());
		this.getFormHM().put("point_value", this.getPoint_value());
		
		this.getFormHM().put("targetColumnList", this.getTargetColumnList());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("targetDataList",this.getTargetDataList());
		
		
		this.getFormHM().put("cycle",this.getCycle());
		this.getFormHM().put("sql_whl",this.getSql_whl());
		this.getFormHM().put("selectedIds",this.getSelectedIds());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("okcount", this.getOkcount());
		this.getFormHM().put("errorname", this.getErrorname());
	}

	@Override
    public void outPutFormHM()
	{
		
		this.setTargetDataListSql((String)this.getFormHM().get("targetDataListSql"));
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String savetask=(String)hm.get("b_saveTaskData");
		if(savetask==null||savetask.trim().length()==0){
			this.setSql_whl2((String)this.getFormHM().get("sql_whl"));
		}else{
			hm.remove("b_saveTaskData");
		}
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setZbnumber(((Integer)this.getFormHM().get("zbnumber")).intValue());
		this.setHjsoft((String)this.getFormHM().get("hjsoft"));
		this.setOrgLinks((ArrayList)this.getFormHM().get("orgLinks"));
		this.setObj_type((String)this.getFormHM().get("obj_type"));
		this.setOrgCode((String)this.getFormHM().get("orgCode"));
		this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
		this.setNumber((String)this.getFormHM().get("number"));
		this.setObjectCycleList((ArrayList)this.getFormHM().get("objectCycleList"));
		this.setObjectPointList((ArrayList)this.getFormHM().get("objectPointList"));
		this.setSql_whl("");
		this.setTree_loadtype((String)this.getFormHM().get("tree_loadtype"));
		this.setTree_flag((String)this.getFormHM().get("tree_flag"));
		this.setCycleList((ArrayList)this.getFormHM().get("cycleList"));
		this.setCycle((String)this.getFormHM().get("cycle"));
		this.setPerTargetVo((RecordVo)this.getFormHM().get("perTargetVo"));
		this.setTargetColumnList((ArrayList)this.getFormHM().get("targetColumnList"));
		this.setPointClassList((ArrayList)this.getFormHM().get("pointClassList"));
		this.setSelectedPointList((ArrayList)this.getFormHM().get("selectedPointList"));
		this.setTarget_id((String)this.getFormHM().get("target_id"));
		this.setTargetDataList((ArrayList)this.getFormHM().get("targetDataList"));
		this.getTargetDataListform().setList((ArrayList)this.getFormHM().get("targetDataList"));
		this.setRoot_url((String)this.getFormHM().get("root_url"));
		this.setPointList((ArrayList)this.getFormHM().get("pointList"));
		if(this.perTargetVo!=null&&this.perTargetVo.getString("object_type")!=null)
			this.setObject_type(this.perTargetVo.getString("object_type"));
		this.setOkcount((String)this.getFormHM().get("okcount"));
		this.setErrorname((String)this.getFormHM().get("errorname"));
		
	}
	
	public ArrayList getPointClassList() {
		return pointClassList;
	}
	public void setPointClassList(ArrayList pointClassList) {
		this.pointClassList = pointClassList;
	}
	public ArrayList getTargetColumnList() {
		return targetColumnList;
	}
	public void setTargetColumnList(ArrayList targetColumnList) {
		this.targetColumnList = targetColumnList;
	}
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	public ArrayList getSelectedPointList() {
		return selectedPointList;
	}
	public void setSelectedPointList(ArrayList selectedPointList) {
		this.selectedPointList = selectedPointList;
	}
	public ArrayList getTargetDataList() {
		return targetDataList;
	}
	public void setTargetDataList(ArrayList targetDataList) {
		this.targetDataList = targetDataList;
	}
	public String getTree_flag() {
		return tree_flag;
	}
	public void setTree_flag(String tree_flag) {
		this.tree_flag = tree_flag;
	}
	public String getTree_loadtype() {
		return tree_loadtype;
	}
	public void setTree_loadtype(String tree_loadtype) {
		this.tree_loadtype = tree_loadtype;
	}
	public ArrayList getCycleList() {
		return cycleList;
	}
	public void setCycleList(ArrayList cycleList) {
		this.cycleList = cycleList;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getSelectedIds() {
		return selectedIds;
	}
	public void setSelectedIds(String selectedIds) {
		this.selectedIds = selectedIds;
	}
	public String getRoot_url() {
		return root_url;
	}
	public void setRoot_url(String root_url) {
		this.root_url = root_url;
	}
	public ArrayList getPointList() {
		return pointList;
	}
	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}
	public RecordVo getPerTargetVo() {
		return perTargetVo;
	}
	public void setPerTargetVo(RecordVo perTargetVo) {
		this.perTargetVo = perTargetVo;
	}
	public String getSql_whl() {
		return sql_whl;
	}
	public void setSql_whl(String sql_whl) {
		this.sql_whl = sql_whl;
	}
	public PaginationForm getTargetDataListform() {
		return targetDataListform;
	}
	public void setTargetDataListform(PaginationForm targetDataListform) {
		this.targetDataListform = targetDataListform;
	}
	public String getObject_type() {
		return object_type;
	}
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	public String getPointId() {
		return pointId;
	}
	public void setPointId(String pointId) {
		this.pointId = pointId;
	}
	public String getPoint_value() {
		return point_value;
	}
	public void setPoint_value(String point_value) {
		this.point_value = point_value;
	}
	public String getSql_whl2() {
		return sql_whl2;
	}
	public void setSql_whl2(String sql_whl2) {
		this.sql_whl2 = sql_whl2;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getOkcount() {
		return okcount;
	}
	public void setOkcount(String okcount) {
		this.okcount = okcount;
	}
	public ArrayList getObjectPointList() {
		return objectPointList;
	}
	public void setObjectPointList(ArrayList objectPointList) {
		this.objectPointList = objectPointList;
	}
	public ArrayList getObjectCycleList() {
		return objectCycleList;
	}
	public void setObjectCycleList(ArrayList objectCycleList) {
		this.objectCycleList = objectCycleList;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getObj_type() {
		return obj_type;
	}
	public void setObj_type(String obj_type) {
		this.obj_type = obj_type;
	}		
	public ArrayList getOrgLinks() {
		return orgLinks;
	}
	public void setOrgLinks(ArrayList orgLinks) {
		this.orgLinks = orgLinks;
	}
	public String getHjsoft() {
		return hjsoft;
	}
	public void setHjsoft(String hjsoft) {
		this.hjsoft = hjsoft;
	}
	public int getZbnumber() {
		return zbnumber;
	}
	public void setZbnumber(int zbnumber) {
		this.zbnumber = zbnumber;
	}
	public String getObject_id() {
		return object_id;
	}
	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}
	public String getErrorname() {
		return errorname;
	}
	public void setErrorname(String errorname) {
		this.errorname = errorname;
	}
	public String getTargetDataListSql() {
		return targetDataListSql;
	}
	public void setTargetDataListSql(String targetDataListSql) {
		this.targetDataListSql = targetDataListSql;
	}
}
