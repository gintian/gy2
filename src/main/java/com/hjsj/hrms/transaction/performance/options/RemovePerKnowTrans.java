package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class RemovePerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {	
		String removestr = (String)this.getFormHM().get("deletestr");
		String num = (String)this.getFormHM().get("num");	
		String seq = (String)this.getFormHM().get("seq");	
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		/** 上移操作*/
		if("1".equals(num)){
			if(Integer.parseInt(seq)>1){
				try{
					 int seqmax=this.getMaxSeq(seq);
					 int knowId=this.getKnowId(seqmax);
					 if(seqmax>-1){
						 RecordVo vo=new RecordVo("per_know");
						 vo.setString("know_id",""+knowId);
		                 vo.setString("seq",seq);
		   	   		  	 dao.updateValueObject(vo);	
						
						 RecordVo voRemove=new RecordVo("per_know");
						 voRemove.setString("know_id",removestr);
						 voRemove.setString("seq",""+seqmax); 
		   	   		  	 dao.updateValueObject(voRemove);	
					 }
				}catch (Exception exx) {
					exx.printStackTrace();
				    throw GeneralExceptionHandler.Handle(exx);
				}
			}
			
			
		}
		/** 下移操作*/
		if("-1".equals(num)){
			if(Integer.parseInt(seq)<this.getMinSeq(seq)){
				try{
					 int seqmin=this.getMinSeq(seq);
					 int knowId=this.getKnowId(seqmin);
					 RecordVo vo=new RecordVo("per_know");
					 vo.setString("know_id",""+knowId);
	                 vo.setString("seq",seq);
	   	   		  	 dao.updateValueObject(vo);	
					
					 RecordVo voRemove=new RecordVo("per_know");
					 voRemove.setString("know_id",removestr);
					 voRemove.setString("seq",""+seqmin); 
	   	   		  	 dao.updateValueObject(voRemove);	
				}catch (Exception exx) {
					exx.printStackTrace();
				    throw GeneralExceptionHandler.Handle(exx);
				}
			}
			
		}
	}
	
	
	/** 查出 per_mainbodyset 表的字段 seq 的最大值*/
	public synchronized int getMaxSeq(String seq) throws GeneralException{
		int num = -1;  //序号默认为0
		String sql="select max(seq) as num  from per_know where  seq<"+seq;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				num = this.frowset.getInt("num");
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num;		
	}
	public synchronized int getMinSeq(String seq) throws GeneralException{
		int num = -1;  //序号默认为0
		String sql="select min(seq) as num  from per_know where  seq>"+seq;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				num = this.frowset.getInt("num");
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num;		
	}
	/** 根据 seq 的值查出 per_mainbodyset 表的字段 body_id 的值*/
	public synchronized int getKnowId(int seq) throws GeneralException{
		int bodyId = 0;  //序号默认为0
		String sql="select know_id from per_know where seq="+seq;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				bodyId = this.frowset.getInt("know_id");
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return bodyId;		
	}

}
