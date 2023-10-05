package pan.artem.conspecter.service;

import java.io.IOException;
import java.text.ParseException;

public interface ConspectInitializer {

    void initialize(int conspectId, String path) throws IOException, ParseException, InterruptedException;
}
