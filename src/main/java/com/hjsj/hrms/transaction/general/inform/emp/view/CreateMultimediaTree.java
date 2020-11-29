package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author FengXiBin
 *@version 4.0
  */
public class CreateMultimediaTree {

	private String a0100;
	
	private String dbname;
	
	private String action;
	
	private String target;
	
	private String tree;
	
	private String flag;
	
	private String kind;
	
	private UserView userview;
	
	private int isvisible=0;
	public CreateMultimediaTree(String a0100,String dbname,String flag)
	{
		this.a0100 = a0100;
		this.dbname = dbname;
		this.flag = flag;
		this.action = "/general/inform/emp/view/opermultimedia.do";
		this.target = "il_body";
		this.tree = "/general/inform/view/multimedia_tree.jsp";
	}
	
	public CreateMultimediaTree(String a0100,String dbname,String flag,String action,String kind)
	{
		this.a0100 = a0100;
		this.dbname = dbname;
		this.flag = flag;
		this.action = action;
		this.target = "il_body";
		this.kind = kind;
		this.tree = "/general/inform/view/multimedia_tree.jsp";
	}
	
//	public CreateMultimediaTree(String a0100,String dbname,String flag,String action,String target)
//	{
//		this.a0100 = a0100;
//		this.dbname = dbname;
//		this.flag = flag;
//		this.action = action;
//		this.target = target;
//		this.tree = "/general/inform/view/multimedia_tree.jsp";
//	}
	
	public CreateMultimediaTree(String a0100,String dbname,String flag,String action,String target,String tree)
	{
		this.a0100 = a0100;
		this.dbname = dbname;
		this.flag = flag;
		this.action = action;
		this.target = target;
		this.tree = tree;
	}
	/**
	 * @param a0100
	 * @param dbname
	 * @param flag
	 * @param action
	 * @param kind
	 * @param userview
	 * @param isvisible lizhenwei add 2008/06/21 控制页面是否显示关闭按钮,只有等于1的时候不显示，=0或者为空的时候都显示
	 */
	public CreateMultimediaTree(String a0100,String dbname,String flag,String action,String kind,UserView userview,String isvisible)
	{
		this.a0100 = a0100;
		this.dbname = dbname;
		this.flag = flag;
		this.action = action;
		this.target = "il_body";
		this.kind = kind;
		this.tree = "/general/inform/view/multimedia_tree.jsp";
		this.userview = userview;
		if(isvisible!=null&& "1".equalsIgnoreCase(isvisible))
			this.isvisible=1;
	}
	
    /**
     * @param a0100
     * @param dbname
     * @param flag
     * @param action
     * @param kind
     * @param tree
     * @param userview
     * @param isvisible
     */
    public CreateMultimediaTree(String a0100,String dbname,String flag,String action,String kind,String tree ,
            UserView userview,String isvisible)
    {
        this.a0100 = a0100;
        this.dbname = dbname;
        this.flag = flag;
        this.action = action;
        this.target = "il_body";
        this.kind = kind;
        this.tree = tree;
        this.userview = userview;
        if(isvisible!=null&& "1".equalsIgnoreCase(isvisible))
            this.isvisible=1;
    }
	
