package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveStatItemTrans extends IBusiness
{
	public void execute() throws GeneralException 
    {
		ArrayList arr=(ArrayList)this.getFormHM().get("arr");
		if(arr==null)
			throw GeneralExceptionHandler.Handle(new GeneralException("统计条件不能为空！"));
		String flagtype=(String)this.getFormHM().get("flagtype");
		String statid=(String)this.getFormHM().get("statid");
		String history=(String)this.getFormHM().get("history");
		String texts=(String)this.getFormHM().get("texts");
		texts=PubFunc.keyWord_reback(texts);
		String titles=(String)this.getFormHM().get("titles");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(flagtype!=null&& "edit".equalsIgnoreCase(flagtype))
		{
			try 
			{
				String editid=(String)this.getFormHM().get("editid");
				StringBuffer sql=new StringBuffer();
				sql.append("update hr_hisdata_slegend set ");
				sql.append("Legend='"+titles+"'");			
				sql.append(",Lexpr='"+texts+"'");
				String factor=getFactor(arr);
				sql.append(",Factor='"+factor+"'");
				sql.append(" where id='"+statid+"'");
				sql.append(" and nOrder='"+editid+"'");
				dao.update(sql.toString());
				texts=PubFunc.toGet(texts);
				factor=PubFunc.toGet(factor);
				factor=factor.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("&lt;","<").replaceAll("&gt;",">");
				texts = URLEncoder.encode(texts);
				factor = URLEncoder.encode(factor);
				/*try {
					
					//factor=new String(factor.toString().getBytes("GBK"),"ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				StringBuffer action=new StringBuffer();
				action.append("statshow.do?b_data=data&statid=" + statid);
				action.append("&norder="+editid+"&flag=1&strlexpr=" + texts);
				
				
				action.append( "&strfactor=" + factor+"&history="+history+"&showflag=");
				this.getFormHM().put("legend", titles);
				this.getFormHM().put("action", action.toString());
				this.getFormHM().put("uid", "");
				this.getFormHM().put("opflag", "true");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.getFormHM().put("opflag", "false");
				throw GeneralExceptionHandler.Handle(new GeneralException("修改统计条件出错！"));
			}
		}else if(flagtype!=null&& "new".equalsIgnoreCase(flagtype))
		{
			
	    	try {
	    		String norder=getMaxId(statid);
				StringBuffer sql=new StringBuffer();
				sql.setLength(0);
				sql.append("insert into hr_hisdata_slegend(Id,nOrder,Legend,LExpr,Factor)values(?,?,?,?,?)");
				ArrayList paralist=new ArrayList();
				paralist.add(new Integer(statid));
		    	paralist.add(new Integer(norder));	    	
		    	paralist.add(titles);//09年3.3修改，保存覆盖后，只有一条记录
		    	paralist.add(texts);
		    	String factor=getFactor(arr);
		    	if(factor==null||factor.length()<=0)
		    		factor="";
		    	paralist.add(factor);
				dao.update(sql.toString(),paralist);
				texts=PubFunc.toGet(texts);
				factor=PubFunc.toGet(factor);
				factor=factor.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("&lt;","<").replaceAll("&gt;",">");
				texts = URLEncoder.encode(texts);
				factor = URLEncoder.encode(factor);
				/*try {
					
					factor=new String(factor.toString().getBytes("GBK"),"ISO-8859-1");
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				StringBuffer action=new StringBuffer();
				action.append("statshow.do?b_data=data&statid=" + statid);
				action.append("&norder="+norder+"&flag=1&strlexpr=" + texts);	
				action.append( "&strfactor=" + factor+"&history="+history+"&showflag=");
				String uid=statid+norder;
				this.getFormHM().put("uid", uid);
				this.getFormHM().put("legend", titles);
				this.getFormHM().put("action", action.toString());
				this.getFormHM().put("opflag", "true");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.getFormHM().put("opflag", "false");
				throw GeneralExceptionHandler.Handle(new GeneralException("新增统计条件出错！"));
			}
		}
	}
	/**
	 * 组合条件
	 * @param arrList
	 * @return
	 */
    private String getFactor(ArrayList arrList)
    {
    	StringBuffer factor=new StringBuffer();
    	if(arrList==null)
    		return "";
    	for(int i=0;i<arrList.size();i++)
    	{
    		LazyDynaBean bean=(LazyDynaBean)arrList.get(i);
    		if(bean==null)
    			continue;
    		String fieldname=(String)bean.get("fieldname");
    		String oper=(String)bean.get("oper");
    		oper=PubFunc.keyWord_reback(oper);
    		String value=(String)bean.get("value");
    		if(value==null||value.length()<=0)
    			value="Null";
    		factor.append(fieldname);
    		factor.append(oper);
    		factor.append(value);
    		factor.append("`");
    	}
    	if(factor.length()>0)
    		factor.setLength(factor.length()-1);
    	return factor.toString();
    }
    private String getMaxId(String id)throws GeneralException
	{
		int nid=-1;
		StringBuffer sql=new StringBuffer("select max(norder)+1 as nmax from hr_hisdata_slegend where ");
		sql.append(" id='"+id+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				nid=this.frowset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(nid);
	}
}
