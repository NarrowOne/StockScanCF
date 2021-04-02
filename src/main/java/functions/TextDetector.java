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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextDetector {
    private static final Logger logger = Logger.getLogger(TextDetector.class.getName());
    private static final String TAG = "TextDetector";

    public TextDetector(){
    }

    public static String performOCR(byte[] imgData){
        String text = detectTextInImage(imgData)/*.toLowerCase(Locale.ROOT)*/;
        HashMap<String, Object> currentProduce = recognisedProduce(text, new HashMap<>());
        if(!currentProduce.isEmpty()){
            return getProduceDetails(text, currentProduce);
        }

        return "\"error\" : \"Produce not recognised:\n"+"\",\n";
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
        Pattern pattern;
        Matcher matcher;

        boolean name=false, code=false, producer=false;

        DAO dao = new RecognisedProduceDAO();
        List<HashMap<String, Object>> produceList = dao.getFullTable();
        logger.warning(text);
        logger.warning(produceList.toString());

        for (HashMap<String, Object> produce : produceList) {
            if (!name) {
                pattern = Pattern.compile((String) produce.get("name"), Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(text);
                if (matcher.find()) {
                    logger.warning("name match");
                    name = true;
                }
            }
            if (!code) {
                pattern = Pattern.compile((String) produce.get("product_code"), Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(text);
                if (matcher.find()) {
                    logger.warning("code match");
                    code = true;
                }
            }
            logger.warning(String.valueOf(text.contains((CharSequence) produce.get("producer"))));
            if (!producer) {
                pattern = Pattern.compile((String) produce.get("producer"), Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(text);
                if (matcher.find()) {
                    logger.warning("producer match");
                    producer = true;
                }
            }

            if (name && code && producer) {
                logger.warning("full match");
                currentProduce = produce;
                return currentProduce;
            } else {
                name = false;
                code = false;
                producer = false;
            }
        }

        return currentProduce;
    }

    private static String getProduceDetails(String text, HashMap<String, Object> currentProduce){
        ProduceDirector director = new ProduceDirector(new ProduceQuickBuild(currentProduce, text));
        director.buildProduce();

        return "\"produce_details\" : "+director.getProduce().getJsonString()+",\n";
    }

}
