package frc.robot.subsystems;

import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import javax.xml.crypto.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import javax.json.JsonBuilderFactory;

public class NavGrid {

    public boolean[] grid;

    public boolean[] getGrid() {
        //Get the value of a box on the grid
        return grid;
    }
    public void setGrid(boolean[] grid) {
        this.grid = grid;
    }

}

