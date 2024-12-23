package socks.socks_invent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socks.socks_invent.model.Sock;
import socks.socks_invent.service.SockService;

import java.io.IOException;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Socks", description = "Operations related to socks management")
public class SockController {

    private static final Logger logger = LoggerFactory.getLogger(SockController.class);

    @Autowired
    private SockService sockService;

    @PostMapping("/income")
    @Operation(summary = "Регистрация прихода носков")
    public ResponseEntity<Void> incomeSock(@RequestBody Sock sock) {
        logger.info("Income request received: {}", sock);
        sockService.incomeSock(sock);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/outcome")
    @Operation(summary = "Регистрация отпуска носков")
    public ResponseEntity<String> outcomeSock(@RequestBody Sock sock) {
        logger.info("Outcome request received: {}", sock);
        try {
            sockService.outcomeSock(sock);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Not enough socks: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Получение общего количества носков с фильтрацией")
    public ResponseEntity<Integer> getSocks(
                            @Parameter(description = "Цвет носков") @RequestParam(required = false) String color,
                            @Parameter(description = "Оператор сравнения") @RequestParam(required = false) String operator,
                            @Parameter(description = "Процент содержания хлопка") @RequestParam(required = false) Integer cottonPercentage,
                            @Parameter(description = "Минимальный процент хлопка") @RequestParam(required = false) Integer minCottonPercentage,
                            @Parameter(description = "Максимальный процент хлопка") @RequestParam(required = false) Integer maxCottonPercentage) {
        logger.info("Get socks received with parameters: color={}, operator={}, cottonPercentage={}, minCottonPercentage={}, maxCottonPercentage={}", color, operator, cottonPercentage, minCottonPercentage, maxCottonPercentage);
        Integer quantity = sockService.getSocks(color, operator, cottonPercentage, minCottonPercentage, maxCottonPercentage);
        return new ResponseEntity<>(quantity != null ? quantity : 0, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление данных о носках")
    public ResponseEntity<Sock> updateSock(
            @Parameter(description = "ID of the sock to update", required = true) @PathVariable Long id,
            @Parameter(description = "New socks information", required = true, schema = @Schema(implementation = Sock.class)) @RequestBody Sock sock) {
        logger.info("Update sock request received for ID {}: {}", id, sock);
        try {
            Sock updatedSock = sockService.updateSock(id, sock);
            return ResponseEntity.ok(updatedSock);
        } catch (IllegalArgumentException e) {
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/batch")
    @Operation(summary = "Загрузка партий носков из Excel файла")
    public ResponseEntity<Void> processBatch(@RequestParam("file")MultipartFile file) throws IOException {
        logger.info("Batch upload request received for file: {}", file.getOriginalFilename());
        try {
            sockService.processBatch(file);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Error processing batch file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
