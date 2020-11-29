package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class getPosListByOrgIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		String orgID=(String)this.getFormHM().get("orgID");
		String operator=(String)this.getFormHM().get("operator");
		String hirepath = this.getFormHM().get("hirepath")==null?"":(String)this.getFormHM().get("hirepath");
		String id = this.getFormHM().get("id")==null?"":(String)this.getFormHM().get("id");
		id = com.hjsj.hrms.utils.PubFunc.decrypt(id);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		try
		{
			if("1".equals(operator))
			{
				ArrayList list=new ArrayList();
				list.add(new CommonData("",""));
				String isOrgWillTableIdDefine=(String)this.getFormHM().get("isOrgWillTableIdDefine");
				if(isOrgWillTableIdDefine==null)
					isOrgWillTableIdDefine="0";
//不注释，单位下建的岗位 下级的部门可选
//				if(AdminCode.getCodeName("UM",orgID)!=null&&AdminCode.getCodeName("UM",orgID).length()>0)  //部门
//				{
//					String un_id="";
//					//寻找直属单位下的职位信息
//					String acodeitemid=orgID;
//					while(true)
//					{
//						this.frowset=dao.search("select codesetid,codeitemid,parentid from  organization where codeitemid=(select parentid from organization where codeitemid='"+acodeitemid+"')");
//						if(this.frowset.next())
//						{
//							String codesetid=this.frowset.getString("codesetid");
//							String codeitemid=this.frowset.getString("codeitemid");
//							String parentid=this.frowset.getString("parentid");
//							if(codesetid.equalsIgnoreCase("UN"))
//							{
//								un_id=codeitemid;
//								break;
//							}
//							else
//								acodeitemid=codeitemid;
//						}
//					}
//					
//				    this.frowset=dao.search("select * from organization where codesetid='@K' and parentid='"+un_id+"' and codeitemid in (select E01A1 from K01) and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
//					while(this.frowset.next())
//					{
//						CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
//						list.add(data);
//					}
//				}
				try
				{	
					String sql="select * from organization where codesetid='@K' and parentid='"+orgID+"' and ( codeitemid in (select E01A1 from K01) and "+Sql_switcher.dateValue(bosdate)
					+" between start_date and end_date  or codeitemdesc='待定' )";
					this.frowset=dao.search(sql);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new Exception("岗位基本情况主集K01未构库！"));
				}
				while(this.frowset.next())
				{
					CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					list.add(data);
				}
				if(this.userView.hasTheFunction("310115")||this.userView.hasTheFunction("0A035"))
	     			list.add(new CommonData("-1","新建岗位..."));
				this.getFormHM().put("poslist",list);
				this.getFormHM().put("isOrgWillTableIdDefine", isOrgWillTableIdDefine);
				this.getFormHM().put("orgID",orgID);
				PositionDemand pd=new PositionDemand(this.getFrameconn());
				String unCode=pd.getUnitCode(orgID);
				this.getFormHM().put("unCode", unCode);
				PosparameXML pos = new PosparameXML(this.getFrameconn());  
				/**=1控制到部门，=0控制到单位*/
				String bzctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
				GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap hm = XMLbo.getValuesMap();
				/**=0控制到部门*/
				String gzctrl_type = "";
				if(hm!=null&&hm.get("ctrl_type")!=null){//dml 2011-6-21 15:21:21
					 gzctrl_type = (String) hm.get("ctrl_type");
				}
				
				if((bzctrl_type!=null&& "1".equals(bzctrl_type))||gzctrl_type!=null&& "0".equals(gzctrl_type))
					this.getFormHM().put("showUM", "1");
				else
					this.getFormHM().put("showUM","0");
			}
			else if("2".equals(operator))  //取得该组织下的代码规则
			{
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
				this.frowset =dao.search("select codeitemid from vorganization where  parentid='"+orgID+"'  and parentid<>codeitemid  ");
				while(this.frowset.next())
				{
					existItemid.append("#"+this.frowset.getString("codeitemid").substring(orgID.length()));
				}
				if(existItemid.length()==0)
					newValue=autoIncrease("",alength);
				else
				{
					newValue=autoIncrease(oldValue,alength);
				}
				if(existItemid.indexOf(newValue)!=-1)
					newValue="";
				
				this.getFormHM().put("newValue",newValue);
				this.getFormHM().put("existItemid",existItemid.toString());				
				this.getFormHM().put("allowLength",String.valueOf(allowLength));
				this.getFormHM().put("orgID",orgID);
			}
			else if("3".equals(operator))
			{
				ArrayList list=new ArrayList();
				list.add(new CommonData("",""));
				int yy=0;
				int mm=0;
				int dd=0;
				Calendar d=Calendar.getInstance();
				Date date = new Date();
				d.setTime(date);  //本周期时间 
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
				StringBuffer sql = new StringBuffer();
				sql.append("  and ( "+Sql_switcher.year("end_date")+">"+yy);
				sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 				
				
					this.frowset=dao.search("select * from organization where codesetid='@K' and codeitemid like '"+orgID+"%' and codeitemid in (select E01A1 from K01) "+sql.toString()+" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date or codeitemdesc='待定'");
				
				while(this.frowset.next())
				{
				    if("待定".equalsIgnoreCase(this.frowset.getString("codeitemdesc")))
				        continue;
				    
					CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					list.add(data);
				}				
				this.getFormHM().put("poslist",list);
			}	
			else if("4".equals(operator))
			{
				ArrayList list=new ArrayList();
				int yy=0;
				int mm=0;
				int dd=0;
				Calendar d=Calendar.getInstance();
				Date date = new Date();
				d.setTime(date);  //本周期时间 
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
				StringBuffer sql = new StringBuffer();
				sql.append("  and ( "+Sql_switcher.year("oz.end_date")+">"+yy);
				sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+">"+mm+" ) ");
				sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+"="+mm+" and "+Sql_switcher.day("oz.end_date")+">="+dd+" ) ) ");
				sql.append(" and ( "+Sql_switcher.year("oz.start_date")+"<"+yy);
				sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"<"+mm+" ) ");
				sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"="+mm+" and "+Sql_switcher.day("oz.start_date")+"<="+dd+" ) ) ");	 				
				boolean flag = false;
				StringBuffer sql2 =new StringBuffer();
				FieldItem hireMajoritem=null;
				String hireMajor="";//dml 2011-03-29
				if(hirepath.length()>0){
					if("01".equals(hirepath)){
						ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
						HashMap map0 = bo2.getAttributeValues();
						//String hireMajor="";		//xieguiquan 2010-09-17
						if(map0.get("hireMajor")!=null)
							hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
						
						if(hireMajor.length()>0&&id.length()>0)
						{
							hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
							if(hireMajoritem!=null&&hireMajoritem.isCode()){//dml 2011-03-29  
								sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
								sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"'  and codeitemid in ( ");
								sql2.append(" select   z1."+hireMajor+" from (select "+hireMajor+" from z03  where z0325 like '"+orgID+"%'  and z0336='01' ) z1 ))oz, ");
								sql2.append(" (select   z2."+hireMajor+",z2.z0301,z2.z0319 from (select "+hireMajor+",z0301,z0319 from z03  where z0325 like '"+orgID+"%'  and z0336='01' )z2)  z");
								sql2.append(" where oz.codesetid='"+hireMajoritem.getCodesetid()+"'and oz.codeitemid=z."+hireMajor+"  and z.z0319<>'01' ");
							}
							if(hireMajoritem!=null&&!hireMajoritem.isCode()&&orgID!=null&&orgID.length()!=0){//dml 2011-03-29 兼容非代码型
								sql2.append("select z0301, ");
								sql2.append(hireMajor);
								sql2.append(" from z03 where (z0325 like '");
								sql2.append(orgID);
								sql2.append("%' or z0321 like'");
								sql2.append(orgID);
								sql2.append("%')  and z0336='01' and z0319<>'01'");
								
							}
						}
						
					}else{
						sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
						sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from organization where codesetid='@K'  and codeitemid in ( ");
						sql2.append(" select   z1.z0311 from (select z0311 from z03  where z0325 like '"+orgID+"%'  and z0336<>'01' ) z1 ))oz, ");
						sql2.append(" (select   z2.z0311,z2.z0301,z2.z0319 from (select z0311,z0301,z0319 from z03  where z0325  like '"+orgID+"%'  and z0336<>'01' )z2)  z");
						sql2.append(" where oz.codesetid='@K'and oz.codeitemid=z.z0311  and z.z0319<>'01' ");
					}
				}
				if(sql2.length()!=0){//dml 2011-03-29 兼容非代码型
					this.frowset=dao.search(sql2.toString());
					if(hirepath.length()>0){}{
					if(hireMajoritem!=null&&!hireMajoritem.isCode()){
						while(this.frowset.next()){
							CommonData data=new CommonData(hireMajor+"-"+this.frowset.getString("z0301"),this.frowset.getString(2));
							list.add(data);

						}
					}else
						while(this.frowset.next())
						{
							CommonData data=new CommonData(this.frowset.getString("codeitemid")+"-"+this.frowset.getString("z0301"),this.frowset.getString("codeitemdesc"));
							list.add(data);
						}
					}
				}
//					this.frowset=dao.search(sql2.toString());
//				
//				while(this.frowset.next())
//				{
//					CommonData data=new CommonData(this.frowset.getString("codeitemid")+"-"+this.frowset.getString("z0301"),this.frowset.getString("codeitemdesc"));
//					list.add(data);
//				}				
				this.getFormHM().put("poslist",list);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

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
	
}
