/*
 * Created on 2011-2-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liweichao
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgDeptPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//ArrayList fieldlist=(ArrayList)this.getFormHM().get("infofieldlist");	
		String pretype=(String)this.getFormHM().get("pretype");	
		String orgparentcode=(String)this.getFormHM().get("orgparentcodestart");	
		String deptparentcode=(String)this.getFormHM().get("deptparentcodestart");	
    	String posparentcode=(String)this.getFormHM().get("posparentcodestart");	
    	
		if("UN".equalsIgnoreCase(pretype))
		{
		   //为了在选择代码时方便而压入权限码开始
			String temp="";
			if(!userView.isSuper_admin()){
				if(userView.getStatus()==4)
					temp=this.getUserView().getManagePrivCodeValue();
				else{
					String codeall = userView.getUnit_id();
					if(codeall!=null&&codeall.length()>2)
						temp=codeall;//.split("`")[0].substring(2);
					if("".equals(temp))
						temp=this.getUserView().getManagePrivCodeValue();
				}
			}else
				temp=this.getUserView().getManagePrivCodeValue();
			this.getFormHM().put("orgparentcode",temp);
		   //为了在选择代码时方便而压入权限码结束
		    //System.out.println("a" + orgparentcode);
			this.getFormHM().put("deptparentcode",orgparentcode);
			this.getFormHM().put("posparentcode",orgparentcode);
	    }else if("UM".equalsIgnoreCase(pretype))
		{
	    	//System.out.println("ddd" + orgparentcode);
    	    /*if(orgparentcode==null || (orgparentcode!=null && orgparentcode.trim().length()==0))
		    {*/
				if(deptparentcode!=null && deptparentcode.trim().length()>0)
				{
					List savePos=getStationPos(deptparentcode,"UM");
					for(int n=0;n<savePos.size();n++)
					{
						StationPosView posview=(StationPosView)savePos.get(n);
						if("b0110".equalsIgnoreCase(posview.getItem()))
						{
							String temp="";
							if(!userView.isSuper_admin()){
								if(userView.getStatus()==4)
									temp=this.getUserView().getManagePrivCodeValue();
								else{
									String codeall = userView.getUnit_id();
									if(codeall!=null&&codeall.length()>2)
										temp=codeall;//.split("`")[0].substring(2);
									if("".equals(temp))
										temp=this.getUserView().getManagePrivCodeValue();
								}
							}else
								temp=this.getUserView().getManagePrivCodeValue();
							this.getFormHM().put("orgparentcode",temp);
							this.getFormHM().put("orgvalue",posview.getItemvalue());
							this.getFormHM().put("orgviewvalue",posview.getItemviewvalue());
							//this.getFormHM().put("orgparentcode",userView.getManagePrivCodeValue());
							this.getFormHM().put("deptparentcode",posview.getItemvalue());
							this.getFormHM().put("posparentcode",deptparentcode);
							break;
						}					
					}
				}
		   /*}else
			    {
				this.getFormHM().put("orgvalue","");
				this.getFormHM().put("orgviewvalue","");
				this.getFormHM().put("deptparentcode","");
				this.getFormHM().put("orgparentcode",userView.getManagePrivCodeValue());
				this.getFormHM().put("posparentcode",deptparentcode);
		   }*/
		}else if("@K".equalsIgnoreCase(pretype))
		{
			//if(orgparentcode==null || (orgparentcode!=null && orgparentcode.trim().length()==0))
		   // {
		    	if(posparentcode!=null && posparentcode.trim().length()>0)
				{
					List savePos=getStationPos(posparentcode,"@K");
					for(int n=0;n<savePos.size();n++)
					{
						StationPosView posview=(StationPosView)savePos.get(n);
						if("b0110".equalsIgnoreCase(posview.getItem()))
						{
							String temp="";
							if(!userView.isSuper_admin()){
								if(userView.getStatus()==4)
									temp=this.getUserView().getManagePrivCodeValue();
								else{
									String codeall = userView.getUnit_id();
									if(codeall!=null&&codeall.length()>2)
										temp=codeall.split("`")[0].substring(2);
									else if("".equals(temp))
										temp=this.getUserView().getManagePrivCodeValue();
								}
							}else
								temp=this.getUserView().getManagePrivCodeValue();
							this.getFormHM().put("orgparentcode",temp);
							this.getFormHM().put("orgvalue",posview.getItemvalue());
							this.getFormHM().put("orgviewvalue",posview.getItemviewvalue());
							this.getFormHM().put("deptparentcode",posview.getItemvalue());
							//this.getFormHM().put("orgparentcode",userView.getManagePrivCodeValue());
							this.getFormHM().put("posparentcode",posview.getItemvalue());
							//break;
						}
						if("e0122".equalsIgnoreCase(posview.getItem()))
						{
							this.getFormHM().put("deptvalue",posview.getItemvalue());
							this.getFormHM().put("deptviewvalue",posview.getItemviewvalue());
							this.getFormHM().put("posparentcode",posview.getItemvalue());
							//break;
						}		
					}
				}
			//}
			//if(deptparentcode==null || (deptparentcode!=null && deptparentcode.trim().length()==0))
		    //{
				if(posparentcode!=null && posparentcode.trim().length()>0)
				{
					List savePos=getStationPos(posparentcode,"@K");
					for(int n=0;n<savePos.size();n++)
					{
						StationPosView posview=(StationPosView)savePos.get(n);
						if("e0122".equalsIgnoreCase(posview.getItem()))
						{
							String temp="";
							if(!userView.isSuper_admin()){
								if(userView.getStatus()==4)
									temp=this.getUserView().getManagePrivCodeValue();
								else{
									String codeall = userView.getUnit_id();
									if(codeall!=null&&codeall.length()>2)
										temp=codeall.split("`")[0].substring(2);
									else if("".equals(temp))
										temp=this.getUserView().getManagePrivCodeValue();
								}
							}else
								temp=this.getUserView().getManagePrivCodeValue();
							this.getFormHM().put("orgparentcode",temp);
							this.getFormHM().put("deptvalue",posview.getItemvalue());
							this.getFormHM().put("deptviewvalue",posview.getItemviewvalue());
							this.getFormHM().put("posparentcode",posview.getItemvalue());
							//this.getFormHM().put("orgparentcode",userView.getManagePrivCodeValue());
							break;
						}		
					}
				}
			//}
		}
	}
	private ArrayList getStationPos(String code,String pre)
	{
		//System.out.println("pos" + code + kind);
		ArrayList poslist=new ArrayList();
	    Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
			if("UN".equals(pre))
			{
				strsql.append("select * from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");		
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				rs =db.search(strsql.toString());	
				if(rs.next())
				{
					StationPosView posview=new StationPosView();
					posview.setItem("b0110");
					posview.setItemvalue(rs.getString("codeitemid"));
					posview.setItemviewvalue(rs.getString("codeitemdesc"));
					poslist.add(posview);
				}
			}
			else
			{
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				while(!"UN".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					rs =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(rs.next())
					{
						StationPosView posview=new StationPosView();
						pre=rs.getString("codesetid");
						if("@K".equalsIgnoreCase(pre))
						{
							if(ispos==false)
							{
							  posview.setItem("e01a1");
							  posview.setItemvalue(rs.getString("codeitemid"));
							  posview.setItemviewvalue(rs.getString("codeitemdesc"));
							  ispos=true;
							  poslist.add(posview);
							}
						}else if("UM".equalsIgnoreCase(pre))
						{
							if(isdep==false)
							{
							  posview.setItem("e0122");
							  posview.setItemvalue(rs.getString("codeitemid"));
							  posview.setItemviewvalue(rs.getString("codeitemdesc"));
							  isdep=true;
							  poslist.add(posview);
							}
						}else if("UN".equalsIgnoreCase(pre))
						{
							if(isorg==false)
							{
	  						  posview.setItem("b0110");
							  posview.setItemvalue(rs.getString("codeitemid"));
							  posview.setItemviewvalue(rs.getString("codeitemdesc"));
							  isorg=true;
							  poslist.add(posview);
							}
						}
						code=rs.getString("parentid");	
					}			
				}				
			  }
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}		
			finally{
				try{
					//if (rs != null){
					//	rs.close();
					//}
					if (stmt != null){
						stmt.close();
					}				
				}catch (SQLException sql){
					sql.printStackTrace();
				}
			}

		return poslist;
	}
}