	 /**
     * 创建多媒体树
     * @return xmls
     * @throws Exception
     */
	   public String getMultimediaTree()throws GeneralException
	   {
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
	        String theaction = "";
	        
	        try{
	        	ContentDAO dao = new ContentDAO(conn);
	        	if(!(flag==null || "".equals(flag)))
	        	{
//	        		sqlstr.append("select id,flag,sortname from mediasort where dbflag=1 and flag='"+flag+"'" );
	        	}else{
	        		if("6".equals(this.kind))// 人员
	        		{
	        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=1 order by id");
	        		}else if("0".equals(this.kind))// 职位
	        		{
	        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=3 order by id");
	        		}else if("9".equals(this.kind)){//基准岗位  gdd
	        			
	        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=4 order by id");
	        		}else  // 单位
	        		{
	        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=2 order by id");
	        		}
	        		
	        	}
	        	if(!(sqlstr==null || "".equals(sqlstr.toString())))
	        	{
		            rset = dao.search(sqlstr.toString());
		            if("0".equals(this.kind) || "9".equals(this.kind)){
			            if(this.userview.hasTheMediaSet("K")){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类
			            	String id="9999";
		            		Element child = new Element("TreeNode");
				            this.flag = "K";
				            String sortname = ResourceFactory.getProperty("lable.pos.e01a1.manual");
				            child.setAttribute("id",id);
				            child.setAttribute("text", sortname);
				            child.setAttribute("title",this.flag);	         
				            theaction = this.action+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible;
				            child.setAttribute("href", theaction);
				            child.setAttribute("target",this.target);
				           // child.setAttribute("xml",this.tree+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible);
				            child.setAttribute("icon","/images/open.png");	          
				            root.addContent(child);
			            }
		            }
		            while(rset.next()){
		            	String checkid = rset.getString("flag");	
		            	if(this.userview.isSuper_admin())
		            	{
		            		String id=rset.getString("id");
		            		Element child = new Element("TreeNode");
				            this.flag = rset.getString("flag");
				            String sortname = rset.getString("sortname");
				            child.setAttribute("id",id);
				            child.setAttribute("text", sortname);
				            //child.setAttribute("title",this.flag);	
				            child.setAttribute("title",sortname);	
				            theaction = this.action+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible;
				            child.setAttribute("href", theaction);
				            child.setAttribute("target",this.target);
				            //child.setAttribute("xml",this.tree+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible);
				            child.setAttribute("icon","/images/open.png");	          
				            root.addContent(child);
		            	}else
		            	{
//		            		if(this.checkMediaPriv(checkid))
		            		String id=rset.getString("id");		            		
		            		if(this.userview.hasTheMediaSet(checkid))
		            		//if(userview.isHaveResource(IResourceConstant.MEDIA_EMP,checkid))
			            	{
			            		
			            		Element child = new Element("TreeNode");
					            this.flag = rset.getString("flag");
					            String sortname = rset.getString("sortname");
					            child.setAttribute("id",id);
					            child.setAttribute("text", sortname);
					            //child.setAttribute("title",this.flag);	
					            child.setAttribute("title",sortname);
					            theaction = this.action+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible;
					            child.setAttribute("href", theaction);
					            child.setAttribute("target",this.target);
					            //child.setAttribute("xml",this.tree+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible);
					            child.setAttribute("icon","/images/open.png");	          
					            root.addContent(child);
			            	}
		            	}		            	
			            
		            }
	        	}
	            

	            XMLOutputter outputter = new XMLOutputter();
	            Format format=Format.getPrettyFormat();
	            format.setEncoding("UTF-8");
	            outputter.setFormat(format);
	            xmls.append(outputter.outputString(myDocument));
	        }catch(Exception ee){
	          ee.printStackTrace();
	        }finally{
	            try{
	                if(rset != null)
	                {
	                	rset.close();
	                }
	                if(stmt != null)
	                {
	                	stmt.close();
	                }
	                if(conn != null)
	                {
	                	conn.close();
	                }
	            }catch(SQLException ee){
	                ee.printStackTrace();
	            } 
	        }
	        return xmls.toString();        
	    }
	   
