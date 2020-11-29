package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class QueryExamPaperTrans extends IBusiness {

	public void execute() throws GeneralException {

		ArrayList itemlist = new ArrayList();
		//TrainCourseBo tb = new TrainCourseBo(this.userView,this.frameconn);
		try{
			itemlist.add(DataDictionary.getFieldItem("r5301"));
			itemlist.add(DataDictionary.getFieldItem("r5307"));
			itemlist.add(DataDictionary.getFieldItem("r5308"));
			itemlist.add(DataDictionary.getFieldItem("r5304"));
			itemlist.add(DataDictionary.getFieldItem("r5305"));
			itemlist.add(DataDictionary.getFieldItem("r5311"));
			FieldItem item = (FieldItem)(DataDictionary.getFieldItem("r5000").cloneItem());
			item.setItemdesc("所属课程");
			item.setVisible(true);
			item.setCodesetid("0");
			item.setItemtype("A");
			itemlist.add(item);

			String columns="r5300,r5301,r5307,r5308,r5304,r5305,r5311,b0110,r5000";
			String strsql="select r5300,r5301,r5307,r5308,r5304,r5305,r5311,b0110,r5300 r5000";
			StringBuffer strwhere = new StringBuffer("from r53 where 1=1");
			
			if(!this.userView.isSuper_admin()){
				String code="";
				TrainCourseBo tb = new TrainCourseBo(this.userView);
				code = tb.getUnitIdByBusi();
				if(code.indexOf("UN`")==-1){
					String unitarr[] = code.split("`"); 
					String str="";
					for(int i=0;i<unitarr.length;i++){
						if(unitarr[i]!=null&&unitarr[i].trim().length()>2&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))){
								if (i != 0) 
									str+=" or ";
								String tmpb0110 = unitarr[i].substring(2);
								str +="b0110 like '"+tmpb0110+"%' or ";
								//tmpb0110=tb.getSupUnit(tmpb0110,0);//上级单位
								//if(tmpb0110!=null&&tmpb0110.length()>0)
								//	str += tmpb0110;
								str +="b0110=" + Sql_switcher.substr("'"+tmpb0110+"'", "1", Sql_switcher.length("b0110"));
						}
					}
					strwhere.append(" and (");
					if(code.indexOf("UN`")==-1){
						if(str.length()>0){
							//str = str.replaceAll("r5020", "b0110");
							strwhere.append(""+str/*.substring(0, str.lastIndexOf("or")-1)*/+" or b0110 = '' or b0110 is null");
						}else
				    		strwhere.append("b0110 = '' or b0110 is null");
					}
					strwhere.append(" or r5313=1)");//公开试卷
				}
			}
			String r5307 = (String)this.getFormHM().get("r5307");
			if(r5307!=null&&r5307.length()>0)
				strwhere.append(" and r5307 ='"+r5307+"'");
			String r5308 = (String)this.getFormHM().get("r5308");
			if(r5308!=null&&r5308.length()>0)
				strwhere.append(" and r5308 ='"+r5308+"'");
			String r5301 = (String)this.getFormHM().get("r5301");
			if(r5301!=null&&r5301.length()>0)
				strwhere.append(" and r5301 like '%"+r5301+"%'");
			
			getStateInfo(strwhere.toString());
			
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("strsql", strsql);
			this.getFormHM().put("strwhere", strwhere.toString());
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("itemlist", itemlist);
			this.getFormHM().put("order_by", " order by norder");
		}
	}
	
	private void getStateInfo(String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r5300 "+strwhere;
		try {
			this.frowset = dao.search(sql+" order by norder");
			if(this.frowset.next())
				this.getFormHM().put("start", this.frowset.getInt("r5300")+"");
			
			this.frowset = dao.search(sql+" order by norder desc");
			if(this.frowset.next())
				this.getFormHM().put("end", this.frowset.getInt("r5300")+"");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
