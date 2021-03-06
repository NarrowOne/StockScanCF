package functions;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import dao.DAO;
import daoImpl.RecognisedProduceDAO;
import utils.FunctionLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextDetector {
    private static final String TAG = "TextDetector";
    private byte[] imgData;
    HashMap<String, Object> currentProduce;


    public TextDetector(byte[] imgData){
        this.imgData = imgData;
    }

    public String performOCR(){

        String text = detectTextInImage(imgData);
        if(recognisedProduce(text)){
            return getProduceDetails(text);
        }

        return "\"error\" : \"Produce not recognised\",\n";
    }

    private String detectTextInImage(byte[] imgData){
        FunctionLog.addLog(TAG,"Begging text detection");
        List<AnnotateImageRequest> requests = new ArrayList<>();
        String text = "";
//        ByteString imgBytes = ByteString.copyFrom(imgData);

        Image img = Image.newBuilder().setContent(ByteString.copyFrom(imgData)).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try(ImageAnnotatorClient client = ImageAnnotatorClient.create()){
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            int loopRuns = 0;
            for(AnnotateImageResponse res : responses){
                if(res.hasError()){
                    text = TAG+" - Error: " + res.getError().getMessage();
                }
                    text = res.getFullTextAnnotation().getText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    private boolean recognisedProduce(String text) {
        if(text == null)
            return false;

        boolean name=false, code=false, producer=false;

        DAO dao = new RecognisedProduceDAO();
        List<HashMap<String, Object>> produceList = dao.getFullTable();

        for(int i = 0; i < produceList.size(); i++){
            HashMap<String, Object> produce = produceList.get(i);
            if(!name){
                if(text.contains((CharSequence) produce.get("name")))
                    name = true;
            }
            if(!code){
                if(text.contains((CharSequence) produce.get("product_code")))
                    code = true;
            }
            if(!producer){
                if(text.contains((CharSequence) produce.get("producer")))
                    producer= true;
            }

            if(name&&code&&producer){
                currentProduce = produce;
                return true;
            }
        }

        return false;
    }

    private String getProduceDetails(String text){
        String[] weightWords = {"weight"};
        String[] batchWords = {"batch", "lot"};
        String[] expWords = {"use by", "best before"};

        StringBuilder json = new StringBuilder();
        text = text.toLowerCase();

        if(currentProduce == null){
            FunctionLog.addLog(TAG, "current produce not set;");
            return null;
        }

        json.append("\"produce_details\" : {\n");
        json.append("\"name\" : \""+currentProduce.get("name")+"\",\n");
        json.append("\"code\" : \""+currentProduce.get("product_code")+"\",\n");
        json.append("\"producer\" : \""+currentProduce.get("producer")+"\",\n");
        String[] textBlock = text.split("\n");
        //find batch code
        for(String word : batchWords) {
            for (String line : textBlock) {
                if (line.contains(word)) {
                    json.append("\"batch\" : \"" + line + "\",\n");
                }
            }
        }
//        find weight
        for(String word : weightWords) {
            for(String line : textBlock) {
                if (line.contains(word)) {
                    json.append("\"weight\" : \"" + line + "\",\n");
                }
            }
        }
        //find expiry date
        for(String word : expWords){
            for(String line : textBlock) {
                if (line.contains(word)) {
                    json.append("\"expiration\" : \"" + line + "\"\n},\n");
                }
            }
        }

//        if(weight == null || batch == null || expiration == null){
//            FunctionLog.addLog(TAG, "Missing produce details");
//            return null;
//        }

        return json.toString();
    }

}
