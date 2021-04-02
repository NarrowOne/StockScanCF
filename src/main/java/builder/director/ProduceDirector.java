package builder.director;

import builder.ProduceBuilder;
import builder.builderImpl.ProduceBuild;
import models.Produce;

public class ProduceDirector {
    private ProduceBuilder builder;

    public ProduceDirector() {
    }
    public ProduceDirector(ProduceBuilder builder) {
        this.builder = builder;
    }

    public Produce getProduce(){
        return builder.getProduce();
    }

    public void setBuilder(ProduceBuilder builder){
        this.builder = builder;
    }

    public void buildProduce(){
        builder.buildID();
        builder.buildName();
        builder.buildProdCode();
        builder.buildProducer();
        builder.buildBatch();
        builder.buildWeight();
        builder.buildExpiry();
    }
}
