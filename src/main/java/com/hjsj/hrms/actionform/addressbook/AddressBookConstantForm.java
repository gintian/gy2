package com.hjsj.hrms.actionform.addressbook;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class AddressBookConstantForm extends FrameForm {
	private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
	private String setname="A01";
	private String constant;
	private String type;
	private String str_value="";
	private String describe;
    private String str_valueini="";
    private List str_valuelist =new ArrayList();
    private List fielditemlist=new ArrayList();
    private String a0100;
    private String nbase;
    private ArrayList dbaselist=new ArrayList();
    private ArrayList fieldlist=new ArrayList();
    private String treeCode;//树形菜单，在HtmlMenu中
    private String sqlstr;
    private String strwhere;
    private String orderby;
    private String columns;
    private String select_name;//筛选名字
    private String code;//连接级别  
    private String kind;
    private String uplevel;
    /**选中的字段名数组*/
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    private String querylike;
    private String query;//cha xun biao shi 
    
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getQuerylike() {
		return querylike;
	}
	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public ArrayList getDbaselist() {
		return dbaselist;
	}
	public void setDbaselist(ArrayList dbaselist) {
		this.dbaselist = dbaselist;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getStrwhere() {
		return strwhere;
	}
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the fielditemlist.
	 */
	public List getFielditemlist() {
		return fielditemlist;
	}
	/**
	 * @param fielditemlist The fielditemlist to set.
	 */
	public void setFielditemlist(List fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
    CardTagParamView cardparam=new CardTagParamView();
	//private RecordVo constant_vo=ConstantParamter.getConstantVo("SS_ADDRESSBOOK");
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
        this.setStr_valuelist((List)this.getFormHM().get("str_valuelist"));
        this.setStr_value((String)this.getFormHM().get("str_value"));   
        this.setStr_valueini((String)this.getFormHM().get("str_valueini")); 
        this.setFielditemlist((List)this.getFormHM().get("fielditemlist"));
        //this.setSelect_name((String)this.getFormHM().get("select_name"));
        this.setTreeCode((String)this.getFormHM().get("treeCode"));	
        this.setSqlstr((String)this.getFormHM().get("sqlstr"));
 	    this.setColumns((String)this.getFormHM().get("columns"));
 	    this.setStrwhere((String)this.getFormHM().get("strwhere"));
 	    this.setOrderby((String)this.getFormHM().get("orderby"));
 	    this.setNbase((String)this.getFormHM().get("nbase"));
 	    this.setDbaselist((ArrayList)this.getFormHM().get("dbaselist"));
 	    this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
 	    this.setCode((String)this.getFormHM().get("code"));
 	    this.setKind((String)this.getFormHM().get("kind"));
 	    this.setUplevel((String)this.getFormHM().get("uplevel"));
 	    this.setQuery((String) this.getFormHM().get("query"));
	}
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("str_valuelist",str_valuelist);
		this.getFormHM().put("str_value",str_value);
		this.getFormHM().put("str_valueini",str_valueini);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("fielditemlist",fielditemlist);
		this.getFormHM().put("nbase", nbase);
		this.getFormHM().put("select_name", select_name);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("querylike", this.getQuerylike());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("query",this.getQuery());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    
		 if(("/selfservice/addressbook/queryaddressbook".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)||("/selfservice/addressbook/initqueryaddressbook".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null))
		    {
		        if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		    }
		 
		 // 清空姓名
		 if(("/selfservice/addressbook/queryaddressbook".equals(arg0.getPath())
				 && arg1.getParameter("b_search")!=null 
				 && arg1.getParameter("kind") != null)||("/selfservice/addressbook/initqueryaddressbook".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)) {
			 this.select_name = "";
		    }
		 return super.validate(arg0, arg1);
	}
	/**
	 * @return Returns the constant.
	 */
	public String getConstant() {
		return constant;
	}
	/**
	 * @param constant The constant to set.
	 */
	public void setConstant(String constant) {
		this.constant = constant;
	}

	/**
	 * @return Returns the describe.
	 */
	public String getDescribe() {
		return describe;
	}
	/**
	 * @param describe The describe to set.
	 */
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	/**
	 * @return Returns the str_value.
	 */
	public String getStr_value() {
		return str_value;
	}
	/**
	 * @param str_value The str_value to set.
	 */
	public void setStr_value(String str_value) {
		this.str_value = str_value;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return Returns the str_valuelist.
	 */
	public List getStr_valuelist() {
		return str_valuelist;
	}
	/**
	 * @param str_valuelist The str_valuelist to set.
	 */
	public void setStr_valuelist(List str_valuelist) {
		this.str_valuelist = str_valuelist;
	}
	/**
	 * @return Returns the str_valueini.
	 */
	public String getStr_valueini() {
		return str_valueini;
	}
	/**
	 * @param str_valueini The str_valueini to set.
	 */
	public void setStr_valueini(String str_valueini) {
		this.str_valueini = str_valueini;
	}
	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	public String getUsedflag() {
		return usedflag;
	}
	public void setUsedflag(String usedflag) {
		this.usedflag = usedflag;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

    public String getDbaseCount() {
        return Integer.toString(dbaselist.size());
    }
}
