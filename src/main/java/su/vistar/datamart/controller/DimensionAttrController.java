package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import su.vistar.datamart.entity.DimensionAttr;
import su.vistar.datamart.model.DimensionAttrModel;
import su.vistar.datamart.service.DimensionAttrService;

@Controller
@AllArgsConstructor
@RequestMapping("/dimension_attr")
public class DimensionAttrController {
    private final DimensionAttrService dimensionAttrService;

    @GetMapping("/{id}")
    public ResponseEntity<DimensionAttr> getDimensionAttrById(@PathVariable Long id) {
        return new ResponseEntity<>(dimensionAttrService.getDimensionAttrById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<DimensionAttr>> getDimensionAttrs() {
        return new ResponseEntity<>(dimensionAttrService.getDimensionAttrs(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DimensionAttr> addDimensionAttr(@RequestBody DimensionAttrModel dimensionAttrModel) {
        return new ResponseEntity<>(dimensionAttrService.addDimensionAttr(dimensionAttrModel), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DimensionAttr> updateDimensionAttr(@RequestBody DimensionAttrModel dimensionAttrModel) {
        return new ResponseEntity<>(dimensionAttrService.updateDimensionAttr(dimensionAttrModel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        dimensionAttrService.deleteById(id);
    }
}
