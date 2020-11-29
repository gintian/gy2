package com.hjsj.hrms.businessobject.general.approve.personinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;

public class ApproveSQLStr {	
	public static String[] getSelSumStr(ArrayList tablelist,String state,UserView uv,String pdbflag,String abkflag){

//		System.out.println(w);		
		String codeid=uv.getManagePrivCode();
		String codevalue=uv.getManagePrivCodeValue();
		String[] sumStr=new String[3];
		StringBuffer sbsql=new StringBuffer();
		String condition=" where "+Sql_switcher.isnull("state","'-1'")+"='0'";//state is not null and state='0' ";
		if("1".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='1'";//state is not null and state='1' ";
		}
		if("2".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='2'";//is not null and state='2' ";
		}
		if("3".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'3'")+"='3'";//(state is null or state='3' or state='')";
			//condition=" where state is not null and state='3' ";
		}
		if("4".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='4'";//state is not null and state='4' ";
		}
		if("5".equals(state)){
			condition=" where  "+Sql_switcher.isnull("state","'-1'")+"='5'";//state is not null and state='5' ";
		}
		if("6".equals(state)){
			condition=" where  "+Sql_switcher.isnull("state","'-1'")+"='6'";//state is not null and state='6' ";
		}
//		condition=condition+" and  a0100 in(select a0100 from tablename where e01a1 like '"+codevalue+"%' ) ";
		if("A".equalsIgnoreCase(abkflag)){
			
			if(uv.isSuper_admin()){
				//where=" from "+pdbflag+"a01 u  where "+orgs+" like '"+orgid+"%' )p";
				if("UM".equals(codeid)){
					condition=condition+" and  a0100 in(select a0100 from "+pdbflag+"a01 where e01a1 like '"+codevalue+"%' ) ";
			
				}else{
					condition=condition+" and  a0100 in(select a0100 from "+pdbflag+"a01 where e0122 like '"+codevalue+"%' ) ";
				}
			}else{
				String sw;
				try {
					sw = uv.getPrivSQLExpression(pdbflag,false);
					condition=condition+" and  a0100 in(select "+pdbflag+"a01.a0100 "+sw+")";	
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
			}
			
		}
		sbsql.append("from ");
		for(int i=0;i<tablelist.size();i++){
			String tablename = (String) tablelist.get(i);
//			condition=condition+" and  a0100 in(select a0100 from "+"USRA01"+" where e01a1 like '"+codevalue+"%' ) ";
			if(i==0){
				
				if(Sql_switcher.searchDbServer()== Constant.ORACEL){
					sbsql.append("(select (select count(*) from "+tablename+condition+") as editnum,'"+tablename.substring(tablename.length()-3,tablename.length())+"' as setid from dual");
				}else{
					sbsql.append("(select editnum=(select count(*) from "+tablename+condition+"),setid='"+tablename.substring(tablename.length()-3,tablename.length())+"'");
				}
			}else{
				sbsql.append(" union all ");
				if(Sql_switcher.searchDbServer()== Constant.ORACEL){
					sbsql.append("select (select count(*) from "+tablename+condition+") as editnum,'"+tablename.substring(tablename.length()-3,tablename.length())+"' as setid from dual");
				}else{
					sbsql.append("select editnum=(select count(*) from "+tablename+condition+"),setid='"+tablename.substring(tablename.length()-3,tablename.length())+"'");
				}
			}
		}
		sbsql.append(") sets");
		sbsql.append(" left join (select * from fieldset) fields on ");
		sbsql.append(" sets.setid = fields.fieldsetid ");
		sbsql.append(" where editnum<>0");
		String sql="select editnum,setid,fieldsetdesc as setdesc";
		String where=sbsql.toString();
		String column="editnum,setid,setdesc";
		//System.out.println(sql+" "+where);
		sumStr[0]=sql;
		sumStr[1]=where;
		sumStr[2]=column;
		return sumStr;
		
	}
	public static String[] getSelSetStr(ArrayList fielditemlist,String tablename, String setid){
		return getSelSetStr(fielditemlist,null,null,tablename,setid);
	}
	public static String[] getSelSetStr(ArrayList fielditemlist,String unit,String tablename ,String setid){
		return getSelSetStr(fielditemlist,unit,null,tablename,setid);
	}
	
	public static String[] getSelSetStr(ArrayList fileditemlist, String  unit,String department,String tablename,String setid){
		String[] sqlStr = new String [3];
		StringBuffer sbsql =new StringBuffer();
		StringBuffer sbcolumn=new StringBuffer();
		StringBuffer sbwhere =new StringBuffer();
		if("A".equals(setid.substring(0,1))){
			if("A01".equalsIgnoreCase(setid)){
				sbcolumn.append("a0100,");
			}else{
				sbcolumn.append("a0100,i9999,");
			}
		}
		if("B".equals(setid.substring(0,1))){
			if("B01".equalsIgnoreCase(setid)){
				sbcolumn.append("b0110,");
				
			}else{
				sbcolumn.append("b0110,i9999,");
			}
		}
		if("K".equals(setid.substring(0,1))){
			if("K01".equalsIgnoreCase(setid)){
				sbcolumn.append("e01a1,");
			}else{
				sbcolumn.append("e01a1,i9999,");
			}
		}
		sbcolumn.append("modusername,modtime,");
		
		for(int i= 0;i<fileditemlist.size();i++){
			String itemid= (String) fileditemlist.get(i);
				if(i==0){
					sbcolumn.append(itemid);
				}else{
					sbcolumn.append(","+itemid);
				}
			
		}
		sbsql.append("select "+sbcolumn.toString());
		sbsql.append(" "+sbcolumn.toString());
		sbwhere.append(" from (select * from "+tablename+" where  state=0) p");
		if(unit!=null){
			sbwhere.append("  where p.b0110 like'"+unit+"%' ");
			
		}
		if(department!=null){
			sbwhere.append(" where p.e0122='"+department+"'");
		}
		sqlStr[0]=sbsql.toString();
		sqlStr[1]=sbwhere.toString();
		sqlStr[2]=sbcolumn.toString();
		return sqlStr;
		
	}
	public static String[] getRetStr(UserView userView,ArrayList fileditemlist, String  unit,String department,String a0100,String tablename,String setid,String state){
		String[] sqlStr = new String [3];
		StringBuffer sbsql =new StringBuffer();
		StringBuffer sbcolumn=new StringBuffer();
		StringBuffer sbwhere =new StringBuffer();
		sbsql.append("select ");
		/*打开这里可以增加修改任何修改时间。
		 * sbsql.append("p.modtime as modtime,p.modusername as modusername,");
		 * sbcolumn.append("modtime,modusername,");
		 */
		
		if("A".equals(setid.substring(0,1))){
			sbcolumn.append("a0100,");
			sbsql.append("p.a0100 as a0100,");
			if(!"A01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		if("B".equals(setid.substring(0,1))){
			sbcolumn.append("b0110,");
			sbsql.append("p.b0110 as b0110,");
			if(!"B01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		if("K".equals(setid.substring(0,1))){
			sbcolumn.append("e01a1,");
			sbsql.append("p.e01a1 as e01a1,");
			if(!"K01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		ArrayList templist = getcolums(fileditemlist);
		sbcolumn.append(templist.get(0));
		sbsql.append(templist.get(1));
		sbwhere.append(getWhere(userView,tablename,unit,department,a0100,setid,state));
		//System.out.println(sbsql+" "+sbwhere);
		sqlStr[0]=sbsql.toString();
		sqlStr[1]=sbwhere.toString();
		sqlStr[2]=sbcolumn.toString();
		return sqlStr;
	}
	private static ArrayList getcolums(ArrayList fileditemlist){
		ArrayList relist=new ArrayList();
		StringBuffer sbcolumn=new StringBuffer();
		StringBuffer sbsql=new StringBuffer();
		for(int i= 0;i<fileditemlist.size();i++){
			String itemid= (String) fileditemlist.get(i);				
				if(i==0){
					sbcolumn.append(itemid);
					sbsql.append("p."+itemid);
				}else{
					sbcolumn.append(","+itemid);
					sbsql.append(",p."+itemid);
				}
			
		}
		relist.add(sbcolumn.toString());
		relist.add(sbsql.toString());
		return relist;
		
	}
	private static String getWhere(UserView userView,String tablename,String unit,String dep,String a0100,String setid,String state){
		String nbase=tablename.substring(0,3);
		String condition=" where "+Sql_switcher.isnull("state","'-1'")+"='0'";
		if("1".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='1'";
		}
		if("2".equals(state)){
			condition=" where  "+Sql_switcher.isnull("state","'-1'")+"='2'";
		}
		if("3".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'3'")+"='3'";
		}
		if("4".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='4'";
		}
		if("5".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='5'";
		}
		if("6".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='6'";
		}
		StringBuffer sbwhere=new StringBuffer();
		sbwhere.append(" from (select * from "+tablename+condition+" ) p");
		if("A".equals(setid.substring(0,1))){
			if(!"A01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from usra01) u on p.a0100=u.a0100 ");
				boolean ff=false;
				if(unit!=null){					
					ff=true;
					sbwhere.append(" where b0110 like'"+unit+"%'");
				}else if(dep!=null){					
					ff=true;
					sbwhere.append(" where e0122 like'"+dep+"%'");					
				}else if(a0100!=null){
					ff=true;
					sbwhere.append(" where a0100 ='"+a0100+"'");
				}
				if(!userView.isSuper_admin()){	
					String sw;
					try {
						sw = userView.getPrivSQLExpression(nbase,false);
						if(ff) {
                            sbwhere.append(" and exists(select "+nbase+"A01.a0100 "+sw+" and u.a0100="+nbase+"A01.a0100)");
                        } else {
                            sbwhere.append(" where exists(select "+nbase+"A01.a0100 "+sw+" and u.a0100="+nbase+"A01.a0100)");
                        }
					} catch (GeneralException e) {						
						e.printStackTrace();
					}
				}
			}else{				
				boolean ff=false;
				if(unit!=null){		
					ff=true;
					sbwhere.append(" where b0110 like'"+unit+"%'");
				}else if(dep!=null){	
					ff=true;
					sbwhere.append(" where e0122 like'"+dep+"%'");					
				}else if(a0100!=null){
					ff=true;
					sbwhere.append(" where a0100 ='"+a0100+"'");
				}
				if(!userView.isSuper_admin()){	
					String sw;
					try {
						sw = userView.getPrivSQLExpression(nbase,false);
						if(ff) {
                            sbwhere.append(" and a0100 in (select "+nbase+"A01.a0100 "+sw+")");
                        } else {
                            sbwhere.append(" where a0100 in (select "+nbase+"A01.a0100 "+sw+")");
                        }
					} catch (GeneralException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
            
		}
		if("K".equals(setid.substring(0,1))){
			if(!"K01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from k01) u on p.e01a1=u.e01a1 ");
				if(!(unit==null)){
				sbwhere.append(" where u.e01a1 like'"+unit+"%'");
				}
				if(!(dep==null)){
				sbwhere.append(" where u.e01a1 like'"+dep+"%'");
				}
				if(a0100!=null){
					sbwhere.append(" where u.e01a0 ='"+a0100+"'");
				}
			}else{
				if(!(unit==null)){
					sbwhere.append(" where e01a1 like'"+unit+"%'");
					}
					if(!(dep==null)){
					sbwhere.append(" where e01a1 like'"+dep+"%'");
					}
					if(a0100!=null){
						sbwhere.append(" where e01a0 ='"+a0100+"'");
					}
			}
			
		}
		if("B".equals(setid.substring(0,1))){
			if(!"B01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from b01) u on p.b0110=u.b0110 ");
				if(!(unit==null)){
				sbwhere.append(" where u.b0110 like'"+unit+"%'");
				}
				if(!(dep==null)){
					sbwhere.append(" where u.b0110 like'"+dep+"%'");
				}
				if(a0100!=null){
					sbwhere.append(" where u.b0110 ='"+a0100+"'");
				}
			}else{
				if(!(unit==null)){
					sbwhere.append(" where b0110 like'"+unit+"%'");
				}
				if(!(dep==null)){
					sbwhere.append(" where b0110 like'"+dep+"%'");
				}
				if(a0100!=null){
					sbwhere.append(" where b0110 ='"+a0100+"'");
				}
			}
			
		}
		return sbwhere.toString();
	}
	
	
	public static String[] getRetStr1(UserView userView,ArrayList fileditemlist, String  unit,String department,String a0100,String tablename,String setid,String state){
		String[] sqlStr = new String [3];
		StringBuffer sbsql =new StringBuffer();
		StringBuffer sbcolumn=new StringBuffer();
		StringBuffer sbwhere =new StringBuffer();
		sbsql.append("select ");
		/*打开这里可以增加修改任何修改时间。
		 * sbsql.append("p.modtime as modtime,p.modusername as modusername,");
		 * sbcolumn.append("modtime,modusername,");
		 */
		
		if("A".equals(setid.substring(0,1))){
			sbcolumn.append("a0100,");
			sbsql.append("p.a0100 as a0100,");
			if(!"A01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		if("B".equals(setid.substring(0,1))){
			sbcolumn.append("b0110,");
			sbsql.append("p.b0110 as b0110,");
			if(!"B01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		if("K".equals(setid.substring(0,1))){
			sbcolumn.append("e01a1,");
			sbsql.append("p.e01a1 as e01a1,");
			if(!"K01".equalsIgnoreCase(setid)){
				sbcolumn.append("i9999,");
				sbsql.append("p.i9999 as i9999,");
			}
		}
		
		ArrayList templist = getcolums(fileditemlist);
		if(templist.get(0)!=null&&templist.get(0).toString().length()>0)
		{
			sbcolumn.append(templist.get(0));
		}else
		{
			sbcolumn.append("a0101");
		}
        if(templist.get(1)!=null&&templist.get(1).toString().length()>0)
        {
        	sbsql.append(templist.get(1));
        }else
        {
        	sbsql.append("a0101");
        }	 		
		sbwhere.append(getWhere1(userView,tablename,unit,department,a0100,setid,state));
		sqlStr[0]=sbsql.toString();
		sqlStr[1]=sbwhere.toString();
		sqlStr[2]=sbcolumn.toString();
		return sqlStr;
	}
	
	
	private static String getWhere1(UserView userView,String tablename,String unit,String dep,String a0100,String setid,String state){
		String nbase=tablename.substring(0,3);
		ArrayList fieldlist=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);		
		String condition=" where "+Sql_switcher.isnull("state","'-1'")+"='0'";	
		if("1".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='1'";
		}
		if("2".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='2'";
		}
		if("3".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","3")+"=3";
		}
		if("4".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='4'";
		}
		if("5".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='5'";
		}
		if("6".equals(state)){
			condition=" where "+Sql_switcher.isnull("state","'-1'")+"='6'";
		}
		StringBuffer inWh=new StringBuffer();
		inWh.append(" where EXists (");
		/*for(int r=0;r<fieldlist.size();r++){
			FieldSet fs=(FieldSet)fieldlist.get(r);
			inWh.append("(select a0100 from "+nbase+fs.getFieldsetid()+"");
			inWh.append(" "+condition+")");
			if(r<fieldlist.size()-1)
				inWh.append(" union ");
		}	*/
		inWh.append("(select a0100 from "+nbase+"A01 NN");
		inWh.append(" "+condition+" and NN.a0100="+tablename+".a0100)");
		inWh.append(")");	
		StringBuffer sbwhere=new StringBuffer();
		if(fieldlist!=null&&fieldlist.size()>0)
		{
			sbwhere.append(" from (select * from "+tablename+inWh.toString()+" ) p");
		}else
		{
			sbwhere.append(" from (select * from "+tablename+condition+" ) p");
		}
		
		if("A".equals(setid.substring(0,1))){
			if(!"A01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from usra01) u on p.a0100=u.a0100 ");
				if(unit!=null){
					if(isunit(unit)){
						sbwhere.append(" where u.b0110 ='"+unit+"'");
					}else{
						sbwhere.append(" where u.e0122 ='"+unit+"'");
					}
				
				}
				if(dep!=null){
					if(isdep(dep)){
						sbwhere.append(" where e0122 ='"+dep+"'");
					}else if(isunit(dep)){
						sbwhere.append(" where b0110 ='"+dep+"'");
						
					}
				}
				if(a0100!=null){
					sbwhere.append(" where u.a0100 ='"+a0100+"'");
				}
			}else{
				if(unit!=null){
					if(isunit(unit)){
						sbwhere.append(" where p.b0110 ='"+unit+"'");
					}else if(isdep(unit)){
						sbwhere.append(" where p.e0122 ='"+unit+"'");
					}
				}
				if(dep!=null){
					if(isdep(dep)){
						sbwhere.append(" where e0122 ='"+dep+"'");
					}else if(isunit(dep)){
						sbwhere.append(" where b0110 ='"+dep+"'");
						
					}
				}
				if(a0100!=null){
					sbwhere.append(" where a0100 ='"+a0100+"'");
				}
			}
			if(!userView.isSuper_admin()){	
				
				String sw;
				try {
					sw = userView.getPrivSQLExpression(nbase,false);
					sbwhere.append(" and a0100 in (select "+tablename+".a0100 "+sw+")");
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		if("K".equals(setid.substring(0,1))){
			if(!"K01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from k01) u on p.e01a1=u.e01a1 ");
				if(unit!=null){
					sbwhere.append(" where u.e01a1 ='"+unit+"'");
				}
				if(dep!=null){
					sbwhere.append(" where u.e01a1 ='"+dep+"'");
				}
				if(a0100!=null){
					sbwhere.append(" where u.e01a1 ='"+a0100+"'");
				}
			}else{
				if(unit!=null){
					sbwhere.append(" where e01a1 ='"+unit+"'");
				}
				if(dep!=null){
					sbwhere.append(" where e01a1 ='"+dep+"'");
				}
				if(a0100!=null){
					sbwhere.append(" where e01a1 ='"+a0100+"'");
				}
			}
			
		}
		if("B".equals(setid.substring(0,1))){
			if(!"B01".equalsIgnoreCase(setid)){
				sbwhere.append(" left join ");
				sbwhere.append(" (select * from b01) u on p.b0110=u.b0110 ");
				if(unit!=null){
					sbwhere.append(" where u.b0110 ='"+unit+"'");
				}
				if(dep!=null){
					sbwhere.append(" where u.b0110 ='"+dep+"'");
				}
				if(a0100!=null){
					sbwhere.append(" where u.b0110 ='"+a0100+"'");
				}
			}else{
				if(unit!=null){
					sbwhere.append(" where b0110 ='"+unit+"'");
				}
				if(dep!=null){
					sbwhere.append(" where b0110 ='"+dep+"'");
				}
				if(a0100!=null){
					sbwhere.append(" where b0110 ='"+a0100+"'");
				}
			}
			
		}
		return sbwhere.toString();
	}
	public String[] getStatesql(ContentDAO dao,UserView uv,String orgid,String pdbflag) throws GeneralException{
		//String upsql="update "+pdbflag+"a01 set state='3' where state is null";
		String upsql="update "+pdbflag+"a01 set state='3' where "+Sql_switcher.isnull("state","'-1'")+"='-1'";
		try {
			dao.update(upsql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sqlc="select * from organization where ";
		if(orgid!=null&&orgid.trim().length()>0) {
            sqlc+="codeitemid like '"+orgid+"%' and codesetid='UN' and codeitemid<>'"+orgid+"'";
        } else {
            sqlc+="codeitemid=parentid and codesetid='UN'";
        }
		ArrayList tetlist=dao.searchDynaList(sqlc);
		String orgs="b0110";
		if(tetlist.size()==0){
			orgs="e0122";
		}
		String[] sql=new String[4];
		String sqlss="select org ,state0,state1,state2,state3,state4,state5";
		StringBuffer sbsql=new StringBuffer();
		sbsql.append("from (select org ,Sum(state0) as state0,sum(state1) as state1,sum(state2) as state2,sum(state3) as state3,sum(state4) as state4,");
		sbsql.append("sum(state5) as state5 ");
		sbsql.append("from (");
		sbsql.append("select "+orgs+" as org");
		StringBuffer column=new StringBuffer();
		column.append("org");
		String  where="";
		String orderby="";
		ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		String pri_Where="";
		if(uv.isSuper_admin()){
			pri_Where="select a0100 from "+pdbflag+"a01  where "+orgs+" like '"+orgid+"%'";
		}else{
			String sw=uv.getPrivSQLExpression(pdbflag,false);
			pri_Where=" select a0100 "+sw+"";
		}
		for(int i=0;i<6;i++){
			column.append(",state"+i);
			if(i<4)
			{  
				if(i==3)//state is null or state='3' or state=''
                {
                    sbsql.append(","+"(select count(state) from  "+pdbflag+"a01 uu where "+Sql_switcher.isnull("state","'3'")+"='3' and uu.a0100 = u.a0100) as state"+i);
                } else {
                    sbsql.append(","+"(select count(state) from  "+pdbflag+"a01 uu where state='"+i+"' and uu.a0100 = u.a0100) as state"+i);
                }
			}else
			{
				if(fieldlist!=null&&fieldlist.size()>0)
				{
					sbsql.append(",(select count(a0100) from(");
					for(int r=0;r<fieldlist.size();r++){
						FieldSet fs=(FieldSet)fieldlist.get(r);
						if(i==3)
						{
							sbsql.append("(select uu.a0100 as a0100 from  "+pdbflag+""+fs.getFieldsetid()+" uu where "+Sql_switcher.isnull("state","'3'")+"='3') ");
						}else
						{
//							sbsql.append("(select a0100 from  "+pdbflag+""+fs.getFieldsetid()+" uu where state='"+i+"' and uu.a0100 in ("+pri_Where+"))");
							sbsql.append("(select uu.a0100 as a0100 from  "+pdbflag+""+fs.getFieldsetid()+" uu where state='"+i+"') ");
						}						
						if(r<fieldlist.size()-1) {
                            sbsql.append(" union ");
                        }
					}		
					//sbsql.append(") auu"+i );
					sbsql.append(") auu"+i+" where auu"+i+".a0100=u.a0100 group by auu"+i+".a0100");
					sbsql.append(")as state"+i);
				}else
				{
					sbsql.append(" ,0 as state"+i);
				}
				
			}
				
		}
		
		if(uv.isSuper_admin()){
			where=" from "+pdbflag+"a01 u  where "+orgs+" like '"+orgid+"%' )p";
		}else{
			String ss=uv.getPrivExpression();
			String sw=uv.getPrivSQLExpression(pdbflag,false);
			where=" from (select "+pdbflag+"a01.a0100,"+orgs+" "+sw+") u  where "+orgs+" like '"+orgid+"%')p";
		}
		where=where+" group by org ) u";		
		sql[0]=sqlss;
		sql[1]=column.toString();
		sql[2]=sbsql.toString()+where;
		sql[3]=orderby;
		return sql;
	}
	
	public String[] getStatesql2(ContentDAO dao,UserView uv,String orgid,String pdbflag) throws GeneralException{
		String upsql="update "+pdbflag+"a01 set state='3' where state is null";
		try {
			dao.update(upsql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sqlc="select * from organization where ";
		if(orgid!=null&&orgid.trim().length()>0) {
            sqlc+="codeitemid like '"+orgid+"%' and codesetid='UN' and codeitemid<>'"+orgid+"'";
        } else {
            sqlc+="codeitemid=parentid and codesetid='UN'";
        }
		ArrayList tetlist=dao.searchDynaList(sqlc);
		String orgs="b0110";
		if(tetlist.size()==0){
			orgs="e0122";
		}
		String[] sql=new String[4];
		//String sqlss="select org ,state0,state1,state2,state3,state4,state5";
		String sqlss="select "+orgs+" org,sum(state0) state0,sum(state1) state1,sum(state2) state2,sum(state3) state3,sum(state4) state4,sum(state5) state5 ";
		StringBuffer sbsql=new StringBuffer();
		//sbsql.append("from (select "+orgs+" org,sum(state0) state0,sum(state1) state1,sum(state2) state2,sum(state3) state3,sum(state4) state4,sum(state5) state5 from (");
		sbsql.append("from (");
		sbsql.append("select "+orgs+",case when state='0' then con else 0 end state0,case when state='1' then con else 0 end state1,case when state='2' then con else 0 end state2,");
		sbsql.append("case when state='3' then con else 0 end state3,case when state='4' then con else 0 end state4,case when state='5' then con else 0 end state5 from (");
		sbsql.append("select "+orgs+",b.state,count(b.a0100) con from usra01 a left join (");
		sbsql.append("(select   a0100,state from  Usra01 where state='0' )");
		sbsql.append("union (select   a0100,state from  Usra01 where state='1' )");
		sbsql.append("union (select   a0100,state from  Usra01 where state='2' )");
		sbsql.append("union (select   a0100,'3' state from  Usra01 where "+Sql_switcher.isnull("state","'3'")+"='3')");
		
		
		StringBuffer column=new StringBuffer();
		column.append("org,state0,state1,state2,state3,state4,state5");
		String  where="";
		String orderby="";
		ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		String pri_Where="";
		if(uv.isSuper_admin()){
			pri_Where="select a0100 from "+pdbflag+"a01   where "+orgs+" like '"+orgid+"%'";
		}else{
			
			String sw=uv.getPrivSQLExpression(pdbflag,false);			
			pri_Where=" select a0100 "+sw+"";
		}
			
		if(fieldlist!=null&&fieldlist.size()>0) {
			for(int r=0;r<fieldlist.size();r++){
				FieldSet fs=(FieldSet)fieldlist.get(r);
//							sbsql.append("(select a0100 from  "+pdbflag+""+fs.getFieldsetid()+" uu where state='"+i+"' and uu.a0100 in ("+pri_Where+"))");
					sbsql.append("union (select a0100,state from  "+pdbflag+""+fs.getFieldsetid()+" uu where state='4' or state='5') ");
			}		
		}
		
		sbsql.append(") b on a.a0100=b.a0100 where "+Sql_switcher.isnull("b.a0100","'###'")+"<>'###' and exists ("+pri_Where+" and a.a0100="+pdbflag+"A01.a0100) group by "+orgs+",b.state");
		sbsql.append(")s)k group by ");
		sbsql.append(orgs);
		//sbsql.append(" order by "+orgs+""); tiany修改该行代码 order by 拼写在select中分页会抛出异常的
		orderby =" order by "+orgs+"";
			
		sql[0]=sqlss;
		sql[1]=column.toString();
		sql[2]=sbsql.toString();
		sql[3]=orderby;
		return sql;
	}
	
	private static boolean isunit(String orgid){
		boolean flag=false;
		String unitdesc=AdminCode.getCodeName("UN",orgid);
		if(unitdesc.length()>0){
			flag=true;
		}
		return flag;
	}
	private static boolean isdep(String orgid){
		boolean flag=false;
		String unitdesc=AdminCode.getCodeName("UM",orgid);
		if(unitdesc.length()>0){
			flag=true;
		}
		return flag;
	}
}
