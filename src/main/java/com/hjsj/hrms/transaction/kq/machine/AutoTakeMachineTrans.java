package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoTakeMachineTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String machine_num=(String)this.getFormHM().get("machine_num");		//考勤机编号
		String machine_data=(String)this.getFormHM().get("machine_data");//考勤数据
		String cardno_len=(String)this.getFormHM().get("cardno_len"); //最开始指定中控指纹机卡号长度的 是否是5位考勤机的
		String datarule=(String)this.getFormHM().get("datarule");//文件规则编号
		KqCardData kqCardData=null;
		ArrayList filelist =new ArrayList();
		String flag="ok";
		if(datarule==null||datarule.length()<=0)
		{
			StringBuffer sql=new StringBuffer();				
			sql.append("select type_id,location,card_len,machine_no from kq_machine_location");
			sql.append(" where location_id='"+machine_num+"'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());		
			String type_id="";				
			String location="";
			String card_len="";
			String machine_no="";
			try
			{
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					type_id=this.frowset.getString("type_id");
					location=this.frowset.getString("location");
					card_len=this.frowset.getString("card_len");
					machine_no=this.frowset.getString("machine_no");
					
					if(card_len == null || card_len.length()<=0 || "0".equals(card_len))
					{
					    card_len = cardno_len;
					}
				}else
				{
					flag="error";
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.type.rule.error"),"",""));
				}
				if(type_id!=null&&!"0".equals(type_id))
				{
					machine_no="";
				}
			}catch(Exception e)
			{
				flag="error";
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}		 
			UserView userView =new UserView("su",this.getFrameconn());
			try {
				if(!userView.canLogin())
				{
					flag="error";
					this.getFormHM().put("flag", flag);
					throw GeneralExceptionHandler.Handle(new GeneralException("","初始化错误！","",""));				
				}	
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				throw GeneralExceptionHandler.Handle(new GeneralException("","初始化错误！","",""));				
			}
			kqCardData=new KqCardData(userView,this.getFrameconn(),"Machine",location);
			HashMap hashM=kqCardData.getFile_Rule(type_id,cardno_len,card_len);	
			try
			{
				if(hashM!=null)
				{
					String[] datas=machine_data.split("`");
					if(datas!=null&&datas.length>0)
					{
						for(int i=0;i<datas.length;i++)
						{
							String line=datas[i];
						
							if(line==null||line.length()<=0)
								continue;
							filelist.add(kqCardData.getFileValue(line,hashM,machine_no));
							
						}
					}else
					{
						flag="error";
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.take.data.error"),"",""));
					}
					
				}else
				{
					flag="error";
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.type.rule.error"),"",""));
				}
			}catch(Exception e)
			{
				flag="error";
				throw GeneralExceptionHandler.Handle(e);
			}
		}else{
			HashMap hashM=getFile_Rule(datarule);
			kqCardData=new KqCardData(this.userView,this.getFrameconn());
			if(hashM!=null)
			{
				int max_L=Integer.parseInt((String)hashM.get("max"));				
				try
				{
					String[] datas=machine_data.split("`");					
					if(datas!=null&&datas.length>0)
					{
						for(int i=0;i<datas.length;i++)
						{
							String line=datas[i];							
							if(line==null||line.length()<=0)
								continue;				
							if(i==0)
						    {
							   String status=(String)hashM.get("status");
							   if(status!=null&& "1".equals(status))
							   {
								   machine_num=line;
							   }else
							   {
								   if(line.length()<=0)
									{
										continue;
									}else if(max_L!=line.length())
									{
										throw GeneralExceptionHandler.Handle(new GeneralException("","文件格式不正确!","",""));
									}
								    filelist.add(kqCardData.getFileValue(line,hashM,machine_num));   
							   }
						   }else
						   {
							   if(line.length()<=0)
								{
									
									continue;
								}else if(max_L!=line.length())
								{
									throw GeneralExceptionHandler.Handle(new GeneralException("","文件格式不正确!","",""));
									
								}
							   filelist.add(kqCardData.getFileValue(line,hashM,machine_num));    
						   }
						}
					}
				}catch(Exception e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.file.error"),"",""));
				}finally
				{
					
						
				}
				ArrayList  dblist=this.userView.getPrivDbList();
				ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());			
				String org_id=managePrivCode.getPrivOrgId();  
			    KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn()); 
				String cardno_field=kq_paramter.getCardno();
				if(cardno_field!=null||cardno_field.length()>0)
				{
					kqCardData.insert_kq_originality_data(filelist,dblist,cardno_field);
				}else
				{
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.card.nocreate.card_no"),"",""));
				}
						
			}
		}
		
			   
		ArrayList  dblist=userView.getPrivDbList();
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());			
		String org_id=managePrivCode.getPrivOrgId();  
	    KqParameter kq_paramter = new KqParameter(this.getFormHM(),userView,org_id,this.getFrameconn()); 
		String cardno_field=kq_paramter.getCardno();		
		if(cardno_field!=null||cardno_field.length()>0)
		{
						
			if(!kqCardData.insert_kq_originality_data(filelist,dblist,cardno_field))
			{
				flag="error";
				throw GeneralExceptionHandler.Handle(new GeneralException("","数据处理失败","",""));
			}			
		}else
		{
			flag="error";
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.card.nocreate.card_no"),"",""));
		}
		this.getFormHM().put("flag",flag);
	
		
	}
	/**
	 * 返回对应规则
	 * @param file_num
	 * @throws GeneralException
	 */
	public HashMap getFile_Rule(String file_num)throws GeneralException 
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		sql.append("select rule_id,rule_name,machine_s,machine_e,card_s,card_e,");
		sql.append("year_s,year_e,md_s,md_e,hm_s,hm_e,status from kq_data_rule");
		sql.append(" where rule_id='"+file_num+"'");
		HashMap hashM=new HashMap();
		int max=0;
		int cont=0;
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				hashM.put("machine_s",this.frowset.getString("machine_s"));
				hashM.put("machine_e",this.frowset.getString("machine_e"));
				hashM.put("card_s",this.frowset.getString("card_s"));
				hashM.put("card_e",this.frowset.getString("card_e"));
				hashM.put("year_s",this.frowset.getString("year_s"));
				hashM.put("year_e",this.frowset.getString("year_e"));
				hashM.put("md_s",this.frowset.getString("md_s"));
				hashM.put("md_e",this.frowset.getString("md_e"));
				hashM.put("hm_s",this.frowset.getString("hm_s"));
				hashM.put("hm_e",this.frowset.getString("hm_e"));
				hashM.put("status",this.frowset.getString("status"));			
				if(max<cont)
					max=cont;
				if(this.frowset.getString("card_s")!=null&&this.frowset.getString("card_s").length()>0)
					cont=Integer.parseInt(this.frowset.getString("card_s"));
				if(max<cont)
					max=cont;
				if(this.frowset.getString("year_s")!=null&&this.frowset.getString("year_s").length()>0)
					cont=Integer.parseInt(this.frowset.getString("year_s"));
				if(max<cont)
					max=cont;
				if(this.frowset.getString("status")==null||!"1".equals(this.frowset.getString("status")))
				{
					if(this.frowset.getString("machine_s")!=null&&this.frowset.getString("machine_s").length()>0)
						cont=Integer.parseInt(this.frowset.getString("machine_s"));
					if(max<cont)
						max=cont;
					if(this.frowset.getString("machine_e")!=null&&this.frowset.getString("machine_e").length()>0)
						cont=Integer.parseInt(this.frowset.getString("machine_e"));
					if(max<cont)
						max=cont;
				}
				if(this.frowset.getString("md_s")!=null&&this.frowset.getString("md_s").length()>0)
					cont=Integer.parseInt(this.frowset.getString("md_s"));
				if(max<cont)
					max=cont;
				if(this.frowset.getString("md_e")!=null&&this.frowset.getString("md_e").length()>0)
					cont=Integer.parseInt(this.frowset.getString("md_e"));
				
				if(max<cont)
					max=cont;
				if(this.frowset.getString("hm_s")!=null&&this.frowset.getString("hm_s").length()>0)
					cont=Integer.parseInt(this.frowset.getString("hm_s"));
				if(max<cont)
					max=cont;
				if(this.frowset.getString("hm_e")!=null&&this.frowset.getString("hm_e").length()>0)
					cont=Integer.parseInt(this.frowset.getString("hm_e"));
				if(max<cont)
					max=cont;
				hashM.put("max",max+"");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.work.error"),"",""));
		}
		return hashM;
	}
}
