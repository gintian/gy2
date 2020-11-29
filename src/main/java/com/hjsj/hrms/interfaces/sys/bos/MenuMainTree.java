package com.hjsj.hrms.interfaces.sys.bos;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.transaction.sys.bos.menu.InitMenuTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuMainTree {
	String opt="0";
	String codeid="0";
	String parentid ="0";
	UserView userView = null;
	private EncryptLockClient lock;
	
	public EncryptLockClient getLock() {
		return lock;
	}

	public void setLock(EncryptLockClient lock) {
		this.lock = lock;
	}

	public MenuMainTree(String a_opt,String a_codeid,String parent_id)
	{
		this.opt=a_opt;
		this.codeid=a_codeid;
		this.parentid = parent_id;
	}
	
	public MenuMainTree(String a_opt,String a_codeid,String parent_id,UserView userView)
	{
		this.opt=a_opt;
		this.codeid=a_codeid;
		this.parentid = parent_id;
		this.userView = userView;
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
		
		MenuMainBo menuMainBo = new MenuMainBo(null, this.userView);

		ArrayList list =getInfoList(doc);
		int i = 0;
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();
			
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			
			//zxj 20160624 菜单权限特殊判断（主要：新老人事异动的控制）
			if(!menuMainBo.havaMenuPri(codeitemid))
			    continue;
			
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String flag=(String)abean.get("flag");
			String _opt=(String)abean.get("_opt");
			
			// 创建子元素
			Element child = new Element("TreeNode");
			child.setAttribute("icon","/images/close.png");
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
		
			child.setAttribute("href","/system/bos/menu/menuMain.do?b_search=query&encryptParam="+PubFunc.encrypt("parentid="+codeitemid));
			
			child.setAttribute("target", "mil_body"); 
			
			String a_xml="/system/bos/menu/menu_main_tree.jsp?encryptParam="+PubFunc.encrypt("opt=1&codeid=0&menu_id="+codeitemid);
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
		InitMenuTrans init = new InitMenuTrans();
		ArrayList list = new ArrayList();
       //InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/menu.xml");
		//System.out.println("使用this.getClass().getResourceAsStream获得menu.xml");
		//if(in==null){
		//判断session里是否存在写入的文件交给	getDocument方法进行判断获取
		//直接走交易类把doc放在form里
		//Document doc = init.getDocument();
		//InputStream	in = bo.getInputStreamFromjar(arg1);
		//	System.out.println("使用bo.getInputStreamFromjar()获得menu.xml");
		//}
        try
        {
        	//InputStream in = new FileInputStream("D:/Tomcat5.5/webapps/hrms/WEB-INF/classes/com/hjsj/hrms/constant/menu.xml");

	      //  Document doc = saxbuilder.build(in);
	      
	        Element root = doc.getRootElement();
	        List rlist = root.getChildren("menu");
	       
	        for (int i = 0; i < rlist.size(); i++)
	        {
	        	
	        	LazyDynaBean a_bean = new LazyDynaBean();
	          Element node = (Element) rlist.get(i);
	          String menu_id=node.getAttributeValue("id");
	          String menu_name = node.getAttributeValue("name");
	          String mod_id = node.getAttributeValue("mod_id");
	          String func_id = node.getAttributeValue("func_id");
	          if("0".equals(this.opt))
				{
	        	  if("90".equalsIgnoreCase(menu_id)){//工具条按钮在hcm平台之前不支持菜单定制
	        		  if(userView!=null&&!"hcm".equalsIgnoreCase(userView.getBosflag()))
		        		  continue;
	        	  }
	        	  if(!haveFuncPriv(func_id,mod_id)){
	        		  continue;
	        	  }
	        	  
	        	  a_bean.set("codeitemid", menu_id);  
				  a_bean.set("codeitemdesc",menu_name);
				  a_bean.set("_opt","1");
				  a_bean.set("flag","0");
				   list.add(a_bean);
				}
	        }
	         //根据parentid或得当前的node
					 //递归的写法2	
	          //递归的写法1
	        if(!"0".equals(this.opt)){
					String xpath = "//menu[@id=\"" + parentid + "\"]";
		        	XPath xpath_ = XPath.newInstance(xpath);
		        	Element ele = (Element) xpath_.selectSingleNode(doc);
		        	if(ele!=null){
		        	List alist =ele.getChildren("menu");
		        	if(alist.size()>0){
			         for(int j=0;j<alist.size();j++){
			        	   Element node2 = (Element) alist.get(j);
					          String menu_id=node2.getAttributeValue("id");
					          String menu_name = node2.getAttributeValue("name");
					          String mod_id = node2.getAttributeValue("mod_id");
					          String func_id = node2.getAttributeValue("func_id");
					          if(!haveFuncPriv(func_id,mod_id)){
				        		  continue;
				        	  }
					          LazyDynaBean  a_bean = new LazyDynaBean();
					        	  a_bean.set("codeitemid", menu_id);  
								  a_bean.set("codeitemdesc",menu_name);
								  list.add(a_bean);
								  }
		        	
		        			}
		        	}
	        		}
			
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }
        finally
        {
            
        }
        	return list;
	}
	
	 private boolean haveFuncPriv(String function_id,String module_id)
	  {
	      boolean bfunc=true,bmodule=true;
	      
	      /**
	       * 在这里进行权限分析
	       */
	       /**版本功能控制*/
	      VersionControl ver_ctrl=new VersionControl();	
	      UserView userview=userView;   
	      ver_ctrl.setVer(lock.getVersion());

         if(!(module_id==null|| "".equals(module_id)))
         {
       	String[] modules =StringUtils.split(module_id,",");
           for(int i=0;i<modules.length;i++)
           {
           	module_id=modules[i];
           	bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
           	if(bmodule)
           		break;
           }

         }	
	      
         if(!(function_id==null|| "".equals(function_id)))
         {	      
       	  String[] funcs =StringUtils.split(function_id,","); 
       	  for(int i=0;i<funcs.length;i++)
       	  {
       		  bfunc=ver_ctrl.searchFunctionId(funcs[i],true);
       		  if(bfunc)
       			  break;
       	  }   
        }
		 return (bfunc&bmodule);
	  }	
	  
	  /**
	     * 标准版、专业版功能区分
	     * @param funcid
	     * @param ver_s =1专业版 =0标准版
	     * @return
	     */
	    private boolean haveVersionFunc(String funcid,int ver_s)
	    {
	    	return PubFunc.haveVersionFunc(userView, funcid, ver_s);
	    }
}
