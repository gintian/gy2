package com.hjsj.hrms.utils.components.codeselector.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 代码搜索功能类
 * @author hjsoftcomcn
 *
 */
public class SearchCodeItems {

	//代码类
	String codesetid = "";
	//搜索关键字
	String codename = "";
	
	UserView userView = null;
	
	Connection conn = null;
	RowSet rowset = null;
	ContentDAO dao = null;
	
	//查询虚拟机构
	boolean showVOrg = false;

	/**
	 * ctrltype
	 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
	 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
	 *         默认值为1
	 *  如果是普通代码类 
	 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
	 */
	String ctrltype = "";
	// 业务模块号
	String nmodule = "";
	
	String nodeid = "";
	
	String parentid = null;
	
	boolean multiple = false;
	
	boolean onlySelectCodeset = true;
	int layerLevel = 0;//只针对部门   层级显示

	boolean hideTip = false;
	/**
	 * 代码查询
	 * @param codesetid 代码类
	 * @param codename  搜索关键字
	 */
	public SearchCodeItems(String codesetid,String nodeid,String codename,String ctrltype,String nmodule,boolean multiple,UserView userView){
		this.codesetid = codesetid.toUpperCase();
		this.codename = codename;
		this.userView = userView;
		this.ctrltype = ctrltype==null || ctrltype.length()<1?"1":ctrltype;
		this.nmodule  = nmodule;
		this.multiple = multiple;
		this.nodeid = nodeid;
	}
	
	
	/**
	 * 执行搜索
	 * @return
	 */
	public ArrayList  executeCodeSearch(){
		
		ArrayList resultList = new ArrayList();
		
		try{
			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);
			if(this.codesetid == null || this.codesetid.trim().length()!=2)
				return resultList;
			
			if("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)){
				
				resultList = orgCodeSearch();
			}else if("@@".equals(this.codesetid)){
				resultList = dbpreCodeSearch();
			}else{
				resultList = normalCodeSearch();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rowset);
		}
		
