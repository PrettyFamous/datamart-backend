package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import su.vistar.datamart.entity.FactAttr;
import su.vistar.datamart.model.FactAttrModel;
import su.vistar.datamart.service.FactAttrService;


@Controller
@AllArgsConstructor
@RequestMapping("/fact_attr")
public class FactAttrController {
    private final FactAttrService factAttrService;

    @GetMapping("/{id}")
    public ResponseEntity<FactAttr> getFactAttrById(@PathVariable Long id) {
        return new ResponseEntity<>(factAttrService.getFactAttrById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<FactAttr>> getFactAttrs() {
        return new ResponseEntity<>(factAttrService.getFactAttrs(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FactAttr> addFactAttr(@RequestBody FactAttrModel factAttrModel) {
        return new ResponseEntity<>(factAttrService.addFactAttr(factAttrModel), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FactAttr> updateFactAttr(@RequestBody FactAttrModel factAttrModel) {
        return new ResponseEntity<>(factAttrService.updateFactAttr(factAttrModel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        factAttrService.deleteById(id);
    }
}
