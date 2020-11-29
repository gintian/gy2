package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.general.inform.CommonSql;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchItemTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100 = (String)hm.get("a0100");
		a0100=a0100!=null?a0100:"";
		hm.remove("a0100");
		
		String dbname = (String)hm.get("dbname");
		dbname=dbname!=null?dbname:"";
		hm.remove("dbname");
		
		if(dbname.trim().length()<1){
			ArrayList dblist = this.userView.getPrivDbList();
			if(dblist.size()>0)
				dbname = (String)dblist.get(0);
		}
		
		String fieldid = (String)hm.get("fieldid");
		fieldid=fieldid!=null?fieldid:"";
		hm.remove("fieldid");
		if(fieldid.trim().length()<1){
			ArrayList setlist = (ArrayList)this.getFormHM().get("setlist");
			for(int i=0;i<setlist.size();i++){
				CommonData cod = (CommonData)setlist.get(i);
				if("A01".equalsIgnoreCase(cod.getDataValue()))
					continue;
				else{
					fieldid = cod.getDataValue();
					break;
				}
			}
		}

		ArrayList list=getFieldList(fieldid);
		if("A00".equalsIgnoreCase(fieldid)){
			OrgDataBo orgbo = new OrgDataBo(this.frameconn,this.userView);
			list.addAll(orgbo.a00ItemList("1"));
		}
		
		String itemtable = dbname+fieldid;
		if(fieldid!=null&& "t_vorg_staff".equalsIgnoreCase(fieldid))
			itemtable = fieldid;
		StringBuffer strsql=new StringBuffer();
		String maintable=dbname+"A01";
		String fields=getFields(list, maintable,fieldid);				
		strsql.append("select ");
		strsql.append(fields);
		strsql.append(" from ");
		strsql.append(itemtable);
		strsql.append(" a right join ");
		strsql.append(maintable);
		strsql.append(" on ");
		strsql.append(maintable);
		strsql.append(".A0100=");
		strsql.append(" a ");
		strsql.append(".A0100 ");
		strsql.append(" where ");
		strsql.append(maintable);
		strsql.append(".A0100 ");				
		strsql.append("='" );
		strsql.append(a0100);
		strsql.append("'");
		if(fieldid!=null&& "t_vorg_staff".equalsIgnoreCase(fieldid)){
			strsql.append(" and dbase='");
			strsql.append(dbname);
			strsql.append("'");
		}
		
		// 多媒体过滤照片
		if("A00".equalsIgnoreCase(fieldid)){
			strsql.append(" and a.flag!='P'");
		}
		
		String inforflag = (String) hm.get("inforflag");
        StringBuffer buf = new StringBuffer();
        if ("2".equalsIgnoreCase(inforflag) && !this.userView.isSuper_admin()) {
            TrainCourseBo bo = new TrainCourseBo(this.userView);
            String codeall = bo.getUnitIdByBusi();
            if ("".equals(codeall))
                throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
            else {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                String[] codearr = codeall.split("`");
                boolean vorg = false;
                for (int i = 0; i < codearr.length; i++) {
                    vorg = false;
                    if(codearr!=null&&codearr[i].length()!=3)
                        vorg = isVorg(dao,codearr[i]);
                    buf.append("("+CommonSql.whereCodeStr(this.userView,codearr[i],dbname,vorg)+") or ");
                }

                if(buf.toString().trim().endsWith("or"))
                    buf.setLength(buf.length()-4);
            
            }
        }
        
        if (buf != null && buf.length() > 0)
            strsql.append(" and " + buf);
        
		String recordCount="0";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    RowSet rs = dao.search(strsql.toString());
		    if(rs.next())
		        recordCount=rs.getString("i9999")==null?"0":rs.getString("i9999");
		} catch (Exception e)
		{
		    e.printStackTrace();		    
		}
		String visible="true";
		String pri = "2";
		if(!this.userView.isSuper_admin()){
			pri = this.userView.analyseTablePriv(fieldid);
		}
		if(!"2".equals(pri)){
			visible="false";
		}
		
		String isInsert = this.getIsInsert(itemtable,a0100);
		this.getFormHM().put("isInsert", isInsert);
		this.getFormHM().put("viewbutton",visible);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("dbname", dbname);
		this.getFormHM().put("itemtable", itemtable);
		this.getFormHM().put("itemsql", strsql.toString());
		this.getFormHM().put("itemlist",list);
		this.getFormHM().put("defitem", fieldid);
		this.getFormHM().put("recordCount", recordCount);
	}
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname){
		
		
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		if(setname!=null&& "t_vorg_staff".equalsIgnoreCase(setname)){
			return gzbo.torgItemList();
		}
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		//ArrayList list=DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
		ArrayList list = gzbo.itemList(fieldset);
		ArrayList fieldlist=new ArrayList();
		Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
		String units=othparam.getValue(Sys_Oth_Parameter.UNITS);
		String place=othparam.getValue(Sys_Oth_Parameter.PLACE);
		String reserveitem = "";

		if(!fieldset.isMainset()){
		    	FieldItem item3=new FieldItem();
			item3.setFieldsetid(setname);
			item3.setItemid("oper");
			item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
			item3.setItemtype("A");
			item3.setCodesetid("0");
			item3.setAlign("center");
			item3.setReadonly(true);
			String pri = "2";
			if(!this.userView.isSuper_admin()){
				pri = this.userView.analyseTablePriv(setname);
			}
			if(!"2".equals(pri)){//子集读权限如果存在写权限的指标也可以编辑这些指标
			    boolean isVisible=false;	
			    for(int i=0;i<list.size();i++)
			    {
			    	Field field=(Field)list.get(i);
			    	String itemid=field.getName();

			    	if("B0110".equalsIgnoreCase(itemid)){
			    		continue;
			    	}else if("E0122".equalsIgnoreCase(itemid)){
			    		continue;
			    	}else if("E01A1".equalsIgnoreCase(itemid)){
			    		continue;
			    	}else if("A0101".equalsIgnoreCase(itemid)){
			    		continue;
			    	}
			    	else if(field.getLength()==0)
			    		continue;

			    	String fieldPriv = this.userView.analyseFieldPriv(itemid);
			    	if("2".equals(fieldPriv))//指标写权限
			    	{
			    		isVisible=true;
			    		break;
			    	}				
			    }	
			    item3.setVisible(isVisible);
			}
			fieldlist.add(item3.cloneField());		    
		    
			Field tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setReadonly(true);			
			tempfield.setVisible(false);
			fieldlist.add(tempfield);

		}else{
			Field tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setVisible(false);
			fieldlist.add(tempfield);

			/**有排序功能时，让其对A0000字段的值可维护,也即手动排序*/
			tempfield=new Field("A0000",ResourceFactory.getProperty("kjg.gather.xuhao"));
			tempfield.setDatatype(DataType.INT);
			tempfield.setVisible(false);
			fieldlist.add(tempfield);
		}
		int I9999 = 1;
		String i9999str="";
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();
			
			if(fieldset.isMainset()){
				if("B0110".equalsIgnoreCase(itemid)){
					if(units!=null&& "1".equals(units))
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
				}else if("E01A1".equalsIgnoreCase(itemid)){
					if(place!=null&& "1".equals(place))
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
				}
			}else{
				if("B0110".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					field.setVisible(false);
				}else if("E0122".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					field.setVisible(false);
				}else if("E01A1".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					field.setVisible(false);
				}else if("A0101".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					field.setVisible(false);
				}
			}
			if("0".equals(this.userView.analyseFieldPriv(itemid,0))&& "0".equals(this.userView.analyseFieldPriv(itemid,1)))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(itemid,0))|| "1".equals(this.userView.analyseFieldPriv(itemid,1)))
				field.setReadonly(true);
