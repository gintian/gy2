package com.hjsj.hrms.module.template.templatetoolbar.jobtitle;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *  
 * <p>Title:InitReViewListTrans.java</p>
 * <p>Description:初始化评审会议列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:2015-10-19 上午09:42:00 
 * @version 7.x
 */
public class InitReViewListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{ 
			ArrayList datelist=getDateList();			
			this.getFormHM().put("datelist",datelist);		
		}
		catch(Exception e)
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public ArrayList getDateList()
	{
		ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
		RowSet rset = null;
		try
		{
			StringBuffer buf=new StringBuffer();
			// 评审会议状态： 执行中 
			buf.append("select * from w03 where W0321 in ('05') and ");
			//加上开始时间和结束时间限制，没设开始和结束时间则不考虑 haosl 2017-10-11 add
			String format ="yyyy-MM-dd";
			buf.append("("+Sql_switcher.dateToChar(Sql_switcher.sqlNow(), format));
			buf.append("between (case when w0309 is null then "+Sql_switcher.dateToChar(Sql_switcher.sqlNow(), format)+" else "+Sql_switcher.dateToChar("w0309", format)+" end)  ");
			buf.append("and (case when w0311 is null then "+Sql_switcher.dateToChar(Sql_switcher.sqlNow(), format)+" else "+Sql_switcher.dateToChar("w0311", format)+" end)) ");
			// 评审会议限制业务范围 chent 20160310 start
			String b0110 = this.userView.getUnitIdByBusi("9");//取得所属单位
			if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空则取本级，下级。为空则为最高权限
				String[] b0110Array = b0110.split("`");
				
				buf.append("and (");
				
				for(int i=0; i<b0110Array.length; i++){
					String b = b0110Array[i].substring(2);
					
					buf.append("b0110 like '"+b+"%' or ");//本级、下级
				}
				buf.append("NULLIF(b0110,'') is NULL");
				buf.append(") ");
			}
			// 评审会议限制业务范围 chent 20160310 end
			
			buf.append(" order by W0309 desc");
	
			ContentDAO dao=new ContentDAO(this.frameconn);
			rset = dao.search(buf.toString());
			Map<String,String> map = null;
			while(rset.next())
			{
				String w0301=PubFunc.encrypt(rset.getString("w0301"));//rset.getString("w0301") w0301加密    haosl update 20170426
				String w0303=rset.getString("w0303");
				String sub_committee_id = rset.getString("sub_committee_id");
				map = new HashMap<String,String>();
				map.put("w0301", w0301);
				map.put("w0303", w0303);
				if(StringUtils.isNotBlank(sub_committee_id))
					map.put("sub_committee_id", PubFunc.encrypt(sub_committee_id));
				list.add(map);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(rset);
		}
		return list;
	}

}
