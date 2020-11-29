package com.hjsj.hrms.actionform.query;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:HighQueryForm</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 18, 2005:9:53:59 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class HighQueryForm extends FrameForm {
    

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
    /** 保存模糊查询的值 0:不用模糊查询１模糊查询*/
    private String likevalue;
    /**查询结果*/
    private String result="0";  
    /**查询对象，查部门＼单位＼都查*/
    private String qobj="0";
    /**对历史记录进行查询*/
    private String history;
    /**保存历史记录进行查询 0:不用１用*/
    private String historysave;
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
    private String order;
    /**唯一标识*/
    private String distinct;
    /**键值列表*/
    private String keys;
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
    /**常用条件列表*/
    private String condname="";
    private ArrayList condlist=new ArrayList();
    /**控制返回按钮的跳转页面*/
    private String home="0";
    
    /**role_id高级授权*/
    private String role_id="";
    /**浏览信息所用的卡片号*/
    private String tabid="-1";    
    private String uplevel;
    private String part_unit;
    private String part_setid;
    private String photo_other_view;
    private String photolength="";
    private String returnvalue;
    private String categories;
    private ArrayList catelist = new ArrayList();
    private String hidcategories;
    
    private String userbase="";
    private HashMap part_map=new HashMap();
    
    private String multimedia_file_flag;//信息集是否显示附件
	
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


	public String getUplevel() {
		return uplevel;
	}


	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}


	public String getTabid() {
		return tabid;
	}


	public void setTabid(String tabid) {
		this.tabid = tabid;
	}


	public String getRole_id() {
		return role_id;
	}


	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}


	public String getHome() {
		return home;
	}


	public void setHome(String home) {
		this.home = home;
	}


	public String getKeyid() {
		return keyid;
	}


	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}


	/**
     * 
     */
    public HighQueryForm() {
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
        this.setKeys((String)this.getFormHM().get("keys"));
        this.setQuery_type((String)this.getFormHM().get("query_type"));
        this.setResultlist((ArrayList)this.getFormHM().get("resultlist"));
        this.setExpression((String)this.getFormHM().get("expression"));
        this.setDistinct((String)this.getFormHM().get("distinct"));
        this.setType((String)this.getFormHM().get("type"));
        this.setSetname((String)this.getFormHM().get("setname"));
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setTabid((String)this.getFormHM().get("tabid"));        
        this.like="0";
        this.history="0";        
        this.setOrder((String)this.getFormHM().get("order"));
        this.setUplevel((String)this.getFormHM().get("uplevel"));
        this.setLikevalue((String)this.getFormHM().get("likevalue")); 
        this.setHistorysave((String)this.getFormHM().get("historysave"));
        this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
        String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	     this.setPhotolength(photolength);
        this.setCatelist((ArrayList)this.getFormHM().get("catelist"));
        this.setHidcategories((String)this.getFormHM().get("hidcategories"));
        this.setCategories((String)this.getFormHM().get("categories"));
        
        this.setPart_map((HashMap)this.getFormHM().get("part_map"));
        this.setUserbase((String)this.getFormHM().get("userbase"));
        this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
        this.setPart_unit((String)this.getFormHM().get("part_unit"));
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
        this.getFormHM().put("setname",this.getSetname());
        this.getFormHM().put("result",this.getResult());
        this.getFormHM().put("qobj",this.getQobj());
        this.getFormHM().put("type",this.getType());
        this.getFormHM().put("condname",this.getCondname());
        this.getFormHM().put("condid",this.getKeyid());
        this.getFormHM().put("role_id", getRole_id());
        this.getFormHM().put("likevalue",this.getLikevalue());
        this.getFormHM().put("historysave",this.getHistorysave()); 
        this.getFormHM().put("categories", this.getCategories());
        
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
        this.getFormHM().put("part_unit", this.getPart_unit());
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
    	}
    	else if("3".equals(type))
    	{
    		this.setDomainflag(Integer.toString(Constant.POS_FIELD_SET));
    	}   
    	else
    	{
    		this.setDomainflag(Integer.toString(Constant.ALL_FIELD_SET)); 
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


	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/workbench/query/hquerycond_interface".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}


	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if("/workbench/query/hquery_interface".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
//			this.like="0";
//			this.history="0";
//			this.result="0";
			this.setQobj("0");
			//if(this.factorlist!=null)
			//	this.factorlist.clear(); 
			if(this.fieldlist!=null)
				this.fieldlist.clear();
			this.right_fields=new String[0];
			this.setExpression("");
			//【835】人员范围授权：给一个自助用户进行高级授权，然后在给另外一个自助用户进行高级授权，保留了第一次的授权  jingq upd 2014.11.17
			//【3814】员工管理-查询浏览-简单查询，按岗位名称查询岗位名称等于A岗位的之后，进入通用查询，依然选择岗位名称这个指标，默认显示出上次查询的选项内容 A岗位，并且重置按钮不好使，点击了也不会清空查询指标的内容
			this.setFactorlist(new ArrayList());
		}
		//【6207】员工管理：通用查询，重新选择查询条件项后，在设置表达式页面，表达式栏中默认显示上次设置的表达式，建议去掉  jingq add 2014.12.29
		//a_query =1简单查询，=2通用查询，只有简单查询和通用查询时才清空因子表达式，否则用户管理-人员范围-高级中的因子表达式显示不正确。  jingq add 2015.02.12
		//right_fields[]
		String initialData = "";
		for (int i = 0; i < right_fields.length; i++) { //循环得到一维数组  		           
		    initialData = initialData + "*" + (i + 1); //将一维数组转化为字符串输出  
		}
		if (!"".equals(initialData)) initialData = initialData.substring(1);
		initialData = PubFunc.hireKeyWord_filter(initialData);
		if (initialData.equals(expression)) 
		{
		    if ("/workbench/query/hquery_interface".equals(arg0.getPath()) && (arg1.getParameter("b_next") != null) && (!"3".equals(arg1.getParameter("a_query"))))
		    {
		        this.setExpression("");
		    }
		} else {

		}
		this.like="0";
		this.history="0";
		this.result="0";		
		this.setKeyid("");	
		this.setCondname("");
		//this.home="";		

	}


	public String getDistinct() {
		return distinct;
	}


	public void setDistinct(String distinct) {
		this.distinct = distinct;
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
	public String getLikevalue() {
        return likevalue;
    }
    public void setLikevalue(String likevalue) {
        this.likevalue = likevalue;
    }
    
    public String getHistorysave() {
        return historysave;
    }
    public void setHistorysave(String historysave) {
        this.historysave = historysave;
    }


	public String getReturnvalue() {
		return returnvalue;
	}


	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}


	public String getCategories() {
		return categories;
	}


	public void setCategories(String categories) {
		this.categories = categories;
	}


	public ArrayList getCatelist() {
		return catelist;
	}


	public void setCatelist(ArrayList catelist) {
		this.catelist = catelist;
	}


	public String getHidcategories() {
		return hidcategories;
	}


	public void setHidcategories(String hidcategories) {
		this.hidcategories = hidcategories;
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


	public String getPhotolength() {
		return photolength;
	}


	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
}
