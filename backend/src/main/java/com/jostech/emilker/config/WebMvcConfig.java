package com.jostech.emilker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

// Sin esto, las fotos que suben los administradores se guardarian en el
// servidor pero el navegador no podria "verlas" en una URL: aqui le decimos a
// Spring que cualquier direccion que empiece por /uploads/ debe buscarse en la
// carpeta real de subidas, no dentro del proyecto compilado.
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String carpetaSubidas;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = new File(carpetaSubidas).getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + rutaAbsoluta + File.separator);
    }
}
