package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveExamSelectTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = "ok";
		String r5400 = (String) this.getFormHM().get("r5400");
		r5400 =PubFunc.decrypt(SafeCode.decode(r5400));
		// String wherestr = (String)this.getFormHM().get("wherestr");
		String expr = (String) this.getFormHM().get("expr");
		expr = PubFunc.keyWord_reback(expr);
		String factor = (String) this.getFormHM().get("factor");
		factor = PubFunc.keyWord_reback(factor);
		String pre = (String) this.getFormHM().get("pre");
		String like = (String) this.getFormHM().get("like");
		boolean likeflag = false;
		if ("1".equals(like)) {
			likeflag = true;
		}
		FactorList factorslist = new FactorList(expr, PubFunc.getStr(factor), pre, false, likeflag, true, 1, userView.getUserId());
		// ArrayList fieldlist=factorslist.getFieldList();
		String wherestr = factorslist.getSqlExpression();
		if (wherestr != null && wherestr.length() > 0) {
			
			String tmpStr = "";
			if (!this.userView.isSuper_admin()) {
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String unit = bo.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
				if(unit.indexOf("UN`")==-1){
					String []units = unit.split("`");
					String tmp = "";
					String sql=" and (";
					if (units.length > 0 && unit.length() > 0) {
						for (int i = 0; i < units.length; i++) {
							String drp = units[i].substring(0, 2);
							String b0110s = units[i].substring(2);
							if("UN".equalsIgnoreCase(drp))
								tmp="b0110";
							else if("UM".equalsIgnoreCase(drp))
								tmp="e0122";
							else if("@K".equalsIgnoreCase(drp))
								tmp="e01a1";
							else
								continue;
							//sql+=tmp+"=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length(tmp));
							sql+=tmp+" like '";
							sql+=b0110s;
							sql+="%'";
							sql+=" or ";
						}
					}
//					sql+=Sql_switcher.isnull(tmp, "'-1'");
//					sql+="='-1'";
//					if (Sql_switcher.searchDbServer() == 1) {
//						sql+=" or "+tmp+"=''";
//					}
					if(sql.length()>10){
						sql = sql.substring(0,sql.length()-4);
						sql+=")";
					}
					tmpStr=sql;
				}
			}
			 //wherestr = SafeCode.decode(wherestr);
			 StringBuffer sql = new StringBuffer();
			 if("all".equalsIgnoreCase(pre)){
				 ArrayList dbstr = userView.getPrivDbList();
				 //参数中人员库
				 ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
				 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
				 ArrayList sel_nbase=new ArrayList();
				 if(tmpnbase!=null&&tmpnbase.length()>0){
					 String nbs[]=tmpnbase.split(",");
					 for(int i=0;i<nbs.length;i++){
						 if(nbs[i]!=null&&nbs[i].length()>0){
							 sel_nbase.add(nbs[i]);
						 }
					 }
				 }
				    		
				 for (int i = 0; i < dbstr.size(); i++) {
					 String tmp = (String)dbstr.get(i);
					 if(tmp==null||tmp.length()<1)
						 continue;
					 if(!sel_nbase.contains(tmp))//是否在参数中存在
						 continue;
					 if(i>0&&sql!=null&&sql.length()>0)
						 sql.append(" union ");
					 sql.append("select '" + tmp + "' as nbase,"+tmp+"A01.a0100,"+tmp+"A01.a0101,"+tmp+"A01.b0110,"+tmp+"A01.e0122,"+tmp+"A01.e01a1 " + wherestr.replaceAll("all", tmp) + tmpStr);
				 }
			}else{
				sql.append("select '" + pre + "' as nbase,"+pre+"A01.a0100,"+pre+"A01.a0101,"+pre+"A01.b0110,"+pre+"A01.e0122,"+pre+"A01.e01a1 " + wherestr + tmpStr);
			}
			// System.out.println(sql);
			if (sql != null && sql.length() > 0) {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					// ArrayList sqllist=new ArrayList();
					this.frowset = dao.search(sql.toString());
					while (this.frowset.next()) {
						String nbase = this.frowset.getString("nbase");
						String a0100 = this.frowset.getString("a0100");
						String a0101 = this.frowset.getString("a0101");
						a0101 = a0101 == null || a0101.length() < 1 ? "" : a0101;
						String b0110 = this.frowset.getString("b0110");
						b0110 = b0110 == null || b0110.length() < 1 ? "" : b0110;
						String e0122 = this.frowset.getString("e0122");
						e0122 = e0122 == null || e0122.length() < 1 ? "" : e0122;
						String e01a1 = this.frowset.getString("e01a1");
						e01a1 = e01a1 == null || e01a1.length() < 1 ? "" : e01a1;

						sql.setLength(0);
						sql.append("select 1 from r55 where r5400=" + r5400);
						sql.append(" and nbase='" + nbase + "'");
						sql.append(" and a0100='" + a0100 + "'");
						this.frecset = dao.search(sql.toString());
						if (this.frecset.next())
							continue;

						if(!"".equals(b0110))
						{						
							sql.setLength(0);
							sql.append("insert into r55 (r5400,nbase,a0100,a0101,b0110,e0122,e01a1,r5513,r5515) values(");
							sql.append(r5400);
							sql.append(",'" + nbase);
							sql.append("','" + a0100);
							sql.append("','" + a0101);
							sql.append("','" + b0110);
							sql.append("','" + e0122);
							sql.append("','" + e01a1);
							sql.append("',-1,-1)");
							// System.out.println(sql);
							// sqllist.add(sql);
							dao.insert(sql.toString(), new ArrayList());
						}
					}
					// dao.batchUpdate(sqllist);
				} catch (SQLException e) {
					flag = "error";
					e.printStackTrace();
				}
			}
		}
		// }
		this.getFormHM().put("flag", flag);
	}
}
