package su.vistar.datamart.controller;

import com.sun.tools.jconsole.JConsoleContext;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.vistar.datamart.entity.Dimension;
import su.vistar.datamart.model.DimensionInfoModel;
import su.vistar.datamart.service.DimensionService;
import su.vistar.datamart.model.DimensionModel;
import su.vistar.datamart.service.InsertDimensionService;

@Controller
@AllArgsConstructor
@RequestMapping("/dimension")
public class DimensionController {
    private final DimensionService dimensionService;
    private final InsertDimensionService insertDimensionService;

    @GetMapping("/{id}")
    public ResponseEntity<Dimension> getDimensionById(@PathVariable Long id) {
        return new ResponseEntity<>(dimensionService.getDimensionById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<Dimension>> getDimensions(@RequestParam("search") String name) {
        return new ResponseEntity<>(dimensionService.getDimensions(name), HttpStatus.OK);
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity handleFileUpload(@RequestParam("name") String name,
                                           @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                String content = new String(file.getBytes());
                insertDimensionService.createAndFillDimension(name, content);

                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<DimensionInfoModel> getDimensionInfoById(@PathVariable Long id) {
        return new ResponseEntity<>(dimensionService.getDimensionInfoById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Dimension> addDimension(@RequestBody DimensionModel dimensionModel) {
        return new ResponseEntity<>(dimensionService.addDimension(dimensionModel), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        dimensionService.deleteById(id);
    }
}