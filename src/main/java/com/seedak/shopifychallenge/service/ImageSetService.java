package com.seedak.shopifychallenge.service;

import com.seedak.shopifychallenge.model.ImageSet;
import com.seedak.shopifychallenge.repository.ImageSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageSetService {

    @Autowired
    private ImageSetRepository imageSetRepository;

    public void saveImage(ImageSet imageSet) {
        imageSetRepository.save(imageSet);
    }

    public List<ImageSet> getAllActiveImages() {
        return imageSetRepository.findAll();
    }

    public Optional<ImageSet> getImageById(int id) {
        return imageSetRepository.findById(id);
    }

    public void deleteImage(int imageid){
        imageSetRepository.deleteById(imageid);
    }
}
