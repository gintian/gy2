package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: SavevalidTrans 
 * @Description: TODO保存指标对应设置到数据xml
 * @author xmsh
 * @date 2013-12-27 上午11:35:21 
 *
 */
public class SavevalidTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM();
		String xml;

		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(this.getFrameconn());

		String resumefld = (String) hm.get("list");
		String resumeset = (String) hm.get("resumeset");
		ArrayList baseitems = (ArrayList) hm.get("baseitems");
		ArrayList itemvalues = (ArrayList) hm.get("itemvalues");

		Document doc;
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		int flag = 0; // xml中是否存在codeset节点,0不存在,1存在

		try {
			xml = resumeImportSchemeXmlBo.getXml();
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+ resumeset + "']/menus");
			Element menus = (Element) xPath.selectSingleNode(doc);
			List list = menus.getChildren();

			String[] resumeArray = resumefld.split("\\|");

			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				for (int j = 0; j < resumeArray.length; j++) {
					String resume = resumeArray[j];
					String resumeid = resume.split("=")[0];
					String valid = resume.split("=")[1];
					// 开始未选中,后来选中
					if (resumeid.equals(element.getAttributeValue("resumefld"))) {
						element.setAttribute("valid", valid);
					}
				}
			}

			for (int i = 0; i < baseitems.size(); i++) {
				XPath xPath1 = XPath.newInstance("/scheme/sets/set[@resumeset='"+ resumeset + "']/menus/menu[@resumefld='"+ (String) baseitems.get(i) + "']");
				Element menu = (Element) xPath1.selectSingleNode(doc);
				// 修改menu指标对应
				String item = (String) itemvalues.get(i);//这是对应的itemid
				String name = (String) baseitems.get(i);//这是对应的描述
				// 判断是否修改
				if (!item.equalsIgnoreCase(menu.getAttributeValue("ehrfld"))) {//对应的指标发生了变化

					menu.setAttribute("ehrfld", (String) itemvalues.get(i));
					// 判断代码型指标,并添加或修改codesets
					XPath mPath = XPath.newInstance("/scheme/codesets");
					Element codesets = (Element) mPath.selectSingleNode(doc);
					List child = codesets.getChildren();
					this.frowset = dao.search("select codesetid from FIELDITEM where UPPER(ITEMID)='"+ item.toUpperCase()+ "' and UPPER(CODESETID)<>'0'");
					// 是代码型指标
					if (frowset.next()) {//都得到对应的代码itemid了何苦再去查？ 唉···
						String codesetid = frowset.getString("codesetid");
						// 判断是否存在codeset
						for (int j = 0; j < child.size(); j++) {
							Element element = (Element) child.get(j);//得到每一个codeset
							// 存在codeset节点并且codeset所对应的指标发生了改变
							if (element.getAttributeValue("resumefld").equals(name)&& !element.getAttributeValue("ehrfld").equalsIgnoreCase(item)) {
								flag = 1;
							}
							if (flag == 1) {
								element.setAttribute("ehrfld", item); // 修改对应的指标id
								// 删除原有的codeitems
								element.removeChildren("codeitems");
								// 生成新的codeitems节点
								Element codeitems = new Element("codeitems");

								rs = dao.search("select * from codeitem where codesetid='"+ codesetid + "'");
								while (rs.next()) {
									Element codeitem = new Element("codeitem");
									codeitem.setAttribute("ehritemid", rs.getString("codeitemid"));
									codeitems.addContent(codeitem);
								}
								element.addContent(codeitems);
								flag = 2;

							}
						}
						Element codeset = new Element("codeset");
						if (flag == 0) {
							// 新建codeset节点

							codeset.setAttribute("ehrfld", item);
							codeset.setAttribute("resumefld", name);
							codeset.setAttribute("resumeset", resumeset);
							codeset.setAttribute("codesetid", codesetid);//为xml新加一个属性codesetid防止对应指标未变，但是对应指标关联的代码类发生了变化时用到
							codesets.addContent(codeset);

							// 生成新的codeitems
							Element codeitems = new Element("codeitems");

							rs = dao.search("select * from codeitem where codesetid='"+ codesetid + "'");
							while (rs.next()) {
								Element codeitem = new Element("codeitem");
								codeitem.setAttribute("ehritemid", rs.getString("codeitemid"));
								codeitems.addContent(codeitem);
							}
							codeset.addContent(codeitems);
						}
					} else {
						// 判断是否存在codeset,若存在则删除
						for (int j = 0; j < child.size(); j++) {
							Element element = (Element) child.get(j);
							if (element.getAttributeValue("resumefld").equals(name)) {
								flag = 1;
							}
							if (flag == 1) {
								codesets.removeContent(element);
							}
						}
					}
				}else{//如果对应的指标没有发生变化  代码类的要刷新数据
					FieldItem changeItem =DataDictionary.getFieldItem(item);
					if(changeItem==null){
						continue;
					}
					if(changeItem!=null&&!"0".equals(changeItem.getCodesetid())){//如果是代码类
						XPath mPath = XPath.newInstance("/scheme/codesets");
						Element codesets = (Element) mPath.selectSingleNode(doc);//得到所有的codeset
						ArrayList codeItemList=AdminCode.getCodeItemList(changeItem.getCodesetid());//得到代码类中的所有代码项
						List child = codesets.getChildren();
						Element codeitemsElement=null;
						Element codesetElement=null;
						String codesetId="";
						for(int n=0;n<child.size();n++){
							Element codeset =(Element) child.get(n);//得到循环中的codeset
							String ehrfld=codeset.getAttributeValue("ehrfld");//xml中存放的指标对应的字段
							if(ehrfld.equalsIgnoreCase(item)){//如果是同一个指标，那么得到codeitems
								codesetId=codeset.getAttributeValue("codesetid");
								codeitemsElement=codeset.getChild("codeitems");//得到codeitems结点
								codesetElement=codeset;
								break;
							}
						}
						if(codeitemsElement!=null){//对应指标没变，但指标所关联的代码类发生了变化，那么移除掉原来有的，增加现在数据字典中的
							/**将原有的数据从codesets里面移除,并加载新的**/
							codesets.removeContent(codesetElement);
							/**开始最追加新的数据codeset数据**/
							Element newCodeset=new Element("codeset");
							newCodeset.setAttribute("ehrfld", item);
							newCodeset.setAttribute("resumefld", name);
							newCodeset.setAttribute("resumeset", resumeset);
							newCodeset.setAttribute("codesetid",changeItem.getCodesetid());
							Element newcodeitems = new Element("codeitems");
							for(int n=0;n<codeItemList.size();n++){//数据字典中的数据永远是正确的
								CodeItem codeitem=(CodeItem) codeItemList.get(n);
								String codeitemid=codeitem.getCcodeitem();
								Element newcodeitem= new Element("codeitem");
								newcodeitem.setAttribute("ehritemid",codeitemid);
								newcodeitems.addContent(newcodeitem);
							}
							newCodeset.addContent(newcodeitems);
							codesets.addContent(newCodeset);
						}
						if(codeitemsElement==null||codesetElement==null){//这种情况是由非代码类转成代码类出现的
							Element newCodeset=new Element("codeset");
							newCodeset.setAttribute("ehrfld", item);
							newCodeset.setAttribute("resumefld", name);
							newCodeset.setAttribute("resumeset", resumeset);
							newCodeset.setAttribute("codesetid",changeItem.getCodesetid());
							Element newcodeitems = new Element("codeitems");
							for(int n=0;n<codeItemList.size();n++){//数据字典中的数据永远是正确的
								CodeItem codeitem=(CodeItem) codeItemList.get(n);
								String codeitemid=codeitem.getCcodeitem();
								Element newcodeitem= new Element("codeitem");
								newcodeitem.setAttribute("ehritemid",codeitemid);
								newcodeitems.addContent(newcodeitem);
							}
							newCodeset.addContent(newcodeitems);
							codesets.addContent(newCodeset);
						}
					}else{//如果不是代码类，判断是否是由原来的代码类转变成了非代码类
						XPath mPath = XPath.newInstance("/scheme/codesets");
						Element codesets = (Element) mPath.selectSingleNode(doc);//得到所有的codeset
						List child = codesets.getChildren();
						Element codesetElement=null;
						for(int n=0;n<child.size();n++){
							Element codeset =(Element) child.get(n);//得到循环中的codeset
							String ehrfld=codeset.getAttributeValue("ehrfld");//xml中存放的指标对应的字段
							String inresumefld=codeset.getAttributeValue("resumefld");//存在于xml中的数据
							if(ehrfld.equalsIgnoreCase(item)&&inresumefld.equalsIgnoreCase(name)){//如果是同一个指标，那么得到codeset
								codesetElement=codeset;
								break;
							}
						}
						if(codesetElement!=null){
							codesets.removeContent(codesetElement);//移除掉存在的代码对应项
						}
					}
				}
			}

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent(" ");
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(format);
			XMLOut.output(doc, bo);
			String StrValue = bo.toString();
			resumeImportSchemeXmlBo.UpdateConstantXml(StrValue); // 更新数据库中的xml
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
