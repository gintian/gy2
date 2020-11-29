package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class DeleteFieldClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			String subsys_id=(String)this.getFormHM().get("subsys_id");	
			StringBuffer ids = new StringBuffer("");
			String msg="1";


			Connection con=null;
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			
			if(!"root".equalsIgnoreCase(pointsetid)){//根节点
				
			sql.append("select scope,pointsetid,pointsetname,parent_id,b0110,child_id,seq,validflag,subsys_id from per_pointset where pointsetid='"+pointsetid+"'");
			ContentDAO dao1 = new ContentDAO(this.getFrameconn());
			this.frowset = dao1.search(sql.toString());
			String b0110 = "";
			if(this.frowset.next()){
				b0110 = this.frowset.getString("b0110");
			}
			String yxb0110 = KhFieldTree.getyxb0110(this.userView,this.getFrameconn());
			int yxb0110le = yxb0110.length();
			int b0110le = b0110.length();
			if(yxb0110le<b0110le)
				yxb0110le = yxb0110.length();
			else 
				yxb0110le = b0110.length();
			if(!b0110.substring(0,yxb0110le).equals(yxb0110)&&!userView.isSuper_admin()&&!"1".equals(userView.getGroupId())&&!"hjsj".equalsIgnoreCase(b0110)){
			this.getFormHM().put("msg","9");
			}else{		
			
			deleteChild(pointsetid,ids);
			if("root".equalsIgnoreCase(pointsetid))
			{
				if(ids.toString().length()>0)
				{
					ids.setLength(ids.length()-1);
				}
			}
			else
			{
				ids.append(pointsetid);
			}
			if(!hasUsedPoint(ids.toString()))
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.deletePoint(ids.toString(), dao);
			}
			else
			{
				msg="2";
			}
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("msg",msg);
			}
			}}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 递归删除指标分类,指标要素,和标度内容
	 * @param pointsetid
	 */
	public void deleteChild(String pointsetid,StringBuffer ids) throws GeneralException
	{
		try
		{
			 StringBuffer buf=new StringBuffer();
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  /**查找子节点*/
			  buf.append("select pointsetid from per_pointset where ");
			  if("root".equalsIgnoreCase(pointsetid))
			  {
				  buf.append("parent_id is null or parent_id = ''");
			  }
			  else
			  {
				  buf.append("parent_id ='"+pointsetid+"'");
			  }
			 
			  RowSet rset=dao.search(buf.toString());
			  while(rset.next())
			  {
				  String temp=rset.getString("pointsetid");
				  ids.append(temp+",");
				  deleteChild(temp,ids);
			  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public void deletePoint(String pointsetid,ContentDAO dao) throws GeneralException 
	{
		try
		{
			StringBuffer sql= new StringBuffer();
			sql.append("delete from per_grade where point_id in(");
			sql.append("select point_id from per_point where pointsetid in("+pointsetid+"))");
			dao.delete(sql.toString(),new ArrayList());
			sql.setLength(0);
			sql.append("delete from per_point where pointsetid in ("+pointsetid+")");
			dao.delete(sql.toString(),new ArrayList());
			sql.setLength(0);
			sql.append("delete from per_pointset where pointsetid in("+pointsetid+")");
			dao.delete(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public boolean hasUsedPoint(String pointsetid)
	{
		boolean flag=false;
		try
		{
			StringBuffer sql=new StringBuffer("");
			sql.append("select point_id from per_template_point where point_id in(");
			sql.append("select point_id from per_point where pointsetid in(");
			sql.append(pointsetid+"))");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				flag=true;
				break;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
