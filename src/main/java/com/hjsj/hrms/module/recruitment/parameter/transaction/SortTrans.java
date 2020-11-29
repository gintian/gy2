package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SortTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		reqhm.remove("flag");
		
		String sortitem = (String)reqhm.get("sortitem");
		sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
		reqhm.remove("sortitem");
		sortitem = SafeCode.decode(sortitem);
		
		ArrayList fieldlist = new ArrayList();
		ArrayList itemlist = new ArrayList();
		
		
		if("1".equalsIgnoreCase(flag)){
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.fieldList(this.frameconn);
			fieldlist.remove(0);
		}else if("2".equalsIgnoreCase(flag)){
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.subsetFieldList(this.frameconn);
		}else if("3".equalsIgnoreCase(flag)){
			String setname=(String)reqhm.get("setname");
			setname=setname!=null&&setname.trim().length()>0?setname:"";
			reqhm.remove("setname");
			
			GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
			//ArrayList list=DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
			ArrayList list = gzbo.itemList(setname);
			for(int i=0;i<list.size();i++){
				Field fielditem = (Field)list.get(i);
				if(sortitem.indexOf(fielditem.getName())!=-1)
					continue;
				
				CommonData dataobj = new CommonData(fielditem.getName()+":"+fielditem.getLabel(),fielditem.getLabel());
				itemlist.add(dataobj);
			}
		}else if("r1".equalsIgnoreCase(flag)){
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.sortFieldList1(this.userView,"1");
		}else if("z3".equalsIgnoreCase(flag)|| "4".equalsIgnoreCase(flag)){ //人员
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.sortFieldList(this.userView,"1");
		}else if("21".equalsIgnoreCase(flag)){ //单位
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.sortFieldList(this.userView,"2");
		}else if("41".equalsIgnoreCase(flag)){ //职位
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.sortFieldList(this.userView,"3");
		}else if("51".equalsIgnoreCase(flag)){//基准岗位//liuy 2015-1-30 7246：组织机构-岗位管理-基准岗位-高级花名册-取数-下一步-勾选排序指标-选择排序指标（没有列出基准岗位子集和指标） start
			TempvarBo tempvarbo = new TempvarBo();
			fieldlist = tempvarbo.sortFieldList(this.userView,"6");
		}else if("5".equalsIgnoreCase(flag)){ //人事异动
			String relatTableid=(String)reqhm.get("relatTableid");
			relatTableid=relatTableid!=null&&relatTableid.trim().length()>0?relatTableid:"";
			reqhm.remove("relatTableid");
			
			
			String stritem="";
			if(relatTableid.length()>0){
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(relatTableid),this.userView);
				ArrayList list = changebo.getAllFieldItem();
				
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					String itemdesc = "";
					if(fielditem.isChangeAfter()&&!fielditem.isMemo()){
						if(stritem.indexOf(fielditem.getItemid()+"_2")!=-1)
							continue;
						stritem+=fielditem.getItemid()+"_2,";
						if(sortitem.indexOf(fielditem.getItemid()+"_2")!=-1)
							continue;
						itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
						CommonData dataobj = new CommonData(fielditem.getItemid()+"_2:"+ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc(),
								ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc());
						itemlist.add(dataobj);
					}else if(fielditem.isChangeBefore()){
						if(stritem.indexOf(fielditem.getItemid()+"_1")!=-1)
							continue;
						stritem+=fielditem.getItemid()+"_1,";
						if(sortitem.indexOf(fielditem.getItemid()+"_1")!=-1)
							continue;
						if(fielditem.isMainSet()){
							itemdesc=fielditem.getItemdesc();
						}else{
							itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
						}
						CommonData dataobj = new CommonData(fielditem.getItemid()+"_1:"+itemdesc,itemdesc);
						itemlist.add(dataobj);
					} else {
						itemdesc=fielditem.getItemdesc();
						if(sortitem.indexOf(fielditem.getItemid())!=-1)
							continue;
						if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())
								&&!fielditem.isMemo()){
							CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
							itemlist.add(dataobj);
						}
					}
					
				}
			}
		}else if("81".equalsIgnoreCase(flag)){ //考勤
			String tabID=(String)reqhm.get("tid");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String fieldsetid=this.getFieldSetids(dao, tabID);
			if("Q05".equalsIgnoreCase(fieldsetid) || "Q07".equalsIgnoreCase(fieldsetid) 
					|| "Q09".equalsIgnoreCase(fieldsetid)){
				fieldsetid = "Q03";
			}
			ArrayList list = this.userView.getPrivFieldList(fieldsetid);
			for(int i=0;i<list.size();i++){
				FieldItem field = (FieldItem)list.get(i);
				if(field==null)
					continue;
				if(sortitem.indexOf(field.getItemid())!=-1)
					continue;
				if("Q07".equalsIgnoreCase(fieldsetid)|| "Q09".equalsIgnoreCase(fieldsetid))
				{
					String ItemId=field.getItemid();
					if("a0101".equalsIgnoreCase(ItemId)|| "state".equalsIgnoreCase(ItemId)|| "A0100".equalsIgnoreCase(ItemId)|| "NBASE".equalsIgnoreCase(ItemId)||
							"E0122".equalsIgnoreCase(ItemId)|| "E01A1".equalsIgnoreCase(ItemId)|| "Q03Z3".equalsIgnoreCase(ItemId)|| "Q03Z5".equalsIgnoreCase(ItemId))
						continue;
				}
				CommonData dataobj = new CommonData(field.getItemid()+":"+field.getItemdesc(),field.getItemdesc());
				itemlist.add(dataobj);
			}
		}else if("15".equalsIgnoreCase(flag)){ //个税表
			TaxMxBo taxbo=new TaxMxBo(this.frameconn);
			ArrayList list = taxbo.getFieldlist();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
				if(field==null)
					continue;
				if("Tax_max_id".equalsIgnoreCase(field.getName()))
					continue;
				if("Flag".equalsIgnoreCase(field.getName()))
					continue;
				if(sortitem.indexOf(field.getName())!=-1)
					continue;
				CommonData dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
				itemlist.add(dataobj);
			}
		}else if("zfw".equalsIgnoreCase(flag)){ //政法委的查询使用
			String model=(String)reqhm.get("model");
			model=model!=null&&model.trim().length()>0?model:"";
			reqhm.remove("model");
			
			String setid=(String)reqhm.get("setid");
			setid=setid!=null&&setid.trim().length()>0?setid:"";
			reqhm.remove("setid");
			
			if("2".equalsIgnoreCase(model))  //如果为查询使用模块  需加上主集代码型指标
			{
				ArrayList list=this.userView.getPrivFieldList("A01",Constant.USED_FIELD_SET);
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					if(fielditem!=null){
						if("M".equalsIgnoreCase(fielditem.getItemtype()))
							continue;
						CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),
								fielditem.getItemdesc());
						itemlist.add(dataobj);
					}
				}
			}
			ArrayList list=this.userView.getPrivFieldList(setid,Constant.USED_FIELD_SET);
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)list.get(i);
				if(fielditem!=null){
					if("M".equalsIgnoreCase(fielditem.getItemtype()))
						continue;
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),
							fielditem.getItemdesc());
					itemlist.add(dataobj);
				}
			}
			
		}else if("xuj".equals(flag)){
			String salaryid=(String)reqhm.get("salaryid");
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
			String checksalary=(String)reqhm.get("checksalary");
			checksalary=checksalary!=null&&checksalary.trim().length()>0?checksalary:"";
			this.formHM.put("salaryid", salaryid);
			reqhm.remove("salaryid");
			String xuj = (String)reqhm.get("xuj");
			xuj = xuj!=null&&xuj.trim().length()>0?xuj:"";
			this.formHM.put("xuj", xuj);
			reqhm.remove("xuj");
			SalaryCtrlParamBo scpb = new SalaryCtrlParamBo(this.getFrameconn(),
					Integer.parseInt(salaryid));//取得默认排序
			sortitem = scpb.getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
			sortitem = sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
			ArrayList templist = new ArrayList();//改变格式为 b0110 desc
			ArrayList sortitemlist = new ArrayList();//改变格式为 b0110:单位:0
			String[] sortitems = sortitem.split(",");
			for(int i=0;i<sortitems.length;i++){
				String temp = sortitems[i];
				if(temp.trim().length()>0){
					templist.add(temp);
				}
			}
			
			if(salaryid.length()>0){
				String[] arr =salaryid.split(",");
				String itemstr="";
				for(int j=0;j<arr.length;j++){
					String id = arr[j];
					if(id!=null&&id.trim().length()>0){
						SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(id),this.userView);
						String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
						ArrayList list = gzbo.getFieldlist();
						String gz_tablename = gzbo.getGz_tablename();
						if("analysis".equalsIgnoreCase(checksalary)){
							gz_tablename = "SalaryHistory";
						}
						DbWizard dbw = new DbWizard(this.getFrameconn());
						boolean f  = false;//标示相应的临时表中是否已有B0110_O，E0122_O字段
						RecordVo vo = new RecordVo(gz_tablename);
						if(vo.hasAttribute("b0110_o")&&vo.hasAttribute("e0122_o")&&vo.hasAttribute("dbid")){
							f = true;
						}
						for(int i=0;i<list.size();i++){
							Field field = (Field)list.get(i);
							if(field.getDatatype()==DataType.CLOB)
								continue;
							if("a01z0".equalsIgnoreCase(field.getName())&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
								continue;
							if("a0000".equalsIgnoreCase(field.getName())|| "add_flag".equalsIgnoreCase(field.getName()))//过滤人员序号和追加标记指标 xuj2009-10-9
								continue;
							// FengXiBin add 2088-02-15
							if(!(/*field.getName().equalsIgnoreCase("a0000") || */"a0100".equalsIgnoreCase(field.getName())))
							{
								boolean b = false;
								for(int a=0;a<templist.size();a++){
									String str = (String)templist.get(a);//格式为 b0110 desc
									if(str.toLowerCase().indexOf(field.getName().toLowerCase())!=-1){
										if(str.toLowerCase().indexOf("desc")!=-1){
											str = str.replace(" desc", ":"+field.getLabel()+":0");
										}else{
											str = str.replace(" asc", ":"+field.getLabel()+":1");
										}
										sortitemlist.add(str);
										b=true;
										break;
									}
								}
								if(b)
									continue;
								if(itemstr.indexOf(field.getName())!=-1)
									continue;
								CommonData dataobj = null;
								//xujian 2009-9-14
								if("NBASE".equalsIgnoreCase(field.getName())){
									if(f)
										dataobj = new CommonData("dbid:"+field.getLabel(),field.getLabel());
									else
										dataobj = new CommonData("dbname.dbid:"+field.getLabel(),field.getLabel());
								}else if("E0122".equalsIgnoreCase(field.getName())){
									if(f){
										dataobj = new CommonData("E0122_O:"+field.getLabel(),field.getLabel());
									}else{
										dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
									}
								}else if("B0110".equalsIgnoreCase(field.getName())){
									if(f){
										dataobj = new CommonData("B0110_O:"+field.getLabel(),field.getLabel());
									}else{
										dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
									}
								}else{
									dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
								}
								itemlist.add(dataobj);
								itemstr+=field.getName()+",";
							}

						}
						
					}
				}
			}
			sortitem="";
			for(int i=0;i<sortitemlist.size();i++){
				sortitem+=(String)sortitemlist.get(i)+"`";
			}
		}else if("outer_train".equalsIgnoreCase(flag)){//外部培训
		    String setid = (String)reqhm.get("setid");
            setid = setid != null && setid.trim().length() > 0 ? setid : "";
            reqhm.remove("setid");
            
            ArrayList list = this.userView.getPrivFieldList(setid,Constant.USED_FIELD_SET);
            for(int i=0;i<list.size();i++){
                FieldItem fielditem = (FieldItem)list.get(i);
                if(fielditem!=null){
                    if("M".equalsIgnoreCase(fielditem.getItemtype()))
                        continue;
                    CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),
                            fielditem.getItemdesc());
                    itemlist.add(dataobj);
                }
            }       
		}else if("zppostsort".equals(flag)){//招聘外网显示指标排序（来自参数配置）

            try{
                
                String pos_listfield_sort=(String) reqhm.get("selectedFields");
                StringBuffer fieldNames=new StringBuffer("");
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                StringBuffer whl=new StringBuffer("");
                if(pos_listfield_sort==null||pos_listfield_sort.trim().length()<=0){
                    
                }else{
                    String[] names;
                    String[] idAndSorts;//存放传过来的id和排序方式id:[desc||asc]
                    HashMap sortMap=new HashMap();
                   
                    if(pos_listfield_sort.indexOf(",")==-1)
                    {
                        idAndSorts=pos_listfield_sort.split(":");
                        whl.append(",'"+idAndSorts[0].trim()+"'");
                        sortMap.put(idAndSorts[0].trim().toLowerCase(), idAndSorts[1]);//存放的是[KEY:VALUE][ID:SORT]
                        names=new String[1];
                        names[0]=pos_listfield_sort;
                    }
                    else
                    {
                        String[] fields=pos_listfield_sort.split(",");
                        for(int i=0;i<fields.length;i++)
                        {
                            idAndSorts=fields[i].split(":");
                            whl.append(",'"+idAndSorts[0].trim().toUpperCase()+"'");
                            sortMap.put(idAndSorts[0].trim().toLowerCase(), idAndSorts[1]);//存放的是[KEY:VALUE][ID:SORT]
                        }
                        names=fields;
                    }
                    RowSet rs = null;
                    
                       rs=dao.search("select itemid,itemdesc from t_hr_busiField where itemid in ("+(whl.substring(1))+") and useflag='1' and fieldsetid='Z03'");
                       HashMap hm=new HashMap();
                        while(rs.next())
                        {
                            String itemid=rs.getString("itemid").toLowerCase();
                            String sort =(String) sortMap.get(itemid);
                            String itemdesc=rs.getString("itemdesc");
                            if("ASC".equalsIgnoreCase(sort)){
                                itemdesc=itemid+":"+itemdesc+":1";
                            }else{
                                itemdesc=itemid+":"+itemdesc+":0";
                            }
                            hm.put(itemid,itemdesc);
                        }
                        for(int i=0;i<names.length;i++)
                        {
                            idAndSorts=names[i].split(":");
                            fieldNames.append((String)hm.get(idAndSorts[0].toLowerCase())+"`");
                        }
                    }
                StringBuffer sqlbuffer=new StringBuffer();
                sqlbuffer.append("select * from t_hr_busiField where fieldsetid='Z03' and itemtype<>'M' and useflag=1 and itemid<>'Z0321'");
                if(whl.length()>0){
                    sqlbuffer.append(" and lower(itemid) not in (");
                    sqlbuffer.append((whl.substring(1).toLowerCase()));
                    sqlbuffer.append(")");
                }
                
                this.frowset=dao.search(sqlbuffer.toString());
                while(this.frowset.next())
                {
                    if("0".equals(this.frowset.getString("state")))
                        continue;
                    if("Z0301".equalsIgnoreCase(this.frowset.getString("itemid")))
                        continue;
                     CommonData dataobj = new CommonData(this.frowset.getString("itemid")+":"+this.frowset.getString("itemdesc"), this.frowset.getString("itemdesc"));
                     itemlist.add(dataobj);
                }
                sortitem=fieldNames.toString();
            }catch(Exception e){
                e.printStackTrace();
            }            
		}
		else{
			String salaryid=(String)reqhm.get("salaryid");
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
			String checksalary=(String)reqhm.get("checksalary");
			checksalary=checksalary!=null&&checksalary.trim().length()>0?checksalary:"";
			this.formHM.put("salaryid", salaryid);
			reqhm.remove("salaryid");
			String xuj = (String)reqhm.get("xuj");
			xuj = xuj!=null&&xuj.trim().length()>0?xuj:"";
			this.formHM.put("xuj", xuj);
			reqhm.remove("xuj");
			if(salaryid.length()>0){
				String[] arr =salaryid.split(",");
				String itemstr="";
				for(int j=0;j<arr.length;j++){
					String id = arr[j];
					if(id!=null&&id.trim().length()>0){
						SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(id),this.userView);
						String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
						ArrayList list = gzbo.getFieldlist();
						String gz_tablename = gzbo.getGz_tablename();
						if("analysis".equalsIgnoreCase(checksalary)){
							gz_tablename = "SalaryHistory";
						}
						DbWizard dbw = new DbWizard(this.getFrameconn());
						boolean f  = false;//标示相应的临时表中是否已有B0110_O，E0122_O字段
						RecordVo vo = new RecordVo(gz_tablename);
						if(vo.hasAttribute("b0110_o")&&vo.hasAttribute("e0122_o")&&vo.hasAttribute("dbid")){
							f = true;
						}
						for(int i=0;i<list.size();i++){
							Field field = (Field)list.get(i);
							if(field.getDatatype()==DataType.CLOB)
								continue;
							if("a01z0".equalsIgnoreCase(field.getName())&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
								continue;
							if("a0000".equalsIgnoreCase(field.getName())|| "add_flag".equalsIgnoreCase(field.getName()))//过滤人员序号和追加标记指标 xuj2009-10-9
								continue;
							// FengXiBin add 2088-02-15
							if(!(/*field.getName().equalsIgnoreCase("a0000") || */"a0100".equalsIgnoreCase(field.getName())))
							{
								if(sortitem.indexOf(field.getName())!=-1)
									continue;
								if(itemstr.indexOf(field.getName())!=-1)
									continue;
								CommonData dataobj = null;
								//xujian 2009-9-14
								if("NBASE".equalsIgnoreCase(field.getName())){
									if(f)
										dataobj = new CommonData("dbid:"+field.getLabel(),field.getLabel());
									else
										dataobj = new CommonData("dbname.dbid:"+field.getLabel(),field.getLabel());
								}else if("E0122".equalsIgnoreCase(field.getName())){
									if(f){
										dataobj = new CommonData("E0122_O:"+field.getLabel(),field.getLabel());
									}else{
										dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
									}
								}else if("B0110".equalsIgnoreCase(field.getName())){
									if(f){
										dataobj = new CommonData("B0110_O:"+field.getLabel(),field.getLabel());
									}else{
										dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
									}
								}else{
									dataobj = new CommonData(field.getName()+":"+field.getLabel(),field.getLabel());
								}
								itemlist.add(dataobj);
								itemstr+=field.getName()+",";
							}

						}
					}
				}
			}
		}
		if("1".equals(flag)|| "2".equals(flag)|| "4".equals(flag)|| "r1".equals(flag)
				|| "21".equals(flag)|| "41".equals(flag)|| "51".equals(flag)|| "z3".equals(flag)){
			this.getFormHM().put("checkflag","1");
		}else{
			this.getFormHM().put("checkflag","0");
		}
		this.getFormHM().put("fieldid","");
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("itemid","");
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("sortitem",sortitem);
	}
	private String getFieldSetids(ContentDAO dao,String tabid){
		String fieldsetid="";
		try {
			this.frowset=dao.search("select SetName,Field_Name from muster_cell where Tabid='"+tabid+"'");
			while(this.frowset.next()){
				String setname = this.frowset.getString("SetName");
				if(setname!=null&&setname.trim().length()>0)
				{
					fieldsetid=setname;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fieldsetid;
	}

}
