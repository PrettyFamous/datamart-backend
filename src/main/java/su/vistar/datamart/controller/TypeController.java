package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import su.vistar.datamart.entity.Type;
import su.vistar.datamart.model.TypeModel;
import su.vistar.datamart.service.TypeService;


@Controller
@AllArgsConstructor
@RequestMapping("/type")
public class TypeController {
    private final TypeService typeService;

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable Long id) {
        return new ResponseEntity<>(typeService.getTypeById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<Type>> getTypes() {
        return new ResponseEntity<>(typeService.getTypes(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Type> addType(@RequestBody TypeModel typeModel) {
        return new ResponseEntity<>(typeService.addType(typeModel), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Type> updateType(@RequestBody TypeModel typeModel) {
        return new ResponseEntity<>(typeService.updateType(typeModel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        typeService.deleteById(id);
    }
}
