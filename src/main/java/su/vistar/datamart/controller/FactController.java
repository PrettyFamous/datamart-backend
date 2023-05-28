package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.vistar.datamart.entity.Fact;
import su.vistar.datamart.model.FactInfoModel;
import su.vistar.datamart.model.FactModel;
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
    public ResponseEntity<Iterable<Fact>> getFacts(@RequestParam("search") String name) {
        return new ResponseEntity<>(factService.getFacts(name), HttpStatus.OK);
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
