package bg.tu.sofia.store.service.impl;

import bg.tu.sofia.store.exception.StoreException;
import bg.tu.sofia.store.service.FilesStorageService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            throw new StoreException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            UUID uuid = UUID.randomUUID();
            int i = file.getOriginalFilename().lastIndexOf('.');
            String fileName;
            if (i != -1) {
                fileName = file.getOriginalFilename().substring(0, i);
                String postFix = file.getOriginalFilename().substring(i);
                fileName = fileName + "-" + uuid + postFix;
            } else {
                fileName = file.getOriginalFilename() + uuid;
            }
            Files.copy(file.getInputStream(), this.root.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new StoreException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StoreException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new StoreException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new StoreException("Could not load the files!");
        }
    }
}
