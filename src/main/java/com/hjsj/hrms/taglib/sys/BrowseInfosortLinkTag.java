package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class BrowseInfosortLinkTag extends BodyTagSupport {

	private String tag;
	private String name;
	private List infoSetList=new ArrayList();
	private String a0100;
	private String nbase;
	private String setprv;
	private String returnvalue;
	private String userpriv;
	public String getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getSetprv() {
		return setprv;
	}
	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public List getInfoSetList() {
		return infoSetList;
	}
	public void setInfoSetList(List infoSetList) {
		this.infoSetList = infoSetList;
	}
	private String type;//0，信息录入，1，信息浏览,2自助平台的信息维护
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String a00_url="";
	private String a01_url="";
	private String axx_url="";
	public int doEndTag() throws JspException 
	{
		Connection conn=null;
		
		try{
			conn=AdminDb.getConnection();
			SaveInfo_paramXml infoxml = new SaveInfo_paramXml(conn);		
			String tagorder = infoxml.getInfo_param("order");	
			
			if(tagorder!=null&&tagorder.length()>0)
			{
				/**
				 * 处理新构库子集默认放在分类配置子集最后
				 */
				ArrayList set_A_list=infoxml.getSet_A_Name$Text();
				ArrayList fenlei_list=infoxml.getFenLeiDesc(set_A_list);
				ArrayList fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
				String strs=fenlei_list.toString().replace("[", ",").replace("]", ",").replaceAll(", ", ",")+tagorder+",";
				StringBuffer tmp=new StringBuffer();
				for(int i=0;i<fielditemlist.size();i++){
					FieldSet fs = (FieldSet)fielditemlist.get(i);
					if(strs.indexOf(","+fs.getCustomdesc()+",")==-1)
							tmp.append(","+fs.getCustomdesc());
				}
				if(tmp.length()>0)
					tagorder+=tmp.toString();
				viewInfosortLinkForOrder(infoxml,tagorder);
			}else
			{
				viewInfosortLinkNoOrder(infoxml);
			}

			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return SKIP_BODY;	
	}
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * 排序显示子集连接
	 * @param infoxml
	 * @param tagorder
	 * @throws Exception
	 */
	private void viewInfosortLinkForOrder(SaveInfo_paramXml infoxml,String tagorder)throws Exception
	{	
		Connection conns = null;
		try
		{
			conns=AdminDb.getConnection();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conns);
			//是否需要审核，1为需要，0为不需要
			String approveflag=sysoth.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			//是否直接入库，如果1：不直接入库；0为直接入库
			String inputchinfor=sysoth.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
			//System.out.println(returnvalue);
			List setlist=new ArrayList();
			String[] basesort_Arr=tagorder.split(",");
			ArrayList basesort_list=infoxml.getView_tag("SET_A");//分类
			String classname="settext";
			for(int n=0;n<basesort_Arr.length;n++)
			{
				name=basesort_Arr[n];						
				if(name==null||name.length()<=0)
					continue;			
				String setname=infoxml.getView_value("SET_A", name);
				if(setname!=null&&setname.length()>0)
				{
					setlist=infoxml.getInfoSortFieldSet(infoSetList,setname,true);
					if(setlist==null||setlist.size()<=0)
						continue;
					pageContext.getOut().println("<tr>");
				    pageContext.getOut().println("<td width='100%' align='left' nowrap>");
					pageContext.getOut().println("<table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' >");
					pageContext.getOut().println("<tr>  ");                 
					pageContext.getOut().println("<td align='left' width='10' nowrap>");
					pageContext.getOut().println("</td>");
					pageContext.getOut().println("<td valign=\"top\" align='left' nowrap>");
					pageContext.getOut().println("<table border='0' cellspacing='0' cellpadding='0'><tr>");
//					pageContext.getOut().println("<td width='8'></td> ");
					pageContext.getOut().println("<td align='left' nowrap><a href='#id"+n+"#' onclick=\"showsub('sub"+n+"','arrow"+n+"');showDiv(this);\">"+name+"</a></td>");
					pageContext.getOut().println("<td><span id=arrow"+n+"><img src=\"/images/darrow.gif\" border=0></span></td>");
					pageContext.getOut().println("</tr></table>");
					pageContext.getOut().println("</td>");             
					pageContext.getOut().println("</tr>");
					pageContext.getOut().println("</table>");
					pageContext.getOut().println("</td>");
					pageContext.getOut().println("</tr>");
					pageContext.getOut().println("<tr>");
				    pageContext.getOut().println("<td align='left' nowrap>");
				    pageContext.getOut().println("<div id='sub"+n+"' style='display:none;' scroll='AUTO'>");
					pageContext.getOut().println("<table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' >");				
					if(setlist!=null&&setlist.size()>0)
					{
						String fieldsetid="";
						for(int i=0;i<setlist.size();i++)
						{
							FieldSet fieldset=(FieldSet)setlist.get(i);						
							fieldsetid=fieldset.getFieldsetid();
							classname="settext";						
							if(type!=null&& "0".equals(type))
							{
								if(fieldset.getPriv_status()==1)
								{
									this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
									this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
								}else if(fieldset.getPriv_status()==2)
								{
									this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+this.setprv+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
									this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
								}
								
							}else if(type!=null&& "2".equals(type))
							{
								if(fieldset.getPriv_status()==1)
								{
									classname="LinkRead";
								}
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else if(type!=null&& "11".equals(type))
							{
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else if(type!=null&& "self".equals(type))//自助我的信息维护
							{
								if(fieldset.getPriv_status()==1)
								{
									classname="LinkRead";
								}
								
								this.a01_url="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								if ("1".equals(inputchinfor) && "1".equals(approveflag) &&fieldset.getPriv_status()==2) {
									this.a00_url = "/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv=2&flag=self&returnvalue=3&isUserEmploy=0&button=0";
								} else {
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=infoself&returnvalue=3&isUserEmploy=0";
								}
								this.axx_url="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
							}else
							{
								if(this.returnvalue!=null&& "11".equals(this.returnvalue))
								{
									this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
									this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
								}else
								{
									boolean flag = false;
									if ("1".equals(inputchinfor) && "1".equals(approveflag)
											&&fieldset.getPriv_status()==2) {
										flag = true;
									}
									if ("1".equals(this.returnvalue) && flag) {
										this.a00_url="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue=3&isUserEmploy=0&button=1&userbase="+this.nbase;
									} else {
										this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00";
									}
									
									this.a01_url="/workbench/browse/browseinfo.do?b_search=link&setname="+fieldsetid;
									this.axx_url="/workbench/browse/showselfinfodetail.do?b_search=link&setname="+fieldsetid;
								}
								
							}
							pageContext.getOut().println("<tr>");                                  
							pageContext.getOut().println("<td>");
							pageContext.getOut().println("<table border='0' cellspacing='0' cellpadding='0'>");  
							pageContext.getOut().println("<tr>"); 
							pageContext.getOut().println("<td width='15' nowrap>");
							pageContext.getOut().println("</td> ");
							pageContext.getOut().println("<td align='left' width='8' nowrap>");	               
			                pageContext.getOut().println("</td>");
			                pageContext.getOut().println("<td nowrap> ");
			                pageContext.getOut().println("<font class='settext'>");
			                if("A00".equals(fieldset.getFieldsetid()))
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+" onclick=\"winhref('"+this.a00_url+"','mil_body');showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }else if("A01".equals(fieldset.getFieldsetid()))
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref("+this.a01_url+",mil_body);showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }else
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.axx_url+"','mil_body');showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }
			                pageContext.getOut().println("</font>");
			                pageContext.getOut().println("</td>   ");
			                pageContext.getOut().println("</tr>");
			                pageContext.getOut().println("</table>");
			                pageContext.getOut().println("</td>   ");             
			                pageContext.getOut().println("</tr> ");
						}					
						pageContext.getOut().println("</table>");
						pageContext.getOut().println("</div>");
						pageContext.getOut().println("</td>");
						pageContext.getOut().println("</tr>");
					}
				}else
				{
					
					setlist=getInfoSortFieldSet(infoSetList,name,false);
					if(setlist!=null&&setlist.size()>0)
					{
						String fieldsetid="";
						for(int i=0;i<setlist.size();i++)
						{
							FieldSet fieldset=(FieldSet)setlist.get(i);
							fieldsetid=fieldset.getFieldsetid();
							classname="settext";
							if(type!=null&& "0".equals(type))
							{
								if(fieldset.getPriv_status()==1)
								{
									this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
									this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
								}else if(fieldset.getPriv_status()==2)
								{
									this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+this.setprv+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
									this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
								}
							}else if(type!=null&& "2".equals(type))
							{
								if(fieldset.getPriv_status()==1)
								{
									classname="LinkRead";
								}
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else if(type!=null&& "11".equals(type))
							{
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else if(type!=null&& "self".equals(type))//自助我的信息维护
							{
								if(fieldset.getPriv_status()==1)
								   classname="LinkRead";
								/**
								 * 当需要审核且不直接入库的时，跳转到多媒体的报批页面searchmediainfolist.jsp
								 */
								
								this.a01_url="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								
								if ("1".equals(inputchinfor) && "1".equals(approveflag) &&fieldset.getPriv_status()==2) {
									this.a00_url = "/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv=2&flag=self&returnvalue=3&isUserEmploy=0&button=0";
								} else {
									this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=infoself&returnvalue=3&isUserEmploy=0";
								}
								this.axx_url="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
							}else
							{
								if(this.returnvalue!=null&& "11".equals(this.returnvalue))
								{
									this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
									this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
									this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
								}else
								{
									boolean flag = false;
									if ("1".equals(inputchinfor) && "1".equals(approveflag)
											&&fieldset.getPriv_status()==2) {
										flag = true;
									}
									if ("1".equals(this.returnvalue) && flag) {
										this.a00_url="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue=3&isUserEmploy=0&button=1&userbase="+this.nbase;
									} else {
										this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00";
									}
									this.a01_url="/workbench/browse/browseinfo.do?b_search=link&setname="+fieldsetid;
									this.axx_url="/workbench/browse/showselfinfodetail.do?b_search=link&setname="+fieldsetid;
								}
								
							}
							pageContext.getOut().println("<tr>");                                  
							pageContext.getOut().println("<td>");
							pageContext.getOut().println("<table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' background=''>");  
							pageContext.getOut().println("<tr>"); 							
							pageContext.getOut().println("<td align='left' width='10' nowrap>");							
			                pageContext.getOut().println("</td>");
			                pageContext.getOut().println("<td nowrap align='left'>");
			                pageContext.getOut().println("<font class='settext'>");
			                if("A00".equals(fieldset.getFieldsetid()))
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.a00_url+"','mil_body',this);showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }else if("A01".equals(fieldset.getFieldsetid()))
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref("+this.a01_url+",mil_body,this);showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }else
			                {
			                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.axx_url+"','mil_body',this);showDiv(this);\">");
			                	pageContext.getOut().println("<font class='"+classname+"'>") ;
			                	pageContext.getOut().println(fieldset.getCustomdesc());
			                	pageContext.getOut().println("</font></a>");
			                }
			                pageContext.getOut().println("</font>");
			                pageContext.getOut().println("</td>   ");
			                pageContext.getOut().println("</tr>");
			                pageContext.getOut().println("</table>");
			                pageContext.getOut().println("</td>   ");             
			                pageContext.getOut().println("</tr> ");
						}
					}
				}
			}
		}catch(Exception e)
		{
			
		}finally
		{
			if (conns != null) {
				conns.close();
			}
		}
		
		
	}
	/**
	 * 不排序的显示子集排序
	 * @param infoxml
	 * @throws Exception
	 */
	private void viewInfosortLinkNoOrder(SaveInfo_paramXml infoxml)throws Exception
	{
		StringBuffer setSub=new StringBuffer();
		List setlist=new ArrayList();
		ArrayList basesort_list=infoxml.getView_tag("SET_A");//分类
		if(basesort_list!=null&&basesort_list.size()>0)
			basesort_list.add(0,"未分类子集");  
		String classname="settext";
		for(int n=0;n<basesort_list.size();n++)
		{
			name=(String)basesort_list.get(n);
			if(name!=null&& "未分类子集".equals(name))
			{
				if(basesort_list!=null&&basesort_list.size()>0)
				{
					
					for(int i=0;i<basesort_list.size();i++)
					{
						setSub.append(infoxml.getView_value("SET_A",(String)basesort_list.get(i) ));
					}						
					setlist=infoxml.getInfoSortFieldSet(infoSetList,setSub.toString(),false);
				}
				if(setlist!=null&&setlist.size()>0)
				{
					String fieldsetid="";
					for(int i=0;i<setlist.size();i++)
					{
						FieldSet fieldset=(FieldSet)setlist.get(i);
						fieldsetid=fieldset.getFieldsetid();
						classname="settext";
						if(type!=null&& "0".equals(type))
						{
							if(fieldset.getPriv_status()==1)
							{
								this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&returnvalue=2&userbase="+this.nbase;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&setprv="+fieldset.getPriv_status()+"&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
							}else if(fieldset.getPriv_status()==2)
							{
								this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+this.setprv+"&flag=notself&returnvalue=3&userbase="+this.nbase;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
							}
						}else if(type!=null&& "2".equals(type))
						{
							if(fieldset.getPriv_status()==1)
							{
								classname="LinkRead";
							}
							this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue=2&userbase="+this.nbase;
							this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
							this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
						}else if(type!=null&& "11".equals(type))
						{
							this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							//this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue=111";
							this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
							this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
						}else if(type!=null&& "self".equals(type))//自助我的信息维护
						{
							if(fieldset.getPriv_status()==1)
								   classname="LinkRead";
							this.a01_url="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue=3&isUserEmploy=0";
							this.axx_url="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
						}else
						{
							if(this.returnvalue!=null&& "11".equals(this.returnvalue))
							{
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue=111";
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else
							{
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00";
								this.a01_url="/workbench/browse/browseinfo.do?b_search=link&setname="+fieldsetid;
								this.axx_url="/workbench/browse/showselfinfodetail.do?b_search=link&setname="+fieldsetid;
							}
							
						}
						pageContext.getOut().println("<tr>");                                  
						pageContext.getOut().println("<td>");
						pageContext.getOut().println("<table background='/images/back1.jpg'>");  
						pageContext.getOut().println("<tr>"); 							
						pageContext.getOut().println("<td align='left' nowrap>");							
		                pageContext.getOut().println("</td>");
		                pageContext.getOut().println("<td nowrap> ");
		                pageContext.getOut().println("<font class='"+classname+"'>");
		                              //  <a href="/workbench/browse/browseinfo.do?b_search=link&a0100=${browseForm.a0100}&setname=a01&infosortflag=<%=ii%>" target="mil_body" onclick="showDiv(this);"><font styleClass="settext"> <bean:write name="setlist"/></font></a>
		                if("A00".equals(fieldset.getFieldsetid()))
		                {
		                	pageContext.getOut().println("&nbsp; <a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.a00_url+"','mil_body');showDiv(this);\">");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }else if("A01".equals(fieldset.getFieldsetid()))
		                {
		                	pageContext.getOut().println("&nbsp; <a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref("+this.a01_url+",mil_body);showDiv(this);\">");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }else
		                {
		                	pageContext.getOut().println("&nbsp; <a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.axx_url+"','mil_body');showDiv(this);\">");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }
		                pageContext.getOut().println("</font>");
		                pageContext.getOut().println("</td>   ");
		                pageContext.getOut().println("</tr>");
		                pageContext.getOut().println("</table>");
		                pageContext.getOut().println("</td>   ");             
		                pageContext.getOut().println("</tr> ");
					}					
					/*pageContext.getOut().println("</table>");						
					pageContext.getOut().println("</td>");
					pageContext.getOut().println("</tr>");*/
				}
			}else
			{
				String setname=infoxml.getView_value("SET_A", name);
				setlist=infoxml.getInfoSortFieldSet(infoSetList,setname,true);
				if(setlist==null||setlist.size()<=0)
					continue;
				pageContext.getOut().println("<tr>");
			    pageContext.getOut().println("<td align='left' nowrap>");
				pageContext.getOut().println("<table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' background='/images/back1.jpg'>   ");
				pageContext.getOut().println("<tr>  ");                 
				pageContext.getOut().println("<td align='left' nowrap>");
								
				pageContext.getOut().println("</td>");
				pageContext.getOut().println("<td align='absmiddle'> ");
				//pageContext.getOut().println("&nbsp;  <a href='#id"+n+"#' onclick=\"showsub('sub"+n+"','arrow"+n+"');showDiv(this);\">"+name+"<span id=arrow"+n+"><img src=\"/images/darrow.gif\" border=0></span></a>");
				pageContext.getOut().println("<table  border='0' cellspacing='0' cellpadding='0'><tr>");
				pageContext.getOut().println("<td>&nbsp;  <a href='#id"+n+"#' onclick=\"showsub('sub"+n+"','arrow"+n+"');showDiv(this);\">"+name+"</a></td>");
				pageContext.getOut().println("<td><span id=arrow"+n+"><img src=\"/images/darrow.gif\" border=0></span></td>");
				pageContext.getOut().println("</tr></table>");
				pageContext.getOut().println("</td>");    
				pageContext.getOut().println("</tr>");
				pageContext.getOut().println("</table>");
				pageContext.getOut().println("</td>");
				pageContext.getOut().println("</tr>");
				pageContext.getOut().println("<tr>");
			    pageContext.getOut().println("<td align='left' nowrap>");
			    pageContext.getOut().println("<div id='sub"+n+"' style='display:none;' scroll='AUTO'>");
				pageContext.getOut().println("<table width='100%' border='0' cellspacing='1' align='center' cellpadding='1' background='/images/back1.jpg'>   ");				
				if(setlist!=null&&setlist.size()>0)
				{
					String fieldsetid="";
					for(int i=0;i<setlist.size();i++)
					{
						FieldSet fieldset=(FieldSet)setlist.get(i);
						fieldsetid=fieldset.getFieldsetid();
						classname="settext";
						if(type!=null&& "0".equals(type))
						{
							if(fieldset.getPriv_status()==1)
							{
								this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&returnvalue=2&userbase="+this.nbase;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&flag=notself&setprv="+fieldset.getPriv_status()+"&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
							}else if(fieldset.getPriv_status()==2)
							{
								this.a01_url="/workbench/info/addinfo/add.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+this.setprv+"&flag=notself&returnvalue=3&userbase="+this.nbase;
								this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
								this.axx_url="/workbench/info/addinfo/add.do?b_searchdetail=search&setname="+fieldsetid+"&flag=noself";
							}
						}else if(type!=null&& "2".equals(type))
						{
							if(fieldset.getPriv_status()==1)
							{
								classname="LinkRead";
							}
							this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							//this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue=2&userbase="+this.nbase;
							this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv=2&flag=notself&returnvalue="+returnvalue+"&userbase="+this.nbase;
							this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
						}else if(type!=null&& "11".equals(type))
						{
							this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							//this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue=111";
							this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
							this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
						}else if(type!=null&& "self".equals(type))//自助我的信息维护
						{
							if(fieldset.getPriv_status()==1)
								   classname="LinkRead";
							this.a01_url="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
							this.a00_url="/workbench/media/searchmediainfolist.do?b_search=link&setname="+fieldsetid+"&setprv="+fieldset.getPriv_status()+"&flag=notself&returnvalue=3&isUserEmploy=0";
							this.axx_url="/selfservice/selfinfo/searchselfdetailinfo.do?b_search=search&setname="+fieldsetid+"&flag=infoself";
						}else
						{
							if(this.returnvalue!=null&& "11".equals(this.returnvalue))
							{
								this.a01_url="/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname="+fieldsetid;
								//this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue=111";
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&returnvalue="+returnvalue;
								this.axx_url="/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+fieldsetid+"&flag=noself";
							}else
							{
								this.a00_url="/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00";
								this.a01_url="/workbench/browse/browseinfo.do?b_search=link&setname="+fieldsetid;
								this.axx_url="/workbench/browse/showselfinfodetail.do?b_search=link&setname="+fieldsetid;
							}
							
						}
						pageContext.getOut().println("<tr>");                                  
						pageContext.getOut().println("<td>");
						pageContext.getOut().println("<table>");  
						pageContext.getOut().println("<tr>"); 
						pageContext.getOut().println("<td width='15'>");
						pageContext.getOut().println("</td> ");
						pageContext.getOut().println("<td align='left' nowrap>");	               
		                pageContext.getOut().println("</td>");
		                pageContext.getOut().println("<td nowrap> ");
		                pageContext.getOut().println("<font class='"+classname+"'>");
		                              //  <a href="/workbench/browse/browseinfo.do?b_search=link&a0100=${browseForm.a0100}&setname=a01&infosortflag=<%=ii%>" target="mil_body" onclick="showDiv(this);"><font styleClass="settext"> <bean:write name="setlist"/></font></a>
		                if("A00".equals(fieldset.getFieldsetid()))
		                {
		                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.a00_url+"','mil_body');showDiv(this);\">");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }else if("A01".equals(fieldset.getFieldsetid()))
		                {
		                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=/'swinhref('"+this.a01_url+"','mil_body');showDiv(this);'>");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;	                	
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }else
		                {
		                	pageContext.getOut().println("<a href='#"+fieldset.getFieldsetid()+"' onclick=\"winhref('"+this.axx_url+"','mil_body');showDiv(this);\">");
		                	pageContext.getOut().println("<font class='"+classname+"'>") ;
		                	pageContext.getOut().println(fieldset.getCustomdesc());
		                	pageContext.getOut().println("</font></a>");
		                }
		                pageContext.getOut().println("</font>");
		                pageContext.getOut().println("</td>   ");
		                pageContext.getOut().println("</tr>");
		                pageContext.getOut().println("</table>");
		                pageContext.getOut().println("</td>   ");             
		                pageContext.getOut().println("</tr> ");
					}	
				}
				pageContext.getOut().println("</table>");
				pageContext.getOut().println("</div>");
				pageContext.getOut().println("</td>");
				pageContext.getOut().println("</tr>");
			}
		}
	}
	/**
	 * 
	 * @param infoFieldViewList 
	 * @param infoFielditem  显示的项
	 * @param isCorrect  true:显示指定项，false显示未定义的
	 * @return
	 */
	public List getInfoSortFieldSet(List infoSetList,String setSub,boolean isCorrect)
	{
		List infoFieldList=new ArrayList();   
		if(setSub==null||setSub.length()<=0)
			return infoFieldList;			
		for(int i=0;i<infoSetList.size();i++)
		{
			 FieldSet fieldset=(FieldSet)infoSetList.get(i);			
			/*if(setSub.indexOf(fieldset.getCustomdesc())!=-1&&!fieldset.getFieldsetid().toUpperCase().equals("A01"))
			{
				infoFieldList.add(fieldset);
				break;
			}*/
			 if(setSub.equalsIgnoreCase(fieldset.getCustomdesc())&&!"A01".equals(fieldset.getFieldsetid().toUpperCase()))
				{
					infoFieldList.add(fieldset);
					break;
				}
		}
		return infoFieldList;
	}	
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
}
