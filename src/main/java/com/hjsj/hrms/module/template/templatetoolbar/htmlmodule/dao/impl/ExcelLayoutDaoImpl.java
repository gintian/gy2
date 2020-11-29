package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.impl;

import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.ExcelLayoutDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExcelLayoutDaoImpl implements ExcelLayoutDao{
	private Connection conn=null;
	
	public ExcelLayoutDaoImpl(Connection conn){
		this.conn=conn;
	}
	@Override
    public String getTempletName(int tabid) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
        String name="";
        ArrayList paramList=new ArrayList();
		try {
			String sql ="select name from template_table where tabid=?";
			rs = dao.search(sql);
			paramList.add(tabid);
			rs=dao.search(sql,paramList);
			if(rs.next()){
				name=rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
	  	{
	  		PubFunc.closeDbObj(rs);
	  	}
		return name;
	}
	@Override
    public ArrayList getPageIdList(int tabid, String noshow_pageno) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		ArrayList list=new ArrayList();
		try {
			sql.append("select * from Template_Page where tabid=");
			sql.append(tabid);
			if("".equals(noshow_pageno))//如果有设置的不显示页签 优先走这个
				sql.append(" and isprn<>0");
			sql.append(" and "+Sql_switcher.isnull("ismobile", "0")+"<>1");
			sql.append(" order by pageid ");
			list=dao.searchDynaList(sql.toString());
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	@Override
    public ArrayList getPageSetList(int tabid, String pageid) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		ArrayList list=new ArrayList();
		try {
			sql.append("select * from Template_Set where tabid=");
			sql.append(tabid);
			sql.append(" and pageid=");
			sql.append(pageid);
			sql.append(" order by rtop,rleft");
			list=dao.searchDynaList(sql.toString());
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得模板信息
	 * @param tabid 模板ID
	 * @param conn
	 * @return
	 */
	  @Override
      public RecordVo getTableVo(int tabid) {
		  	RecordVo tab_vo=new RecordVo("Template_table");
		  	RowSet rowSet=null;
		  	try
		  	{
		  		ContentDAO dao=new ContentDAO(this.conn);
		  		String sql="select template_table.tabid,template_table.name,template_table.noticeid,template_table.gzstandid,template_table.flag";
		  		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) //昆仑数据库static为系统关键字
		  			sql+=",template_table.\"static\"";
		  		else	
		  			sql+=",template_table.static";
		  		sql+=",template_table.paperori,template_table.paper,template_table.tmargin,template_table.bmargin,template_table.rmargin,template_table.lmargin,template_table.paperw,template_table.paperh";
		  		sql+=",template_table.operationcode,template_table.operationname,template_table.factor,template_table.lexpr,template_table.llexpr,template_table.userfalg,template_table.username,template_table.userflag,template_table.sp_flag,template_table.dest_base,template_table.content,template_table.ctrl_para from template_table  WHERE template_table.tabid=?";
		  		rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
		  		if(rowSet.next())
		  		{
		  			tab_vo.setInt("tabid",rowSet.getInt("tabid"));
		  			tab_vo.setString("name",rowSet.getString("name"));
		  			tab_vo.setString("noticeid",rowSet.getString("noticeid"));
		  			tab_vo.setString("gzstandid",rowSet.getString("gzstandid"));
		  			tab_vo.setInt("flag",rowSet.getInt("flag"));
		  			tab_vo.setInt("static",rowSet.getInt("static"));
		  			tab_vo.setString("operationcode",rowSet.getString("operationcode"));
		  			tab_vo.setString("operationname",rowSet.getString("operationname")); 
		  			tab_vo.setString("factor",Sql_switcher.readMemo(rowSet,"factor"));
		  			tab_vo.setString("lexpr",Sql_switcher.readMemo(rowSet,"lexpr"));
		  			tab_vo.setString("llexpr",Sql_switcher.readMemo(rowSet,"llexpr")); 
		  			tab_vo.setString("userfalg",rowSet.getString("userfalg"));
		  			tab_vo.setString("username",rowSet.getString("username"));
		  			tab_vo.setString("userflag",rowSet.getString("userflag"));
		  			tab_vo.setString("sp_flag",rowSet.getString("sp_flag"));
		  			tab_vo.setString("dest_base",rowSet.getString("dest_base"));
		  			tab_vo.setString("content",Sql_switcher.readMemo(rowSet,"content"));
		  			tab_vo.setString("ctrl_para",Sql_switcher.readMemo(rowSet,"ctrl_para")); 
		  			tab_vo.setInt("paperori",rowSet.getInt("paperori"));
		  			tab_vo.setInt("paper",rowSet.getInt("paper"));
		  			tab_vo.setDouble("tmargin",rowSet.getFloat("tmargin"));
		  			tab_vo.setDouble("bmargin",rowSet.getFloat("bmargin"));
		  			tab_vo.setDouble("rmargin",rowSet.getFloat("rmargin"));
		  			tab_vo.setDouble("lmargin",rowSet.getFloat("lmargin"));
		  			tab_vo.setDouble("paperw",rowSet.getFloat("paperw"));
		  			tab_vo.setDouble("paperh",rowSet.getFloat("paperh"));
		  		}
		  	}
		  	catch(Exception e)
		  	{
		  		e.printStackTrace();
		  	}
		  	finally
		  	{
		  		PubFunc.closeDbObj(rowSet);
		  	}
			return tab_vo;
	    }
	  
	  
	  /**
	   * 获得模板所有私有和公有临时变量
	   * @param tabId 模板ID
	   * @return
	   */
	  @Override
      public HashMap getAllVariableHm(int tabId)
	  {
		  HashMap hm=new HashMap(); 
		  StringBuffer strsql=new StringBuffer();
		  ArrayList paramList=new ArrayList();
		  ContentDAO dao=new ContentDAO(this.conn); 
		  RowSet rset=null;
		  try
		  {
			  strsql.append("select * from midvariable where nflag=0 and templetId <> 0 and (templetId =? or cstate = '1')"); //包含共享临时变量 2014-02-22
			  strsql.append(" order by sorting");	
			  paramList.add(tabId);
			  rset=dao.search(strsql.toString(),paramList);
			  while(rset.next())
			  {
				  RecordVo vo=new RecordVo("midvariable");
				  vo.setString("cname",rset.getString("cname"));
				  vo.setString("chz",rset.getString("chz"));
				  vo.setInt("ntype",rset.getInt("ntype"));
				  vo.setString("cvalue",rset.getString("cValue"));
				  String codesetid=rset.getString("codesetid");
				  if(codesetid==null|| "".equalsIgnoreCase(codesetid))
					  codesetid="0";
				  vo.setString("codesetid",codesetid);
				  vo.setInt("fldlen",rset.getInt("fldlen"));
				  vo.setInt("flddec",rset.getInt("flddec"));
				  hm.put(rset.getString("cname"),vo);
			  }
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		  finally
		  {
			  PubFunc.closeDbObj(rset);
		  }
		  return hm;
	 }
		/**
		 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
		 * @param task_id
		 * @return
		 */
	  @Override
      public HashMap getFieldPrivFillable(String task_id, int tabid) {
			HashMap _map=new HashMap();
			Document doc=null;
			Element element=null;
			try
			{
				if(task_id!=null)
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="select ext_param from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
					if("0".equals(task_id.trim())){
						sql="select ext_param from t_wf_node where nodetype=1 and tabid="+tabid;
					}
					RowSet rowSet=dao.search(sql);
					SAXBuilder saxbuilder=new SAXBuilder();
					if(rowSet.next())
					{
						String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
						if(ext_param!=null&&ext_param.trim().length()>0)
						{
							StringReader reader=new StringReader(ext_param);
							doc=saxbuilder.build(reader); 
							String xpath="/params/field_priv/field";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);	
							if(childlist.size()==0){
								xpath="/params/field_priv/field";
								 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								 childlist=findPath.selectNodes(doc);
							}
							if(childlist!=null&&childlist.size()>0)
							{
								for(int i=0;i<childlist.size();i++)
								{
									element=(Element)childlist.get(i);
									String editable="";
									//0|1|2(无|读|写)
									if(element!=null&&element.getAttributeValue("editable")!=null)
										editable=element.getAttributeValue("editable");
									if(editable!=null&&editable.trim().length()>0)
									{
										String columnname=element.getAttributeValue("name").toLowerCase();
										if(columnname.endsWith("_2")|| columnname.startsWith("s_")||"photo".equals(columnname)||"attachment".equals(columnname) ){
											if("1".equals(editable))
												editable="0";
											String fillable = element.getAttributeValue("fillable");
											if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable))
												editable="3";
											if(columnname.startsWith("s_")){
												_map.put(columnname.toUpperCase(), editable);
											}else{
												_map.put(columnname.split("_")[0], editable);
											}
										}
									}
								}
							}
						}
					}
					PubFunc.closeDbObj(rowSet);
				
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return _map;
		}
	  
		@Override
        public int getLayerByCodesetid(String codeid) {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
	        int layer=0;
	        ArrayList paramList=new ArrayList();
			try {
				String tableName="codeitem";
				String itemid="layer";
				if(codeid!=null&&("UM".equalsIgnoreCase(codeid)|| "UN".equalsIgnoreCase(codeid)|| "@K".equalsIgnoreCase(codeid))){
					tableName="organization";
					itemid="grade";
				}
				//判断是否是多层级，如果有值 则是多层级。。传值2
				String strsql ="select 1 from "+tableName+"  where  codeitemid<>parentid and codesetid=?";
				paramList.add(codeid);
				rs=dao.search(strsql.toString(),paramList);
				if(rs.next()){
					layer=2;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
		  	{
		  		PubFunc.closeDbObj(rs);
		  	}
			return layer;
		}
		@Override
		public int getMaxPageId(String tabId) {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
	        int result=0;
			try {
				ArrayList paramList=new ArrayList();
				String strsql ="select max(pageId) pageId  from template_page  where tabid=?";
				paramList.add(tabId);
				rs=dao.search(strsql.toString(),paramList);
				if(rs.next()){
					result=rs.getInt("pageId");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
		  	{
		  		PubFunc.closeDbObj(rs);
		  	}
			return result;
		}
		@Override
		public boolean getLeafCode(String codeid) {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
	        boolean isLeafCode=true;
	        ArrayList paramList=new ArrayList();
			try {
				String strsql ="select leaf_node from codeset  where codesetid=?";
				paramList.add(codeid);
				rs=dao.search(strsql.toString(),paramList);
				if(rs.next()){
					String leafNode = rs.getString("leaf_node");
					if ("1".equals(leafNode)) {
						isLeafCode=false;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
		  	{
		  		PubFunc.closeDbObj(rs);
		  	}
			return isLeafCode;
		}
	  
}