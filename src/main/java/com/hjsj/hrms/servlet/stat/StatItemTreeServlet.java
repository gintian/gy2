  /*
 * Created on 2005-6-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.stat;

  import com.hjsj.hrms.interfaces.sys.IResourceConstant;
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
  import java.io.UnsupportedEncodingException;
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
    	String crossshow=req.getParameter("crossshow");
    	String categories=req.getParameter("categories");
		sbXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
		try {
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			if("3".equals(crossshow)&&categories!=null&&!"".equals(categories))				
				sbXml.append(loadStatisticsNodes(userview, crossshow, categories, infokind, target, querycond));
			else
				sbXml.append(loadStatItemNodes(cate,tablename, parentid,target,querycond,infokind,crossshow,userview));
		} catch (Exception e) {
			System.out.println(e);
		}	
		sbXml.append("</TreeNode>\n");
		//System.out.println("sbXml " + sbXml.toString());
		resp.setContentType("text/xml;charset=UTF-8");
		//PubFunc.writeDate(sbXml.toString());
		resp.getWriter().println(sbXml.toString());
    }
    
    /**
     * 导出指定多维分类
     * @author liuy 
     * @time 2014-12-1
     * @param userview
     * @param crossshow
     * @param categories
     * @param infokind
     * @param target
     * @param querycond
     * @return
     */
	private String loadStatisticsNodes(UserView userview,String crossshow,String categories,String infokind,String target,String querycond){
		StringBuffer sbXml=new StringBuffer();
		String sql = "select * from sname where " + infokindCond(infokind) +" and categories='"+categories+"' and type in (3) order by snorder";
		List rs=ExecuteSQL.executeMyQuery(sql);
		if(!rs.isEmpty()){
			String id;
			String infoid;
			String name;
			ArrayList groups = new ArrayList();//存放分组
			for(int i=0;i<rs.size();i++){
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				id=rec.get("id")!=null?rec.get("id").toString():"";
	        	infoid=rec.get("infokind")!=null?rec.get("infokind").toString():"";
	        	name=rec.get("name")!=null?rec.get("name").toString().replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;"):"null";
			    name=name.replace("~","～");//半角~无法显示替换成全角~
			    if((userview.isHaveResource(IResourceConstant.STATICS,id)) || "su".equalsIgnoreCase(userview.getUserName()))
   	            {
				    treeitem.setName(id);
				    treeitem.setText(name);
			        treeitem.setTitle(name);  
			        String url = "/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid;
			        try {
						url = URLEncoder.encode(url, "GBK");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
			        treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url="+url);
			        //treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid);
		         	treeitem.setXml("/com/workbench/stat/statitemtree?tablename=slegend&amp;parentid=" + id + "&amp;target=" + target+ "&amp;querycond=" + querycond+"&amp;crossshow="+crossshow);
				    treeitem.setTarget(target);
				    treeitem.setIcon("/images/groups.gif");
				    sbXml.append(treeitem.toChildNodeJS() + "\n");
			    }
			}
		}
		return sbXml.toString();
	}
    
   private String loadStatItemNodes(String cate,String tablename,String parentid,String target,String querycond,String infokind,String crossshow,UserView userview) throws Exception
   {
   	StringBuffer sbXml=new StringBuffer();
   	StringBuffer strsql=new StringBuffer();
   	//System.out.println(tablename);
    String zpid=userview.getResourceString(3);
    
   	if("sname".equals(tablename))
   		if("3".equals(crossshow)) {
   			strsql.append("select * from sname where " + infokindCond(infokind) +"  and type in (3) order by snorder");			
		}else{
			strsql.append("select * from sname where " + infokindCond(infokind) +"  and type in (1,2,3) order by snorder");
		}
   	else if(cate!=null){
   		cate = SafeCode.decode(cate);
   		cate=cate.replace("'", "''");
   		if("3".equals(crossshow)) {
   			strsql.append("select * from sname where " + infokindCond(infokind) +" and categories='"+cate+"' and type in (3) order by snorder");
		}else{
			strsql.append("select * from sname where " + infokindCond(infokind) +" and categories='"+cate+"' and type in (1,2,3) order by snorder");
		}
   	}else
   		strsql.append("select * from slegend  where id=" + parentid + " order by norder");
    //System.out.println(strsql.toString());
   	List rs=ExecuteSQL.executeMyQuery(strsql.toString());
   	if(!rs.isEmpty())
   	{
   		String id;
   		String infoid;
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
	        	infoid=rec.get("infokind")!=null?rec.get("infokind").toString():"";
	        	String categories = rec.get("categories")!=null?rec.get("categories").toString():"";
	        	categories= "null".equals(categories)?"":categories;
	        	if(categories.length()>0){
	        		if((userview.isHaveResource(IResourceConstant.STATICS,id)) || "su".equalsIgnoreCase(userview.getUserName()))
	   	            {
		        		if(!groups.contains(categories)){
		        			groups.add(categories);
		        			String encodeCategories = SafeCode.encode(categories);
		        			categories = categories.replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;");
		        			treeitem.setName(categories);
						    treeitem.setText(categories);
					        treeitem.setTitle(categories);
					        treeitem.setAction("");
				         	treeitem.setXml("/com/workbench/stat/statitemtree?encryptParam="+PubFunc.encrypt("cate="+encodeCategories)+"&amp;infokind="+infokind+"&amp;querycond=" + querycond+"&amp;crossshow="+crossshow+"&amp;target=mil_body");
						    treeitem.setIcon("/images/open.png");
						    sbXml.append(treeitem.toChildNodeJS() + "\n");
		        		}
	   	            }
	        	}else{
				    name=rec.get("name")!=null?rec.get("name").toString().replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;"):"null";
				    //name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"null";
				    if(userview.isHaveResource(IResourceConstant.STATICS,id))
	   	            {
					    treeitem.setName(id);
					    treeitem.setText(name);
				        treeitem.setTitle(name);  
				        if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
				        {
				        	if("1".equals(rec.get("type").toString()))
				         	    treeitem.setSimpleAction("statshow.do?b_chart=chart&amp;statid=" + id+"&amp;infokind="+infoid);
				         	else if("2".equals(rec.get("type").toString()))
				         		treeitem.setSimpleAction("statshow.do?b_doubledata=data&amp;statid=" + id+"&amp;vtotal=0&amp;htotal=0&amp;infokind="+infoid);
				         	else if("3".equals(rec.get("type").toString())){
				         		//二维交叉表
				         		String url = "/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1";
				         		url = URLEncoder.encode(url, "GBK");
				         		treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url="+url);//隐藏自定义区域的标识参数hideFlag
				         		//treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1");//隐藏自定义区域的标识参数hideFlag
				         	}
				        }else
				        {
				        	if("1".equals(rec.get("type").toString()))
				         	    treeitem.setSimpleAction("statshow.do?b_chart=chart&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;infokind="+infoid);
				         	else if("2".equals(rec.get("type").toString()))
				         		treeitem.setSimpleAction("statshow.do?b_doubledata=data&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;vtotal=0&amp;htotal=0&amp;infokind="+infoid);
				        	else if("3".equals(rec.get("type").toString())){
				        		String url = "/general/deci/statics/crosstab.do?b_show=link&amp;statid=" + id+"&amp;infokind="+infoid+"`hideFlag=1";
				        		url = URLEncoder.encode(url, "GBK");
				        		treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url="+url);
				        		//treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link&amp;statid=" + id+"&amp;infokind="+infoid+"`hideFlag=1");
				        	}
				        }
				        if("1".equals(rec.get("type").toString()))
				        	treeitem.setXml("/com/workbench/stat/statitemtree?tablename=slegend&amp;parentid=" + id + "&amp;target=" + target+ "&amp;querycond=" + querycond+"&amp;crossshow="+crossshow);
			         	
					    treeitem.setTarget(target);
					    treeitem.setIcon("/images/groups.gif");
					    sbXml.append(treeitem.toChildNodeJS() + "\n");
				    }
	        	}
	         }else if(cate!=null){
	        	 id=rec.get("id")!=null?rec.get("id").toString():"";
	        	 infoid=rec.get("infokind")!=null?rec.get("infokind").toString():"";
	        	 name=rec.get("name")!=null?rec.get("name").toString().replaceAll("<", "").replaceAll(">", "").replaceAll("&",	"&amp;"):"null";
				    //name=rec.get("name")!=null?rec.get("name").toString().replaceAll("&",	"&amp;"):"null";
				    if((userview.isHaveResource(IResourceConstant.STATICS,id)) || "su".equalsIgnoreCase(userview.getUserName()))
	   	            {
					    treeitem.setName(id);
					    treeitem.setText(name);
				        treeitem.setTitle(name);  
				        if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
				        {
				        	if("1".equals(rec.get("type").toString()))
				         	    treeitem.setSimpleAction("statshow.do?b_chart=chart&amp;statid=" + id+"&amp;infokind="+infoid);
				         	else if("2".equals(rec.get("type").toString()))
				         		treeitem.setSimpleAction("statshow.do?b_doubledata=data&amp;statid=" + id+"&amp;vtotal=0&amp;htotal=0&amp;infokind="+infoid);
				        	else if("3".equals(rec.get("type").toString())){
				        		String url = "/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1";
				        		url = URLEncoder.encode(url,"GBK");
				        		treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url="+url);
				        		//treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1");
				        	}
				        }else
				        {
				        	if("1".equals(rec.get("type").toString()))
				         	    treeitem.setSimpleAction("statshow.do?b_chart=chart&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;infokind="+infoid);
				         	else if("2".equals(rec.get("type").toString()))
				         		treeitem.setSimpleAction("statshow.do?b_doubledata=data&amp;statid=" + id + "&amp;querycond=" + querycond+"&amp;vtotal=0&amp;htotal=0&amp;infokind="+infoid);
				        	else if("3".equals(rec.get("type").toString())){
				        		String url = "/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1";
				        		url = URLEncoder.encode(url,"GBK");
				        		treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url="+url);
				        		//treeitem.setSimpleAction("/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid=" + id+"`infokind="+infoid+"`hideFlag=1");
				        	}
				        }
				        if("1".equals(rec.get("type").toString()))
				        	treeitem.setXml("/com/workbench/stat/statitemtree?tablename=slegend&amp;parentid=" + id + "&amp;target=" + target+ "&amp;querycond=" + querycond+"&amp;crossshow="+crossshow);
			         	
					    treeitem.setTarget(target);
					    treeitem.setIcon("/images/groups.gif");
					    sbXml.append(treeitem.toChildNodeJS() + "\n");
				    }
	         }else
	         {
	         	 id=parentid + (rec.get("norder")!=null?rec.get("norder").toString():"");
	         	 infoid=rec.get("infokind")!=null?rec.get("infokind").toString():"";
			     name=rec.get("legend")!=null&& rec.get("legend").toString().length()>0?rec.get("legend").toString().replaceAll("&",	"&amp;"):"null";
			     name=name.replace("~","～");//半角~无法显示替换成全角~
			     treeitem.setName(id);
			     treeitem.setText(name);
		         treeitem.setTitle(name);  			     
		         treeitem.setTarget(target);
		         String lexpr=rec.get("lexpr")!=null?rec.get("lexpr").toString():"";
		         String factor=rec.get("factor")!=null?rec.get("factor").toString():"";
                 lexpr=PubFunc.toGet(lexpr);
                 lexpr=URLEncoder.encode(lexpr);
    	         //factor=PubFunc.toGet(factor);   
    	         //factor=new String(factor.toString().getBytes("GBK"),"ISO-8859-1");
    	         factor=SafeCode.encode(factor);//解决汉字问题
    	         factor=URLEncoder.encode(factor);
    	         String norder=rec.get("norder")!=null?rec.get("norder").toString():"";
    	         String history=rec.get("flag")!=null?rec.get("flag").toString():"";
    	         if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
			     {
    	        	 treeitem.setSimpleAction("statshow.do?b_data=data&amp;statid=" + parentid +"&amp;norder="+norder+"&amp;flag=1&amp;strlexpr=" + lexpr + "&amp;strfactor=" + factor+"&amp;history="+history+"&amp;showflag=0&amp;infokind="+infoid);
			     }else
		             treeitem.setSimpleAction("statshow.do?b_data=data&amp;statid=" + parentid +"&amp;norder="+norder+"&amp;flag=1&amp;strlexpr=" + lexpr + "&amp;strfactor=" + factor + "&amp;querycond=" + querycond+"&amp;history="+history+"&amp;showflag=0&amp;infokind="+infoid);
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
    
    private String infokindCond(String infokind){
	    if("1,2,3".equals(infokind))
	        return "infokind in ('1', '2', '3')";
	    else
	        return "infokind='"+infokind+"'";
	}
}
