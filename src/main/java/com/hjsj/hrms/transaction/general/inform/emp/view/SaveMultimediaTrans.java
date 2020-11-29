package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.UUID;
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
		
		String multimediaflag = (String)this.getFormHM().get("filetype");
		String filepath = (String)this.getFormHM().get("filepath");
		String title = (String)this.getFormHM().get("filetitle");
		title = PubFunc.hireKeyWord_filter(title);
		String isvisible=(String)this.getFormHM().get("isvisible");
		if(title==null || "".equals(title))
			title = this.getFileTitle(filepath);
		
		this.getFormHM().put("isvisible", isvisible);
		this.getFormHM().put("i9999","");
		this.getFormHM().put("flag",multimediaflag);
		this.getFormHM().put("filepath","");
		this.getFormHM().put("filetitle","");
		FormFile file=(FormFile)getFormHM().get("picturefile");
		MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);
        multiMediaBo.initParam();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
		String filetype = this.getPostfix(filepath);
		InputStream in = null;
		try {
    		byte[] bytes = null;
    		//add by wangchaoqun on 2014-9-12 begin 
    		try {
    			//判断文件后缀是否正确
    			if(!FileTypeUtil.isFileTypeEqual(file)){
    				this.getFormHM().put("fileHasPro","true");
    				return;
    			}
    			//处理图形文件，返回处理后的流或字节
    			if(".jpg".equals(filetype) || ".gif".equals(filetype) || ".bmp".equals(filetype) || ".png".equals(filetype)){
    				in = ImageBO.imgStream(file, filetype);
    				bytes = ImageBO.imgByte(file, filetype);
    			}else{
    				in = file.getInputStream();
    				bytes = file.getFileData();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		//add by wangchaoqun on 2014-9-12 end
    		String fileName = file.getFileName();
    		int maxi9999 = this.getMaxI9999(kind,a0100,dbname,dao);
    		if(maxi9999<1)
    		{
    			this.insert(kind,a0100,dbname,title,multimediaflag,filetype,dao);
    			this.updateEmail_attach(kind,in,bytes,a0100,dbname,"1",dao,fileName);
    		}else{
    			String geti9999 = maxi9999+1+"";
    			this.insert(kind,a0100,dbname,title,multimediaflag,filetype,geti9999,dao);
    			this.updateEmail_attach(kind,in,bytes,a0100,dbname,geti9999,dao,fileName);
    		}
		} finally {
           PubFunc.closeResource(in);
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
		}else if("9".equals(kind))
		{
			sb.append(" select max(i9999) as i9999 from H00 ");
			sb.append(" where h0100='"+a0100+"' ");
		}else 
		{
			sb.append(" select max(i9999) as i9999 from b00 ");
			sb.append(" where b0110='"+a0100+"' ");
		}

		try {
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
			sb.append("(a0100,i9999,title,flag,ext,state,");
		}else if("0".equals(kind))
		{
			sb.append("insert into k00 ");
			sb.append("(e01a1,i9999,title,flag,ext,state,");
		}else if("9".equals(kind))
		{
			sb.append("insert into H00 ");
			sb.append("(h0100,i9999,title,flag,ext,state,");
		}else 
		{
			sb.append("insert into b00 ");
			sb.append("(b0110,i9999,title,flag,ext,state,");
		}
		//liuy 2014-11-3 单位多媒体维护，多媒体名称很长时，前台保存不上，后台报错 start
		String temp=PubFunc.splitString(title, 40);
        if(title!=temp){
        	title=temp;
        }
        //liuy end
		sb.append("CreateTime,CreateUserName)");
		sb.append(" values ");
		sb.append("('"+a0100+"',1,'"+title+"','"+flag+"','"+filetype+"',3,");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'"+this.userView.getUserName()+"'");
		sb.append(")");
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
			sb.append("(a0100,i9999,title,flag,ext,state,");
		}else if("0".equals(kind))
		{
			sb.append("insert  into k00 ");
			sb.append("(e01a1,i9999,title,flag,ext,state,");
		}else if("9".equals(kind))
		{
			sb.append("insert into H00 ");
			sb.append("(h0100,i9999,title,flag,ext,state,");
		}else 
		{
			sb.append("insert  into b00 ");
			sb.append("(b0110,i9999,title,flag,ext,state,");
		}
		//liuy 2014-11-3 单位多媒体维护，多媒体名称很长时，前台保存不上，后台报错 start
		String temp=PubFunc.splitString(title, 40);
        if(title!=temp){
        	title=temp;
        }
        //liuy end
		sb.append("CreateTime,CreateUserName)");
		sb.append(" values ");
		sb.append("('"+a0100+"',"+i9999+",'"+title+"','"+flag+"',");
		sb.append("'"+filetype+"',3,");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'"+this.userView.getUserName()+"'");
		sb.append(")");
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
		try
		{
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
	public void updateEmail_attach(String kind,InputStream in,byte[] bytes,String a0100,String dbpre,String i9999,ContentDAO dao,
			String fileName)
	{
		try
		{
			
			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
			String userName = this.userView.getUserName();
			if("6".equals(kind))
			{
				RecordVo vo = new RecordVo(dbpre+"a00");
			    vo.setString("a0100",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
			    RecordVo a_vo =dao.findByPrimaryKey(vo);
	            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
	            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
	            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, getGuidKey(dbpre+"a01", a0100, "1"), 
	            		in, fileName, "", false);	
	            a_vo.setString("fileid",fieldId);
			    dao.updateValueObject(a_vo);
			}else if("0".equals(kind))
			{
				RecordVo vo = new RecordVo("k00");
			    vo.setString("e01a1",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
			    RecordVo a_vo =dao.findByPrimaryKey(vo);
			    VfsModulesEnum vfsModulesEnum = VfsModulesEnum.JG;
	            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.post;
	            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, getGuidKey("organization", a0100, "2"), 
	            		in, fileName, "", false);	
	            a_vo.setString("fileid",fieldId);
			    dao.updateValueObject(a_vo);
			    
			}else if("9".equals(kind)){
				
				RecordVo vo = new RecordVo("H00");
			    vo.setString("h0100",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
			    RecordVo a_vo =dao.findByPrimaryKey(vo);
			    VfsModulesEnum vfsModulesEnum = VfsModulesEnum.JG;
	            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
	            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, null, 
	            		in, fileName, "", false);	
	            a_vo.setString("fileid",fieldId);
			    dao.updateValueObject(a_vo);
			    
			}else 
			{
				RecordVo vo = new RecordVo("b00");
			    vo.setString("b0110",a0100);
			    vo.setInt("i9999",Integer.parseInt(i9999));
			    RecordVo a_vo =dao.findByPrimaryKey(vo);
			    VfsModulesEnum vfsModulesEnum = VfsModulesEnum.JG;
	            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.unit;
	            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, getGuidKey("organization", a0100, "2"), 
	            		in, fileName, "", false);	
	            a_vo.setString("fileid",fieldId);
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
	 * 获取人员主集的guidkey
	 * 
	 * @param tableName
	 *            人员库
	 * @param a0100
	 *            人员编号
	 * @param flag
	 *            =1:人员；=2：单位/部门；=3：岗位；=4：基准岗位
	 * @return
	 */
	private String getGuidKey(String tableName, String id, String flag) {
		String guid = "";
		try {
			String keyField = "A0100";
			if("1".equals(flag)) {
				keyField = "A0100";
			} else {
				keyField = "codeitemid";
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("select GUIDKEY from ");
			sb.append(tableName);
			sb.append(" where " + keyField + "=?");
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(id);

			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString(), paramList);
			StringBuffer stmp = new StringBuffer();
			stmp.append("update  ");
			stmp.append(tableName);
			stmp.append(" set GUIDKEY =?");
			stmp.append(" where " + keyField + "=?");
			stmp.append(" and guidkey is null ");
			if (this.frowset.next()) {
				guid = this.frowset.getString("guidkey");
				if (StringUtils.isEmpty(guid)) {
					UUID uuid = UUID.randomUUID();
					guid = uuid.toString();
					paramList.add(0, guid);
					dao.update(stmp.toString(), paramList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return guid;
	}
}
