package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:培训班</p>
 * <p>Description:发布培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class PublishedTrans extends IBusiness {

	public void execute() throws GeneralException {
		String msg = "true";
		String[] cid = null;
		cat.debug("table name=r31");
		String ids = (String)this.getFormHM().get("ids");
		if(ids!=null&ids.length()>0)
			cid = ids.split(",");
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		TrainClassBo bo = new TrainClassBo(this.getFrameconn());
		try{
			String namestr="";
			StringBuffer exper = new StringBuffer("");
			for(int i=0;i<cid.length;i++){
			    if(!bo.checkClassPiv(cid[i], this.userView))
			        continue;
				RecordVo vo = new RecordVo("r31");
				vo.setString("r3101", cid[i]);
				vo = dao.findByPrimaryKey(vo);
				String sp = vo.getString("r3127");
				String r3101 =  vo.getString("r3101");
				String r3130 = vo.getString("r3130");
				r3130=r3130.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				if("01".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.drafting")+"!");
					continue;
				}else if("02".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.approvalr")+"!");
					continue;
				}else if("04".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.published")+"!");
					continue;
				}else if("05".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.perform")+"!");
					continue;
				}else if("06".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.end")+"!");
					continue;
				}else if("07".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.overrule")+"!");
					continue;
				}else if("08".equals(sp)){
					exper.append("\n\n["+r3130+"]"+ResourceFactory.getProperty("train.b_plan.published.submit.approve")+"!");
					continue;
				}
				namestr+="'"+r3101+"',";
			}
			if(namestr.trim().length()>0){
				StringBuffer sqlstr = new StringBuffer("");
				sqlstr.append("update r31 set r3127='04' where R3101 in(");
				sqlstr.append(namestr.substring(0,namestr.length()-1));
				sqlstr.append(")");
				
				dao.update(sqlstr.toString());
				
				if(!this.userView.isSuper_admin()){
				    String roleid="",flag="";
				    if(this.userView.getStatus()==0){
				    	roleid=this.userView.getUserId();
				    	flag="0";
				    }else{
				    	roleid=this.userView.getDbname()+this.userView.getA0100();
				    	flag="4";
				    }
				    SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
					String res_str=privbo.getWarn_str();
					ResourceParser parser=new ResourceParser(res_str,10);
					String aaa = parser.getContent();
					namestr="',"+namestr+"'";
					namestr=namestr.replaceAll("','", ",");
					if(!aaa.endsWith(",")){
						parser.reSetContent(aaa+namestr.substring(0, namestr.length()-1));
					}else{
						parser.reSetContent(aaa+namestr.substring(1,namestr.length()-1));
					}
					res_str=parser.outResourceContent();
					saveResourceString(roleid,flag,res_str);
			    }
			}
			if(exper.length()>1)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.job.fail")+"\n"+exper.toString()));
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("msg", msg);
	}

	private void saveResourceString(String role_id,String flag,String res_str)
    {
        if(res_str==null)
        	res_str="";
        /*
        RecordVo vo=new RecordVo("t_sys_function_priv",1);
        vo.setString("id",role_id);
        vo.setString("status",flag);
        vo.setString("warnpriv",res_str);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save(); 
        */
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(flag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	this.frowset=dao.search(strsql.toString());
	    	cat.debug("select sql="+strsql.toString());	

	    	if(this.frowset.next())
	    	{
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("update t_sys_function_priv set warnpriv=?");
	    		//strsql.append(field_str);
	    		strsql.append(" where id='");
	    		strsql.append(role_id);
	    		strsql.append("' and status=");
	    		strsql.append(flag);
	    	}
	    	else
	    	{
		    	paralist.add(role_id);	    		
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("insert into t_sys_function_priv (id,warnpriv,status) values(?,?,");
	    		strsql.append(flag);
	    		strsql.append(")");
	    	}
	    	cat.debug("updat warnpriv sql="+strsql.toString());
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }
    }	
}
