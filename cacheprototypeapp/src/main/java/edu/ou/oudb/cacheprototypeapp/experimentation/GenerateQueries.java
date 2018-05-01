package edu.ou.oudb.cacheprototypeapp.experimentation;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONLoader;
import edu.ou.oudb.cacheprototypelibrary.utils.JSONParser;

//Created by Zach Arani, 2018/04/30
//Helper class for experimentation that's used to generate test queries. Very similar to CloudDataAccessProvider

public class GenerateQueries {

    String url = "http://10.204.69.210:8080/CloudWebService/rest/result?query=";

    ArrayList<String> operators;
    public GenerateQueries(int size)
    {
        operators = new ArrayList<String>();
        operators.add("<"); operators.add(">"); operators.add("<="); operators.add(">="); operators.add("=");
    }

    public ArrayList<String> generate(int size)
    {

        ArrayList<ArrayList<String>> possibilities = getPossibilities();
        ArrayList<String> queries = new ArrayList<>();
        for(int i = 0; i<size; i++)
        {
            int currAttribute = (int)(Math.random() * 8); //Randomly generate which attribute to use this query
            switch(currAttribute)
            {
                case 0:
                    queries.add(generateNum(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 1:
                    queries.add(generateString(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 2:
                    queries.add(generateString(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 3:
                    queries.add(generateString(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 4:
                    queries.add(generateString(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 5:
                    queries.add(generateString(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 6:
                    queries.add(generateDate(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                case 7:
                    queries.add(generateNum(possibilities.get(0).get(currAttribute), possibilities.get(currAttribute+1)));
                    break;
                default:
                    queries.add(generateNum(possibilities.get(0).get(0), possibilities.get(1)));
                    break;
            }

        }
        return queries;
    }

    /**
     * Used for generating data based on known values
     * @return an Arraylist of Arraylist where the first arrayList is the name for each attribute and every other list is every value stored for an attribute in the table
     */
    public ArrayList<ArrayList<String>> getPossibilities() {
        List<List<String>> attributes = null;
        List<List<String>> attributePossibilities = null; //Every single entry for an attribute in table
        ArrayList<ArrayList<String>> possibilities = new ArrayList<>();
        possibilities.add(new ArrayList<String>()); //Have to have a new ArrayList for the list of attributes
        try {
            attributes = process("show columns in patients");
        } catch(JSONParserException | DownloadDataException e) {}

        for(int i = 0; i<attributes.size(); i++) {
            possibilities.add(new ArrayList<String>()); //Have to have a new ArrayList for the attribute to store values
            possibilities.get(0).add(attributes.get(i).get(0)); //Adds the attribute to the first array in possibilities (the list of attributes)
        }
        for(int i = 1; i<attributes.size()+1; i++)
        {
            try {
                attributePossibilities = process("SELECT " + possibilities.get(0).get(i-1) + " FROM patients"); //Gets every entry for an attribute in the table
            } catch(JSONParserException | DownloadDataException e){}

            for(List<String> value : attributePossibilities)
            {
                possibilities.get(i).add(value.get(0)); //Add each value into that attribute's list
            }
        }

        return possibilities;
    }

    public String generateNum(String attribute, ArrayList<String> possibilities)
    {

        return "SELECT * from patients WHERE " + attribute + " " + operators.get((int)(Math.random() * operators.size())) + " " + possibilities.get((int)(Math.random() * (possibilities.size())));
    }

    public String generateString(String attribute, ArrayList<String> possibilities)
    {
        return "SELECT * from patients WHERE " + attribute + " = '" + possibilities.get((int)(Math.random() * (possibilities.size()))) + "'";
    }

    public String generateDate(String attribute, ArrayList<String> possibilities)
    {
        int date_or_time = (int)(Math.random() * 2 ); //0 for date, 1 for time

        if(date_or_time == 0)
        {
            return "SELECT * from patients WHERE substr(" + attribute +  ",0,10) " + operators.get((int)(Math.random() * operators.size())) + " '" + possibilities.get((int)(Math.random() * (possibilities.size()))).substring(0,10)+"'";
        }
        else
        {
            return "SELECT * from patients WHERE substr(" + attribute + ",12) " + operators.get((int)(Math.random() * operators.size())) + " '" + possibilities.get((int)(Math.random() * (possibilities.size()))).substring(11) + "'";
        }
    }
    public List<List<String>> process(String query) throws JSONParserException, DownloadDataException {

        QuerySegment querySegment = null;
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(query
                .replace(" ", "%20")
                .replace("<", "%3C")
                .replace(">","%3E")
                .replace(";", ""));
        String urlString = sb.toString();

        InputStream jsonStream = JSONLoader.getJSONInputStreamFromUrl(urlString);

        JSONParser.QueryResult result;
        try {
            result = JSONParser.parseQueryResult(jsonStream);
            if (result == null)
            {
                throw new JSONParserException();
            }
        } catch (IOException e) {
            throw new JSONParserException(e.getMessage());
        }
        querySegment = new QuerySegment(result.tuples);
        return querySegment.getTuples();

    }


}
