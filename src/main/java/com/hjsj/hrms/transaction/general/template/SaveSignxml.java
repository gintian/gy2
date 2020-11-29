/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:2011.06.13
 * @author xieguiquan
 * @version 4.0
 */
public class SaveSignxml extends IBusiness {

	public void execute() throws GeneralException {
		String table_name = (String)this.getFormHM().get("table_name");
		String infor_type =(String)this.getFormHM().get("infor_type");
		String signxml = (String)this.getFormHM().get("signxml");
		String  ins_id= (String)this.getFormHM().get("ins_id");
		String  flag= (String)this.getFormHM().get("flag");
		TemplateModuleParam templateModuleParam = new TemplateModuleParam(this.frameconn, this.userView);
		int signatureType = templateModuleParam.getSignatureType();
		if(signatureType!=0) {//不等于金格的不执行下面代码
			return;
		}
		signxml = SafeCode.decode(signxml);
		signxml = PubFunc.keyWord_reback(signxml);
		RowSet rowSet=null;
		//signxml = signxml.replace("'", "\"");
		Document doc=null;
		Document doc2=null;
		Element element=null;
			try {
				ContentDAO dao = new ContentDAO(this.frameconn);
				doc=PubFunc.generateDom(signxml);;
			 Element root = doc.getRootElement();
		        List childlist = root.getChildren("record");
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					String id="";
					String DocuemntID="";
					id= element.getAttributeValue("id");
					DocuemntID= element.getAttributeValue("DocuemntID");
					StringBuffer strxml = new StringBuffer();
					strxml.append("<?xml version='1.0' encoding='GB2312'?>");
					strxml.append("<params>");
					strxml.append("</params>");
					doc2 =PubFunc.generateDom(strxml.toString());;
					Element hr=doc2.getRootElement();
				  
					Element aelement=(Element)element.clone(); 
					List achildlist=aelement.getChildren("item"); 
					if(achildlist!=null)
					{
						for(int n=0;n<achildlist.size();n++){
							Element element2=(Element)achildlist.get(n);
							String signatureID=element2.getAttributeValue("SignatureID");
							rowSet=dao.search("select * from HTMLSignature where signatureid='"+signatureID+"' and documentid='"+DocuemntID+"'");
							if(rowSet.next())
							{
								 
								
							}
							else
								achildlist.remove(n);
							 
						
						}
						 
					}  
					hr.addContent(aelement);
				//	hr.addContent((Element)element.clone());
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					String updatexml =outputter.outputString(doc2);
					RecordVo vo = new RecordVo(table_name);
					//人事异动sutemplet_1主键为A0100，BasePre，单位管理主键：B0110，岗位管理主键：E01a1
					//流程中：templet_1主键：A0100，BasePre，ins_id单位管理主键：B0110，ins_id岗位管理主键：E01a1，ins_id
					if(table_name.startsWith("g_templet")){
						String basePre = id.substring(0,id.indexOf("|"));
						String a0100 =  id.substring(id.indexOf("|")+1,id.length());
						vo.setString("a0100", a0100);
						vo.setString("basepre", basePre);
					}else{
					if(table_name.startsWith("templet")){
				//		StringBuffer where = new StringBuffer();
				//		where.append(" where ");
						String basePre = id.substring(0,id.indexOf("|"));
						String a0100 =  id.substring(id.indexOf("|")+1,id.length());
						if(infor_type!=null&& "1".equals(infor_type)){
						vo.setString("a0100", a0100);
						vo.setString("basepre", basePre);
						
				//		where.append(" a0100='"+a0100+"'");
				//		where.append(" and lower(basepre)='"+basePre.toLowerCase()+"'");
						}else if(infor_type!=null&& "2".equals(infor_type)){
							vo.setString("b0110", a0100);
			//				where.append(" b0110='"+a0100+"'");
						}else if(infor_type!=null&& "3".equals(infor_type)){
							vo.setString("e01a1", a0100);
			//				where.append(" e01a1='"+a0100+"'");
						}
			//			this.frowset = dao.search(" select * from "+table_name+" "+where);
			//			if(this.frowset.next())
			//				ins_id = this.frowset.getString("ins_id");
						vo.setString("ins_id", ins_id);
					}else{
						String basePre = id.substring(0,id.indexOf("|"));
						String a0100 =  id.substring(id.indexOf("|")+1,id.length());
						if(infor_type!=null&& "1".equals(infor_type)){
						vo.setString("a0100", a0100);
						vo.setString("basepre", basePre);
						}else if(infor_type!=null&& "2".equals(infor_type)){
							vo.setString("b0110", a0100);
						}else if(infor_type!=null&& "3".equals(infor_type)){
							vo.setString("e01a1", a0100);
						}
					}
					}
	//				vo = dao.findByPrimaryKey(vo);
	//				vo.removeValue("photo");
	//				vo.removeValue("ext");
	//				vo.removeValue("submitflag");
					
					vo.setString("signature", updatexml);
					dao.updateValueObject(vo);
				}
			}
			this.getFormHM().put("flag", flag);
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

}
