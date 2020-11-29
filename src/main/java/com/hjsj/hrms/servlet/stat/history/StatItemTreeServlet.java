  /*
 * Created on 2005-6-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.stat.history;

  import com.hjsj.hrms.utils.PubFunc;
  import com.hjsj.hrms.valueobject.database.ExecuteSQL;
  import com.hjsj.hrms.valueobject.tree.TreeItemView;
  import com.hrms.frame.codec.SafeCode;
  import com.hrms.struts.constant.WebConstant;
  import com.hrms.struts.valueobject.UserView;
  import org.apache.commons.beanutils.DynaBean;

  import javax.servlet.ServletException;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.net.URLEncoder;
  import java.util.ArrayList;
  import java.util.List;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatItemTreeServlet extends HttpServlet {
	   /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
             throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
    	String tablename=req.getParameter("tablename");
    	String cate = req.getParameter("cate");
    	String parentid=req.getParameter("parentid");
    	String target=req.getParameter("target");
    	String infokind=req.getParameter("infokind");
    	String querycond=req.getParameter("querycond");
    	String type=req.getParameter("type");
		sbXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<TreeNode>\n");
		try {
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			sbXml.append(loadStatItemNodes(cate,tablename, parentid,target,querycond,infokind,userview,type));
		} catch (Exception e) {
			System.out.println(e);
		}	
		sbXml.append("</TreeNode>\n");
		//System.out.println("sbXml " + sbXml.toString());
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
    }
   private String loadStatItemNodes(String cate,String tablename,String parentid,String target,String querycond,String infokind,UserView userview,String rtype) throws Exception
   {
   	StringBuffer sbXml=new StringBuffer();
   	StringBuffer strsql=new StringBuffer();
   	//System.out.println(tablename);
    String zpid=userview.getResourceString(3);
   	if("sname".equals(tablename))
    	strsql.append("select * from hr_hisdata_sname where infokind=" + infokind +"  order by snorder");
   	else if(cate!=null){
   		cate = SafeCode.decode(cate);
   		strsql.append("select * from hr_hisdata_sname where infokind=" + infokind +" and categories='"+cate+"' order by snorder");
   	}else
   		strsql.append("select * from hr_hisdata_slegend  where id=" + parentid + " order by norder");
    //System.out.println(strsql.toString());
   	List rs=ExecuteSQL.executeMyQuery(strsql.toString());
   	if(!rs.isEmpty())
   	{
   		String id;
   		String name;
   		ArrayList groups = new ArrayList();//存放分组
   		for(int i=0;i<rs.size();i++)
   		{
   		 	 //System.out.println(tablename);
		     TreeItemView treeitem=new TreeItemView();
		     DynaBean rec=(DynaBean)rs.get(i);

	         if("sname".equals(tablename))
	         {
	        	id=rec.get("id")!=null?rec.get("id").toString():"";
	        	
	        	String categories = rec.get("categories")!=null?rec.get("categories").toString():"";
	        	categories= "null".equals(categories)?"":categories;
	        	if(categories.length()>0){
		        		if(!groups.contains(categories)){
		        			groups.add(categories);
		        			String encodeCategories = SafeCode.encode(categories);
		        			encodeCategories = URLEncoder.encode(encodeCategories);
		        			categories =/* new String(*/categories.replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;");//.getBytes("gb2312"),"ISO-8859-1");
		        			treeitem.setName(categories);
						    treeitem.setText(categories);
					        treeitem.setTitle(categories);
					        treeitem.setAction("");
				         	treeitem.setXml("/com/workbench/stat/history/statitemtree?cate="+encodeCategories+"&amp;infokind="+infokind+"&amp;querycond=" + querycond+"&amp;target=mmil_body");
						    treeitem.setIcon("/images/open.png");
						    sbXml.append(treeitem.toChildNodeJS() + "\n");
		        		}
	        	}else{
				    name=rec.get("name")!=null?/*new String(*/rec.get("name").toString().replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;"):"null";//.getBytes("gb2312"),"ISO-8859-1"):"null";
				    //name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"null";
					    treeitem.setName(id);
					    treeitem.setText(name);
				        treeitem.setTitle(name);  
				        String type=rec.get("type").toString();
				        if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
				        {
				        	
				        	if("1".equals(type))
				         	    treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_chart=chart&amp;statid=" + id+"&amp;type="+type);
				         	else
				         		treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_doubledata=data&amp;statid=" + id+"&amp;type="+type);
				        }else
				        {
				        	//String type=rec.get("type").toString();
				        	if("1".equals(type))
				         	    treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_chart=chart&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;type="+type);
				         	else
				         		treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_doubledata=data&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;type="+type);
				        }
				        if("1".equals(type))
			         	treeitem.setXml("/com/workbench/stat/history/statitemtree?tablename=slegend&amp;parentid=" + id + "&amp;target=" + target+ "&amp;querycond=" + querycond+"&amp;type="+type);
			         	
					    treeitem.setTarget(target);
					    treeitem.setIcon("/images/groups.gif");
					    sbXml.append(treeitem.toChildNodeJS() + "\n");
	        	}
	         }else if(cate!=null){
	        	 id=rec.get("id")!=null?rec.get("id").toString():"";
	        	 name=rec.get("name")!=null?/*new String(*/rec.get("name").toString().replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;"):"null";//.getBytes("gb2312"),"ISO-8859-1"):"null";
				    //name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"null";
					    treeitem.setName(id);
					    treeitem.setText(name);
				        treeitem.setTitle(name);
				        String type=rec.get("type").toString();
				        if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
				        {
				        	
				        	if("1".equals(type))
				         	    treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_chart=chart&amp;statid=" + id+"&amp;type="+type);
				         	else
				         		treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_doubledata=data&amp;statid=" + id+"&amp;type="+type);
				        }else
				        {
				        	if("1".equals(type))
				         	    treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_chart=chart&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;type="+type);
				         	else
				         		treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_doubledata=data&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;type="+type);
				        }
				        if("1".equals(type))
			         	treeitem.setXml("/com/workbench/stat/history/statitemtree?tablename=slegend&amp;parentid=" + id + "&amp;target=" + target+ "&amp;querycond=" + querycond+"&amp;type="+type);
			         	
					    treeitem.setTarget(target);
					    treeitem.setIcon("/images/groups.gif");
					    sbXml.append(treeitem.toChildNodeJS() + "\n");
				  
	         }else
	         {
	         	 id=parentid + (rec.get("norder")!=null?rec.get("norder").toString():"");
			     name=rec.get("legend")!=null&& rec.get("legend").toString().length()>0?/*new String(*/rec.get("legend").toString().replaceAll("&",	"&amp;"):"null";//.getBytes("GBK"),"ISO-8859-1"):"null";
			     treeitem.setName(id);
			     treeitem.setText(name);
		         treeitem.setTitle(name);  			     
		         treeitem.setTarget(target);
		         String lexpr=rec.get("lexpr")!=null?rec.get("lexpr").toString():"";
		         String factor=rec.get("factor")!=null?rec.get("factor").toString():"";
                 lexpr=PubFunc.toGet(lexpr);
    	         /*factor=PubFunc.toGet(factor);   
    	         factor=new String(factor.toString().getBytes("GBK"),"ISO-8859-1");*/
                 factor=SafeCode.encode(factor);//解决汉字问题
                 lexpr = URLEncoder.encode(lexpr);
                 factor = URLEncoder.encode(factor);
    	         String norder=rec.get("norder")!=null?rec.get("norder").toString():"";    	         
    	         String history=rec.get("flag")!=null?rec.get("flag").toString():"";
    	         if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
			     {
    	        	 treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_data=data&amp;statid=" + parentid +"&amp;norder="+norder+"&amp;flag=1&amp;strlexpr=" + lexpr + "&amp;strfactor=" + factor+"&amp;history="+history+"&amp;showflag=0&amp;type="+rtype);
			     }else
		             treeitem.setSimpleAction("/general/static/commonstatic/history/statshow.do?b_data=data&amp;statid=" + parentid +"&amp;norder="+norder+"&amp;flag=1&amp;strlexpr=" + lexpr + "&amp;strfactor=" + factor + "&amp;querycond=" + querycond+"&amp;history="+history+"&amp;showflag=0&amp;type="+rtype);
		         treeitem.setIcon("/images/prop_ps.gif");	
		         sbXml.append(treeitem.toChildNodeJS() + "\n");
	         }                     
	         // System.out.println(treeitem.toChildNodeJS());
	       
		}
   	}  
   	return sbXml.toString();
   }
	  /* 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
}
