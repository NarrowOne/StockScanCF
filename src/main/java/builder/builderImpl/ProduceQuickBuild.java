package builder.builderImpl;

import builder.ProduceBuilder;
import models.Produce;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProduceQuickBuild implements ProduceBuilder {
    private final String[] textBlock;
    private final Produce produce;
    private final HashMap<String, Object> details;

    public ProduceQuickBuild(HashMap<String, Object> details, String text) {
        this.produce = new Produce();
        this.details = details;
        this.textBlock = text.toLowerCase(Locale.ROOT).split("\n");
    }

    @Override
    public void buildName() {
        produce.setName((String) details.get("name"));
    }

    @Override
    public void buildProdCode() {
        produce.setProductCode((String) details.get("product_code"));

    }

    @Override
    public void buildProducer() {
        produce.setProducer((String) details.get("producer"));
    }

    @Override
    public void buildBatch() {
        String pattern = "batch|lot";
        String[] batchWords = {"batch", "lot"};
        String batchLine = getLine(pattern);

        produce.setBatch(trimLine(batchLine));
    }

    @Override
    public void buildWeight() {
        String pattern = "([0-9]+)(\\,{1}[0-9]{3})?(\\.|\\,{1}[0-9]*)?(kg|g)";
        String[] weightWords = {"weight"};


        String weightLine = getLine(pattern);
        weightLine = trimLine(weightLine);
        if(weightLine.contains(","))
            weightLine = weightLine.replace(",", "");

        if(weightLine.contains("kg")){
            weightLine = weightLine.replace("kg", "");
            if(weightLine.contains(".")){
                String[] digits = weightLine.split("\\.");

                int kg = Integer.parseInt(digits[0])*1000;
                int g = Integer.parseInt(digits[1]);
                weightLine = String.valueOf(kg+g);
            }else{
                int kg = Integer.parseInt(weightLine) * 1000;
                weightLine = String.valueOf(kg);
            }
        }else {
            weightLine = weightLine.replace("g", "");
        }

        produce.setWeight(weightLine);
    }

    @Override
    public void buildExpiry() {
        String pattern = "([0-3]*[0-9]{1}){1}([\\.|\\/|\\-|\\s]){1}([0-1]*[0-9]{1}){1}([\\.|\\/|\\-|\\s]){1}([0-9]{2,4}){1}";
        String[] expWords = {"use by", "best before"};
        String expLine = getLine(pattern);
        expLine = trimLine(expLine);

        if(expLine.contains("."))
            expLine = expLine.replace(".", "/");
        else if(expLine.contains("-"))
            expLine = expLine.replace("-", "/");
        else if(expLine.contains(" "))
            expLine = expLine.replace(" ", "/");

        produce.setExpiry(expLine);
    }


    //Get line containing attribute
//    private String getLine(String[] wordList){
//        for(String word : wordList) {
//            for (String line : textBlock){
//                if (line.contains(word)) {
//                    return line;
//                }
//            }
//        }
//
//        return "Error: Could not find "+wordList[0]+" value";
//    }

    private String getLine(String regEx){
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        for (String line : textBlock){
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                return line;
            }
        }

        return "Error: Could not find value";
    }

    private String trimLine(String line){
        if(line.contains(":")){
            int index = line.indexOf(": ");
            line = line.substring(index+2);
        }

        return line;
    }

    @Override
    public Produce getProduce(){
        return produce;
    }

}
