/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:RefilloutMusterTrans</p>
 * <p>Description:重填花名册</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-26:14:01:58</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class RefilloutMusterTrans extends IBusiness {
	
	public void execute() throws GeneralException {
	  try
	  {	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String[] tabid=(String[])this.getFormHM().get("tabid");
//		if(tabid==null&&hm.get("a_tabid")==null)
//		{
//			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
//		}
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)this.getFormHM().get("inforkind");
		
		String checkflag=(String)hm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"0";
		hm.remove("checkflag");
		
		if(infor_kind==null|| "".equals(infor_kind))
			infor_kind="1";
		/**用查询结果,重新生成花名册数据*/
		String dbpre=(String)this.getFormHM().get("dbpre");
		if(dbpre==null)
			dbpre="";	
		String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	    a_code=a_code!=null?a_code:"";
	    
	    this.getFormHM().put("a_code",a_code);
		if(a_code.trim().length()>1){
			a_code=a_code.substring(2);
		}
		
		String thetabid="";
		if(hm.get("a_tabid")==null)
		{
			List tablist=Arrays.asList(tabid);
			/**选中多个花名册时,仅以第一个为准*/
			thetabid=(String)tablist.get(0);		
		}
		else
		{
			thetabid=(String)hm.get("a_tabid");
		}
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		String tabname=null;
		if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.MUSTER, thetabid))
			throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
		String returncheck=(String)this.getFormHM().get("returncheck");
		returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"0";
		this.getFormHM().put("returncheck",returncheck);
		
		/** 是否包含历史记录*/
		String history="0";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		this.frowset=dao.search("select expr from lbase where tabid="+thetabid);
		if(this.frowset.next())
		{
			String expr=Sql_switcher.readMemo(this.frowset,"expr");
			if(expr!=null&& "1".equals(expr.trim()))
				history="1";
		}
		
		if(!"1".equals(infor_kind))
			history="0";  //目前部门、职位花名册没有提供历史记录功能,所以默认置为 0  dengcan 2008/02/03
		if("1".equals(returncheck)){
			if(musterbo.createMusterTempTable(infor_kind,dbpre,thetabid,this.userView.getUserName().trim(),history,a_code))
				tabname=musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName().trim());
		}else{
			if(musterbo.createMusterTempTable(infor_kind,dbpre,thetabid,this.userView.getUserName().trim(),history))
				tabname=musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName().trim());
		}
		this.getFormHM().put("mustername",tabname);
		
		ArrayList fieldlist = musterbo.getFieldlist();
		ArrayList itemlist = new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			Field field = (Field)fieldlist.get(i);
			if("A0100".equals(field.getName()))
				field.setVisible(false);
//			if(field.getName().equals("A0100")||field.getName().equals("B0110")
//					||field.getName().equals("E01A1"))
//				continue;
			if(!"recidx".equalsIgnoreCase(field.getName())&& "0".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setReadonly(true);
			if("recidx".equalsIgnoreCase(field.getName()))
				field.setReadonly(true);
			itemlist.add(field);
		}
		
		/**把花名册指标-->显示的数据格式*/
		this.getFormHM().put("fieldlist",itemlist);
		String countPerson = "";
		if(infor_kind!=null&& "1".equals(infor_kind)){
			RowSet rs = dao.search("select count(A0100) from "+tabname+" group by A0100");
			int i=0;
			while(rs.next()){
				i++;
			}
			countPerson="(总人数:"+i+"人)";
		}
		String sql="select * from "+tabname+" order by recidx";
		this.getFormHM().put("sql",sql);
		this.getFormHM().put("countStr",countPerson);
		this.getFormHM().put("currid",thetabid);
		this.getFormHM().put("infor_Flag",infor_kind);
		this.getFormHM().put("coumsize",itemlist.size()+"");
		this.getFormHM().put("checkflag",checkflag);
		this.getFormHM().put("condlist",musterbo.getUsuallyCondList(infor_kind,this.userView));
		this.getFormHM().put("sortitem",musterbo.getOrderFieldStr(thetabid));
		String title = titleName(dao,thetabid,infor_kind);
		title=title!=null?title:"";

		this.getFormHM().put("mustertitle",title);
		if(hm.get("a_tabid")!=null)
			hm.remove("a_tabid");
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }		
	}
	private String titleName(ContentDAO dao,String tabid,String infor_kind){
		StringBuffer title= new StringBuffer();
		
		ExecuteExcel excel = new ExecuteExcel(this.getFrameconn());
		excel.setPagePrintStyle(tabid);
		String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String t_fontEffect="0";
        int    t_fontSize=16;  
        String t_color="#000000";       
        String  t_underLine="#fu[0]";
        String  t_strikethru="#fs[0]";
        if(excel.paramtervo!=null){
        	if(excel.paramtervo.getTitle_fn().length()>0)
        		t_fontfamilyname=excel.paramtervo.getTitle_fn();
	       	 t_fontEffect=excel.getFontEffect(4);
	       	 if(excel.paramtervo.getTitle_fz().length()>0)
	       		 t_fontSize=Integer.parseInt(excel.paramtervo.getTitle_fz());
	       	 if(excel.paramtervo.getTitle_fc().length()>0)
	       		 t_color=excel.paramtervo.getTitle_fc();	       	 
	       	 if(excel.paramtervo.getTitle_fu()!=null&&excel.paramtervo.getTitle_fu().length()>0)
	       		 t_underLine=excel.paramtervo.getTitle_fu();
	       	if(excel.paramtervo.getTitle_fs()!=null&&excel.paramtervo.getTitle_fs().length()>0)
	       		t_strikethru=excel.paramtervo.getTitle_fs();

        }
		try {
			String content = "";
			this.frowset = dao.search("select Title from lname where Tabid="+tabid);
			if(this.frowset.next()){
				content=this.frowset.getString("Title");
			}
			content=content!=null&&content.trim().length()>0?content:"";
			if(content.trim().length()>0){
				int top = 40;
				if(t_fontSize>16)
					top = 30;
//				if(infor_kind!=null&&infor_kind.equals("1"))
//					title.append("<div style=position:absolute;top:"+top+";left:580;WIDTH:500;WORD-BREAK:break-all;");
//				else
//					title.append("<div style=position:absolute;top:"+top+";left:520;WIDTH:500;WORD-BREAK:break-all;");
//				title.append("font-size: "+t_fontSize+"px;");
//				title.append("color: "+t_color+";");
//				title.append("FONT-FAMILY: "+t_fontfamilyname+";");
//				if(t_fontEffect.equals("2")){
//					title.append("font-weight:bold;");
//				}else if(t_fontEffect.equals("3")){
//					title.append("font-style:italic;");
//				}else if(t_fontEffect.equals("4")){
//					title.append("font-weight:bold;");
//					title.append("font-style:italic;");
//				}
//				if(t_underLine.equals("#fu[1]")){
//					title.append("text-decoration:underline");
//					if(t_strikethru.equals("#fs[1]"))
//						title.append(" line-through");
//					title.append(";");
//				}else{
//					if(t_strikethru.equals("#fs[1]")){
//						title.append("text-decoration:line-through;");
//					}
//				}
//				title.append(">");
				title.append(content);
//				title.append("</div>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title.toString();
	}
}
