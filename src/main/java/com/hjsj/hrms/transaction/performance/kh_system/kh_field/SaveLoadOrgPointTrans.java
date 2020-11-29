package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

public class SaveLoadOrgPointTrans extends IBusiness{
	public void execute()throws GeneralException{
		String unitcode=(String)this.getFormHM().get("unitcode");
		String points=(String)this.getFormHM().get("points");
		points=points.substring(1);
		String[] temp=points.split(",");
		String[]	temp3=new String[10];
		AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		appb.init();
		appb.setReturnHt(null);
		Hashtable ht=appb.analyseParameterXml();
		String pointset_menu=(String)ht.get("pointset_menu");
	    String pointcode_menu=(String)ht.get("pointcode_menu");
	    String pointname_menu=(String)ht.get("pointname_menu");
	    
	    String	showmenus=(String)ht.get("showmenus");
	    if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
	    	unitcode=unitcode.substring(2);
	    }
	    String sql="select i9999,"+pointcode_menu+" from "+pointset_menu +"  where b0110='"+unitcode+"' and i9999=(select max(i9999) from "+pointset_menu+"  where b0110='"+unitcode+"')";
	    String i9999;
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    StringBuffer sb=new StringBuffer("insert into ");
	 
	    String temp1="(b0110,i9999,"+pointcode_menu+","+pointname_menu;
	    String values="values (?,?,?,?";
	    ArrayList list;
	    ArrayList alist=new ArrayList();
	    if(showmenus.trim().length()!=0){
		    temp3=showmenus.split(",");
		    for(int m=0;m<temp3.length;m++){
					temp1+=","+temp3[m];
					values+=",?";
			}
	    }
	    sb.append(pointset_menu+temp1+")"+values+")");
	    try {
	    	 this.frowset=dao.search(sql);
	    	 if(this.frowset.next()){
	    		 if(this.frowset.getString(pointcode_menu)!=null){
	    			  i9999=this.frowset.getString("i9999");
		    		 for(int i=0;i<temp.length;i++){
		    			 String sqlt="select pointname from per_point where point_id='"+temp[i]+"'";
		    				i9999=String.valueOf(Integer.parseInt(i9999)+1);
		    			 this.frowset=dao.search(sqlt);
		    			 if(this.frowset.next()){
		    				 list=new ArrayList();
			    			 String pointname=this.frowset.getString("pointname")==null?"":this.frowset.getString("pointname");
			    			 list.add(unitcode);
			    			 list.add(i9999);
			    			 list.add(temp[i]);
			    			 list.add(pointname);
			    			  if(showmenus.trim().length()!=0){
				    			 for(int m=0;m<temp3.length;m++){
					 					list.add(null);
					 			}
			    			  }
			    			 alist.add(list);
		    			 }
		    		 }
		    		
	    		 }
	    		
	    	 }else{
	    		 i9999="0";
	    		alist=this.getlist(unitcode+","+unitcode, pointset_menu, pointcode_menu, pointname_menu,showmenus,i9999);
	    		if(alist.size()!=0){
	    			LazyDynaBean bean=(LazyDynaBean)alist.get(alist.size()-1);
		    		alist.remove(alist.size()-1);
		    		i9999=(String)bean.get("i9999");
	    		}
	    		for(int i=0;i<temp.length;i++){
	    			 String sqlt="select pointname from per_point where point_id='"+temp[i]+"'";
	    			i9999=String.valueOf(Integer.parseInt(i9999)+1);
	    			 this.frowset=dao.search(sqlt);
	    			 if(this.frowset.next()){
	    				 list=new ArrayList();
		    			 String pointname=this.frowset.getString("pointname")==null?"":this.frowset.getString("pointname");
		    			 list.add(unitcode);
		    			 list.add(i9999);
		    			 list.add(temp[i]);
		    			 list.add(pointname);
		    			 if(showmenus.length()!=0){
			    			 
			    			 for(int m=0;m<temp3.length;m++){
				 					list.add(null);
				 				
				 			}
		    			 }
		    			 alist.add(list);
	    			 }
	    		 }
	    	 }
	    	 dao.batchInsert(sb.toString(), alist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    this.getFormHM().put("unitcode", unitcode);
	}
	public ArrayList getlist(String unitcode,String pointset_menu,String pointcode_menu, String pointname_menu,String showmenus,String i9999){
		ArrayList alist=new ArrayList();
		ArrayList list;
		String fiunitcode=unitcode.split(",")[0];
		String a_code=unitcode.split(",")[1];
		String sql1="select parentid from organization where codeitemid='"+a_code+"' and parentid<>codeitemid";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String code="";
		String[] temp2=new String [10];
		if(showmenus.trim().length()!=0){
			 temp2=showmenus.split(",");
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
				
				list.add(this.frowset.getString(pointcode_menu));
				list.add(this.frowset.getString(pointname_menu));
				if(showmenus.trim().length()!=0){
					for(int m=0;m<temp2.length;m++){
						list.add(this.frowset.getString(temp2[m]));
					}
				}
				alist.add(list);
			}
			if(alist.size()==0){
				if(code==null||code.trim().length()==0){
					return alist;
				}else{
					return getlist(fiunitcode+","+code,pointset_menu,pointcode_menu,pointname_menu,showmenus,i9999);
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
}
