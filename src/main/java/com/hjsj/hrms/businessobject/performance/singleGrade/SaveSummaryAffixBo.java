package com.hjsj.hrms.businessobject.performance.singleGrade;


import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * 绩效模板功能类Bo
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014 16:05:00</p>
 * @author zangxj
 * @version 1.0
 *
 */
public class SaveSummaryAffixBo {

	private Connection conn=null;
	UserView userViwe = null;
	
	
	public SaveSummaryAffixBo(UserView userView,Connection con)
	{
		this.conn=con;
		this.userViwe = userView;
	}
	
	public SaveSummaryAffixBo(Connection con)
	{
		this.conn=con;
	}	
	
	public SaveSummaryAffixBo()
	{
		
	}
	
	/**
	* 下载模板文件
	* @param plan_id
	* @param opt
	* @return outname
	* @throws GeneralException
	*/
	public String summaryDown(String plan_id,String opt) throws GeneralException {
		String outname = "";
		try {
			ContentDAO dao=new ContentDAO(conn);
			RowSet rs = null;
			String sql =" select Article_name from per_article where Fileflag=3 and plan_id = "+plan_id;
			rs = dao.search(sql);
			String isnull = "";
			if(rs.next()){
				isnull = rs.getString("Article_name");
				}
			if(isnull != null && isnull.length() != 0&& "down".equals(opt)){
				 outname = outFile(plan_id,conn);
			}
			if(rs!=null)
				rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);	
		}
		return outname;
	}
	
	/**
	* 上传模板文件
	* @param plan_id
	* @param ext
	* @param form_file
	* @param fname
	* @return 
	* @throws GeneralException
	*/
	public void summaryUp(String planId,String ext,FormFile form_file,String fname) throws GeneralException {
		
		ContentDAO dao=new ContentDAO(conn);
		
		//判断是否存在
		RowSet rs = null;
		String tempTable = "per_article";
		String sql="select Article_id from per_article where Fileflag=3 and Plan_id="+planId;
		String id = "";
		String Article_id = "";
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement prestmt = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				Article_id = rs.getString("Article_id");
				}
			if("".equals(Article_id)||Article_id.equals(null)){
			
				int intid=DbNameBo.getPrimaryKey("per_article","article_id",this.conn);
				String sql41 = " INSERT INTO per_article (Article_id,Plan_id,Article_name,Ext,Article_type,Fileflag) VALUES ('"+intid+"','"+planId+"','"+fname+"','"+ext+"',2,3)";
				dao.insert(sql41, new ArrayList());
				id=intid+"";
			}
			else{
			id=Article_id;
			}
			String sqlinsertstream = "UPDATE per_article SET Affix = ?,Article_name = ?,Ext = ?,Article_type = ?,Fileflag = ?  WHERE Fileflag=3 and Plan_id = ?";
			if (Sql_switcher.searchDbServer() != Constant.ORACEL)
			{
				java.io.InputStream fis = form_file.getInputStream();
				try{
    				prestmt = conn.prepareStatement(sqlinsertstream.toString());
    				prestmt.setBinaryStream(1, fis, form_file.getFileSize());
    				prestmt.setString(2,fname);
    				prestmt.setString(3,ext);
    				prestmt.setInt(4,2);
    				prestmt.setInt(5,3);
    				prestmt.setInt(6, Integer.parseInt(planId));
    				// 打开Wallet
    				dbS.open(conn, sqlinsertstream.toString());
    				prestmt.executeUpdate();
				}
				finally{
					PubFunc.closeDbObj(prestmt);
				    PubFunc.closeResource(fis);
				}
			}else{
				updateEmail_attach(form_file,planId,id);
				java.io.InputStream fis = form_file.getInputStream();
				try{
    				prestmt = conn.prepareStatement(sqlinsertstream.toString());
    				prestmt.setBinaryStream(1, fis, form_file.getFileSize());
    				prestmt.setString(2,fname);
    				prestmt.setString(3,ext);
    				prestmt.setInt(4,2);
    				prestmt.setInt(5,3);
    				prestmt.setInt(6, Integer.parseInt(planId));
    				// 打开Wallet
    				dbS.open(conn, sqlinsertstream.toString());
    				prestmt.executeUpdate();
				}
				finally{
				    PubFunc.closeResource(fis);
				    PubFunc.closeDbObj(prestmt);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally{
			PubFunc.closeDbObj(prestmt);
			try
			{
				if(rs!=null)
					rs.close();
				// 关闭Wallet
				dbS.close(this.conn);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	/**
	* 删除模板文件
	* @param plan_id
	* @return 
	* @throws GeneralException
	*/
	public void summaryDel(String plan_id) throws GeneralException {
		String flag = "";
	try {
		ContentDAO dao=new ContentDAO(conn);
		String sqlinsertstream = "delete from  per_article  WHERE Fileflag=3 and plan_id = "+plan_id;
		dao.update(sqlinsertstream);
		} catch (SQLException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);		
		}
	}
	

	/**
	* 判断模板文件是否为空
	* @param plan_id
	* @return 
	* @throws GeneralException
	*/
	public String isnullArticle_name(String plan_id) throws GeneralException {
		
		String IsNull = "";
		
		try {
			ContentDAO dao1=new ContentDAO(conn);
			String outname = "";

			RowSet rs = null;
			String sql =" select Article_name from per_article where Fileflag=3 and plan_id = "+plan_id;
			rs = dao1.search(sql);
			String isnull = "";
			if(rs.next()){
				isnull = rs.getString("Article_name");
				}
			if(isnull != null && isnull.length() != 0){
				IsNull = "have";
			}
			else{
				IsNull = "null";
			}		
			if(rs!=null)
				rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);	
		}
		return IsNull;
	}
	
	/**
	* 文件名
	* @param plan_id
	* @return 
	* @throws GeneralException
	*/
	public String filename_Articlename(String plan_id) throws GeneralException {
		
		String IsNull = "";
		
		try {
			ContentDAO dao1=new ContentDAO(conn);
			String outname = "";

			RowSet rs = null;
			String sql =" select Article_name from per_article where Fileflag=3 and plan_id = "+plan_id;
			rs = dao1.search(sql);
			String isnull = "";
			if(rs.next()){
				isnull = rs.getString("Article_name");
				}
			if(isnull != null && isnull.length() != 0){
				IsNull = isnull;
			}
			else{
				IsNull = " ";
			}		
			if(rs!=null)
				rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);	
		}
		return IsNull;
	}
	
	/**
	* mysql输出流方法
	* @param plan_id
	* @param conn
	* @return pathstr
	* @throws GeneralException
	*/
	public String outFile(String plan_id,Connection conn) throws GeneralException {
		ResultSet rsy=null;
		Blob blob=null;
		String pathstr = "";
		ContentDAO dao = new ContentDAO(conn);
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator");
		InputStream in = null;
		OutputStream out = null;
		FileOutputStream fout = null;
		File file = null;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select affix,article_name,ext");
		sqlstr.append(" from per_article where fileflag=3 and plan_id=");
		sqlstr.append(plan_id);
		String ext = "";
		BufferedInputStream in2 = null;
		BufferedOutputStream out2 = null;
		
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix,ext from ");
		strSearch.append("per_article  ");
		strSearch.append(" where Fileflag=3 and plan_id =");
		strSearch.append(plan_id);		
		RowSet rs = null;
		try {	
			rsy = dao.search(strSearch.toString());

			if(Sql_switcher.searchDbServer() != Constant.ORACEL){
				rs = dao.search(sqlstr.toString());
				if(rs.next()){
					in = rs.getBinaryStream(1);
					ext = rs.getString(3);
					pathstr+="模板文件"+PubFunc.getStrg()+"_"+userViwe.getUserName()+ext;
					url +=pathstr;
					file = new File(url);
					fout = new java.io.FileOutputStream(file);
					int len;
					if(in!=null){
						byte buf[] = new byte[1024];	
						while ((len = in.read(buf, 0, 1024)) != -1) {
							fout.write(buf, 0, len);
						}
					}
					
					
					if(fout!=null){
						fout.close();
						fout = null;
					}
					if(in!=null){
						in.close();
						in = null;
					}
					file = null;
				}
			}
			else{
				while (rsy.next()) {
					ext = rsy.getString(2);
					pathstr+="模板文件"+PubFunc.getStrg()+"_"+userViwe.getUserName()+ext;
					url +=pathstr;
					blob=rsy.getBlob(1);
					try {
						out = new FileOutputStream(url);
						in = blob.getBinaryStream();
						in2 = new BufferedInputStream(in,1024);
						out2 = new BufferedOutputStream(out);
						int c;
						byte buf[] = new byte[1024];
						while ((c = in2.read(buf, 0, 1024)) != -1) {
							out2.write(buf, 0, c);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						PubFunc.closeResource(out2);
						PubFunc.closeResource(out);
						PubFunc.closeResource(in2);
						PubFunc.closeResource(in);
					}
					}
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}finally {
			PubFunc.closeResource(out2);
			PubFunc.closeResource(out);
			PubFunc.closeResource(in2);
			PubFunc.closeResource(in);
			PubFunc.closeResource(fout);
			PubFunc.closeResource(file);
			PubFunc.closeResource(rsy);
		}
		return pathstr;
	}
	
	
	
	   public static void ReadStreamToByteFile(String path,Blob blobstr) throws IOException{   
		   FileOutputStream fileOutputStream = null;  
		   InputStream in = null;
		   try {
			   in= blobstr.getBinaryStream();   
	
			   fileOutputStream = new FileOutputStream(new File(path).toURI().getPath());
			   byte[] buffer = new byte[1024];
			   int length = 0;
			   while ((length = in.read(buffer)) > 0) {
			       fileOutputStream.write(buffer, 0, length);
			   }
			   
			   fileOutputStream.flush();
		   
		   } catch (SQLException e) {
			   e.printStackTrace();  
		   } catch (FileNotFoundException e) {
		         e.printStackTrace();
		   } catch (IOException e) {
		         e.printStackTrace();
		   } finally {
		    	 PubFunc.closeResource(fileOutputStream);//资源释放 jingq 2014.12.29
		    	 PubFunc.closeResource(in);
		   }
		  
	   }

	   
	   
	/**
	* 文件存到数据库
	* @param file
	* @param planid
	* @param Article_id
	* @return
	* @throws GeneralException
	*/
	


	   
	public void updateEmail_attach(FormFile file,String planid,String id) throws GeneralException 
	{
		  byte[] data = null;
		  Blob blob=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("per_article");
		    vo.setInt("plan_id",Integer.parseInt(planid));
		    vo.setInt("article_id",Integer.parseInt(id));
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		  
		    switch(Sql_switcher.searchDbServer())
		    {
		    case Constant.ORACEL:
		    	break;
		    default:
		    	data = file.getFileData();
		        a_vo.setObject("affix",data);
		        break;	
		    }
		  //  dao.updateValueObject(a_vo);
		   // dao.updateValueObject(a_vo);
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    {
		    	blob = getOracleBlob(file,planid);
		    	a_vo.setObject("affix",blob);	
		    }
		    dao.updateValueObject(a_vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		finally
		{
			data=null;
			blob=null;
		}
		
	}

	/**
	* oracle得到blob字段
	* @param planid
	* @param file
	* @return blob
	* @throws FileNotFoundException, IOException ,GeneralException
	*/
	private Blob getOracleBlob(FormFile file,String planid) throws FileNotFoundException, IOException ,GeneralException {
	    InputStream inputStream = null;
	    Blob blob = null;
		try {
			StringBuffer strSearch=new StringBuffer();
			strSearch.append("select affix from ");
			strSearch.append("per_article  ");
			strSearch.append(" where Fileflag=3 and plan_id =");
			strSearch.append(planid);		
			strSearch.append(" FOR UPDATE");
			
			StringBuffer strInsert=new StringBuffer();
			strInsert.append("update  ");
			strInsert.append("per_article");
			strInsert.append(" set affix=EMPTY_BLOB() where plan_id=");
			strInsert.append(planid);
			OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
			inputStream = file.getInputStream();
			blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream); 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		} finally{
		    if(inputStream != null)
		        PubFunc.closeResource(inputStream);
		}
		
		return blob;
	}
	
	
	
	
	
	
	
}
