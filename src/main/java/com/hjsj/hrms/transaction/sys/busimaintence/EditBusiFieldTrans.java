package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EditBusiFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		BusiSelStr busiselstr=new BusiSelStr();
		String fieldsetid=(String) reqhm.get("fieldsetid");
		String box = (String)reqhm.get("bitianxiang");
		
		DbWizard dbWizard= new DbWizard(this.getFrameconn());
		
		if(fieldsetid==null||fieldsetid.length()<1){
			RecordVo busiFiledVo=(RecordVo) hm.get("fieldVo");
			String expression = busiFiledVo.getString("expression");
			expression = PubFunc.keyWord_reback(expression);
			busiFiledVo.setString("expression", expression);
			String s = busiFiledVo.getString("codesetid");
			String len = "";
			try {
				String itemtype = busiFiledVo.getString("itemtype");
				//业务字典，修改代码型或字符型指标后，指标类型为空 jingq add 2014.11.13
				itemtype = PubFunc.keyWord_reback(itemtype);
				itemtype = itemtype.replace(".", "/");
				if("A/S".equals(itemtype)){
					busiFiledVo.setString("codesetid", "0");
					busiFiledVo.setString("codeflag", "0");
					busiFiledVo.setString("reserveitem", box);
					busiFiledVo.setString("itemtype","A");
				}else if("D".equals(itemtype)|| "N".equals(itemtype)){
					busiFiledVo.setString("codesetid", "0");
					busiFiledVo.setString("reserveitem", box);
				}else if("M".equals(itemtype)){
					if("".equals(busiFiledVo.getString("itemlength"))||"10".equals(busiFiledVo.getString("itemlength"))){
						busiFiledVo.setString("itemlength", "10");
					}
					//busiFiledVo.setString("itemlength", "0");
					busiFiledVo.setString("codesetid", "0");
					busiFiledVo.setString("reserveitem", box);
				}else if("A/C".equals(itemtype)){
					if("@K".equals(s)|| "UM".equals(s)|| "UN".equals(s)){
//						RowSet rs = dao.search("select max("+Sql_switcher.length("codeitemid")+") as len from Organization where codesetid = '"+s+"'");
//						if(rs.next()){
//							int is = rs.getInt("len");
//							len = new Integer(is).toString();
//						}
						len = "30";
						busiFiledVo.setString("itemlength", len);
					}else if ("@@".equals(s)){
						busiFiledVo.setString("itemlength", "3");
					}else if(!"@K".equals(s)||!"UM".equals(s)||!"UN".equals(s)||!"0".equals(s)){
						RowSet rs = dao
						.search("select MAX("+Sql_switcher.length("codeitemid")+") as len from codeitem where codesetid='"
								+ s + "'");
						if (rs.next()) {
							int is = rs.getInt("len");
							len = new Integer(is).toString();
						}
						busiFiledVo.setString("itemlength", len);
					}
					busiFiledVo.setString("codeflag", "0");
					busiFiledVo.setString("reserveitem", box);
					busiFiledVo.setString("itemtype","A");
				}else if("A/R".equals(itemtype)){
					/*String length = busiFiledVo.getString("reserveitem");
					String lengt[] = length.split("/");*/
					busiFiledVo.setString("itemtype","A");
					/*busiFiledVo.setString("codesetid", lengt[0]);
					busiFiledVo.setString("itemlength", lengt[1]);*/
					busiFiledVo.setString("reserveitem", box);
					busiFiledVo.setString("codeflag", "1");
				}
				dao.updateValueObject(busiFiledVo);
				
				FieldItem fi = DataDictionary.getFieldItem(busiFiledVo.getString("itemid"));
				if(fi!=null){
					fi.setItemdesc(busiFiledVo.getString("itemdesc"));
					fi.setItemtype(busiFiledVo.getString("itemtype"));
					int length = 0;
					try{
						length = Integer.parseInt(busiFiledVo.getString("itemlength"));
					}catch(Exception e){
						busiFiledVo.setString("itemlength", "0");
						length = 0;
					}
					fi.setItemlength(length);
					try{
						length = Integer.parseInt(busiFiledVo.getString("DECIMALWIDTH".toLowerCase()));
					}catch(Exception e){
						busiFiledVo.setString("DECIMALWIDTH".toLowerCase(), "0");
						length = 0;
					}
					fi.setDecimalwidth(length);
					if("1".equals(box))
						fi.setFillable(true);
					else
						fi.setFillable(false);
				}
				
				if("1".equalsIgnoreCase(busiFiledVo.getString("useflag")))
				{
					if("N".equalsIgnoreCase(busiFiledVo.getString("itemtype")))
					{
						ArrayList lengthlist = new ArrayList();
						StringBuffer new_tableNames =new StringBuffer();
						Field temf=itemleng(busiFiledVo.getString("itemid"),busiFiledVo.getString("itemtype"),busiFiledVo.getString("itemdesc"),busiFiledVo.getString("itemlength"),busiFiledVo.getString("decimalwidth"));
						lengthlist.add(temf);
						Table table=new Table(busiFiledVo.getString("fieldsetid"));
						if(dbWizard.isExistTable(busiFiledVo.getString("fieldsetid")))
						{
							String exist_tableNames=getExistColumnNameStr(busiFiledVo.getString("fieldsetid"));
							exist_tableNames=exist_tableNames.toLowerCase();
							int i=0;
							for(Iterator t=lengthlist.iterator();t.hasNext();)
							{
								Field aField=(Field)t.next();
								String columnName=aField.getName();
								new_tableNames.append(columnName.toLowerCase()+",");
								if(exist_tableNames.indexOf(columnName.toLowerCase()+",")!=-1)
								{
									i++;
									table.addField(aField);
								}
								if(i>0)
								{
									dbWizard.alterColumns(table);
								}
								
							}
						}
					}else if("A".equalsIgnoreCase(busiFiledVo.getString("itemtype"))){
						
						Table table=new Table(busiFiledVo.getString("fieldsetid"));
						if(dbWizard.isExistTable(busiFiledVo.getString("fieldsetid")))
						{
							table.addField(fi.cloneField());
							dbWizard.alterColumns(table);
						}
					}
					
					//xuj add 2014-08-09当修改Q03中数字型（长度、小数点精确度）、字符型（长度）时同步其他关联表结构
					if("Q03".equalsIgnoreCase(busiFiledVo.getString("fieldsetid"))){
						sycQ(busiFiledVo,"Q05");
						sycQ(busiFiledVo,"Q07");
						sycQ(busiFiledVo,"Q09");
						sycQ(busiFiledVo,"Q03_arc");
						sycQ(busiFiledVo,"Q05_arc");
						sycQ(busiFiledVo,"Q07_arc");
						sycQ(busiFiledVo,"Q09_arc");
						// zxj 20190430 增加对集中数据处理结果表的字段结构同步
						if(dbWizard.isExistTable("kq_analyse_result", false))
						    sycQ(busiFiledVo, "kq_analyse_result");
					}
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			if(fieldsetid.length()==6)
			{
				String sub=fieldsetid.substring(0,3);
				
				if("R40".equalsIgnoreCase(sub))
				{
					fieldsetid=sub;
				}
			}
			String itemid=(String) reqhm.get("itemid");
			reqhm.remove("fieldsetid");
			reqhm.remove("itemid");
			RecordVo busiFieldVo=new RecordVo("t_hr_busifield");
			busiFieldVo.setString("fieldsetid",fieldsetid);
			busiFieldVo.setString("itemid",itemid);
			try {
				busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			hm.put("codesetsel",busiselstr.getCodeStr(dao,busiFieldVo.getString("codesetid")+"/"+busiFieldVo.getString("itemlength")));
			try {
				hm.put("relating",busiselstr.getRelatingCode(dao,busiFieldVo.getString("codesetid")+"/"+busiFieldVo.getString("itemlength")));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			hm.put("date",busiselstr.getDateSel(busiFieldVo.getString("itemlength")));
			hm.put("busiFieldVo",this.putRecord(busiFieldVo));
			hm.put("useflag",busiFieldVo.getString("useflag"));
			}
	
	}
	public RecordVo putRecord(RecordVo busiFiledVo){
		String itemtype=busiFiledVo.getString("itemtype");
		String codeflag=busiFiledVo.getString("codeflag");
		String codesetid=busiFiledVo.getString("codesetid");
		if("A".equals(itemtype)){
			//业务字典，修改指标，保存后指标类型为空，此处修改   jingq upd 2014.10.13
			if("0".equals(codesetid)){
				//busiFiledVo.setString("itemtype","A/S");
				busiFiledVo.setString("itemtype","A.S");
			}else{
				if("0".equals(codeflag)){
					//busiFiledVo.setString("itemtype","A/C");
					busiFiledVo.setString("itemtype","A.C");
				}else{
					//busiFiledVo.setString("itemtype","A/R");
					busiFiledVo.setString("itemtype","A.R");
				}
			}
		}
		
		return busiFiledVo;
	}
	/*
	 * 更改构库指标的长度
	 * 
	 * */
	public Field itemleng(String itemid,String itemtype,String itemdesc,String itemlength,String decimalwidth)
	{
		Field obj=new Field(itemid,itemdesc);
		int width = Integer.parseInt(decimalwidth);
		int length = Integer.parseInt(itemlength);
		if("N".equalsIgnoreCase(itemtype))
		{
			if(width!=0&&width>0)
			{
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(width);
				obj.setLength(length);
			}else{
				obj.setDatatype(DataType.INT);
				obj.setDecimalDigits(width);
				obj.setLength(length);
			}
		}
		return obj;
	}
	//得到表的列名字符串
	public String getExistColumnNameStr(String tableName)
	{
		
		String sql="select * from "+tableName+" where 1=2";
		StringBuffer names=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet recset=dao.search(sql);
			ResultSetMetaData metaData=recset.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				names.append(metaData.getColumnName(i)+",");
			}
			if(metaData!=null)
				metaData=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return names.toString();
	}
	
	/**
	 * 修改Q03表结构是需同步处理Q05、Q07、Q09
	 * 以及归档表 Q03_arc、Q05_arc、Q07_arc、Q09_arc
	 */
	private void sycQ(RecordVo busiFiledVo,String tablename){
		try{
			DbWizard dbWizard= new DbWizard(this.getFrameconn());
			if(dbWizard.isExistTable(tablename)){
				FieldItem fi = DataDictionary.getFieldItem(busiFiledVo.getString("itemid"));
				if(fi!=null){
					if("N".equalsIgnoreCase(busiFiledVo.getString("itemtype")))
					{
						ArrayList lengthlist = new ArrayList();
						StringBuffer new_tableNames =new StringBuffer();
						Field temf=itemleng(busiFiledVo.getString("itemid"),busiFiledVo.getString("itemtype"),busiFiledVo.getString("itemdesc"),busiFiledVo.getString("itemlength"),busiFiledVo.getString("decimalwidth"));
						lengthlist.add(temf);
						
						Table table=new Table(tablename);
							String exist_tableNames=getExistColumnNameStr(tablename);
							exist_tableNames=exist_tableNames.toLowerCase();
							int i=0;
							for(Iterator t=lengthlist.iterator();t.hasNext();)
							{
								Field aField=(Field)t.next();
								String columnName=aField.getName();
								new_tableNames.append(columnName.toLowerCase()+",");
								if(exist_tableNames.indexOf(columnName.toLowerCase()+",")!=-1)
								{
									i++;
									table.addField(aField);
								}
								if(i>0)
								{
									dbWizard.alterColumns(table);
								}
								
							}
					}else if("A".equalsIgnoreCase(busiFiledVo.getString("itemtype"))){
						Table table=new Table(tablename);
						
							table.addField(fi.cloneField());
							dbWizard.alterColumns(table);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
