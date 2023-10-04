package pan.artem.conspecter.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class ShortLineReader implements LineReader {

    private final Reader in;

    public ShortLineReader(Reader in) {
        this.in = in;
    }

    @Override
    public Optional<String> readLine() throws IOException {   // FIXME: quite not effective - too many method calls instead of a buffer
        StringBuilder sb = new StringBuilder();
        int went = 0;
        int r = in.read();
        if (r < 0) {
            return Optional.empty();
        }
        for (; went < ParsingProperties.getMaxLineLength() && r >= 0; went++) {
            char c = (char) r;
            if (c == '\n') {
                break;
            }
            sb.append(c);
            r = in.read();
        }
        return Optional.of(sb.toString());
    }
}
