package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:修改指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 9, 2008:3:26:02 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class AmendIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		IndexBo subset = new IndexBo(this.getFrameconn());
		String fieldsetid=(String)reqhm.get("fieldsetid");
		if (fieldsetid == null || fieldsetid.length() < 1) {
			RecordVo busiFiledVo = (RecordVo) hm.get("busiFieldVo");
			String s = busiFiledVo.getString("codesetid");
			String bitianxiang = (String) this.getFormHM().get("bitianxiang");
			String len = "";
			try {
				// 提前将数据库中表字段数据提取出来  yangj update 2015-02-11
				RecordVo oldRecordVo = new RecordVo("fielditem");
				
				oldRecordVo.setString("fieldsetid", busiFiledVo.getString("fieldsetid"));
				oldRecordVo.setString("itemid", busiFiledVo.getString("itemid"));
				try {
					oldRecordVo = dao.findByPrimaryKey(oldRecordVo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				String itemtype = busiFiledVo.getString("itemtype");
				// 指标体系，修改代码型或字符型指标后，指标类型为空 jingq add 2014.09.26
				itemtype = PubFunc.keyWord_reback(itemtype);
				itemtype = itemtype.replace(".", "/");
				if ("A/S".equals(itemtype)) {
					busiFiledVo.setString("codesetid", "0");
					busiFiledVo.setString("itemtype", "A");
					busiFiledVo.setString("decimalwidth", "0");
				} else if ("D".equals(itemtype) || "N".equals(itemtype)) {
					if ("N".equals(itemtype)) {
						busiFiledVo.setString("codesetid", "0");
					} else {
						busiFiledVo.setString("codesetid", "0");
						busiFiledVo.setString("decimalwidth", "0");
					}

				} else if ("M".equals(itemtype)) {
					if(busiFiledVo.getString("itemlength")==""||"10".equals(busiFiledVo.getString("itemlength"))){
						busiFiledVo.setString("itemlength", "10");
					}
					//busiFiledVo.setString("itemlength", "10");
					busiFiledVo.setString("codesetid", "0");
					busiFiledVo.setString("decimalwidth", "0");
				} else if ("A/C".equals(itemtype)) {
					if ("@K".equals(s) || "UM".equals(s) || "UN".equals(s)) {
						busiFiledVo.setString("itemlength", "30");
						busiFiledVo.setString("decimalwidth", "0");
					} else if ("@@".equals(s)) {
						busiFiledVo.setString("itemlength", "3");
						busiFiledVo.setString("decimalwidth", "0");
					} else if (!"@K".equals(s) || !"UM".equals(s) || !"UN".equals(s) || !"0".equals(s)) {
						RowSet rs = dao.search("select MAX(" + Sql_switcher.length("codeitemid") + ") as len from codeitem where codesetid='" + s + "'");
						if (rs.next()) {
							int is = rs.getInt("len");
							len = new Integer(is).toString();
						}
						busiFiledVo.setString("itemlength", len);
						busiFiledVo.setString("decimalwidth", "0");
					}
					busiFiledVo.setString("itemtype", "A");
				}
				busiFiledVo.setString("reserveitem", bitianxiang);
				dao.updateValueObject(busiFiledVo);

				// 刷新数据字典
				FieldItem fi = DataDictionary.getFieldItem(busiFiledVo.getString("itemid"));
				if (fi != null) {
					fi.setItemdesc(busiFiledVo.getString("itemdesc"));
					fi.setExplain(busiFiledVo.getString("itemmemo"));
					fi.setItemtype(busiFiledVo.getString("itemtype"));
					int length = 0;
					try {
						length = Integer.parseInt(busiFiledVo.getString("itemlength"));
					} catch (Exception e) {
						length = 0;
					}
					fi.setItemlength(length);
					try {
						length = Integer.parseInt(busiFiledVo.getString("DECIMALWIDTH".toLowerCase()));
					} catch (Exception e) {
						length = 0;
					}
					fi.setDecimalwidth(length);
					if ("1".equals(bitianxiang))
						fi.setFillable(true);
					else
						fi.setFillable(false);
				}
			
				// 如果已够库，还需要修改数据库中表的字段长度,D和M的不可修改
				if ("1".equalsIgnoreCase(busiFiledVo.getString("useflag")) && !("D".equals(itemtype) || "M".equals(itemtype))) {
					// 对比decimalwidth和itemlength的数据，如果有变化则修改数据库中表字段结构
					if (!oldRecordVo.getString("decimalwidth").equals(busiFiledVo.getString("decimalwidth"))
							|| !oldRecordVo.getString("itemlength").equals(busiFiledVo.getString("itemlength"))) {
						DbWizard dbw = new DbWizard(this.frameconn);
						fieldsetid = busiFiledVo.getString("fieldsetid");
						if (fieldsetid.startsWith("A")) {
							ArrayList dblist = DataDictionary.getDbpreList();          // zhangcq 2016-5-24 修改人员库字段
							for (int i = 0; i < dblist.size(); i++) {
								Table table = new Table((String) dblist.get(i) + fieldsetid);
								if (dbw.isExistTable((String) dblist.get(i) + fieldsetid, false)) {
									Field item_o = fi.cloneField();
									item_o.setDecimalDigits(fi.getDecimalwidth());
									item_o.setLength(fi.getItemlength());
									table.addField(item_o);
									dbw.alterColumns(table);
								}
						
							}
							if (dbw.isExistTable("A01Log",false)) {
								Table logtable = new Table("A01Log"); //zhangcq 2016-5-24 修改人员库字段的长度的同时也修改a01log日志表
								if(dbw.isExistField("A01Log", busiFiledVo.getString("itemid"), false)){
									Field item_o = fi.cloneField();
									item_o.setDecimalDigits(fi.getDecimalwidth());
									item_o.setLength(fi.getItemlength());
									logtable.addField(item_o);
									dbw.alterColumns(logtable);
								}
							
							}
							
							/*更新指标时连带更新人员数据视图中的字段信息 guodd 2019-02-18*/
							if (dbw.isExistTable("t_hr_view",false) && dbw.isExistField("t_hr_view", busiFiledVo.getString("itemid"), false)) {
								Table hrtable = new Table("t_hr_view");
								Field item_o = fi.cloneField();
								item_o.setDecimalDigits(fi.getDecimalwidth());
								item_o.setLength(fi.getItemlength());
								hrtable.addField(item_o);
								dbw.alterColumns(hrtable);
							}
							
						} else {
							Table table = new Table(fieldsetid);
							if (dbw.isExistTable(fieldsetid)) {
								Field item_o = fi.cloneField();
								item_o.setDecimalDigits(fi.getDecimalwidth());
								item_o.setLength(fi.getItemlength());
								table.addField(item_o);
								dbw.alterColumns(table);
							}
							
							/*更新指标时连带更新单位数据视图中的字段信息 guodd 2019-02-18*/
							if (fieldsetid.startsWith("B")) {
								if (dbw.isExistTable("t_org_view",false) && dbw.isExistField("t_org_view", busiFiledVo.getString("itemid"), false)) {
									Table orgtable = new Table("t_org_view");
									Field item_o = fi.cloneField();
									item_o.setDecimalDigits(fi.getDecimalwidth());
									item_o.setLength(fi.getItemlength());
									orgtable.addField(item_o);
									dbw.alterColumns(orgtable);
								}
								
							}else if(fieldsetid.startsWith("K")) {
								/*更新指标时连带更新岗位数据视图中的字段信息 guodd 2019-02-18*/
								if (dbw.isExistTable("t_post_view",false) && dbw.isExistField("t_post_view", busiFiledVo.getString("itemid"), false)) {
									Table posttable = new Table("t_post_view");
									Field item_o = fi.cloneField();
									item_o.setDecimalDigits(fi.getDecimalwidth());
									item_o.setLength(fi.getItemlength());
									posttable.addField(item_o);
									dbw.alterColumns(posttable);
								}
								
							}
							
						}
					}
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			String itemid = (String) reqhm.get("itemid");
			reqhm.remove("fieldsetid");
			reqhm.remove("itemid");
			RecordVo busiFieldVo = new RecordVo("fielditem");
			busiFieldVo.setString("fieldsetid", fieldsetid);
			busiFieldVo.setString("itemid", itemid);
			try {
				busiFieldVo = dao.findByPrimaryKey(busiFieldVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			IndexBo index = new IndexBo(this.getFrameconn());
			this.getFormHM().put("inputtypeMList", index.getInputtypeMList());
			hm.put("joincodename", subset.getCodeStr(dao, busiFieldVo.getString("codesetid") + "/" + busiFieldVo.getString("itemlength")));
			hm.put("datelength", subset.getDateSel(busiFieldVo.getString("itemlength")));
			hm.put("busiFieldVo", this.putRecord(busiFieldVo));
			hm.put("useflag", busiFieldVo.getString("useflag"));
			hm.put("reserveitem", busiFieldVo.getString("reserveitem"));
		}
	}
	
	public RecordVo putRecord(RecordVo busiFiledVo){
		String itemtype=busiFiledVo.getString("itemtype");
		String codesetid=busiFiledVo.getString("codesetid");
		if("A".equals(itemtype)){
			//指标体系，修改指标，保存后指标类型为空，此处修改   jingq upd 2014.09.28
			if("0".equals(codesetid)){
				busiFiledVo.setString("itemtype","A.S");
			}else{
				if(codesetid!="0"){
					busiFiledVo.setString("itemtype","A.C");
				}
			}
		}
		return busiFiledVo;
	}

}
