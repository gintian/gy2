package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class RelExamCourseTrans extends IBusiness {

	public void execute() throws GeneralException {

		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));

		String columns="r5000,r5004,r5003,r5012,r5009,r5007,r5300";
		StringBuffer sbsql=new StringBuffer("select r5000,r5004,r5003,r5012,r5009,r5007");
		if(r5300!=null&&r5300.length()>0&&r5300.indexOf(",")==-1)
			sbsql.append(",(select r5300 from tr_lesson_paper where r50.r5000=tr_lesson_paper.r5000 and r5300="+r5300+") r5300");//单个试卷关联
		else
			sbsql.append(",'' r5300");
		StringBuffer sqlwhere=new StringBuffer();
		sqlwhere.append(" from r50 where r5022='04' and r5024=1 and (1=1");
		TrainCourseBo tb = new TrainCourseBo(this.userView,this.frameconn);
		
		if (!this.userView.isSuper_admin()) {
			String unit = tb.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
			if(unit.indexOf("UN`")==-1){
				String []units = unit.split("`");
				String sql=" and (";
				if (units.length > 0 && unit.length() > 0) {
					for (int i = 0; i < units.length; i++) {
						String b0110s = units[i].substring(2);
						sql+="r5020=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("r5020"));
						sql+=" or r5020 like '";
						sql+=b0110s;
						sql+="%'";
						sql+=" or ";
					}
				}
				sql+=Sql_switcher.isnull("r5020", "'-1'");
				sql+="='-1'";
				if (Sql_switcher.searchDbServer() == 1) {
					sql+=" or r5020=''";
				}
				sql+=" or r5014 ='1' or "+Sql_switcher.isnull("r5014", "1")+"='1')";
				sqlwhere.append(sql);
			}
		}
		
		String itemize = (String)this.getFormHM().get("itemize");
		if(itemize!=null&&itemize.length()>0)
			sqlwhere.append(" and r5004 like '"+itemize+"%'");
		String coursename = (String)this.getFormHM().get("coursename");
		if(coursename!=null&&coursename.length()>0)
			sqlwhere.append(" and r5003 like '%"+coursename+"%'");
		String courseintro = (String)this.getFormHM().get("courseintro");
		if(courseintro!=null&&courseintro.length()>0)
			sqlwhere.append(" and r5012 like '%"+courseintro+"%'");
		
		sqlwhere.append(getWhereCode());
		sqlwhere.append(")");
		
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		sqlwhere.append(" and "+Sql_switcher.dateValue(bosdate)+" between R5030 and R5031 ");
        
		ArrayList itemlist = new ArrayList();
		FieldItem fieldItem = DataDictionary.getFieldItem("r5000");
		fieldItem.setVisible(false);
		itemlist.add(fieldItem);
		itemlist.add(DataDictionary.getFieldItem("r5004"));
		itemlist.add(DataDictionary.getFieldItem("r5003"));
		itemlist.add(DataDictionary.getFieldItem("r5012"));
		itemlist.add(DataDictionary.getFieldItem("r5009"));
		itemlist.add(DataDictionary.getFieldItem("r5007"));
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("itemlist", itemlist);
		this.getFormHM().put("order_by", "order by r5000");
		this.getFormHM().put("strwhere", sqlwhere.toString());
		this.getFormHM().put("strsql", sbsql.toString());
	}
	
    private String getWhereCode() {
        String tmpCodes = "";
        TrainCourseBo tbo = new TrainCourseBo(this.userView);
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select codeitemid,b0110 from codeitem where codesetid='55' and not EXISTS (select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            this.frowset = dao.search(sqlstr.toString());
            while (this.frowset.next()) {
                String b0110 = this.frowset.getString("b0110");
                if (tbo.isUserParent(b0110) != -1) {
                    tmpCodes += this.frowset.getString("codeitemid") + ",";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (tmpCodes != null && tmpCodes.length() > 0) {
            tmpCodes = tmpCodes.substring(0, tmpCodes.length() - 1);
            tmpCodes = " and (r5004 in ('" + tmpCodes.replaceAll(",", "','") + "')" + " or " + Sql_switcher.isnull("r5004", "'0'") + "='0' or r5004='')";
        } else {
            tmpCodes = " and " + Sql_switcher.isnull("r5004", "'0'") + "='0'";
        }
        return tmpCodes;
    }
}
