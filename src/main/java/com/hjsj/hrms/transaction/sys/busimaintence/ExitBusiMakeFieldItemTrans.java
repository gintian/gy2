package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 修改
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 10, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ExitBusiMakeFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String zijisel=(String)this.getFormHM().get("zijisel");
		String[] right_fields=(String[])this.getFormHM().get("right_fields");//构建的
		String[] left_fields=(String[])this.getFormHM().get("left_fields");//未构建的
		String dui="";
		if(left_fields!=null){
			if(right_fields.length>=left_fields.length){
				for(int i =0;i<right_fields.length;i++)
				{
					dui=right_fields[i];
					if(!"".equals(dui)||dui!=null)
					{
						for(int j=0;j<left_fields.length;j++)
						{
							if(dui.equalsIgnoreCase(left_fields[j]))
							{
								left_fields[j]=null;
							}
						}
					}
					
				}
			}
		}
		

		ArrayList recordlist=new ArrayList();  //构建的
		ArrayList leftlist = new ArrayList();  //未构建的
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String editflag="1";
		
		try
		{
			recordlist=constructField(dao,right_fields,zijisel,"1",recordlist);//构建的
			leftlist =constructFields(dao,left_fields,zijisel,"0",leftlist);//未构建的
			updateDictionary(dao,recordlist,leftlist);
			updateArchivetable(dao,recordlist,leftlist,zijisel);
		}catch(Exception e)
		{
			e.printStackTrace();
			editflag="2";
		}
		this.getFormHM().put("zijisel", zijisel);
		this.getFormHM().put("editflag", editflag);
	}
	/**
	 * 构建指标
	 * @param right_fields
	 */
    private ArrayList constructField(ContentDAO dao,String[] fields,String setid,String useflag,ArrayList recordlist) throws GeneralException, SQLException
    {
    	if(fields==null||fields.length<=0)
    		return recordlist;    	
    	for(int i=0;i<fields.length;i++)
    	{
    		String item=fields[i];			
			RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
			busiFieldVo.setString("fieldsetid",setid);
			busiFieldVo.setString("itemid",item);
			busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
//			busiFieldVo.setString("displayid",i+1+"");
			busiFieldVo.setString("useflag",useflag);
			dao.updateValueObject(busiFieldVo);
			recordlist.add(busiFieldVo);
    	}
        return recordlist;
    }
    private ArrayList constructFields(ContentDAO dao,String[] fields,String setid,String useflag,ArrayList leftlist)
    {
    	try{
    		if(fields==null||fields.length<=0)
        		return leftlist;    	
        	for(int i=0;i<fields.length;i++)
        	{
        		String item=fields[i];
        		if(item != null && !"null".equalsIgnoreCase(item)){
        			RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
        			busiFieldVo.setString("fieldsetid",setid);
        			busiFieldVo.setString("itemid",item);
        			busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
//        			busiFieldVo.setString("displayid",i+1+"");
        			busiFieldVo.setString("useflag",useflag);
        			dao.updateValueObject(busiFieldVo);
        			leftlist.add(busiFieldVo);
        		}else
        		{
        			return leftlist;
        		}
    			
        	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return leftlist;
    	
    }
    
    private void updateDictionary(ContentDAO dao,ArrayList recordlist,ArrayList leftlist) throws GeneralException{
		TableOperateBo tob=new TableOperateBo(this.getFrameconn());
		ArrayList fieldlist=new ArrayList();
		ArrayList leftlistvalue= new ArrayList(); //未构库指标
		String tablename="";
		for(int i=0;i<recordlist.size();i++){
			RecordVo busiVo=(RecordVo) recordlist.get(i);		
			tablename=busiVo.getString("fieldsetid");			
				boolean flag=false;
				if("1".equals(busiVo.getString("keyflag"))){
					flag=true;
				}
				Field temf=tob.getField(flag,busiVo.getString("itemid"),busiVo.getString("itemdesc"),busiVo.getString("itemtype"),busiVo.getInt("itemlength"),busiVo.getInt("decimalwidth"));
			    fieldlist.add(temf);
		}
		for(int j=0;j<leftlist.size();j++)
		{
			if(leftlist.isEmpty())
			{
				leftlistvalue=null;
			}else
			{
				RecordVo busiVo=(RecordVo) leftlist.get(j);
				tablename=busiVo.getString("fieldsetid");
				Field temfs= new Field (busiVo.getString("itemid"),busiVo.getString("itemdesc"));
				leftlistvalue.add(temfs);
			}
			//RecordVo busiVo=(RecordVo) recordlist.get(j);
			
		}
		tob.create_update_Tables(tablename,fieldlist,true,leftlistvalue);
		if("Q03".equalsIgnoreCase(tablename)){
			tob.create_update_Tables("Q05",fieldlist,true,leftlistvalue);
			tob.create_update_Tables("Q07",fieldlist,true,leftlistvalue);
			tob.create_update_Tables("Q09",fieldlist,true,leftlistvalue);
		}
		
		//检查是不是考勤模块的表
		
		//是考勤里的表
		
		//是否存在对应的归档表
		
		//有归档表 _arc
		
		
		DataDictionary.refresh();

	}
    
    private void updateArchivetable(ContentDAO dao,ArrayList recordlist,ArrayList leftlist,String zijisel) throws SQLException{
        TableOperateBo tob=new TableOperateBo(this.getFrameconn());
        ArrayList fieldlist=new ArrayList();
        ArrayList leftlistvalue= new ArrayList(); //未构库指标
        String tablename="";
        for(int i=0;i<recordlist.size();i++){
            RecordVo busiVo=(RecordVo) recordlist.get(i);       
            tablename=busiVo.getString("fieldsetid");           
                boolean flag=false;
                if("1".equals(busiVo.getString("keyflag"))){
                    flag=true;
                }
                Field temf=tob.getField(flag,busiVo.getString("itemid"),busiVo.getString("itemdesc"),busiVo.getString("itemtype"),busiVo.getInt("itemlength"),busiVo.getInt("decimalwidth"));
                fieldlist.add(temf);
        }
        for(int j=0;j<leftlist.size();j++)
        {
            if(leftlist.isEmpty())
            {
                leftlistvalue=null;
            }else
            {
                RecordVo busiVo=(RecordVo) leftlist.get(j);
                tablename=busiVo.getString("fieldsetid");
                Field temfs= new Field (busiVo.getString("itemid"),busiVo.getString("itemdesc"));
                leftlistvalue.add(temfs);
            }
            
        }
        
        //检查是不是考勤模块的表
        this.frowset = dao.search("select * from t_hr_busitable where id = '30' and fieldsetid = '"+zijisel+"'");
        if (this.frowset!=null)//是考勤里的表
        {
            //是否存在对应的归档表
            DbWizard dbWizard = new DbWizard(frameconn);
            if(dbWizard.isExistTable(tablename+"_arc",false))//有归档表 _arc
            {
                tob.create_update_Tables(tablename+"_arc",fieldlist,true,leftlistvalue);
                if("Q03".equalsIgnoreCase(tablename)){
                    tob.create_update_Tables("Q05_arc",fieldlist,true,leftlistvalue);
                    tob.create_update_Tables("Q07_arc",fieldlist,true,leftlistvalue);
                    tob.create_update_Tables("Q09_arc",fieldlist,true,leftlistvalue);
                }
            }
            
        }
    }
}

