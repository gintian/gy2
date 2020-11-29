package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UpPosBookletTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String posID=(String)hm.get("posID");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("u".equals(opt))   //上传
			{
				FormFile form_file = (FormFile) getFormHM().get("file");
				//调用文件白名单检查方法，验证上传文件是否存在白名单内
				PubFunc.checkFileType(form_file);
				if(form_file.getFileData().length/1024>512){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.attach.size.error")));
				}
				if(form_file!=null&&form_file.getFileData().length>0&&form_file.getFileData().length<524288&&FileTypeUtil.isFileTypeEqual(form_file))
				{
					 int i9999=getRecordI9999(posID); 
				   	 String fname=form_file.getFileName();
				   	 int indexInt=fname.lastIndexOf(".");
				   	 RecordVo vo=new RecordVo("k00");
				   	 vo.setString("e01a1", posID);
				   	 vo.setInt("i9999",i9999);
				   	 vo.setString("title", "职位说明书");
				   	 
				   	 String ext=fname.substring(indexInt,fname.length());
				   	 vo.setString("ext",ext);
					 	/**blob字段保存,数据库中差异*/
					 switch(Sql_switcher.searchDbServer())
					 {
					 	   case Constant.ORACEL:
					 			break;
					 	   default:
								byte[] data=form_file.getFileData();				
					 	   		vo.setObject("ole",data);
					 			break;
					 }				
					 vo.setString("flag","K");			
					 vo.setDate("createtime", new Date());
					 vo.setDate("modtime", new Date());
					 vo.setString("createusername",this.getUserView().getUserName());
					 vo.setString("modusername",this.getUserView().getUserName());
					 vo.setString("state","3");
					 
					 dao.addValueObject(vo);
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						RecordVo updatevo=dao.findByPrimaryKey(vo);
					 	Blob blob = getOracleBlob(form_file,"k00",posID,String.valueOf(i9999));
					 	updatevo.setObject("ole",blob);			
						dao.updateValueObject(updatevo);
					}
					
				} else {
				    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.position.upfile.fail")));
				}
			}
			else if("s".equals(opt))
			{
				EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
				this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(posID));
				this.getFormHM().put("e01a1",posID);
				
			}
			else if("d".equals(opt))
			{
				dao.delete("delete from K00 where e01a1='"+posID+"' and flag='K'", new ArrayList());
				EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
				this.getFormHM().put("isPosBooklet",employNetPortalBo.getPosIsBooklet(posID));
				this.getFormHM().put("e01a1",posID);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	/** 取得需 新增纪录的 i9999 */
	public int getRecordI9999(String posId)
	{
		int i9999=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select i9999 from K00 where e01a1='"+posId+"' and Flag='K'");
			if(this.frowset.next())
			{
				i9999=this.frowset.getInt("i9999");
				dao.delete("delete from K00 where e01a1='"+posId+"'  and i9999="+i9999,new ArrayList());
			}
			else
			{
				this.frowset=dao.search("select max(i9999) from K00 where e01a1='"+posId+"'");
				if(this.frowset.next())
				{
					if(this.frowset.getString(1)!=null)
					{
						i9999=this.frowset.getInt(1)+1;
					}
					else
						i9999=1;	
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i9999;
	}
	
	
	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String tablename,String e01a1,String i9999) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select ole from ");
		strSearch.append(tablename);
		strSearch.append(" where e01a1='");
		strSearch.append(e01a1);		
		strSearch.append("' and i9999="+i9999+" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set ole=EMPTY_BLOB() where e01a1='");
		strInsert.append(e01a1);
		strInsert.append("' and i9999="+i9999);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    Blob blob = null;
	    InputStream stream = null;
	    try {
	    	stream = file.getInputStream();
	    	blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),stream); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}finally{
			PubFunc.closeIoResource(stream);
		}
		return blob;
	}

}
