package com.baruunaylal.backend.config;

import com.baruunaylal.backend.dto.MediaRequestDTO;
import com.baruunaylal.backend.entity.MediaItem;
import com.baruunaylal.backend.enums.MediaType; // Таны Enum-ийн зөв импортыг шалгаарай
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 1. ModelMapper-ийн ерөнхий тохиргоо
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true);

        // 2. 🛑 ENUM-ИЙГ STRING РУУ ХӨРВҮҮЛЭГЧИЙГ ТОДОРХОЙЛОХ
        Converter<MediaType, String> mediaTypeToStringConverter = context ->
                context.getSource() != null ? context.getSource().name() : null;

        // 3. Гараар маппинг хийх: MediaRequestDTO -> MediaItem
        modelMapper.createTypeMap(MediaRequestDTO.class, MediaItem.class)
                .addMapping(MediaRequestDTO::getUrl, MediaItem::setUrl) // 'url' талбар
                // 🛑 ЭЦСИЙН ЗАСВАР: Converter ашиглан 'type' (Enum)-ийг 'fileType' (String) руу маппинг хийх
                .addMappings(mapper -> {
                    mapper.using(mediaTypeToStringConverter) // Хөрвүүлэгчийг ашиглана
                            .map(MediaRequestDTO::getType, MediaItem::setFileType); // 'type' -> 'fileType'
                });

        return modelMapper;
    }
}