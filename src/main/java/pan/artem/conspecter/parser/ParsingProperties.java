package pan.artem.conspecter.parser;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("conspecter")
public class ParsingProperties {

    @Getter
    private static int maxLineLength;
    @Getter
    private static int minTaskSize;

    @Value("${conspecter.maxLineLength}")
    private int maxLineLengthNotStatic;
    @Value("${conspecter.minTaskSize}")
    private int minTaskSizeNotStatic;

    @PostConstruct
    private void init() {
        maxLineLength = maxLineLengthNotStatic;
        minTaskSize = minTaskSizeNotStatic;
    }
}
