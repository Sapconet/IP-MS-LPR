import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprException;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class OpenALPR_Test {
    private static Alpr alpr;
    public static void main(String[] args) {
        alpr = new Alpr("eu", System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/runtime_data/config/openalpr.conf", System.getProperty("user.dir").replace('\\', '/') + "/src/main/java/runtime_data");

        // Set top N candidates returned to 20
        alpr.setTopN(5);

        // Attempt to detect a region
        alpr.setDefaultRegion("de");
        AlprResults results = null;
        try {
            Path path = Paths.get("C:/Users/cwjvr/Documents/openalpr_64/samples/za-2.jpg");
            byte[] imagedata = Files.readAllBytes(path);
            results = alpr.recognize(imagedata);
        } catch (AlprException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        assert results != null;
        System.out.print(results.getImgWidth());
        System.out.format("  %-15s%-8s\n", "Plate Number", "Confidence");

        for (AlprPlateResult result : results.getPlates()) {
            for (AlprPlate plate : result.getTopNPlates()) {
                if (plate.isMatchesTemplate())
                    System.out.print("  * ");
                else
                    System.out.print("  - ");

                System.out.format("%-15s%-8f\n", plate.getCharacters(), plate.getOverallConfidence());

            }

        }          // Make sure to call this to release memory

        alpr.unload();
    }

    private static List<String> readFiles(String dir) {
        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Paths.get(dir))) {
            result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".jpg")).collect(Collectors.toList());
            
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
}
