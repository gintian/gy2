package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.b_plan.CreateFileBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InputFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		String fileFlag = (String)this.getFormHM().get("fileFlag");
		
		String check = (String)this.getFormHM().get("check");
		check=check!=null&&check.trim().length()>0?check:"";
		CreateFileBo cb = new CreateFileBo(this.frameconn);
		if("outfile".equalsIgnoreCase(check)){
			String fileid = (String)this.getFormHM().get("fileid");
			fileid=fileid!=null&&fileid.trim().length()>0?fileid:"";
			String outname = cb.outFile(this.userView,flag, fileid);
			this.getFormHM().put("outname", outname);
		}else{
			String filepath = (String)this.getFormHM().get("filepath");
			filepath=filepath!=null&&filepath.trim().length()>0?filepath:"";
			

			String separator = System.getProperty("file.separator");
			filepath=filepath.replace("``", separator);
			//filepath=filepath+"media";
			//cb.createDir(filepath);
			if("54".equals(flag)){
				filepath+=separator+"resources";
			}else if("55".equals(flag)){
				filepath+=separator+"coureware";
			}
			cb.deletefile(filepath);
			cb.createDir(filepath);

			cb.createFile(filepath, flag);
		}
		this.getFormHM().put("fileFlag", fileFlag);
	}
	

}
