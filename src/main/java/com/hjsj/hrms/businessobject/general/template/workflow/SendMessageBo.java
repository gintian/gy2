package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
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
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendMessageBo {
	private Connection con=null;
	private UserView userview=null;
	
	private String task_id="";
	private String ins_id="";
	private String sp_flag="";
	private String businessModel=""; //61:报备  71加签
	private String template_emailAddress="";
	private String template__set="";
	
	private boolean isSmsContext=false;
	private String sendresource="";
	private boolean bSelfApply=false;
	
	public boolean isbSelfApply() {
		return bSelfApply;
	}

	public void setbSelfApply(boolean bSelfApply) {
		this.bSelfApply = bSelfApply;
	}

	private HashMap destination_a0100=null;//存储最终a0100
	/** 是否是直接提交模板 发送邮件通知  */
	//private String isSub="0";   
	
	
	public SendMessageBo(Connection conn,UserView userview)
	{
		con=conn;
		this.userview=userview;
	}
	
	public SendMessageBo(Connection conn)
	{
		con=conn;
	}
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	        Document doc =PubFunc.generateDom(param);;
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
	
	
	
	/**
	 * 中建接口，往oa里发送邮件
	 */
	public void sendMessageToOa(String tabid) 
	{
		try
		{
			
			if(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")))
			{
				if(SystemConfig.getPropertyValue("sso_templetOwner")!=null)
				{
					ContentDAO dao=new ContentDAO(this.con);	
					RowSet rowSet=null;
					String str=	SystemConfig.getPropertyValue("sso_templetOwner");
					String[] temps=str.split("&");
					HashSet set=new HashSet();
					for(int i=0;i<temps.length;i++)
					{
						String[] temps2=temps[i].split(":");
						if(temps2[0].equals(tabid))
						{
							String temp=temps2[1].substring(1).substring(0, temps2[1].length()-1);
							String[] temps3=temp.split("~");
							for(int j=0;j<temps3.length;j++)
							{
								String[] temps4=temps3[j].split(";");
								if("#".equals(temps4[1])) {
                                    continue;
                                }
								rowSet=dao.search("select * from operuser where username='"+temps4[1]+"'");
								if(rowSet.next())
								{
									set.add("4:"+temps4[1]);
								}
								else
								{
									set.add(",1:"+temps4[1]);
								}
								
							}	
						}
					}
					if(set.size()>0)
					{
						sendMessageToOA(set,"    请办理相关手续!","人事异动业务通知",tabid);
					}

				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	/**
	 * 中建接口，往oa里发送邮件
	 * @param sets
	 * @param isSendMessage
	 * @param context
	 * @param title
	 */
	public void sendMessageToOA(HashSet sets,String context,String title,String tabid)
	{
		try
		{
			AttestationUtils utils=new AttestationUtils();
			context=context.replace("\r\n","<br>").replace("\n","<br>").replace("\r","<br>");//bug 38021 邮件内容没有换行
			context=context.replace(" ","&nbsp;");
			ContentDAO dao=new ContentDAO(this.con);	
			SendEmailFormOA oa=new SendEmailFormOA(this.con);
			LazyDynaBean a_bean=null;
			ArrayList manList=new ArrayList();
			RowSet rowSet=null;
			if(SystemConfig.getPropertyValue("sso_templetOwner")!=null)
			{
				String str=	SystemConfig.getPropertyValue("sso_templetOwner");

				String[] temps=str.split("&");
				String[] temps3=null;
				for(int i=0;i<temps.length;i++)
				{
					String[] temps2=temps[i].split(":");
					if(temps2[0].equals(tabid))
					{
						String temp=temps2[1].substring(1);
						temp=temp.substring(0, temp.length()-1);
						temps3=temp.split("~");
					}
				}
				
				for(Iterator t=sets.iterator();t.hasNext();)
				{
							String atemp=(String)t.next();
							if(atemp==null||atemp.trim().length()==0) {
                                continue;
                            }
							String[] atemps=atemp.split(":");	
							String isUrl="0";
							String toTabid="";
							
							if(temps3!=null)
							{
								String _isUrl="0";
								String _toTabid="";
								boolean isflag=false;
								boolean isY=false;
								
								for(int j=0;j<temps3.length;j++)
								{
									String[] temps4=temps3[j].split(";");
									if("#".equalsIgnoreCase(temps4[1]))
									{
										isflag=true;
										_isUrl=temps4[2];
										_toTabid=temps4[0];
									}
									if(temps4[1].equals(atemps[1]))
									{
										isUrl=temps4[2];
										toTabid=temps4[0];
										isY=true;
									}
								}
								
								if(!isY&&isflag)
								{
									isUrl=_isUrl;
									toTabid=_toTabid;
								}
							}
							
							String username=""; //登陆用户名
							String password=""; //登陆密码
							if("4".equals(atemps[0]))
							{
								rowSet=dao.search("select * from operuser where username='"+atemps[1]+"'");
								if(rowSet.next())
								{
									username=rowSet.getString("username");
									password=rowSet.getString("password");
									
									if(rowSet.getString("a0100")!=null&&rowSet.getString("a0100").length()>0)
									{
										atemps[1]=rowSet.getString("nbase")+rowSet.getString("a0100");
									}
									else {
                                        continue;
                                    }
								}
							
							}
							else
							{
								String nbase=atemps[1].substring(0,3);
								String a0100=atemps[1].substring(3);
								LazyDynaBean fieldbean=utils.getUserNamePassField();
								String username_field=(String)fieldbean.get("name");
							    String password_field=(String)fieldbean.get("pass");
							    StringBuffer sql=new StringBuffer("");
							    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
							    sql.append(" where a0100='"+a0100+"'");
							    rowSet=dao.search(sql.toString());
							    if(rowSet.next())
								{
									username=rowSet.getString("username");
									password=rowSet.getString("password");
								}
							}
							a_bean=new LazyDynaBean();
							a_bean.set("nbase",atemps[1].substring(0,3));
							a_bean.set("a0100",atemps[1].substring(3));
							if("1".equals(isUrl))
							{
								if(password==null) {
                                    password="";
                                }
								String etoken=PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								String url="/general/template/edit_form.do?b_query=link&returnflag=3&sp_flag=1&ins_id=0&tabid="+toTabid+"&appfwd=1&etoken="+etoken+"&validatepwd=false";
								//url="http://cscec-oa.cscec.com.cn/oahr.asp?tabid="+toTabid+"&etoken="+etoken+"&ins_id=0&taskid=0&type=1";
								a_bean.set("url",url);
							}
							else {
                                a_bean.set("url","");
                            }
							manList.add(a_bean);
				}
						
				LazyDynaBean sendBean=null;
				if(this.userview.getA0100()!=null&&this.userview.getA0100().length()>0)
				{
							sendBean=new LazyDynaBean();
							sendBean.set("nbase",this.userview.getDbname());
							sendBean.set("a0100",this.userview.getA0100());
				}
				oa.sendEmail(manList,sendBean,title,context);
					
				//	}
						
				//}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得邮件模板的信息
	 * @param id
	 * @return
	 */
	public LazyDynaBean getTemplateMailInfo(String id)
	{
		LazyDynaBean  abean=new LazyDynaBean();
		ContentDAO dao=new ContentDAO(this.con);	
		try
		{
			RowSet rowSet=dao.search("select * from email_name where id="+id);
			if(rowSet.next())
			{
				String subject=rowSet.getString("subject");
				String content=Sql_switcher.readMemo(rowSet,"content");
				String address=rowSet.getString("address")!=null?rowSet.getString("address"):"";
				if(address.trim().length()>0)
				{
					String[] temps=address.split(":");
					abean.set("address",temps[0]);
					FieldItem item=DataDictionary.getFieldItem(((String)abean.get("address")).toLowerCase());
					if(item!=null) {
                        abean.set("set", item.getFieldsetid());
                    }
				}
				else
				{	abean.set("address","");
					abean.set("set","");
				}
				abean.set("subject", subject);
				abean.set("content", content);
				
				//zxj 20141023 取附件
				EmailTemplateBo emailTemplateBo = new EmailTemplateBo(this.con);
				ArrayList attachList = emailTemplateBo.getAttachFileName(id);
				abean.set("attach", attachList);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	/**
	 * 取得发送对象列表信息
	 * @param sets
	 * @param dao
	 * @param khRelationRoleList
	 * @param user_bo
	 * @param template_bos业务人员办理模板
	 * @return
	 */
	public ArrayList getSendObjectList(HashSet sets,ContentDAO dao,ArrayList khRelationRoleList,UserObjectBo user_bo,String template_bos_emailAddress,String template_bos_set)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			for(Iterator t=sets.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				if(temp.trim().length()==0) {
                    continue;
                }
				String[] temps=temp.split(":");	
				
				if("1".equals(temps[0])&&temps[1].length()>3)
				{
					LazyDynaBean a_bean=new LazyDynaBean();
					a_bean.set("a0100",temps[1]);
					a_bean.set("email","");
					a_bean.set("phone","");
					a_bean.set("status","1");
					list.add(a_bean);
				}	
				/**operuser中的用户*/
				else if("4".equals(temps[0]))
				{
					LazyDynaBean a_bean=new LazyDynaBean();
					RecordVo vo=new RecordVo("operuser");
					vo.setString("username",temps[1]);
					vo=dao.findByPrimaryKey(vo);
					String dbase=vo.getString("nbase");
					String a0100=vo.getString("a0100");
					a_bean.set("a0100",temps[1]);
					a_bean.set("email","");
					a_bean.set("phone","");
					if(a0100==null|| "".equalsIgnoreCase(a0100))
					{
						a_bean.set("email",vo.getString("email"));
						a_bean.set("phone",vo.getString("phone"));
						a_bean.set("status","0");
					}
					else
					{
						a_bean.set("status","1");
						a_bean.set("a0100",dbase+a0100);
					}
					list.add(a_bean);
				}
				else if("2".equals(temps[0]))//角色
				{
					rowSet=dao.search("select * from t_sys_role where role_id='"+temps[1]+"'");
					int role_property=0;
					if(rowSet.next()) {
                        role_property=rowSet.getInt("role_property");
                    }
					if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14)
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("role_id", temps[1]);
						abean.set("role_property",String.valueOf(role_property));
						khRelationRoleList.add(abean);
						continue;
					}
					ArrayList alist=findUserListByRoleId(temps[1],template_bos_emailAddress,template_bos_set);
					LazyDynaBean abean=null;
					for(int i=0;i<alist.size();i++)
					{
						LazyDynaBean a_bean=new LazyDynaBean();
						abean=(LazyDynaBean)alist.get(i);
						a_bean.set("a0100",(String)abean.get("a0100"));
						a_bean.set("email",(String)abean.get("email"));
						a_bean.set("phone",(String)abean.get("phone"));
						a_bean.set("status",abean.get("status"));
						list.add(a_bean);
					}
				}
				else if("3".equals(temps[0]))
				{
					String codesetid=temps[1].substring(0,2);
					String codeitemid=temps[1].substring(2);
					ArrayList alist=findUserListByOrgId(codeitemid,codesetid,template_bos_emailAddress,template_bos_set);
					LazyDynaBean abean=null;
					for(int i=0;i<alist.size();i++)
					{
						abean=(LazyDynaBean)alist.get(i);
						LazyDynaBean a_bean=new LazyDynaBean();
						a_bean.set("a0100",(String)abean.get("a0100"));
						a_bean.set("email",(String)abean.get("email"));
						a_bean.set("phone",(String)abean.get("phone"));
						a_bean.set("status","1");
						list.add(a_bean);
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
		}
		return list;
	}
	
	/**
	 * 根据组织机构查找对应信息,用户信息通过LazyDynaBean保存
	 * 属性包括username,userfullname,b0110,e0122,e01a1,email,phone，
	 * @param org_id
	 * @return
	 */
	public ArrayList findUserListByOrgId(String org_id,String codeid,String template_bos_emailAddress,String template_bos_set)
	{
	  ArrayList list=new ArrayList();		
	  try
	  {
		ContentDAO dao=new ContentDAO(this.con);
		HashMap userhm=new HashMap();		  
		RecordVo org_vo=new RecordVo("organization");
		org_vo.setString("codesetid",codeid);
		org_vo.setString("codeitemid",org_id);
		org_vo=dao.findByPrimaryKey(org_vo);
		//findUserListByOrgId(org_vo,userhm,template_bos_emailAddress,template_bos_set);
		findUserListByOrgId(codeid,org_id,"",userhm,template_bos_emailAddress,template_bos_set);
		Iterator  iterator=userhm.values().iterator();
		while(iterator.hasNext())
		{
			list.add(iterator.next());
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return list;
	}
	
	
	/**比对权限大小，返回大的权限 此方法主要用于比对角色人员范围与用户的人员范围
	 * @param managePriv1 权限1 
	 * @param managePriv2  权限2
	 * @return
	 */
	public String getBigMangePriv(String managePriv1,String managePriv2)
	{
	  String strManagePriv=managePriv1;		
	  try
	  {
		    if (managePriv1==null) {
                managePriv1="";
            }
		    if (managePriv2==null) {
                managePriv2="";
            }
		    if (managePriv1.length()<2){
		    	return managePriv2;
		    }
		    else if (managePriv2.length()<2){
		    	return managePriv1;
		    }
		    
		  	int index = 2;
	        String code1 = managePriv1.substring(0, index);
	        String value1 = managePriv1.substring(index);
	        
	        String code2 = managePriv2.substring(0, index);
	        String value2 = managePriv2.substring(index);
	        
	        //管理机构类型相同 都是单位或都是部门或都是岗位
	        if("UN".equals(code1) && "UN".equals(code2) || "UM".equals(code1) && "UM".equals(code2) || "@K".equals(code1) && "@K".equals(code2))
	        {
	            if(value1.length() < value2.length())
	            {
	            	strManagePriv=managePriv2;
	            }
	        } else  if(("UM".equals(code1) || "@K".equals(code1)) && "UN".equals(code2))
	        {
	        	strManagePriv=managePriv2;
	        } else  if("@K".equals(code1) && ("UN".equals(code2) || "UM".equals(code2)))
	        {
	        	strManagePriv=managePriv2;
	        }
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return strManagePriv;
	}
	
    
	/**返回某一模块的业务范围 
	 * @param busiOrg 业务范围内容
	 * @param module 模块号
	 * @return
	 */
	public String getBusiOrg(String busiOrg,String module)
	{
	  String strManagePriv="";		
	  try
	  {
		    if (busiOrg==null) {
                busiOrg="";
            }
		    if (busiOrg.length()<1){
		    	return strManagePriv;
		    }
		    
		    String tmparr[] = StringUtils.split(busiOrg, "|");
            int i = 0;
            while(i < tmparr.length) 
            {
                String tmp = tmparr[i];
                String busiarr[] = StringUtils.split(tmp, ",");
                if(busiarr.length == 2){
                	if (module.equals(busiarr[0])){
                		strManagePriv=busiarr[1];
                	}
                }
                i++;
            }
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return strManagePriv;
	}
	

	/**返回用户的人事异动模块的权限范围，有业务范围返回业务范围，其次操作单位，再次是管理范围
	 * @param busiOrg 业务范围
	 * @param org 操作单位
	 * @param managePriv 管理范围
	 * @return
	 */
	public String getTemplateMangerPirv(String busiOrg,String org,String managePriv)
	{
	  String strManagePriv=managePriv;		
	  try
	  {
		    if (busiOrg==null) {
                busiOrg="";
            }
		    if (org==null) {
                org="";
            }
		    if (managePriv==null) {
                managePriv="";
            }
		    busiOrg= getBusiOrg(busiOrg,"8");//取得人事异动权限
		    if (busiOrg.length()>0){
		    	return busiOrg;
		    }
		    else if (org.length()>0){
		    	return org;
		    }
		    else if (org.length()>0){
		    	return managePriv;
		    }
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return strManagePriv;
	}
	
	
	/**
	 * 根据角色号取得对应用户列表，用户信息通过LazyDynaBean保存
	 * ，属性包括username,userfullname,b0110,e0122,e01a1,email,phone,a0100，
	 * @param role_id
	 * @return
	 */
	public ArrayList findUserListByRoleId(String role_id,String address,String setid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		String key=null;
		LazyDynaBean vo=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			HashMap map = new HashMap();
			rset = dao.search(" select pre from dbname ");
			while(rset.next()){
				map.put(rset.getString("pre").toUpperCase(), "1");
			}
			//因为效率问题，单位部门、岗位、自助业务分开处理了。wangrd 2015-07-16		
			//取得角色人员范围
			String roleMangePriv="";
			rset = dao.search("select managepriv from t_sys_function_priv where id ='"+role_id+"'");
			if (rset.next()){
				roleMangePriv=Sql_switcher.readMemo(rset, "managepriv");
			}
			//处理业务用户 
			DbNameBo dbbo=new DbNameBo(this.con);
			ArrayList loginDblist=dbbo.getAllLoginDbNameList();//认证库
		    //处理不关联自助的用户
			strsql.setLength(0);
			strsql.append(" select T.*,F.managepriv  from ")			
				.append(" (select R.staff_id,O.username,O.fullname,O.password,O.busi_org_dept,o.org_dept,o.email,o.phone")
				.append(" from t_sys_staff_in_role R ,OperUser O  ")			
				.append(" where R.staff_id= O.UserName ")		
				.append(" and R.status =0 ")
				.append(" and R.role_id ='"+role_id+"'")
				.append(" and nullif(O.A0100,'') is null ")
				.append(" ) T ");
			strsql.append(" left join t_sys_function_priv F ");
			strsql.append(" on  upper(F.id)=upper(T.staff_id)  ");
			
			try{
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					String userName=rset.getString("username");
					int status=0;
					key=userName+"_"+status;				
					vo=new LazyDynaBean();
					vo.set("username",userName);
					vo.set("a0100","");
					vo.set("userfullname",rset.getString("fullname")==null?"":rset.getString("fullname"));
					vo.set("email",rset.getString("email")==null?"":rset.getString("email"));
					vo.set("phone",rset.getString("phone")==null?"":rset.getString("phone"));
					vo.set("b0110","");
					vo.set("e0122","");
					vo.set("e01a1","");
					vo.set("status",String.valueOf(status));
					vo.set("password",rset.getString("password")==null?"":rset.getString("password"));
					vo.set("type", "1");
					//权限字段
					String busi_org_dept= rset.getString("busi_org_dept")==null?"":rset.getString("busi_org_dept");
					String org_dept= rset.getString("org_dept")==null?"":rset.getString("org_dept");
					String managepriv=rset.getString("managepriv")==null?"":rset.getString("managepriv");
					managepriv=getBigMangePriv(managepriv,roleMangePriv);				
					vo.set("busi_org_dept",busi_org_dept);
					vo.set("org_dept", org_dept);
					vo.set("managepriv", managepriv);
					String templateMangerPirv= getTemplateMangerPirv(busi_org_dept,org_dept,managepriv);
					vo.set("templateMangerPirv", templateMangerPirv);
					
					userhm.put(key,vo);					
				}	
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//处理关联自助的用户
			String email=ConstantParamter.getEmailField().toLowerCase();			
			String phone=ConstantParamter.getMobilePhoneField().toLowerCase();
			String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
			String password = ConstantParamter.getLoginPasswordField().toLowerCase();
            //解决多个A01子集字段不一直的问题 712所数据视图同步不同库导致
            String fieldNames = "A.a0100,A.b0110,A.e0122,A.e01a1,A.a0101";
            if(setid!=null&& "A01".equalsIgnoreCase(setid)
                    &&address!=null&&address.trim().length()>0){
                email=address.toLowerCase();
            }
            if(!"".equals(email)){
                fieldNames=fieldNames+","+"A."+email;
            }
            
            if(phone!=null&&phone.trim().length()>0 && (fieldNames.indexOf(phone)==-1)){
                fieldNames=fieldNames+","+"A."+phone;
            }
            if(loguser!=null&&loguser.trim().length()>0 && (fieldNames.indexOf(loguser)==-1)){
                fieldNames=fieldNames+","+"A."+loguser;
            }
            if(password!=null&&password.trim().length()>0 && (fieldNames.indexOf(password)==-1)){
                fieldNames=fieldNames+","+"A."+password;
            }
            
			strsql.setLength(0);
			for (int i=0;i<loginDblist.size();i++){
				RecordVo logindb=(RecordVo)loginDblist.get(i);
				String dbPre=logindb.getString("pre");
				if (strsql.length()>0){
					strsql.append(" union all ");		
				}
				strsql.append(" select T.*,"+fieldNames+" from  ");			
				strsql.append(" (select T1.*, F.managepriv from ")  
					  .append(" (select R.staff_id,O.UserName,O.busi_org_dept,o.org_dept,o.a0100,o.nbase ")
					  .append("from t_sys_staff_in_role R ,OperUser O ")
					  .append(" where R.staff_id= O.UserName ")	
					  .append(" and R.status =0 ")
					  .append(" and R.role_id ='"+role_id+"'")
					  .append(" and nullif(O.A0100,'') is not null ")
					  .append(" and upper(O.nbase) ='"+dbPre.toUpperCase()+"' ")
				.append(") T1 ");
				strsql.append(" left join t_sys_function_priv F ");
				strsql.append(" on  upper(F.id)=upper(T1.staff_id)  ");
				strsql.append(") T ");		
				strsql.append("  left join "+dbPre+"A01 A");			
				strsql.append(" on A.A0100 = T.A0100");
			}
			try{
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					String userName=rset.getString("username");
					String a0100=rset.getString("a0100");
					String nbase=rset.getString("nbase");
					if (a0100==null ||a0100.length()<1) {
                        continue;
                    }
					int status=1;
					key=userName+"_"+status;				
					vo=new LazyDynaBean();
					vo.set("username",userName);
					vo.set("a0100",nbase+a0100);
					vo.set("userfullname",rset.getString("a0101")==null?"":rset.getString("a0101"));
					if(!"".equals(email)) {
                        vo.set("email",rset.getString(email)==null ?"":rset.getString(email));
                    }
					if(setid!=null&& "A01".equalsIgnoreCase(setid)&&address!=null&&address.trim().length()>0) {
                        vo.set("email",rset.getString(address.toLowerCase())==null?"":rset.getString(address.toLowerCase()));
                    }
					if(!"".equals(phone)) {
                        vo.set("phone",rset.getString(phone)==null?"":rset.getString(phone));
                    }
                    vo.set("b0110",rset.getString("b0110")==null?"":rset.getString("b0110"));
                    vo.set("e0122",rset.getString("e0122")==null?"":rset.getString("e0122"));
                    vo.set("e01a1",rset.getString("e01a1")==null?"":rset.getString("e01a1"));
					vo.set("status",String.valueOf(status));
					vo.set("password", rset.getString(password)==null?"":rset.getString(password));
					vo.set("type", "2");
					//权限字段
					String busi_org_dept= rset.getString("busi_org_dept")==null?"":rset.getString("busi_org_dept");
					String org_dept= rset.getString("org_dept")==null?"":rset.getString("org_dept");
					String managepriv=rset.getString("managepriv")==null?"":rset.getString("managepriv");
					managepriv=getBigMangePriv(managepriv,roleMangePriv);				
					vo.set("busi_org_dept",busi_org_dept);
					vo.set("org_dept", org_dept);
					vo.set("managepriv", managepriv);
					String templateMangerPirv= getTemplateMangerPirv(busi_org_dept,org_dept,managepriv);
					vo.set("templateMangerPirv", templateMangerPirv);
					
					userhm.put(key,vo);
					if(setid!=null && setid.trim().length()>0&&!"A01".equalsIgnoreCase(setid))
					{
						RowSet rowSet=dao.search("select "+address+" from "+nbase+"setid where a0100='"+a0100+"'  order by i9999 desc" );
						if(rowSet.next()) {
                            vo.set("email",rowSet.getString(address)==null?"":rowSet.getString(address));
                        }
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			//处理自助用户
			strsql.setLength(0);
			for (int i=0;i<loginDblist.size();i++){
				RecordVo logindb=(RecordVo)loginDblist.get(i);
				String dbPre=logindb.getString("pre");
				if (strsql.length()>0){
					strsql.append(" union all ");		
				}
				strsql.append("select T.*,"+fieldNames+" from ");			
				strsql.append(" (select R.staff_id,F.busi_org_dept,'' as org_dept,F.managepriv ,'"+dbPre+"' as nbase")  
					  .append(" from  t_sys_staff_in_role R  left join t_sys_function_priv F")
					  .append(" on R.staff_id=  F.id ")	
					  .append(" where R.status =1 ")
					  .append(" and R.role_id ='"+role_id+"'")
					  .append(" and upper("+ Sql_switcher.substr("R.staff_id", "1", "3") +") ='"+dbPre.toUpperCase()+"' ")
					  .append(") T ");		
				strsql.append("  left join "+dbPre+"A01 A");			
				strsql.append(" on A.A0100 = "+Sql_switcher.substr("staff_id", "4", Sql_switcher.length("staff_id")) );
			}
			try{
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					String a0100=rset.getString("a0100");
					String nbase=rset.getString("nbase");
					if (a0100==null ||a0100.length()<1) {
                        continue;
                    }
					String logon_user=rset.getString(loguser);
                    if (rset.getString(loguser)==null || "".equals((String)rset.getString(loguser))){
                        continue;
                    }
					int status=1;
					key=nbase+a0100+"_"+status;				
					vo=new LazyDynaBean();
					vo.set("username",logon_user);
					vo.set("a0100",nbase+a0100);
					vo.set("userfullname",rset.getString("a0101"));
					if(!"".equals(email)) {
                        vo.set("email",rset.getString(email)==null ?"":rset.getString(email));
                    }
					if(setid!=null&& "A01".equalsIgnoreCase(setid)&&address!=null&&address.trim().length()>0) {
                        vo.set("email",rset.getString(address.toLowerCase())==null?"":rset.getString(address.toLowerCase()));
                    }
					if(!"".equals(phone)) {
                        vo.set("phone",rset.getString(phone)==null?"":rset.getString(phone));
                    }
                    vo.set("b0110",rset.getString("b0110")==null?"":rset.getString("b0110"));
                    vo.set("e0122",rset.getString("e0122")==null?"":rset.getString("e0122"));
                    vo.set("e01a1",rset.getString("e01a1")==null?"":rset.getString("e01a1"));
					vo.set("status",String.valueOf(status));
					vo.set("password", rset.getString(password)==null?"":rset.getString(password));
					vo.set("type", "2");
					//权限字段
					String busi_org_dept= rset.getString("busi_org_dept")==null?"":rset.getString("busi_org_dept");
					String org_dept= "";
					String managepriv=rset.getString("managepriv")==null?"":rset.getString("managepriv");
					managepriv=getBigMangePriv(managepriv,roleMangePriv);				
					vo.set("busi_org_dept",busi_org_dept);
					vo.set("org_dept", org_dept);
					vo.set("managepriv", managepriv);
					String templateMangerPirv= getTemplateMangerPirv(busi_org_dept,org_dept,managepriv);
					vo.set("templateMangerPirv", templateMangerPirv);
					
					userhm.put(key,vo);	
					if(setid!=null && setid.trim().length()>0&&!"A01".equalsIgnoreCase(setid))
					{
						RowSet rowSet=dao.search("select "+address+" from "+nbase+"setid where a0100='"+a0100+"'  order by i9999 desc" );
						if(rowSet.next()) {
                            vo.set("email",rowSet.getString(address)==null?"":rowSet.getString(address));
                        }
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			strsql.setLength(0);
			strsql.append("select O.codeitemid  ");
			strsql.append("from t_sys_staff_in_role R,organization O where R.staff_id =O.codeitemid " );
			strsql.append(" and R.role_id='");
			strsql.append(role_id);
			strsql.append("'");
			strsql.append(" and R.status =2");
			strsql.append(" and O.codesetid ='@K'");
			rset=dao.search(strsql.toString());
			if (rset.next())
			{
				try{
					findUserListByOrgId("@K","",strsql.toString(),userhm,address,setid,roleMangePriv);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}finally {
				
				}
				
			}//for while loop end.
			

			//处理具有此角色的单位部门
			strsql.setLength(0);
			strsql.append("select R.staff_id,R.role_id,R.status,O.codesetid,O.codeitemid ");
			strsql.append("from t_sys_staff_in_role R,organization O where R.staff_id =O.codeitemid " );
			strsql.append(" and R.role_id='");
			strsql.append(role_id);
			strsql.append("'");
			strsql.append(" and R.status =2");
			strsql.append(" and (O.codesetid ='UM' or O.codesetid ='UN')");
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				String codeitemid=rset.getString("staff_id");
				String 	codesetid=rset.getString("codesetid");						
				findUserListByOrgId(codesetid,codeitemid,"",userhm,address,setid,roleMangePriv);
					
			}//for while loop end.
			 
			Iterator iterator=userhm.entrySet().iterator();
			HashMap isExistMap=new HashMap();
			HashMap a0100Map = new HashMap();
			HashMap usernameMap = new HashMap();
			strsql.setLength(0);
			strsql.append("select TSR.role_id, TSR.role_property,TSS.staff_id,TSS.status from t_sys_role tsr,t_sys_staff_in_role tss where  TSR.role_id=TSS.role_id and TSR.role_property in(0,15,16)");
			rset=dao.search(strsql.toString());
			HashMap roleMap = new HashMap();//系统三元角色
			while(rset.next()) {
				String staff_id = rset.getString("staff_id").toLowerCase();
				int status = rset.getInt("status");
				roleMap.put(staff_id, status);
			}
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String strkey=((String)entry.getKey()).toLowerCase();
				String _key=strkey.substring(0,strkey.length()-2);
				if(isExistMap.get(_key)!=null)
				{
					continue;
				}
				LazyDynaBean value = (LazyDynaBean)entry.getValue();
				String a0100 = (String)value.get("a0100");
				String status = (String)value.get("status");//自助与业务区别标识 0业务,1自助
				String _username = (String)value.get("username");
				if(roleMap.size()>0) {
					if("1".equals(status)&&roleMap.containsKey(a0100.toLowerCase())) {
                        continue;
                    } else if("0".equals(status)&&roleMap.containsKey(_username.toLowerCase())) {
                        continue;
                    }
				}
				if((!"".equals(a0100)&&a0100Map.get(a0100)!=null&&(StringUtils.isNotBlank(_username)&&usernameMap.get(_username)!=null||StringUtils.isBlank(_username))&&"1".equals(status))){//存在此人员或者是三元角色//bug 多个业务用户关联同一个自助用户,只判断a0100会过滤掉业务用户导致任务监控不显示人。
					 
				}else{
					list.add(entry.getValue()/*iterator.next()*/);
					if("1".equals(status)){
						a0100Map.put(a0100, "1");
						usernameMap.put(_username, "1");
					}
					isExistMap.put(_key, "1"); 
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 * syl 52539 招商蛇口：在流程定义-设置拆单模式，按单据中人员数据的姓名来设置（详情见图“拆单设置”），设置完成后，在人事异动发送面试通知业务，选择多个人来进行发送面试通知，一个人可收到包括自己和其他人的邮件（详情见图“邮件1”、“邮件2”，此此处用的是测试数据）
	 * 拆单模式下，首次发起，会菜单成一一个个单据，然后依次发送信息，会有如上情况
	 * 
	 * @param 
	 * @return
	 */
	public ArrayList findUserListBySeqNum(String tabid,String task_id,String actor_type,String cd_whl)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			HashMap map = new HashMap();
			rset = dao.search(" select pre from dbname ");
			while(rset.next()){
			map.put(rset.getString("pre").toUpperCase(), "1");
			}
			LazyDynaBean vo=null;
		
			if("5".equals(actor_type))//本人
			{
				String sql0="select count(tt.A0100) from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
				sql0+=" and twt.task_id="+task_id+" and ("+Sql_switcher.isnull("twt.state","0")+"<>1 or "+Sql_switcher.isnull("twt.state","0")+"<>3) ";
				rset=dao.search(sql0);
				int count=0;
				if(rset.next()) {
                    count=rset.getInt(1);
                }
				if(count>0)
				{
					sql0="select twt.*,tt.A0100,tt.BasePre from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
					sql0+=" and twt.task_id="+task_id+" and ("+Sql_switcher.isnull("twt.state","0")+"<>1 or "+Sql_switcher.isnull("twt.state","0")+"<>3) ";
				}
				else
				{
					if(this.bSelfApply){
						sql0 = "select A0100,BasePre from  g_templet_" + tabid + " where a0100='"+this.userview.getA0100()+"' and lower(BasePre)='"+this.userview.getDbname().toLowerCase()+"'";
					}else{
						sql0 = "select A0100,BasePre from  "+ this.userview.getUserName() + "templet_" + tabid + " where submitflag=1";
						if(StringUtils.isNotEmpty(cd_whl)) {
                            sql0+=cd_whl;
                        }
					}
				}
					
				rset=dao.search(sql0);
				while(rset.next()){
					String dbpre = rset.getString("BasePre");
					String a0100 =  rset.getString("a0100");
					String key = dbpre+a0100;
				 
					if(map!=null&&map.get(dbpre.toUpperCase())!=null){
						
					}else{
						continue;
					}
					RecordVo user_vo=new RecordVo(dbpre+"A01");
					user_vo.setString("a0100",a0100);
					
					/*
					try{
					user_vo=dao.findByPrimaryKey(user_vo);
					}catch(Exception e){//库中的人不存在了
					//	e.printStackTrace();
					}*/
					if(dao.isExistRecordVo(user_vo))
					{
						String email=ConstantParamter.getEmailField();
						String phone=ConstantParamter.getMobilePhoneField();	
						String loguser=ConstantParamter.getLoginUserNameField();
								vo=new LazyDynaBean();
								if(loguser!=null&&loguser.trim().length()>0) {
                                    vo.set("username",user_vo.getString(loguser.toLowerCase()));
                                } else {
                                    vo.set("username","");
                                }
								vo.set("a0100",dbpre+user_vo.getString("a0100"));
								vo.set("userfullname",user_vo.getString("a0101"));
								
								if(email!=null&&email.trim().length()>0) {
                                    vo.set("email",user_vo.getString(email.toLowerCase()));
                                } else {
                                    vo.set("email","");
                                }
								if(phone!=null&&phone.trim().length()>0) {
                                    vo.set("phone",user_vo.getString(phone.toLowerCase()));
                                } else {
                                    vo.set("phone","");
                                }
								vo.set("b0110",user_vo.getString("b0110"));
								vo.set("e0122",user_vo.getString("e0122"));
								vo.set("e01a1",user_vo.getString("e01a1"));
								vo.set("status","5");
								//if(!userhm.containsKey(key))
								userhm.put(key,vo);
							
					}
				 }
			}
			Iterator iterator=userhm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				//String strkey=(String)entry.getKey();
				list.add(entry.getValue()/*iterator.next()*/);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 * 
	 * 
	 * @param 
	 * @return
	 */
	public ArrayList findUserListBySeqNum(String tabid,String task_id,String actor_type)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			HashMap map = new HashMap();
			rset = dao.search(" select pre from dbname ");
			while(rset.next()){
			map.put(rset.getString("pre").toUpperCase(), "1");
			}
			LazyDynaBean vo=null;
		
			if("5".equals(actor_type))//本人
			{
				String sql0="select count(tt.A0100) from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
				sql0+=" and twt.task_id="+task_id+" and ("+Sql_switcher.isnull("twt.state","0")+"<>1 or "+Sql_switcher.isnull("twt.state","0")+"<>3) ";
				rset=dao.search(sql0);
				int count=0;
				if(rset.next()) {
                    count=rset.getInt(1);
                }
				if(count>0)
				{
					sql0="select twt.*,tt.A0100,tt.BasePre from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
					sql0+=" and twt.task_id="+task_id+" and ("+Sql_switcher.isnull("twt.state","0")+"<>1 or "+Sql_switcher.isnull("twt.state","0")+"<>3) ";
				}
				else
				{
					if(this.bSelfApply){
						sql0 = "select A0100,BasePre from  g_templet_" + tabid + " where a0100='"+this.userview.getA0100()+"' and lower(BasePre)='"+this.userview.getDbname().toLowerCase()+"'";
					}else{
						sql0 = "select A0100,BasePre from  "+ this.userview.getUserName() + "templet_" + tabid + " where submitflag=1";
					}
				}
					
				rset=dao.search(sql0);
				while(rset.next()){
					String dbpre = rset.getString("BasePre");
					String a0100 =  rset.getString("a0100");
					String key = dbpre+a0100;
				 
					if(map!=null&&map.get(dbpre.toUpperCase())!=null){
						
					}else{
						continue;
					}
					RecordVo user_vo=new RecordVo(dbpre+"A01");
					user_vo.setString("a0100",a0100);
					
					/*
					try{
					user_vo=dao.findByPrimaryKey(user_vo);
					}catch(Exception e){//库中的人不存在了
					//	e.printStackTrace();
					}*/
					if(dao.isExistRecordVo(user_vo))
					{
						String email=ConstantParamter.getEmailField();
						String phone=ConstantParamter.getMobilePhoneField();	
						String loguser=ConstantParamter.getLoginUserNameField();
								vo=new LazyDynaBean();
								if(loguser!=null&&loguser.trim().length()>0) {
                                    vo.set("username",user_vo.getString(loguser.toLowerCase()));
                                } else {
                                    vo.set("username","");
                                }
								vo.set("a0100",dbpre+user_vo.getString("a0100"));
								vo.set("userfullname",user_vo.getString("a0101"));
								
								if(email!=null&&email.trim().length()>0) {
                                    vo.set("email",user_vo.getString(email.toLowerCase()));
                                } else {
                                    vo.set("email","");
                                }
								if(phone!=null&&phone.trim().length()>0) {
                                    vo.set("phone",user_vo.getString(phone.toLowerCase()));
                                } else {
                                    vo.set("phone","");
                                }
								vo.set("b0110",user_vo.getString("b0110"));
								vo.set("e0122",user_vo.getString("e0122"));
								vo.set("e01a1",user_vo.getString("e01a1"));
								vo.set("status","5");
								//if(!userhm.containsKey(key))
								userhm.put(key,vo);
							
					}
				 }
			}
			Iterator iterator=userhm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				//String strkey=(String)entry.getKey();
				list.add(entry.getValue()/*iterator.next()*/);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	/**
	 * 
	 * 获得接收范围对应的操作用户
	 * @param 
	 * @return
	 */
	public String findUserNameBySeqNum(String tabid,String task_id,String actor_type,String node_id)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		String username=",";
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			LazyDynaBean vo=null;
		
			if("2".equals(actor_type))//角色
			{
				Document doc=null;
				Element element=null;
				String scope_field="";
				rset=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
				String ext_param="";
				if(rset.next()) {
                    ext_param=Sql_switcher.readMemo(rset,"ext_param");
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
							}
						}
					}
				}
				
				
				String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
				sql0+=" and twt.task_id="+task_id+" and ("+Sql_switcher.isnull("twt.state","0")+"<>1 or "+Sql_switcher.isnull("twt.state","0")+"<>3) ";
				rset=dao.search(sql0);
				
				while(rset.next()){
					if(rset.getString("username")!=null&&rset.getString("username").trim().length()>0) {
                        username += rset.getString("username")+",";
                    }
				}
				
			}
		
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return username;
	}
	


	/**
	 * 查找待发人员信息 此方法可以弃用了
	 * @param org_vo
	 * @param userhm
	 */
	private void findUserListByOrgId(RecordVo org_vo,HashMap userhm,String address,String setid)throws GeneralException
	{
		if(org_vo==null) {
            return;
        }
		String codesetid=org_vo.getString("codesetid");
		String codeitemid=org_vo.getString("codeitemid");
		findUserListByOrgId(codesetid,codeitemid,"",userhm,address,setid);
	}
	private void findUserListByOrgId(String codeSetId,String codeItemId,String inSql,
			  HashMap userhm,String address,String setid)throws GeneralException
	{
		findUserListByOrgId(codeSetId,codeItemId,inSql, userhm,address,setid,"");
		
	}
	/**
	 * 按机构代码查询下属人员，codeitemid 与 insql 只能一个参数有值，如两个有值 优先使用codeitemid
	 * codesetid:机构类型
	 * codeitemid：机构编码
	 * param: insql:机构编码，一般为子查询如(select e01a1 from usra01 where 1=1 )，也可为逗号分隔的串，符合sql语法即可
	 * userhm：存储成员列表
	 * address 邮箱指标
	 * setid 邮箱所在子集
	 */	
	private void findUserListByOrgId(String codeSetId,String codeItemId,String inSql,
			  HashMap userhm,String address,String setid,String roleMangePriv)throws GeneralException
	{
	
	
		StringBuffer strsql=new StringBuffer();
		DbNameBo dbbo=new DbNameBo(this.con);
		String email=ConstantParamter.getEmailField();
		String phone=ConstantParamter.getMobilePhoneField();	
		String loguser=ConstantParamter.getLoginUserNameField();
		String password = ConstantParamter.getLoginPasswordField().toLowerCase();
		if(address!=null&&address.trim().length()>0){
			address=address.trim();
		}
		try
		{
			ArrayList list=dbbo.getAllLoginDbNameList();
			StringBuffer colums=new StringBuffer();
            colums.append("a.a0100,a.a0101,a.b0110,a.e0122,a.e01a1");
            if (!"a0101".equalsIgnoreCase(loguser)){
                colums.append(",a."+loguser);
            }
			colums.append(",");
			colums.append("a."+password+",");
			if(!"".equals(email))
			{
				if(address!=null&&address.trim().length()>0)
				{
					if(setid!=null&&!"A01".equalsIgnoreCase(setid)) {
                        colums.append("d."+address);
                    } else {
                        colums.append("a."+address);
                    }
					email=address;
				}
				else {
                    colums.append("a."+email);
                }
				colums.append(",");
			}
			if(!"".equals(phone))
			{
				colums.append("a."+phone);
				colums.append(",");
			}
			//colums.setLength(colums.length()-1);
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				strsql.append("select ");
				strsql.append(colums.toString());
				strsql.append("'");
				strsql.append(vo.getString("pre").toUpperCase());
				strsql.append("' dbpre from ");
				strsql.append(vo.getString("pre"));
				strsql.append("a01 a ");
				
				if(address!=null&&address.trim().length()>0&&setid!=null&&!"A01".equalsIgnoreCase(setid))
				{
					strsql.append(",(select * from "+vo.getString("pre")+setid+" b where  b.i9999=(select max(c.i9999) from "+vo.getString("pre")+setid+" c where b.a0100=c.a0100 )) d");
				}
				
				strsql.append(" where ");
				
				if(address!=null&&address.trim().length()>0&&setid!=null&&!"A01".equalsIgnoreCase(setid))
				{
					strsql.append(" a.a0100=d.a0100 and ");
				}
				
				if (codeItemId!=null && codeItemId.length()>0){
					if("UN".equalsIgnoreCase(codeSetId)) {
                        strsql.append(" a.b0110 like '");
                    } else if("UM".equalsIgnoreCase(codeSetId)) {
                        strsql.append(" a.e0122 like '");
                    } else {
                        strsql.append(" a.e01a1 like '");
                    }
					strsql.append(codeItemId); 					
					strsql.append("%'");
				}
				else if (inSql!=null && inSql.length()>0){ 
					if("UN".equalsIgnoreCase(codeSetId)) {
                        strsql.append(" a.b0110 in (");
                    } else if("UM".equalsIgnoreCase(codeSetId)) {
                        strsql.append(" a.e0122 in (");
                    } else {
                        strsql.append(" a.e01a1 in (");
                    }
					strsql.append(inSql); 					
					strsql.append(")");
				}
				else {
					return;
				}
				strsql.append(" and  nullif(a.");
				strsql.append(loguser);
				strsql.append(",'') is not null  ");
				strsql.append(" union all ");
				
			}
			strsql.setLength(strsql.length()-11);
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				LazyDynaBean vo=new LazyDynaBean();
				String str_key=rset.getString("dbpre")+rset.getString("a0100")+"_2";
                if (rset.getString(loguser)==null || "".equals((String)rset.getString(loguser))){
                    continue;
                }
				vo.set("username",rset.getString(loguser));
				 
				vo.set("a0100",rset.getString("dbpre")+rset.getString("a0100"));
				vo.set("userfullname",rset.getString("a0101"));
				if(!"".equals(email))
				{ 
					String temp=rset.getString(email);
					vo.set("email",(temp==null)?"":temp);
				}
				if(!"".equals(phone))
				{
					String temp=rset.getString(phone);
					vo.set("phone",(temp==null)?"":temp);
				}
                vo.set("b0110",rset.getString("b0110")==null?"":rset.getString("b0110"));
                vo.set("e0122",rset.getString("e0122")==null?"":rset.getString("e0122"));
                vo.set("e01a1",rset.getString("e01a1")==null?"":rset.getString("e01a1"));
				vo.set("status",String.valueOf(1));
				vo.set("password", rset.getString(password)==null?"":rset.getString(password));
				vo.set("type", "2");
//				if(!userhm.containsKey(str_key))
				userhm.put(str_key,vo);					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
		
	
	
	
	
	/**
	 * 按对象的考核关系角色发送消息
	 *角色属性“直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，属性值各自为“9，10，11，12，13
	 */
	public void sendObjectKhRelationRole(String tabid,String objs_sql,ArrayList khRelationRoleList,String title,String context,AsyncEmailBo bo,String isSendMessage,String fromaddr)
	{
		ContentDAO dao=new ContentDAO(this.con);	
		RowSet rowSet=null;
		try
		{
			
			StringBuffer level_str=new StringBuffer("");
			LazyDynaBean abean=null;
			for(int i=0;i<khRelationRoleList.size();i++)
			{
				abean=(LazyDynaBean)khRelationRoleList.get(i);
				String role_property=(String)abean.get("role_property");
				if("9".equals(role_property)) {
                    level_str.append(",1");
                }
				if("10".equals(role_property)) {
                    level_str.append(",0");
                }
				if("11".equals(role_property)) {
                    level_str.append(",-1");
                }
				if("12".equals(role_property)) {
                    level_str.append(",-2");
                }
				if("13".equals(role_property)) {
                    level_str.append(",1,0,-1,-2");
                }
				
			}
			
		//	if(this.isSub.equals("0"))
			{
				RowSet rowSet2=dao.search(objs_sql);
				while(rowSet2.next())
				{
					
					ArrayList list=new ArrayList();
					String dbase=rowSet2.getString("basepre");
					String a0100=rowSet2.getString("a0100");
					
					String sql="select pmb.*  from per_mainbody_std pmb,per_mainbodyset pmbs "
						+" where pmb.body_id=pmbs.body_id  and object_id='"+a0100+"' and ";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                        sql+=" level_o";
                    } else {
                        sql+=" level ";
                    }
					sql+=" in ("+level_str.substring(1)+")";
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String mainbody_id=rowSet.getString("mainbody_id");
						LazyDynaBean a_bean=new LazyDynaBean();
						a_bean.set("a0100","Usr"+mainbody_id);
						a_bean.set("email","");
						a_bean.set("phone","");
						a_bean.set("status","1");
						list.add(a_bean);
					}
					
					sendMessageToUser(dao,list,title,context,bo,isSendMessage,fromaddr,tabid,objs_sql);
				
				}
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rowSet2!=null) {
                    rowSet2.close();
                }
			}
		/*	else
			{
				String[] temps=objs_sql.split("/");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]==null||temps[i].trim().length()==0)
						continue;
					
					ArrayList list=new ArrayList();
					String dbase=temps[i].substring(0,3);
					String a0100=temps[i].substring(3);
					
					String sql="select pmb.*  from per_mainbody_std pmb,per_mainbodyset pmbs "
						+" where pmb.body_id=pmbs.body_id  and object_id='"+a0100+"' and ";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql+=" level_o";
					else
						sql+=" level ";
					sql+=" in ("+level_str.substring(1)+")";
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String mainbody_id=rowSet.getString("mainbody_id");
						LazyDynaBean a_bean=new LazyDynaBean();
						a_bean.set("a0100","Usr"+mainbody_id);
						a_bean.set("email","");
						a_bean.set("phone","");
						a_bean.set("status","1");
						list.add(a_bean);
					}
					
					sendMessageToUser(dao,list,title,context,bo,isSendMessage,fromaddr,tabid);
				
				}
				
				
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//提取"<<XXXX>>"中的文字XXXX
	public  List getContext(String html) {
        List resultList = new ArrayList();
		String str="<<(.+?)>>"; //"<-([^->]*)";
	    Pattern p = Pattern.compile(str); 
	    Matcher m = p.matcher(html );//开始编译
	    while (m.find()) {
	    	resultList.add(m.group(1));//获取被匹配的部分
	    }
	    return resultList;
	}
	
	
	/**
	 * 给本人发送邮件
	 * @param dao
	 * @param columnMap
	 * @param rowSet
	 * @param title
	 * @param context
	 * @param bo
	 * @param isSendMessage
	 * @param fromaddr
	 * @param tabid
	 */
	//liuyz bug26219
	public LazyDynaBean  sendMessageToSelf(ContentDAO dao,HashMap columnMap,RowSet rowSet,String title,String context,AsyncEmailBo newEmailBo,String isSendMessage,String fromaddr,String tabid)
	{
		 return sendMessageToSelf(dao, columnMap, rowSet, title, context, newEmailBo, isSendMessage, fromaddr, tabid, null);
	}
	
	/**
	 * 给本人发送邮件
	 * @Title: sendMessageToSelf   
	 * @Description: zxj 20141110  发送带附件的邮件   
	 * @param dao
	 * @param columnMap
	 * @param rowSet
	 * @param title
	 * @param context
	 * @param newEmailBo
	 * @param isSendMessage
	 * @param fromaddr
	 * @param tabid
	 * @param attachList
	 */
    public LazyDynaBean sendMessageToSelf(ContentDAO dao, HashMap columnMap, RowSet rowSet, String title, String context, AsyncEmailBo newEmailBo,
            String isSendMessage, String fromaddr, String tabid, ArrayList attachList) {
        LazyDynaBean abean = new LazyDynaBean();
        try {
            String a0100 = rowSet.getString("a0100");
            String basepre = rowSet.getString("basepre");
            //liuyz 如果是直接提交且为移库后发邮件，需要更改a0100和basepre为移库后的库前缀
            ResultSetMetaData metaData = rowSet.getMetaData();
            if("dest_base".equals(metaData.getColumnName(1).toLowerCase())&&rowSet.getString("dest_base")!=null&&!"".equals(rowSet.getString("dest_base")))
            {
            	basepre=rowSet.getString("dest_base");
            	if(this.destination_a0100!=null&&this.destination_a0100.size()>0)
            	{
            		String tempA0100=(String)this.destination_a0100.get(a0100);
            		a0100=(tempA0100==null||"".equals(tempA0100))?a0100:tempA0100;
            	}

            }
            String _context = context.replace("\r\n", "<br>").replace("\r", "<br>").replace("\n", "<br>");//bug 35915 邮件内容没有换行
            abean = getEmailBean("1", rowSet, title, _context, basepre + a0100, this.userview, tabid, "");
            //title = (String) abean.get("title");
            //context = (String) abean.get("context");

            if (("1".equals(isSendMessage) || "3".equals(isSendMessage)) && newEmailBo != null) {
                String toAddr = "";
                String objectId = "";
                //String email_field = newEmailBo.getEmail_field();
                //  if(columnMap.get(email_field.toLowerCase()+"_1")!=null&&rowSet.getString(email_field+"_1")!=null)
                //      email=rowSet.getString(email_field+"_1");

                //如果模板中没有变化前邮件地址，则找库中的数据

                abean.set("objectId", basepre+a0100);//发微信信息用
                if (this.template_emailAddress != null && this.template_emailAddress.trim().length() > 0) {
                    toAddr = getEmailAddress(basepre, a0100);
                    abean.set("toAddr", toAddr);
                } else{
                  objectId = basepre+a0100;
                  abean.set("objectId", objectId);
                }
                if(attachList!=null&&attachList.size()>0){
                    abean.set("attachList", attachList);
                }   
                
//                if (email != null && email.trim().length() > 0)
//                    newEmailBo.sendEmail(title, _context, attachList, fromaddr, email);              
            }
            
            if ("2".equals(isSendMessage) || "3".equals(isSendMessage)) {
                SmsBo smsbo = new SmsBo(this.con);
                smsbo.sendMessage(this.userview, basepre + a0100, context);
                if("2".equals(isSendMessage)){//如果是仅仅是发送消息,这个bean就不用返回用来发送邮件了
                    abean = null;
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return abean;
    }
	
	/**
	 * 
	 * @Title: sendMessageToUser   
	 * @Description: zxj 20141110  发送带附件的邮件   
	 * @param dao
	 * @param list
	 * @param title
	 * @param context
	 * @param newEmailBo
	 * @param isSendMessage
	 * @param fromaddr
	 * @param tabid
	 * @param objs_sql
	 * @param attachList
	 */
    public void sendMessageToUser(ContentDAO dao, ArrayList list, String title, String context, AsyncEmailBo newEmailBo, String isSendMessage,
            String fromaddr, String tabid, String objs_sql, ArrayList attachList) {
        try {
            ArrayList<LazyDynaBean> emailBeanList = new ArrayList<LazyDynaBean>();
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) list.get(i);
                LazyDynaBean _bean = getEmailBean("2", null, title, context, (String) abean.get("a0100"), this.userview, tabid,  objs_sql);

                String ori_context = (String) _bean.get("bodyText");

                title = (String) _bean.get("subject");
                if (("1".equals(isSendMessage) || "3".equals(isSendMessage)) && newEmailBo != null) {
                    String email = (String) abean.get("email");
                    String status = (String) abean.get("status");
                    String _context = ori_context.replace("\r\n", "<br>").replace("\r","<br>").replace("\n","<br>");
                    if (!isHTML(_context)) {
                        _context=_context.replaceAll(" ","&nbsp;");
                    }
                    
                    if ("1".equals(status)) {
                        String str = (String) abean.get("a0100");
                        String dbase = str.substring(0, 3);
                        String a0100 = str.substring(3);

                        _bean.set("objectId", str);
                        String toaddr = "";
                        String objectId = "";
                        if (this.template_emailAddress != null && this.template_emailAddress.trim().length() > 0) {
                            toaddr = getEmailAddress(dbase, a0100);
                            _bean.set("toAddr", toaddr);
                        } else{
                            objectId = dbase + a0100;
                            _bean.set("objectId", objectId);
                        }
                        
//                            toaddr = newEmailBo..getEmailAddrByA0100(dbase + a0100);
//                        
//                        if (toaddr != null)
//                            newEmailBo.sendEmail(title, _context, attachList, fromaddr, toaddr);
                        //此处没发邮件? wangrd 发微信的也不需要
                        //new TemplateUtilBo(this.con,this.userview).sendWeixinMessageFromEmail(_bean);
                        
                    } else {
                        if (email != null && email.length() > 0){
                            _bean.set("toAddr", email);
                        }
                    }
                    if(attachList!=null&&attachList.size()>0){
                        _bean.set("attachList", attachList);
                    }
                    emailBeanList.add(_bean);
                }
                
                if ("2".equals(isSendMessage) || "3".equals(isSendMessage)) {
                    SmsBo smsbo = new SmsBo(this.con);
                    String phone = (String) abean.get("phone");
                    String status = (String) abean.get("status");

                    this.setSmsContext(true);
                    _bean = getEmailBean("2", null, title, context, (String) abean.get("a0100"), this.userview, tabid, objs_sql);
                    ori_context = (String) _bean.get("bodyText");
                    this.setSmsContext(false);
                    ori_context = ori_context.replace("<br>", "");
                    ori_context = ori_context.replace("&nbsp;", " ");
                    if ("1".equals(status)) {
                        String str = (String) abean.get("a0100");
                        String dbase = str.substring(0, 3);
                        String a0100 = str.substring(3);
                        smsbo.sendMessage(this.userview, dbase + a0100, ori_context);
                    } else {
                        smsbo.setBflag(true);
                        smsbo.sendMessage(this.userview, phone, ori_context);
                    }
                }
            }
            if(emailBeanList.size()>0){
            	//gaohy
            	//2015-11-5
            	//抄送时，发送邮件
                newEmailBo.send(emailBeanList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void sendMessageToUser(ContentDAO dao,ArrayList list,String title,String context,AsyncEmailBo newEmailBo,String isSendMessage,String fromaddr,String tabid,String objs_sql)
	{
	    sendMessageToUser(dao, list, title, context, newEmailBo, isSendMessage, fromaddr, tabid, objs_sql, null);
	}
	
	public String getEmailAddress(String dbpre,String a0100)
	{
		String email="";
		try
		{
			ContentDAO dao=new ContentDAO(this.con);	
			RowSet rowSet=null;
			
			 
			if(this.template__set==null||this.template__set.trim().length()==0|| "A01".equalsIgnoreCase(this.template__set))
			{
					rowSet=dao.search("select "+this.template_emailAddress+" from "+dbpre+"A01 where a0100='"+a0100+"'");
					if(rowSet.next())
					{
						if(rowSet.getString(this.template_emailAddress.trim())!=null) {
                            email=rowSet.getString(this.template_emailAddress.trim());
                        }
					}
			}
			else
			{
					rowSet=dao.search("select "+this.template_emailAddress+" from "+dbpre+template__set+" b where  b.i9999=(select max(c.i9999) from "+dbpre+template__set+" c where b.a0100=c.a0100 )     and b.a0100='"+a0100+"'");
					if(rowSet.next())
					{
						if(rowSet.getString(this.template_emailAddress.trim())!=null) {
                            email=rowSet.getString(this.template_emailAddress.trim());
                        }
					}
			}
			 
		}
		catch(Exception e)
		{
			
		}
		
		return email;
	}
	
	
	
	
	/**
	 * 根据邮件模板id找邮件地址指标
	 * @param id
	 * @param dao
	 * @return
	 */
	public String getEmailFiledByTemplate(String id,ContentDAO dao)
	{
		String field="";
		try
		{
			RowSet rowSet=dao.search("select address from email_name where id="+id);
			if(rowSet.next())
			{
				String address=rowSet.getString("address");
				String[] temps=address.split(":");
				field=temps[0].trim();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return field;
	}
	
	//不需要审批时，提交弹窗，点击确定后执行该方法给
	public  void sendMessage(HashSet sets,String isSendMessage,String context,String title,String tabid,String objs_sql,boolean email_staff,String email_staff_value,String template_staff)
	{
		this.sendMessage(sets, isSendMessage, context, title, tabid, objs_sql, email_staff, email_staff_value, template_staff, null);
	}
	//不需要审批时，提交弹窗，点击确定后执行该方法给
	public  void sendMessage(HashSet sets,String isSendMessage,String context,String title,String tabid,String objs_sql,boolean email_staff,String email_staff_value,String template_staff,String template_bos)
	{
		ContentDAO dao=new ContentDAO(this.con);	
		try
		{
			String ori_title=title;
			String ori_context=context;
			 
			UserObjectBo user_bo=new UserObjectBo(this.con);
			RowSet rowSet=null;
			String fromaddr=this.getFromAddr();
			ArrayList list=new ArrayList();
			ArrayList khRelationRoleList=new ArrayList();
			
			TemplateTableBo tablebo=new TemplateTableBo(this.con,Integer.parseInt(tabid),this.userview);
			if(!email_staff) {
                email_staff=tablebo.isEmail_staff();
            }
			if(template_staff==null) {
                template_staff=tablebo.getTemplate_staff();  //员工本人的邮件模板
            }
			if(template_bos==null) {
                template_bos=tablebo.getTemplate_bos();    ////业务办理人员的邮件模板
            }
		
			//EMailBo bo =null;
			AsyncEmailBo newEmailBo = null;
			if(template_bos!=null&&template_bos.trim().length()>0)
			{
				LazyDynaBean mailInfo=getTemplateMailInfo(template_bos);
				context=(String)mailInfo.get("content");
			//	title=(String)mailInfo.get("subject");
				this.template_emailAddress=(String)mailInfo.get("address");
				this.template__set=(String)mailInfo.get("set");
				
				
			}
			//发送邮件给报送对象
			list=getSendObjectList(sets,dao,khRelationRoleList,user_bo,this.template_emailAddress,this.template__set);			
			
			/** 中建接口 */
			boolean flag=false;
			if(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail"))) {
                flag=true;
            }
			if(flag){
					sendMessageToOA(sets,context,title,tabid);
			}
			else
			{
				if(newEmailBo==null)
				{
					try
					{
						//bo=new EMailBo(this.con,true,"");
					    newEmailBo = new AsyncEmailBo(this.con, this.userview);
					}
					catch(Exception e)
					{
						return;
					}
				}
				if(list.size()>0)
				{
						sendMessageToUser(dao,list,title,context,newEmailBo,isSendMessage,fromaddr,tabid,objs_sql);
				}
			
				if(khRelationRoleList.size()>0)
				{
					sendObjectKhRelationRole(tabid,objs_sql,khRelationRoleList,title,context,newEmailBo, isSendMessage,fromaddr);
				}
			}
			
			
			//// 发送邮件给本人
			if(email_staff&& "1".equals(email_staff_value))
			{
				if(newEmailBo==null)
				{
					try
					{
					    newEmailBo = new AsyncEmailBo(con, this.userview);
					}
					catch(Exception e)
					{
						return;
					}
				}
				
				context=ori_context;
				title=ori_title;
				ArrayList attachList=new ArrayList();//bug 35405 直接提交通知本人没有附件
				if(template_staff!=null&&template_staff.trim().length()>0)
				{
					LazyDynaBean mailInfo=getTemplateMailInfo(template_staff);
					context=(String)mailInfo.get("content");
					title=(String)mailInfo.get("subject");
					this.template_emailAddress=(String)mailInfo.get("address");
					this.template__set=(String)mailInfo.get("set");
					attachList = (ArrayList)mailInfo.get("attach"); 
				}
				
				HashMap columnMap=new HashMap();
				rowSet=dao.search(objs_sql);
				ResultSetMetaData md=rowSet.getMetaData();
				for(int i=0;i<md.getColumnCount();i++)
				{
					int columnType=md.getColumnType(i+1);	
					String columnName=md.getColumnName(i+1).toLowerCase();
					if(columnType==java.sql.Types.TIMESTAMP||columnType==java.sql.Types.DATE||columnType==java.sql.Types.TIME) {
                        columnMap.put(columnName, "D");
                    } else {
                        columnMap.put(columnName, "A");
                    }
				}
			//	context=context.replaceAll("\r\n","<br>");
			//	context=context.replaceAll(" ","&nbsp;");
				//liuyz bug26219
				ArrayList<LazyDynaBean> emailBeanList = new ArrayList<LazyDynaBean>();
				while(rowSet.next())
				{
					LazyDynaBean abean=sendMessageToSelf(dao,columnMap,rowSet,title,context,newEmailBo,isSendMessage,fromaddr,tabid,attachList);//bug 35405 直接提交通知本人没有附件
					if(abean!=null)
					{
						emailBeanList.add(abean);
					}
				}
				if(emailBeanList.size()>0)
				{
					newEmailBo.send(emailBeanList);
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
	}
	
	/**
	 * @param infor_type 
	 * 
	* @Title: sendFilingMessage 
	* @Description: 发送业务处理的报备信息 
	* @param @param actorid 处理用户的id
	* @param @param actortype 处理业务的人员的性质 人员||用户
	* @param @param newEmailBo 发送邮件中使用的bo类
	* @param @param tablebo 人事异动的templateTableBo类
	* @param @param isSendMessage 是否发送消息
	* @param @param context  邮件内容
	* @param @param title 邮件主题
	* @param @param tabid 模版的id
	* @param @param emailInfo_staff 邮件模版的相关信息
	* @param @param emialInfo_bos  邮件模版的相关信息
	* @param @param objs_sql 相关的查询语句
	* @param @param email_staff_value
	* @param @param columnMap
	* @param @param seqnumList  涉及到的相关人员
	* @param @param opt  是报批 驳回  还是批准 （自定义审批流程时 发送报备信息时传递的都是3）
	* @return void    返回类型 
	* @throws
	 */
	public  void sendFilingMessage(String actorid,String actortype,AsyncEmailBo newEmailBo,TemplateTableBo tablebo,String isSendMessage,String context,String title,
			String tabid,LazyDynaBean emailInfo_staff,LazyDynaBean emialInfo_bos,String objs_sql,String email_staff_value,HashMap columnMap, ArrayList seqnumList, String opt, int infor_type)
	{
		ContentDAO dao=new ContentDAO(this.con);	
		try
		{
			String ori_title=title;
			String ori_context=context; 
			RowSet rowSet=null;
			String fromaddr=this.getFromAddr(); 
			HashSet set=new HashSet();
			if(actorid.length()>0&&actortype.length()>0) {
                set.add(actortype+":"+actorid);
            }
		 	 
			ArrayList attachList = null;
			if(emialInfo_bos!=null)
			{ 
				context = (String)emialInfo_bos.get("content");
				//title = (String)emialInfo_bos.get("subject"); 
				//zxj 20141023 邮件模板附件
				attachList = (ArrayList)emialInfo_bos.get("attach"); 
			}
			
			//发送邮件给报送对象
			ArrayList list = getSendObjectList(set,dao,null,null,"","");			
			
			/** 中建接口 */
			String ssoZjzOASendMail = SystemConfig.getPropertyValue("sso_zjz_oa_sendmail");
			boolean flag = ssoZjzOASendMail != null && "true".equalsIgnoreCase(ssoZjzOASendMail);
			
			if(flag){
				sendMessageToOA(set,context,title,tabid);
			} else if(list.size()>0) {
				//sendMessageToUser(dao,list,title,context,bo,isSendMessage,fromaddr,tabid,objs_sql, attachList);
			    sendMessageToUserForNewEmail(dao,list,ori_title,context,newEmailBo,isSendMessage,fromaddr,tabid,objs_sql, attachList,emialInfo_bos,seqnumList,opt,infor_type);
			}
			
			//// 发送邮件给本人   
			if("1".equals(email_staff_value))
			{ 
				context=ori_context;
				title=ori_title;
				if(emailInfo_staff!=null)
				{ 
					context = (String)emailInfo_staff.get("content");
					title = (String)emailInfo_staff.get("subject"); //liuyz 获取模版中定义的名称
					//zxj 20141023 邮件模板附件
					attachList = (ArrayList)emailInfo_staff.get("attach"); 
				} 
				rowSet=dao.search(objs_sql);
				ArrayList emailBeanList = new ArrayList();
				while(rowSet.next())
				{
					LazyDynaBean abean = null;
					if("2".equals(opt)||emailInfo_staff==null){//驳回||没有定义邮件模版
					    abean = sendMeaasgeToSelfForNewEmail(rowSet,title,context,isSendMessage,tabid,attachList,opt,emailInfo_staff,objs_sql,seqnumList,infor_type);
					}else{//定义了邮件模版的报备
					    abean = sendMessageToSelf(dao,columnMap,rowSet,title,context,newEmailBo,isSendMessage,fromaddr,tabid, attachList);
					}
					if(abean!=null){
					    emailBeanList.add(abean);
					}
				}
				if(emailBeanList.size()>0){
				    newEmailBo.send(emailBeanList);
	                //发微信 wangrd 2015-05-11
				    TemplateUtilBo sendWx= new TemplateUtilBo(this.con,this.userview);
				    sendWx.setIsEmail_staff_value(true);
				    sendWx.sendWeixinMessageFromEmail(emailBeanList);  
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
	}
	
	/**
	 * @param infor_type 
	 * @param seqnumList 如果是报备或者驳回的话,表头中人员的提示信息从这里取值
	 * @param objs_sql  如果是需要审批的流程,从这个sql中取得人员的信息
    * @Title: sendMeaasgeToSelfForNewEmail 
    * @Description: 给本人发送邮件
    * @param @param rowSet 取得本人的信息的集合
    * @param @param title  邮件的表头
    * @param @param context 邮件的内容
    * @param @param newEmailBo 邮件BO类
    * @param @param isSendMessage 是否发送消息 0：不发送 1：发送邮件 2：发送短信 3：发送邮件和短信
    * @param @param tabid  模版号
    * @param @param attachList  邮件是否有附件
    * @param @param opt  处理方式  驳回  还是报批  
    * @param @param emailInfoStaff  邮件模版的相关信息
    * @param @return    设定文件 
    * @return LazyDynaBean    返回类型 
    * @throws 
    */ 
    
    
    private LazyDynaBean sendMeaasgeToSelfForNewEmail(RowSet rowSet, String title, String context, String isSendMessage, String tabid, ArrayList attachList, String opt,
            LazyDynaBean emailInfoStaff, String objs_sql, ArrayList seqnumList, int infor_type) {

        LazyDynaBean abean = new LazyDynaBean();
        try {
            String a0100 = rowSet.getString("a0100");
            String basepre = rowSet.getString("basepre");
            
            if("2".equals(opt)||emailInfoStaff==null){
                abean = getTileAndContent(opt, title, context, basepre + a0100, this.userview, tabid, objs_sql,seqnumList,infor_type);
            }else{
                abean = getEmailBean("1", rowSet, title, context, basepre + a0100, this.userview, tabid, "");
            }

            if ("1".equals(isSendMessage) || "3".equals(isSendMessage)) {
                String toAddr = "";
                String objectId = "";
                //发微信信息用 wangrd
                abean.set("objectId", basepre+a0100);
                if (this.template_emailAddress != null && this.template_emailAddress.trim().length() > 0) {
                    toAddr = getEmailAddress(basepre, a0100);
                    abean.set("toAddr", toAddr);
                } else{
                  objectId = basepre+a0100;
                  abean.set("objectId", objectId);
                }
                if(attachList!=null&&attachList.size()>0){
                    abean.set("attachList", attachList);
                }   
            }
            
            if ("2".equals(isSendMessage) || "3".equals(isSendMessage)) {
                SmsBo smsbo = new SmsBo(this.con);
                String smscontext = (String) abean.get("bodyText");
                smsbo.sendMessage(this.userview, basepre + a0100, smscontext);
                if("2".equals(isSendMessage)){//如果是仅仅是发送消息,这个bean就不用返回用来发送邮件了
                    abean = null;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return abean;
    }

    /**
	 * @param opt 是驳回还是批准 还是报批
	 * @param seqnumList 
	 * @param emialInfoBos  
    * @Title: sendMessageToUserForNewEmail 
    * @Description: TODO发送邮件采用最新的bo,方式也要变一下
    * @param  dao
    * @param  list//要发送人员的list
    * @param  title//邮件的主题
    * @param  context//邮件的内同
    * @param  bo
    * @param  isSendMessage
    * @param  fromaddr//由谁发出
    * @param  tabid//模版号
    * @param  objsSql
    * @param  attachList  模板的附件
    * @return void    返回类型 
    * @throws 
    */ 
    
    
    private void sendMessageToUserForNewEmail(ContentDAO dao, ArrayList list, String title, String context, AsyncEmailBo newEmailBo, String isSendMessage,
            String fromaddr, String tabid, String objs_sql, ArrayList attachList, LazyDynaBean emialInfoBos, ArrayList seqnumList, String opt,int infor_type) {
        try {
            ArrayList emailSendList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) list.get(i);
                LazyDynaBean _bean = null;
                if("2".equals(opt)||emialInfoBos==null){//如果是驳回或者没有定义邮件模板,采用固定的邮件消息信息
                    _bean = getTileAndContent(opt, title, context, (String) abean.get("a0100"), this.userview, tabid, objs_sql,seqnumList,infor_type);
                }else{
                    _bean = getEmailBean("2", null, title, context, (String) abean.get("a0100"), this.userview, tabid,  objs_sql);
                }
                //发送微信信息用
                if(StringUtils.isEmpty((String) abean.get("email"))) {//业务用户设置邮箱后，优先走业务用户设置邮箱，无需再根据重新查询人员邮箱
                	_bean.set("objectId", (String) abean.get("a0100"));
                }
                
                String ori_context = (String) _bean.get("bodyText");

                title = (String) _bean.get("subject");
                if (("1".equals(isSendMessage) || "3".equals(isSendMessage)) && newEmailBo != null) {
                    String email = (String) abean.get("email");
                    String status = (String) abean.get("status");
                    String _context = ori_context.replace("\r\n", "<br>").replace("\r","<br>").replace("\n","<br>");
                    if (!isHTML(_context)) {
                        _context=_context.replaceAll(" ","&nbsp;");
                    }
                    
                    if ("1".equals(status)) {
                        String str = (String) abean.get("a0100");
                        String dbase = str.substring(0, 3);
                        String a0100 = str.substring(3);

                        String toaddr = "";
                        String objectId = "";
                        if (this.template_emailAddress != null && this.template_emailAddress.trim().length() > 0) {
                            toaddr = getEmailAddress(dbase, a0100);
                        } else{
                            objectId= dbase+a0100;
                        }
                        if(toaddr.trim().length()>0){
                            _bean.set("toAddr", toaddr);
                        }else{
                            _bean.set("objectId", objectId);
                        }
                    } else {
                        if (email != null && email.length() > 0){
                            //bo.sendEmail(title, _context, attachList, fromaddr, email);
                            _bean.set("toAddr", email);
                           
                        }
                            
                    }
                    
                    if(attachList!=null&&attachList.size()>0){
                        _bean.set("attachList", attachList);
                    }
                    emailSendList.add(_bean);
                }
                
                if ("2".equals(isSendMessage) || "3".equals(isSendMessage)) {
                    SmsBo smsbo = new SmsBo(this.con);
                    String phone = (String) abean.get("phone");
                    String status = (String) abean.get("status");

                    this.setSmsContext(true);
                    _bean = getEmailBean("2", null, title, context, (String) abean.get("a0100"), this.userview, tabid, objs_sql);
                    ori_context = (String) _bean.get("bodyText");
                    this.setSmsContext(false);
                    ori_context = ori_context.replaceAll("<br>", "");
                    ori_context = ori_context.replaceAll("&nbsp;", " ");
                    if ("1".equals(status)) {
                        String str = (String) abean.get("a0100");
                        String dbase = str.substring(0, 3);
                        String a0100 = str.substring(3);
                        smsbo.sendMessage(this.userview, dbase + a0100, ori_context);
                    } else {
                        smsbo.setBflag(true);
                        smsbo.sendMessage(this.userview, phone, ori_context);
                    }
                }
            }
            if(emailSendList.size()>0){
            	//发送邮件
                newEmailBo.send(emailSendList);
                //发微信 wangrd 2015-05-11
                new TemplateUtilBo(this.con,this.userview).sendWeixinMessageFromEmail(emailSendList); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * 取得邮件模板内容
	 * @param title
	 * @param context
	 * @param userView
	 * @param tabid
	 * @param obj_sql
	 * @param opt 1:抄送本人  
	 * @return
	 */
	public LazyDynaBean getEmailBean(String opt,RowSet recset,String title,String context,String a0100,UserView userView,String tabid,String obj_sql )
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			abean.set("subject",title);
			context = PubFunc.keyWord_reback(context);
			ContentDAO dao=new ContentDAO(this.con);	
			HashMap columnMap=new HashMap();
			RowSet rowSet=null;
			if("1".equals(opt)) {
                rowSet=recset;
            } else{
				if ("".equals(obj_sql)){
					return abean;
				}
				rowSet=dao.search(obj_sql);
			}
			ResultSetMetaData md=rowSet.getMetaData();
			for(int i=0;i<md.getColumnCount();i++)
			{
				int columnType=md.getColumnType(i+1);	
				String columnName=md.getColumnName(i+1).toLowerCase();
				if(columnType==java.sql.Types.TIMESTAMP||columnType==java.sql.Types.DATE||columnType==java.sql.Types.TIME) {
                    columnMap.put(columnName, "D");
                } else {
                    columnMap.put(columnName, "A");
                }
			}
			Date d=new Date();
			SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");
			context=context.replaceAll("#", "＃");
			if("1".equals(this.sendresource)) {
                context=context.replaceAll("＃发件人姓名＃","人力资源系统");
            } else {
                context=context.replaceAll("＃发件人姓名＃",userView.getUserFullName());
            }
			context=context.replaceAll("＃日期＃",f.format(d));
			f=new SimpleDateFormat("HH:mm");
			
			context=context.replaceAll("＃时间＃",f.format(d));
			
			if(context.indexOf("＃模板名称＃")!=-1)
			{
				/*
				RowSet rowSet0=dao.search("select name from template_table where tabid="+tabid);
				if(rowSet0.next())
					context=context.replaceAll("＃模板名称＃",rowSet0.getString("name"));
				if(rowSet0!=null)
					rowSet0.close();
					*/
				RecordVo tabvo=TemplateStaticDataBo.getTableVo(Integer.parseInt(tabid),this.con); //20171111 邓灿，采用缓存解决并发下压力过大问题
				if(tabvo!=null&&tabvo.getString("name")!=null)
				{
					context=context.replaceAll("＃模板名称＃",tabvo.getString("name"));
				}
			}
			
			/** <table>学历性质$<<a04:c0401:current>>|学历$<<a04:a0405:current>>|毕业时间$<<a04:a0430:current>></table>          
			 * 主集： <<A0101_1>>   
			 * 子集：<<A02_1:A0202:xxxx>>   
			 * (xxxx:  current:当前记录，first:第一条记录,　数字：第几条记录)
			 */
			context=getTableHtml(opt,recset,obj_sql,context,columnMap);
			if("1".equals(opt))
			{
				context= getFieldValue(context,columnMap,rowSet);
			}
			else if(rowSet.next()) {
                context= getFieldValue(context,columnMap,rowSet);
            }
			
			context=context.replaceAll("#", "＃");
			LazyDynaBean _abean=getUserNamePassword(a0100);
			String url="";
			if(_abean!=null)
			{
				String username=(String)_abean.get("username");
				String password=(String)_abean.get("password");	
				String a0101=(String)_abean.get("a0101");
				if(a0101==null||a0101.trim().length()==0) {
                    a0101=username;
                }
				context=context.replaceAll("＃收件人姓名＃",a0101);
				String _businessModel="";
				if("61".equals(businessModel)|| "71".equals(businessModel)) {
                    _businessModel="&businessModel="+businessModel;
                } else {
                    _businessModel="&businessModel=0";
                }
				String pendingCode="HRMS-"+task_id; 
				
				
				//在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
	            int tab_id=Integer.parseInt(tabid);
	            TemplateUtilBo tb=new TemplateUtilBo(this.con,userView);
	            String view = tb.getTemplateView(tab_id);
	            
	        	if(view!=null&& "list".equalsIgnoreCase(view)){
	        		url = userView.getServerurl() +"/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag="+sp_flag+"&pre_pendingID="+pendingCode+"&validatepwd=false&ins_id="+ins_id+"&returnflag=12&task_id="+task_id+"&tabid="+tabid+"&index_template=1&appfwd=1&isemail=true&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
	        	}else {
	        		url = userView.getServerurl() +"/general/template/edit_form.do?b_query=link"+_businessModel+"&tabid="+tabid+"&pre_pendingID="+pendingCode+"&validatepwd=false&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(task_id)+"&sp_flag="+sp_flag+"&returnflag=12&appfwd=1&isemail=true&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
	        	}
				if (PubFunc.isUseNewPrograme(this.userview)){
				    String approve_flag="1";
				    if ("3".equals(sp_flag)){
				        approve_flag="3"; 
				    }
				    //liuyz bug26155 begin
				    if("2".equals(sp_flag))
				    {
				    	approve_flag="2"; 
				    }
				  //liuyz bug26155 end
				    if("0".equals(sp_flag))
				    {
				    	approve_flag="0"; 
				    }
				    String newUrl ="/module/template/templatemain/templatemain.html?b_query=link&ins_id="+ins_id+"&task_id="
	                    +PubFunc.encrypt(task_id)+"&tab_id="+tabid+"&return_flag=13"
	                    +"&approve_flag="+approve_flag
	                    +"&pre_pendingID="+pendingCode;
				    url=userView.getServerurl()+"/module/utils/jsp.do?br_query=link&param="
				        +SafeCode.encode(newUrl)  
			            +"&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)))+"&sp_flag="+sp_flag; 
				}
				//String url = userView.getServerurl() +"/general/template/edit_form.do?b_query=link"+_businessModel+"&tabid="+tabid+"&pre_pendingID="+pendingCode+"&validatepwd=false&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(task_id)+"&sp_flag="+sp_flag+"&returnflag=12&appfwd=1&isemail=true&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
				 
				
				if(task_id==null||task_id.trim().length()==0) {
                    url="";
                }
			}
			
			abean.set("href", url);
			if(!isSmsContext)
			{
				context=context.replace("\r\n","<br>").replace("\n","<br>").replace("\r","<br>");//bug 38021 邮件内容没有换行
				//zxj 20141201 非html有邮件模板需特殊处理空格符，否则多空格会显示一个
				if (!isHTML(context)) {
                    context=context.replace(" ","&nbsp;");
                }
				context=context.replace("<<自动登录>>","");
				context=context.replace("＃自动登录链接＃","");
			
			}
			else{
				context=context.replace("<<自动登录>>","");
				context=context.replace("＃自动登录链接＃","");
			}
			
			abean.set("bodyText",context);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	public LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		LazyDynaBean rec=null;
		StringBuffer sql=new StringBuffer();
		sql.append("select username,password,fullname a0101 from operuser where username='"+value+"'");
		List rs=ExecuteSQL.executeMyQuery(sql.toString());
		if(rs!=null&&rs.size()>0)
		{
		    	rec=(LazyDynaBean)rs.get(0);	    	
		}
		else
		{
			String nbase=value.substring(0,3);
			String a0100=value.substring(3);
			AttestationUtils utils=new AttestationUtils();
			LazyDynaBean fieldbean=utils.getUserNamePassField();
			String username_field=(String)fieldbean.get("name");
		    String password_field=(String)fieldbean.get("pass");
		    sql.setLength(0);
		    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
		    sql.append(" where a0100='"+a0100+"'");
		    rs=ExecuteSQL.executeMyQuery(sql.toString());
		    
		    if(rs!=null&&rs.size()>0)
		    {
		    	rec=(LazyDynaBean)rs.get(0);	    	
		    }else{//登录用户名“刘启蒙”他有nbase的情况下
		    	sql.setLength(0);
		    	sql.append("select username,password,fullname a0101 from operuser where username='"+a0100+"'");
				 rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(rs!=null&&rs.size()>0)
				{
				    	rec=(LazyDynaBean)rs.get(0);	    	
				}
		    }
		} 
	    return rec;
	}
	/**将邮件模板中的指标替换成真正的值。邮件模板中指标的格式为<<a0101_1>>*/
	public String getFieldValue(String context,HashMap columnMap,RowSet rowSet)
	{
		try
		{
//			String a0100=rowSet.getString("a0100");
//			String basepre=rowSet.getString("basepre");
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd");
			/**将邮件中的指标值替换为模板中的值
			 * 主集： <<A0101_1>>   
			 * 子集：<<A02_1:A0202:xxxx>>   
			 * (xxxx:  current:当前记录，first:第一条记录,　数字：第几条记录)
			 */
			context = PubFunc.keyWord_reback(context);
			List replaceStrList=getContext(context);
			String tempStr="";
			for(int i=0;i<replaceStrList.size();i++)
			{
				tempStr=(String)replaceStrList.get(i);
			 
				
				String type="";
				if(columnMap.get(tempStr.toLowerCase())!=null) {
                    type=(String)columnMap.get(tempStr.toLowerCase());
                }
				boolean isvalue=false;
				if("D".equalsIgnoreCase(type)&&rowSet.getDate(tempStr)!=null) {
                    isvalue=true;
                }
				if(!"D".equalsIgnoreCase(type)&&type.length()>0&&rowSet.getString(tempStr)!=null) {
                    isvalue=true;
                }
				if(tempStr.indexOf(":")==-1&&isvalue)
				{
					if("D".equals((String)columnMap.get(tempStr.toLowerCase()))) {
                        context=context.replaceAll("<<"+tempStr+">>",df.format(rowSet.getDate(tempStr)));
                    } else
					{
						FieldItem item=DataDictionary.getFieldItem(tempStr.substring(0,tempStr.length()-2));
						if(item!=null&&!"0".equals(item.getCodesetid())) {
                            context=context.replaceAll("<<"+tempStr+">>",AdminCode.getCodeName(item.getCodesetid(),rowSet.getString(tempStr)));
                        } else {
                            context=context.replaceAll("<<"+tempStr+">>", rowSet.getString(tempStr));
                        }
					}
				}
				else if(tempStr.indexOf(":")==-1&&!isvalue&&!"自动登录".equals(tempStr))
				{
					context=context.replaceAll("<<"+tempStr+">>","");
				
				}
				else
				{
					String[] temps=tempStr.split(":");
					if(columnMap.get("t_"+temps[0].toLowerCase())!=null)
					{
						 String columnvalue = rowSet.getString("t_"+temps[0]);
						 if(StringUtils.isNotBlank(columnvalue)) {
//							 byte bt[] =rowSet.getString("t_"+temps[0]).getBytes();
//							 ByteArrayInputStream input = new ByteArrayInputStream(bt);
							 Document doc = PubFunc.generateDom(rowSet.getString("t_"+temps[0]));;
							 Element 	root = doc.getRootElement();
							 String columns=root.getAttributeValue("columns").toUpperCase();
							 String[] _columns=columns.split("`");
							
							 String replaceValue="";
							 int index=-1;
							 int columnIndex=-1;
							 for(int j=0;j<_columns.length;j++)
							 {
								 if(_columns[j].equalsIgnoreCase(temps[1]))
								 {
									 columnIndex=j;
									 break;
								 }
							 }
							 List childList=root.getChildren();
							 if("current".equalsIgnoreCase(temps[2])) {
                                 index=childList.size()-1;
                             } else  if("first".equalsIgnoreCase(temps[2])&&childList.size()>0) {
                                 index=0;
                             } else
							 {
								 if(Integer.parseInt(temps[2])<=childList.size()) {
                                     index=Integer.parseInt(temps[2])-1;
                                 }
							 }
							 
							 
							 
							 
							 for(int j=0;j<childList.size();j++)
							 {
								 if(j==index)
								 {
									 Element e=(Element)childList.get(j);
									 String value=e.getValue();
									 String[] _values=value.split("`");
									 if(columnIndex!=-1&&columnIndex<_values.length) {
                                         replaceValue=_values[columnIndex];
                                     }
								 }
							 }
							 
							 if(replaceValue.length()>0)
							 {
								 FieldItem item=DataDictionary.getFieldItem(temps[1]);
								 if(item!=null&&!"0".equals(item.getCodesetid())) {
                                     context=context.replaceAll("<<"+tempStr+">>",AdminCode.getCodeName(item.getCodesetid(),replaceValue));
                                 } else {
                                     context=context.replaceAll("<<"+tempStr+">>",replaceValue);
                                 }
							 }
							 else {
                                 context=context.replaceAll("<<"+tempStr+">>","");
                             }
						 }
						 else {
                             context=context.replaceAll("<<"+tempStr+">>","");
                         }
					}
					else if(!"自动登录".equals(tempStr)) {
                        context=context.replaceAll("<<"+tempStr+">>","");
                    }
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return context;
	}
	
	
	//<table>学历性质$<<a04:c0401:current>>|学历$<<a04:a0405:current>>|毕业时间$<<a04:a0430:current>></table>
	public String getTableHtml(String opt,RowSet recset,String obj_sql,String context,HashMap columnMap)
	{
	
		StringBuffer str=new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.con);	
		try
		{
			int from_index=-1;
			int to_index=-1;
			context = PubFunc.keyWord_reback(context);
			if(context.indexOf("<table>")!=-1) {
                from_index=context.indexOf("<table>");
            }
			if(context.indexOf("</table>")!=-1) {
                to_index=context.indexOf("</table>");
            }
			if(from_index!=-1&&to_index!=-1&&from_index<to_index)
			{
				String _context=context.substring(from_index+7,to_index);
				String[] num=_context.split("\\|");
				str.append("<table ><tr>");
				for(int i=0;i<num.length;i++)
				{
					String[] _temp=num[i].trim().split("\\$");
					str.append("<td >&nbsp;&nbsp;"+_temp[0]+"&nbsp;&nbsp;</td>");
					
				}
				str.append("</tr>");
				RowSet rowSet=null;
				if("1".equals(opt))
				{
					rowSet=recset;
					insetRowHtml(rowSet,str,_context,columnMap);
				}
				else
				{	
					rowSet=dao.search(obj_sql);
					while(rowSet.next())
					{
						insetRowHtml(rowSet,str,_context,columnMap);
					}
				}
				
				str.append("</table>");
				
				
				String s1=context.substring(0,from_index);
				String s2=context.substring(to_index+8);
				context="";
				context+=s1+"<br>";
				context+=str.toString();
				context+="<br>"+s2;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return context;
	}
	
	
	public void insetRowHtml(RowSet rowSet,StringBuffer str,String context,HashMap columnMap)
	{
		try
		{
			SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd");
			str.append("<tr>");
			
			String a0100=rowSet.getString("a0100");
			String basepre=rowSet.getString("basepre");

			
			/**将邮件中的指标值替换为模板中的值
			 * 主集： <<A0101_1>>   
			 * 子集：<<A02_1:A0202:xxxx>>   
			 * (xxxx:  current:当前记录，first:第一条记录,　数字：第几条记录)
			 */
			context = PubFunc.keyWord_reback(context);
			List replaceStrList=getContext(context);
			
			String tempStr="";
			for(int i=0;i<replaceStrList.size();i++)
			{
				tempStr=(String)replaceStrList.get(i);
				String type="";
				if(columnMap.get(tempStr.toLowerCase())!=null) {
                    type=(String)columnMap.get(tempStr.toLowerCase());
                }
				boolean isvalue=false;
				if("D".equalsIgnoreCase(type)&&rowSet.getDate(tempStr)!=null) {
                    isvalue=true;
                }
				if(!"D".equalsIgnoreCase(type)&&type.length()>0&&rowSet.getString(tempStr)!=null) {
                    isvalue=true;
                }
				String value="";
				if(tempStr.indexOf(":")==-1&&isvalue)
				{
					if("D".equals((String)columnMap.get(tempStr.toLowerCase()))) {
                        value=df.format(rowSet.getDate(tempStr));
                    } else
					{
						FieldItem item=DataDictionary.getFieldItem(tempStr.substring(0,tempStr.length()-2));
						if(item!=null&&!"0".equals(item.getCodesetid())) {
                            value=AdminCode.getCodeName(item.getCodesetid(),rowSet.getString(tempStr));
                        } else {
                            value=rowSet.getString(tempStr);
                        }
					}
				}
				else if(tempStr.indexOf(":")==-1&&!isvalue) {
                    value="";
                } else
				{
					String[] temps=tempStr.split(":");
					if(columnMap.get("t_"+temps[0].toLowerCase())!=null)
					{
						String columvalue = rowSet.getString("t_"+temps[0]);
						if(StringUtils.isNotBlank(columvalue)) {
//							 byte bt[] =columvalue.getBytes();
//							 ByteArrayInputStream input = new ByteArrayInputStream(bt);
							 Document doc = PubFunc.generateDom(columvalue);;
							 Element 	root = doc.getRootElement();
							 String columns=root.getAttributeValue("columns").toUpperCase();
							 String[] _columns=columns.split("`");
							
							 String replaceValue="";
							 int index=-1;
							 int columnIndex=-1;
							 for(int j=0;j<_columns.length;j++)
							 {
								 if(_columns[j].equalsIgnoreCase(temps[1]))
								 {
									 columnIndex=j;
									 break;
								 }
							 }
							 List childList=root.getChildren();
							 if("current".equalsIgnoreCase(temps[2])) {
                                 index=childList.size()-1;
                             } else  if("first".equalsIgnoreCase(temps[2])&&childList.size()>0) {
                                 index=0;
                             } else
							 {
								 if(Integer.parseInt(temps[2])<=childList.size()) {
                                     index=Integer.parseInt(temps[2])-1;
                                 }
							 }
							 
							 
							 
							 
							 for(int j=0;j<childList.size();j++)
							 {
								 if(j==index)
								 {
									 Element e=(Element)childList.get(j);
									 String avalue=e.getValue();
									 String[] _values=avalue.split("`");
									 if(columnIndex!=-1&&columnIndex<_values.length) {
                                         replaceValue=_values[columnIndex];
                                     }
								 }
							 }
							 
							 if(replaceValue.length()>0)
							 {
								 FieldItem item=DataDictionary.getFieldItem(temps[1]);
								 if(item!=null&&!"0".equals(item.getCodesetid())) {
                                     value=AdminCode.getCodeName(item.getCodesetid(),replaceValue);
                                 } else {
                                     value=replaceValue;
                                 }
							 }
							 else {
                                 value="";
                             }
						}
						else {
                            value="";
                        }
					}
					else {
                        value="";
                    }
					
				}
				str.append("<td align='left'  >&nbsp;&nbsp;"+value+"&nbsp;&nbsp;</td>");//bug 35701 内容和标题没有对齐。
			}
			str.append("</tr>");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * 检查是否为html文本（匹配html中的一些元素）
     * @param content
     * @return
     */
    private boolean isHTML(String content) {
        String curContext = content.toLowerCase();
        return null != curContext 
              && !"".equals(curContext)
              && (
                  curContext.contains("<html")
               || curContext.contains("<meta")
               || curContext.contains("<body")
               || curContext.contains("<p")
               || curContext.contains("<h")
               || curContext.contains("<div")
               || curContext.contains("<table")
               || curContext.contains("<td")
               || curContext.contains("<tr")
              );
    }
	

	public static void main(String[] args)
	{
		try
		{
		//	System.out.println(PubFunc.convertTo64Base("余鸿林").replaceAll("\\+", "df"));
		/*
			String urlParam="=+ /?%#&";
			urlParam=urlParam.replaceAll("%", "%25");
			urlParam=urlParam.replaceAll("\\+", "%2B");
			   urlParam=urlParam.replaceAll(" ", "%20");
			   urlParam=urlParam.replaceAll("\\/", "%2F");
			   urlParam=urlParam.replaceAll("\\?", "%3F");
			 
			   urlParam=urlParam.replaceAll("#", "%23");
			   urlParam=urlParam.replaceAll("&", "%26");
			   urlParam=urlParam.replaceAll("=", "%3D");
			System.out.println(urlParam);
			
			*/
			
			/*
			
			String context="集团总部财务部会计赵钳同志,您好:您的信息变化如下:<table>学历性质:<<a04:c0401:current>>学历:<<a04:a0405:current>>毕业时间:<<a04:a0430:current>></table>日期:#1:业务日期#";
		 int from_index=-1;
		 int to_index=-1;
		 if(context.indexOf("<table>")!=-1)
				from_index=context.indexOf("<table>");
		 if(context.indexOf("</table>")!=-1)
				to_index=context.indexOf("</table>");
		 
		 System.out.println(context.substring(0,from_index));
		 System.out.println(context.substring(to_index+8));
		 */
		String aa=PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base("dc,"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getIns_id() {
		return ins_id;
	}

	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getTemplate_emailAddress() {
		return template_emailAddress;
	}

	public void setTemplate_emailAddress(String template_emailAddress) {
		this.template_emailAddress = template_emailAddress;
	}

	public String getTemplate__set() {
		return template__set;
	}

	public void setTemplate__set(String template__set) {
		this.template__set = template__set;
	}

	public boolean isSmsContext() {
		return isSmsContext;
	}

	public void setSmsContext(boolean isSmsContext) {
		this.isSmsContext = isSmsContext;
	}

	public String getSendresource() {
		return sendresource;
	}

	public void setSendresource(String sendresource) {
		this.sendresource = sendresource;
	}

	 

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}
	//兼容老方法调用，新方法会多传递一个参数
    public LazyDynaBean getTileAndContent(String opt,String title,String context,String a0100,UserView userView,String tabid,String obj_sql, ArrayList seqnumList, int infor_type) {
    	return this.getTileAndContent(opt, title, context, a0100, userView, tabid, obj_sql, seqnumList, infor_type,"");
    }
    /**
     * @param j 
     * @param arrayList 
     * @param sql 
     * @param tabid 
     * @param userView2 
     * @param string2 
     * @param context 
     * @param string  
    * @Title: getTileAndContent 
    * @Description: TODO获取邮件中要使用的信息,主要是为了修改title和content
    * @param @param bean
    * @param @param objsSql
    * @param @return    设定文件 
    * @return LazyDynaBean    返回类型 
    * @throws 
    */ 
    
    
    public LazyDynaBean getTileAndContent(String opt,String title,String context,String a0100,UserView userView,String tabid,String obj_sql, ArrayList seqnumList, int infor_type,String ydNodeWarnJob) {

        LazyDynaBean abean=new LazyDynaBean();
        try
        {
            abean.set("subject",title);
            String oriConText = PubFunc.keyWord_reback(context);
            String oriTitle = title;
            int k=title.indexOf("(");
        	if (k>0){
        		title=title.substring(0, k);
        	}
            context = PubFunc.keyWord_reback(context);
            ContentDAO dao=new ContentDAO(this.con);
            String taskContainUsername="";
            int i=0;
            RowSet rowSet  = null;
            if(!"2".equals(opt)){//如果不是驳回的信息,那么obj_sql,有相关的查询数据
                if(StringUtils.isNotEmpty(obj_sql)) {
                    rowSet = dao.search(obj_sql);
                    while(rowSet.next()){
                        if(i>0){
                            i++;
                            break;
                        }else{
                            if(infor_type==1){
                                taskContainUsername=rowSet.getString("a0101_1");
                            }else if(infor_type==2||infor_type==3){
                                String codeitemdesc_1 = rowSet.getString("codeitemdesc_1");
                                taskContainUsername=codeitemdesc_1;
                            }
                           
                        }
                        i++;
                    }
                }
                /*
                oriTitle = "";
                oriTitle = oriTitle+taskContainUsername;
                if(i>1){
                    oriTitle = oriTitle+"等的"+title;
                }else{
                    oriTitle = oriTitle+"的"+title;
                } 
                */
            }else{//如果是驳回的数据,需要从seqnumlist里面取得seqnum,并且查询出相关人员的信息
                int size = seqnumList.size();
                String seqnum = (String)seqnumList.get(0); //此时获得的seqnum=seqnum：审批人 例如： 52e6775enull5082d6ef:gwj ，应将：后面的去掉  liuzy 20150707.
                if(seqnum.contains(":"))
                {
                   int m=seqnum.indexOf(":");
                   seqnum=seqnum.substring(0, m);
                }
                String sql = "";
                if(infor_type==1){
                    sql = "select a0101_1 from templet_"+tabid+" where seqnum = '"+seqnum+"'";
                }else if(infor_type==2||infor_type==3){
                    sql = "select codeitemdesc_1 from templet_"+tabid+" where seqnum = '"+seqnum+"'";
                }
                 
                rowSet = dao.search(sql);
                if(rowSet.next()){
                    taskContainUsername = rowSet.getString(1);
                }
                oriTitle = "";
                oriTitle = oriTitle+taskContainUsername;
                if(size>1){
                    oriTitle = oriTitle+"等的"+title+"("+ResourceFactory.getProperty("info.appleal.state2")+")";
                }else{
                    oriTitle = oriTitle+"的"+title+"("+ResourceFactory.getProperty("info.appleal.state2")+")";
                }
            }
            
            LazyDynaBean _abean=getUserNamePassword(a0100);
            String username="";
            String password="";
            String a0101="";
            if(_abean!=null)
            {
            	username=(String)_abean.get("username");
            
            	password=(String)_abean.get("password"); 
            
            	a0101=(String)_abean.get("a0101");
	            if(a0101==null||a0101.trim().length()==0){
	                a0101=username;
	            }
            }
            oriConText = "";
            oriConText = oriConText+a0101+",您好<br/>";
            oriConText = oriConText+"&nbsp;&nbsp;&nbsp;&nbsp;";
            oriConText = oriConText+taskContainUsername;
            if(!"2".equals(opt)){
                if(i>1){//非驳回数据人员多于一个
                    oriConText = oriConText+"等的"+title;
                }else{
                    oriConText = oriConText+"的"+title;
                }
            }
            
            if("2".equals(opt)){//驳回数据,人员信息的处理
                int size = seqnumList.size();
                if(size>1){
                    oriConText = oriConText+"等的"+title;
                }else{
                    oriConText = oriConText+"的"+title;
                }
                String userFullName = userView.getUserFullName();
                if(userFullName==null||userFullName.trim().length()==0){
                    userFullName = userView.getUserName();
                }
                oriConText = oriConText+"已经被"+userFullName+"驳回,";
                oriConText = oriConText+"请查阅单据详情。"+ydNodeWarnJob+"<br />";
                if(context.trim().length()>0&&!title.equals(context)){
                    oriConText = oriConText+"驳回原因:";
                    oriConText = oriConText+context.substring((title+"("+ResourceFactory.getProperty("info.appleal.state2")+")").length());
                }
                
            }else{ 
             // 1:报批  2：驳回  3：批准  4:重新分配
                if("3".equals(opt)){
                    oriConText = oriConText+"已经被批准,";
                }else if("2".equals(opt)){
                	oriConText = oriConText+"已经被驳回,";
                }else{
                	if("3".equals(sp_flag)){
                		oriConText = oriConText+"已经报备,";
                	}else{
                		oriConText = oriConText+"已经提交,";
                	}
                }
                
                if("61".equals(businessModel)|| "71".equals(businessModel)||"3".equals(opt)||"3".equals(sp_flag)){
                    oriConText = oriConText+"请查阅单据详情。"+ydNodeWarnJob+"<br/>";
                }else{
                    oriConText = oriConText+"请查阅单据详情并审批。"+ydNodeWarnJob+"<br/>";
                }
            }
            
            String _businessModel="";
            if("61".equals(businessModel)|| "71".equals(businessModel)) {
                _businessModel="&businessModel="+businessModel;
            } else {
                _businessModel="&businessModel=0";
            }
            String pendingCode="HRMS-"+task_id; 
            
          //在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
            int tab_id=Integer.parseInt(tabid);
            TemplateUtilBo tb=new TemplateUtilBo(this.con,userView);
            String view = tb.getTemplateView(tab_id);
            String url="";
        	if(view!=null&& "list".equalsIgnoreCase(view)){
        		url=userView.getServerurl() +"/general/template/templatelist.do?b_init=init"+_businessModel+"&isInitData=1&pre_pendingID="+pendingCode+"&validatepwd=false&sp_flag="+sp_flag+"&ins_id="+ins_id+"&returnflag=12&task_id="+task_id+"&tabid="+tabid+"&index_template=1&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
        	}else {
        		url=userView.getServerurl() +"/general/template/edit_form.do?b_query=link"+_businessModel+"&tabid="+tabid+"&pre_pendingID="+pendingCode+"&validatepwd=false&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(task_id)+"&sp_flag="+sp_flag+"&returnflag=12&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
        	}
        	
           // String url=userView.getServerurl() +"/general/template/edit_form.do?b_query=link"+_businessModel+"&tabid="+tabid+"&pre_pendingID="+pendingCode+"&validatepwd=false&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(task_id)+"&sp_flag="+sp_flag+"&returnflag=12&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)));
             
            if (PubFunc.isUseNewPrograme(this.userview)){
                String approve_flag="1";
                if ("3".equals(sp_flag)){
                    approve_flag="3"; 
                }
                //liuyz bug26155 begin
                if ("2".equals(sp_flag)){
                    approve_flag="2"; 
                }
                //liuyz bug26155 end 
                if ("0".equals(sp_flag)){
                    approve_flag="0"; 
                }
                String newUrl ="/module/template/templatemain/templatemain.html?b_query=link&ins_id="+ins_id+"&task_id="
                    +PubFunc.encrypt(task_id)+"&tab_id="+tabid+"&return_flag=13"
                    +"&approve_flag="+approve_flag
                    +"&pre_pendingID="+pendingCode;
                url=userView.getServerurl()+"/module/utils/jsp.do?br_query=link&param="
                    +SafeCode.encode(newUrl)  
                    +"&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password)))+"&sp_flag="+this.sp_flag; 
            }
            if(task_id==null||task_id.trim().length()==0) {
                url="";
            }
            abean.set("subject", oriTitle);
            abean.set("href", url);
            abean.set("bodyText",oriConText);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return abean;
    
    }

	public HashMap getDestination_a0100() {
		return destination_a0100;
	}

	public void setDestination_a0100(HashMap destination_a0100) {
		this.destination_a0100 = destination_a0100;
	}
	
/*
	public String getIsSub() {
		return isSub;
	}

	public void setIsSub(String isSub) {
		this.isSub = isSub;
	}
	*/
	
}
