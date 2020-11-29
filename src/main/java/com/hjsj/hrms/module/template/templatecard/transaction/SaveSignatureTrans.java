package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import sun.misc.BASE64Decoder;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 签章保存查询用
 * @author hjsoft.com.cn
 *
 */
public class SaveSignatureTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String signflag = (String)this.getFormHM().get("signflag");
		try{
			RowSet rowSet=null;
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("1".equals(signflag)){//BJCA
				String documentid = (String)this.getFormHM().get("DocumentID");
				String signatureid =(String)this.getFormHM().get("SignatureID");
				String signatureText = (String)this.getFormHM().get("signatureText");
				String signatureSize= (String)this.getFormHM().get("signatureSize");
				String flag= (String)this.getFormHM().get("flag");
				
				if("save".equals(flag)){
					rowSet=dao.search("select * from HTMLSignature where signatureid='"+signatureid+"' and documentid='"+documentid+"'");
					if(rowSet.next()){
						
					}else{
						RecordVo vo = new RecordVo("HTMLSignature");
						vo.setString("documentid", documentid);
						vo.setString("username", this.userView.getUserName());
						vo.setString("signatureid", signatureid);
						vo.setString("signaturetext", signatureText);
						vo.setString("signaturesize", signatureSize);
						dao.addValueObject(vo);
					}
					this.formHM.put("flag", "true");
				}
				if("search".equals(flag)){
					rowSet=dao.search("select * from HTMLSignature where signatureid='"+signatureid+"' and documentid='"+documentid+"'");
					if(rowSet.next()){
						this.formHM.put("signaturetext", rowSet.getString("signaturetext"));
					}
				}
			 }else if("0".equals(signflag)){//金格科技
				HashMap formMap= this.getFormHM();
				String info_type = (String)this.getFormHM().get("info_type");//
				String modeflag = (String)this.getFormHM().get("modeflag");//
				String outflag = (String)this.getFormHM().get("outflag");//
				TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
				String task_id = frontProperty.getTaskId();
				String returnFlag = frontProperty.getReturnFlag();
				 String moduleId = frontProperty.getModuleId();
				TemplateUtilBo utilBo= new TemplateUtilBo(this.frameconn,this.userView);    
				String tableName=utilBo.getTableName(frontProperty.getModuleId(),
		                Integer.parseInt(frontProperty.getTabId()), frontProperty.getTaskId());
				String filterStr =  this.userView.getHm().get("filterStr")==null?"":(String)this.userView.getHm().get("filterStr");
				ArrayList tasklist=null;
		        if(frontProperty.isBatchApprove())
		        {
		           tasklist=getTaskList(task_id);//getInsList(batch_task);
		        }
		        else
		        {
		            tasklist=new ArrayList();
		            tasklist.add(task_id);
		        }       
				StringBuffer buf=new StringBuffer();
				String documentidarr = "";
				TemplateParam  templateParam = new TemplateParam(this.frameconn,this.userView, Integer.parseInt(frontProperty.getTabId()));
				int signatureType = templateParam.getTemplateModuleParam().getSignatureType();
				if(signatureType==3) {
					String flag = (String)this.getFormHM().get("flag");//
					String documentid = (String)this.getFormHM().get("DocumentID");
					if("0".equals(flag)) {
						String solveflag = (String)this.getFormHM().get("solveflag");
						String signatureid =(String)this.getFormHM().get("signatureId");
						String signatureData = (String)this.getFormHM().get("signatureData");
						rowSet=dao.search("select * from HTMLSignature where signatureid='"+signatureid+"' and documentid='"+documentid+"'");
						if(rowSet.next()){
							if("update".equals(solveflag)) {
								RecordVo vo = new RecordVo("HTMLSignature");
								vo.setString("documentid", documentid);
								vo.setString("signatureid", signatureid);
								vo.setString("signaturetext", signatureData);
								dao.updateValueObject(vo);
							}else if("del".equals(solveflag)) {
								RecordVo vo = new RecordVo("HTMLSignature");
								vo.setString("documentid", documentid);
								vo.setString("signatureid", signatureid);
								dao.deleteValueObject(vo);
							}else if("image".equals(solveflag)) {
								String base64 =(String)this.getFormHM().get("base64");
								generateImage(base64,signatureid);
							}
						}else{
							RecordVo vo = new RecordVo("HTMLSignature");
							vo.setString("documentid", documentid);
							vo.setString("username", this.userView.getUserName());
							vo.setString("signatureid", signatureid);
							vo.setString("signaturetext", signatureData);
							dao.addValueObject(vo);
						}
					}else if("1".equals(flag)) {
						String signatureid =(String)this.getFormHM().get("signatureId");
						rowSet=dao.search("select signaturetext,signatureid from HTMLSignature where signatureid='"+signatureid+"' and documentid='"+documentid+"'");
						String signaturetext = "";
						if(rowSet.next()){
							signaturetext = rowSet.getString("signaturetext");
							signatureid = rowSet.getString("signatureid");
						}
						this.formHM.put("signaturetext", signaturetext);
						this.formHM.put("signatureid", signatureid);
					}
				}
				else {
					if("0".equals(modeflag)){
						if("2".equals(outflag)){//全部人员
			                if(!"0".equals(task_id)){
			                    buf.append("select * from ");
			                    buf.append(tableName);    
			                    buf.append(" where 1=1 ");
			                
			                    buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum  and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
			                    if("1".equals(returnFlag)||"2".equals(returnFlag)||"3".equals(returnFlag)){//´ý°ì£¬ÎÒµÄÉêÇë£¬ÎÒµÄÒÑ°ì
			                        buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
			                    }
			                    
			                    if(frontProperty.isBatchApprove()){
			                        buf.append(" and   task_id in (");
			                        for(int i=0;i<tasklist.size();i++){
			                            if(i!=0)
			                                buf.append(",");
			                            buf.append(tasklist.get(i));
			                        }
			                        buf.append(")");
			                    }else{
			                        if(!"0".equals(task_id)){
			                            buf.append(" and  task_id=");
			                            buf.append(task_id);
			                        }
			                    }
			                        buf.append(" and (state is null or state<>3) ) ");
			                }else{
			                    buf.append("select *  from ");
			                    buf.append(tableName);
			                    buf.append(" where 1=1 ");
			                }
			                
			                if(filterStr.trim().length()>0){
			                    buf.append(" and "+filterStr);
			                }
							/*buf.append("select signature from ");
							buf.append(tableName);*/    
						}
						else if ("3".equals(outflag)){//部分人员
							if(!"0".equals(task_id)){
			                    buf.append("select * from ");
			                    buf.append(tableName);
			                    buf.append(" where 1=1  ");
			                    buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
			                    if("1".equals(moduleId)||"2".equals(moduleId)||"3".equals(moduleId)){//´ý°ì£¬ÎÒµÄÉêÇë£¬ÎÒµÄÒÑ°ì
			                        buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
			                    }
			                    if(frontProperty.isBatchApprove()){
			                        buf.append(" and submitflag=1 and  task_id in (");                      
			                        for(int i=0;i<tasklist.size();i++){
			                            if(i!=0)
			                                buf.append(",");
			                            buf.append(tasklist.get(i));
			                        }
			                        buf.append(")");
			                    }else{
			                        if(!"0".equals(task_id)){
			                            buf.append(" and submitflag=1 and task_id=");
			                            buf.append(task_id);
			                            //buf.append(")");
			                        }
			                    }
			                    buf.append(" and (state is null or state<>3) )");
			                }else{
			                    buf.append("select *  from ");  
			                    buf.append(tableName);    
			                    buf.append(" where submitflag=1");                  
			                }
							/*buf.append("select signature from ");
							buf.append(tableName); 
							buf.append(" where submitflag=1"); */
						}
						else{
							String a0100="";
							String basepre="";
							String object_id =(String)this.getFormHM().get("object_id");
				            object_id = PubFunc.decrypt(object_id);
				            buf.append("select signature from ");
							 if("2".equals(info_type)){
								 buf.append(tableName);   
								 buf.append(" where b0110='"+object_id+"'");   
				             }else if("3".equals(info_type)){
								 buf.append(tableName);   
								 buf.append(" where e01a1='"+object_id+"'");
				             }else{
				                 int i = object_id.indexOf("`");
				                 if (i>0){
				                     basepre=object_id.substring(0,i);
				                     a0100=object_id.substring(i+1);
				                 }
								 buf.append(tableName);   
								 buf.append(" where basepre='"+basepre+"'");
								 buf.append(" and a0100='"+a0100+"'");
				             }
						}
					}else if("1".equals(modeflag)){//打印
						buf.append("select * from ");
			            buf.append(tableName);
			            if (!"0".equals(task_id)) {
			                buf.append(" where 1=1 ");
			                buf.append(" and exists (select null from t_wf_task_objlink where " + tableName + ".seqnum=t_wf_task_objlink.seqnum and " + tableName + ".ins_id=t_wf_task_objlink.ins_id  ");
			                if ("1".equals(returnFlag) || "2".equals(returnFlag) || "3".equals(returnFlag)) {
			                    buf.append(" and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
			                }
			                buf.append(" and  submitflag=1 and  task_id in (");
			                for (int i = 0; i < tasklist.size(); i++) {
			                    if (i != 0)
			                        buf.append(",");
			                    buf.append(tasklist.get(i));
			                }
			                buf.append(")");
	
			                buf.append(" and (state is null or state<>3) ) ");
			            } else {
			                if (frontProperty.isSelfApply()){
			                    buf.append(" where ");
			                    buf.append(" basepre='");
			                    buf.append(this.userView.getDbname());
			                    buf.append("' and a0100='");
			                    buf.append(this.userView.getA0100());
			                    buf.append("'");
			                }
			                else {
			                    buf.append(" where submitflag=1");
			                    
			                }
			            }
					}
					rowSet=dao.search(buf.toString());
					while(rowSet.next()){
						String documentid = "";
						String signature = rowSet.getString("signature");
						if(signature!=null&&!"".equals(signature))
							documentid = analysisSignatureXml(signature);
						documentidarr+=documentid;
					}
					if(!"".equals(documentidarr))
						documentidarr = documentidarr.substring(0, documentidarr.length()-1);
					this.formHM.put("documentidarr", documentidarr);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			this.formHM.put("flag", "false");
			//throw new GeneralException("签章出错，请退出当前表单重新执行!");
		}
	}

	private String analysisSignatureXml(String signature) {
		Document doc = null;
		String documentidarr = "";
		try {
			doc = PubFunc.generateDom(signature);
			List<Element> elelist = doc.getRootElement().getChildren();
			for(int j = 0; j < elelist.size(); j++){
				Element ele = elelist.get(j);
				String documentid = ele.getAttributeValue("DocuemntID");
				if(!"BJCA".equals(documentid)){
					List<Element> ele2list = ele.getChildren("item");
					if(ele2list!=null&&ele2list.size()>0){
						for(int k=0;k<ele2list.size();k++){
							Element ele2=(Element)ele2list.get(k);
							String pageid = ele2.getAttributeValue("PageID");
							String signatureid = ele2.getAttributeValue("SignatureID");
							documentidarr+=documentid+"_"+signatureid+"_"+pageid+",";
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return documentidarr;
	}
	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			list.add(lists[i]);
		}
		return list;
	}
	private boolean generateImage(String imgData, String signatureid) throws GeneralException, IOException{
        if (imgData == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out = null;
        String imgFilePath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+signatureid+".gif";
        try {
            out = new FileOutputStream(imgFilePath);
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(out);
        }
        return true;
    }
}
