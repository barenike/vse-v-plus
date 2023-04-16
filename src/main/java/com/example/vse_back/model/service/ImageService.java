package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.InputFileIsNotImageException;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.repository.ImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
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

    public ImageEntity createImage(MultipartFile file) {
        checkWhetherFileIsImage(file);

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = UUID.randomUUID().toString();
        String imagePath = String.format("/%s.%s", name, extension);
        String imageUrl;

        try (InputStream input = file.getInputStream()) {
            imageUrl = dropboxService.uploadFile(imagePath, input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    private void checkWhetherFileIsImage(MultipartFile file) throws InputFileIsNotImageException {
        try (InputStream input = file.getInputStream()) {
            if (ImageIO.read(input) == null) {
                throw new InputFileIsNotImageException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
