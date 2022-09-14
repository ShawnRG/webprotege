package edu.stanford.bmir.protege.web.server.download;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Apr 2017
 *
 * A small task that sends a file to the browser/client.
 */
class FileTransferTask implements Callable<Void> {

    private static Logger logger = LoggerFactory.getLogger(FileTransferTask.class);

    private final ProjectId projectId;

    private final UserId userId;

    private final Path downloadSource;

    private final OutputStream outputStream;


    /**
     * Creates a {@link FileTransferTask} to transfer the specified file.
     * @param fileToTransfer The file to transfer.
     */
    public FileTransferTask(@Nonnull ProjectId projectId,
                            @Nonnull UserId userId,
                            @Nonnull Path fileToTransfer,
                            @Nonnull OutputStream outputStream) {
        this.projectId = checkNotNull(projectId);
        this.userId = checkNotNull(userId);
        this.downloadSource = checkNotNull(fileToTransfer);
        this.outputStream = checkNotNull(outputStream);
    }

    @Override
    public Void call() throws Exception {
        streamFileToClient();
        return null;
    }


    private void streamFileToClient() throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(downloadSource))) {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(this.outputStream)) {
                double sizeMB = Files.size(downloadSource) / (1024.0 * 1024);
                String formattedSize = String.format("%.4f", sizeMB);
                logger.info("{} {} Transferring {} MB download to client",
                            projectId,
                            userId,
                            formattedSize);

                Stopwatch stopwatch = Stopwatch.createStarted();
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();

                logger.info("{} {} Finished transferring {} MB to client after {} ms",
                            projectId,
                            userId,
                            formattedSize,
                            stopwatch.elapsed(MILLISECONDS));
            }
        }
    }
}
