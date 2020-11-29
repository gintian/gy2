package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class QueryUerManagerTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{
	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	    if(hm.get("b_query")!=null)
		{
		    hm.remove("b_query");
		    this.getFormHM().put("select_pre","all");
		    this.getFormHM().put("select_name","");
		}
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");        
        String kind=(String)this.getFormHM().get("kind");
        String code=(String)this.getFormHM().get("code");
        String kq_type=(String)this.getFormHM().get("kq_type");	
        String kq_cardno=(String)this.getFormHM().get("kq_cardno");	
        String kq_gno=(String)this.getFormHM().get("kq_gno");
        ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");
        ArrayList nbaselist=(ArrayList)this.getFormHM().get("nbaselist");
        ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
        String slflag = (String)this.getFormHM().get("slflag");
        if("1".equals(slflag)){
        	Iterator it = factorlist.iterator();
        	while(it.hasNext()){
        		Factor factor = (Factor)it.next();
        		if("=".equals(factor.getOper())){
        			factor.setOper("like");
        		}
        	}
        }
        if(code==null||code.length()<=0)
		{
			code=managePrivCode.getPrivOrgId(); 
			kind="2";
		}else if(kind==null||kind.length()<=0)
		{
			code=managePrivCode.getPrivOrgId(); 
			kind="2";
		}	
        UserManager userManager=new UserManager();
        String columns=userManager.getDisplayColumns(fieldlist,kq_type,kq_cardno,kq_gno);	
        String strsql=getQueryString(factorlist,fieldlist,nbaselist,kind,code,kq_type,kq_cardno,kq_gno);       
        this.getFormHM().put("strsql",strsql);
		this.getFormHM().put("columns",columns);
		this.getFormHM().put("manageWhere",managerWhere(kind,code,factorlist));
		this.getFormHM().put("orderby","");//人员基本信息 查询 from里含有A0000；但是sql句中不含有；出现问题  wy
		
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getQueryString(ArrayList factorlist,ArrayList fieldlist,ArrayList nbaselist,String kind,String code,String kq_type,String kq_cardno,String kq_gno)
	{
		StringBuffer whereTrem=new StringBuffer();
		whereTrem.append("(1=1");
		String dbpre="";		
		boolean isCorrect=false;//判断是否选择人员库
		
		for(int i=0;i<factorlist.size();i++)
		{
			Factor factor = (Factor)factorlist.get(i);
			if("nbase".equals(factor.getFieldname().trim()))
			{
				dbpre=factor.getValue();
			}else
			{
				whereTrem.append(getOneTerm(factor));	
			}
					
		}
		ArrayList dblist=new ArrayList();
		if("all".equals(dbpre))
		{
			dblist=nbaselist;
		}else if(dbpre==null||dbpre.length()<=0)
		{
			dblist=nbaselist;
		}else
		{
			dblist.add(dbpre);
		}
		whereTrem.append(")");
		StringBuffer sql=new StringBuffer();
		for(int i=0;i<dblist.size();i++)
		{
			String nbase=dblist.get(i).toString();
			String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
			sql.append("select "+i+" as i,");
			/*sql.append(nbase);
			sql.append("' as nbase,");
			sql.append("b0110,e0122,e01a1,a0100,a0101,");*/
			int r=0;
			int s=0;
			int y=0;
			for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem item=(FieldItem)fieldlist.get(j);
				String name=item.getItemid();
				if("nbase".equalsIgnoreCase(name))
				{
					sql.append("'"+nbase);
					sql.append("' as nbase,");
				}
				else if(kq_cardno!=null&&kq_cardno.length()>0&& "t1".equals(name))
				{
					sql.append(kq_cardno);	
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
						
						continue;
				}else if(kq_gno!=null&&kq_gno.length()>0&& "t2".equals(name))
				{
					sql.append(kq_gno);		
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
						
						continue;
				}else if(kq_type!=null&&kq_type.length()>0&& "t3".equals(name))
				{
					sql.append(kq_type);
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
						
						continue;
				}else if(!item.getItemid().equalsIgnoreCase(kq_type)&&!item.getItemid().equalsIgnoreCase(kq_gno)&&!item.getItemid().equalsIgnoreCase(kq_cardno))
				{
					sql.append(item.getItemid()+",");						
					
				}
				/*if(kq_cardno!=null&&kq_cardno.length()>0&&r==0)
				{
					sql.append(kq_cardno);	
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
						j=j+1;
						r=1;
						continue;
				}else if(kq_gno!=null&&kq_gno.length()>0&&s==0)
				{
					sql.append(kq_gno);		
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
						j=j+1;
						s=1;
						continue;
				}else if(kq_type!=null&&kq_type.length()>0&&y==0)
				{
					sql.append(kq_type);
					sql.append(" ");
					sql.append(item.getItemid());
					sql.append(",");
					j=j+1;
					y=1;
					continue;
				}else if(!item.getItemid().equalsIgnoreCase(kq_type)&&!item.getItemid().equalsIgnoreCase(kq_gno)&&!item.getItemid().equalsIgnoreCase(kq_cardno))
				{
					sql.append(item.getItemid()+",");							
					j=j+1;
				}*/
			}
			sql.setLength(sql.length()-1);
			sql.append(" from "+nbase+"A01");
			sql.append(" where 1=1");
			if("1".equals(kind))
			{
				sql.append(" and e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				sql.append(" and e01a1 like '"+code+"%'");
			}
			else 
			{
				sql.append(" and b0110 like '"+code+"%'");				
			}
			sql.append(" and "+whereTrem.toString());
			sql.append(" and a0100 in(select a0100 "+whereIN+")");					
			sql.append(" UNION ");
		}
		sql.setLength(sql.length()-7);
		return sql.toString();
	}
	/**
	 * 得到一个条件的sql
	 * */
	public String getOneTerm(Factor factor)
	{
		StringBuffer oneterm = new StringBuffer();
		String fieldname=factor.getFieldname();//字段名字
		String value=factor.getValue();//值
		String oper=factor.getOper();//关系符
		factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
		String log=factor.getLog();//逻辑值
		if(value!=null&&value.length()>0)
			value=PubFunc.getStr(value);
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		/**解决为空的记录查不找；wangy 用 IS 或者IS NOT**/
		if(value!=null&&value.length()>0)
		{
			oneterm.append(logtag);
			oneterm.append(fieldname);
			if("like".equals(oper.trim()))
			{
				oneterm.append(" "+oper+" ");
				oneterm.append(" '%"+value+"%' ");
			}else
			{
				oneterm.append(oper);
				oneterm.append("'"+value+"'");
			}
		}else
		{
			
			if("like".equals(oper.trim()))
			{
				oneterm.append(logtag);
				oneterm.append(fieldname);
				oneterm.append(" "+oper+" ");
				oneterm.append(" '%"+value+"%' ");
			}else if("=".equals(oper.trim()))
			{
				oneterm.append(logtag);
				oneterm.append("(");
				oneterm.append(fieldname);
				oneterm.append(" is");
				oneterm.append(" null ");
				oneterm.append(" or ");
				oneterm.append(fieldname);
				oneterm.append(" =");
				oneterm.append(" '' ");
				oneterm.append(")");
			}else if("<>".equals(oper.trim()))
			{
				oneterm.append(logtag);
				oneterm.append("(");
				oneterm.append(fieldname);
				oneterm.append(" is not");
				oneterm.append(" null ");
				oneterm.append(" or ");
				oneterm.append(fieldname);
				oneterm.append(" <>");
				oneterm.append(" '' ");
				oneterm.append(")");
			}else
			{
				oneterm.append(logtag);
				oneterm.append(fieldname);
				oneterm.append(oper);
				oneterm.append("'"+value+"'");
			}
		}
		/**结束**/
		//原来的
//		oneterm.append(logtag);
//		oneterm.append(fieldname);
//		if(oper.trim().equals("like"))
//		{
//			oneterm.append(" "+oper+" ");
//			oneterm.append(" '%"+value+"%' ");
//		}else
//		{
//			oneterm.append(oper);
//			oneterm.append("'"+value+"'");
//		}	
						
		
		return oneterm.toString();
	} 
	 public String managerWhere(String kind,String code,ArrayList factorlist)
	    {
	    	StringBuffer where=new StringBuffer();
	    	StringBuffer whereTrem=new StringBuffer();
			whereTrem.append("(1=1");
			String dbpre="";
			ArrayList dblist=new ArrayList();
			boolean isCorrect=false;//判断是否选择人员库
			
			for(int i=0;i<factorlist.size();i++)
			{
				Factor factor = (Factor)factorlist.get(i);
				if("nbase".equals(factor.getFieldname().trim()))
				{
					dbpre=factor.getValue();
				}else
				{
					whereTrem.append(getOneTerm(factor));	
				}
						
			}			
			whereTrem.append(")");
	    	if("1".equals(kind))
			{
	    		where.append("e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				where.append("e01a1 like '"+code+"%'");
			}
			else 
			{
				where.append("b0110 like '"+code+"%'");			
			}
	    	where.append(" and "+whereTrem.toString());
	    	this.getFormHM().put("selectWhere", whereTrem.toString());
	    	return where.toString();
	    }
}
