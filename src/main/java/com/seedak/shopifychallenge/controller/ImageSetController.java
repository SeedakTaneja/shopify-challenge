package com.seedak.shopifychallenge.controller;

import com.seedak.shopifychallenge.model.ImageSet;
import com.seedak.shopifychallenge.service.ImageSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ImageSetController {

    @Value("${uploadDir}")
    private String uploadFolder;

    @Autowired
    private ImageSetService imageSetService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = {"/", "/home"})
    public String addProductPage() {
        return "index";
    }

    @PostMapping("/image/saveImageDetails")
    public @ResponseBody
    ResponseEntity<?> createProduct(@RequestParam("name") String name,
                                    @RequestParam("price") double price, @RequestParam("description") String description, Model model, HttpServletRequest request
            , final @RequestParam("image") MultipartFile file) {
        try {
            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
            String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
            log.info("uploadDirectory:: " + uploadDirectory);
            String fileName = file.getOriginalFilename();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            log.info("FileName: " + file.getOriginalFilename());
            if (fileName == null || fileName.contains("..")) {
                model.addAttribute("invalid", "Sorry! Filename contains invalid path sequence \" + fileName");
                return new ResponseEntity<>("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }
            String[] names = name.split(",");
            String[] descriptions = description.split(",");
            Date createDate = new Date();
            log.info("Name: " + names[0]+" "+filePath);
            log.info("description: " + descriptions[0]);
            log.info("price: " + price);
            try {
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    log.info("Folder Created");
                    dir.mkdirs();
                }
                // Save the file locally
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(file.getBytes());
                stream.close();
            } catch (Exception e) {
                log.info("in catch");
                e.printStackTrace();
            }
            byte[] imageData = file.getBytes();
            ImageSet imageSet = new ImageSet();
            imageSet.setName(names[0]);
            imageSet.setImage(imageData);
            imageSet.setPrice(price);
            imageSet.setDescription(descriptions[0]);
            imageSet.setCreateDate(createDate);
            imageSetService.saveImage(imageSet);
            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
            return new ResponseEntity<>("Product Saved With File - " + fileName, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") int id, HttpServletResponse response, Optional<ImageSet> imageSet)
            throws ServletException, IOException {
        log.info("Id :: " + id);
        imageSet = imageSetService.getImageById(id);
        response.setContentType("image/jpeg");
        response.setContentType("image/jpg");
        response.setContentType("image/png");
        response.setContentType("image/gif");
//        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(imageSet.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/image/imageDetails")
    String showProductDetails(@RequestParam("id") int id, Optional<ImageSet> imageSet, Model model) {
        try {
            log.info("Id :: " + id);
            if (id != 0) {
                imageSet = imageSetService.getImageById(id);

                log.info("products :: " + imageSet);
                if (imageSet.isPresent()) {
                    model.addAttribute("id", imageSet.get().getId());
                    model.addAttribute("description", imageSet.get().getDescription());
                    model.addAttribute("name", imageSet.get().getName());
                    model.addAttribute("price", imageSet.get().getPrice());
                    return "imagedetails";
                }
                return "redirect:/home";
            }
            return "redirect:/home";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home";
        }
    }

    @GetMapping("/image/show")
    String show(Model map) {
        List<ImageSet> images = imageSetService.getAllActiveImages();
        map.addAttribute("images", images);
        return "images";
    }
}
