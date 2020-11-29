package com.hjsj.hrms.businessobject.train;

import java.sql.Connection;

/**
 * <p>
 * Title:TrainArchiveBoFactory.java
 * </p>
 * <p>
 * Description:培训归档业务对象抽象工厂
 * </p>
 * <p>
 * Company:hjsoft
 * </p>
 * <p>
 * create time:2012-04-25 15:25:00
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 * 
 */

public abstract class TrainArchiveBoFactory
{
    //培训班归档
    public final static String ARCH_CLASS = "1";
    //培训教师归档
    public final static String ARCH_TEACHER = "2";
    //培训考试归档
    public final static String ARCH_EXAM = "3";
    
    public static TrainArchiveBaseBo getTrainArchiveBo(String archType, String busiId, Connection cn)
    {           
        TrainArchiveBaseBo bo = null;
        if (ARCH_CLASS.equalsIgnoreCase(archType))
        {            
            bo = new TrainClassArchiveBo(busiId, cn); 
        }
        else if(ARCH_TEACHER.equalsIgnoreCase(archType))
        {
        	bo = new TrainTeacherArchiveBo(busiId, cn);
        }
        else if(ARCH_EXAM.equalsIgnoreCase(archType))
        {
            bo = new TrainExamArchiveBo(busiId, cn);
        } 
        else 
        {
            //todo 其它培训内容归档
        }
        
        return bo;
    }
}
