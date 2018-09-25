package edu.stanford.bmir.protege.web.shared.bulkop;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Sep 2018
 */
public class ReplaceAnnotationValuesAction implements ProjectAction<ReplaceAnnotationValuesResult> {

    private ProjectId projectId;

    private ImmutableSet<OWLEntity> entities;

    private OWLAnnotationProperty property;

    private String matchString;

    private boolean regex;

    private String replacement;

    public ReplaceAnnotationValuesAction(@Nonnull ProjectId projectId,
                                         @Nonnull ImmutableSet<OWLEntity> entities,
                                         @Nonnull OWLAnnotationProperty property,
                                         @Nonnull String matchString,
                                         boolean regex,
                                         @Nonnull String replacement) {
        this.projectId = checkNotNull(projectId);
        this.entities = checkNotNull(entities);
        this.property = checkNotNull(property);
        this.matchString = checkNotNull(matchString);
        this.regex = regex;
        this.replacement = checkNotNull(replacement);
    }

    @GwtSerializationConstructor
    private ReplaceAnnotationValuesAction() {
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public ImmutableSet<OWLEntity> getEntities() {
        return entities;
    }

    @Nonnull
    public OWLAnnotationProperty getProperty() {
        return property;
    }

    @Nonnull
    public String getMatchString() {
        return matchString;
    }

    public boolean isRegex() {
        return regex;
    }

    @Nonnull
    public String getReplacement() {
        return replacement;
    }
}