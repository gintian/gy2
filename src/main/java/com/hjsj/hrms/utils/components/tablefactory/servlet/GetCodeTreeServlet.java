package com.hjsj.hrms.utils.components.tablefactory.servlet;

import com.hjsj.hrms.interfaces.hire.OrganizationByXml;
import com.hjsj.hrms.interfaces.sys.CreateCodeXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hjsj.hrms.utils.components.codeselector.utils.SearchCodeItems;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author hjsoftcomcn
 * 
 *         output string format:{children: [{id: 'basic-panels',text: 'Basic
 *         Panel',leaf:true,icon:'/images/add_2.gif'}, {id:
 *         'framed-panels',text: 'Framed Panel',leaf: false}]}
 */
public class GetCodeTreeServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//String treeJSON = "";
		String istitle = req.getParameter("istitle");
		String codesetid = req.getParameter("codesetid");
		String parentid = req.getParameter("parentid");
		boolean multiple = Boolean.parseBoolean(req.getParameter("multiple"));
		boolean checkroot = Boolean.parseBoolean(req.getParameter("checkroot"));
		String searchtext = (String) req.getParameter("searchtext");
		UserView userView = (UserView) req.getSession().getAttribute(
				WebConstant.userView);

		if ("true".equals(istitle)) {//查询代码类 描述
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(CreateCodeXml.getCodeSetDesc(codesetid));
			return;
		}

		
		ArrayList treeItems = new ArrayList();
		
		try {
			String nodeid = req.getParameter("node"); // 要展开的节点id（codeitemid）
			
			boolean doChecked = false;
			if("root".equals(nodeid) && checkroot)
				doChecked = true;
			
			if("root".equals(nodeid) && parentid!=null && parentid.length()>0)
				nodeid = parentid;
			String codesource = req.getParameter("codesource");// 代码数据生成类名
			if (codesource.length() > 0) {// 如果设置了代码 数据源类，则走自定义类
				//路径统一，自定义代码生成器必须实现CodeDataFactory接口
				String classpath = "com.hjsj.hrms.utils.components.codeselector.";
				CodeDataFactory codebean = (CodeDataFactory) Class.forName(
						classpath + codesource).newInstance();
				if(searchtext != null && searchtext.length() > 0){
					searchtext = PubFunc.hireKeyWord_filter_reback(searchtext);
					// 前台进行encodeUrl加密，解密
					searchtext = java.net.URLDecoder.decode(searchtext, "UTF-8");
					treeItems = codebean.searchCodeByText(codesetid,searchtext,userView);
				}else{
					treeItems = codebean.createCodeData(codesetid, nodeid,
							userView);
				}
				outPutTree(resp,treeItems);
				return;
			}

			/**
			 * ctrltype
			 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
			 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
			 *         默认值为1
			 *  如果是普通代码类 
			 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
			 */
			String ctrltype = req.getParameter("ctrltype");
			ctrltype = ctrltype==null || ctrltype.length()<1?"1":ctrltype;
			
			String nmodule = req.getParameter("nmodule");// 模块号

			

			if (searchtext != null && searchtext.length() > 0) {

				searchtext = PubFunc.hireKeyWord_filter_reback(searchtext);
				// 前台进行encodeUrl加密，解密
				searchtext = java.net.URLDecoder.decode(searchtext, "UTF-8");
				SearchCodeItems sci = new SearchCodeItems(codesetid,nodeid,
						searchtext,ctrltype,nmodule,multiple,userView);
				treeItems = sci.executeCodeSearch();
				//treeJSON = "{children:" + JSON.toString(treeItems) + "}";
				outPutTree(resp, treeItems);
				return;
			}

			nodeid = nodeid.replaceAll("root", "ALL");
			//if(!nodeid.equals("ALL"))
			//	nodeid = nodeid.substring(2);
			treeItems = getCodeListParams(codesetid, nodeid,
					ctrltype, nmodule, multiple,doChecked,userView);

			if ("ALL".equals(nodeid)) {
				/*
				 * HashMap root = new HashMap(); root.put("id", "root");
				 * root.put("text", "代码"); root.put("expanded", Boolean.TRUE);
				 * root.put("children", treeItems);
				 */
				//treeJSON = "{children:" + JSON.toString(/* root */treeItems)
				//		+ "}";
				outPutTree(resp, treeItems);
			} else {
				//treeJSON = "{children:" + JSON.toString(treeItems)
				//		+ ",expanded:true}";
				outPutTree(resp, treeItems);
			}

			// CodeDataFactory codebean =
			// (CodeDataFactory)Class.forName("com.hjsj.hrms.utils.components.codeselector.CodeSource").newInstance();
			// codebean.createCodeData("", "", null);

			//output(resp,treeJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void outPutTree(HttpServletResponse resp, ArrayList treeItems)
			throws IOException {
		String treeJSON = "{children:"+JSON.toString(treeItems)+"}";
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(treeJSON);
	}

	private ArrayList getCodeListParams(String codesetid,
			String nodeid, String ctrltype, String nmodule,boolean multiple,boolean doChecked,UserView userView) throws Exception {

		
		ArrayList itemList = new ArrayList();
		
		CreateCodeXml codeXml = new CreateCodeXml(codesetid, nodeid, null,userView);
		
		if("0".equals(ctrltype)){//等于0 不控制
			//codeXml 默认是过滤普通代码的有效无效的，这里设置一下过滤
			codeXml.setIsValidCtr("0");
			return codeXml.outCodeJSON(multiple,doChecked);
		}
		
		boolean isOrg = "UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid);
		if(!isOrg){//如果 是普通代码  
			return codeXml.outCodeJSON(multiple,doChecked);
		}
		
		
		
		/*下面的都是机构代码了 */
		
		//超级用户
		if(userView.isSuper_admin())
			return codeXml.outCodeJSON(multiple,doChecked);
		
		//获取权限
		String codesstr = "";
		if ("1".equals(ctrltype)) {// 管理范围
					codesstr = userView.getManagePrivCodeValue();
		} else if ("2".equals(ctrltype)) {// 操作单位
					codesstr = userView.getUnit_id();
		} else if (nmodule.length() > 0 && "3".equals(ctrltype)) {// 业务范围
					codesstr = userView.getUnitIdByBusi(nmodule);
		} else {
				throw new Exception("获取代码参数出错：ctrytype 或  nmodule ");
		}
		
		//如果权限有UN`说明有所有权限
		if(codesstr.indexOf("UN`")!=-1){//如果 不是刚进入  或者 是超级用户 
			return codeXml.outCodeJSON(multiple,doChecked);
		}
		
		//nodeid不等于All时要检查
		if(!"ALL".equals(nodeid)){
			String[] temp=codesstr.replace("UN","").replace("UM", "").replace("@K", "").split("`");
			boolean hasPriv = false;
			for(int i=0;i<temp.length;i++)
			{
				if(nodeid.startsWith(temp[i]))
					hasPriv = true;
			}
			
			if(!hasPriv){
				return itemList;
			}
			
			return codeXml.outCodeJSON(multiple,doChecked);
		}
		
		// 走到这里 就代表是 第一次加载树 并且是机构代码，并且需要权限控制
		
		//如果 权限范围里有 UN`则说明有全部范围，不用继续控制了
		//if(codesstr.indexOf("UN`")!=-1){
		//	return codeXml.outCodeJSON();
		//}
		
		//codesstr = codesstr.replace("UN", "");
		//codesstr = codesstr.replace("UM", "");
		//codesstr = codesstr.replace("@K", "");
		
		if(codesstr.trim().length()<1)
			return null;
		
//		String[] codesArray = codesstr.split("`");
//		String searchCodes = "";
//		for(int i=0;i<codesArray.length;i++){
//			searchCodes+="'"+codesArray[i]+"',";
//		}
//		searchCodes+="'code'";
		
		String searchCodes = "";
		String[] temp=codesstr.split("`");
		HashMap map = new OrganizationByXml().getPrivMange(temp);
		for(int i=0;i<temp.length;i++)
		{
			if(map.get(temp[i].substring(2))==null)
				searchCodes+="'"+temp[i].substring(2)+"',";
		}
		searchCodes+="'code'";
		
		String codefilter = "";
		if("UN".equals(codesetid))
			codefilter+=" and codesetid<>'UM' and codesetid<>'@K' ";
		else if("UM".equals(codesetid))
			codefilter+=" and codesetid<>'@K' ";
		
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid,codeitemdesc,(select count(1) from organization where parentid=A.codeitemid ");
		sql.append(codefilter);
		sql.append(") cnum from organization A where codeitemid in (");
		sql.append(searchCodes);
		sql.append(")");
		sql.append(codefilter);
		
		List codelist = ExecuteSQL.executeMyQuery(sql.toString());
	    for(int k=0;k<codelist.size();k++){
	    	LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
	    	HashMap treeitem = new HashMap();
	    	String setid = ldb.get("codesetid").toString();
	    	treeitem.put("id",ldb.get("codeitemid"));
	    	treeitem.put("text", ldb.get("codeitemdesc"));
	    	treeitem.put("codesetid",setid);
	    	//设置图片
	    	if("UN".equals(setid))
	    		treeitem.put("icon","/images/unit.gif");
			else if("UM".equals(setid))
				treeitem.put("icon","/images/dept.gif");
			else
				treeitem.put("icon","/images/pos_l.gif");
	    	
	    	//是否叶子节点
	    	if(Integer.parseInt(ldb.get("cnum").toString())>0)
	    		treeitem.put("leaf", Boolean.FALSE);
	    	else
	    		treeitem.put("leaf", Boolean.TRUE);
	    	if(multiple)
    			treeitem.put("checked", false);
	    	if(doChecked)
    			treeitem.put("checked", true);
	    	itemList.add(treeitem);
	    }
		return itemList;
	}

}
