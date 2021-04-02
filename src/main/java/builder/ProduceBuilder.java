package builder;

import models.Produce;

public interface ProduceBuilder {
    void buildID();
    void buildName();
    void buildProdCode();
    void buildProducer();
    void buildBatch();
    void buildWeight();
    void buildExpiry();
    Produce getProduce();

}
