// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AvgTemperatureMapper
  extends Mapper<LongWritable, Text, Text, IntWritable> {

  private static final int MISSING = 9999;
  
  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    
    String line = value.toString();
    String Station_ID = line.substring(0, 12);
    String Date=line.substring(17,21);
    String mapKey=Station_ID+" "+Date;
    int airTemperature;
    if(line.substring(22) != " "){
      airTemperature = Integer.parseInt((line.substring(22)).trim());
    
      context.write(new Text(mapKey), new IntWritable(airTemperature));
  }
  }
}
// ^^ MaxTemperatureMapper
