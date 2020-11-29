package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SavePartyFeesParamTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		MorphDynaBean partyFeesInfoList = (MorphDynaBean) this.formHM.get("partyFeesInfoList");
		String email = (String) partyFeesInfoList.get("email");
		String sms = (String) partyFeesInfoList.get("sms");
		String payFeesMessage = "";
		if(!"".equalsIgnoreCase(email))
			payFeesMessage +="," + email;
		if(!"".equalsIgnoreCase(sms))
			payFeesMessage +="," + sms;
		if(!"".equalsIgnoreCase(payFeesMessage))
			payFeesMessage = payFeesMessage.substring(1);
		RecordVo vo=ConstantParamter.getRealConstantVo("PARTY_PARAM");
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList list =new ArrayList();
		if(vo != null){
			if(vo.getString("str_value").toLowerCase()!=null 
					&&	vo.getString("str_value").toLowerCase().trim().length()>0 
					&&	vo.getString("str_value").toLowerCase().indexOf("xml")!=-1){
				Document doc=null;
				try {
					doc = PubFunc.generateDom(vo.getString("str_value"));
					Element root = doc.getRootElement(); 
					Element partyfees = root.getChild("partyfees");
					if(partyfees==null){//党费收缴参数没有配置
						partyfees = new Element("partyfees");
						partyfees.setAttribute("set",(String)partyFeesInfoList.get("set"));
						partyfees.setAttribute("computeFeesField",(String)partyFeesInfoList.get("computeFeesField"));
						partyfees.setAttribute("payFeesField",(String)partyFeesInfoList.get("payFeesField"));
						partyfees.setAttribute("payStatusField",(String)partyFeesInfoList.get("payStatusField"));
						partyfees.setAttribute("payTimeField",(String)partyFeesInfoList.get("payTimeField"));
						partyfees.setAttribute("payFeesMessage",payFeesMessage);
						root.addContent(partyfees);
					}else{
						partyfees.setAttribute("set",(String)partyFeesInfoList.get("set"));
						partyfees.setAttribute("computeFeesField",(String)partyFeesInfoList.get("computeFeesField"));
						partyfees.setAttribute("payFeesField",(String)partyFeesInfoList.get("payFeesField"));
						partyfees.setAttribute("payStatusField",(String)partyFeesInfoList.get("payStatusField"));
						partyfees.setAttribute("payTimeField",(String)partyFeesInfoList.get("payTimeField"));
						partyfees.setAttribute("payFeesMessage",payFeesMessage);
					}
					StringBuffer xmls = new StringBuffer();
				    XMLOutputter outputter = new XMLOutputter();
				    Format format=Format.getPrettyFormat();
		   	     	format.setEncoding("UTF-8");
		   	     	outputter.setFormat(format);
		   	        xmls.setLength(0);
		   	        xmls.append(outputter.outputString(doc));
		   	        
		   	        saveStrValue(xmls.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			StringBuffer sql = new StringBuffer();
			sql.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			sql.append("<param>");
			sql.append("</param>");
			list.add("PARTY_PARAM");
			String insert="insert into constant(Constant) values (?)";
			try {
				dao.insert(insert, list);
				sql.setLength(0);
				sql.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
				sql.append("<param>");
				sql.append("  <partyfees");
				sql.append("     set=\""+ (String)partyFeesInfoList.get("set") +"\"");
				sql.append("     computeFeesField=\""+ (String)partyFeesInfoList.get("computeFeesField") +"\"");
				sql.append("     payFeesField=\""+ (String)partyFeesInfoList.get("payFeesField") +"\"");
				sql.append("     payStatusField=\""+ (String)partyFeesInfoList.get("payStatusField") +"\"");
				sql.append("     payTimeField=\""+ (String)partyFeesInfoList.get("payTimeField") +"\"");
				sql.append("     payFeesMessage=\""+ payFeesMessage +"\"");
				sql.append("  />");
				sql.append("</param>");
				
				saveStrValue(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		HashMap paramMap = new HashMap();
		paramMap.put("setid", partyFeesInfoList.get("set"));
		paramMap.put("computeFeesFieldId",partyFeesInfoList.get("computeFeesField"));
		paramMap.put("payFeesFieldId",  partyFeesInfoList.get("payFeesField"));
		paramMap.put("payStatusFieldId", partyFeesInfoList.get("payStatusField"));
		paramMap.put("payTimeFieldId", partyFeesInfoList.get("payTimeField"));
		paramMap.put("payFeesMessage",  payFeesMessage);
		this.formHM.put("paramlist", paramMap);
	}
	/**
	 * 保存配置参数,更新缓存
	 * @param sql
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private void saveStrValue(String sql) throws GeneralException, SQLException{
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","PARTY_PARAM");
		vo.setString("str_value",sql);
		if(dao.updateValueObject(vo) > 0 ){
			this.formHM.put("result", true);
			ConstantParamter.putConstantVo(vo,"PARTY_PARAM");
		}else{
			this.formHM.put("result", false);
		}
	}
}
