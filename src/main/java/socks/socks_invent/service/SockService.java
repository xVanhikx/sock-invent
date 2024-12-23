package socks.socks_invent.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socks.socks_invent.model.Sock;
import socks.socks_invent.repository.SockRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SockService {

    private static final Logger logger = LoggerFactory.getLogger(SockService.class);

    @Autowired
    private SockRepository sockRepository;

    public Sock incomeSock(Sock sock) {
        logger.info("Income operation: {}", sock);
        if (sockRepository.findSockByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage()) == null) {
            return sockRepository.save(sock);

        } else {
            Sock updSock = sockRepository.findSockByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage());
            int updatedQuantity = sock.getQuantity() + updSock.getQuantity();
            updSock.setQuantity(updatedQuantity);
            return sockRepository.save(updSock);
        }
    }

    public void outcomeSock(Sock sock) {
        logger.info("Outcome operation: {}", sock);
        Sock sockInStock = sockRepository.findSockByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage());
        int quantityInStock = sockInStock.getQuantity();
        int quantity = sock.getQuantity();
        if (quantity > quantityInStock) {
            throw new RuntimeException("Not enough socks in stock");
        }
        if (quantityInStock == quantity) {
            sockRepository.deleteById(sockInStock.getId());
        } else {
            sockInStock.setQuantity(quantityInStock - quantity);
            sockRepository.save(sockInStock);
        }
    }

    public Integer getSocks(String color,String operator ,
                            Integer cottonPercentage,
                            Integer minCottonPercentage,
                            Integer maxCottonPercentage) {
        logger.info("Get sock operation with filters: color={}, operator={}, cottonPercentage={}, minCottonPercentage={}, maxCottonPercentage={}",
                color, operator, cottonPercentage, minCottonPercentage, maxCottonPercentage);
        if (minCottonPercentage != null && maxCottonPercentage != null) {
            return sockRepository.sumByColorAndCottonPercentageBetween(color, minCottonPercentage, maxCottonPercentage);
        } else if (operator != null && cottonPercentage != null) {
            switch (operator.toLowerCase()) {
                case "morethan":
                    return sockRepository.sumByColorAndMoreThanCottonPercentage(color, cottonPercentage);
                case "lessthan":
                    return sockRepository.sumByColorAndLessThanCottonPercentage(color, cottonPercentage);
                case "equal":
                default:
                    return sockRepository.sumByColorAndEqualCottonPercentage(color, cottonPercentage);
            }
        } else {
            return sockRepository.sumByColorAndEqualCottonPercentage(color, cottonPercentage);
        }

    }

    public Sock updateSock(Long id, Sock sock) {
        Sock updSock = sockRepository.findById(id).orElseThrow(() -> new RuntimeException("Sock not found"));
        updSock.setColor(sock.getColor());
        updSock.setQuantity(sock.getQuantity());
        updSock.setCottonPercentage(sock.getCottonPercentage());
        return sockRepository.save(updSock);
    }

    public void processBatch(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String color = row.getCell(0).getStringCellValue();
                int cottonPercentage = (int) row.getCell(1).getNumericCellValue();
                int quantity = (int) row.getCell(2).getNumericCellValue();
                incomeSock(new Sock(null, color, cottonPercentage, quantity));
            }
        } catch (IOException e) {
            logger.error("Error processing Excel file: {}", e.getMessage());
            throw e;
        }
    }
}
