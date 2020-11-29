/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class MenuRevertTrans extends IBusiness {
    /**
	 */


	
	
	public void execute() throws GeneralException {
		FormFile file = (FormFile) this.getFormHM().get("file");
		InputStream in = null;
		try {
			boolean flag = FileTypeUtil.isFileTypeEqual(file);
			if(!flag){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			in = file.getInputStream();
			Document doc1=null;
			if(this.getFormHM().get("menu_dom")!=null){//dml 2011-04-19
				 doc1 = (Document)this.getFormHM().get("menu_dom");
			}else{
				FuncMainBo fbo = new FuncMainBo();
				doc1 =fbo.getDocument();
			}
				Document doc =  PubFunc.generateDom(in);
				//hej 2015-07-08 校验xml文件是否正确
				Element hrp_re = doc1.getRootElement();
				Element hrp = doc.getRootElement();
				if(!hrp.getName().equals(hrp_re.getName())){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
				}
				List list=hrp.getChildren();
				for(int k=0;k<list.size();k++){
					Element dd=(Element)list.get(k);								//通过备份文件中节点id查找session中xml要更新的节点
					String id=dd.getAttributeValue("id");
					String xpath = "//menu[@id=\"" + id.toLowerCase() + "\"]";
					XPath xPath = XPath.newInstance(xpath);
		        	Element function = (Element) xPath.selectSingleNode(doc1);
		        	if(function!=null){												//如果session中xml存在该节点，则找到其父节点删除该节点将备份中的节点加入到该节点下。
		        		Element parent=function.getParentElement();
		        		parent.removeContent(function);
		        		Element cp=(Element)dd.clone();
		        		parent.addContent(cp);
		        	}else{															//如果session中xml不存在该节点，则找根节点的parentid找到该节点的父节点，将该节点直接加入到父节点下。
		        		Element hr=doc1.getRootElement();
		        		Element paa=doc.getRootElement();
		        		String parent=paa.getAttributeValue("parentid");
		        		if(parent!=null&&parent.length()!=0){
		        			String temp="//menu[@id=\"" + parent.toLowerCase() + "\"]";
		        			xPath = XPath.newInstance(temp);
		        			Element pafunction = (Element) xPath.selectSingleNode(doc1);
		        			if(pafunction!=null){
		        				Element cp=(Element)dd.clone();
		        				pafunction.addContent(cp);
		        			}else{													//如果session中xml没找到parentid对应节点将该节点直接放在根节点下
		        				Element cp=(Element)dd.clone();
				        		hr.addContent(cp);
		        			}
		        		}else{														//如果session中xml根节点没有parentid属性则将节点直接加到根节点下。
		        			Element cp=(Element)dd.clone();
		        			hr.addContent(cp);
		        		}
		        	}
				}
				this.getFormHM().put("menu_dom", doc1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (JDOMException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(in);//资源释放 jingq 2014.12.29
		}
   
	} 

}
