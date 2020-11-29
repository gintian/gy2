/**
 * 
 */
package com.hjsj.hrms.actionform.general.query;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:QuickQueryForm</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-26:11:34:15</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QuickQueryForm extends FrameForm {

    /**应用库表前缀*/
    private String[] dbpre;
    /**查询字段列表*/
    private ArrayList fieldlist=new ArrayList();
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
    
    /**查询语句*/
    private String sql;
    private String cloums;
    private String orderby;
    /**显示应用库前缀,主要用于跨库查询*/
    private String show_dbpre;
    
	public String[] getDbpre() {
		return dbpre;
	}


	public void setDbpre(String[] dbpre) {
		this.dbpre = dbpre;
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


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
    public void outPutFormHM() {
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setShowlist((ArrayList)this.getFormHM().get("showlist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setCloums((String)this.getFormHM().get("cloums"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("result",this.getResult());
		this.getFormHM().put("history",this.getHistory());
		this.getFormHM().put("like",this.getLike());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("show_dbpre",this.getShow_dbpre());
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		if("/general/query/quick/quick_query".equals(mapping.getPath())){
			request.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		return super.validate(mapping, request);
	}

	public ArrayList getDblist() {
		return dblist;
	}


	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}


	public ArrayList getShowlist() {
		return showlist;
	}


	public void setShowlist(ArrayList showlist) {
		this.showlist = showlist;
	}


	public String getSetname() {
		return setname;
	}


	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setHistory("0");
		this.setLike("0");
		this.setResult("0");
		String[] temp=new String[1];
		this.setDbpre(temp);
//		
//		for(int i=0;i<this.fieldlist.size();i++)
//		{
//			FieldItem item=(FieldItem)this.fieldlist.get(i);
//			item.setValue("");
//			item.setViewvalue("");
//		}
	}


	public String getShow_dbpre() {
		return show_dbpre;
	}


	public void setShow_dbpre(String show_dbpre) {
		this.show_dbpre = show_dbpre;
	}


	public String getCloums() {
		return cloums;
	}


	public void setCloums(String cloums) {
		this.cloums = cloums;
	}


	public String getOrderby() {
		return orderby;
	}


	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

}
