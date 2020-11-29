package com.hjsj.hrms.actionform.query;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:QueryInterfaceForm</p>
 * <p>Description:查询接口表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-10:12:14:52</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QueryInterfaceForm extends FrameForm {
    /**应用库表前缀*/
    private String dbpre;
    /**查询字段列表*/
    private ArrayList fieldlist=new ArrayList();
    /**应用过滤条件*/
    private String dbcond="''";
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**查询结果*/
    private String result="0";   
    /**查询对象，查部门=1，查单位=2，都查=0*/
    private String qobj="0";
    /**查询条件*/
    private String strsql;
    /**查询条件*/
    private String strwhere;
    /**显示的字段名称*/
    private String columns;
    /**唯一标识*/
    private String distinct;
    /**键值列表*/
    private String keys;
    /**常用条件列表*/
    private ArrayList condlist=new ArrayList();
    /**选中的常用条件号*/
    private String[] curr_id;
    /**对历史记录进行查询*/
    private String history;
    /**人员单位及职位标识*/
    private String type="1";
    /**查询结果字段列表*/
    private ArrayList resultlist=new ArrayList();
    private String order="";
    //主键号,包括人员编号,单位编码,职位编码
    private String keyid;
    /**控制返回按钮的跳转页面*/
    private String home="0";
    /**浏览信息所用的卡片号*/
    private String tabid="-1";
    private String uplevel;
    //兼职单位
    private String part_unit;
    //兼职子集
    private String part_setid;
    private String photo_other_view;
    private String photolength="";
    private String returnvalue;
    private ArrayList catelist= new ArrayList();
    private String categories;
    private String cond_str;
    private String order_by;
    
    private String userbase="";
    private HashMap part_map=new HashMap();
    //常用查询条件名称
    private String lexprName="";
    //信息集是否显示附件
    private String multimedia_file_flag;
	
	public String getMultimedia_file_flag() {
		return multimedia_file_flag;
	}
	public void setMultimedia_file_flag(String multimedia_file_flag) {
		this.multimedia_file_flag = multimedia_file_flag;
	}
	
    public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public ArrayList getResultlist() {
		return resultlist;
	}
	public void setResultlist(ArrayList resultlist) {
		this.resultlist = resultlist;
	}
	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getHistory() {
        return history;
    }
    public void setHistory(String history) {
        this.history = history;
    }

    
    public QueryInterfaceForm() {
        super();
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
        this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setDbcond((String)this.getFormHM().get("dbcond"));
        this.setStrsql((String)this.getFormHM().get("cond_sql"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setStrwhere((String)this.getFormHM().get("strwhere"));
        this.setKeys((String)this.getFormHM().get("keys"));
        //gquer常用查询*/
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setType((String)this.getFormHM().get("type"));
        this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
        this.setDistinct((String)this.getFormHM().get("distinct"));
        this.setKeyid((String)this.getFormHM().get("keyid"));
        this.setTabid((String)this.getFormHM().get("tabid"));
        this.setOrder((String)this.getFormHM().get("order"));
        this.setUplevel((String)this.getFormHM().get("uplevel"));
        this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
        String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	     this.setPhotolength(photolength);
        this.setCatelist((ArrayList)this.getFormHM().get("catelist"));
        this.setCategories((String)this.getFormHM().get("categories"));
        this.setOrder_by((String)this.getFormHM().get("order_by"));
        this.setCond_str((String)this.getFormHM().get("cond_str"));
        
        this.setPart_map((HashMap)this.getFormHM().get("part_map"));
        this.setUserbase((String)this.getFormHM().get("userbase"));
        this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
        this.setPart_unit((String)this.getFormHM().get("part_unit"));
        this.setLexprName((String)this.getFormHM().get("lexprName"));
        this.getFormHM().remove("lexprName");
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("fieldlist",this.getFieldlist());
        this.getFormHM().put("like",this.getLike());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("curr_id",this.getCurr_id());
        this.getFormHM().put("history",this.getHistory());
        this.getFormHM().put("type",this.getType());
		this.getFormHM().put("result",this.getResult());
		this.getFormHM().put("qobj",this.getQobj());
		
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
		this.getFormHM().put("part_unit", this.getPart_unit());
    }

    public String getDbpre() {
        return dbpre;
    }
    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
    }
    public ArrayList getFieldlist() {
        return fieldlist;
    }
    public void setFieldlist(ArrayList fieldlist) {
        this.fieldlist = fieldlist;
    }
    public String getDbcond() {
        return dbcond;
    }
    public void setDbcond(String dbcond) {
        this.dbcond = dbcond;
    }
    public String getLike() {
        return like;
    }
    public void setLike(String like) {
        this.like = like;
    }

    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
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
    public ArrayList getCondlist() {
        return condlist;
    }
    public void setCondlist(ArrayList condlist) {
        this.condlist = condlist;
    }
    public String[] getCurr_id() {
        return curr_id;
    }
    public void setCurr_id(String[] curr_id) {
        this.curr_id = curr_id;
    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/workbench/query/query_interface".equals(arg0.getPath())&&arg1.getParameter("b_mquery")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage(); 
        }
        if("/workbench/query/query_interface1".equals(arg0.getPath())&&arg1.getParameter("b_queryperson")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
        if("/workbench/query/gquery_interface".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();    
            //this.setHome("3");
        }
        else if(("/workbench/query/query_interface".equals(arg0.getPath())&&arg1.getParameter("b_gquery")!=null)||(arg1.getParameter("br_greturn")!=null))
        {
            //this.setHome("3");
        }  
        else
        {
        	 //this.setHome("0");
        }        
		return super.validate(arg0, arg1);
	}
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
	    if ("/workbench/query/query_interface".equals(arg0.getPath()) && arg1.getParameter("Switch") == null&& arg1.getParameter("br_field")==null) {
	        this.setLike("0");
	        this.setHistory("0");
	        this.setResult("0");
	        this.setQobj("0");
	        //this.setHome("0");		         
	    }
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getQobj() {
		return qobj;
	}
	public void setQobj(String qobj) {
		this.qobj = qobj;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getPart_unit() {
		return part_unit;
	}
	public void setPart_unit(String part_unit) {
		this.part_unit = part_unit;
	}
	public String getPart_setid() {
		return part_setid;
	}
	public void setPart_setid(String part_setid) {
		this.part_setid = part_setid;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public ArrayList getCatelist() {
		return catelist;
	}
	public void setCatelist(ArrayList catelist) {
		this.catelist = catelist;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public String getCond_str() {
		return cond_str;
	}
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
    public String getLexprName() {
        return lexprName;
    }
    public void setLexprName(String lexprName) {
        this.lexprName = lexprName;
    }
	
}
