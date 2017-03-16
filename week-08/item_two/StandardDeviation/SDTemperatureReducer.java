
// cc MaxTemperatureReducer Reducer for maximum temperature example
// vv MaxTemperatureReducer
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SDTemperatureReducer
  extends Reducer<Text, IntWritable, Text, IntWritable> {

  @Override
  public void reduce(Text key, Iterable<IntWritable> values,
      Context context)
      throws IOException, InterruptedException {

    double sdValue;

        List<Integer> record = new ArrayList<Integer>();

    for (IntWritable value : values) {
      record.add(value.get());
    }

        sdValue=stdev(record);

    context.write(key, new IntWritable((int)sdValue));
  }

  public static double stdev(List<Integer> list){
    double sum = 0.0;
    double mean = 0.0;
    double num=0.0;
    double numi = 0.0;
    double deno = 0.0;

    for (int i : list) {
        sum+=i;
    }

    mean = sum/(list.size());

    for (int i : list) {
         numi = Math.pow(((double) i - mean), 2);
         num+=numi;
    }

    return Math.sqrt(num/(list.size()));
}

}
