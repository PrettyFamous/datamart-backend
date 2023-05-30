package su.vistar.datamart.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
