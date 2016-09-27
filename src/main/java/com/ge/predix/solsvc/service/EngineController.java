package com.ge.predix.solsvc.service;

import com.opencsv.CSVReader;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ge.predix.solsvc.util.*;

/**
 * Created by franspaco on 27/09/16.
 * "/home/franspaco/Documents/DATA/engines.csv"
 */
@RestController
public class EngineController {
    EngineReader ER;
    public EngineController() {
        super();
        ER = new EngineReader("DATA/engines.csv");
    }


    @SuppressWarnings("nls")
    @RequestMapping(value = "/engines/raw", method = RequestMethod.GET)
    public String enginesRaw(){
        return String.format(ER.getRaw().toString());
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/engines/summary", method = RequestMethod.GET)
    public String enginesSummary(){
        return String.format(ER.getEngineData(false).toString());
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/engines/status", method = RequestMethod.GET)
    public String enginesStatus(){
        return String.format(ER.getEngineData(true).toString());
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/engines/failures", method = RequestMethod.GET)
    public String enginesFailures(){
        return String.format(ER.getFailureData().toString());
    }
}
