package com.hjsj.hrms.servlet.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.templateanalyse.ParseHtml;
import com.hjsj.hrms.module.utils.asposeword.AsposeReadWordUtil;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

public class OutputTemplateDataServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public OutputTemplateDataServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		  response.setContentType("APPLICATION/OCTET-STREAM");   
		  response.setHeader("Content-Disposition",   "attachment;   filename=\""  
		    		+   new String("fasdf".getBytes("gb2312"),"iso8859-1") +   "\"");
         */		
		
		String questionid=request.getParameter("questionid");
		String current_id=request.getParameter("current_id");
		LazyDynaBean paramBean=new LazyDynaBean();
		paramBean.set("questionid", questionid);
		paramBean.set("current_id", current_id);
		String tabid=request.getParameter("tabid");
		String templatefile=request.getParameter("templatefile");
		templatefile=PubFunc.decrypt(templatefile);
		String pre=request.getParameter("pre");
		String a0100=request.getParameter("a0100");
		String isHtml=request.getParameter("isHtml");
		String fileName=request.getParameter("fileName");//模版名称_用户名
		//liuyz 导出单人模版和多人模版
		String object_id=request.getParameter("object_id");
		if(a0100==null|| "".equals(a0100)||a0100.trim().length()==0|| "undefined".equals(a0100))
		{
			 object_id = PubFunc.decrypt(object_id);
			 int i = object_id.indexOf("`");
             if (i>0){
            	 pre=object_id.substring(0,i);
                 a0100=object_id.substring(i+1);
             }
		}
		String filename=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + templatefile;
		HttpSession session = request.getSession();
		if (session != null) {
			ServletUtilities.registerPhotoForDeletion(templatefile, session);
        }
		int index=0;
		if(templatefile.contains("."))
		{
			index=templatefile.indexOf(".");
		}
		String last=templatefile.substring(index, templatefile.length());
        UserView userView=(UserView)request.getSession().getAttribute(WebConstant.userView);
		String name=fileName+last;//liuyz 下载文件的名称为模版名称_登录用户名+后缀。
				
		String nid="";
		String sp_batch=(String)request.getParameter("sp_batch");
		if(sp_batch==null|| "".equals(sp_batch))
			sp_batch="0";//单个任务审批	
		String taskid = request.getParameter("taskid");
		taskid = PubFunc.decrypt(taskid);//liuyz 导出单人模版和多人模版
		String ins_id=(String)request.getParameter("ins_id");
		String batch_task=(String)request.getParameter("batch_task");
		String filetype=(String)request.getParameter("filetype");//liuyz 导出单人模版和多人模版
		ArrayList inslist=null;
		
		Connection conn=null;
		
		try{
			conn=AdminDb.getConnection();

			if("1".equals(sp_batch))
			{
				inslist=getInsList(batch_task,conn);
				if(batch_task.startsWith(","))
					batch_task = batch_task.substring(1);
				if(batch_task.endsWith(","))
					batch_task = batch_task.substring(0,batch_task.length()-1);
				taskid = batch_task;
			}
			else
			{
				inslist=new ArrayList();
				inslist.add(ins_id);
			}			
			if(userView==null)
				return;
			//sutemplet_39,如果实例号为０，则业务和用户名有关
			String tablename="templet_" + tabid;
			if("0".equals(ins_id))
			  tablename=userView.getUserName() + tablename;
			ParseHtml parsehtml=new ParseHtml(filename,userView,tabid,taskid,inslist,sp_batch,conn);
		    parsehtml.setSrc_a0100(a0100);
		    parsehtml.setSrc_per(pre);
		    TemplateTableBo tableBo=new TemplateTableBo(conn,Integer.parseInt(tabid),userView);
		    parsehtml.setInfor_type(String.valueOf(tableBo.getInfor_type()));
		    /**按个是输出**/
	    	//liuyz 导出单人模版和多人模版
	    	if("0".equals(filetype))
	    	{
	    		parsehtml.isSubmitflagSave(tablename);//=1（选择），提交数据时，仅把选择中的记录提交至档案库中。
	    	}
			if("false".equalsIgnoreCase(isHtml)){
				InputStream in = null;
				OutputStream out = null;
				try{
					//获取文件中的标签
					AsposeReadWordUtil readAspo=new AsposeReadWordUtil(filename);
					ArrayList worBeanList = readAspo.getWordBean();
					//解析标签，保存返回结果
					for(int i=0;i<worBeanList.size();i++)
					{
						LazyDynaBean bean=(LazyDynaBean) worBeanList.get(i);
						Boolean isTable="subSet".equalsIgnoreCase(String.valueOf(bean.get("hz")));//判断是不是表格标签
						Boolean isPhoto="photo".equalsIgnoreCase(String.valueOf(bean.get("photo")));//判断是不是照片
						String datastr="";
						if(isTable)
						{
							String hzVale=(String) bean.get("hzValue");
							Integer rowNum=(Integer) bean.get("rowNum");//表格需要返回的数据行数
							ArrayList list=parsehtml.executeTemplateDocumentSubList(hzVale, tablename, object_id, paramBean, rowNum,bean);
							bean.set("subSetList", list);
						}
						else if(isPhoto)
						{
							String photoUrl=parsehtml.executeTemplatePhoto(String.valueOf(bean.get("hz")),tablename, object_id, paramBean);
							bean.set("hzValue", photoUrl);
						}
						else
						{
							datastr=parsehtml.executeTemplateDocument(String.valueOf(bean.get("hz")),tablename,object_id,paramBean);
					    	datastr=datastr.replaceAll("wlhxryhrp"," ");
							datastr=datastr.replaceAll("xrywlh888","\r\n");
							bean.set("hzValue", datastr);
						}
					}
					String wordUrl = readAspo.getWordUrl(worBeanList);//获取保存的文件路径
			         response.setHeader("content-disposition", "attachment;filename="+URLEncoder.encode(name, "UTF-8"));
			         in = new FileInputStream(wordUrl);
			         int len = 0;
			         byte[] buffer = new byte[1024];
			         out = response.getOutputStream();
			         while ((len = in.read(buffer)) > 0) {
			             out.write(buffer,0,len);
			         }

				}
				catch(Exception e)
				{
					e.printStackTrace();
					response.setContentType("application/msword;charset=UTF-8");
					response.setHeader("Content-disposition", "attachment; filename="+java.net.URLEncoder.encode(name,"UTF-8"));//加上这段代码将文件先下载下来，然后交给系统进行默认打开，避免了浏览器不认文件而打不开的现象，liuzy 20150715
				    response.setCharacterEncoding("GBK");
					PrintWriter out2 = response.getWriter();
					out2.println(e.getMessage());
					out2.flush();
					out2.close();
				}finally{
					try{
						if(in!=null){
							in.close();
						}
						if(out!=null){
							out.close();
						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else{
				try{
					response.setContentType("application/msword;charset=UTF-8");
					response.setCharacterEncoding("GBK");
					response.setHeader("Content-disposition", "attachment; filename="+java.net.URLEncoder.encode(name,"UTF-8"));//加上这段代码将文件先下载下来，然后交给系统进行默认打开，避免了浏览器不认文件而打不开的现象，liuzy 20150715
		    	   
			    	Document doc=parsehtml.getTemplateDocument();//格式化HTML的body部分获得XML的body部分
			    	String headstr=parsehtml.getTemplateHeadDataValue();	    	
			    	if(tablename.equalsIgnoreCase("templet_"+tabid))
			    	{
			    		object_id+="`"+ins_id;
			    	}
			    	parsehtml.executeTemplateDocument(doc,tablename,object_id,paramBean);
			    	String datastr=parsehtml.outTemplateDataDocument(doc);
			    	//System.out.println(datastr);
				   	if(datastr.indexOf("</head>")!=-1)
			    	    datastr=headstr + datastr.substring(datastr.indexOf("</head>") + "</head>".length());
			    	if(datastr.indexOf("</head>".toUpperCase())!=-1)
			    		datastr=headstr + datastr.substring(datastr.indexOf("</head>".toUpperCase()) + "</head>".toUpperCase().length());
					PrintWriter out = response.getWriter();
					datastr=datastr.replaceAll("wlhxryhrp","&nbsp;");
					datastr=datastr.replaceAll("xrywlh888","<br>");
					//System.out.println(datastr);
					
					out.println(datastr);
					out.flush();
					out.close();
					}
					catch(Exception e){
						response.setContentType("application/msword;charset=UTF-8");
					    response.setCharacterEncoding("GBK");
						PrintWriter out = response.getWriter();
						if(parsehtml.getError_fieldname()!=null&&parsehtml.getError_fieldname().length()>0)
						{
							out.println("文件模规则可能在\""+parsehtml.getError_fieldname()+"\"附近有错误!");
						}else
						{
							out.println(ResourceFactory.getProperty("general.template.operation.outdoc"));
						}
						
						out.flush();
						out.close();
					}
			}
    	    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try{
				if(conn!=null)
					conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		

		
	}

	private ArrayList getInsList(String batch_task,Connection conn)throws GeneralException
	{
		ArrayList inslist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ins_id from t_wf_task where task_id in (");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
				inslist.add(rset.getString("ins_id"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return inslist;
	}	
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
