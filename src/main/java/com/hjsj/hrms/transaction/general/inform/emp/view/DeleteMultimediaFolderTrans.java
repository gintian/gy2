package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
/**
 *<p>Title:DeleteMultimediaFolderTrans</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class DeleteMultimediaFolderTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
//      删除文件夹，并把该文件夹里的文件移动到另一个文件夹里		
//		ContentDAO dao = new ContentDAO(this.getFrameconn());
//		String dbname = (String)this.getFormHM().get("dbname");
//		String kind = (String)this.getFormHM().get("kind");
//		String multimediaflag = (String)this.getFormHM().get("multimediaflag");
//		String tomultimediaflag = (String)this.getFormHM().get("tomultimediaflag");
//		if(!(multimediaflag==null || multimediaflag.equals(""))
//				 && !(multimediaflag==null || multimediaflag.equals("")))
//		{
//			tomultimediaflag = this.getFlag(tomultimediaflag,dao);
//			this.updateMultimedia(dbname,tomultimediaflag,multimediaflag,kind,dao);
//			this.deleteMultimedia(multimediaflag,dao);
//		}
			
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String dbpre = (String)this.getFormHM().get("dbname");
		String kind = (String)this.getFormHM().get("kind");
		String id = (String)this.getFormHM().get("id");
		String flag = "";	
		if(!(id==null || "".equals(id)))
		{
			flag = this.getFlag(id,dao);
			if(!(flag==null || "".equals(flag)))
			{
				if (deleteBefore(flag, dao) || selectOtherClass(flag, dao)){
					this.getFormHM().put("result", "true");
					return;
				}else {
					this.getFormHM().put("result", "false");
				}
				this.deleteMultimedia(id,dao);
			}
			
		}
	}
	/**
	 *  删除分类之前查询该分类之下是否有附件记录
	 * @param id
	 * @param dao
	 * @return true 表示有记录，或者查询发生异常； false 没有记录
	 */
	public boolean deleteBefore(String flag,ContentDAO dao) {
		StringBuffer sb = new StringBuffer();
		RowSet rs=null;
		//根据分类查询记录
		sb.append("select count(1) as num from hr_multimedia_file");
		sb.append(" where class='"+flag);
		sb.append("'");
		try
		{
			rs = dao.search(sb.toString());
			//判断分类下是否有记录
			if(rs.next()){
				int rows = rs.getInt("num");
				if(rows > 0){
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 更新
	 * @param a0100
	 * @param dbpre
	 * @param i9999
	 * @param dao
	 */
	public void updateMultimedia(String dbpre,String tomultimediaflag,String multimediaflag,String kind,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		if("6".equals(kind))
		{
			sb.append(" update "+dbpre+"a00 ");
		}else if("0".equals(kind))
		{
			sb.append(" update k00 ");
		}else 
		{
			sb.append(" update b00 ");
		}
		sb.append(" set flag='"+tomultimediaflag+"' ");
		sb.append(" where flag='"+multimediaflag+"'");
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//	/**
//	 *  删除
//	 * @param multimediaflag
//	 * @param dao
//	 */
//	public void deleteMultimedia(String multimediaflag,ContentDAO dao)
//	{
//		StringBuffer sb = new StringBuffer();
//		sb.append(" delete mediasort ");
//		sb.append(" where flag='"+multimediaflag+"' ");
////		System.out.println(sb.toString());
//		try
//		{
//			dao.update(sb.toString());
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	
	/**
	 *  删除
	 * @param multimediaflag
	 * @param dao
	 */
	public void deleteMultimedia(String id,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" delete mediasort ");
		sb.append(" where id="+id);
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到flag的值
	 * @param multimediaid
	 * @param dao
	 * @return
	 */
	public String getFlag(String multimediaid,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select flag from mediasort ");
		sb.append(" where id="+multimediaid);
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retstr = rs.getString("flag");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public void deleteContent(String kind,String dbpre,String flag,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		if("6".equals(kind))
		{
			sb.append(" delete "+dbpre+"a00 ");
		}else if("0".equals(kind))
		{
			sb.append(" delete k00 ");
		}else 
		{
			sb.append(" delete b00 ");
		}
		sb.append(" where flag='"+flag+"'");
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除分类之前查询该其他库中是否有该分类记录
	 * @param flag
	 * @param dao
	 * @return true 表示有记录，或者查询发生异常； false 没有记录
	 */
	public boolean selectOtherClass(String flag,ContentDAO dao)
	{
		String dbpre = "";
		RowSet rs_pre = null;
		RowSet rs_a = null,rs_b = null,rs_k = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select Pre from DBName");
		try {
			rs_pre = dao.search(sb.toString());
			
			//循环判断各个人员库
			while(rs_pre.next()){
					//得到人员库前缀
					dbpre = rs_pre.getString("Pre");
					sb.setLength(0);
					sb.append("select count(1) as num from ");
					sb.append(dbpre);
					sb.append("A00 where Flag='");
					sb.append(flag);
					sb.append("'");
					//查询人员库是否有附件记录
					rs_a = dao.search(sb.toString());
					if(rs_a.next()){
						int rows = rs_a.getInt("num");
						if (rows > 0) {
							return true;
						}
					}
			}
			//判断单位库
			sb.setLength(0);
			sb.append("select count(1) as num from ");
			sb.append("B00 where Flag='");
			sb.append(flag);
			sb.append("'");
			//查询单位库是否有附件记录
			rs_b = dao.search(sb.toString());
			if(rs_b.next()){
				int rows = rs_b.getInt("num");
				if (rows > 0) {
					return true;
				}
			}
			
			//判断岗位库
			sb.setLength(0);
			sb.append("select count(1) as num from ");
			sb.append("K00 where Flag='");
			sb.append(flag);
			sb.append("'");
			//查询岗位库是否有附件记录
			rs_k = dao.search(sb.toString());
			if(rs_k.next()){
				int rows = rs_k.getInt("num");
				if (rows > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs_k != null) {
					rs_k.close();
				}
				if (rs_b != null) {
					rs_b.close();
				}
				if (rs_a != null) {
					rs_a.close();
				}
				if (rs_pre != null) {
					rs_pre.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
