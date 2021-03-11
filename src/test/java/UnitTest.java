import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import functions.FunctionMain;
import functions.TextDetector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class UnitTest {
    @Mock private HttpRequest request;
    @Mock private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;

    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "FunctionMain";
    private static final Gson gson = new Gson();
    private TextDetector detector;
    private static PrintWriter writer;
    private String error;

    @Before
    public void beforeTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        // use an empty string as the default request content
        BufferedReader reader = new BufferedReader(new StringReader(""));
        when(request.getReader()).thenReturn(reader);

        responseOut = new StringWriter();
        writerOut = new BufferedWriter(responseOut);
        when(response.getWriter()).thenReturn(writerOut);
    }

    @Test
    public void helloHttp_noParamsGet() throws IOException {
        new FunctionMain().service(request, response);

        writerOut.flush();
        assertThat(responseOut.toString()).isEqualTo("Hello world!");
    }
}
