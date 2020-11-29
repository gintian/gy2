package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 刷新附件内容
 * @author liuyz
 *
 */
public class RefrefAttachmentTrans  extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		
		try {
			String ins_id = (String) this.getFormHM().get("ins_id");
			String tabid = (String) this.getFormHM().get("tabid");
			String infor_type = (String) hm.get("infor_type");
			String object_id = (String)hm.get("object_id")==null?"":(String)hm.get("object_id");
			object_id = PubFunc.decrypt(SafeCode.decode(object_id));
			String attachmenttype = (String)hm.get("attachmenttype");//附件类型 =0公共附件 =1 个人附件
			String module_id=(String)hm.get("module_id");
			String username=this.userView.getUserName();
			String rwPriv = (String)hm.get("rwPriv");//权限，2可编辑，1是只读
			String uniqueId = (String)hm.get("uniqueId");//记录标签的唯一性属性。fld_pageid_gridno
			int peopleCount = 1;//左侧人员列表中人员的个数.一定有人。如果没有人，程序在上面就return了。
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=null;
			String[] object_id_Arr = object_id.split("`");
			String basepre = null;
			String objectid = null; //a0100|b0110|e01a1
			if("1".equals(infor_type)){
				basepre = object_id_Arr[0];
				objectid = object_id_Arr[1]; //a0100|b0110|e01a1
			}else
				objectid = object_id;
			String[] arra0100s={objectid};
			ArrayList insIdsList=new ArrayList();
			insIdsList.add(ins_id);
			TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tabid));
			TemplateParam paramBo = new TemplateParam(this.getFrameconn(),this.userView, Integer.parseInt(tabid));
			boolean isSavaAttachToMinSet = false;
			String archive_attach_to = paramBo.getArchive_attach_to();	
			Boolean attach_history=paramBo.isAttach_history();
			FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
			if(paramBo.isArchiveAttachToMainSet() && "1".equals(a01Set.getMultimedia_file_flag())){
				isSavaAttachToMinSet = true;
			}
			templateBo.sysPersonAttachmentToTemplate(arra0100s, basepre, archive_attach_to.toUpperCase(),insIdsList,isSavaAttachToMinSet,attach_history);
			//同步完档案库数据后查询结果返回前台显示。
			if(object_id==null || "".equals(object_id)){//左侧人员列表无人员
				return;
			}
			//如果是通过自助申请，且是业务用户关联自助用户，就把上传附件人的帐号填入自助的帐号，否则用微信打开看不到。
			if("9".equalsIgnoreCase(module_id)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getDbname())&&StringUtils.isNotBlank(this.userView.getA0100())){
				DbNameBo db = new DbNameBo(this.frameconn);
				String loginNameField = db.getLogonUserNameField();
				String usernameSele="";
				if(StringUtils.isNotBlank(loginNameField)) {
					loginNameField = loginNameField.toLowerCase();
					String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
					rowSet=dao.search(sql);
					while(rowSet.next()){
						usernameSele=rowSet.getString("username");
					}
					if(StringUtils.isNotBlank(usernameSele)){
						username=usernameSele;
					}
				}
			}
			ArrayList list = new ArrayList();
			StringBuffer sb = new StringBuffer("");
			if(peopleCount>0){//左侧列表有人
				if(ins_id!=null&&!"0".equals(ins_id)){//进入了审批流
					/**进入了审批流,ins_id决定了是能否查看的文件,如果ins_id正确那么就能查看到相应ins_id流程中的文件**/
					/** 屏蔽掉
				    HashMap cardAttachMap = (HashMap) this.userView.getHm().get("cardAttachMap");
					if(cardAttachMap!=null&&!cardAttachMap.containsKey(ins_id)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.no.permission.ins_id"));
					}
					*/ 
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) and (state=0 or state is null) ");//
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.attachmenttype=1");
						sb.append(" and t.objectid='");
						sb.append(objectid);
						sb.append("' and (state=0 or state is null) ");//如果state=0标识未删除，state是null可能是以前引入的
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						if(!this.userView.isAdmin()){//如果不是超级管理员，根据用户多媒体权限显示附件
							String[] fileTypes = this.userView.getMediapriv().toString().split(",");
							String fileTypeStr="";
							for(String type:fileTypes){
								fileTypeStr+="'"+type+"',";
							}
							if(StringUtils.isNotBlank(fileTypeStr)){
								fileTypeStr=fileTypeStr.substring(0,fileTypeStr.length()-1);
								sb.append(" and fileType in (select id from MediaSort where flag in ("+fileTypeStr+") and dbflag='"+paramBo.getInfor_type()+"')");
							}
						}
						//从uniqueId中解析pageid和gridno,查询附件的设置，是否设置了分类。
						String[] split = uniqueId.split("_");
						if(split.length==3){
							String pageid=split[1];
							String gridno=split[2];
							String sql="select sub_domain from template_set where tabid=? and pageid=? and gridno=?";
							ArrayList paramList=new ArrayList();
							paramList.add(tabid);
							paramList.add(pageid);
							paramList.add(gridno);
							this.frowset = dao.search(sql,paramList);
							if(this.frowset.next()){
								String sub_domain=this.frowset.getString("sub_domain");
								if(StringUtils.isNotBlank(sub_domain)){
									Document doc=null;
									Element element=null;
									doc=PubFunc.generateDom(sub_domain);;
									String xpath="/sub_para/para";
									XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									List childlist=findPath.selectNodes(doc);	
									if(childlist!=null&&childlist.size()>0)
									{
										element=(Element) childlist.get(0);
										String file_type=(String)element.getAttributeValue("file_type");
										if(StringUtils.isNotBlank(file_type)){
											sb.append(" and m.flag='"+file_type+"'");
										}
									}
								}
							}
						}
					}
				}else{//还未进入审批流
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
						sb.append(" and t.create_user='");
						sb.append(username);
						sb.append("'  and (state=0 or state is null) ");
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.attachmenttype=1");
						sb.append(" and ( t.create_user='");
						sb.append(username);
						sb.append("' ) and t.objectid='");
						sb.append(objectid);
						sb.append("' and (state=0 or state is null) ");
						
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						if(!this.userView.isAdmin()){
							String[] fileTypes = this.userView.getMediapriv().toString().split(",");
							String fileTypeStr="";
							for(String type:fileTypes){
								fileTypeStr+="'"+type+"',";
							}
							if(StringUtils.isNotBlank(fileTypeStr)){
								fileTypeStr=fileTypeStr.substring(0,fileTypeStr.length()-1);
								sb.append(" and fileType in (select id from MediaSort where flag in ("+fileTypeStr+") and dbflag='"+paramBo.getInfor_type()+"')");
							}
						}
						String[] split = uniqueId.split("_");
						if(split.length==3){
							String pageid=split[1];
							String gridno=split[2];
							String sql="select sub_domain from template_set where tabid=? and pageid=? and gridno=?";
							ArrayList paramList=new ArrayList();
							paramList.add(tabid);
							paramList.add(pageid);
							paramList.add(gridno);
							this.frowset = dao.search(sql,paramList);
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
											sb.append(" and m.flag='"+file_type+"'");
										}
									}
								}
							}
						}
					}
				}
			}
			if(sb.length()>0){
				sb.append(" order by file_id");
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					LazyDynaBean bean = new LazyDynaBean();
					//**安全平台改造,将file_id进行加密处理**//*
					bean.set("file_id", SafeCode.encode(PubFunc.encrypt(frowset.getString("file_id"))));
					bean.set("attachmentname", frowset.getString("name"));
					bean.set("sortname", frowset.getString("sortname"));
					bean.set("ext", frowset.getString("ext"));
					bean.set("ins_id", frowset.getString("ins_id"));
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					bean.set("create_time", d_str);
					String name = frowset.getString("fullname");
					String user_name = frowset.getString("create_user");//下载不要
					if(StringUtils.isBlank(name))
						name = user_name;
					bean.set("fullname", name);
					if("2".equals(rwPriv)){//可以编辑
						if(username!=null&&username.equals(frowset.getString("create_user"))){
							bean.set("candelete", "1");
						}else{
							bean.set("candelete", "0");
						}
					}
					list.add(bean);
				} //while loop end
			}
		
			this.getFormHM().put("attachmentList", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
