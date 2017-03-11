import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class ChainMapperDriver {

    public static void main(String[] args) throws Exception {
        
		
		
		Configuration config = new Configuration();
        String[] otherArgs = new GenericOptionsParser(config, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.print("Useage: wordcount <in> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance();
                             
		Configuration MaxMapConfig = new Configuration(false);
        ChainMapper.addMapper(job,MaxTemperatureMapper.class, LongWritable.class, Text.class, Text.class, IntWritable.class, MaxMapConfig);
		
		Configuration MaxreduceConf = new Configuration(false);
        ChainReducer.setReducer(job, MaxTemperatureReducer.class, Text.class, IntWritable.class, Text.class, IntWritable.class,MaxreduceConf);

//	job.waitForCompletion(true);

														  
        Configuration AvgMapConfig = new Configuration(false);
        ChainMapper.addMapper(job, AvgTemperatureMapper.class, Text.class, IntWritable.class, Text.class, IntWritable.class, AvgMapConfig);
		
																  
        job.setJarByClass(ChainMapperDriver.class);
        job.setReducerClass(AvgTemperatureReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}
