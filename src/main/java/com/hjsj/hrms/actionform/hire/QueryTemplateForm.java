/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:QueryTemplateForm</p>
 * <p>Description:查询模板表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 10, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class QueryTemplateForm extends FrameForm {

	/**记录集名称*/
    private String setname="A01";
    /**构库标识*/
    private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
    /**信息种类，对人员信息查询则选全部子集*/
    private String domainflag=Integer.toBinaryString(Constant.ALL_FIELD_SET);
    /**选中的字段值对列表*/
    private ArrayList fieldlist=new ArrayList();
    /**选中的字段名数组*/
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  

    /**应用库表前缀*/
    private String dbpre;
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**对历史记录进行查询*/
    private String history;
    /**人员单位及职位标识*/
    private String type;
    /**应用过滤条件*/
    private String dbcond="''";
    
    /**关系操作符*/
    private ArrayList operlist=new ArrayList();
    /**逻辑操作符*/
    private ArrayList logiclist=new ArrayList(); 
    /**factor list*/
    private ArrayList factorlist=new ArrayList();
    /**查询条件*/
    private String strsql;
    /**查询条件*/
    private String strwhere;
    /**显示的字段名称*/
    private String columns;    
    /**查询类型
     * =1简单查询
     * =2通用查询
     * */
    private String query_type="1";
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
    private String expression;
    
    /**查询结果字段列表*/
    private ArrayList resultlist=new ArrayList();
    private String succeedinfo;


	/**
	 * @return Returns the succeedinfo.
	 */
	public String getSucceedinfo() {
		return succeedinfo;
	}
	/**
	 * @param succeedinfo The succeedinfo to set.
	 */
	public void setSucceedinfo(String succeedinfo) {
		this.succeedinfo = succeedinfo;
	}
	/**
     * 
     */
    public QueryTemplateForm() {
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


    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        if(this.getFormHM().get("fieldlist")!=null)
            this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setDbcond((String)this.getFormHM().get("dbcond")); 
        this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
        this.setStrsql((String)this.getFormHM().get("cond_sql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));  
        this.setQuery_type((String)this.getFormHM().get("query_type"));
        this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
        this.setSucceedinfo((String)this.getFormHM().get("succeedinfo"));
        this.setLeft_fields((String[])this.getFormHM().get("left_fields"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("right_fields",this.getRight_fields());
        this.getFormHM().put("like",this.getLike());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("history",this.getHistory());     
        this.getFormHM().put("factorlist",this.getFactorlist());
        this.getFormHM().put("query_type",this.getQuery_type());
        this.getFormHM().put("expression",this.getExpression());
        this.getFormHM().put("fieldlist",this.fieldlist);
    }

    public ArrayList getResultlist() {
		return resultlist;
	}


	public void setResultlist(ArrayList resultlist) {
		this.resultlist = resultlist;
	}    
    public ArrayList getOperlist() {
        return operlist;
    }
    public void setOperlist(ArrayList operlist) {
        this.operlist = operlist;
    }
    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public String getDbcond() {
        return dbcond;
    }
    public void setDbcond(String dbcond) {
        this.dbcond = dbcond;
    }
    public String getDbpre() {
        return dbpre;
    }
    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSetname() {
        return setname;
    }
    public void setSetname(String setname) {
        this.setname = setname;
    }

    public ArrayList getFieldlist() {
        return fieldlist;
    }
    public void setFieldlist(ArrayList fieldlist) {
        this.fieldlist = fieldlist;
    }
    public String getDomainflag() {
        return domainflag;
    }
    public void setDomainflag(String domainflag) {
        this.domainflag = domainflag;
    }
    public String getUsedflag() {
        return usedflag;
    }
    public void setUsedflag(String usedflag) {
        this.usedflag = usedflag;
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

    public String getQuery_type() {
        return query_type;
    }
    public void setQuery_type(String query_type) {
        this.query_type = query_type;
    }
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
}