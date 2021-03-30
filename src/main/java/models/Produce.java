package models;

public class Produce {
    private String name;
    private String productCode;
    private String producer;
    private String batch;
    private String weight;
    private String expiry;

    public Produce(){

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
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
        return expiry;
    }
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getJsonString(){
        return  "\"produce_details\" : {\n" +
                                        "\"name\" : \"" +          name+"\",\n"+
                                        "\"product_code\" : \"" +  productCode+"\",\n"+
                                        "\"producer\" : \"" +      producer+"\",\n"+
                                        "\"batch\" : \"" +         batch+"\",\n"+
                                        "\"weight\" : \"" +        weight+"\",\n"+
                                        "\"expiry\" : \""+         expiry+"\",\n"+
                                      "},\n";
    }
}
