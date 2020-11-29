package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存个人总结信息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 1, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveCommentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("_opt");   // 1:保存附件  2：保存报告 3删除附件 4提交报告
		String planid=(String)this.getFormHM().get("planid");
		String object_id=(String)this.getFormHM().get("object_id");
		String errorInfo="";
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if ("2".equals(opt)|| "4".equals(opt)) {
				String summary=(String)this.getFormHM().get("summary");
				summary = PubFunc.hireKeyWord_filter(summary); // 刘蒙
				int article_id=0;
				this.frowset = dao
				.search("select * from per_article where plan_id="
						+ planid + " and a0100='"
						+ this.userView.getA0100()
						+ "' and lower(nbase)='"
						+ this.userView.getDbname().toLowerCase()
						+ "'  and article_type=2 and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(planid,2, 1);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", summary);
				
				
				boolean isSub=true;
				if("4".equals(opt))
				{
					dao.update("delete from per_article where plan_id="
						+ planid + " and a0100='"
						+ this.userView.getA0100()
						+ "' and lower(nbase)='"
						+ this.userView.getDbname().toLowerCase()
						+ "'  and article_type=2 and fileflag=1 and article_id!="+article_id);
					isSub=isSub(summary,planid,this.userView.getA0100(),this.userView.getDbname(),2);
					if(isSub)
						vo.setInt("state",1);
					else
						vo.setInt("state",0);
					vo.setString("description", "");
				}
				if("2".equals(opt))
				{
					vo.setInt("state",0);
					vo.setString("description", "");
				}
				dao.updateValueObject(vo);
				
				if(!isSub)
				{
					errorInfo=ResourceFactory.getProperty("info.appleal.state17")+"!";
				//	throw GeneralExceptionHandler.Handle(new Exception("不允许提交没有内容的绩效报告!"));	
				}
				
			} else if ("1".equals(opt)) // 上传附件
			{
				FormFile form_file = (FormFile) getFormHM().get("file");
				String fileName=(String)this.getFormHM().get("fileName");
				fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
				int index = fileName.lastIndexOf(".");
				if (index<0){//无后缀名，从FormFile文件中取
				    if (form_file != null && form_file.getFileData().length > 0) {
				        String fname = form_file.getFileName();
				        String _name = fileName;
	                    int indexInt = fname.lastIndexOf(".");
	                    String _ext = "";
	                    if (indexInt>0)
	                        _ext = fname.substring(indexInt + 1, fname.length());
				        _name = substrChinese(_name,50-_ext.length()-1);//去掉后缀长度 .xls .doc等等
				        if (_ext.length()>0)
				            fileName = _name+"."+_ext;
				        else 
				            fileName = _name; 
				    }
				}
				else {
				    String _name = fileName.substring(0, index);
				    String _ext = fileName.substring(index + 1, fileName.length());
				    _name = substrChinese(_name,50-_ext.length()-1);//去掉后缀长度 .xls .doc等等
				    fileName = _name+"."+_ext;
				}
				String file_max_size="512";
				if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0)
				{
					file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
					if(file_max_size.toLowerCase().indexOf("k")!=-1)
						file_max_size=file_max_size.substring(0,file_max_size.length()-1);
				}
				
				
				
				if (form_file != null && form_file.getFileData().length > 0
						&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {
	
					String fname = form_file.getFileName();
					if(fname.getBytes().length > 40) {
						errorInfo = ResourceFactory.getProperty("label.resource.upfile.lengthoverError").replace("{0}", "40");
					}else {
						int indexInt = fname.lastIndexOf(".");
						String ext ="";
						if (indexInt>0)
	                        ext = fname.substring(indexInt + 1, fname.length());
						int article_id = insertPerArticleRecord(planid, 2, 2);
		
						String sql = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
						try(
						PreparedStatement pt = this.getFrameconn()
								.prepareStatement(sql);
						) {
							pt.setString(1, ext);
							// blob字段保存,数据库中差异
							switch (Sql_switcher.searchDbServer()) {
								case Constant.ORACEL:
									Blob blob = getOracleBlob(form_file, "per_article",
											article_id);
									pt.setBlob(2, blob);
									pt.setString(3, fileName);
									pt.setInt(4, article_id);
									break;
								default:
									byte[] data = form_file.getFileData();
									// a_vo.setObject("affix",data);
									pt.setBytes(2, data);
									pt.setString(3, fileName);
									pt.setInt(4, article_id);
									break;
							}
							// 打开Wallet
							dbS.open(this.getFrameconn(), sql);
							pt.execute();
						}
					}
					
				}else if(form_file.getFileData().length > Integer.parseInt(file_max_size)*1024) {//不能超过制定的大小
					errorInfo = ResourceFactory.getProperty("lable.resource.upfile.sizeoverErrors").replace("{0}", file_max_size);
				}
				
				
				
				
				String summary=(String)this.getFormHM().get("summary");
				summary = PubFunc.hireKeyWord_filter(summary); // 刘蒙
				int article_id=0;
				this.frowset = dao
				.search("select * from per_article where plan_id="
						+ planid + " and a0100='"
						+ this.userView.getA0100()
						+ "' and lower(nbase)='"
						+ this.userView.getDbname().toLowerCase()
						+ "'  and article_type=2 and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(planid,2, 1);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", summary);
				vo.setInt("state",0);
				vo.setString("description", "");
				dao.updateValueObject(vo);
				
				
				
			} else if ("3".equals(opt)) // 删除上传附件
			{
	
				String article_id = (String) hm.get("article_id");
				dao.delete("delete from per_article where article_id="
						+ article_id, new ArrayList());
	
			}
			this.getFormHM().put("errorInfo",errorInfo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);				
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**考评结果表*/
	}
	
	