		 /**
	     * 创建多媒体树
	     * @return xmls
	     * @throws Exception
	     */
		   public String getMediaTree()
		   {
		        StringBuffer xmls = new StringBuffer();
		        StringBuffer sqlstr = new StringBuffer();
		        Connection conn = null;
		        Statement stmt = null; 
		        ResultSet rset = null;
		        Element root = new Element("TreeNode");

		        root.setAttribute("id","$$00");
		        root.setAttribute("text","root");
		        root.setAttribute("title","codeitem");
		        Document myDocument = new Document(root);
		        String theaction = "";
		        
		        try{
		        	conn = AdminDb.getConnection();
		        	if(!(flag==null || "".equals(flag)))
		        	{
//		        		sqlstr.append("select id,flag,sortname from mediasort where dbflag=1 and flag='"+flag+"'" );
		        	}else{
		        		if("1".equals(this.kind))// 人员
		        		{
		        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=1 order by id");
		        		}else if("3".equals(this.kind))// 职位
		        		{
		        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=3 order by id");
		        		}else if("9".equals(this.kind))
		        		{
		        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=4 order by id");  
		        		}else// 单位
		        		{
		        			sqlstr.append("select id,flag,sortname from mediasort where dbflag=2 order by id");
		        		}
		        		
		        	}
		        	if(!(sqlstr==null || "".equals(sqlstr.toString())))
		        	{
		        		ContentDAO dao = new ContentDAO(conn);
			            rset = dao.search(sqlstr.toString());
			            while(rset.next()){
			            	String checkid = rset.getString("flag");	
			            	if(this.userview.isSuper_admin())
			            	{
			            		String id=rset.getString("id");
			            		Element child = new Element("TreeNode");
					            this.flag = rset.getString("flag");
					            String sortname = rset.getString("sortname");
					            child.setAttribute("id",this.flag);
					            child.setAttribute("text", sortname);
					            child.setAttribute("title",this.flag);	         
					            child.setAttribute("href", theaction);
					            child.setAttribute("target",this.target);
					            child.setAttribute("xml",this.tree+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible);
					            child.setAttribute("icon","/images/open.png");	          
					            root.addContent(child);
			            	}else
			            	{
//			            		if(this.checkMediaPriv(checkid))
			            		String id=rset.getString("id");		            		
			            		if(this.userview.hasTheMediaSet(checkid))
			            		//if(userview.isHaveResource(IResourceConstant.MEDIA_EMP,checkid))
				            	{
				            		
				            		Element child = new Element("TreeNode");
						            this.flag = rset.getString("flag");
						            String sortname = rset.getString("sortname");
						            child.setAttribute("id",this.flag);
						            child.setAttribute("text", sortname);
						            child.setAttribute("title",this.flag);	         
						            theaction = this.action+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible;
						            child.setAttribute("href", theaction);
						            child.setAttribute("target",this.target);
						            child.setAttribute("xml",this.tree+"?b_query=link&a0100="+a0100+"&dbname="+dbname+"&multimediaflag="+this.flag+"&kind="+this.kind+"&isvisible="+this.isvisible);
						            child.setAttribute("icon","/images/open.png");	          
						            root.addContent(child);
				            	}
			            	}		            	
				            
			            }
		        	}
		            

		            XMLOutputter outputter = new XMLOutputter();
		            Format format=Format.getPrettyFormat();
		            format.setEncoding("UTF-8");
		            outputter.setFormat(format);
		            xmls.append(outputter.outputString(myDocument));
		        }catch(Exception ee){
		          ee.printStackTrace();
		        }finally{
		            try{
		                if(rset != null)
		                {
		                	rset.close();
		                }
		                if(stmt != null)
		                {
		                	stmt.close();
		                }
		                if(conn != null)
		                {
		                	conn.close();
		                }
		            }catch(SQLException ee){
		                ee.printStackTrace();
		            } 
		        }
		        return xmls.toString();        
		    }
	   /**
	    * 
	    * @param flag
	    * @return
	    * @throws GeneralException
	    */
	   public boolean checkMediaPriv(String flag)throws GeneralException
	   {
		   boolean ret = false;
		   Connection conn = AdminDb.getConnection();
		   Statement stmt = null; 
	       ResultSet rset = null;
		   String mediapriv = "";
			int status =  0 ;
			StringBuffer sb = new StringBuffer();
			sb.append(" select * from t_sys_function_priv where id = '"+this.userview.getUserName().toLowerCase()+"'");		
			try
			{
				ContentDAO dao = new ContentDAO(conn);
	            rset = dao.search(sb.toString());
				while(rset.next())
				{
					mediapriv = rset.getString("mediapriv");
				}
				if(!(mediapriv==null || "".equals(mediapriv)|| ",".equals(mediapriv)))
				{
					String arr[] = mediapriv.split(",");
					for(int i=0;i<arr.length;i++)
					{
						if(arr[i].equalsIgnoreCase(flag))
						{
							ret = true;
							break;
						}
					}
				}
		
			}catch(Exception ee){
	          ee.printStackTrace();
	        }finally{
	            try{
	                if(rset != null)
	                {
	                	rset.close();
	                }
	                if(stmt != null)
	                {
	                	stmt.close();
	                }
	                if(conn != null)
	                {
	                	conn.close();
	                }
	            }catch(SQLException ee){
	                ee.printStackTrace();
	            } 
	        }
		   return ret;
	   }
}
