package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * @author dengc
 *
 */
public class ValidateSelectObjTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String tabid = (String)this.getFormHM().get("tabid");
			String taskid = (String)this.getFormHM().get("taskid");
			String batch_task = (String)this.getFormHM().get("batch_task");
			String sp_batch = (String)this.getFormHM().get("sp_batch");
			
			String selfapply = (String)this.getFormHM().get("selfapply");//
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String flag="1";  // 0:有未选择的记录（不是全选）: 1:全选  2:表中没有数据 4：一条也没有选中（全不选）
			
			if("0".equals(taskid)&&!"1".equals(sp_batch))//如果不是批量处理
			{
				String tabname=this.userView.getUserName()+"templet_"+tabid;
				if("true".equals(selfapply))
				{
					tabname="g_"+tabname;
				}
				int count=0;//未选中的数据条数
				int count1=0;//表中所有的数据条数
				StringBuffer sb = new StringBuffer("");
				sb.append("select count(*) from "+tabname+" where  "+Sql_switcher.isnull("submitflag", "0")+"=0 ");
				this.frowset=dao.search(sb.toString());
				if(this.frowset.next())
				{
					count=this.frowset.getInt(1);
					if(this.frowset.getInt(1)>0)
						flag="0";
				}
				sb.setLength(0);
				sb.append("select count(*) from "+tabname+" ");
				this.frowset=dao.search(sb.toString());
				if(this.frowset.next())
				{
					count1=this.frowset.getInt(1);
					if(this.frowset.getInt(1)==0)
						flag="2";
				}
				if(count1-count==0&&count>0)
				{
					flag="4";
				}
				
				
			}
			else//如果是批量处理
			{
				
			    String sqlstr ="";
				if("1".equals(sp_batch))
				{ 
				    flag=validateBatchTasks(dao,tabid,batch_task);
					/*
				    String[] temps=batch_task.split(",");
					StringBuffer ss=new StringBuffer("");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].trim().length()>0)
							ss.append(","+temps[i]);
					}
					sqlstr="select count(seqnum) from t_wf_task_objlink where tab_id="+tabid+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id in ("+ss.substring(1)+")   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and lower(username)='"+this.userView.getUserName().toLowerCase()+"'   )  )";
					String _sql="select count(seqnum) from t_wf_task_objlink where tab_id="+tabid+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id in ("+ss.substring(1)+") and "+Sql_switcher.isnull("submitflag", "0")+"=0  and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and lower(username)='"+this.userView.getUserName().toLowerCase()+"'   )  )";
					this.frowset=dao.search(_sql);
					if(this.frowset.next())
                    {
                        if(this.frowset.getInt(1)>0)
                            flag="0";
                    }
                    this.frowset=dao.search(sqlstr);
                    if(this.frowset.next())
                    {
                        if(this.frowset.getInt(1)==0)
                            flag="2";
                    }
			*/
				}
				else
				{
					String sql="select count(seqnum) from t_wf_task_objlink where tab_id="+tabid+" and task_id="+taskid+" and "+Sql_switcher.isnull("submitflag", "0")+"=0 ";
					sql+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
					sqlstr ="select count(seqnum) from t_wf_task_objlink where tab_id="+tabid+" and task_id="+taskid+" ";
					sqlstr+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{
					    if(this.frowset.getInt(1)>0)
					        flag="0";
					}
					this.frowset=dao.search(sqlstr);
					if(this.frowset.next())
					{
					    if(this.frowset.getInt(1)==0)
					        flag="2";
					}
				}
			}
			this.getFormHM().put("flag",flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private String validateBatchTasks(ContentDAO dao,String tabid,String batch_task)throws GeneralException
    {
	    String flag="1";
        try
        {
            String tabname="templet_"+tabid;
            TemplateTableBo tableBo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
            String a0101="a0101_1"; 
            String keyTitle="人员";
            if (tableBo.getInfor_type()==1){
                if(tableBo.getOperationtype()==0)//调入
                {
                    a0101="a0101_2";
                }
            }
            else if(tableBo.getInfor_type()==2 || tableBo.getInfor_type()==3)
            {
                if (tableBo.getInfor_type()==2){
                    keyTitle="机构" ;
                }
                else {
                    keyTitle="岗位" ;
                }
                a0101="codeitemdesc_1";
                if(tableBo.getOperationtype()==5)
                    a0101="codeitemdesc_2";
            }
            
            String[] temps=batch_task.split(",");
            StringBuffer ss=new StringBuffer("");
            for(int i=0;i<temps.length;i++)
            {
                if(temps[i]!=null&&temps[i].trim().length()>0)
                    ss.append(","+temps[i]);
            }
            //批量审批，多个单据没必要同时提交，只要来自同一单据的人员被全选中了即可。
            String sql="select distinct task_id from t_wf_task_objlink where tab_id="
                +tabid+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id in ("+ss.substring(1)
                +") and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
                +Sql_switcher.isnull("special_node","0")+"=0 or ("
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )";
            ArrayList selectTaskList= dao.searchDynaList(sql);
            StringBuffer strHint= new StringBuffer();
            strHint.append("来源于同一单据的"+keyTitle+"，不支持部分审批！");
            boolean bHave=false;//有未选中记录
            for (int i=0;i<selectTaskList.size();i++){//分析选中的记录
                LazyDynaBean bean = (LazyDynaBean)selectTaskList.get(i);
                String task_id= (String)bean.get("task_id");
                
                RecordVo task_vo=new RecordVo("t_wf_task");
                task_vo.setInt("task_id",Integer.parseInt(task_id));
                task_vo=dao.findByPrimaryKey(task_vo);
                if(task_vo==null)
                    throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));  
                String task_topic= task_vo.getString("task_topic");
                //未选中人员                
                sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
                    +" and tab_id="
                    +tabid+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
                    +" and "+Sql_switcher.isnull("submitflag", "0")+"=0  and ( "
                    +Sql_switcher.isnull("special_node","0")+"=0 or ("
                    +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )"
                    +")";
                this.frowset=dao.search(sql);
                if (this.frowset.next()){
                    bHave=true;
                    strHint.append("\r\n");
                    flag="5"; 
                    strHint.append(task_topic+":");
                    strHint.append("未选中"+keyTitle+":");
                    String a0101s="";
                    this.frowset.beforeFirst();                    
                    while (this.frowset.next()){
                        String a0101Value=this.frowset.getString(a0101);
                        if (a0101s.length()>0 ) a0101s=a0101s+"、";
                        a0101s=a0101s+a0101Value;
                    }
                    strHint.append(a0101s); 
                  //已选中人员
                    sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
                        +" and tab_id="
                        +tabid+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
                        +" and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
                        +Sql_switcher.isnull("special_node","0")+"=0 or ("
                        +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )"
                        +")";   
                    strHint.append("    已选中"+keyTitle+":");
                    a0101s="";
                    this.frowset=dao.search(sql);
                    while (this.frowset.next()){
                        String a0101Value=this.frowset.getString(a0101);
                        if (a0101s.length()>0 ) a0101s=a0101s+"、";
                        a0101s=a0101s+a0101Value;
                    }
                    strHint.append(a0101s); 
                }
                if (!bHave) strHint.setLength(0);
                this.getFormHM().put("selectHint",SafeCode.encode(strHint.toString()));
            }
            if (selectTaskList.size()<1){//没有选中记录
                flag="2";
            }
           
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return flag;
    }
	
	
	   
 

}
