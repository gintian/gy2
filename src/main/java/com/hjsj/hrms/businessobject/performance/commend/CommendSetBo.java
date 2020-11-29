package com.hjsj.hrms.businessobject.performance.commend;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:CommendSetBo.java</p>
 * <p>Description:后备干部推荐的一些方法</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2007.05.25 13:42:12 pm</p>
 * @author lizhenwei
 * @version 4.0
 *
 */
public class CommendSetBo {
	private Connection conn;
	public CommendSetBo(Connection conn){
		this.conn=conn;
		
	}
	/**
	 * 票数统计的方法
	 * @param ids
	 */
	
	public void AnalyseVote(String ids){
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select count(distinct a.logon_id) num,a.p0300 from per_talent_vote a,p03 b ");
		sql.append("where a.p0300=b.p0300");
		sql.append(" and a.p0201 in("+ids+") and a.p0201=b.p0201 and b.p0201 in("+ids+") group by a.p0300");
		try{
			rs=dao.search(sql.toString());
			while(rs.next()){
				LazyDynaBean bean= new LazyDynaBean();
				bean.set("p0300",rs.getString("p0300"));
				if(rs.getString("num") ==null) {
                    bean.set("num","0");
                } else {
                    bean.set("num",rs.getString("num"));
                }
				list.add(bean);
			}
			rs.close();
			StringBuffer str=new StringBuffer();
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean=(LazyDynaBean)list.get(i);
				str.append("update p03 set p0304 =");
				str.append((String)bean.get("num"));
				str.append(" where p0300=");
				str.append((String)bean.get("p0300"));
				dao.update(str.toString());
				str.setLength(0);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 历年票数统计
	 * @param ids
	 * @return
	 */
	public ArrayList getEveryYearAnalyseVoteList(HashMap commendFieldMap,HashMap commendFieldCodesetidMap,String type,String um,String year){
		ArrayList list = new ArrayList();
		String p0201_str=(String)commendFieldMap.get("p0201_str");
		if(p0201_str==null||p0201_str.trim().length()<=0) {
            return list;
        }
		String[] p0201_Arr= p0201_str.split(",");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		StringBuffer sql = new StringBuffer();
		for(int i=0;i<p0201_Arr.length;i++)
		{
			sql.append("select p02.p0203,p03.b0110,p03.e0122,p03.nbase,p03.p0304,p03.a0101,p03.a0100");
			if(commendFieldMap.get(p0201_Arr[i])!=null&&((String)commendFieldMap.get(p0201_Arr[i])).trim().length()>0) {
                sql.append(",p03."+(String)commendFieldMap.get(p0201_Arr[i]));
            }
			sql.append(" from p02,p03 where p02.p0201=p03.p0201 and p02.p0201="+p0201_Arr[i]);
			if("2".equals(type))
			{
				if(!"aaaaa".equalsIgnoreCase(um)) {
                    sql.append(" and p03.e0122 like '"+um+"%'");
                }
				if(!"aaaaa".equals(year))
				{
	     			sql.append(" and ");
	     			sql.append(Sql_switcher.year("p0205"));
		    		sql.append("=");
	    			sql.append(year);
				}
			}
			
			if(commendFieldMap.get(p0201_Arr[i])!=null&&((String)commendFieldMap.get(p0201_Arr[i])).trim().length()>0) {
                sql.append(" order by p03.p0304 desc"/*+(String)commendFieldMap.get(p0201_Arr[i])*/);
            } else {
                sql.append(" order by p03.p0304 desc");
            }
			try{
				rs = dao.search(sql.toString());
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0203",rs.getString("p0203"));
					//bean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
					bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
					bean.set("a0101",rs.getString("a0101"));
					if(rs.getString("p0304") == null || rs.getString("p0304").trim().length()<=0) {
                        bean.set("p0304","0");
                    } else {
                        bean.set("p0304",rs.getString("p0304"));
                    }
					/*if(commendFieldMap.get(p0201_Arr[i])!=null&&((String)commendFieldMap.get(p0201_Arr[i])).trim().length()>0)
						bean.set("commend_field",AdminCode.getCodeName((String)commendFieldCodesetidMap.get(p0201_Arr[i]),rs.getString((String)commendFieldMap.get(p0201_Arr[i]))));
					else
						bean.set("commend_field","");*/
					bean.set("a0100",rs.getString("a0100"));
					bean.set("nbase",rs.getString("nbase"));
					bean.set("p0201", p0201_Arr[i]);
					list.add(bean);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			sql.setLength(0);
		}
		//}
		return list;
	}
	/**
	 * 执行中的后备推荐的票数统计
	 * @param ids
	 * @return
	 */
	
	
	public ArrayList getExecutingVoteAnalyse(String ids){
		ArrayList returnList = new ArrayList();
	
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		String count="";
		String sql = "";
		try{
			String[] str_Arr=ids.substring(1).split(",");
			for(int i=0;i<str_Arr.length;i++){
				ArrayList list = new ArrayList();
				sql = "select a0100 from p03 where p0201 = "+str_Arr[i];
				rs=dao.search(sql);
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("a0100",rs.getString("a0100"));
					list.add(bean);
					
				}
				rs.close();
				if(list == null || list.size()==0) {
                    continue;
                }
				for(int j=0;j<list.size();j++){
					LazyDynaBean abean = (LazyDynaBean)list.get(j);
					sql ="select count(logon_id) from per_talent_vote where p0201 = "+str_Arr[i]+"and a0100 ='"+(String)abean.get("a0100")+"'";
					rs = dao.search(sql);
					while(rs.next()){
						if(rs.getString(1)==null) {
                            count="0";
                        } else {
                            count = rs.getString(1);
                        }
					}
					rs.close();
					sql = "select p02.p0203,p03.* from p02,p03 where p02.p0201="+str_Arr[i]+"and p02.p0201 = p03.p0201 and p03.a0100='"+(String)abean.get("a0100")+"'";
					rs=dao.search(sql);
					while(rs.next()){
						LazyDynaBean returnBean = new LazyDynaBean();
						returnBean.set("p0203",rs.getString("p0203"));
						returnBean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
						returnBean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
						returnBean.set("a0101",rs.getString("a0101"));
						returnBean.set("p0304",count);
						returnList.add(returnBean);
					}
					rs.close();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnList;
	}
	/**
	 * 执行中的后备推荐列表
	 * @return
	 */

	public ArrayList getInsupportCommendList(){
		ArrayList list  =new ArrayList();
		/*Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month= calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);*/
		//p0206,p0207
		String sql = "select p0203,p0201 from p02 where p0209='05' order by p0201";
	/*	StringBuffer sql = new StringBuffer();
		sql.append("select p0203,p0201 from p02 where p0209='05'");
		sql.append(" and (");
		sql.append(Sql_switcher.year("p0206"));
		sql.append("<"+year+" or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"<"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"="+month+" and "+Sql_switcher.day("p0206")+"<="+day);
		sql.append(")) and (");
		sql.append(Sql_switcher.year("p0207"));
		sql.append(">"+year+" or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+">"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+"="+month+" and "+Sql_switcher.day("p0207")+">="+day);
		sql.append(")) order by p0201");*/
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		//list.add(new CommonData("#","请选择记录"));
		try{
			rs = dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString(2),rs.getString(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 判断正在执行中的后备推荐记录是一条还是多条
	 * @return
	 */
	public int haveOneOrMoreRecord(String type){
		int i=0;
		/*Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		StringBuffer sql = new StringBuffer();
		sql.append("select p0203,p0201 from p02 where p0209='05'");
		sql.append(" and (");
		sql.append(Sql_switcher.year("p0206"));
		sql.append("<"+year+" or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"<"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"="+month+" and "+Sql_switcher.day("p0206")+"<="+day);
		sql.append(")) and (");
		sql.append(Sql_switcher.year("p0207"));
		sql.append(">"+year+" or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+">"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+"="+month+" and "+Sql_switcher.day("p0207")+">="+day);
		sql.append(")) order by p0201");*/
		String sql = "select p0203,p0201 from p02 where p0209='"+type+"' order by p0201";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try{
			rs=dao.search(sql.toString());
			while(rs.next()){
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return i;
	}
	/**
	 * 只有一条执行中的记录,得到这条记录的信息
	 * @return
	 */
	public LazyDynaBean getOnlyOneRecord(){
		LazyDynaBean bean = new LazyDynaBean();
		String sql = "select p0203,p0201 from p02 where p0209='05' order by p0201";
		/*Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month= calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		//p0206,p0207
		StringBuffer sql = new StringBuffer();
		sql.append("select p0203,p0201 from p02 where p0209='05'");
		sql.append(" and (");
		sql.append(Sql_switcher.year("p0206"));
		sql.append("<"+year+" or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"<"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"="+month+" and "+Sql_switcher.day("p0206")+"<="+day);
		sql.append(")) and (");
		sql.append(Sql_switcher.year("p0207"));
		sql.append(">"+year+" or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+">"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+"="+month+" and "+Sql_switcher.day("p0207")+">="+day);
		sql.append(")) order by p0201");*/
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				bean.set("p0203",rs.getString("p0203"));
				bean.set("p0201",rs.getString("p0201"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return bean;
		
	}
	/**
	 * 得到正在执行中的按p0201排序的后备推荐记录的第一条记录id 
	 * @return
	 */
	public String getFirstRecord(String type)
	{
		String sql = "select p0203,p0201 from p02 where p0209='"+type+"' order by p0201";
		/*Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month= calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		//p0206,p0207
		
		StringBuffer sql = new StringBuffer();
		sql.append("select p0203,p0201 from p02 where p0209='05'");
		sql.append(" and (");
		sql.append(Sql_switcher.year("p0206"));
		sql.append("<"+year+" or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"<"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0206")+"="+year+" and "+Sql_switcher.month("p0206")+"="+month+" and "+Sql_switcher.day("p0206")+"<="+day);
		sql.append(")) and (");
		sql.append(Sql_switcher.year("p0207"));
		sql.append(">"+year+" or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+">"+month);
		sql.append(") or (");
		sql.append(Sql_switcher.year("p0207")+"="+year+" and "+Sql_switcher.month("p0207")+"="+month+" and "+Sql_switcher.day("p0207")+">="+day);
		sql.append(")) order by p0201");*/
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs =null;
		String p0201="";
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				p0201 = rs.getString("p0201");
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return p0201;
	}
	public ArrayList getDbList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql="select pre from dbname ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				list.add(rs.getString("pre"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到候选人列表
	 * @param p0201
	 * @param b0110
	 * @param e0122
	 * @return
	 */
	public ArrayList getCandidateList(String isAdmin,String p0201,String b0110,String e0122,String logon_id,String commend_field,String commend_field_codesetid){
		ArrayList list = new ArrayList();
		try{
			
				ArrayList dbList = this.getDbList();
    			ContentDAO dao =new ContentDAO(this.conn);
	    		RowSet rs = null;
	    		StringBuffer sql = new StringBuffer();
	    		StringBuffer buf = new StringBuffer();
			//未选择的人
		    	sql.append(" select * from ((");
		    	for(int i=0;i<dbList.size();i++)
		    	{
				String pre=(String)dbList.get(i);
				if(i>0)
				{
					buf.append(" union ");
					sql.append(" union ");
				}
	    		sql.append(" select dbname.dbid,e.*,u.a0000 from dbname,"+pre+"A01 u,");
		        sql.append("( select a0100,a0101,nbase,0 as submit,b0110,e0122,p0300,p0307");
		        if(commend_field!=null && commend_field.trim().length()>0)
		    	{
		    		sql.append(","+commend_field);
	    		}
	    	    sql.append(" from p03 ,organization o where not exists ( ");
    		    sql.append(" select p0300,logon_id from per_talent_vote ptv where p03.p0300=ptv.p0300");
		    	sql.append(" and ptv.logon_id='"+logon_id+"' ) and p03.p0201 = "+p0201);
		    	sql.append(" and p03.p0307=o.codeitemid ");
		    	/*if(!isAdmin.equals("1"))
		    	{
			    	buf.append(" and (p03.p0307 like '"+b0110+"%' and o.codesetid='UN' or p03.p0307 like '");
			    	buf.append(e0122+"%' and o.codesetid='UM') ");
		    	}*/
	    		sql.append(") e where e.nbase=dbname.pre and e.nbase='"+pre+"' and e.a0100=u.a0100");   
	    		
	    		//--
	    		buf.append(" select dbname.dbid,f.*,u.a0000 from dbname,"+pre+"A01 u,");
				buf.append("( select a0100,a0101,nbase,1 as submit,b0110,e0122,p0300,p0307");
				if(commend_field!=null && commend_field.trim().length()>0)
				{
					buf.append(","+commend_field);
				}
				buf.append(" from p03,organization o where exists ( ");
				buf.append(" select p0300,logon_id from per_talent_vote ptv where p03.p0300=ptv.p0300");
				buf.append(" and ptv.logon_id='"+logon_id+"')");
				buf.append(" and p0201="+p0201);
				buf.append(" and p03.p0307=o.codeitemid ");
				/*if(!isAdmin.equals("1"))
		    	{
			    	buf.append(" and (p03.p0307 like '"+b0110+"%' and o.codesetid='UN' or p03.p0307 like '");
			    	buf.append(e0122+"%' and o.codesetid='UM') ");
		    	}*/
				buf.append(" ) f where f.nbase=dbname.pre and f.nbase='"+pre+"' and f.a0100=u.a0100");
	            
			}
			sql.append(") union (");
			sql.append(buf.toString()+")) l ");
		
			sql.append(" order by l.a0000");
			rs= dao.search(sql.toString());

			while(rs.next()){
				String p0307=rs.getString("p0307")==null?"":rs.getString("p0307");
				if("1".equals(isAdmin)|| "".equals(p0307)||b0110.startsWith(p0307)||e0122.startsWith(p0307))
				{
		    		LazyDynaBean bean=new LazyDynaBean();
		    		bean.set("p0300",rs.getString("p0300"));
		    		bean.set("a0101",rs.getString("a0101"));
				//bean.set("p0201",p0201);
			    	bean.set("choosed",rs.getString("submit"));
			    	bean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
			    	bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
			    	if(commend_field!=null && commend_field.trim().length()>0&&commend_field_codesetid!=null && commend_field_codesetid.trim().length()>0)
			    	{
			    		bean.set("commend_field",AdminCode.getCodeName(commend_field_codesetid,rs.getString(commend_field)));
			    	}
		    		else
		    		{
		    			bean.set("commend_field","");
		    		}
		    		list.add(bean);
	    		}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 判断是否已经选过候选人
	 * @param logon_id
	 * @param p0201
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
	public boolean isSelected(String logon_id,String p0201){
		boolean flag=false;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			StringBuffer sql= new StringBuffer();
			sql.append("select logon_id,p0300 from per_talent_vote where (( exists( ");
			sql.append("select p0300 from p03 where p03.p0300=per_talent_vote.p0300 and p03.p0201 = ");
			sql.append(p0201+")) or ( per_talent_vote.p0300 =0))");
			sql.append(" and per_talent_vote.logon_id='");
			sql.append(logon_id+"' and per_talent_vote.p0201=");
			sql.append(p0201);
			rs=dao.search(sql.toString());
			while(rs.next()){
				flag=true;
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 得到登录用户的单位和部门信息
	 * @param dbpre
	 * @param a0100
	 * @return
	 */
    public LazyDynaBean getUserInfo(String dbpre,String a0100){
    	LazyDynaBean bean= new LazyDynaBean();
    	ContentDAO dao =new ContentDAO(this.conn);
    	RowSet rs =null;
    	StringBuffer sql =new StringBuffer();
    	sql.append("select b0110,e0122 from ");
    	sql.append(dbpre);
    	sql.append("a01");
    	sql.append(" where a0100 = '");
    	sql.append(a0100+"'");
    	try{
    	    rs = dao.search(sql.toString());
    	    while(rs.next()){
    	    	if(rs.getString("b0110")==null) {
                    bean.set("b0110","");
                } else {
                    bean.set("b0110",rs.getString("b0110"));
                }
    	    	if(rs.getString("e0122")==null) {
                    bean.set("e0122","");
                } else {
                    bean.set("e0122",rs.getString("e0122"));
                }
    	    }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return bean;
    	
    	
    }
    /**
     * 保存用户选择的候选人
     * @param choose_per
     */
    public void saveUserChooseCandidate(String[] choose_per,String logon_id,String p0201){
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sb=new StringBuffer();
    		if(choose_per != null){
    		    for(int i=0 ;i<choose_per.length;i++){
    			   sb.append("insert into per_talent_vote values (");
    			   sb.append("'"+logon_id+"',");
    			   sb.append("'"+choose_per[i]+"',");
    			   sb.append(p0201+")");
    			   dao.insert(sb.toString(),new ArrayList());
    			   sb.setLength(0);
    		    }
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
   
    public HashMap getStateList(){
    	HashMap map = new HashMap();
    	try{
    		String sql="select p0201,p0209 from p02 where p0209 in('05','06') ";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs= dao.search(sql);
    		while(rs.next()){
    			map.put(rs.getString("p0201"),rs.getString("p0209"));
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }
 
	public String getCommendFieldCodesetid(ArrayList list, String commend_field ){
		String returnStr="";
		try
		{
			for(int i=0;i<list.size();i++)
			{
				FieldItem item = (FieldItem) list.get(i);
				if(item.getItemid().equalsIgnoreCase(commend_field))
				{
					returnStr=item.getCodesetid();
					break;
				}/** if end */
			}/** for i end */
		}/**try end*/
		catch(Exception e)
		{
			e.printStackTrace();
		}/** catch end */
		return returnStr;
	}/** method getCommendFieldCodesetid end */

	public String getP0300sByP0201(String p0201s){
		StringBuffer sb= new StringBuffer();
		try
		{
			String sql="select p0300 from p03 where p0201 in ("+p0201s+")";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs= dao.search(sql);
			while(rs.next()){
				sb.append(",");
				sb.append(rs.getString("p0300"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(sb!=null&&sb.toString().trim().length()>0) {
            return sb.toString().substring(1);
        } else {
            return "''";
        }
		
	}
	/**
	 * 推荐职务指标是否为空
	 * @param commend_field
	 * @return
	 */
	public boolean isNull(String commend_field){
		boolean flag=false;
		try
		{
			if(commend_field == null || commend_field.trim().length()<=0) {
                flag=true;
            }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 弃权
	 * @param logon_id
	 * @param p0201
	 */
	public void insertDisclaim(String logon_id,String p0201)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" insert into per_talent_vote values(");
			sql.append("'"+logon_id+"','");
			sql.append("0','");
			sql.append(p0201+"')");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 得票状况分析
	 * @param p0201
	 * @return
	 */
	public ArrayList getVoteStatusList(String p0201)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(p0201==null|| "".equals(p0201)) {
                return list;
            }
			StringBuffer sql = new StringBuffer();
			sql.append("select count(distinct logon_id) personcount,vote from (");
			sql.append("select count(p0300) vote,logon_id from per_talent_vote where logon_id=logon_id and p0300<>0 and p0201=");
			sql.append(p0201+" group by logon_id) b");
			sql.append(" where b.vote=b.vote group by b.vote order by b.vote");
			ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs = null;
		    rs = dao.search(sql.toString());
		    while(rs.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("personcount",rs.getString("personcount"));
		    	bean.set("vote",rs.getString("vote"));
		    	list.add(bean);
		    }
		    sql.setLength(0);
		    rs.close();
		    sql.append("select count(logon_id) num from per_talent_vote where p0300=0 and p0201=");
		    sql.append(p0201);
		    rs=dao.search(sql.toString());
		    while(rs.next())
		    {
		    	LazyDynaBean bean= new LazyDynaBean();
		    	bean.set("vote","0");
		    	bean.set("personcount",rs.getString("num"));
		    	list.add(bean);
		    	
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getFinishCommendList()
	{
		ArrayList list = new ArrayList();
		String sql ="select p0201,p0203 from p02 where p0209='06' order by p0201";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try
		{
			rs= dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("p0201"),rs.getString("p0203")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	//-----------------------------根据条件提名候选人--------------------------------//
	/**
	 * 权限范围内的人员库列表
	 * @param dbpreList
	 */
    public String getPrivPre(ArrayList dbpreList) throws GeneralException
    {
    	ArrayList list = new ArrayList();
    	StringBuffer preSql=new StringBuffer();
    	try
    	{
    		if(dbpreList.size()==0)
    		{
    			throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
    		}
    		for(int i=0;i<dbpreList.size();i++)
    		{
    			preSql.append(",");
    			preSql.append((String)dbpreList.get(i));
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);	
    	}
    	if(preSql.toString().trim().length()>0) {
            return preSql.toString().substring(1);
        } else {
            return "";
        }
    }
    public ArrayList getUMList()
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		String sql="select codeitemid,codeitemdesc from organization where codesetid='UM'";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs= null;
    		rs=dao.search(sql);
    		while(rs.next())
    		{
    			list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
    		}
    		list.add(0,new CommonData("aaaaa","全部"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public ArrayList getVoteList(String nbase,String a0100)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		StringBuffer buf=new StringBuffer();
    		buf.append("select p02.p0203,p03.p0304,");
    		buf.append(Sql_switcher.year("p0205"));
    		buf.append(" as p0205 from p02,p03 where ");
    		buf.append(" p02.p0201=p03.p0201 and p03.a0100='");
    		buf.append(a0100);
    		buf.append("' and p03.nbase='");
    		buf.append(nbase);
    		buf.append("' and p02.p0209='06' order by p0304 desc");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(buf.toString());
    		while(rs.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("p0203",rs.getString("p0203"));
    			bean.set("p0205",rs.getString("p0205"));
    			bean.set("p0304",rs.getString("p0304")==null?"0":rs.getString("p0304"));
    			list.add(bean);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public HashMap getPersonInfo(String a0100,String nbase)
    {
    	HashMap map = new HashMap();
    	try
    	{
    		String sql = "select b0110,e0122,a0101 from "+nbase+"a01 where a0100='"+a0100+"'";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql);
    		while(rs.next())
    		{
    			map.put("dw",AdminCode.getCodeName("UN",rs.getString("b0110")));
    			map.put("bm",AdminCode.getCodeName("UM",rs.getString("e0122")));
    			map.put("xm",rs.getString("a0101"));
    		}
    	}
    	catch(Exception e)
		{
			e.printStackTrace();
		}
    	return map;
    }
    public ArrayList getAllYearList()
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		String sql = "select distinct "+Sql_switcher.year("p0205")+" as p0205 from p02 where p0209='06'";
    		RowSet rs = null;
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search(sql);
    		list.add(new CommonData("aaaaa","全部"));
    		while(rs.next())
    		{
    			list.add(new CommonData(rs.getString("p0205"),rs.getString("p0205")));
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
   
}
