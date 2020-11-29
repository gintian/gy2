package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class InitEditPageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String p0400=(String)hm.get("p0400");
			String opt=(String)hm.get("operate");  //1:删除调整后的任务 2：编辑调整后的任务
			String status=(String)this.getFormHM().get("status");  //　０:分值模版  1:权重模版
			String itemKind="";
			String itemtype="";
			String fromflag="";
			if("1".equals(opt))
			{
				this.getFormHM().put("adjustDesc", "");
				this.getFormHM().put("adjustBeforePointList", new ArrayList());
			}
			else
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from p04 where p0400="+p0400);
				String plan_id="";
				if(this.frowset.next())
				{
					fromflag=this.frowset.getString("fromflag");
					itemtype=this.frowset.getString("itemtype");
					plan_id=this.frowset.getString("plan_id");
					this.getFormHM().put("adjustDesc", Sql_switcher.readMemo(this.frowset,"p0425"));
					if("0".equals(status))
					{
						DecimalFormat myformat1 = new DecimalFormat("########.####");
						String before_value=this.frowset.getString("p0413")!=null?this.frowset.getString("p0413"):"";
						String after_value=this.frowset.getString("p0421")!=null?this.frowset.getString("p0421"):"";
						
						if(before_value.length()>0)
							before_value=myformat1.format(Double.parseDouble(before_value));
						if(after_value.length()>0)
							after_value=myformat1.format(Double.parseDouble(after_value));
						else if(before_value.length()>0)//默认为标准分值
							after_value=myformat1.format(Double.parseDouble(before_value));
						this.getFormHM().put("before_value",before_value);
						this.getFormHM().put("after_value",after_value);
					}
					else
					{
						DecimalFormat myformat1 = new DecimalFormat("########.####");
						String rank=this.frowset.getString("p0415");
						if(rank==null||rank.trim().length()==0)
							rank="0";
						else
							rank=rank.trim();
						String temp=myformat1.format(Double.parseDouble(rank)*100);
						
						
						String rank2=this.frowset.getString("p0423");
						if(rank2==null||rank2.trim().length()==0)//默认为标准权重
							rank2=rank;//"0";
						else
							rank2=rank2.trim();
						String temp2=myformat1.format(Double.parseDouble(rank2)*100);
						rank2=temp2;
						rank=temp;
						this.getFormHM().put("before_value",rank);
						this.getFormHM().put("after_value",rank2);
					}
					this.getFormHM().put("pointContent", this.frowset.getString("p0407"));
				}
				
				ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.userView,plan_id);
				itemKind=bo.getItemKind(p0400);
				
				
				ArrayList list=DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);
				if("2".equals(opt))
					this.getFormHM().put("adjustBeforePointList", bo.getAdjustBeforePointList(list, p0400));
				else
					this.getFormHM().put("adjustBeforePointList", new ArrayList());
			}
			this.getFormHM().put("itemKind",itemKind);
			if(itemtype==null|| "".equals(itemtype))
				itemtype="0";
			this.getFormHM().put("itemtype",itemtype);
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			this.getFormHM().put("adjustDate", df.format(new Date()));
			this.getFormHM().put("fromflag", fromflag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
