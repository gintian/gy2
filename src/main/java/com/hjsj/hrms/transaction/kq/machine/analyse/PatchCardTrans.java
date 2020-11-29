package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:数据处理补签功能</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 3, 2010:3:27:44 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class PatchCardTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String table=gettableName(this.userView);
			String date=(String) this.getFormHM().get("specdata");
			date = PubFunc.keyWord_reback(date);
			String str[]= date.split("/");
			StringBuffer sql = new StringBuffer();
			String column="nbase,a0100,a0101,b0110,e0122,card_no,g_no";
			String statr_date=(String)hm.get("start_date"); //开始时间
			String end_date_patch=(String)hm.get("end_date");  //结束时间
			statr_date=statr_date.replaceAll("-","\\.");
			end_date_patch=end_date_patch.replaceAll("-","\\.");
			String a0100NbaseStr = "";
			StringBuffer sql1 = new StringBuffer();
			
			//取第一条记录的日期作为补签页面默认日期
			String defDate = "";
			for(int i = 0;i<str.length;i++)
			{
				String value=str[i];
				String strvalue[]=value.split(",");
				String nbase="";
				String a0100="";
				String q03z0="";
				for(int p = 0;p<strvalue.length;p++)
				{
					if(p==0)
						nbase=strvalue[0]; 
					if(p==1)
						a0100=strvalue[1];
					if(p==2) {
						q03z0=strvalue[2];
						if("".equals(defDate))
						    defDate = q03z0.replace("'", "");
					}
				}
				sql1.setLength(0);
				sql1.append("select "+column+" from "+table);
				sql1.append(" where q03z0="+q03z0+" and nbase="+nbase+" and a0100="+a0100+"");
				sql1.append(" and (isok like '%旷工%')");
				
				frowset = dao.search(sql1.toString());
				if (frowset.next() && a0100NbaseStr.indexOf(a0100 + nbase) == -1) {
					if(a0100NbaseStr.length() == 0)
					{
						sql.append("select "+column+" from "+table);
						sql.append(" where (q03z0="+q03z0+" and nbase="+nbase+" and a0100="+a0100+"");
						sql.append(" and (isok like '%旷工%'))");
					}else
					{
						//sql.append(" union all select "+column+" from "+table);
						sql.append(" or (q03z0="+q03z0+" and nbase="+nbase+" and a0100="+a0100+"");
						sql.append(" and (isok like '%旷工%'))");
					}
					a0100NbaseStr += a0100 + nbase + "`";
				}
			}
			if (!(sql.length() >0) )
			{
				throw new GeneralException(ResourceFactory.getProperty("kq.machine.analyse.patchcard"));
			}
			StringBuffer sql2=new StringBuffer();
				sql2.append("select Distinct "+ column +" from (");
				sql2.append(sql.toString());
				sql2.append(")aa ");
			
			
			String querySql = PubFunc.keyWord_reback(sql2.toString());
			/**补刷原因**/
			String card_causation= KqParam.getInstance().getCardCausation();
			if(card_causation==null||card_causation.length()<=0)
			{
				card_causation="";
			}
			this.getFormHM().put("card_causation",card_causation);//补刷卡原因代码项
			/****结束****/
			this.getFormHM().put("sqlstrpatch",querySql.toString());
			this.getFormHM().put("columnpatch", column);
			this.getFormHM().put("orderpatch", "order by a0100");
			this.getFormHM().put("repair_flag","0");
			if("".equals(defDate))
			    this.getFormHM().put("statr_date", statr_date);
			else
			    this.getFormHM().put("statr_date", defDate);
			this.getFormHM().put("end_date_patch", end_date_patch);
			this.getFormHM().put("repair_fashion", "0");
			this.getFormHM().put("into_flag", "1");  //进出标示
			this.getFormHM().put("causation", "");  //补刷原因
			
			if("".equals(defDate))
			    this.getFormHM().put("jddate", statr_date);
            else
                this.getFormHM().put("jddate", defDate);
			
			this.getFormHM().put("class_flag", "0");
			this.getFormHM().put("cycle_date",statr_date); //复杂规则循环时间
			this.getFormHM().put("cycle_num","1");  //复杂规则
			this.getFormHM().put("cycle_hh","00");  //复杂规则
			this.getFormHM().put("cycle_mm","00");  //复杂规则
			/****/
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String gettableName(UserView userView)
	{
		String name="";
		String mark=KqParam.getInstance().getData_processing();
		mark=mark!=null&&mark.length()>0?mark:"0";
		if("1".equalsIgnoreCase(mark))
		{
			name="kq_analyse_result";
		}else
		{
			name="kt_"+userView.getUserName()+"_dd";
		}
		return name;
	}
}
