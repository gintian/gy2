package com.hjsj.hrms.module.recruitment.util;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.transaction.SendMsgIsSuccess;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/***
 * 查询邮件模板
 * <p>Title: EmailInfoBo </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-6-3 下午04:31:11</p>
 * @author xiexd
 * @version 1.0
 */
public class EmailInfoBo {
	private Connection conn=null;
    private UserView userview;
    
    public EmailInfoBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    /**
     * 得到当前邮件所需要的信息
     * @param a0100 人员编号
     * @return
     */
    public LazyDynaBean getInfo(String a0100, String z0301)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	ContentDAO dao = new ContentDAO(conn);
    	StringBuffer sqlstr = new StringBuffer();
    	ArrayList list = new ArrayList();
    	sqlstr.append(" select Z0351,Z0375,custom_name,codeitemdesc UM,");
    	sqlstr.append(" (select codeitemdesc from organization ");
    	sqlstr.append(" where codeitemid = (select parentid from Z03 ,zp_pos_tache,organization ");
    	sqlstr.append(" where Z03.Z0301 = zp_pos_tache.ZP_POS_ID and z03.Z0325=organization.codeitemid  and zp_pos_tache.A0100 = ? and z03.z0301 = ?)) UN");
    	sqlstr.append(" from Z03 ,zp_pos_tache,organization,zp_flow_links where Z03.Z0301 = zp_pos_tache.ZP_POS_ID");
    	sqlstr.append(" and zp_pos_tache.link_id = zp_flow_links.id and z03.Z0325=organization.codeitemid  and zp_pos_tache.A0100 = ?");
    	list.add(a0100);
    	list.add(z0301);
    	list.add(a0100);
    	RowSet rs = null;
    	try {
			rs = dao.search(sqlstr.toString(), list);
			if(rs.next())
			{
				bean.set("Z0351",rs.getString("Z0351") );
				if(rs.getString("Z0375")!=null&&rs.getString("Z0375").length()>0)
				{					
					bean.set("Z0375",rs.getString("Z0375").substring(0,10) );
				}else{
					bean.set("Z0375","未设置");
				}
				bean.set("custom_name",rs.getString("custom_name") );
				bean.set("UM",rs.getString("UM") );
				bean.set("UN",rs.getString("UN") );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rs);
		}
		
