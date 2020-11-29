package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * <p>Title:</p>
 * <p>Description:生成反查页面</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 1, 2006:9:15:00 AM</p>
 * @author lu
 * @version 1.0
 *
 */
public class ReverseFindHtmlBo {
	private Connection conn=null;
	private int rowNum=20;
	private String sql="";
	private String setMap_str="";
	private String fieldItem_str="";
	private String scanMode="";
	private String tabid="";
	private UserView userView = null;
	
	public ReverseFindHtmlBo(Connection conn) {
		this.conn=conn;
	}
	public ReverseFindHtmlBo(Connection conn,UserView userView) {
		this.conn=conn;
		this.userView=userView;
	}	
	
	/**
	 * 反查列表指标：序号、姓名、单位是固定的(扫描人员)
	 * 后面的指标：先列后行，列有多层的是从上到下，行是从左到右，每个格按统计条件指标的先后顺序显示
	 * @param userid
	 * @param username
	 * @param conditionSql
	 * @param tableTermsMap
	 * @param i
	 * @param j
	 * @param tnameBo
	 * @param count
	 * @param pageNum
	 * @param userview
	 * @return
	 */
	public String getReverseHtml(String userid,String username,String conditionSql,HashMap tableTermsMap,int i,int j,TnameBo tnameBo,int count,int pageNum,UserView userview)
	{
		String html="";
		ArrayList termList=tnameBo.getReverseValue(userid,username,conditionSql,tableTermsMap,i,j,userview);
		if(termList.size()==0){
			return "null";
		}else if(termList.size()==1){
			return "b";
		}
			
		String sql = termList.get(0).toString();//获得库中数据 进行分页 xgq 2010.3.10 start
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rowSet;
			if(sql.toLowerCase().indexOf("order")!=-1){
				rowSet =	dao.search("select count(*)  from ("+sql.substring(0,sql.toLowerCase().indexOf("order"))+") tablename ");
			}else{
				rowSet =	dao.search("select count(*)  from ("+sql+") tablename ");
			}
		if(rowSet.next()) {
            count=rowSet.getInt(1);
        }
		} catch (SQLException e) {
			e.printStackTrace();
		}//获得库中数据 进行分页 xgq 2010.3.10 end
		int total_page=0;  //总页数
		if(count%20==0) {
            total_page=count/rowNum;
        } else {
            total_page=count/rowNum+1;
        }
		html=executeHtml(termList,total_page,pageNum,count);
		return html;
	}
	
	
	
	
	public String executeHtml(ArrayList termList,int total_page,int pageNum,int count)
	{
		StringBuffer html=new StringBuffer("<div class='reportDiv common_border_color'>");
		html.append("<table width='100%' height='20' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
		String sql=(String)termList.get(0);
		String scanMode=(String)termList.get(1);
		this.scanMode=scanMode;
		
		ArrayList fieldItemSet=(ArrayList)termList.get(2);
		HashMap codeValueMap=(HashMap)termList.get(3);
		HashMap typeMap=(HashMap)termList.get(4);
		HashMap setMap=(HashMap)termList.get(5);
		HashMap nameMap=(HashMap)termList.get(6);
		HashMap midMap=new HashMap();	//临时变量  处理小数位  xieguiquan
		ArrayList fieldItemList=new ArrayList();
		
	/*	HashMap umMap=new HashMap();   //部门
		HashMap unMap=new HashMap();   //单位
		HashMap kMap=new HashMap();	   //职位*/
		int um=0;int un=1; int k=0;
		for(Iterator t=fieldItemSet.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			if(!"".equals(temp)&&!" ".equals(temp))
			{
				fieldItemList.add(temp);
				if(setMap.get(temp)!=null)
				{
					if("UM".equals((String)setMap.get(temp)))
					{
						um=1;
					}				
					else if("@K".equals((String)setMap.get(temp)))
					{
						k=1;
					}
				}
			}
		}
	/*	umMap=getOrganizationMap("UM",um);
		unMap=getOrganizationMap("UN",un);
		kMap=getOrganizationMap("@K",k);*/
		StringBuffer html2=new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		boolean isValue=false;
		try
		{
			String menTabid="";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			if(sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp")!=null&&sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp").length()>0) {
                menTabid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
            }
		
			HashMap db_map=new HashMap();
			for (int a = 0; a < fieldItemList.size(); a++) {
				String itemid = (String) fieldItemList.get(a);
				if("1".equals(scanMode)&&("B0110".equals(itemid)|| "A0101".equals(itemid))) {
                    continue;
                }
//				if(scanMode.equals("6")&&(itemid.equals("B0110")||itemid.equals("A0101")||itemid.equalsIgnoreCase("create_date")))
//					continue;
				if(DataDictionary.getFieldItem(itemid.toLowerCase())==null){ //临时变量的处理
					    //wangcq 2014-12-22  cstate='1'此字段为临时变量共享时查询相应的数据
					 	recset = dao.search(" select * from midvariable where (templetid="+this.tabid+" or cstate='1') and cname='"+itemid+"' and nflag=2");
					RecordVo vo=new RecordVo("MidVariable");
					if(recset.next()){
					vo.setInt("nid",recset.getInt("nid"));
					vo.setString("cname",recset.getString("cname"));
					vo.setString("chz",recset.getString("chz"));
					vo.setInt("ntype",recset.getInt("ntype"));
					vo.setString("cvalue",Sql_switcher.readMemo(recset,"cvalue"));
					vo.setInt("nflag",recset.getInt("nflag"));
					vo.setString("cstate",recset.getString("cstate"));
					vo.setInt("fldlen",recset.getInt("fldlen"));
					vo.setInt("flddec",recset.getInt("flddec"));
					vo.setInt("templetid",recset.getInt("templetid"));
					vo.setString("codesetid",recset.getString("codesetid"));
					midMap.put(recset.getString("cname"), vo);
					}
				}
			}
			recset=dao.search("select * from dbname");
			while(recset.next())
			{
				db_map.put(recset.getString("Pre").toUpperCase(),recset.getString("DBName"));
			}
			
			Set keySet=setMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				this.setMap_str+="/"+key+":"+(String)setMap.get(key);
			}
//			for(int i=0;i<fieldItemList.size();i++){
//				String item = (String)fieldItemList.get(i);
//				if(this.setMap_str.indexOf("/"+item+":")==-1)
//					this.setMap_str+="/"+item+":0";
//			}
			
			this.sql=sql;
			recset=dao.search(sql);
			int i=0;
			while(recset.next())
			{
				
				++i;
				if(i<=(pageNum-1)*rowNum) {
                    continue;
                }
				if(i>pageNum*rowNum) {
                    break;
                }
				html2.append("<tr class='"+(i%2==0?"trShallow":"trDeep")+"' >");
				html2.append("<td align='center' class='RecordRow' nowrap style='border-left:none;border-top:none;'>");
				html2.append(i);
				html2.append("</td>");
				
				if("1".equals(scanMode)|| "6".equals(scanMode))
				{	
					ArrayList dblist=this.userView.getPrivDbList();
					if(dblist.size()!=1)
					{
						html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");	
						html2.append((String)db_map.get(recset.getString("dbpre").toUpperCase()));
						html2.append("</td>");
					}
					
					html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("<a href='/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&encryptParam="+PubFunc.encrypt("userbase="+recset.getString("dbpre")+"&username="+this.userView.getUserName()+"&a0100="+"~"+SafeCode.encode(PubFunc.convertTo64Base(recset.getString("a0100")))+"&inforkind=1&tabid="+menTabid)+"&multi_cards=-1' target='_blank'>");
                    }
					html2.append(recset.getString("a0101")!=null?recset.getString("a0101"):"");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("</a>");
                    }
					html2.append("</td>");
					html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
					//html2.append((String)unMap.get(recset.getString("b0110")));
					if(recset.getString("b0110")!=null) {
                        html2.append(AdminCode.getCodeName("UN", recset.getString("b0110")));
                    }
					
					html2.append("</td>");
				}else if("2".equals(scanMode)|| "3".equals(scanMode)|| "4".equals(scanMode)){
					String aa = recset.getString("b0110");
					String bsql="select codesetid,parentid from organization where codeitemid='"+aa+"'";
					String kind="" ;
					String orgtype="";
					String codesetid="";
					String parentid="";
					boolean isOrg=false;
					try {
						RowSet brs=dao.search(bsql);
						if(brs.next())
						{
							codesetid=brs.getString("codesetid");
							parentid=brs.getString("parentid");
							kind=getKindFormCodeSetId(codesetid);
							orgtype="org";
							isOrg=true;
						}
						if(!isOrg)
						{
							sql="select codesetid,parentid from vorganization where codeitemid='"+aa+"'";
							brs=dao.search(sql);
							if(brs.next())
							{
								codesetid=brs.getString("codesetid");
								parentid=brs.getString("parentid");
								kind=getKindFormCodeSetId(codesetid);
								orgtype="vorg";					
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("<a href='/workbench/orginfo/editorginfodata.do?b_search=link&code="+aa+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&edittype=update&isself=0' target='_blank'>");
                    }
					html2.append(recset.getString("a_name")!=null?recset.getString("a_name"):"");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("</a>");
                    }
					html2.append("</td>");
				}else if("5".equals(scanMode)){     //wangcq 2014-12-04 职位与单位部门分开，单独查询
					String aa = recset.getString("E01A1");
					String bsql="select codesetid,parentid from organization where codeitemid='"+aa+"'";
					String kind="" ;
					String orgtype="";
					String codesetid="";
					String parentid="";
					boolean isOrg=false;
					try {
						RowSet brs=dao.search(bsql);
						if(brs.next())
						{
							codesetid=brs.getString("codesetid");
							parentid=brs.getString("parentid");
							kind=getKindFormCodeSetId(codesetid);
							orgtype="org";
							isOrg=true;
						}
						if(!isOrg)
						{
							sql="select codesetid,parentid from vorganization where codeitemid='"+aa+"'";
							brs=dao.search(sql);
							if(brs.next())
							{
								codesetid=brs.getString("codesetid");
								parentid=brs.getString("parentid");
								kind=getKindFormCodeSetId(codesetid);
								orgtype="vorg";					
							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("<a href='/workbench/dutyinfo/editorginfodata.do?b_search=link&code="+aa+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&edittype=update&isself=0' target='_blank'>");
                    }
					html2.append(recset.getString("a_name")!=null?recset.getString("a_name"):"");
					if(menTabid!=null&&menTabid.length()>0&&!"#".equals(menTabid)) {
                        html2.append("</a>");
                    }
					html2.append("</td>");
				}
				for (int a = 0; a < fieldItemList.size(); a++) {
					String itemid = (String) fieldItemList.get(a);
					
					
					if("1".equals(scanMode)&&("B0110".equals(itemid)|| "A0101".equals(itemid))) {
                        continue;
                    }
					if("6".equals(scanMode)&&("B0110".equals(itemid)|| "A0101".equals(itemid))) {
                        continue;
                    }
					if(!"1".equals(scanMode))//wangcq 2014-12-03 跟表头一样过滤此项
		        	 {
		        		if("B0110".equalsIgnoreCase(itemid)|| "E01A1".equalsIgnoreCase(itemid)) {
                            continue;
                        }
		        	 }
					if("6".equals(scanMode)&& "create_date".equalsIgnoreCase(itemid)){
						
					}else{
					if(DataDictionary.getFieldItem(itemid.toLowerCase())==null){ //临时变量的处理
						String value = "";
						if(midMap!=null&&midMap.get(itemid)!=null){
							RecordVo vo = (RecordVo)midMap.get(itemid);
							int ntype = vo.getInt("ntype");
							int flddec  = vo.getInt("flddec");
							String codesetid = vo.getString("codesetid");
							switch(ntype){
							case 1:			//数值型
								value = PubFunc.round(recset.getString(itemid),flddec);
							break;
							case 2:			//字符型
							case 3:			//日期型
								value = recset.getString(itemid);
								if(value!=null&&("1899.12.30".equals(value)|| "1899-12-30".equals(value))) {
                                    value ="";
                                }
								break;
							case 4:			//代码型
								value = AdminCode.getCodeName(codesetid, recset.getString(itemid));
								break;
							}
						}
						html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
						html2.append(value);
						html2.append("&nbsp;</td>");
						if(!isValue)
						{
							this.fieldItem_str+="/"+itemid;
						}
						continue;
					}
					}	
					
					
					if(!isValue)
					{
						this.fieldItem_str+="/"+itemid;
					}
					
					html2.append("<td align='left' class='RecordRow' nowrap style='border-right:none;border-top:none;'> &nbsp;");
					String set = (String) setMap.get(itemid);
					if (set==null|| "0".equals(set)) // 不是代码型
					{
						String context="";
						if("create_date".equalsIgnoreCase(itemid)){
							context=""+recset.getDate(itemid);
						}else{
							 context=recset.getString(itemid);
						}
						
						if(context!=null)
						{
							FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
							if(item!=null&& "N".equalsIgnoreCase(item.getItemtype()))
							{
								int decimalWidth=item.getDecimalwidth();
								String value=recset.getString(itemid);
								html2.append(PubFunc.round(value, decimalWidth));
								
							}else if(item!=null&& "D".equalsIgnoreCase(item.getItemtype()))
							{
								if(context!=null&&context.trim().length()==10&&("1899-12-30".equals(context)|| "1899.12.30".equals(context))){
									context = "";
								}
								html2.append(context);
								
							}
							else
							{
								if("create_date".equalsIgnoreCase(itemid)){
									if(context.length()>9) {
                                        html2.append(context.substring(0, 10));
                                    } else {
                                        html2.append(recset.getString(itemid));
                                    }
								}else {
                                    html2.append(recset.getString(itemid));
                                }
							}
						}
					} else {
						String context="";
						if(recset.getString(itemid)!=null&&!"".equals(recset.getString(itemid)))
						{
							if("UM".equals(set))
							{
							//	context=umMap.get(recset.getString(itemid))!=null?(String)umMap.get(recset.getString(itemid)):"";
								if(recset.getString(itemid)!=null) {
                                    html2.append(AdminCode.getCodeName("UM", recset.getString(itemid)));
                                }
							}
							else if("UN".equals(set))
							{
								//context=unMap.get(recset.getString(itemid))!=null?(String)unMap.get(recset.getString(itemid)):"";
								if(recset.getString(itemid)!=null)
								{									
									if(AdminCode.getCodeName("UN", recset.getString(itemid))!=null && AdminCode.getCodeName("UN", recset.getString(itemid)).trim().length()>0)
									{
										html2.append("<a href='/general/inform/org/searchorgbrowse.do?b_search=link&code="+recset.getString(itemid)+"&kind=2&orgtype=org' target='_blank'>");																			
										html2.append(AdminCode.getCodeName("UN", recset.getString(itemid)));
										html2.append("</a>");
									}
									else
									{
										html2.append("<a href='/general/inform/org/searchorgbrowse.do?b_search=link&code="+recset.getString(itemid)+"&kind=1&orgtype=org' target='_blank'>");																			
										html2.append(AdminCode.getCodeName("UM", recset.getString(itemid)));
										html2.append("</a>");
									}									
								}
							}
							else if("@K".equals(set))
							{
								//context=kMap.get(recset.getString(itemid))!=null?(String)kMap.get(recset.getString(itemid)):"";
								if(recset.getString(itemid)!=null) {
                                    html2.append(AdminCode.getCodeName("@K", recset.getString(itemid)));
                                }
							}
							else 
							{
								context=((String) codeValueMap.get(set + "##"
										+ recset.getString(itemid)));						
								
							}
						}
						if(context!=null) {
                            html2.append(context);
                        }
					}
					html2.append("&nbsp;</td>");
				}
				html2.append("</tr>");
				isValue=true;
			}
		
		
		
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(html2.toString().trim().length()<1) {
            total_page=1;
        }
		html.append(getPageHeade(scanMode,fieldItemList,nameMap,total_page,pageNum,midMap,count));   //添加表头和翻页符
		html.append(html2.toString());
		html.append("</table>");
		html.append("</div>");
		if(isValue)
			/* 提取数据-查阅-反查hcm样式调整 xiaoyun 2014-6-25 start */
			//html.append("<table align='center' width='95%'><tr><td style='height:35px'><input type='button' value='输出Excel' onclick='exportReverseExcel()' class='mybutton' /></td></tr></table> ");
        {
            html.append("<table align='center' width='95%'><tr><td style='height:35px'><input type='button' id='excel' value='输出Excel' onclick='exportReverseExcel()' class='mybutton' /></td></tr></table> ");
        }
			/* 提取数据-查阅-反查hcm样式调整 xiaoyun 2014-6-25 end */
		return html.toString();
	}
	
    private String getKindFormCodeSetId(String codesetid)
    {
    	String kind="";
    	if("UN".equalsIgnoreCase(codesetid)) {
            kind="2";
        } else if("UM".equalsIgnoreCase(codesetid)) {
            kind="1";
        } else if("@K".equalsIgnoreCase(codesetid)) {
            kind="0";
        } else {
            kind="2";
        }
    	return kind;
    }
	
	
	//得到表头和翻页符
	public String getPageHeade(String scanMode,ArrayList fieldItemList,HashMap nameMap,int total_page,int pageNum,HashMap midMap,int count)
	{
		int colspan=fieldItemList.size();
		String extendTd="";
		if("1".equals(scanMode)|| "6".equals(scanMode))
		{
			colspan+=4;
			ArrayList dblist=this.userView.getPrivDbList();
			if(dblist.size()==1) {
                extendTd="<td align='center' class='TableRow' nowrap style='border-left:none;border-top:none;'>"+ResourceFactory.getProperty("recidx.label")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("kq.emp.change.emp.a0101")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("report.b0110")+"</td>";
            } else {
                extendTd="<td align='center' class='TableRow' nowrap style='border-left:none;border-top:none;'>"+ResourceFactory.getProperty("recidx.label")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("report.nbase")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("kq.emp.change.emp.a0101")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("report.b0110")+"</td>";
            }
		}
		else
		{
			colspan+=2;
			extendTd="<td align='center' class='TableRow' nowrap style='border-left:none;border-top:none;'>"+ResourceFactory.getProperty("recidx.label")+"</td><td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>"+ResourceFactory.getProperty("general.inform.org.organizationName")+"</td>";
	
		}
		
		StringBuffer pageHead1=new StringBuffer("");		
        for(int i=0;i<fieldItemList.size();i++)
         {	 
        	 String itemid=(String)fieldItemList.get(i);
        	 if("1".equals(scanMode)&&("B0110".equals(itemid)|| "A0101".equals(itemid)))
        	 {
        		 colspan--;
        		 continue;
        	 } 
        	 if("6".equals(scanMode)&&("B0110".equals(itemid)|| "A0101".equals(itemid)))
        	 {
        		 colspan--;
        		 continue;
        	 }
        	 if(!"1".equals(scanMode))
        	 {
        		if("B0110".equalsIgnoreCase(itemid)|| "E01A1".equalsIgnoreCase(itemid)) {
                    continue;
                }
        	 }
        	  
        	 if(DataDictionary.getFieldItem(itemid.toLowerCase())==null){
        		 pageHead1.append("<td align='center' class='TableRow' nowrap >");
        		 if("create_date".equalsIgnoreCase(itemid)){
        			 pageHead1.append("归档日期"); 
        		 }else{
        			 if(midMap!=null&&midMap.get(itemid)!=null){
							RecordVo vo = (RecordVo)midMap.get(itemid);
							 pageHead1.append(vo.getString("chz"));
        			 }else {
                         pageHead1.append(itemid);
                     }
        		 }
            	 pageHead1.append("&nbsp;</td>"); 
        		 continue;
        	 }
        	 
        	 pageHead1.append("<td align='center' class='TableRow' nowrap style='border-right:none;border-top:none;'>");
        	 pageHead1.append(nameMap.get(itemid));
        	 pageHead1.append("&nbsp;</td>");
         }
        
        StringBuffer pageHead=new StringBuffer("<thead><tr><td colspan='"+colspan+"' class='TableRow' style='border-right:none;border-left:none;border-top:none;'>"+ResourceFactory.getProperty("report.parse.p")+"：<select name='pageNum' size='1' onchange='change();'>");
        for(int i=1;i<=total_page;i++)
		{
			pageHead.append("<option value='"+i+"' ");
			if(i==pageNum) {
                pageHead.append(" selected ");
            }
			pageHead.append(">"+ResourceFactory.getProperty("reportanalyse.di")+i+ResourceFactory.getProperty("label.page.page")+"</option>");
		}
         pageHead.append("</select>共"+count+"条记录</td></tr><tr>"+extendTd);
         pageHead.append(pageHead1.toString());
         pageHead.append("</tr></thead>");
		return pageHead.toString();
	}
	
	
	
	
	
	
	

	public HashMap getOrganizationMap(String codesetid,int flag)
	{
		HashMap map=new HashMap();
		if(flag==0) {
            return map;
        }
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"'");
			while(recset.next()) {
                map.put(recset.getString("codeitemid"),recset.getString("codeitemdesc"));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}



	public String getSql() {
		return sql;
	}



	public void setSql(String sql) {
		this.sql = sql;
	}



	public String getSetMap_str() {
		return setMap_str;
	}



	public void setSetMap_str(String setMap_str) {
		this.setMap_str = setMap_str;
	}



	public String getFieldItem_str() {
		return fieldItem_str;
	}



	public void setFieldItem_str(String fieldItem_str) {
		this.fieldItem_str = fieldItem_str;
	}



	public String getScanMode() {
		return scanMode;
	}



	public void setScanMode(String scanMode) {
		this.scanMode = scanMode;
	}



	public String getTabid() {
		return tabid;
	}



	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	
	
	
	
	
	
}




















