package com.hjsj.hrms.transaction.train.job;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainMovementDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String r3101=(String)hm.get("r3101");
			r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
			String operator=(String)hm.get("operator");
			TrainClassBo trainClassBo=new TrainClassBo(this.getFrameconn());
			
			ArrayList list =new ArrayList();
			StringBuffer buf = new StringBuffer();
			StringBuffer wherestr = new StringBuffer();
			buf.append("select ");
			StringBuffer columns = new StringBuffer();
			ArrayList fieldlist = DataDictionary.getFieldList("r41",Constant.USED_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
	        FieldItem fieldItem = (FieldItem) fieldlist.get(i);
				if ("R4104".equalsIgnoreCase(fieldItem.getItemid()) 
						|| "R4103".equalsIgnoreCase(fieldItem.getItemid())
						|| "R4108".equalsIgnoreCase(fieldItem.getItemid())
						|| "R4110".equalsIgnoreCase(fieldItem.getItemid())
						|| "R4116".equalsIgnoreCase(fieldItem.getItemid())
						|| "R4110 ".equalsIgnoreCase(fieldItem.getItemid())
						|| "0".equalsIgnoreCase(fieldItem.getState()))
	            continue;
				
				buf.append(fieldItem.getItemid());
				columns.append(fieldItem.getItemid());
				buf.append(",");
				columns.append(",");
				if("r4101".equalsIgnoreCase(fieldItem.getItemid())){
					list.add(0,fieldItem);
				}else
					list.add(fieldItem);
			}
			
			String lesson = showPush();
			String[] lessons = lesson.split(":");
			if ((!"R41".equals(lessons[0]))&&lessons[0].length()>0&&lessons.length>0) {
				FieldItem fi = DataDictionary.getFieldItem(lessons[1], lessons[0]);
				if ("A".equals(fi.getItemtype()) && "50".equals(fi.getCodesetid())) {
					list.add(fi);
					buf.append(lessons[0] + "." + lessons[1] + " as " + lessons[1] + ",");
					columns.append(fi.getItemid() + ",");
					if ("R13".equals(lessons[0])) {
						wherestr.append(" from r41 left join r13 on r41.r4105=r13.r1301 where r41.r4103='");
						wherestr.append(r3101);
						wherestr.append("'");
					}
					if ("R07".equals(lessons[0])) {
						wherestr.append(" from r41 left join r07 on r41.r4114=r07.r0701 where r41.r4103='");
						wherestr.append(r3101);
						wherestr.append("'");
					}
				}

			}else{
				wherestr.append(" from r41 where r4103='");
				wherestr.append(r3101);
				wherestr.append("'");
			}
			
			
			this.getFormHM().put("r41list",list);
			this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
			this.getFormHM().put("wherestr",wherestr.toString());
			this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
			if(lessons.length<=1)
				this.getFormHM().put("lesson", "");
			else
				this.getFormHM().put("lesson", lessons[1].toLowerCase());
			
			this.getFormHM().put("trainClassDesc",trainClassBo.getTrainClassDesc(r3101));
			this.getFormHM().put("operator",operator);
			SafeCode.decode("");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	private String showPush() {
		String pushitem = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		// 只取一个指标优先级顺序 培训课程r41,培训项目r13或培训资料r07 中有关联表r50的代码指标
		String sql = "select FieldSetId,itemid from t_hr_busifield b left join t_hr_relatingcode r on b.codesetid=r.codesetid where codetable = 'R50' and FieldSetId in ('R13','R07','R41') and codeflag=1 and useflag=1 order by FieldSetId desc";
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next())// 如果培训项目和培训资料中都有关联在线课程指标 则根据培训资料显示
				pushitem = this.frowset.getString("FieldSetId") + ":"
						+ this.frowset.getString("itemid");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return pushitem;
	}

}
