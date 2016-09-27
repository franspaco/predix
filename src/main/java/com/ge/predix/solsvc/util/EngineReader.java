package com.ge.predix.solsvc.util;

import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franspaco on 27/09/16.
 */
public class EngineReader {
    String engineFile;
    JSONObject summary = null;

    public EngineReader(String engineFile){
        this.engineFile = engineFile;
    }

    public List readFile(){
        List lines;
        try {
            System.out.println("Reading.");
            CSVReader reader = new CSVReader(new FileReader(engineFile), ',');
            lines = reader.readAll();
            System.out.println("Read.");
            lines.remove(0);

            //Erase NAs
            //Bad idea because cancellations are NAs
            /*
            for(int i = 0; i < lines.size(); i++){
                String [] line = (String[])lines.get(i);
                for(int j = 0; j < line.length;j++ ){
                    if(line[j] == "NA"){
                        lines.remove(i);
                        i--;
                    }
                }
            }
            */
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getRaw(){
        List lines = readFile();
        JSONObject out = new JSONObject();
        String [] line;

        for(int i = 0; i < lines.size(); i++){
            line = (String[])lines.get(i);
            JSONObject temp = new JSONObject();
            temp.put("flightID",              line[0]);
            temp.put("EngineTemperature",     line[1]);
            temp.put("EngineSerial",          line[2]);
            temp.put("EngineRoute",           line[3]);
            temp.put("EngineTemperatureTime", line[4]);
            temp.put("Cancellation",          line[5]);
            out.accumulate("entry", temp);
        }

        return out;
    }

    public JSONObject getFailureData(){
        JSONObject out = new JSONObject();
        JSONObject all = getEngineData(false);

        System.out.println(all.get("engines"));

        JSONArray engines = new JSONArray(all.get("engines").toString());
        for(int i = 0; i < engines.length(); i++){
            JSONObject temp = engines.getJSONObject(i);
            if(temp.getBoolean("failed")){
                out.accumulate("engines", temp);
            }
        }

        return out;
    }

    public JSONObject getEngineData(boolean status){
        //Status true for current status aka resets time when a cancellation takes place
        // and part is replaced

        //THIS WILL CAUSE PROBLEMS IF THE FILE IS CHANGES AND THE SERVER NOT RESTARTED
        /*
        if(summary != null)
            return summary;*/

        List lines = readFile();

        ArrayList<String> engines = new ArrayList<String>();
        ArrayList<Integer> times = new ArrayList<Integer>();
        ArrayList<Integer> cycles = new ArrayList<Integer>();
        ArrayList<Integer> routes = new ArrayList<Integer>();
        ArrayList<Boolean> failedYet = new ArrayList<Boolean>();


        for(int i = 0; i < lines.size(); i++){
            String [] line = (String[])lines.get(i);
            int indx = engines.indexOf(line[2]);
            int time = 0;
            int route = -1;
            double temp = 0;
            try{
                time = Integer.parseInt(line[4]);
                temp = Double.parseDouble(line[1]);
                route = Integer.parseInt(line[3]);
            }catch(Exception ex){

            }

            if(indx == -1){
                engines.add(line[2]);
                times.add(0);
                cycles.add(0);
                routes.add(route);
                failedYet.add(false);
                indx = engines.size() -1;
            }

            if(line[5].indexOf("1") != -1){
                failedYet.set(indx, Boolean.TRUE);
                if(status) {
                    times.set(indx, 0);
                    cycles.set(indx, 0);
                }
            }
            if(status){
                if (temp >= 263.0) {
                    times.set(indx, times.get(indx) + time);
                }
            }else {
                if (temp >= 263.0 && !failedYet.get(indx)) {
                    times.set(indx, times.get(indx) + time);
                }
            }

            cycles.set(indx, cycles.get(indx) + 1);
        }

        JSONObject out = new JSONObject();

        int mpc = 0;

        int tot = 0;
        for(int i = 0; i < engines.size(); i++){
            JSONObject engine = new JSONObject();
            tot += cycles.get(i);
            engine.put("engineSerial", engines.get(i));
            engine.put("route", routes.get(i));
            engine.put("time", times.get(i));
            engine.put("cycles", cycles.get(i));
            engine.put("failed", failedYet.get(i));
            engine.put("mpc", (double)times.get(i)/(double)cycles.get(i));
            out.accumulate("engines", engine);
        }

        summary = out;
        return out;
    }


}
