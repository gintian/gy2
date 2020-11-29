package com.hjsj.hrms.transaction.kq.options.imports;

import com.hjsj.hrms.businessobject.kq.options.imports.SearchImportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:保存导入考勤规则指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 28, 2010:11:41:39 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SearchImportTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String subset=(String)hm.get("subset");  //子集
		String field=(String)hm.get("field");  //指标
		String begindate=(String)hm.get("begindate");  //开始时间
		String enddate=(String)hm.get("enddate");  //结束时间
		String akq_item1=(String)hm.get("akq_item1"); //id
		
		try{
		    SearchImportBo searchImportbo = new SearchImportBo(this.getFrameconn(),akq_item1);
		    searchImportbo.setValue("subset",subset);
		    searchImportbo.setValue("field",field);
		    searchImportbo.setValue("begindate",begindate);
		    searchImportbo.setValue("enddate",enddate);
		    searchImportbo.saveParameter(akq_item1);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
//	public boolean setValue(String property,String value,Document doc)
//	{
//		boolean bflag=true;
//		String name="import";
//		if(value==null)
//			value="";
//		if(value.equals("#"))
//			value="";
//		if(!name.equals(""))
//		{
//			try
//			{
//				String str_path="/param/"+name;
//				XPath xpath=XPath.newInstance(str_path);
//				List childlist=xpath.selectNodes(doc);
//				Element element=null;
//				if(childlist.size()==0)
//				{
//					element=new Element(name);
//					element.setAttribute(property,value);
//					doc.getRootElement().addContent(element);
//				}
//				else
//				{
//					element=(Element)childlist.get(0);
//					element.setAttribute(property,value);
//				}
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//				  bflag=false;
//			}
//		}
//		return bflag;
//	}
//	private String init(String akq_item1)
//	{
//		String xmlcontent="";
//		RecordVo vo=new RecordVo("kq_item");
//		vo.setString("item_id",akq_item1);
//		StringBuffer strxml=new StringBuffer();
//		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
//		strxml.append("<param>");
//		strxml.append("</param>");
//		try
//		{
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			vo=dao.findByPrimaryKey(vo);
//			if(vo!=null)
//				xmlcontent=vo.getString("other_param");
//			if(xmlcontent==null||xmlcontent.equals(""))
//			{
//				xmlcontent=strxml.toString();
//			}
//		}
//		catch(Exception ex)
//		{
//			xmlcontent=strxml.toString();
//			ex.printStackTrace();
//		}
//		return xmlcontent;
//	}
}
