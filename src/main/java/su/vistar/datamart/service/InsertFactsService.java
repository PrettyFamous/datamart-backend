package su.vistar.datamart.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface InsertFactsService {
    void createAndFillFacts(MultipartFile templateFile, String templateFileName, MultipartFile dataFile, String dataFileName);
}
