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
//import javax.json.JsonBuilderFactory;
import frc.robot.subsystems.NavGrid;
import org.json.JSONArray;
import org.json.Tokener;

public class NavGridCoord {

  public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String filePath = "src/main/deploy/pathplanner/navgrid.json";

        // File file = Paths.get(filePath).toFile();
        //     System.out.println(file);

        // boolean[][] plane = new boolean[(int) 16.54][(int) 8.07];
      try {
            FileReader reader = new FileReader(filePath);
            JSONTokener Tokener = new JSONTokener(reader);
    
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

