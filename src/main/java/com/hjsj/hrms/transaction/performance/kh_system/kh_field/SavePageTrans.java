package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class SavePageTrans extends IBusiness{
	public void execute() throws GeneralException{
		ArrayList list=(ArrayList)this.getFormHM().get("data_table_record");
		ContentDAO dao=null;
		String b0110a="";
		String b0110="";
		ArrayList alist=new ArrayList();
		try {
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    String pointname_menu=(String)ht.get("pointname_menu");
		    String	showmenus=(String)ht.get("showmenus");
			if(!(list==null||list.size()==0)){
				for(int i=0;i<list.size();i++)
				{
					    RecordVo vo=(RecordVo)list.get(i);
						String info=getInfo(vo);
						b0110=vo.getString("b0110");
						if(info.length()>1)
							throw new GeneralException(info.toString());
				}
				dao=new ContentDAO(this.getFrameconn());
				for(int k=0;k<list.size();k++)
				{
					RecordVo vo=(RecordVo)list.get(k);
					dao.updateValueObject(vo);
				}
				String sql="select * from "+pointset_menu+" where b0110='"+b0110+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					sql="delete from "+pointset_menu+" where b0110='"+b0110+"'";
					dao.delete(sql, alist);
					this.movedata(b0110, pointset_menu);
				}else{
					this.movedata(b0110, pointset_menu);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getInfo(RecordVo vo) throws GeneralException{
		String info="";
 		AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		appb.init();
		appb.setReturnHt(null);
		Hashtable ht=appb.analyseParameterXml();
		String pointset_menu=(String)ht.get("pointset_menu");
	    String pointcode_menu=(String)ht.get("pointcode_menu");
	    String pointname_menu=(String)ht.get("pointname_menu");
	    String	showmenus=(String)ht.get("showmenus");
	   
	    String t="";
	    HashMap hm=vo.getValues();
	    double tr;
	    String e="";
	    if(showmenus!=null&&showmenus.trim().length()>0&&showmenus.indexOf(",")!=-1){
	    	 String[] temp=showmenus.split(",");
	        for(int k=0;k<temp.length;k++){
			    DataDictionary da=new DataDictionary();
			    FieldItem item=da.getFieldItem(temp[k]);
			    String itemtype=item.getItemtype();
			    if("N".equals(itemtype)){
			    	t=hm.get(temp[k].toLowerCase()).toString();
			    	if(t.indexOf("E")!=-1){
			    		int index=t.indexOf("E");
			    		e=t.substring(index+1);
			    		if(e.indexOf("-")==-1){
			    			if(Integer.parseInt(e)>7){
			    				info="存在不符合规范数据!";
			    			}
			    		}else{
			    			if(Integer.parseInt(e.substring(1))>3){
			    				info="存在不符合规范数据!";
			    			}
			    		}
			    	}
			    }
		    }
	    }
	
		return info;
	}
	public ArrayList insert(ArrayList listvo,String b0110a,String pointset_menu,String pointcode_menu,String pointname_menu,String showmenus){
		ResultSet rs=null;
		Connection con=null;
		
		String parentid="";
		ArrayList list;
		String b0110=b0110a.split(",")[0];
		
		String unitcode=b0110a.split(",")[1];
		String sql="select parentid from organization where codeitemid='"+unitcode+"'";
		ArrayList alist=new ArrayList();
		String[] temp=new String [10];
		if(showmenus.length()!=0){
			temp=showmenus.split(",");
		}
		try {
			con=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs=dao.search(sql);
			if(rs.next()){
				parentid=rs.getString("parentid");
			}
			if(parentid.equalsIgnoreCase(b0110a)){
				
			}else{
				sql="select * from "+pointset_menu +" where b0110='"+parentid+"'";
				rs=dao.search(sql);
				while(rs.next()){
					list=new ArrayList();
					list.add(b0110);
					list.add(rs.getString("i9999"));
					list.add(rs.getString(pointcode_menu));
					list.add(rs.getString(pointname_menu));
					if(showmenus.length()!=0){
						for(int i=0;i<temp.length;i++)
							list.add(rs.getString(temp[i]));
					}
					alist.add(list);
				}
				if(alist.size()==0){
					return this.insert(listvo, b0110+","+parentid, pointset_menu, pointcode_menu, pointname_menu, showmenus);
				}else{
					return alist;
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
	    }
		return alist;
	}
	public void movedata(String unitcode,String tablename){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer str=new StringBuffer();
		StringBuffer str2=new StringBuffer();
		RowSet rowSet = null;
		FieldItem item;
		ArrayList list=new ArrayList();
		try {
			String sql="select * from OrgPointTable where 1=2";
		
			rowSet=dao.search(sql);
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{	
				if("username".equalsIgnoreCase(mt.getColumnName(i+1))){
					continue;
				}
				str.append(mt.getColumnName(i+1)+",");
				
			}
			str.setLength(str.length()-1);
			sql="insert into "+tablename+"("+str.toString()+") select "+str.toString()+ " from OrgPointTable where b0110='" +unitcode+"'and username='"+this.userView.getUserName()+"'";
			dao.insert(sql, list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
