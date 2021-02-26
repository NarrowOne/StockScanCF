import functions.TextDetector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(JUnit4.class)
public class Integration {

    @Test
    public void ocrTest() throws Throwable {
        try{
            byte[] imgData = getFileData();
            String imgTxt = getImageText(imgData);
            assert imgTxt.equals("produce recognised");
            System.out.println(imgTxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImageText(byte[] imgBytes){
        TextDetector detector = new TextDetector(imgBytes);
        return detector.performOCR();
    }

    private byte[] getFileData() throws IOException {
        String path = "C:\\Users\\comki\\Desktop\\College\\Year 4\\FYP\\Image recognition\\Product Samples\\ground_almonds.jpg";

        return encodeFileToBase64Binary(new File(path));
    }

    private byte[] encodeFileToBase64Binary(File f) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(f);
        byte[] bytes = new byte[(int)f.length()];
        assert fileInputStream.read(bytes) == 4642744;
        return bytes;
    }
}
