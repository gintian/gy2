package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Transition;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TemplateStaticDataBo {
	
	  private static HashMap  templateHashMap=new HashMap ();
	  private static HashMap  NodesHashMap=new HashMap ();
	  private static  HashMap  midvarHashMap=new HashMap ();
	  private static HashMap  pageCellMap=new HashMap (); 
	  private static HashMap constantStrMap=new HashMap();
	  private static HashMap operationtypeMap=new HashMap();
	  private static HashMap borderRectMap=new HashMap();
	  private static HashMap transitionMap=new HashMap();
	  private static HashMap beginCountMap=new HashMap();//记录开始节点被执行次数值，如果后面节点值小于此值，则获取此值。
	  private static long delayTime=15000; //有效时间 30秒
	  
	  public static void removeEleFromBeginCountMap(String ins_id){//驳回到发起人时需要把此值删除，下次重新查询。
		  if(beginCountMap.containsKey(ins_id)){
			  beginCountMap.remove(ins_id);
		  }
		  if(beginCountMap.containsKey(ins_id+"_time")){
			  beginCountMap.remove(ins_id+"_time");
		  }
	  }
	  
	  public static int getBeginCount(String ins_id,Connection conn ,String tabid)
	  {
		  int count=-1;
		  boolean readDatabase=false;
		  if(beginCountMap.get(ins_id)==null)
		  		readDatabase=true;
		  else
		  {
		  		long writetime=Long.parseLong((String)beginCountMap.get(ins_id+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime/3) //有效时间5秒
		  			readDatabase=true;
		  }
		  
		  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("beginCountMap="+readDatabase);
		  if(readDatabase)
		  {  
			   RowSet rset = null;		        
		        ContentDAO dao = new ContentDAO(conn);
		        int flag = -1;
		        try {
		        	String sql="select count from t_wf_task_objlink where ins_id=?  and node_id=(select node_id from t_wf_node where tabid=? and nodetype='1') order by task_id desc";
		        	ArrayList list=new ArrayList();
		        	list.add(ins_id);
		        	list.add(tabid);
		        	rset=dao.search(sql,list);
		            if (rset.next())
		            	flag = rset.getInt("count");
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        } finally {
		            if (rset != null) {
		                try {
		                    rset.close();
		                } catch (SQLException e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
		            }
		        }
		        
		        operationtypeMap.put(ins_id+"",flag+"");
		        operationtypeMap.put(ins_id+"_time",System.currentTimeMillis()+"");
		        
		        
		  } 
		  if(operationtypeMap.get(ins_id+"")!=null)
			  count=Integer.parseInt((String)operationtypeMap.get(ins_id));
		  return count;
	 }
	  
	  /**
	   @Description: 业务类型 对人员调入的业务单独处理
       *               =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
       *               =10其它不作特殊处理的业务 如果目标库未指定的话，则按源库进行处理
       * @param operationcode
	   * @return
	   */
	  public static int getOperationType(String operationcode,Connection conn )
	  {
		   
		  int type = -1;
		  boolean readDatabase=false;
		  if(operationtypeMap.get(operationcode)==null)
		  		readDatabase=true;
		  else
		  {
		  		long writetime=Long.parseLong((String)operationtypeMap.get(operationcode+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime/3) //有效时间5秒
		  			readDatabase=true;
		  }
		  
		  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getOperationType="+readDatabase);
		  if(readDatabase)
		  {  
			   RowSet rset = null;
		        String strsql="select operationtype from operation where operationcode=? ";
		        ArrayList list=new ArrayList();
		        list.add(operationcode);
		        ContentDAO dao = new ContentDAO(conn);
		        int flag = -1;
		        try {
		            rset = dao.search(strsql,list);
		            if (rset.next())
		                flag = rset.getInt("operationtype");
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        } finally {
		            if (rset != null) {
		                try {
		                    rset.close();
		                } catch (SQLException e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
		            }
		        }
		        
		        operationtypeMap.put(operationcode+"",flag+"");
		        operationtypeMap.put(operationcode+"_time",System.currentTimeMillis()+"");
		        
		        
		  } 
		  if(operationtypeMap.get(operationcode+"")!=null)
			  type=Integer.parseInt((String)operationtypeMap.get(operationcode));
		  return type;
	 }
	  
	  
	  
	  
	  
	  /**
		 * 求下一步|上一步变迁条件
		 * @param flag =1上一步    else 下一步
		 */
		public static  ArrayList getTransitionList(String tabid,int node_id,int flag,Connection conn)
		{
			 ArrayList translist=new ArrayList();
			 boolean readDatabase=false;
			 if(tabid==null||tabid.trim().length()==0)
				 tabid="";
			 String key=tabid+"_"+node_id;
			 if(transitionMap.get(key)==null)
			  		readDatabase=true;
			 else
			 {
			  		long writetime=Long.parseLong((String)transitionMap.get(key+"_time"));
			  		if(System.currentTimeMillis()-writetime>delayTime) //有效时间30秒
			  			readDatabase=true;
			 }
			  
			  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getTransitionList="+readDatabase);
			  if(readDatabase)
			  {    
			        ContentDAO dao=new ContentDAO(conn);
			        RowSet rset=null; 
			        try
			        {
			            StringBuffer strsql=new StringBuffer();
			            ArrayList list=new ArrayList();
			            if(flag==1)
			                strsql.append("select twt.*,twd.nodetype from t_wf_transition twt,t_wf_node twd  where twt.pre_nodeid=twd.node_id and  next_nodeid=? ");
			            else
			                strsql.append("select  twt.*,twd.nodetype from t_wf_transition twt,t_wf_node twd  where  twt.next_nodeid=twd.node_id and pre_nodeid=? ");
			            list.add(node_id);
			            if(tabid.length()>0){ //2013-12-18 邓灿 数据库中有脏数据时，会得到非发散节点的下级节点有多个
			                strsql.append(" and twt.tabid=? ");
			                list.add(tabid);
			            }
			            rset=dao.search(strsql.toString(),list);
			            while(rset.next())
			            {
			                WF_Transition trans=new WF_Transition();
			                trans.setPre_nodeid(rset.getInt("pre_nodeid"));
			                trans.setNext_nodeid(rset.getInt("next_nodeid"));
			                trans.setTabid((String)rset.getString("tabid"));
			                trans.setCondition(Sql_switcher.readMemo(rset,"condition"));
			                trans.setTran_id(rset.getInt("tran_id"));
			                translist.add(trans);
			            }
			            transitionMap.put(key+"",translist);
			            transitionMap.put(key+"_time",System.currentTimeMillis()+"");
			        }
			        catch(Exception ex)
			        {
			            ex.printStackTrace(); 
			        }
			        finally
					{
						PubFunc.closeDbObj(rset);
					}
				    
			       
			  }
			  if(transitionMap.get(key+"")!=null)
				  translist= (ArrayList)transitionMap.get(key);
			return translist;
		}
	  
	  
	  
	  
		/**
		 * 求外边框的区域
		 */
		public static  Rectangle getBorderRect(int tabid,int pageid,Connection conn)
		{
			 Rectangle rect=new Rectangle(0,0,0,0);
			  boolean readDatabase=false;
			  String key=tabid+"_"+pageid;
			  if(borderRectMap.get(key)==null)
			  		readDatabase=true;
			  else
			  {
			  		long writetime=Long.parseLong((String)borderRectMap.get(key+"_time"));
			  		if(System.currentTimeMillis()-writetime>delayTime) //有效时间30秒
			  			readDatabase=true;
			  }
			  
			  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getBorderRect="+readDatabase);
			  if(readDatabase)
			  {   
					StringBuffer sql=new StringBuffer();
					ContentDAO dao=new ContentDAO(conn);
					
					RowSet rset=null;		
					try
					{
						sql.append("select min(rtop) as ltop,min(rleft) as lleft,");
						sql.append("max(rtop+rheight) as dtop,max(RLeft + RWidth) as dleft from template_set ");
						sql.append(" where tabid=? ");
						sql.append(" and pageid=? ");
						ArrayList list=new ArrayList();
						list.add(tabid);
						list.add(pageid);
						rset=dao.search(sql.toString(),list);
						if(rset.next())
						{
							int x=rset.getInt("lleft");
							int y=rset.getInt("ltop");
							int width=rset.getInt("dleft")-x;
							int height=rset.getInt("dtop")-y;
							rect.setRect(x,y,width,height);
						}
						
						borderRectMap.put(key+"",rect);
						borderRectMap.put(key+"_time",System.currentTimeMillis()+"");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						PubFunc.closeDbObj(rset);
					}
				    
			       
			  }
			  if(borderRectMap.get(key+"")!=null)
				  rect= (Rectangle)borderRectMap.get(key);
			return rect;
		}
	  
	  
	  
	  
	  /**
	   * 获得模板所有私有和公有临时变量
	   * @param tabId 模板ID
	   * @param enforce 是否强行读数据库
	   * @param conn
	   * @return
	   */
	  public static String getConstantStr(String str,Connection conn )
	  {
		   
		  String strxml="<?xml version='1.0' encoding='GB2312' ?>";
		  strxml+="<param>";
		  strxml+="</param>";	
		  
		  boolean readDatabase=false;
		  if(constantStrMap.get(str)==null)
		  		readDatabase=true;
		  else
		  {
		  		long writetime=Long.parseLong((String)constantStrMap.get(str+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime/3) //有效时间5秒
		  			readDatabase=true;
		  }
		  
		  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getConstantStr="+readDatabase);
		  if(readDatabase )
		  {  
			   String xmlcontent="";
			   RowSet rset=null;
			   ArrayList paramList=new ArrayList();
			   StringBuffer strsql=new StringBuffer();
			   strsql.append("select str_value from constant where constant=? ");
			   paramList.add(str);
				ContentDAO dao=new ContentDAO(conn); 
				
				try
				{
					rset=dao.search(strsql.toString(),paramList);
				    if(rset.next())
						 strxml=Sql_switcher.readMemo(rset, "str_value");		
					 
				    constantStrMap.put(str+"",strxml);
				    constantStrMap.put(str+"_time",System.currentTimeMillis()+"");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				finally
		    	{
		    		PubFunc.closeDbObj(rset);
		    	}
		  } 
		  if(constantStrMap.get(str+"")!=null)
			  strxml=(String)constantStrMap.get(str);
		  return strxml;
	 }
	 /**
	  * 清除constantStrMap中缓存    bug 33959 wangb 20180115
	  * @param str 参数名
	  */
	 public static void  refreshConstantStr(String str){
		 if(constantStrMap.containsKey(str+"")){
			 constantStrMap.remove(str+"");
			 constantStrMap.remove(str+"_time");
		 }
		 
	 }
	   
	  
	  /**
	   * 获得模板所有私有和公有临时变量
	   * @param tabId 模板ID
	   * @param enforce 是否强行读数据库
	   * @param conn
	   * @return
	   */
	  public static HashMap getAllVariableHm( int tabId,Connection conn,boolean enforce )
	  {
		  
		  HashMap hm=new HashMap(); 
		  boolean readDatabase=false;
		  if(midvarHashMap.get(tabId+"")==null)
		  		readDatabase=true;
		  else
		  {
		  		long writetime=Long.parseLong((String)midvarHashMap.get(tabId+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime/3) //有效时间 5秒
		  			readDatabase=true;
		  }
		  
		  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getAllVariableHm="+readDatabase+"、"+enforce);
		  if(readDatabase||enforce)
		  {  
				StringBuffer strsql=new StringBuffer();
				ArrayList paramList=new ArrayList();
				ContentDAO dao=new ContentDAO(conn); 
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
					midvarHashMap.put(tabId+"",hm);
					midvarHashMap.put(tabId+"_time",System.currentTimeMillis()+"");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				finally
		    	{
		    		PubFunc.closeDbObj(rset);
		    	}
		  } 
		  if(midvarHashMap.get(tabId+"")!=null)
			  hm=(HashMap)midvarHashMap.get(tabId+"");
		  return hm;
	 }
	  
	  
	  /**
	   * 获得流程节点对象
	   * @param nodeid 节点id
	   * @param conn
	   * @return
	   */
	  public static RecordVo getWfNodeVo(int nodeid,Connection conn)
	  {
		  RecordVo vo=new RecordVo("t_wf_node");
		  boolean readDatabase=false;
		  if(NodesHashMap.get(nodeid+"")==null)
		  		readDatabase=true;
		  else
		  {
		  		long writetime=Long.parseLong((String)NodesHashMap.get(nodeid+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime)
		  			readDatabase=true;
		  }
		  Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getWfNodeVo="+readDatabase);
		  if(readDatabase)
		  { 
		    	ContentDAO dao=new ContentDAO(conn); 
		        vo.setInt("node_id",nodeid);
		        try
		        {
		            vo=dao.findByPrimaryKey(vo);
		            NodesHashMap.put(nodeid+"",vo);
		            NodesHashMap.put(nodeid+"_time",System.currentTimeMillis()+"");
		        }
		        catch(Exception ex)
		        {
		            ex.printStackTrace();
		        }
		  }
		  if(NodesHashMap.get(nodeid+"")!=null)
			  vo=(RecordVo)NodesHashMap.get(nodeid+"");
		  return vo;
		  
	  }
	  
	  
	  
	/**
	 * 获得模板信息
	 * @param tabid 模板ID
	 * @param conn
	 * @return
	 */
	  public static RecordVo getTableVo(int tabid,Connection conn) {
		  	RecordVo tab_vo=new RecordVo("Template_table");
		  
		  	boolean readDatabase=false;
		  	if(templateHashMap.get(tabid+"")==null)
		  		readDatabase=true;
		  	else
		  	{
		  		long writetime=Long.parseLong((String)templateHashMap.get(tabid+"_time"));
		  		if(System.currentTimeMillis()-writetime>delayTime)
		  			readDatabase=true;
		  	}
		  	Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getTableVo="+readDatabase);
		    if(readDatabase)
		    { 
		    	ContentDAO dao=new ContentDAO(conn);
		    	RowSet rowSet=null;
		    	try
		    	{
		    		String sql="select template_table.tabid,template_table.name,template_table.noticeid,template_table.gzstandid,template_table.flag";
		    		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) //昆仑数据库static为系统关键字
		    			sql+=",template_table.\"static\"";
		    		else if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
		    			sql+=",template_table.static_o";
		    		}else {
		    			sql+=",template_table.static";
		    		}	
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
		    			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
		    				tab_vo.setInt("static_o",rowSet.getInt("static_o"));
		    			}else {
		    				tab_vo.setInt("static",rowSet.getInt("static"));
		    			}
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
		    			
		    			templateHashMap.put(tabid+"",tab_vo);
		    			templateHashMap.put(tabid+"_time",System.currentTimeMillis()+"");
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
		    }
		    if(templateHashMap.get(tabid+"")!=null)
		    	tab_vo=(RecordVo)templateHashMap.get(tabid+"");
			
			return tab_vo;
	    }

	
	  
	  //-------------------------------------------------getPageCell start--------------------------------------------------------------------------
	  
	  /** 
	    * @Title: getPageCell 
	    * @Description: 返回指定页的所有单元格celllist
	    * @param @param tabId
	    * @param @param pagenum
	    * @param @return
	    * @return ArrayList
	    */ 
	    public static ArrayList getPageCell(int tabId,int pageNum,Connection conn) {
	    	
	    	
	    	ArrayList new_setbo=new ArrayList();
			boolean readDatabase=false;
			if (pageNum<=-1) {
	            	pageNum=-1;
	        }
		    if(pageCellMap.get(tabId+"_"+pageNum)==null)
			  		readDatabase=true;
		    else
		    {
			  		long writetime=Long.parseLong((String)pageCellMap.get(tabId+"_"+pageNum+"_time"));
			  		if(System.currentTimeMillis()-writetime>delayTime/2) //有效时间 15秒
			  			readDatabase=true;
		     }
			  
		     Category.getInstance("com.hjsj.hrms.module.template.utils.TemplateStaticDataBo").debug("getPageCell="+readDatabase);
		     if(readDatabase)
		     {  
	    	
	    	
					HashMap var_hm = new HashMap();
					if(getAllVariableHm(tabId, conn,false)!=null)
						var_hm=(HashMap)getAllVariableHm(tabId, conn,false);
			        
				    HashMap setMap=new HashMap();
					RowSet rset = null;
					try {
						
						String sql = "select a.*,b.ismobile from Template_Set a,template_page b where a.tabid=b.tabid and a.pageid=b.pageid and a.Tabid=? ";
						if (pageNum >-1) {
							sql = sql + "and a.Pageid=?";
						}
						sql = sql +" order by a.Pageid,a.rtop,a.rleft";//bug 40916 业务模板里批量修改多个指标时,指标顺序没按页排序
						ContentDAO dao = new ContentDAO(conn);
						ArrayList sqlList = new ArrayList();
						sqlList.add(Integer.valueOf(tabId));
						if (pageNum > -1) {
							sqlList.add(Integer.valueOf(pageNum));
						}
						ArrayList setBoList = new ArrayList();
						rset = dao.search(sql, sqlList);
						while (rset.next()) {
							TemplateSet setBo=getTemplateSet(rset,var_hm);
							if(setBo!=null){
								int page_id=setBo.getPageId();
								if(setMap.containsKey(page_id)){
									ArrayList list=(ArrayList) setMap.get(page_id);
									list.add(setBo);
								}else{
									ArrayList list=new ArrayList();
									list.add(setBo);
									setMap.put(page_id, list);
								}
							}
						}
						
						//重新设置单元格四条边线
			            int b=0;
			            int l=0;
			            int r=0;
			            int t=0;
			            Iterator iter = setMap.entrySet().iterator();
			            while (iter.hasNext()) {
			            	Map.Entry entry = (Map.Entry) iter.next();
			            	setBoList=(ArrayList) entry.getValue();
			            	String page_id=String.valueOf(entry.getKey());
			            	ArrayList page_new_setBo=new ArrayList();
			            	if(setBoList!=null){
					            for(int i=0;i<setBoList.size();i++)
					            {
					                TemplateSet cur_setbo =(TemplateSet)setBoList.get(i);  
					                b=getRlineForList(setBoList,"b",cur_setbo.getB(),cur_setbo);
					                l=getRlineForList(setBoList,"l",cur_setbo.getL(),cur_setbo);
					                r=getRlineForList(setBoList,"r",cur_setbo.getR(),cur_setbo);
					                t=getRlineForList(setBoList,"t",cur_setbo.getT(),cur_setbo);
					                cur_setbo.setB(b);                  
					                cur_setbo.setL(l);
					                cur_setbo.setR(r);
					                cur_setbo.setT(t);
					                new_setbo.add(cur_setbo);
					                page_new_setBo.add(cur_setbo);
					            }
			            	}
				            pageCellMap.put(tabId+"_"+page_id,page_new_setBo);
				            pageCellMap.put(tabId+"_"+page_id+"_time",System.currentTimeMillis()+"");
			            }
			            
			            
			           
			            pageCellMap.put(tabId+"_"+pageNum,new_setbo);
			            pageCellMap.put(tabId+"_"+pageNum+"_time",System.currentTimeMillis()+"");
			            
			            
		
					} catch (SQLException e) {
						e.printStackTrace();
					}
					finally
			    	{
			    		PubFunc.closeDbObj(rset);
			    	}
		     }
		      
		     if(pageCellMap.get(tabId+"_"+pageNum)!=null)
		    	 new_setbo=(ArrayList)pageCellMap.get(tabId+"_"+pageNum);
		     
			return new_setbo;
	    }
	    
	    /**
	     * 重新取得线型，由于画线的原因
	     * @param list
	     * @param flag
	     * @param line
	     * @param cur_setbo//当前操作对象
	     * @return
	     */
	    private static int  getRlineForList(ArrayList list,String flag,int line,TemplateSet cur_setbo)
	    {
	        if(line==0)
	            return line;
	        else
	        {
	            float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
	            float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
	            float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
	            float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
	            TemplateSet setbo;  
	            float rtop=0;
	            float rheight=0;
	            float rleft=0;
	            float rwidth=0;
	            int b=0;
	            int t=0;
	            int r=0;
	            int l=0;
	            int cur_gridno=cur_setbo.getGridno();
	            int gridno=0;
	            try
	            {  
	                for(int i=0;i<list.size();i++)
	                {
	                    setbo=(TemplateSet)list.get(i);  
	                    rtop=setbo.getRtop();
	                    rheight=setbo.getRheight();
	                    rleft=setbo.getRleft();
	                    rwidth=setbo.getRwidth();
	                    gridno=setbo.getGridno();
	                    if (setbo.getPageId()!=cur_setbo.getPageId()){
	                        continue;
	                    }
	                    if(cur_gridno==gridno)
	                        continue;
	                    if("t".equals(flag))
	                    {
	                       b=setbo.getB();//得到每一个单元格的下部                    
	                       if(b==0)
	                       {
	                         if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
	                          {
	                             line=0;
	                             break;
	                          }
	                       }
	                    }else if("b".equals(flag))
	                    {
	                        t=setbo.getT();
	                        if(t==0)
	                        {
	                            if(rtop==(cur_rtop+cur_rheight)&&
	                                ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
	                                 (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
	                                )
	                              )
	                            {
	                                line=0;
	                                 break;
	                            }
	                        }                       
	                    }else if("l".equals(flag))
	                    {
	                        r=setbo.getR();
	                        if(r==0)
	                        {
	                            if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
	                            {
	                                line=0;
	                                break;
	                            }
	                        }                       
	                    }else if("r".equals(flag))
	                    {
	                        l=setbo.getL();
	                        if(l==0)
	                        {
	                            if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
	                            {
	                                line=0;
	                                break;
	                            }
	                        }
	                    }
	                }
	                
	            }catch(Exception e)
	            {
	                e.printStackTrace();
	            }
	        }       
	        return line; 
	    }
	 
	  
	  
	    /**
		 * 如果为null返回“”字符串
		 * @param value
		 * @return
		 */
		private static String nullToSpace(String value)
		{
			if(value==null)
				return "";
			else 
				return value;
		}
	  
	    /**
		 * 获得TemplateSet对象
		 * @param rset
		 * @param var_hm  模版相关的变量集
		 * @return
		 */
		private  static TemplateSet getTemplateSet(RowSet rset,HashMap var_hm)
		{
			TemplateSet setBo = new TemplateSet(); 
			try
			{ 
				setBo.setTabId(rset.getInt("tabid"));
				setBo.setPageId(rset.getInt("pageid"));
				setBo.setIsMobile(rset.getString("ismobile"));
				setBo.setHz(nullToSpace(rset.getString("hz")));// 设置表格的汉字描述
				setBo.setSetname(nullToSpace(rset.getString("setname")));// 设置子集的代码
				setBo.setCodeid(nullToSpace(rset.getString("codeid")));// 相关的代码类
				setBo.setField_hz(nullToSpace(rset.getString("Field_hz")));// 字段的汉字描述 取自业务字典
				setBo.setField_name(nullToSpace(rset.getString("Field_name")));// 指标的代码
				String flag = rset.getString("Flag") == null ? "" : rset.getString("Flag");// 数据源的标识（文本描述、照片......）
				setBo.setFlag(rset.getString("Flag"));// 设置数据源的标识
				String temp = rset.getString("subflag");// 子表控制符 0：字段 1：子集
				if (temp == null || "".equals(temp) || "0".equals(temp))
					setBo.setSubflag(false);
				else{
				    setBo.setSubflag(true);
				}
				setBo.setField_type(nullToSpace(rset.getString("Field_type")));
				setBo.setOld_fieldType(nullToSpace(rset.getString("Field_type")));
				setBo.setFormula(nullToSpace(Sql_switcher.readMemo(rset, "Formula")));// 设置字段的计算公式
				setBo.setAlign(rset.getInt("Align"));// 文字在单元格中的排列方式
				setBo.setDisformat(rset.getInt("DisFormat"));// 设置数据的格式

				if ("V".equalsIgnoreCase(flag)) {// 变量
					RecordVo vo = (RecordVo) var_hm.get(rset
							.getString("Field_name"));
					if (vo != null) {
						setBo.setDisformat(vo.getInt("flddec"));// 如果是临时变量
																// 么要根据临时变量表里面的小数位数来设置
						setBo.setVarVo(vo);
					}
				}
				setBo.setChgstate(rset.getInt("ChgState"));// 设置字段是变化前还是变化后
				setBo.setFonteffect(rset.getInt("Fonteffect"));// 设置字体效果
				setBo.setFontname(rset.getString("FontName"));// 设置字体名称
				setBo.setFontsize(rset.getInt("Fontsize"));// 设置字体大小
				setBo.setHismode(rset.getInt("HisMode"));// 设置历史定位方式
				if (Sql_switcher.searchDbServer() == 2)
					setBo.setMode(rset.getInt("Mode_o"));
				else
					setBo.setMode(rset.getInt("Mode"));// 多条记录的时候 那几种选择
				// (最近..最初..)
				setBo.setNsort(rset.getInt("nSort"));// 相同指示顺序号
				setBo.setGridno(rset.getInt("gridno"));// 单元格号
				setBo.setRcount(rset.getInt("Rcount"));// 记录数 和HisMode
				// 配合试用（标识最近（Rcount条））
				setBo.setRheight(rset.getInt("RHeight"));// 设置单元格高度
				setBo.setRleft(rset.getInt("RLeft"));// 单元格左边的坐标值
				setBo.setRwidth(rset.getInt("RWidth"));// 单元格的宽度
				setBo.setRtop(rset.getInt("RTop"));// 单元格上边坐标值
				setBo.setL(rset.getInt("L"));
				/** LBRT 代表着表格左下右上是否有线 **/
				setBo.setB(rset.getInt("B"));
				setBo.setR(rset.getInt("R"));
				setBo.setT(rset.getInt("T"));

				if (rset.getInt("yneed") == 0)
					setBo.setYneed(false);
				else
					setBo.setYneed(true);
				String sub_domain = Sql_switcher.readMemo(rset, "sub_domain");
				setBo.setXml_param(sub_domain);
		
				if (rset.getString("nhide") != null)
					setBo.setNhide(rset.getInt("nhide"));
				else
					setBo.setNhide(0);// 打印还是隐藏 0：打印 1：隐藏
				
				// 普通指标需检查是否构库 未构库的过滤掉
				if ( setBo.isABKItem() && !setBo.isSpecialItem()){
					if (!setBo.isSubflag()) {
					    FieldItem item = DataDictionary.getFieldItem(setBo.getField_name());
	                    if (item == null) {// 数据字典里为空
	                        //return null; 
	                    }
	                    else {
	                    	//屏蔽 给出提示
	                       // setBo.setCodeid(nullToSpace(item.getCodesetid()));//重新赋一遍，有时候不对template_set表中的codeid在指标发生变化后，没有更新。陈总事业演示库 wangrd 20160617
	                    }
					}
					else {
					    FieldSet fieldset=DataDictionary.getFieldSetVo(setBo.getSetname());
	                    if(fieldset==null)
	                    {
	                        return null; 
	                    }
	                    else {
	                    	setBo.setField_hz(fieldset.getFieldsetdesc());
	                    }
					}
				}else if(setBo.isSpecialItem()){//特殊字段 lis 20160706
					if("parentid".equals(setBo.getField_name()))//上级组织`单元名称`
						setBo.setCodeid(rset.getString("codeid"));
					else
						setBo.setCodeid("0");
				}
				
				if (setBo.isNeedChangeFieldType()) {
					setBo.setField_type("M");
				}
				if (setBo.isSubflag()){
				    setBo.setField_type("M");
				}
				if ("V".equalsIgnoreCase(flag)) {// 临时变量
					if(setBo.getVarVo()!=null){//如果模板中设置的临时变量在临时变量表中不存在  则不予计算了
						String codeid= setBo.getVarVo().getString("codesetid");
						setBo.setCodeid(codeid); 
					}
	            }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return setBo;
		}
		/**
		 * 用于保存完后实时给templateHashMap塞值，保证获取到的是保存后的值，因为缓存机制等待15秒有点长。
		 * @param tabid
		 * @param tab_vo
		 */
		public static void setTemplateHashMap(String tabid,RecordVo tab_vo) {
			templateHashMap.put(tabid+"",tab_vo);
			templateHashMap.put(tabid+"_time",System.currentTimeMillis()+"");
		}
	
	  
	  //---------------------------------------------  end --------------------------------------------------------------------------

}
