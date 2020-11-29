package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class InterviewEvaluatingBo {
	private Connection conn=null;
	private boolean  addProfessionalColumnName=false;
	private ArrayList removeItemList=new ArrayList();//由于高级
	public InterviewEvaluatingBo(Connection conn)
	{
		this.conn=conn;
	}
	
	
	/**
	 * 生成 状态 html
	 * @param z0501
	 * @param state
	 * @return
	 */
	public String getStateHtml(String z0501,String state,UserView view)
	{
		StringBuffer html=new StringBuffer("<select name='"+z0501+"' onchange='setState(this)'");
		if(!view.hasTheFunction("3103103")&&!view.hasTheFunction("0A0101")) {
            html.append(" disabled");
        }
		ArrayList itemList=AdminCode.getCodeItemList("36");
		html.append(" > \n");
		//2014.11.4 xxd 根据库中状态获取状态值添加到select标签中
		for(int i=0;i<itemList.size();i++){
			CodeItem codeItem = (CodeItem)itemList.get(i);//获取当前代码类对象
			String codeitemid = codeItem.getCodeitem();//获取当前对象代码id
			String pcodeitem = codeItem.getPcodeitem();//获取当前对象父代码id
			String codeitemdesc = codeItem.getCodename();//获取当前对象代码值
			
			if(pcodeitem!=null && "2".equals(pcodeitem) && !"2".equals(codeitemid)){
				html.append("<option value='"+codeitemid+"' ");
				if(state.equals(codeitemid)) {
                    html.append("selected");
                }
				html.append(" >"+codeitemdesc+"</option> \n");

			}
		}
		//老版本将数据直接固定写死到select当中
//		html.append("<option value='21' ");//待通知 
//		if(state.equals("21"))
//			html.append("selected");
//		html.append(" >"+ResourceFactory.getProperty("hire.interviewEvaluating.dnotice")+"</option> \n");//待通知
//		html.append("<option value='22' ");//已通知
//		if(state.equals("22"))
//			html.append("selected");
//		html.append(" >"+ResourceFactory.getProperty("hire.interviewEvaluating.ynotice")+"</option> \n");//已通知
//		html.append("<option value='23' ");//未联系上
//		if(state.equals("23"))
//			html.append("selected");
//		html.append(" >"+ResourceFactory.getProperty("hire.interviewEvaluating.nonotice")+"</option> \n");	//联系不上
//		html.append("<option value='24' ");
//		if(state.equals("24"))
//			html.append(" selected ");
//		html.append(">"+ResourceFactory.getProperty("hire.interviewEvaluating.hadwork")+"</option>\n");//已有工作
//		html.append("<option value='25' ");
//		if(state.equals("25"))
//			html.append(" selected ");
//		html.append(">"+ResourceFactory.getProperty("hire.interviewEvaluating.giveup")+"</option>\n");//个人放弃
//		html.append("</select>");
		return html.toString();
	}
	
	
	/**
	 * 前台表列集合 
	 * @param fielditemList
	 * @return
	 */
	public ArrayList getColumnList(ArrayList fielditemList,String ss_email,String ss_phone,String dbname)
	{
		ArrayList list=new ArrayList();
		try
		{
			LazyDynaBean lazyDynaBean=new LazyDynaBean();
			lazyDynaBean.set("itemid","z0501_html");
			lazyDynaBean.set("itemtype","A");
			lazyDynaBean.set("codesetid","0");
			lazyDynaBean.set("itemdesc","");  
			list.add(lazyDynaBean);
					
			LazyDynaBean lazyDynaBean1=new LazyDynaBean();
			lazyDynaBean1.set("itemid","a0101");
			lazyDynaBean1.set("itemtype","A");
			lazyDynaBean1.set("codesetid","0");
			lazyDynaBean1.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.name"));
			lazyDynaBean1.set("fieldsetid",dbname+"A01");
			list.add(lazyDynaBean1);
			
			
	
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
			HashMap map=bo2.getAttributeValues();
			if(map!=null&&map.get("resume_state")!=null) {
                resume_state_field=(String)map.get("resume_state");
            }
			
			LazyDynaBean lazyDynaBean2=new LazyDynaBean();
			lazyDynaBean2.set("itemid",resume_state_field);
			lazyDynaBean2.set("itemtype","A");
			lazyDynaBean2.set("codesetid","36");
			lazyDynaBean2.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.resumeState"));
			lazyDynaBean2.set("fieldsetid",dbname+"A01");
			list.add(lazyDynaBean2);
			
			LazyDynaBean lazyDynaBean3_0=new LazyDynaBean();
			lazyDynaBean3_0.set("itemid","unit");
			lazyDynaBean3_0.set("itemtype","A");
			lazyDynaBean3_0.set("codesetid","UN");
			lazyDynaBean3_0.set("itemdesc",ResourceFactory.getProperty("hire.interviewExamine.interviewUnit"));
			lazyDynaBean3_0.set("fieldsetid","org3");
			list.add(lazyDynaBean3_0);
			LazyDynaBean lazyDynaBean3_1=new LazyDynaBean();
			lazyDynaBean3_1.set("itemid","departid");
			lazyDynaBean3_1.set("itemtype","A");
			lazyDynaBean3_1.set("codesetid","UM");
			lazyDynaBean3_1.set("itemdesc",ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment"));
			lazyDynaBean3_1.set("fieldsetid","org2");
			list.add(lazyDynaBean3_1);
			
			
			LazyDynaBean lazyDynaBean3=new LazyDynaBean();
			lazyDynaBean3.set("itemid","codeitemdesc");
			lazyDynaBean3.set("itemtype","A");
			lazyDynaBean3.set("codesetid","@K");
			String schoolPosition = "";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0) {
                schoolPosition=(String)map.get("schoolPosition");
            }
			
			if(schoolPosition!=null&&schoolPosition.trim().length()>0) {
                lazyDynaBean3.set("itemdesc",ResourceFactory.getProperty("hire.apply.majorposition"));
            } else {
                lazyDynaBean3.set("itemdesc",ResourceFactory.getProperty("hire.apply.position"));
            }
			lazyDynaBean3.set("fieldsetid","org");
			list.add(lazyDynaBean3);
					
			LazyDynaBean lazyDynaBean0=new LazyDynaBean();
			lazyDynaBean0.set("itemid","state");
			lazyDynaBean0.set("itemtype","A");
			lazyDynaBean0.set("codesetid","36");
			lazyDynaBean0.set("itemdesc",ResourceFactory.getProperty("hire.interviewEvaluating.isNotice")); 
			lazyDynaBean0.set("fieldsetid","Z05");
			list.add(lazyDynaBean0);
			
			for(int i=0;i<fielditemList.size();i++)
			{
				FieldItem item = (FieldItem) fielditemList.get(i);
				if(!"a0100".equalsIgnoreCase(item.getItemid())&&!"z0501".equalsIgnoreCase(item.getItemid())&&!"z0513".equalsIgnoreCase(item.getItemid())&&!"state".equalsIgnoreCase(item.getItemid()))
				{
				    if ("Z0515".equalsIgnoreCase(item.getItemid())&&!item.isVisible()) {
                        continue;
                    }
					LazyDynaBean lazyDynaBean4=new LazyDynaBean();
					lazyDynaBean4.set("itemid",item.getItemid());
					lazyDynaBean4.set("itemtype",item.getItemtype());
					lazyDynaBean4.set("codesetid",item.getCodesetid());
					lazyDynaBean4.set("itemdesc",item.getItemdesc());
					lazyDynaBean4.set("fieldsetid",item.getFieldsetid());
					list.add(lazyDynaBean4);
				}	
			}		
			if(!"#".equals(ss_email))
			{  
				LazyDynaBean lazyDynaBean7 = new LazyDynaBean();
			    lazyDynaBean7.set("itemid","sendMail");
			    lazyDynaBean7.set("itemtype","A");
			    lazyDynaBean7.set("codesetid","0");
			    lazyDynaBean7.set("itemdesc",ResourceFactory.getProperty("selfservice.param.otherparam.email_send"));
			    list.add(lazyDynaBean7);
				LazyDynaBean lazyDynaBean5=new LazyDynaBean();
				lazyDynaBean5.set("itemid",ss_email);
				lazyDynaBean5.set("itemtype","A");
				lazyDynaBean5.set("codesetid","0");
				lazyDynaBean5.set("itemdesc",ResourceFactory.getProperty("selfservice.param.otherparam.email_title"));
				list.add(lazyDynaBean5);
				
				
			}
			if(!"#".equals(ss_phone))
			{
				LazyDynaBean lazyDynaBean6=new LazyDynaBean();
				lazyDynaBean6.set("itemid",ss_phone);
				lazyDynaBean6.set("itemtype","A");
				lazyDynaBean6.set("codesetid","0");
				lazyDynaBean6.set("itemdesc",ResourceFactory.getProperty("selfservice.param.otherparam.phone_title"));
				list.add(lazyDynaBean6);
			}
			LazyDynaBean lazyDynaBean6=new LazyDynaBean();
			lazyDynaBean6.set("itemid","kgemail");
			lazyDynaBean6.set("itemtype","A");
			lazyDynaBean6.set("codesetid","0");
			lazyDynaBean6.set("itemdesc","发送消息（考官）");
			list.add(lazyDynaBean6);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	

	
	public ArrayList getFieldList(ArrayList beanList)
	{
		ArrayList list=new ArrayList();
		
		for(int i=0;i<beanList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)beanList.get(i);
			String itemid=(String)abean.get("itemid");
			String itemtype=(String)abean.get("itemtype");
			String codesetid=(String)abean.get("codesetid");
			String desc=(String)abean.get("itemdesc");
			
			if("codeitemdesc".equalsIgnoreCase(itemid)|| "unit".equalsIgnoreCase(itemid)|| "departid".equalsIgnoreCase(itemid)) {
                codesetid="0";
            }
			if("z0501_html".equalsIgnoreCase(itemid)) {
                continue;
            }
			
			FieldItem fieldItem1=new FieldItem();
			fieldItem1.setItemid(itemid);
			fieldItem1.setItemtype(itemtype);
			fieldItem1.setCodesetid(codesetid);
			fieldItem1.setItemdesc(desc);
			list.add(fieldItem1);

		}
		
		
		return list;
	}
	
	
	
	public HashMap getDbList()
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select pre from dbname";
			ContentDAO dao= new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("pre").toUpperCase(),rs.getString("pre"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getName(String pre,String a0100)
	{
		String name="";
		try
		{
			String sql = " select a0101 from "+pre+"a01 where a0100='"+a0100+"'";
			ContentDAO dao= new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				name=rs.getString("a0101")==null?"":rs.getString("a0101");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * 得到人员面试安排信息列表
	 * @param codeid  组织范围id
	 * @param dbName  应用库前缀
	 * @param model   5:面试安排  6：面试通知
	 * @return
	 */
	public ArrayList getInterviewArrangeInfoList(String codeid,String dbName,String ss_email,String ss_phone,ArrayList fielditemList,String extendWhereSql,String orderSql,String model,UserView view)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		HashMap nameMap=getEmployerNameMap("Usr");
		HashMap dbMap = this.getDbList();
		String sql=getInterviewArrangeInfoSQL(codeid,dbName,ss_email,ss_phone,fielditemList,extendWhereSql,orderSql,1,view);
		try
		{
			String resume_state_field = "";
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map = bo2.getAttributeValues();
			if (map != null && map.get("resume_state") != null) {
                resume_state_field = (String) map.get("resume_state");
            }
			rowSet=dao.search(sql);
			String a_z0505="";
			String a_z0507="";
			String state="";
			int num=0;
			while(rowSet.next())
			{
				LazyDynaBean lazyDynaBean=new LazyDynaBean();	
				if(rowSet.getString("a0101")==null)
				{
					lazyDynaBean.set("a0101","");
				}
				else {
                    lazyDynaBean.set("a0101",rowSet.getString("a0101"));
                }
				String a_state=rowSet.getString(resume_state_field);
				lazyDynaBean.set(resume_state_field,AdminCode.getCodeName("36",a_state));
				if(rowSet.getString("unit")!=null) {
                    lazyDynaBean.set("unit",rowSet.getString("unit"));
                } else {
                    lazyDynaBean.set("unit","");
                }
				if(rowSet.getString("departid")!=null) {
                    lazyDynaBean.set("departid",rowSet.getString("departid"));
                } else {
                    lazyDynaBean.set("departid","");
                }
				if(rowSet.getString("codeitemdesc")!=null) {
                    lazyDynaBean.set("codeitemdesc",rowSet.getString("codeitemdesc"));
                } else {
                    lazyDynaBean.set("codeitemdesc","");
                }
				lazyDynaBean.set("z0501_html","&nbsp;<input type='checkbox' name='id' value='"+PubFunc.encrypt(rowSet.getString("z0501"))+"' />&nbsp;");
				//lazyDynaBean.set("z0501_html",String.valueOf(++num));
				for(int i=0;i<fielditemList.size();i++)
				{
					FieldItem item = (FieldItem) fielditemList.get(i);
					if("state".equalsIgnoreCase(item.getItemid()))
					{
						if("5".equals(model))
						{
							lazyDynaBean.set("state",getStateHtml(rowSet.getString("z0501"),rowSet.getString("state"),view));
						}
						else if("6".equals(model)) {
                            lazyDynaBean.set("state",AdminCode.getCodeName("36",rowSet.getString("state")));
                        }
					}
					else if("z0505".equalsIgnoreCase(item.getItemid())|| "z0507".equalsIgnoreCase(item.getItemid()))
					{
						StringBuffer z0505=new StringBuffer();
						StringBuffer z0507=new StringBuffer();
						a_z0505=rowSet.getString("z0505");
						a_z0507=rowSet.getString("z0507");
						if(a_z0505!=null&&a_z0505.trim().length()>0)
						{
							String[] aa_z0505=a_z0505.split(",");
							int a=0;
							while(a<aa_z0505.length)
							{
								String tt="";
								if(aa_z0505[a].length()<11) {
                                    tt="USR";
                                }
								String tempname=(String)nameMap.get(tt+aa_z0505[a].toUpperCase());
                                if(tempname==null){
                                    tempname=""; 
                                }
								z0505.append(","+tempname);
								a++;
							}
						}
						if(a_z0507!=null&&a_z0507.trim().length()>0)
						{
							String[] aa_z0507=a_z0507.split(",");
							int a=0;
							while(a<aa_z0507.length)
							{
								String tt="";
								if(aa_z0507[a].length()<11) {
                                    tt="USR";
                                }
								String tempname=(String)nameMap.get(tt+aa_z0507[a].toUpperCase());
								if(tempname==null){
								    tempname=""; 
								}
								z0507.append(","+tempname);
								a++;
							}
						}		
						
						String z0505_text="";
						if("5".equals(model)) {
                            z0505_text="<input type='text' class='TEXT_NB common_border_color' readonly size='20' onclick='selectEmployer(this)'  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/z0505") + "'  value='"+(z0505.length()>0?z0505.substring(1):"")+"' />";
                        } else if("6".equals(model)) {
                            z0505_text=z0505.length()>0?z0505.substring(1):"";
                        }
							
						String z0507_text="";
						if("5".equals(model)) {
                            z0507_text="<input type='text' class='TEXT_NB common_border_color'  readonly size='20' onclick='selectEmployer(this)'   name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/z0507") + "'  value='"+(z0507.length()>0?z0507.substring(1):"")+"' />";
                        } else if("6".equals(model)) {
                            z0507_text=z0507.length()>0?z0507.substring(1):"";
                        }
							
						lazyDynaBean.set("z0505",z0505_text);
						lazyDynaBean.set("z0507",z0507_text);
					}
					else if("Z0511".equalsIgnoreCase(item.getItemid()))
					{
						String context="";
						if(rowSet.getString(item.getItemid())!=null)
						{
							String tt=rowSet.getString(item.getItemid());
							
							if((tt.indexOf("`")!=-1)&&(tt.length()>3))
							{ 
								String dbname=tt.substring(0,3);
								if(dbMap.containsKey(dbname.toUpperCase()))
								{
								   
									context = this.getName(tt.substring(0,3).toUpperCase(),tt.substring(4));
								}
								else
								{
									context=tt;
								}
							}
							else
							{
								context=tt;
							}
						}
						lazyDynaBean.set(item.getItemid(),context);
					}else if(this.removeItemList.contains(item.getItemid().toLowerCase())){
						int decwidth=item.getDecimalwidth();
						if(decwidth>0){
							Number value=(Number) rowSet.getObject(item.getItemid());
							if(value==null||value.doubleValue()==0){
								lazyDynaBean.set(item.getItemid(),"");
							}else{
								lazyDynaBean.set(item.getItemid(),rowSet.getDouble(item.getItemid())+"");
							}
							
						}else{
							int value=rowSet.getInt(item.getItemid());
							if(value==0){
								lazyDynaBean.set(item.getItemid(),"");
							}else{
								lazyDynaBean.set(item.getItemid(),rowSet.getInt(item.getItemid())+"");
							}
						}
					}
					else
					{
						String context=getContext(rowSet,item,model);
						lazyDynaBean.set(item.getItemid(),(context==null|| "null".equalsIgnoreCase(context))?"":context);
					}
				}
				if(!"#".equals(ss_email))
				{
					try
					{
						if(rowSet.getString(ss_email)!=null&&!"".equals(rowSet.getString(ss_email))&&(view.hasTheFunction("3103101")||view.hasTheFunction("3103201")||view.hasTheFunction("0A0102"))){
							lazyDynaBean.set("sendMail","<img src='/images/edit.gif' style='cursor:hand' onclick=\"sendMail('"+PubFunc.encrypt(rowSet.getString("Z0501"))+"','"+PubFunc.encrypt(rowSet.getString("a0100"))+"','"+PubFunc.encrypt(rowSet.getString("zp_pos_id"))+"')\" >");
							
							lazyDynaBean.set(ss_email,"<a href='mailto:"+rowSet.getString(ss_email)+"'>"+rowSet.getString(ss_email)+"</a>");
						}else{
							lazyDynaBean.set("sendMail","");
							if(rowSet.getString(ss_email)!=null&&!"".equals(rowSet.getString(ss_email))) {
                                lazyDynaBean.set(ss_email, rowSet.getString(ss_email));
                            } else {
                                lazyDynaBean.set(ss_email,"");
                            }
							
						}
					}
					catch(Exception ee)
					{
						throw GeneralExceptionHandler.Handle(new Exception("邮箱地址指标不存在"));
					}
				}
				if(!"#".equals(ss_phone))
				{
					try
					{
						if(rowSet.getString(ss_phone)!=null) {
                            lazyDynaBean.set(ss_phone,rowSet.getString(ss_phone));
                        } else {
                            lazyDynaBean.set(ss_phone,"");
                        }
					}
					catch(Exception ee)
					{
						throw GeneralExceptionHandler.Handle(new Exception("邮箱地址指标不存在"));
					}
				}
				if(view.hasTheFunction("3103102")||view.hasTheFunction("3103202")||view.hasTheFunction("0A0103")) {
                    lazyDynaBean.set("kgemail", "<img src='/images/edit.gif' style='cursor:hand' onclick=\"sendMailKG('"+PubFunc.encrypt(rowSet.getString("Z0501"))+"','"+PubFunc.encrypt(rowSet.getString("a0100"))+"','"+PubFunc.encrypt(rowSet.getString("zp_pos_id"))+"')\" >");
                } else {
                    lazyDynaBean.set("kgemail", "");
                }
				list.add(lazyDynaBean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	public String getContext(RowSet rowSet,FieldItem item,String model)
	{
		String context="";
		try
		{
				String value="";
				if("A".equalsIgnoreCase(item.getItemtype()))
				{
					if(rowSet.getString(item.getItemid())!=null) {
                        value=rowSet.getString(item.getItemid());
                    }
					if("5".equals(model))
					{
						if("0".equals(item.getCodesetid())) {
                            context="<input type='text' size='20' class='TEXT_NB common_border_color' onchange=\"save('A',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"'  value='"+value+"' />";
                        } else
						{
							context="<input type='text' size='20' class='TEXT_NB common_border_color' onblur=\"saveC('"+PubFunc.encrypt(item.getItemid())+"','"+PubFunc.encrypt(rowSet.getString("z0501"))+"',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+".viewvalue'  value='"+AdminCode.getCodeName(item.getCodesetid(), value)+"'";
							context+=" onclick='openInputCodeDialog(\""+item.getCodesetid()+"\",\""+rowSet.getString("z0501")+"/"+item.getItemid()+".viewvalue\");'/>";
							//context+="<input type='hidden' name='"+rowSet.getString("z0501")+"/"+item.getItemid()+".viewvalue' onchange='changeValue(this,\""+rowSet.getString("z0501")+"/"+item.getItemid()+"\");'/>";
							context+="<input type='hidden' value='' id='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"' name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid()+".value")+"'/>";
						}
					
					}else if("6".equals(model))
					{
						if(item.isCode()) {
                            context=AdminCode.getCodeName(item.getCodesetid(), value);
                        } else {
                            context=value;
                        }
					}
				}
				else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					//java.sql.Date--->java.sql.Timestamp
					//new java.sql.Timestamp(yourDate.getTime());

					//java.sql.Timestamp-->java.sql.Date
					//new java.sql.Date(yourTimestamp.getTime());
					 
				       
				    
					Object obj=rowSet.getObject(item.getItemid());
					if(obj!=null&& obj instanceof Timestamp){
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式
						Timestamp now =(Timestamp)obj;
						value = df.format(now);
					}else{
					if(rowSet.getString(item.getItemid())!=null) {
                        value=rowSet.getString(item.getItemid());
                    }
					}
					
					if("5".equals(model)) {
                        context="<input type='text' class='TEXT_NB common_border_color'  size='20' onchange=\"save('D',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,true)'  value='"+((value==null|| "null".equalsIgnoreCase(value))?"":value)+"' />";
                    } else if("6".equals(model)) {
                        context=value;
                    }
				}
				else if("N".equalsIgnoreCase(item.getItemtype()))
				{
					if(rowSet.getString(item.getItemid())!=null) {
                        value=rowSet.getString(item.getItemid());
                    }
					if("5".equals(model))
					{
						if(item.getDecimalwidth()>0) {
                            context="<input type='text' class='TEXT_NB common_border_color' size='10' onchange=\"save('N',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"'    value='"+value+"' />";
                        } else
						{
							context="<input type='text' class='TEXT_NB common_border_color' size='10' onchange=\"save('N0',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"'    value='"+value+"' />";
						}
					}
					else if("6".equals(model))
					{
						context=value;
					}
				}
				else if("M".equalsIgnoreCase(item.getItemtype()))
				{
					value=Sql_switcher.readMemo(rowSet,item.getItemid());
					if("5".equals(model)) {
                        context="<input type='text' size='20' class='TEXT_NB common_border_color' onchange=\"save('A',this)\"  name='"+PubFunc.encrypt(rowSet.getString("z0501")+"/"+item.getItemid())+"'  value='"+value+"' />";
                    } else if("6".equals(model)) {
                        context=value;
                    }
				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return context;
	}
	public ArrayList getCodeValueList()
	{
		ArrayList list = new ArrayList();
		try
		{
			ParameterXMLBo bo = new ParameterXMLBo(this.conn, "1");
			HashMap map = bo.getAttributeValues();
			String interview_itemid="";
			if(map!=null&&map.get("interviewing_itemid")!=null)
			{
				interview_itemid=(String)map.get("interviewing_itemid");
			}
			else
			{
				throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置面试回复指标！"));
			}
			String sql=" select codeitemdesc,codeitemid from codeitem where codesetid=(select codesetid from fielditem where UPPER(itemid)='"+interview_itemid.toUpperCase()+"')";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			list.add(new CommonData("-1","全部"));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getInterviewRevertInfoList(String codeid,String dbName,String extendWhereSql,String orderSql,String value,String start_date,String end_date) throws GeneralException
	{
		HashMap list = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			String resume_state_field = "";
			String interview_itemid="";
			ParameterXMLBo bo = new ParameterXMLBo(this.conn, "1");
			HashMap map = bo.getAttributeValues();
			String hireMajor="";
			if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			}
			if (map != null && map.get("resume_state") != null) {
                resume_state_field = (String) map.get("resume_state");
            }
			if(map!=null&&map.get("interviewing_itemid")!=null)
			{
				interview_itemid=(String)map.get("interviewing_itemid");
			}
			else
			{
				throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置面试回复指标！"));
			}
			if("#".equals(interview_itemid)) {
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置面试回复指标！"));
            }
			sql.append("select "+dbName+"a01.a0101,"+dbName+"a01."+resume_state_field+" state,"+dbName+"a01."+interview_itemid+" interview,org3.codeitemdesc unit,org2.codeitemdesc  departid,");
			if(hireMajoritem!=null)
			{
				if(hireMajoritem.isCode())
    			{
					sql.append("case when z03.Z0336='01'  then  code10.codeitemdesc else org.codeitemdesc end as codeitemdesc");
    			}
    			else
    			{
    				sql.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else org.codeitemdesc end as codeitemdesc");
    			}
			}else{
				sql.append("org.codeitemdesc as codeitemdesc");
			}
			
			String cloumns="a0101,state,interview,unit,departid,codeitemdesc";
			StringBuffer sql_whl = new StringBuffer("");
			sql_whl.append(" from "+dbName+"a01 left join z05 on z05.a0100="+dbName+"a01.a0100 ");
			sql_whl.append(" left join zp_pos_tache zp on "+dbName+"a01.a0100=zp.a0100 ");
			sql_whl.append(" left join z03 on zp.zp_pos_id=z03.z0301 ");
			sql_whl.append(" left join organization org on z03.z0311=org.codeitemid ");
			sql_whl.append(" left join organization org2 on org.parentid=org2.codeitemid ");
			sql_whl.append(" left join organization org3 on z03.z0321=org3.codeitemid ");
			if(hireMajoritem!=null&&hireMajoritem.isCode())
			{
				sql_whl.append(" left join (select codeitemid,codeitemdesc from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"') code10 ");
				sql_whl.append(" on z03."+hireMajoritem.getItemid()+"=code10.codeitemid");
			}
			sql_whl.append(" where (" + dbName + "A01." + resume_state_field
					+ "='31' or " + dbName + "A01." + resume_state_field
					+ "='32' or " + dbName + "A01." + resume_state_field
					+ "='33') ");
	        if(!"-1".equals(value))
	        {
	        	sql_whl.append(" and "+ dbName+"a01."+interview_itemid+"='"+value+"'");
	        }
			if (!"0".equals(codeid))
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				for(int i=0;i<temps.length;i++)
				{ 
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
					tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				}
			//	sql_whl.append(" and ("+tempSql.substring(3)+") ");
				sql_whl.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				
			}
			sql_whl.append(" and zp.resume_flag='12' ");
			sql_whl.append(" and z03.z0319='04' ");
			if (extendWhereSql.trim().length() > 0) {
                sql_whl.append(" and (" + extendWhereSql + ")");
            }
			if(start_date!=null&&start_date.trim().length()>0&&end_date!=null&&end_date.trim().length()>0)
			{
				sql_whl.append(" and (");
				String itemid="z0509";
				/*sql.append(" and (("+Sql_switcher.year(itemid)+">"+xx.substring(0,4)+") or ("+Sql_switcher.year(itemid)+"="+xx.substring(0,4));
			    sql.append(" and "+Sql_switcher.month(itemid)+">"+xx.substring(5, 7)+") or (");
				sql.append(Sql_switcher.year(itemid)+"="+xx.substring(0,4)+" and "+Sql_switcher.month(itemid)+"="+xx.substring(5, 7)+" and ");
				sql.append(Sql_switcher.day(itemid)+">="+xx.substring(8,10)+"))");			*/
				sql_whl.append(" (("+Sql_switcher.year(itemid)+">"+start_date.substring(0,4)+") or ("+Sql_switcher.year(itemid)+"="+start_date.substring(0,4));
				sql_whl.append(" and "+Sql_switcher.month(itemid)+">"+start_date.substring(5, 7)+") or (");
			    sql_whl.append(Sql_switcher.year(itemid)+"="+start_date.substring(0,4)+" and "+Sql_switcher.month(itemid)+"="+start_date.substring(5, 7)+" and ");
				sql_whl.append(Sql_switcher.day(itemid)+">="+start_date.substring(8,10)+"))");
				sql_whl.append(" and ");
				sql_whl.append(" (("+Sql_switcher.year(itemid)+"<"+end_date.substring(0,4)+") or ("+Sql_switcher.year(itemid)+"="+end_date.substring(0,4));
				sql_whl.append(" and "+Sql_switcher.month(itemid)+"<"+end_date.substring(5, 7)+") or (");
			    sql_whl.append(Sql_switcher.year(itemid)+"="+end_date.substring(0,4)+" and "+Sql_switcher.month(itemid)+"="+end_date.substring(5, 7)+" and ");
				sql_whl.append(Sql_switcher.day(itemid)+"<="+end_date.substring(8,10)+"))");
				sql_whl.append(")");
			}
			/*if (orderSql.trim().length() > 0)
				sql_whl.append(orderSql);
			sql.append(sql_whl);*/
			ContentDAO dao = new ContentDAO(this.conn);
			String codesetid="";
			RowSet rs1 = dao.search(" select codesetid from fielditem where UPPER(itemid)='"+interview_itemid.toUpperCase()+"'");
			while(rs1.next())
			{
				codesetid=rs1.getString("codesetid");
			}
			list.put("select",sql.toString());
			list.put("where",sql_whl.toString());
			list.put("cloumns", cloumns);
			list.put("codesetid",codesetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @param codeid		 组织范围id
	 * @param dbName		 库前缀
	 * @param ss_email		 email指标
	 * @param ss_phone       移动电话指标
	 * @param fielditemList  
	 * @param extendWhereSql 附加的查询条件
	 * @param orderSql		 排列循序
	 * @return
	 */
	public String getInterviewArrangeInfoSQL(String codeid,String dbName,String ss_email,String ss_phone,ArrayList fielditemList,String extendWhereSql,String orderSql,int type,UserView view)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql = new StringBuffer("");
		try {

			String resume_state_field = "";
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map = bo2.getAttributeValues();
			if (map != null && map.get("resume_state") != null) {
                resume_state_field = (String) map.get("resume_state");
            }
			
			String hireMajor="";
			if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                    hireMajorIsCode=true;
                }
			}
			
			
			
			StringBuffer sql_select = new StringBuffer(
					"select zp_pos_id,"+ dbName+ "A01.a0101,"+dbName+"A01.a0100,"+ dbName+ "A01."+resume_state_field+" ,org3.codeitemdesc unit,org2.codeitemdesc  departid,");
			if(hireMajor.length()==0) {
                sql_select.append("org.codeitemdesc");
            } else
			{
				if(hireMajorIsCode)
    			{
					sql_select.append("case when z03.Z0336='01'  then  code10.codeitemdesc else org.codeitemdesc end as codeitemdesc");
    			}
    			else
    			{
    				sql_select.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else org.codeitemdesc end as codeitemdesc");
    			}
			}
			
			sql_select.append(", z0511");
			StringBuffer sql_from = new StringBuffer(" from Z05  left join "
					+ dbName + "A01  on Z05.a0100=" + dbName + "A01.a0100 ");
			/**联系人。，既通知的应聘者来面试的那个人，z0511直接存的是用户名，不用关联a01表了*/
			//sql_from.append(" left join (select a0100,a0101 from UsrA01) aa01 on Z05.z0511=aa01.a0100 ");
			sql_from.append(" left join zp_pos_tache zp on Z05.a0100=zp.a0100");
			sql_from.append(" left join z03 on zp.zp_pos_id=z03.z0301");
			sql_from.append(" left join z01 on z03.z0101=z01.z0101");
			sql_from.append(" left join organization org on z03.z0311=org.codeitemid ");
			sql_from.append(" left join organization org2 on z03.z0325=org2.codeitemid  "); // dengcan org.parentid=org2.codeitemid ");
			sql_from.append(" left join organization org3 on z03.z0321=org3.codeitemid ");
			if(hireMajorIsCode) {
                sql_from.append(" left join  (select * from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"') code10 on z03."+hireMajor+"=code10.codeitemid ");
            }
			StringBuffer sql_whl = new StringBuffer("");

			for (int i = 0; i < fielditemList.size(); i++) {
				FieldItem item = (FieldItem) fielditemList.get(i);
				if ("z0511".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
				if("z0509".equalsIgnoreCase(item.getItemid()))
				{
					/**页面展现时间精确到分钟*/
					if(type==1)
					{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
		    	    		sql_select.append(","+Sql_switcher.dateToChar("z05.z0509", "yyyy-mm-dd hh24:mi")+" as z0509");
		    			}
		    			else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    			{
						//convert(varchar(16),z05.z0509,20) as z0509
		    				sql_select.append(",convert(varchar(16),z05.z0509,20) as z0509");
		    			}
		    			else
		    			{
		    				sql_select.append(",Z05." + item.getItemid());
		     			}
					}/**导出excel，时间暂时正常显示*/
					else
					{
						sql_select.append(",Z05." + item.getItemid());
					}
					
				}
				else
				{
					sql_select.append(",Z05." + item.getItemid());
				}
			}
			rowSet = dao.search("select * from fielditem where itemid='"
					+ ss_email + "' or itemid='" + ss_phone + "'");
			StringBuffer existTableName = new StringBuffer(",A01");
			while (rowSet.next()) {
				String itemid = rowSet.getString("itemid");
				String fieldsetid = rowSet.getString("fieldsetid");
				sql_select.append("," + dbName + fieldsetid + "." + itemid);
				if (existTableName.indexOf(fieldsetid) == -1) {
					String tempName = dbName + fieldsetid;
					sql_from.append(",");
					sql_from.append("(SELECT * FROM ");
					sql_from.append(tempName);
					sql_from.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
					sql_from.append(tempName);
					sql_from.append(" B WHERE ");
					sql_from.append(" A.A0100=B.A0100  )) ");
					sql_from.append(tempName);

					sql_whl.append(" and ");
					sql_whl.append(dbName);
					sql_whl.append("A01.a0100=");
					sql_whl.append(tempName);
					sql_whl.append(".a0100");

					existTableName.append("," + fieldsetid);
				}
			}

			sql.append(sql_select.toString());
			/********zzk sql库  面试安排、面试通知按时间倒序排列  null排前面*******/
			if (Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql.append(", case when z05.z0509 is null  then '9999-12-31 14:21:38.000' else z05.z0509 end as Z0509A ");
            }
			sql.append(sql_from.toString());
			sql_whl.append(" where (" + dbName + "A01." + resume_state_field
					+ "='31' or " + dbName + "A01." + resume_state_field
					+ "='32' or " + dbName + "A01." + resume_state_field
					+ "='33') ");
			if (!"0".equals(codeid))
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				for(int i=0;i<temps.length;i++)
				{
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
					tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				}
			//	sql_whl.append(" and ("+tempSql.substring(3)+") ");
				sql_whl.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				
				//sql_whl.append(" and ( z03.Z0311 like '"+codeid+"%'  or ( ( z03.z0303 is not null and z03.z0303 like '"+codeid+"%') or ( z03.z0305 is not null and z03.z0305 like '"+codeid+"%')    ) )  ");
				//sql_whl.append(" and z03.z0311 like '" + codeid + "%' ");
			}
			sql_whl.append(" and zp.resume_flag='12' ");
			sql_whl.append(" and z03.z0319='04' ");
			/*String active_field="";
			if(map!=null&&map.get("active_field")!=null&&!((String)map.get("active_field")).equals(""))
			{
				active_field=(String)map.get("active_field");
			}
			if(active_field!=null&&!active_field.trim().equals(""))
			{
				sql_whl.append(" and "+dbName+"a01."+active_field+"='1'");
				if(!(view.isSuper_admin()||view.getGroupId().equals("1")))
				{
					String org=view.getUserOrgId();
					if(org==null||org.equals(""))
						sql_whl.append(" and 1=2 ");
					else
						sql_whl.append("and "+dbName+"a01.b0110 not like '"+org+"%'");
				}
			}*/
			if (extendWhereSql.trim().length() > 0) {
                sql_whl.append(" and (" + extendWhereSql + ")");
            }
			if (orderSql.trim().length() > 0) {
                sql_whl.append(orderSql);
            }
			sql.append(sql_whl.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();
	}
	
	
	
	
	
	/**
	 * 取得 面试考官 a0100对应名字的map
	 * @return
	 */
	public HashMap getEmployerNameMap(String dbName)
	{
		HashMap map=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer ids=new StringBuffer("");
		
			String a_z0505="";
			String a_z0507="";
			HashSet idSet=new HashSet();
			HashMap amap = new HashMap();
			rowSet=dao.search("select pre from dbname");
        	HashMap tmap = new HashMap();
        	while(rowSet.next())
        	{
        		tmap.put(rowSet.getString(1).toUpperCase(), "1");
        	}
        	rowSet=dao.search("select z0505,z0507 from z05 ");
			while(rowSet.next())
			{
				a_z0505=rowSet.getString("z0505");
				a_z0507=rowSet.getString("z0507");
				int i=0;
				if(a_z0505!=null&&a_z0505.indexOf(",")!=-1)
				{
					String[] aa_z0505=a_z0505.split(",");
					for(int index = 0;index < aa_z0505.length;index++)
					{
						if(aa_z0505[index]==null|| "".equals(aa_z0505[index])) {
                            continue;
                        }
						if(tmap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
						{
							if(amap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
							{
								String t= (String)amap.get(aa_z0505[index].substring(0,3).toUpperCase());
								t+=","+aa_z0505[index].substring(3);
								amap.put(aa_z0505[index].substring(0,3).toUpperCase(), t);
							}
							else
							{
								amap.put(aa_z0505[index].substring(0,3).toUpperCase(), ","+aa_z0505[index].substring(3));
							}
						}
						else
						{
							String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
							t+=","+aa_z0505[index];
							amap.put("USR", t);
						}
					}
				}
				if(a_z0507!=null&&a_z0507.indexOf(",")!=-1)
				{
					String[] aa_z0507=a_z0507.split(",");
					for(int index = 0;index < aa_z0507.length;index++)
					{
						if(aa_z0507[index]==null|| "".equals(aa_z0507[index])) {
                            continue;
                        }
						if(tmap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
						{
							if(amap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
							{
								String t= (String)amap.get(aa_z0507[index].substring(0,3).toUpperCase());
								t+=","+aa_z0507[index].substring(3);
								amap.put(aa_z0507[index].substring(0,3).toUpperCase(), t);
							}
							else
							{
								amap.put(aa_z0507[index].substring(0,3).toUpperCase(), ","+aa_z0507[index].substring(3));
							}
						}
						else
						{
							String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
							t+=","+aa_z0507[index];
							amap.put("USR", t);
						}
					}
				}
			}
			Set keySet = amap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key = (String)t.next();
				String a0100=(String)amap.get(key);
				if(a0100!=null&&a0100.length()>0)
				{
					a0100="'"+a0100.substring(1).replaceAll(",", "','")+"'";
					String a[]=a0100.split(",");
					StringBuffer tem=new StringBuffer();
					for(int i=0;i<a.length;i++){
						tem.append("or a0100=");
						tem.append(a[i]);
					}
					String sql="select a0100,a0101 from "+key+"A01 where " +tem.toString().substring(2);
					//System.out.print(sql);
					//rowSet=dao.search("select a0100,a0101 from "+key+"A01 where a0100 in ("+a0100+")");//dml 2011年11月30日10:27:32 in中最多放 1000个值 上海泰为 出现问题
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						map.put(key.toUpperCase()+rowSet.getString("a0100"),rowSet.getString("a0101"));
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 改变人员简历状态，将已选人员 改为 待通知状态,并往面试安排信息表中插入相应的纪录。
	 *@param codeid 组织权限范围
	 *@param dbname 库前缀
	 */
	public void changeState(String codeid,String dbname,String resume_state_field)
	{		
		ContentDAO dao = new ContentDAO(this.conn);		
		try
		{
			String sql2="select distinct "+dbname+"A01.a0100 from "+dbname+"A01,zp_pos_tache zp,z03 where "+dbname+"A01.a0100=zp.a0100 and zp.zp_pos_id=z03.z0301  and "+dbname+"A01."+resume_state_field+"='12'  and z03.z0319='04'  and zp.resume_flag='12' ";
			if(codeid!=null&&codeid.trim().length()>0)
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				for(int i=0;i<temps.length;i++)
				{ 
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
					tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				}
			//	sql2+=" and ("+tempSql.substring(3)+") ";
				sql2+=" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ";
				
			}
			sql2+=" and "+dbname+"A01.a0100 not in (select a0100 from z05)";
			RowSet rowSet=dao.search(sql2);
			ArrayList recordList=new ArrayList();	
			while(rowSet.next())
			{
				 RecordVo vo=new RecordVo("Z05");	
				 IDGenerator idg=new IDGenerator(2,this.conn);
				 String id=idg.getId("Z05.Z0501");		
				 vo.setString("z0501",id);
				 vo.setString("a0100",rowSet.getString("a0100"));
				 vo.setDate("z0515",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				 vo.setString("state","21");
				 recordList.add(vo);
			}
			dao.addValueObject(recordList);
			StringBuffer sql=new StringBuffer("update "+dbname+"A01 set "+resume_state_field+"='31' where a0100 in ");
			sql.append("( select distinct zp.a0100 from "+dbname+"A01,zp_pos_tache zp,z03  where "+dbname+"A01.a0100=zp.a0100 and zp.zp_pos_id=z03.z0301");
			sql.append(" and "+dbname+"A01."+resume_state_field+"='12' and zp.resume_flag='12' ");
			if(codeid!=null&&codeid.trim().length()>0)
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				
				for(int i=0;i<temps.length;i++)
				{
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
					tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				}
			//  	sql2+=" and ("+tempSql.substring(3)+") ";
				
			  	sql.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				
				
			//	sql.append(" and z03.z0311 like '"+codeid+"%' ");
			}
			sql.append(" )");
			dao.update(sql.toString());		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 保存人员面试安排信息表中的内容
	 * @param Z0501      id号
	 * @param columnName 列名
	 * @param a_value    值
	 * @param type       数据类型
	 */
	public void saveInterviewArrangeInfo(String Z0501,String columnName,String a_value,String type)
	{
		ContentDAO dao = new ContentDAO(this.conn);	
		try
		{
			
    			RecordVo vo=new RecordVo("z05");
    			vo.setString("z0501",Z0501);
    			vo=dao.findByPrimaryKey(vo);
    			if("A".equals(type))
    			{
    				vo.setString(columnName.toLowerCase(),a_value);
    			}
	    		else if("D".equals(type))
	    		{
	    			if(a_value!=null&&a_value.trim().length()>0){
	    				java.util.Date dd = null;
	    				if(a_value.length()==19){
	    					dd = DateUtils.getDate(a_value,"yyyy-MM-dd HH:mm:ss");
	    				}else{	    					
	    					dd = DateUtils.getDate(a_value,"yyyy-MM-dd HH:mm");
	    				}
	    				vo.setDate(columnName.toLowerCase(),dd);
	    			}else{
	    				vo.setDate(columnName.toLowerCase(),"");
	    			}
    			}
    			else if("N".equals(type))
    			{
    				if(a_value!=null&&a_value.trim().length()>0){
    					vo.setDouble(columnName.toLowerCase(),Double.parseDouble(a_value));
	    			}else{
	    				vo.setDate(columnName.toLowerCase(),"");
	    			}
	    			
    			}
     			else if("N0".equals(type))
	    		{
     				if(a_value!=null&&a_value.trim().length()>0){
     					vo.setInt(columnName.toLowerCase(),Integer.parseInt(a_value));
	    			}else{
	    				vo.setDate(columnName.toLowerCase(),"");
	    			}
    				
    			}
    			dao.updateValueObject(vo);

    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @param date 2006-11-15 14:29:40
	 * @return
	 */
	public java.util.Date getDate(String date)
	{
		Calendar d=Calendar.getInstance();		
		d.set(Calendar.YEAR,Integer.parseInt(date.substring(0,4)));
		d.set(Calendar.MONDAY,Integer.parseInt(date.substring(5,7))-1);
		d.set(Calendar.DATE,Integer.parseInt(date.substring(8,10)));
		if(date.length()>11)
		{
			d.set(Calendar.HOUR_OF_DAY,Integer.parseInt(date.substring(11,13)));
			d.set(Calendar.MINUTE,Integer.parseInt(date.substring(14,16)));
			d.set(Calendar.SECOND,Integer.parseInt(date.substring(17)));
		}
		return d.getTime();
	}
	
	

	public static void main(String[] arg)
	{
		String date="2006-11-15 14:29:40";
		System.out.println(date.substring(0,4));
		System.out.println(date.substring(5,7));
		System.out.println(date.substring(8,10));
		System.out.println(date.substring(11,13));
		System.out.println(date.substring(14,16));
		System.out.println(date.substring(17));
		
	}
	
	
	
	/**
	 * 取得邮箱 和 移动电话对应的指标
	 * @return 邮箱/移动电话  
	 */
	public String getEmail_PhoneField()
	{
		String a_field="#";
		String b_field="#";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			rowSet=dao.search("select * from constant where constant='SS_EMAIL'");
			while(rowSet.next())
			{
				a_field=(Sql_switcher.readMemo(rowSet,"str_value")==null|| "".equals(Sql_switcher.readMemo(rowSet,"str_value"))?"#":Sql_switcher.readMemo(rowSet,"str_value"));
			}
			rowSet = dao.search("select * from constant where constant='SS_MOBILE_PHONE'");
			while(rowSet.next())
			{
				b_field=(Sql_switcher.readMemo(rowSet,"str_value")==null|| "".equals(Sql_switcher.readMemo(rowSet,"str_value"))?"#":Sql_switcher.readMemo(rowSet,"str_value"));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return a_field+"/"+b_field;
	}
	public static  boolean isMail(String email){

		    boolean retval = false;
		    String emailPattern ="^([a-z0-9A-Z]+[_]*[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		    if(email==null){
		    	email="";
		    }
		  
		    return  retval = email.matches(emailPattern);
		  
	}
	public String getDbpre_str(UserView view,String dbname)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			ArrayList list = new ArrayList();
			ArrayList dblist=view.getPrivDbList();	
			DbNameBo dbbo=new DbNameBo(conn);				
			ArrayList logdblist=dbbo.getAllLoginDbNameList();
			StringBuffer strlog=new StringBuffer();
			for(int i=0;i<logdblist.size();i++)
			{
				RecordVo vo=(RecordVo)logdblist.get(i);
				strlog.append(vo.getString("pre"));
				strlog.append(",");
			}
			String str_db=strlog.toString().toUpperCase();
			for(int j=0;j<dblist.size();j++)
			{
				String dbpre=(String)dblist.get(j);
				if(str_db.indexOf(dbpre.toUpperCase())==-1) {
                    continue;
                }
				list.add(dbpre);
			}
			for(int i=0;i<list.size();i++)
			{
				String dbpre=(String)list.get(i);
				if(dbname.equalsIgnoreCase(dbpre)) {
                    continue;
                }
				buf.append(dbpre+",");
			}
			if(buf.toString().length()>0) {
                buf.setLength(buf.length()-1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}


	public boolean isAddProfessionalColumnName() {
		return addProfessionalColumnName;
	}


	public void setAddProfessionalColumnName(boolean addProfessionalColumnName) {
		this.addProfessionalColumnName = addProfessionalColumnName;
	}
	public ArrayList getRemoveItemList() {
		return removeItemList;
	}


	public void setRemoveItemList(ArrayList removeItemList) {
		this.removeItemList = removeItemList;
	}

	
}
