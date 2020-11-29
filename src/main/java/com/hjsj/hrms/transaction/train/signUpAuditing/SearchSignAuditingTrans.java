package com.hjsj.hrms.transaction.train.signUpAuditing;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public class SearchSignAuditingTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=null;
		try
		{
			 String a0101="";
			 String trainMovementID="";
			 String priv="";
			 String sp_flag = "";
			 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			 boolean isFirst=false;
			 if(hm.get("b_query")!=null&& "search".equals((String)hm.get("b_query")))
			 {
				 a0101=(String)this.getFormHM().get("a0101");
				 
				 trainMovementID=(String)this.getFormHM().get("trainMovementID");
				 trainMovementID = PubFunc.decrypt(SafeCode.decode(trainMovementID));
				 
				 sp_flag = (String)this.getFormHM().get("sp_flag");
			 }
			 else{
				 isFirst=true;
				 sp_flag = "08";
				 this.getFormHM().put("sp_flag", "08");
			 }
			 
			 dao=new ContentDAO(this.getFrameconn());
			 ArrayList studentList=new ArrayList();
//			 String[] dbArray=this.userView.getDbpriv().toString().split(",");
			 ArrayList dblist = this.userView.getPrivDbList();
			 
			 HashSet set=new HashSet();
			 if(isFirst){
				 StringBuffer sqlstr = new StringBuffer();
				 sqlstr.append(" select r3101 from r31 right join r40 on r31.r3101=r40.r4005 where R3127 in ('04','05','09') ");
				 sqlstr.append(" and R40.nbase in ('");
				 StringBuffer nbasestr = new StringBuffer();
				 for(int i=0;i<dblist.size();i++){
					 String dbnamestr = (String)dblist.get(i);
					 if(dbnamestr==null||dbnamestr.trim().length()==0)
						 continue;
					 nbasestr.append(dbnamestr+"','");
				 }
				 if(nbasestr.length()>0){
					 nbasestr.delete(nbasestr.length()-3, nbasestr.length());
					 sqlstr.append(nbasestr);
				 }
				 sqlstr.append("')");
				 frowset = dao.search(sqlstr.toString());
				 while(frowset.next()){
					 set.add(frowset.getString("r3101"));
				 }
				 this.getFormHM().put("trainMovementList",getTrainMovementList(set,"0"));
			 }
			 
			 TrainClassBo tcb = new TrainClassBo(frameconn);
			 ArrayList spList = tcb.getR40sp_flag();
			 this.getFormHM().put("spList", spList);
			 
			 for(int i=0;i<dblist.size();i++)
			 {
				 String dbnamestr = (String)dblist.get(i);
				 if(dbnamestr==null||dbnamestr.trim().length()==0)
					 continue;
				 String scope_str="select a0100 "+getPrivSQLExpression(dbnamestr);//this.getUserView().getPrivSQLExpression(dbnamestr,false);
				 String dbName=dbnamestr+"A01";
				 StringBuffer sql=new StringBuffer("select "+dbName+".a0100,"+dbName+".b0110,"+dbName+".e0122,"+dbName+".e01a1,"+dbName+".a0101");
				 sql.append(",r3130,r3101,r4013 from "+dbName+",R40,R31 where R40.R4001="+dbName+".a0100 and R40.R4005=R31.R3101 ");
				 sql.append(" and R40.nbase='"+dbnamestr+"' and R40.R4001 in ("+scope_str+") and R31.R3127 in ('04','05','09')");
				 if(sp_flag.length()>0)
				    sql.append(" and r4013='"+sp_flag+"' ");
				 if(a0101.length()>0)
				 {
					 sql.append(" and "+dbName+".a0101 like '%"+PubFunc.getStr(a0101)+"%'");
				 }
				 if(trainMovementID.length()>0)
				 {
					 sql.append(" and r31.r3101='"+trainMovementID+"' ");
				 }
				 sql.append(" order by R40.R4005");
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("dbname",SafeCode.encode(PubFunc.encrypt(dbnamestr)));
					abean.set("a0100",SafeCode.encode(PubFunc.encrypt(this.frowset.getString("a0100"))));
					abean.set("a0101",this.frowset.getString("a0101"));
					if(this.frowset.getString("b0110")!=null)
						abean.set("b0110",AdminCode.getCodeName("UN",this.frowset.getString("b0110")));
					else
						abean.set("b0110","");
					if(this.frowset.getString("e0122")!=null)
						abean.set("e0122",AdminCode.getCodeName("UM",this.frowset.getString("e0122")));
					else
						abean.set("e0122","");
					if(this.frowset.getString("e01a1")!=null)
						abean.set("e01a1",AdminCode.getCodeName("@K",this.frowset.getString("e01a1")));
					else
						abean.set("e01a1","");
					
					abean.set("r3130",this.frowset.getString("r3130")!=null?this.frowset.getString("r3130"):"");
					
					abean.set("r3101",SafeCode.encode(PubFunc.encrypt(this.frowset.getString("r3101"))));
					//set.add(this.frowset.getString("r3101"));
					abean.set("r4013",AdminCode.getCodeName("23",this.frowset.getString("r4013")));
					if(this.frowset.getString("r4013")!=null&& "08".equals(this.frowset.getString("r4013")))
						abean.set("flag","1");
					else
						abean.set("flag","0");
					studentList.add(abean);
				}
			 } 
			this.getFormHM().put("studentList",studentList);
			
			
			
			this.getFormHM().put("trainMovementList2",getTrainMovementList(null,"1"));
			this.getFormHM().put("a0101",PubFunc.getStr(a0101.trim()));
			this.getFormHM().put("trainMovementID",SafeCode.encode(PubFunc.encrypt(trainMovementID)));
			
			if(this.getUserView().getManagePrivCodeValue().length()==0)
			{
				String userDeptId=this.getUserView().getUserDeptId();
				String userOrgId=this.getUserView().getUserOrgId();
				if(userDeptId!=null&&userDeptId.trim().length()>0)
				{
					priv=userDeptId;
					
				}
				else if(userOrgId!=null&&userOrgId.trim().length()>0)
				{
					priv=userOrgId;
				
				}
			}
			else
			{
				priv=this.getUserView().getManagePrivCodeValue();
			}
			this.getFormHM().put("priv",priv);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		

	}
	
	//由业务用户得到关联的自助用户
	public UserView getUserView_zizhu()
	{
		UserView userview = null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
	
			if(userView.getA0100()!=null&&!"".equals(userView.getA0100()))
			{
				 String zpFld = "";
				 String pwdFld="";
			     RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				 if (login_vo != null)
				 {
					    String login_name = login_vo.getString("str_value");
						int idx = login_name.indexOf(",");
						if (idx != -1)
						{
						    zpFld = login_name.substring(0, idx);
						    if(login_name.length()>idx)
						       pwdFld=login_name.substring(idx+1);
						}
				}
					if("".equals(pwdFld)|| "#".equals(pwdFld))
						   pwdFld="userpassword";
					if("".equals(zpFld)|| "#".equals(zpFld))
						zpFld="username";
			        	try
						{
							this.frowset=dao.search("select "+zpFld+","+pwdFld+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'");
				
			        	if(this.frowset.next())
			        	{
			        		String name=this.frowset.getString(zpFld)==null?"":this.frowset.getString(zpFld);
			        		String pw=this.frowset.getString(pwdFld)==null?"":this.frowset.getString(pwdFld);
			        		 userview = new UserView(name,pw,this.frameconn);
			        		try
							{
			        			userview.canLogin();
							} catch (Exception e)
							{
								e.printStackTrace();
							}
							
			        	}
						} catch (SQLException e)
						{

							e.printStackTrace();
						}
			        }
			
		return userview;
	}
	
	
	public ArrayList getTrainMovementList(HashSet set,String operate)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			list.add(new CommonData("",""));
			if("1".equals(operate))
			{
			/*	String codeid="";
				String codeset="";
				String userDeptId=this.getUserView().getUserDeptId();
				String userOrgId=this.getUserView().getUserOrgId();
				if(!this.getUserView().isAdmin()&&!this.getUserView().getGroupId().equals("1"))
				{
					if(this.getUserView().getManagePrivCodeValue().length()==0)
					{
						if(userDeptId!=null&&userDeptId.trim().length()>0)
						{
							codeid=userDeptId;
							codeset="UM";
						}
						else if(userOrgId!=null&&userOrgId.trim().length()>0)
						{
							codeid=userOrgId;
							codeset="UN";
						}
					}
					else
					{
						codeid=this.getUserView().getManagePrivCodeValue();
						codeset=this.getUserView().getManagePrivCode();
					}
				}*/
				StringBuffer sql=new StringBuffer("select r3101,r3130,r3113,r3114 from r31 where  r3127='04'");  //04已发布，05执行中
			/*	if(!codeid.equals(""))
				{
					if(codeset.equals("UN"))
					{
						sql.append(" and b0110 like '"+codeid+"%' ");
					}
					else if(codeset.equals("UM"))
					{
						sql.append(" and e0122 like '"+codeid+"%' ");
					}
					
				}*/
				HashMap map = new HashMap();
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					Date r3113=this.frowset.getDate("r3113");    //报名开始时间
					Date r3114=this.frowset.getDate("r3114");    //报名结束时间	
					String r3101=this.frowset.getString("r3101");
					if(isOverTime(r3113,r3114))
					{
							continue;
					}
					if(!this.getUserView().isHaveResource(IResourceConstant.TRAINJOB,r3101))
						continue;
					
					map.put(this.frowset.getString("r3101"),this.frowset.getString("r3130"));
					CommonData data=new CommonData(SafeCode.encode(PubFunc.encrypt(this.frowset.getString("r3101"))),this.frowset.getString("r3130"));
					list.add(data);
				}
				
				//通过业务用户获得关联的自助用户 获取有权限的培训班
				UserView userview2=null; 
				if(userView.getStatus()==0)//业务用户
					userview2 = this.getUserView_zizhu();
				if(userview2!=null)//自助用户
				{
					this.frowset=dao.search(sql.toString());
					while(this.frowset.next())
					{
						Date r3113=this.frowset.getDate("r3113");    //报名开始时间
						Date r3114=this.frowset.getDate("r3114");    //报名结束时间	
						String r3101=this.frowset.getString("r3101");
						if(isOverTime(r3113,r3114))
						{
								continue;
						}
					
							if(!userview2.isHaveResource(IResourceConstant.TRAINJOB,r3101))
								continue;
						
							if(map.get(this.frowset.getString("r3101"))==null)
							{
								CommonData data=new CommonData(SafeCode.encode(PubFunc.encrypt(this.frowset.getString("r3101"))),this.frowset.getString("r3130"));
								list.add(data);
							}					
					}
				}		
			}
			else if("0".equals(operate))
			{
				StringBuffer whl=new StringBuffer("");
				for(Iterator t=set.iterator();t.hasNext();)
				{
					whl.append(",'"+(String)t.next()+"'");
				}
				if(whl.length()>0)
				{
					this.frowset=dao.search("select r3101,r3130 from r31 where r3101 in ("+whl.substring(1)+")");
					
					while(this.frowset.next())
					{
						CommonData data=new CommonData(SafeCode.encode(PubFunc.encrypt(this.frowset.getString("r3101"))),this.frowset.getString("r3130"));
						list.add(data);
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	

	public boolean isOverTime(Date startDate,Date endDate)
	{
		boolean flag=false;
		if(startDate!=null&&endDate!=null)
		{
			Calendar today=Calendar.getInstance();
			Calendar appealStart=Calendar.getInstance();
			Calendar appealEnd=Calendar.getInstance();
			appealStart.setTime(DateUtils.addDays(startDate,-1));
			appealEnd.setTime(DateUtils.addDays(endDate,1));
			if(today.before(appealStart)||today.after(appealEnd))
				flag=true;
		}else if(startDate==null&&endDate!=null){
			Calendar today=Calendar.getInstance();
			Calendar appealEnd=Calendar.getInstance();
			appealEnd.setTime(DateUtils.addDays(endDate,1));
			if(today.after(appealEnd))
				flag=true;
		}else if(startDate!=null&&endDate==null){
			Calendar today=Calendar.getInstance();
			Calendar appealStart=Calendar.getInstance();
			appealStart.setTime(DateUtils.addDays(startDate,-1));
			if(today.before(appealStart))
				flag=true;
		}
		return flag;
	}
	
	private String getPrivSQLExpression(String dbname){
		StringBuffer buf = new StringBuffer(" from "+dbname+"A01 ");
		try{
		    if(!this.userView.isSuper_admin()){
		        TrainCourseBo bo = new TrainCourseBo(this.userView);
		        String code = bo.getUnitIdByBusi();
		        if(code.length()>2&&code.toUpperCase().indexOf("UN`")==-1){
		            String tmp[] = code.split("`");
		            buf.append(" where ");
		            for (int i = 0; i < tmp.length; i++) {
		                String t = tmp[i];
		                if(t.toUpperCase().startsWith("UN"))
		                    buf.append(dbname+"A01.b0110 like '"+t.substring(2)+"%' or ");
		                else if(t.toUpperCase().startsWith("UM"))
		                    buf.append(dbname+"A01.e0122 like '"+t.substring(2)+"%' or ");
		                else if(t.toUpperCase().startsWith("@K"))
		                    buf.append(dbname+"A01.e01a1 like '"+t.substring(2)+"%' or ");
		            }
		            if(buf.toString().endsWith("or "))
		                buf.setLength(buf.length()-4);
		        }
		    }
		}catch (Exception e) {
		    e.printStackTrace();
        }
		return buf.toString();
	}
}
