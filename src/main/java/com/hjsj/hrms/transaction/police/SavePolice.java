package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 *<p>Title:SavePolice</p> 
 *<p>Description:保存文件</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2010-2-8</p> 
 *@author wangzhongjun
 *@version 1.0
 */
public class SavePolice extends IBusiness {
	public void execute() throws GeneralException {
		String b0110=(String)this.getFormHM().get("b0110");
		String filetitle=(String)this.getFormHM().get("filetitle");
		FormFile file=(FormFile)this.getFormHM().get("mediafile");
		RecordVo vo = new RecordVo("b00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
		insertDAO(vo,file,dao,b0110,filetitle);
	}
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String b0110,String filetitle) throws GeneralException {
		boolean bflag=true,b=false;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=Integer.parseInt(new StructureExecSqlString().getOrgI9999("b00", b0110, "b0110", this.getFrameconn()));
		int i9999 = 0;
		String cyclename = (String) this.getFormHM().get("cyclename");
		String cycle = (String) this.getFormHM().get("cycle");
		try 
		{   
			vo.setString("b0110",b0110);
			vo.setInt("i9999",recid);
			if ("yqdt".equals(cyclename)) {
				vo.setString("flag","y");
			} else if ("dept".equals(cyclename)) {
				vo.setString("flag", "t");
			}
			
			vo.setString("title",filetitle);
			
			vo.setDate("modtime",DateStyle.getSystemTime());
			vo.setString("createusername",userView.getUserName());
			vo.setString("modusername",userView.getUserName());
			if(bflag)
			{
		   	 	String fname=file.getFileName();
		   	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt+1,fname.length());
				vo.setString("ext",ext);
			 	/**blob字段保存,数据库中差异*/
			 	switch(Sql_switcher.searchDbServer())
			 	{
			 	   case Constant.ORACEL:
//					 	Blob blob = getOracleBlob(vo, file);
//					 	vo.setObject("thefile",blob);
			 			break;
			 	   default:
						byte[] data=file.getFileData();				
						vo.setObject("ole",data);
			 			break;
			 	}				
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			int date = Integer.parseInt(PubFunc.FormatDate(new Date(),"yyyy"));
			SimpleDateFormat simpleDateFormat = new	SimpleDateFormat("yyyy");   
			Date d1 =  simpleDateFormat.parse(date+"");
			Date d2 =  simpleDateFormat.parse(date+1+"");
			
			if ("0".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				d1 = simpleDateFormat.parse(taskyear);
				d2 = simpleDateFormat.parse(String.valueOf(Integer.parseInt(taskyear) + 1));
			} else if ("1".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				String taskmonth = (String) this.getFormHM().get("taskmonth");
				SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
				d1 = fo.parse(taskyear + "-" + taskmonth + "-01");
				if (Integer.parseInt(taskmonth) == 12) {
					d2 = fo.parse(String.valueOf(Integer.parseInt(taskyear) + 1) + "-" + "01-01");
				} else {
					d2 = fo.parse(taskyear + "-" + String.valueOf(Integer.parseInt(taskmonth) + 1) + "-01");
				}
				
			} else if ("2".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				String taskweek = (String) this.getFormHM().get("taskweek");
				SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
				d1 = fo.parse(taskyear + "-" +(Integer.parseInt(taskweek) * 3 -2) + "-01");
				if (Integer.parseInt(taskweek) == 4) {
					d2 = fo.parse((Integer.parseInt(taskyear)+1) + "-01-01");
				} else {
					d2 = fo.parse(taskyear + "-" +((Integer.parseInt(taskweek) + 1) * 3 -2) + "-01");
				}
			}
			Date nowDate = new Date();
			if (nowDate.getTime() >= d1.getTime() && nowDate.getTime() < d2.getTime()) {
				vo.setDate("createtime",DateStyle.getSystemTime());
			} else {
				vo.setDate("createtime",d1);
			}
			//String sql = "select * from b00 where b0110='"+b0110+"' and createtime>='"+DateStyle.dateformat(d1,"yyyy-MM-dd hh:mm:ss")+"' and createtime <='"+DateStyle.dateformat(d2,"yyyy-MM-dd hh:mm:ss")+"'";
			String sql = "select * from b00 where b0110='"+b0110+"' and createtime>="+Sql_switcher.dateValue(DateUtils.format(d1,"yyyy-MM-dd"))+" and createtime <"+Sql_switcher.dateValue(DateUtils.format(d2,"yyyy-MM-dd"))+"";
			if ("yqdt".equals(cyclename)) {
				sql += " and flag='y'";
			} else if ("dept".equals(cyclename)) {
				sql += " and flag='t'";
			}
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				i9999 = this.frowset.getInt("i9999");
				vo.setInt("i9999",i9999);
				dao.updateValueObject(vo);
				b= true;
			}else{
				dao.addValueObject(vo);
			}
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				RecordVo updatevo=new RecordVo("b00");
				vo.setString("b0110",b0110);
				if(b)
				{
					updatevo.setInt("i9999",i9999);
					recid=i9999;
				}
				else
					updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob(updatevo, file,b0110,recid);
			 	updatevo.setObject("ole",blob);	
				dao.updateValueObject(updatevo);
			}
		}	
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);			
		}
	}
	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(RecordVo vo, FormFile file,String b0110,int recid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select ole from ");
		strSearch.append("b00 where b0110='");
		strSearch.append(b0110);
		strSearch.append("' and i9999=");
		strSearch.append(recid);
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append("b00 set ole=EMPTY_BLOB() where b0110='");
		strInsert.append(b0110);
		strInsert.append("' and i9999=");
		strInsert.append(recid);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    Blob blob = null;
	    InputStream is = null;
	    try{
	        is = file.getInputStream();
	        blob = blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    finally{
	       PubFunc.closeResource(is); 
	    }
	   
		return blob;
	}
}
