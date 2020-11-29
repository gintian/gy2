package com.hrms.hjsj.syssqllog;

import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author guodd
 * createtime 2016-09-08
 * description SQL语句记录日志
 *
 */
public class SqlLogger{

    private static String path = null;
    //记录sql执行日志logger
    private static Logger sql_logger = null;
    /**
     * 不记录日志的表，需要扩展时在括号中添加过滤规则：
     * 例如追加Usr开头的表：((t#([^\"]+))|(Usr([^\"]+)))
     * 或 名为"fr_txlog"的表：(fr_txlog)|(fr_txlog([^\"]+))
     * 这是现在不需要记录的表名规则
     *  XXXresult  XXX_muster_XXX  ##xxx  temp_xxx t_temp_xxx t#xxx fr_txlog Template_Set  salaryset salary_mapping syslog
     */
    private static String tableFilter = "(t#([^\"]+))|(([^\"]+)result)|(([^\"]+)_muster_([^\"]+))|(##([^\"]+))|(temp_([^\"]+))|(t_temp_([^\"]+))|(fr_txlog)|(template_set)|(salaryset)|(salary_mapping)|(fr_txlog([^\"]+))|(template_set([^\"]+))|(salaryset([^\"]+))|(salary_mapping([^\"]+))|(syslog)|(syslog([^\"]+))";

    //第一遍校验是否符合操作
    private static Pattern sqlPattern = null;
    //第二遍校验表名
    private static Pattern sqlPatternTwo = null;


    static{

        try{
            //获取system中的日志路径
            String filePath = SystemConfig.getPropertyValue("sql_log_file");
            //当前系统路径分割符号
            String sysFileSep = System.getProperty("file.separator");

            getPath:if(filePath!=null && !"".equals(filePath)){
                filePath=new String(filePath.getBytes("ISO-8859-1"),"gb2312");
                filePath = filePath.replace("\\","/");
                filePath = filePath.replace("/", sysFileSep);
            }else{
                //读取 log4j 中配置的日志文件路径
                ResourceBundle bundle = ResourceBundle.getBundle("log4j");
                if(!bundle.containsKey("log4j.appender.file.File"))
                    break getPath;
                filePath = bundle.getString("log4j.appender.file.File");
                filePath=new String(filePath.getBytes("ISO-8859-1"),"gb2312");
                filePath = filePath.replace("\\","/");
                filePath = filePath.replace("/", sysFileSep);
                //默认日志文件名为sqllog.log
                filePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep)+1)+"sqllog.log";
            }

