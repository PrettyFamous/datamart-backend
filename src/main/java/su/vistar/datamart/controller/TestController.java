package su.vistar.datamart.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping(value = "/hello")
    public ResponseEntity<String> testHello() {
        return new ResponseEntity<String>("Hello world", HttpStatus.OK);
    }

    @GetMapping(value = "/bye")
    public ResponseEntity<String> testBye() {
        return new ResponseEntity<String>("Bye world", HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping(value = "/active")
    public ResponseEntity<Message> getActiveColors() {
        return new ResponseEntity<>(new Message(), HttpStatus.OK);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private class Message {
        @JsonProperty(value="active")
        private int[] active = new int[] {1,3,4};
    }
}