		return resultList;
	}
	
	/**
	 * 普通代码查询
	 * @return
	 * @throws SQLException
	 */
	private ArrayList normalCodeSearch() throws SQLException{
		ArrayList resultList = new ArrayList();
		StringBuffer str = new StringBuffer(" select codesetid,codeitemid,codeitemdesc,parentid," +
				"(select a.codeitemdesc from codeitem a where a.codeitemid = codetable.parentid and a.codesetid=codetable.codesetid) " +
				"parentdesc from codeitem codetable where codesetid=? and (codeitemdesc like ? or codeitemid like ?) ");
		if(this.onlySelectCodeset)
			str.append(" and not exists (select 1 from codeitem where codesetid=codetable.codesetid and parentid=codetable.codeitemid and parentid<>codeitemid) ");
		
		ArrayList paramList = new ArrayList();
		paramList.add(this.codesetid);
		paramList.add("%"+this.codename+"%");
		paramList.add(this.codename+"%");
		if(!"0".equals(ctrltype)){//过滤无效的，过期的代码
			int codeFlag = getCodeFlag(codesetid);
			if(codeFlag == 1){
		    	String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		    	str.append(" and "+Sql_switcher.dateValue(now)+" between start_date and end_date ");
		    }else
		    	str.append(" and invalid=1 ");
		}
		str.append(" ORDER BY a0000,codeitemid");
		rowset = this.dao.search(str.toString(), paramList);
		while(rowset.next()){
			HashMap hm = new HashMap();
			String itemdesc = "";
			String codesetid = rowset.getString("codesetid");
			String codeitemid = rowset.getString("codeitemid");
			String parentid = rowset.getString("parentid");
			String parentdesc = rowset.getString("parentdesc");
			String codeitemdesc = rowset.getString("codeitemdesc");
			if(!codeitemid.equals(parentid))
				itemdesc = codeitemdesc +"("+parentdesc+")";
			else
				itemdesc = codeitemdesc;
			hm.put("id", codeitemid);
			hm.put("text", itemdesc);
			hm.put("itemdesc", codeitemdesc);
			hm.put("codesetid", codesetid);
			if(!hideTip)
				hm.put("qtip","ID:"+codeitemid);
			hm.put("leaf",Boolean.TRUE);
			if(this.multiple)
				hm.put("checked", false);
			resultList.add(hm);
		}
		
		paramList = null;
		str = null;
		return resultList;
	}
	
	/**
	 * 机构代码类
	 * @return
	 * @throws Exception 
	 */
	private ArrayList orgCodeSearch() throws Exception{
		ArrayList resultList = new ArrayList();
		StringBuffer str = new StringBuffer();
		
		String dataTable = "organization";
		if(showVOrg)
			dataTable = " (select codesetid,codeitemid, parentid,(select a.codeitemdesc from organization a where a.codeitemid = organization.parentid ) parentdesc,codeitemdesc,start_date,end_date from organization " +
					"union all select codesetid,codeitemid, parentid,(select a.codeitemdesc from organization a where a.codeitemid = vorganization.parentid ) parentdesc,codeitemdesc,start_date,end_date from vorganization )";
		
		str.append(" select codesetid,codeitemid,parentid,(select a.codeitemdesc from organization a where a.codeitemid = codetable.parentid ) parentdesc, codeitemdesc from ");
		str.append(dataTable+" codetable");
		str.append(" where (codesetid=? or codesetid=?) and ? between start_date and end_date and (codeitemdesc like ? or codeitemid like ?)");
		/*if(!nodeid.equals("ALL")){
			str.append(" and parentid=? ");
		}*/
		//权限过滤
		if(!"0".equals(ctrltype) && !userView.isSuper_admin()){
			StringBuffer privSql = new StringBuffer();
			
			String privOrg = "";
			if ("1".equals(ctrltype)) {// 管理范围
				privOrg = userView.getManagePrivCodeValue();
				if ("UN".equalsIgnoreCase(userView.getManagePrivCode()) && privOrg.trim().length()==0) {//当用户管理范围值为UN时，需要特殊处理  wangb 20191012
					privOrg = "UN`";
				}
			} else if ("2".equals(ctrltype)) {// 操作单位
				privOrg = userView.getUnit_id();
			} else if (nmodule.length() > 0 && "3".equals(ctrltype)) {// 业务范围
				privOrg = userView.getUnitIdByBusi(nmodule);
			} else {
				throw new Exception("获取代码参数出错：ctrytype 或  nmodule ");
			}
			
			if(privOrg.indexOf("UN`")>-1){
				privSql.append(" or 1=1 ");
				privOrg="";
			}
			privOrg = privOrg.replaceAll("UN", "");
			privOrg = privOrg.replaceAll("UM", "");
			String[] orgids = privOrg.split("`");
			for(int i=0;i<orgids.length;i++){
				String orgid = orgids[i];
				if(orgid.trim().length()<1)
					continue;
				privSql.append(" or codeitemid like '"+orgid+"%' ");
			}
			
		    str.append(" and ( 1=2 "+privSql+")");
		    
		    if(parentid!=null && parentid.length()>0)
				str.append(" and parentid like '"+parentid+"%' ");
		}else {//如果模糊时有传parentid，则模糊的项应根据parentid查 bug38117
			if(!"ALL".equals(nodeid)){
				if(parentid!=null && parentid.length()>0)
					str.append(" and parentid like '"+parentid+"%' ");
			}
		}
		
		str.append(" ORDER BY codeitemid");
		ArrayList paramList = new ArrayList();
		paramList.add(this.codesetid);
		if("UM".equalsIgnoreCase(this.codesetid) && !this.onlySelectCodeset)
			paramList.add("UN");
		else
			paramList.add(this.codesetid);
		Date date = new Date();
		paramList.add(new java.sql.Date(date.getYear(),date.getMonth(),date.getDate()));
		paramList.add("%"+this.codename+"%");
		paramList.add(this.codename+"%");
		/*if(!nodeid.equals("ALL")){
			paramList.add(this.nodeid);
		}*/
		this.rowset = dao.search(str.toString(), paramList);
		while(rowset.next()){
			HashMap hm = new HashMap();
			String itemdesc = "";
			String codesetid = rowset.getString("codesetid");
			String codeitemid = rowset.getString("codeitemid");
			String parentid = rowset.getString("parentid");
			String parentdesc = rowset.getString("parentdesc");
			String codeitemdesc = rowset.getString("codeitemdesc");
			if(!codeitemid.equals(parentid))
				itemdesc = codeitemdesc +"("+parentdesc+")";
			else
				itemdesc = codeitemdesc;
			hm.put("id", codeitemid);
			hm.put("text", itemdesc);
			if(!hideTip)
				hm.put("qtip","ID:"+codeitemid);
			hm.put("codesetid", codesetid);
			hm.put("itemdesc", codeitemdesc);
			String layerdesc = "";
    		if("UM".equalsIgnoreCase(codesetid)&&this.layerLevel>0){
				CodeItem item=AdminCode.getCode("UM",codeitemid,this.layerLevel);
				if(item!=null){
					layerdesc = item.getCodename();
        		}else{
    	    		layerdesc = AdminCode.getCodeName(codesetid,codeitemid);
    	    	}
			}
    		hm.put("layerdesc", layerdesc);
			if("UN".equalsIgnoreCase(codesetid))
				hm.put("icon", "/images/unit.gif");
			else if("UM".equalsIgnoreCase(codesetid)){
				hm.put("icon", "/images/dept.gif");
			}else if("@K".equalsIgnoreCase(codesetid)){
				hm.put("icon", "/images/pos_l.gif");
			}
			hm.put("leaf",Boolean.TRUE);
			if(this.multiple)
				hm.put("checked", false);
			resultList.add(hm);
		}
		paramList = null;
		str = null;
		return resultList;
	}
	
	/**
	 * 数据库代码查询 “@@”
	 * @return
	 * @throws SQLException
	 */
	private ArrayList dbpreCodeSearch() throws SQLException{
		ArrayList resultList = new ArrayList();
		StringBuffer str = new StringBuffer();
		
		str.append(" select dbname,pre from dbname where dbname like ? ");
		//TODO 权限过滤
		ArrayList paramList = new ArrayList();
		paramList.add("%"+this.codename+"%");
		this.rowset = dao.search(str.toString(), paramList);
		
		while(rowset.next()){
			HashMap hm = new HashMap();
			hm.put("id", rowset.getString("pre"));
			hm.put("text",rowset.getString("dbname"));
			hm.put("codesetid","@@");
			hm.put("leaf",Boolean.TRUE);
			if(!hideTip)
				hm.put("qtip","ID:"+rowset.getString("pre"));
			if(this.multiple)
				hm.put("checked", false);
			resultList.add(hm);
		}
		
		paramList = null;
		str = null;
		return resultList;
	}
	
	private int getCodeFlag(String codesetid){
    	int codeflag=-1;
    	String sql = " select validateflag from codeset where codesetid='"+codesetid+"'";

    	ResultSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(conn);
    		rs = dao.search(sql);
    		if(rs.next())
    			codeflag = rs.getInt("validateflag");
    	}catch(SQLException e){
    	    e.printStackTrace();
    	}finally{
    		try{
    			if(rs != null)
    				rs.close();
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    		    
    	}
    	return codeflag;
    }


	public void setOnlySelectCodeset(boolean onlySelectCodeset) {
		this.onlySelectCodeset = onlySelectCodeset;
	}


	public String getParentid() {
		return parentid;
	}


	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
	public void setLayerLevel(int layerLevel) {
		this.layerLevel = layerLevel;
	}
	public void setHideTip(boolean show){
		this.hideTip = show;
	}
	
}


