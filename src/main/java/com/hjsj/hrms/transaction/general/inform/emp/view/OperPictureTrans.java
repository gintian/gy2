package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
/**
 *<p>Title:OperPictureTrans</p> 
 *<p>Description:照片操作</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-10:上午09:25:26</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class OperPictureTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String a0100 = (String)this.getFormHM().get("a0100");
			String dbname = (String)this.getFormHM().get("dbname");
			String type = (String)this.getFormHM().get("type"); 

			if("clear".equalsIgnoreCase(type))
			{
				boolean flag = this.judgePicture(a0100,dbname,dao);
				if(flag)
				{
					String i9999 = this.getPicturePrimaryKey(a0100,dbname,dao);
					this.clearPicture( a0100, dbname, i9999, dao);
				}
			}else if("upload".equalsIgnoreCase(type)){
				FormFile file=(FormFile)getFormHM().get("picturefile");
				String filename = file.getFileName();
				String filetxt = filename.substring(filename.lastIndexOf("."));
				filetxt=filetxt!=null&&filetxt.trim().length()>1?filetxt:".jpg";
				String filetype = file.getContentType();
//				System.out.print(filetype);
				if(filetype.startsWith("image"))
				{	
					// 判断 flag 是否已有此人照片
					boolean flag = this.judgePicture(a0100,dbname,dao);
					if(flag)
					{
						String i9999 = this.getPicturePrimaryKey(a0100,dbname,dao);	
						this.updateEmail_attach(file,dbname,a0100,i9999,dao);
					}else{
						int i9999 = this.getMaxI9999(a0100,dbname,dao);	
						// 判断此人有没其他多媒体文件
						if(i9999<1)
						{
							this.insert(a0100,dbname,dao,filetxt);
							this.updateEmail_attach(file,dbname,a0100,"1",dao);
						}else{
							String geti9999 = i9999+1+"";
							this.insert(a0100,dbname,geti9999,dao,filetxt);
							this.updateEmail_attach(file,dbname,a0100,geti9999,dao);
						}
						
					}
				}
			}
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("dbname",dbname);

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	/**
	 * 插入照片
	 * @param a0100
	 * @param dbpre
	 */
	public void insert(String a0100,String dbpre,ContentDAO dao,String filetxt)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+dbpre+"A00 ");
		sb.append("(A0100,I9999,Flag,ext,");
		sb.append("CreateTime,CreateUserName)");
		sb.append(" values ");
		sb.append("('"+a0100+"',1,'P','"+filetxt+"',");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'"+this.userView.getUserName()+"'");
		sb.append(")");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 插入照片
	 * @param a0100
	 * @param dbpre
	 */
	public void insert(String a0100,String dbpre,String i9999,ContentDAO dao,String filetxt)
	{
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+dbpre+"A00 ");
		sb.append("(A0100,I9999,Flag,ext,");
		sb.append("CreateTime,CreateUserName)");
		sb.append(" values ");
		sb.append("('"+a0100+"',"+i9999+",'P','"+filetxt+"',");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'"+this.userView.getUserName()+"'");
		sb.append(")");
//		System.out.println(sb.toString());
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sb.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 判断此人照片是否存在
	 * @param a0100
	 * @param dbpre
	 * @return true为存在
	 */
	public boolean judgePicture(String a0100,String dbpre,ContentDAO dao)
	{
		RowSet rs;
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from "+dbpre+"A00 ");
		sb.append(" where A0100='"+a0100+"' ");
		sb.append(" and flag='P' ");
//		System.out.println(sb.toString());
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				String A0100 = rs.getString("A0100");
				if(A0100!=null&&A0100.trim().length()>0)
					flag = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * @param a0100
	 * @param dbpre
	 * @return true为存在
	 */
	public String  getPicturePrimaryKey(String a0100,String dbpre,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select I9999 from "+dbpre+"A00 ");
		sb.append(" where A0100='"+a0100+"' ");
		sb.append(" and flag='P' ");
//		System.out.println(sb.toString());
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retstr = rs.getString("I9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	/**
	 * 得到i9999
	 * @param a0100
	 * @param dbpre
	 * @return
	 */
	public int getMaxI9999(String a0100,String dbpre,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select max(I9999) as I9999 from "+dbpre+"A00 ");
		sb.append(" where A0100='"+a0100+"' ");
//		System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("I9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	/**
	 * 图片存到数据库
	 * @param file
	 * @param attach_id
	 */
	public void updateEmail_attach(FormFile file,String dbpre,String a0100,String i9999,ContentDAO dao)
	{
		try
		{
			RecordVo vo = new RecordVo(dbpre+"A00");
		    vo.setString("a0100",a0100);
		    vo.setInt("i9999",Integer.parseInt(i9999));
		    vo.setString("flag","P");
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		    switch(Sql_switcher.searchDbServer())
		    {
		    case Constant.ORACEL:
		    	break;
		    default:
		    	byte[] data = file.getFileData();
		        a_vo.setObject("ole",data);
		        break;	
		    }
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    {
		    	Blob blob = getOracleBlob(file,dbpre,a0100,i9999);
		    	a_vo.setObject("ole",blob);
		    	dao.updateValueObject(a_vo);
		    }else{
		    	 dao.updateValueObject(a_vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * oracle得到blob字段
	 * @param file
	 * @param attach_id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String dbpre,String a0100,String i9999) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select Ole from ");
		strSearch.append(dbpre+"A00 ");
		strSearch.append(" where A0100='");
		strSearch.append(a0100);	
		strSearch.append("' and I9999=");
		strSearch.append(i9999);	
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(dbpre+"A00 ");
		strInsert.append(" set Ole=EMPTY_BLOB() where A0100='");
		strInsert.append(a0100);
		strInsert.append("' and I9999=");
		strInsert.append(i9999);

	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream()); 
		return blob;
	}
	/**
	 * 删除照片
	 * @param a0100
	 * @param dbpre
	 * @param i9999
	 * @param dao
	 */
	public void clearPicture(String a0100,String dbpre,String i9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" delete "+dbpre+"A00 ");
		sb.append(" where A0100='"+a0100+"' and Flag='P'");
//		sb.append(" and I9999=");
//		sb.append(i9999);
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
