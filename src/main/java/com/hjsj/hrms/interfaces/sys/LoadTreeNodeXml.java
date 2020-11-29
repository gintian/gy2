/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import org.apache.commons.beanutils.DynaBean;

import java.util.List;



/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadTreeNodeXml {
	private String userbase;       //人员库
	private String parentid;       //父节点id
	private String managepriv;     //管理权限
	private String target;         //目标窗口
	private String action;         //动作
	private String params;         //参数是否是跟目录
	private boolean isSuperuser;   //是否是超级用户
	private String src;            //装在chile节点的动作
	private String type;            //0代表单位1代表岗位2代表职位
	private String codetype;         //0代表组织结构树，1代表代码树
	private String fieldname;        //在作为代码树时要目的属性列的编号
    public LoadTreeNodeXml(String params,boolean isSuperuser,String managepriv,String codetype,String fieldname,String type,String action,String target,String src,String parentid,String userbase){
    	this.managepriv=managepriv;
    	this.target=target;
    	this.action=action;
    	this.params=params;
    	this.type=type;
    	this.isSuperuser=isSuperuser;
    	this.parentid=parentid;
    	this.userbase=userbase;
    	this.src=src;
    	this.codetype=codetype;
    	this.fieldname=fieldname;
    } 
    public String outTreeNode(){
    	StringBuffer strXml=new StringBuffer();  //生成的tree的xml
    	try{
    		String codeitemid;
    		String codeitemdesc;
    		if(this.codetype!=null && "1".equals(this.codetype))             //显示树的是代码树
    		{
    			if(this.type!=null && ("UN".equals(this.type) || "UM".equals(this.type) || "@K".equals(this)))
    			{
            		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString());    	
            		if(!rs.isEmpty())
            		{
            			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
            			for(int i=0;i<rs.size();i++)
            			{
            			     TreeItemView treeitem=new TreeItemView();
            			     DynaBean rec=(DynaBean)rs.get(i);
            			     codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
            			     codeitemdesc=rec.get("codeitemdesc")!=null?rec.get("codeitemdesc").toString():"";
            			     treeitem.setName(codeitemid);
            			     treeitem.setText(codeitemdesc);
            		         treeitem.setTitle(codeitemdesc);  
          		             treeitem.setTarget(this.target);
            		         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
            		         {
            		         	String parentid=codeitemid;
            		            treeitem.setXml(this.src + "?params=child&amp;parentid="  + parentid + "&amp;kind=2&amp;fieldname=" + this.fieldname + "&amp;type=" + this.type);
            		            treeitem.setIcon("/images/unit.gif");
            		            if(this.codetype !=null && "1".equals(this.codetype) && (this.type==null || this.type!=null && "UN".equals(this.type)))
               		            {
                  		             treeitem.setAction("javascript:paste(&quot;" + codeitemdesc + "&quot;,&quot;" + codeitemid + "&quot;,&quot;" + this.fieldname + "&quot;)");
               		            }
            		            else
            		            {
            		            	treeitem.setAction("");
            		            }
            		         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid"))){
            		         	String parentid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
            		         	treeitem.setXml(this.src + "?params=child&amp;parentid=" + parentid +
        		         		"&amp;kind=1&amp;fieldname=" + this.fieldname  + "&amp;type=" + this.type);
            		            treeitem.setIcon("/images/dept.gif");
            		            if(this.codetype !=null && "1".equals(this.codetype) && (this.type==null || this.type!=null && "UM".equals(this.type)))
               		            {
                   		             treeitem.setAction("javascript:paste(&quot;" + codeitemdesc + "&quot;,&quot;" + codeitemid + "&quot;,&quot;" + this.fieldname + "&quot;)");
               		            }
            		            else
            		            {
            		            	treeitem.setAction("javascript:void(0)");
            		            }
               		         }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid"))){
            		         	String parentid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
            		         	treeitem.setXml(this.src + "?params=child&amp;parentid=" + parentid +
        		         		"&amp;kind=0&amp;fieldname=" + this.fieldname  + "&amp;type=" + this.type);
            		            treeitem.setIcon("/images/pos_l.gif");
            		            if(this.codetype !=null && "1".equals(this.codetype)  && (this.type==null || this.type!=null && "@K".equals(this.type)))
               		            {
                  		             treeitem.setAction("javascript:paste(&quot;" + codeitemdesc + "&quot;,&quot;" + codeitemid + "&quot;,&quot;" + this.fieldname + "&quot;)");
               		            } 
            		            else
            		            {
            		            	treeitem.setAction("javascript:void(0)");
            		            }
            		         }
            		        // System.out.println(treeitem.toChildNodeJS());
            		         strXml.append(treeitem.toChildNodeJS() + "\n");
            			}
            			strXml.append("</TreeNode>\n");
                		return strXml.toString();
            		}   

    			}else
    			{
    				///非单位部门职位的代码树
    				List rs=ExecuteSQL.executeMyQuery(getCodeTreeQueryString());    
    				if(!rs.isEmpty())
            		{
            			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
            			for(int i=0;i<rs.size();i++)
            			{
            			     TreeItemView treeitem=new TreeItemView();
            			     DynaBean rec=(DynaBean)rs.get(i);
            			     codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
            			     codeitemdesc=rec.get("codeitemdesc")!=null?rec.get("codeitemdesc").toString():"";
            			     treeitem.setName(codeitemid);
            			     treeitem.setText(codeitemdesc);
            		         treeitem.setTitle(codeitemdesc);  
          		             treeitem.setTarget(this.target);
            		         String parentid=codeitemid;
            		         treeitem.setXml(this.src + "?params=child&amp;parentid="  + parentid + "&amp;fieldname=" + this.fieldname + "&amp;type=" + this.type);
            		         treeitem.setIcon("/images/unit.gif");
            		         treeitem.setAction("javascript:paste(&quot;" + codeitemdesc + "&quot;,&quot;" + codeitemid + "&quot;,&quot;" + this.fieldname + "&quot;)");
               		        // System.out.println(treeitem.toChildNodeJS());
            		         strXml.append(treeitem.toChildNodeJS() + "\n");
            			}
            			strXml.append("</TreeNode>\n");
                		return strXml.toString();
            		} 
    			}
    		}else                                                           //显示的是组织结构树
    		{
        		List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString());    	
        		if(!rs.isEmpty())
        		{
        			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
        			for(int i=0;i<rs.size();i++)
        			{
        			     TreeItemView treeitem=new TreeItemView();
        			     DynaBean rec=(DynaBean)rs.get(i);
        			     codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
        			     codeitemdesc=rec.get("codeitemdesc")!=null?rec.get("codeitemdesc").toString():"";
        			     treeitem.setName(codeitemid);
        			     treeitem.setText(codeitemdesc);
        		         treeitem.setTitle(codeitemdesc);  
      		             treeitem.setTarget(this.target);
        		         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
        		         {
        		         	String parentid=codeitemid;
        		            treeitem.setXml(this.src + "?params=child&amp;parentid="  + parentid + "&amp;kind=2");
        		            treeitem.setIcon("/images/unit.gif");
        		            treeitem.setAction("");///////所添的动作
        		         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid"))){
        		         	String parentid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
        		         	treeitem.setXml(this.src + "?params=child&amp;parentid=" + parentid +
    		         		"&amp;kind=1&amp;fieldname=" + this.fieldname  + "&amp;type=" + this.type);
        		            treeitem.setIcon("/images/dept.gif");
        		            treeitem.setAction("");///////所添的动作
        		          }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid")) && (this.codetype==null || this.codetype!=null && "@K".equals(this.codetype))){
        		         	String parentid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
        		         	treeitem.setXml(this.src + "?params=child&amp;parentid=" + parentid +
    		         		"&amp;kind=0&amp;fieldname=" + this.fieldname  + "&amp;type=" + this.type);
        		            treeitem.setIcon("/images/pos.gif");
        		            treeitem.setAction("");  ///////所添的动作
        		         }
        		       //  System.out.println(treeitem.toChildNodeJS());
        		         strXml.append(treeitem.toChildNodeJS() + "\n");
        			}
        			strXml.append("</TreeNode>\n");
            		return strXml.toString();
        		}   
    		} 	
    	}catch(Exception e){
    	    e.printStackTrace();	
    	}
    	return null;
    }
    private String getCodeTreeQueryString()
    {
    	StringBuffer strsql=new StringBuffer();
    	strsql.append("select * from codeitem ");
    	if(params!=null && "root".equals(params))
    	{
    		strsql.append(" where codesetid='");
    		strsql.append(this.type);
    		strsql.append("' and codeitemid=parentid");
    	}
    	else
    	{
    		strsql.append(" WHERE parentid='");
    		strsql.append(this.parentid);
    		strsql.append("'");
    		strsql.append(" AND codeitemid<>parentid and codesetid='");
    		strsql.append(this.type);
    		strsql.append("'");
    	}
    	strsql.append(" Order by a0000,codeitemid");
    	//System.out.println("-------->loadtree sql" + strsql.toString());
    	return strsql.toString();
    }
	/**
	 * @return
	 */
	private String getLoadTreeQueryString() {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid ");
		strsql.append(" FROM organization ");    	
    	if(params!=null && "root".equals(params)){
    		if(isSuperuser)
    		{
    			strsql.append(" WHERE codesetid='UN' AND codeitemid=parentid ");
    		}
    		else
    		{
    			strsql.append(" WHERE codeitemid='");
    			strsql.append(this.managepriv);
    			strsql.append("'");        
    		}
    	}
    	else
    	{
    		strsql.append(" WHERE parentid='");
    		strsql.append(this.parentid);
    		strsql.append("'");
    		strsql.append(" AND codeitemid<>parentid ");
    	}
    	if(this.codetype !=null && "1".equals(this.codetype)&& this.type!=null && "UN".equals(this.type))
		   strsql.append(" AND codesetid='UN' ");
		else if(this.codetype !=null && "1".equals(this.codetype) && this.type!=null && "UM".equals(this.type))
		    strsql.append(" AND (codesetid='UN' OR codesetid='UM') ");
    	strsql.append(" ORDER BY a0000,codeitemid ");
    	//System.out.println("-------->loadtree sql" + strsql.toString());
		return strsql.toString();
	}
}
