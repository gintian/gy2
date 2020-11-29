package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SetResumeStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{

			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
			String operate=(String)hm.get("operate");  // set:设置状态 del:删除 switch:转入人才库
			String state=(String)hm.get("state");
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname=vo.getString("str_value");
			
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=xmlBo.getAttributeValues();
			String resume_state="";
			if(map.get("resume_state")!=null)
				resume_state=(String)map.get("resume_state");
			if("".equals(resume_state))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置简历状态指标！"));
			
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl2=new StringBuffer("");
			StringBuffer a0100s=new StringBuffer("");
			StringBuffer zppos=new StringBuffer("");
			ArrayList a0100List = new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				String a0100=(String)abean.get("a0100");
				String zp_pos_id=(String)abean.get("zp_pos_id");
				zppos.append(" ,"+zp_pos_id);
				whl.append(" or ( a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"' ) ");
				whl2.append(" or  a0100='"+a0100+"' ");
				a0100s.append(",'"+a0100+"'");
				a0100List.add(a0100);
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			EmployNetPortalBo bo1=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			ArrayList alist1=bo1.getZpFieldList();
			HashMap fieldMap=(HashMap)alist1.get(1);
			ArrayList fieldList=(ArrayList)fieldMap.get("A01");//DataDictionary.getFieldList("A01",Constant.USED_FIELD_SET);	
			if(fieldList==null)
				fieldList=(ArrayList)fieldMap.get("a01");
			
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			for(int i=0;i<fieldList.size();i++)
			{
				String id=(String)fieldList.get(i);
				FieldItem item=DataDictionary.getFieldItem(id.toLowerCase());
				if(item==null){
					throw GeneralExceptionHandler.Handle(new Exception(dbname+"A01的"+id+"字段无效!,请重新配置应聘人才库基本信息中的指标"));
				}
			}
			String person_type="";
			if(map.get("person_type")!=null)
				person_type=(String)map.get("person_type");
			if("".equals(person_type))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置人才库标识指标！"));
			if("set".equals(operate))
			{
				StringBuffer sql=new StringBuffer("select distinct "+dbname+"a01.a0100 from "+dbname+"a01,");
				sql.append(" (select * from zp_pos_tache where  "+whl.substring(3)+"  ) zpt ");
				sql.append(" where "+dbname+"a01.a0100=zpt.a0100  and "+dbname+"a01."+resume_state+"<>zpt.resume_flag and  "+dbname+"a01."+resume_state+"<>'10' and "+dbname+"a01."+resume_state+"<>'13' ");
				sql.append("  and "+dbname+"a01.a0100 in ("+a0100s.substring(1)+") ");
			    StringBuffer _a0100s=new StringBuffer("");
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					_a0100s.append(",'"+this.frowset.getString(1)+"'");
				}
				if(_a0100s.length()>0)
				{
					dao.update("update zp_pos_tache set resume_flag='"+state+"' where ( "+whl.substring(3)+" ) and a0100 not in ( "+_a0100s.substring(1)+" ) ");
					dao.update("update "+dbname+"a01 set "+resume_state+"='"+state+"' where a0100 in ("+a0100s.substring(1)+")  and a0100 not in ( "+_a0100s.substring(1)+" ) ");
					if("12".equals(state))
					{
						dao.update("update zp_pos_tache set username='"+this.getUserView().getUserName()+"' where ( "+whl.substring(3)+" ) and a0100 not in ( "+_a0100s.substring(1)+" ) ");
					}
				}
				else
				{
					dao.update("update zp_pos_tache set resume_flag='"+state+"' where ( "+whl.substring(3)+" ) ");
					dao.update("update "+dbname+"a01 set "+resume_state+"='"+state+"' where a0100 in ("+a0100s.substring(1)+")  ");
					if("12".equals(state))
						dao.update("update zp_pos_tache set username='"+this.getUserView().getUserName()+"' where ( "+whl.substring(3)+" ) ");
				}
				/**如果将简历置为已选，如果基本情况子集中有系统序号指标，生成值*/
				if("12".equals(state))
				{
					
					for(int i=0;i<fieldList.size();i++)
					{
						String id=(String)fieldList.get(i);
						FieldItem item=DataDictionary.getFieldItem(id.toLowerCase());
						if(item==null){
						    throw GeneralExceptionHandler.Handle(new Exception(dbname+"A01的"+id+"字段无效!,请重新配置应聘人才库基本信息中的指标"));
						}
						if(!item.isSequenceable())
							continue;
						String prefix_field=item.getSeqprefix_field();
						int prefix=item.getPrefix_field_len();
						for(int j=0;j<a0100List.size();j++)
						{
							String person=(String)a0100List.get(j);
							RecordVo pvo = new RecordVo(dbname+"A01");
							pvo.setString("a0100",person);
							pvo=dao.findByPrimaryKey(pvo);
							if(pvo.getString(item.getItemid())==null|| "".equals(pvo.getString(item.getItemid())))
							{
						    	String prefix_value="";
						    	String temp="";
						    	if(prefix_field!=null&&prefix_field.trim().length()>0)
						    		prefix_value=pvo.getString(prefix_field.toLowerCase());
						    	if(prefix_value==null)
							    	prefix_value="";
						    	temp=prefix_value;
						    	if(prefix_value.length()>prefix&&prefix_field!=null&&prefix_field.length()>0)
						    		prefix_value=prefix_value.substring(0,prefix);
						    	String backfix="";
						    	if(prefix_value!=null&&prefix_value.length()>0)
						    		backfix="_"+prefix_value;
						    	RecordVo idFactory=new RecordVo("id_factory");
						    	idFactory.setString("sequence_name", item.getFieldsetid().toUpperCase()+"."+item.getItemid().toUpperCase()+backfix);
						    	String sequ_value="";
						    	/**如果该序号还没建立，取没有前缀的序号*/
						    	if(dao.isExistRecordVo(idFactory))
						    	{
						    		sequ_value=idg.getId(item.getFieldsetid().toUpperCase()+"."+item.getItemid().toUpperCase()+backfix);
						    	}
						    	else
						    	{
						    		sequ_value=idg.getId(item.getFieldsetid().toUpperCase()+"."+item.getItemid().toUpperCase());
						    	}
						    	
						    	String value=prefix_value+sequ_value;
						    	pvo.setString(item.getItemid(), value);
						    	dao.updateValueObject(pvo);
							}
						}
					}
					//将已选人员 改为 待通知状态 在z05表插入记录 wangrd 2014-05-28 /
		           InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		           String codeid="";
		           if (!this.userView.isSuper_admin())
		               codeid= this.userView.getUnitIdByBusi("7");
		           interviewEvaluatingBo.changeState(codeid,dbname,resume_state);      
				}
				VersionControl vc = new VersionControl();
				if(zppos.toString().length()>0&& "12".equals(state)&&vc.searchFunctionId("31015"))
				{
					String str=zppos.toString().substring(1);
					/**给招聘订单负责人发邮件*/
					String fromAddr=this.getFromAddr();
					if(fromAddr==null|| "".equals(fromAddr))
					{
						throw GeneralExceptionHandler.Handle(new Exception("系统未设置邮件服务器，不能发送邮件！"));
					}
					EMailBo bo=new EMailBo(this.getFrameconn(),true,"");
					String title="招聘订单处理";
					String[] arr = str.split(",");
					String[] pre= this.getPre(dao);
					String emailField=this.getEmailField();
					String zpFld = "";
					RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
					if (login_vo != null)
					{
					    String login_name = login_vo.getString("str_value");
						int idx = login_name.indexOf(",");
						if (idx != -1)
						    zpFld = login_name.substring(0, idx);
					 }
					if ("".equals(zpFld)|| "#".equals(zpFld))
					    zpFld = "username";
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					for(int i=0;i<arr.length;i++)
					{
						String id=arr[i];
						String z04sql = "select * from z04 where z0407='"+id+"'";
						RowSet rset=dao.search(z04sql);
						String content="";
						while(rset.next())
						{
				            String z0409=rset.getString("Z0414");
				            if(z0409==null|| "".equals(z0409.trim()))
				            	continue;
				            HashMap infoMap = this.getHashMapFromSet(z0409, emailField, zpFld, pre, dao);
				            String a0101 = (String)infoMap.get("a0101");
				            String email=(String)infoMap.get("email");
				            content+=a0101+"您好：<br>";
				            content+="您负责的招聘订单 "+AdminCode.getCodeName("UN",rset.getString("z0404"));
				            content+="/"+AdminCode.getCodeName("UM",rset.getString("z0405"));
				            content+="/"+AdminCode.getCodeName("@K",rset.getString("z0403"));
				            content+=" 有简历设置为已选状态，请及时查阅处理！<br>";
				            content+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+format.format(new Date());
				            if(email!=null&&!"".equals(email))
				            {
				            	 bo.sendEmail(title,content,"",fromAddr,email);
				            	 break;
				            }
				            
						}
					}
				}
			}
			else if("del".equals(operate))
			{
				String    resumeState=(String)this.getFormHM().get("resumeState");
				String personType=(String)hm.get("personType");
				/**应聘简历模块*/
				if("0".equals(personType))
				{
					if("-2".equals(resumeState)|| "-3".equals(resumeState))
						return;
					/**resumeState=-1是未选职位的*/
					if(!"-1".equals(resumeState))
					{
						for(int i=0;i<list.size();i++)
						{
							LazyDynaBean abean=(LazyDynaBean)list.get(i);
							String a0100=(String)abean.get("a0100");
							String zp_pos_id=(String)abean.get("zp_pos_id");
							dao.update("update zp_pos_tache set thenumber=thenumber-1 where a0100='"+a0100+"' and thenumber>(select thenumber from zp_pos_tache where a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"' )");
							dao.delete("delete from zp_pos_tache where  a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"'",new ArrayList());
						}
						//dao.delete("delete from  zp_pos_tache  where "+whl.substring(3),new ArrayList());
						/**待选和已选设置成未选*/
						//zxj 20150326 除未选和未通过两种状态，其它阶段的在删除后都应置回初始状态未选
						if(!"10".equals(resumeState)&&!"13".equals(resumeState))
							dao.update("update "+dbname+"A01 set "+resume_state+"='10' where "+whl2.substring(3));
					}
					/**删除未选职位的，将对应招聘信息全部删除(防止存在垃圾数据)*/
					else
					{
						EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());
						ArrayList list1=bo.getZpFieldList();
						for(int i=0;i<((ArrayList)list1.get(0)).size();i++)
						{
							LazyDynaBean abean=(LazyDynaBean)((ArrayList)list1.get(0)).get(i);
							String setid=(String)abean.get("fieldSetId");
							dao.delete("delete from "+dbname+setid+" where  a0100 in ("+a0100s.substring(1)+") ",new ArrayList());
							dao.delete("delete from  zp_pos_tache  where a0100 in("+a0100s.substring(1)+") ",new ArrayList());
						}
						dao.delete("delete from "+dbname+"a00 where a0100 in("+a0100s.substring(1)+")", new ArrayList());
						
						
					}
				}
				/**我的收藏夹*/
				else if("4".equals(personType))
				{
					dao.delete("delete from  zp_resume_pack   where a0100 in ("+a0100s.substring(1)+") and nbase='"+dbname+"' and  logonname='"+this.getUserView().getUserId()+"'",new ArrayList());
					
				}
				/**删除面试记录子集信息*/
				String remenberExamineSet="";
				if(map!=null&&map.get("remenberExamineSet")!=null)
				{
					remenberExamineSet=(String)map.get("remenberExamineSet");
				}
				String isRemenberExamine="0";
				if(map!=null&&map.get("isRemenberExamine")!=null)
				{
					isRemenberExamine=(String)map.get("isRemenberExamine");
				}
				if("1".equals(isRemenberExamine)&&remenberExamineSet!=null&&!"".equals(remenberExamineSet)&&!"#".equals(remenberExamineSet))
				{
					dao.delete("delete from "+dbname+remenberExamineSet+" where a0100 in ("+a0100s.substring(1)+")", new ArrayList());
				}
			}
			else if("switch".equals(operate))
			{
				
			//	dao.delete("delete from zp_pos_tache where a0100 in ("+a0100s.substring(1)+")",new ArrayList());
				/*String codeid="";
				if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
				{
					codeid=" ";
				}
				else
				{
					if(this.userView.getStatus()==0)
					{
						String code=this.userView.getUnit_id();
						if(code==null||code.equals(""))
						{
							return;
						}
						else if(code.trim().length()==3)
						{
							code=" ";
						}
						else
						{
							if(code.indexOf("`")==-1)
							{
								code=code.substring(2);
							}else
							{
								String[] temp=code.split("`");
								for(int i=0;i<temp.length;i++)
								{
									if(temp[i]==null||temp[i].equals(""))
										continue;
									code=temp[i].substring(2);
									break;
								}
							}
						}
						codeid=code;
					}
					else if(this.userView.getStatus()==4)
					{
						String codeset=this.userView.getManagePrivCode();
						String codevalue=this.userView.getManagePrivCodeValue();
						if(codeset==null||codeset.equals(""))
						{
							return;
						}else
						{
							if(codevalue==null||codevalue.equals(""))
							{
								codevalue=" ";
							}
						}
						codeid=codevalue;
					}
				}*/
				String personType=(String)hm.get("personType");
				if("0".equals(personType))       //转入人才库,b0110='"+codeid+"'
					dao.update("update "+dbname+"a01 set "+person_type+"='1' where a0100 in ("+a0100s.substring(1)+") ");
				else if("1".equals(personType))  //移至应聘库
					dao.update("update "+dbname+"a01 set "+person_type+"='0'  where a0100 in ("+a0100s.substring(1)+") ");
			}
			else if("selectPos".equals(operate))
			{
			//	dao.update("update "+dbname+"a01 set "+resume_state+"='10',"+person_type+"='0'  where a0100 in ("+a0100s.substring(1)+") ");
				
				
				String z0301s=(String)hm.get("z0301s"); 
				String[] z0301_array=z0301s.split("~");
				StringBuffer str=new StringBuffer("");
				for(int i=0;i<z0301_array.length;i++)
				{
					String tempz0301 = PubFunc.decrypt(z0301_array[i]);
					str.append(",'"+tempz0301+"'");	
				}
				//优化一下 推荐岗位会导致将以选择岗位删掉导致招聘外网已应聘岗位志愿出错和应聘简历中已选状态简历被删除 ，
				// 解决方法将推荐人中已有被推荐岗位中含有的方位过滤掉，不再推荐给已有该岗位的人且不删掉该岗位，就解决这个问题 2012年2月20日13:38:30 dml
				HashMap apos=this.getHasSelect(a0100s.substring(1), str.substring(1));
				for(int l=0;l<a0100List.size();l++){
					String a0100=(String)a0100List.get(l);
					String delsql="delete from zp_pos_tache where  a0100='"+a0100+"' and zp_pos_id in("+str.substring(1)+")";
					if(apos.get(a0100)!=null){
						String tem="";
						HashMap tt=(HashMap)apos.get(a0100);
						Set key = tt.keySet();
					    for (Iterator it = key.iterator(); it.hasNext();) {
					       String s = (String) it.next();
					       tem+=",'"+s+"'";
					    }
					   if(tem.trim().length()>0){
						   delsql+=" and zp_pos_id not in("+tem.substring(1)+")";
					   }
					}else{
						
					}
					dao.delete(delsql,new ArrayList());	
				}
//				dao.delete("delete from zp_pos_tache where  a0100 in ("+a0100s.substring(1)+") and zp_pos_id in ("+str.substring(1)+")",new ArrayList());				
				ArrayList alist=new ArrayList();
				String sql="select a0100,max(thenumber) as thenumber from zp_pos_tache where a0100 in ("+a0100s.substring(1)+") group by a0100 ";
				HashMap thenumberMap=new HashMap();
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					thenumberMap.put(rs.getString("a0100"),rs.getInt("thenumber")+"");
				}
				ArrayList updateList = new ArrayList();
				Calendar calendar = Calendar.getInstance();
				for(int i=1;i<=z0301_array.length;i++)
				{
					for(int j=0;j<list.size();j++)
					{
						LazyDynaBean abean=(LazyDynaBean)list.get(j);
						String a0100=(String)abean.get("a0100");
						
						RecordVo avo=new RecordVo("zp_pos_tache");
						String zp_pos_id = PubFunc.decrypt(z0301_array[i-1]);
						avo.setString("zp_pos_id",zp_pos_id);
						avo.setString("a0100",a0100);
						if(apos.get(a0100)!=null){// 如果该人已经应聘该岗位，就不在推荐该岗位，也不删除该岗位。
							HashMap posMap=(HashMap)apos.get(a0100);
							if(posMap.get(z0301_array[i-1])!=null){
								throw GeneralExceptionHandler.Handle(new Exception("不能推荐相同的岗位！"));
							}
						}
						if(thenumberMap.get(a0100)!=null)
						{
							avo.setInt("thenumber", Integer.parseInt(((String)thenumberMap.get(a0100)))+1);
							thenumberMap.put(a0100, String.valueOf(Integer.parseInt(((String)thenumberMap.get(a0100)))+1));//dml 2011-6-21 16:02:37
						}
						else
						{
							/**如果简历已经没有应聘职位了，但是人员库中的简历状态不是10，这样的数据不对了*/
							StringBuffer updateSql = new StringBuffer();
							updateSql.append("update "+dbname+"A01 set ");
							updateSql.append(resume_state+"='10' where ");
							updateSql.append(" a0100 ='"+a0100+"'");	
							updateList.add(updateSql.toString());
				     		avo.setInt("thenumber",1);
				     		thenumberMap.put(a0100, "1");
						}
						calendar.add(Calendar.MILLISECOND, i);    //时间毫秒递增 目的：导出excel时根据一个人的最后申请时间获得ID，如果有多条一样时间的记录，多表关联查询时会产生重复数据
						//System.out.println(calendar.getTime());
						avo.setDate("apply_date",calendar.getTime());
						avo.setString("status","0");
						avo.setString("resume_flag","10");
						alist.add(avo);	
					}
				}
				dao.addValueObject(alist);
//				if(updateList.size()>0)
//					dao.batchUpdate(updateList);
			}
			
			
		} catch (GeneralException e) {
            throw e;
        }
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.talent.recommend.error")));
		}
	}
	
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return "";
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	  public String getEmailField(){
	    	String str="";
	    	try{
	    	   RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
	           if(stmp_vo==null)
	         	  return "";
	           String param=stmp_vo.getString("str_value");
	           if(param==null|| "#".equals(param))
	         	   return "";
	           str=param;
	    	}catch(Exception ex)
	         {
	         	ex.printStackTrace();
	         }  
	    	
	    	return str;
	    }
	public HashMap getHashMapFromSet(String z0409,String emailField,String username,String[] pre,ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			boolean flag=false;
			for(int i=0;i<pre.length;i++)
			{
				String sql = "select a0101,"+emailField+" from "+pre[i]+"a01 where "+username+"='"+z0409+"'";
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					map.put("a0101",this.frowset.getString("a0101"));
					map.put("email",this.frowset.getString(emailField)==null?"":this.frowset.getString(emailField));
					flag=true;
					break;
				}
			}
			if(!flag)
			{
				String sql = "select username,fullname,a0100,nbase,email from operuser where username='"+z0409+"'";
				this.frowset=dao.search(sql);
				String name="";
				String email="";
				while(this.frowset.next())
				{
					String a0100=this.frowset.getString("a0100");
					if(a0100==null|| "".equals(a0100))
					{
						String fullusername=this.frowset.getString("fullname");
						if(fullusername==null|| "".equals(fullusername.trim()))
							fullusername=this.frowset.getString("username");
						name=fullusername;
						email=this.frowset.getString("email")==null?"":this.frowset.getString("email");
					}
					else
					{
						String nbase=this.frowset.getString("nbase");
						String str="select a0101 ,"+emailField+" from "+nbase+"a01 where a0100='"+a0100+"'";
						RowSet rr=dao.search(str);
						while(rr.next())
						{
							name=rr.getString("a0101");
							email=rr.getString(emailField)==null?"":rr.getString(emailField);
						}
					}
				}
				map.put("a0101",name);
				map.put("email",email);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String[] getPre(ContentDAO dao)
	{
		String[] pre = new String[1];
		try
		{
			String sql ="select pre from dbname ";
			this.frowset=dao.search(sql);
			String aa="";
			int i=0;
			while(this.frowset.next())
			{
				if(i!=0)
					aa+=",";
				aa+=this.frowset.getString(1);
				i++;
			}
			pre=aa.split(",");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre;
	}
	public HashMap getHasSelect(String a0100,String zp_pos_id){
		HashMap map=new HashMap ();
		String sql="";
		sql="select a0100,zp_pos_id from zp_pos_tache where a0100 in ("+a0100+") and zp_pos_id in("+zp_pos_id+") order by a0100";
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql);
			HashMap zpos=new HashMap();
			while(this.frowset.next()){
				String a1001=this.frowset.getString(1);
				String zp=this.frowset.getString(2);
				if(map.get(a1001)!=null){
					zpos=(HashMap)map.get(a1001);
					zpos.put(zp, "1");
				}else{
					zpos=new HashMap();
					zpos.put(zp, "1");
				}
				map.put(a1001, zpos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return map;
	}
}
