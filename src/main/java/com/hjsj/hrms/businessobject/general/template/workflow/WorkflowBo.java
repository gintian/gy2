package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class WorkflowBo {
	private Connection conn=null; 
	private int tabid=-1;
	private UserView userview;
	private TemplateTableBo tablebo=null;
	private RecordVo t_wf_relationVo=null;
	private String relation_id="";  //审批关系id
	private ArrayList translist=null;
//	private HashMap nodeInfoMap=null;
	private HashMap selfNodeInfoMap=new HashMap();
	private HashMap actorInfoMap=null;
	 
	private HashMap scope_fieldMap=new HashMap();//节点的接收范围
	private HashMap role_propertyMap=new HashMap(); //角色特征
	private String task_ids=null;
	
	public String getTask_ids() {
		return task_ids;
	}

	public void setTask_ids(String task_ids) {
		this.task_ids = task_ids;
	}

	public WorkflowBo(Connection con ,int tabid,UserView _userview)
	{
		this.conn=con;
		this.tabid=tabid;
		this.userview=_userview;
		try
		{
			tablebo=new TemplateTableBo(this.conn,this.tabid,this.userview);
			if(this.tablebo.getRelation_id()!=null&&this.tablebo.getRelation_id().trim().length()>0)
			{
				try
				{
					ContentDAO dao=new ContentDAO(this.conn);
					t_wf_relationVo=new RecordVo("t_wf_relation");
					this.relation_id=this.tablebo.getRelation_id();
					if(!"gwgx".equalsIgnoreCase(this.tablebo.getRelation_id()))  //岗位汇报关系
					{ 
						t_wf_relationVo.setInt("relation_id", Integer.parseInt(this.tablebo.getRelation_id()));
						t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
					}
				}
				catch(Exception ee)
				{
					
				}
			}
			this.translist=this.getTransitionList(tabid);
		//	this.nodeInfoMap=this.getNodeInfoMap(tabid); 20170726 影响性能，废弃
			this.actorInfoMap=this.getActorInfoMap(tabid);
		}
		catch(Exception e)
		{
			
		}
	}
	
    /**提供直接传TemplateTableBo的构造方法，因为此类初始比较慢
     * @param con
     * @param tbo
     * @param _userview
     */
    public WorkflowBo(Connection con ,TemplateTableBo tbo,UserView _userview)
    {
        this.conn=con;
        this.userview=_userview;
        try
        {
            tablebo=tbo;
            this.tabid=tablebo.getTabid();
            if(this.tablebo.getRelation_id()!=null&&this.tablebo.getRelation_id().trim().length()>0)
            {
                try
                {
                    ContentDAO dao=new ContentDAO(this.conn);
                    t_wf_relationVo=new RecordVo("t_wf_relation");
                    this.relation_id=this.tablebo.getRelation_id();
                    if(!"gwgx".equalsIgnoreCase(this.tablebo.getRelation_id()))  //岗位汇报关系
                    { 
                        t_wf_relationVo.setInt("relation_id", Integer.parseInt(this.tablebo.getRelation_id()));
                        t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
                    }
                }
                catch(Exception ee)
                {
                    
                }
            }
            this.translist=this.getTransitionList(tabid);
        //    this.nodeInfoMap=this.getNodeInfoMap(tabid); 20170726 影响性能，废弃
            this.actorInfoMap=this.getActorInfoMap(tabid);
        }
        catch(Exception e)
        {
            
        }
    }
    
	public WorkflowBo(Connection con,UserView _userview)
	{
		this.conn=con; 
		this.userview=_userview;
	}
	
	
	
	private WF_Node getNodeInfoById(String node_id)
	{
		WF_Node wf_node=null;
		if(selfNodeInfoMap.get(node_id)!=null) {
            wf_node=(WF_Node)selfNodeInfoMap.get(node_id);
        } else
		{
			wf_node=new WF_Node(Integer.parseInt(node_id),this.conn,this.tablebo);
			selfNodeInfoMap.put(node_id,wf_node); 
		} 
		return wf_node;
	}
	 
	
	
	/**
	 * 获得流程中本人 角色能处理的单子里的记录
	 * @param paramBean
	 * @param userView
	 * @return
	 */
	public ArrayList getRecordList(LazyDynaBean paramBean,UserView userView)
	{
		ArrayList list=new ArrayList();
		try
		{
			RowSet rowSet2=null;
			Document doc=null;
			Element element=null;
			
			ContentDAO dao=new ContentDAO(this.conn);
			String tabid=(String)paramBean.get("tabid");
			String task_id=(String)paramBean.get("task_id");
			String ins_id=(String)paramBean.get("ins_id");
			String actor_type=(String)paramBean.get("actor_type");
			LazyDynaBean abean=null;
			HashMap dataMap=new HashMap();
			if("5".equals(actor_type))//本人
			{
				
				String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
				sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0 and tt.a0100='"+userView.getA0100()+"' and lower(tt.basepre)='"+userView.getDbname().toLowerCase()+"'";
				rowSet2=dao.search(sql0);
				
				while(rowSet2.next())
				{
					abean=new LazyDynaBean(); 
					String node_id=rowSet2.getString("node_id");
					abean.set("seqnum",rowSet2.getString("seqnum"));
					abean.set("task_id",task_id);
					abean.set("ins_id",rowSet2.getString("ins_id"));
					abean.set("node_id",node_id);
					String special_node=rowSet2.getString("special_node")!=null?rowSet2.getString("special_node"):"0";
					if(dataMap.get(node_id+task_id)==null&&!"1".equals(special_node))
					{
						// bug9992 
						dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
						dataMap.put(node_id+task_id,"1");
					}
					
					String username=rowSet2.getString("username");
					if(username==null||username.trim().length()==0)
					{
						// bug9992 本人都能看到，不需要加usrname标志。wangrd 2015-06-10
						dao.update("update t_wf_task_objlink set username='"+userView.getDbname().toUpperCase()+userView.getA0100()+"' where seqnum='"+rowSet2.getString("seqnum")+"' and task_id="+rowSet2.getString("task_id"));
					}
					else if(!username.equalsIgnoreCase(userView.getDbname()+userView.getA0100())) {
                        continue;
                    }
					list.add(abean);
					
				}
				 
			} 
			if("2".equals(actor_type))//角色
			{
				String scope_field="";
				String containUnderOrg="0"; //包含下属机构
				String node_id=(String)paramBean.get("node_id");
				String actor_id =(String)paramBean.get("actorid");
				if(scope_fieldMap!=null&&scope_fieldMap.get(tabid+"_"+node_id)!=null)
				{
					String temp=(String)scope_fieldMap.get(tabid+"_"+node_id);
					scope_field=temp.split("`")[0];
					containUnderOrg=temp.split("`")[1];
				}
				else
				{
					
					rowSet2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
					String ext_param="";
					if(rowSet2.next()) {
                        ext_param=Sql_switcher.readMemo(rowSet2,"ext_param");
                    }
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);; 
						String xpath="/params/scope_field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);
						if(childlist.size()==0){
							xpath="/param/scope_field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
								{
									scope_field=element.getText().trim();
									if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim())) {
                                        containUnderOrg="1";
                                    }
								}
							}
						}
					}
					if(scope_field==null) {
                        scope_field="";
                    }
					scope_fieldMap.put(tabid+"_"+node_id,scope_field+"`"+containUnderOrg);
				}
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userview);
				
				
				if(scope_field.length()>0)
				{
					String sql0="select twt.*";
					if ("parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)){
						sql0=sql0+ ",tt."+scope_field;
					}
					sql0=sql0+ " from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
					sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"'   ";
					//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
					if(usernameList.size()>0){
						for(int i=0;i<usernameList.size();i++){
							sql0+=" or username='"+usernameList.get(i)+"' ";
						}
					}		
					sql0+= " ) ";
					{
						String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
						//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
						if (actor_id!=null && actor_id.length()>0){
							String role_property="";//角色特征
							if(role_propertyMap!=null&&role_propertyMap.get(actor_id)==null)
							{ 
								StringBuffer sql = new StringBuffer("select role_property from t_sys_role where role_id= '"+actor_id+"'");
								RowSet rset = dao.search(sql.toString());
								if (rset.next()){    	
									role_property= rset.getString("role_property");
								}
								role_propertyMap.put(actor_id,role_property);
							}
							else
							{
								role_property=(String)role_propertyMap.get(actor_id);
							}
								
							String filterField="";
							if ("1".equals(role_property)){//部门领导
								String e0122=this.userview.getUserDeptId();
								if (e0122!=null &&e0122.length()>0){
									operOrg="UN"+e0122;
								}
								else {
									operOrg="";
								}
							}
							else if ("6".equals(role_property)){//单位领导
								String b0110=this.userview.getUserOrgId();
								if (b0110!=null &&b0110.length()>0){
									operOrg="UN"+b0110;
								}
								else {
									operOrg="";
								}
							}
							
						}
						
						String codesetid="";
						boolean noSql=true;
						if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
						{ 
							if(scope_field.toUpperCase().indexOf("E0122")!=-1)
							{
								codesetid="UM";
								String value=getSubmitTaskInfo(task_id,"UM");
								if(value.length()>0)
								{ 
									scope_field="'"+value+"'"; 
								}
								else
								{
									noSql=false;
									 
								}
							}
							else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
							{
								codesetid="UN";  
								String value=getSubmitTaskInfo(task_id,"UN");
								if(value.length()>0)
								{
									scope_field="'"+value+"'"; 
								}
								else
								{
									noSql=false;
									 
								}
							}
						}
						else if ("parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)){//岗位与单位， 取得上级组织机构的代码类
							//随机取出一条记录，取出上级组织值，主要为了得到当前上级组织类型。20160819
							rowSet2=dao.search(sql0);
							codesetid="UN";
							if (rowSet2.next()){
								String value=rowSet2.getString(scope_field);
								CodeItem codeItem = AdminCode.getCode("UN", value);
								if (codeItem==null){
									codesetid="UM";
								}
								
							}
                        }
						else
						{
							String[] temps=scope_field.split("_");
							String itemid=temps[0].toLowerCase(); 
							
							FieldItem _item=DataDictionary.getFieldItem(itemid);
							codesetid=_item.getCodesetid();
						}
						if("UN`".equalsIgnoreCase(operOrg))
						{
							
						}
						else if(noSql)
						{
							
							if(operOrg!=null && operOrg.length() > 3)
							{
								StringBuffer tempSql = new StringBuffer(""); 
								String[] temp = operOrg.split("`");
								for (int i = 0; i < temp.length; i++) {
									if("1".equals(containUnderOrg))
									{
										tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");		
									}
									else
									{
										if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                                            tempSql.append(" or "+scope_field+"='" + temp[i].substring(2)+ "'");
                                        } else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                                            tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");
                                        }
									}
								}
								
								if(tempSql.length()==0)
								{
									if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制 2014-04-01 dengcan
									{
										if("UN".equalsIgnoreCase(codesetid))
										{
											if("1".equals(containUnderOrg)) {
                                                tempSql.append(" or "+scope_field+" like '"+userView.getUserOrgId()+"%'");
                                            } else {
                                                tempSql.append(" or "+scope_field+"='"+userView.getUserOrgId()+"'");
                                            }
										}
										else if ("UM".equalsIgnoreCase(codesetid)){
										    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
										        tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
										    }else{
										        tempSql.append(" or 1=2 ");
										    }
											
										}
									}
								}
								
								if(tempSql.toString().trim().length()==0) {
                                    tempSql.append(" or 1=2 ");
                                }
								
								sql0+=" and ( " + tempSql.substring(3) + " ) ";
							}
							else
							{
								if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
								{
									if("UN".equalsIgnoreCase(codesetid))
									{
										if("1".equals(containUnderOrg)) {
                                            sql0+=" and "+scope_field+" like '"+userView.getUserOrgId()+"%'";
                                        } else {
                                            sql0+=" and "+scope_field+"='"+userView.getUserOrgId()+"'";
                                        }
									}
									else if ("UM".equalsIgnoreCase(codesetid)){
									    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
									        sql0+=" and "+scope_field+" like '"+userView.getUserDeptId()+"%'";
									    }else{
									        sql0+=" and 1=2 ";
									    }
									}
								}
								else {
                                    sql0+=" and 1=2 ";
                                }
							}
						}
						else {
                            sql0+=" and 1=2 ";
                        }
					
					}
					
					rowSet2=dao.search(sql0);
					ArrayList valueList=new ArrayList();
					while(rowSet2.next())
					{
						abean=new LazyDynaBean();
						abean.set("seqnum",rowSet2.getString("seqnum"));
						abean.set("task_id",rowSet2.getString("task_id"));
						abean.set("ins_id",rowSet2.getString("ins_id"));
						abean.set("node_id",rowSet2.getString("node_id"));
						String username=rowSet2.getString("username");
						String special_node=rowSet2.getString("special_node")!=null?rowSet2.getString("special_node"):"0";
				/*
						if(dataMap.get(node_id+task_id)==null&&!special_node.equals("1"))
						{
							dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
							dataMap.put(node_id+task_id,"1");
						}
				 		
						
						if(username==null||username.trim().length()==0)
						{
							ArrayList tempList=new ArrayList();
							tempList.add(userView.getUserName());
							tempList.add(rowSet2.getString("seqnum"));
							tempList.add(new Integer(rowSet2.getString("task_id")));
							valueList.add(tempList);
						}
				*/		
						list.add(abean);
						
					}
					/*
					if(valueList.size()>0)
					{
						String sql="update t_wf_task_objlink set username=? where seqnum=? and task_id=?";
						dao.batchUpdate(sql,valueList );
					}*/
					
				}
				else
				{
					String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
					sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ";
					//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
					if(usernameList.size()>0){
						for(int i=0;i<usernameList.size();i++){
							sql0+=" or username='"+usernameList.get(i)+"' ";
						}
					}	
					sql0+=" ) ";
					rowSet2=dao.search(sql0);
					while(rowSet2.next())
					{
						abean=new LazyDynaBean();
						abean.set("seqnum",rowSet2.getString("seqnum"));
						abean.set("task_id",rowSet2.getString("task_id"));
						abean.set("ins_id",rowSet2.getString("ins_id"));
						abean.set("node_id",rowSet2.getString("node_id"));
						list.add(abean);
						
					}
					/*
					if(list.size()>0) //普通角色也抢单 2013-7-19 dengc
						dao.update("update t_wf_task_objlink set  username='"+userView.getUserName()+"' where task_id="+task_id+" and node_id="+node_id);
					*/
				}
			}
			if(rowSet2!=null) {
                rowSet2.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * 获得报批人所在的单位  或 部门
	 * @param task_id
	 * @param orgFlag UN:单位  UM：部门
	 * @return
	 */
	private String getSubmitTaskInfo(String task_id,String orgFlag)
	{
		String info="";
		ContentDAO dao=new ContentDAO(this.conn);
		String fielditem="e0122";
		if("UN".equalsIgnoreCase(orgFlag)) {
            fielditem="b0110";
        }
		RowSet rset=null;
		try
		{
			int ins_id=0;
			int node_id=0;
			String state="";
			String a0100="";//报批人的人员编号
		//	rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
			rset=dao.search("select ins_id,state,node_id from t_wf_task where task_id="+task_id);
			if(rset.next())
			{
				node_id=rset.getInt("node_id");
				ins_id=rset.getInt("ins_id");
				state=rset.getString("state");
			}
			if("07".equals(state))  //驳回
			{ 
				rset=dao.search("select a0100_1 from t_wf_task where node_id="+node_id+" and ins_id="+ins_id+" and state='08' and "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='5' order by task_id desc");
			}
			else {
                rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
            }
			if(rset.next()) {
                a0100=rset.getString(1);
            }
			if(a0100!=null&&a0100.trim().length()>0)
			{
				if(a0100.length()>3)
				{
					String dbpre=a0100.substring(0,3);
					boolean flag=false;
					ArrayList dblist=DataDictionary.getDbpreList();
					for(int i=0;i<dblist.size();i++)
					{
						if(((String)dblist.get(i)).equalsIgnoreCase(dbpre)) {
                            flag=true;
                        }
					}
					if(flag)
					{
						rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
						if(rset.next())
						{
							info=rset.getString(1);
						}
					} 
				}
				
				if(info.length()==0)
				{
					rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
					if(rset.next())
					{
						String _a0100=rset.getString("a0100");
						String _nbase=rset.getString("nbase");
						if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
						{
							a0100 = _nbase+_a0100;
							rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
							if(rset.next())
							{
								info=rset.getString(1);
							}
						}
						
					}
					
				}
				if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
					String dbpre=a0100.substring(0,3);
					fielditem="b0110";
					rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
					if(rset.next())
					{
						info=rset.getString(1);
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch(Exception e)
			{
				
			}
		}
		return info;
	}
	
	
	
	private  ArrayList getTransitionList(int tabid) throws GeneralException
	{
		ArrayList translist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer(); 
			strsql.append("select * from t_wf_transition where tabid="+tabid); 
			rset=dao.search(strsql.toString());
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch(Exception e)
			{
				
			}
		}
		return translist;		
	}
	
	/**
	 * 获得流程参与者信息
	 * @param tabid
	 * @return
	 */
	private HashMap getActorInfoMap(int tabid)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{ 
			WF_Actor wf_actor=null;
			StringBuffer strsql=new StringBuffer(); 
			strsql.append("select twa.* from t_wf_actor twa,t_wf_node twn where twa.node_id=twn.node_id and tabid="+tabid); 
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				int node_id=rset.getInt("node_id");
				wf_actor=new WF_Actor(rset.getString("actorid"),rset.getString("actor_type")) ;
				map.put(String.valueOf(node_id),wf_actor);
			 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch(Exception e)
			{
				
			}
		}
		return map;
	}
	
	
	/*
	private HashMap getNodeInfoMap(int tabid)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{ 
			StringBuffer strsql=new StringBuffer(); 
			strsql.append("select * from t_wf_node where tabid="+tabid); 
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				int node_id=rset.getInt("node_id");
				map.put(String.valueOf(node_id),new WF_Node(node_id,this.conn,this.tablebo));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception e)
			{
				
			}
		}
		return map;
	}
	*/
	
	/**
	 * 获得特殊角色下的对象
	 * @param roleId_str
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getSpecialRoleMap(String roleid,String role_property) throws GeneralException
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null; 
		 
			String nodename=ResourceFactory.getProperty("label.role.fleader");//直接领导	 
			String sql="";	
			String errorMsg = "当前操作用户没有定义审批关系中的直接领导!";
			String spGradeStr=role_property;
			if("13".equals(role_property))
			{ 
						spGradeStr="9,10,11,12";
						nodename=ResourceFactory.getProperty("label.role.allleader");//全部领导
						errorMsg = ResourceFactory.getProperty("general.template.wf_node.nosprelation")+ "!";
			}
			else if("10".equals(role_property))
			{ 
				nodename=ResourceFactory.getProperty("label.role.sleader");//主管领导(上上级)
				errorMsg = "当前操作用户没有定义审批关系中的主管领导!";
			}
			else if("11".equals(role_property))
			{ 
				nodename=ResourceFactory.getProperty("label.role.tleader");//第三级领导
				errorMsg = "当前操作用户没有定义审批关系中的第三级领导!";
			}
			else if("12".equals(role_property))
			{ 
				nodename=ResourceFactory.getProperty("label.role.ffleader");//第四级领导
				errorMsg = "当前操作用户没有定义审批关系中的第四级领导!";
			}
			
			if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
			{ 
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("type","@K");
				abean.set("value",this.userview.getUserPosId());
				abean.set("from_nodeid","reportNode"); 
				ArrayList tempList=getSuperPos_userList(abean,"human",role_property);
		//		if(!(role_property.equals("10")||role_property.equals("11")||role_property.equals("12")))
				if(!"13".equals(role_property))
				{ 
					if(tempList.size()>0) {
                        map.put(nodename+"`human", tempList);
                    }
				}
			}
			else
			{
			
				ArrayList tempList=new ArrayList();
				if("1".equals(t_wf_relationVo.getString("actor_type")))  //自助用户
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("type","P");
					abean.set("value",this.userview.getDbname()+this.userview.getA0100());
					abean.set("from_nodeid","reportNode"); 
					HashMap a_map=getSuperSql(Integer.parseInt(role_property),this.tablebo.getRelation_id(),abean);
					if(a_map.size()==0) {
                        throw new GeneralException(errorMsg);
                    }
					sql=(String)a_map.get("sql"); 
				//	sql="select * from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"'"; 
				}
				else if("4".equals(t_wf_relationVo.getString("actor_type"))) //业务用户
				{
							sql="select t_wf_mainbody.*,usergroup.groupname from t_wf_mainbody ,usergroup where usergroup.groupid=t_wf_mainbody.groupid  and  Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+this.userview.getUserName()+"'";
							sql+=" and SP_GRADE in ("+spGradeStr+")";
				}else{//未定义审批关系
					return map;
				}
				sql+=" order by SP_GRADE" ;
				rowSet=dao.search(sql); 
				LazyDynaBean abean=new LazyDynaBean();
				while(rowSet.next())
				{
							abean=new LazyDynaBean();
							String actor_type=rowSet.getString("actor_type"); //1:HUMAN 4:业务用户 
							abean.set("actor_type",actor_type);
							abean.set("spgrade",rowSet.getString("sp_grade")); 
							abean.set("node_id","human");
							if("1".equals(actor_type))
							{
								abean.set("a0101",rowSet.getString("a0101"));
								String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
								String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
								String e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
								if(b0110.length()>0) {
                                    abean.set("b0110",AdminCode.getCodeName("UN",b0110));
                                } else {
                                    abean.set("b0110","");
                                }
								if(e0122.length()>0) {
                                    abean.set("e0122",AdminCode.getCodeName("UM",e0122));
                                } else {
                                    abean.set("e0122","");
                                }
								if(e01a1.length()>0) {
                                    abean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
                                } else {
                                    abean.set("e01a1","");
                                }
							
							}
							else if("4".equals(actor_type))
							{
								if(rowSet.getString("a0101")==null||rowSet.getString("a0101").trim().length()==0) {
                                    abean.set("a0101",rowSet.getString("Mainbody_id"));
                                } else {
                                    abean.set("a0101",rowSet.getString("a0101"));
                                }
								int groupid=rowSet.getInt("groupid");  //用户组id
								String groupname=rowSet.getString("groupname");
								abean.set("groupname",rowSet.getString("groupname"));
							}
							abean.set("mainbodyid",rowSet.getString("Mainbody_id"));
							tempList.add(abean);
				}
				if(tempList.size()>0) {
                    map.put(nodename+"`human", tempList);
                }
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return map;
	}
	
	
	
	

	
	/**
	 * 获得标准职位汇报关系中的上级领导
	 * @param userview
	 * @return
	 */
	public ArrayList getSuperPos_userList(LazyDynaBean bean,String node_id,String role_property) throws GeneralException
	{
		ArrayList tempList=new ArrayList();
		try
		{ 
			String superpos_item="";
			RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR"); 
			superpos_item=vo.getString("str_value"); 
			if(bean==null||bean.get("type")==null||!"@K".equalsIgnoreCase((String)bean.get("type"))) {
                throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info8")+"!");
            }
			String e0122_value=(String)bean.get("value"); 
			if(e0122_value==null||e0122_value.trim().length()==0)
			{
				throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nopos")+"!");
			}
			if("13".equals(role_property))
			{
				throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info7")+"!");
			}
			//取得上级汇报岗位
			String post=getSuperPosition(e0122_value,superpos_item,Integer.parseInt(role_property)); 
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			 
			
			if(post==null||post.trim().length()==0) {
                throw new GeneralException(AdminCode.getCodeName("@K",e0122_value)+ResourceFactory.getProperty("general.template.wf_node.nodefinereportpos")+"!");
            }
			 
			 /**登录参数表*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String nbase_str="";
            if(login_vo!=null) {
                nbase_str = login_vo.getString("str_value");//.toLowerCase();
            }
            if(nbase_str==null||nbase_str.trim().length()==0) {
                throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nodefineloginbase")+"!");
            }
            String[] temps=nbase_str.split(",");
            LazyDynaBean abean=null;
            int num=0;
            for(int j=0;j<temps.length;j++)
            {
            	if(temps[j].trim().length()>0)
            	{
            		rowSet=dao.search("select * from "+temps[j].trim()+"A01 where e01a1='"+post+"'");
            		while(rowSet.next())
            		{
            			abean=new LazyDynaBean();
						abean.set("actor_type","1");
						abean.set("spgrade","9"); 
						abean.set("node_id",node_id);
						abean.set("a0101",rowSet.getString("a0101"));
						String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
						String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
						String e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
						if(b0110.length()>0) {
                            abean.set("b0110",AdminCode.getCodeName("UN",b0110));
                        } else {
                            abean.set("b0110","");
                        }
						if(e0122.length()>0) {
                            abean.set("e0122",AdminCode.getCodeName("UM",e0122));
                        } else {
                            abean.set("e0122","");
                        }
						if(e01a1.length()>0) {
                            abean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
                        } else {
                            abean.set("e01a1","");
                        }
						
						
						
						abean.set("mainbodyid",temps[j].trim()+rowSet.getString("a0100"));
						tempList.add(abean);
            		}
            	} 
            }
            if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw  GeneralExceptionHandler.Handle(e); 
		}
		return tempList;
	}
	
	
	
	
	
	/**
	 * 获得报批者各审批层级下的对象
	 * @param roleId_str
	 * @return
	 */
	public HashMap getSpecialRoleMap(String specialRoleNodeId,int ins_id,int task_id,String selfapply)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet0=null; 
			RowSet rowSet=null; 
			String[] temps=specialRoleNodeId.split("`");
			String nodeidstr="";
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0)
				{
					nodeidstr+=","+temps[i].trim();
				}
			} 
			String sql="select twn.nodename,twa.node_id,tsr.role_property,twn.ext_param from t_wf_actor twa,t_wf_node twn,t_sys_role tsr where twa.node_id=twn.node_id "
				+" and twa.actorid=tsr.role_id and twa.node_id in ("+nodeidstr.substring(1)+") order by role_property "; 
			rowSet0=dao.search(sql);
		 
			while(rowSet0.next())
			{
					String nodename=rowSet0.getString("nodename");
					String node_id=rowSet0.getString("node_id");
					String role_property=rowSet0.getString("role_property");
					String ext_param=Sql_switcher.readMemo(rowSet0,"ext_param");
					String spGradeStr=role_property;
					if("13".equals(role_property))
					{ 
						spGradeStr="9,10,11,12";
					}
					 
					LazyDynaBean _abean=getFromNodeid_role(ext_param,ins_id,dao,task_id,selfapply,"");
					
					if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
					{   
						ArrayList tempList=getSuperPos_userList(_abean,node_id,role_property);
					//	if(!(role_property.equals("10")||role_property.equals("11")||role_property.equals("12")))
						if(!"13".equals(role_property))
						{ 
							if(tempList.size()>0) {
                                map.put(nodename+"`"+node_id, tempList);
                            }
						}
					}
					else
					{
						
						ArrayList tempList=new ArrayList();
					
						if("1".equals(t_wf_relationVo.getString("actor_type")))  //自助用户
						{
							HashMap a_map=getSuperSql(Integer.parseInt(role_property),this.tablebo.getRelation_id(),_abean);
							if(a_map.size()==0) {
                                throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
                            }
							sql=(String)a_map.get("sql"); 
					//		sql="select * from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"'";
						}
						else if("4".equals(t_wf_relationVo.getString("actor_type"))) //业务用户
						{
							sql="select t_wf_mainbody.*,usergroup.groupname from t_wf_mainbody ,usergroup where usergroup.groupid=t_wf_mainbody.groupid  and  Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+(String)_abean.get("value")+"'";
							sql+=" and SP_GRADE in ("+spGradeStr+")" ;
						}else{//未定义审批关系
							continue;
						}
						sql+=" order by SP_GRADE" ;
						
						rowSet=dao.search(sql); 
						LazyDynaBean abean=new LazyDynaBean();
						while(rowSet.next())
						{
							abean=new LazyDynaBean();
							String actor_type=rowSet.getString("actor_type"); //1:HUMAN 4:业务用户 
							abean.set("actor_type",actor_type);
							abean.set("spgrade",rowSet.getString("sp_grade")); 
							abean.set("node_id",node_id);
							if("1".equals(actor_type))
							{
								abean.set("a0101",rowSet.getString("a0101"));
								String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
								String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
								String e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
								if(b0110.length()>0) {
                                    abean.set("b0110",AdminCode.getCodeName("UN",b0110));
                                } else {
                                    abean.set("b0110","");
                                }
								if(e0122.length()>0) {
                                    abean.set("e0122",AdminCode.getCodeName("UM",e0122));
                                } else {
                                    abean.set("e0122","");
                                }
								if(e01a1.length()>0) {
                                    abean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
                                } else {
                                    abean.set("e01a1","");
                                }
							
							}
							else if("4".equals(actor_type))
							{
								if(rowSet.getString("a0101")==null||rowSet.getString("a0101").trim().length()==0) {
                                    abean.set("a0101",rowSet.getString("Mainbody_id"));
                                } else {
                                    abean.set("a0101",rowSet.getString("a0101"));
                                }
								int groupid=rowSet.getInt("groupid");  //用户组id
								String groupname=rowSet.getString("groupname");
								abean.set("groupname",rowSet.getString("groupname"));
							}
							abean.set("mainbodyid",rowSet.getString("Mainbody_id"));
							tempList.add(abean);
						}
						if(tempList.size()>0) {
                            map.put(nodename+"`"+node_id, tempList);
                        }
					
					}
			}
			
			if(rowSet0!=null) {
                rowSet0.close();
            }
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	
	/**
	 * 获得特殊角色是来自哪个节点
	 * @param ext_param
	 * @return
	 */
	public LazyDynaBean getFromNodeid_role(String ext_param,int ins_id,ContentDAO dao,int taskid,String selfapply,String whl)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			if(ext_param!=null&&ext_param.length()>0)//&&ins_id!=0)
			{
				Document doc=null;
				Element element=null; 
				doc=PubFunc.generateDom(ext_param);; 
				String xpath="/params/special_role";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				element=(Element)findPath.selectSingleNode(doc);
				String from_nodeid="reportNode";
				String item_id="";
				RowSet rowSet=null;
				if(element!=null)
				{
					if(element.getAttributeValue("from_nodeid")!=null&&element.getAttributeValue("from_nodeid").trim().length()>0)	
					{
						from_nodeid=element.getAttributeValue("from_nodeid");
					} 
					if(element.getAttributeValue("item_id")!=null&&element.getAttributeValue("item_id").trim().length()>0)	
					{
						item_id=element.getAttributeValue("item_id");
					} 
				}
				
				
				if("reportNode".equalsIgnoreCase(from_nodeid))  //取自报批人
				{
					abean=getNodeBean("","",from_nodeid,item_id,"",dao,taskid,selfapply,""); 
				}
				else if("startNode".equalsIgnoreCase(from_nodeid)) //取自发起人
				{
					if(ins_id!=0)
					{
						rowSet=dao.search("select actor_type,actorid from t_wf_instance where ins_id="+ins_id);
						if(rowSet.next()) {
                            abean=getNodeBean(rowSet.getString("actorid"),rowSet.getString("actor_type"),from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        }
					}
					else
					{
						if(t_wf_relationVo!=null){
							if("1".equals(t_wf_relationVo.getString("actor_type"))){//自助
							    //abean=getNodeBean("1",this.userview.getDbname()+this.userview.getA0100(),from_nodeid,item_id,"",dao,taskid,selfapply,""); 
								abean=getNodeBean(this.userview.getDbname()+this.userview.getA0100(),"1",from_nodeid,item_id,"",dao,taskid,selfapply,""); 
							}
							else
							{
								//abean=getNodeBean("4",this.userview.getUserName(),from_nodeid,item_id,"",dao,taskid,selfapply,""); 
								abean=getNodeBean(this.userview.getUserName(),"4",from_nodeid,item_id,"",dao,taskid,selfapply,""); 
							}
						}
					}
				}
				else if("form".equalsIgnoreCase(from_nodeid)) //取自表单
				{
					abean=getNodeBean("","",from_nodeid,item_id,"",dao,taskid,selfapply,whl); 
				}
				else  //定义的节点
				{ 
					if(ins_id!=0)
					{
						rowSet=dao.search("select * from t_wf_task where task_id=(select   max(task_id)   from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  ins_id="+ins_id+" and node_id="+from_nodeid+" )");
						if(rowSet.next()) {
                            abean=getNodeBean(rowSet.getString("actorid"),rowSet.getString("actor_type"),from_nodeid,item_id,rowSet.getString("a0100"),dao,taskid,selfapply,"");
                        }
					}
					else
					{
						if(this.userview.getStatus()==4) {
                            abean=getNodeBean(this.userview.getDbname()+this.userview.getA0100(),"1",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        } else {
                            abean=getNodeBean(this.userview.getUserName(),"4",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        }
					}
				}
				
			}
			else
			{
				abean=getNodeBean("","","reportNode","","",dao,taskid,selfapply,""); 
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw  GeneralExceptionHandler.Handle(e); 
		}
		
		return abean;
	}
	
	
	/**
	 * 获得特殊角色节点取自信息
	 * @param actorid
	 * @param actor_type
	 * @param from_nodeid
	 * @param item_id
	 * @param a0100
	 * @param dao
	 * @param taskid
	 * @param tabid
	 * @return
	 */
	private LazyDynaBean getNodeBean(String actorid,String actor_type,String from_nodeid,String item_id,String a0100,ContentDAO dao,int taskid,String selfapply,String whl)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
		 
			if("startNode".equalsIgnoreCase(from_nodeid))  //取自发起人
			{
				abean.set("from_nodeid","startNode");
				
				
				if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
                {
                    item_id="E01A1";
                }
				if("self".equalsIgnoreCase(item_id))
				{
					
					abean.set("value",actorid);
					abean.set("type","P"); //对象
				}
				else
				{
					String _loguser="";
					if("1".equals(actor_type)) //自助
					{
						String loguser=ConstantParamter.getLoginUserNameField().toLowerCase(); 
						String dbpre=actorid.substring(0,3);
						RowSet rowSet=dao.search("select * from "+dbpre+"A01 where a0100='"+actorid.substring(3)+"'");
						if(rowSet.next()) {
                            _loguser=rowSet.getString(loguser);
                        }
						if(rowSet!=null) {
                            rowSet.close();
                        }
					}
					else {
                        _loguser=actorid;
                    }
					 UserView _userView=new UserView(_loguser,conn);
			    	 _userView.canLogin(false);
			    	 if("B0110".equalsIgnoreCase(item_id))
			    	 {
			    		 abean.set("value",_userView.getUserOrgId());
						 abean.set("type","UN"); //对象 
						 
			    	 }
			    	 else if("E0122".equalsIgnoreCase(item_id))
			    	 {
			    		 abean.set("value",_userView.getUserDeptId());
						 abean.set("type","UM"); //对象 
						
			    	 }
			    	 else if("E01A1".equalsIgnoreCase(item_id))
			    	 {
			    		 abean.set("value",_userView.getUserPosId());
						 abean.set("type","@K"); //对象 
			    	 } 
				}
				
			}
			else if("reportNode".equalsIgnoreCase(from_nodeid)) //取自报批人
			{
				
				
				abean.set("from_nodeid","reportNode"); 
				if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
				{
					abean.set("value",this.userview.getUserPosId());
					abean.set("type","@K"); //对象 
				}
				else
				{ 
					if(t_wf_relationVo!=null){
						if("1".equals(t_wf_relationVo.getString("actor_type"))){//自助
							abean.set("value",this.userview.getDbname()+this.userview.getA0100());
						}else{
							abean.set("value",this.userview.getUserName());
						}
						/*if(this.userview.getStatus()==4)
							abean.set("value",this.userview.getDbname()+this.userview.getA0100());
						else
							abean.set("value",this.userview.getUserName());*/
					}else{
						abean.set("value","");
					}
					abean.set("type","P"); //对象
				}
			}
			else if("form".equalsIgnoreCase(from_nodeid)) //取自表单
			{
				
				String tabname="templet_"+this.tabid;
				if("1".equals(selfapply)&&taskid==0) {
                    tabname="g_"+tabname;
                }
				if(taskid==0)
				{
					if("0".equals(selfapply))
					{
						tabname=this.userview.getUserName()+tabname; 
					}
		//			else
		//				tabname="g_"+tabname;
				} 
				
				if(whl!=null&&whl.trim().length()>0&&taskid!=0) {
                    tabname=this.userview.getUserName()+tabname;
                }
				 
				String sql="select * from "+tabname+" where 1=1 ";
				if(tabname.equalsIgnoreCase("templet_"+this.tabid)&&(whl==null||whl.trim().length()==0))
				{
					sql="select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
					sql+=" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+taskid+" and td.state<>3 )";
				}
				else if(tabname.equalsIgnoreCase(this.userview.getUserName()+"templet_"+this.tabid)||(whl!=null&&whl.trim().length()>0))
				{
					
					sql+=" and submitflag=1 ";
					if(whl!=null) //拆单操作需加上取数条件
                    {
                        sql+=whl;
                    }
					
				}
				else
				{
					sql+=" and lower(basepre)='"+userview.getDbname().toLowerCase()+"' and a0100='"+userview.getA0100()+"'";
				} 
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String itemid=item_id;
					if(itemid==null||itemid.trim().length()==0|| "people".equalsIgnoreCase(item_id)) //取自表单 但没选指标时默认为人员 20160630 邓灿
					{
						abean.set("type","P"); //对象
						abean.set("value",rowSet.getString("basepre")+rowSet.getString("a0100"));  
						abean.set("from",from_nodeid);  
					}
					else
					{
						String _itemid = itemid;
						String codesetid="";
						//判断此指标是否是临时变量，并且是否是关联UN,UM,@K
						String midCodesetid = this.checkIsMidVar(itemid);
						if(!"".equals(midCodesetid)) {
							codesetid = midCodesetid;
						}else {
							_itemid=itemid.substring(0,itemid.length()-2);
							FieldItem item=DataDictionary.getFieldItem(_itemid.toLowerCase());
							if(item!=null) {
                                codesetid=item.getCodesetid();
                            } else if("parentid".equalsIgnoreCase(_itemid)) //20160608 DENGCAN
                            {
                                codesetid="UM";
                            }
						}
						
						abean.set("value",rowSet.getString(itemid)!=null?rowSet.getString(itemid):"");
						abean.set("type",codesetid);  
						abean.set("from",from_nodeid);  
					}
				}
				
				
			}
			else //节点
			{
				abean.set("from_nodeid",from_nodeid);
				if("1".equals(actor_type)|| "4".equals(actor_type)) //自助|业务
				{
					abean.set("value",actorid);
					abean.set("type","P"); //对象
				}
				else
				{
					abean.set("value",a0100);
					abean.set("type","P"); //对象 
				}
				
				
				if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
				{
					String _actorid=(String)abean.get("value");
					String _loguser="";
					RowSet rowSet=dao.search("select * from operuser where lower(username)='"+_actorid.toLowerCase()+"'");
					if(rowSet.next())
					{
						_loguser=_actorid;
					}
					else
					{
						String loguser=ConstantParamter.getLoginUserNameField().toLowerCase(); 
						String dbpre=actorid.substring(0,3);
						rowSet=dao.search("select * from "+dbpre+"A01 where a0100='"+actorid.substring(3)+"'");
						if(rowSet.next()) {
                            _loguser=rowSet.getString(loguser);
                        }
					} 
					 UserView _userView=new UserView(_loguser,conn);
			    	 _userView.canLogin(false);
			    	abean.set("value",_userView.getUserPosId());
					abean.set("type","@K"); 
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
		
	}
	
	private String checkIsMidVar(String itemid) {
		String midcodesetid = "";
		RowSet rowset = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select codesetid from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 ");
			buf.append(" and (templetId ="+this.tabid+"  or cstate ='1') "); 
			buf.append(" and cname='"+itemid+"'"); 
			rowset = dao.search(buf.toString());
			if(rowset.next()) {
				String codesetid = rowset.getString("codesetid");
				if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)) {
					midcodesetid = codesetid;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowset);
		}
		return midcodesetid;
	}

	/**
	 * 获得下一流程节点
	 * @param task_id
	 * @param ins_id
	 * @param selfapply
	 * @return
	 */
	public String getNextNodeStr(int task_id,int ins_id,String selfapply) throws GeneralException
	{
		 
		StringBuffer str=new StringBuffer("");
		int flag =0;
		try
		{
		 
			RowSet rowSet=null;
			RowSet rowSet2=null;
			ContentDAO dao=new ContentDAO(this.conn);
			
			ArrayList nodelist= getNextNode(task_id,ins_id,selfapply);
			flag++;
			int n=0;
			StringBuffer singleStr=new StringBuffer("");
			for(int i=0;i<nodelist.size();i++)
			{
				WF_Node wf_node=(WF_Node)nodelist.get(i);
				String ext_param=wf_node.getExt_param();
				WF_Actor _wf_actor=wf_node.getWf_Actor(wf_node.getNode_id());
				if(_wf_actor!=null&& "2".equalsIgnoreCase(_wf_actor.getActortype()))
				{
					rowSet=dao.search("select * from t_sys_role where role_id='"+_wf_actor.getActorid()+"'");
					if(rowSet.next())
					{
						int role_property=rowSet.getInt("role_property");
						String role_name = rowSet.getString("role_name");
						if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
						{ 
							LazyDynaBean bean=getFromNodeid_role(ext_param,ins_id,dao,task_id,selfapply,"");
							String sql="";
							if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
							{
								if(bean==null||bean.get("type")==null||!"@K".equalsIgnoreCase((String)bean.get("type"))) {
                                    throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info8")+"!");
                                }
								String e01a1=(String)bean.get("value");
								String superpos_item=getPS_SUPERIOR_value();
								if(e01a1==null||e01a1.trim().length()==0)
								{
									throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nopos")+"!");
								} 
								if(role_property==13)
								{
									throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info7")+"!");
								} 
								String post=getSuperPosition(e01a1,superpos_item,role_property);//取得上级汇报岗位
								if(post==null||post.trim().length()==0) {
                                    throw new GeneralException(AdminCode.getCodeName("@K",e01a1)+ResourceFactory.getProperty("general.template.wf_node.nodefinereportpos")+"!");
                                }
								 /**登录参数表*/
					            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
					            String nbase_str="";
					            if(login_vo!=null) {
                                    nbase_str = login_vo.getString("str_value");//.toLowerCase();
                                }
					            if(nbase_str==null||nbase_str.trim().length()==0) {
                                    throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nodefineloginbase")+"!");
                                }
					            String[] temps=nbase_str.split(",");
					            int num=0;
					            for(int j=0;j<temps.length;j++)
					            {
					            	if(temps[j].trim().length()>0)
					            	{
					            		rowSet=dao.search("select count(*) from "+temps[j].trim()+"A01 where e01a1='"+post+"'");
					            		if(rowSet.next()) {
                                            num+=rowSet.getInt(1);
                                        }
					            	} 
					            }
					            if(num>1)
					            {
					            	str.append("`"+wf_node.getNode_id()+":1");	
									n++;
					            }
					            else if(num==1)
								{
					            	for(int j=0;j<temps.length;j++)
						            {
						            	if(temps[j].trim().length()>0)
						            	{
							            	rowSet2=dao.search("select a0100,a0101 from "+temps[j].trim()+"A01 where e01a1='"+post+"'");
											if(rowSet2.next())
											{ 
												singleStr.append(","+wf_node.getNode_id()+":"+temps[j]+rowSet2.getString("a0100")+"`"+rowSet2.getString("a0101"));
											}
											str.append("`"+wf_node.getNode_id()+":1");
						            	}
						            }
					            	
								}
					            else{
									throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info1")+"!");
								}
							}
							else
							{
								if(t_wf_relationVo==null) {
                                    throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info2")+"!");
                                }
								
						//		if(t_wf_relationVo.getString("actor_type").equals("1"))  //自助用户
								{
									HashMap a_map=getSuperSql(role_property,this.tablebo.getRelation_id(),bean);
									if(a_map.size()==0) {
                                        throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
                                    }
									String a_num=(String)a_map.get("num");
									String a_sql=(String)a_map.get("sql"); 
									if("multiple".equalsIgnoreCase(a_num))
									{
										str.append("`"+wf_node.getNode_id()+":1");	
										n++;
									}
									else if("single".equalsIgnoreCase(a_num))
									{ 
										rowSet2=dao.search(a_sql);
										if(rowSet2.next())
										{
											String a0101=rowSet2.getString("a0101"); 
											singleStr.append(","+wf_node.getNode_id()+":"+rowSet2.getString("mainbody_id")+"`"+a0101);
										}
										str.append("`"+wf_node.getNode_id()+":1");	
									} 
							}
							//bug 33829 多人报批拆单都报给了第一个人直接领导。1、来自表单。2、非业务申请。3、多个人。
							if("form".equals(bean.get("from"))&&(StringUtils.isNotBlank(task_ids)&&(!"0".equalsIgnoreCase(task_ids))||("0".equalsIgnoreCase(task_ids)&&"0".equals(selfapply)))){
								Boolean isClear=true;
								//业务办理起草状态查询报的单子是否只有一个人
								if("0".equalsIgnoreCase(task_ids)){
									String sqlStr="select Count(1) count from "+this.userview.getUserName()+"templet_"+this.tabid+" where submitflag=1";
									rowSet2 = dao.search(sqlStr);
									
									if(rowSet2.next()) {
										int count = rowSet2.getInt("count");
										if(count==1) {
                                            isClear=false;
                                        }
									}
								}	
								else{ 
								//进行中的任务查询是否有多个人。
									String sqlStr="select Count(1) count from "+"templet_"+this.tabid+" a left join t_wf_task_objlink b on a.seqnum=b.seqnum  where b.task_id in("+task_ids+") and b.submitflag=1 and b.state<>3 ";
									rowSet2 = dao.search(sqlStr);
									if(rowSet2.next()) {
										int count = rowSet2.getInt("count");
										if(count==1) {
                                            isClear=false;
                                        }
									}
								}
								if("0".equalsIgnoreCase(task_ids)){//起草状态报批才判断是否设置了拆单
									ArrayList whlList = tablebo.getSplitInstanceWhl();
			                        if (whlList.size() == 1 && ((String) whlList.get(0)).trim().length() == 0)// 不拆单
                                    {
                                        isClear=false;
                                    }
								}
								if(isClear){
									str.setLength(0);
									singleStr.setLength(0);
								}
							}
						/*		else if(t_wf_relationVo.getString("actor_type").equals("4")) //业务用户
								{
									  
										sql="select count(*)  from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
										if(role_property!=13)
											sql+=" and SP_GRADE="+role_property+" " ;
										else
											sql+=" and SP_GRADE in (9,10,11,12) " ;
										rowSet=dao.search(sql);
										if(rowSet.next())
										{
											if(rowSet.getInt(1)>1)
											{
												str.append("`"+wf_node.getNode_id()+":1");	
												n++;
											}
											else if(rowSet.getInt(1)==1)
											{
												sql="select *  from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
												if(role_property!=13)
													sql+=" and SP_GRADE="+role_property+" " ;
												else
													sql+=" and SP_GRADE in (9,10,11,12) " ;
												rowSet2=dao.search(sql);
												if(rowSet2.next())
												{
													String a0101=rowSet2.getString("a0101");
													if(t_wf_relationVo.getString("actor_type").equals("4"))
													{
														if(a0101==null||a0101.trim().length()==0)
															a0101=rowSet2.getString("Mainbody_id"); 
													}
													singleStr.append(","+wf_node.getNode_id()+":"+rowSet2.getString("mainbody_id")+"`"+a0101);
												}
												str.append("`"+wf_node.getNode_id()+":1");	
											}else{
												throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info1")+"!");
											}
											
										}else{
											throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info1")+"!");
										}
								}
								else{
									throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info2")+"!");
								}
								
								*/
							} 
						}
						else {
                            str.append("`"+wf_node.getNode_id()+":0");
                        }
					}
				}
				else
				{
					str.append("`"+wf_node.getNode_id()+":0");	
				}
			 	 
				
				}
			if(n==0&&singleStr.length()>0)
			{
				str.setLength(0);
				str.append("$$$"+singleStr.substring(1));
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			if(rowSet2!=null) {
                rowSet2.close();
            }
		}
		catch(Exception e)
		{
			//throw new GeneralException("该业务流程定义出现了问题!"); 
			if(flag==0){
				if(e.toString().indexOf(ResourceFactory.getProperty("general.template.workflowbo.info4"))!=-1) {
                    throw  GeneralExceptionHandler.Handle(e);
                } else {
                    throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info3")+"!");
                }
			}else {
                throw  GeneralExceptionHandler.Handle(e);
            }
		}
		if(str.length()>0) {
            return str.substring(1);
        }
		return str.toString();
	}
	
	
	public static void main(String[] args)
	{
		String umid="010203";
		for(int i=umid.length();i>0;i--)
		{
			String temp=umid.substring(0,i);
			System.out.println("=="+temp);
		}
	}
	
	/**
	 * 获得审批人sql
	 * @param role_property 审批层级
	 * @param relation_id   审批关系ID
	 * @param userView  
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getSuperSql(int role_property,String relation_id,LazyDynaBean bean) throws GeneralException
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset = null;
		HashMap objectmap = new HashMap();
		ArrayList dblist = new ArrayList();
		StringBuffer whl=new StringBuffer("");
		String sql = "";
		try
		{
			rset = dao.search("select * from dbname");
	        while (rset.next()) {
                dblist.add(rset.getString("pre").toLowerCase());
            }
			String type=(String)bean.get("type");
			String value=(String)bean.get("value")==null?"":(String)bean.get("value");
			/*if(value!=null&&value.length()>0)
			    value=value.toLowerCase();*/
			if("UM".equalsIgnoreCase(type)|| "UN".equalsIgnoreCase(type))
			{
				if(value!=null&&value.length()>0)
				{
					String codesetid = this.getCodeSetId(value);
					value=value.toLowerCase();
					for(int i=value.length();i>0;i--)
					{
						String temp=value.substring(0,i); 
						//判断value是单位值还是部门值  如果是部门值挨个缩减要判断缩减的值是不是单位，如果是则跳出
						if("UM".equalsIgnoreCase(type)&&"UM".equalsIgnoreCase(codesetid)) {
							String codesetid_=this.getCodeSetId(temp);
							if("UN".equalsIgnoreCase(codesetid_)) {
                                break;
                            }
							whl.append(",'"+type.toLowerCase()+temp+"'");
						}//【61434 】中水联：流程审批时未按制定流程进行，到了错误的人员身上
						//调整内容：是单位时，只取当前单位，是部门和岗位时，是取上级部门和岗位。
						else if	("UN".equalsIgnoreCase(codesetid)) {
							whl.append(",'un"+temp+"'");
							break;
						}
						else {
                            whl.append(",'"+type.toLowerCase()+temp+"'");
                        }
						/*if(type.equalsIgnoreCase("UM")){
							whl.append(",'un"+temp+"'");  //为了兼容 兼职单位 的情况，在查询部门时，也将单位的情况包容进去 liuzy 20150925
						}*/
					}
				}
			}
			else if("@K".equalsIgnoreCase(type)) {
                whl.append(",'@k"+value.toLowerCase()+"'");
            } else if("P".equalsIgnoreCase(type)) {
				//得到人员得岗位和部门
				if(value!=null&&value.length()>0)
				{
					if("1".equals(this.t_wf_relationVo.getString("actor_type"))) {
						RecordVo vo = new RecordVo(value.substring(0,3)+"A01");
						vo.setString("a0100",value.substring(3,value.length()));
						vo=dao.findByPrimaryKey(vo);
						String e0122=vo.getString("e0122");
						whl=this.getWhlsql(whl,e0122);
						String e01a1=vo.getString("e01a1");
						whl=this.getWhlsql(whl,e01a1);
						whl.append(",'"+value.toLowerCase()+"'");
					}
					else if("4".equals(this.t_wf_relationVo.getString("actor_type"))) {
						whl.append(",'"+value.toLowerCase()+"'");
					}
				}
			}
			 
			if(whl.length()==0) {
                whl.append(",'##'");
            }
			sql="select *  from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id) in ("+whl.substring(1).toLowerCase()+")";
			if(role_property!=13) {
                sql+=" and SP_GRADE="+role_property+"  " ;
            } else {
                sql+=" and SP_GRADE in (9,10,11,12) " ;
            }
			sql+=" order by object_id desc";
		
			int num=0;
			RowSet rowSet=dao.search(sql);
			String objectId="";
			while(rowSet.next())
			{
				String object_id=rowSet.getString("object_id");
				String mainbody_id=rowSet.getString("mainbody_id");
				if(mainbody_id.equalsIgnoreCase(value)) {
                    continue;
                }
				
				if("UN".equalsIgnoreCase(type)|| "UM".equalsIgnoreCase(type))
				{
					if(objectId.length()==0) {
                        objectId=object_id;
                    }
					if(object_id.equals(objectId)) //20140725 dengcan  报批给单据部门指标对应的上级领导时，如果部门为2级部门，报批时即使上级领导只有一个还弹出选择领导的提示窗口，
                    {
                        num++;
                    }
					if(object_id.length()>objectId.length())
					{
						objectId=object_id;
						num=1;
					}
				}else if("P".equalsIgnoreCase(type)) {
					ArrayList objectlist_p = new ArrayList();
					if(!objectmap.containsKey(object_id)) {
						objectlist_p.add(mainbody_id);
						objectmap.put(object_id,objectlist_p);
					}else {
						objectlist_p = (ArrayList)objectmap.get(object_id);
						objectlist_p.add(mainbody_id);
						objectmap.put(object_id,objectlist_p);
					}
				}
				else
				{
					objectId=object_id; 
					num++;
				}
			}
			if("P".equalsIgnoreCase(type)) {
				if(objectmap.isEmpty()) {
                    num=0;
                } else {
					boolean isHaveP = false;
					String key_K = "";
					String key_UM = "";
					for (Object key : objectmap.keySet()) {  
						if(!isHaveP&&(("1".equals(this.t_wf_relationVo.getString("actor_type"))&&dblist.contains(key.toString().substring(0,3).toLowerCase()))
								||"4".equals(this.t_wf_relationVo.getString("actor_type")))) {
							isHaveP = true;
							ArrayList objectlist_p = (ArrayList)objectmap.get(key);
							objectId = key.toString();
							num = objectlist_p.size();
						}
						else{
							if("@K".equalsIgnoreCase(key.toString().substring(0,2))) {
								if("".equals(key_K)) {
                                    key_K = key.toString();
                                }
								if(key_K.length()>0&&key.toString().length()>key_K.length()) {
									key_K = key.toString();
								}
							}else if("UM".equalsIgnoreCase(key.toString().substring(0,2))) {
								if("".equals(key_UM)) {
                                    key_UM = key.toString();
                                }
								if(key_UM.length()>0&&key.toString().length()>key_UM.length()) {
									key_UM = key.toString();
								}
							}
						}
			        } 
					if(!isHaveP) {
						if(key_K.length()>0) {
							ArrayList objectlist_p = (ArrayList)objectmap.get(key_K);
							objectId = key_K.toString();
							num = objectlist_p.size();
						}else if(key_UM.length()>0) {
							ArrayList objectlist_p = (ArrayList)objectmap.get(key_UM);
							objectId = key_UM.toString();
							num = objectlist_p.size();
						}
					}
				}
			}
			if(num==0) {
				return map;
			}
			
			String _sql="";
			if(num==1) {
                map.put("num","single");
            } else {
                map.put("num","multiple");
            }
			_sql="select * from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+objectId.toLowerCase()+"'"; 
			if(role_property!=13) {
                _sql+=" and SP_GRADE="+role_property+"  " ;
            } else {
                _sql+=" and SP_GRADE in (9,10,11,12) " ;
            }
			map.put("sql",_sql);
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw  GeneralExceptionHandler.Handle(e); 
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return map;
	}
	
	/**
	 * 获得机构编码对应的机构类型
	 * @param value
	 * @return
	 */
	private String getCodeSetId(String value) {
		String codesetid = "";
		CodeItem _item = AdminCode.getCode("UN", value);
		if(_item!=null) {
            codesetid = "UN";
        } else {
			_item = AdminCode.getCode("UM", value);
			if(_item!=null) {
                codesetid = "UM";
            } else {
				_item = AdminCode.getCode("@K", value);
				if(_item!=null) {
                    codesetid = "@K";
                }
			}	
		}
		return codesetid;
	}
	
	private StringBuffer getWhlsql(StringBuffer whl, String code) {
		for(int i=code.length();i>0;i--) {
			String temp_=code.substring(0,i);
			String codesetid_=this.getCodeSetId(temp_);
			if("UN".equalsIgnoreCase(codesetid_)) {
                break;
            } else if("UM".equalsIgnoreCase(codesetid_)) {
				if(whl.indexOf("'um"+temp_+"'")==-1) {
                    whl.append(",'um"+temp_+"'");
                }
			}else if("@K".equalsIgnoreCase(codesetid_)){
				if(whl.indexOf("'@k"+temp_+"'")==-1) {
                    whl.append(",'@k"+temp_+"'");
                }
			}
		}
		return whl;
	}
	
	
	/**
	 * 获得当前岗位的汇报职位
	 * @param e0122_value  岗位id
	 * @param super_pos    上级岗位指标
	 * @param role_property  9\10\11\12\13
	 * @return
	 */
	public String getSuperPosition(String e0122_value,String super_pos,int role_property)
	{
		String pos="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String sql="";
			if(role_property==9) {
                sql="select "+super_pos+" from k01 where e01a1='"+e0122_value+"'";
            } else if(role_property==10) {
                sql="select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1='"+e0122_value+"')";
            } else if(role_property==11) {
                sql="select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1='"+e0122_value+"'))";
            } else if(role_property==12) {
                sql="select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1=(select "+super_pos+" from k01 where e01a1='"+e0122_value+"')))";
            }
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0) {
                    pos=rowSet.getString(1);
                }
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pos;
	}
	
	/**
	 * 获得 汇报关系中 直接上级指标
	 * @return
	 */
	public String getPS_SUPERIOR_value()
	{
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
        if(vo==null) {
            return fieldItem;
        }
        String param=vo.getString("str_value");
        if(param==null|| "".equals(param)|| "#".equals(param)) {
            return fieldItem;
        }
		fieldItem=param;
		return fieldItem;
	}
	
	
	/**
	 * 将暂停状态的任务改成结束状态
	 * @param ins_id
	 * @param tabid
	 * @param opt 1:汇聚  2：发散
	 * @param node_id
	 */
	public void taskState_stopToEnd(int ins_id,int tabid,int node_id,int opt)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 
			String nodeid_str="";
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==node_id&&opt==1)
				{
					nodeid_str+=","+pre_nodeid;
				}
				else if(pre_nodeid==node_id&&opt==2)
				{
					nodeid_str+=","+nextid;
				}
			}
			if(nodeid_str.length()>0)
			{
				dao.update("update t_wf_task set task_state='5' where task_state='6' and ins_id="+ins_id+" and node_id in ("+nodeid_str.substring(1)+") ");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 碰到与汇聚获得可以继往下走的对象
	 * @param task_id
	 * @param ins_id
	 * @param opt 1:与汇聚  2：或汇聚
	 * @return
	 */
	public ArrayList getContinueObj_AND(int task_id,int ins_id,int tabid,WF_Node wf_node,int opt)
	{
		ArrayList list=new ArrayList();
		try
		{
	     	RowSet rowSet=null;
		//	RowSet rowSet2=null;
			RowSet rowSet0=null;
			double pass_rate=0;  //与汇聚通过率
			
			if(opt==1) //与汇聚
			{
				Document doc=null;
				Element element=null;
				String ext_param=wf_node.getExt_param();
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param);; 
					String xpath="/params/pass_rate";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist=findPath.selectNodes(doc);
					if(childlist.size()==0){
						xpath="/param/pass_rate";
						 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						 childlist=findPath.selectNodes(doc);
					}
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
							{
								pass_rate=Float.parseFloat(element.getText().trim())*0.01;
							}
						}
					}
				}
			}
			
			
			ContentDAO dao=new ContentDAO(this.conn);
		 
		    ArrayList alist=getSplitNodeList(wf_node.getNode_id(),ins_id,tabid,opt);
			StringBuffer startNodeStr=new StringBuffer(""); 
			String tempNodeid="";
			String splitNodeId="";
			for(int i=0;i<alist.size();i++)
			{
				ArrayList tempList=(ArrayList)alist.get(i);
				startNodeStr.append(","+(String)tempList.get(tempList.size()-1)); 
				tempNodeid=(String)tempList.get(tempList.size()-1);
			}
			
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==Integer.parseInt(tempNodeid))
				{
					splitNodeId=String.valueOf(pre_nodeid);
					break;
				}
			}
			int count=1;
			String sql0="select max(count) from t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and  node_id="+splitNodeId+" and tab_id="+tabid+" and ins_id="+ins_id;
			rowSet0=dao.search(sql0);
			if(rowSet0.next()) {
                count=rowSet0.getInt(1);
            }
	 
			ArrayList firstNodeList=getNodes_AndSplit(startNodeStr.substring(1),ins_id,tabid,dao,0,count);
			String str=",";
			for(int i=0;i<firstNodeList.size();i++)
			{
				str+=((String)firstNodeList.get(i)).split(":")[0]+",";
			} 
		 
			int n=0;
			int sub_n=0;
			HashSet subedTaskSet=new HashSet();
			StringBuffer noSubedFirstNode=new StringBuffer(","); 
			for(int i=0;i<alist.size();i++)
			{
				ArrayList tempList=(ArrayList)alist.get(i);
				String endid=(String)tempList.get(0);
				String firstid=(String)tempList.get(tempList.size()-1); 
				if(str.indexOf(","+firstid+",")!=-1)
				{
					n++;
					boolean isDelete=true;
					boolean isSub=false;
					boolean isNoOpt=false;
					
					
					String sub_sql=" select  * from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id+" and node_id="+endid+" and ( flag is null or flag<>1 )"; //当驳回遇到与|或汇聚，会将其前一节点的 count置为-1，避免产生错误
				//	sub_sql+="  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and lower(username)='"+this.userview.getUserName().toLowerCase()+"' ) )";//防止计算其他人记录
					sub_sql+=" and task_id=( select max(task_id) maxid   from t_wf_task_objlink ";
					sub_sql+=" where "+Sql_switcher.isnull("task_type","'1'")+"='1' and  node_id="+endid+" and tab_id="+tabid+" and ins_id="+ins_id+" )";
					sub_sql+=" and count>="+count;// bug 53241 驳回的单据再次报批遇到与发散不能正常报批
					rowSet0=dao.search(sub_sql);
					int temp_n=0;
					String _task_id="";
					while(rowSet0.next())
					{
						String state=rowSet0.getString("state")!=null?rowSet0.getString("state"):"0";
						_task_id=rowSet0.getString("task_id");
						if(!"3".equals(state)) {
                            isDelete=false;
                        }
						if("1".equals(state)){
							isSub=true;
						}
						if("0".equals(state)) {
                            isNoOpt=true;
                        }
						
						temp_n++;
					}
					if(isSub&&isNoOpt) {
                        isSub=false;
                    }
					/*
					 * 判断当前任务是否已处理过，可能存在重复审批情况。
					 * 如果已处理过 置为0 因为：君正集团有判断错误的情况，5条线只走了4条线，且审批意见有重复记录，猜测可能有多次执行的情况 wangrd 20151016 bug13227
					 * 上一版有问题，未考虑同一单据多个人处理的情况，如部门经理角色处理各自权限范围的人。
					*/
					/* 屏蔽 不能采用这种方法 ，或汇聚后跟或汇聚的情况 不对。20160715
					if (_task_id.equals(String.valueOf(task_id)) && isSub){
					    isSub=false;
                    }
                    */
					
					if(temp_n==0) {
                        isDelete=false;
                    }
					
					if(isSub||isDelete)
					{
						sub_n++;
						if(isSub)
						{
							subedTaskSet.add(_task_id);
						}
					}
					else
					{
						noSubedFirstNode.append(firstid+",");
					}
				}  
			} 
			RecordVo task_vo=null;
			int num=1;
	/*		if(opt==2)
			{
				
			}
			else*/
			{
				task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",task_id);
				task_vo=dao.findByPrimaryKey(task_vo);
				String currentnode=String.valueOf(task_vo.getInt("node_id"));
				WF_Node wf_node0=getNodeInfoById(String.valueOf(currentnode));//(WF_Node)this.nodeInfoMap.get(String.valueOf(currentnode));
				if(wf_node0.getNodetype()==4||wf_node0.getNodetype()==5||wf_node0.getNodetype()==6||wf_node0.getNodetype()==7|| "4".equals(task_vo.getString("bs_flag"))) {
                    num=0;
                }

			}
			if(pass_rate==0||pass_rate==1)  
			{
				if(n==sub_n+num)
				{
					StringBuffer _str=new StringBuffer("");
					for(Iterator t=subedTaskSet.iterator();t.hasNext();)
					{
						_str.append(","+(String)t.next());
					} 
					String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where "+Sql_switcher.isnull("task_type","'1'")+"='1' and tab_id="+tabid+" and ins_id="+ins_id ;
				/*
					if(opt==2) //或汇聚  xieguiquan 
					{
						sub_sql+=" and task_id in ("+task_id+") and (state is null or state=0 or state=1 )";
					}else  */
					{
						sub_sql+=" and task_id in ("+task_id+_str.toString()+") and (state is null or state=0 or state=1 )";	
					}
					rowSet0=dao.search(sub_sql);
					while(rowSet0.next())
					{
						list.add(rowSet0.getString("seqnum"));
					}
				}
			}
			else
			{
				 
				if((sub_n+num)*1.0/n>=pass_rate)
				{
					HashSet seqnumSet=new HashSet();
					StringBuffer _str=new StringBuffer("");
					for(Iterator t=subedTaskSet.iterator();t.hasNext();)
					{
						_str.append(","+(String)t.next());
					} 
					String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where "+Sql_switcher.isnull("task_type","'1'")+"='1' and tab_id="+tabid+" and ins_id="+ins_id ;
					sub_sql+=" and task_id in ("+task_id+_str.toString()+") and (state is null or state=0 or state=1 )"; 
					rowSet0=dao.search(sub_sql);
					while(rowSet0.next())
					{
						seqnumSet.add(rowSet0.getString("seqnum"));
					}
					
					PendingTask imip=new PendingTask();
					for(int i=0;i<alist.size();i++)
					{
						ArrayList tempList=(ArrayList)alist.get(i);
						String endid=(String)tempList.get(0);
						String firstid=(String)tempList.get(tempList.size()-1);
						if(noSubedFirstNode.indexOf(","+firstid+",")!=-1)
						{
								String tempStr=getBetweenNodes(Integer.parseInt(firstid),Integer.parseInt(endid));
								rowSet0=dao.search("select task_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  task_state='3' and task_id<>"+task_id+" and node_id in ("+tempStr+") and ins_id="+ins_id);
								while(rowSet0.next())
								{
									int _task_id=rowSet0.getInt(1);
									rowSet=dao.search("select seqnum from t_wf_task_objlink  where state<>3 and tab_id="+tabid+" and task_id="+_task_id+" and ins_id="+ins_id+"");
									while(rowSet.next()) {
                                        seqnumSet.add(rowSet.getString("seqnum"));
                                    }
									task_vo=new RecordVo("t_wf_task");
									task_vo.setInt("task_id",_task_id);
									task_vo=dao.findByPrimaryKey(task_vo);
									task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
									task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
									task_vo.setString("state","08"); //审批状态
									dao.updateValueObject(task_vo);
									
									String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(_task_id));
									String pendingType="业务模板"; 
									//将旧的代办信息置为已办状态  
									imip.updatePending("T",pendingCode,1,pendingType,this.userview);  
									dao.update("update t_wf_task_objlink set state=1,submitflag=1 where task_id="+_task_id+" and ( state is null or state=0 )");
								}
						}
					}
					
					for(Iterator t=seqnumSet.iterator();t.hasNext();)
					{
						list.add((String)t.next());
					}
				}
			}
			
			
			
			
			
		/*
			
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String seqnum=rowSet.getString("seqnum");
				
				int count=1;
				String sql0="select max(count) from t_wf_task_objlink where seqnum='"+seqnum+"' and node_id="+andSplitNodeId+" and tab_id="+tabid+" and ins_id="+ins_id;
				rowSet0=dao.search(sql0);
				if(rowSet0.next())
					count=rowSet0.getInt(1); 
				ArrayList firstNodeList=getNodes_AndSplit(startNodeStr.substring(1),ins_id,tabid,seqnum,dao,0,count);
 
				String str=",";
				for(int i=0;i<firstNodeList.size();i++)
				{
					str+=((String)firstNodeList.get(i)).split(":")[0]+",";
				}
				
			 
				StringBuffer endNodeStr=new StringBuffer("");
				for(int i=0;i<alist.size();i++)
				{
					ArrayList tempList=(ArrayList)alist.get(i);
					String endid=(String)tempList.get(0);
					String firstid=(String)tempList.get(tempList.size()-1);
					if(str.indexOf(","+firstid+",")!=-1)
					{
						endNodeStr.append(","+endid);
					} 
				} 
				
				ArrayList endNodeList_opted=getNodes_AndSplit(endNodeStr.substring(1),ins_id,tabid,seqnum,dao,1,count);
				if(firstNodeList.size()>0)
				{
					if(pass_rate==0||pass_rate==1)
					{
						if(firstNodeList.size()==(endNodeList_opted.size()+1))
							list.add(seqnum);
					}
					else
					{
				
						if((endNodeList_opted.size()+1)/firstNodeList.size()>=pass_rate)
						{
					
							int n=0;
							for(int i=0;i<alist.size();i++)
							{
								ArrayList tempList=(ArrayList)alist.get(i);
								String firstid=(String)tempList.get(0);
								String endid=(String)tempList.get(tempList.size()-1);
								if(str.indexOf(","+firstid+",")!=-1)
								{
									{
										String tempStr=getBetweenNodes(Integer.parseInt(firstid),Integer.parseInt(endid));
										ArrayList list2=getNodes_AndSplit(tempStr,ins_id,tabid,seqnum,dao,3,count);
										if(list2.size()==0)
										{
											
										}
										else
											n++;
									}
								}
							}
							if(n==1)
								list.add(seqnum);
							
							
							
						}
						
					} 
				} 
			}
			*/
			if(rowSet0!=null) {
                rowSet0.close();
            }
		
			if(rowSet!=null) {
                rowSet.close();
            }
			/*	if(rowSet2!=null)
				rowSet2.close(); */
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得起始节点中间的节点
	 * @param firstNodeid
	 * @param endNodeid
	 * @param tabid
	 * @return
	 */
	private String getBetweenNodes(int firstNodeid,int endNodeid)
	{
		StringBuffer node_str=new StringBuffer("");
		ArrayList list=new ArrayList();
		list.add(String.valueOf(firstNodeid));
		getBeteenNodeList(firstNodeid,endNodeid,list);
		list.add(String.valueOf(endNodeid));
		
		for(int i=0;i<list.size();i++)
		{
			node_str.append(","+(String)list.get(i));
		}
		return node_str.substring(1);
	}
	
	
	private void getBeteenNodeList(int firswtNodeid,int endNodeid,ArrayList list)
	{
		 
		for(int i=0;i<this.translist.size();i++)
		{
			WF_Transition trans=(WF_Transition)this.translist.get(i);
			int nextid=trans.getNext_nodeid();
			int pre_nodeid=trans.getPre_nodeid();
			if(firswtNodeid==pre_nodeid)
			{
				if(nextid==endNodeid) {
                    continue;
                }
				list.add(String.valueOf(nextid));
				getBeteenNodeList(nextid,endNodeid,list);
				
			}
		
		}
		
	}
	
	
	
	private ArrayList getNodes_AndSplit(String nodeid_str,int ins_id,int tabid ,ContentDAO dao,int opt,int count)
	{
		ArrayList list=new ArrayList();
		try
		{
			
			String sql="select node_id,state from t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id in ("+nodeid_str+") and tab_id="+tabid+" and ins_id="+ins_id;
			if(opt==0) {
                sql+=" and count>="+count+" order by count desc";
            } else
			{
				/*
				sql=" select t_wf_task_objlink.* from t_wf_task_objlink,(select max(task_id) maxid ,node_id nodeid from t_wf_task_objlink ";
				sql+=" where seqnum='"+seqnum+"' and node_id in ("+nodeid_str+") and tab_id="+tabid+" and ins_id="+ins_id+" group by node_id ) a ";
				sql+="  where task_id=a.maxid and node_id=a.nodeid and seqnum='"+seqnum+"' ";
				sql+=" and node_id in ("+nodeid_str+") and tab_id="+tabid+" and ins_id="+ins_id;
				if(opt==1)
					sql+=" and state=1 ";
				else if(opt==2)  //目前已废弃
					sql+=" and state is not null and state<>0";
				else if(opt==3)
					sql+=" and ( state is null or state=0  or state=2 ) ";
					*/
			}
			 
			RowSet rowSet=dao.search(sql);
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				String nodeid=rowSet.getString("node_id");
				if(map.get(nodeid)==null)
				{
					String _state=rowSet.getString("state")!=null?rowSet.getString("state"):"0"; 
					list.add((String)rowSet.getString("node_id")+":"+_state);
					map.put(nodeid,"1");
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	/**
	 * 获得汇聚对应的发散节点
	 * @param andJoinNodeid
	 * @param opt 1:与汇聚  2：或汇聚
	 * @return
	 */
	private ArrayList getNodeList(int splitNodeid,int opt)
	{
		ArrayList list=new ArrayList();
		try
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(pre_nodeid==splitNodeid)
				{
					ArrayList tempList=new ArrayList();
					tempList.add(String.valueOf(nextid)); 
					int flag=1; 
					
					getJoinNodeid(nextid,nextid,flag,tempList,opt);
					
				    list.add(tempList);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	

	/**
	 * 获得pre_nodeid 对应路由中与发散得第一个节点
	 * @param pre_nodeid
	 * @param tabid
	 * opt 1:与汇聚  2：或汇聚
	 * @return
	 */
	private void getJoinNodeid(int next_nodeid,int selfNodeid,int flag,ArrayList tempList,int opt)
	{ 
		if(flag==0) {
            return;
        }
		
		WF_Node wf_node=getNodeInfoById(String.valueOf(next_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(next_nodeid));
		if((wf_node.getNodetype()==5&&opt==1)||(wf_node.getNodetype()==7&&opt==2))  // 汇聚
		{
			if(flag==1)
			{
				 String nodeid=(String)tempList.get(0);
				 if(!nodeid.equals(String.valueOf(selfNodeid))) {
                     tempList.add(String.valueOf(selfNodeid));
                 }
				 flag=0;
			}
			else {
                flag--;
            }
		}
		if((wf_node.getNodetype()==4&&opt==1)||(wf_node.getNodetype()==6&&opt==2))       //发散
        {
            flag++;
        }
		
		if(flag!=0)
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int _pre_nodeid=trans.getPre_nodeid();
				if(_pre_nodeid==next_nodeid)
				{
					getJoinNodeid(nextid,_pre_nodeid,flag,tempList,opt);
					break;
				}
			} 
		}
	}
	
	
	
	
	/**
	 * 获得汇聚对应的发散节点
	 * @param andJoinNodeid
	 * @param opt 1:与汇聚  2：或汇聚
	 * @return
	 */
	private ArrayList getSplitNodeList(int JoinNodeid,int ins_id,int tabid,int opt)
	{
		ArrayList list=new ArrayList();
		try
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==JoinNodeid)
				{
					ArrayList tempList=new ArrayList();
					tempList.add(String.valueOf(pre_nodeid)); 
					int flag=1;
					WF_Node wf_node=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					if(opt==1)
					{
						if(wf_node.getNodetype()==5)       //与汇聚
                        {
                            flag++;
                        }
					}
					else if(opt==2)
					{
						if(wf_node.getNodetype()==7)       //或汇聚
                        {
                            flag++;
                        }
					}
					int _pre_nodeid=0;
					for(int n=0;n<this.translist.size();n++)
					{
						trans=(WF_Transition)this.translist.get(n);
						int _nextid=trans.getNext_nodeid();
						_pre_nodeid=trans.getPre_nodeid();
						if(_nextid==pre_nodeid)
						{ 
							break;
						}
					}
				    getSplitFirstNodeid(_pre_nodeid,pre_nodeid,tabid,flag,tempList,opt);
				    list.add(tempList);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	//取得发散节点下子节点的最大count
	public int getSplitMaxCount(int node_id,int tabid,int ins_id)
	{
		int count=1;
		RowSet rowSet=null;  
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer node_str=new StringBuffer("");
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int _pre_nodeid=trans.getPre_nodeid();
				if(_pre_nodeid==node_id)
				{
					node_str.append(","+nextid);
				}
			} 
			node_str.append(","+node_id);
			rowSet=dao.search("select max(count) from t_wf_task_objlink where  "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id in ("+node_str.substring(1)+") and tab_id="+tabid+" and ins_id="+ins_id);
			if(rowSet.next())
			{ 
					count=rowSet.getInt(1); 
					count++;
			}
			if("2".equalsIgnoreCase(this.tablebo.getReject_type())){
				int beginCount=TemplateStaticDataBo.getBeginCount(String.valueOf(ins_id), this.conn, String.valueOf(this.tablebo.getTabid()));
                if(beginCount>=count){
                    count=beginCount;
                }
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{
				
			}
		}
		
		return count;
	}
	
	
	
	/**
	 * 获得pre_nodeid 对应路由中与发散得第一个节点
	 * @param pre_nodeid
	 * @param tabid
	 * opt 1:与汇聚  2：或汇聚
	 * @return
	 */
	private void getSplitFirstNodeid(int pre_nodeid,int self_nodeid,int tabid,int flag,ArrayList tempList,int opt)
	{ 
		if(flag==0) {
            return;
        }
		
		WF_Node wf_node=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
		if((wf_node.getNodetype()==4&&opt==1)||(wf_node.getNodetype()==6&&opt==2))  // 发散
		{
			if(flag==1)
			{
				 String nodeid=(String)tempList.get(0);
				 if(!nodeid.equals(String.valueOf(self_nodeid))) {
                     tempList.add(String.valueOf(self_nodeid));
                 }
				 flag=0;
			}
			else {
                flag--;
            }
		}
		if((wf_node.getNodetype()==5&&opt==1)||(wf_node.getNodetype()==7&&opt==2))       //汇聚
        {
            flag++;
        }
		
		if(flag!=0)
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int _pre_nodeid=trans.getPre_nodeid();
				if(nextid==pre_nodeid)
				{
					getSplitFirstNodeid(_pre_nodeid,nextid,tabid,flag,tempList,opt);
					break;
				}
			} 
		}
	}
	
	
	
	/**
	 * 根据任务号获得前一流程节点
	 * @param task_id
	 * @return
	 */
	public WF_Node getPreWF_Node(int task_id)
	{
		WF_Node wf_node=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			WF_Transition trans=null; 
			int _node_id=-1;
			if(task_id!=0)
			{
				rowSet=dao.search("select node_id from t_wf_task where task_id="+task_id);
				if(rowSet.next()) {
                    _node_id=rowSet.getInt(1);
                }
			}
			 
			for(int i=0;i<this.translist.size();i++)
			{
				trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==_node_id)
				{
					wf_node=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					break;
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return wf_node;
	}
	
	
	public HashMap getPreNode(int task_id,int ins_id,WF_Node wf_node)
	{
		HashMap nodeMap=new HashMap();
		RowSet rowSet=null; 
		RowSet rowSet2=null;
		String tabname="templet_"+this.tabid; 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list0=new ArrayList();
			WF_Transition trans=null;  
			ArrayList seqnumList=new ArrayList();
			if(wf_node.getNodetype()==4||wf_node.getNodetype()==6)
			{
				String nodeid=String.valueOf(wf_node.getNode_id());
				double pass_rate=0;  //与汇聚通过率
				String joinNodeid="";
				if(wf_node.getNodetype()==4) //与发散
				{  
					joinNodeid=getAndOrSplitNode(Integer.parseInt(nodeid),4,1,2);
					Document doc=null;
					Element element=null;
					WF_Node _wf_node=getNodeInfoById(joinNodeid);//(WF_Node)this.nodeInfoMap.get(joinNodeid);
					String ext_param=_wf_node.getExt_param();
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);; 
						String xpath="/params/pass_rate";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);
						if(childlist.size()==0){
							xpath="/param/pass_rate";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
								{
									pass_rate=Float.parseFloat(element.getText().trim())*0.01;
								}
							}
						}
					}
				} 
				
				if(pass_rate==0) {
                    pass_rate=1;
                }
				
				
				
				StringBuffer startNodeStr=new StringBuffer("");
				for(int i=0;i<this.translist.size();i++)
				{
					trans=(WF_Transition)this.translist.get(i);
					int nextid=trans.getNext_nodeid();
					int pre_nodeid=trans.getPre_nodeid();
					if(pre_nodeid==Integer.parseInt(nodeid))
					{
						startNodeStr.append(","+nextid);
					}
				}
				 
				int count=1;
				String sql0="select max(count) from t_wf_task_objlink where  "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id="+nodeid+" and tab_id="+tabid+" and ins_id="+ins_id;
				rowSet=dao.search(sql0);
				if(rowSet.next()) {
                    count=rowSet.getInt(1);
                }
			
				int currentTaskCount=1;
				if(wf_node.getNodetype()==4||wf_node.getNodetype()==6)
				{
					currentTaskCount=count; 
				}
				else
				{
					sql0="select count from t_wf_task_objlink where   task_id="+task_id+" and tab_id="+tabid+" and ins_id="+ins_id;
					rowSet=dao.search(sql0);
					if(rowSet.next()) {
                        currentTaskCount=rowSet.getInt(1);
                    }
				}
				
		 
				ArrayList firstNodeList=getNodes_AndSplit(startNodeStr.substring(1),ins_id,tabid,dao,0,count);
				String str=",";
				for(int i=0;i<firstNodeList.size();i++)
				{
					str+=((String)firstNodeList.get(i)).split(":")[0]+",";
				} 
			 
				ArrayList list=null;
				if(wf_node.getNodetype()==4) {
                    list=getNodeList(Integer.parseInt(nodeid),1);
                }
				if(wf_node.getNodetype()==6) {
                    list=getNodeList(Integer.parseInt(nodeid),2);
                }
				 
				
				int n=0;
				int sub_n=0;
				int no_optn=0;  //没处理过的分支
				HashSet subedTaskSet=new HashSet();
	 			 
				StringBuffer noSubedFirstNode=new StringBuffer(","); 
				for(int i=0;i<this.translist.size();i++)
				{
					trans=(WF_Transition)this.translist.get(i);
					int nextid=trans.getNext_nodeid();
					int pre_nodeid=trans.getPre_nodeid();
					if(pre_nodeid==Integer.parseInt(nodeid))
					{
						if(str.indexOf(","+nextid+",")!=-1)
						{
							n++;
							boolean isDelete=true;
							boolean isSub=false;
							boolean isNoOpt=false;
							String sub_sql=" select  * from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id+" and node_id="+nextid;
							sub_sql+=" and task_id=( select max(task_id) maxid   from t_wf_task_objlink ";
							sub_sql+=" where  "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id="+nextid+" and tab_id="+tabid+" and ins_id="+ins_id+" )";
							rowSet=dao.search(sub_sql);
							int temp_n=0;
							String _task_id="";
							while(rowSet.next())
							{
								int state=rowSet.getInt("state");
								_task_id=rowSet.getString("task_id");
								if(state!=3) {
                                    isDelete=false;
                                }
								if(state==2) {
                                    isSub=true;
                                } else if(state==0) {
                                    isNoOpt=true;
                                }
								temp_n++;
							}
							
							if(temp_n==0) {
                                isDelete=false;
                            }
							
							if(isNoOpt) {
                                no_optn++;
                            }
							
							if(isSub||isDelete)
							{
								sub_n++;
								if(isSub)
								{
									subedTaskSet.add(_task_id);
									 
								} 
							}
							else
							{
								noSubedFirstNode.append(nextid+","); 
							}
							
						}  
					} 
				}
				
				
				if((pass_rate==0||pass_rate==1)&&(n==sub_n))
				{
					StringBuffer _str=new StringBuffer("");
				//	if(n==sub_n)
					{				
						for(Iterator t=subedTaskSet.iterator();t.hasNext();)
						{
							_str.append(","+(String)t.next());
						} 
						
						String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where "+Sql_switcher.isnull("task_type","'1'")+"='1' and tab_id="+tabid+" and ins_id="+ins_id ;
						sub_sql+=" and task_id in ("+_str.substring(1)+") and (state=2 )"; 
						rowSet=dao.search(sub_sql);
						while(rowSet.next())
						{
								seqnumList.add(rowSet.getString("seqnum"));
						}
					} 
					
				}
				else
				{
					
							
					if(((sub_n*1.0)/n>=pass_rate)||(((n-sub_n)*1.0)/n<pass_rate)||((pass_rate==0||pass_rate==1)&&(n!=sub_n)))
					{
						HashSet seqnumSet=new HashSet();
						StringBuffer _str=new StringBuffer("");
						for(Iterator t=subedTaskSet.iterator();t.hasNext();)
						{
							_str.append(","+(String)t.next());
						} 
						String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where "+Sql_switcher.isnull("task_type","'1'")+"='1' and tab_id="+tabid+" and ins_id="+ins_id ;
						sub_sql+=" and task_id in ("+_str.substring(1)+") and (state=2 )"; 
						rowSet=dao.search(sub_sql);
						while(rowSet.next())
						{
							seqnumSet.add(rowSet.getString("seqnum"));
						}
						
						 
						
						for(int i=0;i<list.size();i++)
						{
							ArrayList tempList=(ArrayList)list.get(i); 
							String pre_nodeid=(String)tempList.get(0);
							if(noSubedFirstNode.indexOf(","+pre_nodeid+",")!=-1)
							{
										String tempStr=getBetweenNodes(Integer.parseInt(pre_nodeid),Integer.parseInt((String)tempList.get(tempList.size()-1)));
										
										String sql="select seqnum from t_wf_task_objlink  where   tab_id="+tabid+" and task_id in ( ";
										sql+="select task_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and   task_id<>"+task_id+" and node_id in ("+tempStr+") and ins_id="+ins_id ;
										sql+=" ) and count>="+currentTaskCount+" and state<>3 and ins_id="+ins_id;
										rowSet=dao.search(sql);
										while(rowSet.next())
										{
												seqnumSet.add(rowSet.getString("seqnum"));
										}
										
										
										PendingTask imip=new PendingTask();
										rowSet=dao.search("select task_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  task_state='3' and task_id<>"+task_id+" and node_id in ("+tempStr+") and ins_id="+ins_id);
										while(rowSet.next())
										{
											int _task_id=rowSet.getInt(1);
									/*		rowSet2=dao.search("select seqnum from t_wf_task_objlink  where state<>3 and tab_id="+tabid+" and task_id="+_task_id+" and ins_id="+ins_id+"");
											while(rowSet2.next())
												seqnumSet.add(rowSet2.getString("seqnum"));*/
											RecordVo task_vo=new RecordVo("t_wf_task");
											task_vo.setInt("task_id",_task_id);
											task_vo=dao.findByPrimaryKey(task_vo);
											task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
											task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
											task_vo.setString("state","07"); //驳回状态
											dao.updateValueObject(task_vo);
											dao.update("update t_wf_task_objlink set state=2,submitflag=1 where task_id="+_task_id+" and ( state is null or state=0 )");
											list0.add(String.valueOf(task_vo.getInt("node_id")));
											
											String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(_task_id));//bug 39604 taskid没有加密
											String pendingType="业务模板"; 
											//将旧的代办信息置为已办状态  
											imip.updatePending("T",pendingCode,1,pendingType,this.userview);  
											
										 
										}
							}
						 
						}
						
						for(Iterator t=seqnumSet.iterator();t.hasNext();)
						{
							seqnumList.add((String)t.next());
						}
					}
				} 
			}
			else
			{
				
				String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id ;
				sub_sql+=" and task_id="+task_id+" and (state=2 )"; 
				rowSet=dao.search(sub_sql);
				while(rowSet.next())
				{
					seqnumList.add(rowSet.getString("seqnum"));
				}
				
			}
			
			
			ArrayList alist=new ArrayList(); 
			if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
			{
				 int opt=1 ; //与汇聚  2：或汇聚
				 if(wf_node.getNodetype()==7) {
                     opt=2;
                 }
				 alist=getSplitNodeList(wf_node.getNode_id(),ins_id,tabid,opt); 
			 
			}
			 
			
			 
			
			ArrayList preNodeList=new ArrayList(); //前一节点 			 
			for(int e=0;e<seqnumList.size();e++)
			{
				String seqnum=(String)seqnumList.get(e);
				ArrayList _nodeList=new ArrayList();
	 			
				getPreWFNode(String.valueOf(wf_node.getNode_id()),_nodeList,task_id,ins_id,seqnum); 
				if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
				{
							int opt=1;
							if(wf_node.getNodetype()==7) {
                                opt=2;
                            }
							ArrayList templist=getNodeList(opt,wf_node,ins_id,seqnum,alist); 
							_nodeList.addAll(templist);
				}
				 	 
				for(int i=0;i<_nodeList.size();i++)
				{ 
					String _node_id1=(String)_nodeList.get(i);
					if(nodeMap.get(_node_id1)==null)
					{
						ArrayList seqlist=new ArrayList();
						seqlist.add(seqnum);
						nodeMap.put(_node_id1,seqlist);
						preNodeList.add(_node_id1);
					}
					else
					{
						ArrayList seqlist=(ArrayList)nodeMap.get(_node_id1);
						seqlist.add(seqnum);
						nodeMap.put(_node_id1,seqlist);
						preNodeList.add(_node_id1);
					} 
				} 
			} 
			
			
			if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
			{  /*
				 for(int i=0;i<alist.size();i++)
				 {
						ArrayList tempList=(ArrayList)alist.get(i);
						String endid=(String)tempList.get(0);
						String firstid=(String)tempList.get(tempList.size()-1); 
						String tempStr=getBetweenNodes(Integer.parseInt(firstid),Integer.parseInt(endid));
						dao.update("update t_wf_task_objlink set flag=1 where  node_id in ("+tempStr+")  and tab_id="+tabid+" and ins_id="+ins_id+" "); 
				*/
			    //原来是把发散各条线上的节点flag全部置为1，目前只更改离或汇聚节点最近的节点。
			    for(int i=0;i<preNodeList.size();i++)
                 {
			            int  endid=wf_node.getNode_id();
                        String firstid=(String)preNodeList.get(i);
                        String tempStr=getBetweenNodes(Integer.parseInt(firstid),endid);
                        dao.update("update t_wf_task_objlink set flag=1 where  node_id in ("+tempStr+")  and tab_id="+tabid+" and ins_id="+ins_id+" "); 
                }
				
				 
			}
			else
			{
				String sql="update t_wf_task_objlink set flag=1 where  node_id="+wf_node.getNode_id()+"  and tab_id="+tabid+" and ins_id="+ins_id+" ";
				dao.update(sql);
			}
			for(int i=0;i<list0.size();i++)
			{
				
				dao.update("update t_wf_task_objlink set flag=1 where node_id="+(String)list0.get(i)+"    and tab_id="+tabid+" and ins_id="+ins_id);
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rowSet2!=null) {
                    rowSet2.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
			
		}
		return nodeMap;
	}
	
	
	
	private ArrayList getNodeList(int opt,WF_Node wf_node_temp,int ins_id,String seqnum,ArrayList alist)
	{
		ArrayList nodelist=new ArrayList();
		try
		{
			RowSet rowSet=null;
			RowSet rowSet0=null;
			ContentDAO dao=new ContentDAO(this.conn);  
			StringBuffer startNodeStr=new StringBuffer(""); 
			String tempNodeid="";
			String splitNodeId="";
			for(int f=0;f<alist.size();f++)
			{
				ArrayList tempList=(ArrayList)alist.get(f);
				startNodeStr.append(","+(String)tempList.get(tempList.size()-1)); 
				tempNodeid=(String)tempList.get(tempList.size()-1);
			} 
			for(int g=0;g<this.translist.size();g++)
			{
				WF_Transition atrans=(WF_Transition)this.translist.get(g);
				int anextid=atrans.getNext_nodeid();
				int apre_nodeid=atrans.getPre_nodeid();
				if(anextid==Integer.parseInt(tempNodeid))
				{
					splitNodeId=String.valueOf(apre_nodeid);
					break;
				}
			}
			int count=1;
			String sql0="select max(count) from t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and  node_id="+splitNodeId+" and tab_id="+tabid+" and ins_id="+ins_id;
		    rowSet=dao.search(sql0);
			if(rowSet.next()) {
                count=rowSet.getInt(1);
            }
			ArrayList firstNodeList=getNodes_AndSplit(startNodeStr.substring(1),ins_id,tabid,dao,0,count);
			String str=",";
			for(int h=0;h<firstNodeList.size();h++)
			{
				str+=((String)firstNodeList.get(h)).split(":")[0]+",";
			} 
			
			for(int i=0;i<alist.size();i++)
			{
				ArrayList tempList=(ArrayList)alist.get(i);
				String endid=(String)tempList.get(0);
				String firstid=(String)tempList.get(tempList.size()-1);
				if(str.indexOf(","+firstid+",")!=-1)
				{
						String tempStr=getBetweenNodes(Integer.parseInt(firstid),Integer.parseInt(endid));
						rowSet=dao.search("select task_id,node_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  task_state='4'    and node_id in ("+tempStr+") and ins_id="+ins_id);
						while(rowSet.next())
						{
							int node_id=rowSet.getInt("node_id");
							int task_id=rowSet.getInt("task_id"); 
							String sub_sql=" select  max(task_id) maxid from t_wf_task_objlink  where "+Sql_switcher.isnull("task_type","'1'")+"='1' and tab_id="+tabid+"  and  "+Sql_switcher.isnull("flag","0")+"<>1  and seqnum='"+seqnum+"'  and ins_id="+ins_id+" and node_id="+node_id;
							rowSet0=dao.search(sub_sql);
							if(rowSet0.next())
							{
								if(rowSet0.getInt(1)==task_id) {
                                    nodelist.add(String.valueOf(node_id));
                                }
							}
							
							
						}
						
						
					  
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return nodelist;
	}
	
	
	
	
	private void getPreWFNode(String nodeid,ArrayList list,int task_id,int ins_id,String seqnum)
	{
		WF_Node wf_node=getNodeInfoById(nodeid);//(WF_Node)this.nodeInfoMap.get(nodeid);
		if(wf_node.getNodetype()==4 ) //与发散节点
		{ 
			try
			{ 
				boolean isOk=true; 
				if(isOk)
				{
					for(int i=0;i<this.translist.size();i++)
					{
						WF_Transition trans=(WF_Transition)this.translist.get(i);
						int nextid=trans.getNext_nodeid();
						int pre_nodeid=trans.getPre_nodeid();
						if(nextid==wf_node.getNode_id())
						{ 
							WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
							if(wf_node2.getNodetype()==4 ) //与发散节点
							{
								break;  // 不支持
							}
							else if(wf_node2.getNodetype()==6 ) //或发散节点
							{
								break;  //不支持
							}
							else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
							{
								getPreWFNode(String.valueOf(pre_nodeid),list,task_id,ins_id,seqnum);
							}
							else
							{ 
									list.add(String.valueOf(pre_nodeid));
							} 
							break;
						}
					}
					
				} 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if(wf_node.getNodetype()==6) //或发散节点
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						break;  // 不支持
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						break;  //不支持
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getPreWFNode(String.valueOf(pre_nodeid),list,task_id,ins_id,seqnum);
					}
					else
					{ 
						list.add(String.valueOf(pre_nodeid));
					} 
					break;
				}
			}
		}
		else if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
		{
			
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid)); 
					/*
					if(wf_node2.getNodetype()==2&&actorInfoMap.get(pre_nodeid)==null ) //空节点
					{
						for(int j=0;j<this.translist.size();j++)
						{
							trans=(WF_Transition)this.translist.get(j);
							nextid=trans.getNext_nodeid();
							int _pre_nodeid=trans.getPre_nodeid();
							if(nextid==pre_nodeid)
							{
								wf_node2=(WF_Node)this.nodeInfoMap.get(String.valueOf(_pre_nodeid)); 
								pre_nodeid=_pre_nodeid;
								break;
							}
						}
					} */
					
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						
						break;  // 不支持
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						
						break;  //不支持
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getPreWFNode(String.valueOf(pre_nodeid),list,task_id,ins_id,seqnum);
					}
					else
					{ 
						if(isCopeNode(String.valueOf(pre_nodeid),task_id,ins_id,seqnum))
						{
							list.add(String.valueOf(pre_nodeid));
						//	if(wf_node2.getNodetype()==7)
						//		break;
						}
						 
					} 
				}
			} 
		} 
		else
		{
			list.add(nodeid);
		}
		
	}
	
	
	/**
	 * 获得 与或汇聚节点对应的发散节点
	 * opt: 1:找发散节点   2：找汇聚节点
	 */
	private String getAndOrSplitNode(int nodeid,int nodetype,int flag,int opt)
	{ 
		if(flag<0) {
            return "";
        }
		String _nodeid="";
		for(int i=0;i<this.translist.size();i++)
		{
			WF_Transition trans=(WF_Transition)this.translist.get(i);
			int nextid=trans.getNext_nodeid();
			int pre_nodeid=trans.getPre_nodeid();
			if(nextid==nodeid&&opt==1)
			{
			 
				WF_Node wf_node=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
				if(wf_node.getNodetype()==4&&nodetype==5)       //与汇聚
				{
					if(flag==1)
					{
						_nodeid=String.valueOf(pre_nodeid);
						break;
					}
					else
					{
						flag--;
						_nodeid=getAndOrSplitNode(pre_nodeid,nodetype,flag,opt);
					}
				}
				else if(wf_node.getNodetype()==6&&nodetype==7)       //或汇聚
				{
					if(flag==1)
					{
						_nodeid=String.valueOf(pre_nodeid);
						break;
					}
					else
					{
						flag--;
						_nodeid=getAndOrSplitNode(pre_nodeid,nodetype,flag,opt);
					}
				}
				else if(wf_node.getNodetype()==5&&nodetype==5)
				{
					flag++;
					_nodeid=getAndOrSplitNode(pre_nodeid,nodetype,flag,opt);
				}
				else if(wf_node.getNodetype()==7&&nodetype==7)
				{
					flag++;
					_nodeid=getAndOrSplitNode(pre_nodeid,nodetype,flag,opt);
				}
				else {
                    _nodeid=getAndOrSplitNode(pre_nodeid,nodetype,flag,opt);
                }
				
				break;
			}
			else if(pre_nodeid==nodeid&&opt==2)
			{
			 
				WF_Node wf_node=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
				if(wf_node.getNodetype()==5&&nodetype==4)       //与汇聚
				{
					if(flag==1)
					{
						_nodeid=String.valueOf(nextid);
						break;
					}
					else
					{
						flag--;
						_nodeid=getAndOrSplitNode(nextid,nodetype,flag,opt);
					}
				}
				else if(wf_node.getNodetype()==7&&nodetype==6)       //或汇聚
				{
					if(flag==1)
					{
						_nodeid=String.valueOf(nextid);
						break;
					}
					else
					{
						flag--;
						_nodeid=getAndOrSplitNode(nextid,nodetype,flag,opt);
					}
				}
				else if(wf_node.getNodetype()==4&&nodetype==4)
				{
					flag++;
					_nodeid=getAndOrSplitNode(nextid,nodetype,flag,opt);
				}
				else if(wf_node.getNodetype()==6&&nodetype==6)
				{
					flag++;
					_nodeid=getAndOrSplitNode(nextid,nodetype,flag,opt);
				}
				else {
                    _nodeid=getAndOrSplitNode(nextid,nodetype,flag,opt);
                }
				
				break;
			}
		}
		return _nodeid;
	}
	
	private int getAndOrSplitCount(int nodeid,int nodetype,String seqnum,int ins_id)
	{
		int count=1;
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn); 
			String _nodeid=getAndOrSplitNode(nodeid,nodetype,1,1);
			RowSet rowSet=dao.search("select count from t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and seqnum='"+seqnum+"' and ins_id="+ins_id+" and node_id="+_nodeid+" order by count desc");
			if(rowSet.next()) {
                count=rowSet.getInt("count");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 是否处理过该节点
	 */
	private boolean isCopeNode(String nodeid,int task_id,int ins_id,String seqnum)
	{
		boolean flag=false;
		RowSet rowSet=null;  
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn); 
			String sub_sql="select * from t_wf_task_objlink  where seqnum='"+seqnum+"' and tab_id="+tabid+" and  "+Sql_switcher.isnull("flag","0")+"<>1  and state<>3 and node_id="+nodeid+" and ins_id="+ins_id;
			sub_sql+=" and task_id=( select max(task_id) maxid   from t_wf_task_objlink ";
			sub_sql+=" where  "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id="+nodeid+" and tab_id="+tabid+" and ins_id="+ins_id+" ) and task_id not in (select task_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  node_id="+nodeid+" and task_state=4 and ins_id="+ins_id+") ";
			rowSet=dao.search(sub_sql);
			if(rowSet.next()) {
                flag=true;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} 
		return flag;
	}
	
	
	/**
	 * 
	 * @param task_id
	 * @param selfapply 员工自助申请标识 ＝1员工 ＝0业务员 
	 * @return
	 */
	public ArrayList getNextNode(int task_id,int ins_id,String selfapply,String whl)throws GeneralException
	{
		ArrayList nodeList=new ArrayList();
		RowSet rowSet=null; 
		String tabname="templet_"+this.tabid;
		if(task_id==0)
		{
			if("0".equals(selfapply)) {
                tabname=this.userview.getUserName()+tabname;
            } else {
                tabname="g_"+tabname;
            }
		}
		
		
		String sql="select * from "+tabname+" where 1=1 ";
		if(tabname.equalsIgnoreCase("templet_"+this.tabid))
		{
			sql="select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
			sql+=" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+task_id+" and td.state<>3 )";
		}
		else if(tabname.equalsIgnoreCase(this.userview.getUserName()+"templet_"+this.tabid))
		{
			sql+=" and submitflag=1";
			if(whl!=null&&whl.trim().length()>0) {
                sql+=whl;
            }
			
		}
		else
		{
			sql+=" and lower("+tabname+".basepre)='"+userview.getDbname().toLowerCase()+"' and "+tabname+".a0100='"+userview.getA0100()+"'";
		}
