package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadberOperation;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 显示班子分析的储备干部
 *<p>Title:SearchCandiLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 27, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchCandiLeaderTrans extends IBusiness {

	public void execute() throws GeneralException {
	    String code=(String)this.getFormHM().get("code");
	    String kind=(String)this.getFormHM().get("kind");
	    String curpage=(String)this.getFormHM().get("curpage");
	    String psize=(String)this.getFormHM().get("pagesize");
	    psize=psize==null||psize.length()==0?"21":psize;
	    LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
	    String candid_setid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"setid");//数据集编号	
	    String candid_codesetid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"fielditem");//标示字段名
	    String candid_value=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"value");//标示值
	    boolean isCorrect=true;
	    if(candid_setid==null||candid_setid.length()<=0)
	    	isCorrect=false;;
	    if(candid_codesetid==null||candid_codesetid.length()<=0)
	    	isCorrect=false;
	    if(candid_value==null||candid_value.length()<=0)
	    	isCorrect=false;
	    if(!isCorrect)
	    {
	    	this.getFormHM().put("candi_info", "noting");
	    	return;
	    }	    
	    String display_field=leadarParamXML.getTextValue(LeadarParamXML.CONDI_DISPLAY);//显示指标
	    String hb_field=leadarParamXML.getTextValue(LeadarParamXML.HBDBPRE);//显示后备库
	    LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList display_list=leaderParam.getFields(display_field);
		LeadberOperation leadberOperation=new LeadberOperation(this.getFrameconn(),this.userView);
		HashMap candid_map=leadberOperation.getLeadberMap(hb_field,candid_setid,candid_codesetid,candid_value,display_list,code,kind);
		String candid_sql=(String)candid_map.get("select_str");
		ArrayList candid_fieldlist=(ArrayList)candid_map.get("fieldlist");
		if(curpage==null||curpage.length()<=0|| "undefined".equals(curpage))
			curpage="1";
		ArrayList beanlist=leadberOperation.beanList(candid_fieldlist,candid_sql);
		if(beanlist==null||beanlist.size()<=0)
		{
			this.getFormHM().put("candi_info", "noting");
			return;
		}
			
		String chtml="";
		int pagesize=5;
		try{
			pagesize=Integer.parseInt(psize);
		}catch(Exception e){
			pagesize=21;
		}
		if(beanlist!=null&&beanlist.size()>0)
			chtml=leadberOperation.beanlistHtml(display_list,candid_fieldlist,beanlist,Integer.parseInt(curpage),pagesize,beanlist.size(),code,kind);
		StringBuffer html=new StringBuffer();
		html.append("<table width='90%' border='0' cellspacing='0'  align='left' cellpadding='0'><tr><td>");
		html.append("<table width='100%' border='0' cellspacing='0'  align='left' cellpadding='0' class='ListTable'>");
		html.append("<thead>");
		for(int i=0;i<display_list.size();i++)
		{
			CommonData data=(CommonData)display_list.get(i);
			String itemid=data.getDataValue();			
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			html.append("<td align='center' class='TableRow' width='' nowrap>");
			html.append("&nbsp;"+fielditem.getItemdesc()+"&nbsp;");
			html.append("</td>");
		}
		html.append("<td align='center' class='TableRow' width='' nowrap>基本情况</td>");
		html.append("</thead>");
		html.append(chtml);
		html.append("</td></tr>");
		html.append("</table>");	
		
		//html.append("<tr ><td align='center'  colspan='"+(display_list.size()+1)+"'>");
		int curpage_in=Integer.parseInt(curpage);
		int sumsize=beanlist.size();
		int sumpage=sumsize/pagesize;
		int mod=sumsize%pagesize;
		if(mod>0)
			sumpage=sumpage+1;
		html.append("</td></tr><tr><td class='RecordRowP'>");
		html.append("<table width='99%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
		html.append("<tr ><td align='left' >");	
		html.append("第  "+curpage_in+" 页&nbsp;&nbsp;共 "+sumpage+" 页 &nbsp;&nbsp;");	
		html.append("共"+beanlist.size()+"人&nbsp;&nbsp;");
		html.append("&nbsp;每页&nbsp;<input type=hidden id=curpage value="+curpage_in+" /><input type='text' size='4' id='pagerows'  value='"+pagesize+"' onkeypress='checkNumber(this,event)'>&nbsp;<a href='javascript:void(0);' onclick='getcandi_leader("+(curpage_in)+",document.getElementById(\"pagerows\").value);'>刷新 </a>&nbsp;");
		//html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		html.append("</td><td align='right'>");
		if(curpage_in==1){
			html.append("首页&nbsp;&nbsp;上一页&nbsp;&nbsp;");
		}else{
			html.append("<a href='javascript:void(0)' onclick='getcandi_leader("+1+",document.getElementById(\"pagerows\").value);'>首页</a>&nbsp;&nbsp;");
			html.append("<a href='javascript:void(0)' onclick='getcandi_leader("+(curpage_in-1)+",document.getElementById(\"pagerows\").value);'>上一页</a>&nbsp;&nbsp;");
		}
		if(sumpage==curpage_in){
			html.append("下一页&nbsp;&nbsp;末页");
		}else{
			html.append("<a href='javascript:void(0)' onclick='getcandi_leader("+(curpage_in+1)+",document.getElementById(\"pagerows\").value);'>下一页</a>&nbsp;&nbsp;");
			html.append("<a href='javascript:void(0)' onclick='getcandi_leader("+sumpage+",document.getElementById(\"pagerows\").value);'>末页</a>");
		}
		html.append("</td></tr>");
		html.append("</table>");
		html.append("</td></tr></table>");
		this.getFormHM().put("candi_info", "have");
		//System.out.println(candid_sql.toString());
		this.getFormHM().put("candi_html",com.hrms.frame.codec.SafeCode.encode(html.toString()));
	}
    
}
