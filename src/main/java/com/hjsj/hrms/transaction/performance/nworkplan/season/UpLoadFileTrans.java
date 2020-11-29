package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.sql.Blob;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UpLoadFileTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn , this.userView);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String year = (String)this.getFormHM().get("year");
		String type = (String)this.getFormHM().get("type");
		String isdept = (String)this.getFormHM().get("isdept");//1:个人 2：部门
		String season = "";
		String p0100 = (String)this.getFormHM().get("p0100");//p0100如果有值，说明p01中有记录了。如果没值，说明没有记录
		if("1".equals(type)){
			season = (String)this.getFormHM().get("season");
		}else{
			season = "13";
		}
		String startMonth = (String)this.getFormHM().get("startMonth");
		String endMonth = (String)this.getFormHM().get("endMonth");
		//message = SafeCode.decode(message);
		String startDate = "";
		String endDate = "";
		if("1".equals(type)){//季报
			startDate = year + "-" + startMonth + "-01";
			String strDate = year+"-"+endMonth+"-1";
			int totalday = bo.getTotalDay(strDate);
			endDate = year + "-" + endMonth +"-"+totalday;
		}else if("2".equals(type)){//年报
			startDate = year + "-01-01";
			endDate = year + "-12-31";
		}
		
		FormFile form_file = (FormFile) getFormHM().get("file");
		String fileName=(String)this.getFormHM().get("fileName");
		int maxsize = 1024*1024;//KB为单位
		String file_max_size=maxsize+"";
		if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0)
		{
			file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
			if(file_max_size.toLowerCase().indexOf("k")!=-1)
				file_max_size=file_max_size.substring(0,file_max_size.length()-1);
		}
		
		try {
			if(form_file.getFileData().length==0){
				this.getFormHM().put("isTooBig", "2");//上传文件为空
			}else{
				if (form_file != null && form_file.getFileData().length > 0
						&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {//如果允许插入数据
					
					//在P01中判断当年 当前登录人 是否已经有记录 如果没有 则插入p0100 如果有 则取到P0100
					if("".equals(p0100)){//如果p01中没有数据
						try {
							p0100 = bo.InsertInfoToP01(isdept,type, Integer.parseInt(season), Integer.parseInt(year),new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
//					else{
//						p0100 = bo.getP0100ByCheckSeason(Integer.parseInt(year), Integer.parseInt(season), isdept , type);
//					}
					String fname = form_file.getFileName();
					String [] files = fname.split("[.]");
					int indexInt = fname.lastIndexOf(".");
					String ext = fname.substring(indexInt + 1, fname.length());
					int fileId = bo.InsertInToFile(p0100);
					
					String sql = "update per_diary_file set ext=?,content=?,name=? where file_id=?";
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					ArrayList paramList = new ArrayList();
					paramList.add(ext);
					// blob字段保存,数据库中差异
					switch (Sql_switcher.searchDbServer()) {
					case Constant.ORACEL:
						Blob blob = bo.getOracleBlob(form_file, "per_diary_file",fileId);
						paramList.add(blob);
						paramList.add(files[0]);
						paramList.add(fileId);
						break;
					default:
						byte[] data = form_file.getFileData();
					// a_vo.setObject("affix",data);
						paramList.add(data);
						paramList.add(files[0]);
						paramList.add(fileId);
					break;
					}
					dao.update(sql,paramList);
					this.getFormHM().put("isTooBig", "0");//上传成功
				}else{
					this.getFormHM().put("isTooBig", "1");//上传文件过大
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
}
