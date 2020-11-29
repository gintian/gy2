package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.GroupOperation;
import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class TeamPhotoShowTrans extends IBusiness {

	public void execute() throws GeneralException {
		 String code=(String)this.getFormHM().get("code");
		    String kind=(String)this.getFormHM().get("kind");
		    String curpage=(String)this.getFormHM().get("curpage");
		    String pagesize_s=(String)this.getFormHM().get("pagesize");
		    LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		    String candid_setid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"setid");//数据集编号	
		    String candid_codesetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"fielditem");//标示字段名
		    String candid_value=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"value");//标示值
		    String bz_field=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);
		    boolean isCorrect=true;
		    if(candid_setid==null||candid_setid.length()<=0)
		    	isCorrect=false;;
		    if(candid_codesetid==null||candid_codesetid.length()<=0)
		    	isCorrect=false;
		    if(candid_value==null||candid_value.length()<=0)
		    	isCorrect=false;
		    if(!isCorrect)
		    {
		    	this.getFormHM().put("candi_html",SafeCode.encode("没有完整定义后备干部标识！"));
		    	return;
		    }
		    GroupOperation leadberOperation=new GroupOperation(this.getFrameconn(),this.userView);
		    int sumsize=leadberOperation.getLeadberConnt(bz_field,candid_setid,candid_codesetid,candid_value,code,kind);
		    int pagesize=21;//一页显示几个
		    if(pagesize_s!=null&&pagesize_s.length()>0)
		    	pagesize=Integer.parseInt(pagesize_s);
		    int trNum=2;
		    if(curpage==null||curpage.length()<=0)
		    	curpage="1";
		    int curpage_in=Integer.parseInt(curpage);//当前页
			int sumpage=sumsize/pagesize;//总页数
			int mod=sumsize%pagesize;
			if(mod>0)
				sumpage=sumpage+1;
			ArrayList beanlist=leadberOperation.getLeadberEmpMess(bz_field,candid_setid,candid_codesetid,candid_value,code,kind);
			StringBuffer html=new StringBuffer();
			html.append("<table width='100%' border='0' cellspacing='1'  align='center' cellpadding='1' >");
			String candi_html=leadberOperation.beanPhotoHtml("",beanlist,curpage_in,pagesize,sumsize,trNum);
			html.append(candi_html);
			html.append("<tr><td align='center' colspan='"+(pagesize/trNum)+"'>");
			html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
			html.append("<tr ><td align='left'>");
			html.append("&nbsp;&nbsp;&nbsp;&nbsp;第&nbsp;" + curpage_in + "&nbsp;页&nbsp;&nbsp;");
			html.append("共&nbsp;" + sumpage + "&nbsp;页&nbsp;&nbsp;");
			html.append("&nbsp;每页&nbsp;<input type=hidden id=curpage value="+curpage_in+" /><input type='text' class='inputtext' size='4' id='pagerows'  value='"+pagesize+"' onkeypress='checkNumber(this,event)'>&nbsp;<a href='javascript:void(0);' onclick='getteam_photo("+(curpage_in)+",document.getElementById(\"pagerows\").value);'>刷新 </a>&nbsp;");
			html.append("</td><td align='right'>");
			if (curpage_in == 1) 
			{
				html.append("首页&nbsp;&nbsp;");
			}else 
			{
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+1+",document.getElementById(\"pagerows\").value);'>首页</a>&nbsp;&nbsp;");
			}
			
			if(curpage_in == sumpage && sumpage == 1)
			{
				html.append("上一页&nbsp;&nbsp;");
				html.append("下一页&nbsp;&nbsp;");
			}else if (curpage_in >= sumpage) 
			{
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+(curpage_in-1)+",document.getElementById(\"pagerows\").value);'>上一页</a>&nbsp;&nbsp;");
				html.append("下一页&nbsp;&nbsp;");
			}
			else if(curpage_in<=1)
			{
				html.append("上一页&nbsp;&nbsp;");
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+(curpage_in+1)+",document.getElementById(\"pagerows\").value);'>下一页</a>&nbsp;&nbsp;");
			}else
			{
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+(curpage_in-1)+",document.getElementById(\"pagerows\").value);'>上一页</a>&nbsp;&nbsp;");
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+(curpage_in+1)+",document.getElementById(\"pagerows\").value);'>下一页</a>&nbsp;&nbsp;");
			}
			
			if (curpage_in == sumpage) 
			{
				html.append("末页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}else 
			{
				html.append("<a href='javascript:void(0)' onclick='getteam_photo("+sumpage+",document.getElementById(\"pagerows\").value);'>末页&nbsp;</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			html.append("</td></tr>");
			html.append("</table>");	
			html.append("</table>");	
			this.getFormHM().put("candi_html",SafeCode.encode(html.toString()));
	}

}
