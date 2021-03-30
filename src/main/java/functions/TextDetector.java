package functions;

import builder.builderImpl.ProduceQuickBuild;
import builder.director.ProduceDirector;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import dao.DAO;
import dao.daoImpl.RecognisedProduceDAO;
import utils.FunctionLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TextDetector {
    private static final String TAG = "TextDetector";
//    private byte[] imgData;
//    HashMap<String, Object> currentProduce;

    public TextDetector(){

    }

//    public TextDetector(byte[] imgData){
//        this.imgData = imgData;
//    }

    public static String performOCR(byte[] imgData){

        String text = detectTextInImage(imgData)/*.toLowerCase(Locale.ROOT)*/;
        HashMap<String, Object> currentProduce = recognisedProduce(text, new HashMap<>());
        if(!currentProduce.isEmpty()){
            return getProduceDetails(text, currentProduce);
        }

        return "\"error\" : \"Produce not recognised\",\n";
    }

    private static String detectTextInImage(byte[] imgData){
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

    private static HashMap<String, Object> recognisedProduce(String text, HashMap<String, Object> currentProduce) {
        if(text == null)
            return currentProduce;

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
                return currentProduce;
            }else{
                name =false;
                code = false;
                producer = false;
            }
        }

        return currentProduce;
    }

    private static String getProduceDetails(String text, HashMap<String, Object> currentProduce){
        ProduceDirector director = new ProduceDirector(new ProduceQuickBuild(currentProduce, text));
        director.buildProduce();

        return director.getProduce().getJsonString();
    }

}
