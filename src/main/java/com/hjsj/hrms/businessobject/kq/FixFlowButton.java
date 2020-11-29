package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.general.operation.TwfdefineBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FixFlowButton {
    private Connection conn;
    private ContentDAO dao;
    private UserView userview;
	public FixFlowButton(Connection conn,UserView userview)
	{
		this.conn=conn;
		this.dao=new ContentDAO(this.conn);
		this.userview=userview;
	}
	/**
	 * 通过名称得到固定表单定义的id
	 * @param name
	 * @return
	 */
	public ArrayList getT_wf_define_Tabid_list(String name)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select tabid from t_wf_define ");
		sql.append(" where name like '"+name+"%'");
		ArrayList list=new ArrayList();
		String tabid="";
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());
			while(rs.next()) {
                list.add(rs.getString("tabid"));
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	}
	public String getT_wf_define_Tabid(ArrayList list,String param_name,String url)
	{
		String tabid="";
		for(int i=0;i<list.size();i++)
		{
			tabid=list.get(i).toString();
			
			if(isSaveUrl(param_name,tabid,url)) {
                return tabid;
            }
		}
		return tabid;
	}
	/**
	 * 判断路径是否存在
	 * @param param_name
	 * @param tabid
	 * @param url
	 * @return
	 */
	private boolean isSaveUrl(String param_name, String tabid,String url)
	{
		boolean isCorrect=true;
		StringBuffer sql=new StringBuffer();		
		sql.append("select ctrl_para from t_wf_define ");
		sql.append(" where tabid ='"+tabid+"'");
		String ctrl_para="";
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());
			if(rs.next()) {
                ctrl_para=rs.getString("ctrl_para");
            }
			if(ctrl_para!=null&&ctrl_para.length()>0)
			{
				Document doc=PubFunc.generateDom(ctrl_para);
				String xpath="/params/"+param_name+"[@url='"+url+"']";
				XPath reportPath = XPath.newInstance(xpath);//取得子集结点
				List childlist=reportPath.selectNodes(doc);
			    Iterator t = childlist.iterator();
			    if(!t.hasNext())
			    {
			    	isCorrect=false;		    	
			    }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return isCorrect;
	} 
	/**
	 * 判断提交action路径是否与输入url相符
	 * @param tabid
	 * @param url
	 * @return
	 */
	public boolean isEditUrl(String tabid,String url,HashMap map)
	{
		boolean isCorrect=false;		
		isCorrect=isSaveUrl("edit_form",tabid,url,map);
		return isCorrect;
	}
	/**
	 * 判断提交action路径是否与审批的url相符
	 * @param tabid
	 * @param url
	 * @return
	 */
	public boolean isApplyUrl(String tabid,String url,HashMap map)
	{
		boolean isCorrect=false;		
		isCorrect=isSaveUrl("appeal_form",tabid,url,map);
		return isCorrect;
	}
	private boolean isSaveUrl(String param_name, String tabid,String url,HashMap map)
	{
		boolean isCorrect=true;
		StringBuffer sql=new StringBuffer();		
		sql.append("select ctrl_para from t_wf_define ");
		sql.append(" where tabid ='"+tabid+"'");
		String ctrl_para="";
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());
			if(rs.next()) {
                ctrl_para=rs.getString("ctrl_para");
            }
			if(ctrl_para!=null&&ctrl_para.length()>0)
			{
				Document doc=PubFunc.generateDom(ctrl_para);
				String xpath="/params/"+param_name+"[@url='"+url+"']";
				XPath reportPath = XPath.newInstance(xpath);//取得子集结点
				List childlist=reportPath.selectNodes(doc);
			    Iterator t = childlist.iterator();
			    if(t.hasNext())
			    {
			    	Element element=(Element)t.next();
			    	childlist=element.getChildren();
			    	Iterator b=childlist.iterator();
			    	while(b.hasNext())
			    	{
			    		Element elementB=(Element)b.next();
			    		String name=elementB.getAttributeValue("name")!=null&&elementB.getAttributeValue("name").length()>0?elementB.getAttributeValue("name"):"";
			    		String value=elementB.getAttributeValue("value")!=null&&elementB.getAttributeValue("value").length()>0?elementB.getAttributeValue("value"):"";
			    		String parvalue=(String)map.get(name);
			    		if(parvalue==null||parvalue.length()<=0&& "*".equals(value))
			    		{
			    			continue;
			    		}else if(value!=null&&value.length()>0&&!"*".equals(value))
			    		{
			    			if(!value.equalsIgnoreCase(parvalue))
			    			{
			    				isCorrect=false;
			    				break;
			    			}
			    		}
			    	}			    	
			    }else
			    {
			    	isCorrect=false;
			    }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return isCorrect;
	}
	/**
	 * 显示输入按钮
	 * @param tabid
	 * @return
	 */
	public String showEditButton(String tabid)
	{
		StringBuffer nodesql=new StringBuffer();
		nodesql.append("select n.node_id,n.nodename,a.actorname from t_wf_node n");
		nodesql.append(Sql_switcher.left_join("t_wf_node n","t_wf_actor a","n.node_id","a.node_id"));
		nodesql.append(",t_wf_transition o where n.tabid=");
		nodesql.append(tabid);
		nodesql.append(" and nodetype='2' and o.next_nodeid=n.node_id order by o.tran_id");
		StringBuffer button=new StringBuffer();
		RowSet rs=null;
		try
		{
			rs=this.dao.search(nodesql.toString());
			if(rs.next())
			{ 
				button.append("<input type='button' name='apply' ");
				button.append(" value=\"报送["+rs.getString("nodename")+"]审批\" ");
				button.append("class='mybutton' onclick='flow_areport(\""+rs.getString("node_id")+"\")'>");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return button.toString();
	}
	private String showAppealButton(String task_id,String ins_id,String tabid)
	{   
		StringBuffer buf=new StringBuffer();
		ArrayList actorlist=new ArrayList();
		Define_WF_Node d_wf_node=null;
		boolean b_end=false;
		TemplateDefineBo defineBo=new TemplateDefineBo(this.conn,tabid,this.userview);
		
		try
		{
			if(ins_id==null|| "0".equals(ins_id))
			{
				d_wf_node=defineBo.getWF_StartNode();
				ArrayList list=d_wf_node.getNextHumanNodeList();
				if(list.size()>0)
				{
					d_wf_node=(Define_WF_Node)list.get(0);
					actorlist=d_wf_node.getActorList();
				}
			}else
			{
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",Integer.parseInt(task_id));
				task_vo=dao.findByPrimaryKey(task_vo);
				if(task_vo==null) {
                    throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
                }
				int node_id=task_vo.getInt("node_id");
				d_wf_node=new Define_WF_Node(node_id,this.conn,defineBo);
				/**分析下一个节点是否为END*/
				ArrayList nextlist=d_wf_node.getNextNodeList();
				if(nextlist.size()==1)
				{
					Define_WF_Node wf_endnode=(Define_WF_Node)nextlist.get(0);
					if(wf_endnode.getNodetype()==NodeType.END_NODE)
					{
						b_end=true;
					}
				}
				
				nextlist=d_wf_node.getNextHumanNodeList();//取下一个人工节点
				for(int i=0;i<nextlist.size();i++)
				{
					d_wf_node=(Define_WF_Node)nextlist.get(i);
					actorlist.addAll(d_wf_node.getActorList());
				}
				for(int i=0;i<actorlist.size();i++)
				{
					WF_Actor wf_actor=(WF_Actor)actorlist.get(i);
					if(i>0) {
                        buf.append(",");
                    } else
					{
						buf.append("报送[");
					}
					buf.append(wf_actor.getActorname());
				}//for i loop end.
				if(buf.length()==0)
				{
					if(b_end) {
                        buf.append(ResourceFactory.getProperty("button.submit"));
                    } else {
                        buf.append(ResourceFactory.getProperty("button.appeal"));
                    }
				}
				else
				{
					buf.append("]审批");
				}
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return "";
	}
	public String showJavaScript(String tabid,HashMap formMap)
	{
		StringBuffer scriptSTR=new StringBuffer();		
		StringBuffer sql=new StringBuffer();		
		sql.append("select ctrl_para from t_wf_define ");
		sql.append(" where tabid ='"+tabid+"'");	
		String ctrl_para="";
		boolean isCorrect=true;		
		StringBuffer  param=new StringBuffer();
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());
			if(rs.next()) {
                ctrl_para=rs.getString("ctrl_para");
            }
			if(ctrl_para!=null&&ctrl_para.length()>0)
			{
				Document doc=PubFunc.generateDom(ctrl_para);
				String xpath="/params/appeal_form";
				XPath reportPath = XPath.newInstance(xpath);//取得子集结点
				List childlist=reportPath.selectNodes(doc);
			    Iterator t = childlist.iterator();
			    if(t.hasNext())
			    {
			    	Element element=(Element)t.next();
			    	childlist=element.getChildren();
			    	Iterator b=childlist.iterator();
			    	while(b.hasNext())
			    	{
			    		Element elementB=(Element)b.next();
			    		String name=elementB.getAttributeValue("name")!=null&&elementB.getAttributeValue("name").length()>0?elementB.getAttributeValue("name"):"";
			    		String value=elementB.getAttributeValue("value")!=null&&elementB.getAttributeValue("value").length()>0?elementB.getAttributeValue("value"):"";
			    		if(value!=null&&value.indexOf("[")!=-1&&value.indexOf("]")!=-1)
			    		{
			    			String fromvalue=getFormValue(formMap,value);
			    			if(fromvalue==null||fromvalue.length()<=0)
			    			{
			    				isCorrect=false;
			    				break;
			    			}
			    			param.append(name+"="+fromvalue+"&");
			    			
			    		}else
			    		{
			    			param.append(name+"="+value+"&");
			    		}
			    	}
                    if(param.length()>0) {
                        param.setLength(param.length()-1);
                    }
			    }else
			    {
			    	isCorrect=false;
			    }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		scriptSTR.append("<script language=\"javascript\">");
		scriptSTR.append("function flow_areport(){");		
		if(isCorrect)
		{
			RecordVo t_wf_defineVo=new RecordVo("t_wf_define");
			TwfdefineBo twf=new TwfdefineBo();
			t_wf_defineVo.setInt("tabid",(new Integer(tabid)).intValue());
			
			try {
				t_wf_defineVo=dao.findByPrimaryKey(t_wf_defineVo);
				twf.paraxml(t_wf_defineVo.getString("ctrl_para"));
			}  catch (Exception e) {
				e.printStackTrace();				
			}			
			scriptSTR.append(" var hashvo=new ParameterSet();");
			scriptSTR.append(" hashvo.setValue(\"define_tabid\",\""+tabid+"\");");		
			scriptSTR.append(" hashvo.setValue(\"sp_mode\",\""+twf.getSp_flag()+"\");");	
			scriptSTR.append(" var params=new Array();");
			scriptSTR.append(" params[0]=\""+param+"\";");
			scriptSTR.append(" hashvo.setValue(\"params\",params);");
			scriptSTR.append("var request=new Request({method:'post',onSuccess:showflow,functionId:'0570010401'},hashvo);");
		}else
		{
			scriptSTR.append("alert(\"请先保存申请，再报送审批\");");
		}
		scriptSTR.append("}");
		scriptSTR.append("\r\n");
		scriptSTR.append("function showflow(outparamters){");
        scriptSTR.append(" var flag=outparamters.getValue(\"flag\");");
        scriptSTR.append(" if(flag==\"0\")");
        scriptSTR.append("   alert(\"报送成功！\");");
        scriptSTR.append(" else ");
        scriptSTR.append("   alert(\"报送失败！\");");
		scriptSTR.append("}");
		scriptSTR.append("</script>");
		return scriptSTR.toString();
	}
	/**
	 * 得到职
	 * @param formMap
	 * @param name
	 * @return
	 */
	private String getFormValue(HashMap formMap,String name)
	{
		name=name.substring(name.indexOf("[")+1,name.indexOf("]"));
		if(formMap==null) {
            return "";
        }
		String value=(String)formMap.get(name);
		return value;
	}
}
