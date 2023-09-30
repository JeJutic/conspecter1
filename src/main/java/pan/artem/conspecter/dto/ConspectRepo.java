package pan.artem.conspecter.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ConspectRepo {
    int id;
    String fullName;
    String author;
    String pathName;
}
