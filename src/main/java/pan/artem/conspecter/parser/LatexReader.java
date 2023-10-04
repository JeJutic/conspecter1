package pan.artem.conspecter.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class LatexReader extends ShortLineReader {

    public LatexReader(Reader in) {
        super(in);
    }

    @Override
    public Optional<String> readLine() throws IOException {
        Optional<String> optional = super.readLine();
        if (optional.isEmpty()) {
            return optional;
        }
        String raw = optional.get();
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '%') {
                return Optional.of(raw.substring(0, i));
            }
        }
        return Optional.of(raw);
    }
}
