package edu.stanford.bmir.protege.web.server.download;

import static edu.stanford.bmir.protege.web.server.logging.RequestFormatter.formatAddr;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.access.ProjectResource;
import edu.stanford.bmir.protege.web.server.access.Subject;
import edu.stanford.bmir.protege.web.server.api.ApiRootResource;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.bmir.protege.web.shared.user.UserId;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/06/2012
 * <p>
 * A servlet which allows ontologies to be downloaded from WebProtege.  See {@link ProjectDownloader} for
 * the piece of machinery that actually does the processing of request parameters and the downloading.
 * </p>
 */
@Path("download")
public class ProjectDownloadResource implements ApiRootResource {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDownloadResource.class);

    @Nonnull
    private final AccessManager accessManager;

    @Nonnull
    private final ProjectDownloadService projectDownloadService;

    @Inject
    public ProjectDownloadResource(@Nonnull AccessManager accessManager,
                                   @Nonnull ProjectDownloadService projectDownloadService) {
        this.accessManager = accessManager;
        this.projectDownloadService = projectDownloadService;
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@Nonnull @Context UserId userId, @Nonnull @Context HttpServletRequest req) throws IOException {
        FileDownloadParameters downloadParameters = new FileDownloadParameters(req);
        if(!downloadParameters.isProjectDownload()) {
            logger.info("Bad project download request from {} at {}.  Request URI: {}  Query String: {}",
                        userId,
                        formatAddr(req),
                        req.getRequestURI(),
                        req.getQueryString());
            return Response.status(BAD_REQUEST)
                    .build();
        }
        logger.info("Received download request from {} at {} for project {}",
                    userId,
                    formatAddr(req),
                    downloadParameters.getProjectId());

        if (!accessManager.hasPermission(Subject.forUser(userId),
                                         new ProjectResource(downloadParameters.getProjectId()),
                                         BuiltInAction.DOWNLOAD_PROJECT)) {
            logger.info("Denied download request as user does not have permission to download this project.");
            return Response.status(FORBIDDEN)
                    .build();
        }
        else if (downloadParameters.isProjectDownload()) {
            return startProjectDownload(userId, downloadParameters);
        }

        return Response.serverError().build();
    }

    private Response startProjectDownload(
                                      UserId userId,
                                      FileDownloadParameters downloadParameters) throws IOException {
        ProjectId projectId = downloadParameters.getProjectId();
        RevisionNumber revisionNumber = downloadParameters.getRequestedRevision();
        DownloadFormat format = downloadParameters.getFormat();
        return projectDownloadService.downloadProject(userId, projectId, revisionNumber, format);
    }

    @PreDestroy
    public void destroy() {
        projectDownloadService.shutDown();
    }
}
