package su.vistar.datamart.service;

import org.springframework.web.multipart.MultipartFile;

public interface InsertFactsService {
    void createAndFillFacts(MultipartFile templateFile, String templateFileName, MultipartFile dataFile, String dataFileName);
}
