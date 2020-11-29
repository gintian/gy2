package com.hjsj.hrms.transaction.general.kanban;

import com.hjsj.hrms.businessobject.general.kanban.KanBanBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KanBanTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ArrayList list = DataDictionary.getFieldList("p05",Constant.USED_FIELD_SET);
		ArrayList orderlist = new ArrayList();//排序指标
		ArrayList desclist = new ArrayList();//升降
		ArrayList fieldlist = new ArrayList();//字段列表
		ArrayList itemlist = new ArrayList();//查询字段列表

		String checkflag = (String)hm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
		hm.remove("checkflag");
		
		String clearwhere = (String)hm.get("clearwhere");
		clearwhere=clearwhere!=null&&clearwhere.trim().length()>0?clearwhere:"0";
		hm.remove("clearwhere");

		CommonData tempobj = new CommonData("no","");
		itemlist.add(tempobj);
		tempobj = new CommonData("A0101","发单人");
		itemlist.add(tempobj);
		tempobj = new CommonData("A0101_0","接单人");
		itemlist.add(tempobj);
		tempobj = new CommonData("A0101_1","审核人");
		itemlist.add(tempobj);
		try{
			KanBanBo kb = new KanBanBo(this.userView,this.frameconn);
			if("add".equalsIgnoreCase(checkflag)){
				ArrayList vlauelist = (ArrayList) this.getFormHM().get("fieldlist");
				String billperson =this.userView.getDbname()+"::"
				+this.userView.getA0100()+"::"+this.userView.getUserFullName();

				String person = (String)this.getFormHM().get("person");
				person=person!=null&&person.trim().length()>1?person:billperson;

				String checkperson = (String)this.getFormHM().get("checkperson");
				checkperson=checkperson!=null&&checkperson.trim().length()>1?checkperson:billperson;

				kb.addValue(vlauelist, person, checkperson);
			}else if("update".equalsIgnoreCase(checkflag)){
				ArrayList vlauelist = (ArrayList) this.getFormHM().get("fieldlist");

				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");
				String billperson =this.userView.getDbname()+"::"
				+this.userView.getA0100()+"::"+this.userView.getUserFullName();
				String person = (String)this.getFormHM().get("person");
				person=person!=null&&person.trim().length()>1?person:billperson;

				String checkperson = (String)this.getFormHM().get("checkperson");
				checkperson=checkperson!=null&&checkperson.trim().length()>1?checkperson:billperson;

				kb.updateValue(vlauelist, person, checkperson, p0500);
			}else if("delete".equalsIgnoreCase(checkflag)){
				String p0500arr = (String)hm.get("p0500arr");
				p0500arr=p0500arr!=null?p0500arr:"";

				kb.deleteValue(p0500arr);
			}else if("reply".equalsIgnoreCase(checkflag)){
				ArrayList vlauelist = (ArrayList) this.getFormHM().get("fieldlist");
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");

				kb.replyValue(vlauelist, p0500);
			}else if("fill".equalsIgnoreCase(checkflag)){
				ArrayList vlauelist = (ArrayList) this.getFormHM().get("fieldlist");
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");

				String subs = (String)hm.get("subs");
				subs=subs!=null&&subs.trim().length()>0?subs:"0";
				hm.remove("subs");

				kb.fillValue(vlauelist,p0500,subs);
			}else if("audit".equalsIgnoreCase(checkflag)){
				ArrayList vlauelist = (ArrayList) this.getFormHM().get("fieldlist");
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");

				kb.auditValue(vlauelist,p0500);
			}


			StringBuffer sqlstr = new StringBuffer("select ");
			StringBuffer cloums = new StringBuffer();
			StringBuffer orderby= new StringBuffer();

			String orderid = (String)this.getFormHM().get("orderid");
			orderid=orderid!=null?orderid:"";

			String descid = (String)this.getFormHM().get("descid");
			descid=descid!=null&&descid.trim().length()>0?descid:"0";

			if(orderid.trim().length()>2){
				orderby.append("order by ");
				orderby.append(orderid);
				if("0".equals(descid))
					orderby.append(" asc");
				else
					orderby.append(" desc");
			}
			String sortitem = (String)this.getFormHM().get("sortitem");
			sortitem=sortitem!=null?sortitem:"";
			if(sortitem.length()>2){
				String items[] = sortitem.split("`");
				orderby.setLength(0);
				for (int i = 0; i < items.length; i++) {
					String item = items[i];
					String properties[] = item.split(":");
					if (properties.length == 3) {
						orderby.append(properties[0]);
						if ("0".equalsIgnoreCase(properties[2])) {
							orderby.append(" desc,");
						} else if ("1".equalsIgnoreCase(properties[2])) {
							orderby.append(" asc,");
						}
					} else {
						continue;
					}
				}
				String sqlorderby = null;
				if (orderby.length() == 0) {
					sqlorderby = "";
				} else {
					sqlorderby = "order by "+orderby.substring(0, orderby.length() - 1);
				}
				orderby.setLength(0);
				orderby.append(sqlorderby);
			}
			CommonData dataobj1 = new CommonData("","");//xuj 2009-11-5  发单人、接单人、任务审核人进行排序
			orderlist.add(dataobj1);
			tempobj = new CommonData("A0101","发单人");
			orderlist.add(tempobj);
			tempobj = new CommonData("A0101_0","接单人");
			orderlist.add(tempobj);
			tempobj = new CommonData("A0101_1","任务审核人");
			orderlist.add(tempobj);
			ArrayList setlist = kb.fieldList();
			for(int i=0;i<setlist.size();i++){
				FieldItem fielditem = (FieldItem)setlist.get(i);
				cloums.append(fielditem.getItemid()+",");
				fieldlist.add(fielditem);
			}

			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)list.get(i);
				if("p0501".equalsIgnoreCase(fielditem.getItemid())
						|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
					fielditem.setItemlength(20);
				}
				cloums.append(fielditem.getItemid()+",");
				if(!fielditem.isVisible())
					continue;
				if("N".equalsIgnoreCase(fielditem.getItemtype())&&fielditem.getDecimalwidth()>2)
					fielditem.setDecimalwidth(2);
				
				fieldlist.add(fielditem);
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				if("p0500".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}
				
				dataobj1 = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
				itemlist.add(dataobj1);
				orderlist.add(dataobj1);
			}
			
			/** 计算完成状态*/
			updateFlag();
			
			String hsearch = (String)this.getFormHM().get("hsearch");
			hsearch=hsearch!=null?hsearch:"";
			sqlstr.append(cloums.substring(0, cloums.length()-1));
			sqlstr.append(" from p05 ");
			if("1".equals(clearwhere)){
				this.getFormHM().put("itemid","");
				this.getFormHM().put("hsearch","");
			}else{
				if(hsearch.trim().length()>0){
					sqlstr.append(hWhereStr(hsearch));
				}else
					sqlstr.append(searchWhere());
			}
			CommonData dataobj2 = new CommonData("0","升序");
			desclist.add(dataobj2);
			dataobj2 = new CommonData("1","降序");
			desclist.add(dataobj2);
			this.userView.getHm().put("performance_sql", sqlstr.toString() + " " + orderby.toString());
			this.getFormHM().put("hsearch", hsearch);
			this.getFormHM().put("sqlstr", sqlstr.toString());
			this.getFormHM().put("cloums", cloums.substring(0, cloums.length()-1).toString());
			this.getFormHM().put("orderby", orderby.toString());
			this.getFormHM().put("descid", descid.toString());
			this.getFormHM().put("orderid", orderid.toString());
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("orderlist", orderlist);
			this.getFormHM().put("desclist", desclist);
			this.getFormHM().put("person", "");
			this.getFormHM().put("checkperson","");
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("sortitem", sortitem);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private String searchWhere(){
		StringBuffer wheresql = new StringBuffer();
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null?itemid:"no";
		String codeid = "";
		String fromnum = "";
		String tonum = "";
		String fromdate = "";
		String todate = "";
		String searchtext = "";
		if(itemid!=null&&itemid.trim().length()>2){
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			if("A0101_0".equalsIgnoreCase(itemid)){
				fielditem = new FieldItem("A01","A01");
				fielditem.setItemid("A0101_0");
				fielditem.setItemdesc("A0101_0");
				fielditem.setItemtype("A");
				fielditem.setCodesetid("0");
			}else if("A0101_1".equalsIgnoreCase(itemid)){
				fielditem = new FieldItem("A01","A01");
				fielditem.setItemid("A0101_1");
				fielditem.setItemdesc("A0101_1");
				fielditem.setItemtype("A");
				fielditem.setCodesetid("0");
			}
			if(fielditem!=null){
				if("A".equalsIgnoreCase(fielditem.getItemtype())){
					if(!fielditem.isCode()){
						searchtext = (String)this.getFormHM().get("searchtext");
						searchtext=searchtext!=null?searchtext:"";
						if(searchtext.length()>0){
							wheresql.append(" where ");
							wheresql.append(itemid+" like '");
							wheresql.append(searchtext+"%'");
						}
						
					}else{
						 codeid = (String)this.getFormHM().get("codeid");
						 codeid=codeid!=null&&codeid.trim().length()>0?codeid:"";
						 if(codeid.length()>0){
							 wheresql.append(" where ");
							 wheresql.append(itemid+"='");
							 wheresql.append(codeid+"'");
						 }
					}
				}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
					fromdate = (String)this.getFormHM().get("fromdate");
					fromdate=fromdate!=null?fromdate:"";
					WorkdiarySQLStr wss=new WorkdiarySQLStr();
					todate = (String)this.getFormHM().get("todate");
					todate=todate!=null?todate:"";
					if(fromdate.trim().length()>0||todate.trim().length()>0){
						wheresql.append(" where ");
						if(todate.trim().length()<1){
							wheresql.append(wss.getDataValue(itemid,"=", fromdate));
						}else if(fromdate.trim().length()<1){
							wheresql.append(wss.getDataValue(itemid,"=", todate));
						}else{
							wheresql.append(itemid);
							wheresql.append(" BETWEEN ");
							wheresql.append(Sql_switcher.dateValue(fromdate+" 00:00:00"));
							wheresql.append(" AND "+Sql_switcher.dateValue(todate+" 23:59:59"));
						}
					}
				}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
					fromnum = (String)this.getFormHM().get("fromnum");
					fromnum=fromnum!=null?fromnum:"";
					
					tonum = (String)this.getFormHM().get("tonum");
					tonum=tonum!=null?tonum:"";
					if(fromnum.trim().length()>0||tonum.trim().length()>0){
						wheresql.append(" where ");
						if("0".equals(fromnum)){
							if(tonum.trim().length()<1){
								wheresql.append("(");
								wheresql.append(itemid+"="+fromnum);
								wheresql.append(" or "+itemid+" is null or "+itemid+"='')");
							}else{
								wheresql.append("("+itemid+" BETWEEN '"+fromnum+"' AND '"+tonum+"'");
								wheresql.append(" or "+itemid+" is null or "+itemid+"='')");
							}
						}else{
							if(tonum.trim().length()<1){
								wheresql.append(itemid+"="+fromnum);
							}else{
								wheresql.append(itemid+" BETWEEN '"+fromnum+"' AND '"+tonum+"'");
							}
						}
						
					}
				}
			}
		}
		this.getFormHM().put("codeid", codeid);
		this.getFormHM().put("fromnum", fromnum);
		this.getFormHM().put("tonum", tonum);
		this.getFormHM().put("fromdate", fromdate);
		this.getFormHM().put("todate", todate);
		this.getFormHM().put("itemid", itemid);
		return wheresql.toString();
	}
	private String hWhereStr(String hsearch){
		StringBuffer sqlwhere = new StringBuffer();
		if (hsearch != null && hsearch.trim().length() > 0) {
			String searcharr[] = hsearch.split("::");
			if (searcharr.length == 3) {
				String sexpr = searcharr[0];
				String sfactor = searcharr[1];
				try {
					boolean blike = false;
					blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true
							: false;
					FieldItem fielditem = new FieldItem("P05","P05");
					fielditem.setItemid("A0101_0");
					fielditem.setItemdesc("A0101_0");
					fielditem.setItemtype("A");
					fielditem.setCodesetid("0");
					fielditem.setUseflag("1");
					HashMap map = new HashMap();
					map.put("A0101_0", fielditem);
					fielditem = new FieldItem("P05","P05");
					fielditem.setItemid("A0101_1");
					fielditem.setItemdesc("A0101_1");
					fielditem.setItemtype("A");
					fielditem.setCodesetid("0");
					fielditem.setUseflag("1");
					map.put("A0101_1", fielditem);
					
					fielditem = DataDictionary.getFieldItem("A0101");
					fielditem.setUseflag("1");
					map.put("A0101", fielditem);
					ArrayList list = DataDictionary.getFieldList("P05",Constant.USED_FIELD_SET);
					for(int i=0;i<list.size();i++){
						FieldItem field = (FieldItem)list.get(i);
						if(field!=null){
							fielditem.setUseflag("1");
							map.put(field.getItemid().toUpperCase(), field);
						}
					}
					FactorList factor = new FactorList(sexpr, sfactor.toUpperCase(), "",
							false, blike, true, 1, "su",false,map);
					String wherestr = factor.getSqlExpression();
					if (wherestr.indexOf("WHERE") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("WHERE") + 5, wherestr
										.length());
					if (wherestr.indexOf("where") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("where") + 5, wherestr
										.length());
					if (wherestr.indexOf("I9999") != -1)
						wherestr = wherestr.substring(0, wherestr
								.lastIndexOf("AND"));

					wherestr = wherestr.replaceAll("A01\\.", "P05\\.");
					sqlwhere.append(" where " + wherestr);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
		}
		this.getFormHM().put("codeid", "");
		this.getFormHM().put("fromnum", "");
		this.getFormHM().put("tonum", "");
		this.getFormHM().put("fromdate", "");
		this.getFormHM().put("todate", "");
		this.getFormHM().put("itemid", "");
		return sqlwhere.toString();
	}
	private void updateFlag(){
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select p0500,p0502,p0515,p0517,p0519 from p05");
		try {
			this.frowset = dao.search(sqlstr.toString());
			ArrayList listvlaue = new ArrayList();
			int p0500 ;
			Date p0502date = null;
			Date p0515date = null;
			Date p0517date = null;
			Date p0519date = null;
			String flag="";
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				p0500 = this.frowset.getInt(1);
				p0502date = this.frowset.getDate(2);
				p0515date = this.frowset.getDate(3);
				p0517date = this.frowset.getDate(4);
				p0519date = this.frowset.getDate(5);
				if(p0502date!=null){
					if(p0517date!=null){
						if(WeekUtils.comPareTime(p0502date,p0517date)==0){
							flag = "1";
						}else if(WeekUtils.comPareTime(p0517date,p0502date)==1){
							flag = "2";
						}else{
							if(p0519date!=null){
								if(WeekUtils.comPareTime(p0502date,p0519date)==2){
									flag = "4";
								}else{
									flag = "3";
								}
							}else{
								flag = "4";
							}
						}
					}else{
						if(p0515date!=null){
							if(WeekUtils.comPareTime(p0502date,p0515date)==0){
								flag = "1";
							}else if(WeekUtils.comPareTime(p0515date,p0502date)==1){
								flag = "2";
							}else{
								if(p0519date!=null){
									if(WeekUtils.comPareTime(p0502date,p0519date)==2){
										flag = "4";
									}else{
										flag = "3";
									}
								}else{
									flag = "4";
								}
							}
						}else{
							if(p0519date!=null){
								if(WeekUtils.comPareTime(p0502date,p0519date)==2){
									flag = "4";
								}else if(WeekUtils.comPareTime(p0502date,p0519date)==1){
									flag = "3";
								}else{
									flag = "2";
								}
							}else{
								flag = "";
							}
						}
					}
				}else{
					flag="";
				}
				list.add(flag);
				list.add(p0500+"");
				listvlaue.add(list);
			}
			dao.batchUpdate("update p05 set p0521=? where p0500=?", listvlaue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
