package br.com.lucio.service02.model.dynamo;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class ProductEventKey {

    public ProductEventKey() {
    }

    private String pk;

    private String sk;

    @DynamoDBHashKey(attributeName = "pk")
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDBRangeKey(attributeName = "sk")
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    @Override
    public String toString() {
        return "ProductEventKey{" +
                "pk='" + pk + '\'' +
                ", sk='" + sk + '\'' +
                '}';
    }
}
