package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.CreateKhTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class LoadOrgPointTrans extends IBusiness{
	public void execute()throws GeneralException{
	    try
		{	
			HashMap hm=  (HashMap)this.getFormHM().get("requestPamaHM");
			String a_code=(String)hm.get("a_code");
			String flag=(String)hm.get("flag");
			hm.remove("a_code");
			hm.remove("flag");
			if(a_code==null || a_code.trim().length()<=0)
				a_code = "00";
			String b0110="";
			String info="";
			String sql1="";
			if("UN".equalsIgnoreCase(a_code)){
				throw GeneralExceptionHandler.Handle(new Exception("请选择正确的组织机构！"));
			}
			 if(a_code.indexOf("UN")!=-1||a_code.indexOf("UM")!=-1){
				 b0110=a_code.substring(2);
			 }else{
				 b0110=a_code;	
			 }
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    String pointname_menu=(String)ht.get("pointname_menu");
		    String	showmenus=(String)ht.get("showmenus");
		    DbWizard dbwizard=new DbWizard(this.getFrameconn());
		    if(dbwizard.isExistTable(pointset_menu,false)){
			    CreateKhTableBo ckt=new CreateKhTableBo(this.getFrameconn(),pointset_menu,this.getUserView());
			    ckt.createTable();
		    
			    
			    String sql="select  b0110,i9999,";
			    String tablename=pointset_menu;
			    sql+=pointcode_menu+","+pointname_menu;
			    String whsql=" where b0110='";
			    info=this.getInfo(b0110, pointset_menu, "0");
			    if(showmenus==null||showmenus.trim().length()==0){
			    	
			    }else{
				    String[] temp=showmenus.split(",");
				    for(int i=0;i<temp.length;i++){
				    	sql+=","+temp[i];
				    }
			    }
			    String sql2="";
			    sql1+=sql;
			    sql+=" from  "+pointset_menu;
			    sql1+=",username from OrgPointTable";
			    String temp= this.search(sql, whsql, b0110,this.getFrameconn(),sql2);
			    sql2=temp.split("`")[0];
			    String unitcode=temp.split("`")[1];
			    ckt.copyData(b0110,unitcode);
			    sql1+=whsql+b0110+"' and username='"+this.userView.getUserName()+"'";
			    ArrayList list=DataDictionary.getFieldList(pointset_menu,Constant.USED_FIELD_SET);
			    ArrayList fieldlist=new ArrayList();
			    fieldlist.add("0");
			    fieldlist.add("0");
			    for(int i=0;i<list.size();i++){
			    	FieldItem fi=(FieldItem)list.get(i);
			    	Field field=(Field)fi.cloneField();
			    	if("0".equals(fi.getState())){
			    		field.setVisible(false);
			    		
			    	}
			    	if(fi.getItemid().equalsIgnoreCase(pointcode_menu)||fi.getItemid().equalsIgnoreCase(pointname_menu)){
			    		field.setReadonly(true);
			    		field.setVisible(true);
			    		
			    		if(fi.getItemid().equalsIgnoreCase(pointcode_menu)){
			    			field.setLabel("指标编号");
			    			fieldlist.set(0, field);
			    		}else{
			    			field.setLabel("指标名称");
			    			fieldlist.set(1, field);
			    			
			    		}
			    		
			    	}
			    	if(showmenus.toLowerCase().indexOf(fi.getItemid())!=-1){
			    		field.setVisible(true);
			    		field.setAlign("center");
			    		fieldlist.add(field);
			    		
			    	}
			    }
			    Field nfield=new Field("b0110","");
			    nfield.setVisible(false);
			    fieldlist.add(nfield);
			    
			    nfield=new Field("username","");
			    nfield.setVisible(false);
			    fieldlist.add(nfield);
			    
			    nfield=new Field("i9999","");
			    nfield.setVisible(false);
			    fieldlist.add(nfield);
			    
			    
			    this.getFormHM().put("sql", sql1);
			    this.getFormHM().put("tablename", "OrgPointTable");
			    this.getFormHM().put("fieldlist", fieldlist);
			    this.getFormHM().put("info", info);
			    this.getFormHM().put("unitcode", b0110);
		    }else{
		    	hm.put("showmenus", "");
				hm.put("pointcode_menu", "");
				hm.put("pointname_menu", "");
				String nodename="ORG_POINT";
				appb.init();
				appb.setParam(nodename, "", hm);
				appb.saveParam();
				this.getFormHM().put("orgpoint", "");
				throw new GeneralException("指定的考核子集已经不存在，请重新指定！");
				
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	    
	}
	public String search(String sql,String where,String b0110,Connection con,String sql2){
    	ContentDAO cdao=new ContentDAO(con);
    	String temp=sql+where + b0110+"' order by i9999";
    	
    	try {
    		
			this.frowset=cdao.search(temp);
			if(this.frowset.next()){
				return temp+"`"+b0110;
	    	}else{
	    		String parentid="";
	    		this.frowset=cdao.search("select parentid from organization where codeitemid='"+b0110+"' and parentid<>codeitemid");
	    		if(this.frowset.next()){
	    			parentid=this.frowset.getString("parentid");
	    			return search(sql,where,parentid,this.getFrameconn(),sql2);
	    		}else
	    		{
	    			return temp+"`"+b0110;
	    		}
	    		
	    	}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	
		return temp;
    }
	String getInfo(String unitcode,String pointset_menu,String flag) throws SQLException{
		String info="当前机构继承上级机构的指标";
		String sql="select *from "+pointset_menu+" where b0110='"+unitcode+"'";
		ResultSet rs=null;
		Connection con=null;
		String parentid="";
		try {
			if(unitcode.trim().length()==0){
				return "";
			}
			con=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs=dao.search(sql);
			if(rs.next()){
				if("0".equals(flag)){
					return "";
				}else{
					return info;
				}
			}else{
				
				sql="select parentid from organization  where codeitemid='"+unitcode+"'";
				rs=dao.search(sql);
				if(rs.next()){
					parentid=rs.getString("parentid");
					if(parentid.equalsIgnoreCase(unitcode)){
						return "";
					}else{
						return this.getInfo(parentid, pointset_menu, "1");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
		}
		return info;
	}
}
