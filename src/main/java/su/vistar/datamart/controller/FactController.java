package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;
import su.vistar.datamart.model.PageDTO;
import su.vistar.datamart.service.FactService;
import su.vistar.datamart.service.InsertFactsService;

@Controller
@AllArgsConstructor
@RequestMapping("/fact")
public class FactController {
    private final FactService factService;
    private final InsertFactsService insertFactsService;

    @GetMapping("/{id}")
    public ResponseEntity<Fact> getFactById(@PathVariable Long id) {
        return new ResponseEntity<>(factService.getFactById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Fact>> getFacts(@RequestParam("page") int pageNum, @RequestParam("search") String name) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNumber(pageNum);
        Page<Fact> page = factService.getFacts(name, pageDTO);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity handleFileUpload(@RequestParam("templateFileName") String templateFileName,
                                           @RequestParam("templateFile") MultipartFile templateFile,
                                           @RequestParam("dataFileName") String dataFileName,
                                           @RequestParam("dataFile") MultipartFile dataFile){

        if (!templateFile.isEmpty() && !dataFile.isEmpty()) {
            try {
                insertFactsService.createAndFillFacts(templateFile, templateFileName, dataFile, dataFileName);

                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<FactInfoModel> getFactInfoById(@PathVariable Long id) {
        return new ResponseEntity<>(factService.getFactInfoById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Fact> addFact(@RequestBody FactModel factModel) {
        return new ResponseEntity<>(factService.addFact(factModel), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Fact> updateFact(@RequestBody FactModel factModel) {
        return new ResponseEntity<>(factService.updateFact(factModel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        factService.deleteById(id);
    }
}
