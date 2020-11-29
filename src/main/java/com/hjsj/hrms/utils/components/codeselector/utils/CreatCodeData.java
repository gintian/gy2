package com.hjsj.hrms.utils.components.codeselector.utils;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreatCodeData {

	/** 代码类 */
    private String codesetid;

    /** 代码项 */
    private String codeitemid;
	
	
	private String isValidCtr="1"; //是否过滤有效无效。0不控制，1控制 默认控制
	
	private boolean vorg = false;
	
	private static boolean isHavA0000=false;//是否有a0000字段
	
	static{
		RecordVo vo = new RecordVo("codeitem");
		if(vo.hasAttribute("a0000"))
			isHavA0000=true;
	}

	private boolean onlyLeafNode = false;
	
	private int layerLevel = 0;//只针对部门   层级显示
	
	public CreatCodeData(String codesetid,String codeitemid){
		this.codeitemid = PubFunc.getReplaceStr(codeitemid);
		this.codesetid = PubFunc.getReplaceStr(codesetid);
	}
	
	/**
     * 查询下级节点并创建节点对象
     * @param multiple  是否添加多选框
     * @param doChecked 是否选中
     * @param expanded 展开下级节点，
	 * @param isHideTip 
     * @return
     * @throws GeneralException
     */
	public ArrayList outCodeData(boolean multiple,boolean doChecked,String expanded, Boolean isHideTip) throws GeneralException{
		return outCodeData(multiple,doChecked,expanded,isHideTip,false,"");
	}
	
	/**
     * 查询下级节点并创建节点对象
     * @param multiple  是否添加多选框
     * @param doChecked 是否选中
     * @param expanded 展开下级节点，
	 * @param isHideTip 
	 * @param showLevelDept 是否显示多层级等部门
	 * @param checkedcodeids 
     * @return
     * @throws GeneralException
     */
    public ArrayList outCodeData(boolean multiple,boolean doChecked,String expanded, Boolean isHideTip,boolean showLevelDept, String checkedcodeids) throws GeneralException{
    	StringBuffer strsql = new StringBuffer();
    	ResultSet rset = null;
    	Connection conn = null;
	    //xus 18/3/14 显示多层级等部门------END
    	ArrayList  childrens = new ArrayList();
    	try
    	{
    		conn = AdminDb.getConnection();
        	//xus 18/3/14 显示多层级等部门------BEGIN 
    		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
    		String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
    		if(uplevelStr==null||uplevelStr.length()==0)
    	    	uplevelStr="0";
    	    int upLevel = Integer.parseInt(uplevelStr);
    	    strsql.append(getQueryString(conn));
    	    ContentDAO dao = new ContentDAO(conn);
    	    rset = dao.search(strsql.toString());
    	    while (rset.next())
    	    {
	    	    HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetid = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc.trim());
	    		treeitem.put("codesetid", codesetid);
	    		treeitem.put("itemdesc", codeitemdesc.trim());
	    		String layerdesc = this.searchLevelDesc(itemid, codesetid, this.layerLevel);
	    		treeitem.put("layerdesc", layerdesc);
	    		//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,itemid);
	    		if(!"".equals(tempcodeitemid))
	    			treeitem.put("leaf", Boolean.FALSE);
	    		else{
	    			treeitem.put("leaf", Boolean.TRUE);
	    		}
	    		
	    		if(!isHideTip)
	    			treeitem.put("qtip","ID:"+itemid);
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/unit.gif");
			else if("UM".equals(codesetid)){
					treeitem.put("icon","/images/dept.gif");
					CodeItem code = AdminCode.getCode("UM",itemid, upLevel);
					if(showLevelDept)
						treeitem.put("levelName",code.getCodename());
			}else if("@K".equals(codesetid))
					treeitem.put("icon","/images/pos_l.gif");
			else{
				//普通代码如果设置只能选叶子节点，并且当前节点不是叶子节点，将codesetid置为空
				if(this.onlyLeafNode && !"".equals(tempcodeitemid))
					treeitem.put("codesetid", "");
			}
		    	
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		else
	    			treeitem.put("selectable", "true");
	    		
	    		
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		else {
	    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
	    				treeitem.put("checked", true);
		    		}
	    		}
	    		
	    		if("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)){
	    			if(onlyLeafNode && !this.codesetid.equals(codesetid)){//UM UN @K 兼容多级单位或部门也可以选择
	    				treeitem.remove("checked");
	    			}
	    		}else{
	    			if(onlyLeafNode && !"".equals(tempcodeitemid)){//add by xiegh on date 20180319 如果设置了“进末级代码项可选” 且  该节点没有下级节点  就把复选框去除
	    				treeitem.remove("checked");
	    				treeitem.put("selectable", "false");
	    			}
	    		}
	    		if("true".equals(expanded)){ //true 全部展开
		    		treeitem.put("expanded",Boolean.TRUE);
		    		if(!"".equals(tempcodeitemid))
		    			treeitem.put("children", getleafJson(codesetid,itemid,multiple,doChecked,checkedcodeids)); //展开二级节点 changxy
	    		}else						//不展开下级
	    			treeitem.put("expanded",Boolean.FALSE);
	    		
	    		childrens.add(treeitem);
	    		
	    		
    	    }
    	    
    	    //如果不是机构或者不显示虚拟机构，返回
    	    if((!"UN".equals(codesetid) && !"UM".equals(codesetid) && !"@K".equals(codesetid)) || !this.vorg)
    	    		return childrens;
    	    
    	    String vsql = strsql.toString().replaceAll("organization", "vorganization");
    	    rset = dao.search(vsql);
    	    while (rset.next())
    	    {
	    	    HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetid = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc);
	    		treeitem.put("codesetid", codesetid);
	    		treeitem.put("itemdesc", codeitemdesc);
	    		treeitem.put("orgtype", "vorg");
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/b_vroot.gif");
			else if("UM".equals(codesetid))
					treeitem.put("icon","/images/vdept.gif");
			else if("@K".equals(codesetid))
					treeitem.put("icon","/images/vpos_l.gif");
			else{
				if(!isHideTip)
					treeitem.put("qtip","代码："+itemid);
			}
		    	
	    		//查询一下，判断是否有孩子节点
	    		treeitem.put("leaf", Boolean.FALSE);
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		else {
	    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
	    				treeitem.put("checked", true);
		    		}
	    		}
	    		childrens.add(treeitem);
	    		
	    		
    	    }
    	} catch (SQLException ee)
    	{
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	} finally
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
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
    	
    	return childrens;
    }
    
    /** 求查询代码的字符串 */
    private String getQueryString(Connection conn)
    {   
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
		StringBuffer str = new StringBuffer();
		if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
		{
		    if ("UN".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
				str.append(this.codesetid);
				str.append("'");
		    } else if ("UM".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
				str.append(this.codesetid);
				str.append("' or codesetid='UN') ");
		    } else if ("@K".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
				str.append(this.codesetid);
				str.append("' or codesetid='UN' or codesetid='UM') ");
		    }
		    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		}else if("@@".equalsIgnoreCase(this.codesetid))//人员库
		{
		    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname order by dbid");
		    return str.toString();
		}
		else
		{
	        int codeFlag = getCodeFlag(this.codesetid);		
		    str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
		    str.append(this.codesetid);
		    str.append("'");
		    if("1".equals(this.isValidCtr)){
			    /**去掉已过期的 lizw 2012-02-29*/
			    if(codeFlag == 1)
			        str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			    else
			        //去掉设置为无效的 gdd 13-7-17 v6x
			        str.append(" and invalid<>0 ");
		    }
		}
		
		if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid)) {
			str.append(" and parentid=codeitemid");
		} else {
			str.append(" and parentid<>codeitemid and ");
			str.append(" parentid='");
			str.append(codeitemid);
			str.append("'");
		}
		
		if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
		{
		    str.append(" ORDER BY a0000,codeitemid ");
		}else if(!"@@".equalsIgnoreCase(this.codesetid))
		{
				if(isHavA0000)
					str.append(" ORDER BY a0000,codeitemid ");
				else
					str.append(" ORDER BY codeitemid ");
		}
		return str.toString();
    }
    
    public String getTempCodeItemid(String codesetid,String codeitemid) throws GeneralException{
    	String str = "";
		
		ResultSet rset = null;
		Connection conn = AdminDb.getConnection();
    	try{
    		ContentDAO dao = new ContentDAO(conn);
    		StringBuffer sb = new StringBuffer("");
    		sb.append("select codeitemid from ");
			if ("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)) {
				sb.append("organization where codeitemid in (select codeitemid from organization where parentid='"+codeitemid+"' and parentid<>codeitemid) and (codesetid='");
				if ("UN".equals(this.codesetid)) {
					sb.append("UN')");
				} else if ("UM".equals(this.codesetid)) {
					sb.append("UM' or codesetid='UN')");
				} else if ("@K".equals(this.codesetid)) {
					sb.append("@K' or codesetid='UM' or codesetid='UN')");
				} 
				 String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				 sb.append(" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+now);
				 sb.append(" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+now);
				 sb.append(" order by a0000,codeitemid");
			} else {
				sb.append("codeitem where codesetid='"+codesetid+"' and parentid='"+codeitemid+"' and codeitemid<>parentid");
					int codeFlag = getCodeFlag(this.codesetid);		
				    if("1".equals(this.isValidCtr)){
					    if(codeFlag == 1){
					    	String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					        sb.append(" and "+Sql_switcher.dateValue(now)+" between start_date and end_date ");
					    }else
					        sb.append(" and invalid=1 ");
				    }
				if(isHavA0000)
					sb.append(" order by a0000,codeitemid");
				else
					sb.append(" order by codeitemid");
			}
    		rset = dao.search(sb.toString());
    		if(rset.next()){
    			str = rset.getString("codeitemid")==null?"":rset.getString("codeitemid");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
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
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
		return str;
    }
    
    
    /**
     * 加载二级节点下的数据
     * changxy 20160612
     * @param checkedcodeids 
     * 
     * */
    public ArrayList getleafJson(String codesetid,String codeitemid,Boolean multiple,Boolean doChecked, String checkedcodeids)throws GeneralException {
    		//根据父节点查询数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate =sdf.format(new Date());
	    	StringBuffer sbf=new StringBuffer();
	    	sbf.append("select codesetid,codeitemid,codeitemdesc,childid from ");
	    	if("@K".equalsIgnoreCase(this.codesetid)||"UM".equalsIgnoreCase(this.codesetid)||"UN".equalsIgnoreCase(this.codesetid)) {
	    		sbf.append(" organization ");
	    		sbf.append(" where  ");
	    		//zxj 20180418 组织机构树比较特殊，下级代码中UN、UM、@K三类都可能有，不能只取本codesetid的。
                sbf.append("codesetid in (");
                sbf.append("'").append(codesetid).append("'");
                sbf.append(",'").append(this.codesetid).append("'");
                if ("@K".equalsIgnoreCase(this.codesetid)) {
                    sbf.append(",'UM','UN'");
                } else if ("UM".equalsIgnoreCase(this.codesetid)) {
                    sbf.append(",'UN'");
                }
                sbf.append(")");
	    	} else {
	    	    sbf.append(" codeitem ");
    	        sbf.append(" where  ");
    	        sbf.append("codesetid=");
    	        sbf.append("'");
    	        sbf.append(codesetid);
    	        sbf.append("'");
	    	}
	    	sbf.append(" and parentid=");
	    	sbf.append("'");
	    	sbf.append(codeitemid);
	    	sbf.append("'");
	    	sbf.append(" and codeitemid <>");
	    	sbf.append("'");
	    	sbf.append(codeitemid);
	    	sbf.append("'");
	    	sbf.append(" and ");
	    	sbf.append( Sql_switcher.dateValue(backdate) +" between start_date and end_date");  //添加日期 changxy
	    	sbf.append(" ORDER BY a0000,codeitemid ");
	    	
	    	ResultSet rset=null;
	    	Connection conn=AdminDb.getConnection();
	    	ContentDAO dao=new ContentDAO(conn);
	    	ArrayList childrens=new ArrayList();
	    	try {
				rset=dao.search(sbf.toString());
				while(rset.next()){
					HashMap treeitem = new HashMap();
		    		String itemid = rset.getString("codeitemid");
		    		if (itemid == null)
		    		    itemid = "";
		    		itemid = itemid.trim();
		    		String codeitemdesc = rset.getString("codeitemdesc");
		    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
		    		String nodeCodeSetId = rset.getString("codesetid");
		    		treeitem.put("id", itemid);
		    		treeitem.put("text", codeitemdesc);
		    		treeitem.put("codesetid", nodeCodeSetId);
		    		treeitem.put("itemdesc", codeitemdesc);
		    		String layerdesc = this.searchLevelDesc(itemid, nodeCodeSetId, this.layerLevel);
		    		treeitem.put("layerdesc", layerdesc);
		    		//设置图片
			    	if("UN".equals(nodeCodeSetId))
			    		treeitem.put("icon","/images/unit.gif");
					else if("UM".equals(nodeCodeSetId))
						treeitem.put("icon","/images/dept.gif");
					else if("@K".equals(nodeCodeSetId))
						treeitem.put("icon","/images/pos_l.gif");
			    	
		    		//查询一下，判断是否有孩子节点
		    		String tempcodeitemid = getTempCodeItemid(nodeCodeSetId,itemid);
		    		if(!"".equals(tempcodeitemid))
		    			treeitem.put("leaf", Boolean.FALSE);
		    		else{
		    			treeitem.put("leaf", Boolean.TRUE);
		    		}
		    		
		    		if(multiple)
		    			treeitem.put("checked", false);
		    		if(doChecked)
		    			treeitem.put("checked", true);
		    		else {
		    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
		    				treeitem.put("checked", true);
			    		}
		    		}
		    		childrens.add(treeitem);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				 GeneralExceptionHandler.Handle(e);
			}finally{
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
	    	    } catch (SQLException ee)
	    	    {
	    		ee.printStackTrace();
	    	    }
			}
	    	
	    	return childrens;
    }
    
    private int getCodeFlag(String codesetid){
	    	int codeflag=-1;
	    	String sql = " select validateflag from codeset where codesetid='"+codesetid+"'";
	    	Connection conn = null;
	    	ResultSet rs = null;
	    	try{
	    		conn = AdminDb.getConnection();
	    		ContentDAO dao = new ContentDAO(conn);
	    		rs = dao.search(sql);
	    		if(rs.next())
	    			codeflag = rs.getInt("validateflag");
	    	}catch(SQLException e){
	    	    e.printStackTrace();
	    	} catch (GeneralException e) {
				e.printStackTrace();
			}finally{
	    		try{
	    			if(conn !=null)
	    				conn.close();
	    			
	    			if(rs != null)
	    				rs.close();
	    		}catch(Exception ex){
	    			ex.printStackTrace();
	    		}
	    		    
	    	}
	    	return codeflag;
    }
    
    private String searchLevelDesc(String codeitemid, String codesetid, int layerLevel) {
		String layerdesc = "";
		if("UM".equalsIgnoreCase(codesetid)&&layerLevel>0){
			CodeItem item=AdminCode.getCode("UM",codeitemid,layerLevel);
			if(item!=null){
				layerdesc = item.getCodename();
    		}else{
	    		layerdesc = AdminCode.getCodeName(codesetid,codeitemid);
	    	}
		}
		return layerdesc;
	}

	public void setIsValidCtr(String isValidCtr) {
		this.isValidCtr = isValidCtr;
	}

	public void setVorg(boolean vorg) {
		this.vorg = vorg;
	}

	public void setOnlyLeafNode(boolean onlyLeafNode) {
		this.onlyLeafNode = onlyLeafNode;
	}
	
	public void setLayerLevel(int layerLevel) {
		this.layerLevel = layerLevel;
	}
    
}
