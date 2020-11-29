package com.hjsj.hrms.transaction.info;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class SortInfoDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String strsql = (String)this.getFormHM().get("strsql");
			String cond_str = (String)this.getFormHM().get("cond_str");
			String sortstr = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("sortstr");
			sortstr = com.hrms.frame.codec.SafeCode.decode(sortstr);
			String userbase= (String)this.getFormHM().get("userbase");
			String sql = this.getoMianOrderbyStr(userbase.toUpperCase(),sortstr.toUpperCase(),strsql.toUpperCase());
			if(sortstr!=null){
				ContentDAO dao = new ContentDAO(this.frameconn);
				
				String s = "select a0000 from "+userbase+"a01 GROUP by a0000 having count(a0000)>1";
				this.frowset = dao.search(s);
				ArrayList a0100list = new ArrayList();
				if(this.frowset.next()){
					s = "select a0100 from "+userbase+"a01 order by a0000";
					this.frowset = dao.search(s);
					while(this.frowset.next()){
						a0100list.add(this.frowset.getString("a0100"));
					}
					int len=a0100list.size();
					ArrayList values = new ArrayList();
					for(int n=0;n<len;n++){
						ArrayList tmplist = new ArrayList();
						tmplist.add(new Integer(n+10));
						tmplist.add((String)a0100list.get(n));
						values.add(tmplist);
					}
					s = "update "+userbase+"a01 set a0000=? where a0100=?";
					dao.batchUpdate(s, values);
				}

				s = "select a0100 from "+userbase+"A01 where a0000 is null";
				this.frowset = dao.search(s);
				boolean f = false;
				a0100list.clear();
				while(this.frowset.next()){
					f = true;
					a0100list.add(this.frowset.getString("a0100"));
				}
				if(f){
					s = "select max(a0000) a0000 from "+userbase+"a01";
					this.frowset = dao.search(s);
					int maxa0000 = 9999;
					if(this.frowset.next())
						maxa0000 = this.frowset.getInt("a0000");
					s = "update "+userbase+"a01 set a0000=? where a0100=?";
					ArrayList values = new ArrayList();
					for(int i=a0100list.size()-1;i>=0;i--){
						ArrayList tmps = new ArrayList();
						tmps.add(new Integer(maxa0000+i+1));
						tmps.add((String)a0100list.get(i));
						values.add(tmps);
					}
					dao.batchUpdate(s, values);
				}

				this.frowset = dao.search(sql);
				a0100list.clear();
				ArrayList a0000list = new ArrayList();
				while(this.frowset.next()){
					a0100list.add(this.frowset.getString("A0100"));
					a0000list.add(new Integer(frowset.getInt("A0000")));
				}
				Object [] a0000s = a0000list.toArray();
				Arrays.sort(a0000s);
				ArrayList values = new ArrayList();
				for(int i=a0100list.size()-1;i>=0;i--){
					ArrayList tmps = new ArrayList();
					tmps.add((Integer)(a0000s[i]));
					tmps.add((String)a0100list.get(i));
					values.add(tmps);
				}
				sql ="update "+userbase+"a01 set a0000=? where a0100=?";
				dao.batchUpdate(sql, values);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
	}

	public String getoMianOrderbyStr(String userbase,String sort_fields,String cond_str) 
	{
		StringBuffer fieldsb = new StringBuffer(" ORDER BY ");
		StringBuffer select = new StringBuffer("SELECT A0000,"+userbase+"A01.A0100 A0100");
		StringBuffer sql = new StringBuffer(" FROM "+userbase+"A01");
		StringBuffer where = new StringBuffer(" WHERE ");
		if(sort_fields!=null && sort_fields.length()>0) 
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				if(!(temps[i]==null || "".equals(temps[i])))
				{
					String[] arr = temps[i].split(":");
					if(!(arr[0]==null || "".equals(arr[0])))
					{
						String sortmode = "";
						if(!(arr[0]==null || "".equals(arr[0])))
						{
							FieldItem fi = DataDictionary.getFieldItem(arr[0]);
							if(!"M".equalsIgnoreCase(fi.getItemtype()))
							{
								String fieldsetid = fi.getFieldsetid();
								String table = userbase+fieldsetid;
								String codesetid = fi.getCodesetid();
								if(!"0".equalsIgnoreCase(codesetid)){
									select.append(","+this.getORG(fi, userbase));
									if(sql.indexOf(table)==-1){
										sql.append(" LEFT JOIN "+table+" ON "+table+".A0100="+userbase+"A01.A0100");
										where.append(table+".I9999=(SELECT MAX(I9999) FROM "+table+" WHERE "+table+".A0100="+userbase+"A01.A0100)"+" AND ");
									}
									
									if("1".equalsIgnoreCase(arr[2]))
									{
										sortmode = "ASC";
									}else{
										sortmode = "DESC";
									}
									fieldsb.append(arr[0]+"_I "+sortmode+",");
								}else{
									if(sql.indexOf(table)==-1){
										sql.append(" LEFT JOIN "+table+" ON "+table+".A0100="+userbase+"A01.A0100");
										where.append(table+".I9999=(SELECT MAX(I9999) FROM "+table+" WHERE "+table+".A0100="+userbase+"A01.A0100)"+" AND ");
									}
									if("1".equalsIgnoreCase(arr[2]))
									{
										sortmode = "ASC";
									}else{
										sortmode = "DESC";
									}
									if(Sql_switcher.searchDbServer()==2&& "A".equals(fi.getItemtype())&&(fi.getCodesetid().length()==0|| "0".equals(fi.getCodesetid())))
										fieldsb.append(" nlssort("+table+"."+arr[0]+",'NLS_SORT=SCHINESE_PINYIN_M') "+sortmode+",");
									else
										fieldsb.append(table+"."+arr[0]+" "+sortmode+",");
								}
							}
						}		
					}					
				}				
			}	
			if(fieldsb.length()>0){
				int index = cond_str.indexOf("FROM");
				where.append(userbase+"A01.A0100 IN (SELECT "+userbase+"A01.A0100 "+cond_str.substring(index)+")");
				fieldsb.append(userbase+"A01.A0000");
				sql.append(where);
				sql.append(fieldsb);
				return select.append(sql).toString();
			}else
				return null;
		}else
			return null;
	}
	
	private String getORG(FieldItem item,String userbase){
		String fielditemid = item.getItemid();
		String fieldsetid = item.getFieldsetid();
		String codesetid = item.getCodesetid();
		String table = userbase+fieldsetid;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new Date());
		StringBuffer sb =new StringBuffer();
		if("B0110".equalsIgnoreCase(fielditemid) || "E01A1".equalsIgnoreCase(fielditemid)
				|| "E0122".equalsIgnoreCase(fielditemid) || "UN".equalsIgnoreCase(codesetid)
				||"UM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)) {
			sb.append("(select a0000 from (select codeitemid,a0000 from organization where ");
			sb.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='" + codesetid + "'");
			sb.append(" union select codeitemid,a0000 from vorganization where ");
			sb.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='" + codesetid + "') tt");
			sb.append(" where tt.codeitemid="+table+"."+fielditemid+") "+fielditemid+"_I");
		} else {
			sb.append("(select a0000 from (select codeitemid,a0000 from codeitem where ");
			sb.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date");
			sb.append(" and codesetid='" + codesetid + "') tt where tt.codeitemid="+table+"."+fielditemid+") "+fielditemid+"_I");
		}
		return sb.toString();
	}
}