//		try
//		{
			ContentDAO dao=new ContentDAO(this.conn);
			int node_id=-1;
			if(task_id!=0)
			{
				try {
					rowSet=dao.search("select node_id from t_wf_task where task_id="+task_id);
				
				if(rowSet.next()) {
                    node_id=rowSet.getInt(1);
                }
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(node_id==-1)
			{
				WF_Node wf_node=new  WF_Node(this.conn);
				RecordVo vo=wf_node.getBeginNode(String.valueOf(this.tabid));
				node_id=vo.getInt("node_id");
			}
			WF_Node wf_node=new  WF_Node(node_id,this.conn);  
			if(wf_node.getNodetype()==4||wf_node.getNodetype()==6) //与|或发散节点
			{
				ArrayList transitionList=wf_node.getOutTransitionList();
				nodeList=getNextNodeList_trans(transitionList,tabname,sql,wf_node,ins_id,task_id,selfapply,whl); 
			}
			else
			{
				ArrayList nextNodeList=wf_node.getNextNodeList(null); //获得下一节点
				WF_Node nextnode=(WF_Node)nextNodeList.get(0);
				if(nextnode.getNodetype()==4||nextnode.getNodetype()==6) //与|或发散节点
				{
					ArrayList transitionList=nextnode.getOutTransitionList(); //获得变迁列表
					nodeList=getNextNodeList_trans(transitionList,tabname,sql,nextnode,ins_id,task_id,selfapply,whl); 
				}
				else if(nextnode.getNodetype()==5||nextnode.getNodetype()==7) //与|或汇聚节点
				{
					nodeList.add(nextnode);
				}
				else
				{ 
					try {
						rowSet=dao.search(sql);
					
					ArrayList seqnumList=new ArrayList();
					while(rowSet.next()) {
                        seqnumList.add(rowSet.getString("seqnum"));
                    }
					nextnode.setSeqnumList(seqnumList); 
					nodeList.add(nextnode);
					} catch (SQLException e) {
						e.printStackTrace();
					} 
					
				}
			}
 
		return nodeList;
	}
	
	
	
	/**
	 * 
	 * @param task_id
	 * @param selfapply 员工自助申请标识 ＝1员工 ＝0业务员 
	 * @return
	 */
	public ArrayList getNextNode(int task_id,int ins_id,String selfapply)throws GeneralException
	{
		ArrayList nodeList=new ArrayList();
		RowSet rowSet=null; 
		String tabname="templet_"+this.tabid;
		if(task_id==0)
		{
			if("0".equals(selfapply)) {
                tabname=this.userview.getUserName()+tabname;
            } else {
                tabname="g_"+tabname;
            }
		}
		
		
		String sql="select * from "+tabname+" where 1=1 ";
		if(tabname.equalsIgnoreCase("templet_"+this.tabid))
		{
			sql="select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
			sql+=" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+task_id+" and td.state<>3 )";
		}
		else if(tabname.equalsIgnoreCase(this.userview.getUserName()+"templet_"+this.tabid))
		{
			sql+=" and submitflag=1";
			
		}
		else
		{
			sql+=" and lower("+tabname+".basepre)='"+userview.getDbname().toLowerCase()+"' and "+tabname+".a0100='"+userview.getA0100()+"'";
		}
//		try
//		{
			ContentDAO dao=new ContentDAO(this.conn);
			int node_id=-1;
			if(task_id!=0)
			{
				try {
					rowSet=dao.search("select node_id from t_wf_task where task_id="+task_id);
				
				if(rowSet.next()) {
                    node_id=rowSet.getInt(1);
                }
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(node_id==-1)
			{
				WF_Node wf_node=new  WF_Node(this.conn);
				RecordVo vo=wf_node.getBeginNode(String.valueOf(this.tabid));
				node_id=vo.getInt("node_id");
			}
			WF_Node wf_node=new  WF_Node(node_id,this.conn);  
			if(wf_node.getNodetype()==4||wf_node.getNodetype()==6) //与|或发散节点
			{
				ArrayList transitionList=wf_node.getOutTransitionList();
				nodeList=getNextNodeList_trans(transitionList,tabname,sql,wf_node,ins_id,task_id,selfapply,""); 
			}
			else
			{
				ArrayList nextNodeList=wf_node.getNextNodeList(null); //获得下一节点
				WF_Node nextnode=(WF_Node)nextNodeList.get(0);
				if(nextnode.getNodetype()==4||nextnode.getNodetype()==6) //与|或发散节点
				{
					ArrayList transitionList=nextnode.getOutTransitionList(); //获得变迁列表
					nodeList=getNextNodeList_trans(transitionList,tabname,sql,nextnode,ins_id,task_id,selfapply,""); 
					//或发散后面某条线第一个节点为空的节点，暂时只往下判断一层。首体刘泽清提 2015-09-12
//					int len =nodeList.size();
//					for(int i=len-1;i>=0;i--)
//					{
//						wf_node=(WF_Node)nodeList.get(i);
//						if (wf_node.getNodetype()==2 && "".equals(nextnode.getActorid())){//空节点，继续往下查
//							ArrayList _nextNodeList =wf_node.getNextNodeList(null);
//							if (_nextNodeList.size()>0){
//								WF_Node _nextnode=(WF_Node)_nextNodeList.get(0);								
//								if(_nextnode.getNodetype()==4||_nextnode.getNodetype()==6) {//与|或发散节点
//									ArrayList _transitionList=_nextnode.getOutTransitionList(); //获得变迁列表
//									ArrayList _nodeList=getNextNodeList_trans(_transitionList,tabname,sql,_nextnode,ins_id,task_id,selfapply); 
//									nodeList.addAll(_nodeList);
//								}
//							}
//						}
//					}
				}
				else if(nextnode.getNodetype()==5||nextnode.getNodetype()==7) //与|或汇聚节点
				{
					nodeList.add(nextnode);
				}
				else
				{ 
					try {
						rowSet=dao.search(sql);
					
					ArrayList seqnumList=new ArrayList();
					while(rowSet.next()) {
                        seqnumList.add(rowSet.getString("seqnum"));
                    }
					nextnode.setSeqnumList(seqnumList); 
					nodeList.add(nextnode);
					} catch (SQLException e) {
						e.printStackTrace();
					} 
					
				}
			}

//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		return nodeList;
	}
	
	
	
	/**
	 * 获得报备条下单据的人员
	 * @param cond
	 * @param taskid
	 * @param selfapply
	 * @return
	 */
	public ArrayList getFilingObjs(String  sql,YksjParser yp,String cond,int taskid,int ins_id,String selfapply,ArrayList objList,String whl)
	{
		ArrayList seqList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);  
			cond=cond.replaceAll("%26lt;","<").replaceAll("%26gt;",">").trim();
			String strfilter="";
			if(cond!=null&&cond.trim().length()>0)
			{
				cond=filterConditionFormular(cond,this.userview,ins_id,taskid,selfapply,objList,whl);
				yp.run_where(cond);
				strfilter=yp.getSQL();
			} 
			RowSet rowSet=dao.search(sql+" and ("+strfilter+")");
			//判断是单位模板还是人事模板  lis 20160708 start
			String a0101=""; 
			if(this.tablebo.getInfor_type()==1) {
                a0101="a0101_1";
            } else {
                a0101="codeitemdesc_1";
            }
			if(this.tablebo!=null&&this.tablebo.getOperationtype()==0)//调入
			{
				if(this.tablebo.getInfor_type()==1) {
                    a0101="a0101_2";
                } else {
                    a0101="codeitemdesc_2";
                }
			}
			//lis 20160708 end
			while(rowSet.next())
			{
			
				seqList.add(rowSet.getString("seqnum")+"`"+rowSet.getString(a0101)); 
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seqList;
	}
	
	
	
	

	/**
	 * 判断变迁条件中是否包含“直接领导”，根据报批人的审批关系，程序判断替换是否为空
	 * @param formular
	 * @param selfapply 员工自助申请标识 ＝1员工 ＝0业务员 
	 * @param userView
	 * @return
	 */
	private String filterConditionFormular(String formular ,UserView userView,int ins_id,int taskid,String selfapply,ArrayList objList,String whl)throws GeneralException
	{ 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1)
			{ 
				
				int n=0;
				LazyDynaBean objBean=new LazyDynaBean();
				for(int i=0;i<objList.size();i++)
				{
					objBean=(LazyDynaBean)objList.get(i);
					if(objBean.get("special_role")!=null)
					{
						n++;
						break;
					} 
				}
				if(n==0)
				{
					objBean.set("from_nodeid", "reportNode");
				}
				
				LazyDynaBean bean=getFromNodeid_role(objBean,ins_id,dao,taskid,selfapply,whl);
				if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
				{ 
						if(formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1)
						{ 
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","10");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1)
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","9");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						
						if(formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1) //第三级领导
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","11");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						if(formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1) //第四级领导
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","12");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						
						
						
				}
				else
				{
					   
							if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(9,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(10,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(11,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(12,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							 
				}
				
			 
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return formular;
	}
	

	/**
	 * 获得特殊角色是来自哪个节点
	 * @param ext_param
	 * @return
	 */
	public LazyDynaBean getFromNodeid_role(LazyDynaBean objBean,int ins_id,ContentDAO dao,int taskid,String selfapply,String whl)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
				RowSet rowSet=null;
				String from_nodeid=(String)objBean.get("from_nodeid");
				String item_id=(String)objBean.get("item_id");  
				if("reportNode".equalsIgnoreCase(from_nodeid))  //取自报批人
				{
					abean=getNodeBean("","",from_nodeid,item_id,"",dao,taskid,selfapply,""); 
				}
				else if("startNode".equalsIgnoreCase(from_nodeid)) //取自发起人
				{
					if(ins_id!=0)
					{
						rowSet=dao.search("select actor_type,actorid from t_wf_instance where ins_id="+ins_id);
						if(rowSet.next()) {
                            abean=getNodeBean(rowSet.getString("actorid"),rowSet.getString("actor_type"),from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        }
					}
					else
					{
						if(this.userview.getStatus()==4) {
                            abean=getNodeBean(this.userview.getDbname()+this.userview.getA0100(),"1",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        } else {
                            abean=getNodeBean(this.userview.getUserName(),"4",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        }
					}
				}
				else if("form".equalsIgnoreCase(from_nodeid)) //取自表单
				{
					abean=getNodeBean("","",from_nodeid,item_id,"",dao,taskid,selfapply,whl); 
				}
				else  //定义的节点
				{ 
					if(ins_id!=0)
					{
						rowSet=dao.search("select * from t_wf_task where task_id=(select   max(task_id)   from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and  ins_id="+ins_id+" and node_id="+from_nodeid+" )");
						if(rowSet.next()) {
                            abean=getNodeBean(rowSet.getString("actorid"),rowSet.getString("actor_type"),from_nodeid,item_id,rowSet.getString("a0100"),dao,taskid,selfapply,"");
                        }
					}
					else
					{
						if(this.userview.getStatus()==4) {
                            abean=getNodeBean(this.userview.getDbname()+this.userview.getA0100(),"1",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        } else {
                            abean=getNodeBean(this.userview.getUserName(),"4",from_nodeid,item_id,"",dao,taskid,selfapply,"");
                        }
					}
				}
				
			 
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw  GeneralExceptionHandler.Handle(e); 
		}
		
		return abean;
	}
	
	
	
	
	/**
	 * 根据报备条件判断是否要计算临时变量
	 * @param tabname
	 * @param condList
	 */
	public  void  computeVarByCondition(String tabname,ArrayList condList,ArrayList fldvarlist,ArrayList fieldlist,String sql)
	{
		 try
		 {
			//判断条件是否含临时变量
			HashMap usedVarMap=new HashMap(); 
			FieldItem item=null;
			LazyDynaBean bean=null;
			for(int i=0;i<condList.size();i++)
			{
				bean=(LazyDynaBean)condList.get(i);
				String condition=(String)bean.get("cond");  
				if(condition!=null&&condition.trim().length()>0) //定义了变迁条件
				{
						 
					condition=condition.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
					for(int j=0;j<fieldlist.size();j++)
					{
						item=(FieldItem)fieldlist.get(j);
						if(condition.indexOf(item.getItemid())!=-1||condition.indexOf(item.getItemdesc())!=-1)
						{
										 usedVarMap.put(item.getItemid(),item);
						} 
					}   
				}
			}
			//自动计算临时变量
			if(usedVarMap.size()>0)
			{
				ArrayList varlist=new ArrayList();
				for(int j=0;j<fieldlist.size();j++)
				{
					 item=(FieldItem)fieldlist.get(j);
					 if(usedVarMap.get(item.getItemid())!=null) {
                         varlist.add(item);
                     }
				}
				if(this.tablebo.getInfor_type()==1)
				{ 
					this.tablebo.setBz_tablename(tabname);
					
					String strwhere="";
					if(sql.indexOf("where")!=-1) {
                        strwhere=sql.substring(sql.indexOf("where"));
                    }
					/**计算临时变量,把临时变量加到变动处理表中去*/
					/**应用库前缀*/
					
					Object[] dbarr=null; 
					ArrayList dblist=searchDBPreList(tabname);
					dbarr=dblist.toArray();	 
					this.tablebo.addMidVarIntoGzTable(" "+strwhere,dbarr,varlist);
				}
			}
			
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		
	}
	
	
	
	
	
	
	/**
	 * 获得符合条件的下一流程节点
	 * @param transitionList 
	 * @param selfapply 员工自助申请标识 ＝1员工 ＝0业务员 
	 * @return
	 */
	private ArrayList getNextNodeList_trans(ArrayList transitionList,String tabname,String sql,WF_Node node,int ins_id,int taskid,String selfapply,String whl) throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null; 
		HashMap existsMap=new HashMap();
		boolean flag = false;
		String names = "";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fldvarlist=this.tablebo.getAllFieldItem(); 
			ArrayList fieldlist=this.tablebo.getMidVariableList();
			fldvarlist.addAll(fieldlist);
			for(int i=0;i<fldvarlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fldvarlist.get(i);
				if(fielditem.getVarible()!=1)
				{
					if(fielditem.isChangeAfter()) {
                        fielditem.setItemid(fielditem.getItemid()+"_2");
                    }
					if(fielditem.isChangeBefore()){
							if(this.tablebo.getSub_domain_map()!=null&&this.tablebo.getSub_domain_map().get(""+i)!=null&&this.tablebo.getSub_domain_map().get(""+i).toString().trim().length()>0){
								fielditem.setItemid(fielditem.getItemid()+"_"+this.tablebo.getSub_domain_map().get(""+i)+"_1");
								fielditem.setItemdesc(""+this.tablebo.getSub_domain_map().get(""+i+"hz"));
								}else{
									fielditem.setItemid(fielditem.getItemid()+"_1");	
								}
						
					}
				}
			}
			
			//判断条件是否含临时变量
			HashMap usedVarMap=new HashMap();
			Document doc=null;
			Element element=null;
			FieldItem item=null;
			for(int i=0;i<transitionList.size();i++)
			{
				WF_Transition trans=(WF_Transition)transitionList.get(i);
				String condition=trans.getCondition();
				int nextNodeId=trans.getNext_nodeid();
				if(condition!=null&&condition.trim().length()>0) //定义了变迁条件
				{
						doc=PubFunc.generateDom(condition);;
						String xpath="/params/condition";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);
						if(childlist.size()==0){
							xpath="/param/condition";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element)childlist.get(0);
							String condition_formular=element.getText();
							condition_formular=condition_formular.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
							String strfilter="";
							if(condition_formular!=null&&condition_formular.trim().length()>0)
							{
								for(int j=0;j<fieldlist.size();j++)
								{
									 item=(FieldItem)fieldlist.get(j);
									 if(item==null||item.getItemdesc()==null||item.getItemid()==null)//临时变量对应的指标属性如果等于null的话 跳过 bug23365
                                     {
                                         continue;
                                     }
									 if(condition_formular.indexOf(item.getItemid())!=-1||condition_formular.indexOf(item.getItemdesc())!=-1)
									 {
										 usedVarMap.put(item.getItemid(),item);
									 } 
								} 
							}
						}
				}
			}
			//自动计算临时变量
			if(usedVarMap.size()>0)
			{
				ArrayList varlist=new ArrayList();
				for(int j=0;j<fieldlist.size();j++)
				{
					 item=(FieldItem)fieldlist.get(j);
					 if(usedVarMap.get(item.getItemid())!=null) {
                         varlist.add(item);
                     }
				}
				if(this.tablebo.getInfor_type()==1)
				{ 
					this.tablebo.setBz_tablename(tabname);
					
					String strwhere="";
					if(sql.indexOf("where")!=-1) {
                        strwhere=sql.substring(sql.indexOf("where"));
                    }
					/**计算临时变量,把临时变量加到变动处理表中去*/
					/**应用库前缀*/
					
					Object[] dbarr=null; 
					ArrayList dblist=searchDBPreList(tabname);
					dbarr=dblist.toArray();	 
					this.tablebo.addMidVarIntoGzTable(" "+strwhere,dbarr,varlist);
				}
			}
			
			
			
			int infoGroupFlag=YksjParser.forPerson;
			if(this.tablebo.getInfor_type()==2) {
                infoGroupFlag=YksjParser.forUnit;
            }
			if(this.tablebo.getInfor_type()==3) {
                infoGroupFlag=YksjParser.forPosition;
            }
			YksjParser yp = new YksjParser( this.userview ,fldvarlist,
					YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", ""); 
			HashMap conditonmap = new HashMap();
			for(int i=0;i<transitionList.size();i++)
			{
				WF_Transition trans=(WF_Transition)transitionList.get(i);
				String condition=trans.getCondition();
				int nextNodeId=trans.getNext_nodeid();
				if(condition!=null&&condition.trim().length()>0) //定义了变迁条件
				{
						doc=PubFunc.generateDom(condition);;
						String xpath="/params/condition";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);
						if(childlist.size()==0){
							xpath="/param/condition";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element)childlist.get(0);
							String condition_formular=element.getText();
							condition_formular=condition_formular.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
							String strfilter="";
							if(condition_formular!=null&&condition_formular.trim().length()>0)
							{
								condition_formular=filterConditionFormular(condition_formular ,this.userview,transitionList,String.valueOf(nextNodeId),node,ins_id,taskid,selfapply,whl);
								yp.run_where(condition_formular);
								strfilter=yp.getSQL();
							} 
							rowSet=dao.search(sql+" and ("+strfilter+")");
							if(rowSet.next())
							{ 
									if(node.getNodetype()==4) //与发散
									{
										WF_Node _node=new WF_Node(nextNodeId,this.conn);
										rowSet=dao.search(sql+" and ("+strfilter+")");
										ArrayList seqnumList=new ArrayList();
										while(rowSet.next()){
											seqnumList.add(rowSet.getString("seqnum"));
											conditonmap.put(rowSet.getString("seqnum"), rowSet.getString("seqnum"));
										}
										_node.setSeqnumList(seqnumList);
										list.add(_node);
									}
									else if(node.getNodetype()==6) //或发散
									{
										WF_Node _node=new WF_Node(nextNodeId,this.conn);
										rowSet=dao.search(sql+" and ("+strfilter+")");
										ArrayList seqnumList=new ArrayList();
										while(rowSet.next())
										{
											if(existsMap.get(rowSet.getString("seqnum"))==null)
											{
												seqnumList.add(rowSet.getString("seqnum"));
												existsMap.put(rowSet.getString("seqnum"), "1");
											}
											conditonmap.put(rowSet.getString("seqnum"), rowSet.getString("seqnum"));
										}
										if(seqnumList.size()>0)
										{
											_node.setSeqnumList(seqnumList);
											list.add(_node);
										}
									  
									} 
							}
						}
				}
				else
				{
					flag = true;
					if(node.getNodetype()==4) //与发散
					{
						WF_Node _node=new WF_Node(nextNodeId,this.conn);
						rowSet=dao.search(sql);
						ArrayList seqnumList=new ArrayList();
						while(rowSet.next()) {
                            seqnumList.add(rowSet.getString("seqnum"));
                        }
						_node.setSeqnumList(seqnumList);
						list.add(_node);
					}
					else if(node.getNodetype()==6) //或发散
					{
						WF_Node _node=new WF_Node(nextNodeId,this.conn);
						rowSet=dao.search(sql);
						ArrayList seqnumList=new ArrayList();
						while(rowSet.next())
						{	 
							if(existsMap.get(rowSet.getString("seqnum"))==null)
							{
								seqnumList.add(rowSet.getString("seqnum"));
								existsMap.put(rowSet.getString("seqnum"), "1");
							}
						}
						if(seqnumList.size()>0)
						{
							_node.setSeqnumList(seqnumList);
							list.add(_node);
						}
						  
					}
				} 
			}
			if(!flag){
				rowSet=dao.search(sql);
				while(rowSet.next()){
					String seqnum = rowSet.getString("seqnum");
					if(conditonmap!=null&&conditonmap.get(seqnum)!=null){
						
					}else{
						if(this.tablebo.getInfor_type()==1) {
                            names+= rowSet.getString("a0101_1")+",";
                        } else {
                            names+= rowSet.getString("codeitemdesc_1")+",";
                        }
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{
				
			}
		}
		if(!flag&&names.length()>0){
			throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.records")+":\r\n"+names.substring(0,names.length()-1)+"\r\n"+ResourceFactory.getProperty("general.template.workflowbo.info5")+"!");
		}
		return list;
		
	}
	
	/**
	 * 判断变迁条件中是否包含“直接领导”，根据报批人的审批关系，程序判断替换是否为空
	 * @param formular
	 * @param selfapply 员工自助申请标识 ＝1员工 ＝0业务员 
	 * @param userView
	 * @param curTransNextNodeId 当前变迁条件定义的下一节点，如果是特殊角色，则优先走此节点的接收范围，否则，随机走一条特殊角色的接收范围 wangrd 20150803
	 * @return
	 */
	private String filterConditionFormular(String formular ,UserView userView,ArrayList transitionList,String curTransNextNodeId,WF_Node node,int ins_id,int taskid,String selfapply,String whl)throws GeneralException
	{ 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1||formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1)
			{
				if (!isHasSpecialRoleInFormular(formular)){
					return formular; 
				}
				WF_Node wf_node=null; 
				WF_Actor _wf_actor=null;
				Document doc=null;
				Element element=null;
				String ext_param="";
			//	String from_nodeid="reportNode"; //reportNode:报批人 ,startNode:开始节点 ,form:单据 
				for(int i=0;i<transitionList.size();i++)
				{
					WF_Transition trans=(WF_Transition)transitionList.get(i);
					String condition=trans.getCondition();
					String nextNodeId=String.valueOf(trans.getNext_nodeid());
					wf_node=getNodeInfoById(nextNodeId);//(WF_Node)this.nodeInfoMap.get(nextNodeId);
					if(wf_node.getNodetype()==2&&actorInfoMap.get(nextNodeId)!=null ) //人工节点
					{
						_wf_actor=(WF_Actor)actorInfoMap.get(nextNodeId);
						if(_wf_actor.decideIsKhRelation(_wf_actor.getActorid(),_wf_actor.getActortype(),this.conn)!=0) //特殊角色
						{
							ext_param=wf_node.getExt_param();  
							if (curTransNextNodeId.equals(nextNodeId)){//如果是当前变迁条件，并且当前变迁条件后面的角色也是特殊角色，则优先走此节点的接收范围
								break;
							}
							/*
							if(ext_param!=null&&ext_param.trim().length()>0)
							{
								StringReader reader=new StringReader(ext_param);
								doc=saxbuilder.build(reader); 
								String xpath="/params/special_role"; 
								
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								element=(Element) findPath.selectSingleNode(doc);
								if(element!=null)
								{
									from_nodeid=element.getAttributeValue("from_nodeid");
									break;
								}
							} 
							*/
						}
					} 
				} 
				LazyDynaBean bean=getFromNodeid_role(ext_param,ins_id,dao,taskid,selfapply,whl); 
				if("gwgx".equalsIgnoreCase(this.relation_id)) //标准岗位关系
				{ 
						if(formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1)
						{ 
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","10");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1)
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","9");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						
						if(formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1) //第三级领导
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","11");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						if(formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1) //第四级领导
						{
							try
							{
								ArrayList tempList=getSuperPos_userList(bean,"human","12");
								if(tempList.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							catch(Exception ee)
							{
								formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
							}
						}
						
						
						
				}
				else
				{
					 
					//	if(t_wf_relationVo.getString("actor_type").equals("1"))  //自助用户
						{
							if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(9,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(10,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.tleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(11,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.tleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.ffleader"))!=-1)
							{ 
								HashMap a_map=getSuperSql(12,this.tablebo.getRelation_id(),bean);
								if(a_map.size()>0) {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),"'1'");
                                } else {
                                    formular=formular.replaceAll(ResourceFactory.getProperty("label.role.ffleader"),ResourceFactory.getProperty("kq.wizard.null"));
                                }
							}
							
						}
					/*	else if(t_wf_relationVo.getString("actor_type").equals("4")) //业务用户
						{
							if(formular.indexOf(ResourceFactory.getProperty("label.role.fleader"))!=-1)
							{
								String sql="select count(t_wf_mainbody.mainbody_id) from t_wf_mainbody ,usergroup where usergroup.groupid=t_wf_mainbody.groupid  and  Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
								sql+=" and SP_GRADE=9";
								rowSet=dao.search(sql);
								if(rowSet.next())
								{
									if(rowSet.getInt(1)>0)
									{
										formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),"'1'");
									}
									else
										formular=formular.replaceAll(ResourceFactory.getProperty("label.role.fleader"),ResourceFactory.getProperty("kq.wizard.null"));
								}
							}
							if(formular.indexOf(ResourceFactory.getProperty("label.role.zleader"))!=-1)
							{
								String sql="select count(t_wf_mainbody.mainbody_id) from t_wf_mainbody ,usergroup where usergroup.groupid=t_wf_mainbody.groupid  and  Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
								sql+=" and SP_GRADE=10";
								rowSet=dao.search(sql);
								if(rowSet.next())
								{
									if(rowSet.getInt(1)>0)
									{
										formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),"'1'");
									}
									else
										formular=formular.replaceAll(ResourceFactory.getProperty("label.role.zleader"),ResourceFactory.getProperty("kq.wizard.null"));
								}
							}
							
						} */
				}
				
			 
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return formular;
	}
	
	
	/**判断公式是否有特殊角色名称，目前客户有如下条件:拟人力资源部主管领导意见="01"，导致判断错误，所以多判断一层。
	 * @param formular
	 * @return
	 */
	private boolean isHasSpecialRoleInFormular(String formular)
	{
		boolean b=false;
		try
		{
			formular=formular.replace("　", ",");
			formular=formular.replace(" ", ",");
			formular=formular.replace("＝", ",");
			formular=formular.replace("=", ",");
			formular=formular.replace("＜", ",");
			formular=formular.replace("<", ",");
			formular=formular.replace("＞", ",");
			formular=formular.replace(">", ",");
	        formular=formular.replace("(", ",");
	        formular=formular.replace(")", ",");
			
			String [] arr =formular.split(",");
			for (int i=0;i<arr.length  ;i++){
				String str= arr[i];
				if(str.equals(ResourceFactory.getProperty("label.role.fleader"))
						||str.equals(ResourceFactory.getProperty("label.role.zleader"))
						||str.equals(ResourceFactory.getProperty("label.role.tleader"))
						||str.equals(ResourceFactory.getProperty("label.role.ffleader")))
				{
					b=true;	
					break;
				}
			}		
		}
		catch(Exception e)
		{			
			e.printStackTrace();
		}
		return b;
	}	
	
	
	
	

	/**
	 * 查询库前缀
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList searchDBPreList(String tabname)throws GeneralException
	{
		ArrayList dblist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct basepre from ");
			buf.append(tabname);
			RowSet rset=dao.search(buf.toString());
			while(rset.next()) {
                dblist.add(rset.getString("basepre"));
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return dblist;
	}
	/*
	 * 获得节点以前走过的节点
	 */
	private void getAllPreWFNode2(String nodeid,ArrayList list)
	{
		list.add(nodeid);
		WF_Node wf_node=getNodeInfoById(nodeid);//(WF_Node)this.nodeInfoMap.get(nodeid);
		if(wf_node==null) {
            return;
        }
		if(wf_node.getNodetype()==4 ) //与发散节点
		{ 
			try
			{ 
				boolean isOk=true; 
				if(isOk)
				{
					for(int i=0;i<this.translist.size();i++)
					{
						WF_Transition trans=(WF_Transition)this.translist.get(i);
						int nextid=trans.getNext_nodeid();
						int pre_nodeid=trans.getPre_nodeid();
						if(nextid==wf_node.getNode_id())
						{ 
							WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
							if(wf_node2.getNodetype()==4 ) //与发散节点
							{
								break;  // 不支持
							}
							else if(wf_node2.getNodetype()==6 ) //或发散节点
							{
								break;  //不支持
							}
							else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
							{
								getAllPreWFNode2(String.valueOf(pre_nodeid),list);
							}
							else
							{ 
								if(wf_node2.getNodetype()==1){
									list.add(String.valueOf(pre_nodeid));
									break;
								}
									list.add(String.valueOf(pre_nodeid));
									getAllPreWFNode2(String.valueOf(pre_nodeid),list);//xieguiquan
							} 
							break;
						}
					}
					
				} 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if(wf_node.getNodetype()==6) //或发散节点
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						break;  // 不支持
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						break;  //不支持
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllPreWFNode2(String.valueOf(pre_nodeid),list);
					}
					else
					{ 
						if(wf_node2.getNodetype()==1){
							list.add(String.valueOf(pre_nodeid));
							break;
						}
							list.add(String.valueOf(pre_nodeid));
							getAllPreWFNode2(String.valueOf(pre_nodeid),list);
					} 
					break;
				}
			}
		}
		else if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
		{
			
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						break;  // 不支持
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						break;  //不支持
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllPreWFNode2(String.valueOf(pre_nodeid),list);
					}
					else
					{ 
						if(wf_node2.getNodetype()==1){
							list.add(String.valueOf(pre_nodeid));
							break;
						}
							list.add(String.valueOf(pre_nodeid));
							getAllPreWFNode2(String.valueOf(pre_nodeid),list);
						//	if(wf_node2.getNodetype()==7)
						//		break;
						
						 
					} 
				}
			} 
		} 
		else
		{
			list.add(nodeid);
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(nextid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(pre_nodeid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						getAllPreWFNode2(String.valueOf(pre_nodeid),list);
						break;
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						getAllPreWFNode2(String.valueOf(pre_nodeid),list);
						break;
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllPreWFNode2(String.valueOf(pre_nodeid),list);
					}
					else
					{ 
						if(wf_node2.getNodetype()==1){
							list.add(String.valueOf(pre_nodeid));
							break;
						}
							list.add(String.valueOf(pre_nodeid));
							getAllPreWFNode2(String.valueOf(pre_nodeid),list);
						
						 
					} 
				}
			} 
		}
		
	}
	/*
	 * 获得节点以前走过的节点
	 */
	private void getAllAfterWFNode2(String nodeid,ArrayList list)
	{
		WF_Node wf_node=getNodeInfoById(nodeid);//(WF_Node)this.nodeInfoMap.get(nodeid);
		if(wf_node==null) {
            return;
        }
		if(wf_node.getNodetype()==4 ) //与发散节点
		{ 
			try
			{ 
				boolean isOk=true; 
				if(isOk)
				{
					for(int i=0;i<this.translist.size();i++)
					{
						WF_Transition trans=(WF_Transition)this.translist.get(i);
						int nextid=trans.getNext_nodeid();
						int pre_nodeid=trans.getPre_nodeid();
						if(pre_nodeid==wf_node.getNode_id())
						{ 
							WF_Node wf_node2=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
							if(wf_node2.getNodetype()==4 ) //与发散节点
							{
								break;  // 不支持
							}
							else if(wf_node2.getNodetype()==6 ) //或发散节点
							{
								break;  //不支持
							}
							else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
							{
								getAllAfterWFNode2(String.valueOf(nextid),list);
							}
							else
							{ 
								if(wf_node2.getNodetype()==1){
									list.add(String.valueOf(nextid));
									break;
								}
									list.add(String.valueOf(nextid));
									getAllAfterWFNode2(String.valueOf(nextid),list);//xieguiquan
							} 
					//		break;
						}
					}
					
				} 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if(wf_node.getNodetype()==6) //或发散节点
		{
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(pre_nodeid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						break;  // 不支持
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						break;  //不支持
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllAfterWFNode2(String.valueOf(nextid),list);
					}
					else
					{ 
						if(wf_node2.getNodetype()==1){
							list.add(String.valueOf(nextid));
							break;
						}
							list.add(String.valueOf(nextid));
							getAllAfterWFNode2(String.valueOf(nextid),list);
					} 
			//		break;
				}
			}
		}
		else if(wf_node.getNodetype()==5||wf_node.getNodetype()==7) //与|或汇聚节点
		{
			
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans0=(WF_Transition)this.translist.get(i);
				int _nextid=trans0.getNext_nodeid();
				int _pre_nodeid=trans0.getPre_nodeid();
				if(_pre_nodeid==wf_node.getNode_id())
				{ 
					WF_Node wf_node3=getNodeInfoById(String.valueOf(_nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(_nextid));
					if(wf_node3.getNodetype()==4 ) //与发散节点
					{
						for(int j=0;j<this.translist.size();j++)
						{
							WF_Transition trans=(WF_Transition)this.translist.get(j);
							int nextid=trans.getNext_nodeid();
							int pre_nodeid=trans.getPre_nodeid();
							if(pre_nodeid==wf_node3.getNode_id())
							{ 
								WF_Node wf_node2=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
								if(wf_node2.getNodetype()==4 ) //与发散节点
								{
									break;  // 不支持
								}
								else if(wf_node2.getNodetype()==6 ) //或发散节点
								{
									break;  //不支持
								}
								else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
								{
									getAllAfterWFNode2(String.valueOf(nextid),list);
								}
								else
								{ 
									if(wf_node2.getNodetype()==1){
										list.add(String.valueOf(nextid));
										break;
									}
										list.add(String.valueOf(nextid));
										getAllAfterWFNode2(String.valueOf(nextid),list);//xieguiquan
								} 
						//		break;
							}
						}
						
						
						//break;  // 不支持
					}
					else if(wf_node3.getNodetype()==6 ) //或发散节点
					{
						
						for(int j=0;j<this.translist.size();j++)
						{
							WF_Transition trans=(WF_Transition)this.translist.get(j);
							int nextid=trans.getNext_nodeid();
							int pre_nodeid=trans.getPre_nodeid(); 
							if(pre_nodeid==wf_node3.getNode_id())
							{ 
								WF_Node wf_node2=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
								if(wf_node2.getNodetype()==4 ) //与发散节点
								{
									break;  // 不支持
								}
								else if(wf_node2.getNodetype()==6 ) //或发散节点
								{
									break;  //不支持
								}
								else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
								{
									getAllAfterWFNode2(String.valueOf(nextid),list);
								}
								else
								{ 
									if(wf_node2.getNodetype()==1){
										list.add(String.valueOf(nextid));
										break;
									}
										list.add(String.valueOf(nextid));
										getAllAfterWFNode2(String.valueOf(nextid),list);
								} 
					//			break;
							}
						}
						
						//break;  //不支持
					}
					else if(wf_node3.getNodetype()==5||wf_node3.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllAfterWFNode2(String.valueOf(_nextid),list);
					}
					else
					{ 
						if(wf_node3.getNodetype()==1){
							list.add(String.valueOf(_nextid));
							break;
						}
							list.add(String.valueOf(_nextid));
							getAllAfterWFNode2(String.valueOf(_nextid),list);
						//	if(wf_node2.getNodetype()==7)
						//		break;
						
						 
					} 
				}
			} 
		} 
		else
		{
			list.add(nodeid);
			for(int i=0;i<this.translist.size();i++)
			{
				WF_Transition trans=(WF_Transition)this.translist.get(i);
				int nextid=trans.getNext_nodeid();
				int pre_nodeid=trans.getPre_nodeid();
				if(pre_nodeid==wf_node.getNode_id())
				{ 
					WF_Node wf_node2=getNodeInfoById(String.valueOf(nextid));//(WF_Node)this.nodeInfoMap.get(String.valueOf(nextid));
					if(wf_node2.getNodetype()==4 ) //与发散节点
					{
						getAllAfterWFNode2(String.valueOf(nextid),list);
						break;
					}
					else if(wf_node2.getNodetype()==6 ) //或发散节点
					{
						getAllAfterWFNode2(String.valueOf(nextid),list);
						break;
					}
					else if(wf_node2.getNodetype()==5||wf_node2.getNodetype()==7 ) //与||或汇聚节点
					{
						getAllAfterWFNode2(String.valueOf(nextid),list);
					}
					else
					{ 
						if(wf_node2.getNodetype()==1){
							list.add(String.valueOf(nextid));
							break;
						}
							list.add(String.valueOf(nextid));
							getAllAfterWFNode2(String.valueOf(nextid),list);
						
						 
					} 
				}
			} 
		}
		
	}
	
	
	/**
	 * 获得模板所经历的任务号
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public String getSubedTaskids(int task_id,int ins_id)
	{
		StringBuffer taskIds=new StringBuffer("");
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer seqnumStr=new StringBuffer(""); 
			String sql=" select  distinct seqnum from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id+" and state<>3 " ;
			sql+=" and task_id="+task_id+"  "; 
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				seqnumStr.append(",'"+rowSet.getString("seqnum")+"'");
			}
			if(seqnumStr.length()>0)
			{
                //不与t_wf_node关联，流程发生改变后，审批过程查不到流程。
                //sql="select distinct task_id from t_wf_task_objlink,t_wf_node,t_wf_actor where  t_wf_task_objlink.node_id=t_wf_node.node_id   and t_wf_node.node_id=t_wf_actor.node_id    and  "+Sql_switcher.isnull("t_wf_task_objlink.task_type","'1'")+" in ('1','2','3' )and t_wf_node.nodetype not in ('4','5','6','7') and tabid="+this.tabid+" and ins_id="+ins_id+" and seqnum in ("+seqnumStr.substring(1)+")";
                
                sql="select distinct o.task_id from t_wf_task_objlink o,t_wf_task t where o.task_id=t.task_id and  "
                    +Sql_switcher.isnull("o.task_type","'1'")
                    +" in ('1','2','3' ) and t.task_type not in ('4','5','6','7') and o.tab_id="
                    +this.tabid+" and o.ins_id="+ins_id+" and o.seqnum in ("+seqnumStr.substring(1)+")";
                rowSet=dao.search(sql);
				while(rowSet.next())
				{
					taskIds.append(","+rowSet.getString(1));
				}
			}
			sql="select distinct task_id from t_wf_task,t_wf_node where  t_wf_task.node_id=t_wf_node.node_id and t_wf_node.nodetype='1' and   ins_id="+ins_id;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				taskIds.append(","+rowSet.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				 
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
			
		}
		
		return taskIds.toString();
	}
	
	
	/*
	 * 自动流转获得当前任务的以前的所有节点
	 * 用于审批过程
	 */
	public HashMap getAllPreWFNode(int task_id,int ins_id)
	{
		HashMap nodeMap=new HashMap();
		RowSet rowSet=null; 
		RowSet rowSet2=null;
		String tabname="templet_"+this.tabid; 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list0=new ArrayList();
			WF_Transition trans=null; 
			int _node_id=-1;
			HashMap nodeokmap = new HashMap();
			if(task_id!=0)
			{
				rowSet=dao.search("select node_id from t_wf_task where task_id="+task_id);
				if(rowSet.next()) {
                    _node_id=rowSet.getInt(1);
                }
			}
			
//			WF_Node wf_node=null;
//			
//			for(int i=0;i<this.translist.size();i++)
//			{
//				trans=(WF_Transition)this.translist.get(i);
//				int nextid=trans.getNext_nodeid();
//				int pre_nodeid=trans.getPre_nodeid();
//				if(nextid==_node_id)
//				{
//					wf_node=(WF_Node)this.nodeInfoMap.get(String.valueOf(pre_nodeid));
//					break;
//				}
//			}
//			if(wf_node==null)
//				wf_node=(WF_Node)this.nodeInfoMap.get(String.valueOf(_node_id));
//			if(wf_node==null){
//				wf_node=(WF_Node)this.nodeInfoMap.get(this.nodeInfoMap.keySet().iterator().next());
//			}
//			RecordVo vo=wf_node.getBeginNode(String.valueOf(this.tabid));
//			String start_node_id=vo.getString("node_id");
//			nodeMap.put(""+start_node_id,""+start_node_id);
			ArrayList seqnumList=new ArrayList();
			
				
				String sub_sql=" select  distinct seqnum from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id+" and state<>3 " ;
				sub_sql+=" and task_id="+task_id+"  "; 
				rowSet=dao.search(sub_sql);
				while(rowSet.next())
				{
					seqnumList.add(rowSet.getString("seqnum"));
				}
				
//			for(int e=0;e<seqnumList.size();e++)
//			{
//				String seqnum=(String)seqnumList.get(e);
				ArrayList _nodeList=new ArrayList();
//				//获得所有走过流程的节点
//				sub_sql=" select  * from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id+" and seqnum='"+seqnum+"' " ;//and state=1
//				rowSet=dao.search(sub_sql);
//			
//				while(rowSet.next())
//				{
//					nodeokmap.put(rowSet.getString("node_id")+"_"+seqnum+"_"+ins_id, rowSet.getString("node_id"));
//				//	nodeMap.put(""+rowSet.getString("node_id"),""+rowSet.getString("node_id"));
//				}
				getAllPreWFNode2(""+_node_id,_nodeList); 
				getAllAfterWFNode2(""+_node_id,_nodeList); 
					 
				for(int i=0;i<_nodeList.size();i++)
				{ 
					String _node_id1=(String)_nodeList.get(i);
						nodeMap.put(_node_id1,_node_id1);
				} 
		//	} 
			
			nodeMap.put(""+_node_id,""+_node_id);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rowSet2!=null) {
                    rowSet2.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
			
		}
		return nodeMap;
	}
	public RecordVo getT_wf_relationVo() {
		return t_wf_relationVo;
	}



	public void setT_wf_relationVo(RecordVo vo) {
		t_wf_relationVo = vo;
	}
	
	/**
	 * 获得模板所经历的任务号
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public String getSubedTaskids(int task_id,int ins_id,String returnFlag,String isDelete){
		StringBuffer taskIds=new StringBuffer("");
		RowSet rowSet=null;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer seqnumStr=new StringBuffer(""); 
			String sql=" select  distinct seqnum from t_wf_task_objlink  where  tab_id="+tabid+" and ins_id="+ins_id;
			if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
				sql+=" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ";
			}else {
				sql+=" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ";
			}
			sql+=" and task_id="+task_id+"  "; 
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				seqnumStr.append(",'"+rowSet.getString("seqnum")+"'");
			}
			if(seqnumStr.length()>0){
                sql="select distinct o.task_id from t_wf_task_objlink o,t_wf_task t where o.task_id=t.task_id and  "
                    +Sql_switcher.isnull("o.task_type","'1'")
                    +" in ('1','2','3' ) and t.task_type not in ('4','5','6','7') and o.tab_id="
                    +this.tabid+" and o.ins_id="+ins_id+" and o.seqnum in ("+seqnumStr.substring(1)+")";
                rowSet=dao.search(sql);
				while(rowSet.next())
				{
					taskIds.append(","+rowSet.getString(1));
				}
			}
			sql="select distinct task_id from t_wf_task,t_wf_node where  t_wf_task.node_id=t_wf_node.node_id and t_wf_node.nodetype='1' and   ins_id="+ins_id;
			rowSet=dao.search(sql);
			while(rowSet.next()){
				taskIds.append(","+rowSet.getString(1));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			PubFunc.closeDbObj(rowSet);
		}
		return taskIds.toString();
	}
	
	
}
