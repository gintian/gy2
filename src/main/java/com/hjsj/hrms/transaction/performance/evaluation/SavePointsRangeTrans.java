package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SavePointsRangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList rangelist=new ArrayList();
		rangelist=(ArrayList)this.getFormHM().get("rangelist");
		String tem="";
		String isvalidate=(String)this.getFormHM().get("isvalidate");
		if(isvalidate==null||isvalidate.trim().length()==0){
			isvalidate="false";
		}
		String planid=(String)this.getFormHM().get("planid");
		ArrayList effectivelist=new ArrayList();
		for(int i=0;i<rangelist.size();i++){
			
			LazyDynaBean bean=new LazyDynaBean();
			bean=(LazyDynaBean)rangelist.get(i);
			String id=(String)bean.get("point_id");
			String upvalue=(String)bean.get("maxscore");
			String downvalue=(String)bean.get("minscore");
			if((upvalue!=null&&upvalue.trim().length()!=0)||(downvalue!=null&&downvalue.trim().length()!=0)){
				effectivelist.add(bean);
				
			}
			
		}
		PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
		pe.savePointScoprRange(effectivelist, isvalidate);
		this.getFormHM().put("isvalidate", isvalidate);
		
	}

}
