package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.HashMap;

public class ReductionFileTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
	    InputStream in = null;
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String b_init = (String)map.get("b_reduction");
			if(!"init".equalsIgnoreCase(b_init))
			{
		    	String projectpath=SafeCode.decode((String)this.getFormHM().get("path"));
	    		FormFile file = (FormFile)this.getFormHM().get("r_file");
	    		if(!FileTypeUtil.isFileTypeEqual(file)){
	    			 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
	    		}
	    		ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
	    		String parent = bo.getParentPath(projectpath);
	    		//bo.unzip(parent,file);
	    		in = file.getInputStream();
	    		bo.reductionFile(in, parent);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		finally 
		{
		    com.hjsj.hrms.utils.PubFunc.closeIoResource(in);
		}
		
	}

}
