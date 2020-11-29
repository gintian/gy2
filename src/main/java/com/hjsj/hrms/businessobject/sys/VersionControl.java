package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class VersionControl {
	/**版本功能字典*/
	private static HashMap hmver;
	/**版本号*/
	private int ver=30;
	
	 public void setVer(int ver) {
		this.ver = ver;
	}

	/**从功能授权配置的文件取得所有的功能编码*/
    private HashMap searchVersionXmlHtml()
    {
        HashMap hash=new HashMap();
        InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/version.xml");
        String xpath="/ehr-version/versions/version";
        try
        {
	        Document doc = PubFunc.generateDom(in);   
	        XPath reportPath = XPath.newInstance(xpath);// 取得根节点
	        List list=reportPath.selectNodes(doc);
	        int currver=0;
	        for (int i = 0; i < list.size(); i++)
	        {
	          Element node = (Element) list.get(i);
	          String valid=node.getAttributeValue("valid");	  
	          String vername=node.getAttributeValue("name");
	          currver=Integer.parseInt(vername);
	          
	          if("true".equalsIgnoreCase(valid)&&(this.ver<=currver))
	          {
	        	  List list_node=node.getChildren("func_id");
	        	  for(int j=0;j<list_node.size();j++)
	        	  {
	        		  Element childR=(Element) list_node.get(j);
	        		  String funcId=childR.getAttributeValue("id");
		        	  String validChild=childR.getAttributeValue("valid");		        	  
		        	  hash.put(funcId,validChild);
	        	  }
	          }
	          else
	          {
	        	  continue;
	          }
	        }
        }catch(Exception e)
        {
        	e.printStackTrace();
        }finally{
            if(in!=null){
                PubFunc.closeIoResource(in);
            }
        }
        return hash;
    }
    
    public  boolean searchFunctionId(String ver_id)
    {
    	boolean isCorrect=false;
    	if(hmver==null) {
            hmver=searchVersionXmlHtml();
        }
    	//HashMap hash=searchVersionXmlHtml();
    	String valid=(String)hmver.get(ver_id);
    	if((valid!=null&& "true".equals(valid))||valid==null)
    	{
    		isCorrect=true;
    	}
    	return isCorrect;
    }
}
