package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.TwfdefineBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.HashMap;
/**
 * 保存
 * @author sunx
 *
 */
public class SaveFixTableTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RecordVo t_wf_defineVo =new RecordVo("t_wf_define");
		HashMap hm=this.getFormHM();		
 		ContentDAO dao=new ContentDAO(this.getFrameconn());
		t_wf_defineVo=(RecordVo) hm.get("t_wf_defineVo");
		String validateflag=(String) hm.get("validateflag");
		String[] inputname=(String[])hm.get("inputname");
		String[] inputparam=(String[])hm.get("inputparam");
		String[] appname=(String[])hm.get("appname");
		String[] appparam=(String[])hm.get("appparam");			
		if("on".equals(validateflag)){
			validateflag="1";
		}else{
			validateflag="0";
		}
		t_wf_defineVo.setString("flag",validateflag);
		String inputurl=(String) hm.get("inputurl");
		String appurl=(String) hm.get("appurl");
		String ctrl_para="";
		try {
			ctrl_para=this.getctrl_para(inputurl,appurl);
			TwfdefineBo twf=new TwfdefineBo();
			ctrl_para=twf.updatectrl_Formpara(ctrl_para,inputname,inputparam,appname,appparam);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		t_wf_defineVo.setString("ctrl_para",ctrl_para);
		dao.addValueObject(t_wf_defineVo);		
	}
	public String getctrl_para(String inputurl,String appurl) throws JDOMException{
		Document doc=this.createxmlctrl_para();
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element edit_form=element.getChild("edit_form");
			edit_form.getAttribute("url").setValue(inputurl);
			Element appeal_form=element.getChild("appeal_form");
			appeal_form.getAttribute("url").setValue(appurl);
		
		}
		return outputter.outputString(doc);
	}
    private Document createxmlctrl_para(){
		
		Element params=new Element("params");
		Element notes =new Element("notes");
		notes.setAttribute("email","false");
		notes.setAttribute("sms","false");
		Element sp_flag=new Element("sp_flag");
		sp_flag.setAttribute("mode","0");
		Element edit_form=new Element("edit_form");
		edit_form.setAttribute("url","");
		Element appeal_form=new Element("appeal_form");
		appeal_form.setAttribute("url","");
		params.addContent(notes);
		params.addContent(sp_flag);
		params.addContent(edit_form);
		params.addContent(appeal_form);
		Document doc =new Document(params);
		return doc;
	}
}
