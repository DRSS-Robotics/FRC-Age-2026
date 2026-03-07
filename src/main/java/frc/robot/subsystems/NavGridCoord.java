package frc.robot.subsystems;

import com.fasterxml.jackson.*;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

import javax.xml.crypto.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.JSONTokener;
import frc.robot.subsystems.NavGrid;


public class NavGridCoord {

  public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        NavGrid stgrid = new NavGrid();
        String filePath = "src/main/deploy/pathplanner/navgrid.json";

        // File file = Paths.get(filePath).toFile();
        //     System.out.println(file);

        // boolean[][] plane = new boolean[(int) 16.54][(int) 8.07];
      try(FileReader reader = new FileReader(filePath)) {
            // FileReader reader = new FileReader(filePath);
            // JSONTokener tokener = new JSONTokener(reader);
            // JSONObject jsonObject = new JSONObject();
            StringBuilder StringB = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                StringB.append((char) character);
            }
            String jsonContent = StringB.toString();

            JSONObject jsonObject = new JSONObject(jsonContent);

            //Get the "activeStates" array as a JSONArray
            JSONArray jsonArray = jsonObject.getJSONArray("grid");

            //Convert the JSONArray to a Java boolean array or List<Boolean>
            boolean[] booleanArray = new boolean[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); i++) {
                booleanArray[i] = jsonArray.getBoolean(i);
            }
        
            //Print the resulting boolean array
            System.out.println("Boolean Array: ");
            for (boolean b : booleanArray) {
                System.out.println(b);
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) {
//    // String NavGrid;
//      ObjectMapper objectMapper = new ObjectMapper();
//      File jsonFile = new File("NavGrid.json");
//      JsonNode rootNode = objectMapper.readTree(jsonFile);
//      String content = "";
//  try {
//     content = Files.readString(Paths.get("data.json"));
//     }

//  } catch (IOException e) {
//     e.printStackTrace();
//  }
// JSONObject jsonObject = new JSONObject(content);
// JSONArray jsonArray = new JSONArray(content);
}

