package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class OrgTreeBo {
	 /**
	  * 创建公司树
	  * @param itemid
	  * @param userView
	  * @param nbase
	  * @param preflag 1.培训机构,2.培训课程推送选人（以后扩展用，暂时就这一情况）
	  * @param itemkey 过滤人员条件所需要的值
	  * @return
	  * @throws GeneralException
	  */
	   public String outViewAreaTree(String itemid,UserView userView,
			   String nbase,String preflag,String itemkey)throws GeneralException{
	        StringBuffer xmls = new StringBuffer();
	        StringBuffer sqlstr = new StringBuffer();
	        Connection conn = AdminDb.getConnection();
	        Statement stmt = null; 
	        ResultSet rset = null;
	        Element root = new Element("TreeNode");
	        
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","codeitem");
	        Document myDocument = new Document(root);
	        preflag = preflag!=null&&preflag.trim().length()>0?preflag:"1";
	        String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
	        DbSecurityImpl dbS = new DbSecurityImpl();
	        try{
	          sqlstr.append("select codesetid,codeitemid,codeitemdesc from organization where (codesetid='UN' or codesetid='UM')");
	          sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
	          if("root".equalsIgnoreCase(itemid)){
	        	  if(userView.isSuper_admin()&& "1".equals(userView.getGroupId())){
	        		  sqlstr.append(" AND codeitemid=parentid ");
	      		}else{
	      			//String managepriv = userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	      			//if((managepriv !=null && managepriv.trim().length()==2)){
	      			//	sqlstr.append(" AND codeitemid=parentid ");
	      			//}else if((managepriv !=null && managepriv.trim().length()>=2)){
	      			//	managepriv=managepriv.substring(2,managepriv.length());
	      			//	sqlstr.append(" AND codeitemid='");
	      			//	sqlstr.append(managepriv);
	      			//	sqlstr.append("'");  
	      			//}else{
	      			//	sqlstr.append(" AND 1=2");	    		
	      			//}
	      			TrainCourseBo bo = new TrainCourseBo(userView);
	      			String managepriv = bo.getUnitIdByBusi();
	      			if(managepriv!=null&&(managepriv.length()==3||managepriv.toUpperCase().indexOf("UN`")!=-1)){
	      				sqlstr.append(" AND codeitemid=parentid ");
	      			}else if(managepriv !=null && managepriv.trim().length()>2){
	      				String tmp[] = managepriv.split("`");
	      				sqlstr.append(" AND (");
	      				for (int i = 0; i < tmp.length; i++) {
							if(i>0) {
                                sqlstr.append(" or ");
                            }
							sqlstr.append("codeitemid='"+tmp[i].substring(2)+"'");
						}
	      				sqlstr.append(")");
	      			}else{
	      				sqlstr.append(" AND 1=2");
	      			}
	      		}
	          }else{
	        	  sqlstr.append(" AND parentid='");
	        	  sqlstr.append(itemid);
	        	  sqlstr.append("'");
	        	  sqlstr.append(" AND codeitemid<>parentid ");
	          }
	          sqlstr.append(" order by A0000,codeitemid");
	          
	          stmt = conn.createStatement();
	          dbS.open(conn, sqlstr.toString());
	          rset = stmt.executeQuery(sqlstr.toString());
	          while (rset.next()){
	            Element child = new Element("TreeNode");
	            String codeitemid=rset.getString("codeitemid");
	            String codeitemdesc = rset.getString("codeitemdesc");
	            String codesetid = rset.getString("codesetid");

	            child.setAttribute("id",SafeCode.encode(PubFunc.encrypt(codesetid+codeitemid)));
	            child.setAttribute("text", codeitemdesc);
	            child.setAttribute("title",codeitemdesc);
	            StringBuffer xml = new StringBuffer();
	            xml.append("/train/request/select_tree.jsp?itemid=");
	            xml.append(codeitemid);
	            xml.append("&nbase=");
	            xml.append(nbase);
	            xml.append("&preflag=");
	            xml.append(preflag);
	            xml.append("&itemkey=");
	            xml.append(itemkey);
	            
	            child.setAttribute("xml",xml.toString());
	            if("UN".equalsIgnoreCase(codesetid)) {
                    child.setAttribute("icon","/images/unit.gif");
                } else if("UM".equalsIgnoreCase(codesetid)) {
                    child.setAttribute("icon","/images/dept.gif");
                } else if("@K".equalsIgnoreCase(codesetid)) {
                    child.setAttribute("icon","/images/pos_l.gif");
                } else {
                    child.setAttribute("icon","/images/unit.gif");
                }
	            root.addContent(child);
	          }
	          ArrayList dblist = new ArrayList();
	          nbase=nbase!=null&&nbase.trim().length()>0?nbase:"";
	          nbase= "all".equalsIgnoreCase(nbase)?"":nbase;
	          String nbasearr[] = nbase.split(","); 
	          if(nbase.trim().length()>0&&nbasearr.length>0){
	        	  for(int i=0;i<nbasearr.length;i++){
	        		  if(nbasearr[i]!=null&&nbasearr[i].length()>0) {
                          dblist.add(nbasearr[i]);
                      }
	        	  }
	          }else {
                  dblist=userView.getPrivDbList();
              }
	          
	         //培训考试计划手工选人特殊处理  取参培参数设置的交集
	         ArrayList arrayList = new ArrayList();
	         ArrayList sel_nbase = new ArrayList();
 			 ConstantXml constantbo = new ConstantXml(conn,"TR_PARAM");
 			 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
 			 if(tmpnbase!=null&&tmpnbase.length()>0){
 				 String nbs[]=tmpnbase.split(",");
 				 for(int i=0;i<nbs.length;i++){
 					 if(nbs[i]!=null&&nbs[i].length()>0){
 						 sel_nbase.add(nbs[i]);
 					 }
 				 }
 			 }
	          for (int i = 0; i < dblist.size(); i++) {
					if(sel_nbase.contains(dblist.get(i))) {
                        arrayList.add(dblist.get(i));
                    }
			  }
	          
	          for(int i=0;i<arrayList.size();i++){
	        	  String pre=(String)arrayList.get(i);
	        	  if(pre!=null&&pre.trim().length()>0){
	        		  StringBuffer buf = new StringBuffer();
	    	          buf.append("select A0100,A0101,B0110,E0122,");
	    	          buf.append("(select codeitemdesc from organization where codeitemid=");
	        	  	  buf.append(pre+"A01.B0110) as B0110_desc,");
	        	  	  buf.append("(select codeitemdesc from organization where codeitemid=");
	        	  	  buf.append(pre+"A01.E0122) as E0122_desc");
	    	          buf.append(" from "+pre+"A01 where B0110 ='");
	    	          buf.append(itemid);
	    	          buf.append("' or E0122 ='");
	    	          buf.append(itemid);
	    	          buf.append("'");
	    	          if("1".equals(preflag)){
	        	  		  buf.append(" and A0100 not in(select R4001 from R40 where R4005='");
	        	  		  buf.append(itemkey+"' and nbase='"+pre+"')");  
	        	  	  }else if("2".equals(preflag)){
	        	  		  if(itemkey!=null&&itemkey.length()>0){
	        	  			  buf.append(" and A0100 not in(select A0100 from tr_selected_lesson where nbase='"+pre+"' and r5000 in (");
	        	  			  String[] t = itemkey.split(",");
	        	  			  int j = 0;
	        	  			  for (; j < t.length; j++) {
	        	  				  if(j>0) {
                                      buf.append(",");
                                  }
								 buf.append(t[j]);
							  } 
	        	  			  buf.append(") GROUP BY A0100 HAVING count(A0100)>"+(j-1));
	        	  			  buf.append(")");
	        	  		  }
	        	  	  }
	    	          buf.append(" order by b0110,e0122,e01a1,a0000");
	    	          dbS.open(conn, buf.toString());
	    	          //关闭上一个rset
	    	          PubFunc.closeResource(rset);
	    	          rset = stmt.executeQuery(buf.toString());
	    	          while (rset.next()){
	    	        	  Element child = new Element("TreeNode");
	    		          String codeitemid=rset.getString("A0100");
	    		          String codeitemdesc = rset.getString("A0101");
	    		          codeitemdesc=codeitemdesc!=null?codeitemdesc:"";
	    		          String B0110 = rset.getString("B0110");
	    		          String E0122 = rset.getString("E0122");
	    		          String B0110_desc = rset.getString("B0110_desc");
	    		          B0110_desc=B0110_desc!=null?B0110_desc:"";
	    		          String E0122_desc = rset.getString("E0122_desc");
	    		          E0122_desc=E0122_desc!=null?E0122_desc:"";
	    		          String id=SafeCode.encode(PubFunc.encrypt(codeitemid))+"::"+codeitemdesc;
	    		          if(B0110!=null&&B0110.trim().length()>0){
	    		        	  if(B0110.equals(itemid)){
	    		        		  if(E0122!=null&&E0122.trim().length()>0) {
                                      continue;
                                  } else{
	    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt("UN"+ B0110));
	    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt(B0110_desc));
	    		        		  }
	    		        	  }else{
	    		        		  if(E0122!=null&&E0122.trim().length()>0){
	    		        			  if(!E0122.equals(itemid)) {
                                          continue;
                                      } else{
		    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt("UM"+ E0122));
		    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt(E0122_desc));
	    		        			  }
	    		        		  } else{
	    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt("UM"+ E0122));
	    		        			  id+="::" + SafeCode.encode(PubFunc.encrypt(E0122_desc));
	    		        		  }
	    		        	  }
	    		          }else{
	    		        	  id+="::" + SafeCode.encode(PubFunc.encrypt("root")); 
	    		        	  id+="::";
	    		          }
	    		          id+="::"+SafeCode.encode(PubFunc.encrypt(pre));

	    		          child.setAttribute("id",id);
	    		          child.setAttribute("text", codeitemdesc);
	    		          child.setAttribute("title",codeitemdesc);
	    		          child.setAttribute("xml","aaaa");
	    		          child.setAttribute("icon","/images/man.gif");
	    		          root.addContent(child);
	    	          }
	        	  }
	          }
	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(myDocument));
	        }catch (Exception ee){
	          ee.printStackTrace();
	          String ss="2313123";
	          ss.trim();
	        }finally{
	        	// 关闭Wallet
	        	dbS.close(conn);
	        	PubFunc.closeDbObj(rset);
	        	PubFunc.closeDbObj(stmt);
	        	PubFunc.closeDbObj(conn);
	            
	        }
	        return xmls.toString();        
	    }
}
