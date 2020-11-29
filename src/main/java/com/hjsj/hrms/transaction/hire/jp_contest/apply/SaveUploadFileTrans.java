package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hjsj.hrms.businessobject.hire.JingPingPosBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.util.HashMap;

/**
 * 
 *<p>Title:SaveUploadFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 24, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SaveUploadFileTrans extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			JingPingPosBo jpbo = new JingPingPosBo(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String z0700 = (String)this.getFormHM().get("z0700");
//			System.out.println(z0700);

			String filepath = (String)this.getFormHM().get("filepath");
			String title = (String)this.getFormHM().get("filetitle");
			if(title==null || "".equals(title))
			{
				title = this.getFileTitle(filepath);
			}
			FormFile file=(FormFile)getFormHM().get("uploadfile");
			String filetype = this.getPostfix(filepath);
			
			String id = this.getId(z0700,dao);
			if((id==null || "".equals(id)))
			{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			    id =idg.getId("zp_apply_jobs.id");
			    this.insertApplyJobs(id,z0700,dao);
			}
		    
		    IDGenerator idgs = new IDGenerator(2, this.getFrameconn());
		    String fileid =idgs.getId("zp_apply_file.fileid");
		    this.insertUploadFile(fileid,id,title,filetype,dao);
		    this.updateApplyFile(file,fileid,dao);
		    
//			ArrayList apply_file_list = this.getApplyFile(z0700,dao);
			this.getFormHM().put("filetitle","");
			this.getFormHM().put("filepath","");
			this.getFormHM().put("applyflag","yes");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
//	public ArrayList getApplyFile(String z0700,ContentDAO dao)
//	{
//		ArrayList retlist = new ArrayList();
//		StringBuffer sql = new StringBuffer();
//		sql.append(" select * from zp_apply_file ");
//		sql.append("  where id = (");
//		sql.append(" select id from zp_apply_jobs where z0700= ");
//		sql.append(z0700);
//		sql.append(")" );
//		try 
//		{
//			retlist = dao.searchDynaList(sql.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return retlist;
//	}

	public void insertApplyJobs(String id,String z0700,ContentDAO dao)
	{
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into zp_apply_jobs  ");
		sb.append("(id,nbase,a0100,z0700,state)");
		sb.append(" values ");
		sb.append("("+id+",");
		sb.append("'"+this.userView.getDbname()+"',");
		sb.append("'"+this.userView.getA0100()+"',");
		sb.append(z0700+",");
		sb.append("'01')");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString().toUpperCase());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void updateApplyFile(FormFile file,String fileid,ContentDAO dao)
	{
		try
		{
//			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("zp_apply_file");
		    vo.setString("fileid",fileid);
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		    switch(Sql_switcher.searchDbServer())
		    {
		    case Constant.ORACEL:
		    	break;
		    default:
		    	byte[] data = file.getFileData();
		        a_vo.setObject("content",data);
		        break;	
		    }
		    dao.updateValueObject(a_vo);
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    {
		    	Blob blob = getOracleBlob(file,fileid);
		    	a_vo.setObject("content",blob);
		    	dao.updateValueObject(a_vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private Blob getOracleBlob(FormFile file,String fileid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select content from ");
		strSearch.append(" zp_apply_file ");
		strSearch.append(" where fileid=");
		strSearch.append(fileid);	
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(" zp_apply_file ");
		strInsert.append(" set content=EMPTY_BLOB() where fileid=");
		strInsert.append(fileid);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		Blob blob=blobutils.readBlob(strSearch.toString().toUpperCase(),strInsert.toString().toUpperCase(),file.getInputStream()); 
		return blob;
	}
	
	public void insertUploadFile(String fileid,String id,String name,String filetype,ContentDAO dao)
	{
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into zp_apply_file  ");
		sb.append("(fileid,id,name,ext)");
		sb.append(" values ");
		sb.append("("+fileid+",");
		sb.append(id+",");
		sb.append("'"+name+"',");
		sb.append("'"+filetype+"')");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString().toUpperCase());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public String getId(String z0700,ContentDAO dao)
	{
		String retstr = "";
		try 
		{
			String sql = "select * from zp_apply_jobs  where z0700 ="+z0700+"  and a0100='"+this.userView.getA0100()+"'";
			this.frowset = dao.search(sql.toUpperCase());
			if(this.frowset.next())
			{
				retstr = this.frowset.getInt("ID")+"";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}	
	    return retstr;
	}
	
}