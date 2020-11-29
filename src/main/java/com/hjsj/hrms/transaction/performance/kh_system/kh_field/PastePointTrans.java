package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class PastePointTrans extends IBusiness{
	public void execute()throws GeneralException{
		
		try {
			String plag=(String)this.getFormHM().get("plag");
			String sql;
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    String pointname_menu=(String)ht.get("pointname_menu");
		    String	showmenus=(String)ht.get("showmenus");
		    String[] temp1=new String[10];
		    if(showmenus.length()!=0){
		    	temp1=showmenus.split(",");
		    }
			String unitcode=(String)this.getFormHM().get("unticode");
			if("1".equals(plag)){
				String copypoints=(String)this.getFormHM().get("copypoints");
			
				if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
					unitcode=unitcode.substring(2);
				}else{
					
				}
				String[]temp=copypoints.split(",");
				if(temp[0].indexOf("UN")!=-1||temp[0].indexOf("UM")!=-1){
					temp[0]=temp[0].substring(2);
				}else{
					
				}
				this.pastedata(temp, "OrgPointTable", pointset_menu, pointcode_menu, pointname_menu, showmenus, unitcode);
			}else{
				String copyorg=(String)this.getFormHM().get("copyorg");
				if(copyorg.indexOf("UN")!=-1||copyorg.indexOf("UM")!=-1){
					copyorg=copyorg.substring(2);
				}else{
					
				}
				if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
					unitcode=unitcode.substring(2);
				}else{
					
				}
				this.pasteorg(copyorg, "OrgPointTable", pointset_menu, pointcode_menu, pointname_menu, showmenus, unitcode);
			}
			this.getFormHM().put("unitcode", unitcode);	
		} catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public ArrayList getlist2(String unitcode,String pointset_menu,String pointcode_menu, String pointname_menu,String i9999,String showmenus){
		ArrayList alist=new ArrayList();
		ArrayList list;
		String fiunitcode=unitcode.split(",")[0];
		String a_code=unitcode.split(",")[1];
		String sql1="select parentid from organization where codeitemid='"+a_code+"' and parentid<>codeitemid";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String code="";
		String[] temp1=new String[10];
		if(showmenus.length()!=0){
			temp1=showmenus.split(",");
		}
		try {
			this.frowset=dao.search(sql1);
			if(this.frowset.next()){
				code=this.frowset.getString("parentid");
				sql1="select * from "+pointset_menu+" where b0110='"+code+"'";
			}else{
				sql1="select * from "+pointset_menu+" where b0110='"+a_code+"'";
			}
			
			this.frowset=dao.search(sql1);
			while(this.frowset.next()){
				i9999=String.valueOf(Integer.parseInt(i9999)+1);
				list=new ArrayList();
				list.add(fiunitcode);
				list.add(i9999);
				list.add(this.frowset.getString(pointcode_menu)==null?"":this.frowset.getString(pointcode_menu));
				list.add(this.frowset.getString(pointname_menu)==null?"":this.frowset.getString(pointname_menu));
				if(showmenus.length()!=0){
					for(int k=0;k<temp1.length;k++){
						list.add(this.frowset.getString(temp1[k]));
					}
				}
				alist.add(list);
			}
			if(alist.size()==0){
				if(code==null||code.trim().length()==0){
					return alist;
				}else{
					return getlist2(fiunitcode+","+code,pointset_menu,pointcode_menu,pointname_menu,i9999,showmenus);
				}
			}else{
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("i9999",i9999);
				alist.add(bean);
				return	alist;
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return alist;
		
	}
	public HashMap getmap(String unitcode,String pointset_menu,String pointcode_menu,String pointname_menu){
		HashMap ham=new HashMap();
		if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
			unitcode=unitcode.substring(2);
		}else{
			
		}
		String sql ="select * from  "+pointset_menu +" where b0110='"+unitcode+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				ham.put(this.frowset.getString(pointcode_menu), "1");
				ham.put(this.frowset.getString(pointname_menu), "1");
			
			}
			if(ham.size()==0){
				sql="select parentid from organization  where codeitemid='"+unitcode+"' and parentid<>codeitemid";
				this.frowset=dao.search(sql);
			
				if(this.frowset.next()){
					unitcode=this.frowset.getString("parentid");
					return this.getmap(unitcode, pointset_menu, pointcode_menu, pointname_menu);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ham;
	}
	public void pastedata(String[] points,String tablename,String pointset_menu,String pointcode_menu,String pointname_menu,String showmenus,String unitcode){
		String sql="select * from "+tablename+" where b0110="+points[0]+" and username='"+this.userView.getUserName()+"' and i9999 in( ";
		ArrayList alist=new ArrayList();
		ArrayList list;
		String sql2="update "+pointset_menu+" set ";
		String whl=" where b0110=? and "+pointcode_menu.toLowerCase()+"=?";
		String sql3="insert into "+pointset_menu+"(b0110,i9999,"+pointcode_menu+","+pointname_menu;
		String values="values(?,?,?,?";
		HashMap hm=new HashMap();
		ArrayList list1=new ArrayList();
		ArrayList list2=new ArrayList();
		HashMap ham=this.getmap(unitcode, pointset_menu, pointcode_menu, pointname_menu);
		LazyDynaBean bean=new LazyDynaBean();
		try {
			if(showmenus.length()!=0){
				String [] tem=showmenus.split(",");
				for(int m=0;m<tem.length;m++){
					sql2+=tem[m].toLowerCase()+"=?,";
					sql3+=","+tem[m].toLowerCase();
					values+=",?";
				}
			
			}
			sql2=sql2.substring(0,sql2.length()-1);
			sql2+=whl;
			sql3+=")"+values+")";
			for(int i=1;i<points.length;i++){
				sql+=points[i]+",";
			}
			sql=sql.substring(0,sql.length()-1);
			sql+=")";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				list=new ArrayList();
				if(showmenus.length()!=0){
					String [] tem=showmenus.split(",");
					for(int m=0;m<tem.length;m++){
						list.add(this.frowset.getString(tem[m]));
					}
				
				}
				list.add(unitcode);
				if(ham.get(this.frowset.getString(pointcode_menu))!=null){
					list1.add(this.frowset.getString(pointcode_menu));
				}else{
					list2.add(this.frowset.getString(pointcode_menu));
				}
				list.add(this.frowset.getString(pointcode_menu));
				
				
				hm.put(this.frowset.getString(pointcode_menu), list);
				
			}
			sql="select * from "+pointset_menu+" where b0110="+unitcode;
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				String i9999="";
				sql="select max(i9999) from "+pointset_menu+" where b0110=" +unitcode;
				this.frowset=dao.search(sql);
				if(this.frowset.next())
					i9999=this.frowset.getString(1);
				if(list2.size()!=0){
					sql="select * from "+tablename+" where b0110="+points[0]+" and username='"+ this.userView.getUserName()+"' and "+pointcode_menu+" in('";
					for(int i=0;i<list2.size();i++){
						sql+=list2.get(i)+"','";
					}
					sql=sql.substring(0,sql.length()-2);
					sql+=")";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						list=new ArrayList();
						list.add(unitcode);
						i9999=String.valueOf(Integer.parseInt(i9999)+1);
						list.add(i9999);
						list.add(this.frowset.getString(pointcode_menu));
						list.add(this.frowset.getString(pointname_menu));
						if(showmenus.length()!=0){
							String [] tem=showmenus.split(",");
							for(int m=0;m<tem.length;m++){
								list.add(this.frowset.getString(tem[m]));
							}
						}
						alist.add(list);
					}
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}else{
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}
			}else{
				String i9999="0";
				alist=this.getlist2(unitcode+","+unitcode, pointset_menu, pointcode_menu, pointname_menu, i9999, showmenus);
				if(alist.size()!=0){
	    			bean=(LazyDynaBean)alist.get(alist.size()-1);
		    		alist.remove(alist.size()-1);
		    		i9999=(String)bean.get("i9999");
	    		}
				if(list2.size()!=0){
					sql="select * from "+tablename+" where b0110="+points[0]+" and username='"+ this.userView.getUserName()+"' and "+pointcode_menu+" in(";
					for(int i=0;i<list2.size();i++){
						sql+="'"+list2.get(i)+"',";
					}
					sql=sql.substring(0,sql.length()-1);
					sql+=")";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						list=new ArrayList();
						list.add(unitcode);
						i9999=String.valueOf(Integer.parseInt(i9999)+1);
						list.add(i9999);
						list.add(this.frowset.getString(pointcode_menu));
						list.add(this.frowset.getString(pointname_menu));
						if(showmenus.length()!=0){
							String [] tem=showmenus.split(",");
							for(int m=0;m<tem.length;m++){
								list.add(this.frowset.getString(tem[m]));
							}
						}
						alist.add(list);
					}
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}else{
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void pasteorg(String copyorg,String tablename,String pointset_menu,String pointcode_menu,String pointname_menu,String showmenus,String unitcode) throws GeneralException{
		HashMap ham=this.getmap(unitcode, pointset_menu, pointcode_menu, pointname_menu);
		String sql="select * from "+tablename+" where b0110="+copyorg+" and username='"+this.userView.getUserName()+"'";
		HashMap hm=new HashMap();
		ArrayList list;
		ArrayList list1=new ArrayList();
		ArrayList list2=new ArrayList();
		ArrayList alist=new ArrayList();
		String sql2="update "+pointset_menu+" set ";
		String whl=" where b0110=? and "+pointcode_menu.toLowerCase()+"=?";
		String sql3="insert into "+pointset_menu+"(b0110,i9999,"+pointcode_menu+","+pointname_menu;
		String values="values(?,?,?,?";
		LazyDynaBean bean=new LazyDynaBean();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			if(showmenus.length()!=0){
				String [] tem=showmenus.split(",");
				for(int m=0;m<tem.length;m++){
					sql2+=tem[m].toLowerCase()+"=?,";
					sql3+=","+tem[m].toLowerCase();
					values+=",?";
				}
			
			}
			sql2=sql2.substring(0,sql2.length()-1);
			sql2+=whl;
			sql3+=")"+values+")";
			
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				list=new ArrayList();
				if(showmenus.length()!=0){
					String [] tem=showmenus.split(",");
					for(int m=0;m<tem.length;m++){
						list.add(this.frowset.getString(tem[m]));
					}
				
				}
				list.add(unitcode);
				if(ham.get(this.frowset.getString(pointcode_menu))!=null){
					list1.add(this.frowset.getString(pointcode_menu));
				}else{
					list2.add(this.frowset.getString(pointcode_menu));
				}
				list.add(this.frowset.getString(pointcode_menu));
				
				
				hm.put(this.frowset.getString(pointcode_menu), list);
				
			}
			if(hm.size()==0){
				throw GeneralExceptionHandler.Handle(new Exception("复制机构不包含考核指标！"));
			}
			sql="select * from "+pointset_menu+" where b0110="+unitcode;
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				String i9999="";
				sql="select max(i9999) from "+pointset_menu+" where b0110=" +unitcode;
				this.frowset=dao.search(sql);
				if(this.frowset.next())
					i9999=this.frowset.getString(1);
				if(list2.size()!=0){
					sql="select * from "+tablename+" where b0110="+copyorg+" and username='"+ this.userView.getUserName()+"' and "+pointcode_menu+" in(";
					for(int i=0;i<list2.size();i++){
						sql+="'"+list2.get(i)+"',";
					}
					sql=sql.substring(0,sql.length()-1);
					sql+=")";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						list=new ArrayList();
						list.add(unitcode);
						i9999=String.valueOf(Integer.parseInt(i9999)+1);
						list.add(i9999);
						list.add(this.frowset.getString(pointcode_menu));
						list.add(this.frowset.getString(pointname_menu));
						if(showmenus.length()!=0){
							String [] tem=showmenus.split(",");
							for(int m=0;m<tem.length;m++){
								list.add(this.frowset.getString(tem[m]));
							}
						}
						alist.add(list);
					}
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}else{
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}
			}else{
				String i9999="0";
				alist=this.getlist2(unitcode+","+unitcode, pointset_menu, pointcode_menu, pointname_menu, i9999, showmenus);
				if(alist.size()!=0){
	    			bean=(LazyDynaBean)alist.get(alist.size()-1);
		    		alist.remove(alist.size()-1);
		    		i9999=(String)bean.get("i9999");
	    		}
				if(list2.size()!=0){
					sql="select * from "+tablename+" where b0110="+copyorg+" and username='"+ this.userView.getUserName()+"' and "+pointcode_menu+" in(";
					for(int i=0;i<list2.size();i++){
						sql+="'"+list2.get(i)+"',";
					}
					sql=sql.substring(0,sql.length()-1);
					sql+=")";
					this.frowset=dao.search(sql);
					while(this.frowset.next()){
						list=new ArrayList();
						list.add(unitcode);
						i9999=String.valueOf(Integer.parseInt(i9999)+1);
						list.add(i9999);
						list.add(this.frowset.getString(pointcode_menu));
						list.add(this.frowset.getString(pointname_menu));
						if(showmenus.length()!=0){
							String [] tem=showmenus.split(",");
							for(int m=0;m<tem.length;m++){
								list.add(this.frowset.getString(tem[m]));
							}
						}
						alist.add(list);
					}
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}else{
					dao.batchInsert(sql3, alist);
					if(list1.size()!=0){
						if(showmenus.length()!=0){
							ArrayList temp=new ArrayList();
							for(int i=0;i<list1.size();i++){
								temp.add(hm.get(list1.get(i)));
							}
							dao.batchUpdate(sql2, temp);
						}else{
							
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
