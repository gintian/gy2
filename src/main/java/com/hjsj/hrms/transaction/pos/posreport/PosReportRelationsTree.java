package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 *<p>Title:PosReportRelationsTree.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class PosReportRelationsTree {

	private String position;
	
	private UserView userView;
	
	private String action;
	
	private String target;
	
	private String tree;
    private String code;
	
    private String yfiles;
    private String sep;
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	//从yfiles 新岗位汇报进入
	public void setYfiles(String yfiles) {
		this.yfiles = yfiles;
		
		if(yfiles!=null && "1".equals(yfiles))
		  this.action = "/pos/posreport/show_relations_map.do";
	}
	
	public void setSep(String sep){
		this.sep = sep;
	}
	
	public PosReportRelationsTree(String position,UserView userView)
	{	
		this.action="/pos/posreport/search_report_relations.do";
		this.target="mil_body";
		this.position=position;
		this.tree="/pos/posreport/pos_report_relations_tree.jsp";
		this.userView = userView;
	}
	/**
    * 创建汇报关系树
    * @return xmls
    * @throws Exception
    */
	public String getReportRelationsTree()throws GeneralException
	{
        StringBuffer xmls = new StringBuffer();
        StringBuffer sqlstr = new StringBuffer();
        Connection conn = null;
        
        conn = AdminDb.getConnection();
        
        ResultSet rset = null;
        Element root = new Element("TreeNode");
        ContentDAO dao = new ContentDAO(conn);
        
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate = "";
        backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
        try{       	
        	HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("POS_MAPOPTION",true,"Usr",userView); 
        	String constantstr = "";
        	if(Sql_switcher.searchDbServer()== Constant.ORACEL)
			{

        		sqlstr.append(" select * from constant where constant = 'PS_SUPERIOR' ");
			}
			else
			{			
				sqlstr.append(" select * from [constant] where [constant] = 'PS_SUPERIOR' ");
			}
        	
            rset = dao.search(sqlstr.toString());
            while(rset.next())
            {
            	if(Sql_switcher.searchDbServer()== Constant.ORACEL)
    			{
            		constantstr = Sql_switcher.readMemo(rset,"str_value");
    			}
    			else
    			{			
    				constantstr = rset.getString("str_value");
    			}
        	
            }         
            sqlstr.delete(0,sqlstr.length());
 
            if(!(constantstr==null || "".equals(constantstr.toString()) || DataDictionary.getFieldItem(constantstr)==null || "0".equals(DataDictionary.getFieldItem(constantstr).getUseflag()))) // 有上级职位，有汇报关系
			{
        		//	 没有下级职位  position=no
        		if(( "first".equals(this.position)))
	        	{
        			sqlstr.append(" update K01 ");
        			sqlstr.append(" set K01."+constantstr);
        			sqlstr.append(" ='' ");
        			sqlstr.append(" where "+Sql_switcher.sqlNull("K01."+constantstr,null)+" like 'null' ");
//		        			System.out.println(sqlstr.toString());
        			dao.update(sqlstr.toString());
        			sqlstr.delete(0,sqlstr.length());
	        		sqlstr.append(" select K01.E01A1, K01."+constantstr+",Org.codeItemDesc ");
	        		sqlstr.append(" from K01 K01, organization org ");
	        		sqlstr.append(" where K01.E01A1 = org.CodeItemId ");
	        		if(code==null||code.length()==0){
		        		if(Sql_switcher.searchDbServer()== Constant.ORACEL)
		        			sqlstr.append(" and K01."+constantstr+" is null ");
		    			else
		    				sqlstr.append(" and K01."+constantstr+" like '' ");
	        		}
	        		else
	        		{
	        			/**
	        			 * cmq changed at 20121003 单位或岗位权限控制规则
	        			 * 业务范围-操作单位-人员范围
	        			 */
	        			/*
	        			sqlstr.append(" and org.CodeItemId like '"+code+"%' ");
	        			sqlstr.append(" and K01."+constantstr+" not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and org.CodeItemId like '"+code+"%') ");
	        			*/
	        		    
	        		    /**
	        		     * zxj changed at 20131228 
	        		     * 增加k01.constantstr为空的条件，否则，授权到单位或整个组织机构查不到记录
	        		     */
	        			sqlstr.append(" and "+code);
	        			sqlstr.append(" and (");
	        			sqlstr.append(" K01."+constantstr+" not in (select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+code+") ");
	        			sqlstr.append(" or ");
	        			sqlstr.append(" k01." + constantstr + " is null");
	        			if (Sql_switcher.searchDbServer()!= Constant.ORACEL)
	        			    sqlstr.append(" or k01." + constantstr + "=''");
	        			sqlstr.append(")");

	        		}
	        		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
	        		sqlstr.append(" order by K01.E01A1 ");
			        		//System.out.println(sqlstr.toString());
	        		rset = dao.search(sqlstr.toString());
		            int i=0;
		            while(rset.next())
		            {
			            Element child = new Element("TreeNode");
			            if(("1").equals(yfiles) && i==50 ){
			            	child.setAttribute("text", "本级节点太多，请设置岗位直接上级！");
			            	child.setAttribute("icon", "tree_warning.png");
			            	root.addContent(child);
			            	break;
			            }
			            
			            this.position=rset.getString(constantstr);
			            String id="";
			            if(!(this.position==null || "".equals(this.position)))
			            {					            	
			            	id=rset.getString(constantstr);
			            	String codeItemDesc = "";
			            	if(paramehashmap.containsKey("isshowposup") && "true".equals(paramehashmap.get("isshowposup")))
			            		codeItemDesc = OrgMapBo.getUnitAndDept(this.position).replace(",", sep)+this.getCodeItemDesc(this.position);
			            	if("".equals(codeItemDesc))//处理上级为空是的问题 wusy
			            		continue;
			            	else
			            	    codeItemDesc = this.getCodeItemDesc(this.position);
			            	//String codeItemDesc = this.getCodeItemDesc(this.position);
			            	if("".equals(codeItemDesc))//处理上级为空是的问题 wusy
			            		continue;
			            	child.setAttribute("text", codeItemDesc);
			            	child.setAttribute("xml",this.tree+"?encryptParam="+PubFunc.encrypt("position="+this.position+"&yfiles="+yfiles+"&sep="+this.sep));
			            }else
			            {
			            	id=rset.getString("e01a1");
			            	String codeItemDesc = "";
			            	if(paramehashmap.containsKey("isshowposup") && "true".equals(paramehashmap.get("isshowposup")))
			            		codeItemDesc = OrgMapBo.getUnitAndDept(id).replace(",", sep)+rset.getString("codeItemDesc");
			            	else
			            	    codeItemDesc = rset.getString("codeItemDesc");
			            	child.setAttribute("text", codeItemDesc);
			            	child.setAttribute("xml",this.tree+"?encryptParam="+PubFunc.encrypt("position="+id+"&yfiles="+yfiles+"&sep="+this.sep));
			            }
//					            String codeItemDesc = rset.getString("codeItemDesc");
			            child.setAttribute("id",id);
//					            child.setAttribute("text", codeItemDesc);
			            child.setAttribute("title",id);	         
			            theaction = this.action+"?b_search=link&encryptParam="+PubFunc.encrypt("code="+id+"&kind=0");
			            child.setAttribute("href", theaction);
			            child.setAttribute("target",this.target);				            
			            child.setAttribute("icon","/images/pos_l.gif");	          
			            root.addContent(child);
			            i++;
		            }

	        	}else if(!"no".equals(this.position))//  有下级职位
	        	{
	        		sqlstr.append(" select K01.E01A1, K01."+constantstr+",Org.codeItemDesc ");
	        		sqlstr.append(" from K01 K01, organization org ");
	        		sqlstr.append(" where K01.E01A1 = org.CodeItemId ");
	        		sqlstr.append(" and "+Sql_switcher.sqlNull("K01."+constantstr,null)+"not like 'null' ");
	        		sqlstr.append(" and k01."+constantstr+"='"+this.position+"' ");
	        		
	        		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
	        		
	        		if(code!=null&&code.length()>0)
	        		{
	        			/**
	        			 * cmq changed at 20121003 for 单位和岗位权限范围规则控制 
	        			 * 业务范围-操作单位-人员范围
	        			 */
	        		   //sqlstr.append(" and org.CodeItemId like '"+code+"%' ");
	        			sqlstr.append(" and "+code);	        			
	        		}
	        		
	        		sqlstr.append(" order by K01.E01A1 ");
//			        		System.out.println(sqlstr.toString());
	        		rset = dao.search(sqlstr.toString());
		            while(rset.next())
		            {
			            Element child = new Element("TreeNode");
			            this.position=rset.getString(constantstr);
			            String id=rset.getString("e01a1");
			            String codeItemDesc = "";
		            	if(paramehashmap.containsKey("isshowposup") && "true".equals(paramehashmap.get("isshowposup")))
		            		codeItemDesc = OrgMapBo.getUnitAndDept(id).replace(",", sep)+rset.getString("codeItemDesc");
		            	else
		            	    codeItemDesc = rset.getString("codeItemDesc");
			            child.setAttribute("id",id);
			            child.setAttribute("text", codeItemDesc);
			            child.setAttribute("title",id);	         
			            theaction = this.action+"?b_search=link&encryptParam="+PubFunc.encrypt("code="+id+"&kind=0");
			            child.setAttribute("href", theaction);
			            child.setAttribute("target",this.target);
			            child.setAttribute("xml",this.tree+"?encryptParam="+PubFunc.encrypt("position="+id+"&yfiles="+yfiles+"&sep="+this.sep));
			            child.setAttribute("icon","/images/pos_l.gif");	          
			            root.addContent(child);
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
            	if(conn != null)
                {
                	conn.close();
                }
                if(rset != null)
                {
                	rset.close();
                }
//                if(stmt != null)
//                {
//                	stmt.close();
//                }
                
            }catch(SQLException ee){
                ee.printStackTrace();
            } 
        }
        return xmls.toString();        
	}
	   
	public String getCodeItemDesc(String constantstr)
	{
		String codeItemDesc = "";
		Connection conn = null;//AdminDb.getConnection();
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			RecordVo vo = new RecordVo("organization");
	    	vo.setString("codesetid", "@K");
	    	vo.setString("codeitemid", constantstr);	    	
	    	RecordVo a_vo =dao.findByPrimaryKey(vo);
	    	codeItemDesc = a_vo.getString("codeitemdesc");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
            try{	               
                if(conn != null)
                {
                	conn.close();
                }
            }catch(SQLException ee){
                ee.printStackTrace();
            } 
        }
		return codeItemDesc;
	}
	
}
