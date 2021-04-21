package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Produce {
    private String id;
    private String name;
    private String product_code;
    private String producer;
    private String batch;
    private String weight;
    private Date expiry;
    private String[] tags;

    public Produce(){

    }

    public Produce(String name, String product_code, String producer, String batch, String weight, String expiry) throws ParseException {
        this.name = name;
        this.product_code = product_code;
        this.producer = producer;
        this.batch = batch;
        this.weight = weight;
        this.expiry = new SimpleDateFormat("yyyy/MM/dd").parse(expiry);
//        this.expiry = expiry;
    }

    public Produce(String id, String name, String product_code, String producer, String batch, String weight, String expiry) throws ParseException {
        this.id = id;
        this.name = name;
        this.product_code = product_code;
        this.producer = producer;
        this.batch = batch;
        this.weight = weight;
        this.expiry = new SimpleDateFormat("yyyy/MM/dd").parse(expiry);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_code() {
        return product_code;
    }
    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProducer() {
        return producer;
    }
    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getBatch() {
        return batch;
    }
    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getExpiry() {
        return new SimpleDateFormat("yyyy/MM/dd").format(expiry);
    }
    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
    public void setExpiry(String expiry) throws ParseException {
        this.expiry = new SimpleDateFormat("dd/MM/yyyy").parse(expiry);
    }

    public String getJsonString(){
        return  "{\n" +
                                        "\"name\" : \"" +          name+"\",\n"+
                                        "\"product_code\" : \"" + product_code +"\",\n"+
                                        "\"producer\" : \"" +      producer+"\",\n"+
                                        "\"batch\" : \"" +         batch+"\",\n"+
                                        "\"weight\" : \"" +        weight+"\",\n"+
                                        "\"expiry\" : \""+         new SimpleDateFormat("yyyy/MM/dd").format(expiry)+"\"\n"+
                                      "}";
    }
}
