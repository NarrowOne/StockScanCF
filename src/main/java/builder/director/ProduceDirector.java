package builder.director;

import builder.ProduceBuilder;
import models.Produce;

public class ProduceDirector {
    private final ProduceBuilder builder;

    public ProduceDirector(ProduceBuilder builder) {
        this.builder = builder;
    }

    public Produce getProduce(){
        return builder.getProduce();
    }

    public void buildProduce(){
        builder.buildName();
        builder.buildProdCode();
        builder.buildProducer();
        builder.buildBatch();
        builder.buildWeight();
        builder.buildExpiry();
    }
}
