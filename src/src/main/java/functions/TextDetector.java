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


    public TextDetector(byte[] imgData){
        this.imgData = imgData;
    }

    public String performOCR(){

        String[] text = detectTextInImage(imgData);
        if(recognisedProduce(text)){
            return "produce recognised";
        }

        return "produce not recognised";
    }

    private boolean recognisedProduce(String[] text) {
        if(text == null)
            return false;

        boolean name=false, code=false, producer=false;

        DAO dao = new RecognisedProduceDAO();
        List<HashMap<String, Object>> produceList = dao.getFullTable();

        for(String line : text){
            for(HashMap<String,Object>produce : produceList){
                if(!name){
                    if(line.contains((CharSequence) produce.get("name")))
                        name = true;
                }
                if(!code){
                    if(line.contains((CharSequence) produce.get("product_code")))
                        code = true;
                }
                if(!producer){
                    if(line.contains((CharSequence) produce.get("producer")))
                        producer= true;
                }
            }
        }

        return (name&&code&&producer);
    }

    private String[] detectTextInImage(byte[] imgData){
        FunctionLog.addLog(TAG,"Begging text detection");
        List<AnnotateImageRequest> requests = new ArrayList<>();
        List<String> text = new ArrayList<>();
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
                    text.add("Error: " + res.getError().getMessage());
                }

                for(EntityAnnotation annotation : res.getTextAnnotationsList()){
                    text.add(annotation.getDescription());
                    loopRuns++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toArray(new String[0]);
    }



}
