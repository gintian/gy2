package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 0202001024
 * <p>Title:AutoPosidTrans.java</p>
 * <p>Description>:AutoPosidTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 15, 2010  1:53:09 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class AutoPosidTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");
			if("1".equals(opt))//检验代码是否已存在
			{
				String code=(String)this.getFormHM().get("code");
				String flag="1";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from organization where Codesetid='@K' and UPPER(codeitemid)='"+code.toUpperCase()+"'");
				while(this.frowset.next())
				{
					flag="2";
				}
				this.getFormHM().put("flag", flag);
			}
			else if("2".equals(opt))//取得机构下代码规则，自动生成一个代码
			{
				String orgID=(String)this.getFormHM().get("orgID");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sqlbuf="select * from organization where codesetid='@K' and codeitemdesc='待岗' and parentid='"+orgID+"' and parentid<>codeitemid";
				this.frowset=dao.search(sqlbuf);
				CommonData cd;
				ArrayList list=new ArrayList();
				StringBuffer innerhtml=new StringBuffer();
				
				while(this.frowset.next()){
					 cd=new CommonData();
					cd.setDataName(this.frowset.getString("codeitemdesc"));
					cd.setDataValue(this.frowset.getString("codeitemid"));
					list.add(cd);
				
				}
				if(list.size()==0){
					int allowLength=30-orgID.length();
					int alength=2;
					String oldValue="";
					String sql="select "+Sql_switcher.isnull("max("+Sql_switcher.length("codeitemid")+")","0")+",max(codeitemid) from organization where  parentid='"+orgID+"'  and parentid<>codeitemid ";
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{
						if(this.frowset.getInt(1)!=0)
						{
							oldValue=this.frowset.getString(2).substring(orgID.length());
							int a_len=this.frowset.getInt(1);
							if(a_len!=0)
							{
								allowLength=a_len-orgID.length();
								alength=a_len-orgID.length();
							}
						}
						else
						{
							allowLength=30-orgID.length();
							alength=2;
						}
					}
					StringBuffer existItemid=new StringBuffer("");
					
					String newValue="";
					this.frowset=dao.search("select codeitemid from organization where  parentid='"+orgID+"'  and parentid<>codeitemid  ");
					while(this.frowset.next())
					{
						existItemid.append("#"+this.frowset.getString("codeitemid").substring(orgID.length()));
						//oldValue=this.frowset.getString("codeitemid").substring(orgID.length());
					}
					if(existItemid.length()==0)
						newValue=autoIncrease("",alength);
					else
					{
						newValue=autoIncrease(oldValue,alength);
					}
					if(existItemid.indexOf(newValue)!=-1)
						newValue="";
					this.getFormHM().put("hascode", "false");
					this.getFormHM().put("newValue",newValue);
					if("".equals(orgID)){
						this.getFormHM().put("newValue","");
					}
					this.getFormHM().put("existItemid",existItemid.toString());				
					this.getFormHM().put("allowLength",String.valueOf(allowLength));
					this.getFormHM().put("orgID",orgID);
				}else{
					
					this.getFormHM().put("hascode", "true");
					/**
					 * dumeilong*/
					if(list!=null&&list.size()!=0){
						innerhtml.append("<select id=\"posdesc\" name=\"parameterForm2\" property=\"schoolPositionOrgDesc\" onchange=\"changecode()\">");
						 CommonData item = null;
						 innerhtml.append("<option value=\"\"></option>");
						for(int i=0;i<list.size();i++){
							item=(CommonData)list.get(i);
							innerhtml.append("<option value=\"");
							innerhtml.append(item.getDataValue());
							innerhtml.append("\">");
							innerhtml.append(item.getDataName());
							innerhtml.append("</option>");
						}
						innerhtml.append("</select>");
						innerhtml.append("<input type=\"hidden\" name=\"schoolPositionDesc\" value=\"\"/>");
						this.getFormHM().put("innerhtml", SafeCode.encode(innerhtml.toString()));
						this.getFormHM().put("orgID",orgID);//dumeilong
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public String  autoIncrease(String oldValue,int maxLength)
	{
		String newValue="";
		if(oldValue.length()==0)
		{
			for(int i=0;i<maxLength-1;i++)
			{
				newValue+="0";
			}
			newValue+="1";
		}
		else
		{
			int lastVar=String.valueOf(oldValue.charAt(oldValue.length()-1)).hashCode();
			int lastVar2=0;
			boolean isLastVar2=false;
			if(oldValue.length()>1)
			{
				lastVar2=String.valueOf(oldValue.charAt(oldValue.length()-2)).hashCode();
				isLastVar2=true;
			}
			String value2="";
			if(isLastVar2)
			{
				byte[] d={new Integer(lastVar2).byteValue()};
				value2=new String(d);
				if(lastVar==57||lastVar==122||lastVar==90)
				{
					value2=getIncreaseChar(lastVar2);
				}
			}
			String value1=getIncreaseChar(lastVar);
			if(oldValue.length()>1)
				newValue=oldValue.substring(0,oldValue.length()-2);
			newValue+=value2+value1;
			
		}
		return newValue;
	}
	public String getIncreaseChar(int value)
	{
		
		int newValue=0;
		if(value==57)
			newValue=48;
		else if(value==122)
			newValue=97;
		else if(value==90)
			newValue=65;
		else 
			newValue=++value;
		byte[] v={new Integer(newValue).byteValue()};
		return new String(v);
	}
	

}
