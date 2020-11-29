package com.hjsj.hrms.transaction.info;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 查询不同子集中的写权限指标
 * @author xujian
 *Apr 21, 2010
 */
public class QueryBatchFieldInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		String selectlist = (String)this.getFormHM().get("selectlist");
		String secondlist = (String)this.getFormHM().get("secondlist");
		ArrayList fieldList = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		StringBuffer sb = new StringBuffer();
		Connection conn = this.getFrameconn();
		String fieldsetdesc="";
		try{
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select fieldsetdesc from fieldSet where fieldsetid='"+fieldsetid+"'";//获取指标集编码对应的名称
			RowSet rs = dao.search(sql);
			if(rs.next()){
				fieldsetdesc=rs.getString("fieldsetdesc");
			}
			fieldsetid=fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"A01";
			fieldList =this.userView.getPrivFieldList(fieldsetid);
			if(fieldList!=null)
			for(int i=0;i<fieldList.size();i++){
				FieldItem fi = (FieldItem)fieldList.get(i);
				if("1".equalsIgnoreCase(this.userView.analyseFieldPriv(fi.getItemid()))){//读权限
					continue;
				}
				/*if(i%2==0){
					sb.append("<tr class='trShallow'>");
				}else{
					sb.append("<tr class='trDeep'>");
				}
				sb.append("<td align=\"left\" class=\"RecordRow\" 	style=\"word-break:break-all;\"  nowrap>&nbsp;"+(i+1)+"</td>");
				sb.append("<td align=\"left\" class=\"RecordRow\" 	style=\"word-break:break-all;\"  nowrap>&nbsp;"+fi.getItemdesc()+"</td>");
				sb.append(" <td align=\"center\" class=\"RecordRow\" nowrap>");
				sb.append("<input type=\"checkbox\" name=\"selectflag\" value=\""+fi.getItemid()+"\">&nbsp;");
				sb.append("</td>");
				sb.append("</tr>");*/
				LazyDynaBean ld = new LazyDynaBean();
				ld.set("itemdesc", fi.getItemdesc());
				ld.set("itemid", fi.getItemid());
				fieldlist.add(ld);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//this.getFormHM().put("targethtml", /*SafeCode.encode(sb.toString())*/sb.toString());
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("selectlist", selectlist);
			this.getFormHM().put("secondlist", secondlist);
			this.getFormHM().put("fieldsetdesc", fieldsetdesc);
		}
	}

}
