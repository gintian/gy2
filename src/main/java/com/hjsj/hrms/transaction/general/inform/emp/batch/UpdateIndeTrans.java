package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 *
 */
public class UpdateIndeTrans extends IBusiness {
	private Logger log = LoggerFactory.getLogger(UpdateIndeTrans.class);
	public void execute() throws GeneralException {
		long start = System.currentTimeMillis();
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";

		String setname = (String)this.getFormHM().get("setname");
		setname=setname!=null&&setname.trim().length()>0?setname:"";

		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;

		String dbname = (String)this.getFormHM().get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";

		String viewsearch = (String)this.getFormHM().get("viewsearch");
		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";

		String infor = (String)this.getFormHM().get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"1";

		String entranceFlag=(String)this.getFormHM().get("entranceFlag");
		entranceFlag=entranceFlag!=null&&entranceFlag.length()>0?entranceFlag:"0";//进入模块标志=1从工资管理的基础数据维护进入，默认为0从其他模块进入

		String inforflag = (String)this.getFormHM().get("inforflag"); //1:员工管理BS表格录入 2：外部培训
		inforflag=inforflag!=null&&inforflag.trim().length()>0?inforflag:"1";

		BatchBo batchbo = new BatchBo(this.userView);
		batchbo.setUserName(this.userView.getUserName());
		batchbo.setEntranceFlag(entranceFlag);

		String tablename = dbname+setname;
		String check="";
		if("alert".equals(flag)){
			String updatevalue = (String)this.getFormHM().get("updatevalue");
			updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"";

			String history = (String)this.getFormHM().get("history");
			history=history!=null&&history.trim().length()>0?history:"0";

			String flagcheck = (String)this.getFormHM().get("flagcheck");
			flagcheck=flagcheck!=null&&flagcheck.trim().length()>0?flagcheck:"1";

			String itemid = (String)this.getFormHM().get("itemid");
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";

			String strid = (String)this.getFormHM().get("strid");
			strid=strid!=null&&strid.trim().length()>0?strid:"";

			String selectid = (String)this.getFormHM().get("selectid");
			selectid=selectid!=null&&selectid.trim().length()>0?selectid:"0";

			if("1".equals(selectid)){
				if(batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
						itemid,updatevalue,viewsearch,dbname,flagcheck,infor,history,inforflag)){
					if("1".equals(infor)){
						if(itemid.toUpperCase().indexOf("B0110")!=-1){
							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"E0122:A:UM","",viewsearch,dbname,flagcheck,infor,history,inforflag);
							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"E01A1:A:@K","",viewsearch,dbname,flagcheck,infor,history,inforflag);
						}else if(itemid.toUpperCase().indexOf("E0122")!=-1){
							CodeItem codeitem = AdminCode.getCode("UM",updatevalue);
							String b0110 = codeitem.getPcodeitem();
							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"E01A1:A:@K","",viewsearch,dbname,flagcheck,infor,history,inforflag);
							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"B0110:A:UN",b0110,viewsearch,dbname,flagcheck,infor,history,inforflag);
						}else if(itemid.toUpperCase().indexOf("E01A1")!=-1){
							CodeItem codeitem = AdminCode.getCode("@K",updatevalue);
							String e0122 = codeitem.getPcodeitem();
							String b0110 = getParentCodeValue(e0122);

							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"E0122:A:@K",e0122,viewsearch,dbname,flagcheck,infor,history,inforflag);
							batchbo.alertUpdate(this.frameconn,this.getUserView(),tablename,a_code,
									"B0110:A:UN",b0110,viewsearch,dbname,flagcheck,infor,history,inforflag);
						}
					}
					check="ok";
				}else{
					check="no";
				}
			}else{
				if(batchbo.alertUpdate(this.frameconn,tablename,
						itemid,updatevalue,flagcheck,infor,strid,inforflag,dbname)){
					if("1".equals(infor)){
						if(itemid.toUpperCase().indexOf("B0110")!=-1){
							batchbo.alertUpdate(this.frameconn,tablename,
									"E0122:A:UM","",flagcheck,infor,strid,inforflag,dbname);
							batchbo.alertUpdate(this.frameconn,tablename,
									"E01A1:A:@K","",flagcheck,infor,strid,inforflag,dbname);
						}else if(itemid.toUpperCase().indexOf("E0122")!=-1){
							CodeItem codeitem = AdminCode.getCode("UM",updatevalue);
							String b0110 = codeitem.getPcodeitem();
							batchbo.alertUpdate(this.frameconn,tablename,
									"E01A1:A:UM","",flagcheck,infor,strid,inforflag,dbname);
							batchbo.alertUpdate(this.frameconn,tablename,
									"B0110:A:UN",b0110,flagcheck,infor,strid,inforflag,dbname);
						}else if(itemid.toUpperCase().indexOf("E01A1")!=-1){
							CodeItem codeitem = AdminCode.getCode("@K",updatevalue);
							String e0122 = codeitem.getPcodeitem();
							String b0110 = getParentCodeValue(e0122);

							batchbo.alertUpdate(this.frameconn,tablename,
									"E0122:A:UM",e0122,flagcheck,infor,strid,inforflag,dbname);
							batchbo.alertUpdate(this.frameconn,tablename,
									"B0110:A:UN",b0110,flagcheck,infor,strid,inforflag,dbname);
						}
					}
					check="ok";
				}else{
					check="no";
				}
			}
		}else if("alertmore".equals(flag)){
			String history = (String)this.getFormHM().get("history");
			history=history!=null&&history.trim().length()>0?history:"0";

			String strid = (String)this.getFormHM().get("strid");
			strid=strid!=null&&strid.trim().length()>0?strid:"";

			String selectid = (String)this.getFormHM().get("selectid");
			selectid=selectid!=null&&selectid.trim().length()>0?selectid:"0";

			ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
			ArrayList itemvalue_arr = (ArrayList)this.getFormHM().get("itemvalue_arr");
			this.getFormHM().remove("itemvalue_arr");
			for(int i = 0; i < itemvalue_arr.size(); i++) {
				String itemeValue = (String) itemvalue_arr.get(i);
				itemeValue = SafeCode.decode(itemeValue);
				itemvalue_arr.set(i, itemeValue);
			}
			/**/// xuj 2010-5-31
			if("1".equals(infor)){
				batchbo.setStrid(strid); //20141029  dengcan 人员信息批量修改增加对所选记录的更新操作
				//检查编制 wangrd 2013-09-06
				String info =batchbo.scanFormationBeforeBatModify(this.frameconn,this.getUserView(),setname,itemid_arr,
						itemvalue_arr,dbname,infor,history,selectid);
				if ("ok".equals(info)){
					this.getFormHM().put("scanformation","");
				}else{
					this.getFormHM().put("scanformation",info);
				}
				//批量修改
				if(batchbo.alertMoreUpdate(this.frameconn,this.getUserView(),setname,itemid_arr,
						itemvalue_arr,dbname,infor,history,selectid,inforflag)){
					check="ok";
				}else{
					check="no";
				}

			}
			else{
				if("1".equals(selectid)){//所有记录
					if(batchbo.alertMoreUpdate(this.frameconn,this.getUserView(),setname,a_code,itemid_arr,
							itemvalue_arr,viewsearch,"",infor,history,inforflag)){
						check="ok";
					}else{
						check="no";
					}
				}else{//当前选中
					if(batchbo.alertMoreUpdate(this.frameconn,setname,a_code,itemid_arr,
							itemvalue_arr,"",infor,strid,inforflag)){
						check="ok";
					}else{
						check="no";
					}
				}
			}

		}else if("add".equals(flag)){
			ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
			ArrayList value_arr = (ArrayList)this.getFormHM().get("itemvalue_arr");

			ArrayList itemvalue_arr = checkDate(itemid_arr,value_arr);

			if(batchbo.addUpdate(this.frameconn,this.userView,setname,dbname,a_code,itemid_arr
					,itemvalue_arr,viewsearch,infor, inforflag)){
				check="ok";
			}else{
				check="no";
			}
		}else if("del".equals(flag)){
			String updatevalue = (String)this.getFormHM().get("updatevalue");
			updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"";

			String year = (String)this.getFormHM().get("year");
			year=year!=null&&year.trim().length()>0?year:"";

			String month = (String)this.getFormHM().get("month");
			month=month!=null&&month.trim().length()>0?month:"";
			String frequency = (String)this.getFormHM().get("frequency");
			frequency=frequency!=null&&frequency.trim().length()>0?frequency:"";
			if("1".equalsIgnoreCase(updatevalue)){
				if(batchbo.delUpdate(this.getFrameconn(),this.userView,setname,dbname,a_code,viewsearch,infor,inforflag)){
					check="ok";
				}
			}else if("2".equalsIgnoreCase(updatevalue)){
				if(batchbo.delUpdate(this.getFrameconn(),this.userView,setname,dbname,a_code,year,month,frequency,infor,inforflag)){
					check="ok";
				}
			}else if("0".equalsIgnoreCase(updatevalue)){
//				if(batchbo.delUpdate(this.getFrameconn(),setname,dbname)){
				if(batchbo.delUpdate(this.getFrameconn(),setname,dbname,infor,a_code,inforflag)){
					check="ok";
				}
			}
		}else if("updatecol".equals(flag)){
			String sortstr = (String)this.getFormHM().get("sortstr");
			sortstr=sortstr!=null&&sortstr.trim().length()>0?sortstr:"";

			String results = (String)this.getFormHM().get("results");
			results=results!=null&&results.trim().length()>0?results:"0";

			String history = (String)this.getFormHM().get("history");
			history=history!=null&&history.trim().length()>0?history:"0";

			if(sortstr.length()>0){
				if(batchbo.saveSort(this.frameconn,sortstr)){
					check="ok";
				}
			}
			try{
				if(infor!=null&& "5".equals(infor)){
					if(batchbo.colUpdate1(this.frameconn,this.userView,dbname,
							setname,a_code,results,history,viewsearch,infor,inforflag)){
						check="ok";
					}else{
						check="no";
					}
				}else if(infor!=null&& "6".equals(infor)){
					if(batchbo.colUpdate(this.frameconn,this.userView,dbname,
							setname,a_code,results,history,viewsearch,inforflag)){
						check="ok";
					}else{
						check="no";
					}
				}else if(infor!=null&& "1".equals(infor)){
					//检查编制 wangrd 2013-09-06
					log.info("UpdateIndeTrans第 1 段用时:{} ms",(System.currentTimeMillis() - start));
					String info =batchbo.scanFormationBeforCalc(this.frameconn,this.userView,dbname,
							setname,results,history);//根据计算公式计算出每个指标的值保存到临时表
					if ("ok".equals(info)){
						this.getFormHM().put("scanformation","");
					}else{
						this.getFormHM().put("scanformation",info);
					}
					log.info("UpdateIndeTrans第 2 段用时:{} ms",(System.currentTimeMillis() - start));
					if(batchbo.colUpdate(this.frameconn,this.userView,dbname,
							setname,results,history,inforflag)){//从临时表中计算出的值更新到每个指标中
						check="ok";
					}else{
						check="no";
					}
					log.info("UpdateIndeTrans第 3 段用时:{} ms",(System.currentTimeMillis() - start));
				}else {
					String computeScope = (String)this.formHM.get("computeScope");
					computeScope = computeScope == null || computeScope.trim().length()<1 ? "0" : computeScope;
					batchbo.setComputeScope(computeScope);
					if(batchbo.colUpdate(this.frameconn,this.userView,dbname,
							setname,a_code,results,history,viewsearch,infor)){
						check="ok";
					}else{
						check="no";
					}
				}
			}catch(GeneralException e){
				if((e.getErrorDescription().equalsIgnoreCase(ResourceFactory.getProperty("train.b_plan.update.submit.approval.error1")+"！"))){
					check="msg";
				}
				else {
					throw e;
				}

			}
		}
		this.getFormHM().put("check",check);
	}
	private ArrayList checkDate(ArrayList itemlist,ArrayList valuelist){
		ArrayList list = new ArrayList();
		for(int i=0;i<itemlist.size();i++){
			if(itemlist.get(i)!=null&&itemlist.get(i).toString().length()>0){
				String itemid = (String)itemlist.get(i);
				FieldItem fielditem=DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					String values = (String)valuelist.get(i);
					if("D".equalsIgnoreCase(fielditem.getItemtype())){
						if(values!=null&&values.trim().length()>1){
							list.add(values);
						}else{
							list.add(null);
						}
					}else{
						list.add(values);
					}
				}
			}
		}
		return list;
	}
	/**
	 * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位 节点。
	 *
	 * @param codevalue
	 * @return
	 */
	private String getParentCodeValue(String codevalue) {

		String value = "";
		StringBuffer buf = new StringBuffer();
		buf
				.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
		ArrayList paralist = new ArrayList();
		paralist.add(codevalue);
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			RowSet rset = dao.search(buf.toString(), paralist);
			if (rset.next()) {
				String codeid = rset.getString("codesetid");
				String parentid = rset.getString("parentid");
				if (!"UN".equalsIgnoreCase(codeid))
					value = getParentCodeValue(parentid);
				else
					value = rset.getString("codeitemid");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return value;
	}
}
