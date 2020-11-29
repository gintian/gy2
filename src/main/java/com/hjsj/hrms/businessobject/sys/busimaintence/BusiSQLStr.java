package com.hjsj.hrms.businessobject.sys.busimaintence;

public class BusiSQLStr {
	public static String[] getFieldStr(String fieldsetid){
		String[] sqlstr=new String[4];
		
		String where="from t_hr_BusiField  where   fieldsetid='"+fieldsetid+"'";
		String column="fieldsetid,itemid,displayid,itemdesc,itemtype,itemlength,decimalwidth,codesetid,displaywidth,state,useflag,keyflag,codeflag,ownflag";
		String sql="select "+column;
		String orderby="order by displayid";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return  sqlstr;
	}
	
	public static String[] getFieldString(String fieldsetid){
		String[] sqlstr=new String[4];
		
		//String where="from fielditem  where   fieldsetid='"+fieldsetid+"' and useflag='1'";
		String where=" from fielditem  where   fieldsetid='"+fieldsetid+"' ";
		String column="fieldsetid,itemid,displayid,itemdesc,itemtype,itemlength,decimalwidth,codesetid,displaywidth,state,useflag";
		String sql="select "+column;
		String orderby="order by displayid";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return  sqlstr;
	}
	public String[] getRelatingcodeSQL(){
		String[] sqlstr=new String[4];
		StringBuffer where=new StringBuffer();
		String column="codesetid,codetable,codevalue,codedesc,upcodevalue,status";
//		String sql="select thr.codesetid,codetable=codetable+':'+thbt.fieldsetdesc,codevalue=codevalue+':'+thb.itemdesc,codedesc=codedesc+':'+thb.itemdesc,upcodevalue=upcodevalue+':'+thb.itemdesc,thr.status";
//		where.append("from t_hr_relatingcode thr,t_hr_busifield thb ,t_hr_busiTable thbt where thr.codetable=thb.FieldSetId and thr.codevalue=thb.ItemId and thbt.fieldsetid=thr.codetable");
//		String orderby="order by thr.codesetid";
		String sql="select thr2.codesetid,codetable,codevalue=codevalue+':'+(select itemdesc from t_hr_relatingcode thr,t_hr_busifield thb  where thr.codetable=thb.FieldSetId and thr.codevalue=thb.ItemId and thr.codesetid = thr2.codesetid ),codedesc=codedesc+':'+(select itemdesc from t_hr_relatingcode thr,t_hr_busifield thb  where thr.codetable=thb.FieldSetId and thr.codedesc=thb.ItemId and thr.codesetid = thr2.codesetid ),upcodevalue=upcodevalue+':'+(select itemdesc from t_hr_relatingcode thr,t_hr_busifield thb  where thr.codetable=thb.FieldSetId and thr.upcodevalue=thb.ItemId and thr.codesetid = thr2.codesetid ),thr2.status";
		where.append("from t_hr_relatingcode thr2");
		String orderby="order by thr2.codesetid";
		sqlstr[0]=sql;
		sqlstr[1]=where.toString(); 
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return  sqlstr;
		
	}
	public String[] getBusiNameStr(){
		String[] sqlstr=new String[4];
		if(com.hrms.hjsj.utils.Sql_switcher.searchDbServer()==1){//sqlserver
			String sel="select ts.id id,name,description,min(state) state";
			String where="from t_hr_busitable tb left join t_hr_subsys ts on tb.id=ts.id where  is_available='1' group by ts.id,name,description"/* and id='20' or id='30' or id='31' or id='32' or id='33' or id='34' or id='35' or id='36'"*/;
			String column="id,name,description,state";
			String orderby="order by ts.id";
			sqlstr[0]=sel;
			sqlstr[1]=where;
			sqlstr[2]=column;
			sqlstr[3]=orderby;
		}else{
			String sel="select id,name,description,state";
			String where="from(select ts.id id,name,description,min(state) state from t_hr_busitable tb left join t_hr_subsys ts on tb.id=ts.id where  is_available='1' group by ts.id,name,description) tt"/* and id='20' or id='30' or id='31' or id='32' or id='33' or id='34' or id='35' or id='36'"*/;
			String column="id,name,description,state";
			String orderby="order by id";
			sqlstr[0]=sel;
			sqlstr[1]=where;
			sqlstr[2]=column;
			sqlstr[3]=orderby;
		}
		return sqlstr;
	}	
	public String[] getSubsysStr(String id){
		String[] sqlstr=new String[4];
		String sql="select fieldsetid,id,fieldsetdesc,displayorder,customdesc,ownflag,useflag";
		String where="from t_hr_busiTable where id='"+id+"'";
		String column="fieldsetid,id,fieldsetdesc,displayorder,customdesc,ownflag,useflag";
//		String orderby="order by fieldsetid";  更改更左边树排序一样
		String orderby="order by displayorder";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return sqlstr;
	}
	
}
