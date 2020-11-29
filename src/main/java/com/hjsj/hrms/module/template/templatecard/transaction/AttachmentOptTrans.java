package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * 项目名称 ：ehr
 * 类名称：AttachmentOptTrans
 * 类描述：删除、下载、显示上传附件
 * 创建人： lis
 * 创建时间：2016-5-25
 */
public class AttachmentOptTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try {
			String type=(String)this.getFormHM().get("type");
			if(StringUtils.isBlank(type))
				type = "del";
			if("del".equals(type)){//删除文件
				String file_ids =(String)this.getFormHM().get("file_ids");//删除返回的是用”,“分割的字符串
				String module_id =(String)this.getFormHM().get("module_id");
				if(file_ids==null||file_ids.trim().length()==0){
					this.getFormHM().put("ok", "0");
					return;
				}
				
				/*ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM");
				String rootdir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
                rootdir=rootdir.replace("\\",File.separator);
                if (!rootdir.endsWith(File.separator)) rootdir =rootdir+File.separator;
                rootdir += "multimedia"+File.separator;*/
				StringBuffer fileIds = new StringBuffer();
				for(String file_id:file_ids.split(",")){
					/**基于安全平台改造,将加密的文件Id解密回来**/
					if(file_id!=null&&file_id.trim().length()>0){
						file_id = PubFunc.decrypt(SafeCode.decode(file_id));
						fileIds.append(",");
						fileIds.append(file_id);
					}
				}
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String username=this.userView.getUserName();
				if("9".equalsIgnoreCase(module_id)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getDbname())&&StringUtils.isNotBlank(this.userView.getA0100())){
					DbNameBo db = new DbNameBo(this.getFrameconn());
					String loginNameField = db.getLogonUserNameField();
					String usernameSele="";
					if(StringUtils.isNotBlank(loginNameField)) {
						loginNameField = loginNameField.toLowerCase();
						String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
						this.frowset=dao.search(sql);
						while(this.frowset.next()){
							usernameSele=this.frowset.getString("username");
						}
						if(StringUtils.isNotBlank(usernameSele)){
							username=usernameSele;
						}
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append("select filepath from t_wf_file where create_user='");
				sb.append(username);
				sb.append("' and file_id in(-1,");
				sb.append(fileIds.toString().substring(1));
				sb.append(")");
				this.frowset = dao.search(sb.toString());
				while(this.frowset.next()){
					String filePath = this.frowset.getString("filepath");
					if(StringUtils.isNotBlank(filePath)){
						//区分文件来源 人事异动上传的文件真删除 否则假删除
						VfsFileEntity enty = VfsService.getFileEntity(filePath);
						if(enty.getModuleid().equals(VfsModulesEnum.RS.toString())) {
							//文件从人事异动表单提交归档的附件 filetag 标记为YG的认为是已归档的附件 不删除
							if(StringUtils.isEmpty(enty.getFiletag())||!enty.getFiletag().equals(VfsModulesEnum.YG.toString())) {
								VfsService.deleteFile(this.userView.getUserName(), filePath);
							}
						}
					}
				}
				sb.setLength(0);
				sb.append("update  t_wf_file set state=1  where create_user='");//将真删除改为假删除，打上state=1标识删除
				sb.append(username);
				sb.append("' and file_id in(-1,");
				sb.append(fileIds.toString().substring(1));
				sb.append(")");
				dao.update(sb.toString());
				this.getFormHM().put("ok", "1");
			}else if("download".equals(type)){//下载文件
				InputStream in = null;
				String tabid = (String)this.getFormHM().get("tabid");
				String file_id =(String)this.getFormHM().get("file_id");//删除返回的是用”,“分割的字符串
				String isIE =(String)this.getFormHM().get("isIE");//删除返回的是用”,“分割的字符串
				/**基于安全平台改造,将加密的文件Id解密回来**/
				if(file_id!=null&&file_id.trim().length()>0){
					file_id = PubFunc.decrypt(SafeCode.decode(file_id));
				}
				if(file_id==null||file_id.trim().length()==0){
					this.getFormHM().put("ok", "0");
					return;
				}
				AttachmentBo attachmentBo = new AttachmentBo(userView, frameconn, tabid);
				HashMap fileMap =attachmentBo.downloadFile(file_id);
				String filePath = (String)fileMap.get("filepath");
				in = (InputStream)fileMap.get("ole");
				String srcfilename =attachmentBo.getDestFileName();
				String ext = attachmentBo.getExt().toLowerCase();
				//改造为vfs下载文件
				if(StringUtils.isNotBlank(filePath)) {
					//兼容数据库存储路径的情况
					if(filePath.indexOf("\\")>-1) {
						if(in!=null) {
							this.saveInputStreamToFileByVfs(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
						}else {
							if(!filePath.startsWith("\\")) {
								filePath="\\"+filePath;
							}
							this.getFormHM().put("filePath", PubFunc.encrypt(filePath));
							this.getFormHM().put("ext", ext);
							this.getFormHM().put("displayfilename", srcfilename);
							//this.downSuccess(filePath,srcfilename,ext,isIE,file);
						}
					}else {
						this.getFormHM().put("filePath", filePath);
						this.getFormHM().put("ext", ext);
						this.getFormHM().put("displayfilename", srcfilename);
					}
				}else if(in!=null){
					this.saveInputStreamToFileByVfs(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
				}else{
					throw new GeneralException("未找到文件！");//"未找到文件！"
				}
				/*if(StringUtils.isNotBlank(filePath)){
					File file = new File(filePath);
					if (!file.exists()) {
						if(in!=null)
							this.saveInputStreamToFile(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
						else
							throw new GeneralException("未找到文件" + "(" + srcfilename + ")！");
					}else
						this.downSuccess(filePath,srcfilename,ext,isIE,file);
				}else if(in!=null){
					this.saveInputStreamToFile(filePath,attachmentBo,file_id,srcfilename,ext,isIE,in);
				}else{
					throw new GeneralException("未找到文件！");//"未找到文件！"
				}*/
			}else if("upload".equals(type)){//上传文件
				this.upLoad();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
	}
	/**
	 * 二进制文件流转存 调用vfs转存
	 * @param filePath
	 * @param attachmentBo
	 * @param file_id
	 * @param srcfilename
	 * @param ext
	 * @param isIE
	 * @param in
	 * @throws GeneralException
	 */
	private void saveInputStreamToFileByVfs(String filePath, AttachmentBo attachmentBo, String file_id, String srcfilename, String ext, String isIE, InputStream in) throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String fileid=VfsService.addFile(this.userView.getUserName(), VfsFiletypeEnum.multimedia,VfsModulesEnum.RS, VfsCategoryEnum.other,"", in, srcfilename, "", true);
			//将filepath存储到对应记录
			StringBuffer sbin = new StringBuffer();
			sbin.append("update t_wf_file set filepath='"+fileid+"' where file_id ="+file_id);
			dao.update(sbin.toString());
			this.getFormHM().put("filePath", fileid);
			this.getFormHM().put("ext", ext);
			this.getFormHM().put("displayfilename", srcfilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将二进制转换成路径存储并保存文件
	 * @param filePath 
	 * @param attachmentBo
	 * @param file_id
	 * @param srcfilename
	 * @param ext
	 * @param isIE
	 * @param in 
	 * @throws GeneralException 
	 */
	private void saveInputStreamToFile(String filePath, AttachmentBo attachmentBo, String file_id, String srcfilename, String ext, String isIE, InputStream in) throws GeneralException {
		OutputStream output = null;
		try{
			//保存到指定目录(路径)按照子集附件保存路径存储
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		    attachmentBo.initParam(true);
		    String middlepath = "";
			if("\\".equals(File.separator)){//证明是windows
				middlepath = "subdomain\\template_";
			}else if("/".equals(File.separator)){//证明是linux
				middlepath = "subdomain/template_";
			}
			UUID uuid = UUID.randomUUID();
		    String fileuuidname = uuid.toString();
		    filePath = attachmentBo.getAbsoluteDir(fileuuidname,middlepath)+File.separator+fileuuidname + ext;
		    String rootDir = attachmentBo.getRootDir();
		    String absolutFilePath=attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
		    if(!absolutFilePath.startsWith(rootDir)){
		    	absolutFilePath=rootDir+File.separator+attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
		    }
		    // 保存文件
			File file = new File(absolutFilePath, fileuuidname + ext);
			output = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int read = 0;
			while ((read = in.read(bt)) != -1) {
				output.write(bt, 0, read);
			}
			//将filepath存储到对应记录
			StringBuffer sbin = new StringBuffer();
			sbin.append("update t_wf_file set filepath='"+filePath+"' where file_id ="+file_id);
			dao.update(sbin.toString());
			this.downSuccess(filePath,srcfilename,ext,isIE,file);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(output);
		}
	}
	/**
	 * 下载文件回传信息
	 * @param filePath
	 * @param srcfilename
	 * @param ext
	 * @param isIE
	 * @param file
	 * @throws GeneralException
	 */
	private void downSuccess(String filePath, String srcfilename, String ext, String isIE, File file) throws GeneralException{
		try{
	    	String docExt = ",.docx,.doc,.dot,.xlsx,.xls,.pptx,.ppt,";
	    	if("true".equals(isIE) && docExt.indexOf(","+ext+",") > -1){
	    		srcfilename = PubFunc.encryption(file.getName());
	    	}else{
	    		srcfilename = SafeCode.encode(srcfilename);
	    	}
			//filePath = PubFunc.encryption(filePath);
			this.getFormHM().put("filePath", PubFunc.encrypt(filePath));
			this.getFormHM().put("ext", ext);
			this.getFormHM().put("displayfilename", srcfilename);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @author lis
	 * @Description: 显示文件上传内容
	 * @date Aug 20, 2016
	 * @throws GeneralException
	 */
	private void upLoad() throws GeneralException{
		try {
			HashMap hm = (HashMap)this.getFormHM();
			String tabid = (String)hm.get("tabid");//不可能为空
			TemplateParam param = new TemplateParam(this.getFrameconn(),this.userView,Integer.valueOf(tabid));
			String infor_type = param.getInfor_type() + "";//1是人员，2是单位，3是岗位
			ArrayList mediasortList = new ArrayList();//多媒体目录
			ArrayList mediasortList2 = new ArrayList();//多媒体目录
			String attachmenttype=(String) hm.get("attachmenttype");//附件的类型 0:公共附件 1:个人附件
			String uniqueId=(String) hm.get("uniqueId");//记录标签的唯一性属性。fld_pageid_gridno
			String fillInfo=(String) this.userView.getHm().get("fillInfo");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList paramList=new ArrayList();
			Boolean isOnlyOneType=false;
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from mediasort where dbflag=?");
			paramList.add(infor_type);
			String[] split = uniqueId.split("_");
			if(split.length==3){
				String pageid=split[1];
				String gridno=split[2];
				String sql="select sub_domain from template_set where tabid=? and pageid=? and gridno=?";
				ArrayList list=new ArrayList();
				list.add(tabid);
				list.add(pageid);
				list.add(gridno);
				this.frowset = dao.search(sql,list);
				if(this.frowset.next()){
					String sub_domain=this.frowset.getString("sub_domain");
					if(StringUtils.isNotBlank(sub_domain)){
						Document doc=null;
						Element element=null;
						doc=PubFunc.generateDom(sub_domain);
						String xpath="/sub_para/para";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);	
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element) childlist.get(0);
							String file_type=(String)element.getAttributeValue("file_type");
							if(StringUtils.isNotBlank(file_type)){
								sb.append(" and flag=?");
								paramList.add(file_type);
								isOnlyOneType=true;
							}
						}
					}
				}
			}
			this.frowset = dao.search(sb.toString(),paramList);
			while(this.frowset.next()){
			    String flag = this.frowset.getString("flag");
			    String id = String.valueOf(this.frowset.getInt("id"));
			    String sortname = this.frowset.getString("sortname");
			    CommonData data=new CommonData(id,sortname);
			    mediasortList2.add(data);
			    if (!this.userView.isSuper_admin()){//判断多媒体权限
			    	if(!"1".equals(fillInfo)){
			    		if (!this.userView.hasTheMediaSet(flag)) continue;
			    	}
			    }
				mediasortList.add(data);
			}
			if(attachmenttype!=null&& "0".equals(attachmenttype)){//如果是公共附件不用判断权限
			}else{//如果是个人附件
				if(mediasortList.size() < 1)
					throw GeneralExceptionHandler.Handle(new GeneralException("没有设置多媒体文件类型权限或者没有设置多媒体分类，不能进行上传操作！"));//bug 50581 没有多媒体分类权限或者没有多媒体分类。
		         if (mediasortList2.size()<1){
		              if("1".equals(infor_type)){
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.cannotUploadAttach")));  
		              }else if("2".equals(infor_type)){
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.havenotCreateEmpMediaType")));
		              }else{
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.havenotCreatePositionMediaType")));
		              }
		         }
			}
			this.getFormHM().put("mediasortList", mediasortList);
			this.getFormHM().put("isOnlyOneType", isOnlyOneType);//是否是选择了附件分类。true前台不显示选择分类下拉。
			if(mediasortList.size()==0){
				this.getFormHM().put("mediasortid", "-9999");
			}else{
				CommonData obj = (CommonData)mediasortList.get(0);
				this.getFormHM().put("mediasortid", obj.getDataValue());//默认显示第一个
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
