package waterfall.api.io;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This record is a custom wrapper for jakarta.servlet.http.Part
 */
public record UploadedFile(Metadata metadata, InputStream stream) {
    public void write(String path) throws IOException {
        Path target = Path.of(path);
        Path parent = target.getParent();

        if (parent != null)
            Files.createDirectories(parent);

        Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static UploadedFile of(Part part) throws IOException {
        try (InputStream stream = part.getInputStream()) {
            Map<String, Collection<String>> headers = new HashMap<>();

            for (String headerName : part.getHeaderNames())
                headers.put(headerName, part.getHeaders(headerName));

            Metadata metadata = new Metadata(
                    part.getSubmittedFileName(),
                    part.getSize(),
                    part.getContentType(),
                    headers,
                    part.getName()
            );

            return new UploadedFile(metadata, stream);
        }
    }

    public record Metadata(
            String submittedFileName,
            long size,
            String contentType,
            Map<String, Collection<String>> headers,
            String formName
    ) {}
}