    	return bean;
    }
  
    public LazyDynaBean getEmailInfo()
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	try {
    		LazyDynaBean emailBean = new LazyDynaBean();
			String emailContent = (String)emailBean.get("");
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return bean;
    }
    //得到当前流程邮件模板列表
    public String getTemplateList(String nModule,String Sub_module, String nodeId)
    {
    	ArrayList<ArrayList> templateList = new ArrayList<ArrayList>();
    	RowSet rs = null;
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
			ArrayList value = new ArrayList();
			StringBuffer sql= new StringBuffer("select id,name,nModule,nInfoclass,Subject,content,attach,address,");
			sql.append("Sub_module,Return_address,B0110,ownflag from email_name where nModule=? "); 
			sql.append("  and (valid=1 or valid is null) ");
			
			value.add(nModule);
			if("90".equalsIgnoreCase(Sub_module)){
			    sql.append(" and other_flag=?");
			    value.add(nodeId);
			}
			
		    RecruitPrivBo bo = new RecruitPrivBo();
		    String where = bo.getPrivB0110Whr(this.userview, "B0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
		    sql.append(" and " + where);
			
			if(Sub_module!=null&&Sub_module.trim().length()>0)
			{
				sql.append(" and Sub_module=? ");
				value.add(Sub_module);
			}
			
			rs=dao.search(sql.toString(),value);
			while(rs.next())
			{
				ArrayList<String> param = new ArrayList<String>();
				param.add(rs.getString("id"));
				param.add(rs.getString("name"));
				templateList.add(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rs);
		}
		
    	return JSON.toString(templateList);
    }
    /***
     * 查询当前邮件用户
    * @Title:getUserName
    * @Description：
    * @author xiexd
    * @param a0100s 人员库+人员编号
    * @return
     */
    public String getUserName(String a0100s)
    {
    	String userName = "";
    	RowSet rs = null;
    	try {
    		a0100s = a0100s.trim();
			String nbase = a0100s.substring(0, 3);
			String a0100 = a0100s.substring(3);
			StringBuffer sql = new StringBuffer();
			sql.append("select A0101 from "+nbase+"A01 where A0100 = '"+a0100+"'");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				userName = rs.getString("A0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rs);
		}
    	return userName;
    }
    /***
     * 获取邮件内容（直接从数据库中获取）
     * @param nModule模板编号
     * @param Sub_module子模板编号
     * @param b0110所属机构
     * @return
     */
    public LazyDynaBean getTemplateInfo(String nModule,String Sub_module, String id, String nodeId)
	{
		LazyDynaBean bean  = new LazyDynaBean();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList value = new ArrayList();
			StringBuffer sql= new StringBuffer(" select id,name,nModule,nInfoclass,Subject,content,attach,address,");
			sql.append(" Sub_module,Return_address,B0110,ownflag from email_name where nModule=? "); 
			sql.append("  and (valid=1 or valid is null) ");
			value.add(nModule);
			if("90".equalsIgnoreCase(Sub_module)){
                sql.append(" and other_flag=?");
			    value.add(nodeId);
			}
			
			RecruitPrivBo bo = new RecruitPrivBo();
            String where = bo.getPrivB0110Whr(this.userview, "B0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
            sql.append(" and " + where);
            
			if(Sub_module!=null&&Sub_module.trim().length()>0)
			{
				sql.append(" and Sub_module=? ");
				value.add(Sub_module);
			}
			if(id!=null&&id.trim().length()>0)
			{
				sql.append(" and id=? ");
				value.add(id);
			}
			rs=dao.search(sql.toString(),value);
			if(rs.next())
			{
				bean.set("id",rs.getString("id"));//主键Id
				bean.set("name",rs.getString("name"));//模板名称
				bean.set("nModule",rs.getString("nModule"));//模板编号
				bean.set("nInfoclass",rs.getString("nInfoclass"));//信息集
				bean.set("attach",rs.getString("attach"));//邮件附件
				bean.set("sub_module",rs.getString("Sub_module"));//子模块编号
				bean.set("return_address",(rs.getString("Return_address")!=null)?rs.getString("Return_address"):"");//回复地址
				bean.set("b0110",rs.getString("B0110"));//所属机构
				bean.set("ownflag",rs.getString("ownflag"));//系统模板  1：系统内置模板；0：自定义模板
				String emailField=rs.getString("address");//接收地址
				if(emailField!=null&&emailField.trim().length()>0)
				{
					emailField=emailField.substring(0,emailField.indexOf(":")).trim();
				} else {
				    emailField = "";
				}
				bean.set("address",emailField);
				bean.set("subject", rs.getString("subject")==null?"":rs.getString("subject"));//邮件主题
				bean.set("content", rs.getString("content")==null?"":rs.getString("content"));//邮件内容
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return bean;
	}
    /***
     * 获取邮件附件并还原文件
    * @Title:getAttachFileName
    * @Description：
    * @author xiexd
    * @param templateId
    * @return
     */
    public ArrayList getAttachFileName(String templateId)
    {
 	   ArrayList list = new ArrayList();
 	   FileOutputStream fileOut=null;
 	   RowSet rs=null;
 	   try
 	   {
 		   String sql ="select server_filename,filename,fileid from email_attach where id="+templateId;
 		   ContentDAO dao = new ContentDAO(this.conn);
 		   rs=dao.search(sql);
 		   LazyDynaBean bean = new LazyDynaBean();
 		   while(rs.next())
 		   {
 			   try{
 				   String file=rs.getString("server_filename");
 				   if(file==null)
 				   {
 					   continue;
 				   }
 				   // 20150803 xiexd 文件路径从服务器目录进行取得
 				   String fileId = rs.getString("fileid");
 				   bean = new LazyDynaBean();
 				   bean.set("filename", rs.getString("filename"));
 				   bean.set("filepath", fileId);
 				   list.add(bean);
 			   }finally{
 				   PubFunc.closeResource(fileOut);
 			   }
 		   }
 	   }
 	   catch(Exception e)
 	   {
 		   e.printStackTrace();
 	   } finally {
 	       PubFunc.closeResource(rs);
 	   }
 	   return list;
    }
    /***
     * 附件列表头
    * @Title:getFileColumn
    * @Description：
    * @return
     */
    public String getFileColumn()
	{
		StringBuffer fileColumn = new StringBuffer("[");
		try {
			fileColumn.append("{'text':'编号','width':50,'align':'center','dataIndex':'fileid','editablevalidfunc':null,'renderer':null},");
			fileColumn.append("{'text':'文件','width':250,'align':'center','dataIndex':'filename','editablevalidfunc':null,'renderer':null},");
			fileColumn.append("{'text':'类型','width':90,'align':'center','dataIndex':'extname','editablevalidfunc':null,'renderer':null}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileColumn.append("]");
		return fileColumn.toString();
	}
    /***
     *  邮件附件列表内容
    * @Title:getFileList
    * @Description：
    * @author xiexd
    * @param templateId
    * @return
     */
    public String getFileContentList(String templateId)
    {
    	StringBuffer fileList = new StringBuffer("[");
    	RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select filename,extname from email_attach where id="+templateId+"");
			rs = dao.search(sql.toString());
			int fileId = 1; 
			while(rs.next()) {
				fileList.append("{'fileid':'"+fileId+"','filename':'"+rs.getString("filename")+"','extname':'"+rs.getString("extname")+"'},");
				fileId++;
			}
			
			if(fileId > 1)
			    fileList.deleteCharAt(fileList.length()-1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rs);
		}
		
		fileList.append("]");
		return fileList.toString();
    }
    /**
     * 发送邮件
    * @Title:sendEmail
    * @Description：
    * @author xiexd
    * @param email 邮箱
    * @param title 邮件标题
    * @param content 邮件内容
    * @param emailFile 邮件附件
    * @return
     */
    public String sendEmail(String email,String title,String content,ArrayList emailFile,String returnAddress)
	{
		String msg = "";
		try {
			SendMsgIsSuccess sendMsgIsSuccess=new SendMsgIsSuccess();
			AsyncEmailBo emailbo = new AsyncEmailBo(conn, userview, sendMsgIsSuccess);
			String fromAddr=this.getFromAddr();
			boolean bool = true;
			if(fromAddr==null|| "".equals(fromAddr.trim()))
			{
				msg="系统未设置邮件服务器！";
				bool = false;
			}
			LazyDynaBean emails = new LazyDynaBean();
			emails.set("subject", title);
			emails.set("bodyText", content);
			emails.set("toAddr", email);
			emails.set("attachList", emailFile);
			emails.set("returnAddress", returnAddress);
			try
			{
				if(email!=null&&email.length()>0)
				{					
					if(bool)
					{
						emailbo.send(emails);
						msg = "1";
					}
				}else{
					msg="当前简历邮箱信息为空！";
				}
			}
			catch(Exception e)
			{
				msg="系统邮件服务器配置不正确，请联系系统管理员！";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
    
    /**
     *获取邮件信息对象
    * @Title:sendEmail
    * @Description：
    * @author wangjl
    * @param email 邮箱
    * @param title 邮件标题
    * @param content 邮件内容
     * @param content2 
    * @param emailFile 邮件附件
    * @return
     */
    public String getEmailBean(ArrayList beans, String templateId, String emailContent,String title,String content,ArrayList emailFile,String returnAddress)
	{
		String msg = "";
		LazyDynaBean email = new LazyDynaBean();
		email.set("templateId", templateId);
		email.set("subject", title);
		email.set("bodyText", content);
		email.set("toAddr", emailContent);
		email.set("attachList", emailFile);
		email.set("returnAddress", returnAddress);
		email.set("a0100", this.userview.getA0100());
		email.set("username", this.userview.getUserFullName());
		if(emailContent!=null&&emailContent.length()>0)
			beans.add(email);
		else{
			msg="当前简历邮箱信息为空！";
		}
		return msg;
	}
    

    /**
     * 批量发送邮件
     * @param beans
     * @return
     */
    public String bulkSendEmail(ArrayList<LazyDynaBean> beans)
	{
		String msg = "";
		try {
			SendMsgIsSuccess sendMsgIsSuccess = new SendMsgIsSuccess();
			AsyncEmailBo emailbo = new AsyncEmailBo(conn, userview, sendMsgIsSuccess);
			String fromAddr = this.getFromAddr();
			boolean bool = true;
			if(StringUtils.isEmpty(fromAddr))
			{
				msg="系统未设置邮件服务器！";
				bool = false;
			}
			if(bool)
				emailbo.send(beans);
		} catch (Exception e) {
			msg="系统邮件服务器配置不正确，请联系系统管理员！";
			e.printStackTrace();
		}
		return msg;
	}
    
    /***
     * 查询邮件服务器是否配置
    * @Title:getFromAddr
    * @Description：
    * @author xiexd
    * @return
    * @throws GeneralException
     */
	public String getFromAddr() throws GeneralException {
		String str = "";
		RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (stmp_vo == null)
			return "";
		String param = stmp_vo.getString("str_value");
		if (param == null || "".equals(param))
			return "";
		Document doc = null;
		try {
			doc = PubFunc.generateDom(param);
			Element root = doc.getRootElement();
			Element stmp = root.getChild("stmp");
			str = stmp.getAttributeValue("from_addr");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(doc);
		}
		return str;
	}
	/**
	 * 取得模板指标或公式的信息
	 * @param templateId
	 * @return
	 */
	public ArrayList getTemplateFieldInfo(int templateId,int type)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			String sql = "select * from email_field where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id",String.valueOf(templateId));
				bean.set("fieldid",rs.getString("fieldid")==null?"":rs.getString("fieldid").trim());
				bean.set("fieldtitle",rs.getString("fieldtitle")==null?"":rs.getString("fieldtitle").trim());
				bean.set("fieldtype",rs.getString("fieldtype"));
				if(type==1)
				{
			    	  bean.set("fieldcontent",rs.getString("fieldcontent"));
				}
				else
				{
					bean.set("fieldcontent",rs.getString("fieldcontent"));
				}
				bean.set("dateformat",rs.getString("dateformat"));
				bean.set("fieldlen",rs.getString("fieldlen"));
				bean.set("ndec",rs.getString("ndec"));
				bean.set("codeset",rs.getString("codeset")==null?"":rs.getString("codeset").trim());
				bean.set("nflag",rs.getString("nflag"));
				bean.set("fieldSet", rs.getString("FieldSet")==null?"":rs.getString("FieldSet").trim());
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	private String inserttime="";
	public String getInserttime() {
		return inserttime;
	}
	public void setInserttime(String inserttime) {
		this.inserttime = inserttime;
	}
	/***
	 * 查询系统自定义字段项
	 * @return
	 */
	public ArrayList getSysItems()
	{
		ArrayList sys_items = new ArrayList();//系统自定义字段
    	java.util.Calendar c=java.util.Calendar.getInstance();    
        java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");
		String orgDesc = "";
		String deptDesc = "";
		RowSet deptrs = null;
		RowSet codeRs = null;
		RowSet orgrs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			Boolean flag = true;
			String b0100 = userview.getUserOrgId();
			deptrs = dao.search("select codeitemdesc from organization where codeitemid = '"+b0100+"' ");
			if(deptrs.next())
			{
				deptDesc = deptrs.getString("codeitemdesc");
			}
			while(flag)
    		{
    			codeRs = dao.search("select codeitemid,parentid from  organization where codeitemid='"+b0100+"'");
    			if(codeRs.next())
    			{    				
    				String parentid = codeRs.getString("parentid");
    				b0100 = codeRs.getString("codeitemid");
    				if(parentid.equalsIgnoreCase(b0100))
    				{
    					flag = false;
    				}else{
    					b0100 = parentid;
    				}
    			}else{
    				codeRs = dao.search("select codeitemid from organization  where codeitemid=parentid");
    				if(codeRs.next())
        			{    				
        				b0100 = codeRs.getString("codeitemid");
        				flag = false;
        			}
    			}
    		}
			orgrs = dao.search("select codeitemdesc from organization where codeitemid = '"+b0100+"' ");
			if(orgrs.next())
			{
				orgDesc = orgrs.getString("codeitemdesc");
			}
			LazyDynaBean sys_item = null;
			sys_item = new LazyDynaBean();
			sys_item.set("itemdesc","联系人");
			sys_item.set("itemid", userview.getUserFullName()==null?"(未填写联系人)":userview.getUserFullName());
			sys_items.add(sys_item);
			sys_item = new LazyDynaBean();
			sys_item.set("itemdesc","联系人电话");
			sys_item.set("itemid", userview.getUserTelephone()==null?"(未填写电话)":userview.getUserTelephone());
			sys_items.add(sys_item);
			sys_item = new LazyDynaBean();
			sys_item.set("itemdesc","联系人部门");
			sys_item.set("itemid", deptDesc);
			sys_items.add(sys_item);
			sys_item = new LazyDynaBean();
			sys_item.set("itemdesc","联系人单位");
			sys_item.set("itemid", orgDesc);
			sys_items.add(sys_item);
			sys_item = new LazyDynaBean();
			sys_item.set("itemdesc","发件日期");
			sys_item.set("itemid", f.format(c.getTime()));
			sys_items.add(sys_item);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(orgrs);
		    PubFunc.closeResource(codeRs);
		    PubFunc.closeResource(deptrs);
		}
		return sys_items;
	}
	/**
	 * 替换系统项
	 * @param content
	 * @return
	 */
	public String getSysContent(String content)
	{
	    if (StringUtils.isEmpty(content)) {
	        return "通知";
	    }
	        
		String sys_content = content;
		if(content!=null && !"".equals(content)){
			ArrayList sys_items = this.getSysItems();
			for(int i=0;i<sys_items.size();i++)
			{
				String replace="";//要被替换的内容
				LazyDynaBean itemBean = (LazyDynaBean)sys_items.get(i);
				String itemId = (String)itemBean.get("itemid");
				String itemdesc = (String)itemBean.get("itemdesc");
				replace="\\$sys:"+itemdesc.trim()+"\\$";
				sys_content=sys_content.replaceAll(replace,itemId);
			}
		}
		return sys_content;
	}
	/**
	 * 取得实际发送内容
	 * @param content
	 * @param prea0100
	 * @param fieldList
	 * @return
	 */
	public String getFactContent(String content,String prea0100,ArrayList fieldList,UserView uv,String z0301){
		//getFactContent这个方法在多个地方被调用 为了新加一个参数又不改其他地方的代码   现在新加一个方法 供人岗推荐中发送邮件使用
		return getFactContent(null, content, prea0100, fieldList, uv, z0301);
	}
	public String getFactContent(String nomodule,String content,String prea0100,ArrayList fieldList,UserView uv,String z0301)
	{
		String fact_content=content;
		RowSet rs = null;
		try
		{
			z0301 = z0301.trim();
			prea0100 = prea0100.trim();
			String pre=prea0100.substring(0,3).trim();
			String a0100=prea0100.substring(3).trim();
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name=new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql=new StringBuffer();
			StringBuffer where_sql2= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<fieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)fieldList.get(i);
				String id=(String)bean.get("id");
				String fieldtitle=(String)bean.get("fieldtitle");
				String fieldtype=((String)bean.get("fieldtype")).trim();
				String fieldcontent=(String)bean.get("fieldcontent");
				fieldcontent = fieldcontent.trim();
				String fieldid=(String)bean.get("fieldid");
				String dateformat=(String)bean.get("dateformat");
				String fieldlen=(String)bean.get("fieldlen");
				String ndec=(String)bean.get("ndec");
				String codeset=(String)(bean.get("codeset")==null?"":bean.get("codeset"));
				String fieldsetid=(String)(bean.get("fieldSet")==null?"":bean.get("fieldSet"));
				if("sys".equalsIgnoreCase(fieldsetid))
					continue;
				String nflag=(String)bean.get("nflag");
				String replace="";//要被替换的内容
				String factcontent="";
				String setid="";
				if("0".equals(nflag))
				{
					replace="\\$"+fieldid+":"+fieldtitle.trim()+"\\$";
				}
				if("1".equals(nflag))
				{
					replace="\\#"+fieldid+":"+fieldtitle.trim()+"\\#";
				}
				/**指标和公式项目处理不同*/
				if("0".equals(nflag))//指标
				{
					if(StringUtils.isEmpty(fieldsetid)) {
						if("A0101".equalsIgnoreCase(fieldcontent.trim())|| "B0110".equalsIgnoreCase(fieldcontent.trim())|| "E0122".equalsIgnoreCase(fieldcontent.trim())|| "e01a1".equalsIgnoreCase(fieldcontent.trim()))
							fieldsetid="a01";
						else
							fieldsetid=getFieldSetId(fieldcontent.trim());
						
						if(StringUtils.isEmpty(fieldsetid))
							fieldsetid = getbusiField(fieldcontent.trim());
						
					}
					if("Z03".equalsIgnoreCase(fieldsetid))
					{
						buf.append("select ");
						String sqlstr = "";
						if("UN".equalsIgnoreCase(codeset)||"UM".equalsIgnoreCase(codeset)||"@K".equalsIgnoreCase(codeset))
						{
							buf.append(" organization.codeitemid ");
							sqlstr = " left join organization on "+fieldsetid+"."+fieldcontent+"=organization.codeitemid ";
						}
						if("link_id".equals(fieldcontent))
						{
							buf.append(" custom_name ");
							sqlstr = "";
						}
						buf.append(fieldcontent);
						buf.append(" from ");
						buf.append(fieldsetid);
						buf.append(sqlstr);
						buf.append(" where ");
						buf.append("z0301='");
						buf.append(z0301+"'");
					}else if("Z05".equalsIgnoreCase(fieldsetid)){
						buf.append("select ");
						String sqlstr = "";
						if("link_id".equalsIgnoreCase(fieldcontent.trim()))
						{
							buf.append(" custom_name ");
							sqlstr = " from  Z03 left join zp_pos_tache on z03.Z0301=zp_pos_tache.ZP_POS_ID left join zp_flow_links " +
									"on zp_pos_tache.link_id=zp_flow_links.id where A0100='"+a0100+"' and  ZP_POS_ID='"+z0301+"'";
						}else{
							sqlstr = " from "+fieldsetid+" where A0100='"+a0100+"'";
						}
						buf.append(fieldcontent);
						buf.append(sqlstr);
					}else{
						if(StringUtils.isEmpty(fieldsetid))
							continue;	
						
						buf.append("select ");
						buf.append(fieldcontent);
						buf.append(" from ");
						buf.append(pre+fieldsetid);
						buf.append(" where ");
						buf.append(pre+fieldsetid+".a0100='");
						buf.append(a0100+"'");
						if(!"a01".equalsIgnoreCase(fieldsetid))
						{
							buf.append(" and ");
							buf.append(pre+fieldsetid+".i9999=(select max(i9999) from ");
							buf.append(pre+fieldsetid);
							buf.append(" where ");
							buf.append(pre+fieldsetid+".a0100='");
							buf.append(a0100+"')");
						}						
					}
					rs=dao.search(buf.toString());
					while(rs.next())
					{			
						// orcle库遇到日期型的字段后台会报错
						if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))  // 代码型
						{
							factcontent=rs.getString(fieldcontent.trim());
							
						}else if("N".equalsIgnoreCase(fieldtype.trim()))  // 数值型按格式显示
						{
							factcontent=rs.getString(fieldcontent.trim());
							
						}else if("D".equalsIgnoreCase(fieldtype.trim()))  // 日期型按格式显示
						{
							java.sql.Date dd=rs.getDate(fieldcontent.trim());
							if(dd!=null){
								SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
								factcontent=format.format(dd);
							}
							
						}else 
						{
							factcontent=rs.getString(fieldcontent.trim());
						}
						
					}
					
					if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))//代码型
					{
						factcontent=AdminCode.getCodeName(codeset,factcontent);
					}
					
					/**日期型按格式显示*/
					if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
						factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
					/**数值型按格式显示*/
					if("N".equalsIgnoreCase(fieldtype.trim()))
						factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
					if(factcontent==null||factcontent.trim().length()==0)
		    		{
		    			if("N".equalsIgnoreCase(fieldtype.trim()))
		    				factcontent="0.0";
		    			else
		    				factcontent=" ";
		    		}
					
					if(factcontent==null||factcontent.trim().length()==0)
					{						
						factcontent = " ";
					}
					//用于前台替换时间和地址
					if("Z0503".equalsIgnoreCase(fieldcontent.trim()))
					{
						factcontent = "(面试地址)";
					}else if("Z0509".equalsIgnoreCase(fieldcontent.trim()))
					{
						factcontent = "(面试日期)";
					}
					fact_content=fact_content.replaceAll(replace,factcontent);
				    buf.setLength(0);
					table_name.setLength(0);
					where_sql.setLength(0);
					where_sql2.setLength(0);
				
				}
				if("1".equals(nflag))//公式
				{
					ArrayList list = new ArrayList();
					String zh_sql=getSql(fieldcontent,fieldtype,uv,list);
					if(zh_sql==null||zh_sql.length()<=0)
						continue;
					if("D".equalsIgnoreCase(fieldtype.trim()))//日期型是否直接查
					{
					    setid=getFieldSetId(zh_sql.trim());
						buf.append("select ");
						buf.append(zh_sql);
						buf.append(" from ");
						if(setid!=null&&!"".equals(setid))
							buf.append(pre+setid);
						else
							buf.append(" usra01 ");
						buf.append(" where a0100='");
						buf.append(a0100+"'");
					}
					else
					{
				    	if(list.size()>0)
			    		{
				    		buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
			    			for(int j=0;j<list.size();j++)
			    			{
			    				String fieldSetid = (String)list.get(j);
					    		String tableName=pre+fieldSetid;
					    		name_set.add(tableName);
					    		if(j!=0)
						    		where_sql2.append(" and ");
					     		where_sql2.append(tableName+".a0100='");
					      		where_sql2.append(a0100+"'");
					    		if(!"a01".equalsIgnoreCase(fieldSetid))
					    		{
					    			if(j!=0)
					    				where_sql.append(" and ");
				    	    		where_sql.append(tableName);
					        		where_sql.append(".i9999=(");
				         			where_sql.append("select max(i9999) from ");
					        		where_sql.append(tableName+" "+tableName);
					        		where_sql.append(" where ");
					        		where_sql.append(tableName+".a0100='");
					        		where_sql.append(a0100+"') ");
					    		}
					    	}
				    		buf.append(name_set.toString().substring(1,name_set.toString().length()-1));
				    		buf.append(" where ");
				    		buf.append(where_sql);
					    	if(where_sql.length()>0)
					    	      buf.append(" and ");
			    	    	buf.append(where_sql2);
			    		}
			    		else
			    		{
			    			buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
				    		buf.append("usra01 where ");
				    		buf.append("a0100='");
				    		buf.append(a0100+"'");
				    	}	
			    	}					
					rs=dao.search(buf.toString());
		    		while(rs.next())
		    		{
		    			if("D".equalsIgnoreCase(fieldtype))
		    			{
	    					if(rs.getDate(1)!=null)
	    					{
	    						java.sql.Date dd=rs.getDate(1);
		    			        SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		    			        factcontent=format.format(dd);
	    					}
	    					else
	    					{
	    						factcontent="";
	    					}
		    			}
		    			else
	    	    			factcontent=rs.getString(1);
		    		}
		    		
		    		/**日期型按格式显示*/
					if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
						factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
					/**数值型按格式显示*/
					if("N".equalsIgnoreCase(fieldtype.trim()))
						factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
					if(factcontent==null||factcontent.trim().length()==0)
		    		{
		    			if("N".equalsIgnoreCase(fieldtype.trim()))
		    				factcontent="0.0";
		    			else
		    				factcontent=" ";
		    		}
					if(factcontent==null||factcontent.trim().length()==0)
					{
						factcontent = " ";
					}
				    fact_content=fact_content.replaceAll(replace,factcontent);
			    	buf.setLength(0);
			    	table_name.setLength(0);
			    	name_set.clear();
		    		where_sql.setLength(0);
		    		where_sql2.setLength(0);
				}
			
			}
			StringBuffer url = new StringBuffer(this.userview.getServerurl());
	    	url.append("/recruitment/recruitprocess/confirmInvitation.do?b_accept=link");
	    	url.append("&encryptParam=");
	    	ArrayList<String> flowParam = getFlowParam(a0100, z0301, pre);
	    	if(flowParam.size()>0){
	    				fact_content=fact_content.replaceAll("\\$sys:确认参加\\$", "<a href =\""+url+PubFunc.encryption("a0100="+a0100+"&zp_pos_id="+z0301+"&link_id="+flowParam.get(0)+"&status="+flowParam.get(1)+"&confirm=1")+"\" target=\"_blank\">确认</a>"
	    						+ "&nbsp;&nbsp;&nbsp;<a href =\""+url+PubFunc.encryption("a0100="+a0100+"&zp_pos_id="+z0301+"&link_id="+flowParam.get(0)+"&status="+flowParam.get(1)+"&confirm=2")+"\" target=\"_blank\">放弃</a>");
	    	}
	    	if(null!=nomodule&&"91".equals(nomodule)){//zpZ0336渠道编号
	    		ParameterXMLBo xmlBo=new ParameterXMLBo(conn,"1");
				HashMap map=xmlBo.getAttributeValues();
				String candidate_status_itemId = "#";
				if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
					candidate_status_itemId=(String)map.get("candidate_status");
				EmployNetPortalBo bo  = new EmployNetPortalBo(conn);
				String hireChannel = "";
				if(!"#".equals(candidate_status_itemId)) {
					hireChannel = bo.getCandidateStatus(candidate_status_itemId,a0100);
				}
				if("#".equals(candidate_status_itemId) || StringUtils.isEmpty(hireChannel)){
					String channelSql = " select  z0336 from z03 where z0301= ? ";
					ArrayList valuelist = new ArrayList();
					valuelist.add(z0301);
			   	    rs  = dao.search(channelSql, valuelist);
			   	    while(rs.next()){
			   	    	hireChannel = rs.getString("z0336");
			   	    }
				}
	    		String zplink = "/hire/hireNetPortal/search_zp_position.do?b_posDesc=link&hireChannel="+hireChannel+"&z0301="+SafeCode.encode(z0301);//hireChannel
				fact_content += "<br><br>"+"应聘链接："+"<a href="+userview.getServerurl()+zplink+" target=\"_blank\">查看申请职位</a>";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rs);
		}
		return fact_content;
	}
	/**
	 * 将时间指标或公式按指定格式显示
	 * @param type
	 * @param time
	 * @return
	 */
	public String getYMDFormat(int type,String time)
	{
		String ret="";
		String year="";
		String month="";
		String day="";
		if(time==null||time.trim().length()<=0)
		{
			return ret;
		}
		if(time.length()>10)//是带时分秒的格式
		{
			int index=time.indexOf(" ");
			time=time.substring(0,index);
		}
		String separapor="";
		if(time.indexOf("-")!=-1)
			separapor="-";
		else if(time.indexOf(".")!=-1)
			separapor=".";
		year=time.substring(0,time.indexOf(separapor));
		month=time.substring(time.indexOf(separapor)+1,time.lastIndexOf(separapor));
		day=time.substring(time.lastIndexOf(separapor)+1);
		try
		{
			switch(type)
			{
			case 1:{
				ret=year+"."+month+"."+day;
				break;
			}
			case 2:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				if(Integer.parseInt(day)<10)
					day=day.substring(1);
				ret=year+"."+month+"."+day;
				break;
			}
			case 3:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				if(Integer.parseInt(day)<10)
					day=day.substring(1);
				year=year.substring(2);
				ret=year+"."+month+"."+day;
				break;
			}
			case 4:{
				ret=year+"."+month;
				break;
			}
			case 5:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				ret=year+"."+month; 
				break;
			}
			case 6:{
				year=year.substring(2);
				ret=year+"."+month;
				break;
			}
			case 7:{
				year=year.substring(2);
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				ret=year+"."+month;
				break;
			}
			case 8:{
				ret=year+ResourceFactory.getProperty("datestyle.year")
				    +month+ResourceFactory.getProperty("datestyle.month")
				    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 9:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				if(Integer.parseInt(day)<10)
					day=day.substring(1);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 10:{
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 11:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 12:{
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 13:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				if(Integer.parseInt(day)<10)
					day=day.substring(1);
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 14:{
				if(Integer.parseInt(month)<10)
					month=month.substring(1);
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 15:{//年限
				int cYear=Calendar.getInstance().get(Calendar.YEAR);//当前年
				int cMonth=Calendar.getInstance().get(Calendar.MONTH)+1;//当前月
				int cDay=Calendar.getInstance().get(Calendar.DATE);//当前日
				ret=cYear-Integer.parseInt(year)+"";
				break;
			}
			case 16:{//年份
				ret=year;
				break;
			}
			case 17:{//月份
				if(month.length()>2&&Integer.parseInt(month)<10)
					month=month.substring(1);
				ret=month;
				break;
			}
			case 18:{//日份
				if(day.length()>2&&Integer.parseInt(day)<10)
					day=day.substring(1);
				ret=day;
				break;
			}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 将数值型按指定的整数长度和小数长度显示
	 * @param integerlength 整数长度
	 * @param ndeclen 小数长度
	 * @param number 数值
	 * @return
	 */
	public String getNumberFormat(int integerlength,int ndeclen,String number)
	{
		String ret="";
		try
		{
			if(number==null||number.trim().length()<=0)
		    	return ret;
			/**为了不在数字前面补0，整数位只要1位*/
//			String temp=this.getFormat(integerlength);
//			if(temp.trim().length()<=0)
//				temp="0";
			 String temp="0";
		     if(ndeclen>0)
		     {
		    	 String temp2=this.getFormat(ndeclen);
		    	 temp=temp+"."+temp2;
		     }
		     if(temp.length()>0)
		     {
		    	 DecimalFormat dcom= new DecimalFormat(temp);
		    	 ret=dcom.format(Double.parseDouble(number));
		     }else
		    	 ret=number;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * get format String
	 * @param len
	 * @return
	 */
	public String getFormat(int len)
	{
		String temp="";
		try
		{
			for(int i=0;i<len;i++)
			{
				temp+="0";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	/**
	 * 得到数据类型
	 * @param type
	 * @return
	 */
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=IParserConstant.STRVALUE;
		}else if("D".equals(type)){
			temp=IParserConstant.DATEVALUE;
		}else if("N".equals(type)){
			temp=IParserConstant.FLOAT;
		}else if("L".equals(type)){
			temp=IParserConstant.LOGIC;
		}else{
			temp=IParserConstant.STRVALUE;
		}
		return temp;
	}
	/**
	 * 取得指标的主集id
	 * @param itemid
	 * @return
	 */
	public String getFieldSetId(String itemid)
	{
		String sql = "select fieldsetid from fielditem where UPPER(itemid)=?";
		PreparedStatement pstmt = null;	
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		String fieldSet="";
		try{
			pstmt = this.conn.prepareStatement(sql);	
			pstmt.setString(1, itemid.toUpperCase());
            rs=pstmt.executeQuery();
     		while(rs.next())
     		{
     		    fieldSet = rs.getString("fieldsetid"); 
     		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(pstmt);
		}
		return fieldSet;
	}
	/**
	 * 查询业务指标所在表
	 * @param itemid
	 * @return
	 */
	private String getbusiField(String itemid)
	{
		String table = itemid.substring(0, 3);
		String fieldSetid = "";
		FieldSet fieldSet = DataDictionary.getFieldSetVo(table);
		if(fieldSet!=null) {
			FieldItem fieldItem = DataDictionary.getFieldItem(itemid, table);
			if(fieldItem!=null)
				fieldSetid = fieldItem.getFieldsetid();
		}
		
		if(StringUtils.isEmpty(fieldSetid)) {
			FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
			if(fieldItem!=null)
				fieldSetid = fieldItem.getFieldsetid();
		}
			
		return fieldSetid;
	}
	
	/**
	 * 将中文公式转换成sql
	 * @param c_expr
	 * @param varType
	 * @param uv
	 * @return
	 */
	public String getSql(String c_expr,String varType,UserView uv){
		String  temp = "";
		try{
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType.trim()),3,"usr","");
				yp.run(c_expr);
				temp = yp.getSQL();//公式的结果
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		return temp ;
	}
	 public String getSql(String selectedid,String tablename)
	  {
		  String sqlstr="";
		
		  try
		  {
			  StringBuffer s=new StringBuffer();
			  StringBuffer sql = new StringBuffer("");
			  String[] arr=selectedid.split(",");
			  String column="";
			  if("e".equalsIgnoreCase(tablename))
				  column="pre";
			  if("s".equalsIgnoreCase(tablename)|| "a".equalsIgnoreCase(tablename))
				  column="nbase";
			  for(int i=0;i<arr.length;i++)
			  {
				  if(arr[i]==null|| "".equals(arr[i]))
					  continue;
				  String[] str=arr[i].split("~");
				  sql.append(" or  (UPPER("+tablename+"."+column+")='"+str[0].substring(0, 3)+"' and "+tablename+".a0100='"+str[0].substring(3)+"')");  
			  }
			 
			  if(sql.toString().length()>0)
			  {
				  sqlstr=sql.toString().substring(4);
				  s.append("(");
				  s.append(sqlstr);
				  s.append(")");
				  sqlstr=s.toString();
			  }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return sqlstr;
	  }
	 public String getSql(String c_expr,String varType,UserView uv,ArrayList listset){
			String  temp = "";
			try{
				if(c_expr.trim().length()>0){
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
					YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType.trim()),YksjParser.forPerson,"usr","");
					yp.run(c_expr);
					temp = yp.getSQL();//公式的结果
					listset.addAll(yp.getUsedSets());
				}
			}catch(Exception ex){
				 ex.printStackTrace();
			}
			return temp ;
		}
	 
	 /**
		 * 获取邮件地址指标
		* @Title:getEmailItemId
		* @Description：
		* @author xiexd
		* @return
		 */
		public String getEmailItemId()
		{
			String emailId = "";
			RowSet rs = null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
				rs = dao.search(sql.toString());
				if(rs.next())
				{
					emailId = rs.getString("Str_Value");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(rs);
			}
			return emailId;
		}
		
		/****
		 * 查询邮箱
		* @Title:getEmailAddress
		* @Description：
		* @author xiexd
		* @param hm
		* @return
		 */
		public String getEmailAddress(HashMap hm)
		{
			String address = "";
			RowSet rs = null;
			try {
				StringBuffer sql = new StringBuffer("select "+hm.get("email").toString().trim()+" from "+hm.get("nbase").toString().trim()+"A01 where A0100='"+hm.get("a0100").toString().trim()+"'");
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql.toString());
				if(rs.next())
				{
					address = rs.getString(hm.get("email").toString())==null?"":rs.getString(hm.get("email").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(rs);
			}
			return address;
		}
		
		/**
		 * 根据职位id 获取流程和状态
		 * @param a0100  人员id
		 * @param z0301  职位id
		 * @param nbase 应聘库
		 * @return
		 */
		public ArrayList<String> getFlowParam(String a0100, String z0301, String nbase) {
			ArrayList<String> list = new ArrayList<String>();
			RowSet rs = null;
			try {
				list.add(z0301);
				list.add(a0100);
				list.add(nbase);
				StringBuffer sql = new StringBuffer("select link_id,resume_flag from zp_pos_tache ");
				sql.append( "where ZP_POS_ID=? and A0100=? and nbase=?");
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql.toString(), list);
				list.clear();
				if(rs.next()) {
					list.add(rs.getString("link_id"));
					list.add(rs.getString("resume_flag"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(rs);
			}
			return list;
		}
}
