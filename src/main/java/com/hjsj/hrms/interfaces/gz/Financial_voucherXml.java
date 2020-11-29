package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;

public class Financial_voucherXml {
        private String params;     
        private String action;    
        private String target;
        private String image="";
        private UserView userView;
        public Financial_voucherXml (UserView userView,String params,String action,String target) {
            this.userView=userView;
            this.params=params;
            this.target=target;
            this.action=action;
        }
        
        public String outTree()throws GeneralException
        {
            StringBuffer xmls = new StringBuffer();
            StringBuffer strsql = new StringBuffer();
            ResultSet rset = null;
            Connection conn = AdminDb.getConnection();
            ContentDAO dao  = new ContentDAO(conn);
            Element root = new Element("TreeNode");
            root.setAttribute("id","00");
            root.setAttribute("text","root");
            root.setAttribute("title","organization");
            Document myDocument = new Document(root);
            String theaction=null;
            try
            {
              strsql.append("select * from GZ_Warrant order by pn_id");
              rset = dao.search(strsql.toString());
              while (rset.next())
              {
            	  String privflag = this.IsHavePriv(this.userView,rset.getString("b0110"));//1：没关系 2：包含（上级） 3：下级
            	  if("1".equals(privflag)){
            		  continue;
            	  }
	              Element child = new Element("TreeNode");    
	              child.setAttribute("id", rset.getString("pn_id"));
	              String c_name =rset.getString("c_name");
	              if(null==c_name)//xiegh 20170620  避免GZ_Warrant凭证定义表中未c_name为null的数据时报错 
	            	  continue;
	              child.setAttribute("text", c_name);
	              child.setAttribute("title", c_name);
	              if(this.action!=null&&this.action.length()>0)
	              {
	                 theaction=this.action+"?b_query=link&interface_type="+rset.getString("interface_type")+"&pn_id="+rset.getString("pn_id")+"&showflag=2&privflag="+privflag;//链接中记入privflag用于判断后续操作是否可编辑（上级的凭证职能看不能改）
	                 child.setAttribute("href", theaction);
	                 child.setAttribute("target", this.target);
	              }
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
            	PubFunc.closeIoResource(rset);
    			PubFunc.closeIoResource(conn);
          }
          return xmls.toString();        
        }
        /**
         * 判断该用户权限范围与凭证模板的关系 zhaoxg add 2015-9-22
         * @param b0110
         * @return 1：没关系 2：包含（上级） 3：下级
         */
        public static String IsHavePriv(UserView userView,String b0110){
        	String  flag = "1";//该用户的权限范围和凭证模板的关系  1：没关系 2：包含（上级） 3：下级
        	try{
        		if(userView.isSuper_admin()|| "1".equals(userView.getGroupId())){
        			return "2";//超级用户全能看见
        		}
        		if(b0110==null||b0110.length()==0){
        			return "2";//归属单位没设置，任务是所有人均可看见  即为任何用户的下级（用户包含模板权限）
        		}
    			String b_units = userView.getUnitIdByBusiOutofPriv("1");

        		if("un`".equalsIgnoreCase(b_units))
        		    return "2";

    			String unitarr[] =b_units.split("`");
				for(int i=0;i<unitarr.length;i++)
				{
    				String codeid=unitarr[i];
    				if(codeid==null|| "".equals(codeid))
    					continue;
	    			if(codeid!=null&&codeid.trim().length()>2){
						String privCodeValue = codeid.substring(2);	
						String b0110value = b0110.substring(2);
						
						if(privCodeValue.length()>b0110value.length()){
							if(b0110value.equals(privCodeValue.substring(0,b0110value.length()))&&!"2".equals(flag)){//该用户的该权限是凭证模板的下级并且还没被判定是别的权限的上级
								flag = "3";
							}
						}else{
							if(privCodeValue.equals(b0110value.substring(0,privCodeValue.length()))){//该用户权限是包含凭证模板权限
								flag = "2";
							}
						}
	    			}
				}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	return flag;
        }
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
}
