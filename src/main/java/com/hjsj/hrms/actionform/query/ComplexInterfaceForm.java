package com.hjsj.hrms.actionform.query;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ComplexInterfaceForm  extends FrameForm {
	private ArrayList complexList=new ArrayList();
	private String setid="";
	private String fieldItems="";
	private ArrayList setlist=new ArrayList();
	private ArrayList itemlist=new ArrayList();
	private String complex_expr="";
	private String complex_id="";
	private String codeItemId="";
	private String fieldItemId="";	
	private String complex_name="";
	private String strsql="";
	private String columns="";
	private String dbpre="";
	private String tabid="";
	private ArrayList dblist=new ArrayList();
	private ArrayList compledblist=new ArrayList();
	private String comple_db="";
	private String fieldstr="";
	private String photo_other_view;
	private String photolength="";
	private String order="";
	private ArrayList browsefields=new ArrayList();
	private String uplevel="0";
	/**在高级花名册中增加了复杂查询，不显示查询后的结果页面，用此参数以作区别=1为从高级花名册进入， lizw 2012-02-22*/
	private String fromFlag;
	
	private String userbase="";
    private HashMap part_map=new HashMap();
    
    private String multimedia_file_flag;//信息集是否显示附件
	
	public String getMultimedia_file_flag() {
		return multimedia_file_flag;
	}
	public void setMultimedia_file_flag(String multimedia_file_flag) {
		this.multimedia_file_flag = multimedia_file_flag;
	}
    
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getFieldstr() {
		return fieldstr;
	}
	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}
	public ArrayList getBrowsefields() {
		return browsefields;
	}
	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}
	public ArrayList getCompledblist() {
		return compledblist;
	}
	public void setCompledblist(ArrayList compledblist) {
		this.compledblist = compledblist;
	}
	public String getComple_db() {
		return comple_db;
	}
	public void setComple_db(String comple_db) {
		this.comple_db = comple_db;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public String getDbpre() {
		return dbpre;
	}
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getComplex_name() {
		return complex_name;
	}
	public void setComplex_name(String complex_name) {
		this.complex_name = complex_name;
	}
	public String getCodeItemId() {
		return codeItemId;
	}
	public void setCodeItemId(String codeItemId) {
		this.codeItemId = codeItemId;
	}
	public String getFieldItemId() {
		return fieldItemId;
	}
	public void setFieldItemId(String fieldItemId) {
		this.fieldItemId = fieldItemId;
	}
	public String getComplex_expr() {
		return complex_expr;
	}
	public void setComplex_expr(String complex_expr) {
		this.complex_expr = complex_expr;
	}
	public String getComplex_id() {
		return complex_id;
	}
	public void setComplex_id(String complex_id) {
		this.complex_id = complex_id;
	}
	public ArrayList getComplexList() {
		return complexList;
	}
	public void setComplexList(ArrayList complexList) {
		this.complexList = complexList;
	}
	public String getFieldItems() {
		return fieldItems;
	}
	public void setFieldItems(String fieldItems) {
		this.fieldItems = fieldItems;
	}
	public ArrayList getItemlist() {
		return itemlist;
	}
	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getSetid() {
		return setid;
	}
	public void setSetid(String setid) {
		this.setid = setid;
	}
	public ArrayList getSetlist() {
		return setlist;
	}
	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}
	@Override
    public void outPutFormHM() {
		this.setFromFlag((String)this.getFormHM().get("fromFlag"));
		this.setComplexList((ArrayList)this.getFormHM().get("complexList"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setComplex_expr((String)this.getFormHM().get("complex_expr"));
		this.setFieldItems((String)this.getFormHM().get("fieldItems"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setComplex_id((String)this.getFormHM().get("complex_id"));
		this.setCodeItemId((String)this.getFormHM().get("codeItemId"));
		this.setFieldItemId((String)this.getFormHM().get("fieldItemId"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setCompledblist((ArrayList)this.getFormHM().get("compledblist"));
		this.setComple_db((String)this.getFormHM().get("comple_db"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
		this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
		String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	     this.setPhotolength(photolength);
		this.setOrder((String)this.getFormHM().get("order"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		
		this.setPart_map((HashMap)this.getFormHM().get("part_map"));
        this.setUserbase((String)this.getFormHM().get("userbase"));
        this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("fromFlag", this.getFromFlag());
		this.getFormHM().put("complex_expr", this.getComplex_expr());
		this.getFormHM().put("complex_id", this.getComplex_id());
		this.getFormHM().put("complexList", this.getComplexList());
		this.getFormHM().put("complex_name", this.getComplex_name());
		this.getFormHM().put("dbpre", this.getDbpre());
		this.getFormHM().put("comple_db", this.comple_db);
		this.getFormHM().put("fieldstr", this.getFieldstr());
		
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/workbench/query/complex_interface".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	    	String currentpage = arg1.getParameter("currentpage");
	    	if(currentpage==null||!"yes".equals(currentpage))
	    		if(this.getPagination()!=null)
	    			this.getPagination().firstPage();//?
	    }
	    if("/workbench/query/complex_interface_pho".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        //if(this.getPagination()!=null)
	       //   this.getPagination().firstPage();//?
	    }
	    if("/workbench/query/complex_interface".equals(arg0.getPath())&&arg1.getParameter("b_gquery")!=null)
	    {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();
	            this.setComplex_id("");
	            this.getFormHM().put("complex_id", "");
	    }
	    if("/workbench/query/complex_interface".equals(arg0.getPath())&&arg1.getParameter("b_qsearch")!=null)
	    {
	           this.getFormHM().put("dbpre", "");
	    }
	    return super.validate(arg0, arg1);
	}
	public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getFromFlag() {
		return fromFlag;
	}
	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
}
