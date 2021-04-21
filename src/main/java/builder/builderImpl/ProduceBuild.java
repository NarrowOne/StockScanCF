package builder.builderImpl;

import builder.ProduceBuilder;
import models.Produce;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class ProduceBuild implements ProduceBuilder {
    private final Produce produce;
    private final HashMap<String, Object> details;

    public ProduceBuild(HashMap<String, Object> details) {
        this.produce = new Produce();
        this.details = details;
    }


    @Override
    public void buildID() {
        produce.setId(String.valueOf(details.get("id")));
    }

    @Override
    public void buildName() {
        produce.setName((String) details.get("name"));
    }

    @Override
    public void buildProdCode() {
        produce.setProduct_code((String) details.get("product_code"));

    }

    @Override
    public void buildProducer() {
        produce.setProducer((String) details.get("producer"));

    }

    @Override
    public void buildBatch() {
        produce.setBatch((String) details.get("batch"));

    }

    @Override
    public void buildWeight() {
        produce.setWeight(String.valueOf(details.get("weight")));

    }

    @Override
    public void buildExpiry() {
        produce.setExpiry((Date) details.get("expiration"));

    }

    @Override
    public Produce getProduce() {
        return produce;
    }


}
