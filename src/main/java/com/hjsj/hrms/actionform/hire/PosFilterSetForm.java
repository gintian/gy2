/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:PosFilterSetForm</p>
 * <p>Description:人员过滤条件表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 05, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class PosFilterSetForm extends FrameForm {
	
	private String userBase = "Usr";
	/**记录集名称*/
    private String setname="A01";
    private String domainflag=Integer.toBinaryString(Constant.ALL_FIELD_SET);
    /**选中的字段值对列表*/
    private ArrayList fieldlist=new ArrayList();
    /**选中的字段名数组*/
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**对历史记录进行查询*/
    private String history;
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
	 /**查询条件串*/
    private String cond_str="";
    /**查询串*/
    private String sql_str;
    /**需显示的指标串*/
    private String columns;
    private String dbpre="usr";
    private ArrayList dblist=new ArrayList();
    /**员工自助的表名*/
    private String tablename="UsrA01";
    
    public ArrayList getDblist() {
        return dblist;
    }
    public void setDblist(ArrayList dblist) {
        this.dblist = dblist;
    }
    /**登录用户对象*/
    private RecordVo user_vo=new RecordVo("UsrA01");
    /**面试材料对象*/
    private RecordVo testQuestionvo=new RecordVo("ZP_POS_TEST");
    /**面试材料对象列表*/
    private PaginationForm testQuestionForm=new PaginationForm();
    /**用户号标识*/
    private String user_id;
    /**状态标识，用于标识返回的页面的*/
    private String status="0";
    
    private String pos_id = "";
    /**构库标识*/
    private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    public PosFilterSetForm() {
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
    
    @Override
    public void outPutFormHM() {
        this.setUser_vo((RecordVo)this.getFormHM().get("user_vo"));
        this.setCond_str((String)this.getFormHM().get("cond_str"));
        this.setUserBase((String)this.getFormHM().get("userBase"));
        this.getTestQuestionForm().setList((ArrayList)this.getFormHM().get("testQuestionlist"));
        this.setTestQuestionvo((RecordVo)this.getFormHM().get("testQuestionvo"));
        this.setUser_id((String)this.getFormHM().get("user_id"));
        this.setStatus((String)this.getFormHM().get("userflag"));
        this.setSql_str((String)this.getFormHM().get("sql_str"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setDblist((ArrayList)this.getFormHM().get("dblist"));
        this.setTablename((String)this.getFormHM().get("tablename"));
        this.setSucceedinfo((String)this.getFormHM().get("succeedinfo"));
        this.setStrsql((String)this.getFormHM().get("cond_sql"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));  
        this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
       this.setDbcond((String)this.getFormHM().get("dbcond")); 
       this.setFactorlist((ArrayList)this.getFormHM().get("factorlist")); 
       this.setQuery_type((String)this.getFormHM().get("query_type"));
       this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
       this.setLeft_fields((String[])this.getFormHM().get("left_fields"));
       this.setExpression((String)this.getFormHM().get("expression"));
       this.setRight_fields((String[])this.getFormHM().get("right_fields"));
     }

     @Override
     public void inPutTransHM() {
         this.getFormHM().put("selectedlist",this.getTestQuestionForm().getSelectedList());
         this.getFormHM().put("testQuestionvo",this.getTestQuestionvo());
         this.getFormHM().put("user_id",this.getUser_id());
         this.getFormHM().put("dbpre",this.getDbpre());
         this.getFormHM().put("right_fields",this.getRight_fields());
         this.getFormHM().put("like",this.getLike());
         this.getFormHM().put("history",this.getHistory());     
         this.getFormHM().put("factorlist",this.getFactorlist());
         this.getFormHM().put("query_type",this.getQuery_type());
         this.getFormHM().put("expression",this.getExpression());
         this.getFormHM().put("fieldlist",this.fieldlist);
         this.getFormHM().put("userBase",this.userBase);
     }

     public String getColumns() {
         return columns;
     }
     public void setColumns(String columns) {
         this.columns = columns;
     }
     public String getSql_str() {
         return sql_str;
     }
     public void setSql_str(String sql_str) {
         this.sql_str = sql_str;
     }    
     public String getCond_str() {
         return cond_str;
     }
     public void setCond_str(String cond_str) {
         this.cond_str = cond_str;
     }
     public RecordVo getUser_vo() {
         return user_vo;
     }
     public void setUser_vo(RecordVo user_vo) {
         this.user_vo = user_vo;
         
     }
     public String getDbpre() {
         return dbpre;
     }
     public void setDbpre(String dbpre) {
         this.dbpre = dbpre;
     }    
     @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
     	
         if("/hire/zp_options/pos_filter_login".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
         {
             if(this.getPagination()!=null)
               this.getPagination().firstPage();
         }
         return super.validate(arg0, arg1);
     }
     public String getUser_id() {
         return user_id;
     }
     public void setUser_id(String user_id) {
         this.user_id = user_id;
     }
     public String getStatus() {
         return status;
     }
     public void setStatus(String status) {
         this.status = status;
     }
 	public String getTablename() {
 		return tablename;
 	}
 	public void setTablename(String tablename) {
 		this.tablename = tablename;
 	}
 	public PaginationForm getTestQuestionForm() {
 		return testQuestionForm;
 	}
 	public void setTestQuestionForm(PaginationForm testQuestionForm) {
 		this.testQuestionForm = testQuestionForm;
 	}
 	public String getPos_id() {
 		return pos_id;
 	}
 	public void setPos_id(String pos_id) {
 		this.pos_id = pos_id;
 	}
 	public RecordVo getTestQuestionvo() {
 		return testQuestionvo;
 	}
 	public void setTestQuestionvo(RecordVo testQuestionvo) {
 		this.testQuestionvo = testQuestionvo;
 	}
 	public String getSucceedinfo() {
 		return succeedinfo;
 	}
 	public void setSucceedinfo(String succeedinfo) {
 		this.succeedinfo = succeedinfo;
 	}
 	public String getUsedflag() {
 		return usedflag;
 	}
 	public void setUsedflag(String usedflag) {
 		this.usedflag = usedflag;
 	}
 	public String[] getRight_fields() {
 		return right_fields;
 	}
 	public void setRight_fields(String[] right_fields) {
 		this.right_fields = right_fields;
 	}
 	public String getSetname() {
 		return setname;
 	}
 	public void setSetname(String setname) {
 		this.setname = setname;
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
 	public String getDbcond() {
 		return dbcond;
 	}
 	public void setDbcond(String dbcond) {
 		this.dbcond = dbcond;
 	}
 	public String getDomainflag() {
 		return domainflag;
 	}
 	public void setDomainflag(String domainflag) {
 		this.domainflag = domainflag;
 	}
 	public String getExpression() {
 		return expression;
 	}
 	public void setExpression(String expression) {
 		this.expression = expression;
 	}
 	public ArrayList getFactorlist() {
 		return factorlist;
 	}
 	public void setFactorlist(ArrayList factorlist) {
 		this.factorlist = factorlist;
 	}
 	public ArrayList getFieldlist() {
 		return fieldlist;
 	}
 	public void setFieldlist(ArrayList fieldlist) {
 		this.fieldlist = fieldlist;
 	}
 	public String getHistory() {
 		return history;
 	}
 	public void setHistory(String history) {
 		this.history = history;
 	}
 	public String[] getLeft_fields() {
 		return left_fields;
 	}
 	public void setLeft_fields(String[] left_fields) {
 		this.left_fields = left_fields;
 	}
 	public String getLike() {
 		return like;
 	}
 	public void setLike(String like) {
 		this.like = like;
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
 	public String getQuery_type() {
 		return query_type;
 	}
 	public void setQuery_type(String query_type) {
 		this.query_type = query_type;
 	}
 	public ArrayList getResultlist() {
 		return resultlist;
 	}
 	public void setResultlist(ArrayList resultlist) {
 		this.resultlist = resultlist;
 	}
 	public String getUserBase() {
 		return userBase;
 	}
 	public void setUserBase(String userBase) {
 		this.userBase = userBase;
 	}
}
