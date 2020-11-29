
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;


public class MoveReportUnitTrans extends IBusiness {


	public void execute() throws GeneralException {
	//	LazyDynaBean  objbean = (LazyDynaBean)this.getFormHM().get("objbean");
		String unitcode1 = (String)this.getFormHM().get("unitcode");
		String a00001 = (String)this.getFormHM().get("a0000");
		String page = (String)this.getFormHM().get("page");
		String pagecount = (String)this.getFormHM().get("pagecount");
		String sql = (String) this.userView.getHm().get("sql_filter");//从userView里面获取sql，防止页面能看见sql zhaoxg 2014-2-10
		String method =(String)this.getFormHM().get("method");

		//parentid
		String parentidcode =(String)this.getFormHM().get("parentidcode");
		String id=(String)this.getFormHM().get("id");
		String pages=(String)this.getFormHM().get("pages");
		String count=(String)this.getFormHM().get("counts");
		sql=PubFunc.keyWord_reback(sql);
		
		int start = (Integer.parseInt(page)-1)*Integer.parseInt(pagecount);
		int num=Integer.parseInt(id);
		int counts=Integer.parseInt(count);
		int pageint=(Integer.parseInt(page));
		int pagesint=Integer.parseInt(pages);
		int intpage=Integer.parseInt(pagecount);
		boolean flag1=false;
		String a00002=null;
		String unitid2=null;
		ContentDAO  dao1=new ContentDAO(this.getFrameconn());
		String sql2="select * from tt_organization" ;
		String sql3,sql4;	
		try{
			this.frowset=dao1.search(sql2);
			while(this.frowset.next()){
				 a00002=this.frowset.getString("a0000");
				 if(a00002==null){
					flag1=true;
					break;	
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	
		if(flag1){
			try{
			
				sql4="select unitid from tt_organization where unitcode='"+unitcode1+"'";	
				sql3="update tt_organization set a0000=unitid";
				this.frowset=dao1.search(sql4);
				if(this.frowset.next())
					unitid2=this.frowset.getString("unitid");
				if(a00001==null||a00001.trim().length()==0){
					a00001=unitid2;
				}
				dao1.update(sql3);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if("up".equals(method)){//上移操作
			/*杜美龙加*/
				if(num==0&&pageint==1){
					

					throw new GeneralException("已经是第一条记录,不允许上移！");
				}
				
				String upsql="";
				if("".equals(parentidcode)){
					 upsql ="select a0000,unitcode from tt_organization where a0000<"+a00001+" and parentid=unitcode  order by a0000 desc";
				}else{
				 upsql ="select a0000,unitcode from tt_organization where a0000<"+a00001+" and parentid='"+parentidcode+"'  order by a0000 desc";
				}
			
			if(!"".equals(unitcode1)&&!"".equals(a00001))
				updateA0000(unitcode1,Integer.parseInt(a00001),upsql);
		
			
		}else if("down".equals(method)){
			int temp=counts-(pagesint-1)*intpage-1;
			if(pagesint==pageint&&num==temp){
				throw new GeneralException("已经是最后一条记录,不允许下移！");
			}
			String downsql ="";
			if("".equals(parentidcode)){
			 downsql ="select a0000,unitcode from tt_organization where a0000>"+a00001+" and parentid=unitcode  order by a0000 asc";
			}else{
				 downsql ="select a0000,unitcode from tt_organization where a0000>"+a00001+" and parentid='"+parentidcode+"'  order by a0000 asc";
					
			}
			if(!"".equals(unitcode1)&&!"".equals(a00001))
				updateA0000(unitcode1,Integer.parseInt(a00001),downsql);
		}
		//查询
		//RecordVo vo = RecordVo
		//获得当前页
		//获得每页显示的数据
		//获得当前的sql
		//组合<tr><td>
		StringBuffer bodyHtml=new StringBuffer();
		LazyDynaBean abean=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
			try {
				this.frowset=dao.search(sql);
				ResultSetMetaData data =this.frowset.getMetaData();
				
				while(this.frowset.next()){
					if(this.frowset.getRow()<start+1)
						continue;
					if(this.frowset.getRow()>start+Integer.parseInt(pagecount))
						break;
					abean=new LazyDynaBean();
					
					RecordVo vo = new RecordVo("tt_organization");
					String uid = String.valueOf(this.frowset.getInt("unitid"));
					vo.setString("unitid",uid);
					abean.set("unitid", uid);
					StringBuffer temp = new StringBuffer(this.frowset.getString("unitname"));
					/*if(temp.length()>=25){
						temp.insert(25,"<br>");
					}*/
					vo.setString("unitname" ,temp.toString());
					abean.set("unitname", temp.toString());
					String unitcode = this.frowset.getString("unitcode");
					vo.setString("unitcode" , unitcode );
					abean.set("unitcode",unitcode);
					String reporttypes = this.frowset.getString("reporttypes");
					
					//System.out.println("reporttypes=" + reporttypes);
					
					if(reporttypes == null || "".equals(reporttypes)){
						vo.setString("reporttypes" , "");
						abean.set("reporttypes","");
					}else{
						vo.setString("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
						abean.set("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
					}
					String un = this.getUserName(unitcode);
					
					//System.out.println("un=" + un);
					
					if(un==null|| "".equals(un)){
						vo.setString("b0110","");
						abean.set("b0110","");
					}else{
						vo.setString("b0110","("+un.substring(0,un.length()-1)+")");
						abean.set("b0110","("+un.substring(0,un.length()-1)+")");
					}
					String a0000 = this.frowset.getString("a0000");
					
					//System.out.println("reporttypes=" + reporttypes);
					
					if(a0000 == null || "".equals(a0000)){
						vo.setString("a0000" , "");
						abean.set("a0000","");
					}else{
						vo.setString("a0000",a0000);
						abean.set("a0000",a0000);
					}
					
					abean.set("pages", pages);
					abean.set("counts",count);
					
					list.add(abean);
		
	}
		this.frowset=dao.search(sql);
		while(this.frowset.next()){
			abean=new LazyDynaBean();
			
			RecordVo vo = new RecordVo("tt_organization");
			String uid = String.valueOf(this.frowset.getInt("unitid"));
			vo.setString("unitid",uid);
			abean.set("unitid", uid);
			StringBuffer temp = new StringBuffer(this.frowset.getString("unitname"));
			/*if(temp.length()>=25){
				temp.insert(25,"<br>");
			}*/
			vo.setString("unitname" ,temp.toString());
			abean.set("unitname", temp.toString());
			String unitcode = this.frowset.getString("unitcode");
			vo.setString("unitcode" , unitcode );
			abean.set("unitcode",unitcode);
			String reporttypes = this.frowset.getString("reporttypes");
			
			//System.out.println("reporttypes=" + reporttypes);
			
			if(reporttypes == null || "".equals(reporttypes)){
				vo.setString("reporttypes" , "");
				abean.set("reporttypes","");
			}else{
				vo.setString("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
				abean.set("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
			}
			String un = this.getUserName(unitcode);
			
			//System.out.println("un=" + un);
			
			if(un==null|| "".equals(un)){
				vo.setString("b0110","");
				abean.set("b0110","");
			}else{
				vo.setString("b0110","("+un.substring(0,un.length()-1)+")");
				abean.set("b0110","("+un.substring(0,un.length()-1)+")");
			}
			String a0000 = this.frowset.getString("a0000");
			
			//System.out.println("reporttypes=" + reporttypes);
			
			if(a0000 == null || "".equals(a0000)){
				vo.setString("a0000" , "");
				abean.set("a0000","");
			}else{
				vo.setString("a0000",a0000);
				abean.set("a0000",a0000);
			}
			
			list2.add(abean);
		
	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			bodyHtml.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
			bodyHtml.append("<tr>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append("<input type='checkbox' name='selbox' onclick=batch_select(this,'reportUnitListForm.select'); title='全选'>");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("orglist.reportunitlist.codename")+"&nbsp;");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("orglist.reportunitlist.code")+"&nbsp;");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("orglist.reportunitlist.edit"));
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("orglist.reportunitlist.tsortcarveup"));
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("orglist.reportunitlist.reportprincipal"));
			bodyHtml.append("</td>");
			bodyHtml.append("<hrms:priv func_id='2905006'>");
			bodyHtml.append("<td align='center' class='TableRow' nowrap>");
			bodyHtml.append(ResourceFactory.getProperty("label.zp_exam.sort"));
			bodyHtml.append("</td>");
			bodyHtml.append("</hrms:priv>");
			bodyHtml.append("</tr>");
			bodyHtml.append("</thead>");
			for(int i=0;i<list.size();i++){
			LazyDynaBean bean =	(LazyDynaBean)list.get(i);
//			bodyHtml.append("<hrms:extenditerate id='element' name='searchReportUnitForm' property='reportUnitListForm.list' ");
//			bodyHtml.append("indexes='indexes'  pagination='reportUnitListForm.pagination' pageCount='15' scope='session'>");
			//bodyHtml.append("\r\n");
			
			bodyHtml.append("<div id='selected"+i+"'>");
			
			if (i % 2 == 0) {
				bodyHtml.append("<tr class='trShallow'>");
			}
						 else {
								bodyHtml.append("<tr class='trDeep'>");	
						}
			bodyHtml.append("<td align='center' class='RecordRow' nowrap>");
			bodyHtml.append("<input type='checkbox' name='reportUnitListForm.select["+i+"]' value='true'>");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='left' class='RecordRow' style='word-wrap:break-word; word-break:break-all;' nowrap> ");
			
			bodyHtml.append("&nbsp;"+bean.get("unitname")+"&nbsp;");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='left' class='RecordRow' nowrap>");
			bodyHtml.append("&nbsp;"+bean.get("unitcode")+"&nbsp;");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='center' class='RecordRow' nowrap> ");
			bodyHtml.append("<hrms:priv func_id='2905003'>");
			bodyHtml.append("<a href='/report/org_maintenance/reportunitupdate.do?b_updates=link&unitid="+bean.get("unitid")+ "'target='mil_body'><img src='../../images/edit.gif' width='11' height='17' border=0></a> ");
			bodyHtml.append("</hrms:priv>");
			bodyHtml.append("</td>");
			bodyHtml.append("<td align='left' class='RecordRow' nowrap> ");
			bodyHtml.append("	&nbsp;<a href='/report/org_maintenance/reportunitlist.do?b_typelist=link&rtunitcode="+bean.get("unitcode") + "'  >");
			bodyHtml.append("<img src='../../images/edit.gif' width='11' height='17' border=0></a> ");
			bodyHtml.append(bean.get("reporttypes"));
			bodyHtml.append("</td> ");
			bodyHtml.append("<td align='left' class='RecordRow' nowrap>");
			bodyHtml.append("&nbsp;<a href=javaScript:reportUser('"+bean.get("unitcode")+ "')>");
			bodyHtml.append("<img src='../../images/edit.gif' width='11' height='17' border=0></a> ");
			bodyHtml.append(bean.get("b0110"));
			bodyHtml.append("</td> ");
			bodyHtml.append("<hrms:priv func_id='2905006'>");
			bodyHtml.append("<td align='center' class='RecordRow' nowrap>");
			bodyHtml.append("&nbsp;<a href=javaScript:upItem('"+bean.get("counts")+"','"+bean.get("unitcode").toString().trim()+"','"+bean.get("a0000").toString().trim()+"','"+page+"','"+pagecount+"','"+i+"','"+bean.get("pages")+"')>");
			bodyHtml.append("<img src='../../images/up01.gif'  border=0></a> ");
			bodyHtml.append("&nbsp;<a href=javaScript:downItem('"+bean.get("counts")+"','"+bean.get("unitcode").toString().trim()+"','"+bean.get("a0000").toString().trim()+"','"+page+"','"+pagecount+"','"+i+"','"+bean.get("pages")+"')>");
			bodyHtml.append("<img src='../../images/down01.gif'  border=0></a> ");
			bodyHtml.append("</td> ");
			bodyHtml.append("</hrms:priv>");
			bodyHtml.append("</tr> ");
			bodyHtml.append("	</div>");
			}
			bodyHtml.append("</table>");
			//System.out.println(bodyHtml.toString());
			this.getFormHM().put("bodyHtml", bodyHtml.toString());
			this.getFormHM().put("reportUnitList2",list2);
			this.getFormHM().put("reportUnitList",list);
			this.getFormHM().put("parentCode",parentidcode);
	}
		public String getUserName(String uid){
			StringBuffer userName = new StringBuffer();;
			String sql="select fullname,username from operuser where unitcode='"+uid+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			RowSet rs;
			try {
				rs = dao.search(sql);
				String un="";
				while(rs.next()){
					if(rs.getString("fullname")==null||rs.getString("fullname").trim().length()==0){
						un=rs.getString("username");
					}else{
						un = rs.getString("fullname");
					}
					userName.append(un);
					userName.append(",");
				}	
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return userName.toString();
		}
		
		public void updateA0000(String unitcode , int a0000,String sql){
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			RowSet rs;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					
					int a00001  = rs.getInt("a0000");
					String unitcode1 =rs.getString("unitcode");
					String upadatesql = " update tt_organization set a0000="+a00001+" where unitcode='"+unitcode+"'";
					dao.update(upadatesql);
					upadatesql = " update tt_organization set a0000="+a0000+" where unitcode='"+unitcode1+"'";
					dao.update(upadatesql);
				}else{
					
				}	
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}

}
