package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BatchSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String tab_id = (String) this.getFormHM().get("tab_id");
		String task_id = (String) this.getFormHM().get("task_id");
		String infor_type = (String) this.getFormHM().get("infor_type");
		String module_id = (String) this.getFormHM().get("module_id");
		String signxml = (String) this.getFormHM().get("signxml");
		String object_id = (String) this.getFormHM().get("object_id");
		String cur_task_id = (String) this.getFormHM().get("cur_task_id");
		String cur_ins_id = (String) this.getFormHM().get("cur_ins_id");
		String markpath = (String) this.getFormHM().get("markpath");
		String DocumentID = (String) this.getFormHM().get("DocumentID");
		String signatureId = (String) this.getFormHM().get("signatureId");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
		Document doc = null;
    	RowSet rset = null;
		try {
			TemplateModuleParam moduleParam = new TemplateModuleParam(this.frameconn, this.userView);
			int signatureType = moduleParam.getSignatureType();
			XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
			cur_task_id = "0".equals(cur_task_id)?cur_task_id:PubFunc.decryption(cur_task_id);
            object_id = PubFunc.decrypt(object_id);
			doc = PubFunc.generateDom(signxml);
			List<Element> elelist = doc.getRootElement().getChildren();
			String updatesql = "";
			String sql = "";
			ArrayList paramList = new ArrayList();
			if(elelist.size()>0) {
				Element ele = elelist.get(elelist.size()-1);
				List<Element> ele2list = ele.getChildren("item");
				Element ele2=(Element)ele2list.get(0);
				String UserName = ele2.getAttributeValue("UserName");
				String SignatureID = ele2.getAttributeValue("SignatureID");
				String PageID = ele2.getAttributeValue("PageID");
				String GridNO = ele2.getAttributeValue("GridNO");
				String node_id = ele2.getAttributeValue("node_id");
				String pointx = ele2.getAttributeValue("pointx");
				String pointy = ele2.getAttributeValue("pointy");
				String width = "";
				String height = "";
				if(signatureType==3) {
					width = ele2.getAttributeValue("width");
					height = ele2.getAttributeValue("height");
				}
				sql = "select ";
				updatesql = "update ";
				String tablename = "";
				if(!"0".equals(task_id)){
					tablename = "templet_"+tab_id;
				}else {
					if("9".equals(module_id))
						tablename = "g_templet_"+tab_id;
					else
						tablename = this.userView.getUserName()+"templet_"+tab_id;
				}
				updatesql+=tablename +" set signature=? where " ;
				if("1".equals(infor_type)){
					sql+="basepre,a0100 ";  
					updatesql+="lower(basepre"+Sql_switcher.concat()+"a0100)=? ";
                }else if("2".equals(infor_type)){
                    sql+="b0110 ";
                    updatesql+="b0110=? ";
                }else if("3".equals(infor_type)){
                    sql+="e01a1 ";
                    updatesql+="e01a1=? ";
                }
				if(!"0".equals(task_id)) {
					sql+=",ins_id,task_id";
					updatesql+=" and ins_id=? ";
				}
				sql+=",signature from ";
				if("0".equals(task_id)) {
					sql+=tablename+" where submitflag=1";
				}else {
					ArrayList tasklist=null;
			        if(task_id.contains(",")){
			            tasklist=getTaskList(task_id);
			        }else{
			            tasklist=new ArrayList();
			            task_id = PubFunc.decrypt(task_id);
			            tasklist.add(task_id);
			        } 
			        sql += tablename+" where 1=1  ";
			        sql += " and exists (select null from t_wf_task_objlink where "+tablename+".seqnum=t_wf_task_objlink.seqnum and "+tablename+".ins_id=t_wf_task_objlink.ins_id  ";
                    if("1".equals(module_id)||"2".equals(module_id)||"3".equals(module_id)){//待办，我的申请，我的已办
                        sql += " and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ";
                    }
                    if(task_id.contains(",")){
                    	sql += " and task_id in (";                      
                        for(int i=0;i<tasklist.size();i++){
                            if(i!=0)
                            	sql += ",";
                            sql += tasklist.get(i);
                        }
                        sql += ")";
                    }else{
                        if(!"0".equals(task_id)){
                        	sql += " and task_id=";
                        	sql += task_id;
                        }
                    }
                    sql += " and (state is null or state<>3) )";
				}
				rset = dao.search(sql);
				while(rset.next()) {
					ArrayList list = new ArrayList();
					String signature = rset.getString("signature");
					String objectid = "";
					if("1".equals(infor_type)){
						objectid=rset.getString("basepre")+"`"+rset.getString("a0100");
                    }else if("2".equals(infor_type)){
                        objectid=rset.getString("b0110");
                    }else if("3".equals(infor_type)){
                        objectid=rset.getString("e01a1");
                    }
					String insid = "0";
					if(!"0".equals(task_id)) {
						insid = rset.getString("ins_id");
					}
					if(objectid.equalsIgnoreCase(object_id)&&((!"0".equals(cur_ins_id)&&cur_ins_id.equals(insid))||"0".equals(cur_ins_id))) {
						list.add(signxml);
				        list.add((rset.getString("basepre")+rset.getString("a0100")).toLowerCase());
				        if(!"0".equals(task_id)) {
				        	list.add(rset.getInt("ins_id"));
				        }
				        paramList.add(list);
					}else {
						if(StringUtils.isBlank(signature)) {
							StringBuffer xml = new StringBuffer();
							xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
				         	xml.append("<params>");
				         	xml.append("</params>");
				         	signature = xml.toString();
						}
						doc = PubFunc.generateDom(signature);
						List<Element> elelist_s = doc.getRootElement().getChildren();
						boolean isHave = false;
						for(int j=0;j<elelist_s.size();j++) {
							String documentid = elelist_s.get(j).getAttributeValue("DocuemntID");
							if(signatureType==2) {
								if(documentid.equals(PageID+"_"+GridNO)) {
									List<Element> itemlist = elelist_s.get(j).getChildren();
									Element item = itemlist.get(0);
									String SignatureHtmlID = item.getAttributeValue("SignatureHtmlID");
									String Page_ID = item.getAttributeValue("PageID");
									String Grid_NO = item.getAttributeValue("GridNO");
									String documentid_ = tab_id+"_"+Page_ID+"_"+Grid_NO;
									String searchsql = "select * from HTMLSignature where signatureid='"+SignatureHtmlID+"' and documentid='"+documentid_+"'";
									this.frowset = dao.search(searchsql);
									//判断是否有章，有的话就不覆盖了
									if(this.frowset.next()) {
										isHave = true;
									}
									break;
								}
							}else if(signatureType==3) {
								if(documentid.equals(DocumentID)) {
									Element item = new Element("item");
							        item.setAttribute("UserName", UserName);
							        item.setAttribute("SignatureID", signatureId);
							        item.setAttribute("PageID", PageID);
							        item.setAttribute("GridNO", GridNO);
							        item.setAttribute("node_id", node_id);
							        item.setAttribute("pointx", pointx);
							        item.setAttribute("pointy", pointy);
							        item.setAttribute("width",width);
							        item.setAttribute("height",height);
							        item.setAttribute("batch_flag","true");
							        elelist_s.get(j).addContent(item);
							        String newxml = outputter.outputString(doc);
							        list.add(newxml);
							        list.add((rset.getString("basepre")+rset.getString("a0100")).toLowerCase());
							        if(!"0".equals(task_id)) {
							        	list.add(rset.getInt("ins_id"));
							        }
							        paramList.add(list);
							        isHave = true;
									break;
								}
							}
						}
						if(!isHave) {
							//添加子节点
							Element record = new Element("record");
							if(signatureType==2)
								record.setAttribute("DocuemntID", PageID+"_"+GridNO);
							else if(signatureType==3) {
								record.setAttribute("DocuemntID", DocumentID);
							}
					        Element item = new Element("item");
					        Date dt=new Date();
				            long lg=dt.getTime();
				            Long ld=new Long(lg);
				            String SignatureHtmlID=ld.toString();
					        item.setAttribute("UserName", UserName);
					        if(signatureType==2) {
					        	item.setAttribute("SignatureID", SignatureID);
					        	item.setAttribute("SignatureHtmlID", SignatureHtmlID);
					        }else if(signatureType==3)
					        	item.setAttribute("SignatureID", signatureId);
					        item.setAttribute("PageID", PageID);
					        item.setAttribute("GridNO", GridNO);
					        item.setAttribute("node_id", node_id);
					        item.setAttribute("pointx", pointx);
					        item.setAttribute("pointy", pointy);
					        if(signatureType==2)
					        	item.setAttribute("zindex", "3");
					        if(signatureType==3) {
					        	item.setAttribute("width",width);
					        	item.setAttribute("height",height);
					        	item.setAttribute("batch_flag","true");
							}
					        record.addContent(item);
					        doc.getRootElement().addContent(record);
					        String newxml = outputter.outputString(doc);
					        if(signatureType==2)
					        	bo.saveToHtmlSignature(tab_id+"_"+PageID+"_"+GridNO,SignatureHtmlID,markpath,SignatureID);
					        list.add(newxml);
					        list.add((rset.getString("basepre")+rset.getString("a0100")).toLowerCase());
					        if(!"0".equals(task_id)) {
					        	list.add(rset.getInt("ins_id"));
					        }
					        paramList.add(list);
						}
					}
				}
				dao.batchUpdate(updatesql, paramList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	private ArrayList getTaskList(String batch_task)throws GeneralException{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			list.add(PubFunc.decrypt(temptaskid));
		}
		return list;
	}
}
