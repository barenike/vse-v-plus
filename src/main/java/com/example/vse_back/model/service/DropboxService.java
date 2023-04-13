package com.example.vse_back.model.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.example.vse_back.exceptions.ImageDeleteFromDropboxFailedException;
import com.example.vse_back.exceptions.ImageUploadToDropboxFailedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DropboxService {
    private final DbxClientV2 client;

    public DropboxService(DbxClientV2 client) {
        this.client = client;
    }

    public String uploadFile(String filePath, InputStream inputStream) {
        try {
            client.files().uploadBuilder(filePath).uploadAndFinish(inputStream);
            return client.sharing().createSharedLinkWithSettings(filePath).getUrl().replaceAll("dl=0", "raw=1");
        } catch (IOException | DbxException e) {
            throw new ImageUploadToDropboxFailedException(filePath, e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            client.files().deleteV2(filePath);
        } catch (DbxException e) {
            throw new ImageDeleteFromDropboxFailedException(filePath, e);
        }
    }
}