            if(filePath!=null && !"".equals(filePath)){
                //如果使用了变量，解析变量
                if(filePath.startsWith("${catalina.base}")){
                    filePath = System.getProperty("catalina.base")+filePath.substring(16);
                }
                if(filePath.startsWith("${catalina.home}")){
                    filePath = System.getProperty("catalina.home")+filePath.substring(16);
                }

                //获取文件所在路径，并判断路径是否合法
                String pruePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep)+1);
                File file = new File(pruePath);
                if(file.exists() || file.mkdir())
                    path = filePath;
            }

        }catch(Exception e){

        }


    }



    /**
     * 开始保存sql日志
     * @param sql
     */
    public static void start(String sql){
        SqlLogger.start(sql,null);
    }

    /**
     * 开始保存sql日志
     * @param sqls
     */
    public static void start(List sqls){
        for(int i=0;i<sqls.size();i++)
            SqlLogger.start(sqls.get(i).toString(),null);
    }

    /**
     * 开始保存sql日志
     * @param sqls
     * @param values
     */
    public static void start(List sqls,List values){
        for(int i=0;i<sqls.size();i++){
            SqlLogger.start(sqls.get(i).toString(),values.get(i));
        }
    }

    /**
     * 开始保存sql日志
     * @param sql
     * @param obj 预处理sql参数
     */
    public static void start(String sql,Object obj){

        try {
            //如果没有，不记录
            if(path==null)
                return;

            //检查sql，如果不符合条件(直接操作的表为不需要记录的表)，不执行
            if(!matchSql(sql))
                return;

            //记录sql到日志中
            Logger sqlKeeper = getSqlLogger(path);
            if(obj!=null)
                sql+=";values="+obj.toString();
            sqlKeeper.info(sql);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * 检查sql
     * @param sql
     * @return boolean
     */
    private static boolean matchSql(String sql){

        if(sqlPattern==null){
            //第一遍校验是否符合操作
            StringBuilder sub = new StringBuilder();
            sub.append("(?i)update\\s+([^\"]+)\\s+set\\s+");//(?i)不区分大小写；update 表名  set
            sub.append("|delete\\s+from\\s+([^\"]+)\\s+");// | 或  delete from  表名
            sub.append("|delete\\s+([^\"]+)\\s+");// | 或  delete from  表名
            sub.append("|drop\\s+table\\s+([^\"]+)\\s+");// | 或  drop table  表名
            sub.append("|insert\\s+into\\s+([^\"]+)\\s+");// | 或  insert into  表名
            sub.append("|truncate\\s+table\\s+([^\"]+)");// | 或  truncate table  表名
            sub.append("|select\\s+([^\"]+)\\s+into\\s+([^\"]+)\\s+from\\s+");// | 或  select * into 表名  from
            sqlPattern = Pattern.compile(sub.toString(), Pattern.DOTALL);
        }

        Matcher matcher = sqlPattern.matcher(sql);

        //符合操作条件后
        if(matcher.find()){
            if(sqlPatternTwo==null){
                //第二遍校验表名
                StringBuilder subTwo = new StringBuilder();
                subTwo.append("(?i)update\\s+("+tableFilter+")\\s+set\\s+");//(?i)不区分大小写；update 表名  set
                subTwo.append("|delete\\s+from\\s+("+tableFilter+")\\s+");// | 或  delete from  表名
                subTwo.append("|delete\\s+("+tableFilter+")\\s+");// | 或  delete from  表名
                subTwo.append("|drop\\s+table\\s+("+tableFilter+")\\s+");// | 或  drop table  表名
                subTwo.append("|insert\\s+into\\s+("+tableFilter+")\\s+");// | 或  insert into  表名
                subTwo.append("|truncate\\s+table\\s+("+tableFilter+")");// | 或  truncate table  表名
                subTwo.append("|select\\s+([^\"]+)\\s+into\\s+("+tableFilter+")\\s+from\\s+");// | 或  select * into 表名  from
                sqlPatternTwo = Pattern.compile(subTwo.toString(), Pattern.DOTALL);
            }
            Matcher matcherTwo = sqlPatternTwo.matcher(sql);
            // ! matcherTwo.find() 不包含tableFilter这些表名的sql记录，需要保存
            if(!matcherTwo.find())
                return true;
        }
        return false;
    }

    /**
     * 获取日志对象
     * @param path 日志文件路径
     * @return Logger
     * @throws IOException
     */
    private static Logger getSqlLogger(String path) throws IOException{
        //动态创建 Logger
        if(sql_logger==null){
            //sql_logger = Logger.getLogger("sqlkeeper");
            //设置格式：日期 | 线程名称 | message \n
            Layout layout = new PatternLayout(">>%d|%t|%m%n");
            //文件记录方式：当文件到达50兆时备份，最多存在10个备份，超过时删除最早的备份
		/*	RollingFileAppender appender = new SqlRollingFileAppender(layout, path);
			appender.setMaxBackupIndex(10);
			//文件大小读取参数配置，如果没有配置，默认为50MB
			String filesize = SystemConfig.getPropertyValue("sql_log_filesize");
			filesize = filesize==null || filesize.length()<1?"50MB":filesize;
			appender.setMaxFileSize(filesize);
			sql_logger.addAppender(appender);
			sql_logger.setAdditivity(false);
			sql_logger.setLevel(Level.INFO);*/
            sql_logger = Logger.getLogger(SqlLogger.class.getName());

        }

        return sql_logger;
    }
}