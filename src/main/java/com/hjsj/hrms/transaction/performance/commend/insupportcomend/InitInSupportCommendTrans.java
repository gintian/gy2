package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitInSupportCommendTrans extends IBusiness {
	public void execute() throws GeneralException {
		try {
			StringBuffer sb = new StringBuffer();
			ArrayList list = DataDictionary.getFieldList("P02",Constant.USED_FIELD_SET);
			ArrayList commendList = new ArrayList();
//			int extendattrflag=0;
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				Field field = (Field) item.cloneField();
				if("p0201".equalsIgnoreCase(field.getName()))
					field.setVisible(false);
				/**状态指标为只读*/
				if("p0209".equalsIgnoreCase(field.getName()))
					field.setReadonly(true);
				field.setSortable(true);
				/**推荐参数*/
				if("extendattr".equalsIgnoreCase(field.getName())){
//					extendattrflag=1;
//					field.setReadonly(true);
//					if(!this.getUserView().hasTheFunction("0D4102"))
//						field.setVisible(false);
					continue;
				}
				if("p0209".equalsIgnoreCase(field.getName()))
					commendList.add(0,field);
				else
				    commendList.add(field);
				if(!item.isVisible())
					field.setVisible(false);
				
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			StringBuffer sql2 = new StringBuffer();
			for (int j = 0; j < list.size(); j++) {
				FieldItem aitem = (FieldItem) list.get(j);
				String temp = aitem.getItemid();
				
				sql2.append("," + temp);
			}
			DbWizard db=new DbWizard(this.getFrameconn());
			if(!db.isExistField("p02", "extendattr",false)){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update("alter table p02 add extendattr text");
				this.frecset=dao.search("select itemid from t_hr_busifield where fieldsetid='P02' and itemid='extendattr'");
				if(!this.frecset.next()){
					String sql0="insert into t_hr_busifield(fieldsetid, itemid, displayid, itemtype, itemdesc,itemlength, decimalwidth, codesetid, displaywidth,state,useflag, keyflag, codeflag,ownflag) values(";
					sql0+="'P02','extendattr',8,'M','推荐参数',0,0,0,10,1,1,0,0,1)";
					dao.update(sql0);
				}
			}
			//if(extendattrflag==0){
				Field field = new Field("extr","推荐参数");
				field.setReadonly(true);
				field.setSortable(true);
				if(!this.getUserView().hasTheFunction("0D4102"))
					field.setVisible(false);
				commendList.add(field);
				sql2.append(",'' extr");
			//}
			
			Field newField = new Field("b", "提名候选人");
			newField.setReadonly(true);
			newField.setSortable(true);
			commendList.add(newField);
			
			sql.append(sql2.toString().substring(1));
			sql.append(",p0203 b from p02 order by p02.p0201");
			String tabname = "p02";
			this.getFormHM().put("tabname", tabname);
			this.getFormHM().put("sql", sql.toString());
			this.getFormHM().put("commendList", commendList);
			this.getFormHM().put("state","00");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
