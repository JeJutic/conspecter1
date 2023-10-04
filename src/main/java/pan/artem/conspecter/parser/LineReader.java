package pan.artem.conspecter.parser;

import java.io.IOException;
import java.util.Optional;

public interface LineReader {

    Optional<String> readLine() throws IOException;
}
