package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.businessobject.general.inform.search.SearchInformBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalyseSelectfactorResTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String sfactor=(String)this.getFormHM().get("sfactor");
		String sexpr=(String)this.getFormHM().get("sexpr");
		String history=(String)this.getFormHM().get("history");
		String likeflag=(String)this.getFormHM().get("likeflag");
		String userbase=(String)this.getFormHM().get("userbase");
		
//		boolean bhis=false;
//		if(history!=null&&history.equals("1"))
//        	bhis=true;
		if(sfactor!=null&&sfactor.length()>0)
		sfactor=SafeCode.decode(sfactor);
		if(sexpr!=null&&sexpr.length()>0)
			sexpr=SafeCode.decode(sexpr);	
		sexpr = PubFunc.keyWord_reback(sexpr);
		sfactor = PubFunc.keyWord_reback(sfactor);
		ArrayList fieldlist=new ArrayList();
		String chwhere="";		
//		boolean likeflagb = false;
//		if("1".equals(likeflag)){
//			likeflagb=true;
//		}
		InfoUtils infoUtils=new InfoUtils();
		//FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),userbase,bhis,likeflagb,true,1,userView.getUserId());
    	//fieldlist=factorslist.getFieldList();
       // chwhere=factorslist.getSqlExpression();
		SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,"all",userbase);
		chwhere = searchInformBo.strWhere(sexpr,sfactor,"1",history,likeflag,"0","1","2");
		chwhere = chwhere.replaceAll("AND 1=2", "");
        String sql="select "+userbase+"A01.e01a1 e01a1 "+chwhere;
       // System.out.println(sql);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        StringBuffer strname=new StringBuffer();
        StringBuffer strvalue=new StringBuffer();
        HashMap map = getPxPost();
        try {
			this.frowset=dao.search(sql);
			ArrayList arrayList = new ArrayList();//去重复
			while(this.frowset.next())
			{
				String tmpE01A1 = this.frowset.getString("e01a1");
				if(tmpE01A1!=null && tmpE01A1.trim().length()>0&&!arrayList.contains(tmpE01A1) && (map != null && map.containsKey(tmpE01A1))){
				    strname.append(AdminCode.getCodeName("@K", tmpE01A1)+",") ;
					strvalue.append("@K"+tmpE01A1+",");
					arrayList.add(tmpE01A1);
				}
			}
			//if(!this.userView.isSuper_admin())
			//	savesearch(dao,chwhere);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(strvalue.length()>0)
			strvalue.setLength(strvalue.length()-1);
		if(strname.length()>0)
			strname.setLength(strname.length()-1);
		if(strvalue.length()==0)
			strvalue.append("@K");
		this.getFormHM().put("value", strvalue.toString());
		this.getFormHM().put("name", strname.toString());
		this.getFormHM().put("chwhere", chwhere);
		//checkSql="exists(select 1  "+chwhere+" and "+userbase+"A01.a0100=C.a0100)";
	}

	private void savesearch(ContentDAO dao,String sql) throws SQLException{
		//sql=sql.replaceAll("AND 1=2", "")+" and "+Sql_switcher.isnull("e01a1", "''")+"<>''";
		String tabldName="";
		String dbpre = (String)this.getFormHM().get("userbase");
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		if(userView.getStatus()==4)
		{
			tabldName = "t_sys_result";
			Table table = new Table(tabldName);
			
			if (!dbWizard.isExistTable(table)) {
				return;
			}
			String flag="0";					
			String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
			str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";	
			dao.delete(str, new ArrayList());
			
			StringBuffer buf_sql = new StringBuffer("");
			buf_sql.append("insert into " + tabldName);
			buf_sql.append("(username,nbase,obj_id,flag) ");
			buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
			buf_sql.append(" from (select a0100 "+sql.toString()+") myset");
			dao.insert(buf_sql.toString(), new ArrayList());
		}else
		{
			tabldName=userView.getUserName()+dbpre+"result";
			dao.update("delete from "+tabldName);	
			
			StringBuffer inssql=new StringBuffer();	    	
	    	inssql.append("insert into ");
	    	inssql.append(tabldName);
	    	inssql.append("(");
	    	inssql.append("A0100)");
    		inssql.append(" select ");
    		inssql.append("A0100  from ( select a0100");
    		inssql.append(sql.toString());
    		inssql.append(") myset");			    				
    		dao.update(inssql.toString());
		}
	}
	
    public HashMap getPxPost() throws GeneralException {
        HashMap map = new HashMap();
        try {
            TrainStationBo trainStationBo = new TrainStationBo();
            HashMap mapg = trainStationBo.getStationSett(this.frameconn);
            String postSetId = (String) mapg.get("post_setid");// 岗位培训子集编号
            String postCloumn = (String) mapg.get("post_coursecloumn");// 岗位培训子集中参培课程指标
            if (postSetId == null || postSetId.length() < 1 || postCloumn == null || postCloumn.length() < 1) {
                return map;
            }

            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer strsql = new StringBuffer();
            strsql.append("select e01a1 from ");
            strsql.append(postSetId);
            
            if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                strsql.append(" where " + postCloumn + " is not null");
            else
                strsql.append(" where " + Sql_switcher.isnull(postCloumn, "''") + "<>''");
            
            strsql.append(" group by e01a1");
            this.frowset = dao.search(strsql.toString());

            while (this.frowset.next()) {
                map.put(this.frowset.getString("e01a1"), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }
}
