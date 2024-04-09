package bg.tu.sofia.store.model; // Промяна на пакета

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfo {

    // Име на файла
    private String name;

    // URL адрес към файла
    private String url;
}
