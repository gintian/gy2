package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
public class ValidateIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			String blackFieldItem=(String)this.getFormHM().get("blackFieldItem");
			String blackNbase=(String)this.getFormHM().get("blackNbase");
			String blackFieldValue=SafeCode.decode((String)this.getFormHM().get("blackFieldValue")).trim();
			blackFieldValue=PubFunc.getReplaceStr(blackFieldValue);
			String msg="0";
			/**身份证号校验*/
			if(type==null|| "0".equals(type))
			{
		    	String idValue=(String)this.getFormHM().get("idValue");
		    	String idItem=(String)this.getFormHM().get("idItem");
		    	String dbname=(String)this.getFormHM().get("dbname");
		    	String a0100=(String)this.getFormHM().get("a0100");
		    	idValue=PubFunc.getReplaceStr(idValue);
		    	a0100=PubFunc.getReplaceStr(a0100);
		    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		    	this.frowset=dao.search("select * from "+dbname+"A01 where a0100<>'"+a0100+"' and  "+idItem+"='"+idValue.trim()+"'");
		    	if(this.frowset.next())
		    	{
		    		msg="1";
		    		this.getFormHM().put("info","failue");
		    	}
		    	else
			    	this.getFormHM().put("info","success");
			}
			/**唯一性指标校验*/
			else if("1".equals(type))
			{
				
				String dbname=(String)this.getFormHM().get("dbname");
				String str=SafeCode.decode((String)this.getFormHM().get("only_str"));
				String a0100=(String)this.getFormHM().get("a0100");
				String[] arr=str.split(",");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					String arr_i=arr[i];
					//itemid/value/itemdesc
					String[] c_arr=arr_i.split("/");
					if(ifHasRecord(dbname,c_arr,a0100))
					{
						msg="本系统中已有 "+c_arr[2]+" 为 "+c_arr[1]+" 的记录，不能重复录入";
						break;
					}
				}
			}
			if("0".equals(msg))
			{
				if(blackFieldItem!=null&&!"".equals(blackFieldItem)&&blackNbase!=null&&!"".equals(blackNbase)&&!"".equals(blackFieldValue))
				{
					if(blackFieldValue==null|| "".equals(blackFieldValue))
					{
						FieldItem item=DataDictionary.getFieldItem(blackFieldItem.toUpperCase());
						if(item==null)
							item=DataDictionary.getFieldItem(blackFieldItem.toLowerCase());
						msg=item.getItemdesc()+" 为黑名单检验指标，不能为空！";
		    			this.getFormHM().put("info","failue");
					}
					else
					{
			    		EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
				    	String dbname=(String)this.getFormHM().get("dbname");
				       	String a0100=(String)this.getFormHM().get("a0100");
				    	if(blackNbase.equalsIgnoreCase(dbname))
				    	{
				    		if(bo.isBlackPersonTheSameDB(blackFieldItem, blackNbase, blackFieldValue, a0100))
				    		{
				    			msg="您已经被系统列入黑名单，不能提交简历！";
			        			this.getFormHM().put("info","failue");
					    	}
				    		else
				    		{
				    			this.getFormHM().put("info","success");
				    		}
			    		}
				    	else
				    	{
			        		if(bo.isBlackPerson(blackFieldItem, blackNbase, blackFieldValue))
			        		{
			        			msg="您已经被系统列入黑名单，不能提交简历！";
			    	    		this.getFormHM().put("info","failue");
				        	}
			    	    	else
			        		{
			        			this.getFormHM().put("info","success");
			         		}
					   }
					}
				}
				else
				{
				    this.getFormHM().put("info","success");
				}
			}
			else
				this.getFormHM().put("info","failue");
			this.getFormHM().put("msg", SafeCode.encode(msg));
			this.getFormHM().put("type",type);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public boolean ifHasRecord(String dbName,String[] value,String a0100)
	{
		boolean flag=false;
		try
		{
			a0100=PubFunc.getReplaceStr(a0100);
			value[1]=PubFunc.getReplaceStr(value[1]);
			String sql="select a0100 total from "+dbName+"a01 where UPPER("+value[0].toUpperCase()+")='"+value[1].toUpperCase()+"' and a0100<>'"+a0100+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String xx=rs.getString("total")==null?"0":rs.getString("total");
				if(Integer.parseInt(xx)>=1)
					flag=true;
				//break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