//			if(!this.userView.analyseTablePriv(setname).equals("2"))
//				field.setReadonly(true);
			field.setSortable(true);
			if(field.getLabel().indexOf("<font color='red'>*</font>")!=-1){
				if(!field.isReadonly()){
					reserveitem+=field.getName()+",."+field.getLabel().replace("<font color='red'>*</font>", "")+"`";
				}
			}

			fieldlist.add(field);
			if(!fieldset.isMainset()){
				if("A0101".equalsIgnoreCase(itemid)&&I9999>0){
					Field tempfield=new Field("I9999","序号");
					tempfield.setDatatype(DataType.INT);
					tempfield.setReadonly(true);
					tempfield.setVisible(false);
					fieldlist.add(tempfield);
					I9999=0;
					i9999str = "i9999";
				}
			}
		}//i loop end.
		if(i9999str.trim().length()<1){
			Field tempfield=new Field("I9999","序号");
			tempfield.setDatatype(DataType.INT);
			tempfield.setReadonly(true);
			tempfield.setVisible(true);
			fieldlist.add(tempfield);
		}
		this.getFormHM().put("resitemid", reserveitem);
		return fieldlist;  
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	private String getFields(ArrayList list,String maintable,String itemtable)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if("A0100".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".A0100,");
			}else if("B0110".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".B0110,");
			}else if("E0122".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E0122,");
			}else if("E01A1".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E01A1,");
			}else if("A0101".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".A0101,");
			}
			else if("downole".equalsIgnoreCase(field.getName()))
			    buf.append("'' downole,");
			else if("upole".equalsIgnoreCase(field.getName()))
			    buf.append("'' upole,");
			else{
				if(!"oper".equalsIgnoreCase(field.getName())){
					if("state".equalsIgnoreCase(field.getName())){
						if("A00".equalsIgnoreCase(itemtable)){
							buf.append(" CASE WHEN ");
							buf.append(Sql_switcher.length("a."+field.getName()));
							buf.append("=1 THEN '0'"+Sql_switcher.getCatOp()+"a."+field.getName());
							buf.append(" ELSE a."+field.getName()+" END");
							buf.append(" AS "+field.getName()+",");
						}else{
							if(Sql_switcher.searchDbServer()==Constant.ORACEL
									||Sql_switcher.searchDbServer()==Constant.DB2){
								buf.append("TRUNC(a."+field.getName()+",0) as "+field.getName()+",");
							}else
								buf.append("CAST(a."+field.getName()+" AS INT) as "+field.getName()+",");
						}
					}else if("flag".equalsIgnoreCase(field.getName())){
						if("A00".equalsIgnoreCase(itemtable)){
							buf.append("(select SORTNAME from mediasort where FLAG=a.flag) as flag,");
						}else{
							buf.append("a."+field.getName()+",");
						}
					}else
						buf.append("a."+field.getName()+",");
				}
			}
		
		}//for i loop end.
		buf.append("'' as oper,");
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
//	是否出现插入按钮
	public String getIsInsert(String itemtable,String a0100)
	{
	    String isInsert = "0";
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    String sql = "select * from "+itemtable+" where a0100='"+a0100+"'";
	    try
	    {
		RowSet rs = dao.search(sql);
		if(rs.next())
		isInsert = "1";
	    } catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return isInsert;
	}
	
	private boolean isVorg(ContentDAO dao,String a_code){
        boolean flag = false;
        String codesetid = "";
        String codeitemid = "";
        if(a_code==null||a_code.length()<3)
            return false;
        codesetid = a_code.substring(0,2);
        codeitemid = a_code.substring(2);
        
        
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select codesetid from vorganization where codeitemid='");
        sqlstr.append(codeitemid);
        sqlstr.append("' and codesetid='");
        sqlstr.append(codesetid);
        sqlstr.append("'");
        try {
            this.frowset=dao.search(sqlstr.toString());
            if(this.frowset.next()){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
