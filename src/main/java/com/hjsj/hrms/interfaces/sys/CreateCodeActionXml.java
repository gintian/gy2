/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.ScormXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateCodeActionXml {
    /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /**根据管理范围找第一层代码*/
    private String privflag;
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
    /**
     * 获得子节点的jsp页名
     */
    private String getcodetree;
    
    private UserView userView;
    private String backdate;
    private String validateflag;
    private String checked;
    // 课件id
    private String r5100;
    // 分类
    private String classes;
    // 课程id
    private String r5000;
    
    private ArrayList valueList = new ArrayList();
    
    public void setClasses(String classes) {
		this.classes = classes;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	/*
     * 
     */
    public CreateCodeActionXml(String codesetid,String codeitemid) {
        this.codeitemid=codeitemid;
        this.codesetid=codesetid;
        this.getcodetree="/system/get_code_tree.jsp";
    }
    
    public CreateCodeActionXml(String codesetid,String codeitemid,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.getcodetree="/system/get_code_tree.jsp";
    } 
    public CreateCodeActionXml(String codesetid,String codeitemid,String action,String target,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree="/system/get_code_tree.jsp";
    	/**/
    }    
    public CreateCodeActionXml(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	/**/
    }
    public CreateCodeActionXml(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree,UserView userView) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	this.userView=userView;
    	/**/
    }
    
    public CreateCodeActionXml(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree,UserView userView,String backdate) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	this.userView=userView;
    	this.backdate=backdate;
    	/**/
    }
    public CreateCodeActionXml(String codesetid,String codeitemid,String action,String target,String privflag,String getcodetree,UserView userView,String backdate,String checked,String validateflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.action=action;
    	this.target=target;
    	this.getcodetree=getcodetree;
    	this.userView=userView;
    	this.backdate=backdate;
    	this.checked=checked;
    	this.validateflag=validateflag;
    	/**/
    }
    /**求查询代码的字符串*/
    private String getQueryString()
    {
        valueList.clear();
    	String codesetidL = "";
		String flag = "";
		if (this.codesetid.indexOf("_") != -1) {
			codesetidL = this.codesetid;
			String[] codesetidLs=codesetidL.split("_");
			if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
				this.codesetid=codesetidLs[0];
				flag=codesetidLs[1];
			}
		}
		
        StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
        	str.append("select codesetid,codeitemid,codeitemdesc,childid,b0110 from codeitem where codesetid=?");
            valueList.add(this.codesetid);
        } else  if ("40000".equals(this.codesetid) || "40001".equals(this.codesetid)) {
        	str.append("select * from r51 where r5100=?");
        	valueList.add(this.r5100);
        }
        else
        {
            str.append("select codesetid,codeitemid,codeitemdesc,childid,b0110,end_date,invalid from codeitem where codesetid=?");
            valueList.add(this.codesetid);
            
            if ("55".equalsIgnoreCase(this.codesetid)) {//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
    			if ("1".equalsIgnoreCase(flag))
    				str.append(" and not exists(select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
    			else if ("2".equalsIgnoreCase(flag)){
    				str.append(" and exists(select 1 from r50 where R5022='04'"); 
    				backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    				str.append(" and "+Sql_switcher.year("R5030")+"*10000+"+Sql_switcher.month("R5030")+"*100+"+Sql_switcher.day("R5030")+"<="+backdate);
    				str.append(" and "+Sql_switcher.year("R5031")+"*10000+"+Sql_switcher.month("R5031")+"*100+"+Sql_switcher.day("R5031")+">="+backdate);
    				str.append(" and r50.codeitemid=codeitem.codeitemid)");
    			}
    			
    			if("55_1".equalsIgnoreCase(codesetidL)|| "55_2".equalsIgnoreCase(codesetidL))
    				this.codesetid = codesetidL;
    		}
        }
        
        if (!"40000".equalsIgnoreCase(this.codesetid) && !"40001".equalsIgnoreCase(this.codesetid)){
	        /**所有的第一层代码值列表*/
	        if(privflag==null|| "".equals(privflag))
	        {
	        	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
		              str.append(" and parentid=codeitemid");
		        }
		        else
		        {
		            str.append(" and parentid<>codeitemid and parentid=?");
		            valueList.add(codeitemid);
		         }    
	        }
	        else //根据管理范围过滤相应的节点内容
	        {
	            if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
	            	   str.append(" and parentid=codeitemid");
		        }
		        else
		        {
		        	str.append(" and codeitemid=?");
		        	valueList.add(codeitemid);
		        }
	        }
	        
	        if(validateflag!=null&&"1".equals(validateflag)){
		        if(backdate!=null&&backdate.length()>0){
		        	/**用between方法，今天新建的，树中显示不出来*/
		        	String s_str=PubFunc.getDateSql("<=", "start_date", backdate);
		        	String e_str=PubFunc.getDateSql(">=", "end_date", backdate);
		        	//str.append(" and "+com.hrms.hjsj.utils.Sql_switcher.dateValue(backdate)+" between start_date and end_date"); 
		        	str.append(" and (("+s_str.substring(4)+" "+e_str+") or (start_date is null))");
		        }
	        }else{
	        	if(checked!=null&&checked.length()>0){
	        		if(!"1".equals(checked)){
	        			str.append(" and invalid=1"); 
	        		}
	        	}
	        }
	       str.append(" order by a0000,codeitemid");
        }
        return str.toString();
    }
    
    public String outCodeTree()throws GeneralException
    {
    	this.action = PubFunc.keyWord_reback(this.action);//add by wangchaoqun on 2014-9-13 对于转换为全角的链接进行还原
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString(), valueList);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          Date nowDate=sdf.parse(sdf.format(new Date()));
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            
            theaction=this.action+"?b_query=link&encryptParam="+PubFunc.encrypt("a_code="+rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            if(!rset.getString("childid").equals(rset.getString("codeitemid"))) //如果是叶子节点就没有前边的展开符号了
                child.setAttribute("xml", this.getcodetree + "?encryptParam="+PubFunc.encrypt("codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid")+"&action="+this.action+"&target="+this.target+"&backdate="+this.backdate+"&checked="+this.checked+"&validateflag="+this.validateflag));
            if("68".equals(codesetid)){
            	String tmpb0110=rset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	int isP = tbo.isUserParent(tmpb0110);
            	if(userView!=null&&!userView.isSuper_admin()){
            		if(isP==-1){
            			continue;
            		}else if(isP==2){
            			child.setAttribute("icon","/images/book1.gif");//上级图片 待定
            		}else
            			child.setAttribute("icon","/images/book.gif");
            	}else
            		child.setAttribute("icon","/images/book.gif");
            }else{
            	if(validateflag!=null&&validateflag.length()>0&&"1".equals(validateflag)){
        	        if(backdate!=null&&backdate.length()>0){
        	        	if(rset.getDate("end_date")!=null&&nowDate.compareTo(rset.getDate("end_date"))<=0){
        	        		child.setAttribute("icon","/images/table.gif");
        	        	}else{
        	        		child.setAttribute("icon","/images/table1.gif");
        	        	}
        	        }else{
        	        	child.setAttribute("icon","/images/table.gif");
        	        }
                }else if(validateflag!=null&&validateflag.length()>0&&"0".equals(validateflag)){
                	if(checked!=null&&checked.length()>0){
                		if(!"1".equals(checked)){
                			child.setAttribute("icon","/images/table.gif");
                		}else{
                			int invalid=rset.getInt("invalid");
                			if(1==invalid){
                				child.setAttribute("icon","/images/table.gif");
                			}else{
                				child.setAttribute("icon","/images/table1.gif");
                			}
                		}
                	}else{
                		child.setAttribute("icon","/images/table.gif");
                	}
                }else{
                	child.setAttribute("icon","/images/table.gif");
                }
            }
            	
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        } catch (ParseException e) {
			e.printStackTrace();
		}
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
           
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    public String outTrainCodeItemTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        //String theaction=null;
        try
        {
          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString(), valueList);
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid") + rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid"));
            if(!userView.isSuper_admin()){
            	String tmpb0110=rset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	int isP = tbo.isUserParent(tmpb0110);
            	if(isP==-1)
            		continue;
            	else
            		child.setAttribute("icon","/images/book.gif");
            }else
            	child.setAttribute("icon","/images/book.gif");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
           
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    public String outCodeItemTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        RowSet rs = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        ContentDAO dao = new ContentDAO(conn);
        try
        {
          strsql.append(getQueryString());
          rset = dao.search(strsql.toString(), valueList);
          while (rset.next())
          {
            boolean flag = false;
            Element child = new Element("TreeNode");
            child.setAttribute("id", SafeCode.encode(PubFunc.encrypt(rset.getString("codeitemid"))));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            if("55_1".equalsIgnoreCase(codesetid) || "55".equalsIgnoreCase(codesetid) || "54".equalsIgnoreCase(codesetid) || "69".equalsIgnoreCase(codesetid))
                theaction=this.action+"&encryptParam="+PubFunc.encrypt("a_code="+SafeCode.encode(PubFunc.encrypt(rset.getString("codeitemid"))));
            else
                theaction=this.action+"&encryptParam="+PubFunc.encrypt("a_code="+rset.getString("codeitemid"));
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            if(hasChild(codesetid,rset.getString("codeitemid"),conn))
                child.setAttribute("xml", this.getcodetree + "?encryptParam="+PubFunc.encrypt("codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid")));
            
            if("55_1".equalsIgnoreCase(codesetid) || "55".equalsIgnoreCase(codesetid)){
                rs = dao.search("select 1 from r50 where codeitemid='" + rset.getString("codeitemid") + "'");
                if(rs.next())
                    flag =true;
            }
            
            if(("54".equals(codesetid) || "69".equals(codesetid) || "55".equals(codesetid) 
            		|| "55_1".equals(codesetid) || "55_2".equals(codesetid))&&userView!=null&&!userView.isSuper_admin()){
            	String tmpb0110=rset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	int isP = tbo.isUserParent(tmpb0110);
            
            	if(isP==-1)
            		continue;
            	else if(isP==2){
            	    if(flag || "55_2".equalsIgnoreCase(codesetid))
            	        child.setAttribute("icon","/images/icon_wsx.gif");//上级图片 待定
            	    else
            	        child.setAttribute("icon","/images/book1.gif");//上级图片 待定
            	} else {
            	    if(flag || "55_2".equalsIgnoreCase(codesetid))
                        child.setAttribute("icon","/images/icon_wsx.gif");
                    else
            		    child.setAttribute("icon","/images/book.gif");
            	}
            }else {
                if(flag || "55_2".equalsIgnoreCase(codesetid))
                    child.setAttribute("icon","/images/icon_wsx.gif");
                else
                    child.setAttribute("icon","/images/book.gif");
            }
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
    /**
     * 获得tree的xml内容
     * @return
     * @throws GeneralException
     */
	public String outCodeItemTree2()throws GeneralException {
		// tree的xml内容
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = null;
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction = null;
        if(this.r5100 != null || this.r5000.length() > 0)
        	this.r5100 = PubFunc.decrypt(SafeCode.decode(this.r5100));
        
        try {
        	conn = AdminDb.getConnection();
        	
        	ContentDAO dao = new ContentDAO(conn);
        	
        	// 保存sco 的id及状态
        	Map map = new HashMap();
        	if ("40001".equals(this.codesetid)){
        	    ArrayList<String> list = new ArrayList<String>();
	        	strsql.delete(0, strsql.length());
	        	strsql.append("select * from tr_selected_course_scorm where a0100=?");
	        	strsql.append(" and nbase=?");
	        	strsql.append(" and r5100=?");
	        	list.add(this.userView.getA0100());
	        	list.add(this.userView.getDbname());
	        	list.add(this.r5100);
	        	rset = dao.search(strsql.toString(), list);
	        	while (rset.next()) {
	        		map.put(rset.getString("scoid"), rset.getString("lesson_status"));
	        	}
        	
        	}
        	strsql.delete(0, strsql.length());
        	strsql.append(getQueryString());
        	rset = dao.search(strsql.toString(), valueList);
        	if (rset.next()) {
        		// 课件的imsmanifest.xml文件内容
        		String xmlContent = rset.getString("xmlcontent");
        		if(StringUtils.isEmpty(xmlContent))
        		    return "";
        		
        		//  解析xml文件
        		String path = "";
        		if (this.codeitemid == null || this.codeitemid.length() <= 0 ) {
        			path = "/mo:manifest/mo:organizations/mo:organization";
        		} else {
        			path = SafeCode.decode(codeitemid);
        		}
        		
        		ScormXMLBo bo = new ScormXMLBo(xmlContent, "mo");
        		
        		// 获得organization节点
        		List orgList = new ArrayList();
        		if (this.codeitemid == null || this.codeitemid.length() <= 0 ) {
        			orgList = bo.getElement(path);
        		} else {
        			org.dom4j.Element el = (org.dom4j.Element) bo.getSingleElement(path);
        			orgList = el.elements("item");
        		}
        		
        		// 当organization节点只有一个，且没有title信息时，不显示该节点
        		if (orgList.size() == 1) {
        			org.dom4j.Element el = (org.dom4j.Element) orgList.get(0);
        			String title = el.elementText("title");
        			if (title == null && "organization".equalsIgnoreCase(el.getName())) {
        				orgList = el.elements("item");
        			}
        		}
        		
        		String[] num = new String[]{"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五","十六","十七","十八","十九","二十","二十一","二十二","二十三","二十四","二十五","二十六","二十七","二十八","二十九","三十"};
        		int numCount = 0;
        		
        		for (int i = 0; i < orgList.size(); i++) {
        			org.dom4j.Element el = (org.dom4j.Element) orgList.get(i);
        			       			
        			Element child = new Element("TreeNode");
        			String identifier = el.attributeValue("identifier");
        			String title = el.elementText("title");
        			if (title == null && "organization".equalsIgnoreCase(el.getName()) && orgList.size() > 1){
        				String str = "";
        				if (numCount < 30) {
        					str = num[numCount];
        					numCount ++;
        				}
        				title = title == null ? "课程" + str : title;
        			} else {
        				title = title == null ? "" : title;
        			}
                    child.setAttribute("id", identifier);
                    child.setAttribute("text", title);
                    child.setAttribute("title", title);
                   
                    String identifierref = el.attributeValue("identifierref");
                    
                    if (identifierref != null && identifierref.length() > 0) {
//	                    xpath = XPath.newInstance("/manifest/resources/resource[@identifier='"+ identifierref +"']");
	                    // 获得resource节点
	                    org.dom4j.Element el2 = (org.dom4j.Element) bo.getSingleElement("/mo:manifest/mo:resources/mo:resource[@identifier='"+ identifierref +"']");
	                    // 获得resource类型
	                    String  type = el2.attributeValue("scormtype");
	                    String htmHref = "";
	                    if ("sco".equalsIgnoreCase(type)) {
	                    	htmHref = el2.attributeValue("href");
	                    }
	                    
	                    theaction = this.action+"&a_code=" + identifier + "&htmhref=" + htmHref;
                    } else {
                    	theaction = "javascript:;";
                    }
                    
                    child.setAttribute("href", theaction);
                    child.setAttribute("target", this.target);
                    child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid+"&r5000="+this.r5000+"&classes="+this.classes+"&r5100=" + SafeCode.encode(PubFunc.encrypt(this.r5100)) + "&codeitemid=" + SafeCode.encode(this.getPath(el) + "[@identifier='" + identifier + "']"));
                    if ("40001".equals(this.codesetid)){
	                    if ("2".equals(map.get(identifier).toString())) {
	                    	child.setAttribute("icon","/images/book1.gif");
	                    } else {
	                    	child.setAttribute("icon","/images/book.gif");
	                    }
                    } else {
                    	child.setAttribute("icon","/images/book.gif");
                    }
                    root.addContent(child);
        		}
            
            	
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      this.r5100 = SafeCode.encode(PubFunc.encrypt(this.r5100));
      return xmls.toString();        
    }
	
	private String getPath(org.dom4j.Element el) {
		String path = "/mo:" + el.getName();
		if (!el.isRootElement()) {
			org.dom4j.Element parent = el.getParent();
			while (true) {
				path = "/mo:" + parent.getName() + path;
				if (parent.isRootElement()) {
					break;
				} else {
					parent = parent.getParent();
				}
			}
		}
		return path;
	}
	
	public boolean hasChild(String codesetid,String codeitemid,Connection conn) throws SQLException{
		boolean hasChilds = false;
		 String sql = "select '1' from codeitem where codesetid=? and parentid =? and parentid<>codeitemid";
		 ResultSet rs = null;
		 try{
		     ArrayList<String> list = new ArrayList<String>();
		     list.add(codesetid);
		     list.add(codeitemid);
			 ContentDAO dao = new ContentDAO(conn);
			 rs = dao.search(sql, list);
			 if(rs.next())
				 hasChilds = true;
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if(rs!=null){
				 rs.close();
			 }
			 
		 }
		 return hasChilds;
	}
	
    public String outCodeItemTree1()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          CreateCodeXml createCodebo = new CreateCodeXml(codesetid,"");
          Integer  leaf_only = createCodebo.getSelectFlag(dao);
          rset = dao.search(strsql.toString(), valueList);
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            //theaction=this.action+"&a_code="+rset.getString("codeitemid");
            //child.setAttribute("href", theaction);
            //child.setAttribute("target", this.target);
            
            String tempcodeitemid = createCodebo.getTempCodeItemid(codesetid,rset.getString("codeitemid"));
        	//leaf_only：只能选择末级代码项    =1 ：是 =0：否
    		child.setAttribute("selectable","true");//默认可以选
    		if(leaf_only == 1 && !"".equals(tempcodeitemid))//如果是只能选择末级代码项 且 当前节点为非叶子节点
    			child.setAttribute("selectable","false");
    		
            child.setAttribute("xml", this.getcodetree + "?codesetid=" + this.codesetid/*rset.getString("codesetid")*/+"&codeitemid="+rset.getString("codeitemid"));
            if(("54".equals(codesetid)||"55".equals(codesetid)||"55_1".equals(this.codesetid)
            		|| "55_2".equals(codesetid))&&userView!=null&&!userView.isSuper_admin()){
            	String tmpb0110=rset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	int isP = tbo.isUserParent(tmpb0110);
            	if("54".equals(codesetid)&&isP!=1&&isP!=3)
            		continue;
            	
            	if(isP==-1)
            		continue;
            	else if(isP==2)
            		child.setAttribute("icon","/images/book1.gif");//上级图片 待定
            	else
            		child.setAttribute("icon","/images/book.gif");
            }else{
            	child.setAttribute("icon","/images/book.gif");
            }
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
           
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
    public String getCodeitemid() {
        return codeitemid;
    }
    public void setCodeitemid(String codeitemid) {
        this.codeitemid = codeitemid;
    }
    public String getCodesetid() {
        return codesetid;
    }
    public void setCodesetid(String codesetid) {
        this.codesetid = codesetid;
    }

	public void setR5100(String r5100) {
		this.r5100 = r5100;
	}
}
