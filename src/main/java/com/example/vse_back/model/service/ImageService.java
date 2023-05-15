package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.InvalidImageException;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.repository.ImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final DropboxService dropboxService;

    public ImageService(ImageRepository imageRepository, DropboxService dropboxService) {
        this.imageRepository = imageRepository;
        this.dropboxService = dropboxService;
    }

    public ImageEntity createAndGetImage(MultipartFile file) {
        if (!validateImage(file)) {
            throw new InvalidImageException();
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        UUID name = UUID.randomUUID();
        String imagePath = String.format("/%s.%s", name, extension);
        String imageUrl;

        imageUrl = dropboxService.uploadFile(imagePath, file);

        ImageEntity image = new ImageEntity();
        image.setImageUrl(imageUrl);
        image.setImagePath(imagePath);
        imageRepository.save(image);
        return image;
    }

    public void deleteImage(UUID id) {
        if (imageRepository.existsById(id)) {
            dropboxService.deleteFile(imageRepository.getReferenceById(id).getImagePath());
            imageRepository.deleteById(id);
        }
    }

    // I don't think that this is enough. Read https://portswigger.net/web-security/file-upload
    private boolean validateImage(MultipartFile file) {
        if (file.getSize() > 5000000) {
            return false;
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!isSupportedExtension(extension)) {
            return false;
        }

        Tika tika = new Tika();
        try (InputStream input = file.getInputStream()) {
            String mimeType = tika.detect(input);
            if (!isSupportedContentType(mimeType)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private boolean isSupportedExtension(String extension) {
        return extension != null && (
                extension.equals("png")
                        || extension.equals("jpg")
                        || extension.equals("jpeg")
                        || extension.equals("webp"));
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/webp");
    }
}
