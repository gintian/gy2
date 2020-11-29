package com.hjsj.hrms.interfaces.sys.bos;

import com.hjsj.hrms.transaction.sys.bos.func.InitFuncTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AchivementMainTree {
	String opt="0";
	String codeid="0";
	String parentid ="0";
	String lockVersion = null;
	String ctrl_ver = null;
	
	public void setLockVersion(String lockVersion) {
		this.lockVersion = lockVersion;
	}
	
	public void setCtrl_ver(String ctrl_ver) {
		this.ctrl_ver = ctrl_ver;
	}
	
	public AchivementMainTree(String a_opt,String a_codeid,String parent_id)
	{
		this.opt=a_opt;
		this.codeid=a_codeid;
		this.parentid = parent_id;
	}
	
	public String outPut_Xml(Document doc) throws GeneralException {
		
//		 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		// 设置跳转字符串
		String theaction = "";

		ArrayList list =getInfoList(doc);
		int i = 0;
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();
			
			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String flag=(String)abean.get("flag");
			String _opt=(String)abean.get("_opt");
			String ctrl_ver = (String)abean.get("ctrl_ver");
			
			child.setAttribute("icon","/images/close.png");
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("extraparam", ctrl_ver);
			child.setAttribute("href","/system/bos/func/functionMain.do?b_search=query&encryptParam="+PubFunc.encrypt("parentid="+codeitemid+"&ctrl_ver="+ctrl_ver));
			
			child.setAttribute("target", "mil_body"); 
			
			String a_xml="/system/bos/func/achivement_main_tree.jsp?opt=1&encryptParam="+PubFunc.encrypt("codeid=0&function_id="+codeitemid+"&ctrl_ver="+ctrl_ver);
			child.setAttribute("xml", a_xml);
			// 将子元素作为内容添加到根元素
			//child的自动加载
			root.addContent(child);//问题出在这　
			
		}

		XMLOutputter outputter = new XMLOutputter();
		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));
		return xmls.toString();
	}
	public ArrayList getInfoList(Document doc)
	{
		InitFuncTrans init = new InitFuncTrans();
		ArrayList list = new ArrayList();
       //InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
		//System.out.println("使用this.getClass().getResourceAsStream获得function.xml");
		//if(in==null){
		//判断session里是否存在写入的文件交给	getDocument方法进行判断获取
		//直接走交易类把doc放在form里
		//Document doc = init.getDocument();
		//InputStream	in = bo.getInputStreamFromjar(arg1);
		//	System.out.println("使用bo.getInputStreamFromjar()获得function.xml");
		//}
        try
        {
        	//InputStream in = new FileInputStream("D:/Tomcat5.5/webapps/hrms/WEB-INF/classes/com/hjsj/hrms/constant/function.xml");

	      //  Document doc = saxbuilder.build(in);
	      
	        Element root = doc.getRootElement();
	        List rlist = root.getChildren("function");
	       
	        for (int i = 0; i < rlist.size(); i++)
	        {
	        	
	        	
	        	LazyDynaBean a_bean = new LazyDynaBean();
	          Element node = (Element) rlist.get(i);
	          /**版本之间的差异控制，市场考滤*/
		        VersionControl ver_ctrl=new VersionControl();
		        /**版本控制*/
		          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
		        	  continue;
	          String func_id=node.getAttributeValue("id");
	          String func_name = node.getAttributeValue("name");
	          
	          //如果权限节点设置了 锁版本号，并且不包含当前版本，跳过此节点 guodd 2018-09-07
	          String ctrl_ver = node.getAttributeValue("ctrl_ver");
	          ctrl_ver = ctrl_ver==null?"":ctrl_ver;
	          if(ctrl_ver.length()>0 && this.lockVersion!=null && (","+ctrl_ver+",").indexOf(","+this.lockVersion+",")==-1)
	        	  continue;
	          
	          if("0".equals(this.opt))
				{
	        	  a_bean.set("codeitemid", func_id);  
				  a_bean.set("codeitemdesc",func_name);
				  a_bean.set("_opt","1");
				  a_bean.set("flag","0");
				  a_bean.set("ctrl_ver",ctrl_ver);
				   list.add(a_bean);
				}
	        }
	         //根据parentid或得当前的node
					 //递归的写法2	
	          //递归的写法1
	        if(!"0".equals(this.opt)){
					String xpath = "//function[@id=\"" + parentid + "\"]";
					if(this.ctrl_ver!=null) {
						xpath = "//function[@id=\"" + parentid + "\" and @ctrl_ver=\""+this.ctrl_ver+"\"]";
					}
		        	XPath xpath_ = XPath.newInstance(xpath);
		        	Element ele = (Element) xpath_.selectSingleNode(doc);
		        	if(ele!=null){
		        		List alist =ele.getChildren("function");
		        		if(alist.size()>0){
		        			for(int j=0;j<alist.size();j++){
		        				Element node2 = (Element) alist.get(j);
		        				/**版本之间的差异控制，市场考滤*/
		        				VersionControl ver_ctrl=new VersionControl();
		        				/**版本控制*/
		        				if(!ver_ctrl.searchFunctionId(node2.getAttributeValue("id")))
					        	  continue;
		        				String func_id=node2.getAttributeValue("id");
		        				String func_name = node2.getAttributeValue("name");
					          
		        				//如果权限节点设置了 锁版本号，并且不包含当前版本，跳过此节点 guodd 2018-09-07
		        				String ctrl_ver = node2.getAttributeValue("ctrl_ver");
		        				ctrl_ver = ctrl_ver==null?"":ctrl_ver;
		        				if(ctrl_ver.length()>0 && this.lockVersion!=null && (","+ctrl_ver+",").indexOf(","+this.lockVersion+",")==-1)
					        	  continue;
					          
		        				LazyDynaBean  a_bean = new LazyDynaBean();
					        	a_bean.set("codeitemid", func_id);  
								a_bean.set("codeitemdesc",func_name);
								a_bean.set("ctrl_ver",ctrl_ver);
								list.add(a_bean);
							}
		        	
		        		}
		        	}
	        	}
			
        }
        catch(Exception ee)
        {
            
        }
        finally
        {
            
        }
        	return list;
	}
}
