package com.hjsj.hrms.businessobject.sys.options.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class QueryTemplateBo {
	private Connection conn;
	public QueryTemplateBo(Connection conn){
		this.conn = conn;
	}
	/*
	 *  取得通知摸板列表
	 *  @return
	 */
	public ArrayList getQueryTemplateList(){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs = dao.search("select * from t_sys_msgtemplate order by template_id");
			while(rs.next()){
				LazyDynaBean ldbean = new LazyDynaBean();
				ldbean.set("name",rs.getString("name"));
				ldbean.set("zploop",rs.getString("zploop"));
				ldbean.set("title",rs.getString("title"));
				ldbean.set("address",rs.getString("adress"));
				ldbean.set("template_type",new Integer(rs.getInt("template_type")));
				list.add(ldbean);
	
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getZbj_list(String sql,String id,String desc,RowSet frowset){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(conn);
		 
		   frowset = dao.search(sql);
		    while(frowset.next()){
		    	
				CommonData vo=new CommonData(frowset.getString(id),frowset.getString(desc));
				list.add(vo);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	public ArrayList getZb_list(String sql,String id,String desc,RowSet frowset){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(conn);
			frowset = dao.search(sql);
			while(frowset.next()){
				CommonData vo = new CommonData(frowset.getString(id),frowset.getString(desc));
				list.add(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	

}
