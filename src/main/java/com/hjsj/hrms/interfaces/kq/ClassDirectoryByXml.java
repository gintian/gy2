package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.util.ArrayList;

public class ClassDirectoryByXml {

	    private String params;
	   
	    private String action;
	  
	    private String target;
        private String image="";
        private UserView userView;
	    public ClassDirectoryByXml (UserView userView,String params,String action,String target) {
	        this.userView=userView;
	    	this.params=PubFunc.keyWord_reback(params);
	        this.target=target;
	        this.action=action;
	        
	    }
	    
	    public String outTree()throws GeneralException
	    {
	  	
	        StringBuffer xmls = new StringBuffer();
	        Connection conn = AdminDb.getConnection();
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","organization");
	        Document myDocument = new Document(root);
	        String theaction=null;
	        try
	        {
	            ArrayList list = new ArrayList();
	            //取公共提取明细方法
	            KqUtilsClass kqcl = new KqUtilsClass(conn,this.userView);
	            list = kqcl.getKqClassListInPriv();
	            LazyDynaBean ldb = new LazyDynaBean();
	            String codeid = RegisterInitInfoData.getKqPrivCode(userView);
	            String codevalue = RegisterInitInfoData.getKqPrivCodeValue(userView);
	            String changePublicRight = "";
	            if((kqcl.topInstitutions().size()==1&&(codeid+codevalue).equals(kqcl.topInstitutions().get(0)))||userView.isSuper_admin()){
	                changePublicRight="1";
	            }else{
	                changePublicRight="0";
	            }
	            for(int i=0;i<list.size();i++){
	                ldb = (LazyDynaBean) list.get(i);
	                //给树赋值
	                if("0".equals((String)ldb.get("classId")))
	                continue;    
	                Element child = new Element("TreeNode"); 
	                child.setAttribute("id", (String)ldb.get("classId")+"_"+(String)ldb.get("classType")+"_"+changePublicRight);
	                child.setAttribute("text", (String)ldb.get("name")+"-"+(String)ldb.get("classId"));
	                child.setAttribute("title", (String)ldb.get("name"));
	                
	                if(this.action!=null&&this.action.length()>0)
	                {
	                     theaction=this.action+"?b_query=link&encryptParam="+PubFunc.encrypt("class_id="+(String)ldb.get("classId"));
	                     theaction = theaction+"&classType="+ldb.get("classType");
	                     theaction = theaction+"&changePublicRight="+changePublicRight;
	                     child.setAttribute("href", theaction);
	                     child.setAttribute("target", this.target);
	                }
	                
	                //child.setAttribute("xml", "/kq/options/class/class_list.jsp?params=1<2 and class_id<'0'&class_id="+rset.getString("class_id"));
	                if(this.image!=null&&this.image.length()>0)
	                    child.setAttribute("icon",this.image);
	                else
	                  child.setAttribute("icon","/images/table.gif");
	                root.addContent(child);
	                
	            }
	            

	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);

	          xmls.append(outputter.outputString(myDocument));
	        }
	        catch (Exception ee)
	        {
	          ee.printStackTrace();
	          GeneralExceptionHandler.Handle(ee);
	        }
	        finally
	        {
	            PubFunc.closeResource(conn);
	        }
	        return xmls.toString();        
	    }

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}
}
