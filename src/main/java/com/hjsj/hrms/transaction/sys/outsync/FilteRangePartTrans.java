package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

/**
 * 同步接口信息配置过滤范围子集
 * 
 * @author LiWeichao 2011-07-20 11:22:46
 */
public class FilteRangePartTrans extends IBusiness {

	public void execute() throws GeneralException {
		//组装监控条件xml 格式：(like,sexpr,sfactor)
		String sexpr = (String)this.getFormHM().get("sexpr");
		sexpr = sexpr!=null && sexpr.length()>0 ? sexpr : "";
		String sfactor = (String)this.getFormHM().get("sfactor");
		sfactor = SafeCode.decode(sfactor);
		sfactor=PubFunc.keyWord_reback(sfactor);
		String like = (String)this.getFormHM().get("like");
		like = like==null || like.length()<1 ? "0" : like;
		String other_param = (String)this.getFormHM().get("other_param");
		String type = (String)this.getFormHM().get("type");
		String check = null;
		if("1".equals(type))
			check = otherParamXML("A",like+","+sexpr+","+sfactor,SafeCode.decode(other_param));
		else if("2".equals(type))
			check = otherParamXML("B",like+","+sexpr+","+sfactor,SafeCode.decode(other_param));
		else if("3".equals(type))
			check = otherParamXML("K",like+","+sexpr+","+sfactor,SafeCode.decode(other_param));
		
		this.getFormHM().put("check", SafeCode.encode(check));
	}
	
	/**
	 * 监控条件XML
	 * @param name A,B,K
	 * @param text (like,sexpr,sfactor)
	 * @param strXML 待修改xml
	 * @return strXML
	 */
	private String otherParamXML(String name,String text,String strXML){
		Document doc=null;
		
		if(text.length()<4)
			text = "";
		try {
			if(strXML==null||strXML.length()<20){
				strXML = "<?xml version='1.0' encoding=\"utf-8\"?>"+
						 "<params>"+
						 "     <control name=\"A\"/>"+
						 "     <control name=\"B\"/>"+
						 "     <control name=\"K\"/>"+
						 "</params>";
			}
			doc=DocumentHelper.parseText(strXML);
			Element root=doc.getRootElement();
			List ctrls = root.elements("control");
			if(ctrls.size()<1){
				Element A = root.addElement("control");
				A.addAttribute("name", "A");
				Element B = root.addElement("control");
				B.addAttribute("name", "B");
				Element K = root.addElement("control");
				K.addAttribute("name", "K");
				//xus 17-7-1 编辑接口配置-过滤范围 首次设置时 保存不上。
				ctrls = root.elements("control");
			}
			
			for(int i=0;i<ctrls.size();i++){
				Element element = (Element)ctrls.get(i);
				String tempName = element.attributeValue("name");
				tempName = tempName == null ? "" : tempName;
				if(name.equalsIgnoreCase(tempName)){
					element.setText(text);
					break;
				}
				
			}
			/*
			for(Iterator it=root.elementIterator();it.hasNext();){
				Element element = (Element) it.next();
				String tempName = element.attributeValue("name");
				tempName = tempName == null ? "" : tempName;
				if(name.equalsIgnoreCase(tempName))
					element.setText(text);
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//System.out.println(doc.asXML());
		return doc.asXML();
	}
}
