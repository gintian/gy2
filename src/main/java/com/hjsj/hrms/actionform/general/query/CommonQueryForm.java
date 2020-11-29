/**
 * 
 */
package com.hjsj.hrms.actionform.general.query;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:CommonQueryForm</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-28:11:05:25</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CommonQueryForm extends FrameForm {

    /**应用库表前缀*/
    private String[] dbpre;
    /**查询结果显示字段列表*/
    private ArrayList showlist=new ArrayList();
    
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**查询结果*/
    private String result="0"; 
    /**历史记录*/
    private String history="0";
    /**人员单位及职位标识
     * =1人员,=2单位,=3职位
     * */
    private String type="1";
    /**权限范围内的人员库*/
    private ArrayList dblist=new ArrayList();
    /**数据集名称*/
    private String setname;
    private String setidpiv;
    private String setdescpiv;
    /**查询语句*/
    private String sql;
    /**显示应用库前缀,主要用于跨库查询*/
    private String show_dbpre;
	
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
    private String expression;   
    /**查询类型
     * =1简单查询
     * =2通用查询
     */
    private String query_type="1";
    /**条件定义控制符*/
    private String define="0";
    /**factor list*/
    private ArrayList factorlist=new ArrayList();    
    /**关系操作符*/
    private ArrayList operlist=new ArrayList();
    /**逻辑操作符*/
    private ArrayList logiclist=new ArrayList(); 
    /**选中的指标列表*/
    private ArrayList selectedlist=new ArrayList();
    
    /**常用条件列表*/
    private ArrayList selectedCondlist=new ArrayList();
    /**常用条件列表*/
    private String condname="";    
    private ArrayList condlist=new ArrayList(); 
    private String keyid="";
    /**表达式：1+2|A0405=`A0107=1`
     * 重新解释查询表达式
     * */
    private String expr;
    
    
    /**过滤检索表达式*/
    private String filter_factor;
    
    private String row_num="";
    /**条件名称*/
    private String name;
    private String checkselect="0";
    private String columns="";
    private String orderby="";
    private String chpriv="";

    private String isGetSql="0";
    private String uplevel;
    
    private String queryflag;
    
    private String tabId;//人事异动区分考勤模板 需传递模板号
    
	public String getQueryflag() {
		return queryflag;
	}


	public void setQueryflag(String queryflag) {
		this.queryflag = queryflag;
	}


	public String getKeyid() {
		return keyid;
	}


	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

	public CommonQueryForm() {
        CommonData vo=new CommonData("=","=");
        operlist.add(vo);
        vo=new CommonData(">",">");
        operlist.add(vo);  
        vo=new CommonData(">=",">=");
        operlist.add(vo); 
        vo=new CommonData("<","<");
        operlist.add(vo);
        vo=new CommonData("<=","<=");
        operlist.add(vo);   
        vo=new CommonData("<>","<>");
        operlist.add(vo);
        vo=new CommonData("*","并且");
        logiclist.add(vo);
        vo=new CommonData("+","或");  
        logiclist.add(vo);
	}


	public ArrayList getFactorlist() {
		return factorlist;
	}


	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}


	public ArrayList getLogiclist() {
		return logiclist;
	}


	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}


	public ArrayList getOperlist() {
		return operlist;
	}


	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}


	public ArrayList getDblist() {
		return dblist;
	}

	
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String[] getDbpre() {
		return dbpre;
	}

	public void setDbpre(String[] dbpre) {
		this.dbpre = dbpre;
	}

	/**数据清空*/
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setHistory("0");
		this.setLike("0");
		this.setResult("0");
		String[] temp=new String[1];
	//	this.setDbpre(temp);
		String temp_type=arg1.getParameter("type");
		if(temp_type!=null)
		{
			if(!temp_type.equalsIgnoreCase(this.getType())&&this.getSelectedlist()!=null)
			{
				this.setExpression("");
				this.getSelectedlist().clear();
			}
		}	
		this.setKeyid("");	
		this.setExpr("");
		this.setCondname("");
		this.setDefine("0");
		
		for(int i=0;i<this.factorlist.size();i++){
			 Factor factor=(Factor)factorlist.get(i);
	            String oper = PubFunc.hireKeyWord_filter_reback(factor.getOper());
	            factor.setOper(oper);
		}
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getShow_dbpre() {
		return show_dbpre;
	}

	public void setShow_dbpre(String show_dbpre) {
		this.show_dbpre = show_dbpre;
	}

	public ArrayList getShowlist() {
		return showlist;
	}

	public void setShowlist(ArrayList showlist) {
		this.showlist = showlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
    public void outPutFormHM() {
		this.setIsGetSql((String)this.getFormHM().get("isGetSql"));
		
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setShowlist((ArrayList)this.getFormHM().get("showlist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setExpression((String)this.getFormHM().get("expression"));
		this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
		this.setSelectedCondlist((ArrayList)this.getFormHM().get("selectedCondlist"));
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setExpr((String)this.getFormHM().get("expr"));
        this.setName((String)this.getFormHM().get("name"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setOrderby((String)this.getFormHM().get("orderby"));
        this.setChpriv((String)this.getFormHM().get("chpriv"));
        this.setRow_num((String)this.getFormHM().get("row_num"));
        this.setUplevel((String)this.getFormHM().get("uplevel"));
        this.setTabId((String)this.getFormHM().get("tabId"));
        this.setSetidpiv((String)this.getFormHM().get("setidpiv"));
        this.setSetdescpiv((String)this.getFormHM().get("setdescpiv"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("result",this.getResult());
		this.getFormHM().put("history",this.getHistory());
		this.getFormHM().put("like",this.getLike());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("show_dbpre",this.getShow_dbpre());
		this.getFormHM().put("expression",this.getExpression());
		this.getFormHM().put("right_fields",this.getRight_fields());
        this.getFormHM().put("factorlist",this.getFactorlist());		
        this.getFormHM().put("query_type",this.getQuery_type());
        this.getFormHM().put("condname",this.getCondname()); 
        this.getFormHM().put("condid",this.getKeyid());       
        this.getFormHM().put("expr",this.getExpr());
        this.getFormHM().put("filter_factor",this.filter_factor);
        this.getFormHM().put("name",this.getName());
        this.getFormHM().put("checkselect",this.getCheckselect());
        this.getFormHM().put("row_num", this.row_num);
        this.getFormHM().put("personPriv", this.getChpriv());
        this.getFormHM().put("tabId", this.getTabId());
        this.getFormHM().put("setidpiv", this.getSetidpiv());
        this.getFormHM().put("setdescpiv", this.getSetdescpiv());
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
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


	public String getQuery_type() {
		return query_type;
	}


	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}


	public ArrayList getSelectedlist() {
		return selectedlist;
	}


	public void setSelectedlist(ArrayList selectedlist) {
		if(selectedlist==null)
			selectedlist=new ArrayList();
		this.selectedlist = selectedlist;
	}


	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/query/common/select_query_fields".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null){
			//form中数据未清空，从request中取出queryflag放入form  jingq add 2014.10.23
			this.setQueryflag(arg1.getParameter("queryflag"));
		}
		return super.validate(arg0, arg1);
	}


	public ArrayList getCondlist() {
		return condlist;
	}


	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}


	public String getCondname() {
		return condname;
	}


	public void setCondname(String condname) {
		this.condname = condname;
	}


	public String getExpr() {
		return expr;
	}


	public void setExpr(String expr) {
		this.expr = expr;
	}


	public String getDefine() {
		return define;
	}


	public void setDefine(String define) {
		this.define = define;
	}


	public String getFilter_factor() {
		return this.filter_factor;
	}


	public void setFilter_factor(String filter_factor) {
		this.filter_factor = filter_factor;
	}


	public ArrayList getSelectedCondlist() {
		return selectedCondlist;
	}


	public void setSelectedCondlist(ArrayList selectedCondlist) {
		this.selectedCondlist = selectedCondlist;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCheckselect() {
		return checkselect;
	}


	public void setCheckselect(String checkselect) {
		this.checkselect = checkselect;
	}


	public String getColumns() {
		return columns;
	}


	public void setColumns(String columns) {
		this.columns = columns;
	}


	public String getOrderby() {
		return orderby;
	}


	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}


	public String getChpriv() {
		return chpriv;
	}


	public void setChpriv(String chpriv) {
		this.chpriv = chpriv;
	}


	public String getRow_num() {
		return row_num;
	}


	public void setRow_num(String row_num) {
		this.row_num = row_num;
	}


	public String getIsGetSql() {
		return isGetSql;
	}


	public void setIsGetSql(String isGetSql) {
		this.isGetSql = isGetSql;
	}


	public String getUplevel() {
		return uplevel;
	}


	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}


	public String getSetidpiv() {
		return setidpiv;
	}


	public void setSetidpiv(String setidpiv) {
		this.setidpiv = setidpiv;
	}


	public String getSetdescpiv() {
		return setdescpiv;
	}


	public void setSetdescpiv(String setdescpiv) {
		this.setdescpiv = setdescpiv;
	}

}
