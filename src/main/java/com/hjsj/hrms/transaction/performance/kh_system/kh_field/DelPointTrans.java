package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

public class DelPointTrans extends IBusiness{
	public void execute()throws GeneralException{
		
		try {
			String b0110=(String)this.getFormHM().get("unitcode");
			String dflag=(String)this.getFormHM().get("dflag");
			String orgpoint=(String)this.getFormHM().get("orgpoint");
			String i9999=(String)this.getFormHM().get("i9999");
			String sql="";
			ContentDAO dao =new ContentDAO(this.getFrameconn());
			if("1".equalsIgnoreCase(dflag.trim())){
				String khpid=(String)this.getFormHM().get("khpid");
				String delpoints=(String)this.getFormHM().get("delpoints");
				
				sql="delete from "+ orgpoint+" where b0110=?  and i9999=?";
				delpoints=delpoints.substring(1,delpoints.length());
				i9999=i9999.substring(1,i9999.length());
				if(b0110.indexOf("UN")!=-1||b0110.indexOf("UM")!=-1){
					b0110=b0110.substring(2);
				}else{
					
				}
				String sql2="select * from "+orgpoint +" where b0110='"+b0110+"'";
				this.frowset=dao.search(sql2);
				if(this.frowset.next()){
					String[] temp=delpoints.split(",");
					String [] tt=i9999.split(",");
					ArrayList list;
					ArrayList alist=new ArrayList();
					for(int i=0;i<temp.length;i++){
						list=new ArrayList();
						list.add(b0110);
						
						
						list.add(tt[i]);
						dao.delete(sql, list);
					}	
				}else{
					this.delete2(delpoints, b0110+","+b0110, orgpoint);
					this.getFormHM().put("unitcode", b0110);
				}
				
			}
			
			if("2".equalsIgnoreCase(dflag.trim())){
				if(b0110.indexOf("UN")!=-1||b0110.indexOf("UM")!=-1){
					b0110=b0110.substring(2);
				}
				
				ArrayList list=new ArrayList();
				list.add(b0110);
				sql="select 1 from "+orgpoint +" where b0110=?";
				this.frowset = dao.search(sql, list);
				if(this.frowset.next()){
				    sql="delete from "+orgpoint +" where b0110=?";
	                dao.delete(sql, list);
				}else{
				    throw new GeneralException("",ResourceFactory.getProperty("kh.field.orgpoint.extends"),"","");
				}
				
			}
			
			if("3".equalsIgnoreCase(dflag.trim())){
				
				if(b0110.indexOf("UN")!=-1||b0110.indexOf("UM")!=-1){
					b0110=b0110.substring(2);
				}else{
					
				}
				this.delete(b0110, orgpoint);
			}
			this.getFormHM().put("unitcode", b0110);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
 	}
	public void delete(String unitcode,String orgpoint) throws GeneralException{
		
		Connection con=null;
		ResultSet rs=null;
		String sql="delete from "+orgpoint +" where b0110='"+unitcode+ "' or b0110 in(select codeitemid from organization where parentid='"+unitcode+"')" ;
		ArrayList list=new ArrayList();
		try{
			con=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			dao.update(sql);
			sql="select codeitemid from organization where parentid='"+unitcode+"' and parentid<>codeitemid";
			rs=dao.search(sql);
			while(rs.next()){
				list.add(rs.getString("codeitemid"));
			}
			
			if(list.size()==0){
			    throw new GeneralException("",ResourceFactory.getProperty("kh.field.orgpoint.empty"),"","");
			}
			
            for (int i = 0; i < list.size(); i++) {
                delete((String) list.get(i), orgpoint);
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
		}
		
	}
	public void delete2(String delpoints,String unitcode,String orgpoint){
		
		ArrayList list;
		ArrayList alist=new ArrayList();
		String[] temp=delpoints.split(",");
		String unitcode1=unitcode.split(",")[0];
		String unitcode2=unitcode.split(",")[1];
		
		AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		appb.init();
		appb.setReturnHt(null);
		Hashtable ht=appb.analyseParameterXml();
		String pointset_menu=(String)ht.get("pointset_menu");
	    String pointcode_menu=(String)ht.get("pointcode_menu");
	    String pointname_menu=(String)ht.get("pointname_menu");
	    String	showmenus=(String)ht.get("showmenus");
	    String i9999="0";
	    int flag=0;
	    String[] ll=new String[10];
	    if(showmenus.length()>0){
	    	ll= showmenus.split(",");
	    }
	   String values=" values(?,?,?,?";
	   String sql2="select parentid from organization where codeitemid='"+unitcode2+"'and parentid<>codeitemid";
		String sql="select * from "+orgpoint+" where b0110=(select parentid from organization where codeitemid='"+unitcode2+"'and parentid<>codeitemid)";
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql2);
			if(this.frowset.next()){
				this.frowset=dao.search(sql);
			}else{
				this.frowset=dao.search("select * from "+orgpoint+" where b0110='"+unitcode2+"'");
			}
			
			while(this.frowset.next()){
				flag++;
				if(delpoints.indexOf(this.frowset.getString(pointcode_menu))!=-1){
					
					continue;
				}else{
					list=new ArrayList();
					list.add(unitcode1);
					i9999=String.valueOf(Integer.parseInt(i9999)+1);
					list.add(i9999);
					list.add(this.frowset.getString(pointcode_menu));
					list.add(this.frowset.getString(pointname_menu));
					if(showmenus.length()!=0){
						for(int i=0;i<ll.length;i++){
							list.add(this.frowset.getString(ll[i]));
						}
					}
					alist.add(list);
				}
				
			}
			if(flag==delpoints.split(",").length){
				return;
			}
			if(alist.size()==0){
				String unitcode3="";
				sql="select parentid from organization where codeitemid='"+unitcode2+"'and parentid<>codeitemid";
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					unitcode3=this.frowset.getString("parentid");
				}else{
					unitcode3=unitcode2;
				}
				delete2( delpoints, unitcode1+","+unitcode3,orgpoint);
			}
			else{
				sql="insert into "+orgpoint+" (b0110,i9999,"+pointcode_menu+","+pointname_menu;
				if(showmenus.length()!=0){
					for(int k=0;k<ll.length;k++){
						sql+=","+ll[k];
						values+=",?";
					}
					
				
				}
				sql+=") "+values+")";
				dao.batchInsert(sql, alist);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
		
