package com.iseage.wms.Stock;

import java.util.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemRepository itemRepo;
    public ItemController(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAllItems(){
        return ResponseEntity.ok(itemRepo.findAll());
    }

    @GetMapping("/get")
    public ResponseEntity<Item> getItem(@RequestParam Long iid){
        if(itemRepo.existsById(iid)){
            return ResponseEntity.ok(itemRepo.findById(iid).orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    // Admin APIs
    @PostMapping("/create")
    public ResponseEntity<String> createItem(@Valid @RequestBody Item item){
        if(!itemRepo.existsByName(item.getName())){
            itemRepo.save(item);
            return ResponseEntity.ok().body("Sucecss!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item already exists!");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateItem(@RequestParam Long iid, @Valid @RequestBody Item item) {
        if (itemRepo.existsById(iid)) {
            item.setId(iid);
            itemRepo.save(item);
            return ResponseEntity.ok().body("Success!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item Not Found!");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestParam Long iid){
        if (itemRepo.existsById(iid)){
            itemRepo.deleteById(iid);
            return ResponseEntity.ok().body("Success!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item Not Found!");
    }
}