// cc MaxTemperatureReducer Reducer for maximum temperature example
// vv MaxTemperatureReducer
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MinTemperatureReducer
  extends Reducer<Text, IntWritable, Text, Text> {
  
  @Override
  public void reduce(Text key, Iterable<IntWritable> values,
      Context context)
      throws IOException, InterruptedException {
    
    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;
    for (IntWritable value : values) {
      minValue = Math.min(minValue, value.get());
      maxValue = Math.max(maxValue, value.get());
    }

//     int maxValue = Integer.MIN_VALUE;
//    for (IntWritable value : values) {
//      maxValue = Math.max(maxValue, value.get());
///    }

   String valueforkey = String.valueOf(minValue)+"         "+String.valueOf(maxValue);

    context.write(key, new Text(valueforkey));
  }
}
// ^^ MaxTemperatureReducer