//	新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag)
	{
		int article_id=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel("per_article");
			RecordVo avo=new RecordVo("per_article");
			article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
			avo.setInt("article_id", article_id);
			avo.setInt("plan_id",Integer.parseInt(planid));
			String b0110="";String e0122="";String e01a1="";
			this.frowset=dao.search("select b0110,e0122,e01a1 from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"'");
			if(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
				e0122=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
				e01a1=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase",this.userView.getDbname());
			avo.setString("a0100",this.userView.getA0100());
			avo.setString("a0101",this.userView.getUserFullName());
			avo.setInt("article_type", article_type);
			avo.setInt("fileflag",fileflag);
			avo.setInt("state",0);
			dao.addValueObject(avo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return article_id;
	}
	
	//是否可提交 
	public boolean isSub(String context,String plan_id,String a0100,String nbase,int article_type)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select count(*) from per_article where article_type="+article_type+" and  plan_id="+plan_id+" and fileflag=2 and a0100='"+a0100+ "' and lower(nbase)='"+nbase.toLowerCase()+"'");
			int n=0;
			if(rowSet.next())
				n=rowSet.getInt(1);
			if(n==0&&(context==null||context.trim().length()==0))
				flag=false;
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String tablename,int article_id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix from ");
		strSearch.append(tablename);
		strSearch.append(" where article_id=");
		strSearch.append(article_id);		
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
		strInsert.append(article_id);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    InputStream in = null;
	    try{
	    	in = file.getInputStream();
	    	Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    	return blob;
	    }finally{
	    	PubFunc.closeResource(in);
	    }
	}
    /**  
     * 按长度截取字符串  zhaoxg add 2014-10-28
     *   
     * @param content 输入的内容  
     * @param maxSize 最大长度  
     * @return  
     */  
    public String substrChinese(String content, int maxSize) {   
        String result = content;   
        if (result!=null&&result.length()>0) {   
            int valueLength = 0;   
            int valuelength = 0;//用来截断的长度
            String chinese = "[\u0391-\uFFE5]";   
            /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */  
            for (int i = 0; i < result.length(); i++) {   
                /* 获取一个字符 */  
                String temp = result.substring(i, i + 1);   
                /* 判断是否为中文字符 */  
                if (temp.matches(chinese)) {   
                    /* 中文字符长度为2 */  
                    valueLength += 2;   
                    valuelength++;
                    if (valueLength >= maxSize) {   
                        result = result.substring(0, valuelength);   
                        break;
                    }   
                } else {   
                    /* 其他字符长度为1 */  
                    valueLength += 1;  
                    valuelength++;
                    if (valueLength >= maxSize) {   
                        result = result.substring(0, valuelength);  
                        break;
                    }   
                }   
            }   
        }   
        return result;   
    }  
}
