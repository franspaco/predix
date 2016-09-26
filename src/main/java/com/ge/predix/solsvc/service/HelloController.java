package com.ge.predix.solsvc.service;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.*;

/**
 * An example of creating a Rest api using Spring Annotations @RestController.
 * 
 * 
 * 
 * @author predix
 */
@RestController
public class HelloController
{

    /**
     * 
     */
    public HelloController()
    {
        super();
    }

    /**
     * Sample Endpoint which returns a Welcome Message
     * 
     * @param echo
     *            - the string to echo back
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String index(@RequestParam(value = "echo", defaultValue = "echo this text") String echo)
    {
        return "Greetings from Predix Spring Boot! echo=" + echo + " " + (new Date());
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health()
    {
        return String.format("{\"status\":\"up\", \"date\": \" " + new Date() + "\"}");
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public String company(){
        JSONObject temp = new JSONObject();
        temp.put("compañía", "Always Up Analytics");
        temp.accumulate("miembros", "Ameyalli Gomez");
        temp.accumulate("miembros", "Francisco Huelsz");
        temp.accumulate("miembros", "Bardo Ojeda");
        temp.accumulate("miembros", "Maria Antonia Orozco");

        return String.format(temp.toString());
    }

}
