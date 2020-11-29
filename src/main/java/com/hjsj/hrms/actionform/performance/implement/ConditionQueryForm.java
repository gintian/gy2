package com.hjsj.hrms.actionform.performance.implement;

import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ConditionQueryForm extends FrameForm {

	/**考核计划id */
	private String plan_id="0";

	private String flag="1"; //1。插入考核对象  2：插入考核主体
	
	private String str_sql="";
	private String str_whl="";
	
	private String body_id="";  //主体分类id
	private String object_id=""; //对象id
	private String objectType="2";  //考核类别 1：部门 2：人员
	
	
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
    /**人员条件选择是否需要按部门对应标志*/
    private String accordByDepartment="";
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
    /**唯一标识*/
    private String distinct;    
    /**查询类型
     * =1简单查询
     * =2通用查询
     * */
    private String query_type="1";
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
    private String expression;
    
    /**查询结果字段列表*/
    private ArrayList resultlist=new ArrayList();
    //主键号,包括人员编号,单位编码,职位编码
    private String keyid;
    //是否全选
    private String isSelectAll="0";
	public String getKeyid() {
		return keyid;
	}


	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}


	/**
     * 
     */
    public ConditionQueryForm() {
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
    	this.setIsSelectAll((String)this.getFormHM().get("isSelectAll"));
    	this.setObjectType((String)this.getFormHM().get("objectType"));
        if(this.getFormHM().get("fieldlist")!=null)
            this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setDbcond((String)this.getFormHM().get("dbcond")); 
        this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
        this.setStrsql((String)this.getFormHM().get("cond_sql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));  
        this.setQuery_type((String)this.getFormHM().get("query_type"));
        this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
        this.setExpression((String)this.getFormHM().get("expression"));
        this.setDistinct((String)this.getFormHM().get("distinct"));
        this.setType((String)this.getFormHM().get("type"));
        this.like="0";
        this.history="0";     
        this.setPlan_id((String)this.getFormHM().get("plan_id"));
        this.setStr_sql((String)this.getFormHM().get("str_sql"));
        this.setStr_whl((String)this.getFormHM().get("str_whl"));
        this.setObject_id((String)this.getFormHM().get("object_id"));
        this.setBody_id((String)this.getFormHM().get("body_id"));
        this.setFlag((String)this.getFormHM().get("flag"));
        this.setAccordByDepartment((String)this.getFormHM().get("accordByDepartment"));
    }	

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
    	this.getFormHM().put("accordByDepartment", this.getAccordByDepartment());
    	this.getFormHM().put("isSelectAll",this.getIsSelectAll());
    	if(this.getPagination()!=null)  //2013.11.28 pjf
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
    	this.getFormHM().put("body_id",this.getBody_id());
    	this.getFormHM().put("plan_id",this.getPlan_id());
        this.getFormHM().put("right_fields",this.getRight_fields());
        this.getFormHM().put("like",this.getLike());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("history",this.getHistory());     
        this.getFormHM().put("factorlist",this.getFactorlist());
        this.getFormHM().put("query_type",this.getQuery_type());
        this.getFormHM().put("expression",this.getExpression());
    }

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if("/selfservice/performance/hquery_interface".equals(arg0.getPath())&&(arg1.getParameter("b_query2")!=null))
		{
			if(this.fieldlist!=null)
				this.fieldlist.clear();
			this.right_fields=new String[0];
			this.setExpression("");
		}

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
    	if("2".equals(type))
    	{
    		this.setDomainflag(Integer.toString(Constant.UNIT_FIELD_SET));
    		this.setSetname("B01");
    	}
    	else if("3".equals(type))
    	{
    		this.setDomainflag(Integer.toString(Constant.POS_FIELD_SET));
    		this.setSetname("K01");    		
    	}   
    	else
    	{
    		this.setDomainflag(Integer.toString(Constant.ALL_FIELD_SET)); 
    		this.setSetname("A01");      		
    	}
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




	public String getDistinct() {
		return distinct;
	}


	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}


	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}



	public String getStr_sql() {
		return str_sql;
	}


	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}


	public String getStr_whl() {
		return str_whl;
	}


	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}


	public String getBody_id() {
		return body_id;
	}


	public void setBody_id(String body_id) {
		this.body_id = body_id;
	}


	public String getObject_id() {
		return object_id;
	}


	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}


	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public String getIsSelectAll()
	{
		return isSelectAll;
	}


	public void setIsSelectAll(String isSelectAll)
	{
		this.isSelectAll = isSelectAll;
	}


	public String getAccordByDepartment()
	{
		return accordByDepartment;
	}


	public void setAccordByDepartment(String accordByDepartment)
	{
		this.accordByDepartment = accordByDepartment;
	}




}
