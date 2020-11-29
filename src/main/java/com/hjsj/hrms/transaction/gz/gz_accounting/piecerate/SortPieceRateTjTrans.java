package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 计件统计信息报表排序
 * @author tianye
 * @date 2013-4-8
 */
public class SortPieceRateTjTrans extends IBusiness{

	public void execute() throws GeneralException {
		
			StringBuffer info = new StringBuffer();
			String defIds=(String)this.getFormHM().get("defId")==null?"":(String)this.getFormHM().get("defId");
			String sortIds=(String)this.getFormHM().get("sortIds")==null?"":(String)this.getFormHM().get("sortIds");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String[] dIds = defIds.split(",");
			String[] sIds = sortIds.split(",");
			//Arrays.sort(sIds);
			//当dIds与sIds长度不行等时，说明用户进行了删除操作，但是sIds长度肯定大于dIds，即使用户删除了也不影响排序
			RecordVo vo = new RecordVo("hr_summarydef");
			if(dIds.length>0){
				for(int i = 0;i<dIds.length;i++){
					try {
							vo.setInt("defid", Integer.parseInt(dIds[i]));
							vo = dao.findByPrimaryKey(vo);
							vo.setInt("sortid", Integer.parseInt(sIds[i]));
							dao.updateValueObject(vo);
						} catch (SQLException sqle) {
							sqle.printStackTrace();
							info.append("第"+i+"条排序失败！\r\n");
						}
				}
				if(!"".equals(info.toString())){
					info.append("请重新排序！");
				}else{
					info.append("排序保存成功");
				}
			}else{
				info.append("没有可排序进行保存");
			}
			this.getFormHM().put("info",info.toString() );
	}

}
