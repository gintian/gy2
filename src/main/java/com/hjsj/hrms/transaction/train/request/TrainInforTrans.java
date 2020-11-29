package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainInforTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r3101 = (String)hm.get("r3101");
		r3101 = r3101==null||r3101.length()<1?(String)this.getFormHM().get("r3101"):r3101;
		r3101 = r3101!=null&&r3101.trim().length()>0?r3101:"";
		hm.remove("r3101");
		
		String r3127 = (String)hm.get("r3127");
		r3127 = r3127==null||r3127.length()<1?(String)this.getFormHM().get("r3127"):r3127;
		r3127=r3127!=null?r3127:"";
		hm.remove("r3127");
		
        if (r3101 != null && r3101.length() > 0) {
            TrainClassBo bo = new TrainClassBo(this.frameconn);
            if (!bo.checkClassPiv(r3101, this.userView))
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
        }
		String flag = (String)hm.get("flag");
		flag = flag!=null&&flag.trim().length()>0?flag:"1";
		hm.remove("flag");
		
		if("1".equals(flag))
			course(r3101,r3127);
		else if("2".equals(flag))
			trainsCourse(r3101,r3127);
		else if("3".equals(flag))
			trainsRes(r3101,r3127);
		else if("4".equals(flag))
			trainsStu(r3101,r3127);
		
		this.getFormHM().put("r3101",r3101);
		this.getFormHM().put("r3127",r3127);
	}
	private void course(String r3101,String r3127){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String r3122 = "";
		try {
			if(r3101!=null&&r3101.trim().length()>0){
				RecordVo vo = new RecordVo("r31");
				vo.setString("r3101",r3101);
				vo = dao.findByPrimaryKey(vo);
				r3122=vo.getString("r3122");
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("r3122",r3122);
	}
	private void trainsCourse(String r3101,String r3127){
		ArrayList list = new ArrayList();
		ArrayList codelist = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList("r41",Constant.USED_FIELD_SET);
		StringBuffer buf = new StringBuffer();
		StringBuffer wherestr = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		buf.append("select ");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("r4106".equalsIgnoreCase(fielditem.getItemid())|| "r4105".equalsIgnoreCase(fielditem.getItemid())
					|| "r4114".equalsIgnoreCase(fielditem.getItemid())){
				CommonData cd = new CommonData();
				cd.setDataName(fielditem.getItemid());
				cd.setDataValue(fielditem.getCodesetid());				
				codelist.add(cd);
			}
			if("r4103".equalsIgnoreCase(fielditem.getItemid())){
				continue;
			}else if("r4104".equalsIgnoreCase(fielditem.getItemid())){
				continue;
			}else if("0".equalsIgnoreCase(fielditem.getState())){
				continue;
			}
			if("r4106".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setCodesetid("0");
				buf.append("(select r0402 from r04 where r0401=r41.r4106) as r4106");
				columns.append("r4106");
			}else if("r4105".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setCodesetid("0");
				buf.append("(select r1302 from r13 where r1301=r41.r4105) as r4105");
				columns.append("r4105");
			}else if("r4114".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setCodesetid("0");
				buf.append("(select r0702 from r07 where r0701=r41.r4114) as r4114");
				columns.append("r4114");
			}else{
				buf.append(fielditem.getItemid());
				columns.append(fielditem.getItemid());
			}
			buf.append(",");
			columns.append(",");
			if("r4101".equalsIgnoreCase(fielditem.getItemid())){
				list.add(0,fielditem);
			}else
				list.add(fielditem);
		}
		
		FieldItem fi = DataDictionary.getFieldItem("r4101", "r41");
		String column = columns.toString();
		if(column.indexOf("r4101")==-1){
			buf.append(fi.getItemid()+",");
			column="r4101,"+column;
			list.add(0,fi);
		}
		
		wherestr.append(" from r41 where r4103='");
		wherestr.append(r3101);
		wherestr.append("'");
		
		showNum(wherestr.toString());//显示条数 用于控制删除按钮的显示与否 lwc
		showPush(r3127);//查找推送课程列
		
		this.getFormHM().put("r41list",list);
		this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",column.substring(0,column.length()-1));
		this.getFormHM().put("codelist",codelist);
	}
	
	private void trainsStu(String r3101,String r3127){
		ArrayList list = new ArrayList();
		ArrayList codelist = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList("r37",Constant.USED_FIELD_SET);
		StringBuffer buf = new StringBuffer();
		StringBuffer wherestr = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		buf.append("select ");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("r3702".equalsIgnoreCase(fielditem.getItemid())|| "r3705".equalsIgnoreCase(fielditem.getItemid())){
				CommonData cd = new CommonData();
				cd.setDataName(fielditem.getItemid());
				cd.setDataValue(fielditem.getCodesetid());				
				codelist.add(cd);
			}
			if("0".equalsIgnoreCase(fielditem.getState()))
				continue;
			if("r3703".equalsIgnoreCase(fielditem.getItemid())){
				continue;
			}
			if("r3702".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setCodesetid("0");
				buf.append("(select codeitemdesc from codeitem where codeitemid=r37.r3702 and codesetid='14') as r3702");
				columns.append("r3702");
			}else if("r3705".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setCodesetid("0");
				buf.append("(case r37.r3702 ");
				buf.append("when '01' then (select r0402 from r04 where r0401=r37.r3705) ");
				buf.append("when '02' then (select r0102 from r01 where r0101=r37.r3705) ");
				buf.append("when '03' then (select r0702 from r07 where r0701=r37.r3705) ");
				buf.append("when '04' then (select R1011 from r10 where r1001=r37.r3705) ");
				buf.append("when '05' then (select r1302 from r13 where r1301=r37.r3705) ");
				buf.append("end ) as r3705");
				columns.append("r3705");
			}else{
				buf.append(fielditem.getItemid());
				columns.append(fielditem.getItemid());
			}
			buf.append(",");
			columns.append(",");
			if("r3701".equalsIgnoreCase(fielditem.getItemid())){
				list.add(0,fielditem);
			}else
				list.add(fielditem);
		}
		
		wherestr.append(" from r37 where R3703='");
		wherestr.append(r3101);
		wherestr.append("'");
		
		showNum(wherestr.toString());//显示条数 用于控制删除按钮的显示与否 lwc
		
		this.getFormHM().put("setlist",list);
		this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
		this.getFormHM().put("codelist",codelist);
	}
	private void trainsRes(String r3101,String r3127){
	    try {
	        ArrayList list = new ArrayList();
	        String spid = (String)this.getFormHM().get("spid");
	        spid=spid!=null&&spid.trim().length()>0?spid:"02";
	        
	        String searchstr = (String) this.getFormHM().get("searchstr");
	        searchstr = searchstr != null && searchstr.trim().length() > 0 ? searchstr : "";
	        searchstr = PubFunc.reBackWord(searchstr);
	        searchstr = PubFunc.keyWord_reback(searchstr);
	        this.getFormHM().put("searchstr", "");
	        
	        ArrayList fieldlist = DataDictionary.getFieldList("r40",Constant.USED_FIELD_SET);
	        StringBuffer buf = new StringBuffer();
	        StringBuffer wherestr = new StringBuffer();
	        StringBuffer columns = new StringBuffer();
	        buf.append("select ");
	        for(int i=0;i<fieldlist.size();i++){
	            FieldItem fielditem = (FieldItem)fieldlist.get(i);
	            if("0".equalsIgnoreCase(fielditem.getState()))
	                continue;
	            
	            buf.append(fielditem.getItemid());
	            columns.append(fielditem.getItemid());
	            buf.append(",");
	            columns.append(",");
	            if("r4001".equalsIgnoreCase(fielditem.getItemid())){
	                list.add(0,fielditem);
	            }else
	                list.add(fielditem);
	        }
	        
	        buf.append("nbase,");
	        columns.append("nbase,");
	        if(buf.toString().toLowerCase().indexOf("r4001") < 0) {
                buf.append("r4001,");
                columns.append("r4001,");
                FieldItem fielditem = DataDictionary.getFieldItem("r4001", "r40");
                list.add(0,fielditem);
            }
	        
	        wherestr.append(" from r40 where r4005='");
	        wherestr.append(r3101);
	        wherestr.append("' and R4013='"+spid+"'");
	        TrainCourseBo tb = new TrainCourseBo(this.userView);
	        String codes = tb.getUnitIdByBusi();
	        if(!this.userView.isSuper_admin()&&codes.indexOf("UN`")==-1){
	            String[] tmp = codes.split("`");
	            StringBuffer tmpstr=new StringBuffer();
	            for(int i=0;i<tmp.length;i++){
	                String code = tmp[i];
	                if (i > 0)
	                    tmpstr.append(" or ");
	                
	                if ("UN".equalsIgnoreCase(code.substring(0, 2)))
	                    tmpstr.append("B0110 like '" + code.substring(2, code.length()) + "%'");
	                
	                if ("UM".equalsIgnoreCase(code.substring(0, 2)))
	                    tmpstr.append("E0122 like '" + code.substring(2, code.length()) + "%'");
	            }
	            if(tmpstr!=null&&tmpstr.length()>0)
	                wherestr.append(" and ("+tmpstr+")");
	        }
	        String where = sqlWhere(searchstr);
	        if(where!=null&&where.length()>0){
	            wherestr.append(" and ("+where+")");
	        }
	        //System.out.println(buf.substring(0,buf.length()-1) +wherestr.toString() );
	        showNum(wherestr.toString());//显示条数 用于控制删除查询按钮的显示与否 lwc
	        this.getFormHM().put("setlist",list);
	        this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
	        this.getFormHM().put("wherestr",wherestr.toString());
	        this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
	        TransDataBo bo = new TransDataBo();
	        this.getFormHM().put("splist",bo.flagList());
	        this.getFormHM().put("spid",spid);
	    } catch (Exception e) {
	        e.printStackTrace();
        }
	}
	
	private void showNum(String strwhere){
		int num = 0;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search("select count(1) s "+strwhere);
			if(this.frowset.next())
				num = this.frowset.getInt("s");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			//System.out.println(num);
			this.getFormHM().put("num", num+"");
		}
	}
	
	//查找培训课程推送列
	private void showPush(String r3127) {
		String pushitem = "";
		if("04".equals(r3127)){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			//只取一个指标优先级顺序 培训课程r41,培训项目r13或培训资料r07 中有关联表r50的代码指标
			String sql = "select FieldSetId,itemid,b.codesetid from t_hr_busifield b left join t_hr_relatingcode r on b.codesetid=r.codesetid where codetable = 'R50' and FieldSetId in ('R13','R07','R41') and codeflag=1 and useflag=1 order by FieldSetId desc";
			String sqls = "select FieldSetId,itemid,codesetid from t_hr_busifield where FieldSetId='R41' and itemid='R4118' and state =1 and useflag=1";
			RowSet rs=null;
			try {
				this.frowset = dao.search(sql);
				rs = dao.search(sqls);
				if(rs.next())
					pushitem = rs.getString("FieldSetId") + ":" + rs.getString("itemid")+":"+rs.getString("codesetid");
				else if(this.frowset.next())//如果培训项目和培训资料中都有关联在线课程指标 则根据培训资料显示
					pushitem = this.frowset.getString("FieldSetId") + ":" + this.frowset.getString("itemid")+":"+this.frowset.getString("codesetid");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		this.getFormHM().put("pushitem", pushitem);
	}
	
	public String sqlWhere(String search) {
		StringBuffer sqlwhere = new StringBuffer();
		String column = "";
		if(search == null || search.length()<1)
		    return "";
		
		if (search != null && search.trim().length() > 0) {
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				String sexpr = searcharr[0];
				String sfactor = searcharr[1];
				
				String[] sfactors = sfactor.split("`");
				
				for(int i=0;i<sfactors.length;i++){
				    if(sfactors[i]==null||sfactors[i].length()<1)
				        continue;
				    String[] columns = getColumn(sfactors[i]);
				    column+=columns[0]+",";
				}
				
				try {
					boolean blike = false;
                    blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true : false;
                    FactorList factor = new FactorList(sexpr, sfactor, "", true, blike, true, 1, "su");
                    String wherestr = factor.getSqlExpression();
                    if (wherestr.indexOf("WHERE") != -1)
                        wherestr = wherestr.substring(wherestr.indexOf("WHERE") + 5, wherestr.length());
                    else if (wherestr.indexOf("where") != -1)
                        wherestr = wherestr.substring(wherestr.indexOf("where") + 5, wherestr.length());
                    wherestr = wherestr.replaceAll("A01", "r40");
                    sqlwhere.append(wherestr);
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
			}
		}
        String where = "";
        if (sqlwhere.length() > 0 && sqlwhere != null && column.length() > 0 && column != null) {
            TrainClassBo bo = new TrainClassBo(this.frameconn);
            if(column != null && column.length() > 0)
                column = column.substring(0, column.length()-1);
            
            where = bo.getSqlWhere("R40", sqlwhere.toString(), column);
            sqlwhere.delete(0, sqlwhere.length());
            sqlwhere.append(where);
        }
        
		return sqlwhere.toString();
	}
	/**
	 * 获取查询条件中的指标
	 * @param sfactor 查询条件
	 * @return
	 */
    public String[] getColumn(String sfactor) {
        String[] Columns = null;
        if (sfactor.indexOf("=") > -1)
            Columns = sfactor.split("=");

        if (sfactor.indexOf(">") > -1)
            Columns = sfactor.split(">");

        if (sfactor.indexOf(">=") > -1)
            Columns = sfactor.split(">=");

        if (sfactor.indexOf("<") > -1)
            Columns = sfactor.split("<");

        if (sfactor.indexOf("<=") > -1)
            Columns = sfactor.split("<=");

        if (sfactor.indexOf("<>") > -1)
            Columns = sfactor.split("<>");

        return Columns;
    }
}
