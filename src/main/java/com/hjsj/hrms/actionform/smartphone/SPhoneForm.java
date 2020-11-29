/**
 * 
 */
package com.hjsj.hrms.actionform.smartphone;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cmq
 * Dec 24, 20103:10:04 PM
 */
public class SPhoneForm extends FrameForm {

	/**
	 * 模块号
	 */
	private String moduleid;
	/**
	 * 模块名称
	 */
	private String name;
	
    /**查询条件*/
    private String strsql;
    /**查询条件*/
    private String strwhere;
    /**显示的字段名称*/
    private String columns;   
    private String order;
    /**输入查询内容，姓名或姓名的拼音简码*/
    private String queryitem;
    
    private String nbase="";
    private String a0100="00000007";
    
    private String a_code;
    private String p_a_code;
    private String p_codeitemdesc;
    private String allcount;
    private String showstyle;
    private Map basicinfo_template=new HashMap();
    private Map setsMap;
    private String canQuery;
    private String selectField;
    private String queryValue;
    private PaginationForm sphoneForm=new PaginationForm();
    private String html;
    private String sortid;
    private String cardid;//登记表格号
    
    private String statid="";//统计id
    private String norder="";//统计项id
    private String snamedisplay="";//统计名称
    private ArrayList legendlist=new ArrayList();
    private LazyDynaBean nordercountbean=new LazyDynaBean();
    private ArrayList dblist=new ArrayList();
    private String charttype="1";//1柱状；2饼状；3：线状
    private String returnvalue="";
    private String categories="";//分类
    private String statlabel="";//层级分类
    private String resourcepriv;
	public String getCategories() {
		return categories;
	}


	public void setCategories(String categories) {
		this.categories = categories;
	}


