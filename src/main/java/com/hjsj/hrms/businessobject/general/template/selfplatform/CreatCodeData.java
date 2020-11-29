package com.hjsj.hrms.businessobject.general.template.selfplatform;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreatCodeData{

	/** 代码类 */
    private String codesetid;

    /** 代码项 */
    private String codeitemid;
	
	
	private String isValidCtr="1"; //是否过滤有效无效。0不控制，1控制 默认控制
	
	private boolean vorg = false;
	
	private static boolean isHavA0000=false;//是否有a0000字段
	
	static{
		RecordVo vo = new RecordVo("codeitem");
		if(vo.hasAttribute("a0000")) {
            isHavA0000=true;
        }
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
	 * @throws Exception 
     */
	public ArrayList outCodeData(boolean multiple,boolean doChecked,String expanded, Boolean isHideTip, Boolean showLevelDept,
			String checkedcodeids) throws Exception{
		return outCodeData(multiple,doChecked,expanded,isHideTip,false);
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
	 * @throws Exception 
     */
    public ArrayList outCodeData(boolean multiple,boolean doChecked,String expanded, Boolean isHideTip,boolean showLevelDept) throws Exception{
    	StringBuffer strsql = new StringBuffer();
    	ArrayList nodes = new ArrayList();
    	ArrayList<CodeItem>  codeList = new ArrayList<CodeItem>();
    	Connection conn = null;
    	try {
    		conn = AdminDb.getConnection();
    		//xus 18/3/14 显示多层级等部门------BEGIN 
    		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
    		String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
    		if(uplevelStr==null||uplevelStr.length()==0) {
                uplevelStr="0";
            }
    	    int upLevel = Integer.parseInt(uplevelStr);
    	    strsql.append(getQueryString());
    	    // 添加organization
    	    codeList.addAll(getCodeList(strsql.toString()));
    	    
    	    //如果不是机构或者不显示虚拟机构，返回
    	    if(("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)) && this.vorg) {
    	    	String vsql = strsql.toString().replaceAll("organization", "vorganization");
    	    	codeList.addAll(getCodeList(vsql.toString()));
    	    }
    	    
    	    boolean isRecHistoryCode = AdminCode.isRecHistoryCode(this.codesetid);
    	    
    		for (int i=0; i<codeList.size(); i++) {
    			CodeItem code = (CodeItem)codeList.get(i);
    			// 寻找根节点
    			if (!code.getCodeitem().equalsIgnoreCase(code.getPcodeitem())) {
                    continue;
                }
    			boolean isInvalid = validCode(isRecHistoryCode, code);
                if(!"UN".equalsIgnoreCase(codesetid) && !"UM".equalsIgnoreCase(codesetid) &&
                		!"@K".equalsIgnoreCase(codesetid) && !isInvalid) {
                    continue;
                }
    			HashMap treeitem = new HashMap();
    			treeitem = getTreeItem(code, isHideTip, upLevel, showLevelDept);
    			
    			// 递归加载子节点
    			ArrayList childNodes = getChildCode(codeList, code.getCodeitem(),isRecHistoryCode, isHideTip, upLevel, showLevelDept);
    			if (childNodes != null && childNodes.size()>0) {
    			    treeitem.put("children", childNodes);
    			}

    			nodes.add(treeitem);
    		}
    	} catch (SQLException ee) {
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	}finally
    	{
    	    PubFunc.closeDbObj(conn);
    	}
    	return nodes;
    }
    
    private ArrayList<CodeItem> getCodeList(String sql) throws Exception {
		ArrayList<CodeItem> result = new ArrayList<CodeItem>();
		Connection conn = null;
    	ResultSet rs = null;
    	try{
    		conn = AdminDb.getConnection();
    		ContentDAO dao = new ContentDAO(conn);
    		rs = dao.search(sql);
    		while(rs.next()) {
    			CodeItem item = new CodeItem();
    			item.setCodeid(rs.getString("CodeSetID"));
    			item.setCodeitem(rs.getString("CodeItemId"));
    			String text = rs.getString("CodeItemDesc");
    			if (text == null) {
    				text = "";            }
    			text = text.replaceAll("\r", "");
    			text = text.replaceAll("\n", "");
    			item.setCodename(text);
    			item.setPcodeitem(rs.getString("parentid"));
    			String childid = rs.getString("childid");
    			if (childid == null || childid.length() < 1) {
    				childid = rs.getString("CodeItemId");            }
    			item.setCcodeitem(childid);
    			result.add(item);
    		}
    	}catch(SQLException e){
    	    e.printStackTrace();
    	} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
    		try{
    			if(conn !=null) {
                    conn.close();
                }
    			
    			if(rs != null) {
                    rs.close();
                }
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    		    
    	}
		return result;
	}
    
    /** 求查询代码的字符串 
     * @throws GeneralException */
    private String getQueryString() throws GeneralException
    {   
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
		StringBuffer str = new StringBuffer();
		if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
		{
		    if ("UN".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where codesetid='");
				str.append(this.codesetid);
				str.append("'");
		    } else if ("UM".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
				str.append(this.codesetid);
				str.append("' or codesetid='UN') ");
		    } else if ("@K".equalsIgnoreCase(this.codesetid))
		    {
				str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
				str.append(this.codesetid);
				str.append("' or codesetid='UN' or codesetid='UM') ");
		    }
		    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		}else if("@@".equalsIgnoreCase(this.codesetid))//人员库
		{
		    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid,'0' parentid from dbname order by dbid");
		    return str.toString();
		}
		else
		{
	        int codeFlag = getCodeFlag(this.codesetid);		
		    str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from codeitem where codesetid='");
		    str.append(this.codesetid);
		    str.append("'");
		    if("1".equals(this.isValidCtr)){
			    /**去掉已过期的 lizw 2012-02-29*/
			    if(codeFlag == 1) {
                    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
                } else
			        //去掉设置为无效的 gdd 13-7-17 v6x
                {
                    str.append(" and invalid<>0 ");
                }
		    }
		}
		
//		if (this.codeitemid == null || this.codeitemid.equals("") || this.codeitemid.equals("ALL")) {
//			str.append(" and parentid=codeitemid");
//		} else {
//			str.append(" and parentid<>codeitemid and ");
//			str.append(" parentid='");
//			str.append(codeitemid);
//			str.append("'");
//		}
		
		if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
		{
		    str.append(" ORDER BY a0000,codeitemid ");
		}else if(!"@@".equalsIgnoreCase(this.codesetid))
		{
				if(isHavA0000) {
                    str.append(" ORDER BY a0000,codeitemid ");
                } else {
                    str.append(" ORDER BY codeitemid ");
                }
		}
		return str.toString();
    }
    
//    public String getTempCodeItemid(String codesetid,String codeitemid) throws GeneralException{
//    	String str = "";
//		
//		ResultSet rset = null;
//    	try{
//    		ContentDAO dao = new ContentDAO(this.getConnection());
//    		StringBuffer sb = new StringBuffer("");
//    		sb.append("select codeitemid from ");
//			if ("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)) {
//				sb.append("organization where codeitemid in (select codeitemid from organization where parentid='"+codeitemid+"' and parentid<>codeitemid) and (codesetid='");
//				if ("UN".equals(this.codesetid)) {
//					sb.append("UN')");
//				} else if ("UM".equals(this.codesetid)) {
//					sb.append("UM' or codesetid='UN')");
//				} else if ("@K".equals(this.codesetid)) {
//					sb.append("@K' or codesetid='UM' or codesetid='UN')");
//				} 
//				 String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
//				 sb.append(" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+now);
//				 sb.append(" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+now);
//				 sb.append(" order by a0000,codeitemid");
//			} else {
//				sb.append("codeitem where codesetid='"+codesetid+"' and parentid='"+codeitemid+"' and codeitemid<>parentid");
//					int codeFlag = getCodeFlag(this.codesetid);		
//				    if("1".equals(this.isValidCtr)){
//					    if(codeFlag == 1){
//					    	String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//					        sb.append(" and "+Sql_switcher.dateValue(now)+" between start_date and end_date ");
//					    }else
//					        sb.append(" and invalid=1 ");
//				    }
//				if(isHavA0000)
//					sb.append(" order by a0000,codeitemid");
//				else
//					sb.append(" order by codeitemid");
//			}
//    		rset = dao.search(sb.toString());
//    		if(rset.next()){
//    			str = rset.getString("codeitemid")==null?"":rset.getString("codeitemid");
//    		}
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}finally{
//    	    PubFunc.closeDbObj(rset);
//    	}
//		return str;
//    }
    
    private int getCodeFlag(String codesetid){
    	int codeflag=-1;
    	String sql = " select validateflag from codeset where codesetid='"+codesetid+"'";
    	Connection conn = null;
    	ResultSet rs = null;
    	try{
    		conn = AdminDb.getConnection();
    		ContentDAO dao = new ContentDAO(conn);
    		rs = dao.search(sql);
    		if(rs.next()) {
                codeflag = rs.getInt("validateflag");
            }
    	}catch(SQLException e){
    		e.printStackTrace();
    	} catch (GeneralException e) {
    		e.printStackTrace();
    	}finally{
    		try{
    			if(conn !=null) {
                    conn.close();
                }
    			
    			if(rs != null) {
                    rs.close();
                }
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
    
    /**
     * 判定代码指标是否有效
     * @param isRecHistoryCode 是否记录历史记录
     * @param code 代码值
     * @return isInvalid 指标是否有效
     */
    public boolean validCode(boolean isRecHistoryCode,CodeItem code) {
        boolean isInvalid = true;//默认代码有效
        //进行代码过滤，去掉无效的代码
        if(!isRecHistoryCode) {//如果不是记录历史的代码类,直接根据invalid
            int invalid = code.getInvalid();
            if(invalid==0) {//等于0是无效代码
                isInvalid = false;
            }
        }else {
            Date endDate = code.getEndDate();//结束日期
            Date startDate = code.getStartDate();//开始日期
            Date now = new Date();
            if(now.getTime()>endDate.getTime()||now.getTime()<startDate.getTime()) {//大于结束日期小于开始日期
                isInvalid = false;
            }
        }
        return isInvalid;
    }
    
    public ArrayList<HashMap> getChildCode(ArrayList codeList, String parentId, boolean isRecHistoryCode, boolean isHideTip, int upLevel, boolean showLevelDept) {
		ArrayList<HashMap> nodes = new ArrayList<HashMap>();

		for (int i=0; i<codeList.size(); i++) {
			CodeItem item = (CodeItem)codeList.get(i);

			// 根节点跳过
			if (item.getCodeitem().equalsIgnoreCase(parentId)) {
                continue;
            }

			// 非parentId的子节点跳过
			if (!item.getPcodeitem().equalsIgnoreCase(parentId)) {
                continue;
            }
			boolean isInvalid = validCode(isRecHistoryCode, item);
            if(!"UN".equalsIgnoreCase(codesetid) && !"UM".equalsIgnoreCase(codesetid) &&
            		!"@K".equalsIgnoreCase(codesetid) && !isInvalid) {
                continue;
            }
			HashMap treeitem = new HashMap();
			treeitem = getTreeItem(item, isHideTip, upLevel, showLevelDept);

			ArrayList<HashMap> childNodes = getChildCode(codeList, item.getCodeitem(),isRecHistoryCode, isHideTip, upLevel, showLevelDept);
			if (childNodes != null && childNodes.size()>0) {
			    treeitem.put("children", childNodes);
			}

			nodes.add(treeitem);
		}
		return nodes;
	}
    
    private HashMap<String, Object> getTreeItem(CodeItem code, boolean isHideTip, int upLevel, boolean showLevelDept){
    	HashMap<String, Object> treeitem = new HashMap<String, Object>();
    	try {
    		String itemid = code.getCodeitem().trim();
    		treeitem.put("value", itemid);
			treeitem.put("codesetid", code.getCodeid());
			treeitem.put("label", code.getCodename());
    		String layerdesc = this.searchLevelDesc(itemid, codesetid, this.layerLevel);
    		treeitem.put("layerdesc", layerdesc);
    		
    		if(!isHideTip) {
                treeitem.put("qtip","ID:"+itemid);
            }
    		//设置图片
	    	if("UM".equalsIgnoreCase(code.getCodeid())){
	    	    treeitem.put("icon","umicon.png");
            }else if("UN".equalsIgnoreCase(code.getCodeid())){
                treeitem.put("icon","unicon.png");
            }else if("@K".equalsIgnoreCase(code.getCodeid())){
                treeitem.put("icon","atkicon.png");
            }
	    	
			if(showLevelDept) {
				CodeItem code_ = AdminCode.getCode("UM",itemid, upLevel);
				treeitem.put("levelName",code_.getCodename());
	    	}
    		
    		if("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)){
    			if(((onlyLeafNode || "@K".equals(this.codesetid)) && !this.codesetid.equals(codesetid))){//UM UN @K 兼容多级单位或部门也可以选择
    				treeitem.put("disabled", true);
    			}
    		}
//    		if(expanded.equals("true")){ //true 全部展开
//    			treeitem.put("expanded",Boolean.TRUE);
//			}else						//不展开下级
//				treeitem.put("expanded",Boolean.FALSE);
//
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return treeitem;
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
