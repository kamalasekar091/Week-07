// cc MaxTemperatureMapper Mapper for maximum temperature example
// vv MaxTemperatureMapper
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AvgTemperatureMapper
  extends Mapper<Text, IntWritable, Text, IntWritable> {

  private static final int MISSING = 9999;
  
  @Override
  public void map(Text key, IntWritable value, Context context)
      throws IOException, InterruptedException {
    
    String line = key.toString();
    String Station_ID = line.substring(0, 12);
    String Date=line.substring(17,21);
    String mapKey=Station_ID+" "+Date;
   // int airTemperature;
    //if(line.substring(22) != " "){
   //   airTemperature = Integer.parseInt((line.substring(22)).trim());
    
      context.write(new Text(mapKey), value);
 // }
  }
}
// ^^ MaxTemperatureMapper
