package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class GetTreeInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String itemids=(String)this.getFormHM().get("itemids");
			String[] itemid=itemids.split("/");
			String sql=(String)this.getFormHM().get("sql");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			String opt=(String)this.getFormHM().get("opt");
			StringBuffer buf = new StringBuffer();
			ArrayList valuelist = new ArrayList();
			ArrayList newlist = new ArrayList();
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				buf.append(" and  1=1 ");
			}
			else
			{			
				buf.append(" and (");
	    		String b_units=this.userView.getUnitIdByBusi("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");	
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);	
		    				boolean flag = true;
		    				for(int j=0;j<valuelist.size();j++){//取范围内最顶级的几个节点
		    					String obj = (String) valuelist.get(j);
		    					obj = "'"+obj;
		    					String value = "'"+privCodeValue;
		    					if(obj.indexOf(value)!=-1){
		    						valuelist.set(j, privCodeValue);
		    						flag = false;
		    					}
		    					if(value.indexOf(obj)!=-1){
		    						flag = false;
		    					}
		    				}
		    				if(flag){
		    					valuelist.add(privCodeValue);
		    				}
		    				if(privCode!=null&&!"".equals(privCode))
							{
					     		buf.append(" (parentid like '"+(privCodeValue==null?"":privCodeValue)+"%'");
					    		if(privCodeValue==null)
					    			buf.append(" or parentid is null ");
					    		buf.append(") or");
							}
	    				}
					}
					HashSet set = new HashSet();
					set.addAll(valuelist);
					
					newlist.addAll(set);
				}else if("UN`".equalsIgnoreCase(b_units)){
					buf.append( "  1=1 or");
				}
				else{
					buf.append(" 1=2 or");
				}
				String _str = buf.toString();
				buf.setLength(0);
				buf.append(_str.substring(0, _str.length()-3));
				buf.append(")");
			}
			String gzmodel = (String) this.userView.getHm().get("gzmodel");//薪资发放 0；历史数据分析 3
			if("0".equals(opt)&&("0".equals(gzmodel)||"1".equals(gzmodel)))//薪资发放、薪资审批报表
			{
	    		if(sql.indexOf("order by")!=-1)
			    	sql=sql.substring(0,sql.indexOf("order by"));
		    	sql+=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
		    	sql+=" order by a0000";
		    	this.frowset=dao.search(sql);
		     	while(this.frowset.next())
		    	{
			    	LazyDynaBean c_dynabean=new LazyDynaBean();
			    	for(int i=0;i<itemid.length;i++)
			    	{
				    	c_dynabean.set(itemid[i], this.frowset.getString(itemid[i]));
			     	}
			    	list.add(c_dynabean);
		     	}
			}else if((opt==null||"3".equals(gzmodel))&&!"1".equals(opt)&&!"2".equals(opt))//薪资分析或者薪资历史数据里面的报表
			{
	    		if(sql.indexOf("order by")!=-1)
			    	sql=sql.substring(0,sql.indexOf("order by"));
	    		if(sql.indexOf("parentid=codeitemid")!=-1){//首次进入  过来的sql中含有此段
	    			StringBuffer str = new StringBuffer();
	    			for(int i=0;i<newlist.size();i++){
	    				str.append(" codeitemid = '"+newlist.get(i)+"' or" );
	    			}
	    			sql="select * from organization   where codesetid<>'@K' ";
	    			if(str.length()>0){
	    				sql+="and ("+str.substring(0, str.length()-3)+")";
	    			}else if(buf.indexOf("1=1")!=-1){//选全部了，b_units.equalsIgnoreCase("UN`")
	    				sql+=" and parentid=codeitemid";
	    			}else if(buf.indexOf("1=2")!=-1){//啥权限都没有的情况
	    				sql+=buf.toString();
	    			}
	    		}else{	    		
	    			sql+=buf.toString();
	    		}
		    	sql+=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
		    	
		    	sql+=" order by a0000";
		    	this.frowset=dao.search(sql);
		     	while(this.frowset.next())
		    	{
			    	LazyDynaBean c_dynabean=new LazyDynaBean();
			    	for(int i=0;i<itemid.length;i++)
			    	{
				    	c_dynabean.set(itemid[i], this.frowset.getString(itemid[i]));
			     	}
			    	list.add(c_dynabean);
		     	}
			}
			else if("1".equals(opt))
			{
				String unitid=this.getUserView().getUnitIdByBusi("1");
				StringBuffer wheresql = new StringBuffer();
				StringBuffer strsql=new StringBuffer("");
				strsql.append("select * from organization   where codesetid<>'@K' ");
				if(unitid==null|| "".equals(unitid))
				{
					unitid=this.getUserView().getUnit_id();
					if("UN".equalsIgnoreCase(unitid))
						unitid="";
				}
				if(unitid!=null&&!"".equals(unitid))
				{
			    	unitid=PubFunc.getTopOrgDept(unitid);
			    	String codevalue="";
			        String codeall="";
			        String unitarr[] = unitid.split("`"); 
			        for(int i=0;i<unitarr.length;i++){
			        	String codeid = unitarr[i];
			        	if(codeid!=null&&codeid.trim().length()>2){
			        		codevalue+="'"+codeid.substring(2)+"',";
			        	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
			        		codeall=codeid;
			        	}
			        }
			        codevalue+="'aaaaa'";
			        if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
			        	 wheresql.append(" codeitemid=parentid");
			        }else{
			        	wheresql.append(" codeitemid in("+codevalue+")");
			        }
				}else
				{
					wheresql.append(" codeitemid='"+this.getUserView().getManagePrivCodeValue()+"'");
				}
				strsql.append(" and "+wheresql);
				strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by a0000");
				this.frowset=dao.search(strsql.toString());
		     	while(this.frowset.next())
		    	{
			    	LazyDynaBean c_dynabean=new LazyDynaBean();
			    	for(int i=0;i<itemid.length;i++)
			    	{
				    	c_dynabean.set(itemid[i], this.frowset.getString(itemid[i]));
			     	}
			    	list.add(c_dynabean);
		     	}
			}
			/**安操作单位*/
			else if("2".equals(opt))
			{
				String unitid=this.getUserView().getUnitIdByBusi("1");
				String _unitid = unitid;
				StringBuffer wheresql = new StringBuffer();
				StringBuffer strsql=new StringBuffer("");
				strsql.append("select * from organization   where codesetid<>'@K' ");
				if("UN".equalsIgnoreCase(unitid))
					unitid="";
				if(unitid!=null&&!"".equals(unitid))
				{
			    	unitid=PubFunc.getTopOrgDept(unitid);
			    	String codevalue="";
			        String codeall="";
			        String unitarr[] = unitid.split("`"); 
			        for(int i=0;i<unitarr.length;i++){
			        	String codeid = unitarr[i];
			        	if(codeid!=null&&codeid.trim().length()>2){
			        		codevalue+="'"+codeid.substring(2)+"',";
			        	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
			        		codeall=codeid;
			        	}
			        	if(_unitid.indexOf("UN`")!=-1){//全部单位 zhaoxg add 2014-10-13
			        		codevalue+="'"+codeid+"',";
			        	}
			        }
			        codevalue+="'aaaaa'";
			        if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
			        	 wheresql.append(" codeitemid=parentid");
			        }else{
			        	wheresql.append(" codeitemid in("+codevalue+")");
			        }
				}else
				{
					wheresql.append(" codeitemid='"+this.getUserView().getManagePrivCodeValue()+"'");
				}
				strsql.append(" and "+wheresql);
				strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by a0000");
				this.frowset=dao.search(strsql.toString());
		     	while(this.frowset.next())
		    	{
			    	LazyDynaBean c_dynabean=new LazyDynaBean();
			    	for(int i=0;i<itemid.length;i++)
			    	{
				    	c_dynabean.set(itemid[i], this.frowset.getString(itemid[i]));
			     	}
			    	list.add(c_dynabean);
		     	}
			}
			
			this.getFormHM().put("list",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
