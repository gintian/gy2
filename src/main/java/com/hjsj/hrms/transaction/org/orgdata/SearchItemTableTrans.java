package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
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
		String itemid = (String)hm.get("itemid");
		itemid=itemid!=null?itemid:"";
		hm.remove("itemid");
		
		String infor = (String)hm.get("infor");
		infor=infor!=null?infor:"2";
		hm.remove("infor");

		String fieldid = (String)hm.get("fieldid");
		fieldid=fieldid!=null?fieldid:"";
		hm.remove("fieldid");
		if(fieldid.trim().length()<1){
			ArrayList setlist = (ArrayList)this.getFormHM().get("setlist");
			for(int i=0;i<setlist.size();i++){
				CommonData cod = (CommonData)setlist.get(i);
				if("B01".equalsIgnoreCase(cod.getDataValue())|| "K01".equalsIgnoreCase(cod.getDataValue()))
					continue;
				else{
					fieldid = cod.getDataValue();
					break;
				}
			}
		}
		OrgDataBo orgbo = new OrgDataBo(this.frameconn,this.userView);
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldid);
		if("".equals(infor))
		{
		   if("b".equalsIgnoreCase(fieldid.substring(0, 1)))
		   		infor="2";
		   else  if("k".equalsIgnoreCase(fieldid.substring(0, 1)))
		       	infor="3";
		}
		ArrayList itemlist = orgbo.fieldList(fieldset,infor);
		
		String resitemid = "";
		for(int i=0;i<itemlist.size();i++){
			Field field = (Field)itemlist.get(i);
			if(field!=null&&field.getLabel().indexOf("<font color='red'>*</font>")!=-1){
				if(!field.isReadonly()){
					resitemid+=field.getName()+",."+field.getLabel().replace("<font color='red'>*</font>", "")+"`";
				}
			}
		}
		if("B00".equalsIgnoreCase(fieldset.getFieldsetid())|| "K00".equalsIgnoreCase(fieldset.getFieldsetid())){
			itemlist.addAll(orgbo.a00ItemList(infor));
		}
		this.getFormHM().put("resitemid", resitemid);
		
		String maintable="";
		String mainitem="";
		
		if("2".equals(infor)){
			maintable="B01";
			mainitem="B0110";
		}else if("3".equals(infor)){
			maintable="K01";
			mainitem="E01A1";
		}
		
		StringBuffer strsql=new StringBuffer();
		String fields=getFields(itemlist, maintable,fieldid,infor);				
		strsql.append("select ");
		strsql.append(fields);
		strsql.append(" from ");
		strsql.append(fieldid);
		strsql.append(" a right join ");
		strsql.append(maintable);
		strsql.append(" on ");
		strsql.append(maintable);
		strsql.append("."+mainitem+"=");
		strsql.append(" a");
		strsql.append("."+mainitem);
		strsql.append(" where ");
		strsql.append(maintable);
		strsql.append("."+mainitem);				
		strsql.append("='" );
		strsql.append(itemid);
		strsql.append("'");
		
		String priItem = this.userView.analyseTablePriv(fieldid);
		String isInsert = this.getIsInsert(fieldid,mainitem,itemid);
		this.getFormHM().put("isInsert", isInsert);
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("itemtable", fieldid);
		this.getFormHM().put("itemsql", strsql.toString());
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("defitem", fieldid);
		this.getFormHM().put("infor", infor);
		this.getFormHM().put("priItem", priItem);
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	private String getFields(ArrayList list,String maintable,String itemtable,String infor)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			if("B0110".equalsIgnoreCase(field.getName())){
				if("2".equals(infor)){
					buf.append(maintable);
					buf.append(".B0110");
				}else{
					buf.append("(select parentid from organization where codeitemid=");
					buf.append(maintable);
					buf.append(".E0122 group by parentid) as B0110");
				}
			}else if("E01A1".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E01A1");
			}else if("E0122".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E0122");
			}else if("oper".equalsIgnoreCase(field.getName()))
			    buf.append("'' oper");
			else if("downole".equalsIgnoreCase(field.getName()))
			    buf.append("'' downole");
			else if("upole".equalsIgnoreCase(field.getName()))
			    buf.append("'' upole");
			else{
				
				if("state".equalsIgnoreCase(field.getName())){
					if("K00".equalsIgnoreCase(itemtable)|| "B00".equalsIgnoreCase(itemtable)){
						buf.append(" CASE WHEN ");
						buf.append(Sql_switcher.length("a."+field.getName()));
						buf.append("=1 THEN '0'"+Sql_switcher.getCatOp()+"a."+field.getName());
						buf.append(" ELSE a."+field.getName()+" END");
						buf.append(" AS "+field.getName());
					}else{
						buf.append("a."+field.getName());
					}
				}else if("flag".equalsIgnoreCase(field.getName())){
					if("K00".equalsIgnoreCase(itemtable)|| "B00".equalsIgnoreCase(itemtable)){
						buf.append("(select SORTNAME from mediasort where FLAG=a.flag) as flag");
					}else{
						buf.append("a."+field.getName());
					}
				}else{
					buf.append("a."+field.getName());
				}
			}
			buf.append(",");
		}//for i loop end.
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	//是否出现插入按钮
	public String getIsInsert(String fieldid,String mainitem,String itemid)
	{
	    String isInsert = "0";
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    String sql = "select * from "+fieldid+" where "+mainitem+"='"+itemid+"'";
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
}
