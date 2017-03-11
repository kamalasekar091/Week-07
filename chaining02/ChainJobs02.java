
/*Reference http://stackoverflow.com/questions/6840922/hadoop-mapreduce-driver-for-chaining-mappers-within-a-mapreduce-job/10470437#10470437 */
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class ChainJobs02 extends Configured implements Tool {

/*Max Temperature Mapper*/

 public static class MaxTemperatureMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

  private static final int MISSING = 9999;
  
  
  //@Override
  public void map(LongWritable key, Text value, 
                OutputCollector<Text, IntWritable> output, 
                Reporter reporter) throws IOException {
    
    String line = value.toString();
    String Station_ID = line.substring(4, 10);
    String Station_identifier= line.substring(10,15);
    String Date=line.substring(15,23);
    String mapKey=Station_ID+"-"+Station_identifier+" "+Date;
    int airTemperature;
    if (line.charAt(87) == '+') { // parseInt doesn't like leading plus signs
      airTemperature = Integer.parseInt(line.substring(88, 92));
    } else {
      airTemperature = Integer.parseInt(line.substring(87, 92));
    }
    String quality = line.substring(92, 93);
    if (airTemperature != MISSING && quality.matches("[01459]")) {
      output.collect(new Text(mapKey), new IntWritable(airTemperature));
    }
  }
}


/*Max Temperature Reducer*/
  public static class MaxTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
  
  
 // @Override
  public void reduce(Text key, Iterator<IntWritable> values,
                OutputCollector<Text, IntWritable> output, 
                Reporter reporter) throws IOException {
    
    int maxValue = Integer.MIN_VALUE;
    for (IntWritable value : values) {
      maxValue = Math.max(maxValue, value.get());
    }
    output.collect(key, new IntWritable(maxValue));
  }
}


/*Second Mapper Average Mapper*/
  public static class AvgTemperatureMapper extends MapReduceBase implements Mapper<Text, IntWritable, Text, IntWritable> {

  private static final int MISSING = 9999;
  
  
 // @Override
  public void map(LongWritable key, Text value, 
                OutputCollector<Text, IntWritable> output, 
                Reporter reporter) throws IOException {
    
    
    String line = value.toString();
    String Station_ID = line.substring(0, 12);
    String Date=line.substring(17,21);
    String mapKey=Station_ID+" "+Date;
    int airTemperature;
    if(line.substring(22) != " "){
      airTemperature = Integer.parseInt((line.substring(22)).trim());
    
      output.collect(new Text(mapKey), new IntWritable(airTemperature));
  }
  }
}


/*Second Reducer*/

public static class AvgTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
  
  
 // @Override
  public void reduce(Text key, Iterator<IntWritable> values,
                OutputCollector<Text, IntWritable> output, 
                Reporter reporter) throws IOException {
    
    int maxValue = Integer.MIN_VALUE;
    for (IntWritable value : values) {
      maxValue = Math.max(maxValue, value.get());
    }
    output.collect(key, new IntWritable(maxValue));
  }
}




     public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(), ChainJobs02.class);
        conf.setJobName("Max Mean Temperature");

        if (args.length != 2) {
            System.out.println("ERROR: Wrong number of parameters: " +
                    args.length + " instead of 2.");
            return printUsage();
        }
        FileInputFormat.setInputPaths(conf, args[0]);
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        JobConf mapAConf = new JobConf(false);
        ChainMapper.addMapper(conf, MaxTemperatureMapper.class, LongWritable.class, Text.class, Text.class, IntWritable.class, true, mapAConf);

        JobConf reduceConf = new JobConf(false);
        ChainReducer.setReducer(conf, MaxTemperatureReducer.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, reduceConf);

        JobConf mapBConf = new JobConf(false);
        ChainMapper.addMapper(conf, AvgTemperatureMapper.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, mapBConf);

        JobConf reduceBConf = new JobConf(false);
        ChainReducer.setReducer(conf, AvgTemperatureReducer.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, reduceBConf);

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ChainJobs02(), args);
        System.exit(res);
    }

    static int printUsage() {
        System.out.println("Average <input> <output>");
        ToolRunner.printGenericCommandUsage(System.out);
        return -1;
    }
}
