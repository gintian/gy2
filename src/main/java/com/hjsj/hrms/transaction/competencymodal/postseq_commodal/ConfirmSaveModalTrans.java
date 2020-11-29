package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
/**
 * 
* 
* 类名称：ConfirmSaveModalTrans   
* 类描述：岗位素质模型指标修改保存验证是否有素质计划引用了此指标打分，并做出提示
* Company:HJSJ   
* 创建人：zhaozk
* 创建时间：Dec 2, 2013 10:26:57 AM     
* @version    
*
 */
public class ConfirmSaveModalTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor="ok";
		String point_id=(String)this.getFormHM().get("point_id");
		String object_type=(String)this.getFormHM().get("object_type");
		String object_id=(String)this.getFormHM().get("object_id");
		ResultSet res=null;
		ContentDAO dao  = new ContentDAO(this.frameconn);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate=sdf.format(calendar.getTime());
		HashMap map=new HashMap();
		try{
			if("3".equals(object_type)){

				//岗位素质模型指标
				/**************先判断启动时间在岗位素质模型指标有效期内的素质计划是否引用了岗位素质模型指标打过分******************/
				boolean bool=true;
				String head="";
				String body="";
				StringBuffer sb=new StringBuffer();
				sb.append(" select pcm.point_id,per_point.pointname,pp.plan_id,pp.name from per_plan pp,per_object po,per_competency_modal pcm ,per_ScoreDetail psd,per_point");
				sb.append(" where pcm.point_id='"+point_id+"' and pcm.object_type="+Integer.parseInt(object_type)+" and pcm.object_id='"+object_id+"' and "+Sql_switcher.dateValue(nowDate)+" between pcm.start_date and pcm.end_date ");
				sb.append(" and pp.execute_date between pcm.start_date and pcm.end_date");
				sb.append(" and pp.busitype=1");
				sb.append(" and pp.plan_id=po.plan_id");
				sb.append(" and po.E01A1=pcm.object_id");
				sb.append(" and pp.plan_id=psd.plan_id");
				sb.append(" and pcm.point_id=psd.point_id");
				sb.append(" and pcm.point_id=per_point.point_id");
				res=dao.search(sb.toString());
				while(res.next()){
					bool=false;
					if(map.get(res.getString("plan_id"))==null){
						head="修改指标 "+res.getString("pointname")+" 将会对以下计划的素质分析结果产生影响: \n";
						body+=" 计划号:"+res.getString("plan_id")+"  "+res.getString("name")+"  \n";
						map.put(res.getString("plan_id"), "1");
					}

				}
				if(bool){
					/**************再判断归档时间在岗位素质模型指标有效期内的素质计划是否引用了岗位素质模型指标*****************/
					sb.setLength(0);
					sb.append(" select pcm.point_id,per_point.pointname,pp.plan_id,pp.name from per_plan pp,per_history_result phr,per_competency_modal pcm,per_point ");
					sb.append(" where pcm.point_id='"+point_id+"' and pcm.object_type="+object_type+" and pcm.object_id='"+object_id+"' and "+Sql_switcher.dateValue(nowDate)+" between pcm.start_date and pcm.end_date ");
					sb.append(" and phr.archive_date between pcm.start_date and pcm.end_date");
					sb.append(" and pp.busitype=1");
					sb.append(" and pp.plan_id=phr.plan_id");
					sb.append(" and phr.E01A1=pcm.object_id");
					sb.append(" and phr.point_id=pcm.point_id");
					sb.append(" and pcm.point_id=per_point.point_id");
					res=dao.search(sb.toString());
					while(res.next()){
						bool=false;
						if(map.get(res.getString("plan_id"))==null){
							head="修改指标 "+res.getString("pointname")+" 将会对以下计划的素质分析结果产生影响: \n";
							body+="计划号:"+res.getString("plan_id")+"  "+res.getString("name")+"  \n";
							map.put(res.getString("plan_id"), "1");
						}
					}
				}
				if(!bool){
					infor=head+body+" 是否要继续修改? \n (若不想影响历史结果分析，建议删除此指标再重建!)";
				}
			}
			//System.out.println(infor);
			this.getFormHM().put("infor", SafeCode.encode(infor));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(res!=null){
				try {
					res.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


}
