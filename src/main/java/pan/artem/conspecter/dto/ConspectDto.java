package pan.artem.conspecter.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ConspectDto {
    int id;
    String path;
    int score;
    int outOf;
}
