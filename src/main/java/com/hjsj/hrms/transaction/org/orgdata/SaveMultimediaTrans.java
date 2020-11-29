package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
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
import java.io.InputStream;
import java.sql.Blob;
/**
 *<p>Title:SaveMultimediaTrans</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class SaveMultimediaTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String a0100  = (String)this.getFormHM().get("a0100");
		String dbname = "";
		String kind = (String)this.getFormHM().get("kind");
		if("6".equals(kind))
		    dbname = (String)this.getFormHM().get("dbname");
		
		String curri9999 = (String)this.getFormHM().get("curri9999");
		String multimediaflag = (String)this.getFormHM().get("filetype");
		String filepath = (String)this.getFormHM().get("filepath");
		String title = (String)this.getFormHM().get("filetitle");
		String isvisible=(String)this.getFormHM().get("isvisible");
		if(title==null || "".equals(title))
		{
			title = this.getFileTitle(filepath);
		}
//		this.getFormHM().put("a0100",a0100);
//		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("isvisible", isvisible);
		this.getFormHM().put("i9999","");
		this.getFormHM().put("flag",multimediaflag);
		this.getFormHM().put("filepath","");
		this.getFormHM().put("filetitle","");
		FormFile file=(FormFile)getFormHM().get("picturefile");
		String filetype = this.getPostfix(filepath);
//		System.out.print(filetype);	
//		String[] gettitle = this.getStringArr(title);
//		title = gettitle[0];
//		System.out.print(title);
		if("0".equals(curri9999))//新增
		{
		    int maxi9999 = this.getMaxI9999(kind,a0100,dbname,dao);
		    if(maxi9999<1)
		    {
				this.insert(kind,a0100,dbname,title,multimediaflag,filetype,dao);
				this.updateEmail_attach(kind,file,a0100,dbname,"1",dao);
		    }else{
				String geti9999 = maxi9999+1+"";
				this.insert(kind,a0100,dbname,title,multimediaflag,filetype,geti9999,dao);
				this.updateEmail_attach(kind,file,a0100,dbname,geti9999,dao);
		    }		    
		}else//插入
		{
		    this.updateI9999(kind, a0100, dbname, curri9999, dao);
		    this.insert(kind,a0100,dbname,title,multimediaflag,filetype,curri9999,dao);
		}			
	}
	/**
	 * 获得文件名
	 * @param filepath
	 * @return
	 */
	public String getFileTitle(String filepath)
	{
		String filetitle = "";
		int numstrart =filepath.lastIndexOf("\\")+1;
		int numend = filepath.lastIndexOf(".");
		filetitle = filepath.substring(numstrart,numend);
		return filetitle;
	}
	/**
	 * 获得后缀
	 * @param filepath
	 * @return
	 */
	public String getPostfix(String filepath)
	{
		String postfix = "";
		int num = filepath.lastIndexOf(".");
		postfix = filepath.substring(num,filepath.length());
		return postfix;
	}
	public String[] getStringArr (String str)
	{
		String[] Stringarr = null;
		int tempnum = str.split("\\.").length;
		if(tempnum>0)
		{
			Stringarr = str.split("\\.");
		}
		return Stringarr;
	}
	/**
	 * 得到i9999
	 * @param a0100
	 * @param dbpre
	 * @return
	 */
	public int getMaxI9999(String kind,String a0100,String dbpre,ContentDAO dao)
	{
		RowSet rs;
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		if("6".equals(kind))
		{
			sb.append(" select max(i9999) as i9999 from "+dbpre+"a00 ");
			sb.append(" where a0100='"+a0100+"' ");
		}else if("0".equals(kind))
		{
			sb.append(" select max(i9999) as i9999 from k00 ");
			sb.append(" where e01a1='"+a0100+"' ");
		}else 
		{
			sb.append(" select max(i9999) as i9999 from b00 ");
			sb.append(" where b0110='"+a0100+"' ");
		}

//		System.out.println(sb.toString());
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("i9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	/**
	 * 插入照片
	 * @param a0100
	 * @param dbpre
	 */
	public void insert(String kind,String a0100,String dbpre,String title,String flag,String filetype,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		if("6".equals(kind))
		{
			sb.append("insert into "+dbpre+"a00 ");
			sb.append("(a0100,i9999,title,flag,ext,");
		}else if("0".equals(kind))
		{
			sb.append("insert into k00 ");
			sb.append("(e01a1,i9999,title,flag,ext,");
		}else 
		{
			sb.append("insert into b00 ");
			sb.append("(b0110,i9999,title,flag,ext,");
		}
		sb.append("CreateTime,CreateUserName)");
		sb.append(" values ");
		sb.append("('"+a0100+"',1,'"+title+"','"+flag+"','"+filetype+"',");
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
	public void insert(String kind,String a0100,String dbpre,String title,String flag,String filetype,String i9999,ContentDAO dao)
	{
		
		StringBuffer sb = new StringBuffer();
		if("6".equals(kind))
		{
			sb.append("insert  into  "+dbpre+"a00 ");
			sb.append("(a0100,i9999,title,flag,ext,");
		}else if("0".equals(kind))
		{
			sb.append("insert  into k00 ");
			sb.append("(e01a1,i9999,title,flag,ext,");
		}else 
		{
			sb.append("insert  into b00 ");
			sb.append("(b0110,i9999,title,flag,ext,");
		}
		title=title.length()>40?title.substring(0,40):title;
		sb.append("CreateTime,CreateUserName,state)");
		sb.append(" values ");
		sb.append("('"+a0100+"',"+i9999+",'"+title+"','"+flag+"',");
		sb.append("'"+filetype+"',");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'"+this.userView.getUserName()+"','1'");
		sb.append(")");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateI9999(String kind,String a0100,String dbpre,String curi9999,ContentDAO dao)
	{		
		StringBuffer sb = new StringBuffer();
		if("6".equals(kind))
		{
			sb.append("update  "+dbpre+"a00 ");
			sb.append("set i9999=i9999+1 where a0100='"+a0100+"' and i9999>="+curi9999);
			
		}else if("0".equals(kind))
		{
			sb.append("update k00 ");
			sb.append("set i9999=i9999+1 where e01a1='"+a0100+"' and i9999>="+curi9999);
			
		}else 
		{
			sb.append("update b00 ");
			sb.append("set i9999=i9999+1 where b0110='"+a0100+"' and i9999>="+curi9999);
			
		}
		
		try
		{
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
		sb.append(" select * from "+dbpre+"a00 ");
		sb.append(" where a0100='"+a0100+"' ");
		sb.append(" and flag='p' ");
//		System.out.println(sb.toString());
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				flag = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 图片存到数据库
	 * @param file
	 * @param attach_id
	 */
	public void updateEmail_attach(String kind,FormFile file,String a0100,String dbpre,String i9999,ContentDAO dao)
	{
		try
		{
			if("6".equals(kind))
			{
				RecordVo vo = new RecordVo(dbpre+"a00");
			    vo.setString("a0100",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
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
			    	Blob blob = getOracleBlob(file,dbpre,a0100,i9999,kind);
			    	a_vo.setObject("ole",blob);
//			    	dao.updateValueObject(a_vo);
			    }
			    dao.updateValueObject(a_vo);
			}else if("0".equals(kind))
			{
				RecordVo vo = new RecordVo("k00");
			    vo.setString("e01a1",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
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
			    	Blob blob = getOracleBlob(file,"",a0100,i9999,kind);
			    	a_vo.setObject("ole",blob);
//			    	dao.updateValueObject(a_vo);
			    }
			    dao.updateValueObject(a_vo);
			}else 
			{
				RecordVo vo = new RecordVo("b00");
			    vo.setString("b0110",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
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
			    	Blob blob = getOracleBlob(file,"",a0100,i9999,kind);
			    	a_vo.setObject("ole",blob);
//			    	dao.updateValueObject(a_vo);
			    }
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
	private Blob getOracleBlob(FormFile file,String dbpre,String a0100,String i9999,String kind) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		StringBuffer strInsert=new StringBuffer();
		if("6".equals(kind))
		{
			strSearch.append("select ole from ");
			strSearch.append(dbpre+"a00 ");
			strSearch.append(" where a0100='");
			strSearch.append(a0100.toUpperCase());	
			strSearch.append("' and i9999=");
			strSearch.append(i9999);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(dbpre+"a00 ");
			strInsert.append(" set ole=EMPTY_BLOB() where a0100='");
			strInsert.append(a0100.toUpperCase());
			strInsert.append("' and i9999=");
			strInsert.append(i9999);			
		}else if("0".equals(kind))
		{
			strSearch.append("select ole from ");
			strSearch.append("k00 ");
			strSearch.append(" where e01a1='");
			strSearch.append(a0100.toUpperCase());	
			strSearch.append("' and i9999=");
			strSearch.append(i9999);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append("k00 ");
			strInsert.append(" set ole=EMPTY_BLOB() where e01a1='");
			strInsert.append(a0100.toUpperCase());
			strInsert.append("' and i9999=");
			strInsert.append(i9999);
		}else
		{
			strSearch.append("select ole from ");
			strSearch.append("b00 ");
			strSearch.append(" where b0110='");
			strSearch.append(a0100.toUpperCase());	
			strSearch.append("' and i9999=");
			strSearch.append(i9999);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append("b00 ");
			strInsert.append(" set ole=EMPTY_BLOB() where b0110='");
			strInsert.append(a0100.toUpperCase());
			strInsert.append("' and i9999=");
			strInsert.append(i9999);
		}
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    InputStream fileStream = null;
	    Blob blob= null;
	    try{
	    	fileStream = file.getInputStream();
		    blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),fileStream); 
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	PubFunc.closeIoResource(fileStream); //关闭资源 guodd 2014-12-29
	    }
		return blob;
	}
	
}
