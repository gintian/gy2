package com.hjsj.hrms.transaction.train.report.lessonAnalyse;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchLessonAnalyseTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String a_code = (String) hm.get("a_code");
    	String r5000 = (String)this.getFormHM().get("r5000");
    	String classViewvalue = (String)this.getFormHM().get("classViewvalue");
    	String classValue = (String)this.getFormHM().get("classValue");
    	this.formHM.remove("classViewvalue");
    	r5000 = PubFunc.keyWord_reback(r5000);
    	String name = (String)this.getFormHM().get("name");
    	String lprogress_t = (String)this.getFormHM().get("lprogress_t");
    	lprogress_t = lprogress_t==null||lprogress_t.length()<1?"0":lprogress_t;
    	String lprogress_d = (String)this.getFormHM().get("lprogress_d");
    	lprogress_d = lprogress_d==null||lprogress_d.length()<1?"100":lprogress_d;
    	
    	//id，人员库，学员编号，单位，部门，岗位，姓名，初试时间，结束时间，学习时长，学习次数，学习进度
		String columns="id,nbase,a0100,b0110,e0122,e01a1,a0101,r5003,start_date,end_date,learnedhour,learnednum,lprogress,tr_selfscore";
		String strsql = "select id,t.nbase,t.a0100,b0110,e0122,e01a1,a0101,r5003,"+Sql_switcher.dateToChar("start_date", "yyyy-MM-dd")+" start_date,"+Sql_switcher.dateToChar("end_date", "yyyy-MM-dd")+" end_date,learnedhour,learnednum,lprogress,"+Sql_switcher.isnull(floatTochar("s.score","9999.99"),"'0.00'")+" tr_selfscore";
		StringBuffer strwhere = new StringBuffer(" from tr_selected_lesson t left join R50 r on r.R5000=t.R5000  ");
		strwhere.append(" left join (select  max(score) score,a0100,nbase,tp.R5000  from tr_selfexam_paper trp left join  tr_lesson_paper tp on tp.r5300 = trp.r5300  group by tp.R5000,a0100,nbase) s on s.r5000 =  t.r5000 and s.nbase = t.nbase and s.a0100 = t.a0100 where ");
		TrainCourseBo bo = new TrainCourseBo(this.userView,this.getFrameconn());
		if((!this.userView.isSuper_admin())&&(a_code==null||a_code.length()<3)){
			String priv = bo.getUnitIdByBusi();
			if(priv!=null&&priv.length()>2){
				String tmpwhere = bo.getPrivSql("", priv);
				if(tmpwhere.length()>0){
					strwhere.append("("+tmpwhere+")");
				}else
					strwhere.append("1=1");
			}else
				throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
		}else if(a_code!=null&&a_code.length()>2){
			String codeid = a_code.substring(0,2);
			String codesetid = a_code.substring(2);
			if("UN".equalsIgnoreCase(codeid))
				strwhere.append("b0110 like '"+codesetid+"%'");
			else if("UM".equalsIgnoreCase(codeid))
				strwhere.append("e0122 like '"+codesetid+"%'");
			else if("@K".equalsIgnoreCase(codeid))
				strwhere.append("e01a1 like '"+codesetid+"%'");
			else if(a_code.length()>3)
				strwhere.append(" t.nbase='"+a_code.substring(0,3)+"' and t.a0100='"+a_code.substring(3)+"'");
		}else
			strwhere.append("1=1");
		
		if(r5000!=null&&r5000.length()>0&&!"#".equals(r5000))
			strwhere.append(" and r.r5000="+r5000);
		
		if(name!=null&&name.length()>0)
			strwhere.append(" and a0101 like '%"+name+"%'");
		
		if(classViewvalue!=null&&classViewvalue.length()>0){
		    strwhere.append( " AND r.R5000 IN (SELECT R5000 FROM R50 ");
		strwhere.append(" WHERE R5004 IN (SELECT codeitemid FROM codeitem A WHERE A.codesetid ='55' AND codeitemdesc LIKE '%"+classViewvalue+"%') ");
		strwhere.append(" OR codeitemid in (SELECT codeitemid FROM codeitem A WHERE A.codesetid ='55' AND codeitemdesc LIKE '%"+classViewvalue+"%')) ");
		}
		try{
			int start = Integer.parseInt(lprogress_t); 
			int end = Integer.parseInt(lprogress_d); 
			if(end < start){
				end = end + start;
				start = end - start;
				end = end - start;
			}
			if(start > 0)
				strwhere.append(" and lprogress>="+start);
			if(end < 100)
				strwhere.append(" and lprogress<="+end);
		}catch (Exception e) {}
		String sqlstr = strsql + strwhere.toString() + " order by b0110,e0122,e01a1,t.a0100";
		//xiexd 2014.09.24将sql保存至服务器
		this.getUserView().getHm().put("key_train_sql1", sqlstr);
		this.formHM.put("itemlist", bo.getCourseList("04,09", true));
		this.getFormHM().put("lprogress_t", lprogress_t);
		this.getFormHM().put("lprogress_d", lprogress_d);
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", " order by b0110,e0122,e01a1,t.a0100");
    }
    
    private String floatTochar(String itemid,String f){
        StringBuffer strvalue = new StringBuffer();
        switch (Sql_switcher.searchDbServer())
        {
            case 1:
                strvalue.append("CAST(");
                strvalue.append(itemid);
                strvalue.append(" AS NUMERIC(8,2))");
                break;
            case 2:
                strvalue.append("TRIM(TO_CHAR(");
                strvalue.append(itemid);
                strvalue.append(",'"+f+"'))");
                break;
            case 3:
                strvalue.append("CHAR(INT(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
        }
        return strvalue.toString();
    }



}
