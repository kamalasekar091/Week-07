import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ChainJobs extends Configured implements Tool {

 private static final String OUTPUT_PATH = "intermediate_output";

 @Override
 public int run(String[] args) throws Exception {
  /*
   * Job 1
   */
  Configuration conf = getConf();
  FileSystem fs = FileSystem.get(conf);
  Job job = new Job(conf, "Job1");
  job.setJarByClass(ChainJobs.class);

  job.setMapperClass(MaxTemperatureMapper.class);
  job.setReducerClass(MaxTemperatureReducer.class);

  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(IntWritable.class);

  job.setInputFormatClass(TextInputFormat.class);
  job.setOutputFormatClass(TextOutputFormat.class);

  TextInputFormat.addInputPath(job, new Path(args[0]));
  TextOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

  job.waitForCompletion(true);

  /*
   * Job 2
   */
  
  Job job2 = new Job(conf, "Job 2");
  job2.setJarByClass(ChainJobs.class);

  job2.setMapperClass(AvgTemperatureMapper.class);
  job2.setReducerClass(AvgTemperatureReducer.class);

  job2.setOutputKeyClass(Text.class);
  job2.setOutputValueClass(IntWritable.class);

  job2.setInputFormatClass(TextInputFormat.class);
  job2.setOutputFormatClass(TextOutputFormat.class);

  TextInputFormat.addInputPath(job2, new Path(OUTPUT_PATH));
  TextOutputFormat.setOutputPath(job2, new Path(args[1]));

  return job2.waitForCompletion(true) ? 0 : 1;
 }

 /**
  * Method Name: main Return type: none Purpose:Read the arguments from
  * command line and run the Job till completion
  * 
  */
 public static void main(String[] args) throws Exception {
  // TODO Auto-generated method stub
  if (args.length != 2) {
   System.err.println("Enter valid number of arguments <Inputdirectory>  <Outputlocation>");
   System.exit(0);
  }
  ToolRunner.run(new Configuration(), new ChainJobs(), args);
 }
}
