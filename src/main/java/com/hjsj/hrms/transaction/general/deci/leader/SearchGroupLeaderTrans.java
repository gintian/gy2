package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.GroupOperation;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 显示班子分析的班子干部
 *<p>Title:SearchGroupLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 30, 2007</p> 
 *@author huaitao
 *@version 4.0
 */

public class SearchGroupLeaderTrans extends IBusiness {

	public void execute() throws GeneralException {
	    String code=(String)this.getFormHM().get("code");
	    String kind=(String)this.getFormHM().get("kind");
	    String curpage=(String)this.getFormHM().get("curpage");
	    String psize=(String)this.getFormHM().get("pagesize");
	    psize=psize==null||psize.length()==0?"20":psize;
	    LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
	    String groupid_setid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"setid");//数据集编号	
	    String groupid_codesetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"fielditem");//标示字段名
	    String groupid_value=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"value");//标示值
	    boolean isCorrect=true;
	    if(groupid_setid==null||groupid_setid.length()<=0)
	    	isCorrect=false;;
	    if(groupid_codesetid==null||groupid_codesetid.length()<=0)
	    	isCorrect=false;
	    if(groupid_value==null||groupid_value.length()<=0)
	    	isCorrect=false;
	    if(!isCorrect)
	    {
	    	this.getFormHM().put("candi_info", "noting");
	    	return;
	    }	    
	    String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);//显示指标
	    String bz_field=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);//显示班子库
	    LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList display_list=leaderParam.getFields(display_field);
		GroupOperation leadberOperation=new GroupOperation(this.getFrameconn(),this.userView);
		HashMap group_map=leadberOperation.getLeadberMap(bz_field,groupid_setid,groupid_codesetid,groupid_value,display_field,display_list,code,kind);
		String group_sql=(String)group_map.get("select_str");
		ArrayList group_fieldlist=(ArrayList)group_map.get("fieldlist");
		if(curpage==null||curpage.length()<=0|| "undefined".equals(curpage))
			curpage="1";
		ArrayList beanlist=leadberOperation.beanList(group_fieldlist,group_sql);
		String chtml="";
		int pagesize=0;
		try{
			pagesize=Integer.parseInt(psize);
		}catch(Exception e){
			pagesize=21;
		}
		if(beanlist!=null&&beanlist.size()>0)
			chtml=leadberOperation.beanlistHtml(display_list,group_fieldlist,beanlist,Integer.parseInt(curpage),pagesize,beanlist.size(),code,kind);
		else
		{
			this.getFormHM().put("candi_info", "noting");
			return;
		}
		StringBuffer html=new StringBuffer();
		html.append("<table width='90%' border='0' cellspacing='0'  align='left' cellpadding='0'><tr><td>");
		html.append("<table width='100%' border='0' cellspacing='0'  align='left' cellpadding='0' class='ListTable' >");
		html.append("<thead>");
		for(int i=0;i<display_list.size();i++)
		{
			CommonData data=(CommonData)display_list.get(i);
			String itemid=data.getDataValue();			
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			html.append("<td align='center'  width='' class='TableRow' nowrap>");
			html.append("&nbsp;"+fielditem.getItemdesc()+"&nbsp;");
			html.append("</td>");
		}
		html.append("<td align='center' class='TableRow' width='' nowrap>基本情况</td>");
		html.append("</thead>");
		html.append(chtml);
		html.append("</td></tr>");
		html.append("</table>");	
		
		//html.append("<tr><td align='center' colspan='"+(display_list.size()+1)+"'>");
		int curpage_in=Integer.parseInt(curpage);//当前页数
		int sumsize=beanlist.size();//总记录数
		int sumpage=sumsize/pagesize;//总页数
		int mod=sumsize%pagesize;//总记录数 对 总页数 求余
		if(mod>0)//如果余数大于0
			sumpage=sumpage+1;//总页数 + 1
		html.append("</td></tr><tr><td class='RecordRowP'>");
		html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
		html.append("<tr ><td align='left'>");
		html.append("&nbsp;第&nbsp;" + curpage_in + "&nbsp;页&nbsp;&nbsp;");
		html.append("共&nbsp;" + sumpage + "&nbsp;页&nbsp;&nbsp;");
		html.append("共&nbsp;" + sumsize + "&nbsp;人&nbsp;&nbsp;");
		html.append("&nbsp;每页&nbsp;<input type=hidden id=curpage value="+curpage_in+" /><input type='text' class='inputtext' size='4' id='pagerows'  value='"+pagesize+"' onkeypress='checkNumber(this,event)'>&nbsp;<a href='javascript:void(0);' onclick='getteam_leader("+(curpage_in)+",document.getElementById(\"pagerows\").value);'>刷新 </a>&nbsp;");
		html.append("</td><td align='right'>");
		if (curpage_in == 1) 
		{
			html.append("首页&nbsp;&nbsp;");
		}else 
		{
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+1+",document.getElementById(\"pagerows\").value);'>首页</a>&nbsp;&nbsp;");
		}
		
		if(curpage_in == sumpage && sumpage == 1)
		{
			html.append("上一页&nbsp;&nbsp;");
			html.append("下一页&nbsp;&nbsp;");
		}else if (curpage_in >= sumpage) 
		{
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+(curpage_in-1)+",document.getElementById(\"pagerows\").value);'>上一页</a>&nbsp;&nbsp;");
			html.append("下一页&nbsp;&nbsp;");
		}
		else if(curpage_in<=1)
		{
			html.append("上一页&nbsp;&nbsp;");
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+(curpage_in+1)+",document.getElementById(\"pagerows\").value);'>下一页</a>&nbsp;&nbsp;");
		}else
		{
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+(curpage_in-1)+",document.getElementById(\"pagerows\").value);'>上一页</a>&nbsp;&nbsp;");
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+(curpage_in+1)+",document.getElementById(\"pagerows\").value);'>下一页</a>&nbsp;&nbsp;");
		}
		
		if (curpage_in == sumpage) 
		{
			html.append("末页&nbsp;");
		}else 
		{
			html.append("<a href='javascript:void(0)' onclick='getteam_leader("+sumpage+",document.getElementById(\"pagerows\").value);'>末页&nbsp;</a>");
		}
		html.append("</td></tr>");
		html.append("</table>");
		html.append("</td></tr></table>");
		this.getFormHM().put("candi_html",com.hrms.frame.codec.SafeCode.encode(html.toString()));
		this.getFormHM().put("candi_info", "have");
	}

}
