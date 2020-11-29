package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.SQLException;

public class ViewHideSortBo {
	private String hideitemid="";
	private String sortitem="";
	private String fieldsetid="";
	private UserView userView=null;
	ContentDAO dao = null;
	public ViewHideSortBo(ContentDAO dao,UserView userView,String fieldsetid){
		this.dao = dao;
		this.userView =userView;
		this.fieldsetid = fieldsetid;
		init();
	}
	private void init(){
		String  viewhide="";
		String sort = "";
		StringBuffer buf =  new StringBuffer();
		buf.append("select Display_order,Display_hide from t_sys_viewplan where Username='");
		buf.append(this.userView.getUserName());
		buf.append("' and Setid='");
		buf.append(fieldsetid);
		buf.append("'");
		RowSet rs;
		try {
			rs = dao.search(buf.toString());
			if(rs.next()){
				viewhide = rs.getString("Display_hide");
				viewhide=viewhide!=null?viewhide:"";
				sort = rs.getString("Display_order");
				sort=sort!=null?sort:"";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(viewhide.trim().length()>4){
			this.setHideitemid(viewhide);
		}else{
			if(!this.userView.isSuper_admin()) {
                initSu("Display_hide");
            }
		}
		if(sort.trim().length()>4){
			this.setSortitem(sort);
		}else{
			if(!this.userView.isSuper_admin()) {
                initSu("Display_order");
            }
		}
	}
	private void initSu(String itemid){
		String  sortviewhide="";
		StringBuffer buf =  new StringBuffer();
		buf.append("select "+itemid+" from t_sys_viewplan where Username='su'");
		buf.append(" and Setid='");
		buf.append(fieldsetid);
		buf.append("'");
		RowSet rs;
		try {
			rs = dao.search(buf.toString());
			if(rs.next()){
				sortviewhide = rs.getString(itemid);
				sortviewhide=sortviewhide!=null?sortviewhide:"";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if("Display_order".equalsIgnoreCase(itemid)) {
            this.setSortitem(sortviewhide);
        } else {
            this.setHideitemid(sortviewhide);
        }
		
	}
	public String getHideitemid() {
		return hideitemid;
	}
	public void setHideitemid(String hideitemid) {
		this.hideitemid = hideitemid;
	}
	public String getSortitem() {
		return sortitem;
	}
	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}
	
}