	public ArrayList getDblist() {
		return dblist;
	}


	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}


	public String getCharttype() {
		return charttype;
	}


	public void setCharttype(String charttype) {
		this.charttype = charttype;
	}


	public String getQueryitem() {
		return queryitem;
	}


	public void setQueryitem(String queryitem) {
		this.queryitem = queryitem;
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


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("moduleid", this.getModuleid());
		this.getFormHM().put("queryitem", this.getQueryitem());
		this.getFormHM().put("a_code", a_code);
		this.getFormHM().put("basicinfo_template", basicinfo_template);
		this.userView.getHm().put("basicinfo_template", basicinfo_template);
		this.getFormHM().put("setsMap", setsMap);
		this.userView.getHm().put("setsMap", setsMap);
		this.getFormHM().put("canQuery", canQuery);
		this.getFormHM().put("selectField", selectField);
		this.userView.getHm().put("selectField", selectField);
		this.getFormHM().put("queryValue", queryValue);
		this.getFormHM().put("nbase", nbase);		
		this.getFormHM().put("statid", this.getStatid());
		this.getFormHM().put("norder", this.getNorder());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("charttype", this.getCharttype());
		this.getFormHM().put("showstyle", this.getShowstyle());
		this.getFormHM().put("categories", this.getCategories());
		this.getFormHM().put("returnvalue", this.getReturnvalue());
		this.getFormHM().put("resourcepriv", resourcepriv);
	}


	@Override
    public void outPutFormHM() {
        this.setStrsql((String)this.getFormHM().get("sql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));
        this.setOrder((String)this.getFormHM().get("order"));
        this.setP_a_code((String)this.getFormHM().get("p_a_code"));
        this.setA_code((String)this.getFormHM().get("a_code"));
        this.setAllcount((String)this.getFormHM().get("allcount"));
        this.setShowstyle((String)this.getFormHM().get("showstyle"));
        this.setBasicinfo_template((Map)this.getFormHM().get("basicinfo_template"));
        this.setSetsMap((Map)this.getFormHM().get("setsMap"));
        this.setCanQuery((String)this.getFormHM().get("canQuery"));
        this.setSelectField((String)this.getFormHM().get("selectField"));
        this.setP_codeitemdesc((String)this.getFormHM().get("p_codeitemdesc"));
        if(this.getSphoneForm()!=null){
        	this.getSphoneForm().setList((ArrayList)this.getFormHM().get("condlist"));
        }
        this.setHtml((String)this.getFormHM().get("html"));
        this.setStatid((String)this.getFormHM().get("statid"));
        this.setNorder((String)this.getFormHM().get("norder"));
        this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
        this.setLegendlist((ArrayList)this.getFormHM().get("legendlist"));
        this.setNordercountbean((LazyDynaBean)this.getFormHM().get("nordercountbean"));
        this.setNbase((String)this.getFormHM().get("nbase"));
        this.setDblist((ArrayList)this.getFormHM().get("dblist"));
        this.setCharttype((String)this.getFormHM().get("charttype"));
        this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
        this.setStatlabel((String)this.getFormHM().get("statlabel"));
        this.setCardid((String)this.getFormHM().get("cardid"));
        this.setResourcepriv((String)this.getFormHM().get("resourcepriv"));
	}


	public String getModuleid() {
		return moduleid;
	}


	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}



	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		
		super.reset(mapping, request);
		this.queryitem="";
		if("link".equals(request.getParameter("b_int"))||"link".equals(request.getParameter("b_init"))||"hroster".equals(request.getParameter("b_init"))){
			/**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();  
		}
		if("/phone-app/app/myteam".equals(mapping.getPath())&&("link".equals(request.getParameter("b_search"))||"link".equals(request.getParameter("b_query")))){
			if(this.getPagination()!=null)
            	this.getPagination().firstPage(); 
		}
		if("/phone-app/app/generalquery".equals(mapping.getPath())&&"link".equals(request.getParameter("b_query"))){
			if(this.sphoneForm.getPagination()!=null)
            	this.sphoneForm.getPagination().firstPage(); 
		}
		if("/phone-app/app/hroster".equals(mapping.getPath())&&"link".equals(request.getParameter("b_query"))){
			if(this.sphoneForm.getPagination()!=null)
            	this.sphoneForm.getPagination().firstPage(); 
		}
		if("/phone-app/app/contacts".equals(mapping.getPath())&&"link".equals(request.getParameter("b_query"))){
			if(this.getPagination()!=null)
            	this.getPagination().firstPage(); 
		}
	}


	public String getNbase() {
		return nbase;
	}


	public void setNbase(String nbase) {
		this.nbase = nbase;
	}


	public String getA0100() {
		return a0100;
	}


	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}


	public String getA_code() {
		return a_code;
	}


	public void setA_code(String a_code) {
		this.a_code = a_code;
	}


	public String getP_a_code() {
		return p_a_code;
	}


	public void setP_a_code(String p_a_code) {
		this.p_a_code = p_a_code;
	}


	public String getAllcount() {
		return allcount;
	}


	public void setAllcount(String allcount) {
		this.allcount = allcount;
	}


	public String getShowstyle() {
		return showstyle;
	}


	public void setShowstyle(String showstyle) {
		this.showstyle = showstyle;
	}


	public Map getBasicinfo_template() {
		return basicinfo_template;
	}


	public void setBasicinfo_template(Map basicinfo_template) {
		this.basicinfo_template = basicinfo_template;
	}



	public Map getSetsMap() {
		return setsMap;
	}


	public void setSetsMap(Map setsMap) {
		this.setsMap = setsMap;
	}


	public String getCanQuery() {
		return canQuery;
	}


	public void setCanQuery(String canQuery) {
		this.canQuery = canQuery;
	}


	public String getSelectField() {
		return selectField;
	}


	public void setSelectField(String selectField) {
		this.selectField = selectField;
	}


	public String getP_codeitemdesc() {
		return p_codeitemdesc;
	}


	public void setP_codeitemdesc(String p_codeitemdesc) {
		this.p_codeitemdesc = p_codeitemdesc;
	}

	public String getStatid() {
		return statid;
	}


	public void setStatid(String statid) {
		this.statid = statid;
	}


	public String getNorder() {
		return norder;
	}


	public void setNorder(String norder) {
		this.norder = norder;
	}


	public String getSnamedisplay() {
		return snamedisplay;
	}


	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}


	public ArrayList getLegendlist() {
		return legendlist;
	}


	public void setLegendlist(ArrayList legendlist) {
		this.legendlist = legendlist;
	}


	public LazyDynaBean getNordercountbean() {
		return nordercountbean;
	}


	public void setNordercountbean(LazyDynaBean nordercountbean) {
		this.nordercountbean = nordercountbean;
	}

	public String getReturnvalue() {
		return returnvalue;
	}


	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}



	public String getQueryValue() {
		return queryValue;
	}


	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}

	public PaginationForm getSphoneForm() {
		return sphoneForm;
	}


	public void setSphoneForm(PaginationForm sphoneForm) {
		this.sphoneForm = sphoneForm;
	}


	public String getHtml() {
		return html;
	}


	public void setHtml(String html) {
		this.html = html;
	}

	public String getStatlabel() {
		return statlabel;
	}


	public void setStatlabel(String statlabel) {
		this.statlabel = statlabel;
	}


	public String getSortid() {
		return sortid;
	}


	public void setSortid(String sortid) {
		this.sortid = sortid;
	}


	public String getCardid() {
		return cardid;
	}


	public void setCardid(String cardid) {
		this.cardid = cardid;
	}


	public String getResourcepriv() {
		return resourcepriv;
	}


	public void setResourcepriv(String resourcepriv) {
		this.resourcepriv = resourcepriv;
	}

}
