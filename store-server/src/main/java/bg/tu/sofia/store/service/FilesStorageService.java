package bg.tu.sofia.store.service;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

// Интерфейс за управление на файловете в системата
public interface FilesStorageService {

    // Метод за инициализиране на съхранението на файловете
    void init();

    // Метод за запазване на файл в съхранението
    String save(MultipartFile file);

    // Метод за зареждане на файл по зададено име
    Resource load(String filename);

    // Метод за изтриване на всички файлове от съхранението
    void deleteAll();

    // Метод за зареждане на всички файлове от съхранението като стрийм от пътища
    Stream<Path> loadAll();
}
